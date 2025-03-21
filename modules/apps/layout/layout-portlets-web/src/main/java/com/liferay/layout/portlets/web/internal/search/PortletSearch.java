/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.portlets.web.internal.search;

import com.liferay.layout.portlets.web.internal.constants.LayoutsPortletsPortletKeys;
import com.liferay.layout.portlets.web.internal.util.comparator.PortletDisplayNameComparator;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Ferrer
 */
public class PortletSearch extends SearchContainer<Portlet> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-widgets-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("name");
			add("category");
		}
	};

	public PortletSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new PortletDisplayTerms(portletRequest),
			new PortletDisplayTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		PortletDisplayTerms displayTerms =
			(PortletDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			PortletDisplayTerms.CATEGORIES, displayTerms.getCategories());
		iteratorURL.setParameter(
			PortletDisplayTerms.NAME, displayTerms.getName());

		String orderByCol = SearchOrderByUtil.getOrderByCol(
			portletRequest, LayoutsPortletsPortletKeys.LAYOUT_PORTLETS, "name");

		setOrderByCol(orderByCol);

		String orderByType = SearchOrderByUtil.getOrderByType(
			portletRequest, LayoutsPortletsPortletKeys.LAYOUT_PORTLETS, "asc");

		setOrderByComparator(_getOrderByComparator(orderByCol, orderByType));
		setOrderByType(orderByType);
	}

	private OrderByComparator<Portlet> _getOrderByComparator(
		String orderByCol, String orderByType) {

		OrderByComparator<Portlet> orderByComparator = null;

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("name")) {
			orderByComparator = PortletDisplayNameComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

}