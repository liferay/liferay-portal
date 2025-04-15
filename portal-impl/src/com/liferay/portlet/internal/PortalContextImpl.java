/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.ReleaseInfo;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class PortalContextImpl implements PortalContext {

	public static List<PortletMode> portletModes =
		new ArrayList<PortletMode>() {
			{
				add(PortletMode.EDIT);
				add(PortletMode.HELP);
				add(PortletMode.VIEW);
				add(LiferayPortletMode.ABOUT);
				add(LiferayPortletMode.CONFIG);
				add(LiferayPortletMode.EDIT_DEFAULTS);
				add(LiferayPortletMode.PREVIEW);
				add(LiferayPortletMode.PRINT);
			}
		};
	public static Properties properties = new Properties() {
		{
			setProperty(MARKUP_HEAD_ELEMENT_SUPPORT, Boolean.TRUE.toString());
		}
	};
	public static List<WindowState> windowStates =
		new ArrayList<WindowState>() {
			{
				add(WindowState.MAXIMIZED);
				add(WindowState.MINIMIZED);
				add(WindowState.NORMAL);
				add(LiferayWindowState.EXCLUSIVE);
				add(LiferayWindowState.POP_UP);
			}
		};

	public static boolean isSupportedPortletMode(PortletMode portletMode) {
		return _portletModes.contains(portletMode);
	}

	public static boolean isSupportedWindowState(WindowState windowState) {
		return _windowStates.contains(windowState);
	}

	@Override
	public String getPortalInfo() {
		return ReleaseInfo.getServerInfo();
	}

	@Override
	public String getProperty(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return properties.getProperty(name);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return (Enumeration<String>)properties.propertyNames();
	}

	@Override
	public Enumeration<PortletMode> getSupportedPortletModes() {
		return Collections.enumeration(_portletModes);
	}

	@Override
	public Enumeration<WindowState> getSupportedWindowStates() {
		return Collections.enumeration(_windowStates);
	}

	private static final List<PortletMode> _portletModes = new ArrayList<>();
	private static final Set<WindowState> _windowStates = new HashSet<>();

	static {
		_portletModes.addAll(portletModes);
		_windowStates.addAll(windowStates);
	}

}