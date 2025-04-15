/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.secure;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.http.HttpAuthManagerUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Alexander Chow
 */
public abstract class BaseAuthFilter extends BasePortalFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		_basicAuthEnabled = GetterUtil.getBoolean(
			filterConfig.getInitParameter("basic_auth"));
		_digestAuthEnabled = GetterUtil.getBoolean(
			filterConfig.getInitParameter("digest_auth"));

		String propertyPrefix = filterConfig.getInitParameter(
			"portal_property_prefix");

		String[] hostsAllowed = null;

		if (Validator.isNull(propertyPrefix)) {
			hostsAllowed = StringUtil.split(
				filterConfig.getInitParameter("hosts.allowed"));
			_httpsRequired = GetterUtil.getBoolean(
				filterConfig.getInitParameter("https.required"));
		}
		else {
			hostsAllowed = PropsUtil.getArray(propertyPrefix + "hosts.allowed");
			_httpsRequired = GetterUtil.getBoolean(
				PropsUtil.get(propertyPrefix + "https.required"));
		}

		if (hostsAllowed.length == 0) {
			_hostsAllowed = Collections.emptySet();
		}
		else {
			_hostsAllowed = new HashSet<>(Arrays.asList(hostsAllowed));
		}

		_usePermissionChecker = GetterUtil.getBoolean(
			filterConfig.getInitParameter("use_permission_checker"));
	}

	protected HttpServletRequest basicAuth(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		User user1 = (User)httpSession.getAttribute(WebKeys.USER);

		if (user1 == null) {
			long userId = 0;

			try {
				userId = HttpAuthManagerUtil.getBasicUserId(httpServletRequest);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			if (userId > 0) {
				httpServletRequest = setCredentials(
					httpServletRequest, httpSession,
					UserLocalServiceUtil.getUser(userId),
					HttpServletRequest.BASIC_AUTH);
			}
			else {
				HttpAuthManagerUtil.generateChallenge(
					httpServletRequest, httpServletResponse,
					new HttpAuthorizationHeader(
						HttpAuthorizationHeader.SCHEME_BASIC));

				return null;
			}
		}
		else {
			User user2 = UserLocalServiceUtil.getUser(user1.getUserId());

			if (!user2.isActive()) {
				httpSession.invalidate();

				HttpAuthManagerUtil.generateChallenge(
					httpServletRequest, httpServletResponse,
					new HttpAuthorizationHeader(
						HttpAuthorizationHeader.SCHEME_BASIC));

				return null;
			}

			httpServletRequest = new ProtectedServletRequest(
				httpServletRequest, String.valueOf(user1.getUserId()),
				HttpServletRequest.BASIC_AUTH);

			PrincipalThreadLocal.setPassword(
				PortalUtil.getUserPassword(httpServletRequest));
		}

		return httpServletRequest;
	}

	protected HttpServletRequest digestAuth(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		User user1 = (User)httpSession.getAttribute(WebKeys.USER);

		if (user1 == null) {
			long userId = 0;

			try {
				userId = HttpAuthManagerUtil.getDigestUserId(
					httpServletRequest);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			if (userId > 0) {
				user1 = UserLocalServiceUtil.getUser(userId);

				httpServletRequest = setCredentials(
					httpServletRequest, httpSession, user1,
					HttpServletRequest.DIGEST_AUTH);

				httpSession.setAttribute(
					WebKeys.USER_DIGEST, user1.getDigest());
			}
			else {
				HttpAuthManagerUtil.generateChallenge(
					httpServletRequest, httpServletResponse,
					new HttpAuthorizationHeader(
						HttpAuthorizationHeader.SCHEME_DIGEST));

				return null;
			}
		}
		else {
			User user2 = UserLocalServiceUtil.getUser(user1.getUserId());

			if (_isDigestModified(httpSession) || !user2.isActive()) {
				httpSession.invalidate();

				HttpAuthManagerUtil.generateChallenge(
					httpServletRequest, httpServletResponse,
					new HttpAuthorizationHeader(
						HttpAuthorizationHeader.SCHEME_DIGEST));

				return null;
			}

			httpServletRequest = new ProtectedServletRequest(
				httpServletRequest, String.valueOf(user1.getUserId()),
				HttpServletRequest.DIGEST_AUTH);

			PrincipalThreadLocal.setPassword(
				PortalUtil.getUserPassword(httpServletRequest));
		}

		return httpServletRequest;
	}

	protected void initThreadLocals(User user) throws Exception {
		CompanyThreadLocal.setCompanyId(user.getCompanyId());

		PrincipalThreadLocal.setName(user.getUserId());

		if (!_usePermissionChecker) {
			return;
		}

		PermissionThreadLocal.getPermissionChecker(user, true);
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (AccessControlUtil.isAccessAllowed(
				httpServletRequest, _hostsAllowed)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Access allowed for " + httpServletRequest.getRemoteAddr());
			}
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Access denied for " + httpServletRequest.getRemoteAddr());
			}

			httpServletResponse.sendError(
				HttpServletResponse.SC_FORBIDDEN,
				"Access denied for " + httpServletRequest.getRemoteAddr());

			return;
		}

		if (_log.isDebugEnabled()) {
			if (_httpsRequired) {
				_log.debug("https is required");
			}
			else {
				_log.debug("https is not required");
			}
		}

		if (_httpsRequired && !PortalUtil.isSecure(httpServletRequest)) {
			if (_log.isDebugEnabled()) {
				String completeURL = HttpComponentsUtil.getCompleteURL(
					httpServletRequest);

				_log.debug("Securing " + completeURL);
			}

			StringBundler sb = new StringBundler(5);

			sb.append(PortalUtil.getPortalURL(httpServletRequest, true));
			sb.append(PortalUtil.getPathContext(httpServletRequest));
			sb.append(httpServletRequest.getRequestURI());

			if (Validator.isNotNull(httpServletRequest.getQueryString())) {
				sb.append(StringPool.QUESTION);
				sb.append(httpServletRequest.getQueryString());
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Redirect to " + sb.toString());
			}

			httpServletResponse.sendRedirect(sb.toString());
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Not securing " +
						HttpComponentsUtil.getCompleteURL(httpServletRequest));
			}

			User user = null;

			try {
				user = PortalUtil.initUser(httpServletRequest);
			}
			catch (NoSuchUserException noSuchUserException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchUserException);
				}

				httpServletResponse.sendRedirect(
					HttpComponentsUtil.getCompleteURL(httpServletRequest));

				return;
			}

			initThreadLocals(user);

			if (!user.isGuestUser()) {
				String authType = ParamUtil.getString(
					httpServletRequest, "authType");

				if (authType == null) {
					Company company = PortalUtil.getCompany(httpServletRequest);

					authType = company.getAuthType();
				}

				httpServletRequest = setCredentials(
					httpServletRequest, httpServletRequest.getSession(), user,
					authType);
			}
			else {
				if (_digestAuthEnabled) {
					httpServletRequest = digestAuth(
						httpServletRequest, httpServletResponse);
				}
				else if (_basicAuthEnabled) {
					httpServletRequest = basicAuth(
						httpServletRequest, httpServletResponse);
				}
			}

			if (httpServletRequest != null) {
				Class<?> clazz = getClass();

				processFilter(
					clazz.getName(), httpServletRequest, httpServletResponse,
					filterChain);
			}
		}
	}

	protected HttpServletRequest setCredentials(
			HttpServletRequest httpServletRequest, HttpSession httpSession,
			User user, String authType)
		throws Exception {

		httpServletRequest = new ProtectedServletRequest(
			httpServletRequest, String.valueOf(user.getUserId()), authType);

		PrincipalThreadLocal.setPassword(
			PortalUtil.getUserPassword(httpServletRequest));

		return httpServletRequest;
	}

	protected void setUsePermissionChecker(boolean usePermissionChecker) {
		_usePermissionChecker = usePermissionChecker;
	}

	private boolean _isDigestModified(HttpSession httpSession)
		throws Exception {

		User user = (User)httpSession.getAttribute(WebKeys.USER);

		if (user == null) {
			return false;
		}

		user = UserLocalServiceUtil.getUser(user.getUserId());

		return !Objects.equals(
			user.getDigest(), httpSession.getAttribute(WebKeys.USER_DIGEST));
	}

	private static final Log _log = LogFactoryUtil.getLog(BaseAuthFilter.class);

	private boolean _basicAuthEnabled;
	private boolean _digestAuthEnabled;
	private Set<String> _hostsAllowed;
	private boolean _httpsRequired;
	private boolean _usePermissionChecker;

}