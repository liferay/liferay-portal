{{- define "database.crossplaneProvider" -}}
{{- get (dict "mysql" "dbformysql" "postgresql" "dbforpostgresql") .Values.database.engine -}}
{{- end -}}

{{- define "database.flexibleServerType" -}}
{{- get (dict "mysql" "Microsoft.DBforMySQL/flexibleServers" "postgresql" "Microsoft.DBforPostgreSQL/flexibleServers") .Values.database.engine -}}
{{- end -}}

{{- define "network.databasePrivateDnsZoneName" -}}
{{- get (dict "mysql" "privatelink.mysql.database.azure.com" "postgresql" "privatelink.postgres.database.azure.com") .Values.database.engine -}}
{{- end -}}

{{- define "network.databaseSubnetName" -}}
{{- printf "%s-database-subnet" .Values.deploymentContext.deploymentName -}}
{{- end -}}

{{- define "network.virtualNetworkId" -}}
{{- printf "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/virtualNetworks/%s" .Values.deploymentContext.subscriptionId .Values.deploymentContext.resourceGroupName (include "network.virtualNetworkName" .) -}}
{{- end -}}

{{- define "network.virtualNetworkName" -}}
{{- printf "%s-vnet" .Values.deploymentContext.deploymentName -}}
{{- end -}}