/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v4_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

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
public class SegmentsExperienceUpgradeProcessTest {

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
	@TestInfo("LPD-99785")
	public void testUpgrade() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		SegmentsExperience draftSegmentsExperience =
			_getDefaultSegmentsExperience(draftLayout);

		SegmentsExperience segmentsExperience = _getDefaultSegmentsExperience(
			layout);

		String expectedDraftExternalReferenceCode =
			draftSegmentsExperience.getExternalReferenceCode();
		String expectedExternalReferenceCode =
			segmentsExperience.getExternalReferenceCode();

		_updateExternalReferenceCode(
			RandomTestUtil.randomString(),
			draftSegmentsExperience.getSegmentsExperienceId());
		_updateExternalReferenceCode(
			RandomTestUtil.randomString(),
			segmentsExperience.getSegmentsExperienceId());

		_runUpgrade();

		draftSegmentsExperience = _getDefaultSegmentsExperience(draftLayout);

		Assert.assertEquals(
			expectedDraftExternalReferenceCode,
			draftSegmentsExperience.getExternalReferenceCode());

		segmentsExperience = _getDefaultSegmentsExperience(layout);

		Assert.assertEquals(
			expectedExternalReferenceCode,
			segmentsExperience.getExternalReferenceCode());
	}

	private SegmentsExperience _getDefaultSegmentsExperience(Layout layout) {
		return _segmentsExperienceLocalService.fetchSegmentsExperience(
			layout.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
			layout.getPlid());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(4, 1, 1));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private void _updateExternalReferenceCode(
			String externalReferenceCode, long segmentsExperienceId)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"update SegmentsExperience set externalReferenceCode = ? " +
					"where segmentsExperienceId = ?")) {

			preparedStatement.setString(1, externalReferenceCode);
			preparedStatement.setLong(2, segmentsExperienceId);

			preparedStatement.executeUpdate();
		}
	}

	private Connection _connection;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.segments.internal.upgrade.registry.SegmentsServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}