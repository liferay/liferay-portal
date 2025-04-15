/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.client.http.HttpInvoker;
import com.liferay.headless.admin.taxonomy.client.pagination.Page;
import com.liferay.headless.admin.taxonomy.client.pagination.Pagination;
import com.liferay.headless.admin.taxonomy.client.permission.Permission;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.admin.taxonomy.client.serdes.v1_0.TaxonomyCategorySerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseTaxonomyCategoryResourceTestCase {

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

		_taxonomyCategoryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		taxonomyCategoryResource = TaxonomyCategoryResource.builder(
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

		permissionsTaxonomyCategoryResource = TaxonomyCategoryResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameter(
			"nestedFields", "permissions"
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

		TaxonomyCategory taxonomyCategory1 = randomTaxonomyCategory();

		String json = objectMapper.writeValueAsString(taxonomyCategory1);

		TaxonomyCategory taxonomyCategory2 = TaxonomyCategorySerDes.toDTO(json);

		Assert.assertTrue(equals(taxonomyCategory1, taxonomyCategory2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		TaxonomyCategory taxonomyCategory = randomTaxonomyCategory();

		String json1 = objectMapper.writeValueAsString(taxonomyCategory);
		String json2 = TaxonomyCategorySerDes.toJSON(taxonomyCategory);

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

		TaxonomyCategory taxonomyCategory = randomTaxonomyCategory();

		taxonomyCategory.setDescription(regex);
		taxonomyCategory.setExternalReferenceCode(regex);
		taxonomyCategory.setId(regex);
		taxonomyCategory.setName(regex);
		taxonomyCategory.setSiteExternalReferenceCode(regex);

		String json = TaxonomyCategorySerDes.toJSON(taxonomyCategory);

		Assert.assertFalse(json.contains(regex));

		taxonomyCategory = TaxonomyCategorySerDes.toDTO(json);

		Assert.assertEquals(regex, taxonomyCategory.getDescription());
		Assert.assertEquals(regex, taxonomyCategory.getExternalReferenceCode());
		Assert.assertEquals(regex, taxonomyCategory.getId());
		Assert.assertEquals(regex, taxonomyCategory.getName());
		Assert.assertEquals(
			regex, taxonomyCategory.getSiteExternalReferenceCode());
	}

	@Test
	public void testGetTaxonomyCategoriesRankedPage() throws Exception {
		Page<TaxonomyCategory> page =
			taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
				null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				randomTaxonomyCategory());

		page = taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
			null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			taxonomyCategory1, (List<TaxonomyCategory>)page.getItems());
		assertContains(
			taxonomyCategory2, (List<TaxonomyCategory>)page.getItems());
		assertValid(
			page, testGetTaxonomyCategoriesRankedPage_getExpectedActions());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory1.getId());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetTaxonomyCategoriesRankedPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetTaxonomyCategoriesRankedPageWithPagination()
		throws Exception {

		Page<TaxonomyCategory> taxonomyCategoryPage =
			taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
				null, null);

		int totalCount = GetterUtil.getInteger(
			taxonomyCategoryPage.getTotalCount());

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory3 =
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				randomTaxonomyCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TaxonomyCategory> page1 =
				taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)page1.getItems());

			Page<TaxonomyCategory> page2 =
				taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)page2.getItems());

			Page<TaxonomyCategory> page3 =
				taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				taxonomyCategory3, (List<TaxonomyCategory>)page3.getItems());
		}
		else {
			Page<TaxonomyCategory> page1 =
				taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
					null, Pagination.of(1, totalCount + 2));

			List<TaxonomyCategory> taxonomyCategories1 =
				(List<TaxonomyCategory>)page1.getItems();

			Assert.assertEquals(
				taxonomyCategories1.toString(), totalCount + 2,
				taxonomyCategories1.size());

			Page<TaxonomyCategory> page2 =
				taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
					null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TaxonomyCategory> taxonomyCategories2 =
				(List<TaxonomyCategory>)page2.getItems();

			Assert.assertEquals(
				taxonomyCategories2.toString(), 1, taxonomyCategories2.size());

			Page<TaxonomyCategory> page3 =
				taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
					null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)page3.getItems());
			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)page3.getItems());
			assertContains(
				taxonomyCategory3, (List<TaxonomyCategory>)page3.getItems());
		}
	}

	protected TaxonomyCategory
			testGetTaxonomyCategoriesRankedPage_addTaxonomyCategory(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPage()
		throws Exception {

		String parentTaxonomyCategoryId =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId();
		String irrelevantParentTaxonomyCategoryId =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getIrrelevantParentTaxonomyCategoryId();

		Page<TaxonomyCategory> page =
			taxonomyCategoryResource.getTaxonomyCategoryTaxonomyCategoriesPage(
				parentTaxonomyCategoryId, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantParentTaxonomyCategoryId != null) {
			TaxonomyCategory irrelevantTaxonomyCategory =
				testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
					irrelevantParentTaxonomyCategoryId,
					randomIrrelevantTaxonomyCategory());

			page =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						irrelevantParentTaxonomyCategoryId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantTaxonomyCategory,
				(List<TaxonomyCategory>)page.getItems());
			assertValid(
				page,
				testGetTaxonomyCategoryTaxonomyCategoriesPage_getExpectedActions(
					irrelevantParentTaxonomyCategoryId));
		}

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, randomTaxonomyCategory());

		page =
			taxonomyCategoryResource.getTaxonomyCategoryTaxonomyCategoriesPage(
				parentTaxonomyCategoryId, null, null, null,
				Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			taxonomyCategory1, (List<TaxonomyCategory>)page.getItems());
		assertContains(
			taxonomyCategory2, (List<TaxonomyCategory>)page.getItems());
		assertValid(
			page,
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getExpectedActions(
				parentTaxonomyCategoryId));

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory1.getId());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getExpectedActions(
				String parentTaxonomyCategoryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String parentTaxonomyCategoryId =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId();

		TaxonomyCategory taxonomyCategory1 = randomTaxonomyCategory();

		taxonomyCategory1 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, taxonomyCategory1);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyCategory> page =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null,
						getFilterString(
							entityField, "between", taxonomyCategory1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyCategory1),
				(List<TaxonomyCategory>)page.getItems());
		}
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilterStringContains()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilterStringEquals()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetTaxonomyCategoryTaxonomyCategoriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String parentTaxonomyCategoryId =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId();

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, randomTaxonomyCategory());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, randomTaxonomyCategory());

		for (EntityField entityField : entityFields) {
			Page<TaxonomyCategory> page =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null,
						getFilterString(
							entityField, operator, taxonomyCategory1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyCategory1),
				(List<TaxonomyCategory>)page.getItems());
		}
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithPagination()
		throws Exception {

		String parentTaxonomyCategoryId =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId();

		Page<TaxonomyCategory> taxonomyCategoryPage =
			taxonomyCategoryResource.getTaxonomyCategoryTaxonomyCategoriesPage(
				parentTaxonomyCategoryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			taxonomyCategoryPage.getTotalCount());

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory3 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, randomTaxonomyCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TaxonomyCategory> page1 =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)page1.getItems());

			Page<TaxonomyCategory> page2 =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)page2.getItems());

			Page<TaxonomyCategory> page3 =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				taxonomyCategory3, (List<TaxonomyCategory>)page3.getItems());
		}
		else {
			Page<TaxonomyCategory> page1 =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<TaxonomyCategory> taxonomyCategories1 =
				(List<TaxonomyCategory>)page1.getItems();

			Assert.assertEquals(
				taxonomyCategories1.toString(), totalCount + 2,
				taxonomyCategories1.size());

			Page<TaxonomyCategory> page2 =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TaxonomyCategory> taxonomyCategories2 =
				(List<TaxonomyCategory>)page2.getItems();

			Assert.assertEquals(
				taxonomyCategories2.toString(), 1, taxonomyCategories2.size());

			Page<TaxonomyCategory> page3 =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)page3.getItems());
			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)page3.getItems());
			assertContains(
				taxonomyCategory3, (List<TaxonomyCategory>)page3.getItems());
		}
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithSortDateTime()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				BeanTestUtil.setProperty(
					taxonomyCategory1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithSortDouble()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				BeanTestUtil.setProperty(
					taxonomyCategory1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					taxonomyCategory2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithSortInteger()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				BeanTestUtil.setProperty(
					taxonomyCategory1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					taxonomyCategory2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetTaxonomyCategoryTaxonomyCategoriesPageWithSortString()
		throws Exception {

		testGetTaxonomyCategoryTaxonomyCategoriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				Class<?> clazz = taxonomyCategory1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						taxonomyCategory1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						taxonomyCategory2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						taxonomyCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						taxonomyCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						taxonomyCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						taxonomyCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetTaxonomyCategoryTaxonomyCategoriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, TaxonomyCategory, TaxonomyCategory, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String parentTaxonomyCategoryId =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId();

		TaxonomyCategory taxonomyCategory1 = randomTaxonomyCategory();
		TaxonomyCategory taxonomyCategory2 = randomTaxonomyCategory();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, taxonomyCategory1, taxonomyCategory2);
		}

		taxonomyCategory1 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, taxonomyCategory1);

		taxonomyCategory2 =
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				parentTaxonomyCategoryId, taxonomyCategory2);

		Page<TaxonomyCategory> page =
			taxonomyCategoryResource.getTaxonomyCategoryTaxonomyCategoriesPage(
				parentTaxonomyCategoryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyCategory> ascPage =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)ascPage.getItems());
			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)ascPage.getItems());

			Page<TaxonomyCategory> descPage =
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)descPage.getItems());
			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)descPage.getItems());
		}
	}

	protected TaxonomyCategory
			testGetTaxonomyCategoryTaxonomyCategoriesPage_addTaxonomyCategory(
				String parentTaxonomyCategoryId,
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
			parentTaxonomyCategoryId, taxonomyCategory);
	}

	protected String
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getIrrelevantParentTaxonomyCategoryId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostTaxonomyCategoryTaxonomyCategory() throws Exception {
		TaxonomyCategory randomTaxonomyCategory = randomTaxonomyCategory();

		TaxonomyCategory postTaxonomyCategory =
			testPostTaxonomyCategoryTaxonomyCategory_addTaxonomyCategory(
				randomTaxonomyCategory);

		assertEquals(randomTaxonomyCategory, postTaxonomyCategory);
		assertValid(postTaxonomyCategory);
	}

	protected TaxonomyCategory
			testPostTaxonomyCategoryTaxonomyCategory_addTaxonomyCategory(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
			testGetTaxonomyCategoryTaxonomyCategoriesPage_getParentTaxonomyCategoryId(),
			taxonomyCategory);
	}

	@Test
	public void testDeleteTaxonomyCategory() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyCategory taxonomyCategory =
			testDeleteTaxonomyCategory_addTaxonomyCategory();

		assertHttpResponseStatusCode(
			204,
			taxonomyCategoryResource.deleteTaxonomyCategoryHttpResponse(
				taxonomyCategory.getId()));

		assertHttpResponseStatusCode(
			404,
			taxonomyCategoryResource.getTaxonomyCategoryHttpResponse(
				taxonomyCategory.getId()));
		assertHttpResponseStatusCode(
			404, taxonomyCategoryResource.getTaxonomyCategoryHttpResponse("-"));
	}

	protected TaxonomyCategory testDeleteTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteTaxonomyCategory() throws Exception {

		// No namespace

		TaxonomyCategory taxonomyCategory1 =
			testGraphQLDeleteTaxonomyCategory_addTaxonomyCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteTaxonomyCategory",
						new HashMap<String, Object>() {
							{
								put(
									"taxonomyCategoryId",
									"\"" + taxonomyCategory1.getId() + "\"");
							}
						})),
				"JSONObject/data", "Object/deleteTaxonomyCategory"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"taxonomyCategory",
					new HashMap<String, Object>() {
						{
							put(
								"taxonomyCategoryId",
								"\"" + taxonomyCategory1.getId() + "\"");
						}
					},
					new GraphQLField("id"))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminTaxonomy_v1_0

		TaxonomyCategory taxonomyCategory2 =
			testGraphQLDeleteTaxonomyCategory_addTaxonomyCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"deleteTaxonomyCategory",
							new HashMap<String, Object>() {
								{
									put(
										"taxonomyCategoryId",
										"\"" + taxonomyCategory2.getId() +
											"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
				"Object/deleteTaxonomyCategory"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminTaxonomy_v1_0",
					new GraphQLField(
						"taxonomyCategory",
						new HashMap<String, Object>() {
							{
								put(
									"taxonomyCategoryId",
									"\"" + taxonomyCategory2.getId() + "\"");
							}
						},
						new GraphQLField("id")))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected TaxonomyCategory
			testGraphQLDeleteTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		return testGraphQLTaxonomyCategory_addTaxonomyCategory();
	}

	@Test
	public void testDeleteTaxonomyCategoryBatch() throws Exception {
		TaxonomyCategory taxonomyCategory1 =
			testDeleteTaxonomyCategoryBatch_addTaxonomyCategory();

		testDeleteTaxonomyCategoryBatch_deleteTaxonomyCategory(
			"COMPLETED", null, taxonomyCategory1.getId());

		assertHttpResponseStatusCode(
			404,
			taxonomyCategoryResource.getTaxonomyCategoryHttpResponse(
				taxonomyCategory1.getId()));
	}

	protected TaxonomyCategory
			testDeleteTaxonomyCategoryBatch_addTaxonomyCategory()
		throws Exception {

		return testDeleteTaxonomyCategory_addTaxonomyCategory();
	}

	protected void testDeleteTaxonomyCategoryBatch_deleteTaxonomyCategory(
			String expectedExecuteStatus, String externalReferenceCode,
			String id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			taxonomyCategoryResource.deleteTaxonomyCategoryBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(202, httpResponse.getStatusCode());

		waitForFinish(
			expectedExecuteStatus,
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetTaxonomyCategory() throws Exception {
		TaxonomyCategory postTaxonomyCategory =
			testGetTaxonomyCategory_addTaxonomyCategory();

		TaxonomyCategory getTaxonomyCategory =
			taxonomyCategoryResource.getTaxonomyCategory(
				postTaxonomyCategory.getId());

		assertEquals(postTaxonomyCategory, getTaxonomyCategory);
		assertValid(getTaxonomyCategory);

		Assert.assertNull(getTaxonomyCategory.getPermissions());

		getTaxonomyCategory =
			permissionsTaxonomyCategoryResource.getTaxonomyCategory(
				postTaxonomyCategory.getId());

		Assert.assertNotNull(getTaxonomyCategory.getPermissions());
	}

	protected TaxonomyCategory testGetTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTaxonomyCategory() throws Exception {
		TaxonomyCategory taxonomyCategory =
			testGraphQLGetTaxonomyCategory_addTaxonomyCategory();

		// No namespace

		Assert.assertTrue(
			equals(
				taxonomyCategory,
				TaxonomyCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"taxonomyCategory",
								new HashMap<String, Object>() {
									{
										put(
											"taxonomyCategoryId",
											"\"" + taxonomyCategory.getId() +
												"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/taxonomyCategory"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				taxonomyCategory,
				TaxonomyCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"taxonomyCategory",
									new HashMap<String, Object>() {
										{
											put(
												"taxonomyCategoryId",
												"\"" +
													taxonomyCategory.getId() +
														"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/taxonomyCategory"))));
	}

	@Test
	public void testGraphQLGetTaxonomyCategoryNotFound() throws Exception {
		String irrelevantTaxonomyCategoryId =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"taxonomyCategory",
						new HashMap<String, Object>() {
							{
								put(
									"taxonomyCategoryId",
									irrelevantTaxonomyCategoryId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"taxonomyCategory",
							new HashMap<String, Object>() {
								{
									put(
										"taxonomyCategoryId",
										irrelevantTaxonomyCategoryId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected TaxonomyCategory
			testGraphQLGetTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		return testGraphQLTaxonomyCategory_addTaxonomyCategory();
	}

	@Test
	public void testPatchTaxonomyCategory() throws Exception {
		TaxonomyCategory postTaxonomyCategory =
			testPatchTaxonomyCategory_addTaxonomyCategory();

		TaxonomyCategory randomPatchTaxonomyCategory =
			randomPatchTaxonomyCategory();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyCategory patchTaxonomyCategory =
			taxonomyCategoryResource.patchTaxonomyCategory(
				postTaxonomyCategory.getId(), randomPatchTaxonomyCategory);

		TaxonomyCategory expectedPatchTaxonomyCategory =
			postTaxonomyCategory.clone();

		BeanTestUtil.copyProperties(
			randomPatchTaxonomyCategory, expectedPatchTaxonomyCategory);

		TaxonomyCategory getTaxonomyCategory =
			taxonomyCategoryResource.getTaxonomyCategory(
				patchTaxonomyCategory.getId());

		assertEquals(expectedPatchTaxonomyCategory, getTaxonomyCategory);
		assertValid(getTaxonomyCategory);
	}

	protected TaxonomyCategory testPatchTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutTaxonomyCategory() throws Exception {
		TaxonomyCategory postTaxonomyCategory =
			testPutTaxonomyCategory_addTaxonomyCategory();

		TaxonomyCategory randomTaxonomyCategory = randomTaxonomyCategory();

		TaxonomyCategory putTaxonomyCategory =
			taxonomyCategoryResource.putTaxonomyCategory(
				postTaxonomyCategory.getId(), randomTaxonomyCategory);

		assertEquals(randomTaxonomyCategory, putTaxonomyCategory);
		assertValid(putTaxonomyCategory);

		Assert.assertNull(putTaxonomyCategory.getPermissions());

		TaxonomyCategory getTaxonomyCategory =
			taxonomyCategoryResource.getTaxonomyCategory(
				putTaxonomyCategory.getId());

		assertEquals(randomTaxonomyCategory, getTaxonomyCategory);
		assertValid(getTaxonomyCategory);

		TaxonomyCategory randomPermissionsTaxonomyCategory =
			randomPermissionsTaxonomyCategory();

		putTaxonomyCategory = taxonomyCategoryResource.putTaxonomyCategory(
			postTaxonomyCategory.getId(), randomPermissionsTaxonomyCategory);

		assertEquals(randomPermissionsTaxonomyCategory, putTaxonomyCategory);
		assertValid(putTaxonomyCategory);

		Assert.assertNull(putTaxonomyCategory.getPermissions());

		putTaxonomyCategory =
			permissionsTaxonomyCategoryResource.putTaxonomyCategory(
				postTaxonomyCategory.getId(),
				randomPermissionsTaxonomyCategory);

		Assert.assertNotNull(putTaxonomyCategory.getPermissions());
	}

	protected TaxonomyCategory testPutTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetTaxonomyCategoryPermissionsPage() throws Exception {
		TaxonomyCategory postTaxonomyCategory =
			testGetTaxonomyCategoryPermissionsPage_addTaxonomyCategory();

		Page<Permission> page =
			taxonomyCategoryResource.getTaxonomyCategoryPermissionsPage(
				postTaxonomyCategory.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected TaxonomyCategory
			testGetTaxonomyCategoryPermissionsPage_addTaxonomyCategory()
		throws Exception {

		return testPostTaxonomyCategoryTaxonomyCategory_addTaxonomyCategory(
			randomTaxonomyCategory());
	}

	@Test
	public void testPutTaxonomyCategoryPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyCategory taxonomyCategory =
			testPutTaxonomyCategoryPermissionsPage_addTaxonomyCategory();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			taxonomyCategoryResource.
				putTaxonomyCategoryPermissionsPageHttpResponse(
					taxonomyCategory.getId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			taxonomyCategoryResource.
				putTaxonomyCategoryPermissionsPageHttpResponse(
					"-",
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected TaxonomyCategory
			testPutTaxonomyCategoryPermissionsPage_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPage()
		throws Exception {

		Long taxonomyVocabularyId =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId();
		Long irrelevantTaxonomyVocabularyId =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getIrrelevantTaxonomyVocabularyId();

		Page<TaxonomyCategory> page =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					taxonomyVocabularyId, null, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantTaxonomyVocabularyId != null) {
			TaxonomyCategory irrelevantTaxonomyCategory =
				testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
					irrelevantTaxonomyVocabularyId,
					randomIrrelevantTaxonomyCategory());

			page =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						irrelevantTaxonomyVocabularyId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantTaxonomyCategory,
				(List<TaxonomyCategory>)page.getItems());
			assertValid(
				page,
				testGetTaxonomyVocabularyTaxonomyCategoriesPage_getExpectedActions(
					irrelevantTaxonomyVocabularyId));
		}

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, randomTaxonomyCategory());

		page =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					taxonomyVocabularyId, null, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			taxonomyCategory1, (List<TaxonomyCategory>)page.getItems());
		assertContains(
			taxonomyCategory2, (List<TaxonomyCategory>)page.getItems());
		assertValid(
			page,
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getExpectedActions(
				taxonomyVocabularyId));

		for (TaxonomyCategory taxonomyCategory : page.getItems()) {
			Assert.assertNull(taxonomyCategory.getPermissions());
		}

		page =
			permissionsTaxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					taxonomyVocabularyId, null, null, null, null,
					Pagination.of(1, 10), null);

		for (TaxonomyCategory taxonomyCategory : page.getItems()) {
			Assert.assertNotNull(taxonomyCategory.getPermissions());
		}

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory1.getId());

		taxonomyCategoryResource.deleteTaxonomyCategory(
			taxonomyCategory2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getExpectedActions(
				Long taxonomyVocabularyId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/batch".
				replace(
					"{taxonomyVocabularyId}",
					String.valueOf(taxonomyVocabularyId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long taxonomyVocabularyId =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId();

		TaxonomyCategory taxonomyCategory1 = randomTaxonomyCategory();

		taxonomyCategory1 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, taxonomyCategory1);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyCategory> page =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null,
						getFilterString(
							entityField, "between", taxonomyCategory1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyCategory1),
				(List<TaxonomyCategory>)page.getItems());
		}
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilterStringContains()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilterStringEquals()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long taxonomyVocabularyId =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId();

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, randomTaxonomyCategory());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, randomTaxonomyCategory());

		for (EntityField entityField : entityFields) {
			Page<TaxonomyCategory> page =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null,
						getFilterString(
							entityField, operator, taxonomyCategory1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyCategory1),
				(List<TaxonomyCategory>)page.getItems());
		}
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithPagination()
		throws Exception {

		Long taxonomyVocabularyId =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId();

		Page<TaxonomyCategory> taxonomyCategoryPage =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					taxonomyVocabularyId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			taxonomyCategoryPage.getTotalCount());

		TaxonomyCategory taxonomyCategory1 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory2 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, randomTaxonomyCategory());

		TaxonomyCategory taxonomyCategory3 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, randomTaxonomyCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TaxonomyCategory> page1 =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)page1.getItems());

			Page<TaxonomyCategory> page2 =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)page2.getItems());

			Page<TaxonomyCategory> page3 =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				taxonomyCategory3, (List<TaxonomyCategory>)page3.getItems());
		}
		else {
			Page<TaxonomyCategory> page1 =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<TaxonomyCategory> taxonomyCategories1 =
				(List<TaxonomyCategory>)page1.getItems();

			Assert.assertEquals(
				taxonomyCategories1.toString(), totalCount + 2,
				taxonomyCategories1.size());

			Page<TaxonomyCategory> page2 =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TaxonomyCategory> taxonomyCategories2 =
				(List<TaxonomyCategory>)page2.getItems();

			Assert.assertEquals(
				taxonomyCategories2.toString(), 1, taxonomyCategories2.size());

			Page<TaxonomyCategory> page3 =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)page3.getItems());
			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)page3.getItems());
			assertContains(
				taxonomyCategory3, (List<TaxonomyCategory>)page3.getItems());
		}
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSortDateTime()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				BeanTestUtil.setProperty(
					taxonomyCategory1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSortDouble()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				BeanTestUtil.setProperty(
					taxonomyCategory1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					taxonomyCategory2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSortInteger()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				BeanTestUtil.setProperty(
					taxonomyCategory1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					taxonomyCategory2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSortString()
		throws Exception {

		testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, taxonomyCategory1, taxonomyCategory2) -> {
				Class<?> clazz = taxonomyCategory1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						taxonomyCategory1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						taxonomyCategory2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						taxonomyCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						taxonomyCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						taxonomyCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						taxonomyCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetTaxonomyVocabularyTaxonomyCategoriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, TaxonomyCategory, TaxonomyCategory, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long taxonomyVocabularyId =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId();

		TaxonomyCategory taxonomyCategory1 = randomTaxonomyCategory();
		TaxonomyCategory taxonomyCategory2 = randomTaxonomyCategory();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, taxonomyCategory1, taxonomyCategory2);
		}

		taxonomyCategory1 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, taxonomyCategory1);

		taxonomyCategory2 =
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				taxonomyVocabularyId, taxonomyCategory2);

		Page<TaxonomyCategory> page =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					taxonomyVocabularyId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyCategory> ascPage =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)ascPage.getItems());
			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)ascPage.getItems());

			Page<TaxonomyCategory> descPage =
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				taxonomyCategory2, (List<TaxonomyCategory>)descPage.getItems());
			assertContains(
				taxonomyCategory1, (List<TaxonomyCategory>)descPage.getItems());
		}
	}

	protected TaxonomyCategory
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_addTaxonomyCategory(
				Long taxonomyVocabularyId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
			taxonomyVocabularyId, taxonomyCategory);
	}

	protected Long
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getIrrelevantTaxonomyVocabularyId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostTaxonomyVocabularyTaxonomyCategory() throws Exception {
		TaxonomyCategory randomTaxonomyCategory = randomTaxonomyCategory();

		TaxonomyCategory postTaxonomyCategory =
			testPostTaxonomyVocabularyTaxonomyCategory_addTaxonomyCategory(
				randomTaxonomyCategory);

		assertEquals(randomTaxonomyCategory, postTaxonomyCategory);
		assertValid(postTaxonomyCategory);

		TaxonomyCategory randomPermissionsTaxonomyCategory1 =
			randomPermissionsTaxonomyCategory();

		TaxonomyCategory postPermissionsTaxonomyCategory1 =
			testPostTaxonomyVocabularyTaxonomyCategory_addTaxonomyCategory(
				randomPermissionsTaxonomyCategory1);

		Assert.assertNull(postPermissionsTaxonomyCategory1.getPermissions());

		TaxonomyCategory randomPermissionsTaxonomyCategory2 =
			randomPermissionsTaxonomyCategory();

		TaxonomyCategory postPermissionsTaxonomyCategory2 =
			testPostTaxonomyVocabularyTaxonomyCategory_addPermissionsTaxonomyCategory(
				randomPermissionsTaxonomyCategory2);

		Assert.assertNotNull(postPermissionsTaxonomyCategory2.getPermissions());
	}

	protected TaxonomyCategory
			testPostTaxonomyVocabularyTaxonomyCategory_addTaxonomyCategory(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
			testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId(),
			taxonomyCategory);
	}

	protected TaxonomyCategory
			testPostTaxonomyVocabularyTaxonomyCategory_addPermissionsTaxonomyCategory(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return permissionsTaxonomyCategoryResource.
			postTaxonomyVocabularyTaxonomyCategory(
				testGetTaxonomyVocabularyTaxonomyCategoriesPage_getTaxonomyVocabularyId(),
				taxonomyCategory);
	}

	@Test
	public void testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyCategory taxonomyCategory =
			testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory();

		assertHttpResponseStatusCode(
			204,
			taxonomyCategoryResource.
				deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeHttpResponse(
					testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						taxonomyCategory),
					taxonomyCategory.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeHttpResponse(
					testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						taxonomyCategory),
					taxonomyCategory.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeHttpResponse(
					testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						taxonomyCategory),
					"-"));
	}

	protected Long
			testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategory.getTaxonomyVocabularyId();
	}

	protected TaxonomyCategory
			testDeleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		TaxonomyCategory postTaxonomyCategory =
			testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory();

		TaxonomyCategory getTaxonomyCategory =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						postTaxonomyCategory),
					postTaxonomyCategory.getExternalReferenceCode());

		assertEquals(postTaxonomyCategory, getTaxonomyCategory);
		assertValid(getTaxonomyCategory);

		Assert.assertNull(getTaxonomyCategory.getPermissions());

		getTaxonomyCategory =
			permissionsTaxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						postTaxonomyCategory),
					postTaxonomyCategory.getExternalReferenceCode());

		Assert.assertNotNull(getTaxonomyCategory.getPermissions());
	}

	protected Long
			testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategory.getTaxonomyVocabularyId();
	}

	protected TaxonomyCategory
			testGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		TaxonomyCategory taxonomyCategory =
			testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory();

		// No namespace

		Assert.assertTrue(
			equals(
				taxonomyCategory,
				TaxonomyCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"taxonomyVocabularyId",
											testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
												taxonomyCategory));

										put(
											"externalReferenceCode",
											"\"" +
												taxonomyCategory.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				taxonomyCategory,
				TaxonomyCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"taxonomyVocabularyId",
												testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
													taxonomyCategory));

											put(
												"externalReferenceCode",
												"\"" +
													taxonomyCategory.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategory.getTaxonomyVocabularyId();
	}

	@Test
	public void testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeNotFound()
		throws Exception {

		Long irrelevantTaxonomyVocabularyId = RandomTestUtil.randomLong();
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"taxonomyVocabularyId",
									irrelevantTaxonomyVocabularyId);
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"taxonomyVocabularyId",
										irrelevantTaxonomyVocabularyId);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected TaxonomyCategory
			testGraphQLGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		return testGraphQLTaxonomyCategory_addTaxonomyCategory();
	}

	@Test
	public void testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode()
		throws Exception {

		TaxonomyCategory postTaxonomyCategory =
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory();

		TaxonomyCategory randomTaxonomyCategory = randomTaxonomyCategory();

		TaxonomyCategory putTaxonomyCategory =
			taxonomyCategoryResource.
				putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						postTaxonomyCategory),
					postTaxonomyCategory.getExternalReferenceCode(),
					randomTaxonomyCategory);

		assertEquals(randomTaxonomyCategory, putTaxonomyCategory);
		assertValid(putTaxonomyCategory);

		Assert.assertNull(putTaxonomyCategory.getPermissions());

		TaxonomyCategory getTaxonomyCategory =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						putTaxonomyCategory),
					putTaxonomyCategory.getExternalReferenceCode());

		assertEquals(randomTaxonomyCategory, getTaxonomyCategory);
		assertValid(getTaxonomyCategory);

		TaxonomyCategory randomPermissionsTaxonomyCategory =
			randomPermissionsTaxonomyCategory();

		putTaxonomyCategory =
			taxonomyCategoryResource.
				putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						postTaxonomyCategory),
					postTaxonomyCategory.getExternalReferenceCode(),
					randomPermissionsTaxonomyCategory);

		assertEquals(randomPermissionsTaxonomyCategory, putTaxonomyCategory);
		assertValid(putTaxonomyCategory);

		Assert.assertNull(putTaxonomyCategory.getPermissions());

		putTaxonomyCategory =
			permissionsTaxonomyCategoryResource.
				putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						postTaxonomyCategory),
					postTaxonomyCategory.getExternalReferenceCode(),
					randomPermissionsTaxonomyCategory);

		Assert.assertNotNull(putTaxonomyCategory.getPermissions());

		TaxonomyCategory newTaxonomyCategory =
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_createTaxonomyCategory();

		putTaxonomyCategory =
			taxonomyCategoryResource.
				putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						newTaxonomyCategory),
					newTaxonomyCategory.getExternalReferenceCode(),
					newTaxonomyCategory);

		assertEquals(newTaxonomyCategory, putTaxonomyCategory);
		assertValid(putTaxonomyCategory);

		getTaxonomyCategory =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
					testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
						putTaxonomyCategory),
					putTaxonomyCategory.getExternalReferenceCode());

		assertEquals(newTaxonomyCategory, getTaxonomyCategory);

		Assert.assertEquals(
			newTaxonomyCategory.getExternalReferenceCode(),
			putTaxonomyCategory.getExternalReferenceCode());
	}

	protected Long
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_getTaxonomyVocabularyId(
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		return taxonomyCategory.getTaxonomyVocabularyId();
	}

	protected TaxonomyCategory
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_createTaxonomyCategory()
		throws Exception {

		return randomTaxonomyCategory();
	}

	protected TaxonomyCategory
			testPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected TaxonomyCategory testGraphQLTaxonomyCategory_addTaxonomyCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		TaxonomyCategory taxonomyCategory,
		List<TaxonomyCategory> taxonomyCategories) {

		boolean contains = false;

		for (TaxonomyCategory item : taxonomyCategories) {
			if (equals(taxonomyCategory, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			taxonomyCategories + " does not contain " + taxonomyCategory,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		TaxonomyCategory taxonomyCategory1,
		TaxonomyCategory taxonomyCategory2) {

		Assert.assertTrue(
			taxonomyCategory1 + " does not equal " + taxonomyCategory2,
			equals(taxonomyCategory1, taxonomyCategory2));
	}

	protected void assertEquals(
		List<TaxonomyCategory> taxonomyCategories1,
		List<TaxonomyCategory> taxonomyCategories2) {

		Assert.assertEquals(
			taxonomyCategories1.size(), taxonomyCategories2.size());

		for (int i = 0; i < taxonomyCategories1.size(); i++) {
			TaxonomyCategory taxonomyCategory1 = taxonomyCategories1.get(i);
			TaxonomyCategory taxonomyCategory2 = taxonomyCategories2.get(i);

			assertEquals(taxonomyCategory1, taxonomyCategory2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TaxonomyCategory> taxonomyCategories1,
		List<TaxonomyCategory> taxonomyCategories2) {

		Assert.assertEquals(
			taxonomyCategories1.size(), taxonomyCategories2.size());

		for (TaxonomyCategory taxonomyCategory1 : taxonomyCategories1) {
			boolean contains = false;

			for (TaxonomyCategory taxonomyCategory2 : taxonomyCategories2) {
				if (equals(taxonomyCategory1, taxonomyCategory2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				taxonomyCategories2 + " does not contain " + taxonomyCategory1,
				contains);
		}
	}

	protected void assertValid(TaxonomyCategory taxonomyCategory)
		throws Exception {

		boolean valid = true;

		if (taxonomyCategory.getDateCreated() == null) {
			valid = false;
		}

		if (taxonomyCategory.getDateModified() == null) {
			valid = false;
		}

		if (taxonomyCategory.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				taxonomyCategory.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (taxonomyCategory.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (taxonomyCategory.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (taxonomyCategory.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (taxonomyCategory.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (taxonomyCategory.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (taxonomyCategory.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (taxonomyCategory.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (taxonomyCategory.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfTaxonomyCategories", additionalAssertFieldName)) {

				if (taxonomyCategory.getNumberOfTaxonomyCategories() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentTaxonomyCategory", additionalAssertFieldName)) {

				if (taxonomyCategory.getParentTaxonomyCategory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentTaxonomyVocabulary", additionalAssertFieldName)) {

				if (taxonomyCategory.getParentTaxonomyVocabulary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (taxonomyCategory.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (taxonomyCategory.getSiteExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryProperties", additionalAssertFieldName)) {

				if (taxonomyCategory.getTaxonomyCategoryProperties() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryUsageCount", additionalAssertFieldName)) {

				if (taxonomyCategory.getTaxonomyCategoryUsageCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyVocabularyId", additionalAssertFieldName)) {

				if (taxonomyCategory.getTaxonomyVocabularyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (taxonomyCategory.getViewableBy() == null) {
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

	protected void assertValid(Page<TaxonomyCategory> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<TaxonomyCategory> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<TaxonomyCategory> taxonomyCategories =
			page.getItems();

		int size = taxonomyCategories.size();

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

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.taxonomy.dto.v1_0.
						TaxonomyCategory.class)) {

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
		TaxonomyCategory taxonomyCategory1,
		TaxonomyCategory taxonomyCategory2) {

		if (taxonomyCategory1 == taxonomyCategory2) {
			return true;
		}

		if (!Objects.equals(
				taxonomyCategory1.getSiteId(), taxonomyCategory2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyCategory1.getActions(),
						(Map)taxonomyCategory2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getAvailableLanguages(),
						taxonomyCategory2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getCreator(),
						taxonomyCategory2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getDateCreated(),
						taxonomyCategory2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getDateModified(),
						taxonomyCategory2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getDescription(),
						taxonomyCategory2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyCategory1.getDescription_i18n(),
						(Map)taxonomyCategory2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getExternalReferenceCode(),
						taxonomyCategory2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getId(), taxonomyCategory2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getName(),
						taxonomyCategory2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyCategory1.getName_i18n(),
						(Map)taxonomyCategory2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfTaxonomyCategories", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getNumberOfTaxonomyCategories(),
						taxonomyCategory2.getNumberOfTaxonomyCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentTaxonomyCategory", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getParentTaxonomyCategory(),
						taxonomyCategory2.getParentTaxonomyCategory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentTaxonomyVocabulary", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getParentTaxonomyVocabulary(),
						taxonomyCategory2.getParentTaxonomyVocabulary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getPermissions(),
						taxonomyCategory2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getSiteExternalReferenceCode(),
						taxonomyCategory2.getSiteExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryProperties", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getTaxonomyCategoryProperties(),
						taxonomyCategory2.getTaxonomyCategoryProperties())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryUsageCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getTaxonomyCategoryUsageCount(),
						taxonomyCategory2.getTaxonomyCategoryUsageCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyVocabularyId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategory1.getTaxonomyVocabularyId(),
						taxonomyCategory2.getTaxonomyVocabularyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategory1.getViewableBy(),
						taxonomyCategory2.getViewableBy())) {

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

		if (!(_taxonomyCategoryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taxonomyCategoryResource;

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
		TaxonomyCategory taxonomyCategory) {

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

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = taxonomyCategory.getDateCreated();

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

				sb.append(_format.format(taxonomyCategory.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = taxonomyCategory.getDateModified();

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

				sb.append(_format.format(taxonomyCategory.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = taxonomyCategory.getDescription();

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

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = taxonomyCategory.getExternalReferenceCode();

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
			Object object = taxonomyCategory.getId();

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

		if (entityFieldName.equals("name")) {
			Object object = taxonomyCategory.getName();

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

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfTaxonomyCategories")) {
			sb.append(
				String.valueOf(
					taxonomyCategory.getNumberOfTaxonomyCategories()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentTaxonomyCategory")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentTaxonomyVocabulary")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteExternalReferenceCode")) {
			Object object = taxonomyCategory.getSiteExternalReferenceCode();

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

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryProperties")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryUsageCount")) {
			sb.append(
				String.valueOf(
					taxonomyCategory.getTaxonomyCategoryUsageCount()));

			return sb.toString();
		}

		if (entityFieldName.equals("taxonomyVocabularyId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("viewableBy")) {
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

	protected TaxonomyCategory randomTaxonomyCategory() throws Exception {
		return new TaxonomyCategory() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfTaxonomyCategories = RandomTestUtil.randomInt();
				siteExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
				taxonomyCategoryUsageCount = RandomTestUtil.randomInt();
				taxonomyVocabularyId = RandomTestUtil.randomLong();
			}
		};
	}

	protected TaxonomyCategory randomIrrelevantTaxonomyCategory()
		throws Exception {

		TaxonomyCategory randomIrrelevantTaxonomyCategory =
			randomTaxonomyCategory();

		randomIrrelevantTaxonomyCategory.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantTaxonomyCategory;
	}

	protected TaxonomyCategory randomPatchTaxonomyCategory() throws Exception {
		return randomTaxonomyCategory();
	}

	protected TaxonomyCategory randomPermissionsTaxonomyCategory()
		throws Exception {

		TaxonomyCategory taxonomyCategory = randomTaxonomyCategory();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		taxonomyCategory.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return taxonomyCategory;
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

	protected TaxonomyCategoryResource taxonomyCategoryResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected TaxonomyCategoryResource permissionsTaxonomyCategoryResource;
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
		LogFactoryUtil.getLog(BaseTaxonomyCategoryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.taxonomy.resource.v1_0.
			TaxonomyCategoryResource _taxonomyCategoryResource;

}