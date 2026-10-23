package licensing

import (
	"context"
	"maps"
	"path/filepath"
	"slices"
	"testing"
	"time"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	autoscalingv2 "k8s.io/api/autoscaling/v2"
	corev1 "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	unstructured "k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	runtime "k8s.io/apimachinery/pkg/runtime"
	types "k8s.io/apimachinery/pkg/types"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	fake "sigs.k8s.io/controller-runtime/pkg/client/fake"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
	controllerconfig "sigs.k8s.io/controller-runtime/pkg/config"
	envtest "sigs.k8s.io/controller-runtime/pkg/envtest"
	metricsserver "sigs.k8s.io/controller-runtime/pkg/metrics/server"
)

func TestCapReplicaBounds(t *testing.T) {
	testCases := map[string]struct {
		autoscaling           *licensingv1alpha1.Autoscaling
		expectedReplicaBounds replicaBounds
		replicaCeiling        int32
	}{
		"caps the maximum at the replica ceiling": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			expectedReplicaBounds: replicaBounds{Maximum: 3, Minimum: 1},
			replicaCeiling:        3,
		},
		"caps the minimum at the capped maximum": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 5},
			expectedReplicaBounds: replicaBounds{Maximum: 3, Minimum: 3},
			replicaCeiling:        3,
		},
		"keeps bounds within the replica ceiling": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 4, MinReplicas: 2},
			expectedReplicaBounds: replicaBounds{Maximum: 4, Minimum: 2},
			replicaCeiling:        5,
		},
		"pauses at a ceiling of zero": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 2},
			expectedReplicaBounds: replicaBounds{Maximum: 1, Minimum: 1, Paused: true},
			replicaCeiling:        0,
		},
		"raises a zero minimum at a ceiling of zero": {
			autoscaling:           &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 0},
			expectedReplicaBounds: replicaBounds{Maximum: 1, Minimum: 1, Paused: true},
			replicaCeiling:        0,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			actualReplicaBounds := capReplicaBounds(
				testCase.autoscaling, testCase.replicaCeiling,
			)

			if actualReplicaBounds != testCase.expectedReplicaBounds {
				t.Errorf(
					"capReplicaBounds = %+v, want %+v",
					actualReplicaBounds, testCase.expectedReplicaBounds,
				)
			}
		})
	}
}

