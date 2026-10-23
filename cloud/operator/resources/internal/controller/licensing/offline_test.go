package licensing

import (
	"archive/zip"
	"context"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"testing"
	"time"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
)

func TestReadOfflineActivationBundleParsesALicenseOnlyBundle(t *testing.T) {
	licenseXML := "<licenses>ok</licenses>"

	testCases := map[string]string{
		"with an empty add-ons array": fmt.Sprintf(
			`{
				"add-ons": [],
				"licenseXML": %q,
				"maxClusterNodes": 1
			}`,
			base64.StdEncoding.EncodeToString([]byte(licenseXML)),
		),
		"without an add-ons key": fmt.Sprintf(
			`{
				"licenseXML": %q,
				"maxClusterNodes": 1
			}`,
			base64.StdEncoding.EncodeToString([]byte(licenseXML)),
		),
	}

	for name, manifest := range testCases {
		t.Run(name, func(t *testing.T) {
			path := filepath.Join(t.TempDir(), "bundle.zip")

			writeOfflineActivationBundle(
				map[string]string{"manifest.json": manifest}, path, t,
			)

			entitlements, error := readOfflineActivationBundle(path)

			if error != nil {
				t.Fatalf("Unexpected error: %v", error)
			}

			if string(entitlements.LicenseXML) != licenseXML {
				t.Errorf("LicenseXML = %q, want %q", entitlements.LicenseXML, licenseXML)
			}

			if entitlements.MaxClusterNodes != 1 {
				t.Errorf("MaxClusterNodes = %d, want 1", entitlements.MaxClusterNodes)
			}

			if length := len(entitlements.AddOns); length != 0 {
				t.Errorf("AddOns length = %d, want 0", length)
			}
		})
	}
}

func TestReadOfflineActivationBundleParsesAWellFormedBundle(t *testing.T) {
	path := filepath.Join(t.TempDir(), "bundle.zip")

	licenseXML := "<licenses>ok</licenses>"

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app.lpkg": "PK-fake",
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [
						{
							"productId": "fake",
							"sha256Checksum": "0000"
						}
					],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		path, t,
	)

	entitlements, error := readOfflineActivationBundle(path)

	if error != nil {
		t.Fatalf("Unexpected error: %v", error)
	}

	if string(entitlements.LicenseXML) != licenseXML {
		t.Errorf("LicenseXML = %q, want %q", entitlements.LicenseXML, licenseXML)
	}

	if entitlements.MaxClusterNodes != 3 {
		t.Errorf("MaxClusterNodes = %d, want 3", entitlements.MaxClusterNodes)
	}

	if length := len(entitlements.AddOns); length != 1 {
		t.Fatalf("AddOns length = %d, want 1", length)
	}

	if entitlements.AddOns[0].ProductID != "fake" {
		t.Errorf("AddOns[0].ProductID = %q, want fake", entitlements.AddOns[0].ProductID)
	}
}

func TestReadOfflineActivationBundleRejectsANonZipFile(t *testing.T) {
	path := filepath.Join(t.TempDir(), "not.zip")

	if error := os.WriteFile(path, []byte("this is not a zip"), 0o644); error != nil {
		t.Fatalf("Unable to write the file: %v", error)
	}

	if _, error := readOfflineActivationBundle(path); error == nil {
		t.Error("readOfflineActivationBundle error = nil, want a zip error")
	}
}

func TestReadOfflineActivationBundleRejectsMalformedBundles(t *testing.T) {
	testCases := map[string]map[string]string{
		"missing manifest": {
			"add-ons/app.lpkg": "PK-fake",
		},
		"undecodable license": {
			"manifest.json": `{
					"licenseXML": "@@not-base64@@",
					"maxClusterNodes": 1
				}`,
		},
		"unparsable manifest json": {
			"manifest.json": "{not-json",
		},
	}

	for name, files := range testCases {
		t.Run(name, func(t *testing.T) {
			path := filepath.Join(t.TempDir(), "bundle.zip")

			writeOfflineActivationBundle(files, path, t)

			if _, error := readOfflineActivationBundle(path); error == nil {
				t.Error("readOfflineActivationBundle error = nil, want a validation error")
			}
		})
	}
}

func TestReadOfflineActivationBundleReturnsNotFoundForAMissingFile(t *testing.T) {
	_, error := readOfflineActivationBundle(filepath.Join(t.TempDir(), "absent.zip"))

	if !isOfflineActivationBundleNotFound(error) {
		t.Errorf("readOfflineActivationBundle error = %v, want isOfflineActivationBundleNotFound", error)
	}
}

