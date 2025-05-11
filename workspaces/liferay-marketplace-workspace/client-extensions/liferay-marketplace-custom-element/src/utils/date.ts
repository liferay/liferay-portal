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

export function formatDate(date: string) {
	return new Intl.DateTimeFormat(
		Liferay.ThemeDisplay.getBCP47LanguageId(),
		dateOptions
	).format(new Date(date));
}

export function getLastDayOfMonth(month: number, year: number) {
	return new Date(year, month + 1, 0).getDate();
}
