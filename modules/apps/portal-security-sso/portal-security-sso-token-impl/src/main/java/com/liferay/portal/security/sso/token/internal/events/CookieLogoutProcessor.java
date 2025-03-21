/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.events;

import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.security.sso.token.events.LogoutProcessor;
import com.liferay.portal.security.sso.token.events.LogoutProcessorType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	property = "logout.processor.type=" + LogoutProcessorType.COOKIE,
	service = LogoutProcessor.class
)
public class CookieLogoutProcessor implements LogoutProcessor {

	@Override
	public void logout(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String... parameters) {

		CookiesManagerUtil.deleteCookies(
			CookiesManagerUtil.getDomain(httpServletRequest),
			httpServletRequest, httpServletResponse, parameters);
	}

}