package licensing

import (
	"context"
	"fmt"
	"strings"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	appsv1 "k8s.io/api/apps/v1"
	autoscalingv2 "k8s.io/api/autoscaling/v2"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	schema "k8s.io/apimachinery/pkg/runtime/schema"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
)

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
		for _, workloadKind := range workloadKinds {
			t.Run(fmt.Sprintf("%s for a %s", name, workloadKind), func(t *testing.T) {
				liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
					ObjectMeta: metav1.ObjectMeta{
						Name:      "dev",
						Namespace: "liferay-dev",
					},
					Spec: licensingv1alpha1.LiferayEnvironmentSpec{
						Autoscaling:     testCase.autoscaling,
						DesiredReplicas: testCase.desiredReplicas,
						WorkloadRef: licensingv1alpha1.WorkloadRef{
							Kind: workloadKind,
							Name: "dev-liferay",
						},
					},
				}

				objects := []client.Object{liferayEnvironment}

				if testCase.workloadExists {
					objects = append(
						objects,
						newDevelopmentWorkload(testCase.workloadReplicas, workloadKind),
					)
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

				assertReplicasEqual(
					liferayEnvironment.Status.ReplicaCeiling,
					&testCase.replicaCeiling,
					"status.replicaCeiling",
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

				if condition.Reason == "ExceedsLicensedMaximum" &&
					!strings.Contains(condition.Message, "replica ceiling") {

					t.Errorf(
						"Condition message = %q, want it to name the replica ceiling",
						condition.Message,
					)
				}

				if condition.Reason == "WorkloadNotFound" &&
					!strings.Contains(condition.Message, string(workloadKind)) {

					t.Errorf(
						"Condition message = %q, want it to name the %s",
						condition.Message, workloadKind,
					)
				}

				if !testCase.workloadExists {
					return
				}

				workload, error := liferayEnvironmentReconciler.getWorkload(
					context.Background(), liferayEnvironment,
				)

				if error != nil {
					t.Fatalf("Unable to read the workload: %v", error)
				}

				assertReplicasEqual(
					getReplicas(workload), testCase.expectedReplicas,
					"workload.spec.replicas", t,
				)
			})
		}
	}
}

func TestEnforceReplicaCeilingMarksItsOwnScaleToZero(t *testing.T) {
	testCases := map[string]struct {
		expectedMarked   bool
		expectedReplicas int32
		liveReplicas     int32
		marked           bool
		replicaCeiling   int32
	}{
		"keeps the marker while the ceiling stays at zero": {
			expectedMarked:   true,
			expectedReplicas: 0,
			liveReplicas:     0,
			marked:           true,
			replicaCeiling:   0,
		},
		"leaves a workload a user scaled to zero at zero": {
			expectedMarked:   false,
			expectedReplicas: 0,
			liveReplicas:     0,
			marked:           false,
			replicaCeiling:   3,
		},
		"leaves a workload already at zero unmarked": {
			expectedMarked:   false,
			expectedReplicas: 0,
			liveReplicas:     0,
			marked:           false,
			replicaCeiling:   0,
		},
		"marks an autoscaled workload it scales to zero": {
			expectedMarked:   true,
			expectedReplicas: 0,
			liveReplicas:     3,
			marked:           false,
			replicaCeiling:   0,
		},
		"raises its own scale to zero back to the minimum": {
			expectedMarked:   false,
			expectedReplicas: 2,
			liveReplicas:     0,
			marked:           true,
			replicaCeiling:   3,
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
					Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 2},
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
					Replicas: &testCase.liveReplicas,
				},
			}

			if testCase.marked {
				statefulSet.Annotations = map[string]string{annotationScaledToZero: "true"}
			}

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(t, liferayEnvironment, statefulSet),
			}

			if _, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
				context.Background(), liferayEnvironment, testCase.replicaCeiling,
			); error != nil {
				t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
			}

			statefulSet = getStatefulSet(liferayEnvironmentReconciler, t)

			assertReplicasEqual(
				statefulSet.Spec.Replicas, &testCase.expectedReplicas,
				"statefulSet.spec.replicas", t,
			)

			if marked := statefulSet.Annotations[annotationScaledToZero] == "true"; marked != testCase.expectedMarked {
				t.Errorf(
					"statefulSet.metadata.annotations[%q] present = %t, want %t",
					annotationScaledToZero, marked, testCase.expectedMarked,
				)
			}
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

					observedCeiling = stored.Status.ReplicaCeiling
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

