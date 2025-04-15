/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.portlet;

import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-navigation",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.display-category=category.highlighted",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/navigation.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Navigation",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SiteNavigationMenuPortlet extends MVCPortlet {

	@Activate
	protected void activate() {
		_portletRegistry.registerAlias(
			_ALIAS, SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_TEMPLATE, _portletDisplayTemplate);

		super.doDispatch(renderRequest, renderResponse);
	}

	private static final String _ALIAS = "nav";

	@Reference
	private PortletDisplayTemplate _portletDisplayTemplate;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.site.navigation.menu.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}