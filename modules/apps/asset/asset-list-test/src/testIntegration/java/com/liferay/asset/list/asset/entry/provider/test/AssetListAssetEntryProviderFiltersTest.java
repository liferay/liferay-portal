/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.asset.entry.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.info.pagination.InfoPage;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class AssetListAssetEntryProviderFiltersTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_listTypeDefinition = _addListTypeDefinition();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					ObjectFieldConstants.DB_TYPE_DATE, true, false, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_DATE,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
					ObjectFieldConstants.DB_TYPE_DATE_TIME, true, false, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_DATE_TIME,
					Collections.singletonList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE,
							ObjectFieldSettingConstants.
								VALUE_USE_INPUT_AS_ENTERED)),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
					ObjectFieldConstants.DB_TYPE_INTEGER, true, false, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_INTEGER,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_KEYWORD,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_TEXT,
					false),
				ObjectFieldUtil.createObjectField(
					_listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
					null, ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(),
					_OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, false, false),
				ObjectFieldUtil.createObjectField(
					_listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_PICKLIST,
					false, false)),
			ObjectDefinitionConstants.SCOPE_SITE);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithDateRangeFilters()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_DATE, "2026-01-15"
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"between", _OBJECT_FIELD_NAME_DATE,
					JSONUtil.putAll("2026-01-01", "2026-03-01"))),
			objectEntry1);

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_DATE_TIME, "2026-01-15 10:30"
			).build());

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_DATE_TIME, "2026-06-15 10:30"
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"between", _OBJECT_FIELD_NAME_DATE_TIME,
					JSONUtil.putAll("2026-01-15 00:00", "2026-01-15 23:59"))),
			objectEntry2);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithEqualityFilters()
		throws Exception {

		int priority = RandomTestUtil.randomInt();

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, priority
			).put(
				_OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"eq", _OBJECT_FIELD_NAME_INTEGER,
					String.valueOf(priority))),
			objectEntry1);

		String title = StringUtil.toLowerCase(RandomTestUtil.randomString());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"not-eq", _OBJECT_FIELD_NAME_TEXT, title)),
			objectEntry1);

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, RandomTestUtil.randomInt()
			).put(
				_OBJECT_FIELD_NAME_TEXT, title
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject("eq", _OBJECT_FIELD_NAME_TEXT, title)),
			objectEntry2);
		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"not-eq", _OBJECT_FIELD_NAME_INTEGER,
					String.valueOf(priority))),
			objectEntry2);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithKeywordTextContainsFilters()
		throws Exception {

		String keyword = RandomTestUtil.randomString();

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_KEYWORD, keyword
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"contains", _OBJECT_FIELD_NAME_KEYWORD, keyword)),
			objectEntry1);

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_KEYWORD, RandomTestUtil.randomString()
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"not-contains", _OBJECT_FIELD_NAME_KEYWORD, keyword)),
			objectEntry2);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithMultipleFiltersJoinedWithMust()
		throws Exception {

		int priority = RandomTestUtil.randomInt();
		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, priority
			).put(
				_OBJECT_FIELD_NAME_TEXT, title
			).build());

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, RandomTestUtil.randomInt()
			).put(
				_OBJECT_FIELD_NAME_TEXT, title
			).build());

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, priority
			).put(
				_OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"contains", _OBJECT_FIELD_NAME_TEXT, title),
				_buildFilterJSONObject(
					"eq", _OBJECT_FIELD_NAME_INTEGER,
					String.valueOf(priority))),
			objectEntry1);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithNegationFiltersIncludeFieldAbsentEntries()
		throws Exception {

		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_TEXT, title
			).build());

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, RandomTestUtil.randomInt()
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"not-eq", _OBJECT_FIELD_NAME_TEXT, title)),
			objectEntry2);

		String keyword = RandomTestUtil.randomString();

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_KEYWORD, keyword
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"not-contains", _OBJECT_FIELD_NAME_KEYWORD, keyword)),
			objectEntry1, objectEntry2);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithNumericRangeFilters()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, RandomTestUtil.randomInt(1, 100)
			).build());

		int priority1 = RandomTestUtil.randomInt(101, 200);

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"lt", _OBJECT_FIELD_NAME_INTEGER,
					String.valueOf(priority1))),
			objectEntry1);

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, priority1
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"le", _OBJECT_FIELD_NAME_INTEGER,
					String.valueOf(priority1))),
			objectEntry1, objectEntry2);

		int priority2 = RandomTestUtil.randomInt(201, 300);

		ObjectEntry objectEntry3 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_INTEGER, priority2
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"between", _OBJECT_FIELD_NAME_INTEGER,
					JSONUtil.putAll(
						String.valueOf(priority1), String.valueOf(priority2)))),
			objectEntry2, objectEntry3);
		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"ge", _OBJECT_FIELD_NAME_INTEGER,
					String.valueOf(priority1))),
			objectEntry2, objectEntry3);
		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"gt", _OBJECT_FIELD_NAME_INTEGER,
					String.valueOf(priority1))),
			objectEntry3);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithPicklistFilters()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_PICKLIST, _LIST_TYPE_ENTRY_KEY_1
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildPicklistFilter(
					_OBJECT_FIELD_NAME_PICKLIST, "any",
					_LIST_TYPE_ENTRY_KEY_1)),
			objectEntry1);

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_PICKLIST, _LIST_TYPE_ENTRY_KEY_2
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildPicklistFilter(
					_OBJECT_FIELD_NAME_PICKLIST, "any", _LIST_TYPE_ENTRY_KEY_1,
					_LIST_TYPE_ENTRY_KEY_2)),
			objectEntry1, objectEntry2);

		ObjectEntry objectEntry3 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					_LIST_TYPE_ENTRY_KEY_1, _LIST_TYPE_ENTRY_KEY_2)
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildPicklistFilter(
					_OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, "all",
					_LIST_TYPE_ENTRY_KEY_1, _LIST_TYPE_ENTRY_KEY_2)),
			objectEntry3);
		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildPicklistFilter(
					_OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, "any",
					_LIST_TYPE_ENTRY_KEY_1)),
			objectEntry3);

		ObjectEntry objectEntry4 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					_LIST_TYPE_ENTRY_KEY_2, _LIST_TYPE_ENTRY_KEY_3)
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildPicklistFilter(
					_OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, "all",
					_LIST_TYPE_ENTRY_KEY_2)),
			objectEntry3, objectEntry4);

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildPicklistFilter(
					_OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, "any",
					_LIST_TYPE_ENTRY_KEY_3)),
			objectEntry4);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageWithTextContainsFilters()
		throws Exception {

		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_TEXT, title
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"contains", _OBJECT_FIELD_NAME_TEXT, title)),
			objectEntry1);

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).build());

		_assertFilteredClassPKs(
			_buildFiltersJSONArray(
				_buildFilterJSONObject(
					"not-contains", _OBJECT_FIELD_NAME_TEXT, title)),
			objectEntry2);
	}

	@FeatureFlag(enable = false, value = "LPD-74731")
	@Test
	public void testGetAssetEntryQueryWithFiltersWhenFeatureFlagDisabled()
		throws Exception {

		JSONArray filtersJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"classNameId",
				_portal.getClassNameId(_objectDefinition.getClassName())
			).put(
				"classTypeId", _objectDefinition.getObjectDefinitionId()
			).put(
				"propertyName", _OBJECT_FIELD_NAME_TEXT
			).put(
				"value", RandomTestUtil.randomString()
			));

		AssetListEntry assetListEntry = _addDynamicAssetListEntryWithFilters(
			filtersJSONArray.toString());

		AssetEntryQuery assetEntryQuery =
			_assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null);

		Assert.assertNull(assetEntryQuery.getAttribute("filters"));
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntryQueryWithFiltersWhenFeatureFlagEnabled()
		throws Exception {

		String propertyName = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		JSONArray filtersJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"classNameId",
				_portal.getClassNameId(_objectDefinition.getClassName())
			).put(
				"classTypeId", _objectDefinition.getObjectDefinitionId()
			).put(
				"operatorName", "contains"
			).put(
				"propertyName", propertyName
			).put(
				"value", value
			),
			JSONUtil.put(
				"classNameId",
				_portal.getClassNameId(_objectDefinition.getClassName())
			).put(
				"classTypeId", _objectDefinition.getObjectDefinitionId()
			).put(
				"operatorName", "eq"
			).put(
				"propertyName", RandomTestUtil.randomString()
			).put(
				"value", String.valueOf(RandomTestUtil.randomInt())
			));

		AssetListEntry assetListEntry = _addDynamicAssetListEntryWithFilters(
			filtersJSONArray.toString());

		AssetEntryQuery assetEntryQuery =
			_assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null);

		JSONArray actualJSONArray = (JSONArray)assetEntryQuery.getAttribute(
			"filters");

		Assert.assertEquals(
			actualJSONArray.toString(), 2, actualJSONArray.length());

		JSONObject jsonObject = actualJSONArray.getJSONObject(0);

		Assert.assertEquals(
			_portal.getClassNameId(_objectDefinition.getClassName()),
			jsonObject.getLong("classNameId"));
		Assert.assertEquals(propertyName, jsonObject.getString("propertyName"));
		Assert.assertEquals(value, jsonObject.getString("value"));
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntryQueryWithInvalidFilters() throws Exception {
		AssetListEntry assetListEntry = _addDynamicAssetListEntryWithFilters(
			RandomTestUtil.randomString());

		AssetEntryQuery assetEntryQuery =
			_assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null);

		Assert.assertNull(assetEntryQuery.getAttribute("filters"));
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntryQueryWithoutFilters() throws Exception {
		AssetListEntry assetListEntry = _addDynamicAssetListEntryWithFilters(
			null);

		AssetEntryQuery assetEntryQuery =
			_assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null);

		Assert.assertNull(assetEntryQuery.getAttribute("filters"));
	}

	private AssetListEntry _addDynamicAssetListEntryWithFilters(
			String filtersJSON)
		throws Exception {

		UnicodePropertiesBuilder.UnicodePropertiesWrapper
			unicodePropertiesWrapper = UnicodePropertiesBuilder.create(
				true
			).put(
				"anyAssetType",
				String.valueOf(
					_portal.getClassNameId(_objectDefinition.getClassName()))
			);

		if (filtersJSON != null) {
			unicodePropertiesWrapper = unicodePropertiesWrapper.put(
				"filters", filtersJSON);
		}

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId(), 0);

		UnicodeProperties typeSettingsUnicodeProperties =
			unicodePropertiesWrapper.build();

		_assetListEntryLocalService.updateAssetListEntryTypeSettings(
			assetListEntry.getAssetListEntryId(),
			SegmentsEntryConstants.ID_DEFAULT,
			typeSettingsUnicodeProperties.toString());

		return _assetListEntryLocalService.getAssetListEntry(
			assetListEntry.getAssetListEntryId());
	}

	private ListTypeDefinition _addListTypeDefinition() throws Exception {
		return _listTypeDefinitionLocalService.addListTypeDefinition(
			null, TestPropsValues.getUserId(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			false,
			Arrays.asList(
				ListTypeEntryUtil.createListTypeEntry(
					_LIST_TYPE_ENTRY_KEY_1,
					Collections.singletonMap(
						LocaleUtil.US, _LIST_TYPE_ENTRY_KEY_1)),
				ListTypeEntryUtil.createListTypeEntry(
					_LIST_TYPE_ENTRY_KEY_2,
					Collections.singletonMap(
						LocaleUtil.US, _LIST_TYPE_ENTRY_KEY_2)),
				ListTypeEntryUtil.createListTypeEntry(
					_LIST_TYPE_ENTRY_KEY_3,
					Collections.singletonMap(
						LocaleUtil.US, _LIST_TYPE_ENTRY_KEY_3))),
			new ServiceContext());
	}

	private ObjectEntry _addObjectEntry(Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertFilteredClassPKs(
			JSONArray filtersJSONArray, ObjectEntry... expectedObjectEntries)
		throws Exception {

		AssetListEntry assetListEntry = _addDynamicAssetListEntryWithFilters(
			filtersJSONArray.toString());

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK, StringPool.BLANK,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<Long> actualClassPKs = TransformUtil.transform(
			infoPage.getPageItems(), AssetEntry::getClassPK);

		List<Long> expectedClassPKs = TransformUtil.transformToList(
			expectedObjectEntries, ObjectEntry::getObjectEntryId);

		Assert.assertEquals(
			actualClassPKs.toString(), expectedClassPKs.size(),
			actualClassPKs.size());
		Assert.assertTrue(
			actualClassPKs.toString(),
			actualClassPKs.containsAll(expectedClassPKs));
	}

	private JSONObject _buildFilterJSONObject(
		String operatorName, String propertyName, Object value) {

		return JSONUtil.put(
			"classNameId",
			_portal.getClassNameId(_objectDefinition.getClassName())
		).put(
			"classTypeId", _objectDefinition.getObjectDefinitionId()
		).put(
			"operatorName", operatorName
		).put(
			"propertyName", propertyName
		).put(
			"value", value
		);
	}

	private JSONArray _buildFiltersJSONArray(JSONObject... filterJSONObjects) {
		return JSONUtil.putAll((Object[])filterJSONObjects);
	}

	private JSONObject _buildPicklistFilter(
		String propertyName, String quantifier, String... keys) {

		return JSONUtil.put(
			"classNameId",
			_portal.getClassNameId(_objectDefinition.getClassName())
		).put(
			"classTypeId", _objectDefinition.getObjectDefinitionId()
		).put(
			"operatorName", "contains"
		).put(
			"propertyName", propertyName
		).put(
			"quantifier", quantifier
		).put(
			"value",
			JSONUtil.putAll(
				(Object[])TransformUtil.transform(
					keys, key -> JSONUtil.put("value", key), JSONObject.class))
		);
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private static final String _LIST_TYPE_ENTRY_KEY_1 =
		RandomTestUtil.randomString();

	private static final String _LIST_TYPE_ENTRY_KEY_2 =
		RandomTestUtil.randomString();

	private static final String _LIST_TYPE_ENTRY_KEY_3 =
		RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_DATE =
		"xDate" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_DATE_TIME =
		"xDateTime" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_INTEGER =
		"xInteger" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_KEYWORD =
		"xKeyword" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_MULTISELECT_PICKLIST =
		"xCategories" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_PICKLIST =
		"xCategory" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_TEXT =
		"xText" + RandomTestUtil.randomString();

	@Inject
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private Portal _portal;

}