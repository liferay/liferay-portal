/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.DDMStorageLinkDataCleanupPreupgradeProcess;

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
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DDMStorageLinkDataCleanupPreupgradeProcessTest
	extends DDMStorageLinkDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		long contentId = RandomTestUtil.nextLong();

		runSQL(
			StringBundler.concat(
				"insert into DDMContent (mvccVersion, ctCollectionId, ",
				"contentId) values (0, 0, ", contentId, ")"));
		runSQL(
			StringBundler.concat(
				"insert into DDMField (mvccVersion, ctCollectionId, fieldId, ",
				"storageId) values (0, 0, ", RandomTestUtil.nextLong(), ", ",
				contentId, ")"));
		runSQL(
			StringBundler.concat(
				"insert into DDMFieldAttribute (mvccVersion, ctCollectionId, ",
				"fieldAttributeId, storageId) values (0, 0, ",
				RandomTestUtil.nextLong(), ", ", contentId, ")"));

		try (Connection connection = DataAccess.getConnection();
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OrphanReferencesDataCleanupUtil.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 3, messages.size());

			DBInspector dbInspector = new DBInspector(connection);

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", dbInspector.normalizeName("DDMContent"),
						", 1 row deleted because ",
						dbInspector.normalizeName("contentId"),
						StringPool.SPACE, contentId,
						" was not found in column ",
						dbInspector.normalizeName("classPK"), " from table ",
						dbInspector.normalizeName("DDMStorageLink"))));
			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", dbInspector.normalizeName("DDMField"),
						", 1 row deleted because ",
						dbInspector.normalizeName("storageId"),
						StringPool.SPACE, contentId,
						" was not found in column ",
						dbInspector.normalizeName("classPK"), " from table ",
						dbInspector.normalizeName("DDMStorageLink"))));
			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ",
						dbInspector.normalizeName("DDMFieldAttribute"),
						", 1 row deleted because ",
						dbInspector.normalizeName("storageId"),
						StringPool.SPACE, contentId,
						" was not found in column ",
						dbInspector.normalizeName("classPK"), " from table ",
						dbInspector.normalizeName("DDMStorageLink"))));
		}
		finally {
			runSQL("delete from DDMContent where contentId = " + contentId);
			runSQL("delete from DDMField where storageId = " + contentId);
			runSQL(
				"delete from DDMFieldAttribute where storageId = " + contentId);
		}
	}

}