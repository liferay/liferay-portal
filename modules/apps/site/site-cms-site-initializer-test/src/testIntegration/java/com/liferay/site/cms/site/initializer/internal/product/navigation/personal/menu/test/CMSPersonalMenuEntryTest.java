/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.product.navigation.personal.menu.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;
import com.liferay.site.cms.site.initializer.internal.display.context.test.BaseDisplayContextTestCase;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class CMSPersonalMenuEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMSTestUtil.getOrAddGroup(BaseDisplayContextTestCase.class);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			new ServiceContext() {
				{
					setCompanyId(TestPropsValues.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
	}

	@Test
	public void testIsShow() throws Exception {
		_testIsShowAdminUser();
		_testIsShowCMSAdminRole();
		_testIsShowDepotEntryMemberUser();
		_testIsShowDepotEntryMemberUserGroup();
		_testIsShowWithoutPermissions();
	}

	private void _testIsShowAdminUser() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
	}

	private void _testIsShowCMSAdminRole() throws Exception {
		User user = UserTestUtil.addUser();

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), user);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private void _testIsShowDepotEntryMemberUser() throws Exception {
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroup(
			user.getUserId(), _depotEntry.getGroup());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private void _testIsShowDepotEntryMemberUserGroup() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroups(
			_depotEntry.getGroupId(), new long[] {userGroup.getUserGroupId()});

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(
			user.getUserId(), userGroup.getUserGroupId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
		finally {
			_userLocalService.deleteUser(user);

			// Order matters, first the user, then the user group

			_userGroupLocalService.deleteUserGroup(userGroup);
		}
	}

	private void _testIsShowWithoutPermissions() throws Exception {
		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertFalse(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.product.navigation.personal.menu.CMSPersonalMenuEntry"
	)
	private PersonalMenuEntry _personalMenuEntry;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}