/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';

// @ts-ignore

import React, {useState} from 'react';
import {flushSync} from 'react-dom';

import {NumericProps} from '../Numeric/Numeric';
import NumericBase, {IMaskedNumber} from '../Numeric/NumericBase';
import {useNumericInputValueMemo} from '../Numeric/hooks';
import {getLocalizedObjectFieldValue, getSymbols} from '../Numeric/numericUtil';
import {LocalizedValue} from '../types';
import LocalesDropdown, {
	EditingLocale,
} from '../util/localizable/LocalesDropdown';
import {getEditingLocales, getLocale} from './util/locales';

export default function NumericLocalizedObjectField({
	availableLocales,
	dataType,
	decimalPlaces,
	defaultLanguageId,
	defaultLocale,
	fieldName,
	focused,
	inputMask,
	inputMaskFormat,
	localizedSymbols,
	localizedValue,
	onChange,
	placeholder,
	predefinedValue,
	symbols: symbolsProp = {decimalSymbol: '.'},
	settingsContext,
	value,
	...otherProps
}: NumericProps) {
	const initialEditingLocales = getEditingLocales(
		availableLocales,
		defaultLocale,
		value ?? {['en_US']: ''}
	);

	const [editingLocales, setEditingLocales] = useState<EditingLocale[]>(
		initialEditingLocales
	);

	const [currentEditingLocale, setCurrentEditingLocale] = useState({
		...getLocale(editingLocales, defaultLocale, defaultLocale.localeId),
	});

	const symbols = getSymbols({
		editingLocale: currentEditingLocale.localeId,
		inputMask,
		localizedSymbols,
		settingsContext,
		symbolsProp,
	});

	const inputValue = useNumericInputValueMemo({
		dataType,
		decimalPlaces,
		focused,
		inputMask,
		inputMaskFormat,
		placeholder,
		symbols,
		value: getLocalizedObjectFieldValue(
			currentEditingLocale.localeId,
			value
		),
	});

	const handleChange = (formattedValue: IMaskedNumber) => {
		if (formattedValue && formattedValue.masked !== inputValue.masked) {
			const localizedValue = {
				...(value as LocalizedValue<string>),
				[currentEditingLocale.localeId]: formattedValue.raw,
			};

			onChange({target: {value: localizedValue}});
		}
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
		<>
			<NumericBase
				{...otherProps}
				dataType={dataType}
				decimalPlaces={decimalPlaces}
				defaultLanguageId={defaultLanguageId}
				defaultLocale={defaultLocale}
				editingLocale={currentEditingLocale.localeId}
				fieldName={fieldName}
				focused={focused}
				inputMask={inputMask}
				inputMaskFormat={inputMaskFormat}
				inputValue={inputValue}
				localizedSymbols={localizedSymbols}
				localizedValue={localizedValue}
				onChange={handleChange}
				placeholder={placeholder}
				predefinedValue={predefinedValue}
				symbols={symbols}
				value={value}
			/>

			<ClayInput.GroupItem shrink>
				<LocalesDropdown
					availableLocales={editingLocales}
					editingLocale={currentEditingLocale}
					fieldName={fieldName}
					onLanguageClicked={handleTranslationChange}
				/>
			</ClayInput.GroupItem>
		</>
	);
}
