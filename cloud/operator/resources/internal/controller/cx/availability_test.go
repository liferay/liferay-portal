package cx

import (
	"context"
	"strings"
	"testing"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

func TestCronJobAvailableCondition(t *testing.T) {
	cronJob := &batchv1.CronJob{
		ObjectMeta: metav1.ObjectMeta{Name: "able", Namespace: "liferay-cx", UID: types.UID("able-uid")},
		Spec:       batchv1.CronJobSpec{Schedule: "*/5 * * * *"},
	}

	testCases := map[string]struct {
		jobs        []batchv1.Job
		wantMessage string
		wantReason  string
		wantStatus  metav1.ConditionStatus
	}{
		"a CronJob whose last run completed": {
			jobs: []batchv1.Job{
				newCronJobRun(batchv1.JobFailed, cronJob, 1, "able-1"),
				newCronJobRun(batchv1.JobComplete, cronJob, 2, "able-2"),
			},
			wantMessage: `Job "able-2", completed.`,
			wantReason:  ReasonCronJobScheduled,
			wantStatus:  metav1.ConditionTrue,
		},
		"a CronJob whose last run failed": {
			jobs: []batchv1.Job{
				newCronJobRun(batchv1.JobComplete, cronJob, 1, "able-1"),
				newCronJobRun(batchv1.JobFailed, cronJob, 2, "able-2"),
			},
			wantMessage: `Job "able-2", failed: BackoffLimitExceeded`,
			wantReason:  ReasonJobFailed,
			wantStatus:  metav1.ConditionFalse,
		},
		"a CronJob that has not run": {
			jobs: []batchv1.Job{
				{ObjectMeta: metav1.ObjectMeta{Name: "baker-1", Namespace: "liferay-cx"}},
			},
			wantMessage: `has not run yet. Its schedule is "*/5 * * * *".`,
			wantReason:  ReasonCronJobScheduled,
			wantStatus:  metav1.ConditionTrue,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			assertCondition(
				cronJobAvailableCondition(cronJob, testCase.jobs), t, testCase.wantMessage, testCase.wantReason,
				testCase.wantStatus,
			)
		})
	}
}

func TestDeploymentAvailableCondition(t *testing.T) {
	testCases := map[string]struct {
		deployment  *appsv1.Deployment
		podProblem  string
		wantMessage string
		wantReason  string
		wantStatus  metav1.ConditionStatus
	}{
		"a Deployment past its progress deadline": {
			deployment: newAvailabilityDeployment(
				0, 1, 1,
				appsv1.DeploymentCondition{
					Message: "ReplicaSet \"able-1\" has timed out progressing.", Reason: "ProgressDeadlineExceeded",
					Type: appsv1.DeploymentProgressing,
				},
			),
			wantMessage: `has 0 of 1 replicas available. ReplicaSet "able-1" has timed out progressing.`,
			wantReason:  ReasonWorkloadUnavailable,
			wantStatus:  metav1.ConditionFalse,
		},
		"a Deployment rolling out": {
			deployment:  newAvailabilityDeployment(1, 2, 1),
			wantMessage: `Deployment "able" has 1 of 2 replicas available.`,
			wantReason:  ReasonWorkloadProgressing,
			wantStatus:  metav1.ConditionFalse,
		},
		"a Deployment whose controller has not seen its latest spec": {
			deployment: func() *appsv1.Deployment {
				deployment := newAvailabilityDeployment(1, 1, 1)

				deployment.Generation = 2

				return deployment
			}(),
			wantReason: ReasonWorkloadProgressing,
			wantStatus: metav1.ConditionFalse,
		},
		"a Deployment whose pod fails": {
			deployment:  newAvailabilityDeployment(0, 1, 1),
			podProblem:  `Container "able" of pod "able-1" is waiting: CrashLoopBackOff. It last exited with code 1.`,
			wantMessage: `has 0 of 1 replicas available. Container "able" of pod "able-1" is waiting: CrashLoopBackOff.`,
			wantReason:  ReasonWorkloadUnavailable,
			wantStatus:  metav1.ConditionFalse,
		},
		"an available Deployment": {
			deployment:  newAvailabilityDeployment(2, 2, 2),
			wantMessage: `Deployment "able" has 2 of 2 replicas available.`,
			wantReason:  ReasonWorkloadAvailable,
			wantStatus:  metav1.ConditionTrue,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			assertCondition(
				deploymentAvailableCondition(testCase.deployment, testCase.podProblem), t, testCase.wantMessage,
				testCase.wantReason, testCase.wantStatus,
			)
		})
	}
}

