package v1alpha1

import (
	appsv1 "k8s.io/api/apps/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	WorkloadKindDeployment  WorkloadKind = "Deployment"
	WorkloadKindStatefulSet WorkloadKind = "StatefulSet"
)

func (workloadRef WorkloadRef) EffectiveKind() WorkloadKind {
	if workloadRef.Kind == "" {
		return WorkloadKindStatefulSet
	}

	return workloadRef.Kind
}

func (workloadRef WorkloadRef) NewObject() client.Object {
	if workloadRef.EffectiveKind() == WorkloadKindDeployment {
		return &appsv1.Deployment{}
	}

	return &appsv1.StatefulSet{}
}

func init() {
	SchemeBuilder.Register(&LiferayEnvironment{}, &LiferayEnvironmentList{})
}

type AppStatus struct {
	// +optional
	Checksum string `json:"checksum,omitempty"`

	// +optional
	ConsecutiveFailures int32 `json:"consecutiveFailures,omitempty"`

	// +optional
	Message string `json:"message,omitempty"`

	// +optional
	Name string `json:"name,omitempty"`

	// +optional
	NextRetry *metav1.Time `json:"nextRetry,omitempty"`

	// +kubebuilder:validation:Enum=Downloaded;Downloading;Failed;Orphaned
	// +optional
	State string `json:"state,omitempty"`

	// +optional
	VirtualEntryID int64 `json:"virtualEntryId,omitempty"`
}

// +kubebuilder:validation:XValidation:message="minReplicas must not exceed maxReplicas",rule="self.minReplicas <= self.maxReplicas"
type Autoscaling struct {
	// +kubebuilder:validation:Minimum=1
	// +kubebuilder:validation:Required
	MaxReplicas int32 `json:"maxReplicas"`

	// +kubebuilder:validation:Minimum=0
	// +kubebuilder:validation:Required
	MinReplicas int32 `json:"minReplicas"`
}

type LicenseStatus struct {
	// +optional
	Checksum string `json:"checksum,omitempty"`

	// +optional
	LastVerified *metav1.Time `json:"lastVerified,omitempty"`

	// +optional
	MaxClusterNodes *int32 `json:"maxClusterNodes,omitempty"`

	// +optional
	ValidUntil *metav1.Time `json:"validUntil,omitempty"`
}

// +kubebuilder:object:root=true
// +kubebuilder:printcolumn:JSONPath=`.spec.workloadRef.name`,name="Workload",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.conditions[?(@.type=="Activated")].status`,name="Activated",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.conditions[?(@.type=="AddOnsReady")].status`,name="Add-Ons",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.license.maxClusterNodes`,name="Max",type=integer
// +kubebuilder:printcolumn:JSONPath=`.status.license.validUntil`,name="Valid-Until",type=string
// +kubebuilder:printcolumn:JSONPath=`.spec.desiredReplicas`,name="Desired",type=integer
// +kubebuilder:printcolumn:JSONPath=`.status.replicaCeiling`,name="Ceiling",type=integer
// +kubebuilder:printcolumn:JSONPath=`.status.effectiveReplicas`,name="Effective",type=integer
// +kubebuilder:printcolumn:JSONPath=`.status.phase`,name="Phase",priority=1,type=string
// +kubebuilder:printcolumn:JSONPath=`.status.environmentId`,name="Environment-ID",priority=1,type=string
// +kubebuilder:printcolumn:JSONPath=`.status.activatedAt`,name="Activated-At",priority=1,type=date
// +kubebuilder:printcolumn:JSONPath=`.status.unreachableSince`,name="Unreachable-Since",priority=1,type=date
// +kubebuilder:resource:shortName=lenv
// +kubebuilder:subresource:status
type LiferayEnvironment struct {
	metav1.ObjectMeta `json:"metadata,omitempty"`
	metav1.TypeMeta   `json:",inline"`

	Spec   LiferayEnvironmentSpec   `json:"spec,omitempty"`
	Status LiferayEnvironmentStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true
type LiferayEnvironmentList struct {
	metav1.ListMeta `json:"metadata,omitempty"`
	metav1.TypeMeta `json:",inline"`

	Items []LiferayEnvironment `json:"items"`
}

type LiferayEnvironmentSpec struct {
	// +kubebuilder:validation:Required
	ActivationCodeSecretRef SecretKeyRef `json:"activationCodeSecretRef"`

	// +optional
	Autoscaling *Autoscaling `json:"autoscaling,omitempty"`

	// +optional
	DesiredReplicas *int32 `json:"desiredReplicas,omitempty"`

	// +optional
	DxpVersion string `json:"dxpVersion,omitempty"`

	// +optional
	EnvironmentName string `json:"environmentName,omitempty"`

	// +optional
	Offline bool `json:"offline,omitempty"`

	// +optional
	OfflineActivationBundle string `json:"offlineActivationBundle,omitempty"`

	// +kubebuilder:validation:Required
	WorkloadRef WorkloadRef `json:"workloadRef"`
}

type LiferayEnvironmentStatus struct {
	// +optional
	ActivatedAt *metav1.Time `json:"activatedAt,omitempty"`

	// +optional
	Apps []AppStatus `json:"apps,omitempty"`

	// +listMapKey=type
	// +listType=map
	// +optional
	Conditions []metav1.Condition `json:"conditions,omitempty"`

	// +optional
	ConsecutiveFailures int32 `json:"consecutiveFailures,omitempty"`

	// +optional
	EffectiveReplicas *int32 `json:"effectiveReplicas,omitempty"`

	// +optional
	EnvironmentID string `json:"environmentId,omitempty"`

	// +optional
	License LicenseStatus `json:"license,omitempty"`

	// +kubebuilder:validation:Enum=Degraded;Pending;Ready
	// +optional
	Phase string `json:"phase,omitempty"`

	// +optional
	ReplicaCeiling *int32 `json:"replicaCeiling,omitempty"`

	// +optional
	UnreachableSince *metav1.Time `json:"unreachableSince,omitempty"`
}

type SecretKeyRef struct {
	// +kubebuilder:validation:Required
	Key string `json:"key"`

	// +kubebuilder:validation:Required
	Name string `json:"name"`
}

// +kubebuilder:validation:Enum=Deployment;StatefulSet
type WorkloadKind string

type WorkloadRef struct {
	// +kubebuilder:default=StatefulSet
	// +optional
	Kind WorkloadKind `json:"kind,omitempty"`

	// +kubebuilder:validation:Required
	Name string `json:"name"`
}
