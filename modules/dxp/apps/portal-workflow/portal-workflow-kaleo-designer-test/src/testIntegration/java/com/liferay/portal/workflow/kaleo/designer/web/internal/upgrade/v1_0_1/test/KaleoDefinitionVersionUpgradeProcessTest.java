/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.upgrade.v1_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nathaly Gomes
 */
@RunWith(Arquillian.class)
public class KaleoDefinitionVersionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_db = DBManagerUtil.getDB();

		_addKaleoDefinition();
		_addKaleoDraftDefinition();
	}

	@After
	public void tearDown() throws Exception {
		_db.runSQL("DROP_TABLE_IF_EXISTS(KaleoDraftDefinition)");
	}

	@Test
	public void testUpgrade() throws PortalException {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				_kaleoDefinition.getCompanyId(), _kaleoDefinition.getName(),
				_kaleoDefinition.getVersion() + StringPool.PERIOD + 0);

		Assert.assertEquals(
			kaleoDefinitionVersion.getKaleoDefinitionId(),
			_kaleoDefinition.getKaleoDefinitionId());
		Assert.assertEquals(
			kaleoDefinitionVersion.getStatus(),
			WorkflowConstants.STATUS_APPROVED);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				_kaleoDefinition.getCompanyId(), _kaleoDefinition.getName(),
				_VERSION + StringPool.PERIOD + --_draftVersion);

		Assert.assertEquals(
			kaleoDefinitionVersion.getKaleoDefinitionId(),
			_kaleoDefinition.getKaleoDefinitionId());
		Assert.assertEquals(
			kaleoDefinitionVersion.getStatus(), WorkflowConstants.STATUS_DRAFT);
	}

	private void _addKaleoDefinition() throws Exception {
		_kaleoDefinition = _kaleoDefinitionLocalService.addKaleoDefinition(
			null, _NAME, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, StringPool.BLANK, false, 1,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _addKaleoDraftDefinition() throws Exception {
		_db.runSQL(
			StringBundler.concat(
				"create table KaleoDraftDefinition (groupId LONG,companyId ",
				"LONG,userId LONG,createDate DATE null,modifiedDate DATE null,",
				"name VARCHAR(75) null,title STRING null,content TEXT null,",
				"version INTEGER,draftVersion INTEGER)"));

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into KaleoDraftDefinition (groupId, companyId, ",
					"userId, createDate, modifiedDate, name, title, content, ",
					"version, draftVersion) values (?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?)"))) {

			preparedStatement.setLong(1, TestPropsValues.getGroupId());
			preparedStatement.setLong(2, TestPropsValues.getCompanyId());
			preparedStatement.setLong(3, TestPropsValues.getUserId());
			preparedStatement.setTimestamp(
				4, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setTimestamp(
				5, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(6, _NAME);
			preparedStatement.setString(7, RandomTestUtil.randomString());
			preparedStatement.setString(8, null);
			preparedStatement.setInt(9, _VERSION);
			preparedStatement.setInt(10, _draftVersion);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.workflow.kaleo.designer.web.internal.upgrade." +
			"v1_0_1.KaleoDefinitionVersionUpgradeProcess";

	private static final String _NAME = RandomTestUtil.randomString();

	private static final int _VERSION = RandomTestUtil.randomInt();

	private DB _db;
	private int _draftVersion = RandomTestUtil.randomInt();

	@DeleteAfterTestRun
	private KaleoDefinition _kaleoDefinition;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.portal.workflow.kaleo.designer.web.internal.upgrade.registry.KaleoDesignerWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}