/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.repository.portletrepository.PortletRepository;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class FileEntryStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCompanyScopeDependencies() throws Exception {
		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addCompanyDependencies();

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		exportImportStagedModel(stagedModel);

		validateCompanyDependenciesImport(dependentStagedModelsMap, liveGroup);
	}

	@Test
	public void testExportFileEntryFriendlyURLEntries() throws Exception {
		String fileName = "PDF_Test.pdf";

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null, serviceContext);

		fileEntry = _dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringPool.BLANK,
			ContentTypes.TEXT_PLAIN, fileEntry.getTitle(), "urltitle",
			StringPool.BLANK, StringPool.BLANK, DLVersionNumberIncrease.MINOR,
			(byte[])null, null, null, null, serviceContext);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertNotNull(friendlyURLEntry);
		Assert.assertEquals("urltitle", friendlyURLEntry.getUrlTitle());

		exportImportStagedModel(fileEntry);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), liveGroup.getGroupId());

		FriendlyURLEntry importedFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId());

		Assert.assertNotNull(importedFriendlyURLEntry);
		Assert.assertEquals(
			friendlyURLEntry.getUrlTitle(),
			importedFriendlyURLEntry.getUrlTitle());
	}

	@Test
	public void testExportFileEntryFriendlyURLEntriesNormalizedTitle()
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), liveGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), StringPool.BLANK,
			StringUtil.randomString(), StringUtil.randomString(), new byte[0],
			null, null, null,
			ServiceContextTestUtil.getServiceContext(
				liveGroup.getGroupId(), TestPropsValues.getUserId()));

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertNotNull(friendlyURLEntry);
		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(fileEntry.getTitle()),
			friendlyURLEntry.getUrlTitle());

		exportImportStagedModelFromLiveToStaging(fileEntry);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), liveGroup.getGroupId());

		FriendlyURLEntry importedFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId());

		Assert.assertNotNull(importedFriendlyURLEntry);
		Assert.assertEquals(
			friendlyURLEntry.getUrlTitle(),
			importedFriendlyURLEntry.getUrlTitle());
	}

	@Test
	public void testExportImportCheckedOutFileEntryAfterInitialPublication()
		throws Exception {

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			String fileName = "PDF_Test.pdf";

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					liveGroup.getGroupId(), TestPropsValues.getUserId());

			FileEntry fileEntry = _dlAppLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), liveGroup.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
				ContentTypes.APPLICATION_PDF,
				FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
				null, null, serviceContext);

			_dlFileEntryLocalService.checkOutFileEntry(
				TestPropsValues.getUserId(), fileEntry.getFileEntryId(),
				serviceContext);

			exportImportStagedModelFromLiveToStaging(fileEntry);

			FileEntry stagingGroupFileEntry =
				_dlAppLocalService.getFileEntryByUuidAndGroupId(
					fileEntry.getUuid(), stagingGroup.getGroupId());

			exportImportStagedModel(stagingGroupFileEntry);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	public void testExportImportFileEntryFriendlyURLEntries() throws Exception {
		String fileName = "PDF_Test.pdf";

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null, serviceContext);

		fileEntry = _dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringPool.BLANK,
			ContentTypes.TEXT_PLAIN, fileEntry.getTitle(), "urltitle",
			StringPool.BLANK, StringPool.BLANK, DLVersionNumberIncrease.MINOR,
			(byte[])null, null, null, null, serviceContext);

		FriendlyURLEntry mainFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertEquals("urltitle", mainFriendlyURLEntry.getUrlTitle());

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				fileEntry.getGroupId(), _portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertEquals(
			friendlyURLEntries.toString(), 2, friendlyURLEntries.size());

		exportImportStagedModel(fileEntry);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), liveGroup.getGroupId());

		FriendlyURLEntry mainImportedFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertEquals(
			"urltitle", mainImportedFriendlyURLEntry.getUrlTitle());

		List<FriendlyURLEntry> importedFriendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				importedFileEntry.getGroupId(),
				_portal.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId());

		Assert.assertEquals(
			importedFriendlyURLEntries.toString(), 2,
			importedFriendlyURLEntries.size());
	}

	@Test
	public void testExportImportFileEntryFriendlyURLEntriesUpdatingFileEntry()
		throws Exception {

		String fileName = "PDF_Test.pdf";

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null, serviceContext);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertNotNull(friendlyURLEntry);
		Assert.assertEquals("pdf_test-pdf", friendlyURLEntry.getUrlTitle());

		exportImportStagedModel(fileEntry);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), liveGroup.getGroupId());

		FriendlyURLEntry importedFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId());

		Assert.assertEquals(
			"pdf_test-pdf", importedFriendlyURLEntry.getUrlTitle());

		fileEntry = _dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringPool.BLANK,
			ContentTypes.TEXT_PLAIN, fileEntry.getTitle(), "urltitle",
			StringPool.BLANK, StringPool.BLANK, DLVersionNumberIncrease.MINOR,
			(byte[])null, null, null, null, serviceContext);

		exportImportStagedModel(fileEntry);

		importedFileEntry = _dlAppLocalService.getFileEntryByUuidAndGroupId(
			fileEntry.getUuid(), liveGroup.getGroupId());

		importedFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId());

		Assert.assertEquals("urltitle", importedFriendlyURLEntry.getUrlTitle());
	}

	@Test
	public void testExportImportFileExtension() throws Exception {
		String fileName = "PDF_Test.pdf";

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null, serviceContext);

		exportImportStagedModel(fileEntry);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals("pdf", importedFileEntry.getExtension());

		String title = RandomTestUtil.randomString() + ".awesome";

		fileEntry = _dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringPool.BLANK,
			ContentTypes.TEXT_PLAIN, title, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, DLVersionNumberIncrease.MINOR, (byte[])null, null,
			null, null, serviceContext);

		exportImportStagedModel(fileEntry);

		importedFileEntry = _dlAppLocalService.getFileEntryByUuidAndGroupId(
			fileEntry.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals("pdf", importedFileEntry.getExtension());
		Assert.assertEquals(title, importedFileEntry.getTitle());
	}

	@Test
	public void testExportImportRequiredFieldInDocumentType() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			liveGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		DLFileEntryType dlFileEntryType = addDLFileEntryType(
			liveGroup.getGroupId(), ddmStructure.getStructureId());

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			String fileName = "PDF_Test.pdf";

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					liveGroup.getGroupId(), TestPropsValues.getUserId());

			serviceContext.setAttribute(
				"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

			FileEntry fileEntry = _dlAppLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), liveGroup.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
				ContentTypes.APPLICATION_PDF,
				FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
				null, null, serviceContext);

			DDMForm ddmForm = ddmStructure.getDDMForm();

			List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

			ddmFormFields.forEach(
				ddmFormField -> ddmFormField.setRequired(true));

			DDMStructureLocalServiceUtil.updateStructure(
				ddmStructure.getUserId(), ddmStructure.getStructureId(),
				ddmForm, ddmStructure.getDDMFormLayout(), serviceContext);

			exportImportStagedModelFromLiveToStaging(fileEntry);

			FileEntry stagingGroupFileEntry =
				_dlAppLocalService.getFileEntryByUuidAndGroupId(
					fileEntry.getUuid(), stagingGroup.getGroupId());

			exportImportStagedModel(stagingGroupFileEntry);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	public void testExportsTheVersionAfterDeletingOnStaging() throws Exception {
		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			FileEntry fileEntry = addStagedModel(
				stagingGroup, addCompanyDependencies());

			exportImportStagedModel(fileEntry = addVersion(fileEntry));
			exportImportStagedModel(fileEntry = addVersion(fileEntry));
			exportImportStagedModel(fileEntry = _deleteLastVersion(fileEntry));

			FileEntry importedFileEntry = getStagedModel(
				fileEntry.getUuid(), liveGroup);

			Assert.assertEquals(
				fileEntry.getVersion(), importedFileEntry.getVersion());
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	public void testExportsTheVersionOnStaging() throws Exception {
		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			FileEntry fileEntry = addStagedModel(
				stagingGroup, addCompanyDependencies());

			fileEntry = addVersion(fileEntry);
			fileEntry = addVersion(fileEntry);
			fileEntry = addVersion(fileEntry);

			exportImportStagedModel(fileEntry);

			FileEntry importedFileEntry = getStagedModel(
				fileEntry.getUuid(), liveGroup);

			Assert.assertEquals(
				fileEntry.getVersion(), importedFileEntry.getVersion());
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	public void testImportFileEntryFriendlyURLEntries() throws Exception {
		String fileName = "PDF_Test.pdf";

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertNotNull(friendlyURLEntry);
		Assert.assertEquals("pdf_test-pdf", friendlyURLEntry.getUrlTitle());

		exportImportStagedModel(fileEntry);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), liveGroup.getGroupId());

		FriendlyURLEntry importedFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				importedFileEntry.getFileEntryId());

		Assert.assertEquals(
			"pdf_test-pdf", importedFriendlyURLEntry.getUrlTitle());
	}

	@Test
	public void testImportFileEntryWithExistingFileName() throws Exception {
		String fileName = "PDF_Test.pdf";

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), liveGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				liveGroup.getGroupId(), TestPropsValues.getUserId()));

		FileEntry stagingFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportStagedModel(stagingFileEntry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			portletDataContext.setDataStrategy(
				PortletDataHandlerKeys.DATA_STRATEGY_MIRROR);

			StagedModel exportedStagedModel = readExportedStagedModel(
				stagingFileEntry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);
		}

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				stagingFileEntry.getUuid(), liveGroup.getGroupId());

		Assert.assertNotEquals(fileName, importedFileEntry.getFileName());
	}

	@Test
	public void testsExportImportNondefaultRepository() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		Repository repository = RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			PortalUtil.getClassNameId(PortletRepository.class.getName()),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "com.liferay.blogs",
			"test repository", DLPortletKeys.DOCUMENT_LIBRARY,
			new UnicodeProperties(), true, serviceContext);

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), repository.getRepositoryId(),
			repository.getDlFolderId(), "testFolder", "test folder",
			serviceContext);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), repository.getRepositoryId(),
			folder.getFolderId(), "test.txt", "text/plain", new byte[] {0, 1},
			null, null, null, serviceContext);

		exportImportStagedModel(fileEntry);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), liveGroup.getGroupId());

		Folder importedFolder = _dlAppLocalService.getFolder(
			importedFileEntry.getFolderId());

		Repository importedRepository =
			RepositoryLocalServiceUtil.getRepository(
				importedFileEntry.getRepositoryId());

		Folder repositoryFolder = _dlAppLocalService.getFolder(
			importedRepository.getDlFolderId());

		Assert.assertEquals(
			importedRepository.getDlFolderId(),
			importedFolder.getParentFolderId());
		Assert.assertEquals(
			repositoryFolder.getRepositoryId(),
			importedRepository.getRepositoryId());
	}

	protected Map<String, List<StagedModel>> addCompanyDependencies()
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		Company company = CompanyLocalServiceUtil.fetchCompany(
			stagingGroup.getCompanyId());

		Group companyGroup = company.getGroup();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			companyGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		DLFileEntryType dlFileEntryType = addDLFileEntryType(
			companyGroup.getGroupId(), ddmStructure.getStructureId());

		addDependentStagedModel(
			dependentStagedModelsMap, DLFileEntryType.class, dlFileEntryType);

		Folder folder = _dlAppService.addFolder(
			null, stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		addDependentStagedModel(
			dependentStagedModelsMap, DLFolder.class, folder);

		return dependentStagedModelsMap;
	}

	protected DLFileEntryType addDLFileEntryType(
			long groupId, long ddmStructureId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), groupId, ddmStructureId,
				null,
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

		DDMStructure ddmStructure =
			DDMStructureLocalServiceUtil.getDDMStructure(ddmStructureId);

		ddmStructure.setStructureKey(
			DLUtil.getDDMStructureKey(dlFileEntryType));

		DDMStructureLocalServiceUtil.updateDDMStructure(ddmStructure);

		return dlFileEntryType;
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new LinkedHashMap<>();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		DLFileEntryType dlFileEntryType = addDLFileEntryType(
			group.getGroupId(), ddmStructure.getStructureId());

		addDependentStagedModel(
			dependentStagedModelsMap, DLFileEntryType.class, dlFileEntryType);

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		addDependentStagedModel(
			dependentStagedModelsMap, DLFolder.class, folder);

		return dependentStagedModelsMap;
	}

	@Override
	protected FileEntry addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> folderDependentStagedModels =
			dependentStagedModelsMap.get(DLFolder.class.getSimpleName());

		Folder folder = (Folder)folderDependentStagedModels.get(0);

		List<StagedModel> dlFileEntryTypeDependentStagedModels =
			dependentStagedModelsMap.get(DLFileEntryType.class.getSimpleName());

		DLFileEntryType dlFileEntryType =
			(DLFileEntryType)dlFileEntryTypeDependentStagedModels.get(0);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		DLAppTestUtil.populateServiceContext(
			serviceContext, dlFileEntryType.getFileEntryTypeId());

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			folder.getFolderId(), RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, TestDataConstants.TEST_BYTE_ARRAY, null,
			null, null, serviceContext);
	}

	@Override
	protected StagedModel addStagedModelWithExternalReferenceCode(
			Group group, String externalReferenceCode,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected FileEntry addVersion(StagedModel stagedModel) throws Exception {
		FileEntry fileEntry = (FileEntry)stagedModel;

		return _dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), StringPool.BLANK,
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			DLVersionNumberIncrease.MINOR, (byte[])null, null, null, null,
			ServiceContextThreadLocal.getServiceContext());
	}

	@Override
	protected FileEntry getStagedModel(String uuid, Group group)
		throws PortalException {

		return _dlAppLocalService.getFileEntryByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return DLFileEntry.class;
	}

	@Override
	protected boolean isCommentableStagedModel() {
		return true;
	}

	@Override
	protected boolean isVersionableStagedModel() {
		return true;
	}

	protected void validateCompanyDependenciesImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> ddmStructureDependentStagedModels =
			dependentStagedModelsMap.get(DDMStructure.class.getSimpleName());

		Assert.assertEquals(
			ddmStructureDependentStagedModels.toString(), 1,
			ddmStructureDependentStagedModels.size());

		DDMStructure ddmStructure =
			(DDMStructure)ddmStructureDependentStagedModels.get(0);

		Assert.assertNull(
			"Company DDM structure dependency should not be imported",
			DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(
				ddmStructure.getUuid(), group.getGroupId()));

		List<StagedModel> dlFileEntryTypesDependentStagedModels =
			dependentStagedModelsMap.get(DLFileEntryType.class.getSimpleName());

		Assert.assertEquals(
			dlFileEntryTypesDependentStagedModels.toString(), 1,
			dlFileEntryTypesDependentStagedModels.size());

		DLFileEntryType dlFileEntryType =
			(DLFileEntryType)dlFileEntryTypesDependentStagedModels.get(0);

		Assert.assertNull(
			"Company DL file entry dependency should not be imported",
			_dlFileEntryTypeLocalService.fetchDLFileEntryTypeByUuidAndGroupId(
				dlFileEntryType.getUuid(), group.getGroupId()));
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> ddmStructureDependentStagedModels =
			dependentStagedModelsMap.get(DDMStructure.class.getSimpleName());

		Assert.assertEquals(
			ddmStructureDependentStagedModels.toString(), 1,
			ddmStructureDependentStagedModels.size());

		DDMStructure ddmStructure =
			(DDMStructure)ddmStructureDependentStagedModels.get(0);

		DDMStructureLocalServiceUtil.getDDMStructureByUuidAndGroupId(
			ddmStructure.getUuid(), group.getGroupId());

		List<StagedModel> dlFileEntryTypesDependentStagedModels =
			dependentStagedModelsMap.get(DLFileEntryType.class.getSimpleName());

		Assert.assertEquals(
			dlFileEntryTypesDependentStagedModels.toString(), 1,
			dlFileEntryTypesDependentStagedModels.size());

		DLFileEntryType dlFileEntryType =
			(DLFileEntryType)dlFileEntryTypesDependentStagedModels.get(0);

		_dlFileEntryTypeLocalService.getDLFileEntryTypeByUuidAndGroupId(
			dlFileEntryType.getUuid(), group.getGroupId());

		List<StagedModel> foldersDependentStagedModels =
			dependentStagedModelsMap.get(DLFolder.class.getSimpleName());

		Assert.assertEquals(
			foldersDependentStagedModels.toString(), 1,
			foldersDependentStagedModels.size());

		Folder folder = (Folder)foldersDependentStagedModels.get(0);

		_dlFolderLocalService.getDLFolderByUuidAndGroupId(
			folder.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		DateTestUtil.assertEquals(
			stagedModel.getCreateDate(), importedStagedModel.getCreateDate());

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());

		FileEntry fileEntry = (FileEntry)stagedModel;
		FileEntry importedFileEntry = (FileEntry)importedStagedModel;

		Assert.assertEquals(
			fileEntry.getExternalReferenceCode(),
			importedFileEntry.getExternalReferenceCode());
		Assert.assertEquals(
			fileEntry.getFileName(), importedFileEntry.getFileName());
		Assert.assertEquals(
			fileEntry.getExtension(), importedFileEntry.getExtension());
		Assert.assertEquals(
			fileEntry.getMimeType(), importedFileEntry.getMimeType());
		Assert.assertEquals(fileEntry.getTitle(), importedFileEntry.getTitle());
		Assert.assertEquals(
			fileEntry.getDescription(), importedFileEntry.getDescription());
		Assert.assertEquals(fileEntry.getSize(), importedFileEntry.getSize());

		FileVersion latestFileVersion = null;

		try {
			latestFileVersion = fileEntry.getLatestFileVersion();
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}

			return;
		}

		FileVersion importedLatestFileVersion =
			importedFileEntry.getLatestFileVersion();

		Assert.assertEquals(
			latestFileVersion.getUuid(), importedLatestFileVersion.getUuid());
		Assert.assertEquals(
			latestFileVersion.getFileName(),
			importedLatestFileVersion.getFileName());
		Assert.assertEquals(
			latestFileVersion.getExtension(),
			importedLatestFileVersion.getExtension());
		Assert.assertEquals(
			latestFileVersion.getMimeType(),
			importedLatestFileVersion.getMimeType());
		Assert.assertEquals(
			latestFileVersion.getTitle(), importedLatestFileVersion.getTitle());
		Assert.assertEquals(
			latestFileVersion.getDescription(),
			importedLatestFileVersion.getDescription());
		Assert.assertEquals(
			latestFileVersion.getSize(), importedLatestFileVersion.getSize());
		Assert.assertEquals(
			latestFileVersion.getStatus(),
			importedLatestFileVersion.getStatus());
	}

	private FileEntry _deleteLastVersion(FileEntry fileEntry) throws Exception {
		_dlFileEntryLocalService.deleteFileVersion(
			TestPropsValues.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getVersion());

		return _dlAppLocalService.getFileEntry(fileEntry.getFileEntryId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileEntryStagedModelDataHandlerTest.class);

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Inject
	private Portal _portal;

}