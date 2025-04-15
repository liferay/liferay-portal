/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.auto.login;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = AutoLogin.class)
public class OpenIdConnectAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!_openIdConnect.isEnabled(
				_portal.getCompanyId(httpServletRequest))) {

			return null;
		}

		HttpSession httpSession = httpServletRequest.getSession(false);

		if (httpSession == null) {
			return null;
		}

		Long userId = (Long)httpSession.getAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_AUTHENTICATING_USER_ID);

		if (userId == null) {
			return null;
		}

		httpSession.removeAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_AUTHENTICATING_USER_ID);

		User user = _userLocalService.getUserById(userId);

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}