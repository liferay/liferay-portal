/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';

// @ts-ignore

import React from 'react';

import {FieldChangeEventHandler, Locale} from '../types';
import {getTooltipTitle} from '../util/tooltip';
import {NumericProps} from './Numeric';
import {formatValue} from './numericUtil';

export type IMaskedNumber = {
	masked: string;
	placeholder?: string;
	raw: string;
};

const NumericBase = ({
	append,
	appendType,
	dataType,
	decimalPlaces,
	displayErrors,
	editingLanguageId,
	errorMessage,
	focused,
	htmlAutocompleteAttribute,
	id,
	inputMask,
	inputMaskFormat,
	inputValue,
	name,
	onBlur,
	onChange,
	onFocus,
	readOnly,
	required,
	symbols,
	tip,
	valid,
}: Omit<NumericProps, 'availableLocales' | 'onChange'> & {
	editingLocale: Locale;
	inputValue: IMaskedNumber;
	onChange: (formattedValue: IMaskedNumber) => void;
}) => {
	const accessibleProperties = {
		...((errorMessage || tip) && {
			'aria-describedby': `${id ?? name}_fieldFeedback`,
		}),
		...(displayErrors && !valid && {'aria-invalid': true}),
		'aria-required': required,
	};

	const handleChange: FieldChangeEventHandler<string> = ({
		target: {value},
	}) => {
		const formattedValue = formatValue({
			dataType,
			decimalPlaces,
			focused,
			inputMask,
			inputMaskFormat,
			inputValue,
			symbols,
			value,
		});

		onChange(formattedValue as IMaskedNumber);
	};

	const input = (
		<ClayTooltipProvider>
			<ClayInput.GroupItem
				data-tooltip-align="top"
				{...getTooltipTitle({
					placeholder: inputValue.placeholder!,
					value: inputValue.masked,
				})}
			>
				<ClayInput
					{...accessibleProperties}
					{...(htmlAutocompleteAttribute && {
						autoComplete: htmlAutocompleteAttribute,
					})}
					className={classNames({
						'ddm-form-field-type__numeric--rtl':
							Liferay.Language.direction[editingLanguageId] ===
							'rtl',
					})}
					disabled={readOnly}
					id={id ?? name}
					name={`${name}${inputMask ? '_masked' : ''}`}
					onBlur={onBlur}
					onChange={handleChange}
					onFocus={onFocus}
					placeholder={inputValue.placeholder}
					type="text"
					value={inputValue.masked}
				/>
			</ClayInput.GroupItem>
		</ClayTooltipProvider>
	);

	return (
		<>
			{inputMask && append && dataType === 'double' ? (
				<>
					{appendType === 'prefix' && (
						<ClayInput.GroupItem prepend shrink>
							<ClayInput.GroupText>{append}</ClayInput.GroupText>
						</ClayInput.GroupItem>
					)}

					{input}

					{appendType === 'suffix' && (
						<ClayInput.GroupItem append shrink>
							<ClayInput.GroupText>{append}</ClayInput.GroupText>
						</ClayInput.GroupItem>
					)}
				</>
			) : (
				input
			)}

			{inputMask && (
				<input name={name} type="hidden" value={inputValue.raw} />
			)}
		</>
	);
};

export default NumericBase;
