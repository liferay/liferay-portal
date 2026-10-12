package cx

import (
	"context"
	"reflect"
	"slices"
	"strings"
	"testing"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
	reconcile "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

func TestConfigMapDigestStableComparison(t *testing.T) {
	digest := configDigest(&corev1.ConfigMap{Data: map[string]string{"able": "1", "baker": "2"}})

	if configDigest(&corev1.ConfigMap{Data: map[string]string{"baker": "2", "able": "1"}}) != digest {
		t.Error("Expected the digest to compare stably")
	}

	for _, data := range []map[string]string{
		{"able": "1", "baker": "3"},
		{"able": "1", "baker": "2", "charlie": ""},
		{"able1": "", "baker": "2"},
	} {
		if configDigest(&corev1.ConfigMap{Data: data}) == digest {
			t.Errorf("Expected %v to change the digest", data)
		}
	}

	for name, configMapPair := range map[string][2][]*corev1.ConfigMap{
		"a changed binary value": {
			{{BinaryData: map[string][]byte{"able": []byte("1")}}},
			{{BinaryData: map[string][]byte{"able": []byte("2")}}},
		},
		"a key moved to the next ConfigMap": {
			{{Data: map[string]string{"able": "1"}}, {Data: map[string]string{"baker": "2"}}},
			{{Data: map[string]string{"able": "1", "baker": "2"}}, {}},
		},
	} {
		if configDigest(configMapPair[0]...) == configDigest(configMapPair[1]...) {
			t.Errorf("Expected %s to change the digest", name)
		}
	}
}

func TestReconcileHoldsConfigDigestUntilProvisioned(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

	addOAuth2Application(clientExtension, userAgentApplicationPID)

	clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able"}

	podTemplate := newInitializedPodTemplate()

	podTemplate.Spec.Containers[0].Env = append(
		podTemplate.Spec.Containers[0].Env,
		corev1.EnvVar{Name: "LIFERAY_ROUTES_CLIENT_EXTENSION", Value: MountPathExtInitMetadata},
	)

	podTemplate.Spec.Containers[0].VolumeMounts = append(
		podTemplate.Spec.Containers[0].VolumeMounts,
		corev1.VolumeMount{MountPath: MountPathExtInitMetadata, Name: "ext-init-metadata"},
	)

	podTemplate.Spec.Volumes = append(
		podTemplate.Spec.Volumes,
		corev1.Volume{
			Name: "ext-init-metadata",
			VolumeSource: corev1.VolumeSource{
				ConfigMap: &corev1.ConfigMapVolumeSource{
					LocalObjectReference: corev1.LocalObjectReference{Name: "able-liferay.com-lxc-ext-init-metadata"},
				},
			},
		},
	)

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, newDeployment(podTemplate), newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
		newDxpNamespace("liferay-cx"),
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if digest := getConfigDigest(clientExtensionReconciler, t); digest != "" {
		t.Fatalf("digest = %q, want none before DXP writes ext-init", digest)
	}

	if error := clientExtensionReconciler.Create(
		context.Background(), newExtInitConfigMap("able-oauth-application-user-agent"),
	); error != nil {
		t.Fatal(error)
	}

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	want := configDigest(
		getConfigMap(clientExtensionReconciler, "liferay.com-lxc-dxp-metadata", "liferay-dev", t),
		getConfigMap(clientExtensionReconciler, "able-liferay.com-lxc-ext-init-metadata", "liferay-dev", t),
	)

	if digest := getConfigDigest(clientExtensionReconciler, t); digest != want {
		t.Errorf("digest = %q, want %q: the first digest covers the dxp metadata and ext-init", digest, want)
	}
}

func TestReconcilePutConfigDigestUpdatesWorkload(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

	clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able"}

	dxpMetadata := newDxpMetadataConfigMap("liferay-dev", "liferay.com")

	dxpMetadata.Data = map[string]string{"com.liferay.lxc.dxp.mainDomain": "liferay.example.com"}

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, dxpMetadata, newDeployment(newInitializedPodTemplate()), newDxpNamespace("liferay-cx"),
	)

	recorder := record.NewFakeRecorder(10)

	clientExtensionReconciler.Recorder = recorder

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	firstDigest := getConfigDigest(clientExtensionReconciler, t)

	if configDigest(dxpMetadata) != firstDigest {
		t.Fatalf("digest = %q, want the digest of the dxp metadata", firstDigest)
	}

	if len(recorder.Events) != 1 {
		t.Fatalf("Expected one workload updated event for the first digest, got %d", len(recorder.Events))
	}

	<-recorder.Events

	dxpMetadata.Data["com.liferay.lxc.dxp.mainDomain"] = "uat.example.com"

	if error := clientExtensionReconciler.Update(
		context.Background(), dxpMetadata,
	); error != nil {
		t.Fatal(error)
	}

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if secondDigest := getConfigDigest(
		clientExtensionReconciler, t,
	); firstDigest == secondDigest {
		t.Error("Expected a change to the dxp metadata to change the digest")
	}

	if len(recorder.Events) != 1 {
		t.Fatalf("Expected one update workload event, got %d", len(recorder.Events))
	}

	if event := <-recorder.Events; !strings.HasPrefix(event, corev1.EventTypeNormal+" WorkloadUpdated ") {
		t.Errorf("event = %q, want a workload updated event", event)
	}
}

