/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.util.Validator;

import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.Date;

/**
 * @author Leslie Wong
 */
public class DateRangeUtil {

	public static Date getEndDate(String rangeEnd, String rangeKey) {
		if (Validator.isNotNull(rangeKey)) {
			TimeRange timeRange = TimeRange.valueOf(rangeKey);

			if (timeRange == TimeRange.YESTERDAY) {
				return _toDate(
					LocalDate.now(
						ZoneOffset.UTC
					).minusDays(
						1
					));
			}

			return _toDate(LocalDate.now(ZoneOffset.UTC));
		}

		return _parse(rangeEnd);
	}

	public static Date getStartDate(String rangeKey, String rangeStart) {
		if (Validator.isNotNull(rangeKey)) {
			TimeRange timeRange = TimeRange.valueOf(rangeKey);

			return _toDate(
				LocalDate.now(
					ZoneOffset.UTC
				).minusDays(
					Math.max(1, timeRange.getRangeKey())
				));
		}

		return _parse(rangeStart);
	}

	private static Date _parse(String dateString) {
		if (Validator.isNull(dateString)) {
			return null;
		}

		return _toDate(LocalDate.parse(dateString));
	}

	private static Date _toDate(LocalDate localDate) {
		return Date.from(
			localDate.atStartOfDay(
				ZoneOffset.UTC
			).toInstant());
	}

}