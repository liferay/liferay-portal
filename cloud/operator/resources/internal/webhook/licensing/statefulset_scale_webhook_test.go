package licensing

import (
	"context"
	"encoding/json"
	"strings"
	"testing"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	admissionv1 "k8s.io/api/admission/v1"
	appsv1 "k8s.io/api/apps/v1"
	autoscalingv1 "k8s.io/api/autoscaling/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	runtime "k8s.io/apimachinery/pkg/runtime"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
	admission "sigs.k8s.io/controller-runtime/pkg/webhook/admission"
)

func TestHandle(t *testing.T) {
	testCases := map[string]struct {
		expectedAllowed         bool
		expectedMessageContains string
		licensed                bool
		maxClusterNodes         int32
		otherEnvironments       []client.Object
		request                 admission.Request
	}{
		"create over limit is denied": {
			expectedAllowed:         false,
			expectedMessageContains: "exceeds licensed maxClusterNodes 2",
			licensed:                true,
			maxClusterNodes:         2,
			request:                 createRequest("liferay-default", "liferay-test", pointerInt32(3), "liferay-default", t),
		},
		"create over limit with an empty request name is denied": {
			expectedAllowed:         false,
			expectedMessageContains: "exceeds licensed maxClusterNodes 2",
			licensed:                true,
			maxClusterNodes:         2,
			request:                 createRequest("liferay-default", "liferay-test", pointerInt32(3), "", t),
		},
		"create with malformed object is rejected": {
			expectedAllowed:         false,
			expectedMessageContains: "unexpected end of JSON input",
			licensed:                true,
			maxClusterNodes:         2,
			request:                 malformedRequest("liferay-default", "liferay-test", ""),
		},
		"create with unset replicas defaults to 1 and is allowed": {
			expectedAllowed: true,
			licensed:        true,
			maxClusterNodes: 2,
			request:         createRequest("liferay-default", "liferay-test", nil, "liferay-default", t),
		},
		"create within limit is allowed": {
			expectedAllowed: true,
			licensed:        true,
			maxClusterNodes: 2,
			request:         createRequest("liferay-default", "liferay-test", pointerInt32(1), "liferay-default", t),
		},
		"scale in an unlicensed namespace is allowed": {
			expectedAllowed:         true,
			expectedMessageContains: "not a licensed Liferay workload",
			licensed:                true,
			maxClusterNodes:         1,
			request:                 scaleRequest("liferay-default", "other-namespace", 9, t),
		},
		"scale of non-licensed workload is allowed": {
			expectedAllowed:         true,
			expectedMessageContains: "not a licensed Liferay workload",
			licensed:                false,
			maxClusterNodes:         0,
			request:                 scaleRequest("some-other-statefulset", "liferay-test", 9, t),
		},
		"scale over limit alongside other environments is denied": {
			expectedAllowed:         false,
			expectedMessageContains: "exceeds licensed maxClusterNodes 2",
			licensed:                true,
			maxClusterNodes:         2,
			otherEnvironments: []client.Object{
				licensedEnvironment(9, "other-env", "other-workload"),
			},
			request: scaleRequest("liferay-default", "liferay-test", 3, t),
		},
		"scale over limit is denied": {
			expectedAllowed:         false,
			expectedMessageContains: "exceeds licensed maxClusterNodes 1",
			licensed:                true,
			maxClusterNodes:         1,
			request:                 scaleRequest("liferay-default", "liferay-test", 2, t),
		},
		"scale to exactly the limit is allowed": {
			expectedAllowed: true,
			licensed:        true,
			maxClusterNodes: 2,
			request:         scaleRequest("liferay-default", "liferay-test", 2, t),
		},
		"scale to zero is allowed": {
			expectedAllowed: true,
			licensed:        true,
			maxClusterNodes: 2,
			request:         scaleRequest("liferay-default", "liferay-test", 0, t),
		},
		"scale with malformed object is rejected": {
			expectedAllowed:         false,
			expectedMessageContains: "unexpected end of JSON input",
			licensed:                true,
			maxClusterNodes:         2,
			request:                 malformedRequest("liferay-default", "liferay-test", "scale"),
		},
		"scale with unknown limit is denied": {
			expectedAllowed:         false,
			expectedMessageContains: "not yet available",
			licensed:                true,
			maxClusterNodes:         0,
			request:                 scaleRequest("liferay-default", "liferay-test", 1, t),
		},
		"scale within limit is allowed": {
			expectedAllowed: true,
			licensed:        true,
			maxClusterNodes: 3,
			request:         scaleRequest("liferay-default", "liferay-test", 2, t),
		},
	}

	for name, testCase := range testCases {
		t.Run(
			name,
			func(t *testing.T) {
				var objects []client.Object

				if testCase.licensed {
					objects = append(
						objects,
						licensedEnvironment(
							testCase.maxClusterNodes, "test-env", "liferay-default",
						),
					)
				}

				objects = append(objects, testCase.otherEnvironments...)

				validator := newValidator(objects...)

				response := validator.Handle(context.Background(), testCase.request)

				if response.Allowed != testCase.expectedAllowed {
					t.Fatalf(
						"Expected allowed=%v, got %v (message: %q)",
						testCase.expectedAllowed, response.Allowed,
						responseMessage(response),
					)
				}

				if testCase.expectedMessageContains == "" {
					return
				}

				message := responseMessage(response)

				if !strings.Contains(message, testCase.expectedMessageContains) {
					t.Errorf(
						"Expected message to contain %q, got %q",
						testCase.expectedMessageContains, message,
					)
				}
			},
		)
	}
}

