/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.health.status.web.internal.display.context;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.health.status.CommerceHealthStatusRegistry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceHealthStatusDisplayContext {

	public CommerceHealthStatusDisplayContext(
		CommerceHealthStatusRegistry commerceHealthStatusRegistry,
		PortletResourcePermission portletResourcePermission,
		RenderRequest renderRequest, RenderResponse renderResponse, int type) {

		_commerceHealthStatusRegistry = commerceHealthStatusRegistry;
		_portletResourcePermission = portletResourcePermission;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_type = type;
	}

	public List<CommerceHealthStatus> getCommerceHealthStatuses() {
		return _commerceHealthStatusRegistry.getActiveCommerceHealthStatuses(
			_type);
	}

	public SearchContainer<CommerceHealthStatus> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_renderRequest, _renderResponse.createRenderURL(), null,
			"there-are-no-results");

		List<CommerceHealthStatus> results = getCommerceHealthStatuses();

		_searchContainer.setResultsAndTotal(() -> results, results.size());

		return _searchContainer;
	}

	public boolean hasManageCommerceHealthStatusPermission() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CommerceActionKeys.MANAGE_COMMERCE_HEALTH_STATUS);
	}

	private final CommerceHealthStatusRegistry _commerceHealthStatusRegistry;
	private final PortletResourcePermission _portletResourcePermission;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<CommerceHealthStatus> _searchContainer;
	private final int _type;

}