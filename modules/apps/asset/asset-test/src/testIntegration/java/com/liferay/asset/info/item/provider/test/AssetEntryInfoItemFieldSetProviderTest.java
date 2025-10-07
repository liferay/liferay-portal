/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetEntryInfoItemFieldSetProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetInfoFieldSetAssetEntryPublicAssetVocabularyWithAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategory.getCategoryId());

		InfoFieldSet infoFieldSet =
			_assetEntryInfoItemFieldSetProvider.getInfoFieldSet(assetEntry);

		InfoFieldSetEntry infoFieldSetEntry = infoFieldSet.getInfoFieldSetEntry(
			assetVocabulary.getName());

		Assert.assertEquals(
			assetVocabulary.getName(), infoFieldSetEntry.getName());
	}

	@Test
	public void testGetInfoFieldSetAssetEntryPublicEmptyAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		InfoFieldSet infoFieldSet =
			_assetEntryInfoItemFieldSetProvider.getInfoFieldSet(assetEntry);

		InfoFieldSetEntry infoFieldSetEntry = infoFieldSet.getInfoFieldSetEntry(
			assetVocabulary.getName());

		Assert.assertEquals(
			assetVocabulary.getName(), infoFieldSetEntry.getName());
	}

	@Test
	public void testGetInfoFieldSetAssetLibraryAssetVocabulary()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _depotEntry.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategory.getCategoryId());

		InfoFieldSet infoFieldSet =
			_assetEntryInfoItemFieldSetProvider.getInfoFieldSet(assetEntry);

		InfoFieldSetEntry infoFieldSetEntry = infoFieldSet.getInfoFieldSetEntry(
			assetVocabulary.getName());

		Assert.assertEquals(
			assetVocabulary.getName(), infoFieldSetEntry.getName());
	}

	@Test
	public void testGetInfoFieldSetInternalAssetEntryEmptyAssetVocabulary()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, null, AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		InfoFieldSet infoFieldSet =
			_assetEntryInfoItemFieldSetProvider.getInfoFieldSet(assetEntry);

		Assert.assertNull(
			infoFieldSet.getInfoFieldSetEntry(assetVocabulary.getName()));
	}

	@Test
	public void testGetInfoFieldSetJournalArticleClassPublicEmptyAssetVocabulary()
		throws Exception {

		long classNameId = PortalUtil.getClassNameId(
			"com.liferay.journal.model.JournalArticle");

		Group group = GroupLocalServiceUtil.getCompanyGroup(
			TestPropsValues.getCompanyId());

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			group.getGroupId(), classNameId, "BASIC-WEB-CONTENT");

		long classTypeId = ddmStructure.getStructureId();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), classNameId, classTypeId, false);

		InfoFieldSet infoFieldSet =
			_assetEntryInfoItemFieldSetProvider.getInfoFieldSet(
				JournalArticle.class.getName(), classTypeId,
				_group.getGroupId());

		InfoFieldSetEntry infoFieldSetEntry = infoFieldSet.getInfoFieldSetEntry(
			assetVocabulary.getName());

		Assert.assertEquals(
			assetVocabulary.getName(), infoFieldSetEntry.getName());
	}

	@Test
	public void testGetInfoFieldValuesAssetEntryPublicAssetVocabularyWithAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategory.getCategoryId());

		List<InfoFieldValue<Object>> filteredInfoFieldValues =
			_getInfoFieldValues(assetEntry, assetVocabulary.getName());

		KeyLocalizedLabelPair keyLocalizedLabelPair = _getKeyLocalizedLabelPair(
			filteredInfoFieldValues);

		Assert.assertEquals(
			keyLocalizedLabelPair.getLabel(LocaleUtil.ENGLISH),
			assetCategory.getTitle(LocaleUtil.ENGLISH));
	}

	@Test
	public void testGetInfoFieldValuesJournalArticleAllAssetCategories()
		throws Exception {

		AssetVocabulary internalAssetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);

		AssetCategory internalAssetCategory = _addAssetCategory(
			internalAssetVocabulary);

		AssetVocabulary publicAssetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		AssetCategory publicAssetCategory = _addAssetCategory(
			publicAssetVocabulary);

		AssetEntry assetEntry = _addAssetEntry(
			new long[] {
				internalAssetCategory.getCategoryId(),
				publicAssetCategory.getCategoryId()
			});

		List<InfoFieldValue<Object>> filteredInfoFieldValues =
			_getInfoFieldValues(assetEntry, "categories");

		Assert.assertEquals(
			filteredInfoFieldValues.toString(), 1,
			filteredInfoFieldValues.size());

		KeyLocalizedLabelPair keyLocalizedLabelPair = _getKeyLocalizedLabelPair(
			filteredInfoFieldValues);

		Assert.assertEquals(
			keyLocalizedLabelPair.getLabel(LocaleUtil.ENGLISH),
			publicAssetCategory.getTitle(LocaleUtil.ENGLISH));
	}

	@Test
	public void testGetInfoFieldValuesJournalArticleInternalAssetVocabularyWithAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		AssetEntry assetEntry = _addAssetEntry(
			new long[] {assetCategory.getCategoryId()});

		List<InfoFieldValue<Object>> filteredInfoFieldValues =
			_getInfoFieldValues(assetEntry, assetVocabulary.getName());

		Assert.assertEquals(
			filteredInfoFieldValues.toString(), 0,
			filteredInfoFieldValues.size());
	}

	@Test
	public void testGetInfoFieldValuesJournalArticlePublicAssetVocabularyWithAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		AssetEntry assetEntry = _addAssetEntry(
			new long[] {assetCategory.getCategoryId()});

		List<InfoFieldValue<Object>> filteredInfoFieldValues =
			_getInfoFieldValues(assetEntry, assetVocabulary.getName());

		Assert.assertEquals(
			filteredInfoFieldValues.toString(), 1,
			filteredInfoFieldValues.size());

		KeyLocalizedLabelPair keyLocalizedLabelPair = _getKeyLocalizedLabelPair(
			filteredInfoFieldValues);

		Assert.assertEquals(
			keyLocalizedLabelPair.getLabel(LocaleUtil.ENGLISH),
			assetCategory.getTitle(LocaleUtil.ENGLISH));
	}

	private AssetCategory _addAssetCategory(AssetVocabulary assetVocabulary)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, assetVocabulary.getVocabularyId(), null,
			new ServiceContext());
	}

	private AssetEntry _addAssetEntry(long[] assetCategoryIds)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.getSiteDefault(), false, true, serviceContext);

		return _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
	}

	private AssetVocabulary _addAssetVocabulary(int visibilityTypePublic)
		throws Exception {

		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(), null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, null, visibilityTypePublic, new ServiceContext());
	}

	private List<InfoFieldValue<Object>> _getInfoFieldValues(
		AssetEntry assetEntry, String fieldName) {

		return ListUtil.filter(
			_assetEntryInfoItemFieldSetProvider.getInfoFieldValues(assetEntry),
			infoFieldValue -> {
				InfoField infoField = infoFieldValue.getInfoField();

				return Objects.equals(fieldName, infoField.getName());
			});
	}

	private KeyLocalizedLabelPair _getKeyLocalizedLabelPair(
		List<InfoFieldValue<Object>> filteredInfoFieldValues) {

		InfoFieldValue<Object> infoFieldValue = filteredInfoFieldValues.get(0);

		Object value = infoFieldValue.getValue(LocaleUtil.ENGLISH);

		List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
			(List<KeyLocalizedLabelPair>)value;

		return keyLocalizedLabelPairs.get(0);
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryInfoItemFieldSetProvider
		_assetEntryInfoItemFieldSetProvider;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}