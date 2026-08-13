package v1alpha1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func init() {
	SchemeBuilder.Register(&LiferayEnvironment{}, &LiferayEnvironmentList{})
}

type AppStatus struct {
	// +optional
	Checksum string `json:"checksum,omitempty"`

	// +optional
	Name string `json:"name,omitempty"`

	// +kubebuilder:validation:Enum=Downloaded;Failed
	// +optional
	State string `json:"state,omitempty"`

	// +optional
	VirtualEntryID int64 `json:"virtualEntryId,omitempty"`
}

type LicenseStatus struct {
	// +optional
	Checksum string `json:"checksum,omitempty"`

	// +optional
	LastVerified *metav1.Time `json:"lastVerified,omitempty"`

	// +optional
	MaxClusterNodes int32 `json:"maxClusterNodes,omitempty"`

	// +optional
	ValidUntil *metav1.Time `json:"validUntil,omitempty"`
}

// +kubebuilder:object:root=true
// +kubebuilder:printcolumn:JSONPath=`.spec.workloadRef.name`,name="Workload",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.conditions[?(@.type=="Activated")].status`,name="Activated",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.license.maxClusterNodes`,name="Max",type=integer
// +kubebuilder:printcolumn:JSONPath=`.status.license.validUntil`,name="Valid-Until",type=string
// +kubebuilder:printcolumn:JSONPath=`.spec.desiredReplicas`,name="Desired",type=integer
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
	DesiredReplicas *int32 `json:"desiredReplicas,omitempty"`

	// +optional
	DxpVersion string `json:"dxpVersion,omitempty"`

	// +optional
	EnvironmentName string `json:"environmentName,omitempty"`

	// +optional
	Offline bool `json:"offline,omitempty"`

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
	UnreachableSince *metav1.Time `json:"unreachableSince,omitempty"`
}

type SecretKeyRef struct {
	// +kubebuilder:validation:Required
	Key string `json:"key"`

	// +kubebuilder:validation:Required
	Name string `json:"name"`
}

type WorkloadRef struct {
	// +kubebuilder:validation:Required
	Name string `json:"name"`
}
