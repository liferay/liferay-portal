/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.autologin;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 * @author Peter Fellwock
 * @author Raymond Augé
 */
public class AutoLoginFilter extends BasePortalFilter {

	protected String getLoginRemoteUser(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, HttpSession httpSession,
			String[] credentials)
		throws Exception {

		if ((credentials == null) || (credentials.length != 3)) {
			return null;
		}

		String jUserName = credentials[0];

		if (Validator.isNull(jUserName)) {
			return null;
		}

		long userId = GetterUtil.getLong(jUserName);

		if (userId <= 0) {
			return null;
		}

		User user = UserLocalServiceUtil.fetchUserById(userId);

		if ((user == null) || user.isLockout()) {
			return null;
		}

		if (!PropsValues.AUTH_SIMULTANEOUS_LOGINS) {
			AuthenticatedSessionManagerUtil.signOutSimultaneousLogins(userId);
		}

		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			httpSession = AuthenticatedSessionManagerUtil.renewSession(
				httpServletRequest, httpSession);
		}

		httpSession.setAttribute("j_username", jUserName);

		String jPassword = credentials[1];

		if (Validator.isNotNull(jPassword)) {

			// Not having access to the unencrypted password will not allow you
			// to connect to external resources that require it (mail server)

			if (GetterUtil.getBoolean(credentials[2])) {
				httpSession.setAttribute("j_password", jPassword);
			}
			else {
				httpSession.setAttribute(
					"j_password",
					PasswordEncryptorUtil.encrypt(
						jPassword, user.getPassword()));

				if (PropsValues.SESSION_STORE_PASSWORD) {
					httpSession.setAttribute(WebKeys.USER_PASSWORD, jPassword);
				}
			}
		}

		httpSession.setAttribute("j_remoteuser", jUserName);

		if (PropsValues.PORTAL_JAAS_ENABLE) {
			String mainPath = PortalUtil.getPathMain();

			String redirect = mainPath.concat("/portal/protected");

			if (PropsValues.AUTH_FORWARD_BY_LAST_PATH) {
				redirect = redirect.concat("?redirect=");

				String autoLoginRedirect =
					(String)httpServletRequest.getAttribute(
						AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE);

				if (Validator.isNull(autoLoginRedirect)) {
					autoLoginRedirect = PortalUtil.getCurrentCompleteURL(
						httpServletRequest);
				}

				redirect = redirect.concat(
					URLCodec.encodeURL(autoLoginRedirect));
			}

			httpServletResponse.sendRedirect(redirect);
		}

		return jUserName;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String host = PortalUtil.getHost(httpServletRequest);

		if (PortalInstances.isAutoLoginIgnoreHost(host)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Ignore host " + host);
			}

			processFilter(
				AutoLoginFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		String contextPath = PortalUtil.getPathContext();

		String path = StringUtil.toLowerCase(
			httpServletRequest.getRequestURI());

		if (!contextPath.equals(StringPool.SLASH) &&
			path.startsWith(contextPath)) {

			path = path.substring(contextPath.length());
		}

		if (PortalInstances.isAutoLoginIgnorePath(path)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Ignore path " + path);
			}

			processFilter(
				AutoLoginFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		String remoteUser = httpServletRequest.getRemoteUser();

		HttpSession httpSession = httpServletRequest.getSession();

		String jUserName = (String)httpSession.getAttribute("j_username");

		if (!PropsValues.AUTH_LOGIN_DISABLED && (remoteUser == null) &&
			(jUserName == null)) {

			for (AutoLogin autoLogin : _autoLogins) {
				try {
					String[] credentials = autoLogin.login(
						httpServletRequest, httpServletResponse);

					String redirect = (String)httpServletRequest.getAttribute(
						AutoLogin.AUTO_LOGIN_REDIRECT);

					if (Validator.isNotNull(redirect)) {
						httpServletResponse.sendRedirect(redirect);

						return;
					}

					String loginRemoteUser = getLoginRemoteUser(
						httpServletRequest, httpServletResponse, httpSession,
						credentials);

					if (loginRemoteUser != null) {
						httpServletRequest = new ProtectedServletRequest(
							httpServletRequest, loginRemoteUser);

						if (PropsValues.PORTAL_JAAS_ENABLE) {
							return;
						}

						if (!PropsValues.AUTH_FORWARD_BY_LAST_PATH) {
							redirect = Portal.PATH_MAIN;
						}
						else {
							redirect = (String)httpServletRequest.getAttribute(
								AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE);
						}

						if (Validator.isNotNull(redirect)) {
							httpServletResponse.sendRedirect(redirect);

							return;
						}
					}
				}
				catch (Exception exception) {
					StringBundler sb = new StringBundler(6);

					sb.append("Current URL ");

					String currentURL = PortalUtil.getCurrentURL(
						httpServletRequest);

					sb.append(currentURL);

					sb.append(" generates exception: ");
					sb.append(exception.getMessage());

					if (_log.isInfoEnabled()) {
						sb.append(" stack: ");
						sb.append(StackTraceUtil.getStackTrace(exception));
					}

					if (currentURL.endsWith(_PATH_CHAT_LATEST)) {
						if (_log.isWarnEnabled()) {
							_log.warn(sb.toString());
						}
					}
					else {
						_log.error(sb.toString());
					}
				}
			}
		}

		processFilter(
			AutoLoginFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

	private static final String _PATH_CHAT_LATEST = "/-/chat/latest";

	private static final Log _log = LogFactoryUtil.getLog(
		AutoLoginFilter.class);

	private static final ServiceTrackerList<AutoLogin> _autoLogins =
		ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), AutoLogin.class,
			"(!(private.auto.login=*))");

}