package cx

import (
	"cmp"
	"context"
	"fmt"
	"slices"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	labels "k8s.io/apimachinery/pkg/labels"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

const workloadStatusRequeueInterval = 30 * time.Second

var failedWaitingReasons = []string{
	"CrashLoopBackOff",
	"CreateContainerConfigError",
	"CreateContainerError",
	"ErrImagePull",
	"ImagePullBackOff",
	"InvalidImageName",
	"RunContainerError",
}

func availabilityRequeueInterval(status *cxv1alpha1.ClientExtensionStatus) time.Duration {
	for _, condition := range status.Conditions {
		if (condition.Type == cxv1alpha1.ConditionWorkloadAvailable) && (condition.Status != metav1.ConditionTrue) {
			return workloadStatusRequeueInterval
		}
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

	if failed := jobCondition(lastJob, batchv1.JobFailed); failed != nil {
		return newCondition(
			metav1.ConditionFalse,
			fmt.Sprintf("The last run of CronJob %q, Job %q, failed: %s", cronJob.Name, lastJob.Name, failed.Message),
			ReasonJobFailed,
		)
	}

	state := "is running"

	if jobCondition(lastJob, batchv1.JobComplete) != nil {
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

	if (deployment.Status.ObservedGeneration >= deployment.Generation) &&
		(deployment.Status.UpdatedReplicas >= replicas) && (deployment.Status.AvailableReplicas >= replicas) {

		return newCondition(metav1.ConditionTrue, message, ReasonWorkloadAvailable)
	}

	if podProblem != "" {
		return newCondition(metav1.ConditionFalse, message+" "+podProblem, ReasonWorkloadUnavailable)
	}

	for _, condition := range deployment.Status.Conditions {
		if (condition.Type == appsv1.DeploymentProgressing) && (condition.Reason == "ProgressDeadlineExceeded") {
			return newCondition(metav1.ConditionFalse, message+" "+condition.Message, ReasonWorkloadUnavailable)
		}
	}

	return newCondition(metav1.ConditionFalse, message, ReasonWorkloadProgressing)
}

func jobAvailableCondition(job *batchv1.Job, podProblem string) metav1.Condition {
	if complete := jobCondition(job, batchv1.JobComplete); complete != nil {
		return newCondition(
			metav1.ConditionTrue,
			fmt.Sprintf("Job %q completed at %s.", job.Name, complete.LastTransitionTime.UTC().Format(time.RFC3339)),
			ReasonJobComplete,
		)
	}

	if failed := jobCondition(job, batchv1.JobFailed); failed != nil {
		message := fmt.Sprintf("Job %q failed: %s", job.Name, failed.Message)

		if podProblem != "" {
			message += " " + podProblem
		}

		return newCondition(metav1.ConditionFalse, message, ReasonJobFailed)
	}

	if podProblem != "" {
		return newCondition(
			metav1.ConditionFalse, fmt.Sprintf("Job %q is running. %s", job.Name, podProblem),
			ReasonWorkloadUnavailable,
		)
	}

	return newCondition(metav1.ConditionFalse, fmt.Sprintf("Job %q is running.", job.Name), ReasonJobRunning)
}

func jobCondition(job *batchv1.Job, conditionType batchv1.JobConditionType) *batchv1.JobCondition {
	for index := range job.Status.Conditions {
		condition := &job.Status.Conditions[index]

		if (condition.Type == conditionType) && (condition.Status == corev1.ConditionTrue) {
			return condition
		}
	}

	return nil
}

func podProblem(pods []corev1.Pod) string {
	slices.SortFunc(pods, func(pod1 corev1.Pod, pod2 corev1.Pod) int {
		return cmp.Compare(pod1.Name, pod2.Name)
	})

	for _, pod := range pods {
		for _, containerStatus := range slices.Concat(pod.Status.InitContainerStatuses, pod.Status.ContainerStatuses) {
			if waiting := containerStatus.State.Waiting; (waiting != nil) &&
				slices.Contains(failedWaitingReasons, waiting.Reason) {

				problem := fmt.Sprintf(
					"Container %q of pod %q is waiting: %s.", containerStatus.Name, pod.Name, waiting.Reason,
				)

				if terminated := containerStatus.LastTerminationState.Terminated; terminated != nil {
					problem += fmt.Sprintf(" It last exited with code %d.", terminated.ExitCode)
				} else if waiting.Message != "" {
					problem = fmt.Sprintf(
						"Container %q of pod %q is waiting: %s: %s", containerStatus.Name, pod.Name,
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

func (clientExtensionReconciler *ClientExtensionReconciler) workloadAvailableCondition(
	context context.Context,
	workload client.Object,
) (metav1.Condition, error) {
	switch workload := workload.(type) {
	case *appsv1.Deployment:
		pods, error := clientExtensionReconciler.listPods(context, workload.Namespace, workload.Spec.Selector)

		if error != nil {
			return metav1.Condition{}, error
		}

		return deploymentAvailableCondition(workload, podProblem(pods)), nil
	case *batchv1.CronJob:
		var jobList batchv1.JobList

		if error := clientExtensionReconciler.APIReader.List(
			context, &jobList, client.InNamespace(workload.Namespace),
		); error != nil {
			return metav1.Condition{}, error
		}

		return cronJobAvailableCondition(workload, jobList.Items), nil
	case *batchv1.Job:
		selector := workload.Spec.Selector

		if selector == nil {
			selector = metav1.SetAsLabelSelector(labels.Set{batchv1.JobNameLabel: workload.Name})
		}

		pods, error := clientExtensionReconciler.listPods(context, workload.Namespace, selector)

		if error != nil {
			return metav1.Condition{}, error
		}

		return jobAvailableCondition(workload, podProblem(pods)), nil
	}

	return metav1.Condition{}, fmt.Errorf("unsupported workload %T", workload)
}
