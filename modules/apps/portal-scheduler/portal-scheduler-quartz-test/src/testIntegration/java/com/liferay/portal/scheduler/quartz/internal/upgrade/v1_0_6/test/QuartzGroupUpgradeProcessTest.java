/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_6.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class QuartzGroupUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		if (_oldGroupName != null) {
			_schedulerEngine.delete(_oldGroupName, StorageType.PERSISTED);
		}

		if (_newGroupName != null) {
			_schedulerEngine.delete(_newGroupName, StorageType.PERSISTED);
		}
	}

	@Test
	public void testDeletesOrphanWhenTwinExists() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_dispatchTrigger = _dispatchTriggerLocalService.addDispatchTrigger(
			null, TestPropsValues.getUserId(),
			SetUtil.randomElement(
				_dispatchTaskExecutorRegistry.getDispatchTaskExecutorTypes()),
			null, "test", false);

		long dispatchTriggerId = _dispatchTrigger.getDispatchTriggerId();

		String jobName = StringBundler.concat(
			"DISPATCH_JOB_", String.format("%07d", dispatchTriggerId),
			StringPool.AT, companyId);

		_oldGroupName =
			"DISPATCH_GROUP_" + String.format("%07d", dispatchTriggerId);

		_newGroupName = StringBundler.concat(
			_oldGroupName, StringPool.AT, companyId);

		Message message = new Message();

		message.put("companyId", companyId);
		message.setPayload(
			JSONUtil.put("dispatchTriggerId", dispatchTriggerId));

		_schedule(
			jobName, _oldGroupName, _DISPATCH_DESTINATION_NAME, message);
		_schedule(
			jobName, _newGroupName, _DISPATCH_DESTINATION_NAME, message);

		_runUpgrade();

		Assert.assertNull(
			_schedulerEngine.getScheduledJob(
				jobName, _oldGroupName, StorageType.PERSISTED));
		Assert.assertNotNull(
			_schedulerEngine.getScheduledJob(
				jobName, _newGroupName, StorageType.PERSISTED));
	}

	@Test
	public void testRenamesOrphanWhenTwinDoesNotExist() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_dispatchTrigger = _dispatchTriggerLocalService.addDispatchTrigger(
			null, TestPropsValues.getUserId(),
			SetUtil.randomElement(
				_dispatchTaskExecutorRegistry.getDispatchTaskExecutorTypes()),
			null, "test", false);

		long dispatchTriggerId = _dispatchTrigger.getDispatchTriggerId();

		String jobName = StringBundler.concat(
			"DISPATCH_JOB_", String.format("%07d", dispatchTriggerId),
			StringPool.AT, companyId);

		_oldGroupName =
			"DISPATCH_GROUP_" + String.format("%07d", dispatchTriggerId);

		_newGroupName = StringBundler.concat(
			_oldGroupName, StringPool.AT, companyId);

		Message message = new Message();

		message.put("companyId", companyId);
		message.setPayload(
			JSONUtil.put("dispatchTriggerId", dispatchTriggerId));

		_schedule(
			jobName, _oldGroupName, _DISPATCH_DESTINATION_NAME, message);

		_runUpgrade();

		Assert.assertNull(
			_schedulerEngine.getScheduledJob(
				jobName, _oldGroupName, StorageType.PERSISTED));
		Assert.assertNotNull(
			_schedulerEngine.getScheduledJob(
				jobName, _newGroupName, StorageType.PERSISTED));
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();
		}
	}

	private void _schedule(
			String jobName, String groupName, String destinationName,
			Message message)
		throws Exception {

		Trigger trigger = _triggerFactory.createTrigger(
			jobName, groupName, null, null, 1, TimeUnit.DAY);

		_schedulerEngine.schedule(
			trigger, StringPool.BLANK, destinationName, message,
			StorageType.PERSISTED);
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_6." +
			"QuartzGroupUpgradeProcess";

	private static final String _DISPATCH_DESTINATION_NAME =
		"liferay/dispatch/executor";

	@Inject
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@DeleteAfterTestRun
	private DispatchTrigger _dispatchTrigger;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	private String _newGroupName;
	private String _oldGroupName;

	@Inject(
		filter = "component.name=com.liferay.portal.scheduler.quartz.internal.QuartzSchedulerEngine"
	)
	private SchedulerEngine _schedulerEngine;

	@Inject(
		filter = "component.name=com.liferay.portal.scheduler.quartz.internal.QuartzTriggerFactory"
	)
	private TriggerFactory _triggerFactory;

	@Inject(
		filter = "(&(component.name=com.liferay.portal.scheduler.quartz.internal.upgrade.registry.QuartzServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}