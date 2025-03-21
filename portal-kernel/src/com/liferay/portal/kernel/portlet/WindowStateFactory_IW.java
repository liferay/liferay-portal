/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

/**
 * @author Brian Wing Shun Chan
 */
public class WindowStateFactory_IW {
	public static WindowStateFactory_IW getInstance() {
		return _instance;
	}

	public jakarta.portlet.WindowState getWindowState(java.lang.String name) {
		return WindowStateFactory.getWindowState(name);
	}

	public jakarta.portlet.WindowState getWindowState(java.lang.String name,
		int portletMajorVersion) {
		return WindowStateFactory.getWindowState(name, portletMajorVersion);
	}

	private WindowStateFactory_IW() {
	}

	private static WindowStateFactory_IW _instance = new WindowStateFactory_IW();
}