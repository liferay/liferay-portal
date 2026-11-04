/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalServiceUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.scope.Scope;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceCategorizationTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetObjectEntryWithKeywords() throws Exception {
		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"keywords", JSONUtil.putAll("tag1", "tag2")
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition2().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		JSONArray keywordsJSONArray = jsonObject.getJSONArray("keywords");

		Assert.assertEquals("tag1", keywordsJSONArray.get(0));
		Assert.assertEquals("tag2", keywordsJSONArray.get(1));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"keywords", JSONUtil.putAll("tag1", "tag2", "tag3")
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition2().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		keywordsJSONArray = jsonObject.getJSONArray("keywords");

		Assert.assertEquals("tag1", keywordsJSONArray.get(0));
		Assert.assertEquals("tag2", keywordsJSONArray.get(1));
		Assert.assertEquals("tag3", keywordsJSONArray.get(2));

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
			getObjectDefinition2().getClassName(), jsonObject.getLong("id"));

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
			() -> {
				Map<String, Integer> assetCounts = new HashMap<>();

				for (AssetTag assetTag :
						AssetTagLocalServiceUtil.getAssetEntryAssetTags(
							assetEntry.getEntryId())) {

					assetCounts.put(
						assetTag.getName(), assetTag.getAssetCount());
				}

				Assert.assertEquals(
					HashMapBuilder.put(
						"tag1", 2
					).put(
						"tag2", 2
					).put(
						"tag3", 1
					).build(),
					assetCounts);

				return null;
			});
	}

	@Test
	public void testGetObjectEntryWithTaxonomyCategories() throws Exception {

		// Company scope

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary companyAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory companyTaxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				company.getGroupId(), companyAssetVocabulary.getVocabularyId());

		TaxonomyCategory companyTaxonomyCategory2 =
			_postTaxonomyCategoryTaxonomyCategory(
				company.getGroupId(), companyTaxonomyCategory1.getId(),
				companyTaxonomyCategory1.getTaxonomyVocabularyId());

		_testGetObjectEntryWithTaxonomyCategories(
			0, getObjectDefinition1(), companyTaxonomyCategory1,
			companyTaxonomyCategory2);

		// Depot scope

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		AssetVocabulary depotAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), depotEntry.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory depotTaxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				depotEntry.getGroupId(),
				depotAssetVocabulary.getVocabularyId());

		TaxonomyCategory depotTaxonomyCategory2 =
			_postTaxonomyCategoryTaxonomyCategory(
				depotEntry.getGroupId(), depotTaxonomyCategory1.getId(),
				depotTaxonomyCategory1.getTaxonomyVocabularyId());

		ObjectDefinition depotScopedObjectDefinition =
			addDepotScopedObjectDefinition();

		_testGetObjectEntryWithTaxonomyCategories(
			depotEntry.getGroupId(), depotScopedObjectDefinition,
			depotTaxonomyCategory1, depotTaxonomyCategory2);

		// Site scope

		TaxonomyCategory siteTaxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				testGroupId, assetVocabulary.getVocabularyId());

		TaxonomyCategory siteTaxonomyCategory2 =
			_postTaxonomyCategoryTaxonomyCategory(
				testGroupId, siteTaxonomyCategory1.getId(),
				siteTaxonomyCategory1.getTaxonomyVocabularyId());

		_testGetObjectEntryWithTaxonomyCategories(
			testGroupId, getSiteScopedObjectDefinition1(),
			siteTaxonomyCategory1, siteTaxonomyCategory2);
	}

	@Test
	public void testGetObjectEntryWithTaxonomyCategoriesAndEmbeddedTaxonomyCategory()
		throws Exception {

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory taxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory2 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(
					taxonomyCategory1.getId(), taxonomyCategory2.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				jsonObject.getString("id"),
				"?nestedFields=embeddedTaxonomyCategory"),
			Http.Method.GET);

		_assertTaxonomyCategories(
			jsonObject.getLong("scopeId"),
			jsonObject.getJSONArray("taxonomyCategoryBriefs"), true,
			taxonomyCategory1, taxonomyCategory2);
	}

	@Test
	public void testPatchObjectEntryWithKeywords() throws Exception {
		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"keywords", JSONUtil.putAll("tag1", "tag2")
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"keywords", JSONUtil.putAll("tag1", "tag2", "tag3")
			).toString(),
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		JSONArray keywordsJSONArray = jsonObject.getJSONArray("keywords");

		Assert.assertEquals("tag1", keywordsJSONArray.get(0));
		Assert.assertEquals("tag2", keywordsJSONArray.get(1));
		Assert.assertEquals("tag3", keywordsJSONArray.get(2));
	}

	@Test
	public void testPatchObjectEntryWithTaxonomyCategories() throws Exception {
		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory taxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory2 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(
					taxonomyCategory1.getId(), taxonomyCategory2.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		TaxonomyCategory taxonomyCategory3 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(
					taxonomyCategory1.getId(), taxonomyCategory2.getId(),
					taxonomyCategory3.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.PATCH);

		_assertTaxonomyCategories(
			jsonObject.getLong("scopeId"),
			jsonObject.getJSONArray("taxonomyCategoryBriefs"), false,
			taxonomyCategory1, taxonomyCategory2, taxonomyCategory3);
	}

	@Test
	public void testPostObjectEntryWithKeywordsAndTaxonomyCategoryIdsWhenCategorizationDisabled()
		throws Exception {

		// TODO LPS-173383 Add test coverage for invalid state transitions

		getObjectDefinition1().setEnableCategorization(false);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		try {
			TaxonomyCategory taxonomyCategory =
				postTaxonomyVocabularyTaxonomyCategory();

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"keywords", JSONUtil.putAll("tag")
				).put(
					"taxonomyCategoryIds",
					JSONUtil.putAll(taxonomyCategory.getId())
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

			Assert.assertFalse(jsonObject.has("keywords"));
			Assert.assertFalse(jsonObject.has("taxonomyCategoryBriefs"));

			AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
				getObjectDefinition1().getClassName(), jsonObject.getInt("id"));

			List<AssetTag> assetEntryAssetTags =
				AssetTagLocalServiceUtil.getAssetEntryAssetTags(
					assetEntry.getEntryId());

			Assert.assertEquals(
				assetEntryAssetTags.toString(), 0, assetEntryAssetTags.size());

			List<AssetCategory> assetCategories =
				AssetCategoryLocalServiceUtil.getCategories(
					getObjectDefinition1().getClassName(),
					jsonObject.getInt("id"));

			Assert.assertEquals(
				assetCategories.toString(), 0, assetCategories.size());
		}
		finally {
			getObjectDefinition1().setEnableCategorization(true);

			objectDefinition1 =
				objectDefinitionLocalService.updateObjectDefinition(
					getObjectDefinition1());
		}
	}

	@Test
	public void testPostObjectEntryWithTaxonomyCategories() throws Exception {
		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		long classNameId = classNameLocalService.getClassNameId(
			getObjectDefinition1().getClassName());

		assetVocabulary.setSettings(
			StringBundler.concat(
				"multiValued=false\nrequiredClassNameIds=", classNameId,
				":0\nselectedClassNameIds=", classNameId, ":0"));

		assetVocabulary = assetVocabularyLocalService.updateAssetVocabulary(
			assetVocabulary);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				String.format(
					"Please select at least one category for <em>%s</em>.",
					assetVocabulary.getName())
			).put(
				"type", "AssetCategoryException"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		TaxonomyCategory taxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory2 =
			postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getGroupId(),
				assetVocabulary.getVocabularyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				String.format(
					"You cannot select more than one category for <em>%s</em>.",
					assetVocabulary.getName())
			).put(
				"type", "AssetCategoryException"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.putAll(
						taxonomyCategory1.getId(), taxonomyCategory2.getId())
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"taxonomyCategoryIds",
				JSONUtil.putAll(taxonomyCategory1.getId())
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertNotNull(
			objectEntryLocalService.fetchObjectEntry(jsonObject.getLong("id")));

		assetVocabularyLocalService.deleteVocabulary(assetVocabulary);

		_testPostObjectEntryWithCrossSiteTaxonomyCategories();
	}

	private void _assertTaxonomyCategories(
			long groupId, JSONArray jsonArray1,
			boolean withEmbeddedTaxonomyCategory,
			TaxonomyCategory... taxonomyCategories)
		throws Exception {

		JSONArray jsonArray2 = jsonFactory.createJSONArray();

		for (TaxonomyCategory taxonomyCategory : taxonomyCategories) {
			jsonArray2.put(
				_toTaxonomyCategoryJSONObject(
					groupId, taxonomyCategory, withEmbeddedTaxonomyCategory));
		}

		JSONAssert.assertEquals(
			jsonArray2.toString(), jsonArray1.toString(),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private TaxonomyCategory _postTaxonomyCategoryTaxonomyCategory(
			long groupId, String taxonomyCategoryId, long taxonomyVocabularyId)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
			taxonomyCategoryId,
			randomTaxonomyCategory(groupId, taxonomyVocabularyId));
	}

	private void _testGetObjectEntryWithTaxonomyCategories(
			long groupId, ObjectDefinition objectDefinition,
			TaxonomyCategory... taxonomyCategories)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, "value"
			).put(
				"taxonomyCategoryIds",
				JSONUtil.toJSONArray(
					taxonomyCategories, TaxonomyCategory::getId)
			).toString(),
			getEndpoint(objectDefinition, groupId), Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + StringPool.SLASH +
				jsonObject.getString("id"),
			Http.Method.GET);

		_assertTaxonomyCategories(
			jsonObject.getLong("scopeId"),
			jsonObject.getJSONArray("taxonomyCategoryBriefs"), false,
			taxonomyCategories);
	}

	private void _testPostObjectEntryWithCrossSiteTaxonomyCategories()
		throws Exception {

		// Can add a category from a connected depot entry

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), testGroupId);

		AssetVocabulary depotAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), depotEntry.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory depotTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				depotEntry.getGroupId(),
				depotAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(depotTaxonomyCategory.getId())
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());

		// Can add a category from a parent site

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory taxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				group.getGroupId(), assetVocabulary.getVocabularyId());

		Group childGroup = GroupTestUtil.addGroup(group.getGroupId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				getEndpoint(
					getSiteScopedObjectDefinition1(), childGroup.getGroupId()),
				Http.Method.POST
			).get(
				"id"
			));

		groupLocalService.deleteGroup(childGroup);

		// Can add a category from an all spaces vocabulary

		AssetVocabulary allSpacesAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		AssetVocabularyGroupRelLocalServiceUtil.addAssetVocabularyGroupRel(
			GroupConstants.ANY_PARENT_GROUP_ID,
			allSpacesAssetVocabulary.getVocabularyId(),
			DepotConstants.TYPE_SPACE);

		TaxonomyCategory allSpacesTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				allSpacesAssetVocabulary.getGroupId(),
				allSpacesAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(allSpacesTaxonomyCategory.getId())
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		assetVocabularyLocalService.deleteVocabulary(allSpacesAssetVocabulary);

		// Can add a category from a space associated vocabulary

		AssetVocabulary sharedAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		AssetVocabularyGroupRelLocalServiceUtil.addAssetVocabularyGroupRel(
			testGroupId, sharedAssetVocabulary.getVocabularyId(),
			DepotConstants.TYPE_SPACE);

		TaxonomyCategory sharedTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				sharedAssetVocabulary.getGroupId(),
				sharedAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.putAll(
						JSONUtil.put(
							"parentTaxonomyVocabulary",
							JSONUtil.put(
								"externalReferenceCode",
								sharedAssetVocabulary.
									getExternalReferenceCode())
						).put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							sharedTaxonomyCategory.getExternalReferenceCode()
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		assetVocabularyLocalService.deleteVocabulary(sharedAssetVocabulary);

		// Can add a category from a system vocabulary

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setSystem(true);

		AssetVocabulary systemAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), null,
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(),
				AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				new ServiceContext());

		AssetVocabularyGroupRelLocalServiceUtil.addAssetVocabularyGroupRel(
			GroupConstants.ANY_PARENT_GROUP_ID,
			systemAssetVocabulary.getVocabularyId(), DepotConstants.TYPE_SPACE);

		TaxonomyCategory systemTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				systemAssetVocabulary.getGroupId(),
				systemAssetVocabulary.getVocabularyId());

		Assert.assertNotNull(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.putAll(
						JSONUtil.put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							systemTaxonomyCategory.getExternalReferenceCode()
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST
			).get(
				"id"
			));

		boolean deleteInProcess = GroupThreadLocal.isDeleteInProcess();

		try {
			GroupThreadLocal.setDeleteInProcess(true);

			assetVocabularyLocalService.deleteVocabulary(systemAssetVocabulary);
		}
		finally {
			GroupThreadLocal.setDeleteInProcess(deleteInProcess);
		}

		// Cannot add a category from an unassociated vocabulary

		AssetVocabulary foreignAssetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory foreignTaxonomyCategory =
			postTaxonomyVocabularyTaxonomyCategory(
				foreignAssetVocabulary.getGroupId(),
				foreignAssetVocabulary.getVocabularyId());

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.putAll(
						JSONUtil.put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							foreignTaxonomyCategory.getExternalReferenceCode()
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		assetVocabularyLocalService.deleteVocabulary(foreignAssetVocabulary);

		// Cannot add a category to a company-scoped entry

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		// Cannot add a category from a foreign site

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		// Cannot add a category via UPSERT

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryIds",
					JSONUtil.put(taxonomyCategory.getId())
				).toString(),
				StringBundler.concat(
					getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
					"/by-external-reference-code/",
					RandomTestUtil.randomString()),
				Http.Method.PUT));

		// No empty category created

		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertNull(
			AssetCategoryLocalServiceUtil.
				fetchAssetCategoryByExternalReferenceCode(
					externalReferenceCode, group.getGroupId()));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"taxonomyCategoryBriefs",
					JSONUtil.put(
						JSONUtil.put(
							"scope",
							JSONUtil.put(
								"externalReferenceCode",
								group.getExternalReferenceCode()
							).put(
								"type", "Site"
							)
						).put(
							"taxonomyCategoryExternalReferenceCode",
							externalReferenceCode
						))
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		Assert.assertNull(
			AssetCategoryLocalServiceUtil.
				fetchAssetCategoryByExternalReferenceCode(
					externalReferenceCode, group.getGroupId()));

		assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
		assetVocabularyLocalService.deleteVocabulary(depotAssetVocabulary);
	}

	private JSONObject _toEmbeddedTaxonomyCategoryJSONObject(
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		TaxonomyCategory embeddedTaxonomyCategory = taxonomyCategory.clone();

		embeddedTaxonomyCategory.setActions(
			HashMapBuilder.<String, Map<String, String>>put(
				"add-category",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId(), "/taxonomy-categories")
				).put(
					"method", "POST"
				).build()
			).put(
				"delete",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "DELETE"
				).build()
			).put(
				"get",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "GET"
				).build()
			).put(
				"replace",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "PUT"
				).build()
			).put(
				"update",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/taxonomy-categories/",
						taxonomyCategory.getId())
				).put(
					"method", "PATCH"
				).build()
			).build());

		return JSONFactoryUtil.createJSONObject(
			embeddedTaxonomyCategory.toString());
	}

	private JSONObject _toTaxonomyCategoryJSONObject(
			long groupId, TaxonomyCategory taxonomyCategory,
			boolean withEmbeddedTaxonomyCategory)
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			"parentTaxonomyCategory",
			() -> {
				ParentTaxonomyCategory parentTaxonomyCategory =
					taxonomyCategory.getParentTaxonomyCategory();

				if (parentTaxonomyCategory == null) {
					return null;
				}

				return JSONUtil.put(
					"externalReferenceCode",
					parentTaxonomyCategory.getExternalReferenceCode());
			}
		).put(
			"parentTaxonomyVocabulary",
			() -> {
				ParentTaxonomyVocabulary parentTaxonomyVocabulary =
					taxonomyCategory.getParentTaxonomyVocabulary();

				return JSONUtil.put(
					"externalReferenceCode",
					parentTaxonomyVocabulary.getExternalReferenceCode());
			}
		).put(
			"scope",
			() -> {
				if (groupId == taxonomyCategory.getSiteId()) {
					return null;
				}

				Scope scope = Scope.of(
					taxonomyCategory.getSiteId(), LocaleUtil.getDefault());

				return JSONUtil.put(
					"externalReferenceCode", scope.getExternalReferenceCode()
				).put(
					"type", scope.getTypeAsString()
				);
			}
		).put(
			"taxonomyCategoryExternalReferenceCode",
			taxonomyCategory.getExternalReferenceCode()
		).put(
			"taxonomyCategoryId", Long.valueOf(taxonomyCategory.getId())
		).put(
			"taxonomyCategoryName", taxonomyCategory.getName()
		);

		if (!withEmbeddedTaxonomyCategory) {
			return jsonObject;
		}

		return jsonObject.put(
			"embeddedTaxonomyCategory",
			_toEmbeddedTaxonomyCategoryJSONObject(taxonomyCategory));
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

}