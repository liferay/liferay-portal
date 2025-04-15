/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Mika Koivisto
 */
public class JspUtil {

	public static final String PATH_PORTAL_SAML_ERROR =
		"/portal/saml/error.jsp";

	public static final String PATH_PORTAL_SAML_SLO = "/portal/saml/slo.jsp";

	public static final String PATH_PORTAL_SAML_SLO_SP_STATUS =
		"/portal/saml/slo_sp_status.jsp";

	public static void dispatch(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path, String title)
		throws Exception {

		dispatch(httpServletRequest, httpServletResponse, path, title, false);
	}

	public static void dispatch(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path, String title,
			boolean popUp)
		throws Exception {

		httpServletRequest.setAttribute(
			TilesUtil.DEFINITION,
			new Definition(
				StringPool.BLANK,
				HashMapBuilder.put(
					"content", path
				).put(
					"pop_up", String.valueOf(popUp)
				).put(
					"title", title
				).build()));

		RequestDispatcher requestDispatcher =
			httpServletRequest.getRequestDispatcher(
				_PATH_HTML_COMMON_THEMES_PORTAL);

		if (popUp) {
			requestDispatcher.include(httpServletRequest, httpServletResponse);

			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		boolean stateMaximized = themeDisplay.isStateMaximized();

		themeDisplay.setStateMaximized(true);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		finally {
			themeDisplay.setStateMaximized(stateMaximized);
		}
	}

	private static final String _PATH_HTML_COMMON_THEMES_PORTAL =
		"/html/common/themes/portal.jsp";

}