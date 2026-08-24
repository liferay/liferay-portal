/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.web.internal.portlet.action;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration;
import com.liferay.portal.security.fips.constants.FIPSPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FIPSPortletKeys.FIPS_ADMIN,
		"mvc.command.name=/fips_admin/edit_fips_session_configuration"
	},
	service = MVCActionCommand.class
)
public class EditFIPSSessionConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			_configurationProvider.saveCompanyConfiguration(
				FIPSSessionConfiguration.class, themeDisplay.getCompanyId(),
				HashMapDictionaryBuilder.<String, Object>put(
					"idleTimeout",
					ParamUtil.getInteger(actionRequest, "idleTimeout")
				).put(
					"idleTimeoutTimeUnit",
					ParamUtil.getString(actionRequest, "idleTimeoutTimeUnit")
				).put(
					"maximumAge",
					ParamUtil.getInteger(actionRequest, "maximumAge")
				).put(
					"maximumAgeTimeUnit",
					ParamUtil.getString(actionRequest, "maximumAgeTimeUnit")
				).build());
		}
		catch (Exception exception) {
			if (_isConfigurationModelListenerException(exception)) {
				SessionErrors.add(
					actionRequest,
					ConfigurationModelListenerException.class.getName());

				hideDefaultErrorMessage(actionRequest);
			}
			else {
				throw exception;
			}
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private boolean _isConfigurationModelListenerException(
		Throwable throwable) {

		while (throwable != null) {
			if (throwable instanceof ConfigurationModelListenerException) {
				return true;
			}

			throwable = throwable.getCause();
		}

		return false;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}