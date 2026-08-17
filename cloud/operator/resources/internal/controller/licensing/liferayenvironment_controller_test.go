package licensing

import (
	"bytes"
	"context"
	"crypto/rsa"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	runtime "k8s.io/apimachinery/pkg/runtime"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func (stubProvisioning *stubProvisioning) Activate(
	activationRequest provisioning.ActivationRequest,
	context context.Context,
	privateKey *rsa.PrivateKey,
) error {
	stubProvisioning.activateCalled = true

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

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(t, objects...),
			}

			if error := liferayEnvironmentReconciler.enforceReplicaCeiling(
				context.Background(), liferayEnvironment, testCase.maxClusterNodes,
			); error != nil {
				t.Fatalf("Unexpected error from enforceReplicaCeiling: %v", error)
			}

			assertReplicasEqual(
				liferayEnvironment.Status.EffectiveReplicas,
				testCase.expectedEffective,
				"status.effectiveReplicas",
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

			if !testCase.workloadExists {
				return
			}

			statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

			assertReplicasEqual(
				statefulSet.Spec.Replicas, testCase.expectedReplicas,
				"statefulSet.spec.replicas", t,
			)
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

func TestReconcileDowngradesAfterGracePeriod(t *testing.T) {
	unreachableSince := metav1.NewTime(time.Now().Add(-8 * 24 * time.Hour))

	environment := activatedEnvironment()
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
	}

	provisioningClient := &stubProvisioning{
		manifestError: fmt.Errorf("provisioning: connection refused"),
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(provisioningClient, t, objects...)

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(1), "statefulSet.spec.replicas", t,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if !meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionGracePeriodExpired,
	) {
		t.Error("GracePeriodExpired condition = not True, want True after the grace window")
	}

	if licenseXML := getLicenseXML(liferayEnvironmentReconciler, t); licenseXML != "<license>known-good</license>" {
		t.Errorf("license.xml = %q, should retain last-known-good", licenseXML)
	}

	recorder := liferayEnvironmentReconciler.Recorder.(*record.FakeRecorder)

	select {
	case event := <-recorder.Events:
		if !strings.Contains(event, "GracePeriodExpired") {
			t.Errorf("event = %q, should mention GracePeriodExpired", event)
		}
	default:
		t.Error("Expected a GracePeriodExpired warning event")
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
		LicenseXML:      []byte(virtualClusterLicenseXML("Friday, March 2, 2029 12:00:00 AM GMT", 3)),
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

	if result.RequeueAfter != downloadPollInterval {
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
		LicenseXML:      []byte(virtualClusterLicenseXML("Friday, March 2, 2029 12:00:00 AM GMT", 3)),
		MaxClusterNodes: 3,
	}

	liferayEnvironmentReconciler, result := reconcileEnvironment(
		&stubProvisioning{entitlements: entitlements}, t, developmentObjects()...,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready despite the broken add-on", liferayEnvironment.Status.Phase)
	}

	if result.RequeueAfter != downloadPollInterval {
		t.Errorf("RequeueAfter = %s, want the download poll interval", result.RequeueAfter)
	}

	if length := len(getSecret("dev-entitlements", liferayEnvironmentReconciler, t).Data["add-ons.json"]); length == 0 {
		t.Error("add-ons.json was not written to the entitlements secret")
	}
}

func TestReconcileOfflineAwaitsOfflineActivationBundle(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true

	provisioningClient := &stubProvisioning{}

	liferayEnvironmentReconciler, result := reconcileEnvironment(
		provisioningClient, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	if provisioningClient.activateCalled || provisioningClient.downloadCalled ||
		provisioningClient.manifestCalled {
		t.Errorf(
			"Offline reconcile made provisioning calls: activate=%v download=%v manifest=%v",
			provisioningClient.activateCalled,
			provisioningClient.downloadCalled,
			provisioningClient.manifestCalled,
		)
	}

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want 15s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Pending" {
		t.Errorf("Phase = %q, want Pending", liferayEnvironment.Status.Phase)
	}

	if liferayEnvironment.Status.ActivatedAt != nil {
		t.Errorf(
			"ActivatedAt = %v, want nil in offline mode before a bundle",
			liferayEnvironment.Status.ActivatedAt,
		)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	)

	if condition == nil {
		t.Fatal("Activated condition is nil, wanted value")
	}

	if condition.Reason != "AwaitingOfflineActivationBundle" {
		t.Errorf("Activated condition reason = %v, want AwaitingOfflineActivationBundle", condition.Reason)
	}

	if condition.Status != metav1.ConditionFalse {
		t.Errorf("Activated condition status = %v, want False", condition.Status)
	}
}

func TestReconcileOfflineAwaitsMissingBundleFile(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		t.TempDir(), t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want 15s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Pending" {
		t.Errorf("Phase = %q, want Pending", liferayEnvironment.Status.Phase)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	)

	if condition == nil || condition.Reason != "AwaitingOfflineActivationBundle" {
		t.Errorf(
			"Activated condition = %v, want AwaitingOfflineActivationBundle", condition,
		)
	}
}

func TestReconcileOfflineExtractsAddOnsFromBundle(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	lpkgContent := "PK-fake-lpkg-content"

	checksum := sha256.Sum256([]byte(lpkgContent))

	licenseXML := virtualClusterLicenseXML("Friday, March 2, 2029 12:00:00 AM GMT", 3)

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app-1.lpkg": lpkgContent,
			"manifest.json": fmt.Sprintf(
				`{"add-ons":[{"productId":"app-1","productName":"App One","virtualEntryId":42,"sha256Checksum":%q}],"licenseXML":%q,"maxClusterNodes":3}`,
				hex.EncodeToString(checksum[:]),
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		filepath.Join(marketplaceMountPath, "liferay-dev", "bundle.zip"),
		t,
	)

	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		marketplaceMountPath, t,
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
	)

	if result.RequeueAfter != 10*time.Minute {
		t.Errorf(
			"RequeueAfter = %s, want the heartbeat 10m (offline extraction is synchronous)",
			result.RequeueAfter,
		)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready", liferayEnvironment.Status.Phase)
	}

	if length := len(liferayEnvironment.Status.Apps); length != 1 {
		t.Fatalf("Apps length = %d, want 1", length)
	}

	if state := liferayEnvironment.Status.Apps[0].State; state != "Downloaded" {
		t.Errorf("App state = %q, want Downloaded", state)
	}

	extracted := filepath.Join(marketplaceMountPath, "liferay-dev", "42.lpkg")

	if _, error := os.Stat(extracted); error != nil {
		t.Errorf("Expected the extracted lpkg at %s: %v", extracted, error)
	}
}

