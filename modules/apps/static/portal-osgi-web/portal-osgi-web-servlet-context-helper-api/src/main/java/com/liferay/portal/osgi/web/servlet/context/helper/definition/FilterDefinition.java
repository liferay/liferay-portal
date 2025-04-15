/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.definition;

import jakarta.servlet.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class FilterDefinition {

	public void addDispatcher(String dispatcher) {
		_dispatchers.add(dispatcher);
	}

	public void addServletName(String servletName) {
		_servletNames.add(servletName);
	}

	public void addURLPattern(String urlPattern) {
		_urlPatterns.add(urlPattern);
	}

	public List<String> getDispatchers() {
		return _dispatchers;
	}

	public Filter getFilter() {
		return _filter;
	}

	public Map<String, String> getInitParameters() {
		return _initParameters;
	}

	public String getName() {
		return _name;
	}

	public int getPriority() {
		return _priority;
	}

	public List<String> getServletNames() {
		return _servletNames;
	}

	public List<String> getURLPatterns() {
		return _urlPatterns;
	}

	public boolean isAsyncSupported() {
		return _asyncSupported;
	}

	public void setAsyncSupported(boolean asyncSupported) {
		_asyncSupported = asyncSupported;
	}

	public void setDispatchers(List<String> dispatchers) {
		_dispatchers.addAll(dispatchers);
	}

	public void setFilter(Filter filter) {
		_filter = filter;
	}

	public void setInitParameter(String key, String value) {
		_initParameters.put(key, value);
	}

	public void setInitParameters(Map<String, String> initParameters) {
		_initParameters.putAll(initParameters);
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPriority(int priority) {
		_priority = priority;
	}

	public void setServletNames(List<String> servletNames) {
		_servletNames.addAll(servletNames);
	}

	public void setURLPatterns(List<String> urlPatterns) {
		_urlPatterns.addAll(urlPatterns);
	}

	private boolean _asyncSupported;
	private final List<String> _dispatchers = new ArrayList<>();
	private Filter _filter;
	private final Map<String, String> _initParameters = new HashMap<>();
	private String _name;
	private int _priority;
	private final List<String> _servletNames = new ArrayList<>();
	private final List<String> _urlPatterns = new ArrayList<>();

}