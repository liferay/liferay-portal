/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.site.item.selector.display.context.SitesItemSelectorViewDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SitesItemSelectorViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public SitesItemSelectorViewManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			SitesItemSelectorViewDisplayContext
				sitesItemSelectorViewDisplayContext)
		throws Exception {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			sitesItemSelectorViewDisplayContext.getGroupSearch());

		_sitesItemSelectorViewDisplayContext =
			sitesItemSelectorViewDisplayContext;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getSortingOrder() {
		if (_sitesItemSelectorViewDisplayContext.isShowSortFilter()) {
			return super.getSortingOrder();
		}

		return null;
	}

	@Override
	public String getSortingURL() {
		if (_sitesItemSelectorViewDisplayContext.isShowSortFilter()) {
			return super.getSortingURL();
		}

		return null;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	public Boolean isShowSearch() {
		return _sitesItemSelectorViewDisplayContext.isShowSearch();
	}

	@Override
	protected String getDefaultDisplayStyle() {
		return "icon";
	}

	@Override
	protected String getDisplayStyle() {
		return _sitesItemSelectorViewDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getOrderByKeys() {
		if (_sitesItemSelectorViewDisplayContext.isShowSortFilter()) {
			return new String[] {"name", "type"};
		}

		return null;
	}

	private final SitesItemSelectorViewDisplayContext
		_sitesItemSelectorViewDisplayContext;

}