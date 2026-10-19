/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileShortcutServiceUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class DLFileShortcutServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER);
	}

	@Test
	public void testGetGroupFileShortcutsFiltersByPermission()
		throws Exception {

		DLFileShortcut hiddenDLFileShortcut = _addDLFileShortcut(false);
		DLFileShortcut visibleDLFileShortcut = _addDLFileShortcut(true);

		List<DLFileShortcut> dlFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId());

		Assert.assertEquals(
			SetUtil.fromArray(hiddenDLFileShortcut, visibleDLFileShortcut),
			SetUtil.fromCollection(dlFileShortcuts));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		List<DLFileShortcut> filteredDLFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId());

		Assert.assertEquals(
			Collections.singletonList(visibleDLFileShortcut),
			filteredDLFileShortcuts);
	}

	@Test
	public void testGetGroupFileShortcutsPaginatedFiltersByPermission()
		throws Exception {

		DLFileShortcut hiddenDLFileShortcut = _addDLFileShortcut(false);
		DLFileShortcut visibleDLFileShortcut = _addDLFileShortcut(true);

		List<DLFileShortcut> dlFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId(), 0, 2);

		Assert.assertEquals(
			SetUtil.fromArray(hiddenDLFileShortcut, visibleDLFileShortcut),
			SetUtil.fromCollection(dlFileShortcuts));

		Assert.assertEquals(
			2,
			DLFileShortcutServiceUtil.getGroupFileShortcutsCount(
				_group.getGroupId()));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		List<DLFileShortcut> filteredDLFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId(), 0, 2);

		Assert.assertEquals(
			Collections.singletonList(visibleDLFileShortcut),
			filteredDLFileShortcuts);

		Assert.assertEquals(
			1,
			DLFileShortcutServiceUtil.getGroupFileShortcutsCount(
				_group.getGroupId()));
	}

	@Test
	@TestInfo("LPD-110142")
	public void testUpdateFileShortcut() throws Exception {
		_testUpdateFileShortcut();
		_testUpdateFileShortcutWithoutAddShortcutPermission();
		_testUpdateFileShortcutWithoutFolderChange();
	}

	@Test
	public void testUpdateFileShortcuts() throws Exception {
		_testUpdateFileShortcuts();
		_testUpdateFileShortcutsWithoutUpdatePermission();
	}

	private DLFileShortcut _addDLFileShortcut(boolean addDefaultPermissions)
		throws Exception {

		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		return _addDLFileShortcut(
			addDefaultPermissions, dlFileEntry.getFileEntryId());
	}

	private DLFileShortcut _addDLFileShortcut(
			boolean addDefaultPermissions, long toFileEntryId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(addDefaultPermissions);
		serviceContext.setAddGuestPermissions(addDefaultPermissions);

		return DLFileShortcutLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			toFileEntryId, serviceContext);
	}

	private DLFileShortcut _addDLFileShortcut(
			long folderId, long toFileEntryId, long userId)
		throws Exception {

		return DLFileShortcutLocalServiceUtil.addFileShortcut(
			null, userId, _group.getGroupId(), _group.getGroupId(), folderId,
			toFileEntryId,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), userId));
	}

	private DLFolder _addRestrictedDLFolder() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				new String[] {ActionKeys.VIEW}, new String[] {ActionKeys.VIEW},
				DLFolder.class.getName()));

		return DLTestUtil.addDLFolder(_group.getGroupId(), serviceContext);
	}

	private void _testUpdateFileShortcut() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFileShortcut dlFileShortcut = _addDLFileShortcut(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getFileEntryId(), _user.getUserId());

		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLFileShortcutServiceUtil.updateFileShortcut(
				dlFileShortcut.getFileShortcutId(), _group.getGroupId(),
				dlFolder.getFolderId(), fileEntry.getFileEntryId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));
		}

		dlFileShortcut = DLFileShortcutLocalServiceUtil.getDLFileShortcut(
			dlFileShortcut.getFileShortcutId());

		Assert.assertEquals(
			dlFolder.getFolderId(), dlFileShortcut.getFolderId());
	}

	private void _testUpdateFileShortcutWithoutAddShortcutPermission()
		throws Exception {

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFileShortcut dlFileShortcut = _addDLFileShortcut(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getFileEntryId(), _user.getUserId());

		DLFolder dlFolder = _addRestrictedDLFolder();

		long fileShortcutId = dlFileShortcut.getFileShortcutId();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException.MustHavePermission mustHavePermission =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> DLFileShortcutServiceUtil.updateFileShortcut(
						fileShortcutId, _group.getGroupId(),
						dlFolder.getFolderId(), fileEntry.getFileEntryId(),
						ServiceContextTestUtil.getServiceContext(
							_group.getGroupId(), _user.getUserId())));

			Assert.assertEquals(
				Folder.class.getName(), mustHavePermission.resourceName);
			Assert.assertEquals(
				dlFolder.getFolderId(), mustHavePermission.resourceId);
			Assert.assertArrayEquals(
				new String[] {ActionKeys.ADD_SHORTCUT},
				mustHavePermission.actionId);
		}

		dlFileShortcut = DLFileShortcutLocalServiceUtil.getDLFileShortcut(
			fileShortcutId);

		Assert.assertEquals(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			dlFileShortcut.getFolderId());
	}

	private void _testUpdateFileShortcutWithoutFolderChange() throws Exception {
		FileEntry fileEntry1 = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFolder dlFolder = _addRestrictedDLFolder();

		DLFileShortcut dlFileShortcut = _addDLFileShortcut(
			dlFolder.getFolderId(), fileEntry1.getFileEntryId(),
			_user.getUserId());

		FileEntry fileEntry2 = DLAppTestUtil.addFileEntry(_group.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLFileShortcutServiceUtil.updateFileShortcut(
				dlFileShortcut.getFileShortcutId(), _group.getGroupId(),
				dlFolder.getFolderId(), fileEntry2.getFileEntryId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));
		}

		dlFileShortcut = DLFileShortcutLocalServiceUtil.getDLFileShortcut(
			dlFileShortcut.getFileShortcutId());

		Assert.assertEquals(
			dlFolder.getFolderId(), dlFileShortcut.getFolderId());
		Assert.assertEquals(
			fileEntry2.getFileEntryId(), dlFileShortcut.getToFileEntryId());
	}

	private void _testUpdateFileShortcuts() throws Exception {
		FileEntry fileEntry1 = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFileShortcut dlFileShortcut1 = _addDLFileShortcut(
			true, fileEntry1.getFileEntryId());
		DLFileShortcut dlFileShortcut2 = _addDLFileShortcut(
			true, fileEntry1.getFileEntryId());

		FileEntry fileEntry2 = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFileShortcutServiceUtil.updateFileShortcuts(
			fileEntry1.getFileEntryId(), fileEntry2.getFileEntryId());

		Assert.assertEquals(
			SetUtil.fromArray(dlFileShortcut1, dlFileShortcut2),
			SetUtil.fromCollection(
				DLFileShortcutLocalServiceUtil.getFileShortcuts(
					fileEntry2.getFileEntryId())));
	}

	private void _testUpdateFileShortcutsWithoutUpdatePermission()
		throws Exception {

		FileEntry fileEntry1 = DLAppTestUtil.addFileEntry(_group.getGroupId());

		DLFileShortcut dlFileShortcut1 = _addDLFileShortcut(
			true, fileEntry1.getFileEntryId());
		DLFileShortcut dlFileShortcut2 = _addDLFileShortcut(
			true, fileEntry1.getFileEntryId());

		FileEntry fileEntry2 = DLAppTestUtil.addFileEntry(_group.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER))) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> DLFileShortcutServiceUtil.updateFileShortcuts(
					fileEntry1.getFileEntryId(), fileEntry2.getFileEntryId()));
		}

		Assert.assertEquals(
			SetUtil.fromArray(dlFileShortcut1, dlFileShortcut2),
			SetUtil.fromCollection(
				DLFileShortcutLocalServiceUtil.getFileShortcuts(
					fileEntry1.getFileEntryId())));
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}