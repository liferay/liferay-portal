/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.AnalyticsMessageDataCleanupPreupgradeProcess;

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
public class AnalyticsMessageDataCleanupPreupgradeProcessTest
	extends AnalyticsMessageDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				PortalInstancePool.getDefaultCompanyId());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(_connection);

		if (_safeCloseable != null) {
			_safeCloseable.close();
		}
	}

	@Test
	public void testUpgradeWithContent() throws Exception {
		runSQL(
			StringBundler.concat(
				"insert into AnalyticsMessage (mvccVersion, ctCollectionId, ",
				"analyticsMessageId, companyId) values (0, 0, ",
				RandomTestUtil.nextLong(), ", ",
				CompanyThreadLocal.getCompanyId(), ")"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				AnalyticsMessageDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					"Table " + _dbInspector.normalizeName("AnalyticsMessage") +
						", truncated because data is no longer needed"));

			try (PreparedStatement preparedStatement =
					_connection.prepareStatement(
						"select * from AnalyticsMessage");
				ResultSet resultSet = preparedStatement.executeQuery()) {

				Assert.assertFalse(resultSet.next());
			}
		}
		finally {
			runSQL(
				"delete from AnalyticsMessage where companyId = '" +
					CompanyThreadLocal.getCompanyId() + "'");
		}
	}

	@Test
	public void testUpgradeWithoutContent() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				AnalyticsMessageDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertFalse(
				messages.contains(
					"Table " + _dbInspector.normalizeName("AnalyticsMessage") +
						", truncated because data is no longer needed"));
		}
	}

	private static Connection _connection;
	private static DBInspector _dbInspector;
	private static SafeCloseable _safeCloseable;

}