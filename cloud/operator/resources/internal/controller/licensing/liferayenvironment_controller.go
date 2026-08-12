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
	"math"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	license "github.com/liferay/liferay-portal/cloud/operator/internal/license"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	builder "sigs.k8s.io/controller-runtime/pkg/builder"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
	predicate "sigs.k8s.io/controller-runtime/pkg/predicate"
)

const (
	conditionActivated             = "Activated"
	conditionGracePeriodExpired    = "GracePeriodExpired"
	conditionLicenseValid          = "LicenseValid"
	conditionProvisioningReachable = "ProvisioningReachable"
	conditionReplicasCountValid    = "ReplicasCountValid"
	entitlementsSecretSuffix       = "-entitlements"
	environmentLabel               = "licensing.liferay.com/environment"
	gracePeriodReplicaCeiling      = 1
	identitySecretSuffix           = "-identity"
)

// +kubebuilder:rbac:groups="",resources=events,verbs=create;patch
// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;patch;update;watch
// +kubebuilder:rbac:groups="",resources=secrets,verbs=create;get;list;patch;update;watch
// +kubebuilder:rbac:groups=apps,resources=statefulsets,verbs=get;list;patch;update;watch
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

	if error := liferayEnvironmentReconciler.ensureNamespaceEnvironmentLabel(
		context, liferayEnvironment.Namespace,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	liferayEnvironment.Status.EnvironmentID = environmentID

	privateKey, error := liferayEnvironmentReconciler.ensureIdentity(context, liferayEnvironment)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if liferayEnvironment.Spec.Offline {
		publicKey, error := publicKeyBase64(privateKey)

		if error != nil {
			return controllerruntime.Result{}, error
		}

		offlineActivationPayload, error := provisioning.OfflineActivationPayload(
			provisioning.ActivationRequest{
				EnvironmentID:   environmentID,
				EnvironmentName: liferayEnvironment.Spec.EnvironmentName,
				PublicKey:       publicKey,
			},
			privateKey,
		)

		if error != nil {
			return controllerruntime.Result{}, error
		}

		if error := liferayEnvironmentReconciler.persistOfflineRequest(
			context, liferayEnvironment, offlineActivationPayload,
		); error != nil {
			return controllerruntime.Result{}, error
		}

		logger.V(1).Info("Awaiting offline activation bundle", "environmentID", environmentID)

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: "Waiting for the offline activation bundle to be provided",
				Reason:  "AwaitingOfflineActivationBundle",
				Status:  metav1.ConditionFalse,
				Type:    conditionActivated,
			},
		)

		liferayEnvironment.Status.Phase = "Pending"

		return liferayEnvironmentReconciler.finishAfter(
			context, liferayEnvironment, 15*time.Second,
		)
	}

	if liferayEnvironment.Status.ActivatedAt == nil {
		publicKey, error := publicKeyBase64(privateKey)

		if error != nil {
			return controllerruntime.Result{}, error
		}

		activationCode, error := liferayEnvironmentReconciler.readActivationCode(context, liferayEnvironment)

		if errors.IsNotFound(error) {
			logger.V(1).Info("Awaiting activation code", "environmentID", environmentID)

			meta.SetStatusCondition(
				&liferayEnvironment.Status.Conditions,
				metav1.Condition{
					Message: "Waiting for the activation code secret to be created",
					Reason:  "AwaitingActivationCode",
					Status:  metav1.ConditionFalse,
					Type:    conditionActivated,
				},
			)

			liferayEnvironment.Status.Phase = "Pending"

			return liferayEnvironmentReconciler.finishAfter(
				context, liferayEnvironment, 15*time.Second,
			)
		}

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

			liferayEnvironment.Status.ConsecutiveFailures++
			liferayEnvironment.Status.Phase = "Degraded"

			return liferayEnvironmentReconciler.finishWithBackoff(
				context, liferayEnvironment,
			)
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

		liferayEnvironment.Status.ConsecutiveFailures++

		if liferayEnvironment.Status.UnreachableSince == nil {
			unreachableSince := metav1.NewTime(time.Now())

			liferayEnvironment.Status.UnreachableSince = &unreachableSince
		}

		liferayEnvironment.Status.Phase = "Degraded"

		if error := liferayEnvironmentReconciler.enforceGracePeriod(
			context, liferayEnvironment,
		); error != nil {
			return controllerruntime.Result{}, error
		}

		return liferayEnvironmentReconciler.finishWithBackoff(
			context, liferayEnvironment,
		)
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

	liferayEnvironment.Status.ConsecutiveFailures = 0

	liferayEnvironmentReconciler.clearUnreachable(context, liferayEnvironment)

	if error := liferayEnvironmentReconciler.persistEntitlementsSecret(context, entitlements, liferayEnvironment); error != nil {
		return controllerruntime.Result{}, error
	}

	now := metav1.Now()

	liferayEnvironment.Status.License.Checksum = licenseChecksum(entitlements.LicenseXML)
	liferayEnvironment.Status.License.LastVerified = &now
	liferayEnvironment.Status.License.MaxClusterNodes = entitlements.MaxClusterNodes

	expirationDate, error := license.ExpirationDate(entitlements.LicenseXML)

	if error != nil {
		logger.Error(error, "License validation failed", "environmentID", environmentID)

		liferayEnvironment.Status.License.ValidUntil = nil

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: error.Error(),
				Reason:  "Invalid",
				Status:  metav1.ConditionFalse,
				Type:    conditionLicenseValid,
			},
		)

		liferayEnvironment.Status.Phase = "Degraded"

		return liferayEnvironmentReconciler.finishAfter(
			context, liferayEnvironment, liferayEnvironmentReconciler.HeartbeatInterval,
		)
	}

	validUntil := metav1.NewTime(expirationDate)

	liferayEnvironment.Status.License.ValidUntil = &validUntil

	if now.After(expirationDate) {
		logger.Info(
			"License expired",
			"environmentID", environmentID,
			"expirationDate", expirationDate,
		)

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: fmt.Sprintf(
					"License expired on %s.", expirationDate.Format(time.RFC3339),
				),
				Reason: "Expired",
				Status: metav1.ConditionFalse,
				Type:   conditionLicenseValid,
			},
		)

		liferayEnvironment.Status.Phase = "Degraded"

		return liferayEnvironmentReconciler.finishAfter(
			context, liferayEnvironment, liferayEnvironmentReconciler.HeartbeatInterval,
		)
	}

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "Valid",
			Status: metav1.ConditionTrue,
			Type:   conditionLicenseValid,
		},
	)

	if error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, entitlements.MaxClusterNodes,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	liferayEnvironment.Status.Phase = "Ready"

	return liferayEnvironmentReconciler.finishAfter(
		context, liferayEnvironment, liferayEnvironmentReconciler.HeartbeatInterval,
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) SetupWithManager(
	manager controllerruntime.Manager,
) error {
	return controllerruntime.NewControllerManagedBy(
		manager,
	).For(
		&licensingv1alpha1.LiferayEnvironment{},
		builder.WithPredicates(
			predicate.Or(
				predicate.AnnotationChangedPredicate{},
				predicate.GenerationChangedPredicate{},
			),
		),
	).Named(
		"liferayenvironment",
	).Owns(
		&corev1.Secret{},
	).Complete(
		liferayEnvironmentReconciler,
	)
}

