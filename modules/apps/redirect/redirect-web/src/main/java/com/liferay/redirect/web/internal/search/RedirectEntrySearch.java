/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.web.internal.constants.RedirectPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class RedirectEntrySearch extends SearchContainer<RedirectEntry> {

	public RedirectEntrySearch(
		PortletRequest portletRequest, PortletResponse portletResponse,
		PortletURL iteratorURL, String searchContainerId) {

		super(portletRequest, iteratorURL, null, _EMPTY_RESULTS_MESSAGE);

		setId(searchContainerId);
		setOrderableHeaders(_orderableHeaders);
		setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, RedirectPortletKeys.REDIRECT,
				"redirect-entries-order-by-col", "modified-date"));
		setOrderByType(
			SearchOrderByUtil.getOrderByType(
				portletRequest, RedirectPortletKeys.REDIRECT,
				"redirect-entries-order-by-type", "asc"));
		setRowChecker(new EmptyOnClickRowChecker(portletResponse));
	}

	private static final String _EMPTY_RESULTS_MESSAGE =
		"no-redirects-were-found";

	private static final Map<String, String> _orderableHeaders =
		HashMapBuilder.put(
			"destinationURL", "sourceURL"
		).build();

}