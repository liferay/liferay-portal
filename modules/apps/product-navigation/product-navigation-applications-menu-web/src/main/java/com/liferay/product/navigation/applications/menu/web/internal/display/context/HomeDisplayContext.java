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
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	}

	public PanelCategory getPanelCategory() {
		return _getActivePanelCategory(PanelCategoryKeys.APPLICATIONS_MENU);
	}

	public String getPanelCategoryLabel() {
		PanelCategory panelCategory = getPanelCategory();

		if (panelCategory == null) {
			return null;
		}

		return panelCategory.getLabel(_themeDisplay.getLocale());
	}

	public String getPortletId() {
		return _portletId;
	}

	public Map<String, Object> getProps() throws Exception {
		PanelCategory panelCategory = getPanelCategory();

		if (panelCategory == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"items", _getPropsItems()
		).put(
			"label", getPanelCategoryLabel()
		).put(
			"logo",
			String.format(
				"%s/product_icons/%s.svg", _themeDisplay.getPathThemeImages(),
				panelCategory.getKey())
		).put(
			"portletId", _portletId
		).build();
	}

	private PanelCategory _getActivePanelCategory(String parentKey) {
		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					parentKey, _themeDisplay)) {

			if (_panelCategoryHelper.containsPortlet(
					_portletId, childPanelCategory.getKey())) {

				return childPanelCategory;
			}
		}

		return null;
	}

	private List<Map<String, Object>> _getPropsItems() throws Exception {
		List<Map<String, Object>> propsItems = new ArrayList<>();

		PanelCategory panelCategory = getPanelCategory();

		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					panelCategory.getKey(), _themeDisplay)) {

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
				).put(
					"leadingIcon", "home"
				).build());
		}

		return propsItems;
	}

	private final HttpServletRequest _httpServletRequest;
	private final PanelAppRegistry _panelAppRegistry;
	private final PanelCategoryHelper _panelCategoryHelper;
	private final String _portletId;
	private final ThemeDisplay _themeDisplay;

}