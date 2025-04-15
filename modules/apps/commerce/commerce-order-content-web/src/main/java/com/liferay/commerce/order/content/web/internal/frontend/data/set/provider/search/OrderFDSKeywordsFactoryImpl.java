/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider.search;

import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactory;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"fds.data.provider.key=" + CommerceOrderFDSNames.PENDING_ORDER_ITEMS,
		"fds.data.provider.key=" + CommerceOrderFDSNames.PENDING_ORDERS,
		"fds.data.provider.key=" + CommerceOrderFDSNames.PLACED_ORDER_ITEMS,
		"fds.data.provider.key=" + CommerceOrderFDSNames.PLACED_ORDERS
	},
	service = FDSKeywordsFactory.class
)
public class OrderFDSKeywordsFactoryImpl implements FDSKeywordsFactory {

	@Override
	public FDSKeywords create(HttpServletRequest httpServletRequest) {
		OrderFDSKeywordsImpl orderFDSKeywordsImpl = new OrderFDSKeywordsImpl();

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		long commerceAccountId = ParamUtil.getLong(
			httpServletRequest, "accountId");

		orderFDSKeywordsImpl.setAccountId(commerceAccountId);

		orderFDSKeywordsImpl.setCommerceOrderId(commerceOrderId);
		orderFDSKeywordsImpl.setKeywords(
			ParamUtil.getString(httpServletRequest, "search"));

		return orderFDSKeywordsImpl;
	}

}