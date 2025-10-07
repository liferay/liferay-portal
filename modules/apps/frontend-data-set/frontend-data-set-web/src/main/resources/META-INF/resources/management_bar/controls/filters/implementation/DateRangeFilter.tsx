/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm from '@clayui/form';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import {
	formatDateObject,
	formatDateRangeObject,
	getDateFromDateString,

	// @ts-ignore

} from '../../../../utils/dates';
import {EEntityFieldType} from '../utils/types';

import type {FilterImplementation, FilterImplementationArgs} from '../Filter';

export interface DateRangeFilterImplementationArgs
	extends FilterImplementationArgs<SelectedData> {
	entityFieldType: EEntityFieldType;
	max: Date;
	min: Date;
	placeholder: string;
}

interface Date {
	day: number;
	month: number;
	year: number;
}

interface SelectedData {
	from: Date;
	to: Date;
}

const getIsoString = ({
	direction,
	entityFieldType,
	objectDate,
}: {
	direction: 'from' | 'to';
	entityFieldType: EEntityFieldType;
	objectDate: Date;
}) => {
	const timestamp = Date.UTC(
		objectDate.year,
		objectDate.month - 1,
		objectDate.day
	);

	const date = new Date(timestamp);

	if (direction === 'from') {
		date.setUTCHours(0, 0, 0, 0);
	}
	else {
		date.setUTCHours(23, 59, 59, 999);
	}

	const dateISOString = date.toISOString();

	if (entityFieldType === EEntityFieldType.DATE) {
		return dateISOString.split('T')[0];
	}

	return dateISOString;
};

function getSelectedItemsLabel({
	selectedData,
}: DateRangeFilterImplementationArgs): string {
	return formatDateRangeObject(selectedData);
}

function getOdataString({
	entityFieldType,
	id,
	selectedData,
}: DateRangeFilterImplementationArgs): string {
	const {from, to} = selectedData;

	const fromIsoString =
		from &&
		getIsoString({direction: 'from', entityFieldType, objectDate: from});

	const toIsoString =
		to && getIsoString({direction: 'to', entityFieldType, objectDate: to});

	if (from && to) {
		return `${id} ge ${fromIsoString}) and (${id} le ${toIsoString}`;
	}
	if (from) {
		return `${id} ge ${fromIsoString}`;
	}
	if (to) {
		return `${id} le ${toIsoString}`;
	}

	return '';
}

const DateRangeFilter = ({
	id,
	max,
	min,
	placeholder,
	selectedData,
	setFilter,
}: DateRangeFilterImplementationArgs) => {
	const [fromValue, setFromValue] = useState(
		selectedData?.from && formatDateObject(selectedData.from)
	);
	const [toValue, setToValue] = useState(
		selectedData?.to && formatDateObject(selectedData.to)
	);

	let actionType = 'edit';

	if (selectedData && !fromValue && !toValue) {
		actionType = 'delete';
	}

	if (!selectedData) {
		actionType = 'add';
	}

	let submitDisabled = true;

	if (
		actionType === 'delete' ||
		((!selectedData || !selectedData.from) && fromValue) ||
		((!selectedData || !selectedData.to) && toValue) ||
		(selectedData &&
			selectedData.from &&
			fromValue !== formatDateObject(selectedData.from)) ||
		(selectedData &&
			selectedData.to &&
			toValue !== formatDateObject(selectedData.to))
	) {
		submitDisabled = false;
	}

	return (
		<>
			<ClayDropDown.Caption>
				<div className="form-group">
					<ClayForm.Group className="form-group-sm">
						<label htmlFor={`from-${id}`}>
							{Liferay.Language.get('from')}
						</label>

						<input
							className="form-control"
							id={`from-${id}`}
							max={toValue || (max && formatDateObject(max))}
							min={min && formatDateObject(min)}
							onChange={(event) =>
								setFromValue(event.target.value)
							}
							pattern="\d{4}-\d{2}-\d{2}"
							placeholder={placeholder || 'yyyy-mm-dd'}
							type="date"
							value={fromValue || ''}
						/>
					</ClayForm.Group>

					<ClayForm.Group className="form-group-sm mt-2">
						<label htmlFor={`to-${id}`}>
							{Liferay.Language.get('to')}
						</label>

						<input
							className="form-control"
							id={`to-${id}`}
							max={max && formatDateObject(max)}
							min={fromValue || (min && formatDateObject(min))}
							onChange={(event) => setToValue(event.target.value)}
							pattern="\d{4}-\d{2}-\d{2}"
							placeholder={placeholder || 'yyyy-mm-dd'}
							type="date"
							value={toValue || ''}
						/>
					</ClayForm.Group>
				</div>
			</ClayDropDown.Caption>
			<ClayDropDown.Divider />
			<ClayDropDown.Caption>
				<ClayButton
					disabled={submitDisabled}
					onClick={() => {
						if (actionType === 'delete') {
							setFilter({active: false});
						}
						else {
							const newSelectedData = {
								from: fromValue
									? getDateFromDateString(fromValue)
									: null,
								to: toValue
									? getDateFromDateString(toValue)
									: null,
							};

							setFilter({
								active: true,
								selectedData: newSelectedData,
							});
						}
					}}
					small
				>
					{actionType === 'add' && Liferay.Language.get('add-filter')}

					{actionType === 'edit' &&
						Liferay.Language.get('show-results')}

					{actionType === 'delete' &&
						Liferay.Language.get('delete-filter')}
				</ClayButton>
			</ClayDropDown.Caption>
		</>
	);
};

const dateShape = PropTypes.shape({
	day: PropTypes.number,
	month: PropTypes.number,
	year: PropTypes.number,
});

DateRangeFilter.propTypes = {
	id: PropTypes.string.isRequired,
	max: dateShape,
	min: dateShape,
	placeholder: PropTypes.string,
	selectedData: PropTypes.shape({
		from: dateShape,
		to: dateShape,
	}),
};

const filterImplementation: FilterImplementation<DateRangeFilterImplementationArgs> =
	{
		Component: DateRangeFilter,
		getOdataString,
		getSelectedItemsLabel,
	};

export default filterImplementation;
