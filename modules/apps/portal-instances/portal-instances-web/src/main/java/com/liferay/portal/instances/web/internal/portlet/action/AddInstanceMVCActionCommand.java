/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.portal.instances.web.internal.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalInstances;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán Grande
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortalInstancesPortletKeys.PORTAL_INSTANCES,
		"mvc.command.name=/portal_instances/add_instance"
	},
	service = MVCActionCommand.class
)
public class AddInstanceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_addInstance(actionRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			String errorMessage = "an-unexpected-error-occurred";

			if (exception instanceof CompanyMxException) {
				errorMessage = "please-enter-a-valid-mail-domain";
			}
			else if (exception instanceof CompanyVirtualHostException) {
				errorMessage = "please-enter-a-valid-virtual-host";
			}
			else if (exception instanceof CompanyWebIdException) {
				errorMessage = "please-enter-a-valid-web-id";
			}
			else if (exception instanceof
						ContactNameException.MustHaveFirstName) {

				errorMessage = "please-enter-a-valid-first-name";
			}
			else if (exception instanceof
						ContactNameException.MustHaveLastName) {

				errorMessage = "please-enter-a-valid-last-name";
			}
			else if (exception instanceof
						ContactNameException.MustHaveMiddleName) {

				errorMessage = "please-enter-a-valid-middle-name";
			}
			else if (exception instanceof
						ContactNameException.MustHaveValidFullName) {

				errorMessage =
					"please-enter-a-valid-first-middle-and-last-name";
			}
			else if (exception instanceof UserEmailAddressException) {
				errorMessage = "please-enter-a-valid-email-address";
			}
			else if (exception instanceof UserPasswordException) {
				errorMessage = "please-enter-a-valid-password";
			}
			else if (exception instanceof UserScreenNameException) {
				errorMessage = "please-enter-a-valid-screen-name";
			}

			jsonObject.put(
				"error",
				_language.get(actionRequest.getLocale(), errorMessage));

			hideDefaultSuccessMessage(actionRequest);
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private void _addInstance(ActionRequest actionRequest) throws Exception {
		String webId = ParamUtil.getString(actionRequest, "webId");
		String virtualHostname = ParamUtil.getString(
			actionRequest, "virtualHostname");
		String mx = ParamUtil.getString(actionRequest, "mx");
		int maxUsers = ParamUtil.getInteger(actionRequest, "maxUsers");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		String defaultAdminPassword = ParamUtil.getString(
			actionRequest, "defaultAdminPassword", null);
		String defaultAdminScreenName = ParamUtil.getString(
			actionRequest, "defaultAdminScreenName", null);
		String defaultAdminEmailAddress = ParamUtil.getString(
			actionRequest, "defaultAdminEmailAddress", null);
		String defaultAdminFirstName = ParamUtil.getString(
			actionRequest, "defaultAdminFirstName", null);
		String defaultAdminMiddleName = ParamUtil.getString(
			actionRequest, "defaultAdminMiddleName", null);
		String defaultAdminLastName = ParamUtil.getString(
			actionRequest, "defaultAdminLastName", null);

		PortalInstances.addCompany(
			ParamUtil.getString(actionRequest, "siteInitializerKey"),
			() -> _companyService.addCompany(
				null, webId, virtualHostname, mx, maxUsers, active,
				defaultAdminPassword, defaultAdminScreenName,
				defaultAdminEmailAddress, defaultAdminFirstName,
				defaultAdminMiddleName, defaultAdminLastName));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddInstanceMVCActionCommand.class);

	@Reference
	private CompanyService _companyService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}