<#assign
	entityColumns = entityFinder.entityColumns
	entityFinderArrayableColsList = entityFinder.getArrayableColumns()
/>

<#--
Basic Cases Table:

+---------------------------+-------------------------------+-------------------------------+
|							|	entityFinder.isCollection()		|	!entityFinder.isCollection()		|
+---------------------------+-------------------------------+-------------------------------+
|	entityFinder.isUnique()		|			Case 1				|			Case 2				|
+---------------------------+-------------------------------+-------------------------------+
|	!entityFinder.isUnique()		|			Case 3				|			Case 4				|
+---------------------------+-------------------------------+-------------------------------+

Combination Cases Table 1:

+---------------------------+-------------------------------+-------------------------------+
|							|	entityFinder.isCollection()		|	!entityFinder.isCollection()		|
+---------------------------+---------------------------------------------------------------+
|	entityFinder.isUnique()		|							Case 5								|
+---------------------------+---------------------------------------------------------------+
|	!entityFinder.isUnique()		|							Case 6								|
+---------------------------+---------------------------------------------------------------+

Combination Cases Table 2:

+---------------------------+-------------------------------+-------------------------------+
|							|	entityFinder.isCollection()		|	!entityFinder.isCollection()		|
+---------------------------+-------------------------------+-------------------------------+
|	entityFinder.isUnique()		|								|								|
+---------------------------|			Case 7				|			Case 8				|
|	!entityFinder.isUnique()		|								|								|
+---------------------------+-------------------------------+-------------------------------+

Combination Cases Table 3:

+---------------------------+-------------------------------+-------------------------------+
|							|	entityFinder.isCollection()		|	!entityFinder.isCollection()		|
+---------------------------+-------------------------------+-------------------------------+
|	entityFinder.isUnique()		|																|
+---------------------------|--------------------------------			Case 9				|
|	!entityFinder.isUnique()		|								|								|
+---------------------------+-------------------------------+-------------------------------+

There are a total of 9 cases. The first 4 cases are the basic cases as show in
the first table.

The additional combination case tables allow us to group the basic cases. For
example:

A combination of case 1 and case 2 is grouped as case 5.

A combination of case 3 and case 4 is grouped as case 6.

A combination of case 1 and case 3 is grouped as case 7.

A combination of case 2 and case 4 is grouped as case 8.

A combination of case 1, case 2, and case 4 is grouped as case 9.

Grouping the basic cases allows us to write the finder implementation with as
little duplicate code as possible.

entityFinder.isUnique() means a literal unique finder because it generates a unique
index at the database level. !entityFinder.isCollection() means a conceptual unique
that may or may not be enforced with a unique index at the database level. Case
9 can be considered a union of the literal and conceptual unique finders.
-->

<#-- Case 1: entityFinder.isCollection() && entityFinder.isUnique() -->

<#if entityFinder.isCollection() && entityFinder.isUnique()>
</#if>

<#-- Case 2: !entityFinder.isCollection() && entityFinder.isUnique() -->

<#if !entityFinder.isCollection() && entityFinder.isUnique()>
</#if>

<#-- Case 3: entityFinder.isCollection() && !entityFinder.isUnique() -->

