package cx

import (
	"context"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"maps"
	"slices"
	"strconv"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	handler "sigs.k8s.io/controller-runtime/pkg/handler"
	reconcile "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const AnnotationConfigDigest = "cx.liferay.com/config-digest"

const (
	MountPathDxpMetadata     = "/etc/liferay/lxc/dxp-metadata"
	MountPathExtInitMetadata = "/etc/liferay/lxc/ext-init-metadata"
)

const workloadGracePeriod = 2 * time.Minute

func configDigest(configMaps ...*corev1.ConfigMap) string {
	hash := sha256.New()

	write := func(value string) {
		hash.Write([]byte(strconv.Itoa(len(value)) + ":" + value))
	}

	for _, configMap := range configMaps {
		write(strconv.Itoa(len(configMap.BinaryData) + len(configMap.Data)))

		for _, key := range slices.Sorted(maps.Keys(configMap.Data)) {
			write(key)
			write(configMap.Data[key])
		}

		for _, key := range slices.Sorted(maps.Keys(configMap.BinaryData)) {
			write(key)
			write(string(configMap.BinaryData[key]))
		}
	}

	return hex.EncodeToString(hash.Sum(nil))
}

func podTemplateOf(object client.Object) *corev1.PodTemplateSpec {
	if cronJob, ok := object.(*batchv1.CronJob); ok {
		return &cronJob.Spec.JobTemplate.Spec.Template
	}

	if deployment, ok := object.(*appsv1.Deployment); ok {
		return &deployment.Spec.Template
	}

	if job, ok := object.(*batchv1.Job); ok {
		return &job.Spec.Template
	}

	return nil
}

func (clientExtensionReconciler *ClientExtensionReconciler) putConfigDigest(
	clientExtension *cxv1alpha1.ClientExtension,
	context context.Context,
	digest string,
	workload client.Object,
) error {
	deployment, ok := workload.(*appsv1.Deployment)

	if !ok {
		return nil
	}

	previousDigest := deployment.Spec.Template.Annotations[AnnotationConfigDigest]

	if digest == previousDigest {
		return nil
	}

	patch := client.MergeFrom(deployment.DeepCopy())

	if deployment.Spec.Template.Annotations == nil {
		deployment.Spec.Template.Annotations = map[string]string{}
	}

	deployment.Spec.Template.Annotations[AnnotationConfigDigest] = digest

	if error := clientExtensionReconciler.Patch(
		context, deployment, patch,
	); error != nil {
		return error
	}

	message := fmt.Sprintf(
		"Updated Deployment %q because DXP's metadata changed.", deployment.Name,
	)

	if previousDigest == "" {
		message = fmt.Sprintf(
			"Updated Deployment %q so that its pods restart when DXP's metadata changes.",
			deployment.Name,
		)
	}

	clientExtensionReconciler.Recorder.Event(
		clientExtension, corev1.EventTypeNormal, "WorkloadUpdated", message,
	)

	return nil
}

func (clientExtensionReconciler *ClientExtensionReconciler) requestsForJob(
	context context.Context,
	object client.Object,
) []reconcile.Request {
	requests := clientExtensionReconciler.requestsForWorkload(cxv1alpha1.WorkloadKindJob)(context, object)

	if ownerReference := metav1.GetControllerOf(object); (ownerReference != nil) &&
		(ownerReference.Kind == string(cxv1alpha1.WorkloadKindCronJob)) {

		cronJob := &metav1.PartialObjectMetadata{
			ObjectMeta: metav1.ObjectMeta{Name: ownerReference.Name, Namespace: object.GetNamespace()},
		}

		requests = append(
			requests,
			clientExtensionReconciler.requestsForWorkload(cxv1alpha1.WorkloadKindCronJob)(context, cronJob)...,
		)
	}

	return requests
}

func (clientExtensionReconciler *ClientExtensionReconciler) requestsForWorkload(
	workloadKind cxv1alpha1.WorkloadKind,
) handler.MapFunc {
	return func(context context.Context, object client.Object) []reconcile.Request {
		var clientExtensionList cxv1alpha1.ClientExtensionList

		if error := clientExtensionReconciler.List(
			context, &clientExtensionList, client.InNamespace(object.GetNamespace()),
		); error != nil {
			controllerruntime.LoggerFrom(context).Error(
				error, "Unable to list client extensions", "kind", workloadKind, "workload",
				client.ObjectKeyFromObject(object),
			)

			return nil
		}

		var requests []reconcile.Request

		for index := range clientExtensionList.Items {
			workloadRef := clientExtensionList.Items[index].Spec.WorkloadRef

			if (workloadRef == nil) || (workloadKind != workloadRef.Kind) || (object.GetName() != workloadRef.Name) {
				continue
			}

			requests = append(requests, reconcile.Request{
				NamespacedName: client.ObjectKeyFromObject(&clientExtensionList.Items[index]),
			})
		}

		return requests
	}
}

func validatePodTemplate(
	configMapName string,
	environmentVariableName string,
	mountPath string,
	podTemplate *corev1.PodTemplateSpec,
) []string {
	var workloadIssues []string

	for _, volume := range podTemplate.Spec.Volumes {
		if (volume.ConfigMap == nil) || (configMapName != volume.ConfigMap.Name) {
			continue
		}

		volumeIssues := validateVolume(environmentVariableName, mountPath, podTemplate, volume)

		if len(volumeIssues) == 0 {
			return nil
		}

		workloadIssues = append(workloadIssues, volumeIssues...)
	}

	if len(workloadIssues) == 0 {
		return []string{fmt.Sprintf("No volume holds ConfigMap %q.", configMapName)}
	}

	return workloadIssues
}

func validateVolume(
	environmentVariableName string,
	mountPath string,
	podTemplate *corev1.PodTemplateSpec,
	volume corev1.Volume,
) []string {
	if len(volume.ConfigMap.Items) > 0 {
		return []string{
			fmt.Sprintf("Volume %q holds only some keys of ConfigMap %q.", volume.Name, volume.ConfigMap.Name),
		}
	}

	if (volume.ConfigMap.Optional != nil) && *volume.ConfigMap.Optional {
		return []string{
			fmt.Sprintf(
				"Volume %q marks ConfigMap %q optional, so the pod can start without it.", volume.Name,
				volume.ConfigMap.Name,
			),
		}
	}

	mounted := false
	subPathContainerName := ""

	for _, container := range podTemplate.Spec.Containers {
		volumeMountIndex := slices.IndexFunc(
			container.VolumeMounts,
			func(volumeMount corev1.VolumeMount) bool {
				return (mountPath == volumeMount.MountPath) && (volume.Name == volumeMount.Name)
			},
		)

		if volumeMountIndex < 0 {
			continue
		}

		if container.VolumeMounts[volumeMountIndex].SubPath != "" {
			subPathContainerName = container.Name

			continue
		}

		mounted = true

		if slices.ContainsFunc(
			container.Env,
			func(environmentVariable corev1.EnvVar) bool {
				return (environmentVariable.Name == environmentVariableName) && (environmentVariable.Value == mountPath)
			},
		) {

			return nil
		}
	}

	if !mounted && (subPathContainerName != "") {
		return []string{
			fmt.Sprintf(
				"Container %q mounts a single key of volume %q at %q, not the whole volume.", subPathContainerName,
				volume.Name, mountPath,
			),
		}
	}

	if !mounted {
		return []string{fmt.Sprintf("No container mounts volume %q at %q.", volume.Name, mountPath)}
	}

	return []string{
		fmt.Sprintf(
			"No container mounts volume %q and sets %s to %q.", volume.Name, environmentVariableName, mountPath,
		),
	}
}

func (clientExtensionReconciler *ClientExtensionReconciler) workloadCondition(
	clientExtension *cxv1alpha1.ClientExtension,
	configMapsWithDigest []*corev1.ConfigMap,
	context context.Context,
) (metav1.Condition, *metav1.Condition, string, string, []string, error) {
	workloadRef := clientExtension.Spec.WorkloadRef

	if workloadRef == nil {
		configurationOnly := newCondition(
			metav1.ConditionTrue, "The client extension is configuration only.",
			ReasonConfigurationOnly,
		)

		return configurationOnly, &configurationOnly, "", "", nil, nil
	}

	workload, error := workloadRef.NewObject()

	if error != nil {
		return metav1.Condition{}, nil, "", "", nil, error
	}

	error = clientExtensionReconciler.APIReader.Get(
		context, types.NamespacedName{
			Name: workloadRef.Name, Namespace: clientExtension.Namespace,
		}, workload,
	)

	if apierrors.IsNotFound(error) {
		return newCondition(
			metav1.ConditionFalse,
			fmt.Sprintf(
				"%s %q does not exist in namespace %q.", workloadRef.Kind,
				workloadRef.Name, clientExtension.Namespace,
			),
			ReasonWorkloadNotFound,
		), nil, "", "", nil, nil
	}

	if error != nil {
		return metav1.Condition{}, nil, "", "", nil, error
	}

	workloadAvailable, error := clientExtensionReconciler.workloadAvailableCondition(context, workload)

	if error != nil {
		return metav1.Condition{}, nil, "", "", nil, error
	}

	image := containerImage(workload)

	summary := workloadSummary(workload, workloadAvailable)

	podTemplate := podTemplateOf(workload)

	workloadIssues := validatePodTemplate(
		dxpMetadataName(clientExtension.Spec.VirtualInstanceID), "LIFERAY_ROUTES_DXP",
		MountPathDxpMetadata, podTemplate,
	)

	if len(extInitIdentifiers(clientExtension)) > 0 {
		workloadIssues = append(
			workloadIssues,
			validatePodTemplate(
				extInitName(clientExtension), "LIFERAY_ROUTES_CLIENT_EXTENSION",
				MountPathExtInitMetadata, podTemplate,
			)...,
		)
	}

	if len(workloadIssues) > 0 {
		return newCondition(
			metav1.ConditionFalse,
			fmt.Sprintf(
				"%s %q does not mount DXP's metadata; see status.workloadIssues.",
				workloadRef.Kind, workloadRef.Name,
			),
			ReasonWorkloadMisconfigured,
		), &workloadAvailable, image, summary, workloadIssues, nil
	}

	if configMapsWithDigest != nil {
		if error := clientExtensionReconciler.putConfigDigest(
			clientExtension, context, configDigest(configMapsWithDigest...), workload,
		); error != nil {
			return metav1.Condition{}, nil, "", "", nil, error
		}
	}

	return newCondition(
		metav1.ConditionTrue,
		fmt.Sprintf("%s %q mounts DXP's metadata.", workloadRef.Kind, workloadRef.Name), ReasonWorkloadAccepted,
	), &workloadAvailable, image, summary, nil, nil
}

func workloadGraceRemaining(status *cxv1alpha1.ClientExtensionStatus) time.Duration {
	workloadAccepted := meta.FindStatusCondition(status.Conditions, cxv1alpha1.ConditionWorkloadAccepted)

	if (workloadAccepted == nil) || (workloadAccepted.Reason != ReasonWorkloadNotFound) {
		return 0
	}

	return max(time.Until(workloadAccepted.LastTransitionTime.Add(workloadGracePeriod)), 0)
}
