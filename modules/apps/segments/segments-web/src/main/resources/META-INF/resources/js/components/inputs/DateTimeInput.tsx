/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {dateUtils} from 'frontend-js-web';
import {default as React, useEffect, useRef, useState} from 'react';

import {PROPERTY_TYPES} from '../../utils/constants';
import {convertTimezoneToUTC} from '../../utils/date';

const INTERNAL_DATE_FORMAT = 'yyyy-MM-dd';
const DISPLAY_DATE_FORMAT = 'yyyy/MM/dd';

interface Props {
	disabled?: boolean;
	onChange: (payload: {type: string; value: string}) => void;
	propertyLabel: string;
	propertyType: string;
	value?: string;
}

function DateTimeInput({
	disabled,
	onChange,
	propertyLabel,
	propertyType,
	value,
}: Props) {
	const [expanded, setExpanded] = useState(false);

	const [displayDate, setDisplayDate] = useState<string>(() =>
		toDisplayDate(value || new Date().toISOString())
	);

	const previousDisplayDateRef = useRef(displayDate);

	useEffect(() => {
		const nextDisplayDate = toDisplayDate(
			value || new Date().toISOString()
		);

		previousDisplayDateRef.current = nextDisplayDate;
		setDisplayDate(nextDisplayDate);
	}, [value]);

	const saveDateTimeValue = () => {
		const validDate = toDisplayDate(
			displayDate,
			previousDisplayDateRef.current
		);

		setDisplayDate(validDate);

		const internalDate =
			propertyType === PROPERTY_TYPES.DATE_TIME
				? toInternalDateTime(validDate)
				: toInternalDate(validDate);

		const previousDisplayDate = previousDisplayDateRef.current;

		if (!datesAreEqual(previousDisplayDate, validDate)) {
			previousDisplayDateRef.current = validDate;

			onChange({
				type: propertyType,
				value: internalDate,
			});
		}
	};

	const onExpandedChange = (nextExpanded: boolean) => {
		setExpanded(nextExpanded);

		if (!nextExpanded) {
			saveDateTimeValue();
		}
	};

	return (
		<div className="criterion-input date-input">
			<ClayDatePicker
				ariaLabels={{
					buttonChooseDate: `${propertyLabel}: ${Liferay.Language.get(
						'select-date'
					)}`,
					buttonDot: `${Liferay.Language.get('select-current-date')}`,
					buttonNextMonth: `${Liferay.Language.get(
						'select-next-month'
					)}`,
					buttonPreviousMonth: `${Liferay.Language.get(
						'select-previous-month'
					)}`,
					dialog: `${Liferay.Language.get('select-date')}`,
					input: `${propertyLabel}: ${Liferay.Language.get(
						'input-a-value'
					)}`,
					selectMonth: `${Liferay.Language.get('select-a-month')}`,
					selectYear: `${Liferay.Language.get('select-a-year')}`,
				}}
				data-testid="date-input"
				dateFormat="yyyy/MM/dd"
				disabled={disabled}
				expanded={expanded}
				firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
				months={[
					`${Liferay.Language.get('january')}`,
					`${Liferay.Language.get('february')}`,
					`${Liferay.Language.get('march')}`,
					`${Liferay.Language.get('april')}`,
					`${Liferay.Language.get('may')}`,
					`${Liferay.Language.get('june')}`,
					`${Liferay.Language.get('july')}`,
					`${Liferay.Language.get('august')}`,
					`${Liferay.Language.get('september')}`,
					`${Liferay.Language.get('october')}`,
					`${Liferay.Language.get('november')}`,
					`${Liferay.Language.get('december')}`,
				]}
				onBlur={saveDateTimeValue}
				onChange={setDisplayDate}
				onExpandedChange={onExpandedChange}
				value={displayDate}
				weekdaysShort={dateUtils.getWeekdaysShort()}
				years={{
					end: new Date().getFullYear(),
					start: 1900,
				}}
			/>
		</div>
	);
}

function datesAreEqual(dateA: string, dateB: string) {
	return dateA === dateB;
}

function toDisplayDate(internalOrIsoDate: string, previousDate?: string) {
	let dateObject = convertTimezoneToUTC(internalOrIsoDate);

	const resetDate = previousDate ? new Date(previousDate) : new Date();

	if (!dateUtils.isValid(dateObject)) {
		dateObject = dateUtils.parse(internalOrIsoDate, INTERNAL_DATE_FORMAT);
	}

	if (!dateUtils.isValid(dateObject)) {
		dateObject = resetDate;
	}

	return dateUtils.format(dateObject, 'yyyy/MM/dd');
}

function toInternalDate(displayOrIsoDate: string) {
	let dateObject = new Date(displayOrIsoDate);

	if (!dateUtils.isValid(dateObject)) {
		dateObject = dateUtils.parse(displayOrIsoDate, DISPLAY_DATE_FORMAT);
	}

	if (!dateUtils.isValid(dateObject)) {
		dateObject = new Date();
	}

	return dateUtils.format(dateObject, 'yyyy-MM-dd');
}

function toInternalDateTime(displayOrIsoDate: string) {
	return new Date(toInternalDate(displayOrIsoDate)).toISOString();
}

export default DateTimeInput;
