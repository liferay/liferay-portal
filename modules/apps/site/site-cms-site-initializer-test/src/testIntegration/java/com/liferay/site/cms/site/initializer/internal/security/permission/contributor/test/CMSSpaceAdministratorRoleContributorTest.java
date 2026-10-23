/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.security.permission.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.internal.constants.CMSRolesConstants;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Veronica Gonzalez
 */
@RunWith(Arquillian.class)
public class CMSSpaceAdministratorRoleContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_cmsGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);
	}

	@Test
	public void testContributeWhenRoleIsRenamed() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			depotEntry.getGroupId(), user.getUserId());

		_addUserGroupRole(
			depotEntry, DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR, user);

		Role role = _roleLocalService.fetchRoleByExternalReferenceCode(
			CMSRolesConstants.CMS_SPACE_ADMINISTRATOR_EXTERNAL_REFERENCE_CODE,
			TestPropsValues.getCompanyId());

		String name = role.getName();

		role.setName(RandomTestUtil.randomString());

		role = _roleLocalService.updateRole(role);

		try {
			Assert.assertTrue(_hasView("/dashboard", user));
		}
		finally {
			role.setName(name);

			_roleLocalService.updateRole(role);

			_userLocalService.deleteUser(user);

			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}
	}

	@Test
	public void testContributeWhenUserIsAssetLibraryAdministrator()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			depotEntry.getGroupId(), user.getUserId());

		_addUserGroupRole(
			depotEntry, DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR, user);

		Assert.assertFalse(_hasView("/dashboard", user));

		_userLocalService.deleteUser(user);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	@Test
	public void testContributeWhenUserIsSpaceAdministrator() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			depotEntry.getGroupId(), user.getUserId());

		Assert.assertFalse(_hasView("/dashboard", user));

		_addUserGroupRole(
			depotEntry, DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR, user);

		Assert.assertTrue(_hasView("/broken-links", user));
		Assert.assertTrue(_hasView("/dashboard", user));
		Assert.assertTrue(_hasView("/missing-required-data", user));

		Assert.assertFalse(_hasView("/admin", user));
		Assert.assertFalse(_hasView("/export-import", user));

		_userLocalService.deleteUser(user);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	@Test
	public void testContributeWhenUserIsSpaceAdministratorThroughUserGroup()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);
		User user = UserTestUtil.addUser();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroupGroup(
			userGroup.getUserGroupId(), depotEntry.getGroupId());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		_userGroupGroupRoleLocalService.addUserGroupGroupRoles(
			userGroup.getUserGroupId(), depotEntry.getGroupId(),
			new long[] {role.getRoleId()});

		Assert.assertFalse(_hasView("/dashboard", user));

		_userLocalService.addUserGroupUser(
			userGroup.getUserGroupId(), user.getUserId());

		Assert.assertTrue(_hasView("/dashboard", user));

		_userLocalService.deleteUser(user);

		_userGroupLocalService.deleteUserGroup(userGroup);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	@Test
	public void testContributeWhenUserIsSpaceContentReviewer()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			depotEntry.getGroupId(), user.getUserId());

		_addUserGroupRole(
			depotEntry, DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
			user);

		Assert.assertFalse(_hasView("/dashboard", user));

		_userLocalService.deleteUser(user);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, type,
			ServiceContextTestUtil.getServiceContext(_cmsGroup.getGroupId()));
	}

	private void _addUserGroupRole(
			DepotEntry depotEntry, String roleName, User user)
		throws Exception {

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), depotEntry.getGroupId(),
			new long[] {role.getRoleId()});
	}

	private boolean _hasView(String friendlyURL, User user) throws Exception {
		Layout layout = _layoutLocalService.getFriendlyURLLayout(
			_cmsGroup.getGroupId(), false, friendlyURL);
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			user);

		return LayoutPermissionUtil.contains(
			permissionChecker, layout, ActionKeys.VIEW);
	}

	private Group _cmsGroup;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}