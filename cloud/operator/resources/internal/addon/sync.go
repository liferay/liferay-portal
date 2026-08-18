package addon

import (
	"context"
	"crypto/rsa"
	"io"
	"slices"
	"strconv"
	"sync"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	backoff "github.com/liferay/liferay-portal/cloud/operator/internal/backoff"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	stateDownloaded  = "Downloaded"
	stateDownloading = "Downloading"
	stateFailed      = "Failed"
)

func NewSyncer(
	downloader Downloader,
	pollInterval time.Duration,
	retryInitialDelay time.Duration,
	retryMaxDelay time.Duration,
	runner Runner,
) *Syncer {
	return &Syncer{
		downloader:        downloader,
		inFlight:          map[string]bool{},
		pollInterval:      pollInterval,
		results:           map[string]error{},
		retryInitialDelay: retryInitialDelay,
		retryMaxDelay:     retryMaxDelay,
		runner:            runner,
	}
}

func (goRunner GoRunner) Run(task func()) {
	go task()
}

func Summarize(apps []licensingv1alpha1.AppStatus) Summary {
	var summary Summary

	for _, appStatus := range apps {
		summary.Entitled++

		if appStatus.State == stateDownloading {
			summary.Pending++
		}

		if appStatus.State == stateFailed {
			summary.Failed = append(summary.Failed, appStatus.Name)
		}
	}

	slices.Sort(summary.Failed)

	return summary
}

func (syncer *Syncer) Sync(
	syncRequest SyncRequest,
) ([]licensingv1alpha1.AppStatus, time.Duration) {
	priorByVirtualEntryID := make(
		map[int64]licensingv1alpha1.AppStatus, len(syncRequest.Current),
	)

	for _, appStatus := range syncRequest.Current {
		priorByVirtualEntryID[appStatus.VirtualEntryID] = appStatus
	}

	apps := make([]licensingv1alpha1.AppStatus, 0, len(syncRequest.AddOns))

	requeueAfter := time.Duration(0)

	for _, addOn := range syncRequest.AddOns {
		appStatus, hint := syncer.reconcileAddOn(
			addOn, syncRequest.Cache, syncRequest.Context,
			syncRequest.EnvironmentID, syncRequest.Namespace, syncRequest.Now,
			priorByVirtualEntryID[addOn.VirtualEntryID], syncRequest.PrivateKey,
		)

		apps = append(apps, appStatus)

		requeueAfter = soonest(hint, requeueAfter)
	}

	return apps, requeueAfter
}

func carryBackoff(
	appStatus licensingv1alpha1.AppStatus, prior licensingv1alpha1.AppStatus,
) licensingv1alpha1.AppStatus {
	appStatus.ConsecutiveFailures = prior.ConsecutiveFailures
	appStatus.Message = prior.Message
	appStatus.NextRetry = prior.NextRetry

	return appStatus
}

func carryFailures(
	appStatus licensingv1alpha1.AppStatus, prior licensingv1alpha1.AppStatus,
) licensingv1alpha1.AppStatus {
	appStatus.ConsecutiveFailures = prior.ConsecutiveFailures

	return appStatus
}

func download(
	addOn provisioning.AddOn,
	cache Cache,
	context context.Context,
	downloader Downloader,
	environmentID string,
	privateKey *rsa.PrivateKey,
) error {
	reader, error := downloader.DownloadAddOn(
		context,
		provisioning.DownloadRequest{
			DownloadURL:    addOn.DownloadURL,
			EnvironmentID:  environmentID,
			VirtualEntryID: addOn.VirtualEntryID,
		},
		privateKey,
	)

	if error != nil {
		return error
	}

	defer reader.Close()

	return cache.Save(addOn.SHA256Checksum, reader, addOn.VirtualEntryID)
}

func downloadKey(namespace string, virtualEntryID int64) string {
	return namespace + "/" + strconv.FormatInt(virtualEntryID, 10)
}

func newAppStatus(
	addOn provisioning.AddOn, state string,
) licensingv1alpha1.AppStatus {
	return licensingv1alpha1.AppStatus{
		Checksum:       addOn.SHA256Checksum,
		Name:           addOn.ProductName,
		State:          state,
		VirtualEntryID: addOn.VirtualEntryID,
	}
}

