package cx

import (
	"context"
	"errors"
	"slices"
	"strings"
	"testing"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	runtime "k8s.io/apimachinery/pkg/runtime"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
)

func TestReconcileAcceptsClientExtensionOnceConsentIsGranted(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "sample", "able")
	dxpNamespace := newDxpNamespace("baker")

	clientExtensionReconciler := newReconciler(nil, t, clientExtension, dxpNamespace)

	if phase, reason := reconcileClientExtension(
		clientExtension, clientExtensionReconciler, t,
	); reason != ReasonNamespaceNotPermitted {
		t.Fatalf("Reconcile() = %s / %s, want %s / %s", phase, reason, cxv1alpha1.PhaseDegraded, ReasonNamespaceNotPermitted)
	}

	dxpNamespace.Annotations[cxv1alpha1.AnnotationAllowedClientExtensionNamespaces] = "able,baker"

	if error := clientExtensionReconciler.Update(context.Background(), dxpNamespace); error != nil {
		t.Fatal(error)
	}

	phase, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if (phase != cxv1alpha1.PhasePending) || (reason != ReasonNamespacePermitted) {
		t.Errorf("Reconcile() = %s / %s, want %s / %s", phase, reason, cxv1alpha1.PhasePending, ReasonNamespacePermitted)
	}
}

func TestReconcileRecordsRefusalOnce(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "sample", "able")
	recorder := record.NewFakeRecorder(10)

	clientExtensionReconciler := newReconciler(nil, t, clientExtension, newDxpNamespace("baker"))

	clientExtensionReconciler.Recorder = recorder

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)
	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if len(recorder.Events) != 1 {
		t.Fatalf("Expected one event for an unchanged refusal, got %d", len(recorder.Events))
	}

	if event := <-recorder.Events; !strings.HasPrefix(event, "Warning "+ReasonNamespaceNotPermitted) {
		t.Errorf("Expected a %s warning, got %q", ReasonNamespaceNotPermitted, event)
	}
}

func TestReconcileRequiresConsentAcrossNamespaces(t *testing.T) {
	testCases := map[string]struct {
		dxpNamespace string
		messages     []string
		objects      []client.Object
		wantPhase    string
		wantReason   string
		wantStatus   metav1.ConditionStatus
	}{
		"a listed namespace is permitted": {
			dxpNamespace: "liferay-dev",
			messages:     []string{`Namespace "liferay-dev" accepts client extensions from namespace "able".`},
			objects:      []client.Object{newDxpNamespace(" able , baker ")},
			wantPhase:    cxv1alpha1.PhasePending,
			wantReason:   ReasonNamespacePermitted,
			wantStatus:   metav1.ConditionUnknown,
		},
		"a missing DXP namespace is refused": {
			dxpNamespace: "liferay-dev",
			messages:     []string{`Namespace "liferay-dev" does not exist`, "spec.dxpNamespace"},
			wantPhase:    cxv1alpha1.PhaseDegraded,
			wantReason:   ReasonDxpNamespaceNotFound,
			wantStatus:   metav1.ConditionFalse,
		},
		"an unlisted namespace is refused": {
			dxpNamespace: "liferay-dev",
			messages: []string{
				`Namespace "liferay-dev" does not list "able"`,
				`"cx.liferay.com/allowed-client-extension-namespaces"`,
			},
			objects:    []client.Object{newDxpNamespace("baker")},
			wantPhase:  cxv1alpha1.PhaseDegraded,
			wantReason: ReasonNamespaceNotPermitted,
			wantStatus: metav1.ConditionFalse,
		},
		"no dxpNamespace needs no consent": {
			wantPhase:  cxv1alpha1.PhasePending,
			wantReason: ReasonNamespacePermitted,
			wantStatus: metav1.ConditionUnknown,
		},
		"the DXP namespace itself needs no consent": {
			dxpNamespace: "able",
			wantPhase:    cxv1alpha1.PhasePending,
			wantReason:   ReasonNamespacePermitted,
			wantStatus:   metav1.ConditionUnknown,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension(testCase.dxpNamespace, "sample", "able")

			clientExtensionReconciler := newReconciler(nil, t, append(testCase.objects, clientExtension)...)

			reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

			var updatedClientExtension cxv1alpha1.ClientExtension

			if error := clientExtensionReconciler.Get(
				context.Background(), client.ObjectKeyFromObject(clientExtension), &updatedClientExtension,
			); error != nil {
				t.Fatal(error)
			}

			if updatedClientExtension.Status.Phase != testCase.wantPhase {
				t.Errorf("phase = %q, want %q", updatedClientExtension.Status.Phase, testCase.wantPhase)
			}

			for _, conditionType := range []string{cxv1alpha1.ConditionDelivered, cxv1alpha1.ConditionReady} {
				condition := meta.FindStatusCondition(updatedClientExtension.Status.Conditions, conditionType)

				if condition == nil {
					t.Fatalf("Expected a %s condition", conditionType)
				}

				if (condition.Reason != testCase.wantReason) || (condition.Status != testCase.wantStatus) {
					t.Errorf(
						"%s = %s / %s, want %s / %s",
						conditionType, condition.Status, condition.Reason, testCase.wantStatus, testCase.wantReason,
					)
				}

				for _, message := range testCase.messages {
					if !strings.Contains(condition.Message, message) {
						t.Errorf("Expected the %s message to contain %q, got %q", conditionType, message, condition.Message)
					}
				}
			}
		})
	}
}

