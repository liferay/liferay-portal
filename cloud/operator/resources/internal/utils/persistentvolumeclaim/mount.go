package persistentvolumeclaim

import (
	"slices"

	corev1 "k8s.io/api/core/v1"
)

func GetVolumeByClaimName(podSpec *corev1.PodSpec, claimName string) *corev1.Volume {
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

func IsVolumeMountedReadOnly(podSpec *corev1.PodSpec, volume *corev1.Volume) bool {
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

func isMountReadOnly(volume *corev1.Volume, volumeMount corev1.VolumeMount) bool {
	return volumeMount.ReadOnly || volume.PersistentVolumeClaim.ReadOnly
}
