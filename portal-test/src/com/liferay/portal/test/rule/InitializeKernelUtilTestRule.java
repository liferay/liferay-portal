/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AbstractTestRule;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Constructor;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.Description;

/**
 * @author Matthew Tambara
 */
public class InitializeKernelUtilTestRule
	extends AbstractTestRule<Map<String, String>, Map<String, String>> {

	public static final InitializeKernelUtilTestRule INSTANCE =
		new InitializeKernelUtilTestRule();

	@Override
	protected void afterClass(
			Description description, Map<String, String> originalMap)
		throws ReflectiveOperationException {

		_setUpPropsUtil(originalMap);
	}

	@Override
	protected void afterMethod(
			Description description, Map<String, String> originalMap,
			Object target)
		throws ReflectiveOperationException {

		if ((originalMap != null) && !originalMap.isEmpty()) {
			_setUpPropsUtil(originalMap);
		}
	}

	@Override
	protected Map<String, String> beforeClass(Description description)
		throws ReflectiveOperationException {

		_setUpPortalClassLoader();

		Class<?> clazz = description.getTestClass();

		PortalProps portalProps = clazz.getAnnotation(PortalProps.class);

		Map<String, String> originalMap = null;

		if (portalProps == null) {
			_setUpPropsUtil(null);
		}
		else {
			originalMap = _setUpPropsUtil(
				_processProperties(portalProps.properties()));
		}

		_setUpFileUtil();
		_setUpJSONFactoryUtil();

		return originalMap;
	}

	@Override
	protected Map<String, String> beforeMethod(
			Description description, Object target)
		throws Throwable {

		PortalProps portalProps = description.getAnnotation(PortalProps.class);

		if (portalProps == null) {
			return null;
		}

		return _setUpPropsUtil(_processProperties(portalProps.properties()));
	}

	private Map<String, String> _processProperties(String[] properties) {
		if (properties == null) {
			return null;
		}

		Map<String, String> propertyMap = new HashMap<>();

		for (String variable : properties) {
			String[] parts = StringUtil.split(variable, CharPool.EQUAL);

			if (parts.length != 2) {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"Property ", variable,
						" needs to be in \"key=value\" format"));
			}

			propertyMap.put(parts[0], parts[1]);
		}

		return propertyMap;
	}

	private void _setUpFileUtil() throws ClassNotFoundException {
		Thread thread = Thread.currentThread();

		ClassLoader classLoader = thread.getContextClassLoader();

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(
			ReflectionTestUtil.getFieldValue(
				classLoader.loadClass("com.liferay.portal.util.FileImpl"),
				"_fileImpl"));
	}

	private void _setUpJSONFactoryUtil() throws ReflectiveOperationException {
		Thread thread = Thread.currentThread();

		ClassLoader classLoader = thread.getContextClassLoader();

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portal.json.JSONFactoryImpl");

		Constructor<?> constructor = clazz.getDeclaredConstructor();

		jsonFactoryUtil.setJSONFactory((JSONFactory)constructor.newInstance());
	}

	private void _setUpPortalClassLoader() {
		ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();

		if (portalClassLoader == null) {
			PortalClassLoaderUtil.setClassLoader(
				InitializeKernelUtilTestRule.class.getClassLoader());
		}
	}

	private Map<String, String> _setUpPropsUtil(Map<String, String> map)
		throws ReflectiveOperationException {

		if (map == null) {
			return null;
		}

		Map<String, String> originalMap = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			originalMap.put(entry.getKey(), PropsUtil.get(entry.getKey()));

			PropsUtil.set(entry.getKey(), entry.getValue());
		}

		return originalMap;
	}

}