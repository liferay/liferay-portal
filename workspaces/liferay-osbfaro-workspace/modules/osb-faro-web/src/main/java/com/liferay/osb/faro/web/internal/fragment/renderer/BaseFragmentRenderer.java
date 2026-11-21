/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.web.internal.display.context.ChannelSelectorDisplayContext;
import com.liferay.osb.faro.web.internal.helper.ProjectHelper;
import com.liferay.osb.faro.web.internal.model.display.asah.FaroChannelDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.FaroUserDisplay;
import com.liferay.osb.faro.web.internal.util.FaroFragmentRendererUtil;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.osb.faro.web.internal.util.comparator.FaroChannelComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
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

import java.util.Collections;
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

			if (!FeatureFlagManagerUtil.isEnabled(
					portal.getCompanyId(httpServletRequest), "LPD-114293")) {

				FaroFragmentRendererUtil.renderScriptTag(
					httpServletRequest, httpServletResponse, null,
					StringBundler.concat(
						"document.addEventListener('DOMContentLoaded', () => {",
						"for (const id of ['liferayAUICSS', ",
						"'liferayThemeCSS']) {",
						"const element = document.getElementById(id);",
						"if (element) {element.disabled = true;}}});"),
					null, null);
			}

			PageContext pageContext = PageContextFactoryUtil.create(
				httpServletRequest, httpServletResponse);

			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write("<div>");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			ComponentTag componentTag = new ComponentTag();

			componentTag.setModule(getModule(httpServletRequest));
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
					"channels",
					() -> TransformUtil.transform(
						faroChannelLocalService.search(
							themeDisplay.getScopeGroupId(), null,
							QueryUtil.ALL_POS, QueryUtil.ALL_POS,
							new FaroChannelComparator(
								Collections.singletonList(
									new OrderByField("name", "asc")))),
						faroChannel -> JSONUtil.convertValue(
							new FaroChannelDisplay(faroChannel), Map.class))
				).put(
					"currentUser",
					() -> {
						FaroUser faroUser = faroUserLocalService.fetchFaroUser(
							themeDisplay.getScopeGroupId(),
							themeDisplay.getUserId());

						if (faroUser == null) {
							return JSONUtil.convertValue(
								new FaroUserDisplay(themeDisplay.getUser()),
								Map.class);
						}

						return JSONUtil.convertValue(
							new FaroUserDisplay(faroUser), Map.class);
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
					"ldpEnabled",
					FaroNavigationUtil.isLDPEnabled(
						themeDisplay.getScopeGroupId())
				).put(
					"navigationItems",
					() -> getNavigationItems(httpServletRequest)
				).put(
					"pageURLs",
					() -> {
						Group group = groupLocalService.getGroup(
							themeDisplay.getCompanyId(), GroupConstants.GUEST);

						return HashMapBuilder.putAll(
							_getPageURLs(group.getGroupId(), themeDisplay)
						).putAll(
							_getPageURLs(
								themeDisplay.getScopeGroupId(), themeDisplay)
						).build();
					}
				).put(
					"params", getParams(httpServletRequest)
				).put(
					"project",
					() -> {
						FaroProject faroProject =
							faroProjectLocalService.fetchFaroProjectByGroupId(
								themeDisplay.getScopeGroupId());

						if (faroProject == null) {
							return null;
						}

						return JSONUtil.convertValue(
							projectHelper.getProjectDisplay(faroProject),
							Map.class);
					}
				).put(
					"signOutURL", themeDisplay.getURLSignOut()
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

	protected abstract String getModule(HttpServletRequest httpServletRequest)
		throws Exception;

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
	protected FaroChannelLocalService faroChannelLocalService;

	@Reference
	protected FaroProjectLocalService faroProjectLocalService;

	@Reference
	protected FaroUserLocalService faroUserLocalService;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected ProjectHelper projectHelper;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.osb.faro.web)")
	protected ServletContext servletContext;

	private Map<String, String> _getPageURLs(
			long groupId, ThemeDisplay themeDisplay)
		throws PortalException {

		Map<String, String> pageURLs = new HashMap<>();

		for (Layout layout : layoutLocalService.getLayouts(groupId, false)) {
			pageURLs.put(
				layout.getFriendlyURL(),
				portal.getLayoutFullURL(layout, themeDisplay));
		}

		return pageURLs;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFragmentRenderer.class);

}