/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.CategoryDisplayPage;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.CategoryDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.CategoryDisplayPageSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
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
public abstract class BaseCategoryDisplayPageResourceTestCase {

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

		_categoryDisplayPageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		categoryDisplayPageResource = CategoryDisplayPageResource.builder(
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

		CategoryDisplayPage categoryDisplayPage1 = randomCategoryDisplayPage();

		String json = objectMapper.writeValueAsString(categoryDisplayPage1);

		CategoryDisplayPage categoryDisplayPage2 =
			CategoryDisplayPageSerDes.toDTO(json);

		Assert.assertTrue(equals(categoryDisplayPage1, categoryDisplayPage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		CategoryDisplayPage categoryDisplayPage = randomCategoryDisplayPage();

		String json1 = objectMapper.writeValueAsString(categoryDisplayPage);
		String json2 = CategoryDisplayPageSerDes.toJSON(categoryDisplayPage);

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

		CategoryDisplayPage categoryDisplayPage = randomCategoryDisplayPage();

		categoryDisplayPage.setCategoryExternalReferenceCode(regex);
		categoryDisplayPage.setGroupExternalReferenceCode(regex);
		categoryDisplayPage.setPageUuid(regex);

		String json = CategoryDisplayPageSerDes.toJSON(categoryDisplayPage);

		Assert.assertFalse(json.contains(regex));

		categoryDisplayPage = CategoryDisplayPageSerDes.toDTO(json);

		Assert.assertEquals(
			regex, categoryDisplayPage.getCategoryExternalReferenceCode());
		Assert.assertEquals(
			regex, categoryDisplayPage.getGroupExternalReferenceCode());
		Assert.assertEquals(regex, categoryDisplayPage.getPageUuid());
	}

	@Test
	public void testDeleteCategoryDisplayPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CategoryDisplayPage categoryDisplayPage =
			testDeleteCategoryDisplayPage_addCategoryDisplayPage();

		assertHttpResponseStatusCode(
			204,
			categoryDisplayPageResource.deleteCategoryDisplayPageHttpResponse(
				categoryDisplayPage.getId()));

		assertHttpResponseStatusCode(
			404,
			categoryDisplayPageResource.getCategoryDisplayPageHttpResponse(
				categoryDisplayPage.getId()));
		assertHttpResponseStatusCode(
			404,
			categoryDisplayPageResource.getCategoryDisplayPageHttpResponse(0L));
	}

	protected CategoryDisplayPage
			testDeleteCategoryDisplayPage_addCategoryDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCategoryDisplayPage() throws Exception {

		// No namespace

		CategoryDisplayPage categoryDisplayPage1 =
			testGraphQLDeleteCategoryDisplayPage_addCategoryDisplayPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCategoryDisplayPage",
						new HashMap<String, Object>() {
							{
								put("id", categoryDisplayPage1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteCategoryDisplayPage"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"categoryDisplayPage",
					new HashMap<String, Object>() {
						{
							put("id", categoryDisplayPage1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminChannel_v1_0

		CategoryDisplayPage categoryDisplayPage2 =
			testGraphQLDeleteCategoryDisplayPage_addCategoryDisplayPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteCategoryDisplayPage",
							new HashMap<String, Object>() {
								{
									put("id", categoryDisplayPage2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteCategoryDisplayPage"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminChannel_v1_0",
					new GraphQLField(
						"categoryDisplayPage",
						new HashMap<String, Object>() {
							{
								put("id", categoryDisplayPage2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected CategoryDisplayPage
			testGraphQLDeleteCategoryDisplayPage_addCategoryDisplayPage()
		throws Exception {

		return testGraphQLCategoryDisplayPage_addCategoryDisplayPage();
	}

	@Test
	public void testDeleteCategoryDisplayPageBatch() throws Exception {
		CategoryDisplayPage categoryDisplayPage1 =
			testDeleteCategoryDisplayPageBatch_addCategoryDisplayPage();

		testDeleteCategoryDisplayPageBatch_deleteCategoryDisplayPage(
			202, null, categoryDisplayPage1.getId());

		assertHttpResponseStatusCode(
			404,
			categoryDisplayPageResource.getCategoryDisplayPageHttpResponse(
				categoryDisplayPage1.getId()));
	}

	protected CategoryDisplayPage
			testDeleteCategoryDisplayPageBatch_addCategoryDisplayPage()
		throws Exception {

		return testDeleteCategoryDisplayPage_addCategoryDisplayPage();
	}

	protected void testDeleteCategoryDisplayPageBatch_deleteCategoryDisplayPage(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			categoryDisplayPageResource.
				deleteCategoryDisplayPageBatchHttpResponse(
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
	public void testGetCategoryDisplayPage() throws Exception {
		CategoryDisplayPage postCategoryDisplayPage =
			testGetCategoryDisplayPage_addCategoryDisplayPage();

		CategoryDisplayPage getCategoryDisplayPage =
			categoryDisplayPageResource.getCategoryDisplayPage(
				postCategoryDisplayPage.getId());

		assertEquals(postCategoryDisplayPage, getCategoryDisplayPage);
		assertValid(getCategoryDisplayPage);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		CategoryDisplayPage postCategoryDisplayPage =
			testGetCategoryDisplayPage_addCategoryDisplayPage();

		CategoryDisplayPage getCategoryDisplayPage =
			categoryDisplayPageResource.getCategoryDisplayPage(
				postCategoryDisplayPage.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.CategoryDisplayPage"
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
			postCategoryDisplayPage.getId());

		assertEquals(
			getCategoryDisplayPage,
			CategoryDisplayPageSerDes.toDTO(item.toString()));
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

	protected CategoryDisplayPage
			testGetCategoryDisplayPage_addCategoryDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCategoryDisplayPage() throws Exception {
		CategoryDisplayPage categoryDisplayPage =
			testGraphQLGetCategoryDisplayPage_addCategoryDisplayPage();

		// No namespace

		Assert.assertTrue(
			equals(
				categoryDisplayPage,
				CategoryDisplayPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"categoryDisplayPage",
								new HashMap<String, Object>() {
									{
										put("id", categoryDisplayPage.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/categoryDisplayPage"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertTrue(
			equals(
				categoryDisplayPage,
				CategoryDisplayPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminChannel_v1_0",
								new GraphQLField(
									"categoryDisplayPage",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												categoryDisplayPage.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminChannel_v1_0",
						"Object/categoryDisplayPage"))));
	}

	@Test
	public void testGraphQLGetCategoryDisplayPageNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"categoryDisplayPage",
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
							"categoryDisplayPage",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected CategoryDisplayPage
			testGraphQLGetCategoryDisplayPage_addCategoryDisplayPage()
		throws Exception {

		return testGraphQLCategoryDisplayPage_addCategoryDisplayPage();
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getIrrelevantExternalReferenceCode();

		Page<CategoryDisplayPage> page =
			categoryDisplayPageResource.
				getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			CategoryDisplayPage irrelevantCategoryDisplayPage =
				testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
					irrelevantExternalReferenceCode,
					randomIrrelevantCategoryDisplayPage());

			page =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantCategoryDisplayPage,
				(List<CategoryDisplayPage>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		CategoryDisplayPage categoryDisplayPage1 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, randomCategoryDisplayPage());

		CategoryDisplayPage categoryDisplayPage2 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, randomCategoryDisplayPage());

		page =
			categoryDisplayPageResource.
				getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			categoryDisplayPage1, (List<CategoryDisplayPage>)page.getItems());
		assertContains(
			categoryDisplayPage2, (List<CategoryDisplayPage>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExpectedActions(
				externalReferenceCode));

		categoryDisplayPageResource.deleteCategoryDisplayPage(
			categoryDisplayPage1.getId());

		categoryDisplayPageResource.deleteCategoryDisplayPage(
			categoryDisplayPage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExternalReferenceCode();

		CategoryDisplayPage categoryDisplayPage1 = randomCategoryDisplayPage();

		categoryDisplayPage1 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, categoryDisplayPage1);

		for (EntityField entityField : entityFields) {
			Page<CategoryDisplayPage> page =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, "between", categoryDisplayPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(categoryDisplayPage1),
				(List<CategoryDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilterStringContains()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilterStringEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExternalReferenceCode();

		CategoryDisplayPage categoryDisplayPage1 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, randomCategoryDisplayPage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CategoryDisplayPage categoryDisplayPage2 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, randomCategoryDisplayPage());

		for (EntityField entityField : entityFields) {
			Page<CategoryDisplayPage> page =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, operator, categoryDisplayPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(categoryDisplayPage1),
				(List<CategoryDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExternalReferenceCode();

		Page<CategoryDisplayPage> categoryDisplayPagesPage =
			categoryDisplayPageResource.
				getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			categoryDisplayPagesPage.getTotalCount());

		CategoryDisplayPage categoryDisplayPage1 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, randomCategoryDisplayPage());

		CategoryDisplayPage categoryDisplayPage2 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, randomCategoryDisplayPage());

		CategoryDisplayPage categoryDisplayPage3 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, randomCategoryDisplayPage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<CategoryDisplayPage> page1 =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)page1.getItems());

			Page<CategoryDisplayPage> page2 =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)page2.getItems());

			Page<CategoryDisplayPage> page3 =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				categoryDisplayPage3,
				(List<CategoryDisplayPage>)page3.getItems());
		}
		else {
			Page<CategoryDisplayPage> page1 =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<CategoryDisplayPage> categoryDisplayPages1 =
				(List<CategoryDisplayPage>)page1.getItems();

			Assert.assertEquals(
				categoryDisplayPages1.toString(), totalCount + 2,
				categoryDisplayPages1.size());

			Page<CategoryDisplayPage> page2 =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<CategoryDisplayPage> categoryDisplayPages2 =
				(List<CategoryDisplayPage>)page2.getItems();

			Assert.assertEquals(
				categoryDisplayPages2.toString(), 1,
				categoryDisplayPages2.size());

			Page<CategoryDisplayPage> page3 =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)page3.getItems());
			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)page3.getItems());
			assertContains(
				categoryDisplayPage3,
				(List<CategoryDisplayPage>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSortDateTime()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				BeanTestUtil.setProperty(
					categoryDisplayPage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSortDouble()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				BeanTestUtil.setProperty(
					categoryDisplayPage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					categoryDisplayPage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSortInteger()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				BeanTestUtil.setProperty(
					categoryDisplayPage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					categoryDisplayPage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSortString()
		throws Exception {

		testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				Class<?> clazz = categoryDisplayPage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						categoryDisplayPage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						categoryDisplayPage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						categoryDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						categoryDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						categoryDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						categoryDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, CategoryDisplayPage, CategoryDisplayPage,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExternalReferenceCode();

		CategoryDisplayPage categoryDisplayPage1 = randomCategoryDisplayPage();
		CategoryDisplayPage categoryDisplayPage2 = randomCategoryDisplayPage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, categoryDisplayPage1, categoryDisplayPage2);
		}

		categoryDisplayPage1 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, categoryDisplayPage1);

		categoryDisplayPage2 =
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				externalReferenceCode, categoryDisplayPage2);

		Page<CategoryDisplayPage> page =
			categoryDisplayPageResource.
				getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<CategoryDisplayPage> ascPage =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)ascPage.getItems());
			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)ascPage.getItems());

			Page<CategoryDisplayPage> descPage =
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)descPage.getItems());
			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)descPage.getItems());
		}
	}

	protected CategoryDisplayPage
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_addCategoryDisplayPage(
				String externalReferenceCode,
				CategoryDisplayPage categoryDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeCategoryDisplayPagesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPage() throws Exception {
		Long id = testGetChannelIdCategoryDisplayPagesPage_getId();
		Long irrelevantId =
			testGetChannelIdCategoryDisplayPagesPage_getIrrelevantId();

		Page<CategoryDisplayPage> page =
			categoryDisplayPageResource.getChannelIdCategoryDisplayPagesPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			CategoryDisplayPage irrelevantCategoryDisplayPage =
				testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
					irrelevantId, randomIrrelevantCategoryDisplayPage());

			page =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantCategoryDisplayPage,
				(List<CategoryDisplayPage>)page.getItems());
			assertValid(
				page,
				testGetChannelIdCategoryDisplayPagesPage_getExpectedActions(
					irrelevantId));
		}

		CategoryDisplayPage categoryDisplayPage1 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, randomCategoryDisplayPage());

		CategoryDisplayPage categoryDisplayPage2 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, randomCategoryDisplayPage());

