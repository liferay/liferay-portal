/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Inácio Nery
 */
public class KaleoDefinitionVersionSearch
	extends SearchContainer<KaleoDefinitionVersion> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-entries-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("title");
			add("description");
			add("modifiedDate");
		}
	};
	public static Map<String, String> orderableHeaders = HashMapBuilder.put(
		"title", "modifiedDate"
	).build();

	public KaleoDefinitionVersionSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest,
			new KaleoDefinitionVersionDisplayTerms(portletRequest),
			new KaleoDefinitionVersionSearchTerms(portletRequest),
			DEFAULT_CUR_PARAM, DEFAULT_DELTA, iteratorURL, headerNames,
			EMPTY_RESULTS_MESSAGE);

		KaleoDefinitionVersionDisplayTerms displayTerms =
			(KaleoDefinitionVersionDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			KaleoDefinitionVersionDisplayTerms.DESCRIPTION,
			displayTerms.getDescription());
		iteratorURL.setParameter(
			KaleoDefinitionVersionDisplayTerms.TITLE, displayTerms.getTitle());

		setOrderableHeaders(orderableHeaders);
	}

}