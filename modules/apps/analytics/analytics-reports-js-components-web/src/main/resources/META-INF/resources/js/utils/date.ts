/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {dateUtils} from 'frontend-js-web';

import {RangeSelectors} from '../components/RangeSelectorsDropdown';

export function toUnix(str: string) {
	return new Date(str).getTime();
}

export function formatDate(date: Date, rangeSelector: RangeSelectors) {
	if (rangeSelector === RangeSelectors.Last24Hours) {
		return dateUtils.format(date, 'MMM d, h a');
	}

	return dateUtils.format(date, 'YYYY MMM D');
}

export function getDateRange(rangeSelector: RangeSelectors, date = new Date()) {
	function getDate(value: number) {
		return new Date(date.setDate(date.getDate() - value));
	}

	const startDate = getDate(1);
	const endDate = getDate(Number(rangeSelector));

	return {endDate, startDate};
}
