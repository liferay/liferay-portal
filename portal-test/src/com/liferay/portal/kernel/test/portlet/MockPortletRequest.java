/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.Cookie;

import java.security.Principal;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author Dante Wang
 */
public class MockPortletRequest implements PortletRequest {

	public MockPortletRequest() {
		this(null, null);
	}

	public MockPortletRequest(
		PortalContext portalContext, PortletContext portletContext) {

		_portalContext = Objects.requireNonNullElse(
			portalContext, new MockPortalContext());
		_portletContext = Objects.requireNonNullElse(
			portletContext, new MockPortletContext());
	}

	public MockPortletRequest(PortletContext portletContext) {
		this(null, portletContext);
	}

	public void addLocale(Locale locale) {
		_locales.add(locale);
	}

	public void addParameter(String name, String value) {
		addParameter(name, new String[] {value});
	}

	public void addParameter(String name, String[] values) {
		String[] parameters = _parameters.get(name);

		if (parameters != null) {
			_parameters.put(name, ArrayUtil.append(parameters, values));
		}
		else {
			_parameters.put(name, values);
		}
	}

	public void addPreferredLocale(Locale locale) {
		_locales.add(0, locale);
	}

	public void addPreferredResponseContentType(String responseContentType) {
		_responseContentTypes.add(0, responseContentType);
	}

	public void addProperty(String key, String value) {
		Assert.notNull(key, "Property key must not be null");

		List<String> values = _properties.get(key);

		if (values != null) {
			values.add(value);
		}
		else {
			_properties.put(key, new LinkedList<>(List.of(value)));
		}
	}

	public void addResponseContentType(String responseContentType) {
		_responseContentTypes.add(responseContentType);
	}

	public void addUserRole(String role) {
		_userRoles.add(role);
	}

	public void close() {
		_active = false;
	}

	@Override
	public Object getAttribute(String name) {
		checkActive();

		return _attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		checkActive();

		return Collections.enumeration(_attributes.keySet());
	}

	@Override
	public String getAuthType() {
		return _authType;
	}

	@Override
	public String getContextPath() {
		return _contextPath;
	}

	@Override
	public Cookie[] getCookies() {
		return _cookies;
	}

	@Override
	public Locale getLocale() {
		return _locales.get(0);
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return Collections.enumeration(_locales);
	}

