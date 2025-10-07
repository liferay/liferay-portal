/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.shipment.client.dto.v1_0.ShipmentItem;
import com.liferay.headless.commerce.admin.shipment.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.shipment.client.pagination.Page;
import com.liferay.headless.commerce.admin.shipment.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.shipment.client.resource.v1_0.ShipmentItemResource;
import com.liferay.headless.commerce.admin.shipment.client.serdes.v1_0.ShipmentItemSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseShipmentItemResourceTestCase {

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

		_shipmentItemResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		shipmentItemResource = ShipmentItemResource.builder(
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

		ShipmentItem shipmentItem1 = randomShipmentItem();

		String json = objectMapper.writeValueAsString(shipmentItem1);

		ShipmentItem shipmentItem2 = ShipmentItemSerDes.toDTO(json);

		Assert.assertTrue(equals(shipmentItem1, shipmentItem2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ShipmentItem shipmentItem = randomShipmentItem();

		String json1 = objectMapper.writeValueAsString(shipmentItem);
		String json2 = ShipmentItemSerDes.toJSON(shipmentItem);

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

		ShipmentItem shipmentItem = randomShipmentItem();

		shipmentItem.setExternalReferenceCode(regex);
		shipmentItem.setOrderItemExternalReferenceCode(regex);
		shipmentItem.setShipmentExternalReferenceCode(regex);
		shipmentItem.setUnitOfMeasureKey(regex);
		shipmentItem.setUserName(regex);
		shipmentItem.setWarehouseExternalReferenceCode(regex);

		String json = ShipmentItemSerDes.toJSON(shipmentItem);

		Assert.assertFalse(json.contains(regex));

		shipmentItem = ShipmentItemSerDes.toDTO(json);

		Assert.assertEquals(regex, shipmentItem.getExternalReferenceCode());
		Assert.assertEquals(
			regex, shipmentItem.getOrderItemExternalReferenceCode());
		Assert.assertEquals(
			regex, shipmentItem.getShipmentExternalReferenceCode());
		Assert.assertEquals(regex, shipmentItem.getUnitOfMeasureKey());
		Assert.assertEquals(regex, shipmentItem.getUserName());
		Assert.assertEquals(
			regex, shipmentItem.getWarehouseExternalReferenceCode());
	}

	@Test
	public void testDeleteShipmentItem() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ShipmentItem shipmentItem = testDeleteShipmentItem_addShipmentItem();

		assertHttpResponseStatusCode(
			204,
			shipmentItemResource.deleteShipmentItemHttpResponse(
				shipmentItem.getId()));

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem.getId()));
		assertHttpResponseStatusCode(
			404, shipmentItemResource.getShipmentItemHttpResponse(0L));
	}

	protected ShipmentItem testDeleteShipmentItem_addShipmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteShipmentItem() throws Exception {

		// No namespace

		ShipmentItem shipmentItem1 =
			testGraphQLDeleteShipmentItem_addShipmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteShipmentItem",
						new HashMap<String, Object>() {
							{
								put("shipmentItemId", shipmentItem1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteShipmentItem"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"shipmentItem",
					new HashMap<String, Object>() {
						{
							put("shipmentItemId", shipmentItem1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminShipment_v1_0

		ShipmentItem shipmentItem2 =
			testGraphQLDeleteShipmentItem_addShipmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminShipment_v1_0",
						new GraphQLField(
							"deleteShipmentItem",
							new HashMap<String, Object>() {
								{
									put(
										"shipmentItemId",
										shipmentItem2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminShipment_v1_0",
				"Object/deleteShipmentItem"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminShipment_v1_0",
					new GraphQLField(
						"shipmentItem",
						new HashMap<String, Object>() {
							{
								put("shipmentItemId", shipmentItem2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ShipmentItem testGraphQLDeleteShipmentItem_addShipmentItem()
		throws Exception {

		return testGraphQLShipmentItem_addShipmentItem();
	}

	@Test
	public void testDeleteShipmentItemBatch() throws Exception {
		ShipmentItem shipmentItem1 =
			testDeleteShipmentItemBatch_addShipmentItem();

		testDeleteShipmentItemBatch_deleteShipmentItem(
			202, shipmentItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem1.getId()));

		shipmentItem1 = testDeleteShipmentItemBatch_addShipmentItem();

		testDeleteShipmentItemBatch_deleteShipmentItem(
			202, null, shipmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem1.getId()));

		shipmentItem1 = testDeleteShipmentItemBatch_addShipmentItem();
		ShipmentItem shipmentItem2 =
			testDeleteShipmentItemBatch_addShipmentItem();

		testDeleteShipmentItemBatch_deleteShipmentItem(
			202, shipmentItem2.getExternalReferenceCode(),
			shipmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem2.getId()));

		testDeleteShipmentItemBatch_deleteShipmentItem(
			202, shipmentItem2.getExternalReferenceCode(),
			shipmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem2.getId()));
	}

	protected ShipmentItem testDeleteShipmentItemBatch_addShipmentItem()
		throws Exception {

		return testDeleteShipmentItem_addShipmentItem();
	}

	protected void testDeleteShipmentItemBatch_deleteShipmentItem(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			shipmentItemResource.deleteShipmentItemBatchHttpResponse(
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
	public void testDeleteShipmentItemByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ShipmentItem shipmentItem =
			testDeleteShipmentItemByExternalReferenceCode_addShipmentItem();

		assertHttpResponseStatusCode(
			204,
			shipmentItemResource.
				deleteShipmentItemByExternalReferenceCodeHttpResponse(
					shipmentItem.getExternalReferenceCode()));
	}

	protected ShipmentItem
			testDeleteShipmentItemByExternalReferenceCode_addShipmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteShipmentItemByExternalReferenceCode()
		throws Exception {

		// No namespace

		ShipmentItem shipmentItem1 =
			testGraphQLDeleteShipmentItemByExternalReferenceCode_addShipmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteShipmentItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										shipmentItem1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteShipmentItemByExternalReferenceCode"));

		// Using the namespace headlessCommerceAdminShipment_v1_0

		ShipmentItem shipmentItem2 =
			testGraphQLDeleteShipmentItemByExternalReferenceCode_addShipmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminShipment_v1_0",
						new GraphQLField(
							"deleteShipmentItemByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											shipmentItem2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminShipment_v1_0",
				"Object/deleteShipmentItemByExternalReferenceCode"));
	}

	protected ShipmentItem
			testGraphQLDeleteShipmentItemByExternalReferenceCode_addShipmentItem()
		throws Exception {

		return testGraphQLShipmentItem_addShipmentItem();
	}

	@Test
	public void testGetShipmentByExternalReferenceCodeItem() throws Exception {
		ShipmentItem postShipmentItem =
			testGetShipmentByExternalReferenceCodeItem_addShipmentItem();

		ShipmentItem getShipmentItem =
			shipmentItemResource.getShipmentByExternalReferenceCodeItem(
				postShipmentItem.getExternalReferenceCode());

		assertEquals(postShipmentItem, getShipmentItem);
		assertValid(getShipmentItem);
	}

	protected ShipmentItem
			testGetShipmentByExternalReferenceCodeItem_addShipmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetShipmentByExternalReferenceCodeItem()
		throws Exception {

		ShipmentItem shipmentItem =
			testGraphQLGetShipmentByExternalReferenceCodeItem_addShipmentItem();

		// No namespace

		Assert.assertTrue(
			equals(
				shipmentItem,
				ShipmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"shipmentByExternalReferenceCodeItem",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												shipmentItem.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/shipmentByExternalReferenceCodeItem"))));

		// Using the namespace headlessCommerceAdminShipment_v1_0

		Assert.assertTrue(
			equals(
				shipmentItem,
				ShipmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminShipment_v1_0",
								new GraphQLField(
									"shipmentByExternalReferenceCodeItem",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													shipmentItem.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminShipment_v1_0",
						"Object/shipmentByExternalReferenceCodeItem"))));
	}

	@Test
	public void testGraphQLGetShipmentByExternalReferenceCodeItemNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"shipmentByExternalReferenceCodeItem",
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

		// Using the namespace headlessCommerceAdminShipment_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminShipment_v1_0",
						new GraphQLField(
							"shipmentByExternalReferenceCodeItem",
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

	protected ShipmentItem
			testGraphQLGetShipmentByExternalReferenceCodeItem_addShipmentItem()
		throws Exception {

		return testGraphQLShipmentItem_addShipmentItem();
	}

	@Test
	public void testGetShipmentByExternalReferenceCodeItemsPage()
		throws Exception {

		String externalReferenceCode =
			testGetShipmentByExternalReferenceCodeItemsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetShipmentByExternalReferenceCodeItemsPage_getIrrelevantExternalReferenceCode();

		Page<ShipmentItem> page =
			shipmentItemResource.getShipmentByExternalReferenceCodeItemsPage(
				externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ShipmentItem irrelevantShipmentItem =
				testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
					irrelevantExternalReferenceCode,
					randomIrrelevantShipmentItem());

			page =
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantShipmentItem, (List<ShipmentItem>)page.getItems());
			assertValid(
				page,
				testGetShipmentByExternalReferenceCodeItemsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ShipmentItem shipmentItem1 =
			testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
				externalReferenceCode, randomShipmentItem());

		ShipmentItem shipmentItem2 =
			testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
				externalReferenceCode, randomShipmentItem());

		page = shipmentItemResource.getShipmentByExternalReferenceCodeItemsPage(
			externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(shipmentItem1, (List<ShipmentItem>)page.getItems());
		assertContains(shipmentItem2, (List<ShipmentItem>)page.getItems());
		assertValid(
			page,
			testGetShipmentByExternalReferenceCodeItemsPage_getExpectedActions(
				externalReferenceCode));

		shipmentItemResource.deleteShipmentItem(shipmentItem1.getId());

		shipmentItemResource.deleteShipmentItem(shipmentItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetShipmentByExternalReferenceCodeItemsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetShipmentByExternalReferenceCodeItemsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetShipmentByExternalReferenceCodeItemsPage_getExternalReferenceCode();

		Page<ShipmentItem> shipmentItemsPage =
			shipmentItemResource.getShipmentByExternalReferenceCodeItemsPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			shipmentItemsPage.getTotalCount());

		ShipmentItem shipmentItem1 =
			testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
				externalReferenceCode, randomShipmentItem());

		ShipmentItem shipmentItem2 =
			testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
				externalReferenceCode, randomShipmentItem());

		ShipmentItem shipmentItem3 =
			testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
				externalReferenceCode, randomShipmentItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ShipmentItem> page1 =
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(shipmentItem1, (List<ShipmentItem>)page1.getItems());

			Page<ShipmentItem> page2 =
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(shipmentItem2, (List<ShipmentItem>)page2.getItems());

			Page<ShipmentItem> page3 =
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(shipmentItem3, (List<ShipmentItem>)page3.getItems());
		}
		else {
			Page<ShipmentItem> page1 =
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ShipmentItem> shipmentItems1 =
				(List<ShipmentItem>)page1.getItems();

			Assert.assertEquals(
				shipmentItems1.toString(), totalCount + 2,
				shipmentItems1.size());

			Page<ShipmentItem> page2 =
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ShipmentItem> shipmentItems2 =
				(List<ShipmentItem>)page2.getItems();

			Assert.assertEquals(
				shipmentItems2.toString(), 1, shipmentItems2.size());

			Page<ShipmentItem> page3 =
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(shipmentItem1, (List<ShipmentItem>)page3.getItems());
			assertContains(shipmentItem2, (List<ShipmentItem>)page3.getItems());
			assertContains(shipmentItem3, (List<ShipmentItem>)page3.getItems());
		}
	}

	protected ShipmentItem
			testGetShipmentByExternalReferenceCodeItemsPage_addShipmentItem(
				String externalReferenceCode, ShipmentItem shipmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetShipmentByExternalReferenceCodeItemsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetShipmentByExternalReferenceCodeItemsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetShipmentItem() throws Exception {
		ShipmentItem postShipmentItem = testGetShipmentItem_addShipmentItem();

		ShipmentItem getShipmentItem = shipmentItemResource.getShipmentItem(
			postShipmentItem.getId());

		assertEquals(postShipmentItem, getShipmentItem);
		assertValid(getShipmentItem);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ShipmentItem postShipmentItem = testGetShipmentItem_addShipmentItem();

		ShipmentItem getShipmentItem = shipmentItemResource.getShipmentItem(
			postShipmentItem.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.shipment.dto.v1_0.ShipmentItem"
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

		Object item = vulcanCRUDItemDelegate.getItem(postShipmentItem.getId());

		assertEquals(
			getShipmentItem, ShipmentItemSerDes.toDTO(item.toString()));
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

	protected ShipmentItem testGetShipmentItem_addShipmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetShipmentItem() throws Exception {
		ShipmentItem shipmentItem =
			testGraphQLGetShipmentItem_addShipmentItem();

		// No namespace

		Assert.assertTrue(
			equals(
				shipmentItem,
				ShipmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"shipmentItem",
								new HashMap<String, Object>() {
									{
										put(
											"shipmentItemId",
											shipmentItem.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/shipmentItem"))));

		// Using the namespace headlessCommerceAdminShipment_v1_0

		Assert.assertTrue(
			equals(
				shipmentItem,
				ShipmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminShipment_v1_0",
								new GraphQLField(
									"shipmentItem",
									new HashMap<String, Object>() {
										{
											put(
												"shipmentItemId",
												shipmentItem.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminShipment_v1_0",
						"Object/shipmentItem"))));
	}

	@Test
	public void testGraphQLGetShipmentItemNotFound() throws Exception {
		Long irrelevantShipmentItemId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"shipmentItem",
						new HashMap<String, Object>() {
							{
								put("shipmentItemId", irrelevantShipmentItemId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminShipment_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminShipment_v1_0",
						new GraphQLField(
							"shipmentItem",
							new HashMap<String, Object>() {
								{
									put(
										"shipmentItemId",
										irrelevantShipmentItemId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ShipmentItem testGraphQLGetShipmentItem_addShipmentItem()
		throws Exception {

		return testGraphQLShipmentItem_addShipmentItem();
	}

	@Test
	public void testGetShipmentItemsPage() throws Exception {
		Long shipmentId = testGetShipmentItemsPage_getShipmentId();
		Long irrelevantShipmentId =
			testGetShipmentItemsPage_getIrrelevantShipmentId();

		Page<ShipmentItem> page = shipmentItemResource.getShipmentItemsPage(
			shipmentId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantShipmentId != null) {
			ShipmentItem irrelevantShipmentItem =
				testGetShipmentItemsPage_addShipmentItem(
					irrelevantShipmentId, randomIrrelevantShipmentItem());

			page = shipmentItemResource.getShipmentItemsPage(
				irrelevantShipmentId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantShipmentItem, (List<ShipmentItem>)page.getItems());
			assertValid(
				page,
				testGetShipmentItemsPage_getExpectedActions(
					irrelevantShipmentId));
		}

		ShipmentItem shipmentItem1 = testGetShipmentItemsPage_addShipmentItem(
			shipmentId, randomShipmentItem());

		ShipmentItem shipmentItem2 = testGetShipmentItemsPage_addShipmentItem(
			shipmentId, randomShipmentItem());

		page = shipmentItemResource.getShipmentItemsPage(
			shipmentId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(shipmentItem1, (List<ShipmentItem>)page.getItems());
		assertContains(shipmentItem2, (List<ShipmentItem>)page.getItems());
		assertValid(
			page, testGetShipmentItemsPage_getExpectedActions(shipmentId));

		shipmentItemResource.deleteShipmentItem(shipmentItem1.getId());

		shipmentItemResource.deleteShipmentItem(shipmentItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetShipmentItemsPage_getExpectedActions(Long shipmentId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetShipmentItemsPageWithPagination() throws Exception {
		Long shipmentId = testGetShipmentItemsPage_getShipmentId();

		Page<ShipmentItem> shipmentItemsPage =
			shipmentItemResource.getShipmentItemsPage(shipmentId, null);

		int totalCount = GetterUtil.getInteger(
			shipmentItemsPage.getTotalCount());

		ShipmentItem shipmentItem1 = testGetShipmentItemsPage_addShipmentItem(
			shipmentId, randomShipmentItem());

		ShipmentItem shipmentItem2 = testGetShipmentItemsPage_addShipmentItem(
			shipmentId, randomShipmentItem());

		ShipmentItem shipmentItem3 = testGetShipmentItemsPage_addShipmentItem(
			shipmentId, randomShipmentItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ShipmentItem> page1 =
				shipmentItemResource.getShipmentItemsPage(
					shipmentId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(shipmentItem1, (List<ShipmentItem>)page1.getItems());

			Page<ShipmentItem> page2 =
				shipmentItemResource.getShipmentItemsPage(
					shipmentId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(shipmentItem2, (List<ShipmentItem>)page2.getItems());

			Page<ShipmentItem> page3 =
				shipmentItemResource.getShipmentItemsPage(
					shipmentId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(shipmentItem3, (List<ShipmentItem>)page3.getItems());
		}
		else {
			Page<ShipmentItem> page1 =
				shipmentItemResource.getShipmentItemsPage(
					shipmentId, Pagination.of(1, totalCount + 2));

			List<ShipmentItem> shipmentItems1 =
				(List<ShipmentItem>)page1.getItems();

			Assert.assertEquals(
				shipmentItems1.toString(), totalCount + 2,
				shipmentItems1.size());

			Page<ShipmentItem> page2 =
				shipmentItemResource.getShipmentItemsPage(
					shipmentId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ShipmentItem> shipmentItems2 =
				(List<ShipmentItem>)page2.getItems();

			Assert.assertEquals(
				shipmentItems2.toString(), 1, shipmentItems2.size());

			Page<ShipmentItem> page3 =
				shipmentItemResource.getShipmentItemsPage(
					shipmentId, Pagination.of(1, (int)totalCount + 3));

			assertContains(shipmentItem1, (List<ShipmentItem>)page3.getItems());
			assertContains(shipmentItem2, (List<ShipmentItem>)page3.getItems());
			assertContains(shipmentItem3, (List<ShipmentItem>)page3.getItems());
		}
	}

	protected ShipmentItem testGetShipmentItemsPage_addShipmentItem(
			Long shipmentId, ShipmentItem shipmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetShipmentItemsPage_getShipmentId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetShipmentItemsPage_getIrrelevantShipmentId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchShipmentItem() throws Exception {
		ShipmentItem postShipmentItem = testPatchShipmentItem_addShipmentItem();

		ShipmentItem randomPatchShipmentItem = randomPatchShipmentItem();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ShipmentItem patchShipmentItem = shipmentItemResource.patchShipmentItem(
			postShipmentItem.getId(), randomPatchShipmentItem);

		ShipmentItem expectedPatchShipmentItem = postShipmentItem.clone();

		BeanTestUtil.copyProperties(
			randomPatchShipmentItem, expectedPatchShipmentItem);

		ShipmentItem getShipmentItem = shipmentItemResource.getShipmentItem(
			patchShipmentItem.getId());

		assertEquals(expectedPatchShipmentItem, getShipmentItem);
		assertValid(getShipmentItem);
	}

	protected ShipmentItem testPatchShipmentItem_addShipmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchShipmentItemByExternalReferenceCode()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostShipmentItem() throws Exception {
		ShipmentItem randomShipmentItem = randomShipmentItem();

		ShipmentItem postShipmentItem = testPostShipmentItem_addShipmentItem(
			randomShipmentItem);

		assertEquals(randomShipmentItem, postShipmentItem);
		assertValid(postShipmentItem);
	}

	protected ShipmentItem testPostShipmentItem_addShipmentItem(
			ShipmentItem shipmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostShipmentItemByExternalReferenceCode() throws Exception {
		ShipmentItem randomShipmentItem = randomShipmentItem();

		ShipmentItem postShipmentItem =
			testPostShipmentItemByExternalReferenceCode_addShipmentItem(
				randomShipmentItem);

		assertEquals(randomShipmentItem, postShipmentItem);
		assertValid(postShipmentItem);
	}

	protected ShipmentItem
			testPostShipmentItemByExternalReferenceCode_addShipmentItem(
				ShipmentItem shipmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutShipmentByExternalReferenceCodeItem() throws Exception {
		ShipmentItem postShipmentItem =
			testPutShipmentByExternalReferenceCodeItem_addShipmentItem();

		ShipmentItem randomShipmentItem = randomShipmentItem();

		ShipmentItem putShipmentItem =
			shipmentItemResource.putShipmentByExternalReferenceCodeItem(
				testPutShipmentByExternalReferenceCodeItem_getExternalReferenceCode(
					postShipmentItem),
				randomShipmentItem);

		assertEquals(randomShipmentItem, putShipmentItem);
		assertValid(putShipmentItem);

		ShipmentItem getShipmentItem =
			shipmentItemResource.getShipmentByExternalReferenceCodeItem(
				putShipmentItem.getExternalReferenceCode());

		assertEquals(randomShipmentItem, getShipmentItem);
		assertValid(getShipmentItem);
	}

	protected ShipmentItem
			testPutShipmentByExternalReferenceCodeItem_addShipmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutShipmentByExternalReferenceCodeItem_getExternalReferenceCode(
				ShipmentItem shipmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ShipmentItem shipmentItem1 =
			testBatchEngineDeleteImportTask_addShipmentItem();

		testBatchEngineDeleteImportTask_deleteShipmentItem(
			200, shipmentItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem1.getId()));

		shipmentItem1 = testBatchEngineDeleteImportTask_addShipmentItem();

		testBatchEngineDeleteImportTask_deleteShipmentItem(
			200, null, shipmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem1.getId()));

		shipmentItem1 = testBatchEngineDeleteImportTask_addShipmentItem();
		ShipmentItem shipmentItem2 =
			testBatchEngineDeleteImportTask_addShipmentItem();

		testBatchEngineDeleteImportTask_deleteShipmentItem(
			200, shipmentItem2.getExternalReferenceCode(),
			shipmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem2.getId()));

		testBatchEngineDeleteImportTask_deleteShipmentItem(
			200, shipmentItem2.getExternalReferenceCode(),
			shipmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			shipmentItemResource.getShipmentItemHttpResponse(
				shipmentItem2.getId()));
	}

	protected ShipmentItem testBatchEngineDeleteImportTask_addShipmentItem()
		throws Exception {

		return testDeleteShipmentItem_addShipmentItem();
	}

	protected void testBatchEngineDeleteImportTask_deleteShipmentItem(
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
				"com.liferay.headless.commerce.admin.shipment.dto.v1_0.ShipmentItem",
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

	protected ShipmentItem testGraphQLShipmentItem_addShipmentItem()
		throws Exception {

		return testGraphQLShipmentItem_addShipmentItem(
			testGraphQLShipmentItem_getShipmentId(), randomShipmentItem());
	}

	protected Long testGraphQLShipmentItem_getShipmentId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ShipmentItem testGraphQLShipmentItem_addShipmentItem(
			Long shipmentId, ShipmentItem shipmentItem)
		throws Exception {

		JSONDeserializer<ShipmentItem> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ShipmentItem.class)) {

			if (getGraphQLValue(field.get(shipmentItem)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(shipmentItem)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createShipmentShipmentItem",
						new HashMap<String, Object>() {
							{
								put("shipmentId", shipmentId);
								put("shipmentItem", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createShipmentShipmentItem"),
			ShipmentItem.class);
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
		ShipmentItem shipmentItem, List<ShipmentItem> shipmentItems) {

		boolean contains = false;

		for (ShipmentItem item : shipmentItems) {
			if (equals(shipmentItem, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			shipmentItems + " does not contain " + shipmentItem, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ShipmentItem shipmentItem1, ShipmentItem shipmentItem2) {

		Assert.assertTrue(
			shipmentItem1 + " does not equal " + shipmentItem2,
			equals(shipmentItem1, shipmentItem2));
	}

	protected void assertEquals(
		List<ShipmentItem> shipmentItems1, List<ShipmentItem> shipmentItems2) {

		Assert.assertEquals(shipmentItems1.size(), shipmentItems2.size());

		for (int i = 0; i < shipmentItems1.size(); i++) {
			ShipmentItem shipmentItem1 = shipmentItems1.get(i);
			ShipmentItem shipmentItem2 = shipmentItems2.get(i);

			assertEquals(shipmentItem1, shipmentItem2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ShipmentItem> shipmentItems1, List<ShipmentItem> shipmentItems2) {

		Assert.assertEquals(shipmentItems1.size(), shipmentItems2.size());

		for (ShipmentItem shipmentItem1 : shipmentItems1) {
			boolean contains = false;

			for (ShipmentItem shipmentItem2 : shipmentItems2) {
				if (equals(shipmentItem1, shipmentItem2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				shipmentItems2 + " does not contain " + shipmentItem1,
				contains);
		}
	}

	protected void assertValid(ShipmentItem shipmentItem) throws Exception {
		boolean valid = true;

		if (shipmentItem.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (shipmentItem.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (shipmentItem.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (shipmentItem.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (shipmentItem.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderItemExternalReferenceCode",
					additionalAssertFieldName)) {

				if (shipmentItem.getOrderItemExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderItemId", additionalAssertFieldName)) {
				if (shipmentItem.getOrderItemId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (shipmentItem.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shipmentExternalReferenceCode",
					additionalAssertFieldName)) {

				if (shipmentItem.getShipmentExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shipmentId", additionalAssertFieldName)) {
				if (shipmentItem.getShipmentId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (shipmentItem.getUnitOfMeasureKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userName", additionalAssertFieldName)) {
				if (shipmentItem.getUserName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"validateInventory", additionalAssertFieldName)) {

				if (shipmentItem.getValidateInventory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (shipmentItem.getWarehouseExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (shipmentItem.getWarehouseId() == null) {
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

	protected void assertValid(Page<ShipmentItem> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ShipmentItem> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ShipmentItem> shipmentItems = page.getItems();

		int size = shipmentItems.size();

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
					com.liferay.headless.commerce.admin.shipment.dto.v1_0.
						ShipmentItem.class)) {

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
		ShipmentItem shipmentItem1, ShipmentItem shipmentItem2) {

		if (shipmentItem1 == shipmentItem2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)shipmentItem1.getActions(),
						(Map)shipmentItem2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getCreateDate(),
						shipmentItem2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shipmentItem1.getExternalReferenceCode(),
						shipmentItem2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getId(), shipmentItem2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getModifiedDate(),
						shipmentItem2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderItemExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shipmentItem1.getOrderItemExternalReferenceCode(),
						shipmentItem2.getOrderItemExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderItemId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getOrderItemId(),
						shipmentItem2.getOrderItemId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getQuantity(),
						shipmentItem2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shipmentExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shipmentItem1.getShipmentExternalReferenceCode(),
						shipmentItem2.getShipmentExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shipmentId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getShipmentId(),
						shipmentItem2.getShipmentId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getUnitOfMeasureKey(),
						shipmentItem2.getUnitOfMeasureKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getUserName(),
						shipmentItem2.getUserName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"validateInventory", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shipmentItem1.getValidateInventory(),
						shipmentItem2.getValidateInventory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shipmentItem1.getWarehouseExternalReferenceCode(),
						shipmentItem2.getWarehouseExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shipmentItem1.getWarehouseId(),
						shipmentItem2.getWarehouseId())) {

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

		if (!(_shipmentItemResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_shipmentItemResource;

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
		EntityField entityField, String operator, ShipmentItem shipmentItem) {

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

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = shipmentItem.getCreateDate();

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

				sb.append(_format.format(shipmentItem.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = shipmentItem.getExternalReferenceCode();

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

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = shipmentItem.getModifiedDate();

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

				sb.append(_format.format(shipmentItem.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("orderItemExternalReferenceCode")) {
			Object object = shipmentItem.getOrderItemExternalReferenceCode();

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

		if (entityFieldName.equals("orderItemId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shipmentExternalReferenceCode")) {
			Object object = shipmentItem.getShipmentExternalReferenceCode();

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

		if (entityFieldName.equals("shipmentId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = shipmentItem.getUnitOfMeasureKey();

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

		if (entityFieldName.equals("userName")) {
			Object object = shipmentItem.getUserName();

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

		if (entityFieldName.equals("validateInventory")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("warehouseExternalReferenceCode")) {
			Object object = shipmentItem.getWarehouseExternalReferenceCode();

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

		if (entityFieldName.equals("warehouseId")) {
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

	protected ShipmentItem randomShipmentItem() throws Exception {
		return new ShipmentItem() {
			{
				createDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				orderItemExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderItemId = RandomTestUtil.randomLong();
				shipmentExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shipmentId = RandomTestUtil.randomLong();
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				validateInventory = RandomTestUtil.randomBoolean();
				warehouseExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				warehouseId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ShipmentItem randomIrrelevantShipmentItem() throws Exception {
		ShipmentItem randomIrrelevantShipmentItem = randomShipmentItem();

		return randomIrrelevantShipmentItem;
	}

	protected ShipmentItem randomPatchShipmentItem() throws Exception {
		return randomShipmentItem();
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

	protected ShipmentItemResource shipmentItemResource;
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
		LogFactoryUtil.getLog(BaseShipmentItemResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.shipment.resource.v1_0.
		ShipmentItemResource _shipmentItemResource;

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