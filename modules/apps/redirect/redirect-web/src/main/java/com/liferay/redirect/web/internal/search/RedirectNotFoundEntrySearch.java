/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.web.internal.constants.RedirectPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

/**
 * @author Alejandro Tardín
 */
public class RedirectNotFoundEntrySearch
	extends SearchContainer<RedirectNotFoundEntry> {

	public RedirectNotFoundEntrySearch(
		PortletRequest portletRequest, PortletResponse portletResponse,
		PortletURL iteratorURL, String searchContainerId) {

		super(portletRequest, iteratorURL, null, _EMPTY_RESULTS_MESSAGE);

		setId(searchContainerId);
		setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, RedirectPortletKeys.REDIRECT,
				"redirect-not-found-entries-order-by-col", "requests"));
		setOrderByType(
			SearchOrderByUtil.getOrderByType(
				portletRequest, RedirectPortletKeys.REDIRECT,
				"redirect-not-found-entries-order-by-type", "asc"));
		setRowChecker(new EmptyOnClickRowChecker(portletResponse));
	}

	private static final String _EMPTY_RESULTS_MESSAGE =
		"all-your-pages-are-connected-or-redirected";

}