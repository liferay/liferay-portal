package marketplace

import (
	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	"github.com/liferay/liferay-portal/cloud/operator/internal/utils/persistentvolumeclaim"
	appsv1 "k8s.io/api/apps/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func (volumeManager *MarketplaceVolumeManager) GetVolumeConditions(
	claimResult persistentvolumeclaim.Result,
	statefulSet *appsv1.StatefulSet,
) []metav1.Condition {
	claimSpec := volumeManager.ClaimManager.Spec

	return []metav1.Condition{
		getClaimReadyCondition(claimResult, claimSpec),
		getVolumeMountedCondition(claimSpec.Name, statefulSet),
	}
}

func (volumeManager *MarketplaceVolumeManager) SetVolumeStatus(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	claimResult persistentvolumeclaim.Result,
) {
	volumeStatus := &liferayEnvironment.Status.MarketplaceVolume

	volumeStatus.ClaimName = volumeManager.ClaimManager.Spec.Name

	if claimResult.Phase != "" {
		volumeStatus.Phase = string(claimResult.Phase)
	}
}

type MarketplaceVolumeManager struct {
	ClaimManager *persistentvolumeclaim.PersistentVolumeClaimManager
}
