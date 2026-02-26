/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.internal.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Thiago Buarque
 */
public class SideNavigationDisplayContext {

	public SideNavigationDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_panelAppRegistry = (PanelAppRegistry)httpServletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);

		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_portletId = _themeDisplay.getPpid();

		_panelCategory = _getActivePanelCategory(
			_panelCategoryHelper, PanelCategoryKeys.APPLICATIONS_MENU,
			_portletId, _themeDisplay);
	}

	public List<String> getExpandedKeys() {
		List<String> expandedKeys = new ArrayList<>();

		String expandedKeysString = SessionClicks.get(
			_httpServletRequest, _getExpandedKeysSessionKey(),
			StringPool.BLANK);

		if (!expandedKeysString.isEmpty()) {
			Collections.addAll(
				expandedKeys, expandedKeysString.split(StringPool.COMMA));

			return expandedKeys;
		}

		List<PanelCategory> childPanelCategories =
			_panelCategoryHelper.getChildPanelCategories(
				_panelCategory.getKey(), _themeDisplay);

		for (PanelCategory childPanelCategory : childPanelCategories) {
			expandedKeys.add(childPanelCategory.getKey());
		}

		return expandedKeys;
	}

	public String getPanelCategoryImageUrl() {
		return String.format(
			"%s/product_icons/%s_sm.svg", _themeDisplay.getPathThemeImages(),
			_panelCategory.getKey());
	}

	public String getPanelCategoryLabel() {
		return _panelCategory.getLabel(_themeDisplay.getLocale());
	}

	public String getPortletId() {
		return _portletId;
	}

	public Map<String, Object> getProps() throws Exception {
		String itemSelectedEventName = String.format(
			"_%s_selectSite",
			ProductNavigationProductMenuPortletKeys.
				PRODUCT_NAVIGATION_PRODUCT_MENU);

		return HashMapBuilder.<String, Object>put(
			"categoryImageUrl", getPanelCategoryImageUrl()
		).put(
			"expandedKeys", getExpandedKeys()
		).put(
			"expandedKeysSessionKey", _getExpandedKeysSessionKey()
		).put(
			"items", _getPropsItems()
		).put(
			"label", getPanelCategoryLabel()
		).put(
			"portletId", _portletId
		).put(
			"siteAdministrationItemSelectedEventName", itemSelectedEventName
		).put(
			"siteAdministrationItemSelectorUrl",
			() -> {
				ItemSelector itemSelector = _itemSelectorSnapshot.get();

				SiteItemSelectorCriterion siteItemSelectorCriterion =
					new SiteItemSelectorCriterion();

				siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new URLItemSelectorReturnType());

				return String.valueOf(
					itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						itemSelectedEventName, siteItemSelectorCriterion));
			}
		).put(
			"visible", isVisible()
		).put(
			"visibleSessionKey", _VISIBLE_SESSION_KEY
		).build();
	}

	public List<VerticalNavItem> getVerticalNavItems() throws Exception {
		List<VerticalNavItem> verticalNavItems = new ArrayList<>();

		verticalNavItems.addAll(_getVerticalNavItems(_panelCategory));

		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					_panelCategory.getKey(), _themeDisplay)) {

			List<VerticalNavItem> childrenVerticalNavItems =
				_getVerticalNavItems(childPanelCategory);

			if (childrenVerticalNavItems.isEmpty()) {
				continue;
			}

			String childPanelCategoryKey = childPanelCategory.getKey();

			VerticalNavItem verticalNavItem = new VerticalNavItem();

			verticalNavItem.setExpanded(
				_panelCategoryHelper.containsPortlet(
					_portletId, childPanelCategoryKey));
			verticalNavItem.setId(childPanelCategoryKey);
			verticalNavItem.setItems(childrenVerticalNavItems);
			verticalNavItem.setLabel(
				childPanelCategory.getLabel(_themeDisplay.getLocale()));

			verticalNavItems.add(verticalNavItem);
		}

		return verticalNavItems;
	}

	public boolean isVisible() {
		String state = SessionClicks.get(
			_httpServletRequest, _VISIBLE_SESSION_KEY, "visible");

		return state.equals("visible");
	}

	private PanelCategory _getActivePanelCategory(
		PanelCategoryHelper panelCategoryHelper, String parentKey,
		String portletId, ThemeDisplay themeDisplay) {

		for (PanelCategory childPanelCategory :
			panelCategoryHelper.getChildPanelCategories(
				parentKey, themeDisplay)) {

			if (panelCategoryHelper.containsPortlet(
				portletId, childPanelCategory.getKey())) {

				return childPanelCategory;
			}
		}

		return null;
	}

	private String _getExpandedKeysSessionKey() {
		return String.format(
			"com_liferay_application_list_taglib_SideNavigationExpanded_%sKeys",
			_panelCategory.getKey());
	}

	private List<Map<String, Object>> _getPropsItems() throws Exception {
		List<Map<String, Object>> propsItems = new ArrayList<>();

		propsItems.addAll(_getPropsItems(_panelCategory));

		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					_panelCategory.getKey(), _themeDisplay)) {

			List<Map<String, Object>> childrenPropsItems = _getPropsItems(
				childPanelCategory);

			if (childrenPropsItems.isEmpty()) {
				continue;
			}

			propsItems.add(
				HashMapBuilder.<String, Object>put(
					"id", childPanelCategory.getKey()
				).put(
					"items", childrenPropsItems
				).put(
					"label",
					childPanelCategory.getLabel(_themeDisplay.getLocale())
				).build());
		}

		return propsItems;
	}

	private List<Map<String, Object>> _getPropsItems(
			PanelCategory panelCategory)
		throws Exception {

		List<Map<String, Object>> propsItems = new ArrayList<>();

		for (PanelApp panelApp :
				_panelAppRegistry.getPanelApps(
					panelCategory.getKey(),
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroup())) {

			propsItems.add(
				HashMapBuilder.<String, Object>put(
					"href",
					panelApp.getPortletURL(
						_httpServletRequest
					).toString()
				).put(
					"id", panelApp.getPortletId()
				).put(
					"label", panelApp.getLabel(_themeDisplay.getLocale())
				).build());
		}

		return propsItems;
	}

	private List<VerticalNavItem> _getVerticalNavItems(
			PanelCategory panelCategory)
		throws Exception {

		VerticalNavItemList verticalNavItems = new VerticalNavItemList();

		for (PanelApp panelApp :
				_panelAppRegistry.getPanelApps(
					panelCategory.getKey(),
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroup())) {

			VerticalNavItem verticalNavItem = new VerticalNavItem();

			verticalNavItem.setHref(
				panelApp.getPortletURL(_httpServletRequest));
			verticalNavItem.setId(panelApp.getPortletId());
			verticalNavItem.setLabel(
				panelApp.getLabel(_themeDisplay.getLocale()));

			verticalNavItems.add(verticalNavItem);
		}

		return verticalNavItems;
	}

	private static final String _VISIBLE_SESSION_KEY =
		"com_liferay_application_list_taglib_SideNavigationState";

	private static final Snapshot<ItemSelector> _itemSelectorSnapshot =
		new Snapshot<>(SideNavigationDisplayContext.class, ItemSelector.class);

	private final HttpServletRequest _httpServletRequest;
	private final PanelAppRegistry _panelAppRegistry;
	private final PanelCategory _panelCategory;
	private final PanelCategoryHelper _panelCategoryHelper;
	private final String _portletId;
	private final ThemeDisplay _themeDisplay;

}