/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyProcess;

import org.junit.After;
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
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class VerifyProcessTrackerOSGiCommandsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			VerifyProcessTrackerOSGiCommandsTest.class);

		_bundleContext = bundle.getBundleContext();

		_runOnPortalUpgradeVerifiers =
			StartupHelperUtil.isRunOnPortalUpgradeVerifiers();
		_symbolicName = bundle.getSymbolicName();
		_upgrading = StartupHelperUtil.isUpgrading();

		StartupHelperUtil.setUpgrading(false);
	}

	@After
	public void tearDown() {
		StartupHelperUtil.setRunOnPortalUpgradeVerifiers(
			_runOnPortalUpgradeVerifiers);
		StartupHelperUtil.setUpgrading(_upgrading);

		Release release = _releaseLocalService.fetchRelease(_symbolicName);

		if (release != null) {
			_releaseLocalService.deleteRelease(release);
		}

		_forceFailure = false;
		_initialDeployment = false;
		_initialVerifyStatus = false;
		_verifyProcessRun = false;
	}

	@Test
	public void testRegisterFailedVerifyProcess() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.verify.extender.internal.osgi.commands." +
					"VerifyProcessTrackerOSGiCommands",
				LoggerTestUtil.OFF)) {

			_forceFailure = true;

			try (SafeCloseable safeCloseable = _registerVerifyProcess(
					true, true)) {

				_assertVerify(true);
			}
		}
	}

	@Test
	public void testRegisterInitialDeploymentAndRunOnPortalUpgradeNewVerifyProcessDuringPortalUpgrade() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _upgradePortal(false);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(true, true)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentAndRunOnPortalUpgradeVerifyProcessAfterInitialDeploymentUpgradeProcess() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _executeInitialUpgradeProcess();
			SafeCloseable safeCloseable2 = _registerVerifyProcess(true, true)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentAndRunOnPortalUpgradeVerifyProcessAfterModuleUpgrade() {
		_initialDeployment = true;

		_simulateUpgradeProcessExecution();

		try (SafeCloseable safeCloseable2 = _registerVerifyProcess(
				true, true)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentAndRunOnPortalUpgradeVerifyProcessDuringInitialDeployment() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable = _registerVerifyProcess(true, true)) {
			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentAndRunOnPortalUpgradeVerifyProcessDuringPortalUpgrade() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _upgradePortal(true);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(true, true)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentNewVerifyProcessDuringPortalUpgrade() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _upgradePortal(false);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				true, false)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentVerifyProcessAfterInitialDeploymentUpgradeProcess() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _executeInitialUpgradeProcess();
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				true, false)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentVerifyProcessAfterModuleUpgrade() {
		_initialDeployment = true;

		_simulateUpgradeProcessExecution();

		try (SafeCloseable safeCloseable2 = _registerVerifyProcess(
				true, false)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentVerifyProcessDuringInitialDeployment() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable = _registerVerifyProcess(
				true, false)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterInitialDeploymentVerifyProcessDuringPortalUpgrade() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _upgradePortal(true);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				true, false)) {

			_assertVerify(false);
		}
	}

	@Test
	public void testRegisterNewVerifyProcessDuringUpgradePortal() {
		try (SafeCloseable safeCloseable1 = _upgradePortal(false);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, false)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterRunOnPortalUpgradeNewVerifyProcessDuringPortalUpgrade() {
		try (SafeCloseable safeCloseable1 = _upgradePortal(false);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, true)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterRunOnPortalUpgradeVerifyProcessAfterInitialDeploymentUpgradeProcess() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _executeInitialUpgradeProcess();
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, true)) {

			_assertVerify(false);
		}
	}

	@Test
	public void testRegisterRunOnPortalUpgradeVerifyProcessAfterModuleUpgrade() {
		_simulateUpgradeProcessExecution();

		try (SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, true)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterRunOnPortalUpgradeVerifyProcessDuringInitialDeployment() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable = _registerVerifyProcess(
				false, true)) {

			_assertVerify(false);
		}
	}

	@Test
	public void testRegisterRunOnPortalUpgradeVerifyProcessDuringPortalUpgrade() {
		try (SafeCloseable safeCloseable1 = _upgradePortal(true);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, true)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterVerifyProcessAfterInitialDeploymentUpgradeProcess() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable1 = _executeInitialUpgradeProcess();
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, false)) {

			_assertVerify(false);
		}
	}

	@Test
	public void testRegisterVerifyProcessAfterModuleUpgrade() {
		_simulateUpgradeProcessExecution();

		try (SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, false)) {

			_assertVerify(true);
		}
	}

	@Test
	public void testRegisterVerifyProcessDuringInitialDeployment() {
		_initialDeployment = true;

		try (SafeCloseable safeCloseable = _registerVerifyProcess(
				false, false)) {

			_assertVerify(false);
		}
	}

	@Test
	public void testRegisterVerifyProcessDuringUpgradePortal() {
		try (SafeCloseable safeCloseable1 = _upgradePortal(true);
			SafeCloseable safeCloseable2 = _registerVerifyProcess(
				false, false)) {

			_assertVerify(false);
		}
	}

	private void _assertVerify(boolean verifyProcessRun) {
		Assert.assertEquals(verifyProcessRun, _verifyProcessRun);

		Release release = _releaseLocalService.fetchRelease(_symbolicName);

		Assert.assertNotNull(release);

		if (_forceFailure) {
			Assert.assertFalse(release.isVerified());

			return;
		}

		if (_initialDeployment) {
			Assert.assertTrue(release.isVerified());

			return;
		}

		Assert.assertEquals(
			_initialVerifyStatus || verifyProcessRun, release.isVerified());
	}

	private SafeCloseable _executeInitialUpgradeProcess() {
		ServiceRegistration<UpgradeStep> upgradeStepServiceRegistration =
			_bundleContext.registerService(
				UpgradeStep.class, new DummyUpgradeProcess(),
				HashMapDictionaryBuilder.<String, Object>put(
					"upgrade.bundle.symbolic.name", _symbolicName
				).put(
					"upgrade.from.schema.version", "0.0.0"
				).put(
					"upgrade.to.schema.version", "1.0.0"
				).build());

		return upgradeStepServiceRegistration::unregister;
	}

	private SafeCloseable _registerVerifyProcess(
		boolean initialDeployment, boolean runOnPortalUpgrade) {

		ServiceRegistration<VerifyProcess> verifyProcessServiceRegistration =
			_bundleContext.registerService(
				VerifyProcess.class, _verifyProcess,
				HashMapDictionaryBuilder.<String, Object>put(
					"initial.deployment", initialDeployment
				).put(
					"run.on.portal.upgrade", runOnPortalUpgrade
				).build());

		return verifyProcessServiceRegistration::unregister;
	}

	private void _simulateUpgradeProcessExecution() {
		Release release = _releaseLocalService.createRelease(
			_counterLocalService.increment());

		release.setServletContextName(_symbolicName);
		release.setSchemaVersion("1.0.0");
		release.setVerified(false);

		_initialVerifyStatus = false;

		_releaseLocalService.updateRelease(release);
	}

	private SafeCloseable _upgradePortal(boolean moduleVerified) {
		Release release = _releaseLocalService.createRelease(
			_counterLocalService.increment());

		release.setServletContextName(_symbolicName);
		release.setSchemaVersion("0.0.1");
		release.setVerified(moduleVerified);

		_initialVerifyStatus = moduleVerified;

		_releaseLocalService.updateRelease(release);

		StartupHelperUtil.setRunOnPortalUpgradeVerifiers(true);

		return () -> StartupHelperUtil.setRunOnPortalUpgradeVerifiers(false);
	}

	private static BundleContext _bundleContext;
	private static boolean _runOnPortalUpgradeVerifiers;
	private static String _symbolicName;
	private static boolean _upgrading;

	@Inject
	private CounterLocalService _counterLocalService;

	private boolean _forceFailure;
	private boolean _initialDeployment;
	private boolean _initialVerifyStatus;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	private final VerifyProcessTest _verifyProcess = new VerifyProcessTest();
	private boolean _verifyProcessRun;

	private class DummyUpgradeProcess extends DummyUpgradeStep {
	}

	private class VerifyProcessTest extends VerifyProcess {

		@Override
		protected void doVerify() throws Exception {
			_verifyProcessRun = true;

			if (_forceFailure) {
				throw new Exception();
			}
		}

	}

}