/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ScheduleOptions, {
	toParseableDate,
} from '../../src/main/resources/META-INF/resources/js/ScheduleOptions';

const DEFAULT_PROPS = {
	displayDate: null,
	error: '',
	formId: 'formId',
	portletNamespace: 'portletNamespace',
	setDisplayDate: jest.fn(),
	setError: jest.fn(),
	timeZone: {name: 'UTC'},
};

describe('ScheduleOptions', () => {
	it('shows an AM/PM time hint when the locale uses a 12-hour clock', () => {
		render(<ScheduleOptions {...DEFAULT_PROPS} use12Hours />);

		expect(
			screen.getByPlaceholderText('yyyy-mm-dd-hh-mm-am-pm')
		).toBeInTheDocument();
	});

	it('shows a 24-hour time hint when the locale uses a 24-hour clock', () => {
		render(<ScheduleOptions {...DEFAULT_PROPS} use12Hours={false} />);

		expect(
			screen.getByPlaceholderText('yyyy-mm-dd-hh-mm')
		).toBeInTheDocument();
	});

	it('accepts a complete AM/PM date as valid when the clock is 12-hour', () => {
		const setError = jest.fn();

		render(
			<ScheduleOptions
				{...DEFAULT_PROPS}
				displayDate="2024-01-15 02:30 PM"
				setError={setError}
				use12Hours
			/>
		);

		expect(setError).toHaveBeenCalledWith('');
		expect(setError).not.toHaveBeenCalledWith('please-enter-a-valid-date');
	});

	it('accepts a complete 24-hour date as valid when the clock is 24-hour', () => {
		const setError = jest.fn();

		render(
			<ScheduleOptions
				{...DEFAULT_PROPS}
				displayDate="2024-01-15 14:30"
				setError={setError}
				use12Hours={false}
			/>
		);

		expect(setError).toHaveBeenCalledWith('');
	});

	it('flags an incomplete date as invalid', () => {
		const setError = jest.fn();

		render(
			<ScheduleOptions
				{...DEFAULT_PROPS}
				displayDate="2024-01-15"
				setError={setError}
				use12Hours
			/>
		);

		expect(setError).toHaveBeenCalledWith('please-enter-a-valid-date');
	});
});

describe('toParseableDate', () => {
	it('converts a PM value to 24-hour form', () => {
		expect(toParseableDate('2024-01-15 02:30 PM')).toBe('2024-01-15 14:30');
	});

	it('keeps an AM value in 24-hour form', () => {
		expect(toParseableDate('2024-01-15 02:30 AM')).toBe('2024-01-15 02:30');
	});

	it('maps 12 AM to midnight', () => {
		expect(toParseableDate('2024-01-15 12:05 AM')).toBe('2024-01-15 00:05');
	});

	it('maps 12 PM to noon', () => {
		expect(toParseableDate('2024-01-15 12:00 PM')).toBe('2024-01-15 12:00');
	});

	it('converts a late PM value', () => {
		expect(toParseableDate('2024-01-15 11:45 PM')).toBe('2024-01-15 23:45');
	});

	it('passes a 24-hour value through unchanged', () => {
		expect(toParseableDate('2024-01-15 14:30')).toBe('2024-01-15 14:30');
	});

	it('passes a non-matching value through unchanged', () => {
		expect(toParseableDate('2024-01-15')).toBe('2024-01-15');
	});
});
