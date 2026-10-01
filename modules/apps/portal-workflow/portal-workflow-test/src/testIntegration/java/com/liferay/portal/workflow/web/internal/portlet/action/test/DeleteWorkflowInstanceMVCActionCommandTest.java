/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
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
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

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

		// Nonowner user

		for (String portletId :
				new String[] {
					WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
					WorkflowPortletKeys.SITE_ADMINISTRATION_WORKFLOW,
					WorkflowPortletKeys.USER_WORKFLOW
				}) {

			blogsEntry = _addBlogsEntry();

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_processAction(blogsEntry, portletId, _user2);

			Assert.assertTrue(
				SessionErrors.contains(
					mockLiferayPortletActionRequest,
					PrincipalException.MustHavePermission.class));

			blogsEntry = _blogsEntryLocalService.getEntry(
				blogsEntry.getEntryId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_PENDING, blogsEntry.getStatus());

			Assert.assertNotNull(
				_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
					blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
					BlogsEntry.class.getName(), blogsEntry.getEntryId()));
		}

		// Owner user

		blogsEntry = _addBlogsEntry();

		_processAction(blogsEntry, WorkflowPortletKeys.USER_WORKFLOW, _user1);

		_assertWorkflowInstanceDeleted(blogsEntry.getEntryId());
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		return _blogsEntryLocalService.addEntry(
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

	private MockLiferayPortletActionRequest _processAction(
			BlogsEntry blogsEntry, String portletId, User user)
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

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

		mockLiferayPortletActionRequest.addParameter(
			"workflowInstanceId",
			String.valueOf(workflowInstanceLink.getWorkflowInstanceId()));

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

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

}