func TestEnforceReplicaCeilingPersistsRaisedCeilingBeforeRaisingAutoscaler(t *testing.T) {
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
		Status: licensingv1alpha1.LiferayEnvironmentStatus{
			ReplicaCeiling: pointerInt32(1),
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

	observedAutoscalerPatch := false

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
					stored := &licensingv1alpha1.LiferayEnvironment{}

					if error := writer.Get(
						context, types.NamespacedName{
							Name:      "dev",
							Namespace: "liferay-dev",
						}, stored,
					); error != nil {
						return error
					}

					observedCeiling = stored.Status.ReplicaCeiling
					observedAutoscalerPatch = true
				}

				return writer.Patch(context, object, patch, options...)
			},
		},
	).WithObjects(
		liferayEnvironment, newHorizontalPodAutoscaler(1, 1, "dev-liferay"), statefulSet,
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{Client: fakeClient}

	if _, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
	}

	if !observedAutoscalerPatch {
		t.Fatal("Expected the autoscaler to be patched")
	}

	assertReplicasEqual(observedCeiling, pointerInt32(3), "persisted status.replicaCeiling", t)
}

func TestEnforceReplicaCeilingRecordsRefusalWhenAutoscalerUpdateRejected(t *testing.T) {
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

	stepOutcome, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context.Background(), liferayEnvironment, 3,
	)

	if error != nil {
		t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
	}

	if stepOutcome != stopAfter(30*time.Second) {
		t.Errorf(
			"enforceReplicaCeiling = %+v, want %+v", stepOutcome, stopAfter(30*time.Second),
		)
	}

	assertReplicasEqual(
		getStatefulSet(liferayEnvironmentReconciler, t).Spec.Replicas,
		pointerInt32(3),
		"statefulSet.spec.replicas",
		t,
	)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionReplicasCountValid,
	)

	if condition == nil {
		t.Fatal("Expected a ReplicasCountValid condition, got none")
	}

	if condition.Reason != "AutoscalerUpdateRejected" {
		t.Errorf(
			"condition.Reason = %q, want %q",
			condition.Reason, "AutoscalerUpdateRejected",
		)
	}
}

func TestEnforceReplicaCeilingRecordsRefusalWhenWorkloadUpdateRejected(t *testing.T) {
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

	stepOutcome, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context.Background(), liferayEnvironment, 3,
	)

	if error != nil {
		t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
	}

	if stepOutcome != stopAfter(30*time.Second) {
		t.Errorf(
			"enforceReplicaCeiling = %+v, want %+v", stepOutcome, stopAfter(30*time.Second),
		)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionReplicasCountValid,
	)

	if condition == nil {
		t.Fatal("Expected a ReplicasCountValid condition, got none")
	}

	if condition.Reason != "WorkloadUpdateRejected" {
		t.Errorf(
			"condition.Reason = %q, want %q",
			condition.Reason, "WorkloadUpdateRejected",
		)
	}
}

func TestReconcileAppliesExpirationWhileProvisioningUnreachable(t *testing.T) {
	unreachableSince := metav1.NewTime(time.Now().Add(-time.Hour))
	validUntil := metav1.NewTime(time.Now().Add(-100 * 24 * time.Hour))

	environment := activatedEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Status.License.MaxClusterNodes = pointerInt32(3)
	environment.Status.License.ValidUntil = &validUntil
	environment.Status.ReplicaCeiling = pointerInt32(3)
	environment.Status.UnreachableSince = &unreachableSince

	provisioningClient := &stubProvisioning{
		manifestError: fmt.Errorf("provisioning: connection refused"),
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		provisioningClient,
		t,
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(3),
			},
		},
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	assertReplicasEqual(
		getStatefulSet(liferayEnvironmentReconciler, t).Spec.Replicas,
		pointerInt32(0),
		"statefulSet.spec.replicas",
		t,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		liferayEnvironment.Status.ReplicaCeiling, pointerInt32(0), "status.replicaCeiling", t,
	)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	)

	if condition == nil || condition.Reason != "Expired" {
		t.Errorf("LicenseValid condition = %v, want reason Expired", condition)
	}

	if meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionProvisioningGracePeriodExpired,
	) {
		t.Error("ProvisioningGracePeriodExpired condition = True, want it unset while provisioning is within its grace period")
	}
}

