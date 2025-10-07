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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleAccountGroup;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderRuleAccountGroupResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderRuleAccountGroupSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
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
public abstract class BaseOrderRuleAccountGroupResourceTestCase {

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

		_orderRuleAccountGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderRuleAccountGroupResource = OrderRuleAccountGroupResource.builder(
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

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			randomOrderRuleAccountGroup();

		String json = objectMapper.writeValueAsString(orderRuleAccountGroup1);

		OrderRuleAccountGroup orderRuleAccountGroup2 =
			OrderRuleAccountGroupSerDes.toDTO(json);

		Assert.assertTrue(
			equals(orderRuleAccountGroup1, orderRuleAccountGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderRuleAccountGroup orderRuleAccountGroup =
			randomOrderRuleAccountGroup();

		String json1 = objectMapper.writeValueAsString(orderRuleAccountGroup);
		String json2 = OrderRuleAccountGroupSerDes.toJSON(
			orderRuleAccountGroup);

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

		OrderRuleAccountGroup orderRuleAccountGroup =
			randomOrderRuleAccountGroup();

		orderRuleAccountGroup.setAccountGroupExternalReferenceCode(regex);
		orderRuleAccountGroup.setOrderRuleExternalReferenceCode(regex);

		String json = OrderRuleAccountGroupSerDes.toJSON(orderRuleAccountGroup);

		Assert.assertFalse(json.contains(regex));

		orderRuleAccountGroup = OrderRuleAccountGroupSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			orderRuleAccountGroup.getAccountGroupExternalReferenceCode());
		Assert.assertEquals(
			regex, orderRuleAccountGroup.getOrderRuleExternalReferenceCode());
	}

	@Test
	public void testDeleteOrderRuleAccountGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRuleAccountGroup orderRuleAccountGroup =
			testDeleteOrderRuleAccountGroup_addOrderRuleAccountGroup();

		assertHttpResponseStatusCode(
			204,
			orderRuleAccountGroupResource.
				deleteOrderRuleAccountGroupHttpResponse(
					orderRuleAccountGroup.getOrderRuleAccountGroupId()));
	}

