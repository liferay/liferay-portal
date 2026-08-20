/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getDefaultTimeZoneId} from '../../../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/components/scheduler/timeZones';

const TIME_ZONES = [
	{label: '(UTC) Coordinated Universal Time', value: 'UTC'},
	{label: '(UTC +01:00) Central European Time', value: 'Europe/Paris'},
	{label: '(UTC +02:00) Eastern European Time', value: 'Africa/Cairo'},
];

describe('getDefaultTimeZoneId', () => {
	it('returns the browser time zone when it is listed', () => {
		expect(getDefaultTimeZoneId(TIME_ZONES, 'UTC', 'Europe/Paris')).toBe(
			'Europe/Paris'
		);
	});

	it('matches an unlisted browser time zone by offset and region', () => {
		expect(getDefaultTimeZoneId(TIME_ZONES, 'UTC', 'Europe/Madrid')).toBe(
			'Europe/Paris'
		);
	});

	it('falls back to the user time zone without an offset match', () => {
		expect(
			getDefaultTimeZoneId(TIME_ZONES, 'UTC', 'Australia/Sydney')
		).toBe('UTC');
	});
});
