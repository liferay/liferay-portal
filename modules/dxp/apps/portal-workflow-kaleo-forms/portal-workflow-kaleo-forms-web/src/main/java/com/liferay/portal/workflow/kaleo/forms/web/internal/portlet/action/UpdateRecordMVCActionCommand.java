/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsActionKeys;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
		"mvc.command.name=/kaleo_forms_admin/update_record"
	},
	service = MVCActionCommand.class
)
public class UpdateRecordMVCActionCommand
	extends BaseKaleoFormsMVCActionCommand {

	/**
	 * Updates the <code>DDLRecord</code> (in the
	 * <code>com.liferay.dynamic.data.lists.api</code> module), checking the
	 * permission for the action ID
	 * <code>KaleoFormsActionKeys.COMPLETE_FORM</code> (in the
	 * <code>com.liferay.portal.workflow.kaleo.forms.api</code> module).
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

		checkKaleoProcessPermission(
			serviceContext, KaleoFormsActionKeys.COMPLETE_FORM);

		updateDDLRecord(serviceContext);
	}

}