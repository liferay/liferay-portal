/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class CollectionPersistenceFinder
	<T extends BaseModel<T>, E extends NoSuchModelException>
		extends BasePersistenceFinder<T, E> {

	@SafeVarargs
	public CollectionPersistenceFinder(
		BasePersistenceImpl<T, E> basePersistenceImpl,
		FinderPath paginatedFindPath, FinderPath unpaginatedFindPath,
		FinderPath countFinderPath, String sqlSelectWhere, String sqlCountWhere,
		String defaultOrderByJpql, String orderByEntityAlias, String where,
		String dbWhere, UniquePersistenceFinder<T, E> uniquePersistenceFinder,
		FinderColumn<T>... finderColumns) {

		super(
			basePersistenceImpl, sqlSelectWhere, where, dbWhere, finderColumns);

		_paginatedFindPath = paginatedFindPath;
		_unpaginatedFindPath = unpaginatedFindPath;
		_countFinderPath = countFinderPath;
		_sqlCountWhere = sqlCountWhere;
		_defaultOrderByJpql = defaultOrderByJpql;
		_orderByEntityAlias = orderByEntityAlias;
		_uniquePersistenceFinder = uniquePersistenceFinder;

		List<Integer> arrayableIndexes = new ArrayList<>();

		for (int i = 0; i < finderColumns.length; i++) {
			if (finderColumns[i] instanceof ArrayableFinderColumn) {
				arrayableIndexes.add(i);
			}
		}

		if (arrayableIndexes.isEmpty()) {
			_arrayableIndexes = null;
		}
		else {
			_arrayableIndexes = arrayableIndexes.toArray(new Integer[0]);
		}
	}

	public int count(FinderCache finderCache, Object[] values) {
		try (SafeCloseable safeCloseable =
				setCTCollectionIdWithSafeCloseable()) {

			if ((_uniquePersistenceFinder != null) &&
				_unwrapIfAllArrayableLengthOne(values)) {

				return _uniquePersistenceFinder.count(finderCache, values);
			}

			normalizeValues(values);

			Object[] finderArgs = buildFinderArgs(values);

			Long count = (Long)finderCache.getResult(
				_countFinderPath, finderArgs, basePersistenceImpl);

			if (count == null) {
				String sql = buildSQLWhere(_sqlCountWhere, values, false);

				Session session = null;

				try {
					session = basePersistenceImpl.openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					bindQueryParams(queryPos, values);

					count = (Long)query.uniqueResult();

					finderCache.putResult(_countFinderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw basePersistenceImpl.processException(exception);
				}
				finally {
					basePersistenceImpl.closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	public T fetchFirst(
		FinderCache finderCache, Object[] values,
		OrderByComparator<T> orderByComparator) {

		List<T> list = find(finderCache, values, 0, 1, orderByComparator, true);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<T> find(
		FinderCache finderCache, Object[] values, int start, int end,
		OrderByComparator<T> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				setCTCollectionIdWithSafeCloseable()) {

			if ((_uniquePersistenceFinder != null) &&
				_unwrapIfAllArrayableLengthOne(values)) {

				T entity = _uniquePersistenceFinder.fetch(
					finderCache, values, useFinderCache);

				if (entity == null) {
					return Collections.emptyList();
				}

				return Collections.singletonList(entity);
			}

			normalizeValues(values);

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((_unpaginatedFindPath != null) &&
				(start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null) &&
				!_isMultiElementArrayable(values)) {

				if (useFinderCache) {
					finderPath = _unpaginatedFindPath;
					finderArgs = buildFinderArgs(values);
				}
			}
			else if (useFinderCache) {
				finderPath = _paginatedFindPath;
				finderArgs = _buildPaginatedFinderArgs(
					values, start, end, orderByComparator);
			}

			List<T> list = null;

			if (useFinderCache) {
				list = (List<T>)finderCache.getResult(
					finderPath, finderArgs, basePersistenceImpl);

				if ((list != null) && !list.isEmpty()) {
					for (T entity : list) {
						if (!matchesAll(entity, values)) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				String sql = _buildFindSql(values, orderByComparator);

				Session session = null;

				try {
					session = basePersistenceImpl.openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					bindQueryParams(queryPos, values);

					list = (List<T>)QueryUtil.list(
						query, basePersistenceImpl.getDialect(), start, end);

					basePersistenceImpl.cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw basePersistenceImpl.processException(exception);
				}
				finally {
					basePersistenceImpl.closeSession(session);
				}
			}

			return list;
		}
	}

	public T findFirst(
			FinderCache finderCache, Object[] values,
			OrderByComparator<T> orderByComparator)
		throws E {

		T entity = fetchFirst(finderCache, values, orderByComparator);

		if (entity != null) {
			return entity;
		}

		throw basePersistenceImpl.newNoSuchModelException(
			buildNoSuchKeyMessage(values));
	}

	public void remove(FinderCache finderCache, Object[] values) {
		for (T entity :
				find(
					finderCache, values, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null, true)) {

			basePersistenceImpl.remove(entity);
		}
	}

	private String _buildFindSql(
		Object[] values, OrderByComparator<T> orderByComparator) {

		StringBundler sb = null;

		if (orderByComparator == null) {
			sb = new StringBundler((finderColumns.length * 2) + 3);
		}
		else {
			sb = new StringBundler(
				(finderColumns.length * 2) + 3 +
					(orderByComparator.getOrderByFields().length * 2));
		}

		sb.append(sqlSelectWhere);

		for (int i = 0; i < finderColumns.length; i++) {
			String fragment = finderColumns[i].getSqlFragment(values[i], false);

			if (fragment.isEmpty()) {
				continue;
			}

			sb.append(fragment);
			sb.append(" AND ");
		}

		if (!where.isEmpty()) {
			sb.append(where);
		}
		else if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		if (orderByComparator == null) {
			sb.append(_defaultOrderByJpql);
		}
		else {
			basePersistenceImpl.appendOrderByComparator(
				sb, _orderByEntityAlias, orderByComparator);
		}

		return sb.toString();
	}

	private Object[] _buildPaginatedFinderArgs(
		Object[] values, int start, int end,
		OrderByComparator<T> orderByComparator) {

		Object[] finderArgs = new Object[finderColumns.length + 3];

		for (int i = 0; i < finderColumns.length; i++) {
			finderArgs[i] = finderColumns[i].toFinderArg(values[i]);
		}

		finderArgs[finderColumns.length] = start;
		finderArgs[finderColumns.length + 1] = end;
		finderArgs[finderColumns.length + 2] = orderByComparator;

		return finderArgs;
	}

	private boolean _isMultiElementArrayable(Object[] values) {
		if (_arrayableIndexes == null) {
			return false;
		}

		for (int index : _arrayableIndexes) {
			Object[] array = (Object[])values[index];

			if (array.length > 1) {
				return true;
			}
		}

		return false;
	}

	private boolean _unwrapIfAllArrayableLengthOne(Object[] values) {
		if (_arrayableIndexes == null) {
			return false;
		}

		for (int index : _arrayableIndexes) {
			if (Array.getLength(values[index]) != 1) {
				return false;
			}
		}

		for (int index : _arrayableIndexes) {
			values[index] = Array.get(values[index], 0);
		}

		return true;
	}

	private final Integer[] _arrayableIndexes;
	private final FinderPath _countFinderPath;
	private final String _defaultOrderByJpql;
	private final String _orderByEntityAlias;
	private final FinderPath _paginatedFindPath;
	private final String _sqlCountWhere;
	private final UniquePersistenceFinder<T, E> _uniquePersistenceFinder;
	private final FinderPath _unpaginatedFindPath;

}