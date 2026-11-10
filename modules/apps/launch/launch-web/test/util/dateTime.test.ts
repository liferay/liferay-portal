/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getDateTimeConfig,
	getLocalTime,
	toTimestamp,
} from '../../src/main/resources/META-INF/resources/js/util/dateTime';

describe('dateTime', () => {
	describe('getDateTimeConfig', () => {
		afterEach(() => {
			jest.restoreAllMocks();
		});

		it.each([
			['en-US', 'MM/dd/yyyy', 'MM/DD/YYYY HH:MM AM', true],
			['hu-HU', 'yyyy. MM. dd.', 'YYYY. MM. DD. HH:MM', false],
		])(
			'returns the date format for %s',
			(locale, dateFormat, placeholder, use12Hours) => {
				expect(getDateTimeConfig(locale, 'UTC')).toMatchObject({
					dateFormat,
					placeholder,
					use12Hours,
				});
			}
		);

		it('sets the current time in the time zone as the minimum', () => {
			jest.spyOn(Date, 'now').mockReturnValue(
				Date.UTC(2026, 9, 31, 22, 5)
			);

			expect(getDateTimeConfig('es-ES', 'Asia/Tokyo').min).toBe(
				'01/11/2026 07:05'
			);
		});
	});

	describe('getLocalTime', () => {
		it('returns null when both time zones have the same offset', () => {
			expect(
				getLocalTime({
					locale: 'en-US',
					selectedTimeZoneId: 'Europe/Paris',
					timeZoneId: 'Europe/Madrid',
					timestamp: Date.UTC(2030, 9, 31, 13),
				})
			).toBeNull();
		});

		it('adds the date when the day changes', () => {
			expect(
				getLocalTime({
					locale: 'en-US',
					selectedTimeZoneId: 'America/New_York',
					timeZoneId: 'Europe/Madrid',
					timestamp: Date.UTC(2030, 9, 31, 23),
				})
			).toMatch(/^Nov 1, 2030, 12:00\sAM \(UTC \+01:00\)$/);
		});
	});

	describe('toTimestamp', () => {
		it.each([
			[
				'10/31/2026 01:30 PM',
				'MM/dd/yyyy',
				Date.UTC(2026, 9, 31, 13, 30),
			],
			['31/10/2026 21:15', 'dd/MM/yyyy', Date.UTC(2026, 9, 31, 21, 15)],
			[
				'2026. 10. 31. 09:05',
				'yyyy. MM. dd.',
				Date.UTC(2026, 9, 31, 9, 5),
			],
		])('parses %s in %s', (dateTime, dateFormat, expected) => {
			expect(toTimestamp(dateTime, dateFormat, 'UTC')).toBe(expected);
		});

		it.each([
			'10/31/2026',
			'02/30/2026 10:00',
			'10/31/2026 13:00 PM',
			'10/31/12026 09:00',
		])('returns null for "%s"', (dateTime) => {
			expect(toTimestamp(dateTime, 'MM/dd/yyyy', 'UTC')).toBeNull();
		});

		it('converts a wall clock time to an instant', () => {
			expect(
				toTimestamp('10/31/2026 09:00', 'MM/dd/yyyy', 'Asia/Katmandu')
			).toBe(Date.UTC(2026, 9, 31, 3, 15));
		});

		it('moves a time skipped by daylight saving time forward', () => {
			expect(
				toTimestamp(
					'03/08/2026 02:30',
					'MM/dd/yyyy',
					'America/New_York'
				)
			).toBe(Date.UTC(2026, 2, 8, 7, 30));
		});

		it('picks the first occurrence of a time repeated by daylight saving time', () => {
			expect(
				toTimestamp(
					'11/01/2026 01:30',
					'MM/dd/yyyy',
					'America/New_York'
				)
			).toBe(Date.UTC(2026, 10, 1, 5, 30));
		});
	});
});
