/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer;

import com.liferay.portal.kernel.test.ReflectionTestUtil;

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
	public void testValidateTables() {
		_testValidateTables(
			Arrays.asList("userId"), Arrays.asList("userId", "screenName"));
	}

	@Test
	public void testValidateTablesFailsWhenMissingColumn() {
		try {
			_testValidateTables(
				Arrays.asList("userId", "screenName", "email"),
				Arrays.asList("userId", "screenName"));
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof IllegalArgumentException);

			Assert.assertEquals(
				"Column email of source table User_ does not exist in target " +
					"table User_ in the default schema\n",
				exception.getMessage());
		}
	}

	private void _testValidateTables(
		List<String> sourceColumnNames, List<String> targetColumnNamess) {

		DBCopyTablesProcess dbCopyTablesProcess = new DBCopyTablesProcess(
			null, null);

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