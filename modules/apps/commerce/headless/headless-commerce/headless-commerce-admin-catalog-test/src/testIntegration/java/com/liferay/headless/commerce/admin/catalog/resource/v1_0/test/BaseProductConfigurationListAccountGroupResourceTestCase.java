/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListAccountGroup;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductConfigurationListAccountGroupResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationListAccountGroupSerDes;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseProductConfigurationListAccountGroupResourceTestCase {

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

		_productConfigurationListAccountGroupResource.setContextCompany(
			testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productConfigurationListAccountGroupResource =
			ProductConfigurationListAccountGroupResource.builder(
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

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				randomProductConfigurationListAccountGroup();

		String json = objectMapper.writeValueAsString(
			productConfigurationListAccountGroup1);

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				ProductConfigurationListAccountGroupSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				productConfigurationListAccountGroup1,
				productConfigurationListAccountGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup =
				randomProductConfigurationListAccountGroup();

		String json1 = objectMapper.writeValueAsString(
			productConfigurationListAccountGroup);
		String json2 = ProductConfigurationListAccountGroupSerDes.toJSON(
			productConfigurationListAccountGroup);

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

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup =
				randomProductConfigurationListAccountGroup();

		productConfigurationListAccountGroup.
			setAccountGroupExternalReferenceCode(regex);
		productConfigurationListAccountGroup.
			setProductConfigurationListExternalReferenceCode(regex);

		String json = ProductConfigurationListAccountGroupSerDes.toJSON(
			productConfigurationListAccountGroup);

		Assert.assertFalse(json.contains(regex));

		productConfigurationListAccountGroup =
			ProductConfigurationListAccountGroupSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			productConfigurationListAccountGroup.
				getAccountGroupExternalReferenceCode());
		Assert.assertEquals(
			regex,
			productConfigurationListAccountGroup.
				getProductConfigurationListExternalReferenceCode());
	}

	@Test
	public void testDeleteProductConfigurationListAccountGroup()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup =
				testDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup();

		assertHttpResponseStatusCode(
			204,
			productConfigurationListAccountGroupResource.
				deleteProductConfigurationListAccountGroupHttpResponse(
					productConfigurationListAccountGroup.
						getProductConfigurationListAccountGroupId()));
	}

	protected ProductConfigurationListAccountGroup
			testDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductConfigurationListAccountGroup()
		throws Exception {

		// No namespace

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testGraphQLDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfigurationListAccountGroup",
						new HashMap<String, Object>() {
							{
								put(
									"productConfigurationListAccountGroupId",
									productConfigurationListAccountGroup1.
										getProductConfigurationListAccountGroupId());
							}
						})),
				"JSONObject/data",
				"Object/deleteProductConfigurationListAccountGroup"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				testGraphQLDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfigurationListAccountGroup",
							new HashMap<String, Object>() {
								{
									put(
										"productConfigurationListAccountGroupId",
										productConfigurationListAccountGroup2.
											getProductConfigurationListAccountGroupId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfigurationListAccountGroup"));
	}

	protected ProductConfigurationListAccountGroup
			testGraphQLDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup()
		throws Exception {

		return testGraphQLProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup();
	}

	@Test
	public void testDeleteProductConfigurationListAccountGroupBatch()
		throws Exception {

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testDeleteProductConfigurationListAccountGroupBatch_addProductConfigurationListAccountGroup();

		testDeleteProductConfigurationListAccountGroupBatch_deleteProductConfigurationListAccountGroup(
			202, null,
			productConfigurationListAccountGroup1.
				getProductConfigurationListAccountGroupId());
	}

	protected ProductConfigurationListAccountGroup
			testDeleteProductConfigurationListAccountGroupBatch_addProductConfigurationListAccountGroup()
		throws Exception {

		return testDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup();
	}

	protected void
			testDeleteProductConfigurationListAccountGroupBatch_deleteProductConfigurationListAccountGroup(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productConfigurationListAccountGroupResource.
				deleteProductConfigurationListAccountGroupBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"productConfigurationListAccountGroupId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getIrrelevantExternalReferenceCode();

		Page<ProductConfigurationListAccountGroup> page =
			productConfigurationListAccountGroupResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductConfigurationListAccountGroup
				irrelevantProductConfigurationListAccountGroup =
					testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
						irrelevantExternalReferenceCode,
						randomIrrelevantProductConfigurationListAccountGroup());

			page =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfigurationListAccountGroup,
				(List<ProductConfigurationListAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					externalReferenceCode,
					randomProductConfigurationListAccountGroup());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					externalReferenceCode,
					randomProductConfigurationListAccountGroup());

		page =
			productConfigurationListAccountGroupResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfigurationListAccountGroup1,
			(List<ProductConfigurationListAccountGroup>)page.getItems());
		assertContains(
			productConfigurationListAccountGroup2,
			(List<ProductConfigurationListAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getExpectedActions(
				externalReferenceCode));

		productConfigurationListAccountGroupResource.
			deleteProductConfigurationListAccountGroup(
				productConfigurationListAccountGroup1.
					getProductConfigurationListAccountGroupId());

		productConfigurationListAccountGroupResource.
			deleteProductConfigurationListAccountGroup(
				productConfigurationListAccountGroup2.
					getProductConfigurationListAccountGroupId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getExternalReferenceCode();

		Page<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroupsPage =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationListAccountGroupsPage.getTotalCount());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					externalReferenceCode,
					randomProductConfigurationListAccountGroup());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					externalReferenceCode,
					randomProductConfigurationListAccountGroup());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup3 =
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					externalReferenceCode,
					randomProductConfigurationListAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfigurationListAccountGroup> page1 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfigurationListAccountGroup1,
				(List<ProductConfigurationListAccountGroup>)page1.getItems());

			Page<ProductConfigurationListAccountGroup> page2 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productConfigurationListAccountGroup2,
				(List<ProductConfigurationListAccountGroup>)page2.getItems());

			Page<ProductConfigurationListAccountGroup> page3 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productConfigurationListAccountGroup3,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
		}
		else {
			Page<ProductConfigurationListAccountGroup> page1 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ProductConfigurationListAccountGroup>
				productConfigurationListAccountGroups1 =
					(List<ProductConfigurationListAccountGroup>)
						page1.getItems();

			Assert.assertEquals(
				productConfigurationListAccountGroups1.toString(),
				totalCount + 2, productConfigurationListAccountGroups1.size());

			Page<ProductConfigurationListAccountGroup> page2 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfigurationListAccountGroup>
				productConfigurationListAccountGroups2 =
					(List<ProductConfigurationListAccountGroup>)
						page2.getItems();

			Assert.assertEquals(
				productConfigurationListAccountGroups2.toString(), 1,
				productConfigurationListAccountGroups2.size());

			Page<ProductConfigurationListAccountGroup> page3 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productConfigurationListAccountGroup1,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
			assertContains(
				productConfigurationListAccountGroup2,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
			assertContains(
				productConfigurationListAccountGroup3,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
		}
	}

	protected ProductConfigurationListAccountGroup
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
				String externalReferenceCode,
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getId();
		Long irrelevantId =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getIrrelevantId();

		Page<ProductConfigurationListAccountGroup> page =
			productConfigurationListAccountGroupResource.
				getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductConfigurationListAccountGroup
				irrelevantProductConfigurationListAccountGroup =
					testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
						irrelevantId,
						randomIrrelevantProductConfigurationListAccountGroup());

			page =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfigurationListAccountGroup,
				(List<ProductConfigurationListAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getExpectedActions(
					irrelevantId));
		}

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					id, randomProductConfigurationListAccountGroup());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					id, randomProductConfigurationListAccountGroup());

		page =
			productConfigurationListAccountGroupResource.
				getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfigurationListAccountGroup1,
			(List<ProductConfigurationListAccountGroup>)page.getItems());
		assertContains(
			productConfigurationListAccountGroup2,
			(List<ProductConfigurationListAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getExpectedActions(
				id));

		productConfigurationListAccountGroupResource.
			deleteProductConfigurationListAccountGroup(
				productConfigurationListAccountGroup1.
					getProductConfigurationListAccountGroupId());

		productConfigurationListAccountGroupResource.
			deleteProductConfigurationListAccountGroup(
				productConfigurationListAccountGroup2.
					getProductConfigurationListAccountGroupId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getId();

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				randomProductConfigurationListAccountGroup();

		productConfigurationListAccountGroup1 =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
				id, productConfigurationListAccountGroup1);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListAccountGroup> page =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null,
						getFilterString(
							entityField, "between",
							productConfigurationListAccountGroup1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(
					productConfigurationListAccountGroup1),
				(List<ProductConfigurationListAccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilterDoubleEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilterStringContains()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilterStringEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getId();

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					id, randomProductConfigurationListAccountGroup());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					id, randomProductConfigurationListAccountGroup());

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListAccountGroup> page =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null,
						getFilterString(
							entityField, operator,
							productConfigurationListAccountGroup1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(
					productConfigurationListAccountGroup1),
				(List<ProductConfigurationListAccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithPagination()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getId();

		Page<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroupsPage =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationListAccountGroupsPage.getTotalCount());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					id, randomProductConfigurationListAccountGroup());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					id, randomProductConfigurationListAccountGroup());

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup3 =
				testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
					id, randomProductConfigurationListAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfigurationListAccountGroup> page1 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfigurationListAccountGroup1,
				(List<ProductConfigurationListAccountGroup>)page1.getItems());

			Page<ProductConfigurationListAccountGroup> page2 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationListAccountGroup2,
				(List<ProductConfigurationListAccountGroup>)page2.getItems());

			Page<ProductConfigurationListAccountGroup> page3 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationListAccountGroup3,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
		}
		else {
			Page<ProductConfigurationListAccountGroup> page1 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<ProductConfigurationListAccountGroup>
				productConfigurationListAccountGroups1 =
					(List<ProductConfigurationListAccountGroup>)
						page1.getItems();

			Assert.assertEquals(
				productConfigurationListAccountGroups1.toString(),
				totalCount + 2, productConfigurationListAccountGroups1.size());

			Page<ProductConfigurationListAccountGroup> page2 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfigurationListAccountGroup>
				productConfigurationListAccountGroups2 =
					(List<ProductConfigurationListAccountGroup>)
						page2.getItems();

			Assert.assertEquals(
				productConfigurationListAccountGroups2.toString(), 1,
				productConfigurationListAccountGroups2.size());

			Page<ProductConfigurationListAccountGroup> page3 =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				productConfigurationListAccountGroup1,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
			assertContains(
				productConfigurationListAccountGroup2,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
			assertContains(
				productConfigurationListAccountGroup3,
				(List<ProductConfigurationListAccountGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSortDateTime()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productConfigurationListAccountGroup1,
			 productConfigurationListAccountGroup2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListAccountGroup1,
					entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSortDouble()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productConfigurationListAccountGroup1,
			 productConfigurationListAccountGroup2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListAccountGroup1,
					entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					productConfigurationListAccountGroup2,
					entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSortInteger()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productConfigurationListAccountGroup1,
			 productConfigurationListAccountGroup2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListAccountGroup1,
					entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productConfigurationListAccountGroup2,
					entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSortString()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productConfigurationListAccountGroup1,
			 productConfigurationListAccountGroup2) -> {

				Class<?> clazz =
					productConfigurationListAccountGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productConfigurationListAccountGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productConfigurationListAccountGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productConfigurationListAccountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productConfigurationListAccountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productConfigurationListAccountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productConfigurationListAccountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ProductConfigurationListAccountGroup,
					 ProductConfigurationListAccountGroup, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getId();

		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				randomProductConfigurationListAccountGroup();
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2 =
				randomProductConfigurationListAccountGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productConfigurationListAccountGroup1,
				productConfigurationListAccountGroup2);
		}

		productConfigurationListAccountGroup1 =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
				id, productConfigurationListAccountGroup1);

		productConfigurationListAccountGroup2 =
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
				id, productConfigurationListAccountGroup2);

		Page<ProductConfigurationListAccountGroup> page =
			productConfigurationListAccountGroupResource.
				getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListAccountGroup> ascPage =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productConfigurationListAccountGroup1,
				(List<ProductConfigurationListAccountGroup>)ascPage.getItems());
			assertContains(
				productConfigurationListAccountGroup2,
				(List<ProductConfigurationListAccountGroup>)ascPage.getItems());

			Page<ProductConfigurationListAccountGroup> descPage =
				productConfigurationListAccountGroupResource.
					getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productConfigurationListAccountGroup2,
				(List<ProductConfigurationListAccountGroup>)
					descPage.getItems());
			assertContains(
				productConfigurationListAccountGroup1,
				(List<ProductConfigurationListAccountGroup>)
					descPage.getItems());
		}
	}

	protected ProductConfigurationListAccountGroup
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_addProductConfigurationListAccountGroup(
				Long id,
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationListAccountGroupsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroup()
		throws Exception {

		ProductConfigurationListAccountGroup
			randomProductConfigurationListAccountGroup =
				randomProductConfigurationListAccountGroup();

		ProductConfigurationListAccountGroup
			postProductConfigurationListAccountGroup =
				testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup(
					randomProductConfigurationListAccountGroup);

		assertEquals(
			randomProductConfigurationListAccountGroup,
			postProductConfigurationListAccountGroup);
		assertValid(postProductConfigurationListAccountGroup);
	}

	protected ProductConfigurationListAccountGroup
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup(
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductConfigurationListIdProductConfigurationListAccountGroup()
		throws Exception {

		ProductConfigurationListAccountGroup
			randomProductConfigurationListAccountGroup =
				randomProductConfigurationListAccountGroup();

		ProductConfigurationListAccountGroup
			postProductConfigurationListAccountGroup =
				testPostProductConfigurationListIdProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup(
					randomProductConfigurationListAccountGroup);

		assertEquals(
			randomProductConfigurationListAccountGroup,
			postProductConfigurationListAccountGroup);
		assertValid(postProductConfigurationListAccountGroup);
	}

	protected ProductConfigurationListAccountGroup
			testPostProductConfigurationListIdProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup(
				ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1 =
				testBatchEngineDeleteImportTask_addProductConfigurationListAccountGroup();

		testBatchEngineDeleteImportTask_deleteProductConfigurationListAccountGroup(
			200, null,
			productConfigurationListAccountGroup1.
				getProductConfigurationListAccountGroupId());
	}

	protected ProductConfigurationListAccountGroup
			testBatchEngineDeleteImportTask_addProductConfigurationListAccountGroup()
		throws Exception {

		return testDeleteProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteProductConfigurationListAccountGroup(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListAccountGroup",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"productConfigurationListAccountGroupId", () -> id
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

	protected ProductConfigurationListAccountGroup
			testGraphQLProductConfigurationListAccountGroup_addProductConfigurationListAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup,
		List<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroups) {

		boolean contains = false;

		for (ProductConfigurationListAccountGroup item :
				productConfigurationListAccountGroups) {

			if (equals(productConfigurationListAccountGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productConfigurationListAccountGroups + " does not contain " +
				productConfigurationListAccountGroup,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1,
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2) {

		Assert.assertTrue(
			productConfigurationListAccountGroup1 + " does not equal " +
				productConfigurationListAccountGroup2,
			equals(
				productConfigurationListAccountGroup1,
				productConfigurationListAccountGroup2));
	}

	protected void assertEquals(
		List<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroups1,
		List<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroups2) {

		Assert.assertEquals(
			productConfigurationListAccountGroups1.size(),
			productConfigurationListAccountGroups2.size());

		for (int i = 0; i < productConfigurationListAccountGroups1.size();
			 i++) {

			ProductConfigurationListAccountGroup
				productConfigurationListAccountGroup1 =
					productConfigurationListAccountGroups1.get(i);
			ProductConfigurationListAccountGroup
				productConfigurationListAccountGroup2 =
					productConfigurationListAccountGroups2.get(i);

			assertEquals(
				productConfigurationListAccountGroup1,
				productConfigurationListAccountGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroups1,
		List<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroups2) {

		Assert.assertEquals(
			productConfigurationListAccountGroups1.size(),
			productConfigurationListAccountGroups2.size());

		for (ProductConfigurationListAccountGroup
				productConfigurationListAccountGroup1 :
					productConfigurationListAccountGroups1) {

			boolean contains = false;

			for (ProductConfigurationListAccountGroup
					productConfigurationListAccountGroup2 :
						productConfigurationListAccountGroups2) {

				if (equals(
						productConfigurationListAccountGroup1,
						productConfigurationListAccountGroup2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productConfigurationListAccountGroups2 + " does not contain " +
					productConfigurationListAccountGroup1,
				contains);
		}
	}

	protected void assertValid(
			ProductConfigurationListAccountGroup
				productConfigurationListAccountGroup)
		throws Exception {

		boolean valid = true;

		if (productConfigurationListAccountGroup.
				getProductConfigurationListAccountGroupId() == null) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroup", additionalAssertFieldName)) {
				if (productConfigurationListAccountGroup.getAccountGroup() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productConfigurationListAccountGroup.
						getAccountGroupExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (productConfigurationListAccountGroup.getAccountGroupId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (productConfigurationListAccountGroup.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListAccountGroupId",
					additionalAssertFieldName)) {

				if (productConfigurationListAccountGroup.
						getProductConfigurationListAccountGroupId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productConfigurationListAccountGroup.
						getProductConfigurationListExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListId", additionalAssertFieldName)) {

				if (productConfigurationListAccountGroup.
						getProductConfigurationListId() == null) {

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

	protected void assertValid(
		Page<ProductConfigurationListAccountGroup> page) {

		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductConfigurationListAccountGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductConfigurationListAccountGroup>
			productConfigurationListAccountGroups = page.getItems();

		int size = productConfigurationListAccountGroups.size();

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

		graphQLFields.add(
			new GraphQLField("productConfigurationListAccountGroupId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						ProductConfigurationListAccountGroup.class)) {

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
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup1,
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup2) {

		if (productConfigurationListAccountGroup1 ==
				productConfigurationListAccountGroup2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroup", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationListAccountGroup1.getAccountGroup(),
						productConfigurationListAccountGroup2.
							getAccountGroup())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccountGroup1.
							getAccountGroupExternalReferenceCode(),
						productConfigurationListAccountGroup2.
							getAccountGroupExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationListAccountGroup1.
							getAccountGroupId(),
						productConfigurationListAccountGroup2.
							getAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)productConfigurationListAccountGroup1.getActions(),
						(Map)
							productConfigurationListAccountGroup2.
								getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListAccountGroupId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccountGroup1.
							getProductConfigurationListAccountGroupId(),
						productConfigurationListAccountGroup2.
							getProductConfigurationListAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccountGroup1.
							getProductConfigurationListExternalReferenceCode(),
						productConfigurationListAccountGroup2.
							getProductConfigurationListExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccountGroup1.
							getProductConfigurationListId(),
						productConfigurationListAccountGroup2.
							getProductConfigurationListId())) {

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

		if (!(_productConfigurationListAccountGroupResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productConfigurationListAccountGroupResource;

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
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup) {

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
				productConfigurationListAccountGroup.
					getAccountGroupExternalReferenceCode();

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

		if (entityFieldName.equals("productConfigurationListAccountGroupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"productConfigurationListExternalReferenceCode")) {

			Object object =
				productConfigurationListAccountGroup.
					getProductConfigurationListExternalReferenceCode();

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

		if (entityFieldName.equals("productConfigurationListId")) {
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

	protected ProductConfigurationListAccountGroup
			randomProductConfigurationListAccountGroup()
		throws Exception {

		return new ProductConfigurationListAccountGroup() {
			{
				accountGroupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountGroupId = RandomTestUtil.randomLong();
				productConfigurationListAccountGroupId =
					RandomTestUtil.randomLong();
				productConfigurationListExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				productConfigurationListId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ProductConfigurationListAccountGroup
			randomIrrelevantProductConfigurationListAccountGroup()
		throws Exception {

		ProductConfigurationListAccountGroup
			randomIrrelevantProductConfigurationListAccountGroup =
				randomProductConfigurationListAccountGroup();

		return randomIrrelevantProductConfigurationListAccountGroup;
	}

	protected ProductConfigurationListAccountGroup
			randomPatchProductConfigurationListAccountGroup()
		throws Exception {

		return randomProductConfigurationListAccountGroup();
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

	protected ProductConfigurationListAccountGroupResource
		productConfigurationListAccountGroupResource;
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
		LogFactoryUtil.getLog(
			BaseProductConfigurationListAccountGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductConfigurationListAccountGroupResource
			_productConfigurationListAccountGroupResource;

}