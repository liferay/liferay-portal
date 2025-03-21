/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.paypal.internal.portlet.action;

import com.liferay.commerce.payment.method.paypal.internal.constants.PayPalCommercePaymentMethodConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_PAYMENT_METHODS,
		"mvc.command.name=/commerce_payment_methods/edit_paypal_commerce_payment_method_configuration"
	},
	service = MVCActionCommand.class
)
public class EditPayPalCommercePaymentMethodConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.UPDATE)) {
			_updateCommercePaymentMethod(actionRequest);
		}
	}

	private void _updateCommercePaymentMethod(ActionRequest actionRequest)
		throws Exception {

		long commerceChannelId = ParamUtil.getLong(
			actionRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceChannel.getGroupId(),
				PayPalCommercePaymentMethodConstants.
					COMMERCE_PAYMENT_ENGINE_SERVICE_NAME));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String clientId = ParamUtil.getString(
			actionRequest, "settings--clientId--");

		modifiableSettings.setValue("clientId", clientId);

		String clientSecret = ParamUtil.getString(
			actionRequest, "settings--clientSecret--");

		if (!clientSecret.equals(Portal.TEMP_OBFUSCATION_VALUE)) {
			modifiableSettings.setValue("clientSecret", clientSecret);
		}

		String mode = ParamUtil.getString(actionRequest, "settings--mode--");

		modifiableSettings.setValue("mode", mode);

		String paymentAttempts = ParamUtil.getString(
			actionRequest, "settings--paymentAttempts--");

		modifiableSettings.setValue("paymentAttemptsMaxCount", paymentAttempts);

		String requestDetailsOption = ParamUtil.getString(
			actionRequest, "settings--request--");

		modifiableSettings.setValue("requestDetails", requestDetailsOption);

		modifiableSettings.store();
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

}