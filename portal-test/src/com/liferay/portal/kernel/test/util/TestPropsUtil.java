/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class TestPropsUtil {

	public static String get(String key) {
		return _testPropsUtil._get(key);
	}

	public static Properties getProperties() {
		return _testPropsUtil._props;
	}

	public static void printProperties() {
		_testPropsUtil._printProperties(true);
	}

	public static void set(String key, String value) {
		_testPropsUtil._set(key, value);
	}

	private TestPropsUtil() {
		try (InputStream inputStream = TestPropsUtil.class.getResourceAsStream(
				"/test-portal-impl.properties")) {

			_props.load(inputStream);
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"/test-portal-impl-ext.properties")) {

			if (inputStream != null) {
				_props.load(inputStream);
			}
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}

		_printProperties(false);
	}

	private String _get(String key) {
		return _props.getProperty(key);
	}

	private void _printProperties(boolean update) {
		List<String> keys = Collections.list(
			(Enumeration<String>)_props.propertyNames());

		keys = ListUtil.sort(keys);

		if (update) {
			System.out.println("-- updated properties --");
		}
		else {
			System.out.println("-- listing properties --");
		}

		for (String key : keys) {
			if (!_doNotPrintKeys.contains(key)) {
				System.out.println(key + "=" + _props.getProperty(key));
			}
		}

		System.out.println("");
	}

	private void _set(String key, String value) {
		_props.setProperty(key, value);
	}

	private static final Set<String> _doNotPrintKeys = SetUtil.fromArray(
		"digital.signature.account.base.uri", "digital.signature.api.accountId",
		"digital.signature.api.username", "digital.signature.integration.key",
		"digital.signature.rsa.private.key",
		"digital.signature.site.settings.strategy",
		"object.storage.salesforce.consumer.key",
		"object.storage.salesforce.consumer.secret",
		"object.storage.salesforce.login.url",
		"object.storage.salesforce.password",
		"object.storage.salesforce.username",
		"object.storage.sugarcrm.access.token.url",
		"object.storage.sugarcrm.base.url", "object.storage.sugarcrm.client.id",
		"object.storage.sugarcrm.grant.type",
		"object.storage.sugarcrm.password", "object.storage.sugarcrm.username",
		"vertex.ai.project.id");
	private static final TestPropsUtil _testPropsUtil = new TestPropsUtil();

	private final Properties _props = new Properties();

}