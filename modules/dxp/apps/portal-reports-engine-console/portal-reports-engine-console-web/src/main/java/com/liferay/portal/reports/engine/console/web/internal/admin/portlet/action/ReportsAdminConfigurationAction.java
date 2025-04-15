/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gavin Wan
 * @author Peter Shin
 */
@Component(
	property = "jakarta.portlet.name=" + ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
	service = ConfigurationAction.class
)
public class ReportsAdminConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/admin/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String tabs2 = ParamUtil.getString(actionRequest, "tabs2");

		if (tabs2.equals("delivery-email")) {
			_validateEmailDelivery(actionRequest);
		}
		else if (tabs2.equals("email-from")) {
			validateEmailFrom(actionRequest);
		}
		else if (tabs2.equals("notifications-email")) {
			_validateEmailNotifications(actionRequest);
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _validateEmailDelivery(ActionRequest actionRequest)
		throws Exception {

		String emailDeliverySubject = getParameter(
			actionRequest, "emailDeliverySubject");
		String emailDeliveryBody = getParameter(
			actionRequest, "emailDeliveryBody");

		if (Validator.isNull(emailDeliverySubject)) {
			SessionErrors.add(actionRequest, "emailDeliverySubject");
		}
		else if (Validator.isNull(emailDeliveryBody)) {
			SessionErrors.add(actionRequest, "emailDeliveryBody");
		}
	}

	private void _validateEmailNotifications(ActionRequest actionRequest)
		throws Exception {

		String emailNotificationsSubject = getParameter(
			actionRequest, "emailNotificationsSubject");
		String emailNotificationsBody = getParameter(
			actionRequest, "emailNotificationsBody");

		if (Validator.isNull(emailNotificationsSubject)) {
			SessionErrors.add(actionRequest, "emailNotificationsSubject");
		}
		else if (Validator.isNull(emailNotificationsBody)) {
			SessionErrors.add(actionRequest, "emailNotificationsBody");
		}
	}

}