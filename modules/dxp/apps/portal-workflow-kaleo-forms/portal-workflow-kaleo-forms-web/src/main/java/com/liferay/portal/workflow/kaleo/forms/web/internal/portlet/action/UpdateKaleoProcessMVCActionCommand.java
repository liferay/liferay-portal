/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPairs;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
		"mvc.command.name=/kaleo_forms_admin/update_kaleo_process"
	},
	service = MVCActionCommand.class
)
public class UpdateKaleoProcessMVCActionCommand
	extends BaseKaleoFormsMVCActionCommand {

	/**
	 * Updates the <code>KaleoProcess</code> (in the
	 * <code>com.liferay.portal.workflow.kaleo.forms.api</code> module), or adds
	 * a new process if the Kaleo process ID from the action request is not
	 * greater than <code>0</code>. This method also updates the process's
	 * <code>WorkflowDefinitionLink</code> (in
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

		long kaleoProcessId = ParamUtil.getLong(
			actionRequest, "kaleoProcessId");

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long ddmStructureId = ParamUtil.getLong(
			actionRequest, "ddmStructureId");
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		long ddmTemplateId = ParamUtil.getLong(actionRequest, "ddmTemplateId");
		String workflowDefinitionName = ParamUtil.getString(
			actionRequest, "workflowDefinitionName");
		int workflowDefinitionVersion = ParamUtil.getInteger(
			actionRequest, "workflowDefinitionVersion");

		String kaleoTaskFormPairsData = ParamUtil.getString(
			actionRequest, "kaleoTaskFormPairsData");

		KaleoTaskFormPairs kaleoKaleoTaskFormPairs = KaleoTaskFormPairs.parse(
			kaleoTaskFormPairsData);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			KaleoProcess.class.getName(), actionRequest);

		KaleoProcess kaleoProcess = null;

		if (kaleoProcessId <= 0) {
			kaleoProcess = kaleoProcessService.addKaleoProcess(
				groupId, ddmStructureId, nameMap, descriptionMap, ddmTemplateId,
				workflowDefinitionName, workflowDefinitionVersion,
				kaleoKaleoTaskFormPairs, serviceContext);
		}
		else {
			kaleoProcess = kaleoProcessService.updateKaleoProcess(
				kaleoProcessId, ddmStructureId, nameMap, descriptionMap,
				ddmTemplateId, workflowDefinitionName,
				workflowDefinitionVersion, kaleoKaleoTaskFormPairs,
				serviceContext);
		}

		String workflowDefinition = ParamUtil.getString(
			actionRequest, "workflowDefinition");

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			serviceContext.getUserId(), serviceContext.getCompanyId(), groupId,
			KaleoProcess.class.getName(), kaleoProcess.getKaleoProcessId(), 0,
			workflowDefinition);
	}

	@Reference
	private Localization _localization;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}