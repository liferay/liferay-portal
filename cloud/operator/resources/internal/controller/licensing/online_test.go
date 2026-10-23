package licensing

import (
	"context"
	"fmt"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

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

func TestReconcileActivatesWithEnvironmentName(t *testing.T) {
	testCases := map[string]struct {
		environmentName         string
		expectedEnvironmentName string
	}{
		"named environment sends its name": {
			environmentName:         "prod",
			expectedEnvironmentName: "prod",
		},
		"unnamed environment sends its namespace": {
			expectedEnvironmentName: "liferay-dev",
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			environment := pendingEnvironment()
			environment.Spec.EnvironmentName = testCase.environmentName

			provisioningClient := &stubProvisioning{
				entitlements: &provisioning.Entitlements{},
			}

			reconcileEnvironment(
				provisioningClient, t,
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
				environment,
			)

			if !provisioningClient.activateCalled {
				t.Fatal("Activate was not called")
			}

			if provisioningClient.activationRequest.EnvironmentName != testCase.expectedEnvironmentName {
				t.Errorf(
					"ActivationRequest.EnvironmentName = %q, want %q",
					provisioningClient.activationRequest.EnvironmentName,
					testCase.expectedEnvironmentName,
				)
			}
		})
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

func TestResolveDXPVersion(t *testing.T) {
	testCases := map[string]struct {
		dxpVersion   string
		expected     string
		image        string
		workloadKind licensingv1alpha1.WorkloadKind
	}{
		"detects the tag of a deployment": {
			expected:     "2026.q1.0-lts",
			image:        "liferay/dxp:2026.q1.0-lts",
			workloadKind: licensingv1alpha1.WorkloadKindDeployment,
		},
		"detects the tag of a stateful set": {
			expected:     "2026.q1.0-lts",
			image:        "liferay/dxp:2026.q1.0-lts",
			workloadKind: licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"ignores a mirror under another repository name": {
			expected:     "",
			image:        "registry.example.com/mirror/portal:2026.q1.0-lts",
			workloadKind: licensingv1alpha1.WorkloadKindDeployment,
		},
		"prefers the declared version": {
			dxpVersion:   "2025.q4.0",
			expected:     "2025.q4.0",
			image:        "liferay/dxp:2026.q1.0-lts",
			workloadKind: licensingv1alpha1.WorkloadKindDeployment,
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
					DxpVersion: testCase.dxpVersion,
					WorkloadRef: licensingv1alpha1.WorkloadRef{
						Kind: testCase.workloadKind,
						Name: "dev-liferay",
					},
				},
			}

			workload := newDevelopmentWorkload(nil, testCase.workloadKind)

			getPodTemplate(workload).Spec.Containers = []corev1.Container{
				{
					Image: testCase.image,
					Name:  "liferay",
				},
			}

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(t, liferayEnvironment, workload),
			}

			if actual := liferayEnvironmentReconciler.resolveDXPVersion(
				context.Background(), liferayEnvironment,
			); actual != testCase.expected {
				t.Errorf("resolveDXPVersion = %q, want %q", actual, testCase.expected)
			}
		})
	}
}
