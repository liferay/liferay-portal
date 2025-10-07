/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	IRangeSelectorsDropdown,
	RangeSelectors,
	RangeSelectorsDropdown,
} from '@liferay/analytics-reports-js-components-web';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

jest.mock('frontend-js-web', () => ({
	dateUtils: {
		getFirstDayOfWeek: jest.fn(() => 0),
		getWeekdaysShort: jest.fn(() => [
			'Sun',
			'Mon',
			'Tue',
			'Wed',
			'Thu',
			'Fri',
			'Sat',
		]),
	},
}));

describe('[CMS Dashboard] Components: RangeSelectorsDropdown', () => {
	const mockedOnChange = jest.fn();
	const mockedProps: IRangeSelectorsDropdown = {
		activeRangeSelector: {
			rangeEnd: '',
			rangeKey: RangeSelectors.Last7Days,
			rangeStart: '',
		},
		availableRangeKeys: [
			RangeSelectors.Last24Hours,
			RangeSelectors.Last7Days,
			RangeSelectors.Last28Days,
			RangeSelectors.Last30Days,
			RangeSelectors.Last90Days,
			RangeSelectors.CustomRange,
		],
		onChange: mockedOnChange,
	};

	it('renders correctly with different date range options', () => {
		render(<RangeSelectorsDropdown {...mockedProps} />);

		const RangeSelectorDropdown = screen.getByRole('button');
		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');

		fireEvent.click(RangeSelectorDropdown);

		const Last24HoursOption = screen.getByRole('menuitem', {
			name: /(last-24-hours) (\w{3} \d{2}, \d{2} [AP]\.M\. - \w{3} \d{2}, \d{2} [AP]\.M\.)/,
		});
		expect(Last24HoursOption).toBeInTheDocument();

		const Last7DaysOption = screen.getByRole('menuitem', {
			name: /(last-7-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last7DaysOption).toBeInTheDocument();

		const Last28DaysOption = screen.getByRole('menuitem', {
			name: /(last-28-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last28DaysOption).toBeInTheDocument();

		const Last30DaysOption = screen.getByRole('menuitem', {
			name: /(last-30-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last30DaysOption).toBeInTheDocument();

		const Last90DaysOption = screen.getByRole('menuitem', {
			name: /(last-90-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last90DaysOption).toBeInTheDocument();
	});

	it('calls "onChange" function correctly after click on date range option', () => {
		render(<RangeSelectorsDropdown {...mockedProps} />);

		const RangeSelectorDropdown = screen.getByRole('button');
		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');

		fireEvent.click(RangeSelectorDropdown);

		expect(mockedOnChange).not.toHaveBeenCalled();

		const Last28DaysOption = screen.getByRole('menuitem', {
			name: /(last-28-days)/,
		});
		expect(Last28DaysOption).toBeInTheDocument();

		fireEvent.click(Last28DaysOption);

		expect(mockedOnChange).toHaveBeenCalledTimes(1);
	});

	it('selects a custom date range and checks if it was selected', async () => {
		const {rerender} = render(<RangeSelectorsDropdown {...mockedProps} />);

		const rangeSelectorDropdown = screen.getByRole('button');

		expect(rangeSelectorDropdown).toHaveTextContent('last-7-days');

		await userEvent.click(rangeSelectorDropdown);

		const customRangeOption = screen.getByRole('menuitem', {
			name: /(custom-range)/,
		});

		await userEvent.click(customRangeOption);

		const rangeStartInput = (
			await screen.findByTestId('range-start')
		).querySelector('input.form-control');

		const rangeEndInput = (
			await screen.findByTestId('range-end')
		).querySelector('input.form-control');

		await userEvent.type(rangeStartInput as HTMLInputElement, '2025-05-10');

		await userEvent.type(rangeEndInput as HTMLInputElement, '2025-05-20');

		await userEvent.click(screen.getByRole('button', {name: 'add-filter'}));

		const newProps = {
			...mockedProps,
			activeRangeSelector: {
				rangeEnd: '2025-05-20',
				rangeKey: RangeSelectors.CustomRange,
				rangeStart: '2025-05-10',
			},
		};

		rerender(<RangeSelectorsDropdown {...newProps} />);

		expect(rangeSelectorDropdown).toHaveTextContent(
			'2025-05-10 - 2025-05-20'
		);
	});

	it('navigates drill down to select a custom range and cancel action', async () => {
		render(<RangeSelectorsDropdown {...mockedProps} />);

		const RangeSelectorDropdown = screen.getByRole('button');

		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');

		fireEvent.click(RangeSelectorDropdown);

		const customRangeOption = screen.getByRole('menuitem', {
			name: /(custom-range)/,
		});

		fireEvent.click(customRangeOption);

		const cancelButton = screen.getByTestId('cancel-button');

		expect(cancelButton).toBeInTheDocument();

		expect(screen.getByText('create-date-range')).toBeInTheDocument();
		expect(screen.getByText('from')).toBeInTheDocument();
		expect(screen.getByText('to[date-time]')).toBeInTheDocument();

		fireEvent.click(cancelButton);

		expect(screen.queryByText('create-date-range')).not.toBeInTheDocument();
		expect(screen.queryByText('from')).not.toBeInTheDocument();
		expect(screen.queryByText('to[date-time]')).not.toBeInTheDocument();
	});

	it('renders correctly with given classname', () => {
		const customClass = 'custom-class';

		const {container} = render(
			<RangeSelectorsDropdown {...mockedProps} className={customClass} />
		);

		const Element = container.querySelector('.custom-class');
		expect(Element).toBeInTheDocument();
	});
});
