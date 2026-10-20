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

{{- define "liferay.database.deploymentEngine" -}}
{{- $global := .global | default dict -}}
{{- get ($global.database | default dict) "engine" -}}
{{- end }}

{{- define "liferay.database.engine" -}}
{{- include "liferay.database.validateEngineSwitch" . -}}
{{- include "liferay.database.deploymentEngine" . | default (get (.database | default dict) "engine") -}}
{{- end }}

{{- define "liferay.database.env" -}}
{{- if include "liferay.database.engine" . }}
{{- $customEnvNames := list }}
{{- range $k, $v := .customEnv }}
{{- range $v }}
{{- $customEnvNames = append $customEnvNames .name }}
{{- end }}
{{- end }}
{{- $profile := include "liferay.database.profile" . | fromYaml }}
{{- if and $profile.hibernateDialect (not (has "LIFERAY_HIBERNATE_PERIOD_DIALECT" $customEnvNames)) }}
-   name: LIFERAY_HIBERNATE_PERIOD_DIALECT
    value: {{ $profile.hibernateDialect }}
{{- end }}
{{- if not (has "LIFERAY_JDBC_PERIOD_DEFAULT_PERIOD_DRIVER_UPPERCASEC_LASS_UPPERCASEN_AME" $customEnvNames) }}
-   name: LIFERAY_JDBC_PERIOD_DEFAULT_PERIOD_DRIVER_UPPERCASEC_LASS_UPPERCASEN_AME
    value: {{ $profile.driverClassName }}
{{- end }}
{{- if not (has "LIFERAY_JDBC_PERIOD_DEFAULT_PERIOD_URL" $customEnvNames) }}
-   name: LIFERAY_JDBC_PERIOD_DEFAULT_PERIOD_URL
    value: {{ include "liferay.database.url" . | quote }}
{{- end }}
{{- else if (.database | default dict).host }}
{{- fail "\"database.engine\" must be set when \"database.host\" is set, or Liferay falls back to its embedded Hypersonic database" }}
{{- end }}
{{- end }}

{{- define "liferay.database.host" -}}
{{- $host := (.database | default dict).host -}}
{{- if not $host -}}
{{- fail "\"database.host\" must be set when \"database.engine\" is set" -}}
{{- end -}}
{{- $host -}}
{{- end }}

{{- define "liferay.database.parameters" -}}
{{- $profile := include "liferay.database.profile" . | fromYaml -}}
{{- $parameters := list -}}
{{- with $profile.parameters -}}
{{- $parameters = append $parameters . -}}
{{- end -}}
{{- if (.database | default dict).tlsRequired -}}
{{- $parameters = append $parameters $profile.tlsParameter -}}
{{- end -}}
{{- join "&" $parameters -}}
{{- end }}

{{- define "liferay.database.port" -}}
{{- $profile := include "liferay.database.profile" . | fromYaml -}}
{{- (.database | default dict).port | default $profile.port | toString -}}
{{- end }}

{{- define "liferay.database.profile" -}}
{{- $engine := include "liferay.database.engine" . -}}
{{- $profile := get (include "liferay.database.profiles" . | fromYaml) $engine -}}
{{- if not $profile -}}
{{- fail (printf "Database engine %q is not supported. Set \"database.engine\" to \"mysql\" or \"postgresql\"." $engine) -}}
{{- end -}}
{{- toYaml $profile -}}
{{- end }}

{{- define "liferay.database.profiles" -}}
mysql:
    driverClassName: org.mariadb.jdbc.Driver
    hibernateDialect: org.hibernate.dialect.MySQL8Dialect
    port: "3306"
    scheme: mariadb
    tlsParameter: sslMode=trust
postgresql:
    driverClassName: org.postgresql.Driver
    parameters: useUnicode=true&characterEncoding=UTF-8&useFastDateParsing=false
    port: "5432"
    scheme: postgresql
    tlsParameter: sslmode=require
{{- end }}

{{- define "liferay.database.url" -}}
{{- $profile := include "liferay.database.profile" . | fromYaml -}}
{{- $url := printf "jdbc:%s://%s:%s/lportal" $profile.scheme (include "liferay.database.host" .) (include "liferay.database.port" .) -}}
{{- with include "liferay.database.parameters" . -}}
{{- $url = printf "%s?%s" $url . -}}
{{- end -}}
{{- $url -}}
{{- end }}

{{- define "liferay.database.validateEngineSwitch" -}}
{{- $deploymentEngine := include "liferay.database.deploymentEngine" . -}}
{{- $engine := get (.database | default dict) "engine" -}}
{{- if and $deploymentEngine $engine (ne $deploymentEngine $engine) -}}
{{- fail (printf "Unable to set \"database.engine\" to %q because \"global.database.engine\" sets the database engine of the deployment to %q" $engine $deploymentEngine) -}}
{{- end -}}
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

{{- define "liferay.servicePort" -}}
{{- $portName := .portName -}}
{{- range .ports -}}
{{- if eq .name $portName -}}{{ .port }}{{- end -}}
{{- end -}}
{{- end }}