func TestReconcileReadsWorkloadFromTheAPIServer(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

	clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able"}

	clientExtensionReconciler := newClientExtensionReconciler(
		&interceptor.Funcs{
			Get: func(
				context context.Context, client client.WithWatch, key client.ObjectKey,
				object client.Object, options ...client.GetOption,
			) error {
				if _, ok := object.(*appsv1.Deployment); ok {
					return apierrors.NewNotFound(appsv1.Resource("deployments"), key.Name)
				}

				return client.Get(context, key, object, options...)
			},
		},
		t, clientExtension, newDeployment(newInitializedPodTemplate()), newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
		newDxpNamespace("liferay-cx"),
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if workloadAccepted := getCondition(
		clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionWorkloadAccepted, t,
	); (workloadAccepted == nil) || (workloadAccepted.Reason != ReasonWorkloadAccepted) {
		t.Errorf(
			"WorkloadAccepted = %v, want %s: the cache only holds workload metadata, so the workload is read from the API server",
			workloadAccepted, ReasonWorkloadAccepted,
		)
	}
}

func TestReconcileReportsWorkload(t *testing.T) {
	testCases := map[string]struct {
		objects     []client.Object
		wantIssues  []string
		wantPhase   string
		wantReason  string
		wantStatus  metav1.ConditionStatus
		workloadRef bool
	}{
		"a workload that does not exist yet": {
			wantPhase:   cxv1alpha1.PhasePending,
			wantReason:  ReasonWorkloadNotFound,
			wantStatus:  metav1.ConditionFalse,
			workloadRef: true,
		},
		"a workload that does not mount dxp-metadata": {
			objects:     []client.Object{newDeployment(corev1.PodTemplateSpec{})},
			wantIssues:  []string{`No volume holds ConfigMap "liferay.com-lxc-dxp-metadata".`},
			wantPhase:   cxv1alpha1.PhaseDegraded,
			wantReason:  ReasonWorkloadMisconfigured,
			wantStatus:  metav1.ConditionFalse,
			workloadRef: true,
		},
		"an initialized workload": {
			objects:     []client.Object{newDeployment(newInitializedPodTemplate())},
			wantPhase:   cxv1alpha1.PhaseReady,
			wantReason:  ReasonWorkloadAccepted,
			wantStatus:  metav1.ConditionTrue,
			workloadRef: true,
		},
		"no workload": {
			wantPhase:   cxv1alpha1.PhaseReady,
			wantReason:  ReasonConfigurationOnly,
			wantStatus:  metav1.ConditionTrue,
			workloadRef: false,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

			if testCase.workloadRef {
				clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{
					Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able",
				}
			}

			clientExtensionReconciler := newClientExtensionReconciler(
				nil, t,
				append(
					testCase.objects, clientExtension,
					newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("liferay-cx"),
				)...,
			)

			if phase, _ := reconcileClientExtension(
				clientExtension, clientExtensionReconciler, t,
			); phase != testCase.wantPhase {
				t.Errorf("phase = %q, want %q", phase, testCase.wantPhase)
			}

			workloadAccepted := getCondition(
				clientExtension, clientExtensionReconciler,
				cxv1alpha1.ConditionWorkloadAccepted, t,
			)

			if (workloadAccepted == nil) || (testCase.wantReason != workloadAccepted.Reason) ||
				(testCase.wantStatus != workloadAccepted.Status) {
				t.Errorf(
					"WorkloadAccepted = %v, want %s / %s", workloadAccepted,
					testCase.wantReason, testCase.wantStatus,
				)
			}

			clientExtension = getClientExtension(clientExtension, clientExtensionReconciler, t)

			if workloadIssues := clientExtension.Status.WorkloadIssues; !slices.Equal(workloadIssues, testCase.wantIssues) {
				t.Errorf("workloadIssues = %q, want %q", workloadIssues, testCase.wantIssues)
			}
		})
	}
}

