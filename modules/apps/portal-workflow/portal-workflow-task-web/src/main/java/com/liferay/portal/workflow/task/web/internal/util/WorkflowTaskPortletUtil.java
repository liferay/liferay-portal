/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.util;

import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;

import jakarta.portlet.PortletRequest;

/**
 * @author Marcellus Tavares
 */
public class WorkflowTaskPortletUtil {

	public static String getWorkflowTaskDisplayStyle(
		PortletRequest portletRequest, String[] displayViews) {

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

		String displayStyle = ParamUtil.getString(
			portletRequest, "displayStyle");

		if (Validator.isNull(displayStyle)) {
			displayStyle = portalPreferences.getValue(
				PortletKeys.MY_WORKFLOW_TASK, "display-style", "list");
		}
		else if (ArrayUtil.contains(displayViews, displayStyle)) {
			portalPreferences.setValue(
				PortletKeys.MY_WORKFLOW_TASK, "display-style", displayStyle);
		}

		if (!ArrayUtil.contains(displayViews, displayStyle)) {
			displayStyle = displayViews[0];
		}

		return displayStyle;
	}

	public static OrderByComparator<WorkflowTask>
		getWorkflowTaskOrderByComparator(
			String orderByCol, String orderByType,
			WorkflowComparatorFactory workflowComparatorFactory) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<WorkflowTask> orderByComparator = null;

		if (orderByCol.equals("due-date")) {
			orderByComparator =
				workflowComparatorFactory.getTaskDueDateComparator(orderByAsc);
		}
		else {
			orderByComparator =
				workflowComparatorFactory.getTaskModifiedDateComparator(
					orderByAsc);
		}

		return orderByComparator;
	}

}