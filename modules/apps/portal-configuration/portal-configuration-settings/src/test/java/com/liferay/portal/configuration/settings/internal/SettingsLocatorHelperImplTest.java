/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;

/**
 * @author Shuyang Zhou
 */
public class SettingsLocatorHelperImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRegisterConfigurationBeanClass() throws Exception {
		ManagedService managedService = _registerConfigurationBeanClass();

		Assert.assertEquals("value1", _getValue());

		managedService.updated(MapUtil.singletonDictionary("value", "value2"));

		Assert.assertEquals("value1", _getValue());

		managedService.updated(MapUtil.singletonDictionary("value", "value2"));

		Assert.assertEquals("value2", _getValue());
	}

	@Test
	public void testRegisterConfigurationBeanClassWhenChangeCountIsChanged()
		throws Exception {

		ManagedService managedService = _registerConfigurationBeanClass();

		_changeCount.incrementAndGet();

		managedService.updated(MapUtil.singletonDictionary("value", "value2"));

		Assert.assertEquals("value2", _getValue());
	}

	@Meta.OCD(
		id = "com.liferay.portal.configuration.settings.internal.SettingsLocatorHelperImplTest"
	)
	public interface TestConfiguration {

		public String value();

	}

	private String _getValue() {
		Map<String, Settings> configurationBeanSettings =
			ReflectionTestUtil.getFieldValue(
				_settingsLocatorHelperImpl, "_configurationBeanSettings");

		Settings settings = configurationBeanSettings.get(
			SettingsLocatorHelperImplTest.class.getName());

		return settings.getValue("value", null);
	}

	private ManagedService _registerConfigurationBeanClass() {
		Configuration configuration = (Configuration)ProxyUtil.newProxyInstance(
			Configuration.class.getClassLoader(),
			new Class<?>[] {Configuration.class},
			(proxy, method, arguments) -> {
				String methodName = method.getName();

				if (methodName.equals("getChangeCount")) {
					return _changeCount.get();
				}

				if (methodName.equals("getProcessedProperties")) {
					return MapUtil.singletonDictionary("value", "value1");
				}

				return null;
			});

		ReflectionTestUtil.setFieldValue(
			_settingsLocatorHelperImpl, "_configurationAdmin",
			ProxyUtil.newProxyInstance(
				ConfigurationAdmin.class.getClassLoader(),
				new Class<?>[] {ConfigurationAdmin.class},
				(proxy, method, arguments) -> new Configuration[] {
					configuration
				}));

		AtomicReference<ManagedService> managedServiceReference =
			new AtomicReference<>();

		ReflectionTestUtil.setFieldValue(
			_settingsLocatorHelperImpl, "_bundleContext",
			ProxyUtil.newProxyInstance(
				BundleContext.class.getClassLoader(),
				new Class<?>[] {BundleContext.class},
				(proxy, method, arguments) -> {
					if (arguments[0] == ManagedService.class) {
						managedServiceReference.set(
							(ManagedService)arguments[1]);
					}

					return null;
				}));

		ReflectionTestUtil.invoke(
			_settingsLocatorHelperImpl, "_registerConfigurationBeanClass",
			new Class<?>[] {Class.class}, TestConfiguration.class);

		return managedServiceReference.get();
	}

	private final AtomicLong _changeCount = new AtomicLong();
	private final SettingsLocatorHelperImpl _settingsLocatorHelperImpl =
		new SettingsLocatorHelperImpl();

}