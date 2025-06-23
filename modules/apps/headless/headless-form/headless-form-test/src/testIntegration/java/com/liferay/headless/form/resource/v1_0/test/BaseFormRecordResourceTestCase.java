/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.form.client.dto.v1_0.FormRecord;
import com.liferay.headless.form.client.http.HttpInvoker;
import com.liferay.headless.form.client.pagination.Page;
import com.liferay.headless.form.client.pagination.Pagination;
import com.liferay.headless.form.client.resource.v1_0.FormRecordResource;
import com.liferay.headless.form.client.serdes.v1_0.FormRecordSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseFormRecordResourceTestCase {

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

		_formRecordResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		formRecordResource = FormRecordResource.builder(
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

		FormRecord formRecord1 = randomFormRecord();

		String json = objectMapper.writeValueAsString(formRecord1);

		FormRecord formRecord2 = FormRecordSerDes.toDTO(json);

		Assert.assertTrue(equals(formRecord1, formRecord2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		FormRecord formRecord = randomFormRecord();

		String json1 = objectMapper.writeValueAsString(formRecord);
		String json2 = FormRecordSerDes.toJSON(formRecord);

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

		FormRecord formRecord = randomFormRecord();

		String json = FormRecordSerDes.toJSON(formRecord);

		Assert.assertFalse(json.contains(regex));

		formRecord = FormRecordSerDes.toDTO(json);
	}

	@Test
	public void testGetFormFormRecordByLatestDraft() throws Exception {
		FormRecord postFormRecord =
			testGetFormFormRecordByLatestDraft_addFormRecord();

		FormRecord getFormRecord =
			formRecordResource.getFormFormRecordByLatestDraft(
				testGetFormFormRecordByLatestDraft_getFormId(postFormRecord));

		assertEquals(postFormRecord, getFormRecord);
		assertValid(getFormRecord);
	}

	protected FormRecord testGetFormFormRecordByLatestDraft_addFormRecord()
		throws Exception {

		return testPostFormFormRecord_addFormRecord(randomFormRecord());
	}

	protected Long testGetFormFormRecordByLatestDraft_getFormId(
			FormRecord formRecord)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetFormFormRecordByLatestDraft() throws Exception {
		FormRecord formRecord =
			testGraphQLGetFormFormRecordByLatestDraft_addFormRecord();

		// No namespace

		Assert.assertTrue(
			equals(
				formRecord,
				FormRecordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"formFormRecordByLatestDraft",
								new HashMap<String, Object>() {
									{
										put(
											"formId",
											testGraphQLGetFormFormRecordByLatestDraft_getFormId(
												formRecord));
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/formFormRecordByLatestDraft"))));

		// Using the namespace headlessForm_v1_0

		Assert.assertTrue(
			equals(
				formRecord,
				FormRecordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessForm_v1_0",
								new GraphQLField(
									"formFormRecordByLatestDraft",
									new HashMap<String, Object>() {
										{
											put(
												"formId",
												testGraphQLGetFormFormRecordByLatestDraft_getFormId(
													formRecord));
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessForm_v1_0",
						"Object/formFormRecordByLatestDraft"))));
	}

	protected Long testGraphQLGetFormFormRecordByLatestDraft_getFormId(
			FormRecord formRecord)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetFormFormRecordByLatestDraftNotFound()
		throws Exception {

		Long irrelevantFormId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"formFormRecordByLatestDraft",
						new HashMap<String, Object>() {
							{
								put("formId", irrelevantFormId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessForm_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessForm_v1_0",
						new GraphQLField(
							"formFormRecordByLatestDraft",
							new HashMap<String, Object>() {
								{
									put("formId", irrelevantFormId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected FormRecord
			testGraphQLGetFormFormRecordByLatestDraft_addFormRecord()
		throws Exception {

		return testGraphQLFormRecord_addFormRecord();
	}

	@Test
	public void testGetFormFormRecordsPage() throws Exception {
		Long formId = testGetFormFormRecordsPage_getFormId();
		Long irrelevantFormId =
			testGetFormFormRecordsPage_getIrrelevantFormId();

		Page<FormRecord> page = formRecordResource.getFormFormRecordsPage(
			formId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantFormId != null) {
			FormRecord irrelevantFormRecord =
				testGetFormFormRecordsPage_addFormRecord(
					irrelevantFormId, randomIrrelevantFormRecord());

			page = formRecordResource.getFormFormRecordsPage(
				irrelevantFormId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantFormRecord, (List<FormRecord>)page.getItems());
			assertValid(
				page,
				testGetFormFormRecordsPage_getExpectedActions(
					irrelevantFormId));
		}

		FormRecord formRecord1 = testGetFormFormRecordsPage_addFormRecord(
			formId, randomFormRecord());

		FormRecord formRecord2 = testGetFormFormRecordsPage_addFormRecord(
			formId, randomFormRecord());

		page = formRecordResource.getFormFormRecordsPage(
			formId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(formRecord1, (List<FormRecord>)page.getItems());
		assertContains(formRecord2, (List<FormRecord>)page.getItems());
		assertValid(
			page, testGetFormFormRecordsPage_getExpectedActions(formId));
	}

	protected Map<String, Map<String, String>>
			testGetFormFormRecordsPage_getExpectedActions(Long formId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-form/v1.0/forms/{formId}/form-records/batch".
				replace("{formId}", String.valueOf(formId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetFormFormRecordsPageWithPagination() throws Exception {
		Long formId = testGetFormFormRecordsPage_getFormId();

		Page<FormRecord> formRecordsPage =
			formRecordResource.getFormFormRecordsPage(formId, null);

		int totalCount = GetterUtil.getInteger(formRecordsPage.getTotalCount());

		FormRecord formRecord1 = testGetFormFormRecordsPage_addFormRecord(
			formId, randomFormRecord());

		FormRecord formRecord2 = testGetFormFormRecordsPage_addFormRecord(
			formId, randomFormRecord());

		FormRecord formRecord3 = testGetFormFormRecordsPage_addFormRecord(
			formId, randomFormRecord());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<FormRecord> page1 = formRecordResource.getFormFormRecordsPage(
				formId,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(formRecord1, (List<FormRecord>)page1.getItems());

			Page<FormRecord> page2 = formRecordResource.getFormFormRecordsPage(
				formId,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(formRecord2, (List<FormRecord>)page2.getItems());

			Page<FormRecord> page3 = formRecordResource.getFormFormRecordsPage(
				formId,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(formRecord3, (List<FormRecord>)page3.getItems());
		}
		else {
			Page<FormRecord> page1 = formRecordResource.getFormFormRecordsPage(
				formId, Pagination.of(1, totalCount + 2));

			List<FormRecord> formRecords1 = (List<FormRecord>)page1.getItems();

			Assert.assertEquals(
				formRecords1.toString(), totalCount + 2, formRecords1.size());

			Page<FormRecord> page2 = formRecordResource.getFormFormRecordsPage(
				formId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<FormRecord> formRecords2 = (List<FormRecord>)page2.getItems();

			Assert.assertEquals(
				formRecords2.toString(), 1, formRecords2.size());

			Page<FormRecord> page3 = formRecordResource.getFormFormRecordsPage(
				formId, Pagination.of(1, (int)totalCount + 3));

			assertContains(formRecord1, (List<FormRecord>)page3.getItems());
			assertContains(formRecord2, (List<FormRecord>)page3.getItems());
			assertContains(formRecord3, (List<FormRecord>)page3.getItems());
		}
	}

	protected FormRecord testGetFormFormRecordsPage_addFormRecord(
			Long formId, FormRecord formRecord)
		throws Exception {

		return formRecordResource.postFormFormRecord(formId, formRecord);
	}

	protected Long testGetFormFormRecordsPage_getFormId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetFormFormRecordsPage_getIrrelevantFormId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetFormRecord() throws Exception {
		FormRecord postFormRecord = testGetFormRecord_addFormRecord();

		FormRecord getFormRecord = formRecordResource.getFormRecord(
			postFormRecord.getId());

		assertEquals(postFormRecord, getFormRecord);
		assertValid(getFormRecord);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		FormRecord postFormRecord = testGetFormRecord_addFormRecord();

		FormRecord getFormRecord = formRecordResource.getFormRecord(
			postFormRecord.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.form.dto.v1_0.FormRecord"
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

		Object item = vulcanCRUDItemDelegate.getItem(postFormRecord.getId());

		assertEquals(getFormRecord, FormRecordSerDes.toDTO(item.toString()));
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

	protected FormRecord testGetFormRecord_addFormRecord() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetFormRecord() throws Exception {
		FormRecord formRecord = testGraphQLGetFormRecord_addFormRecord();

		// No namespace

		Assert.assertTrue(
			equals(
				formRecord,
				FormRecordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"formRecord",
								new HashMap<String, Object>() {
									{
										put("formRecordId", formRecord.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/formRecord"))));

		// Using the namespace headlessForm_v1_0

		Assert.assertTrue(
			equals(
				formRecord,
				FormRecordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessForm_v1_0",
								new GraphQLField(
									"formRecord",
									new HashMap<String, Object>() {
										{
											put(
												"formRecordId",
												formRecord.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessForm_v1_0",
						"Object/formRecord"))));
	}

	@Test
	public void testGraphQLGetFormRecordNotFound() throws Exception {
		Long irrelevantFormRecordId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"formRecord",
						new HashMap<String, Object>() {
							{
								put("formRecordId", irrelevantFormRecordId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessForm_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessForm_v1_0",
						new GraphQLField(
							"formRecord",
							new HashMap<String, Object>() {
								{
									put("formRecordId", irrelevantFormRecordId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected FormRecord testGraphQLGetFormRecord_addFormRecord()
		throws Exception {

		return testGraphQLFormRecord_addFormRecord();
	}

	@Test
	public void testPostFormFormRecord() throws Exception {
		FormRecord randomFormRecord = randomFormRecord();

		FormRecord postFormRecord = testPostFormFormRecord_addFormRecord(
			randomFormRecord);

		assertEquals(randomFormRecord, postFormRecord);
		assertValid(postFormRecord);
	}

	protected FormRecord testPostFormFormRecord_addFormRecord(
			FormRecord formRecord)
		throws Exception {

		return formRecordResource.postFormFormRecord(
			testGetFormFormRecordsPage_getFormId(), formRecord);
	}

	@Test
	public void testPutFormRecord() throws Exception {
		FormRecord postFormRecord = testPutFormRecord_addFormRecord();

		FormRecord randomFormRecord = randomFormRecord();

		FormRecord putFormRecord = formRecordResource.putFormRecord(
			postFormRecord.getId(), randomFormRecord);

		assertEquals(randomFormRecord, putFormRecord);
		assertValid(putFormRecord);

		FormRecord getFormRecord = formRecordResource.getFormRecord(
			putFormRecord.getId());

		assertEquals(randomFormRecord, getFormRecord);
		assertValid(getFormRecord);
	}

	protected FormRecord testPutFormRecord_addFormRecord() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected FormRecord testGraphQLFormRecord_addFormRecord()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		FormRecord formRecord, List<FormRecord> formRecords) {

		boolean contains = false;

		for (FormRecord item : formRecords) {
			if (equals(formRecord, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			formRecords + " does not contain " + formRecord, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		FormRecord formRecord1, FormRecord formRecord2) {

		Assert.assertTrue(
			formRecord1 + " does not equal " + formRecord2,
			equals(formRecord1, formRecord2));
	}

	protected void assertEquals(
		List<FormRecord> formRecords1, List<FormRecord> formRecords2) {

		Assert.assertEquals(formRecords1.size(), formRecords2.size());

		for (int i = 0; i < formRecords1.size(); i++) {
			FormRecord formRecord1 = formRecords1.get(i);
			FormRecord formRecord2 = formRecords2.get(i);

			assertEquals(formRecord1, formRecord2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<FormRecord> formRecords1, List<FormRecord> formRecords2) {

		Assert.assertEquals(formRecords1.size(), formRecords2.size());

		for (FormRecord formRecord1 : formRecords1) {
			boolean contains = false;

			for (FormRecord formRecord2 : formRecords2) {
				if (equals(formRecord1, formRecord2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				formRecords2 + " does not contain " + formRecord1, contains);
		}
	}

	protected void assertValid(FormRecord formRecord) throws Exception {
		boolean valid = true;

		if (formRecord.getDateCreated() == null) {
			valid = false;
		}

		if (formRecord.getDateModified() == null) {
			valid = false;
		}

		if (formRecord.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (formRecord.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (formRecord.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("draft", additionalAssertFieldName)) {
				if (formRecord.getDraft() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("formFieldValues", additionalAssertFieldName)) {
				if (formRecord.getFormFieldValues() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("formId", additionalAssertFieldName)) {
				if (formRecord.getFormId() == null) {
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

	protected void assertValid(Page<FormRecord> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<FormRecord> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<FormRecord> formRecords = page.getItems();

		int size = formRecords.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.form.dto.v1_0.FormRecord.class)) {

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

	protected boolean equals(FormRecord formRecord1, FormRecord formRecord2) {
		if (formRecord1 == formRecord2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getCreator(), formRecord2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getDateCreated(),
						formRecord2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getDateModified(),
						formRecord2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getDatePublished(),
						formRecord2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("draft", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getDraft(), formRecord2.getDraft())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("formFieldValues", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getFormFieldValues(),
						formRecord2.getFormFieldValues())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("formId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getFormId(), formRecord2.getFormId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						formRecord1.getId(), formRecord2.getId())) {

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

		if (!(_formRecordResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_formRecordResource;

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
		EntityField entityField, String operator, FormRecord formRecord) {

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
				Date date = formRecord.getDateCreated();

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

				sb.append(_format.format(formRecord.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = formRecord.getDateModified();

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

				sb.append(_format.format(formRecord.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = formRecord.getDatePublished();

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

				sb.append(_format.format(formRecord.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("draft")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("formFieldValues")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("formId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
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

	protected FormRecord randomFormRecord() throws Exception {
		return new FormRecord() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				draft = RandomTestUtil.randomBoolean();
				formId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
			}
		};
	}

	protected FormRecord randomIrrelevantFormRecord() throws Exception {
		FormRecord randomIrrelevantFormRecord = randomFormRecord();

		return randomIrrelevantFormRecord;
	}

	protected FormRecord randomPatchFormRecord() throws Exception {
		return randomFormRecord();
	}

	protected FormRecordResource formRecordResource;
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
		LogFactoryUtil.getLog(BaseFormRecordResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.form.resource.v1_0.FormRecordResource
		_formRecordResource;

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