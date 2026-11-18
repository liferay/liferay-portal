/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.servlet.filter;

import com.liferay.multi.factor.authentication.web.internal.policy.MFAPolicy;
import com.liferay.multi.factor.authentication.web.internal.util.MFABrowserVerificationUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;

import jakarta.portlet.WindowState;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=MFA Login Filter", "url-pattern=/c/portal/login",
		"url-pattern=/c/portal/login/"
	},
	service = Filter.class
)
public class MFALoginFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long companyId = GetterUtil.getLong(
			httpServletRequest.getAttribute(WebKeys.COMPANY_ID));

		if (!_mfaPolicy.isMFAEnabled(companyId)) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);

			return;
		}

		httpServletRequest = _portal.getOriginalServletRequest(
			httpServletRequest);

		if (_portal.getUserId(httpServletRequest) > 0) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);

			return;
		}

		String login = ParamUtil.getString(httpServletRequest, "login");
		String password = ParamUtil.getString(httpServletRequest, "password");

		if (Validator.isBlank(login) || Validator.isBlank(password) ||
			AuthenticatedSessionManagerUtil.isPasswordParameterInQueryString(
				httpServletRequest)) {

			filterChain.doFilter(httpServletRequest, httpServletResponse);

			return;
		}

		AuthTokenUtil.checkCSRFToken(
			httpServletRequest, MFALoginFilter.class.getName());

		long userId = 0;

		try {
			userId = AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
				httpServletRequest, login, password,
				ParamUtil.getString(httpServletRequest, "authType"));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			filterChain.doFilter(httpServletRequest, httpServletResponse);

			return;
		}

		if (_mfaPolicy.isSatisfied(companyId, httpServletRequest, userId)) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);

			return;
		}

		httpServletResponse.sendRedirect(
			MFABrowserVerificationUtil.getVerificationRedirectURL(
				httpServletRequest, httpServletRequest.getParameterMap(),
				ParamUtil.getString(httpServletRequest, "redirect"), userId,
				WindowState.MAXIMIZED));
	}

	private static final Log _log = LogFactoryUtil.getLog(MFALoginFilter.class);

	@Reference
	private MFAPolicy _mfaPolicy;

	@Reference
	private Portal _portal;

}