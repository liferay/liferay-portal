/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPInstanceExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceTable;
import com.liferay.commerce.product.model.impl.CPInstanceImpl;
import com.liferay.commerce.product.model.impl.CPInstanceModelImpl;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceUtil;
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
import com.liferay.portal.kernel.dao.orm.SQLQuery;
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
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
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

import java.sql.Timestamp;

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
 * The persistence implementation for the cp instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPInstancePersistence.class)
public class CPInstancePersistenceImpl
	extends BasePersistenceImpl<CPInstance> implements CPInstancePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPInstanceUtil</code> to access the cp instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPInstanceImpl.class.getName();

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
	 * Returns all the cp instances where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

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

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if (!uuid.equals(cpInstance.getUuid())) {
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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
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

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUuid_First(
			String uuid, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByUuid_First(uuid, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUuid_First(
		String uuid, OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUuid_Last(
			String uuid, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByUuid_Last(uuid, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUuid_Last(
		String uuid, OrderByComparator<CPInstance> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where uuid = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByUuid_PrevAndNext(
			long CPInstanceId, String uuid,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		uuid = Objects.toString(uuid, "");

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, cpInstance, uuid, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByUuid_PrevAndNext(
				session, cpInstance, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByUuid_PrevAndNext(
		Session session, CPInstance cpInstance, String uuid,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CPInstance cpInstance :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

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
		"cpInstance.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(cpInstance.uuid IS NULL OR cpInstance.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the cp instance where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUUID_G(String uuid, long groupId)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByUUID_G(uuid, groupId);

		if (cpInstance == null) {
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

			throw new NoSuchCPInstanceException(sb.toString());
		}

		return cpInstance;
	}

	/**
	 * Returns the cp instance where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the cp instance where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

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

			if (result instanceof CPInstance) {
				CPInstance cpInstance = (CPInstance)result;

				if (!Objects.equals(uuid, cpInstance.getUuid()) ||
					(groupId != cpInstance.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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

					List<CPInstance> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						CPInstance cpInstance = list.get(0);

						result = cpInstance;

						cacheResult(cpInstance);
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
				return (CPInstance)result;
			}
		}
	}

	/**
	 * Removes the cp instance where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByUUID_G(uuid, groupId);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		CPInstance cpInstance = fetchByUUID_G(uuid, groupId);

		if (cpInstance == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"cpInstance.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(cpInstance.uuid IS NULL OR cpInstance.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"cpInstance.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the cp instances where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

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

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if (!uuid.equals(cpInstance.getUuid()) ||
							(companyId != cpInstance.getCompanyId())) {

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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
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

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CPInstance> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByUuid_C_PrevAndNext(
			long CPInstanceId, String uuid, long companyId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		uuid = Objects.toString(uuid, "");

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, cpInstance, uuid, companyId, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByUuid_C_PrevAndNext(
				session, cpInstance, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByUuid_C_PrevAndNext(
		Session session, CPInstance cpInstance, String uuid, long companyId,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CPInstance cpInstance :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

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
		"cpInstance.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(cpInstance.uuid IS NULL OR cpInstance.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"cpInstance.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the cp instances where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

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
				finderArgs = new Object[] {
					groupId, start, end, orderByComparator
				};
			}

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if (groupId != cpInstance.getGroupId()) {
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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByGroupId_First(
			long groupId, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByGroupId_First(
			groupId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByGroupId_First(
		long groupId, OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByGroupId_Last(
			long groupId, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByGroupId_Last(groupId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByGroupId_Last(
		long groupId, OrderByComparator<CPInstance> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where groupId = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByGroupId_PrevAndNext(
			long CPInstanceId, long groupId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, cpInstance, groupId, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByGroupId_PrevAndNext(
				session, cpInstance, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByGroupId_PrevAndNext(
		Session session, CPInstance cpInstance, long groupId,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the cp instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CPInstanceImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CPInstanceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<CPInstance>)QueryUtil.list(
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
	 * Returns the cp instances before and after the current cp instance in the ordered set of cp instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] filterFindByGroupId_PrevAndNext(
			long CPInstanceId, long groupId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				CPInstanceId, groupId, orderByComparator);
		}

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, cpInstance, groupId, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, cpInstance, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance filterGetByGroupId_PrevAndNext(
		Session session, CPInstance cpInstance, long groupId,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CPInstanceImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CPInstanceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (CPInstance cpInstance :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

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
	}

	/**
	 * Returns the number of cp instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp instances that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CPInstance> cpInstances = findByGroupId(groupId);

			cpInstances = InlineSQLHelperUtil.filter(cpInstances, groupId);

			return cpInstances.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_CPINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPInstance.class.getName(),
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
		"cpInstance.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the cp instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

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

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if (companyId != cpInstance.getCompanyId()) {
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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCompanyId_First(
			long companyId, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCompanyId_First(
		long companyId, OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCompanyId_Last(
			long companyId, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCompanyId_Last(
		long companyId, OrderByComparator<CPInstance> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where companyId = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByCompanyId_PrevAndNext(
			long CPInstanceId, long companyId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, cpInstance, companyId, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByCompanyId_PrevAndNext(
				session, cpInstance, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByCompanyId_PrevAndNext(
		Session session, CPInstance cpInstance, long companyId,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CPInstance cpInstance :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

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
		"cpInstance.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCPDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByCPDefinitionId;
	private FinderPath _finderPathCountByCPDefinitionId;

	/**
	 * Returns all the cp instances where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPDefinitionId(long CPDefinitionId) {
		return findByCPDefinitionId(
			CPDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPDefinitionId(
		long CPDefinitionId, int start, int end) {

		return findByCPDefinitionId(CPDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCPDefinitionId;
					finderArgs = new Object[] {CPDefinitionId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCPDefinitionId;
				finderArgs = new Object[] {
					CPDefinitionId, start, end, orderByComparator
				};
			}

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if (CPDefinitionId != cpInstance.getCPDefinitionId()) {
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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPDefinitionId_First(
		long CPDefinitionId, OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByCPDefinitionId(
			CPDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPDefinitionId_Last(
			long CPDefinitionId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByCPDefinitionId_Last(
			CPDefinitionId, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPDefinitionId_Last(
		long CPDefinitionId, OrderByComparator<CPInstance> orderByComparator) {

		int count = countByCPDefinitionId(CPDefinitionId);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByCPDefinitionId(
			CPDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByCPDefinitionId_PrevAndNext(
			long CPInstanceId, long CPDefinitionId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByCPDefinitionId_PrevAndNext(
				session, cpInstance, CPDefinitionId, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByCPDefinitionId_PrevAndNext(
				session, cpInstance, CPDefinitionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByCPDefinitionId_PrevAndNext(
		Session session, CPInstance cpInstance, long CPDefinitionId,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		for (CPInstance cpInstance :
				findByCPDefinitionId(
					CPDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = _finderPathCountByCPDefinitionId;

			Object[] finderArgs = new Object[] {CPDefinitionId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

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

	private static final String _FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2 =
		"cpInstance.CPDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByCPInstanceUuid;
	private FinderPath _finderPathWithoutPaginationFindByCPInstanceUuid;
	private FinderPath _finderPathCountByCPInstanceUuid;

	/**
	 * Returns all the cp instances where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPInstanceUuid(String CPInstanceUuid) {
		return findByCPInstanceUuid(
			CPInstanceUuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end) {

		return findByCPInstanceUuid(CPInstanceUuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByCPInstanceUuid(
			CPInstanceUuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			CPInstanceUuid = Objects.toString(CPInstanceUuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCPInstanceUuid;
					finderArgs = new Object[] {CPInstanceUuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCPInstanceUuid;
				finderArgs = new Object[] {
					CPInstanceUuid, start, end, orderByComparator
				};
			}

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if (!CPInstanceUuid.equals(
								cpInstance.getCPInstanceUuid())) {

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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				boolean bindCPInstanceUuid = false;

				if (CPInstanceUuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_3);
				}
				else {
					bindCPInstanceUuid = true;

					sb.append(_FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindCPInstanceUuid) {
						queryPos.add(CPInstanceUuid);
					}

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPInstanceUuid_First(
			String CPInstanceUuid,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByCPInstanceUuid_First(
			CPInstanceUuid, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPInstanceUuid=");
		sb.append(CPInstanceUuid);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByCPInstanceUuid(
			CPInstanceUuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPInstanceUuid_Last(
			String CPInstanceUuid,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByCPInstanceUuid_Last(
			CPInstanceUuid, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPInstanceUuid=");
		sb.append(CPInstanceUuid);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPInstanceUuid_Last(
		String CPInstanceUuid,
		OrderByComparator<CPInstance> orderByComparator) {

		int count = countByCPInstanceUuid(CPInstanceUuid);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByCPInstanceUuid(
			CPInstanceUuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByCPInstanceUuid_PrevAndNext(
			long CPInstanceId, String CPInstanceUuid,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstanceUuid = Objects.toString(CPInstanceUuid, "");

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByCPInstanceUuid_PrevAndNext(
				session, cpInstance, CPInstanceUuid, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByCPInstanceUuid_PrevAndNext(
				session, cpInstance, CPInstanceUuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByCPInstanceUuid_PrevAndNext(
		Session session, CPInstance cpInstance, String CPInstanceUuid,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		boolean bindCPInstanceUuid = false;

		if (CPInstanceUuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_3);
		}
		else {
			bindCPInstanceUuid = true;

			sb.append(_FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_2);
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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindCPInstanceUuid) {
			queryPos.add(CPInstanceUuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	@Override
	public void removeByCPInstanceUuid(String CPInstanceUuid) {
		for (CPInstance cpInstance :
				findByCPInstanceUuid(
					CPInstanceUuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCPInstanceUuid(String CPInstanceUuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			CPInstanceUuid = Objects.toString(CPInstanceUuid, "");

			FinderPath finderPath = _finderPathCountByCPInstanceUuid;

			Object[] finderArgs = new Object[] {CPInstanceUuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				boolean bindCPInstanceUuid = false;

				if (CPInstanceUuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_3);
				}
				else {
					bindCPInstanceUuid = true;

					sb.append(_FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindCPInstanceUuid) {
						queryPos.add(CPInstanceUuid);
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

	private static final String _FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_2 =
		"cpInstance.CPInstanceUuid = ?";

	private static final String _FINDER_COLUMN_CPINSTANCEUUID_CPINSTANCEUUID_3 =
		"(cpInstance.CPInstanceUuid IS NULL OR cpInstance.CPInstanceUuid = '')";

	private FinderPath _finderPathWithPaginationFindByG_ST;
	private FinderPath _finderPathWithoutPaginationFindByG_ST;
	private FinderPath _finderPathCountByG_ST;

	/**
	 * Returns all the cp instances where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByG_ST(long groupId, int status) {
		return findByG_ST(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByG_ST(
		long groupId, int status, int start, int end) {

		return findByG_ST(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByG_ST(
		long groupId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByG_ST(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByG_ST(
		long groupId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_ST;
					finderArgs = new Object[] {groupId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_ST;
				finderArgs = new Object[] {
					groupId, status, start, end, orderByComparator
				};
			}

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if ((groupId != cpInstance.getGroupId()) ||
							(status != cpInstance.getStatus())) {

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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_G_ST_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_ST_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByG_ST_First(
			long groupId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByG_ST_First(
			groupId, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByG_ST_First(
		long groupId, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByG_ST(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByG_ST_Last(
			long groupId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByG_ST_Last(
			groupId, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByG_ST_Last(
		long groupId, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		int count = countByG_ST(groupId, status);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByG_ST(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByG_ST_PrevAndNext(
			long CPInstanceId, long groupId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByG_ST_PrevAndNext(
				session, cpInstance, groupId, status, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByG_ST_PrevAndNext(
				session, cpInstance, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByG_ST_PrevAndNext(
		Session session, CPInstance cpInstance, long groupId, int status,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_G_ST_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_ST_STATUS_2);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the cp instances that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByG_ST(long groupId, int status) {
		return filterFindByG_ST(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByG_ST(
		long groupId, int status, int start, int end) {

		return filterFindByG_ST(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByG_ST(
		long groupId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_ST(groupId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_ST(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_ST_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_ST_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CPInstanceImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CPInstanceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<CPInstance>)QueryUtil.list(
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
	 * Returns the cp instances before and after the current cp instance in the ordered set of cp instances that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] filterFindByG_ST_PrevAndNext(
			long CPInstanceId, long groupId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_ST_PrevAndNext(
				CPInstanceId, groupId, status, orderByComparator);
		}

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = filterGetByG_ST_PrevAndNext(
				session, cpInstance, groupId, status, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = filterGetByG_ST_PrevAndNext(
				session, cpInstance, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance filterGetByG_ST_PrevAndNext(
		Session session, CPInstance cpInstance, long groupId, int status,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_ST_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_ST_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPInstanceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CPInstanceImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CPInstanceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_ST(long groupId, int status) {
		for (CPInstance cpInstance :
				findByG_ST(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByG_ST(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = _finderPathCountByG_ST;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_G_ST_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_ST_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

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

	/**
	 * Returns the number of cp instances that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching cp instances that the user has permission to view
	 */
	@Override
	public int filterCountByG_ST(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_ST(groupId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CPInstance> cpInstances = findByG_ST(groupId, status);

			cpInstances = InlineSQLHelperUtil.filter(cpInstances, groupId);

			return cpInstances.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_CPINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_G_ST_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_ST_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

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

	private static final String _FINDER_COLUMN_G_ST_GROUPID_2 =
		"cpInstance.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_ST_STATUS_2 =
		"cpInstance.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the cp instances where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_S(long companyId, String sku) {
		return findByC_S(
			companyId, sku, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where companyId = &#63; and sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_S(
		long companyId, String sku, int start, int end) {

		return findByC_S(companyId, sku, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where companyId = &#63; and sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_S(
		long companyId, String sku, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByC_S(companyId, sku, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where companyId = &#63; and sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_S(
		long companyId, String sku, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			sku = Objects.toString(sku, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S;
					finderArgs = new Object[] {companyId, sku};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S;
				finderArgs = new Object[] {
					companyId, sku, start, end, orderByComparator
				};
			}

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if ((companyId != cpInstance.getCompanyId()) ||
							!sku.equals(cpInstance.getSku())) {

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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				boolean bindSku = false;

				if (sku.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_S_SKU_3);
				}
				else {
					bindSku = true;

					sb.append(_FINDER_COLUMN_C_S_SKU_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindSku) {
						queryPos.add(sku);
					}

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_S_First(
			long companyId, String sku,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByC_S_First(
			companyId, sku, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", sku=");
		sb.append(sku);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_S_First(
		long companyId, String sku,
		OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByC_S(
			companyId, sku, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_S_Last(
			long companyId, String sku,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByC_S_Last(
			companyId, sku, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", sku=");
		sb.append(sku);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_S_Last(
		long companyId, String sku,
		OrderByComparator<CPInstance> orderByComparator) {

		int count = countByC_S(companyId, sku);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByC_S(
			companyId, sku, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByC_S_PrevAndNext(
			long CPInstanceId, long companyId, String sku,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		sku = Objects.toString(sku, "");

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, cpInstance, companyId, sku, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByC_S_PrevAndNext(
				session, cpInstance, companyId, sku, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByC_S_PrevAndNext(
		Session session, CPInstance cpInstance, long companyId, String sku,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

		boolean bindSku = false;

		if (sku.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_S_SKU_3);
		}
		else {
			bindSku = true;

			sb.append(_FINDER_COLUMN_C_S_SKU_2);
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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindSku) {
			queryPos.add(sku);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where companyId = &#63; and sku = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 */
	@Override
	public void removeByC_S(long companyId, String sku) {
		for (CPInstance cpInstance :
				findByC_S(
					companyId, sku, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_S(long companyId, String sku) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			sku = Objects.toString(sku, "");

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, sku};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				boolean bindSku = false;

				if (sku.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_S_SKU_3);
				}
				else {
					bindSku = true;

					sb.append(_FINDER_COLUMN_C_S_SKU_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindSku) {
						queryPos.add(sku);
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

	private static final String _FINDER_COLUMN_C_S_COMPANYID_2 =
		"cpInstance.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_SKU_2 = "cpInstance.sku = ?";

	private static final String _FINDER_COLUMN_C_S_SKU_3 =
		"(cpInstance.sku IS NULL OR cpInstance.sku = '')";

	private FinderPath _finderPathFetchByC_C;

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and CPInstanceUuid = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_C(long CPDefinitionId, String CPInstanceUuid)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByC_C(CPDefinitionId, CPInstanceUuid);

		if (cpInstance == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("CPDefinitionId=");
			sb.append(CPDefinitionId);

			sb.append(", CPInstanceUuid=");
			sb.append(CPInstanceUuid);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCPInstanceException(sb.toString());
		}

		return cpInstance;
	}

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and CPInstanceUuid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_C(long CPDefinitionId, String CPInstanceUuid) {
		return fetchByC_C(CPDefinitionId, CPInstanceUuid, true);
	}

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and CPInstanceUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_C(
		long CPDefinitionId, String CPInstanceUuid, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			CPInstanceUuid = Objects.toString(CPInstanceUuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {CPDefinitionId, CPInstanceUuid};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByC_C, finderArgs, this);
			}

			if (result instanceof CPInstance) {
				CPInstance cpInstance = (CPInstance)result;

				if ((CPDefinitionId != cpInstance.getCPDefinitionId()) ||
					!Objects.equals(
						CPInstanceUuid, cpInstance.getCPInstanceUuid())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CPDEFINITIONID_2);

				boolean bindCPInstanceUuid = false;

				if (CPInstanceUuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_C_CPINSTANCEUUID_3);
				}
				else {
					bindCPInstanceUuid = true;

					sb.append(_FINDER_COLUMN_C_C_CPINSTANCEUUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					if (bindCPInstanceUuid) {
						queryPos.add(CPInstanceUuid);
					}

					List<CPInstance> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByC_C, finderArgs, list);
						}
					}
					else {
						CPInstance cpInstance = list.get(0);

						result = cpInstance;

						cacheResult(cpInstance);
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
				return (CPInstance)result;
			}
		}
	}

	/**
	 * Removes the cp instance where CPDefinitionId = &#63; and CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByC_C(long CPDefinitionId, String CPInstanceUuid)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByC_C(CPDefinitionId, CPInstanceUuid);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_C(long CPDefinitionId, String CPInstanceUuid) {
		CPInstance cpInstance = fetchByC_C(CPDefinitionId, CPInstanceUuid);

		if (cpInstance == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_C_CPDEFINITIONID_2 =
		"cpInstance.CPDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CPINSTANCEUUID_2 =
		"cpInstance.CPInstanceUuid = ?";

	private static final String _FINDER_COLUMN_C_C_CPINSTANCEUUID_3 =
		"(cpInstance.CPInstanceUuid IS NULL OR cpInstance.CPInstanceUuid = '')";

	private FinderPath _finderPathFetchByCPDI_S;

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and sku = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPDI_S(long CPDefinitionId, String sku)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByCPDI_S(CPDefinitionId, sku);

		if (cpInstance == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("CPDefinitionId=");
			sb.append(CPDefinitionId);

			sb.append(", sku=");
			sb.append(sku);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCPInstanceException(sb.toString());
		}

		return cpInstance;
	}

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and sku = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPDI_S(long CPDefinitionId, String sku) {
		return fetchByCPDI_S(CPDefinitionId, sku, true);
	}

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and sku = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPDI_S(
		long CPDefinitionId, String sku, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			sku = Objects.toString(sku, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {CPDefinitionId, sku};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByCPDI_S, finderArgs, this);
			}

			if (result instanceof CPInstance) {
				CPInstance cpInstance = (CPInstance)result;

				if ((CPDefinitionId != cpInstance.getCPDefinitionId()) ||
					!Objects.equals(sku, cpInstance.getSku())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_CPDI_S_CPDEFINITIONID_2);

				boolean bindSku = false;

				if (sku.isEmpty()) {
					sb.append(_FINDER_COLUMN_CPDI_S_SKU_3);
				}
				else {
					bindSku = true;

					sb.append(_FINDER_COLUMN_CPDI_S_SKU_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					if (bindSku) {
						queryPos.add(sku);
					}

					List<CPInstance> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByCPDI_S, finderArgs, list);
						}
					}
					else {
						CPInstance cpInstance = list.get(0);

						result = cpInstance;

						cacheResult(cpInstance);
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
				return (CPInstance)result;
			}
		}
	}

	/**
	 * Removes the cp instance where CPDefinitionId = &#63; and sku = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByCPDI_S(long CPDefinitionId, String sku)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByCPDI_S(CPDefinitionId, sku);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and sku = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCPDI_S(long CPDefinitionId, String sku) {
		CPInstance cpInstance = fetchByCPDI_S(CPDefinitionId, sku);

		if (cpInstance == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_CPDI_S_CPDEFINITIONID_2 =
		"cpInstance.CPDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_CPDI_S_SKU_2 =
		"cpInstance.sku = ?";

	private static final String _FINDER_COLUMN_CPDI_S_SKU_3 =
		"(cpInstance.sku IS NULL OR cpInstance.sku = '')";

	private FinderPath _finderPathWithPaginationFindByC_ST;
	private FinderPath _finderPathWithoutPaginationFindByC_ST;
	private FinderPath _finderPathCountByC_ST;

	/**
	 * Returns all the cp instances where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_ST(long CPDefinitionId, int status) {
		return findByC_ST(
			CPDefinitionId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_ST(
		long CPDefinitionId, int status, int start, int end) {

		return findByC_ST(CPDefinitionId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_ST(
		long CPDefinitionId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByC_ST(
			CPDefinitionId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_ST(
		long CPDefinitionId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_ST;
					finderArgs = new Object[] {CPDefinitionId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_ST;
				finderArgs = new Object[] {
					CPDefinitionId, status, start, end, orderByComparator
				};
			}

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if ((CPDefinitionId !=
								cpInstance.getCPDefinitionId()) ||
							(status != cpInstance.getStatus())) {

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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_C_ST_CPDEFINITIONID_2);

				sb.append(_FINDER_COLUMN_C_ST_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					queryPos.add(status);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_ST_First(
			long CPDefinitionId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByC_ST_First(
			CPDefinitionId, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_ST_First(
		long CPDefinitionId, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByC_ST(
			CPDefinitionId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_ST_Last(
			long CPDefinitionId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByC_ST_Last(
			CPDefinitionId, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_ST_Last(
		long CPDefinitionId, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		int count = countByC_ST(CPDefinitionId, status);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByC_ST(
			CPDefinitionId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByC_ST_PrevAndNext(
			long CPInstanceId, long CPDefinitionId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByC_ST_PrevAndNext(
				session, cpInstance, CPDefinitionId, status, orderByComparator,
				true);

			array[1] = cpInstance;

			array[2] = getByC_ST_PrevAndNext(
				session, cpInstance, CPDefinitionId, status, orderByComparator,
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

	protected CPInstance getByC_ST_PrevAndNext(
		Session session, CPInstance cpInstance, long CPDefinitionId, int status,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_C_ST_CPDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_C_ST_STATUS_2);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPDefinitionId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where CPDefinitionId = &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 */
	@Override
	public void removeByC_ST(long CPDefinitionId, int status) {
		for (CPInstance cpInstance :
				findByC_ST(
					CPDefinitionId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_ST(long CPDefinitionId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = _finderPathCountByC_ST;

			Object[] finderArgs = new Object[] {CPDefinitionId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_C_ST_CPDEFINITIONID_2);

				sb.append(_FINDER_COLUMN_C_ST_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					queryPos.add(status);

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

	private static final String _FINDER_COLUMN_C_ST_CPDEFINITIONID_2 =
		"cpInstance.CPDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_C_ST_STATUS_2 =
		"cpInstance.status = ?";

	private FinderPath _finderPathWithPaginationFindByLtD_S;
	private FinderPath _finderPathWithPaginationCountByLtD_S;

	/**
	 * Returns all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByLtD_S;
			finderArgs = new Object[] {
				_getTime(displayDate), status, start, end, orderByComparator
			};

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if ((displayDate.getTime() <= cpInstance.getDisplayDate(
							).getTime()) ||
							(status != cpInstance.getStatus())) {

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

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByLtD_S_First(
			displayDate, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByLtD_S(
			displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByLtD_S_Last(
			Date displayDate, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByLtD_S_Last(
			displayDate, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByLtD_S_Last(
		Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		int count = countByLtD_S(displayDate, status);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByLtD_S(
			displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByLtD_S_PrevAndNext(
			long CPInstanceId, Date displayDate, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByLtD_S_PrevAndNext(
				session, cpInstance, displayDate, status, orderByComparator,
				true);

			array[1] = cpInstance;

			array[2] = getByLtD_S_PrevAndNext(
				session, cpInstance, displayDate, status, orderByComparator,
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

	protected CPInstance getByLtD_S_PrevAndNext(
		Session session, CPInstance cpInstance, Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		for (CPInstance cpInstance :
				findByLtD_S(
					displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByLtD_S;

			Object[] finderArgs = new Object[] {_getTime(displayDate), status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

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

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_1 =
		"cpInstance.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_2 =
		"cpInstance.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_LTD_S_STATUS_2 =
		"cpInstance.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_LtD_S;
	private FinderPath _finderPathWithPaginationCountByC_LtD_S;

	/**
	 * Returns all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status) {

		return findByC_LtD_S(
			CPDefinitionId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status, int start, int end) {

		return findByC_LtD_S(
			CPDefinitionId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByC_LtD_S(
			CPDefinitionId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LtD_S;
			finderArgs = new Object[] {
				CPDefinitionId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if ((CPDefinitionId !=
								cpInstance.getCPDefinitionId()) ||
							(displayDate.getTime() <= cpInstance.getDisplayDate(
							).getTime()) ||
							(status != cpInstance.getStatus())) {

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
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_S_CPDEFINITIONID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_C_LTD_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_LtD_S_First(
			long CPDefinitionId, Date displayDate, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByC_LtD_S_First(
			CPDefinitionId, displayDate, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_LtD_S_First(
		long CPDefinitionId, Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByC_LtD_S(
			CPDefinitionId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_LtD_S_Last(
			long CPDefinitionId, Date displayDate, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByC_LtD_S_Last(
			CPDefinitionId, displayDate, status, orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_LtD_S_Last(
		long CPDefinitionId, Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		int count = countByC_LtD_S(CPDefinitionId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByC_LtD_S(
			CPDefinitionId, displayDate, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByC_LtD_S_PrevAndNext(
			long CPInstanceId, long CPDefinitionId, Date displayDate,
			int status, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByC_LtD_S_PrevAndNext(
				session, cpInstance, CPDefinitionId, displayDate, status,
				orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByC_LtD_S_PrevAndNext(
				session, cpInstance, CPDefinitionId, displayDate, status,
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

	protected CPInstance getByC_LtD_S_PrevAndNext(
		Session session, CPInstance cpInstance, long CPDefinitionId,
		Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_C_LTD_S_CPDEFINITIONID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_C_LTD_S_STATUS_2);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPDefinitionId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status) {

		for (CPInstance cpInstance :
				findByC_LtD_S(
					CPDefinitionId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_LtD_S;

			Object[] finderArgs = new Object[] {
				CPDefinitionId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_S_CPDEFINITIONID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_C_LTD_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

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

	private static final String _FINDER_COLUMN_C_LTD_S_CPDEFINITIONID_2 =
		"cpInstance.CPDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1 =
		"cpInstance.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2 =
		"cpInstance.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_C_LTD_S_STATUS_2 =
		"cpInstance.status = ?";

	private FinderPath _finderPathWithPaginationFindByR_R_S;
	private FinderPath _finderPathWithoutPaginationFindByR_R_S;
	private FinderPath _finderPathCountByR_R_S;

	/**
	 * Returns all the cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status) {

		return findByR_R_S(
			replacementCPInstanceUuid, replacementCProductId, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, int start, int end) {

		return findByR_R_S(
			replacementCPInstanceUuid, replacementCProductId, status, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByR_R_S(
			replacementCPInstanceUuid, replacementCProductId, status, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			replacementCPInstanceUuid = Objects.toString(
				replacementCPInstanceUuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByR_R_S;
					finderArgs = new Object[] {
						replacementCPInstanceUuid, replacementCProductId, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByR_R_S;
				finderArgs = new Object[] {
					replacementCPInstanceUuid, replacementCProductId, status,
					start, end, orderByComparator
				};
			}

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPInstance cpInstance : list) {
						if (!replacementCPInstanceUuid.equals(
								cpInstance.getReplacementCPInstanceUuid()) ||
							(replacementCProductId !=
								cpInstance.getReplacementCProductId()) ||
							(status != cpInstance.getStatus())) {

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
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

				boolean bindReplacementCPInstanceUuid = false;

				if (replacementCPInstanceUuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_3);
				}
				else {
					bindReplacementCPInstanceUuid = true;

					sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_2);
				}

				sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPRODUCTID_2);

				sb.append(_FINDER_COLUMN_R_R_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindReplacementCPInstanceUuid) {
						queryPos.add(replacementCPInstanceUuid);
					}

					queryPos.add(replacementCProductId);

					queryPos.add(status);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Returns the first cp instance in the ordered set where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByR_R_S_First(
			String replacementCPInstanceUuid, long replacementCProductId,
			int status, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByR_R_S_First(
			replacementCPInstanceUuid, replacementCProductId, status,
			orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("replacementCPInstanceUuid=");
		sb.append(replacementCPInstanceUuid);

		sb.append(", replacementCProductId=");
		sb.append(replacementCProductId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the first cp instance in the ordered set where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByR_R_S_First(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, OrderByComparator<CPInstance> orderByComparator) {

		List<CPInstance> list = findByR_R_S(
			replacementCPInstanceUuid, replacementCProductId, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp instance in the ordered set where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByR_R_S_Last(
			String replacementCPInstanceUuid, long replacementCProductId,
			int status, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByR_R_S_Last(
			replacementCPInstanceUuid, replacementCProductId, status,
			orderByComparator);

		if (cpInstance != null) {
			return cpInstance;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("replacementCPInstanceUuid=");
		sb.append(replacementCPInstanceUuid);

		sb.append(", replacementCProductId=");
		sb.append(replacementCProductId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCPInstanceException(sb.toString());
	}

	/**
	 * Returns the last cp instance in the ordered set where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByR_R_S_Last(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, OrderByComparator<CPInstance> orderByComparator) {

		int count = countByR_R_S(
			replacementCPInstanceUuid, replacementCProductId, status);

		if (count == 0) {
			return null;
		}

		List<CPInstance> list = findByR_R_S(
			replacementCPInstanceUuid, replacementCProductId, status, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp instances before and after the current cp instance in the ordered set where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param CPInstanceId the primary key of the current cp instance
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance[] findByR_R_S_PrevAndNext(
			long CPInstanceId, String replacementCPInstanceUuid,
			long replacementCProductId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		replacementCPInstanceUuid = Objects.toString(
			replacementCPInstanceUuid, "");

		CPInstance cpInstance = findByPrimaryKey(CPInstanceId);

		Session session = null;

		try {
			session = openSession();

			CPInstance[] array = new CPInstanceImpl[3];

			array[0] = getByR_R_S_PrevAndNext(
				session, cpInstance, replacementCPInstanceUuid,
				replacementCProductId, status, orderByComparator, true);

			array[1] = cpInstance;

			array[2] = getByR_R_S_PrevAndNext(
				session, cpInstance, replacementCPInstanceUuid,
				replacementCProductId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPInstance getByR_R_S_PrevAndNext(
		Session session, CPInstance cpInstance,
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, OrderByComparator<CPInstance> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

		boolean bindReplacementCPInstanceUuid = false;

		if (replacementCPInstanceUuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_3);
		}
		else {
			bindReplacementCPInstanceUuid = true;

			sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_2);
		}

		sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPRODUCTID_2);

		sb.append(_FINDER_COLUMN_R_R_S_STATUS_2);

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
			sb.append(CPInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindReplacementCPInstanceUuid) {
			queryPos.add(replacementCPInstanceUuid);
		}

		queryPos.add(replacementCProductId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(cpInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63; from the database.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 */
	@Override
	public void removeByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status) {

		for (CPInstance cpInstance :
				findByR_R_S(
					replacementCPInstanceUuid, replacementCProductId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			replacementCPInstanceUuid = Objects.toString(
				replacementCPInstanceUuid, "");

			FinderPath finderPath = _finderPathCountByR_R_S;

			Object[] finderArgs = new Object[] {
				replacementCPInstanceUuid, replacementCProductId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_CPINSTANCE_WHERE);

				boolean bindReplacementCPInstanceUuid = false;

				if (replacementCPInstanceUuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_3);
				}
				else {
					bindReplacementCPInstanceUuid = true;

					sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_2);
				}

				sb.append(_FINDER_COLUMN_R_R_S_REPLACEMENTCPRODUCTID_2);

				sb.append(_FINDER_COLUMN_R_R_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindReplacementCPInstanceUuid) {
						queryPos.add(replacementCPInstanceUuid);
					}

					queryPos.add(replacementCProductId);

					queryPos.add(status);

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
		_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_2 =
			"cpInstance.replacementCPInstanceUuid = ? AND ";

	private static final String
		_FINDER_COLUMN_R_R_S_REPLACEMENTCPINSTANCEUUID_3 =
			"(cpInstance.replacementCPInstanceUuid IS NULL OR cpInstance.replacementCPInstanceUuid = '') AND ";

	private static final String _FINDER_COLUMN_R_R_S_REPLACEMENTCPRODUCTID_2 =
		"cpInstance.replacementCProductId = ? AND ";

	private static final String _FINDER_COLUMN_R_R_S_STATUS_2 =
		"cpInstance.status = ?";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the cp instance where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByERC_C(externalReferenceCode, companyId);

		if (cpInstance == null) {
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

			throw new NoSuchCPInstanceException(sb.toString());
		}

		return cpInstance;
	}

	/**
	 * Returns the cp instance where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the cp instance where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

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

			if (result instanceof CPInstance) {
				CPInstance cpInstance = (CPInstance)result;

				if (!Objects.equals(
						externalReferenceCode,
						cpInstance.getExternalReferenceCode()) ||
					(companyId != cpInstance.getCompanyId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPINSTANCE_WHERE);

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

					List<CPInstance> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_C, finderArgs, list);
						}
					}
					else {
						CPInstance cpInstance = list.get(0);

						result = cpInstance;

						cacheResult(cpInstance);
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
				return (CPInstance)result;
			}
		}
	}

	/**
	 * Removes the cp instance where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByERC_C(externalReferenceCode, companyId);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CPInstance cpInstance = fetchByERC_C(externalReferenceCode, companyId);

		if (cpInstance == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"cpInstance.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(cpInstance.externalReferenceCode IS NULL OR cpInstance.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"cpInstance.companyId = ?";

	public CPInstancePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"deliverySubscriptionTypeSettings", "deliverySubTypeSettings");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPInstance.class);

		setModelImplClass(CPInstanceImpl.class);
		setModelPKClass(long.class);

		setTable(CPInstanceTable.INSTANCE);
	}

	/**
	 * Caches the cp instance in the entity cache if it is enabled.
	 *
	 * @param cpInstance the cp instance
	 */
	@Override
	public void cacheResult(CPInstance cpInstance) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpInstance.getCtCollectionId())) {

			entityCache.putResult(
				CPInstanceImpl.class, cpInstance.getPrimaryKey(), cpInstance);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {cpInstance.getUuid(), cpInstance.getGroupId()},
				cpInstance);

			finderCache.putResult(
				_finderPathFetchByC_C,
				new Object[] {
					cpInstance.getCPDefinitionId(),
					cpInstance.getCPInstanceUuid()
				},
				cpInstance);

			finderCache.putResult(
				_finderPathFetchByCPDI_S,
				new Object[] {
					cpInstance.getCPDefinitionId(), cpInstance.getSku()
				},
				cpInstance);

			finderCache.putResult(
				_finderPathFetchByERC_C,
				new Object[] {
					cpInstance.getExternalReferenceCode(),
					cpInstance.getCompanyId()
				},
				cpInstance);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cp instances in the entity cache if it is enabled.
	 *
	 * @param cpInstances the cp instances
	 */
	@Override
	public void cacheResult(List<CPInstance> cpInstances) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (cpInstances.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CPInstance cpInstance : cpInstances) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						cpInstance.getCtCollectionId())) {

				if (entityCache.getResult(
						CPInstanceImpl.class, cpInstance.getPrimaryKey()) ==
							null) {

					cacheResult(cpInstance);
				}
			}
		}
	}

	/**
	 * Clears the cache for all cp instances.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CPInstanceImpl.class);

		finderCache.clearCache(CPInstanceImpl.class);
	}

	/**
	 * Clears the cache for the cp instance.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CPInstance cpInstance) {
		entityCache.removeResult(CPInstanceImpl.class, cpInstance);
	}

	@Override
	public void clearCache(List<CPInstance> cpInstances) {
		for (CPInstance cpInstance : cpInstances) {
			entityCache.removeResult(CPInstanceImpl.class, cpInstance);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CPInstanceImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CPInstanceImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CPInstanceModelImpl cpInstanceModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpInstanceModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				cpInstanceModelImpl.getUuid(), cpInstanceModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, cpInstanceModelImpl);

			args = new Object[] {
				cpInstanceModelImpl.getCPDefinitionId(),
				cpInstanceModelImpl.getCPInstanceUuid()
			};

			finderCache.putResult(
				_finderPathFetchByC_C, args, cpInstanceModelImpl);

			args = new Object[] {
				cpInstanceModelImpl.getCPDefinitionId(),
				cpInstanceModelImpl.getSku()
			};

			finderCache.putResult(
				_finderPathFetchByCPDI_S, args, cpInstanceModelImpl);

			args = new Object[] {
				cpInstanceModelImpl.getExternalReferenceCode(),
				cpInstanceModelImpl.getCompanyId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_C, args, cpInstanceModelImpl);
		}
	}

	/**
	 * Creates a new cp instance with the primary key. Does not add the cp instance to the database.
	 *
	 * @param CPInstanceId the primary key for the new cp instance
	 * @return the new cp instance
	 */
	@Override
	public CPInstance create(long CPInstanceId) {
		CPInstance cpInstance = new CPInstanceImpl();

		cpInstance.setNew(true);
		cpInstance.setPrimaryKey(CPInstanceId);

		String uuid = PortalUUIDUtil.generate();

		cpInstance.setUuid(uuid);

		cpInstance.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpInstance;
	}

	/**
	 * Removes the cp instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance that was removed
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance remove(long CPInstanceId)
		throws NoSuchCPInstanceException {

		return remove((Serializable)CPInstanceId);
	}

	/**
	 * Removes the cp instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cp instance
	 * @return the cp instance that was removed
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance remove(Serializable primaryKey)
		throws NoSuchCPInstanceException {

		Session session = null;

		try {
			session = openSession();

			CPInstance cpInstance = (CPInstance)session.get(
				CPInstanceImpl.class, primaryKey);

			if (cpInstance == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCPInstanceException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(cpInstance);
		}
		catch (NoSuchCPInstanceException noSuchEntityException) {
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
	protected CPInstance removeImpl(CPInstance cpInstance) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpInstance)) {
				cpInstance = (CPInstance)session.get(
					CPInstanceImpl.class, cpInstance.getPrimaryKeyObj());
			}

			if ((cpInstance != null) &&
				ctPersistenceHelper.isRemove(cpInstance)) {

				session.delete(cpInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpInstance != null) {
			clearCache(cpInstance);
		}

		return cpInstance;
	}

	@Override
	public CPInstance updateImpl(CPInstance cpInstance) {
		boolean isNew = cpInstance.isNew();

		if (!(cpInstance instanceof CPInstanceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpInstance.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(cpInstance);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpInstance proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPInstance implementation " +
					cpInstance.getClass());
		}

		CPInstanceModelImpl cpInstanceModelImpl =
			(CPInstanceModelImpl)cpInstance;

		if (Validator.isNull(cpInstance.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpInstance.setUuid(uuid);
		}

		if (Validator.isNull(cpInstance.getExternalReferenceCode())) {
			cpInstance.setExternalReferenceCode(cpInstance.getUuid());
		}
		else {
			if (!Objects.equals(
					cpInstanceModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpInstance.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpInstance.getCompanyId();

					long groupId = cpInstance.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpInstance.getPrimaryKey();
					}

					try {
						cpInstance.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPInstance.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpInstance.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPInstance ercCPInstance = fetchByERC_C(
				cpInstance.getExternalReferenceCode(),
				cpInstance.getCompanyId());

			if (isNew) {
				if (ercCPInstance != null) {
					throw new DuplicateCPInstanceExternalReferenceCodeException(
						"Duplicate cp instance with external reference code " +
							cpInstance.getExternalReferenceCode() +
								" and company " + cpInstance.getCompanyId());
				}
			}
			else {
				if ((ercCPInstance != null) &&
					(cpInstance.getCPInstanceId() !=
						ercCPInstance.getCPInstanceId())) {

					throw new DuplicateCPInstanceExternalReferenceCodeException(
						"Duplicate cp instance with external reference code " +
							cpInstance.getExternalReferenceCode() +
								" and company " + cpInstance.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpInstance.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpInstance.setCreateDate(date);
			}
			else {
				cpInstance.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!cpInstanceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpInstance.setModifiedDate(date);
			}
			else {
				cpInstance.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpInstance)) {
				if (!isNew) {
					session.evict(
						CPInstanceImpl.class, cpInstance.getPrimaryKeyObj());
				}

				session.save(cpInstance);
			}
			else {
				cpInstance = (CPInstance)session.merge(cpInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CPInstanceImpl.class, cpInstanceModelImpl, false, true);

		cacheUniqueFindersCache(cpInstanceModelImpl);

		if (isNew) {
			cpInstance.setNew(false);
		}

		cpInstance.resetOriginalValues();

		return cpInstance;
	}

	/**
	 * Returns the cp instance with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp instance
	 * @return the cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = fetchByPrimaryKey(primaryKey);

		if (cpInstance == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCPInstanceException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return cpInstance;
	}

	/**
	 * Returns the cp instance with the primary key or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance findByPrimaryKey(long CPInstanceId)
		throws NoSuchCPInstanceException {

		return findByPrimaryKey((Serializable)CPInstanceId);
	}

	/**
	 * Returns the cp instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp instance
	 * @return the cp instance, or <code>null</code> if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				CPInstance.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CPInstance cpInstance = (CPInstance)entityCache.getResult(
			CPInstanceImpl.class, primaryKey);

		if (cpInstance != null) {
			return cpInstance;
		}

		Session session = null;

		try {
			session = openSession();

			cpInstance = (CPInstance)session.get(
				CPInstanceImpl.class, primaryKey);

			if (cpInstance != null) {
				cacheResult(cpInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return cpInstance;
	}

	/**
	 * Returns the cp instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance, or <code>null</code> if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance fetchByPrimaryKey(long CPInstanceId) {
		return fetchByPrimaryKey((Serializable)CPInstanceId);
	}

	@Override
	public Map<Serializable, CPInstance> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(CPInstance.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CPInstance> map =
			new HashMap<Serializable, CPInstance>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CPInstance cpInstance = fetchByPrimaryKey(primaryKey);

			if (cpInstance != null) {
				map.put(primaryKey, cpInstance);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CPInstance.class, primaryKey)) {

				CPInstance cpInstance = (CPInstance)entityCache.getResult(
					CPInstanceImpl.class, primaryKey);

				if (cpInstance == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, cpInstance);
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

			for (CPInstance cpInstance : (List<CPInstance>)query.list()) {
				map.put(cpInstance.getPrimaryKeyObj(), cpInstance);

				cacheResult(cpInstance);
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
	 * Returns all the cp instances.
	 *
	 * @return the cp instances
	 */
	@Override
	public List<CPInstance> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of cp instances
	 */
	@Override
	public List<CPInstance> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cp instances
	 */
	@Override
	public List<CPInstance> findAll(
		int start, int end, OrderByComparator<CPInstance> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cp instances
	 */
	@Override
	public List<CPInstance> findAll(
		int start, int end, OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

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

			List<CPInstance> list = null;

			if (useFinderCache) {
				list = (List<CPInstance>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CPINSTANCE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CPINSTANCE;

					sql = sql.concat(CPInstanceModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CPInstance>)QueryUtil.list(
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
	 * Removes all the cp instances from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CPInstance cpInstance : findAll()) {
			remove(cpInstance);
		}
	}

	/**
	 * Returns the number of cp instances.
	 *
	 * @return the number of cp instances
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPInstance.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_CPINSTANCE);

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
		return "CPInstanceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPINSTANCE;
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
		return CPInstanceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPInstance";
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
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("CPInstanceUuid");
		ctMergeColumnNames.add("sku");
		ctMergeColumnNames.add("gtin");
		ctMergeColumnNames.add("manufacturerPartNumber");
		ctMergeColumnNames.add("purchasable");
		ctMergeColumnNames.add("width");
		ctMergeColumnNames.add("height");
		ctMergeColumnNames.add("depth");
		ctMergeColumnNames.add("weight");
		ctMergeColumnNames.add("price");
		ctMergeColumnNames.add("promoPrice");
		ctMergeColumnNames.add("cost");
		ctMergeColumnNames.add("published");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("overrideSubscriptionInfo");
		ctMergeColumnNames.add("subscriptionEnabled");
		ctMergeColumnNames.add("subscriptionLength");
		ctMergeColumnNames.add("subscriptionType");
		ctMergeColumnNames.add("subscriptionTypeSettings");
		ctMergeColumnNames.add("maxSubscriptionCycles");
		ctMergeColumnNames.add("deliverySubscriptionEnabled");
		ctMergeColumnNames.add("deliverySubscriptionLength");
		ctMergeColumnNames.add("deliverySubscriptionType");
		ctMergeColumnNames.add("deliverySubTypeSettings");
		ctMergeColumnNames.add("deliveryMaxSubscriptionCycles");
		ctMergeColumnNames.add("unspsc");
		ctMergeColumnNames.add("discontinued");
		ctMergeColumnNames.add("discontinuedDate");
		ctMergeColumnNames.add("replacementCPInstanceUuid");
		ctMergeColumnNames.add("replacementCProductId");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("CPInstanceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionId", "CPInstanceUuid"});

		_uniqueIndexColumnNames.add(new String[] {"CPDefinitionId", "sku"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp instance persistence.
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

		_finderPathWithPaginationFindByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPDefinitionId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPDefinitionId"}, true);

		_finderPathWithoutPaginationFindByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPDefinitionId",
			new String[] {Long.class.getName()},
			new String[] {"CPDefinitionId"}, true);

		_finderPathCountByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPDefinitionId",
			new String[] {Long.class.getName()},
			new String[] {"CPDefinitionId"}, false);

		_finderPathWithPaginationFindByCPInstanceUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPInstanceUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPInstanceUuid"}, true);

		_finderPathWithoutPaginationFindByCPInstanceUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPInstanceUuid",
			new String[] {String.class.getName()},
			new String[] {"CPInstanceUuid"}, true);

		_finderPathCountByCPInstanceUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPInstanceUuid",
			new String[] {String.class.getName()},
			new String[] {"CPInstanceUuid"}, false);

		_finderPathWithPaginationFindByG_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ST",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "status"}, true);

		_finderPathWithoutPaginationFindByG_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ST",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, true);

		_finderPathCountByG_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ST",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, false);

		_finderPathWithPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "sku"}, true);

		_finderPathWithoutPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "sku"}, true);

		_finderPathCountByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "sku"}, false);

		_finderPathFetchByC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"CPDefinitionId", "CPInstanceUuid"}, true);

		_finderPathFetchByCPDI_S = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByCPDI_S",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"CPDefinitionId", "sku"}, true);

		_finderPathWithPaginationFindByC_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_ST",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"CPDefinitionId", "status"}, true);

		_finderPathWithoutPaginationFindByC_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_ST",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"CPDefinitionId", "status"}, true);

		_finderPathCountByC_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_ST",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"CPDefinitionId", "status"}, false);

		_finderPathWithPaginationFindByLtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
			new String[] {
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"displayDate", "status"}, true);

		_finderPathWithPaginationCountByLtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
			new String[] {Date.class.getName(), Integer.class.getName()},
			new String[] {"displayDate", "status"}, false);

		_finderPathWithPaginationFindByC_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPDefinitionId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByC_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"CPDefinitionId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByR_R_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_R_S",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"replacementCPInstanceUuid", "replacementCProductId", "status"
			},
			true);

		_finderPathWithoutPaginationFindByR_R_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_R_S",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"replacementCPInstanceUuid", "replacementCProductId", "status"
			},
			true);

		_finderPathCountByR_R_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_R_S",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"replacementCPInstanceUuid", "replacementCProductId", "status"
			},
			false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CPInstanceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPInstanceUtil.setPersistence(null);

		entityCache.removeCache(CPInstanceImpl.class.getName());
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

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_CPINSTANCE =
		"SELECT cpInstance FROM CPInstance cpInstance";

	private static final String _SQL_SELECT_CPINSTANCE_WHERE =
		"SELECT cpInstance FROM CPInstance cpInstance WHERE ";

	private static final String _SQL_COUNT_CPINSTANCE =
		"SELECT COUNT(cpInstance) FROM CPInstance cpInstance";

	private static final String _SQL_COUNT_CPINSTANCE_WHERE =
		"SELECT COUNT(cpInstance) FROM CPInstance cpInstance WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"cpInstance.CPInstanceId";

	private static final String _FILTER_SQL_SELECT_CPINSTANCE_WHERE =
		"SELECT DISTINCT {cpInstance.*} FROM CPInstance cpInstance WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CPInstance.*} FROM (SELECT DISTINCT cpInstance.CPInstanceId FROM CPInstance cpInstance WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CPINSTANCE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CPInstance ON TEMP_TABLE.CPInstanceId = CPInstance.CPInstanceId";

	private static final String _FILTER_SQL_COUNT_CPINSTANCE_WHERE =
		"SELECT COUNT(DISTINCT cpInstance.CPInstanceId) AS COUNT_VALUE FROM CPInstance cpInstance WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "cpInstance";

	private static final String _FILTER_ENTITY_TABLE = "CPInstance";

	private static final String _ORDER_BY_ENTITY_ALIAS = "cpInstance.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CPInstance.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CPInstance exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPInstance exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPInstancePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "deliverySubscriptionTypeSettings"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}