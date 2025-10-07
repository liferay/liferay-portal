/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;

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
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class ReleaseManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testSuccessfulUpgrade() throws Exception {
		Assert.assertTrue(
			Validator.isBlank(_releaseManager.getShortStatusMessage(false)));
		Assert.assertEquals("success", _releaseManager.getStatus());
		Assert.assertTrue(
			Validator.isBlank(_releaseManager.getStatusMessage(false)));
	}

	@Test
	public void testUnsuccessfulUpgradeByMissingModuleUpgradeWithAutorun()
		throws Exception {

		String upgradeDatabaseAutoRun = PropsUtil.get(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN);

		try {
			PropsUtil.set(PropsKeys.UPGRADE_DATABASE_AUTO_RUN, "true");

			Bundle bundle = FrameworkUtil.getBundle(ReleaseManagerTest.class);

			BundleContext bundleContext = bundle.getBundleContext();

			_serviceRegistration = bundleContext.registerService(
				UpgradeStepRegistrator.class,
				new ReleaseManagerTest.TestUpgradeStepRegistrator(), null);

			Release release = _releaseLocalService.fetchRelease(
				bundle.getSymbolicName());

			try {
				release.setSchemaVersion("0.0.0");

				release = _releaseLocalService.updateRelease(release);

				Assert.assertFalse(
					Validator.isBlank(
						_releaseManager.getShortStatusMessage(false)));
				Assert.assertEquals("failure", _releaseManager.getStatus());
				Assert.assertFalse(
					Validator.isBlank(_releaseManager.getStatusMessage(false)));
			}
			finally {
				_releaseLocalService.deleteRelease(release);
			}
		}
		finally {
			PropsUtil.set(
				PropsKeys.UPGRADE_DATABASE_AUTO_RUN, upgradeDatabaseAutoRun);
		}
	}

	@Test
	public void testUnsuccessfulUpgradeByMissingPortalUpgrade()
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			Version version = PortalUpgradeProcess.getCurrentSchemaVersion(
				connection);

			PortalUpgradeProcess.updateSchemaVersion(
				connection, new Version(0, 0, 0));

			try {
				Assert.assertFalse(
					Validator.isBlank(
						_releaseManager.getShortStatusMessage(false)));
				Assert.assertEquals("failure", _releaseManager.getStatus());
				Assert.assertFalse(
					Validator.isBlank(_releaseManager.getStatusMessage(false)));
			}
			finally {
				PortalUpgradeProcess.updateSchemaVersion(connection, version);
			}
		}
	}

	@Inject
	private ReleaseLocalService _releaseLocalService;

	@Inject
	private volatile ReleaseManager _releaseManager;

	private ServiceRegistration<UpgradeStepRegistrator> _serviceRegistration;

	private static class TestUpgradeStepRegistrator
		implements UpgradeStepRegistrator {

		@Override
		public void register(Registry registry) {
			registry.registerInitialization();
		}

	}

}