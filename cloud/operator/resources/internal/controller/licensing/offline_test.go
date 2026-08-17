package licensing

import (
	"archive/zip"
	"encoding/base64"
	"fmt"
	"os"
	"path/filepath"
	"testing"
)

func TestReadOfflineActivationBundleParsesAWellFormedBundle(t *testing.T) {
	path := filepath.Join(t.TempDir(), "bundle.zip")

	licenseXML := "<licenses>ok</licenses>"

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app.lpkg": "PK-fake",
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [
						{
							"productId": "fake",
							"sha256Checksum": "0000"
						}
					],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		path, t,
	)

	entitlements, error := readOfflineActivationBundle(path)

	if error != nil {
		t.Fatalf("Unexpected error: %v", error)
	}

	if string(entitlements.LicenseXML) != licenseXML {
		t.Errorf("LicenseXML = %q, want %q", entitlements.LicenseXML, licenseXML)
	}

	if entitlements.MaxClusterNodes != 3 {
		t.Errorf("MaxClusterNodes = %d, want 3", entitlements.MaxClusterNodes)
	}

	if length := len(entitlements.AddOns); length != 1 {
		t.Fatalf("AddOns length = %d, want 1", length)
	}

	if entitlements.AddOns[0].ProductID != "fake" {
		t.Errorf("AddOns[0].ProductID = %q, want fake", entitlements.AddOns[0].ProductID)
	}
}

func TestReadOfflineActivationBundleRejectsANonZipFile(t *testing.T) {
	path := filepath.Join(t.TempDir(), "not.zip")

	if error := os.WriteFile(path, []byte("this is not a zip"), 0o644); error != nil {
		t.Fatalf("Unable to write the file: %v", error)
	}

	if _, error := readOfflineActivationBundle(path); error == nil {
		t.Error("readOfflineActivationBundle error = nil, want a zip error")
	}
}

func TestReadOfflineActivationBundleRejectsMalformedBundles(t *testing.T) {
	validManifest := fmt.Sprintf(
		`{
				"licenseXML": %q,
				"maxClusterNodes": 1
			}`,
		base64.StdEncoding.EncodeToString([]byte("<licenses/>")),
	)

	testCases := map[string]map[string]string{
		"missing add-ons directory": {
			"manifest.json": validManifest,
		},
		"missing manifest": {
			"add-ons/app.lpkg": "PK-fake",
		},
		"undecodable license": {
			"add-ons/app.lpkg": "PK-fake",
			"manifest.json": `{
					"licenseXML": "@@not-base64@@",
					"maxClusterNodes": 1
				}`,
		},
		"unparsable manifest json": {
			"add-ons/app.lpkg": "PK-fake",
			"manifest.json":    "{not-json",
		},
	}

	for name, files := range testCases {
		t.Run(name, func(t *testing.T) {
			path := filepath.Join(t.TempDir(), "bundle.zip")

			writeOfflineActivationBundle(files, path, t)

			if _, error := readOfflineActivationBundle(path); error == nil {
				t.Error("readOfflineActivationBundle error = nil, want a validation error")
			}
		})
	}
}

func TestReadOfflineActivationBundleReturnsNotFoundForAMissingFile(t *testing.T) {
	_, error := readOfflineActivationBundle(filepath.Join(t.TempDir(), "absent.zip"))

	if !isOfflineActivationBundleNotFound(error) {
		t.Errorf("readOfflineActivationBundle error = %v, want isOfflineActivationBundleNotFound", error)
	}
}

func writeOfflineActivationBundle(files map[string]string, path string, t *testing.T) {
	t.Helper()

	if error := os.MkdirAll(filepath.Dir(path), 0o755); error != nil {
		t.Fatalf("Unable to create the bundle directory: %v", error)
	}

	file, error := os.Create(path)

	if error != nil {
		t.Fatalf("Unable to create the bundle file: %v", error)
	}

	defer file.Close()

	zipWriter := zip.NewWriter(file)

	for name, content := range files {
		writer, error := zipWriter.Create(name)

		if error != nil {
			t.Fatalf("Unable to add %q to the bundle: %v", name, error)
		}

		if _, error := writer.Write([]byte(content)); error != nil {
			t.Fatalf("Unable to write %q to the bundle: %v", name, error)
		}
	}

	if error := zipWriter.Close(); error != nil {
		t.Fatalf("Unable to finalize the bundle: %v", error)
	}
}
