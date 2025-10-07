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
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
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
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ERCAssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.permission.Permission;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.ERCAssetLibraryTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ERCAssetLibraryTestEntitySerDes;
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
public abstract class BaseERCAssetLibraryTestEntityResourceTestCase {

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

		_ercAssetLibraryTestEntityResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		ercAssetLibraryTestEntityResource =
			ERCAssetLibraryTestEntityResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		permissionsERCAssetLibraryTestEntityResource =
			ERCAssetLibraryTestEntityResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).parameter(
				"nestedFields", "permissions"
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

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			randomERCAssetLibraryTestEntity();

		String json = objectMapper.writeValueAsString(
			ercAssetLibraryTestEntity1);

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
			ERCAssetLibraryTestEntitySerDes.toDTO(json);

		Assert.assertTrue(
			equals(ercAssetLibraryTestEntity1, ercAssetLibraryTestEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		String json1 = objectMapper.writeValueAsString(
			ercAssetLibraryTestEntity);
		String json2 = ERCAssetLibraryTestEntitySerDes.toJSON(
			ercAssetLibraryTestEntity);

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

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		ercAssetLibraryTestEntity.setAssetLibraryExternalReferenceCode(regex);
		ercAssetLibraryTestEntity.setDescription(regex);
		ercAssetLibraryTestEntity.setExternalReferenceCode(regex);

		String json = ERCAssetLibraryTestEntitySerDes.toJSON(
			ercAssetLibraryTestEntity);

		Assert.assertFalse(json.contains(regex));

		ercAssetLibraryTestEntity = ERCAssetLibraryTestEntitySerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			ercAssetLibraryTestEntity.getAssetLibraryExternalReferenceCode());
		Assert.assertEquals(regex, ercAssetLibraryTestEntity.getDescription());
		Assert.assertEquals(
			regex, ercAssetLibraryTestEntity.getExternalReferenceCode());
	}

	@Test
	public void testDeleteAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			testDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		assertHttpResponseStatusCode(
			204,
			ercAssetLibraryTestEntityResource.
				deleteAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					ercAssetLibraryTestEntity.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					ercAssetLibraryTestEntity.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					"-"));
	}

	protected ERCAssetLibraryTestEntity
			testDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testGraphQLDeleteAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		// No namespace

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			testGraphQLDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAssetLibraryERCAssetLibraryTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryExternalReferenceCode",
									"\"" +
										ercAssetLibraryTestEntity1.
											getAssetLibraryExternalReferenceCode() +
												"\"");
								put(
									"ercAssetLibraryTestEntityExternalReferenceCode",
									"\"" +
										ercAssetLibraryTestEntity1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAssetLibraryERCAssetLibraryTestEntity"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"assetLibraryERCAssetLibraryTestEntity",
					new HashMap<String, Object>() {
						{
							put(
								"assetLibraryExternalReferenceCode",
								"\"" +
									ercAssetLibraryTestEntity1.
										getAssetLibraryExternalReferenceCode() +
											"\"");
							put(
								"ercAssetLibraryTestEntityExternalReferenceCode",
								"\"" +
									ercAssetLibraryTestEntity1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace test_v1_0

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
			testGraphQLDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"deleteAssetLibraryERCAssetLibraryTestEntity",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryExternalReferenceCode",
										"\"" +
											ercAssetLibraryTestEntity2.
												getAssetLibraryExternalReferenceCode() +
													"\"");
									put(
										"ercAssetLibraryTestEntityExternalReferenceCode",
										"\"" +
											ercAssetLibraryTestEntity2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/test_v1_0",
				"Object/deleteAssetLibraryERCAssetLibraryTestEntity"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"test_v1_0",
					new GraphQLField(
						"assetLibraryERCAssetLibraryTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryExternalReferenceCode",
									"\"" +
										ercAssetLibraryTestEntity2.
											getAssetLibraryExternalReferenceCode() +
												"\"");
								put(
									"ercAssetLibraryTestEntityExternalReferenceCode",
									"\"" +
										ercAssetLibraryTestEntity2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ERCAssetLibraryTestEntity
			testGraphQLDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();
	}

	@Test
	public void testGetAssetLibraryERCAssetLibraryTestEntitiesPage()
		throws Exception {

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getAssetLibraryExternalReferenceCode();
		String irrelevantAssetLibraryExternalReferenceCode =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getIrrelevantAssetLibraryExternalReferenceCode();

		Page<ERCAssetLibraryTestEntity> page =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntitiesPage(
					assetLibraryExternalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryExternalReferenceCode != null) {
			ERCAssetLibraryTestEntity irrelevantERCAssetLibraryTestEntity =
				testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
					irrelevantAssetLibraryExternalReferenceCode,
					randomIrrelevantERCAssetLibraryTestEntity());

			page =
				ercAssetLibraryTestEntityResource.
					getAssetLibraryERCAssetLibraryTestEntitiesPage(
						irrelevantAssetLibraryExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantERCAssetLibraryTestEntity,
				(List<ERCAssetLibraryTestEntity>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getExpectedActions(
					irrelevantAssetLibraryExternalReferenceCode));
		}

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode,
				randomERCAssetLibraryTestEntity());

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode,
				randomERCAssetLibraryTestEntity());

		page =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntitiesPage(
					assetLibraryExternalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			ercAssetLibraryTestEntity1,
			(List<ERCAssetLibraryTestEntity>)page.getItems());
		assertContains(
			ercAssetLibraryTestEntity2,
			(List<ERCAssetLibraryTestEntity>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getExpectedActions(
				assetLibraryExternalReferenceCode));

		for (ERCAssetLibraryTestEntity ercAssetLibraryTestEntity :
				page.getItems()) {

			Assert.assertNull(ercAssetLibraryTestEntity.getPermissions());
		}

		page =
			permissionsERCAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntitiesPage(
					assetLibraryExternalReferenceCode);

		for (ERCAssetLibraryTestEntity ercAssetLibraryTestEntity :
				page.getItems()) {

			Assert.assertNotNull(ercAssetLibraryTestEntity.getPermissions());
		}
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/batch".
				replace(
					"{assetLibraryExternalReferenceCode}",
					String.valueOf(assetLibraryExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected ERCAssetLibraryTestEntity
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
				String assetLibraryExternalReferenceCode,
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode, ercAssetLibraryTestEntity);
	}

	protected String
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	protected String
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getIrrelevantAssetLibraryExternalReferenceCode()
		throws Exception {

		return irrelevantDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testGraphQLGetAssetLibraryERCAssetLibraryTestEntitiesPage()
		throws Exception {

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getAssetLibraryExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryERCAssetLibraryTestEntities",
			new HashMap<String, Object>() {
				{
					put(
						"assetLibraryExternalReferenceCode",
						"\"" + assetLibraryExternalReferenceCode + "\"");
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject assetLibraryERCAssetLibraryTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryERCAssetLibraryTestEntities");

		long totalCount =
			assetLibraryERCAssetLibraryTestEntitiesJSONObject.getLong(
				"totalCount");

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode,
				randomERCAssetLibraryTestEntity());

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
			testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode,
				randomERCAssetLibraryTestEntity());

		assetLibraryERCAssetLibraryTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryERCAssetLibraryTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryERCAssetLibraryTestEntitiesJSONObject.getLong(
				"totalCount"));

		assertContains(
			ercAssetLibraryTestEntity1,
			Arrays.asList(
				ERCAssetLibraryTestEntitySerDes.toDTOs(
					assetLibraryERCAssetLibraryTestEntitiesJSONObject.getString(
						"items"))));
		assertContains(
			ercAssetLibraryTestEntity2,
			Arrays.asList(
				ERCAssetLibraryTestEntitySerDes.toDTOs(
					assetLibraryERCAssetLibraryTestEntitiesJSONObject.getString(
						"items"))));

		// Using the namespace test_v1_0

		assetLibraryERCAssetLibraryTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(new GraphQLField("test_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/test_v1_0",
				"JSONObject/assetLibraryERCAssetLibraryTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryERCAssetLibraryTestEntitiesJSONObject.getLong(
				"totalCount"));

		assertContains(
			ercAssetLibraryTestEntity1,
			Arrays.asList(
				ERCAssetLibraryTestEntitySerDes.toDTOs(
					assetLibraryERCAssetLibraryTestEntitiesJSONObject.getString(
						"items"))));
		assertContains(
			ercAssetLibraryTestEntity2,
			Arrays.asList(
				ERCAssetLibraryTestEntitySerDes.toDTOs(
					assetLibraryERCAssetLibraryTestEntitiesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testGetAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity getERCAssetLibraryTestEntity =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntity(
					postERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode());

		assertEquals(
			postERCAssetLibraryTestEntity, getERCAssetLibraryTestEntity);
		assertValid(getERCAssetLibraryTestEntity);

		Assert.assertNull(getERCAssetLibraryTestEntity.getPermissions());

		getERCAssetLibraryTestEntity =
			permissionsERCAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntity(
					postERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode());

		Assert.assertNotNull(getERCAssetLibraryTestEntity.getPermissions());
	}

	protected ERCAssetLibraryTestEntity
			testGetAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testGraphQLGetAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			testGraphQLGetAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		// No namespace

		Assert.assertTrue(
			equals(
				ercAssetLibraryTestEntity,
				ERCAssetLibraryTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"assetLibraryERCAssetLibraryTestEntity",
								new HashMap<String, Object>() {
									{
										put(
											"assetLibraryExternalReferenceCode",
											"\"" +
												ercAssetLibraryTestEntity.
													getAssetLibraryExternalReferenceCode() +
														"\"");
										put(
											"ercAssetLibraryTestEntityExternalReferenceCode",
											"\"" +
												ercAssetLibraryTestEntity.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/assetLibraryERCAssetLibraryTestEntity"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				ercAssetLibraryTestEntity,
				ERCAssetLibraryTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"assetLibraryERCAssetLibraryTestEntity",
									new HashMap<String, Object>() {
										{
											put(
												"assetLibraryExternalReferenceCode",
												"\"" +
													ercAssetLibraryTestEntity.
														getAssetLibraryExternalReferenceCode() +
															"\"");
											put(
												"ercAssetLibraryTestEntityExternalReferenceCode",
												"\"" +
													ercAssetLibraryTestEntity.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/assetLibraryERCAssetLibraryTestEntity"))));
	}

	@Test
	public void testGraphQLGetAssetLibraryERCAssetLibraryTestEntityNotFound()
		throws Exception {

		String irrelevantErcAssetLibraryTestEntityExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"assetLibraryERCAssetLibraryTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryExternalReferenceCode",
									"\"" +
										irrelevantDepotEntryGroup.
											getExternalReferenceCode() + "\"");
								put(
									"ercAssetLibraryTestEntityExternalReferenceCode",
									irrelevantErcAssetLibraryTestEntityExternalReferenceCode);
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
							"assetLibraryERCAssetLibraryTestEntity",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryExternalReferenceCode",
										"\"" +
											irrelevantDepotEntryGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"ercAssetLibraryTestEntityExternalReferenceCode",
										irrelevantErcAssetLibraryTestEntityExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ERCAssetLibraryTestEntity
			testGraphQLGetAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();
	}

	@Test
	public void testGetAssetLibraryERCAssetLibraryTestEntityPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testGetAssetLibraryERCAssetLibraryTestEntityPermissionsPage_addERCAssetLibraryTestEntity();

		Page<Permission> page =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
					testDepotEntryGroup.getExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode(),
					RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected ERCAssetLibraryTestEntity
			testGetAssetLibraryERCAssetLibraryTestEntityPermissionsPage_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testGraphQLGetAssetLibraryERCAssetLibraryTestEntityPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testGraphQLGetAssetLibraryERCAssetLibraryTestEntityPermissionsPage_addERCAssetLibraryTestEntity();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryERCAssetLibraryTestEntityPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"assetLibraryExternalReferenceCode",
						"\"" +
							postERCAssetLibraryTestEntity.
								getAssetLibraryExternalReferenceCode() + "\"");
					put(
						"ercAssetLibraryTestEntityExternalReferenceCode",
						"\"" +
							postERCAssetLibraryTestEntity.
								getExternalReferenceCode() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject assetLibraryERCAssetLibraryTestEntityPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryERCAssetLibraryTestEntityPermissions");

		Assert.assertNotNull(
			assetLibraryERCAssetLibraryTestEntityPermissionsJSONObject);
	}

	protected ERCAssetLibraryTestEntity
			testGraphQLGetAssetLibraryERCAssetLibraryTestEntityPermissionsPage_addERCAssetLibraryTestEntity()
		throws Exception {

		return testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();
	}

	@Test
	public void testPostAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity randomERCAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testPostAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				randomERCAssetLibraryTestEntity);

		assertEquals(
			randomERCAssetLibraryTestEntity, postERCAssetLibraryTestEntity);
		assertValid(postERCAssetLibraryTestEntity);

		ERCAssetLibraryTestEntity randomPermissionsERCAssetLibraryTestEntity1 =
			randomPermissionsERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity postPermissionsERCAssetLibraryTestEntity1 =
			testPostAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				randomPermissionsERCAssetLibraryTestEntity1);

		Assert.assertNull(
			postPermissionsERCAssetLibraryTestEntity1.getPermissions());

		ERCAssetLibraryTestEntity randomPermissionsERCAssetLibraryTestEntity2 =
			randomPermissionsERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity postPermissionsERCAssetLibraryTestEntity2 =
			testPostAssetLibraryERCAssetLibraryTestEntity_addPermissionsERCAssetLibraryTestEntity(
				randomPermissionsERCAssetLibraryTestEntity2);

		Assert.assertNotNull(
			postPermissionsERCAssetLibraryTestEntity2.getPermissions());
	}

	protected ERCAssetLibraryTestEntity
			testPostAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ERCAssetLibraryTestEntity
			testPostAssetLibraryERCAssetLibraryTestEntity_addPermissionsERCAssetLibraryTestEntity(
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		return permissionsERCAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getAssetLibraryExternalReferenceCode(),
				ercAssetLibraryTestEntity);
	}

	@Test
	public void testGraphQLPostAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity randomERCAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity);

		Assert.assertTrue(
			equals(randomERCAssetLibraryTestEntity, ercAssetLibraryTestEntity));
	}

	@Test
	public void testPutAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testPutAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity randomERCAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity putERCAssetLibraryTestEntity =
			ercAssetLibraryTestEntityResource.
				putAssetLibraryERCAssetLibraryTestEntity(
					postERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode(),
					randomERCAssetLibraryTestEntity);

		assertEquals(
			randomERCAssetLibraryTestEntity, putERCAssetLibraryTestEntity);
		assertValid(putERCAssetLibraryTestEntity);

		Assert.assertNull(putERCAssetLibraryTestEntity.getPermissions());

		ERCAssetLibraryTestEntity getERCAssetLibraryTestEntity =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntity(
					putERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					putERCAssetLibraryTestEntity.getExternalReferenceCode());

		assertEquals(
			randomERCAssetLibraryTestEntity, getERCAssetLibraryTestEntity);
		assertValid(getERCAssetLibraryTestEntity);

		ERCAssetLibraryTestEntity randomPermissionsERCAssetLibraryTestEntity =
			randomPermissionsERCAssetLibraryTestEntity();

		putERCAssetLibraryTestEntity =
			ercAssetLibraryTestEntityResource.
				putAssetLibraryERCAssetLibraryTestEntity(
					postERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode(),
					randomPermissionsERCAssetLibraryTestEntity);

		assertEquals(
			randomPermissionsERCAssetLibraryTestEntity,
			putERCAssetLibraryTestEntity);
		assertValid(putERCAssetLibraryTestEntity);

		Assert.assertNull(putERCAssetLibraryTestEntity.getPermissions());

		putERCAssetLibraryTestEntity =
			permissionsERCAssetLibraryTestEntityResource.
				putAssetLibraryERCAssetLibraryTestEntity(
					postERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode(),
					randomPermissionsERCAssetLibraryTestEntity);

		Assert.assertNotNull(putERCAssetLibraryTestEntity.getPermissions());
	}

	protected ERCAssetLibraryTestEntity
			testPutAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testPutAssetLibraryERCAssetLibraryTestEntityPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			testPutAssetLibraryERCAssetLibraryTestEntityPermissionsPage_addERCAssetLibraryTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			ercAssetLibraryTestEntityResource.
				putAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
					testDepotEntryGroup.getExternalReferenceCode(),
					ercAssetLibraryTestEntity.getExternalReferenceCode(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"PERMISSIONS"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			ercAssetLibraryTestEntityResource.
				putAssetLibraryERCAssetLibraryTestEntityPermissionsPageHttpResponse(
					testDepotEntryGroup.getExternalReferenceCode(),
					ercAssetLibraryTestEntity.getExternalReferenceCode(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected ERCAssetLibraryTestEntity
			testPutAssetLibraryERCAssetLibraryTestEntityPermissionsPage_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			testBatchEngineDeleteImportTask_addAssetLibraryERCAssetLibraryTestEntity();

		testBatchEngineDeleteImportTask_deleteERCAssetLibraryTestEntity(
			200, ercAssetLibraryTestEntity1.getExternalReferenceCode(),
			"assetLibraryExternalReferenceCode",
			testDepotEntryGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity1.
						getAssetLibraryExternalReferenceCode(),
					ercAssetLibraryTestEntity1.getExternalReferenceCode()));
	}

	protected ERCAssetLibraryTestEntity
			testBatchEngineDeleteImportTask_addAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		return testDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteERCAssetLibraryTestEntity(
				int expectedStatusCode, String externalReferenceCode,
				String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCAssetLibraryTestEntity",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected ERCAssetLibraryTestEntity
			testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
			testDepotEntryGroup.getExternalReferenceCode(),
			randomERCAssetLibraryTestEntity());
	}

	protected ERCAssetLibraryTestEntity
			testGraphQLAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				String assetLibraryExternalReferenceCode,
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		JSONDeserializer<ERCAssetLibraryTestEntity> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ERCAssetLibraryTestEntity.class)) {

			if (getGraphQLValue(field.get(ercAssetLibraryTestEntity)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(
					getGraphQLValue(field.get(ercAssetLibraryTestEntity)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryERCAssetLibraryTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryExternalReferenceCode",
									"\"" + assetLibraryExternalReferenceCode +
										"\"");
								put("ercAssetLibraryTestEntity", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createAssetLibraryERCAssetLibraryTestEntity"),
			ERCAssetLibraryTestEntity.class);
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
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity,
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities) {

		boolean contains = false;

		for (ERCAssetLibraryTestEntity item : ercAssetLibraryTestEntities) {
			if (equals(ercAssetLibraryTestEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			ercAssetLibraryTestEntities + " does not contain " +
				ercAssetLibraryTestEntity,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1,
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2) {

		Assert.assertTrue(
			ercAssetLibraryTestEntity1 + " does not equal " +
				ercAssetLibraryTestEntity2,
			equals(ercAssetLibraryTestEntity1, ercAssetLibraryTestEntity2));
	}

	protected void assertEquals(
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities1,
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities2) {

		Assert.assertEquals(
			ercAssetLibraryTestEntities1.size(),
			ercAssetLibraryTestEntities2.size());

		for (int i = 0; i < ercAssetLibraryTestEntities1.size(); i++) {
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
				ercAssetLibraryTestEntities1.get(i);
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
				ercAssetLibraryTestEntities2.get(i);

			assertEquals(
				ercAssetLibraryTestEntity1, ercAssetLibraryTestEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities1,
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities2) {

		Assert.assertEquals(
			ercAssetLibraryTestEntities1.size(),
			ercAssetLibraryTestEntities2.size());

		for (ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 :
				ercAssetLibraryTestEntities1) {

			boolean contains = false;

			for (ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 :
					ercAssetLibraryTestEntities2) {

				if (equals(
						ercAssetLibraryTestEntity1,
						ercAssetLibraryTestEntity2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				ercAssetLibraryTestEntities2 + " does not contain " +
					ercAssetLibraryTestEntity1,
				contains);
		}
	}

	protected void assertValid(
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		boolean valid = true;

		if (ercAssetLibraryTestEntity.getDateCreated() == null) {
			valid = false;
		}

		if (ercAssetLibraryTestEntity.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"assetLibraryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (ercAssetLibraryTestEntity.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (ercAssetLibraryTestEntity.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (ercAssetLibraryTestEntity.getPermissions() == null) {
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

	protected void assertValid(Page<ERCAssetLibraryTestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ERCAssetLibraryTestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ERCAssetLibraryTestEntity>
			ercAssetLibraryTestEntities = page.getItems();

		int size = ercAssetLibraryTestEntities.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						ERCAssetLibraryTestEntity.class)) {

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
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1,
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2) {

		if (ercAssetLibraryTestEntity1 == ercAssetLibraryTestEntity2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"assetLibraryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.
							getAssetLibraryExternalReferenceCode(),
						ercAssetLibraryTestEntity2.
							getAssetLibraryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getDateCreated(),
						ercAssetLibraryTestEntity2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getDateModified(),
						ercAssetLibraryTestEntity2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getDescription(),
						ercAssetLibraryTestEntity2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getExternalReferenceCode(),
						ercAssetLibraryTestEntity2.
							getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getPermissions(),
						ercAssetLibraryTestEntity2.getPermissions())) {

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

		if (!(_ercAssetLibraryTestEntityResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_ercAssetLibraryTestEntityResource;

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
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetLibraryExternalReferenceCode")) {
			Object object =
				ercAssetLibraryTestEntity.
					getAssetLibraryExternalReferenceCode();

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
				Date date = ercAssetLibraryTestEntity.getDateCreated();

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

				sb.append(
					_format.format(ercAssetLibraryTestEntity.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = ercAssetLibraryTestEntity.getDateModified();

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

				sb.append(
					_format.format(
						ercAssetLibraryTestEntity.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = ercAssetLibraryTestEntity.getDescription();

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
			Object object =
				ercAssetLibraryTestEntity.getExternalReferenceCode();

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

		if (entityFieldName.equals("permissions")) {
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

	protected ERCAssetLibraryTestEntity randomERCAssetLibraryTestEntity()
		throws Exception {

		return new ERCAssetLibraryTestEntity() {
			{
				assetLibraryExternalReferenceCode =
					testDepotEntryGroup.getExternalReferenceCode();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ERCAssetLibraryTestEntity
			randomIrrelevantERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity randomIrrelevantERCAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		randomIrrelevantERCAssetLibraryTestEntity.
			setAssetLibraryExternalReferenceCode(
				irrelevantDepotEntryGroup.getExternalReferenceCode());

		return randomIrrelevantERCAssetLibraryTestEntity;
	}

	protected ERCAssetLibraryTestEntity randomPatchERCAssetLibraryTestEntity()
		throws Exception {

		return randomERCAssetLibraryTestEntity();
	}

	protected ERCAssetLibraryTestEntity
			randomPermissionsERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		ercAssetLibraryTestEntity.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return ercAssetLibraryTestEntity;
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected ERCAssetLibraryTestEntityResource
		ercAssetLibraryTestEntityResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected ERCAssetLibraryTestEntityResource
		permissionsERCAssetLibraryTestEntityResource;
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
		LogFactoryUtil.getLog(
			BaseERCAssetLibraryTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		ERCAssetLibraryTestEntityResource _ercAssetLibraryTestEntityResource;

}