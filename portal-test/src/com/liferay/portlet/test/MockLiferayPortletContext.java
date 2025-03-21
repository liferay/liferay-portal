/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletContext;

import jakarta.portlet.PortletRequestDispatcher;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Set;

/**
 * @author David Arques
 * @see com.liferay.portlet.internal.PortletContextImpl
 */
public class MockLiferayPortletContext implements LiferayPortletContext {

	public MockLiferayPortletContext(String path) {
		_path = path;
	}

	@Override
	public Object getAttribute(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

	@Override
	public Enumeration<String> getContainerRuntimeOptions() {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 0;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 0;
	}

	@Override
	public String getInitParameter(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return null;
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public String getMimeType(String path) {
		return null;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public PortletRequestDispatcher getNamedDispatcher(String name) {
		return null;
	}

	@Override
	public Portlet getPortlet() {
		return null;
	}

	@Override
	public String getPortletContextName() {
		return null;
	}

	@Override
	public String getRealPath(String path) {
		return null;
	}

	@Override
	public PortletRequestDispatcher getRequestDispatcher(String path) {
		if (Objects.equals(_path, path)) {
			return new MockPortletRequestDispatcher();
		}

		throw new UnsupportedOperationException();
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return null;
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return null;
	}

	@Override
	public String getServerInfo() {
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public void log(String message) {
	}

	@Override
	public void log(String message, Throwable throwable) {
	}

	@Override
	public void removeAttribute(String name) {
	}

	@Override
	public void setAttribute(String name, Object object) {
	}

	private final String _path;

}