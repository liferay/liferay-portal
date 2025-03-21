/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.internal.FunctionCommerceShippingEngine;
import com.liferay.commerce.shipping.engine.internal.constants.FunctionCommerceShippingEngineWebKeys;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class FunctionCommerceShippingMethodConfigurationScreenNavigationEntry
	extends FunctionCommerceShippingMethodConfigurationScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceShippingMethod> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(
		User user, CommerceShippingMethod commerceShippingMethod) {

		if (commerceShippingMethod == null) {
			return false;
		}

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		return commerceShippingEngine instanceof FunctionCommerceShippingEngine;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			long commerceShippingMethodId = ParamUtil.getLong(
				httpServletRequest, "commerceShippingMethodId");

			CommerceShippingMethod commerceShippingMethod =
				_commerceShippingMethodLocalService.getCommerceShippingMethod(
					commerceShippingMethodId);

			UnicodeProperties typeSettingsUnicodeProperties =
				commerceShippingMethod.getTypeSettingsUnicodeProperties();

			if (typeSettingsUnicodeProperties.isEmpty()) {
				String commerceShippingMethodEngineKey = ParamUtil.getString(
					httpServletRequest, "commerceShippingMethodEngineKey");

				FunctionCommerceShippingEngine functionCommerceShippingEngine =
					(FunctionCommerceShippingEngine)
						_commerceShippingEngineRegistry.
							getCommerceShippingEngine(
								commerceShippingMethodEngineKey);

				httpServletRequest.setAttribute(
					FunctionCommerceShippingEngineWebKeys.IS_DEFAULT_VALUE,
					Boolean.TRUE);
				httpServletRequest.setAttribute(
					FunctionCommerceShippingEngineWebKeys.
						SHIPPING_METHOD_TYPE_SETTINGS,
					functionCommerceShippingEngine.
						getTypeSettingsUnicodeProperties());
			}
			else {
				httpServletRequest.setAttribute(
					FunctionCommerceShippingEngineWebKeys.IS_DEFAULT_VALUE,
					Boolean.FALSE);
				httpServletRequest.setAttribute(
					FunctionCommerceShippingEngineWebKeys.
						SHIPPING_METHOD_TYPE_SETTINGS,
					typeSettingsUnicodeProperties);
			}

			_jspRenderer.renderJSP(
				_servletContext, httpServletRequest, httpServletResponse,
				"/configuration.jsp");
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FunctionCommerceShippingMethodConfigurationScreenNavigationEntry.class);

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.shipping.engine)"
	)
	private ServletContext _servletContext;

}