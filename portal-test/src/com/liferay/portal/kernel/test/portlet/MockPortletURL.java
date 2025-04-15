/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.annotations.PortletSerializable;

import java.io.IOException;
import java.io.Writer;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

/**
 * @author Dante Wang
 */
public class MockPortletURL implements PortletURL {

	public static final String URL_TYPE_ACTION = "action";

	public static final String URL_TYPE_RENDER = "render";

	public MockPortletURL(PortalContext portalContext, String urlType) {
		_portalContext = portalContext;
		_urlType = urlType;
	}

	@Override
	public void addProperty(String key, String value) {
		String[] values = _properties.get(key);

		if (values != null) {
			_properties.put(key, ArrayUtil.append(values, value));
		}
		else {
			_properties.put(key, new String[] {value});
		}
	}

	@Override
	public Appendable append(Appendable appendable) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Appendable append(Appendable appendable, boolean escapeXML)
		throws IOException {

		throw new UnsupportedOperationException();
	}

	public String getParameter(String name) {
		String[] values = parameters.get(name);

		if (ArrayUtil.isEmpty(values)) {
			return null;
		}

		return values[0];
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(parameters);
	}

	public Set<String> getParameterNames() {
		return parameters.keySet();
	}

	public String[] getParameterValues(String name) {
		return parameters.get(name);
	}

	@Override
	public PortletMode getPortletMode() {
		return _portletMode;
	}

	public Map<String, String[]> getProperties() {
		return Collections.unmodifiableMap(_properties);
	}

	@Override
	public MutableRenderParameters getRenderParameters() {
		if (_mutableRenderParameters == null) {
			_mutableRenderParameters = new MockMutableRenderParameters();
		}

		return _mutableRenderParameters;
	}

	@Override
	public WindowState getWindowState() {
		return _windowState;
	}

	public boolean isSecure() {
		return _secure;
	}

	@Override
	public void removePublicRenderParameter(String name) {
		parameters.remove(name);
	}

	@Override
	public void setBeanParameter(PortletSerializable portletSerializable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParameter(String key, String value) {
		parameters.put(key, new String[] {value});
	}

	@Override
	public void setParameter(String key, String[] values) {
		parameters.put(key, values);
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		this.parameters.clear();

		this.parameters.putAll(parameters);
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {

		if (!CollectionUtils.contains(
				_portalContext.getSupportedPortletModes(), portletMode)) {

			throw new PortletModeException(portletMode.toString(), portletMode);
		}

		_portletMode = portletMode;
	}

	@Override
	public void setProperty(String key, String value) {
		_properties.put(key, new String[] {value});
	}

	@Override
	public void setSecure(boolean secure) throws PortletSecurityException {
		_secure = secure;
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {

		if (!CollectionUtils.contains(
				_portalContext.getSupportedWindowStates(), windowState)) {

			throw new WindowStateException(windowState.toString(), windowState);
		}

		_windowState = windowState;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append(encodeParameter("urlType", _urlType));

		if (_windowState != null) {
			sb.append(StringPool.SEMICOLON);
			sb.append(encodeParameter("windowState", _windowState.toString()));
		}

		if (_portletMode != null) {
			sb.append(StringPool.SEMICOLON);
			sb.append(encodeParameter("portletMode", _portletMode.toString()));
		}

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			sb.append(StringPool.SEMICOLON);
			sb.append(
				encodeParameter("param_" + entry.getKey(), entry.getValue()));
		}

		if (isSecure()) {
			sb.append("https");
		}
		else {
			sb.append("http");
		}

		sb.append("//localhost/mockportlet?");

		return sb.toString();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(toString());
	}

	@Override
	public void write(Writer writer, boolean escapeXML) throws IOException {
		writer.write(toString());
	}

	protected String encodeParameter(String name, String value) {
		return URLEncoder.encode(name, StandardCharsets.UTF_8) + "=" +
			URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	protected String encodeParameter(String name, String[] values) {
		StringBundler sb = new StringBundler((values.length * 4) - 1);

		for (int i = 0, n = values.length; i < n; i++) {
			if (i > 0) {
				sb.append(StringPool.SEMICOLON);
			}

			sb.append(URLEncoder.encode(name, StandardCharsets.UTF_8));
			sb.append("=");
			sb.append(URLEncoder.encode(values[i], StandardCharsets.UTF_8));
		}

		return sb.toString();
	}

	protected final Map<String, String[]> parameters = new LinkedHashMap<>();

	private MutableRenderParameters _mutableRenderParameters;
	private final PortalContext _portalContext;
	private PortletMode _portletMode;
	private final Map<String, String[]> _properties = new LinkedHashMap<>();
	private boolean _secure;
	private final String _urlType;
	private WindowState _windowState;

}