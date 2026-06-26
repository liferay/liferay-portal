/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

/**
 * Per-day usage metric for a single data source, returned by the data source
 * usage report endpoint.
 *
 * @author Caio Pinheiro
 */
public class DataSourceUsageMetric {

	public DataSourceUsageMetric(
		String dateString, long eventsCount, long knownIndividualsCount) {

		_dateString = dateString;
		_eventsCount = eventsCount;
		_knownIndividualsCount = knownIndividualsCount;
	}

	public String getDateString() {
		return _dateString;
	}

	public long getEventsCount() {
		return _eventsCount;
	}

	public long getKnownIndividualsCount() {
		return _knownIndividualsCount;
	}

	private final String _dateString;
	private final long _eventsCount;
	private final long _knownIndividualsCount;

}