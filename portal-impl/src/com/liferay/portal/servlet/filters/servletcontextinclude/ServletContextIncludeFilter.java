/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.servletcontextinclude;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Raymond Augé
 */
public class ServletContextIncludeFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled() {
		if (super.isFilterEnabled() && PropsValues.THEME_JSP_OVERRIDE_ENABLED) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			Theme theme = getTheme(httpServletRequest);

			if (theme == null) {
				return false;
			}

			Boolean strict = (Boolean)httpServletRequest.getAttribute(
				WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_STRICT);

			if ((strict != null) && strict) {
				return false;
			}

			FilterConfig filterConfig = getFilterConfig();

			String uri = (String)httpServletRequest.getAttribute(
				WebKeys.INVOKER_FILTER_URI);

			if (theme.resourceExists(
					filterConfig.getServletContext(),
					ThemeUtil.getPortletId(httpServletRequest), uri)) {

				httpServletRequest.setAttribute(
					WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_PATH, uri);
				httpServletRequest.setAttribute(
					WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_THEME, theme);

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	protected Theme getTheme(HttpServletRequest httpServletRequest)
		throws Exception {

		String themeId = ParamUtil.getString(httpServletRequest, "themeId");

		if (Validator.isNotNull(themeId)) {
			return ThemeLocalServiceUtil.getTheme(
				PortalUtil.getCompanyId(httpServletRequest), themeId);
		}

		long plid = ParamUtil.getLong(httpServletRequest, "plid");

		if (plid <= 0) {
			plid = ParamUtil.getLong(httpServletRequest, "p_l_id");
		}

		if (plid > 0) {
			Layout layout = LayoutLocalServiceUtil.getLayout(plid);

			return layout.getTheme();
		}

		Theme theme = (Theme)httpServletRequest.getAttribute(WebKeys.THEME);

		if (theme != null) {
			return theme;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return themeDisplay.getTheme();
		}

		LayoutSet layoutSet = (LayoutSet)httpServletRequest.getAttribute(
			WebKeys.VIRTUAL_HOST_LAYOUT_SET);

		if (layoutSet != null) {
			return layoutSet.getTheme();
		}

		return null;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		Theme theme = (Theme)httpServletRequest.getAttribute(
			WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_THEME);

		httpServletRequest.setAttribute(WebKeys.THEME, theme);

		FilterConfig filterConfig = getFilterConfig();

		ServletContext servletContext = filterConfig.getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/WEB-INF/jsp/_servlet_context_include.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServletContextIncludeFilter.class);

}