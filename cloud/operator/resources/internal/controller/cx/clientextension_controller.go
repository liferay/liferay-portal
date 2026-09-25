package cx

import (
	"context"
	"fmt"
	"slices"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	equality "k8s.io/apimachinery/pkg/api/equality"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	builder "sigs.k8s.io/controller-runtime/pkg/builder"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	handler "sigs.k8s.io/controller-runtime/pkg/handler"
	predicate "sigs.k8s.io/controller-runtime/pkg/predicate"
	reconcile "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const (
	ReasonDxpNamespaceNotFound  = "DxpNamespaceNotFound"
	ReasonNamespaceNotPermitted = "NamespaceNotPermitted"
	ReasonNamespacePermitted    = "NamespacePermitted"
	ReasonStepNotEvaluated      = "StepNotEvaluated"
	ReasonStepsComplete         = "StepsComplete"
)

// +kubebuilder:rbac:groups="",resources=events,verbs=create;patch
// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;watch
// +kubebuilder:rbac:groups=cx.liferay.com,resources=clientextensions,verbs=get;list;watch
// +kubebuilder:rbac:groups=cx.liferay.com,resources=clientextensions/status,verbs=get;patch;update
func (clientExtensionReconciler *ClientExtensionReconciler) Reconcile(
	context context.Context,
	request controllerruntime.Request,
) (controllerruntime.Result, error) {
	var clientExtension cxv1alpha1.ClientExtension

	if error := clientExtensionReconciler.Get(
		context, request.NamespacedName, &clientExtension,
	); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
	}

	dxpNamespace, reason, error := clientExtensionReconciler.resolveDxpNamespace(
		&clientExtension, context,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if reason != "" {
		return controllerruntime.Result{}, clientExtensionReconciler.updateStatus(
			&clientExtension, metav1.ConditionFalse, context,
			refusalMessage(&clientExtension, dxpNamespace, reason), reason,
		)
	}

	return controllerruntime.Result{}, clientExtensionReconciler.updateStatus(
		&clientExtension, metav1.ConditionUnknown, context,
		fmt.Sprintf(
			"Namespace %q accepts client extensions from namespace %q.",
			dxpNamespace, clientExtension.Namespace,
		),
		ReasonNamespacePermitted,
	)
}

func (clientExtensionReconciler *ClientExtensionReconciler) SetupWithManager(
	manager controllerruntime.Manager,
) error {
	return controllerruntime.NewControllerManagedBy(
		manager,
	).For(
		&cxv1alpha1.ClientExtension{},
		builder.WithPredicates(predicate.GenerationChangedPredicate{}),
	).Named(
		"clientextension",
	).Watches(
		&corev1.Namespace{},
		handler.EnqueueRequestsFromMapFunc(clientExtensionReconciler.requestsForNamespace),
	).Complete(
		clientExtensionReconciler,
	)
}

func notReadyCondition(
	conditions []metav1.Condition,
	conditionTypes []string,
) *metav1.Condition {
	var notReady *metav1.Condition

	for _, conditionType := range conditionTypes {
		condition := meta.FindStatusCondition(conditions, conditionType)

		if condition == nil {
			condition = &metav1.Condition{
				Message: fmt.Sprintf("The %s step has not been evaluated.", conditionType),
				Reason:  ReasonStepNotEvaluated,
				Status:  metav1.ConditionUnknown,
			}
		}

		if condition.Status == metav1.ConditionFalse {
			return condition
		}

		if (condition.Status != metav1.ConditionTrue) && (notReady == nil) {
			notReady = condition
		}
	}

	return notReady
}

func refusalMessage(
	clientExtension *cxv1alpha1.ClientExtension, dxpNamespace string, reason string,
) string {
	if reason == ReasonDxpNamespaceNotFound {
		return fmt.Sprintf(
			"Namespace %q does not exist, so there is no DXP to deliver to. Set spec.dxpNamespace to the namespace DXP runs in.",
			dxpNamespace,
		)
	}

	return fmt.Sprintf(
		"Namespace %q does not list %q in its %q annotation, so this client extension is not delivered there. Whoever manages DXP grants access by adding %q to that annotation.",
		dxpNamespace, clientExtension.Namespace,
		cxv1alpha1.AnnotationAllowedClientExtensionNamespaces, clientExtension.Namespace,
	)
}

