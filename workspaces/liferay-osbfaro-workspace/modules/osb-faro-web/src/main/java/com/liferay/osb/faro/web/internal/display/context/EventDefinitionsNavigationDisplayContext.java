/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class EventDefinitionsNavigationDisplayContext {

	public EventDefinitionsNavigationDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public NavigationItemList getNavigationItems() throws Exception {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				boolean active = true;

				if (Objects.equals(_getTab(), "custom")) {
					active = false;
				}

				navigationItem.setActive(active);
				navigationItem.setHref(
					FaroNavigationUtil.fetchLayoutURL(
						"/event-definitions", _themeDisplay));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "default-events"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(_getTab(), "custom"));
				navigationItem.setHref(
					URLBuilder.create(
						FaroNavigationUtil.fetchLayoutURL(
							"/event-definitions", _themeDisplay)
					).addParameter(
						"tab", "custom"
					).build());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "custom-events"));
			}
		).build();
	}

	private String _getTab() {
		if (_tab != null) {
			return _tab;
		}

		_tab = ParamUtil.getString(_httpServletRequest, "tab");

		return _tab;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _tab;
	private final ThemeDisplay _themeDisplay;

}