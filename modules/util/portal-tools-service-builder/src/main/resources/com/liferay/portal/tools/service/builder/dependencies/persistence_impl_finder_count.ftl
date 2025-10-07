<#assign
	entityColumns = entityFinder.entityColumns
	entityFinderArrayableColsList = entityFinder.getArrayableColumns()
/>

/**
 * Returns the number of ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
 *
<#list entityColumns as entityColumn>
 * @param ${entityColumn.name} the ${entityColumn.humanName}
</#list>
 * @return the number of matching ${entity.pluralHumanName}
 */
@Override
public int countBy${entityFinder.name}(

<#list entityColumns as entityColumn>
	${entityColumn.type} ${entityColumn.name}

	<#if entityColumn_has_next>
		,
	</#if>
</#list>

) {
	<#if entityFinder.isCollection() || serviceBuilder.isVersionLTE_7_3_0()>
		<#if entity.isChangeTrackingEnabled()>
			try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
		</#if>

		<#list entityColumns as entityColumn>
			<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
				${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
			</#if>
		</#list>

		FinderPath finderPath =
			<#if !entityFinder.hasCustomComparator()>
				_finderPathCountBy${entityFinder.name};
			<#else>
				_finderPathWithPaginationCountBy${entityFinder.name};
			</#if>

		Object[] finderArgs = new Object[] {
			<#list entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "Date")>
					_getTime(${entityColumn.name})
				<#else>
					${entityColumn.name}
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>
		};

		Long count = (Long)${finderCache}.getResult(finderPath, finderArgs, this);

		if (count == null) {
			<#include "persistence_impl_count_by_query.ftl">

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				<@finderQPos />

				count = (Long)query.uniqueResult();

				${finderCache}.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				<#if serviceBuilder.isVersionLTE_7_2_0()>
					${finderCache}.removeResult(finderPath, finderArgs);
				</#if>

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();

		<#if entity.isChangeTrackingEnabled()>
			}
		</#if>
	<#else>
		${entity.name} ${entity.variableName} = fetchBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name}

			<#if entityColumn_has_next>
				,
			</#if>
		</#list>

		);

		if (${entity.variableName} == null) {
			return 0;
		}

		return 1;
	</#if>
}

