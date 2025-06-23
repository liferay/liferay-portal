/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowLog;
import com.liferay.headless.admin.workflow.client.http.HttpInvoker;
import com.liferay.headless.admin.workflow.client.pagination.Page;
import com.liferay.headless.admin.workflow.client.pagination.Pagination;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowLogResource;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowLogSerDes;
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
public abstract class BaseWorkflowLogResourceTestCase {

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

		_workflowLogResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		workflowLogResource = WorkflowLogResource.builder(
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

		WorkflowLog workflowLog1 = randomWorkflowLog();

		String json = objectMapper.writeValueAsString(workflowLog1);

		WorkflowLog workflowLog2 = WorkflowLogSerDes.toDTO(json);

		Assert.assertTrue(equals(workflowLog1, workflowLog2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WorkflowLog workflowLog = randomWorkflowLog();

		String json1 = objectMapper.writeValueAsString(workflowLog);
		String json2 = WorkflowLogSerDes.toJSON(workflowLog);

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

		WorkflowLog workflowLog = randomWorkflowLog();

		workflowLog.setCommentLog(regex);
		workflowLog.setDescription(regex);
		workflowLog.setPreviousState(regex);
		workflowLog.setPreviousStateLabel(regex);
		workflowLog.setState(regex);
		workflowLog.setStateLabel(regex);

		String json = WorkflowLogSerDes.toJSON(workflowLog);

		Assert.assertFalse(json.contains(regex));

		workflowLog = WorkflowLogSerDes.toDTO(json);

		Assert.assertEquals(regex, workflowLog.getCommentLog());
		Assert.assertEquals(regex, workflowLog.getDescription());
		Assert.assertEquals(regex, workflowLog.getPreviousState());
		Assert.assertEquals(regex, workflowLog.getPreviousStateLabel());
		Assert.assertEquals(regex, workflowLog.getState());
		Assert.assertEquals(regex, workflowLog.getStateLabel());
	}

	@Test
	public void testGetWorkflowInstanceWorkflowLogsPage() throws Exception {
		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowLogsPage_getWorkflowInstanceId();
		Long irrelevantWorkflowInstanceId =
			testGetWorkflowInstanceWorkflowLogsPage_getIrrelevantWorkflowInstanceId();

		Page<WorkflowLog> page =
			workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				workflowInstanceId, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantWorkflowInstanceId != null) {
			WorkflowLog irrelevantWorkflowLog =
				testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
					irrelevantWorkflowInstanceId,
					randomIrrelevantWorkflowLog());

			page = workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				irrelevantWorkflowInstanceId, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWorkflowLog, (List<WorkflowLog>)page.getItems());
			assertValid(
				page,
				testGetWorkflowInstanceWorkflowLogsPage_getExpectedActions(
					irrelevantWorkflowInstanceId));
		}

