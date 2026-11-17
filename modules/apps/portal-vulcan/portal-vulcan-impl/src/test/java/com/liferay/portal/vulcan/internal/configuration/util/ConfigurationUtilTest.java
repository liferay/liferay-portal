/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.util;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.internal.configuration.VulcanCompanyConfiguration;
import com.liferay.portal.vulcan.internal.configuration.VulcanConfiguration;

import java.util.Collections;
import java.util.Dictionary;
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

	@Test
	public void testIsRESTEnabled() {
		Assert.assertTrue(_isRESTEnabled());

		Configuration systemConfiguration = _createConfiguration(
			false, VulcanConfiguration.class.getName());

		Assert.assertFalse(_isRESTEnabled(systemConfiguration));
		Assert.assertTrue(
			_isRESTEnabled(
				systemConfiguration,
				_createConfiguration(
					true, VulcanCompanyConfiguration.class.getName())));

		systemConfiguration = _createConfiguration(
			true, VulcanConfiguration.class.getName());

		Assert.assertFalse(
			_isRESTEnabled(
				systemConfiguration,
				_createConfiguration(
					false, VulcanCompanyConfiguration.class.getName())));
		Assert.assertFalse(
			_isRESTEnabled(
				systemConfiguration,
				_createConfiguration(
					false,
					VulcanCompanyConfiguration.class.getName() + ".scoped")));

		long companyId = RandomTestUtil.randomLong();
		String path = RandomTestUtil.randomString();

		Assert.assertTrue(_isRESTEnabled(companyId, path, systemConfiguration));

		systemConfiguration = _createConfiguration(
			false, VulcanConfiguration.class.getName());

		Assert.assertTrue(_isRESTEnabled(companyId, path, systemConfiguration));

		ConfigurationUtil.clearRESTEnabledCache(RandomTestUtil.randomString());

		Assert.assertTrue(_isRESTEnabled(companyId, path, systemConfiguration));

		ConfigurationUtil.clearRESTEnabledCache(path);

		Assert.assertFalse(
			_isRESTEnabled(companyId, path, systemConfiguration));
	}

	private Configuration _createConfiguration(
		boolean restEnabled, String factoryPid) {

		return _createConfiguration(
			factoryPid,
			HashMapDictionaryBuilder.<String, Object>put(
				"restEnabled", restEnabled
			).build());
	}

	private Configuration _createConfiguration(
		String excludedOperationIds, Class<?> factoryClass) {

		return _createConfiguration(
			factoryClass.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"excludedOperationIds", excludedOperationIds
			).build());
	}

	private Configuration _createConfiguration(
		String factoryPid, Dictionary<String, Object> properties) {

		return (Configuration)ProxyUtil.newProxyInstance(
			ConfigurationUtilTest.class.getClassLoader(),
			new Class<?>[] {Configuration.class},
			(proxy, method, args) -> {
				String methodName = method.getName();

				if (methodName.equals("getFactoryPid")) {
					return factoryPid;
				}

				if (methodName.equals("getProperties")) {
					return properties;
				}

				throw new UnsupportedOperationException(methodName);
			});
	}

	private ConfigurationAdmin _createConfigurationAdmin(
		Configuration... configurations) {

		return (ConfigurationAdmin)ProxyUtil.newProxyInstance(
			ConfigurationUtilTest.class.getClassLoader(),
			new Class<?>[] {ConfigurationAdmin.class},
			(proxy, method, args) -> {
				String methodName = method.getName();

				if (methodName.equals("listConfigurations")) {
					return configurations;
				}

				throw new UnsupportedOperationException(methodName);
			});
	}

	private Set<String> _getExcludedOperationIds(
		Configuration... configurations) {

		return ConfigurationUtil.getExcludedOperationIds(
			RandomTestUtil.randomLong(),
			_createConfigurationAdmin(configurations),
			RandomTestUtil.randomString());
	}

	private boolean _isRESTEnabled(Configuration... configurations) {
		return _isRESTEnabled(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			configurations);
	}

	private boolean _isRESTEnabled(
		long companyId, String path, Configuration... configurations) {

		return ConfigurationUtil.isRESTEnabled(
			(Company)ProxyUtil.newProxyInstance(
				ConfigurationUtilTest.class.getClassLoader(),
				new Class<?>[] {Company.class},
				(proxy, method, args) -> {
					String methodName = method.getName();

					if (methodName.equals("getCompanyId")) {
						return companyId;
					}

					if (methodName.equals("getWebId")) {
						return RandomTestUtil.randomString();
					}

					throw new UnsupportedOperationException(methodName);
				}),
			_createConfigurationAdmin(configurations), path);
	}

}