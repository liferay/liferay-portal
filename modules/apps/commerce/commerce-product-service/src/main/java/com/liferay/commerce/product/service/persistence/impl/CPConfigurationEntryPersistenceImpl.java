/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPConfigurationEntryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationEntryException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntryTable;
import com.liferay.commerce.product.model.impl.CPConfigurationEntryImpl;
import com.liferay.commerce.product.model.impl.CPConfigurationEntryModelImpl;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntryPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntryUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
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

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cp configuration entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPConfigurationEntryPersistence.class)
public class CPConfigurationEntryPersistenceImpl
	extends BasePersistenceImpl<CPConfigurationEntry>
	implements CPConfigurationEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPConfigurationEntryUtil</code> to access the cp configuration entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPConfigurationEntryImpl.class.getName();

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
	 * Returns all the cp configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

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

			List<CPConfigurationEntry> list = null;

			if (useFinderCache) {
				list = (List<CPConfigurationEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPConfigurationEntry cpConfigurationEntry : list) {
						if (!uuid.equals(cpConfigurationEntry.getUuid())) {
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

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

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
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<CPConfigurationEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUuid_First(
			String uuid,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		List<CPConfigurationEntry> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUuid_Last(
			String uuid,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByUuid_Last(
			uuid, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUuid_Last(
		String uuid,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CPConfigurationEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry[] findByUuid_PrevAndNext(
			long CPConfigurationEntryId, String uuid,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		uuid = Objects.toString(uuid, "");

		CPConfigurationEntry cpConfigurationEntry = findByPrimaryKey(
			CPConfigurationEntryId);

		Session session = null;

		try {
			session = openSession();

			CPConfigurationEntry[] array = new CPConfigurationEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, cpConfigurationEntry, uuid, orderByComparator, true);

			array[1] = cpConfigurationEntry;

			array[2] = getByUuid_PrevAndNext(
				session, cpConfigurationEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPConfigurationEntry getByUuid_PrevAndNext(
		Session session, CPConfigurationEntry cpConfigurationEntry, String uuid,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

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
			sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
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
						cpConfigurationEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPConfigurationEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp configuration entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CPConfigurationEntry cpConfigurationEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpConfigurationEntry);
		}
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"cpConfigurationEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(cpConfigurationEntry.uuid IS NULL OR cpConfigurationEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByUUID_G(
			uuid, groupId);

		if (cpConfigurationEntry == null) {
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

			throw new NoSuchCPConfigurationEntryException(sb.toString());
		}

		return cpConfigurationEntry;
	}

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

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

			if (result instanceof CPConfigurationEntry) {
				CPConfigurationEntry cpConfigurationEntry =
					(CPConfigurationEntry)result;

				if (!Objects.equals(uuid, cpConfigurationEntry.getUuid()) ||
					(groupId != cpConfigurationEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

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

					List<CPConfigurationEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						CPConfigurationEntry cpConfigurationEntry = list.get(0);

						result = cpConfigurationEntry;

						cacheResult(cpConfigurationEntry);
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
				return (CPConfigurationEntry)result;
			}
		}
	}

	/**
	 * Removes the cp configuration entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp configuration entry that was removed
	 */
	@Override
	public CPConfigurationEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByUUID_G(uuid, groupId);

		return remove(cpConfigurationEntry);
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		CPConfigurationEntry cpConfigurationEntry = fetchByUUID_G(
			uuid, groupId);

		if (cpConfigurationEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"cpConfigurationEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(cpConfigurationEntry.uuid IS NULL OR cpConfigurationEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"cpConfigurationEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

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

			List<CPConfigurationEntry> list = null;

			if (useFinderCache) {
				list = (List<CPConfigurationEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPConfigurationEntry cpConfigurationEntry : list) {
						if (!uuid.equals(cpConfigurationEntry.getUuid()) ||
							(companyId !=
								cpConfigurationEntry.getCompanyId())) {

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

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

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
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<CPConfigurationEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		List<CPConfigurationEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CPConfigurationEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry[] findByUuid_C_PrevAndNext(
			long CPConfigurationEntryId, String uuid, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		uuid = Objects.toString(uuid, "");

		CPConfigurationEntry cpConfigurationEntry = findByPrimaryKey(
			CPConfigurationEntryId);

		Session session = null;

		try {
			session = openSession();

			CPConfigurationEntry[] array = new CPConfigurationEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, cpConfigurationEntry, uuid, companyId,
				orderByComparator, true);

			array[1] = cpConfigurationEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, cpConfigurationEntry, uuid, companyId,
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

	protected CPConfigurationEntry getByUuid_C_PrevAndNext(
		Session session, CPConfigurationEntry cpConfigurationEntry, String uuid,
		long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

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
			sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
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
						cpConfigurationEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPConfigurationEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp configuration entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CPConfigurationEntry cpConfigurationEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpConfigurationEntry);
		}
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"cpConfigurationEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(cpConfigurationEntry.uuid IS NULL OR cpConfigurationEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"cpConfigurationEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the cp configuration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCompanyId;
					finderArgs = new Object[] {companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCompanyId;
				finderArgs = new Object[] {
					companyId, start, end, orderByComparator
				};
			}

			List<CPConfigurationEntry> list = null;

			if (useFinderCache) {
				list = (List<CPConfigurationEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPConfigurationEntry cpConfigurationEntry : list) {
						if (companyId != cpConfigurationEntry.getCompanyId()) {
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

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<CPConfigurationEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		List<CPConfigurationEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByCompanyId_Last(
			long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CPConfigurationEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry[] findByCompanyId_PrevAndNext(
			long CPConfigurationEntryId, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByPrimaryKey(
			CPConfigurationEntryId);

		Session session = null;

		try {
			session = openSession();

			CPConfigurationEntry[] array = new CPConfigurationEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, cpConfigurationEntry, companyId, orderByComparator,
				true);

			array[1] = cpConfigurationEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, cpConfigurationEntry, companyId, orderByComparator,
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

	protected CPConfigurationEntry getByCompanyId_PrevAndNext(
		Session session, CPConfigurationEntry cpConfigurationEntry,
		long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

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
			sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpConfigurationEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPConfigurationEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp configuration entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CPConfigurationEntry cpConfigurationEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpConfigurationEntry);
		}
	}

	/**
	 * Returns the number of cp configuration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

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
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"cpConfigurationEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCPConfigurationListId;
	private FinderPath _finderPathWithoutPaginationFindByCPConfigurationListId;
	private FinderPath _finderPathCountByCPConfigurationListId;

	/**
	 * Returns all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId) {

		return findByCPConfigurationListId(
			CPConfigurationListId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end) {

		return findByCPConfigurationListId(
			CPConfigurationListId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return findByCPConfigurationListId(
			CPConfigurationListId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCPConfigurationListId;
					finderArgs = new Object[] {CPConfigurationListId};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByCPConfigurationListId;
				finderArgs = new Object[] {
					CPConfigurationListId, start, end, orderByComparator
				};
			}

			List<CPConfigurationEntry> list = null;

			if (useFinderCache) {
				list = (List<CPConfigurationEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPConfigurationEntry cpConfigurationEntry : list) {
						if (CPConfigurationListId !=
								cpConfigurationEntry.
									getCPConfigurationListId()) {

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

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

				sb.append(
					_FINDER_COLUMN_CPCONFIGURATIONLISTID_CPCONFIGURATIONLISTID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPConfigurationListId);

					list = (List<CPConfigurationEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByCPConfigurationListId_First(
			long CPConfigurationListId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry =
			fetchByCPConfigurationListId_First(
				CPConfigurationListId, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPConfigurationListId=");
		sb.append(CPConfigurationListId);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByCPConfigurationListId_First(
		long CPConfigurationListId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		List<CPConfigurationEntry> list = findByCPConfigurationListId(
			CPConfigurationListId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByCPConfigurationListId_Last(
			long CPConfigurationListId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry =
			fetchByCPConfigurationListId_Last(
				CPConfigurationListId, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPConfigurationListId=");
		sb.append(CPConfigurationListId);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByCPConfigurationListId_Last(
		long CPConfigurationListId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		int count = countByCPConfigurationListId(CPConfigurationListId);

		if (count == 0) {
			return null;
		}

		List<CPConfigurationEntry> list = findByCPConfigurationListId(
			CPConfigurationListId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry[] findByCPConfigurationListId_PrevAndNext(
			long CPConfigurationEntryId, long CPConfigurationListId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByPrimaryKey(
			CPConfigurationEntryId);

		Session session = null;

		try {
			session = openSession();

			CPConfigurationEntry[] array = new CPConfigurationEntryImpl[3];

			array[0] = getByCPConfigurationListId_PrevAndNext(
				session, cpConfigurationEntry, CPConfigurationListId,
				orderByComparator, true);

			array[1] = cpConfigurationEntry;

			array[2] = getByCPConfigurationListId_PrevAndNext(
				session, cpConfigurationEntry, CPConfigurationListId,
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

	protected CPConfigurationEntry getByCPConfigurationListId_PrevAndNext(
		Session session, CPConfigurationEntry cpConfigurationEntry,
		long CPConfigurationListId,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

		sb.append(_FINDER_COLUMN_CPCONFIGURATIONLISTID_CPCONFIGURATIONLISTID_2);

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
			sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPConfigurationListId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpConfigurationEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPConfigurationEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp configuration entries where CPConfigurationListId = &#63; from the database.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 */
	@Override
	public void removeByCPConfigurationListId(long CPConfigurationListId) {
		for (CPConfigurationEntry cpConfigurationEntry :
				findByCPConfigurationListId(
					CPConfigurationListId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpConfigurationEntry);
		}
	}

	/**
	 * Returns the number of cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByCPConfigurationListId(long CPConfigurationListId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			FinderPath finderPath = _finderPathCountByCPConfigurationListId;

			Object[] finderArgs = new Object[] {CPConfigurationListId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE);

				sb.append(
					_FINDER_COLUMN_CPCONFIGURATIONLISTID_CPCONFIGURATIONLISTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPConfigurationListId);

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
	}

	private static final String
		_FINDER_COLUMN_CPCONFIGURATIONLISTID_CPCONFIGURATIONLISTID_2 =
			"cpConfigurationEntry.CPConfigurationListId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK) {

		return findByC_C(
			classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return findByC_C(classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return findByC_C(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C;
					finderArgs = new Object[] {classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C;
				finderArgs = new Object[] {
					classNameId, classPK, start, end, orderByComparator
				};
			}

			List<CPConfigurationEntry> list = null;

			if (useFinderCache) {
				list = (List<CPConfigurationEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPConfigurationEntry cpConfigurationEntry : list) {
						if ((classNameId !=
								cpConfigurationEntry.getClassNameId()) ||
							(classPK != cpConfigurationEntry.getClassPK())) {

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

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<CPConfigurationEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByC_C_First(
			classNameId, classPK, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		List<CPConfigurationEntry> list = findByC_C(
			classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByC_C_Last(
			long classNameId, long classPK,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByC_C_Last(
			classNameId, classPK, orderByComparator);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchCPConfigurationEntryException(sb.toString());
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByC_C_Last(
		long classNameId, long classPK,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		int count = countByC_C(classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<CPConfigurationEntry> list = findByC_C(
			classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry[] findByC_C_PrevAndNext(
			long CPConfigurationEntryId, long classNameId, long classPK,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByPrimaryKey(
			CPConfigurationEntryId);

		Session session = null;

		try {
			session = openSession();

			CPConfigurationEntry[] array = new CPConfigurationEntryImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, cpConfigurationEntry, classNameId, classPK,
				orderByComparator, true);

			array[1] = cpConfigurationEntry;

			array[2] = getByC_C_PrevAndNext(
				session, cpConfigurationEntry, classNameId, classPK,
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

	protected CPConfigurationEntry getByC_C_PrevAndNext(
		Session session, CPConfigurationEntry cpConfigurationEntry,
		long classNameId, long classPK,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

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
			sb.append(CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpConfigurationEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPConfigurationEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp configuration entries where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		for (CPConfigurationEntry cpConfigurationEntry :
				findByC_C(
					classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpConfigurationEntry);
		}
	}

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {classNameId, classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPCONFIGURATIONENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

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
	}

	private static final String _FINDER_COLUMN_C_C_CLASSNAMEID_2 =
		"cpConfigurationEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CLASSPK_2 =
		"cpConfigurationEntry.classPK = ?";

	private FinderPath _finderPathFetchByC_C_C;

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByC_C_C(
			classNameId, classPK, CPConfigurationListId);

		if (cpConfigurationEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append(", CPConfigurationListId=");
			sb.append(CPConfigurationListId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCPConfigurationEntryException(sb.toString());
		}

		return cpConfigurationEntry;
	}

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId) {

		return fetchByC_C_C(classNameId, classPK, CPConfigurationListId, true);
	}

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					classNameId, classPK, CPConfigurationListId
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByC_C_C, finderArgs, this);
			}

			if (result instanceof CPConfigurationEntry) {
				CPConfigurationEntry cpConfigurationEntry =
					(CPConfigurationEntry)result;

				if ((classNameId != cpConfigurationEntry.getClassNameId()) ||
					(classPK != cpConfigurationEntry.getClassPK()) ||
					(CPConfigurationListId !=
						cpConfigurationEntry.getCPConfigurationListId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

				sb.append(_FINDER_COLUMN_C_C_C_CPCONFIGURATIONLISTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					queryPos.add(CPConfigurationListId);

					List<CPConfigurationEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByC_C_C, finderArgs, list);
						}
					}
					else {
						CPConfigurationEntry cpConfigurationEntry = list.get(0);

						result = cpConfigurationEntry;

						cacheResult(cpConfigurationEntry);
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
				return (CPConfigurationEntry)result;
			}
		}
	}

	/**
	 * Removes the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the cp configuration entry that was removed
	 */
	@Override
	public CPConfigurationEntry removeByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByC_C_C(
			classNameId, classPK, CPConfigurationListId);

		return remove(cpConfigurationEntry);
	}

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId) {

		CPConfigurationEntry cpConfigurationEntry = fetchByC_C_C(
			classNameId, classPK, CPConfigurationListId);

		if (cpConfigurationEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_C_C_CLASSNAMEID_2 =
		"cpConfigurationEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSPK_2 =
		"cpConfigurationEntry.classPK = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CPCONFIGURATIONLISTID_2 =
		"cpConfigurationEntry.CPConfigurationListId = ?";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (cpConfigurationEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCPConfigurationEntryException(sb.toString());
		}

		return cpConfigurationEntry;
	}

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, companyId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_C, finderArgs, this);
			}

			if (result instanceof CPConfigurationEntry) {
				CPConfigurationEntry cpConfigurationEntry =
					(CPConfigurationEntry)result;

				if (!Objects.equals(
						externalReferenceCode,
						cpConfigurationEntry.getExternalReferenceCode()) ||
					(companyId != cpConfigurationEntry.getCompanyId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_C_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(companyId);

					List<CPConfigurationEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_C, finderArgs, list);
						}
					}
					else {
						CPConfigurationEntry cpConfigurationEntry = list.get(0);

						result = cpConfigurationEntry;

						cacheResult(cpConfigurationEntry);
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
				return (CPConfigurationEntry)result;
			}
		}
	}

	/**
	 * Removes the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp configuration entry that was removed
	 */
	@Override
	public CPConfigurationEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpConfigurationEntry);
	}

	/**
	 * Returns the number of cp configuration entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CPConfigurationEntry cpConfigurationEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (cpConfigurationEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"cpConfigurationEntry.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(cpConfigurationEntry.externalReferenceCode IS NULL OR cpConfigurationEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"cpConfigurationEntry.companyId = ?";

	public CPConfigurationEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPConfigurationEntry.class);

		setModelImplClass(CPConfigurationEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CPConfigurationEntryTable.INSTANCE);
	}

	/**
	 * Caches the cp configuration entry in the entity cache if it is enabled.
	 *
	 * @param cpConfigurationEntry the cp configuration entry
	 */
	@Override
	public void cacheResult(CPConfigurationEntry cpConfigurationEntry) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpConfigurationEntry.getCtCollectionId())) {

			entityCache.putResult(
				CPConfigurationEntryImpl.class,
				cpConfigurationEntry.getPrimaryKey(), cpConfigurationEntry);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					cpConfigurationEntry.getUuid(),
					cpConfigurationEntry.getGroupId()
				},
				cpConfigurationEntry);

			finderCache.putResult(
				_finderPathFetchByC_C_C,
				new Object[] {
					cpConfigurationEntry.getClassNameId(),
					cpConfigurationEntry.getClassPK(),
					cpConfigurationEntry.getCPConfigurationListId()
				},
				cpConfigurationEntry);

			finderCache.putResult(
				_finderPathFetchByERC_C,
				new Object[] {
					cpConfigurationEntry.getExternalReferenceCode(),
					cpConfigurationEntry.getCompanyId()
				},
				cpConfigurationEntry);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cp configuration entries in the entity cache if it is enabled.
	 *
	 * @param cpConfigurationEntries the cp configuration entries
	 */
	@Override
	public void cacheResult(List<CPConfigurationEntry> cpConfigurationEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (cpConfigurationEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CPConfigurationEntry cpConfigurationEntry :
				cpConfigurationEntries) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						cpConfigurationEntry.getCtCollectionId())) {

				if (entityCache.getResult(
						CPConfigurationEntryImpl.class,
						cpConfigurationEntry.getPrimaryKey()) == null) {

					cacheResult(cpConfigurationEntry);
				}
			}
		}
	}

	/**
	 * Clears the cache for all cp configuration entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CPConfigurationEntryImpl.class);

		finderCache.clearCache(CPConfigurationEntryImpl.class);
	}

	/**
	 * Clears the cache for the cp configuration entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CPConfigurationEntry cpConfigurationEntry) {
		entityCache.removeResult(
			CPConfigurationEntryImpl.class, cpConfigurationEntry);
	}

	@Override
	public void clearCache(List<CPConfigurationEntry> cpConfigurationEntries) {
		for (CPConfigurationEntry cpConfigurationEntry :
				cpConfigurationEntries) {

			entityCache.removeResult(
				CPConfigurationEntryImpl.class, cpConfigurationEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CPConfigurationEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CPConfigurationEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CPConfigurationEntryModelImpl cpConfigurationEntryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpConfigurationEntryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				cpConfigurationEntryModelImpl.getUuid(),
				cpConfigurationEntryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, cpConfigurationEntryModelImpl);

			args = new Object[] {
				cpConfigurationEntryModelImpl.getClassNameId(),
				cpConfigurationEntryModelImpl.getClassPK(),
				cpConfigurationEntryModelImpl.getCPConfigurationListId()
			};

			finderCache.putResult(
				_finderPathFetchByC_C_C, args, cpConfigurationEntryModelImpl);

			args = new Object[] {
				cpConfigurationEntryModelImpl.getExternalReferenceCode(),
				cpConfigurationEntryModelImpl.getCompanyId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_C, args, cpConfigurationEntryModelImpl);
		}
	}

	/**
	 * Creates a new cp configuration entry with the primary key. Does not add the cp configuration entry to the database.
	 *
	 * @param CPConfigurationEntryId the primary key for the new cp configuration entry
	 * @return the new cp configuration entry
	 */
	@Override
	public CPConfigurationEntry create(long CPConfigurationEntryId) {
		CPConfigurationEntry cpConfigurationEntry =
			new CPConfigurationEntryImpl();

		cpConfigurationEntry.setNew(true);
		cpConfigurationEntry.setPrimaryKey(CPConfigurationEntryId);

		String uuid = PortalUUIDUtil.generate();

		cpConfigurationEntry.setUuid(uuid);

		cpConfigurationEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpConfigurationEntry;
	}

	/**
	 * Removes the cp configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry that was removed
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry remove(long CPConfigurationEntryId)
		throws NoSuchCPConfigurationEntryException {

		return remove((Serializable)CPConfigurationEntryId);
	}

	/**
	 * Removes the cp configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cp configuration entry
	 * @return the cp configuration entry that was removed
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry remove(Serializable primaryKey)
		throws NoSuchCPConfigurationEntryException {

		Session session = null;

		try {
			session = openSession();

			CPConfigurationEntry cpConfigurationEntry =
				(CPConfigurationEntry)session.get(
					CPConfigurationEntryImpl.class, primaryKey);

			if (cpConfigurationEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCPConfigurationEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(cpConfigurationEntry);
		}
		catch (NoSuchCPConfigurationEntryException noSuchEntityException) {
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
	protected CPConfigurationEntry removeImpl(
		CPConfigurationEntry cpConfigurationEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpConfigurationEntry)) {
				cpConfigurationEntry = (CPConfigurationEntry)session.get(
					CPConfigurationEntryImpl.class,
					cpConfigurationEntry.getPrimaryKeyObj());
			}

			if ((cpConfigurationEntry != null) &&
				ctPersistenceHelper.isRemove(cpConfigurationEntry)) {

				session.delete(cpConfigurationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpConfigurationEntry != null) {
			clearCache(cpConfigurationEntry);
		}

		return cpConfigurationEntry;
	}

	@Override
	public CPConfigurationEntry updateImpl(
		CPConfigurationEntry cpConfigurationEntry) {

		boolean isNew = cpConfigurationEntry.isNew();

		if (!(cpConfigurationEntry instanceof CPConfigurationEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpConfigurationEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpConfigurationEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpConfigurationEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPConfigurationEntry implementation " +
					cpConfigurationEntry.getClass());
		}

		CPConfigurationEntryModelImpl cpConfigurationEntryModelImpl =
			(CPConfigurationEntryModelImpl)cpConfigurationEntry;

		if (Validator.isNull(cpConfigurationEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpConfigurationEntry.setUuid(uuid);
		}

		if (Validator.isNull(cpConfigurationEntry.getExternalReferenceCode())) {
			cpConfigurationEntry.setExternalReferenceCode(
				cpConfigurationEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					cpConfigurationEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpConfigurationEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpConfigurationEntry.getCompanyId();

					long groupId = cpConfigurationEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpConfigurationEntry.getPrimaryKey();
					}

					try {
						cpConfigurationEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPConfigurationEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpConfigurationEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPConfigurationEntry ercCPConfigurationEntry = fetchByERC_C(
				cpConfigurationEntry.getExternalReferenceCode(),
				cpConfigurationEntry.getCompanyId());

			if (isNew) {
				if (ercCPConfigurationEntry != null) {
					throw new DuplicateCPConfigurationEntryExternalReferenceCodeException(
						"Duplicate cp configuration entry with external reference code " +
							cpConfigurationEntry.getExternalReferenceCode() +
								" and company " +
									cpConfigurationEntry.getCompanyId());
				}
			}
			else {
				if ((ercCPConfigurationEntry != null) &&
					(cpConfigurationEntry.getCPConfigurationEntryId() !=
						ercCPConfigurationEntry.getCPConfigurationEntryId())) {

					throw new DuplicateCPConfigurationEntryExternalReferenceCodeException(
						"Duplicate cp configuration entry with external reference code " +
							cpConfigurationEntry.getExternalReferenceCode() +
								" and company " +
									cpConfigurationEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpConfigurationEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpConfigurationEntry.setCreateDate(date);
			}
			else {
				cpConfigurationEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpConfigurationEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpConfigurationEntry.setModifiedDate(date);
			}
			else {
				cpConfigurationEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpConfigurationEntry)) {
				if (!isNew) {
					session.evict(
						CPConfigurationEntryImpl.class,
						cpConfigurationEntry.getPrimaryKeyObj());
				}

				session.save(cpConfigurationEntry);
			}
			else {
				cpConfigurationEntry = (CPConfigurationEntry)session.merge(
					cpConfigurationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CPConfigurationEntryImpl.class, cpConfigurationEntryModelImpl,
			false, true);

		cacheUniqueFindersCache(cpConfigurationEntryModelImpl);

		if (isNew) {
			cpConfigurationEntry.setNew(false);
		}

		cpConfigurationEntry.resetOriginalValues();

		return cpConfigurationEntry;
	}

	/**
	 * Returns the cp configuration entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp configuration entry
	 * @return the cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCPConfigurationEntryException {

		CPConfigurationEntry cpConfigurationEntry = fetchByPrimaryKey(
			primaryKey);

		if (cpConfigurationEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCPConfigurationEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return cpConfigurationEntry;
	}

	/**
	 * Returns the cp configuration entry with the primary key or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry findByPrimaryKey(long CPConfigurationEntryId)
		throws NoSuchCPConfigurationEntryException {

		return findByPrimaryKey((Serializable)CPConfigurationEntryId);
	}

	/**
	 * Returns the cp configuration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp configuration entry
	 * @return the cp configuration entry, or <code>null</code> if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				CPConfigurationEntry.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CPConfigurationEntry cpConfigurationEntry =
			(CPConfigurationEntry)entityCache.getResult(
				CPConfigurationEntryImpl.class, primaryKey);

		if (cpConfigurationEntry != null) {
			return cpConfigurationEntry;
		}

		Session session = null;

		try {
			session = openSession();

			cpConfigurationEntry = (CPConfigurationEntry)session.get(
				CPConfigurationEntryImpl.class, primaryKey);

			if (cpConfigurationEntry != null) {
				cacheResult(cpConfigurationEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return cpConfigurationEntry;
	}

	/**
	 * Returns the cp configuration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry, or <code>null</code> if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry fetchByPrimaryKey(long CPConfigurationEntryId) {
		return fetchByPrimaryKey((Serializable)CPConfigurationEntryId);
	}

	@Override
	public Map<Serializable, CPConfigurationEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(CPConfigurationEntry.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CPConfigurationEntry> map =
			new HashMap<Serializable, CPConfigurationEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CPConfigurationEntry cpConfigurationEntry = fetchByPrimaryKey(
				primaryKey);

			if (cpConfigurationEntry != null) {
				map.put(primaryKey, cpConfigurationEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CPConfigurationEntry.class, primaryKey)) {

				CPConfigurationEntry cpConfigurationEntry =
					(CPConfigurationEntry)entityCache.getResult(
						CPConfigurationEntryImpl.class, primaryKey);

				if (cpConfigurationEntry == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, cpConfigurationEntry);
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

			for (CPConfigurationEntry cpConfigurationEntry :
					(List<CPConfigurationEntry>)query.list()) {

				map.put(
					cpConfigurationEntry.getPrimaryKeyObj(),
					cpConfigurationEntry);

				cacheResult(cpConfigurationEntry);
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
	 * Returns all the cp configuration entries.
	 *
	 * @return the cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findAll(
		int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cp configuration entries
	 */
	@Override
	public List<CPConfigurationEntry> findAll(
		int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

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

			List<CPConfigurationEntry> list = null;

			if (useFinderCache) {
				list = (List<CPConfigurationEntry>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CPCONFIGURATIONENTRY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CPCONFIGURATIONENTRY;

					sql = sql.concat(
						CPConfigurationEntryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CPConfigurationEntry>)QueryUtil.list(
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
	}

	/**
	 * Removes all the cp configuration entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CPConfigurationEntry cpConfigurationEntry : findAll()) {
			remove(cpConfigurationEntry);
		}
	}

	/**
	 * Returns the number of cp configuration entries.
	 *
	 * @return the number of cp configuration entries
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPConfigurationEntry.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_CPCONFIGURATIONENTRY);

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
		return "CPConfigurationEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPCONFIGURATIONENTRY;
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
		return CPConfigurationEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPConfigurationEntry";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("CPConfigurationListId");
		ctMergeColumnNames.add("CPTaxCategoryId");
		ctMergeColumnNames.add("allowedOrderQuantities");
		ctMergeColumnNames.add("backOrders");
		ctMergeColumnNames.add("commerceAvailabilityEstimateId");
		ctMergeColumnNames.add("CPDefinitionInventoryEngine");
		ctMergeColumnNames.add("depth");
		ctMergeColumnNames.add("displayAvailability");
		ctMergeColumnNames.add("displayStockQuantity");
		ctMergeColumnNames.add("freeShipping");
		ctMergeColumnNames.add("height");
		ctMergeColumnNames.add("lowStockActivity");
		ctMergeColumnNames.add("maxOrderQuantity");
		ctMergeColumnNames.add("minOrderQuantity");
		ctMergeColumnNames.add("minStockQuantity");
		ctMergeColumnNames.add("multipleOrderQuantity");
		ctMergeColumnNames.add("purchasable");
		ctMergeColumnNames.add("shippable");
		ctMergeColumnNames.add("shippingExtraPrice");
		ctMergeColumnNames.add("shipSeparately");
		ctMergeColumnNames.add("taxExempt");
		ctMergeColumnNames.add("weight");
		ctMergeColumnNames.add("width");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPConfigurationEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"classNameId", "classPK", "CPConfigurationListId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp configuration entry persistence.
	 */
	@Activate
	public void activate() {
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

		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathFetchByUUID_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_finderPathWithPaginationFindByCPConfigurationListId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByCPConfigurationListId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPConfigurationListId"}, true);

		_finderPathWithoutPaginationFindByCPConfigurationListId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByCPConfigurationListId",
				new String[] {Long.class.getName()},
				new String[] {"CPConfigurationListId"}, true);

		_finderPathCountByCPConfigurationListId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByCPConfigurationListId", new String[] {Long.class.getName()},
			new String[] {"CPConfigurationListId"}, false);

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, false);

		_finderPathFetchByC_C_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"classNameId", "classPK", "CPConfigurationListId"},
			true);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CPConfigurationEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPConfigurationEntryUtil.setPersistence(null);

		entityCache.removeCache(CPConfigurationEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CPCONFIGURATIONENTRY =
		"SELECT cpConfigurationEntry FROM CPConfigurationEntry cpConfigurationEntry";

	private static final String _SQL_SELECT_CPCONFIGURATIONENTRY_WHERE =
		"SELECT cpConfigurationEntry FROM CPConfigurationEntry cpConfigurationEntry WHERE ";

	private static final String _SQL_COUNT_CPCONFIGURATIONENTRY =
		"SELECT COUNT(cpConfigurationEntry) FROM CPConfigurationEntry cpConfigurationEntry";

	private static final String _SQL_COUNT_CPCONFIGURATIONENTRY_WHERE =
		"SELECT COUNT(cpConfigurationEntry) FROM CPConfigurationEntry cpConfigurationEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"cpConfigurationEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CPConfigurationEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPConfigurationEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPConfigurationEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}