/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.info.item.provider;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.BaseInfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"item.class.name=com.liferay.calendar.model.CalendarBooking",
		"service.ranking:Integer=100"
	},
	service = InfoItemObjectProvider.class
)
public class CalendarBookingInfoItemObjectProvider
	extends BaseInfoItemObjectProvider<CalendarBooking> {

	@Override
	protected CalendarBooking doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			CalendarBooking calendarBooking =
				_calendarBookingLocalService.fetchCalendarBooking(
					classPKInfoItemIdentifier.getClassPK());

			if (calendarBooking == null) {
				throw new NoSuchInfoItemException(
					"Unable to get calendar booking with info item " +
						"identifier " + infoItemIdentifier);
			}

			return calendarBooking;
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		CalendarBooking calendarBooking =
			_calendarBookingLocalService.
				fetchCalendarBookingByExternalReferenceCode(
					ercInfoItemIdentifier.getExternalReferenceCode(), groupId);

		if (calendarBooking == null) {
			throw new NoSuchInfoItemException(
				"Unable to get calendar booking with info item identifier " +
					infoItemIdentifier);
		}

		return calendarBooking;
	}

	@Reference
	private CalendarBookingLocalService _calendarBookingLocalService;

}