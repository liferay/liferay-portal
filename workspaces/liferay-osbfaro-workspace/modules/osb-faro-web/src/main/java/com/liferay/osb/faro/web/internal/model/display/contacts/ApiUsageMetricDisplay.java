/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

/**
 * @author Nilton Vieira
 */
public class ApiUsageMetricDisplay {

	public ApiUsageMetricDisplay(long apiCallsCount, String dateString) {
		_apiCallsCount = apiCallsCount;
		_dateString = dateString;
	}

	public long getApiCallsCount() {
		return _apiCallsCount;
	}

	public String getDateString() {
		return _dateString;
	}

	private final long _apiCallsCount;
	private final String _dateString;

}