package licensing

import (
	"strings"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func TestEvaluateLicense(t *testing.T) {
	testCases := map[string]struct {
		expectedMaxClusterNodes *int32
		expectedReason          string
		expectedReplicaCeiling  int32
		expectedStatus          metav1.ConditionStatus
		expectedValidUntil      bool
		licenseXML              string
		now                     time.Time
	}{
		"expired past the grace period": {
			expectedMaxClusterNodes: pointerInt32(3),
			expectedReason:          "Expired",
			expectedReplicaCeiling:  0,
			expectedStatus:          metav1.ConditionFalse,
			expectedValidUntil:      true,
			licenseXML: virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
			),
			now: time.Date(2029, time.March, 4, 0, 0, 0, 0, time.UTC),
		},
		"expired within the grace period": {
			expectedMaxClusterNodes: pointerInt32(3),
			expectedReason:          "Expired",
			expectedReplicaCeiling:  1,
			expectedStatus:          metav1.ConditionFalse,
			expectedValidUntil:      true,
			licenseXML: virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
			),
			now: time.Date(2029, time.March, 2, 12, 0, 0, 0, time.UTC),
		},
		"owner mismatch": {
			expectedMaxClusterNodes: pointerInt32(0),
			expectedReason:          "EnvironmentMismatch",
			expectedReplicaCeiling:  0,
			expectedStatus:          metav1.ConditionFalse,
			expectedValidUntil:      false,
			licenseXML: virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "other-namespace-uid",
			),
			now: time.Date(2029, time.March, 1, 0, 0, 0, 0, time.UTC),
		},
		"unparsable expiration date": {
			expectedMaxClusterNodes: pointerInt32(3),
			expectedReason:          "Invalid",
			expectedReplicaCeiling:  0,
			expectedStatus:          metav1.ConditionFalse,
			expectedValidUntil:      false,
			licenseXML:              virtualClusterLicenseXML("not a date", 3, "dev-namespace-uid"),
			now:                     time.Date(2029, time.March, 1, 0, 0, 0, 0, time.UTC),
		},
		"unparsable owner": {
			expectedMaxClusterNodes: nil,
			expectedReason:          "Invalid",
			expectedReplicaCeiling:  0,
			expectedStatus:          metav1.ConditionFalse,
			expectedValidUntil:      false,
			licenseXML:              "not a license",
			now:                     time.Date(2029, time.March, 1, 0, 0, 0, 0, time.UTC),
		},
		"valid license": {
			expectedMaxClusterNodes: pointerInt32(3),
			expectedReason:          "Valid",
			expectedReplicaCeiling:  3,
			expectedStatus:          metav1.ConditionTrue,
			expectedValidUntil:      true,
			licenseXML: virtualClusterLicenseXML(
				"Friday, March 2, 2029 12:00:00 AM GMT", 3, "dev-namespace-uid",
			),
			now: time.Date(2029, time.March, 1, 0, 0, 0, 0, time.UTC),
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			licenseEvaluation := evaluateLicense(
				&provisioning.Entitlements{
					LicenseXML:      []byte(testCase.licenseXML),
					MaxClusterNodes: 3,
				},
				"dev-namespace-uid", 24*time.Hour, testCase.now,
			)

			if licenseEvaluation.condition.Type != conditionLicenseValid {
				t.Errorf(
					"condition.Type = %q, want %q",
					licenseEvaluation.condition.Type, conditionLicenseValid,
				)
			}

			if licenseEvaluation.condition.Reason != testCase.expectedReason {
				t.Errorf(
					"condition.Reason = %q, want %q",
					licenseEvaluation.condition.Reason, testCase.expectedReason,
				)
			}

			if licenseEvaluation.condition.Status != testCase.expectedStatus {
				t.Errorf(
					"condition.Status = %q, want %q",
					licenseEvaluation.condition.Status, testCase.expectedStatus,
				)
			}

			assertReplicasEqual(
				licenseEvaluation.maxClusterNodes, testCase.expectedMaxClusterNodes,
				"maxClusterNodes", t,
			)

			if licenseEvaluation.replicaCeiling != testCase.expectedReplicaCeiling {
				t.Errorf(
					"replicaCeiling = %d, want %d",
					licenseEvaluation.replicaCeiling, testCase.expectedReplicaCeiling,
				)
			}

			if (licenseEvaluation.validUntil != nil) != testCase.expectedValidUntil {
				t.Errorf(
					"validUntil = %v, want present %t",
					licenseEvaluation.validUntil, testCase.expectedValidUntil,
				)
			}
		})
	}
}

