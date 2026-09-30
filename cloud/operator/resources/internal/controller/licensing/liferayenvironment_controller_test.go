package licensing

import (
	"bytes"
	"context"
	"crypto/rsa"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"slices"
	"strings"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	appsv1 "k8s.io/api/apps/v1"
	autoscalingv1 "k8s.io/api/autoscaling/v1"
	autoscalingv2 "k8s.io/api/autoscaling/v2"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	unstructured "k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	runtime "k8s.io/apimachinery/pkg/runtime"
	schema "k8s.io/apimachinery/pkg/runtime/schema"
	types "k8s.io/apimachinery/pkg/types"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
	controllerconfig "sigs.k8s.io/controller-runtime/pkg/config"
	envtest "sigs.k8s.io/controller-runtime/pkg/envtest"
	metricsserver "sigs.k8s.io/controller-runtime/pkg/metrics/server"
)

func (stubProvisioning *stubProvisioning) Activate(
	activationRequest provisioning.ActivationRequest,
	context context.Context,
	privateKey *rsa.PrivateKey,
) error {
	stubProvisioning.activateCalled = true

	return stubProvisioning.activateError
}

func (stubProvisioning *stubProvisioning) DownloadAddOn(
	context context.Context,
	downloadRequest provisioning.DownloadRequest,
	privateKey *rsa.PrivateKey,
) (io.ReadCloser, error) {
	stubProvisioning.downloadCalled = true

	if stubProvisioning.downloadError != nil {
		return nil, stubProvisioning.downloadError
	}

	return io.NopCloser(bytes.NewReader(stubProvisioning.downloadBody)), nil
}

func (stubProvisioning *stubProvisioning) Manifest(
	context context.Context,
	manifestRequest provisioning.ManifestRequest,
	privateKey *rsa.PrivateKey,
) (*provisioning.Entitlements, error) {
	stubProvisioning.manifestCalled = true

	return stubProvisioning.entitlements, stubProvisioning.manifestError
}

func (inlineRunner inlineRunner) Run(task func()) {
	task()
}

func TestCapReplicaBounds(t *testing.T) {
	testCases := map[string]struct {
		autoscaling           *licensingv1alpha1.Autoscaling
		expectedReplicaBounds replicaBounds
		replicaCeiling        int32
	}{
		"caps the maximum at the replica ceiling": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			expectedReplicaBounds: replicaBounds{Maximum: 3, Minimum: 1},
			replicaCeiling:        3,
		},
		"caps the minimum at the capped maximum": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 5},
			expectedReplicaBounds: replicaBounds{Maximum: 3, Minimum: 3},
			replicaCeiling:        3,
		},
		"floors the maximum at one replica when the ceiling is zero": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 2},
			expectedReplicaBounds: replicaBounds{Maximum: 1, Minimum: 1},
			replicaCeiling:        0,
		},
		"keeps bounds within the replica ceiling": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 4, MinReplicas: 2},
			expectedReplicaBounds: replicaBounds{Maximum: 4, Minimum: 2},
			replicaCeiling:        5,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			actualReplicaBounds := capReplicaBounds(
				testCase.autoscaling, testCase.replicaCeiling,
			)

			if actualReplicaBounds != testCase.expectedReplicaBounds {
				t.Errorf(
					"capReplicaBounds = %+v, want %+v",
					actualReplicaBounds, testCase.expectedReplicaBounds,
				)
			}
		})
	}
}

func TestEnforceAutoscalerCeiling(t *testing.T) {
	testCases := map[string]struct {
		autoscaling         *licensingv1alpha1.Autoscaling
		expectedMaxReplicas int32
		expectedMinReplicas int32
		liveMaxReplicas     int32
		liveMinReplicas     int32
		replicaCeiling      int32
	}{
		"caps the maximum at the licensed ceiling": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			expectedMaxReplicas: 3,
			expectedMinReplicas: 1,
			liveMaxReplicas:     10,
			liveMinReplicas:     1,
			replicaCeiling:      3,
		},
		"caps the minimum at the licensed ceiling": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 5},
			expectedMaxReplicas: 3,
			expectedMinReplicas: 3,
			liveMaxReplicas:     10,
			liveMinReplicas:     5,
			replicaCeiling:      3,
		},
		"floors the maximum at one replica when the ceiling is zero": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 2},
			expectedMaxReplicas: 1,
			expectedMinReplicas: 1,
			liveMaxReplicas:     10,
			liveMinReplicas:     2,
			replicaCeiling:      0,
		},
		"leaves bounds within the licensed ceiling": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 4, MinReplicas: 2},
			expectedMaxReplicas: 4,
			expectedMinReplicas: 2,
			liveMaxReplicas:     4,
			liveMinReplicas:     2,
			replicaCeiling:      5,
		},
		"leaves the autoscaler alone without requested bounds": {
			autoscaling:         nil,
			expectedMaxReplicas: 10,
			expectedMinReplicas: 1,
			liveMaxReplicas:     10,
			liveMinReplicas:     1,
			replicaCeiling:      3,
		},
		"raises a capped maximum after a license upgrade": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			expectedMaxReplicas: 5,
			expectedMinReplicas: 1,
			liveMaxReplicas:     3,
			liveMinReplicas:     1,
			replicaCeiling:      5,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "dev",
					Namespace: "liferay-dev",
				},
				Spec: licensingv1alpha1.LiferayEnvironmentSpec{
					Autoscaling: testCase.autoscaling,
					WorkloadRef: licensingv1alpha1.WorkloadRef{
						Name: "dev-liferay",
					},
				},
			}

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(
					t,
					liferayEnvironment,
					newHorizontalPodAutoscaler(
						testCase.liveMaxReplicas, testCase.liveMinReplicas, "dev-liferay",
					),
					newScaledObject(
						testCase.liveMaxReplicas, testCase.liveMinReplicas, "dev-liferay",
					),
				),
			}

			if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
				context.Background(), liferayEnvironment, testCase.replicaCeiling,
			); error != nil {
				t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
			}

			horizontalPodAutoscaler := getHorizontalPodAutoscaler(
				liferayEnvironmentReconciler, t,
			)

			assertReplicasEqual(
				&horizontalPodAutoscaler.Spec.MaxReplicas,
				&testCase.expectedMaxReplicas,
				"horizontalPodAutoscaler.spec.maxReplicas",
				t,
			)

			assertReplicasEqual(
				horizontalPodAutoscaler.Spec.MinReplicas,
				&testCase.expectedMinReplicas,
				"horizontalPodAutoscaler.spec.minReplicas",
				t,
			)

			scaledObject := getScaledObject(liferayEnvironmentReconciler, t)

			assertScaledObjectReplicaCount(
				int64(testCase.expectedMaxReplicas),
				"maxReplicaCount",
				scaledObject,
				t,
			)

			assertScaledObjectReplicaCount(
				int64(testCase.expectedMinReplicas),
				"minReplicaCount",
				scaledObject,
				t,
			)
		})
	}
}

func TestEnforceAutoscalerCeilingIgnoresOtherAutoscalers(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	kedaHorizontalPodAutoscaler := newHorizontalPodAutoscaler(10, 1, "dev-liferay")

	kedaHorizontalPodAutoscaler.Name = "keda-hpa-dev-liferay"
	kedaHorizontalPodAutoscaler.OwnerReferences = []metav1.OwnerReference{
		{
			APIVersion: "keda.sh/v1alpha1",
			Kind:       "ScaledObject",
			Name:       "dev-liferay",
			UID:        "scaled-object-uid",
		},
	}

	otherHorizontalPodAutoscaler := newHorizontalPodAutoscaler(10, 1, "other-workload")

	otherHorizontalPodAutoscaler.Name = "other-workload"

	otherScaledObject := newScaledObject(10, 1, "other-workload")

	otherScaledObject.SetName("other-workload")

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClient(
			t,
			kedaHorizontalPodAutoscaler,
			liferayEnvironment,
			otherHorizontalPodAutoscaler,
			otherScaledObject,
		),
	}

	if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
	}

	for _, name := range []string{"keda-hpa-dev-liferay", "other-workload"} {
		horizontalPodAutoscaler := &autoscalingv2.HorizontalPodAutoscaler{}

		if error := liferayEnvironmentReconciler.Get(
			context.Background(),
			types.NamespacedName{Name: name, Namespace: "liferay-dev"},
			horizontalPodAutoscaler,
		); error != nil {
			t.Fatalf("Unable to read the autoscaler %q: %v", name, error)
		}

		if horizontalPodAutoscaler.Spec.MaxReplicas != 10 {
			t.Errorf(
				"Autoscaler %q spec.maxReplicas = %d, want 10",
				name, horizontalPodAutoscaler.Spec.MaxReplicas,
			)
		}
	}

	scaledObject := newScaledObject(0, 0, "")

	if error := liferayEnvironmentReconciler.Get(
		context.Background(),
		types.NamespacedName{Name: "other-workload", Namespace: "liferay-dev"},
		scaledObject,
	); error != nil {
		t.Fatalf("Unable to read the scaled object: %v", error)
	}

	assertScaledObjectReplicaCount(10, "maxReplicaCount", scaledObject, t)
}

