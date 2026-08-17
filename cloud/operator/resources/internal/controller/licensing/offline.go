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
	"strings"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	addOnsPrefix = "add-ons/"
	manifestName = "manifest.json"
)

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) awaitOfflineActivationBundle(
	context context.Context,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (controllerruntime.Result, error) {
	logf.FromContext(context).V(1).Info(
		"Awaiting offline activation bundle", "environmentID", environmentID,
	)

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Message: "Waiting for the offline activation bundle to be provided",
			Reason:  "AwaitingOfflineActivationBundle",
			Status:  metav1.ConditionFalse,
			Type:    conditionActivated,
		},
	)

	liferayEnvironment.Status.Phase = "Pending"

	return liferayEnvironmentReconciler.finishAfter(
		context, liferayEnvironment, 15*time.Second,
	)
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
) (controllerruntime.Result, error) {
	logger := logf.FromContext(context)

	if error := liferayEnvironmentReconciler.publishOfflineRequest(
		context, environmentID, liferayEnvironment, privateKey,
	); error != nil {
		return controllerruntime.Result{}, error
	}

	if liferayEnvironment.Spec.OfflineActivationBundle == "" {
		return liferayEnvironmentReconciler.awaitOfflineActivationBundle(
			context, environmentID, liferayEnvironment,
		)
	}

	offlineActivationBundlePath := filepath.Join(
		liferayEnvironmentReconciler.MarketplaceDir,
		liferayEnvironment.Namespace,
		environmentID,
		liferayEnvironment.Spec.OfflineActivationBundle,
	)

	entitlements, error := readOfflineActivationBundle(offlineActivationBundlePath)

	if isOfflineActivationBundleNotFound(error) {
		return liferayEnvironmentReconciler.awaitOfflineActivationBundle(
			context, environmentID, liferayEnvironment,
		)
	}

	if error != nil {
		logger.Error(
			error, "Offline activation bundle is invalid",
			"offlineActivationBundlePath", offlineActivationBundlePath, "environmentID", environmentID,
		)

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: error.Error(),
				Reason:  "OfflineActivationBundleInvalid",
				Status:  metav1.ConditionFalse,
				Type:    conditionActivated,
			},
		)

		liferayEnvironment.Status.Phase = "Degraded"

		return liferayEnvironmentReconciler.finishAfter(
			context, liferayEnvironment, 15*time.Second,
		)
	}

	if liferayEnvironment.Status.ActivatedAt == nil {
		now := metav1.Now()

		liferayEnvironment.Status.ActivatedAt = &now
	}

	logger.Info(
		"Environment activated from offline activation bundle", "environmentID", environmentID,
	)

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "Activated",
			Status: metav1.ConditionTrue,
			Type:   conditionActivated,
		},
	)

	if error := liferayEnvironmentReconciler.persistEntitlementsSecret(context, entitlements, liferayEnvironment); error != nil {
		return controllerruntime.Result{}, error
	}

	return controllerruntime.Result{}, nil
}

func hasAddOns(zipReader *zip.Reader) bool {
	for _, file := range zipReader.File {
		if strings.HasPrefix(file.Name, addOnsPrefix) {
			return true
		}
	}

	return false
}

func isOfflineActivationBundleNotFound(error error) bool {
	return errors.Is(error, errOfflineActivationBundleNotFound)
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) persistOfflineRequest(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	payload string,
) error {
	identityName := liferayEnvironment.Name + identitySecretSuffix

	secret := &corev1.Secret{}

	if error := liferayEnvironmentReconciler.Get(
		context, types.NamespacedName{
			Name:      identityName,
			Namespace: liferayEnvironment.Namespace,
		}, secret); error != nil {
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
		"Stored offline request in identity secret", "secret", identityName,
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
			EnvironmentName: liferayEnvironment.Spec.EnvironmentName,
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

	if !hasAddOns(zipReader) {
		return nil, fmt.Errorf("offline activation bundle: missing %q directory", addOnsPrefix)
	}

	manifestFile := findManifest(zipReader)

	if manifestFile == nil {
		return nil, fmt.Errorf("offline activation bundle: missing %q", manifestName)
	}

	return readManifest(manifestFile)
}

var errOfflineActivationBundleNotFound = errors.New("offline activation bundle: not found")
