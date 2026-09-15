/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.LayoutStaging;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class LayoutStagingImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@FeatureFlag("LPD-105778")
	@Test
	public void testIsBranchingLayoutSet() throws Exception {
		_enableLocalStaging();

		Assert.assertTrue(
			_layoutStaging.isBranchingLayoutSet(
				_group.getStagingGroup(), false));
	}

	@FeatureFlag(enable = false, value = "LPD-105778")
	@Test
	public void testIsBranchingLayoutSetWhenFeatureFlagDisabled()
		throws Exception {

		_enableLocalStaging();

		Assert.assertFalse(
			_layoutStaging.isBranchingLayoutSet(
				_group.getStagingGroup(), false));
	}

	private void _enableLocalStaging() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false, serviceContext);

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty("branchingPublic", "true");

		_groupLocalService.updateGroup(
			_group.getGroupId(), typeSettingsUnicodeProperties.toString());

		_group = _groupLocalService.getGroup(_group.getGroupId());

		_stagingLocalService.checkDefaultLayoutSetBranches(
			TestPropsValues.getUserId(), _group, true, false, false,
			serviceContext);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutStaging _layoutStaging;

	@Inject
	private StagingLocalService _stagingLocalService;

}