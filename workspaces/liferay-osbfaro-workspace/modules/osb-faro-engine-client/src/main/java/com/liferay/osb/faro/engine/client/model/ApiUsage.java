/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Nilton Vieira
 */
public class ApiUsage {

	public long getCallsCount() {
		return _callsCount;
	}

	public String getDateString() {
		return _dateString;
	}

	public void setCallsCount(long callsCount) {
		_callsCount = callsCount;
	}

	public void setDateString(String dateString) {
		_dateString = dateString;
	}

	private long _callsCount;
	private String _dateString;

}