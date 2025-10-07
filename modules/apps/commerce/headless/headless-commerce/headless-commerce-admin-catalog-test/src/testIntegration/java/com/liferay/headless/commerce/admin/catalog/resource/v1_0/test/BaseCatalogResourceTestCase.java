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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.CatalogResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.CatalogSerDes;
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
public abstract class BaseCatalogResourceTestCase {

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

		_catalogResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		catalogResource = CatalogResource.builder(
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

		Catalog catalog1 = randomCatalog();

		String json = objectMapper.writeValueAsString(catalog1);

		Catalog catalog2 = CatalogSerDes.toDTO(json);

		Assert.assertTrue(equals(catalog1, catalog2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Catalog catalog = randomCatalog();

		String json1 = objectMapper.writeValueAsString(catalog);
		String json2 = CatalogSerDes.toJSON(catalog);

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

		Catalog catalog = randomCatalog();

		catalog.setCurrencyCode(regex);
		catalog.setCurrencyExternalReferenceCode(regex);
		catalog.setDefaultLanguageId(regex);
		catalog.setExternalReferenceCode(regex);
		catalog.setName(regex);

		String json = CatalogSerDes.toJSON(catalog);

		Assert.assertFalse(json.contains(regex));

		catalog = CatalogSerDes.toDTO(json);

		Assert.assertEquals(regex, catalog.getCurrencyCode());
		Assert.assertEquals(regex, catalog.getCurrencyExternalReferenceCode());
		Assert.assertEquals(regex, catalog.getDefaultLanguageId());
		Assert.assertEquals(regex, catalog.getExternalReferenceCode());
		Assert.assertEquals(regex, catalog.getName());
	}

	@Test
	public void testDeleteCatalog() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Catalog catalog = testDeleteCatalog_addCatalog();

		assertHttpResponseStatusCode(
			204, catalogResource.deleteCatalogHttpResponse(catalog.getId()));

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog.getId()));
		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(0L));
	}

	protected Catalog testDeleteCatalog_addCatalog() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCatalog() throws Exception {

		// No namespace

		Catalog catalog1 = testGraphQLDeleteCatalog_addCatalog();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCatalog",
						new HashMap<String, Object>() {
							{
								put("id", catalog1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteCatalog"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"catalog",
					new HashMap<String, Object>() {
						{
							put("id", catalog1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Catalog catalog2 = testGraphQLDeleteCatalog_addCatalog();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteCatalog",
							new HashMap<String, Object>() {
								{
									put("id", catalog2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteCatalog"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"catalog",
						new HashMap<String, Object>() {
							{
								put("id", catalog2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Catalog testGraphQLDeleteCatalog_addCatalog() throws Exception {
		return testGraphQLCatalog_addCatalog();
	}

	@Test
	public void testDeleteCatalogBatch() throws Exception {
		Catalog catalog1 = testDeleteCatalogBatch_addCatalog();

		testDeleteCatalogBatch_deleteCatalog(
			202, catalog1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog1.getId()));

		catalog1 = testDeleteCatalogBatch_addCatalog();

		testDeleteCatalogBatch_deleteCatalog(202, null, catalog1.getId());

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog1.getId()));

		catalog1 = testDeleteCatalogBatch_addCatalog();
		Catalog catalog2 = testDeleteCatalogBatch_addCatalog();

		testDeleteCatalogBatch_deleteCatalog(
			202, catalog2.getExternalReferenceCode(), catalog1.getId());

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog1.getId()));
		assertHttpResponseStatusCode(
			200, catalogResource.getCatalogHttpResponse(catalog2.getId()));

		testDeleteCatalogBatch_deleteCatalog(
			202, catalog2.getExternalReferenceCode(), catalog1.getId());

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog2.getId()));
	}

	protected Catalog testDeleteCatalogBatch_addCatalog() throws Exception {
		return testDeleteCatalog_addCatalog();
	}

	protected void testDeleteCatalogBatch_deleteCatalog(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			catalogResource.deleteCatalogBatchHttpResponse(
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
	public void testDeleteCatalogByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Catalog catalog = testDeleteCatalogByExternalReferenceCode_addCatalog();

		assertHttpResponseStatusCode(
			204,
			catalogResource.deleteCatalogByExternalReferenceCodeHttpResponse(
				catalog.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			catalogResource.getCatalogByExternalReferenceCodeHttpResponse(
				catalog.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			catalogResource.getCatalogByExternalReferenceCodeHttpResponse("-"));
	}

	protected Catalog testDeleteCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCatalogByExternalReferenceCode()
		throws Exception {

		// No namespace

		Catalog catalog1 =
			testGraphQLDeleteCatalogByExternalReferenceCode_addCatalog();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCatalogByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + catalog1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteCatalogByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"catalogByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + catalog1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Catalog catalog2 =
			testGraphQLDeleteCatalogByExternalReferenceCode_addCatalog();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteCatalogByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											catalog2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteCatalogByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"catalogByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + catalog2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Catalog
			testGraphQLDeleteCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		return testGraphQLCatalog_addCatalog();
	}

	@Test
	public void testGetCatalog() throws Exception {
		Catalog postCatalog = testGetCatalog_addCatalog();

		Catalog getCatalog = catalogResource.getCatalog(postCatalog.getId());

		assertEquals(postCatalog, getCatalog);
		assertValid(getCatalog);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Catalog postCatalog = testGetCatalog_addCatalog();

		Catalog getCatalog = catalogResource.getCatalog(postCatalog.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog"
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

		Object item = vulcanCRUDItemDelegate.getItem(postCatalog.getId());

		assertEquals(getCatalog, CatalogSerDes.toDTO(item.toString()));
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

	protected Catalog testGetCatalog_addCatalog() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCatalog() throws Exception {
		Catalog catalog = testGraphQLGetCatalog_addCatalog();

		// No namespace

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"catalog",
								new HashMap<String, Object>() {
									{
										put("id", catalog.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/catalog"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"catalog",
									new HashMap<String, Object>() {
										{
											put("id", catalog.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/catalog"))));
	}

	@Test
	public void testGraphQLGetCatalogNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"catalog",
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
							"catalog",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Catalog testGraphQLGetCatalog_addCatalog() throws Exception {
		return testGraphQLCatalog_addCatalog();
	}

	@Test
	public void testGetCatalogByExternalReferenceCode() throws Exception {
		Catalog postCatalog =
			testGetCatalogByExternalReferenceCode_addCatalog();

		Catalog getCatalog = catalogResource.getCatalogByExternalReferenceCode(
			postCatalog.getExternalReferenceCode());

		assertEquals(postCatalog, getCatalog);
		assertValid(getCatalog);
	}

	protected Catalog testGetCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCatalogByExternalReferenceCode()
		throws Exception {

		Catalog catalog =
			testGraphQLGetCatalogByExternalReferenceCode_addCatalog();

		// No namespace

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"catalogByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												catalog.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/catalogByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"catalogByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													catalog.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/catalogByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetCatalogByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"catalogByExternalReferenceCode",
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
							"catalogByExternalReferenceCode",
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

	protected Catalog testGraphQLGetCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		return testGraphQLCatalog_addCatalog();
	}

	@Test
	public void testGetCatalogsPage() throws Exception {
		Page<Catalog> page = catalogResource.getCatalogsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Catalog catalog1 = testGetCatalogsPage_addCatalog(randomCatalog());

		Catalog catalog2 = testGetCatalogsPage_addCatalog(randomCatalog());

		page = catalogResource.getCatalogsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(catalog1, (List<Catalog>)page.getItems());
		assertContains(catalog2, (List<Catalog>)page.getItems());
		assertValid(page, testGetCatalogsPage_getExpectedActions());

		catalogResource.deleteCatalog(catalog1.getId());

		catalogResource.deleteCatalog(catalog2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetCatalogsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCatalogsPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Catalog catalog1 = randomCatalog();

		catalog1 = testGetCatalogsPage_addCatalog(catalog1);

		for (EntityField entityField : entityFields) {
			Page<Catalog> page = catalogResource.getCatalogsPage(
				null, getFilterString(entityField, "between", catalog1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(catalog1),
				(List<Catalog>)page.getItems());
		}
	}

	@Test
	public void testGetCatalogsPageWithFilterDoubleEquals() throws Exception {
		testGetCatalogsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetCatalogsPageWithFilterStringContains() throws Exception {
		testGetCatalogsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetCatalogsPageWithFilterStringEquals() throws Exception {
		testGetCatalogsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetCatalogsPageWithFilterStringStartsWith()
		throws Exception {

		testGetCatalogsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetCatalogsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Catalog catalog1 = testGetCatalogsPage_addCatalog(randomCatalog());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Catalog catalog2 = testGetCatalogsPage_addCatalog(randomCatalog());

		for (EntityField entityField : entityFields) {
			Page<Catalog> page = catalogResource.getCatalogsPage(
				null, getFilterString(entityField, operator, catalog1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(catalog1),
				(List<Catalog>)page.getItems());
		}
	}

	@Test
	public void testGetCatalogsPageWithPagination() throws Exception {
		Page<Catalog> catalogsPage = catalogResource.getCatalogsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(catalogsPage.getTotalCount());

		Catalog catalog1 = testGetCatalogsPage_addCatalog(randomCatalog());

		Catalog catalog2 = testGetCatalogsPage_addCatalog(randomCatalog());

		Catalog catalog3 = testGetCatalogsPage_addCatalog(randomCatalog());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Catalog> page1 = catalogResource.getCatalogsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(catalog1, (List<Catalog>)page1.getItems());

			Page<Catalog> page2 = catalogResource.getCatalogsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(catalog2, (List<Catalog>)page2.getItems());

			Page<Catalog> page3 = catalogResource.getCatalogsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(catalog3, (List<Catalog>)page3.getItems());
		}
		else {
			Page<Catalog> page1 = catalogResource.getCatalogsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Catalog> catalogs1 = (List<Catalog>)page1.getItems();

			Assert.assertEquals(
				catalogs1.toString(), totalCount + 2, catalogs1.size());

			Page<Catalog> page2 = catalogResource.getCatalogsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Catalog> catalogs2 = (List<Catalog>)page2.getItems();

			Assert.assertEquals(catalogs2.toString(), 1, catalogs2.size());

			Page<Catalog> page3 = catalogResource.getCatalogsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(catalog1, (List<Catalog>)page3.getItems());
			assertContains(catalog2, (List<Catalog>)page3.getItems());
			assertContains(catalog3, (List<Catalog>)page3.getItems());
		}
	}

	@Test
	public void testGetCatalogsPageWithSortDateTime() throws Exception {
		testGetCatalogsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, catalog1, catalog2) -> {
				BeanTestUtil.setProperty(
					catalog1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetCatalogsPageWithSortDouble() throws Exception {
		testGetCatalogsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, catalog1, catalog2) -> {
				BeanTestUtil.setProperty(catalog1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(catalog2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetCatalogsPageWithSortInteger() throws Exception {
		testGetCatalogsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, catalog1, catalog2) -> {
				BeanTestUtil.setProperty(catalog1, entityField.getName(), 0);
				BeanTestUtil.setProperty(catalog2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetCatalogsPageWithSortString() throws Exception {
		testGetCatalogsPageWithSort(
			EntityField.Type.STRING,
			(entityField, catalog1, catalog2) -> {
				Class<?> clazz = catalog1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						catalog1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						catalog2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						catalog1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						catalog2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						catalog1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						catalog2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetCatalogsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Catalog, Catalog, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Catalog catalog1 = randomCatalog();
		Catalog catalog2 = randomCatalog();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, catalog1, catalog2);
		}

		catalog1 = testGetCatalogsPage_addCatalog(catalog1);

		catalog2 = testGetCatalogsPage_addCatalog(catalog2);

		Page<Catalog> page = catalogResource.getCatalogsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Catalog> ascPage = catalogResource.getCatalogsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(catalog1, (List<Catalog>)ascPage.getItems());
			assertContains(catalog2, (List<Catalog>)ascPage.getItems());

			Page<Catalog> descPage = catalogResource.getCatalogsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(catalog2, (List<Catalog>)descPage.getItems());
			assertContains(catalog1, (List<Catalog>)descPage.getItems());
		}
	}

	protected Catalog testGetCatalogsPage_addCatalog(Catalog catalog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCatalogsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"catalogs",
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

		JSONObject catalogsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/catalogs");

		long totalCount = catalogsJSONObject.getLong("totalCount");

		Catalog catalog1 = testGraphQLCatalog_addCatalog(randomCatalog());

		Catalog catalog2 = testGraphQLCatalog_addCatalog(randomCatalog());

		catalogsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/catalogs");

		Assert.assertEquals(
			totalCount + 2, catalogsJSONObject.getLong("totalCount"));

		assertContains(
			catalog1,
			Arrays.asList(
				CatalogSerDes.toDTOs(catalogsJSONObject.getString("items"))));
		assertContains(
			catalog2,
			Arrays.asList(
				CatalogSerDes.toDTOs(catalogsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		catalogsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/catalogs");

		Assert.assertEquals(
			totalCount + 2, catalogsJSONObject.getLong("totalCount"));

		assertContains(
			catalog1,
			Arrays.asList(
				CatalogSerDes.toDTOs(catalogsJSONObject.getString("items"))));
		assertContains(
			catalog2,
			Arrays.asList(
				CatalogSerDes.toDTOs(catalogsJSONObject.getString("items"))));
	}

	@Test
	public void testGetProductByExternalReferenceCodeCatalog()
		throws Exception {

		Catalog postCatalog =
			testGetProductByExternalReferenceCodeCatalog_addCatalog();

		Catalog getCatalog =
			catalogResource.getProductByExternalReferenceCodeCatalog(
				testGetProductByExternalReferenceCodeCatalog_getExternalReferenceCode(
					postCatalog),
				Pagination.of(1, 2));

		assertEquals(postCatalog, getCatalog);
		assertValid(getCatalog);
	}

	protected Catalog testGetProductByExternalReferenceCodeCatalog_addCatalog()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeCatalog_getExternalReferenceCode(
				Catalog catalog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCodeCatalog()
		throws Exception {

		Catalog catalog =
			testGraphQLGetProductByExternalReferenceCodeCatalog_addCatalog();

		// No namespace

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productByExternalReferenceCodeCatalog",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetProductByExternalReferenceCodeCatalog_getExternalReferenceCode(
													catalog) + "\"");
										put("page", 1);
										put("pageSize", 10);
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productByExternalReferenceCodeCatalog"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productByExternalReferenceCodeCatalog",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetProductByExternalReferenceCodeCatalog_getExternalReferenceCode(
														catalog) + "\"");
											put("page", 1);
											put("pageSize", 10);
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productByExternalReferenceCodeCatalog"))));
	}

	protected String
			testGraphQLGetProductByExternalReferenceCodeCatalog_getExternalReferenceCode(
				Catalog catalog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCodeCatalogNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productByExternalReferenceCodeCatalog",
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
							"productByExternalReferenceCodeCatalog",
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

	protected Catalog
			testGraphQLGetProductByExternalReferenceCodeCatalog_addCatalog()
		throws Exception {

		return testGraphQLProductCatalog_addCatalog();
	}

	@Test
	public void testGetProductIdCatalog() throws Exception {
		Catalog postCatalog = testGetProductIdCatalog_addCatalog();

		Catalog getCatalog = catalogResource.getProductIdCatalog(
			testGetProductIdCatalog_getId(postCatalog), Pagination.of(1, 2));

		assertEquals(postCatalog, getCatalog);
		assertValid(getCatalog);
	}

	protected Catalog testGetProductIdCatalog_addCatalog() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdCatalog_getId(Catalog catalog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductIdCatalog() throws Exception {
		Catalog catalog = testGraphQLGetProductIdCatalog_addCatalog();

		// No namespace

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productIdCatalog",
								new HashMap<String, Object>() {
									{
										put(
											"id",
											testGraphQLGetProductIdCatalog_getId(
												catalog));
										put("page", 1);
										put("pageSize", 10);
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productIdCatalog"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				catalog,
				CatalogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productIdCatalog",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												testGraphQLGetProductIdCatalog_getId(
													catalog));
											put("page", 1);
											put("pageSize", 10);
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productIdCatalog"))));
	}

	protected Long testGraphQLGetProductIdCatalog_getId(Catalog catalog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductIdCatalogNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productIdCatalog",
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
							"productIdCatalog",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Catalog testGraphQLGetProductIdCatalog_addCatalog()
		throws Exception {

		return testGraphQLProductCatalog_addCatalog();
	}

	@Test
	public void testPatchCatalog() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchCatalogByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostCatalog() throws Exception {
		Catalog randomCatalog = randomCatalog();

		Catalog postCatalog = testPostCatalog_addCatalog(randomCatalog);

		assertEquals(randomCatalog, postCatalog);
		assertValid(postCatalog);
	}

	protected Catalog testPostCatalog_addCatalog(Catalog catalog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostCatalog() throws Exception {
		Catalog randomCatalog = randomCatalog();

		Catalog catalog = testGraphQLCatalog_addCatalog(randomCatalog);

		Assert.assertTrue(equals(randomCatalog, catalog));
	}

	@Test
	public void testPutCatalogByExternalReferenceCode() throws Exception {
		Catalog postCatalog =
			testPutCatalogByExternalReferenceCode_addCatalog();

		Catalog randomCatalog = randomCatalog();

		Catalog putCatalog = catalogResource.putCatalogByExternalReferenceCode(
			postCatalog.getExternalReferenceCode(), randomCatalog);

		assertEquals(randomCatalog, putCatalog);
		assertValid(putCatalog);

		Catalog getCatalog = catalogResource.getCatalogByExternalReferenceCode(
			putCatalog.getExternalReferenceCode());

		assertEquals(randomCatalog, getCatalog);
		assertValid(getCatalog);

		Catalog newCatalog =
			testPutCatalogByExternalReferenceCode_createCatalog();

		putCatalog = catalogResource.putCatalogByExternalReferenceCode(
			newCatalog.getExternalReferenceCode(), newCatalog);

		assertEquals(newCatalog, putCatalog);
		assertValid(putCatalog);

		getCatalog = catalogResource.getCatalogByExternalReferenceCode(
			putCatalog.getExternalReferenceCode());

		assertEquals(newCatalog, getCatalog);

		Assert.assertEquals(
			newCatalog.getExternalReferenceCode(),
			putCatalog.getExternalReferenceCode());
	}

	protected Catalog testPutCatalogByExternalReferenceCode_addCatalog()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Catalog testPutCatalogByExternalReferenceCode_createCatalog()
		throws Exception {

		return randomCatalog();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Catalog catalog1 = testBatchEngineDeleteImportTask_addCatalog();

		testBatchEngineDeleteImportTask_deleteCatalog(
			200, catalog1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog1.getId()));

		catalog1 = testBatchEngineDeleteImportTask_addCatalog();

		testBatchEngineDeleteImportTask_deleteCatalog(
			200, null, catalog1.getId());

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog1.getId()));

		catalog1 = testBatchEngineDeleteImportTask_addCatalog();
		Catalog catalog2 = testBatchEngineDeleteImportTask_addCatalog();

		testBatchEngineDeleteImportTask_deleteCatalog(
			200, catalog2.getExternalReferenceCode(), catalog1.getId());

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog1.getId()));
		assertHttpResponseStatusCode(
			200, catalogResource.getCatalogHttpResponse(catalog2.getId()));

		testBatchEngineDeleteImportTask_deleteCatalog(
			200, catalog2.getExternalReferenceCode(), catalog1.getId());

		assertHttpResponseStatusCode(
			404, catalogResource.getCatalogHttpResponse(catalog2.getId()));
	}

	protected Catalog testBatchEngineDeleteImportTask_addCatalog()
		throws Exception {

		return testDeleteCatalog_addCatalog();
	}

	protected void testBatchEngineDeleteImportTask_deleteCatalog(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog",
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

	protected Catalog testGraphQLCatalog_addCatalog() throws Exception {
		return testGraphQLCatalog_addCatalog(randomCatalog());
	}

	protected Catalog testGraphQLCatalog_addCatalog(Catalog catalog)
		throws Exception {

		JSONDeserializer<Catalog> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Catalog.class)) {
			if (getGraphQLValue(field.get(catalog)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(catalog)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createCatalog",
						new HashMap<String, Object>() {
							{
								put("catalog", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createCatalog"),
			Catalog.class);
	}

	protected Catalog testGraphQLProductCatalog_addCatalog() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

	protected void assertContains(Catalog catalog, List<Catalog> catalogs) {
		boolean contains = false;

		for (Catalog item : catalogs) {
			if (equals(catalog, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(catalogs + " does not contain " + catalog, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Catalog catalog1, Catalog catalog2) {
		Assert.assertTrue(
			catalog1 + " does not equal " + catalog2,
			equals(catalog1, catalog2));
	}

	protected void assertEquals(
		List<Catalog> catalogs1, List<Catalog> catalogs2) {

		Assert.assertEquals(catalogs1.size(), catalogs2.size());

		for (int i = 0; i < catalogs1.size(); i++) {
			Catalog catalog1 = catalogs1.get(i);
			Catalog catalog2 = catalogs2.get(i);

			assertEquals(catalog1, catalog2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Catalog> catalogs1, List<Catalog> catalogs2) {

		Assert.assertEquals(catalogs1.size(), catalogs2.size());

		for (Catalog catalog1 : catalogs1) {
			boolean contains = false;

			for (Catalog catalog2 : catalogs2) {
				if (equals(catalog1, catalog2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				catalogs2 + " does not contain " + catalog1, contains);
		}
	}

	protected void assertValid(Catalog catalog) throws Exception {
		boolean valid = true;

		if (catalog.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (catalog.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (catalog.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (catalog.getCurrencyCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (catalog.getCurrencyExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (catalog.getCurrencyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (catalog.getDefaultLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (catalog.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (catalog.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (catalog.getSystem() == null) {
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

	protected void assertValid(Page<Catalog> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Catalog> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Catalog> catalogs = page.getItems();

		int size = catalogs.size();

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
						Catalog.class)) {

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

	protected boolean equals(Catalog catalog1, Catalog catalog2) {
		if (catalog1 == catalog2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						catalog1.getAccountId(), catalog2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)catalog1.getActions(),
						(Map)catalog2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						catalog1.getCurrencyCode(),
						catalog2.getCurrencyCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						catalog1.getCurrencyExternalReferenceCode(),
						catalog2.getCurrencyExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						catalog1.getCurrencyId(), catalog2.getCurrencyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						catalog1.getDefaultLanguageId(),
						catalog2.getDefaultLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						catalog1.getExternalReferenceCode(),
						catalog2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(catalog1.getId(), catalog2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						catalog1.getName(), catalog2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						catalog1.getSystem(), catalog2.getSystem())) {

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

		if (!(_catalogResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_catalogResource;

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
		EntityField entityField, String operator, Catalog catalog) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("currencyCode")) {
			Object object = catalog.getCurrencyCode();

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

		if (entityFieldName.equals("currencyExternalReferenceCode")) {
			Object object = catalog.getCurrencyExternalReferenceCode();

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

		if (entityFieldName.equals("currencyId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("defaultLanguageId")) {
			Object object = catalog.getDefaultLanguageId();

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
			Object object = catalog.getExternalReferenceCode();

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
			Object object = catalog.getName();

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

	protected Catalog randomCatalog() throws Exception {
		return new Catalog() {
			{
				accountId = RandomTestUtil.randomLong();
				currencyCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyId = RandomTestUtil.randomLong();
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

	protected Catalog randomIrrelevantCatalog() throws Exception {
		Catalog randomIrrelevantCatalog = randomCatalog();

		return randomIrrelevantCatalog;
	}

	protected Catalog randomPatchCatalog() throws Exception {
		return randomCatalog();
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

	protected CatalogResource catalogResource;
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
		LogFactoryUtil.getLog(BaseCatalogResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.catalog.resource.v1_0.
			CatalogResource _catalogResource;

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