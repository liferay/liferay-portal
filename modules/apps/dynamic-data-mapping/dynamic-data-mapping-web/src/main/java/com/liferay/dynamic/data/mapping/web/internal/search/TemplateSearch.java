/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eduardo Lundgren
 */
public class TemplateSearch extends SearchContainer<DDMTemplate> {

	public static final String EMPTY_RESULTS_MESSAGE = "there-are-no-results";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("id");
			add("name");
			add("type");
			add("language");
			add("modified-date");
		}
	};

	public TemplateSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new TemplateDisplayTerms(portletRequest),
			new TemplateSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		TemplateDisplayTerms displayTerms =
			(TemplateDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			TemplateDisplayTerms.DESCRIPTION, displayTerms.getDescription());
		iteratorURL.setParameter(
			TemplateDisplayTerms.NAME, displayTerms.getName());
	}

	public TemplateSearch(
		PortletRequest portletRequest, PortletURL iteratorURL, int status) {

		this(portletRequest, iteratorURL);

		TemplateSearchTerms searchTerms = (TemplateSearchTerms)getSearchTerms();

		searchTerms.setStatus(status);
	}

}