/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.liferay.ai.hub.rest.client.dto.v1_0.TaskDefinition;
import com.liferay.ai.hub.rest.client.pagination.Page;
import com.liferay.ai.hub.rest.client.pagination.Pagination;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author João Victor Alves
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class TaskDefinitionResourceTest
	extends BaseTaskDefinitionResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.ai.hub.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testDeleteTaskDefinition() throws Exception {
		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.liberalGetLatestWorkflowDefinition(
				TestPropsValues.getCompanyId(), "Single Approver");

		workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, workflowDefinition.getCompanyId(),
				workflowDefinition.getUserId(), workflowDefinition.getTitle(),
				RandomTestUtil.randomString(),
				workflowDefinition.getContent(
				).getBytes());

		long workflowDefinitionId =
			workflowDefinition.getWorkflowDefinitionId();

		taskDefinitionResource.deleteTaskDefinition(workflowDefinitionId);

		List<WorkflowDefinition> activeWorkflowDefinitions =
			_workflowDefinitionManager.getActiveWorkflowDefinitions(-1, -1);

		for (WorkflowDefinition activeWorkflowDefinition :
				activeWorkflowDefinitions) {

			Assert.assertNotEquals(
				"The deleted task definition ID should not exist in the list",
				activeWorkflowDefinition.getWorkflowDefinitionId(),
				workflowDefinitionId);
		}
	}

	@Test
	public void testGetTaskDefinitionsPage() throws Exception {
		Page<TaskDefinition> page =
			taskDefinitionResource.getTaskDefinitionsPage(
				null, null, Pagination.of(1, 10), null);

		assertEquals(
			List.of(
				new TaskDefinition() {
					{
						name = WorkflowDefinitionConstants.NAME_CHANGE_TONE;
						version = 1;
					}
				},
				new TaskDefinition() {
					{
						name =
							WorkflowDefinitionConstants.
								NAME_CHAT_MESSAGE_PIPELINE;
						version = 1;
					}
				},
				new TaskDefinition() {
					{
						name =
							WorkflowDefinitionConstants.
								NAME_FIX_SPELLING_AND_GRAMMAR;
						version = 1;
					}
				},
				new TaskDefinition() {
					{
						name = WorkflowDefinitionConstants.NAME_IMPROVE_WRITING;
						version = 1;
					}
				},
				new TaskDefinition() {
					{
						name = WorkflowDefinitionConstants.NAME_MAKE_LONGER;
						version = 1;
					}
				},
				new TaskDefinition() {
					{
						name = WorkflowDefinitionConstants.NAME_MAKE_SHORTER;
						version = 1;
					}
				}),
			(List<TaskDefinition>)page.getItems());
	}

	@Ignore
	@Override
	@Test
	public void testGetTaskDefinitionsPageWithPagination() {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "version"};
	}

	@Override
	protected TaskDefinition testGetTaskDefinitionsPage_addTaskDefinition(
		TaskDefinition taskDefinition) {

		return taskDefinition;
	}

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private static WorkflowDefinitionManager _workflowDefinitionManager;

}