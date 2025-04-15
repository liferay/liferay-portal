/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.servletauthorizing;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Raymond Augé
 */
public class ServletAuthorizingFilter extends BasePortalFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		// Company id

		PortalInstances.getCompanyId(httpServletRequest);

		// Authorize

		long userId = PortalUtil.getUserId(httpServletRequest);

		String remoteUser = httpServletRequest.getRemoteUser();

		if (!PropsValues.PORTAL_JAAS_ENABLE) {
			String jRemoteUser = (String)httpSession.getAttribute(
				"j_remoteuser");

			if (jRemoteUser != null) {
				remoteUser = jRemoteUser;

				httpSession.removeAttribute("j_remoteuser");
			}
		}

		if ((userId > 0) && (remoteUser == null)) {
			remoteUser = String.valueOf(userId);
		}

		if (remoteUser != null) {
			httpServletRequest = new ProtectedServletRequest(
				httpServletRequest, remoteUser);
		}

		if ((userId > 0) || (remoteUser != null)) {

			// Set the principal associated with this thread

			String name = String.valueOf(userId);

			if (remoteUser != null) {
				name = remoteUser;
			}

			PrincipalThreadLocal.setName(name);

			// User id

			userId = GetterUtil.getLong(name);

			try {

				// User

				User user = UserLocalServiceUtil.getUserById(userId);

				// Permission checker

				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(user));

				// User id

				httpSession.setAttribute(WebKeys.USER_ID, Long.valueOf(userId));

				// User locale

				httpSession.setAttribute(WebKeys.LOCALE, user.getLocale());
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		processFilter(
			ServletAuthorizingFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServletAuthorizingFilter.class);

}