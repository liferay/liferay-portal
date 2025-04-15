/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo Barros
 */
public class DDMDataProviderSearch
	extends SearchContainer<DDMDataProviderInstance> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-entries-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("name");
			add("userName");
			add("modified-date");
		}
	};

	public DDMDataProviderSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new DDMDataProviderDisplayTerms(portletRequest),
			new DDMDataProviderSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		DDMDataProviderDisplayTerms displayTerms =
			(DDMDataProviderDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			DDMDataProviderDisplayTerms.DESCRIPTION,
			displayTerms.getDescription());
		iteratorURL.setParameter(
			DDMDataProviderDisplayTerms.NAME,
			String.valueOf(displayTerms.getName()));
	}

}