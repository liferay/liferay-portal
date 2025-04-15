/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.UserGroup;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author André Miranda
 */
public class UserGroupSearch extends SearchContainer<UserGroup> {

	public static final String EMPTY_RESULTS_MESSAGE = "there-are-no-results";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("user-group-name");
		}
	};

	public UserGroupSearch(
		PortletRequest portletRequest, int delta, PortletURL iteratorURL) {

		super(
			portletRequest, new DisplayTerms(portletRequest),
			new DisplayTerms(portletRequest), DEFAULT_CUR_PARAM, delta,
			iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);
	}

	public UserGroupSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		this(portletRequest, DEFAULT_DELTA, iteratorURL);
	}

}