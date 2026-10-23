package licensing

import (
	"context"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	types "k8s.io/apimachinery/pkg/types"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

func getPodTemplate(workload client.Object) *corev1.PodTemplateSpec {
	if deployment, ok := workload.(*appsv1.Deployment); ok {
		return &deployment.Spec.Template
	}

	if statefulSet, ok := workload.(*appsv1.StatefulSet); ok {
		return &statefulSet.Spec.Template
	}

	return nil
}

func getReplicas(workload client.Object) *int32 {
	if deployment, ok := workload.(*appsv1.Deployment); ok {
		return deployment.Spec.Replicas
	}

	if statefulSet, ok := workload.(*appsv1.StatefulSet); ok {
		return statefulSet.Spec.Replicas
	}

	return nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) getWorkload(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (client.Object, error) {
	workload := liferayEnvironment.Spec.WorkloadRef.NewObject()

	if error := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{
			Name:      liferayEnvironment.Spec.WorkloadRef.Name,
			Namespace: liferayEnvironment.Namespace,
		}, workload); error != nil {
		return nil, error
	}

	return workload, nil
}

func setReplicas(replicas *int32, workload client.Object) {
	if deployment, ok := workload.(*appsv1.Deployment); ok {
		deployment.Spec.Replicas = replicas
	}

	if statefulSet, ok := workload.(*appsv1.StatefulSet); ok {
		statefulSet.Spec.Replicas = replicas
	}
}
