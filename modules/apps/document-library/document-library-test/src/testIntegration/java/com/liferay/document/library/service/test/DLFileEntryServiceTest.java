/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class DLFileEntryServiceTest {

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
	public void testCopyFileEntryWithExtensionInFolderToFolder()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DLFolder folder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntry dlFileEntry = addDLFileEntry(folder.getFolderId(), true);

		DLFolder destFolder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntryServiceUtil.copyFileEntry(
			_group.getGroupId(), _group.getGroupId(),
			dlFileEntry.getFileEntryId(), destFolder.getFolderId(),
			serviceContext);
	}

	@Test
	public void testCopyFileEntryWithExtensionInFolderToRootFolder()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DLFolder folder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntry dlFileEntry = addDLFileEntry(folder.getFolderId(), true);

		DLFileEntryServiceUtil.copyFileEntry(
			_group.getGroupId(), _group.getGroupId(),
			dlFileEntry.getFileEntryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);
	}

	@Test
	public void testCopyFileEntryWithExtensionInRootFolderToFolder()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DLFileEntry dlFileEntry = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, true);

		DLFolder destFolder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntryServiceUtil.copyFileEntry(
			_group.getGroupId(), _group.getGroupId(),
			dlFileEntry.getFileEntryId(), destFolder.getFolderId(),
			serviceContext);
	}

	@Test
	public void testCopyFileEntryWithoutExtensionInFolderToFolder()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DLFolder folder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntry dlFileEntry = addDLFileEntry(folder.getFolderId(), false);

		DLFolder destFolder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntryServiceUtil.copyFileEntry(
			_group.getGroupId(), _group.getGroupId(),
			dlFileEntry.getFileEntryId(), destFolder.getFolderId(),
			serviceContext);
	}

	@Test
	public void testCopyFileEntryWithoutExtensionInFolderToRootFolder()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DLFolder folder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntry dlFileEntry = addDLFileEntry(folder.getFolderId(), false);

		DLFileEntryServiceUtil.copyFileEntry(
			_group.getGroupId(), _group.getGroupId(),
			dlFileEntry.getFileEntryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);
	}

	@Test
	public void testCopyFileEntryWithoutExtensionInRootFolderToFolder()
		throws Exception {

		DLFileEntry dlFileEntry = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DLFolder destFolder = DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);

		DLFileEntryServiceUtil.copyFileEntry(
			_group.getGroupId(), _group.getGroupId(),
			dlFileEntry.getFileEntryId(), destFolder.getFolderId(),
			serviceContext);
	}

	@Test
	public void testGetFileAsStreamWithDefaultPermissions() throws Exception {
		DLFileEntry dlFileEntry = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);

		updateStatus(
			dlFileEntry.getFileVersion(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		StreamUtil.cleanUp(
			DLFileEntryServiceUtil.getFileAsStream(
				dlFileEntry.getFileEntryId(), dlFileEntry.getVersion()));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		StreamUtil.cleanUp(
			DLFileEntryServiceUtil.getFileAsStream(
				dlFileEntry.getFileEntryId(), dlFileEntry.getVersion()));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.GUEST));

		StreamUtil.cleanUp(
			DLFileEntryServiceUtil.getFileAsStream(
				dlFileEntry.getFileEntryId(), dlFileEntry.getVersion()));
	}

	@Test(expected = PrincipalException.class)
	public void testGetFileAsStreamWithNoDownloadPermission() throws Exception {
		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		DLFileEntry dlFileEntry = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);

		_removeResourcePermission(
			dlFileEntry.getFileEntryId(), RoleConstants.OWNER,
			ActionKeys.DOWNLOAD);
		_removeResourcePermission(
			dlFileEntry.getFileEntryId(), RoleConstants.SITE_MEMBER,
			ActionKeys.DOWNLOAD);
		_removeResourcePermission(
			dlFileEntry.getFileEntryId(), RoleConstants.GUEST,
			ActionKeys.DOWNLOAD);

		StreamUtil.cleanUp(
			DLFileEntryServiceUtil.getFileAsStream(
				dlFileEntry.getFileEntryId(), dlFileEntry.getVersion()));
	}

	@Test
	public void testGetFileEntriesByRating() throws Exception {
		DLFileEntry dlFileEntry1 = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);
		DLFileEntry dlFileEntry2 = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);
		DLFileEntry dlFileEntry3 = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), DLFileEntry.class.getName(),
			dlFileEntry1.getFileEntryId(), 1.0,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), DLFileEntry.class.getName(),
			dlFileEntry2.getFileEntryId(), 1.0,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), DLFileEntry.class.getName(),
			dlFileEntry3.getFileEntryId(), 0.5,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			2,
			DLFileEntryServiceUtil.getFileEntriesCount(
				_group.getGroupId(), 1.0));
		Assert.assertEquals(
			Arrays.asList(dlFileEntry2, dlFileEntry1),
			DLFileEntryServiceUtil.getFileEntries(
				_group.getGroupId(), 1.0, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), DLFileEntry.class.getName(),
			dlFileEntry1.getFileEntryId(), 1.0,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			2,
			DLFileEntryServiceUtil.getFileEntriesCount(
				_group.getGroupId(), 1.0));
		Assert.assertEquals(
			Arrays.asList(dlFileEntry1, dlFileEntry2),
			DLFileEntryServiceUtil.getFileEntries(
				_group.getGroupId(), 1.0, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));
	}

	@Test
	public void testRestoreFileNameWhenDeletingLatestFileVersion()
		throws Exception {

		DLFileEntry dlFileEntry = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		dlFileEntry = updateStatus(
			dlFileEntry.getLatestFileVersion(true), serviceContext);

		String initialFileName = dlFileEntry.getFileName();

		dlFileEntry.setTitle(RandomTestUtil.randomString());

		dlFileEntry = updateDLFileEntry(dlFileEntry, serviceContext);

		dlFileEntry = updateStatus(
			dlFileEntry.getLatestFileVersion(true), serviceContext);

		DLFileVersion dlFileVersion = dlFileEntry.getLatestFileVersion(true);

		dlFileEntry = DLFileEntryLocalServiceUtil.deleteFileVersion(
			dlFileEntry.getUserId(), dlFileEntry.getFileEntryId(),
			dlFileVersion.getVersion());

		Assert.assertEquals(initialFileName, dlFileEntry.getFileName());
	}

	@Test
	public void testUpdateFileName() throws Exception {
		DLFileEntry dlFileEntry = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		dlFileEntry = updateStatus(
			dlFileEntry.getLatestFileVersion(true), serviceContext);

		String title = RandomTestUtil.randomString();

		dlFileEntry.setTitle(title);

		dlFileEntry = updateDLFileEntry(dlFileEntry, serviceContext);

		dlFileEntry = updateStatus(
			dlFileEntry.getLatestFileVersion(true), serviceContext);

		Assert.assertEquals(
			DLUtil.getSanitizedFileName(title, dlFileEntry.getExtension()),
			dlFileEntry.getFileName());
	}

	@Test
	public void testUpdateFileNameWhenUpdatingFileVersionStatus()
		throws Exception {

		DLFileEntry dlFileEntry = addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		dlFileEntry = updateStatus(
			dlFileEntry.getLatestFileVersion(true), serviceContext);

		DLFileVersion dlFileVersion = dlFileEntry.getLatestFileVersion(true);

		String fileName = RandomTestUtil.randomString();

		dlFileVersion.setFileName(fileName);

		dlFileVersion = DLFileVersionLocalServiceUtil.updateDLFileVersion(
			dlFileVersion);

		dlFileEntry = updateStatus(dlFileVersion, serviceContext);

		Assert.assertEquals(
			DLUtil.getSanitizedFileName(fileName, dlFileEntry.getExtension()),
			dlFileEntry.getFileName());
	}

	protected DLFileEntry addDLFileEntry(long folderId, boolean appendExtension)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		String sourceFileName = RandomTestUtil.randomString();

		if (appendExtension) {
			sourceFileName = sourceFileName.concat(".pdf");
		}

		String fileEntryTitle = RandomTestUtil.randomString();

		return DLFileEntryLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), folderId, sourceFileName, null, fileEntryTitle,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(_CONTENT.getBytes()), 0, null, null,
			null, serviceContext);
	}

	protected DLFileEntry updateDLFileEntry(
			DLFileEntry dlFileEntry, ServiceContext serviceContext)
		throws Exception {

		return DLFileEntryLocalServiceUtil.updateFileEntry(
			dlFileEntry.getUserId(), dlFileEntry.getFileEntryId(),
			dlFileEntry.getTitle(), dlFileEntry.getMimeType(),
			dlFileEntry.getTitle(), null, dlFileEntry.getDescription(),
			StringPool.BLANK, DLVersionNumberIncrease.MINOR,
			dlFileEntry.getFileEntryTypeId(), null, null,
			dlFileEntry.getContentStream(), dlFileEntry.getSize(),
			dlFileEntry.getDisplayDate(), dlFileEntry.getExpirationDate(),
			dlFileEntry.getReviewDate(), serviceContext);
	}

	protected DLFileEntry updateStatus(
			DLFileVersion dlFileVersion, ServiceContext serviceContext)
		throws Exception {

		return DLFileEntryLocalServiceUtil.updateStatus(
			dlFileVersion.getUserId(), dlFileVersion.getFileVersionId(),
			WorkflowConstants.STATUS_APPROVED, serviceContext,
			new HashMap<String, Serializable>());
	}

	private void _removeResourcePermission(
			long fileEntryId, String roleName, String actionId)
		throws Exception {

		Role guestRole = RoleLocalServiceUtil.getRole(
			_group.getCompanyId(), roleName);

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			_group.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(fileEntryId),
			guestRole.getRoleId(), actionId);
	}

	private static final String _CONTENT =
		"Content: Enterprise. Open Source. For Life.";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private RatingsEntryLocalService _ratingsEntryLocalService;

}