/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.servlet.taglib;

import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.redirect.OAuth2RedirectURIInterpolator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(service = DynamicInclude.class)
public class OAuth2ProviderTopJSPDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-48862")) {

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		List<OAuth2Application> oAuth2Applications =
			_oAuth2ApplicationLocalService.getOAuth2Applications(
				_portal.getCompanyId(httpServletRequest),
				ClientProfile.USER_AGENT_APPLICATION.id());

		for (OAuth2Application oAuth2Application : oAuth2Applications) {
			jsonObject.put(
				oAuth2Application.getExternalReferenceCode(),
				_jsonFactory.createJSONObject(
				).put(
					"clientId", oAuth2Application.getClientId()
				).put(
					"homePageURL", oAuth2Application.getHomePageURL()
				).put(
					"redirectURIs",
					_jsonFactory.createJSONArray(
						OAuth2RedirectURIInterpolator.
							interpolateRedirectURIsList(
								httpServletRequest,
								oAuth2Application.getRedirectURIsList(),
								_portal))
				));
		}

		String url =
			_portal.getPortalURL(httpServletRequest) + _portal.getPathContext();

		String string = StringBundler.concat(
			"<script",
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest),
			" data-senna-track=\"temporary\" type=\"",
			ContentTypes.TEXT_JAVASCRIPT,
			"\">window.Liferay = Liferay || {}; window.Liferay.OAuth2 = ",
			"{getAuthorizeURL: function() {return '", url,
			"/o/oauth2/authorize';}, getBuiltInRedirectURL: function() ",
			"{return '", url,
			"/o/oauth2/redirect';}, getIntrospectURL: function() { return '",
			url, "/o/oauth2/introspect';}, getTokenURL: function() {return '",
			url, "/o/oauth2/token';}, getUserAgentApplication: ",
			"function(externalReferenceCode) {return ",
			"Liferay.OAuth2._userAgentApplications[externalReferenceCode];}, ",
			"_userAgentApplications: ", jsonObject, "}</script>");

		printWriter.write(string);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private Portal _portal;

}