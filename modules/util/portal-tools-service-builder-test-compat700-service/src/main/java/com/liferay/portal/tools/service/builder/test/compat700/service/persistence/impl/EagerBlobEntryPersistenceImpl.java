/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat700.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat700.exception.NoSuchEagerBlobEntryException;
import com.liferay.portal.tools.service.builder.test.compat700.model.EagerBlobEntry;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.EagerBlobEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.EagerBlobEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.EagerBlobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.EagerBlobEntryUtil;

import java.io.Serializable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the eager blob entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class EagerBlobEntryPersistenceImpl
	extends BasePersistenceImpl<EagerBlobEntry>
	implements EagerBlobEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>EagerBlobEntryUtil</code> to access the eager blob entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		EagerBlobEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private static Object _split(Object array, int splitSize) {
		int length = Array.getLength(array);

		int pageCount = length / splitSize;

		if ((length % splitSize) > 0) {
			pageCount++;
		}

		Class<?> clazz = array.getClass();

		Class<?> componentType = clazz.getComponentType();

		Object newArray = Array.newInstance(
			componentType, pageCount, splitSize);

		if (pageCount == 1) {
			Array.set(newArray, 0, array);

			return newArray;
		}

		for (int i = 0; i < pageCount; i++) {
			int end = Math.min(length, splitSize * (i + 1));
			int start = splitSize * i;

			int elementLength = end - start;

			Object element = Array.newInstance(componentType, elementLength);

			System.arraycopy(array, start, element, 0, elementLength);

			Array.set(newArray, i, element);
		}

		return newArray;
	}

	private int _databaseInMaxParameters;
	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the eager blob entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the eager blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of eager blob entries
	 * @param end the upper bound of the range of eager blob entries (not inclusive)
	 * @return the range of matching eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the eager blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of eager blob entries
	 * @param end the upper bound of the range of eager blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<EagerBlobEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the eager blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of eager blob entries
	 * @param end the upper bound of the range of eager blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<EagerBlobEntry> orderByComparator,
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

		List<EagerBlobEntry> list = null;

		if (useFinderCache) {
			list = (List<EagerBlobEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (EagerBlobEntry eagerBlobEntry : list) {
					if (!uuid.equals(eagerBlobEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_EAGERBLOBENTRY_WHERE);

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
				sb.append(EagerBlobEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<EagerBlobEntry>)QueryUtil.list(
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
	 * Returns the first eager blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry findByUuid_First(
			String uuid, OrderByComparator<EagerBlobEntry> orderByComparator)
		throws NoSuchEagerBlobEntryException {

		EagerBlobEntry eagerBlobEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (eagerBlobEntry != null) {
			return eagerBlobEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEagerBlobEntryException(sb.toString());
	}

	/**
	 * Returns the first eager blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager blob entry, or <code>null</code> if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry fetchByUuid_First(
		String uuid, OrderByComparator<EagerBlobEntry> orderByComparator) {

		List<EagerBlobEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last eager blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry findByUuid_Last(
			String uuid, OrderByComparator<EagerBlobEntry> orderByComparator)
		throws NoSuchEagerBlobEntryException {

		EagerBlobEntry eagerBlobEntry = fetchByUuid_Last(
			uuid, orderByComparator);

		if (eagerBlobEntry != null) {
			return eagerBlobEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEagerBlobEntryException(sb.toString());
	}

	/**
	 * Returns the last eager blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching eager blob entry, or <code>null</code> if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry fetchByUuid_Last(
		String uuid, OrderByComparator<EagerBlobEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<EagerBlobEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the eager blob entries before and after the current eager blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param eagerBlobEntryId the primary key of the current eager blob entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry[] findByUuid_PrevAndNext(
			long eagerBlobEntryId, String uuid,
			OrderByComparator<EagerBlobEntry> orderByComparator)
		throws NoSuchEagerBlobEntryException {

		uuid = Objects.toString(uuid, "");

		EagerBlobEntry eagerBlobEntry = findByPrimaryKey(eagerBlobEntryId);

		Session session = null;

		try {
			session = openSession();

			EagerBlobEntry[] array = new EagerBlobEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, eagerBlobEntry, uuid, orderByComparator, true);

			array[1] = eagerBlobEntry;

			array[2] = getByUuid_PrevAndNext(
				session, eagerBlobEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected EagerBlobEntry getByUuid_PrevAndNext(
		Session session, EagerBlobEntry eagerBlobEntry, String uuid,
		OrderByComparator<EagerBlobEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_EAGERBLOBENTRY_WHERE);

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
			sb.append(EagerBlobEntryModelImpl.ORDER_BY_JPQL);
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
						eagerBlobEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<EagerBlobEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the eager blob entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (EagerBlobEntry eagerBlobEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(eagerBlobEntry);
		}
	}

	/**
	 * Returns the number of eager blob entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching eager blob entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_EAGERBLOBENTRY_WHERE);

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
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"eagerBlobEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(eagerBlobEntry.uuid IS NULL OR eagerBlobEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;
	private FinderPath _finderPathCountByUUID_G;

	/**
	 * Returns the eager blob entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEagerBlobEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEagerBlobEntryException {

		EagerBlobEntry eagerBlobEntry = fetchByUUID_G(uuid, groupId);

		if (eagerBlobEntry == null) {
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

			throw new NoSuchEagerBlobEntryException(sb.toString());
		}

		return eagerBlobEntry;
	}

	/**
	 * Returns the eager blob entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching eager blob entry, or <code>null</code> if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the eager blob entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching eager blob entry, or <code>null</code> if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry fetchByUUID_G(
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

		if (result instanceof EagerBlobEntry) {
			EagerBlobEntry eagerBlobEntry = (EagerBlobEntry)result;

			if (!Objects.equals(uuid, eagerBlobEntry.getUuid()) ||
				(groupId != eagerBlobEntry.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_EAGERBLOBENTRY_WHERE);

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

				List<EagerBlobEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					EagerBlobEntry eagerBlobEntry = list.get(0);

					result = eagerBlobEntry;

					cacheResult(eagerBlobEntry);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(
						_finderPathFetchByUUID_G, finderArgs);
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
			return (EagerBlobEntry)result;
		}
	}

	/**
	 * Removes the eager blob entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the eager blob entry that was removed
	 */
	@Override
	public EagerBlobEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEagerBlobEntryException {

		EagerBlobEntry eagerBlobEntry = findByUUID_G(uuid, groupId);

		return remove(eagerBlobEntry);
	}

	/**
	 * Returns the number of eager blob entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching eager blob entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUUID_G;

		Object[] finderArgs = new Object[] {uuid, groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_EAGERBLOBENTRY_WHERE);

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
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"eagerBlobEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(eagerBlobEntry.uuid IS NULL OR eagerBlobEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"eagerBlobEntry.groupId = ?";

	public EagerBlobEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("blob", "blob_");

		try {
			Field field = BasePersistenceImpl.class.getDeclaredField(
				"_dbColumnNames");

			field.setAccessible(true);

			field.set(this, dbColumnNames);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}
		}

		setModelClass(EagerBlobEntry.class);
	}

	/**
	 * Caches the eager blob entry in the entity cache if it is enabled.
	 *
	 * @param eagerBlobEntry the eager blob entry
	 */
	@Override
	public void cacheResult(EagerBlobEntry eagerBlobEntry) {
		entityCache.putResult(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryImpl.class, eagerBlobEntry.getPrimaryKey(),
			eagerBlobEntry);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {
				eagerBlobEntry.getUuid(), eagerBlobEntry.getGroupId()
			},
			eagerBlobEntry);

		eagerBlobEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the eager blob entries in the entity cache if it is enabled.
	 *
	 * @param eagerBlobEntries the eager blob entries
	 */
	@Override
	public void cacheResult(List<EagerBlobEntry> eagerBlobEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (eagerBlobEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (EagerBlobEntry eagerBlobEntry : eagerBlobEntries) {
			if (entityCache.getResult(
					EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
					EagerBlobEntryImpl.class, eagerBlobEntry.getPrimaryKey()) ==
						null) {

				cacheResult(eagerBlobEntry);
			}
			else {
				eagerBlobEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all eager blob entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(EagerBlobEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the eager blob entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(EagerBlobEntry eagerBlobEntry) {
		entityCache.removeResult(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryImpl.class, eagerBlobEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((EagerBlobEntryModelImpl)eagerBlobEntry, true);
	}

	@Override
	public void clearCache(List<EagerBlobEntry> eagerBlobEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (EagerBlobEntry eagerBlobEntry : eagerBlobEntries) {
			entityCache.removeResult(
				EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
				EagerBlobEntryImpl.class, eagerBlobEntry.getPrimaryKey());

			clearUniqueFindersCache(
				(EagerBlobEntryModelImpl)eagerBlobEntry, true);
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
				EagerBlobEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		EagerBlobEntryModelImpl eagerBlobEntryModelImpl) {

		Object[] args = new Object[] {
			eagerBlobEntryModelImpl.getUuid(),
			eagerBlobEntryModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathCountByUUID_G, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByUUID_G, args, eagerBlobEntryModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		EagerBlobEntryModelImpl eagerBlobEntryModelImpl, boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {
				eagerBlobEntryModelImpl.getUuid(),
				eagerBlobEntryModelImpl.getGroupId()
			};

			finderCache.removeResult(_finderPathCountByUUID_G, args);
			finderCache.removeResult(_finderPathFetchByUUID_G, args);
		}

		if (!Objects.equals(
				eagerBlobEntryModelImpl.getUuid(),
				eagerBlobEntryModelImpl.getOriginalUuid()) ||
			(eagerBlobEntryModelImpl.getGroupId() !=
				eagerBlobEntryModelImpl.getOriginalGroupId())) {

			Object[] args = new Object[] {
				eagerBlobEntryModelImpl.getOriginalUuid(),
				eagerBlobEntryModelImpl.getOriginalGroupId()
			};

			finderCache.removeResult(_finderPathCountByUUID_G, args);
			finderCache.removeResult(_finderPathFetchByUUID_G, args);
		}
	}

	/**
	 * Creates a new eager blob entry with the primary key. Does not add the eager blob entry to the database.
	 *
	 * @param eagerBlobEntryId the primary key for the new eager blob entry
	 * @return the new eager blob entry
	 */
	@Override
	public EagerBlobEntry create(long eagerBlobEntryId) {
		EagerBlobEntry eagerBlobEntry = new EagerBlobEntryImpl();

		eagerBlobEntry.setNew(true);
		eagerBlobEntry.setPrimaryKey(eagerBlobEntryId);

		String uuid = PortalUUIDUtil.generate();

		eagerBlobEntry.setUuid(uuid);

		return eagerBlobEntry;
	}

	/**
	 * Removes the eager blob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param eagerBlobEntryId the primary key of the eager blob entry
	 * @return the eager blob entry that was removed
	 * @throws NoSuchEagerBlobEntryException if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry remove(long eagerBlobEntryId)
		throws NoSuchEagerBlobEntryException {

		return remove((Serializable)eagerBlobEntryId);
	}

	/**
	 * Removes the eager blob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the eager blob entry
	 * @return the eager blob entry that was removed
	 * @throws NoSuchEagerBlobEntryException if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry remove(Serializable primaryKey)
		throws NoSuchEagerBlobEntryException {

		Session session = null;

		try {
			session = openSession();

			EagerBlobEntry eagerBlobEntry = (EagerBlobEntry)session.get(
				EagerBlobEntryImpl.class, primaryKey);

			if (eagerBlobEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEagerBlobEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(eagerBlobEntry);
		}
		catch (NoSuchEagerBlobEntryException noSuchEntityException) {
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
	protected EagerBlobEntry removeImpl(EagerBlobEntry eagerBlobEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(eagerBlobEntry)) {
				eagerBlobEntry = (EagerBlobEntry)session.get(
					EagerBlobEntryImpl.class,
					eagerBlobEntry.getPrimaryKeyObj());
			}

			if (eagerBlobEntry != null) {
				session.delete(eagerBlobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (eagerBlobEntry != null) {
			clearCache(eagerBlobEntry);
		}

		return eagerBlobEntry;
	}

	@Override
	public EagerBlobEntry updateImpl(EagerBlobEntry eagerBlobEntry) {
		boolean isNew = eagerBlobEntry.isNew();

		if (!(eagerBlobEntry instanceof EagerBlobEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(eagerBlobEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					eagerBlobEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in eagerBlobEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom EagerBlobEntry implementation " +
					eagerBlobEntry.getClass());
		}

		EagerBlobEntryModelImpl eagerBlobEntryModelImpl =
			(EagerBlobEntryModelImpl)eagerBlobEntry;

		if (Validator.isNull(eagerBlobEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			eagerBlobEntry.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(eagerBlobEntry);

				eagerBlobEntry.setNew(false);
			}
			else {
				eagerBlobEntry = (EagerBlobEntry)session.merge(eagerBlobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}
		else {
			if (!Objects.equals(
					eagerBlobEntry.getUuid(),
					eagerBlobEntryModelImpl.getOriginalUuid())) {

				Object[] args = new Object[] {
					eagerBlobEntryModelImpl.getOriginalUuid()
				};

				finderCache.removeResult(_finderPathCountByUuid, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByUuid, args);

				args = new Object[] {eagerBlobEntryModelImpl.getUuid()};

				finderCache.removeResult(_finderPathCountByUuid, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByUuid, args);
			}
		}

		entityCache.putResult(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryImpl.class, eagerBlobEntry.getPrimaryKey(),
			eagerBlobEntry, false);

		clearUniqueFindersCache(eagerBlobEntryModelImpl, false);
		cacheUniqueFindersCache(eagerBlobEntryModelImpl);

		eagerBlobEntry.resetOriginalValues();

		return eagerBlobEntry;
	}

	/**
	 * Returns the eager blob entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the eager blob entry
	 * @return the eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEagerBlobEntryException {

		EagerBlobEntry eagerBlobEntry = fetchByPrimaryKey(primaryKey);

		if (eagerBlobEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEagerBlobEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return eagerBlobEntry;
	}

	/**
	 * Returns the eager blob entry with the primary key or throws a <code>NoSuchEagerBlobEntryException</code> if it could not be found.
	 *
	 * @param eagerBlobEntryId the primary key of the eager blob entry
	 * @return the eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry findByPrimaryKey(long eagerBlobEntryId)
		throws NoSuchEagerBlobEntryException {

		return findByPrimaryKey((Serializable)eagerBlobEntryId);
	}

	/**
	 * Returns the eager blob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the eager blob entry
	 * @return the eager blob entry, or <code>null</code> if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		EagerBlobEntry eagerBlobEntry = (EagerBlobEntry)serializable;

		if (eagerBlobEntry == null) {
			Session session = null;

			try {
				session = openSession();

				eagerBlobEntry = (EagerBlobEntry)session.get(
					EagerBlobEntryImpl.class, primaryKey);

				if (eagerBlobEntry != null) {
					cacheResult(eagerBlobEntry);
				}
				else {
					entityCache.putResult(
						EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
						EagerBlobEntryImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
					EagerBlobEntryImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return eagerBlobEntry;
	}

	/**
	 * Returns the eager blob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param eagerBlobEntryId the primary key of the eager blob entry
	 * @return the eager blob entry, or <code>null</code> if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry fetchByPrimaryKey(long eagerBlobEntryId) {
		return fetchByPrimaryKey((Serializable)eagerBlobEntryId);
	}

	@Override
	public Map<Serializable, EagerBlobEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, EagerBlobEntry> map =
			new HashMap<Serializable, EagerBlobEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			EagerBlobEntry eagerBlobEntry = fetchByPrimaryKey(primaryKey);

			if (eagerBlobEntry != null) {
				map.put(primaryKey, eagerBlobEntry);
			}

			return map;
		}

		if ((_databaseInMaxParameters > 0) &&
			(primaryKeys.size() > _databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < _databaseInMaxParameters) && iterator.hasNext();
					 i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(
				EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
				EagerBlobEntryImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (EagerBlobEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_EAGERBLOBENTRY_WHERE_PKS_IN);

		for (Serializable primaryKey : uncachedPrimaryKeys) {
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

			for (EagerBlobEntry eagerBlobEntry :
					(List<EagerBlobEntry>)query.list()) {

				map.put(eagerBlobEntry.getPrimaryKeyObj(), eagerBlobEntry);

				cacheResult(eagerBlobEntry);

				uncachedPrimaryKeys.remove(eagerBlobEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
					EagerBlobEntryImpl.class, primaryKey, nullModel);
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
	 * Returns all the eager blob entries.
	 *
	 * @return the eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the eager blob entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of eager blob entries
	 * @param end the upper bound of the range of eager blob entries (not inclusive)
	 * @return the range of eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the eager blob entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of eager blob entries
	 * @param end the upper bound of the range of eager blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findAll(
		int start, int end,
		OrderByComparator<EagerBlobEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the eager blob entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of eager blob entries
	 * @param end the upper bound of the range of eager blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findAll(
		int start, int end, OrderByComparator<EagerBlobEntry> orderByComparator,
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

		List<EagerBlobEntry> list = null;

		if (useFinderCache) {
			list = (List<EagerBlobEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_EAGERBLOBENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_EAGERBLOBENTRY;

				sql = sql.concat(EagerBlobEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<EagerBlobEntry>)QueryUtil.list(
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
	 * Removes all the eager blob entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (EagerBlobEntry eagerBlobEntry : findAll()) {
			remove(eagerBlobEntry);
		}
	}

	/**
	 * Returns the number of eager blob entries.
	 *
	 * @return the number of eager blob entries
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
					"SELECT COUNT(eagerBlobEntry) FROM EagerBlobEntry eagerBlobEntry");

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
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return EagerBlobEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the eager blob entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get("value.object.finder.cache.list.threshold"));

		_finderPathWithPaginationFindAll = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED,
			EagerBlobEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED,
			EagerBlobEntryImpl.class, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findAll", new String[0]);

		_finderPathCountAll = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationFindByUuid = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED,
			EagerBlobEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED,
			EagerBlobEntryImpl.class, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByUuid", new String[] {String.class.getName()});

		_finderPathCountByUuid = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()});

		_finderPathFetchByUUID_G = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED,
			EagerBlobEntryImpl.class, FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()});

		_finderPathCountByUUID_G = new FinderPath(
			EagerBlobEntryModelImpl.ENTITY_CACHE_ENABLED,
			EagerBlobEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()});

		EagerBlobEntryUtil.setPersistence(this);
	}

	public void destroy() {
		EagerBlobEntryUtil.setPersistence(null);

		entityCache.removeCache(EagerBlobEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);

		DBType dbType = DBManagerUtil.getDBType(sessionFactory.getDialect());

		_databaseInMaxParameters = GetterUtil.getInteger(
			PropsUtil.get(
				"database.in.max.parameters", new Filter(dbType.getName())));
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		EagerBlobEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EAGERBLOBENTRY =
		"SELECT eagerBlobEntry FROM EagerBlobEntry eagerBlobEntry";

	private static final String _SQL_SELECT_EAGERBLOBENTRY_WHERE_PKS_IN =
		"SELECT eagerBlobEntry FROM EagerBlobEntry eagerBlobEntry WHERE eagerBlobEntryId IN (";

	private static final String _SQL_SELECT_EAGERBLOBENTRY_WHERE =
		"SELECT eagerBlobEntry FROM EagerBlobEntry eagerBlobEntry WHERE ";

	private static final String _SQL_COUNT_EAGERBLOBENTRY_WHERE =
		"SELECT COUNT(eagerBlobEntry) FROM EagerBlobEntry eagerBlobEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No EagerBlobEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No EagerBlobEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		EagerBlobEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "blob"});

}
// LIFERAY-SERVICE-BUILDER-HASH:-662991030