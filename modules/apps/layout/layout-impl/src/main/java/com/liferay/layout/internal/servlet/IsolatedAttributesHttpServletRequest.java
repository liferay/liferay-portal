/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.servlet;

import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.servlet.RequestDispatcherAttributeNames;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
		if (RequestDispatcherAttributeNames.contains(name)) {
			return super.getAttribute(name);
		}

		Object value = _attributes.get(name);

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

		if (_attributes.isEmpty()) {
			return enumeration;
		}

		Set<String> names = new HashSet<>();

		while (enumeration.hasMoreElements()) {
			names.add(enumeration.nextElement());
		}

		for (Map.Entry<String, Object> entry : _attributes.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value == _nullValue) {
				names.remove(key);
			}
			else {
				names.add(key);
			}
		}

		return Collections.enumeration(names);
	}

	@Override
	public void removeAttribute(String name) {
		if (RequestDispatcherAttributeNames.contains(name)) {
			super.removeAttribute(name);
		}
		else {
			_attributes.put(name, _nullValue);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (RequestDispatcherAttributeNames.contains(name)) {
			super.setAttribute(name, value);
		}
		else {
			if (value == null) {
				value = _nullValue;
			}

			_attributes.put(name, value);
		}
	}

	private static final Object _nullValue = new Object();

	private final Map<String, Object> _attributes = new HashMap<>();

}