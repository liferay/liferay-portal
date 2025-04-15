/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;
import com.liferay.site.navigation.taglib.internal.util.SiteNavigationMenuNavItemImpl;
import com.liferay.site.navigation.taglib.servlet.taglib.NavigationMenuMode;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pavel Savinov
 */
public class NavItemUtil {

	public static List<NavItem> getBranchNavItems(
			HttpServletRequest httpServletRequest, long siteNavigationMenuId)
		throws Exception {

		if (siteNavigationMenuId > 0) {
			return _getBranchNavItems(httpServletRequest, siteNavigationMenuId);
		}

		return _getBranchNavItems(httpServletRequest);
	}

	public static List<NavItem> getChildNavItems(
		HttpServletRequest httpServletRequest, long siteNavigationMenuId,
		long parentSiteNavigationMenuItemId) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_getSiteNavigationMenuItems(
				httpServletRequest, siteNavigationMenuId,
				parentSiteNavigationMenuItemId);

		List<NavItem> navItems = new ArrayList<>(
			siteNavigationMenuItems.size());

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			SiteNavigationMenuItemTypeRegistry
				siteNavigationMenuItemTypeRegistry =
					_siteNavigationMenuItemTypeRegistrySnapshot.get();

			SiteNavigationMenuItemType siteNavigationMenuItemType =
				siteNavigationMenuItemTypeRegistry.
					getSiteNavigationMenuItemType(
						siteNavigationMenuItem.getType());

