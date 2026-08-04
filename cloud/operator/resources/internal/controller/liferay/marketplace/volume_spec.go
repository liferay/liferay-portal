package marketplace

import (
	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	"github.com/liferay/liferay-portal/cloud/operator/internal/utils/persistentvolumeclaim"
	corev1 "k8s.io/api/core/v1"
)

const claimNameSuffix = "-marketplace"

func GetVolumeClaimSpec(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) persistentvolumeclaim.Spec {
	volumeSpec := liferayEnvironment.Spec.MarketplaceVolume

	return persistentvolumeclaim.Spec{
		AccessModes: []corev1.PersistentVolumeAccessMode{
			corev1.ReadWriteMany,
		},
		Name:             getVolumeClaimName(liferayEnvironment),
		Namespace:        liferayEnvironment.Namespace,
		Size:             volumeSpec.Size,
		StorageClassName: volumeSpec.StorageClassName,
	}
}

func getVolumeClaimName(liferayEnvironment *licensingv1alpha1.LiferayEnvironment) string {
	if liferayEnvironment.Spec.MarketplaceVolume.ClaimName != "" {
		return liferayEnvironment.Spec.MarketplaceVolume.ClaimName
	}

	return liferayEnvironment.Spec.WorkloadRef.Name + claimNameSuffix
}
