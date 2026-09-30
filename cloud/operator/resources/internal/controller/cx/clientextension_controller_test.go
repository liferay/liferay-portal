package cx

import (
	"context"
	"encoding/json"
	"errors"
	"maps"
	"reflect"
	"strings"
	"testing"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	apiextensionsv1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	runtime "k8s.io/apimachinery/pkg/runtime"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
)

func TestReconcileAcceptsClientExtensionOnceAllowed(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "sample", "able")
	dxpNamespace := newDxpNamespace("baker")

	clientExtensionReconciler := newReconciler(
		nil, t, clientExtension, dxpNamespace, newDxpMetadata("liferay-dev", "liferay.com"),
	)

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

	if (phase != cxv1alpha1.PhaseReady) || (reason != ReasonDelivered) {
		t.Errorf("Reconcile() = %s / %s, want %s / %s", phase, reason, cxv1alpha1.PhaseReady, ReasonDelivered)
	}
}

func TestReconcileAllowedNamespaces(t *testing.T) {
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
			messages:     []string{`in namespace "liferay-dev"`},
			objects: []client.Object{
				newDxpMetadata("liferay-dev", "liferay.com"), newDxpNamespace(" able , baker "),
			},
			wantPhase:  cxv1alpha1.PhaseReady,
			wantReason: ReasonDelivered,
			wantStatus: metav1.ConditionTrue,
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
		"no dxpNamespace is allowed": {
			messages:   []string{`in namespace "able"`},
			objects:    []client.Object{newDxpMetadata("able", "liferay.com")},
			wantPhase:  cxv1alpha1.PhaseReady,
			wantReason: ReasonDelivered,
			wantStatus: metav1.ConditionTrue,
		},
		"the DXP namespace itself is allowed": {
			dxpNamespace: "able",
			messages:     []string{`in namespace "able"`},
			objects:      []client.Object{newDxpMetadata("able", "liferay.com")},
			wantPhase:    cxv1alpha1.PhaseReady,
			wantReason:   ReasonDelivered,
			wantStatus:   metav1.ConditionTrue,
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

			delivered := meta.FindStatusCondition(
				updatedClientExtension.Status.Conditions, cxv1alpha1.ConditionDelivered,
			)

			if delivered == nil {
				t.Fatal("Expected a Delivered condition")
			}

			if (delivered.Reason != testCase.wantReason) || (delivered.Status != testCase.wantStatus) {
				t.Errorf(
					"Delivered = %s / %s, want %s / %s",
					delivered.Status, delivered.Reason, testCase.wantStatus, testCase.wantReason,
				)
			}

			for _, message := range testCase.messages {
				if !strings.Contains(delivered.Message, message) {
					t.Errorf("Expected the Delivered message to contain %q, got %q", message, delivered.Message)
				}
			}
		})
	}
}

func TestReconcileChecksAllowedBeforeVirtualInstance(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	clientExtensionReconciler := newReconciler(
		nil, t, clientExtension, newDxpMetadata("liferay-dev", "other.test"), newDxpNamespace("baker"),
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	delivered := getDelivered(clientExtension, clientExtensionReconciler, t)

	if delivered.Reason != ReasonNamespaceNotPermitted {
		t.Errorf("Delivered reason = %q, want %q", delivered.Reason, ReasonNamespaceNotPermitted)
	}

	if strings.Contains(delivered.Message, "other.test") {
		t.Errorf("Expected the refusal not to list virtual instances, got %q", delivered.Message)
	}
}

func TestReconcileDeletesStaleExtProvisionConfigMaps(t *testing.T) {
	testCases := map[string]struct {
		change        func(clientExtension *cxv1alpha1.ClientExtension)
		wantName      string
		wantNamespace string
	}{
		"a changed dxpNamespace": {
			change: func(clientExtension *cxv1alpha1.ClientExtension) {
				clientExtension.Spec.DxpNamespace = "liferay-uat"
			},
			wantName:      "able-liferay.com-lxc-ext-provision-metadata",
			wantNamespace: "liferay-uat",
		},
		"a changed serviceId": {
			change: func(clientExtension *cxv1alpha1.ClientExtension) {
				clientExtension.Spec.ServiceID = "baker"
			},
			wantName:      "baker-liferay.com-lxc-ext-provision-metadata",
			wantNamespace: "liferay-dev",
		},
		"a changed virtualInstanceId": {
			change: func(clientExtension *cxv1alpha1.ClientExtension) {
				clientExtension.Spec.VirtualInstanceID = "other.test"
			},
			wantName:      "able-other.test-lxc-ext-provision-metadata",
			wantNamespace: "liferay-dev",
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension("liferay-dev", "able", "able")

			uatNamespace := newDxpNamespace("able")

			uatNamespace.Name = "liferay-uat"

			clientExtensionReconciler := newReconciler(
				nil, t, clientExtension,
				newDxpMetadata("liferay-dev", "liferay.com"), newDxpMetadata("liferay-dev", "other.test"),
				newDxpMetadata("liferay-uat", "liferay.com"), newDxpNamespace("able"), uatNamespace,
			)

			reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

			updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

			testCase.change(updatedClientExtension)

			if error := clientExtensionReconciler.Update(context.Background(), updatedClientExtension); error != nil {
				t.Fatal(error)
			}

			if _, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); reason != ReasonDelivered {
				t.Fatalf("Delivered reason = %q, want %q", reason, ReasonDelivered)
			}

			if getExtProvision(clientExtensionReconciler, "liferay-dev", t) != nil {
				t.Error("Expected the stale ConfigMap to be deleted")
			}

			if getConfigMap(clientExtensionReconciler, testCase.wantName, testCase.wantNamespace, t) == nil {
				t.Errorf("Expected ConfigMap %s/%s to be delivered", testCase.wantNamespace, testCase.wantName)
			}
		})
	}
}

