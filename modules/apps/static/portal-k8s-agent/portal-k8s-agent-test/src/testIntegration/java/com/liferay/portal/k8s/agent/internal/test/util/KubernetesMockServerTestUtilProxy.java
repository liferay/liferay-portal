/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.test.util;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shuyang Zhou
 */
public class KubernetesMockServerTestUtilProxy {

	public KubernetesMockServerTestUtilProxy(Class<?> seedClass)
		throws ClassNotFoundException {

		ClassLoader classLoader = _createClassLoader(
			seedClass.getClassLoader());

		_kubernetesMockServerTestUtilClass = classLoader.loadClass(
			KubernetesMockServerTestUtil.class.getName());
	}

	public void createOrReplaceConfigMap(String json) {
		ReflectionTestUtil.invoke(
			_kubernetesMockServerTestUtilClass, "createOrReplaceConfigMap",
			new Class<?>[] {String.class}, json);
	}

	public String getHostName() {
		return ReflectionTestUtil.invoke(
			_kubernetesMockServerTestUtilClass, "getHostName", new Class<?>[0]);
	}

	public int getPort() {
		return ReflectionTestUtil.invoke(
			_kubernetesMockServerTestUtilClass, "getPort", new Class<?>[0]);
	}

	public void start() {
		ReflectionTestUtil.invoke(
			_kubernetesMockServerTestUtilClass, "start", new Class<?>[0]);
	}

	public void stop() {
		ReflectionTestUtil.invoke(
			_kubernetesMockServerTestUtilClass, "stop", new Class<?>[0]);
	}

	public String waitForConfigMap(
		String name, Predicate<Map<String, String>> dataPredicate, long amount,
		TimeUnit timeUnit) {

		return ReflectionTestUtil.invoke(
			_kubernetesMockServerTestUtilClass, "waitForConfigMap",
			new Class<?>[] {
				String.class, Predicate.class, long.class, TimeUnit.class
			},
			name, dataPredicate, amount, timeUnit);
	}

	private ClassLoader _createClassLoader(ClassLoader parentClassLoader) {
		Bundle bundle = FrameworkUtil.getBundle(
			KubernetesMockServerTestUtilProxy.class);

		return new ClassLoader(parentClassLoader) {

			@Override
			protected Class<?> findClass(String name)
				throws ClassNotFoundException {

				URL url = bundle.getEntry(
					StringUtil.replace(name, CharPool.PERIOD, CharPool.SLASH) +
						".class");

				if (url == null) {
					ClassLoader classLoader =
						KubernetesMockServerTestUtilProxy.class.
							getClassLoader();

					return classLoader.loadClass(name);
				}

				try (InputStream inputStream = url.openStream()) {
					byte[] bytes = inputStream.readAllBytes();

					return defineClass(name, bytes, 0, bytes.length);
				}
				catch (IOException ioException) {
					throw new ClassNotFoundException(name, ioException);
				}
			}

		};
	}

	private final Class<?> _kubernetesMockServerTestUtilClass;

}