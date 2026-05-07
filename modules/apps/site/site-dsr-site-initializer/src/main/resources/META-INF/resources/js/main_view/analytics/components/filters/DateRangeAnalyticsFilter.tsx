/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {ClayInput, ClaySelect} from '@clayui/form';
import ClayForm from '@clayui/form/src/Form';
import React, {ChangeEvent, useCallback} from 'react';

import {
	AnalyticsFilters,
	DateRangePreset,
	IAnalyticsDateRangeFilter,
} from '../../types';
import {DATE_RANGE_PRESETS} from '../../utils';

interface IProps {
	filter: IAnalyticsDateRangeFilter;
	setValue: any;
}

export default function DateRangeAnalyticsFilter({
	filter: dateRangeFilter,
	setValue: setDateRange,
}: IProps) {
	const changePreset = useCallback(
		(event: ChangeEvent<HTMLSelectElement>) => {
			event.preventDefault();

			const preset = event.target.value as DateRangePreset;

			setDateRange({
				[AnalyticsFilters.DATE_RANGE]: {
					...dateRangeFilter,
					value: {
						preset,
						...DATE_RANGE_PRESETS[preset],
					},
				},
			});
		},
		[dateRangeFilter, setDateRange]
	);

	const changeDateRange = useCallback(
		(value: string) => {
			const [from, to] = value.split(' - ');

			setDateRange({
				[AnalyticsFilters.DATE_RANGE]: {
					...dateRangeFilter,
					value: {
						from,
						preset: DateRangePreset.CUSTOM_RANGE,
						to,
					},
				},
			});
		},
		[dateRangeFilter, setDateRange]
	);

	return (
		<ClayForm.Group>
			<ClayInput.Group>
				<ClayInput.GroupItem prepend shrink>
					<ClaySelect
						className="bg-white dsr-date-range-preset-select"
						onChange={changePreset}
						value={dateRangeFilter?.value?.preset}
					>
						{Object.keys(DATE_RANGE_PRESETS).map((key: string) => (
							<ClaySelect.Option
								key={key}
								label={Liferay.Language.get(key)}
								value={key}
							/>
						))}
					</ClaySelect>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem append>
					<ClayDatePicker
						className="bg-white dsr-date-range-picker"
						id="dsrEngagementRange"
						onChange={changeDateRange}
						placeholder="YYYY-MM-DD - YYYY-MM-DD"
						range
						time={false}
						value={
							dateRangeFilter?.value?.from &&
							dateRangeFilter?.value?.to
								? `${dateRangeFilter.value.from} - ${dateRangeFilter.value.to}`
								: ''
						}
						years={{
							end: new Date().getFullYear(),
							start: new Date(
								'1969-12-01T00:00:00.000Z'
							).getFullYear(),
						}}
					/>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</ClayForm.Group>
	);
}
