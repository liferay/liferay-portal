/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.display.context.helper;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ParameterMapSettingsLocator;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.reports.engine.console.configuration.ReportsGroupServiceEmailConfiguration;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsoleConstants;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Rafael Praxedes
 */
public class ReportsEngineRequestHelper extends BaseRequestHelper {

	public ReportsEngineRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);

		_renderRequest = (RenderRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);

		_portletPreferences = _renderRequest.getPreferences();
	}

	public PortletPreferences getPortletPreferences() {
		return _portletPreferences;
	}

	public RenderRequest getRenderRequest() {
		return _renderRequest;
	}

	public ReportsGroupServiceEmailConfiguration
			getReportsGroupServiceEmailConfiguration()
		throws PortalException {

		if (_reportsGroupServiceEmailConfiguration != null) {
			return _reportsGroupServiceEmailConfiguration;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (Validator.isNotNull(portletDisplay.getPortletResource())) {
			_reportsGroupServiceEmailConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					ReportsGroupServiceEmailConfiguration.class,
					new ParameterMapSettingsLocator(
						_renderRequest.getParameterMap(),
						new GroupServiceSettingsLocator(
							themeDisplay.getSiteGroupId(),
							ReportsEngineConsoleConstants.SERVICE_NAME)));
		}
		else {
			_reportsGroupServiceEmailConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					ReportsGroupServiceEmailConfiguration.class,
					new GroupServiceSettingsLocator(
						themeDisplay.getSiteGroupId(),
						ReportsEngineConsoleConstants.SERVICE_NAME));
		}

		return _reportsGroupServiceEmailConfiguration;
	}

	private final PortletPreferences _portletPreferences;
	private final RenderRequest _renderRequest;
	private ReportsGroupServiceEmailConfiguration
		_reportsGroupServiceEmailConfiguration;

}