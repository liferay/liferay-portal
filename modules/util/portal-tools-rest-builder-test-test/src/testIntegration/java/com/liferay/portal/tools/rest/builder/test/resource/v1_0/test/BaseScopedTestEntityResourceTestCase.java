/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.ScopedTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ScopedTestEntitySerDes;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public abstract class BaseScopedTestEntityResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_scopedTestEntityResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		scopedTestEntityResource = ScopedTestEntityResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ScopedTestEntity scopedTestEntity1 = randomScopedTestEntity();

		String json = objectMapper.writeValueAsString(scopedTestEntity1);

		ScopedTestEntity scopedTestEntity2 = ScopedTestEntitySerDes.toDTO(json);

		Assert.assertTrue(equals(scopedTestEntity1, scopedTestEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ScopedTestEntity scopedTestEntity = randomScopedTestEntity();

		String json1 = objectMapper.writeValueAsString(scopedTestEntity);
		String json2 = ScopedTestEntitySerDes.toJSON(scopedTestEntity);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		ScopedTestEntity scopedTestEntity = randomScopedTestEntity();

		scopedTestEntity.setAssetLibraryKey(regex);
		scopedTestEntity.setDescription(regex);
		scopedTestEntity.setExternalReferenceCode(regex);

		String json = ScopedTestEntitySerDes.toJSON(scopedTestEntity);

		Assert.assertFalse(json.contains(regex));

		scopedTestEntity = ScopedTestEntitySerDes.toDTO(json);

		Assert.assertEquals(regex, scopedTestEntity.getAssetLibraryKey());
		Assert.assertEquals(regex, scopedTestEntity.getDescription());
		Assert.assertEquals(regex, scopedTestEntity.getExternalReferenceCode());
	}

	@Test
	public void testDeleteAssetLibraryScopedTestEntityByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ScopedTestEntity scopedTestEntity =
			testDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		assertHttpResponseStatusCode(
			204,
			scopedTestEntityResource.
				deleteAssetLibraryScopedTestEntityByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					scopedTestEntity.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			scopedTestEntityResource.
				getAssetLibraryScopedTestEntityByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					scopedTestEntity.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			scopedTestEntityResource.
				getAssetLibraryScopedTestEntityByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					"-"));
	}

	protected ScopedTestEntity
			testDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postAssetLibraryScopedTestEntity(
			testDepotEntry.getDepotEntryId(), randomScopedTestEntity());
	}

	protected Long
			testDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode()
		throws Exception {

		// No namespace

		ScopedTestEntity scopedTestEntity1 =
			testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAssetLibraryScopedTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										scopedTestEntity1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAssetLibraryScopedTestEntityByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"assetLibraryScopedTestEntityByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"assetLibraryId",
								"\"" +
									testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" +
									scopedTestEntity1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace test_v1_0

		ScopedTestEntity scopedTestEntity2 =
			testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"deleteAssetLibraryScopedTestEntityByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											scopedTestEntity2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/test_v1_0",
				"Object/deleteAssetLibraryScopedTestEntityByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"test_v1_0",
					new GraphQLField(
						"assetLibraryScopedTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										scopedTestEntity2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected ScopedTestEntity
			testGraphQLDeleteAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity();
	}

	@Test
	public void testDeleteSiteScopedTestEntityByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ScopedTestEntity scopedTestEntity =
			testDeleteSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		assertHttpResponseStatusCode(
			204,
			scopedTestEntityResource.
				deleteSiteScopedTestEntityByExternalReferenceCodeHttpResponse(
					scopedTestEntity.getSiteId(),
					scopedTestEntity.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			scopedTestEntityResource.
				getSiteScopedTestEntityByExternalReferenceCodeHttpResponse(
					scopedTestEntity.getSiteId(),
					scopedTestEntity.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			scopedTestEntityResource.
				getSiteScopedTestEntityByExternalReferenceCodeHttpResponse(
					scopedTestEntity.getSiteId(), "-"));
	}

	protected ScopedTestEntity
			testDeleteSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postSiteScopedTestEntity(
			testGroup.getGroupId(), randomScopedTestEntity());
	}

	@Test
	public void testGraphQLDeleteSiteScopedTestEntityByExternalReferenceCode()
		throws Exception {

		// No namespace

		ScopedTestEntity scopedTestEntity1 =
			testGraphQLDeleteSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteScopedTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + scopedTestEntity1.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										scopedTestEntity1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteScopedTestEntityByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"scopedTestEntityByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + scopedTestEntity1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									scopedTestEntity1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace test_v1_0

		ScopedTestEntity scopedTestEntity2 =
			testGraphQLDeleteSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"deleteSiteScopedTestEntityByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + scopedTestEntity2.getSiteId() +
											"\"");
									put(
										"externalReferenceCode",
										"\"" +
											scopedTestEntity2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/test_v1_0",
				"Object/deleteSiteScopedTestEntityByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"test_v1_0",
					new GraphQLField(
						"scopedTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + scopedTestEntity2.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										scopedTestEntity2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ScopedTestEntity
			testGraphQLDeleteSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return testGraphQLSiteScopedTestEntity_addScopedTestEntity();
	}

	@Test
	public void testGetAssetLibraryScopedTestEntitiesPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryScopedTestEntitiesPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryScopedTestEntitiesPage_getIrrelevantAssetLibraryId();

		Page<ScopedTestEntity> page =
			scopedTestEntityResource.getAssetLibraryScopedTestEntitiesPage(
				assetLibraryId);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			ScopedTestEntity irrelevantScopedTestEntity =
				testGetAssetLibraryScopedTestEntitiesPage_addScopedTestEntity(
					irrelevantAssetLibraryId,
					randomIrrelevantScopedTestEntity());

			page =
				scopedTestEntityResource.getAssetLibraryScopedTestEntitiesPage(
					irrelevantAssetLibraryId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantScopedTestEntity,
				(List<ScopedTestEntity>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryScopedTestEntitiesPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		ScopedTestEntity scopedTestEntity1 =
			testGetAssetLibraryScopedTestEntitiesPage_addScopedTestEntity(
				assetLibraryId, randomScopedTestEntity());

		ScopedTestEntity scopedTestEntity2 =
			testGetAssetLibraryScopedTestEntitiesPage_addScopedTestEntity(
				assetLibraryId, randomScopedTestEntity());

		page = scopedTestEntityResource.getAssetLibraryScopedTestEntitiesPage(
			assetLibraryId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			scopedTestEntity1, (List<ScopedTestEntity>)page.getItems());
		assertContains(
			scopedTestEntity2, (List<ScopedTestEntity>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryScopedTestEntitiesPage_getExpectedActions(
				assetLibraryId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryScopedTestEntitiesPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/test/v1.0/asset-libraries/{assetLibraryId}/scoped-test-entities/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected ScopedTestEntity
			testGetAssetLibraryScopedTestEntitiesPage_addScopedTestEntity(
				Long assetLibraryId, ScopedTestEntity scopedTestEntity)
		throws Exception {

		return scopedTestEntityResource.postAssetLibraryScopedTestEntity(
			assetLibraryId, scopedTestEntity);
	}

	protected Long testGetAssetLibraryScopedTestEntitiesPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryScopedTestEntitiesPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryScopedTestEntitiesPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryScopedTestEntitiesPage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryScopedTestEntities",
			new HashMap<String, Object>() {
				{
					put("assetLibraryId", "\"" + assetLibraryId + "\"");
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject assetLibraryScopedTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryScopedTestEntities");

		long totalCount = assetLibraryScopedTestEntitiesJSONObject.getLong(
			"totalCount");

		ScopedTestEntity scopedTestEntity1 =
			testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity(
				assetLibraryId, randomScopedTestEntity());

		ScopedTestEntity scopedTestEntity2 =
			testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity(
				assetLibraryId, randomScopedTestEntity());

		assetLibraryScopedTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryScopedTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryScopedTestEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			scopedTestEntity1,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					assetLibraryScopedTestEntitiesJSONObject.getString(
						"items"))));
		assertContains(
			scopedTestEntity2,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					assetLibraryScopedTestEntitiesJSONObject.getString(
						"items"))));

		// Using the namespace test_v1_0

		assetLibraryScopedTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(new GraphQLField("test_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/test_v1_0",
				"JSONObject/assetLibraryScopedTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryScopedTestEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			scopedTestEntity1,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					assetLibraryScopedTestEntitiesJSONObject.getString(
						"items"))));
		assertContains(
			scopedTestEntity2,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					assetLibraryScopedTestEntitiesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetAssetLibraryScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity postScopedTestEntity =
			testGetAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		ScopedTestEntity getScopedTestEntity =
			scopedTestEntityResource.
				getAssetLibraryScopedTestEntityByExternalReferenceCode(
					testGetAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					postScopedTestEntity.getExternalReferenceCode());

		assertEquals(postScopedTestEntity, getScopedTestEntity);
		assertValid(getScopedTestEntity);
	}

	protected ScopedTestEntity
			testGetAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postAssetLibraryScopedTestEntity(
			testDepotEntry.getDepotEntryId(), randomScopedTestEntity());
	}

	protected Long
			testGetAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity scopedTestEntity =
			testGraphQLGetAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		// No namespace

		Assert.assertTrue(
			equals(
				scopedTestEntity,
				ScopedTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"assetLibraryScopedTestEntityByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"assetLibraryId",
											"\"" +
												testGraphQLGetAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												scopedTestEntity.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/assetLibraryScopedTestEntityByExternalReferenceCode"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				scopedTestEntity,
				ScopedTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"assetLibraryScopedTestEntityByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"assetLibraryId",
												"\"" +
													testGraphQLGetAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													scopedTestEntity.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/assetLibraryScopedTestEntityByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryScopedTestEntityByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"assetLibraryScopedTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										irrelevantDepotEntry.getDepotEntryId() +
											"\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace test_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"assetLibraryScopedTestEntityByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											irrelevantDepotEntry.
												getDepotEntryId() + "\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ScopedTestEntity
			testGraphQLGetAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity();
	}

	@Test
	public void testGetSiteScopedTestEntitiesPage() throws Exception {
		Long siteId = testGetSiteScopedTestEntitiesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteScopedTestEntitiesPage_getIrrelevantSiteId();

		Page<ScopedTestEntity> page =
			scopedTestEntityResource.getSiteScopedTestEntitiesPage(siteId);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			ScopedTestEntity irrelevantScopedTestEntity =
				testGetSiteScopedTestEntitiesPage_addScopedTestEntity(
					irrelevantSiteId, randomIrrelevantScopedTestEntity());

			page = scopedTestEntityResource.getSiteScopedTestEntitiesPage(
				irrelevantSiteId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantScopedTestEntity,
				(List<ScopedTestEntity>)page.getItems());
			assertValid(
				page,
				testGetSiteScopedTestEntitiesPage_getExpectedActions(
					irrelevantSiteId));
		}

		ScopedTestEntity scopedTestEntity1 =
			testGetSiteScopedTestEntitiesPage_addScopedTestEntity(
				siteId, randomScopedTestEntity());

		ScopedTestEntity scopedTestEntity2 =
			testGetSiteScopedTestEntitiesPage_addScopedTestEntity(
				siteId, randomScopedTestEntity());

		page = scopedTestEntityResource.getSiteScopedTestEntitiesPage(siteId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			scopedTestEntity1, (List<ScopedTestEntity>)page.getItems());
		assertContains(
			scopedTestEntity2, (List<ScopedTestEntity>)page.getItems());
		assertValid(
			page, testGetSiteScopedTestEntitiesPage_getExpectedActions(siteId));
	}

	protected Map<String, Map<String, String>>
			testGetSiteScopedTestEntitiesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/test/v1.0/sites/{siteId}/scoped-test-entities/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected ScopedTestEntity
			testGetSiteScopedTestEntitiesPage_addScopedTestEntity(
				Long siteId, ScopedTestEntity scopedTestEntity)
		throws Exception {

		return scopedTestEntityResource.postSiteScopedTestEntity(
			siteId, scopedTestEntity);
	}

	protected Long testGetSiteScopedTestEntitiesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteScopedTestEntitiesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteScopedTestEntitiesPage() throws Exception {
		Long siteId = testGetSiteScopedTestEntitiesPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"scopedTestEntities",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject scopedTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/scopedTestEntities");

		long totalCount = scopedTestEntitiesJSONObject.getLong("totalCount");

		ScopedTestEntity scopedTestEntity1 =
			testGraphQLSiteScopedTestEntity_addScopedTestEntity(
				siteId, randomScopedTestEntity());

		ScopedTestEntity scopedTestEntity2 =
			testGraphQLSiteScopedTestEntity_addScopedTestEntity(
				siteId, randomScopedTestEntity());

		scopedTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/scopedTestEntities");

		Assert.assertEquals(
			totalCount + 2, scopedTestEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			scopedTestEntity1,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					scopedTestEntitiesJSONObject.getString("items"))));
		assertContains(
			scopedTestEntity2,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					scopedTestEntitiesJSONObject.getString("items"))));

		// Using the namespace test_v1_0

		scopedTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(new GraphQLField("test_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/test_v1_0",
			"JSONObject/scopedTestEntities");

		Assert.assertEquals(
			totalCount + 2, scopedTestEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			scopedTestEntity1,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					scopedTestEntitiesJSONObject.getString("items"))));
		assertContains(
			scopedTestEntity2,
			Arrays.asList(
				ScopedTestEntitySerDes.toDTOs(
					scopedTestEntitiesJSONObject.getString("items"))));
	}

	@Test
	public void testGetSiteScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity postScopedTestEntity =
			testGetSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		ScopedTestEntity getScopedTestEntity =
			scopedTestEntityResource.
				getSiteScopedTestEntityByExternalReferenceCode(
					postScopedTestEntity.getSiteId(),
					postScopedTestEntity.getExternalReferenceCode());

		assertEquals(postScopedTestEntity, getScopedTestEntity);
		assertValid(getScopedTestEntity);
	}

	protected ScopedTestEntity
			testGetSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postSiteScopedTestEntity(
			testGroup.getGroupId(), randomScopedTestEntity());
	}

	@Test
	public void testGraphQLGetSiteScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity scopedTestEntity =
			testGraphQLGetSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		// No namespace

		Assert.assertTrue(
			equals(
				scopedTestEntity,
				ScopedTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"scopedTestEntityByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												scopedTestEntity.getSiteId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												scopedTestEntity.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/scopedTestEntityByExternalReferenceCode"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				scopedTestEntity,
				ScopedTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"scopedTestEntityByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													scopedTestEntity.
														getSiteId() + "\"");
											put(
												"externalReferenceCode",
												"\"" +
													scopedTestEntity.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/scopedTestEntityByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteScopedTestEntityByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"scopedTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace test_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"scopedTestEntityByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ScopedTestEntity
			testGraphQLGetSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return testGraphQLSiteScopedTestEntity_addScopedTestEntity();
	}

	@Test
	public void testPatchAssetLibraryScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity postScopedTestEntity =
			testPatchAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		ScopedTestEntity randomPatchScopedTestEntity =
			randomPatchScopedTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ScopedTestEntity patchScopedTestEntity =
			scopedTestEntityResource.
				patchAssetLibraryScopedTestEntityByExternalReferenceCode(
					testDepotEntry.getDepotEntryId(),
					postScopedTestEntity.getExternalReferenceCode(),
					randomPatchScopedTestEntity);

		ScopedTestEntity expectedPatchScopedTestEntity =
			postScopedTestEntity.clone();

		BeanTestUtil.copyProperties(
			randomPatchScopedTestEntity, expectedPatchScopedTestEntity);

		ScopedTestEntity getScopedTestEntity =
			scopedTestEntityResource.
				getAssetLibraryScopedTestEntityByExternalReferenceCode(
					testDepotEntry.getDepotEntryId(),
					patchScopedTestEntity.getExternalReferenceCode());

		assertEquals(expectedPatchScopedTestEntity, getScopedTestEntity);
		assertValid(getScopedTestEntity);
	}

	protected ScopedTestEntity
			testPatchAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postAssetLibraryScopedTestEntity(
			testDepotEntry.getDepotEntryId(), randomScopedTestEntity());
	}

	@Test
	public void testPatchSiteScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity postScopedTestEntity =
			testPatchSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		ScopedTestEntity randomPatchScopedTestEntity =
			randomPatchScopedTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ScopedTestEntity patchScopedTestEntity =
			scopedTestEntityResource.
				patchSiteScopedTestEntityByExternalReferenceCode(
					postScopedTestEntity.getSiteId(),
					postScopedTestEntity.getExternalReferenceCode(),
					randomPatchScopedTestEntity);

		ScopedTestEntity expectedPatchScopedTestEntity =
			postScopedTestEntity.clone();

		BeanTestUtil.copyProperties(
			randomPatchScopedTestEntity, expectedPatchScopedTestEntity);

		ScopedTestEntity getScopedTestEntity =
			scopedTestEntityResource.
				getSiteScopedTestEntityByExternalReferenceCode(
					patchScopedTestEntity.getSiteId(),
					patchScopedTestEntity.getExternalReferenceCode());

		assertEquals(expectedPatchScopedTestEntity, getScopedTestEntity);
		assertValid(getScopedTestEntity);
	}

	protected ScopedTestEntity
			testPatchSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postSiteScopedTestEntity(
			testGroup.getGroupId(), randomScopedTestEntity());
	}

	@Test
	public void testPostAssetLibraryScopedTestEntity() throws Exception {
		ScopedTestEntity randomScopedTestEntity = randomScopedTestEntity();

		ScopedTestEntity postScopedTestEntity =
			testPostAssetLibraryScopedTestEntity_addScopedTestEntity(
				randomScopedTestEntity);

		assertEquals(randomScopedTestEntity, postScopedTestEntity);
		assertValid(postScopedTestEntity);
	}

	protected ScopedTestEntity
			testPostAssetLibraryScopedTestEntity_addScopedTestEntity(
				ScopedTestEntity scopedTestEntity)
		throws Exception {

		return scopedTestEntityResource.postAssetLibraryScopedTestEntity(
			testGetAssetLibraryScopedTestEntitiesPage_getAssetLibraryId(),
			scopedTestEntity);
	}

	@Test
	public void testGraphQLPostAssetLibraryScopedTestEntity() throws Exception {
		ScopedTestEntity randomScopedTestEntity = randomScopedTestEntity();

		ScopedTestEntity scopedTestEntity =
			testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity(
				testDepotEntry.getDepotEntryId(), randomScopedTestEntity);

		Assert.assertTrue(equals(randomScopedTestEntity, scopedTestEntity));
	}

	@Test
	public void testPostSiteScopedTestEntity() throws Exception {
		ScopedTestEntity randomScopedTestEntity = randomScopedTestEntity();

		ScopedTestEntity postScopedTestEntity =
			testPostSiteScopedTestEntity_addScopedTestEntity(
				randomScopedTestEntity);

		assertEquals(randomScopedTestEntity, postScopedTestEntity);
		assertValid(postScopedTestEntity);
	}

	protected ScopedTestEntity testPostSiteScopedTestEntity_addScopedTestEntity(
			ScopedTestEntity scopedTestEntity)
		throws Exception {

		return scopedTestEntityResource.postSiteScopedTestEntity(
			testGetSiteScopedTestEntitiesPage_getSiteId(), scopedTestEntity);
	}

	@Test
	public void testGraphQLPostSiteScopedTestEntity() throws Exception {
		ScopedTestEntity randomScopedTestEntity = randomScopedTestEntity();

		ScopedTestEntity scopedTestEntity =
			testGraphQLSiteScopedTestEntity_addScopedTestEntity(
				testGroup.getGroupId(), randomScopedTestEntity);

		Assert.assertTrue(equals(randomScopedTestEntity, scopedTestEntity));
	}

	@Test
	public void testPutAssetLibraryScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity postScopedTestEntity =
			testPutAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		ScopedTestEntity randomScopedTestEntity = randomScopedTestEntity();

		ScopedTestEntity putScopedTestEntity =
			scopedTestEntityResource.
				putAssetLibraryScopedTestEntityByExternalReferenceCode(
					testPutAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					postScopedTestEntity.getExternalReferenceCode(),
					randomScopedTestEntity);

		assertEquals(randomScopedTestEntity, putScopedTestEntity);
		assertValid(putScopedTestEntity);

		ScopedTestEntity getScopedTestEntity =
			scopedTestEntityResource.
				getAssetLibraryScopedTestEntityByExternalReferenceCode(
					testPutAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					putScopedTestEntity.getExternalReferenceCode());

		assertEquals(randomScopedTestEntity, getScopedTestEntity);
		assertValid(getScopedTestEntity);

		ScopedTestEntity newScopedTestEntity =
			testPutAssetLibraryScopedTestEntityByExternalReferenceCode_createScopedTestEntity();

		putScopedTestEntity =
			scopedTestEntityResource.
				putAssetLibraryScopedTestEntityByExternalReferenceCode(
					testPutAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					newScopedTestEntity.getExternalReferenceCode(),
					newScopedTestEntity);

		assertEquals(newScopedTestEntity, putScopedTestEntity);
		assertValid(putScopedTestEntity);

		getScopedTestEntity =
			scopedTestEntityResource.
				getAssetLibraryScopedTestEntityByExternalReferenceCode(
					testPutAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId(),
					putScopedTestEntity.getExternalReferenceCode());

		assertEquals(newScopedTestEntity, getScopedTestEntity);

		Assert.assertEquals(
			newScopedTestEntity.getExternalReferenceCode(),
			putScopedTestEntity.getExternalReferenceCode());
	}

	protected ScopedTestEntity
			testPutAssetLibraryScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postAssetLibraryScopedTestEntity(
			testDepotEntry.getDepotEntryId(), randomScopedTestEntity());
	}

	protected Long
			testPutAssetLibraryScopedTestEntityByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected ScopedTestEntity
			testPutAssetLibraryScopedTestEntityByExternalReferenceCode_createScopedTestEntity()
		throws Exception {

		return randomScopedTestEntity();
	}

	@Test
	public void testPutSiteScopedTestEntityByExternalReferenceCode()
		throws Exception {

		ScopedTestEntity postScopedTestEntity =
			testPutSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity();

		ScopedTestEntity randomScopedTestEntity = randomScopedTestEntity();

		ScopedTestEntity putScopedTestEntity =
			scopedTestEntityResource.
				putSiteScopedTestEntityByExternalReferenceCode(
					postScopedTestEntity.getSiteId(),
					postScopedTestEntity.getExternalReferenceCode(),
					randomScopedTestEntity);

		assertEquals(randomScopedTestEntity, putScopedTestEntity);
		assertValid(putScopedTestEntity);

		ScopedTestEntity getScopedTestEntity =
			scopedTestEntityResource.
				getSiteScopedTestEntityByExternalReferenceCode(
					putScopedTestEntity.getSiteId(),
					putScopedTestEntity.getExternalReferenceCode());

		assertEquals(randomScopedTestEntity, getScopedTestEntity);
		assertValid(getScopedTestEntity);

		ScopedTestEntity newScopedTestEntity =
			testPutSiteScopedTestEntityByExternalReferenceCode_createScopedTestEntity();

		putScopedTestEntity =
			scopedTestEntityResource.
				putSiteScopedTestEntityByExternalReferenceCode(
					newScopedTestEntity.getSiteId(),
					newScopedTestEntity.getExternalReferenceCode(),
					newScopedTestEntity);

		assertEquals(newScopedTestEntity, putScopedTestEntity);
		assertValid(putScopedTestEntity);

		getScopedTestEntity =
			scopedTestEntityResource.
				getSiteScopedTestEntityByExternalReferenceCode(
					putScopedTestEntity.getSiteId(),
					putScopedTestEntity.getExternalReferenceCode());

		assertEquals(newScopedTestEntity, getScopedTestEntity);

		Assert.assertEquals(
			newScopedTestEntity.getExternalReferenceCode(),
			putScopedTestEntity.getExternalReferenceCode());
	}

	protected ScopedTestEntity
			testPutSiteScopedTestEntityByExternalReferenceCode_addScopedTestEntity()
		throws Exception {

		return scopedTestEntityResource.postSiteScopedTestEntity(
			testGroup.getGroupId(), randomScopedTestEntity());
	}

	protected ScopedTestEntity
			testPutSiteScopedTestEntityByExternalReferenceCode_createScopedTestEntity()
		throws Exception {

		return randomScopedTestEntity();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected ScopedTestEntity
			testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity()
		throws Exception {

		return testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity(
			testDepotEntry.getDepotEntryId(), randomScopedTestEntity());
	}

	protected ScopedTestEntity
			testGraphQLAssetLibraryScopedTestEntity_addScopedTestEntity(
				Long assetLibraryId, ScopedTestEntity scopedTestEntity)
		throws Exception {

		JSONDeserializer<ScopedTestEntity> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ScopedTestEntity.class)) {

			if (getGraphQLValue(field.get(scopedTestEntity)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(scopedTestEntity)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryScopedTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" + assetLibraryId + "\"");
								put("scopedTestEntity", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createAssetLibraryScopedTestEntity"),
			ScopedTestEntity.class);
	}

	protected ScopedTestEntity
			testGraphQLSiteScopedTestEntity_addScopedTestEntity()
		throws Exception {

		return testGraphQLSiteScopedTestEntity_addScopedTestEntity(
			testGroup.getGroupId(), randomScopedTestEntity());
	}

	protected ScopedTestEntity
			testGraphQLSiteScopedTestEntity_addScopedTestEntity(
				Long siteId, ScopedTestEntity scopedTestEntity)
		throws Exception {

		JSONDeserializer<ScopedTestEntity> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ScopedTestEntity.class)) {

			if (getGraphQLValue(field.get(scopedTestEntity)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(scopedTestEntity)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteScopedTestEntity",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("scopedTestEntity", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteScopedTestEntity"),
			ScopedTestEntity.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		ScopedTestEntity scopedTestEntity,
		List<ScopedTestEntity> scopedTestEntities) {

		boolean contains = false;

		for (ScopedTestEntity item : scopedTestEntities) {
			if (equals(scopedTestEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			scopedTestEntities + " does not contain " + scopedTestEntity,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ScopedTestEntity scopedTestEntity1,
		ScopedTestEntity scopedTestEntity2) {

		Assert.assertTrue(
			scopedTestEntity1 + " does not equal " + scopedTestEntity2,
			equals(scopedTestEntity1, scopedTestEntity2));
	}

	protected void assertEquals(
		List<ScopedTestEntity> scopedTestEntities1,
		List<ScopedTestEntity> scopedTestEntities2) {

		Assert.assertEquals(
			scopedTestEntities1.size(), scopedTestEntities2.size());

		for (int i = 0; i < scopedTestEntities1.size(); i++) {
			ScopedTestEntity scopedTestEntity1 = scopedTestEntities1.get(i);
			ScopedTestEntity scopedTestEntity2 = scopedTestEntities2.get(i);

			assertEquals(scopedTestEntity1, scopedTestEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ScopedTestEntity> scopedTestEntities1,
		List<ScopedTestEntity> scopedTestEntities2) {

		Assert.assertEquals(
			scopedTestEntities1.size(), scopedTestEntities2.size());

		for (ScopedTestEntity scopedTestEntity1 : scopedTestEntities1) {
			boolean contains = false;

			for (ScopedTestEntity scopedTestEntity2 : scopedTestEntities2) {
				if (equals(scopedTestEntity1, scopedTestEntity2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				scopedTestEntities2 + " does not contain " + scopedTestEntity1,
				contains);
		}
	}

	protected void assertValid(ScopedTestEntity scopedTestEntity)
		throws Exception {

		boolean valid = true;

		if (scopedTestEntity.getDateCreated() == null) {
			valid = false;
		}

		if (scopedTestEntity.getDateModified() == null) {
			valid = false;
		}

		if (scopedTestEntity.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				scopedTestEntity.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				scopedTestEntity.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (scopedTestEntity.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (scopedTestEntity.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (scopedTestEntity.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (scopedTestEntity.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<ScopedTestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ScopedTestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ScopedTestEntity> scopedTestEntities =
			page.getItems();

		int size = scopedTestEntities.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						ScopedTestEntity.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		ScopedTestEntity scopedTestEntity1,
		ScopedTestEntity scopedTestEntity2) {

		if (scopedTestEntity1 == scopedTestEntity2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scopedTestEntity1.getDateCreated(),
						scopedTestEntity2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scopedTestEntity1.getDateModified(),
						scopedTestEntity2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scopedTestEntity1.getDescription(),
						scopedTestEntity2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						scopedTestEntity1.getExternalReferenceCode(),
						scopedTestEntity2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scopedTestEntity1.getId(), scopedTestEntity2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scopedTestEntity1.getPermissions(),
						scopedTestEntity2.getPermissions())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_scopedTestEntityResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_scopedTestEntityResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		ScopedTestEntity scopedTestEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = scopedTestEntity.getAssetLibraryKey();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = scopedTestEntity.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(scopedTestEntity.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = scopedTestEntity.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(scopedTestEntity.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = scopedTestEntity.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = scopedTestEntity.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected ScopedTestEntity randomScopedTestEntity() throws Exception {
		return new ScopedTestEntity() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected ScopedTestEntity randomIrrelevantScopedTestEntity()
		throws Exception {

		ScopedTestEntity randomIrrelevantScopedTestEntity =
			randomScopedTestEntity();

		randomIrrelevantScopedTestEntity.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantScopedTestEntity.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantScopedTestEntity;
	}

	protected ScopedTestEntity randomPatchScopedTestEntity() throws Exception {
		return randomScopedTestEntity();
	}

	protected ScopedTestEntityResource scopedTestEntityResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseScopedTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		ScopedTestEntityResource _scopedTestEntityResource;

}