package marketplace

import (
	fmt "fmt"
	slices "slices"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	storagev1 "k8s.io/api/storage/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	conditionTypeVolumeMounted = "MarketplaceVolumeMounted"
	conditionTypeVolumeReady   = "MarketplaceVolumeReady"
)

func GetVolumeConditions(
	persistentVolumeClaim *corev1.PersistentVolumeClaim,
	statefulSet *appsv1.StatefulSet,
	storageClass *storagev1.StorageClass,
) []metav1.Condition {
	return []metav1.Condition{
		getClaimReadyCondition(persistentVolumeClaim, storageClass),
		getVolumeMountedCondition(persistentVolumeClaim.Name, statefulSet),
	}
}

func getClaimReadyCondition(
	persistentVolumeClaim *corev1.PersistentVolumeClaim,
	storageClass *storagev1.StorageClass,
) metav1.Condition {
	claimName := persistentVolumeClaim.Name

	storageClassName := getStorageClassName(persistentVolumeClaim)

	if storageClass == nil {
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The storage class %q was not found. A ReadWriteMany capable storage class must exist before marketplace artifacts can be provisioned",
				storageClassName),
			"StorageClassNotFound",
			metav1.ConditionFalse,
		)
	}

	if persistentVolumeClaim.Status.Phase != corev1.ClaimBound {
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The persistent volume claim %q is not bound. Its phase is %q",
				claimName, persistentVolumeClaim.Status.Phase),
			"ClaimNotBound",
			metav1.ConditionFalse,
		)
	}

	if !slices.Contains(persistentVolumeClaim.Status.AccessModes, corev1.ReadWriteMany) {
		return newVolumeReadyCondition(
			fmt.Sprintf(
				"The persistent volume claim %q is bound but does not support ReadWriteMany. The storage class %q is not ReadWriteMany capable",
				claimName, storageClassName),
			"ClaimNotReadWriteMany",
			metav1.ConditionFalse,
		)
	}

	return newVolumeReadyCondition(
		fmt.Sprintf(
			"The persistent volume claim %q is bound and supports ReadWriteMany",
			claimName),
		"ClaimBound",
		metav1.ConditionTrue,
	)
}

func getStorageClassName(persistentVolumeClaim *corev1.PersistentVolumeClaim) string {
	if persistentVolumeClaim.Spec.StorageClassName == nil {
		return ""
	}

	return *persistentVolumeClaim.Spec.StorageClassName
}

func getVolumeByClaimName(podSpec *corev1.PodSpec, claimName string) *corev1.Volume {
	for index, volume := range podSpec.Volumes {
		if volume.PersistentVolumeClaim == nil {
			continue
		}

		if volume.PersistentVolumeClaim.ClaimName == claimName {
			return &podSpec.Volumes[index]
		}
	}

	return nil
}

func getVolumeMountedCondition(
	claimName string,
	statefulSet *appsv1.StatefulSet,
) metav1.Condition {
	podSpec := &statefulSet.Spec.Template.Spec

	volume := getVolumeByClaimName(podSpec, claimName)

	if volume == nil {
		return newVolumeMountedCondition(
			fmt.Sprintf(
				"The stateful set %q does not reference the persistent volume claim %q",
				statefulSet.Name, claimName),
			"ClaimNotReferenced",
			metav1.ConditionFalse,
		)
	}

	if !isVolumeMountedReadOnly(podSpec, volume) {
		return newVolumeMountedCondition(
			fmt.Sprintf(
				"The stateful set %q does not mount the volume %q read only",
				statefulSet.Name, volume.Name),
			"ClaimNotReadOnly",
			metav1.ConditionFalse,
		)
	}

	return newVolumeMountedCondition(
		fmt.Sprintf(
			"The stateful set %q mounts the volume %q read only",
			statefulSet.Name, volume.Name),
		"ClaimMounted",
		metav1.ConditionTrue,
	)
}

func isMountReadOnly(volume *corev1.Volume, volumeMount corev1.VolumeMount) bool {
	return volumeMount.ReadOnly || volume.PersistentVolumeClaim.ReadOnly
}

func isVolumeMountedReadOnly(podSpec *corev1.PodSpec, volume *corev1.Volume) bool {
	mounted := false

	for _, container := range slices.Concat(podSpec.Containers, podSpec.InitContainers) {
		for _, volumeMount := range container.VolumeMounts {
			if volumeMount.Name != volume.Name {
				continue
			}

			if !isMountReadOnly(volume, volumeMount) {
				return false
			}

			mounted = true
		}
	}

	return mounted
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
