/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eduardo Lundgren
 * @generated
 */
@ProviderType
public interface CalendarBookingFinder {

	public java.util.List<com.liferay.calendar.model.CalendarBooking>
		findByFutureReminders(long startTime);

	public java.util.List<com.liferay.calendar.model.CalendarBooking>
		findByKeywords(
			long companyId, long[] groupIds, long[] calendarIds,
			long[] calendarResourceIds, long parentCalendarBookingId,
			String keywords, long startTime, long endTime, boolean recurring,
			int[] statuses, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarBooking> orderByComparator);

	public java.util.List<com.liferay.calendar.model.CalendarBooking>
		findByC_G_C_C_P_T_D_L_S_E_S(
			long companyId, long[] groupIds, long[] calendarIds,
			long[] calendarResourceIds, long parentCalendarBookingId,
			String title, String description, String location, long startTime,
			long endTime, boolean recurring, int[] statuses,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarBooking> orderByComparator);

	public java.util.List<com.liferay.calendar.model.CalendarBooking>
		findByC_G_C_C_P_T_D_L_S_E_S(
			long companyId, long[] groupIds, long[] calendarIds,
			long[] calendarResourceIds, long parentCalendarBookingId,
			String[] titles, String[] descriptions, String[] locations,
			long startTime, long endTime, boolean recurring, int[] statuses,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarBooking> orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:1876152090