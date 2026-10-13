/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.exception.LaunchSetScheduleException;
import com.liferay.launch.set.scheduler.LaunchSetScheduler;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class LaunchSetSchedulerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "LaunchSet");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_launchSetObjectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		_objectEntryLocalService.deleteObjectEntry(_launchSetObjectEntry);
	}

	@Test
	public void testFetchScheduledDate() throws Exception {

		// Missing launch set

		Assert.assertNull(
			_launchSetScheduler.fetchScheduledDate(
				RandomTestUtil.randomLong()));

		// Launch set that is not scheduled

		Assert.assertNull(
			_launchSetScheduler.fetchScheduledDate(
				_launchSetObjectEntry.getObjectEntryId()));

		// Scheduled launch set

		Date date = new Date(System.currentTimeMillis() + Time.DAY);

		_launchSetScheduler.schedulePublish(
			_launchSetObjectEntry.getObjectEntryId(),
			TestPropsValues.getUserId(), date);

		Assert.assertEquals(
			date,
			_launchSetScheduler.fetchScheduledDate(
				_launchSetObjectEntry.getObjectEntryId()));
	}

	@Test
	public void testSchedulePublish() throws Exception {
		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		// Without permission

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		try {
			Assert.assertThrows(
				PrincipalException.class,
				() -> _launchSetScheduler.schedulePublish(
					launchSetId, _user.getUserId(),
					new Date(System.currentTimeMillis() + Time.DAY)));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}

		_assertScheduledDate(null, WorkflowConstants.STATUS_DRAFT);

		// Date in the past

		Assert.assertThrows(
			LaunchSetScheduleException.MustHaveFutureDate.class,
			() -> _launchSetScheduler.schedulePublish(
				launchSetId, TestPropsValues.getUserId(),
				new Date(System.currentTimeMillis() - Time.MINUTE)));

		// Null date

		Assert.assertThrows(
			LaunchSetScheduleException.MustHaveFutureDate.class,
			() -> _launchSetScheduler.schedulePublish(
				launchSetId, TestPropsValues.getUserId(), null));

		// Future date

		Date date = new Date(System.currentTimeMillis() + Time.DAY);

		_launchSetScheduler.schedulePublish(
			launchSetId, TestPropsValues.getUserId(), date);

		_assertScheduledDate(date, WorkflowConstants.STATUS_SCHEDULED);

		// Already scheduled

		Assert.assertThrows(
			LaunchSetScheduleException.MustBeOpen.class,
			() -> _launchSetScheduler.schedulePublish(
				launchSetId, TestPropsValues.getUserId(),
				new Date(System.currentTimeMillis() + Time.DAY)));

		_assertScheduledDate(date, WorkflowConstants.STATUS_SCHEDULED);
	}

	@Test
	public void testUnschedulePublish() throws Exception {
		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		// Not scheduled

		_launchSetScheduler.unschedulePublish(launchSetId);

		_assertScheduledDate(null, WorkflowConstants.STATUS_DRAFT);

		// Scheduled

		_launchSetScheduler.schedulePublish(
			launchSetId, TestPropsValues.getUserId(),
			new Date(System.currentTimeMillis() + Time.DAY));

		_launchSetScheduler.unschedulePublish(launchSetId);

		_assertScheduledDate(null, WorkflowConstants.STATUS_DRAFT);

		// Scheduled again

		Date date = new Date(System.currentTimeMillis() + Time.WEEK);

		_launchSetScheduler.schedulePublish(
			launchSetId, TestPropsValues.getUserId(), date);

		_assertScheduledDate(date, WorkflowConstants.STATUS_SCHEDULED);
	}

	private void _assertScheduledDate(Date expectedDate, int expectedStatus)
		throws Exception {

		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(launchSetId);

		Assert.assertEquals(expectedStatus, launchSetObjectEntry.getStatus());

		Date scheduledDate = _launchSetScheduler.fetchScheduledDate(
			launchSetId);

		Map<String, Serializable> values = launchSetObjectEntry.getValues();

		Date scheduledDateValue = (Date)values.get("scheduledDate");

		if (expectedDate == null) {
			Assert.assertNull(scheduledDate);
			Assert.assertNull(scheduledDateValue);

			return;
		}

		Assert.assertEquals(expectedDate, scheduledDate);
		Assert.assertEquals(
			expectedDate.getTime() / Time.SECOND,
			scheduledDateValue.getTime() / Time.SECOND);
	}

	private ObjectEntry _launchSetObjectEntry;

	@Inject
	private LaunchSetScheduler _launchSetScheduler;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

}