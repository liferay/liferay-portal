/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import {
	dateTimeRangeFilterImplementation,
	formatDatePartsForClay,
	getDateConfig,
	isWithinBounds,
	parseClayValue,
} from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/implementation/DateTimeRangeFilter';
import {EEntityFieldType} from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/utils/types';

const {
	Component: DateTimeRangeFilter,
	getOdataString,
	getSelectedItemsLabel,
} = dateTimeRangeFilterImplementation;

const fromDateTime = {day: 11, hour: 15, minute: 30, month: 5, year: 2026};
const toDateTime = {day: 11, hour: 17, minute: 45, month: 5, year: 2026};

function setTimeZone(timeZone: string) {
	globalThis.Liferay.ThemeDisplay.getTimeZone = () => timeZone;
}

function setLocale(locale: string) {
	globalThis.Liferay.ThemeDisplay.getBCP47LanguageId = () => locale;
}

beforeEach(() => {
	setLocale('en-US');
});

describe('DateTimeRangeFilter.getOdataString', () => {
	beforeEach(() => {
		setTimeZone('UTC');
	});

	it('returns an empty string if no dates are selected', () => {
		const result = getOdataString({
			id: 'testField',
			selectedData: {},
		} as any);

		expect(result).toBe('');
	});

	it('generates a "ge" filter in UTC when only "from" is provided', () => {
		const result = getOdataString({
			id: 'testField',
			selectedData: {from: fromDateTime},
		} as any);

		expect(result).toBe('testField ge 2026-05-11T15:30:00Z');
	});

	it('generates an "le" filter in UTC when only "to" is provided', () => {
		const result = getOdataString({
			id: 'testField',
			selectedData: {to: toDateTime},
		} as any);

		expect(result).toBe('testField le 2026-05-11T17:45:00Z');
	});

	it('generates a "ge" and "le" filter when both ends are provided', () => {
		const result = getOdataString({
			id: 'testField',
			selectedData: {from: fromDateTime, to: toDateTime},
		} as any);

		expect(result).toBe(
			'testField ge 2026-05-11T15:30:00Z) and (testField le 2026-05-11T17:45:00Z'
		);
	});

	describe('converts wall-clock to UTC using the user configured timezone (not the browser TZ)', () => {
		it('converts wall-clock to UTC for America/Los_Angeles (PDT, -07:00)', () => {
			setTimeZone('America/Los_Angeles');

			const result = getOdataString({
				id: 'testField',
				selectedData: {from: fromDateTime},
			} as any);

			expect(result).toBe('testField ge 2026-05-11T22:30:00Z');
		});

		it('converts wall-clock to UTC for Asia/Tokyo (+09:00)', () => {
			setTimeZone('Asia/Tokyo');

			const result = getOdataString({
				id: 'testField',
				selectedData: {from: fromDateTime},
			} as any);

			expect(result).toBe('testField ge 2026-05-11T06:30:00Z');
		});

		it('honors DST for Europe/London in summer (BST, +01:00)', () => {
			setTimeZone('Europe/London');

			const result = getOdataString({
				id: 'testField',
				selectedData: {from: fromDateTime},
			} as any);

			expect(result).toBe('testField ge 2026-05-11T14:30:00Z');
		});

		it('falls back to UTC for an invalid timezone', () => {
			setTimeZone('Not/A_Real_Zone');

			const result = getOdataString({
				id: 'testField',
				selectedData: {from: fromDateTime},
			} as any);

			expect(result).toBe('testField ge 2026-05-11T15:30:00Z');
		});
	});

	describe('uses the offset stored in selectedData (URL portability)', () => {
		it('uses the stored offset even when the viewer is in a different timezone', () => {
			setTimeZone('Asia/Tokyo');

			const result = getOdataString({
				id: 'testField',
				selectedData: {
					from: {...fromDateTime, offset: '+02:00'},
				},
			} as any);

			expect(result).toBe('testField ge 2026-05-11T13:30:00Z');
		});

		it('preserves a negative offset when the viewer is in UTC', () => {
			setTimeZone('UTC');

			const result = getOdataString({
				id: 'testField',
				selectedData: {
					from: {...fromDateTime, offset: '-07:00'},
					to: {...toDateTime, offset: '-07:00'},
				},
			} as any);

			expect(result).toBe(
				'testField ge 2026-05-11T22:30:00Z) and (testField le 2026-05-12T00:45:00Z'
			);
		});
	});
});

