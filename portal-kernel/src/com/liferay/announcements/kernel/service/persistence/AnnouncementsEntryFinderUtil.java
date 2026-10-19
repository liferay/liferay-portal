/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AnnouncementsEntryFinderUtil {

	public static int countByScopes(
		long companyId, long userId,
		java.util.LinkedHashMap<Long, long[]> scopes, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue) {

		return getFinder().countByScopes(
			companyId, userId, scopes, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, alert, flagValue);
	}

	public static java.util.List
		<com.liferay.announcements.kernel.model.AnnouncementsEntry>
			findByDisplayDate(
				java.util.Date displayDateLT, java.util.Date displayDateGT) {

		return getFinder().findByDisplayDate(displayDateLT, displayDateGT);
	}

	public static java.util.List
		<com.liferay.announcements.kernel.model.AnnouncementsEntry>
			findByScopes(
				long companyId, long userId,
				java.util.LinkedHashMap<Long, long[]> scopes,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean alert, int flagValue,
				int start, int end) {

		return getFinder().findByScopes(
			companyId, userId, scopes, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, alert, flagValue, start,
			end);
	}

	public static AnnouncementsEntryFinder getFinder() {
		if (_finder == null) {
			_finder = (AnnouncementsEntryFinder)PortalBeanLocatorUtil.locate(
				AnnouncementsEntryFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(AnnouncementsEntryFinder finder) {
		_finder = finder;
	}

	private static AnnouncementsEntryFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:2145325689