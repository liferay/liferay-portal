package addon

import (
	"bytes"
	"context"
	"crypto/rsa"
	"fmt"
	"io"
	"slices"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	testPollInterval      = 15 * time.Second
	testRetryInitialDelay = 30 * time.Second
	testRetryMaxDelay     = 30 * time.Minute
)

func (fakeDownloader *fakeDownloader) DownloadAddOn(
	context context.Context,
	downloadRequest provisioning.DownloadRequest,
	privateKey *rsa.PrivateKey,
) (io.ReadCloser, error) {
	if fakeDownloader.calls == nil {
		fakeDownloader.calls = map[int64]int{}
	}

	fakeDownloader.calls[downloadRequest.VirtualEntryID]++

	if error := fakeDownloader.errors[downloadRequest.VirtualEntryID]; error != nil {
		return nil, error
	}

	return io.NopCloser(
		bytes.NewReader(fakeDownloader.bodies[downloadRequest.VirtualEntryID]),
	), nil
}

func (fakeCache *fakeCache) Has(
	checksum string, virtualEntryID int64,
) (bool, error) {
	if fakeCache.hasError != nil {
		return false, fakeCache.hasError
	}

	if checksum == "" {
		return false, nil
	}

	if fakeCache.seeded[virtualEntryID] == checksum {
		return true, nil
	}

	return fakeCache.saved[virtualEntryID] == checksum, nil
}

func (manualRunner *manualRunner) Run(task func()) {
	manualRunner.tasks = append(manualRunner.tasks, task)
}

func (fakeCache *fakeCache) Save(
	expectedChecksum string, reader io.Reader, virtualEntryID int64,
) error {
	if fakeCache.saveError != nil {
		return fakeCache.saveError
	}

	if _, error := io.ReadAll(reader); error != nil {
		return error
	}

	if fakeCache.saved == nil {
		fakeCache.saved = map[int64]string{}
	}

	fakeCache.saved[virtualEntryID] = expectedChecksum

	return nil
}

func TestSummarize(t *testing.T) {
	testCases := map[string]struct {
		apps []licensingv1alpha1.AppStatus
		want Summary
	}{
		"a downloaded add-on is neither pending nor failing": {
			apps: []licensingv1alpha1.AppStatus{
				{Name: "Alpha", State: stateDownloaded},
			},
			want: Summary{Entitled: 1},
		},
		"a downloading add-on is pending": {
			apps: []licensingv1alpha1.AppStatus{
				{Name: "Alpha", State: stateDownloaded},
				{Name: "Bravo", State: stateDownloading},
			},
			want: Summary{Entitled: 2, Pending: 1},
		},
		"an orphaned add-on is not entitled": {
			apps: []licensingv1alpha1.AppStatus{
				{Name: "Alpha", State: stateDownloaded},
				{Name: "Charlie", State: stateOrphaned},
			},
			want: Summary{Entitled: 1},
		},
		"failing add-ons are listed alphabetically": {
			apps: []licensingv1alpha1.AppStatus{
				{Name: "Delta", State: stateFailed},
				{Name: "Bravo", State: stateFailed},
			},
			want: Summary{Entitled: 2, Failed: []string{"Bravo", "Delta"}},
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			summary := Summarize(testCase.apps)

			if summary.Entitled != testCase.want.Entitled {
				t.Errorf(
					"Entitled = %d, want %d", summary.Entitled,
					testCase.want.Entitled,
				)
			}

			if summary.Pending != testCase.want.Pending {
				t.Errorf(
					"Pending = %d, want %d", summary.Pending,
					testCase.want.Pending,
				)
			}

			if !slices.Equal(summary.Failed, testCase.want.Failed) {
				t.Errorf(
					"Failed = %v, want %v", summary.Failed,
					testCase.want.Failed,
				)
			}
		})
	}
}

