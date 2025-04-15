/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class BaseJSPSettingsConfigurationAction
	extends SettingsConfigurationAction
	implements ConfigurationAction, ResourceServingConfigurationAction {

	public String getJspPath(HttpServletRequest httpServletRequest) {
		PortletConfig selPortletConfig = getSelPortletConfig(
			httpServletRequest);

		String configTemplate = selPortletConfig.getInitParameter(
			"config-template");

		if (Validator.isNotNull(configTemplate)) {
			return configTemplate;
		}

		String configJSP = selPortletConfig.getInitParameter("config-jsp");

		if (Validator.isNotNull(configJSP)) {
			return configJSP;
		}

		return "/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		PortletBag portletBag = PortletBagPool.get(
			PortletIdCodec.decodePortletName(
				ParamUtil.getString(httpServletRequest, "portletResource")));

		ServletContext servletContext = portletBag.getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(getJspPath(httpServletRequest));

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			_log.error(
				"Unable to include JSP " + getJspPath(httpServletRequest),
				servletException);

			throw new IOException(
				"Unable to include " + getJspPath(httpServletRequest),
				servletException);
		}
	}

	protected void removeDefaultValue(
		PortletRequest portletRequest, ModifiableSettings modifiableSettings,
		String key, LocalizedValuesMap localizedMap) {

		String defaultValue = localizedMap.getDefaultValue();

		Map<Locale, String> localizedMapValues = localizedMap.getValues();

		for (Locale locale : localizedMapValues.keySet()) {
			String languageKeyId = key + "_" + LocaleUtil.toLanguageId(locale);

			String value = getParameter(portletRequest, languageKeyId);

			if (defaultValue.equals(value) ||
				StringUtil.equalsIgnoreBreakLine(defaultValue, value)) {

				modifiableSettings.reset(languageKeyId);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPSettingsConfigurationAction.class);

}