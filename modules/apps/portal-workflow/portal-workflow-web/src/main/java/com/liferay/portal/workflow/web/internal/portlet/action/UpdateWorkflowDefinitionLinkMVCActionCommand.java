/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.definition.link.update.handler.WorkflowDefinitionLinkUpdateHandler;
import com.liferay.portal.workflow.definition.link.update.handler.WorkflowDefinitionLinkUpdateHandlerRegistryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Enumeration;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"jakarta.portlet.name=" + WorkflowPortletKeys.SITE_ADMINISTRATION_WORKFLOW,
		"mvc.command.name=/portal_workflow/update_workflow_definition_link"
	},
	service = MVCActionCommand.class
)
public class UpdateWorkflowDefinitionLinkMVCActionCommand
	extends BaseWorkflowDefinitionMVCActionCommand {

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String workflowDefinition = (String)actionRequest.getAttribute(
			WorkflowWebKeys.WORKFLOW_DEFINITION_NAME);

		String resource = ParamUtil.getString(actionRequest, "resource");

		String successMessage = StringPool.BLANK;

		if (Validator.isNull(workflowDefinition)) {
			successMessage = _language.format(
				getResourceBundle(actionRequest), "workflow-unassigned-from-x",
				resource);
		}
		else {
			successMessage = _language.format(
				getResourceBundle(actionRequest), "workflow-assigned-to-x",
				resource);
		}

		SessionMessages.add(actionRequest, "requestProcessed", successMessage);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String[] classNameAndWorkflowDefinition =
			_getClassNameAndWorkflowDefinition(actionRequest);

		String className = classNameAndWorkflowDefinition[0];
		String workflowDefinition = classNameAndWorkflowDefinition[1];

		WorkflowDefinitionLinkUpdateHandler
			workflowDefinitionLinkUpdateHandler =
				WorkflowDefinitionLinkUpdateHandlerRegistryUtil.
					getWorkflowDefinitionLinkUpdateHandler(className);

		if (workflowDefinitionLinkUpdateHandler != null) {
			workflowDefinitionLinkUpdateHandler.updatedWorkflowDefinitionLink(
				workflowDefinition);
		}

		if (Validator.isNotNull(className)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long groupId = ParamUtil.getLong(actionRequest, "groupId");

			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				themeDisplay.getUserId(), themeDisplay.getCompanyId(), groupId,
				className, 0, 0, workflowDefinition);

			actionRequest.setAttribute(
				WorkflowWebKeys.WORKFLOW_DEFINITION_NAME, workflowDefinition);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private String[] _getClassNameAndWorkflowDefinition(
		ActionRequest actionRequest) {

		String className = StringPool.BLANK;
		String workflowDefinition = StringPool.BLANK;

		Enumeration<String> enumeration = actionRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (!name.startsWith(_PREFIX)) {
				continue;
			}

			className = name.substring(_PREFIX.length());
			workflowDefinition = ParamUtil.getString(actionRequest, name);

			break;
		}

		return new String[] {className, workflowDefinition};
	}

	private static final String _PREFIX = "workflowDefinitionName@";

	@Reference
	private Language _language;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}