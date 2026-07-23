/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.time.Instant;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditTimestampTest {

	@Test
	public void testFormat() {
		Assert.assertEquals(
			_TIMESTAMP,
			FIPSAuditTimestamp.format(Instant.ofEpochMilli(_EPOCH_MILLIS)));
		Assert.assertEquals(
			_TIMESTAMP, FIPSAuditTimestamp.format(_EPOCH_MILLIS));

		Assert.assertEquals(
			"2025-05-06T14:19:23.000Z",
			FIPSAuditTimestamp.format(Instant.ofEpochSecond(_EPOCH_SECONDS)));
		Assert.assertEquals(
			"2025-05-06T14:19:23.123Z",
			FIPSAuditTimestamp.format(
				Instant.ofEpochSecond(_EPOCH_SECONDS, 123_999_999)));
	}

	@Test
	public void testFormatIsUTCIndependentOfDefaultTimeZone() {
		TimeZone timeZone = TimeZone.getDefault();

		try {
			TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

			Assert.assertEquals(
				_TIMESTAMP,
				FIPSAuditTimestamp.format(Instant.ofEpochMilli(_EPOCH_MILLIS)));
		}
		finally {
			TimeZone.setDefault(timeZone);
		}
	}

	@Test
	public void testNormalize() {
		Assert.assertEquals(
			_TIMESTAMP, FIPSAuditTimestamp.normalize(new Date(_EPOCH_MILLIS)));
		Assert.assertEquals(
			_TIMESTAMP, FIPSAuditTimestamp.normalize(_TIMESTAMP));
		Assert.assertEquals(
			_TIMESTAMP,
			FIPSAuditTimestamp.normalize("2025-05-06T16:19:23.471+02:00"));

		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> FIPSAuditTimestamp.normalize(RandomTestUtil.randomString()));
	}

	@Test
	public void testNow() {
		String now = FIPSAuditTimestamp.now();

		Assert.assertTrue(
			now,
			now.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"));
	}

	private static final long _EPOCH_MILLIS = 1746541163471L;

	private static final long _EPOCH_SECONDS = 1746541163L;

	private static final String _TIMESTAMP = "2025-05-06T14:19:23.471Z";

}