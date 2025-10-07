/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListeners;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.List;

/**
 * @author Dante Wang
 */
public class HttpSessionWrapper implements HttpSession, Serializable {

	public static HttpSessionWrapper createHttpSessionWrapper(
		HttpSession httpSession,
		LiferayContextController liferayContextController,
		ServletContext servletContext) {

		HttpSessionWrapper httpSessionWrapper = new HttpSessionWrapper(
			httpSession, liferayContextController, servletContext);

		HttpSessionTracker.addHttpSessionWrapper(httpSessionWrapper);

		return httpSessionWrapper;
	}

	@Override
	public Object getAttribute(String name) {
		return _httpSession.getAttribute(_attributePrefix.concat(name));
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(_getAttributeNames());
	}

	public LiferayContextController getContextController() {
		return _liferayContextController;
	}

	@Override
	public long getCreationTime() {
		return _httpSession.getCreationTime();
	}

	@Override
	public String getId() {
		return _id;
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
		return _servletContext;
	}

	@Override
	public void invalidate() {
		HttpSessionEvent httpSessionEvent = new HttpSessionEvent(this);

		EventListeners eventListeners =
			_liferayContextController.getEventListeners();

		for (HttpSessionListener listener :
				eventListeners.get(HttpSessionListener.class)) {

			try {
				listener.sessionDestroyed(httpSessionEvent);
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}
			}
		}

		try {
			for (String attributeName : _getAttributeNames()) {
				removeAttribute(attributeName);
			}
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}
		}

		try {
			HttpSessionTracker.removeHttpSessionWrapper(this);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}
		}

		_liferayContextController.removeActiveSession(_id);
	}

	public void invokeSessionListeners(
		List<Class<? extends EventListener>> classes,
		EventListener eventListener) {

		if (classes == null) {
			return;
		}

		for (Class<? extends EventListener> clazz : classes) {
			if (clazz.equals(HttpSessionListener.class)) {
				HttpSessionEvent sessionEvent = new HttpSessionEvent(this);

				HttpSessionListener httpSessionListener =
					(HttpSessionListener)eventListener;

				httpSessionListener.sessionDestroyed(sessionEvent);
			}

			if (clazz.equals(HttpSessionBindingListener.class) ||
				clazz.equals(HttpSessionAttributeListener.class)) {

				Enumeration<String> attributeNameEnumeration =
					getAttributeNames();

				while (attributeNameEnumeration.hasMoreElements()) {
					String attributeName =
						attributeNameEnumeration.nextElement();

					HttpSessionBindingEvent sessionBindingEvent =
						new HttpSessionBindingEvent(this, attributeName);

					if (clazz.equals(HttpSessionAttributeListener.class)) {
						HttpSessionAttributeListener
							httpSessionAttributeListener =
								(HttpSessionAttributeListener)eventListener;

						httpSessionAttributeListener.attributeRemoved(
							sessionBindingEvent);
					}
					else if (clazz.equals(HttpSessionBindingListener.class)) {
						HttpSessionBindingListener httpSessionBindingListener =
							(HttpSessionBindingListener)eventListener;

						httpSessionBindingListener.valueUnbound(
							sessionBindingEvent);
					}
				}
			}
		}
	}

	@Override
	public boolean isNew() {
		return _httpSession.isNew();
	}

	@Override
	public void removeAttribute(String name) {
		String attributeName = _attributePrefix.concat(name);

		Object value = _httpSession.getAttribute(attributeName);

		_httpSession.removeAttribute(attributeName);

		if (value == null) {
			return;
		}

		EventListeners eventListeners =
			_liferayContextController.getEventListeners();

		List<HttpSessionAttributeListener> httpSessionAttributeListeners =
			eventListeners.get(HttpSessionAttributeListener.class);

		if (!httpSessionAttributeListeners.isEmpty()) {
			HttpSessionBindingEvent httpSessionBindingEvent =
				new HttpSessionBindingEvent(this, attributeName);

			for (HttpSessionAttributeListener httpSessionAttributeListener :
					httpSessionAttributeListeners) {

				httpSessionAttributeListener.attributeRemoved(
					httpSessionBindingEvent);
			}
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		String attributeName = _attributePrefix.concat(name);

		if (value == null) {
			_httpSession.setAttribute(attributeName, null);

			return;
		}

		boolean attributeAdded = false;

		if (_httpSession.getAttribute(attributeName) == null) {
			attributeAdded = true;
		}

		_httpSession.setAttribute(attributeName, value);

		EventListeners eventListeners =
			_liferayContextController.getEventListeners();

		List<HttpSessionAttributeListener> httpSessionAttributeListeners =
			eventListeners.get(HttpSessionAttributeListener.class);

		if (httpSessionAttributeListeners.isEmpty()) {
			return;
		}

		HttpSessionBindingEvent httpSessionBindingEvent =
			new HttpSessionBindingEvent(this, attributeName, value);

		for (HttpSessionAttributeListener httpSessionAttributeListener :
				httpSessionAttributeListeners) {

			if (attributeAdded) {
				httpSessionAttributeListener.attributeAdded(
					httpSessionBindingEvent);
			}
			else {
				httpSessionAttributeListener.attributeReplaced(
					httpSessionBindingEvent);
			}
		}
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		_httpSession.setMaxInactiveInterval(maxInactiveInterval);
	}

	@Override
	public String toString() {
		String value = _toString;

		if (value == null) {
			value = StringBundler.concat(
				_SIMPLE_NAME, CharPool.OPEN_BRACKET, _httpSession.getId(),
				StringPool.COMMA_AND_SPACE, _attributePrefix,
				CharPool.CLOSE_BRACKET);

			_toString = value;
		}

		return value;
	}

	private HttpSessionWrapper(
		HttpSession httpSession,
		LiferayContextController liferayContextController,
		ServletContext servletContext) {

		_httpSession = httpSession;
		_liferayContextController = liferayContextController;
		_servletContext = servletContext;

		_attributePrefix = "equinox.http.".concat(
			liferayContextController.getContextName());
		_id = httpSession.getId();
	}

	private Collection<String> _getAttributeNames() {
		Collection<String> attributeNames = new ArrayList<>();

		Enumeration<String> attributeNameEnumeration =
			_httpSession.getAttributeNames();

		while (attributeNameEnumeration.hasMoreElements()) {
			String attribute = attributeNameEnumeration.nextElement();

			if (attribute.startsWith(_attributePrefix)) {
				attributeNames.add(
					attribute.substring(_attributePrefix.length()));
			}
		}

		return attributeNames;
	}

	private static final String _SIMPLE_NAME =
		HttpSessionWrapper.class.getSimpleName();

	private static final Log _log = LogFactoryUtil.getLog(
		HttpSessionWrapper.class);

	private static final long serialVersionUID = 3418610936889860782L;

	private final transient String _attributePrefix;
	private final transient HttpSession _httpSession;
	private final String _id;
	private final transient LiferayContextController _liferayContextController;
	private final transient ServletContext _servletContext;
	private String _toString;

}