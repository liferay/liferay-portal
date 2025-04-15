/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcellus Tavares
 */
public class DDMFormInstanceSearch extends SearchContainer<DDMFormInstance> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-entries-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("id");
			add("name");
			add("description");
			add("modified-date");
		}
	};

	public DDMFormInstanceSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new DDMFormInstanceDisplayTerms(portletRequest),
			new DDMFormInstaceSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		DDMFormInstanceDisplayTerms displayTerms =
			(DDMFormInstanceDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			DDMFormInstanceDisplayTerms.DESCRIPTION,
			displayTerms.getDescription());
		iteratorURL.setParameter(
			DDMFormInstanceDisplayTerms.NAME,
			String.valueOf(displayTerms.getName()));
	}

}