/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.List;

import org.junit.Assert;
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

	@Test
	public void testCleanUpExcludedTable() throws Exception {
		long auditEventId = RandomTestUtil.nextLong();
		long companyId = RandomTestUtil.nextLong();

		_test(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
			},
			() -> _db.runSQL(
				_connection,
				"delete from Audit_AuditEvent where auditEventId = " +
					auditEventId),
			() -> _db.runSQL(
				_connection,
				StringBundler.concat(
					"insert into Audit_AuditEvent (auditEventId, companyId, ",
					"className) values (", auditEventId, ", ", companyId, ", '",
					OrphanReferencesDataCleanupUtilTest.class.getName(), "')")),
			null, "companyId", "Audit_AuditEvent", "companyId", "Company");
	}

	@Test
	public void testCleanUpWithoutWhereClause() throws Exception {
		long companyId = RandomTestUtil.nextLong();

		_test(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					_getExpectedMessage(
						2, _dbInspector.normalizeName("Portlet"),
						_dbInspector.normalizeName("companyId"),
						_dbInspector.normalizeName("Company"), companyId),
					logEntry.getMessage());
			},
			() -> _db.runSQL(
				_connection,
				"delete from Portlet where companyId = " + companyId),
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
			null, "companyId", "Portlet", "companyId", "Company");
	}

	@Test
	public void testCleanUpWithWhereClause() throws Exception {
		long companyId = RandomTestUtil.nextLong();
		long ownerType1 = PortletKeys.PREFS_OWNER_TYPE_COMPANY;
		long ownerType2 = PortletKeys.PREFS_OWNER_TYPE_GROUP;

		_test(
			logCapture -> {
				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					_getExpectedMessage(
						2, _dbInspector.normalizeName("PortletPreferences"),
						_dbInspector.normalizeName("companyId"),
						_dbInspector.normalizeName("Company"), companyId),
					logEntry.getMessage());
			},
			() -> _db.runSQL(
				_connection,
				"delete from PortletPreferences where companyId = " +
					companyId),
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
			"ownerType = " + ownerType1, "ownerId", "PortletPreferences",
			"companyId", "Company");
	}

	private String _getExpectedMessage(
			long count, String sourceTableName, String targetColumn,
			String targetTable, long targetValue)
		throws Exception {

		return StringBundler.concat(
			count, " orphan entries from table ",
			_dbInspector.normalizeName(sourceTableName),
			" have been deleted because value ", targetValue,
			" was not found in the origin table ",
			_dbInspector.normalizeName(targetTable), " column ",
			_dbInspector.normalizeName(targetColumn));
	}

	private void _test(
			UnsafeConsumer<LogCapture, Exception> assertUnsafeConsumer,
			UnsafeRunnable<Exception> cleanUpDataUnsafeRunnable,
			UnsafeRunnable<Exception> initializeDataUnsafeRunnable,
			String sourceAdditionalWhereClause, String sourceColumnName,
			String sourceTableName, String targetColumnName,
			String targetTableName)
		throws Exception {

		initializeDataUnsafeRunnable.run();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OrphanReferencesDataCleanupUtil.class.getName(),
				LoggerTestUtil.INFO)) {

			OrphanReferencesDataCleanupUtil.cleanUpTable(
				_connection, sourceAdditionalWhereClause,
				_dbInspector.normalizeName(sourceColumnName),
				_dbInspector.normalizeName(sourceTableName),
				_dbInspector.normalizeName(targetColumnName),
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