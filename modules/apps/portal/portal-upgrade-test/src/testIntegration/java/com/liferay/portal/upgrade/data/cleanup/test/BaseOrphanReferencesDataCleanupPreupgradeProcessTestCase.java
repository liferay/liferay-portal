/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
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
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OrphanReferencesDataCleanupUtil.class.getName(),
				LoggerTestUtil.INFO)) {

			getInsertDataRunnable().run();

			getUpgradeProcess().upgrade();

			getLogAssertionConsumer().accept(logCapture);
		}
	}

	protected String getExpectedMessage(
			long count, String sourceTableName, String targetColumn,
			String targetTable, long targetValue)
		throws Exception {

		return StringBundler.concat(
			count, " orphan entries from table ",
			dbInspector.normalizeName(sourceTableName),
			" have been deleted because value ", targetValue,
			" was not found in the origin table ",
			dbInspector.normalizeName(targetTable), " column ",
			dbInspector.normalizeName(targetColumn));
	}

	protected abstract UnsafeRunnable<Exception> getInsertDataRunnable();

	protected abstract UnsafeConsumer<LogCapture, Exception>
		getLogAssertionConsumer();

	protected abstract UpgradeProcess getUpgradeProcess();

	protected static Connection connection;
	protected static DB db;
	protected static DBInspector dbInspector;

}