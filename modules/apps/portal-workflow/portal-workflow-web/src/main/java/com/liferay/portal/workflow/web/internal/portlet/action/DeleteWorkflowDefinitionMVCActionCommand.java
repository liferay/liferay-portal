/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"mvc.command.name=/portal_workflow/delete_workflow_definition"
	},
	service = MVCActionCommand.class
)
public class DeleteWorkflowDefinitionMVCActionCommand
	extends BaseWorkflowDefinitionMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String name = ParamUtil.getString(actionRequest, "name");
		int version = ParamUtil.getInteger(actionRequest, "version");

		workflowDefinitionManager.undeployWorkflowDefinition(
			themeDisplay.getCompanyId(), themeDisplay.getUserId(), name,
			version);
	}

	@Override
	protected String getSuccessMessage(ActionRequest actionRequest) {
		return _language.get(
			getResourceBundle(actionRequest), "workflow-deleted-successfully");
	}

	@Reference
	private Language _language;

}