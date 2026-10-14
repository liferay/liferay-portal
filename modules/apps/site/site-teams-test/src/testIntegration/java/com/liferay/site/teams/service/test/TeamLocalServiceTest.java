/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class TeamLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testClearUserGroupTeams() throws Exception {
		Team team = _addTeam();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_teamLocalService.addUserGroupTeam(
			userGroup.getUserGroupId(), team.getTeamId());

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUser(true, team, user);

		_teamLocalService.clearUserGroupTeams(userGroup.getUserGroupId());

		_assertSearchUser(false, team, user);
	}

	@Test
	public void testClearUserTeams() throws Exception {
		Team team = _addTeam();
		User user = UserTestUtil.addUser();

		_teamLocalService.addUserTeam(user.getUserId(), team.getTeamId());

		_assertSearchUser(true, team, user);

		_teamLocalService.clearUserTeams(user.getUserId());

		_assertSearchUser(false, team, user);
	}

	@Test
	public void testDeleteTeam() throws Exception {
		_group = GroupTestUtil.addGroup();

		Team team = _teamLocalService.addTeam(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"defaultTeamIds", String.valueOf(team.getTeamId()));

		_group = _groupLocalService.updateGroup(_group);

		User user1 = UserTestUtil.addUser();

		_teamLocalService.addUserTeam(user1.getUserId(), team.getTeamId());

		_assertSearchUser(true, team, user1);

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_teamLocalService.addUserGroupTeam(
			userGroup.getUserGroupId(), team.getTeamId());

		User user2 = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user2.getUserId(), userGroup);

		_assertSearchUser(true, team, user2);

		_teamLocalService.deleteTeam(team);

		_assertSearchUser(false, team, user1);
		_assertSearchUser(false, team, user2);

		_group = _groupLocalService.getGroup(_group.getGroupId());

		typeSettingsUnicodeProperties = _group.getTypeSettingsProperties();

		List<Long> defaultTeamIds = ListUtil.fromArray(
			StringUtil.split(
				typeSettingsUnicodeProperties.getProperty("defaultTeamIds"),
				0L));

		Assert.assertFalse(defaultTeamIds.contains(team.getTeamId()));
	}

	@Test
	public void testDeleteUserGroupTeam() throws Exception {
		Team team = _addTeam();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_teamLocalService.addUserGroupTeam(
			userGroup.getUserGroupId(), team.getTeamId());

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserGroupTeam(
			userGroup.getUserGroupId(), team.getTeamId());

		_assertSearchUser(false, team, user);

		_teamLocalService.addUserGroupTeam(userGroup.getUserGroupId(), team);

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserGroupTeam(userGroup.getUserGroupId(), team);

		_assertSearchUser(false, team, user);
	}

	@Test
	public void testDeleteUserGroupTeams() throws Exception {
		Team team = _addTeam();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_teamLocalService.addUserGroupTeams(
			userGroup.getUserGroupId(), new long[] {team.getTeamId()});

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserGroupTeams(
			userGroup.getUserGroupId(), new long[] {team.getTeamId()});

		_assertSearchUser(false, team, user);

		_teamLocalService.addUserGroupTeams(
			userGroup.getUserGroupId(), Collections.singletonList(team));

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserGroupTeams(
			userGroup.getUserGroupId(), Collections.singletonList(team));

		_assertSearchUser(false, team, user);
	}

	@Test
	public void testDeleteUserTeam() throws Exception {
		Team team = _addTeam();
		User user = UserTestUtil.addUser();

		_teamLocalService.addUserTeam(user.getUserId(), team.getTeamId());

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserTeam(user.getUserId(), team.getTeamId());

		_assertSearchUser(false, team, user);

		_teamLocalService.addUserTeam(user.getUserId(), team);

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserTeam(user.getUserId(), team);

		_assertSearchUser(false, team, user);
	}

	@Test
	public void testDeleteUserTeams() throws Exception {
		Team team = _addTeam();
		User user = UserTestUtil.addUser();

		_teamLocalService.addUserTeams(
			user.getUserId(), new long[] {team.getTeamId()});

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserTeams(
			user.getUserId(), new long[] {team.getTeamId()});

		_assertSearchUser(false, team, user);

		_teamLocalService.addUserTeams(
			user.getUserId(), Collections.singletonList(team));

		_assertSearchUser(true, team, user);

		_teamLocalService.deleteUserTeams(
			user.getUserId(), Collections.singletonList(team));

		_assertSearchUser(false, team, user);
	}

	@Test
	public void testSetUserGroupTeams() throws Exception {
		Team team1 = _addTeam();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_teamLocalService.setUserGroupTeams(
			userGroup.getUserGroupId(), new long[] {team1.getTeamId()});

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertSearchUser(true, team1, user);

		Team team2 = _addTeam();

		_assertSearchUser(false, team2, user);

		_teamLocalService.setUserGroupTeams(
			userGroup.getUserGroupId(), new long[] {team2.getTeamId()});

		_assertSearchUser(false, team1, user);
		_assertSearchUser(true, team2, user);
	}

	@Test
	public void testSetUserTeams() throws Exception {
		Team team1 = _addTeam();
		User user = UserTestUtil.addUser();

		_teamLocalService.setUserTeams(
			user.getUserId(), new long[] {team1.getTeamId()});

		_assertSearchUser(true, team1, user);

		Team team2 = _addTeam();

		_assertSearchUser(false, team2, user);

		_teamLocalService.setUserTeams(
			user.getUserId(), new long[] {team2.getTeamId()});

		_assertSearchUser(false, team1, user);
		_assertSearchUser(true, team2, user);
	}

	private Team _addTeam() throws Exception {
		return _teamLocalService.addTeam(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertSearchUser(boolean expected, Team team, User user)
		throws Exception {

		Hits hits = _userLocalService.search(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersTeams", team.getTeamId()
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, (Sort[])null);

		List<Long> userIds = TransformUtil.transform(
			hits.toList(),
			document -> GetterUtil.getLong(document.get(Field.USER_ID)));

		Assert.assertEquals(
			userIds.toString(), expected, userIds.contains(user.getUserId()));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private TeamLocalService _teamLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}