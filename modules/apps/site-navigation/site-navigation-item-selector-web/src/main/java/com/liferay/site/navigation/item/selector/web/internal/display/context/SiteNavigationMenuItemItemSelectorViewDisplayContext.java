/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.item.selector.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class SiteNavigationMenuItemItemSelectorViewDisplayContext {

	public SiteNavigationMenuItemItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		SiteNavigationMenuItemTypeRegistry siteNavigationMenuItemTypeRegistry) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_siteNavigationMenuItemTypeRegistry =
			siteNavigationMenuItemTypeRegistry;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public SiteNavigationMenu getSiteNavigationMenu() {
		if (_siteNavigationMenu != null) {
			return _siteNavigationMenu;
		}

		long siteNavigationMenuId = ParamUtil.getLong(
			_httpServletRequest, "siteNavigationMenuId");

		if (siteNavigationMenuId > 0) {
			_siteNavigationMenu =
				SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(
					siteNavigationMenuId);

			return _siteNavigationMenu;
		}

		int siteNavigationMenuType = _getSiteNavigationMenuType();

		if ((siteNavigationMenuType != SiteNavigationConstants.TYPE_PRIMARY) &&
			(siteNavigationMenuType !=
				SiteNavigationConstants.TYPE_SECONDARY) &&
			(siteNavigationMenuType != SiteNavigationConstants.TYPE_SOCIAL)) {

			return _siteNavigationMenu;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(
				themeDisplay.getScopeGroupId(), siteNavigationMenuType);

		return _siteNavigationMenu;
	}

	public JSONArray getSiteNavigationMenuItemsJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		SiteNavigationMenu siteNavigationMenu = getSiteNavigationMenu();

		if (siteNavigationMenu != null) {
			jsonObject.put(
				"children",
				_getSiteNavigationMenuItemsJSONArray(
					siteNavigationMenu.getSiteNavigationMenuId(), 0)
			).put(
				"disabled", true
			).put(
				"icon", "blogs"
			).put(
				"id", "0"
			).put(
				"name", siteNavigationMenu.getName()
			);

			jsonArray.put(jsonObject);

			return jsonArray;
		}

		int siteNavigationMenuType = _getSiteNavigationMenuType();

		if ((siteNavigationMenuType !=
				SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) &&
			(siteNavigationMenuType !=
				SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY)) {

			return jsonArray;
		}

		String name = "private-pages-hierarchy";
		boolean privateLayout = true;

		if (siteNavigationMenuType ==
				SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group group = themeDisplay.getScopeGroup();

			if (group.isPrivateLayoutsEnabled()) {
				name = "public-pages-hierarchy";
			}
			else {
				name = "pages-hierarchy";
			}

			privateLayout = false;
		}

		jsonObject.put(
			"children", _getLayoutItemsJSONArray(privateLayout, 0)
		).put(
			"disabled", true
		).put(
			"icon", "page"
		).put(
			"id", "0"
		);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		jsonObject.put(
			"name", LanguageUtil.get(themeDisplay.getLocale(), name));

		jsonArray.put(jsonObject);

		return jsonArray;
	}

	public boolean isShowSelectSiteNavigationMenuItem() {
		SiteNavigationMenu siteNavigationMenu = getSiteNavigationMenu();

		if (siteNavigationMenu != null) {
			return true;
		}

		int siteNavigationMenuType = _getSiteNavigationMenuType();

		if ((siteNavigationMenuType ==
				SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) ||
			(siteNavigationMenuType ==
				SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY)) {

			return true;
		}

		return false;
	}

	private JSONArray _getLayoutItemsJSONArray(
		boolean privateLayout, long parentLayoutId) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			themeDisplay.getScopeGroupId(), privateLayout, parentLayoutId);

		for (Layout layout : layouts) {
			if (layout.isHidden() || StagingUtil.isIncomplete(layout)) {
				continue;
			}

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			JSONArray childrenJSONArray = _getLayoutItemsJSONArray(
				privateLayout, layout.getLayoutId());

			if (childrenJSONArray.length() > 0) {
				jsonObject.put("children", childrenJSONArray);
			}

			jsonObject.put(
				"icon", "page"
			).put(
				"id", layout.getUuid()
			).put(
				"name", layout.getName(themeDisplay.getLocale())
			).put(
				"selected", false
			);

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private long _getSiteNavigationMenuItemId() {
		if (_siteNavigationMenuItemId != null) {
			return _siteNavigationMenuItemId;
		}

		_siteNavigationMenuItemId = ParamUtil.getLong(
			_httpServletRequest, "siteNavigationMenuItemId");

		return _siteNavigationMenuItemId;
	}

	private JSONArray _getSiteNavigationMenuItemsJSONArray(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			SiteNavigationMenuItemLocalServiceUtil.getSiteNavigationMenuItems(
				siteNavigationMenuId, parentSiteNavigationMenuItemId);

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			JSONArray childrenJSONArray = _getSiteNavigationMenuItemsJSONArray(
				siteNavigationMenuId,
				siteNavigationMenuItem.getSiteNavigationMenuItemId());

			if (childrenJSONArray.length() > 0) {
				jsonObject.put("children", childrenJSONArray);
			}

			SiteNavigationMenuItemType siteNavigationMenuItemType =
				_siteNavigationMenuItemTypeRegistry.
					getSiteNavigationMenuItemType(
						siteNavigationMenuItem.getType());

			jsonObject.put(
				"icon", siteNavigationMenuItemType.getIcon()
			).put(
				"id",
				String.valueOf(
					siteNavigationMenuItem.getSiteNavigationMenuItemId())
			).put(
				"name",
				siteNavigationMenuItemType.getTitle(
					siteNavigationMenuItem, themeDisplay.getLocale())
			);

			if (siteNavigationMenuItem.getSiteNavigationMenuItemId() ==
					_getSiteNavigationMenuItemId()) {

				jsonObject.put("selected", true);
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private int _getSiteNavigationMenuType() {
		if (_siteNavigationMenuType != null) {
			return _siteNavigationMenuType;
		}

		_siteNavigationMenuType = ParamUtil.getInteger(
			_httpServletRequest, "siteNavigationMenuType");

		return _siteNavigationMenuType;
	}

	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private SiteNavigationMenu _siteNavigationMenu;
	private Long _siteNavigationMenuItemId;
	private final SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;
	private Integer _siteNavigationMenuType;

}