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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountCategory;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.DiscountCategoryResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.DiscountCategorySerDes;
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
public abstract class BaseDiscountCategoryResourceTestCase {

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

		_discountCategoryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		discountCategoryResource = DiscountCategoryResource.builder(
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

		DiscountCategory discountCategory1 = randomDiscountCategory();

		String json = objectMapper.writeValueAsString(discountCategory1);

		DiscountCategory discountCategory2 = DiscountCategorySerDes.toDTO(json);

		Assert.assertTrue(equals(discountCategory1, discountCategory2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DiscountCategory discountCategory = randomDiscountCategory();

		String json1 = objectMapper.writeValueAsString(discountCategory);
		String json2 = DiscountCategorySerDes.toJSON(discountCategory);

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

		DiscountCategory discountCategory = randomDiscountCategory();

		discountCategory.setCategoryExternalReferenceCode(regex);
		discountCategory.setDiscountExternalReferenceCode(regex);

		String json = DiscountCategorySerDes.toJSON(discountCategory);

		Assert.assertFalse(json.contains(regex));

		discountCategory = DiscountCategorySerDes.toDTO(json);

		Assert.assertEquals(
			regex, discountCategory.getCategoryExternalReferenceCode());
		Assert.assertEquals(
			regex, discountCategory.getDiscountExternalReferenceCode());
	}

	@Test
	public void testDeleteDiscountCategory() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DiscountCategory discountCategory =
			testDeleteDiscountCategory_addDiscountCategory();

		assertHttpResponseStatusCode(
			204,
			discountCategoryResource.deleteDiscountCategoryHttpResponse(
				discountCategory.getDiscountCategoryId()));
	}

	protected DiscountCategory testDeleteDiscountCategory_addDiscountCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDiscountCategory() throws Exception {

		// No namespace

		DiscountCategory discountCategory1 =
			testGraphQLDeleteDiscountCategory_addDiscountCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDiscountCategory",
						new HashMap<String, Object>() {
							{
								put(
									"discountCategoryId",
									discountCategory1.getDiscountCategoryId());
							}
						})),
				"JSONObject/data", "Object/deleteDiscountCategory"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		DiscountCategory discountCategory2 =
			testGraphQLDeleteDiscountCategory_addDiscountCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deleteDiscountCategory",
							new HashMap<String, Object>() {
								{
									put(
										"discountCategoryId",
										discountCategory2.
											getDiscountCategoryId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deleteDiscountCategory"));
	}

	protected DiscountCategory
			testGraphQLDeleteDiscountCategory_addDiscountCategory()
		throws Exception {

		return testGraphQLDiscountCategory_addDiscountCategory();
	}

	@Test
	public void testDeleteDiscountCategoryBatch() throws Exception {
		DiscountCategory discountCategory1 =
			testDeleteDiscountCategoryBatch_addDiscountCategory();

		testDeleteDiscountCategoryBatch_deleteDiscountCategory(
			202, null, discountCategory1.getDiscountCategoryId());
	}

	protected DiscountCategory
			testDeleteDiscountCategoryBatch_addDiscountCategory()
		throws Exception {

		return testDeleteDiscountCategory_addDiscountCategory();
	}

	protected void testDeleteDiscountCategoryBatch_deleteDiscountCategory(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			discountCategoryResource.deleteDiscountCategoryBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"discountCategoryId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountCategoriesPage()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getIrrelevantExternalReferenceCode();

		Page<DiscountCategory> page =
			discountCategoryResource.
				getDiscountByExternalReferenceCodeDiscountCategoriesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			DiscountCategory irrelevantDiscountCategory =
				testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_addDiscountCategory(
					irrelevantExternalReferenceCode,
					randomIrrelevantDiscountCategory());

			page =
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountCategory,
				(List<DiscountCategory>)page.getItems());
			assertValid(
				page,
				testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		DiscountCategory discountCategory1 =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_addDiscountCategory(
				externalReferenceCode, randomDiscountCategory());

		DiscountCategory discountCategory2 =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_addDiscountCategory(
				externalReferenceCode, randomDiscountCategory());

		page =
			discountCategoryResource.
				getDiscountByExternalReferenceCodeDiscountCategoriesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			discountCategory1, (List<DiscountCategory>)page.getItems());
		assertContains(
			discountCategory2, (List<DiscountCategory>)page.getItems());
		assertValid(
			page,
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getExpectedActions(
				externalReferenceCode));

		discountCategoryResource.deleteDiscountCategory(
			discountCategory1.getDiscountCategoryId());

		discountCategoryResource.deleteDiscountCategory(
			discountCategory2.getDiscountCategoryId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountCategoriesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getExternalReferenceCode();

		Page<DiscountCategory> discountCategoriesPage =
			discountCategoryResource.
				getDiscountByExternalReferenceCodeDiscountCategoriesPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			discountCategoriesPage.getTotalCount());

		DiscountCategory discountCategory1 =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_addDiscountCategory(
				externalReferenceCode, randomDiscountCategory());

		DiscountCategory discountCategory2 =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_addDiscountCategory(
				externalReferenceCode, randomDiscountCategory());

		DiscountCategory discountCategory3 =
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_addDiscountCategory(
				externalReferenceCode, randomDiscountCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountCategory> page1 =
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				discountCategory1, (List<DiscountCategory>)page1.getItems());

			Page<DiscountCategory> page2 =
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountCategory2, (List<DiscountCategory>)page2.getItems());

			Page<DiscountCategory> page3 =
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountCategory3, (List<DiscountCategory>)page3.getItems());
		}
		else {
			Page<DiscountCategory> page1 =
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<DiscountCategory> discountCategories1 =
				(List<DiscountCategory>)page1.getItems();

			Assert.assertEquals(
				discountCategories1.toString(), totalCount + 2,
				discountCategories1.size());

			Page<DiscountCategory> page2 =
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountCategory> discountCategories2 =
				(List<DiscountCategory>)page2.getItems();

			Assert.assertEquals(
				discountCategories2.toString(), 1, discountCategories2.size());

			Page<DiscountCategory> page3 =
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				discountCategory1, (List<DiscountCategory>)page3.getItems());
			assertContains(
				discountCategory2, (List<DiscountCategory>)page3.getItems());
			assertContains(
				discountCategory3, (List<DiscountCategory>)page3.getItems());
		}
	}

	protected DiscountCategory
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_addDiscountCategory(
				String externalReferenceCode, DiscountCategory discountCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountCategoriesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPage() throws Exception {
		Long id = testGetDiscountIdDiscountCategoriesPage_getId();
		Long irrelevantId =
			testGetDiscountIdDiscountCategoriesPage_getIrrelevantId();

		Page<DiscountCategory> page =
			discountCategoryResource.getDiscountIdDiscountCategoriesPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			DiscountCategory irrelevantDiscountCategory =
				testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
					irrelevantId, randomIrrelevantDiscountCategory());

			page = discountCategoryResource.getDiscountIdDiscountCategoriesPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountCategory,
				(List<DiscountCategory>)page.getItems());
			assertValid(
				page,
				testGetDiscountIdDiscountCategoriesPage_getExpectedActions(
					irrelevantId));
		}

		DiscountCategory discountCategory1 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, randomDiscountCategory());

		DiscountCategory discountCategory2 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, randomDiscountCategory());

		page = discountCategoryResource.getDiscountIdDiscountCategoriesPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			discountCategory1, (List<DiscountCategory>)page.getItems());
		assertContains(
			discountCategory2, (List<DiscountCategory>)page.getItems());
		assertValid(
			page,
			testGetDiscountIdDiscountCategoriesPage_getExpectedActions(id));

		discountCategoryResource.deleteDiscountCategory(
			discountCategory1.getDiscountCategoryId());

		discountCategoryResource.deleteDiscountCategory(
			discountCategory2.getDiscountCategoryId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountIdDiscountCategoriesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountCategoriesPage_getId();

		DiscountCategory discountCategory1 = randomDiscountCategory();

		discountCategory1 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, discountCategory1);

		for (EntityField entityField : entityFields) {
			Page<DiscountCategory> page =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null,
					getFilterString(entityField, "between", discountCategory1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discountCategory1),
				(List<DiscountCategory>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithFilterStringContains()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithFilterStringEquals()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetDiscountIdDiscountCategoriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountCategoriesPage_getId();

		DiscountCategory discountCategory1 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, randomDiscountCategory());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DiscountCategory discountCategory2 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, randomDiscountCategory());

		for (EntityField entityField : entityFields) {
			Page<DiscountCategory> page =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null,
					getFilterString(entityField, operator, discountCategory1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discountCategory1),
				(List<DiscountCategory>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithPagination()
		throws Exception {

		Long id = testGetDiscountIdDiscountCategoriesPage_getId();

		Page<DiscountCategory> discountCategoriesPage =
			discountCategoryResource.getDiscountIdDiscountCategoriesPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			discountCategoriesPage.getTotalCount());

		DiscountCategory discountCategory1 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, randomDiscountCategory());

		DiscountCategory discountCategory2 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, randomDiscountCategory());

		DiscountCategory discountCategory3 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, randomDiscountCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountCategory> page1 =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				discountCategory1, (List<DiscountCategory>)page1.getItems());

			Page<DiscountCategory> page2 =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				discountCategory2, (List<DiscountCategory>)page2.getItems());

			Page<DiscountCategory> page3 =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				discountCategory3, (List<DiscountCategory>)page3.getItems());
		}
		else {
			Page<DiscountCategory> page1 =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<DiscountCategory> discountCategories1 =
				(List<DiscountCategory>)page1.getItems();

			Assert.assertEquals(
				discountCategories1.toString(), totalCount + 2,
				discountCategories1.size());

			Page<DiscountCategory> page2 =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountCategory> discountCategories2 =
				(List<DiscountCategory>)page2.getItems();

			Assert.assertEquals(
				discountCategories2.toString(), 1, discountCategories2.size());

			Page<DiscountCategory> page3 =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				discountCategory1, (List<DiscountCategory>)page3.getItems());
			assertContains(
				discountCategory2, (List<DiscountCategory>)page3.getItems());
			assertContains(
				discountCategory3, (List<DiscountCategory>)page3.getItems());
		}
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithSortDateTime()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, discountCategory1, discountCategory2) -> {
				BeanTestUtil.setProperty(
					discountCategory1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithSortDouble()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, discountCategory1, discountCategory2) -> {
				BeanTestUtil.setProperty(
					discountCategory1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					discountCategory2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithSortInteger()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, discountCategory1, discountCategory2) -> {
				BeanTestUtil.setProperty(
					discountCategory1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					discountCategory2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDiscountIdDiscountCategoriesPageWithSortString()
		throws Exception {

		testGetDiscountIdDiscountCategoriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, discountCategory1, discountCategory2) -> {
				Class<?> clazz = discountCategory1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						discountCategory1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						discountCategory2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						discountCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						discountCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						discountCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						discountCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDiscountIdDiscountCategoriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DiscountCategory, DiscountCategory, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetDiscountIdDiscountCategoriesPage_getId();

		DiscountCategory discountCategory1 = randomDiscountCategory();
		DiscountCategory discountCategory2 = randomDiscountCategory();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, discountCategory1, discountCategory2);
		}

		discountCategory1 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, discountCategory1);

		discountCategory2 =
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				id, discountCategory2);

		Page<DiscountCategory> page =
			discountCategoryResource.getDiscountIdDiscountCategoriesPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DiscountCategory> ascPage =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				discountCategory1, (List<DiscountCategory>)ascPage.getItems());
			assertContains(
				discountCategory2, (List<DiscountCategory>)ascPage.getItems());

			Page<DiscountCategory> descPage =
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				discountCategory2, (List<DiscountCategory>)descPage.getItems());
			assertContains(
				discountCategory1, (List<DiscountCategory>)descPage.getItems());
		}
	}

	protected DiscountCategory
			testGetDiscountIdDiscountCategoriesPage_addDiscountCategory(
				Long id, DiscountCategory discountCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountCategoriesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountCategoriesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostDiscountByExternalReferenceCodeDiscountCategory()
		throws Exception {

		DiscountCategory randomDiscountCategory = randomDiscountCategory();

		DiscountCategory postDiscountCategory =
			testPostDiscountByExternalReferenceCodeDiscountCategory_addDiscountCategory(
				randomDiscountCategory);

		assertEquals(randomDiscountCategory, postDiscountCategory);
		assertValid(postDiscountCategory);
	}

	protected DiscountCategory
			testPostDiscountByExternalReferenceCodeDiscountCategory_addDiscountCategory(
				DiscountCategory discountCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDiscountIdDiscountCategory() throws Exception {
		DiscountCategory randomDiscountCategory = randomDiscountCategory();

		DiscountCategory postDiscountCategory =
			testPostDiscountIdDiscountCategory_addDiscountCategory(
				randomDiscountCategory);

		assertEquals(randomDiscountCategory, postDiscountCategory);
		assertValid(postDiscountCategory);
	}

	protected DiscountCategory
			testPostDiscountIdDiscountCategory_addDiscountCategory(
				DiscountCategory discountCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DiscountCategory discountCategory1 =
			testBatchEngineDeleteImportTask_addDiscountCategory();

		testBatchEngineDeleteImportTask_deleteDiscountCategory(
			200, null, discountCategory1.getDiscountCategoryId());
	}

	protected DiscountCategory
			testBatchEngineDeleteImportTask_addDiscountCategory()
		throws Exception {

		return testDeleteDiscountCategory_addDiscountCategory();
	}

	protected void testBatchEngineDeleteImportTask_deleteDiscountCategory(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountCategory",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"discountCategoryId", () -> id
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

	protected DiscountCategory testGraphQLDiscountCategory_addDiscountCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DiscountCategory discountCategory,
		List<DiscountCategory> discountCategories) {

		boolean contains = false;

		for (DiscountCategory item : discountCategories) {
			if (equals(discountCategory, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			discountCategories + " does not contain " + discountCategory,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DiscountCategory discountCategory1,
		DiscountCategory discountCategory2) {

		Assert.assertTrue(
			discountCategory1 + " does not equal " + discountCategory2,
			equals(discountCategory1, discountCategory2));
	}

	protected void assertEquals(
		List<DiscountCategory> discountCategories1,
		List<DiscountCategory> discountCategories2) {

		Assert.assertEquals(
			discountCategories1.size(), discountCategories2.size());

		for (int i = 0; i < discountCategories1.size(); i++) {
			DiscountCategory discountCategory1 = discountCategories1.get(i);
			DiscountCategory discountCategory2 = discountCategories2.get(i);

			assertEquals(discountCategory1, discountCategory2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DiscountCategory> discountCategories1,
		List<DiscountCategory> discountCategories2) {

		Assert.assertEquals(
			discountCategories1.size(), discountCategories2.size());

		for (DiscountCategory discountCategory1 : discountCategories1) {
			boolean contains = false;

			for (DiscountCategory discountCategory2 : discountCategories2) {
				if (equals(discountCategory1, discountCategory2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				discountCategories2 + " does not contain " + discountCategory1,
				contains);
		}
	}

	protected void assertValid(DiscountCategory discountCategory)
		throws Exception {

		boolean valid = true;

		if (discountCategory.getDiscountCategoryId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (discountCategory.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("category", additionalAssertFieldName)) {
				if (discountCategory.getCategory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"categoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (discountCategory.getCategoryExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("categoryId", additionalAssertFieldName)) {
				if (discountCategory.getCategoryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountCategoryId", additionalAssertFieldName)) {

				if (discountCategory.getDiscountCategoryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (discountCategory.getDiscountExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (discountCategory.getDiscountId() == null) {
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

	protected void assertValid(Page<DiscountCategory> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DiscountCategory> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DiscountCategory> discountCategories =
			page.getItems();

		int size = discountCategories.size();

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

		graphQLFields.add(new GraphQLField("discountCategoryId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						DiscountCategory.class)) {

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
		DiscountCategory discountCategory1,
		DiscountCategory discountCategory2) {

		if (discountCategory1 == discountCategory2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)discountCategory1.getActions(),
						(Map)discountCategory2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("category", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountCategory1.getCategory(),
						discountCategory2.getCategory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"categoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountCategory1.getCategoryExternalReferenceCode(),
						discountCategory2.getCategoryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("categoryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountCategory1.getCategoryId(),
						discountCategory2.getCategoryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountCategoryId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountCategory1.getDiscountCategoryId(),
						discountCategory2.getDiscountCategoryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountCategory1.getDiscountExternalReferenceCode(),
						discountCategory2.getDiscountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountCategory1.getDiscountId(),
						discountCategory2.getDiscountId())) {

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

		if (!(_discountCategoryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_discountCategoryResource;

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
		DiscountCategory discountCategory) {

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
			Object object = discountCategory.getCategoryExternalReferenceCode();

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

		if (entityFieldName.equals("discountCategoryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountExternalReferenceCode")) {
			Object object = discountCategory.getDiscountExternalReferenceCode();

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

	protected DiscountCategory randomDiscountCategory() throws Exception {
		return new DiscountCategory() {
			{
				categoryExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				categoryId = RandomTestUtil.randomLong();
				discountCategoryId = RandomTestUtil.randomLong();
				discountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				discountId = RandomTestUtil.randomLong();
			}
		};
	}

	protected DiscountCategory randomIrrelevantDiscountCategory()
		throws Exception {

		DiscountCategory randomIrrelevantDiscountCategory =
			randomDiscountCategory();

		return randomIrrelevantDiscountCategory;
	}

	protected DiscountCategory randomPatchDiscountCategory() throws Exception {
		return randomDiscountCategory();
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

	protected DiscountCategoryResource discountCategoryResource;
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
		LogFactoryUtil.getLog(BaseDiscountCategoryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		DiscountCategoryResource _discountCategoryResource;

}