func TestReconcileRetriesWhenTheDxpNamespaceIsUnreadable(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "sample", "able")

	clientExtensionReconciler := newReconciler(
		&interceptor.Funcs{
			Get: func(
				context context.Context, client client.WithWatch, key client.ObjectKey,
				object client.Object, options ...client.GetOption,
			) error {
				if _, ok := object.(*corev1.Namespace); ok {
					return errors.New("namespaces is forbidden")
				}

				return client.Get(context, key, object, options...)
			},
		},
		t, clientExtension,
	)

	_, reconcileError := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	)

	if reconcileError == nil {
		t.Fatal("Reconcile() error = nil, want the error so the request is retried")
	}

	var updatedClientExtension cxv1alpha1.ClientExtension

	if error := clientExtensionReconciler.Get(
		context.Background(), client.ObjectKeyFromObject(clientExtension), &updatedClientExtension,
	); error != nil {
		t.Fatal(error)
	}

	if updatedClientExtension.Status.Phase != "" {
		t.Errorf("Expected the status to be left alone, got phase %q", updatedClientExtension.Status.Phase)
	}
}

func TestRequestsForNamespaceRequeuesClientExtensions(t *testing.T) {
	clientExtensionReconciler := newReconciler(
		nil, t,
		newClientExtension("", "beside", "liferay-dev"),
		newClientExtension("liferay-dev", "elsewhere", "able"),
		newClientExtension("liferay-uat", "other-liferay", "able"),
		newClientExtension("baker", "own-namespace", "baker"),
		newClientExtension("liferay-dev", "second", "baker"),
	)

	var names []string

	for _, request := range clientExtensionReconciler.requestsForNamespace(context.Background(), newDxpNamespace("")) {
		names = append(names, request.Namespace+"/"+request.Name)
	}

	slices.Sort(names)

	if want := []string{"able/elsewhere", "baker/second"}; !slices.Equal(names, want) {
		t.Errorf("requestsForNamespace() = %v, want %v", names, want)
	}
}

