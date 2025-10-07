/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import {datetimeUtils} from '@liferay/object-js-components-web';
import {dateUtils} from 'frontend-js-web';
import moment from 'moment';
import React, {useId, useState} from 'react';

import FieldWrapper from '../../../common/components/forms/FieldWrapper';
import {
	ScheduleFields,
	UpdateScheduleProps,
	toServerFormat,
} from '../ContentEditorSidePanel';

const LABELS = {
	expirationDate: Liferay.Language.get('expiration-date'),
	reviewDate: Liferay.Language.get('review-date'),
};

export default function SchedulePanel({
	dateConfig,
	onUpdateSchedule,
	scheduleFields: fields,
}: {
	dateConfig: datetimeUtils.DateConfig;
	onUpdateSchedule: (props: UpdateScheduleProps) => void;
	scheduleFields: ScheduleFields;
}) {
	return (
		<div className="px-3">
			<p className="text-3 text-secondary">
				{Liferay.Language.get(
					'including-an-expiration-date-will-allow-your-files-to-expire-automatically-and-become-unpublished'
				)}
			</p>

			{Object.entries(fields).map(([name, values]) => {
				const label = LABELS[name as keyof typeof LABELS];

				return (
					<ScheduleField
						date={values.value}
						dateConfig={dateConfig}
						error={values.error}
						key={name}
						label={label}
						name={name}
						neverExpire={!values.serverValue}
						updateFieldData={onUpdateSchedule}
					/>
				);
			})}
		</div>
	);
}

function ScheduleField({
	date: initialDate = '',
	dateConfig,
	error: initialError = '',
	label,
	name,
	neverExpire,
	updateFieldData,
}: {
	date: string | undefined;
	dateConfig: datetimeUtils.DateConfig;
	error?: string;
	label: string;
	name: string;
	neverExpire: boolean;
	updateFieldData: any;
}) {
	const [checked, setChecked] = useState<boolean>(neverExpire);
	const [date, setDate] = useState<string>(initialDate);
	const [error, setError] = useState<string>(initialError);

	const id = useId();
	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const onBlur = (value: string) => {
		let error = '';
		const isValid = moment(value, dateConfig.momentFormat, true).isValid();

		if (value && !isValid) {
			error = Liferay.Language.get('the-field-value-is-invalid');
		}
		else if (isPastDate(date)) {
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

	return (
		<div aria-label={label} role="group">
			<FieldWrapper
				disabled={checked}
				errorMessage={!checked ? error : ''}
				fieldId={id}
				label={label}
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
					use12Hours={dateConfig.use12Hours}
					value={date}
					weekdaysShort={dateUtils.getWeekdaysShort(locale)}
					years={{
						end: new Date().getFullYear() + 5,
						start: new Date().getFullYear(),
					}}
				/>
			</FieldWrapper>

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
		</div>
	);
}

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
