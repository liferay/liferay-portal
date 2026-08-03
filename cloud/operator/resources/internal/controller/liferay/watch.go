package liferay

import (
	"context"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	predicate "sigs.k8s.io/controller-runtime/pkg/predicate"
	reconcile "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const (
	liferayComponentLabel = "component"
	liferayComponentValue = "liferay"
)

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
