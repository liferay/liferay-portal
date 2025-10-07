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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderSerDes;
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
public abstract class BaseOrderResourceTestCase {

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

		_orderResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderResource = OrderResource.builder(
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

		Order order1 = randomOrder();

		String json = objectMapper.writeValueAsString(order1);

		Order order2 = OrderSerDes.toDTO(json);

		Assert.assertTrue(equals(order1, order2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Order order = randomOrder();

		String json1 = objectMapper.writeValueAsString(order);
		String json2 = OrderSerDes.toJSON(order);

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

		Order order = randomOrder();

		order.setAccountExternalReferenceCode(regex);
		order.setAdvanceStatus(regex);
		order.setAuthor(regex);
		order.setBillingAddressExternalReferenceCode(regex);
		order.setChannelExternalReferenceCode(regex);
		order.setCouponCode(regex);
		order.setCreatorEmailAddress(regex);
		order.setCurrencyCode(regex);
		order.setCurrencyExternalReferenceCode(regex);
		order.setDeliveryTermDescription(regex);
		order.setDeliveryTermExternalReferenceCode(regex);
		order.setDeliveryTermName(regex);
		order.setExternalReferenceCode(regex);
		order.setName(regex);
		order.setOrderTypeExternalReferenceCode(regex);
		order.setPaymentMethod(regex);
		order.setPaymentTermDescription(regex);
		order.setPaymentTermExternalReferenceCode(regex);
		order.setPaymentTermName(regex);
		order.setPrintedNote(regex);
		order.setPurchaseOrderNumber(regex);
		order.setShippingAddressExternalReferenceCode(regex);
		order.setShippingAmountFormatted(regex);
		order.setShippingDiscountAmountFormatted(regex);
		order.setShippingDiscountWithTaxAmountFormatted(regex);
		order.setShippingMethod(regex);
		order.setShippingOption(regex);
		order.setShippingWithTaxAmountFormatted(regex);
		order.setSubtotalDiscountAmountFormatted(regex);
		order.setSubtotalDiscountWithTaxAmountFormatted(regex);
		order.setSubtotalFormatted(regex);
		order.setSubtotalWithTaxAmountFormatted(regex);
		order.setTaxAmountFormatted(regex);
		order.setTotalDiscountAmountFormatted(regex);
		order.setTotalDiscountWithTaxAmountFormatted(regex);
		order.setTotalFormatted(regex);
		order.setTotalWithTaxAmountFormatted(regex);
		order.setTransactionId(regex);

		String json = OrderSerDes.toJSON(order);

		Assert.assertFalse(json.contains(regex));

		order = OrderSerDes.toDTO(json);

		Assert.assertEquals(regex, order.getAccountExternalReferenceCode());
		Assert.assertEquals(regex, order.getAdvanceStatus());
		Assert.assertEquals(regex, order.getAuthor());
		Assert.assertEquals(
			regex, order.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(regex, order.getChannelExternalReferenceCode());
		Assert.assertEquals(regex, order.getCouponCode());
		Assert.assertEquals(regex, order.getCreatorEmailAddress());
		Assert.assertEquals(regex, order.getCurrencyCode());
		Assert.assertEquals(regex, order.getCurrencyExternalReferenceCode());
		Assert.assertEquals(regex, order.getDeliveryTermDescription());
		Assert.assertEquals(
			regex, order.getDeliveryTermExternalReferenceCode());
		Assert.assertEquals(regex, order.getDeliveryTermName());
		Assert.assertEquals(regex, order.getExternalReferenceCode());
		Assert.assertEquals(regex, order.getName());
		Assert.assertEquals(regex, order.getOrderTypeExternalReferenceCode());
		Assert.assertEquals(regex, order.getPaymentMethod());
		Assert.assertEquals(regex, order.getPaymentTermDescription());
		Assert.assertEquals(regex, order.getPaymentTermExternalReferenceCode());
		Assert.assertEquals(regex, order.getPaymentTermName());
		Assert.assertEquals(regex, order.getPrintedNote());
		Assert.assertEquals(regex, order.getPurchaseOrderNumber());
		Assert.assertEquals(
			regex, order.getShippingAddressExternalReferenceCode());
		Assert.assertEquals(regex, order.getShippingAmountFormatted());
		Assert.assertEquals(regex, order.getShippingDiscountAmountFormatted());
		Assert.assertEquals(
			regex, order.getShippingDiscountWithTaxAmountFormatted());
		Assert.assertEquals(regex, order.getShippingMethod());
		Assert.assertEquals(regex, order.getShippingOption());
		Assert.assertEquals(regex, order.getShippingWithTaxAmountFormatted());
		Assert.assertEquals(regex, order.getSubtotalDiscountAmountFormatted());
		Assert.assertEquals(
			regex, order.getSubtotalDiscountWithTaxAmountFormatted());
		Assert.assertEquals(regex, order.getSubtotalFormatted());
		Assert.assertEquals(regex, order.getSubtotalWithTaxAmountFormatted());
		Assert.assertEquals(regex, order.getTaxAmountFormatted());
		Assert.assertEquals(regex, order.getTotalDiscountAmountFormatted());
		Assert.assertEquals(
			regex, order.getTotalDiscountWithTaxAmountFormatted());
		Assert.assertEquals(regex, order.getTotalFormatted());
		Assert.assertEquals(regex, order.getTotalWithTaxAmountFormatted());
		Assert.assertEquals(regex, order.getTransactionId());
	}

	@Test
	public void testDeleteOrder() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Order order = testDeleteOrder_addOrder();

		assertHttpResponseStatusCode(
			204, orderResource.deleteOrderHttpResponse(order.getId()));

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order.getId()));
		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(0L));
	}

	protected Order testDeleteOrder_addOrder() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrder() throws Exception {

		// No namespace

		Order order1 = testGraphQLDeleteOrder_addOrder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrder",
						new HashMap<String, Object>() {
							{
								put("id", order1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOrder"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"order",
					new HashMap<String, Object>() {
						{
							put("id", order1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Order order2 = testGraphQLDeleteOrder_addOrder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrder",
							new HashMap<String, Object>() {
								{
									put("id", order2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrder"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"order",
						new HashMap<String, Object>() {
							{
								put("id", order2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Order testGraphQLDeleteOrder_addOrder() throws Exception {
		return testGraphQLOrder_addOrder();
	}

	@Test
	public void testDeleteOrderBatch() throws Exception {
		Order order1 = testDeleteOrderBatch_addOrder();

		testDeleteOrderBatch_deleteOrder(
			202, order1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order1.getId()));

		order1 = testDeleteOrderBatch_addOrder();

		testDeleteOrderBatch_deleteOrder(202, null, order1.getId());

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order1.getId()));

		order1 = testDeleteOrderBatch_addOrder();
		Order order2 = testDeleteOrderBatch_addOrder();

		testDeleteOrderBatch_deleteOrder(
			202, order2.getExternalReferenceCode(), order1.getId());

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order1.getId()));
		assertHttpResponseStatusCode(
			200, orderResource.getOrderHttpResponse(order2.getId()));

		testDeleteOrderBatch_deleteOrder(
			202, order2.getExternalReferenceCode(), order1.getId());

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order2.getId()));
	}

	protected Order testDeleteOrderBatch_addOrder() throws Exception {
		return testDeleteOrder_addOrder();
	}

	protected void testDeleteOrderBatch_deleteOrder(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderResource.deleteOrderBatchHttpResponse(
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
	public void testDeleteOrderByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Order order = testDeleteOrderByExternalReferenceCode_addOrder();

		assertHttpResponseStatusCode(
			204,
			orderResource.deleteOrderByExternalReferenceCodeHttpResponse(
				order.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			orderResource.getOrderByExternalReferenceCodeHttpResponse(
				order.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			orderResource.getOrderByExternalReferenceCodeHttpResponse("-"));
	}

	protected Order testDeleteOrderByExternalReferenceCode_addOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderByExternalReferenceCode()
		throws Exception {

		// No namespace

		Order order1 = testGraphQLDeleteOrderByExternalReferenceCode_addOrder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + order1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrderByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + order1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Order order2 = testGraphQLDeleteOrderByExternalReferenceCode_addOrder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											order2.getExternalReferenceCode() +
												"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + order2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Order testGraphQLDeleteOrderByExternalReferenceCode_addOrder()
		throws Exception {

		return testGraphQLOrder_addOrder();
	}

	@Test
	public void testGetOrder() throws Exception {
		Order postOrder = testGetOrder_addOrder();

		Order getOrder = orderResource.getOrder(postOrder.getId());

		assertEquals(postOrder, getOrder);
		assertValid(getOrder);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Order postOrder = testGetOrder_addOrder();

		Order getOrder = orderResource.getOrder(postOrder.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.order.dto.v1_0.Order"
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

		Object item = vulcanCRUDItemDelegate.getItem(postOrder.getId());

		assertEquals(getOrder, OrderSerDes.toDTO(item.toString()));
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

	protected Order testGetOrder_addOrder() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrder() throws Exception {
		Order order = testGraphQLGetOrder_addOrder();

		// No namespace

		Assert.assertTrue(
			equals(
				order,
				OrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"order",
								new HashMap<String, Object>() {
									{
										put("id", order.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/order"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				order,
				OrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"order",
									new HashMap<String, Object>() {
										{
											put("id", order.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/order"))));
	}

	@Test
	public void testGraphQLGetOrderNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"order",
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
							"order",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Order testGraphQLGetOrder_addOrder() throws Exception {
		return testGraphQLOrder_addOrder();
	}

	@Test
	public void testGetOrderByExternalReferenceCode() throws Exception {
		Order postOrder = testGetOrderByExternalReferenceCode_addOrder();

		Order getOrder = orderResource.getOrderByExternalReferenceCode(
			postOrder.getExternalReferenceCode());

		assertEquals(postOrder, getOrder);
		assertValid(getOrder);
	}

	protected Order testGetOrderByExternalReferenceCode_addOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderByExternalReferenceCode() throws Exception {
		Order order = testGraphQLGetOrderByExternalReferenceCode_addOrder();

		// No namespace

		Assert.assertTrue(
			equals(
				order,
				OrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												order.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/orderByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				order,
				OrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													order.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOrderByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderByExternalReferenceCode",
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
							"orderByExternalReferenceCode",
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

	protected Order testGraphQLGetOrderByExternalReferenceCode_addOrder()
		throws Exception {

		return testGraphQLOrder_addOrder();
	}

	@Test
	public void testGetOrdersPage() throws Exception {
		Page<Order> page = orderResource.getOrdersPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Order order1 = testGetOrdersPage_addOrder(randomOrder());

		Order order2 = testGetOrdersPage_addOrder(randomOrder());

		page = orderResource.getOrdersPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(order1, (List<Order>)page.getItems());
		assertContains(order2, (List<Order>)page.getItems());
		assertValid(page, testGetOrdersPage_getExpectedActions());

		orderResource.deleteOrder(order1.getId());

		orderResource.deleteOrder(order2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrdersPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrdersPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Order order1 = randomOrder();

		order1 = testGetOrdersPage_addOrder(order1);

		for (EntityField entityField : entityFields) {
			Page<Order> page = orderResource.getOrdersPage(
				null, getFilterString(entityField, "between", order1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(order1),
				(List<Order>)page.getItems());
		}
	}

	@Test
	public void testGetOrdersPageWithFilterDoubleEquals() throws Exception {
		testGetOrdersPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrdersPageWithFilterStringContains() throws Exception {
		testGetOrdersPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrdersPageWithFilterStringEquals() throws Exception {
		testGetOrdersPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrdersPageWithFilterStringStartsWith() throws Exception {
		testGetOrdersPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetOrdersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Order order1 = testGetOrdersPage_addOrder(randomOrder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Order order2 = testGetOrdersPage_addOrder(randomOrder());

		for (EntityField entityField : entityFields) {
			Page<Order> page = orderResource.getOrdersPage(
				null, getFilterString(entityField, operator, order1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(order1),
				(List<Order>)page.getItems());
		}
	}

	@Test
	public void testGetOrdersPageWithPagination() throws Exception {
		Page<Order> ordersPage = orderResource.getOrdersPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(ordersPage.getTotalCount());

		Order order1 = testGetOrdersPage_addOrder(randomOrder());

		Order order2 = testGetOrdersPage_addOrder(randomOrder());

		Order order3 = testGetOrdersPage_addOrder(randomOrder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Order> page1 = orderResource.getOrdersPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(order1, (List<Order>)page1.getItems());

			Page<Order> page2 = orderResource.getOrdersPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(order2, (List<Order>)page2.getItems());

			Page<Order> page3 = orderResource.getOrdersPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(order3, (List<Order>)page3.getItems());
		}
		else {
			Page<Order> page1 = orderResource.getOrdersPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Order> orders1 = (List<Order>)page1.getItems();

			Assert.assertEquals(
				orders1.toString(), totalCount + 2, orders1.size());

			Page<Order> page2 = orderResource.getOrdersPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Order> orders2 = (List<Order>)page2.getItems();

			Assert.assertEquals(orders2.toString(), 1, orders2.size());

			Page<Order> page3 = orderResource.getOrdersPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(order1, (List<Order>)page3.getItems());
			assertContains(order2, (List<Order>)page3.getItems());
			assertContains(order3, (List<Order>)page3.getItems());
		}
	}

	@Test
	public void testGetOrdersPageWithSortDateTime() throws Exception {
		testGetOrdersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, order1, order2) -> {
				BeanTestUtil.setProperty(
					order1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrdersPageWithSortDouble() throws Exception {
		testGetOrdersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, order1, order2) -> {
				BeanTestUtil.setProperty(order1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(order2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrdersPageWithSortInteger() throws Exception {
		testGetOrdersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, order1, order2) -> {
				BeanTestUtil.setProperty(order1, entityField.getName(), 0);
				BeanTestUtil.setProperty(order2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrdersPageWithSortString() throws Exception {
		testGetOrdersPageWithSort(
			EntityField.Type.STRING,
			(entityField, order1, order2) -> {
				Class<?> clazz = order1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						order1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						order2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						order1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						order2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						order1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						order2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrdersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Order, Order, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Order order1 = randomOrder();
		Order order2 = randomOrder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, order1, order2);
		}

		order1 = testGetOrdersPage_addOrder(order1);

		order2 = testGetOrdersPage_addOrder(order2);

		Page<Order> page = orderResource.getOrdersPage(null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Order> ascPage = orderResource.getOrdersPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(order1, (List<Order>)ascPage.getItems());
			assertContains(order2, (List<Order>)ascPage.getItems());

			Page<Order> descPage = orderResource.getOrdersPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(order2, (List<Order>)descPage.getItems());
			assertContains(order1, (List<Order>)descPage.getItems());
		}
	}

	protected Order testGetOrdersPage_addOrder(Order order) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrdersPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"orders",
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

		JSONObject ordersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/orders");

		long totalCount = ordersJSONObject.getLong("totalCount");

		Order order1 = testGraphQLOrder_addOrder(randomOrder());

		Order order2 = testGraphQLOrder_addOrder(randomOrder());

		ordersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/orders");

		Assert.assertEquals(
			totalCount + 2, ordersJSONObject.getLong("totalCount"));

		assertContains(
			order1,
			Arrays.asList(
				OrderSerDes.toDTOs(ordersJSONObject.getString("items"))));
		assertContains(
			order2,
			Arrays.asList(
				OrderSerDes.toDTOs(ordersJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		ordersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
			"JSONObject/orders");

		Assert.assertEquals(
			totalCount + 2, ordersJSONObject.getLong("totalCount"));

		assertContains(
			order1,
			Arrays.asList(
				OrderSerDes.toDTOs(ordersJSONObject.getString("items"))));
		assertContains(
			order2,
			Arrays.asList(
				OrderSerDes.toDTOs(ordersJSONObject.getString("items"))));
	}

	@Test
	public void testPatchOrder() throws Exception {
		Order postOrder = testPatchOrder_addOrder();

		Order randomPatchOrder = randomPatchOrder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Order patchOrder = orderResource.patchOrder(
			postOrder.getId(), randomPatchOrder);

		Order expectedPatchOrder = postOrder.clone();

		BeanTestUtil.copyProperties(randomPatchOrder, expectedPatchOrder);

		Order getOrder = orderResource.getOrder(patchOrder.getId());

		assertEquals(expectedPatchOrder, getOrder);
		assertValid(getOrder);
	}

	protected Order testPatchOrder_addOrder() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrderByExternalReferenceCode() throws Exception {
		Order postOrder = testPatchOrderByExternalReferenceCode_addOrder();

		Order randomPatchOrder = randomPatchOrder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Order patchOrder = orderResource.patchOrderByExternalReferenceCode(
			postOrder.getExternalReferenceCode(), randomPatchOrder);

		Order expectedPatchOrder = postOrder.clone();

		BeanTestUtil.copyProperties(randomPatchOrder, expectedPatchOrder);

		Order getOrder = orderResource.getOrderByExternalReferenceCode(
			patchOrder.getExternalReferenceCode());

		assertEquals(expectedPatchOrder, getOrder);
		assertValid(getOrder);
	}

	protected Order testPatchOrderByExternalReferenceCode_addOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrder() throws Exception {
		Order randomOrder = randomOrder();

		Order postOrder = testPostOrder_addOrder(randomOrder);

		assertEquals(randomOrder, postOrder);
		assertValid(postOrder);
	}

	protected Order testPostOrder_addOrder(Order order) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostOrder() throws Exception {
		Order randomOrder = randomOrder();

		Order order = testGraphQLOrder_addOrder(randomOrder);

		Assert.assertTrue(equals(randomOrder, order));
	}

	@Test
	public void testPutOrderByExternalReferenceCode() throws Exception {
		Order postOrder = testPutOrderByExternalReferenceCode_addOrder();

		Order randomOrder = randomOrder();

		Order putOrder = orderResource.putOrderByExternalReferenceCode(
			postOrder.getExternalReferenceCode(), randomOrder);

		assertEquals(randomOrder, putOrder);
		assertValid(putOrder);

		Order getOrder = orderResource.getOrderByExternalReferenceCode(
			putOrder.getExternalReferenceCode());

		assertEquals(randomOrder, getOrder);
		assertValid(getOrder);

		Order newOrder = testPutOrderByExternalReferenceCode_createOrder();

		putOrder = orderResource.putOrderByExternalReferenceCode(
			newOrder.getExternalReferenceCode(), newOrder);

		assertEquals(newOrder, putOrder);
		assertValid(putOrder);

		getOrder = orderResource.getOrderByExternalReferenceCode(
			putOrder.getExternalReferenceCode());

		assertEquals(newOrder, getOrder);

		Assert.assertEquals(
			newOrder.getExternalReferenceCode(),
			putOrder.getExternalReferenceCode());
	}

	protected Order testPutOrderByExternalReferenceCode_addOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Order testPutOrderByExternalReferenceCode_createOrder()
		throws Exception {

		return randomOrder();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Order order1 = testBatchEngineDeleteImportTask_addOrder();

		testBatchEngineDeleteImportTask_deleteOrder(
			200, order1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order1.getId()));

		order1 = testBatchEngineDeleteImportTask_addOrder();

		testBatchEngineDeleteImportTask_deleteOrder(200, null, order1.getId());

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order1.getId()));

		order1 = testBatchEngineDeleteImportTask_addOrder();
		Order order2 = testBatchEngineDeleteImportTask_addOrder();

		testBatchEngineDeleteImportTask_deleteOrder(
			200, order2.getExternalReferenceCode(), order1.getId());

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order1.getId()));
		assertHttpResponseStatusCode(
			200, orderResource.getOrderHttpResponse(order2.getId()));

		testBatchEngineDeleteImportTask_deleteOrder(
			200, order2.getExternalReferenceCode(), order1.getId());

		assertHttpResponseStatusCode(
			404, orderResource.getOrderHttpResponse(order2.getId()));
	}

	protected Order testBatchEngineDeleteImportTask_addOrder()
		throws Exception {

		return testDeleteOrder_addOrder();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrder(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.Order",
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

	protected Order testGraphQLOrder_addOrder() throws Exception {
		return testGraphQLOrder_addOrder(randomOrder());
	}

	protected Order testGraphQLOrder_addOrder(Order order) throws Exception {
		JSONDeserializer<Order> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Order.class)) {
			if (getGraphQLValue(field.get(order)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(order)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createOrder",
						new HashMap<String, Object>() {
							{
								put("order", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createOrder"),
			Order.class);
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

	protected void assertContains(Order order, List<Order> orders) {
		boolean contains = false;

		for (Order item : orders) {
			if (equals(order, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(orders + " does not contain " + order, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Order order1, Order order2) {
		Assert.assertTrue(
			order1 + " does not equal " + order2, equals(order1, order2));
	}

	protected void assertEquals(List<Order> orders1, List<Order> orders2) {
		Assert.assertEquals(orders1.size(), orders2.size());

		for (int i = 0; i < orders1.size(); i++) {
			Order order1 = orders1.get(i);
			Order order2 = orders2.get(i);

			assertEquals(order1, order2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Order> orders1, List<Order> orders2) {

		Assert.assertEquals(orders1.size(), orders2.size());

		for (Order order1 : orders1) {
			boolean contains = false;

			for (Order order2 : orders2) {
				if (equals(order1, order2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orders2 + " does not contain " + order1, contains);
		}
	}

	protected void assertValid(Order order) throws Exception {
		boolean valid = true;

		if (order.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (order.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getAccountExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (order.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (order.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("advanceStatus", additionalAssertFieldName)) {
				if (order.getAdvanceStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (order.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("billingAddress", additionalAssertFieldName)) {
				if (order.getBillingAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"billingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getBillingAddressExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("billingAddressId", additionalAssertFieldName)) {
				if (order.getBillingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (order.getChannel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getChannelExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (order.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (order.getCouponCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (order.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorEmailAddress", additionalAssertFieldName)) {

				if (order.getCreatorEmailAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (order.getCurrencyCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getCurrencyExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (order.getCurrencyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (order.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryTermDescription", additionalAssertFieldName)) {

				if (order.getDeliveryTermDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryTermExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getDeliveryTermExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("deliveryTermId", additionalAssertFieldName)) {
				if (order.getDeliveryTermId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("deliveryTermName", additionalAssertFieldName)) {
				if (order.getDeliveryTermName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (order.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"lastPriceUpdateDate", additionalAssertFieldName)) {

				if (order.getLastPriceUpdateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (order.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (order.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderDate", additionalAssertFieldName)) {
				if (order.getOrderDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderItems", additionalAssertFieldName)) {
				if (order.getOrderItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderStatus", additionalAssertFieldName)) {
				if (order.getOrderStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderStatusInfo", additionalAssertFieldName)) {
				if (order.getOrderStatusInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getOrderTypeExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (order.getOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentMethod", additionalAssertFieldName)) {
				if (order.getPaymentMethod() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (order.getPaymentStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusInfo", additionalAssertFieldName)) {

				if (order.getPaymentStatusInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentTermDescription", additionalAssertFieldName)) {

				if (order.getPaymentTermDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentTermExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getPaymentTermExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentTermId", additionalAssertFieldName)) {
				if (order.getPaymentTermId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentTermName", additionalAssertFieldName)) {
				if (order.getPaymentTermName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (order.getPrintedNote() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"purchaseOrderNumber", additionalAssertFieldName)) {

				if (order.getPurchaseOrderNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (order.getRequestedDeliveryDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippable", additionalAssertFieldName)) {
				if (order.getShippable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingAddress", additionalAssertFieldName)) {
				if (order.getShippingAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (order.getShippingAddressExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (order.getShippingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingAmount", additionalAssertFieldName)) {
				if (order.getShippingAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAmountFormatted", additionalAssertFieldName)) {

				if (order.getShippingAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAmountValue", additionalAssertFieldName)) {

				if (order.getShippingAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountAmount", additionalAssertFieldName)) {

				if (order.getShippingDiscountAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountAmountValue", additionalAssertFieldName)) {

				if (order.getShippingDiscountAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel1",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel1WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel2",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel2WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel3",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel3WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel4",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel4() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountPercentageLevel4WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountWithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getShippingDiscountWithTaxAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingMethod", additionalAssertFieldName)) {
				if (order.getShippingMethod() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingOption", additionalAssertFieldName)) {
				if (order.getShippingOption() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingWithTaxAmount", additionalAssertFieldName)) {

				if (order.getShippingWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getShippingWithTaxAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingWithTaxAmountValue", additionalAssertFieldName)) {

				if (order.getShippingWithTaxAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subtotal", additionalAssertFieldName)) {
				if (order.getSubtotal() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subtotalAmount", additionalAssertFieldName)) {
				if (order.getSubtotalAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountAmount", additionalAssertFieldName)) {

				if (order.getSubtotalDiscountAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel1",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel1WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel2",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel2WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel3",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel3WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel4",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel4() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountPercentageLevel4WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountWithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getSubtotalDiscountWithTaxAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalFormatted", additionalAssertFieldName)) {

				if (order.getSubtotalFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalWithTaxAmount", additionalAssertFieldName)) {

				if (order.getSubtotalWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getSubtotalWithTaxAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalWithTaxAmountValue", additionalAssertFieldName)) {

				if (order.getSubtotalWithTaxAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("taxAmount", additionalAssertFieldName)) {
				if (order.getTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxAmountFormatted", additionalAssertFieldName)) {

				if (order.getTaxAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("taxAmountValue", additionalAssertFieldName)) {
				if (order.getTaxAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("total", additionalAssertFieldName)) {
				if (order.getTotal() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("totalAmount", additionalAssertFieldName)) {
				if (order.getTotalAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountAmount", additionalAssertFieldName)) {

				if (order.getTotalDiscountAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountAmountValue", additionalAssertFieldName)) {

				if (order.getTotalDiscountAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel1",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel1WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel2",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel2WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel3",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel3WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel4",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel4() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountPercentageLevel4WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountWithTaxAmount", additionalAssertFieldName)) {

				if (order.getTotalDiscountWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountWithTaxAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountWithTaxAmountValue",
					additionalAssertFieldName)) {

				if (order.getTotalDiscountWithTaxAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("totalFormatted", additionalAssertFieldName)) {
				if (order.getTotalFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalWithTaxAmount", additionalAssertFieldName)) {

				if (order.getTotalWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalWithTaxAmountFormatted", additionalAssertFieldName)) {

				if (order.getTotalWithTaxAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalWithTaxAmountValue", additionalAssertFieldName)) {

				if (order.getTotalWithTaxAmountValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("transactionId", additionalAssertFieldName)) {
				if (order.getTransactionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (order.getWorkflowStatusInfo() == null) {
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

	protected void assertValid(Page<Order> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Order> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Order> orders = page.getItems();

		int size = orders.size();

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
					com.liferay.headless.commerce.admin.order.dto.v1_0.Order.
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

	protected boolean equals(Order order1, Order order2) {
		if (order1 == order2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getAccount(), order2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getAccountExternalReferenceCode(),
						order2.getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getAccountId(), order2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)order1.getActions(), (Map)order2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("advanceStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getAdvanceStatus(), order2.getAdvanceStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getAuthor(), order2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("billingAddress", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getBillingAddress(),
						order2.getBillingAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"billingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getBillingAddressExternalReferenceCode(),
						order2.getBillingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("billingAddressId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getBillingAddressId(),
						order2.getBillingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getChannel(), order2.getChannel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getChannelExternalReferenceCode(),
						order2.getChannelExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getChannelId(), order2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getCouponCode(), order2.getCouponCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getCreateDate(), order2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorEmailAddress", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getCreatorEmailAddress(),
						order2.getCreatorEmailAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getCurrencyCode(), order2.getCurrencyCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getCurrencyExternalReferenceCode(),
						order2.getCurrencyExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getCurrencyId(), order2.getCurrencyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)order1.getCustomFields(),
						(Map)order2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryTermDescription", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getDeliveryTermDescription(),
						order2.getDeliveryTermDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryTermExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getDeliveryTermExternalReferenceCode(),
						order2.getDeliveryTermExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("deliveryTermId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getDeliveryTermId(),
						order2.getDeliveryTermId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("deliveryTermName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getDeliveryTermName(),
						order2.getDeliveryTermName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getExternalReferenceCode(),
						order2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(order1.getId(), order2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"lastPriceUpdateDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getLastPriceUpdateDate(),
						order2.getLastPriceUpdateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getModifiedDate(), order2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(order1.getName(), order2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("orderDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getOrderDate(), order2.getOrderDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getOrderItems(), order2.getOrderItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getOrderStatus(), order2.getOrderStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderStatusInfo", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getOrderStatusInfo(),
						order2.getOrderStatusInfo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getOrderTypeExternalReferenceCode(),
						order2.getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getOrderTypeId(), order2.getOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentMethod", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getPaymentMethod(), order2.getPaymentMethod())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getPaymentStatus(), order2.getPaymentStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getPaymentStatusInfo(),
						order2.getPaymentStatusInfo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentTermDescription", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getPaymentTermDescription(),
						order2.getPaymentTermDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentTermExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getPaymentTermExternalReferenceCode(),
						order2.getPaymentTermExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentTermId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getPaymentTermId(), order2.getPaymentTermId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentTermName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getPaymentTermName(),
						order2.getPaymentTermName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getPrintedNote(), order2.getPrintedNote())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"purchaseOrderNumber", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getPurchaseOrderNumber(),
						order2.getPurchaseOrderNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getRequestedDeliveryDate(),
						order2.getRequestedDeliveryDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getShippable(), order2.getShippable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingAddress", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getShippingAddress(),
						order2.getShippingAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingAddressExternalReferenceCode(),
						order2.getShippingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingAddressId(),
						order2.getShippingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingAmount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getShippingAmount(),
						order2.getShippingAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAmountFormatted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingAmountFormatted(),
						order2.getShippingAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAmountValue", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingAmountValue(),
						order2.getShippingAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountAmount(),
						order2.getShippingDiscountAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountAmountFormatted(),
						order2.getShippingDiscountAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountAmountValue", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountAmountValue(),
						order2.getShippingDiscountAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel1",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountPercentageLevel1(),
						order2.getShippingDiscountPercentageLevel1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getShippingDiscountPercentageLevel1WithTaxAmount(),
						order2.
							getShippingDiscountPercentageLevel1WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel2",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountPercentageLevel2(),
						order2.getShippingDiscountPercentageLevel2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getShippingDiscountPercentageLevel2WithTaxAmount(),
						order2.
							getShippingDiscountPercentageLevel2WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel3",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountPercentageLevel3(),
						order2.getShippingDiscountPercentageLevel3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getShippingDiscountPercentageLevel3WithTaxAmount(),
						order2.
							getShippingDiscountPercentageLevel3WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel4",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountPercentageLevel4(),
						order2.getShippingDiscountPercentageLevel4())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getShippingDiscountPercentageLevel4WithTaxAmount(),
						order2.
							getShippingDiscountPercentageLevel4WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountWithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountWithTaxAmount(),
						order2.getShippingDiscountWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingDiscountWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingDiscountWithTaxAmountFormatted(),
						order2.getShippingDiscountWithTaxAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingMethod", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getShippingMethod(),
						order2.getShippingMethod())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingOption", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getShippingOption(),
						order2.getShippingOption())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingWithTaxAmount(),
						order2.getShippingWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingWithTaxAmountFormatted(),
						order2.getShippingWithTaxAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingWithTaxAmountValue", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getShippingWithTaxAmountValue(),
						order2.getShippingWithTaxAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subtotal", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getSubtotal(), order2.getSubtotal())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subtotalAmount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getSubtotalAmount(),
						order2.getSubtotalAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountAmount(),
						order2.getSubtotalDiscountAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountAmountFormatted(),
						order2.getSubtotalDiscountAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel1",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountPercentageLevel1(),
						order2.getSubtotalDiscountPercentageLevel1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getSubtotalDiscountPercentageLevel1WithTaxAmount(),
						order2.
							getSubtotalDiscountPercentageLevel1WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel2",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountPercentageLevel2(),
						order2.getSubtotalDiscountPercentageLevel2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getSubtotalDiscountPercentageLevel2WithTaxAmount(),
						order2.
							getSubtotalDiscountPercentageLevel2WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel3",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountPercentageLevel3(),
						order2.getSubtotalDiscountPercentageLevel3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getSubtotalDiscountPercentageLevel3WithTaxAmount(),
						order2.
							getSubtotalDiscountPercentageLevel3WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel4",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountPercentageLevel4(),
						order2.getSubtotalDiscountPercentageLevel4())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.
							getSubtotalDiscountPercentageLevel4WithTaxAmount(),
						order2.
							getSubtotalDiscountPercentageLevel4WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountWithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountWithTaxAmount(),
						order2.getSubtotalDiscountWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalDiscountWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalDiscountWithTaxAmountFormatted(),
						order2.getSubtotalDiscountWithTaxAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalFormatted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalFormatted(),
						order2.getSubtotalFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalWithTaxAmount(),
						order2.getSubtotalWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalWithTaxAmountFormatted(),
						order2.getSubtotalWithTaxAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"subtotalWithTaxAmountValue", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getSubtotalWithTaxAmountValue(),
						order2.getSubtotalWithTaxAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("taxAmount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getTaxAmount(), order2.getTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxAmountFormatted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTaxAmountFormatted(),
						order2.getTaxAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("taxAmountValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getTaxAmountValue(),
						order2.getTaxAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("total", additionalAssertFieldName)) {
				if (!Objects.deepEquals(order1.getTotal(), order2.getTotal())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("totalAmount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getTotalAmount(), order2.getTotalAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountAmount(),
						order2.getTotalDiscountAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountAmountFormatted(),
						order2.getTotalDiscountAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountAmountValue", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountAmountValue(),
						order2.getTotalDiscountAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel1",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel1(),
						order2.getTotalDiscountPercentageLevel1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel1WithTaxAmount(),
						order2.
							getTotalDiscountPercentageLevel1WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel2",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel2(),
						order2.getTotalDiscountPercentageLevel2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel2WithTaxAmount(),
						order2.
							getTotalDiscountPercentageLevel2WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel3",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel3(),
						order2.getTotalDiscountPercentageLevel3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel3WithTaxAmount(),
						order2.
							getTotalDiscountPercentageLevel3WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel4",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel4(),
						order2.getTotalDiscountPercentageLevel4())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountPercentageLevel4WithTaxAmount(),
						order2.
							getTotalDiscountPercentageLevel4WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountWithTaxAmount(),
						order2.getTotalDiscountWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountWithTaxAmountFormatted",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountWithTaxAmountFormatted(),
						order2.getTotalDiscountWithTaxAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalDiscountWithTaxAmountValue",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalDiscountWithTaxAmountValue(),
						order2.getTotalDiscountWithTaxAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("totalFormatted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getTotalFormatted(),
						order2.getTotalFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalWithTaxAmount(),
						order2.getTotalWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalWithTaxAmountFormatted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalWithTaxAmountFormatted(),
						order2.getTotalWithTaxAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalWithTaxAmountValue", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getTotalWithTaxAmountValue(),
						order2.getTotalWithTaxAmountValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("transactionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						order1.getTransactionId(), order2.getTransactionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						order1.getWorkflowStatusInfo(),
						order2.getWorkflowStatusInfo())) {

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

		if (!(_orderResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderResource;

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
		EntityField entityField, String operator, Order order) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("account")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountExternalReferenceCode")) {
			Object object = order.getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("advanceStatus")) {
			Object object = order.getAdvanceStatus();

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

		if (entityFieldName.equals("author")) {
			Object object = order.getAuthor();

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

		if (entityFieldName.equals("billingAddress")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("billingAddressExternalReferenceCode")) {
			Object object = order.getBillingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("billingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channel")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelExternalReferenceCode")) {
			Object object = order.getChannelExternalReferenceCode();

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

		if (entityFieldName.equals("channelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("couponCode")) {
			Object object = order.getCouponCode();

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
				Date date = order.getCreateDate();

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

				sb.append(_format.format(order.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("creatorEmailAddress")) {
			Object object = order.getCreatorEmailAddress();

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

		if (entityFieldName.equals("currencyCode")) {
			Object object = order.getCurrencyCode();

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
			Object object = order.getCurrencyExternalReferenceCode();

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("deliveryTermDescription")) {
			Object object = order.getDeliveryTermDescription();

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

		if (entityFieldName.equals("deliveryTermExternalReferenceCode")) {
			Object object = order.getDeliveryTermExternalReferenceCode();

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

		if (entityFieldName.equals("deliveryTermId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("deliveryTermName")) {
			Object object = order.getDeliveryTermName();

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
			Object object = order.getExternalReferenceCode();

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

		if (entityFieldName.equals("lastPriceUpdateDate")) {
			if (operator.equals("between")) {
				Date date = order.getLastPriceUpdateDate();

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

				sb.append(_format.format(order.getLastPriceUpdateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = order.getModifiedDate();

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

				sb.append(_format.format(order.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = order.getName();

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

		if (entityFieldName.equals("orderDate")) {
			if (operator.equals("between")) {
				Date date = order.getOrderDate();

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

				sb.append(_format.format(order.getOrderDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("orderItems")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderStatus")) {
			sb.append(String.valueOf(order.getOrderStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("orderStatusInfo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object = order.getOrderTypeExternalReferenceCode();

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

		if (entityFieldName.equals("orderTypeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentMethod")) {
			Object object = order.getPaymentMethod();

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

		if (entityFieldName.equals("paymentStatus")) {
			sb.append(String.valueOf(order.getPaymentStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("paymentStatusInfo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentTermDescription")) {
			Object object = order.getPaymentTermDescription();

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

		if (entityFieldName.equals("paymentTermExternalReferenceCode")) {
			Object object = order.getPaymentTermExternalReferenceCode();

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

		if (entityFieldName.equals("paymentTermId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentTermName")) {
			Object object = order.getPaymentTermName();

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

		if (entityFieldName.equals("printedNote")) {
			Object object = order.getPrintedNote();

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

		if (entityFieldName.equals("purchaseOrderNumber")) {
			Object object = order.getPurchaseOrderNumber();

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

		if (entityFieldName.equals("requestedDeliveryDate")) {
			if (operator.equals("between")) {
				Date date = order.getRequestedDeliveryDate();

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

				sb.append(_format.format(order.getRequestedDeliveryDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("shippable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAddress")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAddressExternalReferenceCode")) {
			Object object = order.getShippingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("shippingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAmountFormatted")) {
			Object object = order.getShippingAmountFormatted();

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

		if (entityFieldName.equals("shippingAmountValue")) {
			sb.append(String.valueOf(order.getShippingAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("shippingDiscountAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingDiscountAmountFormatted")) {
			Object object = order.getShippingDiscountAmountFormatted();

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

		if (entityFieldName.equals("shippingDiscountAmountValue")) {
			sb.append(String.valueOf(order.getShippingDiscountAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("shippingDiscountPercentageLevel1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"shippingDiscountPercentageLevel1WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingDiscountPercentageLevel2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"shippingDiscountPercentageLevel2WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingDiscountPercentageLevel3")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"shippingDiscountPercentageLevel3WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingDiscountPercentageLevel4")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"shippingDiscountPercentageLevel4WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingDiscountWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingDiscountWithTaxAmountFormatted")) {
			Object object = order.getShippingDiscountWithTaxAmountFormatted();

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

		if (entityFieldName.equals("shippingMethod")) {
			Object object = order.getShippingMethod();

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

		if (entityFieldName.equals("shippingOption")) {
			Object object = order.getShippingOption();

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

		if (entityFieldName.equals("shippingWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingWithTaxAmountFormatted")) {
			Object object = order.getShippingWithTaxAmountFormatted();

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

		if (entityFieldName.equals("shippingWithTaxAmountValue")) {
			sb.append(String.valueOf(order.getShippingWithTaxAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("subtotal")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalAmount")) {
			sb.append(String.valueOf(order.getSubtotalAmount()));

			return sb.toString();
		}

		if (entityFieldName.equals("subtotalDiscountAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalDiscountAmountFormatted")) {
			Object object = order.getSubtotalDiscountAmountFormatted();

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

		if (entityFieldName.equals("subtotalDiscountPercentageLevel1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"subtotalDiscountPercentageLevel1WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalDiscountPercentageLevel2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"subtotalDiscountPercentageLevel2WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalDiscountPercentageLevel3")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"subtotalDiscountPercentageLevel3WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalDiscountPercentageLevel4")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"subtotalDiscountPercentageLevel4WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalDiscountWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalDiscountWithTaxAmountFormatted")) {
			Object object = order.getSubtotalDiscountWithTaxAmountFormatted();

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

		if (entityFieldName.equals("subtotalFormatted")) {
			Object object = order.getSubtotalFormatted();

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

		if (entityFieldName.equals("subtotalWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subtotalWithTaxAmountFormatted")) {
			Object object = order.getSubtotalWithTaxAmountFormatted();

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

		if (entityFieldName.equals("subtotalWithTaxAmountValue")) {
			sb.append(String.valueOf(order.getSubtotalWithTaxAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("taxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxAmountFormatted")) {
			Object object = order.getTaxAmountFormatted();

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

		if (entityFieldName.equals("taxAmountValue")) {
			sb.append(String.valueOf(order.getTaxAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("total")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalAmount")) {
			sb.append(String.valueOf(order.getTotalAmount()));

			return sb.toString();
		}

		if (entityFieldName.equals("totalDiscountAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalDiscountAmountFormatted")) {
			Object object = order.getTotalDiscountAmountFormatted();

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

		if (entityFieldName.equals("totalDiscountAmountValue")) {
			sb.append(String.valueOf(order.getTotalDiscountAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("totalDiscountPercentageLevel1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"totalDiscountPercentageLevel1WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalDiscountPercentageLevel2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"totalDiscountPercentageLevel2WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalDiscountPercentageLevel3")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"totalDiscountPercentageLevel3WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalDiscountPercentageLevel4")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"totalDiscountPercentageLevel4WithTaxAmount")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalDiscountWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalDiscountWithTaxAmountFormatted")) {
			Object object = order.getTotalDiscountWithTaxAmountFormatted();

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

		if (entityFieldName.equals("totalDiscountWithTaxAmountValue")) {
			sb.append(
				String.valueOf(order.getTotalDiscountWithTaxAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("totalFormatted")) {
			Object object = order.getTotalFormatted();

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

		if (entityFieldName.equals("totalWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalWithTaxAmountFormatted")) {
			Object object = order.getTotalWithTaxAmountFormatted();

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

		if (entityFieldName.equals("totalWithTaxAmountValue")) {
			sb.append(String.valueOf(order.getTotalWithTaxAmountValue()));

			return sb.toString();
		}

		if (entityFieldName.equals("transactionId")) {
			Object object = order.getTransactionId();

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

	protected Order randomOrder() throws Exception {
		return new Order() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				advanceStatus = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				billingAddressExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				billingAddressId = RandomTestUtil.randomLong();
				channelExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
				couponCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				creatorEmailAddress = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyId = RandomTestUtil.randomLong();
				deliveryTermDescription = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				deliveryTermExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				deliveryTermId = RandomTestUtil.randomLong();
				deliveryTermName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				lastPriceUpdateDate = RandomTestUtil.nextDate();
				modifiedDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				orderDate = RandomTestUtil.nextDate();
				orderStatus = RandomTestUtil.randomInt();
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
				paymentMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentStatus = RandomTestUtil.randomInt();
				paymentTermDescription = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentTermExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentTermId = RandomTestUtil.randomLong();
				paymentTermName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				printedNote = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				purchaseOrderNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippable = RandomTestUtil.randomBoolean();
				shippingAddressExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingAddressId = RandomTestUtil.randomLong();
				shippingAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingAmountValue = RandomTestUtil.randomDouble();
				shippingDiscountAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingDiscountAmountValue = RandomTestUtil.randomDouble();
				shippingDiscountWithTaxAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingOption = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingWithTaxAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingWithTaxAmountValue = RandomTestUtil.randomDouble();
				subtotalAmount = RandomTestUtil.randomDouble();
				subtotalDiscountAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				subtotalDiscountWithTaxAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				subtotalFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				subtotalWithTaxAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				subtotalWithTaxAmountValue = RandomTestUtil.randomDouble();
				taxAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				taxAmountValue = RandomTestUtil.randomDouble();
				totalAmount = RandomTestUtil.randomDouble();
				totalDiscountAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				totalDiscountAmountValue = RandomTestUtil.randomDouble();
				totalDiscountWithTaxAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				totalDiscountWithTaxAmountValue = RandomTestUtil.randomDouble();
				totalFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				totalWithTaxAmountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				totalWithTaxAmountValue = RandomTestUtil.randomDouble();
				transactionId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Order randomIrrelevantOrder() throws Exception {
		Order randomIrrelevantOrder = randomOrder();

		return randomIrrelevantOrder;
	}

	protected Order randomPatchOrder() throws Exception {
		return randomOrder();
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

	protected OrderResource orderResource;
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
		LogFactoryUtil.getLog(BaseOrderResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.order.resource.v1_0.OrderResource
			_orderResource;

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