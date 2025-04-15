/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=/commerce_payment/edit_function_commerce_tax_method_configuration"
	},
	service = MVCActionCommand.class
)
public class EditFunctionCommerceTaxMethodConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.UPDATE)) {
			_updateCommerceTaxMethod(actionRequest);
		}
	}

	private void _updateCommerceTaxMethod(ActionRequest actionRequest)
		throws Exception {

		long commerceTaxMethodId = ParamUtil.getLong(
			actionRequest, "commerceTaxMethodId");

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodService.getCommerceTaxMethod(commerceTaxMethodId);

		commerceTaxMethod.setTypeSettings(
			ParamUtil.getString(
				actionRequest, "settings--taxMethodTypeSettings--"));

		_commerceTaxMethodService.updateCommerceTaxMethod(commerceTaxMethod);
	}

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

}