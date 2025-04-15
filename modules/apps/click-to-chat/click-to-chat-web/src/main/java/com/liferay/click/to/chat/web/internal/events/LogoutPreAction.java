/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.click.to.chat.web.internal.events;

import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfiguration;
import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfigurationUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jonathan McCann
 */
@Component(property = "key=logout.events.pre", service = LifecycleAction.class)
public class LogoutPreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ClickToChatConfiguration clickToChatConfiguration =
			ClickToChatConfigurationUtil.getClickToChatConfiguration(
				themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId());

		if ((clickToChatConfiguration == null) ||
			!clickToChatConfiguration.enabled() ||
			!StringUtil.equals(
				clickToChatConfiguration.chatProviderId(), "intercom")) {

			return;
		}

		String domain = CookiesManagerUtil.getDomain(httpServletRequest);

		Cookie[] cookies = httpServletRequest.getCookies();

		for (Cookie cookie : cookies) {
			String name = cookie.getName();

			if (name.startsWith("intercom-")) {
				CookiesManagerUtil.deleteCookies(
					domain, httpServletRequest, httpServletResponse, name);
			}
		}
	}

}