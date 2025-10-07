/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Role;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class RoleResourceTest extends BaseRoleResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_process = _workflowMetricsRESTTestHelper.addProcess(
			testGroup.getCompanyId());
	}

	@Override
	@Test
	public void testGetProcessRolesPage() throws Exception {
		super.testGetProcessRolesPage();

		Role role1 = _addRole(_process.getId(), randomRole(), "COMPLETED");

		Role role2 = _addRole(_process.getId(), randomRole(), "COMPLETED");

		Page<Role> page = roleResource.getProcessRolesPage(
			_process.getId(), true);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(role1, role2), (List<Role>)page.getItems());
		assertValid(page);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"id", "name"};
	}

	@Override
	protected Role randomRole() throws Exception {
		com.liferay.portal.kernel.model.Role role1 = _addRole(
			RoleConstants.TYPE_SITE);

		return new Role() {
			{
				id = role1.getRoleId();
				name = role1.getName();
			}
		};
	}

	@Override
	protected Role testGetProcessRolesPage_addRole(Long processId, Role role)
		throws Exception {

		return _addRole(processId, role, "RUNNING");
	}

	@Override
	protected Long testGetProcessRolesPage_getProcessId() throws Exception {
		return _process.getId();
	}

	private com.liferay.portal.kernel.model.Role _addRole(int roleType)
		throws Exception {

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			roleType);

		_roles.add(role);

		return role;
	}

	private Role _addRole(Long processId, Role role, String status)
		throws Exception {

		User user = UserTestUtil.addUser();

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleLocalService.getRole(
				testCompany.getCompanyId(), RoleConstants.USER);

		_userLocalService.deleteRoleUser(
			serviceBuilderRole.getRoleId(), user.getUserId());

		_userLocalService.addRoleUser(role.getId(), user);

		_workflowMetricsRESTTestHelper.addNodeMetric(
			new Assignee() {
				{
					id = user.getUserId();
				}
			},
			testGroup.getCompanyId(),
			() -> _workflowMetricsRESTTestHelper.addInstance(
				testGroup.getCompanyId(), Objects.equals(status, "COMPLETED"),
				processId),
			processId, status, TestPropsValues.getUser());

		return role;
	}

	private Process _process;

	@Inject
	private RoleLocalService _roleLocalService;

	private final List<com.liferay.portal.kernel.model.Role> _roles =
		new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

}