func (clientExtensionReconciler *ClientExtensionReconciler) requestsForNamespace(
	context context.Context,
	object client.Object,
) []reconcile.Request {
	var clientExtensionList cxv1alpha1.ClientExtensionList

	if error := clientExtensionReconciler.List(context, &clientExtensionList); error != nil {
		controllerruntime.LoggerFrom(context).Error(
			error, "Unable to list client extensions", "namespace", object.GetName(),
		)

		return nil
	}

	var requests []reconcile.Request

	for index := range clientExtensionList.Items {
		clientExtension := &clientExtensionList.Items[index]

		if clientExtension.Namespace == object.GetName() {
			continue
		}

		if DxpNamespace(clientExtension) != object.GetName() {
			continue
		}

		requests = append(requests, reconcile.Request{
			NamespacedName: client.ObjectKeyFromObject(clientExtension),
		})
	}

	return requests
}

func (clientExtensionReconciler *ClientExtensionReconciler) resolveDxpNamespace(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
) (string, string, error) {
	dxpNamespace := DxpNamespace(clientExtension)

	if dxpNamespace == clientExtension.Namespace {
		return dxpNamespace, "", nil
	}

	var namespace corev1.Namespace

	error := clientExtensionReconciler.Get(
		context, types.NamespacedName{Name: dxpNamespace}, &namespace,
	)

	if apierrors.IsNotFound(error) {
		return dxpNamespace, ReasonDxpNamespaceNotFound, nil
	}

	if error != nil {
		return "", "", error
	}

	if !slices.Contains(PermittedNamespaces(&namespace), clientExtension.Namespace) {
		return dxpNamespace, ReasonNamespaceNotPermitted, nil
	}

	return dxpNamespace, "", nil
}

func summarize(
	conditionTypes []string,
	generation int64,
	status *cxv1alpha1.ClientExtensionStatus,
) {
	ready := metav1.Condition{
		Message:            "Every step is complete.",
		ObservedGeneration: generation,
		Reason:             ReasonStepsComplete,
		Status:             metav1.ConditionTrue,
		Type:               cxv1alpha1.ConditionReady,
	}

	status.Phase = cxv1alpha1.PhaseReady

	if notReady := notReadyCondition(status.Conditions, conditionTypes); notReady != nil {
		ready.Message = notReady.Message
		ready.Reason = notReady.Reason
		ready.Status = notReady.Status

		status.Phase = cxv1alpha1.PhasePending

		if notReady.Status == metav1.ConditionFalse {
			status.Phase = cxv1alpha1.PhaseDegraded
		}
	}

	meta.SetStatusCondition(&status.Conditions, ready)
}

func (clientExtensionReconciler *ClientExtensionReconciler) updateStatus(
	clientExtension *cxv1alpha1.ClientExtension,
	conditionStatus metav1.ConditionStatus,
	context context.Context,
	message string,
	reason string,
) error {
	status := clientExtension.Status.DeepCopy()

	meta.SetStatusCondition(&status.Conditions, metav1.Condition{
		Message:            message,
		ObservedGeneration: clientExtension.Generation,
		Reason:             reason,
		Status:             conditionStatus,
		Type:               cxv1alpha1.ConditionDelivered,
	})

	summarize(stepConditionTypes, clientExtension.Generation, status)

	status.ObservedGeneration = clientExtension.Generation

	if equality.Semantic.DeepEqual(status, &clientExtension.Status) {
		return nil
	}

	clientExtension.Status = *status

	if error := clientExtensionReconciler.Status().Update(context, clientExtension); error != nil {
		return client.IgnoreNotFound(error)
	}

	if (status.Phase == cxv1alpha1.PhaseDegraded) && (clientExtensionReconciler.Recorder != nil) {
		clientExtensionReconciler.Recorder.Event(clientExtension, corev1.EventTypeWarning, reason, message)
	}

	return nil
}

type ClientExtensionReconciler struct {
	client.Client

	Recorder record.EventRecorder
}

var stepConditionTypes = []string{cxv1alpha1.ConditionDelivered}