func TestReconcileDowngradesAfterProvisioningGracePeriod(t *testing.T) {
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

	assertReplicasEqual(
		liferayEnvironment.Status.ReplicaCeiling, pointerInt32(1), "status.replicaCeiling", t,
	)

	if !meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionProvisioningGracePeriodExpired,
	) {
		t.Error("ProvisioningGracePeriodExpired condition = not True, want True after the grace window")
	}

	if licenseXML := getLicenseXML(liferayEnvironmentReconciler, t); licenseXML != "<license>known-good</license>" {
		t.Errorf("license.xml = %q, should retain last-known-good", licenseXML)
	}

	recorder := liferayEnvironmentReconciler.Recorder.(*record.FakeRecorder)

	select {
	case event := <-recorder.Events:
		if !strings.Contains(event, "ProvisioningGracePeriodExpired") {
			t.Errorf("event = %q, should mention ProvisioningGracePeriodExpired", event)
		}
	default:
		t.Error("Expected a ProvisioningGracePeriodExpired warning event")
	}
}

func TestReconcileKeepsLowerCeilingAfterProvisioningGracePeriod(t *testing.T) {
	unreachableSince := metav1.NewTime(time.Now().Add(-8 * 24 * time.Hour))

	environment := activatedEnvironment()
	environment.Status.ReplicaCeiling = pointerInt32(0)
	environment.Status.UnreachableSince = &unreachableSince

	provisioningClient := &stubProvisioning{
		manifestError: fmt.Errorf("provisioning: connection refused"),
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		provisioningClient,
		t,
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-liferay",
				Namespace: "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(0),
			},
		},
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(0), "statefulSet.spec.replicas", t,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		liferayEnvironment.Status.ReplicaCeiling, pointerInt32(0), "status.replicaCeiling", t,
	)
}

func TestReconcilePersistsCeilingWhenWorkloadUpdateRejected(t *testing.T) {
	environment := activatedEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Status.License.MaxClusterNodes = pointerInt32(0)
	environment.Status.ReplicaCeiling = pointerInt32(0)

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML: []byte(virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
			)),
			MaxClusterNodes: 3,
		},
	}

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClientEnforcingCeiling(
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

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	maxClusterNodes := liferayEnvironment.Status.License.MaxClusterNodes

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
		liferayEnvironment.Status.ReplicaCeiling, pointerInt32(3), "status.replicaCeiling", t,
	)

	assertReplicasEqual(
		getStatefulSet(liferayEnvironmentReconciler, t).Spec.Replicas,
		pointerInt32(3), "Replicas", t,
	)
}

func TestReconcilePersistsProvisioningGracePeriodWhenWorkloadUpdateRejected(t *testing.T) {
	unreachableSince := metav1.NewTime(time.Now().Add(-2 * time.Hour))

	environment := activatedEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Status.License.MaxClusterNodes = pointerInt32(3)
	environment.Status.ReplicaCeiling = pointerInt32(3)
	environment.Status.UnreachableSince = &unreachableSince

	fakeClient := fake.NewClientBuilder().WithInterceptorFuncs(
		interceptor.Funcs{
			Update: func(
				context context.Context,
				writer client.WithWatch,
				object client.Object,
				options ...client.UpdateOption,
			) error {
				if statefulSet, ok := object.(*appsv1.StatefulSet); ok {
					return errors.NewForbidden(
						schema.GroupResource{Group: "apps", Resource: "statefulsets"},
						statefulSet.Name,
						fmt.Errorf("the workload is locked"),
					)
				}

				return writer.Update(context, object, options...)
			},
		},
	).WithObjects(
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
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client:               fakeClient,
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: t.TempDir(),
		Provisioning: &stubProvisioning{
			manifestError: fmt.Errorf("provisioning is unreachable"),
		},
		ProvisioningGracePeriod: time.Hour,
		Recorder:                record.NewFakeRecorder(10),
		RetryInitialDelay:       30 * time.Second,
		RetryMaxDelay:           30 * time.Minute,
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

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf(
			"Phase = %q, want Degraded persisted so that the grace period and the backoff survive the rejected write",
			liferayEnvironment.Status.Phase,
		)
	}

	assertReplicasEqual(
		liferayEnvironment.Status.ReplicaCeiling, pointerInt32(1), "status.replicaCeiling", t,
	)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionReplicasCountValid,
	)

	if condition == nil || condition.Reason != "WorkloadUpdateRejected" {
		t.Errorf(
			"ReplicasCountValid condition = %v, want reason WorkloadUpdateRejected",
			condition,
		)
	}
}

