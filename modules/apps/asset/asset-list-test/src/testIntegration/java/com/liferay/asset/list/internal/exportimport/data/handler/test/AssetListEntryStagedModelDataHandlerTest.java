/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.list.constants.AssetListConstants;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class AssetListEntryStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo(
		{
			"LPD-86116", "LPD-86506", "LPD-102486", "LPD-103053", "LPD-106171",
			"LPD-106967"
		}
	)
	public void testExportImportAssetListEntry() throws Exception {
		_testExportImportAssetListEntryWithAnyClassType();
		_testExportImportAssetListEntryWithAssetCategoryFilters();
		_testExportImportAssetListEntryWithLegacyQueryRules();
		_testExportImportAssetListEntryWithNonexistentClassName();
		_testExportImportAssetListEntryWithNonexistentClassNames();
		_testExportImportAssetListEntryWithOnlyNonexistentClassNameIds();
		_testExportImportAssetListEntryWithSegmentsEntry();
		_testExportImportAssetListEntryWithStaleAnyAssetTypeClassName();
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return _assetListEntryLocalService.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			stagingGroup.getGroupId(), RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));
	}

	@Override
	protected void exportStagedModel(StagedModel stagedModel) throws Exception {
		initExport();

		try {
			ExportImportThreadLocal.setPortletExportInProcess(true);
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, stagedModel);
		}
		finally {
			ExportImportThreadLocal.setPortletExportInProcess(false);
		}
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return _assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return AssetListEntry.class;
	}

	@Override
	protected SafeCloseable initImportWithSafeCloseable(
			Group exportGroup, Group importGroup)
		throws Exception {

		SafeCloseable safeCloseable = super.initImportWithSafeCloseable(
			exportGroup, importGroup);

		ExportImportThreadLocal.setPortletImportInProcess(true);

		return () -> {
			try {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}
			finally {
				safeCloseable.close();
			}
		};
	}

	private AssetListEntry _addAssetListEntry(String typeSettings)
		throws Exception {

		AssetListEntry assetListEntry = (AssetListEntry)addStagedModel(
			stagingGroup, Collections.emptyMap());

		_assetListEntrySegmentsEntryRelLocalService.
			updateAssetListEntrySegmentsEntryRelTypeSettings(
				assetListEntry.getAssetListEntryId(),
				SegmentsEntryConstants.ID_DEFAULT, typeSettings);

		return assetListEntry;
	}

	private SegmentsEntry _addSegmentsEntryByFirstName(
			String firstName, long groupId)
		throws Exception {

		Criteria criteria = new Criteria();

		_segmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", firstName),
			Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			groupId, CriteriaSerializer.serialize(criteria));
	}

	private void _assertEquals(
			AssetListEntry assetListEntry,
			AssetListEntry importedAssetListEntry)
		throws Exception {

		Assert.assertEquals(
			assetListEntry.getExternalReferenceCode(),
			importedAssetListEntry.getExternalReferenceCode());
		Assert.assertEquals(
			assetListEntry.getCreateDate(),
			importedAssetListEntry.getCreateDate());
		Assert.assertEquals(
			assetListEntry.getModifiedDate(),
			importedAssetListEntry.getModifiedDate());
		Assert.assertEquals(
			assetListEntry.getAssetListEntryKey(),
			importedAssetListEntry.getAssetListEntryKey());
		Assert.assertEquals(
			assetListEntry.getAssetEntrySubtype(),
			importedAssetListEntry.getAssetEntrySubtype());
		Assert.assertEquals(
			assetListEntry.getAssetEntryType(),
			importedAssetListEntry.getAssetEntryType());
	}

	private UnicodeProperties _exportImportAssetListEntry(String typeSettings)
		throws Exception {

		AssetListEntry assetListEntry = _addAssetListEntry(typeSettings);

		exportImportStagedModel(assetListEntry);

		return _getImportedTypeSettingsUnicodeProperties(assetListEntry);
	}

	private UnicodeProperties _getImportedTypeSettingsUnicodeProperties(
			AssetListEntry assetListEntry)
		throws Exception {

		AssetListEntry importedAssetListEntry = (AssetListEntry)getStagedModel(
			assetListEntry.getUuid(), liveGroup);

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					importedAssetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT);

		return UnicodePropertiesBuilder.load(
			assetListEntrySegmentsEntryRel.getTypeSettings()
		).build();
	}

	private void _testExportImportAssetListEntryWithAnyClassType()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			stagingGroup.getGroupId(), JournalArticle.class.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(liveGroup.getGroupId());

		serviceContext.setUuid(ddmStructure.getUuid());

		DDMStructure importedDDMStructure = DDMStructureTestUtil.addStructure(
			liveGroup.getGroupId(), JournalArticle.class.getName(), 0,
			ddmStructure.getDDMForm(), LocaleUtil.getSiteDefault(),
			serviceContext);

		Assert.assertNotEquals(
			ddmStructure.getStructureId(),
			importedDDMStructure.getStructureId());

		AssetListEntry assetListEntry = _addAssetListEntry(
			UnicodePropertiesBuilder.put(
				"anyAssetType",
				String.valueOf(
					_classNameLocalService.getClassNameId(
						JournalArticle.class.getName()))
			).put(
				"anyClassTypeJournalArticleAssetRendererFactory",
				String.valueOf(ddmStructure.getStructureId())
			).buildString());

		exportStagedModel(assetListEntry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable(
				stagingGroup, liveGroup)) {

			Map<Long, Long> ddmStructureIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					DDMStructure.class);

			ddmStructureIds.put(
				ddmStructure.getStructureId(),
				importedDDMStructure.getStructureId());

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, readExportedStagedModel(assetListEntry));
		}

		UnicodeProperties unicodeProperties =
			_getImportedTypeSettingsUnicodeProperties(assetListEntry);

		Assert.assertEquals(
			String.valueOf(importedDDMStructure.getStructureId()),
			unicodeProperties.getProperty(
				"anyClassTypeJournalArticleAssetRendererFactory"));
	}

	private void _testExportImportAssetListEntryWithAssetCategoryFilters()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			stagingGroup.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			stagingGroup.getGroupId(), assetVocabulary.getVocabularyId());

		UnicodeProperties unicodeProperties = _exportImportAssetListEntry(
			UnicodePropertiesBuilder.put(
				"filters",
				JSONUtil.putAll(
					JSONUtil.put(
						"operatorName", "contains"
					).put(
						"propertyName", "assetCategories"
					).put(
						"quantifier", "any"
					).put(
						"value",
						JSONUtil.putAll(
							JSONUtil.put(
								"value",
								String.valueOf(assetCategory.getCategoryId())))
					)
				).toString()
			).buildString());

		JSONArray filtersJSONArray = _jsonFactory.createJSONArray(
			unicodeProperties.getProperty("filters"));

		Assert.assertEquals(
			filtersJSONArray.toString(), 1, filtersJSONArray.length());

		AssetCategory importedAssetCategory =
			_assetCategoryLocalService.fetchAssetCategoryByUuidAndGroupId(
				assetCategory.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			String.valueOf(importedAssetCategory.getCategoryId()),
			JSONUtil.getValueAsString(
				filtersJSONArray, "JSONObject/0", "JSONArray/value",
				"JSONObject/0", "Object/value"));
	}

	private void _testExportImportAssetListEntryWithLegacyQueryRules()
		throws Exception {

		String assetTagName = RandomTestUtil.randomString();
		String keyword = RandomTestUtil.randomString();

		UnicodeProperties unicodeProperties = _exportImportAssetListEntry(
			UnicodePropertiesBuilder.put(
				"queryAndOperator0", "false"
			).put(
				"queryAndOperator1", "true"
			).put(
				"queryContains0", "true"
			).put(
				"queryContains1", "true"
			).put(
				"queryName0", "assetTags"
			).put(
				"queryName1", "keywords"
			).put(
				"queryValues0", assetTagName
			).put(
				"queryValues1", keyword
			).buildString());

		for (String key : unicodeProperties.keySet()) {
			Assert.assertFalse(key, key.startsWith("query"));
		}

		JSONArray filtersJSONArray = _jsonFactory.createJSONArray(
			unicodeProperties.getProperty("filters"));

		Assert.assertEquals(
			filtersJSONArray.toString(), 2, filtersJSONArray.length());
		Assert.assertEquals(
			"assetTags",
			JSONUtil.getValueAsString(
				filtersJSONArray, "JSONObject/0", "Object/propertyName"));
		Assert.assertEquals(
			assetTagName,
			JSONUtil.getValueAsString(
				filtersJSONArray, "JSONObject/0", "JSONArray/value",
				"JSONObject/0", "Object/value"));
		Assert.assertEquals(
			"keywords",
			JSONUtil.getValueAsString(
				filtersJSONArray, "JSONObject/1", "Object/propertyName"));
		Assert.assertEquals(
			keyword,
			JSONUtil.getValueAsString(
				filtersJSONArray, "JSONObject/1", "Object/value"));
	}

	private void _testExportImportAssetListEntryWithNonexistentClassName()
		throws Exception {

		long assetEntryClassNameId = _classNameLocalService.getClassNameId(
			AssetEntry.class.getName());

		long nonexistentClassNameId = RandomTestUtil.randomLong();

		UnicodeProperties unicodeProperties = _exportImportAssetListEntry(
			UnicodePropertiesBuilder.put(
				"anyAssetType", String.valueOf(nonexistentClassNameId)
			).put(
				"classNameIds",
				StringUtil.merge(
					new long[] {assetEntryClassNameId, nonexistentClassNameId})
			).buildString());

		Assert.assertEquals(
			String.valueOf(AssetListConstants.CLASS_NAME_ID_NONEXISTENT),
			unicodeProperties.getProperty("anyAssetType"));
		Assert.assertEquals(
			String.valueOf(assetEntryClassNameId),
			unicodeProperties.getProperty("classNameIds"));
	}

	private void _testExportImportAssetListEntryWithNonexistentClassNames()
		throws Exception {

		UnicodeProperties unicodeProperties = _exportImportAssetListEntry(
			UnicodePropertiesBuilder.put(
				"anyAssetType", String.valueOf(RandomTestUtil.randomLong())
			).put(
				"classNameIds",
				StringUtil.merge(
					new long[] {
						RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
					})
			).buildString());

		Assert.assertEquals(
			String.valueOf(AssetListConstants.CLASS_NAME_ID_NONEXISTENT),
			unicodeProperties.getProperty("anyAssetType"));
		Assert.assertNull(unicodeProperties.getProperty("classNameIds"));
	}

	private void _testExportImportAssetListEntryWithOnlyNonexistentClassNameIds()
		throws Exception {

		UnicodeProperties unicodeProperties = _exportImportAssetListEntry(
			UnicodePropertiesBuilder.put(
				"anyAssetType", Boolean.FALSE.toString()
			).put(
				"classNameIds",
				StringUtil.merge(
					new long[] {
						RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
					})
			).buildString());

		Assert.assertEquals(
			Boolean.FALSE.toString(),
			unicodeProperties.getProperty("anyAssetType"));
		Assert.assertEquals(
			String.valueOf(AssetListConstants.CLASS_NAME_ID_NONEXISTENT),
			unicodeProperties.getProperty("classNameIds"));
	}

	private void _testExportImportAssetListEntryWithSegmentsEntry()
		throws Exception {

		AssetListEntry assetListEntry = (AssetListEntry)addStagedModel(
			stagingGroup, Collections.emptyMap());

		User user = TestPropsValues.getUser();

		SegmentsEntry segmentsEntry = _addSegmentsEntryByFirstName(
			user.getFirstName(), stagingGroup.getGroupId());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			stagingGroup.getGroupId(), assetListEntry,
			segmentsEntry.getSegmentsEntryId());

		exportImportStagedModel(assetListEntry);

		_assertEquals(
			assetListEntry,
			(AssetListEntry)getStagedModel(
				assetListEntry.getUuid(), liveGroup));

		Assert.assertNotNull(
			_segmentsEntryLocalService.getSegmentsEntryByExternalReferenceCode(
				segmentsEntry.getExternalReferenceCode(),
				liveGroup.getGroupId()));

		exportImportStagedModel(assetListEntry);

		_assertEquals(
			assetListEntry,
			(AssetListEntry)getStagedModel(
				assetListEntry.getUuid(), liveGroup));

		Assert.assertNotNull(
			_segmentsEntryLocalService.getSegmentsEntryByExternalReferenceCode(
				segmentsEntry.getExternalReferenceCode(),
				liveGroup.getGroupId()));
	}

	private void _testExportImportAssetListEntryWithStaleAnyAssetTypeClassName()
		throws Exception {

		UnicodeProperties unicodeProperties = _exportImportAssetListEntry(
			UnicodePropertiesBuilder.put(
				"anyAssetType", String.valueOf(RandomTestUtil.randomLong())
			).put(
				"anyAssetTypeClassName", AssetEntry.class.getName()
			).buildString());

		Assert.assertEquals(
			String.valueOf(AssetListConstants.CLASS_NAME_ID_NONEXISTENT),
			unicodeProperties.getProperty("anyAssetType"));
		Assert.assertNull(
			unicodeProperties.getProperty("anyAssetTypeClassName"));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _segmentsCriteriaContributor;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}