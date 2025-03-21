/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.workflow.WorkflowControlPanelEntry;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = "jakarta.portlet.name=" + WorkflowPortletKeys.USER_WORKFLOW,
	service = ControlPanelEntry.class
)
public class UserWorkflowControlPanelEntry extends WorkflowControlPanelEntry {

	@Override
	protected boolean hasPermissionImplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		int count = WorkflowInstanceManagerUtil.getWorkflowInstanceCount(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			null, null, null);

		if (count > 0) {
			return true;
		}

		return super.hasPermissionImplicitlyGranted(
			permissionChecker, group, portlet);
	}

}