func createRequest(
	name string,
	namespace string,
	replicas *int32,
	requestName string,
	t *testing.T,
) admission.Request {
	t.Helper()

	bytes, error := json.Marshal(
		&appsv1.StatefulSet{
			ObjectMeta: metav1.ObjectMeta{
				Name:      name,
				Namespace: namespace,
			},
			Spec: appsv1.StatefulSetSpec{
				Replicas: replicas,
			},
			TypeMeta: metav1.TypeMeta{
				APIVersion: "apps/v1",
				Kind:       "StatefulSet",
			},
		},
	)

	if error != nil {
		t.Fatalf("Unable to marshal StatefulSet: %v", error)
	}

	return admission.Request{
		AdmissionRequest: admissionv1.AdmissionRequest{
			Name:      requestName,
			Namespace: namespace,
			Object: runtime.RawExtension{
				Raw: bytes,
			},
		},
	}
}

func licensedEnvironment(
	maxClusterNodes int32,
	name string,
	workloadName string,
) *licensingv1alpha1.LiferayEnvironment {
	return &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: "liferay-test",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: workloadName,
			},
		},
		Status: licensingv1alpha1.LiferayEnvironmentStatus{
			License: licensingv1alpha1.LicenseStatus{
				MaxClusterNodes: maxClusterNodes,
			},
		},
	}
}

func malformedRequest(
	name string,
	namespace string,
	subResource string,
) admission.Request {
	return admission.Request{
		AdmissionRequest: admissionv1.AdmissionRequest{
			Name:      name,
			Namespace: namespace,
			Object: runtime.RawExtension{
				Raw: []byte("{"),
			},
			SubResource: subResource,
		},
	}
}

func newValidator(objects ...client.Object) *StatefulSetScaleValidator {
	scheme := runtime.NewScheme()

	utilruntime.Must(clientgoscheme.AddToScheme(scheme))
	utilruntime.Must(licensingv1alpha1.AddToScheme(scheme))

	return &StatefulSetScaleValidator{
		Client: fake.NewClientBuilder().WithObjects(
			objects...,
		).WithScheme(
			scheme,
		).Build(),
		Decoder: admission.NewDecoder(scheme),
	}
}

func pointerInt32(value int32) *int32 {
	return &value
}

func responseMessage(response admission.Response) string {
	if response.Result == nil {
		return ""
	}

	return response.Result.Message
}

func scaleRequest(
	name string,
	namespace string,
	replicas int32,
	t *testing.T,
) admission.Request {
	t.Helper()

	raw, error := json.Marshal(
		&autoscalingv1.Scale{
			Spec: autoscalingv1.ScaleSpec{
				Replicas: replicas,
			},
			TypeMeta: metav1.TypeMeta{
				APIVersion: "autoscaling/v1",
				Kind:       "Scale",
			},
		},
	)

	if error != nil {
		t.Fatalf("Unable to marshal Scale: %v", error)
	}

	return admission.Request{
		AdmissionRequest: admissionv1.AdmissionRequest{
			Name:      name,
			Namespace: namespace,
			Object: runtime.RawExtension{
				Raw: raw,
			},
			SubResource: "scale",
		},
	}
}