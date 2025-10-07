/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.client.dto.v1_0.Creator;
import com.liferay.headless.admin.user.client.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.UserGroupResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.text.DateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UserGroupResourceTest extends BaseUserGroupResourceTestCase {

	@Override
	@Test
	public void testDeleteUserGroupByExternalReferenceCodeUsers()
		throws Exception {

		UserGroup userGroup =
			testDeleteUserGroupByExternalReferenceCodeUsers_addUserGroup();

		User user = UserTestUtil.addUser();

		_userLocalService.addUserGroupUser(userGroup.getId(), user.getUserId());

		List<User> users = _userLocalService.getUserGroupUsers(
			userGroup.getId());

		Assert.assertTrue(users.contains(user));

		userGroupResource.deleteUserGroupByExternalReferenceCodeUsers(
			userGroup.getExternalReferenceCode(),
			new Long[] {user.getUserId()});

		users = _userLocalService.getUserGroupUsers(userGroup.getId());

		Assert.assertFalse(users.contains(user));
	}

	@Override
	@Test
	public void testDeleteUserGroupUsers() throws Exception {
		UserGroup userGroup = testDeleteUserGroupUsers_addUserGroup();

		User user = UserTestUtil.addUser();

		_userLocalService.addUserGroupUser(userGroup.getId(), user.getUserId());

		List<User> users = _userLocalService.getUserGroupUsers(
			userGroup.getId());

		Assert.assertTrue(users.contains(user));

		userGroupResource.deleteUserGroupUsers(
			userGroup.getId(), new Long[] {user.getUserId()});

		users = _userLocalService.getUserGroupUsers(userGroup.getId());

		Assert.assertFalse(users.contains(user));
	}

	@Override
	@Test
	public void testGetUserGroup() throws Exception {
		super.testGetUserGroup();

		_testGetUserGroupWithNestedFields();
	}

	@Override
	@Test
	public void testGetUserGroupsPage() throws Exception {
		super.testGetUserGroupsPage();

		_testGetUserGroupsPageWithFilter();
	}

	@Override
	@Test
	public void testGetUserUserGroups() throws Exception {
		Long userAccountId = testGetUserUserGroups_getUserAccountId();

		Page<UserGroup> page = userGroupResource.getUserUserGroups(
			userAccountId);

		Assert.assertEquals(0, page.getTotalCount());

		UserGroup userGroup1 = testGetUserUserGroups_addUserGroup(
			userAccountId, randomUserGroup());
		UserGroup userGroup2 = testGetUserUserGroups_addUserGroup(
			userAccountId, randomUserGroup());

		page = userGroupResource.getUserUserGroups(userAccountId);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(userGroup1, userGroup2),
			(List<UserGroup>)page.getItems());
		assertValid(page);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteUserGroupByExternalReferenceCodeUsers()
		throws Exception {

		super.testGraphQLDeleteUserGroupByExternalReferenceCodeUsers();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteUserGroupUsers() throws Exception {
		super.testGraphQLDeleteUserGroupUsers();
	}

	@Override
	@Test
	public void testPostUserGroupByExternalReferenceCodeUsers()
		throws Exception {

		UserGroup userGroup =
			testPostUserGroupByExternalReferenceCodeUsers_addUserGroup();

		Assert.assertTrue(userGroup.getUsersCount() == 0);

		User user = UserTestUtil.addUser();

		Long[] userIds = {user.getUserId()};

		userGroupResource.postUserGroupByExternalReferenceCodeUsers(
			userGroup.getExternalReferenceCode(), userIds);

		Assert.assertArrayEquals(
			_userGroupLocalService.getUserPrimaryKeys(userGroup.getId()),
			ArrayUtil.toArray(userIds));
	}

	@Override
	@Test
	public void testPostUserGroupUsers() throws Exception {
		UserGroup userGroup = _postUserGroup();

		Assert.assertEquals(0, (int)userGroup.getUsersCount());

		User user = UserTestUtil.addUser();

		Long[] userIds = {user.getUserId()};

		userGroupResource.postUserGroupUsers(userGroup.getId(), userIds);

		Assert.assertArrayEquals(
			_userGroupLocalService.getUserPrimaryKeys(userGroup.getId()),
			ArrayUtil.toArray(userIds));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "name"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"description"};
	}

	@Override
	protected UserGroup testDeleteUserGroup_addUserGroup() throws Exception {
		return _postUserGroup();
	}

	@Override
	protected UserGroup
			testDeleteUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		return _postUserGroup();
	}

	@Override
	protected UserGroup
			testDeleteUserGroupByExternalReferenceCodeUsers_addUserGroup()
		throws Exception {

		return _postUserGroup();
	}

	@Override
	protected UserGroup testDeleteUserGroupUsers_addUserGroup()
		throws Exception {

		return _postUserGroup();
	}

	@Override
	protected UserGroup testGetUserGroup_addUserGroup() throws Exception {
		return _postUserGroup();
	}

	@Override
	protected UserGroup testGetUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		return _postUserGroup();
	}

	@Override
	protected UserGroup testGetUserGroupsPage_addUserGroup(UserGroup userGroup)
		throws Exception {

		return _postUserGroup(userGroup);
	}

	@Override
	protected UserGroup testGetUserUserGroups_addUserGroup(
			Long userAccountId, UserGroup userGroup)
		throws Exception {

		userGroup = _postUserGroup(userGroup);

		userGroupResource.postUserGroupUsers(
			userGroup.getId(), new Long[] {userAccountId});

		return userGroup;
	}

	@Override
	protected Long testGetUserUserGroups_getUserAccountId() throws Exception {
		User user = UserTestUtil.addUser();

		return user.getUserId();
	}

	@Override
	protected UserGroup testGraphQLUserGroup_addUserGroup() throws Exception {
		return _postUserGroup();
	}

	@Override
	protected UserGroup testPatchUserGroup_addUserGroup() throws Exception {
		return _postUserGroup();
	}

	@Override
	protected UserGroup testPatchUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		return _postUserGroup();
	}

	@Override
	protected UserGroup testPostUserGroup_addUserGroup(UserGroup userGroup)
		throws Exception {

		return _postUserGroup(userGroup);
	}

	@Override
	protected UserGroup
			testPostUserGroupByExternalReferenceCodeUsers_addUserGroup()
		throws Exception {

		return _postUserGroup();
	}

	@Override
	protected UserGroup testPostUserGroupUsers_addUserGroup() throws Exception {
		return _postUserGroup();
	}

	@Override
	protected UserGroup testPutUserGroup_addUserGroup() throws Exception {
		return _postUserGroup();
	}

	@Override
	protected UserGroup testPutUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		return _postUserGroup();
	}

	private UserGroup _postUserGroup() throws Exception {
		return _postUserGroup(randomUserGroup());
	}

	private UserGroup _postUserGroup(UserGroup userGroup) throws Exception {
		return userGroupResource.postUserGroup(userGroup);
	}

	private void _testGetUserGroupsPageWithFilter() throws Exception {
		Page<UserGroup> page = userGroupResource.getUserGroupsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		// Sleep for 1 second to ensure that user group 1 and existing user
		// groups are created 1 second apart

		Thread.sleep(1000);

		UserGroup userGroup1 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		// Sleep for 1 second to ensure that user group 1 and user group 2 are
		// created 1 second apart

		Thread.sleep(1000);

		UserGroup userGroup2 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		Date date = userGroup1.getDateCreated();

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		page = userGroupResource.getUserGroupsPage(
			null, "dateCreated lt " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(totalCount, page.getTotalCount());

		page = userGroupResource.getUserGroupsPage(
			null, "dateCreated ge " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(2, page.getTotalCount());

		// Sleep for 1 second to ensure that user group 1 and user group 2 are
		// modified 1 second apart

		Thread.sleep(1000);

		userGroup1.setDescription(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		userGroup1 = userGroupResource.patchUserGroup(
			userGroup1.getId(), userGroup1);

		date = userGroup1.getDateModified();

		page = userGroupResource.getUserGroupsPage(
			null, "dateModified ge " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(userGroup1, (List<UserGroup>)page.getItems());

		page = userGroupResource.getUserGroupsPage(
			null, "dateModified lt " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(userGroup2, (List<UserGroup>)page.getItems());
	}

	private void _testGetUserGroupWithNestedFields() throws Exception {
		com.liferay.portal.kernel.model.UserGroup userGroup =
			_userGroupLocalService.addUserGroup(
				StringPool.BLANK, TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		User user1 = UserTestUtil.addUser();
		User user2 = UserTestUtil.addUser();
		User user3 = UserTestUtil.addUser();

		userGroupResource.postUserGroupUsers(
			userGroup.getUserGroupId(),
			new Long[] {user1.getUserId(), user2.getUserId()});

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(
			userGroup.getGroupId(), role.getRoleId());

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			com.liferay.portal.kernel.model.UserGroup.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(userGroup.getUserGroupId()), role.getRoleId(),
			new String[] {ActionKeys.DELETE});

		UserGroupResource userGroupResource = UserGroupResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "creator,permissions,roleBriefs,userAccountBriefs"
		).build();

		UserGroup getUserGroup = userGroupResource.getUserGroup(
			userGroup.getUserGroupId());

		Assert.assertNotNull(getUserGroup.getCreator());

		Creator creator = getUserGroup.getCreator();

		Assert.assertTrue(creator.getId() == TestPropsValues.getUserId());

		Assert.assertTrue(
			ArrayUtil.exists(
				getUserGroup.getPermissions(),
				permission ->
					Objects.equals(permission.getRoleName(), role.getName()) &&
					(permission.getActionIds().length == 1) &&
					Objects.equals(permission.getActionIds()[0], "DELETE")));
		Assert.assertTrue(
			ArrayUtil.exists(
				getUserGroup.getRoleBriefs(),
				groupRole -> groupRole.getId() == role.getRoleId()));
		Assert.assertTrue(
			ArrayUtil.exists(
				getUserGroup.getUserAccountBriefs(),
				userAccountBrief ->
					userAccountBrief.getId() == user1.getUserId()));
		Assert.assertTrue(
			ArrayUtil.exists(
				getUserGroup.getUserAccountBriefs(),
				userAccountBrief ->
					userAccountBrief.getId() == user2.getUserId()));
		Assert.assertFalse(
			ArrayUtil.exists(
				getUserGroup.getUserAccountBriefs(),
				userAccountBrief ->
					userAccountBrief.getId() == user3.getUserId()));
	}

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}