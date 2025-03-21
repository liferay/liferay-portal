/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.portlet.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskDueDateException;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Calendar;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
		"mvc.command.name=/portal_workflow_task/update_task"
	},
	service = MVCResourceCommand.class
)
public class UpdateTaskMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long workflowTaskId = ParamUtil.getLong(
				resourceRequest, "workflowTaskId");

			int dueDateMonth = ParamUtil.getInteger(
				resourceRequest, "dueDateMonth");
			int dueDateDay = ParamUtil.getInteger(
				resourceRequest, "dueDateDay");
			int dueDateYear = ParamUtil.getInteger(
				resourceRequest, "dueDateYear");
			int dueDateHour = ParamUtil.getInteger(
				resourceRequest, "dueDateHour");
			int dueDateMinute = ParamUtil.getInteger(
				resourceRequest, "dueDateMinute");
			int dueDateAmPm = ParamUtil.getInteger(
				resourceRequest, "dueDateAmPm");

			if (dueDateAmPm == Calendar.PM) {
				dueDateHour += 12;
			}

			Date dueDate = _portal.getDate(
				dueDateMonth, dueDateDay, dueDateYear, dueDateHour,
				dueDateMinute, themeDisplay.getTimeZone(),
				WorkflowTaskDueDateException.class);

			WorkflowTask workflowTask = workflowTaskManager.getWorkflowTask(
				workflowTaskId);

			Date createDate = workflowTask.getCreateDate();

			if (createDate.after(dueDate)) {
				throw new WorkflowTaskDueDateException();
			}

			String comment = ParamUtil.getString(resourceRequest, "comment");

			workflowTaskManager.updateDueDate(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				workflowTaskId, comment, dueDate);
		}
		catch (WorkflowException workflowException) {
			_log.error(workflowException);

			SessionErrors.add(
				resourceRequest, workflowException.getClass(),
				workflowException);
		}
	}

	@Reference
	protected WorkflowTaskManager workflowTaskManager;

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateTaskMVCResourceCommand.class);

	@Reference
	private Portal _portal;

}