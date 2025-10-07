/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.address.client.dto.v1_0.Country;
import com.liferay.headless.admin.address.client.http.HttpInvoker;
import com.liferay.headless.admin.address.client.pagination.Page;
import com.liferay.headless.admin.address.client.pagination.Pagination;
import com.liferay.headless.admin.address.client.resource.v1_0.CountryResource;
import com.liferay.headless.admin.address.client.serdes.v1_0.CountrySerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
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
 * @author Drew Brokke
 * @generated
 */
@Generated("")
public abstract class BaseCountryResourceTestCase {

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

		_countryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		countryResource = CountryResource.builder(
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

		Country country1 = randomCountry();

		String json = objectMapper.writeValueAsString(country1);

		Country country2 = CountrySerDes.toDTO(json);

		Assert.assertTrue(equals(country1, country2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Country country = randomCountry();

		String json1 = objectMapper.writeValueAsString(country);
		String json2 = CountrySerDes.toJSON(country);

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

		Country country = randomCountry();

		country.setA2(regex);
		country.setA3(regex);
		country.setName(regex);

		String json = CountrySerDes.toJSON(country);

		Assert.assertFalse(json.contains(regex));

		country = CountrySerDes.toDTO(json);

		Assert.assertEquals(regex, country.getA2());
		Assert.assertEquals(regex, country.getA3());
		Assert.assertEquals(regex, country.getName());
	}

	@Test
	public void testDeleteCountry() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Country country = testDeleteCountry_addCountry();

		assertHttpResponseStatusCode(
			204, countryResource.deleteCountryHttpResponse(country.getId()));

		assertHttpResponseStatusCode(
			404, countryResource.getCountryHttpResponse(country.getId()));
		assertHttpResponseStatusCode(
			404, countryResource.getCountryHttpResponse(0L));
	}

	protected Country testDeleteCountry_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCountry() throws Exception {

		// No namespace

		Country country1 = testGraphQLDeleteCountry_addCountry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCountry",
						new HashMap<String, Object>() {
							{
								put("countryId", country1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteCountry"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"country",
					new HashMap<String, Object>() {
						{
							put("countryId", country1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminAddress_v1_0

		Country country2 = testGraphQLDeleteCountry_addCountry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminAddress_v1_0",
						new GraphQLField(
							"deleteCountry",
							new HashMap<String, Object>() {
								{
									put("countryId", country2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminAddress_v1_0",
				"Object/deleteCountry"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminAddress_v1_0",
					new GraphQLField(
						"country",
						new HashMap<String, Object>() {
							{
								put("countryId", country2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Country testGraphQLDeleteCountry_addCountry() throws Exception {
		return testGraphQLCountry_addCountry();
	}

	@Test
	public void testDeleteCountryBatch() throws Exception {
		Country country1 = testDeleteCountryBatch_addCountry();

		testDeleteCountryBatch_deleteCountry(202, null, country1.getId());

		assertHttpResponseStatusCode(
			404, countryResource.getCountryHttpResponse(country1.getId()));
	}

	protected Country testDeleteCountryBatch_addCountry() throws Exception {
		return testDeleteCountry_addCountry();
	}

	protected void testDeleteCountryBatch_deleteCountry(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			countryResource.deleteCountryBatchHttpResponse(
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
	public void testGetCountriesPage() throws Exception {
		Page<Country> page = countryResource.getCountriesPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Country country1 = testGetCountriesPage_addCountry(randomCountry());

		Country country2 = testGetCountriesPage_addCountry(randomCountry());

		page = countryResource.getCountriesPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(country1, (List<Country>)page.getItems());
		assertContains(country2, (List<Country>)page.getItems());
		assertValid(page, testGetCountriesPage_getExpectedActions());

		countryResource.deleteCountry(country1.getId());

		countryResource.deleteCountry(country2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetCountriesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCountriesPageWithPagination() throws Exception {
		Page<Country> countriesPage = countryResource.getCountriesPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(countriesPage.getTotalCount());

		Country country1 = testGetCountriesPage_addCountry(randomCountry());

		Country country2 = testGetCountriesPage_addCountry(randomCountry());

		Country country3 = testGetCountriesPage_addCountry(randomCountry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Country> page1 = countryResource.getCountriesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(country1, (List<Country>)page1.getItems());

			Page<Country> page2 = countryResource.getCountriesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(country2, (List<Country>)page2.getItems());

			Page<Country> page3 = countryResource.getCountriesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(country3, (List<Country>)page3.getItems());
		}
		else {
			Page<Country> page1 = countryResource.getCountriesPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Country> countries1 = (List<Country>)page1.getItems();

			Assert.assertEquals(
				countries1.toString(), totalCount + 2, countries1.size());

			Page<Country> page2 = countryResource.getCountriesPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Country> countries2 = (List<Country>)page2.getItems();

			Assert.assertEquals(countries2.toString(), 1, countries2.size());

			Page<Country> page3 = countryResource.getCountriesPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(country1, (List<Country>)page3.getItems());
			assertContains(country2, (List<Country>)page3.getItems());
			assertContains(country3, (List<Country>)page3.getItems());
		}
	}

	@Test
	public void testGetCountriesPageWithSortDateTime() throws Exception {
		testGetCountriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, country1, country2) -> {
				BeanTestUtil.setProperty(
					country1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetCountriesPageWithSortDouble() throws Exception {
		testGetCountriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, country1, country2) -> {
				BeanTestUtil.setProperty(country1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(country2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetCountriesPageWithSortInteger() throws Exception {
		testGetCountriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, country1, country2) -> {
				BeanTestUtil.setProperty(country1, entityField.getName(), 0);
				BeanTestUtil.setProperty(country2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetCountriesPageWithSortString() throws Exception {
		testGetCountriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, country1, country2) -> {
				Class<?> clazz = country1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						country1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						country2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						country1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						country2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						country1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						country2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetCountriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Country, Country, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Country country1 = randomCountry();
		Country country2 = randomCountry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, country1, country2);
		}

		country1 = testGetCountriesPage_addCountry(country1);

		country2 = testGetCountriesPage_addCountry(country2);

		Page<Country> page = countryResource.getCountriesPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Country> ascPage = countryResource.getCountriesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(country1, (List<Country>)ascPage.getItems());
			assertContains(country2, (List<Country>)ascPage.getItems());

			Page<Country> descPage = countryResource.getCountriesPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(country2, (List<Country>)descPage.getItems());
			assertContains(country1, (List<Country>)descPage.getItems());
		}
	}

	protected Country testGetCountriesPage_addCountry(Country country)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCountriesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"countries",
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

		JSONObject countriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/countries");

		long totalCount = countriesJSONObject.getLong("totalCount");

		Country country1 = testGraphQLCountry_addCountry(randomCountry());

		Country country2 = testGraphQLCountry_addCountry(randomCountry());

		countriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/countries");

		Assert.assertEquals(
			totalCount + 2, countriesJSONObject.getLong("totalCount"));

		assertContains(
			country1,
			Arrays.asList(
				CountrySerDes.toDTOs(countriesJSONObject.getString("items"))));
		assertContains(
			country2,
			Arrays.asList(
				CountrySerDes.toDTOs(countriesJSONObject.getString("items"))));

		// Using the namespace headlessAdminAddress_v1_0

		countriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminAddress_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminAddress_v1_0",
			"JSONObject/countries");

		Assert.assertEquals(
			totalCount + 2, countriesJSONObject.getLong("totalCount"));

		assertContains(
			country1,
			Arrays.asList(
				CountrySerDes.toDTOs(countriesJSONObject.getString("items"))));
		assertContains(
			country2,
			Arrays.asList(
				CountrySerDes.toDTOs(countriesJSONObject.getString("items"))));
	}

	@Test
	public void testGetCountry() throws Exception {
		Country postCountry = testGetCountry_addCountry();

		Country getCountry = countryResource.getCountry(postCountry.getId());

		assertEquals(postCountry, getCountry);
		assertValid(getCountry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Country postCountry = testGetCountry_addCountry();

		Country getCountry = countryResource.getCountry(postCountry.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.address.dto.v1_0.Country"
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

		Object item = vulcanCRUDItemDelegate.getItem(postCountry.getId());

		assertEquals(getCountry, CountrySerDes.toDTO(item.toString()));
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

	protected Country testGetCountry_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCountry() throws Exception {
		Country country = testGraphQLGetCountry_addCountry();

		// No namespace

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"country",
								new HashMap<String, Object>() {
									{
										put("countryId", country.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/country"))));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminAddress_v1_0",
								new GraphQLField(
									"country",
									new HashMap<String, Object>() {
										{
											put("countryId", country.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminAddress_v1_0",
						"Object/country"))));
	}

	@Test
	public void testGraphQLGetCountryNotFound() throws Exception {
		Long irrelevantCountryId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"country",
						new HashMap<String, Object>() {
							{
								put("countryId", irrelevantCountryId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminAddress_v1_0",
						new GraphQLField(
							"country",
							new HashMap<String, Object>() {
								{
									put("countryId", irrelevantCountryId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Country testGraphQLGetCountry_addCountry() throws Exception {
		return testGraphQLCountry_addCountry();
	}

	@Test
	public void testGetCountryByA2() throws Exception {
		Country postCountry = testGetCountryByA2_addCountry();

		Country getCountry = countryResource.getCountryByA2(
			postCountry.getA2());

		assertEquals(postCountry, getCountry);
		assertValid(getCountry);
	}

	protected Country testGetCountryByA2_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCountryByA2() throws Exception {
		Country country = testGraphQLGetCountryByA2_addCountry();

		// No namespace

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"countryByA2",
								new HashMap<String, Object>() {
									{
										put(
											"a2",
											"\"" + country.getA2() + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/countryByA2"))));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminAddress_v1_0",
								new GraphQLField(
									"countryByA2",
									new HashMap<String, Object>() {
										{
											put(
												"a2",
												"\"" + country.getA2() + "\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminAddress_v1_0",
						"Object/countryByA2"))));
	}

	@Test
	public void testGraphQLGetCountryByA2NotFound() throws Exception {
		String irrelevantA2 = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"countryByA2",
						new HashMap<String, Object>() {
							{
								put("a2", irrelevantA2);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminAddress_v1_0",
						new GraphQLField(
							"countryByA2",
							new HashMap<String, Object>() {
								{
									put("a2", irrelevantA2);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Country testGraphQLGetCountryByA2_addCountry() throws Exception {
		return testGraphQLCountry_addCountry();
	}

	@Test
	public void testGetCountryByA3() throws Exception {
		Country postCountry = testGetCountryByA3_addCountry();

		Country getCountry = countryResource.getCountryByA3(
			postCountry.getA3());

		assertEquals(postCountry, getCountry);
		assertValid(getCountry);
	}

	protected Country testGetCountryByA3_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCountryByA3() throws Exception {
		Country country = testGraphQLGetCountryByA3_addCountry();

		// No namespace

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"countryByA3",
								new HashMap<String, Object>() {
									{
										put(
											"a3",
											"\"" + country.getA3() + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/countryByA3"))));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminAddress_v1_0",
								new GraphQLField(
									"countryByA3",
									new HashMap<String, Object>() {
										{
											put(
												"a3",
												"\"" + country.getA3() + "\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminAddress_v1_0",
						"Object/countryByA3"))));
	}

	@Test
	public void testGraphQLGetCountryByA3NotFound() throws Exception {
		String irrelevantA3 = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"countryByA3",
						new HashMap<String, Object>() {
							{
								put("a3", irrelevantA3);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminAddress_v1_0",
						new GraphQLField(
							"countryByA3",
							new HashMap<String, Object>() {
								{
									put("a3", irrelevantA3);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Country testGraphQLGetCountryByA3_addCountry() throws Exception {
		return testGraphQLCountry_addCountry();
	}

	@Test
	public void testGetCountryByName() throws Exception {
		Country postCountry = testGetCountryByName_addCountry();

		Country getCountry = countryResource.getCountryByName(
			postCountry.getName());

		assertEquals(postCountry, getCountry);
		assertValid(getCountry);
	}

	protected Country testGetCountryByName_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCountryByName() throws Exception {
		Country country = testGraphQLGetCountryByName_addCountry();

		// No namespace

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"countryByName",
								new HashMap<String, Object>() {
									{
										put(
											"name",
											"\"" + country.getName() + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/countryByName"))));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminAddress_v1_0",
								new GraphQLField(
									"countryByName",
									new HashMap<String, Object>() {
										{
											put(
												"name",
												"\"" + country.getName() +
													"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminAddress_v1_0",
						"Object/countryByName"))));
	}

	@Test
	public void testGraphQLGetCountryByNameNotFound() throws Exception {
		String irrelevantName = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"countryByName",
						new HashMap<String, Object>() {
							{
								put("name", irrelevantName);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminAddress_v1_0",
						new GraphQLField(
							"countryByName",
							new HashMap<String, Object>() {
								{
									put("name", irrelevantName);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Country testGraphQLGetCountryByName_addCountry()
		throws Exception {

		return testGraphQLCountry_addCountry();
	}

	@Test
	public void testGetCountryByNumber() throws Exception {
		Country postCountry = testGetCountryByNumber_addCountry();

		Country getCountry = countryResource.getCountryByNumber(
			postCountry.getNumber());

		assertEquals(postCountry, getCountry);
		assertValid(getCountry);
	}

	protected Country testGetCountryByNumber_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCountryByNumber() throws Exception {
		Country country = testGraphQLGetCountryByNumber_addCountry();

		// No namespace

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"countryByNumber",
								new HashMap<String, Object>() {
									{
										put("number", country.getNumber());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/countryByNumber"))));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertTrue(
			equals(
				country,
				CountrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminAddress_v1_0",
								new GraphQLField(
									"countryByNumber",
									new HashMap<String, Object>() {
										{
											put("number", country.getNumber());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminAddress_v1_0",
						"Object/countryByNumber"))));
	}

	@Test
	public void testGraphQLGetCountryByNumberNotFound() throws Exception {
		Integer irrelevantNumber = RandomTestUtil.randomInt();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"countryByNumber",
						new HashMap<String, Object>() {
							{
								put("number", irrelevantNumber);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminAddress_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminAddress_v1_0",
						new GraphQLField(
							"countryByNumber",
							new HashMap<String, Object>() {
								{
									put("number", irrelevantNumber);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Country testGraphQLGetCountryByNumber_addCountry()
		throws Exception {

		return testGraphQLCountry_addCountry();
	}

	@Test
	public void testPatchCountry() throws Exception {
		Country postCountry = testPatchCountry_addCountry();

		Country randomPatchCountry = randomPatchCountry();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Country patchCountry = countryResource.patchCountry(
			postCountry.getId(), randomPatchCountry);

		Country expectedPatchCountry = postCountry.clone();

		BeanTestUtil.copyProperties(randomPatchCountry, expectedPatchCountry);

		Country getCountry = countryResource.getCountry(patchCountry.getId());

		assertEquals(expectedPatchCountry, getCountry);
		assertValid(getCountry);
	}

	protected Country testPatchCountry_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCountry() throws Exception {
		Country randomCountry = randomCountry();

		Country postCountry = testPostCountry_addCountry(randomCountry);

		assertEquals(randomCountry, postCountry);
		assertValid(postCountry);
	}

	protected Country testPostCountry_addCountry(Country country)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostCountry() throws Exception {
		Country randomCountry = randomCountry();

		Country country = testGraphQLCountry_addCountry(randomCountry);

		Assert.assertTrue(equals(randomCountry, country));
	}

	@Test
	public void testPutCountry() throws Exception {
		Country postCountry = testPutCountry_addCountry();

		Country randomCountry = randomCountry();

		Country putCountry = countryResource.putCountry(
			postCountry.getId(), randomCountry);

		assertEquals(randomCountry, putCountry);
		assertValid(putCountry);

		Country getCountry = countryResource.getCountry(putCountry.getId());

		assertEquals(randomCountry, getCountry);
		assertValid(getCountry);
	}

	protected Country testPutCountry_addCountry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Country country1 = testBatchEngineDeleteImportTask_addCountry();

		testBatchEngineDeleteImportTask_deleteCountry(
			200, null, country1.getId());

		assertHttpResponseStatusCode(
			404, countryResource.getCountryHttpResponse(country1.getId()));
	}

	protected Country testBatchEngineDeleteImportTask_addCountry()
		throws Exception {

		return testDeleteCountry_addCountry();
	}

	protected void testBatchEngineDeleteImportTask_deleteCountry(
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
				"com.liferay.headless.admin.address.dto.v1_0.Country", null,
				null, null, null,
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

	protected Country testGraphQLCountry_addCountry() throws Exception {
		return testGraphQLCountry_addCountry(randomCountry());
	}

	protected Country testGraphQLCountry_addCountry(Country country)
		throws Exception {

		JSONDeserializer<Country> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Country.class)) {
			if (getGraphQLValue(field.get(country)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(country)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createCountry",
						new HashMap<String, Object>() {
							{
								put("country", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createCountry"),
			Country.class);
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

	protected void assertContains(Country country, List<Country> countries) {
		boolean contains = false;

		for (Country item : countries) {
			if (equals(country, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(countries + " does not contain " + country, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Country country1, Country country2) {
		Assert.assertTrue(
			country1 + " does not equal " + country2,
			equals(country1, country2));
	}

	protected void assertEquals(
		List<Country> countries1, List<Country> countries2) {

		Assert.assertEquals(countries1.size(), countries2.size());

		for (int i = 0; i < countries1.size(); i++) {
			Country country1 = countries1.get(i);
			Country country2 = countries2.get(i);

			assertEquals(country1, country2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Country> countries1, List<Country> countries2) {

		Assert.assertEquals(countries1.size(), countries2.size());

		for (Country country1 : countries1) {
			boolean contains = false;

			for (Country country2 : countries2) {
				if (equals(country1, country2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				countries2 + " does not contain " + country1, contains);
		}
	}

	protected void assertValid(Country country) throws Exception {
		boolean valid = true;

		if (country.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("a2", additionalAssertFieldName)) {
				if (country.getA2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("a3", additionalAssertFieldName)) {
				if (country.getA3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (country.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("billingAllowed", additionalAssertFieldName)) {
				if (country.getBillingAllowed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"groupFilterEnabled", additionalAssertFieldName)) {

				if (country.getGroupFilterEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("idd", additionalAssertFieldName)) {
				if (country.getIdd() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (country.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("number", additionalAssertFieldName)) {
				if (country.getNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("position", additionalAssertFieldName)) {
				if (country.getPosition() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("regions", additionalAssertFieldName)) {
				if (country.getRegions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingAllowed", additionalAssertFieldName)) {
				if (country.getShippingAllowed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subjectToVAT", additionalAssertFieldName)) {
				if (country.getSubjectToVAT() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (country.getTitle_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("zipRequired", additionalAssertFieldName)) {
				if (country.getZipRequired() == null) {
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

	protected void assertValid(Page<Country> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Country> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Country> countries = page.getItems();

		int size = countries.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.address.dto.v1_0.Country.
						class)) {

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

	protected boolean equals(Country country1, Country country2) {
		if (country1 == country2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("a2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(country1.getA2(), country2.getA2())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("a3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(country1.getA3(), country2.getA3())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getActive(), country2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("billingAllowed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getBillingAllowed(),
						country2.getBillingAllowed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"groupFilterEnabled", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						country1.getGroupFilterEnabled(),
						country2.getGroupFilterEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(country1.getId(), country2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("idd", additionalAssertFieldName)) {
				if (!Objects.deepEquals(country1.getIdd(), country2.getIdd())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getName(), country2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("number", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getNumber(), country2.getNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("position", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getPosition(), country2.getPosition())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("regions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getRegions(), country2.getRegions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingAllowed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getShippingAllowed(),
						country2.getShippingAllowed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subjectToVAT", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getSubjectToVAT(),
						country2.getSubjectToVAT())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)country1.getTitle_i18n(),
						(Map)country2.getTitle_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("zipRequired", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						country1.getZipRequired(), country2.getZipRequired())) {

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

		if (!(_countryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_countryResource;

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
		EntityField entityField, String operator, Country country) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("a2")) {
			Object object = country.getA2();

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

		if (entityFieldName.equals("a3")) {
			Object object = country.getA3();

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

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("billingAllowed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("groupFilterEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("idd")) {
			sb.append(String.valueOf(country.getIdd()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = country.getName();

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

		if (entityFieldName.equals("number")) {
			sb.append(String.valueOf(country.getNumber()));

			return sb.toString();
		}

		if (entityFieldName.equals("position")) {
			sb.append(String.valueOf(country.getPosition()));

			return sb.toString();
		}

		if (entityFieldName.equals("regions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAllowed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subjectToVAT")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("zipRequired")) {
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

	protected Country randomCountry() throws Exception {
		return new Country() {
			{
				a2 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				a3 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				active = RandomTestUtil.randomBoolean();
				billingAllowed = RandomTestUtil.randomBoolean();
				groupFilterEnabled = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				idd = RandomTestUtil.randomInt();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				number = RandomTestUtil.randomInt();
				position = RandomTestUtil.randomDouble();
				shippingAllowed = RandomTestUtil.randomBoolean();
				subjectToVAT = RandomTestUtil.randomBoolean();
				zipRequired = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected Country randomIrrelevantCountry() throws Exception {
		Country randomIrrelevantCountry = randomCountry();

		return randomIrrelevantCountry;
	}

	protected Country randomPatchCountry() throws Exception {
		return randomCountry();
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

	protected CountryResource countryResource;
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
		LogFactoryUtil.getLog(BaseCountryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.address.resource.v1_0.CountryResource
		_countryResource;

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