/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity1;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity2;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity3;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.TestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.TestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.TestEntitySerDes;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public abstract class BaseTestEntityResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

		_testEntityResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		testEntityResource = TestEntityResource.builder(
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

		TestEntity testEntity1 = randomTestEntity();

		String json = objectMapper.writeValueAsString(testEntity1);

		TestEntity testEntity2 = TestEntitySerDes.toDTO(json);

		Assert.assertTrue(equals(testEntity1, testEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		TestEntity testEntity = randomTestEntity();

		String json1 = objectMapper.writeValueAsString(testEntity);
		String json2 = TestEntitySerDes.toJSON(testEntity);

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

		TestEntity testEntity = randomTestEntity();

		testEntity.setDescription(regex);
		testEntity.setJsonProperty(regex);
		testEntity.setName(regex);
		testEntity.setSelf(regex);

		String json = TestEntitySerDes.toJSON(testEntity);

		Assert.assertFalse(json.contains(regex));

		testEntity = TestEntitySerDes.toDTO(json);

		Assert.assertEquals(regex, testEntity.getDescription());
		Assert.assertEquals(regex, testEntity.getJsonProperty());
		Assert.assertEquals(regex, testEntity.getName());
		Assert.assertEquals(regex, testEntity.getSelf());
	}

	@Test
	public void testDeleteTestEntity() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TestEntity testEntity = testDeleteTestEntity_addTestEntity();

		assertHttpResponseStatusCode(
			204,
			testEntityResource.deleteTestEntityHttpResponse(
				testEntity.getId(), null));

		assertHttpResponseStatusCode(
			404,
			testEntityResource.getTestEntityHttpResponse(testEntity.getId()));
		assertHttpResponseStatusCode(
			404, testEntityResource.getTestEntityHttpResponse(0L));
	}

	protected TestEntity testDeleteTestEntity_addTestEntity() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteTestEntity() throws Exception {

		// No namespace

		TestEntity testEntity1 = testGraphQLDeleteTestEntity_addTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteTestEntity",
						new HashMap<String, Object>() {
							{
								put("testEntityId", testEntity1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteTestEntity"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"testEntity",
					new HashMap<String, Object>() {
						{
							put("testEntityId", testEntity1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace test_v1_0

		TestEntity testEntity2 = testGraphQLDeleteTestEntity_addTestEntity();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"deleteTestEntity",
							new HashMap<String, Object>() {
								{
									put("testEntityId", testEntity2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/test_v1_0",
				"Object/deleteTestEntity"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"test_v1_0",
					new GraphQLField(
						"testEntity",
						new HashMap<String, Object>() {
							{
								put("testEntityId", testEntity2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected TestEntity testGraphQLDeleteTestEntity_addTestEntity()
		throws Exception {

		return testGraphQLTestEntity_addTestEntity();
	}

	@Test
	public void testDeleteTestEntityBatch() throws Exception {
		TestEntity testEntity1 = testDeleteTestEntityBatch_addTestEntity();

		testDeleteTestEntityBatch_deleteTestEntity(
			202, null, testEntity1.getId());

		assertHttpResponseStatusCode(
			404,
			testEntityResource.getTestEntityHttpResponse(testEntity1.getId()));
	}

	protected TestEntity testDeleteTestEntityBatch_addTestEntity()
		throws Exception {

		return testDeleteTestEntity_addTestEntity();
	}

	protected void testDeleteTestEntityBatch_deleteTestEntity(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			testEntityResource.deleteTestEntityBatchHttpResponse(
				null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetTestEntitiesPage() throws Exception {
		Page<TestEntity> page = testEntityResource.getTestEntitiesPage(null);

		long totalCount = page.getTotalCount();

		TestEntity testEntity1 = testGetTestEntitiesPage_addTestEntity(
			randomTestEntity());

		TestEntity testEntity2 = testGetTestEntitiesPage_addTestEntity(
			randomTestEntity());

		page = testEntityResource.getTestEntitiesPage(null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(testEntity1, (List<TestEntity>)page.getItems());
		assertContains(testEntity2, (List<TestEntity>)page.getItems());
		assertValid(page, testGetTestEntitiesPage_getExpectedActions());

		testEntityResource.deleteTestEntity(testEntity1.getId(), null);

		testEntityResource.deleteTestEntity(testEntity2.getId(), null);
	}

	protected Map<String, Map<String, String>>
			testGetTestEntitiesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetTestEntitiesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		TestEntity testEntity1 = randomTestEntity();

		testEntity1 = testGetTestEntitiesPage_addTestEntity(testEntity1);

		for (EntityField entityField : entityFields) {
			Page<TestEntity> page = testEntityResource.getTestEntitiesPage(
				getFilterString(entityField, "between", testEntity1));

			assertEquals(
				Collections.singletonList(testEntity1),
				(List<TestEntity>)page.getItems());
		}
	}

	@Test
	public void testGetTestEntitiesPageWithFilterDoubleEquals()
		throws Exception {

		testGetTestEntitiesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetTestEntitiesPageWithFilterStringContains()
		throws Exception {

		testGetTestEntitiesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetTestEntitiesPageWithFilterStringEquals()
		throws Exception {

		testGetTestEntitiesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetTestEntitiesPageWithFilterStringStartsWith()
		throws Exception {

		testGetTestEntitiesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetTestEntitiesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		TestEntity testEntity1 = testGetTestEntitiesPage_addTestEntity(
			randomTestEntity());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TestEntity testEntity2 = testGetTestEntitiesPage_addTestEntity(
			randomTestEntity());

		for (EntityField entityField : entityFields) {
			Page<TestEntity> page = testEntityResource.getTestEntitiesPage(
				getFilterString(entityField, operator, testEntity1));

			assertEquals(
				Collections.singletonList(testEntity1),
				(List<TestEntity>)page.getItems());
		}
	}

	protected TestEntity testGetTestEntitiesPage_addTestEntity(
			TestEntity testEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTestEntitiesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"testEntities",
			new HashMap<String, Object>() {
				{
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject testEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/testEntities");

		long totalCount = testEntitiesJSONObject.getLong("totalCount");

		TestEntity testEntity1 = testGraphQLTestEntity_addTestEntity(
			randomTestEntity());

		TestEntity testEntity2 = testGraphQLTestEntity_addTestEntity(
			randomTestEntity());

		testEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/testEntities");

		Assert.assertEquals(
			totalCount + 2, testEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			testEntity1,
			Arrays.asList(
				TestEntitySerDes.toDTOs(
					testEntitiesJSONObject.getString("items"))));
		assertContains(
			testEntity2,
			Arrays.asList(
				TestEntitySerDes.toDTOs(
					testEntitiesJSONObject.getString("items"))));

		// Using the namespace test_v1_0

		testEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(new GraphQLField("test_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/test_v1_0",
			"JSONObject/testEntities");

		Assert.assertEquals(
			totalCount + 2, testEntitiesJSONObject.getLong("totalCount"));

		assertContains(
			testEntity1,
			Arrays.asList(
				TestEntitySerDes.toDTOs(
					testEntitiesJSONObject.getString("items"))));
		assertContains(
			testEntity2,
			Arrays.asList(
				TestEntitySerDes.toDTOs(
					testEntitiesJSONObject.getString("items"))));
	}

	@Test
	public void testGetTestEntity() throws Exception {
		TestEntity postTestEntity = testGetTestEntity_addTestEntity();

		TestEntity getTestEntity = testEntityResource.getTestEntity(
			postTestEntity.getId());

		assertEquals(postTestEntity, getTestEntity);
		assertValid(getTestEntity);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		TestEntity postTestEntity = testGetTestEntity_addTestEntity();

		TestEntity getTestEntity = testEntityResource.getTestEntity(
			postTestEntity.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.TestEntity"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(postTestEntity.getId());

		assertEquals(getTestEntity, TestEntitySerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected TestEntity testGetTestEntity_addTestEntity() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTestEntity() throws Exception {
		TestEntity testEntity = testGraphQLGetTestEntity_addTestEntity();

		// No namespace

		Assert.assertTrue(
			equals(
				testEntity,
				TestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"testEntity",
								new HashMap<String, Object>() {
									{
										put("testEntityId", testEntity.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/testEntity"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				testEntity,
				TestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"testEntity",
									new HashMap<String, Object>() {
										{
											put(
												"testEntityId",
												testEntity.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/testEntity"))));
	}

	@Test
	public void testGraphQLGetTestEntityNotFound() throws Exception {
		Long irrelevantTestEntityId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"testEntity",
						new HashMap<String, Object>() {
							{
								put("testEntityId", irrelevantTestEntityId);
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
							"testEntity",
							new HashMap<String, Object>() {
								{
									put("testEntityId", irrelevantTestEntityId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected TestEntity testGraphQLGetTestEntity_addTestEntity()
		throws Exception {

		return testGraphQLTestEntity_addTestEntity();
	}

	@Test
	public void testGetTestEntityCount() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchTestEntity() throws Exception {
		TestEntity postTestEntity = testPatchTestEntity_addTestEntity();

		TestEntity randomPatchTestEntity = randomPatchTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TestEntity patchTestEntity = testEntityResource.patchTestEntity(
			postTestEntity.getId(), testPatchTestEntity_getOptionalParameter(),
			randomPatchTestEntity);

		TestEntity expectedPatchTestEntity = postTestEntity.clone();

		BeanTestUtil.copyProperties(
			randomPatchTestEntity, expectedPatchTestEntity);

		TestEntity getTestEntity = testEntityResource.getTestEntity(
			patchTestEntity.getId());

		assertEquals(expectedPatchTestEntity, getTestEntity);
		assertValid(getTestEntity);
	}

	protected TestEntity testPatchTestEntity_addTestEntity() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testPatchTestEntity_getOptionalParameter() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostReservedWord() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostTestEntity() throws Exception {
		TestEntity randomTestEntity = randomTestEntity();

		TestEntity postTestEntity = testPostTestEntity_addTestEntity(
			randomTestEntity);

		assertEquals(randomTestEntity, postTestEntity);
		assertValid(postTestEntity);

		ChildTestEntity1 childTestEntity1 = new ChildTestEntity1() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				documentId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				jsonProperty = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				self = StringUtil.toLowerCase(RandomTestUtil.randomString());
				property1 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());

				type = Type.create("ChildTestEntity1");
			}
		};

		assertEquals(
			childTestEntity1,
			testPostTestEntity_addTestEntity(childTestEntity1));

		ChildTestEntity2 childTestEntity2 = new ChildTestEntity2() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				documentId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				jsonProperty = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				self = StringUtil.toLowerCase(RandomTestUtil.randomString());
				property2 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());

				type = Type.create("ChildTestEntity2");
			}
		};

		assertEquals(
			childTestEntity2,
			testPostTestEntity_addTestEntity(childTestEntity2));

		ChildTestEntity3 childTestEntity3 = new ChildTestEntity3() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				documentId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				jsonProperty = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				self = StringUtil.toLowerCase(RandomTestUtil.randomString());

				type = Type.create("ChildTestEntity3");
			}
		};

		assertEquals(
			childTestEntity3,
			testPostTestEntity_addTestEntity(childTestEntity3));
	}

	protected TestEntity testPostTestEntity_addTestEntity(TestEntity testEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostTestEntity() throws Exception {
		TestEntity randomTestEntity = randomTestEntity();

		TestEntity testEntity = testGraphQLTestEntity_addTestEntity(
			randomTestEntity);

		Assert.assertTrue(equals(randomTestEntity, testEntity));
	}

	@Test
	public void testPostTestEntityMultipartBulk() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPutTestEntity() throws Exception {
		TestEntity postTestEntity = testPutTestEntity_addTestEntity();

		TestEntity randomTestEntity = randomTestEntity();

		TestEntity putTestEntity = testEntityResource.putTestEntity(
			postTestEntity.getId(), testPutTestEntity_getOptionalParameter(),
			randomTestEntity);

		assertEquals(randomTestEntity, putTestEntity);
		assertValid(putTestEntity);

		TestEntity getTestEntity = testEntityResource.getTestEntity(
			putTestEntity.getId());

		assertEquals(randomTestEntity, getTestEntity);
		assertValid(getTestEntity);
	}

	protected TestEntity testPutTestEntity_addTestEntity() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testPutTestEntity_getOptionalParameter() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutTestEntityStatus() throws Exception {
		TestEntity postTestEntity = testPutTestEntityStatus_addTestEntity();

		TestEntity randomTestEntity = randomTestEntity();

		TestEntity putTestEntity = testEntityResource.putTestEntityStatus(
			postTestEntity.getId(), randomTestEntity);

		assertEquals(randomTestEntity, putTestEntity);
		assertValid(putTestEntity);

		TestEntity getTestEntity = testPutTestEntityStatus_getTestEntity(
			putTestEntity.getId());

		assertEquals(randomTestEntity, getTestEntity);
		assertValid(getTestEntity);
	}

	protected TestEntity testPutTestEntityStatus_getTestEntity(
		Long testEntityId) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected TestEntity testPutTestEntityStatus_addTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		TestEntity testEntity1 =
			testBatchEngineDeleteImportTask_addTestEntity();

		testBatchEngineDeleteImportTask_deleteTestEntity(
			200, null, testEntity1.getId());

		assertHttpResponseStatusCode(
			404,
			testEntityResource.getTestEntityHttpResponse(testEntity1.getId()));
	}

	protected TestEntity testBatchEngineDeleteImportTask_addTestEntity()
		throws Exception {

		return testDeleteTestEntity_addTestEntity();
	}

	protected void testBatchEngineDeleteImportTask_deleteTestEntity(
			int expectedStatusCode, String externalReferenceCode, Long id,
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
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.TestEntity",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected TestEntity testGraphQLTestEntity_addTestEntity()
		throws Exception {

		return testGraphQLTestEntity_addTestEntity(randomTestEntity());
	}

	protected TestEntity testGraphQLTestEntity_addTestEntity(
			TestEntity testEntity)
		throws Exception {

		JSONDeserializer<TestEntity> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(TestEntity.class)) {

			if (getGraphQLValue(field.get(testEntity)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(testEntity)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createTestEntity",
						new HashMap<String, Object>() {
							{
								put("testEntity", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createTestEntity"),
			TestEntity.class);
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
		TestEntity testEntity, List<TestEntity> testEntities) {

		boolean contains = false;

		for (TestEntity item : testEntities) {
			if (equals(testEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			testEntities + " does not contain " + testEntity, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		TestEntity testEntity1, TestEntity testEntity2) {

		Assert.assertTrue(
			testEntity1 + " does not equal " + testEntity2,
			equals(testEntity1, testEntity2));
	}

	protected void assertEquals(
		List<TestEntity> testEntities1, List<TestEntity> testEntities2) {

		Assert.assertEquals(testEntities1.size(), testEntities2.size());

		for (int i = 0; i < testEntities1.size(); i++) {
			TestEntity testEntity1 = testEntities1.get(i);
			TestEntity testEntity2 = testEntities2.get(i);

			assertEquals(testEntity1, testEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TestEntity> testEntities1, List<TestEntity> testEntities2) {

		Assert.assertEquals(testEntities1.size(), testEntities2.size());

		for (TestEntity testEntity1 : testEntities1) {
			boolean contains = false;

			for (TestEntity testEntity2 : testEntities2) {
				if (equals(testEntity1, testEntity2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				testEntities2 + " does not contain " + testEntity1, contains);
		}
	}

	protected void assertValid(TestEntity testEntity) throws Exception {
		boolean valid = true;

		if (testEntity.getDateCreated() == null) {
			valid = false;
		}

		if (testEntity.getDateModified() == null) {
			valid = false;
		}

		if (testEntity.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (testEntity.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("documentId", additionalAssertFieldName)) {
				if (testEntity.getDocumentId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("jsonProperty", additionalAssertFieldName)) {
				if (testEntity.getJsonProperty() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (testEntity.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("nestedTestEntity", additionalAssertFieldName)) {
				if (testEntity.getNestedTestEntity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("self", additionalAssertFieldName)) {
				if (testEntity.getSelf() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"stringTestEntities", additionalAssertFieldName)) {

				if (testEntity.getStringTestEntities() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("stringTestEntity", additionalAssertFieldName)) {
				if (testEntity.getStringTestEntity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("testEntities", additionalAssertFieldName)) {
				if (testEntity.getTestEntities() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (testEntity.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("property1", additionalAssertFieldName)) {
				if (!(testEntity instanceof ChildTestEntity1)) {
					continue;
				}

				if (((ChildTestEntity1)testEntity).getProperty1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("property2", additionalAssertFieldName)) {
				if (!(testEntity instanceof ChildTestEntity2)) {
					continue;
				}

				if (((ChildTestEntity2)testEntity).getProperty2() == null) {
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

	protected void assertValid(Page<TestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<TestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<TestEntity> testEntities = page.getItems();

		int size = testEntities.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						TestEntity.class)) {

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

	protected boolean equals(TestEntity testEntity1, TestEntity testEntity2) {
		if (testEntity1 == testEntity2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getDateCreated(),
						testEntity2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getDateModified(),
						testEntity2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getDescription(),
						testEntity2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("documentId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getDocumentId(),
						testEntity2.getDocumentId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getId(), testEntity2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("jsonProperty", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getJsonProperty(),
						testEntity2.getJsonProperty())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getName(), testEntity2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("nestedTestEntity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getNestedTestEntity(),
						testEntity2.getNestedTestEntity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("self", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getSelf(), testEntity2.getSelf())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"stringTestEntities", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						testEntity1.getStringTestEntities(),
						testEntity2.getStringTestEntities())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("stringTestEntity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getStringTestEntity(),
						testEntity2.getStringTestEntity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("testEntities", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getTestEntities(),
						testEntity2.getTestEntities())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						testEntity1.getType(), testEntity2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("property1", additionalAssertFieldName)) {
				if (!(testEntity1 instanceof ChildTestEntity1) ||
					!(testEntity2 instanceof ChildTestEntity1)) {

					continue;
				}

				if (!Objects.deepEquals(
						((ChildTestEntity1)testEntity1).getProperty1(),
						((ChildTestEntity1)testEntity2).getProperty1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("property2", additionalAssertFieldName)) {
				if (!(testEntity1 instanceof ChildTestEntity2) ||
					!(testEntity2 instanceof ChildTestEntity2)) {

					continue;
				}

				if (!Objects.deepEquals(
						((ChildTestEntity2)testEntity1).getProperty2(),
						((ChildTestEntity2)testEntity2).getProperty2())) {

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

		if (!(_testEntityResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_testEntityResource;

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
		EntityField entityField, String operator, TestEntity testEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = testEntity.getDateCreated();

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

				sb.append(_format.format(testEntity.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = testEntity.getDateModified();

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

				sb.append(_format.format(testEntity.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = testEntity.getDescription();

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

		if (entityFieldName.equals("documentId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("jsonProperty")) {
			Object object = testEntity.getJsonProperty();

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
			Object object = testEntity.getName();

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

		if (entityFieldName.equals("nestedTestEntity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("self")) {
			Object object = testEntity.getSelf();

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

		if (entityFieldName.equals("stringTestEntities")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("stringTestEntity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("testEntities")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
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

	protected TestEntity randomTestEntity() throws Exception {
		List<Supplier<TestEntity>> suppliers = Arrays.asList(
			() -> {
				ChildTestEntity1 testEntity = new ChildTestEntity1();

				testEntity.setDateCreated(RandomTestUtil.nextDate());
				testEntity.setDateModified(RandomTestUtil.nextDate());
				testEntity.setDescription(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setDocumentId(RandomTestUtil.randomLong());
				testEntity.setId(RandomTestUtil.randomLong());
				testEntity.setJsonProperty(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setName(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setSelf(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				testEntity.setProperty1(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				testEntity.setType(TestEntity.Type.create("ChildTestEntity1"));

				return testEntity;
			},
			() -> {
				ChildTestEntity2 testEntity = new ChildTestEntity2();

				testEntity.setDateCreated(RandomTestUtil.nextDate());
				testEntity.setDateModified(RandomTestUtil.nextDate());
				testEntity.setDescription(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setDocumentId(RandomTestUtil.randomLong());
				testEntity.setId(RandomTestUtil.randomLong());
				testEntity.setJsonProperty(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setName(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setSelf(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				testEntity.setProperty2(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				testEntity.setType(TestEntity.Type.create("ChildTestEntity2"));

				return testEntity;
			},
			() -> {
				ChildTestEntity3 testEntity = new ChildTestEntity3();

				testEntity.setDateCreated(RandomTestUtil.nextDate());
				testEntity.setDateModified(RandomTestUtil.nextDate());
				testEntity.setDescription(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setDocumentId(RandomTestUtil.randomLong());
				testEntity.setId(RandomTestUtil.randomLong());
				testEntity.setJsonProperty(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setName(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				testEntity.setSelf(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				testEntity.setType(TestEntity.Type.create("ChildTestEntity3"));

				return testEntity;
			});

		Supplier<TestEntity> supplier = suppliers.get(
			RandomTestUtil.randomInt(0, suppliers.size() - 1));

		return supplier.get();
	}

	protected TestEntity randomIrrelevantTestEntity() throws Exception {
		TestEntity randomIrrelevantTestEntity = randomTestEntity();

		return randomIrrelevantTestEntity;
	}

	protected TestEntity randomPatchTestEntity() throws Exception {
		return randomTestEntity();
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

	protected TestEntityResource testEntityResource;
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
		LogFactoryUtil.getLog(BaseTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.portal.tools.rest.builder.test.resource.v1_0.
			TestEntityResource _testEntityResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}