func TestReconcileDeliversExtProvisionConfigMap(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	clientExtension.Spec.Domain = "able.example.com"

	clientExtensionReconciler := newReconciler(
		nil, t, clientExtension, newDxpMetadata("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	if phase, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); reason != ReasonDelivered {
		t.Fatalf("Reconcile() = %s / %s, want %s / %s", phase, reason, cxv1alpha1.PhaseReady, ReasonDelivered)
	}

	configMap := getExtProvision(clientExtensionReconciler, "liferay-dev", t)

	if configMap == nil {
		t.Fatal("Expected the ext-provision ConfigMap to be written")
	}

	wantLabels := map[string]string{
		LabelMetadataType:    MetadataTypeExtProvision,
		LabelOwner:           ownerLabelValue(clientExtension),
		LabelServiceID:       "able",
		LabelVirtualInstance: "liferay.com",
	}

	if !maps.Equal(configMap.Labels, wantLabels) {
		t.Errorf("labels = %v, want %v", configMap.Labels, wantLabels)
	}

	wantAnnotations := map[string]string{
		AnnotationMainDomain:     "able.example.com",
		AnnotationOwnerName:      "able",
		AnnotationOwnerNamespace: "able",
	}

	if !maps.Equal(configMap.Annotations, wantAnnotations) {
		t.Errorf("annotations = %v, want %v", configMap.Annotations, wantAnnotations)
	}

	var payload map[string]map[string]any

	if error := json.Unmarshal([]byte(configMap.Data["able.client-extension-config.json"]), &payload); error != nil {
		t.Fatalf("Expected a JSON payload under able.client-extension-config.json, got %v: %v", configMap.Data, error)
	}

	wantPayload := map[string]map[string]any{
		"com.liferay.client.extension.type.configuration.CETConfiguration~able": {"name": "Sample"},
	}

	if !reflect.DeepEqual(payload, wantPayload) {
		t.Errorf("payload = %v, want %v", payload, wantPayload)
	}
}

func TestReconcileDeliversOnceVirtualInstanceAppears(t *testing.T) {
	clientExtension := newClientExtension("", "able", "able")

	clientExtensionReconciler := newReconciler(nil, t, clientExtension)

	if _, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); reason != ReasonUnknownVirtualInstance {
		t.Fatalf("Delivered reason = %q, want %q", reason, ReasonUnknownVirtualInstance)
	}

	if error := clientExtensionReconciler.Create(
		context.Background(), newDxpMetadata("able", "liferay.com"),
	); error != nil {
		t.Fatal(error)
	}

	if phase, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); reason != ReasonDelivered {
		t.Errorf("Reconcile() = %s / %s, want %s / %s", phase, reason, cxv1alpha1.PhaseReady, ReasonDelivered)
	}

	if getExtProvision(clientExtensionReconciler, "able", t) == nil {
		t.Error("Expected the ext-provision ConfigMap once the virtual instance appears")
	}
}

