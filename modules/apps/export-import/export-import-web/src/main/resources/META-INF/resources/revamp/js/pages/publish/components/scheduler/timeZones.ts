/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TimeZoneOption} from './types';

function getTimeZoneOffset(timeZoneId: string, date: Date): number {
	const timeZoneDate = new Date(
		date.toLocaleString('en-US', {timeZone: timeZoneId})
	);
	const utcDate = new Date(date.toLocaleString('en-US', {timeZone: 'UTC'}));

	return timeZoneDate.getTime() - utcDate.getTime();
}

export function getDefaultTimeZoneId(
	timeZones: TimeZoneOption[],
	userTimeZoneId: string,
	browserTimeZoneId: string = Intl.DateTimeFormat().resolvedOptions().timeZone
): string {
	if (!browserTimeZoneId) {
		return userTimeZoneId;
	}

	if (timeZones.some(({value}) => value === browserTimeZoneId)) {
		return browserTimeZoneId;
	}

	const date = new Date();

	const browserTimeZoneOffset = getTimeZoneOffset(browserTimeZoneId, date);

	const timeZonesWithBrowserOffset = timeZones.filter(
		({value}) => getTimeZoneOffset(value, date) === browserTimeZoneOffset
	);

	const browserRegion = `${browserTimeZoneId.split('/')[0]}/`;

	const defaultTimeZone =
		timeZonesWithBrowserOffset.find(({value}) =>
			value.startsWith(browserRegion)
		) ?? timeZonesWithBrowserOffset[0];

	return defaultTimeZone?.value ?? userTimeZoneId;
}