func TestEnforceAutoscalerCeiling(t *testing.T) {
	testCases := map[string]struct {
		autoscaling         *licensingv1alpha1.Autoscaling
		expectedMaxReplicas int32
		expectedMinReplicas int32
		liveMaxReplicas     int32
		liveMinReplicas     int32
		replicaCeiling      int32
	}{
		"caps the maximum at the licensed ceiling": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			expectedMaxReplicas: 3,
			expectedMinReplicas: 1,
			liveMaxReplicas:     10,
			liveMinReplicas:     1,
			replicaCeiling:      3,
		},
		"caps the minimum at the licensed ceiling": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 5},
			expectedMaxReplicas: 3,
			expectedMinReplicas: 3,
			liveMaxReplicas:     10,
			liveMinReplicas:     5,
			replicaCeiling:      3,
		},
		"floors the maximum at one replica when the ceiling is zero": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 2},
			expectedMaxReplicas: 1,
			expectedMinReplicas: 1,
			liveMaxReplicas:     10,
			liveMinReplicas:     2,
			replicaCeiling:      0,
		},
		"leaves bounds within the licensed ceiling": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 4, MinReplicas: 2},
			expectedMaxReplicas: 4,
			expectedMinReplicas: 2,
			liveMaxReplicas:     4,
			liveMinReplicas:     2,
			replicaCeiling:      5,
		},
		"leaves the autoscaler alone without requested bounds": {
			autoscaling:         nil,
			expectedMaxReplicas: 10,
			expectedMinReplicas: 1,
			liveMaxReplicas:     10,
			liveMinReplicas:     1,
			replicaCeiling:      3,
		},
		"raises a capped maximum after a license upgrade": {
			autoscaling:         &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			expectedMaxReplicas: 5,
			expectedMinReplicas: 1,
			liveMaxReplicas:     3,
			liveMinReplicas:     1,
			replicaCeiling:      5,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "dev",
					Namespace: "liferay-dev",
				},
				Spec: licensingv1alpha1.LiferayEnvironmentSpec{
					Autoscaling: testCase.autoscaling,
					WorkloadRef: licensingv1alpha1.WorkloadRef{
						Name: "dev-liferay",
					},
				},
			}

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(
					t,
					liferayEnvironment,
					newHorizontalPodAutoscaler(
						testCase.liveMaxReplicas, testCase.liveMinReplicas, "dev-liferay",
					),
					newScaledObject(
						testCase.liveMaxReplicas, testCase.liveMinReplicas, "dev-liferay",
					),
				),
			}

			if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
				context.Background(), liferayEnvironment, testCase.replicaCeiling,
			); error != nil {
				t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
			}

			horizontalPodAutoscaler := getHorizontalPodAutoscaler(
				liferayEnvironmentReconciler, t,
			)

			assertReplicasEqual(
				&horizontalPodAutoscaler.Spec.MaxReplicas,
				&testCase.expectedMaxReplicas,
				"horizontalPodAutoscaler.spec.maxReplicas",
				t,
			)

			assertReplicasEqual(
				horizontalPodAutoscaler.Spec.MinReplicas,
				&testCase.expectedMinReplicas,
				"horizontalPodAutoscaler.spec.minReplicas",
				t,
			)

			scaledObject := getScaledObject(liferayEnvironmentReconciler, t)

			assertScaledObjectReplicaCount(
				int64(testCase.expectedMaxReplicas),
				"maxReplicaCount",
				scaledObject,
				t,
			)

			assertScaledObjectReplicaCount(
				int64(testCase.expectedMinReplicas),
				"minReplicaCount",
				scaledObject,
				t,
			)
		})
	}
}

func TestEnforceAutoscalerCeilingIgnoresOtherAutoscalers(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	kedaHorizontalPodAutoscaler := newHorizontalPodAutoscaler(10, 1, "dev-liferay")

	kedaHorizontalPodAutoscaler.Name = "keda-hpa-dev-liferay"
	kedaHorizontalPodAutoscaler.OwnerReferences = []metav1.OwnerReference{
		{
			APIVersion: "keda.sh/v1alpha1",
			Kind:       "ScaledObject",
			Name:       "dev-liferay",
			UID:        "scaled-object-uid",
		},
	}

	otherHorizontalPodAutoscaler := newHorizontalPodAutoscaler(10, 1, "other-workload")

	otherHorizontalPodAutoscaler.Name = "other-workload"

	otherScaledObject := newScaledObject(10, 1, "other-workload")

	otherScaledObject.SetName("other-workload")

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClient(
			t,
			kedaHorizontalPodAutoscaler,
			liferayEnvironment,
			otherHorizontalPodAutoscaler,
			otherScaledObject,
		),
	}

	if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
	}

	for _, name := range []string{"keda-hpa-dev-liferay", "other-workload"} {
		horizontalPodAutoscaler := &autoscalingv2.HorizontalPodAutoscaler{}

		if error := liferayEnvironmentReconciler.Get(
			context.Background(),
			types.NamespacedName{Name: name, Namespace: "liferay-dev"},
			horizontalPodAutoscaler,
		); error != nil {
			t.Fatalf("Unable to read the autoscaler %q: %v", name, error)
		}

		if horizontalPodAutoscaler.Spec.MaxReplicas != 10 {
			t.Errorf(
				"Autoscaler %q spec.maxReplicas = %d, want 10",
				name, horizontalPodAutoscaler.Spec.MaxReplicas,
			)
		}
	}

	scaledObject := newScaledObject(0, 0, "")

	if error := liferayEnvironmentReconciler.Get(
		context.Background(),
		types.NamespacedName{Name: "other-workload", Namespace: "liferay-dev"},
		scaledObject,
	); error != nil {
		t.Fatalf("Unable to read the scaled object: %v", error)
	}

	assertScaledObjectReplicaCount(10, "maxReplicaCount", scaledObject, t)
}