func TestEnforceAutoscalerCeilingSetsMissingScaledObjectBounds(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	scaledObject := newScaledObject(0, 0, "dev-liferay")

	unstructured.RemoveNestedField(scaledObject.Object, "spec", "maxReplicaCount")
	unstructured.RemoveNestedField(scaledObject.Object, "spec", "minReplicaCount")

	if error := unstructured.SetNestedField(
		scaledObject.Object, int64(30), "spec", "pollingInterval",
	); error != nil {
		t.Fatalf("Unable to set scaledObject.spec.pollingInterval: %v", error)
	}

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClient(t, liferayEnvironment, scaledObject),
	}

	if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
	}

	scaledObject = getScaledObject(liferayEnvironmentReconciler, t)

	assertScaledObjectReplicaCount(3, "maxReplicaCount", scaledObject, t)

	assertScaledObjectReplicaCount(1, "minReplicaCount", scaledObject, t)

	pollingInterval, found, error := unstructured.NestedInt64(
		scaledObject.Object, "spec", "pollingInterval",
	)

	if error != nil || !found || pollingInterval != 30 {
		t.Errorf(
			"scaledObject.spec.pollingInterval = %d (found %t, error %v), want 30",
			pollingInterval, found, error,
		)
	}
}

func TestEnforceAutoscalerCeilingSkipsMissingKEDA(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	fakeClient := fake.NewClientBuilder().WithInterceptorFuncs(
		interceptor.Funcs{
			List: func(
				context context.Context,
				writer client.WithWatch,
				list client.ObjectList,
				options ...client.ListOption,
			) error {
				if _, ok := list.(*unstructured.UnstructuredList); ok {
					return &meta.NoKindMatchError{
						GroupKind: scaledObjectGroupVersionKind.GroupKind(),
					}
				}

				return writer.List(context, list, options...)
			},
		},
	).WithObjects(
		liferayEnvironment, newHorizontalPodAutoscaler(10, 1, "dev-liferay"),
	).WithScheme(
		newScheme(t),
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: fakeClient,
	}

	if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
	}

	horizontalPodAutoscaler := getHorizontalPodAutoscaler(liferayEnvironmentReconciler, t)

	if horizontalPodAutoscaler.Spec.MaxReplicas != 3 {
		t.Errorf(
			"spec.maxReplicas = %d, want 3",
			horizontalPodAutoscaler.Spec.MaxReplicas,
		)
	}
}

func TestEnforceReplicaCeiling(t *testing.T) {
	testCases := map[string]struct {
		autoscaling       *licensingv1alpha1.Autoscaling
		desiredReplicas   *int32
		expectedCondition metav1.ConditionStatus
		expectedEffective *int32
		expectedReason    string
		expectedReplicas  *int32
		replicaCeiling    int32
		workloadExists    bool
		workloadReplicas  *int32
	}{
		"caps an autoscaling maximum above the licensed maximum": {
			autoscaling:       &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			desiredReplicas:   nil,
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(2),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(2),
			replicaCeiling:    3,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(2),
		},
		"caps replicas above the licensed maximum": {
			desiredReplicas:   pointerInt32(5),
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(3),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(3),
			replicaCeiling:    3,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(5),
		},
		"caps the current replicas when desired is unset": {
			desiredReplicas:   nil,
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(2),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(2),
			replicaCeiling:    2,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(4),
		},
		"defaults to a single replica when neither desired nor current is set": {
			desiredReplicas:   nil,
			expectedCondition: metav1.ConditionTrue,
			expectedEffective: pointerInt32(1),
			expectedReason:    "WithinLicensedLimit",
			expectedReplicas:  pointerInt32(1),
			replicaCeiling:    5,
			workloadExists:    true,
			workloadReplicas:  nil,
		},
		"downgrades to zero when the ceiling is zero": {
			desiredReplicas:   pointerInt32(3),
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(0),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(0),
			replicaCeiling:    0,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(3),
		},
		"reports when the workload does not exist": {
			desiredReplicas:   pointerInt32(3),
			expectedCondition: metav1.ConditionUnknown,
			expectedEffective: nil,
			expectedReason:    "WorkloadNotFound",
			expectedReplicas:  nil,
			replicaCeiling:    3,
			workloadExists:    false,
			workloadReplicas:  nil,
		},
		"scales a running workload down to the ceiling": {
			desiredReplicas:   pointerInt32(5),
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(3),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(3),
			replicaCeiling:    3,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(5),
		},
		"within the licensed autoscaling limit": {
			autoscaling:       &licensingv1alpha1.Autoscaling{MaxReplicas: 3, MinReplicas: 1},
			desiredReplicas:   nil,
			expectedCondition: metav1.ConditionTrue,
			expectedEffective: pointerInt32(2),
			expectedReason:    "WithinLicensedLimit",
			expectedReplicas:  pointerInt32(2),
			replicaCeiling:    3,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(2),
		},
		"within the licensed limit": {
			desiredReplicas:   pointerInt32(2),
			expectedCondition: metav1.ConditionTrue,
			expectedEffective: pointerInt32(2),
			expectedReason:    "WithinLicensedLimit",
			expectedReplicas:  pointerInt32(2),
			replicaCeiling:    3,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(2),
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "dev",
					Namespace: "liferay-dev",
				},
				Spec: licensingv1alpha1.LiferayEnvironmentSpec{
					Autoscaling:     testCase.autoscaling,
					DesiredReplicas: testCase.desiredReplicas,
					WorkloadRef: licensingv1alpha1.WorkloadRef{
						Name: "dev-liferay",
					},
				},
			}

			objects := []client.Object{liferayEnvironment}

			if testCase.workloadExists {
				objects = append(objects, &appsv1.StatefulSet{
					ObjectMeta: metav1.ObjectMeta{
						Name:      "dev-liferay",
						Namespace: "liferay-dev",
					},
					Spec: appsv1.StatefulSetSpec{
						Replicas: testCase.workloadReplicas,
					},
				})
			}

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(t, objects...),
			}

			if _, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
				context.Background(), liferayEnvironment, testCase.replicaCeiling,
			); error != nil {
				t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
			}

			assertReplicasEqual(
				liferayEnvironment.Status.EffectiveReplicas,
				testCase.expectedEffective,
				"status.effectiveReplicas",
				t,
			)

			condition := meta.FindStatusCondition(
				liferayEnvironment.Status.Conditions, conditionReplicasCountValid,
			)

			if condition == nil {
				t.Fatalf("Expected a %s condition, got none", conditionReplicasCountValid)
			}

			if condition.Status != testCase.expectedCondition {
				t.Errorf(
					"Condition status = %q, want %q",
					condition.Status, testCase.expectedCondition,
				)
			}

			if condition.Reason != testCase.expectedReason {
				t.Errorf(
					"Condition reason = %q, want %q",
					condition.Reason, testCase.expectedReason,
				)
			}

			if !testCase.workloadExists {
				return
			}

			statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

			assertReplicasEqual(
				statefulSet.Spec.Replicas, testCase.expectedReplicas,
				"statefulSet.spec.replicas", t,
			)
		})
	}
}

func TestEnforceReplicaCeilingPersistsCeilingBeforeWritingWorkload(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			DesiredReplicas: pointerInt32(3),
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	statefulSet := &appsv1.StatefulSet{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev-liferay",
			Namespace: "liferay-dev",
		},
		Spec: appsv1.StatefulSetSpec{
			Replicas: pointerInt32(1),
		},
	}

	var observedCeiling *int32

	observedWorkloadWrite := false

	fakeClient := fake.NewClientBuilder().WithInterceptorFuncs(
		interceptor.Funcs{
			Update: func(
				context context.Context,
				writer client.WithWatch,
				object client.Object,
				options ...client.UpdateOption,
			) error {
				if _, ok := object.(*appsv1.StatefulSet); ok {
					stored := &licensingv1alpha1.LiferayEnvironment{}

					if error := writer.Get(
						context, types.NamespacedName{
							Name:      "dev",
							Namespace: "liferay-dev",
						}, stored,
					); error != nil {
						return error
					}

					observedCeiling = stored.Status.License.MaxClusterNodes
					observedWorkloadWrite = true
				}

				return writer.Update(context, object, options...)
			},
		},
	).WithObjects(
		liferayEnvironment, statefulSet,
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{Client: fakeClient}

	liferayEnvironment.Status.License.MaxClusterNodes = pointerInt32(3)

	if _, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
	}

	if !observedWorkloadWrite {
		t.Fatal("Expected the workload to be written")
	}

	if observedCeiling == nil {
		t.Fatal("Expected the ceiling to be persisted before the workload was written, got nil")
	}

	if *observedCeiling != 3 {
		t.Errorf("Persisted ceiling = %d, want 3", *observedCeiling)
	}
}

func TestEnforceReplicaCeilingPersistsRefusalWhenAutoscalerUpdateRejected(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	statefulSet := &appsv1.StatefulSet{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev-liferay",
			Namespace: "liferay-dev",
		},
		Spec: appsv1.StatefulSetSpec{
			Replicas: pointerInt32(5),
		},
	}

	fakeClient := fake.NewClientBuilder().WithInterceptorFuncs(
		interceptor.Funcs{
			Patch: func(
				context context.Context,
				writer client.WithWatch,
				object client.Object,
				patch client.Patch,
				options ...client.PatchOption,
			) error {
				if _, ok := object.(*autoscalingv2.HorizontalPodAutoscaler); ok {
					return errors.NewForbidden(
						schema.GroupResource{
							Group:    "autoscaling",
							Resource: "horizontalpodautoscalers",
						},
						"dev-liferay",
						fmt.Errorf("denied by RBAC"),
					)
				}

				return writer.Patch(context, object, patch, options...)
			},
		},
	).WithObjects(
		liferayEnvironment,
		newHorizontalPodAutoscaler(10, 1, "dev-liferay"),
		statefulSet,
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client:            fakeClient,
		RetryInitialDelay: 30 * time.Second,
	}

	requeueAfter, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context.Background(), liferayEnvironment, 3,
	)

	if error != nil {
		t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
	}

	if requeueAfter != 30*time.Second {
		t.Errorf("requeueAfter = %v, want %v", requeueAfter, 30*time.Second)
	}

	assertReplicasEqual(
		getStatefulSet(liferayEnvironmentReconciler, t).Spec.Replicas,
		pointerInt32(3),
		"statefulSet.spec.replicas",
		t,
	)

	condition := meta.FindStatusCondition(
		getEnvironment(liferayEnvironmentReconciler, t).Status.Conditions,
		conditionReplicasCountValid,
	)

	if condition == nil {
		t.Fatal("Expected a persisted ReplicasCountValid condition, got none")
	}

	if condition.Reason != "AutoscalerUpdateRejected" {
		t.Errorf(
			"Persisted reason = %q, want %q",
			condition.Reason, "AutoscalerUpdateRejected",
		)
	}
}

