/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal.renderer;

import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.item.selector.web.internal.constants.SitesItemSelectorWebKeys;
import com.liferay.site.item.selector.web.internal.display.context.MySitesItemSelectorViewDisplayContext;
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
public class MyGroupItemSelectorViewRendererUtil {

	public static void renderHTML(
			GroupURLProvider groupURLProvider, ServletContext servletContext,
			ServletRequest servletRequest, ServletResponse servletResponse,
			GroupItemSelectorCriterion t, PortletURL portletURL,
			String itemSelectedEventName)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/view_sites.jsp");

		servletRequest.setAttribute(
			SiteWebKeys.GROUP_URL_PROVIDER, groupURLProvider);

		MySitesItemSelectorViewDisplayContext
			mySitesItemSelectorViewDisplayContext =
				new MySitesItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest, t,
					itemSelectedEventName, portletURL);

		servletRequest.setAttribute(
			SitesItemSelectorWebKeys.SITES_ITEM_SELECTOR_DISPLAY_CONTEXT,
			mySitesItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

}