func TestEnforceAutoscalerCeilingPausesKEDAAtZero(t *testing.T) {
	testCases := map[string]struct {
		annotations         map[string]string
		expectedAnnotations map[string]string
		expectedMaxReplicas int32
		liveMaxReplicas     int32
		replicaCeiling      int32
	}{
		"leaves a pause it did not set": {
			annotations:         map[string]string{annotationPausedReplicas: "5"},
			expectedAnnotations: map[string]string{annotationPausedReplicas: "5"},
			expectedMaxReplicas: 3,
			liveMaxReplicas:     10,
			replicaCeiling:      3,
		},
		"lifts its own pause when the ceiling rises": {
			annotations: map[string]string{
				annotationPausedReplicas:      "0",
				annotationPausedReplicasOwner: "true",
			},
			expectedAnnotations: nil,
			expectedMaxReplicas: 3,
			liveMaxReplicas:     1,
			replicaCeiling:      3,
		},
		"pauses at a ceiling of zero": {
			annotations: nil,
			expectedAnnotations: map[string]string{
				annotationPausedReplicas:      "0",
				annotationPausedReplicasOwner: "true",
			},
			expectedMaxReplicas: 1,
			liveMaxReplicas:     10,
			replicaCeiling:      0,
		},
		"pauses when the ceiling drops from one to zero": {
			annotations: nil,
			expectedAnnotations: map[string]string{
				annotationPausedReplicas:      "0",
				annotationPausedReplicasOwner: "true",
			},
			expectedMaxReplicas: 1,
			liveMaxReplicas:     1,
			replicaCeiling:      0,
		},
		"stays unpaused at a ceiling of one": {
			annotations:         nil,
			expectedAnnotations: nil,
			expectedMaxReplicas: 1,
			liveMaxReplicas:     10,
			replicaCeiling:      1,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "dev",
					Namespace: "liferay-dev",
				},
				Spec: licensingv1alpha1.LiferayEnvironmentSpec{
					Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
					WorkloadRef: licensingv1alpha1.WorkloadRef{
						Name: "dev-liferay",
					},
				},
			}

			scaledObject := newScaledObject(testCase.liveMaxReplicas, 1, "dev-liferay")

			scaledObject.SetAnnotations(testCase.annotations)

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(t, liferayEnvironment, scaledObject),
			}

			if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
				context.Background(), liferayEnvironment, testCase.replicaCeiling,
			); error != nil {
				t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
			}

			scaledObject = getScaledObject(liferayEnvironmentReconciler, t)

			if !maps.Equal(scaledObject.GetAnnotations(), testCase.expectedAnnotations) {
				t.Errorf(
					"scaledObject.metadata.annotations = %v, want %v",
					scaledObject.GetAnnotations(), testCase.expectedAnnotations,
				)
			}

			assertScaledObjectReplicaCount(
				int64(testCase.expectedMaxReplicas), "maxReplicaCount", scaledObject, t,
			)
		})
	}
}

func TestEnforceAutoscalerCeilingSetsMissingScaledObjectBounds(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	scaledObject := newScaledObject(0, 0, "dev-liferay")

	unstructured.RemoveNestedField(scaledObject.Object, "spec", "maxReplicaCount")
	unstructured.RemoveNestedField(scaledObject.Object, "spec", "minReplicaCount")

	if error := unstructured.SetNestedField(
		scaledObject.Object, int64(30), "spec", "pollingInterval",
	); error != nil {
		t.Fatalf("Unable to set scaledObject.spec.pollingInterval: %v", error)
	}

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClient(t, liferayEnvironment, scaledObject),
	}

	if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
	}

	scaledObject = getScaledObject(liferayEnvironmentReconciler, t)

	assertScaledObjectReplicaCount(3, "maxReplicaCount", scaledObject, t)

	assertScaledObjectReplicaCount(1, "minReplicaCount", scaledObject, t)

	pollingInterval, found, error := unstructured.NestedInt64(
		scaledObject.Object, "spec", "pollingInterval",
	)

	if error != nil || !found || pollingInterval != 30 {
		t.Errorf(
			"scaledObject.spec.pollingInterval = %d (found %t, error %v), want 30",
			pollingInterval, found, error,
		)
	}
}

