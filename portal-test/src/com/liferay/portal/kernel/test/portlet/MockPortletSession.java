/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.mock.web.MockHttpSession;

/**
 * @author Dante Wang
 */
public class MockPortletSession implements PortletSession {

	public MockPortletSession() {
		this(null);
	}

	public MockPortletSession(PortletContext portletContext) {
		_portletContext = Objects.requireNonNullElse(
			portletContext, new MockPortletContext());

		_applicationAttributes = new HashMap<>();
		_creationTime = System.currentTimeMillis();
		_id = String.valueOf(_nextPortletSessionId++);
		_invalid = false;
		_isNew = true;
		_lastAccessedTime = System.currentTimeMillis();
		_portletAttributes = new HashMap<>();
	}

	public void access() {
		_lastAccessedTime = System.currentTimeMillis();

		setNew(false);
	}

	public void clearAttributes() {
		doClearAttributes(_applicationAttributes);
		doClearAttributes(_portletAttributes);
	}

	@Override
	public Object getAttribute(String name) {
		return _portletAttributes.get(name);
	}

	@Override
	public Object getAttribute(String name, int scope) {
		if (scope == APPLICATION_SCOPE) {
			return _applicationAttributes.get(name);
		}

		if (scope == PORTLET_SCOPE) {
			return _portletAttributes.get(name);
		}

		return null;
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		return Collections.unmodifiableMap(_portletAttributes);
	}

	@Override
	public Map<String, Object> getAttributeMap(int scope) {
		if (scope == APPLICATION_SCOPE) {
			return Collections.unmodifiableMap(_applicationAttributes);
		}

		if (scope == PORTLET_SCOPE) {
			return Collections.unmodifiableMap(_portletAttributes);
		}

		return Collections.emptyMap();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(_portletAttributes.keySet());
	}

	@Override
	public Enumeration<String> getAttributeNames(int scope) {
		if (scope == APPLICATION_SCOPE) {
			return Collections.enumeration(_applicationAttributes.keySet());
		}

		if (scope == PORTLET_SCOPE) {
			return Collections.enumeration(_portletAttributes.keySet());
		}

		return null;
	}

	@Override
	public long getCreationTime() {
		return _creationTime;
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public long getLastAccessedTime() {
		return _lastAccessedTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		return _maxInactiveInterval;
	}

	@Override
	public PortletContext getPortletContext() {
		return _portletContext;
	}

	@Override
	public void invalidate() {
		_invalid = true;

		clearAttributes();
	}

	public boolean isInvalid() {
		return _invalid;
	}

	@Override
	public boolean isNew() {
		return _isNew;
	}

	@Override
	public void removeAttribute(String name) {
		_portletAttributes.remove(name);
	}

	@Override
	public void removeAttribute(String name, int scope) {
		if (scope == APPLICATION_SCOPE) {
			_applicationAttributes.remove(name);
		}
		else if (scope == PORTLET_SCOPE) {
			_portletAttributes.remove(name);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (value != null) {
			_portletAttributes.put(name, value);
		}
		else {
			_portletAttributes.remove(name);
		}
	}

	@Override
	public void setAttribute(String name, Object value, int scope) {
		if (scope == APPLICATION_SCOPE) {
			if (value != null) {
				_applicationAttributes.put(name, value);
			}
			else {
				_applicationAttributes.remove(name);
			}
		}
		else if (scope == PORTLET_SCOPE) {
			if (value != null) {
				_portletAttributes.put(name, value);
			}
			else {
				_portletAttributes.remove(name);
			}
		}
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		_maxInactiveInterval = interval;
	}

	public void setNew(boolean value) {
		_isNew = value;
	}

	protected void doClearAttributes(Map<String, Object> attributes) {
		Set<Map.Entry<String, Object>> entries = attributes.entrySet();

		Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();

			Object value = entry.getValue();

			iterator.remove();

			if (value instanceof
					HttpSessionBindingListener httpSessionBindingListener) {

				httpSessionBindingListener.valueUnbound(
					new HttpSessionBindingEvent(
						new MockHttpSession(), entry.getKey(), value));
			}
		}
	}

	private static int _nextPortletSessionId = 1;

	private final Map<String, Object> _applicationAttributes;
	private final long _creationTime;
	private final String _id;
	private boolean _invalid;
	private boolean _isNew;
	private long _lastAccessedTime;
	private int _maxInactiveInterval;
	private final Map<String, Object> _portletAttributes;
	private final PortletContext _portletContext;

}