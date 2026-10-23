package licensing

import (
	"context"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	license "github.com/liferay/liferay-portal/cloud/operator/internal/license"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	conditionLicenseValid               = "LicenseValid"
	entitlementsSecretSuffix            = "-entitlements"
	expirationGracePeriodReplicaCeiling = 1
)

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) degradeLicense(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	replicaCeiling int32,
) (stepOutcome, error) {
	liferayEnvironment.Status.Phase = phaseDegraded

	stepOutcome, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, replicaCeiling,
	)

	if error != nil || stepOutcome.stop {
		return stepOutcome, error
	}

	return stopAfter(liferayEnvironmentReconciler.HeartbeatInterval), nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceLastKnownLicense(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) error {
	expirationCeiling, expiredCondition, licenseExpired :=
		liferayEnvironmentReconciler.evaluateLastKnownLicense(liferayEnvironment, time.Now())

	if !licenseExpired {
		return nil
	}

	meta.SetStatusCondition(&liferayEnvironment.Status.Conditions, expiredCondition)

	liferayEnvironment.Status.Phase = phaseDegraded

	replicaCeiling := expirationCeiling

	if liferayEnvironment.Status.ReplicaCeiling != nil {
		replicaCeiling = min(replicaCeiling, *liferayEnvironment.Status.ReplicaCeiling)
	}

	_, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, replicaCeiling,
	)

	return error
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceLicense(
	context context.Context,
	entitlements *provisioning.Entitlements,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (stepOutcome, error) {
	now := metav1.Now()

	licenseEvaluation := evaluateLicense(
		entitlements, environmentID, liferayEnvironmentReconciler.ExpirationGracePeriod,
		now.Time,
	)

	logger := logf.FromContext(context)

	switch licenseEvaluation.condition.Reason {
	case "EnvironmentMismatch":
		logger.Info(
			"License was issued for a different environment",
			"environmentID", environmentID, "owner", licenseEvaluation.owner,
		)
	case "Expired":
		logger.Info(
			"License expired",
			"environmentID", environmentID,
			"expirationDate", licenseEvaluation.validUntil.Time,
			"replicaCeiling", licenseEvaluation.replicaCeiling,
		)
	case "Invalid":
		logger.Error(
			licenseEvaluation.error, "License validation failed",
			"environmentID", environmentID,
		)
	}

	liferayEnvironment.Status.License.Checksum = licenseChecksum(entitlements.LicenseXML)
	liferayEnvironment.Status.License.LastVerified = &now
	liferayEnvironment.Status.License.MaxClusterNodes = licenseEvaluation.maxClusterNodes
	liferayEnvironment.Status.License.ValidUntil = licenseEvaluation.validUntil

	meta.SetStatusCondition(&liferayEnvironment.Status.Conditions, licenseEvaluation.condition)

	if licenseEvaluation.condition.Status != metav1.ConditionTrue {
		return liferayEnvironmentReconciler.degradeLicense(
			context, liferayEnvironment, licenseEvaluation.replicaCeiling,
		)
	}

	return liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, licenseEvaluation.replicaCeiling,
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) evaluateLastKnownLicense(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	now time.Time,
) (int32, metav1.Condition, bool) {
	licenseStatus := liferayEnvironment.Status.License

	if licenseStatus.MaxClusterNodes == nil || licenseStatus.ValidUntil == nil ||
		!now.After(licenseStatus.ValidUntil.Time) {

		return 0, metav1.Condition{}, false
	}

	expirationCeiling := expirationReplicaCeiling(
		licenseStatus.ValidUntil.Time, liferayEnvironmentReconciler.ExpirationGracePeriod,
		*licenseStatus.MaxClusterNodes, now,
	)

	return expirationCeiling, expiredLicenseCondition(
		licenseStatus.ValidUntil.Time, liferayEnvironmentReconciler.ExpirationGracePeriod,
		now, expirationCeiling,
	), true
}

func evaluateLicense(
	entitlements *provisioning.Entitlements,
	environmentID string,
	expirationGracePeriod time.Duration,
	now time.Time,
) licenseEvaluation {
	owner, error := license.Owner(entitlements.LicenseXML)

	if error != nil {
		return newInvalidLicenseEvaluation(error, nil)
	}

	if owner != environmentID {
		blocked := int32(0)

		return licenseEvaluation{
			condition: metav1.Condition{
				Message: fmt.Sprintf(
					"License owner %q does not match this environment %q.",
					owner, environmentID,
				),
				Reason: "EnvironmentMismatch",
				Status: metav1.ConditionFalse,
				Type:   conditionLicenseValid,
			},
			maxClusterNodes: &blocked,
			owner:           owner,
		}
	}

	maxClusterNodes := entitlements.MaxClusterNodes

	expirationDate, error := license.ExpirationDate(entitlements.LicenseXML)

	if error != nil {
		return newInvalidLicenseEvaluation(error, &maxClusterNodes)
	}

	validUntil := metav1.NewTime(expirationDate)

	if now.After(expirationDate) {
		replicaCeiling := expirationReplicaCeiling(
			expirationDate, expirationGracePeriod, maxClusterNodes, now,
		)

		return licenseEvaluation{
			condition: expiredLicenseCondition(
				expirationDate, expirationGracePeriod, now, replicaCeiling,
			),
			maxClusterNodes: &maxClusterNodes,
			owner:           owner,
			replicaCeiling:  replicaCeiling,
			validUntil:      &validUntil,
		}
	}

	return licenseEvaluation{
		condition: metav1.Condition{
			Reason: "Valid",
			Status: metav1.ConditionTrue,
			Type:   conditionLicenseValid,
		},
		maxClusterNodes: &maxClusterNodes,
		owner:           owner,
		replicaCeiling:  maxClusterNodes,
		validUntil:      &validUntil,
	}
}

func expirationReplicaCeiling(
	expirationDate time.Time,
	expirationGracePeriod time.Duration,
	maxClusterNodes int32,
	now time.Time,
) int32 {
	if !now.After(expirationDate) {
		return maxClusterNodes
	}

	if now.Before(expirationDate.Add(expirationGracePeriod)) {
		return min(expirationGracePeriodReplicaCeiling, maxClusterNodes)
	}

	return 0
}

func expiredLicenseCondition(
	expirationDate time.Time,
	expirationGracePeriod time.Duration,
	now time.Time,
	replicaCeiling int32,
) metav1.Condition {
	gracePeriodEnd := expirationDate.Add(expirationGracePeriod)

	message := fmt.Sprintf(
		"License expired on %s; replicas are capped at %d until the grace period ends on %s.",
		expirationDate.Format(time.RFC3339), replicaCeiling,
		gracePeriodEnd.Format(time.RFC3339),
	)

	if !now.Before(gracePeriodEnd) {
		message = fmt.Sprintf(
			"License expired on %s and its grace period ended on %s; replicas are capped at %d.",
			expirationDate.Format(time.RFC3339), gracePeriodEnd.Format(time.RFC3339),
			replicaCeiling,
		)
	}

	return metav1.Condition{
		Message: message,
		Reason:  "Expired",
		Status:  metav1.ConditionFalse,
		Type:    conditionLicenseValid,
	}
}

func licenseChecksum(licenseXML []byte) string {
	sum := sha256.Sum256(licenseXML)

	return hex.EncodeToString(sum[:])
}

func newInvalidLicenseEvaluation(
	error error,
	maxClusterNodes *int32,
) licenseEvaluation {
	return licenseEvaluation{
		condition: metav1.Condition{
			Message: error.Error(),
			Reason:  "Invalid",
			Status:  metav1.ConditionFalse,
			Type:    conditionLicenseValid,
		},
		error:           error,
		maxClusterNodes: maxClusterNodes,
	}
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) persistEntitlementsSecret(
	context context.Context,
	entitlements *provisioning.Entitlements,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) error {
	addOns := entitlements.AddOns

	if addOns == nil {
		addOns = []provisioning.AddOn{}
	}

	addOnsJSON, error := json.Marshal(addOns)

	if error != nil {
		return error
	}

	return liferayEnvironmentReconciler.ensureOwnedSecret(
		context,
		map[string][]byte{
			"add-ons.json":    addOnsJSON,
			"license.xml":     entitlements.LicenseXML,
			"maxClusterNodes": []byte(fmt.Sprintf("%d", entitlements.MaxClusterNodes)),
		},
		liferayEnvironment, liferayEnvironment.Name+entitlementsSecretSuffix,
	)
}

type licenseEvaluation struct {
	condition       metav1.Condition
	error           error
	maxClusterNodes *int32
	owner           string
	replicaCeiling  int32
	validUntil      *metav1.Time
}
