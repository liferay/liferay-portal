/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.osgi.web.http.servlet.internal.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayDispatchTargets;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContext;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListeners;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Dante Wang
 */
public class ServletContextWrapper implements ServletContext {

	public ServletContextWrapper(
		Bundle bundle, LiferayContextController liferayContextController,
		ServletContextHelper servletContextHelper,
		ServletContextHelperDataContext servletContextHelperDataContext) {

		_bundle = bundle;
		_liferayContextController = liferayContextController;
		_servletContextHelper = servletContextHelper;
		_servletContextHelperDataContext = servletContextHelperDataContext;

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		_classLoader = bundleWiring.getClassLoader();

		_servletContext = servletContextHelperDataContext.getServletContext();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(
		String filterName, Class<? extends Filter> filterClass) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(
		String filterName, Filter filter) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(
		String filterName, String filterClassName) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration.Dynamic addJspFile(
		String servletName, String jspFile) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(Class<? extends EventListener> eventListenerClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(String eventListenerClassName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> void addListener(T eventListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(
		String servletName, Class<? extends Servlet> servletClass) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(
		String servletName, Servlet servlet) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(
		String servletName, String servletClassName) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> filterClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> T createListener(
		Class<T> eventListenerClass) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> servletClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void declareRoles(String... roleNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ServletContextWrapper)) {
			return false;
		}

		ServletContextWrapper otherServletContextWrapper =
			(ServletContextWrapper)other;

		return _liferayContextController.equals(
			otherServletContextWrapper._liferayContextController);
	}

	@Override
	public Object getAttribute(String name) {
		if (name.equals("osgi-bundlecontext")) {
			return _bundle.getBundleContext();
		}

		Dictionary<String, Object> contextAttributes =
			_servletContextHelperDataContext.getContextAttributes();

		return contextAttributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		Dictionary<String, Object> contextAttributes =
			_servletContextHelperDataContext.getContextAttributes();

		return contextAttributes.keys();
	}

	@Override
	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	@Override
	public ServletContext getContext(String path) {
		return _servletContext.getContext(path);
	}

	@Override
	public String getContextPath() {
		return _liferayContextController.getFullContextPath();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return _servletContext.getDefaultSessionTrackingModes();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return _servletContext.getEffectiveMajorVersion();
	}

	@Override
	public int getEffectiveMinorVersion() {
		return _servletContext.getEffectiveMinorVersion();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return _servletContext.getEffectiveSessionTrackingModes();
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		return _servletContext.getFilterRegistration(filterName);
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return _servletContext.getFilterRegistrations();
	}

	@Override
	public String getInitParameter(String name) {
		Map<String, String> initParams =
			_liferayContextController.getInitParams();

		return initParams.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		Map<String, String> initParams =
			_liferayContextController.getInitParams();

		return Collections.enumeration(initParams.keySet());
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return null;
	}

	@Override
	public int getMajorVersion() {
		return _servletContext.getMajorVersion();
	}

	@Override
	public String getMimeType(String file) {
		String mimeType = _servletContextHelper.getMimeType(file);

		if (mimeType != null) {
			return mimeType;
		}

		return _servletContext.getMimeType(file);
	}

	@Override
	public int getMinorVersion() {
		return _servletContext.getMinorVersion();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String servletName) {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayContextController.getDispatchTargets(
				servletName, null, null, null, null, null, Match.EXACT);

		if (liferayDispatchTargets == null) {
			return null;
		}

		return new RequestDispatcherImpl(liferayDispatchTargets, servletName);
	}

	@Override
	public String getRealPath(String path) {
		return _servletContextHelper.getRealPath(path);
	}

	@Override
	public String getRequestCharacterEncoding() {
		return _servletContext.getRequestCharacterEncoding();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		if (!path.startsWith(StringPool.SLASH)) {
			return null;
		}

		String fullContextPath = _liferayContextController.getFullContextPath();

		if (path.startsWith(fullContextPath)) {
			path = path.substring(fullContextPath.length());
		}

		LiferayDispatchTargets liferayDispatchTargets =
			_liferayContextController.getDispatchTargets(path);

		if (liferayDispatchTargets == null) {
			return null;
		}

		return new RequestDispatcherImpl(liferayDispatchTargets, path);
	}

	@Override
	public URL getResource(String path) {
		return _servletContextHelper.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		URL url = getResource(path);

		if (url == null) {
			return null;
		}

		try {
			return url.openStream();
		}
		catch (IOException ioException) {
			_servletContext.log(ioException.getMessage(), ioException);
		}

		return null;
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		if ((path == null) || !path.startsWith(StringPool.SLASH)) {
			return null;
		}

		return _servletContextHelper.getResourcePaths(path);
	}

	@Override
	public String getResponseCharacterEncoding() {
		return _servletContext.getResponseCharacterEncoding();
	}

	@Override
	public String getServerInfo() {
		return _servletContext.getServerInfo();
	}

	@Override
	public String getServletContextName() {
		return _liferayContextController.getContextName();
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		return _servletContext.getServletRegistration(servletName);
	}

	@Override
	public Map<String, ? extends ServletRegistration>
		getServletRegistrations() {

		return _servletContext.getServletRegistrations();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return _servletContext.getSessionCookieConfig();
	}

	@Override
	public int getSessionTimeout() {
		return _servletContext.getSessionTimeout();
	}

	@Override
	public String getVirtualServerName() {
		return _servletContext.getVirtualServerName();
	}

	@Override
	public int hashCode() {
		return _liferayContextController.hashCode();
	}

	@Override
	public void log(String message) {
		_servletContext.log(message);
	}

	@Override
	public void log(String message, Throwable throwable) {
		_servletContext.log(message, throwable);
	}

	@Override
	public void removeAttribute(String name) {
		Dictionary<String, Object> contextAttributes =
			_servletContextHelperDataContext.getContextAttributes();

		Object value = contextAttributes.remove(name);

		_fireServletContextAttributeEvent(
			ServletContextAttributeListener::attributeRemoved, name, value);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (value == null) {
			removeAttribute(name);

			return;
		}

		Dictionary<String, Object> contextAttributes =
			_servletContextHelperDataContext.getContextAttributes();

		Object oldValue = contextAttributes.put(name, value);

		if (oldValue == null) {
			_fireServletContextAttributeEvent(
				ServletContextAttributeListener::attributeAdded, name, value);
		}
		else {
			_fireServletContextAttributeEvent(
				ServletContextAttributeListener::attributeReplaced, name,
				value);
		}
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		return _servletContext.setInitParameter(name, value);
	}

	@Override
	public void setRequestCharacterEncoding(String encoding) {
		_servletContext.setRequestCharacterEncoding(encoding);
	}

	@Override
	public void setResponseCharacterEncoding(String encoding) {
		_servletContext.setResponseCharacterEncoding(encoding);
	}

	@Override
	public void setSessionTimeout(int sessionTimeout) {
		_servletContext.setSessionTimeout(sessionTimeout);
	}

	@Override
	public void setSessionTrackingModes(
		Set<SessionTrackingMode> sessionTrackingModes) {

		_servletContext.setSessionTrackingModes(sessionTrackingModes);
	}

	private void _fireServletContextAttributeEvent(
		BiConsumer
			<ServletContextAttributeListener, ServletContextAttributeEvent>
				biConsumer,
		String name, Object value) {

		EventListeners eventListeners =
			_liferayContextController.getEventListeners();

		List<ServletContextAttributeListener> servletContextAttributeListeners =
			eventListeners.get(ServletContextAttributeListener.class);

		if (servletContextAttributeListeners.isEmpty()) {
			return;
		}

		ServletContextAttributeEvent servletContextAttributeEvent =
			new ServletContextAttributeEvent(this, name, value);

		for (ServletContextAttributeListener servletContextAttributeListener :
				servletContextAttributeListeners) {

			biConsumer.accept(
				servletContextAttributeListener, servletContextAttributeEvent);
		}
	}

	private final Bundle _bundle;
	private final ClassLoader _classLoader;
	private final LiferayContextController _liferayContextController;
	private final ServletContext _servletContext;
	private final ServletContextHelper _servletContextHelper;
	private final ServletContextHelperDataContext
		_servletContextHelperDataContext;

}