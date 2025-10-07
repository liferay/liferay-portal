/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.impl;

import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehousePersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseUtil;
import com.liferay.commerce.inventory.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce inventory warehouse service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceInventoryWarehousePersistence.class)
public class CommerceInventoryWarehousePersistenceImpl
	extends BasePersistenceImpl<CommerceInventoryWarehouse>
	implements CommerceInventoryWarehousePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceInventoryWarehouseUtil</code> to access the commerce inventory warehouse persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceInventoryWarehouseImpl.class.getName();

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
	 * Returns all the commerce inventory warehouses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		List<CommerceInventoryWarehouse> list = null;

		if (useFinderCache) {
			list = (List<CommerceInventoryWarehouse>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceInventoryWarehouse commerceInventoryWarehouse :
						list) {

					if (!uuid.equals(commerceInventoryWarehouse.getUuid())) {
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

			sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByUuid_First(
			String uuid,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByUuid_First(uuid, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		List<CommerceInventoryWarehouse> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByUuid_Last(
			String uuid,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByUuid_Last(uuid, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByUuid_Last(
		String uuid,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CommerceInventoryWarehouse> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set where uuid = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] findByUuid_PrevAndNext(
			long commerceInventoryWarehouseId, String uuid,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		uuid = Objects.toString(uuid, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, orderByComparator,
				true);

			array[1] = commerceInventoryWarehouse;

			array[2] = getByUuid_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, orderByComparator,
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

	protected CommerceInventoryWarehouse getByUuid_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		String uuid,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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
			sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
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
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce inventory warehouses that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid(String uuid) {
		return filterFindByUuid(
			uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid(
		String uuid, int start, int end) {

		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid(uuid, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid(
					uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			return (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set of commerce inventory warehouses that the user has permission to view where uuid = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] filterFindByUuid_PrevAndNext(
			long commerceInventoryWarehouseId, String uuid,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid_PrevAndNext(
				commerceInventoryWarehouseId, uuid, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = filterGetByUuid_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, orderByComparator,
				true);

			array[1] = commerceInventoryWarehouse;

			array[2] = filterGetByUuid_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, orderByComparator,
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

	protected CommerceInventoryWarehouse filterGetByUuid_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		String uuid,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce inventory warehouses where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceInventoryWarehouse);
		}
	}

	/**
	 * Returns the number of commerce inventory warehouses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUuid(uuid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				findByUuid(uuid);

			commerceInventoryWarehouses = InlineSQLHelperUtil.filter(
				commerceInventoryWarehouses);

			return commerceInventoryWarehouses.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"commerceInventoryWarehouse.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(commerceInventoryWarehouse.uuid IS NULL OR commerceInventoryWarehouse.uuid = '')";

	private static final String _FINDER_COLUMN_UUID_UUID_2_SQL =
		"commerceInventoryWarehouse.uuid_ = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3_SQL =
		"(commerceInventoryWarehouse.uuid_ IS NULL OR commerceInventoryWarehouse.uuid_ = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the commerce inventory warehouses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		List<CommerceInventoryWarehouse> list = null;

		if (useFinderCache) {
			list = (List<CommerceInventoryWarehouse>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceInventoryWarehouse commerceInventoryWarehouse :
						list) {

					if (!uuid.equals(commerceInventoryWarehouse.getUuid()) ||
						(companyId !=
							commerceInventoryWarehouse.getCompanyId())) {

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

			sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		List<CommerceInventoryWarehouse> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByUuid_C_Last(uuid, companyId, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CommerceInventoryWarehouse> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] findByUuid_C_PrevAndNext(
			long commerceInventoryWarehouseId, String uuid, long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		uuid = Objects.toString(uuid, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, companyId,
				orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = getByUuid_C_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, companyId,
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

	protected CommerceInventoryWarehouse getByUuid_C_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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
			sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
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
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce inventory warehouses that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid_C(
		String uuid, long companyId) {

		return filterFindByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C(uuid, companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

			return (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set of commerce inventory warehouses that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] filterFindByUuid_C_PrevAndNext(
			long commerceInventoryWarehouseId, String uuid, long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C_PrevAndNext(
				commerceInventoryWarehouseId, uuid, companyId,
				orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = filterGetByUuid_C_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, companyId,
				orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = filterGetByUuid_C_PrevAndNext(
				session, commerceInventoryWarehouse, uuid, companyId,
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

	protected CommerceInventoryWarehouse filterGetByUuid_C_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce inventory warehouses where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceInventoryWarehouse);
		}
	}

	/**
	 * Returns the number of commerce inventory warehouses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByUuid_C(uuid, companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				findByUuid_C(uuid, companyId);

			commerceInventoryWarehouses = InlineSQLHelperUtil.filter(
				commerceInventoryWarehouses);

			return commerceInventoryWarehouses.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"commerceInventoryWarehouse.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(commerceInventoryWarehouse.uuid IS NULL OR commerceInventoryWarehouse.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_2_SQL =
		"commerceInventoryWarehouse.uuid_ = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3_SQL =
		"(commerceInventoryWarehouse.uuid_ IS NULL OR commerceInventoryWarehouse.uuid_ = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"commerceInventoryWarehouse.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the commerce inventory warehouses where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

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

		List<CommerceInventoryWarehouse> list = null;

		if (useFinderCache) {
			list = (List<CommerceInventoryWarehouse>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceInventoryWarehouse commerceInventoryWarehouse :
						list) {

					if (companyId !=
							commerceInventoryWarehouse.getCompanyId()) {

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

			sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByCompanyId_First(companyId, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		List<CommerceInventoryWarehouse> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByCompanyId_Last(
			long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByCompanyId_Last(companyId, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CommerceInventoryWarehouse> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set where companyId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] findByCompanyId_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
				orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = getByCompanyId_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
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

	protected CommerceInventoryWarehouse getByCompanyId_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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
			sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
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
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce inventory warehouses that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByCompanyId(
		long companyId) {

		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId(companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set of commerce inventory warehouses that the user has permission to view where companyId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] filterFindByCompanyId_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				commerceInventoryWarehouseId, companyId, orderByComparator);
		}

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
				orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
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

	protected CommerceInventoryWarehouse filterGetByCompanyId_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceInventoryWarehouse);
		}
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				findByCompanyId(companyId);

			commerceInventoryWarehouses = InlineSQLHelperUtil.filter(
				commerceInventoryWarehouses);

			return commerceInventoryWarehouses.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"commerceInventoryWarehouse.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_A;
	private FinderPath _finderPathWithoutPaginationFindByC_A;
	private FinderPath _finderPathCountByC_A;

	/**
	 * Returns all the commerce inventory warehouses where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A(
		long companyId, boolean active) {

		return findByC_A(
			companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A(
		long companyId, boolean active, int start, int end) {

		return findByC_A(companyId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return findByC_A(
			companyId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_A;
				finderArgs = new Object[] {companyId, active};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_A;
			finderArgs = new Object[] {
				companyId, active, start, end, orderByComparator
			};
		}

		List<CommerceInventoryWarehouse> list = null;

		if (useFinderCache) {
			list = (List<CommerceInventoryWarehouse>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceInventoryWarehouse commerceInventoryWarehouse :
						list) {

					if ((companyId !=
							commerceInventoryWarehouse.getCompanyId()) ||
						(active != commerceInventoryWarehouse.isActive())) {

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

			sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				list = (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByC_A_First(companyId, active, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		List<CommerceInventoryWarehouse> list = findByC_A(
			companyId, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_A_Last(
			long companyId, boolean active,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse = fetchByC_A_Last(
			companyId, active, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_A_Last(
		long companyId, boolean active,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		int count = countByC_A(companyId, active);

		if (count == 0) {
			return null;
		}

		List<CommerceInventoryWarehouse> list = findByC_A(
			companyId, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] findByC_A_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId, boolean active,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = getByC_A_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
				orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = getByC_A_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
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

	protected CommerceInventoryWarehouse getByC_A_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId, boolean active,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

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
			sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A(
		long companyId, boolean active) {

		return filterFindByC_A(
			companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A(
		long companyId, boolean active, int start, int end) {

		return filterFindByC_A(companyId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A(companyId, active, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_A(
					companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

			return (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set of commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] filterFindByC_A_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId, boolean active,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_PrevAndNext(
				commerceInventoryWarehouseId, companyId, active,
				orderByComparator);
		}

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = filterGetByC_A_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
				orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = filterGetByC_A_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
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

	protected CommerceInventoryWarehouse filterGetByC_A_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId, boolean active,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long companyId, boolean active) {
		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				findByC_A(
					companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceInventoryWarehouse);
		}
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		FinderPath finderPath = _finderPathCountByC_A;

		Object[] finderArgs = new Object[] {companyId, active};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

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
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, boolean active) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_A(companyId, active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				findByC_A(companyId, active);

			commerceInventoryWarehouses = InlineSQLHelperUtil.filter(
				commerceInventoryWarehouses);

			return commerceInventoryWarehouses.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_C_A_COMPANYID_2 =
		"commerceInventoryWarehouse.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_ACTIVE_2 =
		"commerceInventoryWarehouse.active = ?";

	private static final String _FINDER_COLUMN_C_A_ACTIVE_2_SQL =
		"commerceInventoryWarehouse.active_ = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_C(
		long companyId, String countryTwoLettersISOCode) {

		return findByC_C(
			companyId, countryTwoLettersISOCode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_C(
		long companyId, String countryTwoLettersISOCode, int start, int end) {

		return findByC_C(companyId, countryTwoLettersISOCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_C(
		long companyId, String countryTwoLettersISOCode, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return findByC_C(
			companyId, countryTwoLettersISOCode, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_C(
		long companyId, String countryTwoLettersISOCode, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_C;
				finderArgs = new Object[] {companyId, countryTwoLettersISOCode};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_C;
			finderArgs = new Object[] {
				companyId, countryTwoLettersISOCode, start, end,
				orderByComparator
			};
		}

		List<CommerceInventoryWarehouse> list = null;

		if (useFinderCache) {
			list = (List<CommerceInventoryWarehouse>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceInventoryWarehouse commerceInventoryWarehouse :
						list) {

					if ((companyId !=
							commerceInventoryWarehouse.getCompanyId()) ||
						!countryTwoLettersISOCode.equals(
							commerceInventoryWarehouse.
								getCountryTwoLettersISOCode())) {

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

			sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

			sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

			boolean bindCountryTwoLettersISOCode = false;

			if (countryTwoLettersISOCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_3);
			}
			else {
				bindCountryTwoLettersISOCode = true;

				sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindCountryTwoLettersISOCode) {
					queryPos.add(countryTwoLettersISOCode);
				}

				list = (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_C_First(
			long companyId, String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByC_C_First(
				companyId, countryTwoLettersISOCode, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", countryTwoLettersISOCode=");
		sb.append(countryTwoLettersISOCode);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_C_First(
		long companyId, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		List<CommerceInventoryWarehouse> list = findByC_C(
			companyId, countryTwoLettersISOCode, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_C_Last(
			long companyId, String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse = fetchByC_C_Last(
			companyId, countryTwoLettersISOCode, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", countryTwoLettersISOCode=");
		sb.append(countryTwoLettersISOCode);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_C_Last(
		long companyId, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		int count = countByC_C(companyId, countryTwoLettersISOCode);

		if (count == 0) {
			return null;
		}

		List<CommerceInventoryWarehouse> list = findByC_C(
			companyId, countryTwoLettersISOCode, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] findByC_C_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId,
			String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
				countryTwoLettersISOCode, orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = getByC_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
				countryTwoLettersISOCode, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceInventoryWarehouse getByC_C_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_2);
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
			sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindCountryTwoLettersISOCode) {
			queryPos.add(countryTwoLettersISOCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce inventory warehouses that the user has permission to view where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_C(
		long companyId, String countryTwoLettersISOCode) {

		return filterFindByC_C(
			companyId, countryTwoLettersISOCode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses that the user has permission to view where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_C(
		long companyId, String countryTwoLettersISOCode, int start, int end) {

		return filterFindByC_C(
			companyId, countryTwoLettersISOCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_C(
		long companyId, String countryTwoLettersISOCode, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C(
				companyId, countryTwoLettersISOCode, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_C(
					companyId, countryTwoLettersISOCode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindCountryTwoLettersISOCode) {
				queryPos.add(countryTwoLettersISOCode);
			}

			return (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set of commerce inventory warehouses that the user has permission to view where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] filterFindByC_C_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId,
			String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_PrevAndNext(
				commerceInventoryWarehouseId, companyId,
				countryTwoLettersISOCode, orderByComparator);
		}

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = filterGetByC_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
				countryTwoLettersISOCode, orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = filterGetByC_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId,
				countryTwoLettersISOCode, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceInventoryWarehouse filterGetByC_C_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindCountryTwoLettersISOCode) {
			queryPos.add(countryTwoLettersISOCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 */
	@Override
	public void removeByC_C(long companyId, String countryTwoLettersISOCode) {
		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				findByC_C(
					companyId, countryTwoLettersISOCode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceInventoryWarehouse);
		}
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByC_C(long companyId, String countryTwoLettersISOCode) {
		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		FinderPath finderPath = _finderPathCountByC_C;

		Object[] finderArgs = new Object[] {
			companyId, countryTwoLettersISOCode
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

			sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

			boolean bindCountryTwoLettersISOCode = false;

			if (countryTwoLettersISOCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_3);
			}
			else {
				bindCountryTwoLettersISOCode = true;

				sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindCountryTwoLettersISOCode) {
					queryPos.add(countryTwoLettersISOCode);
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
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByC_C(
		long companyId, String countryTwoLettersISOCode) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_C(companyId, countryTwoLettersISOCode);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				findByC_C(companyId, countryTwoLettersISOCode);

			commerceInventoryWarehouses = InlineSQLHelperUtil.filter(
				commerceInventoryWarehouses);

			return commerceInventoryWarehouses.size();
		}

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindCountryTwoLettersISOCode) {
				queryPos.add(countryTwoLettersISOCode);
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

	private static final String _FINDER_COLUMN_C_C_COMPANYID_2 =
		"commerceInventoryWarehouse.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_2 =
		"commerceInventoryWarehouse.countryTwoLettersISOCode = ?";

	private static final String _FINDER_COLUMN_C_C_COUNTRYTWOLETTERSISOCODE_3 =
		"(commerceInventoryWarehouse.countryTwoLettersISOCode IS NULL OR commerceInventoryWarehouse.countryTwoLettersISOCode = '')";

	private FinderPath _finderPathWithPaginationFindByC_A_C;
	private FinderPath _finderPathWithoutPaginationFindByC_A_C;
	private FinderPath _finderPathCountByC_A_C;

	/**
	 * Returns all the commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		return findByC_A_C(
			companyId, active, countryTwoLettersISOCode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode,
		int start, int end) {

		return findByC_A_C(
			companyId, active, countryTwoLettersISOCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode,
		int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return findByC_A_C(
			companyId, active, countryTwoLettersISOCode, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode,
		int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_A_C;
				finderArgs = new Object[] {
					companyId, active, countryTwoLettersISOCode
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_A_C;
			finderArgs = new Object[] {
				companyId, active, countryTwoLettersISOCode, start, end,
				orderByComparator
			};
		}

		List<CommerceInventoryWarehouse> list = null;

		if (useFinderCache) {
			list = (List<CommerceInventoryWarehouse>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceInventoryWarehouse commerceInventoryWarehouse :
						list) {

					if ((companyId !=
							commerceInventoryWarehouse.getCompanyId()) ||
						(active != commerceInventoryWarehouse.isActive()) ||
						!countryTwoLettersISOCode.equals(
							commerceInventoryWarehouse.
								getCountryTwoLettersISOCode())) {

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

			sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_C_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_C_ACTIVE_2);

			boolean bindCountryTwoLettersISOCode = false;

			if (countryTwoLettersISOCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_3);
			}
			else {
				bindCountryTwoLettersISOCode = true;

				sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				if (bindCountryTwoLettersISOCode) {
					queryPos.add(countryTwoLettersISOCode);
				}

				list = (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_A_C_First(
			long companyId, boolean active, String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByC_A_C_First(
				companyId, active, countryTwoLettersISOCode, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", countryTwoLettersISOCode=");
		sb.append(countryTwoLettersISOCode);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_A_C_First(
		long companyId, boolean active, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		List<CommerceInventoryWarehouse> list = findByC_A_C(
			companyId, active, countryTwoLettersISOCode, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_A_C_Last(
			long companyId, boolean active, String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByC_A_C_Last(
				companyId, active, countryTwoLettersISOCode, orderByComparator);

		if (commerceInventoryWarehouse != null) {
			return commerceInventoryWarehouse;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", countryTwoLettersISOCode=");
		sb.append(countryTwoLettersISOCode);

		sb.append("}");

		throw new NoSuchInventoryWarehouseException(sb.toString());
	}

	/**
	 * Returns the last commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_A_C_Last(
		long companyId, boolean active, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		int count = countByC_A_C(companyId, active, countryTwoLettersISOCode);

		if (count == 0) {
			return null;
		}

		List<CommerceInventoryWarehouse> list = findByC_A_C(
			companyId, active, countryTwoLettersISOCode, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] findByC_A_C_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId, boolean active,
			String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = getByC_A_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
				countryTwoLettersISOCode, orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = getByC_A_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
				countryTwoLettersISOCode, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceInventoryWarehouse getByC_A_C_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId, boolean active, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		sb.append(_FINDER_COLUMN_C_A_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_C_ACTIVE_2);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_2);
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
			sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(active);

		if (bindCountryTwoLettersISOCode) {
			queryPos.add(countryTwoLettersISOCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		return filterFindByC_A_C(
			companyId, active, countryTwoLettersISOCode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode,
		int start, int end) {

		return filterFindByC_A_C(
			companyId, active, countryTwoLettersISOCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode,
		int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_C(
				companyId, active, countryTwoLettersISOCode, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_A_C(
					companyId, active, countryTwoLettersISOCode,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
		}

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_C_ACTIVE_2_SQL);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

			if (bindCountryTwoLettersISOCode) {
				queryPos.add(countryTwoLettersISOCode);
			}

			return (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Returns the commerce inventory warehouses before and after the current commerce inventory warehouse in the ordered set of commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the current commerce inventory warehouse
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse[] filterFindByC_A_C_PrevAndNext(
			long commerceInventoryWarehouseId, long companyId, boolean active,
			String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_C_PrevAndNext(
				commerceInventoryWarehouseId, companyId, active,
				countryTwoLettersISOCode, orderByComparator);
		}

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			findByPrimaryKey(commerceInventoryWarehouseId);

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse[] array =
				new CommerceInventoryWarehouseImpl[3];

			array[0] = filterGetByC_A_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
				countryTwoLettersISOCode, orderByComparator, true);

			array[1] = commerceInventoryWarehouse;

			array[2] = filterGetByC_A_C_PrevAndNext(
				session, commerceInventoryWarehouse, companyId, active,
				countryTwoLettersISOCode, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceInventoryWarehouse filterGetByC_A_C_PrevAndNext(
		Session session, CommerceInventoryWarehouse commerceInventoryWarehouse,
		long companyId, boolean active, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_C_ACTIVE_2_SQL);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceInventoryWarehouseModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceInventoryWarehouseModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceInventoryWarehouseImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceInventoryWarehouseImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(active);

		if (bindCountryTwoLettersISOCode) {
			queryPos.add(countryTwoLettersISOCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceInventoryWarehouse)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceInventoryWarehouse> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 */
	@Override
	public void removeByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				findByC_A_C(
					companyId, active, countryTwoLettersISOCode,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceInventoryWarehouse);
		}
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		FinderPath finderPath = _finderPathCountByC_A_C;

		Object[] finderArgs = new Object[] {
			companyId, active, countryTwoLettersISOCode
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_C_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_C_ACTIVE_2);

			boolean bindCountryTwoLettersISOCode = false;

			if (countryTwoLettersISOCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_3);
			}
			else {
				bindCountryTwoLettersISOCode = true;

				sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				if (bindCountryTwoLettersISOCode) {
					queryPos.add(countryTwoLettersISOCode);
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
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_A_C(companyId, active, countryTwoLettersISOCode);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				findByC_A_C(companyId, active, countryTwoLettersISOCode);

			commerceInventoryWarehouses = InlineSQLHelperUtil.filter(
				commerceInventoryWarehouses);

			return commerceInventoryWarehouses.size();
		}

		countryTwoLettersISOCode = Objects.toString(
			countryTwoLettersISOCode, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE);

		sb.append(_FINDER_COLUMN_C_A_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_C_ACTIVE_2_SQL);

		boolean bindCountryTwoLettersISOCode = false;

		if (countryTwoLettersISOCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_3);
		}
		else {
			bindCountryTwoLettersISOCode = true;

			sb.append(_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceInventoryWarehouse.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

			if (bindCountryTwoLettersISOCode) {
				queryPos.add(countryTwoLettersISOCode);
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

	private static final String _FINDER_COLUMN_C_A_C_COMPANYID_2 =
		"commerceInventoryWarehouse.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_C_ACTIVE_2 =
		"commerceInventoryWarehouse.active = ? AND ";

	private static final String _FINDER_COLUMN_C_A_C_ACTIVE_2_SQL =
		"commerceInventoryWarehouse.active_ = ? AND ";

	private static final String
		_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_2 =
			"commerceInventoryWarehouse.countryTwoLettersISOCode = ?";

	private static final String
		_FINDER_COLUMN_C_A_C_COUNTRYTWOLETTERSISOCODE_3 =
			"(commerceInventoryWarehouse.countryTwoLettersISOCode IS NULL OR commerceInventoryWarehouse.countryTwoLettersISOCode = '')";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the commerce inventory warehouse where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchInventoryWarehouseException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceInventoryWarehouse == null) {
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

			throw new NoSuchInventoryWarehouseException(sb.toString());
		}

		return commerceInventoryWarehouse;
	}

	/**
	 * Returns the commerce inventory warehouse where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the commerce inventory warehouse where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByERC_C(
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

		if (result instanceof CommerceInventoryWarehouse) {
			CommerceInventoryWarehouse commerceInventoryWarehouse =
				(CommerceInventoryWarehouse)result;

			if (!Objects.equals(
					externalReferenceCode,
					commerceInventoryWarehouse.getExternalReferenceCode()) ||
				(companyId != commerceInventoryWarehouse.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE);

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

				List<CommerceInventoryWarehouse> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					CommerceInventoryWarehouse commerceInventoryWarehouse =
						list.get(0);

					result = commerceInventoryWarehouse;

					cacheResult(commerceInventoryWarehouse);
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
			return (CommerceInventoryWarehouse)result;
		}
	}

	/**
	 * Removes the commerce inventory warehouse where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce inventory warehouse that was removed
	 */
	@Override
	public CommerceInventoryWarehouse removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceInventoryWarehouse);
	}

	/**
	 * Returns the number of commerce inventory warehouses where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CommerceInventoryWarehouse commerceInventoryWarehouse = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceInventoryWarehouse == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"commerceInventoryWarehouse.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(commerceInventoryWarehouse.externalReferenceCode IS NULL OR commerceInventoryWarehouse.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"commerceInventoryWarehouse.companyId = ?";

	public CommerceInventoryWarehousePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("commerceInventoryWarehouseId", "CIWarehouseId");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceInventoryWarehouse.class);

		setModelImplClass(CommerceInventoryWarehouseImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceInventoryWarehouseTable.INSTANCE);
	}

	/**
	 * Caches the commerce inventory warehouse in the entity cache if it is enabled.
	 *
	 * @param commerceInventoryWarehouse the commerce inventory warehouse
	 */
	@Override
	public void cacheResult(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		entityCache.putResult(
			CommerceInventoryWarehouseImpl.class,
			commerceInventoryWarehouse.getPrimaryKey(),
			commerceInventoryWarehouse);

		finderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				commerceInventoryWarehouse.getExternalReferenceCode(),
				commerceInventoryWarehouse.getCompanyId()
			},
			commerceInventoryWarehouse);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce inventory warehouses in the entity cache if it is enabled.
	 *
	 * @param commerceInventoryWarehouses the commerce inventory warehouses
	 */
	@Override
	public void cacheResult(
		List<CommerceInventoryWarehouse> commerceInventoryWarehouses) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commerceInventoryWarehouses.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				commerceInventoryWarehouses) {

			if (entityCache.getResult(
					CommerceInventoryWarehouseImpl.class,
					commerceInventoryWarehouse.getPrimaryKey()) == null) {

				cacheResult(commerceInventoryWarehouse);
			}
		}
	}

	/**
	 * Clears the cache for all commerce inventory warehouses.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommerceInventoryWarehouseImpl.class);

		finderCache.clearCache(CommerceInventoryWarehouseImpl.class);
	}

	/**
	 * Clears the cache for the commerce inventory warehouse.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		entityCache.removeResult(
			CommerceInventoryWarehouseImpl.class, commerceInventoryWarehouse);
	}

	@Override
	public void clearCache(
		List<CommerceInventoryWarehouse> commerceInventoryWarehouses) {

		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				commerceInventoryWarehouses) {

			entityCache.removeResult(
				CommerceInventoryWarehouseImpl.class,
				commerceInventoryWarehouse);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommerceInventoryWarehouseImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CommerceInventoryWarehouseImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CommerceInventoryWarehouseModelImpl
			commerceInventoryWarehouseModelImpl) {

		Object[] args = new Object[] {
			commerceInventoryWarehouseModelImpl.getExternalReferenceCode(),
			commerceInventoryWarehouseModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C, args, commerceInventoryWarehouseModelImpl);
	}

	/**
	 * Creates a new commerce inventory warehouse with the primary key. Does not add the commerce inventory warehouse to the database.
	 *
	 * @param commerceInventoryWarehouseId the primary key for the new commerce inventory warehouse
	 * @return the new commerce inventory warehouse
	 */
	@Override
	public CommerceInventoryWarehouse create(
		long commerceInventoryWarehouseId) {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			new CommerceInventoryWarehouseImpl();

		commerceInventoryWarehouse.setNew(true);
		commerceInventoryWarehouse.setPrimaryKey(commerceInventoryWarehouseId);

		String uuid = PortalUUIDUtil.generate();

		commerceInventoryWarehouse.setUuid(uuid);

		commerceInventoryWarehouse.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceInventoryWarehouse;
	}

	/**
	 * Removes the commerce inventory warehouse with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was removed
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse remove(long commerceInventoryWarehouseId)
		throws NoSuchInventoryWarehouseException {

		return remove((Serializable)commerceInventoryWarehouseId);
	}

	/**
	 * Removes the commerce inventory warehouse with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was removed
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse remove(Serializable primaryKey)
		throws NoSuchInventoryWarehouseException {

		Session session = null;

		try {
			session = openSession();

			CommerceInventoryWarehouse commerceInventoryWarehouse =
				(CommerceInventoryWarehouse)session.get(
					CommerceInventoryWarehouseImpl.class, primaryKey);

			if (commerceInventoryWarehouse == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchInventoryWarehouseException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commerceInventoryWarehouse);
		}
		catch (NoSuchInventoryWarehouseException noSuchEntityException) {
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
	protected CommerceInventoryWarehouse removeImpl(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceInventoryWarehouse)) {
				commerceInventoryWarehouse =
					(CommerceInventoryWarehouse)session.get(
						CommerceInventoryWarehouseImpl.class,
						commerceInventoryWarehouse.getPrimaryKeyObj());
			}

			if (commerceInventoryWarehouse != null) {
				session.delete(commerceInventoryWarehouse);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceInventoryWarehouse != null) {
			clearCache(commerceInventoryWarehouse);
		}

		return commerceInventoryWarehouse;
	}

	@Override
	public CommerceInventoryWarehouse updateImpl(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		boolean isNew = commerceInventoryWarehouse.isNew();

		if (!(commerceInventoryWarehouse instanceof
				CommerceInventoryWarehouseModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceInventoryWarehouse.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceInventoryWarehouse);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceInventoryWarehouse proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceInventoryWarehouse implementation " +
					commerceInventoryWarehouse.getClass());
		}

		CommerceInventoryWarehouseModelImpl
			commerceInventoryWarehouseModelImpl =
				(CommerceInventoryWarehouseModelImpl)commerceInventoryWarehouse;

		if (Validator.isNull(commerceInventoryWarehouse.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceInventoryWarehouse.setUuid(uuid);
		}

		if (Validator.isNull(
				commerceInventoryWarehouse.getExternalReferenceCode())) {

			commerceInventoryWarehouse.setExternalReferenceCode(
				commerceInventoryWarehouse.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceInventoryWarehouseModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceInventoryWarehouse.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceInventoryWarehouse.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceInventoryWarehouse.getPrimaryKey();
					}

					try {
						commerceInventoryWarehouse.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceInventoryWarehouse.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								commerceInventoryWarehouse.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceInventoryWarehouse ercCommerceInventoryWarehouse =
				fetchByERC_C(
					commerceInventoryWarehouse.getExternalReferenceCode(),
					commerceInventoryWarehouse.getCompanyId());

			if (isNew) {
				if (ercCommerceInventoryWarehouse != null) {
					throw new DuplicateCommerceInventoryWarehouseExternalReferenceCodeException(
						"Duplicate commerce inventory warehouse with external reference code " +
							commerceInventoryWarehouse.
								getExternalReferenceCode() + " and company " +
									commerceInventoryWarehouse.getCompanyId());
				}
			}
			else {
				if ((ercCommerceInventoryWarehouse != null) &&
					(commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId() !=
							ercCommerceInventoryWarehouse.
								getCommerceInventoryWarehouseId())) {

					throw new DuplicateCommerceInventoryWarehouseExternalReferenceCodeException(
						"Duplicate commerce inventory warehouse with external reference code " +
							commerceInventoryWarehouse.
								getExternalReferenceCode() + " and company " +
									commerceInventoryWarehouse.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceInventoryWarehouse.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceInventoryWarehouse.setCreateDate(date);
			}
			else {
				commerceInventoryWarehouse.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceInventoryWarehouseModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceInventoryWarehouse.setModifiedDate(date);
			}
			else {
				commerceInventoryWarehouse.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceInventoryWarehouse);
			}
			else {
				commerceInventoryWarehouse =
					(CommerceInventoryWarehouse)session.merge(
						commerceInventoryWarehouse);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommerceInventoryWarehouseImpl.class,
			commerceInventoryWarehouseModelImpl, false, true);

		cacheUniqueFindersCache(commerceInventoryWarehouseModelImpl);

		if (isNew) {
			commerceInventoryWarehouse.setNew(false);
		}

		commerceInventoryWarehouse.resetOriginalValues();

		return commerceInventoryWarehouse;
	}

	/**
	 * Returns the commerce inventory warehouse with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByPrimaryKey(Serializable primaryKey)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			fetchByPrimaryKey(primaryKey);

		if (commerceInventoryWarehouse == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchInventoryWarehouseException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commerceInventoryWarehouse;
	}

	/**
	 * Returns the commerce inventory warehouse with the primary key or throws a <code>NoSuchInventoryWarehouseException</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByPrimaryKey(
			long commerceInventoryWarehouseId)
		throws NoSuchInventoryWarehouseException {

		return findByPrimaryKey((Serializable)commerceInventoryWarehouseId);
	}

	/**
	 * Returns the commerce inventory warehouse with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse, or <code>null</code> if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByPrimaryKey(
		long commerceInventoryWarehouseId) {

		return fetchByPrimaryKey((Serializable)commerceInventoryWarehouseId);
	}

	/**
	 * Returns all the commerce inventory warehouses.
	 *
	 * @return the commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findAll(
		int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findAll(
		int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
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

		List<CommerceInventoryWarehouse> list = null;

		if (useFinderCache) {
			list = (List<CommerceInventoryWarehouse>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCEINVENTORYWAREHOUSE;

				sql = sql.concat(
					CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommerceInventoryWarehouse>)QueryUtil.list(
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
	 * Removes all the commerce inventory warehouses from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				findAll()) {

			remove(commerceInventoryWarehouse);
		}
	}

	/**
	 * Returns the number of commerce inventory warehouses.
	 *
	 * @return the number of commerce inventory warehouses
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
					_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE);

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
		return "CIWarehouseId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEINVENTORYWAREHOUSE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceInventoryWarehouseModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce inventory warehouse persistence.
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

		_finderPathWithPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "active_"}, true);

		_finderPathWithoutPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, true);

		_finderPathCountByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, false);

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "countryTwoLettersISOCode"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "countryTwoLettersISOCode"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "countryTwoLettersISOCode"}, false);

		_finderPathWithPaginationFindByC_A_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_C",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "active_", "countryTwoLettersISOCode"},
			true);

		_finderPathWithoutPaginationFindByC_A_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_C",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "active_", "countryTwoLettersISOCode"},
			true);

		_finderPathCountByC_A_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_C",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "active_", "countryTwoLettersISOCode"},
			false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CommerceInventoryWarehouseUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceInventoryWarehouseUtil.setPersistence(null);

		entityCache.removeCache(CommerceInventoryWarehouseImpl.class.getName());
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

	private static final String _SQL_SELECT_COMMERCEINVENTORYWAREHOUSE =
		"SELECT commerceInventoryWarehouse FROM CommerceInventoryWarehouse commerceInventoryWarehouse";

	private static final String _SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE =
		"SELECT commerceInventoryWarehouse FROM CommerceInventoryWarehouse commerceInventoryWarehouse WHERE ";

	private static final String _SQL_COUNT_COMMERCEINVENTORYWAREHOUSE =
		"SELECT COUNT(commerceInventoryWarehouse) FROM CommerceInventoryWarehouse commerceInventoryWarehouse";

	private static final String _SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE =
		"SELECT COUNT(commerceInventoryWarehouse) FROM CommerceInventoryWarehouse commerceInventoryWarehouse WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commerceInventoryWarehouse.CIWarehouseId";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE =
			"SELECT DISTINCT {commerceInventoryWarehouse.*} FROM CIWarehouse commerceInventoryWarehouse WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CIWarehouse.*} FROM (SELECT DISTINCT commerceInventoryWarehouse.CIWarehouseId FROM CIWarehouse commerceInventoryWarehouse WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CIWarehouse ON TEMP_TABLE.CIWarehouseId = CIWarehouse.CIWarehouseId";

	private static final String
		_FILTER_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE =
			"SELECT COUNT(DISTINCT commerceInventoryWarehouse.CIWarehouseId) AS COUNT_VALUE FROM CIWarehouse commerceInventoryWarehouse WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"commerceInventoryWarehouse";

	private static final String _FILTER_ENTITY_TABLE = "CIWarehouse";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"commerceInventoryWarehouse.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CIWarehouse.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommerceInventoryWarehouse exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceInventoryWarehouse exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceInventoryWarehousePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "commerceInventoryWarehouseId", "active", "type"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}