func TestSyncBacksOffWhileNextRetryInFuture(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()
	nextRetry := atOffset(now, 5*time.Minute)

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	current := []licensingv1alpha1.AppStatus{
		{
			Checksum:            "abc123",
			ConsecutiveFailures: 2,
			Message:             "boom",
			NextRetry:           &nextRetry,
			State:               stateFailed,
			VirtualEntryID:      42,
		},
	}

	apps, requeueAfter := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, current, now),
	)

	if len(runner.tasks) != 0 {
		t.Errorf("Queued tasks = %d, want 0 while backing off", len(runner.tasks))
	}

	if apps[0].State != stateFailed {
		t.Errorf("State = %q, want %q", apps[0].State, stateFailed)
	}

	if apps[0].ConsecutiveFailures != 2 {
		t.Errorf("ConsecutiveFailures = %d, want 2", apps[0].ConsecutiveFailures)
	}

	if apps[0].Message != "boom" {
		t.Errorf("Message = %q, want %q while backing off", apps[0].Message, "boom")
	}

	if apps[0].NextRetry == nil {
		t.Error("NextRetry = nil, want the pending deadline while backing off")
	}

	if requeueAfter != 5*time.Minute {
		t.Errorf("RequeueAfter = %s, want 5m", requeueAfter)
	}
}

func TestSyncClearsFailureMessageWhileRetrying(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()
	nextRetry := atOffset(now, -time.Minute)

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	current := []licensingv1alpha1.AppStatus{
		{
			Checksum:            "abc123",
			ConsecutiveFailures: 2,
			Message:             "add-on download: unexpected status 503",
			NextRetry:           &nextRetry,
			State:               stateFailed,
			VirtualEntryID:      42,
		},
	}

	apps, _ := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, current, now),
	)

	assertRetryInProgress(apps[0], t)

	apps, _ = syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, apps, now),
	)

	assertRetryInProgress(apps[0], t)
}

func TestSyncDoesNotRelaunchInFlightDownload(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	syncer.Sync(newSyncRequest([]provisioning.AddOn{addOn}, cache, nil, now))

	apps, requeueAfter := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, nil, now),
	)

	if apps[0].State != stateDownloading {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloading)
	}

	if len(runner.tasks) != 1 {
		t.Errorf("Queued tasks = %d, want 1 without a relaunch", len(runner.tasks))
	}

	if requeueAfter != testPollInterval {
		t.Errorf("RequeueAfter = %s, want the poll interval", requeueAfter)
	}
}

func TestSyncDownloadsNewAddOnAndReportsDownloaded(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	apps, requeueAfter := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, nil, now),
	)

	if apps[0].State != stateDownloading {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloading)
	}

	if requeueAfter != testPollInterval {
		t.Errorf("RequeueAfter = %s, want the poll interval", requeueAfter)
	}

	runner.flush()

	if downloader.calls[42] != 1 {
		t.Errorf("DownloadAddOn calls = %d, want 1", downloader.calls[42])
	}

	apps, requeueAfter = syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, apps, now),
	)

	if apps[0].State != stateDownloaded {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloaded)
	}

	if requeueAfter != 0 {
		t.Errorf("RequeueAfter = %s, want 0 once downloaded", requeueAfter)
	}

	if _, saved := cache.saved[42]; !saved {
		t.Error("The add-on was not saved to the cache")
	}
}

func TestSyncEscalatesBackoffOnRepeatedFailure(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{}
	downloader := &fakeDownloader{errors: map[int64]error{42: fmt.Errorf("boom")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	firstFailure := recordFailure(
		syncer, addOn, cache, runner, nil, baseTime(),
	)

	if firstFailure.ConsecutiveFailures != 1 {
		t.Fatalf(
			"ConsecutiveFailures = %d, want 1 after the first failure",
			firstFailure.ConsecutiveFailures,
		)
	}

	secondFailure := recordFailure(
		syncer, addOn, cache, runner,
		[]licensingv1alpha1.AppStatus{firstFailure},
		atOffset(*firstFailure.NextRetry, time.Second),
	)

	if secondFailure.ConsecutiveFailures != 2 {
		t.Errorf(
			"ConsecutiveFailures = %d, want 2 after the second failure",
			secondFailure.ConsecutiveFailures,
		)
	}

	if !secondFailure.NextRetry.After(firstFailure.NextRetry.Time) {
		t.Error("NextRetry did not advance on the second failure")
	}
}

func TestSyncMarksRemovedEntitlementOrphaned(t *testing.T) {
	cache := &fakeCache{}
	downloader := &fakeDownloader{}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	current := []licensingv1alpha1.AppStatus{
		{
			Checksum:       "abc123",
			Name:           "Sample Add-on",
			State:          stateDownloaded,
			VirtualEntryID: 99,
		},
	}

	apps, requeueAfter := syncer.Sync(
		newSyncRequest(nil, cache, current, baseTime()),
	)

	if len(apps) != 1 {
		t.Fatalf("Apps length = %d, want 1", len(apps))
	}

	if apps[0].State != stateOrphaned {
		t.Errorf("State = %q, want %q", apps[0].State, stateOrphaned)
	}

	if apps[0].VirtualEntryID != 99 {
		t.Errorf("VirtualEntryID = %d, want 99", apps[0].VirtualEntryID)
	}

	if requeueAfter != 0 {
		t.Errorf("RequeueAfter = %s, want 0", requeueAfter)
	}

	if len(cache.saved) != 0 {
		t.Error("A removed entitlement must not be saved to the cache")
	}
}

func TestSyncReDownloadsWhenChecksumChanged(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()

	cache := &fakeCache{seeded: map[int64]string{42: "stale"}}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	_, requeueAfter := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, nil, now),
	)

	if requeueAfter != testPollInterval {
		t.Errorf("RequeueAfter = %s, want the poll interval", requeueAfter)
	}

	runner.flush()

	if downloader.calls[42] != 1 {
		t.Errorf("DownloadAddOn calls = %d, want 1", downloader.calls[42])
	}
}