			try {
				if ((siteNavigationMenuItemType == null) ||
					!siteNavigationMenuItemType.hasPermission(
						themeDisplay.getPermissionChecker(),
						siteNavigationMenuItem)) {

					continue;
				}

				if (!siteNavigationMenuItemType.isDynamic()) {
					navItems.add(
						new SiteNavigationMenuNavItemImpl(
							httpServletRequest, themeDisplay,
							siteNavigationMenuItem));

					continue;
				}

				for (SiteNavigationMenuItem dynamicSiteNavigationMenuItem :
						siteNavigationMenuItemType.getSiteNavigationMenuItems(
							httpServletRequest, siteNavigationMenuItem)) {

					navItems.add(
						new SiteNavigationMenuNavItemImpl(
							httpServletRequest, themeDisplay,
							dynamicSiteNavigationMenuItem));
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return navItems;
	}

	public static Map<String, Object> getNavigationMenuContext(
		int displayDepth, String expandedLevels,
		HttpServletRequest httpServletRequest,
		NavigationMenuMode navigationMenuMode, boolean preview,
		String rootItemId, int rootItemLevel, String rootItemType,
		long siteNavigationMenuId) {

		List<NavItem> branchNavItems = null;
		List<NavItem> navItems = null;

		try {
			branchNavItems = getBranchNavItems(
				httpServletRequest, siteNavigationMenuId);

			navItems = getNavItems(
				branchNavItems, httpServletRequest, navigationMenuMode,
				rootItemId, rootItemLevel, rootItemType, siteNavigationMenuId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return HashMapBuilder.<String, Object>put(
			"branchNavItems", branchNavItems
		).put(
			"displayDepth", displayDepth
		).put(
			"includedLayouts", expandedLevels
		).put(
			"navItems", navItems
		).put(
			"preview", preview
		).put(
			"rootLayoutLevel", rootItemLevel
		).put(
			"rootLayoutType", rootItemType
		).build();
	}

	public static List<NavItem> getNavItems(
			List<NavItem> branchNavItems, HttpServletRequest httpServletRequest,
			NavigationMenuMode navigationMenuMode, String rootItemId,
			int rootItemLevel, String rootItemType, long siteNavigationMenuId)
		throws Exception {

		if (siteNavigationMenuId > 0) {
			return _getMenuNavItems(
				httpServletRequest, branchNavItems, rootItemType, rootItemLevel,
				siteNavigationMenuId, rootItemId);
		}

		return _getNavItems(
			navigationMenuMode, httpServletRequest, rootItemType, rootItemLevel,
			rootItemId, branchNavItems);
	}

	private static List<NavItem> _fromLayouts(
			NavigationMenuMode navigationMenuMode,
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		if (navigationMenuMode == NavigationMenuMode.DEFAULT) {
			return themeDisplay.getNavItems();
		}

		boolean privateLayout = false;

		if (navigationMenuMode == NavigationMenuMode.PRIVATE_PAGES) {
			privateLayout = true;
		}

		return NavItem.fromLayouts(
			httpServletRequest, _getLayouts(privateLayout, themeDisplay),
			themeDisplay);
	}

	private static List<NavItem> _getBranchNavItems(
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isDraftLayout()) {
			LayoutLocalService layoutLocalService =
				_layoutLocalServiceSnapshot.get();

			layout = layoutLocalService.fetchLayout(layout.getClassPK());
		}

		if (layout.isRootLayout()) {
			return Collections.singletonList(
				new NavItem(httpServletRequest, themeDisplay, layout));
		}

		List<Layout> ancestorLayouts = layout.getAncestors();

		Collections.reverse(ancestorLayouts);

		ancestorLayouts.add(layout);

		return TransformUtil.transform(
			ancestorLayouts,
			ancestorLayout -> new NavItem(
				httpServletRequest, themeDisplay, ancestorLayout));
	}

	private static List<NavItem> _getBranchNavItems(
		HttpServletRequest httpServletRequest, long siteNavigationMenuId) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SiteNavigationMenuItem siteNavigationMenuItem =
			SiteNavigationMenuItemLocalServiceUtil.fetchSiteNavigationMenuItem(
				_getRelativeSiteNavigationMenuItemId(
					themeDisplay.getLayout(), siteNavigationMenuId));

		if (siteNavigationMenuItem == null) {
			return new ArrayList<>();
		}

		List<SiteNavigationMenuItem> ancestorSiteNavigationMenuItems =
			siteNavigationMenuItem.getAncestors();

		Collections.reverse(ancestorSiteNavigationMenuItems);

		ancestorSiteNavigationMenuItems.add(siteNavigationMenuItem);

		return TransformUtil.transform(
			ancestorSiteNavigationMenuItems,
			ancestorSiteNavigationMenuItem -> new SiteNavigationMenuNavItemImpl(
				httpServletRequest, themeDisplay,
				ancestorSiteNavigationMenuItem));
	}

	private static List<Layout> _getLayouts(
			boolean privateLayout, ThemeDisplay themeDisplay)
		throws Exception {

		LayoutLocalService layoutLocalService =
			_layoutLocalServiceSnapshot.get();

		List<Layout> layouts = ListUtil.copy(
			layoutLocalService.getLayouts(
				themeDisplay.getScopeGroupId(), privateLayout,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID));

		Iterator<Layout> iterator = layouts.iterator();

		while (iterator.hasNext()) {
			Layout layout = iterator.next();

			if (layout.isHidden() || !layout.isPublished() ||
				!LayoutPermissionUtil.contains(
					themeDisplay.getPermissionChecker(), layout,
					ActionKeys.VIEW)) {

				iterator.remove();
			}
		}

		return layouts;
	}

	private static List<NavItem> _getMenuNavItems(
			HttpServletRequest httpServletRequest, List<NavItem> branchNavItems,
			String rootItemType, int rootItemLevel, long siteNavigationMenuId,
			String rootItemId)
		throws Exception {

		if (rootItemType.equals("absolute")) {
			if (rootItemLevel == 0) {
				return getChildNavItems(
					httpServletRequest, siteNavigationMenuId, 0);
			}
			else if (branchNavItems.size() >= rootItemLevel) {
				NavItem rootNavItem = branchNavItems.get(rootItemLevel - 1);

				return rootNavItem.getChildren();
			}
		}
		else if (rootItemType.equals("relative") && (rootItemLevel >= 0) &&
				 (rootItemLevel < (branchNavItems.size() + 1))) {

			int absoluteLevel = branchNavItems.size() - 1 - rootItemLevel;

			if (absoluteLevel == -1) {
				return getChildNavItems(
					httpServletRequest, siteNavigationMenuId, 0);
			}
			else if ((absoluteLevel >= 0) &&
					 (absoluteLevel < branchNavItems.size())) {

				NavItem rootNavItem = branchNavItems.get(absoluteLevel);

				return rootNavItem.getChildren();
			}
		}
		else if (rootItemType.equals("select")) {
			return getChildNavItems(
				httpServletRequest, siteNavigationMenuId,
				GetterUtil.getLong(rootItemId));
		}

		return new ArrayList<>();
	}

	private static List<NavItem> _getNavItems(
			NavigationMenuMode navigationMenuMode,
			HttpServletRequest httpServletRequest, String rootLayoutType,
			int rootLayoutLevel, String rootLayoutUuid,
			List<NavItem> branchNavItems)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<NavItem> navItems = null;
		NavItem rootNavItem = null;

		if (rootLayoutType.equals("absolute")) {
			if (rootLayoutLevel == 0) {
				navItems = _fromLayouts(
					navigationMenuMode, httpServletRequest, themeDisplay);
			}
			else if (branchNavItems.size() >= rootLayoutLevel) {
				rootNavItem = branchNavItems.get(rootLayoutLevel - 1);
			}
		}
		else if (rootLayoutType.equals("relative")) {
			if ((rootLayoutLevel >= 0) &&
				(rootLayoutLevel <= (branchNavItems.size() + 1))) {

				int absoluteLevel = branchNavItems.size() - 1 - rootLayoutLevel;

				if (absoluteLevel == -1) {
					navItems = _fromLayouts(
						navigationMenuMode, httpServletRequest, themeDisplay);
				}
				else if ((absoluteLevel >= 0) &&
						 (absoluteLevel < branchNavItems.size())) {

					rootNavItem = branchNavItems.get(absoluteLevel);
				}
			}
		}
		else if (rootLayoutType.equals("select")) {
			if (Validator.isNotNull(rootLayoutUuid)) {
				Layout layout = themeDisplay.getLayout();

				LayoutLocalService layoutLocalService =
					_layoutLocalServiceSnapshot.get();

				Layout rootLayout =
					layoutLocalService.fetchLayoutByUuidAndGroupId(
						rootLayoutUuid, layout.getGroupId(), false);

				if (rootLayout == null) {
					rootLayout = layoutLocalService.fetchLayoutByUuidAndGroupId(
						rootLayoutUuid, layout.getGroupId(), true);
				}

				if (rootLayout != null) {
					rootNavItem = new NavItem(
						httpServletRequest, themeDisplay, rootLayout);
				}
			}
			else {
				navItems = _fromLayouts(
					navigationMenuMode, httpServletRequest, themeDisplay);
			}
		}

		if (rootNavItem == null) {
			if (navItems == null) {
				return new ArrayList<>();
			}

			return navItems;
		}

		return rootNavItem.getChildren();
	}

