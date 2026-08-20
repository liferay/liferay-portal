{{- define "liferay-platform.argocdExternalURL" -}}
{{- if .Values.argocd.gateway.enabled -}}
{{- $scheme := ternary "https" "http" (ne .Values.argocd.gateway.tls.externalSecretKey "") -}}
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
{{- printf "%s-secret-store" .Values.deploymentContext.deploymentName -}}
{{- end -}}

{{- define "liferay-platform.crossplaneDeploymentRuntimeConfigAnnotations" -}}
instrumentation.opentelemetry.io/inject-dotnet: "false"
instrumentation.opentelemetry.io/inject-java: "false"
instrumentation.opentelemetry.io/inject-nodejs: "false"
instrumentation.opentelemetry.io/inject-python: "false"
sidecar.opentelemetry.io/inject: "false"
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