func (syncer *Syncer) reconcileAddOn(
	addOn provisioning.AddOn,
	cache Cache,
	context context.Context,
	environmentID string,
	namespace string,
	now metav1.Time,
	prior licensingv1alpha1.AppStatus,
	privateKey *rsa.PrivateKey,
) (licensingv1alpha1.AppStatus, time.Duration) {
	logger := logf.FromContext(context)

	key := downloadKey(namespace, addOn.VirtualEntryID)

	syncer.mutex.Lock()

	result, completed := syncer.results[key]

	if completed {
		delete(syncer.results, key)
	}

	inFlight := syncer.inFlight[key]

	syncer.mutex.Unlock()

	has, error := cache.Has(addOn.SHA256Checksum, addOn.VirtualEntryID)

	if error != nil {
		logger.Error(
			error, "Unable to inspect the add-on cache",
			"productName", addOn.ProductName,
			"virtualEntryId", addOn.VirtualEntryID,
		)
	}

	if has {
		return newAppStatus(addOn, stateDownloaded), 0
	}

	if completed && (result != nil) {
		failures := prior.ConsecutiveFailures + 1

		nextRetry := metav1.NewTime(
			now.Add(
				backoff.Duration(
					failures, syncer.retryInitialDelay, syncer.retryMaxDelay,
				),
			),
		)

		logger.Error(
			result, "Unable to download add-on",
			"productName", addOn.ProductName,
			"virtualEntryId", addOn.VirtualEntryID,
		)

		appStatus := newAppStatus(addOn, stateFailed)
		appStatus.ConsecutiveFailures = failures
		appStatus.Message = result.Error()
		appStatus.NextRetry = &nextRetry

		return appStatus, nextRetry.Sub(now.Time)
	}

	if inFlight {
		return carryFailures(newAppStatus(addOn, stateDownloading), prior),
			syncer.pollInterval
	}

	if (prior.NextRetry != nil) && now.Time.Before(prior.NextRetry.Time) &&
		(prior.Checksum == addOn.SHA256Checksum) {

		return carryBackoff(newAppStatus(addOn, stateFailed), prior),
			prior.NextRetry.Sub(now.Time)
	}

	syncer.mutex.Lock()

	syncer.inFlight[key] = true

	syncer.mutex.Unlock()

	syncer.runner.Run(
		func() {
			error := download(
				addOn, cache, context, syncer.downloader, environmentID,
				privateKey,
			)

			syncer.mutex.Lock()

			delete(syncer.inFlight, key)

			syncer.results[key] = error

			syncer.mutex.Unlock()

			if error != nil {
				return
			}

			logf.FromContext(context).Info(
				"Downloaded add-on",
				"productName", addOn.ProductName,
				"virtualEntryId", addOn.VirtualEntryID,
			)
		},
	)

	return carryFailures(newAppStatus(addOn, stateDownloading), prior),
		syncer.pollInterval
}

func soonest(hint time.Duration, requeueAfter time.Duration) time.Duration {
	if hint <= 0 {
		return requeueAfter
	}

	if (requeueAfter <= 0) || (hint < requeueAfter) {
		return hint
	}

	return requeueAfter
}

type Downloader interface {
	DownloadAddOn(
		context context.Context,
		downloadRequest provisioning.DownloadRequest,
		privateKey *rsa.PrivateKey,
	) (io.ReadCloser, error)
}

type GoRunner struct{}

type Runner interface {
	Run(task func())
}

type Summary struct {
	Entitled int
	Failed   []string
	Pending  int
}

type SyncRequest struct {
	AddOns        []provisioning.AddOn
	Cache         Cache
	Context       context.Context
	Current       []licensingv1alpha1.AppStatus
	EnvironmentID string
	Namespace     string
	Now           metav1.Time
	PrivateKey    *rsa.PrivateKey
}

type Syncer struct {
	downloader        Downloader
	inFlight          map[string]bool
	mutex             sync.Mutex
	pollInterval      time.Duration
	results           map[string]error
	retryInitialDelay time.Duration
	retryMaxDelay     time.Duration
	runner            Runner
}
