/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function formatDateObject(dateObject) {
	return `${dateObject.year}-${('0' + dateObject.month).slice(-2)}-${(
		'0' + dateObject.day
	).slice(-2)}`;
}

export function getDateFromDateString(dateString) {
	const [year, month, day] = dateString.split('-');

	return {
		day: Number(day),
		month: Number(month),
		year: Number(year),
	};
}

export function prettifyDateObject(dateObject) {
	const date = new Date(
		dateObject.year,
		dateObject.month - 1,
		dateObject.day
	);

	return date.toLocaleDateString();
}

export function formatDateRangeObject(dateRangeObject) {
	if (dateRangeObject.from && dateRangeObject.to) {
		return `${prettifyDateObject(
			dateRangeObject.from
		)} - ${prettifyDateObject(dateRangeObject.to)}`;
	}
	if (dateRangeObject.from) {
		return `${Liferay.Language.get('from')} ${prettifyDateObject(
			dateRangeObject.from
		)}`;
	}
	if (dateRangeObject.to) {
		return `${Liferay.Language.get('to[date-time]')} ${prettifyDateObject(
			dateRangeObject.to
		)}`;
	}
}
