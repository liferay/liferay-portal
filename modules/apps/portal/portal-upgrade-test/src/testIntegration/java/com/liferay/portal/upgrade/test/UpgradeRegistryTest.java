/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

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
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class UpgradeRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_originalUpgradeDatabaseAutoRun = PropsUtil.get(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN);

		PropsUtil.set(PropsKeys.UPGRADE_DATABASE_AUTO_RUN, "true");
	}

	@After
	public void tearDown() throws Exception {
		PropsUtil.set(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN,
			_originalUpgradeDatabaseAutoRun);

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		Release release = _releaseLocalService.fetchRelease(
			"com.liferay.portal.upgrade.test");

		if (release != null) {
			_releaseLocalService.deleteRelease(release);
		}
	}

	@Test
	public void testUpgradeRegistryFollowsShortestPath() {
		_releaseLocalService.addRelease(
			"com.liferay.portal.upgrade.test", "1.0.0");

		Bundle bundle = FrameworkUtil.getBundle(UpgradeRegistryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		TestUpgradeStep[] testUpgradeSteps = new TestUpgradeStep[4];

		testUpgradeSteps[0] = new TestUpgradeStep("1.0.0", "2.0.0");
		testUpgradeSteps[1] = new TestUpgradeStep("2.0.0", "3.0.0");
		testUpgradeSteps[2] = new TestUpgradeStep("3.0.0", "4.0.0");
		testUpgradeSteps[3] = new TestUpgradeStep("1.0.0", "4.0.0");

		TestUpgradeStepRegistrator testUpgradeStepRegistrator =
			new TestUpgradeStepRegistrator(testUpgradeSteps);

		_serviceRegistration = bundleContext.registerService(
			UpgradeStepRegistrator.class, testUpgradeStepRegistrator, null);

		Assert.assertTrue(testUpgradeStepRegistrator._registratorCalled);

		Assert.assertFalse(testUpgradeSteps[0]._upgradeCalled);
		Assert.assertFalse(testUpgradeSteps[1]._upgradeCalled);
		Assert.assertFalse(testUpgradeSteps[2]._upgradeCalled);
		Assert.assertTrue(testUpgradeSteps[3]._upgradeCalled);
	}

	private static String _originalUpgradeDatabaseAutoRun;

	@Inject
	private static ReleaseLocalService _releaseLocalService;

	private ServiceRegistration<UpgradeStepRegistrator> _serviceRegistration;

	private static class TestUpgradeStep implements UpgradeStep {

		@Override
		public void upgrade() {
			_upgradeCalled = true;
		}

		private TestUpgradeStep(
			String fromSchemaVersionString, String toSchemaVersionString) {

			_fromSchemaVersionString = fromSchemaVersionString;
			_toSchemaVersionString = toSchemaVersionString;
		}

		private final String _fromSchemaVersionString;
		private final String _toSchemaVersionString;
		private boolean _upgradeCalled;

	}

	private static class TestUpgradeStepRegistrator
		implements UpgradeStepRegistrator {

		@Override
		public void register(Registry registry) {
			_registratorCalled = true;

			for (TestUpgradeStep testUpgradeStep : _testUpgradeSteps) {
				registry.register(
					testUpgradeStep._fromSchemaVersionString,
					testUpgradeStep._toSchemaVersionString, testUpgradeStep);
			}
		}

		private TestUpgradeStepRegistrator(TestUpgradeStep[] testUpgradeSteps) {
			_testUpgradeSteps = testUpgradeSteps;
		}

		private boolean _registratorCalled;
		private final TestUpgradeStep[] _testUpgradeSteps;

	}

}