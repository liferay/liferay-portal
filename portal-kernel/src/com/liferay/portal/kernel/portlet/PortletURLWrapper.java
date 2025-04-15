/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.annotations.PortletSerializable;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletURLWrapper implements PortletURL {

	public PortletURLWrapper(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	@Override
	public void addProperty(String key, String value) {
		_portletURL.addProperty(key, value);
	}

	@Override
	public Appendable append(Appendable appendable) throws IOException {
		return _portletURL.append(appendable);
	}

	@Override
	public Appendable append(Appendable appendable, boolean escapeXML)
		throws IOException {

		return _portletURL.append(appendable, escapeXML);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _portletURL.getParameterMap();
	}

	@Override
	public PortletMode getPortletMode() {
		return _portletURL.getPortletMode();
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		return _portletURL.getRenderParameters();
	}

	@Override
	public WindowState getWindowState() {
		return _portletURL.getWindowState();
	}

	@Override
	public void removePublicRenderParameter(String name) {
		_portletURL.removePublicRenderParameter(name);
	}

	@Override
	public void setBeanParameter(PortletSerializable portletSerializable) {
		_portletURL.setBeanParameter(portletSerializable);
	}

	@Override
	public void setParameter(String name, String value) {
		_portletURL.setParameter(name, value);
	}

	@Override
	public void setParameter(String name, String[] values) {
		_portletURL.setParameter(name, values);
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		_portletURL.setParameters(parameters);
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {

		_portletURL.setPortletMode(portletMode);
	}

	@Override
	public void setProperty(String key, String value) {
		_portletURL.setProperty(key, value);
	}

	@Override
	public void setSecure(boolean secure) throws PortletSecurityException {
		_portletURL.setSecure(secure);
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {

		_portletURL.setWindowState(windowState);
	}

	@Override
	public String toString() {
		return _portletURL.toString();
	}

	@Override
	public void write(Writer writer) throws IOException {
		_portletURL.write(writer);
	}

	@Override
	public void write(Writer writer, boolean escapeXML) throws IOException {
		_portletURL.write(writer, escapeXML);
	}

	private final PortletURL _portletURL;

}