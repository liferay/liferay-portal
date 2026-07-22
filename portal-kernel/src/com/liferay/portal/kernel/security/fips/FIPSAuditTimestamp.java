/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Date;

/**
 * Formats FIPS audit event timestamps in a single canonical representation: UTC,
 * ISO 8601 extended form with millisecond precision and a literal <code>Z</code>
 * suffix (for example <code>2026-05-06T14:19:23.471Z</code>), sourced from the
 * host clock.
 *
 * <p>
 * Every FIPS audit event across §5.2 to §5.5 shares this format so the audit
 * trail keeps a stable time ordering across cluster nodes and downstream SIEM
 * correlation. Sub millisecond precision is truncated rather than rounded, and
 * the format is emitted in UTC regardless of the host default time zone.
 * </p>
 *
 * <p>
 * The timestamp carries millisecond precision only; it does not by itself
 * guarantee a unique ordering for two events emitted within the same
 * millisecond, so the §5.4 audit log integrity chain, not the timestamp, is the
 * authority on order.
 * </p>
 *
 * @author Jorge García Jiménez
 */
public class FIPSAuditTimestamp {

	public static String format(Instant instant) {
		return _dateTimeFormatter.format(instant.atZone(ZoneOffset.UTC));
	}

	public static String format(long epochMillis) {
		return format(Instant.ofEpochMilli(epochMillis));
	}

	public static String normalize(Date date) {
		return format(date.toInstant());
	}

	public static String normalize(String timestamp) {
		try {
			return format(Instant.parse(timestamp));
		}
		catch (DateTimeParseException dateTimeParseException1) {
			try {
				OffsetDateTime offsetDateTime = OffsetDateTime.parse(timestamp);

				return format(offsetDateTime.toInstant());
			}
			catch (DateTimeParseException dateTimeParseException2) {
				throw new IllegalArgumentException(
					"Unable to normalize timestamp \"" + timestamp + "\"",
					dateTimeParseException2);
			}
		}
	}

	public static String now() {
		return format(Instant.now());
	}

	private static final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

}