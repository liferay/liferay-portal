/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ValueInput from '../../../../src/main/resources/META-INF/resources/js/components/CollectionFilterBuilder/ValueInput';

const DATE_ERROR = 'please-enter-a-valid-date';
const INTEGER_ERROR = 'value-must-be-an-integer-value';
const RANGE_ERROR = 'please-enter-a-valid-range';
const DATE_RANGE_ERROR = 'date-range-is-invalid.-from-must-be-before-to';

function renderValueInput({
	onChange = jest.fn(),
	operator = 'between',
	type = 'integer',
	value,
}: {
	onChange?: jest.Mock;
	operator?: string;
	type?: 'date' | 'date-time' | 'decimal' | 'integer';
	value?: string | string[];
}) {
	return render(
		<ValueInput
			index={0}
			onChange={onChange}
			operator={operator}
			property={{label: 'Property', name: 'property', type}}
			value={value}
		/>
	);
}

describe('ValueInput', () => {
	describe('numeric range', () => {
		it('propagates the typed values', async () => {
			const onChange = jest.fn();

			renderValueInput({onChange, value: ['', '']});

			await userEvent.type(screen.getByLabelText('from'), '5');

			expect(onChange).toHaveBeenLastCalledWith(['5', '']);

			await userEvent.type(screen.getByLabelText('to[date-time]'), '9');

			expect(onChange).toHaveBeenLastCalledWith(['', '9']);
		});

		it('shows an error when from is greater than to', () => {
			renderValueInput({value: ['10', '2']});

			expect(screen.getByText(RANGE_ERROR)).toBeInTheDocument();
		});

		it('does not show an error for a valid range', () => {
			renderValueInput({value: ['2', '2']});

			expect(screen.queryByText(RANGE_ERROR)).not.toBeInTheDocument();
		});

		it('shows an error when only one value is filled', () => {
			renderValueInput({value: ['10', '']});

			expect(screen.getByText(RANGE_ERROR)).toBeInTheDocument();
		});

		it('does not show an error when both values are empty', () => {
			renderValueInput({value: ['', '']});

			expect(screen.queryByText(RANGE_ERROR)).not.toBeInTheDocument();
		});

		it('shows an error for a decimal in an integer range', () => {
			renderValueInput({value: ['1.5', '10']});

			expect(screen.getByText(INTEGER_ERROR)).toBeInTheDocument();
		});
	});

	describe('number', () => {
		it('shows an error for a decimal in an integer field', () => {
			renderValueInput({operator: 'eq', value: '1.5'});

			expect(screen.getByText(INTEGER_ERROR)).toBeInTheDocument();
		});

		it('does not show an error for a decimal in a decimal field', () => {
			renderValueInput({operator: 'eq', type: 'decimal', value: '1.5'});

			expect(screen.queryByText(INTEGER_ERROR)).not.toBeInTheDocument();
		});
	});

	describe('date range', () => {
		it('shows an error when from is after to', () => {
			renderValueInput({
				type: 'date-time',
				value: ['2026-10-12 10:00', '2026-10-12 09:00'],
			});

			expect(screen.getByText(DATE_RANGE_ERROR)).toBeInTheDocument();
		});

		it('shows an error when from is after to without a time', () => {
			renderValueInput({
				type: 'date-time',
				value: ['2026-10-12 --:--', '2026-10-11 --:--'],
			});

			expect(screen.getByText(DATE_RANGE_ERROR)).toBeInTheDocument();
		});

		it('does not show an error for a valid range', () => {
			renderValueInput({
				type: 'date',
				value: ['2026-10-01', '2026-10-12'],
			});

			expect(
				screen.queryByText(DATE_RANGE_ERROR)
			).not.toBeInTheDocument();
		});

		it('shows an error when only one date is filled', () => {
			renderValueInput({
				type: 'date-time',
				value: ['2026-10-12 10:00', ''],
			});

			expect(screen.getByText(RANGE_ERROR)).toBeInTheDocument();
		});

		it('shows an error for a date that does not exist', () => {
			renderValueInput({
				type: 'date',
				value: ['2026-02-30', '2026-03-12'],
			});

			expect(screen.getByText(DATE_ERROR)).toBeInTheDocument();
		});

		it('shows an error for a date in the wrong format', () => {
			renderValueInput({
				type: 'date-time',
				value: ['2026-10-12 10:00', '2'],
			});

			expect(screen.getByText(DATE_ERROR)).toBeInTheDocument();
		});
	});

	describe('date', () => {
		it('shows an error for a date that does not exist', () => {
			renderValueInput({
				operator: 'eq',
				type: 'date-time',
				value: '2026-13-01 10:00',
			});

			expect(screen.getByText(DATE_ERROR)).toBeInTheDocument();
		});

		it('does not show an error for a valid date', () => {
			renderValueInput({
				operator: 'eq',
				type: 'date-time',
				value: '2026-10-12 --:--',
			});

			expect(screen.queryByText(DATE_ERROR)).not.toBeInTheDocument();
		});

		it('does not show an error for a relative date', () => {
			renderValueInput({
				operator: 'eq',
				type: 'date',
				value: 'past-week',
			});

			expect(screen.queryByText(DATE_ERROR)).not.toBeInTheDocument();
		});
	});

	describe('accessibility', () => {
		it('announces the error', () => {
			renderValueInput({value: ['10', '2']});

			expect(screen.getByRole('alert')).toHaveTextContent(RANGE_ERROR);
		});

		it('does not announce anything for a valid range', () => {
			renderValueInput({value: ['2', '10']});

			expect(screen.queryByRole('alert')).not.toBeInTheDocument();
		});

		it('has no violations with an invalid numeric range', async () => {
			const {container} = renderValueInput({value: ['10', '2']});

			await checkAccessibility({bestPractices: true, context: container});
		});

		it('has no violations with an invalid date range', async () => {
			const {container} = renderValueInput({
				type: 'date-time',
				value: ['2026-10-12 10:00', '2026-10-11 10:00'],
			});

			await checkAccessibility({bestPractices: true, context: container});
		});

		it('has no violations with an invalid date', async () => {
			const {container} = renderValueInput({
				operator: 'eq',
				type: 'date',
				value: '2026-02-30',
			});

			await checkAccessibility({bestPractices: true, context: container});
		});
	});
});
