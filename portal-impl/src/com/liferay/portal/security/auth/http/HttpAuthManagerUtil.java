/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.http;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.servlet.filters.secure.NonceUtil;
import com.liferay.portal.util.PortalInstances;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class HttpAuthManagerUtil {

	public static void generateChallenge(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		HttpAuthorizationHeader httpAuthorizationHeader) {

		if (httpServletRequest == null) {
			throw new IllegalArgumentException("HTTP servlet request is null");
		}

		if (httpServletResponse == null) {
			throw new IllegalArgumentException("HTTP servlet response is null");
		}

		if ((httpAuthorizationHeader == null) ||
			Validator.isBlank(httpAuthorizationHeader.getScheme())) {

			throw new IllegalArgumentException(
				"HTTP authorization header scheme is null");
		}

		String realm = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_REALM);

		if (Validator.isBlank(realm)) {
			httpAuthorizationHeader.setAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_REALM,
				Portal.PORTAL_REALM);
		}

		String scheme = httpAuthorizationHeader.getScheme();

		if (StringUtil.equalsIgnoreCase(
				scheme, HttpAuthorizationHeader.SCHEME_BASIC)) {

			_generateBasicChallenge(
				httpServletResponse, httpAuthorizationHeader);
		}
		else if (StringUtil.equalsIgnoreCase(
					scheme, HttpAuthorizationHeader.SCHEME_DIGEST)) {

			_generateDigestChallenge(
				httpServletRequest, httpServletResponse,
				httpAuthorizationHeader);
		}
		else {
			throw new UnsupportedOperationException("Scheme " + scheme);
		}
	}

	public static long getBasicUserId(HttpServletRequest httpServletRequest)
		throws PortalException {

		HttpAuthorizationHeader httpAuthorizationHeader = parse(
			httpServletRequest);

		if ((httpAuthorizationHeader == null) ||
			!StringUtil.equalsIgnoreCase(
				httpAuthorizationHeader.getScheme(),
				HttpAuthorizationHeader.SCHEME_BASIC)) {

			return 0;
		}

		return getUserId(httpServletRequest, httpAuthorizationHeader);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public static long getDigestUserId(HttpServletRequest httpServletRequest)
		throws PortalException {

		HttpAuthorizationHeader httpAuthorizationHeader = parse(
			httpServletRequest);

		if ((httpAuthorizationHeader == null) ||
			!StringUtil.equalsIgnoreCase(
				httpAuthorizationHeader.getScheme(),
				HttpAuthorizationHeader.SCHEME_DIGEST)) {

			return 0;
		}

		return getUserId(httpServletRequest, httpAuthorizationHeader);
	}

	public static long getUserId(
			HttpServletRequest httpServletRequest,
			HttpAuthorizationHeader httpAuthorizationHeader)
		throws PortalException {

		if (httpServletRequest == null) {
			throw new IllegalArgumentException("HTTP servlet request is null");
		}

		if ((httpAuthorizationHeader == null) ||
			Validator.isBlank(httpAuthorizationHeader.getScheme())) {

			throw new IllegalArgumentException(
				"HTTP authorization header scheme is null");
		}

		String scheme = httpAuthorizationHeader.getScheme();

		if (StringUtil.equalsIgnoreCase(
				scheme, HttpAuthorizationHeader.SCHEME_BASIC)) {

			return _getBasicUserId(httpServletRequest, httpAuthorizationHeader);
		}
		else if (StringUtil.equalsIgnoreCase(
					scheme, HttpAuthorizationHeader.SCHEME_DIGEST)) {

			return _getDigestUserId(
				httpServletRequest, httpAuthorizationHeader);
		}

		throw new UnsupportedOperationException("Scheme " + scheme);
	}

	public static HttpAuthorizationHeader parse(
		HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			throw new IllegalArgumentException("HTTP servlet request is null");
		}

		String authorization = httpServletRequest.getHeader(
			HttpHeaders.AUTHORIZATION);

		if (Validator.isBlank(authorization)) {
			return null;
		}

		String[] authorizationParts = authorization.split("\\s");

		String scheme = authorizationParts[0];

		if (StringUtil.equalsIgnoreCase(
				scheme, HttpAuthorizationHeader.SCHEME_BASIC)) {

			return _parseBasic(authorizationParts);
		}
		else if (StringUtil.equalsIgnoreCase(
					scheme, HttpAuthorizationHeader.SCHEME_BEARER)) {

			return new HttpAuthorizationHeader(
				HttpAuthorizationHeader.SCHEME_BEARER);
		}
		else if (StringUtil.equalsIgnoreCase(
					scheme, HttpAuthorizationHeader.SCHEME_DIGEST)) {

			return _parseDigest(authorization);
		}

		throw new UnsupportedOperationException("Scheme " + scheme);
	}

	private static void _generateBasicChallenge(
		HttpServletResponse httpServletResponse,
		HttpAuthorizationHeader httpAuthorizationHeader) {

		httpServletResponse.setHeader(
			HttpHeaders.WWW_AUTHENTICATE, httpAuthorizationHeader.toString());
		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private static void _generateDigestChallenge(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		HttpAuthorizationHeader httpAuthorizationHeader) {

		// Must generate a new nonce for each 401 (RFC2617, 3.2.1)

		long companyId = PortalInstances.getCompanyId(httpServletRequest);

		String remoteAddress = httpServletRequest.getRemoteAddr();

		String nonce = NonceUtil.generate(companyId, remoteAddress);

		httpAuthorizationHeader.setAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_NONCE, nonce);

		httpServletResponse.setHeader(
			HttpHeaders.WWW_AUTHENTICATE, httpAuthorizationHeader.toString());

		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private static long _getBasicUserId(
			HttpServletRequest httpServletRequest,
			HttpAuthorizationHeader httpAuthorizationHeader)
		throws PortalException {

		String login = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_USERNAME);

		// Strip @uid and @sn for backwards compatibility

		if (login.endsWith("@uid")) {
			int pos = login.indexOf("@uid");

			login = login.substring(0, pos);
		}
		else if (login.endsWith("@sn")) {
			int pos = login.indexOf("@sn");

			login = login.substring(0, pos);
		}

		try {
			String password = httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_PASSWORD);

			return AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
				httpServletRequest, login, password, null);
		}
		catch (AuthException authException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(authException);
			}
		}

		return 0;
	}

	private static long _getDigestUserId(
			HttpServletRequest httpServletRequest,
			HttpAuthorizationHeader httpAuthorizationHeader)
		throws PortalException {

		long userId = 0;

		String username = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_USERNAME);
		String realm = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_REALM);
		String nonce = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_NONCE);
		String uri = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_URI);
		String response = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_RESPONSE);

		if (Validator.isNull(username) || Validator.isNull(realm) ||
			Validator.isNull(nonce) || Validator.isNull(uri) ||
			Validator.isNull(response)) {

			return userId;
		}

		String requestURI = httpServletRequest.getRequestURI();

		String queryString = httpServletRequest.getQueryString();

		if (Validator.isNotNull(queryString)) {
			requestURI = StringBundler.concat(
				requestURI, StringPool.QUESTION, queryString);
		}

		if (!realm.equals(Portal.PORTAL_REALM) || !uri.equals(requestURI) ||
			!NonceUtil.verify(nonce)) {

			return userId;
		}

		return UserLocalServiceUtil.authenticateForDigest(
			PortalInstances.getCompanyId(httpServletRequest), username, realm,
			nonce, httpServletRequest.getMethod(), uri, response);
	}

	private static HttpAuthorizationHeader _parseBasic(
		String[] authorizationParts) {

		String credentials = new String(Base64.decode(authorizationParts[1]));

		String login = null;
		String password = null;

		int index = credentials.indexOf(CharPool.COLON);

		if (index > -1) {
			login = credentials.substring(0, index);

			login = HttpComponentsUtil.decodeURL(login.trim());

			password = credentials.substring(index + 1);

			password = password.trim();
		}
		else {
			login = credentials.trim();
		}

		HttpAuthorizationHeader httpAuthorizationHeader =
			new HttpAuthorizationHeader(HttpAuthorizationHeader.SCHEME_BASIC);

		httpAuthorizationHeader.setAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_USERNAME, login);
		httpAuthorizationHeader.setAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_PASSWORD, password);

		return httpAuthorizationHeader;
	}

	private static HttpAuthorizationHeader _parseDigest(String authorization) {
		HttpAuthorizationHeader httpAuthorizationHeader =
			new HttpAuthorizationHeader(HttpAuthorizationHeader.SCHEME_DIGEST);

		authorization = authorization.substring(
			HttpAuthorizationHeader.SCHEME_DIGEST.length() + 1);

		authorization = StringUtil.replace(
			authorization, CharPool.COMMA, CharPool.NEW_LINE);

		UnicodeProperties authorizationUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				authorization
			).build();

		for (Map.Entry<String, String> authorizationProperty :
				authorizationUnicodeProperties.entrySet()) {

			String key = authorizationProperty.getKey();

			String value = StringUtil.unquote(
				authorizationUnicodeProperties.getProperty(key));

			httpAuthorizationHeader.setAuthParameter(key, value);
		}

		return httpAuthorizationHeader;
	}

	private HttpAuthManagerUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HttpAuthManagerUtil.class);

}