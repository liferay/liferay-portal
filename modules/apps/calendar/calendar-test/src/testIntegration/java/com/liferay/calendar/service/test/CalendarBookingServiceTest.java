/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.service.CalendarBookingService;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarStagingTestUtil;
import com.liferay.calendar.test.util.CalendarTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lino Alves
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CalendarBookingServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_omnidminUser = UserTestUtil.addOmniadminUser();
		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();
	}

	@After
	public void tearDown() {
		CalendarStagingTestUtil.cleanUp();
	}

	@Test
	public void testGetCalendarBookingsWithEmptyStatuses() throws Exception {
		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(
			_user1, serviceContext);

		CalendarBookingTestUtil.addRegularCalendarBooking(
			_user1, calendar, serviceContext);

		List<CalendarBooking> calendarBookings =
			_calendarBookingService.getCalendarBookings(
				calendar.getCalendarId(), new int[0]);

		Assert.assertTrue(
			calendarBookings.toString(), calendarBookings.isEmpty());

		calendarBookings = _calendarBookingLocalService.getCalendarBookings(
			calendar.getCalendarId(), new int[0]);

		Assert.assertTrue(
			calendarBookings.toString(), calendarBookings.isEmpty());
	}

	@Test
	public void testGetUnapprovedCalendarBookingsForOmniadmin()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(
			_user1, serviceContext);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user1, calendar, serviceContext);

		calendarBooking.setStatus(WorkflowConstants.STATUS_PENDING);

		_calendarBookingLocalService.updateCalendarBooking(calendarBooking);

		int[] statuses = {WorkflowConstants.STATUS_PENDING};

		List<CalendarBooking> calendarBookings = Collections.emptyList();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_omnidminUser)) {

			calendarBookings = _calendarBookingService.getCalendarBookings(
				calendar.getCalendarId(), statuses);
		}

		Assert.assertTrue(!calendarBookings.isEmpty());
	}

	@Test
	public void testGetUnapprovedCalendarBookingsForRegularUser()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(
			_user1, serviceContext);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user1, calendar, serviceContext);

		calendarBooking.setStatus(WorkflowConstants.STATUS_PENDING);

		_calendarBookingLocalService.updateCalendarBooking(calendarBooking);

		int[] statuses = {WorkflowConstants.STATUS_PENDING};

		List<CalendarBooking> calendarBookings = Collections.emptyList();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			calendarBookings = _calendarBookingService.getCalendarBookings(
				calendar.getCalendarId(), statuses);
		}

		Assert.assertTrue(!calendarBookings.isEmpty());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user2)) {

			calendarBookings = _calendarBookingService.getCalendarBookings(
				calendar.getCalendarId(), statuses);
		}

		Assert.assertTrue(
			calendarBookings.toString(), calendarBookings.isEmpty());
	}

	@Test
	public void testLiveSiteCalendarInvitesStagingSiteCalendarShouldNotAppearOnLiveResourceUntilPublish()
		throws Exception {

		_liveGroup = GroupTestUtil.addGroup();

		Calendar invitingCalendar = CalendarTestUtil.addCalendar(_user1);

		Calendar liveCalendar = CalendarTestUtil.getDefaultCalendar(_liveGroup);

		CalendarStagingTestUtil.enableLocalStaging(_liveGroup, true);

		CalendarBooking childCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				invitingCalendar, liveCalendar);

		List<CalendarBooking> childCalendarBookings =
			_calendarBookingService.getChildCalendarBookings(
				childCalendarBooking.getParentCalendarBookingId(), true);

		Assert.assertEquals(
			childCalendarBookings.toString(), 2, childCalendarBookings.size());

		childCalendarBookings =
			_calendarBookingService.getChildCalendarBookings(
				childCalendarBooking.getParentCalendarBookingId(), false);

		Assert.assertEquals(
			childCalendarBookings.toString(), 1, childCalendarBookings.size());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUserWithoutPermissionInCalendarShouldNotViewCalendarBooking()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		Calendar calendar = CalendarTestUtil.addCalendar(
			_user1, serviceContext);

		deleteGuestAndUserPermission(calendar);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(
				_user1, calendar, serviceContext);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user2));

		_calendarBookingService.getCalendarBooking(
			calendarBooking.getCalendarBookingId());
	}

	protected ServiceContext createServiceContext() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(_user1.getCompanyId());
		serviceContext.setUserId(_user1.getUserId());

		return serviceContext;
	}

	protected void deleteGuestAndUserPermission(Calendar calendar)
		throws Exception {

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), Calendar.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(calendar.getPrimaryKey()), role.getRoleId(),
			new String[0]);

		role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), Calendar.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(calendar.getPrimaryKey()), role.getRoleId(),
			new String[0]);
	}

	@Inject
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Inject
	private CalendarBookingService _calendarBookingService;

	private Group _liveGroup;
	private User _omnidminUser;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user1;
	private User _user2;

}