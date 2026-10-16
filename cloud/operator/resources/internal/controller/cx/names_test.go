package cx

import (
	"slices"
	"testing"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	apiextensionsv1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func TestAllowedNamespacesReadsConfigMap(t *testing.T) {
	testCases := map[string]struct {
		data map[string]string
		want []string
	}{
		"an empty key permits nothing": {
			data: map[string]string{
				cxv1alpha1.ConfigMapKeyAllowedNamespaces: "",
			},
		},
		"blank entries and spaces are ignored": {
			data: map[string]string{
				cxv1alpha1.ConfigMapKeyAllowedNamespaces: " able, ,baker ,",
			},
			want: []string{"able", "baker"},
		},
		"no key permits nothing": {},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			configMap := &corev1.ConfigMap{
				Data:       testCase.data,
				ObjectMeta: metav1.ObjectMeta{Name: cxv1alpha1.ConfigMapNameClientExtensionPolicy, Namespace: "liferay-prod"},
			}

			if got := allowedNamespaces(configMap); !slices.Equal(got, testCase.want) {
				t.Errorf("allowedNamespaces() = %q, want %q", got, testCase.want)
			}
		})
	}
}

func TestEffectiveDxpNamespaceDefaultsToClientExtensionNamespace(t *testing.T) {
	testCases := map[string]struct {
		dxpNamespace string
		want         string
	}{
		"an empty dxpNamespace is the client extension namespace": {
			want: "able",
		},
		"dxpNamespace is used when set": {
			dxpNamespace: "liferay-prod",
			want:         "liferay-prod",
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension(testCase.dxpNamespace, "sample", "able")

			if got := effectiveDxpNamespace(clientExtension); got != testCase.want {
				t.Errorf("effectiveDxpNamespace() = %q, want %q", got, testCase.want)
			}
		})
	}
}

func TestExtInitIdentifiersReadsOAuth2Configurations(t *testing.T) {
	testCases := map[string]struct {
		configurationKeys []string
		want              []string
	}{
		"a CET configuration beside a user agent application yields the application": {
			configurationKeys: []string{
				"com.liferay.client.extension.type.configuration.CETConfiguration~able",
				"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration~able-oua",
			},
			want: []string{"able-oua"},
		},
		"a CET configuration yields no application": {
			configurationKeys: []string{"com.liferay.client.extension.type.configuration.CETConfiguration~able"},
		},
		"a PID without a separator yields no application": {
			configurationKeys: []string{"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration"},
		},
		"a headless server application yields its identifier": {
			configurationKeys: []string{
				"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationHeadlessServerConfiguration~able-ohs",
			},
			want: []string{"able-ohs"},
		},
		"a virtual instance suffix is not part of the identifier": {
			configurationKeys: []string{
				"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationHeadlessServerConfiguration~able-ohs/able.liferay.cloud",
			},
			want: []string{"able-ohs"},
		},
		"an underscore separates the identifier when there is no tilde": {
			configurationKeys: []string{
				"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration_able-oua",
			},
			want: []string{"able-oua"},
		},
		"every application yields its identifier in order": {
			configurationKeys: []string{
				"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration~baker-oua",
				"com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationHeadlessServerConfiguration~able-ohs",
			},
			want: []string{"able-ohs", "baker-oua"},
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension("", "able", "able")

			clientExtension.Spec.Configs = make(map[string]cxv1alpha1.Configuration)

			for _, configurationKey := range testCase.configurationKeys {
				clientExtension.Spec.Configs[configurationKey] = cxv1alpha1.Configuration{
					JSON: apiextensionsv1.JSON{Raw: []byte(`{}`)},
				}
			}

			if got := extInitIdentifiers(clientExtension); !slices.Equal(got, testCase.want) {
				t.Errorf("extInitIdentifiers() = %v, want %v", got, testCase.want)
			}
		})
	}
}