func TestSyncReDownloadsWhenChecksumChangedDuringBackoff(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()
	nextRetry := atOffset(now, 5*time.Minute)

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	current := []licensingv1alpha1.AppStatus{
		{
			Checksum:            "stale",
			ConsecutiveFailures: 2,
			NextRetry:           &nextRetry,
			State:               stateFailed,
			VirtualEntryID:      42,
		},
	}

	apps, _ := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, current, now),
	)

	if apps[0].State != stateDownloading {
		t.Errorf(
			"State = %q, want %q despite the future NextRetry",
			apps[0].State, stateDownloading,
		)
	}

	runner.flush()

	if downloader.calls[42] != 1 {
		t.Errorf("DownloadAddOn calls = %d, want 1", downloader.calls[42])
	}
}

func TestSyncReportsDownloadedWhenCachePresent(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{seeded: map[int64]string{42: "abc123"}}
	downloader := &fakeDownloader{}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	apps, requeueAfter := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, nil, baseTime()),
	)

	if apps[0].State != stateDownloaded {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloaded)
	}

	if requeueAfter != 0 {
		t.Errorf("RequeueAfter = %s, want 0", requeueAfter)
	}

	if downloader.calls[42] != 0 {
		t.Errorf("DownloadAddOn calls = %d, want 0", downloader.calls[42])
	}
}

func TestSyncReportsFailedOnDownloadError(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{}
	downloader := &fakeDownloader{errors: map[int64]error{42: fmt.Errorf("boom")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	failure := recordFailure(syncer, addOn, cache, runner, nil, baseTime())

	if failure.State != stateFailed {
		t.Errorf("State = %q, want %q", failure.State, stateFailed)
	}

	if failure.Message == "" {
		t.Error("Message is empty, want the download error surfaced")
	}

	if failure.NextRetry == nil {
		t.Error("NextRetry is nil, want it scheduled after the failure")
	}

	if _, saved := cache.saved[42]; saved {
		t.Error("A failed download must not be saved to the cache")
	}
}

func TestSyncReportsFailedWhenSaveRejected(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{saveError: fmt.Errorf("checksum mismatch")}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	failure := recordFailure(syncer, addOn, cache, runner, nil, baseTime())

	if failure.State != stateFailed {
		t.Errorf("State = %q, want %q", failure.State, stateFailed)
	}

	if failure.Message == "" {
		t.Error("Message is empty, want the save error surfaced")
	}
}

func TestSyncResetsBackoffWhenDownloaded(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()
	nextRetry := atOffset(now, 5*time.Minute)

	cache := &fakeCache{seeded: map[int64]string{42: "abc123"}}
	downloader := &fakeDownloader{}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	current := []licensingv1alpha1.AppStatus{
		{
			Checksum:            "abc123",
			ConsecutiveFailures: 3,
			Message:             "boom",
			NextRetry:           &nextRetry,
			State:               stateFailed,
			VirtualEntryID:      42,
		},
	}

	apps, _ := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, current, now),
	)

	if apps[0].State != stateDownloaded {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloaded)
	}

	if apps[0].ConsecutiveFailures != 0 {
		t.Errorf("ConsecutiveFailures = %d, want 0", apps[0].ConsecutiveFailures)
	}

	if apps[0].Message != "" {
		t.Errorf("Message = %q, want empty", apps[0].Message)
	}

	if apps[0].NextRetry != nil {
		t.Error("NextRetry is set, want nil once downloaded")
	}
}

