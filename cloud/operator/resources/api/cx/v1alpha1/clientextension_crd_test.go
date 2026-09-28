package v1alpha1

import (
	"context"
	"encoding/json"
	"os"
	"path/filepath"
	"slices"
	"strings"
	"testing"

	apiextensionsv1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	unstructured "k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	runtime "k8s.io/apimachinery/pkg/runtime"
	types "k8s.io/apimachinery/pkg/types"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	envtest "sigs.k8s.io/controller-runtime/pkg/envtest"
)

const (
	chartDir = "../../../../../helm/dxp-operator"

	customResourceDefinitionName = "clientextensions.cx.liferay.com"

	namespace = metav1.NamespaceDefault
)

func TestCRDAcceptsConfigurationOnlyClientExtension(t *testing.T) {
	testClient := startEnvironment(t)

	clientExtension := validClientExtension("configuration-only")

	clientExtension.Spec.WorkloadRef = nil

	if error := testClient.Create(context.Background(), clientExtension); error != nil {
		t.Errorf("Expected a ClientExtension with no workload to be accepted, got %v", error)
	}
}

func TestCRDAcceptsLiferayNamespace(t *testing.T) {
	testClient := startEnvironment(t)

	clientExtension := validClientExtension("cross-namespace")

	clientExtension.Spec.LiferayNamespace = "liferay-prod"

	if error := testClient.Create(context.Background(), clientExtension); error != nil {
		t.Errorf("Expected a ClientExtension naming Liferay's namespace to be accepted, got %v", error)
	}
}

func TestCRDExposesTheColumnsAndShortName(t *testing.T) {
	testClient := startEnvironment(t)

	var customResourceDefinition apiextensionsv1.CustomResourceDefinition

	if error := testClient.Get(
		context.Background(), types.NamespacedName{Name: customResourceDefinitionName}, &customResourceDefinition,
	); error != nil {
		t.Fatalf("Unable to get the CRD: %v", error)
	}

	if !slices.Contains(customResourceDefinition.Spec.Names.ShortNames, "cx") {
		t.Errorf("Expected the short name %q, got %v", "cx", customResourceDefinition.Spec.Names.ShortNames)
	}

	var columns []string

	for _, column := range customResourceDefinition.Spec.Versions[0].AdditionalPrinterColumns {
		columns = append(columns, column.Name)
	}

	for _, expected := range []string{
		"Config-Accepted", "DXP-Namespace", "Delivered", "Phase", "Provisioned",
		"Virtual-Instance", "Workload",
	} {
		if !slices.Contains(columns, expected) {
			t.Errorf("Expected a %q column, got %v", expected, columns)
		}
	}
}

func TestCRDKeepsStatusBehindTheSubresource(t *testing.T) {
	testClient := startEnvironment(t)

	clientExtension := validClientExtension("status")

	if error := testClient.Create(context.Background(), clientExtension); error != nil {
		t.Fatalf("Unable to create a valid ClientExtension: %v", error)
	}

	clientExtension.Status.Phase = PhaseReady

	if error := testClient.Update(context.Background(), clientExtension); error != nil {
		t.Fatalf("Unable to update the ClientExtension: %v", error)
	}

	if clientExtension.Status.Phase != "" {
		t.Errorf("Expected a spec update to leave the phase empty, got %q", clientExtension.Status.Phase)
	}

	clientExtension.Status.Phase = PhaseReady

	if error := testClient.Status().Update(context.Background(), clientExtension); error != nil {
		t.Fatalf("Unable to update the status: %v", error)
	}

	if clientExtension.Status.Phase != PhaseReady {
		t.Errorf(
			"Expected the status update to set the phase to %q, got %q",
			PhaseReady, clientExtension.Status.Phase,
		)
	}
}

