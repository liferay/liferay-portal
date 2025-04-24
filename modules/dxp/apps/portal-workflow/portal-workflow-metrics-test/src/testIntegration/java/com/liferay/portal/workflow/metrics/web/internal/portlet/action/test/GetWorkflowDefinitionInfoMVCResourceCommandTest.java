/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowNode;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.portal.workflow.metrics.service.util.WorkflowDefinitionUtil;

import java.io.ByteArrayOutputStream;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class GetWorkflowDefinitionInfoMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_permissionChecker = PermissionCheckerFactoryUtil.create(_user);

		_workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), StringUtil.randomId(),
				StringUtil.randomId(), WorkflowDefinitionUtil.getBytes());
	}

	@Test
	public void testGetWorkflowDefinitionInfo() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(WebKeys.LOCALE, LocaleUtil.US);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(_permissionChecker);
		themeDisplay.setUser(_user);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter(
			"workflowDefinitionName", _workflowDefinition.getName());
		mockLiferayResourceRequest.setParameter(
			"workflowDefinitionVersion",
			String.valueOf(_workflowDefinition.getVersion()));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			byteArrayOutputStream.toString());

		Assert.assertNotNull(jsonObject);

		List<WorkflowNode> workflowNodes =
			_workflowDefinition.getWorkflowNodes();

		JSONArray workflowNodesJSONArray = jsonObject.getJSONArray("nodes");

		Assert.assertEquals(
			workflowNodes.size(), workflowNodesJSONArray.length());

		List<WorkflowTransition> workflowTransitions =
			_workflowDefinition.getWorkflowTransitions();

		JSONArray workflowtransitionsJSONArray = jsonObject.getJSONArray(
			"transitions");

		Assert.assertEquals(
			workflowTransitions.size(), workflowtransitionsJSONArray.length());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "mvc.command.name=/workflow_metrics/get_workflow_definition_info"
	)
	private MVCResourceCommand _mvcResourceCommand;

	private PermissionChecker _permissionChecker;
	private User _user;
	private WorkflowDefinition _workflowDefinition;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}