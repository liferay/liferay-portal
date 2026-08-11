/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.workflow.client.dto.v1_0.ObjectReviewed;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstance;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowLog;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.client.pagination.Page;
import com.liferay.headless.admin.workflow.client.pagination.Pagination;
import com.liferay.headless.admin.workflow.client.problem.Problem;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowLogResource;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.ObjectReviewedTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowDefinitionTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowInstanceTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowTaskTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class WorkflowLogResourceTest extends BaseWorkflowLogResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseWorkflowLogResourceTestCase.setUpClass();

		_workflowDefinition =
			WorkflowDefinitionTestUtil.addWorkflowDefinition();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(
				UserLocalServiceUtil.getUser(TestPropsValues.getUserId())));
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_administratorRole = _roleLocalService.getRole(
			testGroup.getCompanyId(), RoleConstants.ADMINISTRATOR);
		_siteContentReviewerRole = _roleLocalService.getRole(
			testGroup.getCompanyId(), RoleConstants.SITE_CONTENT_REVIEWER);

		_objectReviewed = ObjectReviewedTestUtil.addObjectReviewed();

		_workflowInstance = WorkflowInstanceTestUtil.addWorkflowInstance(
			testGroup.getGroupId(), _objectReviewed, _workflowDefinition);

		_workflowTask = WorkflowTaskTestUtil.getWorkflowTask(
			_workflowInstance.getId());

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_userWorkflowLogResource = WorkflowLogResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Override
	@Test
	public void testGetWorkflowInstanceWorkflowLogsPage() throws Exception {
		_workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_workflowTask.getId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);

		Page<WorkflowLog> page =
			workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				_workflowInstance.getId(),
				new String[] {WorkflowLog.Type.TRANSITION.getValue()},
				Pagination.of(1, 2));

		Assert.assertEquals(0, page.getTotalCount());

		page = workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
			_workflowInstance.getId(),
			new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
			Pagination.of(1, 3));

		Assert.assertEquals(3, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				_toAssignToHimselfWorkflowLog(),
				_toAssignToRoleWorkflowLog(_siteContentReviewerRole),
				_toAssignToRoleWorkflowLog(_administratorRole)),
			(List<WorkflowLog>)page.getItems());

		assertValid(page);

		_testGetWorkflowInstanceWorkflowLogsPageWithoutPermission();
	}

	@Override
	@Test
	public void testGetWorkflowInstanceWorkflowLogsPageWithPagination()
		throws Exception {

		_workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_workflowTask.getId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);

		Page<WorkflowLog> page1 =
			workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				_workflowInstance.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(1, 2));

		Assert.assertEquals(3, page1.getTotalCount());

		List<WorkflowLog> workflowLogs1 = (List<WorkflowLog>)page1.getItems();

		Assert.assertEquals(workflowLogs1.toString(), 2, workflowLogs1.size());

		assertEquals(
			Arrays.asList(
				_toAssignToHimselfWorkflowLog(),
				_toAssignToRoleWorkflowLog(_siteContentReviewerRole)),
			workflowLogs1);

		Page<WorkflowLog> page3 =
			workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				_workflowInstance.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(3, 1));

		Assert.assertEquals(3, page3.getTotalCount());

		List<WorkflowLog> workflowLogs2 = (List<WorkflowLog>)page3.getItems();

		Assert.assertEquals(workflowLogs2.toString(), 1, workflowLogs2.size());

		assertEquals(
			Arrays.asList(_toAssignToRoleWorkflowLog(_administratorRole)),
			workflowLogs2);
	}

	@Override
	@Test
	public void testGetWorkflowLog() throws Exception {
		super.testGetWorkflowLog();

		_testGetWorkflowLogWithNonexistentWorkflowLogId();
		_testGetWorkflowLogWithoutPermission();
		_testGetWorkflowLogWithPermission();
	}

	@Override
	@Test
	public void testGetWorkflowTaskWorkflowLogsPage() throws Exception {
		Page<WorkflowLog> page =
			workflowLogResource.getWorkflowTaskWorkflowLogsPage(
				_workflowTask.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(1, 2));

		Assert.assertEquals(2, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				_toAssignToRoleWorkflowLog(_siteContentReviewerRole),
				_toAssignToRoleWorkflowLog(_administratorRole)),
			(List<WorkflowLog>)page.getItems());

		assertValid(page);

		_testGetWorkflowTaskWorkflowLogsPageWithoutPermission();
	}

	@Override
	@Test
	public void testGetWorkflowTaskWorkflowLogsPageWithPagination()
		throws Exception {

		_workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_workflowTask.getId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);

		Page<WorkflowLog> page1 =
			workflowLogResource.getWorkflowTaskWorkflowLogsPage(
				_workflowTask.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(1, 2));

		List<WorkflowLog> workflowLogs1 = (List<WorkflowLog>)page1.getItems();

		Assert.assertEquals(workflowLogs1.toString(), 2, workflowLogs1.size());

		assertEquals(
			Arrays.asList(
				_toAssignToHimselfWorkflowLog(),
				_toAssignToRoleWorkflowLog(_siteContentReviewerRole)),
			workflowLogs1);

		Page<WorkflowLog> page3 =
			workflowLogResource.getWorkflowTaskWorkflowLogsPage(
				_workflowTask.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(3, 1));

		Assert.assertEquals(3, page3.getTotalCount());

		List<WorkflowLog> workflowLogs2 = (List<WorkflowLog>)page3.getItems();

		Assert.assertEquals(workflowLogs2.toString(), 1, workflowLogs2.size());

		assertEquals(
			Arrays.asList(_toAssignToRoleWorkflowLog(_administratorRole)),
			workflowLogs2);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"commentLog", "description", "state", "stateLabel", "type",
			"workflowTaskId"
		};
	}

	@Override
	protected WorkflowLog testGetWorkflowLog_addWorkflowLog() throws Exception {
		Page<WorkflowLog> page =
			workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				_workflowInstance.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(1, 2));

		List<WorkflowLog> workflowLogs = (List<WorkflowLog>)page.getItems();

		return workflowLogs.get(0);
	}

	@Override
	protected WorkflowLog testGraphQLWorkflowLog_addWorkflowLog()
		throws Exception {

		return testGetWorkflowLog_addWorkflowLog();
	}

	private void _assertNotFound(UnsafeRunnable<Exception> unsafeRunnable) {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			Problem.ProblemException problemException = Assert.assertThrows(
				Problem.ProblemException.class, unsafeRunnable::run);

			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetWorkflowInstanceWorkflowLogsPageWithoutPermission() {
		_assertNotFound(
			() -> _userWorkflowLogResource.getWorkflowInstanceWorkflowLogsPage(
				_workflowInstance.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(1, 2)));
	}

	private void _testGetWorkflowLogWithNonexistentWorkflowLogId() {
		_assertNotFound(
			() -> workflowLogResource.getWorkflowLog(
				RandomTestUtil.randomLong()));
	}

	private void _testGetWorkflowLogWithoutPermission() throws Exception {
		WorkflowLog workflowLog = testGetWorkflowLog_addWorkflowLog();

		_assertNotFound(
			() -> _userWorkflowLogResource.getWorkflowLog(workflowLog.getId()));
	}

	private void _testGetWorkflowLogWithPermission() throws Exception {
		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.addWorkflowInstanceLink(
				_user.getUserId(), testCompany.getCompanyId(),
				testGroup.getGroupId(), ObjectReviewed.class.getName(),
				_objectReviewed.getId(), _workflowInstance.getId());

		WorkflowLog workflowLog = testGetWorkflowLog_addWorkflowLog();

		WorkflowLog userWorkflowLog = _userWorkflowLogResource.getWorkflowLog(
			workflowLog.getId());

		Assert.assertEquals(workflowLog.getId(), userWorkflowLog.getId());

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			workflowInstanceLink);
	}

	private void _testGetWorkflowTaskWorkflowLogsPageWithoutPermission() {
		_assertNotFound(
			() -> _userWorkflowLogResource.getWorkflowTaskWorkflowLogsPage(
				_workflowTask.getId(),
				new String[] {WorkflowLog.Type.TASK_ASSIGN.getValue()},
				Pagination.of(1, 2)));
	}

	private WorkflowLog _toAssignToHimselfWorkflowLog() throws Exception {
		return new WorkflowLog() {
			{
				commentLog = StringPool.BLANK;
				description = _language.format(
					LocaleUtil.getDefault(), "x-assigned-the-task-to-himself",
					_portal.getUserName(
						TestPropsValues.getUserId(), StringPool.BLANK),
					false);
				state = "review";
				stateLabel = "Review";
				type = Type.TASK_ASSIGN;
				workflowTaskId = _workflowTask.getId();
			}
		};
	}

	private WorkflowLog _toAssignToRoleWorkflowLog(Role role) {
		String roleTitle = role.getTitle(LocaleUtil.getDefault());

		return new WorkflowLog() {
			{
				commentLog = _language.get(
					LocaleUtil.getDefault(), "assigned-initial-task");
				description = _language.format(
					LocaleUtil.getDefault(),
					"task-initially-assigned-to-the-x-role", roleTitle, false);
				state = "review";
				stateLabel = "Review";
				type = Type.TASK_ASSIGN;
				workflowTaskId = _workflowTask.getId();
			}
		};
	}

	private static WorkflowDefinition _workflowDefinition;

	private Role _administratorRole;

	@Inject
	private Language _language;

	private ObjectReviewed _objectReviewed;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	private Role _siteContentReviewerRole;
	private User _user;
	private WorkflowLogResource _userWorkflowLogResource;
	private WorkflowInstance _workflowInstance;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	private WorkflowTask _workflowTask;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}