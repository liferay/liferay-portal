/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.internal.configuration.VulcanCompanyConfiguration;
import com.liferay.portal.vulcan.internal.configuration.VulcanConfiguration;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Shuyang Zhou
 */
public class ConfigurationUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetExcludedOperationIds() {
		Configuration systemConfiguration = _createConfiguration(
			"getSystem", VulcanConfiguration.class);

		Assert.assertEquals(
			Collections.singleton("getCompany"),
			_getExcludedOperationIds(
				systemConfiguration,
				_createConfiguration(
					"getCompany", VulcanCompanyConfiguration.class)));
		Assert.assertEquals(
			Collections.singleton("getSystem"),
			_getExcludedOperationIds(systemConfiguration));
	}

	private Configuration _createConfiguration(
		String excludedOperationIds, Class<?> factoryClass) {

		return (Configuration)ProxyUtil.newProxyInstance(
			ConfigurationUtilTest.class.getClassLoader(),
			new Class<?>[] {Configuration.class},
			(proxy, method, args) -> {
				String methodName = method.getName();

				if (methodName.equals("getFactoryPid")) {
					return factoryClass.getName();
				}

				if (methodName.equals("getProperties")) {
					return HashMapDictionaryBuilder.<String, Object>put(
						"excludedOperationIds", excludedOperationIds
					).build();
				}

				throw new UnsupportedOperationException(methodName);
			});
	}

	private Set<String> _getExcludedOperationIds(
		Configuration... configurations) {

		return ConfigurationUtil.getExcludedOperationIds(
			RandomTestUtil.randomLong(),
			(ConfigurationAdmin)ProxyUtil.newProxyInstance(
				ConfigurationUtilTest.class.getClassLoader(),
				new Class<?>[] {ConfigurationAdmin.class},
				(proxy, method, args) -> {
					String methodName = method.getName();

					if (methodName.equals("listConfigurations")) {
						return configurations;
					}

					throw new UnsupportedOperationException(methodName);
				}),
			RandomTestUtil.randomString());
	}

}