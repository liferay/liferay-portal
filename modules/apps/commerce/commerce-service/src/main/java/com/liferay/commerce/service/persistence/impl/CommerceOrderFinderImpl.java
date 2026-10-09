/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.configuration.CommerceOrderConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.impl.CommerceOrderImpl;
import com.liferay.commerce.service.persistence.CommerceOrderFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceOrderFinder.class)
public class CommerceOrderFinderImpl
	extends CommerceOrderFinderBaseImpl implements CommerceOrderFinder {

	public static final String FIND_BY_G_O =
		CommerceOrderFinder.class.getName() + ".findByG_O";

	public static final String FIND_BY_G_U_C_O_S =
		CommerceOrderFinder.class.getName() + ".findByG_U_C_O_S";

	public static final String
		GET_SHIPPED_COMMERCE_ORDERS_BY_COMMERCE_SHIPMENT_ID =
			CommerceOrderFinder.class.getName() +
				".getShippedCommerceOrdersByCommerceShipmentId";

	@Override
	public CommerceOrder fetchByG_U_C_O_S_First(
		long groupId, long userId, long commerceAccountId, int orderStatus) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_U_C_O_S);

			sql = replaceStatus(groupId, sql, userId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommerceOrderImpl.TABLE_NAME, CommerceOrderImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(commerceAccountId);
			queryPos.add(orderStatus);
			queryPos.add(userId);

			List<CommerceOrder> commerceOrders =
				(List<CommerceOrder>)QueryUtil.list(
					sqlQuery, getDialect(), 0, 1);

			if (!commerceOrders.isEmpty()) {
				return commerceOrders.get(0);
			}

			return null;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceOrder> findByG_O(long groupId, int[] orderStatuses) {
		return doFindByG_O(
			groupId, orderStatuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<CommerceOrder> findByG_O(
		long groupId, int[] orderStatuses, int start, int end) {

		return doFindByG_O(groupId, orderStatuses, start, end);
	}

	@Override
	public List<CommerceOrder> getShippedCommerceOrdersByCommerceShipmentId(
		long shipmentId, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(),
				GET_SHIPPED_COMMERCE_ORDERS_BY_COMMERCE_SHIPMENT_ID);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("CommerceOrder", CommerceOrderImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(shipmentId);

			return (List<CommerceOrder>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<CommerceOrder> doFindByG_O(
		long groupId, int[] orderStatuses, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_O);

			sql = replaceOrderStatus(sql, orderStatuses);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("CommerceOrder", CommerceOrderImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<CommerceOrder>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
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

	protected String replaceStatus(long groupId, String sql, long userId) {
		try {
			CommerceOrderConfiguration commerceOrderConfiguration =
				_configurationProvider.getConfiguration(
					CommerceOrderConfiguration.class,
					new GroupServiceSettingsLocator(
						groupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			if (CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.equals(
					commerceOrderConfiguration.openOrdersVisibilityScope())) {

				return StringUtil.replace(
					sql, "[$STATUS$]",
					StringBundler.concat(
						"(CommerceOrder.status = 0) AND (CommerceOrder.userId ",
						"= ", userId, ")"));
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return StringUtil.replace(
			sql, "[$STATUS$]", "CommerceOrder.status = 0");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderFinderImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CustomSQL _customSQL;

}