// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments,verbs=get;list;watch;update;patch
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/finalizers,verbs=update
// +kubebuilder:rbac:groups=licensing.liferay.com,resources=liferayenvironments/status,verbs=get;update;patch
package licensing

import (
	"context"
	"time"

	apierrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	ctrl "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
)

type LiferayEnvironmentReconciler struct {
	client.Client

	HeartbeatInterval time.Duration
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) Reconcile(
	context context.Context,
	request ctrl.Request,
) (ctrl.Result, error) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{}

	if error := liferayEnvironmentReconciler.Get(context, request.NamespacedName, liferayEnvironment); error != nil {
		return ctrl.Result{}, client.IgnoreNotFound(error)
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
		if apierrors.IsConflict(error) {
			return ctrl.Result{RequeueAfter: time.Second}, nil
		}

		return ctrl.Result{}, error
	}

	return ctrl.Result{RequeueAfter: liferayEnvironmentReconciler.HeartbeatInterval}, nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) SetupWithManager(manager ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(
		manager,
	).For(
		&licensingv1alpha1.LiferayEnvironment{},
	).Named(
		"liferayenvironment",
	).Complete(
		liferayEnvironmentReconciler,
	)
}
