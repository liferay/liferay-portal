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
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
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
		this(
			httpServletRequest,
			(PanelAppRegistry)httpServletRequest.getAttribute(
				ApplicationListWebKeys.PANEL_APP_REGISTRY));
	}

	public SideNavigationDisplayContext(
		HttpServletRequest httpServletRequest,
		PanelAppRegistry panelAppRegistry) {

		_httpServletRequest = httpServletRequest;
		_panelAppRegistry = panelAppRegistry;

		_panelCategoryHelper = new PanelCategoryHelper(panelAppRegistry);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_portletId = _themeDisplay.getPpid();

		_panelCategory = _panelCategoryHelper.getActivePanelCategory(
			PanelCategoryKeys.APPLICATIONS_MENU, _portletId, _themeDisplay);
	}

	public Map<String, Object> getProps() throws Exception {
		if (_panelCategory == null) {
			return Collections.emptyMap();
		}

		String itemSelectedEventName = String.format(
			"_%s_selectSite",
			ProductNavigationProductMenuPortletKeys.
				PRODUCT_NAVIGATION_PRODUCT_MENU);

		return HashMapBuilder.<String, Object>put(
			"canonicalName", _panelCategory.getLabel(LocaleUtil.ENGLISH)
		).put(
			"categoryImageUrl",
			String.format(
				"%s/product_icons/%s_sm.svg",
				_themeDisplay.getPathThemeImages(), _panelCategory.getKey())
		).put(
			"colorScheme", _getColorScheme()
		).put(
			"colorSchemeSessionKey", _COLOR_SCHEME_SESSION_KEY
		).put(
			"expandedKeys", _getExpandedKeys()
		).put(
			"expandedKeysSessionKey", _getExpandedKeysSessionKey()
		).put(
			"items", _getPropsItems()
		).put(
			"label", _panelCategory.getLabel(_themeDisplay.getLocale())
		).put(
			"navigationItemsURL",
			() -> {
				LiferayPortletURL navigationItemsURL =
					(LiferayPortletURL)
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest
						).createResourceURL(
							ProductNavigationProductMenuPortletKeys.
								PRODUCT_NAVIGATION_PRODUCT_MENU
						);

				navigationItemsURL.setCopyCurrentRenderParameters(false);
				navigationItemsURL.setParameter(
					"p_v_l_s_g_id",
					String.valueOf(_themeDisplay.getScopeGroupId()));
				navigationItemsURL.setParameter(
					"selectedPortletId", _portletId);
				navigationItemsURL.setResourceID(
					"/product_navigation_product_menu/get_navigation_items");

				return navigationItemsURL.toString();
			}
		).put(
			"portletId", StringPool.BLANK
		).put(
			"selectedPortletId", _portletId
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

	public boolean isVisible() {
		String state = SessionClicks.get(
			_httpServletRequest, _VISIBLE_SESSION_KEY, "visible");

		return state.equals("visible");
	}

	private String _getColorScheme() {
		return SessionClicks.get(
			_httpServletRequest, _COLOR_SCHEME_SESSION_KEY, "light");
	}

	private List<String> _getExpandedKeys() {
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
					"canonicalName", panelApp.getLabel(LocaleUtil.ENGLISH)
				).put(
					"href",
					panelApp.getPortletURL(
						_httpServletRequest
					).toString()
				).put(
					"id", panelApp.getPortletId()
				).put(
					"label", panelApp.getLabel(_themeDisplay.getLocale())
				).put(
					"leadingIcon", panelApp.getIcon()
				).build());
		}

		return propsItems;
	}

	private static final String _COLOR_SCHEME_SESSION_KEY =
		"com_liferay_application_list_taglib_SideNavigationColorScheme";

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