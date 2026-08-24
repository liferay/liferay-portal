/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ObjectEntryStartWorkflowInstanceWaitTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddObjectEntryAnswersAfterItsWorkflowReachesATask()
		throws Exception {

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), "textObjectFieldName")),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		// In test mode, the signaler walks the workflow graph inline, so the
		// first task would exist even without the wait. Clear the mode so the
		// walk takes the production path, where only the wait requested
		// through the workflow context creates the task inside the save.

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.set("liferay.mode", StringPool.BLANK);

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build(),
				serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_PENDING, objectEntry.getStatus());

			WorkflowInstanceLink workflowInstanceLink =
				_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
					TestPropsValues.getCompanyId(),
					objectEntry.getNonzeroGroupId(),
					_objectDefinition.getClassName(),
					objectEntry.getObjectEntryId());

			Assert.assertNotNull(workflowInstanceLink);

			List<WorkflowTask> workflowTasks =
				_workflowTaskManager.getWorkflowTasksByUserRoles(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			boolean found = false;

			for (WorkflowTask workflowTask : workflowTasks) {
				if (workflowTask.getWorkflowInstanceId() ==
						workflowInstanceLink.getWorkflowInstanceId()) {

					found = true;

					break;
				}
			}

			Assert.assertTrue(workflowTasks.toString(), found);
		}
		finally {
			SystemProperties.set(
				"liferay.mode", GetterUtil.getString(liferayMode));
		}
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}