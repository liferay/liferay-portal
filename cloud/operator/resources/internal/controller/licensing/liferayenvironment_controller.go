// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments,verbs=get;list;watch
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/finalizers,verbs=update
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/status,verbs=get;update
package licensing

import (
	"context"
	"fmt"
	"path/filepath"
	"strings"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	backoff "github.com/liferay/liferay-portal/cloud/operator/internal/backoff"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	autoscalingv2 "k8s.io/api/autoscaling/v2"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	schema "k8s.io/apimachinery/pkg/runtime/schema"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	builder "sigs.k8s.io/controller-runtime/pkg/builder"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	controllerutil "sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	handler "sigs.k8s.io/controller-runtime/pkg/handler"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
	predicate "sigs.k8s.io/controller-runtime/pkg/predicate"
)

const (
	activationPollInterval                   = 15 * time.Second
	conditionActivated                       = "Activated"
	conditionAddOnsReady                     = "AddOnsReady"
	conditionLegacyGracePeriodExpired        = "GracePeriodExpired"
	environmentLabel                         = "licensing.liferay.com/environment"
	fieldOwner                               = "liferay-dxp-operator"
	phaseDegraded                            = "Degraded"
	phasePending                             = "Pending"
	phaseReady                               = "Ready"
	scaledObjectCustomResourceDefinitionName = "scaledobjects.keda.sh"
)

