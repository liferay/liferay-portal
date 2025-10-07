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
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleChannel;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderRuleChannelResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderRuleChannelSerDes;
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
public abstract class BaseOrderRuleChannelResourceTestCase {

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

		_orderRuleChannelResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderRuleChannelResource = OrderRuleChannelResource.builder(
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

		OrderRuleChannel orderRuleChannel1 = randomOrderRuleChannel();

		String json = objectMapper.writeValueAsString(orderRuleChannel1);

		OrderRuleChannel orderRuleChannel2 = OrderRuleChannelSerDes.toDTO(json);

		Assert.assertTrue(equals(orderRuleChannel1, orderRuleChannel2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderRuleChannel orderRuleChannel = randomOrderRuleChannel();

		String json1 = objectMapper.writeValueAsString(orderRuleChannel);
		String json2 = OrderRuleChannelSerDes.toJSON(orderRuleChannel);

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

		OrderRuleChannel orderRuleChannel = randomOrderRuleChannel();

		orderRuleChannel.setChannelExternalReferenceCode(regex);
		orderRuleChannel.setOrderRuleExternalReferenceCode(regex);

		String json = OrderRuleChannelSerDes.toJSON(orderRuleChannel);

		Assert.assertFalse(json.contains(regex));

		orderRuleChannel = OrderRuleChannelSerDes.toDTO(json);

		Assert.assertEquals(
			regex, orderRuleChannel.getChannelExternalReferenceCode());
		Assert.assertEquals(
			regex, orderRuleChannel.getOrderRuleExternalReferenceCode());
	}

	@Test
	public void testDeleteOrderRuleChannel() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRuleChannel orderRuleChannel =
			testDeleteOrderRuleChannel_addOrderRuleChannel();

		assertHttpResponseStatusCode(
			204,
			orderRuleChannelResource.deleteOrderRuleChannelHttpResponse(
				orderRuleChannel.getOrderRuleChannelId()));
	}

	protected OrderRuleChannel testDeleteOrderRuleChannel_addOrderRuleChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderRuleChannel() throws Exception {

		// No namespace

		OrderRuleChannel orderRuleChannel1 =
			testGraphQLDeleteOrderRuleChannel_addOrderRuleChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderRuleChannel",
						new HashMap<String, Object>() {
							{
								put(
									"orderRuleChannelId",
									orderRuleChannel1.getOrderRuleChannelId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderRuleChannel"));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderRuleChannel orderRuleChannel2 =
			testGraphQLDeleteOrderRuleChannel_addOrderRuleChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderRuleChannel",
							new HashMap<String, Object>() {
								{
									put(
										"orderRuleChannelId",
										orderRuleChannel2.
											getOrderRuleChannelId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderRuleChannel"));
	}

	protected OrderRuleChannel
			testGraphQLDeleteOrderRuleChannel_addOrderRuleChannel()
		throws Exception {

		return testGraphQLOrderRuleChannel_addOrderRuleChannel();
	}

	@Test
	public void testDeleteOrderRuleChannelBatch() throws Exception {
		OrderRuleChannel orderRuleChannel1 =
			testDeleteOrderRuleChannelBatch_addOrderRuleChannel();

		testDeleteOrderRuleChannelBatch_deleteOrderRuleChannel(
			202, null, orderRuleChannel1.getOrderRuleChannelId());
	}

	protected OrderRuleChannel
			testDeleteOrderRuleChannelBatch_addOrderRuleChannel()
		throws Exception {

		return testDeleteOrderRuleChannel_addOrderRuleChannel();
	}

	protected void testDeleteOrderRuleChannelBatch_deleteOrderRuleChannel(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderRuleChannelResource.deleteOrderRuleChannelBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"orderRuleChannelId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getIrrelevantExternalReferenceCode();

		Page<OrderRuleChannel> page =
			orderRuleChannelResource.
				getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			OrderRuleChannel irrelevantOrderRuleChannel =
				testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
					irrelevantExternalReferenceCode,
					randomIrrelevantOrderRuleChannel());

			page =
				orderRuleChannelResource.
					getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleChannel,
				(List<OrderRuleChannel>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		OrderRuleChannel orderRuleChannel1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
				externalReferenceCode, randomOrderRuleChannel());

		OrderRuleChannel orderRuleChannel2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
				externalReferenceCode, randomOrderRuleChannel());

		page =
			orderRuleChannelResource.
				getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleChannel1, (List<OrderRuleChannel>)page.getItems());
		assertContains(
			orderRuleChannel2, (List<OrderRuleChannel>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getExpectedActions(
				externalReferenceCode));

		orderRuleChannelResource.deleteOrderRuleChannel(
			orderRuleChannel1.getOrderRuleChannelId());

		orderRuleChannelResource.deleteOrderRuleChannel(
			orderRuleChannel2.getOrderRuleChannelId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getExternalReferenceCode();

		Page<OrderRuleChannel> orderRuleChannelsPage =
			orderRuleChannelResource.
				getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleChannelsPage.getTotalCount());

		OrderRuleChannel orderRuleChannel1 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
				externalReferenceCode, randomOrderRuleChannel());

		OrderRuleChannel orderRuleChannel2 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
				externalReferenceCode, randomOrderRuleChannel());

		OrderRuleChannel orderRuleChannel3 =
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
				externalReferenceCode, randomOrderRuleChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleChannel> page1 =
				orderRuleChannelResource.
					getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleChannel1, (List<OrderRuleChannel>)page1.getItems());

			Page<OrderRuleChannel> page2 =
				orderRuleChannelResource.
					getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleChannel2, (List<OrderRuleChannel>)page2.getItems());

			Page<OrderRuleChannel> page3 =
				orderRuleChannelResource.
					getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				orderRuleChannel3, (List<OrderRuleChannel>)page3.getItems());
		}
		else {
			Page<OrderRuleChannel> page1 =
				orderRuleChannelResource.
					getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<OrderRuleChannel> orderRuleChannels1 =
				(List<OrderRuleChannel>)page1.getItems();

			Assert.assertEquals(
				orderRuleChannels1.toString(), totalCount + 2,
				orderRuleChannels1.size());

			Page<OrderRuleChannel> page2 =
				orderRuleChannelResource.
					getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleChannel> orderRuleChannels2 =
				(List<OrderRuleChannel>)page2.getItems();

			Assert.assertEquals(
				orderRuleChannels2.toString(), 1, orderRuleChannels2.size());

			Page<OrderRuleChannel> page3 =
				orderRuleChannelResource.
					getOrderRuleByExternalReferenceCodeOrderRuleChannelsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				orderRuleChannel1, (List<OrderRuleChannel>)page3.getItems());
			assertContains(
				orderRuleChannel2, (List<OrderRuleChannel>)page3.getItems());
			assertContains(
				orderRuleChannel3, (List<OrderRuleChannel>)page3.getItems());
		}
	}

	protected OrderRuleChannel
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_addOrderRuleChannel(
				String externalReferenceCode, OrderRuleChannel orderRuleChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderRuleByExternalReferenceCodeOrderRuleChannelsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPage() throws Exception {
		Long id = testGetOrderRuleIdOrderRuleChannelsPage_getId();
		Long irrelevantId =
			testGetOrderRuleIdOrderRuleChannelsPage_getIrrelevantId();

		Page<OrderRuleChannel> page =
			orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			OrderRuleChannel irrelevantOrderRuleChannel =
				testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
					irrelevantId, randomIrrelevantOrderRuleChannel());

			page = orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderRuleChannel,
				(List<OrderRuleChannel>)page.getItems());
			assertValid(
				page,
				testGetOrderRuleIdOrderRuleChannelsPage_getExpectedActions(
					irrelevantId));
		}

		OrderRuleChannel orderRuleChannel1 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, randomOrderRuleChannel());

		OrderRuleChannel orderRuleChannel2 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, randomOrderRuleChannel());

		page = orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			orderRuleChannel1, (List<OrderRuleChannel>)page.getItems());
		assertContains(
			orderRuleChannel2, (List<OrderRuleChannel>)page.getItems());
		assertValid(
			page,
			testGetOrderRuleIdOrderRuleChannelsPage_getExpectedActions(id));

		orderRuleChannelResource.deleteOrderRuleChannel(
			orderRuleChannel1.getOrderRuleChannelId());

		orderRuleChannelResource.deleteOrderRuleChannel(
			orderRuleChannel2.getOrderRuleChannelId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderRuleIdOrderRuleChannelsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleChannelsPage_getId();

		OrderRuleChannel orderRuleChannel1 = randomOrderRuleChannel();

		orderRuleChannel1 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, orderRuleChannel1);

		for (EntityField entityField : entityFields) {
			Page<OrderRuleChannel> page =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null,
					getFilterString(entityField, "between", orderRuleChannel1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRuleChannel1),
				(List<OrderRuleChannel>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithFilterStringContains()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithFilterStringEquals()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderRuleIdOrderRuleChannelsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleChannelsPage_getId();

		OrderRuleChannel orderRuleChannel1 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, randomOrderRuleChannel());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderRuleChannel orderRuleChannel2 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, randomOrderRuleChannel());

		for (EntityField entityField : entityFields) {
			Page<OrderRuleChannel> page =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null,
					getFilterString(entityField, operator, orderRuleChannel1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(orderRuleChannel1),
				(List<OrderRuleChannel>)page.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithPagination()
		throws Exception {

		Long id = testGetOrderRuleIdOrderRuleChannelsPage_getId();

		Page<OrderRuleChannel> orderRuleChannelsPage =
			orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			orderRuleChannelsPage.getTotalCount());

		OrderRuleChannel orderRuleChannel1 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, randomOrderRuleChannel());

		OrderRuleChannel orderRuleChannel2 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, randomOrderRuleChannel());

		OrderRuleChannel orderRuleChannel3 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, randomOrderRuleChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderRuleChannel> page1 =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				orderRuleChannel1, (List<OrderRuleChannel>)page1.getItems());

			Page<OrderRuleChannel> page2 =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				orderRuleChannel2, (List<OrderRuleChannel>)page2.getItems());

			Page<OrderRuleChannel> page3 =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				orderRuleChannel3, (List<OrderRuleChannel>)page3.getItems());
		}
		else {
			Page<OrderRuleChannel> page1 =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<OrderRuleChannel> orderRuleChannels1 =
				(List<OrderRuleChannel>)page1.getItems();

			Assert.assertEquals(
				orderRuleChannels1.toString(), totalCount + 2,
				orderRuleChannels1.size());

			Page<OrderRuleChannel> page2 =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderRuleChannel> orderRuleChannels2 =
				(List<OrderRuleChannel>)page2.getItems();

			Assert.assertEquals(
				orderRuleChannels2.toString(), 1, orderRuleChannels2.size());

			Page<OrderRuleChannel> page3 =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				orderRuleChannel1, (List<OrderRuleChannel>)page3.getItems());
			assertContains(
				orderRuleChannel2, (List<OrderRuleChannel>)page3.getItems());
			assertContains(
				orderRuleChannel3, (List<OrderRuleChannel>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithSortDateTime()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, orderRuleChannel1, orderRuleChannel2) -> {
				BeanTestUtil.setProperty(
					orderRuleChannel1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithSortDouble()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, orderRuleChannel1, orderRuleChannel2) -> {
				BeanTestUtil.setProperty(
					orderRuleChannel1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					orderRuleChannel2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithSortInteger()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, orderRuleChannel1, orderRuleChannel2) -> {
				BeanTestUtil.setProperty(
					orderRuleChannel1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					orderRuleChannel2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderRuleIdOrderRuleChannelsPageWithSortString()
		throws Exception {

		testGetOrderRuleIdOrderRuleChannelsPageWithSort(
			EntityField.Type.STRING,
			(entityField, orderRuleChannel1, orderRuleChannel2) -> {
				Class<?> clazz = orderRuleChannel1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						orderRuleChannel1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						orderRuleChannel2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						orderRuleChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						orderRuleChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						orderRuleChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						orderRuleChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderRuleIdOrderRuleChannelsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, OrderRuleChannel, OrderRuleChannel, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOrderRuleIdOrderRuleChannelsPage_getId();

		OrderRuleChannel orderRuleChannel1 = randomOrderRuleChannel();
		OrderRuleChannel orderRuleChannel2 = randomOrderRuleChannel();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, orderRuleChannel1, orderRuleChannel2);
		}

		orderRuleChannel1 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, orderRuleChannel1);

		orderRuleChannel2 =
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				id, orderRuleChannel2);

		Page<OrderRuleChannel> page =
			orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OrderRuleChannel> ascPage =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				orderRuleChannel1, (List<OrderRuleChannel>)ascPage.getItems());
			assertContains(
				orderRuleChannel2, (List<OrderRuleChannel>)ascPage.getItems());

			Page<OrderRuleChannel> descPage =
				orderRuleChannelResource.getOrderRuleIdOrderRuleChannelsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				orderRuleChannel2, (List<OrderRuleChannel>)descPage.getItems());
			assertContains(
				orderRuleChannel1, (List<OrderRuleChannel>)descPage.getItems());
		}
	}

	protected OrderRuleChannel
			testGetOrderRuleIdOrderRuleChannelsPage_addOrderRuleChannel(
				Long id, OrderRuleChannel orderRuleChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleIdOrderRuleChannelsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderRuleIdOrderRuleChannelsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostOrderRuleByExternalReferenceCodeOrderRuleChannel()
		throws Exception {

		OrderRuleChannel randomOrderRuleChannel = randomOrderRuleChannel();

		OrderRuleChannel postOrderRuleChannel =
			testPostOrderRuleByExternalReferenceCodeOrderRuleChannel_addOrderRuleChannel(
				randomOrderRuleChannel);

		assertEquals(randomOrderRuleChannel, postOrderRuleChannel);
		assertValid(postOrderRuleChannel);
	}

	protected OrderRuleChannel
			testPostOrderRuleByExternalReferenceCodeOrderRuleChannel_addOrderRuleChannel(
				OrderRuleChannel orderRuleChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderRuleIdOrderRuleChannel() throws Exception {
		OrderRuleChannel randomOrderRuleChannel = randomOrderRuleChannel();

		OrderRuleChannel postOrderRuleChannel =
			testPostOrderRuleIdOrderRuleChannel_addOrderRuleChannel(
				randomOrderRuleChannel);

		assertEquals(randomOrderRuleChannel, postOrderRuleChannel);
		assertValid(postOrderRuleChannel);
	}

	protected OrderRuleChannel
			testPostOrderRuleIdOrderRuleChannel_addOrderRuleChannel(
				OrderRuleChannel orderRuleChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderRuleChannel orderRuleChannel1 =
			testBatchEngineDeleteImportTask_addOrderRuleChannel();

		testBatchEngineDeleteImportTask_deleteOrderRuleChannel(
			200, null, orderRuleChannel1.getOrderRuleChannelId());
	}

	protected OrderRuleChannel
			testBatchEngineDeleteImportTask_addOrderRuleChannel()
		throws Exception {

		return testDeleteOrderRuleChannel_addOrderRuleChannel();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderRuleChannel(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleChannel",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"orderRuleChannelId", () -> id
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

	protected OrderRuleChannel testGraphQLOrderRuleChannel_addOrderRuleChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		OrderRuleChannel orderRuleChannel,
		List<OrderRuleChannel> orderRuleChannels) {

		boolean contains = false;

		for (OrderRuleChannel item : orderRuleChannels) {
			if (equals(orderRuleChannel, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderRuleChannels + " does not contain " + orderRuleChannel,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OrderRuleChannel orderRuleChannel1,
		OrderRuleChannel orderRuleChannel2) {

		Assert.assertTrue(
			orderRuleChannel1 + " does not equal " + orderRuleChannel2,
			equals(orderRuleChannel1, orderRuleChannel2));
	}

	protected void assertEquals(
		List<OrderRuleChannel> orderRuleChannels1,
		List<OrderRuleChannel> orderRuleChannels2) {

		Assert.assertEquals(
			orderRuleChannels1.size(), orderRuleChannels2.size());

		for (int i = 0; i < orderRuleChannels1.size(); i++) {
			OrderRuleChannel orderRuleChannel1 = orderRuleChannels1.get(i);
			OrderRuleChannel orderRuleChannel2 = orderRuleChannels2.get(i);

			assertEquals(orderRuleChannel1, orderRuleChannel2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderRuleChannel> orderRuleChannels1,
		List<OrderRuleChannel> orderRuleChannels2) {

		Assert.assertEquals(
			orderRuleChannels1.size(), orderRuleChannels2.size());

		for (OrderRuleChannel orderRuleChannel1 : orderRuleChannels1) {
			boolean contains = false;

			for (OrderRuleChannel orderRuleChannel2 : orderRuleChannels2) {
				if (equals(orderRuleChannel1, orderRuleChannel2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderRuleChannels2 + " does not contain " + orderRuleChannel1,
				contains);
		}
	}

	protected void assertValid(OrderRuleChannel orderRuleChannel)
		throws Exception {

		boolean valid = true;

		if (orderRuleChannel.getOrderRuleChannelId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (orderRuleChannel.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (orderRuleChannel.getChannel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleChannel.getChannelExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (orderRuleChannel.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleChannelId", additionalAssertFieldName)) {

				if (orderRuleChannel.getOrderRuleChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (orderRuleChannel.getOrderRuleExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (orderRuleChannel.getOrderRuleId() == null) {
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

	protected void assertValid(Page<OrderRuleChannel> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderRuleChannel> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderRuleChannel> orderRuleChannels =
			page.getItems();

		int size = orderRuleChannels.size();

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

		graphQLFields.add(new GraphQLField("orderRuleChannelId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.order.dto.v1_0.
						OrderRuleChannel.class)) {

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
		OrderRuleChannel orderRuleChannel1,
		OrderRuleChannel orderRuleChannel2) {

		if (orderRuleChannel1 == orderRuleChannel2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)orderRuleChannel1.getActions(),
						(Map)orderRuleChannel2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleChannel1.getChannel(),
						orderRuleChannel2.getChannel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleChannel1.getChannelExternalReferenceCode(),
						orderRuleChannel2.getChannelExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleChannel1.getChannelId(),
						orderRuleChannel2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleChannelId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleChannel1.getOrderRuleChannelId(),
						orderRuleChannel2.getOrderRuleChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderRuleExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderRuleChannel1.getOrderRuleExternalReferenceCode(),
						orderRuleChannel2.
							getOrderRuleExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderRuleId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderRuleChannel1.getOrderRuleId(),
						orderRuleChannel2.getOrderRuleId())) {

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

		if (!(_orderRuleChannelResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderRuleChannelResource;

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
		OrderRuleChannel orderRuleChannel) {

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

		if (entityFieldName.equals("channel")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelExternalReferenceCode")) {
			Object object = orderRuleChannel.getChannelExternalReferenceCode();

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

		if (entityFieldName.equals("orderRuleChannelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderRuleExternalReferenceCode")) {
			Object object =
				orderRuleChannel.getOrderRuleExternalReferenceCode();

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

	protected OrderRuleChannel randomOrderRuleChannel() throws Exception {
		return new OrderRuleChannel() {
			{
				channelExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
				orderRuleChannelId = RandomTestUtil.randomLong();
				orderRuleExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderRuleId = RandomTestUtil.randomLong();
			}
		};
	}

	protected OrderRuleChannel randomIrrelevantOrderRuleChannel()
		throws Exception {

		OrderRuleChannel randomIrrelevantOrderRuleChannel =
			randomOrderRuleChannel();

		return randomIrrelevantOrderRuleChannel;
	}

	protected OrderRuleChannel randomPatchOrderRuleChannel() throws Exception {
		return randomOrderRuleChannel();
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

	protected OrderRuleChannelResource orderRuleChannelResource;
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
		LogFactoryUtil.getLog(BaseOrderRuleChannelResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.order.resource.v1_0.
		OrderRuleChannelResource _orderRuleChannelResource;

}