func TestJobAvailableCondition(t *testing.T) {
	testCases := map[string]struct {
		conditionType batchv1.JobConditionType
		podProblem    string
		wantMessage   string
		wantReason    string
		wantStatus    metav1.ConditionStatus
	}{
		"a completed Job": {
			conditionType: batchv1.JobComplete,
			wantMessage:   `Job "able" completed at`,
			wantReason:    ReasonJobComplete,
			wantStatus:    metav1.ConditionTrue,
		},
		"a failed Job": {
			conditionType: batchv1.JobFailed,
			podProblem:    `Container "able" of pod "able-x" exited with code 3 (Error).`,
			wantMessage:   `Job "able" failed: BackoffLimitExceeded Container "able" of pod "able-x" exited with code 3 (Error).`,
			wantReason:    ReasonJobFailed,
			wantStatus:    metav1.ConditionFalse,
		},
		"a running Job": {
			wantMessage: `Job "able" is running.`,
			wantReason:  ReasonJobRunning,
			wantStatus:  metav1.ConditionFalse,
		},
		"a running Job whose image cannot be pulled": {
			podProblem:  `Container "able" of pod "able-x" is waiting: ImagePullBackOff: Back-off pulling image.`,
			wantMessage: `Job "able" is running. Container "able" of pod "able-x" is waiting: ImagePullBackOff`,
			wantReason:  ReasonWorkloadUnavailable,
			wantStatus:  metav1.ConditionFalse,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			job := &batchv1.Job{ObjectMeta: metav1.ObjectMeta{Name: "able", Namespace: "liferay-cx"}}

			if testCase.conditionType != "" {
				job.Status.Conditions = []batchv1.JobCondition{{
					Message: "BackoffLimitExceeded", Status: corev1.ConditionTrue, Type: testCase.conditionType,
				}}
			}

			assertCondition(
				jobAvailableCondition(job, testCase.podProblem), t, testCase.wantMessage, testCase.wantReason,
				testCase.wantStatus,
			)
		})
	}
}

func TestPodProblem(t *testing.T) {
	testCases := map[string]struct {
		pods []corev1.Pod
		want string
	}{
		"a container that cannot be created": {
			pods: []corev1.Pod{newPod(corev1.ContainerStatus{
				Name: "able",
				State: corev1.ContainerState{Waiting: &corev1.ContainerStateWaiting{
					Message: "no command specified", Reason: "CreateContainerError",
				}},
			}, "able-1")},
			want: `Container "able" of pod "able-1" is waiting: CreateContainerError: no command specified.`,
		},
		"a container that exited": {
			pods: []corev1.Pod{newPod(corev1.ContainerStatus{
				Name:  "able",
				State: corev1.ContainerState{Terminated: &corev1.ContainerStateTerminated{ExitCode: 3, Reason: "Error"}},
			}, "able-1")},
			want: `Container "able" of pod "able-1" exited with code 3 (Error).`,
		},
		"a crash looping container": {
			pods: []corev1.Pod{newPod(corev1.ContainerStatus{
				LastTerminationState: corev1.ContainerState{
					Terminated: &corev1.ContainerStateTerminated{ExitCode: 1},
				},
				Name:  "able",
				State: corev1.ContainerState{Waiting: &corev1.ContainerStateWaiting{Reason: "CrashLoopBackOff"}},
			}, "able-1")},
			want: `Container "able" of pod "able-1" is waiting: CrashLoopBackOff. It last exited with code 1.`,
		},
		"a container whose image cannot be inspected": {
			pods: []corev1.Pod{newPod(corev1.ContainerStatus{
				Name:  "able",
				State: corev1.ContainerState{Waiting: &corev1.ContainerStateWaiting{Reason: "ImageInspectError"}},
			}, "able-1")},
			want: `Container "able" of pod "able-1" is waiting: ImageInspectError.`,
		},
		"a starting container": {
			pods: []corev1.Pod{newPod(corev1.ContainerStatus{
				Name:  "able",
				State: corev1.ContainerState{Waiting: &corev1.ContainerStateWaiting{Reason: "ContainerCreating"}},
			}, "able-1")},
		},
		"pods in name order": {
			pods: []corev1.Pod{
				newPod(corev1.ContainerStatus{
					Name:  "able",
					State: corev1.ContainerState{Waiting: &corev1.ContainerStateWaiting{Reason: "ErrImagePull"}},
				}, "able-2"),
				newPod(corev1.ContainerStatus{
					Name:  "able",
					State: corev1.ContainerState{Waiting: &corev1.ContainerStateWaiting{Reason: "ImagePullBackOff"}},
				}, "able-1"),
			},
			want: `Container "able" of pod "able-1" is waiting: ImagePullBackOff.`,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			if got := podProblem(testCase.pods); got != testCase.want {
				t.Errorf("podProblem() = %q, want %q", got, testCase.want)
			}
		})
	}
}

