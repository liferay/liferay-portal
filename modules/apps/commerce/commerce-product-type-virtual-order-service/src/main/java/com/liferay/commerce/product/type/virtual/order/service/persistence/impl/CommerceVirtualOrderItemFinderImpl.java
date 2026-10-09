/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.persistence.impl;

import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemImpl;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemFinder;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceVirtualOrderItemFinder.class)
public class CommerceVirtualOrderItemFinderImpl
	extends CommerceVirtualOrderItemFinderBaseImpl
	implements CommerceVirtualOrderItemFinder {

	public static final String COUNT_BY_G_C_A =
		CommerceVirtualOrderItemFinder.class.getName() + ".countByG_C_A";

	public static final String FIND_BY_END_DATE =
		CommerceVirtualOrderItemFinder.class.getName() + ".findByEndDate";

	public static final String FIND_BY_G_C_A =
		CommerceVirtualOrderItemFinder.class.getName() + ".findByG_C_A";

	@Override
	public int countByG_C(long groupId, long commerceAccountId) {
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_C_A);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(commerceAccountId);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			int count = 0;

			Iterator<Long> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
				Long l = iterator.next();

				if (l == null) {
					continue;
				}

				count += l.intValue();
			}

			return count;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceVirtualOrderItem> findByEndDate(Date endDate) {
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_END_DATE);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"CommerceVirtualOrderItem", CommerceVirtualOrderItemImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(endDate);

			return (List<CommerceVirtualOrderItem>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceVirtualOrderItem> findByG_C(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceVirtualOrderItem> orderByComparator) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_C_A);

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"CommerceVirtualOrderItem", CommerceVirtualOrderItemImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(commerceAccountId);

			return (List<CommerceVirtualOrderItem>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Reference
	private CustomSQL _customSQL;

}