/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jose Luis Navarro
 */
public class MCPServerWebNavigationDisplayContext {

	public MCPServerWebNavigationDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public List<NavigationItem> getNavigationItems() {
		String mvcRenderCommandName = ParamUtil.getString(
			_httpServletRequest, "mvcRenderCommandName");

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					mvcRenderCommandName.equals("/mcp_server/view_profiles"));
				navigationItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/mcp_server/view_profiles"
					).buildString());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "profiles"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					mvcRenderCommandName.equals("/mcp_server/view_prompts"));
				navigationItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/mcp_server/view_prompts"
					).buildString());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "prompts"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					mvcRenderCommandName.isEmpty() ||
					mvcRenderCommandName.equals("/mcp_server/edit_data_mask"));
				navigationItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).buildString());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "data-masks"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}