// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments,verbs=get;list;patch;update;watch
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/finalizers,verbs=update
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/status,verbs=get;patch;update
package licensing

import (
	"bytes"
	"context"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"encoding/pem"
	"fmt"
	"maps"
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
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	conditionActivated             = "Activated"
	conditionLicenseValid          = "LicenseValid"
	conditionProvisioningReachable = "ProvisioningReachable"
	entitlementsSecretSuffix       = "-entitlements"
	identitySecretSuffix           = "-identity"
)

// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;watch
// +kubebuilder:rbac:groups="",resources=secrets,verbs=create;get;list;patch;update;watch
func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) Reconcile(
	context context.Context,
	request controllerruntime.Request,
) (controllerruntime.Result, error) {
	logger := logf.FromContext(context)

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

		logger.Info(
			"Activating environment",
			"environmentID", environmentID,
			"environmentName", liferayEnvironment.Spec.EnvironmentName,
		)

		if error := liferayEnvironmentReconciler.Provisioning.Activate(
			provisioning.ActivationRequest{
				ActivationCode:  activationCode,
				EnvironmentID:   environmentID,
				EnvironmentName: liferayEnvironment.Spec.EnvironmentName,
				PublicKey:       publicKey,
			}, context, privateKey); error != nil {
			logger.Error(error, "Activation rejected", "environmentID", environmentID)

			meta.SetStatusCondition(
				&liferayEnvironment.Status.Conditions,
				metav1.Condition{
					Message: error.Error(),
					Reason:  "ActivationRejected",
					Status:  metav1.ConditionFalse,
					Type:    conditionActivated,
				},
			)

			liferayEnvironment.Status.Phase = "Degraded"

			return liferayEnvironmentReconciler.finish(context, liferayEnvironment)
		}

		now := metav1.Now()

		liferayEnvironment.Status.ActivatedAt = &now

		logger.Info("Environment activated", "environmentID", environmentID)

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Reason: "Activated",
				Status: metav1.ConditionTrue,
				Type:   conditionActivated,
			},
		)
	}

	entitlements, error := liferayEnvironmentReconciler.Provisioning.Manifest(
		context,
		provisioning.ManifestRequest{
			DxpVersion:    liferayEnvironmentReconciler.resolveDxpVersion(liferayEnvironment),
			EnvironmentID: environmentID,
		},
		privateKey,
	)

	if error != nil {
		logger.Error(error, "Entitlements fetch failed", "environmentID", environmentID)

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: error.Error(),
				Reason:  "EntitlementsFetchFailed",
				Status:  metav1.ConditionFalse,
				Type:    conditionProvisioningReachable,
			},
		)

		liferayEnvironment.Status.Phase = "Degraded"

		return liferayEnvironmentReconciler.finish(context, liferayEnvironment)
	}

	logger.Info(
		"Entitlements fetched",
		"environmentID", environmentID,
		"maxClusterNodes", entitlements.MaxClusterNodes,
	)

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "Reachable",
			Status: metav1.ConditionTrue,
			Type:   conditionProvisioningReachable,
		},
	)

	if error := liferayEnvironmentReconciler.persistEntitlementsSecret(context, entitlements, liferayEnvironment); error != nil {
		return controllerruntime.Result{}, error
	}

	now := metav1.Now()

	liferayEnvironment.Status.License.Checksum = licenseChecksum(entitlements.LicenseXML)
	liferayEnvironment.Status.License.LastVerified = &now
	liferayEnvironment.Status.License.MaxClusterNodes = entitlements.MaxClusterNodes

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "LicensePresent",
			Status: metav1.ConditionTrue,
			Type:   conditionLicenseValid,
		},
	)

	liferayEnvironment.Status.Phase = "Ready"

	return liferayEnvironmentReconciler.finish(context, liferayEnvironment)
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

	if error := controllerruntime.SetControllerReference(liferayEnvironment, secret, liferayEnvironmentReconciler.Scheme()); error != nil {
		return nil, error
	}

	if error := liferayEnvironmentReconciler.Create(context, secret); error != nil {
		return nil, error
	}

	logf.FromContext(context).Info("Generated identity keypair", "secret", identityName)

	return privateKey, nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) finish(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (controllerruntime.Result, error) {
	if error := liferayEnvironmentReconciler.Status().Update(context, liferayEnvironment); error != nil {
		if errors.IsConflict(error) {
			return controllerruntime.Result{RequeueAfter: time.Second}, nil
		}

		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{RequeueAfter: liferayEnvironmentReconciler.HeartbeatInterval}, nil
}

func licenseChecksum(licenseXML []byte) string {
	sum := sha256.Sum256(licenseXML)

	return hex.EncodeToString(sum[:])
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) persistEntitlementsSecret(
	context context.Context,
	entitlements *provisioning.Entitlements,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) error {
	entitlementsName := liferayEnvironment.Name + entitlementsSecretSuffix

	addOns := entitlements.AddOns

	if addOns == nil {
		addOns = []provisioning.AddOn{}
	}

	addOnsJSON, error := json.Marshal(addOns)

	if error != nil {
		return error
	}

	data := map[string][]byte{
		"add-ons.json":    addOnsJSON,
		"license.xml":     entitlements.LicenseXML,
		"maxClusterNodes": []byte(fmt.Sprintf("%d", entitlements.MaxClusterNodes)),
	}

	secret := &corev1.Secret{}

	getError := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{
			Name:      entitlementsName,
			Namespace: liferayEnvironment.Namespace,
		}, secret)

	if getError == nil {
		if maps.EqualFunc(secret.Data, data, bytes.Equal) {
			return nil
		}

		secret.Data = data

		if error := liferayEnvironmentReconciler.Update(context, secret); error != nil {
			return error
		}

		logf.FromContext(context).Info("Updated entitlements secret", "secret", entitlementsName)

		return nil
	}

	if !errors.IsNotFound(getError) {
		return getError
	}

	secret = &corev1.Secret{
		Data: data,
		ObjectMeta: metav1.ObjectMeta{
			Labels:    map[string]string{"controller-watched": "yes"},
			Name:      entitlementsName,
			Namespace: liferayEnvironment.Namespace,
		},
	}

	if error := controllerruntime.SetControllerReference(liferayEnvironment, secret, liferayEnvironmentReconciler.Scheme()); error != nil {
		return error
	}

	if error := liferayEnvironmentReconciler.Create(context, secret); error != nil {
		return error
	}

	logf.FromContext(context).Info("Created entitlements secret", "secret", entitlementsName)

	return nil
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) resolveDxpVersion(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) string {
	if liferayEnvironment.Spec.DxpVersion != "" {
		return liferayEnvironment.Spec.DxpVersion
	}

	// TODO derive from the workload's container image tag

	return ""
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
