package licensing

import (
	"bytes"
	"context"
	"crypto/rsa"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
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
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
)

func (stubProvisioning *stubProvisioning) Activate(
	activationRequest provisioning.ActivationRequest,
	context context.Context,
	privateKey *rsa.PrivateKey,
) error {
	stubProvisioning.activateCalled = true
	stubProvisioning.activationRequest = activationRequest

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

func TestReconcileRemovesLegacyGracePeriodCondition(t *testing.T) {
	objects := developmentObjects()

	environment := objects[len(objects)-1].(*licensingv1alpha1.LiferayEnvironment)

	meta.SetStatusCondition(
		&environment.Status.Conditions,
		metav1.Condition{
			Message: "Provisioning has been unreachable since 2026-01-01T00:00:00Z.",
			Reason:  "ProvisioningUnreachable",
			Status:  metav1.ConditionTrue,
			Type:    conditionLegacyGracePeriodExpired,
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
		objects...,
	)

	if condition := meta.FindStatusCondition(
		getEnvironment(liferayEnvironmentReconciler, t).Status.Conditions,
		conditionLegacyGracePeriodExpired,
	); condition != nil {
		t.Errorf("GracePeriodExpired condition = %v, want it removed", condition)
	}
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

func assertOwnedSecret(
	liferayEnvironmentReconciler *LiferayEnvironmentReconciler,
	name string,
	t *testing.T,
) {
	t.Helper()

	secret := getSecret(name, liferayEnvironmentReconciler, t)

	owner := metav1.GetControllerOf(secret)

	if owner == nil || owner.Kind != "LiferayEnvironment" || owner.Name != "dev" {
		t.Errorf("GetControllerOf(%q) = %v, want LiferayEnvironment dev", name, owner)
	}

	if label := secret.Labels["controller-watched"]; label != "yes" {
		t.Errorf("Labels[%q] = %q, want %q", "controller-watched", label, "yes")
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

func newDevelopmentWorkload(
	replicas *int32,
	workloadKind licensingv1alpha1.WorkloadKind,
) client.Object {
	workloadRef := licensingv1alpha1.WorkloadRef{Kind: workloadKind}

	workload := workloadRef.NewObject()

	workload.SetName("dev-liferay")
	workload.SetNamespace("liferay-dev")

	setReplicas(replicas, workload)

	return workload
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

func newFakeClientEnforcingCeiling(
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

				replicaCeiling := liferayEnvironment.Status.ReplicaCeiling

				if replicaCeiling == nil {
					replicaCeiling = liferayEnvironment.Status.License.MaxClusterNodes
				}

				if replicaCeiling != nil && *statefulSet.Spec.Replicas > *replicaCeiling {
					return errors.NewForbidden(
						schema.GroupResource{Group: "apps", Resource: "statefulsets"},
						statefulSet.Name,
						fmt.Errorf(
							"replicas %d exceeds the replica ceiling %d",
							*statefulSet.Spec.Replicas, *replicaCeiling,
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
		Client:                  newFakeClient(t, objects...),
		ExpirationGracePeriod:   90 * 24 * time.Hour,
		HeartbeatInterval:       10 * time.Minute,
		MarketplaceMountPath:    t.TempDir(),
		Provisioning:            provisioningClient,
		ProvisioningGracePeriod: 7 * 24 * time.Hour,
		Recorder:                record.NewFakeRecorder(10),
		RetryInitialDelay:       30 * time.Second,
		RetryMaxDelay:           30 * time.Minute,
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
		Client:                newFakeClient(t, objects...),
		ExpirationGracePeriod: 90 * 24 * time.Hour,
		HeartbeatInterval:     10 * time.Minute,
		MarketplaceMountPath:  marketplaceMountPath,
		Provisioning:          &stubProvisioning{},
		Recorder:              record.NewFakeRecorder(10),
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
	activateCalled    bool
	activateError     error
	activationRequest provisioning.ActivationRequest
	downloadBody      []byte
	downloadCalled    bool
	downloadError     error
	entitlements      *provisioning.Entitlements
	manifestCalled    bool
	manifestError     error
}

var workloadKinds = []licensingv1alpha1.WorkloadKind{
	licensingv1alpha1.WorkloadKindDeployment,
	licensingv1alpha1.WorkloadKindStatefulSet,
}