func TestReconcileKeepsConfigMapWhenVirtualInstanceDisappears(t *testing.T) {
	clientExtension := newClientExtension("", "able", "able")

	dxpMetadata := newDxpMetadata("able", "liferay.com")

	clientExtensionReconciler := newReconciler(nil, t, clientExtension, dxpMetadata)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	beforeExtProvisionConfigMap := getExtProvision(clientExtensionReconciler, "able", t)

	if error := clientExtensionReconciler.Delete(context.Background(), dxpMetadata); error != nil {
		t.Fatal(error)
	}

	if _, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); reason != ReasonUnknownVirtualInstance {
		t.Errorf("Delivered reason = %q, want %q", reason, ReasonUnknownVirtualInstance)
	}

	afterExtProvisionConfigMap := getExtProvision(clientExtensionReconciler, "able", t)

	if (afterExtProvisionConfigMap == nil) || (afterExtProvisionConfigMap.UID != beforeExtProvisionConfigMap.UID) || (afterExtProvisionConfigMap.ResourceVersion != beforeExtProvisionConfigMap.ResourceVersion) {
		t.Errorf("Expected the ext-provision ConfigMap to be untouched, got %v", afterExtProvisionConfigMap)
	}
}

func TestReconcileLeavesUnchangedExtProvisionConfigMapAlone(t *testing.T) {
	clientExtension := newClientExtension("", "able", "able")

	clientExtensionReconciler := newReconciler(nil, t, clientExtension, newDxpMetadata("able", "liferay.com"))

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	beforeExtProvisionConfigMap := getExtProvision(clientExtensionReconciler, "able", t)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if afterExtProvisionConfigMap := getExtProvision(clientExtensionReconciler, "able", t); afterExtProvisionConfigMap.ResourceVersion != beforeExtProvisionConfigMap.ResourceVersion {
		t.Errorf("resourceVersion = %s, want %s so the agent does not reapply it", afterExtProvisionConfigMap.ResourceVersion, beforeExtProvisionConfigMap.ResourceVersion)
	}
}

func TestReconcileRefusesExtProvisionConfigMapItDoesNotOwn(t *testing.T) {
	testCases := map[string]struct {
		annotations map[string]string
		labels      map[string]string
		wantMessage string
	}{
		"a ConfigMap another ClientExtension owns": {
			annotations: map[string]string{
				AnnotationOwnerName:      "able",
				AnnotationOwnerNamespace: "baker",
			},
			wantMessage: `already belongs to ClientExtension "able" in namespace "baker"`,
		},
		"a ConfigMap no ClientExtension owns": {
			wantMessage: "is not managed by any ClientExtension",
		},
		"a ConfigMap whose name collides with another serviceId": {
			annotations: map[string]string{
				AnnotationOwnerName:      "able-liferay",
				AnnotationOwnerNamespace: "baker",
			},
			labels: map[string]string{
				LabelServiceID:       "able-liferay",
				LabelVirtualInstance: "com",
			},
			wantMessage: `holds serviceId "able-liferay" on virtual instance "com", whose ConfigMap names collide with serviceId "able" on virtual instance "liferay.com"`,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension("liferay-dev", "able", "able")

			labels := map[string]string{LabelMetadataType: MetadataTypeExtProvision}

			maps.Copy(labels, testCase.labels)

			owned := newExtProvision(testCase.annotations, labels)

			clientExtensionReconciler := newReconciler(
				nil, t, clientExtension, owned,
				newDxpMetadata("liferay-dev", "liferay.com"), newDxpNamespace("able,baker"),
			)

			phase, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

			if (phase != cxv1alpha1.PhaseDegraded) || (reason != ReasonServiceIDConflict) {
				t.Fatalf("Reconcile() = %s / %s, want %s / %s", phase, reason, cxv1alpha1.PhaseDegraded, ReasonServiceIDConflict)
			}

			updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

			delivered := meta.FindStatusCondition(updatedClientExtension.Status.Conditions, cxv1alpha1.ConditionDelivered)

			for _, message := range []string{testCase.wantMessage, `serviceId "able"`} {
				if !strings.Contains(delivered.Message, message) {
					t.Errorf("Expected the message to contain %q, got %q", message, delivered.Message)
				}
			}

			extProvisionConfigMap := getExtProvision(clientExtensionReconciler, "liferay-dev", t)

			if (extProvisionConfigMap.Data["owner"] != "untouched") ||
				!maps.Equal(extProvisionConfigMap.Annotations, owned.Annotations) ||
				!maps.Equal(extProvisionConfigMap.Labels, owned.Labels) {

				t.Errorf(
					"Expected the other owner's ConfigMap to be untouched, got %v / %v / %v",
					extProvisionConfigMap.Annotations, extProvisionConfigMap.Labels, extProvisionConfigMap.Data,
				)
			}
		})
	}
}

