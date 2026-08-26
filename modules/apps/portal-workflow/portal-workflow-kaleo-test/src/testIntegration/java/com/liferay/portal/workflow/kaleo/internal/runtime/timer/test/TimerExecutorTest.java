/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.runtime.timer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.constants.KaleoRuntimeDestinationNames;
import com.liferay.portal.workflow.kaleo.runtime.util.SchedulerUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerLocalService;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Feliphe Marinho
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class TimerExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			ScriptManagementConfigurationTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(TimerExecutorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_workflowHandlerServiceRegistration = bundleContext.registerService(
			(Class<WorkflowHandler<?>>)(Class<?>)WorkflowHandler.class,
			(WorkflowHandler)ProxyUtil.newProxyInstance(
				WorkflowHandler.class.getClassLoader(),
				new Class<?>[] {WorkflowHandler.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getClassName")) {
						return TimerExecutorTest.class.getName();
					}

					if (Objects.equals(method.getName(), "getTitle")) {
						return StringPool.BLANK;
					}

					if (Objects.equals(method.getName(), "isScopeable")) {
						return false;
					}

					return null;
				}),
			HashMapDictionaryBuilder.put(
				"model.class.name=", TimerExecutorTest.class.getName()
			).build());
	}

	@AfterClass
	public static void tearDownClass() {
		_workflowHandlerServiceRegistration.unregister();
	}

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_workflowContext = HashMapBuilder.<String, Serializable>put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
			TimerExecutorTest.class.getName()
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
			String.valueOf(RandomTestUtil.randomLong())
		).put(
			WorkflowConstants.CONTEXT_NOTIFICATION_SENDER_ADDRESS,
			() -> {
				User user = TestPropsValues.getUser();

				return user.getEmailAddress();
			}
		).put(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT, _serviceContext
		).build();

		String content = StringUtil.read(
			getClass(), "dependencies/timer-tasks-workflow-definition.xml");

		_workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		_kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.getLatestKaleoDefinitionVersion(
				_workflowDefinition.getCompanyId(),
				_workflowDefinition.getName());

		_kaleoInstance = _kaleoInstanceLocalService.addKaleoInstance(
			_kaleoDefinitionVersion.getKaleoDefinitionId(),
			_kaleoDefinitionVersion.getKaleoDefinitionVersionId(),
			RandomTestUtil.randomString(), _workflowDefinition.getVersion(),
			_workflowContext, _serviceContext);
	}

	@Test
	public void testExecuteTimerNotifications() throws Exception {
		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			_addKaleoTimerInstanceToken(_KALEO_NODE_NAME_TIMER_NOTIFICATION);

		_executeTimer(kaleoTimerInstanceToken);

		Assert.assertNotNull(
			MessageBusUtil.getDestination(
				KaleoRuntimeDestinationNames.WORKFLOW_TIMER));

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		Assert.assertEquals(
			"Timer notification template", mailMessage.getBody());
	}

	@Test
	public void testExecuteTimerReassignments() throws Exception {
		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			_addKaleoTimerInstanceToken(_KALEO_NODE_NAME_TIMER_REASSIGNMENT);

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTimerInstanceToken.getKaleoTaskInstanceToken();

		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances =
			_kaleoTaskAssignmentInstanceLocalService.
				getKaleoTaskAssignmentInstances(
					kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());

		Assert.assertTrue(ListUtil.isEmpty(kaleoTaskAssignmentInstances));

		_executeTimer(kaleoTimerInstanceToken);

		Assert.assertNotNull(
			MessageBusUtil.getDestination(
				KaleoRuntimeDestinationNames.WORKFLOW_TIMER));

		kaleoTaskAssignmentInstances =
			_kaleoTaskAssignmentInstanceLocalService.
				getKaleoTaskAssignmentInstances(
					kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());

		Assert.assertEquals(
			kaleoTaskAssignmentInstances.toString(), 1,
			kaleoTaskAssignmentInstances.size());

		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
			kaleoTaskAssignmentInstances.get(0);

		Assert.assertEquals(
			User.class.getName(),
			kaleoTaskAssignmentInstance.getAssigneeClassName());
		Assert.assertEquals(
			TestPropsValues.getUserId(),
			kaleoTaskAssignmentInstance.getAssigneeClassPK());
	}

	@Test
	public void testExecuteTimerWithCompletedKaleoTimerInstanceToken()
		throws Exception {

		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			_addKaleoTimerInstanceToken(_KALEO_NODE_NAME_TIMER_NOTIFICATION);

		Message message = _getMessage(kaleoTimerInstanceToken);

		_kaleoTimerInstanceTokenLocalService.completeKaleoTimerInstanceToken(
			kaleoTimerInstanceToken.getKaleoTimerInstanceTokenId(),
			_serviceContext);

		int initialInboxSize = MailServiceTestUtil.getInboxSize();

		_messageListener.receive(message);

		Assert.assertEquals(
			initialInboxSize, MailServiceTestUtil.getInboxSize());
	}

	@Test
	public void testExecuteTimerWithDeletedKaleoTimerInstanceToken()
		throws Exception {

		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			_addKaleoTimerInstanceToken(_KALEO_NODE_NAME_TIMER_NOTIFICATION);

		Message message = _getMessage(kaleoTimerInstanceToken);

		_kaleoTimerInstanceTokenLocalService.deleteKaleoTimerInstanceToken(
			kaleoTimerInstanceToken);

		int initialInboxSize = MailServiceTestUtil.getInboxSize();

		_messageListener.receive(message);

		Assert.assertEquals(
			initialInboxSize, MailServiceTestUtil.getInboxSize());

		String groupName = _getSchedulerGroupName(kaleoTimerInstanceToken);

		Assert.assertNull(
			SchedulerEngineHelperUtil.getScheduledJob(
				groupName, groupName, StorageType.PERSISTED));
	}

	private KaleoInstanceToken _addKaleoInstanceToken(KaleoTask kaleoTask)
		throws Exception {

		return _kaleoInstanceTokenLocalService.addKaleoInstanceToken(
			kaleoTask.getKaleoNodeId(), _kaleoInstance.getKaleoDefinitionId(),
			_kaleoInstance.getKaleoDefinitionVersionId(),
			_kaleoInstance.getKaleoInstanceId(), 0, _workflowContext,
			_serviceContext);
	}

	private KaleoTaskInstanceToken _addKaleoTaskInstanceToken(
			KaleoInstanceToken kaleoInstanceToken, KaleoTask kaleoTask)
		throws Exception {

		return _kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
			kaleoInstanceToken.getKaleoInstanceTokenId(),
			kaleoTask.getKaleoTaskId(), kaleoTask.getName(),
			Collections.emptyList(), null, _workflowContext, _serviceContext);
	}

	private KaleoTimerInstanceToken _addKaleoTimerInstanceToken(
			String kaleoNodeName)
		throws Exception {

		KaleoTask kaleoTask = _getKaleoTask(kaleoNodeName);

		KaleoInstanceToken kaleoInstanceToken = _addKaleoInstanceToken(
			kaleoTask);

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			_addKaleoTaskInstanceToken(kaleoInstanceToken, kaleoTask);

		return _kaleoTimerInstanceTokenLocalService.addKaleoTimerInstanceToken(
			kaleoInstanceToken.getKaleoInstanceTokenId(),
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
			_getKaleoTimerId(kaleoTask), RandomTestUtil.randomString(),
			_workflowContext, _serviceContext);
	}

	private void _executeTimer(KaleoTimerInstanceToken kaleoTimerInstanceToken)
		throws Exception {

		_messageListener.receive(_getMessage(kaleoTimerInstanceToken));
	}

	private KaleoTask _getKaleoTask(String kaleoNodeName) {
		for (KaleoNode kaleoNode :
				_kaleoNodeLocalService.getKaleoDefinitionVersionKaleoNodes(
					_kaleoDefinitionVersion.getKaleoDefinitionVersionId())) {

			if (!Objects.equals(kaleoNodeName, kaleoNode.getName())) {
				continue;
			}

			KaleoTask kaleoTask = null;

			try {
				kaleoTask = _kaleoTaskLocalService.getKaleoNodeKaleoTask(
					kaleoNode.getKaleoNodeId());
			}
			catch (PortalException portalException) {
			}

			if (kaleoTask != null) {
				return kaleoTask;
			}
		}

		return null;
	}

	private long _getKaleoTimerId(KaleoTask kaleoTask) {
		List<KaleoTimer> kaleoTimers = _kaleoTimerLocalService.getKaleoTimers(
			KaleoNode.class.getName(), kaleoTask.getKaleoNodeId());

		KaleoTimer kaleoTimer = kaleoTimers.get(0);

		return kaleoTimer.getKaleoTimerId();
	}

	private Message _getMessage(KaleoTimerInstanceToken kaleoTimerInstanceToken)
		throws Exception {

		String groupName = _getSchedulerGroupName(kaleoTimerInstanceToken);

		SchedulerResponse schedulerResponse =
			SchedulerEngineHelperUtil.getScheduledJob(
				groupName, groupName, StorageType.PERSISTED);

		Message message = schedulerResponse.getMessage();

		message.put(
			SchedulerEngine.DESTINATION_NAME,
			KaleoRuntimeDestinationNames.WORKFLOW_TIMER);
		message.put(SchedulerEngine.GROUP_NAME, groupName);
		message.put(SchedulerEngine.JOB_NAME, groupName);
		message.put("companyId", kaleoTimerInstanceToken.getCompanyId());

		return message;
	}

	private String _getSchedulerGroupName(
		KaleoTimerInstanceToken kaleoTimerInstanceToken) {

		return SchedulerUtil.getGroupName(
			kaleoTimerInstanceToken.getCompanyId(),
			kaleoTimerInstanceToken.getKaleoTimerInstanceTokenId());
	}

	private static final String _KALEO_NODE_NAME_TIMER_NOTIFICATION =
		"Timer Notification";

	private static final String _KALEO_NODE_NAME_TIMER_REASSIGNMENT =
		"Timer Reassignment";

	private static ServiceRegistration<WorkflowHandler<?>>
		_workflowHandlerServiceRegistration;

	private KaleoDefinitionVersion _kaleoDefinitionVersion;

	@Inject
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	private KaleoInstance _kaleoInstance;

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Inject
	private KaleoInstanceTokenLocalService _kaleoInstanceTokenLocalService;

	@Inject
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Inject
	private KaleoTaskAssignmentInstanceLocalService
		_kaleoTaskAssignmentInstanceLocalService;

	@Inject
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Inject
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Inject
	private KaleoTimerInstanceTokenLocalService
		_kaleoTimerInstanceTokenLocalService;

	@Inject
	private KaleoTimerLocalService _kaleoTimerLocalService;

	@Inject(
		filter = "destination.name=" + KaleoRuntimeDestinationNames.WORKFLOW_TIMER
	)
	private MessageListener _messageListener;

	private ServiceContext _serviceContext;
	private Map<String, Serializable> _workflowContext;
	private WorkflowDefinition _workflowDefinition;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}