/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal.renderer;

import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.item.selector.display.context.SitesItemSelectorViewDisplayContext;
import com.liferay.site.item.selector.renderer.SiteItemSelectorViewRenderer;
import com.liferay.site.item.selector.web.internal.constants.SitesItemSelectorWebKeys;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = SiteItemSelectorViewRenderer.class)
public class SiteItemSelectorViewRendererImpl
	implements SiteItemSelectorViewRenderer {

	@Override
	public void renderHTML(
			SitesItemSelectorViewDisplayContext
				sitesItemSelectorViewDisplayContext)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			sitesItemSelectorViewDisplayContext.getPortletRequest());

		httpServletRequest.setAttribute(
			SitesItemSelectorWebKeys.SITES_ITEM_SELECTOR_DISPLAY_CONTEXT,
			sitesItemSelectorViewDisplayContext);
		httpServletRequest.setAttribute(
			SiteWebKeys.GROUP_URL_PROVIDER, _groupURLProvider);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/view_sites.jsp");

		requestDispatcher.include(
			httpServletRequest,
			_portal.getHttpServletResponse(
				sitesItemSelectorViewDisplayContext.getPortletResponse()));
	}

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.item.selector.web)"
	)
	private ServletContext _servletContext;

}