/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class OrganizationServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@After
	public void tearDown() throws Exception {
		_organizationLocalService.clearGroupOrganizations(
			TestPropsValues.getGroupId());
	}

	@Test
	public void testAddGroupOrganizations() throws Exception {
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroups(
			user.getUserId(), new long[] {TestPropsValues.getGroupId()});

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_OWNER);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), TestPropsValues.getGroupId(),
			new long[] {role.getRoleId()});

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization1);

		_userLocalService.addOrganizationUsers(
			organization1.getOrganizationId(), new long[] {user.getUserId()});

		Organization organization2 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization2);

		_userLocalService.addOrganizationUsers(
			organization2.getOrganizationId(), new long[] {user.getUserId()});

		_organizationService.addGroupOrganizations(
			TestPropsValues.getGroupId(),
			new long[] {organization2.getOrganizationId()});

		Organization organization3 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization3);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_organizationService.addGroupOrganizations(
				TestPropsValues.getGroupId(),
				new long[] {organization1.getOrganizationId()});

			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.addGroupOrganizations(
					TestPropsValues.getGroupId(),
					new long[] {organization3.getOrganizationId()}));
			Assert.assertEquals(
				2,
				_organizationLocalService.getGroupOrganizationsCount(
					TestPropsValues.getGroupId()));
			Assert.assertTrue(
				_organizationLocalService.hasGroupOrganization(
					TestPropsValues.getGroupId(),
					organization1.getOrganizationId()));
			Assert.assertTrue(
				_organizationLocalService.hasGroupOrganization(
					TestPropsValues.getGroupId(),
					organization2.getOrganizationId()));
		}
	}

	@Test
	public void testAddOrganizationUserByEmailAddress() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		User organizationAdminUser = UserTestUtil.addOrganizationAdminUser(
			organization);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), organizationAdminUser.getUserId());

		User user1 = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				organizationAdminUser)) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.addOrganizationUserByEmailAddress(
					user1.getEmailAddress(), organization.getOrganizationId(),
					serviceContext));
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organization.getOrganizationId(), user1.getUserId()));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _userService.getUserById(user1.getUserId()));

			User user2 = _organizationService.addOrganizationUserByEmailAddress(
				RandomTestUtil.randomString() + "@liferay.com",
				organization.getOrganizationId(), serviceContext);

			Assert.assertFalse(
				_userLocalService.hasGroupUser(
					group.getGroupId(), user2.getUserId()));
			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organization.getOrganizationId(), user2.getUserId()));
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_addUser())) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.addOrganizationUserByEmailAddress(
					user1.getEmailAddress(), organization.getOrganizationId(),
					serviceContext));
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organization.getOrganizationId(), user1.getUserId()));
			Assert.assertEquals(
				user1, _userService.getUserById(user1.getUserId()));
		}
	}

	@Test
	public void testAddUserOrganizationByEmailAddress() throws Exception {
		Organization organization1 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization1);

		User organizationAdminUser = UserTestUtil.addOrganizationAdminUser(
			organization1);

		User user1 = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				organizationAdminUser)) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.addUserOrganizationByEmailAddress(
					user1.getEmailAddress(),
					organization1.getOrganizationId()));
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organization1.getOrganizationId(), user1.getUserId()));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _userService.getUserById(user1.getUserId()));
		}

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_organizations.add(organization2);

		User user2 = UserTestUtil.addUser();

		_userLocalService.addOrganizationUser(
			organization2.getOrganizationId(), user2.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				organizationAdminUser)) {

			_organizationService.addUserOrganizationByEmailAddress(
				user2.getEmailAddress(), organization1.getOrganizationId());

			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organization1.getOrganizationId(), user2.getUserId()));
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_addUser())) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.addUserOrganizationByEmailAddress(
					user1.getEmailAddress(),
					organization1.getOrganizationId()));
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organization1.getOrganizationId(), user1.getUserId()));
			Assert.assertEquals(
				user1, _userService.getUserById(user1.getUserId()));
		}
	}

	@Test
	public void testDeleteUserOrganizationByEmailAddress() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		User organizationAdminUser1 = UserTestUtil.addOrganizationAdminUser(
			organization);
		User organizationAdminUser2 = UserTestUtil.addOrganizationAdminUser(
			organization);
		User organizationOwnerUser = UserTestUtil.addOrganizationOwnerUser(
			organization);
		User organizationUser = UserTestUtil.addOrganizationUser(
			organization, RoleConstants.ORGANIZATION_USER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				organizationAdminUser1)) {

			_organizationService.deleteUserOrganizationByEmailAddress(
				organizationAdminUser2.getEmailAddress(),
				organization.getOrganizationId());

			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organization.getOrganizationId(),
					organizationAdminUser2.getUserId()));

			_organizationService.deleteUserOrganizationByEmailAddress(
				organizationOwnerUser.getEmailAddress(),
				organization.getOrganizationId());

			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organization.getOrganizationId(),
					organizationOwnerUser.getUserId()));

			_organizationService.deleteUserOrganizationByEmailAddress(
				organizationUser.getEmailAddress(),
				organization.getOrganizationId());

			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organization.getOrganizationId(),
					organizationUser.getUserId()));
		}
	}

	@Test
	public void testGetGtOrganizations() throws Exception {
		for (int i = 0; i < 10; i++) {
			_organizations.add(OrganizationTestUtil.addOrganization());
		}

		long parentOrganizationId = 0;
		int size = 5;

		List<Organization> organizations =
			_organizationService.getGtOrganizations(
				0, TestPropsValues.getCompanyId(), parentOrganizationId, size);

		Assert.assertEquals(
			organizations.toString(), size, organizations.size());

		Organization lastOrganization = organizations.get(
			organizations.size() - 1);

		organizations = _organizationService.getGtOrganizations(
			lastOrganization.getOrganizationId(),
			TestPropsValues.getCompanyId(), parentOrganizationId, size);

		Assert.assertEquals(
			organizations.toString(), size, organizations.size());

		long previousOrganizationId = 0;

		for (Organization organization : organizations) {
			long organizationId = organization.getOrganizationId();

			Assert.assertTrue(
				organizationId > lastOrganization.getOrganizationId());
			Assert.assertTrue(organizationId > previousOrganizationId);

			previousOrganizationId = organizationId;
		}
	}

	@Test
	public void testGetOrAddEmptyOrganization() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			RoleTestUtil.addResourcePermission(
				role, PortletKeys.PORTAL, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				ActionKeys.ADD_ORGANIZATION);

			User user = UserTestUtil.addUser();

			_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user)) {

				Organization organization =
					_organizationService.getOrAddEmptyOrganization(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString());

				Assert.assertNotNull(organization);
			}

			// Without permissions

			user = UserTestUtil.addUser();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user)) {

				_organizationService.getOrAddEmptyOrganization(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}
		}
	}

	@Test
	public void testGetOrganizationsLikeName() throws Exception {
		List<Organization> allChildOrganizations = new ArrayList<>();
		Organization parentOrganization =
			OrganizationTestUtil.addOrganization();

		List<Organization> allOrganizations = new ArrayList<>(
			_organizationLocalService.getOrganizations(
				TestPropsValues.getCompanyId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID));

		try {
			String name = RandomTestUtil.randomString(10);

			long parentOrganizationId = parentOrganization.getOrganizationId();

			List<Organization> likeNameChildOrganizations = new ArrayList<>();

			for (int i = 0; i < 10; i++) {
				Organization organization =
					OrganizationTestUtil.addOrganization(
						parentOrganizationId, name + i, false);

				likeNameChildOrganizations.add(organization);
			}

			allChildOrganizations.addAll(likeNameChildOrganizations);
			allChildOrganizations.add(
				OrganizationTestUtil.addOrganization(
					parentOrganizationId, RandomTestUtil.randomString(10),
					false));
			allChildOrganizations.add(
				OrganizationTestUtil.addOrganization(
					parentOrganizationId, RandomTestUtil.randomString(10),
					false));
			allChildOrganizations.add(
				OrganizationTestUtil.addOrganization(
					parentOrganizationId, RandomTestUtil.randomString(10),
					false));

			allOrganizations.addAll(allChildOrganizations);

			_assertExpectedOrganizations(
				likeNameChildOrganizations, parentOrganizationId, name + "%");
			_assertExpectedOrganizations(
				likeNameChildOrganizations, parentOrganizationId,
				StringUtil.toLowerCase(name) + "%");
			_assertExpectedOrganizations(
				likeNameChildOrganizations, parentOrganizationId,
				StringUtil.toUpperCase(name) + "%");
			_assertExpectedOrganizations(
				likeNameChildOrganizations,
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				StringUtil.toUpperCase(name) + "%");
			_assertExpectedOrganizations(
				allChildOrganizations, parentOrganizationId, null);
			_assertExpectedOrganizations(
				allChildOrganizations, parentOrganizationId, "");
			_assertExpectedOrganizations(
				allOrganizations,
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, "");

			Assert.assertEquals(
				likeNameChildOrganizations.subList(0, 5),
				_organizationService.getOrganizations(
					TestPropsValues.getCompanyId(),
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
					name + "%", 0, 5));
		}
		finally {
			for (Organization childOrganization : allChildOrganizations) {
				_organizationLocalService.deleteOrganization(childOrganization);
			}

			_organizationLocalService.deleteOrganization(parentOrganization);
		}
	}

	@Test
	public void testSetGroupOrganizations() throws Exception {
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroups(
			user.getUserId(), new long[] {TestPropsValues.getGroupId()});

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_OWNER);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), TestPropsValues.getGroupId(),
			new long[] {role.getRoleId()});

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization1);

		_userLocalService.addOrganizationUsers(
			organization1.getOrganizationId(), new long[] {user.getUserId()});

		Organization organization2 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization2);

		_organizationLocalService.addGroupOrganizations(
			TestPropsValues.getGroupId(),
			new long[] {organization2.getOrganizationId()});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser())) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.setGroupOrganizations(
					TestPropsValues.getGroupId(), new long[0]));
			Assert.assertTrue(
				_organizationLocalService.hasGroupOrganization(
					TestPropsValues.getGroupId(),
					organization2.getOrganizationId()));
		}

		Organization organization3 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization3);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_organizationService.setGroupOrganizations(
				TestPropsValues.getGroupId(),
				new long[] {organization1.getOrganizationId()});

			Assert.assertEquals(
				1,
				_organizationLocalService.getGroupOrganizationsCount(
					TestPropsValues.getGroupId()));
			Assert.assertTrue(
				_organizationLocalService.hasGroupOrganization(
					TestPropsValues.getGroupId(),
					organization1.getOrganizationId()));
			Assert.assertFalse(
				_organizationLocalService.hasGroupOrganization(
					TestPropsValues.getGroupId(),
					organization2.getOrganizationId()));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.setGroupOrganizations(
					TestPropsValues.getGroupId(),
					new long[] {organization3.getOrganizationId()}));

			_organizationService.setGroupOrganizations(
				TestPropsValues.getGroupId(), new long[0]);

			Assert.assertEquals(
				0,
				_organizationLocalService.getGroupOrganizationsCount(
					TestPropsValues.getGroupId()));
		}
	}

	@Test
	public void testUnsetGroupOrganizations() throws Exception {
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroups(
			user.getUserId(), new long[] {TestPropsValues.getGroupId()});

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_OWNER);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), TestPropsValues.getGroupId(),
			new long[] {role.getRoleId()});

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization1);

		_userLocalService.addOrganizationUsers(
			organization1.getOrganizationId(), new long[] {user.getUserId()});

		Organization organization2 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization2);

		_organizationLocalService.addGroupOrganizations(
			TestPropsValues.getGroupId(),
			new long[] {
				organization1.getOrganizationId(),
				organization2.getOrganizationId()
			});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_organizationService.unsetGroupOrganizations(
				TestPropsValues.getGroupId(),
				new long[] {organization1.getOrganizationId()});

			Assert.assertFalse(
				_organizationLocalService.hasGroupOrganization(
					TestPropsValues.getGroupId(),
					organization1.getOrganizationId()));
			Assert.assertTrue(
				_organizationLocalService.hasGroupOrganization(
					TestPropsValues.getGroupId(),
					organization2.getOrganizationId()));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _organizationService.unsetGroupOrganizations(
					TestPropsValues.getGroupId(),
					new long[] {organization2.getOrganizationId()}));
		}
	}

	private User _addUser() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, Organization.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			ActionKeys.ASSIGN_MEMBERS);
		RoleTestUtil.addResourcePermission(
			role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.VIEW);

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		return user;
	}

	private void _assertExpectedOrganizations(
			List<Organization> expectedOrganizations, long parentOrganizationId,
			String nameSearch)
		throws Exception {

		List<Organization> actualOrganizations =
			_organizationService.getOrganizations(
				TestPropsValues.getCompanyId(), parentOrganizationId,
				nameSearch, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			actualOrganizations.toString(), expectedOrganizations.size(),
			actualOrganizations.size());
		Assert.assertTrue(
			actualOrganizations.toString(),
			actualOrganizations.containsAll(expectedOrganizations));

		Assert.assertEquals(
			expectedOrganizations.size(),
			_organizationService.getOrganizationsCount(
				TestPropsValues.getCompanyId(), parentOrganizationId,
				nameSearch));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private OrganizationService _organizationService;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserService _userService;

}