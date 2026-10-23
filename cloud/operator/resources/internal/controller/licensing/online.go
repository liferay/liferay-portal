package licensing

import (
	"context"
	"crypto/rsa"
	"fmt"
	"strings"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const conditionProvisioningReachable = "ProvisioningReachable"

func extractLiferayImageTag(image string) string {
	if index := strings.LastIndex(image, "@"); index != -1 {
		image = image[:index]
	}

	index := strings.LastIndex(image, ":")

	if index == -1 {
		return ""
	}

	repository := image[:index]

	if !strings.HasSuffix(repository, "liferay/dxp") {
		return ""
	}

	tag := image[index+1:]

	if strings.Contains(tag, "/") {
		return ""
	}

	return tag
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) handleOnlineActivation(
	context context.Context,
	environmentID string,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	privateKey *rsa.PrivateKey,
) (*provisioning.Entitlements, controllerruntime.Result, error) {
	logger := logf.FromContext(context)

	if liferayEnvironment.Status.ActivatedAt == nil {
		publicKey, error := publicKeyBase64(privateKey)

		if error != nil {
			return nil, controllerruntime.Result{}, error
		}

		activationCode, error := liferayEnvironmentReconciler.readActivationCode(context, liferayEnvironment)

		if errors.IsNotFound(error) {
			logger.V(1).Info("Awaiting activation code", "environmentID", environmentID)

			meta.SetStatusCondition(
				&liferayEnvironment.Status.Conditions,
				metav1.Condition{
					Message: "Waiting for the activation code secret to be created",
					Reason:  "AwaitingActivationCode",
					Status:  metav1.ConditionFalse,
					Type:    conditionActivated,
				},
			)

			liferayEnvironment.Status.Phase = "Pending"

			result, error := liferayEnvironmentReconciler.finishAfter(
				context, liferayEnvironment, 15*time.Second,
			)

			return nil, result, error
		}

		if error != nil {
			return nil, controllerruntime.Result{}, error
		}

		logger.Info(
			"Activating environment",
			"environmentID", environmentID,
			"environmentName", resolveEnvironmentName(liferayEnvironment),
		)

		if error := liferayEnvironmentReconciler.Provisioning.Activate(
			provisioning.ActivationRequest{
				ActivationCode:  activationCode,
				EnvironmentID:   environmentID,
				EnvironmentName: resolveEnvironmentName(liferayEnvironment),
				PublicKey:       publicKey,
			}, context, privateKey); error != nil {
			logger.Error(error, "Activation rejected", "environmentID", environmentID)

			meta.SetStatusCondition(
				&liferayEnvironment.Status.Conditions,
				metav1.Condition{
					Message: error.Error(),
					Reason:  "ActivationRejected",
					Status:  metav1.ConditionFalse,
					Type:    conditionActivated,
				},
			)

			liferayEnvironment.Status.ConsecutiveFailures++
			liferayEnvironment.Status.Phase = "Degraded"

			result, error := liferayEnvironmentReconciler.finishWithBackoff(
				context, liferayEnvironment,
			)

			return nil, result, error
		}

		now := metav1.Now()

		liferayEnvironment.Status.ActivatedAt = &now

		logger.Info("Environment activated", "environmentID", environmentID)

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Reason: "Activated",
				Status: metav1.ConditionTrue,
				Type:   conditionActivated,
			},
		)
	}

	entitlements, error := liferayEnvironmentReconciler.Provisioning.Manifest(
		context,
		provisioning.ManifestRequest{
			DxpVersion:    liferayEnvironmentReconciler.resolveDxpVersion(context, liferayEnvironment),
			EnvironmentID: environmentID,
		},
		privateKey,
	)

	if error != nil {
		logger.Error(error, "Entitlements fetch failed", "environmentID", environmentID)

		meta.SetStatusCondition(
			&liferayEnvironment.Status.Conditions,
			metav1.Condition{
				Message: error.Error(),
				Reason:  "EntitlementsFetchFailed",
				Status:  metav1.ConditionFalse,
				Type:    conditionProvisioningReachable,
			},
		)

		liferayEnvironment.Status.ConsecutiveFailures++

		if liferayEnvironment.Status.UnreachableSince == nil {
			unreachableSince := metav1.NewTime(time.Now())

			liferayEnvironment.Status.UnreachableSince = &unreachableSince
		}

		liferayEnvironment.Status.Phase = "Degraded"

		if error := liferayEnvironmentReconciler.enforceProvisioningGracePeriod(
			context, liferayEnvironment,
		); error != nil {
			return nil, controllerruntime.Result{}, error
		}

		result, error := liferayEnvironmentReconciler.finishWithBackoff(
			context, liferayEnvironment,
		)

		return nil, result, error
	}

	logger.Info(
		"Entitlements fetched",
		"environmentID", environmentID,
		"maxClusterNodes", entitlements.MaxClusterNodes,
	)

	meta.SetStatusCondition(
		&liferayEnvironment.Status.Conditions,
		metav1.Condition{
			Reason: "Reachable",
			Status: metav1.ConditionTrue,
			Type:   conditionProvisioningReachable,
		},
	)

	liferayEnvironment.Status.ConsecutiveFailures = 0

	liferayEnvironmentReconciler.clearUnreachable(context, liferayEnvironment)

	if error := liferayEnvironmentReconciler.persistEntitlementsSecret(
		context, entitlements, liferayEnvironment); error != nil {
		return nil, controllerruntime.Result{}, error
	}

	return entitlements, controllerruntime.Result{}, nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) readActivationCode(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) (string, error) {
	reference := liferayEnvironment.Spec.ActivationCodeSecretRef

	key := types.NamespacedName{
		Name:      reference.Name,
		Namespace: liferayEnvironment.Namespace,
	}

	secret := &corev1.Secret{}

	if error := liferayEnvironmentReconciler.Get(context, key, secret); error != nil {
		return "", error
	}

	code, ok := secret.Data[reference.Key]

	if !ok {
		return "", fmt.Errorf(
			"activation code secret %q missing key %q",
			reference.Name, reference.Key)
	}

	return string(code), nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) resolveDxpVersion(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) string {
	if liferayEnvironment.Spec.DxpVersion != "" {
		return liferayEnvironment.Spec.DxpVersion
	}

	logger := logf.FromContext(context)

	workload, error := liferayEnvironmentReconciler.getWorkload(
		context, liferayEnvironment,
	)

	if error != nil {
		logger.V(1).Info(
			"Unable to read the workload to determine the DXP version",
			"workload", liferayEnvironment.Spec.WorkloadRef.Name,
		)

		return ""
	}

	for _, container := range getPodTemplate(workload).Spec.Containers {
		dxpVersion := extractLiferayImageTag(container.Image)

		if dxpVersion != "" {
			return dxpVersion
		}
	}

	logger.V(1).Info(
		"The workload carries no image tag to determine the DXP version from",
		"workload", liferayEnvironment.Spec.WorkloadRef.Name,
	)

	return ""
}
