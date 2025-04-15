/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.search;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
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
public class CommerceOrderSearch extends SearchContainer<CommerceOrder> {

	public CommerceOrderSearch(
		PortletRequest portletRequest, PortletURL iteratorURL,
		boolean filterByStatuses) {

		super(
			portletRequest, new CommerceOrderDisplayTerms(portletRequest),
			new CommerceOrderDisplayTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, _headerNames, _EMPTY_RESULTS_MESSAGE);

		CommerceOrderDisplayTerms commerceOrderDisplayTerms =
			(CommerceOrderDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.CHANNEL,
			String.valueOf(commerceOrderDisplayTerms.getCommerceChannel()));
		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.COMMERCE_ACCOUNT_ID,
			String.valueOf(commerceOrderDisplayTerms.getCommerceAccountId()));
		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.END_CREATE_DATE_DAY,
			String.valueOf(commerceOrderDisplayTerms.getEndCreateDateDay()));
		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.END_CREATE_DATE_MONTH,
			String.valueOf(commerceOrderDisplayTerms.getEndCreateDateMonth()));
		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.END_CREATE_DATE_YEAR,
			String.valueOf(commerceOrderDisplayTerms.getEndCreateDateYear()));
		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.START_CREATE_DATE_DAY,
			String.valueOf(commerceOrderDisplayTerms.getStartCreateDateDay()));
		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.START_CREATE_DATE_MONTH,
			String.valueOf(
				commerceOrderDisplayTerms.getStartCreateDateMonth()));
		iteratorURL.setParameter(
			CommerceOrderDisplayTerms.START_CREATE_DATE_YEAR,
			String.valueOf(commerceOrderDisplayTerms.getStartCreateDateYear()));

		if (filterByStatuses) {
			iteratorURL.setParameter(
				CommerceOrderDisplayTerms.ADVANCE_STATUS,
				commerceOrderDisplayTerms.getAdvanceStatus());
			iteratorURL.setParameter(
				CommerceOrderDisplayTerms.ORDER_STATUS,
				String.valueOf(commerceOrderDisplayTerms.getOrderStatus()));
		}

		try {
			setOrderableHeaders(_orderableHeaders);
			setOrderByCol(
				SearchOrderByUtil.getOrderByCol(
					portletRequest, CommercePortletKeys.COMMERCE_ORDER,
					"commerce-orders-order-by-col", "create-date"));
			setOrderByType(
				SearchOrderByUtil.getOrderByType(
					portletRequest, CommercePortletKeys.COMMERCE_ORDER,
					"commerce-orders-order-by-type", "desc"));
		}
		catch (Exception exception) {
			_log.error("Unable to initialize commerce order search", exception);
		}
	}

	private static final String _EMPTY_RESULTS_MESSAGE = "no-orders-were-found";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderSearch.class);

	private static final List<String> _headerNames = Arrays.asList(
		"order-date", "status", "customer-name", "customer-id", "order-id",
		"order-value", "notes");
	private static final Map<String, String> _orderableHeaders =
		LinkedHashMapBuilder.put(
			"create-date", "order-date"
		).put(
			"order-id", "order-id"
		).put(
			"total", "order-value"
		).build();

}