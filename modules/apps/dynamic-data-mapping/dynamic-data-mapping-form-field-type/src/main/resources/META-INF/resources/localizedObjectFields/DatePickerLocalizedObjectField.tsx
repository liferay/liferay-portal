/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';

// @ts-ignore

import moment from 'moment/min/moment-with-locales';
import React, {useMemo, useState} from 'react';
import {flushSync} from 'react-dom';

import DatePickerBase, {
	DatePickerBaseProps,
} from '../DatePicker/DatePickerBase';
import LocalesDropdown, {
	EditingLocale,
} from '../util/localizable/LocalesDropdown';
import {getEditingLocales, getLocale} from './util/locales';

import './DatePickerLocalizedObjectField.scss';

import {datetimeUtils} from '@liferay/object-js-components-web';

import {Date, DateMaskParams} from '../DatePicker/DatePicker';

import type {LocalizedValue} from '../types';

export default function DatePickerLocalizedObjectField(
	props: DatePickerLocalizedProps
) {
	const {
		availableLocales,
		defaultLanguageId,
		defaultLocale,
		fieldName,
		localizable,
		name,
		onChange,
		predefinedValue,
		type,
		value,
	} = props;

	const initialEditingLocales = getEditingLocales(
		availableLocales,
		defaultLocale,
		value
	);

	const [editingLocales, setEditingLocales] = useState<EditingLocale[]>(
		initialEditingLocales
	);

	const [currentEditingLocale, setCurrentEditingLocale] = useState({
		...getLocale(editingLocales, defaultLocale, defaultLocale.localeId),
	});

	const dateMaskParams: DateMaskParams = useMemo(() => {
		let parameters: DateMaskParams = {};
		parameters = datetimeUtils.generateDateConfigurations({
			defaultLanguageId,
			locale: currentEditingLocale.localeId,
			type,
		});

		return parameters;
	}, [defaultLanguageId, currentEditingLocale, type]);

	const date: Date = useMemo(() => {
		let formattedDate = '';
		let year = moment().year();
		const locale = currentEditingLocale.localeId;

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
		currentEditingLocale.localeId,
		name,
		predefinedValue,
		value,
	]);

	const handleDateChange = (date: string) => {
		const newValue = {
			...(value as LocalizedValue<string>),
			[currentEditingLocale.localeId]: date,
		};

		onChange({target: {value: newValue}});
	};

	const handleTranslationChange = (localeId: Liferay.Language.Locale) => {
		const currentLocale = getLocale(
			editingLocales,
			defaultLocale,
			localeId
		);

		const updatedLocale = {...currentLocale, isTranslated: true};

		setEditingLocales((previous) =>
			previous.map((locale) =>
				locale.localeId === localeId ? updatedLocale : locale
			)
		);

		setCurrentEditingLocale(updatedLocale);
	};

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem className="ddm-object-field-date-picker-localized">
				<DatePickerBase
					{...props}
					date={date}
					dateMaskParams={dateMaskParams}
					defaultLanguageId={defaultLanguageId}
					locale={currentEditingLocale.localeId}
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
					availableLocales={editingLocales}
					editingLocale={currentEditingLocale}
					fieldName={fieldName}
					onLanguageClicked={handleTranslationChange}
				/>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}

export interface DatePickerLocalizedProps extends DatePickerBaseProps {
	availableLocales: EditingLocale[];
	defaultLocale: EditingLocale;
	fieldName: string;
	onChange: any;
	value: LocalizedValue<string>;
}
