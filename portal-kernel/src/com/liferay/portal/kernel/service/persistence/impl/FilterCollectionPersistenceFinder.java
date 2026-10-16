/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.function.Function;

/**
 * @author Shuyang Zhou
 */
public class FilterCollectionPersistenceFinder
	<T extends BaseModel<T>, E extends NoSuchModelException>
		extends CollectionPersistenceFinder<T, E> {

	@SafeVarargs
	public FilterCollectionPersistenceFinder(
		BasePersistenceImpl<T, E> basePersistenceImpl,
		Function<T, Long> classPKFunction, FinderPath countFinderPath,
		String dbWhere, String defaultOrderByJpql, String orderByEntityAlias,
		FinderPath paginatedFindPath, String sqlCountWhere,
		String sqlSelectWhere,
		UniquePersistenceFinder<T, E> uniquePersistenceFinder,
		FinderPath unpaginatedFindPath, String where,
		FinderColumn<T>... finderColumns) {

		super(
			basePersistenceImpl, paginatedFindPath, unpaginatedFindPath,
			countFinderPath, sqlSelectWhere, sqlCountWhere, defaultOrderByJpql,
			orderByEntityAlias, where, dbWhere, uniquePersistenceFinder,
			finderColumns);

		_classPKFunction = classPKFunction;

		String entityAlias = basePersistenceImpl.getEntityAlias();

		_filterPKColumn = StringBundler.concat(
			entityAlias, ".", basePersistenceImpl.getFilterPKColumnName());

		String tableName = basePersistenceImpl.getTableName();

		_filterSqlSelectWhere = StringBundler.concat(
			"SELECT DISTINCT {", entityAlias, ".*} FROM ", tableName, " ",
			entityAlias, " WHERE ");

		String pkColumnName = basePersistenceImpl.getPKColumnName();

		_filterSqlSelectNoInlineDistinctWhere1 = StringBundler.concat(
			"SELECT {", tableName, ".*} FROM (SELECT DISTINCT ", entityAlias,
			".", pkColumnName, " FROM ", tableName, " ", entityAlias,
			" WHERE ");
		_filterSqlSelectNoInlineDistinctWhere2 = StringBundler.concat(
			") TEMP_TABLE INNER JOIN ", tableName, " ON TEMP_TABLE.",
			pkColumnName, " = ", tableName, ".", pkColumnName);
		_filterSqlCountWhere = StringBundler.concat(
			"SELECT COUNT(DISTINCT ", entityAlias, ".", pkColumnName,
			") AS COUNT_VALUE FROM ", tableName, " ", entityAlias, " WHERE ");

		_orderByEntityTable = tableName.concat(".");
	}

	public int filterCount(FinderCache finderCache, Object[] values) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return count(finderCache, values);
		}

		return _filterCount(finderCache, values, _EMPTY_GROUP_IDS);
	}

	public int filterCount(
		FinderCache finderCache, Object[] values, long groupId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return count(finderCache, values);
		}

		return _filterCount(finderCache, values, new long[] {groupId});
	}

	public int filterCount(
		FinderCache finderCache, Object[] values, long companyId,
		long groupId) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, groupId)) {
			return count(finderCache, values);
		}

		return _filterCount(finderCache, values, _EMPTY_GROUP_IDS);
	}

	public int filterCount(
		FinderCache finderCache, Object[] values, long[] groupIds) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return count(finderCache, values);
		}

		return _filterCount(finderCache, values, groupIds);
	}

	public List<T> filterFind(
		FinderCache finderCache, Object[] values, int start, int end,
		OrderByComparator<T> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return find(
				finderCache, values, start, end, orderByComparator, true);
		}

		return _filterFind(
			finderCache, values, start, end, orderByComparator,
			_EMPTY_GROUP_IDS);
	}

	public List<T> filterFind(
		FinderCache finderCache, Object[] values, int start, int end,
		OrderByComparator<T> orderByComparator, long groupId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return find(
				finderCache, values, start, end, orderByComparator, true);
		}

		return _filterFind(
			finderCache, values, start, end, orderByComparator,
			new long[] {groupId});
	}

	public List<T> filterFind(
		FinderCache finderCache, Object[] values, int start, int end,
		OrderByComparator<T> orderByComparator, long companyId, long groupId) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, groupId)) {
			return find(
				finderCache, values, start, end, orderByComparator, true);
		}

		return _filterFind(
			finderCache, values, start, end, orderByComparator,
			_EMPTY_GROUP_IDS);
	}

	public List<T> filterFind(
		FinderCache finderCache, Object[] values, int start, int end,
		OrderByComparator<T> orderByComparator, long[] groupIds) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return find(
				finderCache, values, start, end, orderByComparator, true);
		}

		return _filterFind(
			finderCache, values, start, end, orderByComparator, groupIds);
	}

	private String _buildFilterFindSql(
		boolean inlineDistinct, Object[] values,
		OrderByComparator<T> orderByComparator) {

		StringBundler sb = null;

		int extraSize = inlineDistinct ? 2 : 3;

		if (orderByComparator == null) {
			sb = new StringBundler(extraSize);
		}
		else {
			sb = new StringBundler(
				extraSize + (orderByComparator.getOrderByFields().length * 2));
		}

		if (inlineDistinct) {
			sb.append(buildSQLWhere(_filterSqlSelectWhere, values, true));
		}
		else {
			sb.append(
				buildSQLWhere(
					_filterSqlSelectNoInlineDistinctWhere1, values, true));
			sb.append(_filterSqlSelectNoInlineDistinctWhere2);
		}

		if (orderByComparator != null) {
			basePersistenceImpl.appendOrderByComparator(
				sb,
				inlineDistinct ? basePersistenceImpl.getEntityAliasPrefix() :
					_orderByEntityTable,
				orderByComparator, true);
		}
		else if (inlineDistinct) {
			sb.append(basePersistenceImpl.getDefaultOrderBySQLInlineDistinct());
		}
		else {
			sb.append(basePersistenceImpl.getDefaultOrderBySQL());
		}

		return sb.toString();
	}

	private int _filterCount(
		FinderCache finderCache, Object[] values, long[] groupIds) {

		if (basePersistenceImpl.isPermissionsInMemoryFilterEnabled()) {
			List<T> list = find(
				finderCache, values, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null,
				true);

			list = InlineSQLHelperUtil.filter(_classPKFunction, list, groupIds);

			return list.size();
		}

		normalizeValues(values);

		String sql = _replacePermissionCheck(
			buildSQLWhere(_filterSqlCountWhere, values, true), groupIds);

		Session session = null;

		try {
			session = basePersistenceImpl.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				BasePersistenceImpl.COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			bindQueryParams(queryPos, values);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw basePersistenceImpl.processException(exception);
		}
		finally {
			basePersistenceImpl.closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	private List<T> _filterFind(
		FinderCache finderCache, Object[] values, int start, int end,
		OrderByComparator<T> orderByComparator, long[] groupIds) {

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			basePersistenceImpl.isPermissionsInMemoryFilterEnabled()) {

			List<T> list = find(
				finderCache, values, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				orderByComparator, true);

			return InlineSQLHelperUtil.filter(_classPKFunction, list, groupIds);
		}

		normalizeValues(values);

		boolean inlineDistinct = basePersistenceImpl.getDB(
		).isSupportsInlineDistinct();

		String sql = _replacePermissionCheck(
			_buildFilterFindSql(inlineDistinct, values, orderByComparator),
			groupIds);

		Session session = null;

		try {
			session = basePersistenceImpl.openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (inlineDistinct) {
				sqlQuery.addEntity(
					basePersistenceImpl.getEntityAlias(),
					basePersistenceImpl.getModelImplClass());
			}
			else {
				sqlQuery.addEntity(
					basePersistenceImpl.getTableName(),
					basePersistenceImpl.getModelImplClass());
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			bindQueryParams(queryPos, values);

			return (List<T>)QueryUtil.list(
				sqlQuery, basePersistenceImpl.getDialect(), start, end);
		}
		catch (Exception exception) {
			throw basePersistenceImpl.processException(exception);
		}
		finally {
			basePersistenceImpl.closeSession(session);
		}
	}

	private String _replacePermissionCheck(String sql, long[] groupIds) {
		Class<T> modelClass = basePersistenceImpl.getModelClass();

		if (groupIds.length == 0) {
			return InlineSQLHelperUtil.replacePermissionCheck(
				sql, modelClass.getName(), _filterPKColumn);
		}

		return InlineSQLHelperUtil.replacePermissionCheck(
			sql, modelClass.getName(), _filterPKColumn, groupIds);
	}

	private static final long[] _EMPTY_GROUP_IDS = new long[0];

	private final Function<T, Long> _classPKFunction;
	private final String _filterPKColumn;
	private final String _filterSqlCountWhere;
	private final String _filterSqlSelectNoInlineDistinctWhere1;
	private final String _filterSqlSelectNoInlineDistinctWhere2;
	private final String _filterSqlSelectWhere;
	private final String _orderByEntityTable;

}