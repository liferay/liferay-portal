/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.action;

import com.liferay.portal.kernel.language.Language;
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
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.kaleo.designer.web.constants.KaleoDesignerPortletKeys;
import com.liferay.portal.workflow.kaleo.designer.web.internal.constants.KaleoDesignerWebKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.text.DateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jeyvison Nascimento
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoDesignerPortletKeys.KALEO_DESIGNER,
		"mvc.command.name=/kaleo_designer/revert_kaleo_definition_version"
	},
	service = MVCActionCommand.class
)
public class RevertKaleoDefinitionVersionMVCActionCommand
	extends BaseKaleoDesignerMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String name = ParamUtil.getString(actionRequest, "name");
		String draftVersion = ParamUtil.getString(
			actionRequest, "draftVersion");

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				themeDisplay.getCompanyId(), name, draftVersion);

		actionRequest.setAttribute(
			WorkflowWebKeys.WORKFLOW_DEFINITION_MODIFIED_DATE,
			kaleoDefinitionVersion.getModifiedDate());

		String content = kaleoDefinitionVersion.getContent();

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionVersion.getKaleoDefinition();

		WorkflowDefinition workflowDefinition = null;

		if (kaleoDefinition.isActive()) {
			validateWorkflowDefinition(
				actionRequest, content.getBytes("UTF-8"),
				themeDisplay.getLocale(),
				kaleoDefinitionVersion.getModifiedDate());

			workflowDefinition =
				workflowDefinitionManager.deployWorkflowDefinition(
					null, themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					kaleoDefinitionVersion.getTitle(), name,
					content.getBytes());
		}
		else {
			workflowDefinition =
				workflowDefinitionManager.saveWorkflowDefinition(
					null, themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					kaleoDefinitionVersion.getTitle(), name,
					content.getBytes());
		}

		kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.getLatestKaleoDefinitionVersion(
				themeDisplay.getCompanyId(), workflowDefinition.getName());

		actionRequest.setAttribute(
			KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION,
			kaleoDefinitionVersion);

		setRedirectAttribute(actionRequest, kaleoDefinitionVersion);
	}

	/**
	 * Returns a success message when the workflow definition is successfully
	 * reverted to a previous version.
	 *
	 * @param  actionRequest the action request used to construct the message
	 * @return the success message, including the date of the restored version
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

		return _language.format(
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

			String message = _language.format(
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
	private Language _language;

}