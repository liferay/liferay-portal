/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ProxyFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Enumeration;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class HttpSessionWrapper implements HttpSession {

	public HttpSessionWrapper(HttpSession httpSession) {
		_httpSession = httpSession;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof HttpSessionWrapper) {
			HttpSessionWrapper sessionWrapper = (HttpSessionWrapper)object;

			object = sessionWrapper.getWrappedSession();
		}

		return _httpSession.equals(object);
	}

	@Override
	public Object getAttribute(String name) {
		return _httpSession.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return _httpSession.getAttributeNames();
	}

	@Override
	public long getCreationTime() {
		return _httpSession.getCreationTime();
	}

	@Override
	public String getId() {
		return _httpSession.getId();
	}

	@Override
	public long getLastAccessedTime() {
		return _httpSession.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		return _httpSession.getMaxInactiveInterval();
	}

	@Override
	public ServletContext getServletContext() {
		return _httpSession.getServletContext();
	}

	/**
	 * @deprecated As of Bunyan (6.0.x)
	 */
	@Deprecated
	public HttpSessionContext getSessionContext() {
		return ProxyFactory.newDummyInstance(HttpSessionContext.class);
	}

	/**
	 * @deprecated As of Bunyan (6.0.x)
	 */
	@Deprecated
	public Object getValue(String name) {
		return _httpSession.getAttribute(name);
	}

	/**
	 * @deprecated As of Bunyan (6.0.x)
	 */
	@Deprecated
	public String[] getValueNames() {
		List<String> names = ListUtil.fromEnumeration(
			_httpSession.getAttributeNames());

		return names.toArray(new String[0]);
	}

	public HttpSession getWrappedSession() {
		return _httpSession;
	}

	@Override
	public int hashCode() {
		return _httpSession.hashCode();
	}

	@Override
	public void invalidate() {
		_httpSession.invalidate();
	}

	@Override
	public boolean isNew() {
		return _httpSession.isNew();
	}

	/**
	 * @deprecated As of Bunyan (6.0.x)
	 */
	@Deprecated
	public void putValue(String name, Object value) {
		_httpSession.setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		_httpSession.removeAttribute(name);
	}

	/**
	 * @deprecated As of Bunyan (6.0.x)
	 */
	@Deprecated
	public void removeValue(String name) {
		_httpSession.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		_httpSession.setAttribute(name, value);
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		_httpSession.setMaxInactiveInterval(interval);
	}

	private final HttpSession _httpSession;

}