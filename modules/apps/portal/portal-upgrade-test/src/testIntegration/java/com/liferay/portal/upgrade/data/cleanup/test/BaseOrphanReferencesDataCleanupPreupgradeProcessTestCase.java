/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Luis Ortiz
 */
public abstract class BaseOrphanReferencesDataCleanupPreupgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		connection = DataAccess.getConnection();
		db = DBManagerUtil.getDB();

		dbInspector = new DBInspector(connection);
	}

	@Test
	public void testUpgrade() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId());
			LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				getLoggerClassName(), LoggerTestUtil.INFO);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.WARN)) {

			UnsafeRunnable<Exception> insertDataUnsafeRunnable =
				getInsertDataUnsafeRunnable();

			insertDataUnsafeRunnable.run();

			UpgradeProcess upgradeProcess = getUpgradeProcess();

			upgradeProcess.upgrade();

			UnsafeBiConsumer<LogCapture, LogCapture, Exception>
				logAssertionUnsafeConsumer = getLogAssertionUnsafeBiConsumer();

			logAssertionUnsafeConsumer.accept(logCapture1, logCapture2);
		}
	}

	protected String getExpectedMessage(
			long count, String sourceColumnName, String sourceTableName,
			String[] targetColumnNames, String targetTableName,
			long targetValue)
		throws Exception {

		for (int i = 0; i < targetColumnNames.length; i++) {
			targetColumnNames[i] = dbInspector.normalizeName(
				targetColumnNames[i]);
		}

		return StringBundler.concat(
			"Table ", dbInspector.normalizeName(sourceTableName), ", ", count,
			(count > 1) ? " rows " : " row ", "deleted because ",
			dbInspector.normalizeName(sourceColumnName), StringPool.SPACE,
			targetValue, " was not found in column",
			(targetColumnNames.length > 1) ? "s " : " ",
			String.join(", ", targetColumnNames), " from table ",
			dbInspector.normalizeName(targetTableName));
	}

	protected abstract UnsafeRunnable<Exception> getInsertDataUnsafeRunnable();

	protected abstract UnsafeBiConsumer<LogCapture, LogCapture, Exception>
		getLogAssertionUnsafeBiConsumer();

	protected abstract String getLoggerClassName();

	protected abstract UpgradeProcess getUpgradeProcess();

	protected static Connection connection;
	protected static DB db;
	protected static DBInspector dbInspector;

}