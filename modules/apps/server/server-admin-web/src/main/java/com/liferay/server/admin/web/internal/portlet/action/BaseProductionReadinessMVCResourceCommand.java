/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
public abstract class BaseProductionReadinessMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (isCSRFProtected()) {
			if (!StringUtil.equals(
					resourceRequest.getMethod(), HttpMethods.POST)) {

				resourceResponse.setProperty(
					ResourceResponse.HTTP_STATUS_CODE,
					String.valueOf(HttpServletResponse.SC_METHOD_NOT_ALLOWED));

				return;
			}

			try {
				authToken.checkCSRFToken(
					portal.getOriginalServletRequest(
						portal.getHttpServletRequest(resourceRequest)),
					BaseProductionReadinessMVCResourceCommand.class.getName());
			}
			catch (PrincipalException principalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(principalException);
				}

				resourceResponse.setProperty(
					ResourceResponse.HTTP_STATUS_CODE,
					String.valueOf(HttpServletResponse.SC_FORBIDDEN));

				return;
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			throw new PrincipalException.MustBeOmniadmin(
				themeDisplay.getUserId());
		}

		serveProductionReadinessResource(resourceRequest, resourceResponse);
	}

	/**
	 * Returns <code>true</code> if the command changes state and therefore
	 * requires a valid CSRF token and the POST method. Read only commands must
	 * override this method to return <code>false</code>.
	 */
	protected boolean isCSRFProtected() {
		return true;
	}

	protected abstract void serveProductionReadinessResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception;

	@Reference
	protected AuthToken authToken;

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseProductionReadinessMVCResourceCommand.class);

}