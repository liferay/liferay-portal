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
	"path/filepath"
	"strings"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	backoff "github.com/liferay/liferay-portal/cloud/operator/internal/backoff"
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
	conditionAddOnsReady           = "AddOnsReady"
	conditionGracePeriodExpired    = "GracePeriodExpired"
	conditionLicenseValid          = "LicenseValid"
	conditionProvisioningReachable = "ProvisioningReachable"
	conditionReplicasCountValid    = "ReplicasCountValid"
	entitlementsSecretSuffix       = "-entitlements"
	environmentLabel               = "licensing.liferay.com/environment"
	fieldOwner                     = "liferay-dxp-operator"
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
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{}

	if error := liferayEnvironmentReconciler.Get(
		context, request.NamespacedName, liferayEnvironment,
	); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
	}

	environmentID, error := liferayEnvironmentReconciler.resolveEnvironmentID(
		context, liferayEnvironment.Namespace,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if error := liferayEnvironmentReconciler.ensureNamespaceEnvironmentLabel(
		context, liferayEnvironment.Namespace,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	liferayEnvironment.Status.EnvironmentID = environmentID

	privateKey, error := liferayEnvironmentReconciler.ensureIdentity(
		context, liferayEnvironment,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	var entitlements *provisioning.Entitlements
	var result controllerruntime.Result

	if liferayEnvironment.Spec.Offline {
		entitlements, result, error = liferayEnvironmentReconciler.handleOfflineActivation(
			context, environmentID, liferayEnvironment, privateKey,
		)
	} else {
		entitlements, result, error = liferayEnvironmentReconciler.handleOnlineActivation(
			context, environmentID, liferayEnvironment, privateKey,
		)
	}

	if error != nil || !result.IsZero() {
		return result, error
	}

	result, error = liferayEnvironmentReconciler.enforceLicense(
		context,
		entitlements,
		environmentID,
		liferayEnvironment,
	)

	if error != nil || !result.IsZero() {
		return result, error
	}

	cache := addon.NewFilesystemCache(
		liferayEnvironmentReconciler.environmentDir(liferayEnvironment.Namespace),
	)

	apps := []licensingv1alpha1.AppStatus{}

	now := metav1.Now()

	requeueAfter := time.Duration(0)

	if liferayEnvironment.Spec.Offline {
		apps, error = liferayEnvironmentReconciler.extractOfflineAddOns(
			cache, context, entitlements, liferayEnvironment,
		)

		if error != nil {
			return controllerruntime.Result{}, error
		}
	} else {
		apps, requeueAfter = liferayEnvironmentReconciler.Syncer.Sync(
			addon.SyncRequest{
				AddOns:        entitlements.AddOns,
				Cache:         cache,
				Context:       context,
				Current:       liferayEnvironment.Status.Apps,
				EnvironmentID: environmentID,
				Namespace:     liferayEnvironment.Namespace,
				Now:           now,
				PrivateKey:    privateKey,
			},
		)
	}

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		addOnsReadyCondition(addon.Summarize(apps)),
	)

	liferayEnvironment.Status.Apps = apps

	liferayEnvironment.Status.Phase = "Ready"

	if requeueAfter == 0 {
		requeueAfter = liferayEnvironmentReconciler.HeartbeatInterval
	}

	return liferayEnvironmentReconciler.finishAfter(
		context, liferayEnvironment, requeueAfter,
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

func addOnsReadyCondition(summary addon.Summary) metav1.Condition {
	if len(summary.Failed) > 0 {
		names := make([]string, 0, len(summary.Failed))

		for _, name := range summary.Failed {
			names = append(names, fmt.Sprintf("%q", name))
		}

		return metav1.Condition{
			Message: fmt.Sprintf(
				"Unable to download %d of %d entitled add-ons: %s.",
				len(summary.Failed), summary.Entitled,
				strings.Join(names, ", "),
			),
			Reason: "DownloadsFailing",
			Status: metav1.ConditionFalse,
			Type:   conditionAddOnsReady,
		}
	}

	if summary.Pending > 0 {
		return metav1.Condition{
			Message: fmt.Sprintf(
				"Downloads are in progress for %d of %d entitled add-ons.",
				summary.Pending, summary.Entitled,
			),
			Reason: "Downloading",
			Status: metav1.ConditionFalse,
			Type:   conditionAddOnsReady,
		}
	}

	return metav1.Condition{
		Reason: "Downloaded",
		Status: metav1.ConditionTrue,
		Type:   conditionAddOnsReady,
	}
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

	if _, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceLicense(
	context context.Context,
	entitlements *provisioning.Entitlements,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (controllerruntime.Result, error) {
	logger := logf.FromContext(context)

	now := metav1.Now()

	liferayEnvironment.Status.License.Checksum = licenseChecksum(entitlements.LicenseXML)
	liferayEnvironment.Status.License.LastVerified = &now

	owner, error := license.Owner(entitlements.LicenseXML)

	if error != nil {
		logger.Error(error, "License validation failed", "environmentID", environmentID)

		liferayEnvironment.Status.License.MaxClusterNodes = nil
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

	if owner != environmentID {
		logger.Info(
			"License was issued for a different environment",
			"environmentID", environmentID, "owner", owner,
		)

		blocked := int32(0)

		liferayEnvironment.Status.License.MaxClusterNodes = &blocked
		liferayEnvironment.Status.License.ValidUntil = nil

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: fmt.Sprintf(
					"License owner %q does not match this environment %q.",
					owner, environmentID,
				),
				Reason: "EnvironmentMismatch",
				Status: metav1.ConditionFalse,
				Type:   conditionLicenseValid,
			},
		)

		liferayEnvironment.Status.Phase = "Degraded"

		requeueAfter, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
			context, liferayEnvironment, 0,
		)

		if error != nil {
			return controllerruntime.Result{}, error
		}

		if requeueAfter == 0 {
			requeueAfter = liferayEnvironmentReconciler.HeartbeatInterval
		}

		return liferayEnvironmentReconciler.finishAfter(
			context, liferayEnvironment, requeueAfter,
		)
	}

	maxClusterNodes := entitlements.MaxClusterNodes

	liferayEnvironment.Status.License.MaxClusterNodes = &maxClusterNodes

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

	requeueAfter, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, entitlements.MaxClusterNodes,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{RequeueAfter: requeueAfter}, nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceReplicaCeiling(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	maxClusterNodes int32,
) (time.Duration, error) {
	logger := logf.FromContext(context)

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
					"Workload StatefulSet %q does not exist.",
					liferayEnvironment.Spec.WorkloadRef.Name,
				),
				Reason: "WorkloadNotFound",
				Status: metav1.ConditionUnknown,
				Type:   conditionReplicasCountValid,
			},
		)

		return 0, nil
	}

	if getError != nil {
		return 0, getError
	}

	desiredReplicas := resolveDesiredReplicas(liferayEnvironment, statefulSet)

	effectiveReplicas := min(desiredReplicas, maxClusterNodes)

	if statefulSet.Spec.Replicas == nil || *statefulSet.Spec.Replicas != effectiveReplicas {
		liveReplicas := statefulSet.Spec.Replicas

		if error := liferayEnvironmentReconciler.Status().Update(context, liferayEnvironment); error != nil {
			return 0, error
		}

		statefulSet.Spec.Replicas = &effectiveReplicas

		if error := liferayEnvironmentReconciler.Update(
			context, statefulSet, client.FieldOwner(fieldOwner),
		); error != nil {
			logger.Error(
				error, "Unable to enforce the licensed replica ceiling",
				"effectiveReplicas", effectiveReplicas,
				"workload", statefulSet.Name,
			)

			liferayEnvironment.Status.EffectiveReplicas = liveReplicas

			meta.SetStatusCondition(
				&liferayEnvironment.Status.Conditions,
				metav1.Condition{
					Message: fmt.Sprintf(
						"Unable to scale StatefulSet %q to %d replicas: %s.",
						statefulSet.Name, effectiveReplicas, error,
					),
					Reason: "WorkloadUpdateRejected",
					Status: metav1.ConditionFalse,
					Type:   conditionReplicasCountValid,
				},
			)

			if error := liferayEnvironmentReconciler.Status().Update(context, liferayEnvironment); error != nil {
				return 0, error
			}

			return liferayEnvironmentReconciler.RetryInitialDelay, nil
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

		return 0, nil
	}

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "WithinLicensedLimit",
			Status: metav1.ConditionTrue,
			Type:   conditionReplicasCountValid,
		},
	)

	return 0, nil
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) environmentDir(
	namespace string,
) string {
	return filepath.Join(liferayEnvironmentReconciler.MarketplaceMountPath, namespace)
}

