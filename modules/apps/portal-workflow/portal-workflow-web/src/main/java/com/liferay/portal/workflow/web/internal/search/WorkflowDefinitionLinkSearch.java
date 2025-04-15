/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo Barros
 */
public class WorkflowDefinitionLinkSearch
	extends SearchContainer<WorkflowDefinitionLinkSearchEntry> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-entries-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("resource");
			add("workflow");
		}
	};

	public WorkflowDefinitionLinkSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest,
			new WorkflowDefinitionLinkDisplayTerms(portletRequest),
			new WorkflowDefinitionLinkSearchTerms(portletRequest),
			DEFAULT_CUR_PARAM, DEFAULT_DELTA, iteratorURL, headerNames,
			EMPTY_RESULTS_MESSAGE);

		WorkflowDefinitionLinkDisplayTerms displayTerms =
			(WorkflowDefinitionLinkDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			WorkflowDefinitionLinkDisplayTerms.RESOURCE,
			String.valueOf(displayTerms.getResource()));
		iteratorURL.setParameter(
			WorkflowDefinitionLinkDisplayTerms.WORKFLOW,
			displayTerms.getWorkflow());
	}

}