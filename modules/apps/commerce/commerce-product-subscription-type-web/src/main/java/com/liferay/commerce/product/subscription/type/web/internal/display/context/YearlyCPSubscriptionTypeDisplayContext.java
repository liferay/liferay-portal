/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.subscription.type.web.internal.display.context;

import com.liferay.commerce.product.subscription.type.web.internal.constants.CPSubscriptionTypeConstants;
import com.liferay.commerce.product.subscription.type.web.internal.display.context.helper.CPSubscriptionTypeRequestHelper;
import com.liferay.commerce.product.subscription.type.web.internal.display.context.util.comparator.YearlyCPSubscriptionTypeCalendarMonthsComparator;
import com.liferay.commerce.util.CommerceSubscriptionTypeUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class YearlyCPSubscriptionTypeDisplayContext {

	public YearlyCPSubscriptionTypeDisplayContext(
		Object object, HttpServletRequest httpServletRequest, boolean payment) {

		_object = object;
		_payment = payment;

		_cpSubscriptionTypeRequestHelper = new CPSubscriptionTypeRequestHelper(
			httpServletRequest);
	}

	public List<Integer> getCalendarMonths() {
		List<Integer> calendarMonths = new ArrayList<>();

		Map<String, Integer> calendarMonthsDisplayNames =
			_getCalendarMonthsDisplayNames();

		for (Map.Entry<String, Integer> entry :
				calendarMonthsDisplayNames.entrySet()) {

			calendarMonths.add(entry.getValue());
		}

		calendarMonths.sort(
			new YearlyCPSubscriptionTypeCalendarMonthsComparator());

		return calendarMonths;
	}

	public int getMonthDay() {
		UnicodeProperties subscriptionTypeSettingsUnicodeProperties =
			CommerceSubscriptionTypeUtil.
				getSubscriptionTypeSettingsUnicodeProperties(_object, _payment);

		if ((subscriptionTypeSettingsUnicodeProperties == null) ||
			subscriptionTypeSettingsUnicodeProperties.isEmpty()) {

			return 1;
		}

		if (isPayment()) {
			return GetterUtil.getInteger(
				subscriptionTypeSettingsUnicodeProperties.get("monthDay"), 1);
		}

		return GetterUtil.getInteger(
			subscriptionTypeSettingsUnicodeProperties.get("deliveryMonthDay"),
			1);
	}

	public String getMonthDisplayName(int month) {
		Map<String, Integer> calendarMonthsDisplayNames =
			_getCalendarMonthsDisplayNames();

		for (Map.Entry<String, Integer> entry :
				calendarMonthsDisplayNames.entrySet()) {

			if (entry.getValue() == month) {
				return entry.getKey();
			}
		}

		return StringPool.BLANK;
	}

	public int getSelectedMonth() {
		UnicodeProperties subscriptionTypeSettingsUnicodeProperties =
			CommerceSubscriptionTypeUtil.
				getSubscriptionTypeSettingsUnicodeProperties(_object, _payment);

		if (subscriptionTypeSettingsUnicodeProperties == null) {
			return 0;
		}

		if (isPayment()) {
			return GetterUtil.getInteger(
				subscriptionTypeSettingsUnicodeProperties.get("month"));
		}

		return GetterUtil.getInteger(
			subscriptionTypeSettingsUnicodeProperties.get("deliveryMonth"));
	}

	public int getSelectedYearlyMode() {
		UnicodeProperties subscriptionTypeSettingsUnicodeProperties =
			CommerceSubscriptionTypeUtil.
				getSubscriptionTypeSettingsUnicodeProperties(_object, _payment);

		if (subscriptionTypeSettingsUnicodeProperties == null) {
			return CPSubscriptionTypeConstants.MODE_ORDER_DATE;
		}

		if (isPayment()) {
			return GetterUtil.getInteger(
				subscriptionTypeSettingsUnicodeProperties.get("yearlyMode"));
		}

		return GetterUtil.getInteger(
			subscriptionTypeSettingsUnicodeProperties.get(
				"deliveryYearlyMode"));
	}

	public boolean isPayment() {
		return _payment;
	}

	private Map<String, Integer> _getCalendarMonthsDisplayNames() {
		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_cpSubscriptionTypeRequestHelper.getLocale());

		return calendar.getDisplayNames(
			Calendar.MONTH, Calendar.LONG,
			_cpSubscriptionTypeRequestHelper.getLocale());
	}

	private final CPSubscriptionTypeRequestHelper
		_cpSubscriptionTypeRequestHelper;
	private final Object _object;
	private final boolean _payment;

}