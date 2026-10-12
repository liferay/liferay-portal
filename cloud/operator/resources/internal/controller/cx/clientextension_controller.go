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
	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
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
	ReasonConfigurationOnly      = "ConfigurationOnly"
	ReasonDeleteIncomplete       = "DeleteIncomplete"
	ReasonDeleting               = "Deleting"
	ReasonDelivered              = "Delivered"
	ReasonDeliveryNotPermitted   = "DeliveryNotPermitted"
	ReasonDxpNamespaceNotFound   = "DxpNamespaceNotFound"
	ReasonExtInitMissing         = "ExtInitMissing"
	ReasonMirrorFailed           = "MirrorFailed"
	ReasonMirrored               = "Mirrored"
	ReasonNamespaceNotPermitted  = "NamespaceNotPermitted"
	ReasonNoExtInitRequired      = "NoExtInitRequired"
	ReasonNoMirrorRequired       = "NoMirrorRequired"
	ReasonProvisioned            = "Provisioned"
	ReasonReady                  = "Ready"
	ReasonServiceIDConflict      = "ServiceIDConflict"
	ReasonUnknownVirtualInstance = "UnknownVirtualInstance"
	ReasonWorkloadAccepted       = "WorkloadAccepted"
	ReasonWorkloadMisconfigured  = "WorkloadMisconfigured"
	ReasonWorkloadNotFound       = "WorkloadNotFound"
)

const deliveryClusterRoleName = "client-extension-delivery-cluster-role"

const extInitGracePeriod = 30 * time.Second

const refusalRequeueInterval = time.Minute

