/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;

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
		"mvc.command.name=/kaleo_forms_admin/delete_kaleo_definition_versions"
	},
	service = MVCActionCommand.class
)
public class DeleteKaleoDefinitionVersionsMVCActionCommand
	extends BaseKaleoFormsMVCActionCommand {

	/**
	 * Deletes all versions of a <code>KaleoDraftDefinition</code> (in the
	 * <code>com.liferay.portal.workflow.kaleo.designer.api</code> module) by
	 * using its name from the action request.
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

		String name = ParamUtil.getString(actionRequest, "name");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersions(
			serviceContext.getCompanyId(), name);
	}

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

}