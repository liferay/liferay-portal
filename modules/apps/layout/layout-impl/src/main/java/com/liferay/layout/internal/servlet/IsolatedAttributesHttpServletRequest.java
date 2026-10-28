/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.servlet;

import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.servlet.RequestDispatcherAttributeNames;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Javier Moral
 */
public class IsolatedAttributesHttpServletRequest
	extends PersistentHttpServletRequestWrapper {

	public IsolatedAttributesHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);
	}

	@Override
	public Object getAttribute(String name) {
		Map<String, Object> attributes = _attributes;

		if (RequestDispatcherAttributeNames.contains(name)) {
			attributes = _requestDispatcherAttributes.getOrDefault(
				getRequest(), Collections.emptyMap());
		}

		Object value = attributes.get(name);

		if (value == _nullValue) {
			return null;
		}

		if (value != null) {
			return value;
		}

		return super.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		Enumeration<String> enumeration = super.getAttributeNames();
		Map<String, Object> requestDispatcherAttributes =
			_requestDispatcherAttributes.getOrDefault(
				getRequest(), Collections.emptyMap());

		if (_attributes.isEmpty() && requestDispatcherAttributes.isEmpty()) {
			return enumeration;
		}

		Set<String> names = new HashSet<>();

		while (enumeration.hasMoreElements()) {
			names.add(enumeration.nextElement());
		}

		_updateAttributeNames(_attributes, names);
		_updateAttributeNames(requestDispatcherAttributes, names);

		return Collections.enumeration(names);
	}

	@Override
	public void removeAttribute(String name) {
		if (RequestDispatcherAttributeNames.contains(name)) {
			super.removeAttribute(name);
		}

		Map<String, Object> attributes = _getAttributes(name);

		attributes.put(name, _nullValue);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (RequestDispatcherAttributeNames.contains(name)) {
			super.setAttribute(name, value);
		}

		if (value == null) {
			value = _nullValue;
		}

		Map<String, Object> attributes = _getAttributes(name);

		attributes.put(name, value);
	}

	private Map<String, Object> _getAttributes(String name) {
		if (!RequestDispatcherAttributeNames.contains(name)) {
			return _attributes;
		}

		return _requestDispatcherAttributes.computeIfAbsent(
			getRequest(), servletRequest -> new HashMap<>());
	}

	private void _updateAttributeNames(
		Map<String, Object> attributes, Set<String> names) {

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value == _nullValue) {
				names.remove(key);
			}
			else {
				names.add(key);
			}
		}
	}

	private static final Object _nullValue = new Object();

	private final Map<String, Object> _attributes = new HashMap<>();
	private final Map<ServletRequest, Map<String, Object>>
		_requestDispatcherAttributes = new IdentityHashMap<>();

}