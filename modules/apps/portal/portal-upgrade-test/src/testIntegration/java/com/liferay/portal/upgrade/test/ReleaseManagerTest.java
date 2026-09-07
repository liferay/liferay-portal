/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.release.SchemaCreator;

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
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

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
	public void testGetStatusWithFailedSchemaCreation() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(ReleaseManagerTest.class);

		String bundleSymbolicName = bundle.getSymbolicName();

		try {
			_registerFailingSchemaCreator(bundle);

			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			Assert.assertEquals("0.0.0", release.getSchemaVersion());
			Assert.assertEquals(
				ReleaseConstants.STATE_UPGRADE_FAILURE, release.getState());

			_assertFailedStatus(bundleSymbolicName, _releaseManager);

			_serviceRegistration.unregister();

			_serviceRegistration = null;

			Assert.assertEquals("success", _releaseManager.getStatus());

			Class<?> clazz = _releaseManager.getClass();

			ComponentDescriptionDTO componentDescriptionDTO =
				_serviceComponentRuntime.getComponentDescriptionDTO(
					FrameworkUtil.getBundle(clazz), clazz.getName());

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					clazz.getName(), LoggerTestUtil.OFF)) {

				Promise<?> promise = _serviceComponentRuntime.disableComponent(
					componentDescriptionDTO);

				promise.getValue();

				_registerFailingSchemaCreator(bundle);

				promise = _serviceComponentRuntime.enableComponent(
					componentDescriptionDTO);

				promise.getValue();

				BundleContext bundleContext = bundle.getBundleContext();

				_assertFailedStatus(
					bundleSymbolicName,
					bundleContext.getService(
						bundleContext.getServiceReference(
							ReleaseManager.class)));
			}
			finally {
				if (!_serviceComponentRuntime.isComponentEnabled(
						componentDescriptionDTO)) {

					Promise<?> promise =
						_serviceComponentRuntime.enableComponent(
							componentDescriptionDTO);

					promise.getValue();
				}
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
	public void testGetStatusWithFailedUpgradeStepRegistration()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(ReleaseManagerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.upgrade.internal.executor.UpgradeExecutor",
				LoggerTestUtil.OFF)) {

			_serviceRegistration = bundleContext.registerService(
				UpgradeStepRegistrator.class,
				registry -> {
					throw new IllegalStateException();
				},
				null);
		}

		_assertFailedStatus(bundle.getSymbolicName(), _releaseManager);
	}

	@Test
	public void testSuccessfulSchemaCreatorAfterUnsuccesfulOne()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(ReleaseManagerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Release release = _releaseLocalService.addRelease(
			bundle.getSymbolicName(), "0.0.0");

		try {
			release.setState(ReleaseConstants.STATE_UPGRADE_FAILURE);

			_releaseLocalService.updateRelease(release);

			_serviceRegistration = bundleContext.registerService(
				SchemaCreator.class,
				new SchemaCreator() {

					@Override
					public void create() {
					}

					@Override
					public String getBundleSymbolicName() {
						return bundle.getSymbolicName();
					}

					public String getSchemaVersion() {
						return "1.0.0";
					}

				},
				null);

			// Wait for SchemaCreator to complete and register the new release

			Thread.sleep(2000);

			release = _releaseLocalService.fetchRelease(
				bundle.getSymbolicName());

			Assert.assertEquals("1.0.0", release.getSchemaVersion());
			Assert.assertEquals(
				ReleaseConstants.STATE_GOOD, release.getState());
		}
		finally {
			_releaseLocalService.deleteRelease(release);
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
	public void testSuccessfulUpgradeWithNewerCompatibleSchemaVersion()
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			Version version = PortalUpgradeProcess.getCurrentSchemaVersion(
				connection);

			try {
				PortalUpgradeProcess.updateSchemaVersion(
					connection,
					new Version(
						version.getMajor(), version.getMinor(),
						version.getMicro() + 1));

				Assert.assertTrue(
					Validator.isBlank(
						_releaseManager.getShortStatusMessage(false)));
				Assert.assertEquals("success", _releaseManager.getStatus());
				Assert.assertTrue(
					Validator.isBlank(_releaseManager.getStatusMessage(false)));
			}
			finally {
				PortalUpgradeProcess.updateSchemaVersion(connection, version);
			}
		}
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

	private void _assertFailedStatus(
			String bundleSymbolicName, ReleaseManager releaseManager)
		throws Exception {

		Assert.assertFalse(
			Validator.isBlank(releaseManager.getShortStatusMessage(true)));
		Assert.assertEquals("failure", releaseManager.getStatus());

		String statusMessage = releaseManager.getStatusMessage(false);

		Assert.assertTrue(
			statusMessage,
			statusMessage.contains(
				"The upgrade of module " + bundleSymbolicName + " failed"));
	}

	private void _registerFailingSchemaCreator(Bundle bundle) {
		BundleContext bundleContext = bundle.getBundleContext();

		Class<?> clazz = _releaseManager.getClass();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				clazz.getName(), LoggerTestUtil.OFF)) {

			_serviceRegistration = bundleContext.registerService(
				SchemaCreator.class,
				new SchemaCreator() {

					@Override
					public void create() throws UpgradeException {
						throw new UpgradeException();
					}

					@Override
					public String getBundleSymbolicName() {
						return bundle.getSymbolicName();
					}

					@Override
					public String getSchemaVersion() {
						return "1.0.0";
					}

				},
				null);
		}
	}

	@Inject
	private ReleaseLocalService _releaseLocalService;

	@Inject
	private volatile ReleaseManager _releaseManager;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

	private ServiceRegistration<?> _serviceRegistration;

	private static class TestUpgradeStepRegistrator
		implements UpgradeStepRegistrator {

		@Override
		public void register(Registry registry) {
			registry.registerInitialization();
		}

	}

}