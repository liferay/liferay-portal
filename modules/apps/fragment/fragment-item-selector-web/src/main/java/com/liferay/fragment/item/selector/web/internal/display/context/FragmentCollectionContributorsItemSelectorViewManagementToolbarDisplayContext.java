/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Víctor Galán
 */
public class
	FragmentCollectionContributorsItemSelectorViewManagementToolbarDisplayContext
		extends SearchContainerManagementToolbarDisplayContext {

	public FragmentCollectionContributorsItemSelectorViewManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	@Override
	public Boolean isDisabled() {
		return true;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String getDisplayStyle() {
		return "icon";
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"icon"};
	}

}