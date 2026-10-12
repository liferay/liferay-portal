package cx

import (
	"context"
	"strings"
	"testing"
	"time"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	meta "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	controllerutil "sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
)

func TestReconcileDeleteGracePeriod(t *testing.T) {
	testCases := map[string]struct {
		delivered   bool
		readyReason string
		readyStatus metav1.ConditionStatus
		wantDeleted bool
		wantEvents  int
	}{
		"a client extension DXP never received": {
			wantDeleted: true,
		},
		"a degraded client extension": {
			delivered:   true,
			readyReason: ReasonWorkloadMisconfigured,
			readyStatus: metav1.ConditionFalse,
		},
		"a delete DXP has not finished within grace period": {
			delivered:   true,
			readyReason: ReasonDeleting,
			readyStatus: metav1.ConditionFalse,
			wantDeleted: true,
			wantEvents:  1,
		},
		"a delete the operator first sees after the grace period": {
			delivered:   true,
			readyReason: ReasonReady,
			readyStatus: metav1.ConditionTrue,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

			graceExpiredTime := metav1.NewTime(
				time.Now().Add(-deleteGracePeriod - time.Second),
			)

			clientExtension.DeletionTimestamp = &graceExpiredTime

			clientExtension.Finalizers = []string{Finalizer}

			if testCase.delivered {
				clientExtension.Status.Conditions = append(
					clientExtension.Status.Conditions,
					metav1.Condition{
						LastTransitionTime: graceExpiredTime,
						Reason:             ReasonDelivered,
						Status:             metav1.ConditionTrue,
						Type:               cxv1alpha1.ConditionDelivered,
					},
				)
			}

			if testCase.readyReason != "" {
				clientExtension.Status.Conditions = append(
					clientExtension.Status.Conditions,
					metav1.Condition{
						LastTransitionTime: graceExpiredTime,
						Reason:             testCase.readyReason,
						Status:             testCase.readyStatus,
						Type:               cxv1alpha1.ConditionReady,
					},
				)
			}

			clientExtensionReconciler := newClientExtensionReconciler(
				nil, t, clientExtension, newDxpNamespace("liferay-cx"),
				newExtInitConfigMap("able-oauth-application-user-agent"),
			)

			recorder := record.NewFakeRecorder(10)

			clientExtensionReconciler.Recorder = recorder

			result, error := clientExtensionReconciler.Reconcile(
				context.Background(), controllerruntime.Request{
					NamespacedName: client.ObjectKeyFromObject(clientExtension),
				},
			)

			if error != nil {
				t.Fatalf("Reconcile() error = %v, want nil", error)
			}

			if testCase.wantDeleted {
				if error := clientExtensionReconciler.Get(
					context.Background(), client.ObjectKeyFromObject(clientExtension),
					&cxv1alpha1.ClientExtension{},
				); !apierrors.IsNotFound(error) {
					t.Errorf("Get() error = %v, want the client extension deleted", error)
				}
			} else {
				updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

				ready := meta.FindStatusCondition(
					updatedClientExtension.Status.Conditions, cxv1alpha1.ConditionReady,
				)

				if (ready == nil) || (ready.Reason != ReasonDeleting) ||
					(time.Since(ready.LastTransitionTime.Time) > time.Minute) {

					t.Errorf(
						"Ready = %+v, want reason %s since the operator first saw the delete",
						ready, ReasonDeleting,
					)
				}

				if updatedClientExtension.Status.Phase != cxv1alpha1.PhasePending {
					t.Errorf(
						"Phase = %q, want %q", updatedClientExtension.Status.Phase,
						cxv1alpha1.PhasePending,
					)
				}

				if result.RequeueAfter <= 0 {
					t.Errorf(
						"Reconcile() RequeueAfter = %v, want a retry", result.RequeueAfter,
					)
				}
			}

			if len(recorder.Events) != testCase.wantEvents {
				t.Fatalf("events = %d, want %d", len(recorder.Events), testCase.wantEvents)
			}

			if testCase.wantEvents > 0 {
				if event := <-recorder.Events; !strings.HasPrefix(
					event, corev1.EventTypeWarning+" "+ReasonDeleteIncomplete+" ",
				) {
					t.Errorf("event = %q, want a DeleteIncomplete warning", event)
				}
			}
		})
	}
}

func TestReconcileDeletesFromDxpOnRemoval(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "liferay-cx")

	addOAuth2Application(clientExtension, userAgentApplicationPID)

	extInitConfigMap := newExtInitConfigMap("able-oauth-application-user-agent")

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, extInitConfigMap,
		newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
		newDxpNamespace("liferay-cx"),
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if !controllerutil.ContainsFinalizer(
		getClientExtension(clientExtension, clientExtensionReconciler, t), Finalizer,
	) {
		t.Fatalf("Expected the %s finalizer on a delivered client extension", Finalizer)
	}

	if error := clientExtensionReconciler.Delete(
		context.Background(), clientExtension,
	); error != nil {
		t.Fatal(error)
	}

	result, error := clientExtensionReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: client.ObjectKeyFromObject(clientExtension),
		},
	)

	if error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if getExtProvisionConfigMap(clientExtensionReconciler, "liferay-dev", t) != nil {
		t.Error("Expected the ext-provision ConfigMap to be deleted")
	}

	if (result.RequeueAfter <= 0) || (result.RequeueAfter > deleteGracePeriod) {
		t.Errorf(
			"Reconcile() RequeueAfter = %v, want a retry within %v", result.RequeueAfter,
			deleteGracePeriod,
		)
	}

	getClientExtension(clientExtension, clientExtensionReconciler, t)

	if error := clientExtensionReconciler.Delete(
		context.Background(), extInitConfigMap,
	); error != nil {
		t.Fatal(error)
	}

	if _, error := clientExtensionReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: client.ObjectKeyFromObject(clientExtension),
		},
	); error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if error := clientExtensionReconciler.Get(
		context.Background(), client.ObjectKeyFromObject(clientExtension), &cxv1alpha1.ClientExtension{},
	); !apierrors.IsNotFound(error) {
		t.Errorf(
			"Get() error = %v, want the client extension deleted once DXP removed its ext-init",
			error,
		)
	}
}
