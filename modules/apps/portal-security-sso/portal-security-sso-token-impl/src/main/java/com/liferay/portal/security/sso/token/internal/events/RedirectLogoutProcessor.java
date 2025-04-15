/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.events;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.token.events.LogoutProcessor;
import com.liferay.portal.security.sso.token.events.LogoutProcessorType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	property = "logout.processor.type=" + LogoutProcessorType.REDIRECT,
	service = LogoutProcessor.class
)
public class RedirectLogoutProcessor implements LogoutProcessor {

	@Override
	public void logout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String... parameters)
		throws IOException {

		if (ArrayUtil.isEmpty(parameters)) {
			return;
		}

		String pathInfo = httpServletRequest.getPathInfo();

		if (pathInfo.contains("/portal/logout")) {
			HttpSession httpSession = httpServletRequest.getSession();

			httpSession.invalidate();

			String redirectURL = parameters[0];

			if (Validator.isNotNull(redirectURL)) {
				httpServletResponse.sendRedirect(redirectURL);
			}
		}
	}

}