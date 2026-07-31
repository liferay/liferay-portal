/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.asset.library.client.dto.v1_0.Role;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.problem.Problem;
import com.liferay.headless.asset.library.client.resource.v1_0.RoleResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class RoleResourceTest extends BaseRoleResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroup(
			testDepotEntry.getGroupId(), _userGroup);
	}

	@FeatureFlag("LPD-96750")
	@Override
	@Test
	public void testGetAssetLibraryRolesPage() throws Exception {
		_testGetAssetLibraryRolesPage(
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);
		_testGetAssetLibraryRolesPage(DepotRolesConstants.ASSET_LIBRARY_MEMBER);
		_testGetAssetLibraryRolesPageWithSubtype();
	}

	@Override
	@Test
	public void testPutAssetLibraryUserAccountRolesPage() throws Exception {
		User user = TestPropsValues.getUser();

		_testPutRolesPage(
			() -> roleResource.getAssetLibraryUserAccountRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				user.getExternalReferenceCode()),
			roles -> roleResource.putAssetLibraryUserAccountRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				user.getExternalReferenceCode(), roles));

		_testPutAssetLibraryUserAccountRolesPageWithAssignMembersPermission();
		_testPutAssetLibraryUserAccountRolesPageWithAssignMembersPermissionAndAdministratorRole();
		_testPutAssetLibraryUserAccountRolesPageWithAssignUserRolesPermission();
		_testPutAssetLibraryUserAccountRolesPageWithAssignUserRolesPermissionAndWithoutRoleViewPermission();
	}

	@Override
	@Test
	public void testPutAssetLibraryUserGroupRolesPage() throws Exception {
		_testPutRolesPage(
			() -> roleResource.getAssetLibraryUserGroupRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				_userGroup.getExternalReferenceCode()),
			roles -> roleResource.putAssetLibraryUserGroupRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				_userGroup.getExternalReferenceCode(), roles));
	}

	@Override
	protected Role randomRole() throws Exception {
		long roleId = RoleTestUtil.addGroupRole(testGroup.getGroupId());

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleLocalService.fetchRole(roleId);

		_serviceBuilderRoles.add(serviceBuilderRole);

		return new Role() {
			{
				externalReferenceCode =
					serviceBuilderRole.getExternalReferenceCode();
				id = serviceBuilderRole.getRoleId();
				name = serviceBuilderRole.getName();
				name_i18n = LocalizedMapUtil.getI18nMap(
					serviceBuilderRole.getTitleMap());
				roleType = serviceBuilderRole.getType();
			}
		};
	}

	@Override
	protected Role testGetAssetLibraryRolesPage_addRole(
			String assetLibraryExternalReferenceCode, Role role)
		throws Exception {

		com.liferay.portal.kernel.model.Role depotServiceBuilderRole =
			RoleTestUtil.addRole(RoleConstants.TYPE_DEPOT);

		_serviceBuilderRoles.add(depotServiceBuilderRole);

		return new Role() {
			{
				externalReferenceCode =
					depotServiceBuilderRole.getExternalReferenceCode();
				id = depotServiceBuilderRole.getRoleId();
				name = depotServiceBuilderRole.getName();
				roleType = depotServiceBuilderRole.getType();
			}
		};
	}

	@Override
	protected String
			testGetAssetLibraryRolesPage_getIrrelevantAssetLibraryExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Override
	protected Role testGetAssetLibraryUserAccountRolesPage_addRole(
			String assetLibraryExternalReferenceCode,
			String userAccountExternalReferenceCode, Role role)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			assetLibraryExternalReferenceCode, TestPropsValues.getCompanyId());
		User user = _userLocalService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, TestPropsValues.getCompanyId());

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), group.getGroupId(), new long[] {role.getId()});

		return role;
	}

	@Override
	protected String
			testGetAssetLibraryUserAccountRolesPage_getUserAccountExternalReferenceCode()
		throws Exception {

		User user = TestPropsValues.getUser();

		return user.getExternalReferenceCode();
	}

	@Override
	protected Role testGetAssetLibraryUserGroupRolesPage_addRole(
			String assetLibraryExternalReferenceCode,
			String userGroupExternalReferenceCode, Role role)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			assetLibraryExternalReferenceCode, TestPropsValues.getCompanyId());
		UserGroup userGroup =
			_userGroupLocalService.getUserGroupByExternalReferenceCode(
				userGroupExternalReferenceCode, TestPropsValues.getCompanyId());

		_userGroupGroupRoleLocalService.addUserGroupGroupRoles(
			userGroup.getUserGroupId(), group.getGroupId(),
			new long[] {role.getId()});

		return role;
	}

	@Override
	protected String
			testGetAssetLibraryUserGroupRolesPage_getUserGroupExternalReferenceCode()
		throws Exception {

		return _userGroup.getExternalReferenceCode();
	}

	private void _assertProblemStatus(
			String status, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
		}
	}

	private void _assertRolesPage(
			Role[] expectedRoles,
			UnsafeSupplier<Page<Role>, Exception> unsafeSupplier)
		throws Exception {

		Page<Role> rolesPage = unsafeSupplier.get();

		Collection<Role> items = rolesPage.getItems();

		Assert.assertEquals(
			items.toString(), expectedRoles.length, items.size());

		for (Role role : expectedRoles) {
			Assert.assertTrue(items.contains(role));
		}
	}

	private RoleResource _getDepotEntryRoleResource(
			String actionId,
			com.liferay.portal.kernel.model.Role... viewableServiceBuilderRoles)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_serviceBuilderRoles.add(serviceBuilderRole);

		_userLocalService.addRoleUser(serviceBuilderRole.getRoleId(), user);

		RoleTestUtil.addResourcePermission(
			serviceBuilderRole, DepotEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(testDepotEntry.getGroupId()), actionId);
		RoleTestUtil.addResourcePermission(
			serviceBuilderRole, DepotEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(testDepotEntry.getGroupId()), ActionKeys.VIEW);
		RoleTestUtil.addResourcePermission(
			serviceBuilderRole, User.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.VIEW);

		for (com.liferay.portal.kernel.model.Role viewableServiceBuilderRole :
				viewableServiceBuilderRoles) {

			_resourcePermissionLocalService.setResourcePermissions(
				viewableServiceBuilderRole.getCompanyId(),
				com.liferay.portal.kernel.model.Role.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(viewableServiceBuilderRole.getRoleId()),
				serviceBuilderRole.getRoleId(), new String[] {ActionKeys.VIEW});
		}

		return RoleResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private RoleResource _getRoleResource(String roleName) throws Exception {
		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {testDepotEntry.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_getServiceBuilderRole(roleName);

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), testDepotEntry.getGroupId(),
			new long[] {serviceBuilderRole.getRoleId()});

		return RoleResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private com.liferay.portal.kernel.model.Role _getServiceBuilderRole(
			String roleName)
		throws Exception {

		return _roleLocalService.getRole(testCompany.getCompanyId(), roleName);
	}

	private void _testGetAssetLibraryRolesPage(String roleName)
		throws Exception {

		RoleResource roleResource = _getRoleResource(roleName);

		Page<Role> rolesPage = roleResource.getAssetLibraryRolesPage(
			testDepotEntryGroup.getExternalReferenceCode(),
			Pagination.of(1, 10));

		List<String> names = TransformUtil.transform(
			rolesPage.getItems(), Role::getName);

		Assert.assertTrue(
			names.toString(),
			names.contains(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR));
		Assert.assertTrue(
			names.toString(),
			names.contains(DepotRolesConstants.ASSET_LIBRARY_MEMBER));
		Assert.assertTrue(
			names.toString(),
			names.contains(DepotRolesConstants.ASSET_LIBRARY_OWNER));
	}

	private void _testGetAssetLibraryRolesPageWithSubtype() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			_roleLocalService.addRole(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				null, 0, RandomTestUtil.randomString(), null, null,
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_PROJECT,
				null);

		_serviceBuilderRoles.add(serviceBuilderRole1);

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			_roleLocalService.addRole(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				null, 0, RandomTestUtil.randomString(), null, null,
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_SPACE,
				null);

		_serviceBuilderRoles.add(serviceBuilderRole2);

		Group group = _depotEntry.getGroup();

		Page<Role> rolesPage = roleResource.getAssetLibraryRolesPage(
			group.getExternalReferenceCode(), Pagination.of(1, 100));

		List<Role> roles = ListUtil.fromCollection(rolesPage.getItems());

		Assert.assertTrue(
			roles.toString(),
			ListUtil.exists(
				roles,
				role -> Objects.equals(
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
					role.getName())));
		Assert.assertFalse(
			roles.toString(),
			ListUtil.exists(
				roles,
				role -> Objects.equals(
					serviceBuilderRole1.getName(), role.getName())));
		Assert.assertTrue(
			roles.toString(),
			ListUtil.exists(
				roles,
				role -> Objects.equals(
					serviceBuilderRole2.getName(), role.getName())));
	}

	private void _testPutAssetLibraryUserAccountRolesPageWithAssignMembersPermission()
		throws Exception {

		RoleResource assignMembersRoleResource = _getDepotEntryRoleResource(
			ActionKeys.ASSIGN_MEMBERS);

		User user = UserTestUtil.addUser(testDepotEntry.getGroupId());

		com.liferay.portal.kernel.model.Role
			assetLibraryMemberServiceBuilderRole = _getServiceBuilderRole(
				DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		Page<Role> rolesPage =
			assignMembersRoleResource.putAssetLibraryUserAccountRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				user.getExternalReferenceCode(),
				_toRoles(assetLibraryMemberServiceBuilderRole));

		List<String> names = TransformUtil.transform(
			rolesPage.getItems(), Role::getName);

		Assert.assertEquals(names.toString(), 1, names.size());
		Assert.assertTrue(
			names.toString(),
			names.contains(DepotRolesConstants.ASSET_LIBRARY_MEMBER));

		com.liferay.portal.kernel.model.Role
			assetLibraryContentReviewerServiceBuilderRole =
				_getServiceBuilderRole(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		_assertProblemStatus(
			"FORBIDDEN",
			() -> assignMembersRoleResource.putAssetLibraryUserAccountRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				user.getExternalReferenceCode(),
				_toRoles(assetLibraryContentReviewerServiceBuilderRole)));
	}

	private void _testPutAssetLibraryUserAccountRolesPageWithAssignMembersPermissionAndAdministratorRole()
		throws Exception {

		RoleResource assignMembersRoleResource = _getDepotEntryRoleResource(
			ActionKeys.ASSIGN_MEMBERS);

		User user = UserTestUtil.addUser(testDepotEntry.getGroupId());

		_assertProblemStatus(
			"FORBIDDEN",
			() -> assignMembersRoleResource.putAssetLibraryUserAccountRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				user.getExternalReferenceCode(),
				_toRoles(
					_getServiceBuilderRole(
						DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR))));
	}

	private void _testPutAssetLibraryUserAccountRolesPageWithAssignUserRolesPermission()
		throws Exception {

		User user = UserTestUtil.addUser(testDepotEntry.getGroupId());

		com.liferay.portal.kernel.model.Role
			assetLibraryMemberServiceBuilderRole = _getServiceBuilderRole(
				DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), testDepotEntry.getGroupId(),
			new long[] {assetLibraryMemberServiceBuilderRole.getRoleId()});

		com.liferay.portal.kernel.model.Role
			assetLibraryContentReviewerServiceBuilderRole =
				_getServiceBuilderRole(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		RoleResource assignUserRolesRoleResource = _getDepotEntryRoleResource(
			ActionKeys.ASSIGN_USER_ROLES,
			assetLibraryContentReviewerServiceBuilderRole);

		Page<Role> rolesPage =
			assignUserRolesRoleResource.putAssetLibraryUserAccountRolesPage(
				testDepotEntryGroup.getExternalReferenceCode(),
				user.getExternalReferenceCode(),
				_toRoles(assetLibraryContentReviewerServiceBuilderRole));

		List<String> names = TransformUtil.transform(
			rolesPage.getItems(), Role::getName);

		Assert.assertEquals(names.toString(), 1, names.size());
		Assert.assertTrue(
			names.toString(),
			names.contains(DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER));
	}

	private void _testPutAssetLibraryUserAccountRolesPageWithAssignUserRolesPermissionAndWithoutRoleViewPermission()
		throws Exception {

		User user = UserTestUtil.addUser(testDepotEntry.getGroupId());

		com.liferay.portal.kernel.model.Role
			assetLibraryMemberServiceBuilderRole = _getServiceBuilderRole(
				DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), testDepotEntry.getGroupId(),
			new long[] {assetLibraryMemberServiceBuilderRole.getRoleId()});

		com.liferay.portal.kernel.model.Role
			assetLibraryContentReviewerServiceBuilderRole =
				_getServiceBuilderRole(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		RoleResource assignUserRolesRoleResource = _getDepotEntryRoleResource(
			ActionKeys.ASSIGN_USER_ROLES);

		_assertProblemStatus(
			"FORBIDDEN",
			() ->
				assignUserRolesRoleResource.putAssetLibraryUserAccountRolesPage(
					testDepotEntryGroup.getExternalReferenceCode(),
					user.getExternalReferenceCode(),
					_toRoles(assetLibraryContentReviewerServiceBuilderRole)));
	}

	private void _testPutRolesPage(
			UnsafeSupplier<Page<Role>, Exception> unsafeSupplier,
			UnsafeConsumer<Role[], Exception> unsafeBiConsumer)
		throws Exception {

		Role randomRole1 = randomRole();

		unsafeBiConsumer.accept(new Role[] {randomRole1});

		_assertRolesPage(new Role[] {randomRole1}, unsafeSupplier);

		Role randomRole2 = randomRole();

		unsafeBiConsumer.accept(new Role[] {randomRole1, randomRole2});

		_assertRolesPage(new Role[] {randomRole1, randomRole2}, unsafeSupplier);

		Role randomRole3 = new Role() {
			{
				name = RandomTestUtil.randomString();
			}
		};

		_assertProblemStatus(
			"NOT_FOUND",
			() -> unsafeBiConsumer.accept(new Role[] {randomRole3}));

		_assertRolesPage(new Role[] {randomRole1, randomRole2}, unsafeSupplier);
	}

	private Role[] _toRoles(
		com.liferay.portal.kernel.model.Role serviceBuilderRole) {

		return new Role[] {
			new Role() {
				{
					id = serviceBuilderRole.getRoleId();
					name = serviceBuilderRole.getName();
				}
			}
		};
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private List<com.liferay.portal.kernel.model.Role> _serviceBuilderRoles =
		new ArrayList<>();

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleService _userGroupRoleService;

	@Inject
	private UserLocalService _userLocalService;

}