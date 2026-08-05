package license

import (
	"testing"
)

const missingExpirationDateLicenseXML = `<?xml version="1.0"?>
<licenses>
    <license>
		<license-type>enterprise</license-type>
    </license>
</licenses>`

const skipsValidationLicenseXML = `<?xml version="1.0"?>
<licenses>
    <license>
		<expiration-date>not a real date</expiration-date>
		<license-type>enterprise</license-type>
    </license>
    <license>
		<expiration-date>Sunday, January 5, 2031 12:00:00 AM GMT</expiration-date>
		<license-type>virtual-cluster</license-type>
    </license>
</licenses>`

const validLicenseXML = `<?xml version="1.0"?>
<licenses>
    <license>
        <description>Digital Sales Room Cloud Native Environment</description>
        <expiration-date>Friday, March 2, 2029 12:00:00 AM GMT</expiration-date>
        <license-type>enterprise</license-type>
        <license-version>3</license-version>
        <owner>Fry Some Animal</owner>
        <product-name>Digital Sales Room</product-name>
        <product-id>23bd2e2b-c4aa-45d7-86c9-ba0e2f890bf5</product-id>
        <product-version>1</product-version>
        <start-date>Wednesday, July 1, 2026 12:00:00 AM GMT</start-date>
    </license>
    <license>
        <account-name>Fry Some Animal</account-name>
        <description>Cloud Native</description>
        <expiration-date>Friday, March 2, 2029 12:00:00 AM GMT</expiration-date>
        <instance-size>Sizing 4</instance-size>
        <license-name>DXP Non-Production (Virtual Cluster)</license-name>
        <license-type>virtual-cluster</license-type>
        <license-version>6</license-version>
        <max-cluster-nodes>1</max-cluster-nodes>
        <owner>Fry Some Animal</owner>
        <product-name>DXP Production</product-name>
        <product-version>2026.Q3</product-version>
        <start-date>Wednesday, July 1, 2026 12:00:00 AM GMT</start-date>
    </license>
</licenses>`

func TestExpirationDateReturnsVirtualClusterDate(t *testing.T) {
	expirationDate, error := ExpirationDate([]byte(validLicenseXML))

	if error != nil {
		t.Fatalf("Unexpected error: %v", error)
	}

	if formatted := expirationDate.Format("2006-01-02"); formatted != "2029-03-02" {
		t.Errorf("Expected 2029-03-02, got %s", formatted)
	}
}

func TestExpirationDateSkipsNonVirtualClusterLicenses(t *testing.T) {
	expirationDate, error := ExpirationDate([]byte(skipsValidationLicenseXML))

	if error != nil {
		t.Fatalf("Unexpected error: %v", error)
	}

	if formatted := expirationDate.Format("2006-01-02"); formatted != "2031-01-05" {
		t.Errorf("Expected 2031-01-05, got %s", formatted)
	}
}

func TestExpirationDateWhenNoVirtualClusterLicenseReturnsError(t *testing.T) {
	if _, error := ExpirationDate([]byte(missingExpirationDateLicenseXML)); error == nil {
		t.Fatal("Expected an error, got nil")
	}
}
