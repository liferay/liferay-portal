/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class OrphanReferencesDataCleanupUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();
		_db = DBManagerUtil.getDB();

		_dbInspector = new DBInspector(_connection);
	}

	@AfterClass
	public static void tearDownClass() {
		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testCleanUpTableDoesNotAffectControlTablesWithDatabasePartitionEnabled()
		throws Exception {

		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			_testCleanUpTable(
				logCapture -> {
					List<LogEntry> logEntries = logCapture.getLogEntries();

					Assert.assertTrue(
						logEntries.toString(), logEntries.isEmpty());
				},
				() -> {
				},
				null,
				() -> {
				},
				false, null, "companyId", "VirtualHost", "companyId",
				"Company");
		}
	}

	@Test
	public void testCleanUpTableExcludedTable() throws Exception {
		long auditEventId = RandomTestUtil.nextLong();
		long companyId = RandomTestUtil.nextLong();

		_testCleanUpTable(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
			},
			() -> _db.runSQL(
				_connection,
				"delete from Audit_AuditEvent where auditEventId = " +
					auditEventId),
			null,
			() -> _db.runSQL(
				_connection,
				StringBundler.concat(
					"insert into Audit_AuditEvent (auditEventId, companyId, ",
					"className) values (", auditEventId, ", ", companyId, ", '",
					OrphanReferencesDataCleanupUtilTest.class.getName(), "')")),
			false, null, "companyId", "Audit_AuditEvent", "companyId",
			"Company");
	}

	@Test
	public void testCleanUpTableWithJoinClause() throws Exception {
		String portletId = "com.liferay." + RandomTestUtil.randomString();

		String instancePortletId =
			portletId + "_INSTANCE_" + RandomTestUtil.randomString();
		String userPortletId =
			portletId + "_USER_" + RandomTestUtil.randomString();

		_testCleanUpTable(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					_getCleanUpTableExpectedMessage(
						1, false, "portletId", "PortletPreferences",
						"portletId", "Portlet", userPortletId),
					logEntry.getMessage());
			},
			() -> {
				_db.runSQL(
					_connection,
					"delete from Portlet where portletId = '" + portletId +
						"'");
				_db.runSQL(
					_connection,
					"delete from PortletPreferences where portletId = '" +
						instancePortletId + "'");
				_db.runSQL(
					_connection,
					"delete from PortletPreferences where portletId = '" +
						userPortletId + "'");
			},
			SQLTransformer.transform(
				StringBundler.concat(
					"CASE WHEN INSTR([$SOURCE_TABLE_ALIAS$].portletId, ",
					"'_INSTANCE_') > 0 THEN SUBSTR([$SOURCE_TABLE_ALIAS$].",
					"portletId, 1, INSTR([$SOURCE_TABLE_ALIAS$].portletId, ",
					"'_INSTANCE_') - 1) ELSE [$SOURCE_TABLE_ALIAS$].portletId ",
					"END ")),
			() -> {
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into Portlet (mvccVersion, id_, portletId) ",
						"values (0, ", RandomTestUtil.nextLong(), ", '",
						portletId, "')"));
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into PortletPreferences (mvccVersion, ",
						"ctCollectionId, portletPreferencesId, ownerType, ",
						"portletId) values (0, 0, ", RandomTestUtil.nextLong(),
						", 99, '", instancePortletId, "')"));
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into PortletPreferences (mvccVersion, ",
						"ctCollectionId, portletPreferencesId, ownerType, ",
						"portletId) values (0, 0, ", RandomTestUtil.nextLong(),
						", 99, '", userPortletId, "')"));
			},
			false, "ownerType = 99", "portletId", "PortletPreferences",
			"portletId", "Portlet");
	}

	@Test
	public void testCleanUpTableWithoutWhereClause() throws Exception {
		long companyId = RandomTestUtil.nextLong();

		_testCleanUpTable(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					_getCleanUpTableExpectedMessage(
						2, false, "companyId", "Portlet", "companyId",
						"Company", companyId),
					logEntry.getMessage());
			},
			() -> _db.runSQL(
				_connection,
				"delete from Portlet where companyId = " + companyId),
			null,
			() -> {
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into Portlet (mvccVersion, id_, companyId, ",
						"portletId, active_) values (0, ",
						RandomTestUtil.nextLong(), ", ", companyId, ", '",
						RandomTestUtil.randomString(), "', [$FALSE$])"));
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into Portlet (mvccVersion, id_, companyId, ",
						"portletId, active_) values (0, ",
						RandomTestUtil.nextLong(), ", ", companyId, ", '",
						RandomTestUtil.randomString(), "', [$FALSE$])"));
			},
			false, null, "companyId", "Portlet", "companyId", "Company");
	}

	@Test
	public void testCleanUpTableWithoutWhereClauseInReadOnlyMode()
		throws Exception {

		long companyId = RandomTestUtil.nextLong();

		_testCleanUpTable(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					_getCleanUpTableExpectedMessage(
						2, true, "companyId", "Portlet", "companyId", "Company",
						companyId),
					logEntry.getMessage());
			},
			() -> _db.runSQL(
				_connection,
				"delete from Portlet where companyId = " + companyId),
			null,
			() -> {
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into Portlet (mvccVersion, id_, companyId, ",
						"portletId, active_) values (0, ",
						RandomTestUtil.nextLong(), ", ", companyId, ", '",
						RandomTestUtil.randomString(), "', [$FALSE$])"));
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into Portlet (mvccVersion, id_, companyId, ",
						"portletId, active_) values (0, ",
						RandomTestUtil.nextLong(), ", ", companyId, ", '",
						RandomTestUtil.randomString(), "', [$FALSE$])"));
			},
			true, null, "companyId", "Portlet", "companyId", "Company");
	}

	@Test
	public void testCleanUpTableWithWhereClause() throws Exception {
		long companyId = RandomTestUtil.nextLong();
		long ownerType1 = PortletKeys.PREFS_OWNER_TYPE_COMPANY;
		long ownerType2 = PortletKeys.PREFS_OWNER_TYPE_GROUP;

		_testCleanUpTable(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					_getCleanUpTableExpectedMessage(
						2, false, "ownerId", "PortletPreferences", "companyId",
						"Company", companyId),
					logEntry.getMessage());
			},
			() -> _db.runSQL(
				_connection,
				"delete from PortletPreferences where companyId = " +
					companyId),
			null,
			() -> {
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into PortletPreferences (mvccVersion, ",
						"ctCollectionId, portletPreferencesId, ownerId, ",
						"ownerType, companyId, portletId) values (0, 0, ",
						RandomTestUtil.nextLong(), ", ", companyId, ", ",
						ownerType1, ", ", companyId, ", '",
						RandomTestUtil.randomString(), "')"));
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into PortletPreferences (mvccVersion, ",
						"ctCollectionId, portletPreferencesId, ownerId, ",
						"ownerType, companyId, portletId) values (0, 0, ",
						RandomTestUtil.nextLong(), ", ", companyId, ", ",
						ownerType1, ", ", companyId, ", '",
						RandomTestUtil.randomString(), "')"));
				_db.runSQL(
					_connection,
					StringBundler.concat(
						"insert into PortletPreferences (mvccVersion, ",
						"ctCollectionId, portletPreferencesId, ownerId, ",
						"ownerType, companyId, portletId) values (0, 0, ",
						RandomTestUtil.nextLong(), ", ", companyId, ", ",
						ownerType2, ", ", companyId, ", '",
						RandomTestUtil.randomString(), "')"));
			},
			false, "ownerType = " + ownerType1, "ownerId", "PortletPreferences",
			"companyId", "Company");
	}

	@Test
	public void testCleanUpTableWrongCompanyWithDatabasePartitionEnabled()
		throws Exception {

		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		long companyId = RandomTestUtil.nextLong();

		_testCleanUpTable(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					_getCleanUpTableExpectedMessage(
						1, false, "companyId", "DLFileEntry", "companyId",
						"Company", companyId),
					logEntry.getMessage());
			},
			() -> {
				_db.runSQL(
					"delete from Company where companyId = " + companyId);
				_db.runSQL(
					_connection,
					"delete from DLFileEntry where companyId = " + companyId);
			},
			null,
			() -> {
				_db.runSQL(
					StringBundler.concat(
						"insert into Company (mvccVersion, companyId) values ",
						"(0 ,", companyId, ")"));
				_db.runSQL(
					StringBundler.concat(
						"insert into DLFileEntry (mvccVersion, ",
						"ctCollectionId, fileEntryId, companyId) values (0, ",
						"0,", RandomTestUtil.nextLong(), ", ", companyId, ")"));
			},
			false, null, "companyId", "DLFileEntry", "companyId", "Company");
	}

	private String _getCleanUpTableExpectedMessage(
			long count, boolean readOnly, String sourceColumnName,
			String sourceTableName, String targetColumnName,
			String targetTableName, Object targetValue)
		throws Exception {

		return StringBundler.concat(
			"Table ", _normalizeTableName(sourceTableName), ", ", count,
			(count == 1) ? " row " : " rows ", readOnly ? "should be " : "",
			"deleted because ", _dbInspector.normalizeName(sourceColumnName),
			StringPool.SPACE, targetValue, " was not found in column ",
			_dbInspector.normalizeName(targetColumnName), " from table ",
			_dbInspector.normalizeName(targetTableName));
	}

	private String _normalizeTableName(String tableName) throws Exception {
		tableName = _dbInspector.normalizeName(tableName);

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return tableName;
		}

		long companyId = CompanyThreadLocal.getNonsystemCompanyId();

		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			return tableName;
		}

		return StringBundler.concat(
			PropsValues.DATABASE_PARTITION_SCHEMA_NAME_PREFIX, companyId,
			StringPool.PERIOD, tableName);
	}

	private void _testCleanUpTable(
			UnsafeConsumer<LogCapture, Exception> assertUnsafeConsumer,
			UnsafeRunnable<Exception> cleanUpDataUnsafeRunnable,
			String customJoinClause,
			UnsafeRunnable<Exception> initializeDataUnsafeRunnable,
			boolean readOnly, String sourceAdditionalWhereClause,
			String sourceColumnName, String sourceTableName,
			String targetColumnName, String targetTableName)
		throws Exception {

		initializeDataUnsafeRunnable.run();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OrphanReferencesDataCleanupUtil.class.getName(),
				LoggerTestUtil.INFO)) {

			OrphanReferencesDataCleanupUtil.cleanUpTable(
				_connection,
				(customJoinClause != null) ? new String[] {customJoinClause} :
					null,
				readOnly, sourceAdditionalWhereClause,
				_dbInspector.normalizeName(sourceColumnName),
				_dbInspector.normalizeName(sourceTableName),
				new String[] {_dbInspector.normalizeName(targetColumnName)},
				_dbInspector.normalizeName(targetTableName));

			assertUnsafeConsumer.accept(logCapture);
		}
		finally {
			cleanUpDataUnsafeRunnable.run();
		}
	}

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

}