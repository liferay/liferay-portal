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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SkuVirtualSettingsFileEntryResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.SkuVirtualSettingsFileEntrySerDes;
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
public abstract class BaseSkuVirtualSettingsFileEntryResourceTestCase {

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

		_skuVirtualSettingsFileEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		skuVirtualSettingsFileEntryResource =
			SkuVirtualSettingsFileEntryResource.builder(
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

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 =
			randomSkuVirtualSettingsFileEntry();

		String json = objectMapper.writeValueAsString(
			skuVirtualSettingsFileEntry1);

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2 =
			SkuVirtualSettingsFileEntrySerDes.toDTO(json);

		Assert.assertTrue(
			equals(skuVirtualSettingsFileEntry1, skuVirtualSettingsFileEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry =
			randomSkuVirtualSettingsFileEntry();

		String json1 = objectMapper.writeValueAsString(
			skuVirtualSettingsFileEntry);
		String json2 = SkuVirtualSettingsFileEntrySerDes.toJSON(
			skuVirtualSettingsFileEntry);

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

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry =
			randomSkuVirtualSettingsFileEntry();

		skuVirtualSettingsFileEntry.setAttachment(regex);
		skuVirtualSettingsFileEntry.setSrc(regex);
		skuVirtualSettingsFileEntry.setUrl(regex);
		skuVirtualSettingsFileEntry.setVersion(regex);

		String json = SkuVirtualSettingsFileEntrySerDes.toJSON(
			skuVirtualSettingsFileEntry);

		Assert.assertFalse(json.contains(regex));

		skuVirtualSettingsFileEntry = SkuVirtualSettingsFileEntrySerDes.toDTO(
			json);

		Assert.assertEquals(regex, skuVirtualSettingsFileEntry.getAttachment());
		Assert.assertEquals(regex, skuVirtualSettingsFileEntry.getSrc());
		Assert.assertEquals(regex, skuVirtualSettingsFileEntry.getUrl());
		Assert.assertEquals(regex, skuVirtualSettingsFileEntry.getVersion());
	}

	@Test
	public void testDeleteSkuVirtualSettingsFileEntry() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry =
			testDeleteSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();

		assertHttpResponseStatusCode(
			204,
			skuVirtualSettingsFileEntryResource.
				deleteSkuVirtualSettingsFileEntryHttpResponse(
					skuVirtualSettingsFileEntry.getId()));

		assertHttpResponseStatusCode(
			404,
			skuVirtualSettingsFileEntryResource.
				getSkuVirtualSettingsFileEntryHttpResponse(
					skuVirtualSettingsFileEntry.getId()));
		assertHttpResponseStatusCode(
			404,
			skuVirtualSettingsFileEntryResource.
				getSkuVirtualSettingsFileEntryHttpResponse(0L));
	}

	protected SkuVirtualSettingsFileEntry
			testDeleteSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSkuVirtualSettingsFileEntry()
		throws Exception {

		// No namespace

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 =
			testGraphQLDeleteSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSkuVirtualSettingsFileEntry",
						new HashMap<String, Object>() {
							{
								put("id", skuVirtualSettingsFileEntry1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteSkuVirtualSettingsFileEntry"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"skuVirtualSettingsFileEntry",
					new HashMap<String, Object>() {
						{
							put("id", skuVirtualSettingsFileEntry1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2 =
			testGraphQLDeleteSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteSkuVirtualSettingsFileEntry",
							new HashMap<String, Object>() {
								{
									put(
										"id",
										skuVirtualSettingsFileEntry2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteSkuVirtualSettingsFileEntry"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"skuVirtualSettingsFileEntry",
						new HashMap<String, Object>() {
							{
								put("id", skuVirtualSettingsFileEntry2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected SkuVirtualSettingsFileEntry
			testGraphQLDeleteSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry()
		throws Exception {

		return testGraphQLSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();
	}

	@Test
	public void testDeleteSkuVirtualSettingsFileEntryBatch() throws Exception {
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 =
			testDeleteSkuVirtualSettingsFileEntryBatch_addSkuVirtualSettingsFileEntry();

		testDeleteSkuVirtualSettingsFileEntryBatch_deleteSkuVirtualSettingsFileEntry(
			202, null, skuVirtualSettingsFileEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			skuVirtualSettingsFileEntryResource.
				getSkuVirtualSettingsFileEntryHttpResponse(
					skuVirtualSettingsFileEntry1.getId()));
	}

	protected SkuVirtualSettingsFileEntry
			testDeleteSkuVirtualSettingsFileEntryBatch_addSkuVirtualSettingsFileEntry()
		throws Exception {

		return testDeleteSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();
	}

	protected void
			testDeleteSkuVirtualSettingsFileEntryBatch_deleteSkuVirtualSettingsFileEntry(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			skuVirtualSettingsFileEntryResource.
				deleteSkuVirtualSettingsFileEntryBatchHttpResponse(
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
	public void testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage()
		throws Exception {

		Long id =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getId();
		Long irrelevantId =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getIrrelevantId();

		Page<SkuVirtualSettingsFileEntry> page =
			skuVirtualSettingsFileEntryResource.
				getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
					id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			SkuVirtualSettingsFileEntry irrelevantSkuVirtualSettingsFileEntry =
				testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_addSkuVirtualSettingsFileEntry(
					irrelevantId,
					randomIrrelevantSkuVirtualSettingsFileEntry());

			page =
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
						irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantSkuVirtualSettingsFileEntry,
				(List<SkuVirtualSettingsFileEntry>)page.getItems());
			assertValid(
				page,
				testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getExpectedActions(
					irrelevantId));
		}

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_addSkuVirtualSettingsFileEntry(
				id, randomSkuVirtualSettingsFileEntry());

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2 =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_addSkuVirtualSettingsFileEntry(
				id, randomSkuVirtualSettingsFileEntry());

		page =
			skuVirtualSettingsFileEntryResource.
				getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
					id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			skuVirtualSettingsFileEntry1,
			(List<SkuVirtualSettingsFileEntry>)page.getItems());
		assertContains(
			skuVirtualSettingsFileEntry2,
			(List<SkuVirtualSettingsFileEntry>)page.getItems());
		assertValid(
			page,
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getExpectedActions(
				id));

		skuVirtualSettingsFileEntryResource.deleteSkuVirtualSettingsFileEntry(
			skuVirtualSettingsFileEntry1.getId());

		skuVirtualSettingsFileEntryResource.deleteSkuVirtualSettingsFileEntry(
			skuVirtualSettingsFileEntry2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPageWithPagination()
		throws Exception {

		Long id =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getId();

		Page<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntriesPage =
			skuVirtualSettingsFileEntryResource.
				getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
					id, null);

		int totalCount = GetterUtil.getInteger(
			skuVirtualSettingsFileEntriesPage.getTotalCount());

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_addSkuVirtualSettingsFileEntry(
				id, randomSkuVirtualSettingsFileEntry());

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2 =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_addSkuVirtualSettingsFileEntry(
				id, randomSkuVirtualSettingsFileEntry());

		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry3 =
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_addSkuVirtualSettingsFileEntry(
				id, randomSkuVirtualSettingsFileEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SkuVirtualSettingsFileEntry> page1 =
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				skuVirtualSettingsFileEntry1,
				(List<SkuVirtualSettingsFileEntry>)page1.getItems());

			Page<SkuVirtualSettingsFileEntry> page2 =
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				skuVirtualSettingsFileEntry2,
				(List<SkuVirtualSettingsFileEntry>)page2.getItems());

			Page<SkuVirtualSettingsFileEntry> page3 =
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				skuVirtualSettingsFileEntry3,
				(List<SkuVirtualSettingsFileEntry>)page3.getItems());
		}
		else {
			Page<SkuVirtualSettingsFileEntry> page1 =
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
						id, Pagination.of(1, totalCount + 2));

			List<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntries1 =
				(List<SkuVirtualSettingsFileEntry>)page1.getItems();

			Assert.assertEquals(
				skuVirtualSettingsFileEntries1.toString(), totalCount + 2,
				skuVirtualSettingsFileEntries1.size());

			Page<SkuVirtualSettingsFileEntry> page2 =
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
						id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntries2 =
				(List<SkuVirtualSettingsFileEntry>)page2.getItems();

			Assert.assertEquals(
				skuVirtualSettingsFileEntries2.toString(), 1,
				skuVirtualSettingsFileEntries2.size());

			Page<SkuVirtualSettingsFileEntry> page3 =
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
						id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				skuVirtualSettingsFileEntry1,
				(List<SkuVirtualSettingsFileEntry>)page3.getItems());
			assertContains(
				skuVirtualSettingsFileEntry2,
				(List<SkuVirtualSettingsFileEntry>)page3.getItems());
			assertContains(
				skuVirtualSettingsFileEntry3,
				(List<SkuVirtualSettingsFileEntry>)page3.getItems());
		}
	}

	protected SkuVirtualSettingsFileEntry
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_addSkuVirtualSettingsFileEntry(
				Long id,
				SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSkuVirtualSettingsFileEntry() throws Exception {
		SkuVirtualSettingsFileEntry postSkuVirtualSettingsFileEntry =
			testGetSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();

		SkuVirtualSettingsFileEntry getSkuVirtualSettingsFileEntry =
			skuVirtualSettingsFileEntryResource.getSkuVirtualSettingsFileEntry(
				postSkuVirtualSettingsFileEntry.getId());

		assertEquals(
			postSkuVirtualSettingsFileEntry, getSkuVirtualSettingsFileEntry);
		assertValid(getSkuVirtualSettingsFileEntry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		SkuVirtualSettingsFileEntry postSkuVirtualSettingsFileEntry =
			testGetSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();

		SkuVirtualSettingsFileEntry getSkuVirtualSettingsFileEntry =
			skuVirtualSettingsFileEntryResource.getSkuVirtualSettingsFileEntry(
				postSkuVirtualSettingsFileEntry.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettingsFileEntry"
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
			postSkuVirtualSettingsFileEntry.getId());

		assertEquals(
			getSkuVirtualSettingsFileEntry,
			SkuVirtualSettingsFileEntrySerDes.toDTO(item.toString()));
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

	protected SkuVirtualSettingsFileEntry
			testGetSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSkuVirtualSettingsFileEntry() throws Exception {
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry =
			testGraphQLGetSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();

		// No namespace

		Assert.assertTrue(
			equals(
				skuVirtualSettingsFileEntry,
				SkuVirtualSettingsFileEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"skuVirtualSettingsFileEntry",
								new HashMap<String, Object>() {
									{
										put(
											"id",
											skuVirtualSettingsFileEntry.
												getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/skuVirtualSettingsFileEntry"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				skuVirtualSettingsFileEntry,
				SkuVirtualSettingsFileEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"skuVirtualSettingsFileEntry",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												skuVirtualSettingsFileEntry.
													getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/skuVirtualSettingsFileEntry"))));
	}

	@Test
	public void testGraphQLGetSkuVirtualSettingsFileEntryNotFound()
		throws Exception {

		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"skuVirtualSettingsFileEntry",
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
							"skuVirtualSettingsFileEntry",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected SkuVirtualSettingsFileEntry
			testGraphQLGetSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry()
		throws Exception {

		return testGraphQLSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();
	}

	@Test
	public void testPatchSkuVirtualSettingsFileEntry() throws Exception {
		SkuVirtualSettingsFileEntry postSkuVirtualSettingsFileEntry =
			testPatchSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();

		SkuVirtualSettingsFileEntry randomPatchSkuVirtualSettingsFileEntry =
			randomPatchSkuVirtualSettingsFileEntry();

		Map<String, File> multipartFiles = getMultipartFiles();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SkuVirtualSettingsFileEntry patchSkuVirtualSettingsFileEntry =
			skuVirtualSettingsFileEntryResource.
				patchSkuVirtualSettingsFileEntry(
					postSkuVirtualSettingsFileEntry.getId(),
					randomPatchSkuVirtualSettingsFileEntry, multipartFiles);

		SkuVirtualSettingsFileEntry expectedPatchSkuVirtualSettingsFileEntry =
			postSkuVirtualSettingsFileEntry.clone();

		BeanTestUtil.copyProperties(
			randomPatchSkuVirtualSettingsFileEntry,
			expectedPatchSkuVirtualSettingsFileEntry);

		SkuVirtualSettingsFileEntry getSkuVirtualSettingsFileEntry =
			skuVirtualSettingsFileEntryResource.getSkuVirtualSettingsFileEntry(
				patchSkuVirtualSettingsFileEntry.getId());

		assertEquals(
			expectedPatchSkuVirtualSettingsFileEntry,
			getSkuVirtualSettingsFileEntry);
		assertValid(getSkuVirtualSettingsFileEntry);

		assertValid(getSkuVirtualSettingsFileEntry, multipartFiles);
	}

	protected SkuVirtualSettingsFileEntry
			testPatchSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSkuVirtualSettingIdSkuVirtualSettingsFileEntry()
		throws Exception {

		SkuVirtualSettingsFileEntry randomSkuVirtualSettingsFileEntry =
			randomSkuVirtualSettingsFileEntry();

		Map<String, File> multipartFiles = getMultipartFiles();

		SkuVirtualSettingsFileEntry postSkuVirtualSettingsFileEntry =
			testPostSkuVirtualSettingIdSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry(
				randomSkuVirtualSettingsFileEntry, multipartFiles);

		assertEquals(
			randomSkuVirtualSettingsFileEntry, postSkuVirtualSettingsFileEntry);
		assertValid(postSkuVirtualSettingsFileEntry);

		assertValid(postSkuVirtualSettingsFileEntry, multipartFiles);
	}

	protected SkuVirtualSettingsFileEntry
			testPostSkuVirtualSettingIdSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry(
				SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry,
				Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 =
			testBatchEngineDeleteImportTask_addSkuVirtualSettingsFileEntry();

		testBatchEngineDeleteImportTask_deleteSkuVirtualSettingsFileEntry(
			200, null, skuVirtualSettingsFileEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			skuVirtualSettingsFileEntryResource.
				getSkuVirtualSettingsFileEntryHttpResponse(
					skuVirtualSettingsFileEntry1.getId()));
	}

	protected SkuVirtualSettingsFileEntry
			testBatchEngineDeleteImportTask_addSkuVirtualSettingsFileEntry()
		throws Exception {

		return testDeleteSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteSkuVirtualSettingsFileEntry(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettingsFileEntry",
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

	protected SkuVirtualSettingsFileEntry
			testGraphQLSkuVirtualSettingsFileEntry_addSkuVirtualSettingsFileEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry,
		List<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntries) {

		boolean contains = false;

		for (SkuVirtualSettingsFileEntry item : skuVirtualSettingsFileEntries) {
			if (equals(skuVirtualSettingsFileEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			skuVirtualSettingsFileEntries + " does not contain " +
				skuVirtualSettingsFileEntry,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1,
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2) {

		Assert.assertTrue(
			skuVirtualSettingsFileEntry1 + " does not equal " +
				skuVirtualSettingsFileEntry2,
			equals(skuVirtualSettingsFileEntry1, skuVirtualSettingsFileEntry2));
	}

	protected void assertEquals(
		List<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntries1,
		List<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntries2) {

		Assert.assertEquals(
			skuVirtualSettingsFileEntries1.size(),
			skuVirtualSettingsFileEntries2.size());

		for (int i = 0; i < skuVirtualSettingsFileEntries1.size(); i++) {
			SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 =
				skuVirtualSettingsFileEntries1.get(i);
			SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2 =
				skuVirtualSettingsFileEntries2.get(i);

			assertEquals(
				skuVirtualSettingsFileEntry1, skuVirtualSettingsFileEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntries1,
		List<SkuVirtualSettingsFileEntry> skuVirtualSettingsFileEntries2) {

		Assert.assertEquals(
			skuVirtualSettingsFileEntries1.size(),
			skuVirtualSettingsFileEntries2.size());

		for (SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1 :
				skuVirtualSettingsFileEntries1) {

			boolean contains = false;

			for (SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2 :
					skuVirtualSettingsFileEntries2) {

				if (equals(
						skuVirtualSettingsFileEntry1,
						skuVirtualSettingsFileEntry2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				skuVirtualSettingsFileEntries2 + " does not contain " +
					skuVirtualSettingsFileEntry1,
				contains);
		}
	}

	protected void assertValid(
			SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry)
		throws Exception {

		boolean valid = true;

		if (skuVirtualSettingsFileEntry.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (skuVirtualSettingsFileEntry.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (skuVirtualSettingsFileEntry.getAttachment() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("src", additionalAssertFieldName)) {
				if (skuVirtualSettingsFileEntry.getSrc() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (skuVirtualSettingsFileEntry.getUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (skuVirtualSettingsFileEntry.getVersion() == null) {
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
			SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry,
			Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<SkuVirtualSettingsFileEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SkuVirtualSettingsFileEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SkuVirtualSettingsFileEntry>
			skuVirtualSettingsFileEntries = page.getItems();

		int size = skuVirtualSettingsFileEntries.size();

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
						SkuVirtualSettingsFileEntry.class)) {

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
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry1,
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry2) {

		if (skuVirtualSettingsFileEntry1 == skuVirtualSettingsFileEntry2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)skuVirtualSettingsFileEntry1.getActions(),
						(Map)skuVirtualSettingsFileEntry2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						skuVirtualSettingsFileEntry1.getAttachment(),
						skuVirtualSettingsFileEntry2.getAttachment())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						skuVirtualSettingsFileEntry1.getId(),
						skuVirtualSettingsFileEntry2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("src", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						skuVirtualSettingsFileEntry1.getSrc(),
						skuVirtualSettingsFileEntry2.getSrc())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						skuVirtualSettingsFileEntry1.getUrl(),
						skuVirtualSettingsFileEntry2.getUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						skuVirtualSettingsFileEntry1.getVersion(),
						skuVirtualSettingsFileEntry2.getVersion())) {

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

		if (!(_skuVirtualSettingsFileEntryResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_skuVirtualSettingsFileEntryResource;

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
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry) {

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
			Object object = skuVirtualSettingsFileEntry.getAttachment();

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
			Object object = skuVirtualSettingsFileEntry.getSrc();

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
			Object object = skuVirtualSettingsFileEntry.getUrl();

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
			Object object = skuVirtualSettingsFileEntry.getVersion();

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

	protected SkuVirtualSettingsFileEntry randomSkuVirtualSettingsFileEntry()
		throws Exception {

		return new SkuVirtualSettingsFileEntry() {
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

	protected SkuVirtualSettingsFileEntry
			randomIrrelevantSkuVirtualSettingsFileEntry()
		throws Exception {

		SkuVirtualSettingsFileEntry
			randomIrrelevantSkuVirtualSettingsFileEntry =
				randomSkuVirtualSettingsFileEntry();

		return randomIrrelevantSkuVirtualSettingsFileEntry;
	}

	protected SkuVirtualSettingsFileEntry
			randomPatchSkuVirtualSettingsFileEntry()
		throws Exception {

		return randomSkuVirtualSettingsFileEntry();
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

	protected SkuVirtualSettingsFileEntryResource
		skuVirtualSettingsFileEntryResource;
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
			BaseSkuVirtualSettingsFileEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		SkuVirtualSettingsFileEntryResource
			_skuVirtualSettingsFileEntryResource;

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