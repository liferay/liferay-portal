/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import {datetimeUtils} from '@liferay/object-js-components-web';
import {dateUtils} from 'frontend-js-web';
import moment from 'moment';
import React, {useId, useImperativeHandle, useState} from 'react';

import FieldWrapper from '../../common/components/forms/FieldWrapper';

export const dateConfig = datetimeUtils.generateDateConfigurations({
	defaultLanguageId: Liferay.ThemeDisplay.getDefaultLanguageId(),
	locale: Liferay.ThemeDisplay.getLanguageId(),
	type: 'DateTime',
});

export default React.forwardRef(function ScheduleField(
	{
		date: initialDate = '',
		dateConfig,
		error: initialError = '',
		label,
		name,
		neverExpire,
		required = false,
		updateFieldData,
	}: {
		date: string | undefined;
		dateConfig: datetimeUtils.DateConfig;
		error?: string;
		label: string;
		name: string;
		neverExpire?: boolean;
		required?: boolean;
		updateFieldData: any;
	},
	ref
) {
	const [checked, setChecked] = useState<boolean>(Boolean(neverExpire));
	const [date, setDate] = useState<string>(initialDate);
	const [error, setError] = useState<string>(initialError);

	const hasNeverExpire = neverExpire !== undefined;
	const id = useId();
	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const onBlur = (value: string) => {
		let error = '';
		const isValid = moment(value, dateConfig.momentFormat, true).isValid();

		if (required && !value) {
			error = Liferay.Language.get('this-field-is-required');
		}
		else if (value && !isValid) {
			error = Liferay.Language.get('the-field-value-is-invalid');
		}
		else if (isPastDate(value)) {
			error = Liferay.Language.get('the-date-entered-is-in-the-past');
		}

		setError(error);

		updateFieldData({
			error,
			name,
			value,
		});
	};

	const placeholder = dateConfig.momentFormat
		.replace(/hh:mm|HH:mm/g, '--:--')
		.replace('A', '--');

	useImperativeHandle(ref, () => ({
		validate: () => {
			onBlur(date);

			return required ? date && !error : !error;
		},
	}));

	return (
		<div aria-label={label} role="group">
			<FieldWrapper
				disabled={checked}
				errorMessage={!hasNeverExpire || !checked ? error : ''}
				fieldId={id}
				label={label}
				required={required}
			>
				<ClayDatePicker
					aria-describedby={error}
					dateFormat={dateConfig.clayFormat}
					disabled={checked}
					firstDayOfWeek={dateUtils.getFirstDayOfWeek(locale)}
					id={id}
					months={dateUtils.getMonthsLong(locale)}
					onBlur={({target: {value}}) => onBlur(value)}
					onChange={(value: string) => {
						setError('');
						setDate(value);
					}}
					onExpandedChange={(open: boolean) => {
						if (!open) {
							onBlur(date);
						}
					}}
					placeholder={placeholder}
					time
					timezone={Liferay.ThemeDisplay.getTimeZone()}
					use12Hours={dateConfig.use12Hours}
					value={date}
					weekdaysShort={dateUtils.getWeekdaysShort(locale)}
					years={{
						end: new Date().getFullYear() + 5,
						start: new Date().getFullYear(),
					}}
				/>
			</FieldWrapper>

			{hasNeverExpire ? (
				<ClayForm.Group>
					<ClayCheckbox
						checked={checked}
						label={Liferay.Language.get('never-expire')}
						onChange={({target: {checked}}) => {
							setChecked(checked);

							updateFieldData({
								error,
								name,
								neverExpire: checked,
								value: checked ? '' : date,
							});
						}}
					/>
				</ClayForm.Group>
			) : null}
		</div>
	);
});

function isPastDate(value: string) {
	const languageId = Liferay.ThemeDisplay.getBCP47LanguageId();
	const timeZone = Liferay.ThemeDisplay.getTimeZone();

	const timeZoneDateTime = new Date(
		new Date().toLocaleString(languageId, {
			timeZone,
		})
	);

	return timeZoneDateTime >= new Date(toServerFormat(value));
}

export function toMomentDate(value: string) {
	return value ? moment(value).format(dateConfig.momentFormat) : '';
}

export function toServerFormat(value: string) {
	return moment(value, dateConfig.momentFormat, true).format(
		dateConfig.serverFormat
	);
}

export function toServerISOFormat(value: string) {
	return toServerFormat(value).replace(' ', 'T');
}
