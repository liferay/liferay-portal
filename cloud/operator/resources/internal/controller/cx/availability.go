package cx

import (
	"cmp"
	"context"
	"fmt"
	"slices"
	"strings"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	labels "k8s.io/apimachinery/pkg/labels"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

const workloadStatusRequeueInterval = 30 * time.Second

func availabilityRequeueInterval(status *cxv1alpha1.ClientExtensionStatus) time.Duration {
	workloadAvailable := meta.FindStatusCondition(status.Conditions, cxv1alpha1.ConditionWorkloadAvailable)

	if (workloadAvailable != nil) &&
		((workloadAvailable.Reason == ReasonWorkloadProgressing) || (workloadAvailable.Reason == ReasonWorkloadUnavailable)) {

		return workloadStatusRequeueInterval
	}

	return 0
}

func containerImage(workload client.Object) string {
	podTemplate := podTemplateOf(workload)

	if (podTemplate == nil) || (len(podTemplate.Spec.Containers) == 0) {
		return ""
	}

	return podTemplate.Spec.Containers[0].Image
}

func cronJobAvailableCondition(cronJob *batchv1.CronJob, jobs []batchv1.Job) metav1.Condition {
	var lastJob *batchv1.Job

	for index := range jobs {
		if !metav1.IsControlledBy(&jobs[index], cronJob) {
			continue
		}

		if (lastJob == nil) || lastJob.CreationTimestamp.Before(&jobs[index].CreationTimestamp) {
			lastJob = &jobs[index]
		}
	}

	if lastJob == nil {
		return newCondition(
			metav1.ConditionTrue,
			fmt.Sprintf("CronJob %q has not run yet. Its schedule is %q.", cronJob.Name, cronJob.Spec.Schedule),
			ReasonCronJobScheduled,
		)
	}

	if failed := jobCondition(batchv1.JobFailed, lastJob); failed != nil {
		return newCondition(
			metav1.ConditionFalse,
			fmt.Sprintf("The last run of CronJob %q, Job %q, failed: %s", cronJob.Name, lastJob.Name, failed.Message),
			ReasonJobFailed,
		)
	}

	state := "is running"

	if jobCondition(batchv1.JobComplete, lastJob) != nil {
		state = "completed"
	}

	return newCondition(
		metav1.ConditionTrue,
		fmt.Sprintf("The last run of CronJob %q, Job %q, %s.", cronJob.Name, lastJob.Name, state),
		ReasonCronJobScheduled,
	)
}

func deploymentAvailableCondition(deployment *appsv1.Deployment, podProblem string) metav1.Condition {
	replicas := int32(1)

	if deployment.Spec.Replicas != nil {
		replicas = *deployment.Spec.Replicas
	}

	message := fmt.Sprintf(
		"Deployment %q has %d of %d replicas available.", deployment.Name, deployment.Status.AvailableReplicas,
		replicas,
	)

	if (deployment.Status.AvailableReplicas >= replicas) &&
		(deployment.Status.ObservedGeneration >= deployment.Generation) && (deployment.Status.UpdatedReplicas >= replicas) {

		return newCondition(metav1.ConditionTrue, message, ReasonWorkloadAvailable)
	}

	if podProblem != "" {
		return newCondition(metav1.ConditionFalse, message+" "+podProblem, ReasonWorkloadUnavailable)
	}

	for _, condition := range deployment.Status.Conditions {
		if (condition.Reason == "ProgressDeadlineExceeded") && (condition.Type == appsv1.DeploymentProgressing) {
			return newCondition(metav1.ConditionFalse, message+" "+condition.Message, ReasonWorkloadUnavailable)
		}
	}

	return newCondition(metav1.ConditionFalse, message, ReasonWorkloadProgressing)
}

func isFailedWaitingReason(reason string) bool {
	return strings.HasPrefix(reason, "Err") || strings.HasSuffix(reason, "BackOff") ||
		strings.HasSuffix(reason, "Error")
}

func jobAvailableCondition(job *batchv1.Job, podProblem string) metav1.Condition {
	if complete := jobCondition(batchv1.JobComplete, job); complete != nil {
		lastTransitionTime := complete.LastTransitionTime.UTC()

		return newCondition(
			metav1.ConditionTrue,
			fmt.Sprintf("Job %q completed at %s.", job.Name, lastTransitionTime.Format(time.RFC3339)),
			ReasonJobComplete,
		)
	}

	if failed := jobCondition(batchv1.JobFailed, job); failed != nil {
		message := fmt.Sprintf("Job %q failed: %s", job.Name, failed.Message)

		if podProblem != "" {
			message += " " + podProblem
		}

		return newCondition(metav1.ConditionFalse, message, ReasonJobFailed)
	}

	message := fmt.Sprintf("Job %q is running.", job.Name)

	if podProblem != "" {
		message += " " + podProblem
	}

	return newCondition(metav1.ConditionFalse, message, ReasonJobRunning)
}

func jobCondition(conditionType batchv1.JobConditionType, job *batchv1.Job) *batchv1.JobCondition {
	for index := range job.Status.Conditions {
		condition := &job.Status.Conditions[index]

		if (condition.Status == corev1.ConditionTrue) && (condition.Type == conditionType) {
			return condition
		}
	}

	return nil
}

func (clientExtensionReconciler *ClientExtensionReconciler) listPods(
	context context.Context,
	namespace string,
	selector *metav1.LabelSelector,
) ([]corev1.Pod, error) {
	if selector == nil {
		return nil, nil
	}

	labelSelector, error := metav1.LabelSelectorAsSelector(selector)

	if (error != nil) || labelSelector.Empty() {
		return nil, error
	}

	var podList corev1.PodList

	if error := clientExtensionReconciler.APIReader.List(
		context, &podList, client.InNamespace(namespace), client.MatchingLabelsSelector{Selector: labelSelector},
	); error != nil {
		return nil, error
	}

	return podList.Items, nil
}

func podProblem(pods []corev1.Pod) string {
	slices.SortFunc(pods, func(pod1 corev1.Pod, pod2 corev1.Pod) int {
		return cmp.Compare(pod1.Name, pod2.Name)
	})

	for _, pod := range pods {
		for _, containerStatus := range slices.Concat(pod.Status.InitContainerStatuses, pod.Status.ContainerStatuses) {
			if waiting := containerStatus.State.Waiting; (waiting != nil) &&
				isFailedWaitingReason(waiting.Reason) {

				problem := fmt.Sprintf(
					"Container %q of pod %q is waiting: %s.", containerStatus.Name, pod.Name, waiting.Reason,
				)

				if terminated := containerStatus.LastTerminationState.Terminated; terminated != nil {
					problem += fmt.Sprintf(" It last exited with code %d.", terminated.ExitCode)
				} else if waiting.Message != "" {
					problem = fmt.Sprintf(
						"Container %q of pod %q is waiting: %s: %s.", containerStatus.Name, pod.Name,
						waiting.Reason, waiting.Message,
					)
				}

				return problem
			}

			if terminated := containerStatus.State.Terminated; (terminated != nil) && (terminated.ExitCode != 0) {
				return fmt.Sprintf(
					"Container %q of pod %q exited with code %d (%s).", containerStatus.Name, pod.Name,
					terminated.ExitCode, terminated.Reason,
				)
			}
		}
	}

	return ""
}

func (clientExtensionReconciler *ClientExtensionReconciler) workloadAvailableCondition(
	context context.Context,
	workload client.Object,
) (metav1.Condition, error) {
	if cronJob, ok := workload.(*batchv1.CronJob); ok {
		var jobList batchv1.JobList

		if error := clientExtensionReconciler.APIReader.List(
			context, &jobList, client.InNamespace(cronJob.Namespace),
		); error != nil {
			return metav1.Condition{}, error
		}

		return cronJobAvailableCondition(cronJob, jobList.Items), nil
	}

	if deployment, ok := workload.(*appsv1.Deployment); ok {
		pods, error := clientExtensionReconciler.listPods(context, deployment.Namespace, deployment.Spec.Selector)

		if error != nil {
			return metav1.Condition{}, error
		}

		return deploymentAvailableCondition(deployment, podProblem(pods)), nil
	}

	if job, ok := workload.(*batchv1.Job); ok {
		selector := job.Spec.Selector

		if selector == nil {
			selector = metav1.SetAsLabelSelector(labels.Set{batchv1.JobNameLabel: job.Name})
		}

		pods, error := clientExtensionReconciler.listPods(context, job.Namespace, selector)

		if error != nil {
			return metav1.Condition{}, error
		}

		return jobAvailableCondition(job, podProblem(pods)), nil
	}

	return metav1.Condition{}, fmt.Errorf("unsupported workload %T", workload)
}

func workloadSummary(workload client.Object, workloadAvailable metav1.Condition) string {
	if cronJob, ok := workload.(*batchv1.CronJob); ok {
		if workloadAvailable.Reason == ReasonJobFailed {
			return "Failed"
		}

		if len(cronJob.Status.Active) > 0 {
			return "Running"
		}

		if cronJob.Status.LastSuccessfulTime != nil {
			return "Completed"
		}

		return "Scheduled"
	}

	if deployment, ok := workload.(*appsv1.Deployment); ok {
		replicas := int32(1)

		if deployment.Spec.Replicas != nil {
			replicas = *deployment.Spec.Replicas
		}

		return fmt.Sprintf("%d/%d", deployment.Status.AvailableReplicas, replicas)
	}

	if _, ok := workload.(*batchv1.Job); ok {
		if workloadAvailable.Reason == ReasonJobComplete {
			return "Completed"
		}

		if workloadAvailable.Reason == ReasonJobFailed {
			return "Failed"
		}

		return "Running"
	}

	return ""
}