func TestEnforceAutoscalerCeilingSkipsMissingKEDA(t *testing.T) {
	liferayEnvironment := &licensingv1alpha1.LiferayEnvironment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "dev",
			Namespace: "liferay-dev",
		},
		Spec: licensingv1alpha1.LiferayEnvironmentSpec{
			Autoscaling: &licensingv1alpha1.Autoscaling{MaxReplicas: 10, MinReplicas: 1},
			WorkloadRef: licensingv1alpha1.WorkloadRef{
				Name: "dev-liferay",
			},
		},
	}

	fakeClient := fake.NewClientBuilder().WithInterceptorFuncs(
		interceptor.Funcs{
			List: func(
				context context.Context,
				writer client.WithWatch,
				list client.ObjectList,
				options ...client.ListOption,
			) error {
				if _, ok := list.(*unstructured.UnstructuredList); ok {
					return &meta.NoKindMatchError{
						GroupKind: scaledObjectGroupVersionKind.GroupKind(),
					}
				}

				return writer.List(context, list, options...)
			},
		},
	).WithObjects(
		liferayEnvironment, newHorizontalPodAutoscaler(10, 1, "dev-liferay"),
	).WithScheme(
		newScheme(t),
	).Build()

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: fakeClient,
	}

	if error := liferayEnvironmentReconciler.enforceAutoscalerCeiling(
		context.Background(), liferayEnvironment, 3,
	); error != nil {
		t.Fatalf("Unexpected error from enforceAutoscalerCeiling: %v", error)
	}

	horizontalPodAutoscaler := getHorizontalPodAutoscaler(liferayEnvironmentReconciler, t)

	if horizontalPodAutoscaler.Spec.MaxReplicas != 3 {
		t.Errorf(
			"spec.maxReplicas = %d, want 3",
			horizontalPodAutoscaler.Spec.MaxReplicas,
		)
	}
}

