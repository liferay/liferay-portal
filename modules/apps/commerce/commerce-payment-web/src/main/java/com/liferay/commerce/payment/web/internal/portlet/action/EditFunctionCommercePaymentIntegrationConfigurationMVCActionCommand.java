/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.portlet.action;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_PAYMENT_METHODS,
		"mvc.command.name=/commerce_payment/edit_function_commerce_payment_integration_configuration"
	},
	service = MVCActionCommand.class
)
public class EditFunctionCommercePaymentIntegrationConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.UPDATE)) {
			_updateCommercePaymentMethodGroupRel(actionRequest);
		}
	}

	private void _updateCommercePaymentMethodGroupRel(
			ActionRequest actionRequest)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(
				ParamUtil.getLong(actionRequest, "commerceChannelId"));

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelService.
				getCommercePaymentMethodGroupRel(
					commerceChannel.getGroupId(),
					ParamUtil.getString(
						actionRequest, "commercePaymentIntegrationKey"));

		commercePaymentMethodGroupRel.setTypeSettings(
			ParamUtil.getString(
				actionRequest, "settings--paymentIntegrationTypeSettings--"));

		_commercePaymentMethodGroupRelService.
			updateCommercePaymentMethodGroupRel(commercePaymentMethodGroupRel);
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

}