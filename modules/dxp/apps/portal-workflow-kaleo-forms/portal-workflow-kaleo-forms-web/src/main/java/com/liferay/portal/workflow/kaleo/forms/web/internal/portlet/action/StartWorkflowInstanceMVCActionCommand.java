/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
		"mvc.command.name=/kaleo_forms_admin/start_workflow_instance"
	},
	service = MVCActionCommand.class
)
public class StartWorkflowInstanceMVCActionCommand
	extends BaseKaleoFormsMVCActionCommand {

	/**
	 * Starts a <code>WorkflowInstance</code> (in
	 * <code>com.liferay.portal.kernel</code>) if the user has the
	 * <code>SUBMIT</code> permission. This method also updates the
	 * <code>DDLRecord</code> (in the
	 * <code>com.liferay.dynamic.data.lists.api</code> module).
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

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDLRecord.class.getName(),
			portal.getUploadPortletRequest(actionRequest));

		checkKaleoProcessPermission(serviceContext, ActionKeys.SUBMIT);

		DDLRecord ddlRecord = updateDDLRecord(serviceContext);

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
			serviceContext.getUserId(), KaleoProcess.class.getName(),
			ddlRecord.getRecordId(), ddlRecord, serviceContext);
	}

}