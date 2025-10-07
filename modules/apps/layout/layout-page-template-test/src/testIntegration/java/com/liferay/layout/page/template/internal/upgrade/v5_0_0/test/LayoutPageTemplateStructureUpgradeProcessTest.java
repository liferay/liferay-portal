/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v5_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateStructureUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_db = DBManagerUtil.getDB();

		_db.alterTableAddColumn(
			_connection, "LayoutPageTemplateStructure", "classNameId", "LONG");
		_db.alterTableAddColumn(
			_connection, "LayoutPageTemplateStructure", "classPK", "LONG");

		_indexMetadatas = _db.dropIndexes(
			_connection, "LayoutPageTemplateStructure", "plid");

		_addIndex(
			"LayoutPageTemplateStructure",
			new String[] {
				"groupId", "classNameId", "classPK", "ctCollectionId"
			});
	}

	@After
	public void tearDown() throws Exception {
		_db.dropIndexes(_connection, "LayoutPageTemplateStructure", "classPK");

		_db.alterTableDropColumn(
			_connection, "LayoutPageTemplateStructure", "classNameId");

		_db.alterTableDropColumn(
			_connection, "LayoutPageTemplateStructure", "classPK");

		if (ListUtil.isNotEmpty(_indexMetadatas)) {
			_db.addIndexes(_connection, _indexMetadatas);
		}

		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testUpgrade() throws Exception {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				TestPropsValues.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		long layoutClassNameId = _classNameLocalService.getClassNameId(
			Layout.class.getName());

		_db.runSQL(
			StringBundler.concat(
				"update LayoutPageTemplateStructure set classNameId = ",
				layoutClassNameId, ", classPK = plid"));

		long layoutPageTemplateEntryClassNameId =
			_classNameLocalService.getClassNameId(
				LayoutPageTemplateEntry.class.getName());

		_insertLayoutPageTemplateStructure(
			layoutPageTemplateEntryClassNameId,
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId());

		_db.runSQL(
			"delete from LayoutPageTemplateStructure where plid = " +
				layoutPageTemplateEntry2.getPlid());

		_insertLayoutPageTemplateStructure(
			layoutPageTemplateEntryClassNameId,
			layoutPageTemplateEntry2.getLayoutPageTemplateEntryId());

		_runUpgrade();

		Assert.assertFalse(
			_hasLayoutPageTemplateStructure(
				layoutPageTemplateEntryClassNameId,
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId()));
		Assert.assertFalse(
			_hasLayoutPageTemplateStructure(
				layoutPageTemplateEntryClassNameId,
				layoutPageTemplateEntry2.getLayoutPageTemplateEntryId()));
		Assert.assertTrue(
			_hasLayoutPageTemplateStructure(
				layoutClassNameId, layoutPageTemplateEntry1.getPlid()));
		Assert.assertTrue(
			_hasLayoutPageTemplateStructure(
				layoutClassNameId, layoutPageTemplateEntry2.getPlid()));
	}

	private void _addIndex(String tableName, String[] columnNames)
		throws Exception {

		List<IndexMetadata> indexMetadatas = Arrays.asList(
			new IndexMetadata(
				"IX_" + RandomTestUtil.randomString(), tableName, true,
				columnNames));

		_db.addIndexes(_connection, indexMetadatas);
	}

	private boolean _hasLayoutPageTemplateStructure(
			long classNameId, long classPK)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select 1 from LayoutPageTemplateStructure where " +
					"ctCollectionId = ? and classNameId = ? and classPK = ?")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, classNameId);
			preparedStatement.setLong(3, classPK);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private void _insertLayoutPageTemplateStructure(
			long classNameId, long classPK)
		throws Exception {

		_db.runSQL(
			StringBundler.concat(
				"insert into LayoutPageTemplateStructure (ctCollectionId, ",
				"uuid_, layoutPageTemplateStructureId, groupId, companyId, ",
				"userId, userName, createDate, modifiedDate, classNameId, ",
				"classPK) values (0, '", RandomTestUtil.randomString(), "', ",
				_counterLocalService.increment(), ",",
				TestPropsValues.getGroupId(), ", ",
				TestPropsValues.getCompanyId(), ", ",
				TestPropsValues.getUserId(), ", '",
				TestPropsValues.getUser(
				).getFullName(),
				"', '", LocalDate.now(), "', '", LocalDate.now(), "', ",
				classNameId, ", ", classPK, ")"));
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.page.template.internal.upgrade.v5_0_0." +
			"LayoutPageTemplateStructureUpgradeProcess";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Connection _connection;

	@Inject
	private CounterLocalService _counterLocalService;

	private DB _db;
	private List<IndexMetadata> _indexMetadatas;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}