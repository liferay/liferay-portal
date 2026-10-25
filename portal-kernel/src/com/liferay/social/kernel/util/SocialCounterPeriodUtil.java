/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Zsolt Berentey
 */
public class SocialCounterPeriodUtil {

	public static int getActivityDay() {
		return getActivityDay(System.currentTimeMillis());
	}

	public static int getActivityDay(Calendar calendar) {
		Date date = calendar.getTime();

		return getActivityDay(date.getTime());
	}

	public static int getActivityDay(long time) {
		return (int)((time / Time.DAY) - (_BASE_TIME / Time.DAY));
	}

	public static int getEndPeriod() {
		if (_isWithinPeriod(_startPeriod, _endPeriod, getActivityDay())) {
			return _endPeriod;
		}

		_endPeriod = getStartPeriod() + getPeriodLength() - 1;

		return _endPeriod;
	}

	public static int getPeriodLength() {
		if (_isMonthlyPeriod()) {
			if (_isWithinPeriod(_startPeriod, _endPeriod, getActivityDay())) {
				return _periodLength;
			}

			Calendar calendar = new GregorianCalendar();

			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			_startPeriod = getActivityDay(calendar);

			_periodLength = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

			_endPeriod = _startPeriod + _periodLength - 1;

			return _periodLength;
		}

		if (_periodLength == -1) {
			_periodLength = GetterUtil.getInteger(
				_SOCIAL_ACTIVITY_COUNTER_PERIOD_LENGTH);
		}

		return _periodLength;
	}

	public static int getStartPeriod() {
		if (_isWithinPeriod(_startPeriod, _endPeriod, getActivityDay())) {
			return _startPeriod;
		}

		if (_isMonthlyPeriod()) {
			Calendar calendar = new GregorianCalendar();

			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			_startPeriod = getActivityDay(calendar);

			return _startPeriod;
		}

		_startPeriod = getActivityDay() / getPeriodLength() * getPeriodLength();

		return _startPeriod;
	}

	private static boolean _isMonthlyPeriod() {
		return _SOCIAL_ACTIVITY_COUNTER_PERIOD_LENGTH.equals("month");
	}

	private static boolean _isWithinPeriod(
		int startPeriod, int endPeriod, int activityDay) {

		if ((activityDay >= startPeriod) && (activityDay <= endPeriod)) {
			return true;
		}

		return false;
	}

	private static final long _BASE_TIME = new GregorianCalendar(
		2011, Calendar.JANUARY, 1
	).getTimeInMillis();

	private static final String _SOCIAL_ACTIVITY_COUNTER_PERIOD_LENGTH =
		PropsUtil.get(PropsKeys.SOCIAL_ACTIVITY_COUNTER_PERIOD_LENGTH);

	private static int _endPeriod;
	private static int _periodLength = -1;
	private static int _startPeriod;

}