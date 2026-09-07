/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.List;
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
public class UpgradeExecutorTest {

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
	public void testExecuteWithFailedRegistration() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeExecutorTest.class);

		String bundleSymbolicName = bundle.getSymbolicName();

		_registerUpgradeStepRegistrator(bundle);

		try {
			Set<String> bundleSymbolicNames = ReflectionTestUtil.invoke(
				_upgradeExecutor, "getBundleSymbolicNames", new Class<?>[0]);

			Assert.assertTrue(
				bundleSymbolicNames.toString(),
				bundleSymbolicNames.contains(bundleSymbolicName));

			Set<String> failedBundleSymbolicNames = ReflectionTestUtil.invoke(
				_upgradeExecutor, "getFailedBundleSymbolicNames",
				new Class<?>[0]);

			Assert.assertTrue(
				failedBundleSymbolicNames.toString(),
				failedBundleSymbolicNames.contains(bundleSymbolicName));

			ReflectionTestUtil.invoke(
				_upgradeExecutor, "execute",
				new Class<?>[] {Bundle.class, List.class}, bundle,
				_getUpgradeInfos(bundleSymbolicName));

			failedBundleSymbolicNames = ReflectionTestUtil.invoke(
				_upgradeExecutor, "getFailedBundleSymbolicNames",
				new Class<?>[0]);

			Assert.assertFalse(
				failedBundleSymbolicNames.toString(),
				failedBundleSymbolicNames.contains(bundleSymbolicName));
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
	public void testGetUpgradeInfosWithFailedRegistration() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeExecutorTest.class);

		String bundleSymbolicName = bundle.getSymbolicName();

		AtomicInteger registerCount = _registerUpgradeStepRegistrator(bundle);

		List<?> upgradeInfos = _getUpgradeInfos(bundleSymbolicName);

		Assert.assertEquals(2, registerCount.get());
		Assert.assertEquals(upgradeInfos.toString(), 1, upgradeInfos.size());

		Set<String> failedBundleSymbolicNames = ReflectionTestUtil.invoke(
			_upgradeExecutor, "getFailedBundleSymbolicNames", new Class<?>[0]);

		Assert.assertTrue(
			failedBundleSymbolicNames.toString(),
			failedBundleSymbolicNames.contains(bundleSymbolicName));

		_serviceRegistration.unregister();

		_serviceRegistration = null;

		failedBundleSymbolicNames = ReflectionTestUtil.invoke(
			_upgradeExecutor, "getFailedBundleSymbolicNames", new Class<?>[0]);

		Assert.assertFalse(
			failedBundleSymbolicNames.toString(),
			failedBundleSymbolicNames.contains(bundleSymbolicName));
	}

	private List<?> _getUpgradeInfos(String bundleSymbolicName) {
		return ReflectionTestUtil.invoke(
			_upgradeExecutor, "getUpgradeInfos", new Class<?>[] {String.class},
			bundleSymbolicName);
	}

	private AtomicInteger _registerUpgradeStepRegistrator(Bundle bundle) {
		AtomicInteger registerCount = new AtomicInteger();

		BundleContext bundleContext = bundle.getBundleContext();

		Class<?> clazz = _upgradeExecutor.getClass();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				clazz.getName(), LoggerTestUtil.OFF)) {

			_serviceRegistration = bundleContext.registerService(
				UpgradeStepRegistrator.class,
				registry -> {
					registry.register("0.0.0", "1.0.0", new DummyUpgradeStep());

					if (registerCount.incrementAndGet() == 1) {
						throw new IllegalStateException();
					}
				},
				null);
		}

		return registerCount;
	}

	@Inject
	private ReleaseLocalService _releaseLocalService;

	private ServiceRegistration<UpgradeStepRegistrator> _serviceRegistration;

	@Inject(
		filter = "component.name=com.liferay.portal.upgrade.internal.executor.UpgradeExecutor",
		type = Inject.NoType.class
	)
	private Object _upgradeExecutor;

}