func TestCRDRejectsInvalidClientExtensions(t *testing.T) {
	testClient := startEnvironment(t)

	testCases := map[string]struct {
		mutate func(object map[string]any)
	}{
		"a liferayNamespace longer than a namespace name": {
			mutate: func(object map[string]any) {
				spec(object)["liferayNamespace"] = strings.Repeat("a", 64)
			},
		},
		"a liferayNamespace that is not a namespace name": {
			mutate: func(object map[string]any) {
				spec(object)["liferayNamespace"] = "Liferay_Prod"
			},
		},
		"a workloadRef with no name": {
			mutate: func(object map[string]any) {
				delete(spec(object)["workloadRef"].(map[string]any), "name")
			},
		},
		"an empty serviceId": {
			mutate: func(object map[string]any) {
				spec(object)["serviceId"] = ""
			},
		},
		"an empty virtualInstanceId": {
			mutate: func(object map[string]any) {
				spec(object)["virtualInstanceId"] = ""
			},
		},
		"an unsupported workload kind": {
			mutate: func(object map[string]any) {
				spec(object)["workloadRef"].(map[string]any)["kind"] = "StatefulSet"
			},
		},
		"no serviceId": {
			mutate: func(object map[string]any) {
				delete(spec(object), "serviceId")
			},
		},
		"no virtualInstanceId": {
			mutate: func(object map[string]any) {
				delete(spec(object), "virtualInstanceId")
			},
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			object := unstructuredClientExtension("invalid", t)

			testCase.mutate(object.Object)

			error := testClient.Create(context.Background(), object)

			if !errors.IsInvalid(error) {
				t.Errorf("Expected the API server to reject %s, got %v", name, error)
			}
		})
	}
}

func TestMain(m *testing.M) {
	var environment *envtest.Environment

	if assetsDir := envtestAssetsDir(); assetsDir != "" {
		environment = &envtest.Environment{
			BinaryAssetsDirectory: assetsDir,
			CRDDirectoryPaths:     []string{filepath.Join(chartDir, "crds")},
			ErrorIfCRDPathMissing: true,
		}

		testClient, environmentError = connect(environment)
	}

	code := m.Run()

	if environment != nil {
		environment.Stop()
	}

	os.Exit(code)
}

func connect(environment *envtest.Environment) (client.Client, error) {
	config, error := environment.Start()

	if error != nil {
		return nil, error
	}

	scheme := runtime.NewScheme()

	if error := AddToScheme(scheme); error != nil {
		return nil, error
	}

	if error := apiextensionsv1.AddToScheme(scheme); error != nil {
		return nil, error
	}

	return client.New(config, client.Options{Scheme: scheme})
}

func envtestAssetsDir() string {
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

func spec(object map[string]any) map[string]any {
	return object["spec"].(map[string]any)
}

func startEnvironment(t *testing.T) client.Client {
	t.Helper()

	if environmentError != nil {
		t.Fatalf("Unable to start the test environment: %v", environmentError)
	}

	if testClient == nil {
		t.Skip(
			"Set KUBEBUILDER_ASSETS, or install the envtest binaries with setup-envtest, to run this test",
		)
	}

	return testClient
}

func unstructuredClientExtension(name string, t *testing.T) *unstructured.Unstructured {
	t.Helper()

	encoded, error := json.Marshal(validClientExtension(name))

	if error != nil {
		t.Fatalf("Unable to encode the ClientExtension: %v", error)
	}

	var object map[string]any

	if error := json.Unmarshal(encoded, &object); error != nil {
		t.Fatalf("Unable to decode the ClientExtension: %v", error)
	}

	delete(object, "status")

	return &unstructured.Unstructured{Object: object}
}

func validClientExtension(name string) *ClientExtension {
	return &ClientExtension{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: ClientExtensionSpec{
			Configs:           []string{`{"com.liferay.client.extension.type.configuration.CETConfiguration~sample": {}}`},
			ServiceID:         "liferay-sample-cx",
			VirtualInstanceID: "liferay.com",
			WorkloadRef: &WorkloadRef{
				Kind: WorkloadKindDeployment,
				Name: "liferay-sample-cx",
			},
		},
		TypeMeta: metav1.TypeMeta{
			APIVersion: SchemeBuilder.GroupVersion.String(),
			Kind:       "ClientExtension",
		},
	}
}

var (
	environmentError error

	testClient client.Client
)