	private static long _getRelativeSiteNavigationMenuItemId(
		Layout layout, long siteNavigationMenuId) {

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			SiteNavigationMenuItemLocalServiceUtil.getSiteNavigationMenuItems(
				siteNavigationMenuId);

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			UnicodeProperties unicodeProperties =
				UnicodePropertiesBuilder.fastLoad(
					siteNavigationMenuItem.getTypeSettings()
				).build();

			String itemLayoutUuid = unicodeProperties.getProperty("layoutUuid");

			if (Objects.equals(layout.getUuid(), itemLayoutUuid)) {
				return siteNavigationMenuItem.getSiteNavigationMenuItemId();
			}
		}

		return 0;
	}

	private static List<SiteNavigationMenuItem> _getSiteNavigationMenuItems(
		HttpServletRequest httpServletRequest, long siteNavigationMenuId,
		long parentSiteNavigationMenuItemId) {

		try {
			if (parentSiteNavigationMenuItemId == 0) {
				SiteNavigationMenuItemService siteNavigationMenuItemService =
					_siteNavigationMenuItemServiceSnapshot.get();

				return siteNavigationMenuItemService.getSiteNavigationMenuItems(
					siteNavigationMenuId, parentSiteNavigationMenuItemId);
			}

			SiteNavigationMenuItemLocalService
				siteNavigationMenuItemLocalService =
					_siteNavigationMenuItemLocalServiceSnapshot.get();

			SiteNavigationMenuItem parentSiteNavigationMenuItem =
				siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
					parentSiteNavigationMenuItemId);

			SiteNavigationMenuItemTypeRegistry
				siteNavigationMenuItemTypeRegistry =
					_siteNavigationMenuItemTypeRegistrySnapshot.get();

			SiteNavigationMenuItemType siteNavigationMenuItemType =
				siteNavigationMenuItemTypeRegistry.
					getSiteNavigationMenuItemType(
						parentSiteNavigationMenuItem.getType());

			if (siteNavigationMenuItemType.isDynamic()) {
				return siteNavigationMenuItemType.
					getChildrenSiteNavigationMenuItems(
						httpServletRequest, parentSiteNavigationMenuItem);
			}

			SiteNavigationMenuItemService siteNavigationMenuItemService =
				_siteNavigationMenuItemServiceSnapshot.get();

			return siteNavigationMenuItemService.getSiteNavigationMenuItems(
				siteNavigationMenuId, parentSiteNavigationMenuItemId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get site navigation menu items", exception);
			}
		}

		return Collections.emptyList();
	}

	private static final Log _log = LogFactoryUtil.getLog(NavItemUtil.class);

	private static final Snapshot<LayoutLocalService>
		_layoutLocalServiceSnapshot = new Snapshot<>(
			NavItemUtil.class, LayoutLocalService.class);
	private static final Snapshot<SiteNavigationMenuItemLocalService>
		_siteNavigationMenuItemLocalServiceSnapshot = new Snapshot<>(
			NavItemUtil.class, SiteNavigationMenuItemLocalService.class);
	private static final Snapshot<SiteNavigationMenuItemService>
		_siteNavigationMenuItemServiceSnapshot = new Snapshot<>(
			NavItemUtil.class, SiteNavigationMenuItemService.class);
	private static final Snapshot<SiteNavigationMenuItemTypeRegistry>
		_siteNavigationMenuItemTypeRegistrySnapshot = new Snapshot<>(
			NavItemUtil.class, SiteNavigationMenuItemTypeRegistry.class);

}