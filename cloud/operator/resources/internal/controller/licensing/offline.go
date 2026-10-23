package licensing

import (
	"archive/zip"
	"context"
	"crypto/rsa"
	"encoding/json"
	"errors"
	"fmt"
	"os"
	"path/filepath"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const manifestName = "manifest.json"

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) awaitOfflineActivationBundle(
	context context.Context,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (stepOutcome, error) {
	logf.FromContext(context).V(1).Info(
		"Awaiting offline activation bundle", "environmentID", environmentID,
	)

	return liferayEnvironmentReconciler.deferOfflineActivation(
		context, liferayEnvironment,
		"Waiting for the offline activation bundle to be provided", phasePending,
		"AwaitingOfflineActivationBundle",
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) deferOfflineActivation(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	message string,
	phase string,
	reason string,
) (stepOutcome, error) {
	setCondition(conditionActivated, liferayEnvironment, message, reason, metav1.ConditionFalse)

	liferayEnvironment.Status.Phase = phase

	if error := liferayEnvironmentReconciler.enforceLastKnownLicense(
		context, liferayEnvironment,
	); error != nil {
		return stepOutcome{}, error
	}

	return stopAfter(activationPollInterval), nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) extractOfflineAddOns(
	cache addon.Cache,
	context context.Context,
	entitlements *provisioning.Entitlements,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) ([]licensingv1alpha1.AppStatus, error) {
	if len(entitlements.AddOns) == 0 {
		return []licensingv1alpha1.AppStatus{}, nil
	}

	file, error := os.Open(
		liferayEnvironmentReconciler.offlineActivationBundlePath(liferayEnvironment),
	)

	if error != nil {
		return nil, error
	}

	defer file.Close()

	fileInfo, error := file.Stat()

	if error != nil {
		return nil, error
	}

	zipReader, error := zip.NewReader(file, fileInfo.Size())

	if error != nil {
		return nil, fmt.Errorf("offline activation bundle: open zip: %w", error)
	}

	return addon.Extract(entitlements.AddOns, cache, context, zipReader), nil
}

func findManifest(zipReader *zip.Reader) *zip.File {
	for _, file := range zipReader.File {
		if file.Name == manifestName {
			return file
		}
	}

	return nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) handleOfflineActivation(
	context context.Context,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	privateKey *rsa.PrivateKey,
) (*provisioning.Entitlements, stepOutcome, error) {
	logger := logf.FromContext(context)

	if error := liferayEnvironmentReconciler.publishOfflineRequest(
		context, environmentID, liferayEnvironment, privateKey,
	); error != nil {
		return nil, stepOutcome{}, error
	}

	if liferayEnvironment.Spec.OfflineActivationBundle == "" {
		stepOutcome, error := liferayEnvironmentReconciler.awaitOfflineActivationBundle(
			context, environmentID, liferayEnvironment,
		)

		return nil, stepOutcome, error
	}

	offlineActivationBundlePath := liferayEnvironmentReconciler.offlineActivationBundlePath(
		liferayEnvironment,
	)

	entitlements, error := readOfflineActivationBundle(offlineActivationBundlePath)

	if isOfflineActivationBundleNotFound(error) {
		stepOutcome, error := liferayEnvironmentReconciler.awaitOfflineActivationBundle(
			context, environmentID, liferayEnvironment,
		)

		return nil, stepOutcome, error
	}

	if error != nil {
		logger.Error(
			error, "Offline activation bundle is invalid",
			"offlineActivationBundlePath", offlineActivationBundlePath, "environmentID", environmentID,
		)

		stepOutcome, error := liferayEnvironmentReconciler.deferOfflineActivation(
			context, liferayEnvironment, error.Error(), phaseDegraded,
			"OfflineActivationBundleInvalid",
		)

		return nil, stepOutcome, error
	}

	logger.Info(
		"Environment was activated from the offline activation bundle", "environmentID", environmentID,
	)

	markActivated(liferayEnvironment)

	if error := liferayEnvironmentReconciler.persistEntitlementsSecret(context, entitlements, liferayEnvironment); error != nil {
		return nil, stepOutcome{}, error
	}

	return entitlements, proceed(), nil
}

func isOfflineActivationBundleNotFound(error error) bool {
	return errors.Is(error, errOfflineActivationBundleNotFound)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) offlineActivationBundlePath(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) string {
	return filepath.Join(
		liferayEnvironmentReconciler.getEnvironmentDir(liferayEnvironment.Namespace),
		liferayEnvironment.Spec.OfflineActivationBundle,
	)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) persistOfflineRequest(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	payload string,
) error {
	secret := &corev1.Secret{}

	if error := liferayEnvironmentReconciler.Get(
		context, getIdentitySecretKey(liferayEnvironment), secret,
	); error != nil {
		return error
	}

	existing := secret.Data["offline-request"]

	if len(existing) > 0 && !provisioning.PayloadExpired(string(existing)) {
		return nil
	}

	if secret.Data == nil {
		secret.Data = map[string][]byte{}
	}

	secret.Data["offline-request"] = []byte(payload)

	if error := liferayEnvironmentReconciler.Update(context, secret); error != nil {
		return error
	}

	logf.FromContext(context).Info(
		"Offline request was stored in the identity secret", "secret", secret.Name,
	)

	return nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) publishOfflineRequest(
	context context.Context,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	privateKey *rsa.PrivateKey,
) error {
	publicKey, error := publicKeyBase64(privateKey)

	if error != nil {
		return error
	}

	offlineActivationPayload, error := provisioning.OfflineActivationPayload(
		provisioning.ActivationRequest{
			EnvironmentID:   environmentID,
			EnvironmentName: resolveEnvironmentName(liferayEnvironment),
			PublicKey:       publicKey,
		},
		privateKey,
	)

	if error != nil {
		return error
	}

	return liferayEnvironmentReconciler.persistOfflineRequest(
		context, liferayEnvironment, offlineActivationPayload,
	)
}

func readManifest(manifestFile *zip.File) (*provisioning.Entitlements, error) {
	readCloser, error := manifestFile.Open()

	if error != nil {
		return nil, fmt.Errorf("offline activation bundle: open %q: %w", manifestName, error)
	}

	defer readCloser.Close()

	var entitlementsResponse provisioning.EntitlementsResponse

	if error := json.NewDecoder(readCloser).Decode(&entitlementsResponse); error != nil {
		return nil, fmt.Errorf("offline activation bundle: decode %q: %w", manifestName, error)
	}

	return provisioning.EntitlementsFromResponse(entitlementsResponse)
}

func readOfflineActivationBundle(path string) (*provisioning.Entitlements, error) {
	file, error := os.Open(path)

	if errors.Is(error, os.ErrNotExist) {
		return nil, errOfflineActivationBundleNotFound
	}

	if error != nil {
		return nil, error
	}

	defer file.Close()

	fileInfo, error := file.Stat()

	if error != nil {
		return nil, error
	}

	zipReader, error := zip.NewReader(file, fileInfo.Size())

	if error != nil {
		return nil, fmt.Errorf("offline activation bundle: open zip: %w", error)
	}

	manifestFile := findManifest(zipReader)

	if manifestFile == nil {
		return nil, fmt.Errorf("offline activation bundle: missing %q", manifestName)
	}

	return readManifest(manifestFile)
}

var errOfflineActivationBundleNotFound = errors.New("offline activation bundle: not found")
