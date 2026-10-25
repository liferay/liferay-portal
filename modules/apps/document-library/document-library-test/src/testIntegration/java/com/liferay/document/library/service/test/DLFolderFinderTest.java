/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashServiceUtil;
import com.liferay.document.library.kernel.service.persistence.DLFolderFinderUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.portletrepository.PortletRepository;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class DLFolderFinderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			TransactionalTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(PortletRepository.class.getName()),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test Repository",
			StringUtil.randomString(), StringUtil.randomString(),
			new UnicodeProperties(), false, serviceContext);

		_folder = DLAppLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Folder A",
			StringPool.BLANK, serviceContext);

		DLAppLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_folder.getFolderId(), "Folder B", StringPool.BLANK,
			serviceContext);

		Folder folder = DLAppLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_folder.getFolderId(), "Folder C", StringPool.BLANK,
			serviceContext);

		DLTrashServiceUtil.moveFolderToTrash(folder.getFolderId());

		FileEntry fileEntry = addFileEntry(
			_group.getGroupId(), _folder.getFolderId(), "FE1.txt",
			ContentTypes.TEXT_PLAIN);

		_fileShortcut = DLAppLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fileEntry.getFolderId(), fileEntry.getFileEntryId(),
			serviceContext);

		addFileEntry(
			_group.getGroupId(), _folder.getFolderId(), "FE2.html",
			ContentTypes.TEXT_HTML);

		fileEntry = addFileEntry(
			_group.getGroupId(), _folder.getFolderId(), "FE3.txt",
			ContentTypes.TEXT_PLAIN);

		DLTrashServiceUtil.moveFileEntryToTrash(fileEntry.getFileEntryId());
	}

	@Test
	public void testCountF_FE_FS_ByG_F_M_M() throws Exception {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>();

		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			6,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), _folder.getFolderId(), null, false,
				queryDefinition));
		Assert.assertEquals(
			5,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), _folder.getFolderId(),
				new String[] {ContentTypes.TEXT_PLAIN}, false,
				queryDefinition));
		Assert.assertEquals(
			1,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				null, false, queryDefinition));
		Assert.assertEquals(
			2,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				null, true, queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			4,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), _folder.getFolderId(), null, false,
				queryDefinition));
		Assert.assertEquals(
			2,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), _folder.getFolderId(),
				new String[] {ContentTypes.TEXT_HTML}, false, queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH);

		Assert.assertEquals(
			2,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), _folder.getFolderId(), null, false,
				queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);

		Assert.assertEquals(
			4,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), _folder.getFolderId(), null, false,
				queryDefinition));
		Assert.assertEquals(
			3,
			DLFolderFinderUtil.filterCountF_FE_FS_ByG_F_M_M(
				_group.getGroupId(), _folder.getFolderId(),
				new String[] {ContentTypes.TEXT_PLAIN}, false,
				queryDefinition));
	}

	@Test
	public void testCountFE_FS_ByG_F() throws Exception {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>();

		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			4,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F(
				_group.getGroupId(), _folder.getFolderId(), queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			3,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F(
				_group.getGroupId(), _folder.getFolderId(), queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH);

		Assert.assertEquals(
			1,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F(
				_group.getGroupId(), _folder.getFolderId(), queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);

		Assert.assertEquals(
			3,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F(
				_group.getGroupId(), _folder.getFolderId(), queryDefinition));
	}

	@Test
	public void testCountFE_FS_ByG_F_M() throws Exception {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>();

		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			4,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(), null,
				queryDefinition));
		Assert.assertEquals(
			3,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(),
				new String[] {ContentTypes.TEXT_PLAIN}, queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			3,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(), null,
				queryDefinition));
		Assert.assertEquals(
			1,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(),
				new String[] {ContentTypes.TEXT_HTML}, queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH);

		Assert.assertEquals(
			1,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(), null,
				queryDefinition));
		Assert.assertEquals(
			1,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(),
				new String[] {ContentTypes.TEXT_PLAIN}, queryDefinition));

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);

		Assert.assertEquals(
			3,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(), null,
				queryDefinition));
		Assert.assertEquals(
			2,
			DLFolderFinderUtil.filterCountFE_FS_ByG_F_M(
				_group.getGroupId(), _folder.getFolderId(),
				new String[] {ContentTypes.TEXT_PLAIN}, queryDefinition));
	}

	@Test
	public void testFindF_FE_FS_ByG_F_M_M() throws Exception {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>();

		queryDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		List<Object> results = DLFolderFinderUtil.filterFindF_FE_FS_ByG_F_M_M(
			_group.getGroupId(), _folder.getFolderId(),
			new String[] {ContentTypes.TEXT_PLAIN}, false, queryDefinition);

		Assert.assertEquals(results.toString(), 3, results.size());

		for (Object result : results) {
			Assert.assertTrue(
				String.valueOf(result.getClass()),
				result instanceof DLFileEntry ||
				result instanceof DLFileShortcut || result instanceof DLFolder);

			if (result instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)result;

				Assert.assertEquals("FE1.txt", dlFileEntry.getTitle());
			}
			else if (result instanceof FileShortcut) {
				FileShortcut fileShortcut = (FileShortcut)result;

				Assert.assertEquals(
					_fileShortcut.getFileShortcutId(),
					fileShortcut.getFileShortcutId());
			}
			else if (result instanceof DLFolder) {
				DLFolder dlFolder = (DLFolder)result;

				Assert.assertEquals("Folder B", dlFolder.getName());
			}
		}

		results = DLFolderFinderUtil.filterFindF_FE_FS_ByG_F_M_M(
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			null, true, queryDefinition);

		Assert.assertEquals(results.toString(), 2, results.size());

		boolean assertMountPointFolder = false;
		boolean assertRegularFolder = false;

		for (Object result : results) {
			Assert.assertTrue(
				String.valueOf(result.getClass()), result instanceof DLFolder);

			DLFolder dlFolder = (DLFolder)result;

			if (dlFolder.isMountPoint()) {
				Assert.assertEquals("Test Repository", dlFolder.getName());

				assertMountPointFolder = true;
			}
			else {
				Assert.assertEquals("Folder A", dlFolder.getName());

				assertRegularFolder = true;
			}
		}

		Assert.assertTrue(assertMountPointFolder);
		Assert.assertTrue(assertRegularFolder);
	}

	protected FileEntry addFileEntry(
			long groupId, long folderId, String sourceFileName, String mimeType)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), groupId, folderId,
			sourceFileName, mimeType, TestDataConstants.TEST_BYTE_ARRAY, null,
			null, null, serviceContext);
	}

	private FileShortcut _fileShortcut;
	private Folder _folder;

	@DeleteAfterTestRun
	private Group _group;

}