<#if entityFinder.isCollection() && !entityFinder.isUnique()>
	/**
	 * Returns all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @return the matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name}

		<#if entityColumn_has_next>
			,
		</#if>
	</#list>

	) {
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @return the range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name},
	</#list>

	int start, int end) {
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		start, end, null);
	}

	/**
	 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name},
	</#list>

	int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name},
	</#list>

	int start, int end, OrderByComparator<${entity.name}> orderByComparator, boolean useFinderCache) {
		<#if entity.isChangeTrackingEnabled()>
			try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
		</#if>

		<#list entityColumns as entityColumn>
			<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
				${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
			</#if>
		</#list>

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		<#if !entityFinder.hasCustomComparator()>
			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) && (orderByComparator == null)) {
				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindBy${entityFinder.name};
					finderArgs = new Object[] {
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
				}
			}
			else if (useFinderCache) {
		</#if>

		finderPath = _finderPathWithPaginationFindBy${entityFinder.name};
		finderArgs = new Object[] {
			<#list entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "Date")>
					_getTime(${entityColumn.name}),
				<#else>
					${entityColumn.name},
				</#if>
			</#list>

			start, end, orderByComparator
		};

		<#if !entityFinder.hasCustomComparator()>
			}
		</#if>

		List<${entity.name}> list = null;

		if (useFinderCache) {
			list = (List<${entity.name}>)${finderCache}.getResult(finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (${entity.name} ${entity.variableName} : list) {
					if (
						<#list entityColumns as entityColumn>
							<#include "persistence_impl_finder_field_comparator.ftl">

							<#if entityColumn_has_next>
								||
							</#if>
						</#list>
					) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			<#include "persistence_impl_find_by_query.ftl">

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				<@finderQPos />

				list = (List<${entity.name}>)QueryUtil.list(query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					${finderCache}.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				<#if serviceBuilder.isVersionLTE_7_2_0()>
					if (useFinderCache) {
						${finderCache}.removeResult(finderPath, finderArgs);
					}
				</#if>

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;

		<#if entity.isChangeTrackingEnabled()>
			}
		</#if>
	}

	/**
	 * Returns the first ${entity.humanName} in the ordered set where ${entityFinder.getHumanConditions(false)}.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ${entity.humanName}
	 * @throws ${noSuchEntity}Exception if a matching ${entity.humanName} could not be found
	 */
	@Override
	public ${entity.name} findBy${entityFinder.name}_First(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name},
	</#list>

	OrderByComparator<${entity.name}> orderByComparator) throws ${noSuchEntity}Exception {
		${entity.name} ${entity.variableName} = fetchBy${entityFinder.name}_First(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		orderByComparator);

		if (${entity.variableName} != null) {
			return ${entity.variableName};
		}

		StringBundler sb = new StringBundler(${(entityColumns?size * 2) + 2});

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		<#list entityColumns as entityColumn>
			sb.append("<#if entityColumn_index != 0>, </#if>${entityColumn.name}${entityColumn.comparator}");
			sb.append(${entityColumn.name});

			<#if !entityColumn_has_next>
				sb.append("}");
			</#if>
		</#list>

		throw new ${noSuchEntity}Exception(sb.toString());
	}

	/**
	 * Returns the first ${entity.humanName} in the ordered set where ${entityFinder.getHumanConditions(false)}.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
	 */
	@Override
	public ${entity.name} fetchBy${entityFinder.name}_First(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name},
	</#list>

	OrderByComparator<${entity.name}> orderByComparator) {
		List<${entity.name}> list = findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ${entity.humanName} in the ordered set where ${entityFinder.getHumanConditions(false)}.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ${entity.humanName}
	 * @throws ${noSuchEntity}Exception if a matching ${entity.humanName} could not be found
	 */
	@Override
	public ${entity.name} findBy${entityFinder.name}_Last(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name},
	</#list>

	OrderByComparator<${entity.name}> orderByComparator) throws ${noSuchEntity}Exception {
		${entity.name} ${entity.variableName} = fetchBy${entityFinder.name}_Last(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		orderByComparator);

		if (${entity.variableName} != null) {
			return ${entity.variableName};
		}

		StringBundler sb = new StringBundler(${(entityColumns?size * 2) + 2});

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		<#list entityColumns as entityColumn>
			sb.append("<#if entityColumn_index != 0>, </#if>${entityColumn.name}${entityColumn.comparator}");
			sb.append(${entityColumn.name});

			<#if !entityColumn_has_next>
				sb.append("}");
			</#if>
		</#list>

		throw new ${noSuchEntity}Exception(sb.toString());
	}

	/**
	 * Returns the last ${entity.humanName} in the ordered set where ${entityFinder.getHumanConditions(false)}.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
	 */
	@Override
	public ${entity.name} fetchBy${entityFinder.name}_Last(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name},
	</#list>

	OrderByComparator<${entity.name}> orderByComparator) {
		int count = countBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name}

			<#if entityColumn_has_next>
				,
			</#if>
		</#list>

		);

		if (count == 0) {
			return null;
		}

		List<${entity.name}> list = findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	<#if !entityFinder.hasEntityColumn(entity.PKVariableName)>
		/**
		 * Returns the ${entity.pluralHumanName} before and after the current ${entity.humanName} in the ordered set where ${entityFinder.getHumanConditions(false)}.
		 *
		 * @param ${entity.PKVariableName} the primary key of the current ${entity.humanName}
		<#list entityColumns as entityColumn>
		 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#list>
		 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
		 * @return the previous, current, and next ${entity.humanName}
		 * @throws ${noSuchEntity}Exception if a ${entity.humanName} with the primary key could not be found
		 */
		@Override
		public ${entity.name}[] findBy${entityFinder.name}_PrevAndNext(${entity.PKClassName} ${entity.PKVariableName},

		<#list entityColumns as entityColumn>
			${entityColumn.type} ${entityColumn.name},
		</#list>

		OrderByComparator<${entity.name}> orderByComparator) throws ${noSuchEntity}Exception {
			<#list entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
					${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
				</#if>
			</#list>

			${entity.name} ${entity.variableName} = findByPrimaryKey(${entity.PKVariableName});

			Session session = null;

			try {
				session = openSession();

				${entity.name}[] array = new ${entity.name}Impl[3];

				array[0] =
					getBy${entityFinder.name}_PrevAndNext(
						session, ${entity.variableName},

						<#list entityColumns as entityColumn>
							${entityColumn.name},
						</#list>

						orderByComparator, true);

				array[1] = ${entity.variableName};

				array[2] =
					getBy${entityFinder.name}_PrevAndNext(
						session, ${entity.variableName},

						<#list entityColumns as entityColumn>
							${entityColumn.name},
						</#list>

						orderByComparator, false);

				return array;
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		protected ${entity.name} getBy${entityFinder.name}_PrevAndNext(
			Session session, ${entity.name} ${entity.variableName},

			<#list entityColumns as entityColumn>
				${entityColumn.type} ${entityColumn.name},
			</#list>

			OrderByComparator<${entity.name}> orderByComparator, boolean previous) {

			<#include "persistence_impl_get_by_prev_and_next_query.ftl">

			String sql = sb.toString();

			Query query = session.createQuery(sql);

			query.setFirstResult(0);
			query.setMaxResults(2);

			QueryPos queryPos = QueryPos.getInstance(query);

			<@finderQPos />

			if (orderByComparator != null) {
				for (Object orderByConditionValue : orderByComparator.getOrderByConditionValues(${entity.variableName})) {
					queryPos.add(orderByConditionValue);
				}
			}

			List<${entity.name}> list = query.list();

			if (list.size() == 2) {
				return list.get(1);
			}
			else {
				return null;
			}
		}
	</#if>

	<#if entity.isPermissionCheckEnabled(entityFinder)>
		/**
		 * Returns all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(false)}.
		 *
		<#list entityColumns as entityColumn>
		 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#list>
		 * @return the matching ${entity.pluralHumanName} that the user has permission to view
		 */
		@Override
		public List<${entity.name}> filterFindBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.type} ${entityColumn.name}

			<#if entityColumn_has_next>
				,
			</#if>
		</#list>

		) {
			return filterFindBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				${entityColumn.name},
			</#list>

			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		/**
		 * Returns a range of all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(false)}.
		 *
		 * <p>
		 * <#include "range_comment.ftl">
		 * </p>
		 *
		<#list entityColumns as entityColumn>
		 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#list>
		 * @param start the lower bound of the range of ${entity.pluralHumanName}
		 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
		 * @return the range of matching ${entity.pluralHumanName} that the user has permission to view
		 */
		@Override
		public List<${entity.name}> filterFindBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.type} ${entityColumn.name},
		</#list>

		int start, int end) {
			return filterFindBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				${entityColumn.name},
			</#list>

			start, end, null);
		}

		/**
		 * Returns an ordered range of all the ${entity.pluralHumanName} that the user has permissions to view where ${entityFinder.getHumanConditions(false)}.
		 *
		 * <p>
		 * <#include "range_comment.ftl">
		 * </p>
		 *
		<#list entityColumns as entityColumn>
		 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#list>
		 * @param start the lower bound of the range of ${entity.pluralHumanName}
		 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
		 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
		 * @return the ordered range of matching ${entity.pluralHumanName} that the user has permission to view
		 */
		@Override
		public List<${entity.name}> filterFindBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.type} ${entityColumn.name},
		</#list>

		int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
			<#if entityFinder.hasEntityColumn("groupId")>
				if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			<#elseif entityFinder.hasEntityColumn("companyId")>
				if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			<#else>
				if (!InlineSQLHelperUtil.isEnabled()) {
			</#if>

				return findBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					${entityColumn.name},
				</#list>

				start, end, orderByComparator);
			}

			<#if serviceBuilder.isVersionGTE_7_4_0()>
				if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) && isPermissionsInMemoryFilterEnabled()) {
					return InlineSQLHelperUtil.filter(
						findBy${entityFinder.name}(

						<#list entityColumns as entityColumn>
							${entityColumn.name},
						</#list>

						QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator)

						<#if entityFinder.hasEntityColumn("groupId")>
							, groupId
						</#if>

						);
				}
			</#if>

			<#list entityColumns as entityColumn>
				<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
					${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
				</#if>
			</#list>

			<#if entity.isPermissionedModel()>
				<#include "persistence_impl_find_by_query.ftl">

				String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, _FILTER_ENTITY_TABLE_FILTER_USERID_COLUMN<#if entityFinder.hasEntityColumn("groupId")>, groupId</#if>);

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					<@finderQPos />

					return (List<${entity.name}>)QueryUtil.list(query, getDialect(), start, end);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			<#else>
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(${entityColumns?size + 2} + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(${entityColumns?size + 3});
				}

				if (getDB().isSupportsInlineDistinct()) {
					sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_WHERE);
				}
				else {
					sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_1);
				}

				<#assign sqlQuery = true />

				<#include "persistence_impl_finder_cols.ftl">

				<#assign sqlQuery = false />

				if (!getDB().isSupportsInlineDistinct()) {
					sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_2);
				}

				if (orderByComparator != null) {
					if (getDB().isSupportsInlineDistinct()) {
						appendOrderByComparator(sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
					}
					else {
						appendOrderByComparator(sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
					}
				}
				else {
					if (getDB().isSupportsInlineDistinct()) {
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							sb.append(${entity.name}ModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
						<#else>
							sb.append(${entity.name}ModelImpl.ORDER_BY_JPQL);
						</#if>
					}
					else {
						sb.append(${entity.name}ModelImpl.ORDER_BY_SQL);
					}
				}

				String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN<#if entityFinder.hasEntityColumn("groupId")>, groupId</#if>);

				Session session = null;

				try {
					session = openSession();

					SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

					if (getDB().isSupportsInlineDistinct()) {
						sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, ${entity.name}Impl.class);
					}
					else {
						sqlQuery.addEntity(_FILTER_ENTITY_TABLE, ${entity.name}Impl.class);
					}

					QueryPos queryPos = QueryPos.getInstance(sqlQuery);

					<@finderQPos />

					return (List<${entity.name}>)QueryUtil.list(sqlQuery, getDialect(), start, end);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			</#if>
		}

		<#if !entityFinder.hasEntityColumn(entity.PKVariableName)>
			/**
			 * Returns the ${entity.pluralHumanName} before and after the current ${entity.humanName} in the ordered set of ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(false)}.
			 *
			 * @param ${entity.PKVariableName} the primary key of the current ${entity.humanName}
			<#list entityColumns as entityColumn>
			 * @param ${entityColumn.name} the ${entityColumn.humanName}
			</#list>
			 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
			 * @return the previous, current, and next ${entity.humanName}
			 * @throws ${noSuchEntity}Exception if a ${entity.humanName} with the primary key could not be found
			 */
			@Override
			public ${entity.name}[] filterFindBy${entityFinder.name}_PrevAndNext(${entity.PKClassName} ${entity.PKVariableName},

			<#list entityColumns as entityColumn>
				${entityColumn.type} ${entityColumn.name},
			</#list>

			OrderByComparator<${entity.name}> orderByComparator) throws ${noSuchEntity}Exception {
				<#if entityFinder.hasEntityColumn("groupId")>
					if (!InlineSQLHelperUtil.isEnabled(groupId)) {
				<#elseif entityFinder.hasEntityColumn("companyId")>
					if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
				<#else>
					if (!InlineSQLHelperUtil.isEnabled()) {
				</#if>

					return findBy${entityFinder.name}_PrevAndNext(${entity.PKVariableName},

					<#list entityColumns as entityColumn>
						${entityColumn.name},
					</#list>

					orderByComparator);
				}

				<#list entityColumns as entityColumn>
					<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
						${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
					</#if>
				</#list>

				${entity.name} ${entity.variableName} = findByPrimaryKey(${entity.PKVariableName});

				Session session = null;

				try {
					session = openSession();

					${entity.name}[] array = new ${entity.name}Impl[3];

					array[0] =
						filterGetBy${entityFinder.name}_PrevAndNext(
							session, ${entity.variableName},

							<#list entityColumns as entityColumn>
								${entityColumn.name},
							</#list>

							orderByComparator, true);

					array[1] = ${entity.variableName};

					array[2] =
						filterGetBy${entityFinder.name}_PrevAndNext(
							session, ${entity.variableName},

							<#list entityColumns as entityColumn>
								${entityColumn.name},
							</#list>

							orderByComparator, false);

					return array;
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			protected ${entity.name} filterGetBy${entityFinder.name}_PrevAndNext(
				Session session, ${entity.name} ${entity.variableName},

				<#list entityColumns as entityColumn>
					${entityColumn.type} ${entityColumn.name},
				</#list>

				OrderByComparator<${entity.name}> orderByComparator, boolean previous) {

				<#if entity.isPermissionedModel()>
					<#include "persistence_impl_get_by_prev_and_next_query.ftl">

					String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, _FILTER_ENTITY_TABLE_FILTER_USERID_COLUMN<#if entityFinder.hasEntityColumn("groupId")>, groupId</#if>);

					Query query = session.createQuery(sql);

					query.setFirstResult(0);
					query.setMaxResults(2);

					QueryPos queryPos = QueryPos.getInstance(query);

					<@finderQPos />

					if (orderByComparator != null) {
						for (Object orderByConditionValue : orderByComparator.getOrderByConditionValues(${entity.variableName})) {
							queryPos.add(orderByConditionValue);
						}
					}

					List<${entity.name}> list = query.list();

					if (list.size() == 2) {
						return list.get(1);
					}
					else {
						return null;
					}
				<#else>
					StringBundler sb = null;

					if (orderByComparator != null) {
						sb = new StringBundler(${entityColumns?size + 4} + (orderByComparator.getOrderByConditionFields().length * 3) + (orderByComparator.getOrderByFields().length * 3));
					}
					else {
						sb = new StringBundler(${entityColumns?size + 3});
					}

					if (getDB().isSupportsInlineDistinct()) {
						sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_WHERE);
					}
					else {
						sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_1);
					}

					<#assign sqlQuery = true />

					<#include "persistence_impl_finder_cols.ftl">

					<#assign sqlQuery = false />

					if (!getDB().isSupportsInlineDistinct()) {
						sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_2);
					}

					if (orderByComparator != null) {
						String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

						if (orderByConditionFields.length > 0) {
							sb.append(WHERE_AND);
						}

						for (int i = 0; i < orderByConditionFields.length; i++) {
							if (getDB().isSupportsInlineDistinct()) {
								sb.append(getColumnName(_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i], true));
							}
							else {
								sb.append(getColumnName(_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i], true));
							}

							if ((i + 1) < orderByConditionFields.length) {
								if (orderByComparator.isAscending() ^ previous) {
									sb.append(WHERE_GREATER_THAN_HAS_NEXT);
								}
								else {
									sb.append(WHERE_LESSER_THAN_HAS_NEXT);
								}
							}
							else {
								if (orderByComparator.isAscending() ^ previous) {
									sb.append(WHERE_GREATER_THAN);
								}
								else {
									sb.append(WHERE_LESSER_THAN);
								}
							}
						}

						sb.append(ORDER_BY_CLAUSE);

						String[] orderByFields = orderByComparator.getOrderByFields();

						for (int i = 0; i < orderByFields.length; i++) {
							if (getDB().isSupportsInlineDistinct()) {
								sb.append(getColumnName(_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
							}
							else {
								sb.append(getColumnName(_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
							}

							if ((i + 1) < orderByFields.length) {
								if (orderByComparator.isAscending() ^ previous) {
									sb.append(ORDER_BY_ASC_HAS_NEXT);
								}
								else {
									sb.append(ORDER_BY_DESC_HAS_NEXT);
								}
							}
							else {
								if (orderByComparator.isAscending() ^ previous) {
									sb.append(ORDER_BY_ASC);
								}
								else {
									sb.append(ORDER_BY_DESC);
								}
							}
						}
					}
					else {
						if (getDB().isSupportsInlineDistinct()) {
							<#if serviceBuilder.isVersionGTE_7_4_0()>
								sb.append(${entity.name}ModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
							<#else>
								sb.append(${entity.name}ModelImpl.ORDER_BY_JPQL);
							</#if>
						}
						else {
							sb.append(${entity.name}ModelImpl.ORDER_BY_SQL);
						}
					}

					String sql = InlineSQLHelperUtil.replacePermissionCheck(sb.toString(), ${entity.name}.class.getName(), _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN<#if entityFinder.hasEntityColumn("groupId")>, groupId</#if>);

					SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

					sqlQuery.setFirstResult(0);
					sqlQuery.setMaxResults(2);

					if (getDB().isSupportsInlineDistinct()) {
						sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, ${entity.name}Impl.class);
					}
					else {
						sqlQuery.addEntity(_FILTER_ENTITY_TABLE, ${entity.name}Impl.class);
					}

					QueryPos queryPos = QueryPos.getInstance(sqlQuery);

					<@finderQPos />

					if (orderByComparator != null) {
						for (Object orderByConditionValue : orderByComparator.getOrderByConditionValues(${entity.variableName})) {
							queryPos.add(orderByConditionValue);
						}
					}

					List<${entity.name}> list = sqlQuery.list();

					if (list.size() == 2) {
						return list.get(1);
					}
					else {
						return null;
					}
				</#if>
			}
		</#if>

		<#if entityFinder.hasArrayableOperator()>
			/**
			 * Returns all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(true)}.
			 *
			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
			 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
				<#else>
			 * @param ${entityColumn.name} the ${entityColumn.humanName}
				</#if>
			</#list>
			 * @return the matching ${entity.pluralHumanName} that the user has permission to view
			 */
			@Override
			public List<${entity.name}> filterFindBy${entityFinder.name}(

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
				return filterFindBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.pluralName},
					<#else>
						${entityColumn.name},
					</#if>
				</#list>

				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
			}

			/**
			 * Returns a range of all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(true)}.
			 *
			 * <p>
			 * <#include "range_comment.ftl">
			 * </p>
			 *
			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
			 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
				<#else>
			 * @param ${entityColumn.name} the ${entityColumn.humanName}
				</#if>
			</#list>
			 * @param start the lower bound of the range of ${entity.pluralHumanName}
			 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
			 * @return the range of matching ${entity.pluralHumanName} that the user has permission to view
			 */
			@Override
			public List<${entity.name}> filterFindBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
					${entityColumn.type}[] ${entityColumn.pluralName},
				<#else>
					${entityColumn.type} ${entityColumn.name},
				</#if>
			</#list>

			int start, int end) {
				return filterFindBy${entityFinder.name}(

				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						${entityColumn.pluralName},
					<#else>
						${entityColumn.name},
					</#if>
				</#list>

				start, end, null);
			}

			/**
			 * Returns an ordered range of all the ${entity.pluralHumanName} that the user has permission to view where ${entityFinder.getHumanConditions(true)}.
			 *
			 * <p>
			 * <#include "range_comment.ftl">
			 * </p>
			 *
			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
			 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
				<#else>
			 * @param ${entityColumn.name} the ${entityColumn.humanName}
				</#if>
			</#list>
			 * @param start the lower bound of the range of ${entity.pluralHumanName}
			 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
			 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
			 * @return the ordered range of matching ${entity.pluralHumanName} that the user has permission to view
			 */
			@Override
			public List<${entity.name}> filterFindBy${entityFinder.name}(

			<#list entityColumns as entityColumn>
				<#if entityColumn.hasArrayableOperator()>
					${entityColumn.type}[] ${entityColumn.pluralName},
				<#else>
					${entityColumn.type} ${entityColumn.name},
				</#if>
			</#list>

			int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
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

					return findBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName},
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					start, end, orderByComparator);
				}

				<#if serviceBuilder.isVersionGTE_7_4_0()>
					if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) && isPermissionsInMemoryFilterEnabled()) {
						return InlineSQLHelperUtil.filter(
							findBy${entityFinder.name}(

							<#list entityColumns as entityColumn>
								<#if entityColumn.hasArrayableOperator()>
									${entityColumn.pluralName},
								<#else>
									${entityColumn.name},
								</#if>
							</#list>

							QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator)

							<#if entityFinder.hasEntityColumn("groupId")>,
								<#if entityFinder.getEntityColumn("groupId").hasArrayableOperator()>
									groupIds
								<#else>
									groupId
								</#if>
							</#if>

							);
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
					<#include "persistence_impl_find_by_arrayable_query.ftl">

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

						return (List<${entity.name}>)QueryUtil.list(query, getDialect(), start, end);
					}
					catch (Exception exception) {
						throw processException(exception);
					}
					finally {
						closeSession(session);
					}
				<#else>
					StringBundler sb = new StringBundler();

					if (getDB().isSupportsInlineDistinct()) {
						sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_WHERE);
					}
					else {
						sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_1);
					}

					<#assign sqlQuery = true />

					<#include "persistence_impl_finder_arrayable_cols.ftl">

					<#assign sqlQuery = false />

					if (!getDB().isSupportsInlineDistinct()) {
						sb.append(_FILTER_SQL_SELECT_${entity.alias?upper_case}_NO_INLINE_DISTINCT_WHERE_2);
					}

					if (orderByComparator != null) {
						if (getDB().isSupportsInlineDistinct()) {
							appendOrderByComparator(sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
						}
						else {
							appendOrderByComparator(sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
						}
					}
					else {
						if (getDB().isSupportsInlineDistinct()) {
							<#if serviceBuilder.isVersionGTE_7_4_0()>
								sb.append(${entity.name}ModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
							<#else>
								sb.append(${entity.name}ModelImpl.ORDER_BY_JPQL);
							</#if>
						}
						else {
							sb.append(${entity.name}ModelImpl.ORDER_BY_SQL);
						}
					}

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

						if (getDB().isSupportsInlineDistinct()) {
							sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, ${entity.name}Impl.class);
						}
						else {
							sqlQuery.addEntity(_FILTER_ENTITY_TABLE, ${entity.name}Impl.class);
						}

						<#if bindParameter(entityColumns)>
							QueryPos queryPos = QueryPos.getInstance(sqlQuery);
						</#if>

						<@finderQPos _arrayable = true />

						return (List<${entity.name}>)QueryUtil.list(sqlQuery, getDialect(), start, end);
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
</#if>

<#-- Case 4: !entityFinder.isCollection() && !entityFinder.isUnique() -->

<#if !entityFinder.isCollection() && !entityFinder.isUnique()>
</#if>

<#-- Case 5: entityFinder.isUnique() -->

<#if entityFinder.isUnique()>
</#if>

<#-- Case 6: !entityFinder.isUnique() -->

<#if !entityFinder.isUnique()>
</#if>

<#-- Case 7: entityFinder.isCollection() && entityFinder.hasArrayableOperator() && !entityFinder.hasArrayablePagination() -->

<#if entityFinder.isCollection() && entityFinder.hasArrayableOperator() && !entityFinder.hasArrayablePagination()>
	/**
	 * Returns all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @return the matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

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
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				${entityColumn.pluralName},
			<#else>
				${entityColumn.name},
			</#if>
		</#list>

		QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @return the range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName},
		<#else>
			${entityColumn.type} ${entityColumn.name},
		</#if>
	</#list>

	int start, int end) {
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				${entityColumn.pluralName},
			<#else>
				${entityColumn.name},
			</#if>
		</#list>

		start, end, null);
	}

	/**
	 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName},
		<#else>
			${entityColumn.type} ${entityColumn.name},
		</#if>
	</#list>

	int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				${entityColumn.pluralName},
			<#else>
				${entityColumn.name},
			</#if>
		</#list>

		start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}, optionally using the finder cache.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			* @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
			* @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName},
		<#else>
			${entityColumn.type} ${entityColumn.name},
		</#if>
	</#list>

	int start, int end, OrderByComparator<${entity.name}> orderByComparator, boolean useFinderCache) {
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

		if (
		<#assign firstCol = true />
		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				<#if firstCol>
					<#assign firstCol = false />
				<#else>
					&&
				</#if>

				${entityColumn.pluralName}.length == 1
			</#if>
		</#list>
		) {
			<#if entityFinder.isUnique()>
				${entity.name} ${entity.variableName} = fetchBy${entityFinder.name}(
					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName}[0]
						<#else>
							${entityColumn.name}
						</#if>

						<#if entityColumn_has_next>
							,
						</#if>
					</#list>);

				if (${entity.variableName} == null) {
					return Collections.emptyList();
				}
				else {
					List<${entity.name}> list = new ArrayList<${entity.name}>(1);

					list.add(${entity.variableName});

					return list;
				}
			<#else>
				return findBy${entityFinder.name}(
					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName}[0],
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					start, end, orderByComparator);
			</#if>
		}

		<#if entity.isChangeTrackingEnabled()>
			try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
		</#if>

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) && (orderByComparator == null)) {
			if (useFinderCache) {
				finderArgs = new Object[] {
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
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						StringUtil.merge(${entityColumn.pluralName}),
					<#elseif stringUtil.equals(entityColumn.type, "Date")>
						_getTime(${entityColumn.name}),
					<#else>
						${entityColumn.name},
					</#if>
				</#list>

				start, end, orderByComparator
			};
		}

		List<${entity.name}> list = null;

		if (useFinderCache) {
			list = (List<${entity.name}>)${finderCache}.getResult(_finderPathWithPaginationFindBy${entityFinder.name}, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (${entity.name} ${entity.variableName} : list) {
					if (
						<#list entityColumns as entityColumn>
							<#if entityColumn.hasArrayableOperator()>
								!ArrayUtil.contains(${entityColumn.pluralName}, ${entity.variableName}.get${entityColumn.methodName}())
							<#else>
								<#include "persistence_impl_finder_field_comparator.ftl">
							</#if>

							<#if entityColumn_has_next>
								||
							</#if>
						</#list>
					) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			<#include "persistence_impl_find_by_arrayable_query.ftl">

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				<#if bindParameter(entityColumns)>
					QueryPos queryPos = QueryPos.getInstance(query);
				</#if>

				<@finderQPos _arrayable = true />

				list = (List<${entity.name}>)QueryUtil.list(query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					${finderCache}.putResult(_finderPathWithPaginationFindBy${entityFinder.name}, finderArgs, list);
				}
			}
			catch (Exception exception) {
				<#if serviceBuilder.isVersionLTE_7_2_0()>
					if (useFinderCache) {
						${finderCache}.removeResult(_finderPathWithPaginationFindBy${entityFinder.name}, finderArgs);
					}
				</#if>

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;

		<#if entity.isChangeTrackingEnabled()>
			}
		</#if>
	}
</#if>

<#-- Case 7.1: entityFinder.isCollection() && entityFinder.hasArrayableOperator() && entityFinder.hasArrayablePagination() -->

<#if entityFinder.isCollection() && entityFinder.hasArrayableOperator() && entityFinder.hasArrayablePagination()>
	/**
	 * Returns all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @return the matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

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
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				${entityColumn.pluralName},
			<#else>
				${entityColumn.name},
			</#if>
		</#list>

		QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @return the range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName},
		<#else>
			${entityColumn.type} ${entityColumn.name},
		</#if>
	</#list>

	int start, int end) {
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				${entityColumn.pluralName},
			<#else>
				${entityColumn.name},
			</#if>
		</#list>

		start, end, null);
	}

	/**
	 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(true)}.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
	 * @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName},
		<#else>
			${entityColumn.type} ${entityColumn.name},
		</#if>
	</#list>

	int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
		return findBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				${entityColumn.pluralName},
			<#else>
				${entityColumn.name},
			</#if>
		</#list>

		start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ${entity.pluralHumanName} where ${entityFinder.getHumanConditions(false)}, optionally using the finder cache.
	 *
	 * <p>
	 * <#include "range_comment.ftl">
	 * </p>
	 *
	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			* @param ${entityColumn.pluralName} the ${entityColumn.pluralHumanName}
		<#else>
			* @param ${entityColumn.name} the ${entityColumn.humanName}
		</#if>
	</#list>
	 * @param start the lower bound of the range of ${entity.pluralHumanName}
	 * @param end the upper bound of the range of ${entity.pluralHumanName} (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ${entity.pluralHumanName}
	 */
	@Override
	public List<${entity.name}> findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName},
		<#else>
			${entityColumn.type} ${entityColumn.name},
		</#if>
	</#list>

	int start, int end, OrderByComparator<${entity.name}> orderByComparator, boolean useFinderCache) {
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

		if (
		<#assign firstCol = true />
		<#list entityColumns as entityColumn>
			<#if entityColumn.hasArrayableOperator()>
				<#if firstCol>
					<#assign firstCol = false />
				<#else>
					&&
				</#if>

				${entityColumn.pluralName}.length == 1
			</#if>
		</#list>
		) {
			<#if entityFinder.isUnique()>
				${entity.name} ${entity.variableName} = fetchBy${entityFinder.name}(
					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName}[0]
						<#else>
							${entityColumn.name}
						</#if>

						<#if entityColumn_has_next>
							,
						</#if>
					</#list>);

				if (${entity.variableName} == null) {
					return Collections.emptyList();
				}
				else {
					return Collections.singletonList(${entity.variableName});
				}
			<#else>
				return findBy${entityFinder.name}(
					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName}[0],
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					start, end, orderByComparator);
			</#if>
		}

		<#if entity.isChangeTrackingEnabled()>
			try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
		</#if>

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) && (orderByComparator == null)) {
			if (useFinderCache) {
				finderArgs = new Object[] {
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
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				<#list entityColumns as entityColumn>
					<#if entityColumn.hasArrayableOperator()>
						StringUtil.merge(${entityColumn.pluralName}),
					<#else>
						${entityColumn.name},
					</#if>
				</#list>

				start, end, orderByComparator
			};
		}

		List<${entity.name}> list = null;

		if (useFinderCache) {
			list = (List<${entity.name}>)${finderCache}.getResult(_finderPathWithPaginationFindBy${entityFinder.name}, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (${entity.name} ${entity.variableName} : list) {
					if (
						<#list entityColumns as entityColumn>
							<#if entityColumn.hasArrayableOperator()>
								!ArrayUtil.contains(${entityColumn.pluralName}, ${entity.variableName}.get${entityColumn.methodName}())
							<#else>
								<#include "persistence_impl_finder_field_comparator.ftl">
							</#if>

							<#if entityColumn_has_next>
								||
							</#if>
						</#list>
					) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			try {
				if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) && (databaseInMaxParameters > 0) && (<#list entityFinderArrayableColsList as arrayableentityColumn>
						(${arrayableentityColumn.pluralName}.length > databaseInMaxParameters)

						<#if arrayableentityColumn_has_next>
							||
						</#if>
					</#list>)) {

					list = new ArrayList<${entity.name}>();

					<#list entityFinderArrayableColsList as arrayableentityColumn>
						${arrayableentityColumn.type}[][] ${arrayableentityColumn.pluralName}Pages = (${arrayableentityColumn.type}[][])ArrayUtil.split(${arrayableentityColumn.pluralName}, databaseInMaxParameters);
					</#list>

					<#list entityFinderArrayableColsList as arrayableentityColumn>
						for (${arrayableentityColumn.type}[] ${arrayableentityColumn.pluralName}Page : ${arrayableentityColumn.pluralName}Pages) {
					</#list>

						list.addAll(_findBy${entityFinder.name}(

						<#list entityColumns as entityColumn>
							<#if entityColumn.hasArrayableOperator()>
								${entityColumn.pluralName}Page,
							<#else>
								${entityColumn.name},
							</#if>
						</#list>

						start, end, orderByComparator));
					<#list entityFinderArrayableColsList as arrayableentityColumn>
						}
					</#list>

					Collections.sort(list, orderByComparator);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = _findBy${entityFinder.name}(

					<#list entityColumns as entityColumn>
						<#if entityColumn.hasArrayableOperator()>
							${entityColumn.pluralName},
						<#else>
							${entityColumn.name},
						</#if>
					</#list>

					start, end, orderByComparator);
				}

				cacheResult(list);

				if (useFinderCache) {
					${finderCache}.putResult(_finderPathWithPaginationFindBy${entityFinder.name}, finderArgs, list);
				}
			}
			catch (Exception exception) {
				<#if serviceBuilder.isVersionLTE_7_2_0()>
					if (useFinderCache) {
						${finderCache}.removeResult(_finderPathWithPaginationFindBy${entityFinder.name}, finderArgs);
					}
				</#if>

				throw processException(exception);
			}
		}

		return list;

		<#if entity.isChangeTrackingEnabled()>
			}
		</#if>
	}

	private List<${entity.name}> _findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		<#if entityColumn.hasArrayableOperator()>
			${entityColumn.type}[] ${entityColumn.pluralName},
		<#else>
			${entityColumn.type} ${entityColumn.name},
		</#if>
	</#list>

	int start, int end, OrderByComparator<${entity.name}> orderByComparator) {
		List<${entity.name}> list = null;

		<#include "persistence_impl_find_by_arrayable_query.ftl">

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			<#if bindParameter(entityColumns)>
				QueryPos queryPos = QueryPos.getInstance(query);
			</#if>

			<@finderQPos _arrayable = true />

			list = (List<${entity.name}>)QueryUtil.list(query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}
</#if>

<#-- Case 8: !entityFinder.isCollection() -->

<#if !entityFinder.isCollection()>
</#if>

<#-- Case 9: !entityFinder.isCollection() || entityFinder.isUnique() -->

<#if !entityFinder.isCollection() || entityFinder.isUnique()>
	/**
	 * Returns the ${entity.humanName} where ${entityFinder.getHumanConditions(false)} or throws a <code>${noSuchEntity}Exception</code> if it could not be found.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @return the matching ${entity.humanName}
	 * @throws ${noSuchEntity}Exception if a matching ${entity.humanName} could not be found
	 */
	@Override
	public ${entity.name} findBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name}

		<#if entityColumn_has_next>
			,
		</#if>
	</#list>

	) throws ${noSuchEntity}Exception {
		${entity.name} ${entity.variableName} = fetchBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name}

			<#if entityColumn_has_next>
				,
			</#if>
		</#list>

		);

		if ( ${entity.variableName} == null) {
			StringBundler sb = new StringBundler(${(entityColumns?size * 2) + 2});

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			<#list entityColumns as entityColumn>
				sb.append("<#if entityColumn_index != 0>, </#if>${entityColumn.name}${entityColumn.comparator}");
				sb.append(${entityColumn.name});

				<#if !entityColumn_has_next>
					sb.append("}");
				</#if>
			</#list>

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new ${noSuchEntity}Exception(sb.toString());
		}

		return ${entity.variableName};
	}

	/**
	 * Returns the ${entity.humanName} where ${entityFinder.getHumanConditions(false)} or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @return the matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
	 */
	@Override
	public ${entity.name} fetchBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name}

		<#if entityColumn_has_next>
			,
		</#if>
	</#list>

	) {
		return fetchBy${entityFinder.name}(

		<#list entityColumns as entityColumn>
			${entityColumn.name},
		</#list>

		true);
	}

	/**
	 * Returns the ${entity.humanName} where ${entityFinder.getHumanConditions(false)} or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	<#list entityColumns as entityColumn>
	 * @param ${entityColumn.name} the ${entityColumn.humanName}
	</#list>
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ${entity.humanName}, or <code>null</code> if a matching ${entity.humanName} could not be found
	 */
	@Override
	public ${entity.name} fetchBy${entityFinder.name}(

	<#list entityColumns as entityColumn>
		${entityColumn.type} ${entityColumn.name}

		,
	</#list>

	boolean useFinderCache) {
		<#if entity.isChangeTrackingEnabled()>
			try (SafeCloseable safeCloseable = ${ctPersistenceHelper}.setCTCollectionIdWithSafeCloseable(${entity.name}.class)) {
		</#if>

		<#list entityColumns as entityColumn>
			<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
				${entityColumn.name} = Objects.toString(${entityColumn.name}, "");
			</#if>
		</#list>

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
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
		}

		Object result = null;

		if (useFinderCache) {
			result = ${finderCache}.getResult(_finderPathFetchBy${entityFinder.name}, finderArgs, this);
		}

		if (result instanceof ${entity.name}) {
			${entity.name} ${entity.variableName} = (${entity.name})result;

			if (
				<#list entityColumns as entityColumn>
					<#if entityColumn.isPrimitiveType(false)>
						<#if stringUtil.equals(entityColumn.type, "boolean")>
							(${entityColumn.name} != ${entity.variableName}.is${entityColumn.methodName}())
						<#else>
							(${entityColumn.name} != ${entity.variableName}.get${entityColumn.methodName}())
						</#if>
					<#else>
						!Objects.equals(${entityColumn.name}, ${entity.variableName}.get${entityColumn.methodName}())
					</#if>

					<#if entityColumn_has_next>
						||
					</#if>
				</#list>
			) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(${entityColumns?size + 2});

			sb.append(_SQL_SELECT_${entity.alias?upper_case}_WHERE);

			<#include "persistence_impl_finder_cols.ftl">

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				<@finderQPos />

				List<${entity.name}> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						${finderCache}.putResult(_finderPathFetchBy${entityFinder.name}, finderArgs, list);
					}
				}
				else {
					<#if !entityFinder.isUnique()>
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {
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
								}

								_log.warn("${entity.name}PersistenceImpl.fetchBy${entityFinder.name}(<#list entityColumns as entityColumn>${entityColumn.type}, </#list>boolean) with parameters (" + StringUtil.merge(finderArgs) + ") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}
					</#if>

					${entity.name} ${entity.variableName} = list.get(0);

					result = ${entity.variableName};

					cacheResult(${entity.variableName});
				}
			}
			catch (Exception exception) {
				<#if serviceBuilder.isVersionLTE_7_2_0()>
					if (useFinderCache) {
						${finderCache}.removeResult(_finderPathFetchBy${entityFinder.name}, finderArgs);
					}
				</#if>

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (${entity.name})result;
		}

		<#if entity.isChangeTrackingEnabled()>
			}
		</#if>
	}
</#if>