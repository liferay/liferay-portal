/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceOrderExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderTable;
import com.liferay.commerce.model.impl.CommerceOrderImpl;
import com.liferay.commerce.model.impl.CommerceOrderModelImpl;
import com.liferay.commerce.service.persistence.CommerceOrderPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.petra.string.StringBundler;
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

import java.util.Date;
import java.util.HashMap;
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
 * The persistence implementation for the commerce order service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceOrderPersistence.class)
public class CommerceOrderPersistenceImpl
	extends BasePersistenceImpl<CommerceOrder>
	implements CommerceOrderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceOrderUtil</code> to access the commerce order persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceOrderImpl.class.getName();

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
	 * Returns all the commerce orders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
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

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if (!uuid.equals(commerceOrder.getUuid())) {
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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

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
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByUuid_First(
			String uuid, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByUuid_First(
			uuid, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUuid_First(
		String uuid, OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByUuid_Last(
			String uuid, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByUuid_Last(uuid, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUuid_Last(
		String uuid, OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByUuid_PrevAndNext(
			long commerceOrderId, String uuid,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		uuid = Objects.toString(uuid, "");

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, commerceOrder, uuid, orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByUuid_PrevAndNext(
				session, commerceOrder, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceOrder getByUuid_PrevAndNext(
		Session session, CommerceOrder commerceOrder, String uuid,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
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
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CommerceOrder commerceOrder :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

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
		"commerceOrder.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(commerceOrder.uuid IS NULL OR commerceOrder.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByUUID_G(String uuid, long groupId)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByUUID_G(uuid, groupId);

		if (commerceOrder == null) {
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

			throw new NoSuchOrderException(sb.toString());
		}

		return commerceOrder;
	}

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUUID_G(
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

		if (result instanceof CommerceOrder) {
			CommerceOrder commerceOrder = (CommerceOrder)result;

			if (!Objects.equals(uuid, commerceOrder.getUuid()) ||
				(groupId != commerceOrder.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

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

				List<CommerceOrder> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					CommerceOrder commerceOrder = list.get(0);

					result = commerceOrder;

					cacheResult(commerceOrder);
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
			return (CommerceOrder)result;
		}
	}

	/**
	 * Removes the commerce order where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order that was removed
	 */
	@Override
	public CommerceOrder removeByUUID_G(String uuid, long groupId)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByUUID_G(uuid, groupId);

		return remove(commerceOrder);
	}

	/**
	 * Returns the number of commerce orders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		CommerceOrder commerceOrder = fetchByUUID_G(uuid, groupId);

		if (commerceOrder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"commerceOrder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(commerceOrder.uuid IS NULL OR commerceOrder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"commerceOrder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
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

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if (!uuid.equals(commerceOrder.getUuid()) ||
						(companyId != commerceOrder.getCompanyId())) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

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
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByUuid_C_PrevAndNext(
			long commerceOrderId, String uuid, long companyId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		uuid = Objects.toString(uuid, "");

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, commerceOrder, uuid, companyId, orderByComparator,
				true);

			array[1] = commerceOrder;

			array[2] = getByUuid_C_PrevAndNext(
				session, commerceOrder, uuid, companyId, orderByComparator,
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

	protected CommerceOrder getByUuid_C_PrevAndNext(
		Session session, CommerceOrder commerceOrder, String uuid,
		long companyId, OrderByComparator<CommerceOrder> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
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
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CommerceOrder commerceOrder :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

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
		"commerceOrder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(commerceOrder.uuid IS NULL OR commerceOrder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"commerceOrder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the commerce orders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
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

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if (groupId != commerceOrder.getGroupId()) {
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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByGroupId_First(
			long groupId, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByGroupId_First(
			groupId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByGroupId_First(
		long groupId, OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByGroupId_Last(
			long groupId, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByGroupId_Last(
		long groupId, OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByGroupId_PrevAndNext(
			long commerceOrderId, long groupId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, commerceOrder, groupId, orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByGroupId_PrevAndNext(
				session, commerceOrder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceOrder getByGroupId_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
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
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the commerce orders before and after the current commerce order in the ordered set of commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] filterFindByGroupId_PrevAndNext(
			long commerceOrderId, long groupId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				commerceOrderId, groupId, orderByComparator);
		}

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, commerceOrder, groupId, orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, commerceOrder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceOrder filterGetByGroupId_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (CommerceOrder commerceOrder :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

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
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceOrder> commerceOrders = findByGroupId(groupId);

			commerceOrders = InlineSQLHelperUtil.filter(
				commerceOrders, groupId);

			return commerceOrders.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
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
		"commerceOrder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns all the commerce orders where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUserId(long userId, int start, int end) {
		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByUserId;
				finderArgs = new Object[] {userId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByUserId;
			finderArgs = new Object[] {userId, start, end, orderByComparator};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if (userId != commerceOrder.getUserId()) {
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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByUserId_First(
			long userId, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByUserId_First(
			userId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUserId_First(
		long userId, OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByUserId(
			userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByUserId_Last(
			long userId, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByUserId_Last(
			userId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByUserId_Last(
		long userId, OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByUserId(userId);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByUserId(
			userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where userId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByUserId_PrevAndNext(
			long commerceOrderId, long userId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByUserId_PrevAndNext(
				session, commerceOrder, userId, orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByUserId_PrevAndNext(
				session, commerceOrder, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceOrder getByUserId_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long userId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (CommerceOrder commerceOrder :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] {userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"commerceOrder.userId = ?";

	private FinderPath _finderPathWithPaginationFindByBillingAddressId;
	private FinderPath _finderPathWithoutPaginationFindByBillingAddressId;
	private FinderPath _finderPathCountByBillingAddressId;

	/**
	 * Returns all the commerce orders where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByBillingAddressId(long billingAddressId) {
		return findByBillingAddressId(
			billingAddressId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end) {

		return findByBillingAddressId(billingAddressId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByBillingAddressId(
			billingAddressId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByBillingAddressId;
				finderArgs = new Object[] {billingAddressId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByBillingAddressId;
			finderArgs = new Object[] {
				billingAddressId, start, end, orderByComparator
			};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if (billingAddressId !=
							commerceOrder.getBillingAddressId()) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_BILLINGADDRESSID_BILLINGADDRESSID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(billingAddressId);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByBillingAddressId_First(
			long billingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByBillingAddressId_First(
			billingAddressId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("billingAddressId=");
		sb.append(billingAddressId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByBillingAddressId_First(
		long billingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByBillingAddressId(
			billingAddressId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByBillingAddressId_Last(
			long billingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByBillingAddressId_Last(
			billingAddressId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("billingAddressId=");
		sb.append(billingAddressId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByBillingAddressId_Last(
		long billingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByBillingAddressId(billingAddressId);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByBillingAddressId(
			billingAddressId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByBillingAddressId_PrevAndNext(
			long commerceOrderId, long billingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByBillingAddressId_PrevAndNext(
				session, commerceOrder, billingAddressId, orderByComparator,
				true);

			array[1] = commerceOrder;

			array[2] = getByBillingAddressId_PrevAndNext(
				session, commerceOrder, billingAddressId, orderByComparator,
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

	protected CommerceOrder getByBillingAddressId_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long billingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_BILLINGADDRESSID_BILLINGADDRESSID_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(billingAddressId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where billingAddressId = &#63; from the database.
	 *
	 * @param billingAddressId the billing address ID
	 */
	@Override
	public void removeByBillingAddressId(long billingAddressId) {
		for (CommerceOrder commerceOrder :
				findByBillingAddressId(
					billingAddressId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByBillingAddressId(long billingAddressId) {
		FinderPath finderPath = _finderPathCountByBillingAddressId;

		Object[] finderArgs = new Object[] {billingAddressId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_BILLINGADDRESSID_BILLINGADDRESSID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(billingAddressId);

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

	private static final String
		_FINDER_COLUMN_BILLINGADDRESSID_BILLINGADDRESSID_2 =
			"commerceOrder.billingAddressId = ?";

	private FinderPath _finderPathWithPaginationFindByCommerceAccountId;
	private FinderPath _finderPathWithoutPaginationFindByCommerceAccountId;
	private FinderPath _finderPathCountByCommerceAccountId;

	/**
	 * Returns all the commerce orders where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByCommerceAccountId(long commerceAccountId) {
		return findByCommerceAccountId(
			commerceAccountId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end) {

		return findByCommerceAccountId(commerceAccountId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByCommerceAccountId(
			commerceAccountId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByCommerceAccountId;
				finderArgs = new Object[] {commerceAccountId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByCommerceAccountId;
			finderArgs = new Object[] {
				commerceAccountId, start, end, orderByComparator
			};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if (commerceAccountId !=
							commerceOrder.getCommerceAccountId()) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_COMMERCEACCOUNTID_COMMERCEACCOUNTID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(commerceAccountId);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByCommerceAccountId_First(
			long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByCommerceAccountId_First(
			commerceAccountId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByCommerceAccountId_First(
		long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByCommerceAccountId(
			commerceAccountId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByCommerceAccountId_Last(
			long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByCommerceAccountId_Last(
			commerceAccountId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByCommerceAccountId_Last(
		long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByCommerceAccountId(commerceAccountId);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByCommerceAccountId(
			commerceAccountId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByCommerceAccountId_PrevAndNext(
			long commerceOrderId, long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByCommerceAccountId_PrevAndNext(
				session, commerceOrder, commerceAccountId, orderByComparator,
				true);

			array[1] = commerceOrder;

			array[2] = getByCommerceAccountId_PrevAndNext(
				session, commerceOrder, commerceAccountId, orderByComparator,
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

	protected CommerceOrder getByCommerceAccountId_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_COMMERCEACCOUNTID_COMMERCEACCOUNTID_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(commerceAccountId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where commerceAccountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 */
	@Override
	public void removeByCommerceAccountId(long commerceAccountId) {
		for (CommerceOrder commerceOrder :
				findByCommerceAccountId(
					commerceAccountId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByCommerceAccountId(long commerceAccountId) {
		FinderPath finderPath = _finderPathCountByCommerceAccountId;

		Object[] finderArgs = new Object[] {commerceAccountId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_COMMERCEACCOUNTID_COMMERCEACCOUNTID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(commerceAccountId);

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

	private static final String
		_FINDER_COLUMN_COMMERCEACCOUNTID_COMMERCEACCOUNTID_2 =
			"commerceOrder.commerceAccountId = ?";

	private FinderPath _finderPathWithPaginationFindByShippingAddressId;
	private FinderPath _finderPathWithoutPaginationFindByShippingAddressId;
	private FinderPath _finderPathCountByShippingAddressId;

	/**
	 * Returns all the commerce orders where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByShippingAddressId(long shippingAddressId) {
		return findByShippingAddressId(
			shippingAddressId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end) {

		return findByShippingAddressId(shippingAddressId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByShippingAddressId(
			shippingAddressId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByShippingAddressId;
				finderArgs = new Object[] {shippingAddressId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByShippingAddressId;
			finderArgs = new Object[] {
				shippingAddressId, start, end, orderByComparator
			};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if (shippingAddressId !=
							commerceOrder.getShippingAddressId()) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_SHIPPINGADDRESSID_SHIPPINGADDRESSID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(shippingAddressId);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByShippingAddressId_First(
			long shippingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByShippingAddressId_First(
			shippingAddressId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("shippingAddressId=");
		sb.append(shippingAddressId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByShippingAddressId_First(
		long shippingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByShippingAddressId(
			shippingAddressId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByShippingAddressId_Last(
			long shippingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByShippingAddressId_Last(
			shippingAddressId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("shippingAddressId=");
		sb.append(shippingAddressId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByShippingAddressId_Last(
		long shippingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByShippingAddressId(shippingAddressId);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByShippingAddressId(
			shippingAddressId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByShippingAddressId_PrevAndNext(
			long commerceOrderId, long shippingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByShippingAddressId_PrevAndNext(
				session, commerceOrder, shippingAddressId, orderByComparator,
				true);

			array[1] = commerceOrder;

			array[2] = getByShippingAddressId_PrevAndNext(
				session, commerceOrder, shippingAddressId, orderByComparator,
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

	protected CommerceOrder getByShippingAddressId_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long shippingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_SHIPPINGADDRESSID_SHIPPINGADDRESSID_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(shippingAddressId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where shippingAddressId = &#63; from the database.
	 *
	 * @param shippingAddressId the shipping address ID
	 */
	@Override
	public void removeByShippingAddressId(long shippingAddressId) {
		for (CommerceOrder commerceOrder :
				findByShippingAddressId(
					shippingAddressId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByShippingAddressId(long shippingAddressId) {
		FinderPath finderPath = _finderPathCountByShippingAddressId;

		Object[] finderArgs = new Object[] {shippingAddressId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_SHIPPINGADDRESSID_SHIPPINGADDRESSID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(shippingAddressId);

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

	private static final String
		_FINDER_COLUMN_SHIPPINGADDRESSID_SHIPPINGADDRESSID_2 =
			"commerceOrder.shippingAddressId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C;
	private FinderPath _finderPathCountByG_C;

	/**
	 * Returns all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C(long groupId, long commerceAccountId) {
		return findByG_C(
			groupId, commerceAccountId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end) {

		return findByG_C(groupId, commerceAccountId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByG_C(
			groupId, commerceAccountId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_C;
				finderArgs = new Object[] {groupId, commerceAccountId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_C;
			finderArgs = new Object[] {
				groupId, commerceAccountId, start, end, orderByComparator
			};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if ((groupId != commerceOrder.getGroupId()) ||
						(commerceAccountId !=
							commerceOrder.getCommerceAccountId())) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_COMMERCEACCOUNTID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(commerceAccountId);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_C_First(
			long groupId, long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_C_First(
			groupId, commerceAccountId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_C_First(
		long groupId, long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByG_C(
			groupId, commerceAccountId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_C_Last(
			long groupId, long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_C_Last(
			groupId, commerceAccountId, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_C_Last(
		long groupId, long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByG_C(groupId, commerceAccountId);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByG_C(
			groupId, commerceAccountId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByG_C_PrevAndNext(
			long commerceOrderId, long groupId, long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByG_C_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByG_C_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId,
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

	protected CommerceOrder getByG_C_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_COMMERCEACCOUNTID_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(commerceAccountId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId) {

		return filterFindByG_C(
			groupId, commerceAccountId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId, int start, int end) {

		return filterFindByG_C(groupId, commerceAccountId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C(
				groupId, commerceAccountId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C(
					groupId, commerceAccountId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_COMMERCEACCOUNTID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(commerceAccountId);

			return (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the commerce orders before and after the current commerce order in the ordered set of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] filterFindByG_C_PrevAndNext(
			long commerceOrderId, long groupId, long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_PrevAndNext(
				commerceOrderId, groupId, commerceAccountId, orderByComparator);
		}

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = filterGetByG_C_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = filterGetByG_C_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId,
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

	protected CommerceOrder filterGetByG_C_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_COMMERCEACCOUNTID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(commerceAccountId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; and commerceAccountId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 */
	@Override
	public void removeByG_C(long groupId, long commerceAccountId) {
		for (CommerceOrder commerceOrder :
				findByG_C(
					groupId, commerceAccountId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByG_C(long groupId, long commerceAccountId) {
		FinderPath finderPath = _finderPathCountByG_C;

		Object[] finderArgs = new Object[] {groupId, commerceAccountId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_COMMERCEACCOUNTID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(commerceAccountId);

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
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long commerceAccountId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C(groupId, commerceAccountId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceOrder> commerceOrders = findByG_C(
				groupId, commerceAccountId);

			commerceOrders = InlineSQLHelperUtil.filter(
				commerceOrders, groupId);

			return commerceOrders.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_COMMERCEACCOUNTID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(commerceAccountId);

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

	private static final String _FINDER_COLUMN_G_C_GROUPID_2 =
		"commerceOrder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_COMMERCEACCOUNTID_2 =
		"commerceOrder.commerceAccountId = ?";

	private FinderPath _finderPathWithPaginationFindByG_CP;
	private FinderPath _finderPathWithoutPaginationFindByG_CP;
	private FinderPath _finderPathCountByG_CP;

	/**
	 * Returns all the commerce orders where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_CP(
		long groupId, String commercePaymentMethodKey) {

		return findByG_CP(
			groupId, commercePaymentMethodKey, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_CP(
		long groupId, String commercePaymentMethodKey, int start, int end) {

		return findByG_CP(groupId, commercePaymentMethodKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_CP(
		long groupId, String commercePaymentMethodKey, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByG_CP(
			groupId, commercePaymentMethodKey, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_CP(
		long groupId, String commercePaymentMethodKey, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		commercePaymentMethodKey = Objects.toString(
			commercePaymentMethodKey, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_CP;
				finderArgs = new Object[] {groupId, commercePaymentMethodKey};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_CP;
			finderArgs = new Object[] {
				groupId, commercePaymentMethodKey, start, end, orderByComparator
			};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if ((groupId != commerceOrder.getGroupId()) ||
						!commercePaymentMethodKey.equals(
							commerceOrder.getCommercePaymentMethodKey())) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_CP_GROUPID_2);

			boolean bindCommercePaymentMethodKey = false;

			if (commercePaymentMethodKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_3);
			}
			else {
				bindCommercePaymentMethodKey = true;

				sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindCommercePaymentMethodKey) {
					queryPos.add(commercePaymentMethodKey);
				}

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_CP_First(
			long groupId, String commercePaymentMethodKey,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_CP_First(
			groupId, commercePaymentMethodKey, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", commercePaymentMethodKey=");
		sb.append(commercePaymentMethodKey);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_CP_First(
		long groupId, String commercePaymentMethodKey,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByG_CP(
			groupId, commercePaymentMethodKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_CP_Last(
			long groupId, String commercePaymentMethodKey,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_CP_Last(
			groupId, commercePaymentMethodKey, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", commercePaymentMethodKey=");
		sb.append(commercePaymentMethodKey);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_CP_Last(
		long groupId, String commercePaymentMethodKey,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByG_CP(groupId, commercePaymentMethodKey);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByG_CP(
			groupId, commercePaymentMethodKey, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByG_CP_PrevAndNext(
			long commerceOrderId, long groupId, String commercePaymentMethodKey,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		commercePaymentMethodKey = Objects.toString(
			commercePaymentMethodKey, "");

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByG_CP_PrevAndNext(
				session, commerceOrder, groupId, commercePaymentMethodKey,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByG_CP_PrevAndNext(
				session, commerceOrder, groupId, commercePaymentMethodKey,
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

	protected CommerceOrder getByG_CP_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		String commercePaymentMethodKey,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_CP_GROUPID_2);

		boolean bindCommercePaymentMethodKey = false;

		if (commercePaymentMethodKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_3);
		}
		else {
			bindCommercePaymentMethodKey = true;

			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_2);
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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindCommercePaymentMethodKey) {
			queryPos.add(commercePaymentMethodKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @return the matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_CP(
		long groupId, String commercePaymentMethodKey) {

		return filterFindByG_CP(
			groupId, commercePaymentMethodKey, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_CP(
		long groupId, String commercePaymentMethodKey, int start, int end) {

		return filterFindByG_CP(
			groupId, commercePaymentMethodKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_CP(
		long groupId, String commercePaymentMethodKey, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_CP(
				groupId, commercePaymentMethodKey, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_CP(
					groupId, commercePaymentMethodKey, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		commercePaymentMethodKey = Objects.toString(
			commercePaymentMethodKey, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_CP_GROUPID_2);

		boolean bindCommercePaymentMethodKey = false;

		if (commercePaymentMethodKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_3);
		}
		else {
			bindCommercePaymentMethodKey = true;

			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindCommercePaymentMethodKey) {
				queryPos.add(commercePaymentMethodKey);
			}

			return (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the commerce orders before and after the current commerce order in the ordered set of commerce orders that the user has permission to view where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] filterFindByG_CP_PrevAndNext(
			long commerceOrderId, long groupId, String commercePaymentMethodKey,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_CP_PrevAndNext(
				commerceOrderId, groupId, commercePaymentMethodKey,
				orderByComparator);
		}

		commercePaymentMethodKey = Objects.toString(
			commercePaymentMethodKey, "");

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = filterGetByG_CP_PrevAndNext(
				session, commerceOrder, groupId, commercePaymentMethodKey,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = filterGetByG_CP_PrevAndNext(
				session, commerceOrder, groupId, commercePaymentMethodKey,
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

	protected CommerceOrder filterGetByG_CP_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		String commercePaymentMethodKey,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_CP_GROUPID_2);

		boolean bindCommercePaymentMethodKey = false;

		if (commercePaymentMethodKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_3);
		}
		else {
			bindCommercePaymentMethodKey = true;

			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindCommercePaymentMethodKey) {
			queryPos.add(commercePaymentMethodKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; and commercePaymentMethodKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 */
	@Override
	public void removeByG_CP(long groupId, String commercePaymentMethodKey) {
		for (CommerceOrder commerceOrder :
				findByG_CP(
					groupId, commercePaymentMethodKey, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByG_CP(long groupId, String commercePaymentMethodKey) {
		commercePaymentMethodKey = Objects.toString(
			commercePaymentMethodKey, "");

		FinderPath finderPath = _finderPathCountByG_CP;

		Object[] finderArgs = new Object[] {groupId, commercePaymentMethodKey};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_CP_GROUPID_2);

			boolean bindCommercePaymentMethodKey = false;

			if (commercePaymentMethodKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_3);
			}
			else {
				bindCommercePaymentMethodKey = true;

				sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindCommercePaymentMethodKey) {
					queryPos.add(commercePaymentMethodKey);
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

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and commercePaymentMethodKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commercePaymentMethodKey the commerce payment method key
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	@Override
	public int filterCountByG_CP(
		long groupId, String commercePaymentMethodKey) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_CP(groupId, commercePaymentMethodKey);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceOrder> commerceOrders = findByG_CP(
				groupId, commercePaymentMethodKey);

			commerceOrders = InlineSQLHelperUtil.filter(
				commerceOrders, groupId);

			return commerceOrders.size();
		}

		commercePaymentMethodKey = Objects.toString(
			commercePaymentMethodKey, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_CP_GROUPID_2);

		boolean bindCommercePaymentMethodKey = false;

		if (commercePaymentMethodKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_3);
		}
		else {
			bindCommercePaymentMethodKey = true;

			sb.append(_FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindCommercePaymentMethodKey) {
				queryPos.add(commercePaymentMethodKey);
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

	private static final String _FINDER_COLUMN_G_CP_GROUPID_2 =
		"commerceOrder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_2 =
		"commerceOrder.commercePaymentMethodKey = ?";

	private static final String _FINDER_COLUMN_G_CP_COMMERCEPAYMENTMETHODKEY_3 =
		"(commerceOrder.commercePaymentMethodKey IS NULL OR commerceOrder.commercePaymentMethodKey = '')";

	private FinderPath _finderPathWithPaginationFindByG_U_O;
	private FinderPath _finderPathWithoutPaginationFindByG_U_O;
	private FinderPath _finderPathCountByG_U_O;

	/**
	 * Returns all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus) {

		return findByG_U_O(
			groupId, userId, orderStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end) {

		return findByG_U_O(groupId, userId, orderStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByG_U_O(
			groupId, userId, orderStatus, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_U_O;
				finderArgs = new Object[] {groupId, userId, orderStatus};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_U_O;
			finderArgs = new Object[] {
				groupId, userId, orderStatus, start, end, orderByComparator
			};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if ((groupId != commerceOrder.getGroupId()) ||
						(userId != commerceOrder.getUserId()) ||
						(orderStatus != commerceOrder.getOrderStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_U_O_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_U_O_USERID_2);

			sb.append(_FINDER_COLUMN_G_U_O_ORDERSTATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(userId);

				queryPos.add(orderStatus);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_U_O_First(
			long groupId, long userId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_U_O_First(
			groupId, userId, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_U_O_First(
		long groupId, long userId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByG_U_O(
			groupId, userId, orderStatus, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_U_O_Last(
			long groupId, long userId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_U_O_Last(
			groupId, userId, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_U_O_Last(
		long groupId, long userId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByG_U_O(groupId, userId, orderStatus);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByG_U_O(
			groupId, userId, orderStatus, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByG_U_O_PrevAndNext(
			long commerceOrderId, long groupId, long userId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByG_U_O_PrevAndNext(
				session, commerceOrder, groupId, userId, orderStatus,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByG_U_O_PrevAndNext(
				session, commerceOrder, groupId, userId, orderStatus,
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

	protected CommerceOrder getByG_U_O_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId, long userId,
		int orderStatus, OrderByComparator<CommerceOrder> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_U_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_O_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_O_ORDERSTATUS_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(orderStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus) {

		return filterFindByG_U_O(
			groupId, userId, orderStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end) {

		return filterFindByG_U_O(
			groupId, userId, orderStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_O(
				groupId, userId, orderStatus, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_O(
					groupId, userId, orderStatus, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_O_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_O_ORDERSTATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(orderStatus);

			return (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the commerce orders before and after the current commerce order in the ordered set of commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] filterFindByG_U_O_PrevAndNext(
			long commerceOrderId, long groupId, long userId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_O_PrevAndNext(
				commerceOrderId, groupId, userId, orderStatus,
				orderByComparator);
		}

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = filterGetByG_U_O_PrevAndNext(
				session, commerceOrder, groupId, userId, orderStatus,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = filterGetByG_U_O_PrevAndNext(
				session, commerceOrder, groupId, userId, orderStatus,
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

	protected CommerceOrder filterGetByG_U_O_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId, long userId,
		int orderStatus, OrderByComparator<CommerceOrder> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_O_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_O_ORDERSTATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(orderStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 */
	@Override
	public void removeByG_U_O(long groupId, long userId, int orderStatus) {
		for (CommerceOrder commerceOrder :
				findByG_U_O(
					groupId, userId, orderStatus, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByG_U_O(long groupId, long userId, int orderStatus) {
		FinderPath finderPath = _finderPathCountByG_U_O;

		Object[] finderArgs = new Object[] {groupId, userId, orderStatus};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_U_O_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_U_O_USERID_2);

			sb.append(_FINDER_COLUMN_G_U_O_ORDERSTATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(userId);

				queryPos.add(orderStatus);

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
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_O(long groupId, long userId, int orderStatus) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_O(groupId, userId, orderStatus);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceOrder> commerceOrders = findByG_U_O(
				groupId, userId, orderStatus);

			commerceOrders = InlineSQLHelperUtil.filter(
				commerceOrders, groupId);

			return commerceOrders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_U_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_O_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_O_ORDERSTATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(orderStatus);

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

	private static final String _FINDER_COLUMN_G_U_O_GROUPID_2 =
		"commerceOrder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_O_USERID_2 =
		"commerceOrder.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_O_ORDERSTATUS_2 =
		"commerceOrder.orderStatus = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_O;
	private FinderPath _finderPathWithoutPaginationFindByG_C_O;
	private FinderPath _finderPathCountByG_C_O;

	/**
	 * Returns all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return findByG_C_O(
			groupId, commerceAccountId, orderStatus, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end) {

		return findByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator) {

		return findByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_C_O;
				finderArgs = new Object[] {
					groupId, commerceAccountId, orderStatus
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_C_O;
			finderArgs = new Object[] {
				groupId, commerceAccountId, orderStatus, start, end,
				orderByComparator
			};
		}

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if ((groupId != commerceOrder.getGroupId()) ||
						(commerceAccountId !=
							commerceOrder.getCommerceAccountId()) ||
						(orderStatus != commerceOrder.getOrderStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_O_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_O_COMMERCEACCOUNTID_2);

			sb.append(_FINDER_COLUMN_G_C_O_ORDERSTATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(commerceAccountId);

				queryPos.add(orderStatus);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_C_O_First(
			long groupId, long commerceAccountId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_C_O_First(
			groupId, commerceAccountId, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_C_O_First(
		long groupId, long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByG_C_O(
			groupId, commerceAccountId, orderStatus, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByG_C_O_Last(
			long groupId, long commerceAccountId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByG_C_O_Last(
			groupId, commerceAccountId, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByG_C_O_Last(
		long groupId, long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByG_C_O(groupId, commerceAccountId, orderStatus);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByG_C_O(
			groupId, commerceAccountId, orderStatus, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByG_C_O_PrevAndNext(
			long commerceOrderId, long groupId, long commerceAccountId,
			int orderStatus, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByG_C_O_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId, orderStatus,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByG_C_O_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId, orderStatus,
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

	protected CommerceOrder getByG_C_O_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_O_COMMERCEACCOUNTID_2);

		sb.append(_FINDER_COLUMN_G_C_O_ORDERSTATUS_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(commerceAccountId);

		queryPos.add(orderStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return filterFindByG_C_O(
			groupId, commerceAccountId, orderStatus, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end) {

		return filterFindByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	@Override
	public List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_O(
				groupId, commerceAccountId, orderStatus, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_O(
					groupId, commerceAccountId, orderStatus, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_O_COMMERCEACCOUNTID_2);

		sb.append(_FINDER_COLUMN_G_C_O_ORDERSTATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(commerceAccountId);

			queryPos.add(orderStatus);

			return (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the commerce orders before and after the current commerce order in the ordered set of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] filterFindByG_C_O_PrevAndNext(
			long commerceOrderId, long groupId, long commerceAccountId,
			int orderStatus, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_O_PrevAndNext(
				commerceOrderId, groupId, commerceAccountId, orderStatus,
				orderByComparator);
		}

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = filterGetByG_C_O_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId, orderStatus,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = filterGetByG_C_O_PrevAndNext(
				session, commerceOrder, groupId, commerceAccountId, orderStatus,
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

	protected CommerceOrder filterGetByG_C_O_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long groupId,
		long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEORDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_O_COMMERCEACCOUNTID_2);

		sb.append(_FINDER_COLUMN_G_C_O_ORDERSTATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CommerceOrderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CommerceOrderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(commerceAccountId);

		queryPos.add(orderStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 */
	@Override
	public void removeByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		for (CommerceOrder commerceOrder :
				findByG_C_O(
					groupId, commerceAccountId, orderStatus, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		FinderPath finderPath = _finderPathCountByG_C_O;

		Object[] finderArgs = new Object[] {
			groupId, commerceAccountId, orderStatus
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_O_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_O_COMMERCEACCOUNTID_2);

			sb.append(_FINDER_COLUMN_G_C_O_ORDERSTATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(commerceAccountId);

				queryPos.add(orderStatus);

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
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_O(groupId, commerceAccountId, orderStatus);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceOrder> commerceOrders = findByG_C_O(
				groupId, commerceAccountId, orderStatus);

			commerceOrders = InlineSQLHelperUtil.filter(
				commerceOrders, groupId);

			return commerceOrders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_O_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_O_COMMERCEACCOUNTID_2);

		sb.append(_FINDER_COLUMN_G_C_O_ORDERSTATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceOrder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(commerceAccountId);

			queryPos.add(orderStatus);

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

	private static final String _FINDER_COLUMN_G_C_O_GROUPID_2 =
		"commerceOrder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_O_COMMERCEACCOUNTID_2 =
		"commerceOrder.commerceAccountId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_O_ORDERSTATUS_2 =
		"commerceOrder.orderStatus = ?";

	private FinderPath _finderPathWithPaginationFindByU_LtC_O;
	private FinderPath _finderPathWithPaginationCountByU_LtC_O;

	/**
	 * Returns all the commerce orders where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByU_LtC_O(
		long userId, Date createDate, int orderStatus) {

		return findByU_LtC_O(
			userId, createDate, orderStatus, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByU_LtC_O(
		long userId, Date createDate, int orderStatus, int start, int end) {

		return findByU_LtC_O(userId, createDate, orderStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByU_LtC_O(
		long userId, Date createDate, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findByU_LtC_O(
			userId, createDate, orderStatus, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByU_LtC_O(
		long userId, Date createDate, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByU_LtC_O;
		finderArgs = new Object[] {
			userId, _getTime(createDate), orderStatus, start, end,
			orderByComparator
		};

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if ((userId != commerceOrder.getUserId()) ||
						(createDate.getTime() <= commerceOrder.getCreateDate(
						).getTime()) ||
						(orderStatus != commerceOrder.getOrderStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_U_LTC_O_USERID_2);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_U_LTC_O_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_U_LTC_O_CREATEDATE_2);
			}

			sb.append(_FINDER_COLUMN_U_LTC_O_ORDERSTATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				queryPos.add(orderStatus);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByU_LtC_O_First(
			long userId, Date createDate, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByU_LtC_O_First(
			userId, createDate, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", createDate<");
		sb.append(createDate);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByU_LtC_O_First(
		long userId, Date createDate, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByU_LtC_O(
			userId, createDate, orderStatus, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByU_LtC_O_Last(
			long userId, Date createDate, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByU_LtC_O_Last(
			userId, createDate, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", createDate<");
		sb.append(createDate);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByU_LtC_O_Last(
		long userId, Date createDate, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByU_LtC_O(userId, createDate, orderStatus);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByU_LtC_O(
			userId, createDate, orderStatus, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByU_LtC_O_PrevAndNext(
			long commerceOrderId, long userId, Date createDate, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByU_LtC_O_PrevAndNext(
				session, commerceOrder, userId, createDate, orderStatus,
				orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByU_LtC_O_PrevAndNext(
				session, commerceOrder, userId, createDate, orderStatus,
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

	protected CommerceOrder getByU_LtC_O_PrevAndNext(
		Session session, CommerceOrder commerceOrder, long userId,
		Date createDate, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		sb.append(_FINDER_COLUMN_U_LTC_O_USERID_2);

		boolean bindCreateDate = false;

		if (createDate == null) {
			sb.append(_FINDER_COLUMN_U_LTC_O_CREATEDATE_1);
		}
		else {
			bindCreateDate = true;

			sb.append(_FINDER_COLUMN_U_LTC_O_CREATEDATE_2);
		}

		sb.append(_FINDER_COLUMN_U_LTC_O_ORDERSTATUS_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (bindCreateDate) {
			queryPos.add(new Timestamp(createDate.getTime()));
		}

		queryPos.add(orderStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 */
	@Override
	public void removeByU_LtC_O(long userId, Date createDate, int orderStatus) {
		for (CommerceOrder commerceOrder :
				findByU_LtC_O(
					userId, createDate, orderStatus, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where userId = &#63; and createDate &lt; &#63; and orderStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByU_LtC_O(long userId, Date createDate, int orderStatus) {
		FinderPath finderPath = _finderPathWithPaginationCountByU_LtC_O;

		Object[] finderArgs = new Object[] {
			userId, _getTime(createDate), orderStatus
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			sb.append(_FINDER_COLUMN_U_LTC_O_USERID_2);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_U_LTC_O_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_U_LTC_O_CREATEDATE_2);
			}

			sb.append(_FINDER_COLUMN_U_LTC_O_ORDERSTATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				queryPos.add(orderStatus);

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

	private static final String _FINDER_COLUMN_U_LTC_O_USERID_2 =
		"commerceOrder.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_LTC_O_CREATEDATE_1 =
		"commerceOrder.createDate IS NULL AND ";

	private static final String _FINDER_COLUMN_U_LTC_O_CREATEDATE_2 =
		"commerceOrder.createDate < ? AND ";

	private static final String _FINDER_COLUMN_U_LTC_O_ORDERSTATUS_2 =
		"commerceOrder.orderStatus = ?";

	private FinderPath _finderPathWithPaginationFindByC_LtC_O;
	private FinderPath _finderPathWithPaginationCountByC_LtC_O;

	/**
	 * Returns all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus) {

		return findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end) {

		return findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator) {

		return findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	@Override
	public List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByC_LtC_O;
		finderArgs = new Object[] {
			_getTime(createDate), commerceAccountId, orderStatus, start, end,
			orderByComparator
		};

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceOrder commerceOrder : list) {
					if ((createDate.getTime() <= commerceOrder.getCreateDate(
						).getTime()) ||
						(commerceAccountId !=
							commerceOrder.getCommerceAccountId()) ||
						(orderStatus != commerceOrder.getOrderStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_C_LTC_O_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_C_LTC_O_CREATEDATE_2);
			}

			sb.append(_FINDER_COLUMN_C_LTC_O_COMMERCEACCOUNTID_2);

			sb.append(_FINDER_COLUMN_C_LTC_O_ORDERSTATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				queryPos.add(commerceAccountId);

				queryPos.add(orderStatus);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Returns the first commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByC_LtC_O_First(
			Date createDate, long commerceAccountId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByC_LtC_O_First(
			createDate, commerceAccountId, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("createDate<");
		sb.append(createDate);

		sb.append(", commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the first commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByC_LtC_O_First(
		Date createDate, long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		List<CommerceOrder> list = findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByC_LtC_O_Last(
			Date createDate, long commerceAccountId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByC_LtC_O_Last(
			createDate, commerceAccountId, orderStatus, orderByComparator);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("createDate<");
		sb.append(createDate);

		sb.append(", commerceAccountId=");
		sb.append(commerceAccountId);

		sb.append(", orderStatus=");
		sb.append(orderStatus);

		sb.append("}");

		throw new NoSuchOrderException(sb.toString());
	}

	/**
	 * Returns the last commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByC_LtC_O_Last(
		Date createDate, long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		int count = countByC_LtC_O(createDate, commerceAccountId, orderStatus);

		if (count == 0) {
			return null;
		}

		List<CommerceOrder> list = findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce orders before and after the current commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param commerceOrderId the primary key of the current commerce order
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder[] findByC_LtC_O_PrevAndNext(
			long commerceOrderId, Date createDate, long commerceAccountId,
			int orderStatus, OrderByComparator<CommerceOrder> orderByComparator)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByPrimaryKey(commerceOrderId);

		Session session = null;

		try {
			session = openSession();

			CommerceOrder[] array = new CommerceOrderImpl[3];

			array[0] = getByC_LtC_O_PrevAndNext(
				session, commerceOrder, createDate, commerceAccountId,
				orderStatus, orderByComparator, true);

			array[1] = commerceOrder;

			array[2] = getByC_LtC_O_PrevAndNext(
				session, commerceOrder, createDate, commerceAccountId,
				orderStatus, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceOrder getByC_LtC_O_PrevAndNext(
		Session session, CommerceOrder commerceOrder, Date createDate,
		long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

		boolean bindCreateDate = false;

		if (createDate == null) {
			sb.append(_FINDER_COLUMN_C_LTC_O_CREATEDATE_1);
		}
		else {
			bindCreateDate = true;

			sb.append(_FINDER_COLUMN_C_LTC_O_CREATEDATE_2);
		}

		sb.append(_FINDER_COLUMN_C_LTC_O_COMMERCEACCOUNTID_2);

		sb.append(_FINDER_COLUMN_C_LTC_O_ORDERSTATUS_2);

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
			sb.append(CommerceOrderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindCreateDate) {
			queryPos.add(new Timestamp(createDate.getTime()));
		}

		queryPos.add(commerceAccountId);

		queryPos.add(orderStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceOrder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceOrder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 */
	@Override
	public void removeByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus) {

		for (CommerceOrder commerceOrder :
				findByC_LtC_O(
					createDate, commerceAccountId, orderStatus,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus) {

		FinderPath finderPath = _finderPathWithPaginationCountByC_LtC_O;

		Object[] finderArgs = new Object[] {
			_getTime(createDate), commerceAccountId, orderStatus
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCEORDER_WHERE);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_C_LTC_O_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_C_LTC_O_CREATEDATE_2);
			}

			sb.append(_FINDER_COLUMN_C_LTC_O_COMMERCEACCOUNTID_2);

			sb.append(_FINDER_COLUMN_C_LTC_O_ORDERSTATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				queryPos.add(commerceAccountId);

				queryPos.add(orderStatus);

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

	private static final String _FINDER_COLUMN_C_LTC_O_CREATEDATE_1 =
		"commerceOrder.createDate IS NULL AND ";

	private static final String _FINDER_COLUMN_C_LTC_O_CREATEDATE_2 =
		"commerceOrder.createDate < ? AND ";

	private static final String _FINDER_COLUMN_C_LTC_O_COMMERCEACCOUNTID_2 =
		"commerceOrder.commerceAccountId = ? AND ";

	private static final String _FINDER_COLUMN_C_LTC_O_ORDERSTATUS_2 =
		"commerceOrder.orderStatus = ?";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceOrder == null) {
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

			throw new NoSuchOrderException(sb.toString());
		}

		return commerceOrder;
	}

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public CommerceOrder fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

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

		if (result instanceof CommerceOrder) {
			CommerceOrder commerceOrder = (CommerceOrder)result;

			if (!Objects.equals(
					externalReferenceCode,
					commerceOrder.getExternalReferenceCode()) ||
				(companyId != commerceOrder.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCEORDER_WHERE);

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

				List<CommerceOrder> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					CommerceOrder commerceOrder = list.get(0);

					result = commerceOrder;

					cacheResult(commerceOrder);
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
			return (CommerceOrder)result;
		}
	}

	/**
	 * Removes the commerce order where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order that was removed
	 */
	@Override
	public CommerceOrder removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceOrder);
	}

	/**
	 * Returns the number of commerce orders where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce orders
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CommerceOrder commerceOrder = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceOrder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"commerceOrder.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(commerceOrder.externalReferenceCode IS NULL OR commerceOrder.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"commerceOrder.companyId = ?";

	public CommerceOrderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"deliveryCommerceTermEntryDescription",
			"deliveryCTermEntryDescription");
		dbColumnNames.put(
			"paymentCommerceTermEntryDescription",
			"paymentCTermEntryDescription");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel1",
			"shippingDiscountPercentLevel1");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel2",
			"shippingDiscountPercentLevel2");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel3",
			"shippingDiscountPercentLevel3");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel4",
			"shippingDiscountPercentLevel4");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel1WithTaxAmount",
			"shippingDiscountPctLev1WithTax");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel2WithTaxAmount",
			"shippingDiscountPctLev2WithTax");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel3WithTaxAmount",
			"shippingDiscountPctLev3WithTax");
		dbColumnNames.put(
			"shippingDiscountPercentageLevel4WithTaxAmount",
			"shippingDiscountPctLev4WithTax");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel1",
			"subtotalDiscountPercentLevel1");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel2",
			"subtotalDiscountPercentLevel2");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel3",
			"subtotalDiscountPercentLevel3");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel4",
			"subtotalDiscountPercentLevel4");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel1WithTaxAmount",
			"subtotalDiscountPctLev1WithTax");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel2WithTaxAmount",
			"subtotalDiscountPctLev2WithTax");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel3WithTaxAmount",
			"subtotalDiscountPctLev3WithTax");
		dbColumnNames.put(
			"subtotalDiscountPercentageLevel4WithTaxAmount",
			"subtotalDiscountPctLev4WithTax");
		dbColumnNames.put(
			"totalDiscountPercentageLevel1WithTaxAmount",
			"totalDiscountPctLev1WithTax");
		dbColumnNames.put(
			"totalDiscountPercentageLevel2WithTaxAmount",
			"totalDiscountPctLev2WithTax");
		dbColumnNames.put(
			"totalDiscountPercentageLevel3WithTaxAmount",
			"totalDiscountPctLev3WithTax");
		dbColumnNames.put(
			"totalDiscountPercentageLevel4WithTaxAmount",
			"totalDiscountPctLev4WithTax");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceOrder.class);

		setModelImplClass(CommerceOrderImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceOrderTable.INSTANCE);
	}

	/**
	 * Caches the commerce order in the entity cache if it is enabled.
	 *
	 * @param commerceOrder the commerce order
	 */
	@Override
	public void cacheResult(CommerceOrder commerceOrder) {
		entityCache.putResult(
			CommerceOrderImpl.class, commerceOrder.getPrimaryKey(),
			commerceOrder);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {commerceOrder.getUuid(), commerceOrder.getGroupId()},
			commerceOrder);

		finderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				commerceOrder.getExternalReferenceCode(),
				commerceOrder.getCompanyId()
			},
			commerceOrder);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce orders in the entity cache if it is enabled.
	 *
	 * @param commerceOrders the commerce orders
	 */
	@Override
	public void cacheResult(List<CommerceOrder> commerceOrders) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commerceOrders.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommerceOrder commerceOrder : commerceOrders) {
			if (entityCache.getResult(
					CommerceOrderImpl.class, commerceOrder.getPrimaryKey()) ==
						null) {

				cacheResult(commerceOrder);
			}
		}
	}

	/**
	 * Clears the cache for all commerce orders.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommerceOrderImpl.class);

		finderCache.clearCache(CommerceOrderImpl.class);
	}

	/**
	 * Clears the cache for the commerce order.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CommerceOrder commerceOrder) {
		entityCache.removeResult(CommerceOrderImpl.class, commerceOrder);
	}

	@Override
	public void clearCache(List<CommerceOrder> commerceOrders) {
		for (CommerceOrder commerceOrder : commerceOrders) {
			entityCache.removeResult(CommerceOrderImpl.class, commerceOrder);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommerceOrderImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CommerceOrderImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CommerceOrderModelImpl commerceOrderModelImpl) {

		Object[] args = new Object[] {
			commerceOrderModelImpl.getUuid(),
			commerceOrderModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathFetchByUUID_G, args, commerceOrderModelImpl);

		args = new Object[] {
			commerceOrderModelImpl.getExternalReferenceCode(),
			commerceOrderModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C, args, commerceOrderModelImpl);
	}

	/**
	 * Creates a new commerce order with the primary key. Does not add the commerce order to the database.
	 *
	 * @param commerceOrderId the primary key for the new commerce order
	 * @return the new commerce order
	 */
	@Override
	public CommerceOrder create(long commerceOrderId) {
		CommerceOrder commerceOrder = new CommerceOrderImpl();

		commerceOrder.setNew(true);
		commerceOrder.setPrimaryKey(commerceOrderId);

		String uuid = PortalUUIDUtil.generate();

		commerceOrder.setUuid(uuid);

		commerceOrder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceOrder;
	}

	/**
	 * Removes the commerce order with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order that was removed
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder remove(long commerceOrderId)
		throws NoSuchOrderException {

		return remove((Serializable)commerceOrderId);
	}

	/**
	 * Removes the commerce order with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce order
	 * @return the commerce order that was removed
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder remove(Serializable primaryKey)
		throws NoSuchOrderException {

		Session session = null;

		try {
			session = openSession();

			CommerceOrder commerceOrder = (CommerceOrder)session.get(
				CommerceOrderImpl.class, primaryKey);

			if (commerceOrder == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchOrderException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commerceOrder);
		}
		catch (NoSuchOrderException noSuchEntityException) {
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
	protected CommerceOrder removeImpl(CommerceOrder commerceOrder) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceOrder)) {
				commerceOrder = (CommerceOrder)session.get(
					CommerceOrderImpl.class, commerceOrder.getPrimaryKeyObj());
			}

			if (commerceOrder != null) {
				session.delete(commerceOrder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceOrder != null) {
			clearCache(commerceOrder);
		}

		return commerceOrder;
	}

	@Override
	public CommerceOrder updateImpl(CommerceOrder commerceOrder) {
		boolean isNew = commerceOrder.isNew();

		if (!(commerceOrder instanceof CommerceOrderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceOrder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceOrder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceOrder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceOrder implementation " +
					commerceOrder.getClass());
		}

		CommerceOrderModelImpl commerceOrderModelImpl =
			(CommerceOrderModelImpl)commerceOrder;

		if (Validator.isNull(commerceOrder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceOrder.setUuid(uuid);
		}

		if (Validator.isNull(commerceOrder.getExternalReferenceCode())) {
			commerceOrder.setExternalReferenceCode(commerceOrder.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceOrderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceOrder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceOrder.getCompanyId();

					long groupId = commerceOrder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = commerceOrder.getPrimaryKey();
					}

					try {
						commerceOrder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceOrder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceOrder.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceOrder ercCommerceOrder = fetchByERC_C(
				commerceOrder.getExternalReferenceCode(),
				commerceOrder.getCompanyId());

			if (isNew) {
				if (ercCommerceOrder != null) {
					throw new DuplicateCommerceOrderExternalReferenceCodeException(
						"Duplicate commerce order with external reference code " +
							commerceOrder.getExternalReferenceCode() +
								" and company " + commerceOrder.getCompanyId());
				}
			}
			else {
				if ((ercCommerceOrder != null) &&
					(commerceOrder.getCommerceOrderId() !=
						ercCommerceOrder.getCommerceOrderId())) {

					throw new DuplicateCommerceOrderExternalReferenceCodeException(
						"Duplicate commerce order with external reference code " +
							commerceOrder.getExternalReferenceCode() +
								" and company " + commerceOrder.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceOrder.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceOrder.setCreateDate(date);
			}
			else {
				commerceOrder.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!commerceOrderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceOrder.setModifiedDate(date);
			}
			else {
				commerceOrder.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = commerceOrder.getCompanyId();

			long groupId = commerceOrder.getGroupId();

			long commerceOrderId = 0;

			if (!isNew) {
				commerceOrderId = commerceOrder.getPrimaryKey();
			}

			try {
				commerceOrder.setDeliveryCommerceTermEntryDescription(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CommerceOrder.class.getName(), commerceOrderId,
						ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						commerceOrder.getDeliveryCommerceTermEntryDescription(),
						null));

				commerceOrder.setPaymentCommerceTermEntryDescription(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CommerceOrder.class.getName(), commerceOrderId,
						ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						commerceOrder.getPaymentCommerceTermEntryDescription(),
						null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceOrder);
			}
			else {
				commerceOrder = (CommerceOrder)session.merge(commerceOrder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommerceOrderImpl.class, commerceOrderModelImpl, false, true);

		cacheUniqueFindersCache(commerceOrderModelImpl);

		if (isNew) {
			commerceOrder.setNew(false);
		}

		commerceOrder.resetOriginalValues();

		return commerceOrder;
	}

	/**
	 * Returns the commerce order with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce order
	 * @return the commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder findByPrimaryKey(Serializable primaryKey)
		throws NoSuchOrderException {

		CommerceOrder commerceOrder = fetchByPrimaryKey(primaryKey);

		if (commerceOrder == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchOrderException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commerceOrder;
	}

	/**
	 * Returns the commerce order with the primary key or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder findByPrimaryKey(long commerceOrderId)
		throws NoSuchOrderException {

		return findByPrimaryKey((Serializable)commerceOrderId);
	}

	/**
	 * Returns the commerce order with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order, or <code>null</code> if a commerce order with the primary key could not be found
	 */
	@Override
	public CommerceOrder fetchByPrimaryKey(long commerceOrderId) {
		return fetchByPrimaryKey((Serializable)commerceOrderId);
	}

	/**
	 * Returns all the commerce orders.
	 *
	 * @return the commerce orders
	 */
	@Override
	public List<CommerceOrder> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of commerce orders
	 */
	@Override
	public List<CommerceOrder> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce orders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce orders
	 */
	@Override
	public List<CommerceOrder> findAll(
		int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce orders
	 */
	@Override
	public List<CommerceOrder> findAll(
		int start, int end, OrderByComparator<CommerceOrder> orderByComparator,
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

		List<CommerceOrder> list = null;

		if (useFinderCache) {
			list = (List<CommerceOrder>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCEORDER);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCEORDER;

				sql = sql.concat(CommerceOrderModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommerceOrder>)QueryUtil.list(
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
	 * Removes all the commerce orders from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommerceOrder commerceOrder : findAll()) {
			remove(commerceOrder);
		}
	}

	/**
	 * Returns the number of commerce orders.
	 *
	 * @return the number of commerce orders
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_COMMERCEORDER);

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
		return "commerceOrderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEORDER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceOrderModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce order persistence.
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

		_finderPathWithPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId"}, true);

		_finderPathWithoutPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathCountByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"},
			false);

		_finderPathWithPaginationFindByBillingAddressId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByBillingAddressId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"billingAddressId"}, true);

		_finderPathWithoutPaginationFindByBillingAddressId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByBillingAddressId",
			new String[] {Long.class.getName()},
			new String[] {"billingAddressId"}, true);

		_finderPathCountByBillingAddressId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByBillingAddressId", new String[] {Long.class.getName()},
			new String[] {"billingAddressId"}, false);

		_finderPathWithPaginationFindByCommerceAccountId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCommerceAccountId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"commerceAccountId"}, true);

		_finderPathWithoutPaginationFindByCommerceAccountId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByCommerceAccountId", new String[] {Long.class.getName()},
			new String[] {"commerceAccountId"}, true);

		_finderPathCountByCommerceAccountId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByCommerceAccountId", new String[] {Long.class.getName()},
			new String[] {"commerceAccountId"}, false);

		_finderPathWithPaginationFindByShippingAddressId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByShippingAddressId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"shippingAddressId"}, true);

		_finderPathWithoutPaginationFindByShippingAddressId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByShippingAddressId", new String[] {Long.class.getName()},
			new String[] {"shippingAddressId"}, true);

		_finderPathCountByShippingAddressId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByShippingAddressId", new String[] {Long.class.getName()},
			new String[] {"shippingAddressId"}, false);

		_finderPathWithPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "commerceAccountId"}, true);

		_finderPathWithoutPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "commerceAccountId"}, true);

		_finderPathCountByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "commerceAccountId"}, false);

		_finderPathWithPaginationFindByG_CP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_CP",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "commercePaymentMethodKey"}, true);

		_finderPathWithoutPaginationFindByG_CP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_CP",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "commercePaymentMethodKey"}, true);

		_finderPathCountByG_CP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_CP",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "commercePaymentMethodKey"}, false);

		_finderPathWithPaginationFindByG_U_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_O",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "orderStatus"}, true);

		_finderPathWithoutPaginationFindByG_U_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_O",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "orderStatus"}, true);

		_finderPathCountByG_U_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_O",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "orderStatus"}, false);

		_finderPathWithPaginationFindByG_C_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_O",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "commerceAccountId", "orderStatus"}, true);

		_finderPathWithoutPaginationFindByG_C_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_O",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "commerceAccountId", "orderStatus"}, true);

		_finderPathCountByG_C_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_O",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "commerceAccountId", "orderStatus"},
			false);

		_finderPathWithPaginationFindByU_LtC_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_LtC_O",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId", "createDate", "orderStatus"}, true);

		_finderPathWithPaginationCountByU_LtC_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_LtC_O",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"userId", "createDate", "orderStatus"}, false);

		_finderPathWithPaginationFindByC_LtC_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtC_O",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"createDate", "commerceAccountId", "orderStatus"},
			true);

		_finderPathWithPaginationCountByC_LtC_O = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtC_O",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"createDate", "commerceAccountId", "orderStatus"},
			false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CommerceOrderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceOrderUtil.setPersistence(null);

		entityCache.removeCache(CommerceOrderImpl.class.getName());
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
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_COMMERCEORDER =
		"SELECT commerceOrder FROM CommerceOrder commerceOrder";

	private static final String _SQL_SELECT_COMMERCEORDER_WHERE =
		"SELECT commerceOrder FROM CommerceOrder commerceOrder WHERE ";

	private static final String _SQL_COUNT_COMMERCEORDER =
		"SELECT COUNT(commerceOrder) FROM CommerceOrder commerceOrder";

	private static final String _SQL_COUNT_COMMERCEORDER_WHERE =
		"SELECT COUNT(commerceOrder) FROM CommerceOrder commerceOrder WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commerceOrder.commerceOrderId";

	private static final String _FILTER_SQL_SELECT_COMMERCEORDER_WHERE =
		"SELECT DISTINCT {commerceOrder.*} FROM CommerceOrder commerceOrder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CommerceOrder.*} FROM (SELECT DISTINCT commerceOrder.commerceOrderId FROM CommerceOrder commerceOrder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEORDER_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CommerceOrder ON TEMP_TABLE.commerceOrderId = CommerceOrder.commerceOrderId";

	private static final String _FILTER_SQL_COUNT_COMMERCEORDER_WHERE =
		"SELECT COUNT(DISTINCT commerceOrder.commerceOrderId) AS COUNT_VALUE FROM CommerceOrder commerceOrder WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "commerceOrder";

	private static final String _FILTER_ENTITY_TABLE = "CommerceOrder";

	private static final String _ORDER_BY_ENTITY_ALIAS = "commerceOrder.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CommerceOrder.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommerceOrder exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceOrder exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "deliveryCommerceTermEntryDescription",
			"paymentCommerceTermEntryDescription",
			"shippingDiscountPercentageLevel1",
			"shippingDiscountPercentageLevel2",
			"shippingDiscountPercentageLevel3",
			"shippingDiscountPercentageLevel4",
			"shippingDiscountPercentageLevel1WithTaxAmount",
			"shippingDiscountPercentageLevel2WithTaxAmount",
			"shippingDiscountPercentageLevel3WithTaxAmount",
			"shippingDiscountPercentageLevel4WithTaxAmount",
			"subtotalDiscountPercentageLevel1",
			"subtotalDiscountPercentageLevel2",
			"subtotalDiscountPercentageLevel3",
			"subtotalDiscountPercentageLevel4",
			"subtotalDiscountPercentageLevel1WithTaxAmount",
			"subtotalDiscountPercentageLevel2WithTaxAmount",
			"subtotalDiscountPercentageLevel3WithTaxAmount",
			"subtotalDiscountPercentageLevel4WithTaxAmount",
			"totalDiscountPercentageLevel1WithTaxAmount",
			"totalDiscountPercentageLevel2WithTaxAmount",
			"totalDiscountPercentageLevel3WithTaxAmount",
			"totalDiscountPercentageLevel4WithTaxAmount"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}