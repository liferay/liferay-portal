/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class UserServiceWhenUpdatingUserStatusTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testActivateUser() throws Exception {
		_testUpdateUserStatusWithValidPermission(
			ActionKeys.ACTIVATE, WorkflowConstants.STATUS_INACTIVE,
			WorkflowConstants.STATUS_APPROVED);

		_testUpdateUserStatusWithInvalidPermission(
			ActionKeys.DEACTIVATE, WorkflowConstants.STATUS_INACTIVE,
			WorkflowConstants.STATUS_APPROVED, "ACTIVATE, DELETE");

		_testUpdateUserStatusWithValidPermission(
			ActionKeys.DELETE, WorkflowConstants.STATUS_INACTIVE,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testDeactivateUser() throws Exception {
		_testUpdateUserStatusWithInvalidPermission(
			ActionKeys.ACTIVATE, WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.STATUS_INACTIVE, "DEACTIVATE, DELETE");

		_testUpdateUserStatusWithValidPermission(
			ActionKeys.DEACTIVATE, WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.STATUS_INACTIVE);

		_testUpdateUserStatusWithValidPermission(
			ActionKeys.DELETE, WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.STATUS_INACTIVE);
	}

	@Test
	public void testDeleteUser() throws Exception {
		_testDeleteUserWithInvalidPermission(ActionKeys.ACTIVATE, "DELETE");

		_testDeleteUserWithInvalidPermission(ActionKeys.DEACTIVATE, "DELETE");

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			_role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_user1.getCompanyId()), ActionKeys.DELETE);

		_userLocalService.addRoleUser(_role.getRoleId(), _user1);

		PermissionChecker oldPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user1));

			_userService.deleteUser(_user2.getUserId());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(oldPermissionChecker);
		}

		Assert.assertNull(_userLocalService.fetchUser(_user2.getUserId()));
	}

	private void _testDeleteUserWithInvalidPermission(
			String actionId, String expectedPermissions)
		throws Exception {

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			_role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_user1.getCompanyId()), actionId);

		_userLocalService.addRoleUser(_role.getRoleId(), _user1);

		PermissionChecker oldPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user1));

			_userService.deleteUser(_user2.getUserId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertEquals(
				String.format(
					"User %s must have %s permission for " +
						"com.liferay.portal.kernel.model.User %s",
					_user1.getUserId(), expectedPermissions,
					_user2.getUserId()),
				principalException.getMessage());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(oldPermissionChecker);
		}

		Assert.assertNotNull(_userLocalService.fetchUser(_user2.getUserId()));
	}

	private void _testUpdateUserStatusWithInvalidPermission(
			String actionId, int sourceStatus, int targetStatus,
			String expectedPermissions)
		throws Exception {

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			_role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_user1.getCompanyId()), actionId);

		_userLocalService.addRoleUser(_role.getRoleId(), _user1);

		_userService.updateStatus(
			_user2.getUserId(), sourceStatus, new ServiceContext());

		User user2 = _userLocalService.getUser(_user2.getUserId());

		Assert.assertEquals(sourceStatus, user2.getStatus());

		PermissionChecker oldPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user1));

			_userService.updateStatus(
				_user2.getUserId(), targetStatus, new ServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertEquals(
				String.format(
					"User %s must have %s permission for " +
						"com.liferay.portal.kernel.model.User %s",
					_user1.getUserId(), expectedPermissions,
					_user2.getUserId()),
				principalException.getMessage());

			user2 = _userLocalService.getUser(_user2.getUserId());

			Assert.assertEquals(sourceStatus, user2.getStatus());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(oldPermissionChecker);
		}
	}

	private void _testUpdateUserStatusWithValidPermission(
			String actionId, int sourceStatus, int targetStatus)
		throws Exception {

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			_role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_user1.getCompanyId()), actionId);

		_userLocalService.addRoleUser(_role.getRoleId(), _user1);

		ServiceContext serviceContext = new ServiceContext();

		_userService.updateStatus(
			_user2.getUserId(), sourceStatus, serviceContext);

		User user2 = _userLocalService.getUser(_user2.getUserId());

		Assert.assertEquals(sourceStatus, user2.getStatus());

		PermissionChecker oldPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user1));

			_userService.updateStatus(
				_user2.getUserId(), targetStatus, serviceContext);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(oldPermissionChecker);
		}

		user2 = _userLocalService.getUser(_user2.getUserId());

		Assert.assertEquals(targetStatus, user2.getStatus());
	}

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserService _userService;

}