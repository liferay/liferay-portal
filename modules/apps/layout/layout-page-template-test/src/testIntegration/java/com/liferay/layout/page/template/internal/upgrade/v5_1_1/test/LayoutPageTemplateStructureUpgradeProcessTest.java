/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v5_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;

import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateStructureUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();
		_db = DBManagerUtil.getDB();
		_dbInspector = new DBInspector(DataAccess.getConnection());

		_addLegacyColumns();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_dropLegacyColumns();
	}

	@Test
	@TestInfo("LPD-60053")
	public void testUpgrade() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				TestPropsValues.getGroupId());

		_db.runSQL(
			StringBundler.concat(
				"update LayoutPageTemplateStructure set classPK = plid where ",
				"plid = ", layoutPageTemplateEntry.getPlid()));

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		layout.setUserId(RandomTestUtil.randomLong());
		layout.setType(LayoutConstants.TYPE_PORTLET);
		layout.setStatus(WorkflowConstants.STATUS_DRAFT);

		layout = _layoutLocalService.updateLayout(layout);

		_runUpgrade();

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			layout.getStatusByUserId(),
			_userLocalService.getGuestUserId(layout.getCompanyId()));
	}

	private static void _addLegacyColumns() throws Exception {
		_legacyColumnsAdded = false;
		_indexMetadataList = Collections.emptyList();

		if (!_dbInspector.hasColumn(
				"LayoutPageTemplateStructure", "classNameId") &&
			!_dbInspector.hasColumn("LayoutPageTemplateStructure", "classPK")) {

			_db.alterTableAddColumn(
				_connection, "LayoutPageTemplateStructure", "classNameId",
				"LONG");

			_db.alterTableAddColumn(
				_connection, "LayoutPageTemplateStructure", "classPK", "LONG");

			_legacyColumnsAdded = true;

			_indexMetadataList = _db.dropIndexes(
				_connection, "LayoutPageTemplateStructure", "plid");
		}
	}

	private static void _dropLegacyColumns() throws Exception {
		if (!_legacyColumnsAdded) {
			return;
		}

		if (_dbInspector.hasColumn(
				"LayoutPageTemplateStructure", "classNameId") &&
			_dbInspector.hasColumn("LayoutPageTemplateStructure", "classPK")) {

			_db.alterTableDropColumn(
				_connection, "LayoutPageTemplateStructure", "classNameId");

			_db.alterTableDropColumn(
				_connection, "LayoutPageTemplateStructure", "classPK");
		}

		if (ListUtil.isNotEmpty(_indexMetadataList)) {
			_db.addIndexes(_connection, _indexMetadataList);
		}
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
		"com.liferay.layout.page.template.internal.upgrade.v5_1_1." +
			"LayoutPageTemplateStructureUpgradeProcess";

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;
	private static List<IndexMetadata> _indexMetadataList;
	private static boolean _legacyColumnsAdded;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}