/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.exception.DispatchLogStartDateException;
import com.liferay.dispatch.exception.DispatchLogStatusException;
import com.liferay.dispatch.exception.NoSuchTriggerException;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.executor.internal.messaging.TestDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.test.util.DispatchLogTestUtil;
import com.liferay.dispatch.test.util.DispatchTriggerTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Beslic
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DispatchLogLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testAddDispatchLogExceptions() throws Exception {
		User user = UserTestUtil.addUser();

		Class<?> exceptionClass = Exception.class;

		DispatchLog dispatchLog = DispatchLogTestUtil.randomDispatchLog(
			user, DispatchTaskStatus.IN_PROGRESS);

		try {
			_dispatchLogLocalService.addDispatchLog(
				user.getUserId(), 444, dispatchLog.getEndDate(),
				dispatchLog.getError(), dispatchLog.getOutput(),
				dispatchLog.getStartDate(),
				DispatchTaskStatus.valueOf(dispatchLog.getStatus()));
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Add dispatch log with no parent dispatch trigger",
			NoSuchTriggerException.class, exceptionClass);

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST,
				1));

		try {
			Date startDate = dispatchLog.getStartDate();

			_dispatchLogLocalService.addDispatchLog(
				user.getUserId(), dispatchTrigger.getDispatchTriggerId(),
				new Date(startDate.getTime() - 60000), dispatchLog.getError(),
				dispatchLog.getOutput(), startDate,
				DispatchTaskStatus.valueOf(dispatchLog.getStatus()));
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Add dispatch log with invalid end date",
			DispatchLogStartDateException.class, exceptionClass);

		try {
			_dispatchLogLocalService.addDispatchLog(
				user.getUserId(), dispatchTrigger.getDispatchTriggerId(), null,
				null, null, null,
				DispatchTaskStatus.valueOf(dispatchLog.getStatus()));
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Add dispatch log with illegal start date",
			DispatchLogStartDateException.class, exceptionClass);

		try {
			_dispatchLogLocalService.addDispatchLog(
				user.getUserId(), dispatchTrigger.getDispatchTriggerId(),
				dispatchLog.getEndDate(), dispatchLog.getError(),
				dispatchLog.getOutput(), dispatchLog.getStartDate(), null);
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Add dispatch log with invalid status",
			DispatchLogStatusException.class, exceptionClass);
	}

	@Test
	public void testDeleteDispatchLogWhileInProgress() throws Exception {
		User user = UserTestUtil.addUser();

		Class<?> exceptionClass = Exception.class;

		for (DispatchTaskStatus dispatchTaskStatus :
				DispatchTaskStatus.values()) {

			DispatchLog dispatchLog = DispatchLogTestUtil.randomDispatchLog(
				user, dispatchTaskStatus);

			dispatchLog = _dispatchLogLocalService.addDispatchLog(dispatchLog);

			try {
				_dispatchLogLocalService.deleteDispatchLog(
					dispatchLog.getDispatchLogId());

				Assert.assertNotEquals(
					DispatchTaskStatus.IN_PROGRESS, dispatchTaskStatus);

				Assert.assertNull(
					_dispatchLogLocalService.fetchDispatchLog(
						dispatchLog.getDispatchLogId()));

				continue;
			}
			catch (Exception exception) {
				exceptionClass = exception.getClass();
			}

			Assert.assertEquals(
				DispatchLogStatusException.class, exceptionClass);
		}
	}

	@Test
	public void testFetchLatestDispatchLog() throws Exception {
		int dispatchLogsCount = RandomTestUtil.randomInt(10, 40);

		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST,
				1));

		DispatchLog dispatchLog =
			_dispatchLogLocalService.fetchLatestDispatchLog(
				dispatchTrigger.getDispatchTriggerId());

		Assert.assertNull(dispatchLog);

		_addDispatchLogs(
			user, dispatchTrigger.getDispatchTriggerId(),
			DispatchTaskStatus.IN_PROGRESS, dispatchLogsCount);

		dispatchLog = _dispatchLogLocalService.fetchLatestDispatchLog(
			dispatchTrigger.getDispatchTriggerId(),
			DispatchTaskStatus.SUCCESSFUL);

		Assert.assertNull(dispatchLog);

		_addDispatchLogs(
			user, dispatchTrigger.getDispatchTriggerId(),
			DispatchTaskStatus.SUCCESSFUL, dispatchLogsCount);

		dispatchLog = _dispatchLogLocalService.fetchLatestDispatchLog(
			dispatchTrigger.getDispatchTriggerId());

		List<DispatchLog> dispatchLogs =
			_dispatchLogLocalService.getDispatchLogs(
				dispatchTrigger.getDispatchTriggerId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		_assertLatestStartDate(dispatchLog, dispatchLogs);

		dispatchLog = _dispatchLogLocalService.fetchLatestDispatchLog(
			dispatchTrigger.getDispatchTriggerId(),
			DispatchTaskStatus.SUCCESSFUL);

		Assert.assertNotNull(dispatchLog);

		_assertLatestStartDate(
			dispatchLog,
			ListUtil.filter(
				dispatchLogs,
				item -> {
					if (DispatchTaskStatus.valueOf(item.getStatus()) ==
							DispatchTaskStatus.SUCCESSFUL) {

						return true;
					}

					return false;
				}));
	}

	@Test
	public void testGetDispatchLogs() throws Exception {
		int dispatchLogsCount = RandomTestUtil.randomInt(10, 40);

		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST,
				1));

		_addDispatchLogs(
			user, dispatchTrigger.getDispatchTriggerId(),
			DispatchTaskStatus.IN_PROGRESS, dispatchLogsCount);

		List<DispatchLog> dispatchLogs =
			_dispatchLogLocalService.getDispatchLogs(
				dispatchTrigger.getDispatchTriggerId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			dispatchLogs.toString(), dispatchLogsCount, dispatchLogs.size());
	}

	@Test
	public void testUpdateDispatchLog() throws Exception {
		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST,
				1));

		DispatchLog expectedDispatchLog = DispatchLogTestUtil.randomDispatchLog(
			user, DispatchTaskStatus.FAILED);

		DispatchLog dispatchLog = _dispatchLogLocalService.addDispatchLog(
			user.getUserId(), dispatchTrigger.getDispatchTriggerId(), null,
			null, null, expectedDispatchLog.getStartDate(),
			DispatchTaskStatus.IN_PROGRESS);

		Assert.assertNull(dispatchLog.getEndDate());
		Assert.assertNull(dispatchLog.getError());
		Assert.assertNull(dispatchLog.getOutput());

		Assert.assertEquals(
			DispatchTaskStatus.IN_PROGRESS,
			DispatchTaskStatus.valueOf(dispatchLog.getStatus()));

		dispatchLog = _dispatchLogLocalService.updateDispatchLog(
			dispatchLog.getDispatchLogId(), expectedDispatchLog.getEndDate(),
			expectedDispatchLog.getError(), expectedDispatchLog.getOutput(),
			DispatchTaskStatus.valueOf(expectedDispatchLog.getStatus()));

		Assert.assertEquals(
			dispatchTrigger.getDispatchTriggerId(),
			dispatchLog.getDispatchTriggerId());

		_assertEquals(expectedDispatchLog, dispatchLog);
	}

	@Test
	public void testUpdateDispatchLogExceptions() throws Exception {
		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST,
				1));

		DispatchLog expectedDispatchLog = DispatchLogTestUtil.randomDispatchLog(
			user, DispatchTaskStatus.IN_PROGRESS);

		Class<?> exceptionClass = Exception.class;

		DispatchLog dispatchLog = _dispatchLogLocalService.addDispatchLog(
			user.getUserId(), dispatchTrigger.getDispatchTriggerId(), null,
			null, null, expectedDispatchLog.getStartDate(),
			DispatchTaskStatus.valueOf(expectedDispatchLog.getStatus()));

		try {
			Date startDate = expectedDispatchLog.getStartDate();

			_dispatchLogLocalService.updateDispatchLog(
				dispatchLog.getDispatchLogId(),
				new Date(startDate.getTime() - 60000), null, null,
				DispatchTaskStatus.SUCCESSFUL);
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Update dispatch log with illegal end date",
			DispatchLogStartDateException.class, exceptionClass);

		try {
			_dispatchLogLocalService.updateDispatchLog(
				dispatchLog.getDispatchLogId(),
				expectedDispatchLog.getEndDate(), null, null, null);
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Update dispatch log with illegal status value",
			DispatchLogStatusException.class, exceptionClass);
	}

	private List<DispatchLog> _addDispatchLogs(
			User user, long dispatchTriggerId,
			DispatchTaskStatus dispatchTaskStatus, int dispatchLogsCount)
		throws Exception {

		List<DispatchLog> dispatchLogs = new ArrayList<>();

		for (int i = 0; i < dispatchLogsCount; i++) {
			DispatchLog dispatchLog = DispatchLogTestUtil.randomDispatchLog(
				user, dispatchTaskStatus);

			dispatchLogs.add(
				_dispatchLogLocalService.addDispatchLog(
					user.getUserId(), dispatchTriggerId,
					dispatchLog.getEndDate(), dispatchLog.getError(),
					dispatchLog.getOutput(), dispatchLog.getStartDate(),
					DispatchTaskStatus.valueOf(dispatchLog.getStatus())));
		}

		return dispatchLogs;
	}

	private DispatchTrigger _addDispatchTrigger(DispatchTrigger dispatchTrigger)
		throws Exception {

		return _dispatchTriggerLocalService.addDispatchTrigger(
			null, dispatchTrigger.getUserId(),
			dispatchTrigger.getDispatchTaskExecutorType(),
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties(),
			dispatchTrigger.getName(), dispatchTrigger.isSystem());
	}

	private void _assertEquals(
		DispatchLog expectedDispatchLog, DispatchLog actualDispatchLog) {

		Assert.assertEquals(
			expectedDispatchLog.getCompanyId(),
			actualDispatchLog.getCompanyId());
		Assert.assertEquals(
			expectedDispatchLog.getUserId(), actualDispatchLog.getUserId());
		Assert.assertEquals(
			expectedDispatchLog.getEndDate(), actualDispatchLog.getEndDate());
		Assert.assertEquals(
			expectedDispatchLog.getError(), actualDispatchLog.getError());
		Assert.assertEquals(
			expectedDispatchLog.getOutput(), actualDispatchLog.getOutput());
		Assert.assertEquals(
			expectedDispatchLog.getStartDate(),
			actualDispatchLog.getStartDate());
		Assert.assertEquals(
			expectedDispatchLog.getStatus(), actualDispatchLog.getStatus());
	}

	private void _assertLatestStartDate(
		DispatchLog currentDispatchLog, List<DispatchLog> dispatchLogs) {

		Date currentStartDate = currentDispatchLog.getStartDate();

		for (DispatchLog dispatchLog : dispatchLogs) {
			if (currentDispatchLog.getDispatchLogId() ==
					dispatchLog.getDispatchLogId()) {

				continue;
			}

			Date startDate = dispatchLog.getStartDate();

			Assert.assertTrue(
				"Latest dispatch log start date",
				currentStartDate.getTime() >= startDate.getTime());
		}
	}

	@Inject
	private DispatchLogLocalService _dispatchLogLocalService;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}