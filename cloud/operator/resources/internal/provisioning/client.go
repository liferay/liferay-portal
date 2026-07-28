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

type Client interface {
	Activate(activationRequest ActivationRequest, context context.Context, privateKey *rsa.PrivateKey) error
}

var ErrorActivationRejected = errors.New("provisioning: activation rejected")
