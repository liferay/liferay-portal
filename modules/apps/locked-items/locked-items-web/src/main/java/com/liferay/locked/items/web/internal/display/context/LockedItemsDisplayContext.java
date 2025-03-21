/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.locked.items.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.locked.items.renderer.LockedItemsRenderer;
import com.liferay.locked.items.renderer.LockedItemsRendererRegistry;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Marco Galluzzi
 */
public class LockedItemsDisplayContext {

	public LockedItemsDisplayContext(
		HttpServletRequest httpServletRequest,
		LockedItemsRendererRegistry lockedItemsRendererRegistry,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_lockedItemsRendererRegistry = lockedItemsRendererRegistry;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getDescription(Locale locale) {
		LockedItemsRenderer lockedItemsRenderer = _getLockedItemsRenderer();

		return lockedItemsRenderer.getDescription(locale);
	}

	public String getName(Locale locale) {
		LockedItemsRenderer lockedItemsRenderer = _getLockedItemsRenderer();

		return lockedItemsRenderer.getName(locale);
	}

	public String getNavigation() {
		if (Validator.isNotNull(_navigation)) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "layouts");

		return _navigation;
	}

	public VerticalNavItemList getVerticalNavItemList() {
		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (LockedItemsRenderer lockedItemsRenderer :
				_lockedItemsRendererRegistry.getLockedItemsRenderers()) {

			verticalNavItemList.add(
				_getVerticalNavItemUnsafeConsumer(
					lockedItemsRenderer.getKey(),
					lockedItemsRenderer.getName(_themeDisplay.getLocale())));
		}

		return verticalNavItemList;
	}

	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		LockedItemsRenderer lockedItemsRenderer = _getLockedItemsRenderer();

		lockedItemsRenderer.render(httpServletRequest, httpServletResponse);
	}

	private LockedItemsRenderer _getLockedItemsRenderer() {
		if (_lockedItemsRenderer != null) {
			return _lockedItemsRenderer;
		}

		_lockedItemsRenderer =
			_lockedItemsRendererRegistry.getLockedItemsRenderer(
				getNavigation());

		return _lockedItemsRenderer;
	}

	private UnsafeConsumer<VerticalNavItem, Exception>
		_getVerticalNavItemUnsafeConsumer(String key, String name) {

		return verticalNavItem -> {
			verticalNavItem.setActive(Objects.equals(getNavigation(), key));
			verticalNavItem.setHref(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCPath(
					"/view.jsp"
				).setNavigation(
					key
				).buildString());
			verticalNavItem.setId(name);
			verticalNavItem.setLabel(name);
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private LockedItemsRenderer _lockedItemsRenderer;
	private final LockedItemsRendererRegistry _lockedItemsRendererRegistry;
	private String _navigation;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}