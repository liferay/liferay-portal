package v1alpha1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func init() {
	SchemeBuilder.Register(&LiferayEnvironment{}, &LiferayEnvironmentList{})
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
// +kubebuilder:printcolumn:JSONPath=`.status.phase`,name="Phase",type=string
// +kubebuilder:printcolumn:JSONPath=`.status.conditions[?(@.type=="Activated")].status`,name="Activated",type=string
// +kubebuilder:printcolumn:JSONPath=`.metadata.creationTimestamp`,name="Age",type=date
// +kubebuilder:printcolumn:JSONPath=`.status.environmentId`,name="Environment-ID",priority=1,type=string
// +kubebuilder:printcolumn:JSONPath=`.status.activatedAt`,name="Activated-At",priority=1,type=date
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
	DxpVersion string `json:"dxpVersion,omitempty"`

	// +optional
	EnvironmentName string `json:"environmentName,omitempty"`

	// +kubebuilder:validation:Required
	WorkloadRef WorkloadRef `json:"workloadRef"`
}

type LiferayEnvironmentStatus struct {
	// +optional
	ActivatedAt *metav1.Time `json:"activatedAt,omitempty"`

	// +listMapKey=type
	// +listType=map
	// +optional
	Conditions []metav1.Condition `json:"conditions,omitempty"`

	// +optional
	EnvironmentID string `json:"environmentId,omitempty"`

	// +optional
	License LicenseStatus `json:"license,omitempty"`

	// +kubebuilder:validation:Enum=Degraded;Pending;Ready
	// +optional
	Phase string `json:"phase,omitempty"`
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
