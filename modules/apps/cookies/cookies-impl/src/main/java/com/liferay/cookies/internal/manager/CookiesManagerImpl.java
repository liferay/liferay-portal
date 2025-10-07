/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.manager;

import com.google.common.net.InternetDomainName;

import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.UnsupportedCookieException;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tamas Molnar
 * @author Brian Wing Shun Chan
 * @author Olivér Kecskeméty
 */
@Component(
	configurationPid = "com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration",
	property = {
		"cookies.functional=" + CookiesConstants.NAME_GUEST_LANGUAGE_ID,
		"cookies.necessary=" + CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL,
		"cookies.necessary=" + CookiesConstants.NAME_CONSENT_TYPE_NECESSARY,
		"cookies.necessary=" + CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
		"cookies.necessary=" + CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
		"cookies.necessary=" + CookiesConstants.NAME_COOKIE_SUPPORT,
		"cookies.necessary=" + CookiesConstants.NAME_USER_CONSENT_CONFIGURED
	},
	service = CookiesManager.class
)
public class CookiesManagerImpl implements CookiesManager {

	@Override
	public boolean addCookie(
		Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		boolean secure = false;

		if (httpServletRequest != null) {
			secure = _portal.isSecure(httpServletRequest);
		}
		else if (cookie != null) {
			secure = cookie.getSecure();
		}

		return addCookie(
			cookie, httpServletRequest, httpServletResponse, secure);
	}

	@Override
	public boolean addCookie(
		Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean secure) {

		if (_internalCookies.get(cookie.getName()) != null) {
			return addCookie(
				_internalCookies.get(cookie.getName()), cookie,
				httpServletRequest, httpServletResponse, secure);
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"The following cookie is trying to be added without consent " +
					"type: " + cookie.getName());
		}

