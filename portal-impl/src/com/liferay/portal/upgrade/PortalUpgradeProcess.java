/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeVersionTreeMap;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.upgrade.util.PortalUpgradeProcessRegistry;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Function;

/**
 * @author Alberto Chaparro
 */
public class PortalUpgradeProcess extends UpgradeProcess {

	public static void createPortalRelease(Connection connection)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into Release_ (releaseId, createDate, ",
					"modifiedDate, servletContextName, schemaVersion, ",
					"buildNumber, buildDate, verified, testString) values (",
					ReleaseConstants.DEFAULT_ID,
					", ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			Date date = new Date(System.currentTimeMillis());

			preparedStatement.setDate(1, date);
			preparedStatement.setDate(2, date);

			preparedStatement.setString(
				3, ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

			Version schemaVersion = getLatestSchemaVersion();

			preparedStatement.setString(4, String.valueOf(schemaVersion));

			preparedStatement.setInt(5, ReleaseInfo.getBuildNumber());

			java.util.Date buildDate = ReleaseInfo.getBuildDate();

			preparedStatement.setDate(6, new Date(buildDate.getTime()));

			preparedStatement.setBoolean(7, false);
			preparedStatement.setString(8, ReleaseConstants.TEST_STRING);

			preparedStatement.executeUpdate();

			_currentPortalReleaseDTODCLSingleton.destroy(null);

			boolean supportsStringCaseSensitiveQuery =
				!_hasDefaultReleaseWithTestString(
					connection,
					StringUtil.toUpperCase(ReleaseConstants.TEST_STRING));

			_currentPortalReleaseDTODCLSingleton.getSingleton(
				() -> new PortalReleaseDTO(
					schemaVersion, ReleaseInfo.getBuildNumber(), buildDate, 0,
					ReleaseConstants.TEST_STRING,
					supportsStringCaseSensitiveQuery));
		}
	}

	public static java.util.Date getCurrentBuildDate(Connection connection)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		if (portalReleaseDTO == PortalReleaseDTO._NULL_INSTANCE) {
			return null;
		}

