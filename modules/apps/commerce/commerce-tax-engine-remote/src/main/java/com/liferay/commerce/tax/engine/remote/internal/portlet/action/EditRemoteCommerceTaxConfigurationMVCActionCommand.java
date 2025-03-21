/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.remote.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.tax.engine.remote.internal.constants.RemoteCommerceTaxEngineConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=editRemoteCommerceTaxConfiguration"
	},
	service = MVCActionCommand.class
)
public class EditRemoteCommerceTaxConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_portal.getScopeGroupId(actionRequest),
				RemoteCommerceTaxEngineConstants.SERVICE_NAME));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String taxValueEndpointAuthorizationToken = ParamUtil.getString(
			actionRequest, "settings--taxValueEndpointAuthorizationToken--");

		if (Validator.isNotNull(taxValueEndpointAuthorizationToken)) {
			modifiableSettings.setValue(
				"taxValueEndpointAuthorizationToken",
				taxValueEndpointAuthorizationToken);
		}

		String taxValueEndpointURL = ParamUtil.getString(
			actionRequest, "settings--taxValueEndpointURL--");

		_validate(taxValueEndpointURL);

		modifiableSettings.setValue(
			"taxValueEndpointURL", String.valueOf(taxValueEndpointURL));

		modifiableSettings.store();
	}

	private void _validate(String taxCalculationEndpointURL) throws Exception {
		if (!Validator.isUrl(taxCalculationEndpointURL)) {
			throw new PortletException(
				"Invalid URL " + taxCalculationEndpointURL);
		}
	}

	@Reference
	private Portal _portal;

}