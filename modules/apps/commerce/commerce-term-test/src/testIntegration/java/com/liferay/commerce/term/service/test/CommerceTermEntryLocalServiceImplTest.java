/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.term.constants.CommerceTermEntryConstants;
import com.liferay.commerce.term.exception.CommerceTermEntryNameException;
import com.liferay.commerce.term.exception.CommerceTermEntryPriorityException;
import com.liferay.commerce.term.exception.CommerceTermEntryTypeException;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommerceTermEntryLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_calendar = CalendarFactoryUtil.getCalendar(_user.getTimeZone());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@Test(expected = CommerceTermEntryTypeException.class)
	public void testAddCommerceTermEntryWithoutType() throws Exception {
		_commerceTermEntryLocalService.addCommerceTermEntry(
			null, _user.getUserId(), true,
			RandomTestUtil.randomLocaleStringMap(),
			_calendar.get(Calendar.MONTH), _calendar.get(Calendar.DAY_OF_MONTH),
			_calendar.get(Calendar.YEAR), _calendar.get(Calendar.HOUR_OF_DAY),
			_calendar.get(Calendar.MINUTE), _calendar.get(Calendar.MONTH),
			_calendar.get(Calendar.DAY_OF_MONTH), _calendar.get(Calendar.YEAR),
			_calendar.get(Calendar.HOUR_OF_DAY), _calendar.get(Calendar.MINUTE),
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), null,
			null, _serviceContext);
	}

	@Test(expected = CommerceTermEntryNameException.class)
	public void testAddDuplicateCommerceTermEntryName() throws Exception {
		String name = RandomTestUtil.randomString();

		_commerceTermEntryLocalService.addCommerceTermEntry(
			null, TestPropsValues.getUserId(), true,
			RandomTestUtil.randomLocaleStringMap(),
			_calendar.get(Calendar.MONTH), _calendar.get(Calendar.DAY_OF_MONTH),
			_calendar.get(Calendar.YEAR), _calendar.get(Calendar.HOUR_OF_DAY),
			_calendar.get(Calendar.MINUTE), _calendar.get(Calendar.MONTH),
			_calendar.get(Calendar.DAY_OF_MONTH), _calendar.get(Calendar.YEAR),
			_calendar.get(Calendar.HOUR_OF_DAY), _calendar.get(Calendar.MINUTE),
			true, RandomTestUtil.randomLocaleStringMap(), name,
			RandomTestUtil.randomDouble(),
			CommerceTermEntryConstants.TYPE_PAYMENT_TERMS, null,
			_serviceContext);

		_commerceTermEntryLocalService.addCommerceTermEntry(
			null, TestPropsValues.getUserId(), true,
			RandomTestUtil.randomLocaleStringMap(),
			_calendar.get(Calendar.MONTH), _calendar.get(Calendar.DAY_OF_MONTH),
			_calendar.get(Calendar.YEAR), _calendar.get(Calendar.HOUR_OF_DAY),
			_calendar.get(Calendar.MINUTE), _calendar.get(Calendar.MONTH),
			_calendar.get(Calendar.DAY_OF_MONTH), _calendar.get(Calendar.YEAR),
			_calendar.get(Calendar.HOUR_OF_DAY), _calendar.get(Calendar.MINUTE),
			true, RandomTestUtil.randomLocaleStringMap(), name,
			RandomTestUtil.randomDouble(),
			CommerceTermEntryConstants.TYPE_PAYMENT_TERMS, null,
			_serviceContext);
	}

	@Test(expected = CommerceTermEntryPriorityException.class)
	public void testAddDuplicateCommerceTermEntryPriority() throws Exception {
		double priority = RandomTestUtil.randomDouble();

		_commerceTermEntryLocalService.addCommerceTermEntry(
			null, TestPropsValues.getUserId(), true,
			RandomTestUtil.randomLocaleStringMap(),
			_calendar.get(Calendar.MONTH), _calendar.get(Calendar.DAY_OF_MONTH),
			_calendar.get(Calendar.YEAR), _calendar.get(Calendar.HOUR_OF_DAY),
			_calendar.get(Calendar.MINUTE), _calendar.get(Calendar.MONTH),
			_calendar.get(Calendar.DAY_OF_MONTH), _calendar.get(Calendar.YEAR),
			_calendar.get(Calendar.HOUR_OF_DAY), _calendar.get(Calendar.MINUTE),
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), priority,
			CommerceTermEntryConstants.TYPE_PAYMENT_TERMS, null,
			_serviceContext);

		_commerceTermEntryLocalService.addCommerceTermEntry(
			null, TestPropsValues.getUserId(), true,
			RandomTestUtil.randomLocaleStringMap(),
			_calendar.get(Calendar.MONTH), _calendar.get(Calendar.DAY_OF_MONTH),
			_calendar.get(Calendar.YEAR), _calendar.get(Calendar.HOUR_OF_DAY),
			_calendar.get(Calendar.MINUTE), _calendar.get(Calendar.MONTH),
			_calendar.get(Calendar.DAY_OF_MONTH), _calendar.get(Calendar.YEAR),
			_calendar.get(Calendar.HOUR_OF_DAY), _calendar.get(Calendar.MINUTE),
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), priority,
			CommerceTermEntryConstants.TYPE_PAYMENT_TERMS, null,
			_serviceContext);
	}

	private Calendar _calendar;

	@Inject
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}