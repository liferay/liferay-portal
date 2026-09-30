package cx

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"slices"
	"strings"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	equality "k8s.io/apimachinery/pkg/api/equality"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	builder "sigs.k8s.io/controller-runtime/pkg/builder"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	controllerutil "sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	handler "sigs.k8s.io/controller-runtime/pkg/handler"
	predicate "sigs.k8s.io/controller-runtime/pkg/predicate"
	reconcile "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const (
	ReasonDelivered              = "Delivered"
	ReasonDeliveryNotPermitted   = "DeliveryNotPermitted"
	ReasonDxpNamespaceNotFound   = "DxpNamespaceNotFound"
	ReasonNamespaceNotPermitted  = "NamespaceNotPermitted"
	ReasonServiceIDConflict      = "ServiceIDConflict"
	ReasonUnknownVirtualInstance = "UnknownVirtualInstance"
)

const deliveryClusterRoleName = "client-extension-delivery-cluster-role"

// RoleBindings, and ConfigMaps without a metadataType label, are not watched,
// so a refusal that one of them can lift is retried on a timer.
const refusalRequeueInterval = time.Minute

// +kubebuilder:rbac:groups="",resources=configmaps,verbs=get;list;watch
// +kubebuilder:rbac:groups="",resources=events,verbs=create;patch
// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;watch
// +kubebuilder:rbac:groups=cx.liferay.com,resources=clientextensions,verbs=get;list;watch
// +kubebuilder:rbac:groups=cx.liferay.com,resources=clientextensions/status,verbs=get;patch;update
func (clientExtensionReconciler *ClientExtensionReconciler) Reconcile(
	context context.Context,
	request controllerruntime.Request,
) (controllerruntime.Result, error) {
	var clientExtension cxv1alpha1.ClientExtension

	if error := clientExtensionReconciler.Get(
		context, request.NamespacedName, &clientExtension,
	); error != nil {
		return controllerruntime.Result{}, client.IgnoreNotFound(error)
	}

	dxpNamespace, refusedReason, error := clientExtensionReconciler.resolveDxpNamespace(
		&clientExtension, context,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if refusedReason != "" {
		return controllerruntime.Result{}, clientExtensionReconciler.updateStatus(
			&clientExtension, metav1.ConditionFalse, context,
			refusalMessage(&clientExtension, dxpNamespace, refusedReason), refusedReason,
		)
	}

	var dxpMetadata corev1.ConfigMap

	error = clientExtensionReconciler.Get(
		context,
		types.NamespacedName{
			Name:      dxpMetadataName(clientExtension.Spec.VirtualInstanceID),
			Namespace: dxpNamespace,
		},
		&dxpMetadata,
	)

	if apierrors.IsNotFound(error) {
		return controllerruntime.Result{}, clientExtensionReconciler.updateStatus(
			&clientExtension, metav1.ConditionFalse, context,
			unknownVirtualInstanceMessage(&clientExtension, dxpNamespace), ReasonUnknownVirtualInstance,
		)
	}

	if error != nil {
		return controllerruntime.Result{}, error
	}

	payload, error := json.MarshalIndent(clientExtension.Spec.Configs, "", "\t")

	if error != nil {
		return controllerruntime.Result{}, error
	}

	conflictingConfigMap, error := clientExtensionReconciler.applyExtProvision(
		&clientExtension, context, dxpNamespace, string(payload),
	)

	if apierrors.IsForbidden(error) {
		return controllerruntime.Result{RequeueAfter: refusalRequeueInterval},
			clientExtensionReconciler.updateStatus(
				&clientExtension, metav1.ConditionFalse, context,
				fmt.Sprintf(
					"The DXP operator is not permitted to write ConfigMaps in namespace %q. DXP grants that by binding ClusterRole %q to ServiceAccount %q there.",
					dxpNamespace, deliveryClusterRoleName, clientExtensionReconciler.ServiceAccount,
				),
				ReasonDeliveryNotPermitted,
			)
	}

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if conflictingConfigMap != nil {
		return controllerruntime.Result{RequeueAfter: refusalRequeueInterval}, clientExtensionReconciler.updateStatus(
			&clientExtension, metav1.ConditionFalse, context,
			serviceIDConflictMessage(&clientExtension, conflictingConfigMap),
			ReasonServiceIDConflict,
		)
	}

	currentConfigMapName := types.NamespacedName{Name: extProvisionName(&clientExtension), Namespace: dxpNamespace}

	ownedExtProvisions, error := clientExtensionReconciler.listOwnedExtProvisions(
		&clientExtension, context,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	undeletedConfigMapNames, error := clientExtensionReconciler.deleteStaleExtProvisions(
		context, currentConfigMapName, ownedExtProvisions,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	message := fmt.Sprintf(
		"Delivered to ConfigMap %q in namespace %q.", currentConfigMapName.Name, currentConfigMapName.Namespace,
	)

	if len(undeletedConfigMapNames) > 0 {
		message += fmt.Sprintf(
			" Unable to delete the stale ConfigMaps %s: the DXP operator is no longer permitted to write in their namespace.",
			strings.Join(undeletedConfigMapNames, ", "),
		)
	}

	return controllerruntime.Result{}, clientExtensionReconciler.updateStatus(
		&clientExtension, metav1.ConditionTrue, context, message, ReasonDelivered,
	)
}

func (clientExtensionReconciler *ClientExtensionReconciler) SetupWithManager(
	manager controllerruntime.Manager,
) error {
	return controllerruntime.NewControllerManagedBy(
		manager,
	).For(
		&cxv1alpha1.ClientExtension{},
		builder.WithPredicates(predicate.GenerationChangedPredicate{}),
	).Named(
		"clientextension",
	).Watches(
		&corev1.ConfigMap{},
		handler.EnqueueRequestsFromMapFunc(clientExtensionReconciler.requestsForConfigMap),
	).Watches(
		&corev1.Namespace{},
		handler.EnqueueRequestsFromMapFunc(clientExtensionReconciler.requestsForNamespace),
	).Complete(
		clientExtensionReconciler,
	)
}

func (clientExtensionReconciler *ClientExtensionReconciler) applyExtProvision(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
	dxpNamespace string,
	payload string,
) (*corev1.ConfigMap, error) {
	configMap := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      extProvisionName(clientExtension),
			Namespace: dxpNamespace,
		},
	}

	_, error := controllerutil.CreateOrUpdate(
		context, clientExtensionReconciler.Client, configMap,
		func() error {
			if (configMap.ResourceVersion != "") && !ownsExtProvision(clientExtension, configMap) {
				return errExtProvisionOwnedElsewhere
			}

			if configMap.Annotations == nil {
				configMap.Annotations = map[string]string{}
			}

			if clientExtension.Spec.Domain == "" {
				delete(configMap.Annotations, AnnotationMainDomain)
			} else {
				configMap.Annotations[AnnotationMainDomain] = clientExtension.Spec.Domain
			}

			configMap.Annotations[AnnotationOwnerName] = clientExtension.Name
			configMap.Annotations[AnnotationOwnerNamespace] = clientExtension.Namespace

			configMap.Data = map[string]string{
				clientExtension.Spec.ServiceID + ".client-extension-config.json": payload,
			}

			if configMap.Labels == nil {
				configMap.Labels = map[string]string{}
			}

			configMap.Labels[LabelMetadataType] = MetadataTypeExtProvision
			configMap.Labels[LabelOwner] = ownerLabelValue(clientExtension)
			configMap.Labels[LabelServiceID] = clientExtension.Spec.ServiceID
			configMap.Labels[LabelVirtualInstance] = clientExtension.Spec.VirtualInstanceID

			return nil
		},
	)

	if errors.Is(error, errExtProvisionOwnedElsewhere) {
		return configMap, nil
	}

	if apierrors.IsAlreadyExists(error) {
		var existingConfigMap corev1.ConfigMap

		if getError := clientExtensionReconciler.APIReader.Get(
			context, client.ObjectKeyFromObject(configMap), &existingConfigMap,
		); getError != nil {
			return nil, getError
		}

		if !ownsExtProvision(clientExtension, &existingConfigMap) {
			return &existingConfigMap, nil
		}
	}

	return nil, error
}

func (clientExtensionReconciler *ClientExtensionReconciler) deleteStaleExtProvisions(
	context context.Context,
	current types.NamespacedName,
	ownedExtProvisions []corev1.ConfigMap,
) ([]string, error) {
	var undeletedConfigMapNames []string

	for index := range ownedExtProvisions {
		configMap := &ownedExtProvisions[index]

		if client.ObjectKeyFromObject(configMap) == current {
			continue
		}

		error := clientExtensionReconciler.Delete(context, configMap)

		// Reported in status rather than as an event, which would repeat on every
		// reconcile for as long as the namespace refuses the delete.

		if apierrors.IsForbidden(error) {
			undeletedConfigMapNames = append(
				undeletedConfigMapNames, fmt.Sprintf("%q", client.ObjectKeyFromObject(configMap).String()),
			)

			continue
		}

		if client.IgnoreNotFound(error) != nil {
			return nil, error
		}
	}

	slices.Sort(undeletedConfigMapNames)

	return undeletedConfigMapNames, nil
}

func (clientExtensionReconciler *ClientExtensionReconciler) listOwnedExtProvisions(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
) ([]corev1.ConfigMap, error) {
	var configMapList corev1.ConfigMapList

	if error := clientExtensionReconciler.List(
		context, &configMapList,
		client.MatchingLabels{
			LabelMetadataType: MetadataTypeExtProvision,
			LabelOwner:        ownerLabelValue(clientExtension),
		},
	); error != nil {
		return nil, error
	}

	return configMapList.Items, nil
}

func refusalMessage(
	clientExtension *cxv1alpha1.ClientExtension, dxpNamespace string, reason string,
) string {
	if reason == ReasonDxpNamespaceNotFound {
		return fmt.Sprintf(
			"Namespace %q does not exist, so there is no DXP to deliver to. Set spec.dxpNamespace to the namespace DXP runs in.",
			dxpNamespace,
		)
	}

	return fmt.Sprintf(
		"Namespace %q does not list %q in its %q annotation, so this client extension is not delivered there. Whoever manages DXP grants access by adding %q to that annotation.",
		dxpNamespace, clientExtension.Namespace,
		cxv1alpha1.AnnotationAllowedClientExtensionNamespaces, clientExtension.Namespace,
	)
}

func (clientExtensionReconciler *ClientExtensionReconciler) requestsForConfigMap(
	context context.Context,
	object client.Object,
) []reconcile.Request {
	labels := object.GetLabels()

	metadataType := labels[LabelMetadataType]

	if (metadataType != MetadataTypeDxp) && (metadataType != MetadataTypeExtProvision) {
		return nil
	}

	var clientExtensionList cxv1alpha1.ClientExtensionList

	if error := clientExtensionReconciler.List(context, &clientExtensionList); error != nil {
		controllerruntime.LoggerFrom(context).Error(
			error, "Unable to list client extensions", "configMap", client.ObjectKeyFromObject(object),
		)

		return nil
	}

	var requests []reconcile.Request

	for index := range clientExtensionList.Items {
		clientExtension := &clientExtensionList.Items[index]

		if effectiveDxpNamespace(clientExtension) != object.GetNamespace() {
			continue
		}

		if clientExtension.Spec.VirtualInstanceID != labels[LabelVirtualInstance] {
			continue
		}

		if (metadataType == MetadataTypeExtProvision) &&
			(clientExtension.Spec.ServiceID != labels[LabelServiceID]) {

			continue
		}

		requests = append(requests, reconcile.Request{
			NamespacedName: client.ObjectKeyFromObject(clientExtension),
		})
	}

	return requests
}

func (clientExtensionReconciler *ClientExtensionReconciler) requestsForNamespace(
	context context.Context,
	object client.Object,
) []reconcile.Request {
	var clientExtensionList cxv1alpha1.ClientExtensionList

	if error := clientExtensionReconciler.List(context, &clientExtensionList); error != nil {
		controllerruntime.LoggerFrom(context).Error(
			error, "Unable to list client extensions", "namespace", object.GetName(),
		)

		return nil
	}

	var requests []reconcile.Request

	for index := range clientExtensionList.Items {
		clientExtension := &clientExtensionList.Items[index]

		if effectiveDxpNamespace(clientExtension) != object.GetName() {
			continue
		}

		requests = append(requests, reconcile.Request{
			NamespacedName: client.ObjectKeyFromObject(clientExtension),
		})
	}

	return requests
}

func (clientExtensionReconciler *ClientExtensionReconciler) resolveDxpNamespace(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
) (string, string, error) {
	dxpNamespace := effectiveDxpNamespace(clientExtension)

	if dxpNamespace == clientExtension.Namespace {
		return dxpNamespace, "", nil
	}

	var namespace corev1.Namespace

	getError := clientExtensionReconciler.Get(
		context, types.NamespacedName{Name: dxpNamespace}, &namespace,
	)

	if apierrors.IsNotFound(getError) {
		return dxpNamespace, ReasonDxpNamespaceNotFound, nil
	}

	if getError != nil {
		return "", "", getError
	}

	if !slices.Contains(allowedNamespaces(&namespace), clientExtension.Namespace) {
		return dxpNamespace, ReasonNamespaceNotPermitted, nil
	}

	return dxpNamespace, "", nil
}

func serviceIDConflictMessage(clientExtension *cxv1alpha1.ClientExtension, configMap *corev1.ConfigMap) string {
	owner := "is not managed by any ClientExtension"

	if ownerName := configMap.Annotations[AnnotationOwnerName]; ownerName != "" {
		owner = fmt.Sprintf(
			"already belongs to ClientExtension %q in namespace %q",
			ownerName, configMap.Annotations[AnnotationOwnerNamespace],
		)
	}

	// The name joins serviceId and virtualInstanceId with a dash, and both can
	// contain dashes, so a different pair can arrive at the same name. DXP names
	// its credentials ConfigMap the same way, so the two still cannot coexist.

	serviceID := configMap.Labels[LabelServiceID]
	virtualInstanceID := configMap.Labels[LabelVirtualInstance]

	if (serviceID != "") &&
		((serviceID != clientExtension.Spec.ServiceID) ||
			(virtualInstanceID != clientExtension.Spec.VirtualInstanceID)) {

		return fmt.Sprintf(
			"Unable to deliver to ConfigMap %q in namespace %q: it %s and holds serviceId %q on virtual instance %q, whose ConfigMap names collide with serviceId %q on virtual instance %q. Give one of them a different serviceId.",
			configMap.Name, configMap.Namespace, owner, serviceID, virtualInstanceID,
			clientExtension.Spec.ServiceID, clientExtension.Spec.VirtualInstanceID,
		)
	}

	return fmt.Sprintf(
		"Unable to deliver to ConfigMap %q in namespace %q: it %s. DXP identifies a client extension by its serviceId %q for a virtual instance, so it can be deployed only once.",
		configMap.Name, configMap.Namespace, owner, clientExtension.Spec.ServiceID,
	)
}

func unknownVirtualInstanceMessage(clientExtension *cxv1alpha1.ClientExtension, dxpNamespace string) string {
	return fmt.Sprintf(
		"Virtual instance %q is unknown to DXP: ConfigMap %q does not exist in namespace %q.",
		clientExtension.Spec.VirtualInstanceID,
		dxpMetadataName(clientExtension.Spec.VirtualInstanceID), dxpNamespace,
	)
}

func (clientExtensionReconciler *ClientExtensionReconciler) updateStatus(
	clientExtension *cxv1alpha1.ClientExtension,
	conditionStatus metav1.ConditionStatus,
	context context.Context,
	message string,
	reason string,
) error {
	status := clientExtension.Status.DeepCopy()

	for _, conditionType := range []string{cxv1alpha1.ConditionDelivered, cxv1alpha1.ConditionReady} {
		meta.SetStatusCondition(&status.Conditions, metav1.Condition{
			Message:            message,
			ObservedGeneration: clientExtension.Generation,
			Reason:             reason,
			Status:             conditionStatus,
			Type:               conditionType,
		})
	}

	status.ObservedGeneration = clientExtension.Generation
	status.Phase = cxv1alpha1.PhaseDegraded

	if conditionStatus == metav1.ConditionTrue {
		status.Phase = cxv1alpha1.PhaseReady
	}

	if equality.Semantic.DeepEqual(status, &clientExtension.Status) {
		return nil
	}

	clientExtension.Status = *status

	if error := clientExtensionReconciler.Status().Update(context, clientExtension); error != nil {
		return client.IgnoreNotFound(error)
	}

	if status.Phase == cxv1alpha1.PhaseDegraded {
		clientExtensionReconciler.Recorder.Event(clientExtension, corev1.EventTypeWarning, reason, message)
	}

	return nil
}

type ClientExtensionReconciler struct {
	client.Client

	APIReader      client.Reader
	Recorder       record.EventRecorder
	ServiceAccount string
}

var errExtProvisionOwnedElsewhere = errors.New("ext-provision: owned by another client extension")
