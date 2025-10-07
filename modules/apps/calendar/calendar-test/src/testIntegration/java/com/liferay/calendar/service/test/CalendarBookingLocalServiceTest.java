/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.constants.CalendarBookingConstants;
import com.liferay.calendar.exception.CalendarBookingRecurrenceException;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.notification.NotificationTemplateType;
import com.liferay.calendar.notification.NotificationType;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarNotificationTemplateTestUtil;
import com.liferay.calendar.test.util.CalendarStagingTestUtil;
import com.liferay.calendar.test.util.CalendarTestUtil;
import com.liferay.calendar.test.util.CheckBookingsSchedulerJobConfigurationTestUtil;
import com.liferay.calendar.test.util.RecurrenceTestUtil;
import com.liferay.calendar.util.JCalendarUtil;
import com.liferay.calendar.util.RecurrenceUtil;
import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CalendarBookingLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		CheckBookingsSchedulerJobConfigurationTestUtil.setUp();
	}

	@After
	public void tearDown() {
		CheckBookingsSchedulerJobConfigurationTestUtil.tearDown();

		CalendarStagingTestUtil.cleanUp();
	}

	@Test
	public void testAddAllDayCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(
			_user, _losAngelesTimeZone, serviceContext);

		java.util.Calendar nowJCalendar = JCalendarUtil.getJCalendar(
			2017, java.util.Calendar.JANUARY, 5, 22, 0, 0, 0, _utcTimeZone);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addAllDayCalendarBooking(
				_user, calendar, nowJCalendar.getTimeInMillis(),
				nowJCalendar.getTimeInMillis(), serviceContext);

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			calendarBooking.getStartTime(), calendarBooking.getTimeZone());

		java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
			calendarBooking.getEndTime(), calendarBooking.getTimeZone());

		assertSameDay(nowJCalendar, startTimeJCalendar);

		assertSameDay(nowJCalendar, endTimeJCalendar);
	}

	@Test
	public void testAddCalendarBooking() throws Exception {
		Locale siteDefault = LocaleUtil.getSiteDefault();

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				serviceContext);

		Assert.assertEquals(
			LocaleUtil.toLanguageId(siteDefault),
			LocalizationUtil.getDefaultLanguageId(calendarBooking.getTitle()));
	}

	@Test
	public void testAddCalendarBookingDoesNotNotifyCreatorTwice()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		User invitingUser = UserTestUtil.addUser();

		Calendar calendar = CalendarTestUtil.addCalendar(
			invitingUser, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			_user, serviceContext);

		long startTime = System.currentTimeMillis() + (Time.MINUTE * 2);

		long endTime = startTime + Time.HOUR;

		long firstReminder = Time.MINUTE;

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addCalendarBooking(
				invitingUser, calendar,
				new long[] {invitedCalendar.getCalendarId()},
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), startTime, endTime,
				null, (int)firstReminder, NotificationType.EMAIL, 0,
				NotificationType.EMAIL, serviceContext);

		_calendarBookingLocalService.updateStatus(
			_user.getUserId(), getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		_calendarBookingLocalService.checkCalendarBookings();

		String mailMessageSubject = StringBundler.concat(
			"Calendar: Event Reminder for \"",
			calendarBooking.getTitle(LocaleUtil.getSiteDefault()),
			StringPool.QUOTE);

		assertMailSubjectCount(mailMessageSubject, 2);
	}

	@Test
	public void testAddCalendarBookingResourceRequested()
		throws PortalException {

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar);

		Assert.assertTrue(firstChildCalendarBooking.isPending());

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar);

		Assert.assertTrue(secondChildCalendarBooking.isDenied());
	}

	@Test
	public void testAddCalendarBookingResourceRequestedEndOverlapsStart()
		throws PortalException {

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar);

		Assert.assertTrue(firstChildCalendarBooking.isPending());

		long startTime = firstChildCalendarBooking.getEndTime();

		long endTime = startTime + Time.HOUR;

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isPending());
	}

	@Test
	public void testAddCalendarBookingResourceRequestedNotifiesDenial()
		throws Exception {

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar);

		CalendarBookingTestUtil.addChildCalendarBooking(
			calendar, resourceCalendar,
			firstChildCalendarBooking.getStartTime(),
			firstChildCalendarBooking.getEndTime());

		String messageBodySnippet = "has declined this invitation";

		Assert.assertTrue(
			MailServiceTestUtil.lastMailMessageContains(messageBodySnippet));
	}

	@Test
	public void testAddCalendarBookingResourceRequestedNotifiesInvitees()
		throws Exception {

		User invitingUser = UserTestUtil.addUser();

		Calendar invintingCalendar = CalendarTestUtil.addCalendar(invitingUser);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		User resourceUser = UserTestUtil.addUser();

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(resourceUser);

		CalendarBooking firstChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invintingCalendar, resourceCalendar);

		CalendarBookingTestUtil.addMasterCalendarBooking(
			_user, invintingCalendar,
			new long[] {
				invitedCalendar.getCalendarId(),
				resourceCalendar.getCalendarId()
			},
			firstChildCalendarBooking.getStartTime(),
			firstChildCalendarBooking.getEndTime(), createServiceContext());

		assertSentEmail(_user);
		assertSentEmail(invitingUser);
	}

	@Test
	public void testAddCalendarBookingResourceRequestedOverlappingStart()
		throws PortalException {

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar);

		Assert.assertTrue(firstChildCalendarBooking.isPending());

		long startTime =
			firstChildCalendarBooking.getStartTime() +
				(firstChildCalendarBooking.getDuration() / 2);
		long endTime =
			firstChildCalendarBooking.getEndTime() +
				(firstChildCalendarBooking.getDuration() / 2);

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isDenied());
	}

	@Test
	public void testAddCalendarBookingResourceRequestedStartOverlapsEnd()
		throws PortalException {

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar);

		Assert.assertTrue(firstChildCalendarBooking.isPending());

		long endTime = firstChildCalendarBooking.getStartTime();

		long startTime = endTime - Time.HOUR;

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isPending());
	}

	@Test
	public void testAddCalendarBookingResourceReserved()
		throws PortalException {

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstCalendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(resourceCalendar);

		Assert.assertTrue(firstCalendarBooking.isApproved());

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, firstCalendarBooking.getStartTime(),
				firstCalendarBooking.getEndTime());

		Assert.assertTrue(secondChildCalendarBooking.isDenied());
	}

	@Test
	public void testAddCalendarBookingResourceReservedEndOverlapsStart()
		throws PortalException {

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstCalendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(resourceCalendar);

		Assert.assertTrue(firstCalendarBooking.isApproved());

		long endTime = firstCalendarBooking.getStartTime();

		long startTime = endTime - Time.HOUR;

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isPending());
	}

	@Test
	public void testAddCalendarBookingResourceReservedOverlappingEnd()
		throws PortalException {

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstCalendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(resourceCalendar);

		Assert.assertTrue(firstCalendarBooking.isApproved());

		long startTime = firstCalendarBooking.getStartTime() - (Time.HOUR / 2);
		long endTime = firstCalendarBooking.getEndTime() - (Time.HOUR / 2);

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isDenied());
	}

	@Test
	public void testAddCalendarBookingResourceReservedOverlappingStart()
		throws PortalException {

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstCalendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(resourceCalendar);

		Assert.assertTrue(firstCalendarBooking.isApproved());

		long startTime = firstCalendarBooking.getStartTime() + (Time.HOUR / 2);
		long endTime = firstCalendarBooking.getEndTime() + (Time.HOUR / 2);

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isDenied());
	}

	@Test
	public void testAddCalendarBookingResourceReservedStartOverlapsEnd()
		throws PortalException {

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstCalendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(resourceCalendar);

		Assert.assertTrue(firstCalendarBooking.isApproved());

		long startTime = firstCalendarBooking.getEndTime();

		long endTime = startTime + Time.HOUR;

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isPending());
	}

	@Test
	public void testAddCalendarBookingResourseRequestedOverlappingEnd()
		throws PortalException {

		Calendar calendar = CalendarTestUtil.addCalendar(_user);

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking firstChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar);

		Assert.assertTrue(firstChildCalendarBooking.isPending());

		long startTime =
			firstChildCalendarBooking.getStartTime() -
				(firstChildCalendarBooking.getDuration() / 2);
		long endTime =
			firstChildCalendarBooking.getEndTime() -
				(firstChildCalendarBooking.getDuration() / 2);

		CalendarBooking secondChildCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, resourceCalendar, startTime, endTime);

		Assert.assertTrue(secondChildCalendarBooking.isDenied());
	}

	@FeatureFlag("LPD-31212")
	@Test
	public void testAddCalendarBookingWithVideoDescription() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		String html = StringBundler.concat(
			"<div class=\"embed-responsive embed-responsive-16by9\" ",
			"data-embed-id=",
			"\"https://www.youtube.com/embed/6LjQ7Z99N74?rel=0\" ",
			"data-styles=\"{&quot;width&quot;:&quot;81%&quot;}\" ",
			"style=\"width:81%\"><iframe allow=\"autoplay; encrypted-media\" ",
			"allowfullscreen=\"\" frameborder=\"0\" height=\"315\" src=",
			"\"https://www.youtube.com/embed/6LjQ7Z99N74?rel=0\" width=\"",
			"560\"></iframe></div><p>&nbsp;</p>");

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addCalendarBooking(
				_user, calendar, new long[0],
				RandomTestUtil.randomLocaleStringMap(),
				HashMapBuilder.create(
					HashMapBuilder.put(
						LocaleUtil.getDefault(),
						html + "<script type=\"text/javascript\">alert('xss " +
							"vulnerability test');</script>"
					).build()
				).build(),
				startTime, startTime + (Time.HOUR * 10), null, (int)startTime,
				NotificationType.EMAIL, 0, NotificationType.EMAIL,
				serviceContext);

		String sanitizedVulnerability =
			"<script type=\"text/javascript\">;</script>";

		Assert.assertEquals(
			html + sanitizedVulnerability,
			calendarBooking.getDescription(LocaleUtil.getDefault()));
	}

	@Test
	public void testAddRecurringCalendarBookingAfterDeletingRecurringCalendarBookingInstance()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		long calendarBookingId = calendarBooking.getCalendarBookingId();

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			_user.getUserId(), calendarBooking, 3, false, true);

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBookingId);

		Assert.assertNotNull(calendarBooking);

		Recurrence recurrence = calendarBooking.getRecurrenceObj();

		Recurrence newRecurrence = recurrence.clone();

		int recurrenceCount = 4;

		newRecurrence.setCount(recurrenceCount);

		CalendarBooking newCalendarBooking =
			_calendarBookingLocalService.getCalendarBookingInstance(
				calendarBookingId, 2);

		newCalendarBooking =
			CalendarBookingTestUtil.
				updateRecurringCalendarBookingInstanceAndAllFollowing(
					_user, newCalendarBooking, 0,
					newCalendarBooking.getTitleMap(),
					newCalendarBooking.getDescriptionMap(),
					newCalendarBooking.getStartTime(),
					newCalendarBooking.getEndTime(),
					RecurrenceSerializer.serialize(newRecurrence),
					serviceContext);

		assertCalendarBookingInstancesCount(
			newCalendarBooking.getCalendarBookingId(), recurrenceCount);
	}

	@Test
	public void testAddRecurringCalendarBookingUntilStartTime()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(
			_user, _losAngelesTimeZone, serviceContext);

		java.util.Calendar startTimeJCalendar = CalendarFactoryUtil.getCalendar(
			2017, java.util.Calendar.JANUARY, 1, 20, 0, 0, 0,
			_losAngelesTimeZone);

		java.util.Calendar untilJCalendar =
			(java.util.Calendar)startTimeJCalendar.clone();

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence(
			_losAngelesTimeZone, untilJCalendar);

		long startTime = startTimeJCalendar.getTimeInMillis();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				recurrence, serviceContext);

		assertCalendarBookingInstancesCount(
			calendarBooking.getCalendarBookingId(), 1);
	}

	@Test
	public void testDeleteCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, titleMap, serviceContext);

		_calendarBookingLocalService.deleteCalendarBooking(calendarBooking);

		calendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getCalendarBookingId());

		Assert.assertEquals(titleMap, calendarBookingInstance.getTitleMap());
	}

	@Test
	public void testDeleteCalendarBookingRecurringInstanceNotifiesInvitees()
		throws Exception {

		User invitingUser = UserTestUtil.addUser();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(invitingUser);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				invitingCalendar, invitedCalendar,
				RecurrenceTestUtil.getDailyRecurrence(5));

		long calendarBookingId = calendarBooking.getCalendarBookingId();

		CalendarBooking calendarBookingInstance =
			RecurrenceUtil.getCalendarBookingInstance(calendarBooking, 3);

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			invitingUser.getUserId(), calendarBooking,
			calendarBookingInstance.getStartTime(), true, false);

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBookingId);

		Assert.assertNotNull(calendarBooking);

		String mailMessageSubject = StringBundler.concat(
			"Calendar: Event Update for \"",
			calendarBooking.getTitle(LocaleUtil.getSiteDefault()),
			StringPool.QUOTE);

		assertMailSubjectCount(mailMessageSubject, 1);

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			invitingUser.getUserId(), calendarBooking, 2, true);

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			invitingUser.getUserId(), calendarBooking, 1, false);

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBookingId);

		Assert.assertNotNull(calendarBooking);

		mailMessageSubject = StringBundler.concat(
			"Calendar: Event Deletion for \"",
			calendarBooking.getTitle(LocaleUtil.getSiteDefault()),
			StringPool.QUOTE);

		assertMailSubjectCount(mailMessageSubject, 2);

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			_user.getUserId(), calendarBooking, 0, false);

		Assert.assertNull(
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingId));
	}

	@Test
	public void testDeleteCalendarBookingWithAllFollowingInstances()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, titleMap, serviceContext);

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			_user.getUserId(), calendarBooking, 1, true, true);

		calendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getCalendarBookingId());

		Assert.assertNull(calendarBookingInstance);

		assertCalendarBookingInstancesCount(
			calendarBooking.getCalendarBookingId(), 1);
	}

	@Test
	public void testDeleteCalendarBookingWithAllRecurringInstances()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, titleMap, serviceContext);

		_calendarBookingLocalService.deleteCalendarBooking(
			calendarBooking, true);

		calendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getCalendarBookingId());

		Assert.assertNull(calendarBookingInstance);
	}

	@Test
	public void testDeleteCalendarBookingWithoutAllRecurringInstances()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, titleMap, serviceContext);

		_calendarBookingLocalService.deleteCalendarBooking(
			calendarBooking, false);

		calendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getCalendarBookingId());

		Assert.assertEquals(titleMap, calendarBookingInstance.getTitleMap());
	}

	@Test
	public void testDeleteLastCalendarBookingInstanceDeletesCalendarBooking()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_user, RecurrenceTestUtil.getDailyRecurrence(2),
				serviceContext);

		long calendarBookingId = calendarBooking.getCalendarBookingId();

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			_user.getUserId(), calendarBooking, 0, false);

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBookingId);

		Assert.assertNotNull(calendarBooking);

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			_user.getUserId(), calendarBooking, 0, false);

		Assert.assertNull(
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingId));
	}

	@Test
	public void testDeleteStagingCalendarBookingDeletesLiveCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(stagingCalendar);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 1);

		_calendarBookingLocalService.deleteCalendarBooking(calendarBooking);

		assertCalendarBookingsCount(liveCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 0);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 0);
	}

	@Test
	public void testEscapeEventNotificationVariables() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		User invitingUser = UserTestUtil.addUser(
			RandomTestUtil.randomString(), LocaleUtil.getDefault(), "firstN@m&",
			"#124 & 423", new long[] {TestPropsValues.getGroupId()});

		Calendar calendar = CalendarTestUtil.addCalendar(
			invitingUser, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			_user, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		String mailBody =
			"[$EVENT_TITLE|uri$], [$TO_NAME|html$], [$EVENT_LOCATION|attr$]";
		String mailSubject = RandomTestUtil.randomString();

		CalendarNotificationTemplateTestUtil.addCalendarNotificationTemplate(
			calendar, NotificationTemplateType.INVITE, "test@liferay.com",
			"Test Test", mailSubject, mailBody);

		long startTime = System.currentTimeMillis() + (Time.MINUTE * 2);

		CalendarBookingLocalServiceUtil.addCalendarBooking(
			invitingUser.getUserId(), calendar.getCalendarId(),
			new long[] {invitedCalendar.getCalendarId()},
			CalendarBookingConstants.PARENT_CALENDAR_BOOKING_ID_DEFAULT,
			CalendarBookingConstants.RECURRING_CALENDAR_BOOKING_ID_DEFAULT,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "mySubject #123 & 412"
			).build(),
			RandomTestUtil.randomLocaleStringMap(), "myLocation #123 & 321",
			startTime, startTime + Time.HOUR, false,
			RecurrenceSerializer.serialize(null), Time.MINUTE,
			NotificationType.EMAIL.getValue(), 0,
			NotificationType.EMAIL.getValue(), serviceContext);

		_calendarBookingLocalService.checkCalendarBookings();

		assertMailBody(
			mailSubject,
			"mySubject #123 &amp; 412, firstN@m&amp; #124 &amp; 423, " +
				"myLocation #123 &amp; 321");
	}

	@Test
	public void testGetRecurringCalendarBookings() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		CalendarBookingTestUtil.updateCalendarBookingInstance(
			calendarBooking, 2, titleMap, serviceContext);

		CalendarBookingTestUtil.updateCalendarBookingInstance(
			calendarBooking, 4, titleMap, serviceContext);

		List<CalendarBooking> instances =
			_calendarBookingLocalService.getRecurringCalendarBookings(
				calendarBooking);

		Assert.assertEquals(instances.toString(), 3, instances.size());

		for (CalendarBooking instance : instances) {
			if (instance.getCalendarBookingId() ==
					calendarBooking.getCalendarBookingId()) {

				continue;
			}

			Assert.assertEquals(titleMap, instance.getTitleMap());
		}
	}

	@Test
	public void testGetRecurringCalendarBookingsSkipPastEvents()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		CalendarBooking firstCalendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, titleMap, serviceContext);

		CalendarBooking secondCalendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 4, titleMap, serviceContext);

		List<CalendarBooking> instances =
			_calendarBookingLocalService.getRecurringCalendarBookings(
				calendarBooking,
				firstCalendarBookingInstance.getStartTime() + 1);

		Assert.assertEquals(instances.toString(), 1, instances.size());

		CalendarBooking instance = instances.get(0);

		Assert.assertEquals(
			secondCalendarBookingInstance.getStartTime(),
			instance.getStartTime());
	}

	@Test
	public void testInviteAndRemoveCalendar() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				calendar, invitedCalendar);

		List<CalendarBooking> childCalendarBookings =
			calendarBooking.getChildCalendarBookings();

		Assert.assertEquals(
			childCalendarBookings.toString(), 2, childCalendarBookings.size());

		calendarBooking = CalendarBookingTestUtil.updateCalendarBooking(
			calendarBooking, new long[0], serviceContext);

		childCalendarBookings = calendarBooking.getChildCalendarBookings();

		Assert.assertEquals(
			childCalendarBookings.toString(), 1, childCalendarBookings.size());

		calendarBooking = CalendarBookingTestUtil.updateCalendarBooking(
			calendarBooking, new long[] {invitedCalendar.getCalendarId()},
			serviceContext);

		childCalendarBookings = calendarBooking.getChildCalendarBookings();

		Assert.assertEquals(
			childCalendarBookings.toString(), 2, childCalendarBookings.size());
	}

	@Test
	public void testInviteGroupCalendar() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(
			_user, serviceContext);

		Group group = GroupTestUtil.addGroup();

		Calendar groupCalendar = CalendarTestUtil.addCalendar(
			group, serviceContext);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, groupCalendar);

		assertCalendar(childCalendarBooking, groupCalendar);
	}

	@Test
	public void testInviteGroupResourceCalendar() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(
			_user, serviceContext);

		Group group = GroupTestUtil.addGroup();

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(group);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, resourceCalendar);

		assertCalendar(childCalendarBooking, resourceCalendar);
	}

	@Test
	public void testInviteLiveSiteCalendarCreatesStagingSiteCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Assert.assertNotNull(stagingCalendar);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, liveCalendar);

		assertCalendar(childCalendarBooking, stagingCalendar);

		assertCalendarBookingsCount(invitingCalendar, 1);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 1);
	}

	@Test
	public void testInviteLiveSiteCalendarWithDeletedStagingSiteCalendarCreatesNoCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.addCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Assert.assertNotNull(stagingCalendar);

		_calendarLocalService.deleteCalendar(stagingCalendar);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, liveCalendar);

		Assert.assertNull(childCalendarBooking);

		assertCalendarBookingsCount(invitingCalendar, 1);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 0);
	}

	@Test
	public void testInviteLiveSiteResourceCalendarCreatesStagingSiteResourceCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.addCalendarResourceCalendar(
			liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Assert.assertNotNull(stagingCalendar);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, liveCalendar);

		assertCalendar(childCalendarBooking, stagingCalendar);

		assertCalendarBookingsCount(invitingCalendar, 1);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 1);
	}

	@Test
	public void testInviteLiveSiteResourceCalendarWithDeletedStagingSiteCalendarCreatesNoCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.addCalendarResourceCalendar(
			liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Assert.assertNotNull(stagingCalendar);

		_calendarResourceLocalService.deleteCalendarResource(
			stagingCalendar.getCalendarResource());

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, liveCalendar);

		Assert.assertNull(childCalendarBooking);

		assertCalendarBookingsCount(invitingCalendar, 1);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 0);
	}

	@Test
	public void testInviteNonstagedSiteCalendarCreatesLiveSiteCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, false);

		Assert.assertNull(
			CalendarStagingTestUtil.getStagingCalendar(
				liveGroup, liveCalendar));

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, liveCalendar);

		assertCalendar(childCalendarBooking, liveCalendar);
	}

	@Test
	public void testInviteNonstagedSiteResourceCalendarCreatesLiveSiteResourceCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.addCalendarResourceCalendar(
			liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, false);

		Assert.assertNull(
			CalendarStagingTestUtil.getStagingCalendar(
				liveGroup, liveCalendar));

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, liveCalendar);

		assertCalendar(childCalendarBooking, liveCalendar);
	}

	@Test
	public void testInviteStagingCalendarShouldNotCreatesPendingLiveCalendarBookingAfterPublish()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Assert.assertNotNull(stagingCalendar);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, stagingCalendar);

		assertCalendar(childCalendarBooking, stagingCalendar);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 1);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 1);
	}

	@Test
	public void testInviteToDraftCalendarBookingResultsInMasterPendingChild()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, startTime + (Time.HOUR * 10), serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING);
	}

	@Test
	public void testInviteToPublishedCalendarBookingNotifiesInvitee()
		throws Exception {

		User invitingUser = UserTestUtil.addUser();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(invitingUser);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBookingWithWorkflow(
				invitingCalendar, invitedCalendar,
				WorkflowConstants.ACTION_PUBLISH);

		String mailMessageSubject = StringBundler.concat(
			"Calendar: Event Notification for \"",
			calendarBooking.getTitle(LocaleUtil.getSiteDefault()),
			StringPool.QUOTE);

		assertMailSubjectCount(mailMessageSubject, 1);
	}

	@Test
	public void testInviteToPublishedCalendarBookingResultsInPendingChild()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, startTime + (Time.HOUR * 10), serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_PENDING);
	}

	@Test
	public void testInviteToStagedCalendarBookingResultsInMasterStagedChild()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				stagingCalendar, invitedCalendar);

		Assert.assertEquals(
			CalendarBookingWorkflowConstants.STATUS_MASTER_STAGING,
			calendarBooking.getStatus());
	}

	@Test
	public void testInviteToStagedCalendarBookingResultsInPendingLiveChild()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				CalendarStagingTestUtil.getStagingCalendar(
					liveGroup, liveCalendar),
				invitedCalendar);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		List<CalendarBooking> childCalendarBookings =
			_calendarBookingLocalService.getCalendarBookings(
				invitedCalendar.getCalendarId(),
				new int[] {WorkflowConstants.STATUS_PENDING});

		Assert.assertEquals(
			childCalendarBookings.toString(), 1, childCalendarBookings.size());

		CalendarBooking childCalendarBooking = childCalendarBookings.get(0);

		Assert.assertEquals(
			calendarBooking.getTitle(), childCalendarBooking.getTitle());
	}

	@Test
	public void testInviteUserCalendarWithWorkflowShouldInviteCalendarBookingOnlyAfterApprovedAndPublished()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		_activateWorkflow(liveGroup);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				CalendarStagingTestUtil.getStagingCalendar(
					liveGroup, liveCalendar),
				invitedCalendar);

		assertStatus(
			childCalendarBooking,
			CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING);

		CalendarBooking calendarBooking =
			childCalendarBooking.getParentCalendarBooking();

		assertStatus(calendarBooking, WorkflowConstants.STATUS_PENDING);

		_completeWorkflow(liveGroup);

		childCalendarBooking =
			_calendarBookingLocalService.fetchCalendarBooking(
				childCalendarBooking.getCalendarBookingId());

		assertStatus(
			childCalendarBooking,
			CalendarBookingWorkflowConstants.STATUS_MASTER_STAGING);

		calendarBooking = childCalendarBooking.getParentCalendarBooking();

		assertStatus(calendarBooking, WorkflowConstants.STATUS_APPROVED);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		List<CalendarBooking> childCalendarBookings =
			_calendarBookingLocalService.getCalendarBookings(
				invitedCalendar.getCalendarId(),
				new int[] {WorkflowConstants.STATUS_PENDING});

		Assert.assertEquals(
			childCalendarBookings.toString(), 1, childCalendarBookings.size());
	}

	@Test
	public void testInviteUserCalendarWithWorkflowShouldNotCreatesCalendarBookingAfterPublish()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		_activateWorkflow(liveGroup);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Assert.assertNotNull(stagingCalendar);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				stagingCalendar, invitedCalendar);

		assertCalendar(childCalendarBooking, invitedCalendar);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(invitedCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 1);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar, 0);

		CalendarBooking calendarBooking =
			childCalendarBooking.getParentCalendarBooking();

		assertStatus(calendarBooking, WorkflowConstants.STATUS_PENDING);

		childCalendarBooking =
			_calendarBookingLocalService.fetchCalendarBooking(
				childCalendarBooking.getCalendarBookingId());

		assertStatus(
			childCalendarBooking,
			CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING);
	}

	@Test
	public void testInviteUserCalendarWithWorkflowShouldNotifyInviteCalendarBookingOnlyAfterApprovedAndPublished()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_activateWorkflow(group);

		User invitingUser = UserTestUtil.addUser();

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(invitingUser);

		Calendar invitingCalendar = CalendarTestUtil.getDefaultCalendar(group);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBookingWithWorkflow(
				invitingCalendar, invitedCalendar,
				WorkflowConstants.ACTION_PUBLISH);

		String mailMessageSubject = StringBundler.concat(
			"Calendar: Event Notification for \"",
			calendarBooking.getTitle(LocaleUtil.getSiteDefault()),
			StringPool.QUOTE);

		assertMailSubjectCount(mailMessageSubject, 0);

		_completeWorkflow(group);

		assertMailSubjectCount(mailMessageSubject, 1);
	}

	@Test
	public void testInviteUserResourceCalendar() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Group group = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(
			group, serviceContext);

		Calendar resourceCalendar =
			CalendarTestUtil.addCalendarResourceCalendar(_user);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, resourceCalendar);

		assertCalendar(childCalendarBooking, resourceCalendar);
	}

	@Test
	public void testMoveStagingCalendarBookingToOtherSiteAndBack()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(stagingCalendar);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 1);

		Group group = GroupTestUtil.addGroup();

		Calendar externalCalendar = CalendarTestUtil.addCalendar(group);

		_calendarBookingLocalService.updateCalendarBooking(
			calendarBooking.getUserId(), calendarBooking.getCalendarBookingId(),
			externalCalendar.getCalendarId(), calendarBooking.getTitleMap(),
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			calendarBooking.getStartTime(), calendarBooking.getEndTime(),
			calendarBooking.isAllDay(), calendarBooking.getRecurrence(),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(), createServiceContext());

		_calendarBookingLocalService.updateCalendarBooking(
			calendarBooking.getUserId(), calendarBooking.getCalendarBookingId(),
			stagingCalendar.getCalendarId(), calendarBooking.getTitleMap(),
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			calendarBooking.getStartTime(), calendarBooking.getEndTime(),
			calendarBooking.isAllDay(), calendarBooking.getRecurrence(),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(), createServiceContext());

		assertCalendarBookingsCount(externalCalendar, 0);

		assertCalendarBookingsCount(liveCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 1);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(externalCalendar, 0);

		assertCalendarBookingsCount(liveCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 1);
	}

	@Test
	public void testMoveStagingCalendarBookingToOtherSiteDeletesLiveCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(stagingCalendar);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 1);

		Group group = GroupTestUtil.addGroup();

		Calendar externalCalendar = CalendarTestUtil.addCalendar(group);

		_calendarBookingLocalService.updateCalendarBooking(
			calendarBooking.getUserId(), calendarBooking.getCalendarBookingId(),
			externalCalendar.getCalendarId(), calendarBooking.getTitleMap(),
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			calendarBooking.getStartTime(), calendarBooking.getEndTime(),
			calendarBooking.isAllDay(), calendarBooking.getRecurrence(),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(), createServiceContext());

		assertCalendarBookingsCount(externalCalendar, 1);

		assertCalendarBookingsCount(liveCalendar, 1);

		assertCalendarBookingsCount(stagingCalendar, 0);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(externalCalendar, 1);

		assertCalendarBookingsCount(liveCalendar, 0);

		assertCalendarBookingsCount(stagingCalendar, 0);
	}

	@Test
	public void testMoveStagingCalendarBookingToSameSitePreservesLiveCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar1 = CalendarTestUtil.getDefaultCalendar(liveGroup);
		Calendar liveCalendar2 = CalendarTestUtil.addCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar1 = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar1);
		Calendar stagingCalendar2 = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar2);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(stagingCalendar1);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar1, 1);
		assertCalendarBookingsCount(liveCalendar2, 0);
		assertCalendarBookingsCount(stagingCalendar1, 1);
		assertCalendarBookingsCount(stagingCalendar2, 0);

		_calendarBookingLocalService.updateCalendarBooking(
			calendarBooking.getUserId(), calendarBooking.getCalendarBookingId(),
			stagingCalendar2.getCalendarId(), calendarBooking.getTitleMap(),
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			calendarBooking.getStartTime(), calendarBooking.getEndTime(),
			calendarBooking.isAllDay(), calendarBooking.getRecurrence(),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(), createServiceContext());

		assertCalendarBookingsCount(liveCalendar1, 1);
		assertCalendarBookingsCount(liveCalendar2, 0);
		assertCalendarBookingsCount(stagingCalendar1, 0);
		assertCalendarBookingsCount(stagingCalendar2, 1);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		assertCalendarBookingsCount(liveCalendar1, 0);
		assertCalendarBookingsCount(liveCalendar2, 1);
		assertCalendarBookingsCount(stagingCalendar1, 0);
		assertCalendarBookingsCount(stagingCalendar2, 1);
	}

	@Test
	public void testMoveToTrashCalendarBookingShouldMoveItsChildrenToTrash()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, startTime + (Time.HOUR * 10), serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_PENDING);

		_calendarBookingLocalService.moveCalendarBookingToTrash(
			_user.getUserId(), calendarBooking);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_IN_TRASH);
	}

	@Test
	public void testNotificationIsSendWithLastPublishedEmailTemplate()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		CalendarBookingTestUtil.addMasterCalendarBookingWithWorkflow(
			stagingCalendar, invitedCalendar, WorkflowConstants.ACTION_PUBLISH);

		String mailBody = RandomTestUtil.randomString();
		String mailSubject = RandomTestUtil.randomString();

		CalendarNotificationTemplateTestUtil.addCalendarNotificationTemplate(
			stagingCalendar, NotificationTemplateType.INVITE,
			"test@liferay.com", "Test Test", mailSubject, mailBody);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		_calendarBookingLocalService.checkCalendarBookings();

		assertMailBody(mailSubject, mailBody);
	}

	@Test
	public void testPublishCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				serviceContext);

		assertStatus(
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBooking.getCalendarBookingId()),
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testPublishDraftCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendar.getCalendarId(), new long[0],
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime,
			startTime + (Time.HOUR * 10), false, null, 0, null, 0, null,
			serviceContext);

		assertStatus(
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBooking.getCalendarBookingId()),
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testPublishDraftCalendarBookingResultsInPendingChild()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, startTime + (Time.HOUR * 10), serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendar.getCalendarId(),
			new long[] {invitedCalendar.getCalendarId()},
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime,
			startTime + (Time.HOUR * 10), false, null, 0, null, 0, null,
			serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_PENDING);
	}

	@Test
	public void testRestoredFromTrashEventResultsInRestoredFromTrashChildren()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, startTime + (Time.HOUR * 10), serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_PENDING);

		_calendarBookingLocalService.moveCalendarBookingToTrash(
			_user.getUserId(), calendarBooking);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_IN_TRASH);

		_calendarBookingLocalService.restoreCalendarBookingFromTrash(
			_user.getUserId(), calendarBooking.getCalendarBookingId());

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			WorkflowConstants.STATUS_PENDING);
	}

	@Test
	public void testSaveAsDraftCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				serviceContext);

		assertStatus(
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBooking.getCalendarBookingId()),
			WorkflowConstants.STATUS_DRAFT);
	}

	@Test
	public void testSaveAsDraftDraftCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendar.getCalendarId(), new long[0],
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime,
			startTime + (Time.HOUR * 10), false, null, 0, null, 0, null,
			serviceContext);

		assertStatus(
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBooking.getCalendarBookingId()),
			WorkflowConstants.STATUS_DRAFT);
	}

	@Test
	public void testSaveAsDraftPublishedCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendar.getCalendarId(), new long[0],
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime,
			startTime + (Time.HOUR * 10), false, null, 0, null, 0, null,
			serviceContext);

		assertStatus(
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBooking.getCalendarBookingId()),
			WorkflowConstants.STATUS_DRAFT);
	}

	@Test
	public void testStagingCalendarBookingDoesNotSendReminderNotification()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		long startTime = System.currentTimeMillis() + (Time.MINUTE * 2);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBookingWithReminders(
				stagingCalendar, startTime, startTime + Time.HOUR,
				(int)Time.MINUTE, 0);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		_calendarBookingLocalService.checkCalendarBookings();

		String mailMessageSubject = StringBundler.concat(
			"Calendar: Event Reminder for \"",
			calendarBooking.getTitle(LocaleUtil.getSiteDefault()),
			StringPool.QUOTE);

		assertMailSubjectCount(mailMessageSubject, 1);
	}

	@Test
	public void testStagingCalendarResourceShouldNotBeInviteToLiveCalendarBookingAfterPublish()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Assert.assertNotNull(stagingCalendar);

		CalendarBooking stagingCalendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(stagingCalendar);

		Group stagingGroup = liveGroup.getStagingGroup();

		CalendarTestUtil.addCalendarResourceCalendar(stagingGroup);

		CalendarTestUtil.addCalendarResourceCalendar(stagingGroup);

		List<CalendarBooking> liveCalendarBookings =
			_calendarBookingLocalService.getCalendarBookings(
				liveCalendar.getCalendarId());

		Assert.assertEquals(
			liveCalendarBookings.toString(), 0, liveCalendarBookings.size());

		List<CalendarBooking> childCalendarBookings =
			stagingCalendarBooking.getChildCalendarBookings();

		Assert.assertEquals(
			childCalendarBookings.toString(), 1, childCalendarBookings.size());

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		liveCalendarBookings = _calendarBookingLocalService.getCalendarBookings(
			liveCalendar.getCalendarId());

		Assert.assertEquals(
			liveCalendarBookings.toString(), 1, liveCalendarBookings.size());

		CalendarBooking liveCalendarBooking = liveCalendarBookings.get(0);

		List<CalendarBooking> liveChildCalendarBookings =
			liveCalendarBooking.getChildCalendarBookings();

		Assert.assertEquals(
			liveChildCalendarBookings.toString(), 1,
			liveChildCalendarBookings.size());

		childCalendarBookings =
			stagingCalendarBooking.getChildCalendarBookings();

		Assert.assertEquals(
			childCalendarBookings.toString(), 1, childCalendarBookings.size());
	}

	@Test(expected = CalendarBookingRecurrenceException.class)
	public void testStartDateBeforeUntilDateThrowsRecurrenceException()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		java.util.Calendar untilJCalendar = CalendarFactoryUtil.getCalendar(
			startTime);

		untilJCalendar.add(java.util.Calendar.DAY_OF_MONTH, -2);

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence(
			untilJCalendar);

		CalendarBookingTestUtil.addRecurringCalendarBooking(
			_user, calendar, startTime, startTime + (Time.HOUR * 10),
			recurrence, serviceContext);
	}

	@Test
	public void testUpdateAllFollowingFromSingleInstance() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> earlierDescriptionMap = new HashMap<>(
			calendarBooking.getDescriptionMap());

		Map<Locale, String> laterDescriptionMap =
			RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, RandomTestUtil.randomLocaleStringMap(),
				serviceContext);

		long instanceStartTime = calendarBookingInstance.getStartTime();
		long instanceEndTime = calendarBookingInstance.getEndTime();

		_calendarBookingLocalService.updateCalendarBookingInstance(
			_user.getUserId(), calendarBookingInstance.getCalendarBookingId(),
			0, calendarBookingInstance.getCalendarId(),
			calendarBookingInstance.getTitleMap(), laterDescriptionMap,
			calendarBookingInstance.getLocation(), instanceStartTime,
			instanceEndTime, false, null, true, 0, null, 0, null,
			serviceContext);

		List<CalendarBooking> recurringCalendarBookings =
			_calendarBookingLocalService.getRecurringCalendarBookings(
				calendarBooking);

		List<CalendarBooking> earlierCalendarBookings = new ArrayList<>();
		List<CalendarBooking> laterCalendarBookings = new ArrayList<>();

		partitionCalendarBookingListByStartTime(
			recurringCalendarBookings, instanceStartTime,
			earlierCalendarBookings, laterCalendarBookings);

		Assert.assertEquals(
			earlierCalendarBookings.toString(), 1,
			earlierCalendarBookings.size());
		Assert.assertEquals(
			laterCalendarBookings.toString(), 2, laterCalendarBookings.size());

		assertDescriptions(earlierCalendarBookings, earlierDescriptionMap);
		assertDescriptions(laterCalendarBookings, laterDescriptionMap);
	}

	@Test
	public void testUpdateAllFollowingFromSingleInstancePreservesDayTime()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		long instanceStartTime =
			calendarBooking.getStartTime() + (Time.DAY * 2);

		long instanceEndTime = instanceStartTime + (Time.HOUR * 10);

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				_user, calendarBooking, 2, titleMap,
				calendarBooking.getDescriptionMap(), instanceStartTime,
				instanceEndTime, serviceContext);

		Map<Locale, String> laterDescriptionMap =
			RandomTestUtil.randomLocaleStringMap();

		calendarBookingInstance =
			CalendarBookingTestUtil.
				updateCalendarBookingInstanceAndAllFollowing(
					_user, calendarBookingInstance, 0, titleMap,
					laterDescriptionMap, instanceStartTime, instanceEndTime,
					serviceContext);

		List<CalendarBooking> recurringCalendarBookings =
			_calendarBookingLocalService.getRecurringCalendarBookings(
				calendarBooking, calendarBooking.getEndTime());

		Assert.assertEquals(
			recurringCalendarBookings.toString(), 2,
			recurringCalendarBookings.size());

		java.util.Calendar expectedEndTimeJCalendar =
			JCalendarUtil.getJCalendar(
				instanceEndTime, calendarBookingInstance.getTimeZone());

		java.util.Calendar expectedStartTimeJCalendar =
			JCalendarUtil.getJCalendar(
				instanceStartTime, calendarBookingInstance.getTimeZone());

		assertCalendarBookingPeriod(
			recurringCalendarBookings.get(0), expectedStartTimeJCalendar,
			expectedEndTimeJCalendar);

		expectedEndTimeJCalendar = JCalendarUtil.getJCalendar(
			instanceEndTime, calendarBookingInstance.getTimeZone());

		expectedEndTimeJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);

		expectedStartTimeJCalendar = JCalendarUtil.getJCalendar(
			instanceStartTime, calendarBookingInstance.getTimeZone());

		expectedStartTimeJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);

		assertCalendarBookingPeriod(
			recurringCalendarBookings.get(1), expectedStartTimeJCalendar,
			expectedEndTimeJCalendar);
	}

	@Test
	public void testUpdateAllFollowingFromSingleInstancePreservesDayTimeAcrossDays()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		long endTime = startTime + Time.DAY;

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_user, calendar, startTime, endTime, recurrence,
				serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		Map<Locale, String> laterDescriptionMap =
			RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, titleMap, serviceContext);

		long instanceEndTime = calendarBookingInstance.getEndTime();
		long instanceStartTime = calendarBookingInstance.getStartTime();

		calendarBookingInstance =
			CalendarBookingTestUtil.
				updateCalendarBookingInstanceAndAllFollowing(
					_user, calendarBookingInstance, 0, titleMap,
					laterDescriptionMap, instanceStartTime, instanceEndTime,
					serviceContext);

		List<CalendarBooking> recurringCalendarBookings =
			_calendarBookingLocalService.getRecurringCalendarBookings(
				calendarBooking, calendarBooking.getEndTime());

		Assert.assertEquals(
			recurringCalendarBookings.toString(), 2,
			recurringCalendarBookings.size());

		java.util.Calendar expectedEndTimeJCalendar =
			JCalendarUtil.getJCalendar(
				instanceEndTime, calendarBookingInstance.getTimeZone());

		java.util.Calendar expectedStartTimeJCalendar =
			JCalendarUtil.getJCalendar(
				instanceStartTime, calendarBookingInstance.getTimeZone());

		assertCalendarBookingPeriod(
			recurringCalendarBookings.get(0), expectedStartTimeJCalendar,
			expectedEndTimeJCalendar);

		expectedEndTimeJCalendar = JCalendarUtil.getJCalendar(
			instanceEndTime, calendarBookingInstance.getTimeZone());

		expectedEndTimeJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);

		expectedStartTimeJCalendar = JCalendarUtil.getJCalendar(
			instanceStartTime, calendarBookingInstance.getTimeZone());

		expectedStartTimeJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);

		assertCalendarBookingPeriod(
			recurringCalendarBookings.get(1), expectedStartTimeJCalendar,
			expectedEndTimeJCalendar);
	}

	@Test
	public void testUpdateAllFollowingWhenRecurrenceIsInSpecificTimeZone()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		java.util.Calendar startTimeJCalendar = new GregorianCalendar(
			_losAngelesTimeZone);

		startTimeJCalendar.set(java.util.Calendar.YEAR, 2017);
		startTimeJCalendar.set(
			java.util.Calendar.MONTH, java.util.Calendar.MAY);
		startTimeJCalendar.set(
			java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
		startTimeJCalendar.set(java.util.Calendar.HOUR_OF_DAY, 20);
		startTimeJCalendar.set(java.util.Calendar.MINUTE, 0);

		long startTime = startTimeJCalendar.getTimeInMillis();

		long endTime = startTime + (Time.HOUR * 1);

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence(
			_losAngelesTimeZone);

		recurrence.setInterval(1);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_user, calendar, startTime, endTime, recurrence,
				serviceContext);

		int instanceIndex = 2;

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		long instanceStartTime = startTime + (Time.DAY * 2) + (Time.HOUR * 1);

		long instanceEndTime = instanceStartTime + (Time.HOUR * 1);

		CalendarBookingTestUtil.updateCalendarBookingInstance(
			_user, calendarBooking, instanceIndex, titleMap,
			calendarBooking.getDescriptionMap(), instanceStartTime,
			instanceEndTime, serviceContext);

		instanceIndex = 1;

		instanceStartTime = startTime + (Time.DAY * 1) + (Time.HOUR * 1);

		instanceEndTime = instanceStartTime + (Time.HOUR * 1);

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBooking.getCalendarBookingId());

		CalendarBooking expectedCalendarBookingInstance =
			CalendarBookingTestUtil.
				updateRecurringCalendarBookingInstanceAndAllFollowing(
					_user, calendarBooking, instanceIndex,
					calendarBooking.getTitleMap(),
					calendarBooking.getDescriptionMap(), instanceStartTime,
					instanceEndTime, calendarBooking.getRecurrence(),
					serviceContext);

		java.util.Calendar expectedJCalendar = new GregorianCalendar(
			_losAngelesTimeZone);

		expectedJCalendar.setTimeInMillis(
			expectedCalendarBookingInstance.getStartTime());

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.DAY_OF_WEEK),
			java.util.Calendar.MONDAY);

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.HOUR_OF_DAY),
			startTimeJCalendar.get(java.util.Calendar.HOUR_OF_DAY) + 1);

		expectedJCalendar.setTimeInMillis(
			expectedCalendarBookingInstance.getEndTime());

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.DAY_OF_WEEK),
			java.util.Calendar.MONDAY);

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.HOUR_OF_DAY),
			startTimeJCalendar.get(java.util.Calendar.HOUR_OF_DAY) + 2);
	}

	@Test
	public void testUpdateAllFollowingWithInvitations() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		User invitingUser = UserTestUtil.addUser();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(invitingUser);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterRecurringCalendarBooking(
				invitedCalendar, invitingCalendar);

		Map<Locale, String> laterDescriptionMap =
			RandomTestUtil.randomLocaleStringMap();

		_calendarBookingLocalService.updateCalendarBookingInstance(
			_user.getUserId(), calendarBooking.getCalendarBookingId(), 0,
			calendarBooking.getCalendarId(), calendarBooking.getTitleMap(),
			laterDescriptionMap, calendarBooking.getLocation(),
			calendarBooking.getStartTime(), calendarBooking.getEndTime(), false,
			null, true, 0, null, 0, null, serviceContext);

		assertCalendarBookingsCount(invitedCalendar, 1);
		assertCalendarBookingsCount(invitingCalendar, 1);

		assertDescriptions(
			calendarBooking.getChildCalendarBookings(), laterDescriptionMap);
	}

	@Test
	public void testUpdateCalendarBookingAndAllRecurringInstancesStatus()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> titleMap = RandomTestUtil.randomLocaleStringMap();

		CalendarBooking thirdCalendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, titleMap, serviceContext);

		CalendarBooking secondCalendarBookingInstance =
			_calendarBookingLocalService.getCalendarBookingInstance(
				calendarBooking.getCalendarBookingId(), 1);

		_calendarBookingLocalService.invokeTransition(
			_user.getUserId(), calendarBooking,
			secondCalendarBookingInstance.getStartTime(),
			CalendarBookingWorkflowConstants.STATUS_MAYBE, true, true,
			serviceContext);

		thirdCalendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				thirdCalendarBookingInstance.getCalendarBookingId());

		assertStatus(
			thirdCalendarBookingInstance,
			CalendarBookingWorkflowConstants.STATUS_MAYBE);
	}

	@Test
	public void testUpdateCalendarBookingDoesNotPreserveChildStatusIfIntervalChanges()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		long endTime = startTime + (Time.HOUR * 10);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, endTime, serviceContext);

		CalendarBooking childCalendarBooking = getChildCalendarBooking(
			calendarBooking);

		assertStatus(childCalendarBooking, WorkflowConstants.STATUS_PENDING);

		_calendarBookingLocalService.updateStatus(
			_user.getUserId(), childCalendarBooking,
			CalendarBookingWorkflowConstants.STATUS_MAYBE, serviceContext);

		long newEndTime = endTime + 1000000;

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendarBooking.getCalendarId(),
			new long[] {invitedCalendar.getCalendarId()},
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime, newEndTime,
			calendarBooking.isAllDay(), calendarBooking.getRecurrence(),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(), serviceContext);

		childCalendarBooking = getChildCalendarBooking(calendarBooking);

		Assert.assertNotEquals(
			CalendarBookingWorkflowConstants.STATUS_MAYBE,
			childCalendarBooking.getStatus());
	}

	@Test
	public void testUpdateCalendarBookingInstance() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> instanceTitleMap =
			RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, instanceTitleMap, serviceContext);

		Assert.assertEquals(
			instanceTitleMap, calendarBookingInstance.getTitleMap());

		Map<Locale, String> newTitleMap =
			RandomTestUtil.randomLocaleStringMap();

		_calendarBookingLocalService.updateCalendarBookingInstance(
			_user.getUserId(), calendarBooking.getCalendarBookingId(), 1,
			calendarBooking.getCalendarId(), newTitleMap,
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			calendarBooking.getStartTime(), calendarBooking.getEndTime(), false,
			calendarBooking.getRecurrence(), true, 0, null, 0, null,
			serviceContext);

		calendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getCalendarBookingId());

		Assert.assertEquals(newTitleMap, calendarBookingInstance.getTitleMap());
	}

	@Test
	public void testUpdateCalendarBookingInstanceWithoutRecurrence()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		Map<Locale, String> instanceTitleMap =
			RandomTestUtil.randomLocaleStringMap();

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, instanceTitleMap, serviceContext);

		Assert.assertEquals(
			instanceTitleMap, calendarBookingInstance.getTitleMap());

		Map<Locale, String> newTitleMap =
			RandomTestUtil.randomLocaleStringMap();

		_calendarBookingLocalService.updateCalendarBookingInstance(
			_user.getUserId(), calendarBooking.getCalendarBookingId(), 1,
			calendarBooking.getCalendarId(), newTitleMap,
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			calendarBooking.getStartTime(), calendarBooking.getEndTime(), false,
			calendarBooking.getRecurrence(), true, 0, null, 0, null,
			serviceContext);

		calendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getCalendarBookingId());

		Assert.assertEquals(newTitleMap, calendarBookingInstance.getTitleMap());
	}

	@Test
	public void testUpdateCalendarBookingPreservesChildReminders()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), startTime,
				startTime + (Time.HOUR * 10), null, RandomTestUtil.randomInt(),
				NotificationType.EMAIL, RandomTestUtil.randomInt(),
				NotificationType.EMAIL, serviceContext);

		CalendarBooking childCalendarBooking = getChildCalendarBooking(
			calendarBooking);

		childCalendarBooking = _calendarBookingLocalService.updateStatus(
			_user.getUserId(), childCalendarBooking,
			CalendarBookingWorkflowConstants.STATUS_MAYBE, serviceContext);

		int firstReminder = RandomTestUtil.randomInt();

		int secondReminder = RandomTestUtil.randomInt(1, firstReminder);

		_calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), childCalendarBooking.getCalendarBookingId(),
			childCalendarBooking.getCalendarId(), new long[0],
			childCalendarBooking.getTitleMap(),
			childCalendarBooking.getDescriptionMap(),
			childCalendarBooking.getLocation(), startTime,
			startTime + (Time.HOUR * 10), childCalendarBooking.isAllDay(),
			childCalendarBooking.getRecurrence(), firstReminder,
			NotificationType.EMAIL.getValue(), secondReminder,
			NotificationType.EMAIL.getValue(), serviceContext);

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendarBooking.getCalendarId(),
			new long[] {invitedCalendar.getCalendarId()},
			calendarBooking.getTitleMap(), calendarBooking.getDescriptionMap(),
			calendarBooking.getLocation(), startTime,
			startTime + (Time.HOUR * 11), calendarBooking.isAllDay(),
			calendarBooking.getRecurrence(), RandomTestUtil.randomInt(),
			calendarBooking.getFirstReminderType(), RandomTestUtil.randomInt(),
			calendarBooking.getSecondReminderType(), serviceContext);

		childCalendarBooking = getChildCalendarBooking(calendarBooking);

		Assert.assertNotEquals(
			calendarBooking.getFirstReminder(),
			childCalendarBooking.getFirstReminder());
		Assert.assertNotEquals(
			calendarBooking.getSecondReminder(),
			childCalendarBooking.getSecondReminder());

		Assert.assertEquals(
			firstReminder, childCalendarBooking.getFirstReminder());
		Assert.assertEquals(
			secondReminder, childCalendarBooking.getSecondReminder());
	}

	@Test
	public void testUpdateCalendarBookingPreservesChildStatus()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		long endTime = startTime + (Time.HOUR * 10);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, endTime, serviceContext);

		CalendarBooking childCalendarBooking = getChildCalendarBooking(
			calendarBooking);

		assertStatus(childCalendarBooking, WorkflowConstants.STATUS_PENDING);

		_calendarBookingLocalService.updateStatus(
			_user.getUserId(), childCalendarBooking,
			CalendarBookingWorkflowConstants.STATUS_MAYBE, serviceContext);

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendarBooking.getCalendarId(),
			new long[] {invitedCalendar.getCalendarId()},
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime, endTime,
			calendarBooking.isAllDay(), calendarBooking.getRecurrence(),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(), serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			CalendarBookingWorkflowConstants.STATUS_MAYBE);
	}

	@Test
	public void testUpdateCalendarBookingPreservesDescriptionTranslations()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Map<Locale, String> oldDescriptionMap = HashMapBuilder.put(
			LocaleUtil.BRAZIL, RandomTestUtil.randomString()
		).put(
			LocaleUtil.GERMANY, RandomTestUtil.randomString()
		).put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		long startTime = System.currentTimeMillis();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.
				addRegularCalendarBookingWithTitleAndDescription(
					_user, calendar, RandomTestUtil.randomLocaleStringMap(),
					oldDescriptionMap, startTime, startTime + (Time.HOUR * 10),
					serviceContext);

		Map<Locale, String> newDescriptionMap = HashMapBuilder.put(
			LocaleUtil.GERMANY, ""
		).put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, oldDescriptionMap.get(LocaleUtil.US)
		).build();

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendar.getCalendarId(), new long[0],
			RandomTestUtil.randomLocaleStringMap(), newDescriptionMap,
			RandomTestUtil.randomString(), startTime,
			startTime + (Time.HOUR * 10), false, null, 0, null, 0, null,
			serviceContext);

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBooking.getCalendarBookingId());

		Assert.assertEquals(
			oldDescriptionMap.get(LocaleUtil.BRAZIL),
			calendarBooking.getDescription(LocaleUtil.BRAZIL));
		Assert.assertEquals(
			newDescriptionMap.get(LocaleUtil.SPAIN),
			calendarBooking.getDescription(LocaleUtil.SPAIN));
		Assert.assertEquals(
			newDescriptionMap.get(LocaleUtil.US),
			calendarBooking.getDescription(LocaleUtil.US));

		Map<Locale, String> descriptionMap =
			calendarBooking.getDescriptionMap();

		Assert.assertFalse(descriptionMap.containsKey(LocaleUtil.GERMANY));
	}

	@Test
	public void testUpdateCalendarBookingPreservesTitleTranslations()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Map<Locale, String> oldTitleMap = HashMapBuilder.put(
			LocaleUtil.BRAZIL, RandomTestUtil.randomString()
		).put(
			LocaleUtil.GERMANY, RandomTestUtil.randomString()
		).put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		long startTime = System.currentTimeMillis();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.
				addRegularCalendarBookingWithTitleAndDescription(
					_user, calendar, oldTitleMap,
					RandomTestUtil.randomLocaleStringMap(), startTime,
					startTime + (Time.HOUR * 10), serviceContext);

		Map<Locale, String> newTitleMap = HashMapBuilder.put(
			LocaleUtil.GERMANY, ""
		).put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, oldTitleMap.get(LocaleUtil.US)
		).build();

		calendarBooking = _calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendar.getCalendarId(), new long[0], newTitleMap,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime,
			startTime + (Time.HOUR * 10), false, null, 0, null, 0, null,
			serviceContext);

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBooking.getCalendarBookingId());

		Assert.assertEquals(
			oldTitleMap.get(LocaleUtil.BRAZIL),
			calendarBooking.getTitle(LocaleUtil.BRAZIL));
		Assert.assertEquals(
			newTitleMap.get(LocaleUtil.SPAIN),
			calendarBooking.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			oldTitleMap.get(LocaleUtil.US),
			calendarBooking.getTitle(LocaleUtil.US));

		Map<Locale, String> titleMap = calendarBooking.getTitleMap();

		Assert.assertFalse(titleMap.containsKey(LocaleUtil.GERMANY));
	}

	@Test
	public void testUpdateChildCalendarBookingPreservesStatus()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(
			calendar.getCalendarResource(), serviceContext);

		long startTime = System.currentTimeMillis();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addMasterCalendarBooking(
				_user, calendar, new long[] {invitedCalendar.getCalendarId()},
				startTime, startTime + (Time.HOUR * 10), serviceContext);

		CalendarBooking childCalendarBooking = getChildCalendarBooking(
			calendarBooking);

		assertStatus(childCalendarBooking, WorkflowConstants.STATUS_PENDING);

		childCalendarBooking = _calendarBookingLocalService.updateStatus(
			_user.getUserId(), childCalendarBooking,
			CalendarBookingWorkflowConstants.STATUS_MAYBE, serviceContext);

		_calendarBookingLocalService.updateCalendarBooking(
			_user.getUserId(), childCalendarBooking.getCalendarBookingId(),
			childCalendarBooking.getCalendarId(),
			new long[] {invitedCalendar.getCalendarId()},
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), startTime,
			startTime + (Time.HOUR * 10), childCalendarBooking.isAllDay(),
			childCalendarBooking.getRecurrence(),
			childCalendarBooking.getFirstReminder(),
			childCalendarBooking.getFirstReminderType(),
			childCalendarBooking.getSecondReminder(),
			childCalendarBooking.getSecondReminderType(), serviceContext);

		assertStatus(
			getChildCalendarBooking(calendarBooking),
			CalendarBookingWorkflowConstants.STATUS_MAYBE);
	}

	@Test
	public void testUpdateLastInstanceCalendarBookingRecurrence()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, RandomTestUtil.randomLocaleStringMap(),
				serviceContext);

		java.util.Calendar untilJCalendar = CalendarFactoryUtil.getCalendar(
			calendarBooking.getStartTime());

		untilJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 10);

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence(
			untilJCalendar);

		_calendarBookingLocalService.
			updateLastInstanceCalendarBookingRecurrence(
				calendarBookingInstance,
				RecurrenceSerializer.serialize(recurrence));

		assertDoesNotRepeat(calendarBookingInstance);

		assertRepeatsUntil(calendarBooking, untilJCalendar);
	}

	@Test
	public void testUpdateLastInstanceCalendarBookingRecurrenceFromSingleInstance()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, RandomTestUtil.randomLocaleStringMap(),
				serviceContext);

		java.util.Calendar untilJCalendar = CalendarFactoryUtil.getCalendar(
			calendarBooking.getStartTime());

		untilJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 10);

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence(
			untilJCalendar);

		_calendarBookingLocalService.
			updateLastInstanceCalendarBookingRecurrence(
				calendarBookingInstance,
				RecurrenceSerializer.serialize(recurrence));

		assertDoesNotRepeat(calendarBookingInstance);

		assertRepeatsUntil(calendarBooking, untilJCalendar);
	}

	@Test
	public void testUpdateLastInstanceCalendarBookingRecurrencePreservesExceptionDate()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.updateCalendarBookingInstance(
				calendarBooking, 2, RandomTestUtil.randomLocaleStringMap(),
				serviceContext);

		java.util.Calendar untilJCalendar = CalendarFactoryUtil.getCalendar(
			calendarBooking.getStartTime());

		untilJCalendar.add(java.util.Calendar.DAY_OF_MONTH, 10);

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence(
			untilJCalendar);

		_calendarBookingLocalService.
			updateLastInstanceCalendarBookingRecurrence(
				calendarBookingInstance,
				RecurrenceSerializer.serialize(recurrence));

		java.util.Calendar instanceStartTimeJCalendar =
			CalendarFactoryUtil.getCalendar(
				calendarBookingInstance.getStartTime(), _utcTimeZone);

		assertRepeatsExceptFor(calendarBooking, instanceStartTimeJCalendar);
	}

	@Test
	public void testUpdateRecurringCalendarBooking() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(_user, serviceContext);

		long startTime = System.currentTimeMillis();

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_user, calendar, startTime, startTime + (Time.HOUR * 10),
				recurrence, serviceContext);

		Map<Locale, String> instanceTitleMap =
			RandomTestUtil.randomLocaleStringMap();

		long instanceStartTime = startTime + (Time.DAY * 2);

		CalendarBooking calendarBookingInstance =
			_calendarBookingLocalService.updateCalendarBookingInstance(
				_user.getUserId(), calendarBooking.getCalendarBookingId(), 2,
				calendar.getCalendarId(), instanceTitleMap,
				calendarBooking.getDescriptionMap(),
				calendarBooking.getLocation(), instanceStartTime,
				instanceStartTime + (Time.HOUR * 10), false, null, false, 0,
				null, 0, null, serviceContext);

		Assert.assertEquals(
			instanceTitleMap, calendarBookingInstance.getTitleMap());

		Map<Locale, String> newTitleMap =
			RandomTestUtil.randomLocaleStringMap();

		_calendarBookingLocalService.updateRecurringCalendarBooking(
			_user.getUserId(), calendarBooking.getCalendarBookingId(),
			calendar.getCalendarId(), new long[0], newTitleMap,
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			startTime, startTime + (Time.HOUR * 10), false, 0, null, 0, null,
			serviceContext);

		calendarBookingInstance =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getCalendarBookingId());

		Assert.assertEquals(newTitleMap, calendarBookingInstance.getTitleMap());
	}

	@Test
	public void testUpdateRecurringCalendarBookingLastInstance()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(
			_user, _losAngelesTimeZone, serviceContext);

		java.util.Calendar startTimeJCalendar = CalendarFactoryUtil.getCalendar(
			2017, java.util.Calendar.JANUARY, 1, 20, 0, 0, 0,
			_losAngelesTimeZone);

		Recurrence recurrence = RecurrenceTestUtil.getDailyRecurrence(
			3, _losAngelesTimeZone);

		long startTime = startTimeJCalendar.getTimeInMillis();

		long endTime = startTime + (Time.HOUR * 10);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_user, calendar, startTime, endTime, recurrence,
				serviceContext);

		long calendarBookingId = calendarBooking.getCalendarBookingId();

		assertCalendarBookingInstancesCount(calendarBookingId, 3);

		recurrence = RecurrenceTestUtil.getDailyRecurrence(
			_losAngelesTimeZone, startTimeJCalendar);

		_calendarBookingLocalService.updateCalendarBooking(
			calendarBooking.getUserId(), calendarBookingId,
			calendar.getCalendarId(), calendarBooking.getTitleMap(),
			calendarBooking.getDescriptionMap(), calendarBooking.getLocation(),
			startTime, endTime, false,
			RecurrenceSerializer.serialize(recurrence),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(), serviceContext);

		assertCalendarBookingInstancesCount(calendarBookingId, 1);
	}

	@Test
	public void testUpdateStagedSiteCalendarBookingShouldUpdateInviteSiteCalendarBooking()
		throws Exception {

		Group liveGroup = GroupTestUtil.addGroup();

		Layout targetLayout = LayoutTestUtil.addTypePortletLayout(liveGroup);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(liveGroup, true);

		Calendar stagingCalendar = CalendarStagingTestUtil.getStagingCalendar(
			liveGroup, liveCalendar);

		Group group = GroupTestUtil.addGroup();

		Calendar invitedCalendar = CalendarTestUtil.getDefaultCalendar(group);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				stagingCalendar, invitedCalendar);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		List<CalendarBooking> invitedCalendarBookings =
			_calendarBookingLocalService.getCalendarBookings(
				liveCalendar.getCalendarId());

		Assert.assertEquals(
			invitedCalendarBookings.toString(), 1,
			invitedCalendarBookings.size());

		CalendarBooking invitedCalendarBooking = invitedCalendarBookings.get(0);

		ServiceContext serviceContext = createServiceContext();

		invitedCalendarBooking = _calendarBookingLocalService.updateStatus(
			_user.getUserId(), invitedCalendarBooking,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		CalendarBookingTestUtil.updateCalendarBooking(
			childCalendarBooking.getParentCalendarBooking(), titleMap,
			serviceContext);

		CalendarStagingTestUtil.publishPortlet(liveGroup, targetLayout, true);

		CalendarStagingTestUtil.publishLayouts(liveGroup, true);

		EntityCacheUtil.clearCache();

		invitedCalendarBooking =
			_calendarBookingLocalService.getCalendarBooking(
				invitedCalendarBooking.getCalendarBookingId());

		Assert.assertEquals(
			titleMap.get(LocaleUtil.US),
			invitedCalendarBooking.getTitle(LocaleUtil.US));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertCalendar(
		CalendarBooking calendarBooking, Calendar calendar) {

		Assert.assertEquals(
			calendar.getCalendarId(), calendarBooking.getCalendarId());
	}

	protected void assertCalendarBookingInstancesCount(
			long calendarBookingId, int count)
		throws PortalException {

		for (int i = 0; i < count; i++) {
			Assert.assertNotNull(
				_calendarBookingLocalService.getCalendarBookingInstance(
					calendarBookingId, i));
		}

		Assert.assertNull(
			_calendarBookingLocalService.getCalendarBookingInstance(
				calendarBookingId, count));
	}

	protected void assertCalendarBookingPeriod(
		CalendarBooking calendarBooking, java.util.Calendar startTimeJCalendar,
		java.util.Calendar endTimeJCalendar) {

		java.util.Calendar instanceStartTimeJCalendar =
			JCalendarUtil.getJCalendar(
				calendarBooking.getStartTime(), calendarBooking.getTimeZone());

		assertSameDay(startTimeJCalendar, instanceStartTimeJCalendar);

		assertSameTime(startTimeJCalendar, instanceStartTimeJCalendar);

		java.util.Calendar instanceEndTimeJCalendar =
			JCalendarUtil.getJCalendar(
				calendarBooking.getEndTime(), calendarBooking.getTimeZone());

		assertSameDay(endTimeJCalendar, instanceEndTimeJCalendar);

		assertSameTime(endTimeJCalendar, instanceEndTimeJCalendar);
	}

	protected void assertCalendarBookingsCount(Calendar calendar, int count) {
		List<CalendarBooking> calendarBookings =
			_calendarBookingLocalService.getCalendarBookings(
				calendar.getCalendarId());

		Assert.assertEquals(
			calendarBookings.toString(), count, calendarBookings.size());
	}

	protected void assertDescriptions(
		List<CalendarBooking> calendarBookings,
		Map<Locale, String> descriptionMap) {

		for (CalendarBooking calendarBooking : calendarBookings) {
			Assert.assertEquals(
				descriptionMap, calendarBooking.getDescriptionMap());
		}
	}

	protected void assertDoesNotRepeat(CalendarBooking calendarBooking) {
		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBooking.getCalendarBookingId());

		Assert.assertNull(calendarBooking.getRecurrenceObj());
	}

	protected void assertMailBody(String subject, String expectedBody) {
		List<MailMessage> mailMessages = MailServiceTestUtil.getMailMessages(
			"Subject", subject);

		Assert.assertFalse(mailMessages.toString(), mailMessages.isEmpty());

		MailMessage mailMessage = mailMessages.get(0);

		String body = mailMessage.getBody();

		Assert.assertTrue(body.contains(expectedBody));
	}

	protected void assertMailSubjectCount(String messageSubject, int count) {
		List<MailMessage> mailMessages = MailServiceTestUtil.getMailMessages(
			"Subject", messageSubject);

		Assert.assertEquals(
			mailMessages.toString(), count, mailMessages.size());
	}

	protected void assertRepeatsExceptFor(
		CalendarBooking calendarBooking,
		java.util.Calendar exceptionJCalendar) {

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBooking.getCalendarBookingId());

		Recurrence recurrence = calendarBooking.getRecurrenceObj();

		List<java.util.Calendar> exceptionJCalendars =
			recurrence.getExceptionJCalendars();

		Assert.assertEquals(
			exceptionJCalendars.toString(), 1, exceptionJCalendars.size());

		assertSameDay(exceptionJCalendar, exceptionJCalendars.get(0));
	}

	protected void assertRepeatsUntil(
		CalendarBooking calendarBooking, java.util.Calendar untilJCalendar) {

		calendarBooking = _calendarBookingLocalService.fetchCalendarBooking(
			calendarBooking.getCalendarBookingId());

		Recurrence recurrence = calendarBooking.getRecurrenceObj();

		assertSameDay(untilJCalendar, recurrence.getUntilJCalendar());
	}

	protected void assertSameDay(
		java.util.Calendar expectedJCalendar,
		java.util.Calendar actualJCalendar) {

		Assert.assertNotNull(expectedJCalendar);
		Assert.assertNotNull(actualJCalendar);

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.YEAR),
			actualJCalendar.get(java.util.Calendar.YEAR));

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.MONTH),
			actualJCalendar.get(java.util.Calendar.MONTH));

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.DAY_OF_MONTH),
			actualJCalendar.get(java.util.Calendar.DAY_OF_MONTH));
	}

	protected void assertSameTime(
		java.util.Calendar expectedJCalendar,
		java.util.Calendar actualJCalendar) {

		Assert.assertNotNull(expectedJCalendar);
		Assert.assertNotNull(actualJCalendar);

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.HOUR),
			actualJCalendar.get(java.util.Calendar.HOUR));

		Assert.assertEquals(
			expectedJCalendar.get(java.util.Calendar.MINUTE),
			actualJCalendar.get(java.util.Calendar.MINUTE));
	}

	protected void assertSentEmail(User toUser) {
		List<MailMessage> mailMessages = MailServiceTestUtil.getMailMessages(
			"To",
			StringBundler.concat(
				toUser.getFullName(), StringPool.SPACE, StringPool.LESS_THAN,
				toUser.getEmailAddress(), StringPool.GREATER_THAN));

		Assert.assertFalse(mailMessages.toString(), mailMessages.isEmpty());
	}

	protected void assertStatus(CalendarBooking calendarBooking, int status) {
		Assert.assertEquals(status, calendarBooking.getStatus());
	}

	protected ServiceContext createServiceContext() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(_user.getCompanyId());
		serviceContext.setUserId(_user.getUserId());

		return serviceContext;
	}

	protected CalendarBooking getChildCalendarBooking(
		CalendarBooking calendarBooking) {

		List<CalendarBooking> childCalendarBookings =
			calendarBooking.getChildCalendarBookings();

		CalendarBooking childCalendarBooking = childCalendarBookings.get(0);

		if (childCalendarBooking.isMasterBooking()) {
			childCalendarBooking = childCalendarBookings.get(1);
		}

		return childCalendarBooking;
	}

	protected void partitionCalendarBookingListByStartTime(
		List<CalendarBooking> calendarBookings, long startTime,
		List<CalendarBooking> earlierCalendarBookings,
		List<CalendarBooking> laterCalendarBookings) {

		for (CalendarBooking calendarBooking : calendarBookings) {
			if (calendarBooking.getStartTime() >= startTime) {
				laterCalendarBookings.add(calendarBooking);
			}
			else {
				earlierCalendarBookings.add(calendarBooking);
			}
		}
	}

	private void _activateWorkflow(Group group) throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			group.getGroupId(), CalendarBooking.class.getName(), 0, 0,
			"Single Approver", 1);
	}

	private void _completeWorkflow(Group group) throws Exception {
		for (WorkflowTask workflowTask : _getWorkflowTasks()) {
			workflowTask = _workflowTaskManager.assignWorkflowTaskToUser(
				group.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
				StringPool.BLANK, null, null);

			Assert.assertEquals(
				TestPropsValues.getUserId(), workflowTask.getAssigneeUserId());

			workflowTask = _workflowTaskManager.completeWorkflowTask(
				group.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), Constants.APPROVE,
				StringPool.BLANK, null);

			Assert.assertTrue(workflowTask.isCompleted());
		}
	}

	private List<WorkflowTask> _getWorkflowTasks() throws Exception {
		List<WorkflowTask> workflowTasks = new ArrayList<>();

		workflowTasks.addAll(
			_workflowTaskManager.getWorkflowTasksByUserRoles(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
		workflowTasks.addAll(
			_workflowTaskManager.getWorkflowTasksByUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		return workflowTasks;
	}

	private static final TimeZone _losAngelesTimeZone = TimeZone.getTimeZone(
		"America/Los_Angeles");
	private static final TimeZone _utcTimeZone = TimeZoneUtil.getTimeZone(
		StringPool.UTC);

	@Inject
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Inject
	private CalendarLocalService _calendarLocalService;

	@Inject
	private CalendarResourceLocalService _calendarResourceLocalService;

	private User _user;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}