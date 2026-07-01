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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat730.exception.DuplicateERCGroupEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchERCGroupEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.ERCGroupEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.ERCGroupEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.ERCGroupEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.ERCGroupEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.ERCGroupEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
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
 * The persistence implementation for the erc group entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ERCGroupEntryPersistenceImpl
	extends BasePersistenceImpl<ERCGroupEntry>
	implements ERCGroupEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ERCGroupEntryUtil</code> to access the erc group entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ERCGroupEntryImpl.class.getName();

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
	 * Returns all the erc group entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc group entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @return the range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc group entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCGroupEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc group entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCGroupEntry> orderByComparator,
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

		List<ERCGroupEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCGroupEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ERCGroupEntry ercGroupEntry : list) {
					if (!uuid.equals(ercGroupEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_ERCGROUPENTRY_WHERE);

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
				sb.append(ERCGroupEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<ERCGroupEntry>)QueryUtil.list(
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
	 * Returns the first erc group entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUuid_First(
			String uuid, OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (ercGroupEntry != null) {
			return ercGroupEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchERCGroupEntryException(sb.toString());
	}

	/**
	 * Returns the first erc group entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUuid_First(
		String uuid, OrderByComparator<ERCGroupEntry> orderByComparator) {

		List<ERCGroupEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last erc group entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUuid_Last(
			String uuid, OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = fetchByUuid_Last(uuid, orderByComparator);

		if (ercGroupEntry != null) {
			return ercGroupEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchERCGroupEntryException(sb.toString());
	}

	/**
	 * Returns the last erc group entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUuid_Last(
		String uuid, OrderByComparator<ERCGroupEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ERCGroupEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the erc group entries before and after the current erc group entry in the ordered set where uuid = &#63;.
	 *
	 * @param ercGroupEntryId the primary key of the current erc group entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next erc group entry
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry[] findByUuid_PrevAndNext(
			long ercGroupEntryId, String uuid,
			OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		uuid = Objects.toString(uuid, "");

		ERCGroupEntry ercGroupEntry = findByPrimaryKey(ercGroupEntryId);

		Session session = null;

		try {
			session = openSession();

			ERCGroupEntry[] array = new ERCGroupEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, ercGroupEntry, uuid, orderByComparator, true);

			array[1] = ercGroupEntry;

			array[2] = getByUuid_PrevAndNext(
				session, ercGroupEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ERCGroupEntry getByUuid_PrevAndNext(
		Session session, ERCGroupEntry ercGroupEntry, String uuid,
		OrderByComparator<ERCGroupEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_ERCGROUPENTRY_WHERE);

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
			sb.append(ERCGroupEntryModelImpl.ORDER_BY_JPQL);
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
						ercGroupEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ERCGroupEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the erc group entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ERCGroupEntry ercGroupEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ercGroupEntry);
		}
	}

	/**
	 * Returns the number of erc group entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_ERCGROUPENTRY_WHERE);

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
		"ercGroupEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(ercGroupEntry.uuid IS NULL OR ercGroupEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;
	private FinderPath _finderPathCountByUUID_G;

	/**
	 * Returns the erc group entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchERCGroupEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = fetchByUUID_G(uuid, groupId);

		if (ercGroupEntry == null) {
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

			throw new NoSuchERCGroupEntryException(sb.toString());
		}

		return ercGroupEntry;
	}

	/**
	 * Returns the erc group entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the erc group entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUUID_G(
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

		if (result instanceof ERCGroupEntry) {
			ERCGroupEntry ercGroupEntry = (ERCGroupEntry)result;

			if (!Objects.equals(uuid, ercGroupEntry.getUuid()) ||
				(groupId != ercGroupEntry.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_ERCGROUPENTRY_WHERE);

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

				List<ERCGroupEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					ERCGroupEntry ercGroupEntry = list.get(0);

					result = ercGroupEntry;

					cacheResult(ercGroupEntry);
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
			return (ERCGroupEntry)result;
		}
	}

	/**
	 * Removes the erc group entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the erc group entry that was removed
	 */
	@Override
	public ERCGroupEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = findByUUID_G(uuid, groupId);

		return remove(ercGroupEntry);
	}

	/**
	 * Returns the number of erc group entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUUID_G;

		Object[] finderArgs = new Object[] {uuid, groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCGROUPENTRY_WHERE);

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
		"ercGroupEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(ercGroupEntry.uuid IS NULL OR ercGroupEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"ercGroupEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the erc group entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc group entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @return the range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc group entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCGroupEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc group entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCGroupEntry> orderByComparator,
		boolean useFinderCache) {

		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByUuid_C;
				finderArgs = new Object[] {uuid, companyId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByUuid_C;
			finderArgs = new Object[] {
				uuid, companyId, start, end, orderByComparator
			};
		}

		List<ERCGroupEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCGroupEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ERCGroupEntry ercGroupEntry : list) {
					if (!uuid.equals(ercGroupEntry.getUuid()) ||
						(companyId != ercGroupEntry.getCompanyId())) {

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

			sb.append(_SQL_SELECT_ERCGROUPENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(ERCGroupEntryModelImpl.ORDER_BY_JPQL);
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

				queryPos.add(companyId);

				list = (List<ERCGroupEntry>)QueryUtil.list(
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
	 * Returns the first erc group entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (ercGroupEntry != null) {
			return ercGroupEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchERCGroupEntryException(sb.toString());
	}

	/**
	 * Returns the first erc group entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ERCGroupEntry> orderByComparator) {

		List<ERCGroupEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last erc group entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (ercGroupEntry != null) {
			return ercGroupEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchERCGroupEntryException(sb.toString());
	}

	/**
	 * Returns the last erc group entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ERCGroupEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ERCGroupEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the erc group entries before and after the current erc group entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param ercGroupEntryId the primary key of the current erc group entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next erc group entry
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry[] findByUuid_C_PrevAndNext(
			long ercGroupEntryId, String uuid, long companyId,
			OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		uuid = Objects.toString(uuid, "");

		ERCGroupEntry ercGroupEntry = findByPrimaryKey(ercGroupEntryId);

		Session session = null;

		try {
			session = openSession();

			ERCGroupEntry[] array = new ERCGroupEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, ercGroupEntry, uuid, companyId, orderByComparator,
				true);

			array[1] = ercGroupEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, ercGroupEntry, uuid, companyId, orderByComparator,
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

	protected ERCGroupEntry getByUuid_C_PrevAndNext(
		Session session, ERCGroupEntry ercGroupEntry, String uuid,
		long companyId, OrderByComparator<ERCGroupEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_ERCGROUPENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

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
			sb.append(ERCGroupEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						ercGroupEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ERCGroupEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the erc group entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ERCGroupEntry ercGroupEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ercGroupEntry);
		}
	}

	/**
	 * Returns the number of erc group entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCGROUPENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

				queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"ercGroupEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(ercGroupEntry.uuid IS NULL OR ercGroupEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"ercGroupEntry.companyId = ?";

	private FinderPath _finderPathFetchByG_ERC;
	private FinderPath _finderPathCountByG_ERC;

	/**
	 * Returns the erc group entry where groupId = &#63; and externalReferenceCode = &#63; or throws a <code>NoSuchERCGroupEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByG_ERC(long groupId, String externalReferenceCode)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = fetchByG_ERC(
			groupId, externalReferenceCode);

		if (ercGroupEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchERCGroupEntryException(sb.toString());
		}

		return ercGroupEntry;
	}

	/**
	 * Returns the erc group entry where groupId = &#63; and externalReferenceCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByG_ERC(
		long groupId, String externalReferenceCode) {

		return fetchByG_ERC(groupId, externalReferenceCode, true);
	}

	/**
	 * Returns the erc group entry where groupId = &#63; and externalReferenceCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByG_ERC(
		long groupId, String externalReferenceCode, boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {groupId, externalReferenceCode};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByG_ERC, finderArgs, this);
		}

		if (result instanceof ERCGroupEntry) {
			ERCGroupEntry ercGroupEntry = (ERCGroupEntry)result;

			if ((groupId != ercGroupEntry.getGroupId()) ||
				!Objects.equals(
					externalReferenceCode,
					ercGroupEntry.getExternalReferenceCode())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_ERCGROUPENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindExternalReferenceCode) {
					queryPos.add(externalReferenceCode);
				}

				List<ERCGroupEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByG_ERC, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									groupId, externalReferenceCode
								};
							}

							_log.warn(
								"ERCGroupEntryPersistenceImpl.fetchByG_ERC(long, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ERCGroupEntry ercGroupEntry = list.get(0);

					result = ercGroupEntry;

					cacheResult(ercGroupEntry);
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
			return (ERCGroupEntry)result;
		}
	}

	/**
	 * Removes the erc group entry where groupId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the erc group entry that was removed
	 */
	@Override
	public ERCGroupEntry removeByG_ERC(
			long groupId, String externalReferenceCode)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = findByG_ERC(
			groupId, externalReferenceCode);

		return remove(ercGroupEntry);
	}

	/**
	 * Returns the number of erc group entries where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByG_ERC(long groupId, String externalReferenceCode) {
		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		FinderPath finderPath = _finderPathCountByG_ERC;

		Object[] finderArgs = new Object[] {groupId, externalReferenceCode};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCGROUPENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindExternalReferenceCode) {
					queryPos.add(externalReferenceCode);
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

	private static final String _FINDER_COLUMN_G_ERC_GROUPID_2 =
		"ercGroupEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2 =
		"ercGroupEntry.externalReferenceCode = ?";

	private static final String _FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3 =
		"(ercGroupEntry.externalReferenceCode IS NULL OR ercGroupEntry.externalReferenceCode = '')";

	public ERCGroupEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ERCGroupEntry.class);

		setModelImplClass(ERCGroupEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the erc group entry in the entity cache if it is enabled.
	 *
	 * @param ercGroupEntry the erc group entry
	 */
	@Override
	public void cacheResult(ERCGroupEntry ercGroupEntry) {
		entityCache.putResult(
			ERCGroupEntryImpl.class, ercGroupEntry.getPrimaryKey(),
			ercGroupEntry);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {ercGroupEntry.getUuid(), ercGroupEntry.getGroupId()},
			ercGroupEntry);

		finderCache.putResult(
			_finderPathFetchByG_ERC,
			new Object[] {
				ercGroupEntry.getGroupId(),
				ercGroupEntry.getExternalReferenceCode()
			},
			ercGroupEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the erc group entries in the entity cache if it is enabled.
	 *
	 * @param ercGroupEntries the erc group entries
	 */
	@Override
	public void cacheResult(List<ERCGroupEntry> ercGroupEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ercGroupEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ERCGroupEntry ercGroupEntry : ercGroupEntries) {
			if (entityCache.getResult(
					ERCGroupEntryImpl.class, ercGroupEntry.getPrimaryKey()) ==
						null) {

				cacheResult(ercGroupEntry);
			}
		}
	}

	/**
	 * Clears the cache for all erc group entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ERCGroupEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the erc group entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ERCGroupEntry ercGroupEntry) {
		entityCache.removeResult(ERCGroupEntryImpl.class, ercGroupEntry);
	}

	@Override
	public void clearCache(List<ERCGroupEntry> ercGroupEntries) {
		for (ERCGroupEntry ercGroupEntry : ercGroupEntries) {
			entityCache.removeResult(ERCGroupEntryImpl.class, ercGroupEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ERCGroupEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ERCGroupEntryModelImpl ercGroupEntryModelImpl) {

		Object[] args = new Object[] {
			ercGroupEntryModelImpl.getUuid(),
			ercGroupEntryModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathCountByUUID_G, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByUUID_G, args, ercGroupEntryModelImpl, false);

		args = new Object[] {
			ercGroupEntryModelImpl.getGroupId(),
			ercGroupEntryModelImpl.getExternalReferenceCode()
		};

		finderCache.putResult(
			_finderPathCountByG_ERC, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByG_ERC, args, ercGroupEntryModelImpl, false);
	}

	/**
	 * Creates a new erc group entry with the primary key. Does not add the erc group entry to the database.
	 *
	 * @param ercGroupEntryId the primary key for the new erc group entry
	 * @return the new erc group entry
	 */
	@Override
	public ERCGroupEntry create(long ercGroupEntryId) {
		ERCGroupEntry ercGroupEntry = new ERCGroupEntryImpl();

		ercGroupEntry.setNew(true);
		ercGroupEntry.setPrimaryKey(ercGroupEntryId);

		String uuid = PortalUUIDUtil.generate();

		ercGroupEntry.setUuid(uuid);

		ercGroupEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ercGroupEntry;
	}

	/**
	 * Removes the erc group entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ercGroupEntryId the primary key of the erc group entry
	 * @return the erc group entry that was removed
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry remove(long ercGroupEntryId)
		throws NoSuchERCGroupEntryException {

		return remove((Serializable)ercGroupEntryId);
	}

	/**
	 * Removes the erc group entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the erc group entry
	 * @return the erc group entry that was removed
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry remove(Serializable primaryKey)
		throws NoSuchERCGroupEntryException {

		Session session = null;

		try {
			session = openSession();

			ERCGroupEntry ercGroupEntry = (ERCGroupEntry)session.get(
				ERCGroupEntryImpl.class, primaryKey);

			if (ercGroupEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchERCGroupEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ercGroupEntry);
		}
		catch (NoSuchERCGroupEntryException noSuchEntityException) {
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
	protected ERCGroupEntry removeImpl(ERCGroupEntry ercGroupEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ercGroupEntry)) {
				ercGroupEntry = (ERCGroupEntry)session.get(
					ERCGroupEntryImpl.class, ercGroupEntry.getPrimaryKeyObj());
			}

			if (ercGroupEntry != null) {
				session.delete(ercGroupEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ercGroupEntry != null) {
			clearCache(ercGroupEntry);
		}

		return ercGroupEntry;
	}

	@Override
	public ERCGroupEntry updateImpl(ERCGroupEntry ercGroupEntry) {
		boolean isNew = ercGroupEntry.isNew();

		if (!(ercGroupEntry instanceof ERCGroupEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ercGroupEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ercGroupEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ercGroupEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ERCGroupEntry implementation " +
					ercGroupEntry.getClass());
		}

		ERCGroupEntryModelImpl ercGroupEntryModelImpl =
			(ERCGroupEntryModelImpl)ercGroupEntry;

		if (Validator.isNull(ercGroupEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ercGroupEntry.setUuid(uuid);
		}

		if (Validator.isNull(ercGroupEntry.getExternalReferenceCode())) {
			ercGroupEntry.setExternalReferenceCode(ercGroupEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					ercGroupEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ercGroupEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ercGroupEntry.getCompanyId();

					long groupId = ercGroupEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ercGroupEntry.getPrimaryKey();
					}

					try {
						ercGroupEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ERCGroupEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ercGroupEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ERCGroupEntry ercERCGroupEntry = fetchByG_ERC(
				ercGroupEntry.getGroupId(),
				ercGroupEntry.getExternalReferenceCode());

			if (isNew) {
				if (ercERCGroupEntry != null) {
					throw new DuplicateERCGroupEntryExternalReferenceCodeException(
						"Duplicate erc group entry with external reference code " +
							ercGroupEntry.getExternalReferenceCode() +
								" and group " + ercGroupEntry.getGroupId());
				}
			}
			else {
				if ((ercERCGroupEntry != null) &&
					(ercGroupEntry.getErcGroupEntryId() !=
						ercERCGroupEntry.getErcGroupEntryId())) {

					throw new DuplicateERCGroupEntryExternalReferenceCodeException(
						"Duplicate erc group entry with external reference code " +
							ercGroupEntry.getExternalReferenceCode() +
								" and group " + ercGroupEntry.getGroupId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ercGroupEntry);
			}
			else {
				ercGroupEntry = (ERCGroupEntry)session.merge(ercGroupEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ERCGroupEntryImpl.class, ercGroupEntryModelImpl, false, true);

		cacheUniqueFindersCache(ercGroupEntryModelImpl);

		if (isNew) {
			ercGroupEntry.setNew(false);
		}

		ercGroupEntry.resetOriginalValues();

		return ercGroupEntry;
	}

	/**
	 * Returns the erc group entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the erc group entry
	 * @return the erc group entry
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = fetchByPrimaryKey(primaryKey);

		if (ercGroupEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchERCGroupEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ercGroupEntry;
	}

	/**
	 * Returns the erc group entry with the primary key or throws a <code>NoSuchERCGroupEntryException</code> if it could not be found.
	 *
	 * @param ercGroupEntryId the primary key of the erc group entry
	 * @return the erc group entry
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry findByPrimaryKey(long ercGroupEntryId)
		throws NoSuchERCGroupEntryException {

		return findByPrimaryKey((Serializable)ercGroupEntryId);
	}

	/**
	 * Returns the erc group entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ercGroupEntryId the primary key of the erc group entry
	 * @return the erc group entry, or <code>null</code> if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry fetchByPrimaryKey(long ercGroupEntryId) {
		return fetchByPrimaryKey((Serializable)ercGroupEntryId);
	}

	/**
	 * Returns all the erc group entries.
	 *
	 * @return the erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc group entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @return the range of erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc group entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findAll(
		int start, int end,
		OrderByComparator<ERCGroupEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc group entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findAll(
		int start, int end, OrderByComparator<ERCGroupEntry> orderByComparator,
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

		List<ERCGroupEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCGroupEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_ERCGROUPENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_ERCGROUPENTRY;

				sql = sql.concat(ERCGroupEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ERCGroupEntry>)QueryUtil.list(
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
	 * Removes all the erc group entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ERCGroupEntry ercGroupEntry : findAll()) {
			remove(ercGroupEntry);
		}
	}

	/**
	 * Returns the number of erc group entries.
	 *
	 * @return the number of erc group entries
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
					"SELECT COUNT(ercGroupEntry) FROM ERCGroupEntry ercGroupEntry");

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
		return "ercGroupEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ERCGROUPENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ERCGroupEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the erc group entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			ERCGroupEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class, new ERCGroupEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", ERCGroupEntry.class.getName()));

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

		_finderPathWithPaginationFindByUuid_C = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_finderPathFetchByG_ERC = _createFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_ERC",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "externalReferenceCode"}, true);

		_finderPathCountByG_ERC = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ERC",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "externalReferenceCode"}, false);

		ERCGroupEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ERCGroupEntryUtil.setPersistence(null);

		entityCache.removeCache(ERCGroupEntryImpl.class.getName());

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
		ERCGroupEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ERCGROUPENTRY =
		"SELECT ercGroupEntry FROM ERCGroupEntry ercGroupEntry";

	private static final String _SQL_SELECT_ERCGROUPENTRY_WHERE =
		"SELECT ercGroupEntry FROM ERCGroupEntry ercGroupEntry WHERE ";

	private static final String _SQL_COUNT_ERCGROUPENTRY_WHERE =
		"SELECT COUNT(ercGroupEntry) FROM ERCGroupEntry ercGroupEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ERCGroupEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ERCGroupEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ERCGroupEntryPersistenceImpl.class);

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

	private static class ERCGroupEntryModelArgumentsResolver
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

			ERCGroupEntryModelImpl ercGroupEntryModelImpl =
				(ERCGroupEntryModelImpl)baseModel;

			long columnBitmask = ercGroupEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(ercGroupEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						ercGroupEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(ercGroupEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			ERCGroupEntryModelImpl ercGroupEntryModelImpl, String[] columnNames,
			boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						ercGroupEntryModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = ercGroupEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:885821859