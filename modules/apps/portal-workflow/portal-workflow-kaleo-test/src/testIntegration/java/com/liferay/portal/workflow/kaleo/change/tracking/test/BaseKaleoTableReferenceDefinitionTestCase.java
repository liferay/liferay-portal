/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.change.tracking.test;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.test.util.BaseTableReferenceDefinitionTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.kaleo.definition.DelayDuration;
import com.liferay.portal.workflow.kaleo.definition.DurationScale;
import com.liferay.portal.workflow.kaleo.definition.Node;
import com.liferay.portal.workflow.kaleo.definition.Notification;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.definition.TaskForm;
import com.liferay.portal.workflow.kaleo.definition.TaskFormReference;
import com.liferay.portal.workflow.kaleo.definition.Timer;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNotification;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskForm;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNotificationLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskFormLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerLocalService;
import com.liferay.portal.workflow.kaleo.service.test.BaseKaleoLocalServiceTestCase;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Brooke Dalton
 */
public abstract class BaseKaleoTableReferenceDefinitionTestCase
	extends BaseTableReferenceDefinitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		serviceContext = ServiceContextTestUtil.getServiceContext();
	}

	protected KaleoDefinition addKaleoDefinition() throws Exception {
		return _kaleoDefinitionLocalService.addKaleoDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			_read("legal-marketing-workflow-definition.xml"), StringPool.BLANK,
			false, 1, serviceContext);
	}

	protected KaleoInstance addKaleoInstance() throws Exception {
		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(),
			ServiceContextTestUtil.getServiceContext());

		return addKaleoInstance(blogsEntry);
	}

	protected KaleoInstance addKaleoInstance(BlogsEntry blogsEntry)
		throws Exception {

		return _kaleoInstanceLocalService.addKaleoInstance(
			1, 1, StringUtil.randomString(), 1,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
				(Serializable)BlogsEntry.class.getName()
			).put(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
				String.valueOf(blogsEntry.getEntryId())
			).put(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT,
				(Serializable)serviceContext
			).build(),
			serviceContext);
	}

	protected KaleoInstanceToken addKaleoInstanceToken(
			KaleoInstance kaleoInstance, KaleoNode kaleoNode)
		throws Exception {

		return _kaleoInstanceTokenLocalService.addKaleoInstanceToken(
			kaleoNode.getKaleoNodeId(), kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(),
			kaleoInstance.getKaleoInstanceId(), 0,
			WorkflowContextUtil.convert(kaleoInstance.getWorkflowContext()),
			serviceContext);
	}

	protected KaleoNode addKaleoNode(KaleoInstance kaleoInstance, Node node)
		throws Exception {

		return _kaleoNodeLocalService.addKaleoNode(
			kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(), node, serviceContext);
	}

	protected KaleoNotification addKaleoNotification(
			KaleoInstance kaleoInstance, KaleoNode kaleoNode)
		throws Exception {

		return _kaleoNotificationLocalService.addKaleoNotification(
			KaleoNode.class.getName(), kaleoInstance.getClassPK(),
			kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(), kaleoNode.getName(),
			new Notification(
				StringUtil.randomString(), StringUtil.randomString(),
				"onAssignment", StringPool.BLANK, "freemarker"),
			serviceContext);
	}

	protected KaleoTask addKaleoTask(
			KaleoInstance kaleoInstance, KaleoNode kaleoNode, Task task)
		throws Exception {

		return _kaleoTaskLocalService.addKaleoTask(
			kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(),
			kaleoNode.getKaleoNodeId(), task, serviceContext);
	}

	protected KaleoTaskForm addKaleoTaskForm(
			KaleoInstance kaleoInstance, KaleoNode kaleoNode,
			KaleoTask kaleoTask)
		throws Exception {

		TaskForm taskForm = new TaskForm(RandomTestUtil.randomString(), 0);

		taskForm.setTaskFormReference(new TaskFormReference());

		taskForm.setFormDefinition(RandomTestUtil.randomString());

		return _kaleoTaskFormLocalService.addKaleoTaskForm(
			kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(),
			kaleoNode.getKaleoNodeId(), kaleoTask, taskForm, serviceContext);
	}

	protected KaleoTaskInstanceToken addKaleoTaskInstanceToken(
			KaleoInstance kaleoInstance, KaleoInstanceToken kaleoInstanceToken)
		throws Exception {

		return _kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
			kaleoInstanceToken.getKaleoInstanceTokenId(), 1,
			StringUtil.randomString(), Collections.emptyList(), null,
			WorkflowContextUtil.convert(kaleoInstance.getWorkflowContext()),
			serviceContext);
	}

	protected KaleoTaskInstanceToken addKaleoTaskInstanceToken(
			KaleoInstance kaleoInstance, KaleoInstanceToken kaleoInstanceToken,
			KaleoTask kaleoTask)
		throws Exception {

		return _kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
			kaleoInstanceToken.getKaleoInstanceTokenId(),
			kaleoTask.getKaleoTaskId(), kaleoTask.getName(),
			Collections.emptyList(), null,
			WorkflowContextUtil.convert(kaleoInstance.getWorkflowContext()),
			serviceContext);
	}

	protected KaleoTimer addKaleoTimer(KaleoInstance kaleoInstance)
		throws Exception {

		Timer timer = new Timer(
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomBoolean());

		DelayDuration delayDuration = new DelayDuration(
			1, DurationScale.parse("second"));

		timer.setDelayDuration(delayDuration);

		return _kaleoTimerLocalService.addKaleoTimer(
			KaleoNode.class.getName(), kaleoInstance.getClassPK(),
			kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(), timer, serviceContext);
	}

	protected ServiceContext serviceContext;

	private String _read(String name) throws Exception {
		ClassLoader classLoader =
			BaseKaleoLocalServiceTestCase.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"com/liferay/portal/workflow/kaleo/dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Inject
	private KaleoInstanceTokenLocalService _kaleoInstanceTokenLocalService;

	@Inject
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Inject
	private KaleoNotificationLocalService _kaleoNotificationLocalService;

	@Inject
	private KaleoTaskFormLocalService _kaleoTaskFormLocalService;

	@Inject
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Inject
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Inject
	private KaleoTimerLocalService _kaleoTimerLocalService;

}