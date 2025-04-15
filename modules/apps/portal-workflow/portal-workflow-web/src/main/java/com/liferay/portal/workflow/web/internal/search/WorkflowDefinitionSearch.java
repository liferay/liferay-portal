/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class WorkflowDefinitionSearch
	extends SearchContainer<WorkflowDefinition> {

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

	public WorkflowDefinitionSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new WorkflowDefinitionDisplayTerms(portletRequest),
			new WorkflowDefinitionSearchTerms(portletRequest),
			DEFAULT_CUR_PARAM, DEFAULT_DELTA, iteratorURL, headerNames,
			EMPTY_RESULTS_MESSAGE);

		WorkflowDefinitionDisplayTerms displayTerms =
			(WorkflowDefinitionDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			WorkflowDefinitionDisplayTerms.DESCRIPTION,
			displayTerms.getDescription());
		iteratorURL.setParameter(
			WorkflowDefinitionDisplayTerms.TITLE, displayTerms.getTitle());

		setOrderableHeaders(orderableHeaders);
	}

}