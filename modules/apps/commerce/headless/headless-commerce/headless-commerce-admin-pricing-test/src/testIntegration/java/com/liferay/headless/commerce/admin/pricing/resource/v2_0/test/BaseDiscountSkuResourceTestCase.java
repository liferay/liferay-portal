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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountSku;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.DiscountSkuResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.DiscountSkuSerDes;
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
public abstract class BaseDiscountSkuResourceTestCase {

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

		_discountSkuResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		discountSkuResource = DiscountSkuResource.builder(
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

		DiscountSku discountSku1 = randomDiscountSku();

		String json = objectMapper.writeValueAsString(discountSku1);

		DiscountSku discountSku2 = DiscountSkuSerDes.toDTO(json);

		Assert.assertTrue(equals(discountSku1, discountSku2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DiscountSku discountSku = randomDiscountSku();

		String json1 = objectMapper.writeValueAsString(discountSku);
		String json2 = DiscountSkuSerDes.toJSON(discountSku);

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

		DiscountSku discountSku = randomDiscountSku();

		discountSku.setDiscountExternalReferenceCode(regex);
		discountSku.setSkuExternalReferenceCode(regex);
		discountSku.setUnitOfMeasureKey(regex);

		String json = DiscountSkuSerDes.toJSON(discountSku);

		Assert.assertFalse(json.contains(regex));

		discountSku = DiscountSkuSerDes.toDTO(json);

		Assert.assertEquals(
			regex, discountSku.getDiscountExternalReferenceCode());
		Assert.assertEquals(regex, discountSku.getSkuExternalReferenceCode());
		Assert.assertEquals(regex, discountSku.getUnitOfMeasureKey());
	}

	@Test
	public void testDeleteDiscountSku() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DiscountSku discountSku = testDeleteDiscountSku_addDiscountSku();

		assertHttpResponseStatusCode(
			204,
			discountSkuResource.deleteDiscountSkuHttpResponse(
				discountSku.getDiscountSkuId()));
	}

	protected DiscountSku testDeleteDiscountSku_addDiscountSku()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDiscountSku() throws Exception {

		// No namespace

		DiscountSku discountSku1 =
			testGraphQLDeleteDiscountSku_addDiscountSku();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDiscountSku",
						new HashMap<String, Object>() {
							{
								put(
									"discountSkuId",
									discountSku1.getDiscountSkuId());
							}
						})),
				"JSONObject/data", "Object/deleteDiscountSku"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		DiscountSku discountSku2 =
			testGraphQLDeleteDiscountSku_addDiscountSku();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deleteDiscountSku",
							new HashMap<String, Object>() {
								{
									put(
										"discountSkuId",
										discountSku2.getDiscountSkuId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deleteDiscountSku"));
	}

	protected DiscountSku testGraphQLDeleteDiscountSku_addDiscountSku()
		throws Exception {

		return testGraphQLDiscountSku_addDiscountSku();
	}

	@Test
	public void testDeleteDiscountSkuBatch() throws Exception {
		DiscountSku discountSku1 = testDeleteDiscountSkuBatch_addDiscountSku();

		testDeleteDiscountSkuBatch_deleteDiscountSku(
			202, null, discountSku1.getDiscountSkuId());
	}

	protected DiscountSku testDeleteDiscountSkuBatch_addDiscountSku()
		throws Exception {

		return testDeleteDiscountSku_addDiscountSku();
	}

	protected void testDeleteDiscountSkuBatch_deleteDiscountSku(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			discountSkuResource.deleteDiscountSkuBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"discountSkuId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountSkusPage()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_getIrrelevantExternalReferenceCode();

		Page<DiscountSku> page =
			discountSkuResource.
				getDiscountByExternalReferenceCodeDiscountSkusPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			DiscountSku irrelevantDiscountSku =
				testGetDiscountByExternalReferenceCodeDiscountSkusPage_addDiscountSku(
					irrelevantExternalReferenceCode,
					randomIrrelevantDiscountSku());

			page =
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountSku, (List<DiscountSku>)page.getItems());
			assertValid(
				page,
				testGetDiscountByExternalReferenceCodeDiscountSkusPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		DiscountSku discountSku1 =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_addDiscountSku(
				externalReferenceCode, randomDiscountSku());

		DiscountSku discountSku2 =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_addDiscountSku(
				externalReferenceCode, randomDiscountSku());

		page =
			discountSkuResource.
				getDiscountByExternalReferenceCodeDiscountSkusPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(discountSku1, (List<DiscountSku>)page.getItems());
		assertContains(discountSku2, (List<DiscountSku>)page.getItems());
		assertValid(
			page,
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_getExpectedActions(
				externalReferenceCode));

		discountSkuResource.deleteDiscountSku(discountSku1.getDiscountSkuId());

		discountSkuResource.deleteDiscountSku(discountSku2.getDiscountSkuId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountSkusPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_getExternalReferenceCode();

		Page<DiscountSku> discountSkusPage =
			discountSkuResource.
				getDiscountByExternalReferenceCodeDiscountSkusPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			discountSkusPage.getTotalCount());

		DiscountSku discountSku1 =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_addDiscountSku(
				externalReferenceCode, randomDiscountSku());

		DiscountSku discountSku2 =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_addDiscountSku(
				externalReferenceCode, randomDiscountSku());

		DiscountSku discountSku3 =
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_addDiscountSku(
				externalReferenceCode, randomDiscountSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountSku> page1 =
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(discountSku1, (List<DiscountSku>)page1.getItems());

			Page<DiscountSku> page2 =
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(discountSku2, (List<DiscountSku>)page2.getItems());

			Page<DiscountSku> page3 =
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(discountSku3, (List<DiscountSku>)page3.getItems());
		}
		else {
			Page<DiscountSku> page1 =
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<DiscountSku> discountSkus1 =
				(List<DiscountSku>)page1.getItems();

			Assert.assertEquals(
				discountSkus1.toString(), totalCount + 2, discountSkus1.size());

			Page<DiscountSku> page2 =
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountSku> discountSkus2 =
				(List<DiscountSku>)page2.getItems();

			Assert.assertEquals(
				discountSkus2.toString(), 1, discountSkus2.size());

			Page<DiscountSku> page3 =
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(discountSku1, (List<DiscountSku>)page3.getItems());
			assertContains(discountSku2, (List<DiscountSku>)page3.getItems());
			assertContains(discountSku3, (List<DiscountSku>)page3.getItems());
		}
	}

	protected DiscountSku
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_addDiscountSku(
				String externalReferenceCode, DiscountSku discountSku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountSkusPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetDiscountIdDiscountSkusPage() throws Exception {
		Long id = testGetDiscountIdDiscountSkusPage_getId();
		Long irrelevantId = testGetDiscountIdDiscountSkusPage_getIrrelevantId();

		Page<DiscountSku> page =
			discountSkuResource.getDiscountIdDiscountSkusPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			DiscountSku irrelevantDiscountSku =
				testGetDiscountIdDiscountSkusPage_addDiscountSku(
					irrelevantId, randomIrrelevantDiscountSku());

			page = discountSkuResource.getDiscountIdDiscountSkusPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountSku, (List<DiscountSku>)page.getItems());
			assertValid(
				page,
				testGetDiscountIdDiscountSkusPage_getExpectedActions(
					irrelevantId));
		}

		DiscountSku discountSku1 =
			testGetDiscountIdDiscountSkusPage_addDiscountSku(
				id, randomDiscountSku());

		DiscountSku discountSku2 =
			testGetDiscountIdDiscountSkusPage_addDiscountSku(
				id, randomDiscountSku());

		page = discountSkuResource.getDiscountIdDiscountSkusPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(discountSku1, (List<DiscountSku>)page.getItems());
		assertContains(discountSku2, (List<DiscountSku>)page.getItems());
		assertValid(
			page, testGetDiscountIdDiscountSkusPage_getExpectedActions(id));

		discountSkuResource.deleteDiscountSku(discountSku1.getDiscountSkuId());

		discountSkuResource.deleteDiscountSku(discountSku2.getDiscountSkuId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountIdDiscountSkusPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountSkusPage_getId();

		DiscountSku discountSku1 = randomDiscountSku();

		discountSku1 = testGetDiscountIdDiscountSkusPage_addDiscountSku(
			id, discountSku1);

		for (EntityField entityField : entityFields) {
			Page<DiscountSku> page =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null,
					getFilterString(entityField, "between", discountSku1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discountSku1),
				(List<DiscountSku>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithFilterDoubleEquals()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithFilterStringContains()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithFilterStringEquals()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithFilterStringStartsWith()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetDiscountIdDiscountSkusPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountSkusPage_getId();

		DiscountSku discountSku1 =
			testGetDiscountIdDiscountSkusPage_addDiscountSku(
				id, randomDiscountSku());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DiscountSku discountSku2 =
			testGetDiscountIdDiscountSkusPage_addDiscountSku(
				id, randomDiscountSku());

		for (EntityField entityField : entityFields) {
			Page<DiscountSku> page =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null,
					getFilterString(entityField, operator, discountSku1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discountSku1),
				(List<DiscountSku>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithPagination()
		throws Exception {

		Long id = testGetDiscountIdDiscountSkusPage_getId();

		Page<DiscountSku> discountSkusPage =
			discountSkuResource.getDiscountIdDiscountSkusPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			discountSkusPage.getTotalCount());

		DiscountSku discountSku1 =
			testGetDiscountIdDiscountSkusPage_addDiscountSku(
				id, randomDiscountSku());

		DiscountSku discountSku2 =
			testGetDiscountIdDiscountSkusPage_addDiscountSku(
				id, randomDiscountSku());

		DiscountSku discountSku3 =
			testGetDiscountIdDiscountSkusPage_addDiscountSku(
				id, randomDiscountSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountSku> page1 =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(discountSku1, (List<DiscountSku>)page1.getItems());

			Page<DiscountSku> page2 =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(discountSku2, (List<DiscountSku>)page2.getItems());

			Page<DiscountSku> page3 =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(discountSku3, (List<DiscountSku>)page3.getItems());
		}
		else {
			Page<DiscountSku> page1 =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<DiscountSku> discountSkus1 =
				(List<DiscountSku>)page1.getItems();

			Assert.assertEquals(
				discountSkus1.toString(), totalCount + 2, discountSkus1.size());

			Page<DiscountSku> page2 =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountSku> discountSkus2 =
				(List<DiscountSku>)page2.getItems();

			Assert.assertEquals(
				discountSkus2.toString(), 1, discountSkus2.size());

			Page<DiscountSku> page3 =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(discountSku1, (List<DiscountSku>)page3.getItems());
			assertContains(discountSku2, (List<DiscountSku>)page3.getItems());
			assertContains(discountSku3, (List<DiscountSku>)page3.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithSortDateTime()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, discountSku1, discountSku2) -> {
				BeanTestUtil.setProperty(
					discountSku1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithSortDouble()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, discountSku1, discountSku2) -> {
				BeanTestUtil.setProperty(
					discountSku1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					discountSku2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithSortInteger()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, discountSku1, discountSku2) -> {
				BeanTestUtil.setProperty(
					discountSku1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					discountSku2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDiscountIdDiscountSkusPageWithSortString()
		throws Exception {

		testGetDiscountIdDiscountSkusPageWithSort(
			EntityField.Type.STRING,
			(entityField, discountSku1, discountSku2) -> {
				Class<?> clazz = discountSku1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						discountSku1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						discountSku2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						discountSku1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						discountSku2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						discountSku1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						discountSku2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDiscountIdDiscountSkusPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, DiscountSku, DiscountSku, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountSkusPage_getId();

		DiscountSku discountSku1 = randomDiscountSku();
		DiscountSku discountSku2 = randomDiscountSku();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, discountSku1, discountSku2);
		}

		discountSku1 = testGetDiscountIdDiscountSkusPage_addDiscountSku(
			id, discountSku1);

		discountSku2 = testGetDiscountIdDiscountSkusPage_addDiscountSku(
			id, discountSku2);

		Page<DiscountSku> page =
			discountSkuResource.getDiscountIdDiscountSkusPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DiscountSku> ascPage =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(discountSku1, (List<DiscountSku>)ascPage.getItems());
			assertContains(discountSku2, (List<DiscountSku>)ascPage.getItems());

			Page<DiscountSku> descPage =
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				discountSku2, (List<DiscountSku>)descPage.getItems());
			assertContains(
				discountSku1, (List<DiscountSku>)descPage.getItems());
		}
	}

	protected DiscountSku testGetDiscountIdDiscountSkusPage_addDiscountSku(
			Long id, DiscountSku discountSku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountSkusPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountSkusPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostDiscountByExternalReferenceCodeDiscountSku()
		throws Exception {

		DiscountSku randomDiscountSku = randomDiscountSku();

		DiscountSku postDiscountSku =
			testPostDiscountByExternalReferenceCodeDiscountSku_addDiscountSku(
				randomDiscountSku);

		assertEquals(randomDiscountSku, postDiscountSku);
		assertValid(postDiscountSku);
	}

	protected DiscountSku
			testPostDiscountByExternalReferenceCodeDiscountSku_addDiscountSku(
				DiscountSku discountSku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDiscountIdDiscountSku() throws Exception {
		DiscountSku randomDiscountSku = randomDiscountSku();

		DiscountSku postDiscountSku =
			testPostDiscountIdDiscountSku_addDiscountSku(randomDiscountSku);

		assertEquals(randomDiscountSku, postDiscountSku);
		assertValid(postDiscountSku);
	}

	protected DiscountSku testPostDiscountIdDiscountSku_addDiscountSku(
			DiscountSku discountSku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DiscountSku discountSku1 =
			testBatchEngineDeleteImportTask_addDiscountSku();

		testBatchEngineDeleteImportTask_deleteDiscountSku(
			200, null, discountSku1.getDiscountSkuId());
	}

	protected DiscountSku testBatchEngineDeleteImportTask_addDiscountSku()
		throws Exception {

		return testDeleteDiscountSku_addDiscountSku();
	}

	protected void testBatchEngineDeleteImportTask_deleteDiscountSku(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountSku",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"discountSkuId", () -> id
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

	protected DiscountSku testGraphQLDiscountSku_addDiscountSku()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DiscountSku discountSku, List<DiscountSku> discountSkus) {

		boolean contains = false;

		for (DiscountSku item : discountSkus) {
			if (equals(discountSku, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			discountSkus + " does not contain " + discountSku, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DiscountSku discountSku1, DiscountSku discountSku2) {

		Assert.assertTrue(
			discountSku1 + " does not equal " + discountSku2,
			equals(discountSku1, discountSku2));
	}

	protected void assertEquals(
		List<DiscountSku> discountSkus1, List<DiscountSku> discountSkus2) {

		Assert.assertEquals(discountSkus1.size(), discountSkus2.size());

		for (int i = 0; i < discountSkus1.size(); i++) {
			DiscountSku discountSku1 = discountSkus1.get(i);
			DiscountSku discountSku2 = discountSkus2.get(i);

			assertEquals(discountSku1, discountSku2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DiscountSku> discountSkus1, List<DiscountSku> discountSkus2) {

		Assert.assertEquals(discountSkus1.size(), discountSkus2.size());

		for (DiscountSku discountSku1 : discountSkus1) {
			boolean contains = false;

			for (DiscountSku discountSku2 : discountSkus2) {
				if (equals(discountSku1, discountSku2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				discountSkus2 + " does not contain " + discountSku1, contains);
		}
	}

	protected void assertValid(DiscountSku discountSku) throws Exception {
		boolean valid = true;

		if (discountSku.getDiscountSkuId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (discountSku.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (discountSku.getDiscountExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (discountSku.getDiscountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountSkuId", additionalAssertFieldName)) {
				if (discountSku.getDiscountSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (discountSku.getProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productName", additionalAssertFieldName)) {
				if (discountSku.getProductName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (discountSku.getSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (discountSku.getSkuExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (discountSku.getSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (discountSku.getUnitOfMeasureKey() == null) {
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

	protected void assertValid(Page<DiscountSku> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DiscountSku> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DiscountSku> discountSkus = page.getItems();

		int size = discountSkus.size();

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

		graphQLFields.add(new GraphQLField("discountSkuId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						DiscountSku.class)) {

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
		DiscountSku discountSku1, DiscountSku discountSku2) {

		if (discountSku1 == discountSku2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)discountSku1.getActions(),
						(Map)discountSku2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountSku1.getDiscountExternalReferenceCode(),
						discountSku2.getDiscountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountSku1.getDiscountId(),
						discountSku2.getDiscountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountSkuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountSku1.getDiscountSkuId(),
						discountSku2.getDiscountSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountSku1.getProductId(),
						discountSku2.getProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productName", additionalAssertFieldName)) {
				if (!equals(
						(Map)discountSku1.getProductName(),
						(Map)discountSku2.getProductName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountSku1.getSku(), discountSku2.getSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountSku1.getSkuExternalReferenceCode(),
						discountSku2.getSkuExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountSku1.getSkuId(), discountSku2.getSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountSku1.getUnitOfMeasureKey(),
						discountSku2.getUnitOfMeasureKey())) {

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

		if (!(_discountSkuResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_discountSkuResource;

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
		EntityField entityField, String operator, DiscountSku discountSku) {

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
			Object object = discountSku.getDiscountExternalReferenceCode();

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

		if (entityFieldName.equals("discountSkuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productName")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sku")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuExternalReferenceCode")) {
			Object object = discountSku.getSkuExternalReferenceCode();

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

		if (entityFieldName.equals("skuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = discountSku.getUnitOfMeasureKey();

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

	protected DiscountSku randomDiscountSku() throws Exception {
		return new DiscountSku() {
			{
				discountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				discountId = RandomTestUtil.randomLong();
				discountSkuId = RandomTestUtil.randomLong();
				productId = RandomTestUtil.randomLong();
				skuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				skuId = RandomTestUtil.randomLong();
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected DiscountSku randomIrrelevantDiscountSku() throws Exception {
		DiscountSku randomIrrelevantDiscountSku = randomDiscountSku();

		return randomIrrelevantDiscountSku;
	}

	protected DiscountSku randomPatchDiscountSku() throws Exception {
		return randomDiscountSku();
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

	protected DiscountSkuResource discountSkuResource;
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
		LogFactoryUtil.getLog(BaseDiscountSkuResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		DiscountSkuResource _discountSkuResource;

}