func TestExpirationReplicaCeiling(t *testing.T) {
	expirationDate := time.Date(2026, time.January, 1, 0, 0, 0, 0, time.UTC)

	testCases := map[string]struct {
		elapsed                time.Duration
		expectedReplicaCeiling int32
		maxClusterNodes        int32
	}{
		"caps at the grace ceiling during the grace period": {
			elapsed:                24 * time.Hour,
			expectedReplicaCeiling: 1,
			maxClusterNodes:        3,
		},
		"drops to zero once the grace period ends": {
			elapsed:                90 * 24 * time.Hour,
			expectedReplicaCeiling: 0,
			maxClusterNodes:        3,
		},
		"keeps a licensed maximum below the grace ceiling": {
			elapsed:                24 * time.Hour,
			expectedReplicaCeiling: 0,
			maxClusterNodes:        0,
		},
		"keeps the licensed maximum at the expiration date": {
			elapsed:                0,
			expectedReplicaCeiling: 3,
			maxClusterNodes:        3,
		},
		"keeps the licensed maximum before the expiration date": {
			elapsed:                -time.Hour,
			expectedReplicaCeiling: 3,
			maxClusterNodes:        3,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			actualReplicaCeiling := expirationReplicaCeiling(
				expirationDate, 90*24*time.Hour, testCase.maxClusterNodes,
				expirationDate.Add(testCase.elapsed),
			)

			if actualReplicaCeiling != testCase.expectedReplicaCeiling {
				t.Errorf(
					"expirationReplicaCeiling = %d, want %d",
					actualReplicaCeiling, testCase.expectedReplicaCeiling,
				)
			}
		})
	}
}

func TestReconcileAdoptsEntitlementsSecret(t *testing.T) {
	licenseXML := virtualClusterLicenseXML(
		"Friday, March 2, 2029 12:00:00 AM GMT", 1, "dev-namespace-uid",
	)

	objects := append(
		developmentObjects(),
		&corev1.Secret{
			Data: map[string][]byte{
				"license.xml": []byte("<license>stale</license>"),
			},
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-entitlements",
				Namespace: "liferay-dev",
			},
		},
	)

	provisioningClient := &stubProvisioning{
		entitlements: &provisioning.Entitlements{
			LicenseXML:      []byte(licenseXML),
			MaxClusterNodes: 1,
		},
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(provisioningClient, t, objects...)

	if got := getLicenseXML(liferayEnvironmentReconciler, t); got != licenseXML {
		t.Errorf("license.xml = %q, want the provisioned license", got)
	}

	assertOwnedSecret(liferayEnvironmentReconciler, "dev-entitlements", t)
}

