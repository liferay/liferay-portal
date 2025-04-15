/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefano Motta
 */
public class PortalDefaultPermissionsSearch
	extends SearchContainer<PortalDefaultPermissionsSearchEntry> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-entries-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("className");
		}
	};

	public PortalDefaultPermissionsSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new DisplayTerms(portletRequest),
			new DisplayTerms(portletRequest), DEFAULT_CUR_PARAM, DEFAULT_DELTA,
			iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);
	}

}