/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.upgrade.v4_2_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarUpgradeTestUtil;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Richard Jeremias
 */
@RunWith(Arquillian.class)
public class CalendarBookingCTUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		_calendarBooking = CalendarBookingTestUtil.addAllDayCalendarBooking(
			"Europe/Paris");

		return _calendarBooking;
	}

	@Override
	protected void deleteCTModel(long primaryKey) throws Exception {
		_calendarBookingLocalService.deleteCalendarBooking(primaryKey);
	}

	@Override
	protected CTService<?> getCTService() {
		return _calendarBookingLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = CalendarUpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.calendar.internal.upgrade.v4_2_1." +
				"CalendarBookingUpgradeProcess");

		upgradeProcess.upgrade();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) {
		CalendarBooking calendarBooking = (CalendarBooking)ctModel;

		calendarBooking.setStartTime(
			CalendarBookingTestUtil.getCalendarTimeInMillis(1, 23, 0));
		calendarBooking.setEndTime(
			CalendarBookingTestUtil.getCalendarTimeInMillis(2, 22, 59));

		return _calendarBookingLocalService.updateCalendarBooking(
			calendarBooking);
	}

	@DeleteAfterTestRun
	private CalendarBooking _calendarBooking;

	@Inject
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Inject(
		filter = "component.name=com.liferay.calendar.internal.upgrade.registry.CalendarServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}