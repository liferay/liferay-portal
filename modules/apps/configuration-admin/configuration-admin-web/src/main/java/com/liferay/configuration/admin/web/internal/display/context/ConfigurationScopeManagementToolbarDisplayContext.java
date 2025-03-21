/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.BaseManagementToolbarDisplayContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class ConfigurationScopeManagementToolbarDisplayContext
	extends BaseManagementToolbarDisplayContext {

	public ConfigurationScopeManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, int total) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);

		_total = total;
	}

	@Override
	public String getClearResultsURL() {
		return String.valueOf(liferayPortletResponse.createRenderURL());
	}

	@Override
	public int getItemsTotal() {
		return _total;
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/configuration_admin/search_results"
		).setRedirect(
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).buildString()
		).buildString();
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	private final int _total;

}