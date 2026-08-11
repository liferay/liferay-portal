package addon

import (
	"context"
	"crypto/rsa"
	"io"
	"strconv"
	"sync"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	stateDownloaded  = "Downloaded"
	stateDownloading = "Downloading"
	stateFailed      = "Failed"
)

func NewSyncer(downloader Downloader, runner Runner) *Syncer {
	return &Syncer{
		downloader: downloader,
		inFlight:   map[string]bool{},
		results:    map[string]error{},
		runner:     runner,
	}
}

func (goRunner GoRunner) Run(task func()) {
	go task()
}

func (syncer *Syncer) Sync(
	addOns []provisioning.AddOn,
	cache Cache,
	context context.Context,
	environmentID string,
	namespace string,
	privateKey *rsa.PrivateKey,
) ([]licensingv1alpha1.AppStatus, bool) {
	apps := make([]licensingv1alpha1.AppStatus, 0, len(addOns))

	pending := false

	for _, addOn := range addOns {
		appStatus, addOnPending := syncer.reconcileAddOn(
			addOn, cache, context, environmentID, namespace, privateKey,
		)

		apps = append(apps, appStatus)

		pending = pending || addOnPending
	}

	return apps, pending
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
	privateKey *rsa.PrivateKey,
) (licensingv1alpha1.AppStatus, bool) {
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
		return newAppStatus(addOn, stateDownloaded), false
	}

	if completed && (result != nil) {
		logger.Error(
			result, "Unable to download add-on",
			"productName", addOn.ProductName,
			"virtualEntryId", addOn.VirtualEntryID,
		)

		return newAppStatus(addOn, stateFailed), false
	}

	if inFlight {
		return newAppStatus(addOn, stateDownloading), true
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

			logger := logf.FromContext(context)

			if error != nil {
				logger.Error(
					error, "Unable to download add-on",
					"productName", addOn.ProductName,
					"virtualEntryId", addOn.VirtualEntryID,
				)

				return
			}

			logger.Info(
				"Downloaded add-on",
				"productName", addOn.ProductName,
				"virtualEntryId", addOn.VirtualEntryID,
			)
		},
	)

	return newAppStatus(addOn, stateDownloading), true
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

type Syncer struct {
	downloader Downloader
	inFlight   map[string]bool
	mutex      sync.Mutex
	results    map[string]error
	runner     Runner
}
