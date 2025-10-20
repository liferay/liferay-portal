/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class BaseUuidUpgradeProcessTest extends BaseUuidUpgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_db.runSQL(
			"create table TestTable1 (id_ INTEGER not null primary key)");
		_db.runSQL(
			"create table TestTable2 (id_ INTEGER not null primary key)");
	}

	@After
	public void tearDown() throws Exception {
		_db.runSQL("DROP_TABLE_IF_EXISTS(TestTable1)");
		_db.runSQL("DROP_TABLE_IF_EXISTS(TestTable2)");
	}

	@Test
	public void testUuidUpgrade() throws Exception {
		_insertValues("TestTable1", 10000);
		_insertValues("TestTable2", 5000);

		upgrade();

		Assert.assertEquals(10000, _getDistinctUuidCount("TestTable1"));
		Assert.assertEquals(5000, _getDistinctUuidCount("TestTable2"));
	}

	protected String[] getTableNames() {
		return new String[] {"TestTable1", "TestTable2"};
	}

	private int _getDistinctUuidCount(String tableName) throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(distinct(uuid_)) from " + tableName)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				return resultSet.getInt(1);
			}
		}
	}

	private void _insertValues(String tableName, int total) throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, "insert into " + tableName + " values (?)")) {

			for (int i = 0; i < total; i++) {
				preparedStatement.setInt(1, i);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	private final DB _db = DBManagerUtil.getDB();

}