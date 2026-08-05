// +kubebuilder:webhook:admissionReviewVersions=v1,failurePolicy=fail,groups=apps,mutating=false,name=vstatefulsetscale.licensing.liferay.com,path=/validate-apps-v1-statefulset,resources=statefulsets;statefulsets/scale,sideEffects=None,verbs=create;update,versions=v1
package licensing

import (
	"context"
	"fmt"
	"net/http"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	autoscalingv1 "k8s.io/api/autoscaling/v1"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
	admission "sigs.k8s.io/controller-runtime/pkg/webhook/admission"
)

const WebhookPath = "/validate-apps-v1-statefulset"

func (statefulSetScaleValidator *StatefulSetScaleValidator) Handle(
	context context.Context,
	request admission.Request,
) admission.Response {
	logger := logf.FromContext(context)

	requestedReplicas, workloadName, error := statefulSetScaleValidator.getRequestedReplicas(
		request,
	)

	if error != nil {
		return admission.Errored(http.StatusBadRequest, error)
	}

	maxClusterNodes, found, error := statefulSetScaleValidator.getMaxClusterNodes(
		context, request.Namespace, workloadName,
	)

	if error != nil {
		logger.Error(
			error, "Unable to determine the licensed maxClusterNodes.",
			"namespace", request.Namespace, "statefulSet", workloadName,
		)

		return admission.Denied(
			fmt.Sprintf(
				"unable to determine the licensed maxClusterNodes for StatefulSet %q: %v",
				workloadName, error,
			),
		)
	}

	if !found {
		return admission.Allowed("not a licensed Liferay workload")
	}

	if maxClusterNodes <= 0 {
		return admission.Denied(
			fmt.Sprintf(
				"licensed maxClusterNodes for StatefulSet %q is not yet available; "+
					"retry once the LiferayEnvironment reports maxClusterNodes",
				workloadName,
			),
		)
	}

	if requestedReplicas > maxClusterNodes {
		return admission.Denied(
			fmt.Sprintf(
				"replicas %d exceeds licensed maxClusterNodes %d for StatefulSet %q",
				requestedReplicas, maxClusterNodes, workloadName,
			),
		)
	}

	return admission.Allowed("within licensed maxClusterNodes")
}

func (statefulSetScaleValidator *StatefulSetScaleValidator) getMaxClusterNodes(
	context context.Context,
	namespace string,
	workloadName string,
) (int32, bool, error) {
	liferayEnvironmentList := &licensingv1alpha1.LiferayEnvironmentList{}

	if error := statefulSetScaleValidator.Client.List(
		context, liferayEnvironmentList, client.InNamespace(namespace),
	); error != nil {
		return 0, false, error
	}

	for _, liferayEnvironment := range liferayEnvironmentList.Items {
		if liferayEnvironment.Spec.WorkloadRef.Name == workloadName {
			return liferayEnvironment.Status.License.MaxClusterNodes, true, nil
		}
	}

	return 0, false, nil
}

func (statefulSetScaleValidator *StatefulSetScaleValidator) getRequestedReplicas(
	request admission.Request,
) (int32, string, error) {
	if request.SubResource == "scale" {
		scale := &autoscalingv1.Scale{}

		if error := statefulSetScaleValidator.Decoder.Decode(request, scale); error != nil {
			return 0, "", error
		}

		return scale.Spec.Replicas, request.Name, nil
	}

	statefulSet := &appsv1.StatefulSet{}

	if error := statefulSetScaleValidator.Decoder.Decode(request, statefulSet); error != nil {
		return 0, "", error
	}

	replicas := int32(1)

	if statefulSet.Spec.Replicas != nil {
		replicas = *statefulSet.Spec.Replicas
	}

	return replicas, statefulSet.Name, nil
}

type StatefulSetScaleValidator struct {
	Client  client.Client
	Decoder admission.Decoder
}
