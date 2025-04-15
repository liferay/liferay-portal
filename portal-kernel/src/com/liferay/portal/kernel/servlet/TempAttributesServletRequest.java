/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class TempAttributesServletRequest extends HttpServletRequestWrapper {

	public TempAttributesServletRequest(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	@Override
	public Object getAttribute(String name) {
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
		Enumeration<String> superEnumeration = super.getAttributeNames();

		if (_attributes.isEmpty()) {
			return superEnumeration;
		}

		Set<String> names = new HashSet<>();

		while (superEnumeration.hasMoreElements()) {
			names.add(superEnumeration.nextElement());
		}

		names.addAll(_attributes.keySet());

		return Collections.enumeration(names);
	}

	@Override
	public void removeAttribute(String name) {
		_attributes.remove(name);

		super.removeAttribute(name);
	}

	public void setTempAttribute(String name, Object value) {
		if (value == null) {
			value = _nullValue;
		}

		_attributes.put(name, value);
	}

	private static final Object _nullValue = new Object();

	private final Map<String, Object> _attributes = new HashMap<>();

}