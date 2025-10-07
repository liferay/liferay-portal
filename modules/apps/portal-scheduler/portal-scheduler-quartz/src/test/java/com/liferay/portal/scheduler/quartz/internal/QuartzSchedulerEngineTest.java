/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.scheduler.JobState;
import com.liferay.portal.kernel.scheduler.JobStateSerializeUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.scheduler.quartz.internal.job.MessageSenderJob;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.quartz.Calendar;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerMetaData;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;

/**
 * @author Tina Tian
 */
public class QuartzSchedulerEngineTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		_schedulerEngineHelperServiceRegistration =
			bundleContext.registerService(
				SchedulerEngineHelper.class,
				Mockito.mock(SchedulerEngineHelper.class), null);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
		_portalUUIDUtilMockedStatic.close();
		_schedulerEngineHelperServiceRegistration.unregister();
	}

	@Before
	public void setUp() throws SchedulerException {
		Thread currentThread = Thread.currentThread();

		ClassLoader currentClassLoader = currentThread.getContextClassLoader();

		PortalClassLoaderUtil.setClassLoader(currentClassLoader);

		_setUpPortalUUIDUtil();

		_quartzSchedulerEngine = new QuartzSchedulerEngine();

		PropsUtil.set(PropsKeys.SCHEDULER_ENABLED, "false");

		_quartzSchedulerEngine.activate();

		ReflectionTestUtil.setFieldValue(
			_quartzSchedulerEngine, "_jsonFactory", _setUpJSONFactory());

		ReflectionTestUtil.setFieldValue(
			_quartzSchedulerEngine, "_memoryScheduler",
			new MockScheduler(StorageType.MEMORY, _MEMORY_TEST_GROUP_NAME));

		ReflectionTestUtil.setFieldValue(
			_quartzSchedulerEngine, "_persistedScheduler",
			new MockScheduler(
				StorageType.PERSISTED, _PERSISTED_TEST_GROUP_NAME));

		_quartzSchedulerEngine.start();
	}

	@After
	public void tearDown() {
		if (_quartzSchedulerEngine != null) {
			_quartzSchedulerEngine.deactivate();
		}
	}

	@Test
	public void testDelete1() throws Exception {

		// Delete by group name

		List<SchedulerResponse> schedulerResponses =
			_quartzSchedulerEngine.getScheduledJobs(
				_MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		Assert.assertEquals(
			schedulerResponses.toString(), _DEFAULT_JOB_NUMBER,
			schedulerResponses.size());

		_quartzSchedulerEngine.delete(
			_MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(
			_MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		// Delete by job name and group name

		SchedulerResponse schedulerResponse =
			_quartzSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
				StorageType.PERSISTED);

		Assert.assertNotNull(schedulerResponse);

		_quartzSchedulerEngine.delete(
			_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		schedulerResponse = _quartzSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		Assert.assertNull(schedulerResponse);
	}

	@Test
	public void testDelete2() throws Exception {
		List<SchedulerResponse> schedulerResponses =
			_quartzSchedulerEngine.getScheduledJobs();

		String testJobName = _TEST_JOB_NAME_PREFIX + "memory";

		Assert.assertEquals(
			schedulerResponses.toString(), 2 * _DEFAULT_JOB_NUMBER,
			schedulerResponses.size());

		Trigger trigger = _quartzTriggerFactory.createTrigger(
			testJobName, _MEMORY_TEST_GROUP_NAME, null, null, _DEFAULT_INTERVAL,
			TimeUnit.SECOND);

		_quartzSchedulerEngine.schedule(
			trigger, StringPool.BLANK, _TEST_DESTINATION_NAME, new Message(),
			StorageType.MEMORY);

		schedulerResponses = _quartzSchedulerEngine.getScheduledJobs();

		Assert.assertEquals(
			schedulerResponses.toString(), (2 * _DEFAULT_JOB_NUMBER) + 1,
			schedulerResponses.size());

		_quartzSchedulerEngine.delete(
			testJobName, _MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		schedulerResponses = _quartzSchedulerEngine.getScheduledJobs();

		Assert.assertEquals(
			schedulerResponses.toString(), 2 * _DEFAULT_JOB_NUMBER,
			schedulerResponses.size());
	}

	@Test
	public void testDisableScheduler() {
		PropsUtil.set(PropsKeys.SCHEDULER_ENABLED, "true");

		_quartzSchedulerEngine.activate();

		Boolean schedulerEngineEnabled = ReflectionTestUtil.getFieldValue(
			_quartzSchedulerEngine, "_schedulerEngineEnabled");

		Assert.assertTrue(schedulerEngineEnabled);

		_quartzSchedulerEngine.deactivate();

		PropsUtil.set(PropsKeys.SCHEDULER_ENABLED, "false");

		_quartzSchedulerEngine.activate();

		schedulerEngineEnabled = ReflectionTestUtil.getFieldValue(
			_quartzSchedulerEngine, "_schedulerEngineEnabled");

		Assert.assertFalse(schedulerEngineEnabled);
	}

	@Test
	public void testMaxLengthValues() {
		int descriptionMaxLength =
			_quartzSchedulerEngine.getDescriptionMaxLength() +
				RandomTestUtil.randomInt();
		int groupNameMaxLength =
			_quartzSchedulerEngine.getGroupNameMaxLength() +
				RandomTestUtil.randomInt();
		int jobNameMaxLength =
			_quartzSchedulerEngine.getJobNameMaxLength() +
				RandomTestUtil.randomInt();

		PropsUtil.set(
			PropsKeys.SCHEDULER_DESCRIPTION_MAX_LENGTH,
			String.valueOf(descriptionMaxLength));

		PropsUtil.set(PropsKeys.SCHEDULER_ENABLED, "false");

		PropsUtil.set(
			PropsKeys.SCHEDULER_GROUP_NAME_MAX_LENGTH,
			String.valueOf(groupNameMaxLength));

		PropsUtil.set(
			PropsKeys.SCHEDULER_JOB_NAME_MAX_LENGTH,
			String.valueOf(jobNameMaxLength));

		_quartzSchedulerEngine.activate();

		Assert.assertEquals(
			descriptionMaxLength,
			_quartzSchedulerEngine.getDescriptionMaxLength());
		Assert.assertEquals(
			groupNameMaxLength, _quartzSchedulerEngine.getGroupNameMaxLength());
		Assert.assertEquals(
			jobNameMaxLength, _quartzSchedulerEngine.getJobNameMaxLength());
	}

	@Test
	public void testPauseAndResume() throws Exception {
		SchedulerResponse schedulerResponse =
			_quartzSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
				StorageType.PERSISTED);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);

		_quartzSchedulerEngine.pause(
			_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		schedulerResponse = _quartzSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		_assertTriggerState(schedulerResponse, TriggerState.PAUSED);

		_quartzSchedulerEngine.resume(
			_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		schedulerResponse = _quartzSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _PERSISTED_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);
	}

	@Test
	public void testSchedule1() throws Exception {
		List<SchedulerResponse> schedulerResponses =
			_quartzSchedulerEngine.getScheduledJobs(
				_MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		Assert.assertEquals(
			schedulerResponses.toString(), _DEFAULT_JOB_NUMBER,
			schedulerResponses.size());

		Trigger trigger = _quartzTriggerFactory.createTrigger(
			_TEST_JOB_NAME_PREFIX + "memory", _MEMORY_TEST_GROUP_NAME, null,
			null, _DEFAULT_INTERVAL, TimeUnit.SECOND);

		_quartzSchedulerEngine.schedule(
			trigger, StringPool.BLANK, _TEST_DESTINATION_NAME, new Message(),
			StorageType.MEMORY);

		schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(
			_MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		Assert.assertEquals(
			schedulerResponses.toString(), _DEFAULT_JOB_NUMBER + 1,
			schedulerResponses.size());
	}

	@Test
	public void testSchedule2() throws Exception {
		List<SchedulerResponse> schedulerResponses =
			_quartzSchedulerEngine.getScheduledJobs(
				_MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		Assert.assertEquals(
			schedulerResponses.toString(), _DEFAULT_JOB_NUMBER,
			schedulerResponses.size());

		Trigger trigger = _quartzTriggerFactory.createTrigger(
			_TEST_JOB_NAME_PREFIX + "memory", _MEMORY_TEST_GROUP_NAME, null,
			null, _DEFAULT_INTERVAL, TimeUnit.SECOND);

		_quartzSchedulerEngine.schedule(
			trigger, StringPool.BLANK, _TEST_DESTINATION_NAME, new Message(),
			StorageType.MEMORY);

		schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(
			_MEMORY_TEST_GROUP_NAME, StorageType.MEMORY);

		Assert.assertEquals(
			schedulerResponses.toString(), _DEFAULT_JOB_NUMBER + 1,
			schedulerResponses.size());
	}

	private void _assertTriggerState(
		SchedulerResponse schedulerResponse,
		TriggerState expectedTriggerState) {

		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		Assert.assertEquals(expectedTriggerState, jobState.getTriggerState());
	}

	private JSONFactory _setUpJSONFactory() {
		_jsonFactory = Mockito.mock(JSONFactory.class);

		Mockito.when(
			_jsonFactory.deserialize(Mockito.anyString())
		).then(
			new Answer<Object>() {

				@Override
				public Object answer(InvocationOnMock invocationOnMock)
					throws Throwable {

					String base64 = (String)invocationOnMock.getArguments()[0];

					byte[] bytes = Base64.decode(base64);

					ByteArrayInputStream byteArrayInputStream =
						new ByteArrayInputStream(bytes);

					ObjectInputStream objectInputStream = new ObjectInputStream(
						byteArrayInputStream);

					return objectInputStream.readObject();
				}

			}
		);

		Mockito.when(
			_jsonFactory.serialize(Mockito.any())
		).then(
			new Answer<String>() {

				@Override
				public String answer(InvocationOnMock invocationOnMock)
					throws Throwable {

					Object object = invocationOnMock.getArguments()[0];

					ByteArrayOutputStream byteArrayOutputStream =
						new ByteArrayOutputStream();

					ObjectOutputStream objectOutputStream =
						new ObjectOutputStream(byteArrayOutputStream);

					objectOutputStream.writeObject(object);

					byte[] bytes = byteArrayOutputStream.toByteArray();

					objectOutputStream.close();

					return Base64.encode(bytes);
				}

			}
		);

		return _jsonFactory;
	}

	private void _setUpPortalUUIDUtil() {
		_portalUUIDUtilMockedStatic.when(
			PortalUUIDUtil::generate
		).then(
			new Answer<String>() {

				@Override
				public String answer(InvocationOnMock invocationOnMock)
					throws Throwable {

					UUID uuid = new UUID(
						SecureRandomUtil.nextLong(),
						SecureRandomUtil.nextLong());

					return uuid.toString();
				}

			}
		);
	}

	private static final int _DEFAULT_INTERVAL = 10;

	private static final int _DEFAULT_JOB_NUMBER = 3;

	private static final String _MEMORY_TEST_GROUP_NAME = "memory.test.group";

	private static final String _PERSISTED_TEST_GROUP_NAME =
		"persisted.test.group";

	private static final String _TEST_DESTINATION_NAME = "liferay/test";

	private static final String _TEST_JOB_NAME_0 = "test.job.0";

	private static final String _TEST_JOB_NAME_PREFIX = "test.job.";

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);
	private static final MockedStatic<PortalUUIDUtil>
		_portalUUIDUtilMockedStatic = Mockito.mockStatic(PortalUUIDUtil.class);
	private static ServiceRegistration<SchedulerEngineHelper>
		_schedulerEngineHelperServiceRegistration;

	private JSONFactory _jsonFactory;
	private QuartzSchedulerEngine _quartzSchedulerEngine;
	private final QuartzTriggerFactory _quartzTriggerFactory =
		new QuartzTriggerFactory();

	private class MockScheduler implements Scheduler {

		public MockScheduler(StorageType storageType, String defaultGroupName) {
			for (int i = 0; i < _DEFAULT_JOB_NUMBER; i++) {
				Trigger trigger = _quartzTriggerFactory.createTrigger(
					_TEST_JOB_NAME_PREFIX + i, defaultGroupName, null, null,
					_DEFAULT_INTERVAL, TimeUnit.SECOND);

				addJob(
					_TEST_JOB_NAME_PREFIX + i, defaultGroupName, storageType,
					(org.quartz.Trigger)trigger.getWrappedTrigger());
			}
		}

		@Override
		public void addCalendar(
			String name, Calendar calendar, boolean replace,
			boolean updateTriggers) {
		}

		@Override
		public void addJob(JobDetail jobDetail, boolean replace) {
			Tuple tuple = _jobs.get(jobDetail.getKey());

			if (tuple == null) {
				_jobs.put(
					jobDetail.getKey(),
					new Tuple(jobDetail, null, TriggerState.NORMAL));
			}
			else {
				_jobs.put(
					jobDetail.getKey(),
					new Tuple(
						jobDetail, tuple.getObject(1), tuple.getObject(2)));
			}
		}

		public final void addJob(
			String jobName, String groupName, StorageType storageType,
			org.quartz.Trigger trigger) {

			JobKey jobKey = new JobKey(jobName, groupName);

			JobBuilder jobBuilder = JobBuilder.newJob(MessageSenderJob.class);

			jobBuilder = jobBuilder.withIdentity(jobKey);

			JobDetail jobDetail = jobBuilder.build();

			JobDataMap jobDataMap = jobDetail.getJobDataMap();

			jobDataMap.put(
				SchedulerEngine.MESSAGE, _jsonFactory.serialize(new Message()));
			jobDataMap.put(
				SchedulerEngine.DESTINATION_NAME, _TEST_DESTINATION_NAME);
			jobDataMap.put(
				SchedulerEngine.STORAGE_TYPE, storageType.toString());

			JobState jobState = new JobState(TriggerState.NORMAL);

			jobDataMap.put(
				SchedulerEngine.JOB_STATE,
				JobStateSerializeUtil.serialize(jobState));

			_jobs.put(
				jobKey, new Tuple(jobDetail, trigger, TriggerState.NORMAL));
		}

		@Override
		public boolean checkExists(JobKey jobKey) {
			return false;
		}

		@Override
		public boolean checkExists(TriggerKey triggerKey) {
			return false;
		}

		@Override
		public void clear() {
		}

		@Override
		public boolean deleteCalendar(String name) {
			return false;
		}

		@Override
		public boolean deleteJob(JobKey jobKey) {
			_jobs.remove(jobKey);

			return true;
		}

		@Override
		public boolean deleteJobs(List<JobKey> jobKeys) {
			return false;
		}

		@Override
		public Calendar getCalendar(String name) {
			return null;
		}

		@Override
		public List<String> getCalendarNames() {
			return Collections.emptyList();
		}

		@Override
		public SchedulerContext getContext() {
			return null;
		}

		@Override
		public List<JobExecutionContext> getCurrentlyExecutingJobs() {
			return Collections.emptyList();
		}

		@Override
		public JobDetail getJobDetail(JobKey jobKey) {
			Tuple tuple = _jobs.get(jobKey);

			if (tuple == null) {
				return null;
			}

			return (JobDetail)tuple.getObject(0);
		}

		@Override
		public List<String> getJobGroupNames() {
			List<String> groupNames = new ArrayList<>();

			for (JobKey jobKey : _jobs.keySet()) {
				if (!groupNames.contains(jobKey.getGroup())) {
					groupNames.add(jobKey.getGroup());
				}
			}

			return groupNames;
		}

		@Override
		public Set<JobKey> getJobKeys(GroupMatcher<JobKey> groupMatcher) {
			Set<JobKey> jobKeys = new HashSet<>();

			String groupName = groupMatcher.getCompareToValue();

			for (JobKey jobKey : _jobs.keySet()) {
				if (Objects.equals(jobKey.getGroup(), groupName)) {
					jobKeys.add(jobKey);
				}
			}

			return jobKeys;
		}

		@Override
		public ListenerManager getListenerManager() {
			return null;
		}

		@Override
		public SchedulerMetaData getMetaData() {
			return null;
		}

		@Override
		public Set<String> getPausedTriggerGroups() {
			return null;
		}

		@Override
		public String getSchedulerInstanceId() {
			return null;
		}

		@Override
		public String getSchedulerName() {
			return null;
		}

		@Override
		public org.quartz.Trigger getTrigger(TriggerKey triggerKey) {
			Tuple tuple = _jobs.get(
				new JobKey(triggerKey.getName(), triggerKey.getGroup()));

			if (tuple == null) {
				return null;
			}

			return (org.quartz.Trigger)tuple.getObject(1);
		}

		@Override
		public List<String> getTriggerGroupNames() {
			return Collections.emptyList();
		}

		@Override
		public Set<TriggerKey> getTriggerKeys(
			GroupMatcher<TriggerKey> groupMatcher) {

			return null;
		}

		@Override
		public List<? extends org.quartz.Trigger> getTriggersOfJob(
			JobKey jobKey) {

			return Collections.emptyList();
		}

		@Override
		public org.quartz.Trigger.TriggerState getTriggerState(
			TriggerKey triggerKey) {

			return null;
		}

		@Override
		public boolean interrupt(JobKey jobKey) {
			return false;
		}

		@Override
		public boolean interrupt(String fireInstanceId) {
			return false;
		}

		@Override
		public boolean isInStandbyMode() {
			return false;
		}

		@Override
		public boolean isShutdown() {
			return !_ready;
		}

		@Override
		public boolean isStarted() {
			return _ready;
		}

		@Override
		public void pauseAll() {
		}

		@Override
		public void pauseJob(JobKey jobKey) {
			Tuple tuple = _jobs.get(jobKey);

			if (tuple == null) {
				return;
			}

			_jobs.put(
				jobKey,
				new Tuple(
					tuple.getObject(0), tuple.getObject(1),
					TriggerState.PAUSED));
		}

		@Override
		public void pauseJobs(GroupMatcher<JobKey> groupMatcher) {
			String groupName = groupMatcher.getCompareToValue();

			for (JobKey jobKey : _jobs.keySet()) {
				if (Objects.equals(jobKey.getGroup(), groupName)) {
					pauseJob(jobKey);
				}
			}
		}

		@Override
		public void pauseTrigger(TriggerKey triggerKey) {
		}

		@Override
		public void pauseTriggers(GroupMatcher<TriggerKey> groupMatcher) {
		}

		@Override
		public Date rescheduleJob(
			TriggerKey triggerKey, org.quartz.Trigger trigger) {

			JobKey jobKey = new JobKey(
				triggerKey.getName(), triggerKey.getGroup());

			Tuple tuple = _jobs.get(jobKey);

			if (tuple == null) {
				return null;
			}

			_jobs.put(
				jobKey,
				new Tuple(tuple.getObject(0), trigger, tuple.getObject(2)));

			return null;
		}

		@Override
		public void resumeAll() {
		}

		@Override
		public void resumeJob(JobKey jobKey) {
			Tuple tuple = _jobs.get(jobKey);

			if (tuple == null) {
				return;
			}

			_jobs.put(
				jobKey,
				new Tuple(
					tuple.getObject(0), tuple.getObject(1),
					TriggerState.NORMAL));
		}

		@Override
		public void resumeJobs(GroupMatcher<JobKey> groupMatcher) {
			String groupName = groupMatcher.getCompareToValue();

			for (JobKey jobKey : _jobs.keySet()) {
				if (Objects.equals(jobKey.getGroup(), groupName)) {
					resumeJob(jobKey);
				}
			}
		}

		@Override
		public void resumeTrigger(TriggerKey triggerKey) {
		}

		@Override
		public void resumeTriggers(GroupMatcher<TriggerKey> groupMatcher) {
		}

		@Override
		public Date scheduleJob(
			JobDetail jobDetail, org.quartz.Trigger trigger) {

			_jobs.put(
				jobDetail.getKey(),
				new Tuple(jobDetail, trigger, TriggerState.NORMAL));

			return null;
		}

		@Override
		public Date scheduleJob(org.quartz.Trigger trigger) {
			return null;
		}

		@Override
		public void scheduleJobs(
			Map<JobDetail, List<org.quartz.Trigger>> map, boolean replace) {
		}

		@Override
		public void setJobFactory(JobFactory jobFactory) {
		}

		@Override
		public void shutdown() {
			_ready = false;
		}

		@Override
		public void shutdown(boolean waitForJobsToComplete) {
			_ready = false;
		}

		@Override
		public void standby() {
		}

		@Override
		public void start() {
			_ready = true;
		}

		@Override
		public void startDelayed(int seconds) {
		}

		@Override
		public void triggerJob(JobKey jobKey) {
		}

		@Override
		public void triggerJob(JobKey jobKey, JobDataMap jobDataMap) {
		}

		@Override
		public boolean unscheduleJob(TriggerKey triggerKey) {
			_jobs.remove(
				new JobKey(triggerKey.getName(), triggerKey.getGroup()));

			return true;
		}

		@Override
		public boolean unscheduleJobs(List<TriggerKey> list) {
			return false;
		}

		private final Map<JobKey, Tuple> _jobs = new HashMap<>();
		private boolean _ready;

	}

}