{{- define "elasticsearch.containerSecurityContext" -}}
allowPrivilegeEscalation: false
capabilities:
    drop:
        -   ALL
privileged: false
readOnlyRootFilesystem: false
{{- end -}}

{{- define "elasticsearch.elasticUserSecretName" -}}
{{- printf "%s-es-elastic-user" (include "elasticsearch.name" .) -}}
{{- end -}}

{{- define "elasticsearch.name" -}}
{{- printf "%s-es" .Release.Name -}}
{{- end -}}

{{- define "elasticsearch.namespace" -}}
{{- printf "%s-elasticsearch" .Release.Namespace -}}
{{- end -}}

{{- define "liferay.validateDNS1123Name" -}}
{{- if not (regexMatch "^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$" .) -}}
{{- fail (printf "Name is not DNS-1123 compliant: %s" .) -}}
{{- end -}}
{{- end -}}

{{- define "liferay.validateEndpoint" -}}
{{- $externalSecretKeys := dig "tls" "externalSecretKeys" (list) .endpoint -}}
{{- if and (eq (int .endpoint.port) 80) (not .endpoint.hostname) (ne .name "http") -}}
{{- fail (printf "The %s endpoint uses port 80 without a hostname, which collides with the implicit http listener" .name) -}}
{{- end -}}
{{- if and (eq .endpoint.protocol "HTTPS") (not $externalSecretKeys) -}}
{{- fail (printf "The %s endpoint serves HTTPS and must set tls.externalSecretKeys" .name) -}}
{{- end -}}
{{- if and (ne .endpoint.protocol "HTTPS") $externalSecretKeys -}}
{{- fail (printf "The %s endpoint does not serve HTTPS and must not set tls.externalSecretKeys" .name) -}}
{{- end -}}
{{- end -}}

{{- define "liferay.validateSecretKey" -}}
{{- if not (hasPrefix .prefix .key) -}}
{{- fail (printf "Secret key must start with %s: %s" .prefix .key) -}}
{{- end -}}
{{- end -}}

{{- define "liferay.validateSecretNameSuffix" -}}
{{- if regexMatch "-[A-Za-z0-9]{6}$" . -}}
{{- fail (printf "Secret name must not end with a hyphen and six characters, because AWS Secrets Manager appends that shape to the ARN: %s" .) -}}
{{- end -}}
{{- end -}}

{{- define "search.secretReaderServiceAccountName" -}}
{{- printf "%s-search-secret-reader" .Release.Name -}}
{{- end -}}

{{- define "search.validateEngine" -}}
{{- if not (has .Values.search.engine (list "elasticsearch" "opensearch")) -}}
{{- fail (printf "Unsupported search engine: %s" .Values.search.engine) -}}
{{- end -}}
{{- end -}}