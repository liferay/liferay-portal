/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashServiceUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingConstants;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
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
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class DLLocalStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_liveGroup.getGroupId());

		_addStagingAttribute(
			serviceContext,
			StagingUtil.getStagedPortletId(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN),
			true);
		_addStagingAttribute(
			serviceContext, PortletDataHandlerKeys.DATA_STRATEGY_MIRROR, true);

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), _liveGroup, false, false,
			serviceContext);

		_liveGroup = GroupLocalServiceUtil.getGroup(_liveGroup.getGroupId());

		_stagingGroup = _liveGroup.getStagingGroup();
	}

	@Test
	@TestInfo("LPD-113633")
	public void testPublishDeletedFileEntryWithFileEntryType()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_stagingGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		DLFileEntryType dlFileEntryType =
			DLFileEntryTypeLocalServiceUtil.addFileEntryType(
				null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
				ddmStructure.getStructureId(), null,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				ServiceContextTestUtil.getServiceContext(
					_stagingGroup.getGroupId()));

		ddmStructure = DDMStructureLocalServiceUtil.getDDMStructure(
			ddmStructure.getStructureId());

		ddmStructure.setStructureKey(
			DLUtil.getDDMStructureKey(dlFileEntryType));

		DDMStructureLocalServiceUtil.updateDDMStructure(ddmStructure);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId());

		serviceContext.setAttribute(
			"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null, serviceContext);

		_publish();

		DLFileEntry liveDLFileEntry =
			DLFileEntryLocalServiceUtil.fetchDLFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), _liveGroup.getGroupId());

		DLFileEntryType liveDLFileEntryType =
			DLFileEntryTypeLocalServiceUtil.getDLFileEntryType(
				liveDLFileEntry.getFileEntryTypeId());

		Assert.assertEquals(
			dlFileEntryType.getUuid(), liveDLFileEntryType.getUuid());

		DLAppLocalServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());

		_publish();

		Assert.assertNull(
			DLFileEntryLocalServiceUtil.fetchDLFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), _liveGroup.getGroupId()));
	}

	@Test
	@TestInfo({"LPD-113633", "LPS-90557"})
	public void testPublishDeletedFileEntryWithFileShortcut()
		throws Exception {

		FileEntry fileEntry = _addFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		DLAppLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		_publish();

		Assert.assertEquals(
			2, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());

		DLAppLocalServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());

		_publish();

		Assert.assertEquals(
			0, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());
	}

	@Test
	@TestInfo({"LPD-113633", "LPS-155794"})
	public void testPublishFileEntryWithURLTitleAndDisableStaging()
		throws Exception {

		byte[] bytes = RandomTestUtil.randomBytes();
		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), urlTitle, StringPool.BLANK,
			StringPool.BLANK, bytes, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		_publish();

		StagingLocalServiceUtil.disableStaging(
			_liveGroup,
			ServiceContextTestUtil.getServiceContext(_liveGroup.getGroupId()));

		Assert.assertFalse(
			GroupLocalServiceUtil.getGroup(
				_liveGroup.getGroupId()
			).hasStagingGroup());

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				_liveGroup.getGroupId(),
				PortalUtil.getClassNameId(FileEntry.class), urlTitle);

		Assert.assertNotNull(friendlyURLEntry);

		FileEntry liveFileEntry = DLAppLocalServiceUtil.getFileEntry(
			friendlyURLEntry.getClassPK());

		Assert.assertEquals(fileEntry.getUuid(), liveFileEntry.getUuid());
		Assert.assertArrayEquals(
			bytes, FileUtil.getBytes(liveFileEntry.getContentStream()));
	}

	@Test
	@TestInfo({"LPD-113633", "LPS-136824"})
	public void testPublishFolderMovedToRecycleBin() throws Exception {
		Folder folder = DLAppLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		_addFileEntry(folder.getFolderId());
		_addFileEntry(folder.getFolderId());

		_publish();

		Assert.assertEquals(
			1, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());

		DLTrashServiceUtil.moveFolderToTrash(folder.getFolderId());

		_publish();

		Assert.assertEquals(
			0, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());
	}

	private FileEntry _addFileEntry(long folderId) throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			folderId, RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomBytes(), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));
	}

	private void _addStagingAttribute(
		ServiceContext serviceContext, String key, Object value) {

		serviceContext.setAttribute(
			StagingConstants.STAGED_PREFIX + key + StringPool.DOUBLE_DASH,
			String.valueOf(value));
	}

	private int _getLiveFoldersAndFileEntriesAndFileShortcutsCount()
		throws Exception {

		return DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcutsCount(
			_liveGroup.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED, false);
	}

	private void _publish() throws Exception {
		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false,
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap());
	}

	@Inject
	private static FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private Group _stagingGroup;

}
