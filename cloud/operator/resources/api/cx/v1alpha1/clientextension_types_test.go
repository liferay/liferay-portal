package v1alpha1

import (
	"testing"

	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

func TestWorkloadRefNewObjectMapsTheKindToItsType(t *testing.T) {
	testCases := map[string]struct {
		kind WorkloadKind
		want client.Object
	}{
		"a CronJob is a batch/v1 CronJob": {
			kind: WorkloadKindCronJob,
			want: &batchv1.CronJob{},
		},
		"a Deployment is an apps/v1 Deployment": {
			kind: WorkloadKindDeployment,
			want: &appsv1.Deployment{},
		},
		"a Job is a batch/v1 Job": {
			kind: WorkloadKindJob,
			want: &batchv1.Job{},
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			object, error := WorkloadRef{Kind: testCase.kind, Name: "sample"}.NewObject()

			if error != nil {
				t.Fatalf("NewObject() error = %v, want nil", error)
			}

			if got, want := typeName(object), typeName(testCase.want); got != want {
				t.Errorf("NewObject() = %s, want %s", got, want)
			}
		})
	}
}

func TestWorkloadRefNewObjectRejectsAnUnsupportedKind(t *testing.T) {
	object, error := WorkloadRef{Kind: "StatefulSet", Name: "sample"}.NewObject()

	if error == nil {
		t.Fatalf("NewObject() = %T, want an error", object)
	}

	if want := `unsupported workload kind "StatefulSet"`; error.Error() != want {
		t.Errorf("NewObject() error = %q, want %q", error.Error(), want)
	}
}

func typeName(object client.Object) string {
	switch object.(type) {
	case *appsv1.Deployment:
		return "apps/v1 Deployment"
	case *batchv1.CronJob:
		return "batch/v1 CronJob"
	case *batchv1.Job:
		return "batch/v1 Job"
	}

	return "unknown"
}