func TestReconcileEnforcesLicenseCeiling(t *testing.T) {
	licenseDate := func(offset time.Duration) string {
		return time.Now().Add(offset).UTC().Format("Monday, January 2, 2006 3:04:05 PM MST")
	}

	testCases := map[string]struct {
		expectedPhase          string
		expectedReason         string
		expectedReplicaCeiling int32
		licenseXML             string
	}{
		"a license expired beyond the grace period scales to zero": {
			expectedPhase:          "Degraded",
			expectedReason:         "Expired",
			expectedReplicaCeiling: 0,
			licenseXML: virtualClusterLicenseXML(
				licenseDate(-100*24*time.Hour), 3, "dev-namespace-uid",
			),
		},
		"a license expired within the grace period scales to the grace ceiling": {
			expectedPhase:          "Degraded",
			expectedReason:         "Expired",
			expectedReplicaCeiling: 1,
			licenseXML: virtualClusterLicenseXML(
				licenseDate(-24*time.Hour), 3, "dev-namespace-uid",
			),
		},
		"a license for another environment scales to zero": {
			expectedPhase:          "Degraded",
			expectedReason:         "EnvironmentMismatch",
			expectedReplicaCeiling: 0,
			licenseXML: virtualClusterLicenseXML(
				licenseDate(365*24*time.Hour), 3, "some-other-environment-uid",
			),
		},
		"a license without a valid expiration date scales to zero": {
			expectedPhase:          "Degraded",
			expectedReason:         "Invalid",
			expectedReplicaCeiling: 0,
			licenseXML: virtualClusterLicenseXML(
				"not a date", 3, "dev-namespace-uid",
			),
		},
		"a valid license scales to the licensed maximum": {
			expectedPhase:          "Ready",
			expectedReason:         "Valid",
			expectedReplicaCeiling: 3,
			licenseXML: virtualClusterLicenseXML(
				licenseDate(365*24*time.Hour), 3, "dev-namespace-uid",
			),
		},
		"an unparseable license scales to zero": {
			expectedPhase:          "Degraded",
			expectedReason:         "Invalid",
			expectedReplicaCeiling: 0,
			licenseXML:             "<licenses><license>",
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			objects := developmentObjects()

			environment := objects[len(objects)-1].(*licensingv1alpha1.LiferayEnvironment)

			environment.Spec.DesiredReplicas = pointerInt32(3)

			liferayEnvironmentReconciler, _ := reconcileEnvironment(
				&stubProvisioning{
					entitlements: &provisioning.Entitlements{
						LicenseXML:      []byte(testCase.licenseXML),
						MaxClusterNodes: 3,
					},
				},
				t,
				objects...,
			)

			liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

			assertReplicasEqual(
				liferayEnvironment.Status.ReplicaCeiling,
				&testCase.expectedReplicaCeiling,
				"status.replicaCeiling",
				t,
			)

			assertReplicasEqual(
				getStatefulSet(liferayEnvironmentReconciler, t).Spec.Replicas,
				&testCase.expectedReplicaCeiling,
				"statefulSet.spec.replicas",
				t,
			)

			if liferayEnvironment.Status.Phase != testCase.expectedPhase {
				t.Errorf(
					"Phase = %q, want %q",
					liferayEnvironment.Status.Phase, testCase.expectedPhase,
				)
			}

			condition := meta.FindStatusCondition(
				liferayEnvironment.Status.Conditions, conditionLicenseValid,
			)

			if condition == nil || condition.Reason != testCase.expectedReason {
				t.Errorf(
					"LicenseValid condition = %v, want reason %q",
					condition, testCase.expectedReason,
				)
			}
		})
	}
}

func TestReconcileRejectsLicenseIssuedForAnotherEnvironment(t *testing.T) {
	entitlements := &provisioning.Entitlements{
		LicenseXML: []byte(virtualClusterLicenseXML(
			"Friday, March 2, 2029 12:00:00 AM GMT", 3, "some-other-environment-uid",
		)),
		MaxClusterNodes: 3,
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(
		&stubProvisioning{entitlements: entitlements}, t, developmentObjects()...,
	)

	liferayEnvironment := getEnvironment(liferayEnvironmentReconciler, t)

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf("Phase = %q, want Degraded", liferayEnvironment.Status.Phase)
	}

	condition := meta.FindStatusCondition(
		liferayEnvironment.Status.Conditions, conditionLicenseValid,
	)

	if condition == nil || condition.Status != metav1.ConditionFalse ||
		condition.Reason != "EnvironmentMismatch" {

		t.Errorf("LicenseValid condition = %v, want False/EnvironmentMismatch", condition)
	}

	if maxClusterNodes := liferayEnvironment.Status.License.MaxClusterNodes; maxClusterNodes == nil ||
		*maxClusterNodes != 0 {

		t.Errorf("License.MaxClusterNodes = %v, want a clamped 0", maxClusterNodes)
	}

	statefulSet := getStatefulSet(liferayEnvironmentReconciler, t)

	assertReplicasEqual(
		statefulSet.Spec.Replicas, pointerInt32(0), "statefulSet.spec.replicas", t,
	)
}

func TestReconcileWritesExpiredLicenseThrough(t *testing.T) {
	expiredLicenseXML := virtualClusterLicenseXML(
		"Wednesday, January 1, 2020 12:00:00 AM GMT", 1, "dev-namespace-uid",
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

	if condition != nil && !strings.Contains(condition.Message, "grace period ended") {
		t.Errorf(
			"LicenseValid message = %q, want it to say the grace period ended",
			condition.Message,
		)
	}

	if liferayEnvironment.Status.Phase != "Degraded" {
		t.Errorf("Phase = %q, want Degraded", liferayEnvironment.Status.Phase)
	}

	assertReplicasEqual(
		liferayEnvironment.Status.ReplicaCeiling, pointerInt32(0), "status.replicaCeiling", t,
	)

	assertReplicasEqual(
		getStatefulSet(liferayEnvironmentReconciler, t).Spec.Replicas,
		pointerInt32(0),
		"statefulSet.spec.replicas",
		t,
	)
}
