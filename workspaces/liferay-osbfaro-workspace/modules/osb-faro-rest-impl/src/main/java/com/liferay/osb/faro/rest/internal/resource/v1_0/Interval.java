/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

/**
 * @author Leslie Wong
 */
public enum Interval {

	DAY("D"), MONTH("M"), WEEK("W");

	public static String getGraphQLInterval(String intervalString) {
		if (intervalString == null) {
			return DAY.getGraphQLInterval();
		}

		Interval interval = valueOf(intervalString);

		return interval.getGraphQLInterval();
	}

	public String getGraphQLInterval() {
		return _graphQLInterval;
	}

	private Interval(String graphQLInterval) {
		_graphQLInterval = graphQLInterval;
	}

	private final String _graphQLInterval;

}