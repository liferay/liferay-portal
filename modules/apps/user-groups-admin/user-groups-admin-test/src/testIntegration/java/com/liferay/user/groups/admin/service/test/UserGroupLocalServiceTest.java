/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateUserGroupException;
import com.liferay.portal.kernel.exception.DuplicateUserGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserGroupNameException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.service.persistence.constants.UserGroupFinderConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class UserGroupLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_count = UserGroupLocalServiceUtil.searchCount(
			TestPropsValues.getCompanyId(), null, new LinkedHashMap<>());

		_userGroup1 = UserGroupTestUtil.addUserGroup();
		_userGroup2 = UserGroupTestUtil.addUserGroup();

		GroupLocalServiceUtil.addRoleGroup(
			_role.getRoleId(), _userGroup1.getGroupId());
	}

	@Test
	public void testAddOrUpdateUserGroup() throws Exception {
		String name = RandomTestUtil.randomString();

		Assert.assertNull(
			_userGroupLocalService.fetchUserGroup(
				TestPropsValues.getCompanyId(), name));

		String externalReferenceCode = RandomTestUtil.randomString();

		_userGroupLocalService.addOrUpdateUserGroup(
			externalReferenceCode, TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), name, RandomTestUtil.randomString(),
			null);

		UserGroup userGroup1 = _userGroupLocalService.fetchUserGroup(
			TestPropsValues.getCompanyId(), name);

		Assert.assertNotNull(userGroup1);

		name = RandomTestUtil.randomString();

		_userGroupLocalService.addOrUpdateUserGroup(
			externalReferenceCode, TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), name, RandomTestUtil.randomString(),
			null);

		UserGroup userGroup2 = _userGroupLocalService.fetchUserGroup(
			TestPropsValues.getCompanyId(), name);

		Assert.assertNotNull(userGroup2);

		Assert.assertEquals(
			userGroup1.getUserGroupId(), userGroup2.getUserGroupId());
	}

	@Test
	public void testAddUserGroup() throws Exception {
		UserGroup userGroup = _userGroupLocalService.addUserGroup(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null);

		try {
			_userGroupLocalService.addUserGroup(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(), userGroup.getName(),
				RandomTestUtil.randomString(), null);

			Assert.fail();
		}
		catch (DuplicateUserGroupException duplicateUserGroupException) {
			Assert.assertNotNull(duplicateUserGroupException);
		}

		_testAddUserGroupWithInvalidName(" ");
		_testAddUserGroupWithInvalidName("1");
		_testAddUserGroupWithInvalidName(RandomTestUtil.randomString() + '*');
		_testAddUserGroupWithInvalidName(RandomTestUtil.randomString() + ',');
	}

	@Test
	public void testAddUserUserGroup() throws Exception {
		User user = UserTestUtil.addUser();
		UserGroup userGroup = _userGroupLocalService.addUserGroup(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null);

		_userGroupLocalService.addUserUserGroup(
			user.getUserId(), userGroup.getUserGroupId());

		Assert.assertEquals(
			1,
			_userLocalService.getUserGroupUsersCount(
				userGroup.getUserGroupId(), WorkflowConstants.STATUS_APPROVED));

		user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		Assert.assertEquals(
			2,
			_userLocalService.getUserGroupUsersCount(
				userGroup.getUserGroupId(), WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testAddUserUserGroups() throws Exception {
		User user = UserTestUtil.addUser();
		UserGroup userGroup1 = _userGroupLocalService.addUserGroup(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null);
		UserGroup userGroup2 = _userGroupLocalService.addUserGroup(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null);

		_userGroupLocalService.addUserUserGroups(
			user.getUserId(),
			new long[] {
				userGroup1.getUserGroupId(), userGroup2.getUserGroupId()
			});

		Assert.assertEquals(
			1,
			_userLocalService.getUserGroupUsersCount(
				userGroup1.getUserGroupId(),
				WorkflowConstants.STATUS_APPROVED));
		Assert.assertEquals(
			1,
			_userLocalService.getUserGroupUsersCount(
				userGroup2.getUserGroupId(),
				WorkflowConstants.STATUS_APPROVED));

		user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroups(
			user.getUserId(), ListUtil.fromArray(userGroup1, userGroup2));

		Assert.assertEquals(
			2,
			_userLocalService.getUserGroupUsersCount(
				userGroup1.getUserGroupId(),
				WorkflowConstants.STATUS_APPROVED));
		Assert.assertEquals(
			2,
			_userLocalService.getUserGroupUsersCount(
				userGroup2.getUserGroupId(),
				WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testClearUserUserGroups() throws Exception {
		User user = UserTestUtil.addUser();
		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();
		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroups(
			user.getUserId(),
			new long[] {
				userGroup1.getUserGroupId(), userGroup2.getUserGroupId()
			});

		_assertSearchUserGroupsCount(2, user);
		_assertSearchUsersCount(1, userGroup1);
		_assertSearchUsersCount(1, userGroup2);

		_userGroupLocalService.clearUserUserGroups(user.getUserId());

		_assertSearchUserGroupsCount(0, user);
		_assertSearchUsersCount(0, userGroup1);
		_assertSearchUsersCount(0, userGroup2);
	}

	@Test
	public void testDatabaseSearchUserUserGroups() throws Exception {
		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), _userGroup1);

		List<UserGroup> userGroups = _search(
			null,
			LinkedHashMapBuilder.<String, Object>put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_USERS,
				user.getUserId()
			).build());

		Assert.assertEquals(userGroups.toString(), 1, userGroups.size());
	}

	@Test
	public void testDatabaseSearchWithInvalidParamKey() throws Exception {
		List<UserGroup> userGroups = _search(
			null,
			LinkedHashMapBuilder.<String, Object>put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_ROLES,
				_role.getRoleId()
			).put(
				"invalidParamKey", "invalidParamValue"
			).build());

		Assert.assertEquals(userGroups.toString(), 1, userGroups.size());
	}

	@Test
	public void testDeleteUserGroup() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.deleteUserGroup(userGroup);

		Assert.assertNull(
			_userGroupLocalService.fetchUserGroup(userGroup.getUserGroupId()));

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0, _portal.getClassNameId(userGroup.getModelClassName()),
				userGroup.getPrimaryKey());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			userGroup.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
	}

	@Test
	public void testDeleteUserUserGroup() throws Exception {
		User user = UserTestUtil.addUser();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroup(
			user.getUserId(), userGroup.getUserGroupId());

		_assertSearchUserGroupsCount(1, user);
		_assertSearchUsersCount(1, userGroup);

		_userGroupLocalService.deleteUserUserGroup(
			user.getUserId(), userGroup.getUserGroupId());

		_assertSearchUserGroupsCount(0, user);
		_assertSearchUsersCount(0, userGroup);

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUserGroupsCount(1, user);
		_assertSearchUsersCount(1, userGroup);

		_userGroupLocalService.deleteUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUserGroupsCount(0, user);
		_assertSearchUsersCount(0, userGroup);
	}

	@Test
	public void testDeleteUserUserGroups() throws Exception {
		User user = UserTestUtil.addUser();
		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();
		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroups(
			user.getUserId(),
			new long[] {
				userGroup1.getUserGroupId(), userGroup2.getUserGroupId()
			});

		_assertSearchUserGroupsCount(2, user);

		_userGroupLocalService.deleteUserUserGroups(
			user.getUserId(), new long[] {userGroup1.getUserGroupId()});

		_assertSearchUserGroupsCount(1, user);
		_assertSearchUsersCount(0, userGroup1);
		_assertSearchUsersCount(1, userGroup2);

		_userGroupLocalService.deleteUserUserGroups(
			user.getUserId(), ListUtil.fromArray(userGroup2));

		_assertSearchUserGroupsCount(0, user);
		_assertSearchUsersCount(0, userGroup2);
	}

	@Test
	public void testGetOrAddEmptyUserGroup() throws Exception {

		// Lazy referencing disabled

		try {
			_userGroupLocalService.getOrAddEmptyUserGroup(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (NoSuchUserGroupException noSuchUserGroupException) {
			Assert.assertNotNull(noSuchUserGroupException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			String externalReferenceCode = RandomTestUtil.randomString();

			UserGroup userGroup = _userGroupLocalService.getOrAddEmptyUserGroup(
				externalReferenceCode, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString());

			Assert.assertEquals(
				externalReferenceCode, userGroup.getExternalReferenceCode());
			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, userGroup.getStatus());
			Assert.assertEquals(
				userGroup,
				_userGroupLocalService.fetchUserGroupByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId()));
		}
	}

	@Test
	public void testSearchRoleUserGroups() throws Exception {
		List<UserGroup> userGroups = _search(
			null,
			LinkedHashMapBuilder.<String, Object>put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_ROLES,
				_role.getRoleId()
			).build());

		Assert.assertEquals(userGroups.toString(), 1, userGroups.size());
	}

	@Test
	public void testSearchRoleUserGroupsWithKeywords() throws Exception {
		List<UserGroup> userGroups = _search(
			_userGroup2.getName(),
			LinkedHashMapBuilder.<String, Object>put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_ROLES,
				_role.getRoleId()
			).build());

		Assert.assertEquals(userGroups.toString(), 0, userGroups.size());
	}

	@Test
	public void testSearchUserGroups() throws Exception {
		List<UserGroup> userGroups = _search(null, new LinkedHashMap<>());

		Assert.assertEquals(
			userGroups.toString(), _count + 2, userGroups.size());
	}

	@Test
	public void testSearchUserGroupsWithKeywords() throws Exception {
		List<UserGroup> userGroups = _search(
			_userGroup1.getName(), new LinkedHashMap<>());

		Assert.assertEquals(userGroups.toString(), 1, userGroups.size());
	}

	@Test
	public void testSearchUserGroupsWithNullParamsAndIndexerDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"USER_GROUPS_SEARCH_WITH_INDEX", Boolean.FALSE)) {

			List<UserGroup> userGroups = _search(null, null);

			Assert.assertEquals(
				userGroups.toString(), _count + 2, userGroups.size());
		}
	}

	@Test
	public void testSearchUserGroupsWithUserIds() throws Exception {
		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), _userGroup1);

		List<UserGroup> userGroups = _search(
			null,
			LinkedHashMapBuilder.<String, Object>put(
				"userIds", () -> new long[] {user.getUserId()}
			).build());

		Assert.assertEquals(userGroups.toString(), 1, userGroups.size());
	}

	@Test
	public void testSearchUserGroupWithDescendingOrder()
		throws PortalException {

		Hits hits = UserGroupLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), null, new LinkedHashMap<>(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			SortFactoryUtil.getSort(UserGroup.class, "name", "desc"));

		List<UserGroup> expectedUserGroups = UsersAdminUtil.getUserGroups(hits);

		List<UserGroup> userGroups = UserGroupLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), null, new LinkedHashMap<>(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			UsersAdminUtil.getUserGroupOrderByComparator("name", "desc"));

		Assert.assertEquals(expectedUserGroups, userGroups);
	}

	@Test
	public void testUpdateExternalReferenceCode() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertNotEquals(
			userGroup.getExternalReferenceCode(), externalReferenceCode);

		userGroup = _userGroupLocalService.updateExternalReferenceCode(
			userGroup, externalReferenceCode);

		Assert.assertEquals(
			userGroup.getExternalReferenceCode(), externalReferenceCode);
	}

	@Test(expected = DuplicateUserGroupExternalReferenceCodeException.class)
	public void testUpdateExternalReferenceCodeInvalidExternalReferenceCode()
		throws Exception {

		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();

		String externalReferenceCode = RandomTestUtil.randomString();

		_userGroupLocalService.updateExternalReferenceCode(
			userGroup1, externalReferenceCode);

		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.updateExternalReferenceCode(
			userGroup2, externalReferenceCode);
	}

	@Test
	public void testUpdateUserGroupWithLazyReferencingEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			String externalReferenceCode = RandomTestUtil.randomString();

			UserGroup userGroup = _userGroupLocalService.getOrAddEmptyUserGroup(
				externalReferenceCode, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString());

			Assert.assertEquals(
				externalReferenceCode, userGroup.getExternalReferenceCode());

			String description = RandomTestUtil.randomString();
			String name = RandomTestUtil.randomString();

			userGroup = _userGroupLocalService.updateUserGroup(
				externalReferenceCode, TestPropsValues.getCompanyId(),
				userGroup.getUserGroupId(), name, description, null);

			Assert.assertEquals(description, userGroup.getDescription());
			Assert.assertEquals(name, userGroup.getName());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, userGroup.getStatus());
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertSearchUserGroupsCount(int expectedCount, User user)
		throws Exception {

		List<UserGroup> userGroups = _search(
			null,
			LinkedHashMapBuilder.<String, Object>put(
				"userIds", new long[] {user.getUserId()}
			).build());

		Assert.assertEquals(
			userGroups.toString(), expectedCount, userGroups.size());
	}

	private void _assertSearchUsersCount(int expectedCount, UserGroup userGroup)
		throws Exception {

		List<User> users = UsersAdminUtil.getUsers(
			_userLocalService.search(
				userGroup.getCompanyId(), null,
				WorkflowConstants.STATUS_APPROVED,
				LinkedHashMapBuilder.<String, Object>put(
					"usersUserGroups", userGroup.getUserGroupId()
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, (Sort[])null));

		Assert.assertEquals(users.toString(), expectedCount, users.size());
	}

	private List<UserGroup> _search(
			String keywords, LinkedHashMap<String, Object> params)
		throws Exception {

		return UserGroupLocalServiceUtil.search(
			TestPropsValues.getCompanyId(), keywords, params, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			UsersAdminUtil.getUserGroupOrderByComparator("name", "asc"));
	}

	private void _testAddUserGroupWithInvalidName(String name)
		throws Exception {

		try {
			_userGroupLocalService.addUserGroup(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(), name,
				RandomTestUtil.randomString(), null);

			Assert.fail();
		}
		catch (UserGroupNameException userGroupNameException) {
			Assert.assertNotNull(userGroupNameException);
		}
	}

	private int _count;

	@Inject
	private Portal _portal;

	private Role _role;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	private UserGroup _userGroup1;
	private UserGroup _userGroup2;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}