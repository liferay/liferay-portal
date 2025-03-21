/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.connector.web.internal.portlet.action;

import com.liferay.commerce.avalara.connector.CommerceAvalaraConnector;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration;
import com.liferay.commerce.avalara.connector.web.internal.constants.CommerceAvalaraPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Katie Nesterovich
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceAvalaraPortletKeys.COMMERCE_AVALARA,
		"mvc.command.name=/commerce_avalara/edit_commerce_avalara_connector"
	},
	service = MVCActionCommand.class
)
public class EditCommerceAvalaraConnectorMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommerceTaxAvalara(actionRequest);

			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

			if (cmd.equals("verifyConnection")) {
				_verifyConnection(actionRequest);
				hideDefaultSuccessMessage(actionRequest);
				SessionMessages.add(actionRequest, "connectionSuccessful");
			}
		}
		catch (Throwable throwable) {
			SessionErrors.add(actionRequest, throwable.getClass(), throwable);

			hideDefaultErrorMessage(actionRequest);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	private void _updateCommerceTaxAvalara(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new CompanyServiceSettingsLocator(
				themeDisplay.getCompanyId(),
				CommerceAvalaraConnectorConfiguration.class.getName()));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String accountNumber = ParamUtil.getString(
			actionRequest, "accountNumber");

		modifiableSettings.setValue("accountNumber", accountNumber);

		String licenseKey = ParamUtil.getString(actionRequest, "licenseKey");

		modifiableSettings.setValue("licenseKey", licenseKey);

		String serviceURL = ParamUtil.getString(actionRequest, "serviceURL");

		modifiableSettings.setValue("serviceURL", serviceURL);

		modifiableSettings.store();
	}

	private void _verifyConnection(ActionRequest actionRequest)
		throws Exception {

		String accountNumber = ParamUtil.getString(
			actionRequest, "accountNumber");
		String licenseKey = ParamUtil.getString(actionRequest, "licenseKey");
		String serviceURL = ParamUtil.getString(actionRequest, "serviceURL");

		_commerceAvalaraConnector.verifyConnection(
			accountNumber, licenseKey, serviceURL);
	}

	@Reference
	private CommerceAvalaraConnector _commerceAvalaraConnector;

}