/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.tab.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTab;

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
public class WorkflowInstancePortletTabTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), _group.getCompanyId(),
			_group.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			"Single Approver", 1);
	}

	@Test
	public void testPrepareRender() throws Exception {

		// Administrator user

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

		_assertWorkflowInstance(
			_prepareRender(
				TestPropsValues.getUser(),
				workflowInstanceLink.getWorkflowInstanceId()),
			workflowInstanceLink.getWorkflowInstanceId());

		// Administrator user from a different company

		_company = CompanyTestUtil.addCompany();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_prepareRender(
				UserTestUtil.addCompanyAdminUser(_company),
				workflowInstanceLink.getWorkflowInstanceId());

		Assert.assertNull(
			mockLiferayPortletRenderRequest.getAttribute(
				WebKeys.WORKFLOW_INSTANCE));
		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletRenderRequest, WorkflowException.class));

		// Workflow instance without a company ID in the workflow context

		WorkflowInstance workflowInstance =
			_workflowInstanceManager.startWorkflowInstance(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId(), "Single Approver", 1, null,
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
					BlogsEntry.class.getName()
				).put(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
					String.valueOf(blogsEntry.getEntryId())
				).put(
					WorkflowConstants.CONTEXT_ENTRY_TYPE, "Blogs Entry"
				).put(
					WorkflowConstants.CONTEXT_SERVICE_CONTEXT,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId())
				).build());

		_assertWorkflowInstance(
			_prepareRender(
				TestPropsValues.getUser(),
				workflowInstance.getWorkflowInstanceId()),
			workflowInstance.getWorkflowInstanceId());
	}

	private void _assertWorkflowInstance(
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest,
		long workflowInstanceId) {

		WorkflowInstance workflowInstance =
			(WorkflowInstance)mockLiferayPortletRenderRequest.getAttribute(
				WebKeys.WORKFLOW_INSTANCE);

		Assert.assertEquals(
			workflowInstanceId, workflowInstance.getWorkflowInstanceId());
	}

	private MockLiferayPortletRenderRequest _prepareRender(
			User user, long workflowInstanceId)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(user.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.addParameter(
			"workflowInstanceId", String.valueOf(workflowInstanceId));

		_workflowPortletTab.prepareRender(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest;
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

	@Inject(
		filter = "portal.workflow.tabs.name=" + WorkflowWebKeys.WORKFLOW_TAB_INSTANCE
	)
	private WorkflowPortletTab _workflowPortletTab;

}