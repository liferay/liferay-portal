/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * @author Neil Griffin
 */
@ManagedBean
@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRedirect")
public class ConfigurationImpl implements Configuration {

	public static final String DEFAULT_VIEW_EXTENSION =
		"com.liferay.mvc.defaultViewExtension";

	@Override
	public Set<Class<?>> getClasses() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<Class<?>, Integer> getContracts(Class<?> componentClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Object> getInstances() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getProperties() {
		return _properties;
	}

	@Override
	public Object getProperty(String name) {
		Map<String, Object> properties = getProperties();

		return properties.get(name);
	}

	@Override
	public Collection<String> getPropertyNames() {
		Map<String, Object> properties = getProperties();

		return properties.keySet();
	}

	@Override
	public RuntimeType getRuntimeType() {
		return RuntimeType.SERVER;
	}

	@Override
	public boolean isEnabled(Class<? extends Feature> featureClass) {
		return false;
	}

	@Override
	public boolean isEnabled(Feature feature) {
		return false;
	}

	@Override
	public boolean isRegistered(Class<?> componentClass) {
		return false;
	}

	@Override
	public boolean isRegistered(Object component) {
		return false;
	}

	@PostConstruct
	public void postConstruct() {
		_properties = new HashMap<>();

		Enumeration<String> enumeration =
			_portletConfig.getInitParameterNames();

		while (enumeration.hasMoreElements()) {
			String initParameterName = enumeration.nextElement();

			_properties.put(
				initParameterName,
				_portletConfig.getInitParameter(initParameterName));
		}

		enumeration = _portletContext.getInitParameterNames();

		while (enumeration.hasMoreElements()) {
			String initParameterName = enumeration.nextElement();

			_properties.put(
				initParameterName,
				_portletContext.getInitParameter(initParameterName));
		}

		if (!_properties.containsKey(DEFAULT_VIEW_EXTENSION)) {
			_properties.put(DEFAULT_VIEW_EXTENSION, "jsp");
		}
	}

	@Autowired
	private PortletConfig _portletConfig;

	@Autowired
	private PortletContext _portletContext;

	private Map<String, Object> _properties;

}