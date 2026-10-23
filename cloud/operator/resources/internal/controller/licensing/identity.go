package licensing

import (
	"context"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const identitySecretSuffix = "-identity"

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) ensureIdentity(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (*rsa.PrivateKey, error) {
	identitySecretKey := getIdentitySecretKey(liferayEnvironment)

	secret := &corev1.Secret{}

	getError := liferayEnvironmentReconciler.Get(context, identitySecretKey, secret)

	if getError == nil {
		return parsePrivateKey(secret.Data["private.pem"])
	}

	if !errors.IsNotFound(getError) {
		return nil, getError
	}

	privateKey, error := rsa.GenerateKey(rand.Reader, 2048)

	if error != nil {
		return nil, error
	}

	privateBytes, error := x509.MarshalPKCS8PrivateKey(privateKey)

	if error != nil {
		return nil, error
	}

	publicPEM, error := publicKeyBase64(privateKey)

	if error != nil {
		return nil, error
	}

	secret = &corev1.Secret{
		Data: map[string][]byte{
			"private.pem": pem.EncodeToMemory(
				&pem.Block{
					Bytes: privateBytes,
					Type:  "PRIVATE KEY",
				},
			),
			"public.pem": []byte(publicPEM),
		},
		ObjectMeta: metav1.ObjectMeta{
			Labels:    map[string]string{"controller-watched": "yes"},
			Name:      identitySecretKey.Name,
			Namespace: identitySecretKey.Namespace,
		},
	}

	if error := controllerruntime.SetControllerReference(liferayEnvironment, secret, liferayEnvironmentReconciler.Scheme()); error != nil {
		return nil, error
	}

	if error := liferayEnvironmentReconciler.Create(context, secret); error != nil {
		return nil, error
	}

	logf.FromContext(context).Info("Identity keypair was generated", "secret", identitySecretKey.Name)

	return privateKey, nil
}

func getIdentitySecretKey(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) types.NamespacedName {
	return types.NamespacedName{
		Name:      liferayEnvironment.Name + identitySecretSuffix,
		Namespace: liferayEnvironment.Namespace,
	}
}

func parsePrivateKey(privatePEM []byte) (*rsa.PrivateKey, error) {
	block, _ := pem.Decode(privatePEM)

	if block == nil {
		return nil, fmt.Errorf("identity secret: \"private.pem\" has no PEM block")
	}

	parsed, error := x509.ParsePKCS8PrivateKey(block.Bytes)

	if error != nil {
		return nil, error
	}

	parsedPrivateKey, ok := parsed.(*rsa.PrivateKey)

	if !ok {
		return nil, fmt.Errorf("identity secret: private key is not an RSA private key")
	}

	return parsedPrivateKey, nil
}

func publicKeyBase64(privateKey *rsa.PrivateKey) (string, error) {
	publicBytes, error := x509.MarshalPKIXPublicKey(&privateKey.PublicKey)

	if error != nil {
		return "", error
	}

	return base64.StdEncoding.EncodeToString(publicBytes), nil
}