func TestReconcilePersistsRefusalWhenAutoscalerUpdateRejected(t *testing.T) {
	environment := activatedEnvironment()
	environment.Spec.Autoscaling = &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1}

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML: []byte(virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
			)),
			MaxClusterNodes: 3,
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
		newHorizontalPodAutoscaler(10, 1, "dev-liferay"),
	).WithScheme(
		newScheme(t),
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client:               fakeClient,
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: t.TempDir(),
		Provisioning:         provisioningClient,
		Recorder:             record.NewFakeRecorder(10),
		RetryInitialDelay:    30 * time.Second,
		Syncer: addon.NewSyncer(
			provisioningClient, 15*time.Second, 30*time.Second, 30*time.Minute,
			inlineRunner{},
		),
	}

	result := reconcile(liferayEnvironmentReconciler, t)

	if result.RequeueAfter != 30*time.Second {
		t.Errorf("result.RequeueAfter = %v, want %v", result.RequeueAfter, 30*time.Second)
	}

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

func TestReconcileRestoresAutoscaledWorkloadWhenLicenseRenewed(t *testing.T) {
	environment := activatedEnvironment()
	environment.Spec.Autoscaling = &licensingv1alpha1.Autoscaling{
		MaxReplicas: 10,
		MinReplicas: 2,
	}
	environment.Status.License.MaxClusterNodes = pointerInt32(0)
	environment.Status.ReplicaCeiling = pointerInt32(0)

	scaledObject := newScaledObject(1, 1, "dev-liferay")

	scaledObject.SetAnnotations(
		map[string]string{
			annotationPausedReplicas:      "0",
			annotationPausedReplicasOwner: "true",
		},
	)

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{
			entitlements: &provisioning.Entitlements{
				LicenseXML: []byte(virtualClusterLicenseXML(
					"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
				)),
				MaxClusterNodes: 3,
			},
		},
		t,
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Annotations: map[string]string{annotationScaledToZero: "true"},
				Name:        "dev-liferay",
				Namespace:   "liferay-dev",
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: pointerInt32(0),
			},
		},
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
		newHorizontalPodAutoscaler(1, 1, "dev-liferay"),
		scaledObject,
	)

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(2), "statefulSet.spec.replicas", t,
	)

	if _, ok := statefulSet.Annotations[annotationScaledToZero]; ok {
		t.Errorf(
			"statefulSet.metadata.annotations = %v, want the scale to zero marker removed",
			statefulSet.Annotations,
		)
	}

	assertReplicasEqual(
		getEnvironment(liferayEnvironmentReconciler, t).Status.ReplicaCeiling,
		pointerInt32(3),
		"status.replicaCeiling",
		t,
	)

	horizontalPodAutoscaler := getHorizontalPodAutoscaler(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		&horizontalPodAutoscaler.Spec.MaxReplicas,
		pointerInt32(3),
		"horizontalPodAutoscaler.spec.maxReplicas",
		t,
	)

	scaledObject = getScaledObject(liferayEnvironmentReconciler, t)

	if annotations := scaledObject.GetAnnotations(); len(annotations) != 0 {
		t.Errorf("scaledObject.metadata.annotations = %v, want the pause lifted", annotations)
	}

	assertScaledObjectReplicaCount(3, "maxReplicaCount", scaledObject, t)
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
			Type:    conditionProvisioningGracePeriodExpired,
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
		liferayEnvironment.Status.Conditions, conditionProvisioningGracePeriodExpired,
	) != nil {
		t.Error("ProvisioningGracePeriodExpired condition still present, want it cleared after recovery")
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

			if actual := resolveDesiredReplicas(
				liferayEnvironment, testCase.workloadReplicas,
			); actual != testCase.expected {
				t.Errorf("Unexpected resolveDesiredReplicas result: got %d, want %d", actual, testCase.expected)
			}
		})
	}
}