describe('DateTimeRangeFilter.isWithinBounds', () => {
	const noBound = {day: 0, hour: 0, minute: 0, month: 0, year: 0};

	const minBound = {day: 1, hour: 0, minute: 0, month: 1, year: 2026};
	const maxBound = {day: 31, hour: 23, minute: 59, month: 12, year: 2026};

	const beforeMin = {day: 31, hour: 23, minute: 59, month: 12, year: 2025};
	const insideBounds = {day: 15, hour: 12, minute: 0, month: 6, year: 2026};
	const afterMax = {day: 1, hour: 0, minute: 0, month: 1, year: 2027};

	describe('without an upper limit', () => {
		it('accepts a date after the lower bound', () => {
			expect(isWithinBounds(afterMax, minBound, noBound)).toBe(true);
		});

		it('rejects a date before the lower bound', () => {
			expect(isWithinBounds(beforeMin, minBound, noBound)).toBe(false);
		});
	});

	describe('without a lower limit', () => {
		it('accepts a date before the upper bound', () => {
			expect(isWithinBounds(beforeMin, noBound, maxBound)).toBe(true);
		});

		it('rejects a date after the upper bound', () => {
			expect(isWithinBounds(afterMax, noBound, maxBound)).toBe(false);
		});
	});

	describe('with both limits', () => {
		it('accepts a date inside the range', () => {
			expect(isWithinBounds(insideBounds, minBound, maxBound)).toBe(true);
		});

		it('rejects a date before the lower bound', () => {
			expect(isWithinBounds(beforeMin, minBound, maxBound)).toBe(false);
		});

		it('rejects a date after the upper bound', () => {
			expect(isWithinBounds(afterMax, minBound, maxBound)).toBe(false);
		});
	});
});

describe('DateTimeRangeFilter.getDateConfig', () => {
	it('returns en-US date-only config (MM/dd/yyyy)', () => {
		const config = getDateConfig(false, 'en-US');

		expect(config.clayFormat).toBe('MM/dd/yyyy');
		expect(config.dateFormat).toBe('MM/dd/yyyy');
		expect(config.placeholder).toBe('mm/dd/yyyy');
		expect(config.use12Hours).toBe(false);
	});

	it('returns en-US dateTime config with 12-hour clock', () => {
		const config = getDateConfig(true, 'en-US');

		expect(config.clayFormat).toBe('MM/dd/yyyy hh:mm aa');
		expect(config.dateFormat).toBe('MM/dd/yyyy');
		expect(config.placeholder).toBe('mm/dd/yyyy hh:mm aa');
		expect(config.use12Hours).toBe(true);
	});

	it('returns es-ES date-only config (dd/MM/yyyy)', () => {
		const config = getDateConfig(false, 'es-ES');

		expect(config.clayFormat).toBe('dd/MM/yyyy');
		expect(config.use12Hours).toBe(false);
	});

	it('returns es-ES dateTime config with 24-hour clock', () => {
		const config = getDateConfig(true, 'es-ES');

		expect(config.clayFormat).toBe('dd/MM/yyyy HH:mm');
		expect(config.use12Hours).toBe(false);
	});

	it('returns de-DE date-only config (dd.MM.yyyy)', () => {
		const config = getDateConfig(false, 'de-DE');

		expect(config.clayFormat).toBe('dd.MM.yyyy');
		expect(config.placeholder).toBe('dd.mm.yyyy');
	});

	it('returns de-DE dateTime config with 24-hour clock', () => {
		const config = getDateConfig(true, 'de-DE');

		expect(config.clayFormat).toBe('dd.MM.yyyy HH:mm');
		expect(config.use12Hours).toBe(false);
	});

	it('falls back to Liferay.ThemeDisplay.getBCP47LanguageId() when no locale is passed', () => {
		setLocale('es-ES');

		const config = getDateConfig(true);

		expect(config.clayFormat).toBe('dd/MM/yyyy HH:mm');
		expect(config.use12Hours).toBe(false);
	});
});

