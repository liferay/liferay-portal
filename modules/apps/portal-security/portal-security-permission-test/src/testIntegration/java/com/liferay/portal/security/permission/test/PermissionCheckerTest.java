/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.PortletPersistence;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 * @author Tomas Polesovsky
 */
@RunWith(Arquillian.class)
public class PermissionCheckerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Package pkg = PermissionCheckerTest.class.getPackage();

		String packageName = pkg.getName();

		_source =
			StringUtil.replace(packageName, '.', '/') +
				"/dependencies/resource-actions.xml";

		_resourceActions.populateModelResources(
			PermissionCheckerTest.class.getClassLoader(), _source);
	}

	@AfterClass
	public static void tearDownClass() {
		List<ResourceAction> portletResourceActions =
			_resourceActionLocalService.getResourceActions(
				_PORTLET_RESOURCE_NAME);

		for (ResourceAction portletResourceAction : portletResourceActions) {
			_resourceActionLocalService.deleteResourceAction(
				portletResourceAction);
		}

		List<String> modelNames = _resourceActions.getPortletModelResources(
			_PORTLET_RESOURCE_NAME);

		for (String modelName : modelNames) {
			List<ResourceAction> modelResourceActions =
				_resourceActionLocalService.getResourceActions(modelName);

			for (ResourceAction modelResourceAction : modelResourceActions) {
				_resourceActionLocalService.deleteResourceAction(
					modelResourceAction);
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAreStagingAndLiveTeamRolePermissionsIndependent()
		throws Exception {

		_user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(_group.getGroupId(), _user.getUserId());

		Team team = TeamLocalServiceUtil.addTeam(
			_user.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		TeamLocalServiceUtil.addUserTeam(_user.getUserId(), team.getTeamId());

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.addFolder(
			null, _user.getUserId(), _group.getGroupId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		_resourceLocalService.addResources(
			_user.getCompanyId(), _group.getGroupId(), 0,
			JournalFolder.class.getName(), journalFolder.getFolderId(), false,
			false, false);

		try {
			Role teamRole = team.getRole();

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), JournalFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalFolder.getFolderId()),
				teamRole.getRoleId(), new String[] {ActionKeys.ADD_SUBFOLDER});

			try {
				Assert.assertFalse(_group.isStaged());

				GroupTestUtil.enableLocalStaging(_group, _user.getUserId());

				Assert.assertTrue(_group.isStaged());

				Group stagingGroup = _group.getStagingGroup();

				Team stagingTeam = TeamLocalServiceUtil.getTeam(
					stagingGroup.getGroupId(), team.getName());

				Role stagingTeamRole = stagingTeam.getRole();

				JournalFolder stagingFolder =
					JournalFolderLocalServiceUtil.
						getJournalFolderByUuidAndGroupId(
							journalFolder.getUuid(), stagingGroup.getGroupId());

				_resourcePermissionLocalService.removeResourcePermission(
					_user.getCompanyId(), JournalFolder.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(stagingFolder.getFolderId()),
					stagingTeamRole.getRoleId(), ActionKeys.ADD_SUBFOLDER);

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(_user);

				boolean hasPermission = permissionChecker.hasPermission(
					stagingGroup.getGroupId(), JournalFolder.class.getName(),
					stagingFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

				Assert.assertFalse(hasPermission);

				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), JournalFolder.class.getName(),
					journalFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

				Assert.assertTrue(hasPermission);

				_resourcePermissionLocalService.setResourcePermissions(
					_user.getCompanyId(), JournalFolder.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(stagingFolder.getFolderId()),
					stagingTeamRole.getRoleId(),
					new String[] {ActionKeys.ADD_SUBFOLDER});

				_resourcePermissionLocalService.removeResourcePermission(
					_user.getCompanyId(), JournalFolder.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(journalFolder.getFolderId()),
					teamRole.getRoleId(), ActionKeys.ADD_SUBFOLDER);

				hasPermission = permissionChecker.hasPermission(
					stagingGroup.getGroupId(), JournalFolder.class.getName(),
					stagingFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

				Assert.assertTrue(hasPermission);

				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), JournalFolder.class.getName(),
					journalFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

				Assert.assertFalse(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), JournalFolder.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					journalFolder.getFolderId());
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), JournalFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				journalFolder.getFolderId());
		}
	}

	@Test
	public void testGetGuestUserRoleIdsDoesNotIncludeGuestGroupRole()
		throws Exception {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_groupLocalService.addRoleGroup(_role.getRoleId(), guestGroup);

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		Assert.assertArrayEquals(
			new long[] {guestRole.getRoleId()},
			permissionChecker.getGuestUserRoleIds());
	}

	@Test
	public void testGetRoleIds() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		permissionChecker.getRoleIds(
			_user.getUserId(), GroupConstants.DEFAULT_LIVE_GROUP_ID);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(_role.getRoleId(), _user);

		Assert.assertTrue(
			ArrayUtil.contains(
				permissionChecker.getRoleIds(
					_user.getUserId(), GroupConstants.DEFAULT_LIVE_GROUP_ID),
				_role.getRoleId()));

		_userLocalService.deleteRoleUser(_role.getRoleId(), _user);

		Assert.assertFalse(
			ArrayUtil.contains(
				permissionChecker.getRoleIds(
					_user.getUserId(), GroupConstants.DEFAULT_LIVE_GROUP_ID),
				_role.getRoleId()));
	}

	@Test
	public void testHasPermissionOnDefaultPortletResourcesWhenPortletDeploys()
		throws Exception {

		_user = UserTestUtil.addUser();

		_userLocalService.setGroupUsers(
			_group.getGroupId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		_deployRemotePortlet(_user.getCompanyId(), _PORTLET_RESOURCE_NAME);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _PORTLET_RESOURCE_NAME,
				_PORTLET_RESOURCE_NAME, ActionKeys.VIEW);

			Assert.assertTrue(hasPermission);

			hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _PORTLET_RESOURCE_NAME,
				_PORTLET_RESOURCE_NAME, ActionKeys.CONFIGURATION);

			Assert.assertTrue(hasPermission);

			hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _PORTLET_RESOURCE_NAME,
				_PORTLET_RESOURCE_NAME, ActionKeys.ACCESS_IN_CONTROL_PANEL);

			Assert.assertFalse(hasPermission);

			hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _ROOT_MODEL_RESOURCE_NAME,
				_ROOT_MODEL_RESOURCE_NAME, _ADD_SITE_TEST_1_ACTION);

			Assert.assertTrue(hasPermission);
		}
		finally {
			_destroyRemotePortlet(_user.getCompanyId(), _PORTLET_RESOURCE_NAME);
		}
	}

	@Test
	public void testHasPermissionOnDefaultPortletResourcesWithNonsitePortlet()
		throws Exception {

		_user = UserTestUtil.addUser();

		_userLocalService.setGroupUsers(
			_group.getGroupId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		_deployRemotePortlet(
			_user.getCompanyId(), _NONSITE_PORTLET_RESOURCE_NAME);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				0, _NONSITE_PORTLET_RESOURCE_NAME,
				_NONSITE_PORTLET_RESOURCE_NAME, _ADD_TEST_RESULT_ACTION);

			Assert.assertTrue(hasPermission);

			hasPermission = permissionChecker.hasPermission(
				0, _NONSITE_ROOT_MODEL_RESOURCE_NAME,
				_NONSITE_ROOT_MODEL_RESOURCE_NAME, _ADD_TEST_ACTION);

			Assert.assertFalse(hasPermission);

			_role = RoleTestUtil.addRole(
				RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

			_userLocalService.setRoleUsers(
				_role.getRoleId(), new long[] {_user.getUserId()});

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), _NONSITE_ROOT_MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_user.getCompanyId()), _role.getRoleId(),
				new String[] {_ADD_TEST_ACTION});

			permissionChecker = _permissionCheckerFactory.create(_user);

			try {
				hasPermission = permissionChecker.hasPermission(
					0, _NONSITE_ROOT_MODEL_RESOURCE_NAME,
					_NONSITE_ROOT_MODEL_RESOURCE_NAME, _ADD_TEST_ACTION);

				Assert.assertTrue(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), _NONSITE_ROOT_MODEL_RESOURCE_NAME,
					ResourceConstants.SCOPE_COMPANY, _user.getCompanyId());
			}
		}
		finally {
			_destroyRemotePortlet(
				_user.getCompanyId(), _NONSITE_PORTLET_RESOURCE_NAME);
		}
	}

	@Test
	public void testHasPermissionOnRootModelResource() throws Exception {
		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_SITE);

		_userLocalService.setRoleUsers(
			_role.getRoleId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		_resourceLocalService.addResources(
			permissionChecker.getCompanyId(), _group.getGroupId(), 0,
			_ROOT_MODEL_RESOURCE_NAME, _group.getGroupId(), false, true, false);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _ROOT_MODEL_RESOURCE_NAME,
				_group.getGroupId(), _ADD_SITE_TEST_1_ACTION);

			Assert.assertFalse(hasPermission);

			_userLocalService.setGroupUsers(
				_group.getGroupId(), new long[] {_user.getUserId()});

			permissionChecker = _permissionCheckerFactory.create(_user);

			hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _ROOT_MODEL_RESOURCE_NAME,
				_group.getGroupId(), _ADD_SITE_TEST_1_ACTION);

			Assert.assertTrue(hasPermission);

			hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _ROOT_MODEL_RESOURCE_NAME,
				_group.getGroupId(), _ADD_SITE_TEST_2_ACTION);

			Assert.assertFalse(hasPermission);

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), _ROOT_MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_group.getGroupId()), _role.getRoleId(),
				new String[] {_ADD_SITE_TEST_2_ACTION});

			try {
				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), _ROOT_MODEL_RESOURCE_NAME,
					_group.getGroupId(), _ADD_SITE_TEST_2_ACTION);

				Assert.assertTrue(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), _ROOT_MODEL_RESOURCE_NAME,
					ResourceConstants.SCOPE_INDIVIDUAL, _group.getGroupId());
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), _ROOT_MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, _group.getGroupId());
		}
	}

	@Test
	public void testHasPermissionWithCompanyScopeResourcePermission()
		throws Exception {

		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_userLocalService.setRoleUsers(
			_role.getRoleId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		long resourceId = 12345;

		_resourceLocalService.addResources(
			_user.getCompanyId(), 0, 0, _MODEL_RESOURCE_NAME, resourceId, false,
			false, false);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
				ActionKeys.DELETE);

			Assert.assertFalse(hasPermission);

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_user.getCompanyId()), _role.getRoleId(),
				new String[] {ActionKeys.DELETE});

			try {
				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
					ActionKeys.DELETE);

				Assert.assertTrue(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), _MODEL_RESOURCE_NAME,
					ResourceConstants.SCOPE_COMPANY, resourceId);
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, resourceId);
		}
	}

	@Test
	public void testHasPermissionWithDifferentCompanyAdmin() throws Exception {
		long resourceId = 12345;

		_resourceLocalService.addResources(
			_group.getCompanyId(), _group.getGroupId(), 0, _MODEL_RESOURCE_NAME,
			resourceId, false, false, false);

		_company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_user = UserTestUtil.addCompanyAdminUser(_company);

			PermissionChecker permissionChecker =
				_permissionCheckerFactory.create(_user);

			boolean companyAdmin = permissionChecker.isCompanyAdmin(
				_company.getCompanyId());

			Assert.assertTrue(companyAdmin);

			permissionChecker.hasPermission(
				0, _MODEL_RESOURCE_NAME, resourceId, ActionKeys.VIEW);

			Assert.fail();
		}
		catch (Throwable throwable) {
			boolean found = false;

			Throwable causeThrowable = throwable;

			while (!found && (causeThrowable != null)) {
				if (causeThrowable instanceof
						NoSuchResourcePermissionException) {

					found = true;
				}

				causeThrowable = causeThrowable.getCause();
			}

			if (!found) {
				throw throwable;
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_group.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, resourceId);
		}
	}

	@Test
	public void testHasPermissionWithGroupScopeResourcePermission()
		throws Exception {

		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_userLocalService.setRoleUsers(
			_role.getRoleId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		long resourceId = 12345;

		_resourceLocalService.addResources(
			_user.getCompanyId(), _group.getGroupId(), 0, _MODEL_RESOURCE_NAME,
			resourceId, false, false, false);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
				ActionKeys.DELETE);

			Assert.assertFalse(hasPermission);

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_group.getGroupId()), _role.getRoleId(),
				new String[] {ActionKeys.DELETE});

			try {
				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
					ActionKeys.DELETE);

				Assert.assertTrue(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), _MODEL_RESOURCE_NAME,
					ResourceConstants.SCOPE_GROUP, _group.getGroupId());
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, resourceId);
		}
	}

	@Test
	public void testHasPermissionWithGroupTemplateScopeResourcePermission()
		throws Exception {

		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_userLocalService.setRoleUsers(
			_role.getRoleId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		long resourceId = 12345;

		_resourceLocalService.addResources(
			_user.getCompanyId(), _group.getGroupId(), 0, _MODEL_RESOURCE_NAME,
			resourceId, false, false, false);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
				ActionKeys.DELETE);

			Assert.assertFalse(hasPermission);

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", _role.getRoleId(),
				new String[] {ActionKeys.DELETE});

			try {
				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
					ActionKeys.DELETE);

				Assert.assertTrue(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), _MODEL_RESOURCE_NAME,
					ResourceConstants.SCOPE_GROUP_TEMPLATE, 0);
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, resourceId);
		}
	}

	@Test
	public void testHasPermissionWithIndividualScopeResourcePermission()
		throws Exception {

		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_userLocalService.setRoleUsers(
			_role.getRoleId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		long resourceId = 12345;

		_resourceLocalService.addResources(
			_user.getCompanyId(), _group.getGroupId(), 0, _MODEL_RESOURCE_NAME,
			resourceId, false, false, false);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
				ActionKeys.DELETE);

			Assert.assertFalse(hasPermission);

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(resourceId),
				_role.getRoleId(), new String[] {ActionKeys.DELETE});

			try {
				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), _MODEL_RESOURCE_NAME, resourceId,
					ActionKeys.DELETE);

				Assert.assertTrue(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), _MODEL_RESOURCE_NAME,
					ResourceConstants.SCOPE_INDIVIDUAL, resourceId);
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), _MODEL_RESOURCE_NAME,
				ResourceConstants.SCOPE_INDIVIDUAL, resourceId);
		}
	}

	@Test
	public void testHasPermissionWithLiveGroup() throws Exception {
		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.setRoleUsers(
			_role.getRoleId(), new long[] {_user.getUserId()});

		_testHasPermissionWithLiveGroup();
		_testHasPermissionWithLiveGroupMissing();
	}

	@Test
	public void testHasPermissionWithMissingResourcePermissions()
		throws Exception {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		try {
			permissionChecker.hasPermission(
				0, _MODEL_RESOURCE_NAME, 12345, ActionKeys.VIEW);

			Assert.fail();
		}
		catch (Throwable throwable) {
			boolean found = false;

			Throwable causeThrowable = throwable;

			while (!found && (causeThrowable != null)) {
				if (causeThrowable instanceof
						NoSuchResourcePermissionException) {

					found = true;
				}

				causeThrowable = causeThrowable.getCause();
			}

			if (!found) {
				throw throwable;
			}
		}
	}

	@Test
	public void testHasTeamPermissionWithStagingEnabled() throws Exception {
		_user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(_group.getGroupId(), _user.getUserId());

		Team team = TeamLocalServiceUtil.addTeam(
			_user.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		TeamLocalServiceUtil.addUserTeam(_user.getUserId(), team.getTeamId());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		JournalFolder journalFolder = JournalFolderLocalServiceUtil.addFolder(
			null, _user.getUserId(), _group.getGroupId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		_resourceLocalService.addResources(
			_user.getCompanyId(), _group.getGroupId(), 0,
			JournalFolder.class.getName(), journalFolder.getFolderId(), false,
			false, false);

		try {
			boolean hasPermission = permissionChecker.hasPermission(
				_group.getGroupId(), JournalFolder.class.getName(),
				journalFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

			Assert.assertFalse(hasPermission);

			Role teamRole = team.getRole();

			_resourcePermissionLocalService.setResourcePermissions(
				_user.getCompanyId(), JournalFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalFolder.getFolderId()),
				teamRole.getRoleId(), new String[] {ActionKeys.ADD_SUBFOLDER});

			try {
				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), JournalFolder.class.getName(),
					journalFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

				Assert.assertTrue(hasPermission);

				Assert.assertFalse(_group.isStaged());

				GroupTestUtil.enableLocalStaging(_group, _user.getUserId());

				Assert.assertTrue(_group.isStaged());

				permissionChecker = _permissionCheckerFactory.create(_user);

				hasPermission = permissionChecker.hasPermission(
					_group.getGroupId(), JournalFolder.class.getName(),
					journalFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

				Assert.assertTrue(hasPermission);

				Group stagingGroup = _group.getStagingGroup();

				JournalFolder stagingFolder =
					JournalFolderLocalServiceUtil.
						getJournalFolderByUuidAndGroupId(
							journalFolder.getUuid(), stagingGroup.getGroupId());

				hasPermission = permissionChecker.hasPermission(
					stagingGroup.getGroupId(), JournalFolder.class.getName(),
					stagingFolder.getFolderId(), ActionKeys.ADD_SUBFOLDER);

				Assert.assertTrue(hasPermission);
			}
			finally {
				_resourcePermissionLocalService.deleteResourcePermissions(
					_user.getCompanyId(), JournalFolder.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					journalFolder.getFolderId());
			}
		}
		finally {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), JournalFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				journalFolder.getFolderId());
		}
	}

	@Test
	public void testIsCompanyAdminWithCompanyAdmin() throws Exception {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(permissionChecker.isCompanyAdmin());
	}

	@Test
	public void testIsCompanyAdminWithRegularUser() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(permissionChecker.isCompanyAdmin());
	}

	@Test
	public void testIsContentReviewerWithCompanyAdminUser() throws Exception {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(
			permissionChecker.isContentReviewer(
				TestPropsValues.getCompanyId(), _group.getGroupId()));
	}

	@Test
	public void testIsContentReviewerWithReviewerUser() throws Exception {
		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(
			RoleConstants.PORTAL_CONTENT_REVIEWER, RoleConstants.TYPE_REGULAR);

		_userLocalService.setRoleUsers(
			_role.getRoleId(), new long[] {_user.getUserId()});

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(
			permissionChecker.isContentReviewer(
				_user.getCompanyId(), _group.getGroupId()));
	}

	@Test
	public void testIsContentReviewerWithSiteContentReviewer()
		throws Exception {

		_role = RoleTestUtil.addRole(
			RoleConstants.SITE_CONTENT_REVIEWER, RoleConstants.TYPE_SITE);

		_user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_CONTENT_REVIEWER);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(
			permissionChecker.isContentReviewer(
				_user.getCompanyId(), _group.getGroupId()));
	}

	@Test
	public void testIsGroupAdminForSubgroupWithManageSubgroupsPermission()
		throws Exception {

		Group parentGroup = GroupTestUtil.addGroup();

		Group subgroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		_groups.add(subgroup);

		_groups.add(parentGroup);

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_SITE);

		_user = UserTestUtil.addGroupUser(parentGroup, _role.getName());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(
			permissionChecker.isGroupAdmin(subgroup.getGroupId()));

		_resourcePermissionLocalService.addResourcePermission(
			_user.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(parentGroup.getGroupId()), _role.getRoleId(),
			ActionKeys.MANAGE_SUBGROUPS);

		Assert.assertTrue(
			permissionChecker.isGroupAdmin(subgroup.getGroupId()));
	}

	@Test
	public void testIsGroupAdminWithCompanyAdmin() throws Exception {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(permissionChecker.isGroupAdmin(_group.getGroupId()));
	}

	@Test
	public void testIsGroupAdminWithDifferentCompanyAdmin() throws Exception {
		_company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_user = UserTestUtil.addCompanyAdminUser(_company);

			PermissionChecker permissionChecker =
				_permissionCheckerFactory.create(_user);

			Company testCompany = _companyLocalService.getCompany(
				TestPropsValues.getCompanyId());

			Assert.assertFalse(
				permissionChecker.isGroupAdmin(testCompany.getGroupId()));
		}
		catch (Throwable throwable) {
			boolean found = false;

			Throwable causeThrowable = throwable;

			while (!found && (causeThrowable != null)) {
				if (causeThrowable instanceof
						NoSuchResourcePermissionException) {

					found = true;
				}

				causeThrowable = causeThrowable.getCause();
			}

			if (!found) {
				throw throwable;
			}
		}
	}

	@Test
	public void testIsGroupAdminWithGroupAdmin() throws Exception {
		_user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(permissionChecker.isGroupAdmin(_group.getGroupId()));
	}

	@Test
	public void testIsGroupAdminWithRegularUser() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(permissionChecker.isGroupAdmin(_group.getGroupId()));
	}

	@Test
	public void testIsGroupMemberWithGroupMember() throws Exception {
		_user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(_group.getGroupId(), _user.getUserId());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(permissionChecker.isGroupMember(_group.getGroupId()));
	}

	@Test
	public void testIsGroupMemberWithNongroupMember() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(
			permissionChecker.isGroupMember(_group.getGroupId()));
	}

	@Test
	public void testIsGroupOwnerWithCompanyAdmin() throws Exception {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(permissionChecker.isGroupOwner(_group.getGroupId()));
	}

	@Test
	public void testIsGroupOwnerWithGroupAdmin() throws Exception {
		_user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(permissionChecker.isGroupOwner(_group.getGroupId()));
	}

	@Test
	public void testIsGroupOwnerWithOwnerUser() throws Exception {
		_user = UserTestUtil.addGroupOwnerUser(_group);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(permissionChecker.isGroupOwner(_group.getGroupId()));
	}

	@Test
	public void testIsGroupOwnerWithRegularUser() throws Exception {
		_user = UserTestUtil.addUser(
			_group.getGroupId(), LocaleUtil.getDefault());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(permissionChecker.isGroupOwner(_group.getGroupId()));
	}

	@Test
	public void testIsOmniadminWithAdministratorRoleUser() throws Exception {
		_user = UserTestUtil.addOmniadminUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(permissionChecker.isOmniadmin());
	}

	@Test
	public void testIsOmniadminWithCompanyAdmin() throws Exception {
		_company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_user = UserTestUtil.addCompanyAdminUser(_company);

			PermissionChecker permissionChecker =
				_permissionCheckerFactory.create(_user);

			Assert.assertFalse(permissionChecker.isOmniadmin());
		}
	}

	@Test
	public void testIsOmniadminWithGroupAdmin() throws Exception {
		_user = UserTestUtil.addGroupAdminUser(_group);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(permissionChecker.isOmniadmin());
	}

	@Test
	public void testIsOmniadminWithRegularUser() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(permissionChecker.isOmniadmin());
	}

	@Test
	public void testIsOrganizationAdminWithCompanyAdmin() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(
			permissionChecker.isOrganizationAdmin(
				_organization.getOrganizationId()));
	}

	@Test
	public void testIsOrganizationAdminWithGroupAdmin() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		_user = UserTestUtil.addGroupAdminUser(_organization.getGroup());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(
			permissionChecker.isOrganizationAdmin(
				_organization.getOrganizationId()));
	}

	@Test
	public void testIsOrganizationAdminWithOrganizationAdmin()
		throws Exception {

		_organization = OrganizationTestUtil.addOrganization();

		_user = UserTestUtil.addOrganizationAdminUser(_organization);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(
			permissionChecker.isOrganizationAdmin(
				_organization.getOrganizationId()));
	}

	@Test
	public void testIsOrganizationAdminWithRegularUser() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		_user = UserTestUtil.addUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(
			permissionChecker.isOrganizationAdmin(
				_organization.getOrganizationId()));
	}

	@Test
	public void testIsOrganizationOwnerWithCompanyAdmin() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(
			permissionChecker.isOrganizationOwner(
				_organization.getOrganizationId()));
	}

	@Test
	public void testIsOrganizationOwnerWithGroupAdmin() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		_user = UserTestUtil.addGroupAdminUser(_organization.getGroup());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(
			permissionChecker.isOrganizationOwner(
				_organization.getOrganizationId()));
	}

	@Test
	public void testIsOrganizationOwnerWithOrganizationAdmin()
		throws Exception {

		_organization = OrganizationTestUtil.addOrganization();

		_user = UserTestUtil.addOrganizationAdminUser(_organization);

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(
			permissionChecker.isOrganizationOwner(
				_organization.getOrganizationId()));
	}

	@Test
	public void testIsOrganizationOwnerWithRegularUser() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		_user = UserTestUtil.addUser();

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertFalse(
			permissionChecker.isOrganizationOwner(
				_organization.getOrganizationId()));
	}

	private void _deployRemotePortlet(long companyId, String portletName)
		throws Exception {

		Portlet portlet = _portletPersistence.create(0);

		portlet.setCompanyId(companyId);
		portlet.setPortletId(portletName);

		_resourceActions.populatePortletResource(
			portlet, PermissionCheckerTest.class.getClassLoader(), _source);

		_portletLocalService.deployRemotePortlet(portlet, "category.hidden");
	}

	private void _destroyRemotePortlet(long companyId, String portletName)
		throws PortalException {

		Portlet portlet = _portletLocalService.getPortletById(
			companyId, portletName);

		List<String> modelNames = _resourceActions.getPortletModelResources(
			portletName);

		for (String modelName : modelNames) {
			_resourceLocalService.deleteResource(
				_user.getCompanyId(), modelName,
				ResourceConstants.SCOPE_INDIVIDUAL, modelName);
		}

		_resourceLocalService.deleteResource(
			_user.getCompanyId(), portletName,
			ResourceConstants.SCOPE_INDIVIDUAL, portletName);

		_portletLocalService.destroyRemotePortlet(portlet);
	}

	private void _testHasPermissionWithLiveGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		Group liveGroup = GroupTestUtil.addGroup();

		_groups.add(liveGroup);

		_resourcePermissionLocalService.setResourcePermissions(
			_user.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(liveGroup.getGroupId()), _role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		group.setLiveGroupId(liveGroup.getGroupId());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(
			permissionChecker.hasPermission(
				group, Group.class.getName(),
				String.valueOf(group.getGroupId()), ActionKeys.UPDATE));
	}

	private void _testHasPermissionWithLiveGroupMissing() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		_resourcePermissionLocalService.setResourcePermissions(
			_user.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(group.getGroupId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		group.setLiveGroupId(RandomTestUtil.randomLong());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_user);

		Assert.assertTrue(
			permissionChecker.hasPermission(
				group, Group.class.getName(),
				String.valueOf(group.getGroupId()), ActionKeys.UPDATE));
	}

	private static final String _ADD_SITE_TEST_1_ACTION = "ADD_SITE_TEST_1";

	private static final String _ADD_SITE_TEST_2_ACTION = "ADD_SITE_TEST_2";

	private static final String _ADD_TEST_ACTION = "ADD_TEST";

	private static final String _ADD_TEST_RESULT_ACTION = "ADD_TEST_RESULT";

	private static final String _MODEL_RESOURCE_NAME =
		"test.com.liferay.portal.security.permission.SiteTest";

	private static final String _NONSITE_PORTLET_RESOURCE_NAME =
		"com_liferay_portal_security_PermissionCheckerTestNonsitePortlet";

	private static final String _NONSITE_ROOT_MODEL_RESOURCE_NAME =
		"com.liferay.portal.security.permission.nonsite";

	private static final String _PORTLET_RESOURCE_NAME =
		"com_liferay_portal_security_PermissionCheckerTestSitePortlet";

	private static final String _ROOT_MODEL_RESOURCE_NAME =
		"com.liferay.portal.security.permission.site";

	@Inject
	private static ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private static ResourceActions _resourceActions;

	private static String _source;

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@DeleteAfterTestRun
	private Organization _organization;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPersistence _portletPersistence;

	@Inject
	private ResourceLocalService _resourceLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}