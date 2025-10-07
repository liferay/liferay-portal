/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.display.context;

import com.liferay.cookies.banner.web.internal.constants.CookiesBannerPortletKeys;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import java.util.Locale;
import java.util.Map;

/**
 * @author Eduardo García
 */
public class CookiesBannerDisplayContext
	extends BaseCookiesBannerDisplayContext {

	public CookiesBannerDisplayContext(
		CookiesConfigurationProvider cookiesConfigurationProvider,
		LayoutUtilityPageEntryLayoutProvider
			layoutUtilityPageEntryLayoutProvider,
		RenderRequest renderRequest) {

		super(
			cookiesConfigurationProvider,
			PortalUtil.getHttpServletRequest(renderRequest),
			layoutUtilityPageEntryLayoutProvider);
	}

	public Object getConfigurationURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				CookiesBannerPortletKeys.COOKIES_BANNER_CONFIGURATION)
		).setMVCPath(
			"/cookies_banner_configuration/view.jsp"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getContent(Locale locale) {
		LocalizedValuesMap contentLocalizedValuesMap =
			cookiesBannerConfiguration.content();

		return contentLocalizedValuesMap.get(locale);
	}

	public Map<String, Object> getContext(Locale locale) {
		LocalizedValuesMap titleLocalizedValuesMap =
			cookiesConsentConfiguration.title();

		return HashMapBuilder.<String, Object>put(
			"configurationNamespace",
			CookiesBannerPortletKeys.COOKIES_BANNER_CONFIGURATION
		).put(
			"configurationURL", getConfigurationURL()
		).put(
			"includeDeclineAllButton", isIncludeDeclineAllButton()
		).put(
			"optionalConsentCookieTypeNames",
			getConsentCookieTypeNamesJSONArray(getOptionalConsentCookieTypes())
		).put(
			"requiredConsentCookieTypeNames",
			getConsentCookieTypeNamesJSONArray(getRequiredConsentCookieTypes())
		).put(
			"title", titleLocalizedValuesMap.get(locale)
		).build();
	}

	public String getLinkDisplayText(Locale locale) {
		LocalizedValuesMap linkDisplayTextLocalizedValuesMap =
			cookiesBannerConfiguration.linkDisplayText();

		return linkDisplayTextLocalizedValuesMap.get(locale);
	}

	public String getPrivacyPolicyLink() throws PortalException {
		String privacyPolicyLink =
			cookiesBannerConfiguration.privacyPolicyLink();

		if (Validator.isNotNull(privacyPolicyLink)) {
			return privacyPolicyLink;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout =
			layoutUtilityPageEntryLayoutProvider.
				getDefaultLayoutUtilityPageEntryLayout(
					themeDisplay.getScopeGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY);

		if (layout != null) {
			return PortalUtil.getLayoutURL(layout, themeDisplay);
		}

		return StringPool.POUND;
	}

	public String getTitle(Locale locale) {
		LocalizedValuesMap titleLocalizedValuesMap =
			cookiesBannerConfiguration.title();

		return titleLocalizedValuesMap.get(locale);
	}

}