/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class UpgradeOSGiCommandsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testExecuteAllWithFailedRegistration() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeOSGiCommandsTest.class);

		String bundleSymbolicName = bundle.getSymbolicName();

		Class<?> upgradeExecutorClass = _upgradeExecutor.getClass();
		Class<?> upgradeOSGiCommandsClass = _upgradeOSGiCommands.getClass();

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_AUTO_RUN", false, false);
			LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				upgradeExecutorClass.getName(), LoggerTestUtil.OFF);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				upgradeOSGiCommandsClass.getName(), LoggerTestUtil.OFF)) {

			_registerFailingUpgradeStepRegistrator(bundle);

			_assertExecuteAll(
				"The following modules had errors while upgrading:\n\t" +
					bundleSymbolicName);

			_serviceRegistration.unregister();

			AtomicInteger registerCount = new AtomicInteger();

			_registerUpgradeStepRegistrator(
				bundle,
				upgradeStepRegistry -> {
					upgradeStepRegistry.register(
						"0.0.0", "1.0.0", new DummyUpgradeStep());

					if (registerCount.incrementAndGet() == 1) {
						throw new IllegalStateException();
					}
				});

			_assertExecuteAll("All modules were successfully upgraded");

			Set<String> failedBundleSymbolicNames = ReflectionTestUtil.invoke(
				_upgradeExecutor, "getFailedBundleSymbolicNames",
				new Class<?>[0]);

			Assert.assertFalse(
				failedBundleSymbolicNames.toString(),
				failedBundleSymbolicNames.contains(bundleSymbolicName));

			_serviceRegistration.unregister();

			_registerFailingUpgradeStepRegistrator(bundle);

			_assertExecuteAll(
				"The following modules had errors while upgrading:\n\t" +
					bundleSymbolicName);
		}
		finally {
			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if (release != null) {
				_releaseLocalService.deleteRelease(release);
			}
		}
	}

	@Test
	public void testExecuteWithFailedRegistration() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeOSGiCommandsTest.class);

		String bundleSymbolicName = bundle.getSymbolicName();

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_AUTO_RUN", false, false)) {

			_registerRecoveringUpgradeStepRegistrator(bundle);

			ReflectionTestUtil.invoke(
				_upgradeOSGiCommands, "execute",
				new Class<?>[] {String.class, String.class}, bundleSymbolicName,
				"1.0.0");

			_assertRecovered(bundleSymbolicName, "1.0.0");

			_serviceRegistration.unregister();

			_registerRecoveringUpgradeStepRegistrator(bundle);

			Class<?> upgradeExecutorClass = _upgradeExecutor.getClass();
			Class<?> upgradeOSGiCommandsClass = _upgradeOSGiCommands.getClass();

			try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
					upgradeExecutorClass.getName(), LoggerTestUtil.OFF);
				LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
					upgradeOSGiCommandsClass.getName(), LoggerTestUtil.OFF)) {

				ReflectionTestUtil.invoke(
					_upgradeOSGiCommands, "execute",
					new Class<?>[] {String.class}, bundleSymbolicName);
			}

			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			Assert.assertEquals("1.0.0", release.getSchemaVersion());

			ReflectionTestUtil.invoke(
				_upgradeOSGiCommands, "execute", new Class<?>[] {String.class},
				bundleSymbolicName);

			_assertRecovered(bundleSymbolicName, "2.0.0");

			_serviceRegistration.unregister();

			_registerFailingUpgradeStepRegistrator(bundle);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					upgradeExecutorClass.getName(), LoggerTestUtil.OFF)) {

				String message = ReflectionTestUtil.invoke(
					_upgradeOSGiCommands, "execute",
					new Class<?>[] {String.class, String.class},
					bundleSymbolicName, RandomTestUtil.randomString());

				Assert.assertEquals(
					"The upgrade of module " + bundleSymbolicName + " failed",
					message);
			}
		}
		finally {
			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if (release != null) {
				_releaseLocalService.deleteRelease(release);
			}
		}
	}

	@Test
	public void testExecuteWithUnregisteredModule() {
		String bundleSymbolicName = RandomTestUtil.randomString();

		String expectedMessage =
			"No upgrade processes registered for " + bundleSymbolicName;

		String message = ReflectionTestUtil.invoke(
			_upgradeOSGiCommands, "execute", new Class<?>[] {String.class},
			bundleSymbolicName);

		Assert.assertEquals(expectedMessage, message);

		message = ReflectionTestUtil.invoke(
			_upgradeOSGiCommands, "execute",
			new Class<?>[] {String.class, String.class}, bundleSymbolicName,
			RandomTestUtil.randomString());

		Assert.assertEquals(expectedMessage, message);
	}

	@Test
	public void testListWithFailedRegistration() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeOSGiCommandsTest.class);

		String bundleSymbolicName = bundle.getSymbolicName();

		_registerFailingUpgradeStepRegistrator(bundle);

		_assertList(bundleSymbolicName);

		_serviceRegistration.unregister();

		Release release = _releaseLocalService.addRelease(
			bundleSymbolicName, "1.0.0");

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_AUTO_RUN", false, false)) {

			_registerFailingUpgradeStepRegistrator(bundle);

			Class<?> clazz = _upgradeExecutor.getClass();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					clazz.getName(), LoggerTestUtil.OFF)) {

				String message = ReflectionTestUtil.invoke(
					_upgradeOSGiCommands, "list", new Class<?>[] {String.class},
					bundleSymbolicName);

				Assert.assertEquals(
					"The upgrade of module " + bundleSymbolicName + " failed",
					message);

				_assertList(bundleSymbolicName);
			}
		}
		finally {
			_releaseLocalService.deleteRelease(release);
		}
	}

	@Test
	public void testListWithUnregisteredModule() {
		String bundleSymbolicName = RandomTestUtil.randomString();

		String message = ReflectionTestUtil.invoke(
			_upgradeOSGiCommands, "list", new Class<?>[] {String.class},
			bundleSymbolicName);

		Assert.assertEquals(
			"No upgrade processes registered for " + bundleSymbolicName,
			message);
	}

	private void _assertExecuteAll(String expectedMessage) {
		String message = ReflectionTestUtil.invoke(
			_upgradeOSGiCommands, "executeAll", new Class<?>[0]);

		Assert.assertTrue(message, message.contains(expectedMessage));
	}

	private void _assertList(String bundleSymbolicName) {
		String message = ReflectionTestUtil.invoke(
			_upgradeOSGiCommands, "list", new Class<?>[0]);

		Assert.assertTrue(
			message,
			message.contains(
				"The upgrade of module " + bundleSymbolicName + " failed"));
	}

	private void _assertRecovered(
		String bundleSymbolicName, String expectedSchemaVersion) {

		Release release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		Assert.assertEquals(expectedSchemaVersion, release.getSchemaVersion());

		Set<String> failedBundleSymbolicNames = ReflectionTestUtil.invoke(
			_upgradeExecutor, "getFailedBundleSymbolicNames", new Class<?>[0]);

		Assert.assertFalse(
			failedBundleSymbolicNames.toString(),
			failedBundleSymbolicNames.contains(bundleSymbolicName));
	}

	private void _registerFailingUpgradeStepRegistrator(Bundle bundle) {
		_registerUpgradeStepRegistrator(
			bundle,
			upgradeStepRegistry -> {
				throw new IllegalStateException();
			});
	}

	private void _registerRecoveringUpgradeStepRegistrator(Bundle bundle) {
		AtomicInteger registerCount = new AtomicInteger();

		_registerUpgradeStepRegistrator(
			bundle,
			upgradeStepRegistry -> {
				upgradeStepRegistry.register(
					"0.0.0", "1.0.0", new DummyUpgradeStep());
				upgradeStepRegistry.register(
					"1.0.0", "2.0.0", new DummyUpgradeStep());

				if (registerCount.incrementAndGet() == 1) {
					throw new IllegalStateException();
				}
			});
	}

	private void _registerUpgradeStepRegistrator(
		Bundle bundle, UpgradeStepRegistrator upgradeStepRegistrator) {

		BundleContext bundleContext = bundle.getBundleContext();

		Class<?> clazz = _upgradeExecutor.getClass();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				clazz.getName(), LoggerTestUtil.OFF)) {

			_serviceRegistration = bundleContext.registerService(
				UpgradeStepRegistrator.class, upgradeStepRegistrator, null);
		}
	}

	@Inject
	private ReleaseLocalService _releaseLocalService;

	private ServiceRegistration<UpgradeStepRegistrator> _serviceRegistration;

	@Inject(
		filter = "component.name=com.liferay.portal.upgrade.internal.executor.UpgradeExecutor",
		type = Inject.NoType.class
	)
	private Object _upgradeExecutor;

	@Inject(
		filter = "component.name=com.liferay.portal.upgrade.internal.release.osgi.commands.UpgradeOSGiCommands",
		type = Inject.NoType.class
	)
	private Object _upgradeOSGiCommands;

}