/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.action.UpdatePasswordActionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.AutoLoginException;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.DefaultAdminUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Alvaro Saugar
 */
public class SetupAdminAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doHandleException(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Exception exception)
		throws AutoLoginException {

		if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		throw new AutoLoginException(exception);
	}

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (Validator.isNotNull(PropsValues.DEFAULT_ADMIN_PASSWORD)) {
			return null;
		}

		Company company = PortalUtil.getCompany(httpServletRequest);

		User user = DefaultAdminUtil.fetchDefaultAdmin(company.getCompanyId());

		if (user == null) {
			return null;
		}

		String reminderQueryAnswer = user.getReminderQueryAnswer();

		if (user.isPasswordReset() &&
			reminderQueryAnswer.equals(WorkflowConstants.LABEL_PENDING) &&
			Validator.isNull(user.getReminderQueryQuestion()) &&
			Validator.isNull(user.getLastFailedLoginDate()) &&
			Validator.isNull(user.getLockoutDate())) {

			httpServletRequest.setAttribute(
				AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE,
				UpdatePasswordActionUtil.generateUpdatePasswordURL(
					httpServletRequest, user));

			String[] credentials = new String[3];

			credentials[0] = String.valueOf(user.getUserId());
			credentials[1] = user.getPassword();
			credentials[2] = Boolean.TRUE.toString();

			return credentials;
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SetupAdminAutoLogin.class);

}