/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v6_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class LayoutUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		DataAccess.cleanUp(_connection);
	}

	@Test
	@TestInfo("LPD-99222")
	public void testUpgrade() throws Exception {
		Layout liveLayout = LayoutTestUtil.addTypePortletLayout(_group);

		GroupTestUtil.enableLocalStaging(_group);

		Group stagingGroup = _group.getStagingGroup();

		Layout stagingLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			liveLayout.getUuid(), stagingGroup.getGroupId(),
			liveLayout.isPrivateLayout());

		String liveExternalReferenceCode = String.valueOf(liveLayout.getPlid());
		String stagingExternalReferenceCode = String.valueOf(
			stagingLayout.getPlid());

		_updateLayout(liveLayout.getPlid(), liveExternalReferenceCode);
		_updateLayout(stagingLayout.getPlid(), stagingExternalReferenceCode);

		_runUpgrade();

		liveLayout = _layoutLocalService.getLayout(liveLayout.getPlid());

		Assert.assertEquals(
			stagingExternalReferenceCode,
			liveLayout.getExternalReferenceCode());

		stagingLayout = _layoutLocalService.getLayout(stagingLayout.getPlid());

		Assert.assertEquals(
			stagingExternalReferenceCode,
			stagingLayout.getExternalReferenceCode());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(6, 1, 1));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private void _updateLayout(long plid, String externalReferenceCode)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"update Layout set externalReferenceCode = ? where plid = ?")) {

			preparedStatement.setString(1, externalReferenceCode);
			preparedStatement.setLong(2, plid);

			preparedStatement.executeUpdate();
		}
	}

	private Connection _connection;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.internal.upgrade.registry.LayoutServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}