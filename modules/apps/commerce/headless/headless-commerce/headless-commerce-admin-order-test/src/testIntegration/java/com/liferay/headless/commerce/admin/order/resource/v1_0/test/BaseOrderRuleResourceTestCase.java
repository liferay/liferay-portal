/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRule;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderRuleResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderRuleSerDes;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseOrderRuleResourceTestCase {

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

		_orderRuleResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderRuleResource = OrderRuleResource.builder(
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

		OrderRule orderRule1 = randomOrderRule();

		String json = objectMapper.writeValueAsString(orderRule1);

		OrderRule orderRule2 = OrderRuleSerDes.toDTO(json);

		Assert.assertTrue(equals(orderRule1, orderRule2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderRule orderRule = randomOrderRule();

		String json1 = objectMapper.writeValueAsString(orderRule);
		String json2 = OrderRuleSerDes.toJSON(orderRule);

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

		OrderRule orderRule = randomOrderRule();

		orderRule.setAuthor(regex);
		orderRule.setDescription(regex);
		orderRule.setExternalReferenceCode(regex);
		orderRule.setName(regex);
		orderRule.setType(regex);
		orderRule.setTypeSettings(regex);

		String json = OrderRuleSerDes.toJSON(orderRule);

		Assert.assertFalse(json.contains(regex));

		orderRule = OrderRuleSerDes.toDTO(json);

		Assert.assertEquals(regex, orderRule.getAuthor());
		Assert.assertEquals(regex, orderRule.getDescription());
		Assert.assertEquals(regex, orderRule.getExternalReferenceCode());
		Assert.assertEquals(regex, orderRule.getName());
		Assert.assertEquals(regex, orderRule.getType());
		Assert.assertEquals(regex, orderRule.getTypeSettings());
	}

	@Test
	public void testDeleteOrderRule() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRule orderRule = testDeleteOrderRule_addOrderRule();

		assertHttpResponseStatusCode(
			204,
			orderRuleResource.deleteOrderRuleHttpResponse(orderRule.getId()));

		assertHttpResponseStatusCode(
			404, orderRuleResource.getOrderRuleHttpResponse(orderRule.getId()));
		assertHttpResponseStatusCode(
			404, orderRuleResource.getOrderRuleHttpResponse(0L));
	}

	protected OrderRule testDeleteOrderRule_addOrderRule() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderRule() throws Exception {

		// No namespace

		OrderRule orderRule1 = testGraphQLDeleteOrderRule_addOrderRule();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderRule",
						new HashMap<String, Object>() {
							{
								put("id", orderRule1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderRule"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderRule",
					new HashMap<String, Object>() {
						{
							put("id", orderRule1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderRule orderRule2 = testGraphQLDeleteOrderRule_addOrderRule();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderRule",
							new HashMap<String, Object>() {
								{
									put("id", orderRule2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderRule"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderRule",
						new HashMap<String, Object>() {
							{
								put("id", orderRule2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderRule testGraphQLDeleteOrderRule_addOrderRule()
		throws Exception {

		return testGraphQLOrderRule_addOrderRule();
	}

	@Test
	public void testDeleteOrderRuleBatch() throws Exception {
		OrderRule orderRule1 = testDeleteOrderRuleBatch_addOrderRule();

		testDeleteOrderRuleBatch_deleteOrderRule(
			202, orderRule1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule1.getId()));

		orderRule1 = testDeleteOrderRuleBatch_addOrderRule();

		testDeleteOrderRuleBatch_deleteOrderRule(202, null, orderRule1.getId());

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule1.getId()));

		orderRule1 = testDeleteOrderRuleBatch_addOrderRule();
		OrderRule orderRule2 = testDeleteOrderRuleBatch_addOrderRule();

		testDeleteOrderRuleBatch_deleteOrderRule(
			202, orderRule2.getExternalReferenceCode(), orderRule1.getId());

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderRuleResource.getOrderRuleHttpResponse(orderRule2.getId()));

		testDeleteOrderRuleBatch_deleteOrderRule(
			202, orderRule2.getExternalReferenceCode(), orderRule1.getId());

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule2.getId()));
	}

	protected OrderRule testDeleteOrderRuleBatch_addOrderRule()
		throws Exception {

		return testDeleteOrderRule_addOrderRule();
	}

	protected void testDeleteOrderRuleBatch_deleteOrderRule(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderRuleResource.deleteOrderRuleBatchHttpResponse(
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
	public void testDeleteOrderRuleByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRule orderRule =
			testDeleteOrderRuleByExternalReferenceCode_addOrderRule();

		assertHttpResponseStatusCode(
			204,
			orderRuleResource.
				deleteOrderRuleByExternalReferenceCodeHttpResponse(
					orderRule.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleByExternalReferenceCodeHttpResponse(
				orderRule.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected OrderRule
			testDeleteOrderRuleByExternalReferenceCode_addOrderRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderRuleByExternalReferenceCode()
		throws Exception {

		// No namespace

		OrderRule orderRule1 =
			testGraphQLDeleteOrderRuleByExternalReferenceCode_addOrderRule();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderRuleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderRule1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrderRuleByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderRuleByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + orderRule1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderRule orderRule2 =
			testGraphQLDeleteOrderRuleByExternalReferenceCode_addOrderRule();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderRuleByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											orderRule2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderRuleByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderRuleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderRule2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderRule
			testGraphQLDeleteOrderRuleByExternalReferenceCode_addOrderRule()
		throws Exception {

		return testGraphQLOrderRule_addOrderRule();
	}

	@Test
	public void testGetOrderRule() throws Exception {
		OrderRule postOrderRule = testGetOrderRule_addOrderRule();

		OrderRule getOrderRule = orderRuleResource.getOrderRule(
			postOrderRule.getId());

		assertEquals(postOrderRule, getOrderRule);
		assertValid(getOrderRule);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		OrderRule postOrderRule = testGetOrderRule_addOrderRule();

		OrderRule getOrderRule = orderRuleResource.getOrderRule(
			postOrderRule.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRule"
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

		Object item = vulcanCRUDItemDelegate.getItem(postOrderRule.getId());

		assertEquals(getOrderRule, OrderRuleSerDes.toDTO(item.toString()));
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

	protected OrderRule testGetOrderRule_addOrderRule() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderRule() throws Exception {
		OrderRule orderRule = testGraphQLGetOrderRule_addOrderRule();

		// No namespace

		Assert.assertTrue(
			equals(
				orderRule,
				OrderRuleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderRule",
								new HashMap<String, Object>() {
									{
										put("id", orderRule.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/orderRule"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderRule,
				OrderRuleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderRule",
									new HashMap<String, Object>() {
										{
											put("id", orderRule.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderRule"))));
	}

	@Test
	public void testGraphQLGetOrderRuleNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderRule",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"orderRule",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OrderRule testGraphQLGetOrderRule_addOrderRule()
		throws Exception {

		return testGraphQLOrderRule_addOrderRule();
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCode() throws Exception {
		OrderRule postOrderRule =
			testGetOrderRuleByExternalReferenceCode_addOrderRule();

		OrderRule getOrderRule =
			orderRuleResource.getOrderRuleByExternalReferenceCode(
				postOrderRule.getExternalReferenceCode());

		assertEquals(postOrderRule, getOrderRule);
		assertValid(getOrderRule);
	}

	protected OrderRule testGetOrderRuleByExternalReferenceCode_addOrderRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderRuleByExternalReferenceCode()
		throws Exception {

		OrderRule orderRule =
			testGraphQLGetOrderRuleByExternalReferenceCode_addOrderRule();

		// No namespace

		Assert.assertTrue(
			equals(
				orderRule,
				OrderRuleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderRuleByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												orderRule.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/orderRuleByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderRule,
				OrderRuleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderRuleByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													orderRule.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderRuleByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOrderRuleByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderRuleByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"orderRuleByExternalReferenceCode",
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

	protected OrderRule
			testGraphQLGetOrderRuleByExternalReferenceCode_addOrderRule()
		throws Exception {

		return testGraphQLOrderRule_addOrderRule();
	}

	@Test
	public void testGetOrderRulesPage() throws Exception {
		Page<OrderRule> page = orderRuleResource.getOrderRulesPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		OrderRule orderRule1 = testGetOrderRulesPage_addOrderRule(
			randomOrderRule());

		OrderRule orderRule2 = testGetOrderRulesPage_addOrderRule(
			randomOrderRule());

		page = orderRuleResource.getOrderRulesPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(orderRule1, (List<OrderRule>)page.getItems());
		assertContains(orderRule2, (List<OrderRule>)page.getItems());
		assertValid(page, testGetOrderRulesPage_getExpectedActions());

		orderRuleResource.deleteOrderRule(orderRule1.getId());

		orderRuleResource.deleteOrderRule(orderRule2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRulesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRulesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderRule orderRule1 = randomOrderRule();

		orderRule1 = testGetOrderRulesPage_addOrderRule(orderRule1);

		for (EntityField entityField : entityFields) {
			Page<OrderRule> page = orderRuleResource.getOrderRulesPage(
				null, getFilterString(entityField, "between", orderRule1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRule1),
				(List<OrderRule>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRulesPageWithFilterDoubleEquals() throws Exception {
		testGetOrderRulesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderRulesPageWithFilterStringContains()
		throws Exception {

		testGetOrderRulesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRulesPageWithFilterStringEquals() throws Exception {
		testGetOrderRulesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRulesPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderRulesPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderRulesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderRule orderRule1 = testGetOrderRulesPage_addOrderRule(
			randomOrderRule());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRule orderRule2 = testGetOrderRulesPage_addOrderRule(
			randomOrderRule());

		for (EntityField entityField : entityFields) {
			Page<OrderRule> page = orderRuleResource.getOrderRulesPage(
				null, getFilterString(entityField, operator, orderRule1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRule1),
				(List<OrderRule>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRulesPageWithPagination() throws Exception {
		Page<OrderRule> orderRulesPage = orderRuleResource.getOrderRulesPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(orderRulesPage.getTotalCount());

		OrderRule orderRule1 = testGetOrderRulesPage_addOrderRule(
			randomOrderRule());

		OrderRule orderRule2 = testGetOrderRulesPage_addOrderRule(
			randomOrderRule());

		OrderRule orderRule3 = testGetOrderRulesPage_addOrderRule(
			randomOrderRule());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRule> page1 = orderRuleResource.getOrderRulesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(orderRule1, (List<OrderRule>)page1.getItems());

			Page<OrderRule> page2 = orderRuleResource.getOrderRulesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(orderRule2, (List<OrderRule>)page2.getItems());

			Page<OrderRule> page3 = orderRuleResource.getOrderRulesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(orderRule3, (List<OrderRule>)page3.getItems());
		}
		else {
			Page<OrderRule> page1 = orderRuleResource.getOrderRulesPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<OrderRule> orderRules1 = (List<OrderRule>)page1.getItems();

			Assert.assertEquals(
				orderRules1.toString(), totalCount + 2, orderRules1.size());

			Page<OrderRule> page2 = orderRuleResource.getOrderRulesPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRule> orderRules2 = (List<OrderRule>)page2.getItems();

			Assert.assertEquals(orderRules2.toString(), 1, orderRules2.size());

			Page<OrderRule> page3 = orderRuleResource.getOrderRulesPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(orderRule1, (List<OrderRule>)page3.getItems());
			assertContains(orderRule2, (List<OrderRule>)page3.getItems());
			assertContains(orderRule3, (List<OrderRule>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderRulesPageWithSortDateTime() throws Exception {
		testGetOrderRulesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, orderRule1, orderRule2) -> {
				BeanTestUtil.setProperty(
					orderRule1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderRulesPageWithSortDouble() throws Exception {
		testGetOrderRulesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, orderRule1, orderRule2) -> {
				BeanTestUtil.setProperty(
					orderRule1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					orderRule2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderRulesPageWithSortInteger() throws Exception {
		testGetOrderRulesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, orderRule1, orderRule2) -> {
				BeanTestUtil.setProperty(orderRule1, entityField.getName(), 0);
				BeanTestUtil.setProperty(orderRule2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderRulesPageWithSortString() throws Exception {
		testGetOrderRulesPageWithSort(
			EntityField.Type.STRING,
			(entityField, orderRule1, orderRule2) -> {
				Class<?> clazz = orderRule1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						orderRule1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						orderRule2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						orderRule1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						orderRule2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						orderRule1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						orderRule2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderRulesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, OrderRule, OrderRule, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderRule orderRule1 = randomOrderRule();
		OrderRule orderRule2 = randomOrderRule();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, orderRule1, orderRule2);
		}

		orderRule1 = testGetOrderRulesPage_addOrderRule(orderRule1);

		orderRule2 = testGetOrderRulesPage_addOrderRule(orderRule2);

		Page<OrderRule> page = orderRuleResource.getOrderRulesPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OrderRule> ascPage = orderRuleResource.getOrderRulesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(orderRule1, (List<OrderRule>)ascPage.getItems());
			assertContains(orderRule2, (List<OrderRule>)ascPage.getItems());

			Page<OrderRule> descPage = orderRuleResource.getOrderRulesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(orderRule2, (List<OrderRule>)descPage.getItems());
			assertContains(orderRule1, (List<OrderRule>)descPage.getItems());
		}
	}

	protected OrderRule testGetOrderRulesPage_addOrderRule(OrderRule orderRule)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderRulesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"orderRules",
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

		JSONObject orderRulesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/orderRules");

		long totalCount = orderRulesJSONObject.getLong("totalCount");

		OrderRule orderRule1 = testGraphQLOrderRule_addOrderRule(
			randomOrderRule());

		OrderRule orderRule2 = testGraphQLOrderRule_addOrderRule(
			randomOrderRule());

		orderRulesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/orderRules");

		Assert.assertEquals(
			totalCount + 2, orderRulesJSONObject.getLong("totalCount"));

		assertContains(
			orderRule1,
			Arrays.asList(
				OrderRuleSerDes.toDTOs(
					orderRulesJSONObject.getString("items"))));
		assertContains(
			orderRule2,
			Arrays.asList(
				OrderRuleSerDes.toDTOs(
					orderRulesJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		orderRulesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
			"JSONObject/orderRules");

		Assert.assertEquals(
			totalCount + 2, orderRulesJSONObject.getLong("totalCount"));

		assertContains(
			orderRule1,
			Arrays.asList(
				OrderRuleSerDes.toDTOs(
					orderRulesJSONObject.getString("items"))));
		assertContains(
			orderRule2,
			Arrays.asList(
				OrderRuleSerDes.toDTOs(
					orderRulesJSONObject.getString("items"))));
	}

	@Test
	public void testPatchOrderRule() throws Exception {
		OrderRule postOrderRule = testPatchOrderRule_addOrderRule();

		OrderRule randomPatchOrderRule = randomPatchOrderRule();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRule patchOrderRule = orderRuleResource.patchOrderRule(
			postOrderRule.getId(), randomPatchOrderRule);

		OrderRule expectedPatchOrderRule = postOrderRule.clone();

		BeanTestUtil.copyProperties(
			randomPatchOrderRule, expectedPatchOrderRule);

		OrderRule getOrderRule = orderRuleResource.getOrderRule(
			patchOrderRule.getId());

		assertEquals(expectedPatchOrderRule, getOrderRule);
		assertValid(getOrderRule);
	}

	protected OrderRule testPatchOrderRule_addOrderRule() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrderRuleByExternalReferenceCode() throws Exception {
		OrderRule postOrderRule =
			testPatchOrderRuleByExternalReferenceCode_addOrderRule();

		OrderRule randomPatchOrderRule = randomPatchOrderRule();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRule patchOrderRule =
			orderRuleResource.patchOrderRuleByExternalReferenceCode(
				postOrderRule.getExternalReferenceCode(), randomPatchOrderRule);

		OrderRule expectedPatchOrderRule = postOrderRule.clone();

		BeanTestUtil.copyProperties(
			randomPatchOrderRule, expectedPatchOrderRule);

		OrderRule getOrderRule =
			orderRuleResource.getOrderRuleByExternalReferenceCode(
				patchOrderRule.getExternalReferenceCode());

		assertEquals(expectedPatchOrderRule, getOrderRule);
		assertValid(getOrderRule);
	}

	protected OrderRule testPatchOrderRuleByExternalReferenceCode_addOrderRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderRule() throws Exception {
		OrderRule randomOrderRule = randomOrderRule();

		OrderRule postOrderRule = testPostOrderRule_addOrderRule(
			randomOrderRule);

		assertEquals(randomOrderRule, postOrderRule);
		assertValid(postOrderRule);
	}

	protected OrderRule testPostOrderRule_addOrderRule(OrderRule orderRule)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostOrderRule() throws Exception {
		OrderRule randomOrderRule = randomOrderRule();

		OrderRule orderRule = testGraphQLOrderRule_addOrderRule(
			randomOrderRule);

		Assert.assertTrue(equals(randomOrderRule, orderRule));
	}

	@Test
	public void testPutOrderRuleByExternalReferenceCode() throws Exception {
		OrderRule postOrderRule =
			testPutOrderRuleByExternalReferenceCode_addOrderRule();

		OrderRule randomOrderRule = randomOrderRule();

		OrderRule putOrderRule =
			orderRuleResource.putOrderRuleByExternalReferenceCode(
				postOrderRule.getExternalReferenceCode(), randomOrderRule);

		assertEquals(randomOrderRule, putOrderRule);
		assertValid(putOrderRule);

		OrderRule getOrderRule =
			orderRuleResource.getOrderRuleByExternalReferenceCode(
				putOrderRule.getExternalReferenceCode());

		assertEquals(randomOrderRule, getOrderRule);
		assertValid(getOrderRule);

		OrderRule newOrderRule =
			testPutOrderRuleByExternalReferenceCode_createOrderRule();

		putOrderRule = orderRuleResource.putOrderRuleByExternalReferenceCode(
			newOrderRule.getExternalReferenceCode(), newOrderRule);

		assertEquals(newOrderRule, putOrderRule);
		assertValid(putOrderRule);

		getOrderRule = orderRuleResource.getOrderRuleByExternalReferenceCode(
			putOrderRule.getExternalReferenceCode());

		assertEquals(newOrderRule, getOrderRule);

		Assert.assertEquals(
			newOrderRule.getExternalReferenceCode(),
			putOrderRule.getExternalReferenceCode());
	}

	protected OrderRule testPutOrderRuleByExternalReferenceCode_addOrderRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected OrderRule
			testPutOrderRuleByExternalReferenceCode_createOrderRule()
		throws Exception {

		return randomOrderRule();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderRule orderRule1 = testBatchEngineDeleteImportTask_addOrderRule();

		testBatchEngineDeleteImportTask_deleteOrderRule(
			200, orderRule1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule1.getId()));

		orderRule1 = testBatchEngineDeleteImportTask_addOrderRule();

		testBatchEngineDeleteImportTask_deleteOrderRule(
			200, null, orderRule1.getId());

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule1.getId()));

		orderRule1 = testBatchEngineDeleteImportTask_addOrderRule();
		OrderRule orderRule2 = testBatchEngineDeleteImportTask_addOrderRule();

		testBatchEngineDeleteImportTask_deleteOrderRule(
			200, orderRule2.getExternalReferenceCode(), orderRule1.getId());

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderRuleResource.getOrderRuleHttpResponse(orderRule2.getId()));

		testBatchEngineDeleteImportTask_deleteOrderRule(
			200, orderRule2.getExternalReferenceCode(), orderRule1.getId());

		assertHttpResponseStatusCode(
			404,
			orderRuleResource.getOrderRuleHttpResponse(orderRule2.getId()));
	}

	protected OrderRule testBatchEngineDeleteImportTask_addOrderRule()
		throws Exception {

		return testDeleteOrderRule_addOrderRule();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderRule(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRule",
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

	protected OrderRule testGraphQLOrderRule_addOrderRule() throws Exception {
		return testGraphQLOrderRule_addOrderRule(randomOrderRule());
	}

	protected OrderRule testGraphQLOrderRule_addOrderRule(OrderRule orderRule)
		throws Exception {

		JSONDeserializer<OrderRule> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(OrderRule.class)) {

			if (getGraphQLValue(field.get(orderRule)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(orderRule)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createOrderRule",
						new HashMap<String, Object>() {
							{
								put("orderRule", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createOrderRule"),
			OrderRule.class);
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
		OrderRule orderRule, List<OrderRule> orderRules) {

		boolean contains = false;

		for (OrderRule item : orderRules) {
			if (equals(orderRule, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderRules + " does not contain " + orderRule, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(OrderRule orderRule1, OrderRule orderRule2) {
		Assert.assertTrue(
			orderRule1 + " does not equal " + orderRule2,
			equals(orderRule1, orderRule2));
	}

	protected void assertEquals(
		List<OrderRule> orderRules1, List<OrderRule> orderRules2) {

		Assert.assertEquals(orderRules1.size(), orderRules2.size());

		for (int i = 0; i < orderRules1.size(); i++) {
			OrderRule orderRule1 = orderRules1.get(i);
			OrderRule orderRule2 = orderRules2.get(i);

			assertEquals(orderRule1, orderRule2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderRule> orderRules1, List<OrderRule> orderRules2) {

		Assert.assertEquals(orderRules1.size(), orderRules2.size());

		for (OrderRule orderRule1 : orderRules1) {
			boolean contains = false;

			for (OrderRule orderRule2 : orderRules2) {
				if (equals(orderRule1, orderRule2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderRules2 + " does not contain " + orderRule1, contains);
		}
	}

	protected void assertValid(OrderRule orderRule) throws Exception {
		boolean valid = true;

		if (orderRule.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (orderRule.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (orderRule.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (orderRule.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (orderRule.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (orderRule.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (orderRule.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (orderRule.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (orderRule.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (orderRule.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (orderRule.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderRuleAccount", additionalAssertFieldName)) {
				if (orderRule.getOrderRuleAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleAccountGroup", additionalAssertFieldName)) {

				if (orderRule.getOrderRuleAccountGroup() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderRuleChannel", additionalAssertFieldName)) {
				if (orderRule.getOrderRuleChannel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleOrderType", additionalAssertFieldName)) {

				if (orderRule.getOrderRuleOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (orderRule.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (orderRule.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeSettings", additionalAssertFieldName)) {
				if (orderRule.getTypeSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (orderRule.getWorkflowStatusInfo() == null) {
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

	protected void assertValid(Page<OrderRule> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderRule> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderRule> orderRules = page.getItems();

		int size = orderRules.size();

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
					com.liferay.headless.commerce.admin.order.dto.v1_0.
						OrderRule.class)) {

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

	protected boolean equals(OrderRule orderRule1, OrderRule orderRule2) {
		if (orderRule1 == orderRule2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderRule1.getActions(),
						(Map)orderRule2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getActive(), orderRule2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getAuthor(), orderRule2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getCreateDate(),
						orderRule2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getDescription(),
						orderRule2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getDisplayDate(),
						orderRule2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getExpirationDate(),
						orderRule2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRule1.getExternalReferenceCode(),
						orderRule2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getId(), orderRule2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getName(), orderRule2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getNeverExpire(),
						orderRule2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderRuleAccount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getOrderRuleAccount(),
						orderRule2.getOrderRuleAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleAccountGroup", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRule1.getOrderRuleAccountGroup(),
						orderRule2.getOrderRuleAccountGroup())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderRuleChannel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getOrderRuleChannel(),
						orderRule2.getOrderRuleChannel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleOrderType", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRule1.getOrderRuleOrderType(),
						orderRule2.getOrderRuleOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getPriority(), orderRule2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getType(), orderRule2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeSettings", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRule1.getTypeSettings(),
						orderRule2.getTypeSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRule1.getWorkflowStatusInfo(),
						orderRule2.getWorkflowStatusInfo())) {

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

		if (!(_orderRuleResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderRuleResource;

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
		EntityField entityField, String operator, OrderRule orderRule) {

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

		if (entityFieldName.equals("author")) {
			Object object = orderRule.getAuthor();

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

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = orderRule.getCreateDate();

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

				sb.append(_format.format(orderRule.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = orderRule.getDescription();

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

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = orderRule.getDisplayDate();

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

				sb.append(_format.format(orderRule.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = orderRule.getExpirationDate();

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

				sb.append(_format.format(orderRule.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = orderRule.getExternalReferenceCode();

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
			Object object = orderRule.getName();

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

		if (entityFieldName.equals("orderRuleAccount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleAccountGroup")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleChannel")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleOrderType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(orderRule.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("type")) {
			Object object = orderRule.getType();

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

		if (entityFieldName.equals("typeSettings")) {
			Object object = orderRule.getTypeSettings();

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

	protected OrderRule randomOrderRule() throws Exception {
		return new OrderRule() {
			{
				active = RandomTestUtil.randomBoolean();
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				neverExpire = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				typeSettings = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected OrderRule randomIrrelevantOrderRule() throws Exception {
		OrderRule randomIrrelevantOrderRule = randomOrderRule();

		return randomIrrelevantOrderRule;
	}

	protected OrderRule randomPatchOrderRule() throws Exception {
		return randomOrderRule();
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

	protected OrderRuleResource orderRuleResource;
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
		LogFactoryUtil.getLog(BaseOrderRuleResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.order.resource.v1_0.
			OrderRuleResource _orderRuleResource;

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