<#assign hasEntityFinderWhereClauses = serviceBuilder.isVersionGTE_7_4_0() && entityFinderWhereClauses?? && (entityFinderWhereClauses?size > 0) />

<#if serviceBuilder.isVersionGTE_7_4_0()>
	package ${packagePath}.service.persistence.impl;

	import ${apiPackagePath}.model.${entity.name}Table;
	import ${packagePath}.model.impl.${entity.name}Impl;
	import ${packagePath}.model.impl.${entity.name}ModelImpl;

	import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
	import com.liferay.portal.kernel.dao.orm.FinderPath;
	import com.liferay.portal.kernel.model.BaseModel;
	import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;

	<#if hasEntityFinderWhereClauses>
		import com.liferay.portal.kernel.util.GetterUtil;
		import com.liferay.portal.kernel.util.Validator;
	</#if>

	import java.util.ArrayList;

	<#if hasEntityFinderWhereClauses>
		import java.util.HashMap;
	</#if>

	import java.util.List;
	import java.util.Map;
	import java.util.Objects;
	import java.util.concurrent.ConcurrentHashMap;

	<#if hasEntityFinderWhereClauses>
		import java.util.function.BiPredicate;
	</#if>

	import org.osgi.service.component.annotations.Component;

	<#assign columnBitmaskEnabled = (entity.databaseRegularEntityColumns?size &lt; 64) && !entity.hasEagerBlobColumn() />

	/**
	 * The arguments resolver class for retrieving value from ${entity.name}.
	 *
	 * @author ${author}
	<#if classDeprecated>
	 * @deprecated ${classDeprecatedComment}
	</#if>
	 * @generated
	 */
	<#if dependencyInjectorDS>
		@Component(
	<#else>
		@OSGiBeanProperties(
	</#if>
		property = {"class.name=${packagePath}.model.impl.${entity.name}Impl", "table.name=${entity.table}"}, service = ArgumentsResolver.class)
	public
<#else>
	private static
</#if>
class ${entity.name}ModelArgumentsResolver implements ArgumentsResolver {

	@Override
	public Object[] getArguments(FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn, boolean original) {
		String[] columnNames = finderPath.getColumnNames();

		if ((columnNames == null) || (columnNames.length == 0)) {
			if (baseModel.isNew()) {
				return new Object[0];
			}

			return null;
		}

		${entity.name}ModelImpl ${entity.variableName}ModelImpl = (${entity.name}ModelImpl)baseModel;

		<#if hasEntityFinderWhereClauses>
			BiPredicate<${entity.name}ModelImpl, Boolean> whereBiPredicate = _whereBiPredicates.get(finderPath.getFinderName());

			if ((whereBiPredicate != null) && !whereBiPredicate.test(${entity.variableName}ModelImpl, original)) {
				return null;
			}
		</#if>

		<#if columnBitmaskEnabled>
			long columnBitmask = ${entity.variableName}ModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(${entity.variableName}ModelImpl, <#if serviceBuilder.isVersionGTE_7_4_0()>finderPath<#else>columnNames</#if>, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |= ${entity.variableName}ModelImpl.getColumnBitmask(columnName);
				}

				<#if hasEntityFinderWhereClauses>
					Long whereColumnBitmask = _whereColumnBitmasks.get(finderPath.getFinderName());

					if (whereColumnBitmask != null) {
						finderPathColumnBitmask |= whereColumnBitmask;
					}
				</#if>

				<#if entity.entityOrder??>
					if (finderPath.isBaseModelResult() && (${entity.name}PersistenceImpl.FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION == finderPath.getCacheName())) {
						finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
					}
				</#if>

				_finderPathColumnBitmasksCache.put(finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(${entity.variableName}ModelImpl, <#if serviceBuilder.isVersionGTE_7_4_0()>finderPath<#else>columnNames</#if>, original);
			}
		<#else>
			<#if hasEntityFinderWhereClauses>
				String[] whereColumnNames = _whereColumnNames.get(finderPath.getFinderName());
			</#if>

			if (!checkColumn || _hasModifiedColumns(${entity.variableName}ModelImpl, columnNames)

			<#if hasEntityFinderWhereClauses>
				|| ((whereColumnNames != null) && _hasModifiedColumns(${entity.variableName}ModelImpl, whereColumnNames))
			</#if>

			<#if entity.entityOrder??>
				|| _hasModifiedColumns(${entity.variableName}ModelImpl, _ORDER_BY_COLUMNS)
			</#if>

			) {
				return _getValue(${entity.variableName}ModelImpl, <#if serviceBuilder.isVersionGTE_7_4_0()>finderPath<#else>columnNames</#if>, original);
			}
		</#if>

		return null;
	}

	<#if serviceBuilder.isVersionGTE_7_4_0()>
		@Override
		public String getClassName() {
			return ${entity.name}Impl.class.getName();
		}

		@Override
		public String getTableName() {
			return ${entity.name}Table.INSTANCE.getTableName();
		}
	</#if>

	<#if hasEntityFinderWhereClauses>
		private static Object _getColumnValue(${entity.name}ModelImpl ${entity.variableName}ModelImpl, String columnName, boolean original) {
			if (original) {
				return ${entity.variableName}ModelImpl.getColumnOriginalValue(columnName);
			}

			return ${entity.variableName}ModelImpl.getColumnValue(columnName);
		}
	</#if>

	private static Object[] _getValue(${entity.name}ModelImpl ${entity.variableName}ModelImpl, <#if serviceBuilder.isVersionGTE_7_4_0()>FinderPath finderPath<#else>String[] columnNames</#if>, boolean original) {
		<#if serviceBuilder.isVersionGTE_7_4_0()>
			String[] columnNames = finderPath.getColumnNames();
		</#if>

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i ++) {
			String columnName = columnNames[i];

			<#if serviceBuilder.isVersionGTE_7_4_0()>
				Object value;

				if (original) {
					value = ${entity.variableName}ModelImpl.getColumnOriginalValue(columnName);
				}
				else {
					value = ${entity.variableName}ModelImpl.getColumnValue(columnName);
				}

				arguments[i] = finderPath.normalizeArgument(i, value);
			<#else>
				if (original) {
					arguments[i] = ${entity.variableName}ModelImpl.getColumnOriginalValue(columnName);
				}
				else {
					arguments[i] = ${entity.variableName}ModelImpl.getColumnValue(columnName);
				}
			</#if>
		}

		return arguments;
	}

	<#if columnBitmaskEnabled>
		private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache = new ConcurrentHashMap<>();
	<#else>
		private static boolean _hasModifiedColumns(${entity.name}ModelImpl ${entity.variableName}ModelImpl, String[] columnNames) {
			if (columnNames.length == 0) {
				return false;
			}

			for (String columnName : columnNames) {
				if (!Objects.equals(${entity.variableName}ModelImpl.getColumnOriginalValue(columnName), ${entity.variableName}ModelImpl.getColumnValue(columnName))) {
					return true;
				}
			}

			return false;
		}
	</#if>

	<#if entity.entityOrder??>
		<#if columnBitmaskEnabled>
			private static final long _ORDER_BY_COLUMNS_BITMASK;

			static {
				long orderByColumnsBitmask = 0;

				<#list entity.entityOrder.entityColumns as entityColumn>
					<#if !entity.PKEntityColumns?seq_contains(entityColumn)>
						orderByColumnsBitmask |= ${entity.name}ModelImpl.getColumnBitmask("${entityColumn.DBName}");
					</#if>
				</#list>

				_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
			}
		<#else>
			private static final String[] _ORDER_BY_COLUMNS;

			static {
				List<String> orderByColumns = new ArrayList<String>();

				<#if entity.entityOrder??>
					<#list entity.entityOrder.entityColumns as entityColumn>
						<#if !entity.PKEntityColumns?seq_contains(entityColumn)>
							orderByColumns.add("${entityColumn.DBName}");
						</#if>
					</#list>
				</#if>

				_ORDER_BY_COLUMNS = orderByColumns.toArray(new String[0]);
			}
		</#if>
	</#if>

	<#if hasEntityFinderWhereClauses>
		<#if columnBitmaskEnabled>
			private static final Map<String, Long> _whereColumnBitmasks = new HashMap<>();
		<#else>
			private static final Map<String, String[]> _whereColumnNames = new HashMap<>();
		</#if>

		private static final Map<String, BiPredicate<${entity.name}ModelImpl, Boolean>> _whereBiPredicates = new HashMap<>();

		static {
			<#list entityFinderWhereClauses?values as entityFinderWhereClause>
				<#if columnBitmaskEnabled>
					<#if entityFinderWhereClause?is_first>long </#if>whereColumnBitmask = <#list entityFinderWhereClause.DBColumnNames as dbColumnName>${entity.name}ModelImpl.getColumnBitmask("${dbColumnName}")<#if dbColumnName_has_next> | </#if></#list>;
				<#else>
					<#if entityFinderWhereClause?is_first>String[] </#if>whereColumnNames = new String[] {<#list entityFinderWhereClause.DBColumnNames as dbColumnName>"${dbColumnName}"<#if dbColumnName_has_next>, </#if></#list>};
				</#if>

				<#if entityFinderWhereClause?is_first>BiPredicate<${entity.name}ModelImpl, Boolean> </#if>whereBiPredicate = (${entity.variableName}ModelImpl, original) -> ${entityFinderWhereClause.javaExpression};

				<#list entityFinderWhereClause.entityFinders as entityFinder>
					<#if columnBitmaskEnabled>
						_whereColumnBitmasks.put("${entityFinder.name}", whereColumnBitmask);
					<#else>
						_whereColumnNames.put("${entityFinder.name}", whereColumnNames);
					</#if>

					_whereBiPredicates.put("${entityFinder.name}", whereBiPredicate);
				</#list>
				<#if entityFinderWhereClause_has_next>

				</#if>
			</#list>
		}
	</#if>

}