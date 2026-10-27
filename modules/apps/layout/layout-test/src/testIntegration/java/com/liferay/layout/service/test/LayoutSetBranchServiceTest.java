/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetBranchConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@FeatureFlag("LPD-105778")
@RunWith(Arquillian.class)
public class LayoutSetBranchServiceTest {

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
	public void testAddLayoutSetBranch() throws Exception {
		_testAddLayoutSetBranchWhenFeatureFlagDisabled();
		_testAddLayoutSetBranchWithCopyLayoutSetBranchFromAnotherGroup();
	}

	@Test
	public void testMergeLayoutSetBranch() throws Exception {
		LayoutSetBranch layoutSetBranch = _addLayoutSetBranch(_group);

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		LayoutSetBranch mergeLayoutSetBranch = _addLayoutSetBranch(group);

		_assertMustHaveViewPermission(
			mergeLayoutSetBranch,
			() -> _layoutSetBranchService.mergeLayoutSetBranch(
				layoutSetBranch.getLayoutSetBranchId(),
				mergeLayoutSetBranch.getLayoutSetBranchId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId())));
	}

	private LayoutSetBranch _addLayoutSetBranch(Group group) throws Exception {
		return _layoutSetBranchLocalService.addLayoutSetBranch(
			TestPropsValues.getUserId(), group.getGroupId(), false,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			LayoutSetBranchConstants.ALL_BRANCHES,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertMustHaveViewPermission(
			LayoutSetBranch layoutSetBranch,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", TestPropsValues.getUserId(),
				" must have VIEW permission for ",
				LayoutSetBranch.class.getName(), " ",
				layoutSetBranch.getLayoutSetBranchId()),
			unsafeRunnable);
	}

	private void _testAddLayoutSetBranchWhenFeatureFlagDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-105778")) {

			AssertUtils.assertFailure(
				UnsupportedOperationException.class,
				"Feature flag LPD-105778 is disabled for company " +
					TestPropsValues.getCompanyId(),
				() -> _layoutSetBranchService.addLayoutSetBranch(
					_group.getGroupId(), false, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), false,
					LayoutSetBranchConstants.ALL_BRANCHES,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId())));
		}
	}

	private void _testAddLayoutSetBranchWithCopyLayoutSetBranchFromAnotherGroup()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		LayoutSetBranch layoutSetBranch = _addLayoutSetBranch(group);

		_assertMustHaveViewPermission(
			layoutSetBranch,
			() -> _layoutSetBranchService.addLayoutSetBranch(
				_group.getGroupId(), false, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false,
				layoutSetBranch.getLayoutSetBranchId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId())));
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Inject
	private LayoutSetBranchService _layoutSetBranchService;

}