package licensing

import (
	"context"
	"crypto/rsa"
	"fmt"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	runtime "k8s.io/apimachinery/pkg/runtime"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func (stubProvisioning *stubProvisioning) Activate(
	activationRequest provisioning.ActivationRequest,
	context context.Context,
	privateKey *rsa.PrivateKey,
) error {
	return stubProvisioning.activateError
}

func (stubProvisioning *stubProvisioning) Manifest(
	context context.Context,
	manifestRequest provisioning.ManifestRequest,
	privateKey *rsa.PrivateKey,
) (*provisioning.Entitlements, error) {
	return stubProvisioning.entitlements, stubProvisioning.manifestError
}

func TestBackoffDuration(t *testing.T) {
	retryInitialDelay := 30 * time.Second
	retryMaxDelay := 30 * time.Minute

	testCases := map[string]struct {
		consecutiveFailures int32
		expected            time.Duration
	}{
		"caps backoff at the maximum": {
			consecutiveFailures: 20,
			expected:            retryMaxDelay,
		},
		"first failure uses the initial delay": {
			consecutiveFailures: 1,
			expected:            retryInitialDelay,
		},
		"second failure doubles the initial delay": {
			consecutiveFailures: 2,
			expected:            2 * retryInitialDelay,
		},
		"third failure quadruples the initial delay": {
			consecutiveFailures: 3,
			expected:            4 * retryInitialDelay,
		},
		"zero failures uses the initial delay": {
			consecutiveFailures: 0,
			expected:            retryInitialDelay,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			if actual := backoffDuration(
				testCase.consecutiveFailures, retryInitialDelay, retryMaxDelay,
			); actual != testCase.expected {
				t.Errorf("backoffDuration = %s, want %s", actual, testCase.expected)
			}
		})
	}
}

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

func TestReconcileIsNotBlockedByAddOns(t *testing.T) {
	entitlements := &provisioning.Entitlements{
		AddOns: []provisioning.AddOn{
			{
				DownloadURL:    "://not-a-real-url",
				ProductID:      "broken-app",
				SHA256Checksum: "0000",
			},
		},
		LicenseXML:      []byte(virtualClusterLicenseXML("Friday, March 2, 2029 12:00:00 AM GMT", 3)),
		MaxClusterNodes: 3,
	}

	reconciler, result := reconcileEnvironment(
		t, &stubProvisioning{entitlements: entitlements}, developmentObjects()...,
	)

	liferayEnvironment := getEnvironment(t, reconciler)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready despite the broken add-on", liferayEnvironment.Status.Phase)
	}

	if result.RequeueAfter != 10*time.Minute {
		t.Errorf("RequeueAfter = %s, want the heartbeat 10m", result.RequeueAfter)
	}

	if length := len(getSecret(t, "dev-entitlements", reconciler).Data["add-ons.json"]); length == 0 {
		t.Error("add-ons.json was not written to the entitlements secret")
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

	reconciler, result := reconcileEnvironment(
		t, &stubProvisioning{
			manifestError: fmt.Errorf("provisioning: connection refused"),
		}, objects...,
	)

	if result.RequeueAfter != 30*time.Second {
		t.Errorf("RequeueAfter = %s, want the base backoff 30s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(t, reconciler)

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionProvisioningReachable,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse {
		t.Errorf("ProvisioningReachable condition = %v, want False", condition)
	}

	if liferayEnvironment.Status.ConsecutiveFailures != 1 {
		t.Errorf("ConsecutiveFailures = %d, want 1", liferayEnvironment.Status.ConsecutiveFailures)
	}

	if licenseXML := string(getSecret(t, "dev-entitlements", reconciler).Data["license.xml"]); licenseXML != "<license>known-good</license>" {
		t.Errorf("license.xml = %q, want the retained last-known-good", licenseXML)
	}
}

func TestReconcileWritesExpiredLicenseThrough(t *testing.T) {
	expiredLicenseXML := virtualClusterLicenseXML(
		"Wednesday, January 1, 2020 12:00:00 AM GMT", 1,
	)

	reconciler, _ := reconcileEnvironment(
		t, &stubProvisioning{
			entitlements: &provisioning.Entitlements{
				LicenseXML:      []byte(expiredLicenseXML),
				MaxClusterNodes: 1,
			},
		}, developmentObjects()...,
	)

	if licenseXML := string(getSecret(t, "dev-entitlements", reconciler).Data["license.xml"]); licenseXML != expiredLicenseXML {
		t.Errorf("license.xml = %q, want the expired license written through", licenseXML)
	}

	liferayEnvironment := getEnvironment(t, reconciler)

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

func getEnvironment(
	t *testing.T,
	reconciler *LiferayEnvironmentReconciler,
) *licensingv1alpha1.LiferayEnvironment {
	t.Helper()

	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{}

	if error := reconciler.Get(
		context.Background(), types.NamespacedName{
			Name:      "dev",
			Namespace: "liferay-dev",
		}, liferayEnvironment); error != nil {
		t.Fatalf("Unable to read the environment: %v", error)
	}

	return liferayEnvironment
}

func getSecret(
	t *testing.T,
	name string,
	reconciler *LiferayEnvironmentReconciler,
) *corev1.Secret {
	t.Helper()

	secret := &corev1.Secret{}

	if error := reconciler.Get(
		context.Background(), types.NamespacedName{
			Name:      name,
			Namespace: "liferay-dev",
		}, secret); error != nil {
		t.Fatalf("Unable to read the secret %q: %v", name, error)
	}

	return secret
}

func newFakeClient(t *testing.T, objects ...client.Object) client.Client {
	t.Helper()

	scheme := runtime.NewScheme()

	if error := appsv1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the apps/v1 scheme: %v", error)
	}

	if error := corev1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the core/v1 scheme: %v", error)
	}

	if error := licensingv1alpha1.AddToScheme(scheme); error != nil {
		t.Fatalf("Unable to register the licensing scheme: %v", error)
	}

	return fake.NewClientBuilder().WithObjects(
		objects...,
	).WithScheme(
		scheme,
	).WithStatusSubresource(
		&licensingv1alpha1.LiferayEnvironment{},
	).Build()
}

func pointerInt32(value int32) *int32 {
	return &value
}

func reconcileEnvironment(
	t *testing.T,
	provisioningClient provisioning.Client,
	objects ...client.Object,
) (*LiferayEnvironmentReconciler, controllerruntime.Result) {
	t.Helper()

	reconciler := &LiferayEnvironmentReconciler{
		Client:            newFakeClient(t, objects...),
		HeartbeatInterval: 10 * time.Minute,
		Provisioning:      provisioningClient,
		RetryInitialDelay: 30 * time.Second,
		RetryMaxDelay:     30 * time.Minute,
	}

	result, error := reconciler.Reconcile(
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

	return reconciler, result
}

func virtualClusterLicenseXML(expirationDate string, maxClusterNodes int32) string {
	return fmt.Sprintf(
		"<licenses><license>"+
			"<expiration-date>%s</expiration-date>"+
			"<license-type>virtual-cluster</license-type>"+
			"<max-cluster-nodes>%d</max-cluster-nodes>"+
			"</license></licenses>",
		expirationDate, maxClusterNodes,
	)
}

type stubProvisioning struct {
	activateError error
	entitlements  *provisioning.Entitlements
	manifestError error
}