func TestEnforceReplicaCeilingPersistsRefusalWhenWorkloadUpdateRejected(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			DesiredReplicas: pointerInt32(3),
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	statefulSet := &appsv1.StatefulSet{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev-liferay",
			Namespace: "liferay-dev",
		},
		Spec: appsv1.StatefulSetSpec{
			Replicas: pointerInt32(1),
		},
	}

	fakeClient := fake.NewClientBuilder().WithInterceptorFuncs(
		interceptor.Funcs{
			Update: func(
				context context.Context,
				writer client.WithWatch,
				object client.Object,
				options ...client.UpdateOption,
			) error {
				if _, ok := object.(*appsv1.StatefulSet); ok {
					return errors.NewForbidden(
						schema.GroupResource{Group: "apps", Resource: "statefulsets"},
						"dev-liferay",
						fmt.Errorf("denied by the admission policy"),
					)
				}

				return writer.Update(context, object, options...)
			},
		},
	).WithObjects(
		liferayEnvironment, statefulSet,
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client:            fakeClient,
		RetryInitialDelay: 30 * time.Second,
	}

	requeueAfter, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context.Background(), liferayEnvironment, 3,
	)

	if error != nil {
		t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
	}

	if requeueAfter != 30*time.Second {
		t.Errorf("requeueAfter = %v, want %v", requeueAfter, 30*time.Second)
	}

	stored := &licensingv1alpha1.LiferayEnvironment{}

	if error := fakeClient.Get(
		context.Background(), types.NamespacedName{
			Name:      "dev",
			Namespace: "liferay-dev",
		}, stored,
	); error != nil {
		t.Fatalf("Unable to read back the environment: %v", error)
	}

	condition := meta.FindStatusCondition(
		stored.Status.Conditions, conditionReplicasCountValid,
	)

	if condition == nil {
		t.Fatal("Expected a persisted ReplicasCountValid condition, got none")
	}

	if condition.Reason != "WorkloadUpdateRejected" {
		t.Errorf(
			"Persisted reason = %q, want %q",
			condition.Reason, "WorkloadUpdateRejected",
		)
	}
}

func TestEnqueueScaleTargetEnvironments(t *testing.T) {
	deploymentScaledObject := newScaledObject(10, 1, "dev-liferay")

	unstructured.RemoveNestedField(
		deploymentScaledObject.Object, "spec", "scaleTargetRef", "kind",
	)

	testCases := map[string]struct {
		autoscaler     client.Object
		expectEnqueued bool
	}{
		"horizontal pod autoscaler scaling another workload": {
			autoscaler:     newHorizontalPodAutoscaler(10, 1, "other-workload"),
			expectEnqueued: false,
		},
		"horizontal pod autoscaler scaling the workload": {
			autoscaler:     newHorizontalPodAutoscaler(10, 1, "dev-liferay"),
			expectEnqueued: true,
		},
		"scaled object scaling a deployment of the same name": {
			autoscaler:     deploymentScaledObject,
			expectEnqueued: false,
		},
		"scaled object scaling another workload": {
			autoscaler:     newScaledObject(10, 1, "other-workload"),
			expectEnqueued: false,
		},
		"scaled object scaling the workload": {
			autoscaler:     newScaledObject(10, 1, "dev-liferay"),
			expectEnqueued: true,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(
					t,
					&licensingv1alpha1.LiferayEnvironment{
						ObjectMeta: metav1.ObjectMeta{
							Name:      "dev",
							Namespace: "liferay-dev",
						},
						Spec: licensingv1alpha1.LiferayEnvironmentSpec{
							WorkloadRef: licensingv1alpha1.WorkloadRef{
								Name: "dev-liferay",
							},
						},
					},
				),
			}

			requests := liferayEnvironmentReconciler.enqueueScaleTargetEnvironments(
				context.Background(), testCase.autoscaler,
			)

			var expectedRequests []controllerruntime.Request

			if testCase.expectEnqueued {
				expectedRequests = []controllerruntime.Request{
					{
						NamespacedName: types.NamespacedName{
							Name:      "dev",
							Namespace: "liferay-dev",
						},
					},
				}
			}

			if !slices.Equal(requests, expectedRequests) {
				t.Errorf(
					"enqueueScaleTargetEnvironments = %v, want %v",
					requests, expectedRequests,
				)
			}
		})
	}
}

func TestExtractLiferayImageTag(t *testing.T) {
	testCases := map[string]struct {
		image string
		want  string
	}{
		"a digest":               {image: "liferay/dxp@sha256:abc123", want: ""},
		"a plain tag":            {image: "liferay/dxp:2026.q3.0", want: "2026.q3.0"},
		"a plain tag with lts":   {image: "liferay/dxp:2026.q1.11-lts", want: "2026.q1.11-lts"},
		"a port and no tag":      {image: "registry:5000/liferay/dxp", want: ""},
		"a registry with a port": {image: "registry:5000/liferay/dxp:2026.q3.0", want: "2026.q3.0"},
		"a tag and a digest":     {image: "liferay/dxp:2026.q3.0@sha256:abc123", want: "2026.q3.0"},
		"no tag at all":          {image: "liferay/dxp", want: ""},
		"wrong image repo":       {image: "liferaycloud/dxp:2026.q3.0", want: ""},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			if got := extractLiferayImageTag(testCase.image); got != testCase.want {
				t.Errorf("extractLiferayImageTag(%q) = %q, want %q", testCase.image, got, testCase.want)
			}
		})
	}
}

func TestReconcileBacksOffFailedAddOn(t *testing.T) {
	body := []byte("PK\x03\x04 sample lpkg")

	sum := sha256.Sum256(body)

	checksum := hex.EncodeToString(sum[:])

	nextRetry := metav1.NewTime(metav1.Now().Add(5 * time.Minute))

	provisioningClient := &stubProvisioning{
		downloadBody: body,
		entitlements: addOnEntitlements(checksum),
	}

	liferayEnvironmentReconciler, result := reconcileEnvironment(
		provisioningClient, t,
		developmentObjectsWithApps(
			[]licensingv1alpha1.AppStatus{
				{
					Checksum:            checksum,
					ConsecutiveFailures: 2,
					NextRetry:           &nextRetry,
					State:               "Failed",
					VirtualEntryID:      77,
				},
			},
		)...,
	)

	if provisioningClient.downloadCalled {
		t.Error("DownloadAddOn was called; the add-on should still be backing off")
	}

	appStatus := getEnvironment(liferayEnvironmentReconciler, t).Status.Apps[0]

	if appStatus.State != "Failed" {
		t.Errorf("State = %q, want Failed while backing off", appStatus.State)
	}

	if appStatus.ConsecutiveFailures != 2 {
		t.Errorf("ConsecutiveFailures = %d, want 2 unchanged", appStatus.ConsecutiveFailures)
	}

	if (result.RequeueAfter <= 0) || (result.RequeueAfter > 5*time.Minute) {
		t.Errorf("RequeueAfter = %s, want within the 5m backoff", result.RequeueAfter)
	}
}

func TestReconcileBacksOffWhenActivationRejected(t *testing.T) {
	objects := []client.Object{
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&corev1.Secret{
			Data: map[string][]byte{
				"activationCode": []byte("one-time-code"),
			},
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-activation",
				Namespace: "liferay-dev",
			},
		},
		pendingEnvironment(),
	}

	provisioningClient := &stubProvisioning{
		activateError: fmt.Errorf("provisioning: activation code rejected"),
	}

	liferayEnvironmentReconciler, result := reconcileEnvironment(provisioningClient, t, objects...)

	if result.RequeueAfter != 30*time.Second {
		t.Errorf("RequeueAfter = %s, want the initial backoff 30s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.ConsecutiveFailures != 1 {
		t.Errorf("ConsecutiveFailures = %d, want 1", liferayEnvironment.Status.ConsecutiveFailures)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse || condition.Reason != "ActivationRejected" {
		t.Errorf("Activated condition = %v, want False/ActivationRejected", condition)
	}

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf("Phase = %q, want Degraded", liferayEnvironment.Status.Phase)
	}
}

