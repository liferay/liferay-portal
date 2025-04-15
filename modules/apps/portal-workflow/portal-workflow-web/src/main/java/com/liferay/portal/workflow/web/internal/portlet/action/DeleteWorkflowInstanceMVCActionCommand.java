/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletSession;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE,
		"jakarta.portlet.name=" + WorkflowPortletKeys.SITE_ADMINISTRATION_WORKFLOW,
		"jakarta.portlet.name=" + WorkflowPortletKeys.USER_WORKFLOW,
		"mvc.command.name=/portal_workflow/delete_workflow_instance"
	},
	service = MVCActionCommand.class
)
public class DeleteWorkflowInstanceMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			WorkflowInstance workflowInstance = _getWorkflowInstance(
				actionRequest);

			Map<String, Serializable> workflowContext =
				workflowInstance.getWorkflowContext();

			_validateUser(workflowContext);

			_updateEntryStatus(workflowContext);

			_deleteWorkflowInstance(workflowContext);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException ||
				exception instanceof WorkflowException) {

				SessionErrors.add(actionRequest, exception.getClass());

				PortletSession portletSession =
					actionRequest.getPortletSession();

				PortletContext portletContext =
					portletSession.getPortletContext();

				PortletRequestDispatcher portletRequestDispatcher =
					portletContext.getRequestDispatcher("/instance/error.jsp");

				portletRequestDispatcher.include(actionRequest, actionResponse);
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteWorkflowInstance(
			Map<String, Serializable> workflowContext)
		throws PortalException {

		long companyId = GetterUtil.getLong(
			workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID));
		long groupId = GetterUtil.getLong(
			workflowContext.get(WorkflowConstants.CONTEXT_GROUP_ID));
		String className = GetterUtil.getString(
			workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME));
		long classPK = GetterUtil.getLong(
			workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			companyId, groupId, className, classPK);
	}

	private WorkflowInstance _getWorkflowInstance(ActionRequest actionRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long workflowInstanceId = ParamUtil.getLong(
			actionRequest, "workflowInstanceId");

		return WorkflowInstanceManagerUtil.getWorkflowInstance(
			themeDisplay.getCompanyId(), workflowInstanceId);
	}

	private void _updateEntryStatus(Map<String, Serializable> workflowContext)
		throws PortalException {

		String className = GetterUtil.getString(
			workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME));

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(className);

		workflowHandler.updateStatus(
			WorkflowConstants.STATUS_DRAFT, workflowContext);
	}

	private void _validateUser(Map<String, Serializable> workflowContext)
		throws PortalException {

		long companyId = GetterUtil.getLong(
			workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID));
		long userId = GetterUtil.getLong(
			workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));

		workflowContext.put(
			WorkflowConstants.CONTEXT_USER_ID,
			String.valueOf(_portal.getValidUserId(companyId, userId)));
	}

	@Reference
	private Portal _portal;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}