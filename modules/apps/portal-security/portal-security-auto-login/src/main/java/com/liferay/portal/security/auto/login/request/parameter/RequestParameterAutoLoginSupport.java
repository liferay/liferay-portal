/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.request.parameter;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = {"private.auto.login=true", "type=request.parameter"},
	service = AutoLogin.class
)
public class RequestParameterAutoLoginSupport extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String login = ParamUtil.getString(httpServletRequest, getLoginParam());

		if (Validator.isNull(login)) {
			return null;
		}

		String password = ParamUtil.getString(
			httpServletRequest, getPasswordParam());

		if (Validator.isNull(password)) {
			return null;
		}

		Company company = _portal.getCompany(httpServletRequest);

		String authType = company.getAuthType();

		long userId = 0;

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			userId = _userLocalService.getUserIdByEmailAddress(
				company.getCompanyId(), login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			userId = _userLocalService.getUserIdByScreenName(
				company.getCompanyId(), login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			userId = GetterUtil.getLong(login);
		}
		else {
			return null;
		}

		if (userId > 0) {
			User user = _userLocalService.getUserById(userId);

			String userPassword = user.getPassword();

			if (!user.isPasswordEncrypted()) {
				userPassword = PasswordEncryptorUtil.encrypt(userPassword);
			}

			String encPassword = PasswordEncryptorUtil.encrypt(
				password, userPassword);

			if (!userPassword.equals(password) &&
				!userPassword.equals(encPassword)) {

				return null;
			}
		}

		return new String[] {
			String.valueOf(userId), password, Boolean.FALSE.toString()
		};
	}

	protected String getLoginParam() {
		return _LOGIN_PARAM;
	}

	protected String getPasswordParam() {
		return _PASSWORD_PARAM;
	}

	private static final String _LOGIN_PARAM = "parameterAutoLoginLogin";

	private static final String _PASSWORD_PARAM = "parameterAutoLoginPassword";

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}