/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.money.order.internal.display.context;

import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.method.money.order.internal.configuration.MoneyOrderGroupServiceConfiguration;
import com.liferay.commerce.payment.method.money.order.internal.constants.MoneyOrderCommercePaymentEngineMethodConstants;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Andrea Di Giorgi
 */
public class MoneyOrderCheckoutStepDisplayContext {

	public MoneyOrderCheckoutStepDisplayContext(
		ConfigurationProvider configurationProvider,
		HttpServletRequest httpServletRequest) {

		_configurationProvider = configurationProvider;
		_httpServletRequest = httpServletRequest;

		_commerceOrder = (CommerceOrder)httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);
	}

	public String getMessage() throws PortalException {
		if (_message != null) {
			return _message;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		MoneyOrderGroupServiceConfiguration
			moneyOrderGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					MoneyOrderGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						_commerceOrder.getGroupId(),
						MoneyOrderCommercePaymentEngineMethodConstants.
							SERVICE_NAME));

		String messageAsLocalizedXML =
			moneyOrderGroupServiceConfiguration.messageAsLocalizedXML();

		_message = LocalizationUtil.getLocalization(
			messageAsLocalizedXML, themeDisplay.getLanguageId());

		return _message;
	}

	private final CommerceOrder _commerceOrder;
	private final ConfigurationProvider _configurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private String _message;

}