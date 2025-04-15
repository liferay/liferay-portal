/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.internal.util;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.taglib.internal.servlet.ServletContextUtil;
import com.liferay.site.navigation.taglib.servlet.taglib.util.NavItemUtil;
import com.liferay.site.navigation.theme.SiteNavigationMenuNavItem;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Savinov
 */
public class SiteNavigationMenuNavItemImpl
	extends NavItem implements SiteNavigationMenuNavItem {

	public SiteNavigationMenuNavItemImpl(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		SiteNavigationMenuItem siteNavigationMenuItem) {

		super(httpServletRequest, themeDisplay, themeDisplay.getLayout());

		_httpServletRequest = httpServletRequest;
		_themeDisplay = themeDisplay;
		_siteNavigationMenuItem = siteNavigationMenuItem;

		_siteNavigationMenuItemType =
			ServletContextUtil.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SiteNavigationMenuNavItemImpl) {
			SiteNavigationMenuNavItemImpl siteNavigationMenuNavItemImpl =
				(SiteNavigationMenuNavItemImpl)object;

			return _siteNavigationMenuItem.equals(
				siteNavigationMenuNavItemImpl);
		}

		return false;
	}

	@Override
	public List<NavItem> getChildren() throws Exception {
		if (!_siteNavigationMenuItemType.isDynamic()) {
			return NavItemUtil.getChildNavItems(
				_httpServletRequest,
				_siteNavigationMenuItem.getSiteNavigationMenuId(),
				_siteNavigationMenuItem.getSiteNavigationMenuItemId());
		}

		List<NavItem> children = new ArrayList<>();

		for (SiteNavigationMenuItem dynamicSiteNavigationMenuItem :
				_siteNavigationMenuItemType.getChildrenSiteNavigationMenuItems(
					_httpServletRequest, _siteNavigationMenuItem)) {

			children.add(
				new SiteNavigationMenuNavItemImpl(
					_httpServletRequest, _themeDisplay,
					dynamicSiteNavigationMenuItem));
		}

		return children;
	}

	@Override
	public String getDisplayIcon() {
		return _siteNavigationMenuItemType.getDisplayIcon(
			_siteNavigationMenuItem);
	}

	@Override
	public Map<String, Serializable> getExpandoAttributes() {
		Map<String, Serializable> expandoAttributes =
			super.getExpandoAttributes();

		if (expandoAttributes == null) {
			expandoAttributes = Collections.emptyMap();
		}

		ExpandoBridge expandoBridge =
			_siteNavigationMenuItem.getExpandoBridge();

		if (expandoBridge != null) {
			expandoAttributes.putAll(expandoBridge.getAttributes());
		}

		return expandoAttributes;
	}

	@Override
	public Layout getLayout() {
		return _siteNavigationMenuItemType.getLayout(_siteNavigationMenuItem);
	}

	@Override
	public long getLayoutId() {
		return _siteNavigationMenuItem.getSiteNavigationMenuItemId();
	}

	@Override
	public String getRegularURL() throws Exception {
		return _siteNavigationMenuItemType.getRegularURL(
			_httpServletRequest, _siteNavigationMenuItem);
	}

	@Override
	public String getResetLayoutURL() throws Exception {
		return _siteNavigationMenuItemType.getResetLayoutURL(
			_httpServletRequest, _siteNavigationMenuItem);
	}

	@Override
	public String getResetMaxStateURL() throws Exception {
		return _siteNavigationMenuItemType.getResetMaxStateURL(
			_httpServletRequest, _siteNavigationMenuItem);
	}

	@Override
	public String getTarget() {
		return _siteNavigationMenuItemType.getTarget(_siteNavigationMenuItem);
	}

	@Override
	public String getTitle() {
		return _siteNavigationMenuItemType.getTitle(
			_siteNavigationMenuItem, _themeDisplay.getLocale());
	}

	@Override
	public String getUnescapedName() {
		return _siteNavigationMenuItemType.getUnescapedName(
			_siteNavigationMenuItem, _themeDisplay.getLanguageId());
	}

	@Override
	public int hashCode() {
		return _siteNavigationMenuItem.hashCode();
	}

	@Override
	public String iconURL() {
		return _siteNavigationMenuItemType.iconURL(
			_siteNavigationMenuItem, _themeDisplay.getPathImage());
	}

	@Override
	public boolean isBrowsable() {
		return _siteNavigationMenuItemType.isBrowsable(_siteNavigationMenuItem);
	}

	@Override
	public boolean isChildSelected() throws PortalException {
		return _siteNavigationMenuItemType.isChildSelected(
			_themeDisplay.isTilesSelectable(), _siteNavigationMenuItem,
			_themeDisplay.getLayout());
	}

	@Override
	public boolean isSelected() throws Exception {
		return _siteNavigationMenuItemType.isSelected(
			_themeDisplay.isTilesSelectable(), _siteNavigationMenuItem,
			_themeDisplay.getLayout());
	}

	private final HttpServletRequest _httpServletRequest;
	private final SiteNavigationMenuItem _siteNavigationMenuItem;
	private final SiteNavigationMenuItemType _siteNavigationMenuItemType;
	private final ThemeDisplay _themeDisplay;

}