func TestReconcileDowngradesAfterGracePeriod(t *testing.T) {
	unreachableSince := metav1.NewTime(time.Now().Add(-8 * 24 * time.Hour))

	environment := activatedEnvironment()
	environment.Spec.Autoscaling = &licensingv1alpha1.Autoscaling{
		MaxReplicas: 10,
		MinReplicas: 2,
	}
	environment.Status.ConsecutiveFailures = 50
	environment.Status.UnreachableSince = &unreachableSince

	meta.SetStatusCondition(
		&environment.Status.Conditions,
		metav1.Condition{
			Reason: "EntitlementsFetchFailed",
			Status: metav1.ConditionFalse,
			Type:   conditionProvisioningReachable,
		},
	)

	objects := []client.Object{
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(3),
			},
		},
		&corev1.Secret{
			Data: map[string][]byte{
				"license.xml": []byte("<license>known-good</license>"),
			},
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-entitlements",
				Namespace: "liferay-dev",
			},
		},
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
		newHorizontalPodAutoscaler(10, 2, "dev-liferay"),
	}

	provisioningClient := &stubProvisioning{
		manifestError: fmt.Errorf("provisioning: connection refused"),
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(provisioningClient, t, objects...)

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(1), "statefulSet.spec.replicas", t,
	)

	horizontalPodAutoscaler := getHorizontalPodAutoscaler(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		&horizontalPodAutoscaler.Spec.MaxReplicas,
		pointerInt32(1),
		"horizontalPodAutoscaler.spec.maxReplicas",
		t,
	)

	assertReplicasEqual(
		horizontalPodAutoscaler.Spec.MinReplicas,
		pointerInt32(1),
		"horizontalPodAutoscaler.spec.minReplicas",
		t,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if !meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionGracePeriodExpired,
	) {
		t.Error("GracePeriodExpired condition = not True, want True after the grace window")
	}

	if licenseXML := getLicenseXML(liferayEnvironmentReconciler, t); licenseXML != "<license>known-good</license>" {
		t.Errorf("license.xml = %q, should retain last-known-good", licenseXML)
	}

	recorder := liferayEnvironmentReconciler.Recorder.(*record.FakeRecorder)

	select {
	case event := <-recorder.Events:
		if !strings.Contains(event, "GracePeriodExpired") {
			t.Errorf("event = %q, should mention GracePeriodExpired", event)
		}
	default:
		t.Error("Expected a GracePeriodExpired warning event")
	}
}

func TestReconcileDownloadsAddOns(t *testing.T) {
	body := []byte("PK\x03\x04 sample lpkg")

	sum := sha256.Sum256(body)

	entitlements := &provisioning.Entitlements{
		AddOns: []provisioning.AddOn{
			{
				DownloadURL:    "https://example.com/marketplace/virtual-entry/77",
				ProductName:    "Sample Add-on",
				SHA256Checksum: hex.EncodeToString(sum[:]),
				VirtualEntryID: 77,
			},
		},
		LicenseXML: []byte(virtualClusterLicenseXML(
			"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
		)),
		MaxClusterNodes: 3,
	}

	liferayEnvironmentReconciler, result := reconcileEnvironment(
		&stubProvisioning{downloadBody: body, entitlements: entitlements}, t,
		developmentObjects()...,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if length := len(liferayEnvironment.Status.Apps); length != 1 {
		t.Fatalf("Status.Apps length = %d, want 1", length)
	}

	if state := liferayEnvironment.Status.Apps[0].State; state != "Downloading" {
		t.Errorf("State = %q, want Downloading on the first pass", state)
	}

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want the download poll interval", result.RequeueAfter)
	}

	result = reconcile(liferayEnvironmentReconciler, t)

	appStatus := getEnvironment(liferayEnvironmentReconciler, t).Status.Apps[0]

	if appStatus.Name != "Sample Add-on" {
		t.Errorf("Name = %q, want Sample Add-on", appStatus.Name)
	}

	if appStatus.State != "Downloaded" {
		t.Errorf("State = %q, want Downloaded on the second pass", appStatus.State)
	}

	if appStatus.VirtualEntryID != 77 {
		t.Errorf("VirtualEntryID = %d, want 77", appStatus.VirtualEntryID)
	}

	if result.RequeueAfter != 10*time.Minute {
		t.Errorf("RequeueAfter = %s, want the heartbeat 10m", result.RequeueAfter)
	}
}

func TestReconcileIsNotBlockedByAddOns(t *testing.T) {
	entitlements := &provisioning.Entitlements{
		AddOns: []provisioning.AddOn{
			{
				DownloadURL:    "://fake-url",
				ProductID:      "fake-app",
				SHA256Checksum: "0000",
			},
		},
		LicenseXML: []byte(virtualClusterLicenseXML(
			"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
		)),
		MaxClusterNodes: 3,
	}

	liferayEnvironmentReconciler, result := reconcileEnvironment(
		&stubProvisioning{entitlements: entitlements}, t, developmentObjects()...,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready despite the broken add-on", liferayEnvironment.Status.Phase)
	}

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want the download poll interval", result.RequeueAfter)
	}

	if length := len(getSecret("dev-entitlements", liferayEnvironmentReconciler, t).Data["add-ons.json"]); length == 0 {
		t.Error("add-ons.json was not written to the entitlements secret")
	}
}

func TestReconcileOfflineAwaitsMissingBundleFile(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		t.TempDir(), t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want 15s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Pending" {
		t.Errorf("Phase = %q, want Pending", liferayEnvironment.Status.Phase)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	)

	if condition == nil || condition.Reason != "AwaitingOfflineActivationBundle" {
		t.Errorf(
			"Activated condition = %v, want AwaitingOfflineActivationBundle", condition,
		)
	}
}

func TestReconcileOfflineAwaitsOfflineActivationBundle(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true

	provisioningClient := &stubProvisioning{}

	liferayEnvironmentReconciler, result := reconcileEnvironment(
		provisioningClient, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	if provisioningClient.activateCalled || provisioningClient.downloadCalled ||
		provisioningClient.manifestCalled {
		t.Errorf(
			"Offline reconcile made provisioning calls: activate=%v download=%v manifest=%v",
			provisioningClient.activateCalled,
			provisioningClient.downloadCalled,
			provisioningClient.manifestCalled,
		)
	}

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want 15s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Pending" {
		t.Errorf("Phase = %q, want Pending", liferayEnvironment.Status.Phase)
	}

	if liferayEnvironment.Status.ActivatedAt != nil {
		t.Errorf(
			"ActivatedAt = %v, want nil in offline mode before a bundle",
			liferayEnvironment.Status.ActivatedAt,
		)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	)

	if condition == nil {
		t.Fatal("Activated condition is nil, wanted value")
	}

	if condition.Reason != "AwaitingOfflineActivationBundle" {
		t.Errorf("Activated condition reason = %v, want AwaitingOfflineActivationBundle", condition.Reason)
	}

	if condition.Status != metav1.ConditionFalse {
		t.Errorf("Activated condition status = %v, want False", condition.Status)
	}
}

func TestReconcileOfflineExtractsAddOnsFromBundle(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	lpkgContent := "PK-fake-lpkg-content"

	checksum := sha256.Sum256([]byte(lpkgContent))

	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
	)

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app-1.lpkg": lpkgContent,
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [
						{
							"productId": "app-1",
							"productName": "App One",
							"virtualEntryId": 42,
							"sha256Checksum": %q
						}
					],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
				hex.EncodeToString(checksum[:]),
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		filepath.Join(marketplaceMountPath, "liferay-dev", "bundle.zip"),
		t,
	)

	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		marketplaceMountPath, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(3),
			},
		},
		environment,
	)

	if result.RequeueAfter != 10*time.Minute {
		t.Errorf(
			"RequeueAfter = %s, want the heartbeat 10m (offline extraction is synchronous)",
			result.RequeueAfter,
		)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready", liferayEnvironment.Status.Phase)
	}

	if length := len(liferayEnvironment.Status.Apps); length != 1 {
		t.Fatalf("Apps length = %d, want 1", length)
	}

	if state := liferayEnvironment.Status.Apps[0].State; state != "Downloaded" {
		t.Errorf("App state = %q, want Downloaded", state)
	}

	extracted := filepath.Join(marketplaceMountPath, "liferay-dev", "42.lpkg")

	if _, error := os.Stat(extracted); error != nil {
		t.Errorf("Expected the extracted lpkg at %s: %v", extracted, error)
	}
}

func TestReconcileOfflineLicensesFromBundle(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
	)

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app.lpkg": "PK-fake-lpkg",
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		filepath.Join(
			marketplaceMountPath, "liferay-dev", "bundle.zip",
		),
		t,
	)

	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		marketplaceMountPath, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(3),
			},
		},
		environment,
	)

	if result.RequeueAfter != 10*time.Minute {
		t.Errorf("RequeueAfter = %s, want the heartbeat 10m", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready", liferayEnvironment.Status.Phase)
	}

	if liferayEnvironment.Status.ActivatedAt == nil {
		t.Error("ActivatedAt = nil, want it set after licensing from the bundle")
	}

	if liferayEnvironment.Status.License.MaxClusterNodes == nil ||
		*liferayEnvironment.Status.License.MaxClusterNodes != 3 {
		t.Errorf(
			"License.MaxClusterNodes = %v, want 3",
			liferayEnvironment.Status.License.MaxClusterNodes,
		)
	}

	if activated := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	); activated == nil || activated.Status != metav1.ConditionTrue {
		t.Errorf("Activated condition = %v, want True", activated)
	}

	if licenseValid := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	); licenseValid == nil || licenseValid.Status != metav1.ConditionTrue {
		t.Errorf("LicenseValid condition = %v, want True", licenseValid)
	}

	if written := getLicenseXML(liferayEnvironmentReconciler, t); written != licenseXML {
		t.Errorf("entitlements license.xml = %q, want the bundle license", written)
	}
}

func TestReconcileOfflineRejectsInvalidBundle(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app.lpkg": "PK-fake-lpkg",
		},
		filepath.Join(
			marketplaceMountPath, "liferay-dev", "bundle.zip",
		),
		t,
	)

	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		marketplaceMountPath, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want 15s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf("Phase = %q, want Degraded", liferayEnvironment.Status.Phase)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse ||
		condition.Reason != "OfflineActivationBundleInvalid" {

		t.Errorf("Activated condition = %v, want False/OfflineActivationBundleInvalid", condition)
	}
}

