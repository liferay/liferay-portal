/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.upgrade.v3_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;

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
public class AssetListEntrySegmentsEntryRelTypeSettingsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());
	}

	@Test
	public void testUpgradeLegacyTypeSettings() throws Exception {
		String assetCategoryId1 = String.valueOf(RandomTestUtil.randomLong());
		String assetCategoryId2 = String.valueOf(RandomTestUtil.randomLong());
		String assetCategoryId3 = String.valueOf(RandomTestUtil.randomLong());
		String assetCategoryId4 = String.valueOf(RandomTestUtil.randomLong());
		String assetTagName1 = RandomTestUtil.randomString();
		String assetTagName2 = RandomTestUtil.randomString();
		String assetTagName3 = RandomTestUtil.randomString();
		String assetTagName4 = RandomTestUtil.randomString();
		String keyword1 = RandomTestUtil.randomString();
		String keyword2 = RandomTestUtil.randomString();
		String keyword3 = RandomTestUtil.randomString();
		String keyword4 = RandomTestUtil.randomString();

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_updateTypeSettings(
				UnicodePropertiesBuilder.put(
					"anyAssetType", "true"
				).put(
					"queryAndOperator0", "true"
				).put(
					"queryAndOperator1", "false"
				).put(
					"queryAndOperator2", "true"
				).put(
					"queryAndOperator3", "false"
				).put(
					"queryAndOperator4", "true"
				).put(
					"queryAndOperator5", "false"
				).put(
					"queryAndOperator6", "true"
				).put(
					"queryAndOperator7", "false"
				).put(
					"queryAndOperator8", "true"
				).put(
					"queryAndOperator9", "false"
				).put(
					"queryAndOperator10", "true"
				).put(
					"queryAndOperator11", "false"
				).put(
					"queryContains0", "true"
				).put(
					"queryContains1", "true"
				).put(
					"queryContains2", "false"
				).put(
					"queryContains3", "false"
				).put(
					"queryContains4", "true"
				).put(
					"queryContains5", "true"
				).put(
					"queryContains6", "false"
				).put(
					"queryContains7", "false"
				).put(
					"queryContains8", "true"
				).put(
					"queryContains9", "true"
				).put(
					"queryContains10", "false"
				).put(
					"queryContains11", "false"
				).put(
					"queryName0", "assetCategories"
				).put(
					"queryName1", "assetCategories"
				).put(
					"queryName2", "assetCategories"
				).put(
					"queryName3", "assetCategories"
				).put(
					"queryName4", "assetTags"
				).put(
					"queryName5", "assetTags"
				).put(
					"queryName6", "assetTags"
				).put(
					"queryName7", "assetTags"
				).put(
					"queryName8", "keywords"
				).put(
					"queryName9", "keywords"
				).put(
					"queryName10", "keywords"
				).put(
					"queryName11", "keywords"
				).put(
					"queryValues0",
					StringUtil.merge(
						new String[] {assetCategoryId1, assetCategoryId2})
				).put(
					"queryValues1",
					StringUtil.merge(
						new String[] {assetCategoryId2, assetCategoryId3})
				).put(
					"queryValues2",
					StringUtil.merge(
						new String[] {assetCategoryId3, assetCategoryId4})
				).put(
					"queryValues3",
					StringUtil.merge(
						new String[] {assetCategoryId4, assetCategoryId1})
				).put(
					"queryValues4",
					StringUtil.merge(
						new String[] {assetTagName1, assetTagName2})
				).put(
					"queryValues5",
					StringUtil.merge(
						new String[] {assetTagName2, assetTagName3})
				).put(
					"queryValues6",
					StringUtil.merge(
						new String[] {assetTagName3, assetTagName4})
				).put(
					"queryValues7",
					StringUtil.merge(
						new String[] {assetTagName4, assetTagName1})
				).put(
					"queryValues8",
					StringUtil.merge(new String[] {keyword1, keyword2})
				).put(
					"queryValues9",
					StringUtil.merge(new String[] {keyword2, keyword3})
				).put(
					"queryValues10",
					StringUtil.merge(new String[] {keyword3, keyword4})
				).put(
					"queryValues11",
					StringUtil.merge(new String[] {keyword4, keyword1})
				).buildString());

		_runUpgrade();

		UnicodeProperties unicodeProperties = _getTypeSettingsUnicodeProperties(
			assetListEntrySegmentsEntryRel);

		Assert.assertEquals(
			"true", unicodeProperties.getProperty("anyAssetType"));

		for (String key : unicodeProperties.keySet()) {
			Assert.assertFalse(key, key.startsWith("query"));
		}

		JSONArray filtersJSONArray = _jsonFactory.createJSONArray(
			unicodeProperties.getProperty("filters"));

		Assert.assertEquals(
			filtersJSONArray.toString(), 12, filtersJSONArray.length());

		_assertFilter(
			filtersJSONArray.getJSONObject(0), "contains", "assetCategories",
			"all", assetCategoryId1, assetCategoryId2);
		_assertFilter(
			filtersJSONArray.getJSONObject(1), "contains", "assetCategories",
			"any", assetCategoryId2, assetCategoryId3);
		_assertFilter(
			filtersJSONArray.getJSONObject(2), "not-contains",
			"assetCategories", "all", assetCategoryId3, assetCategoryId4);
		_assertFilter(
			filtersJSONArray.getJSONObject(3), "not-contains",
			"assetCategories", "any", assetCategoryId4, assetCategoryId1);
		_assertFilter(
			filtersJSONArray.getJSONObject(4), "contains", "assetTags", "all",
			assetTagName1, assetTagName2);
		_assertFilter(
			filtersJSONArray.getJSONObject(5), "contains", "assetTags", "any",
			assetTagName2, assetTagName3);
		_assertFilter(
			filtersJSONArray.getJSONObject(6), "not-contains", "assetTags",
			"all", assetTagName3, assetTagName4);
		_assertFilter(
			filtersJSONArray.getJSONObject(7), "not-contains", "assetTags",
			"any", assetTagName4, assetTagName1);
		_assertFilter(
			filtersJSONArray.getJSONObject(8), "contains", "keywords", "all",
			keyword1 + StringPool.SPACE + keyword2);
		_assertFilter(
			filtersJSONArray.getJSONObject(9), "contains", "keywords", "any",
			keyword2 + StringPool.SPACE + keyword3);
		_assertFilter(
			filtersJSONArray.getJSONObject(10), "not-contains", "keywords",
			"all", keyword3 + StringPool.SPACE + keyword4);
		_assertFilter(
			filtersJSONArray.getJSONObject(11), "not-contains", "keywords",
			"any", keyword4 + StringPool.SPACE + keyword1);
	}

	@Test
	public void testUpgradeRetainsAlreadyMigratedTypeSettings()
		throws Exception {

		String typeSettings = UnicodePropertiesBuilder.put(
			"anyAssetType", "true"
		).put(
			"filters",
			JSONUtil.putAll(
				JSONUtil.put(
					"operatorName", "contains"
				).put(
					"propertyName", "assetTags"
				).put(
					"quantifier", "any"
				).put(
					"value", JSONUtil.putAll(JSONUtil.put("value", "alpha"))
				)
			).toString()
		).buildString();

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_updateTypeSettings(typeSettings);

		_runUpgrade();

		Assert.assertEquals(
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build(),
			_getTypeSettingsUnicodeProperties(assetListEntrySegmentsEntryRel));
	}

	private void _assertFilter(
		JSONObject filterJSONObject, String expectedOperatorName,
		String expectedPropertyName, String expectedQuantifier,
		String... expectedValues) {

		Assert.assertEquals(
			expectedOperatorName, filterJSONObject.getString("operatorName"));
		Assert.assertEquals(
			expectedPropertyName, filterJSONObject.getString("propertyName"));
		Assert.assertEquals(
			expectedQuantifier, filterJSONObject.getString("quantifier"));

		Object value = filterJSONObject.get("value");

		if (value instanceof JSONArray) {
			Assert.assertArrayEquals(
				expectedValues,
				JSONUtil.toStringArray((JSONArray)value, "value"));
		}
		else {
			Assert.assertArrayEquals(
				expectedValues, new String[] {GetterUtil.getString(value)});
		}
	}

	private UnicodeProperties _getTypeSettingsUnicodeProperties(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					assetListEntrySegmentsEntryRel.
						getAssetListEntrySegmentsEntryRelId());

		return UnicodePropertiesBuilder.fastLoad(
			assetListEntrySegmentsEntryRel.getTypeSettings()
		).build();
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private AssetListEntrySegmentsEntryRel _updateTypeSettings(
			String typeSettings)
		throws Exception {

		_assetListEntryLocalService.updateAssetListEntryTypeSettings(
			_assetListEntry.getAssetListEntryId(),
			SegmentsEntryConstants.ID_DEFAULT, typeSettings);

		return _assetListEntrySegmentsEntryRelLocalService.
			fetchAssetListEntrySegmentsEntryRel(
				_assetListEntry.getAssetListEntryId(),
				SegmentsEntryConstants.ID_DEFAULT);
	}

	private static final String _CLASS_NAME =
		"com.liferay.asset.list.internal.upgrade.v3_1_0." +
			"AssetListEntrySegmentsEntryRelTypeSettingsUpgradeProcess";

	@DeleteAfterTestRun
	private AssetListEntry _assetListEntry;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.asset.list.internal.upgrade.registry.AssetListServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}