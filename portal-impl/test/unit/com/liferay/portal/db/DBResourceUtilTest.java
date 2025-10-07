/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.Bundle;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBResourceUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetModuleIndexesSQLWithUnixLineSeparator()
		throws Exception {

		_testGetModuleIndexesSQL(StringPool.NEW_LINE);
	}

	@Test
	public void testGetModuleIndexesSQLWithWindowsLineSeparator()
		throws Exception {

		_testGetModuleIndexesSQL(StringPool.RETURN_NEW_LINE);
	}

	@Test
	public void testGetPortalTablesPrimaryKeyColumnNames() throws Exception {
		Map<String, String[]> portalTablesPrimaryKeyColumnNames =
			DBResourceUtil.getPortalTablesPrimaryKeyColumnNames();

		Assert.assertArrayEquals(
			new String[] {"companyId"},
			portalTablesPrimaryKeyColumnNames.get("Company"));
		Assert.assertArrayEquals(
			new String[] {"virtualHostId", "ctCollectionId"},
			portalTablesPrimaryKeyColumnNames.get("VirtualHost"));
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

		URL url = Mockito.mock(URL.class);

		Mockito.when(
			url.openStream()
		).thenReturn(
			_getSQLFileInputStream(lineSeparator)
		);

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getResource(Mockito.anyString())
		).thenReturn(
			url
		);

		String moduleIndexesSQL = DBResourceUtil.getModuleIndexesSQL(bundle);

		Assert.assertTrue(
			!moduleIndexesSQL.contains(StringPool.RETURN_NEW_LINE));
	}

}