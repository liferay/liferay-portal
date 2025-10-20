/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class SQLEqualsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_db = DBManagerUtil.getDB();

		_db.runSQL(
			StringBundler.concat(
				"create table SQLEqualsTest (pk LONG not null primary key, ",
				"typeBlob BLOB, typeBoolean BOOLEAN, typeDate DATE null, ",
				"typeDouble DOUBLE, typeInteger INTEGER, typeLong LONG null, ",
				"typeString STRING null, typeText TEXT null, typeVarchar ",
				"VARCHAR(75) null)"));

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"insert into SQLEqualsTest (pk, typeBlob, ",
						"typeBoolean, typeDate, typeDouble, typeInteger, ",
						"typeLong, typeString, typeText, typeVarchar) values (",
						"?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")))) {

			boolean autoCommit = connection.getAutoCommit();

			try {
				connection.setAutoCommit(false);

				_insert(
					preparedStatement, 1, "test", false, 5 * Time.DAY, 6.0D, 7,
					8L, "String", "Text", "VarChar");

				_insert(
					preparedStatement, 2, "test", false, 5 * Time.DAY, 6.0D, 7,
					8L, "String", "Text", "VarChar");

				_insert(
					preparedStatement, 3, "test 2", true, 6 * Time.DAY, 7.0D, 8,
					9L, "String 2", "Text 2", "VarChar 2");

				connection.commit();
			}
			catch (Exception exception) {
				connection.rollback();
			}
			finally {
				connection.setAutoCommit(autoCommit);
			}
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_db.runSQL("drop table SQLEqualsTest");
	}

	@Test
	public void testEquals() throws Exception {
		_assert("typeBlob", 2, " = ");
		_assert("typeBoolean", 2, " = ");
		_assert("typeDate", 2, " = ");
		_assert("typeDouble", 2, " = ");
		_assert("typeInteger", 2, " = ");
		_assert("typeLong", 2, " = ");
		_assert("typeString", 2, " = ");
		_assert("typeText", 2, " = ");
		_assert("typeVarchar", 2, " = ");
	}

	@Test
	public void testNotEquals() throws Exception {
		_assert("typeBlob", 3, " != ");
		_assert("typeBoolean", 3, " != ");
		_assert("typeDate", 3, " != ");
		_assert("typeDouble", 3, " != ");
		_assert("typeInteger", 3, " != ");
		_assert("typeLong", 3, " != ");
		_assert("typeString", 3, " != ");
		_assert("typeText", 3, " != ");
		_assert("typeVarchar", 3, " != ");
	}

	private static void _insert(
			PreparedStatement preparedStatement, int pk, String typeBlob,
			boolean typeBoolean, long typeDate, double typeDouble,
			int typeInteger, long typeLong, String typeString, String typeText,
			String typeVarchar)
		throws Exception {

		preparedStatement.setLong(1, pk);
		preparedStatement.setBlob(
			2,
			new UnsyncByteArrayInputStream(
				typeBlob.getBytes(StandardCharsets.US_ASCII)));
		preparedStatement.setBoolean(3, typeBoolean);
		preparedStatement.setDate(4, new Date(typeDate));
		preparedStatement.setDouble(5, typeDouble);
		preparedStatement.setInt(6, typeInteger);
		preparedStatement.setLong(7, typeLong);
		preparedStatement.setString(8, typeString);
		preparedStatement.setString(9, typeText);
		preparedStatement.setString(10, typeVarchar);

		preparedStatement.executeUpdate();
	}

	private void _assert(String columnName, long expectedPK, String compare)
		throws Exception {

		if (columnName.equals("typeBlob") &&
			((DBManagerUtil.getDBType() == DBType.ORACLE) ||
			 (DBManagerUtil.getDBType() == DBType.POSTGRESQL))) {

			return;
		}

		StringBundler sb = new StringBundler(9);

		sb.append("select t1.pk from SQLEqualsTest t1 inner join ");
		sb.append("SQLEqualsTest t2 on t1.pk != 1 and t2.pk = 1 and ");

		if (columnName.equals("typeBlob") &&
			(DBManagerUtil.getDBType() == DBType.SQLSERVER)) {

			sb.append("CAST(t1.");
			sb.append(columnName);
			sb.append(" as varbinary)");
			sb.append(compare);
			sb.append("CAST(t2.");
			sb.append(columnName);
			sb.append(" as varbinary)");
		}
		else if (columnName.equals("typeText")) {
			sb.append("CAST_CLOB_TEXT(t1.");
			sb.append(columnName);
			sb.append(")");
			sb.append(compare);
			sb.append("CAST_CLOB_TEXT(t2.");
			sb.append(columnName);
			sb.append(")");
		}
		else {
			sb.append("t1.");
			sb.append(columnName);
			sb.append(compare);
			sb.append("t2.");
			sb.append(columnName);
		}

		String sql = SQLTransformer.transform(sb.toString());

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(sql, resultSet.next());
			Assert.assertEquals(sql, expectedPK, resultSet.getLong(1));
			Assert.assertFalse(sql, resultSet.next());
		}
	}

	private static DB _db;

}