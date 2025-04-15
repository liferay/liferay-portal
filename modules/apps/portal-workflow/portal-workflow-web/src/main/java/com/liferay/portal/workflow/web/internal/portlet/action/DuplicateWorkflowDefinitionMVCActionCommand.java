/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jeyvison Nascimento
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"mvc.command.name=/portal_workflow/duplicate_workflow_definition"
	},
	service = MVCActionCommand.class
)
public class DuplicateWorkflowDefinitionMVCActionCommand
	extends DeployWorkflowDefinitionMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String randomNamespace = ParamUtil.getString(
			actionRequest, "randomNamespace");

		WorkflowDefinition workflowDefinition = _getWorkflowDefinition(
			themeDisplay,
			ParamUtil.getString(actionRequest, "duplicatedDefinitionName"));

		Map<Locale, String> titleMap = localization.getLocalizationMap(
			actionRequest, randomNamespace + "title");

		validateTitle(actionRequest, titleMap);

		String name = ParamUtil.getString(actionRequest, "name");
		String content = ParamUtil.getString(actionRequest, "content");

		if ((workflowDefinition != null) && workflowDefinition.isActive()) {
			workflowDefinition =
				workflowDefinitionManager.deployWorkflowDefinition(
					null, themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					getTitle(actionRequest, titleMap), name,
					content.getBytes());
		}
		else {
			workflowDefinition =
				workflowDefinitionManager.saveWorkflowDefinition(
					null, themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					getTitle(actionRequest, titleMap), name,
					content.getBytes());
		}

		setRedirectAttribute(actionRequest, workflowDefinition);

		sendRedirect(actionRequest, actionResponse);
	}

	@Override
	protected String getSuccessMessage(ActionRequest actionRequest) {
		String duplicatedDefinitionTitle = ParamUtil.getString(
			actionRequest, "duplicatedDefinitionTitle");

		return language.format(
			getResourceBundle(actionRequest), "duplicated-from-x",
			StringUtil.quote(duplicatedDefinitionTitle));
	}

	private WorkflowDefinition _getWorkflowDefinition(
		ThemeDisplay themeDisplay, String name) {

		try {
			return workflowDefinitionManager.getLatestWorkflowDefinition(
				themeDisplay.getCompanyId(), name);
		}
		catch (WorkflowException workflowException) {
			if (_log.isWarnEnabled()) {
				_log.warn(workflowException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DuplicateWorkflowDefinitionMVCActionCommand.class);

}