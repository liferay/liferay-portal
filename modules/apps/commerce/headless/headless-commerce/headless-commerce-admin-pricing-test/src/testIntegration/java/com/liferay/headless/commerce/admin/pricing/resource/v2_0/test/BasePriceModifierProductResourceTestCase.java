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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierProduct;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceModifierProductResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceModifierProductSerDes;
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
public abstract class BasePriceModifierProductResourceTestCase {

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

		_priceModifierProductResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		priceModifierProductResource = PriceModifierProductResource.builder(
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

		PriceModifierProduct priceModifierProduct1 =
			randomPriceModifierProduct();

		String json = objectMapper.writeValueAsString(priceModifierProduct1);

		PriceModifierProduct priceModifierProduct2 =
			PriceModifierProductSerDes.toDTO(json);

		Assert.assertTrue(equals(priceModifierProduct1, priceModifierProduct2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PriceModifierProduct priceModifierProduct =
			randomPriceModifierProduct();

		String json1 = objectMapper.writeValueAsString(priceModifierProduct);
		String json2 = PriceModifierProductSerDes.toJSON(priceModifierProduct);

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

		PriceModifierProduct priceModifierProduct =
			randomPriceModifierProduct();

		priceModifierProduct.setPriceModifierExternalReferenceCode(regex);
		priceModifierProduct.setProductExternalReferenceCode(regex);

		String json = PriceModifierProductSerDes.toJSON(priceModifierProduct);

		Assert.assertFalse(json.contains(regex));

		priceModifierProduct = PriceModifierProductSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			priceModifierProduct.getPriceModifierExternalReferenceCode());
		Assert.assertEquals(
			regex, priceModifierProduct.getProductExternalReferenceCode());
	}

	@Test
	public void testDeletePriceModifierProduct() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceModifierProduct priceModifierProduct =
			testDeletePriceModifierProduct_addPriceModifierProduct();

		assertHttpResponseStatusCode(
			204,
			priceModifierProductResource.deletePriceModifierProductHttpResponse(
				priceModifierProduct.getPriceModifierProductId()));
	}

