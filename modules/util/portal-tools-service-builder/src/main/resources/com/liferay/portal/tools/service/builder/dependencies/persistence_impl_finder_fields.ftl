<#if entityFinder.where?? && entityFinder.DBWhere?? && (entityFinder.where != entityFinder.DBWhere)>
	<#assign entityFinderDBWhere = true />
<#else>
	<#assign entityFinderDBWhere = false />
</#if>

<#assign entityColumns = entityFinder.entityColumns />

<#list entityColumns as entityColumn>
	<#assign entityColumnName = entityColumn.name finderFieldSuffix = "" />

	<#if !serviceBuilder.isVersionGTE_7_4_0() || (entityFinder.hasArrayableOperator() && !entityFinder.collectionPersistenceFinderEnabled)>
		<#include "persistence_impl_finder_field.ftl">

		<#if entity.isPermissionCheckEnabled(entityFinder) && !entity.isPermissionedModel() && ((entityColumn.name != entityColumn.DBName) || entityFinderDBWhere)>
			<#assign entityColumnName = entityColumn.DBName finderFieldSuffix = finderFieldSQLSuffix />

			<#include "persistence_impl_finder_field.ftl">
		</#if>
	</#if>
</#list>