func extractLiferayImageTag(image string) string {
	if index := strings.LastIndex(image, "@"); index != -1 {
		image = image[:index]
	}

	index := strings.LastIndex(image, ":")

	if index == -1 {
		return ""
	}

	repository := image[:index]

	if !strings.HasSuffix(repository, "liferay/dxp") {
		return ""
	}

	tag := image[index+1:]

	if strings.Contains(tag, "/") {
		return ""
	}

	return tag
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
		context, liferayEnvironment, backoff.Duration(
			liferayEnvironment.Status.ConsecutiveFailures,
			liferayEnvironmentReconciler.RetryInitialDelay,
			liferayEnvironmentReconciler.RetryMaxDelay,
		),
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) handleOnlineActivation(
	context context.Context,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	privateKey *rsa.PrivateKey,
) (*provisioning.Entitlements, controllerruntime.Result, error) {
	logger := logf.FromContext(context)

	if liferayEnvironment.Status.ActivatedAt == nil {
		publicKey, error := publicKeyBase64(privateKey)

		if error != nil {
			return nil, controllerruntime.Result{}, error
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

			result, error := liferayEnvironmentReconciler.finishAfter(
				context, liferayEnvironment, 15*time.Second,
			)

			return nil, result, error
		}

		if error != nil {
			return nil, controllerruntime.Result{}, error
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

			result, error := liferayEnvironmentReconciler.finishWithBackoff(
				context, liferayEnvironment,
			)

			return nil, result, error
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
			DxpVersion:    liferayEnvironmentReconciler.resolveDxpVersion(context, liferayEnvironment),
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
			return nil, controllerruntime.Result{}, error
		}

		result, error := liferayEnvironmentReconciler.finishWithBackoff(
			context, liferayEnvironment,
		)

		return nil, result, error
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

	if error := liferayEnvironmentReconciler.persistEntitlementsSecret(
		context, entitlements, liferayEnvironment); error != nil {
		return nil, controllerruntime.Result{}, error
	}

	return entitlements, controllerruntime.Result{}, nil
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
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) string {
	if liferayEnvironment.Spec.DxpVersion != "" {
		return liferayEnvironment.Spec.DxpVersion
	}

	logger := logf.FromContext(context)

	statefulSet := &appsv1.StatefulSet{}

	if error := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{
			Name:      liferayEnvironment.Spec.WorkloadRef.Name,
			Namespace: liferayEnvironment.Namespace,
		}, statefulSet); error != nil {
		logger.V(1).Info(
			"Unable to read the workload to determine the DXP version",
			"workload", liferayEnvironment.Spec.WorkloadRef.Name,
		)

		return ""
	}

	for _, container := range statefulSet.Spec.Template.Spec.Containers {
		dxpVersion := extractLiferayImageTag(container.Image)

		if dxpVersion != "" {
			return dxpVersion
		}
	}

	logger.V(1).Info(
		"The workload carries no image tag to determine the DXP version from",
		"workload", liferayEnvironment.Spec.WorkloadRef.Name,
	)

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

	GracePeriod          time.Duration
	HeartbeatInterval    time.Duration
	MarketplaceMountPath string
	Provisioning         provisioning.Client
	Recorder             record.EventRecorder
	RetryInitialDelay    time.Duration
	RetryMaxDelay        time.Duration
	Syncer               *addon.Syncer
}
