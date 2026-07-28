// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments,verbs=get;list;patch;update;watch
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/finalizers,verbs=update
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/status,verbs=get;patch;update
package licensing

import (
	"context"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	identitySecretSuffix = "-identity"
	licenseSecretSuffix  = "-license"
)

type LiferayEnvironmentReconciler struct {
	client.Client

	HeartbeatInterval time.Duration
}

// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;watch
// +kubebuilder:rbac:groups="",resources=secrets,verbs=get;list;watch;create;update;patch
func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) Reconcile(
	context context.Context,
	request controllerruntime.Request,
) (controllerruntime.Result, error) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{}

	if error := liferayEnvironmentReconciler.Get(context, request.NamespacedName, liferayEnvironment); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
	}

	// environmentId is the namespace UID: stable, unique, DDOS-allowlisted.
	environmentId, error := liferayEnvironmentReconciler.resolveEnvironmentId(context, liferayEnvironment.Namespace)

	if error != nil {
		return ctrl.Result{}, error
	}

	liferayEnvironment.Status.EnvironmentId = environmentId

	// Ensure the cluster keypair exists; the private key never leaves here.

	if _, error := liferayEnvironmentReconciler.ensureIdentity(context, liferayEnvironment); error != nil {
		return ctrl.Result{}, error
	}

	if liferayEnvironment.Status.Phase == "" {
		liferayEnvironment.Status.Phase = "Pending"
	}

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Message: "Reconcile is not implemented.",
			Reason:  "NotImplemented",
			Status:  metav1.ConditionFalse,
			Type:    "Ready",
		},
	)

	status := liferayEnvironmentReconciler.Status()

	if error := status.Update(context, liferayEnvironment); error != nil {
		if errors.IsConflict(error) {
			return controllerruntime.Result{RequeueAfter: time.Second}, nil
		}

		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{RequeueAfter: liferayEnvironmentReconciler.HeartbeatInterval}, nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) SetupWithManager(
	manager controllerruntime.Manager,
) error {
	return controllerruntime.NewControllerManagedBy(
		manager,
	).For(
		&licensingv1alpha1.LiferayEnvironment{},
	).Named(
		"liferayenvironment",
	).Owns(
		&corev1.Secret{},
	).Complete(
		liferayEnvironmentReconciler,
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) ensureIdentity(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (*rsa.PrivateKey, error) {

	identityName := liferayEnvironment.Name + identitySecretSuffix

	key := types.NamespacedName{Namespace: liferayEnvironment.Namespace, Name: identityName}

	secret := &corev1.Secret{}

	getError := liferayEnvironmentReconciler.Get(context, key, secret)

	if getError == nil {
		return parsePrivateKey(secret.Data["private.pem"])
	}

	if !apierrors.IsNotFound(getError) {
		return nil, getError
	}

	privateKey, generateKeyError := rsa.GenerateKey(rand.Reader, 2048)

	if generateKeyError != nil {
		return nil, generateKeyError
	}

	privateBytes, marshalKeyError := x509.MarshalPKCS8PrivateKey(privateKey)

	if marshalKeyError != nil {
		return nil, marshalKeyError
	}

	privatePEM := pem.EncodeToMemory(
		&pem.Block{Type: "PRIVATE KEY", Bytes: privateBytes},
	)

	publicPEM, publicKeyError := publicKeyPEM(privateKey)

	if publicKeyError != nil {
		return nil, publicKeyError
	}

	secret = &corev1.Secret{
		Data: map[string][]byte{
			"private.pem": privatePEM,
			"public.pem":  []byte(publicPEM),
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: liferayEnvironment.Namespace,
			Name:      identityName,
			Labels:    map[string]string{"controller-watched": "yes"},
		},
	}

	if err := ctrl.SetControllerReference(liferayEnvironment, secret, liferayEnvironmentReconciler.Scheme()); err != nil {
		return nil, err
	}

	if err := liferayEnvironmentReconciler.Create(context, secret); err != nil {
		return nil, err
	}

	return privateKey, nil
}

func parsePrivateKey(data []byte) (*rsa.PrivateKey, error) {
	block, _ := pem.Decode(data)

	if block == nil {
		return nil, fmt.Errorf("identity secret: no PEM block in private.pem")
	}

	parsed, error := x509.ParsePKCS8PrivateKey(block.Bytes)

	if error != nil {
		return nil, error
	}

	privateKey, ok := parsed.(*rsa.PrivateKey)

	if !ok {
		return nil, fmt.Errorf("identity secret: not an RSA private key")
	}

	return privateKey, nil
}

func publicKeyPEM(privateKey *rsa.PrivateKey) (string, error) {
	publicBytes, err := x509.MarshalPKIXPublicKey(&privateKey.PublicKey)

	if err != nil {
		return "", err
	}

	return string(
		pem.EncodeToMemory(
			&pem.Block{Type: "PUBLIC KEY", Bytes: publicBytes},
		),
	), nil
}

func (LiferayEnvironmentReconciler *LiferayEnvironmentReconciler) resolveEnvironmentId(
	context context.Context, namespaceName string,
) (string, error) {

	namespace := &corev1.Namespace{}

	if error := LiferayEnvironmentReconciler.Get(context, types.NamespacedName{Name: namespaceName}, namespace); error != nil {
		return "", error
	}

	return string(namespace.UID), nil
}

type LiferayEnvironmentReconciler struct {
	client.Client

	HeartbeatInterval time.Duration
}
=