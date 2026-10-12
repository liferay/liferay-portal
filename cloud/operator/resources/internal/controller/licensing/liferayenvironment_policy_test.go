package licensing

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	appsv1 "k8s.io/api/apps/v1"
	autoscalingv1 "k8s.io/api/autoscaling/v1"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	unstructured "k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	types "k8s.io/apimachinery/pkg/types"
	yaml "k8s.io/apimachinery/pkg/util/yaml"
	rest "k8s.io/client-go/rest"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	envtest "sigs.k8s.io/controller-runtime/pkg/envtest"
)

const (
	chartDir = "../../../../../helm/dxp-operator"

	operatorUsername = "system:serviceaccount:liferay-system:liferay-dxp-operator"

	policyEnforcementTimeout = 60 * time.Second

	policyProbeNamespace = "liferay-policy-probe"
)

func TestAdmissionPolicyDeniesOverLimitReplicas(t *testing.T) {
	config := startPolicyEnvironment(t)

	testCases := map[string]struct {
		labelNamespace       bool
		licensedWorkloadKind licensingv1alpha1.WorkloadKind
		maxClusterNodes      *int32
		namespaceName        string
		replicaCeiling       *int32
		scaleSubresource     bool
		shouldDeny           bool
		workloadKind         licensingv1alpha1.WorkloadKind
	}{
		"a licensed deployment allows a stateful set of the same name": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindDeployment,
			maxClusterNodes:      pointerInt32(1),
			namespaceName:        "liferay-other-kind",
			replicaCeiling:       pointerInt32(1),
			shouldDeny:           false,
			workloadKind:         licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"a licensed deployment denies an over limit scale": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindDeployment,
			maxClusterNodes:      pointerInt32(1),
			namespaceName:        "liferay-deployment-scale",
			replicaCeiling:       pointerInt32(1),
			scaleSubresource:     true,
			shouldDeny:           true,
			workloadKind:         licensingv1alpha1.WorkloadKindDeployment,
		},
		"a licensed deployment denies an over limit write": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindDeployment,
			maxClusterNodes:      pointerInt32(1),
			namespaceName:        "liferay-deployment",
			replicaCeiling:       pointerInt32(1),
			shouldDeny:           true,
			workloadKind:         licensingv1alpha1.WorkloadKindDeployment,
		},
		"a licensed environment denies an over limit scale": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindStatefulSet,
			maxClusterNodes:      pointerInt32(1),
			namespaceName:        "liferay-scale",
			replicaCeiling:       pointerInt32(1),
			scaleSubresource:     true,
			shouldDeny:           true,
			workloadKind:         licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"a licensed environment denies an over limit write": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindStatefulSet,
			maxClusterNodes:      pointerInt32(1),
			namespaceName:        "liferay-licensed",
			replicaCeiling:       pointerInt32(1),
			shouldDeny:           true,
			workloadKind:         licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"a licensed maximum without a replica ceiling denies an over limit write": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindStatefulSet,
			maxClusterNodes:      pointerInt32(1),
			namespaceName:        "liferay-fallback",
			replicaCeiling:       nil,
			shouldDeny:           true,
			workloadKind:         licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"a replica ceiling below the licensed maximum denies an over limit write": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindStatefulSet,
			maxClusterNodes:      pointerInt32(3),
			namespaceName:        "liferay-ceiling",
			replicaCeiling:       pointerInt32(1),
			shouldDeny:           true,
			workloadKind:         licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"any namespace the policy does not gate allows any write": {
			labelNamespace:       false,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindStatefulSet,
			maxClusterNodes:      pointerInt32(1),
			namespaceName:        "liferay-any",
			replicaCeiling:       pointerInt32(1),
			shouldDeny:           false,
			workloadKind:         licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"any namespace without a ceiling allows any write": {
			labelNamespace:       true,
			licensedWorkloadKind: licensingv1alpha1.WorkloadKindStatefulSet,
			maxClusterNodes:      nil,
			namespaceName:        "liferay-unlicensed",
			replicaCeiling:       nil,
			shouldDeny:           false,
			workloadKind:         licensingv1alpha1.WorkloadKindStatefulSet,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			writeClient := newPolicyFixture(
				config, testCase.labelNamespace, testCase.licensedWorkloadKind,
				testCase.maxClusterNodes, testCase.namespaceName,
				testCase.replicaCeiling, t, testCase.workloadKind,
			)

			error := attemptOverLimitWrite(
				testCase.namespaceName, testCase.scaleSubresource, t,
				testCase.workloadKind, writeClient,
			)

			if testCase.shouldDeny && !errors.IsForbidden(error) {
				error = awaitForbidden(
					testCase.namespaceName, testCase.scaleSubresource, t,
					testCase.workloadKind, writeClient,
				)
			}

			if testCase.shouldDeny {
				if !errors.IsForbidden(error) {
					t.Errorf("Write error = %v, want a forbidden response", error)
				} else if !strings.Contains(
					error.Error(), "ValidatingAdmissionPolicy",
				) {
					t.Errorf(
						"Write error = %v, want the admission policy to be the one refusing",
						error,
					)
				}
			}

			if !testCase.shouldDeny && error != nil {
				t.Errorf("Write error = %v, want the write to be admitted", error)
			}
		})
	}
}

