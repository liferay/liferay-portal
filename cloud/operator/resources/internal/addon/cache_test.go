package addon

import (
	"bytes"
	"crypto/sha256"
	"encoding/hex"
	"os"
	"path/filepath"
	"testing"
)

func TestFilesystemCache(t *testing.T) {
	content := []byte("PK\x03\x04 fake lpkg bytes")
	updated := []byte("PK\x03\x04 newer lpkg bytes")

	contentChecksum := checksum(content)
	updatedChecksum := checksum(updated)

	testCases := map[string]struct {
		hasChecksum   string
		saves         []cacheSave
		wantFile      bool
		wantHas       bool
		wantSaveError bool
	}{
		"overwrites an existing file on a second save": {
			hasChecksum: updatedChecksum,
			saves: []cacheSave{
				{checksum: contentChecksum, content: content},
				{checksum: updatedChecksum, content: updated},
			},
			wantFile: true,
			wantHas:  true,
		},
		"rejects a checksum mismatch and writes no file": {
			saves: []cacheSave{
				{checksum: checksum([]byte("wrong")), content: content},
			},
			wantSaveError: true,
		},
		"reports not held when the checksum differs": {
			hasChecksum: checksum([]byte("other")),
			saves: []cacheSave{
				{checksum: contentChecksum, content: content},
			},
			wantFile: true,
		},
		"reports not held when the file is absent": {
			hasChecksum: contentChecksum,
		},
		"saves then reports held for a matching checksum": {
			hasChecksum: contentChecksum,
			saves: []cacheSave{
				{checksum: contentChecksum, content: content},
			},
			wantFile: true,
			wantHas:  true,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			directory := t.TempDir()
			cache := NewFilesystemCache(directory)

			var saveError error

			for _, cacheSave := range testCase.saves {
				saveError = cache.Save(
					cacheSave.checksum, bytes.NewReader(cacheSave.content), 1,
				)
			}

			if testCase.wantSaveError {
				if saveError == nil {
					t.Fatal("Save error = nil, want a checksum mismatch")
				}

				entries, _ := os.ReadDir(directory)

				if len(entries) != 0 {
					t.Errorf(
						"directory entries = %d, want 0 after a rejected save",
						len(entries),
					)
				}

				return
			}

			if saveError != nil {
				t.Fatalf("Unexpected save error: %v", saveError)
			}

			if testCase.wantFile {
				if _, error := os.Stat(
					filepath.Join(directory, "1.lpkg"),
				); error != nil {
					t.Fatalf("Expected 1.lpkg on disk: %v", error)
				}
			}

			has, error := cache.Has(testCase.hasChecksum, 1)

			if error != nil {
				t.Fatalf("Unexpected error: %v", error)
			}

			if has != testCase.wantHas {
				t.Errorf("Has = %v, want %v", has, testCase.wantHas)
			}
		})
	}
}

func checksum(data []byte) string {
	sum := sha256.Sum256(data)

	return hex.EncodeToString(sum[:])
}

type cacheSave struct {
	checksum string
	content  []byte
}
