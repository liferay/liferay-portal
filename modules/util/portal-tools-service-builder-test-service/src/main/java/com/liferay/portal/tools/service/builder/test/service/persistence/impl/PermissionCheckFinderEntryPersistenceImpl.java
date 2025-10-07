/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchPermissionCheckFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.PermissionCheckFinderEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.PermissionCheckFinderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.PermissionCheckFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.PermissionCheckFinderEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the permission check finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PermissionCheckFinderEntryPersistenceImpl
	extends BasePersistenceImpl<PermissionCheckFinderEntry>
	implements PermissionCheckFinderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PermissionCheckFinderEntryUtil</code> to access the permission check finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PermissionCheckFinderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the permission check finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByGroupId;
				finderArgs = new Object[] {groupId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByGroupId;
			finderArgs = new Object[] {groupId, start, end, orderByComparator};
		}

		List<PermissionCheckFinderEntry> list = null;

		if (useFinderCache) {
			list = (List<PermissionCheckFinderEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PermissionCheckFinderEntry permissionCheckFinderEntry :
						list) {

					if (groupId != permissionCheckFinderEntry.getGroupId()) {
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

			sb.append(_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PermissionCheckFinderEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<PermissionCheckFinderEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
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

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a matching permission check finder entry could not be found
	 */
	@Override
	public PermissionCheckFinderEntry findByGroupId_First(
			long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException {

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			fetchByGroupId_First(groupId, orderByComparator);

		if (permissionCheckFinderEntry != null) {
			return permissionCheckFinderEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPermissionCheckFinderEntryException(sb.toString());
	}

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry, or <code>null</code> if a matching permission check finder entry could not be found
	 */
	@Override
	public PermissionCheckFinderEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		List<PermissionCheckFinderEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a matching permission check finder entry could not be found
	 */
	@Override
	public PermissionCheckFinderEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException {

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			fetchByGroupId_Last(groupId, orderByComparator);

		if (permissionCheckFinderEntry != null) {
			return permissionCheckFinderEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPermissionCheckFinderEntryException(sb.toString());
	}

	/**
	 * Returns the last permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching permission check finder entry, or <code>null</code> if a matching permission check finder entry could not be found
	 */
	@Override
	public PermissionCheckFinderEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<PermissionCheckFinderEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the permission check finder entries before and after the current permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the current permission check finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry[] findByGroupId_PrevAndNext(
			long permissionCheckFinderEntryId, long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException {

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			findByPrimaryKey(permissionCheckFinderEntryId);

		Session session = null;

		try {
			session = openSession();

			PermissionCheckFinderEntry[] array =
				new PermissionCheckFinderEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, permissionCheckFinderEntry, groupId, orderByComparator,
				true);

			array[1] = permissionCheckFinderEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, permissionCheckFinderEntry, groupId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PermissionCheckFinderEntry getByGroupId_PrevAndNext(
		Session session, PermissionCheckFinderEntry permissionCheckFinderEntry,
		long groupId,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

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
			sb.append(PermissionCheckFinderEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						permissionCheckFinderEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PermissionCheckFinderEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching permission check finder entries that the user has permission to view
	 */
	@Override
	public List<PermissionCheckFinderEntry> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries that the user has permission to view
	 */
	@Override
	public List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries that the user has permission to view
	 */
	@Override
	public List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId(groupId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					PermissionCheckFinderEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PermissionCheckFinderEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PermissionCheckFinderEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PermissionCheckFinderEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PermissionCheckFinderEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<PermissionCheckFinderEntry>)QueryUtil.list(
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
	 * Returns the permission check finder entries before and after the current permission check finder entry in the ordered set of permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the current permission check finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry[] filterFindByGroupId_PrevAndNext(
			long permissionCheckFinderEntryId, long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				permissionCheckFinderEntryId, groupId, orderByComparator);
		}

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			findByPrimaryKey(permissionCheckFinderEntryId);

		Session session = null;

		try {
			session = openSession();

			PermissionCheckFinderEntry[] array =
				new PermissionCheckFinderEntryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, permissionCheckFinderEntry, groupId, orderByComparator,
				true);

			array[1] = permissionCheckFinderEntry;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, permissionCheckFinderEntry, groupId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PermissionCheckFinderEntry filterGetByGroupId_PrevAndNext(
		Session session, PermissionCheckFinderEntry permissionCheckFinderEntry,
		long groupId,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					PermissionCheckFinderEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PermissionCheckFinderEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PermissionCheckFinderEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, PermissionCheckFinderEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, PermissionCheckFinderEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						permissionCheckFinderEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PermissionCheckFinderEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the permission check finder entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (PermissionCheckFinderEntry permissionCheckFinderEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(permissionCheckFinderEntry);
		}
	}

	/**
	 * Returns the number of permission check finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PERMISSIONCHECKFINDERENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
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

	/**
	 * Returns the number of permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PermissionCheckFinderEntry> permissionCheckFinderEntries =
				findByGroupId(groupId);

			permissionCheckFinderEntries = InlineSQLHelperUtil.filter(
				permissionCheckFinderEntries, groupId);

			return permissionCheckFinderEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PERMISSIONCHECKFINDERENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PermissionCheckFinderEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"permissionCheckFinderEntry.groupId = ?";

	public PermissionCheckFinderEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("integer", "integer_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PermissionCheckFinderEntry.class);

		setModelImplClass(PermissionCheckFinderEntryImpl.class);
		setModelPKClass(long.class);

		setTable(PermissionCheckFinderEntryTable.INSTANCE);
	}

	/**
	 * Caches the permission check finder entry in the entity cache if it is enabled.
	 *
	 * @param permissionCheckFinderEntry the permission check finder entry
	 */
	@Override
	public void cacheResult(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		entityCache.putResult(
			PermissionCheckFinderEntryImpl.class,
			permissionCheckFinderEntry.getPrimaryKey(),
			permissionCheckFinderEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the permission check finder entries in the entity cache if it is enabled.
	 *
	 * @param permissionCheckFinderEntries the permission check finder entries
	 */
	@Override
	public void cacheResult(
		List<PermissionCheckFinderEntry> permissionCheckFinderEntries) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (permissionCheckFinderEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PermissionCheckFinderEntry permissionCheckFinderEntry :
				permissionCheckFinderEntries) {

			if (entityCache.getResult(
					PermissionCheckFinderEntryImpl.class,
					permissionCheckFinderEntry.getPrimaryKey()) == null) {

				cacheResult(permissionCheckFinderEntry);
			}
		}
	}

	/**
	 * Clears the cache for all permission check finder entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PermissionCheckFinderEntryImpl.class);

		finderCache.clearCache(PermissionCheckFinderEntryImpl.class);
	}

	/**
	 * Clears the cache for the permission check finder entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		entityCache.removeResult(
			PermissionCheckFinderEntryImpl.class, permissionCheckFinderEntry);
	}

	@Override
	public void clearCache(
		List<PermissionCheckFinderEntry> permissionCheckFinderEntries) {

		for (PermissionCheckFinderEntry permissionCheckFinderEntry :
				permissionCheckFinderEntries) {

			entityCache.removeResult(
				PermissionCheckFinderEntryImpl.class,
				permissionCheckFinderEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PermissionCheckFinderEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				PermissionCheckFinderEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new permission check finder entry with the primary key. Does not add the permission check finder entry to the database.
	 *
	 * @param permissionCheckFinderEntryId the primary key for the new permission check finder entry
	 * @return the new permission check finder entry
	 */
	@Override
	public PermissionCheckFinderEntry create(
		long permissionCheckFinderEntryId) {

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			new PermissionCheckFinderEntryImpl();

		permissionCheckFinderEntry.setNew(true);
		permissionCheckFinderEntry.setPrimaryKey(permissionCheckFinderEntryId);

		return permissionCheckFinderEntry;
	}

	/**
	 * Removes the permission check finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry that was removed
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry remove(long permissionCheckFinderEntryId)
		throws NoSuchPermissionCheckFinderEntryException {

		return remove((Serializable)permissionCheckFinderEntryId);
	}

	/**
	 * Removes the permission check finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the permission check finder entry
	 * @return the permission check finder entry that was removed
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry remove(Serializable primaryKey)
		throws NoSuchPermissionCheckFinderEntryException {

		Session session = null;

		try {
			session = openSession();

			PermissionCheckFinderEntry permissionCheckFinderEntry =
				(PermissionCheckFinderEntry)session.get(
					PermissionCheckFinderEntryImpl.class, primaryKey);

			if (permissionCheckFinderEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPermissionCheckFinderEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(permissionCheckFinderEntry);
		}
		catch (NoSuchPermissionCheckFinderEntryException
					noSuchEntityException) {

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
	protected PermissionCheckFinderEntry removeImpl(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(permissionCheckFinderEntry)) {
				permissionCheckFinderEntry =
					(PermissionCheckFinderEntry)session.get(
						PermissionCheckFinderEntryImpl.class,
						permissionCheckFinderEntry.getPrimaryKeyObj());
			}

			if (permissionCheckFinderEntry != null) {
				session.delete(permissionCheckFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (permissionCheckFinderEntry != null) {
			clearCache(permissionCheckFinderEntry);
		}

		return permissionCheckFinderEntry;
	}

	@Override
	public PermissionCheckFinderEntry updateImpl(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		boolean isNew = permissionCheckFinderEntry.isNew();

		if (!(permissionCheckFinderEntry instanceof
				PermissionCheckFinderEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(permissionCheckFinderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					permissionCheckFinderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in permissionCheckFinderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PermissionCheckFinderEntry implementation " +
					permissionCheckFinderEntry.getClass());
		}

		PermissionCheckFinderEntryModelImpl
			permissionCheckFinderEntryModelImpl =
				(PermissionCheckFinderEntryModelImpl)permissionCheckFinderEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(permissionCheckFinderEntry);
			}
			else {
				permissionCheckFinderEntry =
					(PermissionCheckFinderEntry)session.merge(
						permissionCheckFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PermissionCheckFinderEntryImpl.class,
			permissionCheckFinderEntryModelImpl, false, true);

		if (isNew) {
			permissionCheckFinderEntry.setNew(false);
		}

		permissionCheckFinderEntry.resetOriginalValues();

		return permissionCheckFinderEntry;
	}

	/**
	 * Returns the permission check finder entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the permission check finder entry
	 * @return the permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPermissionCheckFinderEntryException {

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			fetchByPrimaryKey(primaryKey);

		if (permissionCheckFinderEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPermissionCheckFinderEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return permissionCheckFinderEntry;
	}

	/**
	 * Returns the permission check finder entry with the primary key or throws a <code>NoSuchPermissionCheckFinderEntryException</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry findByPrimaryKey(
			long permissionCheckFinderEntryId)
		throws NoSuchPermissionCheckFinderEntryException {

		return findByPrimaryKey((Serializable)permissionCheckFinderEntryId);
	}

	/**
	 * Returns the permission check finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry, or <code>null</code> if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry fetchByPrimaryKey(
		long permissionCheckFinderEntryId) {

		return fetchByPrimaryKey((Serializable)permissionCheckFinderEntryId);
	}

	/**
	 * Returns all the permission check finder entries.
	 *
	 * @return the permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findAll(
		int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findAll(
		int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache) {

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

		List<PermissionCheckFinderEntry> list = null;

		if (useFinderCache) {
			list = (List<PermissionCheckFinderEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PERMISSIONCHECKFINDERENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PERMISSIONCHECKFINDERENTRY;

				sql = sql.concat(
					PermissionCheckFinderEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PermissionCheckFinderEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
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

	/**
	 * Removes all the permission check finder entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PermissionCheckFinderEntry permissionCheckFinderEntry :
				findAll()) {

			remove(permissionCheckFinderEntry);
		}
	}

	/**
	 * Returns the number of permission check finder entries.
	 *
	 * @return the number of permission check finder entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_PERMISSIONCHECKFINDERENTRY);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
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

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "permissionCheckFinderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PERMISSIONCHECKFINDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PermissionCheckFinderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the permission check finder entry persistence.
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

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		PermissionCheckFinderEntryUtil.setPersistence(this);
	}

	public void destroy() {
		PermissionCheckFinderEntryUtil.setPersistence(null);

		entityCache.removeCache(PermissionCheckFinderEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_PERMISSIONCHECKFINDERENTRY =
		"SELECT permissionCheckFinderEntry FROM PermissionCheckFinderEntry permissionCheckFinderEntry";

	private static final String _SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE =
		"SELECT permissionCheckFinderEntry FROM PermissionCheckFinderEntry permissionCheckFinderEntry WHERE ";

	private static final String _SQL_COUNT_PERMISSIONCHECKFINDERENTRY =
		"SELECT COUNT(permissionCheckFinderEntry) FROM PermissionCheckFinderEntry permissionCheckFinderEntry";

	private static final String _SQL_COUNT_PERMISSIONCHECKFINDERENTRY_WHERE =
		"SELECT COUNT(permissionCheckFinderEntry) FROM PermissionCheckFinderEntry permissionCheckFinderEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"permissionCheckFinderEntry.permissionCheckFinderEntryId";

	private static final String
		_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE =
			"SELECT DISTINCT {permissionCheckFinderEntry.*} FROM PermissionCheckFinderEntry permissionCheckFinderEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {PermissionCheckFinderEntry.*} FROM (SELECT DISTINCT permissionCheckFinderEntry.permissionCheckFinderEntryId FROM PermissionCheckFinderEntry permissionCheckFinderEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN PermissionCheckFinderEntry ON TEMP_TABLE.permissionCheckFinderEntryId = PermissionCheckFinderEntry.permissionCheckFinderEntryId";

	private static final String
		_FILTER_SQL_COUNT_PERMISSIONCHECKFINDERENTRY_WHERE =
			"SELECT COUNT(DISTINCT permissionCheckFinderEntry.permissionCheckFinderEntryId) AS COUNT_VALUE FROM PermissionCheckFinderEntry permissionCheckFinderEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"permissionCheckFinderEntry";

	private static final String _FILTER_ENTITY_TABLE =
		"PermissionCheckFinderEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"permissionCheckFinderEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"PermissionCheckFinderEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PermissionCheckFinderEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PermissionCheckFinderEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PermissionCheckFinderEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"integer", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}