func TestReconcileReportsWorkloadAvailability(t *testing.T) {
	testCases := map[string]struct {
		deployment      *appsv1.Deployment
		pods            []client.Object
		wantPhase       string
		wantReadyReason string
	}{
		"a Deployment rolling out": {
			deployment:      newAvailabilityDeployment(0, 1, 0),
			wantPhase:       cxv1alpha1.PhasePending,
			wantReadyReason: ReasonWorkloadProgressing,
		},
		"a Deployment whose pod cannot start": {
			deployment: newAvailabilityDeployment(0, 1, 1),
			pods: []client.Object{
				func() *corev1.Pod {
					pod := newPod(corev1.ContainerStatus{
						Name: "able",
						State: corev1.ContainerState{Waiting: &corev1.ContainerStateWaiting{
							Message: "no command specified", Reason: "CreateContainerError",
						}},
					}, "able-1")

					return &pod
				}(),
			},
			wantPhase:       cxv1alpha1.PhaseDegraded,
			wantReadyReason: ReasonWorkloadUnavailable,
		},
		"an available Deployment": {
			deployment:      newAvailabilityDeployment(1, 1, 1),
			wantPhase:       cxv1alpha1.PhaseReady,
			wantReadyReason: ReasonReady,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

			clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{
				Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able",
			}

			clientExtensionReconciler := newClientExtensionReconciler(
				nil, t,
				append(
					testCase.pods, clientExtension, testCase.deployment,
					newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace(),
					newPolicyConfigMap("liferay-cx"),
				)...,
			)

			if phase, _ := reconcileClientExtension(
				clientExtension, clientExtensionReconciler, t,
			); phase != testCase.wantPhase {
				t.Errorf("phase = %q, want %q", phase, testCase.wantPhase)
			}

			if ready := getCondition(
				clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionReady, t,
			); ready.Reason != testCase.wantReadyReason {
				t.Errorf("Ready reason = %q, want %q", ready.Reason, testCase.wantReadyReason)
			}

			clientExtension = getClientExtension(clientExtension, clientExtensionReconciler, t)

			if image := clientExtension.Status.Image; image != "liferay/able:1.0.0" {
				t.Errorf("image = %q, want %q", image, "liferay/able:1.0.0")
			}
		})
	}
}

func TestRequestsForJobMatchesItsCronJob(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

	clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{Kind: cxv1alpha1.WorkloadKindCronJob, Name: "able"}

	clientExtensionReconciler := newClientExtensionReconciler(nil, t, clientExtension)

	cronJob := &batchv1.CronJob{
		ObjectMeta: metav1.ObjectMeta{Name: "able", Namespace: "liferay-cx", UID: types.UID("able-uid")},
	}

	job := newCronJobRun(batchv1.JobComplete, cronJob, 1, "able-1")

	requests := clientExtensionReconciler.requestsForJob(context.Background(), &job)

	if (len(requests) != 1) || (requests[0].Name != "able") {
		t.Errorf("requests = %v, want the ClientExtension of CronJob %q", requests, cronJob.Name)
	}

	job.OwnerReferences = nil

	if requests := clientExtensionReconciler.requestsForJob(context.Background(), &job); len(requests) != 0 {
		t.Errorf("requests = %v, want none for a Job that no CronJob owns", requests)
	}
}