func TestReconcileOfflineRequestIsWriteOnce(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{}, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	first := string(getSecret(
		"dev-identity", liferayEnvironmentReconciler, t,
	).Data["offline-request"])

	if _, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: "liferay-dev",
			},
		},
	); error != nil {
		t.Fatalf("Unexpected error on second reconcile: %v", error)
	}

	second := string(getSecret(
		"dev-identity", liferayEnvironmentReconciler, t,
	).Data["offline-request"])

	if first != second {
		t.Error("offline-request payload changed on re-reconcile; want write-once")
	}
}

func TestReconcileOfflineRestoresCeilingAfterOwnerMatches(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
	)

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app.lpkg": "PK-fake-lpkg",
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		filepath.Join(
			marketplaceMountPath, "liferay-dev", "bundle.zip",
		),
		t,
	)

	activatedAt := metav1.Now()

	environment := pendingEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"
	environment.Status.ActivatedAt = &activatedAt
	environment.Status.License.MaxClusterNodes = pointerInt32(0)
	environment.Status.Phase = "Degraded"

	meta.SetStatusCondition(
		&environment.Status.Conditions,
		metav1.Condition{
			Message: "License was issued for a different environment.",
			Reason:  "EnvironmentMismatch",
			Status:  metav1.ConditionFalse,
			Type:    conditionLicenseValid,
		},
	)

	meta.SetStatusCondition(
		&environment.Status.Conditions,
		metav1.Condition{
			Message: "Requested 3 replicas exceeds the licensed maximum of 0; capping to 0.",
			Reason:  "ExceedsLicensedMaximum",
			Status:  metav1.ConditionFalse,
			Type:    conditionReplicasCountValid,
		},
	)

	liferayEnvironmentReconciler, _ := reconcileOfflineActivationBundle(
		marketplaceMountPath, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(0),
			},
		},
		environment,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready after the owner matches again", liferayEnvironment.Status.Phase)
	}

	if liferayEnvironment.Status.License.MaxClusterNodes == nil ||
		*liferayEnvironment.Status.License.MaxClusterNodes != 3 {
		t.Errorf(
			"License.MaxClusterNodes = %v, want the restored 3",
			liferayEnvironment.Status.License.MaxClusterNodes,
		)
	}

	if licenseValid := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	); licenseValid == nil || licenseValid.Status != metav1.ConditionTrue ||
		licenseValid.Reason != "Valid" {

		t.Errorf("LicenseValid condition = %v, want True/Valid", licenseValid)
	}

	if replicasValid := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionReplicasCountValid,
	); replicasValid == nil || replicasValid.Status != metav1.ConditionTrue ||
		replicasValid.Reason != "WithinLicensedLimit" {

		t.Errorf(
			"ReplicasCountValid condition = %v, want True/WithinLicensedLimit", replicasValid,
		)
	}

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(3), "statefulSet.spec.replicas", t,
	)
}

func TestReconcileOfflineStoresRequestInIdentitySecret(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{}, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	payload := getSecret(
		"dev-identity", liferayEnvironmentReconciler, t,
	).Data["offline-request"]

	if len(payload) == 0 {
		t.Fatal("identity secret has no offline-request payload")
	}

	segments := strings.Split(string(payload), ".")

	if len(segments) != 3 {
		t.Fatalf("offline-request is not a JWT: got %d segments, want 3", len(segments))
	}

	claims, error := base64.RawURLEncoding.DecodeString(segments[1])

	if error != nil {
		t.Fatalf("Unable to decode the JWT payload segment: %v", error)
	}

	var claimsMap map[string]any

	if error := json.Unmarshal(claims, &claimsMap); error != nil {
		t.Errorf("JWT payload segment is not valid JSON: %v", error)
	}
}

func TestReconcileOrphansRemovedEntitlement(t *testing.T) {
	entitlements := &provisioning.Entitlements{
		LicenseXML: []byte(virtualClusterLicenseXML(
			"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
		)),
		MaxClusterNodes: 3,
	}

	provisioningClient := &stubProvisioning{entitlements: entitlements}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		provisioningClient, t,
		developmentObjectsWithApps(
			[]licensingv1alpha1.AppStatus{
				{
					Checksum:       "abc123",
					Name:           "Sample Add-on",
					State:          "Downloaded",
					VirtualEntryID: 77,
				},
			},
		)...,
	)

	if provisioningClient.downloadCalled {
		t.Error("DownloadAddOn was called for a removed entitlement")
	}

	appStatus := getEnvironment(liferayEnvironmentReconciler, t).Status.Apps[0]

	if appStatus.State != "Orphaned" {
		t.Errorf("State = %q, want Orphaned", appStatus.State)
	}

	if appStatus.VirtualEntryID != 77 {
		t.Errorf("VirtualEntryID = %d, want 77", appStatus.VirtualEntryID)
	}
}

func TestReconcilePersistsCeilingWhenWorkloadUpdateRejected(t *testing.T) {
	environment := activatedEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Status.License.MaxClusterNodes = pointerInt32(0)

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML: []byte(virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
			)),
			MaxClusterNodes: 3,
		},
	}

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClientEnforcingMax(
			t,
			&corev1.Namespace{
				ObjectMeta: metav1.ObjectMeta{
					Name: "liferay-dev",
					UID:  "dev-namespace-uid",
				},
			},
			&appsv1.StatefulSet{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "dev-liferay",
					Namespace: "liferay-dev",
				},
				Spec: appsv1.StatefulSetSpec{
					Replicas: pointerInt32(0),
				},
			},
			environment,
		),
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: t.TempDir(),
		Provisioning:         provisioningClient,
		Recorder:             record.NewFakeRecorder(10),
		Syncer: addon.NewSyncer(
			provisioningClient, 15*time.Second, 30*time.Second, 30*time.Minute,
			inlineRunner{},
		),
	}

	_, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: "liferay-dev",
			},
		},
	)

	if error != nil {
		t.Logf("The workload update was rejected: %v", error)
	}

	maxClusterNodes := getEnvironment(
		liferayEnvironmentReconciler, t,
	).Status.License.MaxClusterNodes

	if maxClusterNodes == nil {
		t.Fatal(
			"License.MaxClusterNodes = <nil>, want the licensed 3 persisted so that the next attempt is admitted",
		)
	}

	if *maxClusterNodes != 3 {
		t.Errorf(
			"License.MaxClusterNodes = %d, want the licensed 3 persisted so that the next attempt is admitted",
			*maxClusterNodes,
		)
	}

	assertReplicasEqual(
		getStatefulSet(liferayEnvironmentReconciler, t).Spec.Replicas,
		pointerInt32(3), "Replicas", t,
	)
}

func TestReconcilePersistsGracePeriodWhenWorkloadUpdateRejected(t *testing.T) {
	unreachableSince := metav1.NewTime(time.Now().Add(-2 * time.Hour))

	environment := activatedEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Status.License.MaxClusterNodes = pointerInt32(0)
	environment.Status.UnreachableSince = &unreachableSince

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClientEnforcingMax(
			t,
			&corev1.Namespace{
				ObjectMeta: metav1.ObjectMeta{
					Name: "liferay-dev",
					UID:  "dev-namespace-uid",
				},
			},
			&appsv1.StatefulSet{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "dev-liferay",
					Namespace: "liferay-dev",
				},
				Spec: appsv1.StatefulSetSpec{
					Replicas: pointerInt32(0),
				},
			},
			environment,
		),
		GracePeriod:          time.Hour,
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: t.TempDir(),
		Provisioning: &stubProvisioning{
			manifestError: fmt.Errorf("provisioning is unreachable"),
		},
		Recorder:          record.NewFakeRecorder(10),
		RetryInitialDelay: 30 * time.Second,
		RetryMaxDelay:     30 * time.Minute,
	}

	_, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: "liferay-dev",
			},
		},
	)

	if error != nil {
		t.Logf("The workload update was rejected: %v", error)
	}

	if phase := getEnvironment(
		liferayEnvironmentReconciler, t,
	).Status.Phase; phase != "Degraded" {
		t.Errorf(
			"Phase = %q, want Degraded persisted so that the grace period and the backoff survive the rejected write",
			phase,
		)
	}
}

func TestReconcileRejectsLicenseIssuedForAnotherEnvironment(t *testing.T) {
	entitlements := &provisioning.Entitlements{
		LicenseXML: []byte(virtualClusterLicenseXML(
			"Friday, March 2, 2029 12:00:00 AM GMT", 3, "some-other-environment-uid",
		)),
		MaxClusterNodes: 3,
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{entitlements: entitlements}, t, developmentObjects()...,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf("Phase = %q, want Degraded", liferayEnvironment.Status.Phase)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse ||
		condition.Reason != "EnvironmentMismatch" {

		t.Errorf("LicenseValid condition = %v, want False/EnvironmentMismatch", condition)
	}

	if maxClusterNodes := liferayEnvironment.Status.License.MaxClusterNodes; maxClusterNodes == nil ||
		*maxClusterNodes != 0 {

		t.Errorf("License.MaxClusterNodes = %v, want a clamped 0", maxClusterNodes)
	}

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(0), "statefulSet.spec.replicas", t,
	)
}

func TestReconcileReportsAddOnsNotReadyWhenDownloadFails(t *testing.T) {
	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{
			downloadError: fmt.Errorf("boom"),
			entitlements:  addOnEntitlements("abc123"),
		}, t,
		developmentObjects()...,
	)

	reconcile(liferayEnvironmentReconciler, t)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionAddOnsReady,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse || condition.Reason != "DownloadsFailing" {
		t.Errorf("AddOnsReady condition = %v, want False/DownloadsFailing", condition)
	}

	if !strings.Contains(condition.Message, "Sample Add-on") {
		t.Errorf(
			"AddOnsReady message = %q, want the failing add-on named",
			condition.Message,
		)
	}

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf(
			"Phase = %q, want Ready despite the failing add-on",
			liferayEnvironment.Status.Phase,
		)
	}

	licenseValid := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	)

	if licenseValid == nil || licenseValid.Status != metav1.ConditionTrue {
		t.Errorf("LicenseValid condition = %v, want True", licenseValid)
	}
}

