/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.servlet.filters.compoundsessionid.CompoundSessionIdHttpSession;
import com.liferay.portal.kernel.servlet.filters.compoundsessionid.CompoundSessionIdSplitterUtil;

import jakarta.servlet.http.HttpSessionActivationListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * See https://issues.liferay.com/browse/LEP-2299.
 * </p>
 *
 * @author Olaf Fricke
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class PortletSessionListenerManager
	implements HttpSessionActivationListener, HttpSessionAttributeListener,
			   HttpSessionBindingListener, HttpSessionListener {

	public static void addHttpSessionActivationListener(
		HttpSessionActivationListener httpSessionActivationListener) {

		_httpSessionActivationListeners.add(httpSessionActivationListener);
	}

	public static void addHttpSessionAttributeListener(
		HttpSessionAttributeListener httpSessionAttributeListener) {

		_httpSessionAttributeListeners.add(httpSessionAttributeListener);
	}

	public static void addHttpSessionBindingListener(
		HttpSessionBindingListener httpSessionBindingListener) {

		_httpSessionBindingListeners.add(httpSessionBindingListener);
	}

	public static void addHttpSessionListener(
		HttpSessionListener httpSessionListener) {

		_httpSessionListeners.add(httpSessionListener);
	}

	public static void removeHttpSessionActivationListener(
		HttpSessionActivationListener httpSessionActivationListener) {

		_httpSessionActivationListeners.remove(httpSessionActivationListener);
	}

	public static void removeHttpSessionAttributeListener(
		HttpSessionAttributeListener httpSessionAttributeListener) {

		_httpSessionAttributeListeners.remove(httpSessionAttributeListener);
	}

	public static void removeHttpSessionBindingListener(
		HttpSessionBindingListener httpSessionBindingListener) {

		_httpSessionBindingListeners.remove(httpSessionBindingListener);
	}

	public static void removeHttpSessionListener(
		HttpSessionListener httpSessionListener) {

		_httpSessionListeners.remove(httpSessionListener);
	}

	@Override
	public void attributeAdded(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		if (_httpSessionAttributeListeners.isEmpty()) {
			return;
		}

		httpSessionBindingEvent = getHttpSessionBindingEvent(
			httpSessionBindingEvent);

		for (HttpSessionAttributeListener httpSessionAttributeListener :
				_httpSessionAttributeListeners) {

			httpSessionAttributeListener.attributeAdded(
				httpSessionBindingEvent);
		}
	}

	@Override
	public void attributeRemoved(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		if (_httpSessionAttributeListeners.isEmpty()) {
			return;
		}

		httpSessionBindingEvent = getHttpSessionBindingEvent(
			httpSessionBindingEvent);

		for (HttpSessionAttributeListener httpSessionAttributeListener :
				_httpSessionAttributeListeners) {

			httpSessionAttributeListener.attributeRemoved(
				httpSessionBindingEvent);
		}
	}

	@Override
	public void attributeReplaced(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		if (_httpSessionAttributeListeners.isEmpty()) {
			return;
		}

		httpSessionBindingEvent = getHttpSessionBindingEvent(
			httpSessionBindingEvent);

		for (HttpSessionAttributeListener httpSessionAttributeListener :
				_httpSessionAttributeListeners) {

			httpSessionAttributeListener.attributeReplaced(
				httpSessionBindingEvent);
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionListeners.isEmpty()) {
			return;
		}

		httpSessionEvent = getHttpSessionEvent(httpSessionEvent);

		for (HttpSessionListener httpSessionListener : _httpSessionListeners) {
			Class<?> clazz = httpSessionListener.getClass();

			try (SafeCloseable safeCloseable =
					ThreadContextClassLoaderUtil.swap(clazz.getClassLoader())) {

				httpSessionListener.sessionCreated(httpSessionEvent);
			}
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionListeners.isEmpty()) {
			return;
		}

		httpSessionEvent = getHttpSessionEvent(httpSessionEvent);

		for (HttpSessionListener httpSessionListener : _httpSessionListeners) {
			httpSessionListener.sessionDestroyed(httpSessionEvent);
		}
	}

	@Override
	public void sessionDidActivate(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionActivationListeners.isEmpty()) {
			return;
		}

		httpSessionEvent = getHttpSessionEvent(httpSessionEvent);

		for (HttpSessionActivationListener httpSessionActivationListener :
				_httpSessionActivationListeners) {

			httpSessionActivationListener.sessionDidActivate(httpSessionEvent);
		}
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionActivationListeners.isEmpty()) {
			return;
		}

		httpSessionEvent = getHttpSessionEvent(httpSessionEvent);

		for (HttpSessionActivationListener httpSessionActivationListener :
				_httpSessionActivationListeners) {

			httpSessionActivationListener.sessionWillPassivate(
				httpSessionEvent);
		}
	}

	@Override
	public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
		if (_httpSessionBindingListeners.isEmpty()) {
			return;
		}

		httpSessionBindingEvent = getHttpSessionBindingEvent(
			httpSessionBindingEvent);

		for (HttpSessionBindingListener httpSessionBindingListener :
				_httpSessionBindingListeners) {

			httpSessionBindingListener.valueBound(httpSessionBindingEvent);
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
		if (_httpSessionBindingListeners.isEmpty()) {
			return;
		}

		httpSessionBindingEvent = getHttpSessionBindingEvent(
			httpSessionBindingEvent);

		for (HttpSessionBindingListener httpSessionBindingListener :
				_httpSessionBindingListeners) {

			httpSessionBindingListener.valueUnbound(httpSessionBindingEvent);
		}
	}

	protected HttpSessionBindingEvent getHttpSessionBindingEvent(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		if (CompoundSessionIdSplitterUtil.hasSessionDelimiter()) {
			CompoundSessionIdHttpSession compoundSessionIdHttpSession =
				new CompoundSessionIdHttpSession(
					httpSessionBindingEvent.getSession());

			httpSessionBindingEvent = new HttpSessionBindingEvent(
				compoundSessionIdHttpSession, httpSessionBindingEvent.getName(),
				httpSessionBindingEvent.getValue());
		}

		return httpSessionBindingEvent;
	}

	protected HttpSessionEvent getHttpSessionEvent(
		HttpSessionEvent httpSessionEvent) {

		if (CompoundSessionIdSplitterUtil.hasSessionDelimiter()) {
			CompoundSessionIdHttpSession compoundSessionIdHttpSession =
				new CompoundSessionIdHttpSession(httpSessionEvent.getSession());

			httpSessionEvent = new HttpSessionEvent(
				compoundSessionIdHttpSession);
		}

		return httpSessionEvent;
	}

	private static final List<HttpSessionActivationListener>
		_httpSessionActivationListeners = new ArrayList<>();
	private static final List<HttpSessionAttributeListener>
		_httpSessionAttributeListeners = new ArrayList<>();
	private static final List<HttpSessionBindingListener>
		_httpSessionBindingListeners = new ArrayList<>();
	private static final List<HttpSessionListener> _httpSessionListeners =
		new ArrayList<>();

}