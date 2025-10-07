/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountExternalReferenceCodeException;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountTable;
import com.liferay.commerce.discount.model.impl.CommerceDiscountImpl;
import com.liferay.commerce.discount.model.impl.CommerceDiscountModelImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountUtil;
import com.liferay.commerce.discount.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Timestamp;

import java.util.Collections;
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
 * The persistence implementation for the commerce discount service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceDiscountPersistence.class)
public class CommerceDiscountPersistenceImpl
	extends BasePersistenceImpl<CommerceDiscount>
	implements CommerceDiscountPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceDiscountUtil</code> to access the commerce discount persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceDiscountImpl.class.getName();

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
	 * Returns all the commerce discounts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceDiscount commerceDiscount : list) {
					if (!uuid.equals(commerceDiscount.getUuid())) {
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

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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
				sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the first commerce discount in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByUuid_First(
			String uuid, OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByUuid_First(
			uuid, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the first commerce discount in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByUuid_First(
		String uuid, OrderByComparator<CommerceDiscount> orderByComparator) {

		List<CommerceDiscount> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce discount in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByUuid_Last(
			String uuid, OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByUuid_Last(
			uuid, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the last commerce discount in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByUuid_Last(
		String uuid, OrderByComparator<CommerceDiscount> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CommerceDiscount> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set where uuid = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] findByUuid_PrevAndNext(
			long commerceDiscountId, String uuid,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		uuid = Objects.toString(uuid, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, commerceDiscount, uuid, orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = getByUuid_PrevAndNext(
				session, commerceDiscount, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceDiscount getByUuid_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, String uuid,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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
			sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
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
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid(String uuid) {
		return filterFindByUuid(
			uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid(
		String uuid, int start, int end) {

		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
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
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			return (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set of commerce discounts that the user has permission to view where uuid = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] filterFindByUuid_PrevAndNext(
			long commerceDiscountId, String uuid,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid_PrevAndNext(
				commerceDiscountId, uuid, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = filterGetByUuid_PrevAndNext(
				session, commerceDiscount, uuid, orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = filterGetByUuid_PrevAndNext(
				session, commerceDiscount, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceDiscount filterGetByUuid_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, String uuid,
		OrderByComparator<CommerceDiscount> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
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
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce discounts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CommerceDiscount commerceDiscount :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

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
	 * Returns the number of commerce discounts that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUuid(uuid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceDiscount> commerceDiscounts = findByUuid(uuid);

			commerceDiscounts = InlineSQLHelperUtil.filter(commerceDiscounts);

			return commerceDiscounts.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
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
		"commerceDiscount.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(commerceDiscount.uuid IS NULL OR commerceDiscount.uuid = '')";

	private static final String _FINDER_COLUMN_UUID_UUID_2_SQL =
		"commerceDiscount.uuid_ = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3_SQL =
		"(commerceDiscount.uuid_ IS NULL OR commerceDiscount.uuid_ = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the commerce discounts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceDiscount commerceDiscount : list) {
					if (!uuid.equals(commerceDiscount.getUuid()) ||
						(companyId != commerceDiscount.getCompanyId())) {

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

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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
				sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the first commerce discount in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the first commerce discount in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		List<CommerceDiscount> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce discount in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the last commerce discount in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CommerceDiscount> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] findByUuid_C_PrevAndNext(
			long commerceDiscountId, String uuid, long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		uuid = Objects.toString(uuid, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, commerceDiscount, uuid, companyId, orderByComparator,
				true);

			array[1] = commerceDiscount;

			array[2] = getByUuid_C_PrevAndNext(
				session, commerceDiscount, uuid, companyId, orderByComparator,
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

	protected CommerceDiscount getByUuid_C_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, String uuid,
		long companyId, OrderByComparator<CommerceDiscount> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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
			sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
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
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid_C(
		String uuid, long companyId) {

		return filterFindByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
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
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

			return (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set of commerce discounts that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] filterFindByUuid_C_PrevAndNext(
			long commerceDiscountId, String uuid, long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C_PrevAndNext(
				commerceDiscountId, uuid, companyId, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = filterGetByUuid_C_PrevAndNext(
				session, commerceDiscount, uuid, companyId, orderByComparator,
				true);

			array[1] = commerceDiscount;

			array[2] = filterGetByUuid_C_PrevAndNext(
				session, commerceDiscount, uuid, companyId, orderByComparator,
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

	protected CommerceDiscount filterGetByUuid_C_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, String uuid,
		long companyId, OrderByComparator<CommerceDiscount> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
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
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce discounts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CommerceDiscount commerceDiscount :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

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
	 * Returns the number of commerce discounts that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByUuid_C(uuid, companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceDiscount> commerceDiscounts = findByUuid_C(
				uuid, companyId);

			commerceDiscounts = InlineSQLHelperUtil.filter(commerceDiscounts);

			return commerceDiscounts.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

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
			sb.toString(), CommerceDiscount.class.getName(),
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
		"commerceDiscount.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(commerceDiscount.uuid IS NULL OR commerceDiscount.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_2_SQL =
		"commerceDiscount.uuid_ = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3_SQL =
		"(commerceDiscount.uuid_ IS NULL OR commerceDiscount.uuid_ = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"commerceDiscount.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the commerce discounts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceDiscount commerceDiscount : list) {
					if (companyId != commerceDiscount.getCompanyId()) {
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

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the first commerce discount in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByCompanyId_First(
		long companyId, OrderByComparator<CommerceDiscount> orderByComparator) {

		List<CommerceDiscount> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce discount in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByCompanyId_Last(
			long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the last commerce discount in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByCompanyId_Last(
		long companyId, OrderByComparator<CommerceDiscount> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CommerceDiscount> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set where companyId = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] findByCompanyId_PrevAndNext(
			long commerceDiscountId, long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, commerceDiscount, companyId, orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = getByCompanyId_PrevAndNext(
				session, commerceDiscount, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceDiscount getByCompanyId_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, long companyId,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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
			sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
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
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set of commerce discounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] filterFindByCompanyId_PrevAndNext(
			long commerceDiscountId, long companyId,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				commerceDiscountId, companyId, orderByComparator);
		}

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, commerceDiscount, companyId, orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, commerceDiscount, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceDiscount filterGetByCompanyId_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, long companyId,
		OrderByComparator<CommerceDiscount> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce discounts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CommerceDiscount commerceDiscount :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

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
	 * Returns the number of commerce discounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceDiscount> commerceDiscounts = findByCompanyId(
				companyId);

			commerceDiscounts = InlineSQLHelperUtil.filter(commerceDiscounts);

			return commerceDiscounts.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
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
		"commerceDiscount.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the commerce discounts where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_C(long companyId, String couponCode) {
		return findByC_C(
			companyId, couponCode, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where companyId = &#63; and couponCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_C(
		long companyId, String couponCode, int start, int end) {

		return findByC_C(companyId, couponCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63; and couponCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_C(
		long companyId, String couponCode, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByC_C(
			companyId, couponCode, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63; and couponCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_C(
		long companyId, String couponCode, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		couponCode = Objects.toString(couponCode, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_C;
				finderArgs = new Object[] {companyId, couponCode};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_C;
			finderArgs = new Object[] {
				companyId, couponCode, start, end, orderByComparator
			};
		}

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceDiscount commerceDiscount : list) {
					if ((companyId != commerceDiscount.getCompanyId()) ||
						!couponCode.equals(commerceDiscount.getCouponCode())) {

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

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

			boolean bindCouponCode = false;

			if (couponCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_C_COUPONCODE_3);
			}
			else {
				bindCouponCode = true;

				sb.append(_FINDER_COLUMN_C_C_COUPONCODE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindCouponCode) {
					queryPos.add(StringUtil.toLowerCase(couponCode));
				}

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_C_First(
			long companyId, String couponCode,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByC_C_First(
			companyId, couponCode, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", couponCode=");
		sb.append(couponCode);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_C_First(
		long companyId, String couponCode,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		List<CommerceDiscount> list = findByC_C(
			companyId, couponCode, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce discount in the ordered set where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_C_Last(
			long companyId, String couponCode,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByC_C_Last(
			companyId, couponCode, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", couponCode=");
		sb.append(couponCode);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the last commerce discount in the ordered set where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_C_Last(
		long companyId, String couponCode,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		int count = countByC_C(companyId, couponCode);

		if (count == 0) {
			return null;
		}

		List<CommerceDiscount> list = findByC_C(
			companyId, couponCode, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] findByC_C_PrevAndNext(
			long commerceDiscountId, long companyId, String couponCode,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		couponCode = Objects.toString(couponCode, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, commerceDiscount, companyId, couponCode,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = getByC_C_PrevAndNext(
				session, commerceDiscount, companyId, couponCode,
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

	protected CommerceDiscount getByC_C_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, long companyId,
		String couponCode,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCouponCode = false;

		if (couponCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_3);
		}
		else {
			bindCouponCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_2);
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
			sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindCouponCode) {
			queryPos.add(StringUtil.toLowerCase(couponCode));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_C(
		long companyId, String couponCode) {

		return filterFindByC_C(
			companyId, couponCode, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where companyId = &#63; and couponCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_C(
		long companyId, String couponCode, int start, int end) {

		return filterFindByC_C(companyId, couponCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where companyId = &#63; and couponCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_C(
		long companyId, String couponCode, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C(
				companyId, couponCode, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_C(
					companyId, couponCode, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		couponCode = Objects.toString(couponCode, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCouponCode = false;

		if (couponCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_3);
		}
		else {
			bindCouponCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindCouponCode) {
				queryPos.add(StringUtil.toLowerCase(couponCode));
			}

			return (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set of commerce discounts that the user has permission to view where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] filterFindByC_C_PrevAndNext(
			long commerceDiscountId, long companyId, String couponCode,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_PrevAndNext(
				commerceDiscountId, companyId, couponCode, orderByComparator);
		}

		couponCode = Objects.toString(couponCode, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = filterGetByC_C_PrevAndNext(
				session, commerceDiscount, companyId, couponCode,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = filterGetByC_C_PrevAndNext(
				session, commerceDiscount, companyId, couponCode,
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

	protected CommerceDiscount filterGetByC_C_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, long companyId,
		String couponCode,
		OrderByComparator<CommerceDiscount> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCouponCode = false;

		if (couponCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_3);
		}
		else {
			bindCouponCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindCouponCode) {
			queryPos.add(StringUtil.toLowerCase(couponCode));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce discounts where companyId = &#63; and couponCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 */
	@Override
	public void removeByC_C(long companyId, String couponCode) {
		for (CommerceDiscount commerceDiscount :
				findByC_C(
					companyId, couponCode, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByC_C(long companyId, String couponCode) {
		couponCode = Objects.toString(couponCode, "");

		FinderPath finderPath = _finderPathCountByC_C;

		Object[] finderArgs = new Object[] {companyId, couponCode};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

			boolean bindCouponCode = false;

			if (couponCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_C_COUPONCODE_3);
			}
			else {
				bindCouponCode = true;

				sb.append(_FINDER_COLUMN_C_C_COUPONCODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindCouponCode) {
					queryPos.add(StringUtil.toLowerCase(couponCode));
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
	 * Returns the number of commerce discounts that the user has permission to view where companyId = &#63; and couponCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByC_C(long companyId, String couponCode) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_C(companyId, couponCode);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceDiscount> commerceDiscounts = findByC_C(
				companyId, couponCode);

			commerceDiscounts = InlineSQLHelperUtil.filter(commerceDiscounts);

			return commerceDiscounts.size();
		}

		couponCode = Objects.toString(couponCode, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		boolean bindCouponCode = false;

		if (couponCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_3);
		}
		else {
			bindCouponCode = true;

			sb.append(_FINDER_COLUMN_C_C_COUPONCODE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindCouponCode) {
				queryPos.add(StringUtil.toLowerCase(couponCode));
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
		"commerceDiscount.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_COUPONCODE_2 =
		"lower(commerceDiscount.couponCode) = ?";

	private static final String _FINDER_COLUMN_C_C_COUPONCODE_3 =
		"(commerceDiscount.couponCode IS NULL OR commerceDiscount.couponCode = '')";

	private FinderPath _finderPathWithPaginationFindByLtD_S;
	private FinderPath _finderPathWithPaginationCountByLtD_S;

	/**
	 * Returns all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtD_S;
		finderArgs = new Object[] {
			_getTime(displayDate), status, start, end, orderByComparator
		};

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceDiscount commerceDiscount : list) {
					if ((displayDate.getTime() <=
							commerceDiscount.getDisplayDate(
							).getTime()) ||
						(status != commerceDiscount.getStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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
				sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the first commerce discount in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByLtD_S_First(
			displayDate, status, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the first commerce discount in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		List<CommerceDiscount> list = findByLtD_S(
			displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce discount in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByLtD_S_Last(
			Date displayDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByLtD_S_Last(
			displayDate, status, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the last commerce discount in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByLtD_S_Last(
		Date displayDate, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		int count = countByLtD_S(displayDate, status);

		if (count == 0) {
			return null;
		}

		List<CommerceDiscount> list = findByLtD_S(
			displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] findByLtD_S_PrevAndNext(
			long commerceDiscountId, Date displayDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = getByLtD_S_PrevAndNext(
				session, commerceDiscount, displayDate, status,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = getByLtD_S_PrevAndNext(
				session, commerceDiscount, displayDate, status,
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

	protected CommerceDiscount getByLtD_S_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, Date displayDate,
		int status, OrderByComparator<CommerceDiscount> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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
			sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtD_S(
		Date displayDate, int status) {

		return filterFindByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtD_S(
		Date displayDate, int status, int start, int end) {

		return filterFindByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtD_S(
				displayDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtD_S(
					displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set of commerce discounts that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] filterFindByLtD_S_PrevAndNext(
			long commerceDiscountId, Date displayDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtD_S_PrevAndNext(
				commerceDiscountId, displayDate, status, orderByComparator);
		}

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = filterGetByLtD_S_PrevAndNext(
				session, commerceDiscount, displayDate, status,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = filterGetByLtD_S_PrevAndNext(
				session, commerceDiscount, displayDate, status,
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

	protected CommerceDiscount filterGetByLtD_S_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, Date displayDate,
		int status, OrderByComparator<CommerceDiscount> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce discounts where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		for (CommerceDiscount commerceDiscount :
				findByLtD_S(
					displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		FinderPath finderPath = _finderPathWithPaginationCountByLtD_S;

		Object[] finderArgs = new Object[] {_getTime(displayDate), status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

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

	/**
	 * Returns the number of commerce discounts that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByLtD_S(Date displayDate, int status) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtD_S(displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceDiscount> commerceDiscounts = findByLtD_S(
				displayDate, status);

			commerceDiscounts = InlineSQLHelperUtil.filter(commerceDiscounts);

			return commerceDiscounts.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

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

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_1 =
		"commerceDiscount.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_2 =
		"commerceDiscount.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_LTD_S_STATUS_2 =
		"commerceDiscount.status = ?";

	private FinderPath _finderPathWithPaginationFindByLtE_S;
	private FinderPath _finderPathWithPaginationCountByLtE_S;

	/**
	 * Returns all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(Date expirationDate, int status) {
		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtE_S;
		finderArgs = new Object[] {
			_getTime(expirationDate), status, start, end, orderByComparator
		};

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceDiscount commerceDiscount : list) {
					if ((expirationDate.getTime() <=
							commerceDiscount.getExpirationDate(
							).getTime()) ||
						(status != commerceDiscount.getStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

			boolean bindExpirationDate = false;

			if (expirationDate == null) {
				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
			}
			else {
				bindExpirationDate = true;

				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExpirationDate) {
					queryPos.add(new Timestamp(expirationDate.getTime()));
				}

				queryPos.add(status);

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the first commerce discount in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByLtE_S_First(
			expirationDate, status, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("expirationDate<");
		sb.append(expirationDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the first commerce discount in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		List<CommerceDiscount> list = findByLtE_S(
			expirationDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce discount in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByLtE_S_Last(
			Date expirationDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByLtE_S_Last(
			expirationDate, status, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("expirationDate<");
		sb.append(expirationDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the last commerce discount in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByLtE_S_Last(
		Date expirationDate, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		int count = countByLtE_S(expirationDate, status);

		if (count == 0) {
			return null;
		}

		List<CommerceDiscount> list = findByLtE_S(
			expirationDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] findByLtE_S_PrevAndNext(
			long commerceDiscountId, Date expirationDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = getByLtE_S_PrevAndNext(
				session, commerceDiscount, expirationDate, status,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = getByLtE_S_PrevAndNext(
				session, commerceDiscount, expirationDate, status,
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

	protected CommerceDiscount getByLtE_S_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, Date expirationDate,
		int status, OrderByComparator<CommerceDiscount> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

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
			sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindExpirationDate) {
			queryPos.add(new Timestamp(expirationDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtE_S(
		Date expirationDate, int status) {

		return filterFindByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return filterFindByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtE_S(
				expirationDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtE_S(
					expirationDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindExpirationDate) {
				queryPos.add(new Timestamp(expirationDate.getTime()));
			}

			queryPos.add(status);

			return (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set of commerce discounts that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] filterFindByLtE_S_PrevAndNext(
			long commerceDiscountId, Date expirationDate, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtE_S_PrevAndNext(
				commerceDiscountId, expirationDate, status, orderByComparator);
		}

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = filterGetByLtE_S_PrevAndNext(
				session, commerceDiscount, expirationDate, status,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = filterGetByLtE_S_PrevAndNext(
				session, commerceDiscount, expirationDate, status,
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

	protected CommerceDiscount filterGetByLtE_S_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, Date expirationDate,
		int status, OrderByComparator<CommerceDiscount> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindExpirationDate) {
			queryPos.add(new Timestamp(expirationDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce discounts where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	@Override
	public void removeByLtE_S(Date expirationDate, int status) {
		for (CommerceDiscount commerceDiscount :
				findByLtE_S(
					expirationDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		FinderPath finderPath = _finderPathWithPaginationCountByLtE_S;

		Object[] finderArgs = new Object[] {_getTime(expirationDate), status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

			boolean bindExpirationDate = false;

			if (expirationDate == null) {
				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
			}
			else {
				bindExpirationDate = true;

				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExpirationDate) {
					queryPos.add(new Timestamp(expirationDate.getTime()));
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

	/**
	 * Returns the number of commerce discounts that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByLtE_S(Date expirationDate, int status) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtE_S(expirationDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceDiscount> commerceDiscounts = findByLtE_S(
				expirationDate, status);

			commerceDiscounts = InlineSQLHelperUtil.filter(commerceDiscounts);

			return commerceDiscounts.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindExpirationDate) {
				queryPos.add(new Timestamp(expirationDate.getTime()));
			}

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

	private static final String _FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1 =
		"commerceDiscount.expirationDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2 =
		"commerceDiscount.expirationDate < ? AND ";

	private static final String _FINDER_COLUMN_LTE_S_STATUS_2 =
		"commerceDiscount.status = ?";

	private FinderPath _finderPathFetchByC_C_A;

	/**
	 * Returns the commerce discount where companyId = &#63; and couponCode = &#63; and active = &#63; or throws a <code>NoSuchDiscountException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @return the matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_C_A(
			long companyId, String couponCode, boolean active)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByC_C_A(
			companyId, couponCode, active);

		if (commerceDiscount == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", couponCode=");
			sb.append(couponCode);

			sb.append(", active=");
			sb.append(active);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchDiscountException(sb.toString());
		}

		return commerceDiscount;
	}

	/**
	 * Returns the commerce discount where companyId = &#63; and couponCode = &#63; and active = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_C_A(
		long companyId, String couponCode, boolean active) {

		return fetchByC_C_A(companyId, couponCode, active, true);
	}

	/**
	 * Returns the commerce discount where companyId = &#63; and couponCode = &#63; and active = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_C_A(
		long companyId, String couponCode, boolean active,
		boolean useFinderCache) {

		couponCode = Objects.toString(couponCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, couponCode, active};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_C_A, finderArgs, this);
		}

		if (result instanceof CommerceDiscount) {
			CommerceDiscount commerceDiscount = (CommerceDiscount)result;

			if ((companyId != commerceDiscount.getCompanyId()) ||
				!Objects.equals(couponCode, commerceDiscount.getCouponCode()) ||
				(active != commerceDiscount.isActive())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_C_C_A_COMPANYID_2);

			boolean bindCouponCode = false;

			if (couponCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_C_A_COUPONCODE_3);
			}
			else {
				bindCouponCode = true;

				sb.append(_FINDER_COLUMN_C_C_A_COUPONCODE_2);
			}

			sb.append(_FINDER_COLUMN_C_C_A_ACTIVE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindCouponCode) {
					queryPos.add(StringUtil.toLowerCase(couponCode));
				}

				queryPos.add(active);

				List<CommerceDiscount> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_C_A, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									companyId, couponCode, active
								};
							}

							_log.warn(
								"CommerceDiscountPersistenceImpl.fetchByC_C_A(long, String, boolean, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					CommerceDiscount commerceDiscount = list.get(0);

					result = commerceDiscount;

					cacheResult(commerceDiscount);
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
			return (CommerceDiscount)result;
		}
	}

	/**
	 * Removes the commerce discount where companyId = &#63; and couponCode = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @return the commerce discount that was removed
	 */
	@Override
	public CommerceDiscount removeByC_C_A(
			long companyId, String couponCode, boolean active)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = findByC_C_A(
			companyId, couponCode, active);

		return remove(commerceDiscount);
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63; and couponCode = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param couponCode the coupon code
	 * @param active the active
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByC_C_A(long companyId, String couponCode, boolean active) {
		CommerceDiscount commerceDiscount = fetchByC_C_A(
			companyId, couponCode, active);

		if (commerceDiscount == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_C_A_COMPANYID_2 =
		"commerceDiscount.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_A_COUPONCODE_2 =
		"lower(commerceDiscount.couponCode) = ? AND ";

	private static final String _FINDER_COLUMN_C_C_A_COUPONCODE_3 =
		"(commerceDiscount.couponCode IS NULL OR commerceDiscount.couponCode = '') AND ";

	private static final String _FINDER_COLUMN_C_C_A_ACTIVE_2 =
		"commerceDiscount.active = ?";

	private FinderPath _finderPathWithPaginationFindByC_L_A_S;
	private FinderPath _finderPathWithoutPaginationFindByC_L_A_S;
	private FinderPath _finderPathCountByC_L_A_S;

	/**
	 * Returns all the commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @return the matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		return findByC_L_A_S(
			companyId, level, active, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_L_A_S(
		long companyId, String level, boolean active, int status, int start,
		int end) {

		return findByC_L_A_S(
			companyId, level, active, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_L_A_S(
		long companyId, String level, boolean active, int status, int start,
		int end, OrderByComparator<CommerceDiscount> orderByComparator) {

		return findByC_L_A_S(
			companyId, level, active, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findByC_L_A_S(
		long companyId, String level, boolean active, int status, int start,
		int end, OrderByComparator<CommerceDiscount> orderByComparator,
		boolean useFinderCache) {

		level = Objects.toString(level, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_L_A_S;
				finderArgs = new Object[] {companyId, level, active, status};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_L_A_S;
			finderArgs = new Object[] {
				companyId, level, active, status, start, end, orderByComparator
			};
		}

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceDiscount commerceDiscount : list) {
					if ((companyId != commerceDiscount.getCompanyId()) ||
						!level.equals(commerceDiscount.getLevel()) ||
						(active != commerceDiscount.isActive()) ||
						(status != commerceDiscount.getStatus())) {

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
					6 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(6);
			}

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_C_L_A_S_COMPANYID_2);

			boolean bindLevel = false;

			if (level.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_3);
			}
			else {
				bindLevel = true;

				sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_2);
			}

			sb.append(_FINDER_COLUMN_C_L_A_S_ACTIVE_2);

			sb.append(_FINDER_COLUMN_C_L_A_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindLevel) {
					queryPos.add(level);
				}

				queryPos.add(active);

				queryPos.add(status);

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_L_A_S_First(
			long companyId, String level, boolean active, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByC_L_A_S_First(
			companyId, level, active, status, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", level=");
		sb.append(level);

		sb.append(", active=");
		sb.append(active);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the first commerce discount in the ordered set where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_L_A_S_First(
		long companyId, String level, boolean active, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		List<CommerceDiscount> list = findByC_L_A_S(
			companyId, level, active, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce discount in the ordered set where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByC_L_A_S_Last(
			long companyId, String level, boolean active, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByC_L_A_S_Last(
			companyId, level, active, status, orderByComparator);

		if (commerceDiscount != null) {
			return commerceDiscount;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", level=");
		sb.append(level);

		sb.append(", active=");
		sb.append(active);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchDiscountException(sb.toString());
	}

	/**
	 * Returns the last commerce discount in the ordered set where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByC_L_A_S_Last(
		long companyId, String level, boolean active, int status,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		int count = countByC_L_A_S(companyId, level, active, status);

		if (count == 0) {
			return null;
		}

		List<CommerceDiscount> list = findByC_L_A_S(
			companyId, level, active, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] findByC_L_A_S_PrevAndNext(
			long commerceDiscountId, long companyId, String level,
			boolean active, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		level = Objects.toString(level, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = getByC_L_A_S_PrevAndNext(
				session, commerceDiscount, companyId, level, active, status,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = getByC_L_A_S_PrevAndNext(
				session, commerceDiscount, companyId, level, active, status,
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

	protected CommerceDiscount getByC_L_A_S_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, long companyId,
		String level, boolean active, int status,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_C_L_A_S_COMPANYID_2);

		boolean bindLevel = false;

		if (level.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_3);
		}
		else {
			bindLevel = true;

			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_2);
		}

		sb.append(_FINDER_COLUMN_C_L_A_S_ACTIVE_2);

		sb.append(_FINDER_COLUMN_C_L_A_S_STATUS_2);

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
			sb.append(CommerceDiscountModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindLevel) {
			queryPos.add(level);
		}

		queryPos.add(active);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce discounts that the user has permission to view where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @return the matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		return filterFindByC_L_A_S(
			companyId, level, active, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts that the user has permission to view where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_L_A_S(
		long companyId, String level, boolean active, int status, int start,
		int end) {

		return filterFindByC_L_A_S(
			companyId, level, active, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts that the user has permissions to view where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discounts that the user has permission to view
	 */
	@Override
	public List<CommerceDiscount> filterFindByC_L_A_S(
		long companyId, String level, boolean active, int status, int start,
		int end, OrderByComparator<CommerceDiscount> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_L_A_S(
				companyId, level, active, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_L_A_S(
					companyId, level, active, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		level = Objects.toString(level, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_L_A_S_COMPANYID_2);

		boolean bindLevel = false;

		if (level.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_3_SQL);
		}
		else {
			bindLevel = true;

			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_2_SQL);
		}

		sb.append(_FINDER_COLUMN_C_L_A_S_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_C_L_A_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindLevel) {
				queryPos.add(level);
			}

			queryPos.add(active);

			queryPos.add(status);

			return (List<CommerceDiscount>)QueryUtil.list(
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
	 * Returns the commerce discounts before and after the current commerce discount in the ordered set of commerce discounts that the user has permission to view where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param commerceDiscountId the primary key of the current commerce discount
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount[] filterFindByC_L_A_S_PrevAndNext(
			long commerceDiscountId, long companyId, String level,
			boolean active, int status,
			OrderByComparator<CommerceDiscount> orderByComparator)
		throws NoSuchDiscountException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_L_A_S_PrevAndNext(
				commerceDiscountId, companyId, level, active, status,
				orderByComparator);
		}

		level = Objects.toString(level, "");

		CommerceDiscount commerceDiscount = findByPrimaryKey(
			commerceDiscountId);

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount[] array = new CommerceDiscountImpl[3];

			array[0] = filterGetByC_L_A_S_PrevAndNext(
				session, commerceDiscount, companyId, level, active, status,
				orderByComparator, true);

			array[1] = commerceDiscount;

			array[2] = filterGetByC_L_A_S_PrevAndNext(
				session, commerceDiscount, companyId, level, active, status,
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

	protected CommerceDiscount filterGetByC_L_A_S_PrevAndNext(
		Session session, CommerceDiscount commerceDiscount, long companyId,
		String level, boolean active, int status,
		OrderByComparator<CommerceDiscount> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_L_A_S_COMPANYID_2);

		boolean bindLevel = false;

		if (level.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_3_SQL);
		}
		else {
			bindLevel = true;

			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_2_SQL);
		}

		sb.append(_FINDER_COLUMN_C_L_A_S_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_C_L_A_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceDiscountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceDiscountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceDiscountImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceDiscountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindLevel) {
			queryPos.add(level);
		}

		queryPos.add(active);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceDiscount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceDiscount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 */
	@Override
	public void removeByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		for (CommerceDiscount commerceDiscount :
				findByC_L_A_S(
					companyId, level, active, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		level = Objects.toString(level, "");

		FinderPath finderPath = _finderPathCountByC_L_A_S;

		Object[] finderArgs = new Object[] {companyId, level, active, status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_C_L_A_S_COMPANYID_2);

			boolean bindLevel = false;

			if (level.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_3);
			}
			else {
				bindLevel = true;

				sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_2);
			}

			sb.append(_FINDER_COLUMN_C_L_A_S_ACTIVE_2);

			sb.append(_FINDER_COLUMN_C_L_A_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindLevel) {
					queryPos.add(level);
				}

				queryPos.add(active);

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

	/**
	 * Returns the number of commerce discounts that the user has permission to view where companyId = &#63; and level = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param level the level
	 * @param active the active
	 * @param status the status
	 * @return the number of matching commerce discounts that the user has permission to view
	 */
	@Override
	public int filterCountByC_L_A_S(
		long companyId, String level, boolean active, int status) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_L_A_S(companyId, level, active, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceDiscount> commerceDiscounts = findByC_L_A_S(
				companyId, level, active, status);

			commerceDiscounts = InlineSQLHelperUtil.filter(commerceDiscounts);

			return commerceDiscounts.size();
		}

		level = Objects.toString(level, "");

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_C_L_A_S_COMPANYID_2);

		boolean bindLevel = false;

		if (level.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_3_SQL);
		}
		else {
			bindLevel = true;

			sb.append(_FINDER_COLUMN_C_L_A_S_LEVEL_2_SQL);
		}

		sb.append(_FINDER_COLUMN_C_L_A_S_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_C_L_A_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceDiscount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindLevel) {
				queryPos.add(level);
			}

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_C_L_A_S_COMPANYID_2 =
		"commerceDiscount.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_L_A_S_LEVEL_2 =
		"commerceDiscount.level = ? AND ";

	private static final String _FINDER_COLUMN_C_L_A_S_LEVEL_3 =
		"(commerceDiscount.level IS NULL OR commerceDiscount.level = '') AND ";

	private static final String _FINDER_COLUMN_C_L_A_S_LEVEL_2_SQL =
		"commerceDiscount.levelType = ? AND ";

	private static final String _FINDER_COLUMN_C_L_A_S_LEVEL_3_SQL =
		"(commerceDiscount.levelType IS NULL OR commerceDiscount.levelType = '') AND ";

	private static final String _FINDER_COLUMN_C_L_A_S_ACTIVE_2 =
		"commerceDiscount.active = ? AND ";

	private static final String _FINDER_COLUMN_C_L_A_S_ACTIVE_2_SQL =
		"commerceDiscount.active_ = ? AND ";

	private static final String _FINDER_COLUMN_C_L_A_S_STATUS_2 =
		"commerceDiscount.status = ?";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the commerce discount where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchDiscountException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce discount
	 * @throws NoSuchDiscountException if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceDiscount == null) {
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

			throw new NoSuchDiscountException(sb.toString());
		}

		return commerceDiscount;
	}

	/**
	 * Returns the commerce discount where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the commerce discount where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public CommerceDiscount fetchByERC_C(
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

		if (result instanceof CommerceDiscount) {
			CommerceDiscount commerceDiscount = (CommerceDiscount)result;

			if (!Objects.equals(
					externalReferenceCode,
					commerceDiscount.getExternalReferenceCode()) ||
				(companyId != commerceDiscount.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCEDISCOUNT_WHERE);

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

				List<CommerceDiscount> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					CommerceDiscount commerceDiscount = list.get(0);

					result = commerceDiscount;

					cacheResult(commerceDiscount);
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
			return (CommerceDiscount)result;
		}
	}

	/**
	 * Removes the commerce discount where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce discount that was removed
	 */
	@Override
	public CommerceDiscount removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceDiscount);
	}

	/**
	 * Returns the number of commerce discounts where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce discounts
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CommerceDiscount commerceDiscount = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceDiscount == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"commerceDiscount.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(commerceDiscount.externalReferenceCode IS NULL OR commerceDiscount.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"commerceDiscount.companyId = ?";

	public CommerceDiscountPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("level", "levelType");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceDiscount.class);

		setModelImplClass(CommerceDiscountImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceDiscountTable.INSTANCE);
	}

	/**
	 * Caches the commerce discount in the entity cache if it is enabled.
	 *
	 * @param commerceDiscount the commerce discount
	 */
	@Override
	public void cacheResult(CommerceDiscount commerceDiscount) {
		entityCache.putResult(
			CommerceDiscountImpl.class, commerceDiscount.getPrimaryKey(),
			commerceDiscount);

		finderCache.putResult(
			_finderPathFetchByC_C_A,
			new Object[] {
				commerceDiscount.getCompanyId(),
				commerceDiscount.getCouponCode(), commerceDiscount.isActive()
			},
			commerceDiscount);

		finderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				commerceDiscount.getExternalReferenceCode(),
				commerceDiscount.getCompanyId()
			},
			commerceDiscount);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce discounts in the entity cache if it is enabled.
	 *
	 * @param commerceDiscounts the commerce discounts
	 */
	@Override
	public void cacheResult(List<CommerceDiscount> commerceDiscounts) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commerceDiscounts.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommerceDiscount commerceDiscount : commerceDiscounts) {
			if (entityCache.getResult(
					CommerceDiscountImpl.class,
					commerceDiscount.getPrimaryKey()) == null) {

				cacheResult(commerceDiscount);
			}
		}
	}

	/**
	 * Clears the cache for all commerce discounts.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommerceDiscountImpl.class);

		finderCache.clearCache(CommerceDiscountImpl.class);
	}

	/**
	 * Clears the cache for the commerce discount.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CommerceDiscount commerceDiscount) {
		entityCache.removeResult(CommerceDiscountImpl.class, commerceDiscount);
	}

	@Override
	public void clearCache(List<CommerceDiscount> commerceDiscounts) {
		for (CommerceDiscount commerceDiscount : commerceDiscounts) {
			entityCache.removeResult(
				CommerceDiscountImpl.class, commerceDiscount);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommerceDiscountImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CommerceDiscountImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CommerceDiscountModelImpl commerceDiscountModelImpl) {

		Object[] args = new Object[] {
			commerceDiscountModelImpl.getCompanyId(),
			commerceDiscountModelImpl.getCouponCode(),
			commerceDiscountModelImpl.isActive()
		};

		finderCache.putResult(
			_finderPathFetchByC_C_A, args, commerceDiscountModelImpl);

		args = new Object[] {
			commerceDiscountModelImpl.getExternalReferenceCode(),
			commerceDiscountModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C, args, commerceDiscountModelImpl);
	}

	/**
	 * Creates a new commerce discount with the primary key. Does not add the commerce discount to the database.
	 *
	 * @param commerceDiscountId the primary key for the new commerce discount
	 * @return the new commerce discount
	 */
	@Override
	public CommerceDiscount create(long commerceDiscountId) {
		CommerceDiscount commerceDiscount = new CommerceDiscountImpl();

		commerceDiscount.setNew(true);
		commerceDiscount.setPrimaryKey(commerceDiscountId);

		String uuid = PortalUUIDUtil.generate();

		commerceDiscount.setUuid(uuid);

		commerceDiscount.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceDiscount;
	}

	/**
	 * Removes the commerce discount with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount that was removed
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount remove(long commerceDiscountId)
		throws NoSuchDiscountException {

		return remove((Serializable)commerceDiscountId);
	}

	/**
	 * Removes the commerce discount with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce discount
	 * @return the commerce discount that was removed
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount remove(Serializable primaryKey)
		throws NoSuchDiscountException {

		Session session = null;

		try {
			session = openSession();

			CommerceDiscount commerceDiscount = (CommerceDiscount)session.get(
				CommerceDiscountImpl.class, primaryKey);

			if (commerceDiscount == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchDiscountException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commerceDiscount);
		}
		catch (NoSuchDiscountException noSuchEntityException) {
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
	protected CommerceDiscount removeImpl(CommerceDiscount commerceDiscount) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceDiscount)) {
				commerceDiscount = (CommerceDiscount)session.get(
					CommerceDiscountImpl.class,
					commerceDiscount.getPrimaryKeyObj());
			}

			if (commerceDiscount != null) {
				session.delete(commerceDiscount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceDiscount != null) {
			clearCache(commerceDiscount);
		}

		return commerceDiscount;
	}

	@Override
	public CommerceDiscount updateImpl(CommerceDiscount commerceDiscount) {
		boolean isNew = commerceDiscount.isNew();

		if (!(commerceDiscount instanceof CommerceDiscountModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceDiscount.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceDiscount);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceDiscount proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceDiscount implementation " +
					commerceDiscount.getClass());
		}

		CommerceDiscountModelImpl commerceDiscountModelImpl =
			(CommerceDiscountModelImpl)commerceDiscount;

		if (Validator.isNull(commerceDiscount.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceDiscount.setUuid(uuid);
		}

		if (Validator.isNull(commerceDiscount.getExternalReferenceCode())) {
			commerceDiscount.setExternalReferenceCode(
				commerceDiscount.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceDiscountModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceDiscount.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceDiscount.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceDiscount.getPrimaryKey();
					}

					try {
						commerceDiscount.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceDiscount.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceDiscount.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceDiscount ercCommerceDiscount = fetchByERC_C(
				commerceDiscount.getExternalReferenceCode(),
				commerceDiscount.getCompanyId());

			if (isNew) {
				if (ercCommerceDiscount != null) {
					throw new DuplicateCommerceDiscountExternalReferenceCodeException(
						"Duplicate commerce discount with external reference code " +
							commerceDiscount.getExternalReferenceCode() +
								" and company " +
									commerceDiscount.getCompanyId());
				}
			}
			else {
				if ((ercCommerceDiscount != null) &&
					(commerceDiscount.getCommerceDiscountId() !=
						ercCommerceDiscount.getCommerceDiscountId())) {

					throw new DuplicateCommerceDiscountExternalReferenceCodeException(
						"Duplicate commerce discount with external reference code " +
							commerceDiscount.getExternalReferenceCode() +
								" and company " +
									commerceDiscount.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceDiscount.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceDiscount.setCreateDate(date);
			}
			else {
				commerceDiscount.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceDiscountModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceDiscount.setModifiedDate(date);
			}
			else {
				commerceDiscount.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceDiscount);
			}
			else {
				commerceDiscount = (CommerceDiscount)session.merge(
					commerceDiscount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommerceDiscountImpl.class, commerceDiscountModelImpl, false, true);

		cacheUniqueFindersCache(commerceDiscountModelImpl);

		if (isNew) {
			commerceDiscount.setNew(false);
		}

		commerceDiscount.resetOriginalValues();

		return commerceDiscount;
	}

	/**
	 * Returns the commerce discount with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce discount
	 * @return the commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount findByPrimaryKey(Serializable primaryKey)
		throws NoSuchDiscountException {

		CommerceDiscount commerceDiscount = fetchByPrimaryKey(primaryKey);

		if (commerceDiscount == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchDiscountException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commerceDiscount;
	}

	/**
	 * Returns the commerce discount with the primary key or throws a <code>NoSuchDiscountException</code> if it could not be found.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount
	 * @throws NoSuchDiscountException if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount findByPrimaryKey(long commerceDiscountId)
		throws NoSuchDiscountException {

		return findByPrimaryKey((Serializable)commerceDiscountId);
	}

	/**
	 * Returns the commerce discount with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount, or <code>null</code> if a commerce discount with the primary key could not be found
	 */
	@Override
	public CommerceDiscount fetchByPrimaryKey(long commerceDiscountId) {
		return fetchByPrimaryKey((Serializable)commerceDiscountId);
	}

	/**
	 * Returns all the commerce discounts.
	 *
	 * @return the commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce discounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce discounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findAll(
		int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce discounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce discounts
	 */
	@Override
	public List<CommerceDiscount> findAll(
		int start, int end,
		OrderByComparator<CommerceDiscount> orderByComparator,
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

		List<CommerceDiscount> list = null;

		if (useFinderCache) {
			list = (List<CommerceDiscount>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCEDISCOUNT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCEDISCOUNT;

				sql = sql.concat(CommerceDiscountModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommerceDiscount>)QueryUtil.list(
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
	 * Removes all the commerce discounts from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommerceDiscount commerceDiscount : findAll()) {
			remove(commerceDiscount);
		}
	}

	/**
	 * Returns the number of commerce discounts.
	 *
	 * @return the number of commerce discounts
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_COMMERCEDISCOUNT);

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
		return "commerceDiscountId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEDISCOUNT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceDiscountModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce discount persistence.
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

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "couponCode"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "couponCode"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "couponCode"}, false);

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

		_finderPathWithPaginationFindByLtE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtE_S",
			new String[] {
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"expirationDate", "status"}, true);

		_finderPathWithPaginationCountByLtE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtE_S",
			new String[] {Date.class.getName(), Integer.class.getName()},
			new String[] {"expirationDate", "status"}, false);

		_finderPathFetchByC_C_A = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_C_A",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "couponCode", "active_"}, true);

		_finderPathWithPaginationFindByC_L_A_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L_A_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "levelType", "active_", "status"}, true);

		_finderPathWithoutPaginationFindByC_L_A_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L_A_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"companyId", "levelType", "active_", "status"}, true);

		_finderPathCountByC_L_A_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L_A_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"companyId", "levelType", "active_", "status"},
			false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CommerceDiscountUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceDiscountUtil.setPersistence(null);

		entityCache.removeCache(CommerceDiscountImpl.class.getName());
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

	private static final String _SQL_SELECT_COMMERCEDISCOUNT =
		"SELECT commerceDiscount FROM CommerceDiscount commerceDiscount";

	private static final String _SQL_SELECT_COMMERCEDISCOUNT_WHERE =
		"SELECT commerceDiscount FROM CommerceDiscount commerceDiscount WHERE ";

	private static final String _SQL_COUNT_COMMERCEDISCOUNT =
		"SELECT COUNT(commerceDiscount) FROM CommerceDiscount commerceDiscount";

	private static final String _SQL_COUNT_COMMERCEDISCOUNT_WHERE =
		"SELECT COUNT(commerceDiscount) FROM CommerceDiscount commerceDiscount WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commerceDiscount.commerceDiscountId";

	private static final String _FILTER_SQL_SELECT_COMMERCEDISCOUNT_WHERE =
		"SELECT DISTINCT {commerceDiscount.*} FROM CommerceDiscount commerceDiscount WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CommerceDiscount.*} FROM (SELECT DISTINCT commerceDiscount.commerceDiscountId FROM CommerceDiscount commerceDiscount WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEDISCOUNT_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CommerceDiscount ON TEMP_TABLE.commerceDiscountId = CommerceDiscount.commerceDiscountId";

	private static final String _FILTER_SQL_COUNT_COMMERCEDISCOUNT_WHERE =
		"SELECT COUNT(DISTINCT commerceDiscount.commerceDiscountId) AS COUNT_VALUE FROM CommerceDiscount commerceDiscount WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "commerceDiscount";

	private static final String _FILTER_ENTITY_TABLE = "CommerceDiscount";

	private static final String _ORDER_BY_ENTITY_ALIAS = "commerceDiscount.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CommerceDiscount.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommerceDiscount exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceDiscount exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "level", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}