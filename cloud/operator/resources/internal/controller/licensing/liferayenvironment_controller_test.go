package licensing

import (
	"context"
	"testing"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	runtime "k8s.io/apimachinery/pkg/runtime"
	types "k8s.io/apimachinery/pkg/types"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func TestEnforceReplicaCeiling(t *testing.T) {
	testCases := map[string]struct {
		desiredReplicas   *int32
		expectedCondition metav1.ConditionStatus
		expectedEffective *int32
		expectedReason    string
		expectedReplicas  *int32
		maxClusterNodes   int32
		workloadExists    bool
		workloadReplicas  *int32
	}{
		"caps replicas above the licensed maximum": {
			desiredReplicas:   pointerInt32(5),
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(3),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(3),
			maxClusterNodes:   3,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(5),
		},
		"caps the current replicas when desired is unset": {
			desiredReplicas:   nil,
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(2),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(2),
			maxClusterNodes:   2,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(4),
		},
		"defaults to a single replica when neither desired nor current is set": {
			desiredReplicas:   nil,
			expectedCondition: metav1.ConditionTrue,
			expectedEffective: pointerInt32(1),
			expectedReason:    "WithinLicensedLimit",
			expectedReplicas:  pointerInt32(1),
			maxClusterNodes:   5,
			workloadExists:    true,
			workloadReplicas:  nil,
		},
		"reports when the workload does not exist": {
			desiredReplicas:   pointerInt32(3),
			expectedCondition: metav1.ConditionUnknown,
			expectedEffective: nil,
			expectedReason:    "WorkloadNotFound",
			expectedReplicas:  nil,
			maxClusterNodes:   3,
			workloadExists:    false,
			workloadReplicas:  nil,
		},
		"scales a running workload down to the ceiling": {
			desiredReplicas:   pointerInt32(5),
			expectedCondition: metav1.ConditionFalse,
			expectedEffective: pointerInt32(3),
			expectedReason:    "ExceedsLicensedMaximum",
			expectedReplicas:  pointerInt32(3),
			maxClusterNodes:   3,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(5),
		},
		"skips enforcement when the ceiling is unknown": {
			desiredReplicas:   pointerInt32(3),
			expectedCondition: metav1.ConditionUnknown,
			expectedEffective: nil,
			expectedReason:    "MaxClusterNodesUnknown",
			expectedReplicas:  pointerInt32(3),
			maxClusterNodes:   0,
			workloadExists:    true,
			workloadReplicas:  pointerInt32(3),
		},
		"within the licensed limit": {
			desiredReplicas:   pointerInt32(2),
			expectedCondition: metav1.ConditionTrue,
			expectedEffective: pointerInt32(2),
			expectedReason:    "WithinLicensedLimit",
			expectedReplicas:  pointerInt32(2),
			maxClusterNodes:   3,
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
					DesiredReplicas: testCase.desiredReplicas,
					WorkloadRef: licensingv1alpha1.WorkloadRef{
						Name: "dev-liferay",
					},
				},
			}

			objects := []client.Object{}

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

			reconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(t, objects...),
			}

			if error := reconciler.enforceReplicaCeiling(
				context.Background(), liferayEnvironment, testCase.maxClusterNodes,
			); error != nil {
				t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
			}

			assertReplicasEqual(
				t, "status.effectiveReplicas",
				testCase.expectedEffective,
				liferayEnvironment.Status.EffectiveReplicas,
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

			statefulSet := &appsv1.StatefulSet{}

			if error := reconciler.Get(
				context.Background(), types.NamespacedName{
					Name:      "dev-liferay",
					Namespace: "liferay-dev",
				}, statefulSet); error != nil {
				t.Fatalf("Unable to read the workload: %v", error)
			}

			assertReplicasEqual(
				t, "statefulSet.spec.replicas",
				testCase.expectedReplicas, statefulSet.Spec.Replicas,
			)
		})
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

func assertReplicasEqual(t *testing.T, field string, expected *int32, actual *int32) {
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

func newFakeClient(t *testing.T, objects ...client.Object) client.Client {
	t.Helper()

	scheme := runtime.NewScheme()

	if error := appsv1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the apps/v1 scheme: %v", error)
	}

	if error := licensingv1alpha1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the licensing scheme: %v", error)
	}

	return fake.NewClientBuilder().WithObjects(objects...).WithScheme(scheme).Build()
}

func pointerInt32(value int32) *int32 {
	return &value
}
