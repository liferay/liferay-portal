/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.search;

import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class DepotEntrySearch extends SearchContainer<DepotEntry> {

	public DepotEntrySearch(
		PortletRequest portletRequest, PortletResponse portletResponse,
		PortletURL iteratorURL, String searchContainerId) {

		super(
			portletRequest, iteratorURL, _headerNames, _EMPTY_RESULTS_MESSAGE);

		String portletId = PortletProviderUtil.getPortletId(
			User.class.getName(), PortletProvider.Action.VIEW);

		setId(searchContainerId);
		setOrderableHeaders(_orderableHeaders);
		setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, portletId, "depot-entries-order-by-col",
				"name"));
		setOrderByType(
			SearchOrderByUtil.getOrderByType(
				portletRequest, portletId, "depot-entries-order-by-type",
				"asc"));
		setRowChecker(new EmptyOnClickRowChecker(portletResponse));
	}

	private static final String _EMPTY_RESULTS_MESSAGE =
		"no-asset-libraries-were-found";

	private static final List<String> _headerNames = Arrays.asList("name");
	private static final Map<String, String> _orderableHeaders =
		HashMapBuilder.put(
			"name", "name"
		).build();

}