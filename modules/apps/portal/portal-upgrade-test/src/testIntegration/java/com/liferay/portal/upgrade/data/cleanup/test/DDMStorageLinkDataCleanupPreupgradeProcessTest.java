/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.DDMStorageLinkDataCleanupPreupgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

	@Test
	public void testUpgradeKeepsDDMStructureDefaultValues() throws Exception {
		Group group = GroupTestUtil.addGroup();

		JournalArticle journalArticle = JournalTestUtil.addArticleDefaultValues(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString());

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		Assert.assertNotNull(
			_ddmFieldLocalService.getDDMFormValues(
				ddmStructure.getDDMForm(), journalArticle.getId()));

		upgrade();

		Assert.assertNotNull(
			_ddmFieldLocalService.getDDMFormValues(
				ddmStructure.getDDMForm(), journalArticle.getId()));

		_ddmTemplateLocalService.deleteTemplate(
			journalArticle.getDDMTemplate());

		_groupLocalService.deleteGroup(group);
	}

	@Test
	public void testUpgradeWithJournalArticleDDMFields() throws Exception {
		long id = RandomTestUtil.nextLong();

		runSQL(
			StringBundler.concat(
				"insert into DDMField (mvccVersion, ctCollectionId, fieldId, ",
				"storageId) values (0, 0, ", RandomTestUtil.nextLong(), ", ",
				id, ")"));
		runSQL(
			StringBundler.concat(
				"insert into DDMFieldAttribute (mvccVersion, ctCollectionId, ",
				"fieldAttributeId, storageId) values (0, 0, ",
				RandomTestUtil.nextLong(), ", ", id, ")"));
		runSQL(
			StringBundler.concat(
				"insert into JournalArticle (mvccVersion, ctCollectionId, ",
				"id_) values (0, 0, ", id, ")"));

		try (Connection connection = DataAccess.getConnection();
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OrphanReferencesDataCleanupUtil.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 0, messages.size());

			Assert.assertEquals(
				1, _getCount(connection, "storageId", "DDMField", id));
			Assert.assertEquals(
				1, _getCount(connection, "storageId", "DDMFieldAttribute", id));
		}
		finally {
			runSQL("delete from DDMField where storageId = " + id);
			runSQL("delete from DDMFieldAttribute where storageId = " + id);
			runSQL("delete from JournalArticle where id_ = " + id);
		}
	}

	private long _getCount(
			Connection connection, String columnName, String tableName,
			long value)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) as count from ", tableName, " where ",
					columnName, " = ?"))) {

			preparedStatement.setLong(1, value);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				return resultSet.getLong("count");
			}
		}
	}

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

}