	protected PriceModifierProduct
			testDeletePriceModifierProduct_addPriceModifierProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceModifierProduct() throws Exception {

		// No namespace

		PriceModifierProduct priceModifierProduct1 =
			testGraphQLDeletePriceModifierProduct_addPriceModifierProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceModifierProduct",
						new HashMap<String, Object>() {
							{
								put(
									"priceModifierProductId",
									priceModifierProduct1.
										getPriceModifierProductId());
							}
						})),
				"JSONObject/data", "Object/deletePriceModifierProduct"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceModifierProduct priceModifierProduct2 =
			testGraphQLDeletePriceModifierProduct_addPriceModifierProduct();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceModifierProduct",
							new HashMap<String, Object>() {
								{
									put(
										"priceModifierProductId",
										priceModifierProduct2.
											getPriceModifierProductId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceModifierProduct"));
	}

	protected PriceModifierProduct
			testGraphQLDeletePriceModifierProduct_addPriceModifierProduct()
		throws Exception {

		return testGraphQLPriceModifierProduct_addPriceModifierProduct();
	}

	@Test
	public void testDeletePriceModifierProductBatch() throws Exception {
		PriceModifierProduct priceModifierProduct1 =
			testDeletePriceModifierProductBatch_addPriceModifierProduct();

		testDeletePriceModifierProductBatch_deletePriceModifierProduct(
			202, null, priceModifierProduct1.getPriceModifierProductId());
	}

	protected PriceModifierProduct
			testDeletePriceModifierProductBatch_addPriceModifierProduct()
		throws Exception {

		return testDeletePriceModifierProduct_addPriceModifierProduct();
	}

	protected void
			testDeletePriceModifierProductBatch_deletePriceModifierProduct(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			priceModifierProductResource.
				deletePriceModifierProductBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"priceModifierProductId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage()
		throws Exception {

		String externalReferenceCode =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getIrrelevantExternalReferenceCode();

		Page<PriceModifierProduct> page =
			priceModifierProductResource.
				getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PriceModifierProduct irrelevantPriceModifierProduct =
				testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_addPriceModifierProduct(
					irrelevantExternalReferenceCode,
					randomIrrelevantPriceModifierProduct());

			page =
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceModifierProduct,
				(List<PriceModifierProduct>)page.getItems());
			assertValid(
				page,
				testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PriceModifierProduct priceModifierProduct1 =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_addPriceModifierProduct(
				externalReferenceCode, randomPriceModifierProduct());

		PriceModifierProduct priceModifierProduct2 =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_addPriceModifierProduct(
				externalReferenceCode, randomPriceModifierProduct());

		page =
			priceModifierProductResource.
				getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceModifierProduct1, (List<PriceModifierProduct>)page.getItems());
		assertContains(
			priceModifierProduct2, (List<PriceModifierProduct>)page.getItems());
		assertValid(
			page,
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getExpectedActions(
				externalReferenceCode));

		priceModifierProductResource.deletePriceModifierProduct(
			priceModifierProduct1.getPriceModifierProductId());

		priceModifierProductResource.deletePriceModifierProduct(
			priceModifierProduct2.getPriceModifierProductId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceModifierByExternalReferenceCodePriceModifierProductsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getExternalReferenceCode();

		Page<PriceModifierProduct> priceModifierProductsPage =
			priceModifierProductResource.
				getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			priceModifierProductsPage.getTotalCount());

		PriceModifierProduct priceModifierProduct1 =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_addPriceModifierProduct(
				externalReferenceCode, randomPriceModifierProduct());

		PriceModifierProduct priceModifierProduct2 =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_addPriceModifierProduct(
				externalReferenceCode, randomPriceModifierProduct());

		PriceModifierProduct priceModifierProduct3 =
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_addPriceModifierProduct(
				externalReferenceCode, randomPriceModifierProduct());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceModifierProduct> page1 =
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceModifierProduct1,
				(List<PriceModifierProduct>)page1.getItems());

			Page<PriceModifierProduct> page2 =
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceModifierProduct2,
				(List<PriceModifierProduct>)page2.getItems());

			Page<PriceModifierProduct> page3 =
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceModifierProduct3,
				(List<PriceModifierProduct>)page3.getItems());
		}
		else {
			Page<PriceModifierProduct> page1 =
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<PriceModifierProduct> priceModifierProducts1 =
				(List<PriceModifierProduct>)page1.getItems();

			Assert.assertEquals(
				priceModifierProducts1.toString(), totalCount + 2,
				priceModifierProducts1.size());

			Page<PriceModifierProduct> page2 =
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceModifierProduct> priceModifierProducts2 =
				(List<PriceModifierProduct>)page2.getItems();

			Assert.assertEquals(
				priceModifierProducts2.toString(), 1,
				priceModifierProducts2.size());

			Page<PriceModifierProduct> page3 =
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				priceModifierProduct1,
				(List<PriceModifierProduct>)page3.getItems());
			assertContains(
				priceModifierProduct2,
				(List<PriceModifierProduct>)page3.getItems());
			assertContains(
				priceModifierProduct3,
				(List<PriceModifierProduct>)page3.getItems());
		}
	}

	protected PriceModifierProduct
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_addPriceModifierProduct(
				String externalReferenceCode,
				PriceModifierProduct priceModifierProduct)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceModifierByExternalReferenceCodePriceModifierProductsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPage()
		throws Exception {

		Long id = testGetPriceModifierIdPriceModifierProductsPage_getId();
		Long irrelevantId =
			testGetPriceModifierIdPriceModifierProductsPage_getIrrelevantId();

		Page<PriceModifierProduct> page =
			priceModifierProductResource.
				getPriceModifierIdPriceModifierProductsPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PriceModifierProduct irrelevantPriceModifierProduct =
				testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
					irrelevantId, randomIrrelevantPriceModifierProduct());

			page =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceModifierProduct,
				(List<PriceModifierProduct>)page.getItems());
			assertValid(
				page,
				testGetPriceModifierIdPriceModifierProductsPage_getExpectedActions(
					irrelevantId));
		}

		PriceModifierProduct priceModifierProduct1 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, randomPriceModifierProduct());

		PriceModifierProduct priceModifierProduct2 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, randomPriceModifierProduct());

		page =
			priceModifierProductResource.
				getPriceModifierIdPriceModifierProductsPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceModifierProduct1, (List<PriceModifierProduct>)page.getItems());
		assertContains(
			priceModifierProduct2, (List<PriceModifierProduct>)page.getItems());
		assertValid(
			page,
			testGetPriceModifierIdPriceModifierProductsPage_getExpectedActions(
				id));

		priceModifierProductResource.deletePriceModifierProduct(
			priceModifierProduct1.getPriceModifierProductId());

		priceModifierProductResource.deletePriceModifierProduct(
			priceModifierProduct2.getPriceModifierProductId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceModifierIdPriceModifierProductsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceModifierIdPriceModifierProductsPage_getId();

		PriceModifierProduct priceModifierProduct1 =
			randomPriceModifierProduct();

		priceModifierProduct1 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, priceModifierProduct1);

		for (EntityField entityField : entityFields) {
			Page<PriceModifierProduct> page =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null,
						getFilterString(
							entityField, "between", priceModifierProduct1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceModifierProduct1),
				(List<PriceModifierProduct>)page.getItems());
		}
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithFilterDoubleEquals()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithFilterStringContains()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithFilterStringEquals()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetPriceModifierIdPriceModifierProductsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceModifierIdPriceModifierProductsPage_getId();

		PriceModifierProduct priceModifierProduct1 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, randomPriceModifierProduct());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceModifierProduct priceModifierProduct2 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, randomPriceModifierProduct());

		for (EntityField entityField : entityFields) {
			Page<PriceModifierProduct> page =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null,
						getFilterString(
							entityField, operator, priceModifierProduct1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceModifierProduct1),
				(List<PriceModifierProduct>)page.getItems());
		}
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithPagination()
		throws Exception {

		Long id = testGetPriceModifierIdPriceModifierProductsPage_getId();

		Page<PriceModifierProduct> priceModifierProductsPage =
			priceModifierProductResource.
				getPriceModifierIdPriceModifierProductsPage(
					id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			priceModifierProductsPage.getTotalCount());

		PriceModifierProduct priceModifierProduct1 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, randomPriceModifierProduct());

		PriceModifierProduct priceModifierProduct2 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, randomPriceModifierProduct());

		PriceModifierProduct priceModifierProduct3 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, randomPriceModifierProduct());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceModifierProduct> page1 =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceModifierProduct1,
				(List<PriceModifierProduct>)page1.getItems());

			Page<PriceModifierProduct> page2 =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				priceModifierProduct2,
				(List<PriceModifierProduct>)page2.getItems());

			Page<PriceModifierProduct> page3 =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				priceModifierProduct3,
				(List<PriceModifierProduct>)page3.getItems());
		}
		else {
			Page<PriceModifierProduct> page1 =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PriceModifierProduct> priceModifierProducts1 =
				(List<PriceModifierProduct>)page1.getItems();

			Assert.assertEquals(
				priceModifierProducts1.toString(), totalCount + 2,
				priceModifierProducts1.size());

			Page<PriceModifierProduct> page2 =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceModifierProduct> priceModifierProducts2 =
				(List<PriceModifierProduct>)page2.getItems();

			Assert.assertEquals(
				priceModifierProducts2.toString(), 1,
				priceModifierProducts2.size());

			Page<PriceModifierProduct> page3 =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				priceModifierProduct1,
				(List<PriceModifierProduct>)page3.getItems());
			assertContains(
				priceModifierProduct2,
				(List<PriceModifierProduct>)page3.getItems());
			assertContains(
				priceModifierProduct3,
				(List<PriceModifierProduct>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithSortDateTime()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceModifierProduct1, priceModifierProduct2) -> {
				BeanTestUtil.setProperty(
					priceModifierProduct1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithSortDouble()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceModifierProduct1, priceModifierProduct2) -> {
				BeanTestUtil.setProperty(
					priceModifierProduct1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceModifierProduct2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithSortInteger()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceModifierProduct1, priceModifierProduct2) -> {
				BeanTestUtil.setProperty(
					priceModifierProduct1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					priceModifierProduct2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceModifierIdPriceModifierProductsPageWithSortString()
		throws Exception {

		testGetPriceModifierIdPriceModifierProductsPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceModifierProduct1, priceModifierProduct2) -> {
				Class<?> clazz = priceModifierProduct1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceModifierProduct1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceModifierProduct2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceModifierProduct1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceModifierProduct2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceModifierProduct1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceModifierProduct2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPriceModifierIdPriceModifierProductsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PriceModifierProduct, PriceModifierProduct,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceModifierIdPriceModifierProductsPage_getId();

		PriceModifierProduct priceModifierProduct1 =
			randomPriceModifierProduct();
		PriceModifierProduct priceModifierProduct2 =
			randomPriceModifierProduct();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, priceModifierProduct1, priceModifierProduct2);
		}

		priceModifierProduct1 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, priceModifierProduct1);

		priceModifierProduct2 =
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				id, priceModifierProduct2);

		Page<PriceModifierProduct> page =
			priceModifierProductResource.
				getPriceModifierIdPriceModifierProductsPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceModifierProduct> ascPage =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				priceModifierProduct1,
				(List<PriceModifierProduct>)ascPage.getItems());
			assertContains(
				priceModifierProduct2,
				(List<PriceModifierProduct>)ascPage.getItems());

			Page<PriceModifierProduct> descPage =
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				priceModifierProduct2,
				(List<PriceModifierProduct>)descPage.getItems());
			assertContains(
				priceModifierProduct1,
				(List<PriceModifierProduct>)descPage.getItems());
		}
	}

	protected PriceModifierProduct
			testGetPriceModifierIdPriceModifierProductsPage_addPriceModifierProduct(
				Long id, PriceModifierProduct priceModifierProduct)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceModifierIdPriceModifierProductsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPriceModifierIdPriceModifierProductsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostPriceModifierByExternalReferenceCodePriceModifierProduct()
		throws Exception {

		PriceModifierProduct randomPriceModifierProduct =
			randomPriceModifierProduct();

		PriceModifierProduct postPriceModifierProduct =
			testPostPriceModifierByExternalReferenceCodePriceModifierProduct_addPriceModifierProduct(
				randomPriceModifierProduct);

		assertEquals(randomPriceModifierProduct, postPriceModifierProduct);
		assertValid(postPriceModifierProduct);
	}

	protected PriceModifierProduct
			testPostPriceModifierByExternalReferenceCodePriceModifierProduct_addPriceModifierProduct(
				PriceModifierProduct priceModifierProduct)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceModifierIdPriceModifierProduct() throws Exception {
		PriceModifierProduct randomPriceModifierProduct =
			randomPriceModifierProduct();

		PriceModifierProduct postPriceModifierProduct =
			testPostPriceModifierIdPriceModifierProduct_addPriceModifierProduct(
				randomPriceModifierProduct);

		assertEquals(randomPriceModifierProduct, postPriceModifierProduct);
		assertValid(postPriceModifierProduct);
	}

	protected PriceModifierProduct
			testPostPriceModifierIdPriceModifierProduct_addPriceModifierProduct(
				PriceModifierProduct priceModifierProduct)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PriceModifierProduct priceModifierProduct1 =
			testBatchEngineDeleteImportTask_addPriceModifierProduct();

		testBatchEngineDeleteImportTask_deletePriceModifierProduct(
			200, null, priceModifierProduct1.getPriceModifierProductId());
	}

	protected PriceModifierProduct
			testBatchEngineDeleteImportTask_addPriceModifierProduct()
		throws Exception {

		return testDeletePriceModifierProduct_addPriceModifierProduct();
	}

	protected void testBatchEngineDeleteImportTask_deletePriceModifierProduct(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProduct",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"priceModifierProductId", () -> id
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

	protected PriceModifierProduct
			testGraphQLPriceModifierProduct_addPriceModifierProduct()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PriceModifierProduct priceModifierProduct,
		List<PriceModifierProduct> priceModifierProducts) {

		boolean contains = false;

		for (PriceModifierProduct item : priceModifierProducts) {
			if (equals(priceModifierProduct, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			priceModifierProducts + " does not contain " + priceModifierProduct,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PriceModifierProduct priceModifierProduct1,
		PriceModifierProduct priceModifierProduct2) {

		Assert.assertTrue(
			priceModifierProduct1 + " does not equal " + priceModifierProduct2,
			equals(priceModifierProduct1, priceModifierProduct2));
	}

	protected void assertEquals(
		List<PriceModifierProduct> priceModifierProducts1,
		List<PriceModifierProduct> priceModifierProducts2) {

		Assert.assertEquals(
			priceModifierProducts1.size(), priceModifierProducts2.size());

		for (int i = 0; i < priceModifierProducts1.size(); i++) {
			PriceModifierProduct priceModifierProduct1 =
				priceModifierProducts1.get(i);
			PriceModifierProduct priceModifierProduct2 =
				priceModifierProducts2.get(i);

			assertEquals(priceModifierProduct1, priceModifierProduct2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PriceModifierProduct> priceModifierProducts1,
		List<PriceModifierProduct> priceModifierProducts2) {

		Assert.assertEquals(
			priceModifierProducts1.size(), priceModifierProducts2.size());

		for (PriceModifierProduct priceModifierProduct1 :
				priceModifierProducts1) {

			boolean contains = false;

			for (PriceModifierProduct priceModifierProduct2 :
					priceModifierProducts2) {

				if (equals(priceModifierProduct1, priceModifierProduct2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				priceModifierProducts2 + " does not contain " +
					priceModifierProduct1,
				contains);
		}
	}

	protected void assertValid(PriceModifierProduct priceModifierProduct)
		throws Exception {

		boolean valid = true;

		if (priceModifierProduct.getPriceModifierProductId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (priceModifierProduct.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceModifierProduct.
						getPriceModifierExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceModifierId", additionalAssertFieldName)) {
				if (priceModifierProduct.getPriceModifierId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierProductId", additionalAssertFieldName)) {

				if (priceModifierProduct.getPriceModifierProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("product", additionalAssertFieldName)) {
				if (priceModifierProduct.getProduct() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceModifierProduct.getProductExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (priceModifierProduct.getProductId() == null) {
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

	protected void assertValid(Page<PriceModifierProduct> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PriceModifierProduct> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PriceModifierProduct> priceModifierProducts =
			page.getItems();

		int size = priceModifierProducts.size();

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

		graphQLFields.add(new GraphQLField("priceModifierProductId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						PriceModifierProduct.class)) {

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
		PriceModifierProduct priceModifierProduct1,
		PriceModifierProduct priceModifierProduct2) {

		if (priceModifierProduct1 == priceModifierProduct2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceModifierProduct1.getActions(),
						(Map)priceModifierProduct2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifierProduct1.
							getPriceModifierExternalReferenceCode(),
						priceModifierProduct2.
							getPriceModifierExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceModifierId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifierProduct1.getPriceModifierId(),
						priceModifierProduct2.getPriceModifierId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierProductId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifierProduct1.getPriceModifierProductId(),
						priceModifierProduct2.getPriceModifierProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("product", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifierProduct1.getProduct(),
						priceModifierProduct2.getProduct())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifierProduct1.getProductExternalReferenceCode(),
						priceModifierProduct2.
							getProductExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifierProduct1.getProductId(),
						priceModifierProduct2.getProductId())) {

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

		if (!(_priceModifierProductResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_priceModifierProductResource;

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
		PriceModifierProduct priceModifierProduct) {

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

		if (entityFieldName.equals("priceModifierExternalReferenceCode")) {
			Object object =
				priceModifierProduct.getPriceModifierExternalReferenceCode();

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

		if (entityFieldName.equals("priceModifierId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceModifierProductId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("product")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productExternalReferenceCode")) {
			Object object =
				priceModifierProduct.getProductExternalReferenceCode();

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

		if (entityFieldName.equals("productId")) {
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

	protected PriceModifierProduct randomPriceModifierProduct()
		throws Exception {

		return new PriceModifierProduct() {
			{
				priceModifierExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceModifierId = RandomTestUtil.randomLong();
				priceModifierProductId = RandomTestUtil.randomLong();
				productExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productId = RandomTestUtil.randomLong();
			}
		};
	}

	protected PriceModifierProduct randomIrrelevantPriceModifierProduct()
		throws Exception {

		PriceModifierProduct randomIrrelevantPriceModifierProduct =
			randomPriceModifierProduct();

		return randomIrrelevantPriceModifierProduct;
	}

	protected PriceModifierProduct randomPatchPriceModifierProduct()
		throws Exception {

		return randomPriceModifierProduct();
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

	protected PriceModifierProductResource priceModifierProductResource;
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
		LogFactoryUtil.getLog(BasePriceModifierProductResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		PriceModifierProductResource _priceModifierProductResource;

}