/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.servlet.taglib;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.provider.GlobalPrivacyControlProvider;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryce Osterhaus
 */
@Component(service = DynamicInclude.class)
public class EnableThirdPartyCookiesBottomJSDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration;

		try {
			cookiesPreferenceHandlingConfiguration =
				_cookiesConfigurationProvider.
					getCookiesPreferenceHandlingConfiguration(themeDisplay);
		}
		catch (Exception exception) {
			_log.error(exception);

			return;
		}

		if (!cookiesPreferenceHandlingConfiguration.enabled()) {
			return;
		}

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		ScriptData scriptData = new ScriptData();

		if (_globalPrivacyControlProvider.isSignalActive(httpServletRequest)) {
			scriptData.append(
				_portal.getPortletId(httpServletRequest),
				new JSFragment(
					StringBundler.concat(
						"suppressThirdPartyCookies(",
						cookiesPreferenceHandlingConfiguration.
							consentRenewalPeriod(),
						", \"",
						cookiesPreferenceHandlingConfiguration.
							consentRenewalPeriodTimeUnit(),
						"\", ",
						cookiesPreferenceHandlingConfiguration.
							dissentRenewalPeriod(),
						", \"",
						cookiesPreferenceHandlingConfiguration.
							dissentRenewalPeriodTimeUnit(),
						"\", [\"",
						CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL, "\", \"",
						CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
						"\", \"",
						CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
						"\"], [\"",
						CookiesConstants.NAME_CONSENT_TYPE_NECESSARY, "\"], ",
						cookiesPreferenceHandlingConfiguration.storeConsent(),
						");"),
					Arrays.asList(
						ESImportUtil.getESImport(
							absolutePortalURLBuilder,
							"{suppressThirdPartyCookies} from " +
								"cookies-banner-web/index.js"))));
		}
		else {
			scriptData.append(
				_portal.getPortletId(httpServletRequest),
				new JSFragment(
					"runThirdPartyCookiesInterval();",
					Arrays.asList(
						ESImportUtil.getESImport(
							absolutePortalURLBuilder,
							"{runThirdPartyCookiesInterval} from " +
								"cookies-banner-web/index.js"))));
		}

		scriptData.writeTo(httpServletResponse.getWriter());
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EnableThirdPartyCookiesBottomJSDynamicInclude.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference
	private GlobalPrivacyControlProvider _globalPrivacyControlProvider;

	@Reference
	private Portal _portal;

}