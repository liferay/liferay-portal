/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.tax.CommerceTaxEngine;
import com.liferay.commerce.tax.engine.internal.FunctionCommerceTaxEngine;
import com.liferay.commerce.tax.engine.internal.constants.FunctionCommerceTaxEngineWebKeys;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.commerce.util.CommerceTaxEngineRegistry;
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
 * @author Ivica Cardic
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class FunctionCommerceTaxMethodConfigurationScreenNavigationEntry
	extends FunctionCommerceTaxMethodConfigurationScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceTaxMethod> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommerceTaxMethod commerceTaxMethod) {
		if (commerceTaxMethod == null) {
			return false;
		}

		CommerceTaxEngine commerceTaxEngine =
			_commerceTaxEngineRegistry.getCommerceTaxEngine(
				commerceTaxMethod.getEngineKey());

		return commerceTaxEngine instanceof FunctionCommerceTaxEngine;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			long commerceTaxMethodId = ParamUtil.getLong(
				httpServletRequest, "commerceTaxMethodId");

			CommerceTaxMethod commerceTaxMethod =
				_commerceTaxMethodLocalService.getCommerceTaxMethod(
					commerceTaxMethodId);

			UnicodeProperties typeSettingsUnicodeProperties =
				commerceTaxMethod.getTypeSettingsUnicodeProperties();

			if (typeSettingsUnicodeProperties.isEmpty()) {
				String commerceTaxMethodEngineKey = ParamUtil.getString(
					httpServletRequest, "commerceTaxMethodEngineKey");

				FunctionCommerceTaxEngine functionCommerceTaxEngine =
					(FunctionCommerceTaxEngine)
						_commerceTaxEngineRegistry.getCommerceTaxEngine(
							commerceTaxMethodEngineKey);

				httpServletRequest.setAttribute(
					FunctionCommerceTaxEngineWebKeys.IS_DEFAULT_VALUE,
					Boolean.TRUE);
				httpServletRequest.setAttribute(
					FunctionCommerceTaxEngineWebKeys.TAX_METHOD_TYPE_SETTINGS,
					functionCommerceTaxEngine.
						getTypeSettingsUnicodeProperties());
			}
			else {
				httpServletRequest.setAttribute(
					FunctionCommerceTaxEngineWebKeys.IS_DEFAULT_VALUE,
					Boolean.FALSE);
				httpServletRequest.setAttribute(
					FunctionCommerceTaxEngineWebKeys.TAX_METHOD_TYPE_SETTINGS,
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
		FunctionCommerceTaxMethodConfigurationScreenNavigationEntry.class);

	@Reference
	private CommerceTaxEngineRegistry _commerceTaxEngineRegistry;

	@Reference
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.tax.engine)"
	)
	private ServletContext _servletContext;

}