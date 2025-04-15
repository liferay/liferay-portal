/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class ViewMembershipRequestsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewMembershipRequestsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		ViewMembershipRequestsDisplayContext
			viewMembershipRequestsDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			viewMembershipRequestsDisplayContext.
				getSiteMembershipSearchContainer());

		_viewMembershipRequestsDisplayContext =
			viewMembershipRequestsDisplayContext;
	}

	@Override
	public String getComponentId() {
		return "siteAdminWebManagementToolbar";
	}

	@Override
	public String getSearchContainerId() {
		return "sites";
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String getDisplayStyle() {
		return _viewMembershipRequestsDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"date"};
	}

	private final ViewMembershipRequestsDisplayContext
		_viewMembershipRequestsDisplayContext;

}