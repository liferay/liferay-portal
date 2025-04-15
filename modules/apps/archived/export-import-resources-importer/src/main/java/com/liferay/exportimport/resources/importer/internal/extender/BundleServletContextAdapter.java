/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.extender;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Michael C. Han
 */
public class BundleServletContextAdapter
	extends ServletContextHelper implements ServletContext {

	public BundleServletContextAdapter(Bundle bundle) {
		super(bundle);

		_bundle = bundle;

		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		_classLoader = bundleWiring.getClassLoader();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(
		String s, Class<? extends Filter> aClass) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(Class<? extends EventListener> eventListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(String s) {
	}

	@Override
	public <T extends EventListener> void addListener(T t) {
	}

	@Override
	public ServletRegistration.Dynamic addServlet(
		String s, Class<? extends Servlet> aClass) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> aClass)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> clazz)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> aClass)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void declareRoles(String... strings) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	@Override
	public ServletContext getContext(String uriPath) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContextPath() {
		return StringPool.SLASH;
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 3;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 0;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public FilterRegistration getFilterRegistration(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInitParameter(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMajorVersion() {
		return 3;
	}

	@Override
	public String getMimeType(String file) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		URL url = getResource(name);

		if (url == null) {
			return null;
		}

		try {
			return url.openStream();
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to open resource: " + name, ioException);
			}

			return null;
		}
	}

	@Override
	public String getServerInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Servlet getServlet(String s) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServletContextName() {
		return _bundle.getSymbolicName();
	}

	@Override
	public Enumeration<String> getServletNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration getServletRegistration(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends ServletRegistration>
		getServletRegistrations() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getVirtualServerName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(Exception exception, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String s, Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String s, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setInitParameter(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSessionTrackingModes(
		Set<SessionTrackingMode> sessionTrackingModes) {

		throw new UnsupportedOperationException();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BundleServletContextAdapter.class);

	private final Bundle _bundle;
	private final ClassLoader _classLoader;

}