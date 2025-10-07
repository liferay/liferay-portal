/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Currency;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.CurrencyResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.CurrencySerDes;
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
public abstract class BaseCurrencyResourceTestCase {

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

		_currencyResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		currencyResource = CurrencyResource.builder(
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

		Currency currency1 = randomCurrency();

		String json = objectMapper.writeValueAsString(currency1);

		Currency currency2 = CurrencySerDes.toDTO(json);

		Assert.assertTrue(equals(currency1, currency2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Currency currency = randomCurrency();

		String json1 = objectMapper.writeValueAsString(currency);
		String json2 = CurrencySerDes.toJSON(currency);

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

		Currency currency = randomCurrency();

		currency.setCode(regex);
		currency.setExternalReferenceCode(regex);
		currency.setSymbol(regex);

		String json = CurrencySerDes.toJSON(currency);

		Assert.assertFalse(json.contains(regex));

		currency = CurrencySerDes.toDTO(json);

		Assert.assertEquals(regex, currency.getCode());
		Assert.assertEquals(regex, currency.getExternalReferenceCode());
		Assert.assertEquals(regex, currency.getSymbol());
	}

	@Test
	public void testDeleteCurrency() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Currency currency = testDeleteCurrency_addCurrency();

		assertHttpResponseStatusCode(
			204, currencyResource.deleteCurrencyHttpResponse(currency.getId()));

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency.getId()));
		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(0L));
	}

	protected Currency testDeleteCurrency_addCurrency() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCurrency() throws Exception {

		// No namespace

		Currency currency1 = testGraphQLDeleteCurrency_addCurrency();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCurrency",
						new HashMap<String, Object>() {
							{
								put("id", currency1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteCurrency"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"currency",
					new HashMap<String, Object>() {
						{
							put("id", currency1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Currency currency2 = testGraphQLDeleteCurrency_addCurrency();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteCurrency",
							new HashMap<String, Object>() {
								{
									put("id", currency2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteCurrency"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"currency",
						new HashMap<String, Object>() {
							{
								put("id", currency2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Currency testGraphQLDeleteCurrency_addCurrency()
		throws Exception {

		return testGraphQLCurrency_addCurrency();
	}

	@Test
	public void testDeleteCurrencyBatch() throws Exception {
		Currency currency1 = testDeleteCurrencyBatch_addCurrency();

		testDeleteCurrencyBatch_deleteCurrency(
			202, currency1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency1.getId()));

		currency1 = testDeleteCurrencyBatch_addCurrency();

		testDeleteCurrencyBatch_deleteCurrency(202, null, currency1.getId());

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency1.getId()));

		currency1 = testDeleteCurrencyBatch_addCurrency();
		Currency currency2 = testDeleteCurrencyBatch_addCurrency();

		testDeleteCurrencyBatch_deleteCurrency(
			202, currency2.getExternalReferenceCode(), currency1.getId());

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency1.getId()));
		assertHttpResponseStatusCode(
			200, currencyResource.getCurrencyHttpResponse(currency2.getId()));

		testDeleteCurrencyBatch_deleteCurrency(
			202, currency2.getExternalReferenceCode(), currency1.getId());

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency2.getId()));
	}

	protected Currency testDeleteCurrencyBatch_addCurrency() throws Exception {
		return testDeleteCurrency_addCurrency();
	}

	protected void testDeleteCurrencyBatch_deleteCurrency(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			currencyResource.deleteCurrencyBatchHttpResponse(
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
	public void testDeleteCurrencyByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Currency currency =
			testDeleteCurrencyByExternalReferenceCode_addCurrency();

		assertHttpResponseStatusCode(
			204,
			currencyResource.deleteCurrencyByExternalReferenceCodeHttpResponse(
				currency.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			currencyResource.getCurrencyByExternalReferenceCodeHttpResponse(
				currency.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			currencyResource.getCurrencyByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected Currency testDeleteCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCurrencyByExternalReferenceCode()
		throws Exception {

		// No namespace

		Currency currency1 =
			testGraphQLDeleteCurrencyByExternalReferenceCode_addCurrency();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCurrencyByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										currency1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteCurrencyByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"currencyByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + currency1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Currency currency2 =
			testGraphQLDeleteCurrencyByExternalReferenceCode_addCurrency();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteCurrencyByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											currency2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteCurrencyByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"currencyByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										currency2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Currency
			testGraphQLDeleteCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		return testGraphQLCurrency_addCurrency();
	}

	@Test
	public void testGetCurrenciesPage() throws Exception {
		Page<Currency> page = currencyResource.getCurrenciesPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Currency currency1 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		Currency currency2 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		page = currencyResource.getCurrenciesPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(currency1, (List<Currency>)page.getItems());
		assertContains(currency2, (List<Currency>)page.getItems());
		assertValid(page, testGetCurrenciesPage_getExpectedActions());

		currencyResource.deleteCurrency(currency1.getId());

		currencyResource.deleteCurrency(currency2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetCurrenciesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCurrenciesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Currency currency1 = randomCurrency();

		currency1 = testGetCurrenciesPage_addCurrency(currency1);

		for (EntityField entityField : entityFields) {
			Page<Currency> page = currencyResource.getCurrenciesPage(
				null, getFilterString(entityField, "between", currency1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(currency1),
				(List<Currency>)page.getItems());
		}
	}

	@Test
	public void testGetCurrenciesPageWithFilterDoubleEquals() throws Exception {
		testGetCurrenciesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetCurrenciesPageWithFilterStringContains()
		throws Exception {

		testGetCurrenciesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetCurrenciesPageWithFilterStringEquals() throws Exception {
		testGetCurrenciesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetCurrenciesPageWithFilterStringStartsWith()
		throws Exception {

		testGetCurrenciesPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetCurrenciesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Currency currency1 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Currency currency2 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		for (EntityField entityField : entityFields) {
			Page<Currency> page = currencyResource.getCurrenciesPage(
				null, getFilterString(entityField, operator, currency1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(currency1),
				(List<Currency>)page.getItems());
		}
	}

	@Test
	public void testGetCurrenciesPageWithPagination() throws Exception {
		Page<Currency> currenciesPage = currencyResource.getCurrenciesPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(currenciesPage.getTotalCount());

		Currency currency1 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		Currency currency2 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		Currency currency3 = testGetCurrenciesPage_addCurrency(
			randomCurrency());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Currency> page1 = currencyResource.getCurrenciesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(currency1, (List<Currency>)page1.getItems());

			Page<Currency> page2 = currencyResource.getCurrenciesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(currency2, (List<Currency>)page2.getItems());

			Page<Currency> page3 = currencyResource.getCurrenciesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(currency3, (List<Currency>)page3.getItems());
		}
		else {
			Page<Currency> page1 = currencyResource.getCurrenciesPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Currency> currencies1 = (List<Currency>)page1.getItems();

			Assert.assertEquals(
				currencies1.toString(), totalCount + 2, currencies1.size());

			Page<Currency> page2 = currencyResource.getCurrenciesPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Currency> currencies2 = (List<Currency>)page2.getItems();

			Assert.assertEquals(currencies2.toString(), 1, currencies2.size());

			Page<Currency> page3 = currencyResource.getCurrenciesPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(currency1, (List<Currency>)page3.getItems());
			assertContains(currency2, (List<Currency>)page3.getItems());
			assertContains(currency3, (List<Currency>)page3.getItems());
		}
	}

	@Test
	public void testGetCurrenciesPageWithSortDateTime() throws Exception {
		testGetCurrenciesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, currency1, currency2) -> {
				BeanTestUtil.setProperty(
					currency1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetCurrenciesPageWithSortDouble() throws Exception {
		testGetCurrenciesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, currency1, currency2) -> {
				BeanTestUtil.setProperty(currency1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(currency2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetCurrenciesPageWithSortInteger() throws Exception {
		testGetCurrenciesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, currency1, currency2) -> {
				BeanTestUtil.setProperty(currency1, entityField.getName(), 0);
				BeanTestUtil.setProperty(currency2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetCurrenciesPageWithSortString() throws Exception {
		testGetCurrenciesPageWithSort(
			EntityField.Type.STRING,
			(entityField, currency1, currency2) -> {
				Class<?> clazz = currency1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						currency1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						currency2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						currency1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						currency2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						currency1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						currency2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetCurrenciesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Currency, Currency, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Currency currency1 = randomCurrency();
		Currency currency2 = randomCurrency();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, currency1, currency2);
		}

		currency1 = testGetCurrenciesPage_addCurrency(currency1);

		currency2 = testGetCurrenciesPage_addCurrency(currency2);

		Page<Currency> page = currencyResource.getCurrenciesPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Currency> ascPage = currencyResource.getCurrenciesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(currency1, (List<Currency>)ascPage.getItems());
			assertContains(currency2, (List<Currency>)ascPage.getItems());

			Page<Currency> descPage = currencyResource.getCurrenciesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(currency2, (List<Currency>)descPage.getItems());
			assertContains(currency1, (List<Currency>)descPage.getItems());
		}
	}

	protected Currency testGetCurrenciesPage_addCurrency(Currency currency)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCurrenciesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"currencies",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject currenciesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/currencies");

		long totalCount = currenciesJSONObject.getLong("totalCount");

		Currency currency1 = testGraphQLCurrency_addCurrency(randomCurrency());

		Currency currency2 = testGraphQLCurrency_addCurrency(randomCurrency());

		currenciesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/currencies");

		Assert.assertEquals(
			totalCount + 2, currenciesJSONObject.getLong("totalCount"));

		assertContains(
			currency1,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));
		assertContains(
			currency2,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		currenciesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/currencies");

		Assert.assertEquals(
			totalCount + 2, currenciesJSONObject.getLong("totalCount"));

		assertContains(
			currency1,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));
		assertContains(
			currency2,
			Arrays.asList(
				CurrencySerDes.toDTOs(
					currenciesJSONObject.getString("items"))));
	}

	@Test
	public void testGetCurrency() throws Exception {
		Currency postCurrency = testGetCurrency_addCurrency();

		Currency getCurrency = currencyResource.getCurrency(
			postCurrency.getId());

		assertEquals(postCurrency, getCurrency);
		assertValid(getCurrency);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Currency postCurrency = testGetCurrency_addCurrency();

		Currency getCurrency = currencyResource.getCurrency(
			postCurrency.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Currency"
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

		Object item = vulcanCRUDItemDelegate.getItem(postCurrency.getId());

		assertEquals(getCurrency, CurrencySerDes.toDTO(item.toString()));
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

	protected Currency testGetCurrency_addCurrency() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCurrency() throws Exception {
		Currency currency = testGraphQLGetCurrency_addCurrency();

		// No namespace

		Assert.assertTrue(
			equals(
				currency,
				CurrencySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"currency",
								new HashMap<String, Object>() {
									{
										put("id", currency.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/currency"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				currency,
				CurrencySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"currency",
									new HashMap<String, Object>() {
										{
											put("id", currency.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/currency"))));
	}

	@Test
	public void testGraphQLGetCurrencyNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"currency",
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
							"currency",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Currency testGraphQLGetCurrency_addCurrency() throws Exception {
		return testGraphQLCurrency_addCurrency();
	}

	@Test
	public void testGetCurrencyByExternalReferenceCode() throws Exception {
		Currency postCurrency =
			testGetCurrencyByExternalReferenceCode_addCurrency();

		Currency getCurrency =
			currencyResource.getCurrencyByExternalReferenceCode(
				postCurrency.getExternalReferenceCode());

		assertEquals(postCurrency, getCurrency);
		assertValid(getCurrency);
	}

	protected Currency testGetCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCurrencyByExternalReferenceCode()
		throws Exception {

		Currency currency =
			testGraphQLGetCurrencyByExternalReferenceCode_addCurrency();

		// No namespace

		Assert.assertTrue(
			equals(
				currency,
				CurrencySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"currencyByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												currency.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/currencyByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				currency,
				CurrencySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"currencyByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													currency.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/currencyByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetCurrencyByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"currencyByExternalReferenceCode",
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
							"currencyByExternalReferenceCode",
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

	protected Currency
			testGraphQLGetCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		return testGraphQLCurrency_addCurrency();
	}

	@Test
	public void testPatchCurrency() throws Exception {
		Currency postCurrency = testPatchCurrency_addCurrency();

		Currency randomPatchCurrency = randomPatchCurrency();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Currency patchCurrency = currencyResource.patchCurrency(
			postCurrency.getId(), randomPatchCurrency);

		Currency expectedPatchCurrency = postCurrency.clone();

		BeanTestUtil.copyProperties(randomPatchCurrency, expectedPatchCurrency);

		Currency getCurrency = currencyResource.getCurrency(
			patchCurrency.getId());

		assertEquals(expectedPatchCurrency, getCurrency);
		assertValid(getCurrency);
	}

	protected Currency testPatchCurrency_addCurrency() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchCurrencyByExternalReferenceCode() throws Exception {
		Currency postCurrency =
			testPatchCurrencyByExternalReferenceCode_addCurrency();

		Currency randomPatchCurrency = randomPatchCurrency();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Currency patchCurrency =
			currencyResource.patchCurrencyByExternalReferenceCode(
				postCurrency.getExternalReferenceCode(), randomPatchCurrency);

		Currency expectedPatchCurrency = postCurrency.clone();

		BeanTestUtil.copyProperties(randomPatchCurrency, expectedPatchCurrency);

		Currency getCurrency =
			currencyResource.getCurrencyByExternalReferenceCode(
				patchCurrency.getExternalReferenceCode());

		assertEquals(expectedPatchCurrency, getCurrency);
		assertValid(getCurrency);
	}

	protected Currency testPatchCurrencyByExternalReferenceCode_addCurrency()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCurrency() throws Exception {
		Currency randomCurrency = randomCurrency();

		Currency postCurrency = testPostCurrency_addCurrency(randomCurrency);

		assertEquals(randomCurrency, postCurrency);
		assertValid(postCurrency);
	}

	protected Currency testPostCurrency_addCurrency(Currency currency)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostCurrency() throws Exception {
		Currency randomCurrency = randomCurrency();

		Currency currency = testGraphQLCurrency_addCurrency(randomCurrency);

		Assert.assertTrue(equals(randomCurrency, currency));
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Currency currency1 = testBatchEngineDeleteImportTask_addCurrency();

		testBatchEngineDeleteImportTask_deleteCurrency(
			200, currency1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency1.getId()));

		currency1 = testBatchEngineDeleteImportTask_addCurrency();

		testBatchEngineDeleteImportTask_deleteCurrency(
			200, null, currency1.getId());

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency1.getId()));

		currency1 = testBatchEngineDeleteImportTask_addCurrency();
		Currency currency2 = testBatchEngineDeleteImportTask_addCurrency();

		testBatchEngineDeleteImportTask_deleteCurrency(
			200, currency2.getExternalReferenceCode(), currency1.getId());

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency1.getId()));
		assertHttpResponseStatusCode(
			200, currencyResource.getCurrencyHttpResponse(currency2.getId()));

		testBatchEngineDeleteImportTask_deleteCurrency(
			200, currency2.getExternalReferenceCode(), currency1.getId());

		assertHttpResponseStatusCode(
			404, currencyResource.getCurrencyHttpResponse(currency2.getId()));
	}

	protected Currency testBatchEngineDeleteImportTask_addCurrency()
		throws Exception {

		return testDeleteCurrency_addCurrency();
	}

	protected void testBatchEngineDeleteImportTask_deleteCurrency(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Currency",
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

	protected Currency testGraphQLCurrency_addCurrency() throws Exception {
		return testGraphQLCurrency_addCurrency(randomCurrency());
	}

	protected Currency testGraphQLCurrency_addCurrency(Currency currency)
		throws Exception {

		JSONDeserializer<Currency> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Currency.class)) {

			if (getGraphQLValue(field.get(currency)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(currency)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createCurrency",
						new HashMap<String, Object>() {
							{
								put("currency", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createCurrency"),
			Currency.class);
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
		Currency currency, List<Currency> currencies) {

		boolean contains = false;

		for (Currency item : currencies) {
			if (equals(currency, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			currencies + " does not contain " + currency, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Currency currency1, Currency currency2) {
		Assert.assertTrue(
			currency1 + " does not equal " + currency2,
			equals(currency1, currency2));
	}

	protected void assertEquals(
		List<Currency> currencies1, List<Currency> currencies2) {

		Assert.assertEquals(currencies1.size(), currencies2.size());

		for (int i = 0; i < currencies1.size(); i++) {
			Currency currency1 = currencies1.get(i);
			Currency currency2 = currencies2.get(i);

			assertEquals(currency1, currency2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Currency> currencies1, List<Currency> currencies2) {

		Assert.assertEquals(currencies1.size(), currencies2.size());

		for (Currency currency1 : currencies1) {
			boolean contains = false;

			for (Currency currency2 : currencies2) {
				if (equals(currency1, currency2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				currencies2 + " does not contain " + currency1, contains);
		}
	}

	protected void assertValid(Currency currency) throws Exception {
		boolean valid = true;

		if (currency.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (currency.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("code", additionalAssertFieldName)) {
				if (currency.getCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (currency.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("formatPattern", additionalAssertFieldName)) {
				if (currency.getFormatPattern() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"maxFractionDigits", additionalAssertFieldName)) {

				if (currency.getMaxFractionDigits() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"minFractionDigits", additionalAssertFieldName)) {

				if (currency.getMinFractionDigits() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (currency.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (currency.getPrimary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (currency.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("rate", additionalAssertFieldName)) {
				if (currency.getRate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roundingMode", additionalAssertFieldName)) {
				if (currency.getRoundingMode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("symbol", additionalAssertFieldName)) {
				if (currency.getSymbol() == null) {
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

	protected void assertValid(Page<Currency> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Currency> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Currency> currencies = page.getItems();

		int size = currencies.size();

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
						Currency.class)) {

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

	protected boolean equals(Currency currency1, Currency currency2) {
		if (currency1 == currency2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						currency1.getActive(), currency2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("code", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						currency1.getCode(), currency2.getCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						currency1.getExternalReferenceCode(),
						currency2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("formatPattern", additionalAssertFieldName)) {
				if (!equals(
						(Map)currency1.getFormatPattern(),
						(Map)currency2.getFormatPattern())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(currency1.getId(), currency2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"maxFractionDigits", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						currency1.getMaxFractionDigits(),
						currency2.getMaxFractionDigits())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"minFractionDigits", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						currency1.getMinFractionDigits(),
						currency2.getMinFractionDigits())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)currency1.getName(), (Map)currency2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						currency1.getPrimary(), currency2.getPrimary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						currency1.getPriority(), currency2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("rate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						currency1.getRate(), currency2.getRate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roundingMode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						currency1.getRoundingMode(),
						currency2.getRoundingMode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("symbol", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						currency1.getSymbol(), currency2.getSymbol())) {

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

		if (!(_currencyResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_currencyResource;

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
		EntityField entityField, String operator, Currency currency) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("code")) {
			Object object = currency.getCode();

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

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = currency.getExternalReferenceCode();

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

		if (entityFieldName.equals("formatPattern")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("maxFractionDigits")) {
			sb.append(String.valueOf(currency.getMaxFractionDigits()));

			return sb.toString();
		}

		if (entityFieldName.equals("minFractionDigits")) {
			sb.append(String.valueOf(currency.getMinFractionDigits()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("primary")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(currency.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("rate")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("roundingMode")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("symbol")) {
			Object object = currency.getSymbol();

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

	protected Currency randomCurrency() throws Exception {
		return new Currency() {
			{
				active = RandomTestUtil.randomBoolean();
				code = StringUtil.toLowerCase(RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				maxFractionDigits = RandomTestUtil.randomInt();
				minFractionDigits = RandomTestUtil.randomInt();
				primary = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				symbol = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Currency randomIrrelevantCurrency() throws Exception {
		Currency randomIrrelevantCurrency = randomCurrency();

		return randomIrrelevantCurrency;
	}

	protected Currency randomPatchCurrency() throws Exception {
		return randomCurrency();
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

	protected CurrencyResource currencyResource;
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
		LogFactoryUtil.getLog(BaseCurrencyResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.catalog.resource.v1_0.
			CurrencyResource _currencyResource;

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