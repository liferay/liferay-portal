{{- define "database.validateAuthentication" -}}
{{- if not (has .Values.database.authentication (list "password")) -}}
{{- fail (printf "Unsupported database authentication: %s" .Values.database.authentication) -}}
{{- end -}}
{{- end -}}

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

{{- define "search.secretReaderServiceAccountName" -}}
{{- printf "%s-search-secret-reader" .Release.Name -}}
{{- end -}}

{{- define "search.validateEngine" -}}
{{- if not (has .Values.search.engine (list "elasticsearch" "opensearch")) -}}
{{- fail (printf "Unsupported search engine: %s" .Values.search.engine) -}}
{{- end -}}
{{- end -}}