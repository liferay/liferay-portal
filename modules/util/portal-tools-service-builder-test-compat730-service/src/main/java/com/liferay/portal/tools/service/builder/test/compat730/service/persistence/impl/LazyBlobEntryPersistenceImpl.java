/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat730.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchLazyBlobEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.LazyBlobEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.LazyBlobEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.LazyBlobEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.LazyBlobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.LazyBlobEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * The persistence implementation for the lazy blob entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LazyBlobEntryPersistenceImpl
	extends BasePersistenceImpl<LazyBlobEntry>
	implements LazyBlobEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LazyBlobEntryUtil</code> to access the lazy blob entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LazyBlobEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the lazy blob entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the lazy blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @return the range of matching lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the lazy blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LazyBlobEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the lazy blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LazyBlobEntry> orderByComparator,
		boolean useFinderCache) {

		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByUuid;
				finderArgs = new Object[] {uuid};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByUuid;
			finderArgs = new Object[] {uuid, start, end, orderByComparator};
		}

		List<LazyBlobEntry> list = null;

		if (useFinderCache) {
			list = (List<LazyBlobEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (LazyBlobEntry lazyBlobEntry : list) {
					if (!uuid.equals(lazyBlobEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_LAZYBLOBENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(LazyBlobEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

				list = (List<LazyBlobEntry>)QueryUtil.list(
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
	 * Returns the first lazy blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry findByUuid_First(
			String uuid, OrderByComparator<LazyBlobEntry> orderByComparator)
		throws NoSuchLazyBlobEntryException {

		LazyBlobEntry lazyBlobEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (lazyBlobEntry != null) {
			return lazyBlobEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLazyBlobEntryException(sb.toString());
	}

	/**
	 * Returns the first lazy blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lazy blob entry, or <code>null</code> if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry fetchByUuid_First(
		String uuid, OrderByComparator<LazyBlobEntry> orderByComparator) {

		List<LazyBlobEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last lazy blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry findByUuid_Last(
			String uuid, OrderByComparator<LazyBlobEntry> orderByComparator)
		throws NoSuchLazyBlobEntryException {

		LazyBlobEntry lazyBlobEntry = fetchByUuid_Last(uuid, orderByComparator);

		if (lazyBlobEntry != null) {
			return lazyBlobEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLazyBlobEntryException(sb.toString());
	}

	/**
	 * Returns the last lazy blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching lazy blob entry, or <code>null</code> if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry fetchByUuid_Last(
		String uuid, OrderByComparator<LazyBlobEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<LazyBlobEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the lazy blob entries before and after the current lazy blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param lazyBlobEntryId the primary key of the current lazy blob entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry[] findByUuid_PrevAndNext(
			long lazyBlobEntryId, String uuid,
			OrderByComparator<LazyBlobEntry> orderByComparator)
		throws NoSuchLazyBlobEntryException {

		uuid = Objects.toString(uuid, "");

		LazyBlobEntry lazyBlobEntry = findByPrimaryKey(lazyBlobEntryId);

		Session session = null;

		try {
			session = openSession();

			LazyBlobEntry[] array = new LazyBlobEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, lazyBlobEntry, uuid, orderByComparator, true);

			array[1] = lazyBlobEntry;

			array[2] = getByUuid_PrevAndNext(
				session, lazyBlobEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LazyBlobEntry getByUuid_PrevAndNext(
		Session session, LazyBlobEntry lazyBlobEntry, String uuid,
		OrderByComparator<LazyBlobEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAZYBLOBENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2);
		}

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
			sb.append(LazyBlobEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						lazyBlobEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LazyBlobEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the lazy blob entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (LazyBlobEntry lazyBlobEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(lazyBlobEntry);
		}
	}

	/**
	 * Returns the number of lazy blob entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching lazy blob entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_LAZYBLOBENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"lazyBlobEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(lazyBlobEntry.uuid IS NULL OR lazyBlobEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;
	private FinderPath _finderPathCountByUUID_G;

	/**
	 * Returns the lazy blob entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLazyBlobEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchLazyBlobEntryException {

		LazyBlobEntry lazyBlobEntry = fetchByUUID_G(uuid, groupId);

		if (lazyBlobEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLazyBlobEntryException(sb.toString());
		}

		return lazyBlobEntry;
	}

	/**
	 * Returns the lazy blob entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching lazy blob entry, or <code>null</code> if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the lazy blob entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lazy blob entry, or <code>null</code> if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		uuid = Objects.toString(uuid, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {uuid, groupId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByUUID_G, finderArgs, this);
		}

		if (result instanceof LazyBlobEntry) {
			LazyBlobEntry lazyBlobEntry = (LazyBlobEntry)result;

			if (!Objects.equals(uuid, lazyBlobEntry.getUuid()) ||
				(groupId != lazyBlobEntry.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_LAZYBLOBENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
			}

			sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

				queryPos.add(groupId);

				List<LazyBlobEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					LazyBlobEntry lazyBlobEntry = list.get(0);

					result = lazyBlobEntry;

					cacheResult(lazyBlobEntry);
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
			return (LazyBlobEntry)result;
		}
	}

	/**
	 * Removes the lazy blob entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the lazy blob entry that was removed
	 */
	@Override
	public LazyBlobEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchLazyBlobEntryException {

		LazyBlobEntry lazyBlobEntry = findByUUID_G(uuid, groupId);

		return remove(lazyBlobEntry);
	}

	/**
	 * Returns the number of lazy blob entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching lazy blob entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUUID_G;

		Object[] finderArgs = new Object[] {uuid, groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_LAZYBLOBENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
			}

			sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

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

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"lazyBlobEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(lazyBlobEntry.uuid IS NULL OR lazyBlobEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"lazyBlobEntry.groupId = ?";

	public LazyBlobEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LazyBlobEntry.class);

		setModelImplClass(LazyBlobEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the lazy blob entry in the entity cache if it is enabled.
	 *
	 * @param lazyBlobEntry the lazy blob entry
	 */
	@Override
	public void cacheResult(LazyBlobEntry lazyBlobEntry) {
		entityCache.putResult(
			LazyBlobEntryImpl.class, lazyBlobEntry.getPrimaryKey(),
			lazyBlobEntry);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {lazyBlobEntry.getUuid(), lazyBlobEntry.getGroupId()},
			lazyBlobEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the lazy blob entries in the entity cache if it is enabled.
	 *
	 * @param lazyBlobEntries the lazy blob entries
	 */
	@Override
	public void cacheResult(List<LazyBlobEntry> lazyBlobEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (lazyBlobEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LazyBlobEntry lazyBlobEntry : lazyBlobEntries) {
			if (entityCache.getResult(
					LazyBlobEntryImpl.class, lazyBlobEntry.getPrimaryKey()) ==
						null) {

				cacheResult(lazyBlobEntry);
			}
		}
	}

	/**
	 * Clears the cache for all lazy blob entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LazyBlobEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the lazy blob entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(LazyBlobEntry lazyBlobEntry) {
		entityCache.removeResult(LazyBlobEntryImpl.class, lazyBlobEntry);
	}

	@Override
	public void clearCache(List<LazyBlobEntry> lazyBlobEntries) {
		for (LazyBlobEntry lazyBlobEntry : lazyBlobEntries) {
			entityCache.removeResult(LazyBlobEntryImpl.class, lazyBlobEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(LazyBlobEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		LazyBlobEntryModelImpl lazyBlobEntryModelImpl) {

		Object[] args = new Object[] {
			lazyBlobEntryModelImpl.getUuid(),
			lazyBlobEntryModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathCountByUUID_G, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByUUID_G, args, lazyBlobEntryModelImpl, false);
	}

	/**
	 * Creates a new lazy blob entry with the primary key. Does not add the lazy blob entry to the database.
	 *
	 * @param lazyBlobEntryId the primary key for the new lazy blob entry
	 * @return the new lazy blob entry
	 */
	@Override
	public LazyBlobEntry create(long lazyBlobEntryId) {
		LazyBlobEntry lazyBlobEntry = new LazyBlobEntryImpl();

		lazyBlobEntry.setNew(true);
		lazyBlobEntry.setPrimaryKey(lazyBlobEntryId);

		String uuid = PortalUUIDUtil.generate();

		lazyBlobEntry.setUuid(uuid);

		return lazyBlobEntry;
	}

	/**
	 * Removes the lazy blob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry that was removed
	 * @throws NoSuchLazyBlobEntryException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry remove(long lazyBlobEntryId)
		throws NoSuchLazyBlobEntryException {

		return remove((Serializable)lazyBlobEntryId);
	}

	/**
	 * Removes the lazy blob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the lazy blob entry
	 * @return the lazy blob entry that was removed
	 * @throws NoSuchLazyBlobEntryException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry remove(Serializable primaryKey)
		throws NoSuchLazyBlobEntryException {

		Session session = null;

		try {
			session = openSession();

			LazyBlobEntry lazyBlobEntry = (LazyBlobEntry)session.get(
				LazyBlobEntryImpl.class, primaryKey);

			if (lazyBlobEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchLazyBlobEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(lazyBlobEntry);
		}
		catch (NoSuchLazyBlobEntryException noSuchEntityException) {
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
	protected LazyBlobEntry removeImpl(LazyBlobEntry lazyBlobEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(lazyBlobEntry)) {
				lazyBlobEntry = (LazyBlobEntry)session.get(
					LazyBlobEntryImpl.class, lazyBlobEntry.getPrimaryKeyObj());
			}

			if (lazyBlobEntry != null) {
				session.delete(lazyBlobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (lazyBlobEntry != null) {
			clearCache(lazyBlobEntry);
		}

		return lazyBlobEntry;
	}

	@Override
	public LazyBlobEntry updateImpl(LazyBlobEntry lazyBlobEntry) {
		boolean isNew = lazyBlobEntry.isNew();

		if (!(lazyBlobEntry instanceof LazyBlobEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(lazyBlobEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					lazyBlobEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in lazyBlobEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LazyBlobEntry implementation " +
					lazyBlobEntry.getClass());
		}

		LazyBlobEntryModelImpl lazyBlobEntryModelImpl =
			(LazyBlobEntryModelImpl)lazyBlobEntry;

		if (Validator.isNull(lazyBlobEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			lazyBlobEntry.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(lazyBlobEntry);
			}
			else {
				session.evict(
					LazyBlobEntryImpl.class, lazyBlobEntry.getPrimaryKeyObj());

				session.saveOrUpdate(lazyBlobEntry);
			}

			session.flush();
			session.clear();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			LazyBlobEntryImpl.class, lazyBlobEntryModelImpl, false, true);

		cacheUniqueFindersCache(lazyBlobEntryModelImpl);

		if (isNew) {
			lazyBlobEntry.setNew(false);
		}

		lazyBlobEntry.resetOriginalValues();

		return lazyBlobEntry;
	}

	/**
	 * Returns the lazy blob entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the lazy blob entry
	 * @return the lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchLazyBlobEntryException {

		LazyBlobEntry lazyBlobEntry = fetchByPrimaryKey(primaryKey);

		if (lazyBlobEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchLazyBlobEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return lazyBlobEntry;
	}

	/**
	 * Returns the lazy blob entry with the primary key or throws a <code>NoSuchLazyBlobEntryException</code> if it could not be found.
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry findByPrimaryKey(long lazyBlobEntryId)
		throws NoSuchLazyBlobEntryException {

		return findByPrimaryKey((Serializable)lazyBlobEntryId);
	}

	/**
	 * Returns the lazy blob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry, or <code>null</code> if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry fetchByPrimaryKey(long lazyBlobEntryId) {
		return fetchByPrimaryKey((Serializable)lazyBlobEntryId);
	}

	/**
	 * Returns all the lazy blob entries.
	 *
	 * @return the lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the lazy blob entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @return the range of lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the lazy blob entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findAll(
		int start, int end,
		OrderByComparator<LazyBlobEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the lazy blob entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findAll(
		int start, int end, OrderByComparator<LazyBlobEntry> orderByComparator,
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

		List<LazyBlobEntry> list = null;

		if (useFinderCache) {
			list = (List<LazyBlobEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_LAZYBLOBENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_LAZYBLOBENTRY;

				sql = sql.concat(LazyBlobEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<LazyBlobEntry>)QueryUtil.list(
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
	 * Removes all the lazy blob entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LazyBlobEntry lazyBlobEntry : findAll()) {
			remove(lazyBlobEntry);
		}
	}

	/**
	 * Returns the number of lazy blob entries.
	 *
	 * @return the number of lazy blob entries
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
					"SELECT COUNT(lazyBlobEntry) FROM LazyBlobEntry lazyBlobEntry");

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
		return "lazyBlobEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAZYBLOBENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LazyBlobEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the lazy blob entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			LazyBlobEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class, new LazyBlobEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", LazyBlobEntry.class.getName()));

		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByUuid = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathFetchByUUID_G = _createFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

		_finderPathCountByUUID_G = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, false);

		LazyBlobEntryUtil.setPersistence(this);
	}

	public void destroy() {
		LazyBlobEntryUtil.setPersistence(null);

		entityCache.removeCache(LazyBlobEntryImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		LazyBlobEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAZYBLOBENTRY =
		"SELECT lazyBlobEntry FROM LazyBlobEntry lazyBlobEntry";

	private static final String _SQL_SELECT_LAZYBLOBENTRY_WHERE =
		"SELECT lazyBlobEntry FROM LazyBlobEntry lazyBlobEntry WHERE ";

	private static final String _SQL_COUNT_LAZYBLOBENTRY_WHERE =
		"SELECT COUNT(lazyBlobEntry) FROM LazyBlobEntry lazyBlobEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LazyBlobEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LazyBlobEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LazyBlobEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					MapUtil.singletonDictionary("cache.name", cacheName)));
		}

		return finderPath;
	}

	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();
	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;

	private static class LazyBlobEntryModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return new Object[0];
				}

				return null;
			}

			LazyBlobEntryModelImpl lazyBlobEntryModelImpl =
				(LazyBlobEntryModelImpl)baseModel;

			long columnBitmask = lazyBlobEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(lazyBlobEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						lazyBlobEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(lazyBlobEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			LazyBlobEntryModelImpl lazyBlobEntryModelImpl, String[] columnNames,
			boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						lazyBlobEntryModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = lazyBlobEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:251306839