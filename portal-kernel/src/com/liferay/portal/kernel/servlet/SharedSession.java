/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class SharedSession implements HttpSession {

	public SharedSession(
		HttpSession portalHttpSession, HttpSession portletHttpSession) {

		if (portalHttpSession == null) {
			_portalHttpSession = new NullSession();

			if (_log.isWarnEnabled()) {
				_log.warn("Wrapped portal session is null");
			}
		}
		else {
			_portalHttpSession = portalHttpSession;
		}

		_portletHttpSession = portletHttpSession;
	}

	@Override
	public Object getAttribute(String name) {
		HttpSession httpSession = getSessionDelegate(name);

		return httpSession.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		HttpSession httpSession = getSessionDelegate();

		Enumeration<String> namesEnumeration = httpSession.getAttributeNames();

		if (httpSession == _portletHttpSession) {
			List<String> namesList = Collections.list(namesEnumeration);

			Enumeration<String> portalHttpSessionNamesEnumeration =
				_portalHttpSession.getAttributeNames();

			while (portalHttpSessionNamesEnumeration.hasMoreElements()) {
				String name = portalHttpSessionNamesEnumeration.nextElement();

				if (containsSharedAttribute(name)) {
					namesList.add(name);
				}
			}

			namesEnumeration = Collections.enumeration(namesList);
		}

		return namesEnumeration;
	}

	@Override
	public long getCreationTime() {
		HttpSession httpSession = getSessionDelegate();

		return httpSession.getCreationTime();
	}

	@Override
	public String getId() {
		HttpSession httpSession = getSessionDelegate();

		return httpSession.getId();
	}

	@Override
	public long getLastAccessedTime() {
		HttpSession httpSession = getSessionDelegate();

		return httpSession.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		HttpSession httpSession = getSessionDelegate();

		return httpSession.getMaxInactiveInterval();
	}

	@Override
	public ServletContext getServletContext() {
		HttpSession httpSession = getSessionDelegate();

		return httpSession.getServletContext();
	}

	/**
	 * @deprecated As of Paton (6.1.x)
	 */
	@Deprecated
	public HttpSessionContext getSessionContext() {
		return ProxyFactory.newDummyInstance(HttpSessionContext.class);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public Object getValue(String name) {
		return getAttribute(name);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public String[] getValueNames() {
		List<String> names = ListUtil.fromEnumeration(getAttributeNames());

		return names.toArray(new String[0]);
	}

	@Override
	public void invalidate() {
		HttpSession httpSession = getSessionDelegate();

		httpSession.invalidate();
	}

	@Override
	public boolean isNew() {
		HttpSession httpSession = getSessionDelegate();

		return httpSession.isNew();
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		HttpSession httpSession = getSessionDelegate(name);

		httpSession.removeAttribute(name);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public void removeValue(String name) {
		removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		HttpSession httpSession = getSessionDelegate(name);

		httpSession.setAttribute(name, value);
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		HttpSession httpSession = getSessionDelegate();

		httpSession.setMaxInactiveInterval(maxInactiveInterval);
	}

	protected boolean containsSharedAttribute(String name) {
		for (String sharedName : _SESSION_SHARED_ATTRIBUTES) {
			if (name.startsWith(sharedName)) {
				return true;
			}
		}

		return false;
	}

	protected HttpSession getSessionDelegate() {
		if (_portletHttpSession != null) {
			return _portletHttpSession;
		}

		return _portalHttpSession;
	}

	protected HttpSession getSessionDelegate(String name) {
		if (_portletHttpSession == null) {
			return _portalHttpSession;
		}

		if (_sharedSessionAttributesExcludes.containsKey(name)) {
			return _portletHttpSession;
		}
		else if (containsSharedAttribute(name)) {
			return _portalHttpSession;
		}

		return _portletHttpSession;
	}

	private static final String[] _SESSION_SHARED_ATTRIBUTES =
		PropsUtil.getArray(PropsKeys.SESSION_SHARED_ATTRIBUTES);

	private static final Log _log = LogFactoryUtil.getLog(SharedSession.class);

	private static final Map<String, String> _sharedSessionAttributesExcludes =
		new HashMap<String, String>() {
			{
				for (String name :
						PropsUtil.getArray(
							PropsKeys.SESSION_SHARED_ATTRIBUTES_EXCLUDES)) {

					put(name, name);
				}
			}
		};

	private final HttpSession _portalHttpSession;
	private HttpSession _portletHttpSession;

	private class NullSession implements HttpSession {

		public NullSession() {
			_creationTime = System.currentTimeMillis();
			_id =
				NullSession.class.getName() + StringPool.POUND +
					StringUtil.randomId();
			_lastAccessedTime = _creationTime;
			_maxInactiveInterval = 0;
			_servletContext = null;
			_new = true;
		}

		@Override
		public Object getAttribute(String name) {
			return _attributes.get(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return Collections.enumeration(_attributes.keySet());
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
		public ServletContext getServletContext() {
			return _servletContext;
		}

		/**
		 * @deprecated As of Bunyan (6.0.x)
		 */
		@Deprecated
		public HttpSessionContext getSessionContext() {
			return null;
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		public Object getValue(String name) {
			return getAttribute(name);
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		public String[] getValueNames() {
			List<String> names = ListUtil.fromEnumeration(getAttributeNames());

			return names.toArray(new String[0]);
		}

		@Override
		public void invalidate() {
			_attributes.clear();
		}

		@Override
		public boolean isNew() {
			return _new;
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		public void putValue(String name, Object value) {
			setAttribute(name, value);
		}

		@Override
		public void removeAttribute(String name) {
			_attributes.remove(name);
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		public void removeValue(String name) {
			removeAttribute(name);
		}

		@Override
		public void setAttribute(String name, Object value) {
			_attributes.put(name, value);
		}

		@Override
		public void setMaxInactiveInterval(int maxInactiveInterval) {
			_maxInactiveInterval = maxInactiveInterval;
		}

		private final Map<String, Object> _attributes = new HashMap<>();
		private final long _creationTime;
		private final String _id;
		private final long _lastAccessedTime;
		private int _maxInactiveInterval;
		private final boolean _new;
		private final ServletContext _servletContext;

	}

}