describe('DateTimeRangeFilter round-trip', () => {
	const dateTimeParts = {
		day: 11,
		hour: 9,
		minute: 30,
		month: 5,
		year: 2026,
	};

	const dateOnlyParts = {
		day: 11,
		hour: 0,
		minute: 0,
		month: 5,
		year: 2026,
	};

	it('preserves DateParts through format → parse in en-US dateTime', () => {
		const {clayFormat} = getDateConfig(true, 'en-US');

		const formatted = formatDatePartsForClay(dateTimeParts, clayFormat);

		expect(formatted).toBe('05/11/2026 09:30 AM');
		expect(parseClayValue(formatted, clayFormat)).toEqual(dateTimeParts);
	});

	it('preserves DateParts through format → parse in es-ES dateTime', () => {
		const {clayFormat} = getDateConfig(true, 'es-ES');

		const formatted = formatDatePartsForClay(dateTimeParts, clayFormat);

		expect(formatted).toBe('11/05/2026 09:30');
		expect(parseClayValue(formatted, clayFormat)).toEqual(dateTimeParts);
	});

	it('preserves DateParts through format → parse in de-DE dateTime', () => {
		const {clayFormat} = getDateConfig(true, 'de-DE');

		const formatted = formatDatePartsForClay(dateTimeParts, clayFormat);

		expect(formatted).toBe('11.05.2026 09:30');
		expect(parseClayValue(formatted, clayFormat)).toEqual(dateTimeParts);
	});

	it('preserves DateParts through format → parse in en-US date-only', () => {
		const {clayFormat} = getDateConfig(false, 'en-US');

		const formatted = formatDatePartsForClay(dateOnlyParts, clayFormat);

		expect(formatted).toBe('05/11/2026');
		expect(parseClayValue(formatted, clayFormat)).toEqual(dateOnlyParts);
	});
});

describe('DateTimeRangeFilter.parseClayValue', () => {
	const enDateFormat = 'MM/dd/yyyy';
	const enDateTimeFormat = 'MM/dd/yyyy hh:mm aa';
	const esDateTimeFormat = 'dd/MM/yyyy HH:mm';

	describe('in date-only mode (en-US format)', () => {
		it('parses a valid date', () => {
			expect(parseClayValue('05/11/2026', enDateFormat)).toEqual({
				day: 11,
				hour: 0,
				minute: 0,
				month: 5,
				year: 2026,
			});
		});

		it('accepts February 29 on a leap year', () => {
			expect(parseClayValue('02/29/2024', enDateFormat)).toEqual({
				day: 29,
				hour: 0,
				minute: 0,
				month: 2,
				year: 2024,
			});
		});

		it('rejects February 29 on a non-leap year', () => {
			expect(parseClayValue('02/29/2023', enDateFormat)).toBeNull();
		});

		it('rejects February 30', () => {
			expect(parseClayValue('02/30/2026', enDateFormat)).toBeNull();
		});

		it('rejects month 13', () => {
			expect(parseClayValue('13/01/2026', enDateFormat)).toBeNull();
		});

		it('rejects day 32', () => {
			expect(parseClayValue('01/32/2026', enDateFormat)).toBeNull();
		});

		it('rejects an empty string', () => {
			expect(parseClayValue('', enDateFormat)).toBeNull();
		});

		it('rejects a value in a different locale format', () => {
			expect(parseClayValue('11/05/2026', enDateFormat)).toEqual({
				day: 5,
				hour: 0,
				minute: 0,
				month: 11,
				year: 2026,
			});

			expect(parseClayValue('2026-05-11', enDateFormat)).toBeNull();
		});
	});

	describe('in 24-hour dateTime mode (es-ES format)', () => {
		it('parses a valid datetime', () => {
			expect(
				parseClayValue('11/05/2026 15:30', esDateTimeFormat)
			).toEqual({
				day: 11,
				hour: 15,
				minute: 30,
				month: 5,
				year: 2026,
			});
		});

		it('rejects hour 24', () => {
			expect(
				parseClayValue('11/05/2026 24:00', esDateTimeFormat)
			).toBeNull();
		});

		it('rejects minute 60', () => {
			expect(
				parseClayValue('11/05/2026 23:60', esDateTimeFormat)
			).toBeNull();
		});

		it('rejects month 13', () => {
			expect(
				parseClayValue('01/13/2026 12:00', esDateTimeFormat)
			).toBeNull();
		});
	});

	describe('in 12-hour dateTime mode (en-US format)', () => {
		it('parses a valid AM datetime', () => {
			expect(
				parseClayValue('05/11/2026 09:30 AM', enDateTimeFormat)
			).toEqual({
				day: 11,
				hour: 9,
				minute: 30,
				month: 5,
				year: 2026,
			});
		});

		it('parses 12:00 PM as noon', () => {
			expect(
				parseClayValue('05/11/2026 12:00 PM', enDateTimeFormat)
			).toEqual({
				day: 11,
				hour: 12,
				minute: 0,
				month: 5,
				year: 2026,
			});
		});

		it('parses 12:00 AM as midnight', () => {
			expect(
				parseClayValue('05/11/2026 12:00 AM', enDateTimeFormat)
			).toEqual({
				day: 11,
				hour: 0,
				minute: 0,
				month: 5,
				year: 2026,
			});
		});

		it('rejects a non-padded hour against the padded hh format', () => {
			expect(
				parseClayValue('05/11/2026 9:30 AM', enDateTimeFormat)
			).toBeNull();
		});

		it('rejects minute 60', () => {
			expect(
				parseClayValue('05/11/2026 11:60 AM', enDateTimeFormat)
			).toBeNull();
		});

		it('rejects February 30', () => {
			expect(
				parseClayValue('02/30/2026 11:00 AM', enDateTimeFormat)
			).toBeNull();
		});
	});
});

