/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.persistence.RoleFinder;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael Bowerman
 */
@RunWith(Arquillian.class)
public class RoleFinderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			TransactionalTestRule.INSTANCE);

	@Test
	public void testFilterFindByGroupRoleAndTeamRole() throws Exception {
		List<String> existingRoleNames = _getExistingRoleNames();

		_roleWithViewPermission = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);
		_roleWithoutViewPermission = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		_user = UserTestUtil.addUser();

		Role userRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_roleWithViewPermission.getRoleId()),
			userRole.getRoleId(), new String[] {ActionKeys.VIEW});

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, permissionChecker)) {

			List<Role> roles = _roleFinder.filterFindByGroupRoleAndTeamRole(
				TestPropsValues.getCompanyId(), null, existingRoleNames, null,
				null, _TYPES, null, 0, _user.getGroupId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			Assert.assertEquals(roles.toString(), 1, roles.size());

			Assert.assertEquals(_roleWithViewPermission, roles.get(0));

			_roleWithViewPermission.setSubtype("space");

			_roleWithViewPermission = _roleLocalService.updateRole(
				_roleWithViewPermission);

			roles = _roleFinder.filterFindByGroupRoleAndTeamRole(
				TestPropsValues.getCompanyId(), null, existingRoleNames, null,
				null, _TYPES, "space", 0, _user.getGroupId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			Assert.assertEquals(roles.toString(), 1, roles.size());

			Assert.assertEquals(_roleWithViewPermission, roles.get(0));

			roles = _roleFinder.filterFindByGroupRoleAndTeamRole(
				TestPropsValues.getCompanyId(), null, existingRoleNames, null,
				null, _TYPES, RandomTestUtil.randomString(), 0,
				_user.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertTrue(roles.toString(), roles.isEmpty());

			roles = _roleFinder.filterFindByGroupRoleAndTeamRole(
				TestPropsValues.getCompanyId(), null, existingRoleNames, null,
				null, _TYPES, null, 0, _user.getGroupId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			Assert.assertEquals(roles.toString(), 1, roles.size());

			Assert.assertEquals(_roleWithViewPermission, roles.get(0));
		}
	}

	private List<String> _getExistingRoleNames() throws Exception {
		return TransformUtil.transform(
			_roleLocalService.getRoles(TestPropsValues.getCompanyId(), _TYPES),
			role -> role.getName());
	}

	private static final int[] _TYPES = {RoleConstants.TYPE_REGULAR};

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleFinder _roleFinder;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private Role _roleWithViewPermission;

	@DeleteAfterTestRun
	private Role _roleWithoutViewPermission;

	@DeleteAfterTestRun
	private User _user;

}