/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseAccountGroup;
import com.liferay.headless.commerce.admin.inventory.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.inventory.client.resource.v1_0.WarehouseAccountGroupResource;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.WarehouseAccountGroupSerDes;
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
public abstract class BaseWarehouseAccountGroupResourceTestCase {

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

		_warehouseAccountGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		warehouseAccountGroupResource = WarehouseAccountGroupResource.builder(
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

		WarehouseAccountGroup warehouseAccountGroup1 =
			randomWarehouseAccountGroup();

		String json = objectMapper.writeValueAsString(warehouseAccountGroup1);

		WarehouseAccountGroup warehouseAccountGroup2 =
			WarehouseAccountGroupSerDes.toDTO(json);

		Assert.assertTrue(
			equals(warehouseAccountGroup1, warehouseAccountGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WarehouseAccountGroup warehouseAccountGroup =
			randomWarehouseAccountGroup();

		String json1 = objectMapper.writeValueAsString(warehouseAccountGroup);
		String json2 = WarehouseAccountGroupSerDes.toJSON(
			warehouseAccountGroup);

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

		WarehouseAccountGroup warehouseAccountGroup =
			randomWarehouseAccountGroup();

		warehouseAccountGroup.setAccountGroupExternalReferenceCode(regex);
		warehouseAccountGroup.setWarehouseExternalReferenceCode(regex);

		String json = WarehouseAccountGroupSerDes.toJSON(warehouseAccountGroup);

		Assert.assertFalse(json.contains(regex));

		warehouseAccountGroup = WarehouseAccountGroupSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			warehouseAccountGroup.getAccountGroupExternalReferenceCode());
		Assert.assertEquals(
			regex, warehouseAccountGroup.getWarehouseExternalReferenceCode());
	}

	@Test
	public void testDeleteWarehouseAccountGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WarehouseAccountGroup warehouseAccountGroup =
			testDeleteWarehouseAccountGroup_addWarehouseAccountGroup();

		assertHttpResponseStatusCode(
			204,
			warehouseAccountGroupResource.
				deleteWarehouseAccountGroupHttpResponse(
					warehouseAccountGroup.getWarehouseAccountGroupId()));
	}

