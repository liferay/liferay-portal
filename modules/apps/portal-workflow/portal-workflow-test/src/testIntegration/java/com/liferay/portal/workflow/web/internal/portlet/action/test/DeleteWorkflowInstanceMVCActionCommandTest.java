/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class DeleteWorkflowInstanceMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), _group.getCompanyId(),
			_group.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			"Single Approver", 1);
	}

	@Test
	public void testProcessAction() throws Exception {

		// Administrator user

		BlogsEntry blogsEntry = _addBlogsEntry();

		_processAction(
			blogsEntry, WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE,
			TestPropsValues.getUser());

		_assertWorkflowInstanceDeleted(blogsEntry.getEntryId());

		// Administrator user from a different company

		blogsEntry = _addBlogsEntry();

		_company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.addCompanyAdminUser(_company);

		_assertWorkflowInstanceNotDeleted(
			blogsEntry.getEntryId(), WorkflowException.class,
			_processAction(
				blogsEntry, WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE,
				user));

		// Administrator user with a workflow context from a different company

		WorkflowInstance workflowInstance =
			_workflowInstanceManager.startWorkflowInstance(
				_company.getCompanyId(), 0, user.getUserId(), "Single Approver",
				1, null,
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_COMPANY_ID,
					String.valueOf(blogsEntry.getCompanyId())
				).put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
					BlogsEntry.class.getName()
				).put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
					String.valueOf(blogsEntry.getEntryId())
				).put(
					WorkflowConstants.CONTEXT_GROUP_ID,
					String.valueOf(blogsEntry.getGroupId())
				).put(
					WorkflowConstants.CONTEXT_SERVICE_CONTEXT,
					ServiceContextTestUtil.getServiceContext(
						_company.getGroupId(), user.getUserId())
				).build());

		_assertWorkflowInstanceNotDeleted(
			blogsEntry.getEntryId(),
			PrincipalException.MustHavePermission.class,
			_processAction(
				WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE, user,
				workflowInstance.getWorkflowInstanceId()));

		// Nonowner user

		for (String portletId :
				new String[] {
					WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
					WorkflowPortletKeys.SITE_ADMINISTRATION_WORKFLOW,
					WorkflowPortletKeys.USER_WORKFLOW
				}) {

			blogsEntry = _addBlogsEntry();

			_assertWorkflowInstanceNotDeleted(
				blogsEntry.getEntryId(),
				PrincipalException.MustHavePermission.class,
				_processAction(blogsEntry, portletId, _user2));
		}

		// Owner user

		blogsEntry = _addBlogsEntry();

		_processAction(blogsEntry, WorkflowPortletKeys.USER_WORKFLOW, _user1);

		_assertWorkflowInstanceDeleted(blogsEntry.getEntryId());
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		return BlogsTestUtil.addEntry(
			_user1.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user1.getUserId()));
	}

	private void _assertWorkflowInstanceDeleted(long entryId) throws Exception {
		BlogsEntry blogsEntry = _blogsEntryLocalService.getEntry(entryId);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, blogsEntry.getStatus());

		Assert.assertNull(
			_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
				blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));
	}

	private void _assertWorkflowInstanceNotDeleted(
			long entryId, Class<?> exceptionClass,
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest)
		throws Exception {

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest, exceptionClass));

		BlogsEntry blogsEntry = _blogsEntryLocalService.getEntry(entryId);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, blogsEntry.getStatus());

		Assert.assertNotNull(
			_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
				blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));
	}

	private MockLiferayPortletActionRequest _processAction(
			BlogsEntry blogsEntry, String portletId, User user)
		throws Exception {

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

		return _processAction(
			portletId, user, workflowInstanceLink.getWorkflowInstanceId());
	}

	private MockLiferayPortletActionRequest _processAction(
			String portletId, User user, long workflowInstanceId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(user.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(portletId), null));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.addParameter(
			"workflowInstanceId", String.valueOf(workflowInstanceId));

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/portal_workflow/delete_workflow_instance"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

}