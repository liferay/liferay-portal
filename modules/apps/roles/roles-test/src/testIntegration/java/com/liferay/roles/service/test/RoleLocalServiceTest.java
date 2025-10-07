/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.RoleNameException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ResourcePermissionTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.comparator.RoleRoleIdComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author László Csontos
 */
@RunWith(Arquillian.class)
public class RoleLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		List<Role> roles = _roleLocalService.getRoles(
			RoleConstants.TYPE_REGULAR, StringPool.BLANK);

		_arbitraryRole = roles.get(0);

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(0, 1);

		_arbitraryResourceAction = resourceActions.get(0);

		_resourcePermission = ResourcePermissionTestUtil.addResourcePermission(
			_arbitraryResourceAction.getBitwiseValue(),
			_arbitraryResourceAction.getName(), _arbitraryRole.getRoleId());
	}

	@AfterClass
	public static void tearDownClass() {
		_resourcePermissionLocalService.deleteResourcePermission(
			_resourcePermission);
	}

	@Test
	public void testAddRole() throws Exception {
		try {
			RoleTestUtil.addRole(
				RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE,
				RoleConstants.TYPE_REGULAR);

			Assert.fail();
		}
		catch (RoleNameException roleNameException) {
			Assert.assertNotNull(roleNameException);
		}

		_role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString(4001)),
			RoleConstants.TYPE_REGULAR, null, null);

		Assert.assertNotNull(_role);
	}

	@Test
	public void testAddRoleWithoutExternalReferenceCode() throws Exception {
		_role = _roleLocalService.addRole(
			null, TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			RoleConstants.TYPE_REGULAR, null, null);

		Assert.assertNotNull(_role.getExternalReferenceCode());
	}

	@Test
	public void testCopyRole() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		String name = RandomTestUtil.randomString();
		Role sourceRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.ADMINISTRATOR);

		Role targetRole = _roleLocalService.copyRole(
			TestPropsValues.getUserId(), name, sourceRole.getRoleId(),
			new ServiceContext());

		Assert.assertNotEquals(sourceRole.getRoleId(), targetRole.getRoleId());
		Assert.assertEquals(name, targetRole.getName());

		List<ResourcePermission> sourceRoleResourcePermissions =
			_resourcePermissionLocalService.getRoleResourcePermissions(
				sourceRole.getRoleId());

		List<ResourcePermission> targetRoleResourcePermissions =
			_resourcePermissionLocalService.getRoleResourcePermissions(
				targetRole.getRoleId());

		Assert.assertEquals(
			targetRoleResourcePermissions.toString(),
			sourceRoleResourcePermissions.size(),
			targetRoleResourcePermissions.size());
	}

	@Test
	public void testDeleteRole() throws Exception {
		_group = GroupTestUtil.addGroup();
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"defaultSiteRoleIds", String.valueOf(_role.getRoleId()));

		_group = _groupLocalService.updateGroup(_group);

		_roleLocalService.deleteRole(_role);

		_group = _groupLocalService.getGroup(_group.getGroupId());

		typeSettingsUnicodeProperties = _group.getTypeSettingsProperties();

		List<Long> defaultSiteRoleIds = ListUtil.fromArray(
			StringUtil.split(
				typeSettingsUnicodeProperties.getProperty("defaultSiteRoleIds"),
				0L));

		Assert.assertFalse(defaultSiteRoleIds.contains(_role.getRoleId()));

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0, _portal.getClassNameId(_role.getModelClassName()),
				_role.getPrimaryKey());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			_role.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
	}

	@Test
	public void testGetAssigneesTotalOrganizationRole() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_ORGANIZATION);
		_user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), _organization);
		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _organization.getGroupId(),
			new long[] {_role.getRoleId()});

		Assert.assertEquals(
			1, _roleLocalService.getAssigneesTotal(_role.getRoleId()));
	}

	@Test
	public void testGetAssigneesTotalRegularRole() throws Exception {
		_group = GroupTestUtil.addGroup();
		_organization = OrganizationTestUtil.addOrganization();
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();
		_userGroup = UserGroupTestUtil.addUserGroup();

		_roleLocalService.addUserRole(_user.getUserId(), _role);
		_roleLocalService.addGroupRole(_group.getGroupId(), _role);
		_roleLocalService.addGroupRole(_organization.getGroupId(), _role);
		_roleLocalService.addGroupRole(_userGroup.getGroupId(), _role);

		Assert.assertEquals(
			4, _roleLocalService.getAssigneesTotal(_role.getRoleId()));
	}

	@Test
	public void testGetAssigneesTotalSiteRole() throws Exception {
		_group = GroupTestUtil.addGroup();
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_user = UserTestUtil.addUser();

		_userGroup = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroup(_user.getUserId(), _group);

		_groupLocalService.addUserGroupGroup(
			_userGroup.getUserGroupId(), _group);

		long[] roleIds = {_role.getRoleId()};

		_userGroupGroupRoleLocalService.addUserGroupGroupRoles(
			_userGroup.getGroupId(), _group.getGroupId(), roleIds);
		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _group.getGroupId(), roleIds);

		Assert.assertEquals(
			2, _roleLocalService.getAssigneesTotal(_role.getRoleId()));

		_user.setStatus(WorkflowConstants.STATUS_INACTIVE);

		_user = _userLocalService.updateUser(_user);

		Assert.assertEquals(
			1, _roleLocalService.getAssigneesTotal(_role.getRoleId()));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void testGetGroupRelatedRoles() throws Exception {
		createOrganizationAndTeam();

		long groupId = _organization.getGroupId();

		Group group = _groupLocalService.getGroup(groupId);

		List<Role> actualRoles = _roleLocalService.getGroupRelatedRoles(
			groupId);

		List<Role> expectedRoles = new ArrayList<>();

		for (Role role :
				_roleLocalService.getRoles(_organization.getCompanyId())) {

			int type = role.getType();

			if ((type == RoleConstants.TYPE_REGULAR) ||
				((type == RoleConstants.TYPE_ORGANIZATION) &&
				 group.isOrganization()) ||
				((type == RoleConstants.TYPE_SITE) &&
				 (group.isLayout() || group.isLayoutSetPrototype() ||
				  group.isSite()))) {

				expectedRoles.add(role);
			}
			else if ((type == RoleConstants.TYPE_PROVIDER) && role.isTeam()) {
				Team team = _teamLocalService.getTeam(role.getClassPK());

				if (team.getGroupId() == groupId) {
					expectedRoles.add(role);
				}
			}
		}

		Comparator roleIdComparator = RoleRoleIdComparator.getInstance(false);

		Collections.sort(actualRoles, roleIdComparator);
		Collections.sort(expectedRoles, roleIdComparator);

		Assert.assertEquals(expectedRoles, actualRoles);
	}

	@Test
	public void testGetGroupRolesAndTeamRoles() throws Exception {
		createOrganizationAndTeam();

		long companyId = _organization.getCompanyId();
		long groupId = _organization.getGroupId();

		int[] roleTypes = RoleConstants.TYPES_ORGANIZATION_AND_REGULAR;

		List<String> excludedRoleNames = new ArrayList<>();

		excludedRoleNames.add(RoleConstants.ADMINISTRATOR);
		excludedRoleNames.add(RoleConstants.GUEST);

		List<Role> actualRoles = _roleLocalService.getGroupRolesAndTeamRoles(
			companyId, null, excludedRoleNames, null, null, roleTypes, 0,
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<Role> expectedRoles = ListUtil.filter(
			_roleLocalService.getRoles(companyId),
			role -> {
				if (excludedRoleNames.contains(role.getName()) ||
					(role.getType() == RoleConstants.TYPE_ACCOUNT) ||
					(role.getType() == RoleConstants.TYPE_DEPOT) ||
					(role.getType() == RoleConstants.TYPE_PUBLICATIONS) ||
					(role.getType() == RoleConstants.TYPE_SITE)) {

					return false;
				}

				if (role.getType() != RoleConstants.TYPE_PROVIDER) {
					return true;
				}

				if (!role.isTeam()) {
					return false;
				}

				Team team = _teamLocalService.fetchTeam(role.getClassPK());

				if (team == null) {
					return false;
				}

				return team.getGroupId() == groupId;
			});

		Assert.assertEquals(
			expectedRoles.size(),
			_roleLocalService.getGroupRolesAndTeamRolesCount(
				companyId, null, excludedRoleNames, null, null, roleTypes, 0,
				groupId));

		actualRoles = new ArrayList(actualRoles);
		expectedRoles = new ArrayList(expectedRoles);

		Comparator<Role> roleIdComparator = RoleRoleIdComparator.getInstance(
			false);

		Collections.sort(actualRoles, roleIdComparator);
		Collections.sort(expectedRoles, roleIdComparator);

		Assert.assertEquals(expectedRoles, actualRoles);
	}

	@Test
	public void testGetGroupRolesAndTeamRolesWithKeyword() throws Exception {
		createOrganizationAndTeam();

		long userId = TestPropsValues.getUserId();
		String keyword = RandomTestUtil.randomString();

		Role role1 = _roleLocalService.addRole(
			RandomTestUtil.randomString(), userId, null, 0, keyword,
			Collections.singletonMap(LocaleUtil.getDefault(), keyword),
			Collections.emptyMap(), RoleConstants.TYPE_SITE, StringPool.BLANK,
			new ServiceContext());
		Role role2 = _roleLocalService.addRole(
			RandomTestUtil.randomString(), userId, null, 0,
			StringUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.getDefault(), StringUtil.randomString()),
			Collections.singletonMap(LocaleUtil.getDefault(), keyword),
			RoleConstants.TYPE_SITE, StringPool.BLANK, new ServiceContext());

		long companyId = _organization.getCompanyId();
		List<String> excludedRoleNames = new ArrayList<>();
		long groupId = _organization.getGroupId();
		int[] roleTypes = RoleConstants.TYPES_ORGANIZATION_AND_REGULAR_AND_SITE;

		Assert.assertEquals(
			1,
			_roleLocalService.getGroupRolesAndTeamRolesCount(
				companyId, keyword, excludedRoleNames, keyword, null, roleTypes,
				0, groupId));

		List<Role> roles = _roleLocalService.getGroupRolesAndTeamRoles(
			companyId, keyword, excludedRoleNames, keyword, null, roleTypes, 0,
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(role1, roles.get(0));

		excludedRoleNames.add(role1.getName());

		Assert.assertEquals(
			0,
			_roleLocalService.getGroupRolesAndTeamRolesCount(
				companyId, keyword, excludedRoleNames, keyword, null, roleTypes,
				0, groupId));

		roles = _roleLocalService.getGroupRolesAndTeamRoles(
			companyId, keyword, excludedRoleNames, keyword, null, roleTypes, 0,
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertTrue(roles.toString(), roles.isEmpty());

		Assert.assertEquals(
			1,
			_roleLocalService.getGroupRolesAndTeamRolesCount(
				companyId, keyword, excludedRoleNames, keyword, keyword,
				roleTypes, 0, groupId));

		roles = _roleLocalService.getGroupRolesAndTeamRoles(
			companyId, keyword, excludedRoleNames, keyword, keyword, roleTypes,
			0, groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(role2, roles.get(0));

		keyword = RandomTestUtil.randomString();

		Team team1 = _teamLocalService.addTeam(
			userId, groupId, keyword, RandomTestUtil.randomString(),
			new ServiceContext());
		Team team2 = _teamLocalService.addTeam(
			userId, groupId, RandomTestUtil.randomString(), keyword,
			new ServiceContext());

		Assert.assertEquals(
			1,
			_roleLocalService.getGroupRolesAndTeamRolesCount(
				companyId, keyword, excludedRoleNames, keyword, null, roleTypes,
				0, groupId));

		roles = _roleLocalService.getGroupRolesAndTeamRoles(
			companyId, keyword, excludedRoleNames, keyword, null, roleTypes, 0,
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_role = roles.get(0);

		Assert.assertEquals(team1.getTeamId(), _role.getClassPK());

		Assert.assertEquals(
			2,
			_roleLocalService.getGroupRolesAndTeamRolesCount(
				companyId, keyword, excludedRoleNames, keyword, keyword,
				roleTypes, 0, groupId));

		roles = _roleLocalService.getGroupRolesAndTeamRoles(
			companyId, keyword, excludedRoleNames, keyword, keyword, roleTypes,
			0, groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_role = roles.get(0);

		Assert.assertEquals(team1.getTeamId(), _role.getClassPK());

		_role = roles.get(1);

		Assert.assertEquals(team2.getTeamId(), _role.getClassPK());
	}

	@Test
	public void testGetOrAddEmptyRole() throws Exception {

		// Lazy referencing disabled

		try {
			_roleLocalService.getOrAddEmptyRole(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), Role.class.getName(), 0,
				RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

			Assert.fail();
		}
		catch (NoSuchRoleException noSuchRoleException) {
			Assert.assertNotNull(noSuchRoleException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Role role = _roleLocalService.getOrAddEmptyRole(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), Role.class.getName(), 0,
				RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, role.getStatus());
		}
	}

	@Test
	public void testGetResourceActionRoles() {
		List<Role> roles = _roleLocalService.getResourceRoles(
			_resourcePermission.getCompanyId(), _resourcePermission.getName(),
			_resourcePermission.getScope(), _resourcePermission.getPrimKey(),
			_arbitraryResourceAction.getActionId());

		Assert.assertTrue(
			"The method getResourceActionRoles should have returned the role " +
				_arbitraryRole.getRoleId(),
			roles.contains(_arbitraryRole));
	}

	@Test
	public void testGetResourceRoles() throws Exception {
		long companyId = _resourcePermission.getCompanyId();
		String name = _resourcePermission.getName();
		int scope = _resourcePermission.getScope();
		String primKey = _resourcePermission.getPrimKey();

		Map<String, List<String>> actionIdsLists = new HashMap<>();

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(name);

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					companyId, name, scope, primKey)) {

			Role role = _roleLocalService.getRole(
				resourcePermission.getRoleId());

			long actionIds = resourcePermission.getActionIds();

			List<String> actionIdsList = new ArrayList<>();

			for (ResourceAction resourceAction : resourceActions) {
				if ((resourceAction.getBitwiseValue() & actionIds) != 0) {
					actionIdsList.add(resourceAction.getActionId());
				}
			}

			actionIdsLists.put(role.getName(), actionIdsList);
		}

		Assert.assertEquals(
			actionIdsLists,
			_roleLocalService.getResourceRoles(
				companyId, name, scope, primKey));
	}

	@Test
	public void testGetTeamRoleMapWithExclusion() throws Exception {
		createOrganizationAndTeam();

		Map<Team, Role> teamRoleMap = _roleLocalService.getTeamRoleMap(
			_organization.getGroupId());

		Role role = teamRoleMap.get(_team);

		Assert.assertNotNull(role);

		List<Role> roles = _roleLocalService.getTeamRoles(
			_organization.getGroupId(), new long[] {role.getRoleId()});

		Assert.assertNotNull(roles);
		Assert.assertTrue(roles.toString(), roles.isEmpty());
	}

	@Test(expected = NoSuchGroupException.class)
	public void testGetTeamRoleMapWithInvalidGroupId() throws Exception {
		_roleLocalService.getTeamRoleMap(0L);
	}

	@Test
	public void testGetTeamRoleMapWithOtherGroupId() throws Exception {
		createOrganizationAndTeam();

		User user = TestPropsValues.getUser();

		Organization organization = null;

		try {
			organization = _organizationLocalService.addOrganization(
				user.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				RandomTestUtil.randomString(), false);

			assertGetTeamRoleMap(
				_roleLocalService.getTeamRoleMap(_organization.getGroupId()),
				_teamLocalService.addTeam(
					user.getUserId(), organization.getGroupId(),
					RandomTestUtil.randomString(), null, new ServiceContext()),
				false);
		}
		finally {
			if (organization != null) {
				_organizationLocalService.deleteOrganization(organization);
			}
		}
	}

	@Test
	public void testGetTeamRoleMapWithOwnGroupId() throws Exception {
		createOrganizationAndTeam();

		assertGetTeamRoleMap(
			_roleLocalService.getTeamRoleMap(_organization.getGroupId()), _team,
			true);
	}

	@Test
	public void testGetTeamRoleMapWithParentGroupId() throws Exception {
		createOrganizationAndTeam();

		Group group = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), _organization.getGroupId(),
			LayoutTestUtil.addTypePortletLayout(_organization.getGroupId()));

		assertGetTeamRoleMap(
			_roleLocalService.getTeamRoleMap(group.getGroupId()), _team, true);
	}

	@Test
	public void testGetUserRelatedRoles() throws Exception {
		User user = UserTestUtil.addUser();

		long userId = user.getUserId();

		// See LPS-113146 for the magic number 2100

		long[] groupIds = new long[2100];

		for (int i = 0; i < groupIds.length; i++) {
			groupIds[i] = RandomTestUtil.nextLong();
		}

		_roleLocalService.getUserRelatedRoles(userId, groupIds);

		Role role1 = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_roleLocalService.addUserRole(userId, role1.getRoleId());

		Role role2 = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		Group group = GroupTestUtil.addGroup();

		long groupId = group.getGroupId();

		_roleLocalService.addGroupRole(groupId, role2.getRoleId());

		List<Role> userRelatedRoles = _roleLocalService.getUserRelatedRoles(
			userId, new long[0]);

		Assert.assertTrue(userRelatedRoles.contains(role1));
		Assert.assertFalse(userRelatedRoles.contains(role2));

		userRelatedRoles = _roleLocalService.getUserRelatedRoles(
			userId, new long[] {groupId});

		Assert.assertTrue(userRelatedRoles.contains(role1));
		Assert.assertTrue(userRelatedRoles.contains(role2));

		userRelatedRoles = _roleLocalService.getUserRelatedRoles(
			userId, new long[] {RandomTestUtil.nextLong()});

		Assert.assertTrue(userRelatedRoles.contains(role1));
		Assert.assertFalse(userRelatedRoles.contains(role2));
	}

	@Test
	public void testGetUserTeamRoles() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		Team team = _teamLocalService.addTeam(
			_user.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new ServiceContext());

		List<Role> roles = _roleLocalService.getUserTeamRoles(
			_user.getUserId(), _group.getGroupId());

		Assert.assertEquals(roles.toString(), 0, roles.size());

		_teamLocalService.addUserTeam(_user.getUserId(), team.getTeamId());

		roles = _roleLocalService.getUserTeamRoles(
			_user.getUserId(), _group.getGroupId());

		Role teamRole = team.getRole();

		Assert.assertEquals(roles.toString(), 1, roles.size());
		Assert.assertEquals(teamRole, roles.get(0));

		_teamLocalService.deleteUserTeam(_user.getUserId(), team.getTeamId());

		_userGroup = UserGroupTestUtil.addUserGroup(_group.getGroupId());

		_userLocalService.addUserGroupUser(
			_userGroup.getUserGroupId(), _user.getUserId());

		_teamLocalService.addUserGroupTeam(
			_userGroup.getUserGroupId(), team.getTeamId());

		roles = _roleLocalService.getUserTeamRoles(
			_user.getUserId(), _group.getGroupId());

		Assert.assertEquals(roles.toString(), 1, roles.size());
		Assert.assertEquals(teamRole, roles.get(0));

		_teamLocalService.addUserTeam(_user.getUserId(), team.getTeamId());

		roles = _roleLocalService.getUserTeamRoles(
			_user.getUserId(), _group.getGroupId());

		Assert.assertEquals(roles.toString(), 1, roles.size());
		Assert.assertEquals(teamRole, roles.get(0));
	}

	@Test
	@TestInfo("LPS-159272")
	public void testLoggingAuditMessageProcessorConfigurationEnabled()
		throws Exception {

		PrintStream printStream = System.out;

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.portal.security.audit.router.configuration." +
						"LoggingAuditMessageProcessorConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"outputToConsole", true
					).build())) {

			_testLoggingAuditMessageProcessorConfigurationEnabled();
		}
		finally {
			System.setOut(printStream);
		}
	}

	@Test
	public void testUpdateRoleWithLazyReferencingEnabled() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Role role = _roleLocalService.getOrAddEmptyRole(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), Role.class.getName(), 0,
				RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, role.getStatus());

			role = _roleLocalService.updateRole(
				role.getExternalReferenceCode(), role.getRoleId(),
				role.getName(), role.getTitleMap(), role.getDescriptionMap(),
				role.getSubtype(), null);

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, role.getStatus());
		}
	}

	protected void assertGetTeamRoleMap(
		Map<Team, Role> teamRoleMap, Team team, boolean hasTeam) {

		Assert.assertNotNull(teamRoleMap);
		Assert.assertFalse(teamRoleMap.toString(), teamRoleMap.isEmpty());

		if (hasTeam) {
			Assert.assertTrue(teamRoleMap.containsKey(team));

			Role role = teamRoleMap.get(team);

			Assert.assertEquals(role.getType(), RoleConstants.TYPE_PROVIDER);
		}
		else {
			Assert.assertFalse(teamRoleMap.containsKey(team));
		}
	}

	protected void createOrganizationAndTeam() throws Exception {
		User user = TestPropsValues.getUser();

		_organization = _organizationLocalService.addOrganization(
			user.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		_team = _teamLocalService.addTeam(
			user.getUserId(), _organization.getGroupId(),
			RandomTestUtil.randomString(), null, new ServiceContext());
	}

	private void _testLoggingAuditMessageProcessorConfigurationEnabled()
		throws Exception {

		// Group assign/unassign

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		System.setOut(new PrintStream(byteArrayOutputStream));

		Group group = GroupTestUtil.addGroup();

		group.setSite(true);

		group = _groupLocalService.updateGroup(group);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(group.getGroupId(), role);

		String content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"ASSIGN\""));
		Assert.assertTrue(
			content.contains("\"groupName\":\"" + group.getGroupKey() + "\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));

		byteArrayOutputStream.reset();

		_roleLocalService.deleteGroupRole(group.getGroupId(), role);

		content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"UNASSIGN\""));
		Assert.assertTrue(
			content.contains("\"groupName\":\"" + group.getGroupKey() + "\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));

		byteArrayOutputStream.reset();

		// Organization assign/unassign

		Organization organization = OrganizationTestUtil.addOrganization();

		_roleLocalService.addGroupRole(organization.getGroupId(), role);

		content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"ASSIGN\""));
		Assert.assertTrue(
			content.contains(
				"\"organizationName\":\"" + organization.getName() + "\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));

		byteArrayOutputStream.reset();

		_roleLocalService.deleteGroupRole(organization.getGroupId(), role);

		content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"UNASSIGN\""));
		Assert.assertTrue(
			content.contains(
				"\"organizationName\":\"" + organization.getName() + "\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));

		byteArrayOutputStream.reset();

		// User assign/unassign

		User user = TestPropsValues.getUser();

		_roleLocalService.addUserRole(user.getUserId(), role);

		content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"ASSIGN\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));
		Assert.assertTrue(
			content.contains(
				"\"userEmailAddress\":\"" + user.getEmailAddress() + "\""));

		byteArrayOutputStream.reset();

		_roleLocalService.deleteUserRole(user.getUserId(), role);

		content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"UNASSIGN\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));
		Assert.assertTrue(
			content.contains(
				"\"userEmailAddress\":\"" + user.getEmailAddress() + "\""));

		byteArrayOutputStream.reset();

		// User group assign/unassign

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_roleLocalService.addGroupRole(userGroup.getGroupId(), role);

		content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"ASSIGN\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));
		Assert.assertTrue(
			content.contains(
				"\"userGroupName\":\"" + userGroup.getName() + "\""));

		byteArrayOutputStream.reset();

		_roleLocalService.deleteGroupRole(userGroup.getGroupId(), role);

		content = byteArrayOutputStream.toString();

		Assert.assertTrue(content.contains("\"UNASSIGN\""));
		Assert.assertTrue(
			content.contains("\"roleName\":\"" + role.getName() + "\""));
		Assert.assertTrue(
			content.contains(
				"\"userGroupName\":\"" + userGroup.getName() + "\""));
	}

	private static ResourceAction _arbitraryResourceAction;
	private static Role _arbitraryRole;

	@Inject
	private static GroupLocalService _groupLocalService;

	@Inject
	private static OrganizationLocalService _organizationLocalService;

	@Inject
	private static ResourceActionLocalService _resourceActionLocalService;

	private static ResourcePermission _resourcePermission;

	@Inject
	private static ResourcePermissionLocalService
		_resourcePermissionLocalService;

	@Inject
	private static RoleLocalService _roleLocalService;

	@Inject
	private static TeamLocalService _teamLocalService;

	@Inject
	private static UserGroupGroupRoleLocalService
		_userGroupGroupRoleLocalService;

	@Inject
	private static UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private static UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Organization _organization;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	private Team _team;

	@DeleteAfterTestRun
	private User _user;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

}