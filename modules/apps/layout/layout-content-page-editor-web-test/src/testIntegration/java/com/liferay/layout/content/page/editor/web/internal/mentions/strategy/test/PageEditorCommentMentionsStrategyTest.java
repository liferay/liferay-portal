/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.mentions.strategy.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.mentions.strategy.MentionsStrategy;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class PageEditorCommentMentionsStrategyTest {

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
	public void testGetUsersWithCustomRoleWithUpdatePermission()
		throws Exception {

		Role role = _addRole();

		_addUserWithRole(_group.getGroupId(), role.getName(), "example");

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		RoleTestUtil.addResourcePermission(
			role, "com.liferay.portal.kernel.model.Layout",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.UPDATE);

		List<User> users = _mentionsStrategy.getUsers(
			_group.getCompanyId(), TestPropsValues.getUserId(), "exa",
			JSONUtil.put("plid", layout.getPlid()));

		Assert.assertEquals(users.toString(), 1, users.size());

		User user = users.get(0);

		Assert.assertEquals("example", user.getScreenName());
	}

	@Test
	public void testGetUsersWithCustomRoleWithoutUpdatePermission()
		throws Exception {

		Role role = _addRole();

		_addUserWithRole(_group.getGroupId(), role.getName(), "example");

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		RoleTestUtil.addResourcePermission(
			role, "com.liferay.portal.kernel.model.Layout",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.VIEW);

		List<User> users = _mentionsStrategy.getUsers(
			_group.getCompanyId(), TestPropsValues.getUserId(), "exa",
			JSONUtil.put("plid", layout.getPlid()));

		Assert.assertEquals(users.toString(), 0, users.size());
	}

	@Test
	public void testGetUsersWithSiteAdmin() throws Exception {
		_addUserWithRole(
			_group.getGroupId(), RoleConstants.SITE_ADMINISTRATOR, "example");

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		List<User> users = _mentionsStrategy.getUsers(
			_group.getCompanyId(), TestPropsValues.getUserId(), "exa",
			JSONUtil.put("plid", layout.getPlid()));

		Assert.assertEquals(users.toString(), 1, users.size());

		User user = users.get(0);

		Assert.assertEquals("example", user.getScreenName());
	}

	@Test
	public void testGetUsersWithSiteContentReviewer() throws Exception {
		_addUserWithRole(
			_group.getGroupId(), RoleConstants.SITE_CONTENT_REVIEWER,
			"example1");

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		List<User> users = _mentionsStrategy.getUsers(
			_group.getCompanyId(), TestPropsValues.getUserId(), "exa",
			JSONUtil.put("plid", layout.getPlid()));

		Assert.assertEquals(users.toString(), 0, users.size());
	}

	private Role _addRole() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_roles.add(role);

		return role;
	}

	private User _addUserWithRole(
			long groupId, String roleName, String userName)
		throws Exception {

		User user = UserTestUtil.addUser(userName, _group.getGroupId());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), groupId, new long[] {role.getRoleId()});

		_users.add(user);

		return user;
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "mentions.strategy=pageEditorCommentStrategy")
	private MentionsStrategy _mentionsStrategy;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private final List<Role> _roles = new ArrayList<>();

	@Inject
	private UserGroupRoleService _userGroupRoleService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}