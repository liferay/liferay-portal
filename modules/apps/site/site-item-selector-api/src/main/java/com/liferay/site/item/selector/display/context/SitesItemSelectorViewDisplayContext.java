/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.display.context;

import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

/**
 * @author Julio Camarero
 */
public interface SitesItemSelectorViewDisplayContext {

	public String getDisplayStyle();

	public default GroupItemSelectorCriterion getGroupItemSelectorCriterion() {
		return new SiteItemSelectorCriterion();
	}

	public String getGroupName(Group group) throws PortalException;

	public GroupSearch getGroupSearch() throws Exception;

	public String getItemSelectedEventName();

	public PortletRequest getPortletRequest();

	public PortletResponse getPortletResponse();

	public PortletURL getPortletURL() throws PortletException;

	public boolean isShowChildSitesLink();

	public boolean isShowSearch();

	public boolean isShowSortFilter();

}