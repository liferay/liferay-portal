/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.facebook.connect.internal.auto.login;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectWebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Participates in every unauthenticated HTTP request to Liferay Portal.
 *
 * <p>
 * This class looks for one of two HTTP session attributes:
 * <code>FACEBOOK_USER_ID</code> or <code>FACEBOOK_USER_EMAIL_ADDRESS</code>. If
 * either is found and can be matched with a Liferay Portal user on the
 * corresponding field, then the user is logged in without any further
 * challenge.
 * <p>
 *
 * @author Wilson Man
 */
@Component(service = AutoLogin.class)
public class FacebookConnectAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (!_facebookConnect.isEnabled(companyId)) {
			return null;
		}

		User user = _getUser(httpServletRequest, companyId);

		if (user == null) {
			return null;
		}

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.FALSE.toString();

		return credentials;
	}

	private User _getUser(HttpServletRequest httpServletRequest, long companyId)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		String emailAddress = (String)httpSession.getAttribute(
			WebKeys.FACEBOOK_USER_EMAIL_ADDRESS);

		if (Validator.isNotNull(emailAddress)) {
			httpSession.removeAttribute(WebKeys.FACEBOOK_USER_EMAIL_ADDRESS);

			return _userLocalService.getUserByEmailAddress(
				companyId, emailAddress);
		}

		long facebookId = GetterUtil.getLong(
			(String)httpSession.getAttribute(
				FacebookConnectWebKeys.FACEBOOK_USER_ID));

		if (facebookId > 0) {
			User user = _userLocalService.fetchUserByFacebookId(
				companyId, facebookId);

			if (user == null) {
				throw new NoSuchUserException(
					StringBundler.concat(
						"No user exists with company ID ", companyId,
						" and facebook ID ", facebookId));
			}
		}

		return null;
	}

	@Reference
	private FacebookConnect _facebookConnect;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}