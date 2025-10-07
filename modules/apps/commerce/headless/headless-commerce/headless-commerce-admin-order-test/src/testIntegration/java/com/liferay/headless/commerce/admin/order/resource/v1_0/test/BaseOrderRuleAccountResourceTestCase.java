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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleAccount;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderRuleAccountResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderRuleAccountSerDes;
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
public abstract class BaseOrderRuleAccountResourceTestCase {

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

		_orderRuleAccountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderRuleAccountResource = OrderRuleAccountResource.builder(
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

		OrderRuleAccount orderRuleAccount1 = randomOrderRuleAccount();

		String json = objectMapper.writeValueAsString(orderRuleAccount1);

		OrderRuleAccount orderRuleAccount2 = OrderRuleAccountSerDes.toDTO(json);

		Assert.assertTrue(equals(orderRuleAccount1, orderRuleAccount2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderRuleAccount orderRuleAccount = randomOrderRuleAccount();

		String json1 = objectMapper.writeValueAsString(orderRuleAccount);
		String json2 = OrderRuleAccountSerDes.toJSON(orderRuleAccount);

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

		OrderRuleAccount orderRuleAccount = randomOrderRuleAccount();

		orderRuleAccount.setAccountExternalReferenceCode(regex);
		orderRuleAccount.setOrderRuleExternalReferenceCode(regex);

		String json = OrderRuleAccountSerDes.toJSON(orderRuleAccount);

		Assert.assertFalse(json.contains(regex));

		orderRuleAccount = OrderRuleAccountSerDes.toDTO(json);

		Assert.assertEquals(
			regex, orderRuleAccount.getAccountExternalReferenceCode());
		Assert.assertEquals(
			regex, orderRuleAccount.getOrderRuleExternalReferenceCode());
	}

	@Test
	public void testDeleteOrderRuleAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRuleAccount orderRuleAccount =
			testDeleteOrderRuleAccount_addOrderRuleAccount();

		assertHttpResponseStatusCode(
			204,
			orderRuleAccountResource.deleteOrderRuleAccountHttpResponse(
				orderRuleAccount.getOrderRuleAccountId()));
	}

	protected OrderRuleAccount testDeleteOrderRuleAccount_addOrderRuleAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderRuleAccount() throws Exception {

		// No namespace

		OrderRuleAccount orderRuleAccount1 =
			testGraphQLDeleteOrderRuleAccount_addOrderRuleAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderRuleAccount",
						new HashMap<String, Object>() {
							{
								put(
									"orderRuleAccountId",
									orderRuleAccount1.getOrderRuleAccountId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderRuleAccount"));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderRuleAccount orderRuleAccount2 =
			testGraphQLDeleteOrderRuleAccount_addOrderRuleAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderRuleAccount",
							new HashMap<String, Object>() {
								{
									put(
										"orderRuleAccountId",
										orderRuleAccount2.
											getOrderRuleAccountId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderRuleAccount"));
	}

	protected OrderRuleAccount
			testGraphQLDeleteOrderRuleAccount_addOrderRuleAccount()
		throws Exception {

		return testGraphQLOrderRuleAccount_addOrderRuleAccount();
	}

	@Test
	public void testDeleteOrderRuleAccountBatch() throws Exception {
		OrderRuleAccount orderRuleAccount1 =
			testDeleteOrderRuleAccountBatch_addOrderRuleAccount();

		testDeleteOrderRuleAccountBatch_deleteOrderRuleAccount(
			202, null, orderRuleAccount1.getOrderRuleAccountId());
	}

	protected OrderRuleAccount
			testDeleteOrderRuleAccountBatch_addOrderRuleAccount()
		throws Exception {

		return testDeleteOrderRuleAccount_addOrderRuleAccount();
	}

	protected void testDeleteOrderRuleAccountBatch_deleteOrderRuleAccount(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderRuleAccountResource.deleteOrderRuleAccountBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"orderRuleAccountId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getIrrelevantExternalReferenceCode();

		Page<OrderRuleAccount> page =
			orderRuleAccountResource.
				getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			OrderRuleAccount irrelevantOrderRuleAccount =
				testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantOrderRuleAccount());

			page =
				orderRuleAccountResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleAccount,
				(List<OrderRuleAccount>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		OrderRuleAccount orderRuleAccount1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
				externalReferenceCode, randomOrderRuleAccount());

		OrderRuleAccount orderRuleAccount2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
				externalReferenceCode, randomOrderRuleAccount());

		page =
			orderRuleAccountResource.
				getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleAccount1, (List<OrderRuleAccount>)page.getItems());
		assertContains(
			orderRuleAccount2, (List<OrderRuleAccount>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getExpectedActions(
				externalReferenceCode));

		orderRuleAccountResource.deleteOrderRuleAccount(
			orderRuleAccount1.getOrderRuleAccountId());

		orderRuleAccountResource.deleteOrderRuleAccount(
			orderRuleAccount2.getOrderRuleAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getExternalReferenceCode();

		Page<OrderRuleAccount> orderRuleAccountsPage =
			orderRuleAccountResource.
				getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleAccountsPage.getTotalCount());

		OrderRuleAccount orderRuleAccount1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
				externalReferenceCode, randomOrderRuleAccount());

		OrderRuleAccount orderRuleAccount2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
				externalReferenceCode, randomOrderRuleAccount());

		OrderRuleAccount orderRuleAccount3 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
				externalReferenceCode, randomOrderRuleAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleAccount> page1 =
				orderRuleAccountResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleAccount1, (List<OrderRuleAccount>)page1.getItems());

			Page<OrderRuleAccount> page2 =
				orderRuleAccountResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleAccount2, (List<OrderRuleAccount>)page2.getItems());

			Page<OrderRuleAccount> page3 =
				orderRuleAccountResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleAccount3, (List<OrderRuleAccount>)page3.getItems());
		}
		else {
			Page<OrderRuleAccount> page1 =
				orderRuleAccountResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<OrderRuleAccount> orderRuleAccounts1 =
				(List<OrderRuleAccount>)page1.getItems();

			Assert.assertEquals(
				orderRuleAccounts1.toString(), totalCount + 2,
				orderRuleAccounts1.size());

			Page<OrderRuleAccount> page2 =
				orderRuleAccountResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleAccount> orderRuleAccounts2 =
				(List<OrderRuleAccount>)page2.getItems();

			Assert.assertEquals(
				orderRuleAccounts2.toString(), 1, orderRuleAccounts2.size());

			Page<OrderRuleAccount> page3 =
				orderRuleAccountResource.
					getOrderRuleByExternalReferenceCodeOrderRuleAccountsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				orderRuleAccount1, (List<OrderRuleAccount>)page3.getItems());
			assertContains(
				orderRuleAccount2, (List<OrderRuleAccount>)page3.getItems());
			assertContains(
				orderRuleAccount3, (List<OrderRuleAccount>)page3.getItems());
		}
	}

	protected OrderRuleAccount
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_addOrderRuleAccount(
				String externalReferenceCode, OrderRuleAccount orderRuleAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPage() throws Exception {
		Long id = testGetOrderRuleIdOrderRuleAccountsPage_getId();
		Long irrelevantId =
			testGetOrderRuleIdOrderRuleAccountsPage_getIrrelevantId();

		Page<OrderRuleAccount> page =
			orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			OrderRuleAccount irrelevantOrderRuleAccount =
				testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
					irrelevantId, randomIrrelevantOrderRuleAccount());

			page = orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleAccount,
				(List<OrderRuleAccount>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleIdOrderRuleAccountsPage_getExpectedActions(
					irrelevantId));
		}

		OrderRuleAccount orderRuleAccount1 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, randomOrderRuleAccount());

		OrderRuleAccount orderRuleAccount2 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, randomOrderRuleAccount());

		page = orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleAccount1, (List<OrderRuleAccount>)page.getItems());
		assertContains(
			orderRuleAccount2, (List<OrderRuleAccount>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleIdOrderRuleAccountsPage_getExpectedActions(id));

		orderRuleAccountResource.deleteOrderRuleAccount(
			orderRuleAccount1.getOrderRuleAccountId());

		orderRuleAccountResource.deleteOrderRuleAccount(
			orderRuleAccount2.getOrderRuleAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleIdOrderRuleAccountsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleAccountsPage_getId();

		OrderRuleAccount orderRuleAccount1 = randomOrderRuleAccount();

		orderRuleAccount1 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, orderRuleAccount1);

		for (EntityField entityField : entityFields) {
			Page<OrderRuleAccount> page =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null,
					getFilterString(entityField, "between", orderRuleAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRuleAccount1),
				(List<OrderRuleAccount>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithFilterStringContains()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderRuleIdOrderRuleAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleAccountsPage_getId();

		OrderRuleAccount orderRuleAccount1 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, randomOrderRuleAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRuleAccount orderRuleAccount2 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, randomOrderRuleAccount());

		for (EntityField entityField : entityFields) {
			Page<OrderRuleAccount> page =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null,
					getFilterString(entityField, operator, orderRuleAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRuleAccount1),
				(List<OrderRuleAccount>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithPagination()
		throws Exception {

		Long id = testGetOrderRuleIdOrderRuleAccountsPage_getId();

		Page<OrderRuleAccount> orderRuleAccountsPage =
			orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleAccountsPage.getTotalCount());

		OrderRuleAccount orderRuleAccount1 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, randomOrderRuleAccount());

		OrderRuleAccount orderRuleAccount2 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, randomOrderRuleAccount());

		OrderRuleAccount orderRuleAccount3 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, randomOrderRuleAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleAccount> page1 =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleAccount1, (List<OrderRuleAccount>)page1.getItems());

			Page<OrderRuleAccount> page2 =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				orderRuleAccount2, (List<OrderRuleAccount>)page2.getItems());

			Page<OrderRuleAccount> page3 =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				orderRuleAccount3, (List<OrderRuleAccount>)page3.getItems());
		}
		else {
			Page<OrderRuleAccount> page1 =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<OrderRuleAccount> orderRuleAccounts1 =
				(List<OrderRuleAccount>)page1.getItems();

			Assert.assertEquals(
				orderRuleAccounts1.toString(), totalCount + 2,
				orderRuleAccounts1.size());

			Page<OrderRuleAccount> page2 =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleAccount> orderRuleAccounts2 =
				(List<OrderRuleAccount>)page2.getItems();

			Assert.assertEquals(
				orderRuleAccounts2.toString(), 1, orderRuleAccounts2.size());

			Page<OrderRuleAccount> page3 =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				orderRuleAccount1, (List<OrderRuleAccount>)page3.getItems());
			assertContains(
				orderRuleAccount2, (List<OrderRuleAccount>)page3.getItems());
			assertContains(
				orderRuleAccount3, (List<OrderRuleAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithSortDateTime()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, orderRuleAccount1, orderRuleAccount2) -> {
				BeanTestUtil.setProperty(
					orderRuleAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithSortDouble()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, orderRuleAccount1, orderRuleAccount2) -> {
				BeanTestUtil.setProperty(
					orderRuleAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					orderRuleAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithSortInteger()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, orderRuleAccount1, orderRuleAccount2) -> {
				BeanTestUtil.setProperty(
					orderRuleAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					orderRuleAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleAccountsPageWithSortString()
		throws Exception {

		testGetOrderRuleIdOrderRuleAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, orderRuleAccount1, orderRuleAccount2) -> {
				Class<?> clazz = orderRuleAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						orderRuleAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						orderRuleAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						orderRuleAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						orderRuleAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						orderRuleAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						orderRuleAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderRuleIdOrderRuleAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, OrderRuleAccount, OrderRuleAccount, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleAccountsPage_getId();

		OrderRuleAccount orderRuleAccount1 = randomOrderRuleAccount();
		OrderRuleAccount orderRuleAccount2 = randomOrderRuleAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, orderRuleAccount1, orderRuleAccount2);
		}

		orderRuleAccount1 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, orderRuleAccount1);

		orderRuleAccount2 =
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				id, orderRuleAccount2);

		Page<OrderRuleAccount> page =
			orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OrderRuleAccount> ascPage =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				orderRuleAccount1, (List<OrderRuleAccount>)ascPage.getItems());
			assertContains(
				orderRuleAccount2, (List<OrderRuleAccount>)ascPage.getItems());

			Page<OrderRuleAccount> descPage =
				orderRuleAccountResource.getOrderRuleIdOrderRuleAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				orderRuleAccount2, (List<OrderRuleAccount>)descPage.getItems());
			assertContains(
				orderRuleAccount1, (List<OrderRuleAccount>)descPage.getItems());
		}
	}

	protected OrderRuleAccount
			testGetOrderRuleIdOrderRuleAccountsPage_addOrderRuleAccount(
				Long id, OrderRuleAccount orderRuleAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleIdOrderRuleAccountsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleIdOrderRuleAccountsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostOrderRuleByExternalReferenceCodeOrderRuleAccount()
		throws Exception {

		OrderRuleAccount randomOrderRuleAccount = randomOrderRuleAccount();

		OrderRuleAccount postOrderRuleAccount =
			testPostOrderRuleByExternalReferenceCodeOrderRuleAccount_addOrderRuleAccount(
				randomOrderRuleAccount);

		assertEquals(randomOrderRuleAccount, postOrderRuleAccount);
		assertValid(postOrderRuleAccount);
	}

	protected OrderRuleAccount
			testPostOrderRuleByExternalReferenceCodeOrderRuleAccount_addOrderRuleAccount(
				OrderRuleAccount orderRuleAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderRuleIdOrderRuleAccount() throws Exception {
		OrderRuleAccount randomOrderRuleAccount = randomOrderRuleAccount();

		OrderRuleAccount postOrderRuleAccount =
			testPostOrderRuleIdOrderRuleAccount_addOrderRuleAccount(
				randomOrderRuleAccount);

		assertEquals(randomOrderRuleAccount, postOrderRuleAccount);
		assertValid(postOrderRuleAccount);
	}

	protected OrderRuleAccount
			testPostOrderRuleIdOrderRuleAccount_addOrderRuleAccount(
				OrderRuleAccount orderRuleAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderRuleAccount orderRuleAccount1 =
			testBatchEngineDeleteImportTask_addOrderRuleAccount();

		testBatchEngineDeleteImportTask_deleteOrderRuleAccount(
			200, null, orderRuleAccount1.getOrderRuleAccountId());
	}

	protected OrderRuleAccount
			testBatchEngineDeleteImportTask_addOrderRuleAccount()
		throws Exception {

		return testDeleteOrderRuleAccount_addOrderRuleAccount();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderRuleAccount(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleAccount",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"orderRuleAccountId", () -> id
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

	protected OrderRuleAccount testGraphQLOrderRuleAccount_addOrderRuleAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		OrderRuleAccount orderRuleAccount,
		List<OrderRuleAccount> orderRuleAccounts) {

		boolean contains = false;

		for (OrderRuleAccount item : orderRuleAccounts) {
			if (equals(orderRuleAccount, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderRuleAccounts + " does not contain " + orderRuleAccount,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OrderRuleAccount orderRuleAccount1,
		OrderRuleAccount orderRuleAccount2) {

		Assert.assertTrue(
			orderRuleAccount1 + " does not equal " + orderRuleAccount2,
			equals(orderRuleAccount1, orderRuleAccount2));
	}

	protected void assertEquals(
		List<OrderRuleAccount> orderRuleAccounts1,
		List<OrderRuleAccount> orderRuleAccounts2) {

		Assert.assertEquals(
			orderRuleAccounts1.size(), orderRuleAccounts2.size());

		for (int i = 0; i < orderRuleAccounts1.size(); i++) {
			OrderRuleAccount orderRuleAccount1 = orderRuleAccounts1.get(i);
			OrderRuleAccount orderRuleAccount2 = orderRuleAccounts2.get(i);

			assertEquals(orderRuleAccount1, orderRuleAccount2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderRuleAccount> orderRuleAccounts1,
		List<OrderRuleAccount> orderRuleAccounts2) {

		Assert.assertEquals(
			orderRuleAccounts1.size(), orderRuleAccounts2.size());

		for (OrderRuleAccount orderRuleAccount1 : orderRuleAccounts1) {
			boolean contains = false;

			for (OrderRuleAccount orderRuleAccount2 : orderRuleAccounts2) {
				if (equals(orderRuleAccount1, orderRuleAccount2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderRuleAccounts2 + " does not contain " + orderRuleAccount1,
				contains);
		}
	}

	protected void assertValid(OrderRuleAccount orderRuleAccount)
		throws Exception {

		boolean valid = true;

		if (orderRuleAccount.getOrderRuleAccountId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (orderRuleAccount.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleAccount.getAccountExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (orderRuleAccount.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (orderRuleAccount.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleAccountId", additionalAssertFieldName)) {

				if (orderRuleAccount.getOrderRuleAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleAccount.getOrderRuleExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (orderRuleAccount.getOrderRuleId() == null) {
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

	protected void assertValid(Page<OrderRuleAccount> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderRuleAccount> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderRuleAccount> orderRuleAccounts =
			page.getItems();

		int size = orderRuleAccounts.size();

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

		graphQLFields.add(new GraphQLField("orderRuleAccountId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.order.dto.v1_0.
						OrderRuleAccount.class)) {

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
		OrderRuleAccount orderRuleAccount1,
		OrderRuleAccount orderRuleAccount2) {

		if (orderRuleAccount1 == orderRuleAccount2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleAccount1.getAccount(),
						orderRuleAccount2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleAccount1.getAccountExternalReferenceCode(),
						orderRuleAccount2.getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleAccount1.getAccountId(),
						orderRuleAccount2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderRuleAccount1.getActions(),
						(Map)orderRuleAccount2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleAccountId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleAccount1.getOrderRuleAccountId(),
						orderRuleAccount2.getOrderRuleAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleAccount1.getOrderRuleExternalReferenceCode(),
						orderRuleAccount2.
							getOrderRuleExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleAccount1.getOrderRuleId(),
						orderRuleAccount2.getOrderRuleId())) {

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

		if (!(_orderRuleAccountResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderRuleAccountResource;

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
		OrderRuleAccount orderRuleAccount) {

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
			Object object = orderRuleAccount.getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("orderRuleAccountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleExternalReferenceCode")) {
			Object object =
				orderRuleAccount.getOrderRuleExternalReferenceCode();

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

	protected OrderRuleAccount randomOrderRuleAccount() throws Exception {
		return new OrderRuleAccount() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				orderRuleAccountId = RandomTestUtil.randomLong();
				orderRuleExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderRuleId = RandomTestUtil.randomLong();
			}
		};
	}

	protected OrderRuleAccount randomIrrelevantOrderRuleAccount()
		throws Exception {

		OrderRuleAccount randomIrrelevantOrderRuleAccount =
			randomOrderRuleAccount();

		return randomIrrelevantOrderRuleAccount;
	}

	protected OrderRuleAccount randomPatchOrderRuleAccount() throws Exception {
		return randomOrderRuleAccount();
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

	protected OrderRuleAccountResource orderRuleAccountResource;
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
		LogFactoryUtil.getLog(BaseOrderRuleAccountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.order.resource.v1_0.
		OrderRuleAccountResource _orderRuleAccountResource;

}