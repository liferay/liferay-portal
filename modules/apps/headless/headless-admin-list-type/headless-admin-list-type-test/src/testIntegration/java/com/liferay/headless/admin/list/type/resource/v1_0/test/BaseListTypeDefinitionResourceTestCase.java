/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.client.http.HttpInvoker;
import com.liferay.headless.admin.list.type.client.pagination.Page;
import com.liferay.headless.admin.list.type.client.pagination.Pagination;
import com.liferay.headless.admin.list.type.client.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.admin.list.type.client.serdes.v1_0.ListTypeDefinitionSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
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
import com.liferay.portal.kernel.util.GetterUtil;
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
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public abstract class BaseListTypeDefinitionResourceTestCase {

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

		_listTypeDefinitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		listTypeDefinitionResource = ListTypeDefinitionResource.builder(
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

		ListTypeDefinition listTypeDefinition1 = randomListTypeDefinition();

		String json = objectMapper.writeValueAsString(listTypeDefinition1);

		ListTypeDefinition listTypeDefinition2 = ListTypeDefinitionSerDes.toDTO(
			json);

		Assert.assertTrue(equals(listTypeDefinition1, listTypeDefinition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ListTypeDefinition listTypeDefinition = randomListTypeDefinition();

		String json1 = objectMapper.writeValueAsString(listTypeDefinition);
		String json2 = ListTypeDefinitionSerDes.toJSON(listTypeDefinition);

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

		ListTypeDefinition listTypeDefinition = randomListTypeDefinition();

		listTypeDefinition.setDefaultLanguageId(regex);
		listTypeDefinition.setExternalReferenceCode(regex);
		listTypeDefinition.setName(regex);

		String json = ListTypeDefinitionSerDes.toJSON(listTypeDefinition);

		Assert.assertFalse(json.contains(regex));

		listTypeDefinition = ListTypeDefinitionSerDes.toDTO(json);

		Assert.assertEquals(regex, listTypeDefinition.getDefaultLanguageId());
		Assert.assertEquals(
			regex, listTypeDefinition.getExternalReferenceCode());
		Assert.assertEquals(regex, listTypeDefinition.getName());
	}

	@Test
	public void testDeleteListTypeDefinition() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ListTypeDefinition listTypeDefinition =
			testDeleteListTypeDefinition_addListTypeDefinition();

		assertHttpResponseStatusCode(
			204,
			listTypeDefinitionResource.deleteListTypeDefinitionHttpResponse(
				listTypeDefinition.getId()));

		assertHttpResponseStatusCode(
			404,
			listTypeDefinitionResource.getListTypeDefinitionHttpResponse(
				listTypeDefinition.getId()));
		assertHttpResponseStatusCode(
			404,
			listTypeDefinitionResource.getListTypeDefinitionHttpResponse(0L));
	}

	protected ListTypeDefinition
			testDeleteListTypeDefinition_addListTypeDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteListTypeDefinition() throws Exception {

		// No namespace

		ListTypeDefinition listTypeDefinition1 =
			testGraphQLDeleteListTypeDefinition_addListTypeDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteListTypeDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"listTypeDefinitionId",
									listTypeDefinition1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteListTypeDefinition"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"listTypeDefinition",
					new HashMap<String, Object>() {
						{
							put(
								"listTypeDefinitionId",
								listTypeDefinition1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminListType_v1_0

		ListTypeDefinition listTypeDefinition2 =
			testGraphQLDeleteListTypeDefinition_addListTypeDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminListType_v1_0",
						new GraphQLField(
							"deleteListTypeDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"listTypeDefinitionId",
										listTypeDefinition2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminListType_v1_0",
				"Object/deleteListTypeDefinition"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminListType_v1_0",
					new GraphQLField(
						"listTypeDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"listTypeDefinitionId",
									listTypeDefinition2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ListTypeDefinition
			testGraphQLDeleteListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return testGraphQLListTypeDefinition_addListTypeDefinition();
	}

	@Test
	public void testDeleteListTypeDefinitionBatch() throws Exception {
		ListTypeDefinition listTypeDefinition1 =
			testDeleteListTypeDefinitionBatch_addListTypeDefinition();

		testDeleteListTypeDefinitionBatch_deleteListTypeDefinition(
			202, null, listTypeDefinition1.getId());

		assertHttpResponseStatusCode(
			404,
			listTypeDefinitionResource.getListTypeDefinitionHttpResponse(
				listTypeDefinition1.getId()));
	}

	protected ListTypeDefinition
			testDeleteListTypeDefinitionBatch_addListTypeDefinition()
		throws Exception {

		return testDeleteListTypeDefinition_addListTypeDefinition();
	}

	protected void testDeleteListTypeDefinitionBatch_deleteListTypeDefinition(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			listTypeDefinitionResource.
				deleteListTypeDefinitionBatchHttpResponse(
					null,
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
	public void testGetListTypeDefinition() throws Exception {
		ListTypeDefinition postListTypeDefinition =
			testGetListTypeDefinition_addListTypeDefinition();

		ListTypeDefinition getListTypeDefinition =
			listTypeDefinitionResource.getListTypeDefinition(
				postListTypeDefinition.getId());

		assertEquals(postListTypeDefinition, getListTypeDefinition);
		assertValid(getListTypeDefinition);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ListTypeDefinition postListTypeDefinition =
			testGetListTypeDefinition_addListTypeDefinition();

		ListTypeDefinition getListTypeDefinition =
			listTypeDefinitionResource.getListTypeDefinition(
				postListTypeDefinition.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postListTypeDefinition.getId());

		assertEquals(
			getListTypeDefinition,
			ListTypeDefinitionSerDes.toDTO(item.toString()));
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

	protected ListTypeDefinition
			testGetListTypeDefinition_addListTypeDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetListTypeDefinition() throws Exception {
		ListTypeDefinition listTypeDefinition =
			testGraphQLGetListTypeDefinition_addListTypeDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				listTypeDefinition,
				ListTypeDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"listTypeDefinition",
								new HashMap<String, Object>() {
									{
										put(
											"listTypeDefinitionId",
											listTypeDefinition.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/listTypeDefinition"))));

		// Using the namespace headlessAdminListType_v1_0

		Assert.assertTrue(
			equals(
				listTypeDefinition,
				ListTypeDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminListType_v1_0",
								new GraphQLField(
									"listTypeDefinition",
									new HashMap<String, Object>() {
										{
											put(
												"listTypeDefinitionId",
												listTypeDefinition.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminListType_v1_0",
						"Object/listTypeDefinition"))));
	}

	@Test
	public void testGraphQLGetListTypeDefinitionNotFound() throws Exception {
		Long irrelevantListTypeDefinitionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"listTypeDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"listTypeDefinitionId",
									irrelevantListTypeDefinitionId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminListType_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminListType_v1_0",
						new GraphQLField(
							"listTypeDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"listTypeDefinitionId",
										irrelevantListTypeDefinitionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ListTypeDefinition
			testGraphQLGetListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return testGraphQLListTypeDefinition_addListTypeDefinition();
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCode()
		throws Exception {

		ListTypeDefinition postListTypeDefinition =
			testGetListTypeDefinitionByExternalReferenceCode_addListTypeDefinition();

		ListTypeDefinition getListTypeDefinition =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					postListTypeDefinition.getExternalReferenceCode());

		assertEquals(postListTypeDefinition, getListTypeDefinition);
		assertValid(getListTypeDefinition);
	}

	protected ListTypeDefinition
			testGetListTypeDefinitionByExternalReferenceCode_addListTypeDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetListTypeDefinitionByExternalReferenceCode()
		throws Exception {

		ListTypeDefinition listTypeDefinition =
			testGraphQLGetListTypeDefinitionByExternalReferenceCode_addListTypeDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				listTypeDefinition,
				ListTypeDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"listTypeDefinitionByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												listTypeDefinition.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/listTypeDefinitionByExternalReferenceCode"))));

		// Using the namespace headlessAdminListType_v1_0

		Assert.assertTrue(
			equals(
				listTypeDefinition,
				ListTypeDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminListType_v1_0",
								new GraphQLField(
									"listTypeDefinitionByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													listTypeDefinition.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminListType_v1_0",
						"Object/listTypeDefinitionByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetListTypeDefinitionByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"listTypeDefinitionByExternalReferenceCode",
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

		// Using the namespace headlessAdminListType_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminListType_v1_0",
						new GraphQLField(
							"listTypeDefinitionByExternalReferenceCode",
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

	protected ListTypeDefinition
			testGraphQLGetListTypeDefinitionByExternalReferenceCode_addListTypeDefinition()
		throws Exception {

		return testGraphQLListTypeDefinition_addListTypeDefinition();
	}

	@Test
	public void testGetListTypeDefinitionsPage() throws Exception {
		Page<ListTypeDefinition> page =
			listTypeDefinitionResource.getListTypeDefinitionsPage(
				null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		ListTypeDefinition listTypeDefinition1 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				randomListTypeDefinition());

		ListTypeDefinition listTypeDefinition2 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				randomListTypeDefinition());

		page = listTypeDefinitionResource.getListTypeDefinitionsPage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			listTypeDefinition1, (List<ListTypeDefinition>)page.getItems());
		assertContains(
			listTypeDefinition2, (List<ListTypeDefinition>)page.getItems());
		assertValid(page, testGetListTypeDefinitionsPage_getExpectedActions());

		listTypeDefinitionResource.deleteListTypeDefinition(
			listTypeDefinition1.getId());

		listTypeDefinitionResource.deleteListTypeDefinition(
			listTypeDefinition2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetListTypeDefinitionsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetListTypeDefinitionsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		ListTypeDefinition listTypeDefinition1 = randomListTypeDefinition();

		listTypeDefinition1 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				listTypeDefinition1);

		for (EntityField entityField : entityFields) {
			Page<ListTypeDefinition> page =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null,
					getFilterString(
						entityField, "between", listTypeDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(listTypeDefinition1),
				(List<ListTypeDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionsPageWithFilterDoubleEquals()
		throws Exception {

		testGetListTypeDefinitionsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetListTypeDefinitionsPageWithFilterStringContains()
		throws Exception {

		testGetListTypeDefinitionsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetListTypeDefinitionsPageWithFilterStringEquals()
		throws Exception {

		testGetListTypeDefinitionsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetListTypeDefinitionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetListTypeDefinitionsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetListTypeDefinitionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ListTypeDefinition listTypeDefinition1 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				randomListTypeDefinition());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ListTypeDefinition listTypeDefinition2 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				randomListTypeDefinition());

		for (EntityField entityField : entityFields) {
			Page<ListTypeDefinition> page =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null,
					getFilterString(entityField, operator, listTypeDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(listTypeDefinition1),
				(List<ListTypeDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionsPageWithPagination()
		throws Exception {

		Page<ListTypeDefinition> listTypeDefinitionsPage =
			listTypeDefinitionResource.getListTypeDefinitionsPage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			listTypeDefinitionsPage.getTotalCount());

		ListTypeDefinition listTypeDefinition1 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				randomListTypeDefinition());

		ListTypeDefinition listTypeDefinition2 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				randomListTypeDefinition());

		ListTypeDefinition listTypeDefinition3 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				randomListTypeDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ListTypeDefinition> page1 =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				listTypeDefinition1,
				(List<ListTypeDefinition>)page1.getItems());

			Page<ListTypeDefinition> page2 =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				listTypeDefinition2,
				(List<ListTypeDefinition>)page2.getItems());

			Page<ListTypeDefinition> page3 =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				listTypeDefinition3,
				(List<ListTypeDefinition>)page3.getItems());
		}
		else {
			Page<ListTypeDefinition> page1 =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null, Pagination.of(1, totalCount + 2), null);

			List<ListTypeDefinition> listTypeDefinitions1 =
				(List<ListTypeDefinition>)page1.getItems();

			Assert.assertEquals(
				listTypeDefinitions1.toString(), totalCount + 2,
				listTypeDefinitions1.size());

			Page<ListTypeDefinition> page2 =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ListTypeDefinition> listTypeDefinitions2 =
				(List<ListTypeDefinition>)page2.getItems();

			Assert.assertEquals(
				listTypeDefinitions2.toString(), 1,
				listTypeDefinitions2.size());

			Page<ListTypeDefinition> page3 =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				listTypeDefinition1,
				(List<ListTypeDefinition>)page3.getItems());
			assertContains(
				listTypeDefinition2,
				(List<ListTypeDefinition>)page3.getItems());
			assertContains(
				listTypeDefinition3,
				(List<ListTypeDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionsPageWithSortDateTime()
		throws Exception {

		testGetListTypeDefinitionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, listTypeDefinition1, listTypeDefinition2) -> {
				BeanTestUtil.setProperty(
					listTypeDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetListTypeDefinitionsPageWithSortDouble()
		throws Exception {

		testGetListTypeDefinitionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, listTypeDefinition1, listTypeDefinition2) -> {
				BeanTestUtil.setProperty(
					listTypeDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					listTypeDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetListTypeDefinitionsPageWithSortInteger()
		throws Exception {

		testGetListTypeDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, listTypeDefinition1, listTypeDefinition2) -> {
				BeanTestUtil.setProperty(
					listTypeDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					listTypeDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetListTypeDefinitionsPageWithSortString()
		throws Exception {

		testGetListTypeDefinitionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, listTypeDefinition1, listTypeDefinition2) -> {
				Class<?> clazz = listTypeDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						listTypeDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						listTypeDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						listTypeDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						listTypeDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						listTypeDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						listTypeDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetListTypeDefinitionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ListTypeDefinition, ListTypeDefinition, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ListTypeDefinition listTypeDefinition1 = randomListTypeDefinition();
		ListTypeDefinition listTypeDefinition2 = randomListTypeDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, listTypeDefinition1, listTypeDefinition2);
		}

		listTypeDefinition1 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				listTypeDefinition1);

		listTypeDefinition2 =
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				listTypeDefinition2);

		Page<ListTypeDefinition> page =
			listTypeDefinitionResource.getListTypeDefinitionsPage(
				null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ListTypeDefinition> ascPage =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				listTypeDefinition1,
				(List<ListTypeDefinition>)ascPage.getItems());
			assertContains(
				listTypeDefinition2,
				(List<ListTypeDefinition>)ascPage.getItems());

			Page<ListTypeDefinition> descPage =
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				listTypeDefinition2,
				(List<ListTypeDefinition>)descPage.getItems());
			assertContains(
				listTypeDefinition1,
				(List<ListTypeDefinition>)descPage.getItems());
		}
	}

	protected ListTypeDefinition
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetListTypeDefinitionsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"listTypeDefinitions",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject listTypeDefinitionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/listTypeDefinitions");

		long totalCount = listTypeDefinitionsJSONObject.getLong("totalCount");

		ListTypeDefinition listTypeDefinition1 =
			testGraphQLListTypeDefinition_addListTypeDefinition(
				randomListTypeDefinition());

		ListTypeDefinition listTypeDefinition2 =
			testGraphQLListTypeDefinition_addListTypeDefinition(
				randomListTypeDefinition());

		listTypeDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/listTypeDefinitions");

		Assert.assertEquals(
			totalCount + 2,
			listTypeDefinitionsJSONObject.getLong("totalCount"));

		assertContains(
			listTypeDefinition1,
			Arrays.asList(
				ListTypeDefinitionSerDes.toDTOs(
					listTypeDefinitionsJSONObject.getString("items"))));
		assertContains(
			listTypeDefinition2,
			Arrays.asList(
				ListTypeDefinitionSerDes.toDTOs(
					listTypeDefinitionsJSONObject.getString("items"))));

		// Using the namespace headlessAdminListType_v1_0

		listTypeDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminListType_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminListType_v1_0",
			"JSONObject/listTypeDefinitions");

		Assert.assertEquals(
			totalCount + 2,
			listTypeDefinitionsJSONObject.getLong("totalCount"));

		assertContains(
			listTypeDefinition1,
			Arrays.asList(
				ListTypeDefinitionSerDes.toDTOs(
					listTypeDefinitionsJSONObject.getString("items"))));
		assertContains(
			listTypeDefinition2,
			Arrays.asList(
				ListTypeDefinitionSerDes.toDTOs(
					listTypeDefinitionsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchListTypeDefinition() throws Exception {
		ListTypeDefinition postListTypeDefinition =
			testPatchListTypeDefinition_addListTypeDefinition();

		ListTypeDefinition randomPatchListTypeDefinition =
			randomPatchListTypeDefinition();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ListTypeDefinition patchListTypeDefinition =
			listTypeDefinitionResource.patchListTypeDefinition(
				postListTypeDefinition.getId(), randomPatchListTypeDefinition);

		ListTypeDefinition expectedPatchListTypeDefinition =
			postListTypeDefinition.clone();

		BeanTestUtil.copyProperties(
			randomPatchListTypeDefinition, expectedPatchListTypeDefinition);

		ListTypeDefinition getListTypeDefinition =
			listTypeDefinitionResource.getListTypeDefinition(
				patchListTypeDefinition.getId());

		assertEquals(expectedPatchListTypeDefinition, getListTypeDefinition);
		assertValid(getListTypeDefinition);
	}

	protected ListTypeDefinition
			testPatchListTypeDefinition_addListTypeDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostListTypeDefinition() throws Exception {
		ListTypeDefinition randomListTypeDefinition =
			randomListTypeDefinition();

		ListTypeDefinition postListTypeDefinition =
			testPostListTypeDefinition_addListTypeDefinition(
				randomListTypeDefinition);

		assertEquals(randomListTypeDefinition, postListTypeDefinition);
		assertValid(postListTypeDefinition);
	}

	protected ListTypeDefinition
			testPostListTypeDefinition_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostListTypeDefinition() throws Exception {
		ListTypeDefinition randomListTypeDefinition =
			randomListTypeDefinition();

		ListTypeDefinition listTypeDefinition =
			testGraphQLListTypeDefinition_addListTypeDefinition(
				randomListTypeDefinition);

		Assert.assertTrue(equals(randomListTypeDefinition, listTypeDefinition));
	}

	@Test
	public void testPutListTypeDefinition() throws Exception {
		ListTypeDefinition postListTypeDefinition =
			testPutListTypeDefinition_addListTypeDefinition();

		ListTypeDefinition randomListTypeDefinition =
			randomListTypeDefinition();

		ListTypeDefinition putListTypeDefinition =
			listTypeDefinitionResource.putListTypeDefinition(
				postListTypeDefinition.getId(), randomListTypeDefinition);

		assertEquals(randomListTypeDefinition, putListTypeDefinition);
		assertValid(putListTypeDefinition);

		ListTypeDefinition getListTypeDefinition =
			listTypeDefinitionResource.getListTypeDefinition(
				putListTypeDefinition.getId());

		assertEquals(randomListTypeDefinition, getListTypeDefinition);
		assertValid(getListTypeDefinition);
	}

	protected ListTypeDefinition
			testPutListTypeDefinition_addListTypeDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutListTypeDefinitionByExternalReferenceCode()
		throws Exception {

		ListTypeDefinition postListTypeDefinition =
			testPutListTypeDefinitionByExternalReferenceCode_addListTypeDefinition();

		ListTypeDefinition randomListTypeDefinition =
			randomListTypeDefinition();

		ListTypeDefinition putListTypeDefinition =
			listTypeDefinitionResource.
				putListTypeDefinitionByExternalReferenceCode(
					postListTypeDefinition.getExternalReferenceCode(),
					randomListTypeDefinition);

		assertEquals(randomListTypeDefinition, putListTypeDefinition);
		assertValid(putListTypeDefinition);

		ListTypeDefinition getListTypeDefinition =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					putListTypeDefinition.getExternalReferenceCode());

		assertEquals(randomListTypeDefinition, getListTypeDefinition);
		assertValid(getListTypeDefinition);

		ListTypeDefinition newListTypeDefinition =
			testPutListTypeDefinitionByExternalReferenceCode_createListTypeDefinition();

		putListTypeDefinition =
			listTypeDefinitionResource.
				putListTypeDefinitionByExternalReferenceCode(
					newListTypeDefinition.getExternalReferenceCode(),
					newListTypeDefinition);

		assertEquals(newListTypeDefinition, putListTypeDefinition);
		assertValid(putListTypeDefinition);

		getListTypeDefinition =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					putListTypeDefinition.getExternalReferenceCode());

		assertEquals(newListTypeDefinition, getListTypeDefinition);

		Assert.assertEquals(
			newListTypeDefinition.getExternalReferenceCode(),
			putListTypeDefinition.getExternalReferenceCode());
	}

	protected ListTypeDefinition
			testPutListTypeDefinitionByExternalReferenceCode_addListTypeDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ListTypeDefinition
			testPutListTypeDefinitionByExternalReferenceCode_createListTypeDefinition()
		throws Exception {

		return randomListTypeDefinition();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ListTypeDefinition listTypeDefinition1 =
			testBatchEngineDeleteImportTask_addListTypeDefinition();

		testBatchEngineDeleteImportTask_deleteListTypeDefinition(
			200, null, listTypeDefinition1.getId());

		assertHttpResponseStatusCode(
			404,
			listTypeDefinitionResource.getListTypeDefinitionHttpResponse(
				listTypeDefinition1.getId()));
	}

	protected ListTypeDefinition
			testBatchEngineDeleteImportTask_addListTypeDefinition()
		throws Exception {

		return testDeleteListTypeDefinition_addListTypeDefinition();
	}

	protected void testBatchEngineDeleteImportTask_deleteListTypeDefinition(
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
				"com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition",
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

	protected ListTypeDefinition
			testGraphQLListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return testGraphQLListTypeDefinition_addListTypeDefinition(
			randomListTypeDefinition());
	}

	protected ListTypeDefinition
			testGraphQLListTypeDefinition_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		JSONDeserializer<ListTypeDefinition> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ListTypeDefinition.class)) {

			if (getGraphQLValue(field.get(listTypeDefinition)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(listTypeDefinition)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createListTypeDefinition",
						new HashMap<String, Object>() {
							{
								put("listTypeDefinition", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createListTypeDefinition"),
			ListTypeDefinition.class);
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
		ListTypeDefinition listTypeDefinition,
		List<ListTypeDefinition> listTypeDefinitions) {

		boolean contains = false;

		for (ListTypeDefinition item : listTypeDefinitions) {
			if (equals(listTypeDefinition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			listTypeDefinitions + " does not contain " + listTypeDefinition,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ListTypeDefinition listTypeDefinition1,
		ListTypeDefinition listTypeDefinition2) {

		Assert.assertTrue(
			listTypeDefinition1 + " does not equal " + listTypeDefinition2,
			equals(listTypeDefinition1, listTypeDefinition2));
	}

	protected void assertEquals(
		List<ListTypeDefinition> listTypeDefinitions1,
		List<ListTypeDefinition> listTypeDefinitions2) {

		Assert.assertEquals(
			listTypeDefinitions1.size(), listTypeDefinitions2.size());

		for (int i = 0; i < listTypeDefinitions1.size(); i++) {
			ListTypeDefinition listTypeDefinition1 = listTypeDefinitions1.get(
				i);
			ListTypeDefinition listTypeDefinition2 = listTypeDefinitions2.get(
				i);

			assertEquals(listTypeDefinition1, listTypeDefinition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ListTypeDefinition> listTypeDefinitions1,
		List<ListTypeDefinition> listTypeDefinitions2) {

		Assert.assertEquals(
			listTypeDefinitions1.size(), listTypeDefinitions2.size());

		for (ListTypeDefinition listTypeDefinition1 : listTypeDefinitions1) {
			boolean contains = false;

			for (ListTypeDefinition listTypeDefinition2 :
					listTypeDefinitions2) {

				if (equals(listTypeDefinition1, listTypeDefinition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				listTypeDefinitions2 + " does not contain " +
					listTypeDefinition1,
				contains);
		}
	}

	protected void assertValid(ListTypeDefinition listTypeDefinition)
		throws Exception {

		boolean valid = true;

		if (listTypeDefinition.getDateCreated() == null) {
			valid = false;
		}

		if (listTypeDefinition.getDateModified() == null) {
			valid = false;
		}

		if (listTypeDefinition.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (listTypeDefinition.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (listTypeDefinition.getDefaultLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (listTypeDefinition.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("listTypeEntries", additionalAssertFieldName)) {
				if (listTypeDefinition.getListTypeEntries() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (listTypeDefinition.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (listTypeDefinition.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (listTypeDefinition.getSystem() == null) {
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

	protected void assertValid(Page<ListTypeDefinition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ListTypeDefinition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ListTypeDefinition> listTypeDefinitions =
			page.getItems();

		int size = listTypeDefinitions.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.list.type.dto.v1_0.
						ListTypeDefinition.class)) {

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
		ListTypeDefinition listTypeDefinition1,
		ListTypeDefinition listTypeDefinition2) {

		if (listTypeDefinition1 == listTypeDefinition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)listTypeDefinition1.getActions(),
						(Map)listTypeDefinition2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeDefinition1.getDateCreated(),
						listTypeDefinition2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeDefinition1.getDateModified(),
						listTypeDefinition2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						listTypeDefinition1.getDefaultLanguageId(),
						listTypeDefinition2.getDefaultLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						listTypeDefinition1.getExternalReferenceCode(),
						listTypeDefinition2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeDefinition1.getId(),
						listTypeDefinition2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("listTypeEntries", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeDefinition1.getListTypeEntries(),
						listTypeDefinition2.getListTypeEntries())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeDefinition1.getName(),
						listTypeDefinition2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)listTypeDefinition1.getName_i18n(),
						(Map)listTypeDefinition2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeDefinition1.getSystem(),
						listTypeDefinition2.getSystem())) {

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

		if (!(_listTypeDefinitionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_listTypeDefinitionResource;

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
		ListTypeDefinition listTypeDefinition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = listTypeDefinition.getDateCreated();

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

				sb.append(_format.format(listTypeDefinition.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = listTypeDefinition.getDateModified();

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

				sb.append(_format.format(listTypeDefinition.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("defaultLanguageId")) {
			Object object = listTypeDefinition.getDefaultLanguageId();

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
			Object object = listTypeDefinition.getExternalReferenceCode();

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

		if (entityFieldName.equals("listTypeEntries")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = listTypeDefinition.getName();

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

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("system")) {
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

	protected ListTypeDefinition randomListTypeDefinition() throws Exception {
		return new ListTypeDefinition() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				defaultLanguageId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				system = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ListTypeDefinition randomIrrelevantListTypeDefinition()
		throws Exception {

		ListTypeDefinition randomIrrelevantListTypeDefinition =
			randomListTypeDefinition();

		return randomIrrelevantListTypeDefinition;
	}

	protected ListTypeDefinition randomPatchListTypeDefinition()
		throws Exception {

		return randomListTypeDefinition();
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

	protected ListTypeDefinitionResource listTypeDefinitionResource;
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
		LogFactoryUtil.getLog(BaseListTypeDefinitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.list.type.resource.v1_0.
		ListTypeDefinitionResource _listTypeDefinitionResource;

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