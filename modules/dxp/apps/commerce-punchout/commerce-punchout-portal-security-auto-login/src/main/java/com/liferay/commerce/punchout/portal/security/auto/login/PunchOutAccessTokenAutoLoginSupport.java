/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.portal.security.auto.login;

import com.liferay.commerce.punchout.oauth2.provider.PunchOutAccessTokenProvider;
import com.liferay.commerce.punchout.oauth2.provider.model.PunchOutAccessToken;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(
	property = {"private.auto.login=true", "type=punchout.access.token"},
	service = AutoLogin.class
)
public class PunchOutAccessTokenAutoLoginSupport extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String punchOutAccessTokenFromParam = ParamUtil.getString(
			httpServletRequest, _PUNCH_OUT_ACCESS_TOKEN_PARAM);

		if (Validator.isNull(punchOutAccessTokenFromParam)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					_PUNCH_OUT_ACCESS_TOKEN_PARAM +
						"  parameter not found in request");
			}

			return null;
		}

		PunchOutAccessToken punchOutAccessToken =
			_punchOutAccessTokenProvider.getPunchOutAccessToken(
				punchOutAccessTokenFromParam);

		if (punchOutAccessToken == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Punch out access token not found");
			}

			return null;
		}

		String userEmailAddress = punchOutAccessToken.getUserEmailAddress();

		if (Validator.isBlank(userEmailAddress)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Blank punch out user email address in punch out access " +
						"token");
			}

			return null;
		}

		User punchOutUser = _userLocalService.getUserByEmailAddress(
			_portal.getCompanyId(httpServletRequest), userEmailAddress);

		if (punchOutUser == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Punch out user not found in punch out access token");
			}

			return null;
		}

		httpServletRequest.setAttribute(
			"punchOutAccessToken", punchOutAccessToken);
		httpServletRequest.setAttribute(
			"punchOutUserId", punchOutUser.getUserId());

		_punchOutAccessTokenProvider.removePunchOutAccessToken(
			punchOutAccessTokenFromParam);

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(punchOutUser.getUserId());
		credentials[1] = punchOutUser.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	private static final String _PUNCH_OUT_ACCESS_TOKEN_PARAM =
		"punchOutAccessToken";

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOutAccessTokenAutoLoginSupport.class);

	@Reference
	private Portal _portal;

	@Reference
	private PunchOutAccessTokenProvider _punchOutAccessTokenProvider;

	@Reference
	private UserLocalService _userLocalService;

}