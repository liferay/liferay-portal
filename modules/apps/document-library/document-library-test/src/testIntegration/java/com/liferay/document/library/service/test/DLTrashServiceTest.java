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
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashServiceUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class DLTrashServiceTest {

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
	public void testMoveFileEntryFromTrashToFolderWithAddPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		Folder folder = _addFolder(ActionKeys.ADD_DOCUMENT, ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLTrashServiceUtil.moveFileEntryToTrash(fileEntry.getFileEntryId());

			DLTrashServiceUtil.moveFileEntryFromTrash(
				fileEntry.getFileEntryId(), folder.getFolderId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));
		}

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(
			fileEntry.getFileEntryId());

		Assert.assertEquals(folder.getFolderId(), dlFileEntry.getFolderId());
		Assert.assertFalse(dlFileEntry.isInTrash());
	}

	@Test
	public void testMoveFileEntryFromTrashToFolderWithoutAddPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		Folder folder = _addFolder(ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLTrashServiceUtil.moveFileEntryToTrash(fileEntry.getFileEntryId());

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> DLTrashServiceUtil.moveFileEntryFromTrash(
					fileEntry.getFileEntryId(), folder.getFolderId(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), _user.getUserId())));
		}

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(
			fileEntry.getFileEntryId());

		Assert.assertTrue(dlFileEntry.isInTrash());
	}

	@Test
	public void testMoveFileEntryFromTrashToRootFolder() throws Exception {
		Folder folder = _addFolder(ActionKeys.ADD_DOCUMENT, ActionKeys.VIEW);

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), folder.getFolderId());

		DLTrashServiceUtil.moveFileEntryToTrash(fileEntry.getFileEntryId());

		DLTrashServiceUtil.moveFileEntryFromTrash(
			fileEntry.getFileEntryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(
			fileEntry.getFileEntryId());

		Assert.assertEquals(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			dlFileEntry.getFolderId());
		Assert.assertFalse(dlFileEntry.isInTrash());
	}

	@Test
	public void testMoveFileShortcutFromTrashToFolderWithAddPermission()
		throws Exception {

		DLFileShortcut dlFileShortcut = _addDLFileShortcut();

		Folder folder = _addFolder(ActionKeys.ADD_SHORTCUT, ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLTrashServiceUtil.moveFileShortcutToTrash(
				dlFileShortcut.getFileShortcutId());

			DLTrashServiceUtil.moveFileShortcutFromTrash(
				dlFileShortcut.getFileShortcutId(), folder.getFolderId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));
		}

		dlFileShortcut = DLFileShortcutLocalServiceUtil.getDLFileShortcut(
			dlFileShortcut.getFileShortcutId());

		Assert.assertEquals(folder.getFolderId(), dlFileShortcut.getFolderId());
		Assert.assertFalse(dlFileShortcut.isInTrash());
	}

	@Test
	public void testMoveFileShortcutFromTrashToFolderWithoutAddPermission()
		throws Exception {

		DLFileShortcut dlFileShortcut = _addDLFileShortcut();

		Folder folder = _addFolder(ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLTrashServiceUtil.moveFileShortcutToTrash(
				dlFileShortcut.getFileShortcutId());

			long fileShortcutId = dlFileShortcut.getFileShortcutId();

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> DLTrashServiceUtil.moveFileShortcutFromTrash(
					fileShortcutId, folder.getFolderId(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), _user.getUserId())));
		}

		dlFileShortcut = DLFileShortcutLocalServiceUtil.getDLFileShortcut(
			dlFileShortcut.getFileShortcutId());

		Assert.assertTrue(dlFileShortcut.isInTrash());
	}

	@Test
	public void testMoveFolderFromTrashToFolderWithAddPermission()
		throws Exception {

		Folder folder = _addFolder(_user, ActionKeys.VIEW);

		Folder parentFolder = _addFolder(
			ActionKeys.ADD_SUBFOLDER, ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLTrashServiceUtil.moveFolderToTrash(folder.getFolderId());

			DLTrashServiceUtil.moveFolderFromTrash(
				folder.getFolderId(), parentFolder.getFolderId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));
		}

		DLFolder dlFolder = DLFolderLocalServiceUtil.getDLFolder(
			folder.getFolderId());

		Assert.assertEquals(
			parentFolder.getFolderId(), dlFolder.getParentFolderId());
		Assert.assertFalse(dlFolder.isInTrash());
	}

	@Test
	public void testMoveFolderFromTrashToFolderWithoutAddPermission()
		throws Exception {

		Folder folder = _addFolder(_user, ActionKeys.VIEW);

		Folder parentFolder = _addFolder(ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			DLTrashServiceUtil.moveFolderToTrash(folder.getFolderId());

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> DLTrashServiceUtil.moveFolderFromTrash(
					folder.getFolderId(), parentFolder.getFolderId(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), _user.getUserId())));
		}

		DLFolder dlFolder = DLFolderLocalServiceUtil.getDLFolder(
			folder.getFolderId());

		Assert.assertTrue(dlFolder.isInTrash());
	}

	private DLFileShortcut _addDLFileShortcut() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		return DLFileShortcutLocalServiceUtil.addFileShortcut(
			null, _user.getUserId(), _group.getGroupId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));
	}

	private FileEntry _addFileEntry() throws Exception {
		return DLAppTestUtil.addFileEntry(
			_user.getUserId(), _group.getGroupId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	private Folder _addFolder(String... siteMemberActionIds) throws Exception {
		return _addFolder(TestPropsValues.getUser(), siteMemberActionIds);
	}

	private Folder _addFolder(User user, String... siteMemberActionIds)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				Collections.singletonMap(
					RoleConstants.SITE_MEMBER, siteMemberActionIds),
				DLFolder.class.getName()));

		return DLAppLocalServiceUtil.addFolder(
			null, user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}