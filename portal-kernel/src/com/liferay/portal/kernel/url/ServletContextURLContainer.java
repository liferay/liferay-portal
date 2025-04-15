/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.url;

import com.liferay.petra.reflect.ReflectionUtil;

import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Set;

/**
 * @author Raymond Augé
 */
public class ServletContextURLContainer implements URLContainer {

	public ServletContextURLContainer(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	@Override
	public URL getResource(String name) {
		try {
			return _servletContext.getResource(name);
		}
		catch (MalformedURLException malformedURLException) {
			return ReflectionUtil.throwException(malformedURLException);
		}
	}

	@Override
	public Set<String> getResources(String path) {
		return _servletContext.getResourcePaths(path);
	}

	private final ServletContext _servletContext;

}