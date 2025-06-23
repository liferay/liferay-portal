/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrder;
import com.liferay.headless.commerce.delivery.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.order.client.pagination.Page;
import com.liferay.headless.commerce.delivery.order.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.order.client.resource.v1_0.PlacedOrderResource;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.PlacedOrderSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BasePlacedOrderResourceTestCase {

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

		_placedOrderResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		placedOrderResource = PlacedOrderResource.builder(
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

		PlacedOrder placedOrder1 = randomPlacedOrder();

		String json = objectMapper.writeValueAsString(placedOrder1);

		PlacedOrder placedOrder2 = PlacedOrderSerDes.toDTO(json);

		Assert.assertTrue(equals(placedOrder1, placedOrder2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PlacedOrder placedOrder = randomPlacedOrder();

		String json1 = objectMapper.writeValueAsString(placedOrder);
		String json2 = PlacedOrderSerDes.toJSON(placedOrder);

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

		PlacedOrder placedOrder = randomPlacedOrder();

		placedOrder.setAccount(regex);
		placedOrder.setAuthor(regex);
		placedOrder.setCouponCode(regex);
		placedOrder.setCurrencyCode(regex);
		placedOrder.setExternalReferenceCode(regex);
		placedOrder.setFriendlyURLSeparator(regex);
		placedOrder.setName(regex);
		placedOrder.setOrderType(regex);
		placedOrder.setOrderTypeExternalReferenceCode(regex);
		placedOrder.setOrderUUID(regex);
		placedOrder.setPaymentMethod(regex);
		placedOrder.setPaymentMethodLabel(regex);
		placedOrder.setPaymentStatusLabel(regex);
		placedOrder.setPrintedNote(regex);
		placedOrder.setPurchaseOrderNumber(regex);
		placedOrder.setShippingMethod(regex);
		placedOrder.setShippingOption(regex);
		placedOrder.setStatus(regex);

		String json = PlacedOrderSerDes.toJSON(placedOrder);

		Assert.assertFalse(json.contains(regex));

		placedOrder = PlacedOrderSerDes.toDTO(json);

		Assert.assertEquals(regex, placedOrder.getAccount());
		Assert.assertEquals(regex, placedOrder.getAuthor());
		Assert.assertEquals(regex, placedOrder.getCouponCode());
		Assert.assertEquals(regex, placedOrder.getCurrencyCode());
		Assert.assertEquals(regex, placedOrder.getExternalReferenceCode());
		Assert.assertEquals(regex, placedOrder.getFriendlyURLSeparator());
		Assert.assertEquals(regex, placedOrder.getName());
		Assert.assertEquals(regex, placedOrder.getOrderType());
		Assert.assertEquals(
			regex, placedOrder.getOrderTypeExternalReferenceCode());
		Assert.assertEquals(regex, placedOrder.getOrderUUID());
		Assert.assertEquals(regex, placedOrder.getPaymentMethod());
		Assert.assertEquals(regex, placedOrder.getPaymentMethodLabel());
		Assert.assertEquals(regex, placedOrder.getPaymentStatusLabel());
		Assert.assertEquals(regex, placedOrder.getPrintedNote());
		Assert.assertEquals(regex, placedOrder.getPurchaseOrderNumber());
		Assert.assertEquals(regex, placedOrder.getShippingMethod());
		Assert.assertEquals(regex, placedOrder.getShippingOption());
		Assert.assertEquals(regex, placedOrder.getStatus());
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPage() throws Exception {
		Long accountId = testGetChannelAccountPlacedOrdersPage_getAccountId();
		Long irrelevantAccountId =
			testGetChannelAccountPlacedOrdersPage_getIrrelevantAccountId();
		Long channelId = testGetChannelAccountPlacedOrdersPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelAccountPlacedOrdersPage_getIrrelevantChannelId();

		Page<PlacedOrder> page =
			placedOrderResource.getChannelAccountPlacedOrdersPage(
				accountId, channelId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantAccountId != null) && (irrelevantChannelId != null)) {
			PlacedOrder irrelevantPlacedOrder =
				testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
					irrelevantAccountId, irrelevantChannelId,
					randomIrrelevantPlacedOrder());

			page = placedOrderResource.getChannelAccountPlacedOrdersPage(
				irrelevantAccountId, irrelevantChannelId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrder, (List<PlacedOrder>)page.getItems());
			assertValid(
				page,
				testGetChannelAccountPlacedOrdersPage_getExpectedActions(
					irrelevantAccountId, irrelevantChannelId));
		}

		PlacedOrder placedOrder1 =
			testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
				accountId, channelId, randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
				accountId, channelId, randomPlacedOrder());

		page = placedOrderResource.getChannelAccountPlacedOrdersPage(
			accountId, channelId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(placedOrder1, (List<PlacedOrder>)page.getItems());
		assertContains(placedOrder2, (List<PlacedOrder>)page.getItems());
		assertValid(
			page,
			testGetChannelAccountPlacedOrdersPage_getExpectedActions(
				accountId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelAccountPlacedOrdersPage_getExpectedActions(
				Long accountId, Long channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetChannelAccountPlacedOrdersPage_getAccountId();
		Long channelId = testGetChannelAccountPlacedOrdersPage_getChannelId();

		PlacedOrder placedOrder1 = randomPlacedOrder();

		placedOrder1 = testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
			accountId, channelId, placedOrder1);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null,
					getFilterString(entityField, "between", placedOrder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithFilterStringContains()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithFilterStringEquals()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelAccountPlacedOrdersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetChannelAccountPlacedOrdersPage_getAccountId();
		Long channelId = testGetChannelAccountPlacedOrdersPage_getChannelId();

		PlacedOrder placedOrder1 =
			testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
				accountId, channelId, randomPlacedOrder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PlacedOrder placedOrder2 =
			testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
				accountId, channelId, randomPlacedOrder());

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null,
					getFilterString(entityField, operator, placedOrder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithPagination()
		throws Exception {

		Long accountId = testGetChannelAccountPlacedOrdersPage_getAccountId();
		Long channelId = testGetChannelAccountPlacedOrdersPage_getChannelId();

		Page<PlacedOrder> placedOrdersPage =
			placedOrderResource.getChannelAccountPlacedOrdersPage(
				accountId, channelId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			placedOrdersPage.getTotalCount());

		PlacedOrder placedOrder1 =
			testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
				accountId, channelId, randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
				accountId, channelId, randomPlacedOrder());

		PlacedOrder placedOrder3 =
			testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
				accountId, channelId, randomPlacedOrder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrder> page1 =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(placedOrder1, (List<PlacedOrder>)page1.getItems());

			Page<PlacedOrder> page2 =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(placedOrder2, (List<PlacedOrder>)page2.getItems());

			Page<PlacedOrder> page3 =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
		else {
			Page<PlacedOrder> page1 =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<PlacedOrder> placedOrders1 =
				(List<PlacedOrder>)page1.getItems();

			Assert.assertEquals(
				placedOrders1.toString(), totalCount + 2, placedOrders1.size());

			Page<PlacedOrder> page2 =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrder> placedOrders2 =
				(List<PlacedOrder>)page2.getItems();

			Assert.assertEquals(
				placedOrders2.toString(), 1, placedOrders2.size());

			Page<PlacedOrder> page3 =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(placedOrder1, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithSortDateTime()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithSortDouble()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithSortInteger()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelAccountPlacedOrdersPageWithSortString()
		throws Exception {

		testGetChannelAccountPlacedOrdersPageWithSort(
			EntityField.Type.STRING,
			(entityField, placedOrder1, placedOrder2) -> {
				Class<?> clazz = placedOrder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelAccountPlacedOrdersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, PlacedOrder, PlacedOrder, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetChannelAccountPlacedOrdersPage_getAccountId();
		Long channelId = testGetChannelAccountPlacedOrdersPage_getChannelId();

		PlacedOrder placedOrder1 = randomPlacedOrder();
		PlacedOrder placedOrder2 = randomPlacedOrder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, placedOrder1, placedOrder2);
		}

		placedOrder1 = testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
			accountId, channelId, placedOrder1);

		placedOrder2 = testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
			accountId, channelId, placedOrder2);

		Page<PlacedOrder> page =
			placedOrderResource.getChannelAccountPlacedOrdersPage(
				accountId, channelId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> ascPage =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(placedOrder1, (List<PlacedOrder>)ascPage.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)ascPage.getItems());

			Page<PlacedOrder> descPage =
				placedOrderResource.getChannelAccountPlacedOrdersPage(
					accountId, channelId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				placedOrder2, (List<PlacedOrder>)descPage.getItems());
			assertContains(
				placedOrder1, (List<PlacedOrder>)descPage.getItems());
		}
	}

	protected PlacedOrder testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
			Long accountId, Long channelId, PlacedOrder placedOrder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelAccountPlacedOrdersPage_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelAccountPlacedOrdersPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	protected Long testGetChannelAccountPlacedOrdersPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelAccountPlacedOrdersPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage()
		throws Exception {

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getAccountExternalReferenceCode();
		String irrelevantAccountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getIrrelevantAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getChannelExternalReferenceCode();
		String irrelevantChannelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getIrrelevantChannelExternalReferenceCode();

		Page<PlacedOrder> page =
			placedOrderResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantAccountExternalReferenceCode != null) &&
			(irrelevantChannelExternalReferenceCode != null)) {

			PlacedOrder irrelevantPlacedOrder =
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
					irrelevantAccountExternalReferenceCode,
					irrelevantChannelExternalReferenceCode,
					randomIrrelevantPlacedOrder());

			page =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						irrelevantAccountExternalReferenceCode,
						irrelevantChannelExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrder, (List<PlacedOrder>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getExpectedActions(
					irrelevantAccountExternalReferenceCode,
					irrelevantChannelExternalReferenceCode));
		}

		PlacedOrder placedOrder1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomPlacedOrder());

		page =
			placedOrderResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(placedOrder1, (List<PlacedOrder>)page.getItems());
		assertContains(placedOrder2, (List<PlacedOrder>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getExpectedActions(
				accountExternalReferenceCode, channelExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getExpectedActions(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getChannelExternalReferenceCode();

		PlacedOrder placedOrder1 = randomPlacedOrder();

		placedOrder1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				placedOrder1);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null,
						getFilterString(entityField, "between", placedOrder1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilterStringContains()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilterStringEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getChannelExternalReferenceCode();

		PlacedOrder placedOrder1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomPlacedOrder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PlacedOrder placedOrder2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomPlacedOrder());

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null,
						getFilterString(entityField, operator, placedOrder1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithPagination()
		throws Exception {

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getChannelExternalReferenceCode();

		Page<PlacedOrder> placedOrdersPage =
			placedOrderResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			placedOrdersPage.getTotalCount());

		PlacedOrder placedOrder1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomPlacedOrder());

		PlacedOrder placedOrder3 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomPlacedOrder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrder> page1 =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(placedOrder1, (List<PlacedOrder>)page1.getItems());

			Page<PlacedOrder> page2 =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(placedOrder2, (List<PlacedOrder>)page2.getItems());

			Page<PlacedOrder> page3 =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
		else {
			Page<PlacedOrder> page1 =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<PlacedOrder> placedOrders1 =
				(List<PlacedOrder>)page1.getItems();

			Assert.assertEquals(
				placedOrders1.toString(), totalCount + 2, placedOrders1.size());

			Page<PlacedOrder> page2 =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrder> placedOrders2 =
				(List<PlacedOrder>)page2.getItems();

			Assert.assertEquals(
				placedOrders2.toString(), 1, placedOrders2.size());

			Page<PlacedOrder> page3 =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(placedOrder1, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSortDateTime()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSortDouble()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSortInteger()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSortString()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.STRING,
			(entityField, placedOrder1, placedOrder2) -> {
				Class<?> clazz = placedOrder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, PlacedOrder, PlacedOrder, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getChannelExternalReferenceCode();

		PlacedOrder placedOrder1 = randomPlacedOrder();
		PlacedOrder placedOrder2 = randomPlacedOrder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, placedOrder1, placedOrder2);
		}

		placedOrder1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				placedOrder1);

		placedOrder2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				accountExternalReferenceCode, channelExternalReferenceCode,
				placedOrder2);

		Page<PlacedOrder> page =
			placedOrderResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> ascPage =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(placedOrder1, (List<PlacedOrder>)ascPage.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)ascPage.getItems());

			Page<PlacedOrder> descPage =
				placedOrderResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				placedOrder2, (List<PlacedOrder>)descPage.getItems());
			assertContains(
				placedOrder1, (List<PlacedOrder>)descPage.getItems());
		}
	}

	protected PlacedOrder
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode, PlacedOrder placedOrder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getIrrelevantAccountExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getIrrelevantChannelExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPage()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getIrrelevantExternalReferenceCode();

		Page<PlacedOrder> page =
			placedOrderResource.
				getChannelByExternalReferenceCodePlacedOrdersPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PlacedOrder irrelevantPlacedOrder =
				testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
					irrelevantExternalReferenceCode,
					randomIrrelevantPlacedOrder());

			page =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrder, (List<PlacedOrder>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodePlacedOrdersPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PlacedOrder placedOrder1 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, randomPlacedOrder());

		page =
			placedOrderResource.
				getChannelByExternalReferenceCodePlacedOrdersPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(placedOrder1, (List<PlacedOrder>)page.getItems());
		assertContains(placedOrder2, (List<PlacedOrder>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExternalReferenceCode();

		PlacedOrder placedOrder1 = randomPlacedOrder();

		placedOrder1 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, placedOrder1);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", placedOrder1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilterStringContains()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilterStringEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetChannelByExternalReferenceCodePlacedOrdersPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExternalReferenceCode();

		PlacedOrder placedOrder1 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, randomPlacedOrder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PlacedOrder placedOrder2 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, randomPlacedOrder());

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, placedOrder1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExternalReferenceCode();

		Page<PlacedOrder> placedOrdersPage =
			placedOrderResource.
				getChannelByExternalReferenceCodePlacedOrdersPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			placedOrdersPage.getTotalCount());

		PlacedOrder placedOrder1 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, randomPlacedOrder());

		PlacedOrder placedOrder3 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, randomPlacedOrder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrder> page1 =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(placedOrder1, (List<PlacedOrder>)page1.getItems());

			Page<PlacedOrder> page2 =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(placedOrder2, (List<PlacedOrder>)page2.getItems());

			Page<PlacedOrder> page3 =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
		else {
			Page<PlacedOrder> page1 =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<PlacedOrder> placedOrders1 =
				(List<PlacedOrder>)page1.getItems();

			Assert.assertEquals(
				placedOrders1.toString(), totalCount + 2, placedOrders1.size());

			Page<PlacedOrder> page2 =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrder> placedOrders2 =
				(List<PlacedOrder>)page2.getItems();

			Assert.assertEquals(
				placedOrders2.toString(), 1, placedOrders2.size());

			Page<PlacedOrder> page3 =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(placedOrder1, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithSortDateTime()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithSortDouble()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithSortInteger()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodePlacedOrdersPageWithSortString()
		throws Exception {

		testGetChannelByExternalReferenceCodePlacedOrdersPageWithSort(
			EntityField.Type.STRING,
			(entityField, placedOrder1, placedOrder2) -> {
				Class<?> clazz = placedOrder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetChannelByExternalReferenceCodePlacedOrdersPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, PlacedOrder, PlacedOrder, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExternalReferenceCode();

		PlacedOrder placedOrder1 = randomPlacedOrder();
		PlacedOrder placedOrder2 = randomPlacedOrder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, placedOrder1, placedOrder2);
		}

		placedOrder1 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, placedOrder1);

		placedOrder2 =
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				externalReferenceCode, placedOrder2);

		Page<PlacedOrder> page =
			placedOrderResource.
				getChannelByExternalReferenceCodePlacedOrdersPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> ascPage =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(placedOrder1, (List<PlacedOrder>)ascPage.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)ascPage.getItems());

			Page<PlacedOrder> descPage =
				placedOrderResource.
					getChannelByExternalReferenceCodePlacedOrdersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				placedOrder2, (List<PlacedOrder>)descPage.getItems());
			assertContains(
				placedOrder1, (List<PlacedOrder>)descPage.getItems());
		}
	}

	protected PlacedOrder
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				String externalReferenceCode, PlacedOrder placedOrder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelPlacedOrdersPage() throws Exception {
		Long channelId = testGetChannelPlacedOrdersPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelPlacedOrdersPage_getIrrelevantChannelId();

		Page<PlacedOrder> page = placedOrderResource.getChannelPlacedOrdersPage(
			channelId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantChannelId != null) {
			PlacedOrder irrelevantPlacedOrder =
				testGetChannelPlacedOrdersPage_addPlacedOrder(
					irrelevantChannelId, randomIrrelevantPlacedOrder());

			page = placedOrderResource.getChannelPlacedOrdersPage(
				irrelevantChannelId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrder, (List<PlacedOrder>)page.getItems());
			assertValid(
				page,
				testGetChannelPlacedOrdersPage_getExpectedActions(
					irrelevantChannelId));
		}

		PlacedOrder placedOrder1 =
			testGetChannelPlacedOrdersPage_addPlacedOrder(
				channelId, randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelPlacedOrdersPage_addPlacedOrder(
				channelId, randomPlacedOrder());

		page = placedOrderResource.getChannelPlacedOrdersPage(
			channelId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(placedOrder1, (List<PlacedOrder>)page.getItems());
		assertContains(placedOrder2, (List<PlacedOrder>)page.getItems());
		assertValid(
			page, testGetChannelPlacedOrdersPage_getExpectedActions(channelId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelPlacedOrdersPage_getExpectedActions(Long channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelPlacedOrdersPage_getChannelId();

		PlacedOrder placedOrder1 = randomPlacedOrder();

		placedOrder1 = testGetChannelPlacedOrdersPage_addPlacedOrder(
			channelId, placedOrder1);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null,
					getFilterString(entityField, "between", placedOrder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelPlacedOrdersPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithFilterStringContains()
		throws Exception {

		testGetChannelPlacedOrdersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithFilterStringEquals()
		throws Exception {

		testGetChannelPlacedOrdersPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelPlacedOrdersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelPlacedOrdersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelPlacedOrdersPage_getChannelId();

		PlacedOrder placedOrder1 =
			testGetChannelPlacedOrdersPage_addPlacedOrder(
				channelId, randomPlacedOrder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PlacedOrder placedOrder2 =
			testGetChannelPlacedOrdersPage_addPlacedOrder(
				channelId, randomPlacedOrder());

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> page =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null,
					getFilterString(entityField, operator, placedOrder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(placedOrder1),
				(List<PlacedOrder>)page.getItems());
		}
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithPagination()
		throws Exception {

		Long channelId = testGetChannelPlacedOrdersPage_getChannelId();

		Page<PlacedOrder> placedOrdersPage =
			placedOrderResource.getChannelPlacedOrdersPage(
				channelId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			placedOrdersPage.getTotalCount());

		PlacedOrder placedOrder1 =
			testGetChannelPlacedOrdersPage_addPlacedOrder(
				channelId, randomPlacedOrder());

		PlacedOrder placedOrder2 =
			testGetChannelPlacedOrdersPage_addPlacedOrder(
				channelId, randomPlacedOrder());

		PlacedOrder placedOrder3 =
			testGetChannelPlacedOrdersPage_addPlacedOrder(
				channelId, randomPlacedOrder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrder> page1 =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(placedOrder1, (List<PlacedOrder>)page1.getItems());

			Page<PlacedOrder> page2 =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(placedOrder2, (List<PlacedOrder>)page2.getItems());

			Page<PlacedOrder> page3 =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
		else {
			Page<PlacedOrder> page1 =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<PlacedOrder> placedOrders1 =
				(List<PlacedOrder>)page1.getItems();

			Assert.assertEquals(
				placedOrders1.toString(), totalCount + 2, placedOrders1.size());

			Page<PlacedOrder> page2 =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrder> placedOrders2 =
				(List<PlacedOrder>)page2.getItems();

			Assert.assertEquals(
				placedOrders2.toString(), 1, placedOrders2.size());

			Page<PlacedOrder> page3 =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(placedOrder1, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)page3.getItems());
			assertContains(placedOrder3, (List<PlacedOrder>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithSortDateTime()
		throws Exception {

		testGetChannelPlacedOrdersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithSortDouble()
		throws Exception {

		testGetChannelPlacedOrdersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithSortInteger()
		throws Exception {

		testGetChannelPlacedOrdersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, placedOrder1, placedOrder2) -> {
				BeanTestUtil.setProperty(
					placedOrder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					placedOrder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelPlacedOrdersPageWithSortString()
		throws Exception {

		testGetChannelPlacedOrdersPageWithSort(
			EntityField.Type.STRING,
			(entityField, placedOrder1, placedOrder2) -> {
				Class<?> clazz = placedOrder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						placedOrder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						placedOrder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelPlacedOrdersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, PlacedOrder, PlacedOrder, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelPlacedOrdersPage_getChannelId();

		PlacedOrder placedOrder1 = randomPlacedOrder();
		PlacedOrder placedOrder2 = randomPlacedOrder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, placedOrder1, placedOrder2);
		}

		placedOrder1 = testGetChannelPlacedOrdersPage_addPlacedOrder(
			channelId, placedOrder1);

		placedOrder2 = testGetChannelPlacedOrdersPage_addPlacedOrder(
			channelId, placedOrder2);

		Page<PlacedOrder> page = placedOrderResource.getChannelPlacedOrdersPage(
			channelId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrder> ascPage =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(placedOrder1, (List<PlacedOrder>)ascPage.getItems());
			assertContains(placedOrder2, (List<PlacedOrder>)ascPage.getItems());

			Page<PlacedOrder> descPage =
				placedOrderResource.getChannelPlacedOrdersPage(
					channelId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				placedOrder2, (List<PlacedOrder>)descPage.getItems());
			assertContains(
				placedOrder1, (List<PlacedOrder>)descPage.getItems());
		}
	}

	protected PlacedOrder testGetChannelPlacedOrdersPage_addPlacedOrder(
			Long channelId, PlacedOrder placedOrder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelPlacedOrdersPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelPlacedOrdersPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPlacedOrder() throws Exception {
		PlacedOrder postPlacedOrder = testGetPlacedOrder_addPlacedOrder();

		PlacedOrder getPlacedOrder = placedOrderResource.getPlacedOrder(
			postPlacedOrder.getId());

		assertEquals(postPlacedOrder, getPlacedOrder);
		assertValid(getPlacedOrder);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PlacedOrder postPlacedOrder = testGetPlacedOrder_addPlacedOrder();

		PlacedOrder getPlacedOrder = placedOrderResource.getPlacedOrder(
			postPlacedOrder.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrder"
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

		Object item = vulcanCRUDItemDelegate.getItem(postPlacedOrder.getId());

		assertEquals(getPlacedOrder, PlacedOrderSerDes.toDTO(item.toString()));
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

	protected PlacedOrder testGetPlacedOrder_addPlacedOrder() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrder() throws Exception {
		PlacedOrder placedOrder = testGraphQLGetPlacedOrder_addPlacedOrder();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrder,
				PlacedOrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrder",
								new HashMap<String, Object>() {
									{
										put(
											"placedOrderId",
											placedOrder.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/placedOrder"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrder,
				PlacedOrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrder",
									new HashMap<String, Object>() {
										{
											put(
												"placedOrderId",
												placedOrder.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrder"))));
	}

	@Test
	public void testGraphQLGetPlacedOrderNotFound() throws Exception {
		Long irrelevantPlacedOrderId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrder",
						new HashMap<String, Object>() {
							{
								put("placedOrderId", irrelevantPlacedOrderId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrder",
							new HashMap<String, Object>() {
								{
									put(
										"placedOrderId",
										irrelevantPlacedOrderId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PlacedOrder testGraphQLGetPlacedOrder_addPlacedOrder()
		throws Exception {

		return testGraphQLPlacedOrder_addPlacedOrder();
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCode() throws Exception {
		PlacedOrder postPlacedOrder =
			testGetPlacedOrderByExternalReferenceCode_addPlacedOrder();

		PlacedOrder getPlacedOrder =
			placedOrderResource.getPlacedOrderByExternalReferenceCode(
				postPlacedOrder.getExternalReferenceCode());

		assertEquals(postPlacedOrder, getPlacedOrder);
		assertValid(getPlacedOrder);
	}

	protected PlacedOrder
			testGetPlacedOrderByExternalReferenceCode_addPlacedOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCode()
		throws Exception {

		PlacedOrder placedOrder =
			testGraphQLGetPlacedOrderByExternalReferenceCode_addPlacedOrder();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrder,
				PlacedOrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												placedOrder.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/placedOrderByExternalReferenceCode"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrder,
				PlacedOrderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													placedOrder.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderByExternalReferenceCode",
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

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrderByExternalReferenceCode",
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

	protected PlacedOrder
			testGraphQLGetPlacedOrderByExternalReferenceCode_addPlacedOrder()
		throws Exception {

		return testGraphQLPlacedOrder_addPlacedOrder();
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePaymentURL()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetPlacedOrderPaymentURL() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchPlacedOrder() throws Exception {
		PlacedOrder postPlacedOrder = testPatchPlacedOrder_addPlacedOrder();

		PlacedOrder randomPatchPlacedOrder = randomPatchPlacedOrder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PlacedOrder patchPlacedOrder = placedOrderResource.patchPlacedOrder(
			postPlacedOrder.getId(), randomPatchPlacedOrder);

		PlacedOrder expectedPatchPlacedOrder = postPlacedOrder.clone();

		BeanTestUtil.copyProperties(
			randomPatchPlacedOrder, expectedPatchPlacedOrder);

		PlacedOrder getPlacedOrder = placedOrderResource.getPlacedOrder(
			patchPlacedOrder.getId());

		assertEquals(expectedPatchPlacedOrder, getPlacedOrder);
		assertValid(getPlacedOrder);
	}

	protected PlacedOrder testPatchPlacedOrder_addPlacedOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchPlacedOrderByExternalReferenceCode() throws Exception {
		PlacedOrder postPlacedOrder =
			testPatchPlacedOrderByExternalReferenceCode_addPlacedOrder();

		PlacedOrder randomPatchPlacedOrder = randomPatchPlacedOrder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PlacedOrder patchPlacedOrder =
			placedOrderResource.patchPlacedOrderByExternalReferenceCode(
				postPlacedOrder.getExternalReferenceCode(),
				randomPatchPlacedOrder);

		PlacedOrder expectedPatchPlacedOrder = postPlacedOrder.clone();

		BeanTestUtil.copyProperties(
			randomPatchPlacedOrder, expectedPatchPlacedOrder);

		PlacedOrder getPlacedOrder =
			placedOrderResource.getPlacedOrderByExternalReferenceCode(
				patchPlacedOrder.getExternalReferenceCode());

		assertEquals(expectedPatchPlacedOrder, getPlacedOrder);
		assertValid(getPlacedOrder);
	}

	protected PlacedOrder
			testPatchPlacedOrderByExternalReferenceCode_addPlacedOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected PlacedOrder testGraphQLPlacedOrder_addPlacedOrder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PlacedOrder placedOrder, List<PlacedOrder> placedOrders) {

		boolean contains = false;

		for (PlacedOrder item : placedOrders) {
			if (equals(placedOrder, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			placedOrders + " does not contain " + placedOrder, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PlacedOrder placedOrder1, PlacedOrder placedOrder2) {

		Assert.assertTrue(
			placedOrder1 + " does not equal " + placedOrder2,
			equals(placedOrder1, placedOrder2));
	}

	protected void assertEquals(
		List<PlacedOrder> placedOrders1, List<PlacedOrder> placedOrders2) {

		Assert.assertEquals(placedOrders1.size(), placedOrders2.size());

		for (int i = 0; i < placedOrders1.size(); i++) {
			PlacedOrder placedOrder1 = placedOrders1.get(i);
			PlacedOrder placedOrder2 = placedOrders2.get(i);

			assertEquals(placedOrder1, placedOrder2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PlacedOrder> placedOrders1, List<PlacedOrder> placedOrders2) {

		Assert.assertEquals(placedOrders1.size(), placedOrders2.size());

		for (PlacedOrder placedOrder1 : placedOrders1) {
			boolean contains = false;

			for (PlacedOrder placedOrder2 : placedOrders2) {
				if (equals(placedOrder1, placedOrder2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				placedOrders2 + " does not contain " + placedOrder1, contains);
		}
	}

	protected void assertValid(PlacedOrder placedOrder) throws Exception {
		boolean valid = true;

		if (placedOrder.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (placedOrder.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (placedOrder.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (placedOrder.getAttachments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (placedOrder.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (placedOrder.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (placedOrder.getCouponCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (placedOrder.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (placedOrder.getCurrencyCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (placedOrder.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (placedOrder.getErrorMessages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (placedOrder.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyURLSeparator", additionalAssertFieldName)) {

				if (placedOrder.getFriendlyURLSeparator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"lastPriceUpdateDate", additionalAssertFieldName)) {

				if (placedOrder.getLastPriceUpdateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (placedOrder.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (placedOrder.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderStatusInfo", additionalAssertFieldName)) {
				if (placedOrder.getOrderStatusInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (placedOrder.getOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (placedOrder.getOrderTypeExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (placedOrder.getOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderUUID", additionalAssertFieldName)) {
				if (placedOrder.getOrderUUID() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentMethod", additionalAssertFieldName)) {
				if (placedOrder.getPaymentMethod() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodLabel", additionalAssertFieldName)) {

				if (placedOrder.getPaymentMethodLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (placedOrder.getPaymentStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusInfo", additionalAssertFieldName)) {

				if (placedOrder.getPaymentStatusInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusLabel", additionalAssertFieldName)) {

				if (placedOrder.getPaymentStatusLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderBillingAddress", additionalAssertFieldName)) {

				if (placedOrder.getPlacedOrderBillingAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderBillingAddressId", additionalAssertFieldName)) {

				if (placedOrder.getPlacedOrderBillingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderComments", additionalAssertFieldName)) {

				if (placedOrder.getPlacedOrderComments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("placedOrderItems", additionalAssertFieldName)) {
				if (placedOrder.getPlacedOrderItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderShippingAddress", additionalAssertFieldName)) {

				if (placedOrder.getPlacedOrderShippingAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderShippingAddressId",
					additionalAssertFieldName)) {

				if (placedOrder.getPlacedOrderShippingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (placedOrder.getPrintedNote() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"purchaseOrderNumber", additionalAssertFieldName)) {

				if (placedOrder.getPurchaseOrderNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (placedOrder.getRequestedDeliveryDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shipments", additionalAssertFieldName)) {
				if (placedOrder.getShipments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingMethod", additionalAssertFieldName)) {
				if (placedOrder.getShippingMethod() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingOption", additionalAssertFieldName)) {
				if (placedOrder.getShippingOption() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (placedOrder.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("steps", additionalAssertFieldName)) {
				if (placedOrder.getSteps() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("summary", additionalAssertFieldName)) {
				if (placedOrder.getSummary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("useAsBilling", additionalAssertFieldName)) {
				if (placedOrder.getUseAsBilling() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("valid", additionalAssertFieldName)) {
				if (placedOrder.getValid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (placedOrder.getWorkflowStatusInfo() == null) {
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

	protected void assertValid(Page<PlacedOrder> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PlacedOrder> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PlacedOrder> placedOrders = page.getItems();

		int size = placedOrders.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.delivery.order.dto.v1_0.
						PlacedOrder.class)) {

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
		PlacedOrder placedOrder1, PlacedOrder placedOrder2) {

		if (placedOrder1 == placedOrder2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getAccount(), placedOrder2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getAccountId(),
						placedOrder2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getAttachments(),
						placedOrder2.getAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getAuthor(), placedOrder2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getChannelId(),
						placedOrder2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getCouponCode(),
						placedOrder2.getCouponCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getCreateDate(),
						placedOrder2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getCurrencyCode(),
						placedOrder2.getCurrencyCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)placedOrder1.getCustomFields(),
						(Map)placedOrder2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getErrorMessages(),
						placedOrder2.getErrorMessages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getExternalReferenceCode(),
						placedOrder2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyURLSeparator", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getFriendlyURLSeparator(),
						placedOrder2.getFriendlyURLSeparator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getId(), placedOrder2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"lastPriceUpdateDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getLastPriceUpdateDate(),
						placedOrder2.getLastPriceUpdateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getModifiedDate(),
						placedOrder2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getName(), placedOrder2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderStatusInfo", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getOrderStatusInfo(),
						placedOrder2.getOrderStatusInfo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getOrderType(),
						placedOrder2.getOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getOrderTypeExternalReferenceCode(),
						placedOrder2.getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getOrderTypeId(),
						placedOrder2.getOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderUUID", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getOrderUUID(),
						placedOrder2.getOrderUUID())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentMethod", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getPaymentMethod(),
						placedOrder2.getPaymentMethod())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPaymentMethodLabel(),
						placedOrder2.getPaymentMethodLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getPaymentStatus(),
						placedOrder2.getPaymentStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPaymentStatusInfo(),
						placedOrder2.getPaymentStatusInfo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPaymentStatusLabel(),
						placedOrder2.getPaymentStatusLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderBillingAddress", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPlacedOrderBillingAddress(),
						placedOrder2.getPlacedOrderBillingAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderBillingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPlacedOrderBillingAddressId(),
						placedOrder2.getPlacedOrderBillingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderComments", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPlacedOrderComments(),
						placedOrder2.getPlacedOrderComments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("placedOrderItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getPlacedOrderItems(),
						placedOrder2.getPlacedOrderItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderShippingAddress", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPlacedOrderShippingAddress(),
						placedOrder2.getPlacedOrderShippingAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderShippingAddressId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPlacedOrderShippingAddressId(),
						placedOrder2.getPlacedOrderShippingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getPrintedNote(),
						placedOrder2.getPrintedNote())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"purchaseOrderNumber", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getPurchaseOrderNumber(),
						placedOrder2.getPurchaseOrderNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getRequestedDeliveryDate(),
						placedOrder2.getRequestedDeliveryDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shipments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getShipments(),
						placedOrder2.getShipments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingMethod", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getShippingMethod(),
						placedOrder2.getShippingMethod())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingOption", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getShippingOption(),
						placedOrder2.getShippingOption())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getStatus(), placedOrder2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("steps", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getSteps(), placedOrder2.getSteps())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("summary", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getSummary(), placedOrder2.getSummary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("useAsBilling", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getUseAsBilling(),
						placedOrder2.getUseAsBilling())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("valid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrder1.getValid(), placedOrder2.getValid())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrder1.getWorkflowStatusInfo(),
						placedOrder2.getWorkflowStatusInfo())) {

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

		if (!(_placedOrderResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_placedOrderResource;

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
		EntityField entityField, String operator, PlacedOrder placedOrder) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("account")) {
			Object object = placedOrder.getAccount();

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

		if (entityFieldName.equals("attachments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("author")) {
			Object object = placedOrder.getAuthor();

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
			Object object = placedOrder.getCouponCode();

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
				Date date = placedOrder.getCreateDate();

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

				sb.append(_format.format(placedOrder.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("currencyCode")) {
			Object object = placedOrder.getCurrencyCode();

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("errorMessages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = placedOrder.getExternalReferenceCode();

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

		if (entityFieldName.equals("friendlyURLSeparator")) {
			Object object = placedOrder.getFriendlyURLSeparator();

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
				Date date = placedOrder.getLastPriceUpdateDate();

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

				sb.append(_format.format(placedOrder.getLastPriceUpdateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = placedOrder.getModifiedDate();

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

				sb.append(_format.format(placedOrder.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = placedOrder.getName();

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

		if (entityFieldName.equals("orderStatusInfo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderType")) {
			Object object = placedOrder.getOrderType();

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

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object = placedOrder.getOrderTypeExternalReferenceCode();

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

		if (entityFieldName.equals("orderUUID")) {
			Object object = placedOrder.getOrderUUID();

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

		if (entityFieldName.equals("paymentMethod")) {
			Object object = placedOrder.getPaymentMethod();

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

		if (entityFieldName.equals("paymentMethodLabel")) {
			Object object = placedOrder.getPaymentMethodLabel();

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
			sb.append(String.valueOf(placedOrder.getPaymentStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("paymentStatusInfo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentStatusLabel")) {
			Object object = placedOrder.getPaymentStatusLabel();

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

		if (entityFieldName.equals("placedOrderBillingAddress")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("placedOrderBillingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("placedOrderComments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("placedOrderItems")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("placedOrderShippingAddress")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("placedOrderShippingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("printedNote")) {
			Object object = placedOrder.getPrintedNote();

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
			Object object = placedOrder.getPurchaseOrderNumber();

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
				Date date = placedOrder.getRequestedDeliveryDate();

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

				sb.append(
					_format.format(placedOrder.getRequestedDeliveryDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("shipments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingMethod")) {
			Object object = placedOrder.getShippingMethod();

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
			Object object = placedOrder.getShippingOption();

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

		if (entityFieldName.equals("status")) {
			Object object = placedOrder.getStatus();

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

		if (entityFieldName.equals("steps")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("summary")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("useAsBilling")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("valid")) {
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

	protected PlacedOrder randomPlacedOrder() throws Exception {
		return new PlacedOrder() {
			{
				account = StringUtil.toLowerCase(RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
				couponCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				currencyCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyURLSeparator = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				lastPriceUpdateDate = RandomTestUtil.nextDate();
				modifiedDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				orderType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
				orderUUID = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentMethodLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentStatus = RandomTestUtil.randomInt();
				paymentStatusLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				placedOrderBillingAddressId = RandomTestUtil.randomLong();
				placedOrderShippingAddressId = RandomTestUtil.randomLong();
				printedNote = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				purchaseOrderNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippingMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingOption = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				status = StringUtil.toLowerCase(RandomTestUtil.randomString());
				useAsBilling = RandomTestUtil.randomBoolean();
				valid = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected PlacedOrder randomIrrelevantPlacedOrder() throws Exception {
		PlacedOrder randomIrrelevantPlacedOrder = randomPlacedOrder();

		return randomIrrelevantPlacedOrder;
	}

	protected PlacedOrder randomPatchPlacedOrder() throws Exception {
		return randomPlacedOrder();
	}

	protected PlacedOrderResource placedOrderResource;
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
		LogFactoryUtil.getLog(BasePlacedOrderResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.order.resource.v1_0.
		PlacedOrderResource _placedOrderResource;

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