/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Drew Brokke
 */
public class SearchContainerManagementToolbarDisplayContext
	extends BaseManagementToolbarDisplayContext {

	public SearchContainerManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);

		this.searchContainer = searchContainer;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #SearchContainerManagementToolbarDisplayContext(
	 *             HttpServletRequest, LiferayPortletRequest,
	 *             LiferayPortletResponse, SearchContainer)}
	 */
	@Deprecated
	public SearchContainerManagementToolbarDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		HttpServletRequest httpServletRequest,
		SearchContainer<?> searchContainer) {

		this(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public int getItemsTotal() {
		return searchContainer.getTotal();
	}

	@Override
	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	@Override
	public String getSearchContainerId() {
		return searchContainer.getId(httpServletRequest, getNamespace());
	}

	@Override
	public Boolean isSelectable() {
		if (getItemsTotal() == 0) {
			return false;
		}

		return true;
	}

	@Override
	protected String getOrderByCol() {
		return searchContainer.getOrderByCol();
	}

	@Override
	protected String getOrderByColParam() {
		return searchContainer.getOrderByColParam();
	}

	@Override
	protected String getOrderByType() {
		return searchContainer.getOrderByType();
	}

	@Override
	protected String getOrderByTypeParam() {
		return searchContainer.getOrderByTypeParam();
	}

	protected SearchContainer<?> searchContainer;

}