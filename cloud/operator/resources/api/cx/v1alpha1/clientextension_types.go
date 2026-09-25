package v1alpha1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const AnnotationAllowedClientExtensionNamespaces = "cx.liferay.com/allowed-client-extension-namespaces"

const (
	ConditionConfigurationAccepted = "ConfigurationAccepted"
	ConditionDelivered             = "Delivered"
	ConditionProvisioned           = "Provisioned"
	ConditionReady                 = "Ready"
	ConditionWorkloadAccepted      = "WorkloadAccepted"
)

const (
	PhaseDegraded = "Degraded"
	PhasePending  = "Pending"
	PhaseReady    = "Ready"
)

const (
	WorkloadKindCronJob    = "CronJob"
	WorkloadKindDeployment = "Deployment"
	WorkloadKindJob        = "Job"
)

func init() {
	SchemeBuilder.Register(&ClientExtension{}, &ClientExtensionList{})
}

// +kubebuilder:object:root=true
// +kubebuilder:printcolumn:JSONPath=`.spec.liferayNamespace`,name="DXP-Namespace",type=string
// +kubebuilder:printcolumn:JSONPath=`.spec.virtualInstanceId`,name="Virtual-Instance",type=string
// +kubebuilder:printcolumn:JSONPath=`.spec.workloadRef.kind`,name="Workload",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.conditions[?(@.type=="Delivered")].status`,name="Delivered",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.conditions[?(@.type=="ConfigurationAccepted")].status`,name="Config-Accepted",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.conditions[?(@.type=="Provisioned")].status`,name="Provisioned",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.phase`,name="Phase",type=string
// +kubebuilder:printcolumn:JSONPath=`.metadata.creationTimestamp`,name="Age",type=date
// +kubebuilder:printcolumn:JSONPath=`.spec.serviceId`,name="Service-ID",priority=1,type=string
// +kubebuilder:resource:shortName=cx
// +kubebuilder:subresource:status
type ClientExtension struct {
	metav1.ObjectMeta `json:"metadata,omitempty"`
	metav1.TypeMeta   `json:",inline"`

	Spec   ClientExtensionSpec   `json:"spec,omitempty"`
	Status ClientExtensionStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true
type ClientExtensionList struct {
	metav1.ListMeta `json:"metadata,omitempty"`
	metav1.TypeMeta `json:",inline"`

	Items []ClientExtension `json:"items"`
}

type ClientExtensionSpec struct {
	// +optional
	Configs []string `json:"configs,omitempty"`

	// +optional
	Domain string `json:"domain,omitempty"`

	// +kubebuilder:validation:MaxLength=63
	// +kubebuilder:validation:Pattern=`^[a-z0-9]([-a-z0-9]*[a-z0-9])?$`
	// +optional
	LiferayNamespace string `json:"liferayNamespace,omitempty"`

	// +optional
	ProjectName string `json:"projectName,omitempty"`

	// +kubebuilder:validation:MinLength=1
	// +kubebuilder:validation:Required
	ServiceID string `json:"serviceId"`

	// +kubebuilder:validation:MinLength=1
	// +kubebuilder:validation:Required
	VirtualInstanceID string `json:"virtualInstanceId"`

	// +optional
	WorkloadRef *WorkloadRef `json:"workloadRef,omitempty"`
}

type ClientExtensionStatus struct {
	// +optional
	AppliedConfigurationPIDs []string `json:"appliedConfigurationPids,omitempty"`

	// +listMapKey=type
	// +listType=map
	// +optional
	Conditions []metav1.Condition `json:"conditions,omitempty"`

	// +optional
	ConfigurationErrors []ConfigurationError `json:"configurationErrors,omitempty"`

	// +optional
	ExtInitSecretName string `json:"extInitSecretName,omitempty"`

	// +optional
	ExtProvisionConfigMapNames []string `json:"extProvisionConfigMapNames,omitempty"`

	// +optional
	ObservedGeneration int64 `json:"observedGeneration,omitempty"`

	// +kubebuilder:validation:Enum=Degraded;Pending;Ready
	// +optional
	Phase string `json:"phase,omitempty"`

	// +optional
	WorkloadIssues []string `json:"workloadIssues,omitempty"`

	// +optional
	WorkloadName string `json:"workloadName,omitempty"`
}

type ConfigurationError struct {
	// +optional
	ConfigMapName string `json:"configMapName,omitempty"`

	// +optional
	Message string `json:"message,omitempty"`

	// +kubebuilder:validation:Enum=Apply;Parse
	// +optional
	Phase string `json:"phase,omitempty"`

	// +optional
	PID string `json:"pid,omitempty"`
}

type WorkloadRef struct {
	// +kubebuilder:default=apps/v1
	// +kubebuilder:validation:Enum=apps/v1;batch/v1
	// +optional
	APIVersion string `json:"apiVersion,omitempty"`

	// +kubebuilder:validation:Enum=CronJob;Deployment;Job
	// +kubebuilder:validation:Required
	Kind string `json:"kind"`

	// +kubebuilder:validation:MinLength=1
	// +kubebuilder:validation:Required
	Name string `json:"name"`
}
