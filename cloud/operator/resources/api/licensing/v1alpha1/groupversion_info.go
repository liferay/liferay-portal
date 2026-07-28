// +groupName=licensing.liferay.com
// +kubebuilder:object:generate=true
package v1alpha1

import (
	schema "k8s.io/apimachinery/pkg/runtime/schema"
	scheme "sigs.k8s.io/controller-runtime/pkg/scheme"
)

var (
	SchemeBuilder = &scheme.Builder{
		GroupVersion: schema.GroupVersion{
			Group:   "licensing.liferay.com",
			Version: "v1alpha1",
		},
	}

	AddToScheme = SchemeBuilder.AddToScheme
)
