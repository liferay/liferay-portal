/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountOrderType;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.DiscountOrderTypeResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.DiscountOrderTypeSerDes;
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
public abstract class BaseDiscountOrderTypeResourceTestCase {

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

		_discountOrderTypeResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		discountOrderTypeResource = DiscountOrderTypeResource.builder(
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

		DiscountOrderType discountOrderType1 = randomDiscountOrderType();

		String json = objectMapper.writeValueAsString(discountOrderType1);

		DiscountOrderType discountOrderType2 = DiscountOrderTypeSerDes.toDTO(
			json);

		Assert.assertTrue(equals(discountOrderType1, discountOrderType2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DiscountOrderType discountOrderType = randomDiscountOrderType();

		String json1 = objectMapper.writeValueAsString(discountOrderType);
		String json2 = DiscountOrderTypeSerDes.toJSON(discountOrderType);

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

		DiscountOrderType discountOrderType = randomDiscountOrderType();

		discountOrderType.setDiscountExternalReferenceCode(regex);
		discountOrderType.setOrderTypeExternalReferenceCode(regex);

		String json = DiscountOrderTypeSerDes.toJSON(discountOrderType);

		Assert.assertFalse(json.contains(regex));

		discountOrderType = DiscountOrderTypeSerDes.toDTO(json);

		Assert.assertEquals(
			regex, discountOrderType.getDiscountExternalReferenceCode());
		Assert.assertEquals(
			regex, discountOrderType.getOrderTypeExternalReferenceCode());
	}

	@Test
	public void testDeleteDiscountOrderType() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DiscountOrderType discountOrderType =
			testDeleteDiscountOrderType_addDiscountOrderType();

		assertHttpResponseStatusCode(
			204,
			discountOrderTypeResource.deleteDiscountOrderTypeHttpResponse(
				discountOrderType.getDiscountOrderTypeId()));
	}

	protected DiscountOrderType
			testDeleteDiscountOrderType_addDiscountOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDiscountOrderType() throws Exception {

		// No namespace

		DiscountOrderType discountOrderType1 =
			testGraphQLDeleteDiscountOrderType_addDiscountOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDiscountOrderType",
						new HashMap<String, Object>() {
							{
								put(
									"discountOrderTypeId",
									discountOrderType1.
										getDiscountOrderTypeId());
							}
						})),
				"JSONObject/data", "Object/deleteDiscountOrderType"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		DiscountOrderType discountOrderType2 =
			testGraphQLDeleteDiscountOrderType_addDiscountOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deleteDiscountOrderType",
							new HashMap<String, Object>() {
								{
									put(
										"discountOrderTypeId",
										discountOrderType2.
											getDiscountOrderTypeId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deleteDiscountOrderType"));
	}

	protected DiscountOrderType
			testGraphQLDeleteDiscountOrderType_addDiscountOrderType()
		throws Exception {

		return testGraphQLDiscountOrderType_addDiscountOrderType();
	}

	@Test
	public void testDeleteDiscountOrderTypeBatch() throws Exception {
		DiscountOrderType discountOrderType1 =
			testDeleteDiscountOrderTypeBatch_addDiscountOrderType();

		testDeleteDiscountOrderTypeBatch_deleteDiscountOrderType(
			202, null, discountOrderType1.getDiscountOrderTypeId());
	}

	protected DiscountOrderType
			testDeleteDiscountOrderTypeBatch_addDiscountOrderType()
		throws Exception {

		return testDeleteDiscountOrderType_addDiscountOrderType();
	}

	protected void testDeleteDiscountOrderTypeBatch_deleteDiscountOrderType(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			discountOrderTypeResource.deleteDiscountOrderTypeBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"discountOrderTypeId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getIrrelevantExternalReferenceCode();

		Page<DiscountOrderType> page =
			discountOrderTypeResource.
				getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			DiscountOrderType irrelevantDiscountOrderType =
				testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
					irrelevantExternalReferenceCode,
					randomIrrelevantDiscountOrderType());

			page =
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountOrderType,
				(List<DiscountOrderType>)page.getItems());
			assertValid(
				page,
				testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		DiscountOrderType discountOrderType1 =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
				externalReferenceCode, randomDiscountOrderType());

		DiscountOrderType discountOrderType2 =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
				externalReferenceCode, randomDiscountOrderType());

