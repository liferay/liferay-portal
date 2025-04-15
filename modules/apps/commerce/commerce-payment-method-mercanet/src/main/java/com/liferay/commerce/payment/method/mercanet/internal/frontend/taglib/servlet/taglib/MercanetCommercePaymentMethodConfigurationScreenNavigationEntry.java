/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.payment.constants.CommercePaymentScreenNavigationConstants;
import com.liferay.commerce.payment.method.mercanet.internal.MercanetCommercePaymentMethod;
import com.liferay.commerce.payment.method.mercanet.internal.configuration.MercanetGroupServiceConfiguration;
import com.liferay.commerce.payment.method.mercanet.internal.constants.MercanetCommercePaymentMethodConstants;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ParameterMapSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "screen.navigation.entry.order:Integer=20",
	service = ScreenNavigationEntry.class
)
public class MercanetCommercePaymentMethodConfigurationScreenNavigationEntry
	implements ScreenNavigationEntry<CommercePaymentMethodGroupRel> {

	public static final String
		ENTRY_KEY_MERCANET_COMMERCE_PAYMENT_METHOD_CONFIGURATION =
			"mercanet-configuration";

	@Override
	public String getCategoryKey() {
		return CommercePaymentScreenNavigationConstants.
			CATEGORY_KEY_COMMERCE_PAYMENT_METHOD_CONFIGURATION;
	}

	@Override
	public String getEntryKey() {
		return ENTRY_KEY_MERCANET_COMMERCE_PAYMENT_METHOD_CONFIGURATION;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			locale,
			CommercePaymentScreenNavigationConstants.
				CATEGORY_KEY_COMMERCE_PAYMENT_METHOD_CONFIGURATION);
	}

	@Override
	public String getScreenNavigationKey() {
		return CommercePaymentScreenNavigationConstants.
			SCREEN_NAVIGATION_KEY_COMMERCE_PAYMENT_METHOD;
	}

	@Override
	public boolean isVisible(
		User user, CommercePaymentMethodGroupRel commercePaymentMethod) {

		if (commercePaymentMethod == null) {
			return false;
		}

		return Objects.equals(
			commercePaymentMethod.getPaymentIntegrationKey(),
			MercanetCommercePaymentMethod.KEY);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			long commerceChannelId = ParamUtil.getLong(
				httpServletRequest, "commerceChannelId");

			CommerceChannel commerceChannel =
				_commerceChannelService.getCommerceChannel(commerceChannelId);

			MercanetGroupServiceConfiguration
				mercanetGroupServiceConfiguration =
					_configurationProvider.getConfiguration(
						MercanetGroupServiceConfiguration.class,
						new ParameterMapSettingsLocator(
							httpServletRequest.getParameterMap(),
							new GroupServiceSettingsLocator(
								commerceChannel.getGroupId(),
								MercanetCommercePaymentMethodConstants.
									SERVICE_NAME)));

			httpServletRequest.setAttribute(
				MercanetGroupServiceConfiguration.class.getName(),
				mercanetGroupServiceConfiguration);
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/configuration.jsp");
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.payment.method.mercanet)"
	)
	private ServletContext _servletContext;

}