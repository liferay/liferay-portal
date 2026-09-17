/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.events;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.token.events.LogoutProcessor;
import com.liferay.portal.security.sso.token.events.LogoutProcessorType;

import jakarta.servlet.http.Cookie;
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

		String domain = CookiesManagerUtil.getDomain(httpServletRequest);

		for (String parameter : parameters) {
			Cookie cookie = new Cookie(parameter, StringPool.BLANK);

			if (Validator.isNotNull(domain)) {
				cookie.setDomain(domain);
			}

			cookie.setMaxAge(0);

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL, cookie,
				httpServletRequest, httpServletResponse);
		}
	}

}