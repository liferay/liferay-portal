/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.web.internal.util.WorkflowInstancePortletUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo Barros
 */
public class WorkflowInstanceSearch extends SearchContainer<WorkflowInstance> {

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("asset-title");
			add("asset-type");
			add("status");
			add("definition");
			add("last-activity-date");
			add("end-date");
		}
	};

	public WorkflowInstanceSearch(
		PortletRequest portletRequest, PortletURL iteratorURL,
		WorkflowComparatorFactory workflowComparatorFactory) {

		super(
			portletRequest, new DisplayTerms(portletRequest), null,
			DEFAULT_CUR_PARAM, DEFAULT_DELTA, iteratorURL, headerNames, null);

		String orderByCol = SearchOrderByUtil.getOrderByCol(
			portletRequest, WorkflowPortletKeys.USER_WORKFLOW,
			"instance-order-by-col", "last-activity-date");

		setOrderByCol(orderByCol);

		String orderByType = SearchOrderByUtil.getOrderByType(
			portletRequest, WorkflowPortletKeys.USER_WORKFLOW,
			"instance-order-by-type", "asc");

		setOrderByComparator(
			WorkflowInstancePortletUtil.getWorkflowInstanceOrderByComparator(
				orderByCol, orderByType, workflowComparatorFactory));
		setOrderByType(orderByType);
	}

}