/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useFormState} from 'data-engine-js-components-web';
import React, {FocusEventHandler} from 'react';

import FieldBase from '../FieldBase/ReactFieldBase.es';
import {ISymbols} from '../NumericInputMask/NumericInputMask';
import NumericLocalizedObjectField from '../localizedObjectFields/NumericLocalizedObjectField';

// @ts-ignore

import withConfirmationField from '../util/withConfirmationField.es';
import NumericBase, {IMaskedNumber} from './NumericBase';
import {useNumericInputValueMemo} from './hooks';
import {getSymbols, maxLengthExceeded} from './numericUtil';

import './Numeric.scss';
import {AvailableLocale} from '../util/localizable/LocalesDropdown';

import type {FieldChangeEventHandler, Locale, LocalizedValue} from '../types';

const Numeric: React.FC<NumericProps> = (props) => {
	const {
		dataType,
		decimalPlaces,
		defaultLanguageId,
		focused,
		inputMask,
		inputMaskFormat,
		localizedValue,
		localizedSymbols,
		onChange,
		placeholder,
		predefinedValue,
		settingsContext,
		symbols: symbolsProp = {decimalSymbol: '.'},
		value,
	} = props as Omit<NumericProps, 'value'> & {
		value: string;
	};

	const {editingLanguageId}: {editingLanguageId: Locale} = useFormState();

	const symbols = getSymbols({
		editingLocale: editingLanguageId,
		inputMask,
		localizedSymbols,
		settingsContext,
		symbolsProp,
	});

	const inputValue: any = useNumericInputValueMemo({
		dataType,
		decimalPlaces,
		focused,
		inputMask,
		inputMaskFormat,
		placeholder,
		symbols,
		value:
			value ??
			localizedValue?.[editingLanguageId] ??
			localizedValue?.[defaultLanguageId] ??
			predefinedValue ??
			'',
	});

	const handleChange = (formattedValue: IMaskedNumber) => {
		if (maxLengthExceeded(formattedValue.raw, inputMaskFormat)) {
			return;
		}

		if (formattedValue.masked !== inputValue.masked) {
			onChange({
				target: {value: formattedValue?.raw ? formattedValue.raw : ''},
			});
		}
	};

	return (
		<NumericBase
			{...props}
			editingLanguageId={editingLanguageId}
			inputValue={inputValue}
			onChange={handleChange}
			symbols={symbols}
		/>
	);
};

const Main = ({
	localizedObjectField,
	localizedValue,
	...otherProps
}: NumericProps) => {
	const Component =
		Liferay.FeatureFlags['LPD-32050'] && localizedObjectField
			? NumericLocalizedObjectField
			: Numeric;

	return (
		<FieldBase
			{...otherProps}
			{...(!localizedObjectField && {localizedValue})}
		>
			<ClayInput.Group>
				<Component {...otherProps} localizedValue={localizedValue} />
			</ClayInput.Group>
		</FieldBase>
	);
};

Main.displayName = 'Checkbox';

export {Main};
export default withConfirmationField(Main);

export type NumericProps = {
	append: string;
	appendType: 'prefix' | 'suffix';
	availableLocales: AvailableLocale[];
	dataType: NumericDataType;
	decimalPlaces: number;
	defaultLanguageId: Locale;
	editingLanguageId: Locale;
	editingLocale: Locale;
	errorMessage?: string;
	fieldName: string;
	focused: boolean;
	htmlAutocompleteAttribute: string;
	id: string;
	inputMask?: boolean;
	inputMaskFormat?: string;
	localizedObjectField?: boolean;
	localizedSymbols: LocalizedValue<ISymbols>;
	localizedValue?: LocalizedValue<string>;
	name: string;
	onBlur: FocusEventHandler<HTMLInputElement>;
	onChange: FieldChangeEventHandler<string | LocalizedValue<string>>;
	onFocus: FocusEventHandler<HTMLInputElement>;
	placeholder?: string;
	predefinedValue?: string;
	readOnly: boolean;
	required?: boolean;
	settingsContext?: any;
	symbols: ISymbols;
	tip?: string;
	valid?: boolean;
	value: LocalizedValue<string> | string;
};

export type NumericDataType = 'integer' | 'double';
