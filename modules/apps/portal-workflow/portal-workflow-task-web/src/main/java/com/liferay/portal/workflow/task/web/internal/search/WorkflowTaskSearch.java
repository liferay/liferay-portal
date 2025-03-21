/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.task.web.internal.util.WorkflowTaskPortletUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Marcellus Tavares
 */
public class WorkflowTaskSearch extends SearchContainer<WorkflowTask> {

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("asset-title");
			add("asset-type");
			add("author");
			add("task");
			add("last-activity-date");
			add("due-date");
		}
	};
	public static Map<String, String> orderableHeaders = HashMapBuilder.put(
		"due-date", "due-date"
	).put(
		"last-activity-date", "last-activity-date"
	).build();

	public WorkflowTaskSearch(
		PortletRequest portletRequest, PortletURL iteratorURL,
		WorkflowComparatorFactory workflowComparatorFactory) {

		this(
			portletRequest, DEFAULT_CUR_PARAM, iteratorURL,
			workflowComparatorFactory);
	}

	public WorkflowTaskSearch(
		PortletRequest portletRequest, String curParam, PortletURL iteratorURL,
		WorkflowComparatorFactory workflowComparatorFactory) {

		super(
			portletRequest, new DisplayTerms(portletRequest),
			new DisplayTerms(portletRequest), curParam, DEFAULT_DELTA,
			iteratorURL, headerNames, null);

		setOrderableHeaders(orderableHeaders);

		String orderByCol = SearchOrderByUtil.getOrderByCol(
			portletRequest, PortletKeys.MY_WORKFLOW_TASK, "last-activity-date");

		setOrderByCol(orderByCol);

		String orderByType = SearchOrderByUtil.getOrderByType(
			portletRequest, PortletKeys.MY_WORKFLOW_TASK, "asc");

		setOrderByComparator(
			WorkflowTaskPortletUtil.getWorkflowTaskOrderByComparator(
				orderByCol, orderByType, workflowComparatorFactory));
		setOrderByType(orderByType);
	}

}