func TestReconcileRefusesExtProvisionConfigMapTheCacheCannotSee(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	unlabeledExtProvisionConfigMap := newExtProvision(nil, nil)

	clientExtensionReconciler := newReconciler(
		hideFromCache(unlabeledExtProvisionConfigMap.Name), t, clientExtension, unlabeledExtProvisionConfigMap,
		newDxpMetadata("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	result, error := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	)

	if error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if result.RequeueAfter <= 0 {
		t.Errorf("Reconcile() RequeueAfter = %v, want a retry", result.RequeueAfter)
	}

	if reason := getDelivered(clientExtension, clientExtensionReconciler, t).Reason; reason != ReasonServiceIDConflict {
		t.Fatalf("Delivered reason = %q, want %q", reason, ReasonServiceIDConflict)
	}

	if message := getDelivered(clientExtension, clientExtensionReconciler, t).Message; !strings.Contains(message, "is not managed by any ClientExtension") {
		t.Errorf("Expected the message to say no ClientExtension manages it, got %q", message)
	}

	if configMap := getConfigMap(clientExtensionReconciler, unlabeledExtProvisionConfigMap.Name, "liferay-dev", t); configMap.Data["owner"] != "untouched" {
		t.Errorf("Expected the hand-made ConfigMap to be untouched, got %v", configMap.Data)
	}
}

func TestReconcileRefusesUnknownVirtualInstance(t *testing.T) {
	clientExtension := newClientExtension("", "able", "able")

	clientExtensionReconciler := newReconciler(nil, t, clientExtension, newDxpMetadata("able", "other.test"))

	result, error := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	)

	if error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if result != (controllerruntime.Result{}) {
		t.Errorf("Reconcile() = %+v, want no requeue: the dxp metadata watch delivers once the virtual instance appears", result)
	}

	updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

	if updatedClientExtension.Status.Phase != cxv1alpha1.PhaseDegraded {
		t.Errorf("phase = %q, want %q", updatedClientExtension.Status.Phase, cxv1alpha1.PhaseDegraded)
	}

	delivered := getDelivered(clientExtension, clientExtensionReconciler, t)

	if (delivered.Status != metav1.ConditionFalse) || (delivered.Reason != ReasonUnknownVirtualInstance) {
		t.Fatalf("Delivered = %s / %s, want False / %s", delivered.Status, delivered.Reason, ReasonUnknownVirtualInstance)
	}

	if want := `Virtual instance "liferay.com" is unknown to DXP: ConfigMap "liferay.com-lxc-dxp-metadata" does not exist in namespace "able".`; delivered.Message != want {
		t.Errorf("Delivered message = %q, want %q", delivered.Message, want)
	}

	if getExtProvision(clientExtensionReconciler, "able", t) != nil {
		t.Error("Expected nothing to be written for an unknown virtual instance")
	}
}

func TestReconcileReportsDeliveryDxpNamespaceDoesNotPermit(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	clientExtensionReconciler := newReconciler(
		&interceptor.Funcs{
			Create: func(
				context context.Context, client client.WithWatch, object client.Object,
				options ...client.CreateOption,
			) error {
				if _, ok := object.(*corev1.ConfigMap); ok {
					return apierrors.NewForbidden(corev1.Resource("configmaps"), object.GetName(), errors.New("no RoleBinding"))
				}

				return client.Create(context, object, options...)
			},
		},
		t, clientExtension, newDxpMetadata("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	clientExtensionReconciler.ServiceAccount = "liferay-system/dxp-operator"

	result, error := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	)

	if error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if result.RequeueAfter <= 0 {
		t.Errorf("Reconcile() RequeueAfter = %v, want a retry", result.RequeueAfter)
	}

	delivered := getDelivered(clientExtension, clientExtensionReconciler, t)

	if (delivered.Status != metav1.ConditionFalse) || (delivered.Reason != ReasonDeliveryNotPermitted) {
		t.Fatalf("Delivered = %s / %s, want False / %s", delivered.Status, delivered.Reason, ReasonDeliveryNotPermitted)
	}

	for _, message := range []string{
		`namespace "liferay-dev"`,
		`ClusterRole "client-extension-delivery-cluster-role"`,
		`ServiceAccount "liferay-system/dxp-operator"`,
	} {
		if !strings.Contains(delivered.Message, message) {
			t.Errorf("Expected the message to contain %q, got %q", message, delivered.Message)
		}
	}

	if getExtProvision(clientExtensionReconciler, "liferay-dev", t) != nil {
		t.Error("Expected nothing to be written where delivery is not permitted")
	}
}

func TestReconcileReportsStaleExtProvisionConfigMapItMayNotDelete(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	uatNamespace := newDxpNamespace("able")

	uatNamespace.Name = "liferay-uat"

	clientExtensionReconciler := newReconciler(
		&interceptor.Funcs{
			Delete: func(
				context context.Context, client client.WithWatch, object client.Object,
				options ...client.DeleteOption,
			) error {
				if _, ok := object.(*corev1.ConfigMap); ok && (object.GetNamespace() == "liferay-dev") {
					return apierrors.NewForbidden(corev1.Resource("configmaps"), object.GetName(), errors.New("no RoleBinding"))
				}

				return client.Delete(context, object, options...)
			},
		},
		t, clientExtension,
		newDxpMetadata("liferay-dev", "liferay.com"), newDxpMetadata("liferay-uat", "liferay.com"),
		newDxpNamespace("able"), uatNamespace,
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

	updatedClientExtension.Spec.DxpNamespace = "liferay-uat"

	if error := clientExtensionReconciler.Update(context.Background(), updatedClientExtension); error != nil {
		t.Fatal(error)
	}

	if _, reason := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); reason != ReasonDelivered {
		t.Fatalf("Delivered reason = %q, want %q", reason, ReasonDelivered)
	}

	if getExtProvision(clientExtensionReconciler, "liferay-dev", t) == nil {
		t.Error("Expected the stale ConfigMap that could not be deleted to remain")
	}

	want := `Unable to delete the stale ConfigMaps "liferay-dev/able-liferay.com-lxc-ext-provision-metadata"`

	if message := getDelivered(clientExtension, clientExtensionReconciler, t).Message; !strings.Contains(message, want) {
		t.Errorf("Expected the Delivered message to contain %q, got %q", want, message)
	}
}

func TestReconcileRetriesOnCacheMiss(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	ownedExtProvisionConfigMap := newExtProvision(
		map[string]string{AnnotationOwnerName: "able", AnnotationOwnerNamespace: "able"},
		map[string]string{LabelMetadataType: MetadataTypeExtProvision},
	)

	clientExtensionReconciler := newReconciler(
		hideFromCache(ownedExtProvisionConfigMap.Name), t, clientExtension, ownedExtProvisionConfigMap,
		newDxpMetadata("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	if _, error := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	); !apierrors.IsAlreadyExists(error) {
		t.Fatalf("Reconcile() error = %v, want AlreadyExists so the request is retried", error)
	}

	if updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t); len(updatedClientExtension.Status.Conditions) != 0 {
		t.Errorf("Expected no condition for a cache that is behind, got %v", updatedClientExtension.Status.Conditions)
	}
}

func TestReconcileUpdatesItsOwnExtProvisionConfigMapInPlace(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	ownedExtProvisionConfigMap := newExtProvision(
		map[string]string{
			AnnotationMainDomain:     "old.example.com",
			AnnotationOwnerName:      "able",
			AnnotationOwnerNamespace: "able",
		},
		map[string]string{LabelMetadataType: MetadataTypeExtProvision},
	)

	ownedExtProvisionConfigMap.UID = "able-uid"

	clientExtensionReconciler := newReconciler(
		nil, t, clientExtension, ownedExtProvisionConfigMap,
		newDxpMetadata("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	extProvisionConfigMap := getExtProvision(clientExtensionReconciler, "liferay-dev", t)

	if extProvisionConfigMap.UID != ownedExtProvisionConfigMap.UID {
		t.Errorf("uid = %q, want %q: recreating the ConfigMap would issue new OAuth2 credentials", extProvisionConfigMap.UID, ownedExtProvisionConfigMap.UID)
	}

	if !strings.Contains(extProvisionConfigMap.Data["able.client-extension-config.json"], "CETConfiguration~able") {
		t.Errorf("Expected the current payload, got %v", extProvisionConfigMap.Data)
	}

	if _, ok := extProvisionConfigMap.Data["owner"]; ok {
		t.Errorf("Expected the old payload to be replaced, got %v", extProvisionConfigMap.Data)
	}

	if _, ok := extProvisionConfigMap.Annotations[AnnotationMainDomain]; ok {
		t.Errorf("Expected the domain annotations to be removed with the domain, got %v", extProvisionConfigMap.Annotations)
	}
}

func getClientExtension(
	clientExtension *cxv1alpha1.ClientExtension,
	clientExtensionReconciler *ClientExtensionReconciler,
	t *testing.T,
) *cxv1alpha1.ClientExtension {
	t.Helper()

	var updatedClientExtension cxv1alpha1.ClientExtension

	if error := clientExtensionReconciler.Get(
		context.Background(), client.ObjectKeyFromObject(clientExtension), &updatedClientExtension,
	); error != nil {
		t.Fatal(error)
	}

	return &updatedClientExtension
}

func getConfigMap(
	clientExtensionReconciler *ClientExtensionReconciler,
	name string,
	namespace string,
	t *testing.T,
) *corev1.ConfigMap {
	t.Helper()

	var configMap corev1.ConfigMap

	error := clientExtensionReconciler.APIReader.Get(
		context.Background(), types.NamespacedName{Name: name, Namespace: namespace}, &configMap,
	)

	if apierrors.IsNotFound(error) {
		return nil
	}

	if error != nil {
		t.Fatal(error)
	}

	return &configMap
}

func getDelivered(
	clientExtension *cxv1alpha1.ClientExtension,
	clientExtensionReconciler *ClientExtensionReconciler,
	t *testing.T,
) *metav1.Condition {
	t.Helper()

	delivered := meta.FindStatusCondition(
		getClientExtension(clientExtension, clientExtensionReconciler, t).Status.Conditions,
		cxv1alpha1.ConditionDelivered,
	)

	if delivered == nil {
		t.Fatal("Expected a Delivered condition")
	}

	return delivered
}

func getExtProvision(
	clientExtensionReconciler *ClientExtensionReconciler,
	namespace string,
	t *testing.T,
) *corev1.ConfigMap {
	t.Helper()

	return getConfigMap(clientExtensionReconciler, "able-liferay.com-lxc-ext-provision-metadata", namespace, t)
}

func hideFromCache(name string) *interceptor.Funcs {
	return &interceptor.Funcs{
		Get: func(
			context context.Context, client client.WithWatch, key client.ObjectKey,
			object client.Object, options ...client.GetOption,
		) error {
			if _, ok := object.(*corev1.ConfigMap); ok && (key.Name == name) {
				return apierrors.NewNotFound(corev1.Resource("configmaps"), key.Name)
			}

			return client.Get(context, key, object, options...)
		},
	}
}

func newClientExtension(dxpNamespace string, name string, namespace string) *cxv1alpha1.ClientExtension {
	return &cxv1alpha1.ClientExtension{
		ObjectMeta: metav1.ObjectMeta{Name: name, Namespace: namespace},
		Spec: cxv1alpha1.ClientExtensionSpec{
			Configs: map[string]cxv1alpha1.Configuration{
				"com.liferay.client.extension.type.configuration.CETConfiguration~" + name: {
					JSON: apiextensionsv1.JSON{Raw: []byte(`{"name": "Sample"}`)},
				},
			},
			DxpNamespace:      dxpNamespace,
			ServiceID:         name,
			VirtualInstanceID: "liferay.com",
		},
	}
}

func newDxpMetadata(namespace string, virtualInstanceID string) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Labels: map[string]string{
				LabelMetadataType:    MetadataTypeDxp,
				LabelVirtualInstance: virtualInstanceID,
			},
			Name:      dxpMetadataName(virtualInstanceID),
			Namespace: namespace,
		},
	}
}

func newDxpNamespace(allowedNamespaces string) *corev1.Namespace {
	return &corev1.Namespace{
		ObjectMeta: metav1.ObjectMeta{
			Annotations: map[string]string{
				cxv1alpha1.AnnotationAllowedClientExtensionNamespaces: allowedNamespaces,
			},
			Name: "liferay-dev",
		},
	}
}

func newExtProvision(annotations map[string]string, labels map[string]string) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		Data: map[string]string{"owner": "untouched"},
		ObjectMeta: metav1.ObjectMeta{
			Annotations: annotations,
			Labels:      labels,
			Name:        "able-liferay.com-lxc-ext-provision-metadata",
			Namespace:   "liferay-dev",
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

	apiReader := clientBuilder.Build()

	var cachedClient client.Client = apiReader

	if funcs != nil {
		cachedClient = interceptor.NewClient(apiReader, *funcs)
	}

	return &ClientExtensionReconciler{
		APIReader: apiReader,
		Client:    cachedClient,
		Recorder:  &record.FakeRecorder{},
	}
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