func TestSummarizeDerivesReadyFromTheSteps(t *testing.T) {
	testCases := map[string]struct {
		conditions  []metav1.Condition
		wantMessage string
		wantPhase   string
		wantReason  string
		wantStatus  metav1.ConditionStatus
	}{
		"a false step is Degraded even after an unknown one": {
			conditions: []metav1.Condition{
				{Message: "Delivery is pending.", Reason: "DeliveryPending", Status: metav1.ConditionUnknown, Type: cxv1alpha1.ConditionDelivered},
				{Message: "Provisioning failed.", Reason: "ProvisioningFailed", Status: metav1.ConditionFalse, Type: cxv1alpha1.ConditionProvisioned},
			},
			wantMessage: "Provisioning failed.",
			wantPhase:   cxv1alpha1.PhaseDegraded,
			wantReason:  "ProvisioningFailed",
			wantStatus:  metav1.ConditionFalse,
		},
		"a missing step is Pending": {
			conditions: []metav1.Condition{
				{Message: "Delivered.", Reason: "Delivered", Status: metav1.ConditionTrue, Type: cxv1alpha1.ConditionDelivered},
			},
			wantMessage: "The Provisioned step has not been evaluated.",
			wantPhase:   cxv1alpha1.PhasePending,
			wantReason:  ReasonStepNotEvaluated,
			wantStatus:  metav1.ConditionUnknown,
		},
		"every true step is Ready": {
			conditions: []metav1.Condition{
				{Message: "Delivered.", Reason: "Delivered", Status: metav1.ConditionTrue, Type: cxv1alpha1.ConditionDelivered},
				{Message: "Provisioned.", Reason: "Provisioned", Status: metav1.ConditionTrue, Type: cxv1alpha1.ConditionProvisioned},
			},
			wantMessage: "Every step is complete.",
			wantPhase:   cxv1alpha1.PhaseReady,
			wantReason:  ReasonStepsComplete,
			wantStatus:  metav1.ConditionTrue,
		},
		"the first unknown step is Pending": {
			conditions: []metav1.Condition{
				{Message: "Delivery is pending.", Reason: "DeliveryPending", Status: metav1.ConditionUnknown, Type: cxv1alpha1.ConditionDelivered},
				{Message: "Provisioning is pending.", Reason: "ProvisioningPending", Status: metav1.ConditionUnknown, Type: cxv1alpha1.ConditionProvisioned},
			},
			wantMessage: "Delivery is pending.",
			wantPhase:   cxv1alpha1.PhasePending,
			wantReason:  "DeliveryPending",
			wantStatus:  metav1.ConditionUnknown,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			status := cxv1alpha1.ClientExtensionStatus{Conditions: testCase.conditions}

			summarize(
				[]string{cxv1alpha1.ConditionDelivered, cxv1alpha1.ConditionProvisioned}, 3, &status,
			)

			if status.Phase != testCase.wantPhase {
				t.Errorf("phase = %q, want %q", status.Phase, testCase.wantPhase)
			}

			ready := meta.FindStatusCondition(status.Conditions, cxv1alpha1.ConditionReady)

			if ready == nil {
				t.Fatal("Expected a Ready condition")
			}

			if (ready.Message != testCase.wantMessage) || (ready.Reason != testCase.wantReason) || (ready.Status != testCase.wantStatus) {
				t.Errorf(
					"Ready = %s / %s / %q, want %s / %s / %q",
					ready.Status, ready.Reason, ready.Message,
					testCase.wantStatus, testCase.wantReason, testCase.wantMessage,
				)
			}

			if ready.ObservedGeneration != 3 {
				t.Errorf("Ready observedGeneration = %d, want 3", ready.ObservedGeneration)
			}
		})
	}
}

func newClientExtension(dxpNamespace string, name string, namespace string) *cxv1alpha1.ClientExtension {
	return &cxv1alpha1.ClientExtension{
		ObjectMeta: metav1.ObjectMeta{Name: name, Namespace: namespace},
		Spec: cxv1alpha1.ClientExtensionSpec{
			DxpNamespace:      dxpNamespace,
			ServiceID:         name,
			VirtualInstanceID: "liferay.com",
		},
	}
}

func newDxpNamespace(permittedNamespaces string) *corev1.Namespace {
	return &corev1.Namespace{
		ObjectMeta: metav1.ObjectMeta{
			Annotations: map[string]string{
				cxv1alpha1.AnnotationAllowedClientExtensionNamespaces: permittedNamespaces,
			},
			Name: "liferay-dev",
		},
	}
}

func newReconciler(
	funcs *interceptor.Funcs,
	t *testing.T,
	objects ...client.Object,
) *ClientExtensionReconciler {
	t.Helper()

	scheme := runtime.NewScheme()

	if error := corev1.AddToScheme(scheme); error != nil {
		t.Fatal(error)
	}

	if error := cxv1alpha1.AddToScheme(scheme); error != nil {
		t.Fatal(error)
	}

	clientBuilder := fake.NewClientBuilder().WithObjects(
		objects...,
	).WithScheme(
		scheme,
	).WithStatusSubresource(
		&cxv1alpha1.ClientExtension{},
	)

	if funcs != nil {
		clientBuilder.WithInterceptorFuncs(*funcs)
	}

	return &ClientExtensionReconciler{Client: clientBuilder.Build()}
}

func reconcileClientExtension(
	clientExtension *cxv1alpha1.ClientExtension,
	clientExtensionReconciler *ClientExtensionReconciler,
	t *testing.T,
) (string, string) {
	t.Helper()

	if _, error := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	); error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	var updatedClientExtension cxv1alpha1.ClientExtension

	if error := clientExtensionReconciler.Get(
		context.Background(), client.ObjectKeyFromObject(clientExtension), &updatedClientExtension,
	); error != nil {
		t.Fatal(error)
	}

	delivered := meta.FindStatusCondition(updatedClientExtension.Status.Conditions, cxv1alpha1.ConditionDelivered)

	if delivered == nil {
		t.Fatal("Expected a Delivered condition")
	}

	return updatedClientExtension.Status.Phase, delivered.Reason
}
