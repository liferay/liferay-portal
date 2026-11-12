{{- define "backupRestore.databaseClientImage" -}}
{{- $image := get .Values.images (get (dict "mysql" "mysql" "postgresql" "postgres") (include "backupRestore.databaseEngine" .)) -}}
{{- printf "%s:%s" $image.repository $image.tag -}}
{{- end -}}

{{- define "backupRestore.databaseEngine" -}}
{{- if not (has .Values.database.engine (list "mysql" "postgresql")) -}}
{{- fail (printf "Database engine %q is not supported. Set \"database.engine\" to \"mysql\" or \"postgresql\"." .Values.database.engine) -}}
{{- end -}}
{{- .Values.database.engine -}}
{{- end -}}