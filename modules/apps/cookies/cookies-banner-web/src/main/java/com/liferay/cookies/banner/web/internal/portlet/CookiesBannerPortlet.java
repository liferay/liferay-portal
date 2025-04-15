/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.portlet;

import com.liferay.cookies.banner.web.internal.constants.CookiesBannerPortletKeys;
import com.liferay.cookies.banner.web.internal.constants.CookiesBannerWebKeys;
import com.liferay.cookies.banner.web.internal.display.context.CookiesBannerDisplayContext;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-cookies-banner",
		"com.liferay.portlet.header-portlet-css=/cookies_banner/css/main.css",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.show-portlet-access-denied=false",
		"com.liferay.portlet.show-portlet-inactive=false",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=false",
		"jakarta.portlet.display-name=Cookies Banner",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/cookies_banner/view.jsp",
		"jakarta.portlet.name=" + CookiesBannerPortletKeys.COOKIES_BANNER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CookiesBannerPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CookiesBannerDisplayContext cookiesBannerDisplayContext =
			new CookiesBannerDisplayContext(
				_cookiesConfigurationProvider,
				_layoutUtilityPageEntryLayoutProvider, renderRequest,
				renderResponse);

		renderRequest.setAttribute(
			CookiesBannerWebKeys.COOKIES_BANNER_DISPLAY_CONTEXT,
			cookiesBannerDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

}