func TestReconcileOfflineLicensesFromBundle(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	licenseXML := virtualClusterLicenseXML("Friday, March 2, 2029 12:00:00 AM GMT", 3)

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app.lpkg": "PK-fake-lpkg",
			"manifest.json": fmt.Sprintf(
				`{"add-ons":[],"licenseXML":%q,"maxClusterNodes":3}`,
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		filepath.Join(
			marketplaceMountPath, "liferay-dev", "bundle.zip",
		),
		t,
	)

	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		marketplaceMountPath, t,
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
	)

	if result.RequeueAfter != 10*time.Minute {
		t.Errorf("RequeueAfter = %s, want the heartbeat 10m", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready", liferayEnvironment.Status.Phase)
	}

	if liferayEnvironment.Status.ActivatedAt == nil {
		t.Error("ActivatedAt = nil, want it set after licensing from the bundle")
	}

	if liferayEnvironment.Status.License.MaxClusterNodes != 3 {
		t.Errorf(
			"License.MaxClusterNodes = %d, want 3",
			liferayEnvironment.Status.License.MaxClusterNodes,
		)
	}

	if activated := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	); activated == nil || activated.Status != metav1.ConditionTrue {
		t.Errorf("Activated condition = %v, want True", activated)
	}

	if licenseValid := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	); licenseValid == nil || licenseValid.Status != metav1.ConditionTrue {
		t.Errorf("LicenseValid condition = %v, want True", licenseValid)
	}

	if written := getLicenseXML(liferayEnvironmentReconciler, t); written != licenseXML {
		t.Errorf("entitlements license.xml = %q, want the bundle license", written)
	}
}

