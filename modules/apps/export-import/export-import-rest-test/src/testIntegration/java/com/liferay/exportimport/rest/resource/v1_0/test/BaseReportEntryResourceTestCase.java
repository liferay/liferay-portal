/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.exportimport.rest.client.dto.v1_0.ReportEntry;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.ReportEntryResource;
import com.liferay.exportimport.rest.client.serdes.v1_0.ReportEntrySerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public abstract class BaseReportEntryResourceTestCase {

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

		_reportEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		reportEntryResource = ReportEntryResource.builder(
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

		ReportEntry reportEntry1 = randomReportEntry();

		String json = objectMapper.writeValueAsString(reportEntry1);

		ReportEntry reportEntry2 = ReportEntrySerDes.toDTO(json);

		Assert.assertTrue(equals(reportEntry1, reportEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ReportEntry reportEntry = randomReportEntry();

		String json1 = objectMapper.writeValueAsString(reportEntry);
		String json2 = ReportEntrySerDes.toJSON(reportEntry);

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

		ReportEntry reportEntry = randomReportEntry();

		reportEntry.setClassExternalReferenceCode(regex);
		reportEntry.setErrorMessage(regex);
		reportEntry.setErrorStacktrace(regex);
		reportEntry.setModelName(regex);

		String json = ReportEntrySerDes.toJSON(reportEntry);

		Assert.assertFalse(json.contains(regex));

		reportEntry = ReportEntrySerDes.toDTO(json);

		Assert.assertEquals(regex, reportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(regex, reportEntry.getErrorMessage());
		Assert.assertEquals(regex, reportEntry.getErrorStacktrace());
		Assert.assertEquals(regex, reportEntry.getModelName());
	}

	@Test
	public void testGetImportProcessErrorsPage() throws Exception {
		Long importProcessId =
			testGetImportProcessErrorsPage_getImportProcessId();
		Long irrelevantImportProcessId =
			testGetImportProcessErrorsPage_getIrrelevantImportProcessId();

		Page<ReportEntry> page = reportEntryResource.getImportProcessErrorsPage(
			importProcessId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantImportProcessId != null) {
			ReportEntry irrelevantReportEntry =
				testGetImportProcessErrorsPage_addReportEntry(
					irrelevantImportProcessId, randomIrrelevantReportEntry());

			page = reportEntryResource.getImportProcessErrorsPage(
				irrelevantImportProcessId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantReportEntry, (List<ReportEntry>)page.getItems());
			assertValid(
				page,
				testGetImportProcessErrorsPage_getExpectedActions(
					irrelevantImportProcessId));
		}

		ReportEntry reportEntry1 =
			testGetImportProcessErrorsPage_addReportEntry(
				importProcessId, randomReportEntry());

		ReportEntry reportEntry2 =
			testGetImportProcessErrorsPage_addReportEntry(
				importProcessId, randomReportEntry());

		page = reportEntryResource.getImportProcessErrorsPage(
			importProcessId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(reportEntry1, (List<ReportEntry>)page.getItems());
		assertContains(reportEntry2, (List<ReportEntry>)page.getItems());
		assertValid(
			page,
			testGetImportProcessErrorsPage_getExpectedActions(importProcessId));
	}

	protected Map<String, Map<String, String>>
			testGetImportProcessErrorsPage_getExpectedActions(
				Long importProcessId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetImportProcessErrorsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long importProcessId =
			testGetImportProcessErrorsPage_getImportProcessId();

		ReportEntry reportEntry1 = randomReportEntry();

		reportEntry1 = testGetImportProcessErrorsPage_addReportEntry(
			importProcessId, reportEntry1);

		for (EntityField entityField : entityFields) {
			Page<ReportEntry> page =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null,
					getFilterString(entityField, "between", reportEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(reportEntry1),
				(List<ReportEntry>)page.getItems());
		}
	}

	@Test
	public void testGetImportProcessErrorsPageWithFilterDoubleEquals()
		throws Exception {

		testGetImportProcessErrorsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetImportProcessErrorsPageWithFilterStringContains()
		throws Exception {

		testGetImportProcessErrorsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetImportProcessErrorsPageWithFilterStringEquals()
		throws Exception {

		testGetImportProcessErrorsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetImportProcessErrorsPageWithFilterStringStartsWith()
		throws Exception {

		testGetImportProcessErrorsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetImportProcessErrorsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long importProcessId =
			testGetImportProcessErrorsPage_getImportProcessId();

		ReportEntry reportEntry1 =
			testGetImportProcessErrorsPage_addReportEntry(
				importProcessId, randomReportEntry());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ReportEntry reportEntry2 =
			testGetImportProcessErrorsPage_addReportEntry(
				importProcessId, randomReportEntry());

		for (EntityField entityField : entityFields) {
			Page<ReportEntry> page =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null,
					getFilterString(entityField, operator, reportEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(reportEntry1),
				(List<ReportEntry>)page.getItems());
		}
	}

	@Test
	public void testGetImportProcessErrorsPageWithPagination()
		throws Exception {

		Long importProcessId =
			testGetImportProcessErrorsPage_getImportProcessId();

		Page<ReportEntry> reportEntriesPage =
			reportEntryResource.getImportProcessErrorsPage(
				importProcessId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			reportEntriesPage.getTotalCount());

		ReportEntry reportEntry1 =
			testGetImportProcessErrorsPage_addReportEntry(
				importProcessId, randomReportEntry());

		ReportEntry reportEntry2 =
			testGetImportProcessErrorsPage_addReportEntry(
				importProcessId, randomReportEntry());

		ReportEntry reportEntry3 =
			testGetImportProcessErrorsPage_addReportEntry(
				importProcessId, randomReportEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ReportEntry> page1 =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(reportEntry1, (List<ReportEntry>)page1.getItems());

			Page<ReportEntry> page2 =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(reportEntry2, (List<ReportEntry>)page2.getItems());

			Page<ReportEntry> page3 =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(reportEntry3, (List<ReportEntry>)page3.getItems());
		}
		else {
			Page<ReportEntry> page1 =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ReportEntry> reportEntries1 =
				(List<ReportEntry>)page1.getItems();

			Assert.assertEquals(
				reportEntries1.toString(), totalCount + 2,
				reportEntries1.size());

			Page<ReportEntry> page2 =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ReportEntry> reportEntries2 =
				(List<ReportEntry>)page2.getItems();

			Assert.assertEquals(
				reportEntries2.toString(), 1, reportEntries2.size());

			Page<ReportEntry> page3 =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(reportEntry1, (List<ReportEntry>)page3.getItems());
			assertContains(reportEntry2, (List<ReportEntry>)page3.getItems());
			assertContains(reportEntry3, (List<ReportEntry>)page3.getItems());
		}
	}

	@Test
	public void testGetImportProcessErrorsPageWithSortDateTime()
		throws Exception {

		testGetImportProcessErrorsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, reportEntry1, reportEntry2) -> {
				BeanTestUtil.setProperty(
					reportEntry1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetImportProcessErrorsPageWithSortDouble()
		throws Exception {

		testGetImportProcessErrorsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, reportEntry1, reportEntry2) -> {
				BeanTestUtil.setProperty(
					reportEntry1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					reportEntry2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetImportProcessErrorsPageWithSortInteger()
		throws Exception {

		testGetImportProcessErrorsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, reportEntry1, reportEntry2) -> {
				BeanTestUtil.setProperty(
					reportEntry1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					reportEntry2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetImportProcessErrorsPageWithSortString()
		throws Exception {

		testGetImportProcessErrorsPageWithSort(
			EntityField.Type.STRING,
			(entityField, reportEntry1, reportEntry2) -> {
				Class<?> clazz = reportEntry1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						reportEntry1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						reportEntry2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						reportEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						reportEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						reportEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						reportEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetImportProcessErrorsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, ReportEntry, ReportEntry, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long importProcessId =
			testGetImportProcessErrorsPage_getImportProcessId();

		ReportEntry reportEntry1 = randomReportEntry();
		ReportEntry reportEntry2 = randomReportEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, reportEntry1, reportEntry2);
		}

		reportEntry1 = testGetImportProcessErrorsPage_addReportEntry(
			importProcessId, reportEntry1);

		reportEntry2 = testGetImportProcessErrorsPage_addReportEntry(
			importProcessId, reportEntry2);

		Page<ReportEntry> page = reportEntryResource.getImportProcessErrorsPage(
			importProcessId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ReportEntry> ascPage =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(reportEntry1, (List<ReportEntry>)ascPage.getItems());
			assertContains(reportEntry2, (List<ReportEntry>)ascPage.getItems());

			Page<ReportEntry> descPage =
				reportEntryResource.getImportProcessErrorsPage(
					importProcessId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				reportEntry2, (List<ReportEntry>)descPage.getItems());
			assertContains(
				reportEntry1, (List<ReportEntry>)descPage.getItems());
		}
	}

	protected ReportEntry testGetImportProcessErrorsPage_addReportEntry(
			Long importProcessId, ReportEntry reportEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetImportProcessErrorsPage_getImportProcessId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetImportProcessErrorsPage_getIrrelevantImportProcessId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetReportEntry() throws Exception {
		ReportEntry postReportEntry = testGetReportEntry_addReportEntry();

		ReportEntry getReportEntry = reportEntryResource.getReportEntry(
			postReportEntry.getId());

		assertEquals(postReportEntry, getReportEntry);
		assertValid(getReportEntry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ReportEntry postReportEntry = testGetReportEntry_addReportEntry();

		ReportEntry getReportEntry = reportEntryResource.getReportEntry(
			postReportEntry.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.exportimport.rest.dto.v1_0.ReportEntry"
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

		Object item = vulcanCRUDItemDelegate.getItem(postReportEntry.getId());

		assertEquals(getReportEntry, ReportEntrySerDes.toDTO(item.toString()));
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

	protected ReportEntry testGetReportEntry_addReportEntry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		ReportEntry reportEntry, List<ReportEntry> reportEntries) {

		boolean contains = false;

		for (ReportEntry item : reportEntries) {
			if (equals(reportEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			reportEntries + " does not contain " + reportEntry, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ReportEntry reportEntry1, ReportEntry reportEntry2) {

		Assert.assertTrue(
			reportEntry1 + " does not equal " + reportEntry2,
			equals(reportEntry1, reportEntry2));
	}

	protected void assertEquals(
		List<ReportEntry> reportEntries1, List<ReportEntry> reportEntries2) {

		Assert.assertEquals(reportEntries1.size(), reportEntries2.size());

		for (int i = 0; i < reportEntries1.size(); i++) {
			ReportEntry reportEntry1 = reportEntries1.get(i);
			ReportEntry reportEntry2 = reportEntries2.get(i);

			assertEquals(reportEntry1, reportEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ReportEntry> reportEntries1, List<ReportEntry> reportEntries2) {

		Assert.assertEquals(reportEntries1.size(), reportEntries2.size());

		for (ReportEntry reportEntry1 : reportEntries1) {
			boolean contains = false;

			for (ReportEntry reportEntry2 : reportEntries2) {
				if (equals(reportEntry1, reportEntry2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				reportEntries2 + " does not contain " + reportEntry1, contains);
		}
	}

	protected void assertValid(ReportEntry reportEntry) throws Exception {
		boolean valid = true;

		if (reportEntry.getDateCreated() == null) {
			valid = false;
		}

		if (reportEntry.getDateModified() == null) {
			valid = false;
		}

		if (reportEntry.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"classExternalReferenceCode", additionalAssertFieldName)) {

				if (reportEntry.getClassExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classNameId", additionalAssertFieldName)) {
				if (reportEntry.getClassNameId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (reportEntry.getClassPK() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("configurationId", additionalAssertFieldName)) {
				if (reportEntry.getConfigurationId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (reportEntry.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (reportEntry.getErrorMessage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorStacktrace", additionalAssertFieldName)) {
				if (reportEntry.getErrorStacktrace() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modelName", additionalAssertFieldName)) {
				if (reportEntry.getModelName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("origin", additionalAssertFieldName)) {
				if (reportEntry.getOrigin() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("scope", additionalAssertFieldName)) {
				if (reportEntry.getScope() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (reportEntry.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (reportEntry.getType() == null) {
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

	protected void assertValid(Page<ReportEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ReportEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ReportEntry> reportEntries = page.getItems();

		int size = reportEntries.size();

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
					com.liferay.exportimport.rest.dto.v1_0.ReportEntry.class)) {

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
		ReportEntry reportEntry1, ReportEntry reportEntry2) {

		if (reportEntry1 == reportEntry2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"classExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						reportEntry1.getClassExternalReferenceCode(),
						reportEntry2.getClassExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classNameId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getClassNameId(),
						reportEntry2.getClassNameId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getClassPK(), reportEntry2.getClassPK())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("configurationId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getConfigurationId(),
						reportEntry2.getConfigurationId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getCreator(), reportEntry2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getDateCreated(),
						reportEntry2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getDateModified(),
						reportEntry2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getErrorMessage(),
						reportEntry2.getErrorMessage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorStacktrace", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getErrorStacktrace(),
						reportEntry2.getErrorStacktrace())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getId(), reportEntry2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modelName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getModelName(),
						reportEntry2.getModelName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("origin", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getOrigin(), reportEntry2.getOrigin())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("scope", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getScope(), reportEntry2.getScope())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getStatus(), reportEntry2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						reportEntry1.getType(), reportEntry2.getType())) {

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

		if (!(_reportEntryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_reportEntryResource;

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
		EntityField entityField, String operator, ReportEntry reportEntry) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("classExternalReferenceCode")) {
			Object object = reportEntry.getClassExternalReferenceCode();

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

		if (entityFieldName.equals("classNameId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("classPK")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("configurationId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = reportEntry.getDateCreated();

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

				sb.append(_format.format(reportEntry.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = reportEntry.getDateModified();

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

				sb.append(_format.format(reportEntry.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("errorMessage")) {
			Object object = reportEntry.getErrorMessage();

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

		if (entityFieldName.equals("errorStacktrace")) {
			Object object = reportEntry.getErrorStacktrace();

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

		if (entityFieldName.equals("modelName")) {
			Object object = reportEntry.getModelName();

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

		if (entityFieldName.equals("origin")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("scope")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
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

	protected ReportEntry randomReportEntry() throws Exception {
		return new ReportEntry() {
			{
				classExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				classNameId = RandomTestUtil.randomLong();
				classPK = RandomTestUtil.randomLong();
				configurationId = RandomTestUtil.randomLong();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				errorMessage = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				errorStacktrace = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modelName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ReportEntry randomIrrelevantReportEntry() throws Exception {
		ReportEntry randomIrrelevantReportEntry = randomReportEntry();

		return randomIrrelevantReportEntry;
	}

	protected ReportEntry randomPatchReportEntry() throws Exception {
		return randomReportEntry();
	}

	protected ReportEntryResource reportEntryResource;
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
		LogFactoryUtil.getLog(BaseReportEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.exportimport.rest.resource.v1_0.ReportEntryResource
		_reportEntryResource;

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