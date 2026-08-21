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

{{- define "liferay.validateSecretKey" -}}
{{- if not (hasPrefix .prefix .key) -}}
{{- fail (printf "Secret key must start with %s: %s" .prefix .key) -}}
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