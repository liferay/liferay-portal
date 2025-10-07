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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductSerDes;
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
public abstract class BaseProductResourceTestCase {

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

		_productResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productResource = ProductResource.builder(
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

		Product product1 = randomProduct();

		String json = objectMapper.writeValueAsString(product1);

		Product product2 = ProductSerDes.toDTO(json);

		Assert.assertTrue(equals(product1, product2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Product product = randomProduct();

		String json1 = objectMapper.writeValueAsString(product);
		String json2 = ProductSerDes.toJSON(product);

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

		Product product = randomProduct();

		product.setCatalogExternalReferenceCode(regex);
		product.setDefaultSku(regex);
		product.setExternalReferenceCode(regex);
		product.setProductType(regex);
		product.setProductTypeI18n(regex);
		product.setSkuFormatted(regex);
		product.setThumbnail(regex);

		String json = ProductSerDes.toJSON(product);

		Assert.assertFalse(json.contains(regex));

		product = ProductSerDes.toDTO(json);

		Assert.assertEquals(regex, product.getCatalogExternalReferenceCode());
		Assert.assertEquals(regex, product.getDefaultSku());
		Assert.assertEquals(regex, product.getExternalReferenceCode());
		Assert.assertEquals(regex, product.getProductType());
		Assert.assertEquals(regex, product.getProductTypeI18n());
		Assert.assertEquals(regex, product.getSkuFormatted());
		Assert.assertEquals(regex, product.getThumbnail());
	}

	@Test
	public void testDeleteProduct() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Product product = testDeleteProduct_addProduct();

		assertHttpResponseStatusCode(
			204, productResource.deleteProductHttpResponse(product.getId()));

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product.getId()));
		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(0L));
	}

	protected Product testDeleteProduct_addProduct() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProduct() throws Exception {

		// No namespace

		Product product1 = testGraphQLDeleteProduct_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProduct",
						new HashMap<String, Object>() {
							{
								put("id", product1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProduct"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"product",
					new HashMap<String, Object>() {
						{
							put("id", product1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Product product2 = testGraphQLDeleteProduct_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProduct",
							new HashMap<String, Object>() {
								{
									put("id", product2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProduct"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"product",
						new HashMap<String, Object>() {
							{
								put("id", product2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Product testGraphQLDeleteProduct_addProduct() throws Exception {
		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testDeleteProductBatch() throws Exception {
		Product product1 = testDeleteProductBatch_addProduct();

		testDeleteProductBatch_deleteProduct(
			202, product1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product1.getId()));

		product1 = testDeleteProductBatch_addProduct();

		testDeleteProductBatch_deleteProduct(202, null, product1.getId());

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product1.getId()));

		product1 = testDeleteProductBatch_addProduct();
		Product product2 = testDeleteProductBatch_addProduct();

		testDeleteProductBatch_deleteProduct(
			202, product2.getExternalReferenceCode(), product1.getId());

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product1.getId()));
		assertHttpResponseStatusCode(
			200, productResource.getProductHttpResponse(product2.getId()));

		testDeleteProductBatch_deleteProduct(
			202, product2.getExternalReferenceCode(), product1.getId());

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product2.getId()));
	}

	protected Product testDeleteProductBatch_addProduct() throws Exception {
		return testDeleteProduct_addProduct();
	}

	protected void testDeleteProductBatch_deleteProduct(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productResource.deleteProductBatchHttpResponse(
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
	public void testDeleteProductByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Product product = testDeleteProductByExternalReferenceCode_addProduct();

		assertHttpResponseStatusCode(
			204,
			productResource.deleteProductByExternalReferenceCodeHttpResponse(
				product.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			productResource.getProductByExternalReferenceCodeHttpResponse(
				product.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			productResource.getProductByExternalReferenceCodeHttpResponse("-"));
	}

	protected Product testDeleteProductByExternalReferenceCode_addProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductByExternalReferenceCode()
		throws Exception {

		// No namespace

		Product product1 =
			testGraphQLDeleteProductByExternalReferenceCode_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + product1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteProductByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + product1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Product product2 =
			testGraphQLDeleteProductByExternalReferenceCode_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											product2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + product2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Product
			testGraphQLDeleteProductByExternalReferenceCode_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testDeleteProductByExternalReferenceCodeByVersion()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Product product =
			testDeleteProductByExternalReferenceCodeByVersion_addProduct();

		assertHttpResponseStatusCode(
			204,
			productResource.
				deleteProductByExternalReferenceCodeByVersionHttpResponse(
					product.getExternalReferenceCode(), product.getVersion()));

		assertHttpResponseStatusCode(
			404,
			productResource.
				getProductByExternalReferenceCodeByVersionHttpResponse(
					product.getExternalReferenceCode(), product.getVersion()));
		assertHttpResponseStatusCode(
			404,
			productResource.
				getProductByExternalReferenceCodeByVersionHttpResponse(
					"-", product.getVersion()));
	}

	protected Product
			testDeleteProductByExternalReferenceCodeByVersion_addProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductByExternalReferenceCodeByVersion()
		throws Exception {

		// No namespace

		Product product1 =
			testGraphQLDeleteProductByExternalReferenceCodeByVersion_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductByExternalReferenceCodeByVersion",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + product1.getExternalReferenceCode() +
										"\"");
								put("version", product1.getVersion());
							}
						})),
				"JSONObject/data",
				"Object/deleteProductByExternalReferenceCodeByVersion"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productByExternalReferenceCodeByVersion",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + product1.getExternalReferenceCode() +
									"\"");
							put("version", product1.getVersion());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Product product2 =
			testGraphQLDeleteProductByExternalReferenceCodeByVersion_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductByExternalReferenceCodeByVersion",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											product2.
												getExternalReferenceCode() +
													"\"");
									put("version", product2.getVersion());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductByExternalReferenceCodeByVersion"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productByExternalReferenceCodeByVersion",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + product2.getExternalReferenceCode() +
										"\"");
								put("version", product2.getVersion());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Product
			testGraphQLDeleteProductByExternalReferenceCodeByVersion_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testDeleteProductByVersion() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Product product = testDeleteProductByVersion_addProduct();

		assertHttpResponseStatusCode(
			204,
			productResource.deleteProductByVersionHttpResponse(
				product.getId(), product.getVersion()));

		assertHttpResponseStatusCode(
			404,
			productResource.getProductByVersionHttpResponse(
				product.getId(), product.getVersion()));
		assertHttpResponseStatusCode(
			404,
			productResource.getProductByVersionHttpResponse(
				0L, product.getVersion()));
	}

	protected Product testDeleteProductByVersion_addProduct() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductByVersion() throws Exception {

		// No namespace

		Product product1 = testGraphQLDeleteProductByVersion_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductByVersion",
						new HashMap<String, Object>() {
							{
								put("id", product1.getId());
								put("version", product1.getVersion());
							}
						})),
				"JSONObject/data", "Object/deleteProductByVersion"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productByVersion",
					new HashMap<String, Object>() {
						{
							put("id", product1.getId());
							put("version", product1.getVersion());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Product product2 = testGraphQLDeleteProductByVersion_addProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductByVersion",
							new HashMap<String, Object>() {
								{
									put("id", product2.getId());
									put("version", product2.getVersion());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductByVersion"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productByVersion",
						new HashMap<String, Object>() {
							{
								put("id", product2.getId());
								put("version", product2.getVersion());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Product testGraphQLDeleteProductByVersion_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testGetProduct() throws Exception {
		Product postProduct = testGetProduct_addProduct();

		Product getProduct = productResource.getProduct(postProduct.getId());

		assertEquals(postProduct, getProduct);
		assertValid(getProduct);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Product postProduct = testGetProduct_addProduct();

		Product getProduct = productResource.getProduct(postProduct.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product"
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

		Object item = vulcanCRUDItemDelegate.getItem(postProduct.getId());

		assertEquals(getProduct, ProductSerDes.toDTO(item.toString()));
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

	protected Product testGetProduct_addProduct() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProduct() throws Exception {
		Product product = testGraphQLGetProduct_addProduct();

		// No namespace

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"product",
								new HashMap<String, Object>() {
									{
										put("id", product.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/product"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"product",
									new HashMap<String, Object>() {
										{
											put("id", product.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/product"))));
	}

	@Test
	public void testGraphQLGetProductNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"product",
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
							"product",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Product testGraphQLGetProduct_addProduct() throws Exception {
		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testGetProductByExternalReferenceCode() throws Exception {
		Product postProduct =
			testGetProductByExternalReferenceCode_addProduct();

		Product getProduct = productResource.getProductByExternalReferenceCode(
			postProduct.getExternalReferenceCode());

		assertEquals(postProduct, getProduct);
		assertValid(getProduct);
	}

	protected Product testGetProductByExternalReferenceCode_addProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCode()
		throws Exception {

		Product product =
			testGraphQLGetProductByExternalReferenceCode_addProduct();

		// No namespace

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												product.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													product.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productByExternalReferenceCode",
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
							"productByExternalReferenceCode",
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

	protected Product testGraphQLGetProductByExternalReferenceCode_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testGetProductByExternalReferenceCodeByVersion()
		throws Exception {

		Product postProduct =
			testGetProductByExternalReferenceCodeByVersion_addProduct();

		Product getProduct =
			productResource.getProductByExternalReferenceCodeByVersion(
				postProduct.getExternalReferenceCode(),
				postProduct.getVersion());

		assertEquals(postProduct, getProduct);
		assertValid(getProduct);
	}

	protected Product
			testGetProductByExternalReferenceCodeByVersion_addProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCodeByVersion()
		throws Exception {

		Product product =
			testGraphQLGetProductByExternalReferenceCodeByVersion_addProduct();

		// No namespace

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productByExternalReferenceCodeByVersion",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												product.
													getExternalReferenceCode() +
														"\"");
										put("version", product.getVersion());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productByExternalReferenceCodeByVersion"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productByExternalReferenceCodeByVersion",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													product.
														getExternalReferenceCode() +
															"\"");
											put(
												"version",
												product.getVersion());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productByExternalReferenceCodeByVersion"))));
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCodeByVersionNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		Integer irrelevantVersion = RandomTestUtil.randomInt();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productByExternalReferenceCodeByVersion",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
								put("version", irrelevantVersion);
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
							"productByExternalReferenceCodeByVersion",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
									put("version", irrelevantVersion);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Product
			testGraphQLGetProductByExternalReferenceCodeByVersion_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testGetProductByVersion() throws Exception {
		Product postProduct = testGetProductByVersion_addProduct();

		Product getProduct = productResource.getProductByVersion(
			postProduct.getId(), postProduct.getVersion());

		assertEquals(postProduct, getProduct);
		assertValid(getProduct);
	}

	protected Product testGetProductByVersion_addProduct() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductByVersion() throws Exception {
		Product product = testGraphQLGetProductByVersion_addProduct();

		// No namespace

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productByVersion",
								new HashMap<String, Object>() {
									{
										put("id", product.getId());
										put("version", product.getVersion());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productByVersion"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				product,
				ProductSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productByVersion",
									new HashMap<String, Object>() {
										{
											put("id", product.getId());
											put(
												"version",
												product.getVersion());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productByVersion"))));
	}

	@Test
	public void testGraphQLGetProductByVersionNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();
		Integer irrelevantVersion = RandomTestUtil.randomInt();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productByVersion",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
								put("version", irrelevantVersion);
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
							"productByVersion",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
									put("version", irrelevantVersion);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Product testGraphQLGetProductByVersion_addProduct()
		throws Exception {

		return testGraphQLProduct_addProduct();
	}

	@Test
	public void testGetProductsPage() throws Exception {
		Page<Product> page = productResource.getProductsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Product product1 = testGetProductsPage_addProduct(randomProduct());

		Product product2 = testGetProductsPage_addProduct(randomProduct());

		page = productResource.getProductsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(product1, (List<Product>)page.getItems());
		assertContains(product2, (List<Product>)page.getItems());
		assertValid(page, testGetProductsPage_getExpectedActions());

		productResource.deleteProduct(product1.getId());

		productResource.deleteProduct(product2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductsPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Product product1 = randomProduct();

		product1 = testGetProductsPage_addProduct(product1);

		for (EntityField entityField : entityFields) {
			Page<Product> page = productResource.getProductsPage(
				null, getFilterString(entityField, "between", product1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(product1),
				(List<Product>)page.getItems());
		}
	}

	@Test
	public void testGetProductsPageWithFilterDoubleEquals() throws Exception {
		testGetProductsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductsPageWithFilterStringContains() throws Exception {
		testGetProductsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductsPageWithFilterStringEquals() throws Exception {
		testGetProductsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductsPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetProductsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Product product1 = testGetProductsPage_addProduct(randomProduct());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Product product2 = testGetProductsPage_addProduct(randomProduct());

		for (EntityField entityField : entityFields) {
			Page<Product> page = productResource.getProductsPage(
				null, getFilterString(entityField, operator, product1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(product1),
				(List<Product>)page.getItems());
		}
	}

	@Test
	public void testGetProductsPageWithPagination() throws Exception {
		Page<Product> productsPage = productResource.getProductsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(productsPage.getTotalCount());

		Product product1 = testGetProductsPage_addProduct(randomProduct());

		Product product2 = testGetProductsPage_addProduct(randomProduct());

		Product product3 = testGetProductsPage_addProduct(randomProduct());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Product> page1 = productResource.getProductsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(product1, (List<Product>)page1.getItems());

			Page<Product> page2 = productResource.getProductsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(product2, (List<Product>)page2.getItems());

			Page<Product> page3 = productResource.getProductsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(product3, (List<Product>)page3.getItems());
		}
		else {
			Page<Product> page1 = productResource.getProductsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Product> products1 = (List<Product>)page1.getItems();

			Assert.assertEquals(
				products1.toString(), totalCount + 2, products1.size());

			Page<Product> page2 = productResource.getProductsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Product> products2 = (List<Product>)page2.getItems();

			Assert.assertEquals(products2.toString(), 1, products2.size());

			Page<Product> page3 = productResource.getProductsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(product1, (List<Product>)page3.getItems());
			assertContains(product2, (List<Product>)page3.getItems());
			assertContains(product3, (List<Product>)page3.getItems());
		}
	}

	@Test
	public void testGetProductsPageWithSortDateTime() throws Exception {
		testGetProductsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, product1, product2) -> {
				BeanTestUtil.setProperty(
					product1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductsPageWithSortDouble() throws Exception {
		testGetProductsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, product1, product2) -> {
				BeanTestUtil.setProperty(product1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(product2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductsPageWithSortInteger() throws Exception {
		testGetProductsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, product1, product2) -> {
				BeanTestUtil.setProperty(product1, entityField.getName(), 0);
				BeanTestUtil.setProperty(product2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductsPageWithSortString() throws Exception {
		testGetProductsPageWithSort(
			EntityField.Type.STRING,
			(entityField, product1, product2) -> {
				Class<?> clazz = product1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						product1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						product2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						product1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						product2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						product1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						product2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetProductsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Product, Product, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Product product1 = randomProduct();
		Product product2 = randomProduct();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, product1, product2);
		}

		product1 = testGetProductsPage_addProduct(product1);

		product2 = testGetProductsPage_addProduct(product2);

		Page<Product> page = productResource.getProductsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Product> ascPage = productResource.getProductsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(product1, (List<Product>)ascPage.getItems());
			assertContains(product2, (List<Product>)ascPage.getItems());

			Page<Product> descPage = productResource.getProductsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(product2, (List<Product>)descPage.getItems());
			assertContains(product1, (List<Product>)descPage.getItems());
		}
	}

	protected Product testGetProductsPage_addProduct(Product product)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"products",
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

		JSONObject productsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/products");

		long totalCount = productsJSONObject.getLong("totalCount");

		Product product1 = testGraphQLProduct_addProduct(randomProduct());

		Product product2 = testGraphQLProduct_addProduct(randomProduct());

		productsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/products");

		Assert.assertEquals(
			totalCount + 2, productsJSONObject.getLong("totalCount"));

		assertContains(
			product1,
			Arrays.asList(
				ProductSerDes.toDTOs(productsJSONObject.getString("items"))));
		assertContains(
			product2,
			Arrays.asList(
				ProductSerDes.toDTOs(productsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		productsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/products");

		Assert.assertEquals(
			totalCount + 2, productsJSONObject.getLong("totalCount"));

		assertContains(
			product1,
			Arrays.asList(
				ProductSerDes.toDTOs(productsJSONObject.getString("items"))));
		assertContains(
			product2,
			Arrays.asList(
				ProductSerDes.toDTOs(productsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchProduct() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchProductByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostProduct() throws Exception {
		Product randomProduct = randomProduct();

		Product postProduct = testPostProduct_addProduct(randomProduct);

		assertEquals(randomProduct, postProduct);
		assertValid(postProduct);
	}

	protected Product testPostProduct_addProduct(Product product)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostProduct() throws Exception {
		Product randomProduct = randomProduct();

		Product product = testGraphQLProduct_addProduct(randomProduct);

		Assert.assertTrue(equals(randomProduct, product));
	}

	@Test
	public void testPostProductByExternalReferenceCodeClone() throws Exception {
		Product randomProduct = randomProduct();

		Product postProduct =
			testPostProductByExternalReferenceCodeClone_addProduct(
				randomProduct);

		assertEquals(randomProduct, postProduct);
		assertValid(postProduct);
	}

	protected Product testPostProductByExternalReferenceCodeClone_addProduct(
			Product product)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostProductByExternalReferenceCodeClone()
		throws Exception {

		Product randomProduct = randomProduct();

		Product product = testGraphQLProduct_addProduct(randomProduct);

		Assert.assertTrue(equals(randomProduct, product));
	}

	@Test
	public void testPostProductClone() throws Exception {
		Product randomProduct = randomProduct();

		Product postProduct = testPostProductClone_addProduct(randomProduct);

		assertEquals(randomProduct, postProduct);
		assertValid(postProduct);
	}

	protected Product testPostProductClone_addProduct(Product product)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostProductClone() throws Exception {
		Product randomProduct = randomProduct();

		Product product = testGraphQLProduct_addProduct(randomProduct);

		Assert.assertTrue(equals(randomProduct, product));
	}

	@Test
	public void testPutProductByExternalReferenceCode() throws Exception {
		Product postProduct =
			testPutProductByExternalReferenceCode_addProduct();

		Product randomProduct = randomProduct();

		Product putProduct = productResource.putProductByExternalReferenceCode(
			postProduct.getExternalReferenceCode(), randomProduct);

		assertEquals(randomProduct, putProduct);
		assertValid(putProduct);

		Product getProduct = productResource.getProductByExternalReferenceCode(
			putProduct.getExternalReferenceCode());

		assertEquals(randomProduct, getProduct);
		assertValid(getProduct);

		Product newProduct =
			testPutProductByExternalReferenceCode_createProduct();

		putProduct = productResource.putProductByExternalReferenceCode(
			newProduct.getExternalReferenceCode(), newProduct);

		assertEquals(newProduct, putProduct);
		assertValid(putProduct);

		getProduct = productResource.getProductByExternalReferenceCode(
			putProduct.getExternalReferenceCode());

		assertEquals(newProduct, getProduct);

		Assert.assertEquals(
			newProduct.getExternalReferenceCode(),
			putProduct.getExternalReferenceCode());
	}

	protected Product testPutProductByExternalReferenceCode_addProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Product testPutProductByExternalReferenceCode_createProduct()
		throws Exception {

		return randomProduct();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Product product1 = testBatchEngineDeleteImportTask_addProduct();

		testBatchEngineDeleteImportTask_deleteProduct(
			200, product1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product1.getId()));

		product1 = testBatchEngineDeleteImportTask_addProduct();

		testBatchEngineDeleteImportTask_deleteProduct(
			200, null, product1.getId());

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product1.getId()));

		product1 = testBatchEngineDeleteImportTask_addProduct();
		Product product2 = testBatchEngineDeleteImportTask_addProduct();

		testBatchEngineDeleteImportTask_deleteProduct(
			200, product2.getExternalReferenceCode(), product1.getId());

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product1.getId()));
		assertHttpResponseStatusCode(
			200, productResource.getProductHttpResponse(product2.getId()));

		testBatchEngineDeleteImportTask_deleteProduct(
			200, product2.getExternalReferenceCode(), product1.getId());

		assertHttpResponseStatusCode(
			404, productResource.getProductHttpResponse(product2.getId()));
	}

	protected Product testBatchEngineDeleteImportTask_addProduct()
		throws Exception {

		return testDeleteProduct_addProduct();
	}

	protected void testBatchEngineDeleteImportTask_deleteProduct(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product",
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

	protected Product testGraphQLProduct_addProduct() throws Exception {
		return testGraphQLProduct_addProduct(randomProduct());
	}

	protected Product testGraphQLProduct_addProduct(Product product)
		throws Exception {

		JSONDeserializer<Product> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Product.class)) {
			if (getGraphQLValue(field.get(product)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(product)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createProduct",
						new HashMap<String, Object>() {
							{
								put("product", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createProduct"),
			Product.class);
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

	protected void assertContains(Product product, List<Product> products) {
		boolean contains = false;

		for (Product item : products) {
			if (equals(product, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(products + " does not contain " + product, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Product product1, Product product2) {
		Assert.assertTrue(
			product1 + " does not equal " + product2,
			equals(product1, product2));
	}

	protected void assertEquals(
		List<Product> products1, List<Product> products2) {

		Assert.assertEquals(products1.size(), products2.size());

		for (int i = 0; i < products1.size(); i++) {
			Product product1 = products1.get(i);
			Product product2 = products2.get(i);

			assertEquals(product1, product2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Product> products1, List<Product> products2) {

		Assert.assertEquals(products1.size(), products2.size());

		for (Product product1 : products1) {
			boolean contains = false;

			for (Product product2 : products2) {
				if (equals(product1, product2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				products2 + " does not contain " + product1, contains);
		}
	}

	protected void assertValid(Product product) throws Exception {
		boolean valid = true;

		if (product.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (product.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (product.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (product.getAttachments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("catalog", additionalAssertFieldName)) {
				if (product.getCatalog() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"catalogExternalReferenceCode",
					additionalAssertFieldName)) {

				if (product.getCatalogExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (product.getCatalogId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("categories", additionalAssertFieldName)) {
				if (product.getCategories() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (product.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (product.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("defaultSku", additionalAssertFieldName)) {
				if (product.getDefaultSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (product.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("diagram", additionalAssertFieldName)) {
				if (product.getDiagram() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (product.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expando", additionalAssertFieldName)) {
				if (product.getExpando() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (product.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (product.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("images", additionalAssertFieldName)) {
				if (product.getImages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("linkedProducts", additionalAssertFieldName)) {
				if (product.getLinkedProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("mappedProducts", additionalAssertFieldName)) {
				if (product.getMappedProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metaDescription", additionalAssertFieldName)) {
				if (product.getMetaDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metaKeyword", additionalAssertFieldName)) {
				if (product.getMetaKeyword() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metaTitle", additionalAssertFieldName)) {
				if (product.getMetaTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (product.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (product.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (product.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pins", additionalAssertFieldName)) {
				if (product.getPins() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productAccountGroupFilter", additionalAssertFieldName)) {

				if (product.getProductAccountGroupFilter() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productAccountGroups", additionalAssertFieldName)) {

				if (product.getProductAccountGroups() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productChannelFilter", additionalAssertFieldName)) {

				if (product.getProductChannelFilter() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productChannels", additionalAssertFieldName)) {
				if (product.getProductChannels() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfiguration", additionalAssertFieldName)) {

				if (product.getProductConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (product.getProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productOptions", additionalAssertFieldName)) {
				if (product.getProductOptions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productSpecifications", additionalAssertFieldName)) {

				if (product.getProductSpecifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productStatus", additionalAssertFieldName)) {
				if (product.getProductStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productType", additionalAssertFieldName)) {
				if (product.getProductType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productTypeI18n", additionalAssertFieldName)) {
				if (product.getProductTypeI18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productVirtualSettings", additionalAssertFieldName)) {

				if (product.getProductVirtualSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedProducts", additionalAssertFieldName)) {
				if (product.getRelatedProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingConfiguration", additionalAssertFieldName)) {

				if (product.getShippingConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shortDescription", additionalAssertFieldName)) {
				if (product.getShortDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuFormatted", additionalAssertFieldName)) {
				if (product.getSkuFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skus", additionalAssertFieldName)) {
				if (product.getSkus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subscriptionConfiguration", additionalAssertFieldName)) {

				if (product.getSubscriptionConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("tags", additionalAssertFieldName)) {
				if (product.getTags() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("taxConfiguration", additionalAssertFieldName)) {
				if (product.getTaxConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (product.getThumbnail() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("urls", additionalAssertFieldName)) {
				if (product.getUrls() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (product.getVersion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (product.getWorkflowStatusInfo() == null) {
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

	protected void assertValid(Page<Product> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Product> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Product> products = page.getItems();

		int size = products.size();

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
						Product.class)) {

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

	protected boolean equals(Product product1, Product product2) {
		if (product1 == product2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getActions(),
						(Map)product2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getActive(), product2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getAttachments(), product2.getAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("catalog", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCatalog(), product2.getCatalog())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"catalogExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getCatalogExternalReferenceCode(),
						product2.getCatalogExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCatalogId(), product2.getCatalogId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("categories", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCategories(), product2.getCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCreateDate(), product2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getCustomFields(),
						product2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("defaultSku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getDefaultSku(), product2.getDefaultSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getDescription(),
						(Map)product2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("diagram", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getDiagram(), product2.getDiagram())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getDisplayDate(), product2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expando", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getExpando(),
						(Map)product2.getExpando())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getExpirationDate(),
						product2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getExternalReferenceCode(),
						product2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(product1.getId(), product2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("images", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getImages(), product2.getImages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("linkedProducts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getLinkedProducts(),
						product2.getLinkedProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("mappedProducts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getMappedProducts(),
						product2.getMappedProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metaDescription", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getMetaDescription(),
						(Map)product2.getMetaDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metaKeyword", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getMetaKeyword(),
						(Map)product2.getMetaKeyword())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metaTitle", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getMetaTitle(),
						(Map)product2.getMetaTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getModifiedDate(),
						product2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals((Map)product1.getName(), (Map)product2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getNeverExpire(), product2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pins", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getPins(), product2.getPins())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productAccountGroupFilter", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductAccountGroupFilter(),
						product2.getProductAccountGroupFilter())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productAccountGroups", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductAccountGroups(),
						product2.getProductAccountGroups())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productChannelFilter", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductChannelFilter(),
						product2.getProductChannelFilter())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productChannels", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductChannels(),
						product2.getProductChannels())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfiguration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductConfiguration(),
						product2.getProductConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductId(), product2.getProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productOptions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductOptions(),
						product2.getProductOptions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productSpecifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductSpecifications(),
						product2.getProductSpecifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductStatus(),
						product2.getProductStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductType(), product2.getProductType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productTypeI18n", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getProductTypeI18n(),
						product2.getProductTypeI18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productVirtualSettings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getProductVirtualSettings(),
						product2.getProductVirtualSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedProducts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getRelatedProducts(),
						product2.getRelatedProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingConfiguration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getShippingConfiguration(),
						product2.getShippingConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shortDescription", additionalAssertFieldName)) {
				if (!equals(
						(Map)product1.getShortDescription(),
						(Map)product2.getShortDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuFormatted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getSkuFormatted(),
						product2.getSkuFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getSkus(), product2.getSkus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subscriptionConfiguration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getSubscriptionConfiguration(),
						product2.getSubscriptionConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("tags", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getTags(), product2.getTags())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("taxConfiguration", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getTaxConfiguration(),
						product2.getTaxConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getThumbnail(), product2.getThumbnail())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("urls", additionalAssertFieldName)) {
				if (!equals((Map)product1.getUrls(), (Map)product2.getUrls())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						product1.getVersion(), product2.getVersion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						product1.getWorkflowStatusInfo(),
						product2.getWorkflowStatusInfo())) {

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

		if (!(_productResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productResource;

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
		EntityField entityField, String operator, Product product) {

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

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("attachments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("catalog")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("catalogExternalReferenceCode")) {
			Object object = product.getCatalogExternalReferenceCode();

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

		if (entityFieldName.equals("categories")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = product.getCreateDate();

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

				sb.append(_format.format(product.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("defaultSku")) {
			Object object = product.getDefaultSku();

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

		if (entityFieldName.equals("diagram")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = product.getDisplayDate();

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

				sb.append(_format.format(product.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expando")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = product.getExpirationDate();

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

				sb.append(_format.format(product.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = product.getExternalReferenceCode();

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

		if (entityFieldName.equals("images")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("linkedProducts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("mappedProducts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("metaDescription")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("metaKeyword")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("metaTitle")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = product.getModifiedDate();

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

				sb.append(_format.format(product.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pins")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productAccountGroupFilter")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productAccountGroups")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productChannelFilter")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productChannels")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productOptions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productSpecifications")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productStatus")) {
			sb.append(String.valueOf(product.getProductStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("productType")) {
			Object object = product.getProductType();

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

		if (entityFieldName.equals("productTypeI18n")) {
			Object object = product.getProductTypeI18n();

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

		if (entityFieldName.equals("productVirtualSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("relatedProducts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shortDescription")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuFormatted")) {
			Object object = product.getSkuFormatted();

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

		if (entityFieldName.equals("skus")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscriptionConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("tags")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("thumbnail")) {
			Object object = product.getThumbnail();

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

		if (entityFieldName.equals("urls")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("version")) {
			sb.append(String.valueOf(product.getVersion()));

			return sb.toString();
		}

		if (entityFieldName.equals("workflowStatusInfo")) {
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

	protected Product randomProduct() throws Exception {
		return new Product() {
			{
				active = RandomTestUtil.randomBoolean();
				catalogExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				catalogId = RandomTestUtil.randomLong();
				createDate = RandomTestUtil.nextDate();
				defaultSku = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				neverExpire = RandomTestUtil.randomBoolean();
				productAccountGroupFilter = RandomTestUtil.randomBoolean();
				productChannelFilter = RandomTestUtil.randomBoolean();
				productId = RandomTestUtil.randomLong();
				productStatus = RandomTestUtil.randomInt();
				productType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productTypeI18n = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				skuFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				thumbnail = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				version = RandomTestUtil.randomInt();
			}
		};
	}

	protected Product randomIrrelevantProduct() throws Exception {
		Product randomIrrelevantProduct = randomProduct();

		return randomIrrelevantProduct;
	}

	protected Product randomPatchProduct() throws Exception {
		return randomProduct();
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

	protected ProductResource productResource;
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
		LogFactoryUtil.getLog(BaseProductResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.catalog.resource.v1_0.
			ProductResource _productResource;

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