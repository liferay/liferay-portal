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
	}

	@Override
	public <T extends EventListener> void addListener(T eventListener) {
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
	public <T extends Filter> T createFilter(Class<T> filterClass)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> T createListener(
			Class<T> eventListenerClass)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> servletClass)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void declareRoles(String... roleNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAttribute(String name) {
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
	public FilterRegistration getFilterRegistration(String filterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInitParameter(String name) {
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
	public RequestDispatcher getNamedDispatcher(String servletName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestCharacterEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
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
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to open resource: " + path, ioException);
			}

			return null;
		}
	}

	@Override
	public String getResponseCharacterEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServerInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServletContextName() {
		return _bundle.getSymbolicName();
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends ServletRegistration>
		getServletRegistrations() {

		throw new UnsupportedOperationException();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getSessionTimeout() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getVirtualServerName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String message, Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRequestCharacterEncoding(String encoding) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResponseCharacterEncoding(String encoding) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSessionTimeout(int sessionTimeout) {
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