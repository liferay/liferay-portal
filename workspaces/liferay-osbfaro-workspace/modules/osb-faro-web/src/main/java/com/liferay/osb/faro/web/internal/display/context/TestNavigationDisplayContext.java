/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class TestNavigationDisplayContext {

	public TestNavigationDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public NavigationItemList getNavigationItems() throws Exception {
		Layout layout = _themeDisplay.getLayout();

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/test"));
				navigationItem.setHref(
					URLBuilder.create(
						FaroNavigationUtil.fetchLayoutURL(
							"/test", _themeDisplay)
					).addParameter(
						"id", _getId()
					).build());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "report"));
			}
		).build();
	}

	private String _getId() {
		if (_id != null) {
			return _id;
		}

		_id = ParamUtil.getString(_httpServletRequest, "id");

		return _id;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _id;
	private final ThemeDisplay _themeDisplay;

}