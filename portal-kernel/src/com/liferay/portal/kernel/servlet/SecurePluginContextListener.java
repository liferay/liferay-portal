/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpSessionActivationListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Brian Wing Shun Chan
 */
public class SecurePluginContextListener
	extends PluginContextListener
	implements HttpSessionActivationListener, HttpSessionAttributeListener,
			   HttpSessionBindingListener, HttpSessionListener,
			   ServletRequestAttributeListener, ServletRequestListener {

	@Override
	public void attributeAdded(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		if (_httpSessionAttributeListeners == null) {
			return;
		}

		for (HttpSessionAttributeListener httpSessionAttributeListener :
				_httpSessionAttributeListeners) {

			httpSessionAttributeListener.attributeAdded(
				httpSessionBindingEvent);
		}
	}

	@Override
	public void attributeAdded(
		ServletRequestAttributeEvent servletRequestAttributeEvent) {

		if (_servletRequestAttributeListeners == null) {
			return;
		}

		for (ServletRequestAttributeListener servletRequestAttributeListener :
				_servletRequestAttributeListeners) {

			servletRequestAttributeListener.attributeAdded(
				servletRequestAttributeEvent);
		}
	}

	@Override
	public void attributeRemoved(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		if (_httpSessionAttributeListeners == null) {
			return;
		}

		for (HttpSessionAttributeListener httpSessionAttributeListener :
				_httpSessionAttributeListeners) {

			httpSessionAttributeListener.attributeRemoved(
				httpSessionBindingEvent);
		}
	}

	@Override
	public void attributeRemoved(
		ServletRequestAttributeEvent servletRequestAttributeEvent) {

		if (_servletRequestAttributeListeners == null) {
			return;
		}

		for (ServletRequestAttributeListener servletRequestAttributeListener :
				_servletRequestAttributeListeners) {

			servletRequestAttributeListener.attributeRemoved(
				servletRequestAttributeEvent);
		}
	}

	@Override
	public void attributeReplaced(
		HttpSessionBindingEvent httpSessionBindingEvent) {

		if (_httpSessionAttributeListeners == null) {
			return;
		}

		for (HttpSessionAttributeListener httpSessionAttributeListener :
				_httpSessionAttributeListeners) {

			httpSessionAttributeListener.attributeReplaced(
				httpSessionBindingEvent);
		}
	}

	@Override
	public void attributeReplaced(
		ServletRequestAttributeEvent servletRequestAttributeEvent) {

		if (_servletRequestAttributeListeners == null) {
			return;
		}

		for (ServletRequestAttributeListener servletRequestAttributeListener :
				_servletRequestAttributeListeners) {

			servletRequestAttributeListener.attributeReplaced(
				servletRequestAttributeEvent);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();

		servletContext.setAttribute(
			SecurePluginContextListener.class.getName(), this);

		super.contextInitialized(servletContextEvent);
	}

	public void instantiatingListeners() throws Exception {
		if (_servletRequestListeners != null) {
			return;
		}

		String[] listenerClassNames = StringUtil.split(
			servletContext.getInitParameter("portalListenerClasses"));

		for (String listenerClassName : listenerClassNames) {
			instantiatingListener(listenerClassName);
		}
	}

	@Override
	public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
		if (_servletRequestListeners == null) {
			return;
		}

		for (ServletRequestListener servletRequestListener :
				_servletRequestListeners) {

			servletRequestListener.requestDestroyed(servletRequestEvent);
		}
	}

	@Override
	public void requestInitialized(ServletRequestEvent servletRequestEvent) {
		if (_servletRequestListeners == null) {
			return;
		}

		for (ServletRequestListener servletRequestListener :
				_servletRequestListeners) {

			servletRequestListener.requestInitialized(servletRequestEvent);
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionListeners == null) {
			return;
		}

		for (HttpSessionListener httpSessionListener : _httpSessionListeners) {
			httpSessionListener.sessionCreated(httpSessionEvent);
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionListeners == null) {
			return;
		}

		for (HttpSessionListener httpSessionListener : _httpSessionListeners) {
			httpSessionListener.sessionDestroyed(httpSessionEvent);
		}
	}

	@Override
	public void sessionDidActivate(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionActivationListeners == null) {
			return;
		}

		for (HttpSessionActivationListener httpSessionActivationListener :
				_httpSessionActivationListeners) {

			httpSessionActivationListener.sessionDidActivate(httpSessionEvent);
		}
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent httpSessionEvent) {
		if (_httpSessionActivationListeners == null) {
			return;
		}

		for (HttpSessionActivationListener httpSessionActivationListener :
				_httpSessionActivationListeners) {

			httpSessionActivationListener.sessionWillPassivate(
				httpSessionEvent);
		}
	}

	@Override
	public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
		if (_httpSessionBindingListeners == null) {
			return;
		}

		for (HttpSessionBindingListener httpSessionBindingListener :
				_httpSessionBindingListeners) {

			httpSessionBindingListener.valueBound(httpSessionBindingEvent);
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
		if (_httpSessionBindingListeners == null) {
			return;
		}

		for (HttpSessionBindingListener httpSessionBindingListener :
				_httpSessionBindingListeners) {

			httpSessionBindingListener.valueUnbound(httpSessionBindingEvent);
		}
	}

	@Override
	protected void fireUndeployEvent() {
		if (_servletContextListeners != null) {
			ServletContextEvent servletContextEvent = new ServletContextEvent(
				servletContext);

			for (ServletContextListener servletContextListener :
					_servletContextListeners) {

				try {
					servletContextListener.contextDestroyed(
						servletContextEvent);
				}
				catch (Throwable throwable) {
					_log.error(
						StringBundler.concat(
							ClassUtil.getClassName(
								servletContextListener.getClass()),
							" is unable to process a context destroyed event ",
							"for ", servletContext.getServletContextName()),
						throwable);
				}
			}
		}

		super.fireUndeployEvent();
	}

	protected void instantiatingListener(String listenerClassName)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Instantiating " + listenerClassName);
		}

		Object listener = InstanceFactory.newInstance(
			pluginClassLoader, listenerClassName);

		if (listener instanceof HttpSessionActivationListener) {
			if (_httpSessionActivationListeners == null) {
				_httpSessionActivationListeners = new CopyOnWriteArrayList<>();
			}

			_httpSessionActivationListeners.add(
				(HttpSessionActivationListener)listener);
		}

		if (listener instanceof HttpSessionAttributeListener) {
			if (_httpSessionAttributeListeners == null) {
				_httpSessionAttributeListeners = new CopyOnWriteArrayList<>();
			}

			_httpSessionAttributeListeners.add(
				(HttpSessionAttributeListener)listener);
		}

		if (listener instanceof HttpSessionBindingListener) {
			if (_httpSessionBindingListeners == null) {
				_httpSessionBindingListeners = new CopyOnWriteArrayList<>();
			}

			_httpSessionBindingListeners.add(
				(HttpSessionBindingListener)listener);
		}

		if (listener instanceof HttpSessionListener) {
			if (_httpSessionListeners == null) {
				_httpSessionListeners = new CopyOnWriteArrayList<>();
			}

			_httpSessionListeners.add((HttpSessionListener)listener);
		}

		if (listener instanceof ServletContextListener) {
			if (_servletContextListeners == null) {
				_servletContextListeners = new CopyOnWriteArrayList<>();
			}

			ServletContextListener servletContextListener =
				(ServletContextListener)listener;

			_servletContextListeners.add(servletContextListener);

			ServletContextEvent servletContextEvent = new ServletContextEvent(
				servletContext);

			servletContextListener.contextInitialized(servletContextEvent);
		}

		if (listener instanceof ServletRequestAttributeListener) {
			if (_servletRequestAttributeListeners == null) {
				_servletRequestAttributeListeners =
					new CopyOnWriteArrayList<>();
			}

			_servletRequestAttributeListeners.add(
				(ServletRequestAttributeListener)listener);
		}

		if (listener instanceof ServletRequestListener) {
			if (_servletRequestListeners == null) {
				_servletRequestListeners = new CopyOnWriteArrayList<>();
			}

			_servletRequestListeners.add((ServletRequestListener)listener);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecurePluginContextListener.class);

	private List<HttpSessionActivationListener> _httpSessionActivationListeners;
	private List<HttpSessionAttributeListener> _httpSessionAttributeListeners;
	private List<HttpSessionBindingListener> _httpSessionBindingListeners;
	private List<HttpSessionListener> _httpSessionListeners;
	private List<ServletContextListener> _servletContextListeners;
	private List<ServletRequestAttributeListener>
		_servletRequestAttributeListeners;
	private List<ServletRequestListener> _servletRequestListeners;

}