/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import DateField, {
	getDateError,
	toServerDate,
} from '../../js/components/DateField';

const mockOnChange = jest.fn();

const renderDateField = (
	props?: Partial<React.ComponentProps<typeof DateField>>
) =>
	render(
		<DateField
			id="dueDate"
			label="due-date"
			onChange={mockOnChange}
			{...props}
		/>
	);

describe('DateField', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('points the input at the message for screen readers', () => {
		const {getByRole} = renderDateField({
			errorMessage: 'please-enter-a-valid-date',
		});

		const input = getByRole('textbox');

		expect(input).toHaveAttribute(
			'aria-describedby',
			'dueDatefieldFeedback'
		);
		expect(input).toHaveAttribute('aria-invalid', 'true');
	});

	it('renders the message the parent reports on submit', () => {
		const {getByText} = renderDateField({
			errorMessage: 'please-enter-a-valid-date',
		});

		expect(getByText('please-enter-a-valid-date')).toBeInTheDocument();
	});

	it('reports a required field left empty', () => {
		const {getByRole, getByText} = renderDateField();

		fireEvent.blur(getByRole('textbox'), {target: {value: ''}});

		expect(getByText('this-field-is-required')).toBeInTheDocument();
	});

	describe('getDateError', () => {
		it('accepts a date in the locale format', () => {
			expect(getDateError('08/20/2026', true)).toBe('');
		});

		it('accepts a date surrounded by whitespace', () => {
			expect(getDateError(' 08/20/2026 ', true)).toBe('');
		});

		it('accepts a date typed without leading zeros', () => {
			expect(getDateError('8/2/2026', true)).toBe('');
		});

		it('accepts an optional value left empty', () => {
			expect(getDateError('', false)).toBe('');
		});

		it('reports a partially typed value', () => {
			expect(getDateError('08/20', true)).toBe(
				'please-enter-a-valid-date'
			);
		});

		it('reports a required value left empty', () => {
			expect(getDateError('', true)).toBe('this-field-is-required');
		});

		it('reports a value that is not a date', () => {
			expect(getDateError('13/45/2026', true)).toBe(
				'please-enter-a-valid-date'
			);
		});

		it('reports whitespace as a missing required value', () => {
			expect(getDateError('   ', true)).toBe('this-field-is-required');
		});
	});

	describe('toServerDate', () => {
		it('converts a date typed without leading zeros', () => {
			expect(toServerDate('8/2/2026')).toBe('2026-08-02');
		});
	});
});
