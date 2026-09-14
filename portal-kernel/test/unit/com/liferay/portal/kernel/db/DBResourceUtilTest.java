/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.Bundle;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBResourceUtilTest {

	@Test
	public void testGetModuleColumnDefinitionsMapWithNullTablesSQL()
		throws Exception {

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			null
		);

		Map<String, List<String>> moduleColumnDefinitionsMap =
			DBResourceUtil.getModuleColumnDefinitionsMap(bundle);

		Assert.assertTrue(moduleColumnDefinitionsMap.isEmpty());
	}

	@Test
	public void testGetModuleColumnDefinitionsMapWithTablesSQL()
		throws Exception {

		Bundle bundle = Mockito.mock(Bundle.class);

		URL url = Mockito.mock(URL.class);

		String sql = StringBundler.concat(
			"create table TestTable1 (testTableId LONG not null primary key);",
			"create table TestTable2 (column1 LONG default 0 not null, ",
			"column2 LONG not null, column3 NUMERIC(13,4) null, primary key ",
			"(column1, column2));");

		Mockito.when(
			url.openStream()
		).thenReturn(
			new ByteArrayInputStream(sql.getBytes())
		);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			url
		);

		Map<String, List<String>> moduleColumnDefinitionsMap =
			DBResourceUtil.getModuleColumnDefinitionsMap(bundle);

		Assert.assertEquals(
			Arrays.asList("testTableId LONG not null"),
			moduleColumnDefinitionsMap.get("TestTable1"));
		Assert.assertEquals(
			Arrays.asList(
				"column1 LONG default 0 not null", "column2 LONG not null",
				"column3 NUMERIC(13,4) null"),
			moduleColumnDefinitionsMap.get("TestTable2"));
	}

	@Test
	public void testGetModuleColumnDefinitionsMapWithUnparsableTablesSQL()
		throws Exception {

		Bundle bundle = Mockito.mock(Bundle.class);

		URL url = Mockito.mock(URL.class);

		String sql = StringBundler.concat(
			"create table TestTable1 (]]></tables-sql><indexes-sql><![CDATA[",
			"create index IX_TEST on TestTable1 (column1);]]>",
			"create table TestTable2 (column1 LONG not null primary key);");

		Mockito.when(
			url.openStream()
		).thenReturn(
			new ByteArrayInputStream(sql.getBytes())
		);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			url
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DBResourceUtil.class.getName(), LoggerTestUtil.WARN)) {

			Map<String, List<String>> moduleColumnDefinitionsMap =
				DBResourceUtil.getModuleColumnDefinitionsMap(bundle);

			Assert.assertEquals(
				Arrays.asList("column1 LONG not null"),
				moduleColumnDefinitionsMap.get("TestTable2"));
			Assert.assertNull(moduleColumnDefinitionsMap.get("TestTable1"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to parse the column definitions of [TestTable1]",
				logEntry.getMessage());
		}
	}

	@Test
	public void testGetModuleIndexesSQL() throws Exception {
		_testGetModuleIndexesSQL(StringPool.NEW_LINE);
		_testGetModuleIndexesSQL(StringPool.RETURN_NEW_LINE);
	}

	@Test
	public void testGetModuleTablesIndexMetadatas() throws Exception {
		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			null
		);

		Map<String, List<IndexMetadata>> moduleTablesIndexMetadatas =
			DBResourceUtil.getModuleTablesIndexMetadatas(bundle);

		Assert.assertTrue(moduleTablesIndexMetadatas.isEmpty());

		URL url = Mockito.mock(URL.class);

		Mockito.when(
			url.openStream()
		).thenReturn(
			new ByteArrayInputStream(_INDEXES_SQL.getBytes())
		);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			url
		);

		_assertTablesIndexMetadatas(
			DBResourceUtil.getModuleTablesIndexMetadatas(bundle));
	}

	@Test
	public void testGetModuleTablesPrimaryKeyColumnNamesWithNullTablesSQL()
		throws Exception {

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			null
		);

		Map<String, String[]> moduleTablesPrimaryKeyColumnNames =
			DBResourceUtil.getModuleTablesPrimaryKeyColumnNames(bundle);

		Assert.assertTrue(moduleTablesPrimaryKeyColumnNames.isEmpty());
	}

	@Test
	public void testGetModuleTablesPrimaryKeyColumnNamesWithTablesSQL()
		throws Exception {

		Bundle bundle = Mockito.mock(Bundle.class);

		URL url = Mockito.mock(URL.class);

		String sql = StringBundler.concat(
			"create table TestTable1 (testTableId LONG not null primary key);",
			"create table TestTable2 (column1 LONG default 0 not null, ",
			"column2 LONG not null, primary key (column1, column2));");

		Mockito.when(
			url.openStream()
		).thenReturn(
			new ByteArrayInputStream(sql.getBytes())
		);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			url
		);

		Map<String, String[]> moduleTablesPrimaryKeyColumnNames =
			DBResourceUtil.getModuleTablesPrimaryKeyColumnNames(bundle);

		Assert.assertArrayEquals(
			new String[] {"testTableId"},
			moduleTablesPrimaryKeyColumnNames.get("TestTable1"));
		Assert.assertArrayEquals(
			new String[] {"column1", "column2"},
			moduleTablesPrimaryKeyColumnNames.get("TestTable2"));
	}

	@Test
	public void testGetPortalColumnDefinitionsMap() throws Exception {
		try (MockedStatic<StringUtil> stringUtilMockedStatic =
				Mockito.mockStatic(
					StringUtil.class, Mockito.CALLS_REAL_METHODS)) {

			stringUtilMockedStatic.when(
				() -> StringUtil.read(
					Mockito.nullable(Class.class), Mockito.anyString())
			).thenReturn(
				StringBundler.concat(
					"create table Company (\n",
					"companyId LONG not null primary key,\n",
					"mvccVersion LONG default 0 not null\n);\n\n",
					"create table VirtualHost (\n",
					"ctCollectionId LONG default 0 not null,\n",
					"virtualHostId LONG not null,\n",
					"primary key (virtualHostId, ctCollectionId)\n);")
			);

			Map<String, List<String>> portalColumnDefinitionsMap =
				DBResourceUtil.getPortalColumnDefinitionsMap();

			Assert.assertEquals(
				Arrays.asList(
					"companyId LONG not null",
					"mvccVersion LONG default 0 not null"),
				portalColumnDefinitionsMap.get("Company"));
			Assert.assertEquals(
				Arrays.asList(
					"ctCollectionId LONG default 0 not null",
					"virtualHostId LONG not null"),
				portalColumnDefinitionsMap.get("VirtualHost"));
		}
	}

	@Test
	public void testGetPortalTablesIndexMetadatas() throws Exception {
		try (MockedStatic<StringUtil> stringUtilMockedStatic =
				Mockito.mockStatic(
					StringUtil.class, Mockito.CALLS_REAL_METHODS)) {

			stringUtilMockedStatic.when(
				() -> StringUtil.read(
					Mockito.nullable(Class.class), Mockito.anyString())
			).thenReturn(
				_INDEXES_SQL
			);

			_assertTablesIndexMetadatas(
				DBResourceUtil.getPortalTablesIndexMetadatas());
		}
	}

	@Test
	public void testGetPortalTablesPrimaryKeyColumnNames() throws Exception {
		try (MockedStatic<StringUtil> stringUtilMockedStatic =
				Mockito.mockStatic(
					StringUtil.class, Mockito.CALLS_REAL_METHODS)) {

			stringUtilMockedStatic.when(
				() -> StringUtil.read(
					Mockito.nullable(Class.class), Mockito.anyString())
			).thenReturn(
				StringBundler.concat(
					"create table Company (companyId LONG not null primary key",
					"); create table VirtualHost (ctCollectionId LONG default ",
					"0 not null, virtualHostId LONG not null, primary key ",
					"(virtualHostId, ctCollectionId));")
			);

			Map<String, String[]> portalTablesPrimaryKeyColumnNames =
				DBResourceUtil.getPortalTablesPrimaryKeyColumnNames();

			Assert.assertArrayEquals(
				new String[] {"companyId"},
				portalTablesPrimaryKeyColumnNames.get("Company"));
			Assert.assertArrayEquals(
				new String[] {"virtualHostId", "ctCollectionId"},
				portalTablesPrimaryKeyColumnNames.get("VirtualHost"));
		}
	}

	@Test
	public void testParseCreateTableSQLWhenSQLIsNull() {
		Set<String> tableNames = DBResourceUtil.parseCreateTableSQL(null);

		Assert.assertTrue(tableNames.isEmpty());
	}

	private void _assertIndexMetadata(
		String[] expectedColumnNames, String expectedIndexName,
		boolean expectedUnique, IndexMetadata indexMetadata) {

		Assert.assertArrayEquals(
			expectedColumnNames, indexMetadata.getColumnNames());
		Assert.assertEquals(expectedIndexName, indexMetadata.getIndexName());
		Assert.assertEquals(expectedUnique, indexMetadata.isUnique());
	}

	private void _assertTablesIndexMetadatas(
		Map<String, List<IndexMetadata>> tablesIndexMetadatas) {

		List<IndexMetadata> indexMetadatas = tablesIndexMetadatas.get(
			"TestTable1");

		Assert.assertEquals(
			indexMetadatas.toString(), 2, indexMetadatas.size());

		_assertIndexMetadata(
			new String[] {"column1"}, "IX_TEST1", false, indexMetadatas.get(0));
		_assertIndexMetadata(
			new String[] {"column2", "column3"}, "IX_TEST2", true,
			indexMetadatas.get(1));

		indexMetadatas = tablesIndexMetadatas.get("TestTable2");

		Assert.assertEquals(
			indexMetadatas.toString(), 1, indexMetadatas.size());

		_assertIndexMetadata(
			new String[] {"column1"}, "IX_TEST3", false, indexMetadatas.get(0));
	}

	private InputStream _getSQLFileInputStream(String lineSeparator) {
		String sqlFile = StringBundler.concat(
			"create index IX_TEST1 on Table1 (field1);", lineSeparator,
			"create index IX_TEST2 on Table1 (field2);", lineSeparator,
			lineSeparator, "create index IX_TEST3 on Table2 (field);",
			lineSeparator);

		return new ByteArrayInputStream(sqlFile.getBytes());
	}

	private void _testGetModuleIndexesSQL(String lineSeparator)
		throws Exception {

		Bundle bundle = Mockito.mock(Bundle.class);

		URL url = Mockito.mock(URL.class);

		Mockito.when(
			url.openStream()
		).thenReturn(
			_getSQLFileInputStream(lineSeparator)
		);

		Mockito.when(
			bundle.getResource(ArgumentMatchers.anyString())
		).thenReturn(
			url
		);

		String moduleIndexesSQL = DBResourceUtil.getModuleIndexesSQL(bundle);

		Assert.assertTrue(
			!moduleIndexesSQL.contains(StringPool.RETURN_NEW_LINE));
	}

	private static final String _INDEXES_SQL = StringBundler.concat(
		"create index IX_TEST1 on TestTable1 (column1);\n",
		"create unique index IX_TEST2 on TestTable1 (column2, ",
		"column3[$COLUMN_LENGTH:75$]);\n\n",
		"create index IX_TEST3 on TestTable2 (column1);");

}