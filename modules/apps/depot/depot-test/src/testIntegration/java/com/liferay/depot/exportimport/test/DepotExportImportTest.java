/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslatorUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime Leon
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class DepotExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _addDepotEntry();

		_depotGroup = _depotEntry.getGroup();

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		if ((_larFile != null) && _larFile.exists()) {
			FileUtil.delete(_larFile);
		}
	}

	@Test
	public void testExportImportDepotWithAssetListEntryToDepot()
		throws Exception {

		AssetListEntry assetListEntry = _addAssetListEntry(
			_depotGroup.getGroupId());

		_larFile = _export(_depotGroup.getGroupId());

		Group importedDepotGroup = _addImportedDepotGroup();

		_import(importedDepotGroup.getGroupId(), _larFile);

		_assertImportedAssetListEntry(
			assetListEntry, importedDepotGroup.getGroupId());
	}

	@Test
	public void testExportImportDepotWithAssetListEntryToGroup()
		throws Exception {

		AssetListEntry assetListEntry = _addAssetListEntry(
			_depotGroup.getGroupId());

		_larFile = _export(_depotGroup.getGroupId());

		_import(_group.getGroupId(), _larFile);

		_assertImportedAssetListEntry(assetListEntry, _group.getGroupId());
	}

	@Test
	public void testExportImportDepotWithDLFileEntryTypeToDepot()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_depotGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		DDMStructure metadataSetDDMStructure =
			DDMStructureTestUtil.addStructure(
				_depotGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_depotGroup.getGroupId());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _depotGroup.getGroupId(),
				ddmStructure.getStructureId(), null,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

		_dlFileEntryTypeLocalService.addDDMStructureLinks(
			dlFileEntryType.getFileEntryTypeId(),
			SetUtil.fromArray(
				new long[] {
					ddmStructure.getStructureId(),
					metadataSetDDMStructure.getStructureId()
				}));

		DLAppTestUtil.populateServiceContext(
			serviceContext, dlFileEntryType.getFileEntryTypeId());

		DDMFormValues ddmFormValues = DDMBeanTranslatorUtil.translate(
			DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(
				ddmStructure.getDDMForm()));

		serviceContext.setAttribute(
			DDMFormValues.class.getName() + StringPool.POUND +
				ddmStructure.getStructureId(),
			ddmFormValues);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _depotGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);

		_larFile = _export(_depotGroup.getGroupId());

		Group importedDepotGroup = _addImportedDepotGroup();

		_import(importedDepotGroup.getGroupId(), _larFile);

		DLFileEntryType importedDLFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryTypeByUuidAndGroupId(
				dlFileEntryType.getUuid(), importedDepotGroup.getGroupId());

		Assert.assertEquals(
			dlFileEntryType.getName(), importedDLFileEntryType.getName());

		DDMStructure importedDDMStructure =
			_ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
				ddmStructure.getUuid(), importedDepotGroup.getGroupId());

		Assert.assertEquals(
			importedDDMStructure.getStructureId(),
			importedDLFileEntryType.getDataDefinitionId());

		List<DDMStructure> importedDDMStructures =
			DLFileEntryTypeUtil.getDDMStructures(importedDLFileEntryType);

		Assert.assertEquals(
			importedDDMStructures.toString(), 2, importedDDMStructures.size());
		Assert.assertTrue(importedDDMStructures.contains(importedDDMStructure));

		DDMStructure importedMetadataSetDDMStructure =
			_ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
				metadataSetDDMStructure.getUuid(),
				importedDepotGroup.getGroupId());

		Assert.assertTrue(
			importedDDMStructures.contains(importedMetadataSetDDMStructure));

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), importedDepotGroup.getGroupId());

		DLFileEntry importedDLFileEntry =
			(DLFileEntry)importedFileEntry.getModel();

		Assert.assertEquals(
			importedDLFileEntryType.getFileEntryTypeId(),
			importedDLFileEntry.getFileEntryTypeId());

		FileVersion importedFileVersion = importedFileEntry.getFileVersion();

		DLFileEntryMetadata importedDLFileEntryMetadata =
			_dlFileEntryMetadataLocalService.getFileEntryMetadata(
				importedDDMStructure.getStructureId(),
				importedFileVersion.getFileVersionId());

		Assert.assertEquals(
			ddmFormValues,
			DDMBeanTranslatorUtil.translate(
				_ddmStorageEngineManager.getDDMFormValues(
					importedDLFileEntryMetadata.getDDMStorageId())));
	}

	@Test
	public void testExportImportDepotWithFileEntryToGroup() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			_depotGroup.getGroupId());

		_larFile = _export(_depotGroup.getGroupId());

		_import(_group.getGroupId(), _larFile);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), _group.getGroupId());

		Assert.assertEquals(fileEntry.getTitle(), importedFileEntry.getTitle());
	}

	@Test
	public void testExportImportDepotWithJournalArticleToGroup()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_larFile = _export(_depotGroup.getGroupId());

		_import(_group.getGroupId(), _larFile);

		JournalArticle importedJournalArticle =
			_journalArticleLocalService.fetchJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), _group.getGroupId());

		Assert.assertEquals(
			journalArticle.getTitle(), importedJournalArticle.getTitle());
	}

	@Test
	public void testExportImportDepotWithJournalDDMStructureToDepot()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_larFile = _export(_depotGroup.getGroupId());

		Group importedDepotGroup = _addImportedDepotGroup();

		_import(importedDepotGroup.getGroupId(), _larFile);

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		DDMStructure importedDDMStructure =
			_ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
				ddmStructure.getUuid(), importedDepotGroup.getGroupId());

		Assert.assertEquals(
			ddmStructure.getName(), importedDDMStructure.getName());
		Assert.assertEquals(
			ddmStructure.getStructureKey(),
			importedDDMStructure.getStructureKey());

		DDMTemplate ddmTemplate = journalArticle.getDDMTemplate();

		DDMTemplate importedDDMTemplate =
			_ddmTemplateLocalService.fetchDDMTemplateByUuidAndGroupId(
				ddmTemplate.getUuid(), importedDepotGroup.getGroupId());

		Assert.assertEquals(
			importedDDMStructure.getStructureId(),
			importedDDMTemplate.getClassPK());
		Assert.assertEquals(
			ddmTemplate.getDescription(), importedDDMTemplate.getDescription());
		Assert.assertEquals(
			ddmTemplate.getName(), importedDDMTemplate.getName());
		Assert.assertEquals(
			ddmTemplate.getTemplateKey(), importedDDMTemplate.getTemplateKey());

		JournalArticle importedJournalArticle =
			_journalArticleLocalService.fetchJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), importedDepotGroup.getGroupId());

		Assert.assertEquals(
			journalArticle.getTitle(), importedJournalArticle.getTitle());
	}

	@Test
	public void testExportImportGroupWithAssetListEntryToDepot()
		throws Exception {

		AssetListEntry assetListEntry = _addAssetListEntry(_group.getGroupId());

		_larFile = _export(_group.getGroupId());

		_import(_depotGroup.getGroupId(), _larFile);

		_assertImportedAssetListEntry(assetListEntry, _depotGroup.getGroupId());
	}

	@Test
	public void testExportImportGroupWithFileEntryToDepot() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		_larFile = _export(_group.getGroupId());

		_import(_depotGroup.getGroupId(), _larFile);

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), _depotGroup.getGroupId());

		Assert.assertEquals(fileEntry.getTitle(), importedFileEntry.getTitle());
	}

	@Test
	public void testExportImportGroupWithJournalArticleToDepot()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_larFile = _export(_group.getGroupId());

		_import(_depotGroup.getGroupId(), _larFile);

		JournalArticle importedJournalArticle =
			_journalArticleLocalService.fetchJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), _depotGroup.getGroupId());

		Assert.assertEquals(
			journalArticle.getTitle(), importedJournalArticle.getTitle());
	}

	private AssetListEntry _addAssetListEntry(long groupId) throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			groupId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		return _assetListEntryLocalService.addManualAssetListEntry(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), new long[] {assetEntry.getEntryId()},
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	private Group _addImportedDepotGroup() throws Exception {
		_importedDepotEntry = _addDepotEntry();

		return _importedDepotEntry.getGroup();
	}

	private void _assertImportedAssetListEntry(
			AssetListEntry assetListEntry, long groupId)
		throws Exception {

		AssetListEntry importedAssetListEntry =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				assetListEntry.getUuid(), groupId);

		Assert.assertEquals(
			assetListEntry.getTitle(), importedAssetListEntry.getTitle());
		Assert.assertEquals(
			assetListEntry.getType(), importedAssetListEntry.getType());

		AssetEntry assetEntry = _getAssetEntry(assetListEntry);
		AssetEntry importedAssetEntry = _getAssetEntry(importedAssetListEntry);

		Assert.assertEquals(
			assetEntry.getClassUuid(), importedAssetEntry.getClassUuid());
		Assert.assertEquals(groupId, importedAssetEntry.getGroupId());
	}

	private File _export(long groupId) throws Exception {
		return _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false,
							new long[0], _getParameterMap())));
	}

	private AssetEntry _getAssetEntry(AssetListEntry assetListEntry)
		throws Exception {

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetListEntryAssetEntryRels.toString(), 1,
			assetListEntryAssetEntryRels.size());

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRels.get(0);

		return _assetEntryLocalService.getEntry(
			assetListEntryAssetEntryRel.getAssetEntryId());
	}

	private Map<String, String[]> _getParameterMap() {
		return HashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE}
		).put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private void _import(long groupId, File larFile) throws Exception {
		_exportImportLocalService.importLayouts(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false, null,
							_getParameterMap())),
			larFile);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _depotGroup;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private DepotEntry _importedDepotEntry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private File _larFile;

}