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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderType;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderTypeResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderTypeSerDes;
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
public abstract class BaseOrderTypeResourceTestCase {

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

		_orderTypeResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderTypeResource = OrderTypeResource.builder(
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

		OrderType orderType1 = randomOrderType();

		String json = objectMapper.writeValueAsString(orderType1);

		OrderType orderType2 = OrderTypeSerDes.toDTO(json);

		Assert.assertTrue(equals(orderType1, orderType2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderType orderType = randomOrderType();

		String json1 = objectMapper.writeValueAsString(orderType);
		String json2 = OrderTypeSerDes.toJSON(orderType);

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

		OrderType orderType = randomOrderType();

		orderType.setExternalReferenceCode(regex);

		String json = OrderTypeSerDes.toJSON(orderType);

		Assert.assertFalse(json.contains(regex));

		orderType = OrderTypeSerDes.toDTO(json);

		Assert.assertEquals(regex, orderType.getExternalReferenceCode());
	}

	@Test
	public void testDeleteOrderType() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderType orderType = testDeleteOrderType_addOrderType();

		assertHttpResponseStatusCode(
			204,
			orderTypeResource.deleteOrderTypeHttpResponse(orderType.getId()));

		assertHttpResponseStatusCode(
			404, orderTypeResource.getOrderTypeHttpResponse(orderType.getId()));
		assertHttpResponseStatusCode(
			404, orderTypeResource.getOrderTypeHttpResponse(0L));
	}

	protected OrderType testDeleteOrderType_addOrderType() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderType() throws Exception {

		// No namespace

		OrderType orderType1 = testGraphQLDeleteOrderType_addOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderType",
						new HashMap<String, Object>() {
							{
								put("id", orderType1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderType"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderType",
					new HashMap<String, Object>() {
						{
							put("id", orderType1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderType orderType2 = testGraphQLDeleteOrderType_addOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderType",
							new HashMap<String, Object>() {
								{
									put("id", orderType2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderType"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderType",
						new HashMap<String, Object>() {
							{
								put("id", orderType2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderType testGraphQLDeleteOrderType_addOrderType()
		throws Exception {

		return testGraphQLOrderType_addOrderType();
	}

	@Test
	public void testDeleteOrderTypeBatch() throws Exception {
		OrderType orderType1 = testDeleteOrderTypeBatch_addOrderType();

		testDeleteOrderTypeBatch_deleteOrderType(
			202, orderType1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType1.getId()));

		orderType1 = testDeleteOrderTypeBatch_addOrderType();

		testDeleteOrderTypeBatch_deleteOrderType(202, null, orderType1.getId());

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType1.getId()));

		orderType1 = testDeleteOrderTypeBatch_addOrderType();
		OrderType orderType2 = testDeleteOrderTypeBatch_addOrderType();

		testDeleteOrderTypeBatch_deleteOrderType(
			202, orderType2.getExternalReferenceCode(), orderType1.getId());

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderTypeResource.getOrderTypeHttpResponse(orderType2.getId()));

		testDeleteOrderTypeBatch_deleteOrderType(
			202, orderType2.getExternalReferenceCode(), orderType1.getId());

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType2.getId()));
	}

	protected OrderType testDeleteOrderTypeBatch_addOrderType()
		throws Exception {

		return testDeleteOrderType_addOrderType();
	}

	protected void testDeleteOrderTypeBatch_deleteOrderType(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderTypeResource.deleteOrderTypeBatchHttpResponse(
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
	public void testDeleteOrderTypeByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderType orderType =
			testDeleteOrderTypeByExternalReferenceCode_addOrderType();

		assertHttpResponseStatusCode(
			204,
			orderTypeResource.
				deleteOrderTypeByExternalReferenceCodeHttpResponse(
					orderType.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeByExternalReferenceCodeHttpResponse(
				orderType.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected OrderType
			testDeleteOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderTypeByExternalReferenceCode()
		throws Exception {

		// No namespace

		OrderType orderType1 =
			testGraphQLDeleteOrderTypeByExternalReferenceCode_addOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderTypeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderType1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrderTypeByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderTypeByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + orderType1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderType orderType2 =
			testGraphQLDeleteOrderTypeByExternalReferenceCode_addOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderTypeByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											orderType2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderTypeByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderTypeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderType2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderType
			testGraphQLDeleteOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		return testGraphQLOrderType_addOrderType();
	}

	@Test
	public void testGetOrderRuleOrderTypeOrderType() throws Exception {
		OrderType postOrderType =
			testGetOrderRuleOrderTypeOrderType_addOrderType();

		OrderType getOrderType =
			orderTypeResource.getOrderRuleOrderTypeOrderType(
				testGetOrderRuleOrderTypeOrderType_getOrderRuleOrderTypeId());

		assertEquals(postOrderType, getOrderType);
		assertValid(getOrderType);
	}

	protected OrderType testGetOrderRuleOrderTypeOrderType_addOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleOrderTypeOrderType_getOrderRuleOrderTypeId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderRuleOrderTypeOrderType() throws Exception {
		OrderType orderType =
			testGraphQLGetOrderRuleOrderTypeOrderType_addOrderType();

		// No namespace

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderRuleOrderTypeOrderType",
								new HashMap<String, Object>() {
									{
										put(
											"orderRuleOrderTypeId",
											testGraphQLGetOrderRuleOrderTypeOrderType_getOrderRuleOrderTypeId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/orderRuleOrderTypeOrderType"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderRuleOrderTypeOrderType",
									new HashMap<String, Object>() {
										{
											put(
												"orderRuleOrderTypeId",
												testGraphQLGetOrderRuleOrderTypeOrderType_getOrderRuleOrderTypeId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderRuleOrderTypeOrderType"))));
	}

	protected Long
			testGraphQLGetOrderRuleOrderTypeOrderType_getOrderRuleOrderTypeId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderRuleOrderTypeOrderTypeNotFound()
		throws Exception {

		Long irrelevantOrderRuleOrderTypeId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderRuleOrderTypeOrderType",
						new HashMap<String, Object>() {
							{
								put(
									"orderRuleOrderTypeId",
									irrelevantOrderRuleOrderTypeId);
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
							"orderRuleOrderTypeOrderType",
							new HashMap<String, Object>() {
								{
									put(
										"orderRuleOrderTypeId",
										irrelevantOrderRuleOrderTypeId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OrderType testGraphQLGetOrderRuleOrderTypeOrderType_addOrderType()
		throws Exception {

		return testGraphQLOrderType_addOrderType();
	}

	@Test
	public void testGetOrderType() throws Exception {
		OrderType postOrderType = testGetOrderType_addOrderType();

		OrderType getOrderType = orderTypeResource.getOrderType(
			postOrderType.getId());

		assertEquals(postOrderType, getOrderType);
		assertValid(getOrderType);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		OrderType postOrderType = testGetOrderType_addOrderType();

		OrderType getOrderType = orderTypeResource.getOrderType(
			postOrderType.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderType"
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

		Object item = vulcanCRUDItemDelegate.getItem(postOrderType.getId());

		assertEquals(getOrderType, OrderTypeSerDes.toDTO(item.toString()));
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

	protected OrderType testGetOrderType_addOrderType() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderType() throws Exception {
		OrderType orderType = testGraphQLGetOrderType_addOrderType();

		// No namespace

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderType",
								new HashMap<String, Object>() {
									{
										put("id", orderType.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/orderType"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderType",
									new HashMap<String, Object>() {
										{
											put("id", orderType.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderType"))));
	}

	@Test
	public void testGraphQLGetOrderTypeNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderType",
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
							"orderType",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OrderType testGraphQLGetOrderType_addOrderType()
		throws Exception {

		return testGraphQLOrderType_addOrderType();
	}

	@Test
	public void testGetOrderTypeByExternalReferenceCode() throws Exception {
		OrderType postOrderType =
			testGetOrderTypeByExternalReferenceCode_addOrderType();

		OrderType getOrderType =
			orderTypeResource.getOrderTypeByExternalReferenceCode(
				postOrderType.getExternalReferenceCode());

		assertEquals(postOrderType, getOrderType);
		assertValid(getOrderType);
	}

	protected OrderType testGetOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderTypeByExternalReferenceCode()
		throws Exception {

		OrderType orderType =
			testGraphQLGetOrderTypeByExternalReferenceCode_addOrderType();

		// No namespace

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderTypeByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												orderType.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/orderTypeByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderTypeByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													orderType.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderTypeByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOrderTypeByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderTypeByExternalReferenceCode",
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
							"orderTypeByExternalReferenceCode",
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

	protected OrderType
			testGraphQLGetOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		return testGraphQLOrderType_addOrderType();
	}

	@Test
	public void testGetOrderTypesPage() throws Exception {
		Page<OrderType> page = orderTypeResource.getOrderTypesPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		OrderType orderType1 = testGetOrderTypesPage_addOrderType(
			randomOrderType());

		OrderType orderType2 = testGetOrderTypesPage_addOrderType(
			randomOrderType());

		page = orderTypeResource.getOrderTypesPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(orderType1, (List<OrderType>)page.getItems());
		assertContains(orderType2, (List<OrderType>)page.getItems());
		assertValid(page, testGetOrderTypesPage_getExpectedActions());

		orderTypeResource.deleteOrderType(orderType1.getId());

		orderTypeResource.deleteOrderType(orderType2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderTypesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderTypesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderType orderType1 = randomOrderType();

		orderType1 = testGetOrderTypesPage_addOrderType(orderType1);

		for (EntityField entityField : entityFields) {
			Page<OrderType> page = orderTypeResource.getOrderTypesPage(
				null, getFilterString(entityField, "between", orderType1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderType1),
				(List<OrderType>)page.getItems());
		}
	}

	@Test
	public void testGetOrderTypesPageWithFilterDoubleEquals() throws Exception {
		testGetOrderTypesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderTypesPageWithFilterStringContains()
		throws Exception {

		testGetOrderTypesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderTypesPageWithFilterStringEquals() throws Exception {
		testGetOrderTypesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderTypesPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderTypesPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderTypesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderType orderType1 = testGetOrderTypesPage_addOrderType(
			randomOrderType());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderType orderType2 = testGetOrderTypesPage_addOrderType(
			randomOrderType());

		for (EntityField entityField : entityFields) {
			Page<OrderType> page = orderTypeResource.getOrderTypesPage(
				null, getFilterString(entityField, operator, orderType1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderType1),
				(List<OrderType>)page.getItems());
		}
	}

	@Test
	public void testGetOrderTypesPageWithPagination() throws Exception {
		Page<OrderType> orderTypesPage = orderTypeResource.getOrderTypesPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(orderTypesPage.getTotalCount());

		OrderType orderType1 = testGetOrderTypesPage_addOrderType(
			randomOrderType());

		OrderType orderType2 = testGetOrderTypesPage_addOrderType(
			randomOrderType());

		OrderType orderType3 = testGetOrderTypesPage_addOrderType(
			randomOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderType> page1 = orderTypeResource.getOrderTypesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(orderType1, (List<OrderType>)page1.getItems());

			Page<OrderType> page2 = orderTypeResource.getOrderTypesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(orderType2, (List<OrderType>)page2.getItems());

			Page<OrderType> page3 = orderTypeResource.getOrderTypesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(orderType3, (List<OrderType>)page3.getItems());
		}
		else {
			Page<OrderType> page1 = orderTypeResource.getOrderTypesPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<OrderType> orderTypes1 = (List<OrderType>)page1.getItems();

			Assert.assertEquals(
				orderTypes1.toString(), totalCount + 2, orderTypes1.size());

			Page<OrderType> page2 = orderTypeResource.getOrderTypesPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderType> orderTypes2 = (List<OrderType>)page2.getItems();

			Assert.assertEquals(orderTypes2.toString(), 1, orderTypes2.size());

			Page<OrderType> page3 = orderTypeResource.getOrderTypesPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(orderType1, (List<OrderType>)page3.getItems());
			assertContains(orderType2, (List<OrderType>)page3.getItems());
			assertContains(orderType3, (List<OrderType>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderTypesPageWithSortDateTime() throws Exception {
		testGetOrderTypesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, orderType1, orderType2) -> {
				BeanTestUtil.setProperty(
					orderType1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderTypesPageWithSortDouble() throws Exception {
		testGetOrderTypesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, orderType1, orderType2) -> {
				BeanTestUtil.setProperty(
					orderType1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					orderType2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderTypesPageWithSortInteger() throws Exception {
		testGetOrderTypesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, orderType1, orderType2) -> {
				BeanTestUtil.setProperty(orderType1, entityField.getName(), 0);
				BeanTestUtil.setProperty(orderType2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderTypesPageWithSortString() throws Exception {
		testGetOrderTypesPageWithSort(
			EntityField.Type.STRING,
			(entityField, orderType1, orderType2) -> {
				Class<?> clazz = orderType1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						orderType1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						orderType2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						orderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						orderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						orderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						orderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderTypesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, OrderType, OrderType, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderType orderType1 = randomOrderType();
		OrderType orderType2 = randomOrderType();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, orderType1, orderType2);
		}

		orderType1 = testGetOrderTypesPage_addOrderType(orderType1);

		orderType2 = testGetOrderTypesPage_addOrderType(orderType2);

		Page<OrderType> page = orderTypeResource.getOrderTypesPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OrderType> ascPage = orderTypeResource.getOrderTypesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(orderType1, (List<OrderType>)ascPage.getItems());
			assertContains(orderType2, (List<OrderType>)ascPage.getItems());

			Page<OrderType> descPage = orderTypeResource.getOrderTypesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(orderType2, (List<OrderType>)descPage.getItems());
			assertContains(orderType1, (List<OrderType>)descPage.getItems());
		}
	}

	protected OrderType testGetOrderTypesPage_addOrderType(OrderType orderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderTypesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"orderTypes",
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

		JSONObject orderTypesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/orderTypes");

		long totalCount = orderTypesJSONObject.getLong("totalCount");

		OrderType orderType1 = testGraphQLOrderType_addOrderType(
			randomOrderType());

		OrderType orderType2 = testGraphQLOrderType_addOrderType(
			randomOrderType());

		orderTypesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/orderTypes");

		Assert.assertEquals(
			totalCount + 2, orderTypesJSONObject.getLong("totalCount"));

		assertContains(
			orderType1,
			Arrays.asList(
				OrderTypeSerDes.toDTOs(
					orderTypesJSONObject.getString("items"))));
		assertContains(
			orderType2,
			Arrays.asList(
				OrderTypeSerDes.toDTOs(
					orderTypesJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		orderTypesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
			"JSONObject/orderTypes");

		Assert.assertEquals(
			totalCount + 2, orderTypesJSONObject.getLong("totalCount"));

		assertContains(
			orderType1,
			Arrays.asList(
				OrderTypeSerDes.toDTOs(
					orderTypesJSONObject.getString("items"))));
		assertContains(
			orderType2,
			Arrays.asList(
				OrderTypeSerDes.toDTOs(
					orderTypesJSONObject.getString("items"))));
	}

	@Test
	public void testGetTermOrderTypeOrderType() throws Exception {
		OrderType postOrderType = testGetTermOrderTypeOrderType_addOrderType();

		OrderType getOrderType = orderTypeResource.getTermOrderTypeOrderType(
			testGetTermOrderTypeOrderType_getTermOrderTypeId());

		assertEquals(postOrderType, getOrderType);
		assertValid(getOrderType);
	}

	protected OrderType testGetTermOrderTypeOrderType_addOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetTermOrderTypeOrderType_getTermOrderTypeId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTermOrderTypeOrderType() throws Exception {
		OrderType orderType =
			testGraphQLGetTermOrderTypeOrderType_addOrderType();

		// No namespace

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"termOrderTypeOrderType",
								new HashMap<String, Object>() {
									{
										put(
											"termOrderTypeId",
											testGraphQLGetTermOrderTypeOrderType_getTermOrderTypeId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/termOrderTypeOrderType"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderType,
				OrderTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"termOrderTypeOrderType",
									new HashMap<String, Object>() {
										{
											put(
												"termOrderTypeId",
												testGraphQLGetTermOrderTypeOrderType_getTermOrderTypeId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/termOrderTypeOrderType"))));
	}

	protected Long testGraphQLGetTermOrderTypeOrderType_getTermOrderTypeId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTermOrderTypeOrderTypeNotFound()
		throws Exception {

		Long irrelevantTermOrderTypeId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"termOrderTypeOrderType",
						new HashMap<String, Object>() {
							{
								put(
									"termOrderTypeId",
									irrelevantTermOrderTypeId);
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
							"termOrderTypeOrderType",
							new HashMap<String, Object>() {
								{
									put(
										"termOrderTypeId",
										irrelevantTermOrderTypeId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OrderType testGraphQLGetTermOrderTypeOrderType_addOrderType()
		throws Exception {

		return testGraphQLOrderType_addOrderType();
	}

	@Test
	public void testPatchOrderType() throws Exception {
		OrderType postOrderType = testPatchOrderType_addOrderType();

		OrderType randomPatchOrderType = randomPatchOrderType();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderType patchOrderType = orderTypeResource.patchOrderType(
			postOrderType.getId(), randomPatchOrderType);

		OrderType expectedPatchOrderType = postOrderType.clone();

		BeanTestUtil.copyProperties(
			randomPatchOrderType, expectedPatchOrderType);

		OrderType getOrderType = orderTypeResource.getOrderType(
			patchOrderType.getId());

		assertEquals(expectedPatchOrderType, getOrderType);
		assertValid(getOrderType);
	}

	protected OrderType testPatchOrderType_addOrderType() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrderTypeByExternalReferenceCode() throws Exception {
		OrderType postOrderType =
			testPatchOrderTypeByExternalReferenceCode_addOrderType();

		OrderType randomPatchOrderType = randomPatchOrderType();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderType patchOrderType =
			orderTypeResource.patchOrderTypeByExternalReferenceCode(
				postOrderType.getExternalReferenceCode(), randomPatchOrderType);

		OrderType expectedPatchOrderType = postOrderType.clone();

		BeanTestUtil.copyProperties(
			randomPatchOrderType, expectedPatchOrderType);

		OrderType getOrderType =
			orderTypeResource.getOrderTypeByExternalReferenceCode(
				patchOrderType.getExternalReferenceCode());

		assertEquals(expectedPatchOrderType, getOrderType);
		assertValid(getOrderType);
	}

	protected OrderType testPatchOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderType() throws Exception {
		OrderType randomOrderType = randomOrderType();

		OrderType postOrderType = testPostOrderType_addOrderType(
			randomOrderType);

		assertEquals(randomOrderType, postOrderType);
		assertValid(postOrderType);
	}

	protected OrderType testPostOrderType_addOrderType(OrderType orderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostOrderType() throws Exception {
		OrderType randomOrderType = randomOrderType();

		OrderType orderType = testGraphQLOrderType_addOrderType(
			randomOrderType);

		Assert.assertTrue(equals(randomOrderType, orderType));
	}

	@Test
	public void testPutOrderTypeByExternalReferenceCode() throws Exception {
		OrderType postOrderType =
			testPutOrderTypeByExternalReferenceCode_addOrderType();

		OrderType randomOrderType = randomOrderType();

		OrderType putOrderType =
			orderTypeResource.putOrderTypeByExternalReferenceCode(
				postOrderType.getExternalReferenceCode(), randomOrderType);

		assertEquals(randomOrderType, putOrderType);
		assertValid(putOrderType);

		OrderType getOrderType =
			orderTypeResource.getOrderTypeByExternalReferenceCode(
				putOrderType.getExternalReferenceCode());

		assertEquals(randomOrderType, getOrderType);
		assertValid(getOrderType);

		OrderType newOrderType =
			testPutOrderTypeByExternalReferenceCode_createOrderType();

		putOrderType = orderTypeResource.putOrderTypeByExternalReferenceCode(
			newOrderType.getExternalReferenceCode(), newOrderType);

		assertEquals(newOrderType, putOrderType);
		assertValid(putOrderType);

		getOrderType = orderTypeResource.getOrderTypeByExternalReferenceCode(
			putOrderType.getExternalReferenceCode());

		assertEquals(newOrderType, getOrderType);

		Assert.assertEquals(
			newOrderType.getExternalReferenceCode(),
			putOrderType.getExternalReferenceCode());
	}

	protected OrderType testPutOrderTypeByExternalReferenceCode_addOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected OrderType
			testPutOrderTypeByExternalReferenceCode_createOrderType()
		throws Exception {

		return randomOrderType();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderType orderType1 = testBatchEngineDeleteImportTask_addOrderType();

		testBatchEngineDeleteImportTask_deleteOrderType(
			200, orderType1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType1.getId()));

		orderType1 = testBatchEngineDeleteImportTask_addOrderType();

		testBatchEngineDeleteImportTask_deleteOrderType(
			200, null, orderType1.getId());

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType1.getId()));

		orderType1 = testBatchEngineDeleteImportTask_addOrderType();
		OrderType orderType2 = testBatchEngineDeleteImportTask_addOrderType();

		testBatchEngineDeleteImportTask_deleteOrderType(
			200, orderType2.getExternalReferenceCode(), orderType1.getId());

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderTypeResource.getOrderTypeHttpResponse(orderType2.getId()));

		testBatchEngineDeleteImportTask_deleteOrderType(
			200, orderType2.getExternalReferenceCode(), orderType1.getId());

		assertHttpResponseStatusCode(
			404,
			orderTypeResource.getOrderTypeHttpResponse(orderType2.getId()));
	}

	protected OrderType testBatchEngineDeleteImportTask_addOrderType()
		throws Exception {

		return testDeleteOrderType_addOrderType();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderType(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderType",
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

	protected OrderType testGraphQLOrderType_addOrderType() throws Exception {
		return testGraphQLOrderType_addOrderType(randomOrderType());
	}

	protected OrderType testGraphQLOrderType_addOrderType(OrderType orderType)
		throws Exception {

		JSONDeserializer<OrderType> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(OrderType.class)) {

			if (getGraphQLValue(field.get(orderType)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(orderType)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createOrderType",
						new HashMap<String, Object>() {
							{
								put("orderType", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createOrderType"),
			OrderType.class);
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
		OrderType orderType, List<OrderType> orderTypes) {

		boolean contains = false;

		for (OrderType item : orderTypes) {
			if (equals(orderType, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderTypes + " does not contain " + orderType, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(OrderType orderType1, OrderType orderType2) {
		Assert.assertTrue(
			orderType1 + " does not equal " + orderType2,
			equals(orderType1, orderType2));
	}

	protected void assertEquals(
		List<OrderType> orderTypes1, List<OrderType> orderTypes2) {

		Assert.assertEquals(orderTypes1.size(), orderTypes2.size());

		for (int i = 0; i < orderTypes1.size(); i++) {
			OrderType orderType1 = orderTypes1.get(i);
			OrderType orderType2 = orderTypes2.get(i);

			assertEquals(orderType1, orderType2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderType> orderTypes1, List<OrderType> orderTypes2) {

		Assert.assertEquals(orderTypes1.size(), orderTypes2.size());

		for (OrderType orderType1 : orderTypes1) {
			boolean contains = false;

			for (OrderType orderType2 : orderTypes2) {
				if (equals(orderType1, orderType2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderTypes2 + " does not contain " + orderType1, contains);
		}
	}

	protected void assertValid(OrderType orderType) throws Exception {
		boolean valid = true;

		if (orderType.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (orderType.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (orderType.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (orderType.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (orderType.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (orderType.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayOrder", additionalAssertFieldName)) {
				if (orderType.getDisplayOrder() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (orderType.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (orderType.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (orderType.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (orderType.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeChannels", additionalAssertFieldName)) {

				if (orderType.getOrderTypeChannels() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (orderType.getWorkflowStatusInfo() == null) {
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

	protected void assertValid(Page<OrderType> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderType> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderType> orderTypes = page.getItems();

		int size = orderTypes.size();

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
						OrderType.class)) {

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

	protected boolean equals(OrderType orderType1, OrderType orderType2) {
		if (orderType1 == orderType2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderType1.getActions(),
						(Map)orderType2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderType1.getActive(), orderType2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderType1.getCustomFields(),
						(Map)orderType2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderType1.getDescription(),
						(Map)orderType2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderType1.getDisplayDate(),
						orderType2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayOrder", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderType1.getDisplayOrder(),
						orderType2.getDisplayOrder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderType1.getExpirationDate(),
						orderType2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderType1.getExternalReferenceCode(),
						orderType2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderType1.getId(), orderType2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderType1.getName(), (Map)orderType2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderType1.getNeverExpire(),
						orderType2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeChannels", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderType1.getOrderTypeChannels(),
						orderType2.getOrderTypeChannels())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderType1.getWorkflowStatusInfo(),
						orderType2.getWorkflowStatusInfo())) {

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

		if (!(_orderTypeResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderTypeResource;

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
		EntityField entityField, String operator, OrderType orderType) {

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = orderType.getDisplayDate();

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

				sb.append(_format.format(orderType.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("displayOrder")) {
			sb.append(String.valueOf(orderType.getDisplayOrder()));

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = orderType.getExpirationDate();

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

				sb.append(_format.format(orderType.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = orderType.getExternalReferenceCode();

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

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderTypeChannels")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

	protected OrderType randomOrderType() throws Exception {
		return new OrderType() {
			{
				active = RandomTestUtil.randomBoolean();
				displayDate = RandomTestUtil.nextDate();
				displayOrder = RandomTestUtil.randomInt();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				neverExpire = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected OrderType randomIrrelevantOrderType() throws Exception {
		OrderType randomIrrelevantOrderType = randomOrderType();

		return randomIrrelevantOrderType;
	}

	protected OrderType randomPatchOrderType() throws Exception {
		return randomOrderType();
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

	protected OrderTypeResource orderTypeResource;
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
		LogFactoryUtil.getLog(BaseOrderTypeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.order.resource.v1_0.
			OrderTypeResource _orderTypeResource;

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