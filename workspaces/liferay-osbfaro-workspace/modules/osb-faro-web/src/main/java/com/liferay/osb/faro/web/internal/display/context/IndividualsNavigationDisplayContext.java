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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class IndividualsNavigationDisplayContext {

	public IndividualsNavigationDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public NavigationItemList getNavigationItems() throws Exception {
		Layout layout = _themeDisplay.getLayout();

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(layout.getFriendlyURL(), "/individuals"));
				navigationItem.setHref(
					FaroNavigationUtil.fetchLayoutURL(
						"/individuals", _themeDisplay));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "overview"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(
						layout.getFriendlyURL(), "/known-individuals"));
				navigationItem.setHref(
					FaroNavigationUtil.fetchLayoutURL(
						"/known-individuals", _themeDisplay));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "known-individuals"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					ArrayUtil.contains(
						new String[] {
							"/individuals-interest", "/individuals-interests"
						},
						layout.getFriendlyURL()));
				navigationItem.setHref(
					FaroNavigationUtil.fetchLayoutURL(
						"/individuals-interests", _themeDisplay));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "interests"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(
						layout.getFriendlyURL(), "/individuals-distribution"));
				navigationItem.setHref(
					FaroNavigationUtil.fetchLayoutURL(
						"/individuals-distribution", _themeDisplay));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "distribution"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}