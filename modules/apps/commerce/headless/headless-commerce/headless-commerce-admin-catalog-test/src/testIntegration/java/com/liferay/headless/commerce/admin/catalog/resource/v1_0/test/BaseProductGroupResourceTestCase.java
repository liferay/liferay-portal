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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductGroup;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductGroupResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductGroupSerDes;
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
public abstract class BaseProductGroupResourceTestCase {

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

		_productGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productGroupResource = ProductGroupResource.builder(
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

		ProductGroup productGroup1 = randomProductGroup();

		String json = objectMapper.writeValueAsString(productGroup1);

		ProductGroup productGroup2 = ProductGroupSerDes.toDTO(json);

		Assert.assertTrue(equals(productGroup1, productGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductGroup productGroup = randomProductGroup();

		String json1 = objectMapper.writeValueAsString(productGroup);
		String json2 = ProductGroupSerDes.toJSON(productGroup);

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

		ProductGroup productGroup = randomProductGroup();

		productGroup.setExternalReferenceCode(regex);

		String json = ProductGroupSerDes.toJSON(productGroup);

		Assert.assertFalse(json.contains(regex));

		productGroup = ProductGroupSerDes.toDTO(json);

		Assert.assertEquals(regex, productGroup.getExternalReferenceCode());
	}

	@Test
	public void testDeleteProductGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductGroup productGroup = testDeleteProductGroup_addProductGroup();

		assertHttpResponseStatusCode(
			204,
			productGroupResource.deleteProductGroupHttpResponse(
				productGroup.getId()));

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup.getId()));
		assertHttpResponseStatusCode(
			404, productGroupResource.getProductGroupHttpResponse(0L));
	}

	protected ProductGroup testDeleteProductGroup_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductGroup() throws Exception {

		// No namespace

		ProductGroup productGroup1 =
			testGraphQLDeleteProductGroup_addProductGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductGroup",
						new HashMap<String, Object>() {
							{
								put("id", productGroup1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductGroup"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productGroup",
					new HashMap<String, Object>() {
						{
							put("id", productGroup1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductGroup productGroup2 =
			testGraphQLDeleteProductGroup_addProductGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductGroup",
							new HashMap<String, Object>() {
								{
									put("id", productGroup2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductGroup"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productGroup",
						new HashMap<String, Object>() {
							{
								put("id", productGroup2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductGroup testGraphQLDeleteProductGroup_addProductGroup()
		throws Exception {

		return testGraphQLProductGroup_addProductGroup();
	}

	@Test
	public void testDeleteProductGroupBatch() throws Exception {
		ProductGroup productGroup1 =
			testDeleteProductGroupBatch_addProductGroup();

		testDeleteProductGroupBatch_deleteProductGroup(
			202, productGroup1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup1.getId()));

		productGroup1 = testDeleteProductGroupBatch_addProductGroup();

		testDeleteProductGroupBatch_deleteProductGroup(
			202, null, productGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup1.getId()));

		productGroup1 = testDeleteProductGroupBatch_addProductGroup();
		ProductGroup productGroup2 =
			testDeleteProductGroupBatch_addProductGroup();

		testDeleteProductGroupBatch_deleteProductGroup(
			202, productGroup2.getExternalReferenceCode(),
			productGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup1.getId()));
		assertHttpResponseStatusCode(
			200,
			productGroupResource.getProductGroupHttpResponse(
				productGroup2.getId()));

		testDeleteProductGroupBatch_deleteProductGroup(
			202, productGroup2.getExternalReferenceCode(),
			productGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup2.getId()));
	}

	protected ProductGroup testDeleteProductGroupBatch_addProductGroup()
		throws Exception {

		return testDeleteProductGroup_addProductGroup();
	}

	protected void testDeleteProductGroupBatch_deleteProductGroup(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productGroupResource.deleteProductGroupBatchHttpResponse(
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
	public void testDeleteProductGroupByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductGroup productGroup =
			testDeleteProductGroupByExternalReferenceCode_addProductGroup();

		assertHttpResponseStatusCode(
			204,
			productGroupResource.
				deleteProductGroupByExternalReferenceCodeHttpResponse(
					productGroup.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			productGroupResource.
				getProductGroupByExternalReferenceCodeHttpResponse(
					productGroup.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			productGroupResource.
				getProductGroupByExternalReferenceCodeHttpResponse("-"));
	}

	protected ProductGroup
			testDeleteProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductGroupByExternalReferenceCode()
		throws Exception {

		// No namespace

		ProductGroup productGroup1 =
			testGraphQLDeleteProductGroupByExternalReferenceCode_addProductGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductGroupByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productGroup1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteProductGroupByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productGroupByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									productGroup1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductGroup productGroup2 =
			testGraphQLDeleteProductGroupByExternalReferenceCode_addProductGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductGroupByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											productGroup2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductGroupByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productGroupByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productGroup2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductGroup
			testGraphQLDeleteProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		return testGraphQLProductGroup_addProductGroup();
	}

	@Test
	public void testGetProductGroup() throws Exception {
		ProductGroup postProductGroup = testGetProductGroup_addProductGroup();

		ProductGroup getProductGroup = productGroupResource.getProductGroup(
			postProductGroup.getId());

		assertEquals(postProductGroup, getProductGroup);
		assertValid(getProductGroup);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductGroup postProductGroup = testGetProductGroup_addProductGroup();

		ProductGroup getProductGroup = productGroupResource.getProductGroup(
			postProductGroup.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroup"
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

		Object item = vulcanCRUDItemDelegate.getItem(postProductGroup.getId());

		assertEquals(
			getProductGroup, ProductGroupSerDes.toDTO(item.toString()));
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

	protected ProductGroup testGetProductGroup_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductGroup() throws Exception {
		ProductGroup productGroup =
			testGraphQLGetProductGroup_addProductGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productGroup",
								new HashMap<String, Object>() {
									{
										put("id", productGroup.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productGroup"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productGroup",
									new HashMap<String, Object>() {
										{
											put("id", productGroup.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productGroup"))));
	}

	@Test
	public void testGraphQLGetProductGroupNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productGroup",
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
							"productGroup",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductGroup testGraphQLGetProductGroup_addProductGroup()
		throws Exception {

		return testGraphQLProductGroup_addProductGroup();
	}

	@Test
	public void testGetProductGroupByExternalReferenceCode() throws Exception {
		ProductGroup postProductGroup =
			testGetProductGroupByExternalReferenceCode_addProductGroup();

		ProductGroup getProductGroup =
			productGroupResource.getProductGroupByExternalReferenceCode(
				postProductGroup.getExternalReferenceCode());

		assertEquals(postProductGroup, getProductGroup);
		assertValid(getProductGroup);
	}

	protected ProductGroup
			testGetProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductGroupByExternalReferenceCode()
		throws Exception {

		ProductGroup productGroup =
			testGraphQLGetProductGroupByExternalReferenceCode_addProductGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productGroupByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												productGroup.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productGroupByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productGroupByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													productGroup.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productGroupByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetProductGroupByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productGroupByExternalReferenceCode",
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
							"productGroupByExternalReferenceCode",
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

	protected ProductGroup
			testGraphQLGetProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		return testGraphQLProductGroup_addProductGroup();
	}

	@Test
	public void testGetProductGroupsPage() throws Exception {
		Page<ProductGroup> page = productGroupResource.getProductGroupsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		ProductGroup productGroup1 = testGetProductGroupsPage_addProductGroup(
			randomProductGroup());

		ProductGroup productGroup2 = testGetProductGroupsPage_addProductGroup(
			randomProductGroup());

		page = productGroupResource.getProductGroupsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(productGroup1, (List<ProductGroup>)page.getItems());
		assertContains(productGroup2, (List<ProductGroup>)page.getItems());
		assertValid(page, testGetProductGroupsPage_getExpectedActions());

		productGroupResource.deleteProductGroup(productGroup1.getId());

		productGroupResource.deleteProductGroup(productGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductGroupsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductGroupsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		ProductGroup productGroup1 = randomProductGroup();

		productGroup1 = testGetProductGroupsPage_addProductGroup(productGroup1);

		for (EntityField entityField : entityFields) {
			Page<ProductGroup> page = productGroupResource.getProductGroupsPage(
				null, getFilterString(entityField, "between", productGroup1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productGroup1),
				(List<ProductGroup>)page.getItems());
		}
	}

	@Test
	public void testGetProductGroupsPageWithFilterDoubleEquals()
		throws Exception {

		testGetProductGroupsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductGroupsPageWithFilterStringContains()
		throws Exception {

		testGetProductGroupsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductGroupsPageWithFilterStringEquals()
		throws Exception {

		testGetProductGroupsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductGroupsPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductGroupsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetProductGroupsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ProductGroup productGroup1 = testGetProductGroupsPage_addProductGroup(
			randomProductGroup());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductGroup productGroup2 = testGetProductGroupsPage_addProductGroup(
			randomProductGroup());

		for (EntityField entityField : entityFields) {
			Page<ProductGroup> page = productGroupResource.getProductGroupsPage(
				null, getFilterString(entityField, operator, productGroup1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productGroup1),
				(List<ProductGroup>)page.getItems());
		}
	}

	@Test
	public void testGetProductGroupsPageWithPagination() throws Exception {
		Page<ProductGroup> productGroupsPage =
			productGroupResource.getProductGroupsPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productGroupsPage.getTotalCount());

		ProductGroup productGroup1 = testGetProductGroupsPage_addProductGroup(
			randomProductGroup());

		ProductGroup productGroup2 = testGetProductGroupsPage_addProductGroup(
			randomProductGroup());

		ProductGroup productGroup3 = testGetProductGroupsPage_addProductGroup(
			randomProductGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductGroup> page1 =
				productGroupResource.getProductGroupsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(productGroup1, (List<ProductGroup>)page1.getItems());

			Page<ProductGroup> page2 =
				productGroupResource.getProductGroupsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(productGroup2, (List<ProductGroup>)page2.getItems());

			Page<ProductGroup> page3 =
				productGroupResource.getProductGroupsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(productGroup3, (List<ProductGroup>)page3.getItems());
		}
		else {
			Page<ProductGroup> page1 =
				productGroupResource.getProductGroupsPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<ProductGroup> productGroups1 =
				(List<ProductGroup>)page1.getItems();

			Assert.assertEquals(
				productGroups1.toString(), totalCount + 2,
				productGroups1.size());

			Page<ProductGroup> page2 =
				productGroupResource.getProductGroupsPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductGroup> productGroups2 =
				(List<ProductGroup>)page2.getItems();

			Assert.assertEquals(
				productGroups2.toString(), 1, productGroups2.size());

			Page<ProductGroup> page3 =
				productGroupResource.getProductGroupsPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(productGroup1, (List<ProductGroup>)page3.getItems());
			assertContains(productGroup2, (List<ProductGroup>)page3.getItems());
			assertContains(productGroup3, (List<ProductGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetProductGroupsPageWithSortDateTime() throws Exception {
		testGetProductGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productGroup1, productGroup2) -> {
				BeanTestUtil.setProperty(
					productGroup1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductGroupsPageWithSortDouble() throws Exception {
		testGetProductGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productGroup1, productGroup2) -> {
				BeanTestUtil.setProperty(
					productGroup1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productGroup2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductGroupsPageWithSortInteger() throws Exception {
		testGetProductGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productGroup1, productGroup2) -> {
				BeanTestUtil.setProperty(
					productGroup1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productGroup2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductGroupsPageWithSortString() throws Exception {
		testGetProductGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productGroup1, productGroup2) -> {
				Class<?> clazz = productGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetProductGroupsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ProductGroup, ProductGroup, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ProductGroup productGroup1 = randomProductGroup();
		ProductGroup productGroup2 = randomProductGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, productGroup1, productGroup2);
		}

		productGroup1 = testGetProductGroupsPage_addProductGroup(productGroup1);

		productGroup2 = testGetProductGroupsPage_addProductGroup(productGroup2);

		Page<ProductGroup> page = productGroupResource.getProductGroupsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductGroup> ascPage =
				productGroupResource.getProductGroupsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				productGroup1, (List<ProductGroup>)ascPage.getItems());
			assertContains(
				productGroup2, (List<ProductGroup>)ascPage.getItems());

			Page<ProductGroup> descPage =
				productGroupResource.getProductGroupsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				productGroup2, (List<ProductGroup>)descPage.getItems());
			assertContains(
				productGroup1, (List<ProductGroup>)descPage.getItems());
		}
	}

	protected ProductGroup testGetProductGroupsPage_addProductGroup(
			ProductGroup productGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductGroupsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"productGroups",
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

		JSONObject productGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/productGroups");

		long totalCount = productGroupsJSONObject.getLong("totalCount");

		ProductGroup productGroup1 = testGraphQLProductGroup_addProductGroup(
			randomProductGroup());

		ProductGroup productGroup2 = testGraphQLProductGroup_addProductGroup(
			randomProductGroup());

		productGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/productGroups");

		Assert.assertEquals(
			totalCount + 2, productGroupsJSONObject.getLong("totalCount"));

		assertContains(
			productGroup1,
			Arrays.asList(
				ProductGroupSerDes.toDTOs(
					productGroupsJSONObject.getString("items"))));
		assertContains(
			productGroup2,
			Arrays.asList(
				ProductGroupSerDes.toDTOs(
					productGroupsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		productGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/productGroups");

		Assert.assertEquals(
			totalCount + 2, productGroupsJSONObject.getLong("totalCount"));

		assertContains(
			productGroup1,
			Arrays.asList(
				ProductGroupSerDes.toDTOs(
					productGroupsJSONObject.getString("items"))));
		assertContains(
			productGroup2,
			Arrays.asList(
				ProductGroupSerDes.toDTOs(
					productGroupsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchProductGroup() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchProductGroupByExternalReferenceCode()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostProductGroup() throws Exception {
		ProductGroup randomProductGroup = randomProductGroup();

		ProductGroup postProductGroup = testPostProductGroup_addProductGroup(
			randomProductGroup);

		assertEquals(randomProductGroup, postProductGroup);
		assertValid(postProductGroup);
	}

	protected ProductGroup testPostProductGroup_addProductGroup(
			ProductGroup productGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostProductGroup() throws Exception {
		ProductGroup randomProductGroup = randomProductGroup();

		ProductGroup productGroup = testGraphQLProductGroup_addProductGroup(
			randomProductGroup);

		Assert.assertTrue(equals(randomProductGroup, productGroup));
	}

	@Test
	public void testPutProductGroupByExternalReferenceCode() throws Exception {
		ProductGroup postProductGroup =
			testPutProductGroupByExternalReferenceCode_addProductGroup();

		ProductGroup randomProductGroup = randomProductGroup();

		ProductGroup putProductGroup =
			productGroupResource.putProductGroupByExternalReferenceCode(
				postProductGroup.getExternalReferenceCode(),
				randomProductGroup);

		assertEquals(randomProductGroup, putProductGroup);
		assertValid(putProductGroup);

		ProductGroup getProductGroup =
			productGroupResource.getProductGroupByExternalReferenceCode(
				putProductGroup.getExternalReferenceCode());

		assertEquals(randomProductGroup, getProductGroup);
		assertValid(getProductGroup);

		ProductGroup newProductGroup =
			testPutProductGroupByExternalReferenceCode_createProductGroup();

		putProductGroup =
			productGroupResource.putProductGroupByExternalReferenceCode(
				newProductGroup.getExternalReferenceCode(), newProductGroup);

		assertEquals(newProductGroup, putProductGroup);
		assertValid(putProductGroup);

		getProductGroup =
			productGroupResource.getProductGroupByExternalReferenceCode(
				putProductGroup.getExternalReferenceCode());

		assertEquals(newProductGroup, getProductGroup);

		Assert.assertEquals(
			newProductGroup.getExternalReferenceCode(),
			putProductGroup.getExternalReferenceCode());
	}

	protected ProductGroup
			testPutProductGroupByExternalReferenceCode_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ProductGroup
			testPutProductGroupByExternalReferenceCode_createProductGroup()
		throws Exception {

		return randomProductGroup();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductGroup productGroup1 =
			testBatchEngineDeleteImportTask_addProductGroup();

		testBatchEngineDeleteImportTask_deleteProductGroup(
			200, productGroup1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup1.getId()));

		productGroup1 = testBatchEngineDeleteImportTask_addProductGroup();

		testBatchEngineDeleteImportTask_deleteProductGroup(
			200, null, productGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup1.getId()));

		productGroup1 = testBatchEngineDeleteImportTask_addProductGroup();
		ProductGroup productGroup2 =
			testBatchEngineDeleteImportTask_addProductGroup();

		testBatchEngineDeleteImportTask_deleteProductGroup(
			200, productGroup2.getExternalReferenceCode(),
			productGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup1.getId()));
		assertHttpResponseStatusCode(
			200,
			productGroupResource.getProductGroupHttpResponse(
				productGroup2.getId()));

		testBatchEngineDeleteImportTask_deleteProductGroup(
			200, productGroup2.getExternalReferenceCode(),
			productGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productGroupResource.getProductGroupHttpResponse(
				productGroup2.getId()));
	}

	protected ProductGroup testBatchEngineDeleteImportTask_addProductGroup()
		throws Exception {

		return testDeleteProductGroup_addProductGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteProductGroup(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroup",
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

	protected ProductGroup testGraphQLProductGroup_addProductGroup()
		throws Exception {

		return testGraphQLProductGroup_addProductGroup(randomProductGroup());
	}

	protected ProductGroup testGraphQLProductGroup_addProductGroup(
			ProductGroup productGroup)
		throws Exception {

		JSONDeserializer<ProductGroup> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ProductGroup.class)) {

			if (getGraphQLValue(field.get(productGroup)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(productGroup)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createProductGroup",
						new HashMap<String, Object>() {
							{
								put("productGroup", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createProductGroup"),
			ProductGroup.class);
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
		ProductGroup productGroup, List<ProductGroup> productGroups) {

		boolean contains = false;

		for (ProductGroup item : productGroups) {
			if (equals(productGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productGroups + " does not contain " + productGroup, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductGroup productGroup1, ProductGroup productGroup2) {

		Assert.assertTrue(
			productGroup1 + " does not equal " + productGroup2,
			equals(productGroup1, productGroup2));
	}

	protected void assertEquals(
		List<ProductGroup> productGroups1, List<ProductGroup> productGroups2) {

		Assert.assertEquals(productGroups1.size(), productGroups2.size());

		for (int i = 0; i < productGroups1.size(); i++) {
			ProductGroup productGroup1 = productGroups1.get(i);
			ProductGroup productGroup2 = productGroups2.get(i);

			assertEquals(productGroup1, productGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductGroup> productGroups1, List<ProductGroup> productGroups2) {

		Assert.assertEquals(productGroups1.size(), productGroups2.size());

		for (ProductGroup productGroup1 : productGroups1) {
			boolean contains = false;

			for (ProductGroup productGroup2 : productGroups2) {
				if (equals(productGroup1, productGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productGroups2 + " does not contain " + productGroup1,
				contains);
		}
	}

	protected void assertValid(ProductGroup productGroup) throws Exception {
		boolean valid = true;

		if (productGroup.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (productGroup.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (productGroup.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (productGroup.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("products", additionalAssertFieldName)) {
				if (productGroup.getProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productsCount", additionalAssertFieldName)) {
				if (productGroup.getProductsCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (productGroup.getTitle() == null) {
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

	protected void assertValid(Page<ProductGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductGroup> productGroups = page.getItems();

		int size = productGroups.size();

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
						ProductGroup.class)) {

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
		ProductGroup productGroup1, ProductGroup productGroup2) {

		if (productGroup1 == productGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)productGroup1.getCustomFields(),
						(Map)productGroup2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)productGroup1.getDescription(),
						(Map)productGroup2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productGroup1.getExternalReferenceCode(),
						productGroup2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productGroup1.getId(), productGroup2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("products", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productGroup1.getProducts(),
						productGroup2.getProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productsCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productGroup1.getProductsCount(),
						productGroup2.getProductsCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!equals(
						(Map)productGroup1.getTitle(),
						(Map)productGroup2.getTitle())) {

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

		if (!(_productGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productGroupResource;

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
		EntityField entityField, String operator, ProductGroup productGroup) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = productGroup.getExternalReferenceCode();

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

		if (entityFieldName.equals("products")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productsCount")) {
			sb.append(String.valueOf(productGroup.getProductsCount()));

			return sb.toString();
		}

		if (entityFieldName.equals("title")) {
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

	protected ProductGroup randomProductGroup() throws Exception {
		return new ProductGroup() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				productsCount = RandomTestUtil.randomInt();
			}
		};
	}

	protected ProductGroup randomIrrelevantProductGroup() throws Exception {
		ProductGroup randomIrrelevantProductGroup = randomProductGroup();

		return randomIrrelevantProductGroup;
	}

	protected ProductGroup randomPatchProductGroup() throws Exception {
		return randomProductGroup();
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

	protected ProductGroupResource productGroupResource;
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
		LogFactoryUtil.getLog(BaseProductGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductGroupResource _productGroupResource;

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