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
import com.liferay.portal.tools.service.builder.test.compat730.exception.DuplicateERCVersionedEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchERCVersionedEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.ERCVersionedEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.ERCVersionedEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.ERCVersionedEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.ERCVersionedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.ERCVersionedEntryUtil;

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
 * The persistence implementation for the erc versioned entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ERCVersionedEntryPersistenceImpl
	extends BasePersistenceImpl<ERCVersionedEntry>
	implements ERCVersionedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ERCVersionedEntryUtil</code> to access the erc versioned entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ERCVersionedEntryImpl.class.getName();

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
	 * Returns all the erc versioned entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc versioned entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @return the range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
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

		List<ERCVersionedEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCVersionedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ERCVersionedEntry ercVersionedEntry : list) {
					if (!uuid.equals(ercVersionedEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_ERCVERSIONEDENTRY_WHERE);

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
				sb.append(ERCVersionedEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<ERCVersionedEntry>)QueryUtil.list(
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
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_First(
			String uuid, OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (ercVersionedEntry != null) {
			return ercVersionedEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchERCVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_First(
		String uuid, OrderByComparator<ERCVersionedEntry> orderByComparator) {

		List<ERCVersionedEntry> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last erc versioned entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_Last(
			String uuid, OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = fetchByUuid_Last(
			uuid, orderByComparator);

		if (ercVersionedEntry != null) {
			return ercVersionedEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchERCVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the last erc versioned entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_Last(
		String uuid, OrderByComparator<ERCVersionedEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ERCVersionedEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the erc versioned entries before and after the current erc versioned entry in the ordered set where uuid = &#63;.
	 *
	 * @param ercVersionedEntryId the primary key of the current erc versioned entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry[] findByUuid_PrevAndNext(
			long ercVersionedEntryId, String uuid,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		uuid = Objects.toString(uuid, "");

		ERCVersionedEntry ercVersionedEntry = findByPrimaryKey(
			ercVersionedEntryId);

		Session session = null;

		try {
			session = openSession();

			ERCVersionedEntry[] array = new ERCVersionedEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, ercVersionedEntry, uuid, orderByComparator, true);

			array[1] = ercVersionedEntry;

			array[2] = getByUuid_PrevAndNext(
				session, ercVersionedEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ERCVersionedEntry getByUuid_PrevAndNext(
		Session session, ERCVersionedEntry ercVersionedEntry, String uuid,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_ERCVERSIONEDENTRY_WHERE);

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
			sb.append(ERCVersionedEntryModelImpl.ORDER_BY_JPQL);
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
						ercVersionedEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ERCVersionedEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the erc versioned entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ERCVersionedEntry ercVersionedEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ercVersionedEntry);
		}
	}

	/**
	 * Returns the number of erc versioned entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_ERCVERSIONEDENTRY_WHERE);

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
		"ercVersionedEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(ercVersionedEntry.uuid IS NULL OR ercVersionedEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;
	private FinderPath _finderPathCountByUUID_G;

	/**
	 * Returns the erc versioned entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchERCVersionedEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = fetchByUUID_G(uuid, groupId);

		if (ercVersionedEntry == null) {
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

			throw new NoSuchERCVersionedEntryException(sb.toString());
		}

		return ercVersionedEntry;
	}

	/**
	 * Returns the erc versioned entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the erc versioned entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUUID_G(
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

		if (result instanceof ERCVersionedEntry) {
			ERCVersionedEntry ercVersionedEntry = (ERCVersionedEntry)result;

			if (!Objects.equals(uuid, ercVersionedEntry.getUuid()) ||
				(groupId != ercVersionedEntry.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_ERCVERSIONEDENTRY_WHERE);

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

				List<ERCVersionedEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					ERCVersionedEntry ercVersionedEntry = list.get(0);

					result = ercVersionedEntry;

					cacheResult(ercVersionedEntry);
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
			return (ERCVersionedEntry)result;
		}
	}

	/**
	 * Removes the erc versioned entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the erc versioned entry that was removed
	 */
	@Override
	public ERCVersionedEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = findByUUID_G(uuid, groupId);

		return remove(ercVersionedEntry);
	}

	/**
	 * Returns the number of erc versioned entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUUID_G;

		Object[] finderArgs = new Object[] {uuid, groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCVERSIONEDENTRY_WHERE);

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
		"ercVersionedEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(ercVersionedEntry.uuid IS NULL OR ercVersionedEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"ercVersionedEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the erc versioned entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc versioned entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @return the range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc versioned entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
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

		List<ERCVersionedEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCVersionedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ERCVersionedEntry ercVersionedEntry : list) {
					if (!uuid.equals(ercVersionedEntry.getUuid()) ||
						(companyId != ercVersionedEntry.getCompanyId())) {

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

			sb.append(_SQL_SELECT_ERCVERSIONEDENTRY_WHERE);

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
				sb.append(ERCVersionedEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<ERCVersionedEntry>)QueryUtil.list(
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
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (ercVersionedEntry != null) {
			return ercVersionedEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchERCVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the first erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		List<ERCVersionedEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (ercVersionedEntry != null) {
			return ercVersionedEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchERCVersionedEntryException(sb.toString());
	}

	/**
	 * Returns the last erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ERCVersionedEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the erc versioned entries before and after the current erc versioned entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param ercVersionedEntryId the primary key of the current erc versioned entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry[] findByUuid_C_PrevAndNext(
			long ercVersionedEntryId, String uuid, long companyId,
			OrderByComparator<ERCVersionedEntry> orderByComparator)
		throws NoSuchERCVersionedEntryException {

		uuid = Objects.toString(uuid, "");

		ERCVersionedEntry ercVersionedEntry = findByPrimaryKey(
			ercVersionedEntryId);

		Session session = null;

		try {
			session = openSession();

			ERCVersionedEntry[] array = new ERCVersionedEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, ercVersionedEntry, uuid, companyId, orderByComparator,
				true);

			array[1] = ercVersionedEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, ercVersionedEntry, uuid, companyId, orderByComparator,
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

	protected ERCVersionedEntry getByUuid_C_PrevAndNext(
		Session session, ERCVersionedEntry ercVersionedEntry, String uuid,
		long companyId, OrderByComparator<ERCVersionedEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_ERCVERSIONEDENTRY_WHERE);

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
			sb.append(ERCVersionedEntryModelImpl.ORDER_BY_JPQL);
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
						ercVersionedEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ERCVersionedEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the erc versioned entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ERCVersionedEntry ercVersionedEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ercVersionedEntry);
		}
	}

	/**
	 * Returns the number of erc versioned entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCVERSIONEDENTRY_WHERE);

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
		"ercVersionedEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(ercVersionedEntry.uuid IS NULL OR ercVersionedEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"ercVersionedEntry.companyId = ?";

	private FinderPath _finderPathFetchByG_ERC;
	private FinderPath _finderPathCountByG_ERC;

	/**
	 * Returns the erc versioned entry where groupId = &#63; and externalReferenceCode = &#63; or throws a <code>NoSuchERCVersionedEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry findByG_ERC(
			long groupId, String externalReferenceCode)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = fetchByG_ERC(
			groupId, externalReferenceCode);

		if (ercVersionedEntry == null) {
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

			throw new NoSuchERCVersionedEntryException(sb.toString());
		}

		return ercVersionedEntry;
	}

	/**
	 * Returns the erc versioned entry where groupId = &#63; and externalReferenceCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByG_ERC(
		long groupId, String externalReferenceCode) {

		return fetchByG_ERC(groupId, externalReferenceCode, true);
	}

	/**
	 * Returns the erc versioned entry where groupId = &#63; and externalReferenceCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc versioned entry, or <code>null</code> if a matching erc versioned entry could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByG_ERC(
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

		if (result instanceof ERCVersionedEntry) {
			ERCVersionedEntry ercVersionedEntry = (ERCVersionedEntry)result;

			if ((groupId != ercVersionedEntry.getGroupId()) ||
				!Objects.equals(
					externalReferenceCode,
					ercVersionedEntry.getExternalReferenceCode())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_ERCVERSIONEDENTRY_WHERE);

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

				List<ERCVersionedEntry> list = query.list();

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
								"ERCVersionedEntryPersistenceImpl.fetchByG_ERC(long, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ERCVersionedEntry ercVersionedEntry = list.get(0);

					result = ercVersionedEntry;

					cacheResult(ercVersionedEntry);
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
			return (ERCVersionedEntry)result;
		}
	}

	/**
	 * Removes the erc versioned entry where groupId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the erc versioned entry that was removed
	 */
	@Override
	public ERCVersionedEntry removeByG_ERC(
			long groupId, String externalReferenceCode)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = findByG_ERC(
			groupId, externalReferenceCode);

		return remove(ercVersionedEntry);
	}

	/**
	 * Returns the number of erc versioned entries where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching erc versioned entries
	 */
	@Override
	public int countByG_ERC(long groupId, String externalReferenceCode) {
		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		FinderPath finderPath = _finderPathCountByG_ERC;

		Object[] finderArgs = new Object[] {groupId, externalReferenceCode};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCVERSIONEDENTRY_WHERE);

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
		"ercVersionedEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2 =
		"ercVersionedEntry.externalReferenceCode = ?";

	private static final String _FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3 =
		"(ercVersionedEntry.externalReferenceCode IS NULL OR ercVersionedEntry.externalReferenceCode = '')";

	public ERCVersionedEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ERCVersionedEntry.class);

		setModelImplClass(ERCVersionedEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the erc versioned entry in the entity cache if it is enabled.
	 *
	 * @param ercVersionedEntry the erc versioned entry
	 */
	@Override
	public void cacheResult(ERCVersionedEntry ercVersionedEntry) {
		entityCache.putResult(
			ERCVersionedEntryImpl.class, ercVersionedEntry.getPrimaryKey(),
			ercVersionedEntry);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {
				ercVersionedEntry.getUuid(), ercVersionedEntry.getGroupId()
			},
			ercVersionedEntry);

		finderCache.putResult(
			_finderPathFetchByG_ERC,
			new Object[] {
				ercVersionedEntry.getGroupId(),
				ercVersionedEntry.getExternalReferenceCode()
			},
			ercVersionedEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the erc versioned entries in the entity cache if it is enabled.
	 *
	 * @param ercVersionedEntries the erc versioned entries
	 */
	@Override
	public void cacheResult(List<ERCVersionedEntry> ercVersionedEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ercVersionedEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ERCVersionedEntry ercVersionedEntry : ercVersionedEntries) {
			if (entityCache.getResult(
					ERCVersionedEntryImpl.class,
					ercVersionedEntry.getPrimaryKey()) == null) {

				cacheResult(ercVersionedEntry);
			}
		}
	}

	/**
	 * Clears the cache for all erc versioned entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ERCVersionedEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the erc versioned entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ERCVersionedEntry ercVersionedEntry) {
		entityCache.removeResult(
			ERCVersionedEntryImpl.class, ercVersionedEntry);
	}

	@Override
	public void clearCache(List<ERCVersionedEntry> ercVersionedEntries) {
		for (ERCVersionedEntry ercVersionedEntry : ercVersionedEntries) {
			entityCache.removeResult(
				ERCVersionedEntryImpl.class, ercVersionedEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ERCVersionedEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ERCVersionedEntryModelImpl ercVersionedEntryModelImpl) {

		Object[] args = new Object[] {
			ercVersionedEntryModelImpl.getUuid(),
			ercVersionedEntryModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathCountByUUID_G, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByUUID_G, args, ercVersionedEntryModelImpl, false);

		args = new Object[] {
			ercVersionedEntryModelImpl.getGroupId(),
			ercVersionedEntryModelImpl.getExternalReferenceCode()
		};

		finderCache.putResult(
			_finderPathCountByG_ERC, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByG_ERC, args, ercVersionedEntryModelImpl, false);
	}

	/**
	 * Creates a new erc versioned entry with the primary key. Does not add the erc versioned entry to the database.
	 *
	 * @param ercVersionedEntryId the primary key for the new erc versioned entry
	 * @return the new erc versioned entry
	 */
	@Override
	public ERCVersionedEntry create(long ercVersionedEntryId) {
		ERCVersionedEntry ercVersionedEntry = new ERCVersionedEntryImpl();

		ercVersionedEntry.setNew(true);
		ercVersionedEntry.setPrimaryKey(ercVersionedEntryId);

		String uuid = PortalUUIDUtil.generate();

		ercVersionedEntry.setUuid(uuid);

		ercVersionedEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ercVersionedEntry;
	}

	/**
	 * Removes the erc versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry that was removed
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry remove(long ercVersionedEntryId)
		throws NoSuchERCVersionedEntryException {

		return remove((Serializable)ercVersionedEntryId);
	}

	/**
	 * Removes the erc versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the erc versioned entry
	 * @return the erc versioned entry that was removed
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry remove(Serializable primaryKey)
		throws NoSuchERCVersionedEntryException {

		Session session = null;

		try {
			session = openSession();

			ERCVersionedEntry ercVersionedEntry =
				(ERCVersionedEntry)session.get(
					ERCVersionedEntryImpl.class, primaryKey);

			if (ercVersionedEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchERCVersionedEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ercVersionedEntry);
		}
		catch (NoSuchERCVersionedEntryException noSuchEntityException) {
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
	protected ERCVersionedEntry removeImpl(
		ERCVersionedEntry ercVersionedEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ercVersionedEntry)) {
				ercVersionedEntry = (ERCVersionedEntry)session.get(
					ERCVersionedEntryImpl.class,
					ercVersionedEntry.getPrimaryKeyObj());
			}

			if (ercVersionedEntry != null) {
				session.delete(ercVersionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ercVersionedEntry != null) {
			clearCache(ercVersionedEntry);
		}

		return ercVersionedEntry;
	}

	@Override
	public ERCVersionedEntry updateImpl(ERCVersionedEntry ercVersionedEntry) {
		boolean isNew = ercVersionedEntry.isNew();

		if (!(ercVersionedEntry instanceof ERCVersionedEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ercVersionedEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ercVersionedEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ercVersionedEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ERCVersionedEntry implementation " +
					ercVersionedEntry.getClass());
		}

		ERCVersionedEntryModelImpl ercVersionedEntryModelImpl =
			(ERCVersionedEntryModelImpl)ercVersionedEntry;

		if (Validator.isNull(ercVersionedEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ercVersionedEntry.setUuid(uuid);
		}

		if (Validator.isNull(ercVersionedEntry.getExternalReferenceCode())) {
			ercVersionedEntry.setExternalReferenceCode(
				ercVersionedEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					ercVersionedEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ercVersionedEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ercVersionedEntry.getCompanyId();

					long groupId = ercVersionedEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ercVersionedEntry.getPrimaryKey();
					}

					try {
						ercVersionedEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ERCVersionedEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ercVersionedEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ERCVersionedEntry ercERCVersionedEntry = fetchByG_ERC(
				ercVersionedEntry.getGroupId(),
				ercVersionedEntry.getExternalReferenceCode());

			if (isNew) {
				if (ercERCVersionedEntry != null) {
					throw new DuplicateERCVersionedEntryExternalReferenceCodeException(
						"Duplicate erc versioned entry with external reference code " +
							ercVersionedEntry.getExternalReferenceCode() +
								" and group " + ercVersionedEntry.getGroupId());
				}
			}
			else {
				if ((ercERCVersionedEntry != null) &&
					(ercVersionedEntry.getErcVersionedEntryId() !=
						ercERCVersionedEntry.getErcVersionedEntryId())) {

					throw new DuplicateERCVersionedEntryExternalReferenceCodeException(
						"Duplicate erc versioned entry with external reference code " +
							ercVersionedEntry.getExternalReferenceCode() +
								" and group " + ercVersionedEntry.getGroupId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ercVersionedEntry);
			}
			else {
				ercVersionedEntry = (ERCVersionedEntry)session.merge(
					ercVersionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ERCVersionedEntryImpl.class, ercVersionedEntryModelImpl, false,
			true);

		cacheUniqueFindersCache(ercVersionedEntryModelImpl);

		if (isNew) {
			ercVersionedEntry.setNew(false);
		}

		ercVersionedEntry.resetOriginalValues();

		return ercVersionedEntry;
	}

	/**
	 * Returns the erc versioned entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the erc versioned entry
	 * @return the erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchERCVersionedEntryException {

		ERCVersionedEntry ercVersionedEntry = fetchByPrimaryKey(primaryKey);

		if (ercVersionedEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchERCVersionedEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ercVersionedEntry;
	}

	/**
	 * Returns the erc versioned entry with the primary key or throws a <code>NoSuchERCVersionedEntryException</code> if it could not be found.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry
	 * @throws NoSuchERCVersionedEntryException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry findByPrimaryKey(long ercVersionedEntryId)
		throws NoSuchERCVersionedEntryException {

		return findByPrimaryKey((Serializable)ercVersionedEntryId);
	}

	/**
	 * Returns the erc versioned entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry, or <code>null</code> if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public ERCVersionedEntry fetchByPrimaryKey(long ercVersionedEntryId) {
		return fetchByPrimaryKey((Serializable)ercVersionedEntryId);
	}

	/**
	 * Returns all the erc versioned entries.
	 *
	 * @return the erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @return the range of erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findAll(
		int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of erc versioned entries
	 */
	@Override
	public List<ERCVersionedEntry> findAll(
		int start, int end,
		OrderByComparator<ERCVersionedEntry> orderByComparator,
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

		List<ERCVersionedEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCVersionedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_ERCVERSIONEDENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_ERCVERSIONEDENTRY;

				sql = sql.concat(ERCVersionedEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ERCVersionedEntry>)QueryUtil.list(
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
	 * Removes all the erc versioned entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ERCVersionedEntry ercVersionedEntry : findAll()) {
			remove(ercVersionedEntry);
		}
	}

	/**
	 * Returns the number of erc versioned entries.
	 *
	 * @return the number of erc versioned entries
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
					"SELECT COUNT(ercVersionedEntry) FROM ERCVersionedEntry ercVersionedEntry");

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
		return "ercVersionedEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ERCVERSIONEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ERCVersionedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the erc versioned entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			ERCVersionedEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class,
			new ERCVersionedEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", ERCVersionedEntry.class.getName()));

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

		ERCVersionedEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ERCVersionedEntryUtil.setPersistence(null);

		entityCache.removeCache(ERCVersionedEntryImpl.class.getName());

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
		ERCVersionedEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ERCVERSIONEDENTRY =
		"SELECT ercVersionedEntry FROM ERCVersionedEntry ercVersionedEntry";

	private static final String _SQL_SELECT_ERCVERSIONEDENTRY_WHERE =
		"SELECT ercVersionedEntry FROM ERCVersionedEntry ercVersionedEntry WHERE ";

	private static final String _SQL_COUNT_ERCVERSIONEDENTRY_WHERE =
		"SELECT COUNT(ercVersionedEntry) FROM ERCVersionedEntry ercVersionedEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ERCVersionedEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ERCVersionedEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ERCVersionedEntryPersistenceImpl.class);

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

	private static class ERCVersionedEntryModelArgumentsResolver
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

			ERCVersionedEntryModelImpl ercVersionedEntryModelImpl =
				(ERCVersionedEntryModelImpl)baseModel;

			long columnBitmask = ercVersionedEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(
					ercVersionedEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						ercVersionedEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(
					ercVersionedEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			ERCVersionedEntryModelImpl ercVersionedEntryModelImpl,
			String[] columnNames, boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						ercVersionedEntryModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = ercVersionedEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2045789773