/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.index.IndexUpdaterUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.component.UpgradeRecorderTestComponent;
import com.liferay.portal.upgrade.test.reference.UpgradeRecorderTestReference;
import com.liferay.portal.verify.PreupgradeVerifyProcessSuite;
import com.liferay.portal.verify.VerifyException;
import com.liferay.portal.verify.VerifyProcess;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.commons.lang.time.StopWatch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class UpgradeRecorderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_bundle = FrameworkUtil.getBundle(UpgradeRecorderTest.class);

		_originalStopWatch = ReflectionTestUtil.getFieldValue(
			DBUpgrader.class, "_stopWatch");

		_originalVerifyProcessError = ReflectionTestUtil.getFieldValue(
			_upgradeRecorder, "_verifyProcessError");
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			DBUpgrader.class, "_stopWatch", _originalStopWatch);

		ReflectionTestUtil.setFieldValue(
			_upgradeRecorder, "_verifyProcessError",
			_originalVerifyProcessError);

		ReflectionTestUtil.invoke(
			IndexUpdaterUtil.class, "_clearProcessedServletContextNames", null,
			null);
	}

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(DBUpgrader.class, "_stopWatch", null);

		ReflectionTestUtil.setFieldValue(
			_upgradeRecorder, "_verifyProcessError",
			_originalVerifyProcessError);
	}

	@Test
	public void testFailureResultByPendingModuleUpgrade() {
		BundleContext bundleContext = _bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			UpgradeStepRegistrator.class,
			new UpgradeRecorderTest.TestUpgradeStepRegistrator(), null);

		Release release = _releaseLocalService.fetchRelease(
			_bundle.getSymbolicName());

		try {
			StartupHelperUtil.setUpgrading(true);

			release.setSchemaVersion("0.0.0");

			release = _releaseLocalService.updateRelease(release);

			StartupHelperUtil.setUpgrading(false);
		}
		finally {
			_releaseLocalService.deleteRelease(release);

			if (_serviceRegistration != null) {
				_serviceRegistration.unregister();
			}
		}

		Assert.assertEquals("failure", _getResult());
	}

	@Test
	public void testFailureResultByPreupgradeVerifyExceptionWithoutReleaseManager() {
		StartupHelperUtil.setUpgrading(true);

		ServiceTracker<ReleaseManager, ReleaseManager> originalServiceTracker =
			ReflectionTestUtil.getFieldValue(
				_upgradeRecorder, "_serviceTracker");

		VerifyExceptionProcess verifyExceptionProcess =
			new VerifyExceptionProcess();

		try {
			ServiceTracker<ReleaseManager, ReleaseManager> serviceTracker =
				new ServiceTracker<>(
					_bundle.getBundleContext(), ReleaseManager.class, null) {

					public ReleaseManager getService() {
						return null;
					}

				};

			ReflectionTestUtil.setFieldValue(
				_upgradeRecorder, "_serviceTracker", serviceTracker);

			_appender.start();

			verifyExceptionProcess.verify();

			Assert.fail();
		}
		catch (VerifyException verifyException) {
			_appender.append(
				Log4jLogEvent.newBuilder(
				).setLoggerName(
					PreupgradeVerifyProcessSuite.class.getName()
				).setLevel(
					Level.ERROR
				).setMessage(
					new SimpleMessage(RandomTestUtil.randomString())
				).setThrown(
					verifyException
				).build());
		}
		finally {
			_appender.stop();

			StartupHelperUtil.setUpgrading(false);

			ReflectionTestUtil.setFieldValue(
				_upgradeRecorder, "_serviceTracker", originalServiceTracker);
		}

		Assert.assertEquals("preupgrade verification failure", _getResult());
	}

	@Test
	public void testFailureResultByPreupgradeVerifyExceptionWithReleaseManager() {
		StartupHelperUtil.setUpgrading(true);

		VerifyExceptionProcess verifyExceptionProcess =
			new VerifyExceptionProcess();

		try {
			_appender.start();

			verifyExceptionProcess.verify();

			Assert.fail();
		}
		catch (VerifyException verifyException) {
			_appender.append(
				Log4jLogEvent.newBuilder(
				).setLoggerName(
					PreupgradeVerifyProcessSuite.class.getName()
				).setLevel(
					Level.ERROR
				).setMessage(
					new SimpleMessage(RandomTestUtil.randomString())
				).setThrown(
					verifyException
				).build());
		}
		finally {
			_appender.stop();

			StartupHelperUtil.setUpgrading(false);
		}

		Assert.assertEquals("failure", _getResult());
	}

	@Test
	public void testFailureResultByVerifyException() {
		StartupHelperUtil.setUpgrading(true);

		VerifyExceptionProcess verifyExceptionProcess =
			new VerifyExceptionProcess();

		try {
			_appender.start();

			verifyExceptionProcess.verify();

			Assert.fail();
		}
		catch (VerifyException verifyException) {
			_appender.append(
				Log4jLogEvent.newBuilder(
				).setLoggerName(
					UpgradeRecorderTest.class.getName()
				).setLevel(
					Level.ERROR
				).setMessage(
					new SimpleMessage(RandomTestUtil.randomString())
				).setThrown(
					verifyException
				).build());
		}
		finally {
			_appender.stop();

			StartupHelperUtil.setUpgrading(false);
		}

		Assert.assertEquals("failure", _getResult());
	}

	@Test
	public void testFailureStatusByPendingCoreUpgrade() throws SQLException {
		try (Connection connection = DataAccess.getConnection()) {
			Version version = PortalUpgradeProcess.getCurrentSchemaVersion(
				connection);

			try {
				StartupHelperUtil.setUpgrading(true);

				PortalUpgradeProcess.updateSchemaVersion(
					connection, new Version(0, 0, 0));

				StartupHelperUtil.setUpgrading(false);
			}
			finally {
				PortalUpgradeProcess.updateSchemaVersion(connection, version);
			}
		}

		Assert.assertEquals("failure", _getResult());
	}

	@Test
	public void testMajorUpgrade() {
		_testUpgrade("major");

		Assert.assertEquals("major", _getType());
	}

	@Test
	public void testMicroUpgrade() {
		_testUpgrade("micro");

		Assert.assertEquals("micro", _getType());
	}

	@Test
	public void testMinorUpgrade() {
		_testUpgrade("minor");

		Assert.assertEquals("minor", _getType());
	}

	@Test
	public void testQualifierUpgrade() {
		_testUpgrade("qualifier");

		Assert.assertEquals("micro", _getType());
	}

	@Test
	public void testSuccessResultByNoUpgrades() {
		StartupHelperUtil.setUpgrading(true);

		StartupHelperUtil.setUpgrading(false);

		Assert.assertEquals("success", _getResult());
		Assert.assertEquals("no upgrade", _getType());
	}

	@Test
	public void testSuccessResultWithUnrelatedError() {
		StartupHelperUtil.setUpgrading(true);

		UnrelatedErrorUpgradeProcess unrelatedErrorUpgradeProcess =
			new UnrelatedErrorUpgradeProcess();

		unrelatedErrorUpgradeProcess.doUpgrade();

		StartupHelperUtil.setUpgrading(false);

		Assert.assertEquals("success", _getResult());
		Assert.assertEquals("no upgrade", _getType());
	}

	@Test
	public void testSuccessResultWithWarning() {
		StartupHelperUtil.setUpgrading(true);

		WarningUpgradeProcess warningUpgradeProcess =
			new WarningUpgradeProcess();

		warningUpgradeProcess.doUpgrade();

		StartupHelperUtil.setUpgrading(false);

		Assert.assertEquals("success", _getResult());
		Assert.assertEquals("no upgrade", _getType());
	}

	@Test
	public void testUnresolvedResultByUnsatisfiedComponent() throws Exception {
		BundleContext bundleContext = _bundle.getBundleContext();

		Bundle bundle = bundleContext.installBundle(
			"location", _createBundle());

		bundle.start();

		try {
			StartupHelperUtil.setUpgrading(true);

			StartupHelperUtil.setUpgrading(false);
		}
		finally {
			bundle.uninstall();
		}

		Assert.assertEquals("unresolved", _getResult());
		Assert.assertEquals("major", _getType());
	}

	public static class TestUpgradeStepRegistrator
		implements UpgradeStepRegistrator {

		@Override
		public void register(Registry registry) {
			registry.registerInitialization();
		}

	}

	private InputStream _createBundle() throws Exception {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				Manifest manifest = new Manifest();

				Attributes attributes = manifest.getMainAttributes();

				attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
				attributes.putValue(
					Constants.BUNDLE_SYMBOLICNAME,
					"com.liferay.portal.upgrade.test.bundle");
				attributes.putValue(Constants.BUNDLE_VERSION, "1.0.0");
				attributes.putValue("Manifest-Version", "1.0");
				attributes.putValue(
					"Service-Component",
					"OSGI-INF/" + _TEST_COMPONENT_FILE_NAME);

				jarOutputStream.putNextEntry(
					new ZipEntry(JarFile.MANIFEST_NAME));

				manifest.write(jarOutputStream);

				jarOutputStream.closeEntry();

				_writeClasses(
					jarOutputStream, UpgradeRecorderTestComponent.class,
					UpgradeRecorderTestReference.class);

				jarOutputStream.putNextEntry(
					new ZipEntry("OSGI-INF/" + _TEST_COMPONENT_FILE_NAME));

				_writeServiceComponentFile(jarOutputStream, getClass());

				jarOutputStream.closeEntry();
			}

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private String _getResult() {
		return ReflectionTestUtil.getFieldValue(_upgradeRecorder, "_result");
	}

	private String _getType() {
		return ReflectionTestUtil.getFieldValue(_upgradeRecorder, "_type");
	}

	private void _testUpgrade(String type) {
		List<Release> releases = _releaseLocalService.getReleases(0, 4);

		Release majorRelease = releases.get(0);
		Release microRelease = releases.get(1);
		Release minorRelease = releases.get(2);
		Release qualifierRelease = releases.get(3);

		Version majorSchemaVersion = Version.parseVersion(
			majorRelease.getSchemaVersion());
		Version minorSchemaVersion = Version.parseVersion(
			minorRelease.getSchemaVersion());
		Version microSchemaVersion = Version.parseVersion(
			microRelease.getSchemaVersion());
		String qualifierSchemaVersion = qualifierRelease.getSchemaVersion();

		try {
			qualifierRelease.setSchemaVersion(
				qualifierSchemaVersion + ".step-2");

			qualifierRelease = _releaseLocalService.updateRelease(
				qualifierRelease);

			StartupHelperUtil.setUpgrading(true);

			if (type.equals("major")) {
				majorRelease.setSchemaVersion(
					StringBundler.concat(
						majorSchemaVersion.getMajor() + 1, StringPool.PERIOD,
						majorSchemaVersion.getMinor(), StringPool.PERIOD,
						majorSchemaVersion.getMicro()));

				majorRelease = _releaseLocalService.updateRelease(majorRelease);
			}

			if (type.equals("major") || type.equals("minor")) {
				minorRelease.setSchemaVersion(
					StringBundler.concat(
						minorSchemaVersion.getMajor(), StringPool.PERIOD,
						minorSchemaVersion.getMinor() + 1, StringPool.PERIOD,
						minorSchemaVersion.getMicro()));

				minorRelease = _releaseLocalService.updateRelease(minorRelease);
			}

			if (type.equals("major") || type.equals("minor") ||
				type.equals("micro")) {

				microRelease.setSchemaVersion(
					StringBundler.concat(
						microSchemaVersion.getMajor(), StringPool.PERIOD,
						microSchemaVersion.getMinor(), StringPool.PERIOD,
						microSchemaVersion.getMicro() + 1));

				microRelease = _releaseLocalService.updateRelease(microRelease);
			}

			qualifierRelease.setSchemaVersion(qualifierSchemaVersion);

			_releaseLocalService.updateRelease(qualifierRelease);

			StartupHelperUtil.setUpgrading(false);
		}
		finally {
			majorRelease.setSchemaVersion(majorSchemaVersion.toString());
			microRelease.setSchemaVersion(microSchemaVersion.toString());
			minorRelease.setSchemaVersion(minorSchemaVersion.toString());

			_releaseLocalService.updateRelease(majorRelease);
			_releaseLocalService.updateRelease(microRelease);
			_releaseLocalService.updateRelease(minorRelease);
		}
	}

	private void _writeClasses(
			JarOutputStream jarOutputStream, Class<?>... classes)
		throws IOException, IOException {

		for (Class<?> clazz : classes) {
			String className = clazz.getName();

			String path = StringUtil.replace(
				className, CharPool.PERIOD, CharPool.SLASH);

			String resourcePath = path.concat(".class");

			jarOutputStream.putNextEntry(new ZipEntry(resourcePath));

			ClassLoader classLoader = clazz.getClassLoader();

			StreamUtil.transfer(
				classLoader.getResourceAsStream(resourcePath), jarOutputStream,
				false);

			jarOutputStream.closeEntry();
		}
	}

	private void _writeServiceComponentFile(
			JarOutputStream jarOutputStream, Class<?> clazz)
		throws IOException {

		ClassLoader classLoader = clazz.getClassLoader();

		Package pkg = clazz.getPackage();

		String packagePath = StringUtil.replace(
			pkg.getName(), CharPool.PERIOD, CharPool.SLASH);

		StreamUtil.transfer(
			classLoader.getResourceAsStream(
				StringBundler.concat(
					packagePath, "/dependencies/", _TEST_COMPONENT_FILE_NAME)),
			jarOutputStream, false);
	}

	private static final String _TEST_COMPONENT_FILE_NAME =
		"UpgradeRecorderTestComponent.xml";

	private static Bundle _bundle;
	private static StopWatch _originalStopWatch;
	private static boolean _originalVerifyProcessError;

	@Inject(
		filter = "component.name=com.liferay.portal.upgrade.internal.recorder.UpgradeRecorder",
		type = Inject.NoType.class
	)
	private static Object _upgradeRecorder;

	@Inject(filter = "appender.name=UpgradeLogAppender")
	private Appender _appender;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	private ServiceRegistration<UpgradeStepRegistrator> _serviceRegistration;

	private class UnrelatedErrorUpgradeProcess extends UpgradeProcess {

		@Override
		protected void doUpgrade() {
			Map<String, Map<String, Integer>> errorMessages =
				ReflectionTestUtil.getFieldValue(
					_upgradeRecorder, "_errorMessages");

			errorMessages.put(
				"UnrelatedErrorUpgradeProcess",
				Collections.singletonMap("Error during upgrade", 0));
		}

	}

	private class VerifyExceptionProcess extends VerifyProcess {

		@Override
		protected void doVerify() throws Exception {
			throw new Exception(RandomTestUtil.randomString());
		}

	}

	private class WarningUpgradeProcess extends UpgradeProcess {

		@Override
		protected void doUpgrade() {
			Map<String, Map<String, Integer>> warningMessages =
				ReflectionTestUtil.getFieldValue(
					_upgradeRecorder, "_warningMessages");

			warningMessages.put(
				"WarningUpgradeProcess",
				Collections.singletonMap("Warn during upgrade", 0));
		}

	}

}