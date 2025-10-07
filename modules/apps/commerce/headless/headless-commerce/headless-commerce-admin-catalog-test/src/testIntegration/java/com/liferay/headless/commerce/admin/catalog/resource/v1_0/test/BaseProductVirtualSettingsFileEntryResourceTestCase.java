/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductVirtualSettingsFileEntryResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductVirtualSettingsFileEntrySerDes;
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

import java.io.File;

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
public abstract class BaseProductVirtualSettingsFileEntryResourceTestCase {

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

		_productVirtualSettingsFileEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productVirtualSettingsFileEntryResource =
			ProductVirtualSettingsFileEntryResource.builder(
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

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 =
			randomProductVirtualSettingsFileEntry();

		String json = objectMapper.writeValueAsString(
			productVirtualSettingsFileEntry1);

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry2 =
			ProductVirtualSettingsFileEntrySerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				productVirtualSettingsFileEntry1,
				productVirtualSettingsFileEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry =
			randomProductVirtualSettingsFileEntry();

		String json1 = objectMapper.writeValueAsString(
			productVirtualSettingsFileEntry);
		String json2 = ProductVirtualSettingsFileEntrySerDes.toJSON(
			productVirtualSettingsFileEntry);

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

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry =
			randomProductVirtualSettingsFileEntry();

		productVirtualSettingsFileEntry.setAttachment(regex);
		productVirtualSettingsFileEntry.setSrc(regex);
		productVirtualSettingsFileEntry.setUrl(regex);
		productVirtualSettingsFileEntry.setVersion(regex);

		String json = ProductVirtualSettingsFileEntrySerDes.toJSON(
			productVirtualSettingsFileEntry);

		Assert.assertFalse(json.contains(regex));

		productVirtualSettingsFileEntry =
			ProductVirtualSettingsFileEntrySerDes.toDTO(json);

		Assert.assertEquals(
			regex, productVirtualSettingsFileEntry.getAttachment());
		Assert.assertEquals(regex, productVirtualSettingsFileEntry.getSrc());
		Assert.assertEquals(regex, productVirtualSettingsFileEntry.getUrl());
		Assert.assertEquals(
			regex, productVirtualSettingsFileEntry.getVersion());
	}

	@Test
	public void testDeleteProductVirtualSettingsFileEntry() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry =
			testDeleteProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();

		assertHttpResponseStatusCode(
			204,
			productVirtualSettingsFileEntryResource.
				deleteProductVirtualSettingsFileEntryHttpResponse(
					productVirtualSettingsFileEntry.getId()));

