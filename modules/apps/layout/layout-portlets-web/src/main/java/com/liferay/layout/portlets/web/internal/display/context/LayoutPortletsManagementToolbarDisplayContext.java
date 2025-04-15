/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.portlets.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jorge Ferrer
 */
public class LayoutPortletsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public LayoutPortletsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		LayoutPortletsDisplayContext layoutPortletsDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			layoutPortletsDisplayContext.getSearchContainer());
	}

	@Override
	public String getComponentId() {
		return "layoutPortletsManagementToolbar";
	}

	@Override
	public String getSearchContainerId() {
		return "layoutPortlets";
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name"};
	}

}