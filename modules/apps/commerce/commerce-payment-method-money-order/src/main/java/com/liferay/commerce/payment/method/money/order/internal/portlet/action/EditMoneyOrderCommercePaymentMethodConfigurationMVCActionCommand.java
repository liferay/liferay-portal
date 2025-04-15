/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.money.order.internal.portlet.action;

import com.liferay.commerce.payment.method.money.order.internal.constants.MoneyOrderCommercePaymentEngineMethodConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_PAYMENT_METHODS,
		"mvc.command.name=/commerce_payment_methods/edit_money_order_commerce_payment_method_configuration"
	},
	service = MVCActionCommand.class
)
public class EditMoneyOrderCommercePaymentMethodConfigurationMVCActionCommand
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
				MoneyOrderCommercePaymentEngineMethodConstants.SERVICE_NAME));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String showMessagePage = ParamUtil.getString(
			actionRequest, "settings--showMessagePage--");

		modifiableSettings.setValue("showMessagePage", showMessagePage);

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "settings--messageAsLocalizedXML_");

		Map<String, String> messageMap = new HashMap<>();

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			messageMap.put(entry.getKey(), entry.getValue());
		}

		String messageAsLocalizedXML = _localization.getXml(
			messageMap, StringPool.BLANK, "messageAsLocalizedXML");

		modifiableSettings.setValue(
			"messageAsLocalizedXML", messageAsLocalizedXML);

		modifiableSettings.store();
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private Localization _localization;

}