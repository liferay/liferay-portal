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

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.client.http.HttpInvoker;
import com.liferay.headless.admin.workflow.client.pagination.Page;
import com.liferay.headless.admin.workflow.client.pagination.Pagination;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowTaskResource;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTaskSerDes;
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
public abstract class BaseWorkflowTaskResourceTestCase {

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

		_workflowTaskResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		workflowTaskResource = WorkflowTaskResource.builder(
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

		WorkflowTask workflowTask1 = randomWorkflowTask();

		String json = objectMapper.writeValueAsString(workflowTask1);

		WorkflowTask workflowTask2 = WorkflowTaskSerDes.toDTO(json);

		Assert.assertTrue(equals(workflowTask1, workflowTask2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WorkflowTask workflowTask = randomWorkflowTask();

		String json1 = objectMapper.writeValueAsString(workflowTask);
		String json2 = WorkflowTaskSerDes.toJSON(workflowTask);

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

		WorkflowTask workflowTask = randomWorkflowTask();

		workflowTask.setDescription(regex);
		workflowTask.setLabel(regex);
		workflowTask.setName(regex);
		workflowTask.setWorkflowDefinitionName(regex);
		workflowTask.setWorkflowDefinitionVersion(regex);

		String json = WorkflowTaskSerDes.toJSON(workflowTask);

		Assert.assertFalse(json.contains(regex));

		workflowTask = WorkflowTaskSerDes.toDTO(json);

		Assert.assertEquals(regex, workflowTask.getDescription());
		Assert.assertEquals(regex, workflowTask.getLabel());
		Assert.assertEquals(regex, workflowTask.getName());
		Assert.assertEquals(regex, workflowTask.getWorkflowDefinitionName());
		Assert.assertEquals(regex, workflowTask.getWorkflowDefinitionVersion());
	}

	@Test
	public void testGetWorkflowInstanceWorkflowTasksAssignedToMePage()
		throws Exception {

		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getWorkflowInstanceId();
		Long irrelevantWorkflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getIrrelevantWorkflowInstanceId();

		Page<WorkflowTask> page =
			workflowTaskResource.
				getWorkflowInstanceWorkflowTasksAssignedToMePage(
					workflowInstanceId, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantWorkflowInstanceId != null) {
			WorkflowTask irrelevantWorkflowTask =
				testGetWorkflowInstanceWorkflowTasksAssignedToMePage_addWorkflowTask(
					irrelevantWorkflowInstanceId,
					randomIrrelevantWorkflowTask());

			page =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						irrelevantWorkflowInstanceId, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWorkflowTask, (List<WorkflowTask>)page.getItems());
			assertValid(
				page,
				testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getExpectedActions(
					irrelevantWorkflowInstanceId));
		}

		WorkflowTask workflowTask1 =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		page =
			workflowTaskResource.
				getWorkflowInstanceWorkflowTasksAssignedToMePage(
					workflowInstanceId, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page,
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getExpectedActions(
				workflowInstanceId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getExpectedActions(
				Long workflowInstanceId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowInstanceWorkflowTasksAssignedToMePageWithPagination()
		throws Exception {

		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getWorkflowInstanceId();

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.
				getWorkflowInstanceWorkflowTasksAssignedToMePage(
					workflowInstanceId, null, null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						workflowInstanceId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						workflowInstanceId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						workflowInstanceId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						workflowInstanceId, null,
						Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						workflowInstanceId, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						workflowInstanceId, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_addWorkflowTask(
				Long workflowInstanceId, WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getWorkflowInstanceId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowTasksAssignedToMePage_getIrrelevantWorkflowInstanceId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkflowInstanceWorkflowTasksAssignedToUserPage()
		throws Exception {

		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getWorkflowInstanceId();
		Long irrelevantWorkflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getIrrelevantWorkflowInstanceId();

		Page<WorkflowTask> page =
			workflowTaskResource.
				getWorkflowInstanceWorkflowTasksAssignedToUserPage(
					workflowInstanceId, null, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantWorkflowInstanceId != null) {
			WorkflowTask irrelevantWorkflowTask =
				testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_addWorkflowTask(
					irrelevantWorkflowInstanceId,
					randomIrrelevantWorkflowTask());

			page =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						irrelevantWorkflowInstanceId, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWorkflowTask, (List<WorkflowTask>)page.getItems());
			assertValid(
				page,
				testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getExpectedActions(
					irrelevantWorkflowInstanceId));
		}

		WorkflowTask workflowTask1 =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		page =
			workflowTaskResource.
				getWorkflowInstanceWorkflowTasksAssignedToUserPage(
					workflowInstanceId, null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page,
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getExpectedActions(
				workflowInstanceId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getExpectedActions(
				Long workflowInstanceId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowInstanceWorkflowTasksAssignedToUserPageWithPagination()
		throws Exception {

		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getWorkflowInstanceId();

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.
				getWorkflowInstanceWorkflowTasksAssignedToUserPage(
					workflowInstanceId, null, null, null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						workflowInstanceId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						workflowInstanceId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						workflowInstanceId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						workflowInstanceId, null, null,
						Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						workflowInstanceId, null, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						workflowInstanceId, null, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_addWorkflowTask(
				Long workflowInstanceId, WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getWorkflowInstanceId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowTasksAssignedToUserPage_getIrrelevantWorkflowInstanceId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkflowInstanceWorkflowTasksPage() throws Exception {
		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksPage_getWorkflowInstanceId();
		Long irrelevantWorkflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksPage_getIrrelevantWorkflowInstanceId();

		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
				workflowInstanceId, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantWorkflowInstanceId != null) {
			WorkflowTask irrelevantWorkflowTask =
				testGetWorkflowInstanceWorkflowTasksPage_addWorkflowTask(
					irrelevantWorkflowInstanceId,
					randomIrrelevantWorkflowTask());

			page = workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
				irrelevantWorkflowInstanceId, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWorkflowTask, (List<WorkflowTask>)page.getItems());
			assertValid(
				page,
				testGetWorkflowInstanceWorkflowTasksPage_getExpectedActions(
					irrelevantWorkflowInstanceId));
		}

		WorkflowTask workflowTask1 =
			testGetWorkflowInstanceWorkflowTasksPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowInstanceWorkflowTasksPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		page = workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
			workflowInstanceId, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page,
			testGetWorkflowInstanceWorkflowTasksPage_getExpectedActions(
				workflowInstanceId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowInstanceWorkflowTasksPage_getExpectedActions(
				Long workflowInstanceId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowInstanceWorkflowTasksPageWithPagination()
		throws Exception {

		Long workflowInstanceId =
			testGetWorkflowInstanceWorkflowTasksPage_getWorkflowInstanceId();

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
				workflowInstanceId, null, null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowInstanceWorkflowTasksPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowInstanceWorkflowTasksPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowInstanceWorkflowTasksPage_addWorkflowTask(
				workflowInstanceId, randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
					workflowInstanceId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
					workflowInstanceId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
					workflowInstanceId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
					workflowInstanceId, null, Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
					workflowInstanceId, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
					workflowInstanceId, null,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowInstanceWorkflowTasksPage_addWorkflowTask(
				Long workflowInstanceId, WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowTasksPage_getWorkflowInstanceId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowInstanceWorkflowTasksPage_getIrrelevantWorkflowInstanceId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkflowTask() throws Exception {
		WorkflowTask postWorkflowTask = testGetWorkflowTask_addWorkflowTask();

		WorkflowTask getWorkflowTask = workflowTaskResource.getWorkflowTask(
			postWorkflowTask.getId());

		assertEquals(postWorkflowTask, getWorkflowTask);
		assertValid(getWorkflowTask);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WorkflowTask postWorkflowTask = testGetWorkflowTask_addWorkflowTask();

		WorkflowTask getWorkflowTask = workflowTaskResource.getWorkflowTask(
			postWorkflowTask.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask"
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

		Object item = vulcanCRUDItemDelegate.getItem(postWorkflowTask.getId());

		assertEquals(
			getWorkflowTask, WorkflowTaskSerDes.toDTO(item.toString()));
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

	protected WorkflowTask testGetWorkflowTask_addWorkflowTask()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkflowTask() throws Exception {
		WorkflowTask workflowTask =
			testGraphQLGetWorkflowTask_addWorkflowTask();

		// No namespace

		Assert.assertTrue(
			equals(
				workflowTask,
				WorkflowTaskSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"workflowTask",
								new HashMap<String, Object>() {
									{
										put(
											"workflowTaskId",
											workflowTask.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/workflowTask"))));

		// Using the namespace headlessAdminWorkflow_v1_0

		Assert.assertTrue(
			equals(
				workflowTask,
				WorkflowTaskSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminWorkflow_v1_0",
								new GraphQLField(
									"workflowTask",
									new HashMap<String, Object>() {
										{
											put(
												"workflowTaskId",
												workflowTask.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminWorkflow_v1_0",
						"Object/workflowTask"))));
	}

	@Test
	public void testGraphQLGetWorkflowTaskNotFound() throws Exception {
		Long irrelevantWorkflowTaskId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"workflowTask",
						new HashMap<String, Object>() {
							{
								put("workflowTaskId", irrelevantWorkflowTaskId);
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
							"workflowTask",
							new HashMap<String, Object>() {
								{
									put(
										"workflowTaskId",
										irrelevantWorkflowTaskId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WorkflowTask testGraphQLGetWorkflowTask_addWorkflowTask()
		throws Exception {

		return testGraphQLWorkflowTask_addWorkflowTask();
	}

	@Test
	public void testGetWorkflowTaskHasAssignableUsers() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetWorkflowTasksAssignedToMePage() throws Exception {
		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowTasksAssignedToMePage(
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToMePage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToMePage_addWorkflowTask(
				randomWorkflowTask());

		page = workflowTaskResource.getWorkflowTasksAssignedToMePage(
			Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page, testGetWorkflowTasksAssignedToMePage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowTasksAssignedToMePage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowTasksAssignedToMePageWithPagination()
		throws Exception {

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.getWorkflowTasksAssignedToMePage(null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToMePage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToMePage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowTasksAssignedToMePage_addWorkflowTask(
				randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToMePage(
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToMePage(
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToMePage(
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToMePage(
					Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToMePage(
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToMePage(
					Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask testGetWorkflowTasksAssignedToMePage_addWorkflowTask(
			WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetWorkflowTasksAssignedToMyRolesPage() throws Exception {
		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToMyRolesPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToMyRolesPage_addWorkflowTask(
				randomWorkflowTask());

		page = workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
			Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page,
			testGetWorkflowTasksAssignedToMyRolesPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowTasksAssignedToMyRolesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowTasksAssignedToMyRolesPageWithPagination()
		throws Exception {

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToMyRolesPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToMyRolesPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowTasksAssignedToMyRolesPage_addWorkflowTask(
				randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
					Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
					Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowTasksAssignedToMyRolesPage_addWorkflowTask(
				WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetWorkflowTasksAssignedToRolePage() throws Exception {
		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowTasksAssignedToRolePage(
				null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToRolePage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToRolePage_addWorkflowTask(
				randomWorkflowTask());

		page = workflowTaskResource.getWorkflowTasksAssignedToRolePage(
			null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page, testGetWorkflowTasksAssignedToRolePage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowTasksAssignedToRolePage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowTasksAssignedToRolePageWithPagination()
		throws Exception {

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.getWorkflowTasksAssignedToRolePage(null, null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToRolePage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToRolePage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowTasksAssignedToRolePage_addWorkflowTask(
				randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToRolePage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToRolePage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToRolePage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToRolePage(
					null, Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToRolePage(
					null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToRolePage(
					null, Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowTasksAssignedToRolePage_addWorkflowTask(
				WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetWorkflowTasksAssignedToUserPage() throws Exception {
		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowTasksAssignedToUserPage(
				null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToUserPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToUserPage_addWorkflowTask(
				randomWorkflowTask());

		page = workflowTaskResource.getWorkflowTasksAssignedToUserPage(
			null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page, testGetWorkflowTasksAssignedToUserPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowTasksAssignedToUserPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowTasksAssignedToUserPageWithPagination()
		throws Exception {

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.getWorkflowTasksAssignedToUserPage(null, null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToUserPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToUserPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowTasksAssignedToUserPage_addWorkflowTask(
				randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToUserPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToUserPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToUserPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToUserPage(
					null, Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToUserPage(
					null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToUserPage(
					null, Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowTasksAssignedToUserPage_addWorkflowTask(
				WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetWorkflowTasksAssignedToUserRolesPage() throws Exception {
		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
				null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToUserRolesPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToUserRolesPage_addWorkflowTask(
				randomWorkflowTask());

		page = workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
			null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page,
			testGetWorkflowTasksAssignedToUserRolesPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowTasksAssignedToUserRolesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowTasksAssignedToUserRolesPageWithPagination()
		throws Exception {

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
				null, null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksAssignedToUserRolesPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksAssignedToUserRolesPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowTasksAssignedToUserRolesPage_addWorkflowTask(
				randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
					null, Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
					null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
					null, Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowTasksAssignedToUserRolesPage_addWorkflowTask(
				WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetWorkflowTasksSubmittingUserPage() throws Exception {
		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowTasksSubmittingUserPage(
				null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksSubmittingUserPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksSubmittingUserPage_addWorkflowTask(
				randomWorkflowTask());

		page = workflowTaskResource.getWorkflowTasksSubmittingUserPage(
			null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(workflowTask1, (List<WorkflowTask>)page.getItems());
		assertContains(workflowTask2, (List<WorkflowTask>)page.getItems());
		assertValid(
			page, testGetWorkflowTasksSubmittingUserPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowTasksSubmittingUserPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowTasksSubmittingUserPageWithPagination()
		throws Exception {

		Page<WorkflowTask> workflowTasksPage =
			workflowTaskResource.getWorkflowTasksSubmittingUserPage(null, null);

		int totalCount = GetterUtil.getInteger(
			workflowTasksPage.getTotalCount());

		WorkflowTask workflowTask1 =
			testGetWorkflowTasksSubmittingUserPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask2 =
			testGetWorkflowTasksSubmittingUserPage_addWorkflowTask(
				randomWorkflowTask());

		WorkflowTask workflowTask3 =
			testGetWorkflowTasksSubmittingUserPage_addWorkflowTask(
				randomWorkflowTask());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksSubmittingUserPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(workflowTask1, (List<WorkflowTask>)page1.getItems());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksSubmittingUserPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask2, (List<WorkflowTask>)page2.getItems());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksSubmittingUserPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
		else {
			Page<WorkflowTask> page1 =
				workflowTaskResource.getWorkflowTasksSubmittingUserPage(
					null, Pagination.of(1, totalCount + 2));

			List<WorkflowTask> workflowTasks1 =
				(List<WorkflowTask>)page1.getItems();

			Assert.assertEquals(
				workflowTasks1.toString(), totalCount + 2,
				workflowTasks1.size());

			Page<WorkflowTask> page2 =
				workflowTaskResource.getWorkflowTasksSubmittingUserPage(
					null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowTask> workflowTasks2 =
				(List<WorkflowTask>)page2.getItems();

			Assert.assertEquals(
				workflowTasks2.toString(), 1, workflowTasks2.size());

			Page<WorkflowTask> page3 =
				workflowTaskResource.getWorkflowTasksSubmittingUserPage(
					null, Pagination.of(1, (int)totalCount + 3));

			assertContains(workflowTask1, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask2, (List<WorkflowTask>)page3.getItems());
			assertContains(workflowTask3, (List<WorkflowTask>)page3.getItems());
		}
	}

	protected WorkflowTask
			testGetWorkflowTasksSubmittingUserPage_addWorkflowTask(
				WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchWorkflowTaskAssignToUser() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WorkflowTask workflowTask =
			testPatchWorkflowTaskAssignToUser_addWorkflowTask();

		assertHttpResponseStatusCode(
			204,
			workflowTaskResource.patchWorkflowTaskAssignToUserHttpResponse(
				null));

		assertHttpResponseStatusCode(
			404,
			workflowTaskResource.patchWorkflowTaskAssignToUserHttpResponse(
				null));
	}

	protected WorkflowTask testPatchWorkflowTaskAssignToUser_addWorkflowTask()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchWorkflowTaskChangeTransition() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WorkflowTask workflowTask =
			testPatchWorkflowTaskChangeTransition_addWorkflowTask();

		assertHttpResponseStatusCode(
			204,
			workflowTaskResource.patchWorkflowTaskChangeTransitionHttpResponse(
				null));

		assertHttpResponseStatusCode(
			404,
			workflowTaskResource.patchWorkflowTaskChangeTransitionHttpResponse(
				null));
	}

	protected WorkflowTask
			testPatchWorkflowTaskChangeTransition_addWorkflowTask()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchWorkflowTaskUpdateDueDate() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WorkflowTask workflowTask =
			testPatchWorkflowTaskUpdateDueDate_addWorkflowTask();

		assertHttpResponseStatusCode(
			204,
			workflowTaskResource.patchWorkflowTaskUpdateDueDateHttpResponse(
				null));

		assertHttpResponseStatusCode(
			404,
			workflowTaskResource.patchWorkflowTaskUpdateDueDateHttpResponse(
				null));
	}

	protected WorkflowTask testPatchWorkflowTaskUpdateDueDate_addWorkflowTask()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWorkflowTaskAssignToMe() throws Exception {
		WorkflowTask randomWorkflowTask = randomWorkflowTask();

		WorkflowTask postWorkflowTask =
			testPostWorkflowTaskAssignToMe_addWorkflowTask(randomWorkflowTask);

		assertEquals(randomWorkflowTask, postWorkflowTask);
		assertValid(postWorkflowTask);
	}

	protected WorkflowTask testPostWorkflowTaskAssignToMe_addWorkflowTask(
			WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWorkflowTaskAssignToRole() throws Exception {
		WorkflowTask randomWorkflowTask = randomWorkflowTask();

		WorkflowTask postWorkflowTask =
			testPostWorkflowTaskAssignToRole_addWorkflowTask(
				randomWorkflowTask);

		assertEquals(randomWorkflowTask, postWorkflowTask);
		assertValid(postWorkflowTask);
	}

	protected WorkflowTask testPostWorkflowTaskAssignToRole_addWorkflowTask(
			WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWorkflowTaskAssignToUser() throws Exception {
		WorkflowTask randomWorkflowTask = randomWorkflowTask();

		WorkflowTask postWorkflowTask =
			testPostWorkflowTaskAssignToUser_addWorkflowTask(
				randomWorkflowTask);

		assertEquals(randomWorkflowTask, postWorkflowTask);
		assertValid(postWorkflowTask);
	}

	protected WorkflowTask testPostWorkflowTaskAssignToUser_addWorkflowTask(
			WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWorkflowTaskChangeTransition() throws Exception {
		WorkflowTask randomWorkflowTask = randomWorkflowTask();

		WorkflowTask postWorkflowTask =
			testPostWorkflowTaskChangeTransition_addWorkflowTask(
				randomWorkflowTask);

		assertEquals(randomWorkflowTask, postWorkflowTask);
		assertValid(postWorkflowTask);
	}

	protected WorkflowTask testPostWorkflowTaskChangeTransition_addWorkflowTask(
			WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWorkflowTaskUpdateDueDate() throws Exception {
		WorkflowTask randomWorkflowTask = randomWorkflowTask();

		WorkflowTask postWorkflowTask =
			testPostWorkflowTaskUpdateDueDate_addWorkflowTask(
				randomWorkflowTask);

		assertEquals(randomWorkflowTask, postWorkflowTask);
		assertValid(postWorkflowTask);
	}

	protected WorkflowTask testPostWorkflowTaskUpdateDueDate_addWorkflowTask(
			WorkflowTask workflowTask)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWorkflowTasksPage() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected WorkflowTask testGraphQLWorkflowTask_addWorkflowTask()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		WorkflowTask workflowTask, List<WorkflowTask> workflowTasks) {

		boolean contains = false;

		for (WorkflowTask item : workflowTasks) {
			if (equals(workflowTask, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			workflowTasks + " does not contain " + workflowTask, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WorkflowTask workflowTask1, WorkflowTask workflowTask2) {

		Assert.assertTrue(
			workflowTask1 + " does not equal " + workflowTask2,
			equals(workflowTask1, workflowTask2));
	}

	protected void assertEquals(
		List<WorkflowTask> workflowTasks1, List<WorkflowTask> workflowTasks2) {

		Assert.assertEquals(workflowTasks1.size(), workflowTasks2.size());

		for (int i = 0; i < workflowTasks1.size(); i++) {
			WorkflowTask workflowTask1 = workflowTasks1.get(i);
			WorkflowTask workflowTask2 = workflowTasks2.get(i);

			assertEquals(workflowTask1, workflowTask2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WorkflowTask> workflowTasks1, List<WorkflowTask> workflowTasks2) {

		Assert.assertEquals(workflowTasks1.size(), workflowTasks2.size());

		for (WorkflowTask workflowTask1 : workflowTasks1) {
			boolean contains = false;

			for (WorkflowTask workflowTask2 : workflowTasks2) {
				if (equals(workflowTask1, workflowTask2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				workflowTasks2 + " does not contain " + workflowTask1,
				contains);
		}
	}

	protected void assertValid(WorkflowTask workflowTask) throws Exception {
		boolean valid = true;

		if (workflowTask.getDateCreated() == null) {
			valid = false;
		}

		if (workflowTask.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (workflowTask.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assigneePerson", additionalAssertFieldName)) {
				if (workflowTask.getAssigneePerson() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assigneeRoles", additionalAssertFieldName)) {
				if (workflowTask.getAssigneeRoles() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (workflowTask.getCompleted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (workflowTask.getDateCompletion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateDue", additionalAssertFieldName)) {
				if (workflowTask.getDateDue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (workflowTask.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (workflowTask.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (workflowTask.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectReviewed", additionalAssertFieldName)) {
				if (workflowTask.getObjectReviewed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionId", additionalAssertFieldName)) {

				if (workflowTask.getWorkflowDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionName", additionalAssertFieldName)) {

				if (workflowTask.getWorkflowDefinitionName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionVersion", additionalAssertFieldName)) {

				if (workflowTask.getWorkflowDefinitionVersion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowInstanceId", additionalAssertFieldName)) {

				if (workflowTask.getWorkflowInstanceId() == null) {
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

	protected void assertValid(Page<WorkflowTask> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WorkflowTask> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WorkflowTask> workflowTasks = page.getItems();

		int size = workflowTasks.size();

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
					com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask.
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
		WorkflowTask workflowTask1, WorkflowTask workflowTask2) {

		if (workflowTask1 == workflowTask2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)workflowTask1.getActions(),
						(Map)workflowTask2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assigneePerson", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getAssigneePerson(),
						workflowTask2.getAssigneePerson())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assigneeRoles", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getAssigneeRoles(),
						workflowTask2.getAssigneeRoles())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getCompleted(),
						workflowTask2.getCompleted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getDateCompletion(),
						workflowTask2.getDateCompletion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getDateCreated(),
						workflowTask2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateDue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getDateDue(),
						workflowTask2.getDateDue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getDescription(),
						workflowTask2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getId(), workflowTask2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getLabel(), workflowTask2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getName(), workflowTask2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectReviewed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowTask1.getObjectReviewed(),
						workflowTask2.getObjectReviewed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowTask1.getWorkflowDefinitionId(),
						workflowTask2.getWorkflowDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowTask1.getWorkflowDefinitionName(),
						workflowTask2.getWorkflowDefinitionName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionVersion", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowTask1.getWorkflowDefinitionVersion(),
						workflowTask2.getWorkflowDefinitionVersion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowInstanceId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowTask1.getWorkflowInstanceId(),
						workflowTask2.getWorkflowInstanceId())) {

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

		if (!(_workflowTaskResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_workflowTaskResource;

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
		EntityField entityField, String operator, WorkflowTask workflowTask) {

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

		if (entityFieldName.equals("assigneePerson")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assigneeRoles")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("completed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCompletion")) {
			if (operator.equals("between")) {
				Date date = workflowTask.getDateCompletion();

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

				sb.append(_format.format(workflowTask.getDateCompletion()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = workflowTask.getDateCreated();

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

				sb.append(_format.format(workflowTask.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateDue")) {
			if (operator.equals("between")) {
				Date date = workflowTask.getDateDue();

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

				sb.append(_format.format(workflowTask.getDateDue()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = workflowTask.getDescription();

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

		if (entityFieldName.equals("label")) {
			Object object = workflowTask.getLabel();

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

		if (entityFieldName.equals("name")) {
			Object object = workflowTask.getName();

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

		if (entityFieldName.equals("objectReviewed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("workflowDefinitionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("workflowDefinitionName")) {
			Object object = workflowTask.getWorkflowDefinitionName();

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

		if (entityFieldName.equals("workflowDefinitionVersion")) {
			Object object = workflowTask.getWorkflowDefinitionVersion();

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

		if (entityFieldName.equals("workflowInstanceId")) {
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

	protected WorkflowTask randomWorkflowTask() throws Exception {
		return new WorkflowTask() {
			{
				completed = RandomTestUtil.randomBoolean();
				dateCompletion = RandomTestUtil.nextDate();
				dateCreated = RandomTestUtil.nextDate();
				dateDue = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				label = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				workflowDefinitionId = RandomTestUtil.randomLong();
				workflowDefinitionName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				workflowDefinitionVersion = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				workflowInstanceId = RandomTestUtil.randomLong();
			}
		};
	}

	protected WorkflowTask randomIrrelevantWorkflowTask() throws Exception {
		WorkflowTask randomIrrelevantWorkflowTask = randomWorkflowTask();

		return randomIrrelevantWorkflowTask;
	}

	protected WorkflowTask randomPatchWorkflowTask() throws Exception {
		return randomWorkflowTask();
	}

	protected WorkflowTaskResource workflowTaskResource;
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
		LogFactoryUtil.getLog(BaseWorkflowTaskResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskResource
			_workflowTaskResource;

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