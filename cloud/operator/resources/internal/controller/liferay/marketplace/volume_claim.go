package marketplace

import (
	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const claimNameSuffix = "-marketplace"

func GetVolumeClaim(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) *corev1.PersistentVolumeClaim {
	volumeSpec := liferayEnvironment.Spec.MarketplaceVolume

	storageClassName := volumeSpec.StorageClassName

	return &corev1.PersistentVolumeClaim{
		ObjectMeta: metav1.ObjectMeta{
			Name:      getVolumeClaimName(liferayEnvironment),
			Namespace: liferayEnvironment.Namespace,
		},
		Spec: corev1.PersistentVolumeClaimSpec{
			AccessModes: []corev1.PersistentVolumeAccessMode{
				corev1.ReadWriteMany,
			},
			Resources: corev1.VolumeResourceRequirements{
				Requests: corev1.ResourceList{
					corev1.ResourceStorage: volumeSpec.Size,
				},
			},
			StorageClassName: &storageClassName,
		},
	}
}

func getVolumeClaimName(liferayEnvironment *licensingv1alpha1.LiferayEnvironment) string {
	return liferayEnvironment.Spec.WorkloadRef.Name + claimNameSuffix
}
