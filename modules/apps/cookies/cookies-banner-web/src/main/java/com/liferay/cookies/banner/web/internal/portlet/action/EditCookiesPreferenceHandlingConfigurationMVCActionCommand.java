/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/instance_settings/edit_cookies_preference_handling_configuration"
	},
	service = MVCActionCommand.class
)
public class EditCookiesPreferenceHandlingConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ExtendedObjectClassDefinition.Scope scope = _getScope(actionRequest);

		boolean enabled = ParamUtil.getBoolean(actionRequest, "enabled");

		boolean explicitConsentMode = ParamUtil.getBoolean(
			actionRequest, "explicitConsentMode");

		if (!enabled) {
			explicitConsentMode = true;
		}

		try {
			_cookiesConfigurationProvider.
				updateCookiesPreferenceHandlingConfiguration(
					enabled, explicitConsentMode, scope,
					_getScopePK(actionRequest, scope));
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass(), exception);

			actionResponse.sendRedirect(
				ParamUtil.getString(actionRequest, "redirect"));
		}
	}

	private ExtendedObjectClassDefinition.Scope _getScope(
			ActionRequest actionRequest)
		throws Exception {

		String scopeString = ParamUtil.getString(actionRequest, "scope");

		if (Validator.isNull(scopeString)) {
			throw new PortalException("Unsupported scope: " + scopeString);
		}

		return ExtendedObjectClassDefinition.Scope.getScope(scopeString);
	}

	private long _getScopePK(
			ActionRequest actionRequest,
			ExtendedObjectClassDefinition.Scope scope)
		throws PortalException {

		long scopePK = ParamUtil.getLong(actionRequest, "scopePK");

		if ((scopePK == 0) &&
			(scope != ExtendedObjectClassDefinition.Scope.SYSTEM)) {

			throw new PortalException(
				"Invalid scope primary key 0 for scope " + scope);
		}

		return scopePK;
	}

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

}