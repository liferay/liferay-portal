/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.workflow.security.permission.WorkflowTaskPermission;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
		"mvc.command.name=/portal_workflow_task/assign_task"
	},
	service = MVCResourceCommand.class
)
public class AssignTaskMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long workflowTaskId = ParamUtil.getLong(
			resourceRequest, "workflowTaskId");

		long assigneeUserId = ParamUtil.getLong(
			resourceRequest, "assigneeUserId");
		String comment = ParamUtil.getString(resourceRequest, "comment");

		WorkflowTask workflowTask = workflowTaskManager.getWorkflowTask(
			workflowTaskId);

		long groupId = MapUtil.getLong(
			workflowTask.getOptionalAttributes(), "groupId",
			themeDisplay.getSiteGroupId());

		_workflowTaskPermission.check(
			themeDisplay.getPermissionChecker(), workflowTask, groupId);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"hasPermission",
				_workflowTaskPermission.contains(
					themeDisplay.getPermissionChecker(),
					workflowTaskManager.assignWorkflowTaskToUser(
						themeDisplay.getCompanyId(), themeDisplay.getUserId(),
						workflowTaskId, assigneeUserId, comment, null, null),
					groupId)));

		SessionMessages.add(resourceRequest, "requestProcessed", "");
	}

	@Reference
	protected WorkflowTaskManager workflowTaskManager;

	@Reference
	private WorkflowTaskPermission _workflowTaskPermission;

}