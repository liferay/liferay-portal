/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auto.login;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Máté Thurzó
 */
public abstract class BaseAutoLogin implements AutoLogin {

	@Override
	public String[] login(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws AutoLoginException {

		try {
			return doLogin(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			return doHandleException(
				httpServletRequest, httpServletResponse, exception);
		}
	}

	protected void addRedirect(HttpServletRequest httpServletRequest) {
		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			httpServletRequest.setAttribute(
				AUTO_LOGIN_REDIRECT_AND_CONTINUE,
				PortalUtil.escapeRedirect(redirect));
		}
	}

	protected String[] doHandleException(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Exception exception)
		throws AutoLoginException {

		if (httpServletRequest.getAttribute(AUTO_LOGIN_REDIRECT) == null) {
			throw new AutoLoginException(exception);
		}

		_log.error(exception);

		return null;
	}

	protected abstract String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

	private static final Log _log = LogFactoryUtil.getLog(BaseAutoLogin.class);

}