/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletSession;
import com.liferay.portlet.PortletSessionAttributeMap;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class PortletSessionImpl implements LiferayPortletSession {

	public PortletSessionImpl(
		HttpSession httpSession, PortletContext portletContext,
		String portletName, long plid) {

		this.httpSession = httpSession;
		this.portletContext = portletContext;

		scopePrefix = StringBundler.concat(
			PORTLET_SCOPE_NAMESPACE, portletName, LAYOUT_SEPARATOR, plid,
			StringPool.QUESTION);
	}

	@Override
	public Object getAttribute(String name) {
		return getAttribute(name, PORTLET_SCOPE);
	}

	@Override
	public Object getAttribute(String name, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (_invalidated) {
			throw new IllegalStateException();
		}

		if (scope == PORTLET_SCOPE) {
			name = scopePrefix.concat(name);
		}

		return httpSession.getAttribute(name);
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		return getAttributeMap(PortletSession.PORTLET_SCOPE);
	}

	@Override
	public Map<String, Object> getAttributeMap(int scope) {
		if (scope == PORTLET_SCOPE) {
			return new PortletSessionAttributeMap(httpSession, scopePrefix);
		}

		return new PortletSessionAttributeMap(httpSession);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return getAttributeNames(PORTLET_SCOPE);
	}

	@Override
	public Enumeration<String> getAttributeNames(int scope) {
		if (scope != PORTLET_SCOPE) {
			return httpSession.getAttributeNames();
		}

		List<String> attributeNames = new ArrayList<>();

		Enumeration<String> enumeration = httpSession.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith(scopePrefix)) {
				name = name.substring(scopePrefix.length());

				attributeNames.add(name);
			}
		}

		return Collections.enumeration(attributeNames);
	}

	@Override
	public long getCreationTime() {
		if (_invalidated) {
			throw new IllegalStateException();
		}

		return httpSession.getCreationTime();
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	@Override
	public String getId() {
		return httpSession.getId();
	}

	@Override
	public long getLastAccessedTime() {
		return httpSession.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		return httpSession.getMaxInactiveInterval();
	}

	@Override
	public PortletContext getPortletContext() {
		return portletContext;
	}

	@Override
	public void invalidate() {
		_invalidated = true;

		httpSession.invalidate();
	}

	public boolean isInvalidated() {
		return _invalidated;
	}

	@Override
	public boolean isNew() {
		return httpSession.isNew();
	}

	@Override
	public void removeAttribute(String name) {
		removeAttribute(name, PORTLET_SCOPE);
	}

	@Override
	public void removeAttribute(String name, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (scope == PORTLET_SCOPE) {
			name = scopePrefix.concat(name);
		}

		httpSession.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		setAttribute(name, value, PORTLET_SCOPE);
	}

	@Override
	public void setAttribute(String name, Object value, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (scope == PORTLET_SCOPE) {
			name = scopePrefix.concat(name);
		}

		httpSession.setAttribute(name, value);
	}

	@Override
	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		httpSession.setMaxInactiveInterval(interval);
	}

	protected HttpSession httpSession;
	protected final PortletContext portletContext;
	protected final String scopePrefix;

	private boolean _invalidated;

}