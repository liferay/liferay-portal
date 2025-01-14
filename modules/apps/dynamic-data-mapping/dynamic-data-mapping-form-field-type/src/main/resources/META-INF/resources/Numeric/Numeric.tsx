/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useFormState} from 'data-engine-js-components-web';

// @ts-ignore

import React, {FocusEventHandler} from 'react';

import FieldBase from '../FieldBase/ReactFieldBase.es';
import {ISymbols} from '../NumericInputMask/NumericInputMask';
import NumericLocalizedObjectField from '../localizedObjectFields/NumericLocalizedObjectField';
import {EditingLocale} from '../util/localizable/LocalesDropdown';

// @ts-ignore

import withConfirmationField from '../util/withConfirmationField.es';
import NumericBase from './NumericBase';

import './Numeric.scss';

import type {FieldChangeEventHandler, Locale, LocalizedValue} from '../types';

const Numeric = ({
	localizedObjectField,
	localizedValue,
	...otherProps
}: NumericProps) => {
	const {defaultLanguageId}: {defaultLanguageId: Locale} = useFormState();

	const Component =
		Liferay.FeatureFlags['LPD-32050'] && localizedObjectField
			? NumericLocalizedObjectField
			: NumericBase;

	return (
		<FieldBase
			{...(!localizedObjectField && {localizedValue})}
			{...otherProps}
		>
			<ClayInput.Group>
				<Component
					{...otherProps}
					editingLocale={defaultLanguageId}
					localizedObjectField={localizedObjectField}
				/>
			</ClayInput.Group>
		</FieldBase>
	);
};

export {Numeric};
export default withConfirmationField(Numeric);

export type NumericProps = {
	append: string;
	appendType: 'prefix' | 'suffix';
	availableLocales: EditingLocale[];
	dataType: NumericDataType;
	decimalPlaces: number;
	defaultLanguageId: Locale;
	defaultLocale: EditingLocale;
	errorMessage?: string;
	fieldName: string;
	focused: boolean;
	htmlAutocompleteAttribute: string;
	id: string;
	inputMask?: boolean;
	inputMaskFormat?: string;
	localizedObjectField: boolean;
	localizedSymbols?: LocalizedValue<ISymbols>;
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
