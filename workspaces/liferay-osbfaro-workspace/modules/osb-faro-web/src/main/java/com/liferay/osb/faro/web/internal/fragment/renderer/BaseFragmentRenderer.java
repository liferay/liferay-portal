/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.osb.faro.web.internal.display.context.ChannelSelectorDisplayContext;
import com.liferay.osb.faro.web.internal.util.FaroFragmentRendererUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.util.HashMap;
import java.util.Map;

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

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

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
					"friendlyURL",
					() -> {
						Layout layout = themeDisplay.getLayout();

						return layout.getFriendlyURL();
					}
				).put(
					"groupId", String.valueOf(themeDisplay.getScopeGroupId())
				).put(
					"navigationItems",
					() -> getNavigationItems(httpServletRequest)
				).put(
					"pageURLs",
					() -> {
						Map<String, String> pageURLs = new HashMap<>();

						for (Layout layout :
								layoutLocalService.getLayouts(
									themeDisplay.getScopeGroupId(), false)) {

							pageURLs.put(
								layout.getFriendlyURL(),
								portal.getLayoutRelativeURL(
									layout, themeDisplay));
						}

						return pageURLs;
					}
				).put(
					"params", getParams(httpServletRequest)
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

	protected NavigationItemList getNavigationItems(
			HttpServletRequest httpServletRequest)
		throws Exception {

		return null;
	}

	protected Map<String, String> getParams(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.put(
			"accountId", ParamUtil.getString(httpServletRequest, "accountId")
		).put(
			"accountName",
			ParamUtil.getString(httpServletRequest, "accountName")
		).put(
			"campaignId", ParamUtil.getString(httpServletRequest, "campaignId")
		).put(
			"campaignName",
			ParamUtil.getString(httpServletRequest, "campaignName")
		).put(
			"delta", ParamUtil.getString(httpServletRequest, "delta")
		).put(
			"downloadReport",
			ParamUtil.getString(httpServletRequest, "downloadReport")
		).put(
			"field", ParamUtil.getString(httpServletRequest, "field")
		).put(
			"page", ParamUtil.getString(httpServletRequest, "page")
		).put(
			"query", ParamUtil.getString(httpServletRequest, "query")
		).put(
			"rangeEnd", ParamUtil.getString(httpServletRequest, "rangeEnd")
		).put(
			"rangeKey", ParamUtil.getString(httpServletRequest, "rangeKey")
		).put(
			"rangeStart", ParamUtil.getString(httpServletRequest, "rangeStart")
		).put(
			"segmentId", ParamUtil.getString(httpServletRequest, "segmentId")
		).put(
			"segmentName",
			ParamUtil.getString(httpServletRequest, "segmentName")
		).put(
			"sortOrder", ParamUtil.getString(httpServletRequest, "sortOrder")
		).build();
	}

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected Portal portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.osb.faro.web)")
	protected ServletContext servletContext;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFragmentRenderer.class);

}