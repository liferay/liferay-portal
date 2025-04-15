/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
		"mvc.command.name=/kaleo_forms_admin/deactivate_workflow_definition"
	},
	service = MVCActionCommand.class
)
public class DeactivateWorkflowDefinitionMVCActionCommand
	extends BaseKaleoFormsMVCActionCommand {

	/**
	 * Deactivates the <code>WorkflowDefinition</code> (in
	 * <code>com.liferay.portal.kernel</code>) by using its name and version
	 * from the action request. If deactivation fails, an error key is submitted
	 * to <code>SessionErrors</code> (in
	 * <code>com.liferay.portal.kernel</code>).
	 *
	 * @param  actionRequest the request from which to get the request
	 *         parameters
	 * @param  actionResponse the response to receive the render parameters
	 * @throws Exception if an exception occurred
	 */
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String name = ParamUtil.getString(actionRequest, "name");
		int version = ParamUtil.getInteger(actionRequest, "version");

		try {
			_workflowDefinitionManager.updateActive(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(), name,
				version, false);
		}
		catch (Exception exception) {
			if (isSessionErrorException(exception)) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				sendRedirect(actionRequest, actionResponse);
			}
			else {
				throw exception;
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeactivateWorkflowDefinitionMVCActionCommand.class);

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}