// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments,verbs=get;list;patch;update;watch
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/finalizers,verbs=update
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/status,verbs=get;patch;update
package licensing

import (
	"context"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) Reconcile(
	context context.Context,
	request controllerruntime.Request,
) (controllerruntime.Result, error) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{}

	if error := liferayEnvironmentReconciler.Get(context, request.NamespacedName, liferayEnvironment); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
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
	).Complete(
		liferayEnvironmentReconciler,
	)
}

type LiferayEnvironmentReconciler struct {
	client.Client

	HeartbeatInterval time.Duration
}