func TestEnqueueScaleTargetEnvironments(t *testing.T) {
	deploymentHorizontalPodAutoscaler := newHorizontalPodAutoscaler(10, 1, "dev-liferay")

	deploymentHorizontalPodAutoscaler.Spec.ScaleTargetRef.Kind = "Deployment"

	deploymentScaledObject := newScaledObject(10, 1, "dev-liferay")

	unstructured.RemoveNestedField(
		deploymentScaledObject.Object, "spec", "scaleTargetRef", "kind",
	)

	testCases := map[string]struct {
		autoscaler     client.Object
		expectEnqueued bool
		workloadKind   licensingv1alpha1.WorkloadKind
	}{
		"horizontal pod autoscaler scaling a deployment of the same name": {
			autoscaler:     deploymentHorizontalPodAutoscaler,
			expectEnqueued: false,
		},
		"horizontal pod autoscaler scaling a stateful set of the same name": {
			autoscaler:     newHorizontalPodAutoscaler(10, 1, "dev-liferay"),
			expectEnqueued: false,
			workloadKind:   licensingv1alpha1.WorkloadKindDeployment,
		},
		"horizontal pod autoscaler scaling another workload": {
			autoscaler:     newHorizontalPodAutoscaler(10, 1, "other-workload"),
			expectEnqueued: false,
		},
		"horizontal pod autoscaler scaling the deployment workload": {
			autoscaler:     deploymentHorizontalPodAutoscaler,
			expectEnqueued: true,
			workloadKind:   licensingv1alpha1.WorkloadKindDeployment,
		},
		"horizontal pod autoscaler scaling the workload": {
			autoscaler:     newHorizontalPodAutoscaler(10, 1, "dev-liferay"),
			expectEnqueued: true,
		},
		"scaled object scaling a deployment of the same name": {
			autoscaler:     deploymentScaledObject,
			expectEnqueued: false,
		},
		"scaled object scaling another workload": {
			autoscaler:     newScaledObject(10, 1, "other-workload"),
			expectEnqueued: false,
		},
		"scaled object scaling the deployment workload by the default kind": {
			autoscaler:     deploymentScaledObject,
			expectEnqueued: true,
			workloadKind:   licensingv1alpha1.WorkloadKindDeployment,
		},
		"scaled object scaling the workload": {
			autoscaler:     newScaledObject(10, 1, "dev-liferay"),
			expectEnqueued: true,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client: newFakeClient(
					t,
					&licensingv1alpha1.LiferayEnvironment{
						ObjectMeta: metav1.ObjectMeta{
							Name:      "dev",
							Namespace: "liferay-dev",
						},
						Spec: licensingv1alpha1.LiferayEnvironmentSpec{
							WorkloadRef: licensingv1alpha1.WorkloadRef{
								Kind: testCase.workloadKind,
								Name: "dev-liferay",
							},
						},
					},
				),
			}

			requests := liferayEnvironmentReconciler.enqueueScaleTargetEnvironments(
				context.Background(), testCase.autoscaler,
			)

			var expectedRequests []controllerruntime.Request

			if testCase.expectEnqueued {
				expectedRequests = []controllerruntime.Request{
					{
						NamespacedName: types.NamespacedName{
							Name:      "dev",
							Namespace: "liferay-dev",
						},
					},
				}
			}

			if !slices.Equal(requests, expectedRequests) {
				t.Errorf(
					"enqueueScaleTargetEnvironments = %v, want %v",
					requests, expectedRequests,
				)
			}
		})
	}
}

