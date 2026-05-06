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
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.SharedInternalModelBatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.SharedInternalModelBatchTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.SharedInternalModelBatchTestEntitySerDes;
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
public abstract class BaseSharedInternalModelBatchTestEntityResourceTestCase {

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

		_sharedInternalModelBatchTestEntityResource.setContextCompany(
			testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		sharedInternalModelBatchTestEntityResource =
			SharedInternalModelBatchTestEntityResource.builder(
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
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity1 =
			randomSharedInternalModelBatchTestEntity();

		String json = objectMapper.writeValueAsString(
			sharedInternalModelBatchTestEntity1);

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity2 =
			SharedInternalModelBatchTestEntitySerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				sharedInternalModelBatchTestEntity1,
				sharedInternalModelBatchTestEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity =
			randomSharedInternalModelBatchTestEntity();

		String json1 = objectMapper.writeValueAsString(
			sharedInternalModelBatchTestEntity);
		String json2 = SharedInternalModelBatchTestEntitySerDes.toJSON(
			sharedInternalModelBatchTestEntity);

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

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity =
			randomSharedInternalModelBatchTestEntity();

		sharedInternalModelBatchTestEntity.setExternalReferenceCode(regex);
		sharedInternalModelBatchTestEntity.setName(regex);

		String json = SharedInternalModelBatchTestEntitySerDes.toJSON(
			sharedInternalModelBatchTestEntity);

		Assert.assertFalse(json.contains(regex));

		sharedInternalModelBatchTestEntity =
			SharedInternalModelBatchTestEntitySerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			sharedInternalModelBatchTestEntity.getExternalReferenceCode());
		Assert.assertEquals(
			regex, sharedInternalModelBatchTestEntity.getName());
	}

	@Test
	public void testDeleteSharedInternalModelBatchTestEntityByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity =
			testDeleteSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity();