func TestReconcileWithAdmissionPolicy(t *testing.T) {
	config := startPolicyEnvironment(t)

	testCases := map[string]struct {
		impersonateOperator bool
		namespaceName       string
		workloadKind        licensingv1alpha1.WorkloadKind
	}{
		"the exempt operator restores the ceiling": {
			impersonateOperator: true,
			namespaceName:       "liferay-exempt",
			workloadKind:        licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"the operator the policy validates restores the ceiling": {
			impersonateOperator: false,
			namespaceName:       "liferay-validated",
			workloadKind:        licensingv1alpha1.WorkloadKindStatefulSet,
		},
		"the operator the policy validates restores the ceiling of a deployment": {
			impersonateOperator: false,
			namespaceName:       "liferay-validated-deployment",
			workloadKind:        licensingv1alpha1.WorkloadKindDeployment,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironmentReconciler := newPolicyReconciler(
				config, testCase.impersonateOperator, testCase.namespaceName, t,
				testCase.workloadKind,
			)

			_, error := liferayEnvironmentReconciler.Reconcile(
				context.Background(), controllerruntime.Request{
					NamespacedName: types.NamespacedName{
						Name:      "dev",
						Namespace: testCase.namespaceName,
					},
				},
			)

			if error != nil {
				t.Logf("The workload update was rejected: %v", error)
			}

			var liferayEnvironment licensingv1alpha1.LiferayEnvironment

			if error := liferayEnvironmentReconciler.Get(
				context.Background(),
				types.NamespacedName{
					Name:      "dev",
					Namespace: testCase.namespaceName,
				},
				&liferayEnvironment,
			); error != nil {
				t.Fatalf("Unable to read the environment: %v", error)
			}

			maxClusterNodes := liferayEnvironment.Status.License.MaxClusterNodes

			if maxClusterNodes == nil {
				t.Fatal(
					"License.MaxClusterNodes = <nil>, want maxClusterNodes 3 persisted so that the next attempt is admitted",
				)
			}

			if *maxClusterNodes != 3 {
				t.Errorf(
					"License.MaxClusterNodes = %d, want maxClusterNodes 3 persisted so that the next attempt is admitted",
					*maxClusterNodes,
				)
			}

			assertReplicasEqual(
				liferayEnvironment.Status.ReplicaCeiling, pointerInt32(3),
				"status.replicaCeiling", t,
			)

			workload, error := liferayEnvironmentReconciler.getWorkload(
				context.Background(), &liferayEnvironment,
			)

			if error != nil {
				t.Fatalf("Unable to read the workload: %v", error)
			}

			assertReplicasEqual(
				getReplicas(workload), pointerInt32(3), "Replicas", t,
			)
		})
	}
}

