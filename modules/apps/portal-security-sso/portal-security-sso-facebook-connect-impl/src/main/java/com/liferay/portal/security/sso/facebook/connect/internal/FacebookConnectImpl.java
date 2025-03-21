/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.facebook.connect.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.facebook.connect.configuration.FacebookConnectConfiguration;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectConstants;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectWebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implements the OAuth protocol for Facebook Connect.
 *
 * <p>
 * This class is utilized by many of the other Facebook Connect classes via
 * {@link com.liferay.portal.facebook.FacebookConnectUtil}, which exposes all of
 * its methods statically.
 * </p>
 *
 * @author Wilson Man
 * @author Mika Koivisto
 */
@Component(
	configurationPid = "com.liferay.portal.security.sso.facebook.connect.configuration.FacebookConnectConfiguration",
	service = FacebookConnect.class
)
public class FacebookConnectImpl implements FacebookConnect {

	@Override
	public String getAccessToken(long companyId, String redirect, String code) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		String url = facebookConnectConfiguration.oauthTokenURL();

		url = HttpComponentsUtil.addParameter(
			url, "client_id", facebookConnectConfiguration.appId());
		url = HttpComponentsUtil.addParameter(
			url, "client_secret", facebookConnectConfiguration.appSecret());
		url = HttpComponentsUtil.addParameter(url, "code", code);
		url = HttpComponentsUtil.addParameter(
			url, "redirect_uri",
			facebookConnectConfiguration.oauthRedirectURL());

		Http.Options options = new Http.Options();

		options.setLocation(url);

		try {
			String content = _http.URLtoString(options);

			JSONObject contentJSONObject = _jsonFactory.createJSONObject(
				content);

			String accessToken = contentJSONObject.getString("access_token");

			if (Validator.isNotNull(accessToken)) {
				return accessToken;
			}

			if (_log.isDebugEnabled()) {
				String appSecret = facebookConnectConfiguration.appSecret();

				if (!appSecret.isEmpty()) {
					url = HttpComponentsUtil.setParameter(
						url, "client_secret",
						StringBundler.concat(
							appSecret.charAt(0), "...redacted...",
							appSecret.charAt(appSecret.length() - 1)));
				}

				_log.debug(
					StringBundler.concat(
						"Unable to get access token for URL ", url,
						" because of response:", content));
			}
		}
		catch (Exception exception) {
			throw new SystemException(
				"Unable to retrieve Facebook access token", exception);
		}

		return null;
	}

	@Override
	public String getAccessTokenURL(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.oauthTokenURL();
	}

	@Override
	public String getAppId(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.appId();
	}

	@Override
	public String getAppSecret(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.appSecret();
	}

	@Override
	public String getAuthURL(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.oauthAuthURL();
	}

	@Override
	public JSONObject getGraphResources(
		long companyId, String path, String accessToken, String fields) {

		try {
			String graphURL = getGraphURL(companyId);

			String url = HttpComponentsUtil.addParameter(
				graphURL.concat(path), "access_token", accessToken);

			if (Validator.isNotNull(fields)) {
				url = HttpComponentsUtil.addParameter(url, "fields", fields);
			}

			Http.Options options = new Http.Options();

			options.setLocation(url);

			String json = _http.URLtoString(options);

			return _jsonFactory.createJSONObject(json);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public String getGraphURL(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.graphURL();
	}

	@Override
	public String getProfileImageURL(PortletRequest portletRequest) {
		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			portletRequest);

		httpServletRequest = _portal.getOriginalServletRequest(
			httpServletRequest);

		HttpSession httpSession = httpServletRequest.getSession();

		String facebookId = (String)httpSession.getAttribute(
			FacebookConnectWebKeys.FACEBOOK_USER_ID);

		if (Validator.isNull(facebookId)) {
			return null;
		}

		long companyId = _portal.getCompanyId(httpServletRequest);

		String token = (String)httpSession.getAttribute(
			FacebookConnectWebKeys.FACEBOOK_ACCESS_TOKEN);

		JSONObject jsonObject = getGraphResources(
			companyId, "/me", token, "id,picture");

		return jsonObject.getString("picture");
	}

	@Override
	public String getRedirectURL(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.oauthRedirectURL();
	}

	@Override
	public boolean isEnabled(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.enabled();
	}

	@Override
	public boolean isVerifiedAccountRequired(long companyId) {
		FacebookConnectConfiguration facebookConnectConfiguration =
			_getFacebookConnectConfiguration(companyId);

		return facebookConnectConfiguration.verifiedAccountRequired();
	}

	private FacebookConnectConfiguration _getFacebookConnectConfiguration(
		long companyId) {

		try {
			return _configurationProvider.getConfiguration(
				FacebookConnectConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, FacebookConnectConstants.SERVICE_NAME));
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get Facebook Connect configuration",
				configurationException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FacebookConnectImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}