/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import DateRangeAnalyticsFilter from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/filters/DateRangeAnalyticsFilter';
import {
	AnalyticsFilters,
	DateRangePreset,
	IAnalyticsDateRangeFilter,
} from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/types';
import {DATE_RANGE_PRESETS} from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/utils';

const filter: IAnalyticsDateRangeFilter = {
	active: true,
	component: DateRangeAnalyticsFilter,
	value: {
		from: '2025-01-01',
		preset: DateRangePreset.LAST_WEEK,
		to: '2025-01-07',
	},
};

describe('DateRangeAnalyticsFilter', () => {
	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('exposes the preset select and the date range picker', () => {
		const {container} = render(
			<DateRangeAnalyticsFilter filter={filter} setValue={jest.fn()} />
		);

		expect(
			container.querySelector('.dsr-date-range-preset-select')
		).toBeInTheDocument();
		expect(
			container.querySelector('#dsrEngagementRange')
		).toBeInTheDocument();
	});

	it('renders one option per available preset', () => {
		const {container} = render(
			<DateRangeAnalyticsFilter filter={filter} setValue={jest.fn()} />
		);

		const options = container.querySelectorAll(
			'.dsr-date-range-preset-select option'
		);

		expect(options).toHaveLength(Object.keys(DATE_RANGE_PRESETS).length);
	});

	it('notifies the parent when a preset is selected', () => {
		const setValue = jest.fn();

		const {container} = render(
			<DateRangeAnalyticsFilter filter={filter} setValue={setValue} />
		);

		fireEvent.change(
			container.querySelector('.dsr-date-range-preset-select')!,
			{target: {value: DateRangePreset.ALL_TIME}}
		);

		expect(setValue).toHaveBeenCalledWith({
			[AnalyticsFilters.DATE_RANGE]: expect.objectContaining({
				value: expect.objectContaining({
					preset: DateRangePreset.ALL_TIME,
				}),
			}),
		});
	});

	it('notifies the parent with a custom range when the picker changes', () => {
		const setValue = jest.fn();

		const {container} = render(
			<DateRangeAnalyticsFilter filter={filter} setValue={setValue} />
		);

		fireEvent.change(container.querySelector('#dsrEngagementRange')!, {
			target: {value: '2025-10-01 - 2025-10-07'},
		});

		expect(setValue).toHaveBeenCalledWith({
			[AnalyticsFilters.DATE_RANGE]: expect.objectContaining({
				value: {
					from: '2025-10-01',
					preset: DateRangePreset.CUSTOM_RANGE,
					to: '2025-10-07',
				},
			}),
		});
	});

	it('reflects the selected custom range in the picker', () => {
		function Wrapper() {
			const [dateRangeFilter, setDateRangeFilter] =
				useState<IAnalyticsDateRangeFilter>(filter);

			return (
				<DateRangeAnalyticsFilter
					filter={dateRangeFilter}
					setValue={(payload: {
						[AnalyticsFilters.DATE_RANGE]: IAnalyticsDateRangeFilter;
					}) =>
						setDateRangeFilter(payload[AnalyticsFilters.DATE_RANGE])
					}
				/>
			);
		}

		const {container} = render(<Wrapper />);

		const dateRangePicker = container.querySelector('#dsrEngagementRange')!;

		fireEvent.change(dateRangePicker, {
			target: {value: '2025-10-01 - 2025-10-07'},
		});

		expect(dateRangePicker).toHaveValue('2025-10-01 - 2025-10-07');
	});
});
