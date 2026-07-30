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
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
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

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(
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
					false)),
			ObjectDefinitionConstants.SCOPE_SITE);
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

	private static final String _OBJECT_FIELD_NAME_INTEGER =
		"xInteger" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_KEYWORD =
		"xKeyword" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_TEXT =
		"xText" + RandomTestUtil.randomString();

	@Inject
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

}