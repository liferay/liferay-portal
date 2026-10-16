package cx

import (
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"slices"
	"strings"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	AnnotationMainDomain     = "ext.lxc.liferay.com/mainDomain"
	AnnotationOwnerName      = "cx.liferay.com/owner-name"
	AnnotationOwnerNamespace = "cx.liferay.com/owner-namespace"
	AnnotationSource         = "cx.liferay.com/source"
)

const (
	LabelMetadataType    = "lxc.liferay.com/metadataType"
	LabelMirror          = "cx.liferay.com/mirror"
	LabelOwner           = "cx.liferay.com/owner"
	LabelServiceID       = "ext.lxc.liferay.com/serviceId"
	LabelVirtualInstance = "dxp.lxc.liferay.com/virtualInstanceId"
)

const (
	MetadataTypeDxp          = "dxp"
	MetadataTypeExtInit      = "ext-init"
	MetadataTypeExtProvision = "ext-provision"
)

func allowedNamespaces(configMap *corev1.ConfigMap) []string {
	var allowedNamespaces []string

	for _, name := range strings.Split(
		configMap.Data[cxv1alpha1.ConfigMapKeyAllowedNamespaces], ",",
	) {
		if name = strings.TrimSpace(name); name != "" {
			allowedNamespaces = append(allowedNamespaces, name)
		}
	}

	return allowedNamespaces
}

func dxpMetadataName(virtualInstanceID string) string {
	return virtualInstanceID + "-lxc-dxp-metadata"
}

func effectiveDxpNamespace(clientExtension *cxv1alpha1.ClientExtension) string {
	if clientExtension.Spec.DxpNamespace != "" {
		return clientExtension.Spec.DxpNamespace
	}

	return clientExtension.Namespace
}

func extInitIdentifiers(clientExtension *cxv1alpha1.ClientExtension) []string {
	var identifiers []string

	for configurationKey := range clientExtension.Spec.Configs {
		pid, _, _ := strings.Cut(configurationKey, "/")

		for _, separator := range []string{"~", "_", "-"} {
			index := strings.Index(pid, separator)

			if index <= 0 {
				continue
			}

			if !slices.Contains(extInitFactoryConfigurationIDs, pid[:index]) {
				break
			}

			if identifier := pid[index+1:]; identifier != "" {
				identifiers = append(identifiers, identifier)
			}

			break
		}
	}

	slices.Sort(identifiers)

	return slices.Compact(identifiers)
}

func extInitName(clientExtension *cxv1alpha1.ClientExtension) string {
	return fmt.Sprintf(
		"%s-%s-lxc-ext-init-metadata",
		clientExtension.Spec.ServiceID, clientExtension.Spec.VirtualInstanceID,
	)
}

func extProvisionName(clientExtension *cxv1alpha1.ClientExtension) string {
	return fmt.Sprintf(
		"%s-%s-lxc-ext-provision-metadata",
		clientExtension.Spec.ServiceID, clientExtension.Spec.VirtualInstanceID,
	)
}

func ownerLabelValue(clientExtension *cxv1alpha1.ClientExtension) string {
	sum := sha256.Sum256([]byte(clientExtension.Namespace + "/" + clientExtension.Name))

	return hex.EncodeToString(sum[:16])
}

func ownsExtProvision(clientExtension *cxv1alpha1.ClientExtension, configMap *corev1.ConfigMap) bool {
	return (clientExtension.Name == configMap.Annotations[AnnotationOwnerName]) &&
		(clientExtension.Namespace == configMap.Annotations[AnnotationOwnerNamespace])
}

func ownsMirror(clientExtension *cxv1alpha1.ClientExtension, configMap *corev1.ConfigMap) bool {
	return slices.ContainsFunc(
		configMap.OwnerReferences,
		func(ownerReference metav1.OwnerReference) bool {
			return clientExtension.UID == ownerReference.UID
		},
	)
}

func ownsMirrorAlone(clientExtension *cxv1alpha1.ClientExtension, configMap *corev1.ConfigMap) bool {
	return (len(configMap.OwnerReferences) > 0) &&
		!slices.ContainsFunc(
			configMap.OwnerReferences,
			func(ownerReference metav1.OwnerReference) bool {
				return clientExtension.UID != ownerReference.UID
			},
		)
}

var extInitFactoryConfigurationIDs = []string{
	"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationHeadlessServerConfiguration",
	"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration",
}