describe('DateTimeRangeFilter.getSelectedItemsLabel', () => {
	it('formats a from-and-to range in the user locale (en-US)', () => {
		const result = getSelectedItemsLabel({
			selectedData: {from: fromDateTime, to: toDateTime},
		} as any);

		expect(result).toBe('05/11/2026 03:30 PM - 05/11/2026 05:45 PM');
	});

	it('formats a from-and-to range in es-ES', () => {
		setLocale('es-ES');

		const result = getSelectedItemsLabel({
			selectedData: {from: fromDateTime, to: toDateTime},
		} as any);

		expect(result).toBe('11/05/2026 15:30 - 11/05/2026 17:45');
	});

	it('returns an empty string when nothing is selected', () => {
		const result = getSelectedItemsLabel({
			selectedData: {},
		} as any);

		expect(result).toBe('');
	});

	it('displays stored parts converted to the viewer time zone', () => {
		setTimeZone('America/Los_Angeles');

		const madridFromDateTime = {
			day: 11,
			hour: 17,
			minute: 0,
			month: 5,
			offset: '+02:00',
			year: 2026,
		};
		const madridToDateTime = {
			day: 11,
			hour: 19,
			minute: 25,
			month: 5,
			offset: '+02:00',
			year: 2026,
		};

		const result = getSelectedItemsLabel({
			selectedData: {from: madridFromDateTime, to: madridToDateTime},
		} as any);

		expect(result).toBe('05/11/2026 08:00 AM - 05/11/2026 10:25 AM');
	});
});

describe('DateTimeRangeFilter submit validation', () => {
	const FROM = '06/15/2020 02:30 PM';
	const TO = '11/22/2020 09:15 AM';

	function renderFilter(props: Record<string, unknown> = {}) {
		render(
			<DateTimeRangeFilter
				{...({
					active: true,
					entityFieldType: EEntityFieldType.DATE_TIME,
					id: 'testField',
					selectedData: null,
					setFilter: jest.fn(),
					...props,
				} as any)}
			/>
		);

		return {
			fromInput: screen.getByLabelText('from'),
			submitButton: screen.getByRole('button', {name: 'add-filter'}),
			toInput: screen.getByLabelText('to[date-time]'),
		};
	}

	beforeEach(() => {
		setTimeZone('UTC');
	});

	it('reports an invalid date when a value does not match the locale format', async () => {
		const {submitButton, toInput} = renderFilter();

		await userEvent.type(toInput, '2020-11-22');

		expect(screen.getByText('date-is-invalid')).toBeInTheDocument();

		expect(submitButton).toBeDisabled();
	});

	it('reports an invalid range, not an invalid date, when "to" is before "from"', async () => {
		const {fromInput, submitButton, toInput} = renderFilter();

		await userEvent.type(fromInput, TO);
		await userEvent.type(toInput, FROM);

		expect(
			screen.getByText('date-range-is-invalid.-from-must-be-before-to')
		).toBeInTheDocument();

		expect(screen.queryByText('date-is-invalid')).not.toBeInTheDocument();

		expect(submitButton).toBeDisabled();
	});

	it('enables the submit button when "from" is before "to"', async () => {
		const {fromInput, submitButton, toInput} = renderFilter();

		await userEvent.type(fromInput, FROM);
		await userEvent.type(toInput, TO);

		expect(screen.queryByRole('alert')).not.toBeInTheDocument();

		expect(submitButton).toBeEnabled();
	});

	it('enables the submit button when both ends are the same instant', async () => {
		const {fromInput, submitButton, toInput} = renderFilter();

		await userEvent.type(fromInput, FROM);
		await userEvent.type(toInput, FROM);

		expect(submitButton).toBeEnabled();
	});

	it('reports an out-of-range date when a value falls outside the bounds', async () => {
		const {submitButton, toInput} = renderFilter({
			max: {day: 31, hour: 23, minute: 59, month: 12, year: 2020},
		});

		await userEvent.type(toInput, '01/02/2021 09:15 AM');

		expect(screen.getByText('date-is-out-of-range')).toBeInTheDocument();

		expect(submitButton).toBeDisabled();
	});
});
