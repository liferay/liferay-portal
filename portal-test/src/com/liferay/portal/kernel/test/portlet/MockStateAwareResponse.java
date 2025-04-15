/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.StateAwareResponse;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.springframework.util.CollectionUtils;

/**
 * @author Dante Wang
 */
public class MockStateAwareResponse
	extends MockPortletResponse implements StateAwareResponse {

	public Serializable getEvent(QName name) {
		return _events.get(name);
	}

	public Serializable getEvent(String name) {
		return _events.get(new QName(name));
	}

	public Iterator<QName> getEventNames() {
		Set<QName> keys = _events.keySet();

		return keys.iterator();
	}

	@Override
	public PortletMode getPortletMode() {
		return _portletMode;
	}

	public String getRenderParameter(String key) {
		String[] array = _renderParameters.get(key);

		if (ArrayUtil.isNotEmpty(array)) {
			return array[0];
		}

		return null;
	}

	@Override
	public Map<String, String[]> getRenderParameterMap() {
		return Collections.unmodifiableMap(_renderParameters);
	}

	public Iterator<String> getRenderParameterNames() {
		Set<String> keys = _renderParameters.keySet();

		return keys.iterator();
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		if (_mutableRenderParameters == null) {
			_mutableRenderParameters = new MockMutableRenderParameters();
		}

		return _mutableRenderParameters;
	}

	public String[] getRenderParameterValues(String key) {
		return _renderParameters.get(key);
	}

	@Override
	public WindowState getWindowState() {
		return _windowState;
	}

	@Override
	public void removePublicRenderParameter(String name) {
		_renderParameters.remove(name);
	}

	@Override
	public void setEvent(QName name, Serializable value) {
		_events.put(name, value);
	}

	@Override
	public void setEvent(String name, Serializable value) {
		_events.put(new QName(name), value);
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {

		PortalContext portalContext = getPortalContext();

		if (!CollectionUtils.contains(
				portalContext.getSupportedPortletModes(), portletMode)) {

			throw new PortletModeException(portletMode.toString(), portletMode);
		}

		_portletMode = portletMode;
	}

	@Override
	public void setRenderParameter(String key, String value) {
		_renderParameters.put(key, new String[] {value});
	}

	@Override
	public void setRenderParameter(String key, String[] values) {
		_renderParameters.put(key, values);
	}

	@Override
	public void setRenderParameters(Map<String, String[]> parameters) {
		_renderParameters.clear();

		_renderParameters.putAll(parameters);
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {

		PortalContext portalContext = getPortalContext();

		if (!CollectionUtils.contains(
				portalContext.getSupportedWindowStates(), windowState)) {

			throw new WindowStateException(windowState.toString(), windowState);
		}

		_windowState = windowState;
	}

	private final Map<QName, Serializable> _events = new HashMap<>();
	private MutableRenderParameters _mutableRenderParameters;
	private PortletMode _portletMode;
	private final Map<String, String[]> _renderParameters =
		new LinkedHashMap<>();
	private WindowState _windowState;

}