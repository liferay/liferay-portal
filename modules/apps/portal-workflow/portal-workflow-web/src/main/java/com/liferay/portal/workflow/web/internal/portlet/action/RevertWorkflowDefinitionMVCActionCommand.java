/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionFileException;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.text.DateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"mvc.command.name=/portal_workflow/revert_workflow_definition"
	},
	service = MVCActionCommand.class
)
public class RevertWorkflowDefinitionMVCActionCommand
	extends DeployWorkflowDefinitionMVCActionCommand {

	/**
	 * Reverts a workflow definition to the published state, creating a new
	 * version of it.
	 *
	 * @param actionRequest the action request from which to retrieve the
	 *        workflow definition name and version
	 * @param actionResponse the action response
	 */
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String previousName = ParamUtil.getString(
			actionRequest, "previousName");
		int previousVersion = ParamUtil.getInteger(
			actionRequest, "previousVersion");

		WorkflowDefinition previousWorkflowDefinition =
			_workflowDefinitionManager.liberalGetWorkflowDefinition(
				themeDisplay.getCompanyId(), previousName, previousVersion);

		actionRequest.setAttribute(
			WorkflowWebKeys.WORKFLOW_DEFINITION_MODIFIED_DATE,
			previousWorkflowDefinition.getModifiedDate());

		String content = previousWorkflowDefinition.getContent();

		WorkflowDefinition workflowDefinition = null;

		if (previousWorkflowDefinition.isActive()) {
			validateWorkflowDefinition(
				actionRequest, content.getBytes("UTF-8"),
				themeDisplay.getLocale(),
				previousWorkflowDefinition.getModifiedDate());

			workflowDefinition =
				workflowDefinitionManager.deployWorkflowDefinition(
					null, themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					previousWorkflowDefinition.getTitle(), previousName,
					content.getBytes());
		}
		else {
			workflowDefinition =
				workflowDefinitionManager.saveWorkflowDefinition(
					null, themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					previousWorkflowDefinition.getTitle(), previousName,
					content.getBytes());
		}

		setRedirectAttribute(actionRequest, workflowDefinition);
	}

	/**
	 * Returns a success message for the revert workflow definition action
	 *
	 * @param  actionRequest the action request
	 * @return the success message
	 */
	@Override
	protected String getSuccessMessage(ActionRequest actionRequest) {
		ResourceBundle resourceBundle = getResourceBundle(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DateFormat dateFormat = _getDateFormat(themeDisplay.getLocale());

		Date workflowDefinitionModifiedDate = GetterUtil.getDate(
			actionRequest.getAttribute(
				WorkflowWebKeys.WORKFLOW_DEFINITION_MODIFIED_DATE),
			dateFormat);

		return language.format(
			resourceBundle, "restored-to-revision-from-x",
			dateFormat.format(workflowDefinitionModifiedDate));
	}

	protected void validateWorkflowDefinition(
			ActionRequest actionRequest, byte[] bytes, Locale locale,
			Date previousDateModification)
		throws WorkflowDefinitionFileException {

		try {
			workflowDefinitionManager.validateWorkflowDefinition(bytes);
		}
		catch (WorkflowException workflowException) {
			DateFormat dateFormat = _getDateFormat(locale);

			String message = language.format(
				getResourceBundle(actionRequest),
				"the-version-from-x-is-not-valid-for-publication",
				dateFormat.format(previousDateModification));

			throw new WorkflowDefinitionFileException(
				message, workflowException);
		}
	}

	private DateFormat _getDateFormat(Locale locale) {
		if (DateUtil.isFormatAmPm(locale)) {
			return DateFormatFactoryUtil.getSimpleDateFormat(
				"MMM d, yyyy, hh:mm a", locale);
		}

		return DateFormatFactoryUtil.getSimpleDateFormat(
			"MMM d, yyyy, HH:mm", locale);
	}

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}