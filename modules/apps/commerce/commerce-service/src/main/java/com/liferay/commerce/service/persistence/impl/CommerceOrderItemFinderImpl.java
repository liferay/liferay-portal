/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.impl.CommerceOrderItemImpl;
import com.liferay.commerce.service.persistence.CommerceOrderItemFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringUtil;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceOrderItemFinder.class)
public class CommerceOrderItemFinderImpl
	extends CommerceOrderItemFinderBaseImpl implements CommerceOrderItemFinder {

	public static final String COUNT_BY_G_A_O =
		CommerceOrderItemFinder.class.getName() + ".countByG_A_O";

	public static final String FIND_BY_AVAILABLE_QUANTITY =
		CommerceOrderItemFinder.class.getName() + ".findByAvailableQuantity";

	public static final String FIND_BY_G_A_O =
		CommerceOrderItemFinder.class.getName() + ".findByG_A_O";

	public static final String GET_COMMERCE_ORDER_ITEMS_QUANTITY =
		CommerceOrderItemFinder.class.getName() +
			".getCommerceOrderItemsQuantity";

	public static final String SUM_VALUE = "SUM_VALUE";

	@Override
	public int countByG_A_O(
		long groupId, long commerceAccountId, int[] orderStatuses) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_A_O);

			sql = replaceOrderStatus(sql, orderStatuses);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commerceAccountId);
			queryPos.add(groupId);

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceOrderItem> findByAvailableQuantity(
		long commerceOrderId) {

		return findByAvailableQuantity(
			commerceOrderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<CommerceOrderItem> findByAvailableQuantity(
		long commerceOrderId, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_AVAILABLE_QUANTITY);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"CommerceOrderItem", CommerceOrderItemImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commerceOrderId);

			return (List<CommerceOrderItem>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceOrderItem> findByG_A_O(
		long groupId, long commerceAccountId, int[] orderStatuses, int start,
		int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_A_O);

			sql = replaceOrderStatus(sql, orderStatuses);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"CommerceOrderItem", CommerceOrderItemImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commerceAccountId);
			queryPos.add(groupId);

			return (List<CommerceOrderItem>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public BigDecimal getCommerceOrderItemsQuantity(long commerceOrderId) {
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), GET_COMMERCE_ORDER_ITEMS_QUANTITY);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(SUM_VALUE, Type.BIG_DECIMAL);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commerceOrderId);

			Iterator<BigDecimal> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				BigDecimal sum = iterator.next();

				if (sum != null) {
					return sum;
				}
			}

			return BigDecimal.ZERO;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String replaceOrderStatus(String sql, int[] orderStatuses) {
		StringBundler sb = new StringBundler(orderStatuses.length);

		for (int i = 0; i < orderStatuses.length; i++) {
			sb.append(orderStatuses[i]);

			if (i == (orderStatuses.length - 1)) {
				continue;
			}

			sb.append(", ");
		}

		return StringUtil.replace(sql, "[$ORDER_STATUS$]", sb.toString());
	}

	@Reference
	private CustomSQL _customSQL;

}