func TestSetupWithManagerWatchesScaledObjects(t *testing.T) {
	testCases := map[string]struct {
		installBeforeStart bool
	}{
		"watches scaled objects when keda is installed after the manager starts": {
			installBeforeStart: false,
		},
		"watches scaled objects when keda is installed before the manager starts": {
			installBeforeStart: true,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			assetsDir := envtestAssetsDir(t)

			if assetsDir == "" {
				t.Skip(
					"Set KUBEBUILDER_ASSETS, or install the envtest binaries with setup-envtest, to run this test",
				)
			}

			testEnvironment := &envtest.Environment{
				BinaryAssetsDirectory: assetsDir,
				CRDDirectoryPaths:     []string{filepath.Join(chartDir, "crds")},
				ErrorIfCRDPathMissing: true,
			}

			config, error := testEnvironment.Start()

			if error != nil {
				t.Fatalf("Unable to start the test environment: %v", error)
			}

			t.Cleanup(func() {
				if error := testEnvironment.Stop(); error != nil {
					t.Errorf("Unable to stop the test environment: %v", error)
				}
			})

			scheme := runtime.NewScheme()

			if error := clientgoscheme.AddToScheme(scheme); error != nil {
				t.Fatalf("Unable to register the client-go scheme: %v", error)
			}

			if error := licensingv1alpha1.AddToScheme(scheme); error != nil {
				t.Fatalf("Unable to register the licensing scheme: %v", error)
			}

			setUpClient, error := client.New(config, client.Options{Scheme: scheme})

			if error != nil {
				t.Fatalf("Unable to build a client: %v", error)
			}

			namespace := &corev1.Namespace{
				ObjectMeta: metav1.ObjectMeta{Name: "liferay-dev"},
			}

			if error := setUpClient.Create(context.Background(), namespace); error != nil {
				t.Fatalf("Unable to create the namespace: %v", error)
			}

			statefulSet := newWorkload(
				"liferay-dev", licensingv1alpha1.WorkloadKindStatefulSet,
			)

			setReplicas(pointerInt32(1), statefulSet)

			if error := setUpClient.Create(context.Background(), statefulSet); error != nil {
				t.Fatalf("Unable to create the workload: %v", error)
			}

			liferayEnvironment := activatedEnvironment()
			liferayEnvironment.Spec.Autoscaling = &licensingv1alpha1.Autoscaling{
				MaxReplicas: 10,
				MinReplicas: 1,
			}

			activatedAt := liferayEnvironment.Status.ActivatedAt

			if error := setUpClient.Create(context.Background(), liferayEnvironment); error != nil {
				t.Fatalf("Unable to create the environment: %v", error)
			}

			liferayEnvironment.Status.ActivatedAt = activatedAt

			if error := setUpClient.Status().Update(
				context.Background(), liferayEnvironment,
			); error != nil {
				t.Fatalf("Unable to activate the environment: %v", error)
			}

			if testCase.installBeforeStart {
				installScaledObjectCustomResourceDefinition(setUpClient, t)
			}

			skipNameValidation := true

			manager, error := controllerruntime.NewManager(
				config,
				controllerruntime.Options{
					Controller: controllerconfig.Controller{
						SkipNameValidation: &skipNameValidation,
					},
					HealthProbeBindAddress: "0",
					Metrics:                metricsserver.Options{BindAddress: "0"},
					Scheme:                 scheme,
				},
			)

			if error != nil {
				t.Fatalf("Unable to build the manager: %v", error)
			}

			provisioningClient := &stubProvisioning{
				entitlements: &provisioning.Entitlements{
					LicenseXML: []byte(virtualClusterLicenseXML(
						"Friday, March 2, 2029 12:00:00 AM GMT", 3, string(namespace.UID),
					)),
					MaxClusterNodes: 3,
				},
			}

			liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
				Client:                  manager.GetClient(),
				HeartbeatInterval:       time.Hour,
				MarketplaceMountPath:    t.TempDir(),
				Provisioning:            provisioningClient,
				ProvisioningGracePeriod: 7 * 24 * time.Hour,
				Recorder:                record.NewFakeRecorder(100),
				RetryInitialDelay:       30 * time.Second,
				RetryMaxDelay:           30 * time.Minute,
				Syncer: addon.NewSyncer(
					provisioningClient, 15*time.Second, 30*time.Second, 30*time.Minute,
					inlineRunner{},
				),
			}

			if error := liferayEnvironmentReconciler.SetupWithManager(manager); error != nil {
				t.Fatalf("Unable to set up the controller: %v", error)
			}

			startManager(manager, t)

			awaitCondition(
				func() bool {
					var stored licensingv1alpha1.LiferayEnvironment

					if error := setUpClient.Get(
						context.Background(),
						types.NamespacedName{Name: "dev", Namespace: "liferay-dev"},
						&stored,
					); error != nil {
						return false
					}

					return stored.Status.Phase == "Ready"
				},
				"the environment to finish its first reconcile",
				t,
			)

			if !testCase.installBeforeStart {
				installScaledObjectCustomResourceDefinition(setUpClient, t)
			}

			if error := setUpClient.Create(
				context.Background(), newScaledObject(10, 1, "dev-liferay"),
			); error != nil {
				t.Fatalf("Unable to create the scaled object: %v", error)
			}

			awaitCondition(
				func() bool {
					scaledObject := newScaledObject(0, 0, "")

					if error := setUpClient.Get(
						context.Background(),
						types.NamespacedName{Name: "dev-liferay", Namespace: "liferay-dev"},
						scaledObject,
					); error != nil {
						return false
					}

					maxReplicaCount, _, _ := unstructured.NestedInt64(
						scaledObject.Object, "spec", "maxReplicaCount",
					)

					return maxReplicaCount == 3
				},
				"the scaled object to be capped at the licensed ceiling",
				t,
			)
		})
	}
}