// +kubebuilder:rbac:groups="",resources=configmaps,verbs=get;list;watch
// +kubebuilder:rbac:groups="",resources=events,verbs=create;patch
// +kubebuilder:rbac:groups="",resources=namespaces,verbs=get;list;watch
// +kubebuilder:rbac:groups=apps,resources=deployments,verbs=get;list;patch;watch
// +kubebuilder:rbac:groups=batch,resources=cronjobs,verbs=get;list;watch
// +kubebuilder:rbac:groups=batch,resources=jobs,verbs=get;list;watch
// +kubebuilder:rbac:groups=cx.liferay.com,resources=clientextensions,verbs=get;list;patch;watch
// +kubebuilder:rbac:groups=cx.liferay.com,resources=clientextensions/finalizers,verbs=update
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

	if !clientExtension.DeletionTimestamp.IsZero() {
		return clientExtensionReconciler.deleteClientExtension(&clientExtension, context)
	}

	if error := clientExtensionReconciler.addFinalizer(
		&clientExtension, context,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	dxpNamespace, refusedReason, error := clientExtensionReconciler.resolveDxpNamespace(
		&clientExtension, context,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if refusedReason != "" {
		if error := clientExtensionReconciler.updateStatus(
			&clientExtension, context,
			newCondition(
				metav1.ConditionFalse, refusalMessage(&clientExtension, dxpNamespace, refusedReason), refusedReason,
			),
			"", nil, nil, nil, nil,
		); error != nil {
			return controllerruntime.Result{}, error
		}

		if error := clientExtensionReconciler.cleanUpStaleMirrors(
			&clientExtension, context, nil,
		); apierrors.IsForbidden(error) {
			controllerruntime.LoggerFrom(context).Info(
				"Unable to delete the mirrors of a refused client extension", "namespace", clientExtension.Namespace,
			)
		} else if error != nil {
			return controllerruntime.Result{}, error
		}

		return controllerruntime.Result{}, nil
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

	if client.IgnoreNotFound(error) != nil {
		return controllerruntime.Result{}, error
	}

	if apierrors.IsNotFound(error) || (dxpMetadata.Labels[LabelMirror] == "true") {
		return controllerruntime.Result{}, clientExtensionReconciler.updateStatus(
			&clientExtension, context,
			newCondition(
				metav1.ConditionFalse, unknownVirtualInstanceMessage(&clientExtension, dxpNamespace),
				ReasonUnknownVirtualInstance,
			),
			"", nil, nil, nil, nil,
		)
	}

	payload, error := json.MarshalIndent(clientExtension.Spec.Configs, "", "\t")

	if error != nil {
		return controllerruntime.Result{}, error
	}

	conflictingConfigMap, extProvisionResourceVersion, error := clientExtensionReconciler.applyExtProvision(
		&clientExtension, context, dxpNamespace, string(payload),
	)

	if apierrors.IsForbidden(error) {
		return controllerruntime.Result{RequeueAfter: refusalRequeueInterval},
			clientExtensionReconciler.updateStatus(
				&clientExtension, context,
				newCondition(
					metav1.ConditionFalse,
					fmt.Sprintf(
						"The DXP operator is not permitted to write ConfigMaps in namespace %q. DXP grants that by binding ClusterRole %q to ServiceAccount %q there.",
						dxpNamespace, deliveryClusterRoleName, clientExtensionReconciler.ServiceAccount,
					),
					ReasonDeliveryNotPermitted,
				),
				"", nil, nil, nil, nil,
			)
	}

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if conflictingConfigMap != nil {
		return controllerruntime.Result{RequeueAfter: refusalRequeueInterval}, clientExtensionReconciler.updateStatus(
			&clientExtension, context,
			newCondition(
				metav1.ConditionFalse, serviceIDConflictMessage(&clientExtension, conflictingConfigMap),
				ReasonServiceIDConflict,
			),
			"", nil, nil, nil, nil,
		)
	}

	currentConfigMapName := types.NamespacedName{Name: extProvisionName(&clientExtension), Namespace: dxpNamespace}

	ownedExtProvisionConfigMaps, error := clientExtensionReconciler.listOwnedExtProvisions(
		&clientExtension, context,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	forbiddenConfigMapNames, error := clientExtensionReconciler.cleanUpStaleExtProvisions(
		context, currentConfigMapName, ownedExtProvisionConfigMaps,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	message := fmt.Sprintf(
		"Delivered to ConfigMap %q in namespace %q.", currentConfigMapName.Name, currentConfigMapName.Namespace,
	)

	if len(forbiddenConfigMapNames) > 0 {
		message += fmt.Sprintf(
			" Unable to delete the stale ConfigMaps %s: the DXP operator is no longer permitted to write in their namespace.",
			strings.Join(forbiddenConfigMapNames, ", "),
		)
	}

	provisionedCondition, extInitConfigMap, error := clientExtensionReconciler.provisionedCondition(
		&clientExtension, context, dxpNamespace,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	var result controllerruntime.Result

	mirroredCondition, error := clientExtensionReconciler.mirroredCondition(
		&clientExtension, context, &dxpMetadata, extInitConfigMap,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if mirroredCondition.Status != metav1.ConditionTrue {
		result.RequeueAfter = refusalRequeueInterval
	}

	var configMapsWithDigest []*corev1.ConfigMap

	if (mirroredCondition.Status == metav1.ConditionTrue) && (provisionedCondition.Status == metav1.ConditionTrue) {
		configMapsWithDigest = []*corev1.ConfigMap{&dxpMetadata}

		if extInitConfigMap != nil {
			configMapsWithDigest = append(configMapsWithDigest, extInitConfigMap)
		}
	}

	workloadAcceptedCondition, workloadIssues, error := clientExtensionReconciler.workloadCondition(
		&clientExtension, configMapsWithDigest, context,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if error := clientExtensionReconciler.updateStatus(
		&clientExtension, context,
		newCondition(metav1.ConditionTrue, message, ReasonDelivered),
		extProvisionResourceVersion, &mirroredCondition, &provisionedCondition,
		&workloadAcceptedCondition, workloadIssues,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	for _, graceRemaining := range []time.Duration{
		extInitGraceRemaining(&clientExtension.Status),
		workloadGraceRemaining(&clientExtension.Status),
	} {
		if (graceRemaining > 0) && ((result.RequeueAfter == 0) ||
			(graceRemaining < result.RequeueAfter)) {

			result.RequeueAfter = graceRemaining
		}
	}

	return result, nil
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
		&appsv1.Deployment{},
		handler.EnqueueRequestsFromMapFunc(clientExtensionReconciler.requestsForWorkload(cxv1alpha1.WorkloadKindDeployment)),
		builder.OnlyMetadata,
		builder.WithPredicates(predicate.GenerationChangedPredicate{}),
	).Watches(
		&batchv1.CronJob{},
		handler.EnqueueRequestsFromMapFunc(clientExtensionReconciler.requestsForWorkload(cxv1alpha1.WorkloadKindCronJob)),
		builder.OnlyMetadata,
		builder.WithPredicates(predicate.GenerationChangedPredicate{}),
	).Watches(
		&batchv1.Job{},
		handler.EnqueueRequestsFromMapFunc(clientExtensionReconciler.requestsForWorkload(cxv1alpha1.WorkloadKindJob)),
		builder.OnlyMetadata,
		builder.WithPredicates(predicate.GenerationChangedPredicate{}),
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

func appendOptionalCondition(
	conditions []metav1.Condition,
	condition *metav1.Condition,
	conditionType string,
	status *cxv1alpha1.ClientExtensionStatus,
) []metav1.Condition {
	if condition == nil {
		meta.RemoveStatusCondition(&status.Conditions, conditionType)

		return conditions
	}

	optionalCondition := *condition

	optionalCondition.Type = conditionType

	return append(conditions, optionalCondition)
}

func (clientExtensionReconciler *ClientExtensionReconciler) applyExtProvision(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
	dxpNamespace string,
	payload string,
) (*corev1.ConfigMap, string, error) {
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
		return configMap, "", nil
	}

	if apierrors.IsAlreadyExists(error) {
		var existingConfigMap corev1.ConfigMap

		if getError := clientExtensionReconciler.APIReader.Get(
			context, client.ObjectKeyFromObject(configMap), &existingConfigMap,
		); getError != nil {
			return nil, "", getError
		}

		if !ownsExtProvision(clientExtension, &existingConfigMap) {
			return &existingConfigMap, "", nil
		}
	}

	if error != nil {
		return nil, "", error
	}

	return nil, configMap.ResourceVersion, nil
}

func (clientExtensionReconciler *ClientExtensionReconciler) cleanUpStaleExtProvisions(
	context context.Context,
	current types.NamespacedName,
	ownedExtProvisions []corev1.ConfigMap,
) ([]string, error) {
	var forbiddenConfigMapNames []string

	for index := range ownedExtProvisions {
		configMap := &ownedExtProvisions[index]

		if client.ObjectKeyFromObject(configMap) == current {
			continue
		}

		error := clientExtensionReconciler.Delete(context, configMap)

		if apierrors.IsForbidden(error) {
			forbiddenConfigMapNames = append(
				forbiddenConfigMapNames, fmt.Sprintf("%q", client.ObjectKeyFromObject(configMap).String()),
			)

			continue
		}

		if client.IgnoreNotFound(error) != nil {
			return nil, error
		}
	}

	slices.Sort(forbiddenConfigMapNames)

	return forbiddenConfigMapNames, nil
}

func extInitGraceRemaining(status *cxv1alpha1.ClientExtensionStatus) time.Duration {
	provisioned := meta.FindStatusCondition(status.Conditions, cxv1alpha1.ConditionProvisioned)

	if (provisioned == nil) || (provisioned.Reason != ReasonExtInitMissing) {
		return 0
	}

	graceStartTime := provisioned.LastTransitionTime.Time

	if (status.ExtProvisionObservedTime != nil) && status.ExtProvisionObservedTime.After(graceStartTime) {
		graceStartTime = status.ExtProvisionObservedTime.Time
	}

	return max(time.Until(graceStartTime.Add(extInitGracePeriod)), 0)
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

func (clientExtensionReconciler *ClientExtensionReconciler) mirrorFailedMessage(
	clientExtension *cxv1alpha1.ClientExtension,
	mirrorError error,
) string {
	if apierrors.IsForbidden(mirrorError) {
		return fmt.Sprintf(
			"The DXP operator is not permitted to write ConfigMaps in namespace %q, where it mirrors DXP's metadata. Grant that by binding ClusterRole %q to ServiceAccount %q there.",
			clientExtension.Namespace, deliveryClusterRoleName, clientExtensionReconciler.ServiceAccount,
		)
	}

	return fmt.Sprintf("Unable to mirror DXP's metadata into namespace %q: %s.", clientExtension.Namespace, mirrorError)
}

func (clientExtensionReconciler *ClientExtensionReconciler) mirroredCondition(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
	dxpMetadata *corev1.ConfigMap,
	extInit *corev1.ConfigMap,
) (metav1.Condition, error) {
	mirrorNames, error := clientExtensionReconciler.mirrorMetadata(clientExtension, context, dxpMetadata, extInit)

	if apierrors.IsForbidden(error) || errors.Is(error, errMirrorConflict) {
		return newCondition(
			metav1.ConditionFalse, clientExtensionReconciler.mirrorFailedMessage(clientExtension, error),
			ReasonMirrorFailed,
		), nil
	}

	if error != nil {
		return metav1.Condition{}, error
	}

	if len(mirrorNames) == 0 {
		return newCondition(
			metav1.ConditionTrue,
			fmt.Sprintf(
				"The client extension runs in DXP's namespace %q, so it reads DXP's metadata without a mirror.",
				clientExtension.Namespace,
			),
			ReasonNoMirrorRequired,
		), nil
	}

	quotedMirrorNames := make([]string, len(mirrorNames))

	for index, mirrorName := range mirrorNames {
		quotedMirrorNames[index] = fmt.Sprintf("%q", mirrorName)
	}

	return newCondition(
		metav1.ConditionTrue,
		fmt.Sprintf(
			"Mirrored DXP's ConfigMaps %s into namespace %q.", strings.Join(quotedMirrorNames, ", "),
			clientExtension.Namespace,
		),
		ReasonMirrored,
	), nil
}

func newCondition(conditionStatus metav1.ConditionStatus, message string, reason string) metav1.Condition {
	return metav1.Condition{Message: message, Reason: reason, Status: conditionStatus}
}

func (clientExtensionReconciler *ClientExtensionReconciler) provisionedCondition(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
	dxpNamespace string,
) (metav1.Condition, *corev1.ConfigMap, error) {
	identifiers := extInitIdentifiers(clientExtension)

	if len(identifiers) == 0 {
		return newCondition(
			metav1.ConditionTrue,
			"The configs declare no OAuth2 application, so they require no ext-init ConfigMap from DXP.",
			ReasonNoExtInitRequired,
		), nil, nil
	}

	var extInitConfigMap corev1.ConfigMap

	extInitConfigMapName := types.NamespacedName{Name: extInitName(clientExtension), Namespace: dxpNamespace}

	if error := clientExtensionReconciler.Get(
		context, extInitConfigMapName, &extInitConfigMap,
	); client.IgnoreNotFound(error) != nil {
		return metav1.Condition{}, nil, error
	}

	var existingExtInitConfigMap *corev1.ConfigMap

	if extInitConfigMap.ResourceVersion != "" {
		existingExtInitConfigMap = &extInitConfigMap
	}

	var missingIdentifiers []string

	for _, identifier := range identifiers {
		if _, ok := extInitConfigMap.Data[identifier+".oauth2.token.uri"]; !ok {
			missingIdentifiers = append(missingIdentifiers, fmt.Sprintf("%q", identifier))
		}
	}

	if len(missingIdentifiers) > 0 {
		return newCondition(
			metav1.ConditionFalse,
			fmt.Sprintf(
				"DXP has not written the OAuth2 applications %s to ConfigMap %q in namespace %q.",
				strings.Join(missingIdentifiers, ", "), extInitConfigMapName.Name,
				extInitConfigMapName.Namespace,
			),
			ReasonExtInitMissing,
		), existingExtInitConfigMap, nil
	}

	return newCondition(
		metav1.ConditionTrue,
		fmt.Sprintf("DXP wrote ConfigMap %q in namespace %q.", extInitConfigMapName.Name, extInitConfigMapName.Namespace),
		ReasonProvisioned,
	), existingExtInitConfigMap, nil
}

func readyCondition(conditions []metav1.Condition) metav1.Condition {
	for _, condition := range conditions {
		if condition.Status != metav1.ConditionTrue {
			return condition
		}
	}

	return newCondition(metav1.ConditionTrue, "The client extension is delivered and provisioned.", ReasonReady)
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

	if labels[LabelMirror] == "true" {
		return requestsForMirror(object)
	}

	metadataType := labels[LabelMetadataType]

	if (metadataType != MetadataTypeDxp) && (metadataType != MetadataTypeExtInit) &&
		(metadataType != MetadataTypeExtProvision) {

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

		if (metadataType != MetadataTypeDxp) && (clientExtension.Spec.ServiceID != labels[LabelServiceID]) {
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

	if clientExtension.Namespace == dxpNamespace {
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

	serviceID := configMap.Labels[LabelServiceID]
	virtualInstanceID := configMap.Labels[LabelVirtualInstance]

	if (serviceID != "") &&
		((clientExtension.Spec.ServiceID != serviceID) ||
			(clientExtension.Spec.VirtualInstanceID != virtualInstanceID)) {

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
	context context.Context,
	delivered metav1.Condition,
	extProvisionResourceVersion string,
	mirrored *metav1.Condition,
	provisioned *metav1.Condition,
	workloadAccepted *metav1.Condition,
	workloadIssues []string,
) error {
	status := clientExtension.Status.DeepCopy()

	delivered.Type = cxv1alpha1.ConditionDelivered

	conditions := []metav1.Condition{delivered}

	conditions = appendOptionalCondition(conditions, mirrored, cxv1alpha1.ConditionMirrored, status)
	conditions = appendOptionalCondition(conditions, provisioned, cxv1alpha1.ConditionProvisioned, status)
	conditions = appendOptionalCondition(conditions, workloadAccepted, cxv1alpha1.ConditionWorkloadAccepted, status)

	if previousWorkloadAccepted := meta.FindStatusCondition(
		status.Conditions, cxv1alpha1.ConditionWorkloadAccepted,
	); (workloadAccepted != nil) && (workloadAccepted.Reason == ReasonWorkloadNotFound) &&
		(previousWorkloadAccepted != nil) && (previousWorkloadAccepted.Reason != ReasonWorkloadNotFound) {

		meta.RemoveStatusCondition(&status.Conditions, cxv1alpha1.ConditionWorkloadAccepted)
	}

	ready := readyCondition(conditions)

	ready.Type = cxv1alpha1.ConditionReady

	conditions = append(conditions, ready)

	for _, condition := range conditions {
		condition.ObservedGeneration = clientExtension.Generation

		meta.SetStatusCondition(&status.Conditions, condition)
	}

	if (extProvisionResourceVersion != "") &&
		(extProvisionResourceVersion != status.ExtProvisionResourceVersion) {

		now := metav1.Now()

		status.ExtProvisionObservedTime = &now
		status.ExtProvisionResourceVersion = extProvisionResourceVersion
	}

	status.ObservedGeneration = clientExtension.Generation

	status.WorkloadIssues = workloadIssues
	status.WorkloadName = ""

	if (clientExtension.Spec.WorkloadRef != nil) && (workloadAccepted != nil) {
		status.WorkloadName = clientExtension.Spec.WorkloadRef.Name
	}

	if ((ready.Reason == ReasonExtInitMissing) && (extInitGraceRemaining(status) > 0)) ||
		((ready.Reason == ReasonWorkloadNotFound) && (workloadGraceRemaining(status) > 0)) {

		status.Phase = cxv1alpha1.PhasePending
	} else if ready.Status == metav1.ConditionTrue {
		status.Phase = cxv1alpha1.PhaseReady
	} else {
		status.Phase = cxv1alpha1.PhaseDegraded
	}

	if equality.Semantic.DeepEqual(status, &clientExtension.Status) {
		return nil
	}

	clientExtension.Status = *status

	if error := clientExtensionReconciler.Status().Update(context, clientExtension); error != nil {
		return client.IgnoreNotFound(error)
	}

	if status.Phase == cxv1alpha1.PhaseDegraded {
		clientExtensionReconciler.Recorder.Event(clientExtension, corev1.EventTypeWarning, ready.Reason, ready.Message)
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
