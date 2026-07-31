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

type App struct {
	LpkgDownloadLink string
	Name             string
}

type Client interface {
	Activate(activationRequest ActivationRequest, context context.Context, privateKey *rsa.PrivateKey) error
	Entitlements(context context.Context, entitlementsRequest EntitlementsRequest, privateKey *rsa.PrivateKey) (*Entitlements, error)
}

type Entitlements struct {
	Apps            []App
	LicenseXML      []byte
	MaxClusterNodes int32
}

type EntitlementsRequest struct {
	DxpVersion    string
	EnvironmentID string
}

var ErrorActivationRejected = errors.New("provisioning: activation rejected")