func TestReconcileWithAdmissionPolicyHoldsProvisioningGracePeriodCeiling(t *testing.T) {
	config := startPolicyEnvironment(t)

	namespaceName := "liferay-grace-period"

	liferayEnvironmentReconciler := newPolicyReconciler(
		config, true, namespaceName, t, licensingv1alpha1.WorkloadKindStatefulSet,
	)

	liferayEnvironmentReconciler.Provisioning = &stubProvisioning{
		manifestError: fmt.Errorf("provisioning: connection refused"),
	}
	liferayEnvironmentReconciler.ProvisioningGracePeriod = 168 * time.Hour
	liferayEnvironmentReconciler.RetryInitialDelay = 30 * time.Second
	liferayEnvironmentReconciler.RetryMaxDelay = 30 * time.Minute

	var liferayEnvironment licensingv1alpha1.LiferayEnvironment

	if error := liferayEnvironmentReconciler.Get(
		context.Background(),
		types.NamespacedName{Name: "dev", Namespace: namespaceName},
		&liferayEnvironment,
	); error != nil {
		t.Fatalf("Unable to read the environment: %v", error)
	}

	unreachableSince := metav1.NewTime(time.Now().Add(-8 * 24 * time.Hour))

	liferayEnvironment.Status.License.MaxClusterNodes = pointerInt32(3)
	liferayEnvironment.Status.ReplicaCeiling = pointerInt32(3)
	liferayEnvironment.Status.UnreachableSince = &unreachableSince

	if error := liferayEnvironmentReconciler.Status().Update(
		context.Background(), &liferayEnvironment,
	); error != nil {
		t.Fatalf("Unable to write the last known good license: %v", error)
	}

	if _, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: namespaceName,
			},
		},
	); error != nil {
		t.Fatalf("Unexpected error from Reconcile: %v", error)
	}

	if error := liferayEnvironmentReconciler.Get(
		context.Background(),
		types.NamespacedName{Name: "dev", Namespace: namespaceName},
		&liferayEnvironment,
	); error != nil {
		t.Fatalf("Unable to read the environment: %v", error)
	}

	assertReplicasEqual(
		liferayEnvironment.Status.ReplicaCeiling, pointerInt32(1),
		"status.replicaCeiling", t,
	)

	writeClient, error := client.New(config, client.Options{Scheme: newScheme(t)})

	if error != nil {
		t.Fatalf("Unable to build a client: %v", error)
	}

	if error := awaitForbidden(
		namespaceName, true, t, licensingv1alpha1.WorkloadKindStatefulSet, writeClient,
	); !errors.IsForbidden(error) {
		t.Errorf(
			"Scale error = %v, want the grace period ceiling to refuse an autoscaler scaling back up to the licensed maximum",
			error,
		)
	}
}

func attemptOverLimitWrite(
	namespaceName string, scaleSubresource bool, t *testing.T,
	workloadKind licensingv1alpha1.WorkloadKind, writeClient client.Client,
) error {
	t.Helper()

	workloadRef := licensingv1alpha1.WorkloadRef{Kind: workloadKind}

	workload := workloadRef.NewObject()

	if error := writeClient.Get(
		context.Background(),
		types.NamespacedName{Name: "dev-liferay", Namespace: namespaceName},
		workload,
	); error != nil {
		t.Fatalf("Unable to read the workload: %v", error)
	}

	return writeOverLimitReplicas(scaleSubresource, workload, writeClient)
}

func awaitForbidden(
	namespaceName string, scaleSubresource bool, t *testing.T,
	workloadKind licensingv1alpha1.WorkloadKind, writeClient client.Client,
) error {
	t.Helper()

	deadline := time.Now().Add(policyEnforcementTimeout)

	for {
		error := attemptOverLimitWrite(
			namespaceName, scaleSubresource, t, workloadKind, writeClient,
		)

		if errors.IsForbidden(error) || time.Now().After(deadline) {
			return error
		}

		resetReplicas(namespaceName, t, workloadKind, writeClient)

		time.Sleep(100 * time.Millisecond)
	}
}

func awaitPolicyEnforcement(config *rest.Config, t *testing.T) {
	t.Helper()

	writeClient := newPolicyFixture(
		config, true, licensingv1alpha1.WorkloadKindStatefulSet, pointerInt32(1),
		policyProbeNamespace, pointerInt32(1), t,
		licensingv1alpha1.WorkloadKindStatefulSet,
	)

	if error := awaitForbidden(
		policyProbeNamespace, false, t,
		licensingv1alpha1.WorkloadKindStatefulSet, writeClient,
	); !errors.IsForbidden(error) {
		t.Fatalf(
			"The admission policy admitted an over limit write for %v, so it is installed but inert and no assertion below would be meaningful: %v\n%s",
			policyEnforcementTimeout, error, describePolicyState(config, t),
		)
	}
}

