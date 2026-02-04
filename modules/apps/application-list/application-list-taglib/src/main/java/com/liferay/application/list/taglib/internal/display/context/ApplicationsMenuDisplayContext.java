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
import com.liferay.application.list.util.PanelCategoryRegistryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.IconItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Thiago Buarque
 */
public class ApplicationsMenuDisplayContext {

	public ApplicationsMenuDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_panelAppRegistry = (PanelAppRegistry)httpServletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);

		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_portletId = _themeDisplay.getPpid();
	}

	public PanelCategory getPanelCategory() {
		List<PanelCategory> childPanelCategories =
			PanelCategoryRegistryUtil.getChildPanelCategories(
				PanelCategoryKeys.APPLICATIONS_MENU);

		for (PanelCategory panelCategory : childPanelCategories) {
			if (_panelCategoryHelper.containsPortlet(
					_portletId, panelCategory.getKey())) {

				return panelCategory;
			}
		}

		return null;
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
			"portletId", _portletId
		).put(
			"visibility",
			HashMapBuilder.<String, Object>put(
				"hidden", _STATE_HIDDEN
			).put(
				"initialState", isVisible()
			).put(
				"stateKey", _STATE_KEY
			).put(
				"visible", _STATE_VISIBLE
			).build()
		).build();
	}

	public List<VerticalNavItem> getVerticalNavItems() throws Exception {
		List<VerticalNavItem> verticalNavItems = new ArrayList<>();

		PanelCategory panelCategory = getPanelCategory();

		VerticalNavItem homeVerticalNavItem = new VerticalNavItem();

		// TODO: add Href and update to leadingIcon when it's merged

		homeVerticalNavItem.addIcon(IconItem.of("home", null));
		homeVerticalNavItem.setId(panelCategory.getKey());
		homeVerticalNavItem.setLabel(
			LanguageUtil.get(_httpServletRequest, "home"));

		verticalNavItems.add(homeVerticalNavItem);

		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					panelCategory.getKey(), _themeDisplay)) {

			List<VerticalNavItem> childrenVerticalNavItems =
				_getVerticalNavItems(childPanelCategory);

			if (childrenVerticalNavItems.isEmpty()) {
				continue;
			}

			VerticalNavItem verticalNavItem = new VerticalNavItem();

			verticalNavItem.setId(childPanelCategory.getKey());
			verticalNavItem.setLabel(
				childPanelCategory.getLabel(_themeDisplay.getLocale()));
			verticalNavItem.setItems(childrenVerticalNavItems);
			verticalNavItem.setExpanded(
				_panelCategoryHelper.containsPortlet(
					_portletId, childPanelCategory.getKey()));

			verticalNavItems.add(verticalNavItem);
		}

		return verticalNavItems;
	}

	public boolean isVisible() {
		String state = SessionClicks.get(
			_httpServletRequest, _STATE_KEY, _STATE_DEFAULT);

		return state.equals(_STATE_VISIBLE);
	}

	private List<Map<String, Object>> _getPropsItems() throws Exception {
		List<Map<String, Object>> propsItems = new ArrayList<>();

		PanelCategory panelCategory = getPanelCategory();

		propsItems.add(HashMapBuilder.<String, Object>put(
			"href", "/" // TODO: add actual href
		).put(
			"id", panelCategory.getKey()
		).put(
			"label", LanguageUtil.get(_httpServletRequest, "home")
		).put(
			"leadingIcon", "home"
		).build());

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

			// TODO: use leadingIcon when it gets merged

			verticalNavItem.addIcon(IconItem.of("books", null));
			verticalNavItem.setId(panelApp.getPortletId());
			verticalNavItem.setLabel(
				panelApp.getLabel(_themeDisplay.getLocale()));
			verticalNavItem.setHref(
				panelApp.getPortletURL(_httpServletRequest));

			verticalNavItems.add(verticalNavItem);
		}

		return verticalNavItems;
	}

	private static final String _STATE_DEFAULT =
		ApplicationsMenuDisplayContext._STATE_VISIBLE;

	private static final String _STATE_HIDDEN = "hidden";

	private static final String _STATE_KEY =
		"com_liferay_application_list_taglib_ApplicationsMenuSideMenuState";

	private static final String _STATE_VISIBLE = "visible";

	private final HttpServletRequest _httpServletRequest;
	private final PanelAppRegistry _panelAppRegistry;
	private final PanelCategoryHelper _panelCategoryHelper;
	private final String _portletId;
	private final ThemeDisplay _themeDisplay;

}