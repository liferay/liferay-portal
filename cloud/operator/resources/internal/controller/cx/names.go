package cx

import (
	"strings"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
)

func DxpNamespace(clientExtension *cxv1alpha1.ClientExtension) string {
	if clientExtension.Spec.DxpNamespace != "" {
		return clientExtension.Spec.DxpNamespace
	}

	return clientExtension.Namespace
}

func PermittedNamespaces(namespace *corev1.Namespace) []string {
	var permittedNamespaces []string

	for _, name := range strings.Split(
		namespace.Annotations[cxv1alpha1.AnnotationAllowedClientExtensionNamespaces], ",",
	) {
		if name = strings.TrimSpace(name); name != "" {
			permittedNamespaces = append(permittedNamespaces, name)
		}
	}

	return permittedNamespaces
}
