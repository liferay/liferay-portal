/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v4_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class FragmentEntryStatusUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-113768")
	public void testUpgrade() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.addFragmentEntryByStatus(
				fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_APPROVED);

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"update FragmentEntry set status = ",
				WorkflowConstants.STATUS_DRAFT, " where fragmentEntryId = ",
				fragmentEntry.getFragmentEntryId()));

		FragmentEntry draftFragmentEntry = _fragmentEntryLocalService.getDraft(
			fragmentEntry);

		db.runSQL(
			StringBundler.concat(
				"update FragmentEntry set status = ",
				WorkflowConstants.STATUS_APPROVED, " where fragmentEntryId = ",
				draftFragmentEntry.getFragmentEntryId()));

		for (UpgradeProcess upgradeProcess :
				UpgradeTestUtil.getUpgradeSteps(
					_upgradeStepRegistrator, new Version(4, 1, 1))) {

			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();

		fragmentEntry = _fragmentEntryLocalService.getFragmentEntry(
			fragmentEntry.getFragmentEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fragmentEntry.getStatus());

		draftFragmentEntry = _fragmentEntryLocalService.getFragmentEntry(
			draftFragmentEntry.getFragmentEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, draftFragmentEntry.getStatus());
	}

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.fragment.internal.upgrade.registry.FragmentServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}