/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.session;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserTracker;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RememberMeTokenLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class AuthenticatedSessionManagerUtil {

	public static long getAuthenticatedUserId(
			HttpServletRequest httpServletRequest, String login,
			String password, String authType)
		throws PortalException {

		User user = _getAuthenticatedUser(
			httpServletRequest, login, password, authType);

		return user.getUserId();
	}

	public static boolean isPasswordParameterInQueryString(
		HttpServletRequest httpServletRequest) {

		String queryString = HttpComponentsUtil.getQueryString(
			httpServletRequest);

		if (Validator.isNotNull(queryString) &&
			queryString.contains("password=")) {

			String passwordParameterName = "password=";

			String portletId = PortalUtil.getPortletId(httpServletRequest);

			if (portletId != null) {
				passwordParameterName =
					PortalUtil.getPortletNamespace(portletId) +
						passwordParameterName;
			}

			int index = queryString.indexOf(passwordParameterName);

			if ((index == 0) ||
				((index > 0) &&
				 (queryString.charAt(index - 1) == CharPool.AMPERSAND))) {

				if (_log.isWarnEnabled()) {
					String referer = httpServletRequest.getHeader(
						HttpHeaders.REFERER);

					_log.warn(
						StringBundler.concat(
							"Ignoring login attempt because the password ",
							"parameter was found for the request with the ",
							"referer header: ", referer));
				}

				return true;
			}
		}

		return false;
	}

	public static void login(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String login,
			String password, boolean rememberMe, String authType)
		throws Exception {

		httpServletRequest = PortalUtil.getOriginalServletRequest(
			httpServletRequest);

		if (isPasswordParameterInQueryString(httpServletRequest)) {
			return;
		}

		CookiesManagerUtil.validateSupportCookie(httpServletRequest);

		HttpSession httpSession = httpServletRequest.getSession();

		Company company = PortalUtil.getCompany(httpServletRequest);

		User user = _getAuthenticatedUser(
			httpServletRequest, login, password, authType);

		if (!PropsValues.AUTH_SIMULTANEOUS_LOGINS) {
			signOutSimultaneousLogins(user.getUserId());
		}

		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			httpSession = renewSession(httpServletRequest, httpSession);
		}

		// Set cookies

		String domain = CookiesManagerUtil.getDomain(httpServletRequest);

		if (Validator.isNull(domain)) {
			domain = null;
		}

		String userIdString = String.valueOf(user.getUserId());

		httpSession.setAttribute("j_username", userIdString);

		if (PropsValues.PORTAL_JAAS_PLAIN_PASSWORD) {
			httpSession.setAttribute("j_password", password);
		}
		else {
			httpSession.setAttribute("j_password", user.getPassword());
		}

		httpSession.setAttribute("j_remoteuser", userIdString);

		if (PropsValues.SESSION_STORE_PASSWORD) {
			httpSession.setAttribute(WebKeys.USER_PASSWORD, password);
		}

		Cookie companyIdCookie = new Cookie(
			CookiesConstants.NAME_COMPANY_ID,
			String.valueOf(company.getCompanyId()));

		if (domain != null) {
			companyIdCookie.setDomain(domain);
		}

		Cookie idCookie = new Cookie(
			CookiesConstants.NAME_ID,
			EncryptorUtil.encrypt(company.getKeyObj(), userIdString));

		if (domain != null) {
			idCookie.setDomain(domain);
		}

		int loginMaxAge = PropsValues.COMPANY_SECURITY_AUTO_LOGIN_MAX_AGE;

		if (rememberMe) {
			companyIdCookie.setMaxAge(loginMaxAge);
			idCookie.setMaxAge(loginMaxAge);
		}
		else {

			// This was explicitly changed from 0 to -1 so that the cookie lasts
			// as long as the browser. This allows an external servlet wrapped
			// in AutoLoginFilter to work throughout the client connection. The
			// cookies ARE removed on an actual logout, so there is no security
			// issue. See LEP-4678 and LEP-5177.

			companyIdCookie.setMaxAge(-1);
			idCookie.setMaxAge(-1);
		}

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, companyIdCookie,
			httpServletRequest, httpServletResponse);
		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, idCookie,
			httpServletRequest, httpServletResponse);

		if (rememberMe) {
			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
				_createCookie(
					CookiesConstants.NAME_LOGIN, login, domain, loginMaxAge),
				httpServletRequest, httpServletResponse);
			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
				_createCookie(
					CookiesConstants.NAME_REMEMBER_ME, Boolean.TRUE.toString(),
					domain, loginMaxAge),
				httpServletRequest, httpServletResponse);

			Cookie cookie = _createCookie(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE, StringPool.BLANK,
				domain, loginMaxAge);

			RememberMeToken rememberMeToken =
				RememberMeTokenLocalServiceUtil.addRememberMeToken(
					user.getCompanyId(), user.getUserId(),
					new Date(
						System.currentTimeMillis() +
							((long)loginMaxAge * 1000)),
					cookie::setValue);

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
				_createCookie(
					CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
					String.valueOf(rememberMeToken.getRememberMeTokenId()),
					domain, loginMaxAge),
				httpServletRequest, httpServletResponse);

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL, cookie,
				httpServletRequest, httpServletResponse);
		}
	}

	public static void logout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		EventsProcessorUtil.process(
			PropsKeys.LOGOUT_EVENTS_PRE, PropsValues.LOGOUT_EVENTS_PRE,
			httpServletRequest, httpServletResponse);

		String domain = CookiesManagerUtil.getDomain(httpServletRequest);

		if (Validator.isNull(domain)) {
			domain = null;
		}

		if (!GetterUtil.getBoolean(
				CookiesManagerUtil.getCookieValue(
					CookiesConstants.NAME_REMEMBER_ME, httpServletRequest,
					false))) {

			CookiesManagerUtil.deleteCookies(
				domain, httpServletRequest, httpServletResponse,
				CookiesConstants.NAME_LOGIN);
		}

		String rememberMeTokenId = CookiesManagerUtil.getCookieValue(
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID, httpServletRequest);

		if (Validator.isNotNull(rememberMeTokenId)) {
			RememberMeToken rememberMeToken =
				RememberMeTokenLocalServiceUtil.fetchRememberMeToken(
					GetterUtil.getLong(rememberMeTokenId));

			if (rememberMeToken != null) {
				RememberMeTokenLocalServiceUtil.deleteRememberMeToken(
					rememberMeToken);
			}
		}

		CookiesManagerUtil.deleteCookies(
			domain, httpServletRequest, httpServletResponse,
			CookiesConstants.NAME_COMPANY_ID,
			CookiesConstants.NAME_GUEST_LANGUAGE_ID, CookiesConstants.NAME_ID,
			CookiesConstants.NAME_PASSWORD, CookiesConstants.NAME_REMEMBER_ME,
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE);

		try {
			httpSession.invalidate();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		EventsProcessorUtil.process(
			PropsKeys.LOGOUT_EVENTS_POST, PropsValues.LOGOUT_EVENTS_POST,
			httpServletRequest, httpServletResponse);
	}

	public static HttpSession renewSession(
			HttpServletRequest httpServletRequest, HttpSession httpSession)
		throws Exception {

		// Invalidate the previous session to prevent session fixation attacks

		String[] protectedAttributeNames =
			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES;

		Map<String, Object> protectedAttributes = new HashMap<>();

		for (String protectedAttributeName : protectedAttributeNames) {
			Object protectedAttributeValue = httpSession.getAttribute(
				protectedAttributeName);

			if (protectedAttributeValue == null) {
				continue;
			}

			protectedAttributes.put(
				protectedAttributeName, protectedAttributeValue);
		}

		httpSession.invalidate();

		httpSession = httpServletRequest.getSession(true);

		for (String protectedAttributeName : protectedAttributeNames) {
			Object protectedAttributeValue = protectedAttributes.get(
				protectedAttributeName);

			if (protectedAttributeValue == null) {
				continue;
			}

			httpSession.setAttribute(
				protectedAttributeName, protectedAttributeValue);
		}

		return httpSession;
	}

	public static void signOutSimultaneousLogins(long userId) throws Exception {
		long companyId = CompanyLocalServiceUtil.getCompanyIdByUserId(userId);

		Map<String, UserTracker> sessionUsers = LiveUsers.getSessionUsers(
			companyId);

		List<UserTracker> userTrackers = new ArrayList<>(sessionUsers.values());

		for (UserTracker userTracker : userTrackers) {
			if (userId != userTracker.getUserId()) {
				continue;
			}

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			ClusterNode clusterNode = ClusterExecutorUtil.getLocalClusterNode();

			if (clusterNode != null) {
				jsonObject.put("clusterNodeId", clusterNode.getClusterNodeId());
			}

			jsonObject.put(
				"command", "signOut"
			).put(
				"companyId", companyId
			).put(
				"sessionId", userTracker.getSessionId()
			).put(
				"userId", userId
			);

			MessageBusUtil.sendMessage(
				DestinationNames.LIVE_USERS, jsonObject.toString());
		}
	}

	private static Cookie _createCookie(
		String name, String value, String domain, int maxAge) {

		Cookie cookie = new Cookie(name, value);

		if (domain != null) {
			cookie.setDomain(domain);
		}

		cookie.setMaxAge(maxAge);

		return cookie;
	}

	private static User _getAuthenticatedUser(
			HttpServletRequest httpServletRequest, String login,
			String password, String authType)
		throws PortalException {

		String requestURI = httpServletRequest.getRequestURI();

		String contextPath = PortalUtil.getPathContext();

		if (requestURI.startsWith(contextPath.concat("/api/liferay"))) {
			throw new AuthException();
		}

		Company company = PortalUtil.getCompany(httpServletRequest);

		Map<String, String[]> headerMap = new HashMap<>();

		Enumeration<String> enumeration1 = httpServletRequest.getHeaderNames();

		while (enumeration1.hasMoreElements()) {
			String name = enumeration1.nextElement();

			Enumeration<String> enumeration2 = httpServletRequest.getHeaders(
				name);

			List<String> headers = new ArrayList<>();

			while (enumeration2.hasMoreElements()) {
				String value = enumeration2.nextElement();

				headers.add(value);
			}

			headerMap.put(name, headers.toArray(new String[0]));
		}

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();
		Map<String, Object> resultsMap = new HashMap<>();

		if (Validator.isNull(authType)) {
			authType = company.getAuthType();
		}

		int authResult = Authenticator.FAILURE;

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			authResult = UserLocalServiceUtil.authenticateByEmailAddress(
				company.getCompanyId(), login, password, headerMap,
				parameterMap, resultsMap);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			authResult = UserLocalServiceUtil.authenticateByScreenName(
				company.getCompanyId(), login, password, headerMap,
				parameterMap, resultsMap);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			authResult = UserLocalServiceUtil.authenticateByUserId(
				company.getCompanyId(), GetterUtil.getLong(login), password,
				headerMap, parameterMap, resultsMap);
		}

		if (authResult != Authenticator.SUCCESS) {
			throw new AuthException();
		}

		return (User)resultsMap.get("user");
	}

	private AuthenticatedSessionManagerUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthenticatedSessionManagerUtil.class);

}