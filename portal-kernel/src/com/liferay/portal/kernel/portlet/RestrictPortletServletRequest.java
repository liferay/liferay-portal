/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.servlet.RequestDispatcherAttributeNames;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Mergeable;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class RestrictPortletServletRequest
	extends PersistentHttpServletRequestWrapper {

	public static boolean isSharedRequestAttribute(String name) {
		for (String requestSharedAttribute : _REQUEST_SHARED_ATTRIBUTES) {
			if (name.startsWith(requestSharedAttribute)) {
				return true;
			}
		}

		return false;
	}

	public RestrictPortletServletRequest(
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
		Enumeration<String> superEnumeration = super.getAttributeNames();

		if (_attributes.isEmpty()) {
			return superEnumeration;
		}

		Set<String> names = new HashSet<>();

		while (superEnumeration.hasMoreElements()) {
			names.add(superEnumeration.nextElement());
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

	public Map<String, Object> getAttributes() {
		return _attributes;
	}

	public void mergeSharedAttributes() {
		doMergeSharedAttributes(getRequest());
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

	protected void doMergeSharedAttributes(ServletRequest servletRequest) {
		for (Map.Entry<String, Object> entry : _attributes.entrySet()) {
			String name = entry.getKey();

			doMergeSharedAttributes(servletRequest, name, entry.getValue());
		}
	}

	protected void doMergeSharedAttributes(
		ServletRequest servletRequest, String name, Object value) {

		if (isSharedRequestAttribute(name)) {
			if (value == _nullValue) {
				servletRequest.removeAttribute(name);

				if (_log.isDebugEnabled()) {
					_log.debug("Remove shared attribute " + name);
				}
			}
			else {
				Object masterValue = servletRequest.getAttribute(name);

				if ((masterValue == null) || !(value instanceof Mergeable)) {
					servletRequest.setAttribute(name, value);

					if (_log.isDebugEnabled()) {
						_log.debug("Set shared attribute " + name);
					}
				}
				else {
					Mergeable<Object> masterMergeable =
						(Mergeable<Object>)masterValue;
					Mergeable<Object> slaveMergeable = (Mergeable<Object>)value;

					masterMergeable.merge(slaveMergeable);

					if (_log.isDebugEnabled()) {
						_log.debug("Merge shared attribute " + name);
					}
				}
			}
		}
		else {
			if ((value != _nullValue) && _log.isDebugEnabled()) {
				_log.debug("Ignore setting restricted attribute " + name);
			}
		}
	}

	private static final String[] _REQUEST_SHARED_ATTRIBUTES = ArrayUtil.append(
		PropsUtil.getArray(PropsKeys.REQUEST_SHARED_ATTRIBUTES),
		"LIFERAY_SHARED_");

	private static final Log _log = LogFactoryUtil.getLog(
		RestrictPortletServletRequest.class);

	private static final Object _nullValue = new Object();

	private final Map<String, Object> _attributes = new HashMap<>();

}