		WorkflowLog workflowLog1 =
			testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
				workflowInstanceId, randomWorkflowLog());

		WorkflowLog workflowLog2 =
			testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
				workflowInstanceId, randomWorkflowLog());

		page = workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
			workflowInstanceId, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowLog1, (List<WorkflowLog>)page.getItems());
		assertContains(workflowLog2, (List<WorkflowLog>)page.getItems());
		assertValid(
			page,
			testGetWorkflowInstanceWorkflowLogsPage_getExpectedActions(
				workflowInstanceId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowInstanceWorkflowLogsPage_getExpectedActions(
				Long workflowInstanceId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowInstanceWorkflowLogsPageWithPagination()
		throws Exception {

		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowLogsPage_getWorkflowInstanceId();

		Page<WorkflowLog> workflowLogsPage =
			workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				workflowInstanceId, null, null);

		int totalCount = GetterUtil.getInteger(
			workflowLogsPage.getTotalCount());

		WorkflowLog workflowLog1 =
			testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
				workflowInstanceId, randomWorkflowLog());

		WorkflowLog workflowLog2 =
			testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
				workflowInstanceId, randomWorkflowLog());

		WorkflowLog workflowLog3 =
			testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
				workflowInstanceId, randomWorkflowLog());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowLog> page1 =
				workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
					workflowInstanceId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowLog1, (List<WorkflowLog>)page1.getItems());

			Page<WorkflowLog> page2 =
				workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
					workflowInstanceId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowLog2, (List<WorkflowLog>)page2.getItems());

			Page<WorkflowLog> page3 =
				workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
					workflowInstanceId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowLog3, (List<WorkflowLog>)page3.getItems());
		}
		else {
			Page<WorkflowLog> page1 =
				workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
					workflowInstanceId, null, Pagination.of(1, totalCount + 2));

			List<WorkflowLog> workflowLogs1 =
				(List<WorkflowLog>)page1.getItems();

			Assert.assertEquals(
				workflowLogs1.toString(), totalCount + 2, workflowLogs1.size());

			Page<WorkflowLog> page2 =
				workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
					workflowInstanceId, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowLog> workflowLogs2 =
				(List<WorkflowLog>)page2.getItems();

			Assert.assertEquals(
				workflowLogs2.toString(), 1, workflowLogs2.size());

			Page<WorkflowLog> page3 =
				workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
					workflowInstanceId, null,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowLog1, (List<WorkflowLog>)page3.getItems());
			assertContains(workflowLog2, (List<WorkflowLog>)page3.getItems());
			assertContains(workflowLog3, (List<WorkflowLog>)page3.getItems());
		}
	}

	protected WorkflowLog
			testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
				Long workflowInstanceId, WorkflowLog workflowLog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowLogsPage_getWorkflowInstanceId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowLogsPage_getIrrelevantWorkflowInstanceId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkflowLog() throws Exception {
		WorkflowLog postWorkflowLog = testGetWorkflowLog_addWorkflowLog();

		WorkflowLog getWorkflowLog = workflowLogResource.getWorkflowLog(
			postWorkflowLog.getId());

		assertEquals(postWorkflowLog, getWorkflowLog);
		assertValid(getWorkflowLog);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WorkflowLog postWorkflowLog = testGetWorkflowLog_addWorkflowLog();

		WorkflowLog getWorkflowLog = workflowLogResource.getWorkflowLog(
			postWorkflowLog.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.workflow.dto.v1_0.WorkflowLog"
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

		Object item = vulcanCRUDItemDelegate.getItem(postWorkflowLog.getId());

		assertEquals(getWorkflowLog, WorkflowLogSerDes.toDTO(item.toString()));
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

	protected WorkflowLog testGetWorkflowLog_addWorkflowLog() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkflowLog() throws Exception {
		WorkflowLog workflowLog = testGraphQLGetWorkflowLog_addWorkflowLog();

		// No namespace

		Assert.assertTrue(
			equals(
				workflowLog,
				WorkflowLogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"workflowLog",
								new HashMap<String, Object>() {
									{
										put(
											"workflowLogId",
											workflowLog.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/workflowLog"))));

		// Using the namespace headlessAdminWorkflow_v1_0

		Assert.assertTrue(
			equals(
				workflowLog,
				WorkflowLogSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminWorkflow_v1_0",
								new GraphQLField(
									"workflowLog",
									new HashMap<String, Object>() {
										{
											put(
												"workflowLogId",
												workflowLog.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminWorkflow_v1_0",
						"Object/workflowLog"))));
	}

	@Test
	public void testGraphQLGetWorkflowLogNotFound() throws Exception {
		Long irrelevantWorkflowLogId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"workflowLog",
						new HashMap<String, Object>() {
							{
								put("workflowLogId", irrelevantWorkflowLogId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminWorkflow_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminWorkflow_v1_0",
						new GraphQLField(
							"workflowLog",
							new HashMap<String, Object>() {
								{
									put(
										"workflowLogId",
										irrelevantWorkflowLogId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WorkflowLog testGraphQLGetWorkflowLog_addWorkflowLog()
		throws Exception {

		return testGraphQLWorkflowLog_addWorkflowLog();
	}

	@Test
	public void testGetWorkflowTaskWorkflowLogsPage() throws Exception {
		Long workflowTaskId =
			testGetWorkflowTaskWorkflowLogsPage_getWorkflowTaskId();
		Long irrelevantWorkflowTaskId =
			testGetWorkflowTaskWorkflowLogsPage_getIrrelevantWorkflowTaskId();

		Page<WorkflowLog> page =
			workflowLogResource.getWorkflowTaskWorkflowLogsPage(
				workflowTaskId, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantWorkflowTaskId != null) {
			WorkflowLog irrelevantWorkflowLog =
				testGetWorkflowTaskWorkflowLogsPage_addWorkflowLog(
					irrelevantWorkflowTaskId, randomIrrelevantWorkflowLog());

			page = workflowLogResource.getWorkflowTaskWorkflowLogsPage(
				irrelevantWorkflowTaskId, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWorkflowLog, (List<WorkflowLog>)page.getItems());
			assertValid(
				page,
				testGetWorkflowTaskWorkflowLogsPage_getExpectedActions(
					irrelevantWorkflowTaskId));
		}

		WorkflowLog workflowLog1 =
			testGetWorkflowTaskWorkflowLogsPage_addWorkflowLog(
				workflowTaskId, randomWorkflowLog());

		WorkflowLog workflowLog2 =
			testGetWorkflowTaskWorkflowLogsPage_addWorkflowLog(
				workflowTaskId, randomWorkflowLog());

		page = workflowLogResource.getWorkflowTaskWorkflowLogsPage(
			workflowTaskId, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowLog1, (List<WorkflowLog>)page.getItems());
		assertContains(workflowLog2, (List<WorkflowLog>)page.getItems());
		assertValid(
			page,
			testGetWorkflowTaskWorkflowLogsPage_getExpectedActions(
				workflowTaskId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowTaskWorkflowLogsPage_getExpectedActions(
				Long workflowTaskId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowTaskWorkflowLogsPageWithPagination()
		throws Exception {

		Long workflowTaskId =
			testGetWorkflowTaskWorkflowLogsPage_getWorkflowTaskId();

		Page<WorkflowLog> workflowLogsPage =
			workflowLogResource.getWorkflowTaskWorkflowLogsPage(
				workflowTaskId, null, null);

		int totalCount = GetterUtil.getInteger(
			workflowLogsPage.getTotalCount());

		WorkflowLog workflowLog1 =
			testGetWorkflowTaskWorkflowLogsPage_addWorkflowLog(
				workflowTaskId, randomWorkflowLog());

		WorkflowLog workflowLog2 =
			testGetWorkflowTaskWorkflowLogsPage_addWorkflowLog(
				workflowTaskId, randomWorkflowLog());

		WorkflowLog workflowLog3 =
			testGetWorkflowTaskWorkflowLogsPage_addWorkflowLog(
				workflowTaskId, randomWorkflowLog());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowLog> page1 =
				workflowLogResource.getWorkflowTaskWorkflowLogsPage(
					workflowTaskId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowLog1, (List<WorkflowLog>)page1.getItems());

			Page<WorkflowLog> page2 =
				workflowLogResource.getWorkflowTaskWorkflowLogsPage(
					workflowTaskId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowLog2, (List<WorkflowLog>)page2.getItems());

			Page<WorkflowLog> page3 =
				workflowLogResource.getWorkflowTaskWorkflowLogsPage(
					workflowTaskId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowLog3, (List<WorkflowLog>)page3.getItems());
		}
		else {
			Page<WorkflowLog> page1 =
				workflowLogResource.getWorkflowTaskWorkflowLogsPage(
					workflowTaskId, null, Pagination.of(1, totalCount + 2));

			List<WorkflowLog> workflowLogs1 =
				(List<WorkflowLog>)page1.getItems();

			Assert.assertEquals(
				workflowLogs1.toString(), totalCount + 2, workflowLogs1.size());

			Page<WorkflowLog> page2 =
				workflowLogResource.getWorkflowTaskWorkflowLogsPage(
					workflowTaskId, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowLog> workflowLogs2 =
				(List<WorkflowLog>)page2.getItems();

			Assert.assertEquals(
				workflowLogs2.toString(), 1, workflowLogs2.size());

			Page<WorkflowLog> page3 =
				workflowLogResource.getWorkflowTaskWorkflowLogsPage(
					workflowTaskId, null,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowLog1, (List<WorkflowLog>)page3.getItems());
			assertContains(workflowLog2, (List<WorkflowLog>)page3.getItems());
			assertContains(workflowLog3, (List<WorkflowLog>)page3.getItems());
		}
	}

	protected WorkflowLog testGetWorkflowTaskWorkflowLogsPage_addWorkflowLog(
			Long workflowTaskId, WorkflowLog workflowLog)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkflowTaskWorkflowLogsPage_getWorkflowTaskId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowTaskWorkflowLogsPage_getIrrelevantWorkflowTaskId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected WorkflowLog testGraphQLWorkflowLog_addWorkflowLog()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		WorkflowLog workflowLog, List<WorkflowLog> workflowLogs) {

		boolean contains = false;

		for (WorkflowLog item : workflowLogs) {
			if (equals(workflowLog, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			workflowLogs + " does not contain " + workflowLog, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WorkflowLog workflowLog1, WorkflowLog workflowLog2) {

		Assert.assertTrue(
			workflowLog1 + " does not equal " + workflowLog2,
			equals(workflowLog1, workflowLog2));
	}

	protected void assertEquals(
		List<WorkflowLog> workflowLogs1, List<WorkflowLog> workflowLogs2) {

		Assert.assertEquals(workflowLogs1.size(), workflowLogs2.size());

		for (int i = 0; i < workflowLogs1.size(); i++) {
			WorkflowLog workflowLog1 = workflowLogs1.get(i);
			WorkflowLog workflowLog2 = workflowLogs2.get(i);

			assertEquals(workflowLog1, workflowLog2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WorkflowLog> workflowLogs1, List<WorkflowLog> workflowLogs2) {

		Assert.assertEquals(workflowLogs1.size(), workflowLogs2.size());

		for (WorkflowLog workflowLog1 : workflowLogs1) {
			boolean contains = false;

			for (WorkflowLog workflowLog2 : workflowLogs2) {
				if (equals(workflowLog1, workflowLog2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				workflowLogs2 + " does not contain " + workflowLog1, contains);
		}
	}

	protected void assertValid(WorkflowLog workflowLog) throws Exception {
		boolean valid = true;

		if (workflowLog.getDateCreated() == null) {
			valid = false;
		}

		if (workflowLog.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("auditPerson", additionalAssertFieldName)) {
				if (workflowLog.getAuditPerson() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("commentLog", additionalAssertFieldName)) {
				if (workflowLog.getCommentLog() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (workflowLog.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("person", additionalAssertFieldName)) {
				if (workflowLog.getPerson() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("previousPerson", additionalAssertFieldName)) {
				if (workflowLog.getPreviousPerson() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("previousRole", additionalAssertFieldName)) {
				if (workflowLog.getPreviousRole() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("previousState", additionalAssertFieldName)) {
				if (workflowLog.getPreviousState() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"previousStateLabel", additionalAssertFieldName)) {

				if (workflowLog.getPreviousStateLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("role", additionalAssertFieldName)) {
				if (workflowLog.getRole() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("state", additionalAssertFieldName)) {
				if (workflowLog.getState() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("stateLabel", additionalAssertFieldName)) {
				if (workflowLog.getStateLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (workflowLog.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("workflowTaskId", additionalAssertFieldName)) {
				if (workflowLog.getWorkflowTaskId() == null) {
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

	protected void assertValid(Page<WorkflowLog> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WorkflowLog> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WorkflowLog> workflowLogs = page.getItems();

		int size = workflowLogs.size();

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
					com.liferay.headless.admin.workflow.dto.v1_0.WorkflowLog.
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

	protected boolean equals(
		WorkflowLog workflowLog1, WorkflowLog workflowLog2) {

		if (workflowLog1 == workflowLog2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("auditPerson", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getAuditPerson(),
						workflowLog2.getAuditPerson())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("commentLog", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getCommentLog(),
						workflowLog2.getCommentLog())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getDateCreated(),
						workflowLog2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getDescription(),
						workflowLog2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getId(), workflowLog2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("person", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getPerson(), workflowLog2.getPerson())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("previousPerson", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getPreviousPerson(),
						workflowLog2.getPreviousPerson())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("previousRole", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getPreviousRole(),
						workflowLog2.getPreviousRole())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("previousState", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getPreviousState(),
						workflowLog2.getPreviousState())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"previousStateLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowLog1.getPreviousStateLabel(),
						workflowLog2.getPreviousStateLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("role", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getRole(), workflowLog2.getRole())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("state", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getState(), workflowLog2.getState())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("stateLabel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getStateLabel(),
						workflowLog2.getStateLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getType(), workflowLog2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("workflowTaskId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowLog1.getWorkflowTaskId(),
						workflowLog2.getWorkflowTaskId())) {

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

		if (!(_workflowLogResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_workflowLogResource;

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
		EntityField entityField, String operator, WorkflowLog workflowLog) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("auditPerson")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("commentLog")) {
			Object object = workflowLog.getCommentLog();

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = workflowLog.getDateCreated();

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

				sb.append(_format.format(workflowLog.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = workflowLog.getDescription();

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

		if (entityFieldName.equals("person")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("previousPerson")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("previousRole")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("previousState")) {
			Object object = workflowLog.getPreviousState();

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

		if (entityFieldName.equals("previousStateLabel")) {
			Object object = workflowLog.getPreviousStateLabel();

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

		if (entityFieldName.equals("role")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("state")) {
			Object object = workflowLog.getState();

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

		if (entityFieldName.equals("stateLabel")) {
			Object object = workflowLog.getStateLabel();

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

		if (entityFieldName.equals("type")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("workflowTaskId")) {
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

	protected WorkflowLog randomWorkflowLog() throws Exception {
		return new WorkflowLog() {
			{
				commentLog = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				previousState = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				previousStateLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				state = StringUtil.toLowerCase(RandomTestUtil.randomString());
				stateLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				workflowTaskId = RandomTestUtil.randomLong();
			}
		};
	}

	protected WorkflowLog randomIrrelevantWorkflowLog() throws Exception {
		WorkflowLog randomIrrelevantWorkflowLog = randomWorkflowLog();

		return randomIrrelevantWorkflowLog;
	}

	protected WorkflowLog randomPatchWorkflowLog() throws Exception {
		return randomWorkflowLog();
	}

	protected WorkflowLogResource workflowLogResource;
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
		LogFactoryUtil.getLog(BaseWorkflowLogResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.workflow.resource.v1_0.WorkflowLogResource
			_workflowLogResource;

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