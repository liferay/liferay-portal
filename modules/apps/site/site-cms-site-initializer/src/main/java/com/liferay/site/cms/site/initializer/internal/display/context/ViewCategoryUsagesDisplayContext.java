/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.CategorizationBreadcrumbUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Pei-Jung Lan
 */
public class ViewCategoryUsagesDisplayContext {

	public ViewCategoryUsagesDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getAPIURL() {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&",
			"filter=taxonomyCategoryIds in (",
			ParamUtil.getLong(_httpServletRequest, "categoryId"),
			")&nestedFields=embedded");
	}

	public Map<String, Object> getBreadcrumbProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems",
			CategorizationBreadcrumbUtil.getUsagesBreadcrumbsJSONArray(
				ParamUtil.getLong(_httpServletRequest, "categoryId"),
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY))
		).put(
			"hideSpace", true
		).build();
	}

	private final HttpServletRequest _httpServletRequest;

}