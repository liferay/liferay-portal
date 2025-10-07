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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderItemResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderItemSerDes;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseOrderItemResourceTestCase {

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

		_orderItemResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderItemResource = OrderItemResource.builder(
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

		OrderItem orderItem1 = randomOrderItem();

		String json = objectMapper.writeValueAsString(orderItem1);

		OrderItem orderItem2 = OrderItemSerDes.toDTO(json);

		Assert.assertTrue(equals(orderItem1, orderItem2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderItem orderItem = randomOrderItem();

		String json1 = objectMapper.writeValueAsString(orderItem);
		String json2 = OrderItemSerDes.toJSON(orderItem);

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

		OrderItem orderItem = randomOrderItem();

		orderItem.setDeliveryGroup(regex);
		orderItem.setDeliveryGroupName(regex);
		orderItem.setExternalReferenceCode(regex);
		orderItem.setFormattedQuantity(regex);
		orderItem.setOptions(regex);
		orderItem.setOrderExternalReferenceCode(regex);
		orderItem.setPrintedNote(regex);
		orderItem.setReplacedSku(regex);
		orderItem.setReplacedSkuExternalReferenceCode(regex);
		orderItem.setShippingAddressExternalReferenceCode(regex);
		orderItem.setSku(regex);
		orderItem.setSkuExternalReferenceCode(regex);
		orderItem.setUnitOfMeasure(regex);
		orderItem.setUnitOfMeasureKey(regex);

		String json = OrderItemSerDes.toJSON(orderItem);

		Assert.assertFalse(json.contains(regex));

		orderItem = OrderItemSerDes.toDTO(json);

		Assert.assertEquals(regex, orderItem.getDeliveryGroup());
		Assert.assertEquals(regex, orderItem.getDeliveryGroupName());
		Assert.assertEquals(regex, orderItem.getExternalReferenceCode());
		Assert.assertEquals(regex, orderItem.getFormattedQuantity());
		Assert.assertEquals(regex, orderItem.getOptions());
		Assert.assertEquals(regex, orderItem.getOrderExternalReferenceCode());
		Assert.assertEquals(regex, orderItem.getPrintedNote());
		Assert.assertEquals(regex, orderItem.getReplacedSku());
		Assert.assertEquals(
			regex, orderItem.getReplacedSkuExternalReferenceCode());
		Assert.assertEquals(
			regex, orderItem.getShippingAddressExternalReferenceCode());
		Assert.assertEquals(regex, orderItem.getSku());
		Assert.assertEquals(regex, orderItem.getSkuExternalReferenceCode());
		Assert.assertEquals(regex, orderItem.getUnitOfMeasure());
		Assert.assertEquals(regex, orderItem.getUnitOfMeasureKey());
	}

	@Test
	public void testDeleteOrderItem() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderItem orderItem = testDeleteOrderItem_addOrderItem();

		assertHttpResponseStatusCode(
			204,
			orderItemResource.deleteOrderItemHttpResponse(orderItem.getId()));

		assertHttpResponseStatusCode(
			404, orderItemResource.getOrderItemHttpResponse(orderItem.getId()));
		assertHttpResponseStatusCode(
			404, orderItemResource.getOrderItemHttpResponse(0L));
	}

	protected OrderItem testDeleteOrderItem_addOrderItem() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderItem() throws Exception {

		// No namespace

		OrderItem orderItem1 = testGraphQLDeleteOrderItem_addOrderItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderItem",
						new HashMap<String, Object>() {
							{
								put("id", orderItem1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderItem"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderItem",
					new HashMap<String, Object>() {
						{
							put("id", orderItem1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderItem orderItem2 = testGraphQLDeleteOrderItem_addOrderItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderItem",
							new HashMap<String, Object>() {
								{
									put("id", orderItem2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderItem"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderItem",
						new HashMap<String, Object>() {
							{
								put("id", orderItem2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderItem testGraphQLDeleteOrderItem_addOrderItem()
		throws Exception {

		return testGraphQLOrderItem_addOrderItem();
	}

	@Test
	public void testDeleteOrderItemBatch() throws Exception {
		OrderItem orderItem1 = testDeleteOrderItemBatch_addOrderItem();

		testDeleteOrderItemBatch_deleteOrderItem(
			202, orderItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem1.getId()));

		orderItem1 = testDeleteOrderItemBatch_addOrderItem();

		testDeleteOrderItemBatch_deleteOrderItem(202, null, orderItem1.getId());

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem1.getId()));

		orderItem1 = testDeleteOrderItemBatch_addOrderItem();
		OrderItem orderItem2 = testDeleteOrderItemBatch_addOrderItem();

		testDeleteOrderItemBatch_deleteOrderItem(
			202, orderItem2.getExternalReferenceCode(), orderItem1.getId());

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderItemResource.getOrderItemHttpResponse(orderItem2.getId()));

		testDeleteOrderItemBatch_deleteOrderItem(
			202, orderItem2.getExternalReferenceCode(), orderItem1.getId());

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem2.getId()));
	}

	protected OrderItem testDeleteOrderItemBatch_addOrderItem()
		throws Exception {

		return testDeleteOrderItem_addOrderItem();
	}

	protected void testDeleteOrderItemBatch_deleteOrderItem(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderItemResource.deleteOrderItemBatchHttpResponse(
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
	public void testDeleteOrderItemByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderItem orderItem =
			testDeleteOrderItemByExternalReferenceCode_addOrderItem();

		assertHttpResponseStatusCode(
			204,
			orderItemResource.
				deleteOrderItemByExternalReferenceCodeHttpResponse(
					orderItem.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemByExternalReferenceCodeHttpResponse(
				orderItem.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected OrderItem
			testDeleteOrderItemByExternalReferenceCode_addOrderItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderItemByExternalReferenceCode()
		throws Exception {

		// No namespace

		OrderItem orderItem1 =
			testGraphQLDeleteOrderItemByExternalReferenceCode_addOrderItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderItem1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrderItemByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderItemByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + orderItem1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderItem orderItem2 =
			testGraphQLDeleteOrderItemByExternalReferenceCode_addOrderItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderItemByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											orderItem2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderItemByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderItem2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderItem
			testGraphQLDeleteOrderItemByExternalReferenceCode_addOrderItem()
		throws Exception {

		return testGraphQLOrderItem_addOrderItem();
	}

	@Test
	public void testGetOrderByExternalReferenceCodeOrderItemsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeOrderItemsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrderByExternalReferenceCodeOrderItemsPage_getIrrelevantExternalReferenceCode();

		Page<OrderItem> page =
			orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
				externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			OrderItem irrelevantOrderItem =
				testGetOrderByExternalReferenceCodeOrderItemsPage_addOrderItem(
					irrelevantExternalReferenceCode,
					randomIrrelevantOrderItem());

			page =
				orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
					irrelevantExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderItem, (List<OrderItem>)page.getItems());
			assertValid(
				page,
				testGetOrderByExternalReferenceCodeOrderItemsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		OrderItem orderItem1 =
			testGetOrderByExternalReferenceCodeOrderItemsPage_addOrderItem(
				externalReferenceCode, randomOrderItem());

		OrderItem orderItem2 =
			testGetOrderByExternalReferenceCodeOrderItemsPage_addOrderItem(
				externalReferenceCode, randomOrderItem());

		page = orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
			externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(orderItem1, (List<OrderItem>)page.getItems());
		assertContains(orderItem2, (List<OrderItem>)page.getItems());
		assertValid(
			page,
			testGetOrderByExternalReferenceCodeOrderItemsPage_getExpectedActions(
				externalReferenceCode));

		orderItemResource.deleteOrderItem(orderItem1.getId());

		orderItemResource.deleteOrderItem(orderItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderByExternalReferenceCodeOrderItemsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderByExternalReferenceCodeOrderItemsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeOrderItemsPage_getExternalReferenceCode();

		Page<OrderItem> orderItemsPage =
			orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(orderItemsPage.getTotalCount());

		OrderItem orderItem1 =
			testGetOrderByExternalReferenceCodeOrderItemsPage_addOrderItem(
				externalReferenceCode, randomOrderItem());

		OrderItem orderItem2 =
			testGetOrderByExternalReferenceCodeOrderItemsPage_addOrderItem(
				externalReferenceCode, randomOrderItem());

		OrderItem orderItem3 =
			testGetOrderByExternalReferenceCodeOrderItemsPage_addOrderItem(
				externalReferenceCode, randomOrderItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderItem> page1 =
				orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(orderItem1, (List<OrderItem>)page1.getItems());

			Page<OrderItem> page2 =
				orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(orderItem2, (List<OrderItem>)page2.getItems());

			Page<OrderItem> page3 =
				orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(orderItem3, (List<OrderItem>)page3.getItems());
		}
		else {
			Page<OrderItem> page1 =
				orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
					externalReferenceCode, Pagination.of(1, totalCount + 2));

			List<OrderItem> orderItems1 = (List<OrderItem>)page1.getItems();

			Assert.assertEquals(
				orderItems1.toString(), totalCount + 2, orderItems1.size());

			Page<OrderItem> page2 =
				orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
					externalReferenceCode, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderItem> orderItems2 = (List<OrderItem>)page2.getItems();

			Assert.assertEquals(orderItems2.toString(), 1, orderItems2.size());

			Page<OrderItem> page3 =
				orderItemResource.getOrderByExternalReferenceCodeOrderItemsPage(
					externalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(orderItem1, (List<OrderItem>)page3.getItems());
			assertContains(orderItem2, (List<OrderItem>)page3.getItems());
			assertContains(orderItem3, (List<OrderItem>)page3.getItems());
		}
	}

	protected OrderItem
			testGetOrderByExternalReferenceCodeOrderItemsPage_addOrderItem(
				String externalReferenceCode, OrderItem orderItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderByExternalReferenceCodeOrderItemsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderByExternalReferenceCodeOrderItemsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderIdOrderItemsPage() throws Exception {
		Long id = testGetOrderIdOrderItemsPage_getId();
		Long irrelevantId = testGetOrderIdOrderItemsPage_getIrrelevantId();

		Page<OrderItem> page = orderItemResource.getOrderIdOrderItemsPage(
			id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			OrderItem irrelevantOrderItem =
				testGetOrderIdOrderItemsPage_addOrderItem(
					irrelevantId, randomIrrelevantOrderItem());

			page = orderItemResource.getOrderIdOrderItemsPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderItem, (List<OrderItem>)page.getItems());
			assertValid(
				page,
				testGetOrderIdOrderItemsPage_getExpectedActions(irrelevantId));
		}

		OrderItem orderItem1 = testGetOrderIdOrderItemsPage_addOrderItem(
			id, randomOrderItem());

		OrderItem orderItem2 = testGetOrderIdOrderItemsPage_addOrderItem(
			id, randomOrderItem());

		page = orderItemResource.getOrderIdOrderItemsPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(orderItem1, (List<OrderItem>)page.getItems());
		assertContains(orderItem2, (List<OrderItem>)page.getItems());
		assertValid(page, testGetOrderIdOrderItemsPage_getExpectedActions(id));

		orderItemResource.deleteOrderItem(orderItem1.getId());

		orderItemResource.deleteOrderItem(orderItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderIdOrderItemsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderIdOrderItemsPageWithPagination() throws Exception {
		Long id = testGetOrderIdOrderItemsPage_getId();

		Page<OrderItem> orderItemsPage =
			orderItemResource.getOrderIdOrderItemsPage(id, null);

		int totalCount = GetterUtil.getInteger(orderItemsPage.getTotalCount());

		OrderItem orderItem1 = testGetOrderIdOrderItemsPage_addOrderItem(
			id, randomOrderItem());

		OrderItem orderItem2 = testGetOrderIdOrderItemsPage_addOrderItem(
			id, randomOrderItem());

		OrderItem orderItem3 = testGetOrderIdOrderItemsPage_addOrderItem(
			id, randomOrderItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderItem> page1 = orderItemResource.getOrderIdOrderItemsPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(orderItem1, (List<OrderItem>)page1.getItems());

			Page<OrderItem> page2 = orderItemResource.getOrderIdOrderItemsPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(orderItem2, (List<OrderItem>)page2.getItems());

			Page<OrderItem> page3 = orderItemResource.getOrderIdOrderItemsPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(orderItem3, (List<OrderItem>)page3.getItems());
		}
		else {
			Page<OrderItem> page1 = orderItemResource.getOrderIdOrderItemsPage(
				id, Pagination.of(1, totalCount + 2));

			List<OrderItem> orderItems1 = (List<OrderItem>)page1.getItems();

			Assert.assertEquals(
				orderItems1.toString(), totalCount + 2, orderItems1.size());

			Page<OrderItem> page2 = orderItemResource.getOrderIdOrderItemsPage(
				id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderItem> orderItems2 = (List<OrderItem>)page2.getItems();

			Assert.assertEquals(orderItems2.toString(), 1, orderItems2.size());

			Page<OrderItem> page3 = orderItemResource.getOrderIdOrderItemsPage(
				id, Pagination.of(1, (int)totalCount + 3));

			assertContains(orderItem1, (List<OrderItem>)page3.getItems());
			assertContains(orderItem2, (List<OrderItem>)page3.getItems());
			assertContains(orderItem3, (List<OrderItem>)page3.getItems());
		}
	}

	protected OrderItem testGetOrderIdOrderItemsPage_addOrderItem(
			Long id, OrderItem orderItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderIdOrderItemsPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderIdOrderItemsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderItem() throws Exception {
		OrderItem postOrderItem = testGetOrderItem_addOrderItem();

		OrderItem getOrderItem = orderItemResource.getOrderItem(
			postOrderItem.getId());

		assertEquals(postOrderItem, getOrderItem);
		assertValid(getOrderItem);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		OrderItem postOrderItem = testGetOrderItem_addOrderItem();

		OrderItem getOrderItem = orderItemResource.getOrderItem(
			postOrderItem.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem"
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

		Object item = vulcanCRUDItemDelegate.getItem(postOrderItem.getId());

		assertEquals(getOrderItem, OrderItemSerDes.toDTO(item.toString()));
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

	protected OrderItem testGetOrderItem_addOrderItem() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderItem() throws Exception {
		OrderItem orderItem = testGraphQLGetOrderItem_addOrderItem();

		// No namespace

		Assert.assertTrue(
			equals(
				orderItem,
				OrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderItem",
								new HashMap<String, Object>() {
									{
										put("id", orderItem.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/orderItem"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderItem,
				OrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderItem",
									new HashMap<String, Object>() {
										{
											put("id", orderItem.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderItem"))));
	}

	@Test
	public void testGraphQLGetOrderItemNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderItem",
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
							"orderItem",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OrderItem testGraphQLGetOrderItem_addOrderItem()
		throws Exception {

		return testGraphQLOrderItem_addOrderItem();
	}

	@Test
	public void testGetOrderItemByExternalReferenceCode() throws Exception {
		OrderItem postOrderItem =
			testGetOrderItemByExternalReferenceCode_addOrderItem();

		OrderItem getOrderItem =
			orderItemResource.getOrderItemByExternalReferenceCode(
				postOrderItem.getExternalReferenceCode());

		assertEquals(postOrderItem, getOrderItem);
		assertValid(getOrderItem);
	}

	protected OrderItem testGetOrderItemByExternalReferenceCode_addOrderItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderItemByExternalReferenceCode()
		throws Exception {

		OrderItem orderItem =
			testGraphQLGetOrderItemByExternalReferenceCode_addOrderItem();

		// No namespace

		Assert.assertTrue(
			equals(
				orderItem,
				OrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderItemByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												orderItem.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/orderItemByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderItem,
				OrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderItemByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													orderItem.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderItemByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOrderItemByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderItemByExternalReferenceCode",
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
							"orderItemByExternalReferenceCode",
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

	protected OrderItem
			testGraphQLGetOrderItemByExternalReferenceCode_addOrderItem()
		throws Exception {

		return testGraphQLOrderItem_addOrderItem();
	}

	@Test
	public void testGetOrderItemsPage() throws Exception {
		Page<OrderItem> page = orderItemResource.getOrderItemsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		OrderItem orderItem1 = testGetOrderItemsPage_addOrderItem(
			randomOrderItem());

		OrderItem orderItem2 = testGetOrderItemsPage_addOrderItem(
			randomOrderItem());

		page = orderItemResource.getOrderItemsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(orderItem1, (List<OrderItem>)page.getItems());
		assertContains(orderItem2, (List<OrderItem>)page.getItems());
		assertValid(page, testGetOrderItemsPage_getExpectedActions());

		orderItemResource.deleteOrderItem(orderItem1.getId());

		orderItemResource.deleteOrderItem(orderItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderItemsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderItemsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderItem orderItem1 = randomOrderItem();

		orderItem1 = testGetOrderItemsPage_addOrderItem(orderItem1);

		for (EntityField entityField : entityFields) {
			Page<OrderItem> page = orderItemResource.getOrderItemsPage(
				null, getFilterString(entityField, "between", orderItem1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderItem1),
				(List<OrderItem>)page.getItems());
		}
	}

	@Test
	public void testGetOrderItemsPageWithFilterDoubleEquals() throws Exception {
		testGetOrderItemsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderItemsPageWithFilterStringContains()
		throws Exception {

		testGetOrderItemsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderItemsPageWithFilterStringEquals() throws Exception {
		testGetOrderItemsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderItemsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderItemsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderItemsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderItem orderItem1 = testGetOrderItemsPage_addOrderItem(
			randomOrderItem());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderItem orderItem2 = testGetOrderItemsPage_addOrderItem(
			randomOrderItem());

		for (EntityField entityField : entityFields) {
			Page<OrderItem> page = orderItemResource.getOrderItemsPage(
				null, getFilterString(entityField, operator, orderItem1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderItem1),
				(List<OrderItem>)page.getItems());
		}
	}

	@Test
	public void testGetOrderItemsPageWithPagination() throws Exception {
		Page<OrderItem> orderItemsPage = orderItemResource.getOrderItemsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(orderItemsPage.getTotalCount());

		OrderItem orderItem1 = testGetOrderItemsPage_addOrderItem(
			randomOrderItem());

		OrderItem orderItem2 = testGetOrderItemsPage_addOrderItem(
			randomOrderItem());

		OrderItem orderItem3 = testGetOrderItemsPage_addOrderItem(
			randomOrderItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderItem> page1 = orderItemResource.getOrderItemsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(orderItem1, (List<OrderItem>)page1.getItems());

			Page<OrderItem> page2 = orderItemResource.getOrderItemsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(orderItem2, (List<OrderItem>)page2.getItems());

			Page<OrderItem> page3 = orderItemResource.getOrderItemsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(orderItem3, (List<OrderItem>)page3.getItems());
		}
		else {
			Page<OrderItem> page1 = orderItemResource.getOrderItemsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<OrderItem> orderItems1 = (List<OrderItem>)page1.getItems();

			Assert.assertEquals(
				orderItems1.toString(), totalCount + 2, orderItems1.size());

			Page<OrderItem> page2 = orderItemResource.getOrderItemsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderItem> orderItems2 = (List<OrderItem>)page2.getItems();

			Assert.assertEquals(orderItems2.toString(), 1, orderItems2.size());

			Page<OrderItem> page3 = orderItemResource.getOrderItemsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(orderItem1, (List<OrderItem>)page3.getItems());
			assertContains(orderItem2, (List<OrderItem>)page3.getItems());
			assertContains(orderItem3, (List<OrderItem>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderItemsPageWithSortDateTime() throws Exception {
		testGetOrderItemsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, orderItem1, orderItem2) -> {
				BeanTestUtil.setProperty(
					orderItem1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderItemsPageWithSortDouble() throws Exception {
		testGetOrderItemsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, orderItem1, orderItem2) -> {
				BeanTestUtil.setProperty(
					orderItem1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					orderItem2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderItemsPageWithSortInteger() throws Exception {
		testGetOrderItemsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, orderItem1, orderItem2) -> {
				BeanTestUtil.setProperty(orderItem1, entityField.getName(), 0);
				BeanTestUtil.setProperty(orderItem2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderItemsPageWithSortString() throws Exception {
		testGetOrderItemsPageWithSort(
			EntityField.Type.STRING,
			(entityField, orderItem1, orderItem2) -> {
				Class<?> clazz = orderItem1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						orderItem1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						orderItem2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						orderItem1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						orderItem2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						orderItem1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						orderItem2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderItemsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, OrderItem, OrderItem, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OrderItem orderItem1 = randomOrderItem();
		OrderItem orderItem2 = randomOrderItem();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, orderItem1, orderItem2);
		}

		orderItem1 = testGetOrderItemsPage_addOrderItem(orderItem1);

		orderItem2 = testGetOrderItemsPage_addOrderItem(orderItem2);

		Page<OrderItem> page = orderItemResource.getOrderItemsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OrderItem> ascPage = orderItemResource.getOrderItemsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(orderItem1, (List<OrderItem>)ascPage.getItems());
			assertContains(orderItem2, (List<OrderItem>)ascPage.getItems());

			Page<OrderItem> descPage = orderItemResource.getOrderItemsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(orderItem2, (List<OrderItem>)descPage.getItems());
			assertContains(orderItem1, (List<OrderItem>)descPage.getItems());
		}
	}

	protected OrderItem testGetOrderItemsPage_addOrderItem(OrderItem orderItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrderItem() throws Exception {
		OrderItem postOrderItem = testPatchOrderItem_addOrderItem();

		OrderItem randomPatchOrderItem = randomPatchOrderItem();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderItem patchOrderItem = orderItemResource.patchOrderItem(
			postOrderItem.getId(), randomPatchOrderItem);

		OrderItem expectedPatchOrderItem = postOrderItem.clone();

		BeanTestUtil.copyProperties(
			randomPatchOrderItem, expectedPatchOrderItem);

		OrderItem getOrderItem = orderItemResource.getOrderItem(
			patchOrderItem.getId());

		assertEquals(expectedPatchOrderItem, getOrderItem);
		assertValid(getOrderItem);
	}

	protected OrderItem testPatchOrderItem_addOrderItem() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrderItemByExternalReferenceCode() throws Exception {
		OrderItem postOrderItem =
			testPatchOrderItemByExternalReferenceCode_addOrderItem();

		OrderItem randomPatchOrderItem = randomPatchOrderItem();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderItem patchOrderItem =
			orderItemResource.patchOrderItemByExternalReferenceCode(
				postOrderItem.getExternalReferenceCode(), randomPatchOrderItem);

		OrderItem expectedPatchOrderItem = postOrderItem.clone();

		BeanTestUtil.copyProperties(
			randomPatchOrderItem, expectedPatchOrderItem);

		OrderItem getOrderItem =
			orderItemResource.getOrderItemByExternalReferenceCode(
				patchOrderItem.getExternalReferenceCode());

		assertEquals(expectedPatchOrderItem, getOrderItem);
		assertValid(getOrderItem);
	}

	protected OrderItem testPatchOrderItemByExternalReferenceCode_addOrderItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderByExternalReferenceCodeOrderItem()
		throws Exception {

		OrderItem randomOrderItem = randomOrderItem();

		OrderItem postOrderItem =
			testPostOrderByExternalReferenceCodeOrderItem_addOrderItem(
				randomOrderItem);

		assertEquals(randomOrderItem, postOrderItem);
		assertValid(postOrderItem);
	}

	protected OrderItem
			testPostOrderByExternalReferenceCodeOrderItem_addOrderItem(
				OrderItem orderItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderIdOrderItem() throws Exception {
		OrderItem randomOrderItem = randomOrderItem();

		OrderItem postOrderItem = testPostOrderIdOrderItem_addOrderItem(
			randomOrderItem);

		assertEquals(randomOrderItem, postOrderItem);
		assertValid(postOrderItem);
	}

	protected OrderItem testPostOrderIdOrderItem_addOrderItem(
			OrderItem orderItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutOrderItem() throws Exception {
		OrderItem postOrderItem = testPutOrderItem_addOrderItem();

		OrderItem randomOrderItem = randomOrderItem();

		OrderItem putOrderItem = orderItemResource.putOrderItem(
			postOrderItem.getId(), randomOrderItem);

		assertEquals(randomOrderItem, putOrderItem);
		assertValid(putOrderItem);

		OrderItem getOrderItem = orderItemResource.getOrderItem(
			putOrderItem.getId());

		assertEquals(randomOrderItem, getOrderItem);
		assertValid(getOrderItem);
	}

	protected OrderItem testPutOrderItem_addOrderItem() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutOrderItemByExternalReferenceCode() throws Exception {
		OrderItem postOrderItem =
			testPutOrderItemByExternalReferenceCode_addOrderItem();

		OrderItem randomOrderItem = randomOrderItem();

		OrderItem putOrderItem =
			orderItemResource.putOrderItemByExternalReferenceCode(
				postOrderItem.getExternalReferenceCode(), randomOrderItem);

		assertEquals(randomOrderItem, putOrderItem);
		assertValid(putOrderItem);

		OrderItem getOrderItem =
			orderItemResource.getOrderItemByExternalReferenceCode(
				putOrderItem.getExternalReferenceCode());

		assertEquals(randomOrderItem, getOrderItem);
		assertValid(getOrderItem);

		OrderItem newOrderItem =
			testPutOrderItemByExternalReferenceCode_createOrderItem();

		putOrderItem = orderItemResource.putOrderItemByExternalReferenceCode(
			newOrderItem.getExternalReferenceCode(), newOrderItem);

		assertEquals(newOrderItem, putOrderItem);
		assertValid(putOrderItem);

		getOrderItem = orderItemResource.getOrderItemByExternalReferenceCode(
			putOrderItem.getExternalReferenceCode());

		assertEquals(newOrderItem, getOrderItem);

		Assert.assertEquals(
			newOrderItem.getExternalReferenceCode(),
			putOrderItem.getExternalReferenceCode());
	}

	protected OrderItem testPutOrderItemByExternalReferenceCode_addOrderItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected OrderItem
			testPutOrderItemByExternalReferenceCode_createOrderItem()
		throws Exception {

		return randomOrderItem();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderItem orderItem1 = testBatchEngineDeleteImportTask_addOrderItem();

		testBatchEngineDeleteImportTask_deleteOrderItem(
			200, orderItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem1.getId()));

		orderItem1 = testBatchEngineDeleteImportTask_addOrderItem();

		testBatchEngineDeleteImportTask_deleteOrderItem(
			200, null, orderItem1.getId());

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem1.getId()));

		orderItem1 = testBatchEngineDeleteImportTask_addOrderItem();
		OrderItem orderItem2 = testBatchEngineDeleteImportTask_addOrderItem();

		testBatchEngineDeleteImportTask_deleteOrderItem(
			200, orderItem2.getExternalReferenceCode(), orderItem1.getId());

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderItemResource.getOrderItemHttpResponse(orderItem2.getId()));

		testBatchEngineDeleteImportTask_deleteOrderItem(
			200, orderItem2.getExternalReferenceCode(), orderItem1.getId());

		assertHttpResponseStatusCode(
			404,
			orderItemResource.getOrderItemHttpResponse(orderItem2.getId()));
	}

	protected OrderItem testBatchEngineDeleteImportTask_addOrderItem()
		throws Exception {

		return testDeleteOrderItem_addOrderItem();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderItem(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem",
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

	protected OrderItem testGraphQLOrderItem_addOrderItem() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		OrderItem orderItem, List<OrderItem> orderItems) {

		boolean contains = false;

		for (OrderItem item : orderItems) {
			if (equals(orderItem, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderItems + " does not contain " + orderItem, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(OrderItem orderItem1, OrderItem orderItem2) {
		Assert.assertTrue(
			orderItem1 + " does not equal " + orderItem2,
			equals(orderItem1, orderItem2));
	}

	protected void assertEquals(
		List<OrderItem> orderItems1, List<OrderItem> orderItems2) {

		Assert.assertEquals(orderItems1.size(), orderItems2.size());

		for (int i = 0; i < orderItems1.size(); i++) {
			OrderItem orderItem1 = orderItems1.get(i);
			OrderItem orderItem2 = orderItems2.get(i);

			assertEquals(orderItem1, orderItem2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderItem> orderItems1, List<OrderItem> orderItems2) {

		Assert.assertEquals(orderItems1.size(), orderItems2.size());

		for (OrderItem orderItem1 : orderItems1) {
			boolean contains = false;

			for (OrderItem orderItem2 : orderItems2) {
				if (equals(orderItem1, orderItem2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderItems2 + " does not contain " + orderItem1, contains);
		}
	}

	protected void assertValid(OrderItem orderItem) throws Exception {
		boolean valid = true;

		if (orderItem.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("bookedQuantityId", additionalAssertFieldName)) {
				if (orderItem.getBookedQuantityId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (orderItem.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("decimalQuantity", additionalAssertFieldName)) {
				if (orderItem.getDecimalQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("deliveryGroup", additionalAssertFieldName)) {
				if (orderItem.getDeliveryGroup() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryGroupName", additionalAssertFieldName)) {

				if (orderItem.getDeliveryGroupName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountAmount", additionalAssertFieldName)) {
				if (orderItem.getDiscountAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountManuallyAdjusted", additionalAssertFieldName)) {

				if (orderItem.getDiscountManuallyAdjusted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel1", additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel1WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel2", additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel2WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel3", additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel3WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel4", additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel4() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (orderItem.getDiscountPercentageLevel4WithTaxAmount() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountWithTaxAmount", additionalAssertFieldName)) {

				if (orderItem.getDiscountWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (orderItem.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("finalPrice", additionalAssertFieldName)) {
				if (orderItem.getFinalPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"finalPriceWithTaxAmount", additionalAssertFieldName)) {

				if (orderItem.getFinalPriceWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"formattedQuantity", additionalAssertFieldName)) {

				if (orderItem.getFormattedQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (orderItem.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("options", additionalAssertFieldName)) {
				if (orderItem.getOptions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderExternalReferenceCode", additionalAssertFieldName)) {

				if (orderItem.getOrderExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (orderItem.getOrderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceManuallyAdjusted", additionalAssertFieldName)) {

				if (orderItem.getPriceManuallyAdjusted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (orderItem.getPrintedNote() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (orderItem.getProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("promoPrice", additionalAssertFieldName)) {
				if (orderItem.getPromoPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"promoPriceWithTaxAmount", additionalAssertFieldName)) {

				if (orderItem.getPromoPriceWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (orderItem.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("replacedSku", additionalAssertFieldName)) {
				if (orderItem.getReplacedSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"replacedSkuExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderItem.getReplacedSkuExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("replacedSkuId", additionalAssertFieldName)) {
				if (orderItem.getReplacedSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (orderItem.getRequestedDeliveryDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippable", additionalAssertFieldName)) {
				if (orderItem.getShippable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippedQuantity", additionalAssertFieldName)) {
				if (orderItem.getShippedQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingAddress", additionalAssertFieldName)) {
				if (orderItem.getShippingAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderItem.getShippingAddressExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (orderItem.getShippingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (orderItem.getSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (orderItem.getSkuExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (orderItem.getSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscription", additionalAssertFieldName)) {
				if (orderItem.getSubscription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasure", additionalAssertFieldName)) {
				if (orderItem.getUnitOfMeasure() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (orderItem.getUnitOfMeasureKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitPrice", additionalAssertFieldName)) {
				if (orderItem.getUnitPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"unitPriceWithTaxAmount", additionalAssertFieldName)) {

				if (orderItem.getUnitPriceWithTaxAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("virtualItemURLs", additionalAssertFieldName)) {
				if (orderItem.getVirtualItemURLs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("virtualItems", additionalAssertFieldName)) {
				if (orderItem.getVirtualItems() == null) {
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

	protected void assertValid(Page<OrderItem> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderItem> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderItem> orderItems = page.getItems();

		int size = orderItems.size();

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
						OrderItem.class)) {

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

	protected boolean equals(OrderItem orderItem1, OrderItem orderItem2) {
		if (orderItem1 == orderItem2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("bookedQuantityId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getBookedQuantityId(),
						orderItem2.getBookedQuantityId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getCustomFields(),
						orderItem2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("decimalQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getDecimalQuantity(),
						orderItem2.getDecimalQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("deliveryGroup", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getDeliveryGroup(),
						orderItem2.getDeliveryGroup())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryGroupName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDeliveryGroupName(),
						orderItem2.getDeliveryGroupName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountAmount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getDiscountAmount(),
						orderItem2.getDiscountAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountManuallyAdjusted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountManuallyAdjusted(),
						orderItem2.getDiscountManuallyAdjusted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel1", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel1(),
						orderItem2.getDiscountPercentageLevel1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel1WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel1WithTaxAmount(),
						orderItem2.
							getDiscountPercentageLevel1WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel2", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel2(),
						orderItem2.getDiscountPercentageLevel2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel2WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel2WithTaxAmount(),
						orderItem2.
							getDiscountPercentageLevel2WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel3", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel3(),
						orderItem2.getDiscountPercentageLevel3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel3WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel3WithTaxAmount(),
						orderItem2.
							getDiscountPercentageLevel3WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel4", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel4(),
						orderItem2.getDiscountPercentageLevel4())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountPercentageLevel4WithTaxAmount",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountPercentageLevel4WithTaxAmount(),
						orderItem2.
							getDiscountPercentageLevel4WithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getDiscountWithTaxAmount(),
						orderItem2.getDiscountWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getExternalReferenceCode(),
						orderItem2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("finalPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getFinalPrice(),
						orderItem2.getFinalPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"finalPriceWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getFinalPriceWithTaxAmount(),
						orderItem2.getFinalPriceWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"formattedQuantity", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getFormattedQuantity(),
						orderItem2.getFormattedQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getId(), orderItem2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderItem1.getName(), (Map)orderItem2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("options", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getOptions(), orderItem2.getOptions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getOrderExternalReferenceCode(),
						orderItem2.getOrderExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getOrderId(), orderItem2.getOrderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceManuallyAdjusted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getPriceManuallyAdjusted(),
						orderItem2.getPriceManuallyAdjusted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getPrintedNote(),
						orderItem2.getPrintedNote())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getProductId(), orderItem2.getProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("promoPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getPromoPrice(),
						orderItem2.getPromoPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"promoPriceWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getPromoPriceWithTaxAmount(),
						orderItem2.getPromoPriceWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getQuantity(), orderItem2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("replacedSku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getReplacedSku(),
						orderItem2.getReplacedSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"replacedSkuExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getReplacedSkuExternalReferenceCode(),
						orderItem2.getReplacedSkuExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("replacedSkuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getReplacedSkuId(),
						orderItem2.getReplacedSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getRequestedDeliveryDate(),
						orderItem2.getRequestedDeliveryDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getShippable(), orderItem2.getShippable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippedQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getShippedQuantity(),
						orderItem2.getShippedQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingAddress", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getShippingAddress(),
						orderItem2.getShippingAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getShippingAddressExternalReferenceCode(),
						orderItem2.getShippingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getShippingAddressId(),
						orderItem2.getShippingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getSku(), orderItem2.getSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getSkuExternalReferenceCode(),
						orderItem2.getSkuExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getSkuId(), orderItem2.getSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscription", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getSubscription(),
						orderItem2.getSubscription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasure", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getUnitOfMeasure(),
						orderItem2.getUnitOfMeasure())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getUnitOfMeasureKey(),
						orderItem2.getUnitOfMeasureKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getUnitPrice(), orderItem2.getUnitPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"unitPriceWithTaxAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderItem1.getUnitPriceWithTaxAmount(),
						orderItem2.getUnitPriceWithTaxAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("virtualItemURLs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getVirtualItemURLs(),
						orderItem2.getVirtualItemURLs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("virtualItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderItem1.getVirtualItems(),
						orderItem2.getVirtualItems())) {

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

		if (!(_orderItemResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderItemResource;

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
		EntityField entityField, String operator, OrderItem orderItem) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("bookedQuantityId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("decimalQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("deliveryGroup")) {
			Object object = orderItem.getDeliveryGroup();

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

		if (entityFieldName.equals("deliveryGroupName")) {
			Object object = orderItem.getDeliveryGroupName();

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

		if (entityFieldName.equals("discountAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountManuallyAdjusted")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel1WithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel2WithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel3")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel3WithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel4")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountPercentageLevel4WithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = orderItem.getExternalReferenceCode();

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

		if (entityFieldName.equals("finalPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("finalPriceWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("formattedQuantity")) {
			Object object = orderItem.getFormattedQuantity();

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

		if (entityFieldName.equals("options")) {
			Object object = orderItem.getOptions();

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

		if (entityFieldName.equals("orderExternalReferenceCode")) {
			Object object = orderItem.getOrderExternalReferenceCode();

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

		if (entityFieldName.equals("orderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceManuallyAdjusted")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("printedNote")) {
			Object object = orderItem.getPrintedNote();

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

		if (entityFieldName.equals("productId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("promoPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("promoPriceWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("replacedSku")) {
			Object object = orderItem.getReplacedSku();

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

		if (entityFieldName.equals("replacedSkuExternalReferenceCode")) {
			Object object = orderItem.getReplacedSkuExternalReferenceCode();

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

		if (entityFieldName.equals("replacedSkuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("requestedDeliveryDate")) {
			if (operator.equals("between")) {
				Date date = orderItem.getRequestedDeliveryDate();

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

				sb.append(_format.format(orderItem.getRequestedDeliveryDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("shippable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippedQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAddress")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAddressExternalReferenceCode")) {
			Object object = orderItem.getShippingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("sku")) {
			Object object = orderItem.getSku();

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

		if (entityFieldName.equals("skuExternalReferenceCode")) {
			Object object = orderItem.getSkuExternalReferenceCode();

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

		if (entityFieldName.equals("skuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscription")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unitOfMeasure")) {
			Object object = orderItem.getUnitOfMeasure();

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

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = orderItem.getUnitOfMeasureKey();

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

		if (entityFieldName.equals("unitPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unitPriceWithTaxAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("virtualItemURLs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("virtualItems")) {
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

	protected OrderItem randomOrderItem() throws Exception {
		return new OrderItem() {
			{
				bookedQuantityId = RandomTestUtil.randomLong();
				deliveryGroup = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				deliveryGroupName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				discountManuallyAdjusted = RandomTestUtil.randomBoolean();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				formattedQuantity = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				options = StringUtil.toLowerCase(RandomTestUtil.randomString());
				orderExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderId = RandomTestUtil.randomLong();
				priceManuallyAdjusted = RandomTestUtil.randomBoolean();
				printedNote = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productId = RandomTestUtil.randomLong();
				replacedSku = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				replacedSkuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				replacedSkuId = RandomTestUtil.randomLong();
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippable = RandomTestUtil.randomBoolean();
				shippingAddressExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingAddressId = RandomTestUtil.randomLong();
				sku = StringUtil.toLowerCase(RandomTestUtil.randomString());
				skuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				skuId = RandomTestUtil.randomLong();
				subscription = RandomTestUtil.randomBoolean();
				unitOfMeasure = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected OrderItem randomIrrelevantOrderItem() throws Exception {
		OrderItem randomIrrelevantOrderItem = randomOrderItem();

		return randomIrrelevantOrderItem;
	}

	protected OrderItem randomPatchOrderItem() throws Exception {
		return randomOrderItem();
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

	protected OrderItemResource orderItemResource;
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
		LogFactoryUtil.getLog(BaseOrderItemResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.order.resource.v1_0.
			OrderItemResource _orderItemResource;

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