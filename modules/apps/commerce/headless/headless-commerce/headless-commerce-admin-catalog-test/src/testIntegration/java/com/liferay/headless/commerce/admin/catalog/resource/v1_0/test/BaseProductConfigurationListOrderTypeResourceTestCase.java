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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListOrderType;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductConfigurationListOrderTypeResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationListOrderTypeSerDes;
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
public abstract class BaseProductConfigurationListOrderTypeResourceTestCase {

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

		_productConfigurationListOrderTypeResource.setContextCompany(
			testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productConfigurationListOrderTypeResource =
			ProductConfigurationListOrderTypeResource.builder(
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

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			randomProductConfigurationListOrderType();

		String json = objectMapper.writeValueAsString(
			productConfigurationListOrderType1);

		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			ProductConfigurationListOrderTypeSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				productConfigurationListOrderType1,
				productConfigurationListOrderType2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductConfigurationListOrderType productConfigurationListOrderType =
			randomProductConfigurationListOrderType();

		String json1 = objectMapper.writeValueAsString(
			productConfigurationListOrderType);
		String json2 = ProductConfigurationListOrderTypeSerDes.toJSON(
			productConfigurationListOrderType);

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

		ProductConfigurationListOrderType productConfigurationListOrderType =
			randomProductConfigurationListOrderType();

		productConfigurationListOrderType.setOrderTypeExternalReferenceCode(
			regex);
		productConfigurationListOrderType.
			setProductConfigurationListExternalReferenceCode(regex);

		String json = ProductConfigurationListOrderTypeSerDes.toJSON(
			productConfigurationListOrderType);

		Assert.assertFalse(json.contains(regex));

		productConfigurationListOrderType =
			ProductConfigurationListOrderTypeSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			productConfigurationListOrderType.
				getOrderTypeExternalReferenceCode());
		Assert.assertEquals(
			regex,
			productConfigurationListOrderType.
				getProductConfigurationListExternalReferenceCode());
	}

	@Test
	public void testDeleteProductConfigurationListOrderType() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationListOrderType productConfigurationListOrderType =
			testDeleteProductConfigurationListOrderType_addProductConfigurationListOrderType();

