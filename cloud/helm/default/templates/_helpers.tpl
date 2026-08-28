{{- define "liferay.activationCodeSecretName" -}}
{{- .Values.licensing.activationCodeSecretName | default (printf "%s-activation" (include "liferay.name" .)) }}
{{- end }}

{{- define "liferay.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "liferay.customLabels" -}}
{{- with .Values.customLabels }}
{{ toYaml . }}
{{- end }}
{{- end }}

{{- define "liferay.keda.prometheusServerAddress" -}}
{{- tpl (.Values.autoscaling.keda.prometheusServerAddress | default "") . -}}
{{- end }}

{{- define "liferay.fullname" -}}
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

{{- define "liferay.hostnameSlug" -}}
{{- $hostname := . | lower -}}
{{- $sanitized := $hostname | replace "*" "wildcard" | replace "." "-" | trimPrefix "-" | trimSuffix "-" -}}
{{- if gt (len $sanitized) 50 -}}
{{- $hash := sha256sum $hostname | trunc 8 -}}
{{- $sanitized = printf "%s-%s" (trunc 41 $sanitized | trimSuffix "-") $hash -}}
{{- end -}}
{{- $sanitized -}}
{{- end -}}

{{- define "liferay.labels" -}}
{{ include "liferay.selectorLabels" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
component: liferay
helm.sh/chart: {{ include "liferay.chart" . }}
{{- include "liferay.customLabels" . }}
{{- end }}

{{- define "liferay.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "liferay.namespace" -}}
{{- default .Release.Namespace .Values.namespaceOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "liferay.selectorLabels" -}}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/name: {{ include "liferay.name" . }}
{{- end }}

{{- define "liferay.serviceAccountName" -}}
{{- if .Values.global.liferayServiceAccount.create }}
{{- default (include "liferay.name" .) .Values.global.liferayServiceAccount.name }}
{{- else }}
{{- default "default" .Values.global.liferayServiceAccount.name }}
{{- end }}
{{- end }}