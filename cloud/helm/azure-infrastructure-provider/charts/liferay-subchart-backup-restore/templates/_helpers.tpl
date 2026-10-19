{{- define "backupRestore.databaseApiGroup" -}}
{{- get (dict "mysql" "dbformysql.azure.m.upbound.io" "postgresql" "dbforpostgresql.azure.m.upbound.io") (include "backupRestore.databaseEngine" .) -}}
{{- end -}}

{{- define "backupRestore.databaseCommand" -}}
{{- get (dict "mysql" "mysql" "postgresql" "postgres") (include "backupRestore.databaseEngine" .) -}}
{{- end -}}

{{- define "backupRestore.databaseEngine" -}}
{{- if not (has .Values.database.engine (list "mysql" "postgresql")) -}}
{{- fail (printf "Database engine %q is not supported. Set \"database.engine\" to \"mysql\" or \"postgresql\"." .Values.database.engine) -}}
{{- end -}}
{{- .Values.database.engine -}}
{{- end -}}