/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class AppManagerDisplayContext {

	public AppManagerDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public List<NavigationItem> getModuleNavigationItems() {
		String pluginType = ParamUtil.getString(
			_httpServletRequest, "pluginType", "components");

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(pluginType.equals("components"));
				navigationItem.setHref(_getViewModuleURL("components"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "components"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(pluginType.equals("portlets"));
				navigationItem.setHref(_getViewModuleURL("portlets"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "portlets"));
			}
		).build();
	}

	private String _getViewModuleURL(String pluginType) {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_module.jsp"
		).setParameter(
			"app", ParamUtil.getString(_httpServletRequest, "app")
		).setParameter(
			"moduleGroup",
			ParamUtil.getString(_httpServletRequest, "moduleGroup")
		).setParameter(
			"pluginType", pluginType
		).setParameter(
			"symbolicName",
			ParamUtil.getString(_httpServletRequest, "symbolicName")
		).setParameter(
			"version", ParamUtil.getString(_httpServletRequest, "version")
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}