package persistentvolumeclaim

import (
	"context"
	"slices"

	corev1 "k8s.io/api/core/v1"
	storagev1 "k8s.io/api/storage/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	resource "k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	StateAccessModesUnsupported State = "AccessModesUnsupported"
	StateBound                  State = "Bound"
	StateCreated                State = "Created"
	StateNotBound               State = "NotBound"
	StateStorageClassNotFound   State = "StorageClassNotFound"
)

func (claimManager *PersistentVolumeClaimManager) CreateClaimIfMissing() (Result, error) {
	storageClassFound, error := claimManager.storageClassExists(
		claimManager.Spec.StorageClassName,
	)

	if error != nil {
		return Result{}, error
	}

	if !storageClassFound {
		return Result{State: StateStorageClassNotFound}, nil
	}

	persistentVolumeClaim, error := claimManager.findClaim()

	if error != nil {
		return Result{}, error
	}

	if persistentVolumeClaim == nil {
		if error := claimManager.createClaim(); error != nil {
			return Result{}, error
		}

		return Result{
			Phase: corev1.ClaimPending,
			State: StateCreated,
		}, nil
	}

	return Result{
		Phase: persistentVolumeClaim.Status.Phase,
		State: getClaimState(persistentVolumeClaim, claimManager.Spec),
	}, nil
}

func (claimManager *PersistentVolumeClaimManager) createClaim() error {
	persistentVolumeClaim := newClaim(claimManager.Spec)

	if error := controllerruntime.SetControllerReference(
		claimManager.Owner,
		persistentVolumeClaim,
		claimManager.Scheme(),
	); error != nil {
		return error
	}

	if error := claimManager.Create(
		claimManager.Context,
		persistentVolumeClaim,
	); error != nil && !errors.IsAlreadyExists(error) {
		return error
	}

	return nil
}

func (claimManager *PersistentVolumeClaimManager) findClaim() (*corev1.PersistentVolumeClaim, error) {
	persistentVolumeClaim := &corev1.PersistentVolumeClaim{}

	namespacedName := types.NamespacedName{
		Name:      claimManager.Spec.Name,
		Namespace: claimManager.Spec.Namespace,
	}

	if error := claimManager.Get(
		claimManager.Context,
		namespacedName,
		persistentVolumeClaim,
	); error != nil {
		if errors.IsNotFound(error) {
			return nil, nil
		}

		return nil, error
	}

	return persistentVolumeClaim, nil
}

func getClaimState(
	persistentVolumeClaim *corev1.PersistentVolumeClaim,
	spec Spec,
) State {
	if persistentVolumeClaim.Status.Phase != corev1.ClaimBound {
		return StateNotBound
	}

	for _, accessMode := range spec.AccessModes {
		if !slices.Contains(persistentVolumeClaim.Status.AccessModes, accessMode) {
			return StateAccessModesUnsupported
		}
	}

	return StateBound
}

func newClaim(spec Spec) *corev1.PersistentVolumeClaim {
	return &corev1.PersistentVolumeClaim{
		ObjectMeta: metav1.ObjectMeta{
			Name:      spec.Name,
			Namespace: spec.Namespace,
		},
		Spec: corev1.PersistentVolumeClaimSpec{
			AccessModes: spec.AccessModes,
			Resources: corev1.VolumeResourceRequirements{
				Requests: corev1.ResourceList{
					corev1.ResourceStorage: spec.Size,
				},
			},
			StorageClassName: &spec.StorageClassName,
		},
	}
}

func (claimManager *PersistentVolumeClaimManager) storageClassExists(
	storageClassName string,
) (bool, error) {
	storageClass := &storagev1.StorageClass{}

	if error := claimManager.Get(
		claimManager.Context,
		types.NamespacedName{Name: storageClassName},
		storageClass,
	); error != nil {
		if errors.IsNotFound(error) {
			return false, nil
		}

		return false, error
	}

	return true, nil
}

type PersistentVolumeClaimManager struct {
	client.Client

	Context context.Context
	Spec    Spec
	Owner   client.Object
}

type Result struct {
	Phase corev1.PersistentVolumeClaimPhase
	State State
}

type Spec struct {
	AccessModes      []corev1.PersistentVolumeAccessMode
	Name             string
	Namespace        string
	Size             resource.Quantity
	StorageClassName string
}

type State string
