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
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.After;
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

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
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
			_getExpectedExternalUniqueId(assetVocabulary),
			infoFieldSetEntry.getExternalUniqueId());
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
	@TestInfo("LPD-90753")
	public void testGetInfoFieldSetAssetVocabularyGroupRel() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(DepotConstants.TYPE_SPACE);
		DepotEntry depotEntry2 = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry1.getDepotEntryId(), _group.getGroupId());

		Group cmsGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		long classNameId = _portal.getClassNameId(
			JournalArticle.class.getName());
		long classTypeId = RandomTestUtil.randomLong();

		AssetVocabulary assetVocabulary1 = AssetTestUtil.addVocabulary(
			cmsGroup.getGroupId(), classNameId, classTypeId, false);

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary1.getVocabularyId(),
			new long[] {depotEntry1.getGroupId()}, depotEntry1.getType());

		AssetVocabulary assetVocabulary2 = AssetTestUtil.addVocabulary(
			cmsGroup.getGroupId(), classNameId, classTypeId, false);

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary2.getVocabularyId(),
			new long[] {depotEntry2.getGroupId()}, depotEntry2.getType());

		InfoFieldSet infoFieldSet =
			_assetEntryInfoItemFieldSetProvider.getInfoFieldSet(
				JournalArticle.class.getName(), classTypeId,
				_group.getGroupId());

		Assert.assertNotNull(
			infoFieldSet.getInfoFieldSetEntry(assetVocabulary1.getName()));
		Assert.assertNull(
			infoFieldSet.getInfoFieldSetEntry(assetVocabulary2.getName()));
	}

	@Test
	public void testGetInfoFieldSetDepotAssetVocabulary() throws Exception {
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
			_getExpectedExternalUniqueId(assetVocabulary),
			infoFieldSetEntry.getExternalUniqueId());
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

		long classNameId = _portal.getClassNameId(
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
			keyLocalizedLabelPair.getLabel(LocaleUtil.US),
			assetCategory.getTitle(LocaleUtil.US));
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
			_group.getGroupId(), internalAssetCategory.getCategoryId(),
			publicAssetCategory.getCategoryId());

		List<InfoFieldValue<Object>> filteredInfoFieldValues =
			_getInfoFieldValues(assetEntry, "categories");

		Assert.assertEquals(
			filteredInfoFieldValues.toString(), 1,
			filteredInfoFieldValues.size());

		KeyLocalizedLabelPair keyLocalizedLabelPair = _getKeyLocalizedLabelPair(
			filteredInfoFieldValues);

		Assert.assertEquals(
			keyLocalizedLabelPair.getLabel(LocaleUtil.US),
			publicAssetCategory.getTitle(LocaleUtil.US));
	}

	@Test
	public void testGetInfoFieldValuesJournalArticleInternalAssetVocabularyWithAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		AssetEntry assetEntry = _addAssetEntry(
			_group.getGroupId(), assetCategory.getCategoryId());

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

		_testGetInfoFieldValuesJournalArticlePublicAssetVocabularyWithAssetCategory(
			_addAssetCategory(assetVocabulary), assetVocabulary,
			_group.getGroupId());

		assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(),
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		_testGetInfoFieldValuesJournalArticlePublicAssetVocabularyWithAssetCategory(
			assetCategory, assetVocabulary, _group.getGroupId());
		_testGetInfoFieldValuesJournalArticlePublicAssetVocabularyWithAssetCategory(
			assetCategory, assetVocabulary, _depotEntry.getGroupId());
	}

	private AssetCategory _addAssetCategory(AssetVocabulary assetVocabulary)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), assetVocabulary.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, assetVocabulary.getVocabularyId(), false, null,
			new ServiceContext());
	}

	private AssetEntry _addAssetEntry(long groupId, long... assetCategoryIds)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			groupId, 0, _portal.getClassNameId(JournalArticle.class),
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

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertInfoFieldValues(
			long classPK, KeyLocalizedLabelPair expectedKeyLocalizedLabelPair,
			String... fieldNames)
		throws Exception {

		InfoItemFieldValuesProvider infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class,
				JournalArticle.class.getName());

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(
				_journalArticleLocalService.getLatestArticle(classPK));

		for (String fieldName : fieldNames) {
			InfoFieldValue<?> infoFieldValue =
				infoItemFieldValues.getInfoFieldValue(fieldName);

			Assert.assertNotNull(fieldName, infoFieldValue);

			Object value = infoFieldValue.getValue(LocaleUtil.US);

			List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
				(List<KeyLocalizedLabelPair>)value;

			Assert.assertEquals(
				keyLocalizedLabelPairs.toString(), 1,
				keyLocalizedLabelPairs.size());

			KeyLocalizedLabelPair actualKeyLocalizedLabelPair =
				keyLocalizedLabelPairs.get(0);

			Assert.assertEquals(
				expectedKeyLocalizedLabelPair.getKey(),
				actualKeyLocalizedLabelPair.getKey());
			Assert.assertEquals(
				expectedKeyLocalizedLabelPair.getLabel(LocaleUtil.US),
				actualKeyLocalizedLabelPair.getLabel(LocaleUtil.US));
		}
	}

	private String _getExpectedExternalUniqueId(AssetVocabulary assetVocabulary)
		throws Exception {

		if (assetVocabulary.getGroupId() == _group.getGroupId()) {
			return StringBundler.concat(
				AssetVocabulary.class.getSimpleName(), "__ERC__",
				assetVocabulary.getExternalReferenceCode());
		}

		return StringBundler.concat(
			AssetVocabulary.class.getSimpleName(), "__ERC__",
			assetVocabulary.getExternalReferenceCode(), "__SERC__",
			ScopeUtil.getItemScopeExternalReferenceCode(
				assetVocabulary.getGroupId(), _group.getGroupId()));
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

		Object value = infoFieldValue.getValue(LocaleUtil.US);

		List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
			(List<KeyLocalizedLabelPair>)value;

		return keyLocalizedLabelPairs.get(0);
	}

	private void
			_testGetInfoFieldValuesJournalArticlePublicAssetVocabularyWithAssetCategory(
				AssetCategory assetCategory, AssetVocabulary assetVocabulary,
				long groupId)
		throws Exception {

		AssetEntry assetEntry = _addAssetEntry(
			groupId, assetCategory.getCategoryId());

		List<InfoFieldValue<Object>> filteredInfoFieldValues =
			_getInfoFieldValues(assetEntry, assetVocabulary.getName());

		Assert.assertEquals(
			filteredInfoFieldValues.toString(), 1,
			filteredInfoFieldValues.size());

		KeyLocalizedLabelPair keyLocalizedLabelPair = _getKeyLocalizedLabelPair(
			filteredInfoFieldValues);

		Assert.assertEquals(
			keyLocalizedLabelPair.getLabel(LocaleUtil.US),
			assetCategory.getTitle(LocaleUtil.US));

		_assertInfoFieldValues(
			assetEntry.getClassPK(), keyLocalizedLabelPair,
			_getExpectedExternalUniqueId(assetVocabulary),
			StringBundler.concat(
				AssetVocabulary.class.getSimpleName(), StringPool.UNDERLINE,
				assetVocabulary.getVocabularyId()));
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
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Portal _portal;

}