func backoffDuration(
	consecutiveFailures int32,
	retryInitialDelay time.Duration,
	retryMaxDelay time.Duration,
) time.Duration {
	backoff := float64(retryInitialDelay) * math.Pow(2, float64(max(consecutiveFailures-1, 0)))

	if backoff >= float64(retryMaxDelay) {
		return retryMaxDelay
	}

	return time.Duration(backoff)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) clearUnreachable(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) {
	if liferayEnvironment.Status.UnreachableSince == nil {
		return
	}

	if meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionGracePeriodExpired,
	) {
		logf.FromContext(context).Info(
			"Provisioning recovered; restoring the licensed replica ceiling",
			"environmentID", liferayEnvironment.Status.EnvironmentID,
		)

		liferayEnvironmentReconciler.Recorder.Event(
			liferayEnvironment,
			corev1.EventTypeNormal,
			"ProvisioningRecovered",
			"Provisioning is reachable again; the licensed replica ceiling was restored.",
		)
	}

	meta.RemoveStatusCondition(
		&liferayEnvironment.Status.Conditions, conditionGracePeriodExpired,
	)

	liferayEnvironment.Status.UnreachableSince = nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceGracePeriod(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) error {
	if liferayEnvironment.Status.UnreachableSince == nil {
		return nil
	}

	elapsed := time.Since(liferayEnvironment.Status.UnreachableSince.Time)

	if elapsed < liferayEnvironmentReconciler.GracePeriod {
		return nil
	}

	if error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, gracePeriodReplicaCeiling,
	); error != nil {
		return error
	}

	message := fmt.Sprintf(
		"Provisioning has been unreachable since %s; scaled %q down to %d replica.",
		liferayEnvironment.Status.UnreachableSince.Format(time.RFC3339),
		liferayEnvironment.Spec.WorkloadRef.Name,
		gracePeriodReplicaCeiling,
	)

	if !meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionGracePeriodExpired,
	) {
		logf.FromContext(context).Error(
			nil, message, "environmentID", liferayEnvironment.Status.EnvironmentID,
		)

		liferayEnvironmentReconciler.Recorder.Event(
			liferayEnvironment,
			corev1.EventTypeWarning,
			"GracePeriodExpired",
			message,
		)
	}

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Message: message,
			Reason:  "ProvisioningUnreachable",
			Status:  metav1.ConditionTrue,
			Type:    conditionGracePeriodExpired,
		},
	)

	return nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceReplicaCeiling(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	maxClusterNodes int32,
) error {
	logger := logf.FromContext(context)

	if maxClusterNodes <= 0 {
		liferayEnvironment.Status.EffectiveReplicas = nil

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: "The licensed maximum cluster node count is not yet known.",
				Reason:  "MaxClusterNodesUnknown",
				Status:  metav1.ConditionUnknown,
				Type:    conditionReplicasCountValid,
			},
		)

		return nil
	}

	statefulSet := &appsv1.StatefulSet{}

	getError := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{
			Name:      liferayEnvironment.Spec.WorkloadRef.Name,
			Namespace: liferayEnvironment.Namespace,
		}, statefulSet)

	if errors.IsNotFound(getError) {
		logger.V(1).Info(
			"Workload not found; skipping replica enforcement",
			"workload", liferayEnvironment.Spec.WorkloadRef.Name,
		)

		liferayEnvironment.Status.EffectiveReplicas = nil

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: fmt.Sprintf(
					"Workload StatefulSet %q was not found.",
					liferayEnvironment.Spec.WorkloadRef.Name,
				),
				Reason: "WorkloadNotFound",
				Status: metav1.ConditionUnknown,
				Type:   conditionReplicasCountValid,
			},
		)

		return nil
	}

	if getError != nil {
		return getError
	}

	desiredReplicas := resolveDesiredReplicas(liferayEnvironment, statefulSet)

	effectiveReplicas := min(desiredReplicas, maxClusterNodes)

	if statefulSet.Spec.Replicas == nil || *statefulSet.Spec.Replicas != effectiveReplicas {
		statefulSet.Spec.Replicas = &effectiveReplicas

		if error := liferayEnvironmentReconciler.Update(context, statefulSet); error != nil {
			return error
		}

		logger.Info(
			"Enforced licensed replica ceiling",
			"desiredReplicas", desiredReplicas,
			"effectiveReplicas", effectiveReplicas,
			"maxClusterNodes", maxClusterNodes,
			"workload", statefulSet.Name,
		)
	}

	liferayEnvironment.Status.EffectiveReplicas = &effectiveReplicas

	if desiredReplicas > maxClusterNodes {
		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: fmt.Sprintf(
					"Requested %d replicas exceeds the licensed maximum of %d; capping to %d.",
					desiredReplicas, maxClusterNodes, effectiveReplicas,
				),
				Reason: "ExceedsLicensedMaximum",
				Status: metav1.ConditionFalse,
				Type:   conditionReplicasCountValid,
			},
		)

		return nil
	}

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "WithinLicensedLimit",
			Status: metav1.ConditionTrue,
			Type:   conditionReplicasCountValid,
		},
	)

	return nil
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) ensureNamespaceEnvironmentLabel(
	context context.Context,
	namespaceName string,
) error {
	namespace := &corev1.Namespace{}

	if error := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{Name: namespaceName}, namespace,
	); error != nil {
		return error
	}

	if namespace.Labels[environmentLabel] == "true" {
		return nil
	}

	if namespace.Labels == nil {
		namespace.Labels = map[string]string{}
	}

	namespace.Labels[environmentLabel] = "true"

	logf.FromContext(context).Info(
		"Labeled namespace as a licensed environment", "namespace", namespaceName,
	)

	return liferayEnvironmentReconciler.Update(context, namespace)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) finishAfter(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	requeueAfter time.Duration,
) (controllerruntime.Result, error) {
	if error := liferayEnvironmentReconciler.Status().Update(context, liferayEnvironment); error != nil {
		if errors.IsConflict(error) {
			return controllerruntime.Result{RequeueAfter: time.Second}, nil
		}

		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{RequeueAfter: requeueAfter}, nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) finishWithBackoff(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (controllerruntime.Result, error) {
	return liferayEnvironmentReconciler.finishAfter(
		context, liferayEnvironment, backoffDuration(
			liferayEnvironment.Status.ConsecutiveFailures,
			liferayEnvironmentReconciler.RetryInitialDelay,
			liferayEnvironmentReconciler.RetryMaxDelay,
		),
	)
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) persistOfflineRequest(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	payload string,
) error {
	identityName := liferayEnvironment.Name + identitySecretSuffix

	secret := &corev1.Secret{}

	if error := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{
			Name:      identityName,
			Namespace: liferayEnvironment.Namespace,
		}, secret); error != nil {
		return error
	}

	if bytes.Equal(secret.Data["offline-request"], []byte(payload)) {
		return nil
	}

	if secret.Data == nil {
		secret.Data = map[string][]byte{}
	}

	secret.Data["offline-request"] = []byte(payload)

	if error := liferayEnvironmentReconciler.Update(context, secret); error != nil {
		return error
	}

	logf.FromContext(context).Info(
		"Stored offline request in identity secret", "secret", identityName,
	)

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

func resolveDesiredReplicas(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	statefulSet *appsv1.StatefulSet,
) int32 {
	if liferayEnvironment.Spec.DesiredReplicas != nil {
		return *liferayEnvironment.Spec.DesiredReplicas
	}

	if statefulSet.Spec.Replicas != nil {
		return *statefulSet.Spec.Replicas
	}

	return 1
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

	GracePeriod       time.Duration
	HeartbeatInterval time.Duration
	Provisioning      provisioning.Client
	Recorder          record.EventRecorder
	RetryInitialDelay time.Duration
	RetryMaxDelay     time.Duration
}
