/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.lockout;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Norbert Kocsis
 */
public class LockoutFilter extends BasePortalFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (_isLockout(httpServletRequest)) {
			AuthenticatedSessionManagerUtil.logout(
				httpServletRequest, httpServletResponse);

			if (StringUtil.equals(
					httpServletRequest.getMethod(), HttpMethods.GET)) {

				httpServletResponse.sendRedirect(
					PortalUtil.getCurrentCompleteURL(httpServletRequest));
			}
			else {
				httpServletResponse.sendRedirect(
					PortalUtil.getPortalURL(httpServletRequest));
			}
		}
		else {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
	}

	private boolean _isLockout(HttpServletRequest httpServletRequest) {
		try {
			User user = PortalUtil.getUser(httpServletRequest);

			if ((user != null) && user.isLockout()) {
				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(LockoutFilter.class);

}