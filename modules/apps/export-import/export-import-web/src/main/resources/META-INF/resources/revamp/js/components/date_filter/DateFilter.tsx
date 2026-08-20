/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

import FieldSelectWithOption from '../forms/FieldSelectWithOption';
import DateRangeFields from './DateRangeFields';
import LastRangeFields from './LastRangeFields';
import {
	DateFilterValues,
	EditingState,
	LastRange,
	Range,
	TouchedFields,
} from './types';
import {
	RANGE_OPTIONS,
	dateFilterToEditingState,
	editingToDateFilter,
	getAppliedFilterSummary,
	getIsDirty,
	getValidation,
} from './utils';

const INITIAL_EDITING: EditingState = {
	endDate: '',
	last: LastRange.H12,
	range: Range.All,
	startDate: '',
};

const INITIAL_TOUCHED: TouchedFields = {
	endDate: false,
	startDate: false,
};

export default function DateFilter({
	appliedValue = {range: Range.All} as DateFilterValues,
	itemsCount = 0,
	lastPublishDate,
	onApplyFilter,
}: {
	appliedValue?: DateFilterValues;
	itemsCount?: number;
	lastPublishDate?: string;
	onApplyFilter?: (dateFilterValues: DateFilterValues) => void;
}) {
	const [editing, setEditing] = useState<EditingState>(() =>
		dateFilterToEditingState(appliedValue)
	);
	const [touchedFields, setTouchedFields] =
		useState<TouchedFields>(INITIAL_TOUCHED);

	const validation = useMemo(() => getValidation(editing), [editing]);

	const isDirty = useMemo(
		() => getIsDirty(editing, appliedValue),
		[editing, appliedValue]
	);

	const appliedFilterSummary = useMemo(
		() => getAppliedFilterSummary(appliedValue),
		[appliedValue]
	);

	const updateFilter = (patch: Partial<EditingState>) => {
		setEditing((prev) => ({...prev, ...patch}));
	};

	const updateTouched = (patch: Partial<TouchedFields>) => {
		setTouchedFields((prev) => ({...prev, ...patch}));
	};

	const rangeOptions = useMemo(
		() =>
			lastPublishDate || appliedValue.range === Range.FromLastPublishDate
				? [
						...RANGE_OPTIONS,
						{
							label: Liferay.Language.get(
								'from-last-publish-date'
							),
							value: Range.FromLastPublishDate,
						},
					]
				: RANGE_OPTIONS,
		[appliedValue, lastPublishDate]
	);

	const handleShowResults = () => {
		setTouchedFields({endDate: true, startDate: true});

		if (validation.isValid) {
			onApplyFilter?.(editingToDateFilter(editing));
		}
	};

	const handleClearFilters = () => {
		setEditing(INITIAL_EDITING);
		setTouchedFields(INITIAL_TOUCHED);
		onApplyFilter?.({range: Range.All});
	};

	return (
		<>
			<ClayLayout.ContentRow className="flex-column flex-lg-row" padded>
				<ClayLayout.ContentCol>
					<FieldSelectWithOption
						formGroupProps={{className: 'mb-0'}}
						id="range"
						label={Liferay.Language.get('filter-content-by')}
						name="range"
						onChange={(event) =>
							updateFilter({
								range: event.target.value as Range,
							})
						}
						options={rangeOptions}
						value={editing.range}
					/>
				</ClayLayout.ContentCol>

				{editing.range === Range.Last && (
					<LastRangeFields
						handleUpdateFilter={updateFilter}
						value={editing.last}
					/>
				)}

				{editing.range === Range.DateRange && (
					<DateRangeFields
						editing={editing}
						errors={validation.errors}
						handleUpdateFilter={updateFilter}
						handleUpdateTouched={updateTouched}
						touchedFields={touchedFields}
					/>
				)}

				<ClayLayout.ContentCol
					className="align-items-end d-flex justify-content-center"
					expand
				>
					{!(
						editing.range === Range.All &&
						appliedValue.range === Range.All
					) && (
						<ClayButton
							disabled={isDirty ? !validation.isValid : true}
							displayType="secondary"
							onClick={handleShowResults}
							size="sm"
						>
							{Liferay.Language.get('show-results')}
						</ClayButton>
					)}
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{appliedValue.range !== Range.All && (
				<ClayLayout.ContentRow className="mt-4" padded>
					<ClayLayout.ContentCol expand>
						<ClayAlert
							actions={
								<ClayButton
									borderless
									onClick={handleClearFilters}
									size="sm"
								>
									{Liferay.Language.get('clear-filters')}
								</ClayButton>
							}
							className="w-100"
							displayType="info"
							title={sub(
								Liferay.Language.get(
									'x-results-found-for-colon'
								),
								String(itemsCount)
							)}
							variant="inline"
						>
							{appliedFilterSummary}
						</ClayAlert>
					</ClayLayout.ContentCol>
				</ClayLayout.ContentRow>
			)}
		</>
	);
}
