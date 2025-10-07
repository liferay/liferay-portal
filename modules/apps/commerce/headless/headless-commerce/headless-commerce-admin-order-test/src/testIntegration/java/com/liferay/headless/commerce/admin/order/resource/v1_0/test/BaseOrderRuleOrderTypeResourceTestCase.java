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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleOrderType;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderRuleOrderTypeResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderRuleOrderTypeSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseOrderRuleOrderTypeResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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

		_orderRuleOrderTypeResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderRuleOrderTypeResource = OrderRuleOrderTypeResource.builder(
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

		OrderRuleOrderType orderRuleOrderType1 = randomOrderRuleOrderType();

		String json = objectMapper.writeValueAsString(orderRuleOrderType1);

		OrderRuleOrderType orderRuleOrderType2 = OrderRuleOrderTypeSerDes.toDTO(
			json);

		Assert.assertTrue(equals(orderRuleOrderType1, orderRuleOrderType2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderRuleOrderType orderRuleOrderType = randomOrderRuleOrderType();

		String json1 = objectMapper.writeValueAsString(orderRuleOrderType);
		String json2 = OrderRuleOrderTypeSerDes.toJSON(orderRuleOrderType);

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

		OrderRuleOrderType orderRuleOrderType = randomOrderRuleOrderType();

		orderRuleOrderType.setOrderRuleExternalReferenceCode(regex);
		orderRuleOrderType.setOrderTypeExternalReferenceCode(regex);

		String json = OrderRuleOrderTypeSerDes.toJSON(orderRuleOrderType);

		Assert.assertFalse(json.contains(regex));

		orderRuleOrderType = OrderRuleOrderTypeSerDes.toDTO(json);

		Assert.assertEquals(
			regex, orderRuleOrderType.getOrderRuleExternalReferenceCode());
		Assert.assertEquals(
			regex, orderRuleOrderType.getOrderTypeExternalReferenceCode());
	}

	@Test
	public void testDeleteOrderRuleOrderType() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRuleOrderType orderRuleOrderType =
			testDeleteOrderRuleOrderType_addOrderRuleOrderType();

		assertHttpResponseStatusCode(
			204,
			orderRuleOrderTypeResource.deleteOrderRuleOrderTypeHttpResponse(
				orderRuleOrderType.getOrderRuleOrderTypeId()));
	}

	protected OrderRuleOrderType
			testDeleteOrderRuleOrderType_addOrderRuleOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderRuleOrderType() throws Exception {

		// No namespace

		OrderRuleOrderType orderRuleOrderType1 =
			testGraphQLDeleteOrderRuleOrderType_addOrderRuleOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderRuleOrderType",
						new HashMap<String, Object>() {
							{
								put(
									"orderRuleOrderTypeId",
									orderRuleOrderType1.
										getOrderRuleOrderTypeId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderRuleOrderType"));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderRuleOrderType orderRuleOrderType2 =
			testGraphQLDeleteOrderRuleOrderType_addOrderRuleOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderRuleOrderType",
							new HashMap<String, Object>() {
								{
									put(
										"orderRuleOrderTypeId",
										orderRuleOrderType2.
											getOrderRuleOrderTypeId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderRuleOrderType"));
	}

	protected OrderRuleOrderType
			testGraphQLDeleteOrderRuleOrderType_addOrderRuleOrderType()
		throws Exception {

		return testGraphQLOrderRuleOrderType_addOrderRuleOrderType();
	}

	@Test
	public void testDeleteOrderRuleOrderTypeBatch() throws Exception {
		OrderRuleOrderType orderRuleOrderType1 =
			testDeleteOrderRuleOrderTypeBatch_addOrderRuleOrderType();

		testDeleteOrderRuleOrderTypeBatch_deleteOrderRuleOrderType(
			202, null, orderRuleOrderType1.getOrderRuleOrderTypeId());
	}

	protected OrderRuleOrderType
			testDeleteOrderRuleOrderTypeBatch_addOrderRuleOrderType()
		throws Exception {

		return testDeleteOrderRuleOrderType_addOrderRuleOrderType();
	}

	protected void testDeleteOrderRuleOrderTypeBatch_deleteOrderRuleOrderType(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderRuleOrderTypeResource.
				deleteOrderRuleOrderTypeBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"orderRuleOrderTypeId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getIrrelevantExternalReferenceCode();

		Page<OrderRuleOrderType> page =
			orderRuleOrderTypeResource.
				getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			OrderRuleOrderType irrelevantOrderRuleOrderType =
				testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
					irrelevantExternalReferenceCode,
					randomIrrelevantOrderRuleOrderType());

			page =
				orderRuleOrderTypeResource.
					getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleOrderType,
				(List<OrderRuleOrderType>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		OrderRuleOrderType orderRuleOrderType1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
				externalReferenceCode, randomOrderRuleOrderType());

		OrderRuleOrderType orderRuleOrderType2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
				externalReferenceCode, randomOrderRuleOrderType());

		page =
			orderRuleOrderTypeResource.
				getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleOrderType1, (List<OrderRuleOrderType>)page.getItems());
		assertContains(
			orderRuleOrderType2, (List<OrderRuleOrderType>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getExpectedActions(
				externalReferenceCode));

		orderRuleOrderTypeResource.deleteOrderRuleOrderType(
			orderRuleOrderType1.getOrderRuleOrderTypeId());

		orderRuleOrderTypeResource.deleteOrderRuleOrderType(
			orderRuleOrderType2.getOrderRuleOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getExternalReferenceCode();

		Page<OrderRuleOrderType> orderRuleOrderTypesPage =
			orderRuleOrderTypeResource.
				getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleOrderTypesPage.getTotalCount());

		OrderRuleOrderType orderRuleOrderType1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
				externalReferenceCode, randomOrderRuleOrderType());

		OrderRuleOrderType orderRuleOrderType2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
				externalReferenceCode, randomOrderRuleOrderType());

		OrderRuleOrderType orderRuleOrderType3 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
				externalReferenceCode, randomOrderRuleOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleOrderType> page1 =
				orderRuleOrderTypeResource.
					getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleOrderType1,
				(List<OrderRuleOrderType>)page1.getItems());

			Page<OrderRuleOrderType> page2 =
				orderRuleOrderTypeResource.
					getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleOrderType2,
				(List<OrderRuleOrderType>)page2.getItems());

			Page<OrderRuleOrderType> page3 =
				orderRuleOrderTypeResource.
					getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleOrderType3,
				(List<OrderRuleOrderType>)page3.getItems());
		}
		else {
			Page<OrderRuleOrderType> page1 =
				orderRuleOrderTypeResource.
					getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<OrderRuleOrderType> orderRuleOrderTypes1 =
				(List<OrderRuleOrderType>)page1.getItems();

			Assert.assertEquals(
				orderRuleOrderTypes1.toString(), totalCount + 2,
				orderRuleOrderTypes1.size());

			Page<OrderRuleOrderType> page2 =
				orderRuleOrderTypeResource.
					getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleOrderType> orderRuleOrderTypes2 =
				(List<OrderRuleOrderType>)page2.getItems();

			Assert.assertEquals(
				orderRuleOrderTypes2.toString(), 1,
				orderRuleOrderTypes2.size());

			Page<OrderRuleOrderType> page3 =
				orderRuleOrderTypeResource.
					getOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				orderRuleOrderType1,
				(List<OrderRuleOrderType>)page3.getItems());
			assertContains(
				orderRuleOrderType2,
				(List<OrderRuleOrderType>)page3.getItems());
			assertContains(
				orderRuleOrderType3,
				(List<OrderRuleOrderType>)page3.getItems());
		}
	}

	protected OrderRuleOrderType
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_addOrderRuleOrderType(
				String externalReferenceCode,
				OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleOrderTypesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleOrderTypesPage() throws Exception {
		Long id = testGetOrderRuleIdOrderRuleOrderTypesPage_getId();
		Long irrelevantId =
			testGetOrderRuleIdOrderRuleOrderTypesPage_getIrrelevantId();

		Page<OrderRuleOrderType> page =
			orderRuleOrderTypeResource.getOrderRuleIdOrderRuleOrderTypesPage(
				id, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			OrderRuleOrderType irrelevantOrderRuleOrderType =
				testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
					irrelevantId, randomIrrelevantOrderRuleOrderType());

			page =
				orderRuleOrderTypeResource.
					getOrderRuleIdOrderRuleOrderTypesPage(
						irrelevantId, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleOrderType,
				(List<OrderRuleOrderType>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleIdOrderRuleOrderTypesPage_getExpectedActions(
					irrelevantId));
		}

		OrderRuleOrderType orderRuleOrderType1 =
			testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
				id, randomOrderRuleOrderType());

		OrderRuleOrderType orderRuleOrderType2 =
			testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
				id, randomOrderRuleOrderType());

		page = orderRuleOrderTypeResource.getOrderRuleIdOrderRuleOrderTypesPage(
			id, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleOrderType1, (List<OrderRuleOrderType>)page.getItems());
		assertContains(
			orderRuleOrderType2, (List<OrderRuleOrderType>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleIdOrderRuleOrderTypesPage_getExpectedActions(id));

		orderRuleOrderTypeResource.deleteOrderRuleOrderType(
			orderRuleOrderType1.getOrderRuleOrderTypeId());

		orderRuleOrderTypeResource.deleteOrderRuleOrderType(
			orderRuleOrderType2.getOrderRuleOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleIdOrderRuleOrderTypesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleOrderTypesPageWithPagination()
		throws Exception {

		Long id = testGetOrderRuleIdOrderRuleOrderTypesPage_getId();

		Page<OrderRuleOrderType> orderRuleOrderTypesPage =
			orderRuleOrderTypeResource.getOrderRuleIdOrderRuleOrderTypesPage(
				id, null, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleOrderTypesPage.getTotalCount());

		OrderRuleOrderType orderRuleOrderType1 =
			testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
				id, randomOrderRuleOrderType());

		OrderRuleOrderType orderRuleOrderType2 =
			testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
				id, randomOrderRuleOrderType());

		OrderRuleOrderType orderRuleOrderType3 =
			testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
				id, randomOrderRuleOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleOrderType> page1 =
				orderRuleOrderTypeResource.
					getOrderRuleIdOrderRuleOrderTypesPage(
						id, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleOrderType1,
				(List<OrderRuleOrderType>)page1.getItems());

			Page<OrderRuleOrderType> page2 =
				orderRuleOrderTypeResource.
					getOrderRuleIdOrderRuleOrderTypesPage(
						id, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleOrderType2,
				(List<OrderRuleOrderType>)page2.getItems());

			Page<OrderRuleOrderType> page3 =
				orderRuleOrderTypeResource.
					getOrderRuleIdOrderRuleOrderTypesPage(
						id, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleOrderType3,
				(List<OrderRuleOrderType>)page3.getItems());
		}
		else {
			Page<OrderRuleOrderType> page1 =
				orderRuleOrderTypeResource.
					getOrderRuleIdOrderRuleOrderTypesPage(
						id, null, Pagination.of(1, totalCount + 2));

			List<OrderRuleOrderType> orderRuleOrderTypes1 =
				(List<OrderRuleOrderType>)page1.getItems();

			Assert.assertEquals(
				orderRuleOrderTypes1.toString(), totalCount + 2,
				orderRuleOrderTypes1.size());

			Page<OrderRuleOrderType> page2 =
				orderRuleOrderTypeResource.
					getOrderRuleIdOrderRuleOrderTypesPage(
						id, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleOrderType> orderRuleOrderTypes2 =
				(List<OrderRuleOrderType>)page2.getItems();

			Assert.assertEquals(
				orderRuleOrderTypes2.toString(), 1,
				orderRuleOrderTypes2.size());

			Page<OrderRuleOrderType> page3 =
				orderRuleOrderTypeResource.
					getOrderRuleIdOrderRuleOrderTypesPage(
						id, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				orderRuleOrderType1,
				(List<OrderRuleOrderType>)page3.getItems());
			assertContains(
				orderRuleOrderType2,
				(List<OrderRuleOrderType>)page3.getItems());
			assertContains(
				orderRuleOrderType3,
				(List<OrderRuleOrderType>)page3.getItems());
		}
	}

	protected OrderRuleOrderType
			testGetOrderRuleIdOrderRuleOrderTypesPage_addOrderRuleOrderType(
				Long id, OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleIdOrderRuleOrderTypesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleIdOrderRuleOrderTypesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostOrderRuleByExternalReferenceCodeOrderRuleOrderType()
		throws Exception {

		OrderRuleOrderType randomOrderRuleOrderType =
			randomOrderRuleOrderType();

		OrderRuleOrderType postOrderRuleOrderType =
			testPostOrderRuleByExternalReferenceCodeOrderRuleOrderType_addOrderRuleOrderType(
				randomOrderRuleOrderType);

		assertEquals(randomOrderRuleOrderType, postOrderRuleOrderType);
		assertValid(postOrderRuleOrderType);
	}

	protected OrderRuleOrderType
			testPostOrderRuleByExternalReferenceCodeOrderRuleOrderType_addOrderRuleOrderType(
				OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderRuleIdOrderRuleOrderType() throws Exception {
		OrderRuleOrderType randomOrderRuleOrderType =
			randomOrderRuleOrderType();

		OrderRuleOrderType postOrderRuleOrderType =
			testPostOrderRuleIdOrderRuleOrderType_addOrderRuleOrderType(
				randomOrderRuleOrderType);

		assertEquals(randomOrderRuleOrderType, postOrderRuleOrderType);
		assertValid(postOrderRuleOrderType);
	}

	protected OrderRuleOrderType
			testPostOrderRuleIdOrderRuleOrderType_addOrderRuleOrderType(
				OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderRuleOrderType orderRuleOrderType1 =
			testBatchEngineDeleteImportTask_addOrderRuleOrderType();

		testBatchEngineDeleteImportTask_deleteOrderRuleOrderType(
			200, null, orderRuleOrderType1.getOrderRuleOrderTypeId());
	}

	protected OrderRuleOrderType
			testBatchEngineDeleteImportTask_addOrderRuleOrderType()
		throws Exception {

		return testDeleteOrderRuleOrderType_addOrderRuleOrderType();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderRuleOrderType(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleOrderType",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"orderRuleOrderTypeId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected OrderRuleOrderType
			testGraphQLOrderRuleOrderType_addOrderRuleOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		OrderRuleOrderType orderRuleOrderType,
		List<OrderRuleOrderType> orderRuleOrderTypes) {

		boolean contains = false;

		for (OrderRuleOrderType item : orderRuleOrderTypes) {
			if (equals(orderRuleOrderType, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderRuleOrderTypes + " does not contain " + orderRuleOrderType,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OrderRuleOrderType orderRuleOrderType1,
		OrderRuleOrderType orderRuleOrderType2) {

		Assert.assertTrue(
			orderRuleOrderType1 + " does not equal " + orderRuleOrderType2,
			equals(orderRuleOrderType1, orderRuleOrderType2));
	}

	protected void assertEquals(
		List<OrderRuleOrderType> orderRuleOrderTypes1,
		List<OrderRuleOrderType> orderRuleOrderTypes2) {

		Assert.assertEquals(
			orderRuleOrderTypes1.size(), orderRuleOrderTypes2.size());

		for (int i = 0; i < orderRuleOrderTypes1.size(); i++) {
			OrderRuleOrderType orderRuleOrderType1 = orderRuleOrderTypes1.get(
				i);
			OrderRuleOrderType orderRuleOrderType2 = orderRuleOrderTypes2.get(
				i);

			assertEquals(orderRuleOrderType1, orderRuleOrderType2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderRuleOrderType> orderRuleOrderTypes1,
		List<OrderRuleOrderType> orderRuleOrderTypes2) {

		Assert.assertEquals(
			orderRuleOrderTypes1.size(), orderRuleOrderTypes2.size());

		for (OrderRuleOrderType orderRuleOrderType1 : orderRuleOrderTypes1) {
			boolean contains = false;

			for (OrderRuleOrderType orderRuleOrderType2 :
					orderRuleOrderTypes2) {

				if (equals(orderRuleOrderType1, orderRuleOrderType2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderRuleOrderTypes2 + " does not contain " +
					orderRuleOrderType1,
				contains);
		}
	}

	protected void assertValid(OrderRuleOrderType orderRuleOrderType)
		throws Exception {

		boolean valid = true;

		if (orderRuleOrderType.getOrderRuleOrderTypeId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (orderRuleOrderType.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleOrderType.getOrderRuleExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (orderRuleOrderType.getOrderRuleId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleOrderTypeId", additionalAssertFieldName)) {

				if (orderRuleOrderType.getOrderRuleOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (orderRuleOrderType.getOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleOrderType.getOrderTypeExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (orderRuleOrderType.getOrderTypeId() == null) {
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

	protected void assertValid(Page<OrderRuleOrderType> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderRuleOrderType> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderRuleOrderType> orderRuleOrderTypes =
			page.getItems();

		int size = orderRuleOrderTypes.size();

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

		graphQLFields.add(new GraphQLField("orderRuleOrderTypeId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.order.dto.v1_0.
						OrderRuleOrderType.class)) {

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
		OrderRuleOrderType orderRuleOrderType1,
		OrderRuleOrderType orderRuleOrderType2) {

		if (orderRuleOrderType1 == orderRuleOrderType2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderRuleOrderType1.getActions(),
						(Map)orderRuleOrderType2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleOrderType1.getOrderRuleExternalReferenceCode(),
						orderRuleOrderType2.
							getOrderRuleExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleOrderType1.getOrderRuleId(),
						orderRuleOrderType2.getOrderRuleId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleOrderTypeId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleOrderType1.getOrderRuleOrderTypeId(),
						orderRuleOrderType2.getOrderRuleOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleOrderType1.getOrderType(),
						orderRuleOrderType2.getOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleOrderType1.getOrderTypeExternalReferenceCode(),
						orderRuleOrderType2.
							getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleOrderType1.getOrderTypeId(),
						orderRuleOrderType2.getOrderTypeId())) {

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

		if (!(_orderRuleOrderTypeResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderRuleOrderTypeResource;

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
		OrderRuleOrderType orderRuleOrderType) {

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

		if (entityFieldName.equals("orderRuleExternalReferenceCode")) {
			Object object =
				orderRuleOrderType.getOrderRuleExternalReferenceCode();

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

		if (entityFieldName.equals("orderRuleId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleOrderTypeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object =
				orderRuleOrderType.getOrderTypeExternalReferenceCode();

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

	protected OrderRuleOrderType randomOrderRuleOrderType() throws Exception {
		return new OrderRuleOrderType() {
			{
				orderRuleExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderRuleId = RandomTestUtil.randomLong();
				orderRuleOrderTypeId = RandomTestUtil.randomLong();
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
			}
		};
	}

	protected OrderRuleOrderType randomIrrelevantOrderRuleOrderType()
		throws Exception {

		OrderRuleOrderType randomIrrelevantOrderRuleOrderType =
			randomOrderRuleOrderType();

		return randomIrrelevantOrderRuleOrderType;
	}

	protected OrderRuleOrderType randomPatchOrderRuleOrderType()
		throws Exception {

		return randomOrderRuleOrderType();
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

	protected OrderRuleOrderTypeResource orderRuleOrderTypeResource;
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
		LogFactoryUtil.getLog(BaseOrderRuleOrderTypeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.order.resource.v1_0.
		OrderRuleOrderTypeResource _orderRuleOrderTypeResource;

}