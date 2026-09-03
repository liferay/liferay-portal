{{- define "elasticsearch.name" -}}
{{- .Values.search.elasticsearch.name | default (printf "%s-es" .Release.Name) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "liferay.k8sFriendlyString" -}}
{{- $sanitized := . | lower | replace "/" "-" | replace "_" "-" | trimPrefix "-" | trunc 63 | trimSuffix "-" -}}
{{- if or (empty $sanitized) (not (regexMatch "^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$" $sanitized)) -}}
{{- fail (printf "Name is not DNS-1123 compliant: %s" .) -}}
{{- end -}}
{{- $sanitized -}}
{{- end -}}

{{- define "liferay.getFullCertificateSecretName" -}}
{{- $trimmed := (. | trimPrefix "/") -}}
{{- if hasPrefix "liferay-certificates-" $trimmed }}
{{- $trimmed -}}
{{- else }}
{{- printf "liferay-certificates-%s" $trimmed -}}
{{- end }}
{{- end -}}

{{- define "liferay.getFullLicenseSecretName" -}}
{{- $trimmed := (. | trimPrefix "/") -}}
{{- if hasPrefix "liferay-licenses-" $trimmed -}}
{{- $trimmed -}}
{{- else }}
{{- printf "liferay-licenses-%s" $trimmed -}}
{{- end }}
{{- end -}}

{{- define "liferay.validateSecretNameSuffix" -}}
{{- if regexMatch "-[A-Za-z0-9]{6}$" . -}}
{{- fail (printf "Secret name must not end with a hyphen and six characters, because AWS Secrets Manager appends that shape to the ARN: %s" .) -}}
{{- end -}}
{{- end -}}