/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.OptionCategory;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.OptionCategoryResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.OptionCategorySerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
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
import com.liferay.portal.kernel.util.DateUtil;
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
import java.util.TimeZone;

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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseOptionCategoryResourceTestCase {

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

		_optionCategoryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		optionCategoryResource = OptionCategoryResource.builder(
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

		OptionCategory optionCategory1 = randomOptionCategory();

		String json = objectMapper.writeValueAsString(optionCategory1);

		OptionCategory optionCategory2 = OptionCategorySerDes.toDTO(json);

		Assert.assertTrue(equals(optionCategory1, optionCategory2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OptionCategory optionCategory = randomOptionCategory();

		String json1 = objectMapper.writeValueAsString(optionCategory);
		String json2 = OptionCategorySerDes.toJSON(optionCategory);

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

		OptionCategory optionCategory = randomOptionCategory();

		optionCategory.setExternalReferenceCode(regex);
		optionCategory.setKey(regex);

		String json = OptionCategorySerDes.toJSON(optionCategory);

		Assert.assertFalse(json.contains(regex));

		optionCategory = OptionCategorySerDes.toDTO(json);

		Assert.assertEquals(regex, optionCategory.getExternalReferenceCode());
		Assert.assertEquals(regex, optionCategory.getKey());
	}

	@Test
	public void testDeleteOptionCategory() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OptionCategory optionCategory =
			testDeleteOptionCategory_addOptionCategory();

		assertHttpResponseStatusCode(
			204,
			optionCategoryResource.deleteOptionCategoryHttpResponse(
				optionCategory.getId()));

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory.getId()));
		assertHttpResponseStatusCode(
			404, optionCategoryResource.getOptionCategoryHttpResponse(0L));
	}

	protected OptionCategory testDeleteOptionCategory_addOptionCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOptionCategory() throws Exception {

		// No namespace

		OptionCategory optionCategory1 =
			testGraphQLDeleteOptionCategory_addOptionCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOptionCategory",
						new HashMap<String, Object>() {
							{
								put("id", optionCategory1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOptionCategory"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"optionCategory",
					new HashMap<String, Object>() {
						{
							put("id", optionCategory1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		OptionCategory optionCategory2 =
			testGraphQLDeleteOptionCategory_addOptionCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteOptionCategory",
							new HashMap<String, Object>() {
								{
									put("id", optionCategory2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteOptionCategory"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"optionCategory",
						new HashMap<String, Object>() {
							{
								put("id", optionCategory2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OptionCategory testGraphQLDeleteOptionCategory_addOptionCategory()
		throws Exception {

		return testGraphQLOptionCategory_addOptionCategory();
	}

	@Test
	public void testDeleteOptionCategoryBatch() throws Exception {
		OptionCategory optionCategory1 =
			testDeleteOptionCategoryBatch_addOptionCategory();

		testDeleteOptionCategoryBatch_deleteOptionCategory(
			202, optionCategory1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory1.getId()));

		optionCategory1 = testDeleteOptionCategoryBatch_addOptionCategory();

		testDeleteOptionCategoryBatch_deleteOptionCategory(
			202, null, optionCategory1.getId());

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory1.getId()));

		optionCategory1 = testDeleteOptionCategoryBatch_addOptionCategory();
		OptionCategory optionCategory2 =
			testDeleteOptionCategoryBatch_addOptionCategory();

		testDeleteOptionCategoryBatch_deleteOptionCategory(
			202, optionCategory2.getExternalReferenceCode(),
			optionCategory1.getId());

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory1.getId()));
		assertHttpResponseStatusCode(
			200,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory2.getId()));

		testDeleteOptionCategoryBatch_deleteOptionCategory(
			202, optionCategory2.getExternalReferenceCode(),
			optionCategory1.getId());

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory2.getId()));
	}

	protected OptionCategory testDeleteOptionCategoryBatch_addOptionCategory()
		throws Exception {

		return testDeleteOptionCategory_addOptionCategory();
	}

	protected void testDeleteOptionCategoryBatch_deleteOptionCategory(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			optionCategoryResource.deleteOptionCategoryBatchHttpResponse(
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
	public void testDeleteOptionCategoryByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OptionCategory optionCategory =
			testDeleteOptionCategoryByExternalReferenceCode_addOptionCategory();

		assertHttpResponseStatusCode(
			204,
			optionCategoryResource.
				deleteOptionCategoryByExternalReferenceCodeHttpResponse(
					optionCategory.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.
				getOptionCategoryByExternalReferenceCodeHttpResponse(
					optionCategory.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.
				getOptionCategoryByExternalReferenceCodeHttpResponse("-"));
	}

	protected OptionCategory
			testDeleteOptionCategoryByExternalReferenceCode_addOptionCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOptionCategoryByExternalReferenceCode()
		throws Exception {

		// No namespace

		OptionCategory optionCategory1 =
			testGraphQLDeleteOptionCategoryByExternalReferenceCode_addOptionCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOptionCategoryByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										optionCategory1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOptionCategoryByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"optionCategoryByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									optionCategory1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		OptionCategory optionCategory2 =
			testGraphQLDeleteOptionCategoryByExternalReferenceCode_addOptionCategory();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteOptionCategoryByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											optionCategory2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteOptionCategoryByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"optionCategoryByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										optionCategory2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OptionCategory
			testGraphQLDeleteOptionCategoryByExternalReferenceCode_addOptionCategory()
		throws Exception {

		return testGraphQLOptionCategory_addOptionCategory();
	}

	@Test
	public void testGetOptionCategoriesPage() throws Exception {
		Page<OptionCategory> page =
			optionCategoryResource.getOptionCategoriesPage(
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		OptionCategory optionCategory1 =
			testGetOptionCategoriesPage_addOptionCategory(
				randomOptionCategory());

		OptionCategory optionCategory2 =
			testGetOptionCategoriesPage_addOptionCategory(
				randomOptionCategory());

		page = optionCategoryResource.getOptionCategoriesPage(
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(optionCategory1, (List<OptionCategory>)page.getItems());
		assertContains(optionCategory2, (List<OptionCategory>)page.getItems());
		assertValid(page, testGetOptionCategoriesPage_getExpectedActions());

		optionCategoryResource.deleteOptionCategory(optionCategory1.getId());

		optionCategoryResource.deleteOptionCategory(optionCategory2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOptionCategoriesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOptionCategoriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		OptionCategory optionCategory1 = randomOptionCategory();

		optionCategory1 = testGetOptionCategoriesPage_addOptionCategory(
			optionCategory1);

		for (EntityField entityField : entityFields) {
			Page<OptionCategory> page =
				optionCategoryResource.getOptionCategoriesPage(
					getFilterString(entityField, "between", optionCategory1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(optionCategory1),
				(List<OptionCategory>)page.getItems());
		}
	}

	@Test
	public void testGetOptionCategoriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetOptionCategoriesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOptionCategoriesPageWithFilterStringContains()
		throws Exception {

		testGetOptionCategoriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOptionCategoriesPageWithFilterStringEquals()
		throws Exception {

		testGetOptionCategoriesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOptionCategoriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetOptionCategoriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOptionCategoriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OptionCategory optionCategory1 =
			testGetOptionCategoriesPage_addOptionCategory(
				randomOptionCategory());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OptionCategory optionCategory2 =
			testGetOptionCategoriesPage_addOptionCategory(
				randomOptionCategory());

		for (EntityField entityField : entityFields) {
			Page<OptionCategory> page =
				optionCategoryResource.getOptionCategoriesPage(
					getFilterString(entityField, operator, optionCategory1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(optionCategory1),
				(List<OptionCategory>)page.getItems());
		}
	}

	@Test
	public void testGetOptionCategoriesPageWithPagination() throws Exception {
		Page<OptionCategory> optionCategoriesPage =
			optionCategoryResource.getOptionCategoriesPage(null, null, null);

		int totalCount = GetterUtil.getInteger(
			optionCategoriesPage.getTotalCount());

		OptionCategory optionCategory1 =
			testGetOptionCategoriesPage_addOptionCategory(
				randomOptionCategory());

		OptionCategory optionCategory2 =
			testGetOptionCategoriesPage_addOptionCategory(
				randomOptionCategory());

		OptionCategory optionCategory3 =
			testGetOptionCategoriesPage_addOptionCategory(
				randomOptionCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OptionCategory> page1 =
				optionCategoryResource.getOptionCategoriesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				optionCategory1, (List<OptionCategory>)page1.getItems());

			Page<OptionCategory> page2 =
				optionCategoryResource.getOptionCategoriesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				optionCategory2, (List<OptionCategory>)page2.getItems());

			Page<OptionCategory> page3 =
				optionCategoryResource.getOptionCategoriesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				optionCategory3, (List<OptionCategory>)page3.getItems());
		}
		else {
			Page<OptionCategory> page1 =
				optionCategoryResource.getOptionCategoriesPage(
					null, Pagination.of(1, totalCount + 2), null);

			List<OptionCategory> optionCategories1 =
				(List<OptionCategory>)page1.getItems();

			Assert.assertEquals(
				optionCategories1.toString(), totalCount + 2,
				optionCategories1.size());

			Page<OptionCategory> page2 =
				optionCategoryResource.getOptionCategoriesPage(
					null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OptionCategory> optionCategories2 =
				(List<OptionCategory>)page2.getItems();

			Assert.assertEquals(
				optionCategories2.toString(), 1, optionCategories2.size());

			Page<OptionCategory> page3 =
				optionCategoryResource.getOptionCategoriesPage(
					null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				optionCategory1, (List<OptionCategory>)page3.getItems());
			assertContains(
				optionCategory2, (List<OptionCategory>)page3.getItems());
			assertContains(
				optionCategory3, (List<OptionCategory>)page3.getItems());
		}
	}

	@Test
	public void testGetOptionCategoriesPageWithSortDateTime() throws Exception {
		testGetOptionCategoriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, optionCategory1, optionCategory2) -> {
				BeanTestUtil.setProperty(
					optionCategory1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOptionCategoriesPageWithSortDouble() throws Exception {
		testGetOptionCategoriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, optionCategory1, optionCategory2) -> {
				BeanTestUtil.setProperty(
					optionCategory1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					optionCategory2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOptionCategoriesPageWithSortInteger() throws Exception {
		testGetOptionCategoriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, optionCategory1, optionCategory2) -> {
				BeanTestUtil.setProperty(
					optionCategory1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					optionCategory2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOptionCategoriesPageWithSortString() throws Exception {
		testGetOptionCategoriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, optionCategory1, optionCategory2) -> {
				Class<?> clazz = optionCategory1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						optionCategory1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						optionCategory2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						optionCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						optionCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						optionCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						optionCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOptionCategoriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, OptionCategory, OptionCategory, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		OptionCategory optionCategory1 = randomOptionCategory();
		OptionCategory optionCategory2 = randomOptionCategory();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, optionCategory1, optionCategory2);
		}

		optionCategory1 = testGetOptionCategoriesPage_addOptionCategory(
			optionCategory1);

		optionCategory2 = testGetOptionCategoriesPage_addOptionCategory(
			optionCategory2);

		Page<OptionCategory> page =
			optionCategoryResource.getOptionCategoriesPage(null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OptionCategory> ascPage =
				optionCategoryResource.getOptionCategoriesPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				optionCategory1, (List<OptionCategory>)ascPage.getItems());
			assertContains(
				optionCategory2, (List<OptionCategory>)ascPage.getItems());

			Page<OptionCategory> descPage =
				optionCategoryResource.getOptionCategoriesPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				optionCategory2, (List<OptionCategory>)descPage.getItems());
			assertContains(
				optionCategory1, (List<OptionCategory>)descPage.getItems());
		}
	}

	protected OptionCategory testGetOptionCategoriesPage_addOptionCategory(
			OptionCategory optionCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOptionCategoriesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"optionCategories",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject optionCategoriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/optionCategories");

		long totalCount = optionCategoriesJSONObject.getLong("totalCount");

		OptionCategory optionCategory1 =
			testGraphQLOptionCategory_addOptionCategory(randomOptionCategory());

		OptionCategory optionCategory2 =
			testGraphQLOptionCategory_addOptionCategory(randomOptionCategory());

		optionCategoriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/optionCategories");

		Assert.assertEquals(
			totalCount + 2, optionCategoriesJSONObject.getLong("totalCount"));

		assertContains(
			optionCategory1,
			Arrays.asList(
				OptionCategorySerDes.toDTOs(
					optionCategoriesJSONObject.getString("items"))));
		assertContains(
			optionCategory2,
			Arrays.asList(
				OptionCategorySerDes.toDTOs(
					optionCategoriesJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		optionCategoriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/optionCategories");

		Assert.assertEquals(
			totalCount + 2, optionCategoriesJSONObject.getLong("totalCount"));

		assertContains(
			optionCategory1,
			Arrays.asList(
				OptionCategorySerDes.toDTOs(
					optionCategoriesJSONObject.getString("items"))));
		assertContains(
			optionCategory2,
			Arrays.asList(
				OptionCategorySerDes.toDTOs(
					optionCategoriesJSONObject.getString("items"))));
	}

	@Test
	public void testGetOptionCategory() throws Exception {
		OptionCategory postOptionCategory =
			testGetOptionCategory_addOptionCategory();

		OptionCategory getOptionCategory =
			optionCategoryResource.getOptionCategory(
				postOptionCategory.getId());

		assertEquals(postOptionCategory, getOptionCategory);
		assertValid(getOptionCategory);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		OptionCategory postOptionCategory =
			testGetOptionCategory_addOptionCategory();

		OptionCategory getOptionCategory =
			optionCategoryResource.getOptionCategory(
				postOptionCategory.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionCategory"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postOptionCategory.getId());

		assertEquals(
			getOptionCategory, OptionCategorySerDes.toDTO(item.toString()));
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

	protected OptionCategory testGetOptionCategory_addOptionCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOptionCategory() throws Exception {
		OptionCategory optionCategory =
			testGraphQLGetOptionCategory_addOptionCategory();

		// No namespace

		Assert.assertTrue(
			equals(
				optionCategory,
				OptionCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"optionCategory",
								new HashMap<String, Object>() {
									{
										put("id", optionCategory.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/optionCategory"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				optionCategory,
				OptionCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"optionCategory",
									new HashMap<String, Object>() {
										{
											put("id", optionCategory.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/optionCategory"))));
	}

	@Test
	public void testGraphQLGetOptionCategoryNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"optionCategory",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"optionCategory",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OptionCategory testGraphQLGetOptionCategory_addOptionCategory()
		throws Exception {

		return testGraphQLOptionCategory_addOptionCategory();
	}

	@Test
	public void testGetOptionCategoryByExternalReferenceCode()
		throws Exception {

		OptionCategory postOptionCategory =
			testGetOptionCategoryByExternalReferenceCode_addOptionCategory();

		OptionCategory getOptionCategory =
			optionCategoryResource.getOptionCategoryByExternalReferenceCode(
				postOptionCategory.getExternalReferenceCode());

		assertEquals(postOptionCategory, getOptionCategory);
		assertValid(getOptionCategory);
	}

	protected OptionCategory
			testGetOptionCategoryByExternalReferenceCode_addOptionCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOptionCategoryByExternalReferenceCode()
		throws Exception {

		OptionCategory optionCategory =
			testGraphQLGetOptionCategoryByExternalReferenceCode_addOptionCategory();

		// No namespace

		Assert.assertTrue(
			equals(
				optionCategory,
				OptionCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"optionCategoryByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												optionCategory.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/optionCategoryByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				optionCategory,
				OptionCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"optionCategoryByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													optionCategory.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/optionCategoryByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOptionCategoryByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"optionCategoryByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"optionCategoryByExternalReferenceCode",
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

	protected OptionCategory
			testGraphQLGetOptionCategoryByExternalReferenceCode_addOptionCategory()
		throws Exception {

		return testGraphQLOptionCategory_addOptionCategory();
	}

	@Test
	public void testPatchOptionCategory() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchOptionCategoryByExternalReferenceCode()
		throws Exception {

		OptionCategory postOptionCategory =
			testPatchOptionCategoryByExternalReferenceCode_addOptionCategory();

		OptionCategory randomPatchOptionCategory = randomPatchOptionCategory();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OptionCategory patchOptionCategory =
			optionCategoryResource.patchOptionCategoryByExternalReferenceCode(
				postOptionCategory.getExternalReferenceCode(),
				randomPatchOptionCategory);

		OptionCategory expectedPatchOptionCategory = postOptionCategory.clone();

		BeanTestUtil.copyProperties(
			randomPatchOptionCategory, expectedPatchOptionCategory);

		OptionCategory getOptionCategory =
			optionCategoryResource.getOptionCategoryByExternalReferenceCode(
				patchOptionCategory.getExternalReferenceCode());

		assertEquals(expectedPatchOptionCategory, getOptionCategory);
		assertValid(getOptionCategory);
	}

	protected OptionCategory
			testPatchOptionCategoryByExternalReferenceCode_addOptionCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOptionCategory() throws Exception {
		OptionCategory randomOptionCategory = randomOptionCategory();

		OptionCategory postOptionCategory =
			testPostOptionCategory_addOptionCategory(randomOptionCategory);

		assertEquals(randomOptionCategory, postOptionCategory);
		assertValid(postOptionCategory);
	}

	protected OptionCategory testPostOptionCategory_addOptionCategory(
			OptionCategory optionCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostOptionCategory() throws Exception {
		OptionCategory randomOptionCategory = randomOptionCategory();

		OptionCategory optionCategory =
			testGraphQLOptionCategory_addOptionCategory(randomOptionCategory);

		Assert.assertTrue(equals(randomOptionCategory, optionCategory));
	}

	@Test
	public void testPutOptionCategoryByExternalReferenceCode()
		throws Exception {

		OptionCategory postOptionCategory =
			testPutOptionCategoryByExternalReferenceCode_addOptionCategory();

		OptionCategory randomOptionCategory = randomOptionCategory();

		OptionCategory putOptionCategory =
			optionCategoryResource.putOptionCategoryByExternalReferenceCode(
				postOptionCategory.getExternalReferenceCode(),
				randomOptionCategory);

		assertEquals(randomOptionCategory, putOptionCategory);
		assertValid(putOptionCategory);

		OptionCategory getOptionCategory =
			optionCategoryResource.getOptionCategoryByExternalReferenceCode(
				putOptionCategory.getExternalReferenceCode());

		assertEquals(randomOptionCategory, getOptionCategory);
		assertValid(getOptionCategory);

		OptionCategory newOptionCategory =
			testPutOptionCategoryByExternalReferenceCode_createOptionCategory();

		putOptionCategory =
			optionCategoryResource.putOptionCategoryByExternalReferenceCode(
				newOptionCategory.getExternalReferenceCode(),
				newOptionCategory);

		assertEquals(newOptionCategory, putOptionCategory);
		assertValid(putOptionCategory);

		getOptionCategory =
			optionCategoryResource.getOptionCategoryByExternalReferenceCode(
				putOptionCategory.getExternalReferenceCode());

		assertEquals(newOptionCategory, getOptionCategory);

		Assert.assertEquals(
			newOptionCategory.getExternalReferenceCode(),
			putOptionCategory.getExternalReferenceCode());
	}

	protected OptionCategory
			testPutOptionCategoryByExternalReferenceCode_addOptionCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected OptionCategory
			testPutOptionCategoryByExternalReferenceCode_createOptionCategory()
		throws Exception {

		return randomOptionCategory();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OptionCategory optionCategory1 =
			testBatchEngineDeleteImportTask_addOptionCategory();

		testBatchEngineDeleteImportTask_deleteOptionCategory(
			200, optionCategory1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory1.getId()));

		optionCategory1 = testBatchEngineDeleteImportTask_addOptionCategory();

		testBatchEngineDeleteImportTask_deleteOptionCategory(
			200, null, optionCategory1.getId());

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory1.getId()));

		optionCategory1 = testBatchEngineDeleteImportTask_addOptionCategory();
		OptionCategory optionCategory2 =
			testBatchEngineDeleteImportTask_addOptionCategory();

		testBatchEngineDeleteImportTask_deleteOptionCategory(
			200, optionCategory2.getExternalReferenceCode(),
			optionCategory1.getId());

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory1.getId()));
		assertHttpResponseStatusCode(
			200,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory2.getId()));

		testBatchEngineDeleteImportTask_deleteOptionCategory(
			200, optionCategory2.getExternalReferenceCode(),
			optionCategory1.getId());

		assertHttpResponseStatusCode(
			404,
			optionCategoryResource.getOptionCategoryHttpResponse(
				optionCategory2.getId()));
	}

	protected OptionCategory testBatchEngineDeleteImportTask_addOptionCategory()
		throws Exception {

		return testDeleteOptionCategory_addOptionCategory();
	}

	protected void testBatchEngineDeleteImportTask_deleteOptionCategory(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionCategory",
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected OptionCategory testGraphQLOptionCategory_addOptionCategory()
		throws Exception {

		return testGraphQLOptionCategory_addOptionCategory(
			randomOptionCategory());
	}

	protected OptionCategory testGraphQLOptionCategory_addOptionCategory(
			OptionCategory optionCategory)
		throws Exception {

		JSONDeserializer<OptionCategory> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(OptionCategory.class)) {

			if (getGraphQLValue(field.get(optionCategory)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(optionCategory)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createOptionCategory",
						new HashMap<String, Object>() {
							{
								put("optionCategory", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createOptionCategory"),
			OptionCategory.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		OptionCategory optionCategory, List<OptionCategory> optionCategories) {

		boolean contains = false;

		for (OptionCategory item : optionCategories) {
			if (equals(optionCategory, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			optionCategories + " does not contain " + optionCategory, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OptionCategory optionCategory1, OptionCategory optionCategory2) {

		Assert.assertTrue(
			optionCategory1 + " does not equal " + optionCategory2,
			equals(optionCategory1, optionCategory2));
	}

	protected void assertEquals(
		List<OptionCategory> optionCategories1,
		List<OptionCategory> optionCategories2) {

		Assert.assertEquals(optionCategories1.size(), optionCategories2.size());

		for (int i = 0; i < optionCategories1.size(); i++) {
			OptionCategory optionCategory1 = optionCategories1.get(i);
			OptionCategory optionCategory2 = optionCategories2.get(i);

			assertEquals(optionCategory1, optionCategory2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OptionCategory> optionCategories1,
		List<OptionCategory> optionCategories2) {

		Assert.assertEquals(optionCategories1.size(), optionCategories2.size());

		for (OptionCategory optionCategory1 : optionCategories1) {
			boolean contains = false;

			for (OptionCategory optionCategory2 : optionCategories2) {
				if (equals(optionCategory1, optionCategory2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				optionCategories2 + " does not contain " + optionCategory1,
				contains);
		}
	}

	protected void assertValid(OptionCategory optionCategory) throws Exception {
		boolean valid = true;

		if (optionCategory.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (optionCategory.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (optionCategory.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (optionCategory.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (optionCategory.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (optionCategory.getTitle() == null) {
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

	protected void assertValid(Page<OptionCategory> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OptionCategory> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OptionCategory> optionCategories = page.getItems();

		int size = optionCategories.size();

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
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						OptionCategory.class)) {

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
		OptionCategory optionCategory1, OptionCategory optionCategory2) {

		if (optionCategory1 == optionCategory2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)optionCategory1.getDescription(),
						(Map)optionCategory2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						optionCategory1.getExternalReferenceCode(),
						optionCategory2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						optionCategory1.getId(), optionCategory2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						optionCategory1.getKey(), optionCategory2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						optionCategory1.getPriority(),
						optionCategory2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!equals(
						(Map)optionCategory1.getTitle(),
						(Map)optionCategory2.getTitle())) {

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

		if (!(_optionCategoryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_optionCategoryResource;

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
		OptionCategory optionCategory) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = optionCategory.getExternalReferenceCode();

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

		if (entityFieldName.equals("key")) {
			Object object = optionCategory.getKey();

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

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(optionCategory.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("title")) {
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

	protected OptionCategory randomOptionCategory() throws Exception {
		return new OptionCategory() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				priority = RandomTestUtil.randomDouble();
			}
		};
	}

	protected OptionCategory randomIrrelevantOptionCategory() throws Exception {
		OptionCategory randomIrrelevantOptionCategory = randomOptionCategory();

		return randomIrrelevantOptionCategory;
	}

	protected OptionCategory randomPatchOptionCategory() throws Exception {
		return randomOptionCategory();
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

	protected OptionCategoryResource optionCategoryResource;
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
		LogFactoryUtil.getLog(BaseOptionCategoryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		OptionCategoryResource _optionCategoryResource;

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