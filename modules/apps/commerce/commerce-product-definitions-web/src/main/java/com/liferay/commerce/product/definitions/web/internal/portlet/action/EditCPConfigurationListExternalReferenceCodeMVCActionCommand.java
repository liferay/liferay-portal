/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.DuplicateCPConfigurationListExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationListException;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_CONFIGURATION_LISTS,
		"mvc.command.name=/cp_configuration_lists/edit_cp_configuration_list_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCPConfigurationListExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long cpConfigurationListId = ParamUtil.getLong(
				actionRequest, "cpConfigurationListId");

			String externalReferenceCode = ParamUtil.getString(
				actionRequest, "externalReferenceCode");

			_cpConfigurationListService.updateExternalReferenceCode(
				cpConfigurationListId, externalReferenceCode);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCPConfigurationListExternalReferenceCodeException ||
				exception instanceof NoSuchCPConfigurationListException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				_log.error(exception);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPConfigurationListExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CPConfigurationListService _cpConfigurationListService;

}