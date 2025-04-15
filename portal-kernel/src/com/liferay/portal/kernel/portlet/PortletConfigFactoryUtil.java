/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.ServletContext;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletConfigFactoryUtil {

	public static PortletConfig create(
		Portlet portlet, ServletContext servletContext) {

		return _portletConfigFactory.create(portlet, servletContext);
	}

	public static void destroy(Portlet portlet) {
		_portletConfigFactory.destroy(portlet);
	}

	public static PortletConfig get(Portlet portlet) {
		return _portletConfigFactory.get(portlet);
	}

	public static PortletConfig get(String portletId) {
		return _portletConfigFactory.get(portletId);
	}

	public static PortletConfigFactory getPortletConfigFactory() {
		return _portletConfigFactory;
	}

	public static PortletConfig update(Portlet portlet) {
		return _portletConfigFactory.update(portlet);
	}

	public void setPortletConfigFactory(
		PortletConfigFactory portletConfigFactory) {

		_portletConfigFactory = portletConfigFactory;
	}

	private static PortletConfigFactory _portletConfigFactory;

}