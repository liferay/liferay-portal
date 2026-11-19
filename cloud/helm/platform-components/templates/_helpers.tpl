{{- define "liferay-platform.argocdExternalURL" -}}
{{- if .Values.argocd.gateway.enabled -}}
{{- $tls := .Values.argocd.gateway.tls -}}
{{- $scheme := ternary "https" "http" (or $tls.externalSecretKey $tls.secretName | empty | not) -}}
{{- printf "%s://%s" $scheme .Values.argocd.gateway.hostname -}}
{{- end -}}
{{- end -}}

{{- define "liferay-platform.chartSource" -}}
{{- if hasPrefix "oci://" .repoURL -}}
path: .
{{- else if .path -}}
path: {{ .path }}
{{- else -}}
chart: {{ .chart }}
{{- end }}
repoURL: {{ .repoURL }}
targetRevision: {{ .targetRevision | quote }}
{{- end -}}

{{- define "liferay-platform.clusterSecretStoreName" -}}
{{- $clusterSecretStoreNames := list -}}
{{- range $name, $clusterSecretStore := .Values.clusterSecretStores -}}
{{- if or $clusterSecretStore.default (eq (len $.Values.clusterSecretStores) 1) -}}
{{- $clusterSecretStoreNames = append $clusterSecretStoreNames $name -}}
{{- end -}}
{{- end -}}
{{- if ne (len $clusterSecretStoreNames) 1 -}}
{{- fail "The value \"clusterSecretStores\" must define one store, or mark exactly one store with \"default: true\"" -}}
{{- end -}}
{{- first $clusterSecretStoreNames -}}
{{- end -}}

{{- define "liferay-platform.crossplaneDeploymentRuntimeConfigAnnotations" -}}
instrumentation.opentelemetry.io/inject-dotnet: "false"
instrumentation.opentelemetry.io/inject-java: "false"
instrumentation.opentelemetry.io/inject-nodejs: "false"
instrumentation.opentelemetry.io/inject-python: "false"
sidecar.opentelemetry.io/inject: "false"
{{- end -}}

{{- define "liferay-platform.dxpOperatorServiceAccountName" -}}
{{- $values := .Values.dxpOperator.values | default dict -}}
{{- dig "serviceAccount" "name" "" $values | default (dig "fullnameOverride" "" $values) | required (printf "The value %q must not be empty when %q is true"
    "dxpOperator.values.fullnameOverride" "dxpOperator.values.clientExtensions.enabled") -}}
{{- end -}}

{{- define "liferay-platform.environmentNamespaceLabels" -}}
licensing.liferay.com/environment: "true"
liferay.com/observable: "true"
pod-security.kubernetes.io/enforce: restricted
{{- end -}}

{{- define "liferay-platform.infrastructureRepositoryRevision" -}}
{{- .Values.gitops.infrastructureRepository.revision | default .Values.gitops.repository.revision -}}
{{- end -}}

{{- define "liferay-platform.infrastructureRepositoryURL" -}}
{{- .Values.gitops.infrastructureRepository.url | default .Values.gitops.repository.url -}}
{{- end -}}

{{- define "liferay-platform.labels" -}}
liferay.com/project: liferay-cloud-native
{{- end -}}

{{- define "liferay-platform.operators" -}}
ackPrometheusService:
    name: ack-prometheusservice
argoWorkflows:
    chart: argo-workflows
    name: argo-workflows
crossplane:
    chart: crossplane
    name: crossplane
    namespaceLabels:
        pod-security.kubernetes.io/enforce: restricted
dxpOperator:
    name: dxp-operator
    namespaceLabels:
        pod-security.kubernetes.io/enforce: restricted
eckOperator:
    chart: eck-operator
    name: eck-operator
    namespaceLabels:
        pod-security.kubernetes.io/enforce: restricted
envoyGateway:
    name: envoy-gateway
externalSecrets:
    chart: external-secrets
    name: external-secrets
keda:
    chart: keda
    extraDestinationNamespaces:
        -   kube-system
    name: keda
{{- end -}}

{{- define "liferay-platform.requireAPIServerSources" -}}
{{- if not .apiServerSources -}}
{{- fail (printf "The value \"networkPolicy.apiServerSources\" must not be empty when \"%s.enabled\" is true" .operatorApplication) -}}
{{- end -}}
{{- end -}}

{{- define "liferay-platform.sourceRepoBase" -}}
{{- if hasPrefix "oci://" . -}}
{{- $parts := splitList "/" (trimPrefix "oci://" .) -}}
{{- if gt (len $parts) 3 -}}
{{- printf "oci://%s" (join "/" (slice $parts 0 3)) -}}
{{- else -}}
{{- . -}}
{{- end -}}
{{- else -}}
{{- . -}}
{{- end -}}
{{- end -}}

{{- define "liferay-platform.sourceRepos" -}}
{{- $sourceRepos := list -}}
{{- range $url := . -}}
{{- if $url -}}
{{- $base := include "liferay-platform.sourceRepoBase" $url -}}
{{- $sourceRepos = append $sourceRepos $base -}}
{{- $sourceRepos = append $sourceRepos (printf "%s/**" $base) -}}
{{- end -}}
{{- end -}}
{{- toYaml (uniq $sourceRepos) -}}
{{- end -}}