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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierCategory;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceModifierCategoryResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceModifierCategorySerDes;
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
public abstract class BasePriceModifierCategoryResourceTestCase {

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

		_priceModifierCategoryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		priceModifierCategoryResource = PriceModifierCategoryResource.builder(
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

		PriceModifierCategory priceModifierCategory1 =
			randomPriceModifierCategory();

		String json = objectMapper.writeValueAsString(priceModifierCategory1);

		PriceModifierCategory priceModifierCategory2 =
			PriceModifierCategorySerDes.toDTO(json);

		Assert.assertTrue(
			equals(priceModifierCategory1, priceModifierCategory2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PriceModifierCategory priceModifierCategory =
			randomPriceModifierCategory();

		String json1 = objectMapper.writeValueAsString(priceModifierCategory);
		String json2 = PriceModifierCategorySerDes.toJSON(
			priceModifierCategory);

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

		PriceModifierCategory priceModifierCategory =
			randomPriceModifierCategory();

		priceModifierCategory.setCategoryExternalReferenceCode(regex);
		priceModifierCategory.setPriceModifierExternalReferenceCode(regex);

		String json = PriceModifierCategorySerDes.toJSON(priceModifierCategory);

		Assert.assertFalse(json.contains(regex));

		priceModifierCategory = PriceModifierCategorySerDes.toDTO(json);

		Assert.assertEquals(
			regex, priceModifierCategory.getCategoryExternalReferenceCode());
		Assert.assertEquals(
			regex,
			priceModifierCategory.getPriceModifierExternalReferenceCode());
	}

	@Test
	public void testDeletePriceModifierCategory() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceModifierCategory priceModifierCategory =
			testDeletePriceModifierCategory_addPriceModifierCategory();

		assertHttpResponseStatusCode(
			204,
			priceModifierCategoryResource.
				deletePriceModifierCategoryHttpResponse(
					priceModifierCategory.getPriceModifierCategoryId()));
	}

	protected PriceModifierCategory
			testDeletePriceModifierCategory_addPriceModifierCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceModifierCategory() throws Exception {

		// No namespace

		PriceModifierCategory priceModifierCategory1 =
			testGraphQLDeletePriceModifierCategory_addPriceModifierCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceModifierCategory",
						new HashMap<String, Object>() {
							{
								put(
									"priceModifierCategoryId",
									priceModifierCategory1.
										getPriceModifierCategoryId());
							}
						})),
				"JSONObject/data", "Object/deletePriceModifierCategory"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceModifierCategory priceModifierCategory2 =
			testGraphQLDeletePriceModifierCategory_addPriceModifierCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceModifierCategory",
							new HashMap<String, Object>() {
								{
									put(
										"priceModifierCategoryId",
										priceModifierCategory2.
											getPriceModifierCategoryId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceModifierCategory"));
	}

	protected PriceModifierCategory
			testGraphQLDeletePriceModifierCategory_addPriceModifierCategory()
		throws Exception {

		return testGraphQLPriceModifierCategory_addPriceModifierCategory();
	}

	@Test
	public void testDeletePriceModifierCategoryBatch() throws Exception {
		PriceModifierCategory priceModifierCategory1 =
			testDeletePriceModifierCategoryBatch_addPriceModifierCategory();

		testDeletePriceModifierCategoryBatch_deletePriceModifierCategory(
			202, null, priceModifierCategory1.getPriceModifierCategoryId());
	}

	protected PriceModifierCategory
			testDeletePriceModifierCategoryBatch_addPriceModifierCategory()
		throws Exception {

		return testDeletePriceModifierCategory_addPriceModifierCategory();
	}

	protected void
			testDeletePriceModifierCategoryBatch_deletePriceModifierCategory(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			priceModifierCategoryResource.
				deletePriceModifierCategoryBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"priceModifierCategoryId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage()
		throws Exception {

		String externalReferenceCode =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getIrrelevantExternalReferenceCode();

		Page<PriceModifierCategory> page =
			priceModifierCategoryResource.
				getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PriceModifierCategory irrelevantPriceModifierCategory =
				testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_addPriceModifierCategory(
					irrelevantExternalReferenceCode,
					randomIrrelevantPriceModifierCategory());

			page =
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceModifierCategory,
				(List<PriceModifierCategory>)page.getItems());
			assertValid(
				page,
				testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PriceModifierCategory priceModifierCategory1 =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_addPriceModifierCategory(
				externalReferenceCode, randomPriceModifierCategory());

		PriceModifierCategory priceModifierCategory2 =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_addPriceModifierCategory(
				externalReferenceCode, randomPriceModifierCategory());

		page =
			priceModifierCategoryResource.
				getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceModifierCategory1,
			(List<PriceModifierCategory>)page.getItems());
		assertContains(
			priceModifierCategory2,
			(List<PriceModifierCategory>)page.getItems());
		assertValid(
			page,
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getExpectedActions(
				externalReferenceCode));

		priceModifierCategoryResource.deletePriceModifierCategory(
			priceModifierCategory1.getPriceModifierCategoryId());

		priceModifierCategoryResource.deletePriceModifierCategory(
			priceModifierCategory2.getPriceModifierCategoryId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getExternalReferenceCode();

		Page<PriceModifierCategory> priceModifierCategoriesPage =
			priceModifierCategoryResource.
				getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			priceModifierCategoriesPage.getTotalCount());

		PriceModifierCategory priceModifierCategory1 =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_addPriceModifierCategory(
				externalReferenceCode, randomPriceModifierCategory());

		PriceModifierCategory priceModifierCategory2 =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_addPriceModifierCategory(
				externalReferenceCode, randomPriceModifierCategory());

		PriceModifierCategory priceModifierCategory3 =
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_addPriceModifierCategory(
				externalReferenceCode, randomPriceModifierCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceModifierCategory> page1 =
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceModifierCategory1,
				(List<PriceModifierCategory>)page1.getItems());

			Page<PriceModifierCategory> page2 =
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceModifierCategory2,
				(List<PriceModifierCategory>)page2.getItems());

			Page<PriceModifierCategory> page3 =
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceModifierCategory3,
				(List<PriceModifierCategory>)page3.getItems());
		}
		else {
			Page<PriceModifierCategory> page1 =
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<PriceModifierCategory> priceModifierCategories1 =
				(List<PriceModifierCategory>)page1.getItems();

			Assert.assertEquals(
				priceModifierCategories1.toString(), totalCount + 2,
				priceModifierCategories1.size());

			Page<PriceModifierCategory> page2 =
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceModifierCategory> priceModifierCategories2 =
				(List<PriceModifierCategory>)page2.getItems();

			Assert.assertEquals(
				priceModifierCategories2.toString(), 1,
				priceModifierCategories2.size());

			Page<PriceModifierCategory> page3 =
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				priceModifierCategory1,
				(List<PriceModifierCategory>)page3.getItems());
			assertContains(
				priceModifierCategory2,
				(List<PriceModifierCategory>)page3.getItems());
			assertContains(
				priceModifierCategory3,
				(List<PriceModifierCategory>)page3.getItems());
		}
	}

	protected PriceModifierCategory
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_addPriceModifierCategory(
				String externalReferenceCode,
				PriceModifierCategory priceModifierCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceModifierByExternalReferenceCodePriceModifierCategoriesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPage()
		throws Exception {

		Long id = testGetPriceModifierIdPriceModifierCategoriesPage_getId();
		Long irrelevantId =
			testGetPriceModifierIdPriceModifierCategoriesPage_getIrrelevantId();

		Page<PriceModifierCategory> page =
			priceModifierCategoryResource.
				getPriceModifierIdPriceModifierCategoriesPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PriceModifierCategory irrelevantPriceModifierCategory =
				testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
					irrelevantId, randomIrrelevantPriceModifierCategory());

			page =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceModifierCategory,
				(List<PriceModifierCategory>)page.getItems());
			assertValid(
				page,
				testGetPriceModifierIdPriceModifierCategoriesPage_getExpectedActions(
					irrelevantId));
		}

		PriceModifierCategory priceModifierCategory1 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, randomPriceModifierCategory());

		PriceModifierCategory priceModifierCategory2 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, randomPriceModifierCategory());

		page =
			priceModifierCategoryResource.
				getPriceModifierIdPriceModifierCategoriesPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceModifierCategory1,
			(List<PriceModifierCategory>)page.getItems());
		assertContains(
			priceModifierCategory2,
			(List<PriceModifierCategory>)page.getItems());
		assertValid(
			page,
			testGetPriceModifierIdPriceModifierCategoriesPage_getExpectedActions(
				id));

		priceModifierCategoryResource.deletePriceModifierCategory(
			priceModifierCategory1.getPriceModifierCategoryId());

		priceModifierCategoryResource.deletePriceModifierCategory(
			priceModifierCategory2.getPriceModifierCategoryId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceModifierIdPriceModifierCategoriesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceModifierIdPriceModifierCategoriesPage_getId();

		PriceModifierCategory priceModifierCategory1 =
			randomPriceModifierCategory();

		priceModifierCategory1 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, priceModifierCategory1);

		for (EntityField entityField : entityFields) {
			Page<PriceModifierCategory> page =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null,
						getFilterString(
							entityField, "between", priceModifierCategory1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceModifierCategory1),
				(List<PriceModifierCategory>)page.getItems());
		}
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithFilterStringContains()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithFilterStringEquals()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetPriceModifierIdPriceModifierCategoriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceModifierIdPriceModifierCategoriesPage_getId();

		PriceModifierCategory priceModifierCategory1 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, randomPriceModifierCategory());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceModifierCategory priceModifierCategory2 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, randomPriceModifierCategory());

		for (EntityField entityField : entityFields) {
			Page<PriceModifierCategory> page =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null,
						getFilterString(
							entityField, operator, priceModifierCategory1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceModifierCategory1),
				(List<PriceModifierCategory>)page.getItems());
		}
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithPagination()
		throws Exception {

		Long id = testGetPriceModifierIdPriceModifierCategoriesPage_getId();

		Page<PriceModifierCategory> priceModifierCategoriesPage =
			priceModifierCategoryResource.
				getPriceModifierIdPriceModifierCategoriesPage(
					id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			priceModifierCategoriesPage.getTotalCount());

		PriceModifierCategory priceModifierCategory1 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, randomPriceModifierCategory());

		PriceModifierCategory priceModifierCategory2 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, randomPriceModifierCategory());

		PriceModifierCategory priceModifierCategory3 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, randomPriceModifierCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceModifierCategory> page1 =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceModifierCategory1,
				(List<PriceModifierCategory>)page1.getItems());

			Page<PriceModifierCategory> page2 =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				priceModifierCategory2,
				(List<PriceModifierCategory>)page2.getItems());

			Page<PriceModifierCategory> page3 =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				priceModifierCategory3,
				(List<PriceModifierCategory>)page3.getItems());
		}
		else {
			Page<PriceModifierCategory> page1 =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PriceModifierCategory> priceModifierCategories1 =
				(List<PriceModifierCategory>)page1.getItems();

			Assert.assertEquals(
				priceModifierCategories1.toString(), totalCount + 2,
				priceModifierCategories1.size());

			Page<PriceModifierCategory> page2 =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceModifierCategory> priceModifierCategories2 =
				(List<PriceModifierCategory>)page2.getItems();

			Assert.assertEquals(
				priceModifierCategories2.toString(), 1,
				priceModifierCategories2.size());

			Page<PriceModifierCategory> page3 =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				priceModifierCategory1,
				(List<PriceModifierCategory>)page3.getItems());
			assertContains(
				priceModifierCategory2,
				(List<PriceModifierCategory>)page3.getItems());
			assertContains(
				priceModifierCategory3,
				(List<PriceModifierCategory>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithSortDateTime()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceModifierCategory1, priceModifierCategory2) -> {
				BeanTestUtil.setProperty(
					priceModifierCategory1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithSortDouble()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceModifierCategory1, priceModifierCategory2) -> {
				BeanTestUtil.setProperty(
					priceModifierCategory1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceModifierCategory2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithSortInteger()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceModifierCategory1, priceModifierCategory2) -> {
				BeanTestUtil.setProperty(
					priceModifierCategory1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					priceModifierCategory2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceModifierIdPriceModifierCategoriesPageWithSortString()
		throws Exception {

		testGetPriceModifierIdPriceModifierCategoriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceModifierCategory1, priceModifierCategory2) -> {
				Class<?> clazz = priceModifierCategory1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceModifierCategory1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceModifierCategory2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceModifierCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceModifierCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceModifierCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceModifierCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPriceModifierIdPriceModifierCategoriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PriceModifierCategory, PriceModifierCategory,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceModifierIdPriceModifierCategoriesPage_getId();

		PriceModifierCategory priceModifierCategory1 =
			randomPriceModifierCategory();
		PriceModifierCategory priceModifierCategory2 =
			randomPriceModifierCategory();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, priceModifierCategory1, priceModifierCategory2);
		}

		priceModifierCategory1 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, priceModifierCategory1);

		priceModifierCategory2 =
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				id, priceModifierCategory2);

		Page<PriceModifierCategory> page =
			priceModifierCategoryResource.
				getPriceModifierIdPriceModifierCategoriesPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceModifierCategory> ascPage =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				priceModifierCategory1,
				(List<PriceModifierCategory>)ascPage.getItems());
			assertContains(
				priceModifierCategory2,
				(List<PriceModifierCategory>)ascPage.getItems());

			Page<PriceModifierCategory> descPage =
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				priceModifierCategory2,
				(List<PriceModifierCategory>)descPage.getItems());
			assertContains(
				priceModifierCategory1,
				(List<PriceModifierCategory>)descPage.getItems());
		}
	}

	protected PriceModifierCategory
			testGetPriceModifierIdPriceModifierCategoriesPage_addPriceModifierCategory(
				Long id, PriceModifierCategory priceModifierCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceModifierIdPriceModifierCategoriesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPriceModifierIdPriceModifierCategoriesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostPriceModifierByExternalReferenceCodePriceModifierCategory()
		throws Exception {

		PriceModifierCategory randomPriceModifierCategory =
			randomPriceModifierCategory();

		PriceModifierCategory postPriceModifierCategory =
			testPostPriceModifierByExternalReferenceCodePriceModifierCategory_addPriceModifierCategory(
				randomPriceModifierCategory);

		assertEquals(randomPriceModifierCategory, postPriceModifierCategory);
		assertValid(postPriceModifierCategory);
	}

	protected PriceModifierCategory
			testPostPriceModifierByExternalReferenceCodePriceModifierCategory_addPriceModifierCategory(
				PriceModifierCategory priceModifierCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceModifierIdPriceModifierCategory()
		throws Exception {

		PriceModifierCategory randomPriceModifierCategory =
			randomPriceModifierCategory();

		PriceModifierCategory postPriceModifierCategory =
			testPostPriceModifierIdPriceModifierCategory_addPriceModifierCategory(
				randomPriceModifierCategory);

		assertEquals(randomPriceModifierCategory, postPriceModifierCategory);
		assertValid(postPriceModifierCategory);
	}

	protected PriceModifierCategory
			testPostPriceModifierIdPriceModifierCategory_addPriceModifierCategory(
				PriceModifierCategory priceModifierCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PriceModifierCategory priceModifierCategory1 =
			testBatchEngineDeleteImportTask_addPriceModifierCategory();

		testBatchEngineDeleteImportTask_deletePriceModifierCategory(
			200, null, priceModifierCategory1.getPriceModifierCategoryId());
	}

	protected PriceModifierCategory
			testBatchEngineDeleteImportTask_addPriceModifierCategory()
		throws Exception {

		return testDeletePriceModifierCategory_addPriceModifierCategory();
	}

	protected void testBatchEngineDeleteImportTask_deletePriceModifierCategory(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierCategory",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"priceModifierCategoryId", () -> id
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

	protected PriceModifierCategory
			testGraphQLPriceModifierCategory_addPriceModifierCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PriceModifierCategory priceModifierCategory,
		List<PriceModifierCategory> priceModifierCategories) {

		boolean contains = false;

		for (PriceModifierCategory item : priceModifierCategories) {
			if (equals(priceModifierCategory, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			priceModifierCategories + " does not contain " +
				priceModifierCategory,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PriceModifierCategory priceModifierCategory1,
		PriceModifierCategory priceModifierCategory2) {

		Assert.assertTrue(
			priceModifierCategory1 + " does not equal " +
				priceModifierCategory2,
			equals(priceModifierCategory1, priceModifierCategory2));
	}

	protected void assertEquals(
		List<PriceModifierCategory> priceModifierCategories1,
		List<PriceModifierCategory> priceModifierCategories2) {

		Assert.assertEquals(
			priceModifierCategories1.size(), priceModifierCategories2.size());

		for (int i = 0; i < priceModifierCategories1.size(); i++) {
			PriceModifierCategory priceModifierCategory1 =
				priceModifierCategories1.get(i);
			PriceModifierCategory priceModifierCategory2 =
				priceModifierCategories2.get(i);

			assertEquals(priceModifierCategory1, priceModifierCategory2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PriceModifierCategory> priceModifierCategories1,
		List<PriceModifierCategory> priceModifierCategories2) {

		Assert.assertEquals(
			priceModifierCategories1.size(), priceModifierCategories2.size());

		for (PriceModifierCategory priceModifierCategory1 :
				priceModifierCategories1) {

			boolean contains = false;

			for (PriceModifierCategory priceModifierCategory2 :
					priceModifierCategories2) {

				if (equals(priceModifierCategory1, priceModifierCategory2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				priceModifierCategories2 + " does not contain " +
					priceModifierCategory1,
				contains);
		}
	}

	protected void assertValid(PriceModifierCategory priceModifierCategory)
		throws Exception {

		boolean valid = true;

		if (priceModifierCategory.getPriceModifierCategoryId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (priceModifierCategory.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("category", additionalAssertFieldName)) {
				if (priceModifierCategory.getCategory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"categoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceModifierCategory.getCategoryExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("categoryId", additionalAssertFieldName)) {
				if (priceModifierCategory.getCategoryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierCategoryId", additionalAssertFieldName)) {

				if (priceModifierCategory.getPriceModifierCategoryId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceModifierCategory.
						getPriceModifierExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceModifierId", additionalAssertFieldName)) {
				if (priceModifierCategory.getPriceModifierId() == null) {
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

	protected void assertValid(Page<PriceModifierCategory> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PriceModifierCategory> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PriceModifierCategory> priceModifierCategories =
			page.getItems();

		int size = priceModifierCategories.size();

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

		graphQLFields.add(new GraphQLField("priceModifierCategoryId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						PriceModifierCategory.class)) {

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
		PriceModifierCategory priceModifierCategory1,
		PriceModifierCategory priceModifierCategory2) {

		if (priceModifierCategory1 == priceModifierCategory2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceModifierCategory1.getActions(),
						(Map)priceModifierCategory2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("category", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifierCategory1.getCategory(),
						priceModifierCategory2.getCategory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"categoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifierCategory1.
							getCategoryExternalReferenceCode(),
						priceModifierCategory2.
							getCategoryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("categoryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifierCategory1.getCategoryId(),
						priceModifierCategory2.getCategoryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierCategoryId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifierCategory1.getPriceModifierCategoryId(),
						priceModifierCategory2.getPriceModifierCategoryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifierCategory1.
							getPriceModifierExternalReferenceCode(),
						priceModifierCategory2.
							getPriceModifierExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceModifierId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifierCategory1.getPriceModifierId(),
						priceModifierCategory2.getPriceModifierId())) {

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

		if (!(_priceModifierCategoryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_priceModifierCategoryResource;

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
		PriceModifierCategory priceModifierCategory) {

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

		if (entityFieldName.equals("category")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("categoryExternalReferenceCode")) {
			Object object =
				priceModifierCategory.getCategoryExternalReferenceCode();

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

		if (entityFieldName.equals("categoryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceModifierCategoryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceModifierExternalReferenceCode")) {
			Object object =
				priceModifierCategory.getPriceModifierExternalReferenceCode();

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

	protected PriceModifierCategory randomPriceModifierCategory()
		throws Exception {

		return new PriceModifierCategory() {
			{
				categoryExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				categoryId = RandomTestUtil.randomLong();
				priceModifierCategoryId = RandomTestUtil.randomLong();
				priceModifierExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceModifierId = RandomTestUtil.randomLong();
			}
		};
	}

	protected PriceModifierCategory randomIrrelevantPriceModifierCategory()
		throws Exception {

		PriceModifierCategory randomIrrelevantPriceModifierCategory =
			randomPriceModifierCategory();

		return randomIrrelevantPriceModifierCategory;
	}

	protected PriceModifierCategory randomPatchPriceModifierCategory()
		throws Exception {

		return randomPriceModifierCategory();
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

	protected PriceModifierCategoryResource priceModifierCategoryResource;
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
		LogFactoryUtil.getLog(BasePriceModifierCategoryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		PriceModifierCategoryResource _priceModifierCategoryResource;

}