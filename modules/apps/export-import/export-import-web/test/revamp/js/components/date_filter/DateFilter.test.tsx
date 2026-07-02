/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import DateFilter from '../../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter';
import {
	DateFilterValues,
	LastRange,
	Range,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter/types';

function ControlledDateFilter({
	onApplyFilter,
}: {
	onApplyFilter: (filterValues: DateFilterValues) => void;
}) {
	const [appliedValue, setAppliedValue] = useState<DateFilterValues>({
		range: Range.All,
	});

	return (
		<DateFilter
			appliedValue={appliedValue}
			onApplyFilter={(filterValues) => {
				setAppliedValue(filterValues);
				onApplyFilter(filterValues);
			}}
		/>
	);
}

describe('DateFilter', () => {
	const renderDateFilter = (onApplyFilter = jest.fn()) => {
		const user = userEvent.setup();

		render(<ControlledDateFilter onApplyFilter={onApplyFilter} />);

		return {onApplyFilter, user};
	};

	it('renders in initial state without Show Results button', () => {
		renderDateFilter();

		expect(screen.getByLabelText('filter-content-by')).toHaveValue(
			Range.All
		);
		expect(screen.queryByText('show-results')).not.toBeInTheDocument();
	});

	it('shows Modified Last options and enables the apply button when selected', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.Last
		);

		expect(screen.getByLabelText('modified-last')).toBeInTheDocument();
		expect(screen.getByText('show-results')).toBeInTheDocument();
	});

	it('shows Date Range fields when selected', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		expect(screen.getByLabelText('from')).toBeInTheDocument();
		expect(screen.getByLabelText('to[date-time]')).toBeInTheDocument();
	});

	it('calls onApplyFilter with correct values when applying a Modified Last filter', async () => {
		const {onApplyFilter, user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.Last
		);
		await user.selectOptions(
			screen.getByLabelText('modified-last'),
			LastRange.H24
		);

		await user.click(screen.getByText('show-results'));

		expect(onApplyFilter).toHaveBeenCalledWith({
			last: LastRange.H24,
			range: Range.Last,
		});

		expect(screen.getByText('show-results')).toBeDisabled();
	});

	it('resets the date range fields when filters are cleared', async () => {
		const {onApplyFilter, user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		await user.type(screen.getByLabelText('from'), '2026-01-01 08:00');
		await user.type(
			screen.getByLabelText('to[date-time]'),
			'2026-01-02 08:00'
		);

		await user.click(screen.getByText('show-results'));

		expect(onApplyFilter).toHaveBeenCalledWith({
			endDate: '2026-01-02 08:00',
			range: Range.DateRange,
			startDate: '2026-01-01 08:00',
		});

		expect(screen.getByLabelText('from')).toHaveValue('2026-01-01 08:00');
		expect(screen.getByLabelText('from')).toBeEnabled();
		expect(screen.getByLabelText('to[date-time]')).toHaveValue(
			'2026-01-02 08:00'
		);
		expect(screen.getByLabelText('to[date-time]')).toBeEnabled();

		await user.click(screen.getByText('clear-filters'));

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		expect(screen.getByLabelText('from')).toHaveValue('');
		expect(screen.getByLabelText('to[date-time]')).toHaveValue('');
	});

	it('shows an alert summary and clears filters correctly', async () => {
		const {onApplyFilter, user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.Last
		);
		await user.click(screen.getByText('show-results'));

		expect(screen.getByRole('alert')).toBeInTheDocument();

		await user.click(screen.getByText('clear-filters'));

		expect(onApplyFilter).toHaveBeenLastCalledWith({
			range: Range.All,
		});

		expect(screen.getByLabelText('filter-content-by')).toHaveValue(
			Range.All
		);
		expect(screen.queryByRole('alert')).not.toBeInTheDocument();
	});
});
