/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Prepares a date picker value for saving and comparison. The date-time picker
 * emits `--:--` when only the date is filled, so the time defaults to midnight.
 * A value the browser cannot parse as a date becomes an empty string, and any
 * other value is returned unchanged.
 *
 * This is not a strict validator. The browser parses some dates that do not
 * exist, so `2026-02-30 --:--` becomes `2026-02-30 00:00` rather than an empty
 * string. `getValueErrorMessage` checks that a date is real and in the property's
 * format.
 *
 * normalizeDateTime('2026-10-12 14:30') // '2026-10-12 14:30'
 * normalizeDateTime('2026-10-12 --:--') // '2026-10-12 00:00'
 * normalizeDateTime('2026-10-12') // '2026-10-12'
 * normalizeDateTime('2026-13-01 10:00') // ''
 * normalizeDateTime('') // ''
 */
export default function normalizeDateTime(value: string) {
	if (!value) {
		return value;
	}

	const normalized = value.replace('--:--', '00:00');

	const date = new Date(normalized.replace(' ', 'T'));

	return Number.isNaN(date.getTime()) ? '' : normalized;
}
