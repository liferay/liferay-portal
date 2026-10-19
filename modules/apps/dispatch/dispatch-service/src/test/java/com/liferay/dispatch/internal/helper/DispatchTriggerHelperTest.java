/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.helper;

import com.liferay.dispatch.exception.DispatchTriggerSchedulerException;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cluster.ClusterableContextThreadLocal;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adolfo Pérez
 */
public class DispatchTriggerHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		ClusterableContextThreadLocal.collectThreadLocalContext();

		Mockito.when(
			_dispatchTrigger.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_dispatchTrigger.getDispatchTriggerId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_triggerFactory.createTrigger(
				Mockito.anyString(), Mockito.anyString(),
				Mockito.nullable(Date.class), Mockito.nullable(Date.class),
				Mockito.nullable(String.class), Mockito.any(TimeZone.class))
		).thenReturn(
			Mockito.mock(Trigger.class)
		);
	}

	@Test
	public void testAddLocalSchedulerJob() throws Exception {
		AtomicReference<Map<String, Serializable>> atomicReference =
			new AtomicReference<>();

		Mockito.doAnswer(
			invocationOnMock -> {
				atomicReference.set(
					ClusterableContextThreadLocal.collectThreadLocalContext());

				throw new SchedulerException();
			}
		).when(
			_schedulerEngineHelper
		).schedule(
			Mockito.any(Trigger.class), Mockito.any(StorageType.class),
			Mockito.nullable(String.class), Mockito.anyString(),
			Mockito.any(Message.class)
		);

		Assert.assertThrows(
			DispatchTriggerSchedulerException.class,
			() -> _dispatchTriggerHelper.addLocalSchedulerJob(
				_dispatchTrigger, "UTC"));

		Mockito.verify(
			_schedulerEngineHelper
		).schedule(
			Mockito.any(Trigger.class), Mockito.eq(StorageType.MEMORY),
			Mockito.nullable(String.class), Mockito.anyString(),
			Mockito.any(Message.class)
		);

		Assert.assertFalse(
			MapUtil.getBoolean(
				atomicReference.get(),
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, true));
		Assert.assertTrue(
			MapUtil.getBoolean(
				ClusterableContextThreadLocal.collectThreadLocalContext(),
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING));
	}

	@Test
	public void testDeleteLocalSchedulerJob() throws Exception {
		AtomicReference<Map<String, Serializable>> atomicReference =
			new AtomicReference<>();

		Mockito.doAnswer(
			invocationOnMock -> {
				atomicReference.set(
					ClusterableContextThreadLocal.collectThreadLocalContext());

				throw new SchedulerException();
			}
		).when(
			_schedulerEngineHelper
		).delete(
			Mockito.anyString(), Mockito.anyString(),
			Mockito.any(StorageType.class)
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DispatchTriggerHelper.class.getName(), LoggerTestUtil.ERROR)) {

			_dispatchTriggerHelper.deleteLocalSchedulerJob(_dispatchTrigger);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}

		String dispatchTriggerId = String.format(
			"%07d", _dispatchTrigger.getDispatchTriggerId());

		Mockito.verify(
			_schedulerEngineHelper
		).delete(
			StringBundler.concat(
				"DISPATCH_JOB_", dispatchTriggerId, "@",
				_dispatchTrigger.getCompanyId()),
			StringBundler.concat(
				"DISPATCH_GROUP_", dispatchTriggerId, "@",
				_dispatchTrigger.getCompanyId()),
			StorageType.MEMORY
		);

		Mockito.verify(
			_schedulerEngineHelper, Mockito.never()
		).getScheduledJob(
			Mockito.anyString(), Mockito.anyString(), Mockito.any()
		);

		Assert.assertFalse(
			MapUtil.getBoolean(
				atomicReference.get(),
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, true));
		Assert.assertTrue(
			MapUtil.getBoolean(
				ClusterableContextThreadLocal.collectThreadLocalContext(),
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING));
	}

	@Mock
	private DispatchTrigger _dispatchTrigger;

	@InjectMocks
	private final DispatchTriggerHelper _dispatchTriggerHelper =
		new DispatchTriggerHelper();

	@Mock
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Mock
	private TriggerFactory _triggerFactory;

}