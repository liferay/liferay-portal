/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../liferay/liferay';

const dateOptions: Intl.DateTimeFormatOptions = {
	day: 'numeric',
	month: 'short',
	year: 'numeric',
};

function normalize(date: Date | string) {
	return typeof date === 'string' ? new Date(date) : date;
}

export function formatDate(date: Date | string, fallback = 'N/A') {
	try {
		return new Intl.DateTimeFormat(
			Liferay.ThemeDisplay.getBCP47LanguageId(),
			dateOptions
		).format(normalize(date));
	}
	catch {
		return fallback;
	}
}

export function formatDateTime(date: Date | string, fallback = 'N/A') {
	try {
		return new Intl.DateTimeFormat(
			Liferay.ThemeDisplay.getBCP47LanguageId(),
			{...dateOptions, hour: 'numeric', minute: 'numeric'}
		).format(normalize(date));
	}
	catch {
		return fallback;
	}
}

export function getLastDayOfMonth(month: number, year: number) {
	return new Date(year, month + 1, 0).getDate();
}
