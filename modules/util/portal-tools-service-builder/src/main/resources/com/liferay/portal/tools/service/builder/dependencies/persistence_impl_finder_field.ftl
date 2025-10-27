<#assign finderColConjunction = "" />

<#if entityColumn_has_next>
	<#assign finderColConjunction = " AND " />
<#elseif validator.isNull(finderFieldSuffix) && entityFinder.where?? && validator.isNotNull(entityFinder.getWhere())>
	<#assign finderColConjunction = " AND " + entityFinder.where />
<#elseif validator.isNotNull(finderFieldSuffix) && entityFinder.DBWhere?? && validator.isNotNull(entityFinder.getDBWhere())>
	<#assign finderColConjunction = " AND " + entityFinder.DBWhere />
</#if>

<#if entity.hasCompoundPK() && entityColumn.isPrimary()>
	<#assign finderFieldName = entity.alias + ".primaryKey." + entityColumnName />
<#else>
	<#assign finderFieldName = entity.alias + "." + entityColumnName />
</#if>

<#if serviceBuilder.getSqlType(entity.getName(), entityColumn) == "CLOB">
	<#assign textFinderFieldName = "CAST_CLOB_TEXT(" + finderFieldName + ")" />
<#else>
	<#assign textFinderFieldName = finderFieldName />
</#if>

<#if !entityColumn.isPrimitiveType() && !(stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull())>
	private static final String _FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_1${finderFieldSuffix} =

	<#if (entityColumn.comparator == "<>") || (entityColumn.comparator == "!=")>
		"${finderFieldName} IS NOT NULL${finderColConjunction}"
	<#else>
		"${finderFieldName} IS NULL${finderColConjunction}"
	</#if>

	;
</#if>

<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isCaseSensitive()>
	<#assign finderColExpression = "lower(" + textFinderFieldName + ") " + entityColumn.comparator + " ?" />
<#else>
	<#assign finderColExpression = textFinderFieldName + " " + entityColumn.comparator + " ?" />
</#if>

private static final String _FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_2${finderFieldSuffix} = "${finderColExpression}${finderColConjunction}";

<#if stringUtil.equals(entityColumn.type, "String")>
	<#assign finderColExpression = textFinderFieldName + " " + entityColumn.comparator + " ''" />

	private static final String _FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_3${finderFieldSuffix} = "(${finderFieldName} IS NULL OR ${finderColExpression})${finderColConjunction}";
</#if>

<#if entityColumn.hasArrayableOperator() && validator.isNotNull(finderColConjunction) && stringUtil.equals(entityColumn.type, "String")>
	<#if !entityColumn.isConvertNull()>
		private static final String _FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_4${finderFieldSuffix} = "(" + removeConjunction(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_1) + ")";
	</#if>

	private static final String _FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_5${finderFieldSuffix} = "(" + removeConjunction(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_2) + ")";

	private static final String _FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_6${finderFieldSuffix} = "(" + removeConjunction(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_3) + ")";
</#if>

<#if entityColumn.hasArrayableOperator() && !stringUtil.equals(entityColumn.type, "String")>
	private static final String _FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_7${finderFieldSuffix} = "${finderFieldName}<#if entityColumn.isArrayableAndOperator()> NOT IN (<#else> IN (</#if>";
</#if>