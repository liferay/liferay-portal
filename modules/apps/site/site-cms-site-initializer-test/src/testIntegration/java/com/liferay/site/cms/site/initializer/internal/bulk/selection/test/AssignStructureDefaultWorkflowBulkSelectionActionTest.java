/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;

import java.io.Serializable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Camacho
 */
@RunWith(Arquillian.class)
public class AssignStructureDefaultWorkflowBulkSelectionActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinition.getClassName(), 0, 0,
			RandomTestUtil.randomString(), 1);
	}

	@Test
	public void testDoExecute() throws Exception {
		_invokeDoExecute(TestPropsValues.getUser(), StringPool.BLANK);

		Assert.assertNull(_fetchDefaultWorkflowDefinitionLink());

		_invokeDoExecute(
			TestPropsValues.getUser(),
			WorkflowDefinitionConstants.NAME_SINGLE_APPROVER);

		WorkflowDefinitionLink workflowDefinitionLink =
			_fetchDefaultWorkflowDefinitionLink();

		Assert.assertEquals(
			WorkflowDefinitionConstants.NAME_SINGLE_APPROVER,
			workflowDefinitionLink.getWorkflowDefinitionName());
	}

	@Test
	public void testDoExecuteWithoutUpdatePermission() throws Exception {
		_user = UserTestUtil.addUser();

		WorkflowDefinitionLink workflowDefinitionLink =
			_fetchDefaultWorkflowDefinitionLink();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", _user.getUserId(),
					" must have UPDATE permission for ",
					ObjectDefinition.class.getName(), " ",
					_objectDefinition.getObjectDefinitionId()),
				() -> _invokeDoExecute(
					_user, WorkflowDefinitionConstants.NAME_SINGLE_APPROVER));
		}

		Assert.assertEquals(
			workflowDefinitionLink.getWorkflowDefinitionName(),
			_fetchDefaultWorkflowDefinitionLink().getWorkflowDefinitionName());
	}

	private WorkflowDefinitionLink _fetchDefaultWorkflowDefinitionLink()
		throws Exception {

		return _workflowDefinitionLinkLocalService.
			fetchDefaultWorkflowDefinitionLink(
				TestPropsValues.getCompanyId(),
				_objectDefinition.getClassName());
	}

	private void _invokeDoExecute(User user, String workflowDefinitionName) {
		ReflectionTestUtil.invoke(
			_assignStructureDefaultWorkflowBulkSelectionAction, "doExecute",
			new Class<?>[] {User.class, Map.class, Object.class}, user,
			HashMapBuilder.<String, Serializable>put(
				"workflow", workflowDefinitionName
			).build(),
			_objectDefinition);
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.bulk.selection.AssignStructureDefaultWorkflowBulkSelectionAction"
	)
	private BulkSelectionAction<Object>
		_assignStructureDefaultWorkflowBulkSelectionAction;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}