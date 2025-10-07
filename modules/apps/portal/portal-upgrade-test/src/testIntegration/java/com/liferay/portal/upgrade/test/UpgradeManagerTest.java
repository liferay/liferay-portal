/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.util.promise.Promise;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class UpgradeManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_originalUpgradeDatabaseAutoRun = PropsUtil.get(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN);
	}

	@AfterClass
	public static void tearDownClass() {
		PropsUtil.set(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN,
			_originalUpgradeDatabaseAutoRun);
	}

	@After
	public void tearDown() throws Exception {
		Promise<?> promise = _serviceComponentRuntime.disableComponent(
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(_upgradeRecorder.getClass()),
				"com.liferay.portal.upgrade.internal.jmx.UpgradeManager"));

		promise.getValue();

		_upgradeManager = null;
	}

	@Test
	public void testUpgradeManager() throws Exception {
		_restartComponentEnabler(true);

		String originalResult = ReflectionTestUtil.getFieldValue(
			_upgradeRecorder, "_result");
		String originalType = ReflectionTestUtil.getFieldValue(
			_upgradeRecorder, "_type");

		try {
			Assert.assertTrue(_isUpgradeManagerMBeanRegistered());

			Assert.assertEquals(
				originalResult, _upgradeManagerInvoke("getResult"));
			Assert.assertEquals(originalType, _upgradeManagerInvoke("getType"));

			ReflectionTestUtil.setFieldValue(
				_upgradeRecorder, "_result", "testResult");
			ReflectionTestUtil.setFieldValue(
				_upgradeRecorder, "_type", "testType");

			Assert.assertEquals(
				"testResult", _upgradeManagerInvoke("getResult"));
			Assert.assertEquals("testType", _upgradeManagerInvoke("getType"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_upgradeRecorder, "_result", originalResult);
			ReflectionTestUtil.setFieldValue(
				_upgradeRecorder, "_type", originalType);
		}
	}

	@Test
	public void testUpgradeManagerMBeanDisabled() throws Exception {
		_restartComponentEnabler(false);

		Assert.assertFalse(_isUpgradeManagerMBeanRegistered());
	}

	@Test
	public void testUpgradeManagerMBeanEnabled() throws Exception {
		_restartComponentEnabler(true);

		Assert.assertTrue(_isUpgradeManagerMBeanRegistered());
	}

	private boolean _isUpgradeManagerMBeanRegistered() throws Exception {
		ObjectName objectName = new ObjectName(
			"com.liferay.portal.upgrade:classification=upgrade," +
				"name=UpgradeManager");

		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);

			MBeanServer mBeanServer =
				ManagementFactory.getPlatformMBeanServer();

			if (mBeanServer.isRegistered(objectName)) {
				return true;
			}
		}

		return false;
	}

	private void _restartComponentEnabler(boolean upgradeDatabaseAutoRun)
		throws Exception {

		Promise<?> promise = _serviceComponentRuntime.disableComponent(
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(_upgradeRecorder.getClass()),
				"com.liferay.portal.upgrade.internal.component.enabler." +
					"ComponentEnabler"));

		promise.getValue();

		PropsUtil.set(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN,
			String.valueOf(upgradeDatabaseAutoRun));

		promise = _serviceComponentRuntime.enableComponent(
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(_upgradeRecorder.getClass()),
				"com.liferay.portal.upgrade.internal.component.enabler." +
					"ComponentEnabler"));

		promise.getValue();
	}

	private String _upgradeManagerInvoke(String methodName) throws Exception {
		if (_upgradeManager == null) {
			Bundle bundle = FrameworkUtil.getBundle(
				_upgradeRecorder.getClass());

			BundleContext bundleContext = bundle.getBundleContext();

			ServiceReference<?>[] serviceReferences =
				bundleContext.getServiceReferences(
					"javax.management.DynamicMBean",
					"(component.name=com.liferay.portal.upgrade.internal.jmx." +
						"UpgradeManager)");

			_upgradeManager = bundleContext.getService(serviceReferences[0]);
		}

		return ReflectionTestUtil.invoke(
			_upgradeManager, methodName, new Class<?>[0], null);
	}

	private static String _originalUpgradeDatabaseAutoRun;
	private static Object _upgradeManager;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

	@Inject(
		filter = "component.name=com.liferay.portal.upgrade.internal.recorder.UpgradeRecorder",
		type = Inject.NoType.class
	)
	private Object _upgradeRecorder;

}