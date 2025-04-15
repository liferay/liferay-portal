/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.search;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceOrderItemSearch
	extends SearchContainer<CommerceOrderItem> {

	public CommerceOrderItemSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new CommerceOrderItemDisplayTerms(portletRequest),
			new CommerceOrderItemSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, _headerNames, _EMPTY_RESULTS_MESSAGE);

		CommerceOrderItemDisplayTerms commerceOrderItemDisplayTerms =
			(CommerceOrderItemDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			CommerceOrderItemDisplayTerms.SKU,
			commerceOrderItemDisplayTerms.getSku());
		iteratorURL.setParameter(
			CommerceOrderItemDisplayTerms.NAME,
			commerceOrderItemDisplayTerms.getName());

		try {
			setOrderableHeaders(_orderableHeaders);
			setOrderByCol(
				SearchOrderByUtil.getOrderByCol(
					portletRequest, CommercePortletKeys.COMMERCE_ORDER,
					"commerce-order-items-order-by-col", "sku"));
			setOrderByType(
				SearchOrderByUtil.getOrderByType(
					portletRequest, CommercePortletKeys.COMMERCE_ORDER,
					"commerce-order-items-order-by-type", "asc"));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize commerce order item search", exception);
		}
	}

	private static final String _EMPTY_RESULTS_MESSAGE =
		"no-order-items-were-found";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderItemSearch.class);

	private static final List<String> _headerNames = Arrays.asList(
		"sku", "name", "quantity", "js/price");
	private static final Map<String, String> _orderableHeaders =
		LinkedHashMapBuilder.put(
			"sku", "sku"
		).put(
			"name", "name"
		).put(
			"quantity", "quantity"
		).put(
			"js/price", "js/price"
		).build();

}