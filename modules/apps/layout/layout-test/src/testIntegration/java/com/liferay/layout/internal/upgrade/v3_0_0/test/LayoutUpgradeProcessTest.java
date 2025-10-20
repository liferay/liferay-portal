/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v3_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.sql.Connection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class LayoutUpgradeProcessTest {

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

		_db.alterTableAddColumn(
			_connection, "Layout", "styleBookEntryId", "LONG");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		try {
			_db.alterTableDropColumn(_connection, "Layout", "styleBookEntryId");
		}
		finally {
			DataAccess.cleanUp(_connection);
		}
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-64053")
	public void testUpgrade() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(Validator.isNull(layout1.getStyleBookEntryERC()));

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				false, StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(
			Validator.isNotNull(styleBookEntry.getExternalReferenceCode()));

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(Validator.isNull(layout2.getStyleBookEntryERC()));

		_db.runSQL(
			StringBundler.concat(
				"update Layout set styleBookEntryId = ",
				styleBookEntry.getStyleBookEntryId(), " where plid = ",
				layout1.getPlid()));

		_runUpgrade();

		layout1 = _layoutLocalService.getLayout(layout1.getPlid());

		Assert.assertEquals(
			styleBookEntry.getExternalReferenceCode(),
			layout1.getStyleBookEntryERC());

		layout2 = _layoutLocalService.getLayout(layout2.getPlid());

		Assert.assertTrue(Validator.isNull(layout2.getStyleBookEntryERC()));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(3, 0, 0));

		UpgradeProcess upgradeProcess = upgradeProcesses[0];

		upgradeProcess.upgrade();

		_multiVMPool.clear();
	}

	private static Connection _connection;
	private static DB _db;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.internal.upgrade.registry.LayoutServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}