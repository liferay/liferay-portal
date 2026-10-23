package licensing

import (
	"context"
	"fmt"
	"slices"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	errors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	annotationScaledToZero                  = "licensing.liferay.com/scaled-to-zero"
	conditionProvisioningGracePeriodExpired = "ProvisioningGracePeriodExpired"
	conditionReplicasCountValid             = "ReplicasCountValid"
	provisioningGracePeriodReplicaCeiling   = 1
)

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) clearUnreachable(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) {
	if liferayEnvironment.Status.UnreachableSince == nil {
		return
	}

	if meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionProvisioningGracePeriodExpired,
	) {
		logf.FromContext(context).Info(
			"Provisioning recovered; restoring the licensed replica ceiling",
			"environmentID", liferayEnvironment.Status.EnvironmentID,
		)

		liferayEnvironmentReconciler.Recorder.Event(
			liferayEnvironment,
			corev1.EventTypeNormal,
			"ProvisioningRecovered",
			"Provisioning is reachable again; the licensed replica ceiling was restored.",
		)
	}

	meta.RemoveStatusCondition(
		&liferayEnvironment.Status.Conditions, conditionProvisioningGracePeriodExpired,
	)

	liferayEnvironment.Status.UnreachableSince = nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceProvisioningGracePeriod(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) error {
	if liferayEnvironment.Status.UnreachableSince == nil {
		return nil
	}

	now := time.Now()

	provisioningGracePeriodExpired := now.Sub(liferayEnvironment.Status.UnreachableSince.Time) >=
		liferayEnvironmentReconciler.ProvisioningGracePeriod

	expirationCeiling, expiredCondition, licenseExpired :=
		liferayEnvironmentReconciler.evaluateLastKnownLicense(liferayEnvironment, now)

	if !provisioningGracePeriodExpired && !licenseExpired {
		return nil
	}

	var replicaCeilings []int32

	if liferayEnvironment.Status.ReplicaCeiling != nil {
		replicaCeilings = append(replicaCeilings, *liferayEnvironment.Status.ReplicaCeiling)
	}

	if provisioningGracePeriodExpired {
		replicaCeilings = append(replicaCeilings, provisioningGracePeriodReplicaCeiling)
	}

	if licenseExpired {
		meta.SetStatusCondition(&liferayEnvironment.Status.Conditions, expiredCondition)

		replicaCeilings = append(replicaCeilings, expirationCeiling)
	}

	replicaCeiling := slices.Min(replicaCeilings)

	if _, error := liferayEnvironmentReconciler.enforceReplicaCeiling(
		context, liferayEnvironment, replicaCeiling,
	); error != nil {
		return error
	}

	if !provisioningGracePeriodExpired {
		return nil
	}

	message := fmt.Sprintf(
		"Provisioning has been unreachable since %s; the replica ceiling of %q is %d.",
		liferayEnvironment.Status.UnreachableSince.Format(time.RFC3339),
		liferayEnvironment.Spec.WorkloadRef.Name,
		replicaCeiling,
	)

	if !meta.IsStatusConditionTrue(
		liferayEnvironment.Status.Conditions, conditionProvisioningGracePeriodExpired,
	) {
		logf.FromContext(context).Error(
			nil, message, "environmentID", liferayEnvironment.Status.EnvironmentID,
		)

		liferayEnvironmentReconciler.Recorder.Event(
			liferayEnvironment,
			corev1.EventTypeWarning,
			"ProvisioningGracePeriodExpired",
			message,
		)
	}

	setCondition(
		conditionProvisioningGracePeriodExpired, liferayEnvironment, message,
		"ProvisioningUnreachable", metav1.ConditionTrue,
	)

	return nil
}

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) enforceReplicaCeiling(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	replicaCeiling int32,
) (stepOutcome, error) {
	logger := logf.FromContext(context)

	ceilingChanged := liferayEnvironment.Status.ReplicaCeiling == nil ||
		*liferayEnvironment.Status.ReplicaCeiling != replicaCeiling

	liferayEnvironment.Status.ReplicaCeiling = &replicaCeiling

	workloadKind := liferayEnvironment.Spec.WorkloadRef.EffectiveKind()

	workload, getError := liferayEnvironmentReconciler.getWorkload(
		context, liferayEnvironment,
	)

	if errors.IsNotFound(getError) {
		logger.V(1).Info(
			"Workload was not found; skipping replica enforcement",
			"workload", liferayEnvironment.Spec.WorkloadRef.Name,
		)

		liferayEnvironment.Status.EffectiveReplicas = nil

		setCondition(
			conditionReplicasCountValid, liferayEnvironment,
			fmt.Sprintf(
				"Workload %s %q does not exist.",
				workloadKind, liferayEnvironment.Spec.WorkloadRef.Name,
			),
			"WorkloadNotFound", metav1.ConditionUnknown,
		)

		return proceed(), nil
	}

	if getError != nil {
		return stepOutcome{}, getError
	}

	liveReplicas := getReplicas(workload)

	desiredReplicas := resolveDesiredReplicas(liferayEnvironment, liveReplicas)

	annotations := workload.GetAnnotations()

	markedScaledToZero := annotations[annotationScaledToZero] == "true"

	if markedScaledToZero && replicaCeiling > 0 && liferayEnvironment.Spec.Autoscaling != nil {
		desiredReplicas = max(desiredReplicas, liferayEnvironment.Spec.Autoscaling.MinReplicas)
	}

	effectiveReplicas := min(desiredReplicas, replicaCeiling)

	markScaledToZero := liferayEnvironment.Spec.Autoscaling != nil && replicaCeiling == 0 &&
		(markedScaledToZero || liveReplicas == nil || *liveReplicas > 0)

	replicasChanged := liveReplicas == nil || *liveReplicas != effectiveReplicas

	if ceilingChanged || replicasChanged {
		if error := liferayEnvironmentReconciler.Status().Update(context, liferayEnvironment); error != nil {
			return stepOutcome{}, error
		}
	}

	if replicasChanged || markScaledToZero != markedScaledToZero {
		setReplicas(&effectiveReplicas, workload)

		if markScaledToZero {
			if annotations == nil {
				annotations = map[string]string{}
			}

			annotations[annotationScaledToZero] = "true"
		} else {
			delete(annotations, annotationScaledToZero)
		}

		workload.SetAnnotations(annotations)

		if error := liferayEnvironmentReconciler.Update(
			context, workload, client.FieldOwner(fieldOwner),
		); error != nil {
			logger.Error(
				error, "Unable to enforce the licensed replica ceiling",
				"effectiveReplicas", effectiveReplicas,
				"workload", workload.GetName(),
			)

			liferayEnvironment.Status.EffectiveReplicas = liveReplicas

			setCondition(
				conditionReplicasCountValid, liferayEnvironment,
				fmt.Sprintf(
					"Unable to scale %s %q to %d replicas: %s.",
					workloadKind, workload.GetName(), effectiveReplicas, error,
				),
				"WorkloadUpdateRejected", metav1.ConditionFalse,
			)

			return stopAfter(liferayEnvironmentReconciler.RetryInitialDelay), nil
		}

		logger.Info(
			"Licensed replica ceiling was enforced",
			"desiredReplicas", desiredReplicas,
			"effectiveReplicas", effectiveReplicas,
			"replicaCeiling", replicaCeiling,
			"workload", workload.GetName(),
		)
	}

	liferayEnvironment.Status.EffectiveReplicas = &effectiveReplicas

	if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
		context, liferayEnvironment, replicaCeiling,
	); error != nil {
		logger.Error(
			error, "Unable to enforce the licensed autoscaling ceiling",
			"replicaCeiling", replicaCeiling,
			"workload", workload.GetName(),
		)

		setCondition(
			conditionReplicasCountValid, liferayEnvironment,
			fmt.Sprintf(
				"Unable to cap the autoscaler of %s %q to %d replicas: %s.",
				workloadKind, workload.GetName(), replicaCeiling, error,
			),
			"AutoscalerUpdateRejected", metav1.ConditionFalse,
		)

		return stopAfter(liferayEnvironmentReconciler.RetryInitialDelay), nil
	}

	requestedReplicas := desiredReplicas

	if liferayEnvironment.Spec.Autoscaling != nil {
		requestedReplicas = liferayEnvironment.Spec.Autoscaling.MaxReplicas
	}

	if requestedReplicas > replicaCeiling {
		setCondition(
			conditionReplicasCountValid, liferayEnvironment,
			fmt.Sprintf(
				"Requested %d replicas exceeds the replica ceiling of %d; capping to %d.",
				requestedReplicas, replicaCeiling, replicaCeiling,
			),
			"ExceedsLicensedMaximum", metav1.ConditionFalse,
		)

		return proceed(), nil
	}

	setCondition(
		conditionReplicasCountValid, liferayEnvironment, "", "WithinLicensedLimit",
		metav1.ConditionTrue,
	)

	return proceed(), nil
}

func resolveDesiredReplicas(
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
	workloadReplicas *int32,
) int32 {
	if liferayEnvironment.Spec.DesiredReplicas != nil {
		return *liferayEnvironment.Spec.DesiredReplicas
	}

	if workloadReplicas != nil {
		return *workloadReplicas
	}

	return 1
}
