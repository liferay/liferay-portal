/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.registration;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.HttpSessionWrapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.http.runtime.dto.ListenerDTO;

/**
 * @author Dante Wang
 */
public class EventListenerRegistration
	extends Registration<EventListener, ListenerDTO> {

	public EventListenerRegistration(
		List<Class<? extends EventListener>> classes,
		LiferayContextController liferayContextController,
		ListenerDTO listenerDTO, ServiceHolder<EventListener> serviceHolder,
		ServletContext servletContext) {

		super(listenerDTO, serviceHolder.get());

		_classes = classes;
		_liferayContextController = liferayContextController;
		_serviceHolder = serviceHolder;
		_servletContext = servletContext;

		_classLoader = serviceHolder.getBundleClassLoader();
		_eventListenerProxy = (EventListener)ProxyUtil.newProxyInstance(
			EventListenerRegistration.class.getClassLoader(),
			classes.toArray(new Class<?>[0]),
			new EventListenerInvocationHandler());
	}

	@Override
	public synchronized void destroy() {
		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_classLoader)) {

			Set<EventListenerRegistration> listenerRegistrations =
				_liferayContextController.getListenerRegistrations();

			listenerRegistrations.remove(this);

			EventListeners eventListeners =
				_liferayContextController.getEventListeners();

			eventListeners.remove(_classes, this);

			_liferayContextController.ungetServletContextHelper(
				_serviceHolder.getBundle());

			super.destroy();

			if (_classes.contains(HttpSessionAttributeListener.class) ||
				_classes.contains(HttpSessionBindingListener.class) ||
				_classes.contains(HttpSessionListener.class)) {

				Map<String, HttpSessionWrapper> activeSessions =
					_liferayContextController.getActiveSessions();

				for (HttpSessionWrapper httpSessionWrapper :
						activeSessions.values()) {

					httpSessionWrapper.invokeSessionListeners(
						_classes, super.getService());
				}
			}

			if (_classes.contains(ServletContextListener.class)) {
				ServletContextListener servletContextListener =
					(ServletContextListener)super.getService();

				servletContextListener.contextDestroyed(
					new ServletContextEvent(_servletContext));
			}
		}
		finally {
			_serviceHolder.release();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof
				EventListenerRegistration eventListenerRegistration) {

			return Objects.equals(
				eventListenerRegistration.getService(), super.getService());
		}

		return false;
	}

	@Override
	public EventListener getService() {
		return _eventListenerProxy;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(getDTO().serviceId);
	}

	private final List<Class<? extends EventListener>> _classes;
	private final ClassLoader _classLoader;
	private final EventListener _eventListenerProxy;
	private final LiferayContextController _liferayContextController;
	private final ServiceHolder<EventListener> _serviceHolder;
	private final ServletContext _servletContext;

	private class EventListenerInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			try (SafeCloseable safeCloseable =
					ThreadContextClassLoaderUtil.swap(_classLoader)) {

				return method.invoke(
					EventListenerRegistration.super.getService(), args);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getCause();
			}
		}

	}

}