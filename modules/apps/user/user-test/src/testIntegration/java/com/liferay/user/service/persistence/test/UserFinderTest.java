/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.UserFinder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.social.kernel.model.SocialRelationConstants;
import com.liferay.social.kernel.service.SocialRelationLocalService;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jozsef Illes
 */
@RunWith(Arquillian.class)
public class UserFinderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), TransactionalTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_group = GroupTestUtil.addGroup();
		_groupUser = UserTestUtil.addUser();

		_groupLocalService.addUserGroup(_groupUser.getUserId(), _group);

		_organization1 = OrganizationTestUtil.addOrganization(true);
		_organizationUser1 = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			_organizationUser1.getUserId(), _organization1);

		_organization2 = OrganizationTestUtil.addOrganization(true);
		_organizationUser2 = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			_organizationUser2.getUserId(), _organization2);

		_socialUser = UserTestUtil.addUser();

		_socialRelationLocalService.addRelation(
			_groupUser.getUserId(), _socialUser.getUserId(),
			SocialRelationConstants.TYPE_BI_CONNECTION);

		_team = _teamLocalService.addTeam(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		_teamUser = UserTestUtil.addUser();

		_teamLocalService.addUserTeam(_teamUser.getUserId(), _team);

		_userGroup = UserGroupTestUtil.addUserGroup();
		_userGroupUser = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(
			_userGroupUser.getUserId(), _userGroup);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_depotEntryLocalService.deleteDepotEntry(_depotEntry);

		_groupLocalService.deleteGroup(_group);
		_userLocalService.deleteUser(_groupUser);

		_userLocalService.deleteUser(_organizationUser1);
		_userLocalService.deleteUser(_organizationUser2);

		_organizationLocalService.deleteOrganization(_organization1);
		_organizationLocalService.deleteOrganization(_organization2);

		_userLocalService.deleteUser(_teamUser);

		_teamLocalService.deleteTeam(_team);

		_userLocalService.deleteUser(_socialUser);
		_userLocalService.deleteUser(_userGroupUser);

		_userGroupLocalService.deleteUserGroup(_userGroup);
	}

	@Before
	public void setUp() throws Exception {
		_inheritedUserGroupsParams = LinkedHashMapBuilder.<String, Object>put(
			"inherit", Boolean.TRUE
		).put(
			"usersGroups",
			new Long[] {
				_group.getGroupId(), _organization1.getGroupId(),
				_userGroup.getGroupId()
			}
		).build();

		_inheritedUserGroupsDepotEntryParams =
			LinkedHashMapBuilder.<String, Object>put(
				"inherit", Boolean.TRUE
			).put(
				"usersGroups", new Long[] {_depotEntry.getGroupId()}
			).build();

		_inheritedUserGroupsExpectedCount = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserGroupsParams);

		_roleId = RoleTestUtil.addRegularRole(_group.getGroupId());

		_inheritedUserRolesParams = LinkedHashMapBuilder.<String, Object>put(
			"inherit", Boolean.TRUE
		).put(
			"usersRoles", _roleId
		).build();

		_inheritedUserTeamsParams = LinkedHashMapBuilder.<String, Object>put(
			"inherit", Boolean.TRUE
		).put(
			"usersTeams", new Long[] {_team.getTeamId()}
		).build();
	}

	@After
	public void tearDown() throws Exception {
		_roleLocalService.deleteRole(_roleId);

		_groupLocalService.clearOrganizationGroups(
			_organization1.getOrganizationId());
		_groupLocalService.clearUserGroupGroups(_userGroup.getUserGroupId());
		_teamLocalService.clearUserGroupTeams(_userGroup.getUserGroupId());
	}

	@Test
	public void testCountByKeywordsWithInheritedGroupsThroughDepotEntry()
		throws Exception {

		int expectedCount = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			_inheritedUserGroupsDepotEntryParams);

		_groupLocalService.addUserGroupGroup(
			_userGroup.getUserGroupId(), _depotEntry.getGroupId());

		int count = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			_inheritedUserGroupsDepotEntryParams);

		Assert.assertEquals(expectedCount + 1, count);
	}

	@Test
	public void testCountByKeywordsWithInheritedRoles() throws Exception {
		int expectedCount = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams);

		_roleLocalService.addGroupRole(_organization1.getGroupId(), _roleId);
		_roleLocalService.addGroupRole(_userGroup.getGroupId(), _roleId);

		int count = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams);

		Assert.assertEquals(expectedCount + 2, count);
	}

	@Test
	public void testCountByKeywordsWithInheritedRolesThroughSite()
		throws Exception {

		int expectedCount = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams);

		_groupLocalService.addOrganizationGroup(
			_organization1.getOrganizationId(), _group);
		_groupLocalService.addUserGroupGroup(
			_userGroup.getUserGroupId(), _group);

		int count = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams);

		Assert.assertEquals(expectedCount + 2, count);
	}

	@Test
	public void testCountByKeywordsWithInheritedTeams() throws Exception {
		int expectedCount = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserTeamsParams);

		_teamLocalService.addUserGroupTeam(_userGroup.getUserGroupId(), _team);

		int count = _userFinder.countByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserTeamsParams);

		Assert.assertEquals(expectedCount + 1, count);
	}

	@Test
	public void testFindByC_FN_MN_LN_SN_EA_S() throws Exception {
		String[] firstNames = {null};
		String[] middleNames = {null};
		String[] lastNames = {null};
		String[] screenNames = {null};
		String[] emailAddresses = {null};

		_userFinder.findByC_FN_MN_LN_SN_EA_S(
			TestPropsValues.getCompanyId(), firstNames, middleNames, lastNames,
			screenNames, emailAddresses, 0,
			LinkedHashMapBuilder.<String, Object>put(
				"announcementsDeliveryEmailOrSms", "general"
			).build(),
			true, 0, 1, null);
	}

	@Test
	public void testFindByKeywordsGroupUsers() throws Exception {
		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersGroups", _group.getGroupId()
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.toString(), users.contains(_groupUser));
	}

	@Test
	public void testFindByKeywordsOrganizationsMembershipStrict()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"ORGANIZATIONS_MEMBERSHIP_STRICT", false)) {

			testFindByKeywordsWithInheritedGroups();
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"ORGANIZATIONS_MEMBERSHIP_STRICT", true)) {

			testFindByKeywordsWithInheritedGroups();
		}
	}

	@Test
	public void testFindByKeywordsOrganizationUsers() throws Exception {
		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersOrgs", _organization1.getOrganizationId()
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.toString(), users.contains(_organizationUser1));
		Assert.assertFalse(
			users.toString(), users.contains(_organizationUser2));
	}

	@Test
	public void testFindByKeywordsUserGroupUsers() throws Exception {
		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersUserGroups", _userGroup.getUserGroupId()
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.toString(), users.contains(_userGroupUser));
	}

	@Test
	public void testFindByKeywordsWithInheritedGroups() throws Exception {
		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserGroupsParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.toString(), users.contains(_groupUser));
		Assert.assertTrue(users.toString(), users.contains(_organizationUser1));
		Assert.assertFalse(
			users.toString(), users.contains(_organizationUser2));
		Assert.assertTrue(users.toString(), users.contains(_userGroupUser));
		Assert.assertTrue(
			users.toString(), users.contains(TestPropsValues.getUser()));
		Assert.assertEquals(
			users.toString(), _inheritedUserGroupsExpectedCount, users.size());
	}

	@Test
	public void testFindByKeywordsWithInheritedGroupsThroughDepotEntry()
		throws Exception {

		_groupLocalService.addUserGroupGroup(
			_userGroup.getUserGroupId(), _depotEntry.getGroupId());

		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			_inheritedUserGroupsDepotEntryParams, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.contains(_userGroupUser));
	}

	@Test
	public void testFindByKeywordsWithInheritedRoles() throws Exception {
		List<User> expectedUsers = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_roleLocalService.addGroupRole(_organization1.getGroupId(), _roleId);
		_roleLocalService.addGroupRole(_userGroup.getGroupId(), _roleId);

		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.toString(), users.contains(_groupUser));
		Assert.assertTrue(users.toString(), users.contains(_organizationUser1));
		Assert.assertTrue(users.toString(), users.contains(_userGroupUser));
		Assert.assertTrue(
			users.toString(), users.contains(TestPropsValues.getUser()));
		Assert.assertEquals(
			users.toString(), expectedUsers.size() + 2, users.size());
	}

	@Test
	public void testFindByKeywordsWithInheritedRolesThroughSite()
		throws Exception {

		List<User> expectedUsers = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_groupLocalService.addOrganizationGroup(
			_organization1.getOrganizationId(), _group);
		_groupLocalService.addUserGroupGroup(
			_userGroup.getUserGroupId(), _group);

		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserRolesParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.toString(), users.contains(_groupUser));
		Assert.assertTrue(users.toString(), users.contains(_organizationUser1));
		Assert.assertTrue(users.toString(), users.contains(_userGroupUser));
		Assert.assertTrue(
			users.toString(), users.contains(TestPropsValues.getUser()));
		Assert.assertEquals(
			users.toString(), expectedUsers.size() + 2, users.size());
	}

	@Test
	public void testFindByKeywordsWithInheritedTeams() throws Exception {
		List<User> expectedUsers = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserTeamsParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_teamLocalService.addUserGroupTeam(_userGroup.getUserGroupId(), _team);

		List<User> users = _userFinder.findByKeywords(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, _inheritedUserTeamsParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(users.toString(), users.contains(_teamUser));
		Assert.assertTrue(users.toString(), users.contains(_userGroupUser));
		Assert.assertEquals(
			users.toString(), expectedUsers.size() + 1, users.size());
	}

	@Test
	public void testFindBySocialUsers() throws Exception {
		List<User> users = _userFinder.findBySocialUsers(
			TestPropsValues.getCompanyId(), _groupUser.getUserId(),
			SocialRelationConstants.TYPE_BI_CONNECTION, StringPool.EQUAL,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, UserFirstNameComparator.getInstance(true));

		Assert.assertEquals(users.toString(), 1, users.size());
	}

	private static DepotEntry _depotEntry;

	@Inject
	private static DepotEntryLocalService _depotEntryLocalService;

	private static Group _group;

	@Inject
	private static GroupLocalService _groupLocalService;

	private static User _groupUser;
	private static Organization _organization1;
	private static Organization _organization2;

	@Inject
	private static OrganizationLocalService _organizationLocalService;

	private static User _organizationUser1;
	private static User _organizationUser2;

	@Inject
	private static SocialRelationLocalService _socialRelationLocalService;

	private static User _socialUser;
	private static Team _team;

	@Inject
	private static TeamLocalService _teamLocalService;

	private static User _teamUser;
	private static UserGroup _userGroup;

	@Inject
	private static UserGroupLocalService _userGroupLocalService;

	private static User _userGroupUser;

	@Inject
	private static UserLocalService _userLocalService;

	private LinkedHashMap<String, Object> _inheritedUserGroupsDepotEntryParams;
	private int _inheritedUserGroupsExpectedCount;
	private LinkedHashMap<String, Object> _inheritedUserGroupsParams;
	private LinkedHashMap<String, Object> _inheritedUserRolesParams;
	private LinkedHashMap<String, Object> _inheritedUserTeamsParams;
	private long _roleId;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserFinder _userFinder;

}