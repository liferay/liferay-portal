/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat720.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat720.exception.NoSuchVersionedEntryException;
import com.liferay.portal.tools.service.builder.test.compat720.model.VersionedEntry;
import com.liferay.portal.tools.service.builder.test.compat720.model.impl.VersionedEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat720.model.impl.VersionedEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat720.service.persistence.VersionedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat720.service.persistence.VersionedEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the versioned entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class VersionedEntryPersistenceImpl
	extends BasePersistenceImpl<VersionedEntry>
	implements VersionedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>VersionedEntryUtil</code> to access the versioned entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		VersionedEntryImpl.class.getName();

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
	 * Returns all the versioned entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the versioned entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @return the range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the versioned entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<VersionedEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the versioned entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<VersionedEntry> orderByComparator,
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

		List<VersionedEntry> list = null;

		if (useFinderCache) {
			list = (List<VersionedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (VersionedEntry versionedEntry : list) {
					if (groupId != versionedEntry.getGroupId()) {
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

			sb.append(_SQL_SELECT_VERSIONEDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(VersionedEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<VersionedEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByGroupId_First(
			long groupId, OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (versionedEntry != null) {
			return versionedEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByGroupId_First(
		long groupId, OrderByComparator<VersionedEntry> orderByComparator) {

		List<VersionedEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last versioned entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByGroupId_Last(
			long groupId, OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (versionedEntry != null) {
			return versionedEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the last versioned entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<VersionedEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<VersionedEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the versioned entries before and after the current versioned entry in the ordered set where groupId = &#63;.
	 *
	 * @param versionedEntryId the primary key of the current versioned entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next versioned entry
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry[] findByGroupId_PrevAndNext(
			long versionedEntryId, long groupId,
			OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = findByPrimaryKey(versionedEntryId);

		Session session = null;

		try {
			session = openSession();

			VersionedEntry[] array = new VersionedEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, versionedEntry, groupId, orderByComparator, true);

			array[1] = versionedEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, versionedEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected VersionedEntry getByGroupId_PrevAndNext(
		Session session, VersionedEntry versionedEntry, long groupId,
		OrderByComparator<VersionedEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_VERSIONEDENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ENTITY_ALIAS_PREFIX);
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
				sb.append(_ENTITY_ALIAS_PREFIX);
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
			sb.append(VersionedEntryModelImpl.ORDER_BY_JPQL);
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
						versionedEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<VersionedEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the versioned entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (VersionedEntry versionedEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(versionedEntry);
		}
	}

	/**
	 * Returns the number of versioned entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching versioned entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_VERSIONEDENTRY_WHERE);

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
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"versionedEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId_Head;
	private FinderPath _finderPathWithoutPaginationFindByGroupId_Head;
	private FinderPath _finderPathCountByGroupId_Head;

	/**
	 * Returns all the versioned entries where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId_Head(long groupId, boolean head) {
		return findByGroupId_Head(
			groupId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the versioned entries where groupId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @return the range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId_Head(
		long groupId, boolean head, int start, int end) {

		return findByGroupId_Head(groupId, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the versioned entries where groupId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId_Head(
		long groupId, boolean head, int start, int end,
		OrderByComparator<VersionedEntry> orderByComparator) {

		return findByGroupId_Head(
			groupId, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the versioned entries where groupId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId_Head(
		long groupId, boolean head, int start, int end,
		OrderByComparator<VersionedEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByGroupId_Head;
				finderArgs = new Object[] {groupId, head};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByGroupId_Head;
			finderArgs = new Object[] {
				groupId, head, start, end, orderByComparator
			};
		}

		List<VersionedEntry> list = null;

		if (useFinderCache) {
			list = (List<VersionedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (VersionedEntry versionedEntry : list) {
					if ((groupId != versionedEntry.getGroupId()) ||
						(head != versionedEntry.isHead())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_VERSIONEDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_GROUPID_2);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_HEAD_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(VersionedEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(head);

				list = (List<VersionedEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByGroupId_Head_First(
			long groupId, boolean head,
			OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = fetchByGroupId_Head_First(
			groupId, head, orderByComparator);

		if (versionedEntry != null) {
			return versionedEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByGroupId_Head_First(
		long groupId, boolean head,
		OrderByComparator<VersionedEntry> orderByComparator) {

		List<VersionedEntry> list = findByGroupId_Head(
			groupId, head, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last versioned entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByGroupId_Head_Last(
			long groupId, boolean head,
			OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = fetchByGroupId_Head_Last(
			groupId, head, orderByComparator);

		if (versionedEntry != null) {
			return versionedEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the last versioned entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByGroupId_Head_Last(
		long groupId, boolean head,
		OrderByComparator<VersionedEntry> orderByComparator) {

		int count = countByGroupId_Head(groupId, head);

		if (count == 0) {
			return null;
		}

		List<VersionedEntry> list = findByGroupId_Head(
			groupId, head, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the versioned entries before and after the current versioned entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param versionedEntryId the primary key of the current versioned entry
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next versioned entry
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry[] findByGroupId_Head_PrevAndNext(
			long versionedEntryId, long groupId, boolean head,
			OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = findByPrimaryKey(versionedEntryId);

		Session session = null;

		try {
			session = openSession();

			VersionedEntry[] array = new VersionedEntryImpl[3];

			array[0] = getByGroupId_Head_PrevAndNext(
				session, versionedEntry, groupId, head, orderByComparator,
				true);

			array[1] = versionedEntry;

			array[2] = getByGroupId_Head_PrevAndNext(
				session, versionedEntry, groupId, head, orderByComparator,
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

	protected VersionedEntry getByGroupId_Head_PrevAndNext(
		Session session, VersionedEntry versionedEntry, long groupId,
		boolean head, OrderByComparator<VersionedEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_VERSIONEDENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_HEAD_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPID_HEAD_HEAD_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ENTITY_ALIAS_PREFIX);
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
				sb.append(_ENTITY_ALIAS_PREFIX);
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
			sb.append(VersionedEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						versionedEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<VersionedEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the versioned entries where groupId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 */
	@Override
	public void removeByGroupId_Head(long groupId, boolean head) {
		for (VersionedEntry versionedEntry :
				findByGroupId_Head(
					groupId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(versionedEntry);
		}
	}

	/**
	 * Returns the number of versioned entries where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching versioned entries
	 */
	@Override
	public int countByGroupId_Head(long groupId, boolean head) {
		FinderPath finderPath = _finderPathCountByGroupId_Head;

		Object[] finderArgs = new Object[] {groupId, head};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_VERSIONEDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_GROUPID_2);

			sb.append(_FINDER_COLUMN_GROUPID_HEAD_HEAD_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(head);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_GROUPID_HEAD_GROUPID_2 =
		"versionedEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_GROUPID_HEAD_HEAD_2 =
		"versionedEntry.head = ?";

	private FinderPath _finderPathFetchByHeadId;
	private FinderPath _finderPathCountByHeadId;

	/**
	 * Returns the versioned entry where headId = &#63; or throws a <code>NoSuchVersionedEntryException</code> if it could not be found.
	 *
	 * @param headId the head ID
	 * @return the matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByHeadId(long headId)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = fetchByHeadId(headId);

		if (versionedEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("headId=");
			sb.append(headId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchVersionedEntryException(sb.toString());
		}

		return versionedEntry;
	}

	/**
	 * Returns the versioned entry where headId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param headId the head ID
	 * @return the matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByHeadId(long headId) {
		return fetchByHeadId(headId, true);
	}

	/**
	 * Returns the versioned entry where headId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headId the head ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByHeadId(long headId, boolean useFinderCache) {
		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {headId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByHeadId, finderArgs, this);
		}

		if (result instanceof VersionedEntry) {
			VersionedEntry versionedEntry = (VersionedEntry)result;

			if (headId != versionedEntry.getHeadId()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_VERSIONEDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_HEADID_HEADID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(headId);

				List<VersionedEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByHeadId, finderArgs, list);
					}
				}
				else {
					VersionedEntry versionedEntry = list.get(0);

					result = versionedEntry;

					cacheResult(versionedEntry);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(
						_finderPathFetchByHeadId, finderArgs);
				}

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
			return (VersionedEntry)result;
		}
	}

	/**
	 * Removes the versioned entry where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 * @return the versioned entry that was removed
	 */
	@Override
	public VersionedEntry removeByHeadId(long headId)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = findByHeadId(headId);

		return remove(versionedEntry);
	}

	/**
	 * Returns the number of versioned entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching versioned entries
	 */
	@Override
	public int countByHeadId(long headId) {
		FinderPath finderPath = _finderPathCountByHeadId;

		Object[] finderArgs = new Object[] {headId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_VERSIONEDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_HEADID_HEADID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(headId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_HEADID_HEADID_2 =
		"versionedEntry.headId = ?";

	public VersionedEntryPersistenceImpl() {
		setModelClass(VersionedEntry.class);

		setModelImplClass(VersionedEntryImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(VersionedEntryModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the versioned entry in the entity cache if it is enabled.
	 *
	 * @param versionedEntry the versioned entry
	 */
	@Override
	public void cacheResult(VersionedEntry versionedEntry) {
		entityCache.putResult(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryImpl.class, versionedEntry.getPrimaryKey(),
			versionedEntry);

		finderCache.putResult(
			_finderPathFetchByHeadId, new Object[] {versionedEntry.getHeadId()},
			versionedEntry);

		versionedEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the versioned entries in the entity cache if it is enabled.
	 *
	 * @param versionedEntries the versioned entries
	 */
	@Override
	public void cacheResult(List<VersionedEntry> versionedEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (versionedEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (VersionedEntry versionedEntry : versionedEntries) {
			if (entityCache.getResult(
					VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
					VersionedEntryImpl.class, versionedEntry.getPrimaryKey()) ==
						null) {

				cacheResult(versionedEntry);
			}
			else {
				versionedEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all versioned entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(VersionedEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the versioned entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(VersionedEntry versionedEntry) {
		entityCache.removeResult(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryImpl.class, versionedEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((VersionedEntryModelImpl)versionedEntry, true);
	}

	@Override
	public void clearCache(List<VersionedEntry> versionedEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (VersionedEntry versionedEntry : versionedEntries) {
			entityCache.removeResult(
				VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
				VersionedEntryImpl.class, versionedEntry.getPrimaryKey());

			clearUniqueFindersCache(
				(VersionedEntryModelImpl)versionedEntry, true);
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
				VersionedEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		VersionedEntryModelImpl versionedEntryModelImpl) {

		Object[] args = new Object[] {versionedEntryModelImpl.getHeadId()};

		finderCache.putResult(
			_finderPathCountByHeadId, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByHeadId, args, versionedEntryModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		VersionedEntryModelImpl versionedEntryModelImpl, boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {versionedEntryModelImpl.getHeadId()};

			finderCache.removeResult(_finderPathCountByHeadId, args);
			finderCache.removeResult(_finderPathFetchByHeadId, args);
		}

		if ((versionedEntryModelImpl.getColumnBitmask() &
			 _finderPathFetchByHeadId.getColumnBitmask()) != 0) {

			Object[] args = new Object[] {
				versionedEntryModelImpl.getOriginalHeadId()
			};

			finderCache.removeResult(_finderPathCountByHeadId, args);
			finderCache.removeResult(_finderPathFetchByHeadId, args);
		}
	}

	/**
	 * Creates a new versioned entry with the primary key. Does not add the versioned entry to the database.
	 *
	 * @param versionedEntryId the primary key for the new versioned entry
	 * @return the new versioned entry
	 */
	@Override
	public VersionedEntry create(long versionedEntryId) {
		VersionedEntry versionedEntry = new VersionedEntryImpl();

		versionedEntry.setNew(true);
		versionedEntry.setPrimaryKey(versionedEntryId);

		return versionedEntry;
	}

	/**
	 * Removes the versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param versionedEntryId the primary key of the versioned entry
	 * @return the versioned entry that was removed
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry remove(long versionedEntryId)
		throws NoSuchVersionedEntryException {

		return remove((Serializable)versionedEntryId);
	}

	/**
	 * Removes the versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the versioned entry
	 * @return the versioned entry that was removed
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry remove(Serializable primaryKey)
		throws NoSuchVersionedEntryException {

		Session session = null;

		try {
			session = openSession();

			VersionedEntry versionedEntry = (VersionedEntry)session.get(
				VersionedEntryImpl.class, primaryKey);

			if (versionedEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchVersionedEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(versionedEntry);
		}
		catch (NoSuchVersionedEntryException noSuchEntityException) {
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
	protected VersionedEntry removeImpl(VersionedEntry versionedEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(versionedEntry)) {
				versionedEntry = (VersionedEntry)session.get(
					VersionedEntryImpl.class,
					versionedEntry.getPrimaryKeyObj());
			}

			if (versionedEntry != null) {
				session.delete(versionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (versionedEntry != null) {
			clearCache(versionedEntry);
		}

		return versionedEntry;
	}

	@Override
	public VersionedEntry updateImpl(VersionedEntry versionedEntry) {
		boolean isNew = versionedEntry.isNew();

		if (!(versionedEntry instanceof VersionedEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(versionedEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					versionedEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in versionedEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom VersionedEntry implementation " +
					versionedEntry.getClass());
		}

		VersionedEntryModelImpl versionedEntryModelImpl =
			(VersionedEntryModelImpl)versionedEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(versionedEntry);

				versionedEntry.setNew(false);
			}
			else {
				versionedEntry = (VersionedEntry)session.merge(versionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!VersionedEntryModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			Object[] args = new Object[] {versionedEntryModelImpl.getGroupId()};

			finderCache.removeResult(_finderPathCountByGroupId, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByGroupId, args);

			args = new Object[] {
				versionedEntryModelImpl.getGroupId(),
				versionedEntryModelImpl.isHead()
			};

			finderCache.removeResult(_finderPathCountByGroupId_Head, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByGroupId_Head, args);

			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}
		else {
			if ((versionedEntryModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByGroupId.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					versionedEntryModelImpl.getOriginalGroupId()
				};

				finderCache.removeResult(_finderPathCountByGroupId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId, args);

				args = new Object[] {versionedEntryModelImpl.getGroupId()};

				finderCache.removeResult(_finderPathCountByGroupId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId, args);
			}

			if ((versionedEntryModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByGroupId_Head.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					versionedEntryModelImpl.getOriginalGroupId(),
					versionedEntryModelImpl.getOriginalHead()
				};

				finderCache.removeResult(_finderPathCountByGroupId_Head, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId_Head, args);

				args = new Object[] {
					versionedEntryModelImpl.getGroupId(),
					versionedEntryModelImpl.isHead()
				};

				finderCache.removeResult(_finderPathCountByGroupId_Head, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId_Head, args);
			}
		}

		entityCache.putResult(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryImpl.class, versionedEntry.getPrimaryKey(),
			versionedEntry, false);

		clearUniqueFindersCache(versionedEntryModelImpl, false);
		cacheUniqueFindersCache(versionedEntryModelImpl);

		versionedEntry.resetOriginalValues();

		return versionedEntry;
	}

	/**
	 * Returns the versioned entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the versioned entry
	 * @return the versioned entry
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = fetchByPrimaryKey(primaryKey);

		if (versionedEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchVersionedEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return versionedEntry;
	}

	/**
	 * Returns the versioned entry with the primary key or throws a <code>NoSuchVersionedEntryException</code> if it could not be found.
	 *
	 * @param versionedEntryId the primary key of the versioned entry
	 * @return the versioned entry
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry findByPrimaryKey(long versionedEntryId)
		throws NoSuchVersionedEntryException {

		return findByPrimaryKey((Serializable)versionedEntryId);
	}

	/**
	 * Returns the versioned entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param versionedEntryId the primary key of the versioned entry
	 * @return the versioned entry, or <code>null</code> if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry fetchByPrimaryKey(long versionedEntryId) {
		return fetchByPrimaryKey((Serializable)versionedEntryId);
	}

	/**
	 * Returns all the versioned entries.
	 *
	 * @return the versioned entries
	 */
	@Override
	public List<VersionedEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @return the range of versioned entries
	 */
	@Override
	public List<VersionedEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of versioned entries
	 */
	@Override
	public List<VersionedEntry> findAll(
		int start, int end,
		OrderByComparator<VersionedEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of versioned entries
	 */
	@Override
	public List<VersionedEntry> findAll(
		int start, int end, OrderByComparator<VersionedEntry> orderByComparator,
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

		List<VersionedEntry> list = null;

		if (useFinderCache) {
			list = (List<VersionedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_VERSIONEDENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_VERSIONEDENTRY;

				sql = sql.concat(VersionedEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<VersionedEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the versioned entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (VersionedEntry versionedEntry : findAll()) {
			remove(versionedEntry);
		}
	}

	/**
	 * Returns the number of versioned entries.
	 *
	 * @return the number of versioned entries
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
					"SELECT COUNT(versionedEntry) FROM VersionedEntry versionedEntry");

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "versionedEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_VERSIONEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return VersionedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the versioned entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findAll", new String[0]);

		_finderPathCountAll = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByGroupId", new String[] {Long.class.getName()},
			VersionedEntryModelImpl.GROUPID_COLUMN_BITMASK);

		_finderPathCountByGroupId = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()});

		_finderPathWithPaginationFindByGroupId_Head = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByGroupId_Head",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByGroupId_Head = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByGroupId_Head",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			VersionedEntryModelImpl.GROUPID_COLUMN_BITMASK |
			VersionedEntryModelImpl.HEAD_COLUMN_BITMASK);

		_finderPathCountByGroupId_Head = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId_Head",
			new String[] {Long.class.getName(), Boolean.class.getName()});

		_finderPathFetchByHeadId = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryImpl.class, FINDER_CLASS_NAME_ENTITY, "fetchByHeadId",
			new String[] {Long.class.getName()},
			VersionedEntryModelImpl.HEADID_COLUMN_BITMASK);

		_finderPathCountByHeadId = new FinderPath(
			VersionedEntryModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByHeadId",
			new String[] {Long.class.getName()});

		VersionedEntryUtil.setPersistence(this);
	}

	public void destroy() {
		VersionedEntryUtil.setPersistence(null);

		entityCache.removeCache(VersionedEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		VersionedEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_VERSIONEDENTRY =
		"SELECT versionedEntry FROM VersionedEntry versionedEntry";

	private static final String _SQL_SELECT_VERSIONEDENTRY_WHERE =
		"SELECT versionedEntry FROM VersionedEntry versionedEntry WHERE ";

	private static final String _SQL_COUNT_VERSIONEDENTRY_WHERE =
		"SELECT COUNT(versionedEntry) FROM VersionedEntry versionedEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No VersionedEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No VersionedEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		VersionedEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:131809734