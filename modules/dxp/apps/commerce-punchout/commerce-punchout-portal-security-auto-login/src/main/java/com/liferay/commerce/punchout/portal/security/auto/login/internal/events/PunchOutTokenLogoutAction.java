/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.portal.security.auto.login.internal.events;

import com.liferay.commerce.punchout.constants.PunchOutConstants;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jaclyn Ong
 */
@Component(property = "key=logout.events.pre", service = LifecycleAction.class)
public class PunchOutTokenLogoutAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			String pathInfo = httpServletRequest.getPathInfo();

			if (!pathInfo.contains("/portal/logout")) {
				return;
			}

			HttpSession httpSession = httpServletRequest.getSession();

			Object punchOutReturnUrlObject = httpSession.getAttribute(
				PunchOutConstants.PUNCH_OUT_REDIRECT_URL_ATTRIBUTE_NAME);

			if (punchOutReturnUrlObject == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						PunchOutConstants.
							PUNCH_OUT_REDIRECT_URL_ATTRIBUTE_NAME +
								" not found in session");
				}

				return;
			}

			String redirectURL = (String)punchOutReturnUrlObject;

			if (Validator.isBlank(redirectURL)) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						PunchOutConstants.
							PUNCH_OUT_REDIRECT_URL_ATTRIBUTE_NAME +
								" is blank");
				}

				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Redirecting to " + redirectURL);
			}

			httpServletResponse.sendRedirect(redirectURL);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOutTokenLogoutAction.class);

}