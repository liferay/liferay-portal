/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.site.map.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.site.map.web.internal.configuration.SiteNavigationSiteMapPortletInstanceConfiguration;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Juergen Kappler
 */
public class SiteNavigationSiteMapDisplayContext {

	public SiteNavigationSiteMapDisplayContext(
			HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_itemSelector = (ItemSelector)httpServletRequest.getAttribute(
			ItemSelector.class.getName());

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_siteNavigationSiteMapPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				SiteNavigationSiteMapPortletInstanceConfiguration.class,
				_themeDisplay);
	}

	public String buildSiteMap() throws Exception {
		StringBundler sb = new StringBundler();

		_buildSiteMap(
			_themeDisplay.getLayout(), getRootLayouts(), getRootLayout(),
			isIncludeRootInTree(),
			_siteNavigationSiteMapPortletInstanceConfiguration.displayDepth(),
			_siteNavigationSiteMapPortletInstanceConfiguration.
				showCurrentPage(),
			_siteNavigationSiteMapPortletInstanceConfiguration.useHtmlTitle(),
			_siteNavigationSiteMapPortletInstanceConfiguration.
				showHiddenPages(),
			1, _themeDisplay, sb);

		return sb.toString();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_siteNavigationSiteMapPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				_themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupId = group.getGroupId();
		}
		else {
			_displayStyleGroupId = _themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			_siteNavigationSiteMapPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				_themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public String getItemSelectorURL() {
		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());
		layoutItemSelectorCriterion.setShowBreadcrumb(false);
		layoutItemSelectorCriterion.setMultiSelection(false);

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "selectLayout",
				layoutItemSelectorCriterion)
		).buildString();
	}

	public Layout getRootLayout() {
		if (_rootLayout != null) {
			return _rootLayout;
		}

		String rootLayoutUuid =
			_siteNavigationSiteMapPortletInstanceConfiguration.rootLayoutUuid();

		if (Validator.isNotNull(rootLayoutUuid)) {
			Layout layout = _themeDisplay.getLayout();

			_rootLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				rootLayoutUuid, _themeDisplay.getScopeGroupId(),
				layout.isPrivateLayout());
		}

		return _rootLayout;
	}

	public long getRootLayoutId() {
		if (_rootLayoutId != null) {
			return _rootLayoutId;
		}

		Layout rootLayout = getRootLayout();

		if (Validator.isNotNull(
				_siteNavigationSiteMapPortletInstanceConfiguration.
					rootLayoutUuid()) &&
			(rootLayout != null)) {

			_rootLayoutId = rootLayout.getLayoutId();
		}
		else {
			_rootLayoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;
		}

		return _rootLayoutId;
	}

	public List<Layout> getRootLayouts() {
		Layout layout = _themeDisplay.getLayout();

		return LayoutLocalServiceUtil.getLayouts(
			layout.getGroupId(), layout.isPrivateLayout(), getRootLayoutId());
	}

	public SiteNavigationSiteMapPortletInstanceConfiguration
		getSiteNavigationSiteMapPortletInstanceConfiguration() {

		return _siteNavigationSiteMapPortletInstanceConfiguration;
	}

	public Boolean isIncludeRootInTree() {
		if (_includeRootInTree != null) {
			return _includeRootInTree;
		}

		_includeRootInTree =
			_siteNavigationSiteMapPortletInstanceConfiguration.
				includeRootInTree();

		if (Validator.isNull(
				_siteNavigationSiteMapPortletInstanceConfiguration.
					rootLayoutUuid()) ||
			(getRootLayoutId() == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {

			_includeRootInTree = false;
		}

		return _includeRootInTree;
	}

	private void _buildLayoutView(
			Layout layout, String cssClass, boolean useHtmlTitle,
			ThemeDisplay themeDisplay, StringBundler sb)
		throws Exception {

		sb.append("<a");

		LayoutType layoutType = layout.getLayoutType();

		if (layoutType.isBrowsable()) {
			sb.append(" href=\"");
			sb.append(PortalUtil.getLayoutURL(layout, themeDisplay));
			sb.append("\" ");
			sb.append(PortalUtil.getLayoutTarget(layout));
		}

		if (Validator.isNotNull(cssClass)) {
			sb.append(" class=\"");
			sb.append(cssClass);
			sb.append("\" ");
		}

		sb.append("> ");

		String layoutName = HtmlUtil.escape(
			layout.getName(themeDisplay.getLocale()));

		if (useHtmlTitle) {
			layoutName = HtmlUtil.escape(
				layout.getHTMLTitle(themeDisplay.getLocale()));
		}

		sb.append(layoutName);
		sb.append("</a>");
	}

	private void _buildSiteMap(
			Layout layout, List<Layout> layouts, Layout rootLayout,
			boolean includeRootInTree, int displayDepth,
			boolean showCurrentPage, boolean useHtmlTitle,
			boolean showHiddenPages, int curDepth, ThemeDisplay themeDisplay,
			StringBundler sb)
		throws Exception {

		if (layouts.isEmpty() ||
			((rootLayout != null) &&
			 !LayoutPermissionUtil.contains(
				 themeDisplay.getPermissionChecker(), rootLayout,
				 ActionKeys.VIEW))) {

			return;
		}

		sb.append("<ul>");

		if (includeRootInTree && (rootLayout != null) && (curDepth == 1)) {
			sb.append("<li>");

			String cssClass = "root";

			if (rootLayout.getPlid() == layout.getPlid()) {
				cssClass += " current";
			}

			_buildLayoutView(
				rootLayout, cssClass, useHtmlTitle, themeDisplay, sb);

			_buildSiteMap(
				layout, layouts, rootLayout, includeRootInTree, displayDepth,
				showCurrentPage, useHtmlTitle, showHiddenPages, curDepth + 1,
				themeDisplay, sb);

			sb.append("</li>");
		}
		else {
			for (Layout curLayout : layouts) {
				if ((showHiddenPages || !curLayout.isHidden()) &&
					LayoutPermissionUtil.contains(
						themeDisplay.getPermissionChecker(), curLayout,
						ActionKeys.VIEW)) {

					sb.append("<li>");

					String cssClass = StringPool.BLANK;

					if (curLayout.getPlid() == layout.getPlid()) {
						cssClass = "current";
					}

					_buildLayoutView(
						curLayout, cssClass, useHtmlTitle, themeDisplay, sb);

					if ((displayDepth == 0) || (displayDepth > curDepth)) {
						if (showHiddenPages) {
							_buildSiteMap(
								layout, curLayout.getChildren(), rootLayout,
								includeRootInTree, displayDepth,
								showCurrentPage, useHtmlTitle, showHiddenPages,
								curDepth + 1, themeDisplay, sb);
						}
						else {
							_buildSiteMap(
								layout,
								curLayout.getChildren(
									themeDisplay.getPermissionChecker()),
								rootLayout, includeRootInTree, displayDepth,
								showCurrentPage, useHtmlTitle, showHiddenPages,
								curDepth + 1, themeDisplay, sb);
						}
					}

					sb.append("</li>");
				}
			}
		}

		sb.append("</ul>");
	}

	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final HttpServletRequest _httpServletRequest;
	private Boolean _includeRootInTree;
	private final ItemSelector _itemSelector;
	private final RenderResponse _renderResponse;
	private Layout _rootLayout;
	private Long _rootLayoutId;
	private final SiteNavigationSiteMapPortletInstanceConfiguration
		_siteNavigationSiteMapPortletInstanceConfiguration;
	private final ThemeDisplay _themeDisplay;

}