func TestSyncRetriesWhenNextRetryElapsed(t *testing.T) {
	addOn := sampleAddOn()

	now := baseTime()
	nextRetry := atOffset(now, -time.Minute)

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := newTestSyncer(downloader, runner)

	current := []licensingv1alpha1.AppStatus{
		{
			Checksum:            "abc123",
			ConsecutiveFailures: 1,
			NextRetry:           &nextRetry,
			State:               stateFailed,
			VirtualEntryID:      42,
		},
	}

	apps, _ := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, current, now),
	)

	if apps[0].State != stateDownloading {
		t.Errorf("State = %q, want %q once the backoff elapsed", apps[0].State, stateDownloading)
	}

	if len(runner.tasks) != 1 {
		t.Errorf("Queued tasks = %d, want 1", len(runner.tasks))
	}
}

func assertRetryInProgress(
	appStatus licensingv1alpha1.AppStatus, t *testing.T,
) {
	t.Helper()

	if appStatus.State != stateDownloading {
		t.Errorf("State = %q, want %q", appStatus.State, stateDownloading)
	}

	if appStatus.ConsecutiveFailures != 2 {
		t.Errorf(
			"ConsecutiveFailures = %d, want 2 carried into the retry",
			appStatus.ConsecutiveFailures,
		)
	}

	if appStatus.Message != "" {
		t.Errorf(
			"Message = %q, want empty while retrying", appStatus.Message,
		)
	}

	if appStatus.NextRetry != nil {
		t.Errorf(
			"NextRetry = %v, want nil while retrying", appStatus.NextRetry,
		)
	}
}

func atOffset(base metav1.Time, offset time.Duration) metav1.Time {
	return metav1.NewTime(base.Add(offset))
}

func baseTime() metav1.Time {
	return metav1.NewTime(time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC))
}

func (manualRunner *manualRunner) flush() {
	tasks := manualRunner.tasks

	manualRunner.tasks = nil

	for _, task := range tasks {
		task()
	}
}

func newSyncRequest(
	addOns []provisioning.AddOn,
	cache Cache,
	current []licensingv1alpha1.AppStatus,
	now metav1.Time,
) SyncRequest {
	return SyncRequest{
		AddOns:        addOns,
		Cache:         cache,
		Context:       context.Background(),
		Current:       current,
		EnvironmentID: "env-1",
		Namespace:     "liferay-dev",
		Now:           now,
		PrivateKey:    nil,
	}
}

func newTestSyncer(downloader Downloader, runner Runner) *Syncer {
	return NewSyncer(
		downloader, testPollInterval, testRetryInitialDelay, testRetryMaxDelay,
		runner,
	)
}

func recordFailure(
	syncer *Syncer,
	addOn provisioning.AddOn,
	cache Cache,
	runner *manualRunner,
	current []licensingv1alpha1.AppStatus,
	now metav1.Time,
) licensingv1alpha1.AppStatus {
	syncer.Sync(newSyncRequest([]provisioning.AddOn{addOn}, cache, current, now))

	runner.flush()

	apps, _ := syncer.Sync(
		newSyncRequest([]provisioning.AddOn{addOn}, cache, current, now),
	)

	return apps[0]
}

func sampleAddOn() provisioning.AddOn {
	return provisioning.AddOn{
		DownloadURL:    "https://example.com/marketplace/virtual-entry/42",
		ProductName:    "Sample Add-on",
		SHA256Checksum: "abc123",
		VirtualEntryID: 42,
	}
}

type fakeCache struct {
	hasError  error
	saveError error
	saved     map[int64]string
	seeded    map[int64]string
}

type fakeDownloader struct {
	bodies map[int64][]byte
	calls  map[int64]int
	errors map[int64]error
}

type manualRunner struct {
	tasks []func()
}
