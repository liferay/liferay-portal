/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.security.permission.wrapper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
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
public class DepotPermissionCheckerWrapperTest {

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
	public void testAssetLibraryAdminIsContentReviewer() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryAdministrator(
			depotEntry,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isContentReviewer(
						depotEntry.getCompanyId(), depotEntry.getGroupId()));
			});
	}

	@Test
	public void testHasPermissionForADepotGroupDelegatesToDepot()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.VIEW));

				RoleTestUtil.addResourcePermission(
					role, Group.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.VIEW);

				Assert.assertFalse(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.VIEW));

				RoleTestUtil.addResourcePermission(
					role, DepotEntry.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.VIEW);

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.VIEW));
			});
	}

	@Test
	public void testHasPermissionForADepotGroupDoesNotDelegateToDepotForUnsupportedPermissions()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.MANAGE_LAYOUTS));

				RoleTestUtil.addResourcePermission(
					role, Group.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.MANAGE_LAYOUTS);

				Assert.assertFalse(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.MANAGE_LAYOUTS));
			});
	}

	@Test
	public void testHasPermissionReturnsTrueForAssetLibraryOwners()
		throws Exception {

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				DepotEntry depotEntry = _addDepotEntry(user.getUserId());

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), DLFileEntry.class.getName(),
						DLFileEntry.class.getName(), ActionKeys.UPDATE));
			});
	}

	@Test
	public void testHasPermissionsWithDepotGroupAndAssetLibraryAdmin()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryAdministrator(
			depotEntry,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.ASSIGN_MEMBERS));

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.ASSIGN_USER_ROLES));

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.DELETE));

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.UPDATE));

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.VIEW));

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(), ActionKeys.VIEW_MEMBERS));

				Assert.assertTrue(
					permissionChecker.hasPermission(
						depotEntry.getGroup(), Group.class.getName(),
						depotEntry.getGroupId(),
						ActionKeys.VIEW_SITE_ADMINISTRATION));
			});
	}

	@Test
	public void testHasStagingPermissionReturnsTrueForAssetLibraryOwners()
		throws Exception {

		DepotTestUtil.withRegularUser(
			(user, role) -> DepotTestUtil.withLocalStagingEnabled(
				_addDepotEntry(user.getUserId()),
				stagingDepotEntry -> {
					PermissionChecker permissionChecker =
						_permissionCheckerFactory.create(user);

					Assert.assertTrue(
						permissionChecker.hasPermission(
							stagingDepotEntry.getGroup(), Group.class.getName(),
							Group.class.getName(), ActionKeys.PUBLISH_STAGING));
				}));
	}

	@Test
	public void testIsContentReviewerWithAssetLibraryAdministrator()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryAdministrator(
			depotEntry,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isContentReviewer(
						user.getCompanyId(), depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsContentReviewerWithAssetLibraryContentReviewer()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryContentReviewer(
			depotEntry,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isContentReviewer(
						user.getCompanyId(), depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsContentReviewerWithStagingEnabled() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withLocalStagingEnabled(
			depotEntry,
			stagingDepotEntry -> DepotTestUtil.withAssetLibraryContentReviewer(
				depotEntry,
				user -> {
					PermissionChecker permissionChecker =
						_permissionCheckerFactory.create(user);

					Assert.assertTrue(
						permissionChecker.isContentReviewer(
							user.getCompanyId(),
							stagingDepotEntry.getGroupId()));
				}));
	}

	@Test
	public void testIsGroupAdminWithDepotGroupAndAssetLibraryMember()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				_userLocalService.addGroupUsers(
					depotEntry.getGroupId(), new long[] {user.getUserId()});

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(
					permissionChecker.isGroupAdmin(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupAdminWithDepotGroupAndAssetLibraryOwner()
		throws Exception {

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				DepotEntry depotEntry = _addDepotEntry(user.getUserId());

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isGroupAdmin(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupAdminWithGroup0AndNoOmniadmin() throws Exception {
		DepotTestUtil.withRegularUser(
			(user, role) -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(permissionChecker.isGroupAdmin(0));
			});
	}

	@Test
	public void testIsGroupAdminWithGuestUser() throws PortalException {
		User user = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			user);

		Assert.assertFalse(
			permissionChecker.isGroupAdmin(TestPropsValues.getGroupId()));
	}

	@Test
	public void testIsGroupAdminWithOmniadmin() throws PortalException {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(permissionChecker.isGroupAdmin(0));
	}

	@Test
	public void testIsGroupAdminWithSiteOwner() throws PortalException {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(
			permissionChecker.isGroupAdmin(TestPropsValues.getGroupId()));
	}

	@Test
	public void testIsGroupAdminWithStagingEnabled() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withLocalStagingEnabled(
			depotEntry,
			stagingDepotEntry -> DepotTestUtil.withAssetLibraryAdministrator(
				depotEntry,
				user -> {
					PermissionChecker permissionChecker =
						_permissionCheckerFactory.create(user);

					Assert.assertTrue(
						permissionChecker.isGroupAdmin(
							stagingDepotEntry.getGroupId()));
				}));
	}

	@Test
	public void testIsGroupMemberWithDepotGroupAndAssetLibraryAdmin()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryAdministrator(
			depotEntry,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isGroupMember(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupMemberWithDepotGroupAndAssetLibraryMember()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				_userLocalService.addGroupUsers(
					depotEntry.getGroupId(), new long[] {user.getUserId()});

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isGroupMember(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupMemberWithDepotGroupAndAssetLibraryOwner()
		throws Exception {

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				DepotEntry depotEntry = _addDepotEntry(user.getUserId());

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isGroupMember(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupMemberWithGroup0AndNoOmniadmin() throws Exception {
		DepotTestUtil.withRegularUser(
			(user, role) -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(permissionChecker.isGroupMember(0));
			});
	}

	@Test
	public void testIsGroupMemberWithGuestUser() throws PortalException {
		User user = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			user);

		Assert.assertFalse(
			permissionChecker.isGroupMember(TestPropsValues.getGroupId()));
	}

	@Test
	public void testIsGroupMemberWithOmniadmin() throws PortalException {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertFalse(permissionChecker.isGroupMember(0));
	}

	@Test
	public void testIsGroupMemberWithSiteOwner() throws PortalException {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(
			permissionChecker.isGroupMember(TestPropsValues.getGroupId()));
	}

	@Test
	public void testIsGroupMemberWithStagingEnabled() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withLocalStagingEnabled(
			depotEntry,
			stagingDepotEntry -> DepotTestUtil.withRegularUser(
				(user, role) -> {
					_userLocalService.addGroupUsers(
						depotEntry.getGroupId(), new long[] {user.getUserId()});

					PermissionChecker permissionChecker =
						_permissionCheckerFactory.create(user);

					Assert.assertTrue(
						permissionChecker.isGroupMember(
							stagingDepotEntry.getGroupId()));
				}));
	}

	@Test
	public void testIsGroupOwnerWithDepotGroupAndAssetLibraryAdmin()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryAdministrator(
			depotEntry,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(
					permissionChecker.isGroupOwner(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupOwnerWithDepotGroupAndAssetLibraryMember()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				_userLocalService.addGroupUsers(
					depotEntry.getGroupId(), new long[] {user.getUserId()});

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(
					permissionChecker.isGroupOwner(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupOwnerWithDepotGroupAndAssetLibraryOwner()
		throws Exception {

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				DepotEntry depotEntry = _addDepotEntry(user.getUserId());

				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertTrue(
					permissionChecker.isGroupOwner(depotEntry.getGroupId()));
			});
	}

	@Test
	public void testIsGroupOwnerWithGroup0AndNoOmniadmin() throws Exception {
		DepotTestUtil.withRegularUser(
			(user, role) -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				Assert.assertFalse(permissionChecker.isGroupOwner(0));
			});
	}

	@Test
	public void testIsGroupOwnerWithGuestUser() throws PortalException {
		User user = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			user);

		Assert.assertFalse(
			permissionChecker.isGroupOwner(TestPropsValues.getGroupId()));
	}

	@Test
	public void testIsGroupOwnerWithOmniadmin() throws PortalException {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(permissionChecker.isGroupOwner(0));
	}

	@Test
	public void testIsGroupOwnerWithSiteOwner() throws PortalException {
		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			TestPropsValues.getUser());

		Assert.assertTrue(
			permissionChecker.isGroupOwner(TestPropsValues.getGroupId()));
	}

	@Test
	public void testIsGroupOwnerWithStagingEnabled() throws Exception {
		DepotTestUtil.withRegularUser(
			(user, role) -> DepotTestUtil.withLocalStagingEnabled(
				_addDepotEntry(user.getUserId()),
				stagingDepotEntry -> {
					PermissionChecker permissionChecker =
						_permissionCheckerFactory.create(user);

					Assert.assertTrue(
						permissionChecker.isGroupOwner(
							stagingDepotEntry.getGroupId()));
				}));
	}

	@Test
	public void testNonconnectedSiteMemberDoesNotHaveDepotConnectedSiteMemberUpdatePermission()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		FileEntry fileEntry = _addFileEntry(depotEntry);

		DepotTestUtil.withAssetLibraryPermissions(
			depotEntry, DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
			DLFileEntry.class.getName(), ActionKeys.UPDATE,
			() -> DepotTestUtil.withGroupPermissions(
				_group, RoleConstants.SITE_MEMBER, DLFileEntry.class.getName(),
				ActionKeys.UPDATE,
				() -> DepotTestUtil.withSiteMember(
					_group,
					user -> {
						PermissionChecker permissionChecker =
							_permissionCheckerFactory.create(user);

						Assert.assertFalse(
							permissionChecker.hasPermission(
								fileEntry.getGroupId(),
								DLFileEntry.class.getName(),
								fileEntry.getFileEntryId(), ActionKeys.UPDATE));
					})));
	}

	@Test
	public void testSiteMemberDoesNotHaveDepotConnectedSiteMemberUpdatePermission()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		FileEntry fileEntry = _addFileEntry(depotEntry);

		DepotTestUtil.withAssetLibraryPermissions(
			depotEntry, DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
			DLFileEntry.class.getName(), ActionKeys.VIEW,
			() -> DepotTestUtil.withGroupPermissions(
				_group, RoleConstants.SITE_MEMBER, DLFileEntry.class.getName(),
				ActionKeys.UPDATE,
				() -> DepotTestUtil.withSiteMember(
					_group,
					user -> {
						PermissionChecker permissionChecker =
							_permissionCheckerFactory.create(user);

						Assert.assertFalse(
							permissionChecker.hasPermission(
								fileEntry.getGroupId(),
								DLFileEntry.class.getName(),
								fileEntry.getFileEntryId(), ActionKeys.UPDATE));
					})));
	}

	@Test
	public void testSiteMemberHasDepotConnectedSiteMemberUpdatePermission()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		FileEntry fileEntry = _addFileEntry(depotEntry);

		DepotTestUtil.withAssetLibraryPermissions(
			depotEntry, DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
			DLFileEntry.class.getName(), ActionKeys.UPDATE,
			() -> DepotTestUtil.withGroupPermissions(
				_group, RoleConstants.SITE_MEMBER, DLFileEntry.class.getName(),
				ActionKeys.UPDATE,
				() -> DepotTestUtil.withSiteMember(
					_group,
					user -> {
						PermissionChecker permissionChecker =
							_permissionCheckerFactory.create(user);

						Assert.assertTrue(
							permissionChecker.hasPermission(
								fileEntry.getGroupId(),
								DLFileEntry.class.getName(),
								fileEntry.getFileEntryId(), ActionKeys.UPDATE));
					})));
	}

	private DepotEntry _addDepotEntry(long userId) throws PortalException {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), userId));

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private FileEntry _addFileEntry(DepotEntry depotEntry) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), depotEntry.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), new byte[0],
			null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private UserLocalService _userLocalService;

}