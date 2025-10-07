/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Specification;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SpecificationResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.SpecificationSerDes;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseSpecificationResourceTestCase {

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

		_specificationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		specificationResource = SpecificationResource.builder(
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

		Specification specification1 = randomSpecification();

		String json = objectMapper.writeValueAsString(specification1);

		Specification specification2 = SpecificationSerDes.toDTO(json);

		Assert.assertTrue(equals(specification1, specification2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Specification specification = randomSpecification();

		String json1 = objectMapper.writeValueAsString(specification);
		String json2 = SpecificationSerDes.toJSON(specification);

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

		Specification specification = randomSpecification();

		specification.setExternalReferenceCode(regex);
		specification.setKey(regex);

		String json = SpecificationSerDes.toJSON(specification);

		Assert.assertFalse(json.contains(regex));

		specification = SpecificationSerDes.toDTO(json);

		Assert.assertEquals(regex, specification.getExternalReferenceCode());
		Assert.assertEquals(regex, specification.getKey());
	}

	@Test
	public void testDeleteSpecification() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Specification specification =
			testDeleteSpecification_addSpecification();

		assertHttpResponseStatusCode(
			204,
			specificationResource.deleteSpecificationHttpResponse(
				specification.getId()));

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification.getId()));
		assertHttpResponseStatusCode(
			404, specificationResource.getSpecificationHttpResponse(0L));
	}

	protected Specification testDeleteSpecification_addSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSpecification() throws Exception {

		// No namespace

		Specification specification1 =
			testGraphQLDeleteSpecification_addSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSpecification",
						new HashMap<String, Object>() {
							{
								put("id", specification1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteSpecification"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"specification",
					new HashMap<String, Object>() {
						{
							put("id", specification1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Specification specification2 =
			testGraphQLDeleteSpecification_addSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteSpecification",
							new HashMap<String, Object>() {
								{
									put("id", specification2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteSpecification"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"specification",
						new HashMap<String, Object>() {
							{
								put("id", specification2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Specification testGraphQLDeleteSpecification_addSpecification()
		throws Exception {

		return testGraphQLSpecification_addSpecification();
	}

	@Test
	public void testDeleteSpecificationBatch() throws Exception {
		Specification specification1 =
			testDeleteSpecificationBatch_addSpecification();

		testDeleteSpecificationBatch_deleteSpecification(
			202, specification1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification1.getId()));

		specification1 = testDeleteSpecificationBatch_addSpecification();

		testDeleteSpecificationBatch_deleteSpecification(
			202, null, specification1.getId());

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification1.getId()));

		specification1 = testDeleteSpecificationBatch_addSpecification();
		Specification specification2 =
			testDeleteSpecificationBatch_addSpecification();

		testDeleteSpecificationBatch_deleteSpecification(
			202, specification2.getExternalReferenceCode(),
			specification1.getId());

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification1.getId()));
		assertHttpResponseStatusCode(
			200,
			specificationResource.getSpecificationHttpResponse(
				specification2.getId()));

		testDeleteSpecificationBatch_deleteSpecification(
			202, specification2.getExternalReferenceCode(),
			specification1.getId());

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification2.getId()));
	}

	protected Specification testDeleteSpecificationBatch_addSpecification()
		throws Exception {

		return testDeleteSpecification_addSpecification();
	}

	protected void testDeleteSpecificationBatch_deleteSpecification(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			specificationResource.deleteSpecificationBatchHttpResponse(
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
	public void testDeleteSpecificationByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Specification specification =
			testDeleteSpecificationByExternalReferenceCode_addSpecification();

		assertHttpResponseStatusCode(
			204,
			specificationResource.
				deleteSpecificationByExternalReferenceCodeHttpResponse(
					specification.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			specificationResource.
				getSpecificationByExternalReferenceCodeHttpResponse(
					specification.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			specificationResource.
				getSpecificationByExternalReferenceCodeHttpResponse("-"));
	}

	protected Specification
			testDeleteSpecificationByExternalReferenceCode_addSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSpecificationByExternalReferenceCode()
		throws Exception {

		// No namespace

		Specification specification1 =
			testGraphQLDeleteSpecificationByExternalReferenceCode_addSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSpecificationByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										specification1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSpecificationByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"specificationByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									specification1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Specification specification2 =
			testGraphQLDeleteSpecificationByExternalReferenceCode_addSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteSpecificationByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											specification2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteSpecificationByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"specificationByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										specification2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Specification
			testGraphQLDeleteSpecificationByExternalReferenceCode_addSpecification()
		throws Exception {

		return testGraphQLSpecification_addSpecification();
	}

	@Test
	public void testGetSpecification() throws Exception {
		Specification postSpecification =
			testGetSpecification_addSpecification();

		Specification getSpecification = specificationResource.getSpecification(
			postSpecification.getId());

		assertEquals(postSpecification, getSpecification);
		assertValid(getSpecification);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Specification postSpecification =
			testGetSpecification_addSpecification();

		Specification getSpecification = specificationResource.getSpecification(
			postSpecification.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification"
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

		Object item = vulcanCRUDItemDelegate.getItem(postSpecification.getId());

		assertEquals(
			getSpecification, SpecificationSerDes.toDTO(item.toString()));
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

	protected Specification testGetSpecification_addSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSpecification() throws Exception {
		Specification specification =
			testGraphQLGetSpecification_addSpecification();

		// No namespace

		Assert.assertTrue(
			equals(
				specification,
				SpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"specification",
								new HashMap<String, Object>() {
									{
										put("id", specification.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/specification"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				specification,
				SpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"specification",
									new HashMap<String, Object>() {
										{
											put("id", specification.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/specification"))));
	}

	@Test
	public void testGraphQLGetSpecificationNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"specification",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"specification",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Specification testGraphQLGetSpecification_addSpecification()
		throws Exception {

		return testGraphQLSpecification_addSpecification();
	}

	@Test
	public void testGetSpecificationByExternalReferenceCode() throws Exception {
		Specification postSpecification =
			testGetSpecificationByExternalReferenceCode_addSpecification();

		Specification getSpecification =
			specificationResource.getSpecificationByExternalReferenceCode(
				postSpecification.getExternalReferenceCode());

		assertEquals(postSpecification, getSpecification);
		assertValid(getSpecification);
	}

	protected Specification
			testGetSpecificationByExternalReferenceCode_addSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSpecificationByExternalReferenceCode()
		throws Exception {

		Specification specification =
			testGraphQLGetSpecificationByExternalReferenceCode_addSpecification();

		// No namespace

		Assert.assertTrue(
			equals(
				specification,
				SpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"specificationByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												specification.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/specificationByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				specification,
				SpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"specificationByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													specification.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/specificationByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSpecificationByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"specificationByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"specificationByExternalReferenceCode",
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

	protected Specification
			testGraphQLGetSpecificationByExternalReferenceCode_addSpecification()
		throws Exception {

		return testGraphQLSpecification_addSpecification();
	}

	@Test
	public void testGetSpecificationsPage() throws Exception {
		Page<Specification> page = specificationResource.getSpecificationsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Specification specification1 =
			testGetSpecificationsPage_addSpecification(randomSpecification());

		Specification specification2 =
			testGetSpecificationsPage_addSpecification(randomSpecification());

		page = specificationResource.getSpecificationsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(specification1, (List<Specification>)page.getItems());
		assertContains(specification2, (List<Specification>)page.getItems());
		assertValid(page, testGetSpecificationsPage_getExpectedActions());

		specificationResource.deleteSpecification(specification1.getId());

		specificationResource.deleteSpecification(specification2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSpecificationsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSpecificationsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Specification specification1 = randomSpecification();

		specification1 = testGetSpecificationsPage_addSpecification(
			specification1);

		for (EntityField entityField : entityFields) {
			Page<Specification> page =
				specificationResource.getSpecificationsPage(
					null,
					getFilterString(entityField, "between", specification1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(specification1),
				(List<Specification>)page.getItems());
		}
	}

	@Test
	public void testGetSpecificationsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSpecificationsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSpecificationsPageWithFilterStringContains()
		throws Exception {

		testGetSpecificationsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSpecificationsPageWithFilterStringEquals()
		throws Exception {

		testGetSpecificationsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSpecificationsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSpecificationsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSpecificationsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Specification specification1 =
			testGetSpecificationsPage_addSpecification(randomSpecification());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Specification specification2 =
			testGetSpecificationsPage_addSpecification(randomSpecification());

		for (EntityField entityField : entityFields) {
			Page<Specification> page =
				specificationResource.getSpecificationsPage(
					null,
					getFilterString(entityField, operator, specification1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(specification1),
				(List<Specification>)page.getItems());
		}
	}

	@Test
	public void testGetSpecificationsPageWithPagination() throws Exception {
		Page<Specification> specificationsPage =
			specificationResource.getSpecificationsPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			specificationsPage.getTotalCount());

		Specification specification1 =
			testGetSpecificationsPage_addSpecification(randomSpecification());

		Specification specification2 =
			testGetSpecificationsPage_addSpecification(randomSpecification());

		Specification specification3 =
			testGetSpecificationsPage_addSpecification(randomSpecification());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Specification> page1 =
				specificationResource.getSpecificationsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				specification1, (List<Specification>)page1.getItems());

			Page<Specification> page2 =
				specificationResource.getSpecificationsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				specification2, (List<Specification>)page2.getItems());

			Page<Specification> page3 =
				specificationResource.getSpecificationsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				specification3, (List<Specification>)page3.getItems());
		}
		else {
			Page<Specification> page1 =
				specificationResource.getSpecificationsPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<Specification> specifications1 =
				(List<Specification>)page1.getItems();

			Assert.assertEquals(
				specifications1.toString(), totalCount + 2,
				specifications1.size());

			Page<Specification> page2 =
				specificationResource.getSpecificationsPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Specification> specifications2 =
				(List<Specification>)page2.getItems();

			Assert.assertEquals(
				specifications2.toString(), 1, specifications2.size());

			Page<Specification> page3 =
				specificationResource.getSpecificationsPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				specification1, (List<Specification>)page3.getItems());
			assertContains(
				specification2, (List<Specification>)page3.getItems());
			assertContains(
				specification3, (List<Specification>)page3.getItems());
		}
	}

	@Test
	public void testGetSpecificationsPageWithSortDateTime() throws Exception {
		testGetSpecificationsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, specification1, specification2) -> {
				BeanTestUtil.setProperty(
					specification1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSpecificationsPageWithSortDouble() throws Exception {
		testGetSpecificationsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, specification1, specification2) -> {
				BeanTestUtil.setProperty(
					specification1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					specification2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSpecificationsPageWithSortInteger() throws Exception {
		testGetSpecificationsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, specification1, specification2) -> {
				BeanTestUtil.setProperty(
					specification1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					specification2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSpecificationsPageWithSortString() throws Exception {
		testGetSpecificationsPageWithSort(
			EntityField.Type.STRING,
			(entityField, specification1, specification2) -> {
				Class<?> clazz = specification1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						specification1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						specification2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						specification1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						specification2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						specification1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						specification2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSpecificationsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, Specification, Specification, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Specification specification1 = randomSpecification();
		Specification specification2 = randomSpecification();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, specification1, specification2);
		}

		specification1 = testGetSpecificationsPage_addSpecification(
			specification1);

		specification2 = testGetSpecificationsPage_addSpecification(
			specification2);

		Page<Specification> page = specificationResource.getSpecificationsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Specification> ascPage =
				specificationResource.getSpecificationsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				specification1, (List<Specification>)ascPage.getItems());
			assertContains(
				specification2, (List<Specification>)ascPage.getItems());

			Page<Specification> descPage =
				specificationResource.getSpecificationsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				specification2, (List<Specification>)descPage.getItems());
			assertContains(
				specification1, (List<Specification>)descPage.getItems());
		}
	}

	protected Specification testGetSpecificationsPage_addSpecification(
			Specification specification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSpecificationsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"specifications",
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

		JSONObject specificationsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/specifications");

		long totalCount = specificationsJSONObject.getLong("totalCount");

		Specification specification1 =
			testGraphQLSpecification_addSpecification(randomSpecification());

		Specification specification2 =
			testGraphQLSpecification_addSpecification(randomSpecification());

		specificationsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/specifications");

		Assert.assertEquals(
			totalCount + 2, specificationsJSONObject.getLong("totalCount"));

		assertContains(
			specification1,
			Arrays.asList(
				SpecificationSerDes.toDTOs(
					specificationsJSONObject.getString("items"))));
		assertContains(
			specification2,
			Arrays.asList(
				SpecificationSerDes.toDTOs(
					specificationsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		specificationsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/specifications");

		Assert.assertEquals(
			totalCount + 2, specificationsJSONObject.getLong("totalCount"));

		assertContains(
			specification1,
			Arrays.asList(
				SpecificationSerDes.toDTOs(
					specificationsJSONObject.getString("items"))));
		assertContains(
			specification2,
			Arrays.asList(
				SpecificationSerDes.toDTOs(
					specificationsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchSpecification() throws Exception {
		Specification postSpecification =
			testPatchSpecification_addSpecification();

		Specification randomPatchSpecification = randomPatchSpecification();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Specification patchSpecification =
			specificationResource.patchSpecification(
				postSpecification.getId(), randomPatchSpecification);

		Specification expectedPatchSpecification = postSpecification.clone();

		BeanTestUtil.copyProperties(
			randomPatchSpecification, expectedPatchSpecification);

		Specification getSpecification = specificationResource.getSpecification(
			patchSpecification.getId());

		assertEquals(expectedPatchSpecification, getSpecification);
		assertValid(getSpecification);
	}

	protected Specification testPatchSpecification_addSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchSpecificationByExternalReferenceCode()
		throws Exception {

		Specification postSpecification =
			testPatchSpecificationByExternalReferenceCode_addSpecification();

		Specification randomPatchSpecification = randomPatchSpecification();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Specification patchSpecification =
			specificationResource.patchSpecificationByExternalReferenceCode(
				postSpecification.getExternalReferenceCode(),
				randomPatchSpecification);

		Specification expectedPatchSpecification = postSpecification.clone();

		BeanTestUtil.copyProperties(
			randomPatchSpecification, expectedPatchSpecification);

		Specification getSpecification =
			specificationResource.getSpecificationByExternalReferenceCode(
				patchSpecification.getExternalReferenceCode());

		assertEquals(expectedPatchSpecification, getSpecification);
		assertValid(getSpecification);
	}

	protected Specification
			testPatchSpecificationByExternalReferenceCode_addSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSpecification() throws Exception {
		Specification randomSpecification = randomSpecification();

		Specification postSpecification =
			testPostSpecification_addSpecification(randomSpecification);

		assertEquals(randomSpecification, postSpecification);
		assertValid(postSpecification);
	}

	protected Specification testPostSpecification_addSpecification(
			Specification specification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSpecification() throws Exception {
		Specification randomSpecification = randomSpecification();

		Specification specification = testGraphQLSpecification_addSpecification(
			randomSpecification);

		Assert.assertTrue(equals(randomSpecification, specification));
	}

	@Test
	public void testPutSpecificationByExternalReferenceCode() throws Exception {
		Specification postSpecification =
			testPutSpecificationByExternalReferenceCode_addSpecification();

		Specification randomSpecification = randomSpecification();

		Specification putSpecification =
			specificationResource.putSpecificationByExternalReferenceCode(
				postSpecification.getExternalReferenceCode(),
				randomSpecification);

		assertEquals(randomSpecification, putSpecification);
		assertValid(putSpecification);

		Specification getSpecification =
			specificationResource.getSpecificationByExternalReferenceCode(
				putSpecification.getExternalReferenceCode());

		assertEquals(randomSpecification, getSpecification);
		assertValid(getSpecification);

		Specification newSpecification =
			testPutSpecificationByExternalReferenceCode_createSpecification();

		putSpecification =
			specificationResource.putSpecificationByExternalReferenceCode(
				newSpecification.getExternalReferenceCode(), newSpecification);

		assertEquals(newSpecification, putSpecification);
		assertValid(putSpecification);

		getSpecification =
			specificationResource.getSpecificationByExternalReferenceCode(
				putSpecification.getExternalReferenceCode());

		assertEquals(newSpecification, getSpecification);

		Assert.assertEquals(
			newSpecification.getExternalReferenceCode(),
			putSpecification.getExternalReferenceCode());
	}

	protected Specification
			testPutSpecificationByExternalReferenceCode_addSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Specification
			testPutSpecificationByExternalReferenceCode_createSpecification()
		throws Exception {

		return randomSpecification();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Specification specification1 =
			testBatchEngineDeleteImportTask_addSpecification();

		testBatchEngineDeleteImportTask_deleteSpecification(
			200, specification1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification1.getId()));

		specification1 = testBatchEngineDeleteImportTask_addSpecification();

		testBatchEngineDeleteImportTask_deleteSpecification(
			200, null, specification1.getId());

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification1.getId()));

		specification1 = testBatchEngineDeleteImportTask_addSpecification();
		Specification specification2 =
			testBatchEngineDeleteImportTask_addSpecification();

		testBatchEngineDeleteImportTask_deleteSpecification(
			200, specification2.getExternalReferenceCode(),
			specification1.getId());

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification1.getId()));
		assertHttpResponseStatusCode(
			200,
			specificationResource.getSpecificationHttpResponse(
				specification2.getId()));

		testBatchEngineDeleteImportTask_deleteSpecification(
			200, specification2.getExternalReferenceCode(),
			specification1.getId());

		assertHttpResponseStatusCode(
			404,
			specificationResource.getSpecificationHttpResponse(
				specification2.getId()));
	}

	protected Specification testBatchEngineDeleteImportTask_addSpecification()
		throws Exception {

		return testDeleteSpecification_addSpecification();
	}

	protected void testBatchEngineDeleteImportTask_deleteSpecification(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification",
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

	protected Specification testGraphQLSpecification_addSpecification()
		throws Exception {

		return testGraphQLSpecification_addSpecification(randomSpecification());
	}

	protected Specification testGraphQLSpecification_addSpecification(
			Specification specification)
		throws Exception {

		JSONDeserializer<Specification> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Specification.class)) {

			if (getGraphQLValue(field.get(specification)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(specification)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSpecification",
						new HashMap<String, Object>() {
							{
								put("specification", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSpecification"),
			Specification.class);
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
		Specification specification, List<Specification> specifications) {

		boolean contains = false;

		for (Specification item : specifications) {
			if (equals(specification, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			specifications + " does not contain " + specification, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		Specification specification1, Specification specification2) {

		Assert.assertTrue(
			specification1 + " does not equal " + specification2,
			equals(specification1, specification2));
	}

	protected void assertEquals(
		List<Specification> specifications1,
		List<Specification> specifications2) {

		Assert.assertEquals(specifications1.size(), specifications2.size());

		for (int i = 0; i < specifications1.size(); i++) {
			Specification specification1 = specifications1.get(i);
			Specification specification2 = specifications2.get(i);

			assertEquals(specification1, specification2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Specification> specifications1,
		List<Specification> specifications2) {

		Assert.assertEquals(specifications1.size(), specifications2.size());

		for (Specification specification1 : specifications1) {
			boolean contains = false;

			for (Specification specification2 : specifications2) {
				if (equals(specification1, specification2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				specifications2 + " does not contain " + specification1,
				contains);
		}
	}

	protected void assertValid(Specification specification) throws Exception {
		boolean valid = true;

		if (specification.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (specification.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (specification.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("facetable", additionalAssertFieldName)) {
				if (specification.getFacetable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (specification.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionId", additionalAssertFieldName)) {

				if (specification.getListTypeDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionIds", additionalAssertFieldName)) {

				if (specification.getListTypeDefinitionIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("optionCategory", additionalAssertFieldName)) {
				if (specification.getOptionCategory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (specification.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (specification.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("visible", additionalAssertFieldName)) {
				if (specification.getVisible() == null) {
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

	protected void assertValid(Page<Specification> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Specification> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Specification> specifications = page.getItems();

		int size = specifications.size();

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
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						Specification.class)) {

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
		Specification specification1, Specification specification2) {

		if (specification1 == specification2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)specification1.getDescription(),
						(Map)specification2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						specification1.getExternalReferenceCode(),
						specification2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("facetable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						specification1.getFacetable(),
						specification2.getFacetable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						specification1.getId(), specification2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						specification1.getKey(), specification2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						specification1.getListTypeDefinitionId(),
						specification2.getListTypeDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						specification1.getListTypeDefinitionIds(),
						specification2.getListTypeDefinitionIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("optionCategory", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						specification1.getOptionCategory(),
						specification2.getOptionCategory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						specification1.getPriority(),
						specification2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!equals(
						(Map)specification1.getTitle(),
						(Map)specification2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("visible", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						specification1.getVisible(),
						specification2.getVisible())) {

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

		if (!(_specificationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_specificationResource;

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
		EntityField entityField, String operator, Specification specification) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = specification.getExternalReferenceCode();

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

		if (entityFieldName.equals("facetable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("key")) {
			Object object = specification.getKey();

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

		if (entityFieldName.equals("listTypeDefinitionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("listTypeDefinitionIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("optionCategory")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(specification.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("title")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("visible")) {
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

	protected Specification randomSpecification() throws Exception {
		return new Specification() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				facetable = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				listTypeDefinitionId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
				visible = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected Specification randomIrrelevantSpecification() throws Exception {
		Specification randomIrrelevantSpecification = randomSpecification();

		return randomIrrelevantSpecification;
	}

	protected Specification randomPatchSpecification() throws Exception {
		return randomSpecification();
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

	protected SpecificationResource specificationResource;
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
		LogFactoryUtil.getLog(BaseSpecificationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		SpecificationResource _specificationResource;

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