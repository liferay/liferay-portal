/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.util;

import jakarta.portlet.PortletContext;

import jakarta.servlet.ServletContext;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.Enumeration;
import java.util.function.Function;

/**
 * @author Shuyang Zhou
 */
public class ContextResourcePathsUtil {

	public static <T> T visitResources(
		PortletContext portletContext, String path, String filePattern,
		Function<Enumeration<URL>, T> function) {

		return _visitResources(
			portletContext.getAttribute("osgi-bundlecontext"), path,
			filePattern, function);
	}

	public static <T> T visitResources(
		ServletContext servletContext, String path, String filePattern,
		Function<Enumeration<URL>, T> function) {

		return _visitResources(
			servletContext.getAttribute("osgi-bundlecontext"), path,
			filePattern, function);
	}

	private static <T> T _visitResources(
		Object bundleContext, String path, String filePattern,
		Function<Enumeration<URL>, T> function) {

		if (bundleContext == null) {
			return null;
		}

		Class<?> clazz = bundleContext.getClass();

		try {
			Method method = clazz.getMethod("getBundle");

			Object bundle = method.invoke(bundleContext);

			clazz = bundle.getClass();

			method = clazz.getMethod(
				"findEntries", String.class, String.class, boolean.class);

			return function.apply(
				(Enumeration<URL>)method.invoke(
					bundle, path, filePattern, true));
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			return null;
		}
	}

}