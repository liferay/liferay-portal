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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductConfigurationResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationSerDes;
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
public abstract class BaseProductConfigurationResourceTestCase {

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

		_productConfigurationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productConfigurationResource = ProductConfigurationResource.builder(
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

		ProductConfiguration productConfiguration1 =
			randomProductConfiguration();

		String json = objectMapper.writeValueAsString(productConfiguration1);

		ProductConfiguration productConfiguration2 =
			ProductConfigurationSerDes.toDTO(json);

		Assert.assertTrue(equals(productConfiguration1, productConfiguration2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductConfiguration productConfiguration =
			randomProductConfiguration();

		String json1 = objectMapper.writeValueAsString(productConfiguration);
		String json2 = ProductConfigurationSerDes.toJSON(productConfiguration);

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

		ProductConfiguration productConfiguration =
			randomProductConfiguration();

		productConfiguration.setEntityExternalReferenceCode(regex);
		productConfiguration.setEntityName(regex);
		productConfiguration.setExternalReferenceCode(regex);
		productConfiguration.setInventoryEngine(regex);
		productConfiguration.setLowStockAction(regex);

		String json = ProductConfigurationSerDes.toJSON(productConfiguration);

		Assert.assertFalse(json.contains(regex));

		productConfiguration = ProductConfigurationSerDes.toDTO(json);

		Assert.assertEquals(
			regex, productConfiguration.getEntityExternalReferenceCode());
		Assert.assertEquals(regex, productConfiguration.getEntityName());
		Assert.assertEquals(
			regex, productConfiguration.getExternalReferenceCode());
		Assert.assertEquals(regex, productConfiguration.getInventoryEngine());
		Assert.assertEquals(regex, productConfiguration.getLowStockAction());
	}

	@Test
	public void testDeleteProductConfiguration() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfiguration productConfiguration =
			testDeleteProductConfiguration_addProductConfiguration();

		assertHttpResponseStatusCode(
			204,
			productConfigurationResource.deleteProductConfigurationHttpResponse(
				productConfiguration.getId()));

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration.getId()));
		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				0L));
	}

	protected ProductConfiguration
			testDeleteProductConfiguration_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductConfiguration() throws Exception {

		// No namespace

		ProductConfiguration productConfiguration1 =
			testGraphQLDeleteProductConfiguration_addProductConfiguration();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfiguration",
						new HashMap<String, Object>() {
							{
								put("id", productConfiguration1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductConfiguration"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productConfiguration",
					new HashMap<String, Object>() {
						{
							put("id", productConfiguration1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductConfiguration productConfiguration2 =
			testGraphQLDeleteProductConfiguration_addProductConfiguration();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfiguration",
							new HashMap<String, Object>() {
								{
									put("id", productConfiguration2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfiguration"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productConfiguration",
						new HashMap<String, Object>() {
							{
								put("id", productConfiguration2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductConfiguration
			testGraphQLDeleteProductConfiguration_addProductConfiguration()
		throws Exception {

		return testGraphQLProductConfiguration_addProductConfiguration();
	}

	@Test
	public void testDeleteProductConfigurationBatch() throws Exception {
		ProductConfiguration productConfiguration1 =
			testDeleteProductConfigurationBatch_addProductConfiguration();

		testDeleteProductConfigurationBatch_deleteProductConfiguration(
			202, productConfiguration1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration1.getId()));

		productConfiguration1 =
			testDeleteProductConfigurationBatch_addProductConfiguration();

		testDeleteProductConfigurationBatch_deleteProductConfiguration(
			202, null, productConfiguration1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration1.getId()));

		productConfiguration1 =
			testDeleteProductConfigurationBatch_addProductConfiguration();
		ProductConfiguration productConfiguration2 =
			testDeleteProductConfigurationBatch_addProductConfiguration();

		testDeleteProductConfigurationBatch_deleteProductConfiguration(
			202, productConfiguration2.getExternalReferenceCode(),
			productConfiguration1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration1.getId()));
		assertHttpResponseStatusCode(
			200,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration2.getId()));

		testDeleteProductConfigurationBatch_deleteProductConfiguration(
			202, productConfiguration2.getExternalReferenceCode(),
			productConfiguration1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration2.getId()));
	}

	protected ProductConfiguration
			testDeleteProductConfigurationBatch_addProductConfiguration()
		throws Exception {

		return testDeleteProductConfiguration_addProductConfiguration();
	}

	protected void
			testDeleteProductConfigurationBatch_deleteProductConfiguration(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productConfigurationResource.
				deleteProductConfigurationBatchHttpResponse(
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
	public void testDeleteProductConfigurationByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfiguration productConfiguration =
			testDeleteProductConfigurationByExternalReferenceCode_addProductConfiguration();

		assertHttpResponseStatusCode(
			204,
			productConfigurationResource.
				deleteProductConfigurationByExternalReferenceCodeHttpResponse(
					productConfiguration.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.
				getProductConfigurationByExternalReferenceCodeHttpResponse(
					productConfiguration.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.
				getProductConfigurationByExternalReferenceCodeHttpResponse(
					"-"));
	}

	protected ProductConfiguration
			testDeleteProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductConfigurationByExternalReferenceCode()
		throws Exception {

		// No namespace

		ProductConfiguration productConfiguration1 =
			testGraphQLDeleteProductConfigurationByExternalReferenceCode_addProductConfiguration();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfigurationByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productConfiguration1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteProductConfigurationByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productConfigurationByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									productConfiguration1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductConfiguration productConfiguration2 =
			testGraphQLDeleteProductConfigurationByExternalReferenceCode_addProductConfiguration();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfigurationByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											productConfiguration2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfigurationByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productConfigurationByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productConfiguration2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductConfiguration
			testGraphQLDeleteProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		return testGraphQLProductConfiguration_addProductConfiguration();
	}

	@Test
	public void testGetProductByExternalReferenceCodeConfiguration()
		throws Exception {

		ProductConfiguration postProductConfiguration =
			testGetProductByExternalReferenceCodeConfiguration_addProductConfiguration();

		ProductConfiguration getProductConfiguration =
			productConfigurationResource.
				getProductByExternalReferenceCodeConfiguration(
					testGetProductByExternalReferenceCodeConfiguration_getExternalReferenceCode(
						postProductConfiguration));

		assertEquals(postProductConfiguration, getProductConfiguration);
		assertValid(getProductConfiguration);
	}

	protected ProductConfiguration
			testGetProductByExternalReferenceCodeConfiguration_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeConfiguration_getExternalReferenceCode(
				ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCodeConfiguration()
		throws Exception {

		ProductConfiguration productConfiguration =
			testGraphQLGetProductByExternalReferenceCodeConfiguration_addProductConfiguration();

		// No namespace

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productByExternalReferenceCodeConfiguration",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetProductByExternalReferenceCodeConfiguration_getExternalReferenceCode(
													productConfiguration) +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productByExternalReferenceCodeConfiguration"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productByExternalReferenceCodeConfiguration",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetProductByExternalReferenceCodeConfiguration_getExternalReferenceCode(
														productConfiguration) +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productByExternalReferenceCodeConfiguration"))));
	}

	protected String
			testGraphQLGetProductByExternalReferenceCodeConfiguration_getExternalReferenceCode(
				ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductByExternalReferenceCodeConfigurationNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productByExternalReferenceCodeConfiguration",
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
							"productByExternalReferenceCodeConfiguration",
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

	protected ProductConfiguration
			testGraphQLGetProductByExternalReferenceCodeConfiguration_addProductConfiguration()
		throws Exception {

		return testGraphQLProductProductConfiguration_addProductConfiguration();
	}

	@Test
	public void testGetProductConfiguration() throws Exception {
		ProductConfiguration postProductConfiguration =
			testGetProductConfiguration_addProductConfiguration();

		ProductConfiguration getProductConfiguration =
			productConfigurationResource.getProductConfiguration(
				postProductConfiguration.getId());

		assertEquals(postProductConfiguration, getProductConfiguration);
		assertValid(getProductConfiguration);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductConfiguration postProductConfiguration =
			testGetProductConfiguration_addProductConfiguration();

		ProductConfiguration getProductConfiguration =
			productConfigurationResource.getProductConfiguration(
				postProductConfiguration.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration"
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
			postProductConfiguration.getId());

		assertEquals(
			getProductConfiguration,
			ProductConfigurationSerDes.toDTO(item.toString()));
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

	protected ProductConfiguration
			testGetProductConfiguration_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductConfiguration() throws Exception {
		ProductConfiguration productConfiguration =
			testGraphQLGetProductConfiguration_addProductConfiguration();

		// No namespace

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productConfiguration",
								new HashMap<String, Object>() {
									{
										put("id", productConfiguration.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productConfiguration"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productConfiguration",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												productConfiguration.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productConfiguration"))));
	}

	@Test
	public void testGraphQLGetProductConfigurationNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productConfiguration",
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
							"productConfiguration",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductConfiguration
			testGraphQLGetProductConfiguration_addProductConfiguration()
		throws Exception {

		return testGraphQLProductConfiguration_addProductConfiguration();
	}

	@Test
	public void testGetProductConfigurationByExternalReferenceCode()
		throws Exception {

		ProductConfiguration postProductConfiguration =
			testGetProductConfigurationByExternalReferenceCode_addProductConfiguration();

		ProductConfiguration getProductConfiguration =
			productConfigurationResource.
				getProductConfigurationByExternalReferenceCode(
					postProductConfiguration.getExternalReferenceCode());

		assertEquals(postProductConfiguration, getProductConfiguration);
		assertValid(getProductConfiguration);
	}

	protected ProductConfiguration
			testGetProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductConfigurationByExternalReferenceCode()
		throws Exception {

		ProductConfiguration productConfiguration =
			testGraphQLGetProductConfigurationByExternalReferenceCode_addProductConfiguration();

		// No namespace

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productConfigurationByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												productConfiguration.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productConfigurationByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productConfigurationByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													productConfiguration.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productConfigurationByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetProductConfigurationByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productConfigurationByExternalReferenceCode",
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
							"productConfigurationByExternalReferenceCode",
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

	protected ProductConfiguration
			testGraphQLGetProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		return testGraphQLProductConfiguration_addProductConfiguration();
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getIrrelevantExternalReferenceCode();

		Page<ProductConfiguration> page =
			productConfigurationResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductConfiguration irrelevantProductConfiguration =
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
					irrelevantExternalReferenceCode,
					randomIrrelevantProductConfiguration());

			page =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						irrelevantExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfiguration,
				(List<ProductConfiguration>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductConfiguration productConfiguration1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, randomProductConfiguration());

		ProductConfiguration productConfiguration2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, randomProductConfiguration());

		page =
			productConfigurationResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfiguration1, (List<ProductConfiguration>)page.getItems());
		assertContains(
			productConfiguration2, (List<ProductConfiguration>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExpectedActions(
				externalReferenceCode));

		productConfigurationResource.deleteProductConfiguration(
			productConfiguration1.getId());

		productConfigurationResource.deleteProductConfiguration(
			productConfiguration2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExternalReferenceCode();

		ProductConfiguration productConfiguration1 =
			randomProductConfiguration();

		productConfiguration1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, productConfiguration1);

		for (EntityField entityField : entityFields) {
			Page<ProductConfiguration> page =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null,
						getFilterString(
							entityField, "between", productConfiguration1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfiguration1),
				(List<ProductConfiguration>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilterDoubleEquals()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilterStringContains()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilterStringEquals()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExternalReferenceCode();

		ProductConfiguration productConfiguration1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, randomProductConfiguration());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfiguration productConfiguration2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, randomProductConfiguration());

		for (EntityField entityField : entityFields) {
			Page<ProductConfiguration> page =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null,
						getFilterString(
							entityField, operator, productConfiguration1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfiguration1),
				(List<ProductConfiguration>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExternalReferenceCode();

		Page<ProductConfiguration> productConfigurationsPage =
			productConfigurationResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
					externalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationsPage.getTotalCount());

		ProductConfiguration productConfiguration1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, randomProductConfiguration());

		ProductConfiguration productConfiguration2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, randomProductConfiguration());

		ProductConfiguration productConfiguration3 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, randomProductConfiguration());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfiguration> page1 =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)page1.getItems());

			Page<ProductConfiguration> page2 =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)page2.getItems());

			Page<ProductConfiguration> page3 =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfiguration3,
				(List<ProductConfiguration>)page3.getItems());
		}
		else {
			Page<ProductConfiguration> page1 =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<ProductConfiguration> productConfigurations1 =
				(List<ProductConfiguration>)page1.getItems();

			Assert.assertEquals(
				productConfigurations1.toString(), totalCount + 2,
				productConfigurations1.size());

			Page<ProductConfiguration> page2 =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfiguration> productConfigurations2 =
				(List<ProductConfiguration>)page2.getItems();

			Assert.assertEquals(
				productConfigurations2.toString(), 1,
				productConfigurations2.size());

			Page<ProductConfiguration> page3 =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)page3.getItems());
			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)page3.getItems());
			assertContains(
				productConfiguration3,
				(List<ProductConfiguration>)page3.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSortDateTime()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productConfiguration1, productConfiguration2) -> {
				BeanTestUtil.setProperty(
					productConfiguration1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSortDouble()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productConfiguration1, productConfiguration2) -> {
				BeanTestUtil.setProperty(
					productConfiguration1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productConfiguration2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSortInteger()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productConfiguration1, productConfiguration2) -> {
				BeanTestUtil.setProperty(
					productConfiguration1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productConfiguration2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSortString()
		throws Exception {

		testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productConfiguration1, productConfiguration2) -> {
				Class<?> clazz = productConfiguration1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productConfiguration1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productConfiguration2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productConfiguration1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productConfiguration2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productConfiguration1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productConfiguration2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ProductConfiguration, ProductConfiguration,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExternalReferenceCode();

		ProductConfiguration productConfiguration1 =
			randomProductConfiguration();
		ProductConfiguration productConfiguration2 =
			randomProductConfiguration();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productConfiguration1, productConfiguration2);
		}

		productConfiguration1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, productConfiguration1);

		productConfiguration2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				externalReferenceCode, productConfiguration2);

		Page<ProductConfiguration> page =
			productConfigurationResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
					externalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductConfiguration> ascPage =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)ascPage.getItems());
			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)ascPage.getItems());

			Page<ProductConfiguration> descPage =
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)descPage.getItems());
			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)descPage.getItems());
		}
	}

	protected ProductConfiguration
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_addProductConfiguration(
				String externalReferenceCode,
				ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPage()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationsPage_getId();
		Long irrelevantId =
			testGetProductConfigurationListIdProductConfigurationsPage_getIrrelevantId();

		Page<ProductConfiguration> page =
			productConfigurationResource.
				getProductConfigurationListIdProductConfigurationsPage(
					id, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductConfiguration irrelevantProductConfiguration =
				testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
					irrelevantId, randomIrrelevantProductConfiguration());

			page =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						irrelevantId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfiguration,
				(List<ProductConfiguration>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListIdProductConfigurationsPage_getExpectedActions(
					irrelevantId));
		}

		ProductConfiguration productConfiguration1 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, randomProductConfiguration());

		ProductConfiguration productConfiguration2 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, randomProductConfiguration());

		page =
			productConfigurationResource.
				getProductConfigurationListIdProductConfigurationsPage(
					id, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfiguration1, (List<ProductConfiguration>)page.getItems());
		assertContains(
			productConfiguration2, (List<ProductConfiguration>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListIdProductConfigurationsPage_getExpectedActions(
				id));

		productConfigurationResource.deleteProductConfiguration(
			productConfiguration1.getId());

		productConfigurationResource.deleteProductConfiguration(
			productConfiguration2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListIdProductConfigurationsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationsPage_getId();

		ProductConfiguration productConfiguration1 =
			randomProductConfiguration();

		productConfiguration1 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, productConfiguration1);

		for (EntityField entityField : entityFields) {
			Page<ProductConfiguration> page =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null,
						getFilterString(
							entityField, "between", productConfiguration1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfiguration1),
				(List<ProductConfiguration>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithFilterDoubleEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithFilterStringContains()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithFilterStringEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationsPage_getId();

		ProductConfiguration productConfiguration1 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, randomProductConfiguration());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfiguration productConfiguration2 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, randomProductConfiguration());

		for (EntityField entityField : entityFields) {
			Page<ProductConfiguration> page =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null,
						getFilterString(
							entityField, operator, productConfiguration1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfiguration1),
				(List<ProductConfiguration>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithPagination()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationsPage_getId();

		Page<ProductConfiguration> productConfigurationsPage =
			productConfigurationResource.
				getProductConfigurationListIdProductConfigurationsPage(
					id, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationsPage.getTotalCount());

		ProductConfiguration productConfiguration1 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, randomProductConfiguration());

		ProductConfiguration productConfiguration2 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, randomProductConfiguration());

		ProductConfiguration productConfiguration3 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, randomProductConfiguration());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfiguration> page1 =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)page1.getItems());

			Page<ProductConfiguration> page2 =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)page2.getItems());

			Page<ProductConfiguration> page3 =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfiguration3,
				(List<ProductConfiguration>)page3.getItems());
		}
		else {
			Page<ProductConfiguration> page1 =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null, Pagination.of(1, totalCount + 2),
						null);

			List<ProductConfiguration> productConfigurations1 =
				(List<ProductConfiguration>)page1.getItems();

			Assert.assertEquals(
				productConfigurations1.toString(), totalCount + 2,
				productConfigurations1.size());

			Page<ProductConfiguration> page2 =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null, Pagination.of(2, totalCount + 2),
						null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfiguration> productConfigurations2 =
				(List<ProductConfiguration>)page2.getItems();

			Assert.assertEquals(
				productConfigurations2.toString(), 1,
				productConfigurations2.size());

			Page<ProductConfiguration> page3 =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)page3.getItems());
			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)page3.getItems());
			assertContains(
				productConfiguration3,
				(List<ProductConfiguration>)page3.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithSortDateTime()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productConfiguration1, productConfiguration2) -> {
				BeanTestUtil.setProperty(
					productConfiguration1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithSortDouble()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productConfiguration1, productConfiguration2) -> {
				BeanTestUtil.setProperty(
					productConfiguration1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productConfiguration2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithSortInteger()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productConfiguration1, productConfiguration2) -> {
				BeanTestUtil.setProperty(
					productConfiguration1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productConfiguration2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationsPageWithSortString()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productConfiguration1, productConfiguration2) -> {
				Class<?> clazz = productConfiguration1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productConfiguration1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productConfiguration2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productConfiguration1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productConfiguration2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productConfiguration1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productConfiguration2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ProductConfiguration, ProductConfiguration,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationsPage_getId();

		ProductConfiguration productConfiguration1 =
			randomProductConfiguration();
		ProductConfiguration productConfiguration2 =
			randomProductConfiguration();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productConfiguration1, productConfiguration2);
		}

		productConfiguration1 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, productConfiguration1);

		productConfiguration2 =
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				id, productConfiguration2);

		Page<ProductConfiguration> page =
			productConfigurationResource.
				getProductConfigurationListIdProductConfigurationsPage(
					id, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductConfiguration> ascPage =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)ascPage.getItems());
			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)ascPage.getItems());

			Page<ProductConfiguration> descPage =
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productConfiguration2,
				(List<ProductConfiguration>)descPage.getItems());
			assertContains(
				productConfiguration1,
				(List<ProductConfiguration>)descPage.getItems());
		}
	}

	protected ProductConfiguration
			testGetProductConfigurationListIdProductConfigurationsPage_addProductConfiguration(
				Long id, ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductIdConfiguration() throws Exception {
		ProductConfiguration postProductConfiguration =
			testGetProductIdConfiguration_addProductConfiguration();

		ProductConfiguration getProductConfiguration =
			productConfigurationResource.getProductIdConfiguration(
				testGetProductIdConfiguration_getId(postProductConfiguration));

		assertEquals(postProductConfiguration, getProductConfiguration);
		assertValid(getProductConfiguration);
	}

	protected ProductConfiguration
			testGetProductIdConfiguration_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdConfiguration_getId(
			ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductIdConfiguration() throws Exception {
		ProductConfiguration productConfiguration =
			testGraphQLGetProductIdConfiguration_addProductConfiguration();

		// No namespace

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productIdConfiguration",
								new HashMap<String, Object>() {
									{
										put(
											"id",
											testGraphQLGetProductIdConfiguration_getId(
												productConfiguration));
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productIdConfiguration"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productConfiguration,
				ProductConfigurationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productIdConfiguration",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												testGraphQLGetProductIdConfiguration_getId(
													productConfiguration));
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productIdConfiguration"))));
	}

	protected Long testGraphQLGetProductIdConfiguration_getId(
			ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductIdConfigurationNotFound()
		throws Exception {

		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productIdConfiguration",
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
							"productIdConfiguration",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductConfiguration
			testGraphQLGetProductIdConfiguration_addProductConfiguration()
		throws Exception {

		return testGraphQLProductProductConfiguration_addProductConfiguration();
	}

	@Test
	public void testPatchProductByExternalReferenceCodeConfiguration()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPatchProductConfiguration() throws Exception {
		ProductConfiguration postProductConfiguration =
			testPatchProductConfiguration_addProductConfiguration();

		ProductConfiguration randomPatchProductConfiguration =
			randomPatchProductConfiguration();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfiguration patchProductConfiguration =
			productConfigurationResource.patchProductConfiguration(
				postProductConfiguration.getId(),
				randomPatchProductConfiguration);

		ProductConfiguration expectedPatchProductConfiguration =
			postProductConfiguration.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductConfiguration, expectedPatchProductConfiguration);

		ProductConfiguration getProductConfiguration =
			productConfigurationResource.getProductConfiguration(
				patchProductConfiguration.getId());

		assertEquals(
			expectedPatchProductConfiguration, getProductConfiguration);
		assertValid(getProductConfiguration);
	}

	protected ProductConfiguration
			testPatchProductConfiguration_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchProductConfigurationByExternalReferenceCode()
		throws Exception {

		ProductConfiguration postProductConfiguration =
			testPatchProductConfigurationByExternalReferenceCode_addProductConfiguration();

		ProductConfiguration randomPatchProductConfiguration =
			randomPatchProductConfiguration();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfiguration patchProductConfiguration =
			productConfigurationResource.
				patchProductConfigurationByExternalReferenceCode(
					postProductConfiguration.getExternalReferenceCode(),
					randomPatchProductConfiguration);

		ProductConfiguration expectedPatchProductConfiguration =
			postProductConfiguration.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductConfiguration, expectedPatchProductConfiguration);

		ProductConfiguration getProductConfiguration =
			productConfigurationResource.
				getProductConfigurationByExternalReferenceCode(
					patchProductConfiguration.getExternalReferenceCode());

		assertEquals(
			expectedPatchProductConfiguration, getProductConfiguration);
		assertValid(getProductConfiguration);
	}

	protected ProductConfiguration
			testPatchProductConfigurationByExternalReferenceCode_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchProductIdConfiguration() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostProductConfigurationListByExternalReferenceCodeProductConfiguration()
		throws Exception {

		ProductConfiguration randomProductConfiguration =
			randomProductConfiguration();

		ProductConfiguration postProductConfiguration =
			testPostProductConfigurationListByExternalReferenceCodeProductConfiguration_addProductConfiguration(
				randomProductConfiguration);

		assertEquals(randomProductConfiguration, postProductConfiguration);
		assertValid(postProductConfiguration);
	}

	protected ProductConfiguration
			testPostProductConfigurationListByExternalReferenceCodeProductConfiguration_addProductConfiguration(
				ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductConfigurationListIdProductConfiguration()
		throws Exception {

		ProductConfiguration randomProductConfiguration =
			randomProductConfiguration();

		ProductConfiguration postProductConfiguration =
			testPostProductConfigurationListIdProductConfiguration_addProductConfiguration(
				randomProductConfiguration);

		assertEquals(randomProductConfiguration, postProductConfiguration);
		assertValid(postProductConfiguration);
	}

	protected ProductConfiguration
			testPostProductConfigurationListIdProductConfiguration_addProductConfiguration(
				ProductConfiguration productConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductConfiguration productConfiguration1 =
			testBatchEngineDeleteImportTask_addProductConfiguration();

		testBatchEngineDeleteImportTask_deleteProductConfiguration(
			200, productConfiguration1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration1.getId()));

		productConfiguration1 =
			testBatchEngineDeleteImportTask_addProductConfiguration();

		testBatchEngineDeleteImportTask_deleteProductConfiguration(
			200, null, productConfiguration1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration1.getId()));

		productConfiguration1 =
			testBatchEngineDeleteImportTask_addProductConfiguration();
		ProductConfiguration productConfiguration2 =
			testBatchEngineDeleteImportTask_addProductConfiguration();

		testBatchEngineDeleteImportTask_deleteProductConfiguration(
			200, productConfiguration2.getExternalReferenceCode(),
			productConfiguration1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration1.getId()));
		assertHttpResponseStatusCode(
			200,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration2.getId()));

		testBatchEngineDeleteImportTask_deleteProductConfiguration(
			200, productConfiguration2.getExternalReferenceCode(),
			productConfiguration1.getId());

		assertHttpResponseStatusCode(
			404,
			productConfigurationResource.getProductConfigurationHttpResponse(
				productConfiguration2.getId()));
	}

	protected ProductConfiguration
			testBatchEngineDeleteImportTask_addProductConfiguration()
		throws Exception {

		return testDeleteProductConfiguration_addProductConfiguration();
	}

	protected void testBatchEngineDeleteImportTask_deleteProductConfiguration(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration",
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

	protected ProductConfiguration
			testGraphQLProductConfiguration_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ProductConfiguration
			testGraphQLProductProductConfiguration_addProductConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductConfiguration productConfiguration,
		List<ProductConfiguration> productConfigurations) {

		boolean contains = false;

		for (ProductConfiguration item : productConfigurations) {
			if (equals(productConfiguration, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productConfigurations + " does not contain " + productConfiguration,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductConfiguration productConfiguration1,
		ProductConfiguration productConfiguration2) {

		Assert.assertTrue(
			productConfiguration1 + " does not equal " + productConfiguration2,
			equals(productConfiguration1, productConfiguration2));
	}

	protected void assertEquals(
		List<ProductConfiguration> productConfigurations1,
		List<ProductConfiguration> productConfigurations2) {

		Assert.assertEquals(
			productConfigurations1.size(), productConfigurations2.size());

		for (int i = 0; i < productConfigurations1.size(); i++) {
			ProductConfiguration productConfiguration1 =
				productConfigurations1.get(i);
			ProductConfiguration productConfiguration2 =
				productConfigurations2.get(i);

			assertEquals(productConfiguration1, productConfiguration2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductConfiguration> productConfigurations1,
		List<ProductConfiguration> productConfigurations2) {

		Assert.assertEquals(
			productConfigurations1.size(), productConfigurations2.size());

		for (ProductConfiguration productConfiguration1 :
				productConfigurations1) {

			boolean contains = false;

			for (ProductConfiguration productConfiguration2 :
					productConfigurations2) {

				if (equals(productConfiguration1, productConfiguration2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productConfigurations2 + " does not contain " +
					productConfiguration1,
				contains);
		}
	}

	protected void assertValid(ProductConfiguration productConfiguration)
		throws Exception {

		boolean valid = true;

		if (productConfiguration.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (productConfiguration.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("allowBackOrder", additionalAssertFieldName)) {
				if (productConfiguration.getAllowBackOrder() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"allowedOrderQuantities", additionalAssertFieldName)) {

				if (productConfiguration.getAllowedOrderQuantities() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availabilityEstimateId", additionalAssertFieldName)) {

				if (productConfiguration.getAvailabilityEstimateId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availabilityEstimateName", additionalAssertFieldName)) {

				if (productConfiguration.getAvailabilityEstimateName() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("differences", additionalAssertFieldName)) {
				if (productConfiguration.getDifferences() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"displayAvailability", additionalAssertFieldName)) {

				if (productConfiguration.getDisplayAvailability() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"displayStockQuantity", additionalAssertFieldName)) {

				if (productConfiguration.getDisplayStockQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"entityExternalReferenceCode", additionalAssertFieldName)) {

				if (productConfiguration.getEntityExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (productConfiguration.getEntityId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("entityName", additionalAssertFieldName)) {
				if (productConfiguration.getEntityName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("entityType", additionalAssertFieldName)) {
				if (productConfiguration.getEntityType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (productConfiguration.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("inventoryEngine", additionalAssertFieldName)) {
				if (productConfiguration.getInventoryEngine() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("lowStockAction", additionalAssertFieldName)) {
				if (productConfiguration.getLowStockAction() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("maxOrderQuantity", additionalAssertFieldName)) {
				if (productConfiguration.getMaxOrderQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("minOrderQuantity", additionalAssertFieldName)) {
				if (productConfiguration.getMinOrderQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("minStockQuantity", additionalAssertFieldName)) {
				if (productConfiguration.getMinStockQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"multipleOrderQuantity", additionalAssertFieldName)) {

				if (productConfiguration.getMultipleOrderQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productShippingConfiguration",
					additionalAssertFieldName)) {

				if (productConfiguration.getProductShippingConfiguration() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productTaxConfiguration", additionalAssertFieldName)) {

				if (productConfiguration.getProductTaxConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("purchasable", additionalAssertFieldName)) {
				if (productConfiguration.getPurchasable() == null) {
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

	protected void assertValid(Page<ProductConfiguration> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductConfiguration> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductConfiguration> productConfigurations =
			page.getItems();

		int size = productConfigurations.size();

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
						ProductConfiguration.class)) {

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
		ProductConfiguration productConfiguration1,
		ProductConfiguration productConfiguration2) {

		if (productConfiguration1 == productConfiguration2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)productConfiguration1.getActions(),
						(Map)productConfiguration2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("allowBackOrder", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getAllowBackOrder(),
						productConfiguration2.getAllowBackOrder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"allowedOrderQuantities", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getAllowedOrderQuantities(),
						productConfiguration2.getAllowedOrderQuantities())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availabilityEstimateId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getAvailabilityEstimateId(),
						productConfiguration2.getAvailabilityEstimateId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availabilityEstimateName", additionalAssertFieldName)) {

				if (!equals(
						(Map)
							productConfiguration1.getAvailabilityEstimateName(),
						(Map)
							productConfiguration2.
								getAvailabilityEstimateName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("differences", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getDifferences(),
						productConfiguration2.getDifferences())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"displayAvailability", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getDisplayAvailability(),
						productConfiguration2.getDisplayAvailability())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"displayStockQuantity", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getDisplayStockQuantity(),
						productConfiguration2.getDisplayStockQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"entityExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getEntityExternalReferenceCode(),
						productConfiguration2.
							getEntityExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getEntityId(),
						productConfiguration2.getEntityId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entityName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getEntityName(),
						productConfiguration2.getEntityName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entityType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getEntityType(),
						productConfiguration2.getEntityType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getExternalReferenceCode(),
						productConfiguration2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getId(),
						productConfiguration2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("inventoryEngine", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getInventoryEngine(),
						productConfiguration2.getInventoryEngine())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("lowStockAction", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getLowStockAction(),
						productConfiguration2.getLowStockAction())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("maxOrderQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getMaxOrderQuantity(),
						productConfiguration2.getMaxOrderQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("minOrderQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getMinOrderQuantity(),
						productConfiguration2.getMinOrderQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("minStockQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getMinStockQuantity(),
						productConfiguration2.getMinStockQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"multipleOrderQuantity", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getMultipleOrderQuantity(),
						productConfiguration2.getMultipleOrderQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productShippingConfiguration",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getProductShippingConfiguration(),
						productConfiguration2.
							getProductShippingConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productTaxConfiguration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfiguration1.getProductTaxConfiguration(),
						productConfiguration2.getProductTaxConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("purchasable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfiguration1.getPurchasable(),
						productConfiguration2.getPurchasable())) {

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

		if (!(_productConfigurationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productConfigurationResource;

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
		ProductConfiguration productConfiguration) {

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

		if (entityFieldName.equals("allowBackOrder")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("allowedOrderQuantities")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("availabilityEstimateId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("availabilityEstimateName")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("differences")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayAvailability")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayStockQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("entityExternalReferenceCode")) {
			Object object =
				productConfiguration.getEntityExternalReferenceCode();

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

		if (entityFieldName.equals("entityId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("entityName")) {
			Object object = productConfiguration.getEntityName();

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

		if (entityFieldName.equals("entityType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = productConfiguration.getExternalReferenceCode();

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

		if (entityFieldName.equals("inventoryEngine")) {
			Object object = productConfiguration.getInventoryEngine();

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

		if (entityFieldName.equals("lowStockAction")) {
			Object object = productConfiguration.getLowStockAction();

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

		if (entityFieldName.equals("maxOrderQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("minOrderQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("minStockQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("multipleOrderQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productShippingConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productTaxConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("purchasable")) {
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

	protected ProductConfiguration randomProductConfiguration()
		throws Exception {

		return new ProductConfiguration() {
			{
				allowBackOrder = RandomTestUtil.randomBoolean();
				availabilityEstimateId = RandomTestUtil.randomLong();
				displayAvailability = RandomTestUtil.randomBoolean();
				displayStockQuantity = RandomTestUtil.randomBoolean();
				entityExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				entityId = RandomTestUtil.randomLong();
				entityName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				inventoryEngine = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				lowStockAction = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				purchasable = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ProductConfiguration randomIrrelevantProductConfiguration()
		throws Exception {

		ProductConfiguration randomIrrelevantProductConfiguration =
			randomProductConfiguration();

		return randomIrrelevantProductConfiguration;
	}

	protected ProductConfiguration randomPatchProductConfiguration()
		throws Exception {

		return randomProductConfiguration();
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

	protected ProductConfigurationResource productConfigurationResource;
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
		LogFactoryUtil.getLog(BaseProductConfigurationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductConfigurationResource _productConfigurationResource;

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