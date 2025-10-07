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
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseItem;
import com.liferay.headless.commerce.admin.inventory.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.inventory.client.resource.v1_0.WarehouseItemResource;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.WarehouseItemSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
public abstract class BaseWarehouseItemResourceTestCase {

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

		_warehouseItemResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		warehouseItemResource = WarehouseItemResource.builder(
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

		WarehouseItem warehouseItem1 = randomWarehouseItem();

		String json = objectMapper.writeValueAsString(warehouseItem1);

		WarehouseItem warehouseItem2 = WarehouseItemSerDes.toDTO(json);

		Assert.assertTrue(equals(warehouseItem1, warehouseItem2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WarehouseItem warehouseItem = randomWarehouseItem();

		String json1 = objectMapper.writeValueAsString(warehouseItem);
		String json2 = WarehouseItemSerDes.toJSON(warehouseItem);

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

		WarehouseItem warehouseItem = randomWarehouseItem();

		warehouseItem.setExternalReferenceCode(regex);
		warehouseItem.setSku(regex);
		warehouseItem.setUnitOfMeasureKey(regex);
		warehouseItem.setWarehouseExternalReferenceCode(regex);

		String json = WarehouseItemSerDes.toJSON(warehouseItem);

		Assert.assertFalse(json.contains(regex));

		warehouseItem = WarehouseItemSerDes.toDTO(json);

		Assert.assertEquals(regex, warehouseItem.getExternalReferenceCode());
		Assert.assertEquals(regex, warehouseItem.getSku());
		Assert.assertEquals(regex, warehouseItem.getUnitOfMeasureKey());
		Assert.assertEquals(
			regex, warehouseItem.getWarehouseExternalReferenceCode());
	}

	@Test
	public void testDeleteWarehouseItem() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WarehouseItem warehouseItem =
			testDeleteWarehouseItem_addWarehouseItem();

		assertHttpResponseStatusCode(
			204,
			warehouseItemResource.deleteWarehouseItemHttpResponse(
				warehouseItem.getId()));

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem.getId()));
		assertHttpResponseStatusCode(
			404, warehouseItemResource.getWarehouseItemHttpResponse(0L));
	}

	protected WarehouseItem testDeleteWarehouseItem_addWarehouseItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWarehouseItem() throws Exception {

		// No namespace

		WarehouseItem warehouseItem1 =
			testGraphQLDeleteWarehouseItem_addWarehouseItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWarehouseItem",
						new HashMap<String, Object>() {
							{
								put("id", warehouseItem1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWarehouseItem"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"warehouseItem",
					new HashMap<String, Object>() {
						{
							put("id", warehouseItem1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminInventory_v1_0

		WarehouseItem warehouseItem2 =
			testGraphQLDeleteWarehouseItem_addWarehouseItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteWarehouseItem",
							new HashMap<String, Object>() {
								{
									put("id", warehouseItem2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteWarehouseItem"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0",
					new GraphQLField(
						"warehouseItem",
						new HashMap<String, Object>() {
							{
								put("id", warehouseItem2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WarehouseItem testGraphQLDeleteWarehouseItem_addWarehouseItem()
		throws Exception {

		return testGraphQLWarehouseItem_addWarehouseItem();
	}

	@Test
	public void testDeleteWarehouseItemBatch() throws Exception {
		WarehouseItem warehouseItem1 =
			testDeleteWarehouseItemBatch_addWarehouseItem();

		testDeleteWarehouseItemBatch_deleteWarehouseItem(
			202, warehouseItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem1.getId()));

		warehouseItem1 = testDeleteWarehouseItemBatch_addWarehouseItem();

		testDeleteWarehouseItemBatch_deleteWarehouseItem(
			202, null, warehouseItem1.getId());

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem1.getId()));

		warehouseItem1 = testDeleteWarehouseItemBatch_addWarehouseItem();
		WarehouseItem warehouseItem2 =
			testDeleteWarehouseItemBatch_addWarehouseItem();

		testDeleteWarehouseItemBatch_deleteWarehouseItem(
			202, warehouseItem2.getExternalReferenceCode(),
			warehouseItem1.getId());

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem2.getId()));

		testDeleteWarehouseItemBatch_deleteWarehouseItem(
			202, warehouseItem2.getExternalReferenceCode(),
			warehouseItem1.getId());

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem2.getId()));
	}

	protected WarehouseItem testDeleteWarehouseItemBatch_addWarehouseItem()
		throws Exception {

		return testDeleteWarehouseItem_addWarehouseItem();
	}

	protected void testDeleteWarehouseItemBatch_deleteWarehouseItem(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			warehouseItemResource.deleteWarehouseItemBatchHttpResponse(
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
	public void testDeleteWarehouseItemByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WarehouseItem warehouseItem =
			testDeleteWarehouseItemByExternalReferenceCode_addWarehouseItem();

		assertHttpResponseStatusCode(
			204,
			warehouseItemResource.
				deleteWarehouseItemByExternalReferenceCodeHttpResponse(
					warehouseItem.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.
				getWarehouseItemByExternalReferenceCodeHttpResponse(
					warehouseItem.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.
				getWarehouseItemByExternalReferenceCodeHttpResponse("-"));
	}

	protected WarehouseItem
			testDeleteWarehouseItemByExternalReferenceCode_addWarehouseItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWarehouseItemByExternalReferenceCode()
		throws Exception {

		// No namespace

		WarehouseItem warehouseItem1 =
			testGraphQLDeleteWarehouseItemByExternalReferenceCode_addWarehouseItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWarehouseItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										warehouseItem1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteWarehouseItemByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"warehouseItemByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									warehouseItem1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminInventory_v1_0

		WarehouseItem warehouseItem2 =
			testGraphQLDeleteWarehouseItemByExternalReferenceCode_addWarehouseItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteWarehouseItemByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											warehouseItem2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteWarehouseItemByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0",
					new GraphQLField(
						"warehouseItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										warehouseItem2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WarehouseItem
			testGraphQLDeleteWarehouseItemByExternalReferenceCode_addWarehouseItem()
		throws Exception {

		return testGraphQLWarehouseItem_addWarehouseItem();
	}

	@Test
	public void testGetWarehouseByExternalReferenceCodeWarehouseItemsPage()
		throws Exception {

		String externalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getIrrelevantExternalReferenceCode();

		Page<WarehouseItem> page =
			warehouseItemResource.
				getWarehouseByExternalReferenceCodeWarehouseItemsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WarehouseItem irrelevantWarehouseItem =
				testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_addWarehouseItem(
					irrelevantExternalReferenceCode,
					randomIrrelevantWarehouseItem());

			page =
				warehouseItemResource.
					getWarehouseByExternalReferenceCodeWarehouseItemsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWarehouseItem, (List<WarehouseItem>)page.getItems());
			assertValid(
				page,
				testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WarehouseItem warehouseItem1 =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_addWarehouseItem(
				externalReferenceCode, randomWarehouseItem());

		WarehouseItem warehouseItem2 =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_addWarehouseItem(
				externalReferenceCode, randomWarehouseItem());

		page =
			warehouseItemResource.
				getWarehouseByExternalReferenceCodeWarehouseItemsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(warehouseItem1, (List<WarehouseItem>)page.getItems());
		assertContains(warehouseItem2, (List<WarehouseItem>)page.getItems());
		assertValid(
			page,
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getExpectedActions(
				externalReferenceCode));

		warehouseItemResource.deleteWarehouseItem(warehouseItem1.getId());

		warehouseItemResource.deleteWarehouseItem(warehouseItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseByExternalReferenceCodeWarehouseItemsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getExternalReferenceCode();

		Page<WarehouseItem> warehouseItemsPage =
			warehouseItemResource.
				getWarehouseByExternalReferenceCodeWarehouseItemsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			warehouseItemsPage.getTotalCount());

		WarehouseItem warehouseItem1 =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_addWarehouseItem(
				externalReferenceCode, randomWarehouseItem());

		WarehouseItem warehouseItem2 =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_addWarehouseItem(
				externalReferenceCode, randomWarehouseItem());

		WarehouseItem warehouseItem3 =
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_addWarehouseItem(
				externalReferenceCode, randomWarehouseItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WarehouseItem> page1 =
				warehouseItemResource.
					getWarehouseByExternalReferenceCodeWarehouseItemsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				warehouseItem1, (List<WarehouseItem>)page1.getItems());

			Page<WarehouseItem> page2 =
				warehouseItemResource.
					getWarehouseByExternalReferenceCodeWarehouseItemsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				warehouseItem2, (List<WarehouseItem>)page2.getItems());

			Page<WarehouseItem> page3 =
				warehouseItemResource.
					getWarehouseByExternalReferenceCodeWarehouseItemsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				warehouseItem3, (List<WarehouseItem>)page3.getItems());
		}
		else {
			Page<WarehouseItem> page1 =
				warehouseItemResource.
					getWarehouseByExternalReferenceCodeWarehouseItemsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<WarehouseItem> warehouseItems1 =
				(List<WarehouseItem>)page1.getItems();

			Assert.assertEquals(
				warehouseItems1.toString(), totalCount + 2,
				warehouseItems1.size());

			Page<WarehouseItem> page2 =
				warehouseItemResource.
					getWarehouseByExternalReferenceCodeWarehouseItemsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WarehouseItem> warehouseItems2 =
				(List<WarehouseItem>)page2.getItems();

			Assert.assertEquals(
				warehouseItems2.toString(), 1, warehouseItems2.size());

			Page<WarehouseItem> page3 =
				warehouseItemResource.
					getWarehouseByExternalReferenceCodeWarehouseItemsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				warehouseItem1, (List<WarehouseItem>)page3.getItems());
			assertContains(
				warehouseItem2, (List<WarehouseItem>)page3.getItems());
			assertContains(
				warehouseItem3, (List<WarehouseItem>)page3.getItems());
		}
	}

	protected WarehouseItem
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_addWarehouseItem(
				String externalReferenceCode, WarehouseItem warehouseItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseItemsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWarehouseIdWarehouseItemsPage() throws Exception {
		Long id = testGetWarehouseIdWarehouseItemsPage_getId();
		Long irrelevantId =
			testGetWarehouseIdWarehouseItemsPage_getIrrelevantId();

		Page<WarehouseItem> page =
			warehouseItemResource.getWarehouseIdWarehouseItemsPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			WarehouseItem irrelevantWarehouseItem =
				testGetWarehouseIdWarehouseItemsPage_addWarehouseItem(
					irrelevantId, randomIrrelevantWarehouseItem());

			page = warehouseItemResource.getWarehouseIdWarehouseItemsPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWarehouseItem, (List<WarehouseItem>)page.getItems());
			assertValid(
				page,
				testGetWarehouseIdWarehouseItemsPage_getExpectedActions(
					irrelevantId));
		}

		WarehouseItem warehouseItem1 =
			testGetWarehouseIdWarehouseItemsPage_addWarehouseItem(
				id, randomWarehouseItem());

		WarehouseItem warehouseItem2 =
			testGetWarehouseIdWarehouseItemsPage_addWarehouseItem(
				id, randomWarehouseItem());

		page = warehouseItemResource.getWarehouseIdWarehouseItemsPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(warehouseItem1, (List<WarehouseItem>)page.getItems());
		assertContains(warehouseItem2, (List<WarehouseItem>)page.getItems());
		assertValid(
			page, testGetWarehouseIdWarehouseItemsPage_getExpectedActions(id));

		warehouseItemResource.deleteWarehouseItem(warehouseItem1.getId());

		warehouseItemResource.deleteWarehouseItem(warehouseItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseIdWarehouseItemsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseIdWarehouseItemsPageWithPagination()
		throws Exception {

		Long id = testGetWarehouseIdWarehouseItemsPage_getId();

		Page<WarehouseItem> warehouseItemsPage =
			warehouseItemResource.getWarehouseIdWarehouseItemsPage(id, null);

		int totalCount = GetterUtil.getInteger(
			warehouseItemsPage.getTotalCount());

		WarehouseItem warehouseItem1 =
			testGetWarehouseIdWarehouseItemsPage_addWarehouseItem(
				id, randomWarehouseItem());

		WarehouseItem warehouseItem2 =
			testGetWarehouseIdWarehouseItemsPage_addWarehouseItem(
				id, randomWarehouseItem());

		WarehouseItem warehouseItem3 =
			testGetWarehouseIdWarehouseItemsPage_addWarehouseItem(
				id, randomWarehouseItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WarehouseItem> page1 =
				warehouseItemResource.getWarehouseIdWarehouseItemsPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				warehouseItem1, (List<WarehouseItem>)page1.getItems());

			Page<WarehouseItem> page2 =
				warehouseItemResource.getWarehouseIdWarehouseItemsPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				warehouseItem2, (List<WarehouseItem>)page2.getItems());

			Page<WarehouseItem> page3 =
				warehouseItemResource.getWarehouseIdWarehouseItemsPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				warehouseItem3, (List<WarehouseItem>)page3.getItems());
		}
		else {
			Page<WarehouseItem> page1 =
				warehouseItemResource.getWarehouseIdWarehouseItemsPage(
					id, Pagination.of(1, totalCount + 2));

			List<WarehouseItem> warehouseItems1 =
				(List<WarehouseItem>)page1.getItems();

			Assert.assertEquals(
				warehouseItems1.toString(), totalCount + 2,
				warehouseItems1.size());

			Page<WarehouseItem> page2 =
				warehouseItemResource.getWarehouseIdWarehouseItemsPage(
					id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WarehouseItem> warehouseItems2 =
				(List<WarehouseItem>)page2.getItems();

			Assert.assertEquals(
				warehouseItems2.toString(), 1, warehouseItems2.size());

			Page<WarehouseItem> page3 =
				warehouseItemResource.getWarehouseIdWarehouseItemsPage(
					id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				warehouseItem1, (List<WarehouseItem>)page3.getItems());
			assertContains(
				warehouseItem2, (List<WarehouseItem>)page3.getItems());
			assertContains(
				warehouseItem3, (List<WarehouseItem>)page3.getItems());
		}
	}

	protected WarehouseItem
			testGetWarehouseIdWarehouseItemsPage_addWarehouseItem(
				Long id, WarehouseItem warehouseItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWarehouseIdWarehouseItemsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWarehouseIdWarehouseItemsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWarehouseItem() throws Exception {
		WarehouseItem postWarehouseItem =
			testGetWarehouseItem_addWarehouseItem();

		WarehouseItem getWarehouseItem = warehouseItemResource.getWarehouseItem(
			postWarehouseItem.getId());

		assertEquals(postWarehouseItem, getWarehouseItem);
		assertValid(getWarehouseItem);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WarehouseItem postWarehouseItem =
			testGetWarehouseItem_addWarehouseItem();

		WarehouseItem getWarehouseItem = warehouseItemResource.getWarehouseItem(
			postWarehouseItem.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseItem"
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

		Object item = vulcanCRUDItemDelegate.getItem(postWarehouseItem.getId());

		assertEquals(
			getWarehouseItem, WarehouseItemSerDes.toDTO(item.toString()));
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

	protected WarehouseItem testGetWarehouseItem_addWarehouseItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWarehouseItem() throws Exception {
		WarehouseItem warehouseItem =
			testGraphQLGetWarehouseItem_addWarehouseItem();

		// No namespace

		Assert.assertTrue(
			equals(
				warehouseItem,
				WarehouseItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"warehouseItem",
								new HashMap<String, Object>() {
									{
										put("id", warehouseItem.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/warehouseItem"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertTrue(
			equals(
				warehouseItem,
				WarehouseItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminInventory_v1_0",
								new GraphQLField(
									"warehouseItem",
									new HashMap<String, Object>() {
										{
											put("id", warehouseItem.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminInventory_v1_0",
						"Object/warehouseItem"))));
	}

	@Test
	public void testGraphQLGetWarehouseItemNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"warehouseItem",
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
							"warehouseItem",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WarehouseItem testGraphQLGetWarehouseItem_addWarehouseItem()
		throws Exception {

		return testGraphQLWarehouseItem_addWarehouseItem();
	}

	@Test
	public void testGetWarehouseItemByExternalReferenceCode() throws Exception {
		WarehouseItem postWarehouseItem =
			testGetWarehouseItemByExternalReferenceCode_addWarehouseItem();

		WarehouseItem getWarehouseItem =
			warehouseItemResource.getWarehouseItemByExternalReferenceCode(
				postWarehouseItem.getExternalReferenceCode());

		assertEquals(postWarehouseItem, getWarehouseItem);
		assertValid(getWarehouseItem);
	}

	protected WarehouseItem
			testGetWarehouseItemByExternalReferenceCode_addWarehouseItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWarehouseItemByExternalReferenceCode()
		throws Exception {

		WarehouseItem warehouseItem =
			testGraphQLGetWarehouseItemByExternalReferenceCode_addWarehouseItem();

		// No namespace

		Assert.assertTrue(
			equals(
				warehouseItem,
				WarehouseItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"warehouseItemByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												warehouseItem.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/warehouseItemByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertTrue(
			equals(
				warehouseItem,
				WarehouseItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminInventory_v1_0",
								new GraphQLField(
									"warehouseItemByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													warehouseItem.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminInventory_v1_0",
						"Object/warehouseItemByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetWarehouseItemByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"warehouseItemByExternalReferenceCode",
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
							"warehouseItemByExternalReferenceCode",
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

	protected WarehouseItem
			testGraphQLGetWarehouseItemByExternalReferenceCode_addWarehouseItem()
		throws Exception {

		return testGraphQLWarehouseItem_addWarehouseItem();
	}

	@Test
	public void testGetWarehouseItemsUpdatedPage() throws Exception {
		Page<WarehouseItem> page =
			warehouseItemResource.getWarehouseItemsUpdatedPage(
				RandomTestUtil.nextDate(), RandomTestUtil.nextDate(),
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		WarehouseItem warehouseItem1 =
			testGetWarehouseItemsUpdatedPage_addWarehouseItem(
				randomWarehouseItem());

		WarehouseItem warehouseItem2 =
			testGetWarehouseItemsUpdatedPage_addWarehouseItem(
				randomWarehouseItem());

		page = warehouseItemResource.getWarehouseItemsUpdatedPage(
			null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(warehouseItem1, (List<WarehouseItem>)page.getItems());
		assertContains(warehouseItem2, (List<WarehouseItem>)page.getItems());
		assertValid(
			page, testGetWarehouseItemsUpdatedPage_getExpectedActions());

		warehouseItemResource.deleteWarehouseItem(warehouseItem1.getId());

		warehouseItemResource.deleteWarehouseItem(warehouseItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseItemsUpdatedPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseItemsUpdatedPageWithPagination()
		throws Exception {

		Page<WarehouseItem> warehouseItemsPage =
			warehouseItemResource.getWarehouseItemsUpdatedPage(
				null, null, null);

		int totalCount = GetterUtil.getInteger(
			warehouseItemsPage.getTotalCount());

		WarehouseItem warehouseItem1 =
			testGetWarehouseItemsUpdatedPage_addWarehouseItem(
				randomWarehouseItem());

		WarehouseItem warehouseItem2 =
			testGetWarehouseItemsUpdatedPage_addWarehouseItem(
				randomWarehouseItem());

		WarehouseItem warehouseItem3 =
			testGetWarehouseItemsUpdatedPage_addWarehouseItem(
				randomWarehouseItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WarehouseItem> page1 =
				warehouseItemResource.getWarehouseItemsUpdatedPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				warehouseItem1, (List<WarehouseItem>)page1.getItems());

			Page<WarehouseItem> page2 =
				warehouseItemResource.getWarehouseItemsUpdatedPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				warehouseItem2, (List<WarehouseItem>)page2.getItems());

			Page<WarehouseItem> page3 =
				warehouseItemResource.getWarehouseItemsUpdatedPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				warehouseItem3, (List<WarehouseItem>)page3.getItems());
		}
		else {
			Page<WarehouseItem> page1 =
				warehouseItemResource.getWarehouseItemsUpdatedPage(
					null, null, Pagination.of(1, totalCount + 2));

			List<WarehouseItem> warehouseItems1 =
				(List<WarehouseItem>)page1.getItems();

			Assert.assertEquals(
				warehouseItems1.toString(), totalCount + 2,
				warehouseItems1.size());

			Page<WarehouseItem> page2 =
				warehouseItemResource.getWarehouseItemsUpdatedPage(
					null, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WarehouseItem> warehouseItems2 =
				(List<WarehouseItem>)page2.getItems();

			Assert.assertEquals(
				warehouseItems2.toString(), 1, warehouseItems2.size());

			Page<WarehouseItem> page3 =
				warehouseItemResource.getWarehouseItemsUpdatedPage(
					null, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				warehouseItem1, (List<WarehouseItem>)page3.getItems());
			assertContains(
				warehouseItem2, (List<WarehouseItem>)page3.getItems());
			assertContains(
				warehouseItem3, (List<WarehouseItem>)page3.getItems());
		}
	}

	protected WarehouseItem testGetWarehouseItemsUpdatedPage_addWarehouseItem(
			WarehouseItem warehouseItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchWarehouseItem() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchWarehouseItemByExternalReferenceCode()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostWarehouseByExternalReferenceCodeWarehouseItem()
		throws Exception {

		WarehouseItem randomWarehouseItem = randomWarehouseItem();

		WarehouseItem postWarehouseItem =
			testPostWarehouseByExternalReferenceCodeWarehouseItem_addWarehouseItem(
				randomWarehouseItem);

		assertEquals(randomWarehouseItem, postWarehouseItem);
		assertValid(postWarehouseItem);
	}

	protected WarehouseItem
			testPostWarehouseByExternalReferenceCodeWarehouseItem_addWarehouseItem(
				WarehouseItem warehouseItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWarehouseIdWarehouseItem() throws Exception {
		WarehouseItem randomWarehouseItem = randomWarehouseItem();

		WarehouseItem postWarehouseItem =
			testPostWarehouseIdWarehouseItem_addWarehouseItem(
				randomWarehouseItem);

		assertEquals(randomWarehouseItem, postWarehouseItem);
		assertValid(postWarehouseItem);
	}

	protected WarehouseItem testPostWarehouseIdWarehouseItem_addWarehouseItem(
			WarehouseItem warehouseItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWarehouseItemByExternalReferenceCode()
		throws Exception {

		WarehouseItem randomWarehouseItem = randomWarehouseItem();

		WarehouseItem postWarehouseItem =
			testPostWarehouseItemByExternalReferenceCode_addWarehouseItem(
				randomWarehouseItem);

		assertEquals(randomWarehouseItem, postWarehouseItem);
		assertValid(postWarehouseItem);
	}

	protected WarehouseItem
			testPostWarehouseItemByExternalReferenceCode_addWarehouseItem(
				WarehouseItem warehouseItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutWarehouseItemByExternalReferenceCode() throws Exception {
		WarehouseItem postWarehouseItem =
			testPutWarehouseItemByExternalReferenceCode_addWarehouseItem();

		WarehouseItem randomWarehouseItem = randomWarehouseItem();

		WarehouseItem putWarehouseItem =
			warehouseItemResource.putWarehouseItemByExternalReferenceCode(
				postWarehouseItem.getExternalReferenceCode(),
				randomWarehouseItem);

		assertEquals(randomWarehouseItem, putWarehouseItem);
		assertValid(putWarehouseItem);

		WarehouseItem getWarehouseItem =
			warehouseItemResource.getWarehouseItemByExternalReferenceCode(
				putWarehouseItem.getExternalReferenceCode());

		assertEquals(randomWarehouseItem, getWarehouseItem);
		assertValid(getWarehouseItem);

		WarehouseItem newWarehouseItem =
			testPutWarehouseItemByExternalReferenceCode_createWarehouseItem();

		putWarehouseItem =
			warehouseItemResource.putWarehouseItemByExternalReferenceCode(
				newWarehouseItem.getExternalReferenceCode(), newWarehouseItem);

		assertEquals(newWarehouseItem, putWarehouseItem);
		assertValid(putWarehouseItem);

		getWarehouseItem =
			warehouseItemResource.getWarehouseItemByExternalReferenceCode(
				putWarehouseItem.getExternalReferenceCode());

		assertEquals(newWarehouseItem, getWarehouseItem);

		Assert.assertEquals(
			newWarehouseItem.getExternalReferenceCode(),
			putWarehouseItem.getExternalReferenceCode());
	}

	protected WarehouseItem
			testPutWarehouseItemByExternalReferenceCode_addWarehouseItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WarehouseItem
			testPutWarehouseItemByExternalReferenceCode_createWarehouseItem()
		throws Exception {

		return randomWarehouseItem();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WarehouseItem warehouseItem1 =
			testBatchEngineDeleteImportTask_addWarehouseItem();

		testBatchEngineDeleteImportTask_deleteWarehouseItem(
			200, warehouseItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem1.getId()));

		warehouseItem1 = testBatchEngineDeleteImportTask_addWarehouseItem();

		testBatchEngineDeleteImportTask_deleteWarehouseItem(
			200, null, warehouseItem1.getId());

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem1.getId()));

		warehouseItem1 = testBatchEngineDeleteImportTask_addWarehouseItem();
		WarehouseItem warehouseItem2 =
			testBatchEngineDeleteImportTask_addWarehouseItem();

		testBatchEngineDeleteImportTask_deleteWarehouseItem(
			200, warehouseItem2.getExternalReferenceCode(),
			warehouseItem1.getId());

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem2.getId()));

		testBatchEngineDeleteImportTask_deleteWarehouseItem(
			200, warehouseItem2.getExternalReferenceCode(),
			warehouseItem1.getId());

		assertHttpResponseStatusCode(
			404,
			warehouseItemResource.getWarehouseItemHttpResponse(
				warehouseItem2.getId()));
	}

	protected WarehouseItem testBatchEngineDeleteImportTask_addWarehouseItem()
		throws Exception {

		return testDeleteWarehouseItem_addWarehouseItem();
	}

	protected void testBatchEngineDeleteImportTask_deleteWarehouseItem(
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
				"com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseItem",
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

	protected WarehouseItem testGraphQLWarehouseItem_addWarehouseItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		WarehouseItem warehouseItem, List<WarehouseItem> warehouseItems) {

		boolean contains = false;

		for (WarehouseItem item : warehouseItems) {
			if (equals(warehouseItem, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			warehouseItems + " does not contain " + warehouseItem, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WarehouseItem warehouseItem1, WarehouseItem warehouseItem2) {

		Assert.assertTrue(
			warehouseItem1 + " does not equal " + warehouseItem2,
			equals(warehouseItem1, warehouseItem2));
	}

	protected void assertEquals(
		List<WarehouseItem> warehouseItems1,
		List<WarehouseItem> warehouseItems2) {

		Assert.assertEquals(warehouseItems1.size(), warehouseItems2.size());

		for (int i = 0; i < warehouseItems1.size(); i++) {
			WarehouseItem warehouseItem1 = warehouseItems1.get(i);
			WarehouseItem warehouseItem2 = warehouseItems2.get(i);

			assertEquals(warehouseItem1, warehouseItem2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WarehouseItem> warehouseItems1,
		List<WarehouseItem> warehouseItems2) {

		Assert.assertEquals(warehouseItems1.size(), warehouseItems2.size());

		for (WarehouseItem warehouseItem1 : warehouseItems1) {
			boolean contains = false;

			for (WarehouseItem warehouseItem2 : warehouseItems2) {
				if (equals(warehouseItem1, warehouseItem2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				warehouseItems2 + " does not contain " + warehouseItem1,
				contains);
		}
	}

	protected void assertValid(WarehouseItem warehouseItem) throws Exception {
		boolean valid = true;

		if (warehouseItem.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (warehouseItem.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (warehouseItem.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (warehouseItem.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("reservedQuantity", additionalAssertFieldName)) {
				if (warehouseItem.getReservedQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (warehouseItem.getSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (warehouseItem.getUnitOfMeasureKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (warehouseItem.getWarehouseExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (warehouseItem.getWarehouseId() == null) {
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

	protected void assertValid(Page<WarehouseItem> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WarehouseItem> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WarehouseItem> warehouseItems = page.getItems();

		int size = warehouseItems.size();

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
						WarehouseItem.class)) {

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
		WarehouseItem warehouseItem1, WarehouseItem warehouseItem2) {

		if (warehouseItem1 == warehouseItem2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseItem1.getExternalReferenceCode(),
						warehouseItem2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseItem1.getId(), warehouseItem2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseItem1.getModifiedDate(),
						warehouseItem2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseItem1.getQuantity(),
						warehouseItem2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("reservedQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseItem1.getReservedQuantity(),
						warehouseItem2.getReservedQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseItem1.getSku(), warehouseItem2.getSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseItem1.getUnitOfMeasureKey(),
						warehouseItem2.getUnitOfMeasureKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseItem1.getWarehouseExternalReferenceCode(),
						warehouseItem2.getWarehouseExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseItem1.getWarehouseId(),
						warehouseItem2.getWarehouseId())) {

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

		if (!(_warehouseItemResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_warehouseItemResource;

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
		EntityField entityField, String operator, WarehouseItem warehouseItem) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = warehouseItem.getExternalReferenceCode();

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
				Date date = warehouseItem.getModifiedDate();

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

				sb.append(_format.format(warehouseItem.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("reservedQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sku")) {
			Object object = warehouseItem.getSku();

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
			Object object = warehouseItem.getUnitOfMeasureKey();

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

		if (entityFieldName.equals("warehouseExternalReferenceCode")) {
			Object object = warehouseItem.getWarehouseExternalReferenceCode();

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

	protected WarehouseItem randomWarehouseItem() throws Exception {
		return new WarehouseItem() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				sku = StringUtil.toLowerCase(RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				warehouseExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				warehouseId = RandomTestUtil.randomLong();
			}
		};
	}

	protected WarehouseItem randomIrrelevantWarehouseItem() throws Exception {
		WarehouseItem randomIrrelevantWarehouseItem = randomWarehouseItem();

		return randomIrrelevantWarehouseItem;
	}

	protected WarehouseItem randomPatchWarehouseItem() throws Exception {
		return randomWarehouseItem();
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

	protected WarehouseItemResource warehouseItemResource;
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
		LogFactoryUtil.getLog(BaseWarehouseItemResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.inventory.resource.v1_0.
		WarehouseItemResource _warehouseItemResource;

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