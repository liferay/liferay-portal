/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.CrossOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class CrossOrphanReferencesDataCleanupPreupgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		DB db = DBManagerUtil.getDB();

		try (Connection connection = DataAccess.getConnection();
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OrphanReferencesDataCleanupUtil.class.getName(),
				LoggerTestUtil.INFO)) {

			long companyId1 = RandomTestUtil.nextLong();
			long companyId2 = RandomTestUtil.nextLong();

			_insertEntry(connection, db, companyId1);
			_insertEntry(connection, db, companyId1);
			_insertEntry(connection, db, companyId2);

			UpgradeProcess upgradeProcess =
				new CrossOrphanReferencesDataCleanupPreupgradeProcess(
					"companyId", "Company");

			upgradeProcess.upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 4, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			DBInspector dbInspector = new DBInspector(connection);

			Assert.assertEquals(
				_getExpectedMessage(companyId1, 2, dbInspector, "Image"),
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				_getExpectedMessage(companyId2, 1, dbInspector, "Image"),
				logEntry.getMessage());

			logEntry = logEntries.get(2);

			Assert.assertEquals(
				_getExpectedMessage(companyId1, 2, dbInspector, "Portlet"),
				logEntry.getMessage());

			logEntry = logEntries.get(3);

			Assert.assertEquals(
				_getExpectedMessage(companyId2, 1, dbInspector, "Portlet"),
				logEntry.getMessage());
		}
	}

	private String _getExpectedMessage(
			long companyId, long count, DBInspector dbInspector,
			String tableName)
		throws Exception {

		return StringBundler.concat(
			count, " orphan entries from table ",
			dbInspector.normalizeName(tableName),
			" have been deleted because value ", companyId,
			" was not found in the origin table ",
			dbInspector.normalizeName("Company"), " column ",
			dbInspector.normalizeName("companyId"));
	}

	private void _insertEntry(Connection connection, DB db, long companyId)
		throws Exception {

		db.runSQL(
			connection,
			StringBundler.concat(
				"insert into Image (mvccVersion, ctCollectionId, imageId, ",
				"companyId) values (0, 0, ", RandomTestUtil.nextLong(), ", ",
				companyId, ")"));

		db.runSQL(
			connection,
			StringBundler.concat(
				"insert into Portlet (mvccVersion, id_, companyId, portletId, ",
				"active_) values (0, ", RandomTestUtil.nextLong(), ", ",
				companyId, ", '", RandomTestUtil.randomString(),
				"', [$FALSE$])"));
	}

}