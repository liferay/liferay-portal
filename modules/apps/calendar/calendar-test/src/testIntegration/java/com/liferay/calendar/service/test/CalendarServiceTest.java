/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.service.CalendarService;
import com.liferay.calendar.test.util.CalendarStagingTestUtil;
import com.liferay.calendar.util.CalendarResourceUtil;
import com.liferay.calendar.util.comparator.CalendarNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CalendarServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@After
	public void tearDown() {
		CalendarStagingTestUtil.cleanUp();
	}

	@Test
	public void testIsManageableFromGroup() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(_liveGroup);

		_notStagedGroup = GroupTestUtil.addGroup();

		Group stagingGroup = _liveGroup.getStagingGroup();

		_adminUser = UserTestUtil.addOmniadminUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_adminUser)) {

			Calendar notStagedCalendar = getGroupCalendar(_notStagedGroup);

			assertManageableFromGroup(notStagedCalendar, _notStagedGroup);
			assertManageableFromGroup(notStagedCalendar, _liveGroup);
			assertManageableFromGroup(notStagedCalendar, stagingGroup);

			Calendar liveCalendar = getGroupCalendar(_liveGroup);

			assertNotManageableFromGroup(liveCalendar, _notStagedGroup);
			assertNotManageableFromGroup(liveCalendar, _liveGroup);
			assertNotManageableFromGroup(liveCalendar, stagingGroup);

			Calendar stagingCalendar = getGroupCalendar(stagingGroup);

			assertNotManageableFromGroup(stagingCalendar, _notStagedGroup);
			assertNotManageableFromGroup(stagingCalendar, _liveGroup);
			assertManageableFromGroup(stagingCalendar, stagingGroup);
		}
	}

	@Test
	public void testSearch() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		_adminUser = UserTestUtil.addOmniadminUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_adminUser)) {

			ServiceContext serviceContext = new ServiceContext();

			CalendarResource calendarResource =
				CalendarResourceUtil.getGroupCalendarResource(
					_liveGroup.getGroupId(), serviceContext);

			Locale locale = LocaleUtil.getSiteDefault();

			Map<Locale, String> nameMap = RandomTestUtil.randomLocaleStringMap(
				locale);

			Calendar expectedCalendar = _calendarLocalService.addCalendar(
				_adminUser.getUserId(), _liveGroup.getGroupId(),
				calendarResource.getCalendarResourceId(), nameMap,
				RandomTestUtil.randomLocaleStringMap(), StringPool.UTC,
				RandomTestUtil.randomInt(0, 255), false, false, false,
				serviceContext);

			_calendarLocalService.addCalendar(
				_adminUser.getUserId(), _liveGroup.getGroupId(),
				calendarResource.getCalendarResourceId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), StringPool.UTC,
				RandomTestUtil.randomInt(0, 255), false, false, false,
				serviceContext);

			List<Calendar> actualCalendars = _calendarService.search(
				_liveGroup.getCompanyId(), new long[] {_liveGroup.getGroupId()},
				new long[] {calendarResource.getCalendarResourceId()},
				nameMap.get(locale), true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				CalendarNameComparator.getInstance(false));

			Assert.assertEquals(
				actualCalendars.toString(), 1, actualCalendars.size());

			Calendar actualCalendar = actualCalendars.get(0);

			Assert.assertEquals(
				expectedCalendar.getCalendarId(),
				actualCalendar.getCalendarId());
			Assert.assertEquals(
				expectedCalendar.getNameMap(), actualCalendar.getNameMap());
		}
	}

	protected void assertManageableFromGroup(Calendar calendar, Group group)
		throws PortalException {

		Assert.assertTrue(
			_calendarService.isManageableFromGroup(
				calendar.getCalendarId(), group.getGroupId()));
	}

	protected void assertNotManageableFromGroup(Calendar calendar, Group group)
		throws PortalException {

		Assert.assertFalse(
			_calendarService.isManageableFromGroup(
				calendar.getCalendarId(), group.getGroupId()));
	}

	protected Calendar getGroupCalendar(Group group) throws Exception {
		CalendarResource calendarResource =
			_calendarResourceLocalService.fetchCalendarResource(
				PortalUtil.getClassNameId(Group.class), group.getGroupId());

		if (calendarResource == null) {
			Map<Locale, String> nameMap = HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), group.getDescriptiveName()
			).build();

			Map<Locale, String> descriptionMap = new HashMap<>();

			calendarResource =
				_calendarResourceLocalService.addCalendarResource(
					group.getCreatorUserId(), group.getGroupId(),
					PortalUtil.getClassNameId(Group.class), group.getGroupId(),
					null, null, nameMap, descriptionMap, true,
					new ServiceContext());
		}

		return calendarResource.getDefaultCalendar();
	}

	private User _adminUser;

	@Inject
	private CalendarLocalService _calendarLocalService;

	@Inject
	private CalendarResourceLocalService _calendarResourceLocalService;

	@Inject
	private CalendarService _calendarService;

	private Group _liveGroup;
	private Group _notStagedGroup;

}