// +kubebuilder:rbac:groups="",resources=events,verbs=create;patch
// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;update;watch
// +kubebuilder:rbac:groups="",resources=secrets,verbs=create;get;list;update;watch
// +kubebuilder:rbac:groups=apiextensions.k8s.io,resources=customresourcedefinitions,verbs=get;list;watch
// +kubebuilder:rbac:groups=apps,resources=deployments,verbs=get;list;update;watch
// +kubebuilder:rbac:groups=apps,resources=statefulsets,verbs=get;list;update;watch
// +kubebuilder:rbac:groups=autoscaling,resources=horizontalpodautoscalers,verbs=get;list;patch;watch
// +kubebuilder:rbac:groups=keda.sh,resources=scaledobjects,verbs=get;list;patch;watch
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

	if error := liferayEnvironmentReconciler.ensureDeploySyncSecret(
		context, liferayEnvironment,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	meta.RemoveStatusCondition(
		&liferayEnvironment.Status.Conditions, conditionLegacyGracePeriodExpired,
	)

	environmentID, error := liferayEnvironmentReconciler.labelNamespaceAndGetEnvironmentID(
		context, liferayEnvironment.Namespace,
	)

	if error != nil {
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
	var stepOutcome stepOutcome

	if liferayEnvironment.Spec.Offline {
		entitlements, stepOutcome, error = liferayEnvironmentReconciler.handleOfflineActivation(
			context, environmentID, liferayEnvironment, privateKey,
		)
	} else {
		entitlements, stepOutcome, error = liferayEnvironmentReconciler.handleOnlineActivation(
			context, environmentID, liferayEnvironment, privateKey,
		)
	}

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if stepOutcome.stop {
		return liferayEnvironmentReconciler.finishAfter(
			context, liferayEnvironment, stepOutcome.requeueAfter,
		)
	}

	stepOutcome, error = liferayEnvironmentReconciler.enforceLicense(
		context,
		entitlements,
		environmentID,
		liferayEnvironment,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if stepOutcome.stop {
		return liferayEnvironmentReconciler.finishAfter(
			context, liferayEnvironment, stepOutcome.requeueAfter,
		)
	}

	cache := addon.NewFilesystemCache(
		liferayEnvironmentReconciler.getEnvironmentDir(liferayEnvironment.Namespace),
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

	liferayEnvironment.Status.Phase = phaseReady

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
	customResourceDefinition := &metav1.PartialObjectMetadata{}

	customResourceDefinition.SetGroupVersionKind(customResourceDefinitionGroupVersionKind)

	liferayEnvironmentController, error := controllerruntime.NewControllerManagedBy(
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
	).Watches(
		&autoscalingv2.HorizontalPodAutoscaler{},
		handler.EnqueueRequestsFromMapFunc(
			liferayEnvironmentReconciler.enqueueScaleTargetEnvironments,
		),
		builder.WithPredicates(predicate.GenerationChangedPredicate{}),
	).Watches(
		customResourceDefinition,
		handler.EnqueueRequestsFromMapFunc(
			liferayEnvironmentReconciler.ensureScaledObjectWatch,
		),
		builder.WithPredicates(
			predicate.NewPredicateFuncs(
				func(object client.Object) bool {
					return object.GetName() == scaledObjectCustomResourceDefinitionName
				},
			),
		),
	).Build(
		liferayEnvironmentReconciler,
	)

	if error != nil {
		return error
	}

	liferayEnvironmentReconciler.scaledObjectWatch = &scaledObjectWatch{
		controller: liferayEnvironmentController,
		manager:    manager,
	}

	return nil
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) ensureOwnedSecret(
	context context.Context,
	data map[string][]byte,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	name string,
) error {
	secret := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: liferayEnvironment.Namespace,
		},
	}

	operationResult, error := controllerutil.CreateOrUpdate(
		context, liferayEnvironmentReconciler.Client, secret,
		func() error {
			secret.Data = data

			if secret.Labels == nil {
				secret.Labels = map[string]string{}
			}

			secret.Labels["controller-watched"] = "yes"

			return controllerruntime.SetControllerReference(liferayEnvironment, secret, liferayEnvironmentReconciler.Scheme())
		},
	)

	if error != nil {
		return error
	}

	if operationResult != controllerutil.OperationResultNone {
		logf.FromContext(context).Info("Owned secret was written", "operation", operationResult, "secret", name)
	}

	return nil
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

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) getBackoffDelay(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) time.Duration {
	return backoff.Duration(
		liferayEnvironment.Status.ConsecutiveFailures,
		liferayEnvironmentReconciler.RetryInitialDelay,
		liferayEnvironmentReconciler.RetryMaxDelay,
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) getEnvironmentDir(
	namespace string,
) string {
	return filepath.Join(liferayEnvironmentReconciler.MarketplaceMountPath, namespace)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) labelNamespaceAndGetEnvironmentID(
	context context.Context,
	namespaceName string,
) (string, error) {
	namespace := &corev1.Namespace{}

	if error := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{Name: namespaceName}, namespace,
	); error != nil {
		return "", error
	}

	environmentID := string(namespace.UID)

	if namespace.Labels[environmentLabel] == "true" {
		return environmentID, nil
	}

	if namespace.Labels == nil {
		namespace.Labels = map[string]string{}
	}

	namespace.Labels[environmentLabel] = "true"

	logf.FromContext(context).Info(
		"Namespace was labeled as a licensed environment", "namespace", namespaceName,
	)

	if error := liferayEnvironmentReconciler.Update(context, namespace); error != nil {
		return "", error
	}

	return environmentID, nil
}

func markActivated(liferayEnvironment *licensingv1alpha1.LiferayEnvironment) {
	if liferayEnvironment.Status.ActivatedAt == nil {
		now := metav1.Now()

		liferayEnvironment.Status.ActivatedAt = &now
	}

	setCondition(conditionActivated, liferayEnvironment, "", "Activated", metav1.ConditionTrue)
}

func proceed() stepOutcome {
	return stepOutcome{}
}

func resolveEnvironmentName(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) string {
	if liferayEnvironment.Spec.EnvironmentName != "" {
		return liferayEnvironment.Spec.EnvironmentName
	}

	return liferayEnvironment.Namespace
}

func setCondition(
	conditionType string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	message string,
	reason string,
	status metav1.ConditionStatus,
) {
	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Message: message,
			Reason:  reason,
			Status:  status,
			Type:    conditionType,
		},
	)
}

func stopAfter(requeueAfter time.Duration) stepOutcome {
	return stepOutcome{
		requeueAfter: requeueAfter,
		stop:         true,
	}
}

type LiferayEnvironmentReconciler struct {
	client.Client

	ExpirationGracePeriod   time.Duration
	HeartbeatInterval       time.Duration
	MarketplaceMountPath    string
	Provisioning            provisioning.Client
	ProvisioningGracePeriod time.Duration
	Recorder                record.EventRecorder
	RetryInitialDelay       time.Duration
	RetryMaxDelay           time.Duration
	Syncer                  *addon.Syncer

	scaledObjectWatch *scaledObjectWatch
}

type stepOutcome struct {
	requeueAfter time.Duration
	stop         bool
}

var customResourceDefinitionGroupVersionKind = schema.GroupVersionKind{
	Group:   "apiextensions.k8s.io",
	Kind:    "CustomResourceDefinition",
	Version: "v1",
}