func TestReconcileReportsAddOnsReadyWhenDownloaded(t *testing.T) {
	body := []byte("PK\x03\x04 sample lpkg")

	sum := sha256.Sum256(body)

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{
			downloadBody: body,
			entitlements: addOnEntitlements(hex.EncodeToString(sum[:])),
		}, t,
		developmentObjects()...,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionAddOnsReady,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse || condition.Reason != "Downloading" {
		t.Errorf(
			"AddOnsReady condition = %v, want False/Downloading on the first pass",
			condition,
		)
	}

	reconcile(liferayEnvironmentReconciler, t)

	condition = meta.FindStatusCondition(
		getEnvironment(liferayEnvironmentReconciler, t).Status.Conditions,
		conditionAddOnsReady,
	)

	if condition == nil || condition.Status != metav1.ConditionTrue || condition.Reason != "Downloaded" {
		t.Errorf(
			"AddOnsReady condition = %v, want True/Downloaded once cached",
			condition,
		)
	}
}

func TestReconcileResetsAddOnBackoffOnSuccess(t *testing.T) {
	body := []byte("PK\x03\x04 sample lpkg")

	sum := sha256.Sum256(body)

	checksum := hex.EncodeToString(sum[:])

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{downloadBody: body, entitlements: addOnEntitlements(checksum)}, t,
		developmentObjectsWithApps(
			[]licensingv1alpha1.AppStatus{
				{
					Checksum:            checksum,
					ConsecutiveFailures: 3,
					Message:             "boom",
					State:               "Failed",
					VirtualEntryID:      77,
				},
			},
		)...,
	)

	appStatus := reconcileApp(liferayEnvironmentReconciler, t)

	if appStatus.State != "Downloaded" {
		t.Errorf("State = %q, want Downloaded", appStatus.State)
	}

	if appStatus.ConsecutiveFailures != 0 {
		t.Errorf("ConsecutiveFailures = %d, want 0 after success", appStatus.ConsecutiveFailures)
	}

	if appStatus.Message != "" {
		t.Errorf("Message = %q, want empty after success", appStatus.Message)
	}

	if appStatus.NextRetry != nil {
		t.Error("NextRetry is set, want nil after success")
	}
}

func TestReconcileRestoresReplicasWhenProvisioningRecovers(t *testing.T) {
	unreachableSince := metav1.NewTime(time.Now().Add(-8 * 24 * time.Hour))

	environment := activatedEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Status.ConsecutiveFailures = 50
	environment.Status.UnreachableSince = &unreachableSince

	meta.SetStatusCondition(
		&environment.Status.Conditions,
		metav1.Condition{
			Message: "The grace period elapsed while provisioning was unreachable.",
			Reason:  "ProvisioningUnreachable",
			Status:  metav1.ConditionTrue,
			Type:    conditionGracePeriodExpired,
		},
	)

	objects := []client.Object{
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(1),
			},
		},
		environment,
	}

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML: []byte(virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
			)),
			MaxClusterNodes: 3,
		},
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(provisioningClient, t, objects...)

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(3), "statefulSet.spec.replicas", t,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionGracePeriodExpired,
	) != nil {
		t.Error("GracePeriodExpired condition still present, want it cleared after recovery")
	}

	if liferayEnvironment.Status.UnreachableSince != nil {
		t.Errorf(
			"UnreachableSince = %v, want nil after recovery",
			liferayEnvironment.Status.UnreachableSince,
		)
	}

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready after recovery", liferayEnvironment.Status.Phase)
	}
}

func TestReconcileRetainsLastKnownGoodWhenProvisioningUnreachable(t *testing.T) {
	objects := append(
		developmentObjects(),
		&corev1.Secret{
			Data: map[string][]byte{
				"license.xml": []byte("<license>known-good</license>"),
			},
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-entitlements",
				Namespace: "liferay-dev",
			},
		},
	)

	provisioningClient := &stubProvisioning{
		manifestError: fmt.Errorf("provisioning: connection refused"),
	}

	liferayEnvironmentReconciler, result := reconcileEnvironment(provisioningClient, t, objects...)

	if result.RequeueAfter != 30*time.Second {
		t.Errorf("RequeueAfter = %s, want the base backoff 30s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionProvisioningReachable,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse {
		t.Errorf("ProvisioningReachable condition = %v, want False", condition)
	}

	if liferayEnvironment.Status.ConsecutiveFailures != 1 {
		t.Errorf("ConsecutiveFailures = %d, want 1", liferayEnvironment.Status.ConsecutiveFailures)
	}

	if licenseXML := getLicenseXML(liferayEnvironmentReconciler, t); licenseXML != "<license>known-good</license>" {
		t.Errorf("license.xml = %q, want the retained last-known-good", licenseXML)
	}
}

func TestReconcileRetriesFailedAddOnAfterBackoff(t *testing.T) {
	body := []byte("PK\x03\x04 sample lpkg")

	sum := sha256.Sum256(body)

	checksum := hex.EncodeToString(sum[:])

	nextRetry := metav1.NewTime(metav1.Now().Add(-time.Minute))

	provisioningClient := &stubProvisioning{
		downloadBody: body,
		entitlements: addOnEntitlements(checksum),
	}

	reconcileEnvironment(
		provisioningClient, t,
		developmentObjectsWithApps(
			[]licensingv1alpha1.AppStatus{
				{
					Checksum:            checksum,
					ConsecutiveFailures: 1,
					NextRetry:           &nextRetry,
					State:               "Failed",
					VirtualEntryID:      77,
				},
			},
		)...,
	)

	if !provisioningClient.downloadCalled {
		t.Error("DownloadAddOn was not called; the elapsed backoff should retry")
	}
}

func TestReconcileSurfacesAddOnError(t *testing.T) {
	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{
			downloadError: fmt.Errorf("boom"),
			entitlements:  addOnEntitlements("abc123"),
		}, t,
		developmentObjects()...,
	)

	appStatus := reconcileApp(liferayEnvironmentReconciler, t)

	if appStatus.State != "Failed" {
		t.Errorf("State = %q, want Failed", appStatus.State)
	}

	if appStatus.Message == "" {
		t.Error("Message is empty, want the download error surfaced in status.apps")
	}

	if appStatus.ConsecutiveFailures != 1 {
		t.Errorf("ConsecutiveFailures = %d, want 1", appStatus.ConsecutiveFailures)
	}
}

func TestReconcileWritesExpiredLicenseThrough(t *testing.T) {
	expiredLicenseXML := virtualClusterLicenseXML(
		"Wednesday, January 1, 2020 12:00:00 AM GMT", 1, "dev-namespace-uid",
	)

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML:      []byte(expiredLicenseXML),
			MaxClusterNodes: 1,
		},
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		provisioningClient, t, developmentObjects()...,
	)

	if licenseXML := getLicenseXML(liferayEnvironmentReconciler, t); licenseXML != expiredLicenseXML {
		t.Errorf("license.xml = %q, want the expired license written through", licenseXML)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse || condition.Reason != "Expired" {
		t.Errorf("LicenseValid condition = %v, want False/Expired", condition)
	}

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf("Phase = %q, want Degraded", liferayEnvironment.Status.Phase)
	}
}

func TestResolveDesiredReplicas(t *testing.T) {
	testCases := map[string]struct {
		desiredReplicas  *int32
		expected         int32
		workloadReplicas *int32
	}{
		"defaults to one replica": {
			desiredReplicas:  nil,
			expected:         1,
			workloadReplicas: nil,
		},
		"falls back to the running replica count": {
			desiredReplicas:  nil,
			expected:         2,
			workloadReplicas: pointerInt32(2),
		},
		"prefers the operator intent": {
			desiredReplicas:  pointerInt32(4),
			expected:         4,
			workloadReplicas: pointerInt32(2),
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
				Spec: licensingv1alpha1.LiferayEnvironmentSpec{
					DesiredReplicas: testCase.desiredReplicas,
				},
			}

			statefulSet := &appsv1.StatefulSet{
				Spec: appsv1.StatefulSetSpec{
					Replicas: testCase.workloadReplicas,
				},
			}

			if actual := resolveDesiredReplicas(
				liferayEnvironment, statefulSet,
			); actual != testCase.expected {
				t.Errorf("Unexpected resolveDesiredReplicas result: got %d, want %d", actual, testCase.expected)
			}
		})
	}
}

