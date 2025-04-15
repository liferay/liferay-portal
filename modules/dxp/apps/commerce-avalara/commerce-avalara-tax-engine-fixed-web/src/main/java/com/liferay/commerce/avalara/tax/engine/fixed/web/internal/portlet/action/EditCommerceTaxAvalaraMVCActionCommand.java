/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.tax.engine.fixed.web.internal.portlet.action;

import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorChannelConfiguration;
import com.liferay.commerce.avalara.connector.dispatch.CommerceAvalaraDispatchTrigger;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Calvin Keum
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=/commerce_tax_methods/edit_commerce_tax_avalara"
	},
	service = MVCActionCommand.class
)
public class EditCommerceTaxAvalaraMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals("runNow")) {
			_commerceAvalaraDispatchTriggerHelper.runJob(
				_getCommerceTaxMethod(actionRequest));
		}
		else {
			_updateCommerceTaxAvalara(actionRequest);
		}
	}

	private CommerceTaxMethod _getCommerceTaxMethod(ActionRequest actionRequest)
		throws Exception {

		long commerceTaxMethodId = ParamUtil.getLong(
			actionRequest, "commerceTaxMethodId");

		return _commerceTaxMethodService.getCommerceTaxMethod(
			commerceTaxMethodId);
	}

	private void _updateCommerceTaxAvalara(ActionRequest actionRequest)
		throws Exception {

		CommerceTaxMethod commerceTaxMethod = _getCommerceTaxMethod(
			actionRequest);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceTaxMethod.getGroupId(),
				CommerceAvalaraConnectorChannelConfiguration.class.getName()));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String companyCode = ParamUtil.getString(actionRequest, "companyCode");

		modifiableSettings.setValue("companyCode", companyCode);

		Boolean disableDocumentRecording = ParamUtil.getBoolean(
			actionRequest, "disableDocumentRecording");

		modifiableSettings.setValue(
			"disableDocumentRecording",
			String.valueOf(disableDocumentRecording));

		modifiableSettings.store();
	}

	@Reference
	private CommerceAvalaraDispatchTrigger
		_commerceAvalaraDispatchTriggerHelper;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

}