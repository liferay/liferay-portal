/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.kernel.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface AnnouncementsEntryFinder {

	public int countByScopes(
		long companyId, long userId,
		java.util.LinkedHashMap<Long, long[]> scopes, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue);

	public java.util.List
		<com.liferay.announcements.kernel.model.AnnouncementsEntry>
			findByDisplayDate(
				java.util.Date displayDateLT, java.util.Date displayDateGT);

	public java.util.List
		<com.liferay.announcements.kernel.model.AnnouncementsEntry>
			findByScopes(
				long companyId, long userId,
				java.util.LinkedHashMap<Long, long[]> scopes,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean alert, int flagValue,
				int start, int end);

}
// LIFERAY-SERVICE-BUILDER-HASH:2094422698