	protected OrderRuleAccountGroup
			testDeleteOrderRuleAccountGroup_addOrderRuleAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderRuleAccountGroup() throws Exception {

		// No namespace

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testGraphQLDeleteOrderRuleAccountGroup_addOrderRuleAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderRuleAccountGroup",
						new HashMap<String, Object>() {
							{
								put(
									"orderRuleAccountGroupId",
									orderRuleAccountGroup1.
										getOrderRuleAccountGroupId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderRuleAccountGroup"));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderRuleAccountGroup orderRuleAccountGroup2 =
			testGraphQLDeleteOrderRuleAccountGroup_addOrderRuleAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderRuleAccountGroup",
							new HashMap<String, Object>() {
								{
									put(
										"orderRuleAccountGroupId",
										orderRuleAccountGroup2.
											getOrderRuleAccountGroupId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderRuleAccountGroup"));
	}

	protected OrderRuleAccountGroup
			testGraphQLDeleteOrderRuleAccountGroup_addOrderRuleAccountGroup()
		throws Exception {

		return testGraphQLOrderRuleAccountGroup_addOrderRuleAccountGroup();
	}

	@Test
	public void testDeleteOrderRuleAccountGroupBatch() throws Exception {
		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testDeleteOrderRuleAccountGroupBatch_addOrderRuleAccountGroup();

		testDeleteOrderRuleAccountGroupBatch_deleteOrderRuleAccountGroup(
			202, null, orderRuleAccountGroup1.getOrderRuleAccountGroupId());
	}

	protected OrderRuleAccountGroup
			testDeleteOrderRuleAccountGroupBatch_addOrderRuleAccountGroup()
		throws Exception {

		return testDeleteOrderRuleAccountGroup_addOrderRuleAccountGroup();
	}

	protected void
			testDeleteOrderRuleAccountGroupBatch_deleteOrderRuleAccountGroup(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderRuleAccountGroupResource.
				deleteOrderRuleAccountGroupBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"orderRuleAccountGroupId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getIrrelevantExternalReferenceCode();

		Page<OrderRuleAccountGroup> page =
			orderRuleAccountGroupResource.
				getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			OrderRuleAccountGroup irrelevantOrderRuleAccountGroup =
				testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
					irrelevantExternalReferenceCode,
					randomIrrelevantOrderRuleAccountGroup());

			page =
				orderRuleAccountGroupResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleAccountGroup,
				(List<OrderRuleAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				externalReferenceCode, randomOrderRuleAccountGroup());

		OrderRuleAccountGroup orderRuleAccountGroup2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				externalReferenceCode, randomOrderRuleAccountGroup());

		page =
			orderRuleAccountGroupResource.
				getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleAccountGroup1,
			(List<OrderRuleAccountGroup>)page.getItems());
		assertContains(
			orderRuleAccountGroup2,
			(List<OrderRuleAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getExpectedActions(
				externalReferenceCode));

		orderRuleAccountGroupResource.deleteOrderRuleAccountGroup(
			orderRuleAccountGroup1.getOrderRuleAccountGroupId());

		orderRuleAccountGroupResource.deleteOrderRuleAccountGroup(
			orderRuleAccountGroup2.getOrderRuleAccountGroupId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getExternalReferenceCode();

		Page<OrderRuleAccountGroup> orderRuleAccountGroupsPage =
			orderRuleAccountGroupResource.
				getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleAccountGroupsPage.getTotalCount());

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				externalReferenceCode, randomOrderRuleAccountGroup());

		OrderRuleAccountGroup orderRuleAccountGroup2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				externalReferenceCode, randomOrderRuleAccountGroup());

		OrderRuleAccountGroup orderRuleAccountGroup3 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				externalReferenceCode, randomOrderRuleAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleAccountGroup> page1 =
				orderRuleAccountGroupResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleAccountGroup1,
				(List<OrderRuleAccountGroup>)page1.getItems());

			Page<OrderRuleAccountGroup> page2 =
				orderRuleAccountGroupResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleAccountGroup2,
				(List<OrderRuleAccountGroup>)page2.getItems());

			Page<OrderRuleAccountGroup> page3 =
				orderRuleAccountGroupResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleAccountGroup3,
				(List<OrderRuleAccountGroup>)page3.getItems());
		}
		else {
			Page<OrderRuleAccountGroup> page1 =
				orderRuleAccountGroupResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<OrderRuleAccountGroup> orderRuleAccountGroups1 =
				(List<OrderRuleAccountGroup>)page1.getItems();

			Assert.assertEquals(
				orderRuleAccountGroups1.toString(), totalCount + 2,
				orderRuleAccountGroups1.size());

			Page<OrderRuleAccountGroup> page2 =
				orderRuleAccountGroupResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleAccountGroup> orderRuleAccountGroups2 =
				(List<OrderRuleAccountGroup>)page2.getItems();

			Assert.assertEquals(
				orderRuleAccountGroups2.toString(), 1,
				orderRuleAccountGroups2.size());

			Page<OrderRuleAccountGroup> page3 =
				orderRuleAccountGroupResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				orderRuleAccountGroup1,
				(List<OrderRuleAccountGroup>)page3.getItems());
			assertContains(
				orderRuleAccountGroup2,
				(List<OrderRuleAccountGroup>)page3.getItems());
			assertContains(
				orderRuleAccountGroup3,
				(List<OrderRuleAccountGroup>)page3.getItems());
		}
	}

	protected OrderRuleAccountGroup
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				String externalReferenceCode,
				OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPage()
		throws Exception {

		Long id = testGetOrderRuleIdOrderRuleAccountGroupsPage_getId();
		Long irrelevantId =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_getIrrelevantId();

		Page<OrderRuleAccountGroup> page =
			orderRuleAccountGroupResource.
				getOrderRuleIdOrderRuleAccountGroupsPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			OrderRuleAccountGroup irrelevantOrderRuleAccountGroup =
				testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
					irrelevantId, randomIrrelevantOrderRuleAccountGroup());

			page =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleAccountGroup,
				(List<OrderRuleAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleIdOrderRuleAccountGroupsPage_getExpectedActions(
					irrelevantId));
		}

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, randomOrderRuleAccountGroup());

		OrderRuleAccountGroup orderRuleAccountGroup2 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, randomOrderRuleAccountGroup());

		page =
			orderRuleAccountGroupResource.
				getOrderRuleIdOrderRuleAccountGroupsPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleAccountGroup1,
			(List<OrderRuleAccountGroup>)page.getItems());
		assertContains(
			orderRuleAccountGroup2,
			(List<OrderRuleAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleIdOrderRuleAccountGroupsPage_getExpectedActions(
				id));

		orderRuleAccountGroupResource.deleteOrderRuleAccountGroup(
			orderRuleAccountGroup1.getOrderRuleAccountGroupId());

		orderRuleAccountGroupResource.deleteOrderRuleAccountGroup(
			orderRuleAccountGroup2.getOrderRuleAccountGroupId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleIdOrderRuleAccountGroupsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleAccountGroupsPage_getId();

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			randomOrderRuleAccountGroup();

		orderRuleAccountGroup1 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, orderRuleAccountGroup1);

		for (EntityField entityField : entityFields) {
			Page<OrderRuleAccountGroup> page =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null,
						getFilterString(
							entityField, "between", orderRuleAccountGroup1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRuleAccountGroup1),
				(List<OrderRuleAccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilterStringContains()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilterStringEquals()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderRuleIdOrderRuleAccountGroupsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleAccountGroupsPage_getId();

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, randomOrderRuleAccountGroup());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRuleAccountGroup orderRuleAccountGroup2 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, randomOrderRuleAccountGroup());

		for (EntityField entityField : entityFields) {
			Page<OrderRuleAccountGroup> page =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null,
						getFilterString(
							entityField, operator, orderRuleAccountGroup1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRuleAccountGroup1),
				(List<OrderRuleAccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithPagination()
		throws Exception {

		Long id = testGetOrderRuleIdOrderRuleAccountGroupsPage_getId();

		Page<OrderRuleAccountGroup> orderRuleAccountGroupsPage =
			orderRuleAccountGroupResource.
				getOrderRuleIdOrderRuleAccountGroupsPage(
					id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleAccountGroupsPage.getTotalCount());

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, randomOrderRuleAccountGroup());

		OrderRuleAccountGroup orderRuleAccountGroup2 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, randomOrderRuleAccountGroup());

		OrderRuleAccountGroup orderRuleAccountGroup3 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, randomOrderRuleAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleAccountGroup> page1 =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleAccountGroup1,
				(List<OrderRuleAccountGroup>)page1.getItems());

			Page<OrderRuleAccountGroup> page2 =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				orderRuleAccountGroup2,
				(List<OrderRuleAccountGroup>)page2.getItems());

			Page<OrderRuleAccountGroup> page3 =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				orderRuleAccountGroup3,
				(List<OrderRuleAccountGroup>)page3.getItems());
		}
		else {
			Page<OrderRuleAccountGroup> page1 =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<OrderRuleAccountGroup> orderRuleAccountGroups1 =
				(List<OrderRuleAccountGroup>)page1.getItems();

			Assert.assertEquals(
				orderRuleAccountGroups1.toString(), totalCount + 2,
				orderRuleAccountGroups1.size());

			Page<OrderRuleAccountGroup> page2 =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleAccountGroup> orderRuleAccountGroups2 =
				(List<OrderRuleAccountGroup>)page2.getItems();

			Assert.assertEquals(
				orderRuleAccountGroups2.toString(), 1,
				orderRuleAccountGroups2.size());

			Page<OrderRuleAccountGroup> page3 =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				orderRuleAccountGroup1,
				(List<OrderRuleAccountGroup>)page3.getItems());
			assertContains(
				orderRuleAccountGroup2,
				(List<OrderRuleAccountGroup>)page3.getItems());
			assertContains(
				orderRuleAccountGroup3,
				(List<OrderRuleAccountGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithSortDateTime()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, orderRuleAccountGroup1, orderRuleAccountGroup2) -> {
				BeanTestUtil.setProperty(
					orderRuleAccountGroup1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithSortDouble()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, orderRuleAccountGroup1, orderRuleAccountGroup2) -> {
				BeanTestUtil.setProperty(
					orderRuleAccountGroup1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					orderRuleAccountGroup2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithSortInteger()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, orderRuleAccountGroup1, orderRuleAccountGroup2) -> {
				BeanTestUtil.setProperty(
					orderRuleAccountGroup1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					orderRuleAccountGroup2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountGroupsPageWithSortString()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, orderRuleAccountGroup1, orderRuleAccountGroup2) -> {
				Class<?> clazz = orderRuleAccountGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						orderRuleAccountGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						orderRuleAccountGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						orderRuleAccountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						orderRuleAccountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						orderRuleAccountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						orderRuleAccountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderRuleIdOrderRuleAccountGroupsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, OrderRuleAccountGroup, OrderRuleAccountGroup,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleAccountGroupsPage_getId();

		OrderRuleAccountGroup orderRuleAccountGroup1 =
			randomOrderRuleAccountGroup();
		OrderRuleAccountGroup orderRuleAccountGroup2 =
			randomOrderRuleAccountGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, orderRuleAccountGroup1, orderRuleAccountGroup2);
		}

		orderRuleAccountGroup1 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, orderRuleAccountGroup1);

		orderRuleAccountGroup2 =
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				id, orderRuleAccountGroup2);

		Page<OrderRuleAccountGroup> page =
			orderRuleAccountGroupResource.
				getOrderRuleIdOrderRuleAccountGroupsPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OrderRuleAccountGroup> ascPage =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				orderRuleAccountGroup1,
				(List<OrderRuleAccountGroup>)ascPage.getItems());
			assertContains(
				orderRuleAccountGroup2,
				(List<OrderRuleAccountGroup>)ascPage.getItems());

			Page<OrderRuleAccountGroup> descPage =
				orderRuleAccountGroupResource.
					getOrderRuleIdOrderRuleAccountGroupsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				orderRuleAccountGroup2,
				(List<OrderRuleAccountGroup>)descPage.getItems());
			assertContains(
				orderRuleAccountGroup1,
				(List<OrderRuleAccountGroup>)descPage.getItems());
		}
	}

	protected OrderRuleAccountGroup
			testGetOrderRuleIdOrderRuleAccountGroupsPage_addOrderRuleAccountGroup(
				Long id, OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleIdOrderRuleAccountGroupsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetOrderRuleIdOrderRuleAccountGroupsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostOrderRuleByExternalReferenceCodeOrderRuleAccountGroup()
		throws Exception {

		OrderRuleAccountGroup randomOrderRuleAccountGroup =
			randomOrderRuleAccountGroup();

		OrderRuleAccountGroup postOrderRuleAccountGroup =
			testPostOrderRuleByExternalReferenceCodeOrderRuleAccountGroup_addOrderRuleAccountGroup(
				randomOrderRuleAccountGroup);

		assertEquals(randomOrderRuleAccountGroup, postOrderRuleAccountGroup);
		assertValid(postOrderRuleAccountGroup);
	}

	protected OrderRuleAccountGroup
			testPostOrderRuleByExternalReferenceCodeOrderRuleAccountGroup_addOrderRuleAccountGroup(
				OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderRuleIdOrderRuleAccountGroup() throws Exception {
		OrderRuleAccountGroup randomOrderRuleAccountGroup =
			randomOrderRuleAccountGroup();

		OrderRuleAccountGroup postOrderRuleAccountGroup =
			testPostOrderRuleIdOrderRuleAccountGroup_addOrderRuleAccountGroup(
				randomOrderRuleAccountGroup);

		assertEquals(randomOrderRuleAccountGroup, postOrderRuleAccountGroup);
		assertValid(postOrderRuleAccountGroup);
	}

	protected OrderRuleAccountGroup
			testPostOrderRuleIdOrderRuleAccountGroup_addOrderRuleAccountGroup(
				OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderRuleAccountGroup orderRuleAccountGroup1 =
			testBatchEngineDeleteImportTask_addOrderRuleAccountGroup();

		testBatchEngineDeleteImportTask_deleteOrderRuleAccountGroup(
			200, null, orderRuleAccountGroup1.getOrderRuleAccountGroupId());
	}

	protected OrderRuleAccountGroup
			testBatchEngineDeleteImportTask_addOrderRuleAccountGroup()
		throws Exception {

		return testDeleteOrderRuleAccountGroup_addOrderRuleAccountGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderRuleAccountGroup(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleAccountGroup",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"orderRuleAccountGroupId", () -> id
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

	protected OrderRuleAccountGroup
			testGraphQLOrderRuleAccountGroup_addOrderRuleAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		OrderRuleAccountGroup orderRuleAccountGroup,
		List<OrderRuleAccountGroup> orderRuleAccountGroups) {

		boolean contains = false;

		for (OrderRuleAccountGroup item : orderRuleAccountGroups) {
			if (equals(orderRuleAccountGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderRuleAccountGroups + " does not contain " +
				orderRuleAccountGroup,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OrderRuleAccountGroup orderRuleAccountGroup1,
		OrderRuleAccountGroup orderRuleAccountGroup2) {

		Assert.assertTrue(
			orderRuleAccountGroup1 + " does not equal " +
				orderRuleAccountGroup2,
			equals(orderRuleAccountGroup1, orderRuleAccountGroup2));
	}

	protected void assertEquals(
		List<OrderRuleAccountGroup> orderRuleAccountGroups1,
		List<OrderRuleAccountGroup> orderRuleAccountGroups2) {

		Assert.assertEquals(
			orderRuleAccountGroups1.size(), orderRuleAccountGroups2.size());

		for (int i = 0; i < orderRuleAccountGroups1.size(); i++) {
			OrderRuleAccountGroup orderRuleAccountGroup1 =
				orderRuleAccountGroups1.get(i);
			OrderRuleAccountGroup orderRuleAccountGroup2 =
				orderRuleAccountGroups2.get(i);

			assertEquals(orderRuleAccountGroup1, orderRuleAccountGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderRuleAccountGroup> orderRuleAccountGroups1,
		List<OrderRuleAccountGroup> orderRuleAccountGroups2) {

		Assert.assertEquals(
			orderRuleAccountGroups1.size(), orderRuleAccountGroups2.size());

		for (OrderRuleAccountGroup orderRuleAccountGroup1 :
				orderRuleAccountGroups1) {

			boolean contains = false;

			for (OrderRuleAccountGroup orderRuleAccountGroup2 :
					orderRuleAccountGroups2) {

				if (equals(orderRuleAccountGroup1, orderRuleAccountGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderRuleAccountGroups2 + " does not contain " +
					orderRuleAccountGroup1,
				contains);
		}
	}

	protected void assertValid(OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		boolean valid = true;

		if (orderRuleAccountGroup.getOrderRuleAccountGroupId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroup", additionalAssertFieldName)) {
				if (orderRuleAccountGroup.getAccountGroup() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleAccountGroup.
						getAccountGroupExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (orderRuleAccountGroup.getAccountGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (orderRuleAccountGroup.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleAccountGroupId", additionalAssertFieldName)) {

				if (orderRuleAccountGroup.getOrderRuleAccountGroupId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleAccountGroup.getOrderRuleExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (orderRuleAccountGroup.getOrderRuleId() == null) {
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

	protected void assertValid(Page<OrderRuleAccountGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderRuleAccountGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderRuleAccountGroup> orderRuleAccountGroups =
			page.getItems();

		int size = orderRuleAccountGroups.size();

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

		graphQLFields.add(new GraphQLField("orderRuleAccountGroupId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.order.dto.v1_0.
						OrderRuleAccountGroup.class)) {

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
		OrderRuleAccountGroup orderRuleAccountGroup1,
		OrderRuleAccountGroup orderRuleAccountGroup2) {

		if (orderRuleAccountGroup1 == orderRuleAccountGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroup", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleAccountGroup1.getAccountGroup(),
						orderRuleAccountGroup2.getAccountGroup())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleAccountGroup1.
							getAccountGroupExternalReferenceCode(),
						orderRuleAccountGroup2.
							getAccountGroupExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleAccountGroup1.getAccountGroupId(),
						orderRuleAccountGroup2.getAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderRuleAccountGroup1.getActions(),
						(Map)orderRuleAccountGroup2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleAccountGroupId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleAccountGroup1.getOrderRuleAccountGroupId(),
						orderRuleAccountGroup2.getOrderRuleAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleAccountGroup1.
							getOrderRuleExternalReferenceCode(),
						orderRuleAccountGroup2.
							getOrderRuleExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleAccountGroup1.getOrderRuleId(),
						orderRuleAccountGroup2.getOrderRuleId())) {

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

		if (!(_orderRuleAccountGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderRuleAccountGroupResource;

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
		OrderRuleAccountGroup orderRuleAccountGroup) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountGroup")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountGroupExternalReferenceCode")) {
			Object object =
				orderRuleAccountGroup.getAccountGroupExternalReferenceCode();

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

		if (entityFieldName.equals("accountGroupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleAccountGroupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleExternalReferenceCode")) {
			Object object =
				orderRuleAccountGroup.getOrderRuleExternalReferenceCode();

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

	protected OrderRuleAccountGroup randomOrderRuleAccountGroup()
		throws Exception {

		return new OrderRuleAccountGroup() {
			{
				accountGroupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountGroupId = RandomTestUtil.randomLong();
				orderRuleAccountGroupId = RandomTestUtil.randomLong();
				orderRuleExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderRuleId = RandomTestUtil.randomLong();
			}
		};
	}

	protected OrderRuleAccountGroup randomIrrelevantOrderRuleAccountGroup()
		throws Exception {

		OrderRuleAccountGroup randomIrrelevantOrderRuleAccountGroup =
			randomOrderRuleAccountGroup();

		return randomIrrelevantOrderRuleAccountGroup;
	}

	protected OrderRuleAccountGroup randomPatchOrderRuleAccountGroup()
		throws Exception {

		return randomOrderRuleAccountGroup();
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

	protected OrderRuleAccountGroupResource orderRuleAccountGroupResource;
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
		LogFactoryUtil.getLog(BaseOrderRuleAccountGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.order.resource.v1_0.
		OrderRuleAccountGroupResource _orderRuleAccountGroupResource;

}