	protected WarehouseAccountGroup
			testDeleteWarehouseAccountGroup_addWarehouseAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWarehouseAccountGroup() throws Exception {

		// No namespace

		WarehouseAccountGroup warehouseAccountGroup1 =
			testGraphQLDeleteWarehouseAccountGroup_addWarehouseAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWarehouseAccountGroup",
						new HashMap<String, Object>() {
							{
								put(
									"warehouseAccountGroupId",
									warehouseAccountGroup1.
										getWarehouseAccountGroupId());
							}
						})),
				"JSONObject/data", "Object/deleteWarehouseAccountGroup"));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		WarehouseAccountGroup warehouseAccountGroup2 =
			testGraphQLDeleteWarehouseAccountGroup_addWarehouseAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteWarehouseAccountGroup",
							new HashMap<String, Object>() {
								{
									put(
										"warehouseAccountGroupId",
										warehouseAccountGroup2.
											getWarehouseAccountGroupId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteWarehouseAccountGroup"));
	}

	protected WarehouseAccountGroup
			testGraphQLDeleteWarehouseAccountGroup_addWarehouseAccountGroup()
		throws Exception {

		return testGraphQLWarehouseAccountGroup_addWarehouseAccountGroup();
	}

	@Test
	public void testDeleteWarehouseAccountGroupBatch() throws Exception {
		WarehouseAccountGroup warehouseAccountGroup1 =
			testDeleteWarehouseAccountGroupBatch_addWarehouseAccountGroup();

		testDeleteWarehouseAccountGroupBatch_deleteWarehouseAccountGroup(
			202, null, warehouseAccountGroup1.getWarehouseAccountGroupId());
	}

	protected WarehouseAccountGroup
			testDeleteWarehouseAccountGroupBatch_addWarehouseAccountGroup()
		throws Exception {

		return testDeleteWarehouseAccountGroup_addWarehouseAccountGroup();
	}

	protected void
			testDeleteWarehouseAccountGroupBatch_deleteWarehouseAccountGroup(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			warehouseAccountGroupResource.
				deleteWarehouseAccountGroupBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"warehouseAccountGroupId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage()
		throws Exception {

		String externalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getIrrelevantExternalReferenceCode();

		Page<WarehouseAccountGroup> page =
			warehouseAccountGroupResource.
				getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WarehouseAccountGroup irrelevantWarehouseAccountGroup =
				testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
					irrelevantExternalReferenceCode,
					randomIrrelevantWarehouseAccountGroup());

			page =
				warehouseAccountGroupResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWarehouseAccountGroup,
				(List<WarehouseAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WarehouseAccountGroup warehouseAccountGroup1 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				externalReferenceCode, randomWarehouseAccountGroup());

		WarehouseAccountGroup warehouseAccountGroup2 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				externalReferenceCode, randomWarehouseAccountGroup());

		page =
			warehouseAccountGroupResource.
				getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			warehouseAccountGroup1,
			(List<WarehouseAccountGroup>)page.getItems());
		assertContains(
			warehouseAccountGroup2,
			(List<WarehouseAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getExpectedActions(
				externalReferenceCode));

		warehouseAccountGroupResource.deleteWarehouseAccountGroup(
			warehouseAccountGroup1.getWarehouseAccountGroupId());

		warehouseAccountGroupResource.deleteWarehouseAccountGroup(
			warehouseAccountGroup2.getWarehouseAccountGroupId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getExternalReferenceCode();

		Page<WarehouseAccountGroup> warehouseAccountGroupsPage =
			warehouseAccountGroupResource.
				getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			warehouseAccountGroupsPage.getTotalCount());

		WarehouseAccountGroup warehouseAccountGroup1 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				externalReferenceCode, randomWarehouseAccountGroup());

		WarehouseAccountGroup warehouseAccountGroup2 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				externalReferenceCode, randomWarehouseAccountGroup());

		WarehouseAccountGroup warehouseAccountGroup3 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				externalReferenceCode, randomWarehouseAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WarehouseAccountGroup> page1 =
				warehouseAccountGroupResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				warehouseAccountGroup1,
				(List<WarehouseAccountGroup>)page1.getItems());

			Page<WarehouseAccountGroup> page2 =
				warehouseAccountGroupResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				warehouseAccountGroup2,
				(List<WarehouseAccountGroup>)page2.getItems());

			Page<WarehouseAccountGroup> page3 =
				warehouseAccountGroupResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				warehouseAccountGroup3,
				(List<WarehouseAccountGroup>)page3.getItems());
		}
		else {
			Page<WarehouseAccountGroup> page1 =
				warehouseAccountGroupResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<WarehouseAccountGroup> warehouseAccountGroups1 =
				(List<WarehouseAccountGroup>)page1.getItems();

			Assert.assertEquals(
				warehouseAccountGroups1.toString(), totalCount + 2,
				warehouseAccountGroups1.size());

			Page<WarehouseAccountGroup> page2 =
				warehouseAccountGroupResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WarehouseAccountGroup> warehouseAccountGroups2 =
				(List<WarehouseAccountGroup>)page2.getItems();

			Assert.assertEquals(
				warehouseAccountGroups2.toString(), 1,
				warehouseAccountGroups2.size());

			Page<WarehouseAccountGroup> page3 =
				warehouseAccountGroupResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				warehouseAccountGroup1,
				(List<WarehouseAccountGroup>)page3.getItems());
			assertContains(
				warehouseAccountGroup2,
				(List<WarehouseAccountGroup>)page3.getItems());
			assertContains(
				warehouseAccountGroup3,
				(List<WarehouseAccountGroup>)page3.getItems());
		}
	}

	protected WarehouseAccountGroup
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				String externalReferenceCode,
				WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPage()
		throws Exception {

		Long id = testGetWarehouseIdWarehouseAccountGroupsPage_getId();
		Long irrelevantId =
			testGetWarehouseIdWarehouseAccountGroupsPage_getIrrelevantId();

		Page<WarehouseAccountGroup> page =
			warehouseAccountGroupResource.
				getWarehouseIdWarehouseAccountGroupsPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			WarehouseAccountGroup irrelevantWarehouseAccountGroup =
				testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
					irrelevantId, randomIrrelevantWarehouseAccountGroup());

			page =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWarehouseAccountGroup,
				(List<WarehouseAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetWarehouseIdWarehouseAccountGroupsPage_getExpectedActions(
					irrelevantId));
		}

		WarehouseAccountGroup warehouseAccountGroup1 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, randomWarehouseAccountGroup());

		WarehouseAccountGroup warehouseAccountGroup2 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, randomWarehouseAccountGroup());

		page =
			warehouseAccountGroupResource.
				getWarehouseIdWarehouseAccountGroupsPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			warehouseAccountGroup1,
			(List<WarehouseAccountGroup>)page.getItems());
		assertContains(
			warehouseAccountGroup2,
			(List<WarehouseAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetWarehouseIdWarehouseAccountGroupsPage_getExpectedActions(
				id));

		warehouseAccountGroupResource.deleteWarehouseAccountGroup(
			warehouseAccountGroup1.getWarehouseAccountGroupId());

		warehouseAccountGroupResource.deleteWarehouseAccountGroup(
			warehouseAccountGroup2.getWarehouseAccountGroupId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseIdWarehouseAccountGroupsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetWarehouseIdWarehouseAccountGroupsPage_getId();

		WarehouseAccountGroup warehouseAccountGroup1 =
			randomWarehouseAccountGroup();

		warehouseAccountGroup1 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, warehouseAccountGroup1);

		for (EntityField entityField : entityFields) {
			Page<WarehouseAccountGroup> page =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null,
						getFilterString(
							entityField, "between", warehouseAccountGroup1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(warehouseAccountGroup1),
				(List<WarehouseAccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithFilterDoubleEquals()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithFilterStringContains()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithFilterStringEquals()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithFilterStringStartsWith()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetWarehouseIdWarehouseAccountGroupsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetWarehouseIdWarehouseAccountGroupsPage_getId();

		WarehouseAccountGroup warehouseAccountGroup1 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, randomWarehouseAccountGroup());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WarehouseAccountGroup warehouseAccountGroup2 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, randomWarehouseAccountGroup());

		for (EntityField entityField : entityFields) {
			Page<WarehouseAccountGroup> page =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null,
						getFilterString(
							entityField, operator, warehouseAccountGroup1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(warehouseAccountGroup1),
				(List<WarehouseAccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithPagination()
		throws Exception {

		Long id = testGetWarehouseIdWarehouseAccountGroupsPage_getId();

		Page<WarehouseAccountGroup> warehouseAccountGroupsPage =
			warehouseAccountGroupResource.
				getWarehouseIdWarehouseAccountGroupsPage(
					id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			warehouseAccountGroupsPage.getTotalCount());

		WarehouseAccountGroup warehouseAccountGroup1 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, randomWarehouseAccountGroup());

		WarehouseAccountGroup warehouseAccountGroup2 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, randomWarehouseAccountGroup());

		WarehouseAccountGroup warehouseAccountGroup3 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, randomWarehouseAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WarehouseAccountGroup> page1 =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				warehouseAccountGroup1,
				(List<WarehouseAccountGroup>)page1.getItems());

			Page<WarehouseAccountGroup> page2 =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				warehouseAccountGroup2,
				(List<WarehouseAccountGroup>)page2.getItems());

			Page<WarehouseAccountGroup> page3 =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				warehouseAccountGroup3,
				(List<WarehouseAccountGroup>)page3.getItems());
		}
		else {
			Page<WarehouseAccountGroup> page1 =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<WarehouseAccountGroup> warehouseAccountGroups1 =
				(List<WarehouseAccountGroup>)page1.getItems();

			Assert.assertEquals(
				warehouseAccountGroups1.toString(), totalCount + 2,
				warehouseAccountGroups1.size());

			Page<WarehouseAccountGroup> page2 =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WarehouseAccountGroup> warehouseAccountGroups2 =
				(List<WarehouseAccountGroup>)page2.getItems();

			Assert.assertEquals(
				warehouseAccountGroups2.toString(), 1,
				warehouseAccountGroups2.size());

			Page<WarehouseAccountGroup> page3 =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				warehouseAccountGroup1,
				(List<WarehouseAccountGroup>)page3.getItems());
			assertContains(
				warehouseAccountGroup2,
				(List<WarehouseAccountGroup>)page3.getItems());
			assertContains(
				warehouseAccountGroup3,
				(List<WarehouseAccountGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithSortDateTime()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, warehouseAccountGroup1, warehouseAccountGroup2) -> {
				BeanTestUtil.setProperty(
					warehouseAccountGroup1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithSortDouble()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, warehouseAccountGroup1, warehouseAccountGroup2) -> {
				BeanTestUtil.setProperty(
					warehouseAccountGroup1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					warehouseAccountGroup2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithSortInteger()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, warehouseAccountGroup1, warehouseAccountGroup2) -> {
				BeanTestUtil.setProperty(
					warehouseAccountGroup1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					warehouseAccountGroup2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountGroupsPageWithSortString()
		throws Exception {

		testGetWarehouseIdWarehouseAccountGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, warehouseAccountGroup1, warehouseAccountGroup2) -> {
				Class<?> clazz = warehouseAccountGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						warehouseAccountGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						warehouseAccountGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						warehouseAccountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						warehouseAccountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						warehouseAccountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						warehouseAccountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWarehouseIdWarehouseAccountGroupsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, WarehouseAccountGroup, WarehouseAccountGroup,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetWarehouseIdWarehouseAccountGroupsPage_getId();

		WarehouseAccountGroup warehouseAccountGroup1 =
			randomWarehouseAccountGroup();
		WarehouseAccountGroup warehouseAccountGroup2 =
			randomWarehouseAccountGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, warehouseAccountGroup1, warehouseAccountGroup2);
		}

		warehouseAccountGroup1 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, warehouseAccountGroup1);

		warehouseAccountGroup2 =
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				id, warehouseAccountGroup2);

		Page<WarehouseAccountGroup> page =
			warehouseAccountGroupResource.
				getWarehouseIdWarehouseAccountGroupsPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<WarehouseAccountGroup> ascPage =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				warehouseAccountGroup1,
				(List<WarehouseAccountGroup>)ascPage.getItems());
			assertContains(
				warehouseAccountGroup2,
				(List<WarehouseAccountGroup>)ascPage.getItems());

			Page<WarehouseAccountGroup> descPage =
				warehouseAccountGroupResource.
					getWarehouseIdWarehouseAccountGroupsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				warehouseAccountGroup2,
				(List<WarehouseAccountGroup>)descPage.getItems());
			assertContains(
				warehouseAccountGroup1,
				(List<WarehouseAccountGroup>)descPage.getItems());
		}
	}

	protected WarehouseAccountGroup
			testGetWarehouseIdWarehouseAccountGroupsPage_addWarehouseAccountGroup(
				Long id, WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWarehouseIdWarehouseAccountGroupsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWarehouseIdWarehouseAccountGroupsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostWarehouseByExternalReferenceCodeWarehouseAccountGroup()
		throws Exception {

		WarehouseAccountGroup randomWarehouseAccountGroup =
			randomWarehouseAccountGroup();

		WarehouseAccountGroup postWarehouseAccountGroup =
			testPostWarehouseByExternalReferenceCodeWarehouseAccountGroup_addWarehouseAccountGroup(
				randomWarehouseAccountGroup);

		assertEquals(randomWarehouseAccountGroup, postWarehouseAccountGroup);
		assertValid(postWarehouseAccountGroup);
	}

	protected WarehouseAccountGroup
			testPostWarehouseByExternalReferenceCodeWarehouseAccountGroup_addWarehouseAccountGroup(
				WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWarehouseIdWarehouseAccountGroup() throws Exception {
		WarehouseAccountGroup randomWarehouseAccountGroup =
			randomWarehouseAccountGroup();

		WarehouseAccountGroup postWarehouseAccountGroup =
			testPostWarehouseIdWarehouseAccountGroup_addWarehouseAccountGroup(
				randomWarehouseAccountGroup);

		assertEquals(randomWarehouseAccountGroup, postWarehouseAccountGroup);
		assertValid(postWarehouseAccountGroup);
	}

	protected WarehouseAccountGroup
			testPostWarehouseIdWarehouseAccountGroup_addWarehouseAccountGroup(
				WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WarehouseAccountGroup warehouseAccountGroup1 =
			testBatchEngineDeleteImportTask_addWarehouseAccountGroup();

		testBatchEngineDeleteImportTask_deleteWarehouseAccountGroup(
			200, null, warehouseAccountGroup1.getWarehouseAccountGroupId());
	}

	protected WarehouseAccountGroup
			testBatchEngineDeleteImportTask_addWarehouseAccountGroup()
		throws Exception {

		return testDeleteWarehouseAccountGroup_addWarehouseAccountGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteWarehouseAccountGroup(
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
				"com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccountGroup",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"warehouseAccountGroupId", () -> id
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

	protected WarehouseAccountGroup
			testGraphQLWarehouseAccountGroup_addWarehouseAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		WarehouseAccountGroup warehouseAccountGroup,
		List<WarehouseAccountGroup> warehouseAccountGroups) {

		boolean contains = false;

		for (WarehouseAccountGroup item : warehouseAccountGroups) {
			if (equals(warehouseAccountGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			warehouseAccountGroups + " does not contain " +
				warehouseAccountGroup,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WarehouseAccountGroup warehouseAccountGroup1,
		WarehouseAccountGroup warehouseAccountGroup2) {

		Assert.assertTrue(
			warehouseAccountGroup1 + " does not equal " +
				warehouseAccountGroup2,
			equals(warehouseAccountGroup1, warehouseAccountGroup2));
	}

	protected void assertEquals(
		List<WarehouseAccountGroup> warehouseAccountGroups1,
		List<WarehouseAccountGroup> warehouseAccountGroups2) {

		Assert.assertEquals(
			warehouseAccountGroups1.size(), warehouseAccountGroups2.size());

		for (int i = 0; i < warehouseAccountGroups1.size(); i++) {
			WarehouseAccountGroup warehouseAccountGroup1 =
				warehouseAccountGroups1.get(i);
			WarehouseAccountGroup warehouseAccountGroup2 =
				warehouseAccountGroups2.get(i);

			assertEquals(warehouseAccountGroup1, warehouseAccountGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WarehouseAccountGroup> warehouseAccountGroups1,
		List<WarehouseAccountGroup> warehouseAccountGroups2) {

		Assert.assertEquals(
			warehouseAccountGroups1.size(), warehouseAccountGroups2.size());

		for (WarehouseAccountGroup warehouseAccountGroup1 :
				warehouseAccountGroups1) {

			boolean contains = false;

			for (WarehouseAccountGroup warehouseAccountGroup2 :
					warehouseAccountGroups2) {

				if (equals(warehouseAccountGroup1, warehouseAccountGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				warehouseAccountGroups2 + " does not contain " +
					warehouseAccountGroup1,
				contains);
		}
	}

	protected void assertValid(WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		boolean valid = true;

		if (warehouseAccountGroup.getWarehouseAccountGroupId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroup", additionalAssertFieldName)) {
				if (warehouseAccountGroup.getAccountGroup() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (warehouseAccountGroup.
						getAccountGroupExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (warehouseAccountGroup.getAccountGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (warehouseAccountGroup.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseAccountGroupId", additionalAssertFieldName)) {

				if (warehouseAccountGroup.getWarehouseAccountGroupId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (warehouseAccountGroup.getWarehouseExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (warehouseAccountGroup.getWarehouseId() == null) {
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

	protected void assertValid(Page<WarehouseAccountGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WarehouseAccountGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WarehouseAccountGroup> warehouseAccountGroups =
			page.getItems();

		int size = warehouseAccountGroups.size();

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

		graphQLFields.add(new GraphQLField("warehouseAccountGroupId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.inventory.dto.v1_0.
						WarehouseAccountGroup.class)) {

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
		WarehouseAccountGroup warehouseAccountGroup1,
		WarehouseAccountGroup warehouseAccountGroup2) {

		if (warehouseAccountGroup1 == warehouseAccountGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroup", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseAccountGroup1.getAccountGroup(),
						warehouseAccountGroup2.getAccountGroup())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseAccountGroup1.
							getAccountGroupExternalReferenceCode(),
						warehouseAccountGroup2.
							getAccountGroupExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseAccountGroup1.getAccountGroupId(),
						warehouseAccountGroup2.getAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)warehouseAccountGroup1.getActions(),
						(Map)warehouseAccountGroup2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseAccountGroupId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseAccountGroup1.getWarehouseAccountGroupId(),
						warehouseAccountGroup2.getWarehouseAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseAccountGroup1.
							getWarehouseExternalReferenceCode(),
						warehouseAccountGroup2.
							getWarehouseExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseAccountGroup1.getWarehouseId(),
						warehouseAccountGroup2.getWarehouseId())) {

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

		if (!(_warehouseAccountGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_warehouseAccountGroupResource;

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
		WarehouseAccountGroup warehouseAccountGroup) {

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
				warehouseAccountGroup.getAccountGroupExternalReferenceCode();

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

		if (entityFieldName.equals("warehouseAccountGroupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("warehouseExternalReferenceCode")) {
			Object object =
				warehouseAccountGroup.getWarehouseExternalReferenceCode();

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

	protected WarehouseAccountGroup randomWarehouseAccountGroup()
		throws Exception {

		return new WarehouseAccountGroup() {
			{
				accountGroupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountGroupId = RandomTestUtil.randomLong();
				warehouseAccountGroupId = RandomTestUtil.randomLong();
				warehouseExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				warehouseId = RandomTestUtil.randomLong();
			}
		};
	}

	protected WarehouseAccountGroup randomIrrelevantWarehouseAccountGroup()
		throws Exception {

		WarehouseAccountGroup randomIrrelevantWarehouseAccountGroup =
			randomWarehouseAccountGroup();

		return randomIrrelevantWarehouseAccountGroup;
	}

	protected WarehouseAccountGroup randomPatchWarehouseAccountGroup()
		throws Exception {

		return randomWarehouseAccountGroup();
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

	protected WarehouseAccountGroupResource warehouseAccountGroupResource;
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
		LogFactoryUtil.getLog(BaseWarehouseAccountGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.inventory.resource.v1_0.
		WarehouseAccountGroupResource _warehouseAccountGroupResource;

}