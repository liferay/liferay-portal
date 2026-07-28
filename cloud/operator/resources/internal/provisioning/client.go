package provisioning

import (
	"context"
	"crypto/rsa"
	"errors"
)

var ErrorActivationRejected = errors.New("provisioning: activation rejected")

type ActivationRequest struct {
	ActivationCode  string
	EnvironmentID   string
	EnvironmentName string
	PublicKey       string
}

type Client interface {
	Activate(context context.Context, privateKey *rsa.PrivateKey, activationRequest ActivationRequest) error
}