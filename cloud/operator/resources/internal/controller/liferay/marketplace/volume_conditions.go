package marketplace

import (
	"fmt"

	"github.com/liferay/liferay-portal/cloud/operator/internal/utils/persistentvolumeclaim"
	appsv1 "k8s.io/api/apps/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	conditionTypeVolumeMounted = "MarketplaceVolumeMounted"
	conditionTypeVolumeReady   = "MarketplaceVolumeReady"
)

func getClaimReadyCondition(
	claimResult persistentvolumeclaim.Result,
	claimSpec persistentvolumeclaim.Spec,
) metav1.Condition {
	claimName := claimSpec.Name

	storageClassName := claimSpec.StorageClassName

	switch claimResult.State {
	case persistentvolumeclaim.StateAccessModesUnsupported:
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The persistent volume claim %q is bound but does not support ReadWriteMany. The storage class %q is not ReadWriteMany capable.",
				claimName, storageClassName),
			"ClaimNotReadWriteMany",
			metav1.ConditionFalse,
		)
	case persistentvolumeclaim.StateBound:
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The persistent volume claim %q is bound and supports ReadWriteMany.",
				claimName),
			"ClaimBound",
			metav1.ConditionTrue,
		)
	case persistentvolumeclaim.StateCreated:
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The persistent volume claim %q was created and is waiting to be bound.",
				claimName),
			"ClaimCreated",
			metav1.ConditionFalse,
		)
	case persistentvolumeclaim.StateNotBound:
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The persistent volume claim %q is not bound. Its phase is %q.",
				claimName, claimResult.Phase),
			"ClaimNotBound",
			metav1.ConditionFalse,
		)
	case persistentvolumeclaim.StateStorageClassNotFound:
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The storage class %q was not found. A ReadWriteMany capable storage class must exist before marketplace artifacts can be provisioned.",
				storageClassName),
			"StorageClassNotFound",
			metav1.ConditionFalse,
		)
	default:
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The persistent volume claim %q is in the unknown state %q.",
				claimName, claimResult.State),
			"ClaimStateUnknown",
			metav1.ConditionUnknown,
		)
	}
}

func getVolumeMountedCondition(
	claimName string,
	statefulSet *appsv1.StatefulSet,
) metav1.Condition {
	podSpec := &statefulSet.Spec.Template.Spec

	volume := persistentvolumeclaim.GetVolumeByClaimName(podSpec, claimName)

	if volume == nil {
		return newVolumeMountedCondition(
			fmt.Sprintf(
				"The stateful set %q does not reference the persistent volume claim %q.",
				statefulSet.Name, claimName),
			"ClaimNotReferenced",
			metav1.ConditionFalse,
		)
	}

	if !persistentvolumeclaim.IsVolumeMountedReadOnly(podSpec, volume) {
		return newVolumeMountedCondition(
			fmt.Sprintf(
				"The stateful set %q does not mount the volume %q read only.",
				statefulSet.Name, volume.Name),
			"ClaimNotReadOnly",
			metav1.ConditionFalse,
		)
	}

	return newVolumeMountedCondition(
		fmt.Sprintf(
			"The stateful set %q mounts the volume %q read only.",
			statefulSet.Name, volume.Name),
		"ClaimMounted",
		metav1.ConditionTrue,
	)
}

func newVolumeMountedCondition(
	message string,
	reason string,
	status metav1.ConditionStatus,
) metav1.Condition {
	return metav1.Condition{
		Message: message,
		Reason:  reason,
		Status:  status,
		Type:    conditionTypeVolumeMounted,
	}
}

func newVolumeReadyCondition(
	message string,
	reason string,
	status metav1.ConditionStatus,
) metav1.Condition {
	return metav1.Condition{
		Message: message,
		Reason:  reason,
		Status:  status,
		Type:    conditionTypeVolumeReady,
	}
}
