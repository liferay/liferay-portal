/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.service.persistence.ReleaseUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.upgrade.util.UpgradeModulesFactory;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

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
import org.osgi.framework.ServiceRegistration;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class UpgradeModulesFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalUpgradeDatabaseAutoRun = PropsUtil.get(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN);

		PropsUtil.set(PropsKeys.UPGRADE_DATABASE_AUTO_RUN, "true");
	}

	@AfterClass
	public static void tearDownClass() {
		PropsUtil.set(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN,
			_originalUpgradeDatabaseAutoRun);
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		Release release = _releaseLocalService.fetchRelease(
			_SERVLET_CONTEXT_NAME);

		if (release != null) {
			_releaseLocalService.deleteRelease(release);
		}
	}

	@Test
	public void testUpgradeFromExistingNullRelease() throws UpgradeException {
		_releaseLocalService.addRelease(_SERVLET_CONTEXT_NAME, null);

		_test();
	}

	@Test
	public void testUpgradeFromNonexistingRelease() throws UpgradeException {
		_test();
	}

	private void _test() throws UpgradeException {
		UpgradeProcess upgradeProcess = UpgradeModulesFactory.create(
			new String[] {_SERVLET_CONTEXT_NAME}, null);

		upgradeProcess.upgrade();

		ReleaseUtil.clearCache();

		Bundle bundle = FrameworkUtil.getBundle(
			UpgradeModulesFactoryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		TestUpgradeStep[] testUpgradeSteps = new TestUpgradeStep[1];

		testUpgradeSteps[0] = new TestUpgradeStep("0.0.1", "1.0.0");

		TestUpgradeStepRegistrator testUpgradeStepRegistrator =
			new TestUpgradeStepRegistrator(testUpgradeSteps);

		_serviceRegistration = bundleContext.registerService(
			UpgradeStepRegistrator.class, testUpgradeStepRegistrator, null);

		Assert.assertTrue(testUpgradeStepRegistrator._registratorCalled);

		Release release = _releaseLocalService.fetchRelease(
			_SERVLET_CONTEXT_NAME);

		Assert.assertEquals("1.0.0", release.getSchemaVersion());
	}

	private static final String _SERVLET_CONTEXT_NAME =
		"com.liferay.portal.upgrade.test";

	private static String _originalUpgradeDatabaseAutoRun;

	@Inject
	private static ReleaseLocalService _releaseLocalService;

	private ServiceRegistration<UpgradeStepRegistrator> _serviceRegistration;

	private static class TestUpgradeStep implements UpgradeStep {

		@Override
		public void upgrade() {
		}

		private TestUpgradeStep(
			String fromSchemaVersionString, String toSchemaVersionString) {

			_fromSchemaVersionString = fromSchemaVersionString;
			_toSchemaVersionString = toSchemaVersionString;
		}

		private final String _fromSchemaVersionString;
		private final String _toSchemaVersionString;

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