		assertHttpResponseStatusCode(
			204,
			sharedInternalModelBatchTestEntityResource.
				deleteSharedInternalModelBatchTestEntityByExternalReferenceCodeHttpResponse(
					sharedInternalModelBatchTestEntity.
						getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			sharedInternalModelBatchTestEntityResource.
				getSharedInternalModelBatchTestEntityByExternalReferenceCodeHttpResponse(
					sharedInternalModelBatchTestEntity.
						getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			sharedInternalModelBatchTestEntityResource.
				getSharedInternalModelBatchTestEntityByExternalReferenceCodeHttpResponse(
					"-"));
	}

	protected SharedInternalModelBatchTestEntity
			testDeleteSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSharedInternalModelBatchTestEntityByExternalReferenceCode()
		throws Exception {

		// No namespace

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity1 =
			testGraphQLDeleteSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSharedInternalModelBatchTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										sharedInternalModelBatchTestEntity1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSharedInternalModelBatchTestEntityByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"sharedInternalModelBatchTestEntityByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									sharedInternalModelBatchTestEntity1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace test_v1_0

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity2 =
			testGraphQLDeleteSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"deleteSharedInternalModelBatchTestEntityByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											sharedInternalModelBatchTestEntity2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/test_v1_0",
				"Object/deleteSharedInternalModelBatchTestEntityByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"test_v1_0",
					new GraphQLField(
						"sharedInternalModelBatchTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										sharedInternalModelBatchTestEntity2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected SharedInternalModelBatchTestEntity
			testGraphQLDeleteSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity()
		throws Exception {

		return testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity();
	}

	@Test
	public void testGetSharedInternalModelBatchTestEntitiesPage()
		throws Exception {

		Page<SharedInternalModelBatchTestEntity> page =
			sharedInternalModelBatchTestEntityResource.
				getSharedInternalModelBatchTestEntitiesPage();

		long totalCount = page.getTotalCount();

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity1 =
			testGetSharedInternalModelBatchTestEntitiesPage_addSharedInternalModelBatchTestEntity(
				randomSharedInternalModelBatchTestEntity());

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity2 =
			testGetSharedInternalModelBatchTestEntitiesPage_addSharedInternalModelBatchTestEntity(
				randomSharedInternalModelBatchTestEntity());

		page =
			sharedInternalModelBatchTestEntityResource.
				getSharedInternalModelBatchTestEntitiesPage();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			sharedInternalModelBatchTestEntity1,
			(List<SharedInternalModelBatchTestEntity>)page.getItems());
		assertContains(
			sharedInternalModelBatchTestEntity2,
			(List<SharedInternalModelBatchTestEntity>)page.getItems());
		assertValid(
			page,
			testGetSharedInternalModelBatchTestEntitiesPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetSharedInternalModelBatchTestEntitiesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected SharedInternalModelBatchTestEntity
			testGetSharedInternalModelBatchTestEntitiesPage_addSharedInternalModelBatchTestEntity(
				SharedInternalModelBatchTestEntity
					sharedInternalModelBatchTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSharedInternalModelBatchTestEntitiesPage()
		throws Exception {

		GraphQLField graphQLField = new GraphQLField(
			"sharedInternalModelBatchTestEntities",
			new HashMap<String, Object>() {
				{
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject sharedInternalModelBatchTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/sharedInternalModelBatchTestEntities");

		long totalCount =
			sharedInternalModelBatchTestEntitiesJSONObject.getLong(
				"totalCount");

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity1 =
			testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity(
				randomSharedInternalModelBatchTestEntity());

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity2 =
			testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity(
				randomSharedInternalModelBatchTestEntity());

		sharedInternalModelBatchTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/sharedInternalModelBatchTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			sharedInternalModelBatchTestEntitiesJSONObject.getLong(
				"totalCount"));

		assertContains(
			sharedInternalModelBatchTestEntity1,
			Arrays.asList(
				SharedInternalModelBatchTestEntitySerDes.toDTOs(
					sharedInternalModelBatchTestEntitiesJSONObject.getString(
						"items"))));
		assertContains(
			sharedInternalModelBatchTestEntity2,
			Arrays.asList(
				SharedInternalModelBatchTestEntitySerDes.toDTOs(
					sharedInternalModelBatchTestEntitiesJSONObject.getString(
						"items"))));

		// Using the namespace test_v1_0

		sharedInternalModelBatchTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(new GraphQLField("test_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/test_v1_0",
				"JSONObject/sharedInternalModelBatchTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			sharedInternalModelBatchTestEntitiesJSONObject.getLong(
				"totalCount"));

		assertContains(
			sharedInternalModelBatchTestEntity1,
			Arrays.asList(
				SharedInternalModelBatchTestEntitySerDes.toDTOs(
					sharedInternalModelBatchTestEntitiesJSONObject.getString(
						"items"))));
		assertContains(
			sharedInternalModelBatchTestEntity2,
			Arrays.asList(
				SharedInternalModelBatchTestEntitySerDes.toDTOs(
					sharedInternalModelBatchTestEntitiesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetSharedInternalModelBatchTestEntityByExternalReferenceCode()
		throws Exception {

		SharedInternalModelBatchTestEntity
			postSharedInternalModelBatchTestEntity =
				testGetSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity();

		SharedInternalModelBatchTestEntity
			getSharedInternalModelBatchTestEntity =
				sharedInternalModelBatchTestEntityResource.
					getSharedInternalModelBatchTestEntityByExternalReferenceCode(
						postSharedInternalModelBatchTestEntity.
							getExternalReferenceCode());

		assertEquals(
			postSharedInternalModelBatchTestEntity,
			getSharedInternalModelBatchTestEntity);
		assertValid(getSharedInternalModelBatchTestEntity);
	}

	protected SharedInternalModelBatchTestEntity
			testGetSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSharedInternalModelBatchTestEntityByExternalReferenceCode()
		throws Exception {

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity =
			testGraphQLGetSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity();

		// No namespace

		Assert.assertTrue(
			equals(
				sharedInternalModelBatchTestEntity,
				SharedInternalModelBatchTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"sharedInternalModelBatchTestEntityByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												sharedInternalModelBatchTestEntity.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/sharedInternalModelBatchTestEntityByExternalReferenceCode"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				sharedInternalModelBatchTestEntity,
				SharedInternalModelBatchTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"sharedInternalModelBatchTestEntityByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													sharedInternalModelBatchTestEntity.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/sharedInternalModelBatchTestEntityByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSharedInternalModelBatchTestEntityByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"sharedInternalModelBatchTestEntityByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
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
							"sharedInternalModelBatchTestEntityByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected SharedInternalModelBatchTestEntity
			testGraphQLGetSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity()
		throws Exception {

		return testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity();
	}

	@Test
	public void testPostSharedInternalModelBatchTestEntity() throws Exception {
		SharedInternalModelBatchTestEntity
			randomSharedInternalModelBatchTestEntity =
				randomSharedInternalModelBatchTestEntity();

		SharedInternalModelBatchTestEntity
			postSharedInternalModelBatchTestEntity =
				testPostSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity(
					randomSharedInternalModelBatchTestEntity);

		assertEquals(
			randomSharedInternalModelBatchTestEntity,
			postSharedInternalModelBatchTestEntity);
		assertValid(postSharedInternalModelBatchTestEntity);
	}

	protected SharedInternalModelBatchTestEntity
			testPostSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity(
				SharedInternalModelBatchTestEntity
					sharedInternalModelBatchTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSharedInternalModelBatchTestEntity()
		throws Exception {

		SharedInternalModelBatchTestEntity
			randomSharedInternalModelBatchTestEntity =
				randomSharedInternalModelBatchTestEntity();

		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity =
			testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity(
				randomSharedInternalModelBatchTestEntity);

		Assert.assertTrue(
			equals(
				randomSharedInternalModelBatchTestEntity,
				sharedInternalModelBatchTestEntity));
	}

	@Test
	public void testPutSharedInternalModelBatchTestEntityByExternalReferenceCode()
		throws Exception {

		SharedInternalModelBatchTestEntity
			postSharedInternalModelBatchTestEntity =
				testPutSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity();

		SharedInternalModelBatchTestEntity
			randomSharedInternalModelBatchTestEntity =
				randomSharedInternalModelBatchTestEntity();

		SharedInternalModelBatchTestEntity
			putSharedInternalModelBatchTestEntity =
				sharedInternalModelBatchTestEntityResource.
					putSharedInternalModelBatchTestEntityByExternalReferenceCode(
						postSharedInternalModelBatchTestEntity.
							getExternalReferenceCode(),
						randomSharedInternalModelBatchTestEntity);

		assertEquals(
			randomSharedInternalModelBatchTestEntity,
			putSharedInternalModelBatchTestEntity);
		assertValid(putSharedInternalModelBatchTestEntity);

		SharedInternalModelBatchTestEntity
			getSharedInternalModelBatchTestEntity =
				sharedInternalModelBatchTestEntityResource.
					getSharedInternalModelBatchTestEntityByExternalReferenceCode(
						putSharedInternalModelBatchTestEntity.
							getExternalReferenceCode());

		assertEquals(
			randomSharedInternalModelBatchTestEntity,
			getSharedInternalModelBatchTestEntity);
		assertValid(getSharedInternalModelBatchTestEntity);

		SharedInternalModelBatchTestEntity
			newSharedInternalModelBatchTestEntity =
				testPutSharedInternalModelBatchTestEntityByExternalReferenceCode_createSharedInternalModelBatchTestEntity();

		putSharedInternalModelBatchTestEntity =
			sharedInternalModelBatchTestEntityResource.
				putSharedInternalModelBatchTestEntityByExternalReferenceCode(
					newSharedInternalModelBatchTestEntity.
						getExternalReferenceCode(),
					newSharedInternalModelBatchTestEntity);

		assertEquals(
			newSharedInternalModelBatchTestEntity,
			putSharedInternalModelBatchTestEntity);
		assertValid(putSharedInternalModelBatchTestEntity);

		getSharedInternalModelBatchTestEntity =
			sharedInternalModelBatchTestEntityResource.
				getSharedInternalModelBatchTestEntityByExternalReferenceCode(
					putSharedInternalModelBatchTestEntity.
						getExternalReferenceCode());

		assertEquals(
			newSharedInternalModelBatchTestEntity,
			getSharedInternalModelBatchTestEntity);

		Assert.assertEquals(
			newSharedInternalModelBatchTestEntity.getExternalReferenceCode(),
			putSharedInternalModelBatchTestEntity.getExternalReferenceCode());
	}

	protected SharedInternalModelBatchTestEntity
			testPutSharedInternalModelBatchTestEntityByExternalReferenceCode_addSharedInternalModelBatchTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected SharedInternalModelBatchTestEntity
			testPutSharedInternalModelBatchTestEntityByExternalReferenceCode_createSharedInternalModelBatchTestEntity()
		throws Exception {

		return randomSharedInternalModelBatchTestEntity();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity1 =
			testBatchEngineDeleteImportTask_addSharedInternalModelBatchTestEntity();

		testBatchEngineDeleteImportTask_deleteSharedInternalModelBatchTestEntity(
			200,
			sharedInternalModelBatchTestEntity1.getExternalReferenceCode());
	}

	protected SharedInternalModelBatchTestEntity
			testBatchEngineDeleteImportTask_addSharedInternalModelBatchTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void
			testBatchEngineDeleteImportTask_deleteSharedInternalModelBatchTestEntity(
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
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.SharedInternalModelBatchTestEntity",
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

	protected SharedInternalModelBatchTestEntity
			testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity()
		throws Exception {

		return testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity(
			randomSharedInternalModelBatchTestEntity());
	}

	protected SharedInternalModelBatchTestEntity
			testGraphQLSharedInternalModelBatchTestEntity_addSharedInternalModelBatchTestEntity(
				SharedInternalModelBatchTestEntity
					sharedInternalModelBatchTestEntity)
		throws Exception {

		JSONDeserializer<SharedInternalModelBatchTestEntity> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(SharedInternalModelBatchTestEntity.class)) {

			if (getGraphQLValue(
					field.get(sharedInternalModelBatchTestEntity)) != null) {

				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(
					getGraphQLValue(
						field.get(sharedInternalModelBatchTestEntity)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSharedInternalModelBatchTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"sharedInternalModelBatchTestEntity",
									sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createSharedInternalModelBatchTestEntity"),
			SharedInternalModelBatchTestEntity.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date) {
			Date date = (Date)value;

			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum) {
			Enum<?> enm = (Enum<?>)value;

			return enm.name();
		}
		else if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)value;

			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[]) {
			Object[] array = (Object[])value;

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
		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity,
		List<SharedInternalModelBatchTestEntity>
			sharedInternalModelBatchTestEntities) {

		boolean contains = false;

		for (SharedInternalModelBatchTestEntity item :
				sharedInternalModelBatchTestEntities) {

			if (equals(sharedInternalModelBatchTestEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			sharedInternalModelBatchTestEntities + " does not contain " +
				sharedInternalModelBatchTestEntity,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity1,
		SharedInternalModelBatchTestEntity
			sharedInternalModelBatchTestEntity2) {

		Assert.assertTrue(
			sharedInternalModelBatchTestEntity1 + " does not equal " +
				sharedInternalModelBatchTestEntity2,
			equals(
				sharedInternalModelBatchTestEntity1,
				sharedInternalModelBatchTestEntity2));
	}

	protected void assertEquals(
		List<SharedInternalModelBatchTestEntity>
			sharedInternalModelBatchTestEntities1,
		List<SharedInternalModelBatchTestEntity>
			sharedInternalModelBatchTestEntities2) {

		Assert.assertEquals(
			sharedInternalModelBatchTestEntities1.size(),
			sharedInternalModelBatchTestEntities2.size());

		for (int i = 0; i < sharedInternalModelBatchTestEntities1.size(); i++) {
			SharedInternalModelBatchTestEntity
				sharedInternalModelBatchTestEntity1 =
					sharedInternalModelBatchTestEntities1.get(i);
			SharedInternalModelBatchTestEntity
				sharedInternalModelBatchTestEntity2 =
					sharedInternalModelBatchTestEntities2.get(i);

			assertEquals(
				sharedInternalModelBatchTestEntity1,
				sharedInternalModelBatchTestEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SharedInternalModelBatchTestEntity>
			sharedInternalModelBatchTestEntities1,
		List<SharedInternalModelBatchTestEntity>
			sharedInternalModelBatchTestEntities2) {

		Assert.assertEquals(
			sharedInternalModelBatchTestEntities1.size(),
			sharedInternalModelBatchTestEntities2.size());

		for (SharedInternalModelBatchTestEntity
				sharedInternalModelBatchTestEntity1 :
					sharedInternalModelBatchTestEntities1) {

			boolean contains = false;

			for (SharedInternalModelBatchTestEntity
					sharedInternalModelBatchTestEntity2 :
						sharedInternalModelBatchTestEntities2) {

				if (equals(
						sharedInternalModelBatchTestEntity1,
						sharedInternalModelBatchTestEntity2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				sharedInternalModelBatchTestEntities2 + " does not contain " +
					sharedInternalModelBatchTestEntity1,
				contains);
		}
	}

	protected void assertValid(
			SharedInternalModelBatchTestEntity
				sharedInternalModelBatchTestEntity)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (sharedInternalModelBatchTestEntity.
						getExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (sharedInternalModelBatchTestEntity.getName() == null) {
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

	protected void assertValid(Page<SharedInternalModelBatchTestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SharedInternalModelBatchTestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SharedInternalModelBatchTestEntity>
			sharedInternalModelBatchTestEntities = page.getItems();

		int size = sharedInternalModelBatchTestEntities.size();

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
						SharedInternalModelBatchTestEntity.class)) {

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
		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity1,
		SharedInternalModelBatchTestEntity
			sharedInternalModelBatchTestEntity2) {

		if (sharedInternalModelBatchTestEntity1 ==
				sharedInternalModelBatchTestEntity2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sharedInternalModelBatchTestEntity1.
							getExternalReferenceCode(),
						sharedInternalModelBatchTestEntity2.
							getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedInternalModelBatchTestEntity1.getName(),
						sharedInternalModelBatchTestEntity2.getName())) {

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

		if (!(_sharedInternalModelBatchTestEntityResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_sharedInternalModelBatchTestEntityResource;

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
		SharedInternalModelBatchTestEntity sharedInternalModelBatchTestEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object =
				sharedInternalModelBatchTestEntity.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = sharedInternalModelBatchTestEntity.getName();

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

	protected SharedInternalModelBatchTestEntity
			randomSharedInternalModelBatchTestEntity()
		throws Exception {

		return new SharedInternalModelBatchTestEntity() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected SharedInternalModelBatchTestEntity
			randomIrrelevantSharedInternalModelBatchTestEntity()
		throws Exception {

		SharedInternalModelBatchTestEntity
			randomIrrelevantSharedInternalModelBatchTestEntity =
				randomSharedInternalModelBatchTestEntity();

		return randomIrrelevantSharedInternalModelBatchTestEntity;
	}

	protected SharedInternalModelBatchTestEntity
			randomPatchSharedInternalModelBatchTestEntity()
		throws Exception {

		return randomSharedInternalModelBatchTestEntity();
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

	protected SharedInternalModelBatchTestEntityResource
		sharedInternalModelBatchTestEntityResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
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
			BaseSharedInternalModelBatchTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		SharedInternalModelBatchTestEntityResource
			_sharedInternalModelBatchTestEntityResource;

}
// LIFERAY-REST-BUILDER-HASH:563889637