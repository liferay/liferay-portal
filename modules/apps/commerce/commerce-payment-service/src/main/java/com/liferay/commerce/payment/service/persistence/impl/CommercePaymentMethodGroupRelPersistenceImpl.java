/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.exception.NoSuchPaymentMethodGroupRelException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelUtil;
import com.liferay.commerce.payment.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

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
 * The persistence implementation for the commerce payment method group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommercePaymentMethodGroupRelPersistence.class)
public class CommercePaymentMethodGroupRelPersistenceImpl
	extends BasePersistenceImpl<CommercePaymentMethodGroupRel>
	implements CommercePaymentMethodGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePaymentMethodGroupRelUtil</code> to access the commerce payment method group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePaymentMethodGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the commerce payment method group rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment method group rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @return the range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
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

		List<CommercePaymentMethodGroupRel> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentMethodGroupRel>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommercePaymentMethodGroupRel
						commercePaymentMethodGroupRel : list) {

					if (groupId != commercePaymentMethodGroupRel.getGroupId()) {
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

			sb.append(_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<CommercePaymentMethodGroupRel>)QueryUtil.list(
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
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByGroupId_First(
			long groupId,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			fetchByGroupId_First(groupId, orderByComparator);

		if (commercePaymentMethodGroupRel != null) {
			return commercePaymentMethodGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPaymentMethodGroupRelException(sb.toString());
	}

	/**
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		List<CommercePaymentMethodGroupRel> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce payment method group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByGroupId_Last(
			long groupId,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			fetchByGroupId_Last(groupId, orderByComparator);

		if (commercePaymentMethodGroupRel != null) {
			return commercePaymentMethodGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPaymentMethodGroupRelException(sb.toString());
	}

	/**
	 * Returns the last commerce payment method group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByGroupId_Last(
		long groupId,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<CommercePaymentMethodGroupRel> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce payment method group rels before and after the current commerce payment method group rel in the ordered set where groupId = &#63;.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the current commerce payment method group rel
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel[] findByGroupId_PrevAndNext(
			long commercePaymentMethodGroupRelId, long groupId,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			findByPrimaryKey(commercePaymentMethodGroupRelId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentMethodGroupRel[] array =
				new CommercePaymentMethodGroupRelImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId,
				orderByComparator, true);

			array[1] = commercePaymentMethodGroupRel;

			array[2] = getByGroupId_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId,
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

	protected CommercePaymentMethodGroupRel getByGroupId_PrevAndNext(
		Session session,
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel,
		long groupId,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

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
			sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_JPQL);
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
						commercePaymentMethodGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentMethodGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce payment method group rels that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByGroupId(
		long groupId) {

		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment method group rels that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @return the range of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentMethodGroupRelModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentMethodGroupRel.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					CommercePaymentMethodGroupRelImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					CommercePaymentMethodGroupRelImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<CommercePaymentMethodGroupRel>)QueryUtil.list(
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
	 * Returns the commerce payment method group rels before and after the current commerce payment method group rel in the ordered set of commerce payment method group rels that the user has permission to view where groupId = &#63;.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the current commerce payment method group rel
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel[] filterFindByGroupId_PrevAndNext(
			long commercePaymentMethodGroupRelId, long groupId,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				commercePaymentMethodGroupRelId, groupId, orderByComparator);
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			findByPrimaryKey(commercePaymentMethodGroupRelId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentMethodGroupRel[] array =
				new CommercePaymentMethodGroupRelImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId,
				orderByComparator, true);

			array[1] = commercePaymentMethodGroupRel;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId,
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

	protected CommercePaymentMethodGroupRel filterGetByGroupId_PrevAndNext(
		Session session,
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel,
		long groupId,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentMethodGroupRelModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentMethodGroupRel.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommercePaymentMethodGroupRelImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommercePaymentMethodGroupRelImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentMethodGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentMethodGroupRel> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce payment method group rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commercePaymentMethodGroupRel);
		}
	}

	/**
	 * Returns the number of commerce payment method group rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce payment method group rels
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

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
	 * Returns the number of commerce payment method group rels that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
				findByGroupId(groupId);

			commercePaymentMethodGroupRels = InlineSQLHelperUtil.filter(
				commercePaymentMethodGroupRels, groupId);

			return commercePaymentMethodGroupRels.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentMethodGroupRel.class.getName(),
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
		"commercePaymentMethodGroupRel.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByG_A;
	private FinderPath _finderPathWithoutPaginationFindByG_A;
	private FinderPath _finderPathCountByG_A;

	/**
	 * Returns all the commerce payment method group rels where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByG_A(
		long groupId, boolean active) {

		return findByG_A(
			groupId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment method group rels where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @return the range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByG_A(
		long groupId, boolean active, int start, int end) {

		return findByG_A(groupId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		return findByG_A(groupId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_A;
				finderArgs = new Object[] {groupId, active};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_A;
			finderArgs = new Object[] {
				groupId, active, start, end, orderByComparator
			};
		}

		List<CommercePaymentMethodGroupRel> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentMethodGroupRel>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommercePaymentMethodGroupRel
						commercePaymentMethodGroupRel : list) {

					if ((groupId !=
							commercePaymentMethodGroupRel.getGroupId()) ||
						(active != commercePaymentMethodGroupRel.isActive())) {

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

			sb.append(_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

			sb.append(_FINDER_COLUMN_G_A_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_A_ACTIVE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(active);

				list = (List<CommercePaymentMethodGroupRel>)QueryUtil.list(
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
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByG_A_First(
			long groupId, boolean active,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			fetchByG_A_First(groupId, active, orderByComparator);

		if (commercePaymentMethodGroupRel != null) {
			return commercePaymentMethodGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchPaymentMethodGroupRelException(sb.toString());
	}

	/**
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByG_A_First(
		long groupId, boolean active,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		List<CommercePaymentMethodGroupRel> list = findByG_A(
			groupId, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce payment method group rel in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByG_A_Last(
			long groupId, boolean active,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			fetchByG_A_Last(groupId, active, orderByComparator);

		if (commercePaymentMethodGroupRel != null) {
			return commercePaymentMethodGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchPaymentMethodGroupRelException(sb.toString());
	}

	/**
	 * Returns the last commerce payment method group rel in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByG_A_Last(
		long groupId, boolean active,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		int count = countByG_A(groupId, active);

		if (count == 0) {
			return null;
		}

		List<CommercePaymentMethodGroupRel> list = findByG_A(
			groupId, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce payment method group rels before and after the current commerce payment method group rel in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the current commerce payment method group rel
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel[] findByG_A_PrevAndNext(
			long commercePaymentMethodGroupRelId, long groupId, boolean active,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			findByPrimaryKey(commercePaymentMethodGroupRelId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentMethodGroupRel[] array =
				new CommercePaymentMethodGroupRelImpl[3];

			array[0] = getByG_A_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId, active,
				orderByComparator, true);

			array[1] = commercePaymentMethodGroupRel;

			array[2] = getByG_A_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId, active,
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

	protected CommercePaymentMethodGroupRel getByG_A_PrevAndNext(
		Session session,
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel,
		long groupId, boolean active,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_G_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_A_ACTIVE_2);

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
			sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentMethodGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentMethodGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce payment method group rels that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByG_A(
		long groupId, boolean active) {

		return filterFindByG_A(
			groupId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment method group rels that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @return the range of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByG_A(
		long groupId, boolean active, int start, int end) {

		return filterFindByG_A(groupId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels that the user has permissions to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_A(groupId, active, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_A(
					groupId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentMethodGroupRelModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentMethodGroupRel.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					CommercePaymentMethodGroupRelImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					CommercePaymentMethodGroupRelImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(active);

			return (List<CommercePaymentMethodGroupRel>)QueryUtil.list(
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
	 * Returns the commerce payment method group rels before and after the current commerce payment method group rel in the ordered set of commerce payment method group rels that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the current commerce payment method group rel
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel[] filterFindByG_A_PrevAndNext(
			long commercePaymentMethodGroupRelId, long groupId, boolean active,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_A_PrevAndNext(
				commercePaymentMethodGroupRelId, groupId, active,
				orderByComparator);
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			findByPrimaryKey(commercePaymentMethodGroupRelId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentMethodGroupRel[] array =
				new CommercePaymentMethodGroupRelImpl[3];

			array[0] = filterGetByG_A_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId, active,
				orderByComparator, true);

			array[1] = commercePaymentMethodGroupRel;

			array[2] = filterGetByG_A_PrevAndNext(
				session, commercePaymentMethodGroupRel, groupId, active,
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

	protected CommercePaymentMethodGroupRel filterGetByG_A_PrevAndNext(
		Session session,
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel,
		long groupId, boolean active,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentMethodGroupRelModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentMethodGroupRelModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentMethodGroupRel.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommercePaymentMethodGroupRelImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommercePaymentMethodGroupRelImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentMethodGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentMethodGroupRel> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce payment method group rels where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	@Override
	public void removeByG_A(long groupId, boolean active) {
		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				findByG_A(
					groupId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commercePaymentMethodGroupRel);
		}
	}

	/**
	 * Returns the number of commerce payment method group rels where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce payment method group rels
	 */
	@Override
	public int countByG_A(long groupId, boolean active) {
		FinderPath finderPath = _finderPathCountByG_A;

		Object[] finderArgs = new Object[] {groupId, active};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

			sb.append(_FINDER_COLUMN_G_A_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_A_ACTIVE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

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
	 * Returns the number of commerce payment method group rels that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public int filterCountByG_A(long groupId, boolean active) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_A(groupId, active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
				findByG_A(groupId, active);

			commercePaymentMethodGroupRels = InlineSQLHelperUtil.filter(
				commercePaymentMethodGroupRels, groupId);

			return commercePaymentMethodGroupRels.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_G_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_A_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentMethodGroupRel.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_A_GROUPID_2 =
		"commercePaymentMethodGroupRel.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_A_ACTIVE_2 =
		"commercePaymentMethodGroupRel.active = ?";

	private static final String _FINDER_COLUMN_G_A_ACTIVE_2_SQL =
		"commercePaymentMethodGroupRel.active_ = ?";

	private FinderPath _finderPathFetchByG_P;

	/**
	 * Returns the commerce payment method group rel where groupId = &#63; and paymentIntegrationKey = &#63; or throws a <code>NoSuchPaymentMethodGroupRelException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @return the matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByG_P(
			long groupId, String paymentIntegrationKey)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			fetchByG_P(groupId, paymentIntegrationKey);

		if (commercePaymentMethodGroupRel == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", paymentIntegrationKey=");
			sb.append(paymentIntegrationKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPaymentMethodGroupRelException(sb.toString());
		}

		return commercePaymentMethodGroupRel;
	}

	/**
	 * Returns the commerce payment method group rel where groupId = &#63; and paymentIntegrationKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @return the matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByG_P(
		long groupId, String paymentIntegrationKey) {

		return fetchByG_P(groupId, paymentIntegrationKey, true);
	}

	/**
	 * Returns the commerce payment method group rel where groupId = &#63; and paymentIntegrationKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByG_P(
		long groupId, String paymentIntegrationKey, boolean useFinderCache) {

		paymentIntegrationKey = Objects.toString(paymentIntegrationKey, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {groupId, paymentIntegrationKey};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByG_P, finderArgs, this);
		}

		if (result instanceof CommercePaymentMethodGroupRel) {
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				(CommercePaymentMethodGroupRel)result;

			if ((groupId != commercePaymentMethodGroupRel.getGroupId()) ||
				!Objects.equals(
					paymentIntegrationKey,
					commercePaymentMethodGroupRel.getPaymentIntegrationKey())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE);

			sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

			boolean bindPaymentIntegrationKey = false;

			if (paymentIntegrationKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_P_PAYMENTINTEGRATIONKEY_3);
			}
			else {
				bindPaymentIntegrationKey = true;

				sb.append(_FINDER_COLUMN_G_P_PAYMENTINTEGRATIONKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindPaymentIntegrationKey) {
					queryPos.add(paymentIntegrationKey);
				}

				List<CommercePaymentMethodGroupRel> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByG_P, finderArgs, list);
					}
				}
				else {
					CommercePaymentMethodGroupRel
						commercePaymentMethodGroupRel = list.get(0);

					result = commercePaymentMethodGroupRel;

					cacheResult(commercePaymentMethodGroupRel);
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
			return (CommercePaymentMethodGroupRel)result;
		}
	}

	/**
	 * Removes the commerce payment method group rel where groupId = &#63; and paymentIntegrationKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @return the commerce payment method group rel that was removed
	 */
	@Override
	public CommercePaymentMethodGroupRel removeByG_P(
			long groupId, String paymentIntegrationKey)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel = findByG_P(
			groupId, paymentIntegrationKey);

		return remove(commercePaymentMethodGroupRel);
	}

	/**
	 * Returns the number of commerce payment method group rels where groupId = &#63; and paymentIntegrationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @return the number of matching commerce payment method group rels
	 */
	@Override
	public int countByG_P(long groupId, String paymentIntegrationKey) {
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			fetchByG_P(groupId, paymentIntegrationKey);

		if (commercePaymentMethodGroupRel == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_GROUPID_2 =
		"commercePaymentMethodGroupRel.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PAYMENTINTEGRATIONKEY_2 =
		"commercePaymentMethodGroupRel.paymentIntegrationKey = ?";

	private static final String _FINDER_COLUMN_G_P_PAYMENTINTEGRATIONKEY_3 =
		"(commercePaymentMethodGroupRel.paymentIntegrationKey IS NULL OR commercePaymentMethodGroupRel.paymentIntegrationKey = '')";

	public CommercePaymentMethodGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commercePaymentMethodGroupRelId", "CPaymentMethodGroupRelId");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePaymentMethodGroupRel.class);

		setModelImplClass(CommercePaymentMethodGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePaymentMethodGroupRelTable.INSTANCE);
	}

	/**
	 * Caches the commerce payment method group rel in the entity cache if it is enabled.
	 *
	 * @param commercePaymentMethodGroupRel the commerce payment method group rel
	 */
	@Override
	public void cacheResult(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		entityCache.putResult(
			CommercePaymentMethodGroupRelImpl.class,
			commercePaymentMethodGroupRel.getPrimaryKey(),
			commercePaymentMethodGroupRel);

		finderCache.putResult(
			_finderPathFetchByG_P,
			new Object[] {
				commercePaymentMethodGroupRel.getGroupId(),
				commercePaymentMethodGroupRel.getPaymentIntegrationKey()
			},
			commercePaymentMethodGroupRel);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce payment method group rels in the entity cache if it is enabled.
	 *
	 * @param commercePaymentMethodGroupRels the commerce payment method group rels
	 */
	@Override
	public void cacheResult(
		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commercePaymentMethodGroupRels.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				commercePaymentMethodGroupRels) {

			if (entityCache.getResult(
					CommercePaymentMethodGroupRelImpl.class,
					commercePaymentMethodGroupRel.getPrimaryKey()) == null) {

				cacheResult(commercePaymentMethodGroupRel);
			}
		}
	}

	/**
	 * Clears the cache for all commerce payment method group rels.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommercePaymentMethodGroupRelImpl.class);

		finderCache.clearCache(CommercePaymentMethodGroupRelImpl.class);
	}

	/**
	 * Clears the cache for the commerce payment method group rel.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		entityCache.removeResult(
			CommercePaymentMethodGroupRelImpl.class,
			commercePaymentMethodGroupRel);
	}

	@Override
	public void clearCache(
		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels) {

		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				commercePaymentMethodGroupRels) {

			entityCache.removeResult(
				CommercePaymentMethodGroupRelImpl.class,
				commercePaymentMethodGroupRel);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommercePaymentMethodGroupRelImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CommercePaymentMethodGroupRelImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CommercePaymentMethodGroupRelModelImpl
			commercePaymentMethodGroupRelModelImpl) {

		Object[] args = new Object[] {
			commercePaymentMethodGroupRelModelImpl.getGroupId(),
			commercePaymentMethodGroupRelModelImpl.getPaymentIntegrationKey()
		};

		finderCache.putResult(
			_finderPathFetchByG_P, args,
			commercePaymentMethodGroupRelModelImpl);
	}

	/**
	 * Creates a new commerce payment method group rel with the primary key. Does not add the commerce payment method group rel to the database.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key for the new commerce payment method group rel
	 * @return the new commerce payment method group rel
	 */
	@Override
	public CommercePaymentMethodGroupRel create(
		long commercePaymentMethodGroupRelId) {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			new CommercePaymentMethodGroupRelImpl();

		commercePaymentMethodGroupRel.setNew(true);
		commercePaymentMethodGroupRel.setPrimaryKey(
			commercePaymentMethodGroupRelId);

		commercePaymentMethodGroupRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePaymentMethodGroupRel;
	}

	/**
	 * Removes the commerce payment method group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel that was removed
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel remove(
			long commercePaymentMethodGroupRelId)
		throws NoSuchPaymentMethodGroupRelException {

		return remove((Serializable)commercePaymentMethodGroupRelId);
	}

	/**
	 * Removes the commerce payment method group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel that was removed
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel remove(Serializable primaryKey)
		throws NoSuchPaymentMethodGroupRelException {

		Session session = null;

		try {
			session = openSession();

			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				(CommercePaymentMethodGroupRel)session.get(
					CommercePaymentMethodGroupRelImpl.class, primaryKey);

			if (commercePaymentMethodGroupRel == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPaymentMethodGroupRelException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commercePaymentMethodGroupRel);
		}
		catch (NoSuchPaymentMethodGroupRelException noSuchEntityException) {
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
	protected CommercePaymentMethodGroupRel removeImpl(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePaymentMethodGroupRel)) {
				commercePaymentMethodGroupRel =
					(CommercePaymentMethodGroupRel)session.get(
						CommercePaymentMethodGroupRelImpl.class,
						commercePaymentMethodGroupRel.getPrimaryKeyObj());
			}

			if (commercePaymentMethodGroupRel != null) {
				session.delete(commercePaymentMethodGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePaymentMethodGroupRel != null) {
			clearCache(commercePaymentMethodGroupRel);
		}

		return commercePaymentMethodGroupRel;
	}

	@Override
	public CommercePaymentMethodGroupRel updateImpl(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		boolean isNew = commercePaymentMethodGroupRel.isNew();

		if (!(commercePaymentMethodGroupRel instanceof
				CommercePaymentMethodGroupRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePaymentMethodGroupRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePaymentMethodGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePaymentMethodGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePaymentMethodGroupRel implementation " +
					commercePaymentMethodGroupRel.getClass());
		}

		CommercePaymentMethodGroupRelModelImpl
			commercePaymentMethodGroupRelModelImpl =
				(CommercePaymentMethodGroupRelModelImpl)
					commercePaymentMethodGroupRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePaymentMethodGroupRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePaymentMethodGroupRel.setCreateDate(date);
			}
			else {
				commercePaymentMethodGroupRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePaymentMethodGroupRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePaymentMethodGroupRel.setModifiedDate(date);
			}
			else {
				commercePaymentMethodGroupRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commercePaymentMethodGroupRel);
			}
			else {
				commercePaymentMethodGroupRel =
					(CommercePaymentMethodGroupRel)session.merge(
						commercePaymentMethodGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommercePaymentMethodGroupRelImpl.class,
			commercePaymentMethodGroupRelModelImpl, false, true);

		cacheUniqueFindersCache(commercePaymentMethodGroupRelModelImpl);

		if (isNew) {
			commercePaymentMethodGroupRel.setNew(false);
		}

		commercePaymentMethodGroupRel.resetOriginalValues();

		return commercePaymentMethodGroupRel;
	}

	/**
	 * Returns the commerce payment method group rel with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByPrimaryKey(
			Serializable primaryKey)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			fetchByPrimaryKey(primaryKey);

		if (commercePaymentMethodGroupRel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPaymentMethodGroupRelException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commercePaymentMethodGroupRel;
	}

	/**
	 * Returns the commerce payment method group rel with the primary key or throws a <code>NoSuchPaymentMethodGroupRelException</code> if it could not be found.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByPrimaryKey(
			long commercePaymentMethodGroupRelId)
		throws NoSuchPaymentMethodGroupRelException {

		return findByPrimaryKey((Serializable)commercePaymentMethodGroupRelId);
	}

	/**
	 * Returns the commerce payment method group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel, or <code>null</code> if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByPrimaryKey(
		long commercePaymentMethodGroupRelId) {

		return fetchByPrimaryKey((Serializable)commercePaymentMethodGroupRelId);
	}

	/**
	 * Returns all the commerce payment method group rels.
	 *
	 * @return the commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment method group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @return the range of commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findAll(
		int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findAll(
		int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
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

		List<CommercePaymentMethodGroupRel> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentMethodGroupRel>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL;

				sql = sql.concat(
					CommercePaymentMethodGroupRelModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommercePaymentMethodGroupRel>)QueryUtil.list(
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
	 * Removes all the commerce payment method group rels from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				findAll()) {

			remove(commercePaymentMethodGroupRel);
		}
	}

	/**
	 * Returns the number of commerce payment method group rels.
	 *
	 * @return the number of commerce payment method group rels
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
					_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL);

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
		return "CPaymentMethodGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommercePaymentMethodGroupRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce payment method group rel persistence.
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

		_finderPathWithPaginationFindByG_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "active_"}, true);

		_finderPathWithoutPaginationFindByG_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "active_"}, true);

		_finderPathCountByG_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "active_"}, false);

		_finderPathFetchByG_P = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "paymentIntegrationKey"}, true);

		CommercePaymentMethodGroupRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePaymentMethodGroupRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePaymentMethodGroupRelImpl.class.getName());
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

	private static final String _SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL =
		"SELECT commercePaymentMethodGroupRel FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel";

	private static final String
		_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE =
			"SELECT commercePaymentMethodGroupRel FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL =
		"SELECT COUNT(commercePaymentMethodGroupRel) FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel";

	private static final String _SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE =
		"SELECT COUNT(commercePaymentMethodGroupRel) FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commercePaymentMethodGroupRel.CPaymentMethodGroupRelId";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE =
			"SELECT DISTINCT {commercePaymentMethodGroupRel.*} FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CommercePaymentMethodGroupRel.*} FROM (SELECT DISTINCT commercePaymentMethodGroupRel.CPaymentMethodGroupRelId FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CommercePaymentMethodGroupRel ON TEMP_TABLE.CPaymentMethodGroupRelId = CommercePaymentMethodGroupRel.CPaymentMethodGroupRelId";

	private static final String
		_FILTER_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE =
			"SELECT COUNT(DISTINCT commercePaymentMethodGroupRel.CPaymentMethodGroupRelId) AS COUNT_VALUE FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"commercePaymentMethodGroupRel";

	private static final String _FILTER_ENTITY_TABLE =
		"CommercePaymentMethodGroupRel";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"commercePaymentMethodGroupRel.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"CommercePaymentMethodGroupRel.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommercePaymentMethodGroupRel exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePaymentMethodGroupRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentMethodGroupRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commercePaymentMethodGroupRelId", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}