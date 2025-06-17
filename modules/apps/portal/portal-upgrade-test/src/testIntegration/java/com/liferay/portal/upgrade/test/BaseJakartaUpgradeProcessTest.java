/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.BaseJakartaUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class BaseJakartaUpgradeProcessTest extends BaseJakartaUpgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_db = DBManagerUtil.getDB();

		_companyLocalService.forEachCompany(
			company -> _db.runSQL(
				StringBundler.concat(
					"create table ", _TABLE_NAME,
					" (mvccVersion LONG default 0 not null, uuid_ VARCHAR(75) ",
					"not null, ", _COLUMN_NAME_1, " TEXT null, ",
					_COLUMN_NAME_2, " VARCHAR(255) null, ", _COLUMN_NAME_3,
					" STRING null, ", _COLUMN_NAME_4,
					" TEXT null, primary key (mvccVersion, uuid_))")));
	}

	@After
	public void tearDown() throws Exception {
		_companyLocalService.forEachCompany(
			company -> _db.runSQL("drop table " + _TABLE_NAME));
	}

	@Test
	public void testUpgradeWithCustomSeparators() throws Exception {
		_insertInitialData("import javax$portlet$Portlet");

		_testUpgrade(
			"import jakarta$portlet$Portlet",
			new BaseJakartaUpgradeProcessTest() {

				@Override
				public char[] getCustomSeparators() {
					return new char[] {'$'};
				}

			});
	}

	@Test
	public void testUpgradeWithoutCustomSeparators() throws Exception {
		_insertInitialData("import javax.portlet.Portlet");

		_testUpgrade("import jakarta.portlet.Portlet", this);
	}

	@Override
	protected String[][] getTableAndColumnNames() {
		return new String[][] {
			{_TABLE_NAME, _COLUMN_NAME_1}, {_TABLE_NAME, _COLUMN_NAME_2},
			{_TABLE_NAME, _COLUMN_NAME_3}, {_TABLE_NAME, _COLUMN_NAME_4}
		};
	}

	private void _assertLogEntry(
		Set<String> expectedKeys, String expectedMessage, String message) {

		Assert.assertTrue(message, message.contains(expectedMessage));

		for (String expectedKey : expectedKeys) {
			Assert.assertTrue(message, message.contains(expectedKey));
		}
	}

	private String _getLogEntryString(
			String columnName, long companyId, boolean updated)
		throws Exception {

		String message = "";

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (DBPartition.isPartitionEnabled()) {
				message = " for company " + companyId;
			}

			if (updated) {
				return StringBundler.concat(
					"Table ", dbInspector.normalizeName(_TABLE_NAME),
					" column ", dbInspector.normalizeName(columnName), message,
					" was updated for records with primary keys (",
					dbInspector.normalizeName("mvccVersion"), ", ",
					dbInspector.normalizeName("uuid_"), "): ");
			}

			return StringBundler.concat(
				"Table ", dbInspector.normalizeName(_TABLE_NAME), " column ",
				dbInspector.normalizeName(columnName), message,
				" was not updated");
		}
	}

	private void _insertInitialData(String javaxValue) throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				_db.runSQL(
					StringBundler.concat(
						"insert into ", _TABLE_NAME, " (mvccVersion, uuid_, ",
						_COLUMN_NAME_1, ", ", _COLUMN_NAME_2, ", ",
						_COLUMN_NAME_3, ") values (0, 'uuid1', '", javaxValue,
						"', '", javaxValue, "', '", javaxValue, "')"));
				_db.runSQL(
					StringBundler.concat(
						"insert into ", _TABLE_NAME, " (mvccVersion, uuid_, ",
						_COLUMN_NAME_1, ", ", _COLUMN_NAME_2,
						") values (1, 'uuid2', '", javaxValue, "', '",
						javaxValue, "')"));
			});
	}

	private void _testUpgrade(
			String jakartaValue, UpgradeProcess upgradeProcess)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseJakartaUpgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			upgradeProcess.upgrade();

			try (Connection connection = DataAccess.getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select * from " + _TABLE_NAME + " order by uuid_ asc");
				ResultSet resultSet = preparedStatement.executeQuery()) {

				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(0, resultSet.getLong(1));
				Assert.assertEquals("uuid1", resultSet.getString(2));
				Assert.assertEquals(
					jakartaValue, resultSet.getString(_COLUMN_NAME_1));
				Assert.assertEquals(
					jakartaValue, resultSet.getString(_COLUMN_NAME_2));
				Assert.assertEquals(
					jakartaValue, resultSet.getString(_COLUMN_NAME_3));
				Assert.assertNull(resultSet.getString(_COLUMN_NAME_4));

				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(1, resultSet.getLong(1));
				Assert.assertEquals("uuid2", resultSet.getString(2));
				Assert.assertEquals(
					jakartaValue, resultSet.getString(_COLUMN_NAME_1));
				Assert.assertEquals(
					jakartaValue, resultSet.getString(_COLUMN_NAME_2));
				Assert.assertNull(resultSet.getString(_COLUMN_NAME_3));
				Assert.assertNull(resultSet.getString(_COLUMN_NAME_4));

				Assert.assertFalse(resultSet.next());
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			int logEntriesSize = 4;

			if (DBPartition.isPartitionEnabled()) {
				long[] companyIds = PortalInstancePool.getCompanyIds();

				logEntriesSize *= companyIds.length;
			}

			Assert.assertEquals(
				logEntries.toString(), logEntries.size(), logEntriesSize);

			int i = 0;

			for (long companyId :
					(long[])ReflectionTestUtil.invoke(
						PortalInstancePool.class, "_getCompanyIdsBySQL",
						null)) {

				_assertLogEntry(
					new HashSet<>(Arrays.asList("(0, uuid1)", "(1, uuid2)")),
					_getLogEntryString(_COLUMN_NAME_1, companyId, true),
					String.valueOf(logEntries.get(i++)));
				_assertLogEntry(
					new HashSet<>(Arrays.asList("(0, uuid1)", "(1, uuid2)")),
					_getLogEntryString(_COLUMN_NAME_2, companyId, true),
					String.valueOf(logEntries.get(i++)));
				_assertLogEntry(
					new HashSet<>(Arrays.asList("(0, uuid1)")),
					_getLogEntryString(_COLUMN_NAME_3, companyId, true),
					String.valueOf(logEntries.get(i++)));
				_assertLogEntry(
					new HashSet<>(),
					_getLogEntryString(_COLUMN_NAME_4, companyId, false),
					String.valueOf(logEntries.get(i++)));
			}
		}
	}

	private static final String _COLUMN_NAME_1 = "script1";

	private static final String _COLUMN_NAME_2 = "script2";

	private static final String _COLUMN_NAME_3 = "script3";

	private static final String _COLUMN_NAME_4 = "script4";

	private static final String _TABLE_NAME = "BaseJakartaUpgradeProcessTest";

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static DB _db;

}