/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';

// @ts-ignore

import moment from 'moment/min/moment-with-locales';
import React, {useMemo} from 'react';

import DatePickerBase, {
	DatePickerBaseProps,
} from '../DatePicker/DatePickerBase';
import LocalesDropdown, {
	AvailableLocale,
} from '../util/localizable/LocalesDropdown';

import './DatePickerLocalizedObjectField.scss';

import {datetimeUtils} from '@liferay/object-js-components-web';
import {useFormState} from 'data-engine-js-components-web';

import {Date, DateMaskParams} from '../DatePicker/DatePicker';

import type {LocalizedValue} from '../types';

export default function DatePickerLocalizedObjectField(
	props: DatePickerLocalizedProps
) {
	const {
		availableLocales,
		defaultLanguageId,
		fieldName,
		localizable,
		name,
		onChange,
		predefinedValue,
		type,
		value,
	} = props;

	const {editingLanguageId}: {editingLanguageId: Liferay.Language.Locale} =
		useFormState();

	const dateMaskParams: DateMaskParams = useMemo(() => {
		let parameters: DateMaskParams = {};
		parameters = datetimeUtils.generateDateConfigurations({
			defaultLanguageId,
			locale: editingLanguageId,
			type,
		});

		return parameters;
	}, [defaultLanguageId, editingLanguageId, type]);

	const date: Date = useMemo(() => {
		let formattedDate = '';
		let year = moment().year();
		const locale = editingLanguageId;

		const rawDate =
			value?.[locale] ??
			value?.[defaultLanguageId] ??
			predefinedValue ??
			'';

		if (rawDate !== '') {
			const date = moment(rawDate, dateMaskParams.serverFormat, true);
			formattedDate = date
				.locale(locale ?? defaultLanguageId)
				.format(dateMaskParams.momentFormat);
			year = date.year();
		}

		return {
			formattedDate,
			locale,
			name,
			predefinedValue,
			rawDate,
			years: {end: year + 25, start: year - 100},
		};
	}, [
		dateMaskParams,
		defaultLanguageId,
		editingLanguageId,
		name,
		predefinedValue,
		value,
	]);

	const handleDateChange = (date: string) => {
		const newValue = {
			...(value as LocalizedValue<string>),
			[editingLanguageId]: date,
		};

		onChange({target: {value: newValue}});
	};

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem className="ddm-object-field-date-picker-localized">
				<DatePickerBase
					{...props}
					date={date}
					dateMaskParams={dateMaskParams}
					defaultLanguageId={defaultLanguageId}
					locale={editingLanguageId}
					localizable={localizable}
					name={name}
					onChange={handleDateChange}
					predefinedValue={predefinedValue}
					type={type}
					value={value}
				/>
			</ClayInput.GroupItem>

			<ClayInput.GroupItem shrink>
				<LocalesDropdown
					availableLocales={availableLocales}
					fieldName={fieldName}
					value={value}
				/>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}

export interface DatePickerLocalizedProps extends DatePickerBaseProps {
	availableLocales: AvailableLocale[];
	fieldName: string;
	onChange: any;
	value: LocalizedValue<string>;
}
