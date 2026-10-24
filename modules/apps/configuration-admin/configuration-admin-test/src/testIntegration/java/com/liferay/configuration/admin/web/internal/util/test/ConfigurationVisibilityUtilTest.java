/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class ConfigurationVisibilityUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle1 = FrameworkUtil.getBundle(
			ConfigurationVisibilityUtilTest.class);

		_bundleContext = bundle1.getBundleContext();

		Bundle bundle2 = BundleUtil.getBundle(
			_bundleContext, "com.liferay.configuration.admin.web");

		_configurationVisibilityUtilClass = bundle2.loadClass(
			"com.liferay.configuration.admin.web.internal.util." +
				"ConfigurationVisibilityUtil");
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testIsVisibleWhenVisibilityControllerHidesConfiguration() {
		_registerConfigurationVisibilityController(false);

		Assert.assertFalse(_isVisible(_PID));
		Assert.assertFalse(_isVisible(_getFactoryInstancePid()));
	}

	@Test
	public void testIsVisibleWhenVisibilityControllerShowsConfiguration() {
		_registerConfigurationVisibilityController(true);

		Assert.assertTrue(_isVisible(_PID));
		Assert.assertTrue(_isVisible(_getFactoryInstancePid()));
	}

	private String _getFactoryInstancePid() {
		return _PID + StringPool.TILDE + RandomTestUtil.randomString();
	}

	private boolean _isVisible(String pid) {
		return ReflectionTestUtil.invoke(
			_configurationVisibilityUtilClass, "isVisible",
			new Class<?>[] {
				String.class, ExtendedObjectClassDefinition.Scope.class,
				Serializable.class
			},
			pid, ExtendedObjectClassDefinition.Scope.SYSTEM, null);
	}

	private void _registerConfigurationVisibilityController(boolean visible) {
		_serviceRegistration = _bundleContext.registerService(
			ConfigurationVisibilityController.class,
			(scope, scopePK) -> visible,
			HashMapDictionaryBuilder.<String, Object>put(
				"configuration.pid", _PID
			).build());
	}

	private static final String _PID = RandomTestUtil.randomString();

	private BundleContext _bundleContext;
	private Class<?> _configurationVisibilityUtilClass;
	private ServiceRegistration<ConfigurationVisibilityController>
		_serviceRegistration;

}