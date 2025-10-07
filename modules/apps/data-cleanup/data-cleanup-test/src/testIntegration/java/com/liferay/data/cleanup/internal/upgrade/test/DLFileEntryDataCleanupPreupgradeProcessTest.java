/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.DLFileEntryDataCleanupPreupgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class DLFileEntryDataCleanupPreupgradeProcessTest
	extends DLFileEntryDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testRemoveDLFileEntryOrphanData() throws Exception {
		long fileEntryId1 = RandomTestUtil.nextLong();

		runSQL(
			StringBundler.concat(
				"insert into DLFileEntry (",
				"mvccVersion, ctCollectionId, fileEntryId, groupId) values ",
				"(0, 0, ", fileEntryId1, ", ", RandomTestUtil.nextLong(), ")"));

		long fileEntryId2 = RandomTestUtil.nextLong();

		runSQL(
			StringBundler.concat(
				"insert into DLFileEntry (",
				"mvccVersion, ctCollectionId, fileEntryId, groupId, name) ",
				"values (0, 0, ", fileEntryId2, ", ", RandomTestUtil.nextLong(),
				", '')"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DLFileEntryDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO);
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.data.cleanup.internal.configuration." +
						"DataRemovalConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"removeDLFileEntryOrphanData", true
					).build())) {

			_assertNonexistentDLFileEntry(fileEntryId1);
			_assertNonexistentDLFileEntry(fileEntryId2);

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", _dbInspector.normalizeName("DLFileEntry"),
						", 1 row deleted because ",
						_dbInspector.normalizeName("fileEntryId"),
						StringPool.SPACE, fileEntryId1, StringPool.SPACE,
						_dbInspector.normalizeName("name"), " was null")));
			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", _dbInspector.normalizeName("DLFileEntry"),
						", 1 row deleted because ",
						_dbInspector.normalizeName("fileEntryId"),
						StringPool.SPACE, fileEntryId2, StringPool.SPACE,
						_dbInspector.normalizeName("name"), " was ",
						(DBManagerUtil.getDBType() == DBType.ORACLE) ? "null" :
							"empty")));
		}
		finally {
			runSQL(
				"delete from DLFileEntry where fileEntryId = " + fileEntryId1);
			runSQL(
				"delete from DLFileEntry where fileEntryId = " + fileEntryId2);
		}
	}

	private void _assertNonexistentDLFileEntry(long fileEntryId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) from DLFileEntry where fileEntryId = ?")) {

			preparedStatement.setLong(1, fileEntryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());
				Assert.assertEquals(0, resultSet.getInt(1));
			}
		}
	}

	private static Connection _connection;
	private static DBInspector _dbInspector;

}