func describePolicyState(config *rest.Config, t *testing.T) string {
	t.Helper()

	report := &strings.Builder{}

	describeClient, error := client.New(config, client.Options{Scheme: newScheme(t)})

	if error != nil {
		return "Unable to build a client to describe the policy state"
	}

	for _, kind := range []string{
		"ValidatingAdmissionPolicy", "ValidatingAdmissionPolicyBinding",
	} {
		var object unstructured.Unstructured

		object.SetAPIVersion("admissionregistration.k8s.io/v1")
		object.SetKind(kind)

		if error := describeClient.Get(
			context.Background(),
			types.NamespacedName{Name: "liferay-dxp-operator-workload-scale"},
			&object,
		); error != nil {
			fmt.Fprintf(report, "%s: unreadable: %v\n", kind, error)

			continue
		}

		fmt.Fprintf(
			report, "%s: present, generation %d, status %v\n",
			kind, object.GetGeneration(), object.Object["status"],
		)
	}

	var namespace corev1.Namespace

	if error := describeClient.Get(
		context.Background(),
		types.NamespacedName{Name: policyProbeNamespace}, &namespace,
	); error == nil {
		fmt.Fprintf(report, "Probe namespace labels: %v\n", namespace.Labels)
	}

	var liferayEnvironments licensingv1alpha1.LiferayEnvironmentList

	if error := describeClient.List(
		context.Background(), &liferayEnvironments,
		client.InNamespace(policyProbeNamespace),
	); error != nil {
		fmt.Fprintf(report, "Params: unlistable: %v\n", error)

		return report.String()
	}

	for _, liferayEnvironment := range liferayEnvironments.Items {
		maxClusterNodes := "<nil>"

		if liferayEnvironment.Status.License.MaxClusterNodes != nil {
			maxClusterNodes = fmt.Sprint(
				*liferayEnvironment.Status.License.MaxClusterNodes,
			)
		}

		replicaCeiling := "<nil>"

		if liferayEnvironment.Status.ReplicaCeiling != nil {
			replicaCeiling = fmt.Sprint(*liferayEnvironment.Status.ReplicaCeiling)
		}

		fmt.Fprintf(
			report, "Param %s: maxClusterNodes %s, replicaCeiling %s\n",
			liferayEnvironment.Name, maxClusterNodes, replicaCeiling,
		)
	}

	return report.String()
}

func envtestAssetsDir(t *testing.T) string {
	t.Helper()

	if assetsDir := os.Getenv("KUBEBUILDER_ASSETS"); assetsDir != "" {
		return assetsDir
	}

	assetsDir, error := envtest.SetupEnvtestDefaultBinaryAssetsDirectory()

	if error != nil {
		return ""
	}

	matches, error := filepath.Glob(filepath.Join(assetsDir, "*"))

	if error != nil || len(matches) == 0 {
		return ""
	}

	return matches[len(matches)-1]
}

func installAdmissionPolicy(config *rest.Config, t *testing.T) {
	t.Helper()

	command := exec.Command(
		"helm", "template", "liferay-dxp-operator", chartDir,
		"--namespace", "liferay-system",
		"--show-only", "templates/validating-admission-policy.yaml",
	)

	rendered, error := command.Output()

	if error != nil {
		t.Fatalf("Unable to render the admission policy: %v", error)
	}

	policyClient, error := client.New(config, client.Options{})

	if error != nil {
		t.Fatalf("Unable to build a client: %v", error)
	}

	decoder := yaml.NewYAMLOrJSONDecoder(bytes.NewReader(rendered), 4096)

	installedKinds := map[string]bool{}

	for {
		var object unstructured.Unstructured

		error := decoder.Decode(&object)

		if error == io.EOF {
			break
		}

		if error != nil {
			t.Fatalf("Unable to decode the rendered admission policy: %v", error)
		}

		if len(object.Object) == 0 {
			continue
		}

		if error := policyClient.Create(
			context.Background(), &object,
		); error != nil {
			t.Fatalf(
				"Unable to install %s %s: %v",
				object.GetKind(), object.GetName(), error,
			)
		}

		installedKinds[object.GetKind()] = true
	}

	for _, kind := range []string{
		"ValidatingAdmissionPolicy", "ValidatingAdmissionPolicyBinding",
	} {
		if !installedKinds[kind] {
			t.Fatalf(
				"The rendered chart installed no %s, so nothing would enforce the ceiling",
				kind,
			)
		}
	}
}