func TestWorkloadSummary(t *testing.T) {
	testCases := map[string]struct {
		want              string
		workload          client.Object
		workloadAvailable metav1.Condition
	}{
		"a CronJob that has not run yet": {
			want: "Scheduled", workload: &batchv1.CronJob{},
			workloadAvailable: metav1.Condition{Reason: ReasonCronJobScheduled},
		},
		"a CronJob whose last run failed": {
			want: "Failed", workload: &batchv1.CronJob{}, workloadAvailable: metav1.Condition{Reason: ReasonJobFailed},
		},
		"a CronJob whose last run succeeded": {
			want: "Completed",
			workload: &batchv1.CronJob{
				Status: batchv1.CronJobStatus{LastSuccessfulTime: &metav1.Time{Time: time.Now()}},
			},
			workloadAvailable: metav1.Condition{Reason: ReasonCronJobScheduled},
		},
		"a CronJob with a running Job": {
			want: "Running",
			workload: &batchv1.CronJob{
				Status: batchv1.CronJobStatus{Active: []corev1.ObjectReference{{Name: "able-1"}}},
			},
			workloadAvailable: metav1.Condition{Reason: ReasonCronJobScheduled},
		},
		"a Deployment rolling out": {
			want: "1/2", workload: newAvailabilityDeployment(1, 2, 1),
		},
		"a Job that completed": {
			want: "Completed", workload: &batchv1.Job{}, workloadAvailable: metav1.Condition{Reason: ReasonJobComplete},
		},
		"a Job that failed": {
			want: "Failed", workload: &batchv1.Job{}, workloadAvailable: metav1.Condition{Reason: ReasonJobFailed},
		},
		"a Job that is running": {
			want: "Running", workload: &batchv1.Job{}, workloadAvailable: metav1.Condition{Reason: ReasonJobRunning},
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			if got := workloadSummary(testCase.workload, testCase.workloadAvailable); got != testCase.want {
				t.Errorf("workloadSummary() = %q, want %q", got, testCase.want)
			}
		})
	}
}

func assertCondition(
	condition metav1.Condition,
	t *testing.T,
	wantMessage string,
	wantReason string,
	wantStatus metav1.ConditionStatus,
) {
	t.Helper()

	if !strings.Contains(condition.Message, wantMessage) || (condition.Reason != wantReason) ||
		(condition.Status != wantStatus) {

		t.Errorf(
			"condition = %s / %s / %q, want %s / %s / containing %q", condition.Status, condition.Reason,
			condition.Message, wantStatus, wantReason, wantMessage,
		)
	}
}

func newAvailabilityDeployment(
	availableReplicas int32,
	replicas int32,
	updatedReplicas int32,
	conditions ...appsv1.DeploymentCondition,
) *appsv1.Deployment {
	podTemplate := newInitializedPodTemplate()

	podTemplate.Labels = map[string]string{"app.kubernetes.io/name": "able"}
	podTemplate.Spec.Containers[0].Image = "liferay/able:1.0.0"

	return &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: "able", Namespace: "liferay-cx"},
		Spec: appsv1.DeploymentSpec{
			Replicas: &replicas,
			Selector: &metav1.LabelSelector{MatchLabels: map[string]string{"app.kubernetes.io/name": "able"}},
			Template: podTemplate,
		},
		Status: appsv1.DeploymentStatus{
			AvailableReplicas: availableReplicas,
			Conditions:        conditions,
			UpdatedReplicas:   updatedReplicas,
		},
	}
}

func newCronJobRun(
	conditionType batchv1.JobConditionType,
	cronJob *batchv1.CronJob,
	minutes int,
	name string,
) batchv1.Job {
	controller := true

	return batchv1.Job{
		ObjectMeta: metav1.ObjectMeta{
			CreationTimestamp: metav1.NewTime(time.Date(2026, 1, 1, 0, minutes, 0, 0, time.UTC)),
			Name:              name,
			Namespace:         cronJob.Namespace,
			OwnerReferences: []metav1.OwnerReference{{
				APIVersion: "batch/v1", Controller: &controller, Kind: "CronJob", Name: cronJob.Name,
				UID: cronJob.UID,
			}},
		},
		Status: batchv1.JobStatus{
			Conditions: []batchv1.JobCondition{{
				Message: "BackoffLimitExceeded", Status: corev1.ConditionTrue, Type: conditionType,
			}},
		},
	}
}

func newPod(containerStatus corev1.ContainerStatus, name string) corev1.Pod {
	return corev1.Pod{
		ObjectMeta: metav1.ObjectMeta{
			Labels: map[string]string{"app.kubernetes.io/name": "able"}, Name: name, Namespace: "liferay-cx",
		},
		Status: corev1.PodStatus{ContainerStatuses: []corev1.ContainerStatus{containerStatus}},
	}
}
