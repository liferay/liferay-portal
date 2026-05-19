/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.servlet.taglib;

import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christian Moura
 */
@Component(
	property = "service.ranking:Integer=1000", service = DynamicInclude.class
)
public class ConsentManagementPlatformTopHeadDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-65299")) {

			return;
		}

		ConsentManagementPlatformConfiguration
			consentManagementPlatformConfiguration = null;

		try {
			consentManagementPlatformConfiguration =
				ConfigurationProviderUtil.getGroupConfiguration(
					ConsentManagementPlatformConfiguration.class,
					themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId());
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		if ((consentManagementPlatformConfiguration == null) ||
			!consentManagementPlatformConfiguration.enabled()) {

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.println(consentManagementPlatformConfiguration.scriptTag());

		String bridgeScript =
			consentManagementPlatformConfiguration.bridgeScript();

		if (Validator.isNotNull(bridgeScript)) {
			printWriter.println(bridgeScript);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#consent_management_platform");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConsentManagementPlatformTopHeadDynamicInclude.class);

}