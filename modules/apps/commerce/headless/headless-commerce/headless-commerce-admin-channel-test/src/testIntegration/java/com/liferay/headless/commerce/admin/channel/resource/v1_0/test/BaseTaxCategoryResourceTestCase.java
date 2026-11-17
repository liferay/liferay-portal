/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.permission.Permission;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.TaxCategoryResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.TaxCategorySerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseTaxCategoryResourceTestCase {

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

		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_taxCategoryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		taxCategoryResource = TaxCategoryResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		permissionsTaxCategoryResource = TaxCategoryResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
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

		TaxCategory taxCategory1 = randomTaxCategory();

		String json = objectMapper.writeValueAsString(taxCategory1);

		TaxCategory taxCategory2 = TaxCategorySerDes.toDTO(json);

		Assert.assertTrue(equals(taxCategory1, taxCategory2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		TaxCategory taxCategory = randomTaxCategory();

		String json1 = objectMapper.writeValueAsString(taxCategory);
		String json2 = TaxCategorySerDes.toJSON(taxCategory);

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

		TaxCategory taxCategory = randomTaxCategory();

		taxCategory.setExternalReferenceCode(regex);

		String json = TaxCategorySerDes.toJSON(taxCategory);

		Assert.assertFalse(json.contains(regex));

		taxCategory = TaxCategorySerDes.toDTO(json);

		Assert.assertEquals(regex, taxCategory.getExternalReferenceCode());
	}

	@Test
	public void testGetTaxCategoriesPage() throws Exception {
		Page<TaxCategory> page = taxCategoryResource.getTaxCategoriesPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		TaxCategory taxCategory1 = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory());

		TaxCategory taxCategory2 = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory());

		page = taxCategoryResource.getTaxCategoriesPage(
			null, null, Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(taxCategory1, (List<TaxCategory>)page.getItems());
		assertContains(taxCategory2, (List<TaxCategory>)page.getItems());
		assertValid(page, testGetTaxCategoriesPage_getExpectedActions());

		for (TaxCategory taxCategory : page.getItems()) {
			Assert.assertNull(taxCategory.getPermissions());
		}

		page = permissionsTaxCategoryResource.getTaxCategoriesPage(
			null, null, Pagination.of(1, 10), null);

		for (TaxCategory taxCategory : page.getItems()) {
			Assert.assertNotNull(taxCategory.getPermissions());
		}
	}

	protected Map<String, Map<String, String>>
			testGetTaxCategoriesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetTaxCategoriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		TaxCategory taxCategory1 = randomTaxCategory();

		taxCategory1 = testGetTaxCategoriesPage_addTaxCategory(taxCategory1);

		for (EntityField entityField : entityFields) {
			Page<TaxCategory> page = taxCategoryResource.getTaxCategoriesPage(
				null, getFilterString(entityField, "between", taxCategory1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxCategory1),
				(List<TaxCategory>)page.getItems());
		}
	}

	@Test
	public void testGetTaxCategoriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetTaxCategoriesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetTaxCategoriesPageWithFilterStringContains()
		throws Exception {

		testGetTaxCategoriesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaxCategoriesPageWithFilterStringEquals()
		throws Exception {

		testGetTaxCategoriesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaxCategoriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetTaxCategoriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetTaxCategoriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		TaxCategory taxCategory1 = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxCategory taxCategory2 = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory());

		for (EntityField entityField : entityFields) {
			Page<TaxCategory> page = taxCategoryResource.getTaxCategoriesPage(
				null, getFilterString(entityField, operator, taxCategory1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxCategory1),
				(List<TaxCategory>)page.getItems());
		}
	}

	@Test
	public void testGetTaxCategoriesPageWithPagination() throws Exception {
		Page<TaxCategory> taxCategoriesPage =
			taxCategoryResource.getTaxCategoriesPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			taxCategoriesPage.getTotalCount());

		TaxCategory taxCategory1 = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory());

		TaxCategory taxCategory2 = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory());

		TaxCategory taxCategory3 = testGetTaxCategoriesPage_addTaxCategory(
			randomTaxCategory());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TaxCategory> page1 = taxCategoryResource.getTaxCategoriesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(taxCategory1, (List<TaxCategory>)page1.getItems());

			Page<TaxCategory> page2 = taxCategoryResource.getTaxCategoriesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(taxCategory2, (List<TaxCategory>)page2.getItems());

			Page<TaxCategory> page3 = taxCategoryResource.getTaxCategoriesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(taxCategory3, (List<TaxCategory>)page3.getItems());
		}
		else {
			Page<TaxCategory> page1 = taxCategoryResource.getTaxCategoriesPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<TaxCategory> taxCategories1 =
				(List<TaxCategory>)page1.getItems();

			Assert.assertEquals(
				taxCategories1.toString(), totalCount + 2,
				taxCategories1.size());

			Page<TaxCategory> page2 = taxCategoryResource.getTaxCategoriesPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TaxCategory> taxCategories2 =
				(List<TaxCategory>)page2.getItems();

			Assert.assertEquals(
				taxCategories2.toString(), 1, taxCategories2.size());

			Page<TaxCategory> page3 = taxCategoryResource.getTaxCategoriesPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(taxCategory1, (List<TaxCategory>)page3.getItems());
			assertContains(taxCategory2, (List<TaxCategory>)page3.getItems());
			assertContains(taxCategory3, (List<TaxCategory>)page3.getItems());
		}
	}

	@Test
	public void testGetTaxCategoriesPageWithSortDateTime() throws Exception {
		testGetTaxCategoriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, taxCategory1, taxCategory2) -> {
				BeanTestUtil.setProperty(
					taxCategory1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetTaxCategoriesPageWithSortDouble() throws Exception {
		testGetTaxCategoriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, taxCategory1, taxCategory2) -> {
				BeanTestUtil.setProperty(
					taxCategory1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					taxCategory2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetTaxCategoriesPageWithSortInteger() throws Exception {
		testGetTaxCategoriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taxCategory1, taxCategory2) -> {
				BeanTestUtil.setProperty(
					taxCategory1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					taxCategory2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetTaxCategoriesPageWithSortString() throws Exception {
		testGetTaxCategoriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, taxCategory1, taxCategory2) -> {
				Class<?> clazz = taxCategory1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						taxCategory1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						taxCategory2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						taxCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						taxCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						taxCategory1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						taxCategory2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetTaxCategoriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, TaxCategory, TaxCategory, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		TaxCategory taxCategory1 = randomTaxCategory();
		TaxCategory taxCategory2 = randomTaxCategory();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, taxCategory1, taxCategory2);
		}

		taxCategory1 = testGetTaxCategoriesPage_addTaxCategory(taxCategory1);

		taxCategory2 = testGetTaxCategoriesPage_addTaxCategory(taxCategory2);

		Page<TaxCategory> page = taxCategoryResource.getTaxCategoriesPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<TaxCategory> ascPage =
				taxCategoryResource.getTaxCategoriesPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(taxCategory1, (List<TaxCategory>)ascPage.getItems());
			assertContains(taxCategory2, (List<TaxCategory>)ascPage.getItems());

			Page<TaxCategory> descPage =
				taxCategoryResource.getTaxCategoriesPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				taxCategory2, (List<TaxCategory>)descPage.getItems());
			assertContains(
				taxCategory1, (List<TaxCategory>)descPage.getItems());
		}
	}

	protected TaxCategory testGetTaxCategoriesPage_addTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTaxCategoriesPage() throws Exception {
		GraphQLField graphQLField =
			testGraphQLGetTaxCategoriesPageTaxCategory_getGraphQLField();

		// No namespace

		JSONObject taxCategoriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/taxCategories");

		long totalCount = taxCategoriesJSONObject.getLong("totalCount");

		TaxCategory taxCategory1 = testGraphQLTaxCategory_addTaxCategory(
			randomTaxCategory());

		TaxCategory taxCategory2 = testGraphQLTaxCategory_addTaxCategory(
			randomTaxCategory());

		taxCategoriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/taxCategories");

		Assert.assertEquals(
			totalCount + 2, taxCategoriesJSONObject.getLong("totalCount"));

		assertContains(
			taxCategory1,
			Arrays.asList(
				TaxCategorySerDes.toDTOs(
					taxCategoriesJSONObject.getString("items"))));
		assertContains(
			taxCategory2,
			Arrays.asList(
				TaxCategorySerDes.toDTOs(
					taxCategoriesJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		taxCategoriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminChannel_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminChannel_v1_0",
			"JSONObject/taxCategories");

		Assert.assertEquals(
			totalCount + 2, taxCategoriesJSONObject.getLong("totalCount"));

		assertContains(
			taxCategory1,
			Arrays.asList(
				TaxCategorySerDes.toDTOs(
					taxCategoriesJSONObject.getString("items"))));
		assertContains(
			taxCategory2,
			Arrays.asList(
				TaxCategorySerDes.toDTOs(
					taxCategoriesJSONObject.getString("items"))));
	}

	protected GraphQLField
			testGraphQLGetTaxCategoriesPageTaxCategory_getGraphQLField()
		throws Exception {

		return new GraphQLField(
			"taxCategories",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));
	}

	@Test
	public void testGetTaxCategory() throws Exception {
		TaxCategory postTaxCategory = testGetTaxCategory_addTaxCategory();

		TaxCategory getTaxCategory = taxCategoryResource.getTaxCategory(
			postTaxCategory.getId());

		assertEquals(postTaxCategory, getTaxCategory);
		assertValid(getTaxCategory);

		Assert.assertNull(getTaxCategory.getPermissions());

		getTaxCategory = permissionsTaxCategoryResource.getTaxCategory(
			postTaxCategory.getId());

		Assert.assertNotNull(getTaxCategory.getPermissions());
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		TaxCategory postTaxCategory = testGetTaxCategory_addTaxCategory();

		TaxCategory getTaxCategory = taxCategoryResource.getTaxCategory(
			postTaxCategory.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.TaxCategory"
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

		Object item = vulcanCRUDItemDelegate.getItem(postTaxCategory.getId());

		assertEquals(getTaxCategory, TaxCategorySerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:",
						String.valueOf(PortalUtil.getPortalServerPort(false)),
						"/o/v1.0/", RandomTestUtil.randomString(), "/",
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
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false), "/o/",
						applicationPath, resourcePath));
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
				return URI.create(
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false), "/o/",
						applicationPath));
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

	protected TaxCategory testGetTaxCategory_addTaxCategory() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTaxCategory() throws Exception {
		TaxCategory taxCategory = testGraphQLGetTaxCategory_addTaxCategory();

		// No namespace

		Assert.assertTrue(
			equals(
				taxCategory,
				TaxCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"taxCategory",
								new HashMap<String, Object>() {
									{
										put("id", taxCategory.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/taxCategory"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertTrue(
			equals(
				taxCategory,
				TaxCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminChannel_v1_0",
								new GraphQLField(
									"taxCategory",
									new HashMap<String, Object>() {
										{
											put("id", taxCategory.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminChannel_v1_0",
						"Object/taxCategory"))));
	}

	@Test
	public void testGraphQLGetTaxCategoryNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"taxCategory",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"taxCategory",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected TaxCategory testGraphQLGetTaxCategory_addTaxCategory()
		throws Exception {

		return testGraphQLTaxCategory_addTaxCategory();
	}

	@Test
	public void testGetTaxCategoryByExternalReferenceCode() throws Exception {
		TaxCategory postTaxCategory =
			testGetTaxCategoryByExternalReferenceCode_addTaxCategory();

		TaxCategory getTaxCategory =
			taxCategoryResource.getTaxCategoryByExternalReferenceCode(
				postTaxCategory.getExternalReferenceCode());

		assertEquals(postTaxCategory, getTaxCategory);
		assertValid(getTaxCategory);

		Assert.assertNull(getTaxCategory.getPermissions());

		getTaxCategory =
			permissionsTaxCategoryResource.
				getTaxCategoryByExternalReferenceCode(
					postTaxCategory.getExternalReferenceCode());

		Assert.assertNotNull(getTaxCategory.getPermissions());
	}

	protected TaxCategory
			testGetTaxCategoryByExternalReferenceCode_addTaxCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTaxCategoryByExternalReferenceCode()
		throws Exception {

		TaxCategory taxCategory =
			testGraphQLGetTaxCategoryByExternalReferenceCode_addTaxCategory();

		// No namespace

		Assert.assertTrue(
			equals(
				taxCategory,
				TaxCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"taxCategoryByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												taxCategory.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/taxCategoryByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertTrue(
			equals(
				taxCategory,
				TaxCategorySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminChannel_v1_0",
								new GraphQLField(
									"taxCategoryByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													taxCategory.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminChannel_v1_0",
						"Object/taxCategoryByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetTaxCategoryByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"taxCategoryByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"taxCategoryByExternalReferenceCode",
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

	protected TaxCategory
			testGraphQLGetTaxCategoryByExternalReferenceCode_addTaxCategory()
		throws Exception {

		return testGraphQLTaxCategory_addTaxCategory();
	}

	@Test
	public void testGetTaxCategoryPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxCategory postTaxCategory =
			testGetTaxCategoryPermissionsPage_addTaxCategory();

		Page<Permission> page =
			taxCategoryResource.getTaxCategoryPermissionsPage(
				postTaxCategory.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected TaxCategory testGetTaxCategoryPermissionsPage_addTaxCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTaxCategoryPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxCategory postTaxCategory =
			testGraphQLGetTaxCategoryPermissionsPage_addTaxCategory();

		GraphQLField graphQLField = new GraphQLField(
			"taxCategoryPermissions",
			new HashMap<String, Object>() {
				{
					put("taxCategoryId", postTaxCategory.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject taxCategoryPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/taxCategoryPermissions");

		Assert.assertNotNull(taxCategoryPermissionsJSONObject);
	}

	protected TaxCategory
			testGraphQLGetTaxCategoryPermissionsPage_addTaxCategory()
		throws Exception {

		return testGraphQLTaxCategory_addTaxCategory();
	}

	@Test
	public void testPatchTaxCategory() throws Exception {
		TaxCategory postTaxCategory = testPatchTaxCategory_addTaxCategory();

		TaxCategory randomPatchTaxCategory = randomPatchTaxCategory();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxCategory patchTaxCategory = taxCategoryResource.patchTaxCategory(
			postTaxCategory.getId(), randomPatchTaxCategory);

		TaxCategory expectedPatchTaxCategory = postTaxCategory.clone();

		BeanTestUtil.copyProperties(
			randomPatchTaxCategory, expectedPatchTaxCategory);

		TaxCategory getTaxCategory = taxCategoryResource.getTaxCategory(
			patchTaxCategory.getId());

		assertEquals(expectedPatchTaxCategory, getTaxCategory);
		assertValid(getTaxCategory);
	}

	protected TaxCategory testPatchTaxCategory_addTaxCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchTaxCategoryByExternalReferenceCode() throws Exception {
		TaxCategory postTaxCategory =
			testPatchTaxCategoryByExternalReferenceCode_addTaxCategory();

		TaxCategory randomPatchTaxCategory = randomPatchTaxCategory();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxCategory patchTaxCategory =
			taxCategoryResource.patchTaxCategoryByExternalReferenceCode(
				postTaxCategory.getExternalReferenceCode(),
				randomPatchTaxCategory);

		TaxCategory expectedPatchTaxCategory = postTaxCategory.clone();

		BeanTestUtil.copyProperties(
			randomPatchTaxCategory, expectedPatchTaxCategory);

		TaxCategory getTaxCategory =
			taxCategoryResource.getTaxCategoryByExternalReferenceCode(
				patchTaxCategory.getExternalReferenceCode());

		assertEquals(expectedPatchTaxCategory, getTaxCategory);
		assertValid(getTaxCategory);
	}

	protected TaxCategory
			testPatchTaxCategoryByExternalReferenceCode_addTaxCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostTaxCategory() throws Exception {
		TaxCategory randomTaxCategory = randomTaxCategory();

		TaxCategory postTaxCategory = testPostTaxCategory_addTaxCategory(
			randomTaxCategory);

		assertEquals(randomTaxCategory, postTaxCategory);
		assertValid(postTaxCategory);

		TaxCategory randomPermissionsTaxCategory1 =
			randomPermissionsTaxCategory();

		TaxCategory postPermissionsTaxCategory1 =
			testPostTaxCategory_addTaxCategory(randomPermissionsTaxCategory1);

		Assert.assertNull(postPermissionsTaxCategory1.getPermissions());

		TaxCategory randomPermissionsTaxCategory2 =
			randomPermissionsTaxCategory();

		TaxCategory postPermissionsTaxCategory2 =
			testPostTaxCategory_addPermissionsTaxCategory(
				randomPermissionsTaxCategory2);

		Assert.assertNotNull(postPermissionsTaxCategory2.getPermissions());
	}

	protected TaxCategory testPostTaxCategory_addTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected TaxCategory testPostTaxCategory_addPermissionsTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostTaxCategory() throws Exception {
		TaxCategory randomTaxCategory = randomTaxCategory();

		TaxCategory taxCategory = testGraphQLTaxCategory_addTaxCategory(
			randomTaxCategory);

		Assert.assertTrue(equals(randomTaxCategory, taxCategory));
	}

	@Test
	public void testPutTaxCategoryPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxCategory taxCategory =
			testPutTaxCategoryPermissionsPage_addTaxCategory();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			taxCategoryResource.putTaxCategoryPermissionsPageHttpResponse(
				taxCategory.getId(),
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
			taxCategoryResource.putTaxCategoryPermissionsPageHttpResponse(
				0L,
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected TaxCategory testPutTaxCategoryPermissionsPage_addTaxCategory()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected TaxCategory testGraphQLTaxCategory_addTaxCategory()
		throws Exception {

		return testGraphQLTaxCategory_addTaxCategory(randomTaxCategory());
	}

	protected TaxCategory testGraphQLTaxCategory_addTaxCategory(
			TaxCategory taxCategory)
		throws Exception {

		JSONDeserializer<TaxCategory> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(TaxCategory.class)) {

			if (getGraphQLValue(field.get(taxCategory)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(taxCategory)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createTaxCategory",
						new HashMap<String, Object>() {
							{
								put("taxCategory", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createTaxCategory"),
			TaxCategory.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date) {
			Date date = (Date)value;

			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum) {
			Enum<?> enm = (Enum<?>)value;

			return enm.name();
		}
		else if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)value;

			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[]) {
			Object[] array = (Object[])value;

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
		TaxCategory taxCategory, List<TaxCategory> taxCategories) {

		boolean contains = false;

		for (TaxCategory item : taxCategories) {
			if (equals(taxCategory, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			taxCategories + " does not contain " + taxCategory, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		TaxCategory taxCategory1, TaxCategory taxCategory2) {

		Assert.assertTrue(
			taxCategory1 + " does not equal " + taxCategory2,
			equals(taxCategory1, taxCategory2));
	}

	protected void assertEquals(
		List<TaxCategory> taxCategories1, List<TaxCategory> taxCategories2) {

		Assert.assertEquals(taxCategories1.size(), taxCategories2.size());

		for (int i = 0; i < taxCategories1.size(); i++) {
			TaxCategory taxCategory1 = taxCategories1.get(i);
			TaxCategory taxCategory2 = taxCategories2.get(i);

			assertEquals(taxCategory1, taxCategory2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TaxCategory> taxCategories1, List<TaxCategory> taxCategories2) {

		Assert.assertEquals(taxCategories1.size(), taxCategories2.size());

		for (TaxCategory taxCategory1 : taxCategories1) {
			boolean contains = false;

			for (TaxCategory taxCategory2 : taxCategories2) {
				if (equals(taxCategory1, taxCategory2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				taxCategories2 + " does not contain " + taxCategory1, contains);
		}
	}

	protected void assertValid(TaxCategory taxCategory) throws Exception {
		boolean valid = true;

		if (taxCategory.getDateCreated() == null) {
			valid = false;
		}

		if (taxCategory.getDateModified() == null) {
			valid = false;
		}

		if (taxCategory.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (taxCategory.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (taxCategory.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (taxCategory.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("groupId", additionalAssertFieldName)) {
				if (taxCategory.getGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (taxCategory.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (taxCategory.getPermissions() == null) {
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

	protected void assertValid(Page<TaxCategory> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<TaxCategory> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<TaxCategory> taxCategories = page.getItems();

		int size = taxCategories.size();

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
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						TaxCategory.class)) {

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
		TaxCategory taxCategory1, TaxCategory taxCategory2) {

		if (taxCategory1 == taxCategory2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxCategory1.getCreator(), taxCategory2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxCategory1.getDateCreated(),
						taxCategory2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxCategory1.getDateModified(),
						taxCategory2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxCategory1.getDescription(),
						(Map)taxCategory2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxCategory1.getExternalReferenceCode(),
						taxCategory2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("groupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxCategory1.getGroupId(), taxCategory2.getGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxCategory1.getId(), taxCategory2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxCategory1.getName(),
						(Map)taxCategory2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxCategory1.getPermissions(),
						taxCategory2.getPermissions())) {

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

		if (!(_taxCategoryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taxCategoryResource;

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
		EntityField entityField, String operator, TaxCategory taxCategory) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = taxCategory.getDateCreated();

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

				sb.append(_format.format(taxCategory.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = taxCategory.getDateModified();

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

				sb.append(_format.format(taxCategory.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = taxCategory.getExternalReferenceCode();

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

		if (entityFieldName.equals("groupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("permissions")) {
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
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
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

	protected TaxCategory randomTaxCategory() throws Exception {
		return new TaxCategory() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				groupId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
			}
		};
	}

	protected TaxCategory randomIrrelevantTaxCategory() throws Exception {
		TaxCategory randomIrrelevantTaxCategory = randomTaxCategory();

		return randomIrrelevantTaxCategory;
	}

	protected TaxCategory randomPatchTaxCategory() throws Exception {
		return randomTaxCategory();
	}

	protected TaxCategory randomPermissionsTaxCategory() throws Exception {
		TaxCategory taxCategory = randomTaxCategory();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		taxCategory.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return taxCategory;
	}

	protected TaxCategoryResource taxCategoryResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected TaxCategoryResource permissionsTaxCategoryResource;
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
		LogFactoryUtil.getLog(BaseTaxCategoryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		TaxCategoryResource _taxCategoryResource;

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
// LIFERAY-REST-BUILDER-HASH:1473440073