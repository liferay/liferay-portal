// +kubebuilder:rbac:groups="",resources=persistentvolumeclaims,verbs=create;get;list;watch
// +kubebuilder:rbac:groups=apps,resources=statefulsets,verbs=get;list;watch
package liferay

import (
	context "context"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	marketplace "github.com/liferay/liferay-portal/cloud/operator/internal/controller/liferay/marketplace"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	builder "sigs.k8s.io/controller-runtime/pkg/builder"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	handler "sigs.k8s.io/controller-runtime/pkg/handler"
	predicate "sigs.k8s.io/controller-runtime/pkg/predicate"
	reconcile "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const (
	liferayComponentLabel = "component"
	liferayComponentValue = "liferay"
)

func (liferayStatefulSetReconciler *LiferayStatefulSetReconciler) Reconcile(
	context context.Context,
	request controllerruntime.Request,
) (controllerruntime.Result, error) {
	statefulSet := &appsv1.StatefulSet{}

	if error := liferayStatefulSetReconciler.Get(context, request.NamespacedName, statefulSet); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
	}

	liferayEnvironment, error := liferayStatefulSetReconciler.getLiferayEnvironmentByStatefulSet(context, statefulSet)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if liferayEnvironment == nil || liferayEnvironment.Spec.MarketplaceVolume == nil {
		return controllerruntime.Result{}, nil
	}

	if error := liferayStatefulSetReconciler.createVolumeClaimIfMissing(
		context,
		marketplace.GetVolumeClaim(liferayEnvironment),
		statefulSet,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{}, nil
}

func (liferayStatefulSetReconciler *LiferayStatefulSetReconciler) SetupWithManager(
	manager controllerruntime.Manager,
) error {
	statefulSetPredicate, error := getLiferayStatefulSetPredicate()

	if error != nil {
		return error
	}

	return controllerruntime.NewControllerManagedBy(
		manager,
	).For(
		&appsv1.StatefulSet{},
		builder.WithPredicates(statefulSetPredicate),
	).Named(
		"liferaystatefulset",
	).Owns(
		&corev1.PersistentVolumeClaim{},
	).Watches(
		&licensingv1alpha1.LiferayEnvironment{},
		handler.EnqueueRequestsFromMapFunc(mapLiferayEnvironmentToStatefulSet),
	).Complete(
		liferayStatefulSetReconciler,
	)
}

func (liferayStatefulSetReconciler *LiferayStatefulSetReconciler) createVolumeClaimIfMissing(
	context context.Context,
	persistentVolumeClaim *corev1.PersistentVolumeClaim,
	statefulSet *appsv1.StatefulSet,
) error {
	error := liferayStatefulSetReconciler.Get(
		context,
		client.ObjectKeyFromObject(persistentVolumeClaim),
		&corev1.PersistentVolumeClaim{},
	)

	if error == nil {
		return nil
	}

	if !errors.IsNotFound(error) {
		return error
	}

	if error := controllerruntime.SetControllerReference(
		statefulSet,
		persistentVolumeClaim,
		liferayStatefulSetReconciler.Scheme(),
	); error != nil {
		return error
	}

	if error := liferayStatefulSetReconciler.Create(
		context,
		persistentVolumeClaim,
	); error != nil && !errors.IsAlreadyExists(error) {
		return error
	}

	return nil
}

func (liferayStatefulSetReconciler *LiferayStatefulSetReconciler) getLiferayEnvironmentByStatefulSet(
	context context.Context,
	statefulSet *appsv1.StatefulSet,
) (*licensingv1alpha1.LiferayEnvironment, error) {
	liferayEnvironmentList := &licensingv1alpha1.LiferayEnvironmentList{}

	if error := liferayStatefulSetReconciler.List(
		context,
		liferayEnvironmentList,
		client.InNamespace(statefulSet.Namespace),
	); error != nil {
		return nil, error
	}

	for index, liferayEnvironment := range liferayEnvironmentList.Items {
		if liferayEnvironment.Spec.WorkloadRef.Name == statefulSet.Name {
			return &liferayEnvironmentList.Items[index], nil
		}
	}

	return nil, nil
}

func getLiferayStatefulSetPredicate() (predicate.Predicate, error) {
	return predicate.LabelSelectorPredicate(
		metav1.LabelSelector{
			MatchLabels: map[string]string{
				liferayComponentLabel: liferayComponentValue,
			},
		},
	)
}

func mapLiferayEnvironmentToStatefulSet(
	context context.Context,
	object client.Object,
) []reconcile.Request {
	liferayEnvironment, ok := object.(*licensingv1alpha1.LiferayEnvironment)

	if !ok {
		return nil
	}

	workloadName := liferayEnvironment.Spec.WorkloadRef.Name

	return []reconcile.Request{
		{
			NamespacedName: types.NamespacedName{
				Name:      workloadName,
				Namespace: liferayEnvironment.Namespace,
			},
		},
	}
}

type LiferayStatefulSetReconciler struct {
	client.Client
}
