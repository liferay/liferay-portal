/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class DateRangeUtilTest {

	@Test
	public void testGetEndDate() {
		Assert.assertNull(DateRangeUtil.getEndDate(null, null));

		Assert.assertEquals(
			_toDate(LocalDate.of(2026, 8, 31)),
			DateRangeUtil.getEndDate("2026-08-31", null));

		LocalDate loalDate = LocalDate.now(ZoneOffset.UTC);

		Assert.assertEquals(
			_toDate(loalDate),
			DateRangeUtil.getEndDate("2020-01-01", "LAST_30_DAYS"));
		Assert.assertEquals(
			_toDate(loalDate), DateRangeUtil.getEndDate(null, "LAST_24_HOURS"));
		Assert.assertEquals(
			_toDate(loalDate.minusDays(1)),
			DateRangeUtil.getEndDate(null, "YESTERDAY"));
	}

	@Test
	public void testGetStartDate() {
		Assert.assertNull(DateRangeUtil.getStartDate(null, null));

		Assert.assertEquals(
			_toDate(LocalDate.of(2026, 8, 1)),
			DateRangeUtil.getStartDate(null, "2026-08-01"));

		LocalDate loalDate = LocalDate.now(ZoneOffset.UTC);

		Assert.assertEquals(
			_toDate(loalDate.minusDays(30)),
			DateRangeUtil.getStartDate("LAST_30_DAYS", "2020-01-01"));
		Assert.assertEquals(
			_toDate(loalDate.minusDays(1)),
			DateRangeUtil.getStartDate("LAST_24_HOURS", null));
		Assert.assertEquals(
			_toDate(loalDate.minusDays(1)),
			DateRangeUtil.getStartDate("YESTERDAY", null));
	}

	private Date _toDate(LocalDate localDate) {
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC);

		return Date.from(zonedDateTime.toInstant());
	}

}