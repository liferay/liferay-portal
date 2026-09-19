/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
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
public class FolderStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCompanyScopeDependencies() throws Exception {
		initExport();

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addCompanyDependencies();

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				stagedModel);

			Assert.assertNotNull(exportedStagedModel);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);

			validateCompanyDependenciesImport(
				dependentStagedModelsMap, liveGroup);
		}
	}

	@Test
	public void testFolderWithoutParentFolder() throws Exception {
		initExport();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(stagingGroup.getGroupId());

		Folder parentFolder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		Folder childFolder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			parentFolder.getFolderId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, childFolder);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			Folder exportedChildFolder = (Folder)readExportedStagedModel(
				childFolder);

			Assert.assertNotNull(exportedChildFolder);

			Folder exportedParentFolder = (Folder)readExportedStagedModel(
				parentFolder);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedParentFolder);

			Map<Long, Long> folderIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Folder.class);

			folderIds.remove(parentFolder.getFolderId());

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedChildFolder);

			Folder importedFolder =
				_dlAppLocalService.getFolderByExternalReferenceCode(
					childFolder.getExternalReferenceCode(),
					liveGroup.getGroupId());

			Assert.assertEquals(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				importedFolder.getParentFolderId());
		}
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

		Folder folder = DLAppServiceUtil.addFolder(
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
			DLFileEntryTypeLocalServiceUtil.addFileEntryType(
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
			new HashMap<>();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		DLFileEntryType dlFileEntryType = addDLFileEntryType(
			group.getGroupId(), ddmStructure.getStructureId());

		addDependentStagedModel(
			dependentStagedModelsMap, DLFileEntryType.class, dlFileEntryType);

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
	protected StagedModel addStagedModel(
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

		folder = DLAppServiceUtil.addFolder(
			null, group.getGroupId(), folder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		DLFolder dlFolder = (DLFolder)folder.getModel();

		dlFolder.setDefaultFileEntryTypeId(
			dlFileEntryType.getFileEntryTypeId());
		dlFolder.setRestrictionType(
			DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW);

		dlFolder = DLFolderLocalServiceUtil.updateDLFolder(dlFolder);

		DLFileEntryTypeLocalServiceUtil.updateFolderFileEntryTypes(
			dlFolder, ListUtil.fromArray(dlFileEntryType.getFileEntryTypeId()),
			dlFileEntryType.getFileEntryTypeId(), serviceContext);

		return folder;
	}

	@Override
	protected StagedModel addStagedModelWithExternalReferenceCode(
			Group group, String externalReferenceCode,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return DLAppLocalServiceUtil.addFolder(
			externalReferenceCode, TestPropsValues.getUserId(),
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void deleteStagedModel(
			StagedModel stagedModel,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dlFileEntryTypestagedModels =
			dependentStagedModelsMap.get(DLFileEntryType.class.getSimpleName());

		StagedModelDataHandler stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				DLFileEntryType.class.getName());

		for (StagedModel dlFileEntryTypeStagedModel :
				dlFileEntryTypestagedModels) {

			stagedModelDataHandler.deleteStagedModel(
				dlFileEntryTypeStagedModel);
		}

		super.deleteStagedModel(stagedModel, dependentStagedModelsMap, group);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		DLFolder dlFolder =
			DLFolderLocalServiceUtil.getDLFolderByUuidAndGroupId(
				uuid, group.getGroupId());

		return new LiferayFolder(dlFolder);
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return DLFolder.class;
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
			"Company DL file entry should not be imported",
			DLFileEntryTypeLocalServiceUtil.
				fetchDLFileEntryTypeByUuidAndGroupId(
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

		DLFileEntryTypeLocalServiceUtil.getDLFileEntryTypeByUuidAndGroupId(
			dlFileEntryType.getUuid(), group.getGroupId());

		List<StagedModel> folderDependentStagedModels =
			dependentStagedModelsMap.get(DLFolder.class.getSimpleName());

		Assert.assertEquals(
			folderDependentStagedModels.toString(), 1,
			folderDependentStagedModels.size());

		Folder parentFolder = (Folder)folderDependentStagedModels.get(0);

		DLFolderLocalServiceUtil.getDLFolderByUuidAndGroupId(
			parentFolder.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		DateTestUtil.assertEquals(
			stagedModel.getCreateDate(), importedStagedModel.getCreateDate());

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());

		Folder folder = (Folder)stagedModel;
		Folder importedFolder = (Folder)importedStagedModel;

		Assert.assertEquals(
			folder.getExternalReferenceCode(),
			importedFolder.getExternalReferenceCode());

		Assert.assertEquals(folder.getName(), importedFolder.getName());
		Assert.assertEquals(
			folder.getDescription(), importedFolder.getDescription());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

}