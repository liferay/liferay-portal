/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationList;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductConfigurationListResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationListSerDes;
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
public abstract class BaseProductConfigurationListResourceTestCase {

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

		_productConfigurationListResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productConfigurationListResource =
			ProductConfigurationListResource.builder(
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

		ProductConfigurationList productConfigurationList1 =
			randomProductConfigurationList();

		String json = objectMapper.writeValueAsString(
			productConfigurationList1);

		ProductConfigurationList productConfigurationList2 =
			ProductConfigurationListSerDes.toDTO(json);

		Assert.assertTrue(
			equals(productConfigurationList1, productConfigurationList2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductConfigurationList productConfigurationList =
			randomProductConfigurationList();

		String json1 = objectMapper.writeValueAsString(
			productConfigurationList);
		String json2 = ProductConfigurationListSerDes.toJSON(
			productConfigurationList);

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

		ProductConfigurationList productConfigurationList =
			randomProductConfigurationList();

		productConfigurationList.setCatalogExternalReferenceCode(regex);
		productConfigurationList.setExternalReferenceCode(regex);
		productConfigurationList.setName(regex);

		String json = ProductConfigurationListSerDes.toJSON(
			productConfigurationList);

		Assert.assertFalse(json.contains(regex));

		productConfigurationList = ProductConfigurationListSerDes.toDTO(json);

		Assert.assertEquals(
			regex, productConfigurationList.getCatalogExternalReferenceCode());
		Assert.assertEquals(
			regex, productConfigurationList.getExternalReferenceCode());
		Assert.assertEquals(regex, productConfigurationList.getName());
	}

	@Test
	public void testDeleteProductConfigurationList() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationList productConfigurationList =
			testDeleteProductConfigurationList_addProductConfigurationList();

		assertHttpResponseStatusCode(
			204,
			productConfigurationListResource.
				deleteProductConfigurationListHttpResponse(
					productConfigurationList.getId()));

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList.getId()));
		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(0L));
	}

	protected ProductConfigurationList
			testDeleteProductConfigurationList_addProductConfigurationList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductConfigurationList() throws Exception {

		// No namespace

		ProductConfigurationList productConfigurationList1 =
			testGraphQLDeleteProductConfigurationList_addProductConfigurationList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfigurationList",
						new HashMap<String, Object>() {
							{
								put("id", productConfigurationList1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductConfigurationList"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productConfigurationList",
					new HashMap<String, Object>() {
						{
							put("id", productConfigurationList1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductConfigurationList productConfigurationList2 =
			testGraphQLDeleteProductConfigurationList_addProductConfigurationList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfigurationList",
							new HashMap<String, Object>() {
								{
									put(
										"id",
										productConfigurationList2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfigurationList"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productConfigurationList",
						new HashMap<String, Object>() {
							{
								put("id", productConfigurationList2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductConfigurationList
			testGraphQLDeleteProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return testGraphQLProductConfigurationList_addProductConfigurationList();
	}

	@Test
	public void testDeleteProductConfigurationListBatch() throws Exception {
		ProductConfigurationList productConfigurationList1 =
			testDeleteProductConfigurationListBatch_addProductConfigurationList();

		testDeleteProductConfigurationListBatch_deleteProductConfigurationList(
			202, productConfigurationList1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList1.getId()));

		productConfigurationList1 =
			testDeleteProductConfigurationListBatch_addProductConfigurationList();

		testDeleteProductConfigurationListBatch_deleteProductConfigurationList(
			202, null, productConfigurationList1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList1.getId()));

		productConfigurationList1 =
			testDeleteProductConfigurationListBatch_addProductConfigurationList();
		ProductConfigurationList productConfigurationList2 =
			testDeleteProductConfigurationListBatch_addProductConfigurationList();

		testDeleteProductConfigurationListBatch_deleteProductConfigurationList(
			202, productConfigurationList2.getExternalReferenceCode(),
			productConfigurationList1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList1.getId()));
		assertHttpResponseStatusCode(
			200,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList2.getId()));

		testDeleteProductConfigurationListBatch_deleteProductConfigurationList(
			202, productConfigurationList2.getExternalReferenceCode(),
			productConfigurationList1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList2.getId()));
	}

	protected ProductConfigurationList
			testDeleteProductConfigurationListBatch_addProductConfigurationList()
		throws Exception {

		return testDeleteProductConfigurationList_addProductConfigurationList();
	}

	protected void
			testDeleteProductConfigurationListBatch_deleteProductConfigurationList(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productConfigurationListResource.
				deleteProductConfigurationListBatchHttpResponse(
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
	public void testDeleteProductConfigurationListByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationList productConfigurationList =
			testDeleteProductConfigurationListByExternalReferenceCode_addProductConfigurationList();

		assertHttpResponseStatusCode(
			204,
			productConfigurationListResource.
				deleteProductConfigurationListByExternalReferenceCodeHttpResponse(
					productConfigurationList.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListByExternalReferenceCodeHttpResponse(
					productConfigurationList.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListByExternalReferenceCodeHttpResponse(
					"-"));
	}

	protected ProductConfigurationList
			testDeleteProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductConfigurationListByExternalReferenceCode()
		throws Exception {

		// No namespace

		ProductConfigurationList productConfigurationList1 =
			testGraphQLDeleteProductConfigurationListByExternalReferenceCode_addProductConfigurationList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfigurationListByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productConfigurationList1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteProductConfigurationListByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productConfigurationListByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									productConfigurationList1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductConfigurationList productConfigurationList2 =
			testGraphQLDeleteProductConfigurationListByExternalReferenceCode_addProductConfigurationList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfigurationListByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											productConfigurationList2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfigurationListByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productConfigurationListByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productConfigurationList2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductConfigurationList
			testGraphQLDeleteProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		return testGraphQLProductConfigurationList_addProductConfigurationList();
	}

	@Test
	public void testGetProductConfigurationList() throws Exception {
		ProductConfigurationList postProductConfigurationList =
			testGetProductConfigurationList_addProductConfigurationList();

		ProductConfigurationList getProductConfigurationList =
			productConfigurationListResource.getProductConfigurationList(
				postProductConfigurationList.getId());

		assertEquals(postProductConfigurationList, getProductConfigurationList);
		assertValid(getProductConfigurationList);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductConfigurationList postProductConfigurationList =
			testGetProductConfigurationList_addProductConfigurationList();

		ProductConfigurationList getProductConfigurationList =
			productConfigurationListResource.getProductConfigurationList(
				postProductConfigurationList.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationList"
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
			postProductConfigurationList.getId());

		assertEquals(
			getProductConfigurationList,
			ProductConfigurationListSerDes.toDTO(item.toString()));
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

	protected ProductConfigurationList
			testGetProductConfigurationList_addProductConfigurationList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductConfigurationList() throws Exception {
		ProductConfigurationList productConfigurationList =
			testGraphQLGetProductConfigurationList_addProductConfigurationList();

		// No namespace

		Assert.assertTrue(
			equals(
				productConfigurationList,
				ProductConfigurationListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productConfigurationList",
								new HashMap<String, Object>() {
									{
										put(
											"id",
											productConfigurationList.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productConfigurationList"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productConfigurationList,
				ProductConfigurationListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productConfigurationList",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												productConfigurationList.
													getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productConfigurationList"))));
	}

	@Test
	public void testGraphQLGetProductConfigurationListNotFound()
		throws Exception {

		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productConfigurationList",
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
							"productConfigurationList",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductConfigurationList
			testGraphQLGetProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return testGraphQLProductConfigurationList_addProductConfigurationList();
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCode()
		throws Exception {

		ProductConfigurationList postProductConfigurationList =
			testGetProductConfigurationListByExternalReferenceCode_addProductConfigurationList();

		ProductConfigurationList getProductConfigurationList =
			productConfigurationListResource.
				getProductConfigurationListByExternalReferenceCode(
					postProductConfigurationList.getExternalReferenceCode());

		assertEquals(postProductConfigurationList, getProductConfigurationList);
		assertValid(getProductConfigurationList);
	}

	protected ProductConfigurationList
			testGetProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductConfigurationListByExternalReferenceCode()
		throws Exception {

		ProductConfigurationList productConfigurationList =
			testGraphQLGetProductConfigurationListByExternalReferenceCode_addProductConfigurationList();

		// No namespace

		Assert.assertTrue(
			equals(
				productConfigurationList,
				ProductConfigurationListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productConfigurationListByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												productConfigurationList.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productConfigurationListByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productConfigurationList,
				ProductConfigurationListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productConfigurationListByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													productConfigurationList.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productConfigurationListByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetProductConfigurationListByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productConfigurationListByExternalReferenceCode",
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
							"productConfigurationListByExternalReferenceCode",
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

	protected ProductConfigurationList
			testGraphQLGetProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		return testGraphQLProductConfigurationList_addProductConfigurationList();
	}

	@Test
	public void testGetProductConfigurationListsPage() throws Exception {
		Page<ProductConfigurationList> page =
			productConfigurationListResource.getProductConfigurationListsPage(
				null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		ProductConfigurationList productConfigurationList1 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				randomProductConfigurationList());

		ProductConfigurationList productConfigurationList2 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				randomProductConfigurationList());

		page =
			productConfigurationListResource.getProductConfigurationListsPage(
				null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfigurationList1,
			(List<ProductConfigurationList>)page.getItems());
		assertContains(
			productConfigurationList2,
			(List<ProductConfigurationList>)page.getItems());
		assertValid(
			page, testGetProductConfigurationListsPage_getExpectedActions());

		productConfigurationListResource.deleteProductConfigurationList(
			productConfigurationList1.getId());

		productConfigurationListResource.deleteProductConfigurationList(
			productConfigurationList2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		ProductConfigurationList productConfigurationList1 =
			randomProductConfigurationList();

		productConfigurationList1 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				productConfigurationList1);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationList> page =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null,
						getFilterString(
							entityField, "between", productConfigurationList1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfigurationList1),
				(List<ProductConfigurationList>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListsPageWithFilterDoubleEquals()
		throws Exception {

		testGetProductConfigurationListsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductConfigurationListsPageWithFilterStringContains()
		throws Exception {

		testGetProductConfigurationListsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListsPageWithFilterStringEquals()
		throws Exception {

		testGetProductConfigurationListsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListsPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductConfigurationListsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetProductConfigurationListsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ProductConfigurationList productConfigurationList1 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				randomProductConfigurationList());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationList productConfigurationList2 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				randomProductConfigurationList());

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationList> page =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null,
						getFilterString(
							entityField, operator, productConfigurationList1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfigurationList1),
				(List<ProductConfigurationList>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListsPageWithPagination()
		throws Exception {

		Page<ProductConfigurationList> productConfigurationListsPage =
			productConfigurationListResource.getProductConfigurationListsPage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationListsPage.getTotalCount());

		ProductConfigurationList productConfigurationList1 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				randomProductConfigurationList());

		ProductConfigurationList productConfigurationList2 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				randomProductConfigurationList());

		ProductConfigurationList productConfigurationList3 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				randomProductConfigurationList());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfigurationList> page1 =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfigurationList1,
				(List<ProductConfigurationList>)page1.getItems());

			Page<ProductConfigurationList> page2 =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationList2,
				(List<ProductConfigurationList>)page2.getItems());

			Page<ProductConfigurationList> page3 =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationList3,
				(List<ProductConfigurationList>)page3.getItems());
		}
		else {
			Page<ProductConfigurationList> page1 =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null, Pagination.of(1, totalCount + 2),
						null);

			List<ProductConfigurationList> productConfigurationLists1 =
				(List<ProductConfigurationList>)page1.getItems();

			Assert.assertEquals(
				productConfigurationLists1.toString(), totalCount + 2,
				productConfigurationLists1.size());

			Page<ProductConfigurationList> page2 =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null, Pagination.of(2, totalCount + 2),
						null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfigurationList> productConfigurationLists2 =
				(List<ProductConfigurationList>)page2.getItems();

			Assert.assertEquals(
				productConfigurationLists2.toString(), 1,
				productConfigurationLists2.size());

			Page<ProductConfigurationList> page3 =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				productConfigurationList1,
				(List<ProductConfigurationList>)page3.getItems());
			assertContains(
				productConfigurationList2,
				(List<ProductConfigurationList>)page3.getItems());
			assertContains(
				productConfigurationList3,
				(List<ProductConfigurationList>)page3.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListsPageWithSortDateTime()
		throws Exception {

		testGetProductConfigurationListsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productConfigurationList1,
			 productConfigurationList2) -> {

				BeanTestUtil.setProperty(
					productConfigurationList1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductConfigurationListsPageWithSortDouble()
		throws Exception {

		testGetProductConfigurationListsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productConfigurationList1,
			 productConfigurationList2) -> {

				BeanTestUtil.setProperty(
					productConfigurationList1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productConfigurationList2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductConfigurationListsPageWithSortInteger()
		throws Exception {

		testGetProductConfigurationListsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productConfigurationList1,
			 productConfigurationList2) -> {

				BeanTestUtil.setProperty(
					productConfigurationList1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productConfigurationList2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductConfigurationListsPageWithSortString()
		throws Exception {

		testGetProductConfigurationListsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productConfigurationList1,
			 productConfigurationList2) -> {

				Class<?> clazz = productConfigurationList1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productConfigurationList1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productConfigurationList2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productConfigurationList1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productConfigurationList2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productConfigurationList1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productConfigurationList2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetProductConfigurationListsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ProductConfigurationList,
				 ProductConfigurationList, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ProductConfigurationList productConfigurationList1 =
			randomProductConfigurationList();
		ProductConfigurationList productConfigurationList2 =
			randomProductConfigurationList();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productConfigurationList1,
				productConfigurationList2);
		}

		productConfigurationList1 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				productConfigurationList1);

		productConfigurationList2 =
			testGetProductConfigurationListsPage_addProductConfigurationList(
				productConfigurationList2);

		Page<ProductConfigurationList> page =
			productConfigurationListResource.getProductConfigurationListsPage(
				null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationList> ascPage =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productConfigurationList1,
				(List<ProductConfigurationList>)ascPage.getItems());
			assertContains(
				productConfigurationList2,
				(List<ProductConfigurationList>)ascPage.getItems());

			Page<ProductConfigurationList> descPage =
				productConfigurationListResource.
					getProductConfigurationListsPage(
						null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productConfigurationList2,
				(List<ProductConfigurationList>)descPage.getItems());
			assertContains(
				productConfigurationList1,
				(List<ProductConfigurationList>)descPage.getItems());
		}
	}

	protected ProductConfigurationList
			testGetProductConfigurationListsPage_addProductConfigurationList(
				ProductConfigurationList productConfigurationList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductConfigurationListsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"productConfigurationLists",
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

		JSONObject productConfigurationListsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/productConfigurationLists");

		long totalCount = productConfigurationListsJSONObject.getLong(
			"totalCount");

		ProductConfigurationList productConfigurationList1 =
			testGraphQLProductConfigurationList_addProductConfigurationList(
				randomProductConfigurationList());

		ProductConfigurationList productConfigurationList2 =
			testGraphQLProductConfigurationList_addProductConfigurationList(
				randomProductConfigurationList());

		productConfigurationListsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/productConfigurationLists");

		Assert.assertEquals(
			totalCount + 2,
			productConfigurationListsJSONObject.getLong("totalCount"));

		assertContains(
			productConfigurationList1,
			Arrays.asList(
				ProductConfigurationListSerDes.toDTOs(
					productConfigurationListsJSONObject.getString("items"))));
		assertContains(
			productConfigurationList2,
			Arrays.asList(
				ProductConfigurationListSerDes.toDTOs(
					productConfigurationListsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		productConfigurationListsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/productConfigurationLists");

		Assert.assertEquals(
			totalCount + 2,
			productConfigurationListsJSONObject.getLong("totalCount"));

		assertContains(
			productConfigurationList1,
			Arrays.asList(
				ProductConfigurationListSerDes.toDTOs(
					productConfigurationListsJSONObject.getString("items"))));
		assertContains(
			productConfigurationList2,
			Arrays.asList(
				ProductConfigurationListSerDes.toDTOs(
					productConfigurationListsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchProductConfigurationList() throws Exception {
		ProductConfigurationList postProductConfigurationList =
			testPatchProductConfigurationList_addProductConfigurationList();

		ProductConfigurationList randomPatchProductConfigurationList =
			randomPatchProductConfigurationList();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationList patchProductConfigurationList =
			productConfigurationListResource.patchProductConfigurationList(
				postProductConfigurationList.getId(),
				randomPatchProductConfigurationList);

		ProductConfigurationList expectedPatchProductConfigurationList =
			postProductConfigurationList.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductConfigurationList,
			expectedPatchProductConfigurationList);

		ProductConfigurationList getProductConfigurationList =
			productConfigurationListResource.getProductConfigurationList(
				patchProductConfigurationList.getId());

		assertEquals(
			expectedPatchProductConfigurationList, getProductConfigurationList);
		assertValid(getProductConfigurationList);
	}

	protected ProductConfigurationList
			testPatchProductConfigurationList_addProductConfigurationList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchProductConfigurationListByExternalReferenceCode()
		throws Exception {

		ProductConfigurationList postProductConfigurationList =
			testPatchProductConfigurationListByExternalReferenceCode_addProductConfigurationList();

		ProductConfigurationList randomPatchProductConfigurationList =
			randomPatchProductConfigurationList();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationList patchProductConfigurationList =
			productConfigurationListResource.
				patchProductConfigurationListByExternalReferenceCode(
					postProductConfigurationList.getExternalReferenceCode(),
					randomPatchProductConfigurationList);

		ProductConfigurationList expectedPatchProductConfigurationList =
			postProductConfigurationList.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductConfigurationList,
			expectedPatchProductConfigurationList);

		ProductConfigurationList getProductConfigurationList =
			productConfigurationListResource.
				getProductConfigurationListByExternalReferenceCode(
					patchProductConfigurationList.getExternalReferenceCode());

		assertEquals(
			expectedPatchProductConfigurationList, getProductConfigurationList);
		assertValid(getProductConfigurationList);
	}

	protected ProductConfigurationList
			testPatchProductConfigurationListByExternalReferenceCode_addProductConfigurationList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductConfigurationList() throws Exception {
		ProductConfigurationList randomProductConfigurationList =
			randomProductConfigurationList();

		ProductConfigurationList postProductConfigurationList =
			testPostProductConfigurationList_addProductConfigurationList(
				randomProductConfigurationList);

		assertEquals(
			randomProductConfigurationList, postProductConfigurationList);
		assertValid(postProductConfigurationList);
	}

	protected ProductConfigurationList
			testPostProductConfigurationList_addProductConfigurationList(
				ProductConfigurationList productConfigurationList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostProductConfigurationList() throws Exception {
		ProductConfigurationList randomProductConfigurationList =
			randomProductConfigurationList();

		ProductConfigurationList productConfigurationList =
			testGraphQLProductConfigurationList_addProductConfigurationList(
				randomProductConfigurationList);

		Assert.assertTrue(
			equals(randomProductConfigurationList, productConfigurationList));
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductConfigurationList productConfigurationList1 =
			testBatchEngineDeleteImportTask_addProductConfigurationList();

		testBatchEngineDeleteImportTask_deleteProductConfigurationList(
			200, productConfigurationList1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList1.getId()));

		productConfigurationList1 =
			testBatchEngineDeleteImportTask_addProductConfigurationList();

		testBatchEngineDeleteImportTask_deleteProductConfigurationList(
			200, null, productConfigurationList1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList1.getId()));

		productConfigurationList1 =
			testBatchEngineDeleteImportTask_addProductConfigurationList();
		ProductConfigurationList productConfigurationList2 =
			testBatchEngineDeleteImportTask_addProductConfigurationList();

		testBatchEngineDeleteImportTask_deleteProductConfigurationList(
			200, productConfigurationList2.getExternalReferenceCode(),
			productConfigurationList1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList1.getId()));
		assertHttpResponseStatusCode(
			200,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList2.getId()));

		testBatchEngineDeleteImportTask_deleteProductConfigurationList(
			200, productConfigurationList2.getExternalReferenceCode(),
			productConfigurationList1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationListResource.
				getProductConfigurationListHttpResponse(
					productConfigurationList2.getId()));
	}

	protected ProductConfigurationList
			testBatchEngineDeleteImportTask_addProductConfigurationList()
		throws Exception {

		return testDeleteProductConfigurationList_addProductConfigurationList();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteProductConfigurationList(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationList",
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

	protected ProductConfigurationList
			testGraphQLProductConfigurationList_addProductConfigurationList()
		throws Exception {

		return testGraphQLProductConfigurationList_addProductConfigurationList(
			randomProductConfigurationList());
	}

	protected ProductConfigurationList
			testGraphQLProductConfigurationList_addProductConfigurationList(
				ProductConfigurationList productConfigurationList)
		throws Exception {

		JSONDeserializer<ProductConfigurationList> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ProductConfigurationList.class)) {

			if (getGraphQLValue(field.get(productConfigurationList)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(productConfigurationList)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createProductConfigurationList",
						new HashMap<String, Object>() {
							{
								put("productConfigurationList", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createProductConfigurationList"),
			ProductConfigurationList.class);
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
		ProductConfigurationList productConfigurationList,
		List<ProductConfigurationList> productConfigurationLists) {

		boolean contains = false;

		for (ProductConfigurationList item : productConfigurationLists) {
			if (equals(productConfigurationList, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productConfigurationLists + " does not contain " +
				productConfigurationList,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductConfigurationList productConfigurationList1,
		ProductConfigurationList productConfigurationList2) {

		Assert.assertTrue(
			productConfigurationList1 + " does not equal " +
				productConfigurationList2,
			equals(productConfigurationList1, productConfigurationList2));
	}

	protected void assertEquals(
		List<ProductConfigurationList> productConfigurationLists1,
		List<ProductConfigurationList> productConfigurationLists2) {

		Assert.assertEquals(
			productConfigurationLists1.size(),
			productConfigurationLists2.size());

		for (int i = 0; i < productConfigurationLists1.size(); i++) {
			ProductConfigurationList productConfigurationList1 =
				productConfigurationLists1.get(i);
			ProductConfigurationList productConfigurationList2 =
				productConfigurationLists2.get(i);

			assertEquals(productConfigurationList1, productConfigurationList2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductConfigurationList> productConfigurationLists1,
		List<ProductConfigurationList> productConfigurationLists2) {

		Assert.assertEquals(
			productConfigurationLists1.size(),
			productConfigurationLists2.size());

		for (ProductConfigurationList productConfigurationList1 :
				productConfigurationLists1) {

			boolean contains = false;

			for (ProductConfigurationList productConfigurationList2 :
					productConfigurationLists2) {

				if (equals(
						productConfigurationList1, productConfigurationList2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productConfigurationLists2 + " does not contain " +
					productConfigurationList1,
				contains);
		}
	}

	protected void assertValid(
			ProductConfigurationList productConfigurationList)
		throws Exception {

		boolean valid = true;

		if (productConfigurationList.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (productConfigurationList.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"catalogExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productConfigurationList.
						getCatalogExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (productConfigurationList.getCatalogId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (productConfigurationList.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (productConfigurationList.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (productConfigurationList.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (productConfigurationList.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (productConfigurationList.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("master", additionalAssertFieldName)) {
				if (productConfigurationList.getMaster() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (productConfigurationList.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (productConfigurationList.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentProductConfigurationListId",
					additionalAssertFieldName)) {

				if (productConfigurationList.
						getParentProductConfigurationListId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (productConfigurationList.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurations", additionalAssertFieldName)) {

				if (productConfigurationList.getProductConfigurations() ==
						null) {

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

	protected void assertValid(Page<ProductConfigurationList> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductConfigurationList> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductConfigurationList>
			productConfigurationLists = page.getItems();

		int size = productConfigurationLists.size();

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
						ProductConfigurationList.class)) {

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
		ProductConfigurationList productConfigurationList1,
		ProductConfigurationList productConfigurationList2) {

		if (productConfigurationList1 == productConfigurationList2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)productConfigurationList1.getActions(),
						(Map)productConfigurationList2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"catalogExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationList1.
							getCatalogExternalReferenceCode(),
						productConfigurationList2.
							getCatalogExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getCatalogId(),
						productConfigurationList2.getCatalogId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getCreateDate(),
						productConfigurationList2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getCustomFields(),
						productConfigurationList2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getDisplayDate(),
						productConfigurationList2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getExpirationDate(),
						productConfigurationList2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationList1.getExternalReferenceCode(),
						productConfigurationList2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getId(),
						productConfigurationList2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("master", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getMaster(),
						productConfigurationList2.getMaster())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getName(),
						productConfigurationList2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getNeverExpire(),
						productConfigurationList2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentProductConfigurationListId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationList1.
							getParentProductConfigurationListId(),
						productConfigurationList2.
							getParentProductConfigurationListId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationList1.getPriority(),
						productConfigurationList2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurations", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationList1.getProductConfigurations(),
						productConfigurationList2.getProductConfigurations())) {

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

		if (!(_productConfigurationListResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productConfigurationListResource;

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
		ProductConfigurationList productConfigurationList) {

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

		if (entityFieldName.equals("catalogExternalReferenceCode")) {
			Object object =
				productConfigurationList.getCatalogExternalReferenceCode();

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

		if (entityFieldName.equals("catalogId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = productConfigurationList.getCreateDate();

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
					_format.format(productConfigurationList.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = productConfigurationList.getDisplayDate();

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
					_format.format(productConfigurationList.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = productConfigurationList.getExpirationDate();

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
						productConfigurationList.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = productConfigurationList.getExternalReferenceCode();

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

		if (entityFieldName.equals("master")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = productConfigurationList.getName();

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

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentProductConfigurationListId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(productConfigurationList.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("productConfigurations")) {
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

	protected ProductConfigurationList randomProductConfigurationList()
		throws Exception {

		return new ProductConfigurationList() {
			{
				catalogExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				catalogId = RandomTestUtil.randomLong();
				createDate = RandomTestUtil.nextDate();
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				master = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				neverExpire = RandomTestUtil.randomBoolean();
				parentProductConfigurationListId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
			}
		};
	}

	protected ProductConfigurationList
			randomIrrelevantProductConfigurationList()
		throws Exception {

		ProductConfigurationList randomIrrelevantProductConfigurationList =
			randomProductConfigurationList();

		return randomIrrelevantProductConfigurationList;
	}

	protected ProductConfigurationList randomPatchProductConfigurationList()
		throws Exception {

		return randomProductConfigurationList();
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

	protected ProductConfigurationListResource productConfigurationListResource;
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
			BaseProductConfigurationListResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductConfigurationListResource _productConfigurationListResource;

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