		assertHttpResponseStatusCode(
			404,
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingsFileEntryHttpResponse(
					productVirtualSettingsFileEntry.getId()));
		assertHttpResponseStatusCode(
			404,
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingsFileEntryHttpResponse(0L));
	}

	protected ProductVirtualSettingsFileEntry
			testDeleteProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductVirtualSettingsFileEntry()
		throws Exception {

		// No namespace

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 =
			testGraphQLDeleteProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductVirtualSettingsFileEntry",
						new HashMap<String, Object>() {
							{
								put(
									"id",
									productVirtualSettingsFileEntry1.getId());
							}
						})),
				"JSONObject/data",
				"Object/deleteProductVirtualSettingsFileEntry"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productVirtualSettingsFileEntry",
					new HashMap<String, Object>() {
						{
							put("id", productVirtualSettingsFileEntry1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry2 =
			testGraphQLDeleteProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductVirtualSettingsFileEntry",
							new HashMap<String, Object>() {
								{
									put(
										"id",
										productVirtualSettingsFileEntry2.
											getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductVirtualSettingsFileEntry"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productVirtualSettingsFileEntry",
						new HashMap<String, Object>() {
							{
								put(
									"id",
									productVirtualSettingsFileEntry2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductVirtualSettingsFileEntry
			testGraphQLDeleteProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry()
		throws Exception {

		return testGraphQLProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();
	}

	@Test
	public void testDeleteProductVirtualSettingsFileEntryBatch()
		throws Exception {

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 =
			testDeleteProductVirtualSettingsFileEntryBatch_addProductVirtualSettingsFileEntry();

		testDeleteProductVirtualSettingsFileEntryBatch_deleteProductVirtualSettingsFileEntry(
			202, null, productVirtualSettingsFileEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingsFileEntryHttpResponse(
					productVirtualSettingsFileEntry1.getId()));
	}

	protected ProductVirtualSettingsFileEntry
			testDeleteProductVirtualSettingsFileEntryBatch_addProductVirtualSettingsFileEntry()
		throws Exception {

		return testDeleteProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();
	}

	protected void
			testDeleteProductVirtualSettingsFileEntryBatch_deleteProductVirtualSettingsFileEntry(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productVirtualSettingsFileEntryResource.
				deleteProductVirtualSettingsFileEntryBatchHttpResponse(
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
	public void testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage()
		throws Exception {

		Long id =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getId();
		Long irrelevantId =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getIrrelevantId();

		Page<ProductVirtualSettingsFileEntry> page =
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
					id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductVirtualSettingsFileEntry
				irrelevantProductVirtualSettingsFileEntry =
					testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_addProductVirtualSettingsFileEntry(
						irrelevantId,
						randomIrrelevantProductVirtualSettingsFileEntry());

			page =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductVirtualSettingsFileEntry,
				(List<ProductVirtualSettingsFileEntry>)page.getItems());
			assertValid(
				page,
				testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getExpectedActions(
					irrelevantId));
		}

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_addProductVirtualSettingsFileEntry(
				id, randomProductVirtualSettingsFileEntry());

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry2 =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_addProductVirtualSettingsFileEntry(
				id, randomProductVirtualSettingsFileEntry());

		page =
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
					id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productVirtualSettingsFileEntry1,
			(List<ProductVirtualSettingsFileEntry>)page.getItems());
		assertContains(
			productVirtualSettingsFileEntry2,
			(List<ProductVirtualSettingsFileEntry>)page.getItems());
		assertValid(
			page,
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getExpectedActions(
				id));

		productVirtualSettingsFileEntryResource.
			deleteProductVirtualSettingsFileEntry(
				productVirtualSettingsFileEntry1.getId());

		productVirtualSettingsFileEntryResource.
			deleteProductVirtualSettingsFileEntry(
				productVirtualSettingsFileEntry2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPageWithPagination()
		throws Exception {

		Long id =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getId();

		Page<ProductVirtualSettingsFileEntry>
			productVirtualSettingsFileEntriesPage =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						id, null);

		int totalCount = GetterUtil.getInteger(
			productVirtualSettingsFileEntriesPage.getTotalCount());

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_addProductVirtualSettingsFileEntry(
				id, randomProductVirtualSettingsFileEntry());

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry2 =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_addProductVirtualSettingsFileEntry(
				id, randomProductVirtualSettingsFileEntry());

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry3 =
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_addProductVirtualSettingsFileEntry(
				id, randomProductVirtualSettingsFileEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductVirtualSettingsFileEntry> page1 =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productVirtualSettingsFileEntry1,
				(List<ProductVirtualSettingsFileEntry>)page1.getItems());

			Page<ProductVirtualSettingsFileEntry> page2 =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productVirtualSettingsFileEntry2,
				(List<ProductVirtualSettingsFileEntry>)page2.getItems());

			Page<ProductVirtualSettingsFileEntry> page3 =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productVirtualSettingsFileEntry3,
				(List<ProductVirtualSettingsFileEntry>)page3.getItems());
		}
		else {
			Page<ProductVirtualSettingsFileEntry> page1 =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						id, Pagination.of(1, totalCount + 2));

			List<ProductVirtualSettingsFileEntry>
				productVirtualSettingsFileEntries1 =
					(List<ProductVirtualSettingsFileEntry>)page1.getItems();

			Assert.assertEquals(
				productVirtualSettingsFileEntries1.toString(), totalCount + 2,
				productVirtualSettingsFileEntries1.size());

			Page<ProductVirtualSettingsFileEntry> page2 =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductVirtualSettingsFileEntry>
				productVirtualSettingsFileEntries2 =
					(List<ProductVirtualSettingsFileEntry>)page2.getItems();

			Assert.assertEquals(
				productVirtualSettingsFileEntries2.toString(), 1,
				productVirtualSettingsFileEntries2.size());

			Page<ProductVirtualSettingsFileEntry> page3 =
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
						id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productVirtualSettingsFileEntry1,
				(List<ProductVirtualSettingsFileEntry>)page3.getItems());
			assertContains(
				productVirtualSettingsFileEntry2,
				(List<ProductVirtualSettingsFileEntry>)page3.getItems());
			assertContains(
				productVirtualSettingsFileEntry3,
				(List<ProductVirtualSettingsFileEntry>)page3.getItems());
		}
	}

	protected ProductVirtualSettingsFileEntry
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_addProductVirtualSettingsFileEntry(
				Long id,
				ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductVirtualSettingIdProductVirtualSettingsFileEntriesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductVirtualSettingsFileEntry() throws Exception {
		ProductVirtualSettingsFileEntry postProductVirtualSettingsFileEntry =
			testGetProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();

		ProductVirtualSettingsFileEntry getProductVirtualSettingsFileEntry =
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingsFileEntry(
					postProductVirtualSettingsFileEntry.getId());

		assertEquals(
			postProductVirtualSettingsFileEntry,
			getProductVirtualSettingsFileEntry);
		assertValid(getProductVirtualSettingsFileEntry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductVirtualSettingsFileEntry postProductVirtualSettingsFileEntry =
			testGetProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();

		ProductVirtualSettingsFileEntry getProductVirtualSettingsFileEntry =
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingsFileEntry(
					postProductVirtualSettingsFileEntry.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettingsFileEntry"
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
			postProductVirtualSettingsFileEntry.getId());

		assertEquals(
			getProductVirtualSettingsFileEntry,
			ProductVirtualSettingsFileEntrySerDes.toDTO(item.toString()));
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

	protected ProductVirtualSettingsFileEntry
			testGetProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductVirtualSettingsFileEntry()
		throws Exception {

		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry =
			testGraphQLGetProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();

		// No namespace

		Assert.assertTrue(
			equals(
				productVirtualSettingsFileEntry,
				ProductVirtualSettingsFileEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productVirtualSettingsFileEntry",
								new HashMap<String, Object>() {
									{
										put(
											"id",
											productVirtualSettingsFileEntry.
												getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productVirtualSettingsFileEntry"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productVirtualSettingsFileEntry,
				ProductVirtualSettingsFileEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productVirtualSettingsFileEntry",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												productVirtualSettingsFileEntry.
													getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productVirtualSettingsFileEntry"))));
	}

	@Test
	public void testGraphQLGetProductVirtualSettingsFileEntryNotFound()
		throws Exception {

		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productVirtualSettingsFileEntry",
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
							"productVirtualSettingsFileEntry",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductVirtualSettingsFileEntry
			testGraphQLGetProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry()
		throws Exception {

		return testGraphQLProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();
	}

	@Test
	public void testPatchProductVirtualSettingsFileEntry() throws Exception {
		ProductVirtualSettingsFileEntry postProductVirtualSettingsFileEntry =
			testPatchProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();

		ProductVirtualSettingsFileEntry
			randomPatchProductVirtualSettingsFileEntry =
				randomPatchProductVirtualSettingsFileEntry();

		Map<String, File> multipartFiles = getMultipartFiles();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductVirtualSettingsFileEntry patchProductVirtualSettingsFileEntry =
			productVirtualSettingsFileEntryResource.
				patchProductVirtualSettingsFileEntry(
					postProductVirtualSettingsFileEntry.getId(),
					randomPatchProductVirtualSettingsFileEntry, multipartFiles);

		ProductVirtualSettingsFileEntry
			expectedPatchProductVirtualSettingsFileEntry =
				postProductVirtualSettingsFileEntry.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductVirtualSettingsFileEntry,
			expectedPatchProductVirtualSettingsFileEntry);

		ProductVirtualSettingsFileEntry getProductVirtualSettingsFileEntry =
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingsFileEntry(
					patchProductVirtualSettingsFileEntry.getId());

		assertEquals(
			expectedPatchProductVirtualSettingsFileEntry,
			getProductVirtualSettingsFileEntry);
		assertValid(getProductVirtualSettingsFileEntry);

		assertValid(getProductVirtualSettingsFileEntry, multipartFiles);
	}

	protected ProductVirtualSettingsFileEntry
			testPatchProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductVirtualSettingIdProductVirtualSettingsFileEntry()
		throws Exception {

		ProductVirtualSettingsFileEntry randomProductVirtualSettingsFileEntry =
			randomProductVirtualSettingsFileEntry();

		Map<String, File> multipartFiles = getMultipartFiles();

		ProductVirtualSettingsFileEntry postProductVirtualSettingsFileEntry =
			testPostProductVirtualSettingIdProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry(
				randomProductVirtualSettingsFileEntry, multipartFiles);

		assertEquals(
			randomProductVirtualSettingsFileEntry,
			postProductVirtualSettingsFileEntry);
		assertValid(postProductVirtualSettingsFileEntry);

		assertValid(postProductVirtualSettingsFileEntry, multipartFiles);
	}

	protected ProductVirtualSettingsFileEntry
			testPostProductVirtualSettingIdProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry(
				ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry,
				Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 =
			testBatchEngineDeleteImportTask_addProductVirtualSettingsFileEntry();

		testBatchEngineDeleteImportTask_deleteProductVirtualSettingsFileEntry(
			200, null, productVirtualSettingsFileEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			productVirtualSettingsFileEntryResource.
				getProductVirtualSettingsFileEntryHttpResponse(
					productVirtualSettingsFileEntry1.getId()));
	}

	protected ProductVirtualSettingsFileEntry
			testBatchEngineDeleteImportTask_addProductVirtualSettingsFileEntry()
		throws Exception {

		return testDeleteProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteProductVirtualSettingsFileEntry(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettingsFileEntry",
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

	protected ProductVirtualSettingsFileEntry
			testGraphQLProductVirtualSettingsFileEntry_addProductVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry,
		List<ProductVirtualSettingsFileEntry>
			productVirtualSettingsFileEntries) {

		boolean contains = false;

		for (ProductVirtualSettingsFileEntry item :
				productVirtualSettingsFileEntries) {

			if (equals(productVirtualSettingsFileEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productVirtualSettingsFileEntries + " does not contain " +
				productVirtualSettingsFileEntry,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1,
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry2) {

		Assert.assertTrue(
			productVirtualSettingsFileEntry1 + " does not equal " +
				productVirtualSettingsFileEntry2,
			equals(
				productVirtualSettingsFileEntry1,
				productVirtualSettingsFileEntry2));
	}

	protected void assertEquals(
		List<ProductVirtualSettingsFileEntry>
			productVirtualSettingsFileEntries1,
		List<ProductVirtualSettingsFileEntry>
			productVirtualSettingsFileEntries2) {

		Assert.assertEquals(
			productVirtualSettingsFileEntries1.size(),
			productVirtualSettingsFileEntries2.size());

		for (int i = 0; i < productVirtualSettingsFileEntries1.size(); i++) {
			ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 =
				productVirtualSettingsFileEntries1.get(i);
			ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry2 =
				productVirtualSettingsFileEntries2.get(i);

			assertEquals(
				productVirtualSettingsFileEntry1,
				productVirtualSettingsFileEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductVirtualSettingsFileEntry>
			productVirtualSettingsFileEntries1,
		List<ProductVirtualSettingsFileEntry>
			productVirtualSettingsFileEntries2) {

		Assert.assertEquals(
			productVirtualSettingsFileEntries1.size(),
			productVirtualSettingsFileEntries2.size());

		for (ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1 :
				productVirtualSettingsFileEntries1) {

			boolean contains = false;

			for (ProductVirtualSettingsFileEntry
					productVirtualSettingsFileEntry2 :
						productVirtualSettingsFileEntries2) {

				if (equals(
						productVirtualSettingsFileEntry1,
						productVirtualSettingsFileEntry2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productVirtualSettingsFileEntries2 + " does not contain " +
					productVirtualSettingsFileEntry1,
				contains);
		}
	}

	protected void assertValid(
			ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry)
		throws Exception {

		boolean valid = true;

		if (productVirtualSettingsFileEntry.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (productVirtualSettingsFileEntry.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (productVirtualSettingsFileEntry.getAttachment() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("src", additionalAssertFieldName)) {
				if (productVirtualSettingsFileEntry.getSrc() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (productVirtualSettingsFileEntry.getUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (productVirtualSettingsFileEntry.getVersion() == null) {
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

	protected void assertValid(
			ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry,
			Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<ProductVirtualSettingsFileEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductVirtualSettingsFileEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductVirtualSettingsFileEntry>
			productVirtualSettingsFileEntries = page.getItems();

		int size = productVirtualSettingsFileEntries.size();

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
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						ProductVirtualSettingsFileEntry.class)) {

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
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry1,
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry2) {

		if (productVirtualSettingsFileEntry1 ==
				productVirtualSettingsFileEntry2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)productVirtualSettingsFileEntry1.getActions(),
						(Map)productVirtualSettingsFileEntry2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productVirtualSettingsFileEntry1.getAttachment(),
						productVirtualSettingsFileEntry2.getAttachment())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productVirtualSettingsFileEntry1.getId(),
						productVirtualSettingsFileEntry2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("src", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productVirtualSettingsFileEntry1.getSrc(),
						productVirtualSettingsFileEntry2.getSrc())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productVirtualSettingsFileEntry1.getUrl(),
						productVirtualSettingsFileEntry2.getUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productVirtualSettingsFileEntry1.getVersion(),
						productVirtualSettingsFileEntry2.getVersion())) {

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

		if (!(_productVirtualSettingsFileEntryResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productVirtualSettingsFileEntryResource;

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
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry) {

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

		if (entityFieldName.equals("attachment")) {
			Object object = productVirtualSettingsFileEntry.getAttachment();

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

		if (entityFieldName.equals("src")) {
			Object object = productVirtualSettingsFileEntry.getSrc();

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

		if (entityFieldName.equals("url")) {
			Object object = productVirtualSettingsFileEntry.getUrl();

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

		if (entityFieldName.equals("version")) {
			Object object = productVirtualSettingsFileEntry.getVersion();

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

	protected Map<String, File> getMultipartFiles() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

	protected ProductVirtualSettingsFileEntry
			randomProductVirtualSettingsFileEntry()
		throws Exception {

		return new ProductVirtualSettingsFileEntry() {
			{
				attachment = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				src = StringUtil.toLowerCase(RandomTestUtil.randomString());
				url = StringUtil.toLowerCase(RandomTestUtil.randomString());
				version = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ProductVirtualSettingsFileEntry
			randomIrrelevantProductVirtualSettingsFileEntry()
		throws Exception {

		ProductVirtualSettingsFileEntry
			randomIrrelevantProductVirtualSettingsFileEntry =
				randomProductVirtualSettingsFileEntry();

		return randomIrrelevantProductVirtualSettingsFileEntry;
	}

	protected ProductVirtualSettingsFileEntry
			randomPatchProductVirtualSettingsFileEntry()
		throws Exception {

		return randomProductVirtualSettingsFileEntry();
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

	protected ProductVirtualSettingsFileEntryResource
		productVirtualSettingsFileEntryResource;
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
		LogFactoryUtil.getLog(
			BaseProductVirtualSettingsFileEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductVirtualSettingsFileEntryResource
			_productVirtualSettingsFileEntryResource;

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