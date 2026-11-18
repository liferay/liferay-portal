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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SegmentNavigationDisplayContext {

	public SegmentNavigationDisplayContext(
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
					Objects.equals(layout.getFriendlyURL(), "/segment"));
				navigationItem.setHref(_getLayoutURL("/segment"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "overview"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(
						layout.getFriendlyURL(), "/segment-membership"));
				navigationItem.setHref(_getLayoutURL("/segment-membership"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "membership"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					ArrayUtil.contains(
						new String[] {
							"/segment-interest", "/segment-interests"
						},
						layout.getFriendlyURL()));
				navigationItem.setHref(_getLayoutURL("/segment-interests"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "interests"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(
						layout.getFriendlyURL(), "/segment-distribution"));
				navigationItem.setHref(_getLayoutURL("/segment-distribution"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "distribution"));
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

	private String _getLayoutURL(String friendlyURL) throws Exception {
		return URLBuilder.create(
			FaroNavigationUtil.fetchLayoutURL(friendlyURL, _themeDisplay)
		).addParameter(
			"id", _getId()
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private String _id;
	private final ThemeDisplay _themeDisplay;

}