func TestSetupWithManagerWatchesScaledObjects(t *testing.T) {
	testCases := map[string]struct {
		installBeforeStart bool
	}{
		"watches scaled objects when keda is installed after the manager starts": {
			installBeforeStart: false,
		},
		"watches scaled objects when keda is installed before the manager starts": {
			installBeforeStart: true,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			assetsDir := envtestAssetsDir(t)

			if assetsDir == "" {
				t.Skip(
					"Set KUBEBUILDER_ASSETS, or install the envtest binaries with setup-envtest, to run this test",
				)
			}

			testEnvironment := &envtest.Environment{
				BinaryAssetsDirectory: assetsDir,
				CRDDirectoryPaths:     []string{filepath.Join(chartDir, "crds")},
				ErrorIfCRDPathMissing: true,
			}

			config, error := testEnvironment.Start()

			if error != nil {
				t.Fatalf("Unable to start the test environment: %v", error)
			}

			t.Cleanup(func() {
				if error := testEnvironment.Stop(); error != nil {
					t.Errorf("Unable to stop the test environment: %v", error)
				}
			})

			scheme := runtime.NewScheme()

			if error := clientgoscheme.AddToScheme(scheme); error != nil {
				t.Fatalf("Unable to register the client-go scheme: %v", error)
			}

			if error := licensingv1alpha1.AddToScheme(scheme); error != nil {
				t.Fatalf("Unable to register the licensing scheme: %v", error)
			}

			setUpClient, error := client.New(config, client.Options{Scheme: scheme})

			if error != nil {
				t.Fatalf("Unable to build a client: %v", error)
			}

			namespace := &corev1.Namespace{
				ObjectMeta: metav1.ObjectMeta{Name: "liferay-dev"},
			}

			if error := setUpClient.Create(context.Background(), namespace); error != nil {
				t.Fatalf("Unable to create the namespace: %v", error)
			}

			statefulSet := newWorkload("liferay-dev")
			statefulSet.Spec.Replicas = pointerInt32(1)

			if error := setUpClient.Create(context.Background(), statefulSet); error != nil {
				t.Fatalf("Unable to create the workload: %v", error)
			}

			liferayEnvironment := activatedEnvironment()
			liferayEnvironment.Spec.Autoscaling = &licensingv1alpha1.Autoscaling{
				MaxReplicas: 10,
				MinReplicas: 1,
			}

			activatedAt := liferayEnvironment.Status.ActivatedAt

			if error := setUpClient.Create(context.Background(), liferayEnvironment); error != nil {
				t.Fatalf("Unable to create the environment: %v", error)
			}

			liferayEnvironment.Status.ActivatedAt = activatedAt

			if error := setUpClient.Status().Update(
				context.Background(), liferayEnvironment,
			); error != nil {
				t.Fatalf("Unable to activate the environment: %v", error)
			}

			if testCase.installBeforeStart {
				installScaledObjectCustomResourceDefinition(setUpClient, t)
			}

			skipNameValidation := true

			manager, error := controllerruntime.NewManager(
				config,
				controllerruntime.Options{
					Controller: controllerconfig.Controller{
						SkipNameValidation: &skipNameValidation,
					},
					HealthProbeBindAddress: "0",
					Metrics:                metricsserver.Options{BindAddress: "0"},
					Scheme:                 scheme,
				},
			)

			if error != nil {
				t.Fatalf("Unable to build the manager: %v", error)
			}

			provisioningClient := &stubProvisioning{
				entitlements: &provisioning.Entitlements{
					LicenseXML: []byte(virtualClusterLicenseXML(
						"Friday, March 2, 2029 12:00:00 AM GMT", 3, string(namespace.UID),
					)),
					MaxClusterNodes: 3,
				},
			}

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client:               manager.GetClient(),
				GracePeriod:          7 * 24 * time.Hour,
				HeartbeatInterval:    time.Hour,
				MarketplaceMountPath: t.TempDir(),
				Provisioning:         provisioningClient,
				Recorder:             record.NewFakeRecorder(100),
				RetryInitialDelay:    30 * time.Second,
				RetryMaxDelay:        30 * time.Minute,
				Syncer: addon.NewSyncer(
					provisioningClient, 15*time.Second, 30*time.Second, 30*time.Minute,
					inlineRunner{},
				),
			}

			if error := liferayEnvironmentReconciler.SetupWithManager(manager); error != nil {
				t.Fatalf("Unable to set up the controller: %v", error)
			}

			startManager(manager, t)

			awaitCondition(
				func() bool {
					var stored licensingv1alpha1.LiferayEnvironment

					if error := setUpClient.Get(
						context.Background(),
						types.NamespacedName{Name: "dev", Namespace: "liferay-dev"},
						&stored,
					); error != nil {
						return false
					}

					return stored.Status.Phase == "Ready"
				},
				"the environment to finish its first reconcile",
				t,
			)

			if !testCase.installBeforeStart {
				installScaledObjectCustomResourceDefinition(setUpClient, t)
			}

			if error := setUpClient.Create(
				context.Background(), newScaledObject(10, 1, "dev-liferay"),
			); error != nil {
				t.Fatalf("Unable to create the scaled object: %v", error)
			}

			awaitCondition(
				func() bool {
					scaledObject := newScaledObject(0, 0, "")

					if error := setUpClient.Get(
						context.Background(),
						types.NamespacedName{Name: "dev-liferay", Namespace: "liferay-dev"},
						scaledObject,
					); error != nil {
						return false
					}

					maxReplicaCount, _, _ := unstructured.NestedInt64(
						scaledObject.Object, "spec", "maxReplicaCount",
					)

					return maxReplicaCount == 3
				},
				"the scaled object to be capped at the licensed ceiling",
				t,
			)
		})
	}
}

func activatedEnvironment() *licensingv1alpha1.LiferayEnvironment {
	activatedAt := metav1.Now()

	return &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			ActivationCodeSecretRef: licensingv1alpha1.SecretKeyRef{
				Key:  "activationCode",
				Name: "dev-activation",
			},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
		Status: licensingv1alpha1.LiferayEnvironmentStatus{
			ActivatedAt: &activatedAt,
		},
	}
}

func addOnEntitlements(checksum string) *provisioning.Entitlements {
	return &provisioning.Entitlements{
		AddOns: []provisioning.AddOn{
			{
				DownloadURL:    "https://example.com/marketplace/virtual-entry/77",
				ProductName:    "Sample Add-on",
				SHA256Checksum: checksum,
				VirtualEntryID: 77,
			},
		},
		LicenseXML: []byte(virtualClusterLicenseXML(
			"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
		)),
		MaxClusterNodes: 3,
	}
}

func assertReplicasEqual(actual *int32, expected *int32, field string, t *testing.T) {
	t.Helper()

	if expected == nil {
		if actual != nil {
			t.Errorf("Unexpected %s: got %d, want nil", field, *actual)
		}

		return
	}

	if actual == nil {
		t.Errorf("Unexpected %s: got nil, want %d", field, *expected)

		return
	}

	if *actual != *expected {
		t.Errorf("Unexpected %s: got %d, want %d", field, *actual, *expected)
	}
}

func assertScaledObjectReplicaCount(
	expected int64,
	field string,
	scaledObject *unstructured.Unstructured,
	t *testing.T,
) {
	t.Helper()

	actual, found, error := unstructured.NestedInt64(scaledObject.Object, "spec", field)

	if error != nil || !found {
		t.Errorf("Unexpected scaledObject.spec.%s: missing (%v)", field, error)

		return
	}

	if actual != expected {
		t.Errorf("Unexpected scaledObject.spec.%s: got %d, want %d", field, actual, expected)
	}
}

func awaitCondition(condition func() bool, description string, t *testing.T) {
	t.Helper()

	deadline := time.Now().Add(60 * time.Second)

	for !condition() {
		if time.Now().After(deadline) {
			t.Fatalf("Timed out waiting for %s", description)
		}

		time.Sleep(200 * time.Millisecond)
	}
}

func developmentObjects() []client.Object {
	return []client.Object{
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(1),
			},
		},
		activatedEnvironment(),
	}
}

func developmentObjectsWithApps(
	apps []licensingv1alpha1.AppStatus,
) []client.Object {
	objects := developmentObjects()

	environment := objects[len(objects)-1].(*licensingv1alpha1.LiferayEnvironment)

	environment.Status.Apps = apps

	return objects
}

func getEnvironment(
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler,
	t *testing.T,
) *licensingv1alpha1.LiferayEnvironment {
	t.Helper()

	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{}

	if error := liferayEnvironmentReconciler.Get(
		context.Background(), types.NamespacedName{
			Name:      "dev",
			Namespace: "liferay-dev",
		}, liferayEnvironment); error != nil {
		t.Fatalf("Unable to read the environment: %v", error)
	}

	return liferayEnvironment
}

func getHorizontalPodAutoscaler(
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler,
	t *testing.T,
) *autoscalingv2.HorizontalPodAutoscaler {
	t.Helper()

	horizontalPodAutoscaler := &autoscalingv2.HorizontalPodAutoscaler{}

	if error := liferayEnvironmentReconciler.Get(
		context.Background(),
		types.NamespacedName{
			Name:      "dev-liferay",
			Namespace: "liferay-dev",
		},
		horizontalPodAutoscaler); error != nil {
		t.Fatalf("Unable to read the autoscaler: %v", error)
	}

	return horizontalPodAutoscaler
}

func getLicenseXML(liferayEnvironmentReconciler *LiferayEnvironmentReconciler, t *testing.T) string {
	return string(getSecret("dev-entitlements", liferayEnvironmentReconciler, t).Data["license.xml"])
}

func getScaledObject(
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler,
	t *testing.T,
) *unstructured.Unstructured {
	t.Helper()

	scaledObject := newScaledObject(0, 0, "")

	if error := liferayEnvironmentReconciler.Get(
		context.Background(),
		types.NamespacedName{
			Name:      "dev-liferay",
			Namespace: "liferay-dev",
		},
		scaledObject); error != nil {
		t.Fatalf("Unable to read the scaled object: %v", error)
	}

	return scaledObject
}

func getSecret(
	name string,
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler,
	t *testing.T,
) *corev1.Secret {
	t.Helper()

	secret := &corev1.Secret{}

	if error := liferayEnvironmentReconciler.Get(
		context.Background(), types.NamespacedName{
			Name:      name,
			Namespace: "liferay-dev",
		}, secret); error != nil {
		t.Fatalf("Unable to read the secret %q: %v", name, error)
	}

	return secret
}

