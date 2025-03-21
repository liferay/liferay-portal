/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.display.context;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Michael Bowerman
 */
public class SiteNavigationMenuConfigurationDisplayContext {

	public SiteNavigationMenuConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		SiteNavigationMenuDisplayContext siteNavigationMenuDisplayContext) {

		_httpServletRequest = httpServletRequest;
		_siteNavigationMenuDisplayContext = siteNavigationMenuDisplayContext;
	}

	public String getLayoutsLabel() {
		if (_layoutsLabel != null) {
			return _layoutsLabel;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();
		Layout layout = themeDisplay.getLayout();

		if (scopeGroup.isPrivateLayoutsEnabled()) {
			if (hasLayoutPageTemplateEntry() &&
				(scopeGroup.hasPublicLayouts() ||
				 scopeGroup.hasPrivateLayouts())) {

				_layoutsLabel = "pages-hierarchy";
			}
			else if (scopeGroup.hasPublicLayouts() && layout.isPublicLayout()) {
				_layoutsLabel = "public-pages-hierarchy";
			}
			else if (scopeGroup.hasPrivateLayouts() &&
					 layout.isPrivateLayout()) {

				_layoutsLabel = "private-pages-hierarchy";
			}
			else {
				_layoutsLabel = StringPool.BLANK;
			}

			return _layoutsLabel;
		}

		if (scopeGroup.hasPublicLayouts() &&
			(hasLayoutPageTemplateEntry() || layout.isPublicLayout())) {

			_layoutsLabel = "pages-hierarchy";
		}
		else {
			_layoutsLabel = StringPool.BLANK;
		}

		return _layoutsLabel;
	}

	public int getLayoutsValue() {
		if (_layoutsValue != null) {
			return _layoutsValue;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();
		Layout layout = themeDisplay.getLayout();

		if (scopeGroup.isPrivateLayoutsEnabled()) {
			if (!hasLayoutPageTemplateEntry()) {
				if (scopeGroup.hasPublicLayouts() && layout.isPublicLayout()) {
					_layoutsValue =
						SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY;
				}
				else if (scopeGroup.hasPrivateLayouts() &&
						 layout.isPrivateLayout()) {

					_layoutsValue =
						SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY;
				}
			}

			if (_layoutsValue == null) {
				_layoutsValue = SiteNavigationConstants.TYPE_DEFAULT;
			}

			return _layoutsValue;
		}

		if (scopeGroup.hasPublicLayouts()) {
			if (hasLayoutPageTemplateEntry()) {
				_layoutsValue = SiteNavigationConstants.TYPE_DEFAULT;
			}
			else if (layout.isPublicLayout()) {
				_layoutsValue =
					SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY;
			}
			else {
				_layoutsValue = SiteNavigationConstants.TYPE_DEFAULT;
			}
		}
		else {
			_layoutsValue = SiteNavigationConstants.TYPE_DEFAULT;
		}

		return _layoutsValue;
	}

	public boolean hasLayoutPageTemplateEntry() {
		if (_hasLayoutPageTemplateEntry != null) {
			return _hasLayoutPageTemplateEntry;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		long plid = layout.getPlid();

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if (layoutPageTemplateEntry != null) {
			_hasLayoutPageTemplateEntry = true;
		}
		else {
			_hasLayoutPageTemplateEntry = false;
		}

		return _hasLayoutPageTemplateEntry;
	}

	public boolean isLayoutsSelected() {
		if (_layoutsSelected != null) {
			return _layoutsSelected;
		}

		int selectSiteNavigationMenuType =
			_siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType();

		if (selectSiteNavigationMenuType == getLayoutsValue()) {
			_layoutsSelected = true;
		}
		else {
			_layoutsSelected = false;
		}

		return _layoutsSelected;
	}

	private Boolean _hasLayoutPageTemplateEntry;
	private final HttpServletRequest _httpServletRequest;
	private String _layoutsLabel;
	private Boolean _layoutsSelected;
	private Integer _layoutsValue;
	private final SiteNavigationMenuDisplayContext
		_siteNavigationMenuDisplayContext;

}