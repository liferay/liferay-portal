{{- define "liferay-client-extension.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "liferay-client-extension.dxpMetadataName" -}}
{{- printf "%s-lxc-dxp-metadata" .Values.clientExtension.virtualInstanceId }}
{{- end }}

{{- define "liferay-client-extension.extInitName" -}}
{{- printf "%s-%s-lxc-ext-init-metadata" (include "liferay-client-extension.serviceId" .) .Values.clientExtension.virtualInstanceId }}
{{- end }}

{{- define "liferay-client-extension.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{- define "liferay-client-extension.labels" -}}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ include "liferay-client-extension.chart" . }}
{{ include "liferay-client-extension.selectorLabels" . }}
{{- with .Values.labels }}
{{ toYaml . }}
{{- end }}
{{- end }}

{{- define "liferay-client-extension.requiresExtInit" -}}
{{- range $configurationKey, $_ := .Values.clientExtension.configs }}
{{- if regexMatch "^com\\.liferay\\.oauth2\\.provider\\.configuration\\.OAuth2ProviderApplication(HeadlessServer|UserAgent)Configuration(~[^/]+|_[^~/]+|-[^~_/]+)(/.*)?$" $configurationKey -}}
true
{{- end }}
{{- end }}
{{- end }}

{{- define "liferay-client-extension.selectorLabels" -}}
app.kubernetes.io/instance: {{ include "liferay-client-extension.fullname" . }}
app.kubernetes.io/name: {{ include "liferay-client-extension.serviceId" . | quote }}
{{- end }}

{{- define "liferay-client-extension.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "liferay-client-extension.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "liferay-client-extension.serviceId" -}}
{{- required "clientExtension.serviceId is required" .Values.clientExtension.serviceId }}
{{- end }}

{{- define "liferay-client-extension.workloadName" -}}
{{- $fullname := include "liferay-client-extension.fullname" . }}
{{- if eq .Values.workload.kind "Job" }}
{{- $podTemplate := print (include "liferay-client-extension.podTemplateMetadata" .) (include "liferay-client-extension.podSpec" .) }}
{{- printf "%s-%s" ($fullname | trunc 54 | trimSuffix "-") ($podTemplate | sha256sum | trunc 8) }}
{{- else }}
{{- $fullname }}
{{- end }}
{{- end }}