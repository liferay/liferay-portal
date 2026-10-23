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
	controllerruntime "sigs.k8s.io/controller-runtime"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	conditionLicenseValid               = "LicenseValid"
	entitlementsSecretSuffix            = "-entitlements"
	expirationGracePeriodReplicaCeiling = 1
)

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) degradeLicense(
	condition metav1.Condition,
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	replicaCeiling int32,
) (controllerruntime.Result, error) {
	meta.SetStatusCondition(&liferayEnvironment.Status.Conditions, condition)

	liferayEnvironment.Status.Phase = "Degraded"

	requeueAfter, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, replicaCeiling,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	if requeueAfter == 0 {
		requeueAfter = liferayEnvironmentReconciler.HeartbeatInterval
	}

	return liferayEnvironmentReconciler.finishAfter(
		context, liferayEnvironment, requeueAfter,
	)
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

	liferayEnvironment.Status.Phase = "Degraded"

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
) (controllerruntime.Result, error) {
	logger := logf.FromContext(context)

	now := metav1.Now()

	liferayEnvironment.Status.License.Checksum = licenseChecksum(entitlements.LicenseXML)
	liferayEnvironment.Status.License.LastVerified = &now

	owner, error := license.Owner(entitlements.LicenseXML)

	if error != nil {
		logger.Error(error, "License validation failed", "environmentID", environmentID)

		liferayEnvironment.Status.License.MaxClusterNodes = nil
		liferayEnvironment.Status.License.ValidUntil = nil

		return liferayEnvironmentReconciler.degradeLicense(
			metav1.Condition{
				Message: error.Error(),
				Reason:  "Invalid",
				Status:  metav1.ConditionFalse,
				Type:    conditionLicenseValid,
			},
			context, liferayEnvironment, 0,
		)
	}

	if owner != environmentID {
		logger.Info(
			"License was issued for a different environment",
			"environmentID", environmentID, "owner", owner,
		)

		blocked := int32(0)

		liferayEnvironment.Status.License.MaxClusterNodes = &blocked
		liferayEnvironment.Status.License.ValidUntil = nil

		return liferayEnvironmentReconciler.degradeLicense(
			metav1.Condition{
				Message: fmt.Sprintf(
					"License owner %q does not match this environment %q.",
					owner, environmentID,
				),
				Reason: "EnvironmentMismatch",
				Status: metav1.ConditionFalse,
				Type:   conditionLicenseValid,
			},
			context, liferayEnvironment, 0,
		)
	}

	maxClusterNodes := entitlements.MaxClusterNodes

	liferayEnvironment.Status.License.MaxClusterNodes = &maxClusterNodes

	expirationDate, error := license.ExpirationDate(entitlements.LicenseXML)

	if error != nil {
		logger.Error(error, "License validation failed", "environmentID", environmentID)

		liferayEnvironment.Status.License.ValidUntil = nil

		return liferayEnvironmentReconciler.degradeLicense(
			metav1.Condition{
				Message: error.Error(),
				Reason:  "Invalid",
				Status:  metav1.ConditionFalse,
				Type:    conditionLicenseValid,
			},
			context, liferayEnvironment, 0,
		)
	}

	validUntil := metav1.NewTime(expirationDate)

	liferayEnvironment.Status.License.ValidUntil = &validUntil

	if now.After(expirationDate) {
		replicaCeiling := expirationReplicaCeiling(
			expirationDate, liferayEnvironmentReconciler.ExpirationGracePeriod,
			maxClusterNodes, now.Time,
		)

		logger.Info(
			"License expired",
			"environmentID", environmentID,
			"expirationDate", expirationDate,
			"replicaCeiling", replicaCeiling,
		)

		return liferayEnvironmentReconciler.degradeLicense(
			expiredLicenseCondition(
				expirationDate, liferayEnvironmentReconciler.ExpirationGracePeriod,
				now.Time, replicaCeiling,
			),
			context, liferayEnvironment, replicaCeiling,
		)
	}

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "Valid",
			Status: metav1.ConditionTrue,
			Type:   conditionLicenseValid,
		},
	)

	requeueAfter, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, entitlements.MaxClusterNodes,
	)

	if error != nil {
		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{RequeueAfter: requeueAfter}, nil
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
