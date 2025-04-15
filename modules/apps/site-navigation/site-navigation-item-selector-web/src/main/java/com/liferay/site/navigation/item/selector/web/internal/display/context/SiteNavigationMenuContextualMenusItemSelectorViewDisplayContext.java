/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.item.selector.web.internal.display.context;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemSelectorReturnType;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Víctor Galán
 */
public class SiteNavigationMenuContextualMenusItemSelectorViewDisplayContext {

	public SiteNavigationMenuContextualMenusItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
	}

	public Map<String, Object> getContext(
		LiferayPortletResponse liferayPortletResponse) {

		return HashMapBuilder.<String, Object>put(
			"buttonClass", ".contextual-menu-selector"
		).put(
			"containerId",
			liferayPortletResponse.getNamespace() + "contextualMenuSelector"
		).put(
			"eventName", getItemSelectedEventName()
		).put(
			"returnType",
			SiteNavigationMenuItemSelectorReturnType.class.toString()
		).build();
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public JSONArray getLevelsJSONArray() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.putAll(
			JSONUtil.put(
				"description",
				LanguageUtil.get(
					themeDisplay.getLocale(), "children-description")
			).put(
				"imageURL", _getImageURL("children.svg")
			).put(
				"title", LanguageUtil.get(themeDisplay.getLocale(), "children")
			).put(
				"value", "children"
			),
			JSONUtil.put(
				"description",
				LanguageUtil.get(
					themeDisplay.getLocale(), "self-and-siblings-description")
			).put(
				"imageURL", _getImageURL("self_and_siblings.svg")
			).put(
				"title",
				LanguageUtil.get(themeDisplay.getLocale(), "self-and-siblings")
			).put(
				"value", "self-and-siblings"
			),
			JSONUtil.put(
				"description",
				LanguageUtil.get(
					themeDisplay.getLocale(),
					"parent-and-its-siblings-description")
			).put(
				"imageURL", _getImageURL("parent_and_its_siblings.svg")
			).put(
				"title",
				LanguageUtil.get(
					themeDisplay.getLocale(), "parent-and-its-siblings")
			).put(
				"value", "parent-and-its-siblings"
			));
	}

	private String _getImageURL(String imageName) {
		return PortalUtil.getStaticResourceURL(
			_httpServletRequest,
			PortalUtil.getPathModule() +
				"/site-navigation-item-selector-web/images/" + imageName);
	}

	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;

}