		assertHttpResponseStatusCode(
			204,
			productConfigurationListOrderTypeResource.
				deleteProductConfigurationListOrderTypeHttpResponse(
					productConfigurationListOrderType.
						getProductConfigurationListOrderTypeId()));
	}

	protected ProductConfigurationListOrderType
			testDeleteProductConfigurationListOrderType_addProductConfigurationListOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductConfigurationListOrderType()
		throws Exception {

		// No namespace

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testGraphQLDeleteProductConfigurationListOrderType_addProductConfigurationListOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfigurationListOrderType",
						new HashMap<String, Object>() {
							{
								put(
									"productConfigurationListOrderTypeId",
									productConfigurationListOrderType1.
										getProductConfigurationListOrderTypeId());
							}
						})),
				"JSONObject/data",
				"Object/deleteProductConfigurationListOrderType"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			testGraphQLDeleteProductConfigurationListOrderType_addProductConfigurationListOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfigurationListOrderType",
							new HashMap<String, Object>() {
								{
									put(
										"productConfigurationListOrderTypeId",
										productConfigurationListOrderType2.
											getProductConfigurationListOrderTypeId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfigurationListOrderType"));
	}

	protected ProductConfigurationListOrderType
			testGraphQLDeleteProductConfigurationListOrderType_addProductConfigurationListOrderType()
		throws Exception {

		return testGraphQLProductConfigurationListOrderType_addProductConfigurationListOrderType();
	}

	@Test
	public void testDeleteProductConfigurationListOrderTypeBatch()
		throws Exception {

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testDeleteProductConfigurationListOrderTypeBatch_addProductConfigurationListOrderType();

		testDeleteProductConfigurationListOrderTypeBatch_deleteProductConfigurationListOrderType(
			202, null,
			productConfigurationListOrderType1.
				getProductConfigurationListOrderTypeId());
	}

	protected ProductConfigurationListOrderType
			testDeleteProductConfigurationListOrderTypeBatch_addProductConfigurationListOrderType()
		throws Exception {

		return testDeleteProductConfigurationListOrderType_addProductConfigurationListOrderType();
	}

	protected void
			testDeleteProductConfigurationListOrderTypeBatch_deleteProductConfigurationListOrderType(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productConfigurationListOrderTypeResource.
				deleteProductConfigurationListOrderTypeBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"productConfigurationListOrderTypeId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getIrrelevantExternalReferenceCode();

		Page<ProductConfigurationListOrderType> page =
			productConfigurationListOrderTypeResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductConfigurationListOrderType
				irrelevantProductConfigurationListOrderType =
					testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
						irrelevantExternalReferenceCode,
						randomIrrelevantProductConfigurationListOrderType());

			page =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfigurationListOrderType,
				(List<ProductConfigurationListOrderType>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				externalReferenceCode,
				randomProductConfigurationListOrderType());

		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				externalReferenceCode,
				randomProductConfigurationListOrderType());

		page =
			productConfigurationListOrderTypeResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfigurationListOrderType1,
			(List<ProductConfigurationListOrderType>)page.getItems());
		assertContains(
			productConfigurationListOrderType2,
			(List<ProductConfigurationListOrderType>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getExpectedActions(
				externalReferenceCode));

		productConfigurationListOrderTypeResource.
			deleteProductConfigurationListOrderType(
				productConfigurationListOrderType1.
					getProductConfigurationListOrderTypeId());

		productConfigurationListOrderTypeResource.
			deleteProductConfigurationListOrderType(
				productConfigurationListOrderType2.
					getProductConfigurationListOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getExternalReferenceCode();

		Page<ProductConfigurationListOrderType>
			productConfigurationListOrderTypesPage =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationListOrderTypesPage.getTotalCount());

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				externalReferenceCode,
				randomProductConfigurationListOrderType());

		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				externalReferenceCode,
				randomProductConfigurationListOrderType());

		ProductConfigurationListOrderType productConfigurationListOrderType3 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				externalReferenceCode,
				randomProductConfigurationListOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfigurationListOrderType> page1 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfigurationListOrderType1,
				(List<ProductConfigurationListOrderType>)page1.getItems());

			Page<ProductConfigurationListOrderType> page2 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productConfigurationListOrderType2,
				(List<ProductConfigurationListOrderType>)page2.getItems());

			Page<ProductConfigurationListOrderType> page3 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productConfigurationListOrderType3,
				(List<ProductConfigurationListOrderType>)page3.getItems());
		}
		else {
			Page<ProductConfigurationListOrderType> page1 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ProductConfigurationListOrderType>
				productConfigurationListOrderTypes1 =
					(List<ProductConfigurationListOrderType>)page1.getItems();

			Assert.assertEquals(
				productConfigurationListOrderTypes1.toString(), totalCount + 2,
				productConfigurationListOrderTypes1.size());

			Page<ProductConfigurationListOrderType> page2 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfigurationListOrderType>
				productConfigurationListOrderTypes2 =
					(List<ProductConfigurationListOrderType>)page2.getItems();

			Assert.assertEquals(
				productConfigurationListOrderTypes2.toString(), 1,
				productConfigurationListOrderTypes2.size());

			Page<ProductConfigurationListOrderType> page3 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productConfigurationListOrderType1,
				(List<ProductConfigurationListOrderType>)page3.getItems());
			assertContains(
				productConfigurationListOrderType2,
				(List<ProductConfigurationListOrderType>)page3.getItems());
			assertContains(
				productConfigurationListOrderType3,
				(List<ProductConfigurationListOrderType>)page3.getItems());
		}
	}

	protected ProductConfigurationListOrderType
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				String externalReferenceCode,
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPage()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getId();
		Long irrelevantId =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getIrrelevantId();

		Page<ProductConfigurationListOrderType> page =
			productConfigurationListOrderTypeResource.
				getProductConfigurationListIdProductConfigurationListOrderTypesPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductConfigurationListOrderType
				irrelevantProductConfigurationListOrderType =
					testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
						irrelevantId,
						randomIrrelevantProductConfigurationListOrderType());

			page =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfigurationListOrderType,
				(List<ProductConfigurationListOrderType>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getExpectedActions(
					irrelevantId));
		}

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, randomProductConfigurationListOrderType());

		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, randomProductConfigurationListOrderType());

		page =
			productConfigurationListOrderTypeResource.
				getProductConfigurationListIdProductConfigurationListOrderTypesPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfigurationListOrderType1,
			(List<ProductConfigurationListOrderType>)page.getItems());
		assertContains(
			productConfigurationListOrderType2,
			(List<ProductConfigurationListOrderType>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getExpectedActions(
				id));

		productConfigurationListOrderTypeResource.
			deleteProductConfigurationListOrderType(
				productConfigurationListOrderType1.
					getProductConfigurationListOrderTypeId());

		productConfigurationListOrderTypeResource.
			deleteProductConfigurationListOrderType(
				productConfigurationListOrderType2.
					getProductConfigurationListOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getId();

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			randomProductConfigurationListOrderType();

		productConfigurationListOrderType1 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, productConfigurationListOrderType1);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListOrderType> page =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null,
						getFilterString(
							entityField, "between",
							productConfigurationListOrderType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfigurationListOrderType1),
				(List<ProductConfigurationListOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilterDoubleEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilterStringContains()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilterStringEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getId();

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, randomProductConfigurationListOrderType());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, randomProductConfigurationListOrderType());

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListOrderType> page =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null,
						getFilterString(
							entityField, operator,
							productConfigurationListOrderType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfigurationListOrderType1),
				(List<ProductConfigurationListOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithPagination()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getId();

		Page<ProductConfigurationListOrderType>
			productConfigurationListOrderTypesPage =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationListOrderTypesPage.getTotalCount());

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, randomProductConfigurationListOrderType());

		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, randomProductConfigurationListOrderType());

		ProductConfigurationListOrderType productConfigurationListOrderType3 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, randomProductConfigurationListOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfigurationListOrderType> page1 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfigurationListOrderType1,
				(List<ProductConfigurationListOrderType>)page1.getItems());

			Page<ProductConfigurationListOrderType> page2 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationListOrderType2,
				(List<ProductConfigurationListOrderType>)page2.getItems());

			Page<ProductConfigurationListOrderType> page3 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationListOrderType3,
				(List<ProductConfigurationListOrderType>)page3.getItems());
		}
		else {
			Page<ProductConfigurationListOrderType> page1 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<ProductConfigurationListOrderType>
				productConfigurationListOrderTypes1 =
					(List<ProductConfigurationListOrderType>)page1.getItems();

			Assert.assertEquals(
				productConfigurationListOrderTypes1.toString(), totalCount + 2,
				productConfigurationListOrderTypes1.size());

			Page<ProductConfigurationListOrderType> page2 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfigurationListOrderType>
				productConfigurationListOrderTypes2 =
					(List<ProductConfigurationListOrderType>)page2.getItems();

			Assert.assertEquals(
				productConfigurationListOrderTypes2.toString(), 1,
				productConfigurationListOrderTypes2.size());

			Page<ProductConfigurationListOrderType> page3 =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				productConfigurationListOrderType1,
				(List<ProductConfigurationListOrderType>)page3.getItems());
			assertContains(
				productConfigurationListOrderType2,
				(List<ProductConfigurationListOrderType>)page3.getItems());
			assertContains(
				productConfigurationListOrderType3,
				(List<ProductConfigurationListOrderType>)page3.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSortDateTime()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productConfigurationListOrderType1,
			 productConfigurationListOrderType2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListOrderType1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSortDouble()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productConfigurationListOrderType1,
			 productConfigurationListOrderType2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListOrderType1, entityField.getName(),
					0.1);
				BeanTestUtil.setProperty(
					productConfigurationListOrderType2, entityField.getName(),
					0.5);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSortInteger()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productConfigurationListOrderType1,
			 productConfigurationListOrderType2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListOrderType1, entityField.getName(),
					0);
				BeanTestUtil.setProperty(
					productConfigurationListOrderType2, entityField.getName(),
					1);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSortString()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSort(
			EntityField.Type.STRING,
			(entityField, productConfigurationListOrderType1,
			 productConfigurationListOrderType2) -> {

				Class<?> clazz = productConfigurationListOrderType1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productConfigurationListOrderType1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productConfigurationListOrderType2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productConfigurationListOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productConfigurationListOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productConfigurationListOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productConfigurationListOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ProductConfigurationListOrderType,
					 ProductConfigurationListOrderType, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getId();

		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			randomProductConfigurationListOrderType();
		ProductConfigurationListOrderType productConfigurationListOrderType2 =
			randomProductConfigurationListOrderType();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productConfigurationListOrderType1,
				productConfigurationListOrderType2);
		}

		productConfigurationListOrderType1 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, productConfigurationListOrderType1);

		productConfigurationListOrderType2 =
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				id, productConfigurationListOrderType2);

		Page<ProductConfigurationListOrderType> page =
			productConfigurationListOrderTypeResource.
				getProductConfigurationListIdProductConfigurationListOrderTypesPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListOrderType> ascPage =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productConfigurationListOrderType1,
				(List<ProductConfigurationListOrderType>)ascPage.getItems());
			assertContains(
				productConfigurationListOrderType2,
				(List<ProductConfigurationListOrderType>)ascPage.getItems());

			Page<ProductConfigurationListOrderType> descPage =
				productConfigurationListOrderTypeResource.
					getProductConfigurationListIdProductConfigurationListOrderTypesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productConfigurationListOrderType2,
				(List<ProductConfigurationListOrderType>)descPage.getItems());
			assertContains(
				productConfigurationListOrderType1,
				(List<ProductConfigurationListOrderType>)descPage.getItems());
		}
	}

	protected ProductConfigurationListOrderType
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_addProductConfigurationListOrderType(
				Long id,
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationListOrderTypesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderType()
		throws Exception {

		ProductConfigurationListOrderType
			randomProductConfigurationListOrderType =
				randomProductConfigurationListOrderType();

		ProductConfigurationListOrderType
			postProductConfigurationListOrderType =
				testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderType_addProductConfigurationListOrderType(
					randomProductConfigurationListOrderType);

		assertEquals(
			randomProductConfigurationListOrderType,
			postProductConfigurationListOrderType);
		assertValid(postProductConfigurationListOrderType);
	}

	protected ProductConfigurationListOrderType
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderType_addProductConfigurationListOrderType(
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductConfigurationListIdProductConfigurationListOrderType()
		throws Exception {

		ProductConfigurationListOrderType
			randomProductConfigurationListOrderType =
				randomProductConfigurationListOrderType();

		ProductConfigurationListOrderType
			postProductConfigurationListOrderType =
				testPostProductConfigurationListIdProductConfigurationListOrderType_addProductConfigurationListOrderType(
					randomProductConfigurationListOrderType);

		assertEquals(
			randomProductConfigurationListOrderType,
			postProductConfigurationListOrderType);
		assertValid(postProductConfigurationListOrderType);
	}

	protected ProductConfigurationListOrderType
			testPostProductConfigurationListIdProductConfigurationListOrderType_addProductConfigurationListOrderType(
				ProductConfigurationListOrderType
					productConfigurationListOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductConfigurationListOrderType productConfigurationListOrderType1 =
			testBatchEngineDeleteImportTask_addProductConfigurationListOrderType();

		testBatchEngineDeleteImportTask_deleteProductConfigurationListOrderType(
			200, null,
			productConfigurationListOrderType1.
				getProductConfigurationListOrderTypeId());
	}

	protected ProductConfigurationListOrderType
			testBatchEngineDeleteImportTask_addProductConfigurationListOrderType()
		throws Exception {

		return testDeleteProductConfigurationListOrderType_addProductConfigurationListOrderType();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteProductConfigurationListOrderType(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListOrderType",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"productConfigurationListOrderTypeId", () -> id
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

	protected ProductConfigurationListOrderType
			testGraphQLProductConfigurationListOrderType_addProductConfigurationListOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductConfigurationListOrderType productConfigurationListOrderType,
		List<ProductConfigurationListOrderType>
			productConfigurationListOrderTypes) {

		boolean contains = false;

		for (ProductConfigurationListOrderType item :
				productConfigurationListOrderTypes) {

			if (equals(productConfigurationListOrderType, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productConfigurationListOrderTypes + " does not contain " +
				productConfigurationListOrderType,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductConfigurationListOrderType productConfigurationListOrderType1,
		ProductConfigurationListOrderType productConfigurationListOrderType2) {

		Assert.assertTrue(
			productConfigurationListOrderType1 + " does not equal " +
				productConfigurationListOrderType2,
			equals(
				productConfigurationListOrderType1,
				productConfigurationListOrderType2));
	}

	protected void assertEquals(
		List<ProductConfigurationListOrderType>
			productConfigurationListOrderTypes1,
		List<ProductConfigurationListOrderType>
			productConfigurationListOrderTypes2) {

		Assert.assertEquals(
			productConfigurationListOrderTypes1.size(),
			productConfigurationListOrderTypes2.size());

		for (int i = 0; i < productConfigurationListOrderTypes1.size(); i++) {
			ProductConfigurationListOrderType
				productConfigurationListOrderType1 =
					productConfigurationListOrderTypes1.get(i);
			ProductConfigurationListOrderType
				productConfigurationListOrderType2 =
					productConfigurationListOrderTypes2.get(i);

			assertEquals(
				productConfigurationListOrderType1,
				productConfigurationListOrderType2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductConfigurationListOrderType>
			productConfigurationListOrderTypes1,
		List<ProductConfigurationListOrderType>
			productConfigurationListOrderTypes2) {

		Assert.assertEquals(
			productConfigurationListOrderTypes1.size(),
			productConfigurationListOrderTypes2.size());

		for (ProductConfigurationListOrderType
				productConfigurationListOrderType1 :
					productConfigurationListOrderTypes1) {

			boolean contains = false;

			for (ProductConfigurationListOrderType
					productConfigurationListOrderType2 :
						productConfigurationListOrderTypes2) {

				if (equals(
						productConfigurationListOrderType1,
						productConfigurationListOrderType2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productConfigurationListOrderTypes2 + " does not contain " +
					productConfigurationListOrderType1,
				contains);
		}
	}

	protected void assertValid(
			ProductConfigurationListOrderType productConfigurationListOrderType)
		throws Exception {

		boolean valid = true;

		if (productConfigurationListOrderType.
				getProductConfigurationListOrderTypeId() == null) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (productConfigurationListOrderType.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (productConfigurationListOrderType.getOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productConfigurationListOrderType.
						getOrderTypeExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (productConfigurationListOrderType.getOrderTypeId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (productConfigurationListOrderType.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productConfigurationListOrderType.
						getProductConfigurationListExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListId", additionalAssertFieldName)) {

				if (productConfigurationListOrderType.
						getProductConfigurationListId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListOrderTypeId",
					additionalAssertFieldName)) {

				if (productConfigurationListOrderType.
						getProductConfigurationListOrderTypeId() == null) {

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

	protected void assertValid(Page<ProductConfigurationListOrderType> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductConfigurationListOrderType> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductConfigurationListOrderType>
			productConfigurationListOrderTypes = page.getItems();

		int size = productConfigurationListOrderTypes.size();

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
			new GraphQLField("productConfigurationListOrderTypeId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						ProductConfigurationListOrderType.class)) {

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
		ProductConfigurationListOrderType productConfigurationListOrderType1,
		ProductConfigurationListOrderType productConfigurationListOrderType2) {

		if (productConfigurationListOrderType1 ==
				productConfigurationListOrderType2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)productConfigurationListOrderType1.getActions(),
						(Map)productConfigurationListOrderType2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationListOrderType1.getOrderType(),
						productConfigurationListOrderType2.getOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListOrderType1.
							getOrderTypeExternalReferenceCode(),
						productConfigurationListOrderType2.
							getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationListOrderType1.getOrderTypeId(),
						productConfigurationListOrderType2.getOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationListOrderType1.getPriority(),
						productConfigurationListOrderType2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListOrderType1.
							getProductConfigurationListExternalReferenceCode(),
						productConfigurationListOrderType2.
							getProductConfigurationListExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListOrderType1.
							getProductConfigurationListId(),
						productConfigurationListOrderType2.
							getProductConfigurationListId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListOrderTypeId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListOrderType1.
							getProductConfigurationListOrderTypeId(),
						productConfigurationListOrderType2.
							getProductConfigurationListOrderTypeId())) {

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

		if (!(_productConfigurationListOrderTypeResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productConfigurationListOrderTypeResource;

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
		ProductConfigurationListOrderType productConfigurationListOrderType) {

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

		if (entityFieldName.equals("orderType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object =
				productConfigurationListOrderType.
					getOrderTypeExternalReferenceCode();

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

		if (entityFieldName.equals("priority")) {
			sb.append(
				String.valueOf(
					productConfigurationListOrderType.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals(
				"productConfigurationListExternalReferenceCode")) {

			Object object =
				productConfigurationListOrderType.
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

		if (entityFieldName.equals("productConfigurationListOrderTypeId")) {
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

	protected ProductConfigurationListOrderType
			randomProductConfigurationListOrderType()
		throws Exception {

		return new ProductConfigurationListOrderType() {
			{
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomInt();
				productConfigurationListExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				productConfigurationListId = RandomTestUtil.randomLong();
				productConfigurationListOrderTypeId =
					RandomTestUtil.randomLong();
			}
		};
	}

	protected ProductConfigurationListOrderType
			randomIrrelevantProductConfigurationListOrderType()
		throws Exception {

		ProductConfigurationListOrderType
			randomIrrelevantProductConfigurationListOrderType =
				randomProductConfigurationListOrderType();

		return randomIrrelevantProductConfigurationListOrderType;
	}

	protected ProductConfigurationListOrderType
			randomPatchProductConfigurationListOrderType()
		throws Exception {

		return randomProductConfigurationListOrderType();
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

	protected ProductConfigurationListOrderTypeResource
		productConfigurationListOrderTypeResource;
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
			BaseProductConfigurationListOrderTypeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductConfigurationListOrderTypeResource
			_productConfigurationListOrderTypeResource;

}