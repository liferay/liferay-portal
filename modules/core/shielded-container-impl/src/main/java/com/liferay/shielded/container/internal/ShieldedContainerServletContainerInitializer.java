/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal;

import com.liferay.shielded.container.Ordered;
import com.liferay.shielded.container.ShieldedContainerInitializer;
import com.liferay.shielded.container.internal.proxy.ServletContextDelegate;
import com.liferay.shielded.container.internal.session.ShieldedContainerHttpSessionListener;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class ShieldedContainerServletContainerInitializer
	implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
		throws ServletException {

		ClassLoader shieldedContainerClassLoader =
			_buildShieldContainerClassLoader(servletContext);

		servletContext = ServletContextDelegate.create(
			shieldedContainerClassLoader, servletContext);

		servletContext.addListener(
			new ShieldedContainerHttpSessionListener(servletContext));

		ServiceLoader<ShieldedContainerInitializer> serviceLoader =
			ServiceLoader.load(
				ShieldedContainerInitializer.class,
				shieldedContainerClassLoader);

		List<ShieldedContainerInitializer> shieldedContainerInitializers =
			new ArrayList<>();

		serviceLoader.forEach(shieldedContainerInitializers::add);

		shieldedContainerInitializers.sort(
			(sci1, sci2) -> _getOrder(sci1) - _getOrder(sci2));

		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(shieldedContainerClassLoader);

		try {
			for (ShieldedContainerInitializer shieldedContainerInitializer :
					shieldedContainerInitializers) {

				shieldedContainerInitializer.initialize(servletContext);
			}
		}
		finally {
			currentThread.setContextClassLoader(classLoader);
		}
	}

	private ClassLoader _buildShieldContainerClassLoader(
			ServletContext servletContext)
		throws ServletException {

		List<URL> urls = new ArrayList<>();

		File shieldedContainerLib = new File(
			servletContext.getRealPath(
				ShieldedContainerInitializer.SHIELDED_CONTAINER_LIB));

		try {
			for (File jarFile :
					shieldedContainerLib.listFiles(
						(dir, name) -> {
							String lowercaseName = name.toLowerCase();

							return lowercaseName.endsWith(".jar");
						})) {

				URI uri = jarFile.toURI();

				urls.add(uri.toURL());
			}
		}
		catch (MalformedURLException malformedURLException) {
			throw new ServletException(
				"Unable to convert shielded container lib jar to URL",
				malformedURLException);
		}

		urls.sort(Comparator.comparing(URL::getPath));

		ClassLoader classLoader = new ShieldedContainerClassLoader(
			urls.toArray(new URL[0]), servletContext.getClassLoader());

		servletContext.setAttribute(
			ShieldedContainerClassLoader.NAME, classLoader);

		return classLoader;
	}

	private int _getOrder(Object object) {
		Class<?> clazz = object.getClass();

		Ordered ordered = clazz.getAnnotation(Ordered.class);

		if (ordered == null) {
			return 0;
		}

		return ordered.value();
	}

}