/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.exception.DispatchTriggerDispatchTaskExecutorTypeException;
import com.liferay.dispatch.exception.DispatchTriggerNameException;
import com.liferay.dispatch.exception.DispatchTriggerSchedulerException;
import com.liferay.dispatch.exception.DuplicateDispatchTriggerException;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.internal.messaging.TestDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.service.test.util.CronExpressionUtil;
import com.liferay.dispatch.service.test.util.DispatchTriggerTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
public class DispatchTriggerLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testAddDispatchTriggerExceptions() throws Exception {
		User user = UserTestUtil.addUser();

		_addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, _getRandomDispatchExecutorType(), 1));

		Class<?> exceptionClass = Exception.class;

		try {
			_addDispatchTrigger(
				DispatchTriggerTestUtil.randomDispatchTrigger(
					user, _getRandomDispatchExecutorType(), 1));
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Add dispatch trigger with existing name",
			DuplicateDispatchTriggerException.class, exceptionClass);

		try {
			_addDispatchTrigger(
				DispatchTriggerTestUtil.randomDispatchTrigger(
					user, _getRandomDispatchExecutorType(), -1));
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Add dispatch trigger with no name",
			DispatchTriggerNameException.class, exceptionClass);

		try {
			_addDispatchTrigger(
				DispatchTriggerTestUtil.randomDispatchTrigger(
					user, "INVALID EXECUTOR TYPE", 2));
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Add dispatch trigger with invalid executor type",
			DispatchTriggerDispatchTaskExecutorTypeException.class,
			exceptionClass);
	}

	@Test
	public void testAddDispatchTriggerWithCustomTimeZone() throws Exception {
		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, user.getUserId(),
				TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomBoolean());

		Assert.assertNull(
			_dispatchTriggerLocalService.fetchPreviousFireDate(Long.MIN_VALUE));
		Assert.assertNull(
			_dispatchTriggerLocalService.fetchPreviousFireDate(
				dispatchTrigger.getDispatchTriggerId()));

		String dateString = "7/20/22 02:00:00 AM";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"M/d/yy hh:mm:ss a");

		Date date = simpleDateFormat.parse(dateString);

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		String timeZoneId = "Europe/Paris";

		dispatchTrigger = _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId(), true, "0 0 * * * ? *",
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode()),
			0, 0, 0, 0, 0, true, false, calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			timeZoneId);

		TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

		Assert.assertEquals(
			dispatchTrigger.getStartDate(),
			new Date(date.getTime() - timeZone.getOffset(date.getTime())));

		Assert.assertEquals(dispatchTrigger.getTimeZoneStartDate(), date);

		String liferayMode = SystemProperties.get("liferay.mode");

		try {
			SystemProperties.clear("liferay.mode");

			_dispatchTriggerLocalService.deleteDispatchTrigger(dispatchTrigger);

			if (dispatchTrigger.isSystem()) {
				Assert.assertNotNull(
					_dispatchTriggerLocalService.fetchDispatchTrigger(
						dispatchTrigger.getDispatchTriggerId()));
			}
			else {
				Assert.assertNull(
					_dispatchTriggerLocalService.fetchDispatchTrigger(
						dispatchTrigger.getDispatchTriggerId()));
			}
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}
	}

	@Test
	public void testDeleteDispatchTriggerWithDispatchLogs() throws Exception {
		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, _getRandomDispatchExecutorType(), 1));

		for (int i = 0; i < 3; i++) {
			Date date = new Date();

			Date startDate = new Date(
				date.getTime() - Time.WEEK + (Time.HOUR * i));

			Date endDate = new Date(
				date.getTime() - Time.WEEK + (Time.HOUR * i) + Time.MINUTE);

			_dispatchLogLocalService.addDispatchLog(
				user.getUserId(), dispatchTrigger.getDispatchTriggerId(),
				endDate, null, RandomTestUtil.randomString(), startDate,
				DispatchTaskStatus.SUCCESSFUL);
		}

		EntityCache originalEntityCache = ReflectionTestUtil.getFieldValue(
			_dispatchLogLocalService.getBasePersistence(), "entityCache");

		MockEntityCache mockEntityCache = new MockEntityCache(
			originalEntityCache);

		try {
			ReflectionTestUtil.setFieldValue(
				_dispatchLogLocalService.getBasePersistence(), "entityCache",
				mockEntityCache);

			_dispatchTriggerLocalService.deleteDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dispatchLogLocalService.getBasePersistence(), "entityCache",
				originalEntityCache);
		}

		// Verify that the cache was not invalidated individually for each
		// dispatch log deleted

		Assert.assertEquals(0, mockEntityCache.getRemoveCount());

		Assert.assertEquals(
			0,
			_dispatchLogLocalService.getDispatchLogsCount(
				dispatchTrigger.getDispatchTriggerId()));
		Assert.assertEquals(
			0,
			_dispatchTriggerLocalService.getUserDispatchTriggersCount(
				user.getCompanyId(), user.getUserId()));
	}

	@Test
	public void testGetUserDispatchTriggers() throws Exception {
		Map<User, Integer> userDispatchTriggersCounts = new HashMap<>();

		for (int i = 0; i < 3; i++) {
			User user = UserTestUtil.addUser();

			int dispatchTriggersCount = RandomTestUtil.randomInt(5, 15);

			userDispatchTriggersCounts.put(user, dispatchTriggersCount);

			while (dispatchTriggersCount-- > 0) {
				_addDispatchTrigger(
					DispatchTriggerTestUtil.randomDispatchTrigger(
						user, _getRandomDispatchExecutorType(),
						RandomTestUtil.nextInt()));
			}
		}

		for (Map.Entry<User, Integer> userDispatchTriggersCountEntry :
				userDispatchTriggersCounts.entrySet()) {

			User user = userDispatchTriggersCountEntry.getKey();
			Integer count = userDispatchTriggersCountEntry.getValue();

			Assert.assertEquals(
				count.intValue(),
				_dispatchTriggerLocalService.getUserDispatchTriggersCount(
					user.getCompanyId(), user.getUserId()));

			List<DispatchTrigger> userDispatchTriggers =
				_dispatchTriggerLocalService.getUserDispatchTriggers(
					user.getCompanyId(), user.getUserId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			for (DispatchTrigger dispatchTrigger : userDispatchTriggers) {
				Assert.assertEquals(
					user.getUserId(), dispatchTrigger.getUserId());
				Assert.assertEquals(
					DispatchTaskStatus.NEVER_RAN,
					dispatchTrigger.getDispatchTaskStatus());
			}
		}
	}

	@Test
	public void testUpdateDispatchTrigger() throws Exception {
		User user = UserTestUtil.addUser();

		DispatchTrigger expectedDispatchTrigger =
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, _getRandomDispatchExecutorType(), 1);

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			expectedDispatchTrigger);

		_basicAssertEquals(expectedDispatchTrigger, dispatchTrigger);

		expectedDispatchTrigger = DispatchTriggerTestUtil.randomDispatchTrigger(
			expectedDispatchTrigger, 1);

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				expectedDispatchTrigger.getDispatchTaskClusterMode());

		try {
			dispatchTrigger =
				_dispatchTriggerLocalService.updateDispatchTrigger(
					dispatchTrigger.getDispatchTriggerId(),
					expectedDispatchTrigger.isActive(),
					expectedDispatchTrigger.getCronExpression(),
					dispatchTaskClusterMode, CronExpressionUtil.getMonth() + 1,
					20, CronExpressionUtil.getYear(), 23, 59, false, true,
					CronExpressionUtil.getMonth() - 1, 1,
					CronExpressionUtil.getYear(), 0, 0, "UTC");

			_basicAssertEquals(expectedDispatchTrigger, dispatchTrigger);

			_advancedAssertEquals(expectedDispatchTrigger, dispatchTrigger);
		}
		catch (Exception exception) {
			if (!(exception instanceof DispatchTriggerSchedulerException)) {
				throw exception;
			}

			Assert.assertNull(
				_schedulerEngineHelper.getScheduledJob(
					_getJobName(dispatchTrigger),
					_getGroupName(dispatchTrigger),
					dispatchTaskClusterMode.getStorageType()));
		}
	}

	@Test
	public void testUpdateDispatchTriggerExceptions() throws Exception {
		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger1 = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, _getRandomDispatchExecutorType(), 1));
		DispatchTrigger dispatchTrigger2 = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, _getRandomDispatchExecutorType(), 2));

		Class<?> exceptionClass = Exception.class;

		try {
			_dispatchTriggerLocalService.updateDispatchTrigger(
				dispatchTrigger1.getDispatchTriggerId(),
				dispatchTrigger1.getDispatchTaskSettingsUnicodeProperties(),
				dispatchTrigger2.getName());
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Update dispatch trigger with existing name",
			DuplicateDispatchTriggerException.class, exceptionClass);

		try {
			_dispatchTriggerLocalService.updateDispatchTrigger(
				dispatchTrigger1.getDispatchTriggerId(),
				dispatchTrigger1.getDispatchTaskSettingsUnicodeProperties(),
				null);
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Update dispatch trigger with no name",
			DispatchTriggerNameException.class, exceptionClass);
	}

	@Test
	public void testUpdateDispatchTriggerWhenMultiplePortalInstancesPresent()
		throws Exception {

		User user1 = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger1 = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user1, _getRandomDispatchExecutorType(), 1));

		Company company = CompanyTestUtil.addCompany();

		User user2 = UserTestUtil.addUser(company);

		DispatchTrigger dispatchTrigger2 = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user2, _getRandomDispatchExecutorType(), 1));

		Assert.assertEquals(
			dispatchTrigger1.getName(), dispatchTrigger2.getName());

		dispatchTrigger2 = _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger2.getDispatchTriggerId(),
			dispatchTrigger1.getDispatchTaskSettingsUnicodeProperties(),
			dispatchTrigger1.getName());

		Assert.assertEquals(
			dispatchTrigger1.getName(), dispatchTrigger2.getName());
	}

	@Test
	public void testUpdateDispatchTriggerWithCronExpressions()
		throws Exception {

		// Future start date after the cron expression

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(new Date());

		Calendar futureCalendar = (Calendar)calendar.clone();

		futureCalendar.add(Calendar.HOUR_OF_DAY, 12);

		String cronExpression = StringBundler.concat(
			futureCalendar.get(Calendar.SECOND), " ",
			futureCalendar.get(Calendar.MINUTE), " ",
			futureCalendar.get(Calendar.HOUR_OF_DAY), " * * ? *");

		Calendar startCalendar = (Calendar)calendar.clone();

		startCalendar.add(Calendar.HOUR_OF_DAY, 14);

		_testUpdateDispatchTriggerWithCronExpressions(
			cronExpression, _getExpectedCalendar(futureCalendar, startCalendar),
			startCalendar);

		// Future start date before the cron expression

		startCalendar = (Calendar)calendar.clone();

		startCalendar.add(Calendar.HOUR_OF_DAY, 10);

		_testUpdateDispatchTriggerWithCronExpressions(
			cronExpression, _getExpectedCalendar(futureCalendar, startCalendar),
			startCalendar);

		// Past start Date after the cron expression

		startCalendar = (Calendar)calendar.clone();

		startCalendar.add(Calendar.DAY_OF_MONTH, -1);
		startCalendar.add(Calendar.HOUR_OF_DAY, 14);

		_testUpdateDispatchTriggerWithCronExpressions(
			cronExpression, _getExpectedCalendar(futureCalendar, startCalendar),
			startCalendar);

		// Past start date before the cron expression

		startCalendar = (Calendar)calendar.clone();

		startCalendar.add(Calendar.DAY_OF_MONTH, -1);

		_testUpdateDispatchTriggerWithCronExpressions(
			cronExpression, _getExpectedCalendar(futureCalendar, startCalendar),
			startCalendar);
	}

	@Test
	public void testUpdateDispatchTriggerWithDifferentDispatchTaskClusterMode()
		throws Exception {

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				UserTestUtil.addUser(), _getRandomDispatchExecutorType(), 1));

		dispatchTrigger = _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId(), true,
			CronExpressionUtil.getCronExpression(),
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode()),
			CronExpressionUtil.getMonth() + 1, 20, CronExpressionUtil.getYear(),
			23, 59, false, true, CronExpressionUtil.getMonth() - 1, 1,
			CronExpressionUtil.getYear(), 0, 0, "UTC");

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode());

		DispatchTrigger updateDispatchTrigger =
			_dispatchTriggerLocalService.updateDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId(), true,
				CronExpressionUtil.getCronExpression(),
				DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED,
				CronExpressionUtil.getMonth() + 1, 20,
				CronExpressionUtil.getYear(), 23, 59, false, true,
				CronExpressionUtil.getMonth() - 1, 1,
				CronExpressionUtil.getYear(), 0, 0, "UTC");

		DispatchTaskClusterMode updateDispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				updateDispatchTrigger.getDispatchTaskClusterMode());

		Assert.assertEquals(
			DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED,
			updateDispatchTaskClusterMode);

		Assert.assertNull(
			_schedulerEngineHelper.getScheduledJob(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				dispatchTaskClusterMode.getStorageType()));

		Assert.assertNotNull(
			_schedulerEngineHelper.getScheduledJob(
				_getJobName(updateDispatchTrigger),
				_getGroupName(updateDispatchTrigger),
				updateDispatchTaskClusterMode.getStorageType()));
	}

	private DispatchTrigger _addDispatchTrigger(DispatchTrigger dispatchTrigger)
		throws Exception {

		return _dispatchTriggerLocalService.addDispatchTrigger(
			null, dispatchTrigger.getUserId(),
			dispatchTrigger.getDispatchTaskExecutorType(),
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties(),
			dispatchTrigger.getName(), dispatchTrigger.isSystem());
	}

	private void _advancedAssertEquals(
		DispatchTrigger expectedDispatchTrigger,
		DispatchTrigger actualDispatchTrigger) {

		Assert.assertEquals(
			expectedDispatchTrigger.isActive(),
			actualDispatchTrigger.isActive());
		Assert.assertEquals(
			expectedDispatchTrigger.getCronExpression(),
			actualDispatchTrigger.getCronExpression());
		Assert.assertNotNull(actualDispatchTrigger.getStartDate());

		DispatchTaskClusterMode expectedDispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				expectedDispatchTrigger.getDispatchTaskClusterMode());

		if ((expectedDispatchTaskClusterMode ==
				DispatchTaskClusterMode.ALL_NODES) &&
			_dispatchTaskExecutorRegistry.isClusterModeSingle(
				expectedDispatchTrigger.getDispatchTaskExecutorType())) {

			expectedDispatchTaskClusterMode =
				DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED;
		}

		Assert.assertEquals(
			expectedDispatchTaskClusterMode.getMode(),
			actualDispatchTrigger.getDispatchTaskClusterMode());

		DispatchLog dispatchLog =
			_dispatchLogLocalService.fetchLatestDispatchLog(
				actualDispatchTrigger.getDispatchTriggerId());

		DispatchTaskStatus dispatchTaskStatus = DispatchTaskStatus.NEVER_RAN;

		if (dispatchLog != null) {
			dispatchTaskStatus = DispatchTaskStatus.valueOf(
				dispatchLog.getStatus());
		}

		Assert.assertEquals(
			dispatchTaskStatus, actualDispatchTrigger.getDispatchTaskStatus());
	}

	private void _basicAssertEquals(
		DispatchTrigger expectedDispatchTrigger,
		DispatchTrigger actualDispatchTrigger) {

		Assert.assertNotNull(actualDispatchTrigger);
		Assert.assertEquals(
			expectedDispatchTrigger.getUserId(),
			actualDispatchTrigger.getUserId());
		Assert.assertEquals(
			expectedDispatchTrigger.getName(), actualDispatchTrigger.getName());
		Assert.assertEquals(
			expectedDispatchTrigger.isSystem(),
			actualDispatchTrigger.isSystem());
		Assert.assertEquals(
			expectedDispatchTrigger.getDispatchTaskExecutorType(),
			actualDispatchTrigger.getDispatchTaskExecutorType());

		UnicodeProperties actualDispatchTaskSettingsUnicodeProperties =
			actualDispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		UnicodeProperties expectedDispatchTaskSettingsUnicodeProperties =
			expectedDispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		if (expectedDispatchTaskSettingsUnicodeProperties == null) {
			Assert.assertNull(actualDispatchTaskSettingsUnicodeProperties);

			return;
		}

		Assert.assertNotNull(actualDispatchTaskSettingsUnicodeProperties);

		Assert.assertEquals(
			expectedDispatchTaskSettingsUnicodeProperties.size(),
			actualDispatchTaskSettingsUnicodeProperties.size());

		actualDispatchTaskSettingsUnicodeProperties.forEach(
			(key, value) -> Assert.assertEquals(
				expectedDispatchTaskSettingsUnicodeProperties.getProperty(key),
				value));
	}

	private Calendar _getExpectedCalendar(
		Calendar futureCalendar, Calendar startCalendar) {

		Calendar calendar = (Calendar)futureCalendar.clone();

		calendar.set(Calendar.MILLISECOND, 0);

		if (startCalendar.compareTo(calendar) >= 0) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		return calendar;
	}

	private String _getGroupName(DispatchTrigger dispatchTrigger) {
		String dispatchTriggerId = String.format(
			"%07d", dispatchTrigger.getDispatchTriggerId());

		return StringBundler.concat(
			"DISPATCH_GROUP_", dispatchTriggerId, StringPool.AT,
			dispatchTrigger.getCompanyId());
	}

	private String _getJobName(DispatchTrigger dispatchTrigger) {
		String dispatchTriggerId = String.format(
			"%07d", dispatchTrigger.getDispatchTriggerId());

		return StringBundler.concat(
			"DISPATCH_JOB_", dispatchTriggerId, StringPool.AT,
			dispatchTrigger.getCompanyId());
	}

	private String _getRandomDispatchExecutorType() {
		Set<String> dispatchTaskExecutorTypes =
			_dispatchTaskExecutorRegistry.getDispatchTaskExecutorTypes();

		int index = 0;
		int randomIndex = RandomTestUtil.randomInt(
			0, dispatchTaskExecutorTypes.size() - 1);

		for (String dispatchTaskExecutorType : dispatchTaskExecutorTypes) {
			if (index++ == randomIndex) {
				return dispatchTaskExecutorType;
			}
		}

		return TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST;
	}

	private void _testUpdateDispatchTriggerWithCronExpressions(
			String cronExpression, Calendar expectedCalendar,
			Calendar startCalendar)
		throws Exception {

		DispatchTrigger dispatchTrigger = _addDispatchTrigger(
			DispatchTriggerTestUtil.randomDispatchTrigger(
				UserTestUtil.addUser(), _getRandomDispatchExecutorType(),
				RandomTestUtil.nextInt()));

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED;

		dispatchTrigger.setDispatchTaskClusterMode(
			dispatchTaskClusterMode.getMode());

		Calendar endCalendar = CalendarFactoryUtil.getCalendar();

		endCalendar.setTime(new Date());

		endCalendar.add(Calendar.YEAR, 1);

		dispatchTrigger = _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId(), true, cronExpression,
			dispatchTaskClusterMode, endCalendar.get(Calendar.MONTH),
			endCalendar.get(Calendar.DAY_OF_MONTH),
			endCalendar.get(Calendar.YEAR),
			endCalendar.get(Calendar.HOUR_OF_DAY),
			endCalendar.get(Calendar.MINUTE), false, true,
			startCalendar.get(Calendar.MONTH),
			startCalendar.get(Calendar.DAY_OF_MONTH),
			startCalendar.get(Calendar.YEAR),
			startCalendar.get(Calendar.HOUR_OF_DAY),
			startCalendar.get(Calendar.MINUTE), "UTC");

		Thread.sleep(1000);

		Assert.assertEquals(
			0,
			_dispatchLogLocalService.getDispatchLogsCount(
				dispatchTrigger.getDispatchTriggerId()));

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				dispatchTaskClusterMode.getStorageType());

		Assert.assertNotNull(schedulerResponse);

		Date date = _schedulerEngineHelper.getNextFireTime(schedulerResponse);

		Calendar nextFireCalendar = CalendarFactoryUtil.getCalendar();

		nextFireCalendar.setTime(date);

		Assert.assertEquals(expectedCalendar, nextFireCalendar);
	}

	@Inject
	private DispatchLogLocalService _dispatchLogLocalService;

	@Inject
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	private static class MockEntityCache implements EntityCache {

		public MockEntityCache(EntityCache entityCache) {
			_entityCache = entityCache;
		}

		@Override
		public void clearCache() {
			_entityCache.clearCache();
		}

		@Override
		public void clearCache(Class<?> clazz) {
			_entityCache.clearCache(clazz);
		}

		@Override
		public void clearLocalCache() {
			_entityCache.clearLocalCache();
		}

		@Override
		public <T extends CacheModel<?>> T fetchCacheModel(
			Class<?> clazz, Serializable primaryKey, Class<T> cacheModelClass) {

			return _entityCache.fetchCacheModel(
				clazz, primaryKey, cacheModelClass);
		}

		@Override
		public Serializable getLocalCacheResult(
			Class<?> clazz, Serializable primaryKey) {

			return _entityCache.getLocalCacheResult(clazz, primaryKey);
		}

		@Override
		public PortalCache<Serializable, Serializable> getPortalCache(
			Class<?> clazz) {

			return _entityCache.getPortalCache(clazz);
		}

		public int getRemoveCount() {
			return _removeCount;
		}

		@Override
		public Serializable getResult(Class<?> clazz, Serializable primaryKey) {
			return _entityCache.getResult(clazz, primaryKey);
		}

		@Override
		public void invalidate() {
			_entityCache.invalidate();
		}

		@Override
		public void putResult(
			Class<?> clazz, BaseModel<?> baseModel, boolean quiet,
			boolean updateFinderCache) {

			_entityCache.putResult(clazz, baseModel, quiet, updateFinderCache);
		}

		@Override
		public void putResult(
			Class<?> clazz, Serializable primaryKey, Serializable result) {

			_entityCache.putResult(clazz, primaryKey, result);
		}

		@Override
		public void removeCache(String className) {
			_entityCache.removeCache(className);
		}

		@Override
		public void removeResult(Class<?> clazz, BaseModel<?> baseModel) {
			if (baseModel instanceof DispatchLog) {
				_removeCount++;
			}

			_entityCache.removeResult(clazz, baseModel);
		}

		@Override
		public void removeResult(Class<?> clazz, Serializable primaryKey) {
			_entityCache.removeResult(clazz, primaryKey);
		}

		private final EntityCache _entityCache;
		private int _removeCount;

	}

}