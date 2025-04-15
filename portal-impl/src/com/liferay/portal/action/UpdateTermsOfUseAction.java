/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.constants.ActionConstants;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class UpdateTermsOfUseAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			AuthTokenUtil.checkCSRFToken(
				httpServletRequest, UpdateTermsOfUseAction.class.getName());

			UserServiceUtil.updateAgreedToTermsOfUse(
				PortalUtil.getUserId(httpServletRequest), true);

			return actionMapping.getActionForward(
				ActionConstants.COMMON_REFERER_JSP);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				_log.error("The CSRF token is invalid", exception);

				SessionErrors.add(httpServletRequest, exception.getClass());

				return actionMapping.getActionForward("portal.error");
			}

			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateTermsOfUseAction.class);

}