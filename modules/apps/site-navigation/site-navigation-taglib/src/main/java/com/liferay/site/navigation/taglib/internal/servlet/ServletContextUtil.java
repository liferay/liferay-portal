/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.internal.servlet;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.servlet.ServletContext;

/**
 * @author Michael Bradford
 */
public class ServletContextUtil {

	public static PortletDisplayTemplate getPortletDisplayTemplate() {
		return _portletDisplayTemplateSnapshot.get();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	public static SiteNavigationMenuItemType getSiteNavigationMenuItemType(
		String type) {

		SiteNavigationMenuItemTypeRegistry siteNavigationMenuItemTypeRegistry =
			_siteNavigationMenuItemTypeRegistrySnapshot.get();

		return siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
			type);
	}

	private static final Snapshot<PortletDisplayTemplate>
		_portletDisplayTemplateSnapshot = new Snapshot<>(
			ServletContextUtil.class, PortletDisplayTemplate.class);
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.site.navigation.taglib)");
	private static final Snapshot<SiteNavigationMenuItemTypeRegistry>
		_siteNavigationMenuItemTypeRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, SiteNavigationMenuItemTypeRegistry.class);

}