func newPolicyFixture(
	config *rest.Config, labelNamespace bool,
	licensedWorkloadKind licensingv1alpha1.WorkloadKind, maxClusterNodes *int32,
	namespaceName string, replicaCeiling *int32, t *testing.T,
	workloadKind licensingv1alpha1.WorkloadKind,
) client.Client {
	t.Helper()

	setUpClient, error := client.New(config, client.Options{Scheme: newScheme(t)})

	if error != nil {
		t.Fatalf("Unable to build a client: %v", error)
	}

	labels := map[string]string{}

	if labelNamespace {
		labels[environmentLabel] = "true"
	}

	if error := setUpClient.Create(
		context.Background(),
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Labels: labels,
				Name:   namespaceName,
			},
		},
	); error != nil {
		t.Fatalf("Unable to create the namespace: %v", error)
	}

	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: namespaceName,
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			ActivationCodeSecretRef: licensingv1alpha1.SecretKeyRef{
				Key:  "activationCode",
				Name: "dev-activation",
			},
			DesiredReplicas: pointerInt32(3),
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Kind: licensedWorkloadKind,
				Name: "dev-liferay",
			},
		},
	}

	if error := setUpClient.Create(
		context.Background(), liferayEnvironment,
	); error != nil {
		t.Fatalf("Unable to create the environment: %v", error)
	}

	liferayEnvironment.Status.License.MaxClusterNodes = maxClusterNodes
	liferayEnvironment.Status.ReplicaCeiling = replicaCeiling

	if error := setUpClient.Status().Update(
		context.Background(), liferayEnvironment,
	); error != nil {
		t.Fatalf("Unable to write the ceiling: %v", error)
	}

	if maxClusterNodes != nil || replicaCeiling != nil {
		var persisted licensingv1alpha1.LiferayEnvironment

		if error := setUpClient.Get(
			context.Background(),
			types.NamespacedName{Name: "dev", Namespace: namespaceName},
			&persisted,
		); error != nil {
			t.Fatalf("Unable to read the ceiling back: %v", error)
		}

		if maxClusterNodes != nil && persisted.Status.License.MaxClusterNodes == nil {
			t.Fatal(
				"License.MaxClusterNodes = <nil> after the status write, so the policy would find no ceiling and admit every write",
			)
		}

		if replicaCeiling != nil && persisted.Status.ReplicaCeiling == nil {
			t.Fatal(
				"ReplicaCeiling = <nil> after the status write, so the policy would read the licensed maximum instead",
			)
		}
	}

	if error := setUpClient.Create(
		context.Background(), newWorkload(namespaceName, workloadKind),
	); error != nil {
		t.Fatalf("Unable to create the workload: %v", error)
	}

	return setUpClient
}

func newPolicyReconciler(
	config *rest.Config, impersonateOperator bool, namespaceName string,
	t *testing.T, workloadKind licensingv1alpha1.WorkloadKind,
) *LiferayEnvironmentReconciler {
	t.Helper()

	setUpClient, error := client.New(config, client.Options{Scheme: newScheme(t)})

	if error != nil {
		t.Fatalf("Unable to build a client: %v", error)
	}

	namespace := &corev1.Namespace{
		ObjectMeta: metav1.ObjectMeta{
			Labels: map[string]string{environmentLabel: "true"},
			Name:   namespaceName,
		},
	}

	if error := setUpClient.Create(context.Background(), namespace); error != nil {
		t.Fatalf("Unable to create the namespace: %v", error)
	}

	activatedAt := metav1.Now()

	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: namespaceName,
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			ActivationCodeSecretRef: licensingv1alpha1.SecretKeyRef{
				Key:  "activationCode",
				Name: "dev-activation",
			},
			DesiredReplicas: pointerInt32(3),
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Kind: workloadKind,
				Name: "dev-liferay",
			},
		},
	}

	if error := setUpClient.Create(
		context.Background(), liferayEnvironment,
	); error != nil {
		t.Fatalf("Unable to create the environment: %v", error)
	}

	liferayEnvironment.Status.ActivatedAt = &activatedAt
	liferayEnvironment.Status.License.MaxClusterNodes = pointerInt32(0)
	liferayEnvironment.Status.ReplicaCeiling = pointerInt32(0)

	if error := setUpClient.Status().Update(
		context.Background(), liferayEnvironment,
	); error != nil {
		t.Fatalf("Unable to write the blocked ceiling: %v", error)
	}

	if error := setUpClient.Create(
		context.Background(), newWorkload(namespaceName, workloadKind),
	); error != nil {
		t.Fatalf("Unable to create the workload: %v", error)
	}

	reconcilerConfig := rest.CopyConfig(config)

	if impersonateOperator {
		reconcilerConfig.Impersonate = rest.ImpersonationConfig{
			Groups:   []string{"system:masters"},
			UserName: operatorUsername,
		}
	}

	reconcilerClient, error := client.New(
		reconcilerConfig, client.Options{Scheme: newScheme(t)},
	)

	if error != nil {
		t.Fatalf("Unable to build the reconciler client: %v", error)
	}

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML: []byte(virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3,
				string(namespace.UID),
			)),
			MaxClusterNodes: 3,
		},
	}

	return &LiferayEnvironmentReconciler{
		Client:                reconcilerClient,
		ExpirationGracePeriod: 90 * 24 * time.Hour,
		HeartbeatInterval:     10 * time.Minute,
		MarketplaceMountPath:  t.TempDir(),
		Provisioning:          provisioningClient,
		Recorder:              record.NewFakeRecorder(10),
		Syncer: addon.NewSyncer(
			provisioningClient, 15*time.Second, 30*time.Second, 30*time.Minute,
			inlineRunner{},
		),
	}
}

