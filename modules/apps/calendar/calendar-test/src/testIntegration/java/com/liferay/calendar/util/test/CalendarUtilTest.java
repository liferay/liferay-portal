/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.service.CalendarResourceService;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarTestUtil;
import com.liferay.calendar.test.util.RecurrenceTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Adam Brandizzi
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CalendarUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle testBundle = FrameworkUtil.getBundle(CalendarUtilTest.class);

		Bundle calendarWebBundle = BundleUtil.getBundle(
			testBundle.getBundleContext(), "com.liferay.calendar.web");

		BundleWiring bundleWiring = calendarWebBundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		_calendarUtilClass = classLoader.loadClass(
			"com.liferay.calendar.web.internal.util.CalendarUtil");
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_permissionChecker = PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_permissionChecker);
	}

	@Test
	public void testToCalendarBookingJSONObjectSendLastInstanceRecurrenceWithAllFollowingInstanceFromChildRecurringInstance()
		throws Exception {

		CalendarBooking calendarBookingInstance =
			getCalendarBookingChildAllFollowingInstance();

		Method method = _calendarUtilClass.getMethod(
			"toCalendarBookingJSONObject", ThemeDisplay.class,
			CalendarBooking.class, TimeZone.class);

		JSONObject jsonObject = (JSONObject)method.invoke(
			null, createThemeDisplay(), calendarBookingInstance,
			calendarBookingInstance.getTimeZone());

		Recurrence recurrence = RecurrenceSerializer.deserialize(
			jsonObject.getString("recurrence"),
			calendarBookingInstance.getTimeZone());

		assertRepeatsForever(recurrence);
	}

	@Test
	public void testToCalendarBookingJSONObjectSendLastInstanceRecurrenceWithAllFollowingInstanceFromParentRecurringInstance()
		throws Exception {

		CalendarBooking calendarBookingInstance =
			getCalendarBookingChildAllFollowingInstance();

		CalendarBooking calendarBooking =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getRecurringCalendarBookingId());

		Method method = _calendarUtilClass.getMethod(
			"toCalendarBookingJSONObject", ThemeDisplay.class,
			CalendarBooking.class, TimeZone.class);

		JSONObject jsonObject = (JSONObject)method.invoke(
			null, createThemeDisplay(), calendarBooking,
			calendarBookingInstance.getTimeZone());

		Recurrence recurrence = RecurrenceSerializer.deserialize(
			jsonObject.getString("recurrence"), calendarBooking.getTimeZone());

		assertRepeatsForever(recurrence);
	}

	@Test
	public void testToCalendarBookingJSONObjectSendLastInstanceRecurrenceWithSingleInstanceFromChildRecurringInstance()
		throws Exception {

		CalendarBooking calendarBookingInstance =
			getCalendarBookingChildSingleInstance();

		Method method = _calendarUtilClass.getMethod(
			"toCalendarBookingJSONObject", ThemeDisplay.class,
			CalendarBooking.class, TimeZone.class);

		JSONObject jsonObject = (JSONObject)method.invoke(
			null, createThemeDisplay(), calendarBookingInstance,
			calendarBookingInstance.getTimeZone());

		Recurrence recurrence = RecurrenceSerializer.deserialize(
			jsonObject.getString("recurrence"),
			calendarBookingInstance.getTimeZone());

		assertRepeatsForever(recurrence);
	}

	@Test
	public void testToCalendarBookingJSONObjectSendLastInstanceRecurrenceWithSingleInstanceFromParentRecurringInstance()
		throws Exception {

		CalendarBooking calendarBookingInstance =
			getCalendarBookingChildSingleInstance();

		CalendarBooking calendarBooking =
			_calendarBookingLocalService.fetchCalendarBooking(
				calendarBookingInstance.getRecurringCalendarBookingId());

		Method method = _calendarUtilClass.getMethod(
			"toCalendarBookingJSONObject", ThemeDisplay.class,
			CalendarBooking.class, TimeZone.class);

		JSONObject jsonObject = (JSONObject)method.invoke(
			null, createThemeDisplay(), calendarBookingInstance,
			calendarBookingInstance.getTimeZone());

		Recurrence recurrence = RecurrenceSerializer.deserialize(
			jsonObject.getString("recurrence"), calendarBooking.getTimeZone());

		assertRepeatsForever(recurrence);
	}

	@Test
	public void testToCalendarBookingJSONObjectVulnerabilities()
		throws Exception {

		ServiceContext serviceContext = createServiceContext();

		CalendarResource calendarResource =
			_calendarResourceLocalService.addCalendarResource(
				_user.getUserId(), TestPropsValues.getGroupId(),
				_classNameLocalService.getClassNameId(CalendarResource.class),
				0, null, null,
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					"lp'\"></option><img onerror=alert(document.location) " +
						"src=x>"
				).build(),
				RandomTestUtil.randomLocaleStringMap(), true, serviceContext);

		Calendar calendar = _calendarLocalService.addCalendar(
			_user.getUserId(), TestPropsValues.getGroupId(),
			calendarResource.getCalendarResourceId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), StringPool.UTC, 0, false,
			false, false, serviceContext);

		CalendarBooking calendarBookingInstance =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_user, calendar, RecurrenceTestUtil.getDailyRecurrence(),
				serviceContext);

		Method method = _calendarUtilClass.getMethod(
			"toCalendarBookingJSONObject", ThemeDisplay.class,
			CalendarBooking.class, TimeZone.class);

		JSONObject jsonObject = (JSONObject)method.invoke(
			null, createThemeDisplay(), calendarBookingInstance,
			calendarBookingInstance.getTimeZone());

		Assert.assertEquals(
			"lp&#39;&#34;&gt;&lt;/option&gt;&lt;img " +
				"onerror=alert(document.location) src=x&gt;",
			jsonObject.get("calendarResourceName"));
	}

	@Test
	public void testToCalendarBookingJSONObjectWorksWithoutManageBookingsPermission()
		throws Exception {

		_privateUser = UserTestUtil.addUser();

		Calendar calendar = CalendarTestUtil.addCalendar(_privateUser);

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRecurringCalendarBooking(
				_privateUser, calendar, RecurrenceTestUtil.getDailyRecurrence(),
				serviceContext);

		Method method = _calendarUtilClass.getMethod(
			"toCalendarBookingJSONObject", ThemeDisplay.class,
			CalendarBooking.class, TimeZone.class);

		method.invoke(
			null, createThemeDisplay(), calendarBooking,
			calendarBooking.getTimeZone());
	}

	@Test
	public void testToCalendarBookingsJSONArray() throws Exception {
		CalendarBooking approved =
			CalendarBookingTestUtil.addPublishedCalendarBooking(_user);

		CalendarBooking sameUserDraft =
			CalendarBookingTestUtil.addDraftCalendarBooking(_user);

		User user2 = UserTestUtil.addUser();

		CalendarBooking anotherUserDraft =
			CalendarBookingTestUtil.addDraftCalendarBooking(user2);

		List<CalendarBooking> calendarBookings = getCalendarBookings(
			approved, sameUserDraft, anotherUserDraft);

		Method method = _calendarUtilClass.getMethod(
			"toCalendarBookingsJSONArray", ThemeDisplay.class, List.class,
			TimeZone.class);

		JSONArray jsonArray = (JSONArray)method.invoke(
			null, createThemeDisplay(), calendarBookings,
			TimeZoneUtil.getDefault());

		Assert.assertEquals(2, jsonArray.length());

		Set<Long> actualCalendarBookingIds = getCalendarBookingIds(jsonArray);

		Set<Long> expectedCalendarBookingIds = getCalendarBookingIds(
			calendarBookings);

		expectedCalendarBookingIds.remove(
			anotherUserDraft.getCalendarBookingId());

		Assert.assertEquals(
			expectedCalendarBookingIds, actualCalendarBookingIds);
	}

	@Test
	public void testToCalendarJSONObject() throws Exception {
		String maliciousScript = "'\"></option><img onerror=alert(123) src=x>";

		CalendarResource calendarResource =
			_calendarResourceLocalService.addCalendarResource(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				_classNameLocalService.getClassNameId(CalendarResource.class),
				0, null, null,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), maliciousScript
				).build(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new ServiceContext());

		Calendar calendar = _calendarLocalService.addCalendar(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			calendarResource.getCalendarResourceId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), maliciousScript
			).build(),
			RandomTestUtil.randomLocaleStringMap(), StringPool.UTC, 0, false,
			false, false, new ServiceContext());

		Method method = _calendarUtilClass.getMethod(
			"toCalendarJSONObject", ThemeDisplay.class, Calendar.class);

		JSONObject jsonObject = (JSONObject)method.invoke(
			null, createThemeDisplay(), calendar);

		String escapedMaliciousScript =
			"&#39;&#34;&gt;&lt;/option&gt;&lt;img onerror=alert(123) src=x&gt;";

		Assert.assertEquals(
			escapedMaliciousScript, jsonObject.get("calendarResourceName"));
		Assert.assertEquals(escapedMaliciousScript, jsonObject.get("name"));
	}

	protected void assertRepeatsForever(Recurrence recurrence) {
		Assert.assertNotNull(recurrence);

		Assert.assertNull(recurrence.getUntilJCalendar());

		Assert.assertEquals(0, recurrence.getCount());
	}

	protected ServiceContext createServiceContext() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(_user.getCompanyId());
		serviceContext.setScopeGroupId(_user.getGroupId());
		serviceContext.setUserId(_user.getUserId());

		return serviceContext;
	}

	protected ThemeDisplay createThemeDisplay() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(_user);

		return themeDisplay;
	}

	protected CalendarBooking getCalendarBookingChildAllFollowingInstance()
		throws PortalException {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		return CalendarBookingTestUtil.
			updateCalendarBookingInstanceAndAllFollowing(
				calendarBooking, 2, RandomTestUtil.randomLocaleStringMap(),
				serviceContext);
	}

	protected CalendarBooking getCalendarBookingChildSingleInstance()
		throws PortalException {

		ServiceContext serviceContext = createServiceContext();

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addDailyRecurringCalendarBooking(
				_user, serviceContext);

		return CalendarBookingTestUtil.updateCalendarBookingInstance(
			calendarBooking, 2, RandomTestUtil.randomLocaleStringMap(),
			serviceContext);
	}

	protected Set<Long> getCalendarBookingIds(JSONArray jsonArray) {
		Set<Long> calendarBookingIds = new HashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			calendarBookingIds.add(jsonObject.getLong("calendarBookingId"));
		}

		return calendarBookingIds;
	}

	protected Set<Long> getCalendarBookingIds(
		List<CalendarBooking> calendarBookings) {

		Set<Long> calendarBookingIds = new HashSet<>();

		for (CalendarBooking calendarBooking : calendarBookings) {
			calendarBookingIds.add(calendarBooking.getCalendarBookingId());
		}

		return calendarBookingIds;
	}

	protected List<CalendarBooking> getCalendarBookings(
		CalendarBooking... calendarBookings) {

		return ListUtil.fromArray(calendarBookings);
	}

	private static Class<?> _calendarUtilClass;

	@Inject
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Inject
	private CalendarLocalService _calendarLocalService;

	@Inject
	private CalendarResourceLocalService _calendarResourceLocalService;

	@Inject
	private CalendarResourceService _calendarResourceService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;
	private PermissionChecker _permissionChecker;
	private User _privateUser;
	private User _user;

}