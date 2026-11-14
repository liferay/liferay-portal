/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.osb.faro.web.internal.display.context.ChannelSelectorDisplayContext;
import com.liferay.osb.faro.web.internal.util.FaroFragmentRendererUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseFragmentRenderer implements FragmentRenderer {

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			FaroFragmentRendererUtil.renderLinkTag(
				httpServletRequest, httpServletResponse,
				"/o/osb-faro-web/dist/main.css");
			FaroFragmentRendererUtil.renderScriptTag(
				httpServletRequest, httpServletResponse, "inline",
				"window.faroConstants = " +
					FaroFragmentRendererUtil.getFaroConstants() + ";",
				null, null);
			FaroFragmentRendererUtil.renderScriptTag(
				httpServletRequest, httpServletResponse, null,
				StringBundler.concat(
					"document.addEventListener('DOMContentLoaded', () => {",
					"for (const id of ['liferayAUICSS', 'liferayThemeCSS']) {",
					"const element = document.getElementById(id);",
					"if (element) {element.disabled = true;}}});"),
				null, null);

			PageContext pageContext = PageContextFactoryUtil.create(
				httpServletRequest, httpServletResponse);

			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write("<div>");

			ComponentTag componentTag = new ComponentTag();

			componentTag.setModule(getModule());
			componentTag.setPageContext(pageContext);
			componentTag.setProps(
				HashMapBuilder.<String, Object>put(
					"channelId",
					() -> {
						ChannelSelectorDisplayContext
							channelSelectorDisplayContext =
								new ChannelSelectorDisplayContext(
									httpServletRequest);

						return channelSelectorDisplayContext.getFaroChannelId();
					}
				).put(
					"groupId",
					() -> {
						ThemeDisplay themeDisplay =
							(ThemeDisplay)httpServletRequest.getAttribute(
								WebKeys.THEME_DISPLAY);

						return String.valueOf(themeDisplay.getScopeGroupId());
					}
				).build());
			componentTag.setServletContext(servletContext);

			componentTag.doStartTag();
			componentTag.doEndTag();

			jspWriter.write("</div>");
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	protected abstract String getModule();

	@Reference(target = "(osgi.web.symbolicname=com.liferay.osb.faro.web)")
	protected ServletContext servletContext;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFragmentRenderer.class);

}