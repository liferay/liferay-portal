// +kubebuilder:rbac:groups="",resources=persistentvolumeclaims,verbs=create;get;list;watch
// +kubebuilder:rbac:groups=apps,resources=statefulsets,verbs=get;list;watch
package liferay

import (
	"context"
	"fmt"

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
	logf "sigs.k8s.io/controller-runtime/pkg/log"
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
	logger := logf.FromContext(context)

	statefulSet := &appsv1.StatefulSet{}

	if error := liferayStatefulSetReconciler.Get(context, request.NamespacedName, statefulSet); error != nil {
		if !errors.IsNotFound(error) {
			logger.Error(error, "Unable to get StatefulSet")

			return controllerruntime.Result{}, error
		}

		logger.Info("Skipping StatefulSet that no longer exists")

		return controllerruntime.Result{}, nil
	}

	logger.Info("Reconciling Liferay StatefulSet")

	liferayEnvironment, error := liferayStatefulSetReconciler.getLiferayEnvironmentByStatefulSet(context, statefulSet)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if liferayEnvironment == nil {
		logger.Info("Skipping StatefulSet with no LiferayEnvironment")

		return controllerruntime.Result{}, nil
	}

	marketplaceVolume := liferayEnvironment.Spec.MarketplaceVolume

	if marketplaceVolume == nil || !marketplaceVolume.Enabled {
		logger.Info(
			"Skipping StatefulSet with no marketplace volume enabled",
			"liferayEnvironment", liferayEnvironment.Name,
		)

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
	logger := logf.FromContext(context)

	error := liferayStatefulSetReconciler.Get(
		context,
		client.ObjectKeyFromObject(persistentVolumeClaim),
		&corev1.PersistentVolumeClaim{},
	)

	if error == nil {
		logger.Info(
			"Marketplace volume claim already exists",
			"persistentVolumeClaim", persistentVolumeClaim.Name,
		)

		return nil
	}

	if !errors.IsNotFound(error) {
		logger.Error(
			error,
			"Unable to get marketplace volume claim",
			"persistentVolumeClaim", persistentVolumeClaim.Name,
		)

		return error
	}

	if error := controllerruntime.SetControllerReference(
		statefulSet,
		persistentVolumeClaim,
		liferayStatefulSetReconciler.Scheme(),
	); error != nil {
		logger.Error(
			error,
			"Unable to set the StatefulSet as the owner of the marketplace volume claim",
			"persistentVolumeClaim", persistentVolumeClaim.Name,
			"statefulSet", statefulSet.Name,
		)

		return error
	}

	if error := liferayStatefulSetReconciler.Create(
		context,
		persistentVolumeClaim,
	); error != nil {
		if !errors.IsAlreadyExists(error) {
			logger.Error(
				error,
				"Unable to create marketplace volume claim",
				"persistentVolumeClaim", persistentVolumeClaim.Name,
			)

			return error
		}

		logger.Info(
			"Marketplace volume claim was created concurrently",
			"persistentVolumeClaim", persistentVolumeClaim.Name,
		)

		return nil
	}

	storageClassName := ""

	if persistentVolumeClaim.Spec.StorageClassName != nil {
		storageClassName = *persistentVolumeClaim.Spec.StorageClassName
	}

	logger.Info(
		"Created marketplace volume claim",
		"persistentVolumeClaim", persistentVolumeClaim.Name,
		"size", persistentVolumeClaim.Spec.Resources.Requests.Storage().String(),
		"storageClassName", storageClassName,
	)

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
		logf.FromContext(context).Error(
			error,
			"Unable to list LiferayEnvironments",
			"namespace", statefulSet.Namespace,
		)

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
	logger := logf.FromContext(context)

	liferayEnvironment, ok := object.(*licensingv1alpha1.LiferayEnvironment)

	if !ok {
		logger.Error(
			fmt.Errorf("unexpected object type %T", object),
			"Unable to map watched object to a StatefulSet",
			"object", client.ObjectKeyFromObject(object),
		)

		return nil
	}

	workloadName := liferayEnvironment.Spec.WorkloadRef.Name

	logger.Info(
		"Enqueuing StatefulSet referenced by LiferayEnvironment",
		"liferayEnvironment", liferayEnvironment.Name,
		"namespace", liferayEnvironment.Namespace,
		"statefulSet", workloadName,
	)

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
