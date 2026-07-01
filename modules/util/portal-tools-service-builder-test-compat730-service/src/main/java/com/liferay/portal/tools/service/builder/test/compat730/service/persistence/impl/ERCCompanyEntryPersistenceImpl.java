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
import com.liferay.portal.tools.service.builder.test.compat730.exception.DuplicateERCCompanyEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchERCCompanyEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.ERCCompanyEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.ERCCompanyEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.ERCCompanyEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.ERCCompanyEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.ERCCompanyEntryUtil;

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
 * The persistence implementation for the erc company entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ERCCompanyEntryPersistenceImpl
	extends BasePersistenceImpl<ERCCompanyEntry>
	implements ERCCompanyEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ERCCompanyEntryUtil</code> to access the erc company entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ERCCompanyEntryImpl.class.getName();

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
	 * Returns all the erc company entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc company entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @return the range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc company entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc company entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator,
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

		List<ERCCompanyEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCCompanyEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ERCCompanyEntry ercCompanyEntry : list) {
					if (!uuid.equals(ercCompanyEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_ERCCOMPANYENTRY_WHERE);

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
				sb.append(ERCCompanyEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<ERCCompanyEntry>)QueryUtil.list(
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
	 * Returns the first erc company entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByUuid_First(
			String uuid, OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (ercCompanyEntry != null) {
			return ercCompanyEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchERCCompanyEntryException(sb.toString());
	}

	/**
	 * Returns the first erc company entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByUuid_First(
		String uuid, OrderByComparator<ERCCompanyEntry> orderByComparator) {

		List<ERCCompanyEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last erc company entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByUuid_Last(
			String uuid, OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = fetchByUuid_Last(
			uuid, orderByComparator);

		if (ercCompanyEntry != null) {
			return ercCompanyEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchERCCompanyEntryException(sb.toString());
	}

	/**
	 * Returns the last erc company entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByUuid_Last(
		String uuid, OrderByComparator<ERCCompanyEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ERCCompanyEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the erc company entries before and after the current erc company entry in the ordered set where uuid = &#63;.
	 *
	 * @param ercCompanyEntryId the primary key of the current erc company entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next erc company entry
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry[] findByUuid_PrevAndNext(
			long ercCompanyEntryId, String uuid,
			OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		uuid = Objects.toString(uuid, "");

		ERCCompanyEntry ercCompanyEntry = findByPrimaryKey(ercCompanyEntryId);

		Session session = null;

		try {
			session = openSession();

			ERCCompanyEntry[] array = new ERCCompanyEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, ercCompanyEntry, uuid, orderByComparator, true);

			array[1] = ercCompanyEntry;

			array[2] = getByUuid_PrevAndNext(
				session, ercCompanyEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ERCCompanyEntry getByUuid_PrevAndNext(
		Session session, ERCCompanyEntry ercCompanyEntry, String uuid,
		OrderByComparator<ERCCompanyEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_ERCCOMPANYENTRY_WHERE);

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
			sb.append(ERCCompanyEntryModelImpl.ORDER_BY_JPQL);
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
						ercCompanyEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ERCCompanyEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the erc company entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ERCCompanyEntry ercCompanyEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ercCompanyEntry);
		}
	}

	/**
	 * Returns the number of erc company entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching erc company entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_ERCCOMPANYENTRY_WHERE);

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
		"ercCompanyEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(ercCompanyEntry.uuid IS NULL OR ercCompanyEntry.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the erc company entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc company entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @return the range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc company entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc company entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator,
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

		List<ERCCompanyEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCCompanyEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ERCCompanyEntry ercCompanyEntry : list) {
					if (!uuid.equals(ercCompanyEntry.getUuid()) ||
						(companyId != ercCompanyEntry.getCompanyId())) {

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

			sb.append(_SQL_SELECT_ERCCOMPANYENTRY_WHERE);

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
				sb.append(ERCCompanyEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<ERCCompanyEntry>)QueryUtil.list(
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
	 * Returns the first erc company entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (ercCompanyEntry != null) {
			return ercCompanyEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchERCCompanyEntryException(sb.toString());
	}

	/**
	 * Returns the first erc company entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ERCCompanyEntry> orderByComparator) {

		List<ERCCompanyEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last erc company entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (ercCompanyEntry != null) {
			return ercCompanyEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchERCCompanyEntryException(sb.toString());
	}

	/**
	 * Returns the last erc company entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ERCCompanyEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ERCCompanyEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the erc company entries before and after the current erc company entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param ercCompanyEntryId the primary key of the current erc company entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next erc company entry
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry[] findByUuid_C_PrevAndNext(
			long ercCompanyEntryId, String uuid, long companyId,
			OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		uuid = Objects.toString(uuid, "");

		ERCCompanyEntry ercCompanyEntry = findByPrimaryKey(ercCompanyEntryId);

		Session session = null;

		try {
			session = openSession();

			ERCCompanyEntry[] array = new ERCCompanyEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, ercCompanyEntry, uuid, companyId, orderByComparator,
				true);

			array[1] = ercCompanyEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, ercCompanyEntry, uuid, companyId, orderByComparator,
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

	protected ERCCompanyEntry getByUuid_C_PrevAndNext(
		Session session, ERCCompanyEntry ercCompanyEntry, String uuid,
		long companyId, OrderByComparator<ERCCompanyEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_ERCCOMPANYENTRY_WHERE);

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
			sb.append(ERCCompanyEntryModelImpl.ORDER_BY_JPQL);
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
						ercCompanyEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ERCCompanyEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the erc company entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ERCCompanyEntry ercCompanyEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ercCompanyEntry);
		}
	}

	/**
	 * Returns the number of erc company entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching erc company entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCCOMPANYENTRY_WHERE);

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
		"ercCompanyEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(ercCompanyEntry.uuid IS NULL OR ercCompanyEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"ercCompanyEntry.companyId = ?";

	private FinderPath _finderPathFetchByC_ERC;
	private FinderPath _finderPathCountByC_ERC;

	/**
	 * Returns the erc company entry where companyId = &#63; and externalReferenceCode = &#63; or throws a <code>NoSuchERCCompanyEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByC_ERC(
			long companyId, String externalReferenceCode)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = fetchByC_ERC(
			companyId, externalReferenceCode);

		if (ercCompanyEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchERCCompanyEntryException(sb.toString());
		}

		return ercCompanyEntry;
	}

	/**
	 * Returns the erc company entry where companyId = &#63; and externalReferenceCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByC_ERC(
		long companyId, String externalReferenceCode) {

		return fetchByC_ERC(companyId, externalReferenceCode, true);
	}

	/**
	 * Returns the erc company entry where companyId = &#63; and externalReferenceCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param externalReferenceCode the external reference code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByC_ERC(
		long companyId, String externalReferenceCode, boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, externalReferenceCode};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_ERC, finderArgs, this);
		}

		if (result instanceof ERCCompanyEntry) {
			ERCCompanyEntry ercCompanyEntry = (ERCCompanyEntry)result;

			if ((companyId != ercCompanyEntry.getCompanyId()) ||
				!Objects.equals(
					externalReferenceCode,
					ercCompanyEntry.getExternalReferenceCode())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_ERCCOMPANYENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_ERC_COMPANYID_2);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_ERC_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_C_ERC_EXTERNALREFERENCECODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindExternalReferenceCode) {
					queryPos.add(externalReferenceCode);
				}

				List<ERCCompanyEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_ERC, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									companyId, externalReferenceCode
								};
							}

							_log.warn(
								"ERCCompanyEntryPersistenceImpl.fetchByC_ERC(long, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ERCCompanyEntry ercCompanyEntry = list.get(0);

					result = ercCompanyEntry;

					cacheResult(ercCompanyEntry);
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
			return (ERCCompanyEntry)result;
		}
	}

	/**
	 * Removes the erc company entry where companyId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param externalReferenceCode the external reference code
	 * @return the erc company entry that was removed
	 */
	@Override
	public ERCCompanyEntry removeByC_ERC(
			long companyId, String externalReferenceCode)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = findByC_ERC(
			companyId, externalReferenceCode);

		return remove(ercCompanyEntry);
	}

	/**
	 * Returns the number of erc company entries where companyId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching erc company entries
	 */
	@Override
	public int countByC_ERC(long companyId, String externalReferenceCode) {
		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		FinderPath finderPath = _finderPathCountByC_ERC;

		Object[] finderArgs = new Object[] {companyId, externalReferenceCode};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ERCCOMPANYENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_ERC_COMPANYID_2);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_ERC_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_C_ERC_EXTERNALREFERENCECODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_C_ERC_COMPANYID_2 =
		"ercCompanyEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_ERC_EXTERNALREFERENCECODE_2 =
		"ercCompanyEntry.externalReferenceCode = ?";

	private static final String _FINDER_COLUMN_C_ERC_EXTERNALREFERENCECODE_3 =
		"(ercCompanyEntry.externalReferenceCode IS NULL OR ercCompanyEntry.externalReferenceCode = '')";

	public ERCCompanyEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ERCCompanyEntry.class);

		setModelImplClass(ERCCompanyEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the erc company entry in the entity cache if it is enabled.
	 *
	 * @param ercCompanyEntry the erc company entry
	 */
	@Override
	public void cacheResult(ERCCompanyEntry ercCompanyEntry) {
		entityCache.putResult(
			ERCCompanyEntryImpl.class, ercCompanyEntry.getPrimaryKey(),
			ercCompanyEntry);

		finderCache.putResult(
			_finderPathFetchByC_ERC,
			new Object[] {
				ercCompanyEntry.getCompanyId(),
				ercCompanyEntry.getExternalReferenceCode()
			},
			ercCompanyEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the erc company entries in the entity cache if it is enabled.
	 *
	 * @param ercCompanyEntries the erc company entries
	 */
	@Override
	public void cacheResult(List<ERCCompanyEntry> ercCompanyEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ercCompanyEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ERCCompanyEntry ercCompanyEntry : ercCompanyEntries) {
			if (entityCache.getResult(
					ERCCompanyEntryImpl.class,
					ercCompanyEntry.getPrimaryKey()) == null) {

				cacheResult(ercCompanyEntry);
			}
		}
	}

	/**
	 * Clears the cache for all erc company entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ERCCompanyEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the erc company entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ERCCompanyEntry ercCompanyEntry) {
		entityCache.removeResult(ERCCompanyEntryImpl.class, ercCompanyEntry);
	}

	@Override
	public void clearCache(List<ERCCompanyEntry> ercCompanyEntries) {
		for (ERCCompanyEntry ercCompanyEntry : ercCompanyEntries) {
			entityCache.removeResult(
				ERCCompanyEntryImpl.class, ercCompanyEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ERCCompanyEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ERCCompanyEntryModelImpl ercCompanyEntryModelImpl) {

		Object[] args = new Object[] {
			ercCompanyEntryModelImpl.getCompanyId(),
			ercCompanyEntryModelImpl.getExternalReferenceCode()
		};

		finderCache.putResult(
			_finderPathCountByC_ERC, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByC_ERC, args, ercCompanyEntryModelImpl, false);
	}

	/**
	 * Creates a new erc company entry with the primary key. Does not add the erc company entry to the database.
	 *
	 * @param ercCompanyEntryId the primary key for the new erc company entry
	 * @return the new erc company entry
	 */
	@Override
	public ERCCompanyEntry create(long ercCompanyEntryId) {
		ERCCompanyEntry ercCompanyEntry = new ERCCompanyEntryImpl();

		ercCompanyEntry.setNew(true);
		ercCompanyEntry.setPrimaryKey(ercCompanyEntryId);

		String uuid = PortalUUIDUtil.generate();

		ercCompanyEntry.setUuid(uuid);

		ercCompanyEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ercCompanyEntry;
	}

	/**
	 * Removes the erc company entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ercCompanyEntryId the primary key of the erc company entry
	 * @return the erc company entry that was removed
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry remove(long ercCompanyEntryId)
		throws NoSuchERCCompanyEntryException {

		return remove((Serializable)ercCompanyEntryId);
	}

	/**
	 * Removes the erc company entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the erc company entry
	 * @return the erc company entry that was removed
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry remove(Serializable primaryKey)
		throws NoSuchERCCompanyEntryException {

		Session session = null;

		try {
			session = openSession();

			ERCCompanyEntry ercCompanyEntry = (ERCCompanyEntry)session.get(
				ERCCompanyEntryImpl.class, primaryKey);

			if (ercCompanyEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchERCCompanyEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ercCompanyEntry);
		}
		catch (NoSuchERCCompanyEntryException noSuchEntityException) {
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
	protected ERCCompanyEntry removeImpl(ERCCompanyEntry ercCompanyEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ercCompanyEntry)) {
				ercCompanyEntry = (ERCCompanyEntry)session.get(
					ERCCompanyEntryImpl.class,
					ercCompanyEntry.getPrimaryKeyObj());
			}

			if (ercCompanyEntry != null) {
				session.delete(ercCompanyEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ercCompanyEntry != null) {
			clearCache(ercCompanyEntry);
		}

		return ercCompanyEntry;
	}

	@Override
	public ERCCompanyEntry updateImpl(ERCCompanyEntry ercCompanyEntry) {
		boolean isNew = ercCompanyEntry.isNew();

		if (!(ercCompanyEntry instanceof ERCCompanyEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ercCompanyEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ercCompanyEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ercCompanyEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ERCCompanyEntry implementation " +
					ercCompanyEntry.getClass());
		}

		ERCCompanyEntryModelImpl ercCompanyEntryModelImpl =
			(ERCCompanyEntryModelImpl)ercCompanyEntry;

		if (Validator.isNull(ercCompanyEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ercCompanyEntry.setUuid(uuid);
		}

		if (Validator.isNull(ercCompanyEntry.getExternalReferenceCode())) {
			ercCompanyEntry.setExternalReferenceCode(ercCompanyEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					ercCompanyEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ercCompanyEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ercCompanyEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = ercCompanyEntry.getPrimaryKey();
					}

					try {
						ercCompanyEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ERCCompanyEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ercCompanyEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ERCCompanyEntry ercERCCompanyEntry = fetchByC_ERC(
				ercCompanyEntry.getCompanyId(),
				ercCompanyEntry.getExternalReferenceCode());

			if (isNew) {
				if (ercERCCompanyEntry != null) {
					throw new DuplicateERCCompanyEntryExternalReferenceCodeException(
						"Duplicate erc company entry with external reference code " +
							ercCompanyEntry.getExternalReferenceCode() +
								" and company " +
									ercCompanyEntry.getCompanyId());
				}
			}
			else {
				if ((ercERCCompanyEntry != null) &&
					(ercCompanyEntry.getErcCompanyEntryId() !=
						ercERCCompanyEntry.getErcCompanyEntryId())) {

					throw new DuplicateERCCompanyEntryExternalReferenceCodeException(
						"Duplicate erc company entry with external reference code " +
							ercCompanyEntry.getExternalReferenceCode() +
								" and company " +
									ercCompanyEntry.getCompanyId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ercCompanyEntry);
			}
			else {
				ercCompanyEntry = (ERCCompanyEntry)session.merge(
					ercCompanyEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ERCCompanyEntryImpl.class, ercCompanyEntryModelImpl, false, true);

		cacheUniqueFindersCache(ercCompanyEntryModelImpl);

		if (isNew) {
			ercCompanyEntry.setNew(false);
		}

		ercCompanyEntry.resetOriginalValues();

		return ercCompanyEntry;
	}

	/**
	 * Returns the erc company entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the erc company entry
	 * @return the erc company entry
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = fetchByPrimaryKey(primaryKey);

		if (ercCompanyEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchERCCompanyEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ercCompanyEntry;
	}

	/**
	 * Returns the erc company entry with the primary key or throws a <code>NoSuchERCCompanyEntryException</code> if it could not be found.
	 *
	 * @param ercCompanyEntryId the primary key of the erc company entry
	 * @return the erc company entry
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry findByPrimaryKey(long ercCompanyEntryId)
		throws NoSuchERCCompanyEntryException {

		return findByPrimaryKey((Serializable)ercCompanyEntryId);
	}

	/**
	 * Returns the erc company entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ercCompanyEntryId the primary key of the erc company entry
	 * @return the erc company entry, or <code>null</code> if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByPrimaryKey(long ercCompanyEntryId) {
		return fetchByPrimaryKey((Serializable)ercCompanyEntryId);
	}

	/**
	 * Returns all the erc company entries.
	 *
	 * @return the erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the erc company entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @return the range of erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the erc company entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findAll(
		int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the erc company entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findAll(
		int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator,
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

		List<ERCCompanyEntry> list = null;

		if (useFinderCache) {
			list = (List<ERCCompanyEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_ERCCOMPANYENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_ERCCOMPANYENTRY;

				sql = sql.concat(ERCCompanyEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ERCCompanyEntry>)QueryUtil.list(
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
	 * Removes all the erc company entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ERCCompanyEntry ercCompanyEntry : findAll()) {
			remove(ercCompanyEntry);
		}
	}

	/**
	 * Returns the number of erc company entries.
	 *
	 * @return the number of erc company entries
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
					"SELECT COUNT(ercCompanyEntry) FROM ERCCompanyEntry ercCompanyEntry");

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
		return "ercCompanyEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ERCCOMPANYENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ERCCompanyEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the erc company entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			ERCCompanyEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class,
			new ERCCompanyEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", ERCCompanyEntry.class.getName()));

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

		_finderPathFetchByC_ERC = _createFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_ERC",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "externalReferenceCode"}, true);

		_finderPathCountByC_ERC = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_ERC",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "externalReferenceCode"}, false);

		ERCCompanyEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ERCCompanyEntryUtil.setPersistence(null);

		entityCache.removeCache(ERCCompanyEntryImpl.class.getName());

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
		ERCCompanyEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ERCCOMPANYENTRY =
		"SELECT ercCompanyEntry FROM ERCCompanyEntry ercCompanyEntry";

	private static final String _SQL_SELECT_ERCCOMPANYENTRY_WHERE =
		"SELECT ercCompanyEntry FROM ERCCompanyEntry ercCompanyEntry WHERE ";

	private static final String _SQL_COUNT_ERCCOMPANYENTRY_WHERE =
		"SELECT COUNT(ercCompanyEntry) FROM ERCCompanyEntry ercCompanyEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ERCCompanyEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ERCCompanyEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ERCCompanyEntryPersistenceImpl.class);

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

	private static class ERCCompanyEntryModelArgumentsResolver
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

			ERCCompanyEntryModelImpl ercCompanyEntryModelImpl =
				(ERCCompanyEntryModelImpl)baseModel;

			long columnBitmask = ercCompanyEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(
					ercCompanyEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						ercCompanyEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(
					ercCompanyEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			ERCCompanyEntryModelImpl ercCompanyEntryModelImpl,
			String[] columnNames, boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						ercCompanyEntryModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = ercCompanyEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1716654418