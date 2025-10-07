/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.info.item.provider;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.provider.BaseInfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemDetailsProvider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "item.class.name=com.liferay.calendar.model.CalendarBooking",
	service = InfoItemDetailsProvider.class
)
public class CalendarBookingInfoItemDetailsProvider
	extends BaseInfoItemDetailsProvider<CalendarBooking> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(CalendarBooking.class.getName());
	}

	@Override
	protected InfoItemIdentifierFactory<CalendarBooking>
		getInfoItemIdentifierFactory() {

		return new InfoItemIdentifierFactory<>() {

			@Override
			public ClassPKInfoItemIdentifier createClassPKInfoItemIdentifier(
				CalendarBooking calendarBooking) {

				return new ClassPKInfoItemIdentifier(
					calendarBooking.getCalendarBookingId());
			}

			@Override
			public ERCInfoItemIdentifier createERCInfoItemIdentifier(
				String externalReferenceCode,
				String scopeExternalReferenceCode) {

				return new ERCInfoItemIdentifier(
					externalReferenceCode, scopeExternalReferenceCode);
			}

		};
	}

}