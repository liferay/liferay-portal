/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.events;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Luca Pellizzon
 */
@Component(property = "key=logout.events.pre", service = LifecycleAction.class)
public class LogoutPreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			String domain = CookiesManagerUtil.getDomain(httpServletRequest);

			Cookie[] cookies = httpServletRequest.getCookies();

			for (Cookie cookie : cookies) {
				String name = cookie.getName();

				if (name.startsWith(
						CommerceOrder.class.getName() + StringPool.POUND)) {

					CookiesManagerUtil.deleteCookies(
						domain, httpServletRequest, httpServletResponse, name);

					break;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LogoutPreAction.class);

}