func TestReconcileOfflineAppliesLastKnownLicense(t *testing.T) {
	testCases := map[string]struct {
		bundle                 string
		expectedPhase          string
		expectedReplicaCeiling *int32
		expectedReplicas       int32
		licensed               bool
		validUntil             time.Duration
	}{
		"a missing bundle file drops an expired license to zero": {
			bundle:                 "missing",
			expectedPhase:          "Degraded",
			expectedReplicaCeiling: pointerInt32(0),
			expectedReplicas:       0,
			licensed:               true,
			validUntil:             -100 * 24 * time.Hour,
		},
		"a missing bundle file keeps a valid license": {
			bundle:                 "missing",
			expectedPhase:          "Pending",
			expectedReplicaCeiling: pointerInt32(3),
			expectedReplicas:       3,
			licensed:               true,
			validUntil:             365 * 24 * time.Hour,
		},
		"an invalid bundle caps a license expired within the grace period": {
			bundle:                 "invalid",
			expectedPhase:          "Degraded",
			expectedReplicaCeiling: pointerInt32(1),
			expectedReplicas:       1,
			licensed:               true,
			validUntil:             -24 * time.Hour,
		},
		"an invalid bundle drops an expired license to zero": {
			bundle:                 "invalid",
			expectedPhase:          "Degraded",
			expectedReplicaCeiling: pointerInt32(0),
			expectedReplicas:       0,
			licensed:               true,
			validUntil:             -100 * 24 * time.Hour,
		},
		"an invalid bundle keeps a valid license": {
			bundle:                 "invalid",
			expectedPhase:          "Degraded",
			expectedReplicaCeiling: pointerInt32(3),
			expectedReplicas:       3,
			licensed:               true,
			validUntil:             365 * 24 * time.Hour,
		},
		"an unset bundle drops an expired license to zero": {
			bundle:                 "unset",
			expectedPhase:          "Degraded",
			expectedReplicaCeiling: pointerInt32(0),
			expectedReplicas:       0,
			licensed:               true,
			validUntil:             -100 * 24 * time.Hour,
		},
		"an unset bundle enforces nothing before a license is known": {
			bundle:                 "unset",
			expectedPhase:          "Pending",
			expectedReplicaCeiling: nil,
			expectedReplicas:       3,
			licensed:               false,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			marketplaceMountPath := t.TempDir()

			environment := activatedEnvironment()
			environment.Spec.DesiredReplicas = pointerInt32(3)
			environment.Spec.Offline = true

			if testCase.bundle != "unset" {
				environment.Spec.OfflineActivationBundle = "bundle.zip"
			}

			if testCase.bundle == "invalid" {
				writeOfflineActivationBundle(
					map[string]string{
						"add-ons/app.lpkg": "PK-fake-lpkg",
					},
					filepath.Join(marketplaceMountPath, "liferay-dev", "bundle.zip"),
					t,
				)
			}

			if testCase.licensed {
				validUntil := metav1.NewTime(time.Now().Add(testCase.validUntil))

				environment.Status.License.MaxClusterNodes = pointerInt32(3)
				environment.Status.License.ValidUntil = &validUntil
				environment.Status.ReplicaCeiling = pointerInt32(3)
			}

			liferayEnvironmentReconciler, _ := reconcileOfflineActivationBundle(
				marketplaceMountPath, t,
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
				&testCase.expectedReplicas,
				"statefulSet.spec.replicas",
				t,
			)

			liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

			assertReplicasEqual(
				liferayEnvironment.Status.ReplicaCeiling,
				testCase.expectedReplicaCeiling,
				"status.replicaCeiling",
				t,
			)

			if liferayEnvironment.Status.Phase != testCase.expectedPhase {
				t.Errorf(
					"Phase = %q, want %q",
					liferayEnvironment.Status.Phase, testCase.expectedPhase,
				)
			}

			if !testCase.licensed || testCase.validUntil > 0 {
				return
			}

			condition := meta.FindStatusCondition(
				liferayEnvironment.Status.Conditions, conditionLicenseValid,
			)

			if condition == nil || condition.Reason != "Expired" {
				t.Errorf("LicenseValid condition = %v, want reason Expired", condition)
			}
		})
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

func TestReconcileOfflineExtractsAddOnsFromBundle(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	lpkgContent := "PK-fake-lpkg-content"

	checksum := sha256.Sum256([]byte(lpkgContent))

	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
	)

	writeOfflineActivationBundle(
		map[string]string{
			"add-ons/app-1.lpkg": lpkgContent,
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [
						{
							"productId": "app-1",
							"productName": "App One",
							"virtualEntryId": 42,
							"sha256Checksum": %q
						}
					],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
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

	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
	)

	writeOfflineActivationBundle(
		map[string]string{
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
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

	if liferayEnvironment.Status.License.MaxClusterNodes == nil ||
		*liferayEnvironment.Status.License.MaxClusterNodes != 3 {
		t.Errorf(
			"License.MaxClusterNodes = %v, want 3",
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

func TestReconcileOfflineReportsAddOnsNotReadyWhenBundleLacksAddOn(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
	)

	writeOfflineActivationBundle(
		map[string]string{
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [
						{
							"productId": "app-1",
							"productName": "App One",
							"virtualEntryId": 42,
							"sha256Checksum": "0000"
						}
					],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		filepath.Join(marketplaceMountPath, "liferay-dev", "bundle.zip"),
		t,
	)

	environment := pendingEnvironment()
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"

	liferayEnvironmentReconciler, _ := reconcileOfflineActivationBundle(
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

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready despite the missing add-on", liferayEnvironment.Status.Phase)
	}

	if activated := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionActivated,
	); activated == nil || activated.Status != metav1.ConditionTrue {
		t.Errorf("Activated condition = %v, want True", activated)
	}

	if addOnsReady := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionAddOnsReady,
	); addOnsReady == nil || addOnsReady.Status != metav1.ConditionFalse ||
		addOnsReady.Reason != "DownloadsFailing" {

		t.Errorf("AddOnsReady condition = %v, want False/DownloadsFailing", addOnsReady)
	}

	if length := len(liferayEnvironment.Status.Apps); length != 1 {
		t.Fatalf("Apps length = %d, want 1", length)
	}

	if state := liferayEnvironment.Status.Apps[0].State; state != "Failed" {
		t.Errorf("App state = %q, want Failed", state)
	}
}

func TestReconcileOfflineRequestCarriesEnvironmentName(t *testing.T) {
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
				t.Fatalf("JWT payload segment is not valid JSON: %v", error)
			}

			if claimsMap["environmentName"] != testCase.expectedEnvironmentName {
				t.Errorf(
					"environmentName claim = %v, want %q",
					claimsMap["environmentName"],
					testCase.expectedEnvironmentName,
				)
			}
		})
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

func TestReconcileOfflineRestoresCeilingAfterOwnerMatches(t *testing.T) {
	marketplaceMountPath := t.TempDir()

	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
	)

	writeOfflineActivationBundle(
		map[string]string{
			"manifest.json": fmt.Sprintf(
				`{
					"add-ons": [],
					"licenseXML": %q,
					"maxClusterNodes": 3
				}`,
				base64.StdEncoding.EncodeToString([]byte(licenseXML)),
			),
		},
		filepath.Join(
			marketplaceMountPath, "liferay-dev", "bundle.zip",
		),
		t,
	)

	activatedAt := metav1.Now()

	environment := pendingEnvironment()
	environment.Spec.DesiredReplicas = pointerInt32(3)
	environment.Spec.Offline = true
	environment.Spec.OfflineActivationBundle = "bundle.zip"
	environment.Status.ActivatedAt = &activatedAt
	environment.Status.License.MaxClusterNodes = pointerInt32(0)
	environment.Status.Phase = "Degraded"

	meta.SetStatusCondition(
		&environment.Status.Conditions,
		metav1.Condition{
			Message: "License was issued for a different environment.",
			Reason:  "EnvironmentMismatch",
			Status:  metav1.ConditionFalse,
			Type:    conditionLicenseValid,
		},
	)

	meta.SetStatusCondition(
		&environment.Status.Conditions,
		metav1.Condition{
			Message: "Requested 3 replicas exceeds the replica ceiling of 0; capping to 0.",
			Reason:  "ExceedsLicensedMaximum",
			Status:  metav1.ConditionFalse,
			Type:    conditionReplicasCountValid,
		},
	)

	liferayEnvironmentReconciler, _ := reconcileOfflineActivationBundle(
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
				Replicas: pointerInt32(0),
			},
		},
		environment,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Ready" {
		t.Errorf("Phase = %q, want Ready after the owner matches again", liferayEnvironment.Status.Phase)
	}

	if liferayEnvironment.Status.License.MaxClusterNodes == nil ||
		*liferayEnvironment.Status.License.MaxClusterNodes != 3 {
		t.Errorf(
			"License.MaxClusterNodes = %v, want the restored 3",
			liferayEnvironment.Status.License.MaxClusterNodes,
		)
	}

	if licenseValid := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	); licenseValid == nil || licenseValid.Status != metav1.ConditionTrue ||
		licenseValid.Reason != "Valid" {

		t.Errorf("LicenseValid condition = %v, want True/Valid", licenseValid)
	}

	if replicasValid := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionReplicasCountValid,
	); replicasValid == nil || replicasValid.Status != metav1.ConditionTrue ||
		replicasValid.Reason != "WithinLicensedLimit" {

		t.Errorf(
			"ReplicasCountValid condition = %v, want True/WithinLicensedLimit", replicasValid,
		)
	}

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(3), "statefulSet.spec.replicas", t,
	)
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

func writeOfflineActivationBundle(files map[string]string, path string, t *testing.T) {
	t.Helper()

	if error := os.MkdirAll(filepath.Dir(path), 0o755); error != nil {
		t.Fatalf("Unable to create the bundle directory: %v", error)
	}

	file, error := os.Create(path)

	if error != nil {
		t.Fatalf("Unable to create the bundle file: %v", error)
	}

	defer file.Close()

	zipWriter := zip.NewWriter(file)

	for name, content := range files {
		writer, error := zipWriter.Create(name)

		if error != nil {
			t.Fatalf("Unable to add %q to the bundle: %v", name, error)
		}

		if _, error := writer.Write([]byte(content)); error != nil {
			t.Fatalf("Unable to write %q to the bundle: %v", name, error)
		}
	}

	if error := zipWriter.Close(); error != nil {
		t.Fatalf("Unable to finalize the bundle: %v", error)
	}
}
