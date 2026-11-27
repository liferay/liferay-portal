/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal.renderer;

import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.item.selector.web.internal.constants.SitesItemSelectorWebKeys;
import com.liferay.site.item.selector.web.internal.display.context.RecentSitesItemSelectorViewDisplayContext;
import com.liferay.site.manager.RecentGroupManager;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * @author Cristina González
 */
public class RecentGroupItemSelectorViewRendererUtil {

	public static void renderHTML(
			GroupURLProvider groupURLProvider,
			RecentGroupManager recentGroupManager,
			ServletContext servletContext, ServletRequest servletRequest,
			ServletResponse servletResponse,
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/view_sites.jsp");

		servletRequest.setAttribute(
			SiteWebKeys.GROUP_URL_PROVIDER, groupURLProvider);

		RecentSitesItemSelectorViewDisplayContext
			siteItemSelectorViewDisplayContext =
				new RecentSitesItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest,
					groupItemSelectorCriterion, itemSelectedEventName,
					portletURL, recentGroupManager);

		servletRequest.setAttribute(
			SitesItemSelectorWebKeys.SITES_ITEM_SELECTOR_DISPLAY_CONTEXT,
			siteItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

}