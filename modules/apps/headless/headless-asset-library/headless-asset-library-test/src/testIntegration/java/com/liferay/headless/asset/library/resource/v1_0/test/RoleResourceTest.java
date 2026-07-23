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
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
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
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

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

		CMSTestUtil.getOrAddGroup(RoleResourceTest.class);

		_userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroup(
			testDepotEntry.getGroupId(), _userGroup);
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
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

		com.liferay.portal.kernel.model.Role role = _roleLocalService.fetchRole(
			roleId);

		_roles.add(role);

		return new Role() {
			{
				externalReferenceCode = role.getExternalReferenceCode();
				id = role.getRoleId();
				name = role.getName();
				name_i18n = LocalizedMapUtil.getI18nMap(role.getTitleMap());
				roleType = role.getType();
			}
		};
	}

	@Override
	protected Role testGetAssetLibraryRolesPage_addRole(
			String assetLibraryExternalReferenceCode, Role role)
		throws Exception {

		com.liferay.portal.kernel.model.Role depotRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_DEPOT);

		_roles.add(depotRole);

		return new Role() {
			{
				externalReferenceCode = depotRole.getExternalReferenceCode();
				id = depotRole.getRoleId();
				name = depotRole.getName();
				roleType = depotRole.getType();
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

	private RoleResource _getRoleResource(String roleName) throws Exception {
		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {testDepotEntry.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		com.liferay.portal.kernel.model.Role role = _roleLocalService.getRole(
			testCompany.getCompanyId(), roleName);

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), testDepotEntry.getGroupId(),
			new long[] {role.getRoleId()});

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

	private void _testGetAssetLibraryRolesPage(String roleName)
		throws Exception {

		RoleResource roleResource = _getRoleResource(roleName);

		Page<Role> page = roleResource.getAssetLibraryRolesPage(
			testDepotEntryGroup.getExternalReferenceCode(),
			Pagination.of(1, 10));

		List<String> names = TransformUtil.transform(
			page.getItems(), Role::getName);

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

		_roles.add(serviceBuilderRole1);

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			_roleLocalService.addRole(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				null, 0, RandomTestUtil.randomString(), null, null,
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_SPACE,
				null);

		_roles.add(serviceBuilderRole2);

		Group group = _depotEntry.getGroup();

		Page<Role> page = roleResource.getAssetLibraryRolesPage(
			group.getExternalReferenceCode(), Pagination.of(1, 100));

		List<Role> roles = ListUtil.fromCollection(page.getItems());

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

		try {
			unsafeBiConsumer.accept(new Role[] {randomRole3});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}

		_assertRolesPage(new Role[] {randomRole1, randomRole2}, unsafeSupplier);
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private List<com.liferay.portal.kernel.model.Role> _roles =
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