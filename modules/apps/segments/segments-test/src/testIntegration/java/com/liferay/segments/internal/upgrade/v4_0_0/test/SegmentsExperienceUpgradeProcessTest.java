/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v4_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Collections;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class SegmentsExperienceUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		DB db = DBManagerUtil.getDB();

		db.alterTableAddColumn(
			_connection, "SegmentsExperience", "segmentsEntryId", "LONG");

		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@After
	public void tearDown() throws Exception {
		DataAccess.cleanUp(_connection);
	}

	@Test
	@TestInfo("LPD-73850")
	public void testUpgrade() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		SegmentsEntry segmentsEntry1 = _addSegmentsEntry(_group.getGroupId());
		SegmentsEntry segmentsEntry2 = _addSegmentsEntry(
			companyGroup.getGroupId());

		SegmentsExperience segmentsExperience0 =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				_layout.getPlid());
		SegmentsExperience segmentsExperience1 = _addSegmentsExperience(
			segmentsEntry1);
		SegmentsExperience segmentsExperience2 = _addSegmentsExperience(
			segmentsEntry2);

		_updateSegmentsEntryId(
			segmentsExperience0.getCtCollectionId(),
			SegmentsEntryConstants.ID_DEFAULT,
			segmentsExperience0.getSegmentsExperienceId());
		_updateSegmentsEntryId(
			segmentsExperience1.getCtCollectionId(),
			segmentsEntry1.getSegmentsEntryId(),
			segmentsExperience1.getSegmentsExperienceId());
		_updateSegmentsEntryId(
			segmentsExperience2.getCtCollectionId(),
			segmentsEntry2.getSegmentsEntryId(),
			segmentsExperience2.getSegmentsExperienceId());

		runUpgrade();

		segmentsExperience0 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience0.getSegmentsExperienceId());
		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());
		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		_assertSegmentsExperience(_group, null, segmentsExperience0);
		_assertSegmentsExperience(_group, segmentsEntry1, segmentsExperience1);
		_assertSegmentsExperience(
			companyGroup, segmentsEntry2, segmentsExperience2);

		Assert.assertTrue(
			_dbInspector.hasColumn("SegmentsExperience", "segmentsEntryERC"));
		Assert.assertFalse(
			_dbInspector.hasColumn("SegmentsExperience", "segmentsEntryId"));
		Assert.assertTrue(
			_dbInspector.hasColumn(
				"SegmentsExperience", "segmentsEntryScopeERC"));
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		SegmentsEntry segmentsEntry = _addSegmentsEntry(_group.getGroupId());

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			segmentsEntry);

		_updateSegmentsEntryId(
			segmentsExperience.getCtCollectionId(),
			segmentsEntry.getSegmentsEntryId(),
			segmentsExperience.getSegmentsExperienceId());

		return segmentsExperience;
	}

	@Override
	protected void deleteCTModel(long primaryKey) throws Exception {
		_segmentsExperienceLocalService.deleteSegmentsExperience(primaryKey);
	}

	@Override
	protected CTService<?> getCTService() {
		return _segmentsExperienceLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(4, 0, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			Class<?> upgradeProcessClass = upgradeProcess.getClass();

			Method getPostUpgradeStepsMethod =
				upgradeProcessClass.getDeclaredMethod("getPostUpgradeSteps");

			getPostUpgradeStepsMethod.setAccessible(true);

			UpgradeStep[] postUpgradeSteps =
				(UpgradeStep[])getPostUpgradeStepsMethod.invoke(upgradeProcess);

			upgradeProcess.upgrade();

			for (UpgradeStep postUpgradeStep : postUpgradeSteps) {
				postUpgradeStep.upgrade();
			}
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		SegmentsExperience segmentsExperience = (SegmentsExperience)ctModel;

		segmentsExperience.setName(RandomTestUtil.randomString());

		return _segmentsExperienceLocalService.updateSegmentsExperience(
			segmentsExperience);
	}

	private SegmentsEntry _addSegmentsEntry(long groupId) throws Exception {
		Locale defaultLocale = LocaleUtil.getSiteDefault();

		return _segmentsEntryLocalService.addSegmentsEntry(
			null, RandomTestUtil.randomString(),
			Collections.singletonMap(
				defaultLocale, RandomTestUtil.randomString()),
			Collections.singletonMap(
				defaultLocale, RandomTestUtil.randomString()),
			true, null, SegmentsEntryConstants.SOURCE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private SegmentsExperience _addSegmentsExperience(
			SegmentsEntry segmentsEntry)
		throws Exception {

		return _segmentsExperienceLocalService.addSegmentsExperience(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(),
			ScopeUtil.getItemScopeExternalReferenceCode(
				segmentsEntry.getGroupId(), _group.getGroupId()),
			_layout.getPlid(), RandomTestUtil.randomLocaleStringMap(), true,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertSegmentsExperience(
		Group group, SegmentsEntry segmentsEntry,
		SegmentsExperience segmentsExperience) {

		if (segmentsEntry == null) {
			Assert.assertTrue(segmentsExperience.hasDefaultSegmentsEntry());
			Assert.assertTrue(segmentsExperience.isDefault());
			Assert.assertEquals(
				SegmentsEntryConstants.ID_DEFAULT,
				segmentsExperience.getSegmentsEntryId());
			Assert.assertTrue(
				Validator.isNull(segmentsExperience.getSegmentsEntryERC()));
			Assert.assertTrue(
				Validator.isNull(
					segmentsExperience.getSegmentsEntryScopeERC()));
		}
		else {
			Assert.assertFalse(segmentsExperience.hasDefaultSegmentsEntry());
			Assert.assertFalse(segmentsExperience.isDefault());
			Assert.assertEquals(
				segmentsEntry.getExternalReferenceCode(),
				segmentsExperience.getSegmentsEntryERC());
			Assert.assertEquals(
				segmentsEntry.getSegmentsEntryId(),
				segmentsExperience.getSegmentsEntryId());

			if (segmentsExperience.getGroupId() == segmentsEntry.getGroupId()) {
				Assert.assertTrue(
					Validator.isNull(
						segmentsExperience.getSegmentsEntryScopeERC()));
			}
			else {
				Assert.assertEquals(
					group.getExternalReferenceCode(),
					segmentsExperience.getSegmentsEntryScopeERC());
			}
		}
	}

	private void _updateSegmentsEntryId(
			long ctCollectionId, long segmentsEntryId,
			long segmentsExperienceId)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				StringBundler.concat(
					"update SegmentsExperience set segmentsEntryERC = null, ",
					"segmentsEntryScopeERC = null, segmentsEntryId = ? where ",
					"ctCollectionId = ? and segmentsExperienceId = ?"))) {

			preparedStatement.setLong(1, segmentsEntryId);
			preparedStatement.setLong(2, ctCollectionId);
			preparedStatement.setLong(3, segmentsExperienceId);

			preparedStatement.executeUpdate();
		}
	}

	private Connection _connection;
	private DBInspector _dbInspector;

	@Inject
	private EntityCache _entityCache;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.segments.internal.upgrade.registry.SegmentsServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}