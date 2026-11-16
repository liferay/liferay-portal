{{- define "liferay-client-extension.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "liferay-client-extension.configs" -}}
{{- $descriptor := include "liferay-client-extension.descriptor" . | fromJson -}}
{{- $configs := deepCopy (.Values.clientExtension.configs | default dict) -}}
{{- toJson (merge $configs (get $descriptor "configs" | default dict)) -}}
{{- end }}

{{- define "liferay-client-extension.descriptor" -}}
{{- $descriptor := .Values.descriptor -}}
{{- if not $descriptor -}}
{}
{{- else -}}
{{- if not (kindIs "map" $descriptor) -}}
{{- fail "descriptor must be an object" -}}
{{- end -}}
{{- if ne (toString (get $descriptor "descriptorVersion")) "1" -}}
{{- fail (printf "descriptor version %v is not supported, expected 1" (get $descriptor "descriptorVersion")) -}}
{{- end -}}
{{- $type := get $descriptor "type" -}}
{{- if not (has $type (list "batch" "configuration" "frontend" "service")) -}}
{{- fail (printf "descriptor type %v is not supported, expected batch, configuration, frontend, or service" $type) -}}
{{- end -}}
{{- if not (kindIs "map" (get $descriptor "configs" | default dict)) -}}
{{- fail "descriptor configs must be an object" -}}
{{- end -}}
{{- if not (kindIs "map" (get $descriptor "image" | default dict)) -}}
{{- fail "descriptor image must be an object" -}}
{{- end -}}
{{- toJson $descriptor -}}
{{- end -}}
{{- end }}

{{- define "liferay-client-extension.descriptorType" -}}
{{- get (include "liferay-client-extension.descriptor" . | fromJson) "type" -}}
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

{{- define "liferay-client-extension.image" -}}
{{- $image := get (include "liferay-client-extension.descriptor" . | fromJson) "image" | default dict -}}
{{- $repository := .Values.image.repository | default (get $image "repository") -}}
{{- $tag := .Values.image.tag -}}
{{- if not $tag -}}
{{- $tag = get $image "tag" -}}
{{- with get $image "digest" -}}
{{- $tag = printf "%s@%s" $tag . -}}
{{- end -}}
{{- end -}}
{{- printf "%s:%s" (required "image.repository is required" $repository) (required "image.tag is required" $tag) -}}
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
{{- range $configurationKey, $_ := include "liferay-client-extension.configs" . | fromJson }}
{{- if regexMatch "^com\\.liferay\\.oauth2\\.provider\\.configuration\\.OAuth2ProviderApplication(HeadlessServer|UserAgent)Configuration(~[^/]+|_[^~/]+|-[^~_/]+)(/.*)?$" $configurationKey -}}
true
{{- end }}
{{- end }}
{{- end }}

{{- define "liferay-client-extension.selectorLabels" -}}
app.kubernetes.io/instance: {{ include "liferay-client-extension.fullname" . }}
app.kubernetes.io/name: {{ include "liferay-client-extension.serviceId" . | quote }}
{{- end }}

{{- define "liferay-client-extension.serviceEnabled" -}}
{{- if kindIs "bool" .Values.service.enabled -}}
{{- if .Values.service.enabled -}}
true
{{- end -}}
{{- else if and (include "liferay-client-extension.descriptorType" .) (include "liferay-client-extension.workloadEnabled" .) (eq (include "liferay-client-extension.workloadKind" .) "Deployment") -}}
true
{{- end -}}
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

{{- define "liferay-client-extension.workloadEnabled" -}}
{{- if kindIs "bool" .Values.workload.enabled -}}
{{- if .Values.workload.enabled -}}
true
{{- end -}}
{{- else if ne (include "liferay-client-extension.descriptorType" .) "configuration" -}}
true
{{- end -}}
{{- end }}

{{- define "liferay-client-extension.workloadKind" -}}
{{- if .Values.workload.kind -}}
{{- .Values.workload.kind -}}
{{- else if eq (include "liferay-client-extension.descriptorType" .) "batch" -}}
Job
{{- else -}}
Deployment
{{- end -}}
{{- end }}

{{- define "liferay-client-extension.workloadName" -}}
{{- $fullname := include "liferay-client-extension.fullname" . }}
{{- if eq (include "liferay-client-extension.workloadKind" .) "Job" }}
{{- $podTemplate := print (include "liferay-client-extension.podTemplateMetadata" .) (include "liferay-client-extension.podSpec" .) }}
{{- printf "%s-%s" ($fullname | trunc 54 | trimSuffix "-") ($podTemplate | sha256sum | trunc 8) }}
{{- else }}
{{- $fullname }}
{{- end }}
{{- end }}