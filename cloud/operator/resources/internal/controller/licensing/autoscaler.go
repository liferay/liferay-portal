package licensing

import (
	"context"
	"encoding/json"
	"fmt"
	"sync"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	autoscalingv2 "k8s.io/api/autoscaling/v2"
	meta "k8s.io/apimachinery/pkg/api/meta"
	unstructured "k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	runtime "k8s.io/apimachinery/pkg/runtime"
	schema "k8s.io/apimachinery/pkg/runtime/schema"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	controller "sigs.k8s.io/controller-runtime/pkg/controller"
	handler "sigs.k8s.io/controller-runtime/pkg/handler"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
	predicate "sigs.k8s.io/controller-runtime/pkg/predicate"
	source "sigs.k8s.io/controller-runtime/pkg/source"
)

const (
	annotationPausedReplicas      = "autoscaling.keda.sh/paused-replicas"
	annotationPausedReplicasOwner = "licensing.liferay.com/paused-replicas"
)

func capReplicaBounds(
	autoscaling *licensingv1alpha1.Autoscaling,
	replicaCeiling int32,
) replicaBounds {
	if replicaCeiling <= 0 {
		return replicaBounds{
			Maximum: 1,
			Minimum: 1,
			Paused:  true,
		}
	}

	maximum := max(min(autoscaling.MaxReplicas, replicaCeiling), 1)

	return replicaBounds{
		Maximum: maximum,
		Minimum: min(autoscaling.MinReplicas, maximum),
	}
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceAutoscalerCeiling(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	replicaCeiling int32,
) error {
	autoscaling := liferayEnvironment.Spec.Autoscaling

	if autoscaling == nil {
		return nil
	}

	replicaBounds := capReplicaBounds(autoscaling, replicaCeiling)

	if error := liferayEnvironmentReconciler.enforceHorizontalPodAutoscalerCeiling(
		context, liferayEnvironment, replicaBounds,
	); error != nil {
		return error
	}

	return liferayEnvironmentReconciler.enforceScaledObjectCeiling(
		context, liferayEnvironment, replicaBounds,
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceHorizontalPodAutoscalerCeiling(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	replicaBounds replicaBounds,
) error {
	horizontalPodAutoscalerList := &autoscalingv2.HorizontalPodAutoscalerList{}

	if error := liferayEnvironmentReconciler.List(
		context,
		horizontalPodAutoscalerList,
		client.InNamespace(liferayEnvironment.Namespace),
	); error != nil {
		return error
	}

	for index := range horizontalPodAutoscalerList.Items {
		horizontalPodAutoscaler := &horizontalPodAutoscalerList.Items[index]

		if isOwnedByScaledObject(horizontalPodAutoscaler) ||
			!isScalingWorkload(
				horizontalPodAutoscaler.Spec.ScaleTargetRef.Kind,
				liferayEnvironment,
				horizontalPodAutoscaler.Spec.ScaleTargetRef.Name,
			) {

			continue
		}

		if horizontalPodAutoscaler.Spec.MaxReplicas == replicaBounds.Maximum &&
			horizontalPodAutoscaler.Spec.MinReplicas != nil &&
			*horizontalPodAutoscaler.Spec.MinReplicas == replicaBounds.Minimum {

			continue
		}

		patch := client.MergeFrom(horizontalPodAutoscaler.DeepCopy())

		horizontalPodAutoscaler.Spec.MaxReplicas = replicaBounds.Maximum
		horizontalPodAutoscaler.Spec.MinReplicas = &replicaBounds.Minimum

		if error := liferayEnvironmentReconciler.Patch(
			context, horizontalPodAutoscaler, patch, client.FieldOwner(fieldOwner),
		); error != nil {
			return error
		}

		logf.FromContext(context).Info(
			"Licensed autoscaling ceiling was enforced",
			"autoscaler", horizontalPodAutoscaler.Name,
			"maxReplicas", replicaBounds.Maximum,
			"minReplicas", replicaBounds.Minimum,
		)
	}

	return nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceScaledObjectCeiling(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	replicaBounds replicaBounds,
) error {
	scaledObjectList := &unstructured.UnstructuredList{}

	scaledObjectList.SetGroupVersionKind(
		scaledObjectGroupVersionKind.GroupVersion().WithKind(
			scaledObjectGroupVersionKind.Kind + "List",
		),
	)

	logger := logf.FromContext(context)

	if error := liferayEnvironmentReconciler.List(
		context, scaledObjectList, client.InNamespace(liferayEnvironment.Namespace),
	); error != nil {
		if meta.IsNoMatchError(error) {
			logger.V(1).Info("KEDA is not installed; skipping ScaledObject enforcement")

			return nil
		}

		return error
	}

	for index := range scaledObjectList.Items {
		scaledObject := &scaledObjectList.Items[index]

		scaledObjectSpec, error := readScaledObjectSpec(scaledObject)

		if error != nil {
			return error
		}

		if !isScalingWorkload(
			scaledObjectSpec.ScaleTargetRef.Kind,
			liferayEnvironment,
			scaledObjectSpec.ScaleTargetRef.Name,
		) {

			continue
		}

		annotations := scaledObject.GetAnnotations()

		patchAnnotations := map[string]any{}

		if replicaBounds.Paused && annotations[annotationPausedReplicas] != "0" {
			patchAnnotations[annotationPausedReplicas] = "0"
			patchAnnotations[annotationPausedReplicasOwner] = "true"
		}

		if !replicaBounds.Paused && annotations[annotationPausedReplicasOwner] == "true" {
			patchAnnotations[annotationPausedReplicas] = nil
			patchAnnotations[annotationPausedReplicasOwner] = nil
		}

		if len(patchAnnotations) == 0 &&
			scaledObjectSpec.MaxReplicaCount != nil &&
			*scaledObjectSpec.MaxReplicaCount == replicaBounds.Maximum &&
			scaledObjectSpec.MinReplicaCount != nil &&
			*scaledObjectSpec.MinReplicaCount == replicaBounds.Minimum {

			continue
		}

		patchJSON, error := json.Marshal(
			map[string]any{
				"metadata": map[string]any{
					"annotations": patchAnnotations,
				},
				"spec": map[string]any{
					"maxReplicaCount": replicaBounds.Maximum,
					"minReplicaCount": replicaBounds.Minimum,
				},
			},
		)

		if error != nil {
			return error
		}

		if error := liferayEnvironmentReconciler.Patch(
			context,
			scaledObject,
			client.RawPatch(types.MergePatchType, patchJSON),
			client.FieldOwner(fieldOwner),
		); error != nil {
			return error
		}

		logger.Info(
			"Licensed autoscaling ceiling was enforced",
			"autoscaler", scaledObject.GetName(),
			"maxReplicas", replicaBounds.Maximum,
			"minReplicas", replicaBounds.Minimum,
			"paused", replicaBounds.Paused,
		)
	}

	return nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enqueueScaleTargetEnvironments(
	context context.Context,
	object client.Object,
) []controllerruntime.Request {
	var kind string
	var name string

	switch autoscaler := object.(type) {
	case *autoscalingv2.HorizontalPodAutoscaler:
		kind = autoscaler.Spec.ScaleTargetRef.Kind
		name = autoscaler.Spec.ScaleTargetRef.Name
	case *unstructured.Unstructured:
		scaledObjectSpec, error := readScaledObjectSpec(autoscaler)

		if error != nil {
			logf.FromContext(context).Error(
				error, "Unable to read the scale target of an autoscaler",
				"autoscaler", object.GetName(),
			)

			return nil
		}

		kind = scaledObjectSpec.ScaleTargetRef.Kind
		name = scaledObjectSpec.ScaleTargetRef.Name
	}

	liferayEnvironmentList := &licensingv1alpha1.LiferayEnvironmentList{}

	if error := liferayEnvironmentReconciler.List(
		context, liferayEnvironmentList, client.InNamespace(object.GetNamespace()),
	); error != nil {
		logf.FromContext(context).Error(
			error, "Unable to list the environments an autoscaler scales",
			"autoscaler", object.GetName(),
		)

		return nil
	}

	var requests []controllerruntime.Request

	for index := range liferayEnvironmentList.Items {
		liferayEnvironment := &liferayEnvironmentList.Items[index]

		if !isScalingWorkload(kind, liferayEnvironment, name) {
			continue
		}

		requests = append(requests, controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      liferayEnvironment.Name,
				Namespace: liferayEnvironment.Namespace,
			},
		})
	}

	return requests
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) ensureScaledObjectWatch(
	context context.Context,
	object client.Object,
) []controllerruntime.Request {
	scaledObjectWatch := liferayEnvironmentReconciler.scaledObjectWatch

	scaledObjectWatch.mutex.Lock()

	defer scaledObjectWatch.mutex.Unlock()

	if scaledObjectWatch.added {
		return nil
	}

	logger := logf.FromContext(context)

	if _, error := scaledObjectWatch.manager.GetRESTMapper().RESTMapping(
		scaledObjectGroupVersionKind.GroupKind(),
		scaledObjectGroupVersionKind.Version,
	); error != nil {
		if !meta.IsNoMatchError(error) {
			logger.Error(error, "Unable to resolve the ScaledObject API")
		}

		return nil
	}

	scaledObject := &unstructured.Unstructured{}

	scaledObject.SetGroupVersionKind(scaledObjectGroupVersionKind)

	if error := scaledObjectWatch.controller.Watch(
		source.Kind(
			scaledObjectWatch.manager.GetCache(),
			client.Object(scaledObject),
			handler.EnqueueRequestsFromMapFunc(
				liferayEnvironmentReconciler.enqueueScaleTargetEnvironments,
			),
			predicate.GenerationChangedPredicate{},
		),
	); error != nil {
		logger.Error(error, "Unable to watch ScaledObjects")

		return nil
	}

	scaledObjectWatch.added = true

	logger.Info("Watching ScaledObjects", "customResourceDefinition", object.GetName())

	return nil
}

func isOwnedByScaledObject(object client.Object) bool {
	for _, ownerReference := range object.GetOwnerReferences() {
		groupVersion, error := schema.ParseGroupVersion(ownerReference.APIVersion)

		if error != nil {
			continue
		}

		if ownerReference.Kind == scaledObjectGroupVersionKind.Kind &&
			groupVersion.Group == scaledObjectGroupVersionKind.Group {

			return true
		}
	}

	return false
}

func isScalingWorkload(
	kind string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	name string,
) bool {
	return kind == string(liferayEnvironment.Spec.WorkloadRef.EffectiveKind()) &&
		name == liferayEnvironment.Spec.WorkloadRef.Name
}

func readScaledObjectSpec(
	scaledObject *unstructured.Unstructured,
) (scaledObjectSpec, error) {
	var scaledObjectSpec scaledObjectSpec

	specMap, _, error := unstructured.NestedMap(scaledObject.Object, "spec")

	if error != nil {
		return scaledObjectSpec, error
	}

	if error := runtime.DefaultUnstructuredConverter.FromUnstructured(
		specMap, &scaledObjectSpec,
	); error != nil {
		return scaledObjectSpec, fmt.Errorf(
			"scaled object %q: %w", scaledObject.GetName(), error,
		)
	}

	if scaledObjectSpec.ScaleTargetRef.Kind == "" {
		scaledObjectSpec.ScaleTargetRef.Kind = string(licensingv1alpha1.WorkloadKindDeployment)
	}

	return scaledObjectSpec, nil
}

type replicaBounds struct {
	Maximum int32
	Minimum int32
	Paused  bool
}

type scaledObjectScaleTargetRef struct {
	Kind string `json:"kind,omitempty"`
	Name string `json:"name"`
}

type scaledObjectSpec struct {
	MaxReplicaCount *int32                     `json:"maxReplicaCount,omitempty"`
	MinReplicaCount *int32                     `json:"minReplicaCount,omitempty"`
	ScaleTargetRef  scaledObjectScaleTargetRef `json:"scaleTargetRef"`
}

type scaledObjectWatch struct {
	added      bool
	controller controller.Controller
	manager    controllerruntime.Manager
	mutex      sync.Mutex
}

var scaledObjectGroupVersionKind = schema.GroupVersionKind{
	Group:   "keda.sh",
	Kind:    "ScaledObject",
	Version: "v1alpha1",
}
