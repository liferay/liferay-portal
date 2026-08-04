package license

import (
	"encoding/xml"
	"fmt"
	"time"
)

const (
	dateLayout                = "Monday, January 2, 2006 3:04:05 PM MST"
	licenseTypeVirtualCluster = "virtual-cluster"
)

func ExpirationDate(licenseXML []byte) (time.Time, error) {
	var licenseSet licenseSet

	if error := xml.Unmarshal(licenseXML, &licenseSet); error != nil {
		return time.Time{}, fmt.Errorf("license.xml parse error: %w", error)
	}

	for _, licenseEntry := range licenseSet.Licenses {
		if licenseEntry.LicenseType != licenseTypeVirtualCluster {
			continue
		}

		expirationDate, error := time.Parse(dateLayout, licenseEntry.ExpirationDate)

		if error != nil {
			return time.Time{}, fmt.Errorf(
				"license.xml expiration-date parse error: %q %w",
				licenseEntry.ExpirationDate, error,
			)
		}

		return expirationDate, nil
	}

	return time.Time{}, fmt.Errorf("license: no virtual-cluster license found")
}

type licenseEntry struct {
	ExpirationDate string `xml:"expiration-date"`
	LicenseType    string `xml:"license-type"`
}

type licenseSet struct {
	Licenses []licenseEntry `xml:"license"`
}
