package marketplace

import (
	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	corev1 "k8s.io/api/core/v1"
)

func SetVolumeStatus(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	persistentVolumeClaim *corev1.PersistentVolumeClaim,
) {
	volumeStatus := &liferayEnvironment.Status.MarketplaceVolume

	volumeStatus.ClaimName = persistentVolumeClaim.Name

	if persistentVolumeClaim.Status.Phase != "" {
		volumeStatus.Phase = string(persistentVolumeClaim.Status.Phase)
	}
}
