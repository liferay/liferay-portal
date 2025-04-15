/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletModeFactory_IW {
	public static PortletModeFactory_IW getInstance() {
		return _instance;
	}

	public jakarta.portlet.PortletMode getPortletMode(java.lang.String name) {
		return PortletModeFactory.getPortletMode(name);
	}

	public jakarta.portlet.PortletMode getPortletMode(java.lang.String name,
		int portletMajorVersion) {
		return PortletModeFactory.getPortletMode(name, portletMajorVersion);
	}

	private PortletModeFactory_IW() {
	}

	private static PortletModeFactory_IW _instance = new PortletModeFactory_IW();
}