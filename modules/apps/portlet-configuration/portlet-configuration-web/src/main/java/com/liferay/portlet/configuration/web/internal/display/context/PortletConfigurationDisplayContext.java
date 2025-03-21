/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class PortletConfigurationDisplayContext {

	public PortletConfigurationDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public List<NavigationItem> getNavigationItems() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), _getPortletResource());

		return NavigationItemListBuilder.add(
			() -> portlet.getConfigurationActionInstance() != null,
			navigationItem -> {
				navigationItem.setActive(Objects.equals(getTabs1(), "setup"));
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_configuration.jsp", "redirect", _getRedirect(),
					"returnToFullPageURL", _getReturnToFullPageURL(),
					"portletConfiguration", Boolean.TRUE.toString(),
					"portletResource", _getPortletResource());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "setup"));
			}
		).add(
			() ->
				FeatureFlagManagerUtil.isEnabled("LPD-40533") &&
				portlet.hasMultipleMimeTypes(),
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "supported-clients"));
				navigationItem.setDeprecated(true);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_supported_clients.jsp", "redirect", _getRedirect(),
					"returnToFullPageURL", _getReturnToFullPageURL(),
					"portletConfiguration", Boolean.TRUE.toString(),
					"portletResource", _getPortletResource());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "supported-clients"));
			}
		).add(
			() -> SetUtil.isNotEmpty(portlet.getPublicRenderParameters()),
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "communication"));
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_public_render_parameters.jsp", "redirect",
					_getRedirect(), "returnToFullPageURL",
					_getReturnToFullPageURL(), "portletConfiguration",
					Boolean.TRUE.toString(), "portletResource",
					_getPortletResource());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "communication"));
			}
		).add(
			() -> FeatureFlagManagerUtil.isEnabled("LPD-40534"),
			navigationItem -> {
				navigationItem.setActive(Objects.equals(getTabs1(), "sharing"));
				navigationItem.setDeprecated(true);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_sharing.jsp", "redirect", _getRedirect(),
					"returnToFullPageURL", _getReturnToFullPageURL(),
					"portletConfiguration", Boolean.TRUE.toString(),
					"portletResource", _getPortletResource());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "sharing"));
			}
		).add(
			() ->
				FeatureFlagManagerUtil.isEnabled("LPD-11131") &&
				portlet.isScopeable(),
			navigationItem -> {
				navigationItem.setActive(Objects.equals(getTabs1(), "scope"));
				navigationItem.setDeprecated(true);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_scope.jsp", "redirect", _getRedirect(),
					"returnToFullPageURL", _getReturnToFullPageURL(),
					"portletConfiguration", Boolean.TRUE.toString(),
					"portletResource", _getPortletResource());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "scope"));
			}
		).build();
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_httpServletRequest, "tabs1");

		return _tabs1;
	}

	private String _getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	private String _getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	private String _getReturnToFullPageURL() {
		if (_returnToFullPageURL != null) {
			return _returnToFullPageURL;
		}

		_returnToFullPageURL = ParamUtil.getString(
			_httpServletRequest, "returnToFullPageURL");

		return _returnToFullPageURL;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _portletResource;
	private String _redirect;
	private final RenderResponse _renderResponse;
	private String _returnToFullPageURL;
	private String _tabs1;

}