/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class IntervalTest {

	@Test
	public void testGetGraphQLIntervalDefaultsToDay() {
		Assert.assertEquals("D", Interval.getGraphQLInterval(null));
	}

	@Test
	public void testGetGraphQLIntervalMapsEveryValue() {
		Assert.assertEquals("D", Interval.getGraphQLInterval("DAY"));
		Assert.assertEquals("M", Interval.getGraphQLInterval("MONTH"));
		Assert.assertEquals("W", Interval.getGraphQLInterval("WEEK"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetGraphQLIntervalRejectsUnknownValue() {
		Interval.getGraphQLInterval("HOUR");
	}

}