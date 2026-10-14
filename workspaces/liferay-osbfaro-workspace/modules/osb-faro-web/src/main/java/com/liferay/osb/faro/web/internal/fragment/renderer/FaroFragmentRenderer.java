/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.engine.client.constants.LCPProjectConstants;
import com.liferay.osb.faro.provisioning.client.constants.FaroSubscriptionConstants;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.constants.FaroPaginationConstants;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.LinkTag;
import com.liferay.taglib.aui.ScriptTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
				_renderScriptTag(
					httpServletRequest, httpServletResponse, null,
					StringBundler.concat(
						"window.location.href = '",
						HtmlUtil.escapeJS(
							StringBundler.concat(
								themeDisplay.getURLSignIn(), "?redirect=",
								URLCodec.encodeURL(
									_portal.getCurrentURL(
										httpServletRequest)))),
						"';"),
					null, null);

				return;
			}

			_renderLinkTag(
				httpServletRequest, httpServletResponse,
				"/o/osb-faro-web/css/fonts.css");
			_renderLinkTag(
				httpServletRequest, httpServletResponse,
				"/o/osb-faro-web/dist/main.css");

			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<div id=\"faroApp\"></div>");

			_renderScriptTag(
				httpServletRequest, httpServletResponse, "inline",
				"window.faroConstants = " + _getFaroConstants() + ";", null,
				null);
			_renderScriptTag(
				httpServletRequest, httpServletResponse, null, null,
				_getStaticResourceURL(
					httpServletRequest, "/o/osb-faro-web/dist/main.js"),
				"module");
			_renderScriptTag(
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

	private String _getFaroConstants() throws Exception {
		return JSONUtil.writeValueAsString(
			HashMapBuilder.<String, Object>put(
				"channelPermissionTypes",
				FaroChannelConstants.getPermissionTypes()
			).put(
				"faroURL", FaroPropsValues.FARO_URL
			).put(
				"pagination", FaroPaginationConstants.getConstants()
			).put(
				"projectLocations", LCPProjectConstants.getLocations()
			).put(
				"subscriptionPlans",
				FaroSubscriptionConstants.getFaroSubscriptionPlans()
			).build());
	}

	private String _getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri) {

		return HtmlUtil.escapeAttribute(
			_portal.getStaticResourceURL(httpServletRequest, uri));
	}

	private void _renderLinkTag(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String uri)
		throws JspException {

		LinkTag linkTag = new LinkTag();

		linkTag.setHref(_getStaticResourceURL(httpServletRequest, uri));
		linkTag.setRel("stylesheet");
		linkTag.setType("text/css");

		linkTag.doTag(httpServletRequest, httpServletResponse);
	}

	private void _renderScriptTag(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String position,
			String script, String src, String type)
		throws JspException {

		ScriptTag scriptTag = new ScriptTag();

		scriptTag.setPosition(position);
		scriptTag.setSrc(src);
		scriptTag.setType(type);

		scriptTag.doBodyTag(
			httpServletRequest, httpServletResponse,
			pageContext -> {
				if (script == null) {
					return;
				}

				try {
					Writer writer = pageContext.getOut();

					writer.write(script);
				}
				catch (IOException ioException) {
					ReflectionUtil.throwException(ioException);
				}
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroFragmentRenderer.class);

	@Reference
	private Portal _portal;

}