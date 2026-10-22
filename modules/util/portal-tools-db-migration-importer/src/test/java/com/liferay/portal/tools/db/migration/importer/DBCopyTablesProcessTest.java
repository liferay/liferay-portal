/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.sql.Types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBCopyTablesProcessTest {

	@Test
	@TestInfo("LPD-110097")
	public void testGetPostgreSQLColumnType() {
		_testGetPostgreSQLColumnType(0, Types.NUMERIC, 0, "numeric");
		_testGetPostgreSQLColumnType(10, Types.DECIMAL, 2, "decimal(30, 16)");
		_testGetPostgreSQLColumnType(20, Types.DECIMAL, -2, "numeric");
		_testGetPostgreSQLColumnType(20, Types.DECIMAL, 18, "numeric(20, 18)");
		_testGetPostgreSQLColumnType(30, Types.NUMERIC, 0, "numeric(30, 0)");
		_testGetPostgreSQLColumnType(
			75, Types.NVARCHAR, RandomTestUtil.randomInt(), "varchar(75)");
		_testGetPostgreSQLColumnType(
			75, Types.VARCHAR, RandomTestUtil.randomInt(), "varchar(75)");
		_testGetPostgreSQLColumnType(
			DB.SQL_VARCHAR_MAX_SIZE_THRESHOLD, Types.VARCHAR,
			RandomTestUtil.randomInt(), "text");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), 101, RandomTestUtil.randomInt(),
			"double precision");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.BIGINT,
			RandomTestUtil.randomInt(), "bigint");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.BINARY,
			RandomTestUtil.randomInt(), "bytea");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.BIT, RandomTestUtil.randomInt(),
			"bool");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.BLOB, RandomTestUtil.randomInt(),
			"oid");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.BOOLEAN,
			RandomTestUtil.randomInt(), "bool");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.CHAR, RandomTestUtil.randomInt(),
			"text");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.CLOB, RandomTestUtil.randomInt(),
			"text");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.DOUBLE,
			RandomTestUtil.randomInt(), "double precision");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.FLOAT, RandomTestUtil.randomInt(),
			"double precision");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.INTEGER,
			RandomTestUtil.randomInt(), "integer");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.LONGVARBINARY,
			RandomTestUtil.randomInt(), "bytea");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.LONGVARCHAR,
			RandomTestUtil.randomInt(), "text");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.SMALLINT,
			RandomTestUtil.randomInt(), "integer");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.TIMESTAMP,
			RandomTestUtil.randomInt(), "timestamp");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.TINYINT,
			RandomTestUtil.randomInt(), "integer");
		_testGetPostgreSQLColumnType(
			RandomTestUtil.randomInt(), Types.VARBINARY,
			RandomTestUtil.randomInt(), "bytea");
	}

	@Test
	public void testValidateTables() {
		_testValidateTables(
			Arrays.asList("userId"), Arrays.asList("userId", "screenName"));
	}

	@Test
	@TestInfo("LPD-110096")
	public void testValidateTablesFailsWhenColumnNamesDiffer() {
		try {
			_testValidateTables(
				Arrays.asList("emailAddress", "userId"),
				Arrays.asList("screenName", "userId"));

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof IllegalArgumentException);

			Assert.assertEquals(
				"Column emailAddress of source table User_ does not exist in " +
					"target table User_ in the default schema\n",
				exception.getMessage());
		}
	}

	@Test
	public void testValidateTablesFailsWhenMissingColumn() {
		try {
			_testValidateTables(
				Arrays.asList("userId", "screenName", "email"),
				Arrays.asList("userId", "screenName"));

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof IllegalArgumentException);

			Assert.assertEquals(
				"Column email of source table User_ does not exist in target " +
					"table User_ in the default schema\n",
				exception.getMessage());
		}
	}

	private void _testGetPostgreSQLColumnType(
		int columnSize, int dataType, int decimalDigits,
		String expectedPostgreSQLColumnType) {

		Assert.assertEquals(
			expectedPostgreSQLColumnType,
			ReflectionTestUtil.invoke(
				new DBCopyTablesProcess(false, null, null),
				"_getPostgreSQLColumnType",
				new Class<?>[] {int.class, int.class, int.class}, columnSize,
				dataType, decimalDigits));
	}

	private void _testValidateTables(
		List<String> sourceColumnNames, List<String> targetColumnNamess) {

		DBCopyTablesProcess dbCopyTablesProcess = new DBCopyTablesProcess(
			false, null, null);

		Map<String, List<String>> sourceColumnNamesMap =
			ReflectionTestUtil.getFieldValue(
				dbCopyTablesProcess, "_sourceColumnNamesMap");

		Map<String, List<String>> targetColumnNamesMap =
			ReflectionTestUtil.getFieldValue(
				dbCopyTablesProcess, "_targetColumnNamesMap");

		sourceColumnNamesMap.put("User_", sourceColumnNames);
		targetColumnNamesMap.put("User_", targetColumnNamess);

		Set<String> tableNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		tableNames.add("User_");

		ReflectionTestUtil.invoke(
			dbCopyTablesProcess, "_validateTables",
			new Class<?>[] {Set.class, Set.class}, tableNames, tableNames);
	}

}