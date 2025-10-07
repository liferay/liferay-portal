/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.NullUnicodeContentDataCleanupPreupgradeProcess;

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
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class NullUnicodeContentDataCleanupPreupgradeProcessTest
	extends NullUnicodeContentDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_db = DBManagerUtil.getDB();

		_dbInspector = new DBInspector(_connection);

		_db.alterTableAddColumn(
			_connection, "JournalArticle", "content", "TEXT null");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		try {
			_db.alterTableDropColumn(_connection, "JournalArticle", "content");
			_db.runSQL("delete from JournalArticle where id_ = " + _journalId);
			_db.runSQL(
				"delete from DDMContent where contentId = " + _contentId);
		}
		finally {
			DataAccess.cleanUp(_connection);
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		String cleanContent = RandomTestUtil.randomString();

		String content = "'" + cleanContent + "\\u0000'";

		if (_db.getDBType() == DBType.SQLSERVER) {
			content = "N" + content;
		}

		_contentId = RandomTestUtil.nextLong();

		runSQL(
			_connection,
			StringBundler.concat(
				"insert into DDMContent (",
				"mvccVersion, ctCollectionId, contentId, groupId, data_) ",
				"values (0, 0, ", _contentId, ", ", RandomTestUtil.nextLong(),
				", ", content, ")"));

		_journalId = RandomTestUtil.nextLong();

		runSQL(
			_connection,
			StringBundler.concat(
				"insert into JournalArticle (",
				"mvccVersion, ctCollectionId, id_, groupId, content) values (",
				"0, 0, ", _journalId, ", ", RandomTestUtil.nextLong(), ", ",
				content, ")"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				NullUnicodeContentDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 2, messages.size());
			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", _dbInspector.normalizeName("DDMContent"),
						", 1 row updated column ",
						_dbInspector.normalizeName("data_"),
						" because it had invalid characters")));
			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", _dbInspector.normalizeName("JournalArticle"),
						", 1 row updated column ",
						_dbInspector.normalizeName("content"),
						" because it had invalid characters")));
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select data_ from DDMContent where contentId = " + _contentId);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(cleanContent, resultSet.getString(1));
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select content from JournalArticle where id_ = " + _journalId);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(cleanContent, resultSet.getString(1));
		}
	}

	private static Connection _connection;
	private static long _contentId;
	private static DB _db;
	private static DBInspector _dbInspector;
	private static long _journalId;

}