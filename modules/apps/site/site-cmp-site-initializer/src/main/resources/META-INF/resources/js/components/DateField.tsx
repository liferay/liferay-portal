/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {datetimeUtils} from '@liferay/object-js-components-web';
import {FieldBase} from 'frontend-js-components-web';
import {dateUtils} from 'frontend-js-web';
import moment from 'moment';
import React, {useCallback, useState} from 'react';

import type {FirstDayOfWeekLocale} from 'frontend-js-web';

interface IDateField {
	errorMessage?: string;
	id: string;
	initialValue?: string;
	label: string;
	onChange: (value: string) => Promise<void> | void;
	required?: boolean;
}

export const dateConfig = datetimeUtils.generateDateConfigurations({
	defaultLanguageId: Liferay.ThemeDisplay.getDefaultLanguageId(),
	locale: Liferay.ThemeDisplay.getLanguageId(),
	type: 'Date',
});

const acceptedFormat = dateConfig.momentFormat
	.replace('MM', 'M')
	.replace('DD', 'D');

function parseDate(value: string) {
	return moment(value.trim(), acceptedFormat, true);
}

export function getDateError(value: string, required: boolean) {
	if (!value.trim()) {
		return required ? Liferay.Language.get('this-field-is-required') : '';
	}

	if (!parseDate(value).isValid()) {
		return Liferay.Language.get('please-enter-a-valid-date');
	}

	return '';
}

export function toServerDate(value: string) {
	return parseDate(value).format('YYYY-MM-DD');
}

export default function DateField({
	errorMessage: externalErrorMessage = '',
	id,
	initialValue = '',
	label,
	onChange,
	required = true,
}: IDateField) {
	const [internalErrorMessage, setInternalErrorMessage] =
		useState<string>('');
	const [value, setValue] = useState<string>(initialValue);

	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const errorMessage = internalErrorMessage || externalErrorMessage;

	const handleBlur = useCallback(
		({target: {value}}: React.FocusEvent<HTMLInputElement>) => {
			setInternalErrorMessage(getDateError(value, required));
		},
		[required]
	);

	const handleChange = async (value: string) => {
		setInternalErrorMessage('');
		setValue(value);

		await onChange(value);
	};

	return (
		<FieldBase
			errorMessage={errorMessage}
			id={id}
			label={label}
			required={required}
		>
			<ClayDatePicker
				aria-describedby={
					errorMessage ? `${id}fieldFeedback` : undefined
				}
				aria-invalid={Boolean(errorMessage)}
				dateFormat={dateConfig.clayFormat}
				firstDayOfWeek={dateUtils.getFirstDayOfWeek(
					locale as FirstDayOfWeekLocale
				)}
				id={id}
				months={dateUtils.getMonthsLong(locale)}
				onBlur={handleBlur}
				onChange={handleChange}
				placeholder={dateConfig.momentFormat}
				value={value}
				weekdaysShort={dateUtils.getWeekdaysShort(locale)}
				years={{
					end: new Date().getFullYear() + 25,
					start: new Date().getFullYear() - 100,
				}}
			/>
		</FieldBase>
	);
}
