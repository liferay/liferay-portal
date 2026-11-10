/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class FileShortcutStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-113633")
	public void testExportImportStagedModelWhenFileEntryIsInAnotherGroup()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		FileShortcut fileShortcut = DLAppLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportStagedModel(fileShortcut);

		DLFileShortcut importedDLFileShortcut =
			DLFileShortcutLocalServiceUtil.getDLFileShortcutByUuidAndGroupId(
				fileShortcut.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			importedDLFileShortcut.getToFileEntryId());

		GroupLocalServiceUtil.deleteGroup(group);
	}

	@Test
	public void testFileEntryCheckedOut() throws Exception {
		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		List<StagedModel> fileEntries = dependentStagedModelsMap.get(
			DLFileEntry.class.getSimpleName());

		FileEntry fileEntry = (FileEntry)fileEntries.get(0);

		DLAppServiceUtil.checkOutFileEntry(
			fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		FileShortcut fileShortcut = (FileShortcut)addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, fileShortcut);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			FileShortcut exportedFileShortcut =
				(FileShortcut)readExportedStagedModel(fileShortcut);

			Assert.assertNull(exportedFileShortcut);
		}
	}

	@Test
	@TestInfo("LPD-113633")
	public void testUpdateFileShortcutTarget() throws Exception {
		FileEntry fileEntry1 = _addFileEntry();

		FileShortcut fileShortcut = DLAppLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry1.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportStagedModel(fileShortcut);

		FileEntry fileEntry2 = _addFileEntry();

		fileShortcut = DLAppLocalServiceUtil.updateFileShortcut(
			TestPropsValues.getUserId(), fileShortcut.getFileShortcutId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry2.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportStagedModel(fileShortcut);

		DLFileShortcut importedDLFileShortcut =
			DLFileShortcutLocalServiceUtil.getDLFileShortcutByUuidAndGroupId(
				fileShortcut.getUuid(), liveGroup.getGroupId());

		FileEntry importedFileEntry2 =
			DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
				fileEntry2.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			importedFileEntry2.getFileEntryId(),
			importedDLFileShortcut.getToFileEntryId());
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		Folder folder = DLAppServiceUtil.addFolder(
			null, group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		addDependentStagedModel(
			dependentStagedModelsMap, DLFolder.class, folder);

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);

		addDependentStagedModel(
			dependentStagedModelsMap, DLFileEntry.class, fileEntry);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> folderDependentStagedModels =
			dependentStagedModelsMap.get(DLFolder.class.getSimpleName());

		Folder folder = (Folder)folderDependentStagedModels.get(0);

		List<StagedModel> fileEntryDependentStagedModels =
			dependentStagedModelsMap.get(DLFileEntry.class.getSimpleName());

		FileEntry fileEntry = (FileEntry)fileEntryDependentStagedModels.get(0);

		return DLAppLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			folder.getFolderId(), fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected AssetEntry fetchAssetEntry(StagedModel stagedModel, Group group)
		throws Exception {

		return null;
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return DLFileShortcutLocalServiceUtil.getDLFileShortcutByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return DLFileShortcut.class;
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> fileEntryDependentStagedModels =
			dependentStagedModelsMap.get(DLFileEntry.class.getSimpleName());

		Assert.assertEquals(
			fileEntryDependentStagedModels.toString(), 1,
			fileEntryDependentStagedModels.size());

		FileEntry fileEntry = (FileEntry)fileEntryDependentStagedModels.get(0);

		DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
			fileEntry.getUuid(), group.getGroupId());

		List<StagedModel> folderDependentStagedModels =
			dependentStagedModelsMap.get(DLFolder.class.getSimpleName());

		Assert.assertEquals(
			folderDependentStagedModels.toString(), 1,
			folderDependentStagedModels.size());

		Folder folder = (Folder)folderDependentStagedModels.get(0);

		DLFolderLocalServiceUtil.getDLFolderByUuidAndGroupId(
			folder.getUuid(), group.getGroupId());
	}

	private FileEntry _addFileEntry() throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));
	}

}