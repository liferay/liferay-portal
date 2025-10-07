/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function toDateRangeDate(date: Date) {
	return new Intl.DateTimeFormat('en-US', {
		day: '2-digit',
		month: '2-digit',
		timeZone: 'UTC',
		year: 'numeric',
	}).format(date);
}

export function toDateRangeTime(date: Date) {
	return new Intl.DateTimeFormat('en-US', {
		hour: '2-digit',
		hourCycle: 'h23',
		minute: '2-digit',
		timeZone: 'UTC',
	}).format(date);
}