		page =
			discountOrderTypeResource.
				getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			discountOrderType1, (List<DiscountOrderType>)page.getItems());
		assertContains(
			discountOrderType2, (List<DiscountOrderType>)page.getItems());
		assertValid(
			page,
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getExpectedActions(
				externalReferenceCode));

		discountOrderTypeResource.deleteDiscountOrderType(
			discountOrderType1.getDiscountOrderTypeId());

		discountOrderTypeResource.deleteDiscountOrderType(
			discountOrderType2.getDiscountOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountOrderTypesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getExternalReferenceCode();

		Page<DiscountOrderType> discountOrderTypesPage =
			discountOrderTypeResource.
				getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			discountOrderTypesPage.getTotalCount());

		DiscountOrderType discountOrderType1 =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
				externalReferenceCode, randomDiscountOrderType());

		DiscountOrderType discountOrderType2 =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
				externalReferenceCode, randomDiscountOrderType());

		DiscountOrderType discountOrderType3 =
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
				externalReferenceCode, randomDiscountOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountOrderType> page1 =
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				discountOrderType1, (List<DiscountOrderType>)page1.getItems());

			Page<DiscountOrderType> page2 =
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountOrderType2, (List<DiscountOrderType>)page2.getItems());

			Page<DiscountOrderType> page3 =
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountOrderType3, (List<DiscountOrderType>)page3.getItems());
		}
		else {
			Page<DiscountOrderType> page1 =
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<DiscountOrderType> discountOrderTypes1 =
				(List<DiscountOrderType>)page1.getItems();

			Assert.assertEquals(
				discountOrderTypes1.toString(), totalCount + 2,
				discountOrderTypes1.size());

			Page<DiscountOrderType> page2 =
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountOrderType> discountOrderTypes2 =
				(List<DiscountOrderType>)page2.getItems();

			Assert.assertEquals(
				discountOrderTypes2.toString(), 1, discountOrderTypes2.size());

			Page<DiscountOrderType> page3 =
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				discountOrderType1, (List<DiscountOrderType>)page3.getItems());
			assertContains(
				discountOrderType2, (List<DiscountOrderType>)page3.getItems());
			assertContains(
				discountOrderType3, (List<DiscountOrderType>)page3.getItems());
		}
	}

	protected DiscountOrderType
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_addDiscountOrderType(
				String externalReferenceCode,
				DiscountOrderType discountOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountOrderTypesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPage() throws Exception {
		Long id = testGetDiscountIdDiscountOrderTypesPage_getId();
		Long irrelevantId =
			testGetDiscountIdDiscountOrderTypesPage_getIrrelevantId();

		Page<DiscountOrderType> page =
			discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			DiscountOrderType irrelevantDiscountOrderType =
				testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
					irrelevantId, randomIrrelevantDiscountOrderType());

			page =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					irrelevantId, null, null,
					Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountOrderType,
				(List<DiscountOrderType>)page.getItems());
			assertValid(
				page,
				testGetDiscountIdDiscountOrderTypesPage_getExpectedActions(
					irrelevantId));
		}

		DiscountOrderType discountOrderType1 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, randomDiscountOrderType());

		DiscountOrderType discountOrderType2 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, randomDiscountOrderType());

		page = discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			discountOrderType1, (List<DiscountOrderType>)page.getItems());
		assertContains(
			discountOrderType2, (List<DiscountOrderType>)page.getItems());
		assertValid(
			page,
			testGetDiscountIdDiscountOrderTypesPage_getExpectedActions(id));

		discountOrderTypeResource.deleteDiscountOrderType(
			discountOrderType1.getDiscountOrderTypeId());

		discountOrderTypeResource.deleteDiscountOrderType(
			discountOrderType2.getDiscountOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountIdDiscountOrderTypesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountOrderTypesPage_getId();

		DiscountOrderType discountOrderType1 = randomDiscountOrderType();

		discountOrderType1 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, discountOrderType1);

		for (EntityField entityField : entityFields) {
			Page<DiscountOrderType> page =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null,
					getFilterString(entityField, "between", discountOrderType1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discountOrderType1),
				(List<DiscountOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithFilterDoubleEquals()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithFilterStringContains()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithFilterStringEquals()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithFilterStringStartsWith()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetDiscountIdDiscountOrderTypesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountOrderTypesPage_getId();

		DiscountOrderType discountOrderType1 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, randomDiscountOrderType());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DiscountOrderType discountOrderType2 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, randomDiscountOrderType());

		for (EntityField entityField : entityFields) {
			Page<DiscountOrderType> page =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null,
					getFilterString(entityField, operator, discountOrderType1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discountOrderType1),
				(List<DiscountOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithPagination()
		throws Exception {

		Long id = testGetDiscountIdDiscountOrderTypesPage_getId();

		Page<DiscountOrderType> discountOrderTypesPage =
			discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			discountOrderTypesPage.getTotalCount());

		DiscountOrderType discountOrderType1 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, randomDiscountOrderType());

		DiscountOrderType discountOrderType2 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, randomDiscountOrderType());

		DiscountOrderType discountOrderType3 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, randomDiscountOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountOrderType> page1 =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				discountOrderType1, (List<DiscountOrderType>)page1.getItems());

			Page<DiscountOrderType> page2 =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				discountOrderType2, (List<DiscountOrderType>)page2.getItems());

			Page<DiscountOrderType> page3 =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				discountOrderType3, (List<DiscountOrderType>)page3.getItems());
		}
		else {
			Page<DiscountOrderType> page1 =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<DiscountOrderType> discountOrderTypes1 =
				(List<DiscountOrderType>)page1.getItems();

			Assert.assertEquals(
				discountOrderTypes1.toString(), totalCount + 2,
				discountOrderTypes1.size());

			Page<DiscountOrderType> page2 =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountOrderType> discountOrderTypes2 =
				(List<DiscountOrderType>)page2.getItems();

			Assert.assertEquals(
				discountOrderTypes2.toString(), 1, discountOrderTypes2.size());

			Page<DiscountOrderType> page3 =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				discountOrderType1, (List<DiscountOrderType>)page3.getItems());
			assertContains(
				discountOrderType2, (List<DiscountOrderType>)page3.getItems());
			assertContains(
				discountOrderType3, (List<DiscountOrderType>)page3.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithSortDateTime()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, discountOrderType1, discountOrderType2) -> {
				BeanTestUtil.setProperty(
					discountOrderType1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithSortDouble()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, discountOrderType1, discountOrderType2) -> {
				BeanTestUtil.setProperty(
					discountOrderType1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					discountOrderType2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithSortInteger()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, discountOrderType1, discountOrderType2) -> {
				BeanTestUtil.setProperty(
					discountOrderType1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					discountOrderType2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDiscountIdDiscountOrderTypesPageWithSortString()
		throws Exception {

		testGetDiscountIdDiscountOrderTypesPageWithSort(
			EntityField.Type.STRING,
			(entityField, discountOrderType1, discountOrderType2) -> {
				Class<?> clazz = discountOrderType1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						discountOrderType1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						discountOrderType2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						discountOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						discountOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						discountOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						discountOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDiscountIdDiscountOrderTypesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DiscountOrderType, DiscountOrderType, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountOrderTypesPage_getId();

		DiscountOrderType discountOrderType1 = randomDiscountOrderType();
		DiscountOrderType discountOrderType2 = randomDiscountOrderType();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, discountOrderType1, discountOrderType2);
		}

		discountOrderType1 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, discountOrderType1);

		discountOrderType2 =
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				id, discountOrderType2);

		Page<DiscountOrderType> page =
			discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DiscountOrderType> ascPage =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				discountOrderType1,
				(List<DiscountOrderType>)ascPage.getItems());
			assertContains(
				discountOrderType2,
				(List<DiscountOrderType>)ascPage.getItems());

			Page<DiscountOrderType> descPage =
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				discountOrderType2,
				(List<DiscountOrderType>)descPage.getItems());
			assertContains(
				discountOrderType1,
				(List<DiscountOrderType>)descPage.getItems());
		}
	}

	protected DiscountOrderType
			testGetDiscountIdDiscountOrderTypesPage_addDiscountOrderType(
				Long id, DiscountOrderType discountOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountOrderTypesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountOrderTypesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostDiscountByExternalReferenceCodeDiscountOrderType()
		throws Exception {

		DiscountOrderType randomDiscountOrderType = randomDiscountOrderType();

		DiscountOrderType postDiscountOrderType =
			testPostDiscountByExternalReferenceCodeDiscountOrderType_addDiscountOrderType(
				randomDiscountOrderType);

		assertEquals(randomDiscountOrderType, postDiscountOrderType);
		assertValid(postDiscountOrderType);
	}

	protected DiscountOrderType
			testPostDiscountByExternalReferenceCodeDiscountOrderType_addDiscountOrderType(
				DiscountOrderType discountOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDiscountIdDiscountOrderType() throws Exception {
		DiscountOrderType randomDiscountOrderType = randomDiscountOrderType();

		DiscountOrderType postDiscountOrderType =
			testPostDiscountIdDiscountOrderType_addDiscountOrderType(
				randomDiscountOrderType);

		assertEquals(randomDiscountOrderType, postDiscountOrderType);
		assertValid(postDiscountOrderType);
	}

	protected DiscountOrderType
			testPostDiscountIdDiscountOrderType_addDiscountOrderType(
				DiscountOrderType discountOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DiscountOrderType discountOrderType1 =
			testBatchEngineDeleteImportTask_addDiscountOrderType();

		testBatchEngineDeleteImportTask_deleteDiscountOrderType(
			200, null, discountOrderType1.getDiscountOrderTypeId());
	}

	protected DiscountOrderType
			testBatchEngineDeleteImportTask_addDiscountOrderType()
		throws Exception {

		return testDeleteDiscountOrderType_addDiscountOrderType();
	}

	protected void testBatchEngineDeleteImportTask_deleteDiscountOrderType(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountOrderType",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"discountOrderTypeId", () -> id
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

	protected DiscountOrderType
			testGraphQLDiscountOrderType_addDiscountOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DiscountOrderType discountOrderType,
		List<DiscountOrderType> discountOrderTypes) {

		boolean contains = false;

		for (DiscountOrderType item : discountOrderTypes) {
			if (equals(discountOrderType, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			discountOrderTypes + " does not contain " + discountOrderType,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DiscountOrderType discountOrderType1,
		DiscountOrderType discountOrderType2) {

		Assert.assertTrue(
			discountOrderType1 + " does not equal " + discountOrderType2,
			equals(discountOrderType1, discountOrderType2));
	}

	protected void assertEquals(
		List<DiscountOrderType> discountOrderTypes1,
		List<DiscountOrderType> discountOrderTypes2) {

		Assert.assertEquals(
			discountOrderTypes1.size(), discountOrderTypes2.size());

		for (int i = 0; i < discountOrderTypes1.size(); i++) {
			DiscountOrderType discountOrderType1 = discountOrderTypes1.get(i);
			DiscountOrderType discountOrderType2 = discountOrderTypes2.get(i);

			assertEquals(discountOrderType1, discountOrderType2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DiscountOrderType> discountOrderTypes1,
		List<DiscountOrderType> discountOrderTypes2) {

		Assert.assertEquals(
			discountOrderTypes1.size(), discountOrderTypes2.size());

		for (DiscountOrderType discountOrderType1 : discountOrderTypes1) {
			boolean contains = false;

			for (DiscountOrderType discountOrderType2 : discountOrderTypes2) {
				if (equals(discountOrderType1, discountOrderType2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				discountOrderTypes2 + " does not contain " + discountOrderType1,
				contains);
		}
	}

	protected void assertValid(DiscountOrderType discountOrderType)
		throws Exception {

		boolean valid = true;

		if (discountOrderType.getDiscountOrderTypeId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (discountOrderType.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (discountOrderType.getDiscountExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (discountOrderType.getDiscountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountOrderTypeId", additionalAssertFieldName)) {

				if (discountOrderType.getDiscountOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (discountOrderType.getOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (discountOrderType.getOrderTypeExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (discountOrderType.getOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (discountOrderType.getPriority() == null) {
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

	protected void assertValid(Page<DiscountOrderType> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DiscountOrderType> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DiscountOrderType> discountOrderTypes =
			page.getItems();

		int size = discountOrderTypes.size();

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

		graphQLFields.add(new GraphQLField("discountOrderTypeId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						DiscountOrderType.class)) {

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
		DiscountOrderType discountOrderType1,
		DiscountOrderType discountOrderType2) {

		if (discountOrderType1 == discountOrderType2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)discountOrderType1.getActions(),
						(Map)discountOrderType2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountOrderType1.getDiscountExternalReferenceCode(),
						discountOrderType2.
							getDiscountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountOrderType1.getDiscountId(),
						discountOrderType2.getDiscountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountOrderTypeId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountOrderType1.getDiscountOrderTypeId(),
						discountOrderType2.getDiscountOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountOrderType1.getOrderType(),
						discountOrderType2.getOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountOrderType1.getOrderTypeExternalReferenceCode(),
						discountOrderType2.
							getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountOrderType1.getOrderTypeId(),
						discountOrderType2.getOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountOrderType1.getPriority(),
						discountOrderType2.getPriority())) {

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

		if (!(_discountOrderTypeResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_discountOrderTypeResource;

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
		DiscountOrderType discountOrderType) {

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

		if (entityFieldName.equals("discountExternalReferenceCode")) {
			Object object =
				discountOrderType.getDiscountExternalReferenceCode();

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

		if (entityFieldName.equals("discountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountOrderTypeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object =
				discountOrderType.getOrderTypeExternalReferenceCode();

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
			sb.append(String.valueOf(discountOrderType.getPriority()));

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

	protected DiscountOrderType randomDiscountOrderType() throws Exception {
		return new DiscountOrderType() {
			{
				discountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				discountId = RandomTestUtil.randomLong();
				discountOrderTypeId = RandomTestUtil.randomLong();
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomInt();
			}
		};
	}

	protected DiscountOrderType randomIrrelevantDiscountOrderType()
		throws Exception {

		DiscountOrderType randomIrrelevantDiscountOrderType =
			randomDiscountOrderType();

		return randomIrrelevantDiscountOrderType;
	}

	protected DiscountOrderType randomPatchDiscountOrderType()
		throws Exception {

		return randomDiscountOrderType();
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

	protected DiscountOrderTypeResource discountOrderTypeResource;
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
		LogFactoryUtil.getLog(BaseDiscountOrderTypeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		DiscountOrderTypeResource _discountOrderTypeResource;

}