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
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
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
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ERCSiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.permission.Permission;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.ERCSiteTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ERCSiteTestEntitySerDes;
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
public abstract class BaseERCSiteTestEntityResourceTestCase {

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

		_ercSiteTestEntityResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		ercSiteTestEntityResource = ERCSiteTestEntityResource.builder(
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

		permissionsERCSiteTestEntityResource =
			ERCSiteTestEntityResource.builder(
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

		ERCSiteTestEntity ercSiteTestEntity1 = randomERCSiteTestEntity();

		String json = objectMapper.writeValueAsString(ercSiteTestEntity1);

		ERCSiteTestEntity ercSiteTestEntity2 = ERCSiteTestEntitySerDes.toDTO(
			json);

		Assert.assertTrue(equals(ercSiteTestEntity1, ercSiteTestEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ERCSiteTestEntity ercSiteTestEntity = randomERCSiteTestEntity();

		String json1 = objectMapper.writeValueAsString(ercSiteTestEntity);
		String json2 = ERCSiteTestEntitySerDes.toJSON(ercSiteTestEntity);

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

		ERCSiteTestEntity ercSiteTestEntity = randomERCSiteTestEntity();

		ercSiteTestEntity.setDescription(regex);
		ercSiteTestEntity.setExternalReferenceCode(regex);
		ercSiteTestEntity.setSiteExternalReferenceCode(regex);

		String json = ERCSiteTestEntitySerDes.toJSON(ercSiteTestEntity);

		Assert.assertFalse(json.contains(regex));

		ercSiteTestEntity = ERCSiteTestEntitySerDes.toDTO(json);

		Assert.assertEquals(regex, ercSiteTestEntity.getDescription());
		Assert.assertEquals(
			regex, ercSiteTestEntity.getExternalReferenceCode());
		Assert.assertEquals(
			regex, ercSiteTestEntity.getSiteExternalReferenceCode());
	}

	@Test
	public void testDeleteSiteERCSiteTestEntity() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCSiteTestEntity ercSiteTestEntity =
			testDeleteSiteERCSiteTestEntity_addERCSiteTestEntity();

		assertHttpResponseStatusCode(
			204,
			ercSiteTestEntityResource.deleteSiteERCSiteTestEntityHttpResponse(
				ercSiteTestEntity.getSiteExternalReferenceCode(),
				ercSiteTestEntity.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			ercSiteTestEntityResource.getSiteERCSiteTestEntityHttpResponse(
				ercSiteTestEntity.getSiteExternalReferenceCode(),
				ercSiteTestEntity.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			ercSiteTestEntityResource.getSiteERCSiteTestEntityHttpResponse(
				ercSiteTestEntity.getSiteExternalReferenceCode(), "-"));
	}

	protected ERCSiteTestEntity
			testDeleteSiteERCSiteTestEntity_addERCSiteTestEntity()
		throws Exception {

		return ercSiteTestEntityResource.postSiteERCSiteTestEntity(
			testGroup.getExternalReferenceCode(), randomERCSiteTestEntity());
	}

	@Test
	public void testGraphQLDeleteSiteERCSiteTestEntity() throws Exception {

		// No namespace

		ERCSiteTestEntity ercSiteTestEntity1 =
			testGraphQLDeleteSiteERCSiteTestEntity_addERCSiteTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteERCSiteTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										ercSiteTestEntity1.
											getSiteExternalReferenceCode() +
												"\"");
								put(
									"ercSiteTestEntityExternalReferenceCode",
									"\"" +
										ercSiteTestEntity1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data", "Object/deleteSiteERCSiteTestEntity"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"eRCSiteTestEntity",
					new HashMap<String, Object>() {
						{
							put(
								"siteExternalReferenceCode",
								"\"" +
									ercSiteTestEntity1.
										getSiteExternalReferenceCode() + "\"");
							put(
								"ercSiteTestEntityExternalReferenceCode",
								"\"" +
									ercSiteTestEntity1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace test_v1_0

		ERCSiteTestEntity ercSiteTestEntity2 =
			testGraphQLDeleteSiteERCSiteTestEntity_addERCSiteTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"deleteSiteERCSiteTestEntity",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											ercSiteTestEntity2.
												getSiteExternalReferenceCode() +
													"\"");
									put(
										"ercSiteTestEntityExternalReferenceCode",
										"\"" +
											ercSiteTestEntity2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/test_v1_0",
				"Object/deleteSiteERCSiteTestEntity"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"test_v1_0",
					new GraphQLField(
						"eRCSiteTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										ercSiteTestEntity2.
											getSiteExternalReferenceCode() +
												"\"");
								put(
									"ercSiteTestEntityExternalReferenceCode",
									"\"" +
										ercSiteTestEntity2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ERCSiteTestEntity
			testGraphQLDeleteSiteERCSiteTestEntity_addERCSiteTestEntity()
		throws Exception {

		return testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity();
	}

	@Test
	public void testGetSiteERCSiteTestEntitiesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteERCSiteTestEntitiesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteERCSiteTestEntitiesPage_getIrrelevantSiteExternalReferenceCode();

		Page<ERCSiteTestEntity> page =
			ercSiteTestEntityResource.getSiteERCSiteTestEntitiesPage(
				siteExternalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			ERCSiteTestEntity irrelevantERCSiteTestEntity =
				testGetSiteERCSiteTestEntitiesPage_addERCSiteTestEntity(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantERCSiteTestEntity());

			page = ercSiteTestEntityResource.getSiteERCSiteTestEntitiesPage(
				irrelevantSiteExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantERCSiteTestEntity,
				(List<ERCSiteTestEntity>)page.getItems());
			assertValid(
				page,
				testGetSiteERCSiteTestEntitiesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		ERCSiteTestEntity ercSiteTestEntity1 =
			testGetSiteERCSiteTestEntitiesPage_addERCSiteTestEntity(
				siteExternalReferenceCode, randomERCSiteTestEntity());

		ERCSiteTestEntity ercSiteTestEntity2 =
			testGetSiteERCSiteTestEntitiesPage_addERCSiteTestEntity(
				siteExternalReferenceCode, randomERCSiteTestEntity());

		page = ercSiteTestEntityResource.getSiteERCSiteTestEntitiesPage(
			siteExternalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			ercSiteTestEntity1, (List<ERCSiteTestEntity>)page.getItems());
		assertContains(
			ercSiteTestEntity2, (List<ERCSiteTestEntity>)page.getItems());
		assertValid(
			page,
			testGetSiteERCSiteTestEntitiesPage_getExpectedActions(
				siteExternalReferenceCode));

		for (ERCSiteTestEntity ercSiteTestEntity : page.getItems()) {
			Assert.assertNull(ercSiteTestEntity.getPermissions());
		}

		page =
			permissionsERCSiteTestEntityResource.getSiteERCSiteTestEntitiesPage(
				siteExternalReferenceCode);

		for (ERCSiteTestEntity ercSiteTestEntity : page.getItems()) {
			Assert.assertNotNull(ercSiteTestEntity.getPermissions());
		}
	}

	protected Map<String, Map<String, String>>
			testGetSiteERCSiteTestEntitiesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/test/v1.0/sites/{siteExternalReferenceCode}/erc-site-test-entities/batch".
				replace(
					"{siteExternalReferenceCode}",
					String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected ERCSiteTestEntity
			testGetSiteERCSiteTestEntitiesPage_addERCSiteTestEntity(
				String siteExternalReferenceCode,
				ERCSiteTestEntity ercSiteTestEntity)
		throws Exception {

		return ercSiteTestEntityResource.postSiteERCSiteTestEntity(
			siteExternalReferenceCode, ercSiteTestEntity);
	}

	protected String
			testGetSiteERCSiteTestEntitiesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteERCSiteTestEntitiesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testGraphQLGetSiteERCSiteTestEntitiesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteERCSiteTestEntitiesPage_getSiteExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"eRCSiteTestEntities",
			new HashMap<String, Object>() {
				{
					put(
						"siteExternalReferenceCode",
						"\"" + siteExternalReferenceCode + "\"");
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject eRCSiteTestEntitiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/eRCSiteTestEntities");

		long totalCount = eRCSiteTestEntitiesJSONObject.getLong("totalCount");

		ERCSiteTestEntity ercSiteTestEntity1 =
			testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity(
				siteExternalReferenceCode, randomERCSiteTestEntity());

		ERCSiteTestEntity ercSiteTestEntity2 =
			testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity(
				siteExternalReferenceCode, randomERCSiteTestEntity());

		eRCSiteTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/eRCSiteTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			eRCSiteTestEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			ercSiteTestEntity1,
			Arrays.asList(
				ERCSiteTestEntitySerDes.toDTOs(
					eRCSiteTestEntitiesJSONObject.getString("items"))));
		assertContains(
			ercSiteTestEntity2,
			Arrays.asList(
				ERCSiteTestEntitySerDes.toDTOs(
					eRCSiteTestEntitiesJSONObject.getString("items"))));

		// Using the namespace test_v1_0

		eRCSiteTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(new GraphQLField("test_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/test_v1_0",
			"JSONObject/eRCSiteTestEntities");

		Assert.assertEquals(
			totalCount + 2,
			eRCSiteTestEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			ercSiteTestEntity1,
			Arrays.asList(
				ERCSiteTestEntitySerDes.toDTOs(
					eRCSiteTestEntitiesJSONObject.getString("items"))));
		assertContains(
			ercSiteTestEntity2,
			Arrays.asList(
				ERCSiteTestEntitySerDes.toDTOs(
					eRCSiteTestEntitiesJSONObject.getString("items"))));
	}

	@Test
	public void testGetSiteERCSiteTestEntity() throws Exception {
		ERCSiteTestEntity postERCSiteTestEntity =
			testGetSiteERCSiteTestEntity_addERCSiteTestEntity();

		ERCSiteTestEntity getERCSiteTestEntity =
			ercSiteTestEntityResource.getSiteERCSiteTestEntity(
				postERCSiteTestEntity.getSiteExternalReferenceCode(),
				postERCSiteTestEntity.getExternalReferenceCode());

		assertEquals(postERCSiteTestEntity, getERCSiteTestEntity);
		assertValid(getERCSiteTestEntity);

		Assert.assertNull(getERCSiteTestEntity.getPermissions());

		getERCSiteTestEntity =
			permissionsERCSiteTestEntityResource.getSiteERCSiteTestEntity(
				postERCSiteTestEntity.getSiteExternalReferenceCode(),
				postERCSiteTestEntity.getExternalReferenceCode());

		Assert.assertNotNull(getERCSiteTestEntity.getPermissions());
	}

	protected ERCSiteTestEntity
			testGetSiteERCSiteTestEntity_addERCSiteTestEntity()
		throws Exception {

		return ercSiteTestEntityResource.postSiteERCSiteTestEntity(
			testGroup.getExternalReferenceCode(), randomERCSiteTestEntity());
	}

	@Test
	public void testGraphQLGetSiteERCSiteTestEntity() throws Exception {
		ERCSiteTestEntity ercSiteTestEntity =
			testGraphQLGetSiteERCSiteTestEntity_addERCSiteTestEntity();

		// No namespace

		Assert.assertTrue(
			equals(
				ercSiteTestEntity,
				ERCSiteTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"eRCSiteTestEntity",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												ercSiteTestEntity.
													getSiteExternalReferenceCode() +
														"\"");
										put(
											"ercSiteTestEntityExternalReferenceCode",
											"\"" +
												ercSiteTestEntity.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/eRCSiteTestEntity"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				ercSiteTestEntity,
				ERCSiteTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"eRCSiteTestEntity",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													ercSiteTestEntity.
														getSiteExternalReferenceCode() +
															"\"");
											put(
												"ercSiteTestEntityExternalReferenceCode",
												"\"" +
													ercSiteTestEntity.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/eRCSiteTestEntity"))));
	}

	@Test
	public void testGraphQLGetSiteERCSiteTestEntityNotFound() throws Exception {
		String irrelevantErcSiteTestEntityExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"eRCSiteTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										irrelevantGroup.
											getExternalReferenceCode() + "\"");
								put(
									"ercSiteTestEntityExternalReferenceCode",
									irrelevantErcSiteTestEntityExternalReferenceCode);
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
							"eRCSiteTestEntity",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											irrelevantGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"ercSiteTestEntityExternalReferenceCode",
										irrelevantErcSiteTestEntityExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ERCSiteTestEntity
			testGraphQLGetSiteERCSiteTestEntity_addERCSiteTestEntity()
		throws Exception {

		return testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity();
	}

	@Test
	public void testGetSiteERCSiteTestEntityPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCSiteTestEntity postERCSiteTestEntity =
			testGetSiteERCSiteTestEntityPermissionsPage_addERCSiteTestEntity();

		Page<Permission> page =
			ercSiteTestEntityResource.getSiteERCSiteTestEntityPermissionsPage(
				testGroup.getExternalReferenceCode(),
				postERCSiteTestEntity.getExternalReferenceCode(),
				RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected ERCSiteTestEntity
			testGetSiteERCSiteTestEntityPermissionsPage_addERCSiteTestEntity()
		throws Exception {

		return ercSiteTestEntityResource.postSiteERCSiteTestEntity(
			testGroup.getExternalReferenceCode(), randomERCSiteTestEntity());
	}

	@Test
	public void testGraphQLGetSiteERCSiteTestEntityPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCSiteTestEntity postERCSiteTestEntity =
			testGraphQLGetSiteERCSiteTestEntityPermissionsPage_addERCSiteTestEntity();

		GraphQLField graphQLField = new GraphQLField(
			"eRCSiteTestEntityPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteExternalReferenceCode",
						"\"" +
							postERCSiteTestEntity.
								getSiteExternalReferenceCode() + "\"");
					put(
						"ercSiteTestEntityExternalReferenceCode",
						"\"" +
							postERCSiteTestEntity.getExternalReferenceCode() +
								"\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject eRCSiteTestEntityPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/eRCSiteTestEntityPermissions");

		Assert.assertNotNull(eRCSiteTestEntityPermissionsJSONObject);
	}

	protected ERCSiteTestEntity
			testGraphQLGetSiteERCSiteTestEntityPermissionsPage_addERCSiteTestEntity()
		throws Exception {

		return testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity();
	}

	@Test
	public void testPostSiteERCSiteTestEntity() throws Exception {
		ERCSiteTestEntity randomERCSiteTestEntity = randomERCSiteTestEntity();

		ERCSiteTestEntity postERCSiteTestEntity =
			testPostSiteERCSiteTestEntity_addERCSiteTestEntity(
				randomERCSiteTestEntity);

		assertEquals(randomERCSiteTestEntity, postERCSiteTestEntity);
		assertValid(postERCSiteTestEntity);

		ERCSiteTestEntity randomPermissionsERCSiteTestEntity1 =
			randomPermissionsERCSiteTestEntity();

		ERCSiteTestEntity postPermissionsERCSiteTestEntity1 =
			testPostSiteERCSiteTestEntity_addERCSiteTestEntity(
				randomPermissionsERCSiteTestEntity1);

		Assert.assertNull(postPermissionsERCSiteTestEntity1.getPermissions());

		ERCSiteTestEntity randomPermissionsERCSiteTestEntity2 =
			randomPermissionsERCSiteTestEntity();

		ERCSiteTestEntity postPermissionsERCSiteTestEntity2 =
			testPostSiteERCSiteTestEntity_addPermissionsERCSiteTestEntity(
				randomPermissionsERCSiteTestEntity2);

		Assert.assertNotNull(
			postPermissionsERCSiteTestEntity2.getPermissions());
	}

	protected ERCSiteTestEntity
			testPostSiteERCSiteTestEntity_addERCSiteTestEntity(
				ERCSiteTestEntity ercSiteTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ERCSiteTestEntity
			testPostSiteERCSiteTestEntity_addPermissionsERCSiteTestEntity(
				ERCSiteTestEntity ercSiteTestEntity)
		throws Exception {

		return permissionsERCSiteTestEntityResource.postSiteERCSiteTestEntity(
			testGetSiteERCSiteTestEntitiesPage_getSiteExternalReferenceCode(),
			ercSiteTestEntity);
	}

	@Test
	public void testGraphQLPostSiteERCSiteTestEntity() throws Exception {
		ERCSiteTestEntity randomERCSiteTestEntity = randomERCSiteTestEntity();

		ERCSiteTestEntity ercSiteTestEntity =
			testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity(
				testGroup.getExternalReferenceCode(), randomERCSiteTestEntity);

		Assert.assertTrue(equals(randomERCSiteTestEntity, ercSiteTestEntity));
	}

	@Test
	public void testPutSiteERCSiteTestEntity() throws Exception {
		ERCSiteTestEntity postERCSiteTestEntity =
			testPutSiteERCSiteTestEntity_addERCSiteTestEntity();

		ERCSiteTestEntity randomERCSiteTestEntity = randomERCSiteTestEntity();

		ERCSiteTestEntity putERCSiteTestEntity =
			ercSiteTestEntityResource.putSiteERCSiteTestEntity(
				postERCSiteTestEntity.getSiteExternalReferenceCode(),
				postERCSiteTestEntity.getExternalReferenceCode(),
				randomERCSiteTestEntity);

		assertEquals(randomERCSiteTestEntity, putERCSiteTestEntity);
		assertValid(putERCSiteTestEntity);

		Assert.assertNull(putERCSiteTestEntity.getPermissions());

		ERCSiteTestEntity getERCSiteTestEntity =
			ercSiteTestEntityResource.getSiteERCSiteTestEntity(
				putERCSiteTestEntity.getSiteExternalReferenceCode(),
				putERCSiteTestEntity.getExternalReferenceCode());

		assertEquals(randomERCSiteTestEntity, getERCSiteTestEntity);
		assertValid(getERCSiteTestEntity);

		ERCSiteTestEntity randomPermissionsERCSiteTestEntity =
			randomPermissionsERCSiteTestEntity();

		putERCSiteTestEntity =
			ercSiteTestEntityResource.putSiteERCSiteTestEntity(
				postERCSiteTestEntity.getSiteExternalReferenceCode(),
				postERCSiteTestEntity.getExternalReferenceCode(),
				randomPermissionsERCSiteTestEntity);

		assertEquals(randomPermissionsERCSiteTestEntity, putERCSiteTestEntity);
		assertValid(putERCSiteTestEntity);

		Assert.assertNull(putERCSiteTestEntity.getPermissions());

		putERCSiteTestEntity =
			permissionsERCSiteTestEntityResource.putSiteERCSiteTestEntity(
				postERCSiteTestEntity.getSiteExternalReferenceCode(),
				postERCSiteTestEntity.getExternalReferenceCode(),
				randomPermissionsERCSiteTestEntity);

		Assert.assertNotNull(putERCSiteTestEntity.getPermissions());
	}

	protected ERCSiteTestEntity
			testPutSiteERCSiteTestEntity_addERCSiteTestEntity()
		throws Exception {

		return ercSiteTestEntityResource.postSiteERCSiteTestEntity(
			testGroup.getExternalReferenceCode(), randomERCSiteTestEntity());
	}

	@Test
	public void testPutSiteERCSiteTestEntityPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCSiteTestEntity ercSiteTestEntity =
			testPutSiteERCSiteTestEntityPermissionsPage_addERCSiteTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			ercSiteTestEntityResource.
				putSiteERCSiteTestEntityPermissionsPageHttpResponse(
					testGroup.getExternalReferenceCode(),
					ercSiteTestEntity.getExternalReferenceCode(),
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
			ercSiteTestEntityResource.
				putSiteERCSiteTestEntityPermissionsPageHttpResponse(
					testGroup.getExternalReferenceCode(),
					ercSiteTestEntity.getExternalReferenceCode(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected ERCSiteTestEntity
			testPutSiteERCSiteTestEntityPermissionsPage_addERCSiteTestEntity()
		throws Exception {

		return ercSiteTestEntityResource.postSiteERCSiteTestEntity(
			testGroup.getExternalReferenceCode(), randomERCSiteTestEntity());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ERCSiteTestEntity ercSiteTestEntity1 =
			testBatchEngineDeleteImportTask_addSiteERCSiteTestEntity();

		testBatchEngineDeleteImportTask_deleteERCSiteTestEntity(
			200, ercSiteTestEntity1.getExternalReferenceCode(),
			"siteExternalReferenceCode", testGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			ercSiteTestEntityResource.getSiteERCSiteTestEntityHttpResponse(
				ercSiteTestEntity1.getSiteExternalReferenceCode(),
				ercSiteTestEntity1.getExternalReferenceCode()));
	}

	protected ERCSiteTestEntity
			testBatchEngineDeleteImportTask_addSiteERCSiteTestEntity()
		throws Exception {

		return testDeleteSiteERCSiteTestEntity_addERCSiteTestEntity();
	}

	protected void testBatchEngineDeleteImportTask_deleteERCSiteTestEntity(
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
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCSiteTestEntity",
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

	protected ERCSiteTestEntity
			testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity()
		throws Exception {

		return testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity(
			testGroup.getExternalReferenceCode(), randomERCSiteTestEntity());
	}

	protected ERCSiteTestEntity
			testGraphQLSiteERCSiteTestEntity_addERCSiteTestEntity(
				String siteExternalReferenceCode,
				ERCSiteTestEntity ercSiteTestEntity)
		throws Exception {

		JSONDeserializer<ERCSiteTestEntity> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ERCSiteTestEntity.class)) {

			if (getGraphQLValue(field.get(ercSiteTestEntity)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(ercSiteTestEntity)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteERCSiteTestEntity",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" + siteExternalReferenceCode + "\"");
								put("ercSiteTestEntity", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteERCSiteTestEntity"),
			ERCSiteTestEntity.class);
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
		ERCSiteTestEntity ercSiteTestEntity,
		List<ERCSiteTestEntity> ercSiteTestEntities) {

		boolean contains = false;

		for (ERCSiteTestEntity item : ercSiteTestEntities) {
			if (equals(ercSiteTestEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			ercSiteTestEntities + " does not contain " + ercSiteTestEntity,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ERCSiteTestEntity ercSiteTestEntity1,
		ERCSiteTestEntity ercSiteTestEntity2) {

		Assert.assertTrue(
			ercSiteTestEntity1 + " does not equal " + ercSiteTestEntity2,
			equals(ercSiteTestEntity1, ercSiteTestEntity2));
	}

	protected void assertEquals(
		List<ERCSiteTestEntity> ercSiteTestEntities1,
		List<ERCSiteTestEntity> ercSiteTestEntities2) {

		Assert.assertEquals(
			ercSiteTestEntities1.size(), ercSiteTestEntities2.size());

		for (int i = 0; i < ercSiteTestEntities1.size(); i++) {
			ERCSiteTestEntity ercSiteTestEntity1 = ercSiteTestEntities1.get(i);
			ERCSiteTestEntity ercSiteTestEntity2 = ercSiteTestEntities2.get(i);

			assertEquals(ercSiteTestEntity1, ercSiteTestEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ERCSiteTestEntity> ercSiteTestEntities1,
		List<ERCSiteTestEntity> ercSiteTestEntities2) {

		Assert.assertEquals(
			ercSiteTestEntities1.size(), ercSiteTestEntities2.size());

		for (ERCSiteTestEntity ercSiteTestEntity1 : ercSiteTestEntities1) {
			boolean contains = false;

			for (ERCSiteTestEntity ercSiteTestEntity2 : ercSiteTestEntities2) {
				if (equals(ercSiteTestEntity1, ercSiteTestEntity2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				ercSiteTestEntities2 + " does not contain " +
					ercSiteTestEntity1,
				contains);
		}
	}

	protected void assertValid(ERCSiteTestEntity ercSiteTestEntity)
		throws Exception {

		boolean valid = true;

		if (ercSiteTestEntity.getDateCreated() == null) {
			valid = false;
		}

		if (ercSiteTestEntity.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (ercSiteTestEntity.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (ercSiteTestEntity.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (ercSiteTestEntity.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (ercSiteTestEntity.getSiteExternalReferenceCode() == null) {
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

	protected void assertValid(Page<ERCSiteTestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ERCSiteTestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ERCSiteTestEntity> ercSiteTestEntities =
			page.getItems();

		int size = ercSiteTestEntities.size();

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
						ERCSiteTestEntity.class)) {

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
		ERCSiteTestEntity ercSiteTestEntity1,
		ERCSiteTestEntity ercSiteTestEntity2) {

		if (ercSiteTestEntity1 == ercSiteTestEntity2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercSiteTestEntity1.getDateCreated(),
						ercSiteTestEntity2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercSiteTestEntity1.getDateModified(),
						ercSiteTestEntity2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercSiteTestEntity1.getDescription(),
						ercSiteTestEntity2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						ercSiteTestEntity1.getExternalReferenceCode(),
						ercSiteTestEntity2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercSiteTestEntity1.getPermissions(),
						ercSiteTestEntity2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						ercSiteTestEntity1.getSiteExternalReferenceCode(),
						ercSiteTestEntity2.getSiteExternalReferenceCode())) {

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

		if (!(_ercSiteTestEntityResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_ercSiteTestEntityResource;

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
		ERCSiteTestEntity ercSiteTestEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = ercSiteTestEntity.getDateCreated();

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

				sb.append(_format.format(ercSiteTestEntity.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = ercSiteTestEntity.getDateModified();

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

				sb.append(_format.format(ercSiteTestEntity.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = ercSiteTestEntity.getDescription();

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
			Object object = ercSiteTestEntity.getExternalReferenceCode();

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

		if (entityFieldName.equals("siteExternalReferenceCode")) {
			Object object = ercSiteTestEntity.getSiteExternalReferenceCode();

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

	protected ERCSiteTestEntity randomERCSiteTestEntity() throws Exception {
		return new ERCSiteTestEntity() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteExternalReferenceCode =
					testGroup.getExternalReferenceCode();
			}
		};
	}

	protected ERCSiteTestEntity randomIrrelevantERCSiteTestEntity()
		throws Exception {

		ERCSiteTestEntity randomIrrelevantERCSiteTestEntity =
			randomERCSiteTestEntity();

		randomIrrelevantERCSiteTestEntity.setSiteExternalReferenceCode(
			irrelevantGroup.getExternalReferenceCode());

		return randomIrrelevantERCSiteTestEntity;
	}

	protected ERCSiteTestEntity randomPatchERCSiteTestEntity()
		throws Exception {

		return randomERCSiteTestEntity();
	}

	protected ERCSiteTestEntity randomPermissionsERCSiteTestEntity()
		throws Exception {

		ERCSiteTestEntity ercSiteTestEntity = randomERCSiteTestEntity();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		ercSiteTestEntity.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return ercSiteTestEntity;
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

	protected ERCSiteTestEntityResource ercSiteTestEntityResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected ERCSiteTestEntityResource permissionsERCSiteTestEntityResource;
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
		LogFactoryUtil.getLog(BaseERCSiteTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		ERCSiteTestEntityResource _ercSiteTestEntityResource;

}