func getStatefulSet(
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler,
	t *testing.T,
) *appsv1.StatefulSet {
	t.Helper()

	statefulSet := &appsv1.StatefulSet{}

	if error := liferayEnvironmentReconciler.Get(
		context.Background(),
		types.NamespacedName{
			Name:      "dev-liferay",
			Namespace: "liferay-dev",
		},
		statefulSet); error != nil {
		t.Fatalf("Unable to read the workload: %v", error)
	}

	return statefulSet
}

func installScaledObjectCustomResourceDefinition(setUpClient client.Client, t *testing.T) {
	t.Helper()

	customResourceDefinition := &unstructured.Unstructured{
		Object: map[string]any{
			"spec": map[string]any{
				"group": "keda.sh",
				"names": map[string]any{
					"kind":     "ScaledObject",
					"listKind": "ScaledObjectList",
					"plural":   "scaledobjects",
					"singular": "scaledobject",
				},
				"scope": "Namespaced",
				"versions": []any{
					map[string]any{
						"name": "v1alpha1",
						"schema": map[string]any{
							"openAPIV3Schema": map[string]any{
								"type":                                 "object",
								"x-kubernetes-preserve-unknown-fields": true,
							},
						},
						"served":  true,
						"storage": true,
					},
				},
			},
		},
	}

	customResourceDefinition.SetGroupVersionKind(customResourceDefinitionGroupVersionKind)
	customResourceDefinition.SetName(scaledObjectCustomResourceDefinitionName)

	if error := setUpClient.Create(context.Background(), customResourceDefinition); error != nil {
		t.Fatalf("Unable to install the ScaledObject CRD: %v", error)
	}

	awaitCondition(
		func() bool {
			stored := &unstructured.Unstructured{}

			stored.SetGroupVersionKind(customResourceDefinitionGroupVersionKind)

			if error := setUpClient.Get(
				context.Background(),
				types.NamespacedName{Name: scaledObjectCustomResourceDefinitionName},
				stored,
			); error != nil {
				return false
			}

			conditions, _, _ := unstructured.NestedSlice(stored.Object, "status", "conditions")

			for _, condition := range conditions {
				fields, ok := condition.(map[string]any)

				if ok && fields["type"] == "Established" && fields["status"] == "True" {
					return true
				}
			}

			return false
		},
		"the ScaledObject CRD to be established",
		t,
	)
}

func newFakeClient(t *testing.T, objects ...client.Object) client.Client {
	t.Helper()

	return fake.NewClientBuilder().WithObjects(
		objects...,
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()
}

func newFakeClientEnforcingMax(
	t *testing.T, objects ...client.Object,
) client.Client {
	t.Helper()

	return fake.NewClientBuilder().WithInterceptorFuncs(
		interceptor.Funcs{
			Update: func(
				context context.Context,
				writer client.WithWatch,
				object client.Object,
				options ...client.UpdateOption,
			) error {
				statefulSet, ok := object.(*appsv1.StatefulSet)

				if !ok || statefulSet.Spec.Replicas == nil {
					return writer.Update(context, object, options...)
				}

				var liferayEnvironment licensingv1alpha1.LiferayEnvironment

				if error := writer.Get(
					context,
					types.NamespacedName{Name: "dev", Namespace: "liferay-dev"},
					&liferayEnvironment,
				); error != nil {
					return error
				}

				maxClusterNodes := liferayEnvironment.Status.License.MaxClusterNodes

				if maxClusterNodes != nil && *statefulSet.Spec.Replicas > *maxClusterNodes {
					return errors.NewForbidden(
						schema.GroupResource{Group: "apps", Resource: "statefulsets"},
						statefulSet.Name,
						fmt.Errorf(
							"replicas %d exceeds licensed maxClusterNodes %d",
							*statefulSet.Spec.Replicas, *maxClusterNodes,
						),
					)
				}

				return writer.Update(context, object, options...)
			},
		},
	).WithObjects(
		objects...,
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()
}

func newHorizontalPodAutoscaler(
	maxReplicas int32,
	minReplicas int32,
	scaleTargetName string,
) *autoscalingv2.HorizontalPodAutoscaler {
	return &autoscalingv2.HorizontalPodAutoscaler{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev-liferay",
			Namespace: "liferay-dev",
		},
		Spec: autoscalingv2.HorizontalPodAutoscalerSpec{
			MaxReplicas: maxReplicas,
			MinReplicas: &minReplicas,
			ScaleTargetRef: autoscalingv2.CrossVersionObjectReference{
				APIVersion: "apps/v1",
				Kind:       "StatefulSet",
				Name:       scaleTargetName,
			},
		},
	}
}

func newScaledObject(
	maxReplicaCount int32,
	minReplicaCount int32,
	scaleTargetName string,
) *unstructured.Unstructured {
	scaledObject := &unstructured.Unstructured{
		Object: map[string]any{
			"spec": map[string]any{
				"maxReplicaCount": int64(maxReplicaCount),
				"minReplicaCount": int64(minReplicaCount),
				"scaleTargetRef": map[string]any{
					"apiVersion": "apps/v1",
					"kind":       "StatefulSet",
					"name":       scaleTargetName,
				},
			},
		},
	}

	scaledObject.SetGroupVersionKind(scaledObjectGroupVersionKind)
	scaledObject.SetName("dev-liferay")
	scaledObject.SetNamespace("liferay-dev")

	return scaledObject
}

func newScheme(t *testing.T) *runtime.Scheme {
	t.Helper()

	scheme := runtime.NewScheme()

	if error := appsv1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the apps/v1 scheme: %v", error)
	}

	if error := autoscalingv1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the autoscaling/v1 scheme: %v", error)
	}

	if error := autoscalingv2.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the autoscaling/v2 scheme: %v", error)
	}

	if error := corev1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the core/v1 scheme: %v", error)
	}

	if error := licensingv1alpha1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the licensing scheme: %v", error)
	}

	scheme.AddKnownTypeWithName(
		scaledObjectGroupVersionKind, &unstructured.Unstructured{},
	)
	scheme.AddKnownTypeWithName(
		scaledObjectGroupVersionKind.GroupVersion().WithKind(
			scaledObjectGroupVersionKind.Kind+"List",
		),
		&unstructured.UnstructuredList{},
	)

	return scheme
}

func pendingEnvironment() *licensingv1alpha1.LiferayEnvironment {
	return &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			ActivationCodeSecretRef: licensingv1alpha1.SecretKeyRef{
				Key:  "activationCode",
				Name: "dev-activation",
			},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}
}

func pointerInt32(value int32) *int32 {
	return &value
}

func reconcile(
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler,
	t *testing.T,
) controllerruntime.Result {
	t.Helper()

	result, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: "liferay-dev",
			},
		},
	)

	if error != nil {
		t.Fatalf("Unexpected reconcile error: %v", error)
	}

	return result
}

func reconcileApp(
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler, t *testing.T,
) licensingv1alpha1.AppStatus {
	t.Helper()

	reconcile(liferayEnvironmentReconciler, t)

	return getEnvironment(liferayEnvironmentReconciler, t).Status.Apps[0]
}

func reconcileEnvironment(
	provisioningClient provisioning.Client,
	t *testing.T,
	objects ...client.Object,
) (*LiferayEnvironmentReconciler, controllerruntime.Result) {
	t.Helper()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client:               newFakeClient(t, objects...),
		GracePeriod:          7 * 24 * time.Hour,
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: t.TempDir(),
		Provisioning:         provisioningClient,
		Recorder:             record.NewFakeRecorder(10),
		RetryInitialDelay:    30 * time.Second,
		RetryMaxDelay:        30 * time.Minute,
		Syncer: addon.NewSyncer(
			provisioningClient, 15*time.Second, 30*time.Second, 30*time.Minute,
			inlineRunner{},
		),
	}

	result, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: "liferay-dev",
			},
		},
	)

	if error != nil {
		t.Fatalf("Unexpected reconcile error: %v", error)
	}

	return liferayEnvironmentReconciler, result
}

func reconcileOfflineActivationBundle(
	marketplaceMountPath string,
	t *testing.T,
	objects ...client.Object,
) (*LiferayEnvironmentReconciler, controllerruntime.Result) {
	t.Helper()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client:               newFakeClient(t, objects...),
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: marketplaceMountPath,
		Provisioning:         &stubProvisioning{},
		Recorder:             record.NewFakeRecorder(10),
	}

	return liferayEnvironmentReconciler, reconcile(liferayEnvironmentReconciler, t)
}

func startManager(manager controllerruntime.Manager, t *testing.T) {
	t.Helper()

	managerContext, cancel := context.WithCancel(context.Background())

	managerErrors := make(chan error, 1)

	go func() {
		managerErrors <- manager.Start(managerContext)
	}()

	t.Cleanup(func() {
		cancel()

		if error := <-managerErrors; error != nil {
			t.Errorf("Unexpected error from the manager: %v", error)
		}
	})
}

func virtualClusterLicenseXML(
	expirationDate string, maxClusterNodes int32, owner string,
) string {
	return fmt.Sprintf(
		"<licenses><license>"+
			"<owner>%s</owner>"+
			"<expiration-date>%s</expiration-date>"+
			"<license-type>virtual-cluster</license-type>"+
			"<max-cluster-nodes>%d</max-cluster-nodes>"+
			"</license></licenses>",
		owner, expirationDate, maxClusterNodes,
	)
}

type inlineRunner struct{}

type stubProvisioning struct {
	activateCalled bool
	activateError  error
	downloadBody   []byte
	downloadCalled bool
	downloadError  error
	entitlements   *provisioning.Entitlements
	manifestCalled bool
	manifestError  error
}
