/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.impl;

import com.liferay.expando.kernel.exception.NoSuchColumnException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnTable;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.expando.model.impl.ExpandoColumnImpl;
import com.liferay.portlet.expando.model.impl.ExpandoColumnModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the expando column service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ExpandoColumnPersistenceImpl
	extends BasePersistenceImpl<ExpandoColumn>
	implements ExpandoColumnPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ExpandoColumnUtil</code> to access the expando column persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ExpandoColumnImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByTableId;
	private FinderPath _finderPathWithoutPaginationFindByTableId;
	private FinderPath _finderPathCountByTableId;

	/**
	 * Returns all the expando columns where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByTableId(long tableId) {
		return findByTableId(
			tableId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the expando columns where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @return the range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByTableId(long tableId, int start, int end) {
		return findByTableId(tableId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the expando columns where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByTableId(
		long tableId, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator) {

		return findByTableId(tableId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the expando columns where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByTableId(
		long tableId, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByTableId;
					finderArgs = new Object[] {tableId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByTableId;
				finderArgs = new Object[] {
					tableId, start, end, orderByComparator
				};
			}

			List<ExpandoColumn> list = null;

			if (useFinderCache) {
				list = (List<ExpandoColumn>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ExpandoColumn expandoColumn : list) {
						if (tableId != expandoColumn.getTableId()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_EXPANDOCOLUMN_WHERE);

				sb.append(_FINDER_COLUMN_TABLEID_TABLEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ExpandoColumnModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(tableId);

					list = (List<ExpandoColumn>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first expando column in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando column
	 * @throws NoSuchColumnException if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn findByTableId_First(
			long tableId, OrderByComparator<ExpandoColumn> orderByComparator)
		throws NoSuchColumnException {

		ExpandoColumn expandoColumn = fetchByTableId_First(
			tableId, orderByComparator);

		if (expandoColumn != null) {
			return expandoColumn;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("tableId=");
		sb.append(tableId);

		sb.append("}");

		throw new NoSuchColumnException(sb.toString());
	}

	/**
	 * Returns the first expando column in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando column, or <code>null</code> if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn fetchByTableId_First(
		long tableId, OrderByComparator<ExpandoColumn> orderByComparator) {

		List<ExpandoColumn> list = findByTableId(
			tableId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last expando column in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching expando column
	 * @throws NoSuchColumnException if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn findByTableId_Last(
			long tableId, OrderByComparator<ExpandoColumn> orderByComparator)
		throws NoSuchColumnException {

		ExpandoColumn expandoColumn = fetchByTableId_Last(
			tableId, orderByComparator);

		if (expandoColumn != null) {
			return expandoColumn;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("tableId=");
		sb.append(tableId);

		sb.append("}");

		throw new NoSuchColumnException(sb.toString());
	}

	/**
	 * Returns the last expando column in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching expando column, or <code>null</code> if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn fetchByTableId_Last(
		long tableId, OrderByComparator<ExpandoColumn> orderByComparator) {

		int count = countByTableId(tableId);

		if (count == 0) {
			return null;
		}

		List<ExpandoColumn> list = findByTableId(
			tableId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the expando columns before and after the current expando column in the ordered set where tableId = &#63;.
	 *
	 * @param columnId the primary key of the current expando column
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next expando column
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn[] findByTableId_PrevAndNext(
			long columnId, long tableId,
			OrderByComparator<ExpandoColumn> orderByComparator)
		throws NoSuchColumnException {

		ExpandoColumn expandoColumn = findByPrimaryKey(columnId);

		Session session = null;

		try {
			session = openSession();

			ExpandoColumn[] array = new ExpandoColumnImpl[3];

			array[0] = getByTableId_PrevAndNext(
				session, expandoColumn, tableId, orderByComparator, true);

			array[1] = expandoColumn;

			array[2] = getByTableId_PrevAndNext(
				session, expandoColumn, tableId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ExpandoColumn getByTableId_PrevAndNext(
		Session session, ExpandoColumn expandoColumn, long tableId,
		OrderByComparator<ExpandoColumn> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_EXPANDOCOLUMN_WHERE);

		sb.append(_FINDER_COLUMN_TABLEID_TABLEID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

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
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

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
			sb.append(ExpandoColumnModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(tableId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						expandoColumn)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ExpandoColumn> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the expando columns that the user has permission to view where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the matching expando columns that the user has permission to view
	 */
	@Override
	public List<ExpandoColumn> filterFindByTableId(long tableId) {
		return filterFindByTableId(
			tableId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the expando columns that the user has permission to view where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @return the range of matching expando columns that the user has permission to view
	 */
	@Override
	public List<ExpandoColumn> filterFindByTableId(
		long tableId, int start, int end) {

		return filterFindByTableId(tableId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the expando columns that the user has permissions to view where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching expando columns that the user has permission to view
	 */
	@Override
	public List<ExpandoColumn> filterFindByTableId(
		long tableId, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByTableId(tableId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByTableId(
					tableId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_EXPANDOCOLUMN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_EXPANDOCOLUMN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_TABLEID_TABLEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_EXPANDOCOLUMN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(ExpandoColumnModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(ExpandoColumnModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ExpandoColumn.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, ExpandoColumnImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, ExpandoColumnImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(tableId);

			return (List<ExpandoColumn>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the expando columns before and after the current expando column in the ordered set of expando columns that the user has permission to view where tableId = &#63;.
	 *
	 * @param columnId the primary key of the current expando column
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next expando column
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn[] filterFindByTableId_PrevAndNext(
			long columnId, long tableId,
			OrderByComparator<ExpandoColumn> orderByComparator)
		throws NoSuchColumnException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByTableId_PrevAndNext(
				columnId, tableId, orderByComparator);
		}

		ExpandoColumn expandoColumn = findByPrimaryKey(columnId);

		Session session = null;

		try {
			session = openSession();

			ExpandoColumn[] array = new ExpandoColumnImpl[3];

			array[0] = filterGetByTableId_PrevAndNext(
				session, expandoColumn, tableId, orderByComparator, true);

			array[1] = expandoColumn;

			array[2] = filterGetByTableId_PrevAndNext(
				session, expandoColumn, tableId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ExpandoColumn filterGetByTableId_PrevAndNext(
		Session session, ExpandoColumn expandoColumn, long tableId,
		OrderByComparator<ExpandoColumn> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_EXPANDOCOLUMN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_EXPANDOCOLUMN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_TABLEID_TABLEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_EXPANDOCOLUMN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
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
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
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
				sb.append(ExpandoColumnModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(ExpandoColumnModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ExpandoColumn.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, ExpandoColumnImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, ExpandoColumnImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(tableId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						expandoColumn)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ExpandoColumn> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the expando columns where tableId = &#63; from the database.
	 *
	 * @param tableId the table ID
	 */
	@Override
	public void removeByTableId(long tableId) {
		for (ExpandoColumn expandoColumn :
				findByTableId(
					tableId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(expandoColumn);
		}
	}

	/**
	 * Returns the number of expando columns where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the number of matching expando columns
	 */
	@Override
	public int countByTableId(long tableId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			FinderPath finderPath = _finderPathCountByTableId;

			Object[] finderArgs = new Object[] {tableId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_EXPANDOCOLUMN_WHERE);

				sb.append(_FINDER_COLUMN_TABLEID_TABLEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(tableId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of expando columns that the user has permission to view where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the number of matching expando columns that the user has permission to view
	 */
	@Override
	public int filterCountByTableId(long tableId) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByTableId(tableId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<ExpandoColumn> expandoColumns = findByTableId(tableId);

			expandoColumns = InlineSQLHelperUtil.filter(expandoColumns);

			return expandoColumns.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_EXPANDOCOLUMN_WHERE);

		sb.append(_FINDER_COLUMN_TABLEID_TABLEID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ExpandoColumn.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(tableId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_TABLEID_TABLEID_2 =
		"expandoColumn.tableId = ?";

	private FinderPath _finderPathWithPaginationFindByT_N;
	private FinderPath _finderPathWithoutPaginationFindByT_N;
	private FinderPath _finderPathFetchByT_N;
	private FinderPath _finderPathCountByT_N;
	private FinderPath _finderPathWithPaginationCountByT_N;

	/**
	 * Returns all the expando columns where tableId = &#63; and name = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @return the matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByT_N(long tableId, String[] names) {
		return findByT_N(
			tableId, names, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the expando columns where tableId = &#63; and name = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @return the range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByT_N(
		long tableId, String[] names, int start, int end) {

		return findByT_N(tableId, names, start, end, null);
	}

	/**
	 * Returns an ordered range of all the expando columns where tableId = &#63; and name = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByT_N(
		long tableId, String[] names, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator) {

		return findByT_N(tableId, names, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the expando columns where tableId = &#63; and name = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByT_N(
		long tableId, String[] names, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator,
		boolean useFinderCache) {

		if (names == null) {
			names = new String[0];
		}
		else if (names.length > 1) {
			for (int i = 0; i < names.length; i++) {
				names[i] = Objects.toString(names[i], "");
			}

			names = ArrayUtil.sortedUnique(names);
		}

		if (names.length == 1) {
			ExpandoColumn expandoColumn = fetchByT_N(tableId, names[0]);

			if (expandoColumn == null) {
				return Collections.emptyList();
			}
			else {
				List<ExpandoColumn> list = new ArrayList<ExpandoColumn>(1);

				list.add(expandoColumn);

				return list;
			}
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						tableId, StringUtil.merge(names)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					tableId, StringUtil.merge(names), start, end,
					orderByComparator
				};
			}

			List<ExpandoColumn> list = null;

			if (useFinderCache) {
				list = (List<ExpandoColumn>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByT_N, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ExpandoColumn expandoColumn : list) {
						if ((tableId != expandoColumn.getTableId()) ||
							!ArrayUtil.contains(
								names, expandoColumn.getName())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_EXPANDOCOLUMN_WHERE);

				sb.append(_FINDER_COLUMN_T_N_TABLEID_2);

				if (names.length > 0) {
					sb.append("(");

					for (int i = 0; i < names.length; i++) {
						String name = names[i];

						if (name.isEmpty()) {
							sb.append(_FINDER_COLUMN_T_N_NAME_3);
						}
						else {
							sb.append(_FINDER_COLUMN_T_N_NAME_2);
						}

						if ((i + 1) < names.length) {
							sb.append(WHERE_OR);
						}
					}

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ExpandoColumnModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(tableId);

					for (String name : names) {
						if ((name != null) && !name.isEmpty()) {
							queryPos.add(name);
						}
					}

					list = (List<ExpandoColumn>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByT_N, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the expando column where tableId = &#63; and name = &#63; or throws a <code>NoSuchColumnException</code> if it could not be found.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the matching expando column
	 * @throws NoSuchColumnException if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn findByT_N(long tableId, String name)
		throws NoSuchColumnException {

		ExpandoColumn expandoColumn = fetchByT_N(tableId, name);

		if (expandoColumn == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("tableId=");
			sb.append(tableId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchColumnException(sb.toString());
		}

		return expandoColumn;
	}

	/**
	 * Returns the expando column where tableId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the matching expando column, or <code>null</code> if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn fetchByT_N(long tableId, String name) {
		return fetchByT_N(tableId, name, true);
	}

	/**
	 * Returns the expando column where tableId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching expando column, or <code>null</code> if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn fetchByT_N(
		long tableId, String name, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {tableId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByT_N, finderArgs, this);
			}

			if (result instanceof ExpandoColumn) {
				ExpandoColumn expandoColumn = (ExpandoColumn)result;

				if ((tableId != expandoColumn.getTableId()) ||
					!Objects.equals(name, expandoColumn.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_EXPANDOCOLUMN_WHERE);

				sb.append(_FINDER_COLUMN_T_N_TABLEID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_T_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_T_N_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(tableId);

					if (bindName) {
						queryPos.add(name);
					}

					List<ExpandoColumn> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByT_N, finderArgs, list);
						}
					}
					else {
						ExpandoColumn expandoColumn = list.get(0);

						result = expandoColumn;

						cacheResult(expandoColumn);
					}
				}
				catch (Exception exception) {
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
				return (ExpandoColumn)result;
			}
		}
	}

	/**
	 * Removes the expando column where tableId = &#63; and name = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the expando column that was removed
	 */
	@Override
	public ExpandoColumn removeByT_N(long tableId, String name)
		throws NoSuchColumnException {

		ExpandoColumn expandoColumn = findByT_N(tableId, name);

		return remove(expandoColumn);
	}

	/**
	 * Returns the number of expando columns where tableId = &#63; and name = &#63;.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the number of matching expando columns
	 */
	@Override
	public int countByT_N(long tableId, String name) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByT_N;

			Object[] finderArgs = new Object[] {tableId, name};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_EXPANDOCOLUMN_WHERE);

				sb.append(_FINDER_COLUMN_T_N_TABLEID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_T_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_T_N_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(tableId);

					if (bindName) {
						queryPos.add(name);
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of expando columns where tableId = &#63; and name = any &#63;.
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @return the number of matching expando columns
	 */
	@Override
	public int countByT_N(long tableId, String[] names) {
		if (names == null) {
			names = new String[0];
		}
		else if (names.length > 1) {
			for (int i = 0; i < names.length; i++) {
				names[i] = Objects.toString(names[i], "");
			}

			names = ArrayUtil.sortedUnique(names);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			Object[] finderArgs = new Object[] {
				tableId, StringUtil.merge(names)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByT_N, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_EXPANDOCOLUMN_WHERE);

				sb.append(_FINDER_COLUMN_T_N_TABLEID_2);

				if (names.length > 0) {
					sb.append("(");

					for (int i = 0; i < names.length; i++) {
						String name = names[i];

						if (name.isEmpty()) {
							sb.append(_FINDER_COLUMN_T_N_NAME_3);
						}
						else {
							sb.append(_FINDER_COLUMN_T_N_NAME_2);
						}

						if ((i + 1) < names.length) {
							sb.append(WHERE_OR);
						}
					}

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(tableId);

					for (String name : names) {
						if ((name != null) && !name.isEmpty()) {
							queryPos.add(name);
						}
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByT_N, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of expando columns that the user has permission to view where tableId = &#63; and name = &#63;.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the number of matching expando columns that the user has permission to view
	 */
	@Override
	public int filterCountByT_N(long tableId, String name) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByT_N(tableId, name);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<ExpandoColumn> expandoColumns = Arrays.asList(
				fetchByT_N(tableId, name));

			expandoColumns = InlineSQLHelperUtil.filter(expandoColumns);

			return expandoColumns.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_EXPANDOCOLUMN_WHERE);

		sb.append(_FINDER_COLUMN_T_N_TABLEID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_T_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_T_N_NAME_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ExpandoColumn.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(tableId);

			if (bindName) {
				queryPos.add(name);
			}

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the number of expando columns that the user has permission to view where tableId = &#63; and name = any &#63;.
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @return the number of matching expando columns that the user has permission to view
	 */
	@Override
	public int filterCountByT_N(long tableId, String[] names) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByT_N(tableId, names);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<ExpandoColumn> expandoColumns = InlineSQLHelperUtil.filter(
				findByT_N(tableId, names));

			return expandoColumns.size();
		}

		if (names == null) {
			names = new String[0];
		}
		else if (names.length > 1) {
			for (int i = 0; i < names.length; i++) {
				names[i] = Objects.toString(names[i], "");
			}

			names = ArrayUtil.sortedUnique(names);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_EXPANDOCOLUMN_WHERE);

		sb.append(_FINDER_COLUMN_T_N_TABLEID_2);

		if (names.length > 0) {
			sb.append("(");

			for (int i = 0; i < names.length; i++) {
				String name = names[i];

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_T_N_NAME_3);
				}
				else {
					sb.append(_FINDER_COLUMN_T_N_NAME_2);
				}

				if ((i + 1) < names.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ExpandoColumn.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(tableId);

			for (String name : names) {
				if ((name != null) && !name.isEmpty()) {
					queryPos.add(name);
				}
			}

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_T_N_TABLEID_2 =
		"expandoColumn.tableId = ? AND ";

	private static final String _FINDER_COLUMN_T_N_NAME_2 =
		"expandoColumn.name = ?";

	private static final String _FINDER_COLUMN_T_N_NAME_3 =
		"(expandoColumn.name IS NULL OR expandoColumn.name = '')";

	public ExpandoColumnPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ExpandoColumn.class);

		setModelImplClass(ExpandoColumnImpl.class);
		setModelPKClass(long.class);

		setTable(ExpandoColumnTable.INSTANCE);
	}

	/**
	 * Caches the expando column in the entity cache if it is enabled.
	 *
	 * @param expandoColumn the expando column
	 */
	@Override
	public void cacheResult(ExpandoColumn expandoColumn) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					expandoColumn.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				ExpandoColumnImpl.class, expandoColumn.getPrimaryKey(),
				expandoColumn);

			FinderCacheUtil.putResult(
				_finderPathFetchByT_N,
				new Object[] {
					expandoColumn.getTableId(), expandoColumn.getName()
				},
				expandoColumn);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the expando columns in the entity cache if it is enabled.
	 *
	 * @param expandoColumns the expando columns
	 */
	@Override
	public void cacheResult(List<ExpandoColumn> expandoColumns) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (expandoColumns.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ExpandoColumn expandoColumn : expandoColumns) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						expandoColumn.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						ExpandoColumnImpl.class,
						expandoColumn.getPrimaryKey()) == null) {

					cacheResult(expandoColumn);
				}
			}
		}
	}

	/**
	 * Clears the cache for all expando columns.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(ExpandoColumnImpl.class);

		FinderCacheUtil.clearCache(ExpandoColumnImpl.class);
	}

	/**
	 * Clears the cache for the expando column.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ExpandoColumn expandoColumn) {
		EntityCacheUtil.removeResult(ExpandoColumnImpl.class, expandoColumn);
	}

	@Override
	public void clearCache(List<ExpandoColumn> expandoColumns) {
		for (ExpandoColumn expandoColumn : expandoColumns) {
			EntityCacheUtil.removeResult(
				ExpandoColumnImpl.class, expandoColumn);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(ExpandoColumnImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(ExpandoColumnImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ExpandoColumnModelImpl expandoColumnModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					expandoColumnModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				expandoColumnModelImpl.getTableId(),
				expandoColumnModelImpl.getName()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByT_N, args, expandoColumnModelImpl);
		}
	}

	/**
	 * Creates a new expando column with the primary key. Does not add the expando column to the database.
	 *
	 * @param columnId the primary key for the new expando column
	 * @return the new expando column
	 */
	@Override
	public ExpandoColumn create(long columnId) {
		ExpandoColumn expandoColumn = new ExpandoColumnImpl();

		expandoColumn.setNew(true);
		expandoColumn.setPrimaryKey(columnId);

		expandoColumn.setCompanyId(CompanyThreadLocal.getCompanyId());

		return expandoColumn;
	}

	/**
	 * Removes the expando column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column that was removed
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn remove(long columnId) throws NoSuchColumnException {
		return remove((Serializable)columnId);
	}

	/**
	 * Removes the expando column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the expando column
	 * @return the expando column that was removed
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn remove(Serializable primaryKey)
		throws NoSuchColumnException {

		Session session = null;

		try {
			session = openSession();

			ExpandoColumn expandoColumn = (ExpandoColumn)session.get(
				ExpandoColumnImpl.class, primaryKey);

			if (expandoColumn == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchColumnException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(expandoColumn);
		}
		catch (NoSuchColumnException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected ExpandoColumn removeImpl(ExpandoColumn expandoColumn) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(expandoColumn)) {
				expandoColumn = (ExpandoColumn)session.get(
					ExpandoColumnImpl.class, expandoColumn.getPrimaryKeyObj());
			}

			if ((expandoColumn != null) &&
				CTPersistenceHelperUtil.isRemove(expandoColumn)) {

				session.delete(expandoColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (expandoColumn != null) {
			clearCache(expandoColumn);
		}

		return expandoColumn;
	}

	@Override
	public ExpandoColumn updateImpl(ExpandoColumn expandoColumn) {
		boolean isNew = expandoColumn.isNew();

		if (!(expandoColumn instanceof ExpandoColumnModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(expandoColumn.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					expandoColumn);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in expandoColumn proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ExpandoColumn implementation " +
					expandoColumn.getClass());
		}

		ExpandoColumnModelImpl expandoColumnModelImpl =
			(ExpandoColumnModelImpl)expandoColumn;

		if (!expandoColumnModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				expandoColumn.setModifiedDate(date);
			}
			else {
				expandoColumn.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(expandoColumn)) {
				if (!isNew) {
					session.evict(
						ExpandoColumnImpl.class,
						expandoColumn.getPrimaryKeyObj());
				}

				session.save(expandoColumn);
			}
			else {
				expandoColumn = (ExpandoColumn)session.merge(expandoColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			ExpandoColumnImpl.class, expandoColumnModelImpl, false, true);

		cacheUniqueFindersCache(expandoColumnModelImpl);

		if (isNew) {
			expandoColumn.setNew(false);
		}

		expandoColumn.resetOriginalValues();

		return expandoColumn;
	}

	/**
	 * Returns the expando column with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the expando column
	 * @return the expando column
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn findByPrimaryKey(Serializable primaryKey)
		throws NoSuchColumnException {

		ExpandoColumn expandoColumn = fetchByPrimaryKey(primaryKey);

		if (expandoColumn == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchColumnException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return expandoColumn;
	}

	/**
	 * Returns the expando column with the primary key or throws a <code>NoSuchColumnException</code> if it could not be found.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn findByPrimaryKey(long columnId)
		throws NoSuchColumnException {

		return findByPrimaryKey((Serializable)columnId);
	}

	/**
	 * Returns the expando column with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the expando column
	 * @return the expando column, or <code>null</code> if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				ExpandoColumn.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		ExpandoColumn expandoColumn = (ExpandoColumn)EntityCacheUtil.getResult(
			ExpandoColumnImpl.class, primaryKey);

		if (expandoColumn != null) {
			return expandoColumn;
		}

		Session session = null;

		try {
			session = openSession();

			expandoColumn = (ExpandoColumn)session.get(
				ExpandoColumnImpl.class, primaryKey);

			if (expandoColumn != null) {
				cacheResult(expandoColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return expandoColumn;
	}

	/**
	 * Returns the expando column with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column, or <code>null</code> if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn fetchByPrimaryKey(long columnId) {
		return fetchByPrimaryKey((Serializable)columnId);
	}

	@Override
	public Map<Serializable, ExpandoColumn> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(ExpandoColumn.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, ExpandoColumn> map =
			new HashMap<Serializable, ExpandoColumn>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			ExpandoColumn expandoColumn = fetchByPrimaryKey(primaryKey);

			if (expandoColumn != null) {
				map.put(primaryKey, expandoColumn);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						ExpandoColumn.class, primaryKey)) {

				ExpandoColumn expandoColumn =
					(ExpandoColumn)EntityCacheUtil.getResult(
						ExpandoColumnImpl.class, primaryKey);

				if (expandoColumn == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, expandoColumn);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(primaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (ExpandoColumn expandoColumn :
					(List<ExpandoColumn>)query.list()) {

				map.put(expandoColumn.getPrimaryKeyObj(), expandoColumn);

				cacheResult(expandoColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the expando columns.
	 *
	 * @return the expando columns
	 */
	@Override
	public List<ExpandoColumn> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the expando columns.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @return the range of expando columns
	 */
	@Override
	public List<ExpandoColumn> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the expando columns.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of expando columns
	 */
	@Override
	public List<ExpandoColumn> findAll(
		int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the expando columns.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of expando columns
	 */
	@Override
	public List<ExpandoColumn> findAll(
		int start, int end, OrderByComparator<ExpandoColumn> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindAll;
					finderArgs = FINDER_ARGS_EMPTY;
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindAll;
				finderArgs = new Object[] {start, end, orderByComparator};
			}

			List<ExpandoColumn> list = null;

			if (useFinderCache) {
				list = (List<ExpandoColumn>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_EXPANDOCOLUMN);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_EXPANDOCOLUMN;

					sql = sql.concat(ExpandoColumnModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<ExpandoColumn>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Removes all the expando columns from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ExpandoColumn expandoColumn : findAll()) {
			remove(expandoColumn);
		}
	}

	/**
	 * Returns the number of expando columns.
	 *
	 * @return the number of expando columns
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ExpandoColumn.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_EXPANDOCOLUMN);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathCountAll, FINDER_ARGS_EMPTY, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "columnId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EXPANDOCOLUMN;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return ExpandoColumnModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ExpandoColumn";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("tableId");
		ctStrictColumnNames.add("name");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("defaultData");
		ctMergeColumnNames.add("typeSettings");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("columnId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"tableId", "name"});
	}

	/**
	 * Initializes the expando column persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByTableId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTableId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"tableId"}, true);

		_finderPathWithoutPaginationFindByTableId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTableId",
			new String[] {Long.class.getName()}, new String[] {"tableId"},
			true);

		_finderPathCountByTableId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTableId",
			new String[] {Long.class.getName()}, new String[] {"tableId"},
			false);

		_finderPathWithPaginationFindByT_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"tableId", "name"}, true);

		_finderPathWithoutPaginationFindByT_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"tableId", "name"}, true);

		_finderPathFetchByT_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByT_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"tableId", "name"}, true);

		_finderPathCountByT_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"tableId", "name"}, false);

		_finderPathWithPaginationCountByT_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByT_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"tableId", "name"}, false);

		ExpandoColumnUtil.setPersistence(this);
	}

	public void destroy() {
		ExpandoColumnUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ExpandoColumnImpl.class.getName());
	}

	private static final String _SQL_SELECT_EXPANDOCOLUMN =
		"SELECT expandoColumn FROM ExpandoColumn expandoColumn";

	private static final String _SQL_SELECT_EXPANDOCOLUMN_WHERE =
		"SELECT expandoColumn FROM ExpandoColumn expandoColumn WHERE ";

	private static final String _SQL_COUNT_EXPANDOCOLUMN =
		"SELECT COUNT(expandoColumn) FROM ExpandoColumn expandoColumn";

	private static final String _SQL_COUNT_EXPANDOCOLUMN_WHERE =
		"SELECT COUNT(expandoColumn) FROM ExpandoColumn expandoColumn WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"expandoColumn.columnId";

	private static final String _FILTER_SQL_SELECT_EXPANDOCOLUMN_WHERE =
		"SELECT DISTINCT {expandoColumn.*} FROM ExpandoColumn expandoColumn WHERE ";

	private static final String
		_FILTER_SQL_SELECT_EXPANDOCOLUMN_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {ExpandoColumn.*} FROM (SELECT DISTINCT expandoColumn.columnId FROM ExpandoColumn expandoColumn WHERE ";

	private static final String
		_FILTER_SQL_SELECT_EXPANDOCOLUMN_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN ExpandoColumn ON TEMP_TABLE.columnId = ExpandoColumn.columnId";

	private static final String _FILTER_SQL_COUNT_EXPANDOCOLUMN_WHERE =
		"SELECT COUNT(DISTINCT expandoColumn.columnId) AS COUNT_VALUE FROM ExpandoColumn expandoColumn WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "expandoColumn";

	private static final String _FILTER_ENTITY_TABLE = "ExpandoColumn";

	private static final String _ORDER_BY_ENTITY_ALIAS = "expandoColumn.";

	private static final String _ORDER_BY_ENTITY_TABLE = "ExpandoColumn.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ExpandoColumn exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ExpandoColumn exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoColumnPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}