func TestReconcileReportsWorkloadNotFoundAfterGracePeriod(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

	clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able"}

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("liferay-cx"),
	)

	recorder := record.NewFakeRecorder(10)

	clientExtensionReconciler.Recorder = recorder

	result, error := clientExtensionReconciler.Reconcile(
		context.Background(), controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	)

	if error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if (result.RequeueAfter <= 0) || (result.RequeueAfter > workloadGracePeriod) {
		t.Errorf("Reconcile() RequeueAfter = %v, want a retry within %v", result.RequeueAfter, workloadGracePeriod)
	}

	if len(recorder.Events) != 0 {
		t.Errorf("Expected no event while the chart may still be creating the workload, got %d", len(recorder.Events))
	}

	updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

	workloadAccepted := meta.FindStatusCondition(
		updatedClientExtension.Status.Conditions, cxv1alpha1.ConditionWorkloadAccepted,
	)

	workloadAccepted.LastTransitionTime = metav1.NewTime(time.Now().Add(-workloadGracePeriod - time.Second))

	if error := clientExtensionReconciler.Status().Update(context.Background(), updatedClientExtension); error != nil {
		t.Fatal(error)
	}

	if phase, _ := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); phase != cxv1alpha1.PhaseDegraded {
		t.Errorf("phase = %q, want %q once the grace period has passed", phase, cxv1alpha1.PhaseDegraded)
	}

	if len(recorder.Events) != 1 {
		t.Fatalf("Expected one warning event once the grace period has passed, got %d", len(recorder.Events))
	}

	if event := <-recorder.Events; !strings.HasPrefix(event, corev1.EventTypeWarning+" "+ReasonWorkloadNotFound+" ") {
		t.Errorf("event = %q, want a %s warning", event, ReasonWorkloadNotFound)
	}
}

func TestReconcileRestartsWorkloadGracePeriodWhenMisconfiguredWorkloadDisappears(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

	clientExtension.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able"}

	deployment := newDeployment(corev1.PodTemplateSpec{})

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, deployment, newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
		newDxpNamespace("liferay-cx"),
	)

	if phase, _ := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); phase != cxv1alpha1.PhaseDegraded {
		t.Fatalf("phase = %q, want %q", phase, cxv1alpha1.PhaseDegraded)
	}

	updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

	workloadAccepted := meta.FindStatusCondition(
		updatedClientExtension.Status.Conditions, cxv1alpha1.ConditionWorkloadAccepted,
	)

	workloadAccepted.LastTransitionTime = metav1.NewTime(time.Now().Add(-workloadGracePeriod - time.Second))

	if error := clientExtensionReconciler.Status().Update(context.Background(), updatedClientExtension); error != nil {
		t.Fatal(error)
	}

	if error := clientExtensionReconciler.Delete(context.Background(), deployment); error != nil {
		t.Fatal(error)
	}

	recorder := record.NewFakeRecorder(10)

	clientExtensionReconciler.Recorder = recorder

	if phase, _ := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); phase != cxv1alpha1.PhasePending {
		t.Errorf("phase = %q, want %q: the chart may be recreating the workload", phase, cxv1alpha1.PhasePending)
	}

	if len(recorder.Events) != 0 {
		t.Errorf("Expected no event while the chart may still be recreating the workload, got %d", len(recorder.Events))
	}
}

func TestRequestsForWorkloadMatchesKindAndName(t *testing.T) {
	able := newClientExtension("liferay-dev", "able", "liferay-cx")

	able.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{
		Kind: cxv1alpha1.WorkloadKindDeployment, Name: "able",
	}

	baker := newClientExtension("liferay-dev", "baker", "liferay-cx")

	baker.Spec.WorkloadRef = &cxv1alpha1.WorkloadRef{
		Kind: cxv1alpha1.WorkloadKindJob, Name: "able",
	}

	clientExtensionReconciler := newClientExtensionReconciler(nil, t, able, baker)

	got := clientExtensionReconciler.requestsForWorkload(cxv1alpha1.WorkloadKindDeployment)(
		context.Background(), newDeployment(corev1.PodTemplateSpec{}),
	)

	if want := []reconcile.Request{
		{NamespacedName: types.NamespacedName{Name: "able", Namespace: "liferay-cx"}},
	}; !reflect.DeepEqual(got, want) {
		t.Errorf(
			"requestsForWorkload() = %v, want %v: A Job of the same name is a different workload",
			got, want,
		)
	}
}

