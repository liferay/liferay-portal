/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.test.rule.LazyReferencing;
import com.liferay.exportimport.test.rule.LazyReferencingTestRule;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetType;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.pagination.Page;
import com.liferay.headless.admin.taxonomy.client.pagination.Pagination;
import com.liferay.headless.admin.taxonomy.client.problem.Problem;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class TaxonomyCategoryResourceTest
	extends BaseTaxonomyCategoryResourceTestCase {

	@ClassRule
	@Rule
	public static final LazyReferencingTestRule lazyReferencingTestRule =
		LazyReferencingTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			new ServiceContext());
		_depotAssetVocabulary = _assetVocabularyLocalService.addVocabulary(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testDepotEntry.getGroupId(), RandomTestUtil.randomString(),
			new ServiceContext());
		_globalAssetVocabulary = _assetVocabularyLocalService.addVocabulary(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testCompany.getGroupId(), RandomTestUtil.randomString(),
			new ServiceContext());
		_internalAssetVocabulary = _assetVocabularyLocalService.addVocabulary(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(), null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, null, AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
			new ServiceContext());
	}

	@Override
	@Test
	public void testGetTaxonomyCategory() throws Exception {
		super.testGetTaxonomyCategory();

		_testGetTaxonomyCategoryTaxonomyCategoryUsageCount();
		_testGetTaxonomyCategoryWithAssetCategoryProperty();
	}

	@Override
	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithSortDateTime()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (ListUtil.isEmpty(entityFields)) {
			return;
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		AssetCategory parentAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				_assetVocabulary.getVocabularyId(), serviceContext);

		AssetCategory assetCategory1 = _addAssetCategory(
			_assetVocabulary,
			new Date(System.currentTimeMillis() - (2 * Time.MINUTE)),
			parentAssetCategory, serviceContext);
		AssetCategory assetCategory2 = _addAssetCategory(
			_assetVocabulary, new Date(), parentAssetCategory, serviceContext);

		for (EntityField entityField : entityFields) {
			_assertTaxonomyCategoriesPageOrder(
				entityField, assetCategory1, assetCategory2, "asc",
				parentAssetCategory);
			_assertTaxonomyCategoriesPageOrder(
				entityField, assetCategory2, assetCategory1, "desc",
				parentAssetCategory);
		}
	}

	@Override
	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPage()
		throws Exception {

		super.testGetTaxonomyVocabularyTaxonomyCategoriesPage();

		_testGetTaxonomyVocabularyTaxonomyCategoriesPageFlatten(
			_assetVocabulary);
		_testGetTaxonomyVocabularyTaxonomyCategoriesPageFlatten(
			_depotAssetVocabulary);
		_testGetTaxonomyVocabularyTaxonomyCategoriesPageFlatten(
			_globalAssetVocabulary);
		_testGetTaxonomyVocabularyTaxonomyCategoriesPageFlatten(
			_internalAssetVocabulary);
		_testGetTaxonomyVocabularyTaxonomyCategoriesPageFlattenWithOnlyNameField(
			_assetVocabulary);
	}

	@Override
	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		super.
			testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode();

		TaxonomyCategory taxonomyCategory =
			testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory();

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		try {
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					taxonomyCategory.getTaxonomyVocabularyId(),
					externalReferenceCode);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		super.
			testGraphQLDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxonomyCategoriesRankedPage() throws Exception {
		super.testGraphQLGetTaxonomyCategoriesRankedPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxonomyCategoryTaxonomyCategoriesPage()
		throws Exception {

		super.testGraphQLGetTaxonomyCategoryTaxonomyCategoriesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		super.
			testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode();
	}

	@Override
	@Test
	public void testGraphQLPostSiteTaxonomyCategory() throws Exception {
		TaxonomyCategory randomTaxonomyCategory = randomTaxonomyCategory();

		TaxonomyCategory parentTaxonomyCategory =
			testGetSiteTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory();

		randomTaxonomyCategory.setParentTaxonomyCategory(
			new ParentTaxonomyCategory() {
				{
					externalReferenceCode =
						parentTaxonomyCategory.getExternalReferenceCode();
					id = Long.valueOf(parentTaxonomyCategory.getId());
				}
			});

		randomTaxonomyCategory.setParentTaxonomyVocabulary(
			new ParentTaxonomyVocabulary() {
				{
					externalReferenceCode =
						_assetVocabulary.getExternalReferenceCode();
					id = _assetVocabulary.getVocabularyId();
				}
			});

		TaxonomyCategory taxonomyCategory =
			testGraphQLSiteTaxonomyCategory_addTaxonomyCategory(
				testGroup.getGroupId(), randomTaxonomyCategory);

		Assert.assertTrue(equals(randomTaxonomyCategory, taxonomyCategory));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostTaxonomyVocabularyTaxonomyCategory()
		throws Exception {

		super.testGraphQLPostTaxonomyVocabularyTaxonomyCategory();
	}

	@Override
	@Test
	public void testPatchTaxonomyCategory() throws Exception {
		super.testPatchTaxonomyCategory();

		_testPatchTaxonomyCategoryWithExistingParentTaxonomyCategory(
			testPatchTaxonomyCategory_addTaxonomyCategory(),
			_addAssetVocabulary());
		_testPatchTaxonomyCategoryWithNonexistentParentTaxonomyCategory(
			randomTaxonomyCategory(),
			testPatchTaxonomyCategory_addTaxonomyCategory());
		_testPatchTaxonomyCategoryWithNonexistentParentTaxonomyVocabulary(
			testPatchTaxonomyCategory_addTaxonomyCategory(),
			_randomTaxonomyVocabulary());

		AssetVocabulary assetVocabulary1 = _addAssetVocabulary();
		AssetVocabulary assetVocabulary2 = _addAssetVocabulary();

		_testPatchTaxonomyCategoryWithParentTaxonomyCategoryInADifferentTaxonomyVocabulary(
			_addTaxonomyCategoryWithParentAssetVocabulary(assetVocabulary1),
			_addTaxonomyCategoryWithParentAssetVocabulary(assetVocabulary2));
	}

	@FeatureFlag("LPD-35914")
	@LazyReferencing
	@Override
	@Test
	public void testPostAssetLibraryTaxonomyCategory() throws Exception {
		super.testPostAssetLibraryTaxonomyCategory();

		_testPostAssetLibraryTaxonomyCategoryBatch("INSERT");
		_testPostAssetLibraryTaxonomyCategoryBatch("UPSERT");
	}

	@FeatureFlag("LPD-35914")
	@LazyReferencing
	@Override
	@Test
	public void testPostSiteTaxonomyCategory() throws Exception {
		super.testPostSiteTaxonomyCategory();

		_testPostSiteTaxonomyCategoryBatch("INSERT");
		_testPostSiteTaxonomyCategoryBatch("UPSERT");
	}

	@Override
	@Test
	public void testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		super.
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "name", "taxonomyVocabularyId"};
	}

	@Override
	protected TaxonomyCategory randomTaxonomyCategory() throws Exception {
		TaxonomyCategory taxonomyCategory = super.randomTaxonomyCategory();

		taxonomyCategory.setId(String.valueOf(RandomTestUtil.randomLong()));
		taxonomyCategory.setTaxonomyVocabularyId(
			_assetVocabulary.getVocabularyId());

		return taxonomyCategory;
	}

	@Override
	protected Long
		testDeleteAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId() {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected TaxonomyCategory testDeleteTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		return testGetTaxonomyCategory_addTaxonomyCategory();
	}

	@Override
	protected TaxonomyCategory
			testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		return testGetTaxonomyCategory_addTaxonomyCategory();
	}

	@Override
	protected Long
			testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategory.getTaxonomyVocabularyId();
	}

	@Override
	protected Long
		testGetAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId() {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected TaxonomyCategory
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		taxonomyCategory =
			testPostTaxonomyCategoryTaxonomyCategory_addTaxonomyCategory(
				taxonomyCategory);

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			testGroup.getGroupId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(),
			GetterUtil.getLong(taxonomyCategory.getId()));

		return taxonomyCategory;
	}

	@Override
	protected TaxonomyCategory testGetTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
			_assetVocabulary.getVocabularyId(), randomTaxonomyCategory());
	}

	@Override
	protected String
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId()
		throws Exception {

		TaxonomyCategory taxonomyCategory =
			taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
				_assetVocabulary.getVocabularyId(), randomTaxonomyCategory());

		return taxonomyCategory.getId();
	}

	@Override
	protected Long
		testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId() {

		return _assetVocabulary.getVocabularyId();
	}

	@Override
	protected TaxonomyCategory
			testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		return testGetTaxonomyCategory_addTaxonomyCategory();
	}

	@Override
	protected Long
			testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategory.getTaxonomyVocabularyId();
	}

	@Override
	protected TaxonomyCategory
			testGraphQLGetAssetLibraryTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		return testGetAssetLibraryTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory();
	}

	@Override
	protected Long
		testGraphQLGetAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId() {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected TaxonomyCategory testGraphQLTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		return testPostTaxonomyCategoryTaxonomyCategory_addTaxonomyCategory(
			randomTaxonomyCategory());
	}

	@Override
	protected TaxonomyCategory testPatchTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		return testGetTaxonomyCategory_addTaxonomyCategory();
	}

	@Override
	protected TaxonomyCategory
			testPostTaxonomyCategoryTaxonomyCategory_addTaxonomyCategory(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
			_assetVocabulary.getVocabularyId(), taxonomyCategory);
	}

	@Override
	protected TaxonomyCategory
			testPostTaxonomyVocabularyTaxonomyCategory_addTaxonomyCategory(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return testPostTaxonomyCategoryTaxonomyCategory_addTaxonomyCategory(
			taxonomyCategory);
	}

	@Override
	protected TaxonomyCategory
			testPutAssetLibraryTaxonomyCategoryByExternalReferenceCode_createTaxonomyCategory()
		throws Exception {

		TaxonomyCategory taxonomyCategory =
			_randomAssetLibraryTaxonomyCategory();

		TaxonomyCategory parentTaxonomyCategory =
			taxonomyCategoryResource.postAssetLibraryTaxonomyCategory(
				testGetAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId(),
				_randomAssetLibraryTaxonomyCategory());

		taxonomyCategory.setParentTaxonomyCategory(
			new ParentTaxonomyCategory() {
				{
					externalReferenceCode =
						parentTaxonomyCategory.getExternalReferenceCode();
					id = Long.valueOf(parentTaxonomyCategory.getId());
				}
			});

		taxonomyCategory.setParentTaxonomyVocabulary(
			new ParentTaxonomyVocabulary() {
				{
					externalReferenceCode =
						_depotAssetVocabulary.getExternalReferenceCode();
					id = _depotAssetVocabulary.getVocabularyId();
				}
			});

		return taxonomyCategory;
	}

	@Override
	protected Long
		testPutAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId() {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected TaxonomyCategory testPutTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		return testGetTaxonomyCategory_addTaxonomyCategory();
	}

	@Override
	protected TaxonomyCategory
			testPutTaxonomyCategoryPermissionsPage_addTaxonomyCategory()
		throws Exception {

		return testGetTaxonomyCategory_addTaxonomyCategory();
	}

	@Override
	protected TaxonomyCategory
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		return testGetTaxonomyCategory_addTaxonomyCategory();
	}

	@Override
	protected Long
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategory.getTaxonomyVocabularyId();
	}

	private AssetCategory _addAssetCategory(
			AssetVocabulary assetVocabulary, Date date,
			AssetCategory parentAssetCategory, ServiceContext serviceContext)
		throws Exception {

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			parentAssetCategory.getCategoryId(),
			RandomTestUtil.randomLocaleStringMap(), null,
			assetVocabulary.getVocabularyId(), null, serviceContext);

		assetCategory.setCreateDate(date);
		assetCategory.setModifiedDate(date);

		return _assetCategoryLocalService.updateAssetCategory(assetCategory);
	}

	private AssetVocabulary _addAssetVocabulary() throws Exception {
		return _assetVocabularyLocalService.addVocabulary(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			new ServiceContext());
	}

	private TaxonomyCategory _addTaxonomyCategoryWithParentAssetVocabulary(
			AssetVocabulary assetVocabulary)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
			assetVocabulary.getVocabularyId(), randomTaxonomyCategory());
	}

	private TaxonomyCategory _addTaxonomyCategoryWithParentTaxonomyCategory(
			String parentTaxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
			parentTaxonomyCategoryId, taxonomyCategory);
	}

	private void _assertTaxonomyCategoriesPageOrder(
			EntityField entityField, AssetCategory firstAssetCategory,
			AssetCategory secondAssetCategory, String orderBy,
			AssetCategory parentAssetCategory)
		throws Exception {

		Page<TaxonomyCategory> taxonomyCategoriesPage =
			taxonomyCategoryResource.getTaxonomyCategoryTaxonomyCategoriesPage(
				String.valueOf(parentAssetCategory.getCategoryId()), null, null,
				null, Pagination.of(1, 2),
				entityField.getName() + ":" + orderBy);

		Assert.assertEquals(
			taxonomyCategoriesPage.toString(), 2,
			taxonomyCategoriesPage.getTotalCount());

		List<TaxonomyCategory> taxonomyCategories =
			(List<TaxonomyCategory>)taxonomyCategoriesPage.getItems();

		TaxonomyCategory taxonomyCategory = taxonomyCategories.get(0);

		Assert.assertEquals(
			String.valueOf(firstAssetCategory.getCategoryId()),
			taxonomyCategory.getId());

		taxonomyCategory = taxonomyCategories.get(1);

		Assert.assertEquals(
			String.valueOf(secondAssetCategory.getCategoryId()),
			taxonomyCategory.getId());
	}

	private TaxonomyCategory _randomAssetLibraryTaxonomyCategory()
		throws Exception {

		TaxonomyCategory taxonomyCategory = randomTaxonomyCategory();

		taxonomyCategory.setTaxonomyVocabularyId(
			_depotAssetVocabulary.getVocabularyId());

		return taxonomyCategory;
	}

	private TaxonomyVocabulary _randomTaxonomyVocabulary() {
		return new TaxonomyVocabulary() {
			{
				assetTypes = new AssetType[] {
					new AssetType() {
						{
							required = RandomTestUtil.randomBoolean();
							subtype = "AllAssetSubtypes";
							type = "AllAssetTypes";
						}
					}
				};
				description = RandomTestUtil.randomString();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = RandomTestUtil.randomString();
				siteId = testGroup.getGroupId();
			}
		};
	}

	private void _testGetTaxonomyCategoryTaxonomyCategoryUsageCount()
		throws Exception {

		TaxonomyCategory postTaxonomyCategory =
			testGetTaxonomyCategory_addTaxonomyCategory();

		TaxonomyCategory getTaxonomyCategory =
			taxonomyCategoryResource.getTaxonomyCategory(
				postTaxonomyCategory.getId());

		assertValid(
			getTaxonomyCategory.getActions(),
			HashMapBuilder.<String, Map<String, String>>put(
				"add-category",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:8080/o/headless-admin-taxonomy/v1.0",
						"/taxonomy-categories/", getTaxonomyCategory.getId(),
						"/taxonomy-categories")
				).put(
					"method", "POST"
				).build()
			).put(
				"delete",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-categories/" + getTaxonomyCategory.getId()
				).put(
					"method", "DELETE"
				).build()
			).put(
				"get",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-categories/" + getTaxonomyCategory.getId()
				).put(
					"method", "GET"
				).build()
			).put(
				"replace",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-categories/" + getTaxonomyCategory.getId()
				).put(
					"method", "PUT"
				).build()
			).put(
				"update",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-categories/" + getTaxonomyCategory.getId()
				).put(
					"method", "PATCH"
				).build()
			).build());

		Assert.assertNull(postTaxonomyCategory.getTaxonomyCategoryUsageCount());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			"headless-admin-taxonomy/v1.0/taxonomy-categories/" +
				getTaxonomyCategory.getId() +
					"?nestedFields=taxonomyCategoryUsageCount",
			Http.Method.GET);

		Assert.assertNotNull(jsonObject.get("taxonomyCategoryUsageCount"));

		_addTaxonomyCategoryWithParentTaxonomyCategory(
			postTaxonomyCategory.getId(), randomTaxonomyCategory());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"headless-admin-taxonomy/v1.0/taxonomy-categories/",
				postTaxonomyCategory.getId(), "/taxonomy-categories",
				"?nestedFields=taxonomyCategoryUsageCount"),
			Http.Method.GET);

		JSONArray itemsJSONArray = (JSONArray)jsonObject.get("items");

		JSONObject itemJSONObject = (JSONObject)itemsJSONArray.get(0);

		Assert.assertNotNull(itemJSONObject.get("taxonomyCategoryUsageCount"));
	}

	private void _testGetTaxonomyCategoryWithAssetCategoryProperty()
		throws Exception {

		TaxonomyCategory taxonomyCategory =
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				randomTaxonomyCategory());

		String key = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		_assetCategoryPropertyLocalService.addCategoryProperty(
			TestPropsValues.getUserId(),
			GetterUtil.getLong(taxonomyCategory.getId()), key, value);

		taxonomyCategory = taxonomyCategoryResource.getTaxonomyCategory(
			taxonomyCategory.getId());

		TaxonomyCategoryProperty[] taxonomyCategoryProperties =
			taxonomyCategory.getTaxonomyCategoryProperties();

		Assert.assertNotNull(taxonomyCategoryProperties[0]);

		TaxonomyCategoryProperty taxonomyCategoryProperty =
			taxonomyCategoryProperties[0];

		Assert.assertNotNull(
			taxonomyCategoryProperty.toString(),
			taxonomyCategoryProperty.getExternalReferenceCode());
		Assert.assertEquals(
			taxonomyCategoryProperty.toString(),
			taxonomyCategoryProperty.getKey(), key);
		Assert.assertEquals(
			taxonomyCategoryProperty.toString(),
			taxonomyCategoryProperty.getValue(), value);
	}

	private void _testGetTaxonomyVocabularyTaxonomyCategoriesPageFlatten(
			AssetVocabulary assetVocabulary)
		throws Exception {

		AssetVocabulary irrelevantAssetVocabulary = _addAssetVocabulary();

		TaxonomyCategory taxonomyCategory1 =
			_addTaxonomyCategoryWithParentAssetVocabulary(assetVocabulary);

		TaxonomyCategory taxonomyCategory2 =
			_addTaxonomyCategoryWithParentTaxonomyCategory(
				taxonomyCategory1.getId(), randomTaxonomyCategory());

		TaxonomyCategory irrelevantTaxonomyCategory =
			_addTaxonomyCategoryWithParentAssetVocabulary(
				irrelevantAssetVocabulary);

		Boolean flatten = false;

		Page<TaxonomyCategory> page =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					assetVocabulary.getVocabularyId(), flatten, null, null,
					null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(taxonomyCategory1),
			(List<TaxonomyCategory>)page.getItems());
		assertValid(page);

		flatten = true;

		page =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					assetVocabulary.getVocabularyId(), flatten, null, null,
					null, Pagination.of(1, 10), null);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(taxonomyCategory1, taxonomyCategory2),
			(List<TaxonomyCategory>)page.getItems());
		assertValid(page);

		List<TaxonomyCategory> taxonomyCategories =
			(List<TaxonomyCategory>)page.getItems();

		TaxonomyCategory getTaxonomyCategory1 = taxonomyCategories.get(0);
		TaxonomyCategory getTaxonomyCategory2 = taxonomyCategories.get(1);

		ParentTaxonomyCategory parentTaxonomyCategory1 =
			getTaxonomyCategory1.getParentTaxonomyCategory();
		ParentTaxonomyCategory parentTaxonomyCategory2 =
			getTaxonomyCategory2.getParentTaxonomyCategory();

		Assert.assertTrue(
			((parentTaxonomyCategory1 == null) &&
			 (parentTaxonomyCategory2 != null)) ||
			((parentTaxonomyCategory1 != null) &&
			 (parentTaxonomyCategory2 == null)));

		if (parentTaxonomyCategory1 != null) {
			Assert.assertEquals(
				getTaxonomyCategory2.getId(),
				String.valueOf(parentTaxonomyCategory1.getId()));
			Assert.assertEquals(
				getTaxonomyCategory2.getName(),
				String.valueOf(parentTaxonomyCategory1.getName()));
		}

		if (parentTaxonomyCategory2 != null) {
			Assert.assertEquals(
				getTaxonomyCategory1.getId(),
				String.valueOf(parentTaxonomyCategory2.getId()));
			Assert.assertEquals(
				getTaxonomyCategory1.getName(),
				String.valueOf(parentTaxonomyCategory2.getName()));
		}

		taxonomyCategoryResource.deleteTaxonomyCategory(
			irrelevantTaxonomyCategory.getId());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory2.getId());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory1.getId());
	}

	private void
			_testGetTaxonomyVocabularyTaxonomyCategoriesPageFlattenWithOnlyNameField(
				AssetVocabulary assetVocabulary)
		throws Exception {

		AssetVocabulary irrelevantAssetVocabulary = _addAssetVocabulary();

		TaxonomyCategory taxonomyCategory1 =
			_addTaxonomyCategoryWithParentAssetVocabulary(assetVocabulary);

		TaxonomyCategory taxonomyCategory2 =
			_addTaxonomyCategoryWithParentTaxonomyCategory(
				taxonomyCategory1.getId(), randomTaxonomyCategory());

		TaxonomyCategory irrelevantTaxonomyCategory =
			_addTaxonomyCategoryWithParentAssetVocabulary(
				irrelevantAssetVocabulary);

		TaxonomyCategoryResource.Builder builder =
			TaxonomyCategoryResource.builder();

		taxonomyCategoryResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"fields", "name"
		).build();

		Page<TaxonomyCategory> page =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					assetVocabulary.getVocabularyId(), true, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(2, page.getTotalCount());

		TaxonomyCategory getTaxonomyCategory1 = new TaxonomyCategory() {
			{
				name = taxonomyCategory1.getName();
			}
		};
		TaxonomyCategory getTaxonomyCategory2 = new TaxonomyCategory() {
			{
				name = taxonomyCategory2.getName();
			}
		};

		assertEqualsIgnoringOrder(
			Arrays.asList(getTaxonomyCategory1, getTaxonomyCategory2),
			(List<TaxonomyCategory>)page.getItems());

		assertValid(page);

		taxonomyCategoryResource.deleteTaxonomyCategory(
			irrelevantTaxonomyCategory.getId());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory2.getId());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory1.getId());
	}

	private void _testPatchTaxonomyCategoryWithExistingParentTaxonomyCategory(
			TaxonomyCategory taxonomyCategory, AssetVocabulary assetVocabulary)
		throws Exception {

		taxonomyCategoryResource.patchTaxonomyCategory(
			taxonomyCategory.getId(),
			new TaxonomyCategory() {
				{
					taxonomyVocabularyId = assetVocabulary.getVocabularyId();
				}
			});

		TaxonomyCategory patchParentTaxonomyCategory =
			taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
				assetVocabulary.getVocabularyId(), randomTaxonomyCategory());

		TaxonomyCategory patchTaxonomyCategory =
			taxonomyCategoryResource.patchTaxonomyCategory(
				taxonomyCategory.getId(),
				new TaxonomyCategory() {
					{
						parentTaxonomyCategory = new ParentTaxonomyCategory() {
							{
								externalReferenceCode =
									patchParentTaxonomyCategory.
										getExternalReferenceCode();
								id = Long.valueOf(
									patchParentTaxonomyCategory.getId());
							}
						};
					}
				});

		Assert.assertEquals(
			patchTaxonomyCategory.getTaxonomyVocabularyId(),
			Long.valueOf(assetVocabulary.getVocabularyId()));

		ParentTaxonomyCategory parentTaxonomyCategory =
			patchTaxonomyCategory.getParentTaxonomyCategory();

		Assert.assertEquals(
			parentTaxonomyCategory.getExternalReferenceCode(),
			patchParentTaxonomyCategory.getExternalReferenceCode());
		Assert.assertEquals(
			parentTaxonomyCategory.getId(),
			Long.valueOf(patchParentTaxonomyCategory.getId()));
	}

	private void
			_testPatchTaxonomyCategoryWithNonexistentParentTaxonomyCategory(
				TaxonomyCategory randomTaxonomyCategory,
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		assertHttpResponseStatusCode(
			404,
			taxonomyCategoryResource.patchTaxonomyCategoryHttpResponse(
				taxonomyCategory.getId(),
				new TaxonomyCategory() {
					{
						parentTaxonomyCategory = new ParentTaxonomyCategory() {
							{
								externalReferenceCode =
									randomTaxonomyCategory.
										getExternalReferenceCode();
								id = Long.valueOf(
									randomTaxonomyCategory.getId());
							}
						};
					}
				}));
	}

	private void
			_testPatchTaxonomyCategoryWithNonexistentParentTaxonomyVocabulary(
				TaxonomyCategory taxonomyCategory,
				TaxonomyVocabulary randomTaxonomyVocabulary)
		throws Exception {

		assertHttpResponseStatusCode(
			200,
			taxonomyCategoryResource.patchTaxonomyCategoryHttpResponse(
				taxonomyCategory.getId(),
				new TaxonomyCategory() {
					{
						taxonomyVocabularyId = randomTaxonomyVocabulary.getId();
					}
				}));

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory.getId());

		assertHttpResponseStatusCode(
			404,
			taxonomyCategoryResource.patchTaxonomyCategoryHttpResponse(
				taxonomyCategory.getId(),
				new TaxonomyCategory() {
					{
						taxonomyVocabularyId = randomTaxonomyVocabulary.getId();
					}
				}));
	}

	private void
			_testPatchTaxonomyCategoryWithParentTaxonomyCategoryInADifferentTaxonomyVocabulary(
				TaxonomyCategory taxonomyCategory1,
				TaxonomyCategory taxonomyCategory2)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				taxonomyCategoryResource.patchTaxonomyCategoryHttpResponse(
					taxonomyCategory1.getId(),
					new TaxonomyCategory() {
						{
							parentTaxonomyCategory =
								new ParentTaxonomyCategory() {
									{
										externalReferenceCode =
											taxonomyCategory2.
												getExternalReferenceCode();
										id = Long.valueOf(
											taxonomyCategory2.getId());
									}
								};
							taxonomyVocabularyId =
								taxonomyCategory1.getTaxonomyVocabularyId();
						}
					}));
		}
	}

	private void _testPostAssetLibraryTaxonomyCategoryBatch(
			String createStrategy)
		throws Exception {

		_testPostTaxonomyCategoryBatchCompleteReferences(
			_depotAssetVocabulary, createStrategy, testDepotEntry.getGroupId(),
			"assetLibraryId", testDepotEntry.getDepotEntryId());
		_testPostTaxonomyCategoryBatchFullLazyReferences(
			createStrategy, testDepotEntry.getGroupId(), "assetLibraryId",
			testDepotEntry.getDepotEntryId());
		_testPostTaxonomyCategoryBatchEmptyParentTaxonomyCategory(
			_depotAssetVocabulary, createStrategy, testDepotEntry.getGroupId(),
			"assetLibraryId", testDepotEntry.getDepotEntryId());
		_testPostTaxonomyCategoryBatchEmptyParentTaxonomyVocabulary(
			createStrategy, testDepotEntry.getGroupId(), "assetLibraryId",
			testDepotEntry.getDepotEntryId());
	}

	private void _testPostSiteTaxonomyCategoryBatch(String createStrategy)
		throws Exception {

		_testPostTaxonomyCategoryBatchCompleteReferences(
			_assetVocabulary, createStrategy, testGroup.getGroupId(), "siteId",
			testGroup.getGroupId());
		_testPostTaxonomyCategoryBatchFullLazyReferences(
			createStrategy, testGroup.getGroupId(), "siteId",
			testGroup.getGroupId());
		_testPostTaxonomyCategoryBatchEmptyParentTaxonomyCategory(
			_assetVocabulary, createStrategy, testGroup.getGroupId(), "siteId",
			testGroup.getGroupId());
		_testPostTaxonomyCategoryBatchEmptyParentTaxonomyVocabulary(
			createStrategy, testGroup.getGroupId(), "siteId",
			testGroup.getGroupId());
	}

	private void _testPostTaxonomyCategoryBatch(
			String createStrategy, String parameter, long parameterValue,
			ParentTaxonomyCategory parentTaxonomyCategory,
			ParentTaxonomyVocabulary parentTaxonomyVocabulary,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		taxonomyCategory.setId(String.valueOf(RandomTestUtil.randomLong()));
		taxonomyCategory.setParentTaxonomyCategory(parentTaxonomyCategory);
		taxonomyCategory.setParentTaxonomyVocabulary(parentTaxonomyVocabulary);

		_waitForFinish(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(taxonomyCategory.toString())
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task/",
					com.liferay.headless.admin.taxonomy.dto.v1_0.
						TaxonomyCategory.class.getName(),
					"?createStrategy=", createStrategy, "&", parameter, "=",
					parameterValue),
				Http.Method.POST));

		TaxonomyCategory getParentTaxonomyCategory = null;
		TaxonomyCategory getTaxonomyCategory = null;
		Long groupId = null;

		if (Objects.equals(parameter, "siteId")) {
			getParentTaxonomyCategory =
				taxonomyCategoryResource.
					getSiteTaxonomyCategoryByExternalReferenceCode(
						taxonomyCategory.getSiteId(),
						parentTaxonomyCategory.getExternalReferenceCode());
			getTaxonomyCategory =
				taxonomyCategoryResource.
					getSiteTaxonomyCategoryByExternalReferenceCode(
						taxonomyCategory.getSiteId(),
						taxonomyCategory.getExternalReferenceCode());
			groupId = testGetSiteTaxonomyCategoriesPage_getSiteId();
		}
		else {
			getParentTaxonomyCategory =
				taxonomyCategoryResource.
					getAssetLibraryTaxonomyCategoryByExternalReferenceCode(
						testPutAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId(),
						parentTaxonomyCategory.getExternalReferenceCode());

			getTaxonomyCategory =
				taxonomyCategoryResource.
					getAssetLibraryTaxonomyCategoryByExternalReferenceCode(
						testPutAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId(),
						taxonomyCategory.getExternalReferenceCode());
			groupId =
				testGetAssetLibraryTaxonomyCategoryByExternalReferenceCode_getAssetLibraryId();
		}

		_testPostTaxonomyCategoryBatch(
			getParentTaxonomyCategory, getTaxonomyCategory, groupId,
			parentTaxonomyVocabulary, taxonomyCategory);
	}

	private void _testPostTaxonomyCategoryBatch(
			TaxonomyCategory getParentTaxonomyCategory,
			TaxonomyCategory getTaxonomyCategory, Long groupId,
			ParentTaxonomyVocabulary parentTaxonomyVocabulary,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		assertValid(getTaxonomyCategory);

		Assert.assertEquals(
			taxonomyCategory.getDescription(),
			getTaxonomyCategory.getDescription());
		Assert.assertEquals(
			taxonomyCategory.getName(), getTaxonomyCategory.getName());
		Assert.assertEquals(
			taxonomyCategory.getSiteId(), getTaxonomyCategory.getSiteId());

		assertValid(getParentTaxonomyCategory);

		ParentTaxonomyCategory getTaxonomyParentTaxonomyCategory =
			getTaxonomyCategory.getParentTaxonomyCategory();

		Assert.assertEquals(
			getParentTaxonomyCategory.getId(),
			String.valueOf(getTaxonomyParentTaxonomyCategory.getId()));

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					parentTaxonomyVocabulary.getExternalReferenceCode(),
					groupId);

		Assert.assertEquals(
			Long.valueOf(assetVocabulary.getVocabularyId()),
			getTaxonomyCategory.getTaxonomyVocabularyId());
	}

	private void _testPostTaxonomyCategoryBatchCompleteReferences(
			AssetVocabulary assetVocabulary, String createStrategy,
			long groupId, String parameter, long parameterValue)
		throws Exception {

		TaxonomyCategory postParentTaxonomyCategory =
			taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
				testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId(),
				randomTaxonomyCategory());

		ParentTaxonomyCategory parentTaxonomyCategory =
			new ParentTaxonomyCategory() {
				{
					externalReferenceCode =
						postParentTaxonomyCategory.getExternalReferenceCode();
					id = Long.valueOf(postParentTaxonomyCategory.getId());
				}
			};

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			new ParentTaxonomyVocabulary() {
				{
					externalReferenceCode =
						assetVocabulary.getExternalReferenceCode();
					id = assetVocabulary.getVocabularyId();
				}
			};

		TaxonomyCategory taxonomyCategory = super.randomTaxonomyCategory();

		taxonomyCategory.setTaxonomyVocabularyId(
			assetVocabulary.getVocabularyId());
		taxonomyCategory.setSiteId(groupId);

		_testPostTaxonomyCategoryBatch(
			createStrategy, parameter, parameterValue, parentTaxonomyCategory,
			parentTaxonomyVocabulary, taxonomyCategory);
	}

	private void _testPostTaxonomyCategoryBatchEmptyParentTaxonomyCategory(
			AssetVocabulary assetVocabulary, String createStrategy,
			long groupId, String parameter, long parameterValue)
		throws Exception {

		ParentTaxonomyCategory parentTaxonomyCategory =
			new ParentTaxonomyCategory() {
				{
					externalReferenceCode = RandomTestUtil.randomString();
					id = RandomTestUtil.randomLong();
				}
			};

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			new ParentTaxonomyVocabulary() {
				{
					externalReferenceCode =
						assetVocabulary.getExternalReferenceCode();
					id = assetVocabulary.getVocabularyId();
				}
			};

		TaxonomyCategory taxonomyCategory = super.randomTaxonomyCategory();

		taxonomyCategory.setTaxonomyVocabularyId(() -> null);
		taxonomyCategory.setSiteId(groupId);

		_testPostTaxonomyCategoryBatch(
			createStrategy, parameter, parameterValue, parentTaxonomyCategory,
			parentTaxonomyVocabulary, taxonomyCategory);
	}

	private void _testPostTaxonomyCategoryBatchEmptyParentTaxonomyVocabulary(
			String createStrategy, long groupId, String parameter,
			long parameterValue)
		throws Exception {

		TaxonomyCategory postParentTaxonomyCategory =
			taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
				testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId(),
				randomTaxonomyCategory());

		ParentTaxonomyCategory parentTaxonomyCategory =
			new ParentTaxonomyCategory() {
				{
					externalReferenceCode =
						postParentTaxonomyCategory.getExternalReferenceCode();
					id = Long.valueOf(postParentTaxonomyCategory.getId());
				}
			};

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			new ParentTaxonomyVocabulary() {
				{
					externalReferenceCode = RandomTestUtil.randomString();
					id = RandomTestUtil.randomLong();
				}
			};

		TaxonomyCategory taxonomyCategory = super.randomTaxonomyCategory();

		taxonomyCategory.setTaxonomyVocabularyId(() -> null);
		taxonomyCategory.setSiteId(groupId);

		_testPostTaxonomyCategoryBatch(
			createStrategy, parameter, parameterValue, parentTaxonomyCategory,
			parentTaxonomyVocabulary, taxonomyCategory);
	}

	private void _testPostTaxonomyCategoryBatchFullLazyReferences(
			String createStrategy, long groupId, String parameter,
			long parameterValue)
		throws Exception {

		ParentTaxonomyCategory parentTaxonomyCategory =
			new ParentTaxonomyCategory() {
				{
					externalReferenceCode = RandomTestUtil.randomString();
					id = RandomTestUtil.randomLong();
				}
			};

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			new ParentTaxonomyVocabulary() {
				{
					externalReferenceCode = RandomTestUtil.randomString();
					id = RandomTestUtil.randomLong();
				}
			};

		TaxonomyCategory taxonomyCategory = super.randomTaxonomyCategory();

		taxonomyCategory.setTaxonomyVocabularyId(() -> null);
		taxonomyCategory.setSiteId(groupId);

		_testPostTaxonomyCategoryBatch(
			createStrategy, parameter, parameterValue, parentTaxonomyCategory,
			parentTaxonomyVocabulary, taxonomyCategory);
	}

	private void _waitForFinish(JSONObject jsonObject) throws Exception {
		String endpoint =
			"headless-batch-engine/v1.0/import-task" +
				"/by-external-reference-code/";

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals("COMPLETED", executeStatus);

				return;
			}
		}
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private AssetVocabulary _depotAssetVocabulary;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private AssetVocabulary _globalAssetVocabulary;
	private AssetVocabulary _internalAssetVocabulary;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}