func TestReconcileOfflineRejectsInvalidBundle(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app.lpkg": "PK-fake-lpkg",
		},
		filepath.Join(
			marketplaceMountPath, "liferay-dev", "bundle.zip",
		),
		t,
	)

	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, result := reconcileOfflineActivationBundle(
		marketplaceMountPath, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	if result.RequeueAfter != 15*time.Second {
		t.Errorf("RequeueAfter = %s, want 15s", result.RequeueAfter)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf("Phase = %q, want Degraded", liferayEnvironment.Status.Phase)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse ||
		condition.Reason != "OfflineActivationBundleInvalid" {

		t.Errorf("Activated condition = %v, want False/OfflineActivationBundleInvalid", condition)
	}
}

func TestReconcileOfflineRequestIsWriteOnce(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{}, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	first := string(getSecret(
		"dev-identity", liferayEnvironmentReconciler, t,
	).Data["offline-request"])

	if _, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: "liferay-dev",
			},
		},
	); error != nil {
		t.Fatalf("Unexpected error on second reconcile: %v", error)
	}

	second := string(getSecret(
		"dev-identity", liferayEnvironmentReconciler, t,
	).Data["offline-request"])

	if first != second {
		t.Error("offline-request payload changed on re-reconcile; want write-once")
	}
}

func TestReconcileOfflineStoresRequestInIdentitySecret(t *testing.T) {
	environment := pendingEnvironment()
	environment.Spec.Offline = true

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{}, t,
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		environment,
	)

	payload := getSecret(
		"dev-identity", liferayEnvironmentReconciler, t,
	).Data["offline-request"]

	if len(payload) == 0 {
		t.Fatal("identity secret has no offline-request payload")
	}

	segments := strings.Split(string(payload), ".")

	if len(segments) != 3 {
		t.Fatalf("offline-request is not a JWT: got %d segments, want 3", len(segments))
	}

	claims, error := base64.RawURLEncoding.DecodeString(segments[1])

	if error != nil {
		t.Fatalf("Unable to decode the JWT payload segment: %v", error)
	}

	var claimsMap map[string]any

	if error := json.Unmarshal(claims, &claimsMap); error != nil {
		t.Errorf("JWT payload segment is not valid JSON: %v", error)
	}
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
			Type:    conditionGracePeriodExpired,
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
			LicenseXML:      []byte(virtualClusterLicenseXML("Friday, March 2, 2029 12:00:00 AM GMT", 3)),
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
		liferayEnvironment.Status.Conditions, conditionGracePeriodExpired,
	) != nil {
		t.Error("GracePeriodExpired condition still present, want it cleared after recovery")
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

func TestReconcileWritesExpiredLicenseThrough(t *testing.T) {
	expiredLicenseXML := virtualClusterLicenseXML(
		"Wednesday, January 1, 2020 12:00:00 AM GMT", 1,
	)

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML:      []byte(expiredLicenseXML),
			MaxClusterNodes: 1,
		},
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		provisioningClient, t, developmentObjects()...,
	)

	if licenseXML := getLicenseXML(liferayEnvironmentReconciler, t); licenseXML != expiredLicenseXML {
		t.Errorf("license.xml = %q, want the expired license written through", licenseXML)
	}

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

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

func getLicenseXML(liferayEnvironmentReconciler *LiferayEnvironmentReconciler, t *testing.T) string {
	return string(getSecret("dev-entitlements", liferayEnvironmentReconciler, t).Data["license.xml"])
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

func reconcileEnvironment(
	provisioningClient provisioning.Client,
	t *testing.T,
	objects ...client.Object,
) (*LiferayEnvironmentReconciler, controllerruntime.Result) {
	t.Helper()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client:               newFakeClient(t, objects...),
		GracePeriod:          7 * 24 * time.Hour,
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: t.TempDir(),
		Provisioning:         provisioningClient,
		Recorder:             record.NewFakeRecorder(10),
		RetryInitialDelay:    30 * time.Second,
		RetryMaxDelay:        30 * time.Minute,
		Syncer:               addon.NewSyncer(provisioningClient, inlineRunner{}),
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
		Client:               newFakeClient(t, objects...),
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: marketplaceMountPath,
		Provisioning:         &stubProvisioning{},
		Recorder:             record.NewFakeRecorder(10),
	}

	return liferayEnvironmentReconciler, reconcile(liferayEnvironmentReconciler, t)
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

type inlineRunner struct{}

type stubProvisioning struct {
	activateCalled bool
	activateError  error
	downloadBody   []byte
	downloadCalled bool
	downloadError  error
	entitlements   *provisioning.Entitlements
	manifestCalled bool
	manifestError  error
}
