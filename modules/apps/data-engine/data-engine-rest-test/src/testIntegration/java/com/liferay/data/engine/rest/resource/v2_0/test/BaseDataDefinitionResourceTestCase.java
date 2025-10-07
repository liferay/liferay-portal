/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.client.http.HttpInvoker;
import com.liferay.data.engine.rest.client.pagination.Page;
import com.liferay.data.engine.rest.client.pagination.Pagination;
import com.liferay.data.engine.rest.client.permission.Permission;
import com.liferay.data.engine.rest.client.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.client.serdes.v2_0.DataDefinitionSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
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
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public abstract class BaseDataDefinitionResourceTestCase {

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

		_dataDefinitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		dataDefinitionResource = DataDefinitionResource.builder(
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

		DataDefinition dataDefinition1 = randomDataDefinition();

		String json = objectMapper.writeValueAsString(dataDefinition1);

		DataDefinition dataDefinition2 = DataDefinitionSerDes.toDTO(json);

		Assert.assertTrue(equals(dataDefinition1, dataDefinition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DataDefinition dataDefinition = randomDataDefinition();

		String json1 = objectMapper.writeValueAsString(dataDefinition);
		String json2 = DataDefinitionSerDes.toJSON(dataDefinition);

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

		DataDefinition dataDefinition = randomDataDefinition();

		dataDefinition.setContentType(regex);
		dataDefinition.setDataDefinitionKey(regex);
		dataDefinition.setDefaultLanguageId(regex);
		dataDefinition.setExternalReferenceCode(regex);
		dataDefinition.setStorageType(regex);

		String json = DataDefinitionSerDes.toJSON(dataDefinition);

		Assert.assertFalse(json.contains(regex));

		dataDefinition = DataDefinitionSerDes.toDTO(json);

		Assert.assertEquals(regex, dataDefinition.getContentType());
		Assert.assertEquals(regex, dataDefinition.getDataDefinitionKey());
		Assert.assertEquals(regex, dataDefinition.getDefaultLanguageId());
		Assert.assertEquals(regex, dataDefinition.getExternalReferenceCode());
		Assert.assertEquals(regex, dataDefinition.getStorageType());
	}

	@Test
	public void testDeleteDataDefinition() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataDefinition dataDefinition =
			testDeleteDataDefinition_addDataDefinition();

		assertHttpResponseStatusCode(
			204,
			dataDefinitionResource.deleteDataDefinitionHttpResponse(
				dataDefinition.getId()));

		assertHttpResponseStatusCode(
			404,
			dataDefinitionResource.getDataDefinitionHttpResponse(
				dataDefinition.getId()));
		assertHttpResponseStatusCode(
			404, dataDefinitionResource.getDataDefinitionHttpResponse(0L));
	}

	protected DataDefinition testDeleteDataDefinition_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDataDefinition() throws Exception {

		// No namespace

		DataDefinition dataDefinition1 =
			testGraphQLDeleteDataDefinition_addDataDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDataDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"dataDefinitionId",
									dataDefinition1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDataDefinition"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataDefinition",
					new HashMap<String, Object>() {
						{
							put("dataDefinitionId", dataDefinition1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace dataEngine_v2_0

		DataDefinition dataDefinition2 =
			testGraphQLDeleteDataDefinition_addDataDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"deleteDataDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"dataDefinitionId",
										dataDefinition2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"Object/deleteDataDefinition"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataEngine_v2_0",
					new GraphQLField(
						"dataDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"dataDefinitionId",
									dataDefinition2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DataDefinition testGraphQLDeleteDataDefinition_addDataDefinition()
		throws Exception {

		return testGraphQLDataDefinition_addDataDefinition();
	}

	@Test
	public void testDeleteDataDefinitionBatch() throws Exception {
		DataDefinition dataDefinition1 =
			testDeleteDataDefinitionBatch_addDataDefinition();

		testDeleteDataDefinitionBatch_deleteDataDefinition(
			202, null, dataDefinition1.getId());

		assertHttpResponseStatusCode(
			404,
			dataDefinitionResource.getDataDefinitionHttpResponse(
				dataDefinition1.getId()));
	}

	protected DataDefinition testDeleteDataDefinitionBatch_addDataDefinition()
		throws Exception {

		return testDeleteDataDefinition_addDataDefinition();
	}

	protected void testDeleteDataDefinitionBatch_deleteDataDefinition(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			dataDefinitionResource.deleteDataDefinitionBatchHttpResponse(
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
	public void testDeleteSiteDataDefinitionByContentTypeByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataDefinition dataDefinition =
			testDeleteSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition();

		assertHttpResponseStatusCode(
			204,
			dataDefinitionResource.
				deleteSiteDataDefinitionByContentTypeByExternalReferenceCodeHttpResponse(
					dataDefinition.getSiteId(), dataDefinition.getContentType(),
					dataDefinition.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByExternalReferenceCodeHttpResponse(
					dataDefinition.getSiteId(), dataDefinition.getContentType(),
					dataDefinition.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByExternalReferenceCodeHttpResponse(
					dataDefinition.getSiteId(), dataDefinition.getContentType(),
					"-"));
	}

	protected DataDefinition
			testDeleteSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteDataDefinitionByContentTypeByExternalReferenceCode()
		throws Exception {

		// No namespace

		DataDefinition dataDefinition1 =
			testGraphQLDeleteSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteDataDefinitionByContentTypeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + dataDefinition1.getSiteId() + "\"");
								put(
									"contentType",
									"\"" + dataDefinition1.getContentType() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										dataDefinition1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteDataDefinitionByContentTypeByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataDefinitionByContentTypeByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + dataDefinition1.getSiteId() + "\"");
							put(
								"contentType",
								"\"" + dataDefinition1.getContentType() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									dataDefinition1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace dataEngine_v2_0

		DataDefinition dataDefinition2 =
			testGraphQLDeleteSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"deleteSiteDataDefinitionByContentTypeByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + dataDefinition2.getSiteId() +
											"\"");
									put(
										"contentType",
										"\"" +
											dataDefinition2.getContentType() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											dataDefinition2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"Object/deleteSiteDataDefinitionByContentTypeByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataEngine_v2_0",
					new GraphQLField(
						"dataDefinitionByContentTypeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + dataDefinition2.getSiteId() + "\"");
								put(
									"contentType",
									"\"" + dataDefinition2.getContentType() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										dataDefinition2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DataDefinition
			testGraphQLDeleteSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition()
		throws Exception {

		return testGraphQLSiteDataDefinition_addDataDefinition();
	}

	@Test
	public void testGetDataDefinition() throws Exception {
		DataDefinition postDataDefinition =
			testGetDataDefinition_addDataDefinition();

		DataDefinition getDataDefinition =
			dataDefinitionResource.getDataDefinition(
				postDataDefinition.getId());

		assertEquals(postDataDefinition, getDataDefinition);
		assertValid(getDataDefinition);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DataDefinition postDataDefinition =
			testGetDataDefinition_addDataDefinition();

		DataDefinition getDataDefinition =
			dataDefinitionResource.getDataDefinition(
				postDataDefinition.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.data.engine.rest.dto.v2_0.DataDefinition"
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
			postDataDefinition.getId());

		assertEquals(
			getDataDefinition, DataDefinitionSerDes.toDTO(item.toString()));
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

	protected DataDefinition testGetDataDefinition_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDataDefinition() throws Exception {
		DataDefinition dataDefinition =
			testGraphQLGetDataDefinition_addDataDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				dataDefinition,
				DataDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataDefinition",
								new HashMap<String, Object>() {
									{
										put(
											"dataDefinitionId",
											dataDefinition.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/dataDefinition"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataDefinition,
				DataDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataDefinition",
									new HashMap<String, Object>() {
										{
											put(
												"dataDefinitionId",
												dataDefinition.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataDefinition"))));
	}

	@Test
	public void testGraphQLGetDataDefinitionNotFound() throws Exception {
		Long irrelevantDataDefinitionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"dataDefinitionId",
									irrelevantDataDefinitionId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace dataEngine_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"dataDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"dataDefinitionId",
										irrelevantDataDefinitionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataDefinition testGraphQLGetDataDefinition_addDataDefinition()
		throws Exception {

		return testGraphQLDataDefinition_addDataDefinition();
	}

	@Test
	public void testGetDataDefinitionByContentTypeContentTypePage()
		throws Exception {

		String contentType =
			testGetDataDefinitionByContentTypeContentTypePage_getContentType();
		String irrelevantContentType =
			testGetDataDefinitionByContentTypeContentTypePage_getIrrelevantContentType();

		Page<DataDefinition> page =
			dataDefinitionResource.
				getDataDefinitionByContentTypeContentTypePage(
					contentType, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantContentType != null) {
			DataDefinition irrelevantDataDefinition =
				testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
					irrelevantContentType, randomIrrelevantDataDefinition());

			page =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						irrelevantContentType, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDataDefinition,
				(List<DataDefinition>)page.getItems());
			assertValid(
				page,
				testGetDataDefinitionByContentTypeContentTypePage_getExpectedActions(
					irrelevantContentType));
		}

		DataDefinition dataDefinition1 =
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				contentType, randomDataDefinition());

		DataDefinition dataDefinition2 =
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				contentType, randomDataDefinition());

		page =
			dataDefinitionResource.
				getDataDefinitionByContentTypeContentTypePage(
					contentType, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(dataDefinition1, (List<DataDefinition>)page.getItems());
		assertContains(dataDefinition2, (List<DataDefinition>)page.getItems());
		assertValid(
			page,
			testGetDataDefinitionByContentTypeContentTypePage_getExpectedActions(
				contentType));

		dataDefinitionResource.deleteDataDefinition(dataDefinition1.getId());

		dataDefinitionResource.deleteDataDefinition(dataDefinition2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDataDefinitionByContentTypeContentTypePage_getExpectedActions(
				String contentType)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDataDefinitionByContentTypeContentTypePageWithPagination()
		throws Exception {

		String contentType =
			testGetDataDefinitionByContentTypeContentTypePage_getContentType();

		Page<DataDefinition> dataDefinitionsPage =
			dataDefinitionResource.
				getDataDefinitionByContentTypeContentTypePage(
					contentType, null, null, null);

		int totalCount = GetterUtil.getInteger(
			dataDefinitionsPage.getTotalCount());

		DataDefinition dataDefinition1 =
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				contentType, randomDataDefinition());

		DataDefinition dataDefinition2 =
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				contentType, randomDataDefinition());

		DataDefinition dataDefinition3 =
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				contentType, randomDataDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DataDefinition> page1 =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				dataDefinition1, (List<DataDefinition>)page1.getItems());

			Page<DataDefinition> page2 =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				dataDefinition2, (List<DataDefinition>)page2.getItems());

			Page<DataDefinition> page3 =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				dataDefinition3, (List<DataDefinition>)page3.getItems());
		}
		else {
			Page<DataDefinition> page1 =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null, Pagination.of(1, totalCount + 2),
						null);

			List<DataDefinition> dataDefinitions1 =
				(List<DataDefinition>)page1.getItems();

			Assert.assertEquals(
				dataDefinitions1.toString(), totalCount + 2,
				dataDefinitions1.size());

			Page<DataDefinition> page2 =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null, Pagination.of(2, totalCount + 2),
						null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DataDefinition> dataDefinitions2 =
				(List<DataDefinition>)page2.getItems();

			Assert.assertEquals(
				dataDefinitions2.toString(), 1, dataDefinitions2.size());

			Page<DataDefinition> page3 =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				dataDefinition1, (List<DataDefinition>)page3.getItems());
			assertContains(
				dataDefinition2, (List<DataDefinition>)page3.getItems());
			assertContains(
				dataDefinition3, (List<DataDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetDataDefinitionByContentTypeContentTypePageWithSortDateTime()
		throws Exception {

		testGetDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, dataDefinition1, dataDefinition2) -> {
				BeanTestUtil.setProperty(
					dataDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDataDefinitionByContentTypeContentTypePageWithSortDouble()
		throws Exception {

		testGetDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, dataDefinition1, dataDefinition2) -> {
				BeanTestUtil.setProperty(
					dataDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					dataDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDataDefinitionByContentTypeContentTypePageWithSortInteger()
		throws Exception {

		testGetDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.INTEGER,
			(entityField, dataDefinition1, dataDefinition2) -> {
				BeanTestUtil.setProperty(
					dataDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					dataDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDataDefinitionByContentTypeContentTypePageWithSortString()
		throws Exception {

		testGetDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.STRING,
			(entityField, dataDefinition1, dataDefinition2) -> {
				Class<?> clazz = dataDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						dataDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						dataDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						dataDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						dataDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						dataDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						dataDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DataDefinition, DataDefinition, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String contentType =
			testGetDataDefinitionByContentTypeContentTypePage_getContentType();

		DataDefinition dataDefinition1 = randomDataDefinition();
		DataDefinition dataDefinition2 = randomDataDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, dataDefinition1, dataDefinition2);
		}

		dataDefinition1 =
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				contentType, dataDefinition1);

		dataDefinition2 =
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				contentType, dataDefinition2);

		Page<DataDefinition> page =
			dataDefinitionResource.
				getDataDefinitionByContentTypeContentTypePage(
					contentType, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DataDefinition> ascPage =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				dataDefinition1, (List<DataDefinition>)ascPage.getItems());
			assertContains(
				dataDefinition2, (List<DataDefinition>)ascPage.getItems());

			Page<DataDefinition> descPage =
				dataDefinitionResource.
					getDataDefinitionByContentTypeContentTypePage(
						contentType, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				dataDefinition2, (List<DataDefinition>)descPage.getItems());
			assertContains(
				dataDefinition1, (List<DataDefinition>)descPage.getItems());
		}
	}

	protected DataDefinition
			testGetDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				String contentType, DataDefinition dataDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDataDefinitionByContentTypeContentTypePage_getContentType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDataDefinitionByContentTypeContentTypePage_getIrrelevantContentType()
		throws Exception {

		return null;
	}

	@Test
	public void testGetDataDefinitionDataDefinitionFieldFieldTypes()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetDataDefinitionPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataDefinition postDataDefinition =
			testGetDataDefinitionPermissionsPage_addDataDefinition();

		Page<Permission> page =
			dataDefinitionResource.getDataDefinitionPermissionsPage(
				postDataDefinition.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected DataDefinition
			testGetDataDefinitionPermissionsPage_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeByDataDefinitionKey()
		throws Exception {

		DataDefinition postDataDefinition =
			testGetSiteDataDefinitionByContentTypeByDataDefinitionKey_addDataDefinition();

		DataDefinition getDataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByDataDefinitionKey(
					postDataDefinition.getSiteId(),
					postDataDefinition.getContentType(),
					postDataDefinition.getDataDefinitionKey());

		assertEquals(postDataDefinition, getDataDefinition);
		assertValid(getDataDefinition);
	}

	protected DataDefinition
			testGetSiteDataDefinitionByContentTypeByDataDefinitionKey_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteDataDefinitionByContentTypeByDataDefinitionKey()
		throws Exception {

		DataDefinition dataDefinition =
			testGraphQLGetSiteDataDefinitionByContentTypeByDataDefinitionKey_addDataDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				dataDefinition,
				DataDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataDefinitionByContentTypeByDataDefinitionKey",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + dataDefinition.getSiteId() +
												"\"");
										put(
											"contentType",
											"\"" +
												dataDefinition.
													getContentType() + "\"");
										put(
											"dataDefinitionKey",
											"\"" +
												dataDefinition.
													getDataDefinitionKey() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/dataDefinitionByContentTypeByDataDefinitionKey"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataDefinition,
				DataDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataDefinitionByContentTypeByDataDefinitionKey",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													dataDefinition.getSiteId() +
														"\"");
											put(
												"contentType",
												"\"" +
													dataDefinition.
														getContentType() +
															"\"");
											put(
												"dataDefinitionKey",
												"\"" +
													dataDefinition.
														getDataDefinitionKey() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataDefinitionByContentTypeByDataDefinitionKey"))));
	}

	@Test
	public void testGraphQLGetSiteDataDefinitionByContentTypeByDataDefinitionKeyNotFound()
		throws Exception {

		String irrelevantContentType =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantDataDefinitionKey =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataDefinitionByContentTypeByDataDefinitionKey",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put("contentType", irrelevantContentType);
								put(
									"dataDefinitionKey",
									irrelevantDataDefinitionKey);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace dataEngine_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"dataDefinitionByContentTypeByDataDefinitionKey",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put("contentType", irrelevantContentType);
									put(
										"dataDefinitionKey",
										irrelevantDataDefinitionKey);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataDefinition
			testGraphQLGetSiteDataDefinitionByContentTypeByDataDefinitionKey_addDataDefinition()
		throws Exception {

		return testGraphQLSiteDataDefinition_addDataDefinition();
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeByExternalReferenceCode()
		throws Exception {

		DataDefinition postDataDefinition =
			testGetSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition();

		DataDefinition getDataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByExternalReferenceCode(
					postDataDefinition.getSiteId(),
					postDataDefinition.getContentType(),
					postDataDefinition.getExternalReferenceCode());

		assertEquals(postDataDefinition, getDataDefinition);
		assertValid(getDataDefinition);
	}

	protected DataDefinition
			testGetSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteDataDefinitionByContentTypeByExternalReferenceCode()
		throws Exception {

		DataDefinition dataDefinition =
			testGraphQLGetSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				dataDefinition,
				DataDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataDefinitionByContentTypeByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + dataDefinition.getSiteId() +
												"\"");
										put(
											"contentType",
											"\"" +
												dataDefinition.
													getContentType() + "\"");
										put(
											"externalReferenceCode",
											"\"" +
												dataDefinition.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/dataDefinitionByContentTypeByExternalReferenceCode"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataDefinition,
				DataDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataDefinitionByContentTypeByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													dataDefinition.getSiteId() +
														"\"");
											put(
												"contentType",
												"\"" +
													dataDefinition.
														getContentType() +
															"\"");
											put(
												"externalReferenceCode",
												"\"" +
													dataDefinition.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataDefinitionByContentTypeByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteDataDefinitionByContentTypeByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantContentType =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataDefinitionByContentTypeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put("contentType", irrelevantContentType);
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace dataEngine_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"dataDefinitionByContentTypeByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put("contentType", irrelevantContentType);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataDefinition
			testGraphQLGetSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition()
		throws Exception {

		return testGraphQLSiteDataDefinition_addDataDefinition();
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeContentTypePage()
		throws Exception {

		Long siteId =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getIrrelevantSiteId();
		String contentType =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getContentType();
		String irrelevantContentType =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getIrrelevantContentType();

		Page<DataDefinition> page =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeContentTypePage(
					siteId, contentType, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteId != null) && (irrelevantContentType != null)) {
			DataDefinition irrelevantDataDefinition =
				testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
					irrelevantSiteId, irrelevantContentType,
					randomIrrelevantDataDefinition());

			page =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						irrelevantSiteId, irrelevantContentType, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDataDefinition,
				(List<DataDefinition>)page.getItems());
			assertValid(
				page,
				testGetSiteDataDefinitionByContentTypeContentTypePage_getExpectedActions(
					irrelevantSiteId, irrelevantContentType));
		}

		DataDefinition dataDefinition1 =
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				siteId, contentType, randomDataDefinition());

		DataDefinition dataDefinition2 =
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				siteId, contentType, randomDataDefinition());

		page =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeContentTypePage(
					siteId, contentType, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(dataDefinition1, (List<DataDefinition>)page.getItems());
		assertContains(dataDefinition2, (List<DataDefinition>)page.getItems());
		assertValid(
			page,
			testGetSiteDataDefinitionByContentTypeContentTypePage_getExpectedActions(
				siteId, contentType));

		dataDefinitionResource.deleteDataDefinition(dataDefinition1.getId());

		dataDefinitionResource.deleteDataDefinition(dataDefinition2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDataDefinitionByContentTypeContentTypePage_getExpectedActions(
				Long siteId, String contentType)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeContentTypePageWithPagination()
		throws Exception {

		Long siteId =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getSiteId();
		String contentType =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getContentType();

		Page<DataDefinition> dataDefinitionsPage =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeContentTypePage(
					siteId, contentType, null, null, null);

		int totalCount = GetterUtil.getInteger(
			dataDefinitionsPage.getTotalCount());

		DataDefinition dataDefinition1 =
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				siteId, contentType, randomDataDefinition());

		DataDefinition dataDefinition2 =
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				siteId, contentType, randomDataDefinition());

		DataDefinition dataDefinition3 =
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				siteId, contentType, randomDataDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DataDefinition> page1 =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				dataDefinition1, (List<DataDefinition>)page1.getItems());

			Page<DataDefinition> page2 =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				dataDefinition2, (List<DataDefinition>)page2.getItems());

			Page<DataDefinition> page3 =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				dataDefinition3, (List<DataDefinition>)page3.getItems());
		}
		else {
			Page<DataDefinition> page1 =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(1, totalCount + 2), null);

			List<DataDefinition> dataDefinitions1 =
				(List<DataDefinition>)page1.getItems();

			Assert.assertEquals(
				dataDefinitions1.toString(), totalCount + 2,
				dataDefinitions1.size());

			Page<DataDefinition> page2 =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DataDefinition> dataDefinitions2 =
				(List<DataDefinition>)page2.getItems();

			Assert.assertEquals(
				dataDefinitions2.toString(), 1, dataDefinitions2.size());

			Page<DataDefinition> page3 =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				dataDefinition1, (List<DataDefinition>)page3.getItems());
			assertContains(
				dataDefinition2, (List<DataDefinition>)page3.getItems());
			assertContains(
				dataDefinition3, (List<DataDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeContentTypePageWithSortDateTime()
		throws Exception {

		testGetSiteDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, dataDefinition1, dataDefinition2) -> {
				BeanTestUtil.setProperty(
					dataDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeContentTypePageWithSortDouble()
		throws Exception {

		testGetSiteDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, dataDefinition1, dataDefinition2) -> {
				BeanTestUtil.setProperty(
					dataDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					dataDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeContentTypePageWithSortInteger()
		throws Exception {

		testGetSiteDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.INTEGER,
			(entityField, dataDefinition1, dataDefinition2) -> {
				BeanTestUtil.setProperty(
					dataDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					dataDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteDataDefinitionByContentTypeContentTypePageWithSortString()
		throws Exception {

		testGetSiteDataDefinitionByContentTypeContentTypePageWithSort(
			EntityField.Type.STRING,
			(entityField, dataDefinition1, dataDefinition2) -> {
				Class<?> clazz = dataDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						dataDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						dataDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						dataDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						dataDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						dataDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						dataDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetSiteDataDefinitionByContentTypeContentTypePageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, DataDefinition, DataDefinition, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getSiteId();
		String contentType =
			testGetSiteDataDefinitionByContentTypeContentTypePage_getContentType();

		DataDefinition dataDefinition1 = randomDataDefinition();
		DataDefinition dataDefinition2 = randomDataDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, dataDefinition1, dataDefinition2);
		}

		dataDefinition1 =
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				siteId, contentType, dataDefinition1);

		dataDefinition2 =
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				siteId, contentType, dataDefinition2);

		Page<DataDefinition> page =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeContentTypePage(
					siteId, contentType, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DataDefinition> ascPage =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				dataDefinition1, (List<DataDefinition>)ascPage.getItems());
			assertContains(
				dataDefinition2, (List<DataDefinition>)ascPage.getItems());

			Page<DataDefinition> descPage =
				dataDefinitionResource.
					getSiteDataDefinitionByContentTypeContentTypePage(
						siteId, contentType, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				dataDefinition2, (List<DataDefinition>)descPage.getItems());
			assertContains(
				dataDefinition1, (List<DataDefinition>)descPage.getItems());
		}
	}

	protected DataDefinition
			testGetSiteDataDefinitionByContentTypeContentTypePage_addDataDefinition(
				Long siteId, String contentType, DataDefinition dataDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteDataDefinitionByContentTypeContentTypePage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long
			testGetSiteDataDefinitionByContentTypeContentTypePage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	protected String
			testGetSiteDataDefinitionByContentTypeContentTypePage_getContentType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteDataDefinitionByContentTypeContentTypePage_getIrrelevantContentType()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchDataDefinition() throws Exception {
		DataDefinition postDataDefinition =
			testPatchDataDefinition_addDataDefinition();

		DataDefinition randomPatchDataDefinition = randomPatchDataDefinition();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataDefinition patchDataDefinition =
			dataDefinitionResource.patchDataDefinition(
				postDataDefinition.getId(), randomPatchDataDefinition);

		DataDefinition expectedPatchDataDefinition = postDataDefinition.clone();

		BeanTestUtil.copyProperties(
			randomPatchDataDefinition, expectedPatchDataDefinition);

		DataDefinition getDataDefinition =
			dataDefinitionResource.getDataDefinition(
				patchDataDefinition.getId());

		assertEquals(expectedPatchDataDefinition, getDataDefinition);
		assertValid(getDataDefinition);
	}

	protected DataDefinition testPatchDataDefinition_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDataDefinitionByContentType() throws Exception {
		DataDefinition randomDataDefinition = randomDataDefinition();

		DataDefinition postDataDefinition =
			testPostDataDefinitionByContentType_addDataDefinition(
				randomDataDefinition);

		assertEquals(randomDataDefinition, postDataDefinition);
		assertValid(postDataDefinition);
	}

	protected DataDefinition
			testPostDataDefinitionByContentType_addDataDefinition(
				DataDefinition dataDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDataDefinitionCopy() throws Exception {
		DataDefinition randomDataDefinition = randomDataDefinition();

		DataDefinition postDataDefinition =
			testPostDataDefinitionCopy_addDataDefinition(randomDataDefinition);

		assertEquals(randomDataDefinition, postDataDefinition);
		assertValid(postDataDefinition);
	}

	protected DataDefinition testPostDataDefinitionCopy_addDataDefinition(
			DataDefinition dataDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteDataDefinitionByContentType() throws Exception {
		DataDefinition randomDataDefinition = randomDataDefinition();

		DataDefinition postDataDefinition =
			testPostSiteDataDefinitionByContentType_addDataDefinition(
				randomDataDefinition);

		assertEquals(randomDataDefinition, postDataDefinition);
		assertValid(postDataDefinition);
	}

	protected DataDefinition
			testPostSiteDataDefinitionByContentType_addDataDefinition(
				DataDefinition dataDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutDataDefinition() throws Exception {
		DataDefinition postDataDefinition =
			testPutDataDefinition_addDataDefinition();

		DataDefinition randomDataDefinition = randomDataDefinition();

		DataDefinition putDataDefinition =
			dataDefinitionResource.putDataDefinition(
				postDataDefinition.getId(), randomDataDefinition);

		assertEquals(randomDataDefinition, putDataDefinition);
		assertValid(putDataDefinition);

		DataDefinition getDataDefinition =
			dataDefinitionResource.getDataDefinition(putDataDefinition.getId());

		assertEquals(randomDataDefinition, getDataDefinition);
		assertValid(getDataDefinition);
	}

	protected DataDefinition testPutDataDefinition_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutDataDefinitionPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataDefinition dataDefinition =
			testPutDataDefinitionPermissionsPage_addDataDefinition();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			dataDefinitionResource.putDataDefinitionPermissionsPageHttpResponse(
				dataDefinition.getId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"VIEW"});
							setRoleName(role.getName());
						}
					}
				}));

		assertHttpResponseStatusCode(
			404,
			dataDefinitionResource.putDataDefinitionPermissionsPageHttpResponse(
				0L,
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected DataDefinition
			testPutDataDefinitionPermissionsPage_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteDataDefinitionByContentTypeByExternalReferenceCode()
		throws Exception {

		DataDefinition postDataDefinition =
			testPutSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition();

		DataDefinition randomDataDefinition = randomDataDefinition();

		DataDefinition putDataDefinition =
			dataDefinitionResource.
				putSiteDataDefinitionByContentTypeByExternalReferenceCode(
					postDataDefinition.getSiteId(),
					postDataDefinition.getContentType(),
					postDataDefinition.getExternalReferenceCode(),
					randomDataDefinition);

		assertEquals(randomDataDefinition, putDataDefinition);
		assertValid(putDataDefinition);

		DataDefinition getDataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByExternalReferenceCode(
					putDataDefinition.getSiteId(),
					putDataDefinition.getContentType(),
					putDataDefinition.getExternalReferenceCode());

		assertEquals(randomDataDefinition, getDataDefinition);
		assertValid(getDataDefinition);

		DataDefinition newDataDefinition =
			testPutSiteDataDefinitionByContentTypeByExternalReferenceCode_createDataDefinition();

		putDataDefinition =
			dataDefinitionResource.
				putSiteDataDefinitionByContentTypeByExternalReferenceCode(
					newDataDefinition.getSiteId(),
					newDataDefinition.getContentType(),
					newDataDefinition.getExternalReferenceCode(),
					newDataDefinition);

		assertEquals(newDataDefinition, putDataDefinition);
		assertValid(putDataDefinition);

		getDataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByExternalReferenceCode(
					putDataDefinition.getSiteId(),
					putDataDefinition.getContentType(),
					putDataDefinition.getExternalReferenceCode());

		assertEquals(newDataDefinition, getDataDefinition);

		Assert.assertEquals(
			newDataDefinition.getExternalReferenceCode(),
			putDataDefinition.getExternalReferenceCode());
	}

	protected DataDefinition
			testPutSiteDataDefinitionByContentTypeByExternalReferenceCode_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataDefinition
			testPutSiteDataDefinitionByContentTypeByExternalReferenceCode_createDataDefinition()
		throws Exception {

		return randomDataDefinition();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DataDefinition dataDefinition1 =
			testBatchEngineDeleteImportTask_addDataDefinition();

		testBatchEngineDeleteImportTask_deleteDataDefinition(
			200, null, dataDefinition1.getId());

		assertHttpResponseStatusCode(
			404,
			dataDefinitionResource.getDataDefinitionHttpResponse(
				dataDefinition1.getId()));
	}

	protected DataDefinition testBatchEngineDeleteImportTask_addDataDefinition()
		throws Exception {

		return testDeleteDataDefinition_addDataDefinition();
	}

	protected void testBatchEngineDeleteImportTask_deleteDataDefinition(
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
				"com.liferay.data.engine.rest.dto.v2_0.DataDefinition", null,
				null, null, null,
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

	protected DataDefinition testGraphQLDataDefinition_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataDefinition testGraphQLSiteDataDefinition_addDataDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DataDefinition dataDefinition, List<DataDefinition> dataDefinitions) {

		boolean contains = false;

		for (DataDefinition item : dataDefinitions) {
			if (equals(dataDefinition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			dataDefinitions + " does not contain " + dataDefinition, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DataDefinition dataDefinition1, DataDefinition dataDefinition2) {

		Assert.assertTrue(
			dataDefinition1 + " does not equal " + dataDefinition2,
			equals(dataDefinition1, dataDefinition2));
	}

	protected void assertEquals(
		List<DataDefinition> dataDefinitions1,
		List<DataDefinition> dataDefinitions2) {

		Assert.assertEquals(dataDefinitions1.size(), dataDefinitions2.size());

		for (int i = 0; i < dataDefinitions1.size(); i++) {
			DataDefinition dataDefinition1 = dataDefinitions1.get(i);
			DataDefinition dataDefinition2 = dataDefinitions2.get(i);

			assertEquals(dataDefinition1, dataDefinition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DataDefinition> dataDefinitions1,
		List<DataDefinition> dataDefinitions2) {

		Assert.assertEquals(dataDefinitions1.size(), dataDefinitions2.size());

		for (DataDefinition dataDefinition1 : dataDefinitions1) {
			boolean contains = false;

			for (DataDefinition dataDefinition2 : dataDefinitions2) {
				if (equals(dataDefinition1, dataDefinition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				dataDefinitions2 + " does not contain " + dataDefinition1,
				contains);
		}
	}

	protected void assertValid(DataDefinition dataDefinition) throws Exception {
		boolean valid = true;

		if (dataDefinition.getDateCreated() == null) {
			valid = false;
		}

		if (dataDefinition.getDateModified() == null) {
			valid = false;
		}

		if (dataDefinition.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				dataDefinition.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"availableLanguageIds", additionalAssertFieldName)) {

				if (dataDefinition.getAvailableLanguageIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (dataDefinition.getContentType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionFields", additionalAssertFieldName)) {

				if (dataDefinition.getDataDefinitionFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionKey", additionalAssertFieldName)) {

				if (dataDefinition.getDataDefinitionKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataRules", additionalAssertFieldName)) {
				if (dataDefinition.getDataRules() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultDataLayout", additionalAssertFieldName)) {

				if (dataDefinition.getDefaultDataLayout() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (dataDefinition.getDefaultLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (dataDefinition.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (dataDefinition.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (dataDefinition.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("storageType", additionalAssertFieldName)) {
				if (dataDefinition.getStorageType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (dataDefinition.getUserId() == null) {
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

	protected void assertValid(Page<DataDefinition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DataDefinition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DataDefinition> dataDefinitions = page.getItems();

		int size = dataDefinitions.size();

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
					com.liferay.data.engine.rest.dto.v2_0.DataDefinition.
						class)) {

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
		DataDefinition dataDefinition1, DataDefinition dataDefinition2) {

		if (dataDefinition1 == dataDefinition2) {
			return true;
		}

		if (!Objects.equals(
				dataDefinition1.getSiteId(), dataDefinition2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"availableLanguageIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataDefinition1.getAvailableLanguageIds(),
						dataDefinition2.getAvailableLanguageIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataDefinition1.getContentType(),
						dataDefinition2.getContentType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionFields", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataDefinition1.getDataDefinitionFields(),
						dataDefinition2.getDataDefinitionFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataDefinition1.getDataDefinitionKey(),
						dataDefinition2.getDataDefinitionKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataRules", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataDefinition1.getDataRules(),
						dataDefinition2.getDataRules())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataDefinition1.getDateCreated(),
						dataDefinition2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataDefinition1.getDateModified(),
						dataDefinition2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultDataLayout", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataDefinition1.getDefaultDataLayout(),
						dataDefinition2.getDefaultDataLayout())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataDefinition1.getDefaultLanguageId(),
						dataDefinition2.getDefaultLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataDefinition1.getDescription(),
						(Map)dataDefinition2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataDefinition1.getExternalReferenceCode(),
						dataDefinition2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataDefinition1.getId(), dataDefinition2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataDefinition1.getName(),
						(Map)dataDefinition2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("storageType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataDefinition1.getStorageType(),
						dataDefinition2.getStorageType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataDefinition1.getUserId(),
						dataDefinition2.getUserId())) {

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

		if (!(_dataDefinitionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_dataDefinitionResource;

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
		DataDefinition dataDefinition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("availableLanguageIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("contentType")) {
			Object object = dataDefinition.getContentType();

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

		if (entityFieldName.equals("dataDefinitionFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataDefinitionKey")) {
			Object object = dataDefinition.getDataDefinitionKey();

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

		if (entityFieldName.equals("dataRules")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = dataDefinition.getDateCreated();

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

				sb.append(_format.format(dataDefinition.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = dataDefinition.getDateModified();

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

				sb.append(_format.format(dataDefinition.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("defaultDataLayout")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("defaultLanguageId")) {
			Object object = dataDefinition.getDefaultLanguageId();

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

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = dataDefinition.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("storageType")) {
			Object object = dataDefinition.getStorageType();

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

		if (entityFieldName.equals("userId")) {
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

	protected DataDefinition randomDataDefinition() throws Exception {
		return new DataDefinition() {
			{
				contentType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dataDefinitionKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				defaultLanguageId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
				storageType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userId = RandomTestUtil.randomLong();
			}
		};
	}

	protected DataDefinition randomIrrelevantDataDefinition() throws Exception {
		DataDefinition randomIrrelevantDataDefinition = randomDataDefinition();

		randomIrrelevantDataDefinition.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantDataDefinition;
	}

	protected DataDefinition randomPatchDataDefinition() throws Exception {
		return randomDataDefinition();
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

	protected DataDefinitionResource dataDefinitionResource;
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
		LogFactoryUtil.getLog(BaseDataDefinitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource
		_dataDefinitionResource;

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