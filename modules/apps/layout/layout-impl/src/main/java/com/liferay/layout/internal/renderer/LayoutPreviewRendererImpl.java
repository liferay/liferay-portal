/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.renderer;

import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = LayoutPreviewRenderer.class)
public class LayoutPreviewRendererImpl implements LayoutPreviewRenderer {

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		Theme theme = layout.getTheme();

		layout.includeLayoutContent(httpServletRequest, httpServletResponse);

		Document document = Jsoup.parse(
			ThemeUtil.include(
				ServletContextPool.get(_portal.getServletContextName()),
				httpServletRequest, httpServletResponse, "portal_normal.ftl",
				theme, false));

		Element element = document.getElementById("content");

		if (element == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Replacing all body content because theme " +
						theme.getThemeId() +
							" lacks a tag with ID \"content\"");
			}

			element = document.body();
		}

		StringBundler sb = (StringBundler)httpServletRequest.getAttribute(
			WebKeys.LAYOUT_CONTENT);

		element.html(sb.toString());

		ServletResponseUtil.write(httpServletResponse, document.html());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPreviewRendererImpl.class);

	@Reference
	private Portal _portal;

}