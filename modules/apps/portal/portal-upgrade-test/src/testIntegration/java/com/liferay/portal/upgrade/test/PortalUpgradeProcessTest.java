/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeVersionTreeMap;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class PortalUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			_currentSchemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(connection);
		}
	}

	@Before
	public void setUp() throws Exception {
		_innerPortalUpgradeProcess = new InnerPortalUpgradeProcess();
	}

	@After
	public void tearDown() throws Exception {
		_updateSchemaVersion(_currentSchemaVersion);

		_innerPortalUpgradeProcess.close();
	}

	@Test
	public void testCreatePortalReleaseSavesDisplayName() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"update Release_ set releaseId = -1, servletContextName = '",
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
				"-backup' where servletContextName = '",
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME, "'"));

		try (Connection connection = DataAccess.getConnection()) {
			PortalUpgradeProcess.createPortalRelease(connection);

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select versionDisplayName from Release_ where ",
							"servletContextName = '",
							ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
							"' and versionDisplayName = '",
							ReleaseInfo.getVersionDisplayName(), "'"));
				ResultSet resultSet = preparedStatement.executeQuery()) {

				Assert.assertTrue(resultSet.next());
			}
		}
		finally {
			db.runSQL(
				"delete from Release_ where servletContextName = '" +
					ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME + "'");
			db.runSQL(
				StringBundler.concat(
					"update Release_ set releaseId = ",
					ReleaseConstants.DEFAULT_ID, ", servletContextName = '",
					ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
					"' where servletContextName = '",
					ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME, "-backup'"));
		}
	}

	@Test
	public void testDefineNewMajorSchemaVersion() throws Exception {
		Version previousMajorSchemaVersion = new Version(
			_currentSchemaVersion.getMajor() - 1, 0, 0);

		_updateSchemaVersion(previousMajorSchemaVersion);

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertFalse(
				"Major schema version changes require the upgrade tool " +
					"execution",
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	@Test
	public void testDefineNewMicroSchemaVersion() throws Exception {
		if (_currentSchemaVersion.getMicro() > 0) {
			Version previousMicroSchemaVersion = new Version(
				_currentSchemaVersion.getMajor(),
				_currentSchemaVersion.getMinor(),
				_currentSchemaVersion.getMicro() - 1);

			_updateSchemaVersion(previousMicroSchemaVersion);
		}

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				"Micro schema version changes must be optional",
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	@Test
	public void testDefineNewMinorSchemaVersion() throws Exception {
		Version previousMinorSchemaVersion = new Version(
			_currentSchemaVersion.getMajor(),
			_currentSchemaVersion.getMinor() - 1, 0);

		_updateSchemaVersion(previousMinorSchemaVersion);

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertFalse(
				"Minor schema version changes require the upgrade tool " +
					"exectution",
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	@Test
	public void testGetLatestSchemaVersion() {
		Set<Version> pendingSchemaVersions = ReflectionTestUtil.invoke(
			_innerPortalUpgradeProcess, "getPendingSchemaVersions",
			new Class<?>[] {Version.class}, _ORIGINAL_SCHEMA_VERSION);

		Iterator<Version> iterator = pendingSchemaVersions.iterator();

		Version latestSchemaVersion = iterator.next();

		while (iterator.hasNext()) {
			latestSchemaVersion = iterator.next();
		}

		Assert.assertEquals(
			latestSchemaVersion, PortalUpgradeProcess.getLatestSchemaVersion());
	}

	@Test
	public void testGetRequiredSchemaVersion() {
		Version latestSchemaVersion =
			PortalUpgradeProcess.getLatestSchemaVersion();

		Version requiredSchemaVersion =
			PortalUpgradeProcess.getRequiredSchemaVersion();

		Assert.assertEquals(
			latestSchemaVersion.getMinor(), requiredSchemaVersion.getMinor());

		Assert.assertEquals(
			latestSchemaVersion.getMajor(), requiredSchemaVersion.getMajor());
	}

	@Test
	public void testGetRequiredSchemaVersionWithMultipleSteps()
		throws Exception {

		UpgradeVersionTreeMap newUpgradeProcesses = new UpgradeVersionTreeMap();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PortalUpgradeProcess.class, "_upgradeVersionTreeMap",
					newUpgradeProcesses)) {

			newUpgradeProcesses.put(
				new Version(2, 3, 2), new DummyUpgradeProcess());
			newUpgradeProcesses.put(
				new Version(2, 4, 0), new DummyUpgradeProcess(),
				new DummyUpgradeProcess(), new DummyUpgradeProcess());
			newUpgradeProcesses.put(
				new Version(2, 4, 1), new DummyUpgradeProcess());

			Version requiredSchemaVersion =
				PortalUpgradeProcess.getRequiredSchemaVersion();

			Assert.assertEquals(2, requiredSchemaVersion.getMajor());
			Assert.assertEquals(4, requiredSchemaVersion.getMinor());
			Assert.assertEquals(0, requiredSchemaVersion.getMicro());

			Assert.assertEquals(
				StringPool.BLANK, requiredSchemaVersion.getQualifier());
		}
	}

	@Test
	public void testIsInLatestSchemaVersion() throws Exception {
		_updateSchemaVersion(PortalUpgradeProcess.getLatestSchemaVersion());

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				PortalUpgradeProcess.isInLatestSchemaVersion(connection));
		}
	}

	@Test
	public void testIsInRequiredSchemaVersion() throws Exception {
		_updateSchemaVersion(PortalUpgradeProcess.getRequiredSchemaVersion());

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	@Test
	public void testIsNotInLatestSchemaVersion() throws Exception {
		_updateSchemaVersion(_ORIGINAL_SCHEMA_VERSION);

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertFalse(
				PortalUpgradeProcess.isInLatestSchemaVersion(connection));
		}
	}

	@Test
	public void testRevertCodeToPreviousMajorSchemaVersion() throws Exception {
		Version nextMajorSchemaVersion = new Version(
			_currentSchemaVersion.getMajor() + 1, 0, 0);

		_updateSchemaVersion(nextMajorSchemaVersion);

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertFalse(
				"Major schema version changes must be nonrevertible",
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	@Test
	public void testRevertCodeToPreviousMicroSchemaVersion() throws Exception {
		Version nextMicroSchemaVersion = new Version(
			_currentSchemaVersion.getMajor(), _currentSchemaVersion.getMinor(),
			_currentSchemaVersion.getMicro() + 1);

		_updateSchemaVersion(nextMicroSchemaVersion);

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				"Micro schema version changes must be revertible",
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	@Test
	public void testRevertCodeToPreviousMinorSchemaVersion() throws Exception {
		Version nextMinorSchemaVersion = new Version(
			_currentSchemaVersion.getMajor(),
			_currentSchemaVersion.getMinor() + 1, 0);

		_updateSchemaVersion(nextMinorSchemaVersion);

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				"Minor schema version changes must be revertible",
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	@Test
	public void testSupportsRetry() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(PortalUpgradeProcess.supportsRetry(connection));
		}

		_testSupportsRetry(6210);
		_testSupportsRetry(7010);
	}

	@Test
	public void testUpdateVersionDisplayName() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"update Release_ set versionDisplayName = ",
				"'wrongVersionDisplayName' where servletContextName = '",
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME, "'"));

		try (Connection connection = DataAccess.getConnection()) {
			_updateVersionDisplayName();

			Assert.assertEquals(
				ReleaseInfo.getVersionDisplayName(),
				PortalUpgradeProcess.getCurrentVersionDisplayName(connection));
		}
		finally {
			db.runSQL(
				StringBundler.concat(
					"update Release_ set versionDisplayName = '",
					ReleaseInfo.getVersionDisplayName(),
					"' where servletContextName = '",
					ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME, "'"));
		}
	}

	@Test
	public void testUpgradeWhenCoreIsInLatestSchemaVersion() throws Exception {
		_updateSchemaVersion(PortalUpgradeProcess.getLatestSchemaVersion());

		PortalUpgradeProcess portalServiceUpgrade = new PortalUpgradeProcess();

		try {
			portalServiceUpgrade.upgrade();
		}
		catch (Exception exception) {
			throw new SQLException(
				"No upgrade processes should have been executed", exception);
		}

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				PortalUpgradeProcess.isInLatestSchemaVersion(connection));
		}
	}

	@Test
	public void testUpgradeWhenCoreIsInRequiredSchemaVersion()
		throws Exception {

		_updateSchemaVersion(PortalUpgradeProcess.getRequiredSchemaVersion());

		PortalUpgradeProcess portalServiceUpgrade = new PortalUpgradeProcess();

		try {
			portalServiceUpgrade.upgrade();
		}
		catch (Exception exception) {
			throw new SQLException(
				"The execution of the upgrade process failed after being " +
					"reexecuted. Upgrade processes must be harmless if they " +
						"were executed previously.",
				exception);
		}

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				PortalUpgradeProcess.isInLatestSchemaVersion(connection));
		}
	}

	@Test
	public void testValidateCoreIsInRequiredSchemaVersion() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertTrue(
				"You must first upgrade the portal to the required schema " +
					"version " +
						PortalUpgradeProcess.getRequiredSchemaVersion(),
				PortalUpgradeProcess.isInRequiredSchemaVersion(connection));
		}
	}

	private void _testSupportsRetry(int buildNumber) throws Exception {
		Release release = _releaseLocalService.fetchRelease(
			ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

		int currentBuildNumber = release.getBuildNumber();

		release.setBuildNumber(buildNumber);

		release = _releaseLocalService.updateRelease(release);

		try (Connection connection = DataAccess.getConnection()) {
			Assert.assertFalse(PortalUpgradeProcess.supportsRetry(connection));
		}
		finally {
			release = _releaseLocalService.fetchRelease(
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

			release.setBuildNumber(currentBuildNumber);

			_releaseLocalService.updateRelease(release);
		}
	}

	private void _updateSchemaVersion(Version version) throws Exception {
		_innerPortalUpgradeProcess.updateSchemaVersion(version);
	}

	private void _updateVersionDisplayName() throws Exception {
		_innerPortalUpgradeProcess.updateVersionDisplayName();
	}

	private static final Version _ORIGINAL_SCHEMA_VERSION = new Version(
		0, 0, 0);

	private static Version _currentSchemaVersion;

	private InnerPortalUpgradeProcess _innerPortalUpgradeProcess;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	private static class InnerPortalUpgradeProcess
		extends PortalUpgradeProcess {

		public void close() throws SQLException {
			connection.close();
		}

		public void updateSchemaVersion(Version newSchemaVersion)
			throws SQLException {

			PortalUpgradeProcess.updateSchemaVersion(
				connection, newSchemaVersion);
		}

		public void updateVersionDisplayName() throws SQLException {
			PortalUpgradeProcess.updateVersionDisplayName(connection);
		}

		private InnerPortalUpgradeProcess() throws SQLException {
			connection = DataAccess.getConnection();
		}

	}

}