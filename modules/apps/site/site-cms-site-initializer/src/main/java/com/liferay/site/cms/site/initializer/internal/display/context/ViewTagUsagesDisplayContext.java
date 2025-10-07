/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Brooke Dalton
 */
public class ViewTagUsagesDisplayContext {

	public ViewTagUsagesDisplayContext(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		_httpServletRequest = httpServletRequest;
		_themeDisplay = themeDisplay;
	}

	public String getAPIURL() {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&filter=keywords in ('",
			ParamUtil.getString(_httpServletRequest, "keywordName"),
			"')&nestedFields=embedded");
	}

	public Map<String, Object> getBreadcrumbProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"active", false
				).put(
					"href",
					() -> PortalUtil.getLayoutFullURL(
						LayoutLocalServiceUtil.getLayoutByFriendlyURL(
							_themeDisplay.getScopeGroupId(), false,
							"/categorization/view-tags"),
						_themeDisplay)
				).put(
					"label", LanguageUtil.get(_themeDisplay.getLocale(), "tags")
				)
			).put(
				JSONUtil.put(
					"active", true
				).put(
					"label",
					LanguageUtil.format(
						_themeDisplay.getLocale(), "x-usages",
						ParamUtil.getString(_httpServletRequest, "keywordName"))
				)
			)
		).put(
			"hideSpace", true
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}