		if (_knownCookies.get(cookie.getName()) != null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"The cookie will be added with the consent type used " +
						"previously. Use the API with explicitly declared " +
							"consent type.");
			}

			return addCookie(
				_knownCookies.get(cookie.getName()), cookie, httpServletRequest,
				httpServletResponse, secure);
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"The cookie will be deleted. Use the API with explicitly " +
					"declared consent type.");
		}

		return deleteCookies(
			CookiesManagerUtil.getDomain(httpServletRequest),
			httpServletRequest, httpServletResponse, cookie.getName());
	}

	@Override
	public boolean addCookie(
		int consentType, Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return addCookie(
			consentType, cookie, httpServletRequest, httpServletResponse,
			_portal.isSecure(httpServletRequest));
	}

	@Override
	public boolean addCookie(
		int consentType, Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean secure) {

		if (!_SESSION_ENABLE_PERSISTENT_COOKIES) {
			return false;
		}

		if (cookie.getMaxAge() != 0) {
			CookiesPreferenceHandlingConfiguration
				cookiesPreferenceHandlingConfiguration =
					_getCookiesPreferenceHandlingConfiguration(
						httpServletRequest);

			if (!cookiesPreferenceHandlingConfiguration.enabled()) {
				_deleteCookieConsentCookies(
					httpServletRequest, httpServletResponse);
			}
			else if (!hasConsentType(consentType, httpServletRequest)) {
				return false;
			}
		}

		cookie.setPath(_getContextPath(httpServletRequest));

		// LEP-5175

		cookie.setSecure(secure);

		String originalCookieValue = cookie.getValue();

		String encodedCookieValue = originalCookieValue;

		if (isEncodedCookie(cookie.getName())) {
			encodedCookieValue = UnicodeFormatter.bytesToHex(
				originalCookieValue.getBytes());

			if (_log.isDebugEnabled()) {
				_log.debug("Add encoded cookie " + cookie.getName());
				_log.debug("Original value " + originalCookieValue);
				_log.debug("Hex encoded value " + encodedCookieValue);
			}
		}

		cookie.setValue(encodedCookieValue);
		cookie.setVersion(0);

		httpServletResponse.addCookie(cookie);

		if (httpServletRequest != null) {
			Map<String, Cookie> cookiesMap = _getCookiesMap(httpServletRequest);

			cookiesMap.put(StringUtil.toUpperCase(cookie.getName()), cookie);
		}

		if (_log.isWarnEnabled() &&
			(_knownCookies.get(cookie.getName()) != null) &&
			(_knownCookies.get(cookie.getName()) != consentType)) {

			_log.warn(
				StringBundler.concat(
					"The ", cookie.getName(),
					" cookie was previously added with consent type ",
					_knownCookies.get(cookie.getName()),
					" and will now be modified to consent type ", consentType));
		}

		_knownCookies.put(cookie.getName(), consentType);

		return true;
	}

	@Override
	public boolean addSupportCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Cookie cookieSupportCookie = new Cookie(
			CookiesConstants.NAME_COOKIE_SUPPORT, "true");

		cookieSupportCookie.setMaxAge(CookiesConstants.MAX_AGE);

		return addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookieSupportCookie,
			httpServletRequest, httpServletResponse,
			_portal.isSecure(httpServletRequest));
	}

	@Override
	public boolean deleteCookies(
		String domain, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String... cookieNames) {

		if (!_SESSION_ENABLE_PERSISTENT_COOKIES) {
			return false;
		}

		Map<String, Cookie> cookiesMap = _getCookiesMap(httpServletRequest);

		for (String cookieName : cookieNames) {
			Cookie cookie = cookiesMap.remove(
				StringUtil.toUpperCase(cookieName));

			if (cookie == null) {
				continue;
			}

			if (domain != null) {
				cookie.setDomain(domain);
			}

			cookie.setMaxAge(0);
			cookie.setPath(_getContextPath(httpServletRequest));
			cookie.setSecure(_portal.isSecure(httpServletRequest));
			cookie.setValue(StringPool.BLANK);

			httpServletResponse.addCookie(cookie);
		}

		return true;
	}

	@Override
	public String getCookieValue(
		String cookieName, HttpServletRequest httpServletRequest) {

		return getCookieValue(cookieName, httpServletRequest, true);
	}

	@Override
	public String getCookieValue(
		String cookieName, HttpServletRequest httpServletRequest,
		boolean toUpperCase) {

		if (!_SESSION_ENABLE_PERSISTENT_COOKIES) {
			return null;
		}

		String cookieValue = _getCookieValue(
			cookieName, httpServletRequest, toUpperCase);

		if ((cookieValue == null) || !isEncodedCookie(cookieName)) {
			return cookieValue;
		}

		try {
			String encodedCookieValue = cookieValue;

			String originalCookieValue = new String(
				UnicodeFormatter.hexToBytes(encodedCookieValue));

			if (_log.isDebugEnabled()) {
				_log.debug("Get encoded cookie " + cookieName);
				_log.debug("Hex encoded value " + encodedCookieValue);
				_log.debug("Original value " + originalCookieValue);
			}

			return originalCookieValue;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return cookieValue;
		}
	}

	@Override
	public String getDomain(HttpServletRequest httpServletRequest) {

		// See LEP-4602 and	LEP-4618.

		if (Validator.isNotNull(_SESSION_COOKIE_DOMAIN)) {
			return _SESSION_COOKIE_DOMAIN;
		}

		if (_SESSION_COOKIE_USE_FULL_HOSTNAME) {
			return StringPool.BLANK;
		}

		return getDomain(httpServletRequest.getServerName());
	}

	@Override
	public String getDomain(String host) {

		// See LEP-4602 and LEP-4645.

		if (host == null) {
			return null;
		}

		// See LEP-5595.

		if (Validator.isIPAddress(host)) {
			return host;
		}

		InternetDomainName internetDomainName = InternetDomainName.from(host);

		if (internetDomainName.isPublicSuffix()) {
			return null;
		}

		if (internetDomainName.isTopPrivateDomain()) {
			return internetDomainName.toString();
		}

		int x = host.indexOf(CharPool.PERIOD);

		if (x <= 0) {
			return null;
		}

		int y = host.indexOf(CharPool.PERIOD, x + 1);

		if (y <= 0) {
			return host;
		}

		return host.substring(x + 1);
	}

	@Override
	public boolean hasConsentType(
		int consentType, HttpServletRequest httpServletRequest) {

		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			return true;
		}

		String consentCookieName = StringPool.BLANK;

		if (consentType == CookiesConstants.CONSENT_TYPE_FUNCTIONAL) {
			consentCookieName = CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERFORMANCE) {
			consentCookieName = CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERSONALIZATION) {
			consentCookieName =
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION;
		}

		String consentCookieValue = getCookieValue(
			consentCookieName, httpServletRequest);

		if (Validator.isNotNull(consentCookieValue)) {
			return GetterUtil.getBoolean(consentCookieValue);
		}

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCookiesPreferenceHandlingConfiguration(httpServletRequest);

		return !cookiesPreferenceHandlingConfiguration.explicitConsentMode();
	}

	@Override
	public boolean hasSessionId(HttpServletRequest httpServletRequest) {
		String cookieValue = getCookieValue(
			CookiesConstants.NAME_JSESSIONID, httpServletRequest, false);

		if (cookieValue != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isEncodedCookie(String cookieName) {
		if (cookieName.equals(CookiesConstants.NAME_ID) ||
			cookieName.equals(CookiesConstants.NAME_LOGIN) ||
			cookieName.equals(CookiesConstants.NAME_PASSWORD) ||
			cookieName.equals(CookiesConstants.NAME_USER_UUID)) {

			return true;
		}

		return false;
	}

	@Override
	public void validateSupportCookie(HttpServletRequest httpServletRequest)
		throws UnsupportedCookieException {

		if (_SESSION_ENABLE_PERSISTENT_COOKIES &&
			_SESSION_TEST_COOKIE_SUPPORT &&
			Validator.isNull(
				getCookieValue(
					CookiesConstants.NAME_COOKIE_SUPPORT, httpServletRequest,
					false))) {

			throw new UnsupportedCookieException();
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		for (String name : _getProperty(properties, "cookies.functional")) {
			_internalCookies.put(
				name, CookiesConstants.CONSENT_TYPE_FUNCTIONAL);
		}

		for (String name : _getProperty(properties, "cookies.necessary")) {
			_internalCookies.put(name, CookiesConstants.CONSENT_TYPE_NECESSARY);
		}

		for (String name : _getProperty(properties, "cookies.performance")) {
			_internalCookies.put(
				name, CookiesConstants.CONSENT_TYPE_PERFORMANCE);
		}

		for (String name :
				_getProperty(properties, "cookies.personalization")) {

			_internalCookies.put(
				name, CookiesConstants.CONSENT_TYPE_PERSONALIZATION);
		}
	}

	private boolean _deleteCookieConsentCookies(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		boolean hasConsentTypeFunctionalCookie = Validator.isNotNull(
			getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL,
				httpServletRequest));
		boolean hasConsentTypePerformanceCookie = Validator.isNotNull(
			getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
				httpServletRequest));
		boolean hasConsentTypePersonalizationCookie = Validator.isNotNull(
			getCookieValue(
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
				httpServletRequest));
		boolean hasUserConsentConfiguredCookie = Validator.isNotNull(
			getCookieValue(
				CookiesConstants.NAME_USER_CONSENT_CONFIGURED,
				httpServletRequest));

		if (hasConsentTypeFunctionalCookie || hasConsentTypePerformanceCookie ||
			hasConsentTypePersonalizationCookie ||
			hasUserConsentConfiguredCookie) {

			return deleteCookies(
				getDomain(httpServletRequest), httpServletRequest,
				httpServletResponse,
				CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL,
				CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
				CookiesConstants.NAME_USER_CONSENT_CONFIGURED);
		}

		return false;
	}

	private String _getContextPath(HttpServletRequest httpServletRequest) {
		if (httpServletRequest != null) {
			String contextPath = _portal.getPathContext(
				_portal.getOriginalServletRequest(httpServletRequest));

			if (Validator.isNotNull(contextPath)) {
				return contextPath;
			}
		}

		return StringPool.SLASH;
	}

	private Map<String, Cookie> _getCookiesMap(
		HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			return Collections.emptyMap();
		}

		Map<String, Cookie> cookiesMap =
			(Map<String, Cookie>)httpServletRequest.getAttribute(
				CookiesManagerImpl.class.getName());

		if (cookiesMap != null) {
			return cookiesMap;
		}

		Cookie[] cookies = httpServletRequest.getCookies();

		if (cookies == null) {
			cookiesMap = new HashMap<>();
		}
		else {
			cookiesMap = new HashMap<>(cookies.length * 4 / 3);

			for (Cookie cookie : cookies) {
				String cookieName = GetterUtil.getString(cookie.getName());

				cookieName = StringUtil.toUpperCase(cookieName);

				cookiesMap.put(cookieName, cookie);
			}
		}

		httpServletRequest.setAttribute(
			CookiesManagerImpl.class.getName(), cookiesMap);

		return cookiesMap;
	}

	private CookiesPreferenceHandlingConfiguration
		_getCookiesPreferenceHandlingConfiguration(
			HttpServletRequest httpServletRequest) {

		try {
			if (httpServletRequest != null) {
				long groupId = _portal.getScopeGroupId(httpServletRequest);

				if (groupId > 0) {
					return _configurationProvider.getGroupConfiguration(
						CookiesPreferenceHandlingConfiguration.class, groupId);
				}

				return _configurationProvider.getCompanyConfiguration(
					CookiesPreferenceHandlingConfiguration.class,
					_portal.getCompanyId(httpServletRequest));
			}

			return _configurationProvider.getSystemConfiguration(
				CookiesPreferenceHandlingConfiguration.class);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private String _getCookieValue(
		String cookieName, HttpServletRequest httpServletRequest,
		boolean toUpperCase) {

		Map<String, Cookie> cookiesMap = _getCookiesMap(httpServletRequest);

		if (toUpperCase) {
			cookieName = StringUtil.toUpperCase(cookieName);
		}

		Cookie cookie = cookiesMap.get(cookieName);

		if (cookie == null) {
			return null;
		}

		return cookie.getValue();
	}

	private String[] _getProperty(
		Map<String, Object> properties, String propertyName) {

		String[] propertyValues = GetterUtil.getStringValues(
			properties.get(propertyName));

		if ((propertyValues != null) && (propertyValues.length > 0)) {
			return propertyValues;
		}

		String propertyValue = GetterUtil.getString(
			properties.get(propertyName));

		if (Validator.isNotNull(propertyValue)) {
			return new String[] {propertyValue};
		}

		return new String[0];
	}

	private static final String _SESSION_COOKIE_DOMAIN = PropsUtil.get(
		PropsKeys.SESSION_COOKIE_DOMAIN);

	private static final boolean _SESSION_COOKIE_USE_FULL_HOSTNAME =
		GetterUtil.getBoolean(
			PropsUtil.get(
				PropsKeys.SESSION_COOKIE_USE_FULL_HOSTNAME,
				new Filter(ServerDetector.getServerId())));

	private static final boolean _SESSION_ENABLE_PERSISTENT_COOKIES =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.SESSION_ENABLE_PERSISTENT_COOKIES));

	private static final boolean _SESSION_TEST_COOKIE_SUPPORT =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.SESSION_TEST_COOKIE_SUPPORT));

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesManagerImpl.class);

	private static final Map<String, Integer> _internalCookies =
		new HashMap<>();
	private static final Map<String, Integer> _knownCookies = new HashMap<>();

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}