/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstance;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstanceSubmit;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowInstanceResource;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.ObjectReviewedTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowDefinitionTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowInstanceTestUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.constants.WorkflowActionKeys;

import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class WorkflowInstanceResourceTest
	extends BaseWorkflowInstanceResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_workflowDefinition =
			WorkflowDefinitionTestUtil.addWorkflowDefinition();
	}

	@Override
	@Test
	public void testDeleteWorkflowInstance() throws Exception {
		super.testDeleteWorkflowInstance();

		_testDeleteWorkflowInstanceWithoutPermission();
	}

	@Override
	@Test
	public void testGetWorkflowInstance() throws Exception {
		WorkflowInstance postWorkflowInstance =
			testGetWorkflowInstance_addWorkflowInstance();

		WorkflowInstance getWorkflowInstance =
			workflowInstanceResource.getWorkflowInstance(
				postWorkflowInstance.getId());

		assertEquals(postWorkflowInstance, getWorkflowInstance);
		assertValid(getWorkflowInstance);
		Assert.assertThat(
			getWorkflowInstance.getCurrentNodeNames(),
			CoreMatchers.is(new String[] {"review"}));
	}

	@Override
	@Test
	public void testPatchWorkflowInstance() throws Exception {
		String contextVariable1Name = RandomTestUtil.randomString();
		String contextVariable1Value = RandomTestUtil.randomString();

		WorkflowInstance workflowInstance =
			workflowInstanceResource.postWorkflowInstanceSubmit(
				new WorkflowInstanceSubmit() {
					{
						context = HashMapBuilder.put(
							contextVariable1Name, contextVariable1Value
						).build();
						workflowDefinitionName = _workflowDefinition.getName();
						workflowDefinitionVersion =
							_workflowDefinition.getVersion();
					}
				});

		String contextVariable2name = RandomTestUtil.randomString();
		String contextVariable2Value = RandomTestUtil.randomString();

		workflowInstance = workflowInstanceResource.patchWorkflowInstance(
			workflowInstance.getId(),
			new WorkflowInstance() {
				{
					context = HashMapBuilder.put(
						contextVariable1Name, RandomTestUtil.randomString()
					).put(
						contextVariable2name, contextVariable2Value
					).build();
				}
			});

		Map<String, String> context =
			(Map<String, String>)workflowInstance.getContext();

		Assert.assertEquals(
			contextVariable1Value, context.get(contextVariable1Name));
		Assert.assertEquals(
			contextVariable2Value, context.get(contextVariable2name));
		Assert.assertEquals(context.toString(), 2, context.size());
	}

	@Override
	@Test
	public void testPostWorkflowInstanceSubmit() throws Exception {
		super.testPostWorkflowInstanceSubmit();

		_testPostWorkflowInstanceSubmitWithoutPermission();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"completed", "objectReviewed", "workflowDefinitionName",
			"workflowDefinitionVersion"
		};
	}

	@Override
	protected WorkflowInstance randomWorkflowInstance() throws Exception {
		WorkflowInstance workflowInstance = super.randomWorkflowInstance();

		workflowInstance.setCompleted(false);
		workflowInstance.setObjectReviewed(
			ObjectReviewedTestUtil.addObjectReviewed());
		workflowInstance.setWorkflowDefinitionName(
			_workflowDefinition.getName());
		workflowInstance.setWorkflowDefinitionVersion(
			_workflowDefinition.getVersion());

		return workflowInstance;
	}

	@Override
	protected WorkflowInstance testDeleteWorkflowInstance_addWorkflowInstance()
		throws Exception {

		return testGetWorkflowInstance_addWorkflowInstance();
	}

	@Override
	protected WorkflowInstance testGetWorkflowInstance_addWorkflowInstance()
		throws Exception {

		return testGetWorkflowInstancesPage_addWorkflowInstance(
			randomWorkflowInstance());
	}

	@Override
	protected WorkflowInstance testGetWorkflowInstancesPage_addWorkflowInstance(
			WorkflowInstance workflowInstance)
		throws Exception {

		return WorkflowInstanceTestUtil.addWorkflowInstance(
			testGroup.getGroupId(), workflowInstance.getObjectReviewed(),
			_workflowDefinition);
	}

	@Override
	protected WorkflowInstance testGraphQLWorkflowInstance_addWorkflowInstance()
		throws Exception {

		return testGetWorkflowInstance_addWorkflowInstance();
	}

	@Override
	protected WorkflowInstance
			testPostWorkflowInstanceChangeTransition_addWorkflowInstance(
				WorkflowInstance workflowInstance)
		throws Exception {

		return testGetWorkflowInstancesPage_addWorkflowInstance(
			workflowInstance);
	}

	@Override
	protected WorkflowInstance
			testPostWorkflowInstanceSubmit_addWorkflowInstance(
				WorkflowInstance workflowInstance)
		throws Exception {

		return testGetWorkflowInstancesPage_addWorkflowInstance(
			workflowInstance);
	}

	private void _testDeleteWorkflowInstanceWithoutPermission()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		WorkflowInstanceResource userWorkflowInstanceResource =
			WorkflowInstanceResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		WorkflowInstance workflowInstance =
			testDeleteWorkflowInstance_addWorkflowInstance();

		assertHttpResponseStatusCode(
			403,
			userWorkflowInstanceResource.deleteWorkflowInstanceHttpResponse(
				workflowInstance.getId()));

		assertHttpResponseStatusCode(
			200,
			workflowInstanceResource.getWorkflowInstanceHttpResponse(
				workflowInstance.getId()));
	}

	private void _testPostWorkflowInstanceSubmitWithoutPermission()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		WorkflowInstanceResource userWorkflowInstanceResource =
			WorkflowInstanceResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		WorkflowInstanceSubmit workflowInstanceSubmit =
			new WorkflowInstanceSubmit() {
				{
					context = HashMapBuilder.put(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString()
					).build();
					siteId = testGroup.getGroupId();
					workflowDefinitionName = _workflowDefinition.getName();
					workflowDefinitionVersion =
						_workflowDefinition.getVersion();
				}
			};

		assertHttpResponseStatusCode(
			403,
			userWorkflowInstanceResource.postWorkflowInstanceSubmitHttpResponse(
				workflowInstanceSubmit));

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR,
			WorkflowConstants.RESOURCE_NAME, ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()),
			WorkflowActionKeys.START_WORKFLOW_INSTANCE);

		_roleLocalService.addUserRole(user.getUserId(), role);

		assertHttpResponseStatusCode(
			200,
			userWorkflowInstanceResource.postWorkflowInstanceSubmitHttpResponse(
				workflowInstanceSubmit));
	}

	@Inject
	private RoleLocalService _roleLocalService;

	private WorkflowDefinition _workflowDefinition;

}