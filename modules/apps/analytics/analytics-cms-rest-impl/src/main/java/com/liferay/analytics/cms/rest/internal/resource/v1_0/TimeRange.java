/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

/**
 * @author Leslie Wong
 */
public enum TimeRange {

	LAST_7_DAYS(7), LAST_24_HOURS(0), LAST_28_DAYS(28), LAST_30_DAYS(30),
	LAST_90_DAYS(90), LAST_180_DAYS(180), LAST_YEAR(365), YESTERDAY(1);

	public static Integer getRangeKey(String timeRangeString) {
		if (timeRangeString == null) {
			return null;
		}

		TimeRange timeRange = valueOf(timeRangeString);

		return timeRange.getRangeKey();
	}

	public int getRangeKey() {
		return _rangeKey;
	}

	private TimeRange(int rangeKey) {
		_rangeKey = rangeKey;
	}

	private final int _rangeKey;

}