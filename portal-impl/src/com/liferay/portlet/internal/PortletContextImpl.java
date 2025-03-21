/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;

import jakarta.portlet.PortletRequestDispatcher;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Brett Randall
 * @author Neil Griffin
 */
public class PortletContextImpl implements LiferayPortletContext {

	public PortletContextImpl(Portlet portlet, ServletContext servletContext) {
		_portlet = portlet;
		_servletContext = servletContext;

		_servletContextName = GetterUtil.getString(
			servletContext.getServletContextName());
	}

	@Override
	public Object getAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return _servletContext.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return _servletContext.getAttributeNames();
	}

	@Override
	public ClassLoader getClassLoader() {
		return _servletContext.getClassLoader();
	}

	@Override
	public Enumeration<String> getContainerRuntimeOptions() {
		return Collections.enumeration(_supportedRuntimeOptions);
	}

	@Override
	public String getContextPath() {
		return _servletContext.getContextPath();
	}

	@Override
	public int getEffectiveMajorVersion() {
		PortletApp portletApp = _portlet.getPortletApp();

		return portletApp.getSpecMajorVersion();
	}

	@Override
	public int getEffectiveMinorVersion() {
		PortletApp portletApp = _portlet.getPortletApp();

		return portletApp.getSpecMinorVersion();
	}

	@Override
	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return _servletContext.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return _servletContext.getInitParameterNames();
	}

	@Override
	public int getMajorVersion() {
		return _MAJOR_VERSION;
	}

	@Override
	public String getMimeType(String file) {
		return _servletContext.getMimeType(file);
	}

	@Override
	public int getMinorVersion() {
		return _MINOR_VERSION;
	}

	@Override
	public PortletRequestDispatcher getNamedDispatcher(String name) {
		RequestDispatcher requestDispatcher = null;

		try {
			requestDispatcher = _servletContext.getNamedDispatcher(name);
		}
		catch (Throwable throwable) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get request dispatcher for name " + name,
					throwable);
			}

			return null;
		}

		if (requestDispatcher == null) {
			return null;
		}

		return new PortletRequestDispatcherImpl(requestDispatcher, true, this);
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletContextName() {
		return _servletContextName;
	}

	@Override
	public String getRealPath(String path) {
		return _servletContext.getRealPath(path);
	}

	@Override
	public PortletRequestDispatcher getRequestDispatcher(String path) {
		RequestDispatcher requestDispatcher = null;

		try {
			requestDispatcher = _servletContext.getRequestDispatcher(path);
		}
		catch (Throwable throwable) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get request dispatcher for path " + path,
					throwable);
			}

			return null;
		}

		if (requestDispatcher == null) {
			return null;
		}

		return new PortletRequestDispatcherImpl(
			requestDispatcher, false, this, path);
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		if ((path == null) || !path.startsWith(StringPool.SLASH)) {
			throw new MalformedURLException();
		}

		return _servletContext.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return _servletContext.getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return _servletContext.getResourcePaths(path);
	}

	@Override
	public String getServerInfo() {
		return ReleaseInfo.getServerInfo();
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	public boolean isWARFile() {
		PortletApp portletApp = _portlet.getPortletApp();

		return portletApp.isWARFile();
	}

	@Override
	public void log(String msg) {
		if (_log.isInfoEnabled()) {
			_log.info(msg);
		}
	}

	@Override
	public void log(String msg, Throwable throwable) {
		if (_log.isInfoEnabled()) {
			_log.info(msg, throwable);
		}
	}

	@Override
	public void removeAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		_servletContext.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		_servletContext.setAttribute(name, object);
	}

	private static final int _MAJOR_VERSION = 3;

	private static final int _MINOR_VERSION = 0;

	private static final Log _log = LogFactoryUtil.getLog(
		PortletContextImpl.class);

	private static final Set<String> _supportedRuntimeOptions = new HashSet<>(
		Arrays.asList(
			LiferayPortletConfig.RUNTIME_OPTION_ESCAPE_XML,
			LiferayPortletConfig.RUNTIME_OPTION_PORTAL_CONTEXT));

	private final Portlet _portlet;
	private final ServletContext _servletContext;
	private final String _servletContextName;

}