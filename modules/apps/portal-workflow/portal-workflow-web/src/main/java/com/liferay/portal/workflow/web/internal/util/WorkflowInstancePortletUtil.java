/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.util;

import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.PortletRequest;

/**
 * @author Marcellus Tavares
 */
public class WorkflowInstancePortletUtil {

	public static String getDisplayStyle(
		PortletRequest portletRequest, String[] displayViews) {

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

		String displayStyle = ParamUtil.getString(
			portletRequest, "displayStyle");

		if (Validator.isNull(displayStyle)) {
			displayStyle = portalPreferences.getValue(
				WorkflowPortletKeys.USER_WORKFLOW, "instance-display-style",
				"list");
		}
		else if (ArrayUtil.contains(displayViews, displayStyle)) {
			portalPreferences.setValue(
				WorkflowPortletKeys.USER_WORKFLOW, "instance-display-style",
				displayStyle);
		}

		if (!ArrayUtil.contains(displayViews, displayStyle)) {
			displayStyle = displayViews[0];
		}

		return displayStyle;
	}

	public static OrderByComparator<WorkflowInstance>
		getWorkflowInstanceOrderByComparator(
			String orderByCol, String orderByType,
			WorkflowComparatorFactory workflowComparatorFactory) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<WorkflowInstance> orderByComparator = null;

		if (orderByCol.equals("last-activity-date")) {
			orderByComparator =
				workflowComparatorFactory.getInstanceStartDateComparator(
					orderByAsc);
		}
		else {
			orderByComparator =
				workflowComparatorFactory.getInstanceEndDateComparator(
					orderByAsc);
		}

		return orderByComparator;
	}

}