func TestValidatePodTemplate(t *testing.T) {
	testCases := map[string]struct {
		change     func(podTemplate *corev1.PodTemplateSpec)
		wantIssues []string
	}{
		"a container that is missing volume mounts": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				podTemplate.Spec.Containers[0].VolumeMounts = nil
			},
			wantIssues: []string{`No container mounts volume "dxp-metadata" at "/etc/liferay/lxc/dxp-metadata".`},
		},
		"a container that mounts one key of the volume": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				podTemplate.Spec.Containers[0].VolumeMounts[0].SubPath = "com.liferay.lxc.dxp.domains"
			},
			wantIssues: []string{
				`Container "able" mounts a single key of volume "dxp-metadata" at "/etc/liferay/lxc/dxp-metadata", not the whole volume.`,
			},
		},
		"a container whose envvar is wrong": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				podTemplate.Spec.Containers[0].Env[0].Value = "/incorrect"
			},
			wantIssues: []string{
				`No container mounts volume "dxp-metadata" and sets LIFERAY_ROUTES_DXP to "/etc/liferay/lxc/dxp-metadata".`,
			},
		},
		"a sidecar that sets the variable without mounting the volume": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				podTemplate.Spec.Containers = append(
					podTemplate.Spec.Containers,
					corev1.Container{Env: podTemplate.Spec.Containers[0].Env, Name: "baker"},
				)

				podTemplate.Spec.Containers[0].Env = nil
			},
			wantIssues: []string{
				`No container mounts volume "dxp-metadata" and sets LIFERAY_ROUTES_DXP to "/etc/liferay/lxc/dxp-metadata".`,
			},
		},
		"a template without the volume": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				podTemplate.Spec.Volumes = nil
			},
			wantIssues: []string{`No volume holds ConfigMap "liferay.com-lxc-dxp-metadata".`},
		},
		"a volume that holds only some keys": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				podTemplate.Spec.Volumes[0].ConfigMap.Items = []corev1.KeyToPath{
					{Key: "com.liferay.lxc.dxp.domains", Path: "domains"},
				}
			},
			wantIssues: []string{
				`Volume "dxp-metadata" holds only some keys of ConfigMap "liferay.com-lxc-dxp-metadata".`,
			},
		},
		"a volume that holds some keys beside one that holds them all": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				partialVolume := *podTemplate.Spec.Volumes[0].DeepCopy()

				partialVolume.ConfigMap.Items = []corev1.KeyToPath{{Key: "com.liferay.lxc.dxp.domains", Path: "domains"}}
				partialVolume.Name = "dxp-domains"

				podTemplate.Spec.Volumes = append([]corev1.Volume{partialVolume}, podTemplate.Spec.Volumes...)
			},
		},
		"an initialized template": {
			change: func(podTemplate *corev1.PodTemplateSpec) {},
		},
		"an optional volume": {
			change: func(podTemplate *corev1.PodTemplateSpec) {
				optional := true

				podTemplate.Spec.Volumes[0].ConfigMap.Optional = &optional
			},
			wantIssues: []string{
				`Volume "dxp-metadata" marks ConfigMap "liferay.com-lxc-dxp-metadata" optional, so the pod can start without it.`,
			},
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			podTemplate := newInitializedPodTemplate()

			testCase.change(&podTemplate)

			workloadIssues := validatePodTemplate(
				"liferay.com-lxc-dxp-metadata", "LIFERAY_ROUTES_DXP", MountPathDxpMetadata,
				&podTemplate,
			)

			if !slices.Equal(workloadIssues, testCase.wantIssues) {
				t.Errorf("validatePodTemplate() = %q, want %q", workloadIssues, testCase.wantIssues)
			}
		})
	}
}

func getConfigDigest(clientExtensionReconciler *ClientExtensionReconciler, t *testing.T) string {
	t.Helper()

	var deployment appsv1.Deployment

	if error := clientExtensionReconciler.Get(
		context.Background(), types.NamespacedName{Name: "able", Namespace: "liferay-cx"}, &deployment,
	); error != nil {
		t.Fatal(error)
	}

	return deployment.Spec.Template.Annotations[AnnotationConfigDigest]
}

func newDeployment(podTemplate corev1.PodTemplateSpec) *appsv1.Deployment {
	return &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: "able", Namespace: "liferay-cx"},
		Spec:       appsv1.DeploymentSpec{Template: podTemplate},
	}
}

func newInitializedPodTemplate() corev1.PodTemplateSpec {
	return corev1.PodTemplateSpec{
		Spec: corev1.PodSpec{
			Containers: []corev1.Container{
				{
					Env: []corev1.EnvVar{{
						Name:  "LIFERAY_ROUTES_DXP",
						Value: MountPathDxpMetadata,
					}},
					Name: "able",
					VolumeMounts: []corev1.VolumeMount{{
						MountPath: MountPathDxpMetadata,
						Name:      "dxp-metadata",
					}},
				},
			},
			Volumes: []corev1.Volume{{
				Name: "dxp-metadata",
				VolumeSource: corev1.VolumeSource{
					ConfigMap: &corev1.ConfigMapVolumeSource{
						LocalObjectReference: corev1.LocalObjectReference{Name: "liferay.com-lxc-dxp-metadata"},
					},
				},
			}},
		},
	}
}
