package addon

import (
	"bytes"
	"context"
	"crypto/rsa"
	"fmt"
	"io"
	"testing"

	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
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

func TestSyncDoesNotRelaunchInFlightDownload(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := NewSyncer(downloader, runner)

	syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	apps, pending := syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if !pending {
		t.Error("pending = false, want true while the download is in flight")
	}

	if apps[0].State != stateDownloading {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloading)
	}

	if len(runner.tasks) != 1 {
		t.Errorf("queued tasks = %d, want 1 without a relaunch", len(runner.tasks))
	}
}

func TestSyncDownloadsNewAddOnAndReportsDownloaded(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := NewSyncer(downloader, runner)

	apps, pending := syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if !pending {
		t.Error("pending = false, want true while the download is queued")
	}

	if apps[0].State != stateDownloading {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloading)
	}

	runner.flush()

	if downloader.calls[42] != 1 {
		t.Errorf("DownloadAddOn calls = %d, want 1", downloader.calls[42])
	}

	apps, pending = syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if pending {
		t.Error("pending = true, want false once the download completed")
	}

	if apps[0].State != stateDownloaded {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloaded)
	}

	if _, saved := cache.saved[42]; !saved {
		t.Error("The add-on was not saved to the cache")
	}
}

func TestSyncReDownloadsWhenChecksumChanged(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{seeded: map[int64]string{42: "stale"}}
	downloader := &fakeDownloader{bodies: map[int64][]byte{42: []byte("lpkg")}}
	runner := &manualRunner{}
	syncer := NewSyncer(downloader, runner)

	_, pending := syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if !pending {
		t.Error("pending = false, want true when the checksum changed")
	}

	runner.flush()

	apps, _ := syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if apps[0].State != stateDownloaded {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloaded)
	}

	if downloader.calls[42] != 1 {
		t.Errorf("DownloadAddOn calls = %d, want 1", downloader.calls[42])
	}
}

func TestSyncReportsDownloadedWhenCachePresent(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{seeded: map[int64]string{42: "abc123"}}
	downloader := &fakeDownloader{}
	runner := &manualRunner{}
	syncer := NewSyncer(downloader, runner)

	apps, pending := syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if pending {
		t.Error("pending = true, want false when the cache already holds the add-on")
	}

	if apps[0].State != stateDownloaded {
		t.Errorf("State = %q, want %q", apps[0].State, stateDownloaded)
	}

	if downloader.calls[42] != 0 {
		t.Errorf("DownloadAddOn calls = %d, want 0", downloader.calls[42])
	}

	if len(runner.tasks) != 0 {
		t.Errorf("queued tasks = %d, want 0", len(runner.tasks))
	}
}

func TestSyncReportsFailedOnDownloadError(t *testing.T) {
	addOn := sampleAddOn()

	cache := &fakeCache{}
	downloader := &fakeDownloader{errors: map[int64]error{42: fmt.Errorf("boom")}}
	runner := &manualRunner{}
	syncer := NewSyncer(downloader, runner)

	syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	runner.flush()

	apps, pending := syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if pending {
		t.Error("pending = true, want false after a failed download")
	}

	if apps[0].State != stateFailed {
		t.Errorf("State = %q, want %q", apps[0].State, stateFailed)
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
	syncer := NewSyncer(downloader, runner)

	syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	runner.flush()

	apps, _ := syncer.Sync(
		[]provisioning.AddOn{addOn}, cache, context.Background(), "env-1",
		"liferay-dev", nil,
	)

	if apps[0].State != stateFailed {
		t.Errorf("State = %q, want %q", apps[0].State, stateFailed)
	}
}

func (manualRunner *manualRunner) flush() {
	tasks := manualRunner.tasks

	manualRunner.tasks = nil

	for _, task := range tasks {
		task()
	}
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
