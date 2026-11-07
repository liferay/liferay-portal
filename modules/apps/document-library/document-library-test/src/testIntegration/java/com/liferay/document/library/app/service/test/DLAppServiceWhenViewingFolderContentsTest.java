/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelReadCountComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelTitleComparator;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class DLAppServiceWhenViewingFolderContentsTest
	extends BaseDLAppTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testShouldCountDraftsIfOwner() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		Assert.assertEquals(
			2,
			dlAppService.getFoldersAndFileEntriesAndFileShortcutsCount(
				group.getGroupId(), parentFolder.getFolderId(),
				WorkflowConstants.STATUS_APPROVED, false));
	}

	@Test
	public void testShouldNotCountDraftsIfNotOwner() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		User user = UserTestUtil.addGroupUser(group, "User");

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			Assert.assertEquals(
				1,
				dlAppService.getFoldersAndFileEntriesAndFileShortcutsCount(
					group.getGroupId(), parentFolder.getFolderId(),
					WorkflowConstants.STATUS_APPROVED, false));
		}
		finally {
			_userLocalService.deleteUser(user.getUserId());
		}
	}

	@Test
	public void testShouldNotReturnContentOfFolderNotInGroup()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		Folder targetGroupFolder = dlAppService.addFolder(
			null, targetGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(targetGroup.getGroupId()));

		dlAppService.addFileEntry(
			null, targetGroup.getGroupId(), targetGroupFolder.getFolderId(),
			StringUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			"title1", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);
		dlAppService.addFileEntry(
			null, targetGroup.getGroupId(), targetGroupFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title2", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		Assert.assertEquals(
			0,
			dlAppService.getFoldersAndFileEntriesAndFileShortcutsCount(
				group.getGroupId(), targetGroupFolder.getFolderId(),
				WorkflowConstants.STATUS_APPROVED,
				ArrayUtil.toStringArray(DLUtil.getAllMediaGalleryMimeTypes()),
				false));
	}

	@Test
	public void testShouldNotReturnDraftsIfNotOwner() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		User user = UserTestUtil.addGroupUser(group, "User");

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			List<Object> foldersAndFileEntriesAndFileShortcuts =
				dlAppService.getFoldersAndFileEntriesAndFileShortcuts(
					group.getGroupId(), parentFolder.getFolderId(),
					WorkflowConstants.STATUS_APPROVED, false, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				foldersAndFileEntriesAndFileShortcuts.toString(), 1,
				foldersAndFileEntriesAndFileShortcuts.size());
		}
		finally {
			_userLocalService.deleteUser(user.getUserId());
		}
	}

	@Test
	@TestInfo({"LPD-113625", "LPS-43104"})
	public void testShouldNotReturnFileEntryWithoutViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		FileEntry fileEntry = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		_removeViewPermission(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId(),
			RoleConstants.GUEST);
		_removeViewPermission(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId(),
			RoleConstants.SITE_MEMBER);

		_assertFoldersAndFileEntriesAndFileShortcutsCount(
			1, _userLocalService.getGuestUser(TestPropsValues.getCompanyId()));
		_assertFoldersAndFileEntriesAndFileShortcutsCount(
			2, TestPropsValues.getUser());

		User user = UserTestUtil.addGroupUser(group, "User");

		try {
			_assertFoldersAndFileEntriesAndFileShortcutsCount(1, user);
		}
		finally {
			_userLocalService.deleteUser(user.getUserId());
		}
	}

	@Test
	@TestInfo("LPD-113625")
	public void testShouldNotReturnFolderWithoutViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		Folder folder = dlAppService.addFolder(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), StringPool.BLANK, serviceContext);

		dlAppService.addFolder(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), StringPool.BLANK, serviceContext);

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(folder.getFolderId()), guestRole.getRoleId(),
			new String[] {ActionKeys.ACCESS});

		_removeViewPermission(
			DLFolder.class.getName(), folder.getFolderId(),
			RoleConstants.SITE_MEMBER);

		_assertFoldersAndFileEntriesAndFileShortcutsCount(
			1, _userLocalService.getGuestUser(TestPropsValues.getCompanyId()));
		_assertFoldersAndFileEntriesAndFileShortcutsCount(
			2, TestPropsValues.getUser());

		User user = UserTestUtil.addGroupUser(group, "User");

		try {
			_assertFoldersAndFileEntriesAndFileShortcutsCount(1, user);
		}
		finally {
			_userLocalService.deleteUser(user.getUserId());
		}
	}

	@Test
	public void testShouldNotReturnNotAcceptedMimeTypes() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			"title1", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);
		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title2", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		dlAppService.addFolder(
			null, group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertEquals(
			2,
			dlAppService.getFoldersAndFileEntriesAndFileShortcutsCount(
				group.getGroupId(), parentFolder.getFolderId(),
				WorkflowConstants.STATUS_APPROVED,
				ArrayUtil.toStringArray(DLUtil.getAllMediaGalleryMimeTypes()),
				false));
	}

	@Test
	public void testShouldReturnDraftsIfOwner() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), null, StringUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null, serviceContext);

		List<Object> foldersAndFileEntriesAndFileShortcuts =
			dlAppService.getFoldersAndFileEntriesAndFileShortcuts(
				group.getGroupId(), parentFolder.getFolderId(),
				WorkflowConstants.STATUS_APPROVED, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			foldersAndFileEntriesAndFileShortcuts.toString(), 2,
			foldersAndFileEntriesAndFileShortcuts.size());
	}

	@Test
	public void testShouldReturnOrderedByReadCount() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		List<FileEntry> expectedFileEntries = new ArrayList<>();

		FileEntry fileEntry1 = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title1", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		_viewCountManager.incrementViewCount(
			fileEntry1.getCompanyId(),
			_classNameLocalService.getClassNameId(DLFileEntry.class),
			fileEntry1.getFileEntryId(), 2);

		FileEntry fileEntry2 = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title2", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		_viewCountManager.incrementViewCount(
			fileEntry2.getCompanyId(),
			_classNameLocalService.getClassNameId(DLFileEntry.class),
			fileEntry2.getFileEntryId(), 1);

		FileEntry fileEntry3 = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title3", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		_viewCountManager.incrementViewCount(
			fileEntry3.getCompanyId(),
			_classNameLocalService.getClassNameId(DLFileEntry.class),
			fileEntry3.getFileEntryId(), 3);

		expectedFileEntries.add(fileEntry2);
		expectedFileEntries.add(fileEntry1);
		expectedFileEntries.add(fileEntry3);

		List<Object> actualFoldersAndFileEntriesAndFileShortcuts =
			dlAppService.getFoldersAndFileEntriesAndFileShortcuts(
				group.getGroupId(), parentFolder.getFolderId(),
				WorkflowConstants.STATUS_APPROVED, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				new RepositoryModelReadCountComparator<>(true));

		Assert.assertEquals(
			actualFoldersAndFileEntriesAndFileShortcuts.toString(), 3,
			actualFoldersAndFileEntriesAndFileShortcuts.size());

		for (int i = 0; i < 3; i++) {
			FileEntry expectedFileEntry = expectedFileEntries.get(i);
			FileEntry actualFileEntry =
				(FileEntry)actualFoldersAndFileEntriesAndFileShortcuts.get(i);

			Assert.assertEquals(
				expectedFileEntry.getFileEntryId(),
				actualFileEntry.getFileEntryId());
		}
	}

	@Test
	public void testShouldReturnOrderedByTitle() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		List<FileEntry> expectedFileEntries = new ArrayList<>();

		FileEntry fileEntry1 = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title2", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		FileEntry fileEntry2 = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title1", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		FileEntry fileEntry3 = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"title3", null, StringUtil.randomString(), StringPool.BLANK,
			(byte[])null, null, null, null, serviceContext);

		expectedFileEntries.add(fileEntry2);
		expectedFileEntries.add(fileEntry1);
		expectedFileEntries.add(fileEntry3);

		List<Object> actualFoldersAndFileEntriesAndFileShortcuts =
			dlAppService.getFoldersAndFileEntriesAndFileShortcuts(
				group.getGroupId(), parentFolder.getFolderId(),
				WorkflowConstants.STATUS_APPROVED, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				new RepositoryModelTitleComparator<FileEntry>(true));

		Assert.assertEquals(
			actualFoldersAndFileEntriesAndFileShortcuts.toString(), 3,
			actualFoldersAndFileEntriesAndFileShortcuts.size());

		for (int i = 0; i < 3; i++) {
			FileEntry expectedFileEntry = expectedFileEntries.get(i);
			FileEntry actualFileEntry =
				(FileEntry)actualFoldersAndFileEntriesAndFileShortcuts.get(i);

			Assert.assertEquals(
				expectedFileEntry.getFileEntryId(),
				actualFileEntry.getFileEntryId());
		}
	}

	private void _assertFoldersAndFileEntriesAndFileShortcutsCount(
			int expectedCount, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			List<Object> foldersAndFileEntriesAndFileShortcuts =
				dlAppService.getFoldersAndFileEntriesAndFileShortcuts(
					group.getGroupId(), parentFolder.getFolderId(),
					WorkflowConstants.STATUS_APPROVED, false, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				foldersAndFileEntriesAndFileShortcuts.toString(), expectedCount,
				foldersAndFileEntriesAndFileShortcuts.size());
		}
	}

	private void _removeViewPermission(
			String name, long primKey, String roleName)
		throws Exception {

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), name,
			ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
			role.getRoleId(), ActionKeys.VIEW);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private ViewCountManager _viewCountManager;

}