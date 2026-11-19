/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import DateFilter from '../../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter';
import {
	DateFilterValues,
	LastRange,
	Range,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter/types';
import {ExportImportProcess} from '../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportProcess';

const DAY = 24 * 60 * 60 * 1000;

const FUTURE_DATE = new Date(Date.now() + DAY);

const PAST_DATE_STRING = new Date(Date.now() - DAY).toISOString().slice(0, 10);

function toDisplayDateString(dateString: string) {
	const [year, month, day] = dateString.split('-');

	return `${month}/${day}/${year}`;
}

function ControlledDateFilter({
	appliedValue: initialAppliedValue,
	onApplyFilter,
	process,
	timeZoneId = 'UTC',
}: {
	appliedValue?: DateFilterValues;
	onApplyFilter: (dateFilterValues: DateFilterValues) => void;
	process?: ExportImportProcess;
	timeZoneId?: string;
}) {
	const [appliedValue, setAppliedValue] = useState<DateFilterValues>(
		initialAppliedValue ?? {range: Range.All}
	);

	return (
		<DateFilter
			appliedValue={appliedValue}
			onApplyFilter={(dateFilterValues) => {
				setAppliedValue(dateFilterValues);
				onApplyFilter(dateFilterValues);
			}}
			process={process}
			timeZoneId={timeZoneId}
		/>
	);
}

describe('DateFilter', () => {
	afterEach(() => {
		jest.useRealTimers();
	});

	const renderDateFilter = ({
		appliedValue,
		onApplyFilter = jest.fn(),
		process,
		timeZoneId,
	}: {
		appliedValue?: DateFilterValues;
		onApplyFilter?: jest.Mock;
		process?: ExportImportProcess;
		timeZoneId?: string;
	} = {}) => {
		const user = userEvent.setup({delay: null});

		render(
			<ControlledDateFilter
				appliedValue={appliedValue}
				onApplyFilter={onApplyFilter}
				process={process}
				timeZoneId={timeZoneId}
			/>
		);

		return {onApplyFilter, user};
	};

	const pickCalendarDay = async (
		user: ReturnType<typeof userEvent.setup>,
		label: string,
		date: Date
	) => {
		const chooseDateButton = within(
			screen.getByLabelText(label).closest('.date-picker') as HTMLElement
		).getByRole('button', {name: 'Choose date'});

		await user.click(chooseDateButton);

		const calendar = document.getElementById(
			chooseDateButton.getAttribute('aria-controls') as string
		) as HTMLElement;

		if (!within(calendar).queryByLabelText(date.toDateString())) {
			await user.click(
				within(calendar).getByRole('button', {
					name:
						date.getTime() < Date.now()
							? 'Select the previous month'
							: 'Select the next month',
				})
			);
		}

		await user.click(within(calendar).getByLabelText(date.toDateString()));

		await user.click(document.body);
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

	it('shows the portal locale clock in the date range placeholders', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		expect(screen.getByLabelText('from')).toHaveAttribute(
			'placeholder',
			'MM/DD/YYYY HH:MM AM'
		);
		expect(screen.getByLabelText('to[date-time]')).toHaveAttribute(
			'placeholder',
			'MM/DD/YYYY HH:MM AM'
		);
	});

	it('fills the start and end of the day when the date range bounds are picked without a time', async () => {
		const {onApplyFilter, user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		fireEvent.change(screen.getByLabelText('from'), {
			target: {
				value: `${toDisplayDateString(PAST_DATE_STRING)} --:-- --`,
			},
		});

		expect(screen.getByLabelText('from')).toHaveValue(
			`${toDisplayDateString(PAST_DATE_STRING)} 12:00 AM`
		);

		fireEvent.change(screen.getByLabelText('to[date-time]'), {
			target: {
				value: `${toDisplayDateString(PAST_DATE_STRING)} --:-- --`,
			},
		});

		expect(screen.getByLabelText('to[date-time]')).toHaveValue(
			`${toDisplayDateString(PAST_DATE_STRING)} 11:59 PM`
		);

		await user.click(screen.getByText('show-results'));

		expect(onApplyFilter).toHaveBeenCalledWith({
			endDate: `${PAST_DATE_STRING} 23:59`,
			range: Range.DateRange,
			startDate: `${PAST_DATE_STRING} 00:00`,
		});
	});

	it('flags both bounds as soon as the range is complete and inverted', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		await user.click(screen.getByLabelText('from'));

		await user.paste('01/02/2026 08:00 AM');
		await user.click(screen.getByLabelText('to[date-time]'));

		await user.paste('01/01/2026 08:00 AM');

		expect(screen.getAllByText('date-range-is-invalid')).toHaveLength(2);
		expect(screen.getByText('show-results')).toBeDisabled();
	});

	it('flags both bounds as soon as the range is complete and equal', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		await user.click(screen.getByLabelText('from'));

		await user.paste('01/01/2026 08:00 AM');
		await user.click(screen.getByLabelText('to[date-time]'));

		await user.paste('01/01/2026 08:00 AM');

		expect(screen.getAllByText('date-range-is-invalid')).toHaveLength(2);
		expect(screen.getByText('show-results')).toBeDisabled();
	});

	it('flags a future day picked from the calendar', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		await pickCalendarDay(user, 'from', FUTURE_DATE);

		expect(
			screen.getByText('dates-must-not-be-in-the-future')
		).toBeInTheDocument();
		expect(screen.getByText('show-results')).toBeDisabled();
	});

	it('fills the current time when today is picked as the To bound', async () => {
		jest.useFakeTimers().setSystemTime(Date.UTC(2026, 0, 15, 10, 30));

		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		fireEvent.change(screen.getByLabelText('to[date-time]'), {
			target: {value: '01/15/2026 --:-- --'},
		});

		expect(screen.getByLabelText('to[date-time]')).toHaveValue(
			'01/15/2026 10:30 AM'
		);
		expect(
			screen.queryByText('dates-must-not-be-in-the-future')
		).not.toBeInTheDocument();
		expect(screen.getByText('show-results')).toBeEnabled();
	});

	it('fills the current time of the given time zone when its today is picked as the To bound', async () => {
		jest.useFakeTimers().setSystemTime(Date.UTC(2026, 0, 15, 10, 30));

		const {user} = renderDateFilter({timeZoneId: 'Pacific/Kiritimati'});

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		fireEvent.change(screen.getByLabelText('to[date-time]'), {
			target: {value: '01/16/2026 --:-- --'},
		});

		expect(screen.getByLabelText('to[date-time]')).toHaveValue(
			'01/16/2026 12:30 AM'
		);
		expect(
			screen.queryByText('dates-must-not-be-in-the-future')
		).not.toBeInTheDocument();
		expect(screen.getByText('show-results')).toBeEnabled();
	});

	it('explains why an incomplete bound blocks the results once it is left', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		await user.click(screen.getByLabelText('from'));

		await user.paste('01/01/2026 1');

		expect(
			screen.queryByText('please-enter-a-valid-date')
		).not.toBeInTheDocument();
		expect(screen.getByText('show-results')).toBeDisabled();

		await user.tab();

		expect(
			screen.getByText('please-enter-a-valid-date')
		).toBeInTheDocument();
	});

	it('reports an unfinished year once the bound is left', async () => {
		const {user} = renderDateFilter();

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.DateRange
		);

		await user.click(screen.getByLabelText('from'));

		await user.paste('01/01/202');

		await user.tab();

		expect(
			screen.getByText('please-enter-a-valid-date')
		).toBeInTheDocument();
		expect(screen.getByText('show-results')).toBeDisabled();
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

		await user.click(screen.getByLabelText('from'));

		await user.paste('01/01/2026 08:00 AM');
		await user.click(screen.getByLabelText('to[date-time]'));

		await user.paste('01/02/2026 08:00 AM');

		await user.click(screen.getByText('show-results'));

		expect(onApplyFilter).toHaveBeenCalledWith({
			endDate: '2026-01-02 08:00',
			range: Range.DateRange,
			startDate: '2026-01-01 08:00',
		});

		expect(screen.getByLabelText('from')).toHaveValue(
			'01/01/2026 08:00 AM'
		);
		expect(screen.getByLabelText('from')).toBeEnabled();
		expect(screen.getByLabelText('to[date-time]')).toHaveValue(
			'01/02/2026 08:00 AM'
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

	it('does not offer the from last publish date range outside a publication', () => {
		renderDateFilter();

		expect(
			screen.queryByRole('option', {name: 'from-last-publish-date'})
		).not.toBeInTheDocument();
	});

	it('applies the from last publish date range', async () => {
		const {onApplyFilter, user} = renderDateFilter({
			process: 'publish',
		});

		await user.selectOptions(
			screen.getByLabelText('filter-content-by'),
			Range.FromLastPublishDate
		);

		await user.click(screen.getByText('show-results'));

		expect(onApplyFilter).toHaveBeenCalledWith({
			range: Range.FromLastPublishDate,
		});
	});
});
