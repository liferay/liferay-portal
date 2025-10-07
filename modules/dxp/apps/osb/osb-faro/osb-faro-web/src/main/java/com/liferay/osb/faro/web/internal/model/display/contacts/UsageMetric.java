/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

/**
 * @author Marcos Martins
 */
public class UsageMetric {

	public UsageMetric(
		String dateString, long knownIndividualsCount,
		long knownIndividualsCountSinceLastAnniversary, long pageViewsCount,
		long pageViewsCountSinceLastAnniversary) {

		_dateString = dateString;
		_knownIndividualsCount = knownIndividualsCount;
		_knownIndividualsCountSinceLastAnniversary =
			knownIndividualsCountSinceLastAnniversary;
		_pageViewsCount = pageViewsCount;
		_pageViewsCountSinceLastAnniversary =
			pageViewsCountSinceLastAnniversary;
	}

	public String getDateString() {
		return _dateString;
	}

	public long getKnownIndividualsCount() {
		return _knownIndividualsCount;
	}

	public long getKnownIndividualsCountSinceLastAnniversary() {
		return _knownIndividualsCountSinceLastAnniversary;
	}

	public long getPageViewsCount() {
		return _pageViewsCount;
	}

	public long getPageViewsCountSinceLastAnniversary() {
		return _pageViewsCountSinceLastAnniversary;
	}

	private final String _dateString;
	private final long _knownIndividualsCount;
	private final long _knownIndividualsCountSinceLastAnniversary;
	private final long _pageViewsCount;
	private final long _pageViewsCountSinceLastAnniversary;

}