	@Override
	public String getParameter(String name) {
		String[] parameters = _parameters.get(name);

		if (ArrayUtil.isEmpty(parameters)) {
			return null;
		}

		return parameters[0];
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(_parameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(_parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return _parameters.get(name);
	}

	@Override
	public PortalContext getPortalContext() {
		return _portalContext;
	}

	@Override
	public PortletContext getPortletContext() {
		return _portletContext;
	}

	@Override
	public PortletMode getPortletMode() {
		return _portletMode;
	}

	@Override
	public PortletSession getPortletSession() {
		return getPortletSession(true);
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		checkActive();

		if ((_portletSession instanceof
				MockPortletSession mockPortletSession) &&
			mockPortletSession.isInvalid()) {

			_portletSession = null;
		}

		if ((_portletSession == null) && create) {
			_portletSession = new MockPortletSession(_portletContext);
		}

		return _portletSession;
	}

	@Override
	public PortletPreferences getPreferences() {
		return _portletPreferences;
	}

	@Override
	public Map<String, String[]> getPrivateParameterMap() {
		if (!_publicParameterNames.isEmpty()) {
			Map<String, String[]> privateParameters = new LinkedHashMap<>();

			_parameters.forEach(
				(key, value) -> {
					if (!_publicParameterNames.contains(key)) {
						privateParameters.put(key, _parameters.get(key));
					}
				});

			return privateParameters;
		}

		return Collections.unmodifiableMap(_parameters);
	}

	@Override
	public Enumeration<String> getProperties(String key) {
		Assert.notNull(key, "property key must not be null");

		return Collections.enumeration(_properties.get(key));
	}

	@Override
	public String getProperty(String key) {
		Assert.notNull(key, "Property key must not be null");

		List<String> values = _properties.get(key);

		if (ListUtil.isEmpty(values)) {
			return null;
		}

		return values.get(0);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return Collections.enumeration(_properties.keySet());
	}

	@Override
	public Map<String, String[]> getPublicParameterMap() {
		if (!_publicParameterNames.isEmpty()) {
			Map<String, String[]> publicParameters = new LinkedHashMap<>();

			_parameters.forEach(
				(key, value) -> {
					if (_publicParameterNames.contains(key)) {
						publicParameters.put(key, _parameters.get(key));
					}
				});

			return publicParameters;
		}

		return Collections.emptyMap();
	}

	@Override
	public String getRemoteUser() {
		return _remoteUser;
	}

	@Override
	public RenderParameters getRenderParameters() {
		if (_renderParameters == null) {
			_renderParameters = new MockRenderParameters();
		}

		return _renderParameters;
	}

	@Override
	public String getRequestedSessionId() {
		PortletSession portletSession = getPortletSession();

		if (portletSession == null) {
			return null;
		}

		return portletSession.getId();
	}

	@Override
	public String getResponseContentType() {
		return _responseContentTypes.get(0);
	}

	@Override
	public Enumeration<String> getResponseContentTypes() {
		return Collections.enumeration(_responseContentTypes);
	}

	@Override
	public String getScheme() {
		return _scheme;
	}

	@Override
	public String getServerName() {
		return _serverName;
	}

	@Override
	public int getServerPort() {
		return _serverPort;
	}

	@Override
	public String getUserAgent() {
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return _userPrincipal;
	}

	@Override
	public String getWindowID() {
		return _windowID;
	}

	@Override
	public WindowState getWindowState() {
		return _windowState;
	}

	public boolean isActive() {
		return _active;
	}

	@Override
	public boolean isPortletModeAllowed(PortletMode portletMode) {
		return CollectionUtils.contains(
			_portalContext.getSupportedPortletModes(), portletMode);
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return _requestedSessionIdValid;
	}

	@Override
	public boolean isSecure() {
		return _secure;
	}

	@Override
	public boolean isUserInRole(String role) {
		return _userRoles.contains(role);
	}

	@Override
	public boolean isWindowStateAllowed(WindowState windowState) {
		return CollectionUtils.contains(
			_portalContext.getSupportedWindowStates(), windowState);
	}

	public void registerPublicParameter(String name) {
		_publicParameterNames.add(name);
	}

	@Override
	public void removeAttribute(String name) {
		checkActive();

		_attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		checkActive();

		if (value != null) {
			_attributes.put(name, value);
		}
		else {
			_attributes.remove(name);
		}
	}

	public void setAuthType(String authType) {
		_authType = authType;
	}

	public void setContextPath(String contextPath) {
		_contextPath = contextPath;
	}

	public void setCookies(Cookie... cookies) {
		_cookies = cookies;
	}

	public void setParameter(String key, String value) {
		Assert.notNull(key, "Parameter key must be null");
		Assert.notNull(value, "Parameter value must not be null");

		_parameters.put(key, new String[] {value});
	}

	public void setParameter(String key, String[] values) {
		Assert.notNull(key, "Parameter key must be null");
		Assert.notNull(values, "Parameter values must not be null");

		_parameters.put(key, values);
	}

	public void setParameters(Map<String, String[]> parameters) {
		Assert.notNull(parameters, "Parameters must not be null");

		_parameters.clear();

		_parameters.putAll(parameters);
	}

	public void setPortletMode(PortletMode portletMode) {
		Assert.notNull(portletMode, "Portlet mode must not be null");

		_portletMode = portletMode;
	}

	public void setPortletSession(PortletSession portletSession) {
		_portletSession = portletSession;

		if (portletSession instanceof MockPortletSession mockPortletSession) {
			mockPortletSession.access();
		}
	}

	public void setPreferences(PortletPreferences portletPreferences) {
		Assert.notNull(
			portletPreferences, "Portlet preferences must not be null");

		_portletPreferences = portletPreferences;
	}

	public void setProperty(String key, String value) {
		Assert.notNull(key, "Property key must not be null");

		List<String> list = new LinkedList<>();

		list.add(value);

		_properties.put(key, list);
	}

	public void setRemoteUser(String remoteUser) {
		_remoteUser = remoteUser;
	}

	public void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
		_requestedSessionIdValid = requestedSessionIdValid;
	}

	public void setScheme(String scheme) {
		_scheme = scheme;
	}

	public void setSecure(boolean secure) {
		_secure = secure;
	}

	public void setServerName(String serverName) {
		_serverName = serverName;
	}

	public void setServerPort(int serverPort) {
		_serverPort = serverPort;
	}

	public void setUserPrincipal(Principal userPrincipal) {
		_userPrincipal = userPrincipal;
	}

	public void setWindowID(String windowID) {
		_windowID = windowID;
	}

	public void setWindowState(WindowState windowState) {
		Assert.notNull(windowState, "Window state must not be null");

		_windowState = windowState;
	}

	protected void checkActive() throws IllegalStateException {
		if (!_active) {
			throw new IllegalStateException("Request is inactive");
		}
	}

	protected String getLifecyclePhase() {
		return null;
	}

	private boolean _active = true;
	private final Map<String, Object> _attributes =
		LinkedHashMapBuilder.<String, Object>put(
			"jakarta.portlet.lifecycle_phase", getLifecyclePhase()
		).build();
	private String _authType;
	private String _contextPath = StringPool.BLANK;
	private Cookie[] _cookies;
	private final List<Locale> _locales = new LinkedList<>(
		List.of(LocaleUtil.ENGLISH));
	private final Map<String, String[]> _parameters = new LinkedHashMap<>();
	private final PortalContext _portalContext;
	private final PortletContext _portletContext;
	private PortletMode _portletMode = PortletMode.VIEW;
	private PortletPreferences _portletPreferences =
		new MockPortletPreferences();
	private PortletSession _portletSession;
	private final Map<String, List<String>> _properties = new LinkedHashMap<>();
	private final Set<String> _publicParameterNames = new HashSet<>();
	private String _remoteUser;
	private RenderParameters _renderParameters;
	private boolean _requestedSessionIdValid = true;
	private final List<String> _responseContentTypes = new LinkedList<>(
		List.of("text/html"));
	private String _scheme = "http";
	private boolean _secure;
	private String _serverName = "localhost";
	private int _serverPort = 80;
	private Principal _userPrincipal;
	private final Set<String> _userRoles = new HashSet<>();
	private String _windowID;
	private WindowState _windowState = WindowState.NORMAL;

}