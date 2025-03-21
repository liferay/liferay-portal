/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.constants.WikiPortletKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Bárbara Cabrera
 */
public class WikiConfigurationDisplayContext {

	public WikiConfigurationDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				WikiPortletKeys.WIKI_ADMIN, _themeDisplay.getScopeGroup(), 0, 0)
		).buildString();
	}

	public String getNavigation() {
		if (Validator.isNotNull(_navigation)) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_renderRequest, "navigation", "email-from");

		if (PortalUtil.isRSSFeedsEnabled()) {
			_navigation = ParamUtil.getString(
				_renderRequest, "navigation", "rss");
		}

		return _navigation;
	}

	public VerticalNavItemList getNotificationsVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			_getVerticalNavItemUnsafeConsumer("email-from")
		).add(
			_getVerticalNavItemUnsafeConsumer("page-added-email")
		).add(
			_getVerticalNavItemUnsafeConsumer("page-updated-email")
		).build();
	}

	public PortletURL getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setActionName(
			"editConfiguration"
		).setMVCPath(
			"/edit_configuration.jsp"
		).setPortletResource(
			ParamUtil.getString(_httpServletRequest, "portletResource")
		).setParameter(
			"portletConfiguration", Boolean.TRUE
		).setParameter(
			"settingsScope",
			PortletPreferencesFactoryConstants.SETTINGS_SCOPE_PORTLET_INSTANCE
		).buildPortletURL();

		return _portletURL;
	}

	public PortletURL getRedirect() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setNavigation(
			getNavigation()
		).buildPortletURL();
	}

	public VerticalNavItemList getSettingsVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			_getVerticalNavItemUnsafeConsumer("rss")
		).build();
	}

	public String getSubtitle() {
		if (Objects.equals(getNavigation(), "rss")) {
			return LanguageUtil.get(_httpServletRequest, "rss-subscription");
		}

		return LanguageUtil.get(_httpServletRequest, "email");
	}

	public String getTitle() {
		return LanguageUtil.get(_httpServletRequest, getNavigation());
	}

	private UnsafeConsumer<VerticalNavItem, Exception>
		_getVerticalNavItemUnsafeConsumer(String key) {

		return verticalNavItem -> {
			verticalNavItem.setActive(Objects.equals(getNavigation(), key));
			verticalNavItem.setHref(
				PortletURLBuilder.create(
					getPortletURL()
				).setNavigation(
					key
				).buildString());

			String name = LanguageUtil.get(_httpServletRequest, key);

			verticalNavItem.setId(name);
			verticalNavItem.setLabel(name);
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private String _navigation;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}