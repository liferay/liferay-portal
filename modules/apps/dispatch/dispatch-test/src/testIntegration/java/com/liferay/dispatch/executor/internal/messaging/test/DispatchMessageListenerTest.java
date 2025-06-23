/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.executor.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.executor.internal.messaging.TestDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matija Petanjek
 * @author Igor Beslic
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DispatchMessageListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		TestDispatchTaskExecutor.executionCounter.getAndSet(0);
	}

	@Test
	public void testDoReceiveMissingDispatchTaskExecutor() throws Exception {
		int executeCount = RandomTestUtil.randomInt(1, 3);

		DispatchTrigger dispatchTrigger = _executeDispatchTrigger(
			executeCount, 1000, true,
			TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST);

		List<DispatchLog> dispatchLogs =
			_dispatchLogLocalService.getDispatchLogs(
				dispatchTrigger.getDispatchTriggerId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertTrue(
			String.format(
				"Expected no more than %d dispatch logs", executeCount),
			dispatchLogs.size() <= executeCount);

		for (DispatchLog dispatchLog : dispatchLogs) {
			Assert.assertEquals(
				DispatchTaskStatus.SUCCESSFUL.getStatus(),
				dispatchLog.getStatus());
		}
	}

	@Test
	public void testDoReceiveOverlapAllowed() throws Exception {
		int executeCount = RandomTestUtil.randomInt(4, 10);

		DispatchTrigger dispatchTrigger = _executeDispatchTrigger(
			executeCount, 1000, true,
			TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST);

		_assertExecutionSequence(
			_dispatchLogLocalService.getDispatchLogs(
				dispatchTrigger.getDispatchTriggerId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			executeCount, true);
	}

	@Test
	public void testDoReceiveOverlapNotAllowed() throws Exception {
		int executeCount = RandomTestUtil.randomInt(4, 10);

		DispatchTrigger dispatchTrigger = _executeDispatchTrigger(
			executeCount, 750, false,
			TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST);

		_assertExecutionSequence(
			_dispatchLogLocalService.getDispatchLogs(
				dispatchTrigger.getDispatchTriggerId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			executeCount, false);
	}

	private void _assertExecutionSequence(
		List<DispatchLog> dispatchLogs, int executeCount,
		boolean overlapAllowed) {

		dispatchLogs = ListUtil.filter(
			dispatchLogs,
			dispatchLog -> {
				if (DispatchTaskStatus.valueOf(dispatchLog.getStatus()) ==
						DispatchTaskStatus.SUCCESSFUL) {

					return true;
				}

				return false;
			});

		List<DispatchLog> sortedDispatchLogs = ListUtil.sort(
			dispatchLogs,
			(dispatchLog1, dispatchLog2) -> DateUtil.compareTo(
				dispatchLog1.getCreateDate(), dispatchLog2.getCreateDate()));

		if (sortedDispatchLogs.isEmpty()) {
			return;
		}

		Assert.assertTrue(
			String.format(
				"Expected not more than %d dispatch logs", executeCount),
			executeCount >= sortedDispatchLogs.size());

		Iterator<DispatchLog> iterator = sortedDispatchLogs.iterator();

		DispatchLog currentDispatchLog = iterator.next();

		while (iterator.hasNext()) {
			DispatchLog nextDispatchLog = iterator.next();

			int difference = DateUtil.compareTo(
				currentDispatchLog.getEndDate(),
				nextDispatchLog.getStartDate());

			if (overlapAllowed) {
				Assert.assertTrue(
					String.format(
						"Dispatch log end at %s before next start at %s",
						currentDispatchLog.getEndDate(),
						nextDispatchLog.getStartDate()),
					difference > 0);
			}
			else {
				Assert.assertTrue(
					String.format(
						"Dispatch log end at %s after next start at %s in %s",
						currentDispatchLog.getEndDate(),
						nextDispatchLog.getStartDate(),
						sortedDispatchLogs.toString()),
					difference <= 0);
			}

			currentDispatchLog = nextDispatchLog;
		}
	}

	private void _execute(
			long dispatchTriggerId, int executeCount,
			long scheduledExecutionIntervalMillis)
		throws Exception {

		Message message = _getMessage(dispatchTriggerId);

		NoticeableExecutorService noticeableExecutorService =
			_portalExecutorManager.getPortalExecutor(
				DispatchMessageListenerTest.class.getName());

		while (executeCount-- > 0) {
			noticeableExecutorService.submit(
				() -> {
					_messageListener.receive(message);

					return null;
				});

			Thread.sleep(scheduledExecutionIntervalMillis);
		}

		noticeableExecutorService.shutdown();

		Assert.assertTrue(
			noticeableExecutorService.awaitTermination(
				TestDispatchTaskExecutor.SLEEP_MILLIS * 10,
				TimeUnit.MILLISECONDS));
	}

	private DispatchTrigger _executeDispatchTrigger(
			int executeCount, long scheduledExecutionIntervalMillis,
			boolean overlapAllowed, String type)
		throws Exception {

		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, user.getUserId(), type, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomBoolean());

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		dispatchTrigger = _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId(), false, _CRON_EXPRESSION,
			DispatchTaskClusterMode.NOT_APPLICABLE, 0, 0, 0, 0, 0, true,
			overlapAllowed, calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), "UTC");

		_execute(
			dispatchTrigger.getDispatchTriggerId(), executeCount,
			scheduledExecutionIntervalMillis);

		return dispatchTrigger;
	}

	private Message _getMessage(long dispatchTriggerId) {
		Message message = new Message();

		message.setPayload(
			JSONUtil.put(
				"dispatchTriggerId", dispatchTriggerId
			).toString());

		return message;
	}

	private static final String _CRON_EXPRESSION = "0/1 * * * * ?";

	@Inject
	private DispatchLogLocalService _dispatchLogLocalService;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject(
		filter = "destination.name=" + DispatchConstants.EXECUTOR_DESTINATION_NAME
	)
	private MessageListener _messageListener;

	@Inject
	private PortalExecutorManager _portalExecutorManager;

}