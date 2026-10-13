/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class UserGroupRoleLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddUserGroupRoles() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser(null, _group.getGroupId());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		Assert.assertNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				_user.getUserId(), _group.getGroupId(), role.getRoleId()));

		List<UserGroupRole> userGroupRoles =
			_userGroupRoleLocalService.addUserGroupRoles(
				new long[] {_user.getUserId()}, _group.getGroupId(),
				role.getRoleId());

		Assert.assertEquals(
			userGroupRoles.toString(), 1, userGroupRoles.size());

		_entityCache.clearLocalCache();

		Assert.assertEquals(
			userGroupRoles.get(0),
			_userGroupRoleLocalService.fetchUserGroupRole(
				_user.getUserId(), _group.getGroupId(), role.getRoleId()));

		_userLocalService.deleteGroupUser(
			_group.getGroupId(), _user.getUserId());

		Assert.assertNotNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				_user.getUserId(), _group.getGroupId(), role.getRoleId()));

		_assertSearchUser(false, _group, _user);

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {_user.getUserId()}, _group.getGroupId(),
			role.getRoleId());

		_assertSearchUser(true, _group, _user);

		_userLocalService.deleteGroupUser(
			_group.getGroupId(), _user.getUserId());

		_assertSearchUser(false, _group, _user);

		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _group.getGroupId(),
			new long[] {role.getRoleId()});

		_assertSearchUser(true, _group, _user);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertSearchUser(boolean expected, Group group, User user)
		throws Exception {

		Hits hits = _userLocalService.search(
			group.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersGroups", group.getGroupId()
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, (Sort[])null);

		List<Long> userIds = TransformUtil.transform(
			hits.toList(),
			document -> GetterUtil.getLong(document.get(Field.USER_ID)));

		Assert.assertEquals(
			userIds.toString(), expected, userIds.contains(user.getUserId()));
	}

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}