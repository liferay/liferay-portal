/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.init.DBInitUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Shuyang Zhou
 */
public class DBInitUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheckSQLServer() throws Exception {
		Connection connection = Mockito.mock(Connection.class);

		DataSource dataSource = Mockito.mock(DataSource.class);

		Mockito.when(
			dataSource.getConnection()
		).thenReturn(
			connection
		);

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.when(
			connection.prepareStatement(Mockito.anyString())
		).thenReturn(
			preparedStatement
		);

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			preparedStatement.executeQuery()
		).thenReturn(
			resultSet
		);

		Mockito.when(
			resultSet.next()
		).thenReturn(
			true
		);

		Mockito.when(
			resultSet.getBoolean("is_read_committed_snapshot_on")
		).thenReturn(
			false
		);

		Mockito.when(
			resultSet.getString("name")
		).thenReturn(
			"lportal"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DBInitUtil.class.getName(), LoggerTestUtil.WARN)) {

			ReflectionTestUtil.invoke(
				DBInitUtil.class, "_checkSQLServer",
				new Class<?>[] {DataSource.class}, dataSource);

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"SQL Server may have deadlocks because ",
						"\"read_committed_snapshot\" is disabled for database ",
						"\"lportal\". To enable, execute: alter database ",
						"lportal set read_committed_snapshot on")));
		}
	}

}