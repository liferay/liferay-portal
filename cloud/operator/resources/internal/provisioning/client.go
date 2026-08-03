package provisioning

import (
	"context"
	"crypto/rsa"
	"errors"
)

type ActivationRequest struct {
	ActivationCode  string
	EnvironmentID   string
	EnvironmentName string
	PublicKey       string
}

type AddOn struct {
	DownloadURL    string `json:"downloadURL"`
	ProductID      string `json:"productId"`
	ProductName    string `json:"productName"`
	SHA256Checksum string `json:"sha256Checksum"`
	Version        string `json:"version"`
	VirtualEntryID int64  `json:"virtualEntryId"`
}

type Client interface {
	Activate(activationRequest ActivationRequest, context context.Context, privateKey *rsa.PrivateKey) error
	Manifest(context context.Context, manifestRequest ManifestRequest, privateKey *rsa.PrivateKey) (*Entitlements, error)
}

type Entitlements struct {
	AddOns          []AddOn
	LicenseXML      []byte
	MaxClusterNodes int32
}

type EntitlementsResponse struct {
	AddOns          []AddOn `json:"add-ons"`
	LicenseXML      string  `json:"licenseXML"`
	MaxClusterNodes int32   `json:"maxClusterNodes"`
}

type ManifestRequest struct {
	DxpVersion    string
	EnvironmentID string
}

var ErrorActivationRejected = errors.New("provisioning: activation rejected")