<#if entityFinder.hasArrayableOperator() && !entityFinder.hasArrayablePagination()>
	/**
	 * Returns the number of ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @return the number of matching ${entity.pluralHumanName}
	 */
	@Override
	public int countBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName}
		<#else>
			${entityColumn.type} ${entityColumn.name}
		</#if>

		<#if entityColumn_has_next>
			,
		</#if>
	</#list>

	) {
		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				if (${entityColumn.pluralName} == null) {
					${entityColumn.pluralName} = new ${entityColumn.type}[0];
				}
				else if (${entityColumn.pluralName}.length > 1) {
					<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
						for (int i = 0; i < ${entityColumn.pluralName}.length; i++) {
							${entityColumn.pluralName}[i] = Objects.toString(${entityColumn.pluralName}[i], "");
						}
					</#if>

					<#if serviceBuilder.isVersionGTE_7_2_0()>
						${entityColumn.pluralName} = ArrayUtil.sortedUnique(${entityColumn.pluralName});
					<#else>
						${entityColumn.pluralName} =
							<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isConvertNull()>
								ArrayUtil.distinct(${entityColumn.pluralName}, NULL_SAFE_STRING_COMPARATOR);
							<#else>
								ArrayUtil.unique(${entityColumn.pluralName});
							</#if>

						<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isConvertNull()>
							Arrays.sort(${entityColumn.pluralName}, NULL_SAFE_STRING_COMPARATOR);
						<#else>
							Arrays.sort(${entityColumn.pluralName});
						</#if>
					</#if>
				}
			<#elseif stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
				${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
			</#if>
		</#list>

		<#if entity.isChangeTrackingEnabled()>
			try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
		</#if>

		Object[] finderArgs = new Object[] {
			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
						StringUtil.merge(${entityColumn.pluralName})
				<#elseif stringUtil.equals(entityColumn.type, "Date")>
						_getTime(${entityColumn.name})
				<#else>
					${entityColumn.name}
				</#if>

				<#if entityColumn_has_next>
						,
				</#if>
			</#list>
		};

		Long count = (Long)${finderCache}.getResult(_finderPathWithPaginationCountBy${entityFinder.name}, finderArgs, this);

		if (count == null) {
			<#include "persistence_impl_count_by_arrayable_query.ftl">

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				<#if bindParameter(entityColumns)>
					QueryPos queryPos = QueryPos.getInstance(query);
				</#if>

				<@finderQPos _arrayable = true />

				count = (Long)query.uniqueResult();

				${finderCache}.putResult(_finderPathWithPaginationCountBy${entityFinder.name}, finderArgs, count);

			}
			catch (Exception exception) {
				<#if serviceBuilder.isVersionLTE_7_2_0()>
					${finderCache}.removeResult(_finderPathWithPaginationCountBy${entityFinder.name}, finderArgs);
				</#if>

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();

		<#if entity.isChangeTrackingEnabled()>
			}
		</#if>
	}
</#if>

<#if entityFinder.hasArrayableOperator() && entityFinder.hasArrayablePagination()>
	/**
	 * Returns the number of ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @return the number of matching ${entity.pluralHumanName}
	 */
	@Override
	public int countBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName}
		<#else>
			${entityColumn.type} ${entityColumn.name}
		</#if>

		<#if entityColumn_has_next>
			,
		</#if>
	</#list>

	) {
		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				if (${entityColumn.pluralName} == null) {
					${entityColumn.pluralName} = new ${entityColumn.type}[0];
				}
				else if (${entityColumn.pluralName}.length > 1) {
					<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
						for (int i = 0; i < ${entityColumn.pluralName}.length; i++) {
							${entityColumn.pluralName}[i] = Objects.toString(${entityColumn.pluralName}[i], "");
						}
					</#if>

					<#if serviceBuilder.isVersionGTE_7_2_0()>
						${entityColumn.pluralName} = ArrayUtil.sortedUnique(${entityColumn.pluralName});
					<#else>
						${entityColumn.pluralName} =
							<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isConvertNull()>
								ArrayUtil.distinct(${entityColumn.pluralName}, NULL_SAFE_STRING_COMPARATOR);
							<#else>
								ArrayUtil.unique(${entityColumn.pluralName});
							</#if>

						<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isConvertNull()>
							Arrays.sort(${entityColumn.pluralName}, NULL_SAFE_STRING_COMPARATOR);
						<#else>
							Arrays.sort(${entityColumn.pluralName});
						</#if>
					</#if>
				}
			<#elseif stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
				${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
			</#if>
		</#list>

		<#if entity.isChangeTrackingEnabled()>
			try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
		</#if>

		Object[] finderArgs = new Object[] {
			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
					StringUtil.merge(${entityColumn.pluralName})
				<#else>
					${entityColumn.name}
				</#if>

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>
		};

		Long count = (Long)${finderCache}.getResult(_finderPathWithPaginationCountBy${entityFinder.name}, finderArgs, this);

		if (count == null) {
			try {
				if ((databaseInMaxParameters > 0) && (<#list entityFinderArrayableColsList as arrayablefinderCol>
							(${arrayablefinderCol.pluralName}.length > databaseInMaxParameters)

							<#if arrayablefinderCol_has_next>
								||
							</#if>
						</#list>)) {
						count = Long.valueOf(0);

						<#list entityFinderArrayableColsList as arrayablefinderCol>
							${arrayablefinderCol.type}[][] ${arrayablefinderCol.pluralName}Pages = (${arrayablefinderCol.type}[][])ArrayUtil.split(${arrayablefinderCol.pluralName}, databaseInMaxParameters);
						</#list>

						<#list entityFinderArrayableColsList as arrayablefinderCol>
							for (${arrayablefinderCol.type}[] ${arrayablefinderCol.pluralName}Page : ${arrayablefinderCol.pluralName}Pages) {
						</#list>

							count += Long.valueOf(_countBy${entityFinder.name}(

							<#list entityColumns as entityColumn>
								<#if entityColumn.hasArrayableOperator()>
									${entityColumn.pluralName}Page
								<#else>
									${entityColumn.name}
								</#if>

								<#if entityColumn_has_next>
									,
								</#if>
							</#list>));

					<#list entityFinderArrayableColsList as arrayablefinderCol>
						}
					</#list>
					}
					else {
						count = Long.valueOf(_countBy${entityFinder.name}(

						<#list entityColumns as entityColumn>
							<#if entityColumn.hasArrayableOperator()>
								${entityColumn.pluralName}
							<#else>
								${entityColumn.name}
							</#if>

							<#if entityColumn_has_next>
								,
							</#if>
						</#list>));
					}

					${finderCache}.putResult(_finderPathWithPaginationCountBy${entityFinder.name}, finderArgs, count);
			}
			catch (Exception exception) {
				<#if serviceBuilder.isVersionLTE_7_2_0()>
					${finderCache}.removeResult(_finderPathWithPaginationCountBy${entityFinder.name}, finderArgs);
				</#if>

				throw processException(exception);
			}
		}

		return count.intValue();

		<#if entity.isChangeTrackingEnabled()>
			}
		</#if>
	}

	private int _countBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName}
		<#else>
			${entityColumn.type} ${entityColumn.name}
		</#if>

		<#if entityColumn_has_next>
			,
		</#if>
	</#list>

	) {
		Long count = null;

		<#include "persistence_impl_count_by_arrayable_query.ftl">

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			<#if bindParameter(entityColumns)>
				QueryPos queryPos = QueryPos.getInstance(query);
			</#if>

			<@finderQPos _arrayable = true />

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}
</#if>

<#if entity.isPermissionCheckEnabled(entityFinder)>
	/**
	 * Returns the number of ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(false)}.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @return the number of matching ${entity.pluralHumanName} that the user has permission to view
	 */
	@Override
	public int filterCountBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name}

		<#if entityColumn_has_next>
			,
		</#if>
	</#list>

	) {
		<#if entityFinder.hasEntityColumn("groupId")>
			if (!InlineSQLHelperUtil.isEnabled(groupId)) {
		<#elseif entityFinder.hasEntityColumn("companyId")>
			if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
		<#else>
			if (!InlineSQLHelperUtil.isEnabled()) {
		</#if>

			return countBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				${entityColumn.name}

				<#if entityColumn_has_next>
					,
				</#if>
			</#list>

			);
		}

		<#if serviceBuilder.isVersionGTE_7_4_0()>
			if (isPermissionsInMemoryFilterEnabled()) {
				List<${entity.name}> ${entity.pluralVariableName} =

				<#if !entityFinder.isCollection() || entityFinder.isUnique()>
					Arrays.asList(fetchBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						${entityColumn.name}

						<#if entityColumn_has_next>
							,
						</#if>
					</#list>

					));
				<#else>
					findBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						${entityColumn.name}

						<#if entityColumn_has_next>
							,
						</#if>
					</#list>

					);
				</#if>

				${entity.pluralVariableName} = InlineSQLHelperUtil.filter(
					${entity.pluralVariableName}

					<#if entityFinder.hasEntityColumn("groupId")>
						, groupId
					</#if>
				);

				return ${entity.pluralVariableName}.size();
			}
		</#if>

		<#list entityColumns as entityColumn>
			<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
				${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
			</#if>
		</#list>

		<#if entity.isPermissionedModel()>
			<#include "persistence_impl_count_by_query.ftl">

			String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, _FILTER_ENTITY_TABLE_FILTER_USERID_COLUMN<#if entityFinder.hasEntityColumn("groupId")>, groupId</#if>);

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				<@finderQPos />

				Long count = (Long)query.uniqueResult();

				return count.intValue();
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		<#else>
			StringBundler sb = new StringBundler(${entityColumns?size + 1});

			sb.append(_FILTER_SQL_COUNT_${entity.alias?upper_case}_WHERE);

			<#assign sqlQuery = true />

			<#include "persistence_impl_finder_cols.ftl">

			<#assign sqlQuery = false />

			String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN<#if entityFinder.hasEntityColumn("groupId")>, groupId</#if>);

			Session session = null;

			try {
				session = openSession();

				SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

				sqlQuery.addScalar(COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

				QueryPos queryPos = QueryPos.getInstance(sqlQuery);

				<@finderQPos />

				Long count = (Long)sqlQuery.uniqueResult();

				return count.intValue();
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		</#if>
	}

	<#if entityFinder.hasArrayableOperator()>
		/**
		 * Returns the number of ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(true)}.
		 *
		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
		 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
			<#else>
		 * @param ${entityColumn.name} the ${entityColumn.humanName}
			</#if>
		</#list>
		 * @return the number of matching ${entity.pluralHumanName} that the user has permission to view
		 */
		@Override
		public int filterCountBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				${entityColumn.type}[] ${entityColumn.pluralName}
			<#else>
				${entityColumn.type} ${entityColumn.name}
			</#if>

			<#if entityColumn_has_next>
				,
			</#if>
		</#list>

		) {
			<#if entityFinder.hasEntityColumn("groupId")>
				if (!InlineSQLHelperUtil.isEnabled(
					<#if entityFinder.getEntityColumn("groupId").hasArrayableOperator()>
						groupIds
					<#else>
						groupId
					</#if>
				)) {
			<#elseif entityFinder.hasEntityColumn("companyId")>
				if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			<#else>
				if (!InlineSQLHelperUtil.isEnabled()) {
			</#if>

				return countBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.pluralName}
					<#else>
						${entityColumn.name}
					</#if>

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

				);
			}

			<#if serviceBuilder.isVersionGTE_7_4_0()>
				if (isPermissionsInMemoryFilterEnabled()) {
					List<${entity.name}> ${entity.pluralVariableName} = InlineSQLHelperUtil.filter(
						findBy${entityFinder.name}(

						<#list entityColumns as entityColumn>
							<#if entityColumn.hasArrayableOperator()>
								${entityColumn.pluralName}
							<#else>
								${entityColumn.name}
							</#if>

							<#if entityColumn_has_next>
								,
							</#if>
						</#list>

						)

						<#if entityFinder.hasEntityColumn("groupId")>,
							<#if entityFinder.getEntityColumn("groupId").hasArrayableOperator()>
								groupIds
							<#else>
								groupId
							</#if>
						</#if>

						);

					return ${entity.pluralVariableName}.size();
				}
			</#if>

			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
					if (${entityColumn.pluralName} == null) {
						${entityColumn.pluralName} = new ${entityColumn.type}[0];
					}
					else if (${entityColumn.pluralName}.length > 1) {
						<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
							for (int i = 0; i < ${entityColumn.pluralName}.length; i++) {
								${entityColumn.pluralName}[i] = Objects.toString(${entityColumn.pluralName}[i], "");
							}
						</#if>

						<#if serviceBuilder.isVersionGTE_7_2_0()>
							${entityColumn.pluralName} = ArrayUtil.sortedUnique(${entityColumn.pluralName});
						<#else>
							${entityColumn.pluralName} =
								<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isConvertNull()>
									ArrayUtil.distinct(${entityColumn.pluralName}, NULL_SAFE_STRING_COMPARATOR);
								<#else>
									ArrayUtil.unique(${entityColumn.pluralName});
								</#if>

							<#if stringUtil.equals(entityColumn.type, "String") && !entityColumn.isConvertNull()>
								Arrays.sort(${entityColumn.pluralName}, NULL_SAFE_STRING_COMPARATOR);
							<#else>
								Arrays.sort(${entityColumn.pluralName});
							</#if>
						</#if>
					}
				<#elseif stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
					${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
				</#if>
			</#list>

			<#if entity.isPermissionedModel()>
				<#include "persistence_impl_count_by_arrayable_query.ftl">

				String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, _FILTER_ENTITY_TABLE_FILTER_USERID_COLUMN

				<#if entityFinder.hasEntityColumn("groupId")>,
					<#if entityFinder.getEntityColumn("groupId").hasArrayableOperator()>
						groupIds
					<#else>
						groupId
					</#if>
				</#if>);

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					<#if bindParameter(entityColumns)>
						QueryPos queryPos = QueryPos.getInstance(query);
					</#if>

					<@finderQPos _arrayable = true />

					Long count = (Long)query.uniqueResult();

					return count.intValue();
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			<#else>
				StringBundler sb = new StringBundler();

				sb.append(_FILTER_SQL_COUNT_${entity.alias?upper_case}_WHERE);

				<#assign sqlQuery = true />

				<#include "persistence_impl_finder_arrayable_cols.ftl">

				<#assign sqlQuery = false />

				String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN

				<#if entityFinder.hasEntityColumn("groupId")>,
					<#if entityFinder.getEntityColumn("groupId").hasArrayableOperator()>
						groupIds
					<#else>
						groupId
					</#if>
				</#if>);

				Session session = null;

				try {
					session = openSession();

					SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

					sqlQuery.addScalar(COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

					<#if bindParameter(entityColumns)>
						QueryPos queryPos = QueryPos.getInstance(sqlQuery);
					</#if>

					<@finderQPos _arrayable = true />

					Long count = (Long)sqlQuery.uniqueResult();

					return count.intValue();
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			</#if>
		}
	</#if>
</#if>