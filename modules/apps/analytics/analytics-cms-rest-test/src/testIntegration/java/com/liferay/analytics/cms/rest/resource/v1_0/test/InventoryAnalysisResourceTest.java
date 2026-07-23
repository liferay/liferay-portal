/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.InventoryAnalysis;
import com.liferay.analytics.cms.rest.client.dto.v1_0.InventoryAnalysisItem;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class InventoryAnalysisResourceTest
	extends BaseInventoryAnalysisResourceTestCase {

	@ClassRule
	@Rule
	public static final PermissionCheckerMethodTestRule
		permissionCheckerMethodTestRule =
			PermissionCheckerMethodTestRule.INSTANCE;

	@Override
	@Test
	public void testGetInventoryAnalysis() throws Exception {
		_setUpCMSContext();

		InventoryAnalysis inventoryAnalysis =
			inventoryAnalysisResource.getInventoryAnalysis(
				null, _depotEntry.getDepotEntryId(), null, null, null, null,
				null, null, null, null, null);

		_assertInventoryAnalysis(inventoryAnalysis, 3, 5L, null);

		inventoryAnalysis = inventoryAnalysisResource.getInventoryAnalysis(
			null, _depotEntry.getDepotEntryId(), null, null, null, null, null,
			_webContentDefinition.getObjectDefinitionId(), null, null, null);

		_assertInventoryAnalysis(inventoryAnalysis, 1, 3L, "Basic Web Content");

		inventoryAnalysis = inventoryAnalysisResource.getInventoryAnalysis(
			null, _depotEntry.getDepotEntryId(), null, null, null, null, null,
			_documentDefinition.getObjectDefinitionId(), null, null, null);

		_assertInventoryAnalysis(inventoryAnalysis, 1, 1L, "Basic Document");

		inventoryAnalysis = inventoryAnalysisResource.getInventoryAnalysis(
			null, _depotEntry.getDepotEntryId(), null, null, null, null, null,
			_externalVideoDefinition.getObjectDefinitionId(), null, null, null);

		_assertInventoryAnalysis(inventoryAnalysis, 1, 1L, "External Video");

		inventoryAnalysis = inventoryAnalysisResource.getInventoryAnalysis(
			null, _depotEntry.getDepotEntryId(), "category", null, null, null,
			null, null, null, null, null);

		Assert.assertEquals(
			2L, (long)inventoryAnalysis.getInventoryAnalysisItemsCount());

		InventoryAnalysisItem[] inventoryAnalysisItems =
			inventoryAnalysis.getInventoryAnalysisItems();

		Assert.assertEquals(
			Arrays.toString(inventoryAnalysisItems), 2,
			inventoryAnalysisItems.length);

		InventoryAnalysisItem inventoryAnalysisItem = inventoryAnalysisItems[0];

		Assert.assertEquals(4L, (long)inventoryAnalysisItem.getCount());

		Assert.assertEquals("Unknown", inventoryAnalysisItem.getTitle());

		inventoryAnalysisItem = inventoryAnalysisItems[1];

		Assert.assertEquals(1L, (long)inventoryAnalysisItem.getCount());

		Assert.assertEquals("Category", inventoryAnalysisItem.getTitle());

		inventoryAnalysis = inventoryAnalysisResource.getInventoryAnalysis(
			_assetCategory.getCategoryId(), _depotEntry.getDepotEntryId(),
			"category", null, null, null, null, null, null, null, null);

		Assert.assertEquals(
			1L, (long)inventoryAnalysis.getInventoryAnalysisItemsCount());

		inventoryAnalysisItems = inventoryAnalysis.getInventoryAnalysisItems();

		Assert.assertEquals(
			Arrays.toString(inventoryAnalysisItems), 1,
			inventoryAnalysisItems.length);

		inventoryAnalysisItem = inventoryAnalysisItems[0];

		Assert.assertEquals(1L, (long)inventoryAnalysisItem.getCount());

		Assert.assertEquals("Category", inventoryAnalysisItem.getTitle());
	}

	private void _assertInventoryAnalysis(
			InventoryAnalysis inventoryAnalysis, int expectedItemsCount,
			long expectedTotalCount, String expectedTitle)
		throws Exception {

		InventoryAnalysisItem[] inventoryAnalysisItems =
			inventoryAnalysis.getInventoryAnalysisItems();

		Assert.assertEquals(
			Arrays.toString(inventoryAnalysisItems), expectedItemsCount,
			inventoryAnalysisItems.length);

		Assert.assertEquals(
			expectedTotalCount, (long)inventoryAnalysis.getTotalCount());

		if (expectedTitle != null) {
			InventoryAnalysisItem inventoryAnalysisItem =
				inventoryAnalysisItems[0];

			Assert.assertEquals(
				expectedTitle, inventoryAnalysisItem.getTitle());
		}
	}

	private void _setUpCMSContext() throws Exception {
		CMSTestUtil.getOrAddGroup(InventoryAnalysisResourceTest.class);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testGroup.getGroupId(), TestPropsValues.getUserId());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE, _serviceContext);

		_webContentDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", testCompany.getCompanyId());

		Map<String, Serializable> webContentValues =
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build();

		_objectEntries.add(
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry.getGroupId(), _webContentDefinition,
				webContentValues));

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(), "Vocabulary",
			_serviceContext);

		_assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(), "Category",
			_assetVocabulary.getVocabularyId(), _serviceContext);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), _webContentDefinition, webContentValues);

		_objectEntries.add(objectEntry);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			_webContentDefinition.getClassName(),
			objectEntry.getObjectEntryId());

		_assetEntryAssetCategoryRel =
			_assetEntryAssetCategoryRelLocalService.
				addAssetEntryAssetCategoryRel(
					assetEntry.getEntryId(), _assetCategory.getCategoryId());

		_objectEntries.add(
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry.getGroupId(), _webContentDefinition,
				webContentValues, RandomTestUtil.randomString()));

		DLFolder dlFolder = _dlFolderLocalService.addFolder(
			null, TestPropsValues.getUserId(), _depotEntry.getGroupId(),
			_depotEntry.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			_serviceContext);

		byte[] bytes = FileUtil.getBytes(getClass(), "dependencies/test.pdf");

		String fileName = RandomTestUtil.randomString() + ".pdf";

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), dlFolder.getGroupId(),
			dlFolder.getGroupId(), dlFolder.getFolderId(), fileName,
			ContentTypes.APPLICATION_PDF, fileName, fileName, "", "",
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(bytes), bytes.length, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(dlFolder.getGroupId()));

		_documentDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", testCompany.getCompanyId());

		_objectEntries.add(
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry.getGroupId(), _documentDefinition,
				HashMapBuilder.<String, Serializable>put(
					"file", String.valueOf(dlFileEntry.getFileEntryId())
				).put(
					"title_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).build()
				).build()));

		_externalVideoDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_EXTERNAL_VIDEO", testCompany.getCompanyId());

		_objectEntries.add(
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry.getGroupId(), _externalVideoDefinition,
				HashMapBuilder.<String, Serializable>put(
					"title_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).build()
				).put(
					"videoURL", "https://www.youtube.com/watch?v=HOdbzGCI5ME"
				).build()));
	}

	@DeleteAfterTestRun
	private AssetCategory _assetCategory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@DeleteAfterTestRun
	private AssetEntryAssetCategoryRel _assetEntryAssetCategoryRel;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	private ObjectDefinition _documentDefinition;
	private ObjectDefinition _externalVideoDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private List<ObjectEntry> _objectEntries = new ArrayList<>();

	private ServiceContext _serviceContext;
	private ObjectDefinition _webContentDefinition;

}