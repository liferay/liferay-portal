/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getFDSDateFormat(date: Date) {
	return new Intl.DateTimeFormat('en-US', {
		day: 'numeric',
		month: 'short',
		year: 'numeric',
	}).format(date);
}

export function getObjectEntryAPIDateFormat(date: Date) {
	return new Intl.DateTimeFormat('en-CA', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
	})
		.format(date)
		.replace(/\//g, '-');
}

export function getObjectEntryUIDateFormat(date: Date) {
	return new Intl.DateTimeFormat('en-US', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
	}).format(date);
}

export function getObjectEntryUIDateTimeFormat(date: Date) {
	return date
		.toLocaleString('en-US', {
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
			month: '2-digit',
			year: 'numeric',
		})
		.replace(',', '');
}

export function getPageEditorDateFormat(date: Date) {
	const formatDate = new Intl.DateTimeFormat('en-US', {
		day: '2-digit',
		month: 'numeric',
		year: '2-digit',
	}).format(date);

	return `${formatDate} 12:00 AM`;
}
