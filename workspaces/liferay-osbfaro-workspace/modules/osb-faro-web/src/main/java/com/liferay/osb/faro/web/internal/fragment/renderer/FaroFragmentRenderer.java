/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.osb.faro.web.internal.util.FaroFragmentRendererUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "faro";
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (!themeDisplay.isSignedIn()) {
				FaroFragmentRendererUtil.renderScriptTag(
					httpServletRequest, httpServletResponse, null,
					StringBundler.concat(
						"window.location.href = '",
						HtmlUtil.escapeJS(
							StringBundler.concat(
								themeDisplay.getURLSignIn(), "?redirect=",
								URLCodec.encodeURL(
									PortalUtil.getCurrentURL(
										httpServletRequest)))),
						"';"),
					null, null);

				return;
			}

			FaroFragmentRendererUtil.renderLinkTag(
				httpServletRequest, httpServletResponse,
				"/o/osb-faro-web/dist/main.css");

			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<div id=\"faroApp\"></div>");

			FaroFragmentRendererUtil.renderScriptTag(
				httpServletRequest, httpServletResponse, "inline",
				"window.faroConstants = " +
					FaroFragmentRendererUtil.getFaroConstants() + ";",
				null, null);
			FaroFragmentRendererUtil.renderScriptTag(
				httpServletRequest, httpServletResponse, null, null,
				FaroFragmentRendererUtil.getStaticResourceURL(
					httpServletRequest, "/o/osb-faro-web/dist/main.js"),
				"module");
			FaroFragmentRendererUtil.renderScriptTag(
				httpServletRequest, httpServletResponse, null,
				StringBundler.concat(
					"document.addEventListener('DOMContentLoaded', () => {",
					"for (const id of ['liferayAUICSS', 'liferayThemeCSS']) {",
					"const element = document.getElementById(id);",
					"if (element) {element.disabled = true;}}});"),
				null, null);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroFragmentRenderer.class);

}