/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useFormState} from 'data-engine-js-components-web';

// @ts-ignore

import React from 'react';

import {NumericProps} from '../Numeric/Numeric';
import NumericBase, {IMaskedNumber} from '../Numeric/NumericBase';
import {useNumericInputValueMemo} from '../Numeric/hooks';
import {getLocalizedObjectFieldValue, getSymbols} from '../Numeric/numericUtil';
import {LocalizedValue} from '../types';
import LocalesDropdown from '../util/localizable/LocalesDropdown';

export default function NumericLocalizedObjectField({
	availableLocales,
	dataType,
	decimalPlaces,
	defaultLanguageId,
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
	const {editingLanguageId}: {editingLanguageId: Liferay.Language.Locale} =
		useFormState();

	const symbols = getSymbols({
		editingLocale: editingLanguageId,
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
		value: getLocalizedObjectFieldValue(editingLanguageId, value),
	});

	const handleChange = (formattedValue: IMaskedNumber) => {
		if (formattedValue && formattedValue.masked !== inputValue.masked) {
			const localizedValue = {
				...(value as LocalizedValue<string>),
				[editingLanguageId]: formattedValue.raw,
			};

			onChange({target: {value: localizedValue}});
		}
	};

	return (
		<>
			<NumericBase
				{...otherProps}
				dataType={dataType}
				decimalPlaces={decimalPlaces}
				defaultLanguageId={defaultLanguageId}
				editingLanguageId={editingLanguageId}
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
					availableLocales={availableLocales}
					fieldName={fieldName}
					value={value as LocalizedValue<string>}
				/>
			</ClayInput.GroupItem>
		</>
	);
}
