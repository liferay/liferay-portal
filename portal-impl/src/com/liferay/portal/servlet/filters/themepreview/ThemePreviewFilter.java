/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.themepreview;

import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.servlet.filters.strip.StripFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ganesh Ram
 */
public class ThemePreviewFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (isThemePreview(httpServletRequest)) {
			return true;
		}

		return false;
	}

	protected String getContent(
		HttpServletRequest httpServletRequest, String content) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Pattern cssPattern = Pattern.compile(themeDisplay.getPathThemeCss());

		Matcher cssMatcher = cssPattern.matcher(content);

		content = cssMatcher.replaceAll("css");

		Pattern imagePattern = Pattern.compile(
			themeDisplay.getPathThemeImages());

		Matcher imageMatcher = imagePattern.matcher(content);

		return imageMatcher.replaceAll("images");
	}

	protected boolean isThemePreview(HttpServletRequest httpServletRequest) {
		if (ParamUtil.getBoolean(httpServletRequest, _THEME_PREVIEW)) {
			return true;
		}

		return false;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletRequest.setAttribute(StripFilter.SKIP_FILTER, Boolean.TRUE);

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		processFilter(
			ThemePreviewFilter.class.getName(), httpServletRequest,
			bufferCacheServletResponse, filterChain);

		String content = bufferCacheServletResponse.getString();

		ServletResponseUtil.write(
			httpServletResponse, getContent(httpServletRequest, content));
	}

	private static final String _THEME_PREVIEW = "themePreview";

}