/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.remember.me;

import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.AutoLoginException;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.RememberMeTokenLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = AutoLogin.class)
public class RememberMeAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doHandleException(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Exception exception)
		throws AutoLoginException {

		if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		removeCookies(httpServletRequest, httpServletResponse);

		throw new AutoLoginException(exception);
	}

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		RememberMeToken rememberMeToken = null;

		String rememberMeTokenId = CookiesManagerUtil.getCookieValue(
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID, httpServletRequest,
			false);
		String rememberMeTokenValue = CookiesManagerUtil.getCookieValue(
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE, httpServletRequest,
			false);

		if (Validator.isNotNull(rememberMeTokenId) &&
			Validator.isNotNull(rememberMeTokenValue)) {

			rememberMeToken = _rememberMeTokenLocalService.fetchRememberMeToken(
				GetterUtil.getLong(rememberMeTokenId), rememberMeTokenValue);
		}

		// LPS-11218

		if (rememberMeToken == null) {
			removeCookies(httpServletRequest, httpServletResponse);

			return null;
		}

		User user = _userLocalService.fetchUserById(
			rememberMeToken.getUserId());

		Company company = _portal.getCompany(httpServletRequest);

		User guestUser = _userLocalService.getGuestUser(company.getCompanyId());

		// LEP-5188

		boolean rememberMe = GetterUtil.getBoolean(
			CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_REMEMBER_ME, httpServletRequest, false));

		String proxyPath = _portal.getPathProxy();
		String contextPath = _portal.getPathContext();

		if (proxyPath.equals(contextPath)) {
			if (Validator.isNotNull(httpServletRequest.getContextPath())) {
				rememberMe = true;
			}
		}
		else {
			if (!contextPath.equals(httpServletRequest.getContextPath())) {
				rememberMe = false;
			}
		}

		if (!company.isAutoLogin() || (user == null) ||
			(guestUser.getUserId() == user.getUserId()) || !user.isActive() ||
			!rememberMe || rememberMeToken.isExpired()) {

			removeCookies(httpServletRequest, httpServletResponse);

			_rememberMeTokenLocalService.deleteRememberMeToken(
				rememberMeToken.getRememberMeTokenId());

			return null;
		}

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = String.valueOf(user.isPasswordEncrypted());

		return credentials;
	}

	protected void removeCookies(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		String domain = CookiesManagerUtil.getDomain(httpServletRequest);

		CookiesManagerUtil.deleteCookies(
			domain, httpServletRequest, httpServletResponse,
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID);
		CookiesManagerUtil.deleteCookies(
			domain, httpServletRequest, httpServletResponse,
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RememberMeAutoLogin.class);

	@Reference
	private Portal _portal;

	@Reference
	private RememberMeTokenLocalService _rememberMeTokenLocalService;

	@Reference
	private UserLocalService _userLocalService;

}