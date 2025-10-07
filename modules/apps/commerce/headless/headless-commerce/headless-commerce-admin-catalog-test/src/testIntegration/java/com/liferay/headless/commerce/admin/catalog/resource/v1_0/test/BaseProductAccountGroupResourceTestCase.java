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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductAccountGroup;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductAccountGroupResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductAccountGroupSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
public abstract class BaseProductAccountGroupResourceTestCase {

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

		_productAccountGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productAccountGroupResource = ProductAccountGroupResource.builder(
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

		ProductAccountGroup productAccountGroup1 = randomProductAccountGroup();

		String json = objectMapper.writeValueAsString(productAccountGroup1);

		ProductAccountGroup productAccountGroup2 =
			ProductAccountGroupSerDes.toDTO(json);

		Assert.assertTrue(equals(productAccountGroup1, productAccountGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductAccountGroup productAccountGroup = randomProductAccountGroup();

		String json1 = objectMapper.writeValueAsString(productAccountGroup);
		String json2 = ProductAccountGroupSerDes.toJSON(productAccountGroup);

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

		ProductAccountGroup productAccountGroup = randomProductAccountGroup();

		productAccountGroup.setExternalReferenceCode(regex);
		productAccountGroup.setName(regex);

		String json = ProductAccountGroupSerDes.toJSON(productAccountGroup);

		Assert.assertFalse(json.contains(regex));

		productAccountGroup = ProductAccountGroupSerDes.toDTO(json);

		Assert.assertEquals(
			regex, productAccountGroup.getExternalReferenceCode());
		Assert.assertEquals(regex, productAccountGroup.getName());
	}

	@Test
	public void testDeleteProductAccountGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductAccountGroup productAccountGroup =
			testDeleteProductAccountGroup_addProductAccountGroup();

		assertHttpResponseStatusCode(
			204,
			productAccountGroupResource.deleteProductAccountGroupHttpResponse(
				productAccountGroup.getId()));

		assertHttpResponseStatusCode(
			404,
			productAccountGroupResource.getProductAccountGroupHttpResponse(
				productAccountGroup.getId()));
		assertHttpResponseStatusCode(
			404,
			productAccountGroupResource.getProductAccountGroupHttpResponse(0L));
	}

	protected ProductAccountGroup
			testDeleteProductAccountGroup_addProductAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductAccountGroup() throws Exception {

		// No namespace

		ProductAccountGroup productAccountGroup1 =
			testGraphQLDeleteProductAccountGroup_addProductAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductAccountGroup",
						new HashMap<String, Object>() {
							{
								put("id", productAccountGroup1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductAccountGroup"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productAccountGroup",
					new HashMap<String, Object>() {
						{
							put("id", productAccountGroup1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductAccountGroup productAccountGroup2 =
			testGraphQLDeleteProductAccountGroup_addProductAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductAccountGroup",
							new HashMap<String, Object>() {
								{
									put("id", productAccountGroup2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductAccountGroup"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productAccountGroup",
						new HashMap<String, Object>() {
							{
								put("id", productAccountGroup2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductAccountGroup
			testGraphQLDeleteProductAccountGroup_addProductAccountGroup()
		throws Exception {

		return testGraphQLProductAccountGroup_addProductAccountGroup();
	}

	@Test
	public void testDeleteProductAccountGroupBatch() throws Exception {
		ProductAccountGroup productAccountGroup1 =
			testDeleteProductAccountGroupBatch_addProductAccountGroup();

		testDeleteProductAccountGroupBatch_deleteProductAccountGroup(
			202, null, productAccountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productAccountGroupResource.getProductAccountGroupHttpResponse(
				productAccountGroup1.getId()));
	}

	protected ProductAccountGroup
			testDeleteProductAccountGroupBatch_addProductAccountGroup()
		throws Exception {

		return testDeleteProductAccountGroup_addProductAccountGroup();
	}

	protected void testDeleteProductAccountGroupBatch_deleteProductAccountGroup(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productAccountGroupResource.
				deleteProductAccountGroupBatchHttpResponse(
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
	public void testGetProductAccountGroup() throws Exception {
		ProductAccountGroup postProductAccountGroup =
			testGetProductAccountGroup_addProductAccountGroup();

		ProductAccountGroup getProductAccountGroup =
			productAccountGroupResource.getProductAccountGroup(
				postProductAccountGroup.getId());

		assertEquals(postProductAccountGroup, getProductAccountGroup);
		assertValid(getProductAccountGroup);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductAccountGroup postProductAccountGroup =
			testGetProductAccountGroup_addProductAccountGroup();

		ProductAccountGroup getProductAccountGroup =
			productAccountGroupResource.getProductAccountGroup(
				postProductAccountGroup.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductAccountGroup"
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
			postProductAccountGroup.getId());

		assertEquals(
			getProductAccountGroup,
			ProductAccountGroupSerDes.toDTO(item.toString()));
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

	protected ProductAccountGroup
			testGetProductAccountGroup_addProductAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductAccountGroup() throws Exception {
		ProductAccountGroup productAccountGroup =
			testGraphQLGetProductAccountGroup_addProductAccountGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				productAccountGroup,
				ProductAccountGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productAccountGroup",
								new HashMap<String, Object>() {
									{
										put("id", productAccountGroup.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productAccountGroup"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productAccountGroup,
				ProductAccountGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productAccountGroup",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												productAccountGroup.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productAccountGroup"))));
	}

	@Test
	public void testGraphQLGetProductAccountGroupNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productAccountGroup",
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
							"productAccountGroup",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductAccountGroup
			testGraphQLGetProductAccountGroup_addProductAccountGroup()
		throws Exception {

		return testGraphQLProductAccountGroup_addProductAccountGroup();
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductAccountGroupsPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_getIrrelevantExternalReferenceCode();

		Page<ProductAccountGroup> page =
			productAccountGroupResource.
				getProductByExternalReferenceCodeProductAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductAccountGroup irrelevantProductAccountGroup =
				testGetProductByExternalReferenceCodeProductAccountGroupsPage_addProductAccountGroup(
					irrelevantExternalReferenceCode,
					randomIrrelevantProductAccountGroup());

			page =
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductAccountGroup,
				(List<ProductAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetProductByExternalReferenceCodeProductAccountGroupsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductAccountGroup productAccountGroup1 =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_addProductAccountGroup(
				externalReferenceCode, randomProductAccountGroup());

		ProductAccountGroup productAccountGroup2 =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_addProductAccountGroup(
				externalReferenceCode, randomProductAccountGroup());

		page =
			productAccountGroupResource.
				getProductByExternalReferenceCodeProductAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productAccountGroup1, (List<ProductAccountGroup>)page.getItems());
		assertContains(
			productAccountGroup2, (List<ProductAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_getExpectedActions(
				externalReferenceCode));

		productAccountGroupResource.deleteProductAccountGroup(
			productAccountGroup1.getId());

		productAccountGroupResource.deleteProductAccountGroup(
			productAccountGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductAccountGroupsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_getExternalReferenceCode();

		Page<ProductAccountGroup> productAccountGroupsPage =
			productAccountGroupResource.
				getProductByExternalReferenceCodeProductAccountGroupsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			productAccountGroupsPage.getTotalCount());

		ProductAccountGroup productAccountGroup1 =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_addProductAccountGroup(
				externalReferenceCode, randomProductAccountGroup());

		ProductAccountGroup productAccountGroup2 =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_addProductAccountGroup(
				externalReferenceCode, randomProductAccountGroup());

		ProductAccountGroup productAccountGroup3 =
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_addProductAccountGroup(
				externalReferenceCode, randomProductAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductAccountGroup> page1 =
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productAccountGroup1,
				(List<ProductAccountGroup>)page1.getItems());

			Page<ProductAccountGroup> page2 =
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productAccountGroup2,
				(List<ProductAccountGroup>)page2.getItems());

			Page<ProductAccountGroup> page3 =
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productAccountGroup3,
				(List<ProductAccountGroup>)page3.getItems());
		}
		else {
			Page<ProductAccountGroup> page1 =
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ProductAccountGroup> productAccountGroups1 =
				(List<ProductAccountGroup>)page1.getItems();

			Assert.assertEquals(
				productAccountGroups1.toString(), totalCount + 2,
				productAccountGroups1.size());

			Page<ProductAccountGroup> page2 =
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductAccountGroup> productAccountGroups2 =
				(List<ProductAccountGroup>)page2.getItems();

			Assert.assertEquals(
				productAccountGroups2.toString(), 1,
				productAccountGroups2.size());

			Page<ProductAccountGroup> page3 =
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productAccountGroup1,
				(List<ProductAccountGroup>)page3.getItems());
			assertContains(
				productAccountGroup2,
				(List<ProductAccountGroup>)page3.getItems());
			assertContains(
				productAccountGroup3,
				(List<ProductAccountGroup>)page3.getItems());
		}
	}

	protected ProductAccountGroup
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_addProductAccountGroup(
				String externalReferenceCode,
				ProductAccountGroup productAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeProductAccountGroupsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductIdProductAccountGroupsPage() throws Exception {
		Long id = testGetProductIdProductAccountGroupsPage_getId();
		Long irrelevantId =
			testGetProductIdProductAccountGroupsPage_getIrrelevantId();

		Page<ProductAccountGroup> page =
			productAccountGroupResource.getProductIdProductAccountGroupsPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductAccountGroup irrelevantProductAccountGroup =
				testGetProductIdProductAccountGroupsPage_addProductAccountGroup(
					irrelevantId, randomIrrelevantProductAccountGroup());

			page =
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductAccountGroup,
				(List<ProductAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetProductIdProductAccountGroupsPage_getExpectedActions(
					irrelevantId));
		}

		ProductAccountGroup productAccountGroup1 =
			testGetProductIdProductAccountGroupsPage_addProductAccountGroup(
				id, randomProductAccountGroup());

		ProductAccountGroup productAccountGroup2 =
			testGetProductIdProductAccountGroupsPage_addProductAccountGroup(
				id, randomProductAccountGroup());

		page = productAccountGroupResource.getProductIdProductAccountGroupsPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productAccountGroup1, (List<ProductAccountGroup>)page.getItems());
		assertContains(
			productAccountGroup2, (List<ProductAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetProductIdProductAccountGroupsPage_getExpectedActions(id));

		productAccountGroupResource.deleteProductAccountGroup(
			productAccountGroup1.getId());

		productAccountGroupResource.deleteProductAccountGroup(
			productAccountGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductIdProductAccountGroupsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductIdProductAccountGroupsPageWithPagination()
		throws Exception {

		Long id = testGetProductIdProductAccountGroupsPage_getId();

		Page<ProductAccountGroup> productAccountGroupsPage =
			productAccountGroupResource.getProductIdProductAccountGroupsPage(
				id, null);

		int totalCount = GetterUtil.getInteger(
			productAccountGroupsPage.getTotalCount());

		ProductAccountGroup productAccountGroup1 =
			testGetProductIdProductAccountGroupsPage_addProductAccountGroup(
				id, randomProductAccountGroup());

		ProductAccountGroup productAccountGroup2 =
			testGetProductIdProductAccountGroupsPage_addProductAccountGroup(
				id, randomProductAccountGroup());

		ProductAccountGroup productAccountGroup3 =
			testGetProductIdProductAccountGroupsPage_addProductAccountGroup(
				id, randomProductAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductAccountGroup> page1 =
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productAccountGroup1,
				(List<ProductAccountGroup>)page1.getItems());

			Page<ProductAccountGroup> page2 =
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productAccountGroup2,
				(List<ProductAccountGroup>)page2.getItems());

			Page<ProductAccountGroup> page3 =
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productAccountGroup3,
				(List<ProductAccountGroup>)page3.getItems());
		}
		else {
			Page<ProductAccountGroup> page1 =
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						id, Pagination.of(1, totalCount + 2));

			List<ProductAccountGroup> productAccountGroups1 =
				(List<ProductAccountGroup>)page1.getItems();

			Assert.assertEquals(
				productAccountGroups1.toString(), totalCount + 2,
				productAccountGroups1.size());

			Page<ProductAccountGroup> page2 =
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductAccountGroup> productAccountGroups2 =
				(List<ProductAccountGroup>)page2.getItems();

			Assert.assertEquals(
				productAccountGroups2.toString(), 1,
				productAccountGroups2.size());

			Page<ProductAccountGroup> page3 =
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productAccountGroup1,
				(List<ProductAccountGroup>)page3.getItems());
			assertContains(
				productAccountGroup2,
				(List<ProductAccountGroup>)page3.getItems());
			assertContains(
				productAccountGroup3,
				(List<ProductAccountGroup>)page3.getItems());
		}
	}

	protected ProductAccountGroup
			testGetProductIdProductAccountGroupsPage_addProductAccountGroup(
				Long id, ProductAccountGroup productAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdProductAccountGroupsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdProductAccountGroupsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductAccountGroup productAccountGroup1 =
			testBatchEngineDeleteImportTask_addProductAccountGroup();

		testBatchEngineDeleteImportTask_deleteProductAccountGroup(
			200, null, productAccountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			productAccountGroupResource.getProductAccountGroupHttpResponse(
				productAccountGroup1.getId()));
	}

	protected ProductAccountGroup
			testBatchEngineDeleteImportTask_addProductAccountGroup()
		throws Exception {

		return testDeleteProductAccountGroup_addProductAccountGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteProductAccountGroup(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductAccountGroup",
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

	protected ProductAccountGroup
			testGraphQLProductAccountGroup_addProductAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductAccountGroup productAccountGroup,
		List<ProductAccountGroup> productAccountGroups) {

		boolean contains = false;

		for (ProductAccountGroup item : productAccountGroups) {
			if (equals(productAccountGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productAccountGroups + " does not contain " + productAccountGroup,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductAccountGroup productAccountGroup1,
		ProductAccountGroup productAccountGroup2) {

		Assert.assertTrue(
			productAccountGroup1 + " does not equal " + productAccountGroup2,
			equals(productAccountGroup1, productAccountGroup2));
	}

	protected void assertEquals(
		List<ProductAccountGroup> productAccountGroups1,
		List<ProductAccountGroup> productAccountGroups2) {

		Assert.assertEquals(
			productAccountGroups1.size(), productAccountGroups2.size());

		for (int i = 0; i < productAccountGroups1.size(); i++) {
			ProductAccountGroup productAccountGroup1 =
				productAccountGroups1.get(i);
			ProductAccountGroup productAccountGroup2 =
				productAccountGroups2.get(i);

			assertEquals(productAccountGroup1, productAccountGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductAccountGroup> productAccountGroups1,
		List<ProductAccountGroup> productAccountGroups2) {

		Assert.assertEquals(
			productAccountGroups1.size(), productAccountGroups2.size());

		for (ProductAccountGroup productAccountGroup1 : productAccountGroups1) {
			boolean contains = false;

			for (ProductAccountGroup productAccountGroup2 :
					productAccountGroups2) {

				if (equals(productAccountGroup1, productAccountGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productAccountGroups2 + " does not contain " +
					productAccountGroup1,
				contains);
		}
	}

	protected void assertValid(ProductAccountGroup productAccountGroup)
		throws Exception {

		boolean valid = true;

		if (productAccountGroup.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (productAccountGroup.getAccountGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (productAccountGroup.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (productAccountGroup.getName() == null) {
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

	protected void assertValid(Page<ProductAccountGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductAccountGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductAccountGroup> productAccountGroups =
			page.getItems();

		int size = productAccountGroups.size();

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
						ProductAccountGroup.class)) {

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
		ProductAccountGroup productAccountGroup1,
		ProductAccountGroup productAccountGroup2) {

		if (productAccountGroup1 == productAccountGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productAccountGroup1.getAccountGroupId(),
						productAccountGroup2.getAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productAccountGroup1.getExternalReferenceCode(),
						productAccountGroup2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productAccountGroup1.getId(),
						productAccountGroup2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productAccountGroup1.getName(),
						productAccountGroup2.getName())) {

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

		if (!(_productAccountGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productAccountGroupResource;

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
		ProductAccountGroup productAccountGroup) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountGroupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = productAccountGroup.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = productAccountGroup.getName();

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

	protected ProductAccountGroup randomProductAccountGroup() throws Exception {
		return new ProductAccountGroup() {
			{
				accountGroupId = RandomTestUtil.randomLong();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ProductAccountGroup randomIrrelevantProductAccountGroup()
		throws Exception {

		ProductAccountGroup randomIrrelevantProductAccountGroup =
			randomProductAccountGroup();

		return randomIrrelevantProductAccountGroup;
	}

	protected ProductAccountGroup randomPatchProductAccountGroup()
		throws Exception {

		return randomProductAccountGroup();
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

	protected ProductAccountGroupResource productAccountGroupResource;
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
		LogFactoryUtil.getLog(BaseProductAccountGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductAccountGroupResource _productAccountGroupResource;

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