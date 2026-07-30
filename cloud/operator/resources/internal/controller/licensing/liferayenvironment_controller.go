// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments,verbs=get;list;patch;update;watch
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/finalizers,verbs=update
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/status,verbs=get;patch;update
package licensing

import (
	"context"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	identitySecretSuffix = "-identity"
)

// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;watch
// +kubebuilder:rbac:groups="",resources=secrets,verbs=create;get;list;patch;update;watch
func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) Reconcile(
	context context.Context,
	request controllerruntime.Request,
) (controllerruntime.Result, error) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{}

	if error := liferayEnvironmentReconciler.Get(context, request.NamespacedName, liferayEnvironment); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
	}

	environmentID, error := liferayEnvironmentReconciler.resolveEnvironmentID(context, liferayEnvironment.Namespace)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	liferayEnvironment.Status.EnvironmentID = environmentID

	privateKey, error := liferayEnvironmentReconciler.ensureIdentity(context, liferayEnvironment)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if liferayEnvironment.Status.ActivatedAt == nil {
		publicKey, error := publicKeyBase64(privateKey)

		if error != nil {
			return controllerruntime.Result{}, error
		}

		activationCode, error := liferayEnvironmentReconciler.readActivationCode(context, liferayEnvironment)

		if error != nil {
			return controllerruntime.Result{}, error
		}

		if error := liferayEnvironmentReconciler.Provisioning.Activate(
			provisioning.ActivationRequest{
				ActivationCode:  activationCode,
				EnvironmentID:   environmentID,
				EnvironmentName: liferayEnvironment.Spec.EnvironmentName,
				PublicKey:       publicKey,
			}, context, privateKey); error != nil {
			meta.SetStatusCondition(
				&liferayEnvironment.Status.Conditions,
				metav1.Condition{
					Message: error.Error(),
					Reason:  "ActivationRejected",
					Status:  metav1.ConditionFalse,
					Type:    "Activated",
				},
			)

			liferayEnvironment.Status.Phase = "Degraded"

			return liferayEnvironmentReconciler.finish(context, liferayEnvironment)
		}

		now := metav1.Now()

		liferayEnvironment.Status.ActivatedAt = &now

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Reason: "Activated",
				Status: metav1.ConditionTrue,
				Type:   "Activated",
			},
		)
	}

	if liferayEnvironment.Status.Phase == "" {
		liferayEnvironment.Status.Phase = "Pending"
	}

	if error := liferayEnvironmentReconciler.Status().Update(context, liferayEnvironment); error != nil {
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

	secret := &corev1.Secret{}

	getError := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{
			Name:      identityName,
			Namespace: liferayEnvironment.Namespace,
		}, secret)

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
			Name:      identityName,
			Namespace: liferayEnvironment.Namespace,
		},
	}

	if err := controllerruntime.SetControllerReference(liferayEnvironment, secret, liferayEnvironmentReconciler.Scheme()); err != nil {
		return nil, err
	}

	if err := liferayEnvironmentReconciler.Create(context, secret); err != nil {
		return nil, err
	}

	return privateKey, nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) finish(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (controllerruntime.Result, error) {
	status := liferayEnvironmentReconciler.Status()

	if error := status.Update(context, liferayEnvironment); error != nil {
		if errors.IsConflict(error) {
			return controllerruntime.Result{RequeueAfter: time.Second}, nil
		}

		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{RequeueAfter: liferayEnvironmentReconciler.HeartbeatInterval}, nil
}

func parsePrivateKey(privatePEM []byte) (*rsa.PrivateKey, error) {
	block, _ := pem.Decode(privatePEM)

	if block == nil {
		return nil, fmt.Errorf("identity secret: no PEM block in private.pem")
	}

	parsed, error := x509.ParsePKCS8PrivateKey(block.Bytes)

	if error != nil {
		return nil, error
	}

	parsedPrivateKey, ok := parsed.(*rsa.PrivateKey)

	if !ok {
		return nil, fmt.Errorf("identity secret: not an RSA private key")
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) readActivationCode(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (string, error) {
	reference := liferayEnvironment.Spec.ActivationCodeSecretRef

	key := types.NamespacedName{
		Name:      reference.Name,
		Namespace: liferayEnvironment.Namespace,
	}

	secret := &corev1.Secret{}

	if error := liferayEnvironmentReconciler.Get(context, key, secret); error != nil {
		return "", error
	}

	code, ok := secret.Data[reference.Key]

	if !ok {
		return "", fmt.Errorf(
			"activation code secret %q missing key %q",
			reference.Name, reference.Key)
	}

	return string(code), nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) resolveEnvironmentID(
	context context.Context,
	namespaceName string,
) (string, error) {
	namespace := &corev1.Namespace{}

	if error := liferayEnvironmentReconciler.Get(context, types.NamespacedName{Name: namespaceName}, namespace); error != nil {
		return "", error
	}

	return string(namespace.UID), nil
}

type LiferayEnvironmentReconciler struct {
	client.Client

	HeartbeatInterval time.Duration
	Provisioning      provisioning.Client
}