		page = categoryDisplayPageResource.getChannelIdCategoryDisplayPagesPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			categoryDisplayPage1, (List<CategoryDisplayPage>)page.getItems());
		assertContains(
			categoryDisplayPage2, (List<CategoryDisplayPage>)page.getItems());
		assertValid(
			page,
			testGetChannelIdCategoryDisplayPagesPage_getExpectedActions(id));

		categoryDisplayPageResource.deleteCategoryDisplayPage(
			categoryDisplayPage1.getId());

		categoryDisplayPageResource.deleteCategoryDisplayPage(
			categoryDisplayPage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelIdCategoryDisplayPagesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdCategoryDisplayPagesPage_getId();

		CategoryDisplayPage categoryDisplayPage1 = randomCategoryDisplayPage();

		categoryDisplayPage1 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, categoryDisplayPage1);

		for (EntityField entityField : entityFields) {
			Page<CategoryDisplayPage> page =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null,
						getFilterString(
							entityField, "between", categoryDisplayPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(categoryDisplayPage1),
				(List<CategoryDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithFilterStringContains()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithFilterStringEquals()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelIdCategoryDisplayPagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdCategoryDisplayPagesPage_getId();

		CategoryDisplayPage categoryDisplayPage1 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, randomCategoryDisplayPage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CategoryDisplayPage categoryDisplayPage2 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, randomCategoryDisplayPage());

		for (EntityField entityField : entityFields) {
			Page<CategoryDisplayPage> page =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null,
						getFilterString(
							entityField, operator, categoryDisplayPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(categoryDisplayPage1),
				(List<CategoryDisplayPage>)page.getItems());
		}
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithPagination()
		throws Exception {

		Long id = testGetChannelIdCategoryDisplayPagesPage_getId();

		Page<CategoryDisplayPage> categoryDisplayPagesPage =
			categoryDisplayPageResource.getChannelIdCategoryDisplayPagesPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			categoryDisplayPagesPage.getTotalCount());

		CategoryDisplayPage categoryDisplayPage1 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, randomCategoryDisplayPage());

		CategoryDisplayPage categoryDisplayPage2 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, randomCategoryDisplayPage());

		CategoryDisplayPage categoryDisplayPage3 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, randomCategoryDisplayPage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<CategoryDisplayPage> page1 =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)page1.getItems());

			Page<CategoryDisplayPage> page2 =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)page2.getItems());

			Page<CategoryDisplayPage> page3 =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				categoryDisplayPage3,
				(List<CategoryDisplayPage>)page3.getItems());
		}
		else {
			Page<CategoryDisplayPage> page1 =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<CategoryDisplayPage> categoryDisplayPages1 =
				(List<CategoryDisplayPage>)page1.getItems();

			Assert.assertEquals(
				categoryDisplayPages1.toString(), totalCount + 2,
				categoryDisplayPages1.size());

			Page<CategoryDisplayPage> page2 =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<CategoryDisplayPage> categoryDisplayPages2 =
				(List<CategoryDisplayPage>)page2.getItems();

			Assert.assertEquals(
				categoryDisplayPages2.toString(), 1,
				categoryDisplayPages2.size());

			Page<CategoryDisplayPage> page3 =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)page3.getItems());
			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)page3.getItems());
			assertContains(
				categoryDisplayPage3,
				(List<CategoryDisplayPage>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithSortDateTime()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				BeanTestUtil.setProperty(
					categoryDisplayPage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithSortDouble()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				BeanTestUtil.setProperty(
					categoryDisplayPage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					categoryDisplayPage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithSortInteger()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				BeanTestUtil.setProperty(
					categoryDisplayPage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					categoryDisplayPage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelIdCategoryDisplayPagesPageWithSortString()
		throws Exception {

		testGetChannelIdCategoryDisplayPagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, categoryDisplayPage1, categoryDisplayPage2) -> {
				Class<?> clazz = categoryDisplayPage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						categoryDisplayPage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						categoryDisplayPage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						categoryDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						categoryDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						categoryDisplayPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						categoryDisplayPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelIdCategoryDisplayPagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, CategoryDisplayPage, CategoryDisplayPage,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdCategoryDisplayPagesPage_getId();

		CategoryDisplayPage categoryDisplayPage1 = randomCategoryDisplayPage();
		CategoryDisplayPage categoryDisplayPage2 = randomCategoryDisplayPage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, categoryDisplayPage1, categoryDisplayPage2);
		}

		categoryDisplayPage1 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, categoryDisplayPage1);

		categoryDisplayPage2 =
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				id, categoryDisplayPage2);

		Page<CategoryDisplayPage> page =
			categoryDisplayPageResource.getChannelIdCategoryDisplayPagesPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<CategoryDisplayPage> ascPage =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)ascPage.getItems());
			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)ascPage.getItems());

			Page<CategoryDisplayPage> descPage =
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				categoryDisplayPage2,
				(List<CategoryDisplayPage>)descPage.getItems());
			assertContains(
				categoryDisplayPage1,
				(List<CategoryDisplayPage>)descPage.getItems());
		}
	}

	protected CategoryDisplayPage
			testGetChannelIdCategoryDisplayPagesPage_addCategoryDisplayPage(
				Long id, CategoryDisplayPage categoryDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelIdCategoryDisplayPagesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelIdCategoryDisplayPagesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchCategoryDisplayPage() throws Exception {
		CategoryDisplayPage postCategoryDisplayPage =
			testPatchCategoryDisplayPage_addCategoryDisplayPage();

		CategoryDisplayPage randomPatchCategoryDisplayPage =
			randomPatchCategoryDisplayPage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CategoryDisplayPage patchCategoryDisplayPage =
			categoryDisplayPageResource.patchCategoryDisplayPage(
				postCategoryDisplayPage.getId(),
				randomPatchCategoryDisplayPage);

		CategoryDisplayPage expectedPatchCategoryDisplayPage =
			postCategoryDisplayPage.clone();

		BeanTestUtil.copyProperties(
			randomPatchCategoryDisplayPage, expectedPatchCategoryDisplayPage);

		CategoryDisplayPage getCategoryDisplayPage =
			categoryDisplayPageResource.getCategoryDisplayPage(
				patchCategoryDisplayPage.getId());

		assertEquals(expectedPatchCategoryDisplayPage, getCategoryDisplayPage);
		assertValid(getCategoryDisplayPage);
	}

	protected CategoryDisplayPage
			testPatchCategoryDisplayPage_addCategoryDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelByExternalReferenceCodeCategoryDisplayPage()
		throws Exception {

		CategoryDisplayPage randomCategoryDisplayPage =
			randomCategoryDisplayPage();

		CategoryDisplayPage postCategoryDisplayPage =
			testPostChannelByExternalReferenceCodeCategoryDisplayPage_addCategoryDisplayPage(
				randomCategoryDisplayPage);

		assertEquals(randomCategoryDisplayPage, postCategoryDisplayPage);
		assertValid(postCategoryDisplayPage);
	}

	protected CategoryDisplayPage
			testPostChannelByExternalReferenceCodeCategoryDisplayPage_addCategoryDisplayPage(
				CategoryDisplayPage categoryDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelIdCategoryDisplayPage() throws Exception {
		CategoryDisplayPage randomCategoryDisplayPage =
			randomCategoryDisplayPage();

		CategoryDisplayPage postCategoryDisplayPage =
			testPostChannelIdCategoryDisplayPage_addCategoryDisplayPage(
				randomCategoryDisplayPage);

		assertEquals(randomCategoryDisplayPage, postCategoryDisplayPage);
		assertValid(postCategoryDisplayPage);
	}

	protected CategoryDisplayPage
			testPostChannelIdCategoryDisplayPage_addCategoryDisplayPage(
				CategoryDisplayPage categoryDisplayPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		CategoryDisplayPage categoryDisplayPage1 =
			testBatchEngineDeleteImportTask_addCategoryDisplayPage();

		testBatchEngineDeleteImportTask_deleteCategoryDisplayPage(
			200, null, categoryDisplayPage1.getId());

		assertHttpResponseStatusCode(
			404,
			categoryDisplayPageResource.getCategoryDisplayPageHttpResponse(
				categoryDisplayPage1.getId()));
	}

	protected CategoryDisplayPage
			testBatchEngineDeleteImportTask_addCategoryDisplayPage()
		throws Exception {

		return testDeleteCategoryDisplayPage_addCategoryDisplayPage();
	}

	protected void testBatchEngineDeleteImportTask_deleteCategoryDisplayPage(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.CategoryDisplayPage",
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

	protected CategoryDisplayPage
			testGraphQLCategoryDisplayPage_addCategoryDisplayPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		CategoryDisplayPage categoryDisplayPage,
		List<CategoryDisplayPage> categoryDisplayPages) {

		boolean contains = false;

		for (CategoryDisplayPage item : categoryDisplayPages) {
			if (equals(categoryDisplayPage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			categoryDisplayPages + " does not contain " + categoryDisplayPage,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		CategoryDisplayPage categoryDisplayPage1,
		CategoryDisplayPage categoryDisplayPage2) {

		Assert.assertTrue(
			categoryDisplayPage1 + " does not equal " + categoryDisplayPage2,
			equals(categoryDisplayPage1, categoryDisplayPage2));
	}

	protected void assertEquals(
		List<CategoryDisplayPage> categoryDisplayPages1,
		List<CategoryDisplayPage> categoryDisplayPages2) {

		Assert.assertEquals(
			categoryDisplayPages1.size(), categoryDisplayPages2.size());

		for (int i = 0; i < categoryDisplayPages1.size(); i++) {
			CategoryDisplayPage categoryDisplayPage1 =
				categoryDisplayPages1.get(i);
			CategoryDisplayPage categoryDisplayPage2 =
				categoryDisplayPages2.get(i);

			assertEquals(categoryDisplayPage1, categoryDisplayPage2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<CategoryDisplayPage> categoryDisplayPages1,
		List<CategoryDisplayPage> categoryDisplayPages2) {

		Assert.assertEquals(
			categoryDisplayPages1.size(), categoryDisplayPages2.size());

		for (CategoryDisplayPage categoryDisplayPage1 : categoryDisplayPages1) {
			boolean contains = false;

			for (CategoryDisplayPage categoryDisplayPage2 :
					categoryDisplayPages2) {

				if (equals(categoryDisplayPage1, categoryDisplayPage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				categoryDisplayPages2 + " does not contain " +
					categoryDisplayPage1,
				contains);
		}
	}

	protected void assertValid(CategoryDisplayPage categoryDisplayPage)
		throws Exception {

		boolean valid = true;

		if (categoryDisplayPage.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (categoryDisplayPage.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"categoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (categoryDisplayPage.getCategoryExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("categoryId", additionalAssertFieldName)) {
				if (categoryDisplayPage.getCategoryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"groupExternalReferenceCode", additionalAssertFieldName)) {

				if (categoryDisplayPage.getGroupExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageUuid", additionalAssertFieldName)) {
				if (categoryDisplayPage.getPageUuid() == null) {
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

	protected void assertValid(Page<CategoryDisplayPage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<CategoryDisplayPage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<CategoryDisplayPage> categoryDisplayPages =
			page.getItems();

		int size = categoryDisplayPages.size();

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
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						CategoryDisplayPage.class)) {

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
		CategoryDisplayPage categoryDisplayPage1,
		CategoryDisplayPage categoryDisplayPage2) {

		if (categoryDisplayPage1 == categoryDisplayPage2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)categoryDisplayPage1.getActions(),
						(Map)categoryDisplayPage2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"categoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						categoryDisplayPage1.getCategoryExternalReferenceCode(),
						categoryDisplayPage2.
							getCategoryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("categoryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						categoryDisplayPage1.getCategoryId(),
						categoryDisplayPage2.getCategoryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"groupExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						categoryDisplayPage1.getGroupExternalReferenceCode(),
						categoryDisplayPage2.getGroupExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						categoryDisplayPage1.getId(),
						categoryDisplayPage2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageUuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						categoryDisplayPage1.getPageUuid(),
						categoryDisplayPage2.getPageUuid())) {

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

		if (!(_categoryDisplayPageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_categoryDisplayPageResource;

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
		CategoryDisplayPage categoryDisplayPage) {

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

		if (entityFieldName.equals("categoryExternalReferenceCode")) {
			Object object =
				categoryDisplayPage.getCategoryExternalReferenceCode();

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

		if (entityFieldName.equals("groupExternalReferenceCode")) {
			Object object = categoryDisplayPage.getGroupExternalReferenceCode();

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

		if (entityFieldName.equals("pageUuid")) {
			Object object = categoryDisplayPage.getPageUuid();

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

	protected CategoryDisplayPage randomCategoryDisplayPage() throws Exception {
		return new CategoryDisplayPage() {
			{
				categoryExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				categoryId = RandomTestUtil.randomLong();
				groupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				pageUuid = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected CategoryDisplayPage randomIrrelevantCategoryDisplayPage()
		throws Exception {

		CategoryDisplayPage randomIrrelevantCategoryDisplayPage =
			randomCategoryDisplayPage();

		return randomIrrelevantCategoryDisplayPage;
	}

	protected CategoryDisplayPage randomPatchCategoryDisplayPage()
		throws Exception {

		return randomCategoryDisplayPage();
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

	protected CategoryDisplayPageResource categoryDisplayPageResource;
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
		LogFactoryUtil.getLog(BaseCategoryDisplayPageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		CategoryDisplayPageResource _categoryDisplayPageResource;

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