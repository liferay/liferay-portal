/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
		"mvc.command.name=/portal_workflow_task/complete_task"
	},
	service = MVCActionCommand.class
)
public class CompleteTaskMVCActionCommand
	extends BaseWorkflowTaskMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			boolean hideDefaultSuccessMessage = ParamUtil.getBoolean(
				actionRequest, "hideDefaultSuccessMessage");

			if (hideDefaultSuccessMessage) {
				SessionMessages.add(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.
							KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
			}

			long workflowTaskId = ParamUtil.getLong(
				actionRequest, "workflowTaskId");

			String transitionName = ParamUtil.getString(
				actionRequest, "transitionName");
			String comment = ParamUtil.getString(actionRequest, "comment");

			Map<String, Serializable> workflowContext = _getWorkflowContext(
				themeDisplay.getCompanyId(), workflowTaskId);

			ServiceContext serviceContext = (ServiceContext)workflowContext.get(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

			serviceContext.setRequest(
				_getHttpServletRequest(actionRequest, actionResponse));

			WorkflowHandler<?> workflowHandler =
				WorkflowHandlerRegistryUtil.getWorkflowHandler(
					(String)workflowContext.get(
						WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME));

			workflowHandler.contributeWorkflowContext(workflowContext);

			workflowContext.put(
				WorkflowConstants.CONTEXT_USER_ID,
				String.valueOf(themeDisplay.getUserId()));

			workflowTaskManager.completeWorkflowTask(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				workflowTaskId, transitionName, comment, workflowContext);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, _jsonFactory.createJSONObject());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error",
					_language.get(
						themeDisplay.getLocale(),
						"an-unexpected-error-occurred")));
		}
	}

	@Reference
	protected WorkflowTaskManager workflowTaskManager;

	private HttpServletRequest _getHttpServletRequest(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if (portletResponse == null) {
			httpServletRequest.setAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE,
				_portal.getLiferayPortletResponse(actionResponse));
		}

		return httpServletRequest;
	}

	private Map<String, Serializable> _getWorkflowContext(
			long companyId, long workflowTaskId)
		throws Exception {

		WorkflowTask workflowTask = workflowTaskManager.getWorkflowTask(
			workflowTaskId);

		WorkflowInstance workflowInstance =
			WorkflowInstanceManagerUtil.getWorkflowInstance(
				companyId, workflowTask.getWorkflowInstanceId());

		return workflowInstance.getWorkflowContext();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompleteTaskMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}