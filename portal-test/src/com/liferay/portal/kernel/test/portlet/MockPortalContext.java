/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dante Wang
 */
public class MockPortalContext implements PortalContext {

	public MockPortalContext() {
		_portletModeEnumeration = Collections.enumeration(
			List.of(PortletMode.EDIT, PortletMode.HELP, PortletMode.VIEW));
		_windowStateEnumeration = Collections.enumeration(
			List.of(
				WindowState.MAXIMIZED, WindowState.MINIMIZED,
				WindowState.NORMAL));
	}

	public MockPortalContext(
		List<PortletMode> supportedPortletModes,
		List<WindowState> supportedWindowStates) {

		_portletModeEnumeration = Collections.enumeration(
			new ArrayList<>(supportedPortletModes));
		_windowStateEnumeration = Collections.enumeration(
			new ArrayList<>(supportedWindowStates));
	}

	@Override
	public String getPortalInfo() {
		return "MockPortal/1.0";
	}

	@Override
	public String getProperty(String name) {
		return _properties.get(name);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return Collections.enumeration(_properties.keySet());
	}

	@Override
	public Enumeration<PortletMode> getSupportedPortletModes() {
		return _portletModeEnumeration;
	}

	@Override
	public Enumeration<WindowState> getSupportedWindowStates() {
		return _windowStateEnumeration;
	}

	public void setProperty(String name, String value) {
		_properties.put(name, value);
	}

	private final Enumeration<PortletMode> _portletModeEnumeration;
	private final Map<String, String> _properties = new HashMap<>();
	private final Enumeration<WindowState> _windowStateEnumeration;

}