		return portalReleaseDTO._buildDate;
	}

	public static int getCurrentBuildNumber(Connection connection)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		if (portalReleaseDTO == PortalReleaseDTO._NULL_INSTANCE) {
			return 0;
		}

		return portalReleaseDTO._buildNumber;
	}

	public static Version getCurrentSchemaVersion(Connection connection)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		if (portalReleaseDTO == PortalReleaseDTO._NULL_INSTANCE) {
			return new Version(0, 0, 0);
		}

		return portalReleaseDTO._schemaVersion;
	}

	public static int getCurrentState(Connection connection)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		if (portalReleaseDTO == PortalReleaseDTO._NULL_INSTANCE) {
			throw new IllegalArgumentException(
				"No Release exists with the primary key " +
					ReleaseConstants.DEFAULT_ID);
		}

		return portalReleaseDTO._state;
	}

	public static String getCurrentTestString(Connection connection)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		if (portalReleaseDTO == PortalReleaseDTO._NULL_INSTANCE) {
			return null;
		}

		return portalReleaseDTO._testString;
	}

	public static Version getLatestSchemaVersion() {
		return _upgradeVersionTreeMap.lastKey();
	}

	public static SortedMap<Version, List<UpgradeProcess>>
		getPendingUpgradeProcesses(Version schemaVersion) {

		return _upgradeVersionTreeMap.tailMap(schemaVersion, false);
	}

	public static Version getRequiredSchemaVersion() {
		NavigableSet<Version> reverseSchemaVersions =
			_upgradeVersionTreeMap.descendingKeySet();

		Iterator<Version> iterator = reverseSchemaVersions.iterator();

		Version requiredSchemaVersion = iterator.next();

		while (iterator.hasNext()) {
			Version nextSchemaVersion = iterator.next();

			if ((requiredSchemaVersion.getMajor() !=
					nextSchemaVersion.getMajor()) ||
				(requiredSchemaVersion.getMinor() !=
					nextSchemaVersion.getMinor())) {

				break;
			}

			if (requiredSchemaVersion.getMicro() !=
					nextSchemaVersion.getMicro()) {

				requiredSchemaVersion = nextSchemaVersion;
			}
		}

		return requiredSchemaVersion;
	}

	public static boolean hasPortalRelease(Connection connection)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		if (portalReleaseDTO == PortalReleaseDTO._NULL_INSTANCE) {
			return false;
		}

		return true;
	}

	public static boolean isInLatestSchemaVersion(Connection connection)
		throws SQLException {

		Version latestSchemaVersion = getLatestSchemaVersion();

		return latestSchemaVersion.equals(getCurrentSchemaVersion(connection));
	}

	public static boolean isInRequiredSchemaVersion(Connection connection)
		throws SQLException {

		Version currentSchemaVersion = getCurrentSchemaVersion(connection);

		Version requiredSchemaVersion = getRequiredSchemaVersion();

		int result = requiredSchemaVersion.compareTo(currentSchemaVersion);

		if ((result == 0) ||
			((result < 0) &&
			 (requiredSchemaVersion.getMajor() ==
				 currentSchemaVersion.getMajor()))) {

			return true;
		}

		return false;
	}

	public static boolean isSupportsStringCaseSensitiveQuery(
			Connection connection)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		if (portalReleaseDTO == PortalReleaseDTO._NULL_INSTANCE) {
			return true;
		}

		return portalReleaseDTO._supportsStringCaseSensitiveQuery;
	}

	public static boolean supportsRetry(Connection connection)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select buildNumber from Release_ where servletContextName = " +
					"?")) {

			preparedStatement.setString(
				1, ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					if (resultSet.getInt("buildNumber") >=
							ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER) {

						return true;
					}

					return false;
				}
			}
		}

		return false;
	}

	public static void updateBuildInfo(Connection connection)
		throws SQLException {

		java.util.Date buildDate = ReleaseInfo.getBuildDate();

		_updateRelease(
			connection, "buildNumber = ?, buildDate = ?",
			preparedStatement -> {
				preparedStatement.setInt(1, ReleaseInfo.getParentBuildNumber());

				preparedStatement.setDate(2, new Date(buildDate.getTime()));
			},
			portalReleaseDTO -> new PortalReleaseDTO(
				portalReleaseDTO._schemaVersion,
				ReleaseInfo.getParentBuildNumber(), buildDate,
				portalReleaseDTO._state, portalReleaseDTO._testString,
				portalReleaseDTO._supportsStringCaseSensitiveQuery));
	}

	public static void updateSchemaVersion(
			Connection connection, Version newSchemaVersion)
		throws SQLException {

		_updateRelease(
			connection, "schemaVersion = ?",
			preparedStatement -> preparedStatement.setString(
				1, newSchemaVersion.toString()),
			portalReleaseDTO -> new PortalReleaseDTO(
				newSchemaVersion, portalReleaseDTO._buildNumber,
				portalReleaseDTO._buildDate, portalReleaseDTO._state,
				portalReleaseDTO._testString,
				portalReleaseDTO._supportsStringCaseSensitiveQuery));
	}

	public static void updateState(Connection connection, int state)
		throws SQLException {

		_updateRelease(
			connection, "modifiedDate = ?, state_ = ?",
			preparedStatement -> {
				preparedStatement.setDate(
					1, new Date(System.currentTimeMillis()));
				preparedStatement.setInt(2, state);
			},
			portalReleaseDTO -> new PortalReleaseDTO(
				portalReleaseDTO._schemaVersion, portalReleaseDTO._buildNumber,
				portalReleaseDTO._buildDate, state,
				portalReleaseDTO._testString,
				portalReleaseDTO._supportsStringCaseSensitiveQuery));
	}

	@Override
	public void upgrade() throws UpgradeException {
		long start = System.currentTimeMillis();

		String message = "Completed upgrade process ";

		try (Connection connection = getConnection()) {
			this.connection = connection;

			if (_log.isInfoEnabled()) {
				String info = "Upgrading " + ClassUtil.getClassName(this);

				_log.info(info);
			}

			doUpgrade();

			closeConnections();
		}
		catch (Exception exception) {
			message = "Failed upgrade process ";

			throw new UpgradeException(exception);
		}
		finally {
			this.connection = null;

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						message, ClassUtil.getClassName(this), " in ",
						System.currentTimeMillis() - start, " ms"));
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		_initializeRelease(connection);

		for (Version pendingSchemaVersion :
				getPendingSchemaVersions(getCurrentSchemaVersion(connection))) {

			List<UpgradeProcess> upgradeProcesses = _upgradeVersionTreeMap.get(
				pendingSchemaVersion);

			for (UpgradeProcess upgradeProcess : upgradeProcesses) {
				upgrade(upgradeProcess);
			}

			updateSchemaVersion(connection, pendingSchemaVersion);
		}

		clearIndexesCache();
	}

	protected Set<Version> getPendingSchemaVersions(Version fromSchemaVersion) {
		SortedMap<Version, List<UpgradeProcess>> pendingUpgradeProcesses =
			_upgradeVersionTreeMap.tailMap(fromSchemaVersion, false);

		return pendingUpgradeProcesses.keySet();
	}

	private static PortalReleaseDTO _getCurrentPortalReleaseDTO(
			Connection connection)
		throws SQLException {

		return _currentPortalReleaseDTODCLSingleton.getSingleton(
			() -> {
				String sql = "testString = ?";

				DB db = DBManagerUtil.getDB();

				DBType dbType = db.getDBType();

				if ((dbType == DBType.ORACLE) || (dbType == DBType.SQLSERVER)) {
					sql = StringBundler.concat(
						"select count(*) from Release_ where releaseId = ",
						ReleaseConstants.DEFAULT_ID, " and testString = ?");
				}

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							StringBundler.concat(
								"select schemaVersion, buildNumber, ",
								"buildDate, state_, testString, (", sql,
								") as caseSensitive from Release_ where ",
								"releaseId = ", ReleaseConstants.DEFAULT_ID))) {

					preparedStatement.setString(
						1,
						StringUtil.toUpperCase(ReleaseConstants.TEST_STRING));

					try (ResultSet resultSet =
							preparedStatement.executeQuery()) {

						while (resultSet.next()) {
							Date buildDate = resultSet.getDate("buildDate");

							return new PortalReleaseDTO(
								Version.parseVersion(
									resultSet.getString("schemaVersion")),
								resultSet.getInt("buildNumber"),
								(buildDate != null) ?
									new java.util.Date(buildDate.getTime()) :
										null,
								resultSet.getInt("state_"),
								resultSet.getString("testString"),
								!resultSet.getBoolean("caseSensitive"));
						}
					}
				}
				catch (SQLException sqlException) {
					ReflectionUtil.throwException(sqlException);
				}

				return PortalReleaseDTO._NULL_INSTANCE;
			});
	}

	private static boolean _hasDefaultReleaseWithTestString(
			Connection connection, String testString)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) from Release_ where releaseId = ? and " +
					"testString = ?")) {

			preparedStatement.setLong(1, ReleaseConstants.DEFAULT_ID);
			preparedStatement.setString(2, testString);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next() && (resultSet.getInt(1) > 0)) {
					return true;
				}
			}
		}

		return false;
	}

	private static void _registerUpgradeProcesses(
		PortalUpgradeProcessRegistry... portalUpgradeProcessRegistries) {

		for (PortalUpgradeProcessRegistry portalUpgradeProcessRegistry :
				portalUpgradeProcessRegistries) {

			portalUpgradeProcessRegistry.registerUpgradeProcesses(
				_upgradeVersionTreeMap);
		}
	}

	private static void _updateRelease(
			Connection connection, String sqlSetClause,
			UnsafeConsumer<PreparedStatement, SQLException> consumer,
			Function<PortalReleaseDTO, PortalReleaseDTO> function)
		throws SQLException {

		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update Release_ set ", sqlSetClause, " where releaseId = ",
					ReleaseConstants.DEFAULT_ID))) {

			consumer.accept(preparedStatement);

			if (preparedStatement.executeUpdate() > 0) {
				_currentPortalReleaseDTODCLSingleton.destroy(null);

				_currentPortalReleaseDTODCLSingleton.getSingleton(
					() -> function.apply(portalReleaseDTO));
			}
		}
	}

	private void _initializeRelease(Connection connection) throws Exception {
		PortalReleaseDTO portalReleaseDTO = _getCurrentPortalReleaseDTO(
			connection);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update Release_ set schemaVersion = ?, buildNumber = ? ",
					"where releaseId = ", ReleaseConstants.DEFAULT_ID,
					" and buildNumber < ?"))) {

			preparedStatement.setString(1, _initialSchemaVersion.toString());
			preparedStatement.setInt(2, ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER);
			preparedStatement.setInt(3, ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER);

			if (preparedStatement.executeUpdate() > 0) {
				_currentPortalReleaseDTODCLSingleton.destroy(null);

				_currentPortalReleaseDTODCLSingleton.getSingleton(
					() -> new PortalReleaseDTO(
						_initialSchemaVersion, portalReleaseDTO._buildNumber,
						portalReleaseDTO._buildDate, portalReleaseDTO._state,
						portalReleaseDTO._testString,
						portalReleaseDTO._supportsStringCaseSensitiveQuery));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalUpgradeProcess.class);

	private static final DCLSingleton<PortalReleaseDTO>
		_currentPortalReleaseDTODCLSingleton = new DCLSingleton<>();
	private static final Version _initialSchemaVersion = new Version(0, 1, 0);
	private static final UpgradeVersionTreeMap _upgradeVersionTreeMap =
		new UpgradeVersionTreeMap() {
			{
				put(_initialSchemaVersion, new DummyUpgradeProcess());
			}
		};

	static {
		_registerUpgradeProcesses(
			new com.liferay.portal.upgrade.v7_1_x.
				PortalUpgradeProcessRegistryImpl(),
			new com.liferay.portal.upgrade.v7_2_x.
				PortalUpgradeProcessRegistryImpl(),
			new com.liferay.portal.upgrade.v7_3_x.
				PortalUpgradeProcessRegistryImpl(),
			new com.liferay.portal.upgrade.v7_4_x.
				PortalUpgradeProcessRegistryImpl());
	}

	private static class PortalReleaseDTO {

		private PortalReleaseDTO(
			Version schemaVersion, int buildNumber, java.util.Date buildDate,
			int state, String testString,
			boolean supportsStringCaseSensitiveQuery) {

			_schemaVersion = schemaVersion;
			_buildNumber = buildNumber;
			_buildDate = buildDate;
			_state = state;
			_testString = testString;
			_supportsStringCaseSensitiveQuery =
				supportsStringCaseSensitiveQuery;
		}

		private static final PortalReleaseDTO _NULL_INSTANCE =
			new PortalReleaseDTO(null, 0, null, -1, null, true);

		private final java.util.Date _buildDate;
		private final int _buildNumber;
		private final Version _schemaVersion;
		private final int _state;
		private final boolean _supportsStringCaseSensitiveQuery;
		private final String _testString;

	}

}