func newWorkload(
	namespaceName string, workloadKind licensingv1alpha1.WorkloadKind,
) client.Object {
	labels := map[string]string{"app": "dev-liferay"}

	objectMeta := metav1.ObjectMeta{
		Name:      "dev-liferay",
		Namespace: namespaceName,
	}

	podTemplate := corev1.PodTemplateSpec{
		ObjectMeta: metav1.ObjectMeta{Labels: labels},
		Spec: corev1.PodSpec{
			Containers: []corev1.Container{
				{
					Image: "registry.k8s.io/pause:3.9",
					Name:  "liferay",
				},
			},
		},
	}

	if workloadKind == licensingv1alpha1.WorkloadKindDeployment {
		return &appsv1.Deployment{
			ObjectMeta: objectMeta,
			Spec: appsv1.DeploymentSpec{
				Replicas: pointerInt32(0),
				Selector: &metav1.LabelSelector{MatchLabels: labels},
				Template: podTemplate,
			},
		}
	}

	return &appsv1.StatefulSet{
		ObjectMeta: objectMeta,
		Spec: appsv1.StatefulSetSpec{
			Replicas:    pointerInt32(0),
			Selector:    &metav1.LabelSelector{MatchLabels: labels},
			ServiceName: "dev-liferay",
			Template:    podTemplate,
		},
	}
}

func resetReplicas(
	namespaceName string, t *testing.T,
	workloadKind licensingv1alpha1.WorkloadKind, writeClient client.Client,
) {
	t.Helper()

	workloadRef := licensingv1alpha1.WorkloadRef{Kind: workloadKind}

	workload := workloadRef.NewObject()

	if error := writeClient.Get(
		context.Background(),
		types.NamespacedName{Name: "dev-liferay", Namespace: namespaceName},
		workload,
	); error != nil {
		return
	}

	setReplicas(pointerInt32(0), workload)

	writeClient.Update(context.Background(), workload)
}

func startPolicyEnvironment(t *testing.T) *rest.Config {
	t.Helper()

	assetsDir := envtestAssetsDir(t)

	if assetsDir == "" {
		t.Skip(
			"Set KUBEBUILDER_ASSETS, or install the envtest binaries with setup-envtest, to run this test",
		)
	}

	if _, error := exec.LookPath("helm"); error != nil {
		t.Skip("Install helm to render the admission policy this test installs")
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
			t.Logf("Unable to stop the test environment: %v", error)
		}
	})

	installAdmissionPolicy(config, t)

	awaitPolicyEnforcement(config, t)

	return config
}

func writeOverLimitReplicas(
	scaleSubresource bool, workload client.Object, writeClient client.Client,
) error {
	if scaleSubresource {
		return writeClient.SubResource("scale").Update(
			context.Background(), workload,
			client.WithSubResourceBody(
				&autoscalingv1.Scale{
					ObjectMeta: metav1.ObjectMeta{
						Name:      workload.GetName(),
						Namespace: workload.GetNamespace(),
					},
					Spec: autoscalingv1.ScaleSpec{Replicas: 3},
				},
			),
		)
	}

	setReplicas(pointerInt32(3), workload)

	return writeClient.Update(context.Background(), workload)
}
