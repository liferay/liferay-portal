/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Formats a date as a "YYYY-MM-DD" string, matching the "data-date"
 * attribute FullCalendar renders on each day cell so a test can locate a cell
 * by its date. Built from local date parts rather than "toISOString" so the
 * day never shifts across the UTC boundary.
 * For example: toDateString(new Date(2026, 5, 15)) // "2026-06-15"
 */
export function toDateString(date: Date): string {
	return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}
