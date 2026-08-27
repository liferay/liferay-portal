/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mario Leandro
 */
public class HomeDisplayContext {

	public HomeDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_panelAppRegistry = (PanelAppRegistry)httpServletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);

		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);

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

		return HashMapBuilder.<String, Object>put(
			"icon",
			String.format(
				"%s/product_icons/%s.svg", _themeDisplay.getPathThemeImages(),
				_panelCategory.getKey())
		).put(
			"items", _getPropsItems()
		).put(
			"portletId", _portletId
		).put(
			"title", _panelCategory.getLabel(_themeDisplay.getLocale())
		).build();
	}

	private void _addPropsItem(
		String id, List<Map<String, Object>> items, String label,
		List<Map<String, Object>> propsItems) {

		if (items.isEmpty()) {
			return;
		}

		propsItems.add(
			HashMapBuilder.<String, Object>put(
				"id", id
			).put(
				"items", items
			).put(
				"label", label
			).build());
	}

	private List<Map<String, Object>> _getPropsItems() throws Exception {
		List<Map<String, Object>> propsItems = new ArrayList<>();

		_addPropsItem(
			_panelCategory.getKey(),
			ListUtil.filter(
				_getPropsItems(_panelCategory),
				rootPropsItem -> !Objects.equals(
					rootPropsItem.get("id"), _portletId)),
			null, propsItems);

		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					_panelCategory.getKey(), _themeDisplay)) {

			_addPropsItem(
				childPanelCategory.getKey(), _getPropsItems(childPanelCategory),
				childPanelCategory.getLabel(_themeDisplay.getLocale()),
				propsItems);
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
				).put(
					"leadingIcon", panelApp.getIcon()
				).build());
		}

		return propsItems;
	}

	private final HttpServletRequest _httpServletRequest;
	private final PanelAppRegistry _panelAppRegistry;
	private final PanelCategory _panelCategory;
	private final PanelCategoryHelper _panelCategoryHelper;
	private final String _portletId;
	private final ThemeDisplay _themeDisplay;

}