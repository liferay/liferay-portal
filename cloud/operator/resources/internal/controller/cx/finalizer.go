package cx

import (
	"context"
	"fmt"
	"strings"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	equality "k8s.io/apimachinery/pkg/api/equality"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	controllerutil "sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
)

const Finalizer = "cx.liferay.com/finalizer"

const deleteGracePeriod = 2 * time.Minute

func (clientExtensionReconciler *ClientExtensionReconciler) addFinalizer(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
) error {
	if controllerutil.ContainsFinalizer(clientExtension, Finalizer) {
		return nil
	}

	patch := client.MergeFromWithOptions(
		clientExtension.DeepCopy(),
		client.MergeFromWithOptimisticLock{},
	)

	controllerutil.AddFinalizer(clientExtension, Finalizer)

	return clientExtensionReconciler.Patch(context, clientExtension, patch)
}

func (clientExtensionReconciler *ClientExtensionReconciler) deleteClientExtension(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
) (controllerruntime.Result, error) {
	if !controllerutil.ContainsFinalizer(clientExtension, Finalizer) {
		return controllerruntime.Result{}, nil
	}

	errorMessages, error := clientExtensionReconciler.deleteExtProvisionConfigMaps(
		clientExtension, context,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if len(errorMessages) > 0 {
		deleting, error := clientExtensionReconciler.setDeletingCondition(
			clientExtension, context, errorMessages,
		)

		if error != nil {
			return controllerruntime.Result{}, error
		}

		if graceRemaining := time.Until(
			deleting.LastTransitionTime.Add(deleteGracePeriod),
		); graceRemaining > 0 {
			return controllerruntime.Result{RequeueAfter: graceRemaining}, nil
		}
	}

	patch := client.MergeFromWithOptions(
		clientExtension.DeepCopy(),
		client.MergeFromWithOptimisticLock{},
	)

	controllerutil.RemoveFinalizer(clientExtension, Finalizer)

	if error := clientExtensionReconciler.Patch(context, clientExtension, patch); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
	}

	if len(errorMessages) > 0 {
		clientExtensionReconciler.Recorder.Event(
			clientExtension, corev1.EventTypeWarning, ReasonDeleteIncomplete,
			fmt.Sprintf(
				"Released the client extension for deletion after waiting %s for cleanup: %s.",
				deleteGracePeriod, strings.Join(errorMessages, "; "),
			),
		)
	}

	return controllerruntime.Result{}, nil
}

func (clientExtensionReconciler *ClientExtensionReconciler) deleteExtProvisionConfigMaps(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
) ([]string, error) {
	var ownedExtProvisionConfigMaps corev1.ConfigMapList

	if error := clientExtensionReconciler.APIReader.List(
		context, &ownedExtProvisionConfigMaps, client.MatchingLabels{
			LabelMetadataType: MetadataTypeExtProvision,
			LabelOwner:        ownerLabelValue(clientExtension),
		},
	); error != nil {
		return nil, error
	}

	var errorMessages []string

	for index := range ownedExtProvisionConfigMaps.Items {
		configMap := &ownedExtProvisionConfigMaps.Items[index]

		deleteError := clientExtensionReconciler.Delete(context, configMap)

		if apierrors.IsForbidden(deleteError) {
			errorMessages = append(
				errorMessages,
				fmt.Sprintf(
					"the DXP operator is not permitted to delete ConfigMap %q in namespace %q",
					configMap.Name, configMap.Namespace,
				),
			)

			continue
		}

		if client.IgnoreNotFound(deleteError) != nil {
			return nil, deleteError
		}
	}

	if !meta.IsStatusConditionTrue(
		clientExtension.Status.Conditions, cxv1alpha1.ConditionDelivered,
	) {
		return errorMessages, nil
	}

	var extInitConfigMap corev1.ConfigMap

	extInitConfigMapName := types.NamespacedName{
		Name: extInitName(clientExtension), Namespace: effectiveDxpNamespace(clientExtension),
	}

	getError := clientExtensionReconciler.Get(context, extInitConfigMapName, &extInitConfigMap)

	if getError == nil {
		errorMessages = append(
			errorMessages,
			fmt.Sprintf(
				"DXP has not removed ConfigMap %q from namespace %q",
				extInitConfigMapName.Name, extInitConfigMapName.Namespace,
			),
		)
	} else if !apierrors.IsNotFound(getError) {
		return nil, getError
	}

	return errorMessages, nil
}

func (clientExtensionReconciler *ClientExtensionReconciler) setDeletingCondition(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
	errorMessages []string,
) (*metav1.Condition, error) {
	status := clientExtension.Status.DeepCopy()

	if ready := meta.FindStatusCondition(
		status.Conditions, cxv1alpha1.ConditionReady,
	); (ready != nil) && (ready.Reason != ReasonDeleting) {
		meta.RemoveStatusCondition(&status.Conditions, cxv1alpha1.ConditionReady)
	}

	meta.SetStatusCondition(&status.Conditions, metav1.Condition{
		Message: fmt.Sprintf(
			"Waiting up to %s for cleanup: %s.", deleteGracePeriod, strings.Join(errorMessages, "; "),
		),
		ObservedGeneration: clientExtension.Generation,
		Reason:             ReasonDeleting,
		Status:             metav1.ConditionFalse,
		Type:               cxv1alpha1.ConditionReady,
	})

	status.Phase = cxv1alpha1.PhasePending

	if !equality.Semantic.DeepEqual(status, &clientExtension.Status) {
		clientExtension.Status = *status

		if error := clientExtensionReconciler.Status().Update(context, clientExtension); error != nil {
			return nil, error
		}
	}

	return meta.FindStatusCondition(clientExtension.Status.Conditions, cxv1alpha1.ConditionReady), nil
}
