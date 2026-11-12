/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataSourceFactoryUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_tempDir = FileUtil.createTempFolder();
	}

	@After
	public void tearDown() {
		FileUtil.deltree(_tempDir);
	}

	@Test
	public void testDestroyDataSource() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.spring.hibernate.DialectUtil",
				LoggerTestUtil.OFF)) {

			DataSource dataSource1 = DataSourceFactoryUtil.initDataSource(
				"org.hsqldb.jdbc.JDBCDriver",
				"jdbc:hsqldb:" + _tempDir.getAbsolutePath() + "/lportal1;",
				"sa", StringPool.BLANK, StringPool.BLANK);

			try (Connection connection = dataSource1.getConnection()) {
				Assert.assertNotNull(connection);
			}

			DataSource dataSource2 = DataSourceFactoryUtil.initDataSource(
				"org.hsqldb.jdbc.JDBCDriver",
				"jdbc:hsqldb:" + _tempDir.getAbsolutePath() + "/lportal2;",
				"sa", StringPool.BLANK, StringPool.BLANK);

			try (Connection connection = dataSource2.getConnection()) {
				Assert.assertNotNull(connection);
			}

			DataSourceFactoryUtil.destroyDataSource(dataSource1);

			try (Connection connection = dataSource1.getConnection()) {
				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertEquals(
					"HikariDataSource " + dataSource1 + " has been closed.",
					exception.getMessage());
			}

			DataSourceFactoryUtil.destroyDataSource(dataSource2);

			try (Connection connection = dataSource2.getConnection()) {
				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertEquals(
					"HikariDataSource " + dataSource2 + " has been closed.",
					exception.getMessage());
			}
		}
	}

	private File _tempDir;

}