/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.inventory.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.inventory.client.resource.v1_0.WarehouseResource;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.WarehouseSerDes;
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
import java.util.TimeZone;

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
public abstract class BaseWarehouseResourceTestCase {

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

		_warehouseResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		warehouseResource = WarehouseResource.builder(
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

		Warehouse warehouse1 = randomWarehouse();

		String json = objectMapper.writeValueAsString(warehouse1);

		Warehouse warehouse2 = WarehouseSerDes.toDTO(json);

		Assert.assertTrue(equals(warehouse1, warehouse2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Warehouse warehouse = randomWarehouse();

		String json1 = objectMapper.writeValueAsString(warehouse);
		String json2 = WarehouseSerDes.toJSON(warehouse);

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

		Warehouse warehouse = randomWarehouse();

		warehouse.setCity(regex);
		warehouse.setCountryISOCode(regex);
		warehouse.setExternalReferenceCode(regex);
		warehouse.setRegionISOCode(regex);
		warehouse.setStreet1(regex);
		warehouse.setStreet2(regex);
		warehouse.setStreet3(regex);
		warehouse.setType(regex);
		warehouse.setZip(regex);

		String json = WarehouseSerDes.toJSON(warehouse);

		Assert.assertFalse(json.contains(regex));

		warehouse = WarehouseSerDes.toDTO(json);

		Assert.assertEquals(regex, warehouse.getCity());
		Assert.assertEquals(regex, warehouse.getCountryISOCode());
		Assert.assertEquals(regex, warehouse.getExternalReferenceCode());
		Assert.assertEquals(regex, warehouse.getRegionISOCode());
		Assert.assertEquals(regex, warehouse.getStreet1());
		Assert.assertEquals(regex, warehouse.getStreet2());
		Assert.assertEquals(regex, warehouse.getStreet3());
		Assert.assertEquals(regex, warehouse.getType());
		Assert.assertEquals(regex, warehouse.getZip());
	}

	@Test
	public void testDeleteWarehouseByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Warehouse warehouse =
			testDeleteWarehouseByExternalReferenceCode_addWarehouse();

		assertHttpResponseStatusCode(
			204,
			warehouseResource.
				deleteWarehouseByExternalReferenceCodeHttpResponse(
					warehouse.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			warehouseResource.getWarehouseByExternalReferenceCodeHttpResponse(
				warehouse.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			warehouseResource.getWarehouseByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected Warehouse
			testDeleteWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWarehouseByExternalReferenceCode()
		throws Exception {

		// No namespace

		Warehouse warehouse1 =
			testGraphQLDeleteWarehouseByExternalReferenceCode_addWarehouse();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWarehouseByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										warehouse1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteWarehouseByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"warehouseByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + warehouse1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Warehouse warehouse2 =
			testGraphQLDeleteWarehouseByExternalReferenceCode_addWarehouse();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteWarehouseByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											warehouse2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteWarehouseByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0",
					new GraphQLField(
						"warehouseByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										warehouse2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Warehouse
			testGraphQLDeleteWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		return testGraphQLWarehouse_addWarehouse();
	}

	@Test
	public void testDeleteWarehouseId() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Warehouse warehouse = testDeleteWarehouseId_addWarehouse();

		assertHttpResponseStatusCode(
			204,
			warehouseResource.deleteWarehouseIdHttpResponse(warehouse.getId()));

		assertHttpResponseStatusCode(
			404,
			warehouseResource.getWarehouseIdHttpResponse(warehouse.getId()));
		assertHttpResponseStatusCode(
			404, warehouseResource.getWarehouseIdHttpResponse(0L));
	}

	protected Warehouse testDeleteWarehouseId_addWarehouse() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWarehouseId() throws Exception {

		// No namespace

		Warehouse warehouse1 = testGraphQLDeleteWarehouseId_addWarehouse();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWarehouseId",
						new HashMap<String, Object>() {
							{
								put("id", warehouse1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWarehouseId"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"warehouseId",
					new HashMap<String, Object>() {
						{
							put("id", warehouse1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Warehouse warehouse2 = testGraphQLDeleteWarehouseId_addWarehouse();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteWarehouseId",
							new HashMap<String, Object>() {
								{
									put("id", warehouse2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteWarehouseId"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0",
					new GraphQLField(
						"warehouseId",
						new HashMap<String, Object>() {
							{
								put("id", warehouse2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Warehouse testGraphQLDeleteWarehouseId_addWarehouse()
		throws Exception {

		return testGraphQLWarehouse_addWarehouse();
	}

	@Test
	public void testGetWarehouseByExternalReferenceCode() throws Exception {
		Warehouse postWarehouse =
			testGetWarehouseByExternalReferenceCode_addWarehouse();

		Warehouse getWarehouse =
			warehouseResource.getWarehouseByExternalReferenceCode(
				postWarehouse.getExternalReferenceCode());

		assertEquals(postWarehouse, getWarehouse);
		assertValid(getWarehouse);
	}

	protected Warehouse testGetWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWarehouseByExternalReferenceCode()
		throws Exception {

		Warehouse warehouse =
			testGraphQLGetWarehouseByExternalReferenceCode_addWarehouse();

		// No namespace

		Assert.assertTrue(
			equals(
				warehouse,
				WarehouseSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"warehouseByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												warehouse.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/warehouseByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertTrue(
			equals(
				warehouse,
				WarehouseSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminInventory_v1_0",
								new GraphQLField(
									"warehouseByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													warehouse.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminInventory_v1_0",
						"Object/warehouseByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetWarehouseByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"warehouseByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"warehouseByExternalReferenceCode",
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

	protected Warehouse
			testGraphQLGetWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		return testGraphQLWarehouse_addWarehouse();
	}

	@Test
	public void testGetWarehouseId() throws Exception {
		Warehouse postWarehouse = testGetWarehouseId_addWarehouse();

		Warehouse getWarehouse = warehouseResource.getWarehouseId(
			postWarehouse.getId());

		assertEquals(postWarehouse, getWarehouse);
		assertValid(getWarehouse);
	}

	protected Warehouse testGetWarehouseId_addWarehouse() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWarehouseId() throws Exception {
		Warehouse warehouse = testGraphQLGetWarehouseId_addWarehouse();

		// No namespace

		Assert.assertTrue(
			equals(
				warehouse,
				WarehouseSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"warehouseId",
								new HashMap<String, Object>() {
									{
										put("id", warehouse.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/warehouseId"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertTrue(
			equals(
				warehouse,
				WarehouseSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminInventory_v1_0",
								new GraphQLField(
									"warehouseId",
									new HashMap<String, Object>() {
										{
											put("id", warehouse.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminInventory_v1_0",
						"Object/warehouseId"))));
	}

	@Test
	public void testGraphQLGetWarehouseIdNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"warehouseId",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"warehouseId",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Warehouse testGraphQLGetWarehouseId_addWarehouse()
		throws Exception {

		return testGraphQLWarehouse_addWarehouse();
	}

	@Test
	public void testGetWarehousesPage() throws Exception {
		Page<Warehouse> page = warehouseResource.getWarehousesPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Warehouse warehouse1 = testGetWarehousesPage_addWarehouse(
			randomWarehouse());

		Warehouse warehouse2 = testGetWarehousesPage_addWarehouse(
			randomWarehouse());

		page = warehouseResource.getWarehousesPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(warehouse1, (List<Warehouse>)page.getItems());
		assertContains(warehouse2, (List<Warehouse>)page.getItems());
		assertValid(page, testGetWarehousesPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetWarehousesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehousesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Warehouse warehouse1 = randomWarehouse();

		warehouse1 = testGetWarehousesPage_addWarehouse(warehouse1);

		for (EntityField entityField : entityFields) {
			Page<Warehouse> page = warehouseResource.getWarehousesPage(
				null, getFilterString(entityField, "between", warehouse1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(warehouse1),
				(List<Warehouse>)page.getItems());
		}
	}

	@Test
	public void testGetWarehousesPageWithFilterDoubleEquals() throws Exception {
		testGetWarehousesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetWarehousesPageWithFilterStringContains()
		throws Exception {

		testGetWarehousesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetWarehousesPageWithFilterStringEquals() throws Exception {
		testGetWarehousesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetWarehousesPageWithFilterStringStartsWith()
		throws Exception {

		testGetWarehousesPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetWarehousesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Warehouse warehouse1 = testGetWarehousesPage_addWarehouse(
			randomWarehouse());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Warehouse warehouse2 = testGetWarehousesPage_addWarehouse(
			randomWarehouse());

		for (EntityField entityField : entityFields) {
			Page<Warehouse> page = warehouseResource.getWarehousesPage(
				null, getFilterString(entityField, operator, warehouse1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(warehouse1),
				(List<Warehouse>)page.getItems());
		}
	}

	@Test
	public void testGetWarehousesPageWithPagination() throws Exception {
		Page<Warehouse> warehousesPage = warehouseResource.getWarehousesPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(warehousesPage.getTotalCount());

		Warehouse warehouse1 = testGetWarehousesPage_addWarehouse(
			randomWarehouse());

		Warehouse warehouse2 = testGetWarehousesPage_addWarehouse(
			randomWarehouse());

		Warehouse warehouse3 = testGetWarehousesPage_addWarehouse(
			randomWarehouse());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Warehouse> page1 = warehouseResource.getWarehousesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(warehouse1, (List<Warehouse>)page1.getItems());

			Page<Warehouse> page2 = warehouseResource.getWarehousesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(warehouse2, (List<Warehouse>)page2.getItems());

			Page<Warehouse> page3 = warehouseResource.getWarehousesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(warehouse3, (List<Warehouse>)page3.getItems());
		}
		else {
			Page<Warehouse> page1 = warehouseResource.getWarehousesPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Warehouse> warehouses1 = (List<Warehouse>)page1.getItems();

			Assert.assertEquals(
				warehouses1.toString(), totalCount + 2, warehouses1.size());

			Page<Warehouse> page2 = warehouseResource.getWarehousesPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Warehouse> warehouses2 = (List<Warehouse>)page2.getItems();

			Assert.assertEquals(warehouses2.toString(), 1, warehouses2.size());

			Page<Warehouse> page3 = warehouseResource.getWarehousesPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(warehouse1, (List<Warehouse>)page3.getItems());
			assertContains(warehouse2, (List<Warehouse>)page3.getItems());
			assertContains(warehouse3, (List<Warehouse>)page3.getItems());
		}
	}

	@Test
	public void testGetWarehousesPageWithSortDateTime() throws Exception {
		testGetWarehousesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, warehouse1, warehouse2) -> {
				BeanTestUtil.setProperty(
					warehouse1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWarehousesPageWithSortDouble() throws Exception {
		testGetWarehousesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, warehouse1, warehouse2) -> {
				BeanTestUtil.setProperty(
					warehouse1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					warehouse2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWarehousesPageWithSortInteger() throws Exception {
		testGetWarehousesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, warehouse1, warehouse2) -> {
				BeanTestUtil.setProperty(warehouse1, entityField.getName(), 0);
				BeanTestUtil.setProperty(warehouse2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWarehousesPageWithSortString() throws Exception {
		testGetWarehousesPageWithSort(
			EntityField.Type.STRING,
			(entityField, warehouse1, warehouse2) -> {
				Class<?> clazz = warehouse1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						warehouse1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						warehouse2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						warehouse1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						warehouse2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						warehouse1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						warehouse2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWarehousesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Warehouse, Warehouse, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Warehouse warehouse1 = randomWarehouse();
		Warehouse warehouse2 = randomWarehouse();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, warehouse1, warehouse2);
		}

		warehouse1 = testGetWarehousesPage_addWarehouse(warehouse1);

		warehouse2 = testGetWarehousesPage_addWarehouse(warehouse2);

		Page<Warehouse> page = warehouseResource.getWarehousesPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Warehouse> ascPage = warehouseResource.getWarehousesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(warehouse1, (List<Warehouse>)ascPage.getItems());
			assertContains(warehouse2, (List<Warehouse>)ascPage.getItems());

			Page<Warehouse> descPage = warehouseResource.getWarehousesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(warehouse2, (List<Warehouse>)descPage.getItems());
			assertContains(warehouse1, (List<Warehouse>)descPage.getItems());
		}
	}

	protected Warehouse testGetWarehousesPage_addWarehouse(Warehouse warehouse)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWarehousesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"warehouses",
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

		JSONObject warehousesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/warehouses");

		long totalCount = warehousesJSONObject.getLong("totalCount");

		Warehouse warehouse1 = testGraphQLWarehouse_addWarehouse(
			randomWarehouse());

		Warehouse warehouse2 = testGraphQLWarehouse_addWarehouse(
			randomWarehouse());

		warehousesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/warehouses");

		Assert.assertEquals(
			totalCount + 2, warehousesJSONObject.getLong("totalCount"));

		assertContains(
			warehouse1,
			Arrays.asList(
				WarehouseSerDes.toDTOs(
					warehousesJSONObject.getString("items"))));
		assertContains(
			warehouse2,
			Arrays.asList(
				WarehouseSerDes.toDTOs(
					warehousesJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		warehousesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminInventory_v1_0",
			"JSONObject/warehouses");

		Assert.assertEquals(
			totalCount + 2, warehousesJSONObject.getLong("totalCount"));

		assertContains(
			warehouse1,
			Arrays.asList(
				WarehouseSerDes.toDTOs(
					warehousesJSONObject.getString("items"))));
		assertContains(
			warehouse2,
			Arrays.asList(
				WarehouseSerDes.toDTOs(
					warehousesJSONObject.getString("items"))));
	}

	@Test
	public void testPatchWarehouseByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchWarehouseId() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostWarehouse() throws Exception {
		Warehouse randomWarehouse = randomWarehouse();

		Warehouse postWarehouse = testPostWarehouse_addWarehouse(
			randomWarehouse);

		assertEquals(randomWarehouse, postWarehouse);
		assertValid(postWarehouse);
	}

	protected Warehouse testPostWarehouse_addWarehouse(Warehouse warehouse)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostWarehouse() throws Exception {
		Warehouse randomWarehouse = randomWarehouse();

		Warehouse warehouse = testGraphQLWarehouse_addWarehouse(
			randomWarehouse);

		Assert.assertTrue(equals(randomWarehouse, warehouse));
	}

	@Test
	public void testPutWarehouseByExternalReferenceCode() throws Exception {
		Warehouse postWarehouse =
			testPutWarehouseByExternalReferenceCode_addWarehouse();

		Warehouse randomWarehouse = randomWarehouse();

		Warehouse putWarehouse =
			warehouseResource.putWarehouseByExternalReferenceCode(
				postWarehouse.getExternalReferenceCode(), randomWarehouse);

		assertEquals(randomWarehouse, putWarehouse);
		assertValid(putWarehouse);

		Warehouse getWarehouse =
			warehouseResource.getWarehouseByExternalReferenceCode(
				putWarehouse.getExternalReferenceCode());

		assertEquals(randomWarehouse, getWarehouse);
		assertValid(getWarehouse);

		Warehouse newWarehouse =
			testPutWarehouseByExternalReferenceCode_createWarehouse();

		putWarehouse = warehouseResource.putWarehouseByExternalReferenceCode(
			newWarehouse.getExternalReferenceCode(), newWarehouse);

		assertEquals(newWarehouse, putWarehouse);
		assertValid(putWarehouse);

		getWarehouse = warehouseResource.getWarehouseByExternalReferenceCode(
			putWarehouse.getExternalReferenceCode());

		assertEquals(newWarehouse, getWarehouse);

		Assert.assertEquals(
			newWarehouse.getExternalReferenceCode(),
			putWarehouse.getExternalReferenceCode());
	}

	protected Warehouse testPutWarehouseByExternalReferenceCode_addWarehouse()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Warehouse
			testPutWarehouseByExternalReferenceCode_createWarehouse()
		throws Exception {

		return randomWarehouse();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Warehouse warehouse1 = testBatchEngineDeleteImportTask_addWarehouse();

		testBatchEngineDeleteImportTask_deleteWarehouse(
			200, warehouse1.getExternalReferenceCode());
	}

	protected Warehouse testBatchEngineDeleteImportTask_addWarehouse()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void testBatchEngineDeleteImportTask_deleteWarehouse(
			int expectedStatusCode, String externalReferenceCode,
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
				"com.liferay.headless.commerce.admin.inventory.dto.v1_0.Warehouse",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Warehouse testGraphQLWarehouse_addWarehouse() throws Exception {
		return testGraphQLWarehouse_addWarehouse(randomWarehouse());
	}

	protected Warehouse testGraphQLWarehouse_addWarehouse(Warehouse warehouse)
		throws Exception {

		JSONDeserializer<Warehouse> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Warehouse.class)) {

			if (getGraphQLValue(field.get(warehouse)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(warehouse)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createWarehouse",
						new HashMap<String, Object>() {
							{
								put("warehouse", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createWarehouse"),
			Warehouse.class);
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
		Warehouse warehouse, List<Warehouse> warehouses) {

		boolean contains = false;

		for (Warehouse item : warehouses) {
			if (equals(warehouse, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			warehouses + " does not contain " + warehouse, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Warehouse warehouse1, Warehouse warehouse2) {
		Assert.assertTrue(
			warehouse1 + " does not equal " + warehouse2,
			equals(warehouse1, warehouse2));
	}

	protected void assertEquals(
		List<Warehouse> warehouses1, List<Warehouse> warehouses2) {

		Assert.assertEquals(warehouses1.size(), warehouses2.size());

		for (int i = 0; i < warehouses1.size(); i++) {
			Warehouse warehouse1 = warehouses1.get(i);
			Warehouse warehouse2 = warehouses2.get(i);

			assertEquals(warehouse1, warehouse2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Warehouse> warehouses1, List<Warehouse> warehouses2) {

		Assert.assertEquals(warehouses1.size(), warehouses2.size());

		for (Warehouse warehouse1 : warehouses1) {
			boolean contains = false;

			for (Warehouse warehouse2 : warehouses2) {
				if (equals(warehouse1, warehouse2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				warehouses2 + " does not contain " + warehouse1, contains);
		}
	}

	protected void assertValid(Warehouse warehouse) throws Exception {
		boolean valid = true;

		if (warehouse.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (warehouse.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (warehouse.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (warehouse.getCity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (warehouse.getCountryISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (warehouse.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (warehouse.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (warehouse.getLatitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (warehouse.getLongitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (warehouse.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (warehouse.getRegionISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (warehouse.getStreet1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (warehouse.getStreet2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (warehouse.getStreet3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (warehouse.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("warehouseItems", additionalAssertFieldName)) {
				if (warehouse.getWarehouseItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (warehouse.getZip() == null) {
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

	protected void assertValid(Page<Warehouse> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Warehouse> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Warehouse> warehouses = page.getItems();

		int size = warehouses.size();

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
					com.liferay.headless.commerce.admin.inventory.dto.v1_0.
						Warehouse.class)) {

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

	protected boolean equals(Warehouse warehouse1, Warehouse warehouse2) {
		if (warehouse1 == warehouse2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)warehouse1.getActions(),
						(Map)warehouse2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getActive(), warehouse2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getCity(), warehouse2.getCity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getCountryISOCode(),
						warehouse2.getCountryISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)warehouse1.getDescription(),
						(Map)warehouse2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouse1.getExternalReferenceCode(),
						warehouse2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getId(), warehouse2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getLatitude(), warehouse2.getLatitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getLongitude(), warehouse2.getLongitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)warehouse1.getName(), (Map)warehouse2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getRegionISOCode(),
						warehouse2.getRegionISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getStreet1(), warehouse2.getStreet1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getStreet2(), warehouse2.getStreet2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getStreet3(), warehouse2.getStreet3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getType(), warehouse2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("warehouseItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getWarehouseItems(),
						warehouse2.getWarehouseItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouse1.getZip(), warehouse2.getZip())) {

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

		if (!(_warehouseResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_warehouseResource;

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
		EntityField entityField, String operator, Warehouse warehouse) {

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

		if (entityFieldName.equals("city")) {
			Object object = warehouse.getCity();

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

		if (entityFieldName.equals("countryISOCode")) {
			Object object = warehouse.getCountryISOCode();

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

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = warehouse.getExternalReferenceCode();

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

		if (entityFieldName.equals("latitude")) {
			sb.append(String.valueOf(warehouse.getLatitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("longitude")) {
			sb.append(String.valueOf(warehouse.getLongitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("regionISOCode")) {
			Object object = warehouse.getRegionISOCode();

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

		if (entityFieldName.equals("street1")) {
			Object object = warehouse.getStreet1();

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

		if (entityFieldName.equals("street2")) {
			Object object = warehouse.getStreet2();

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

		if (entityFieldName.equals("street3")) {
			Object object = warehouse.getStreet3();

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

		if (entityFieldName.equals("type")) {
			Object object = warehouse.getType();

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

		if (entityFieldName.equals("warehouseItems")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("zip")) {
			Object object = warehouse.getZip();

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

	protected Warehouse randomWarehouse() throws Exception {
		return new Warehouse() {
			{
				active = RandomTestUtil.randomBoolean();
				city = StringUtil.toLowerCase(RandomTestUtil.randomString());
				countryISOCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				latitude = RandomTestUtil.randomDouble();
				longitude = RandomTestUtil.randomDouble();
				regionISOCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				street1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street2 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street3 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				zip = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Warehouse randomIrrelevantWarehouse() throws Exception {
		Warehouse randomIrrelevantWarehouse = randomWarehouse();

		return randomIrrelevantWarehouse;
	}

	protected Warehouse randomPatchWarehouse() throws Exception {
		return randomWarehouse();
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

	protected WarehouseResource warehouseResource;
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
		LogFactoryUtil.getLog(BaseWarehouseResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.inventory.resource.v1_0.
		WarehouseResource _warehouseResource;

}