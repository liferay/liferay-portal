/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';

// @ts-ignore

import {SettingsContext} from 'data-engine-js-components-web';
import React, {ChangeEventHandler, useMemo} from 'react';

import {ISymbols} from '../NumericInputMask/NumericInputMask';
import {Locale, LocalizedValue} from '../types';
import {getTooltipTitle} from '../util/tooltip';
import {NumericProps} from './Numeric';
import {
	formatValue,
	getFormattedValue,
	getMaskedValue,
	getValue,
} from './numericUtil';

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
	defaultLanguageId,
	editingLocale,
	focused,
	errorMessage,
	htmlAutocompleteAttribute,
	id,
	inputMask,
	inputMaskFormat,
	localizedObjectField,
	localizedValue,
	localizedSymbols,
	name,
	onChange,
	onBlur,
	onFocus,
	placeholder,
	predefinedValue,
	readOnly,
	required,
	symbols: symbolsProp = {decimalSymbol: '.'},
	settingsContext,
	tip,
	valid,
	value,
}: Omit<NumericProps, 'availableLocales'> & {editingLocale: Locale}) => {
	const accessibleProperties = {
		...(tip && {
			'aria-describedby': `${id ?? name}_fieldHelp`,
		}),
		...(errorMessage && {
			'aria-errormessage': `${id ?? name}_fieldError`,
		}),
		'aria-invalid': !valid,
		'aria-required': required,
	};

	const localizedSymbolsContext = settingsContext
		? SettingsContext.getSettingsContextProperty(
				settingsContext,
				'predefinedValue',
				'localizedSymbols'
			)
		: localizedSymbols;

	const symbols = useMemo<ISymbols>(() => {
		if (inputMask) {
			return {
				decimalSymbol: symbolsProp.decimalSymbol,
				thousandsSeparator:
					symbolsProp.thousandsSeparator === 'none'
						? null
						: symbolsProp.thousandsSeparator,
			};
		}

		return localizedSymbolsContext?.[editingLocale] || symbolsProp;
	}, [editingLocale, inputMask, localizedSymbolsContext, symbolsProp]);

	const inputValue = useMemo<IMaskedNumber>(() => {
		let newValue =
			getValue({
				editingLanguageId: editingLocale,
				localizedObjectField,
				value,
			}) ??
			getValue({
				editingLanguageId: defaultLanguageId,
				localizedObjectField,
				value,
			}) ??
			localizedValue?.[editingLocale] ??
			localizedValue?.[defaultLanguageId] ??
			predefinedValue ??
			'';

		if (dataType === 'double') {
			const symbolsValue = newValue.match(/[^-\d]/g);

			newValue = symbolsValue
				? newValue.replace(symbolsValue[0], symbols.decimalSymbol)
				: newValue;
		}

		return inputMask
			? getMaskedValue({
					dataType,
					decimalPlaces,
					focused,
					includeThousandsSeparator: Boolean(
						symbols.thousandsSeparator
					),
					inputMaskFormat: String(inputMaskFormat),
					symbols,
					value: newValue,
				})
			: {
					...getFormattedValue({
						dataType,
						decimalSymbol: symbols.decimalSymbol,
						value: newValue,
					}),
					placeholder,
				};
	}, [
		dataType,
		decimalPlaces,
		defaultLanguageId,
		editingLocale,
		focused,
		inputMask,
		inputMaskFormat,
		localizedObjectField,
		localizedValue,
		placeholder,
		predefinedValue,
		symbols,
		value,
	]);

	const handleChange: ChangeEventHandler<HTMLInputElement> = (event) => {
		const targetValue = event.target.value;

		const formatedValue = formatValue({
			dataType,
			decimalPlaces,
			focused,
			inputMask,
			inputMaskFormat,
			inputValue,
			symbols,
			value: targetValue,
		});

		if (formatedValue && formatedValue.masked !== inputValue.masked) {
			if (localizedObjectField) {
				const localazedValue = {
					...(value as LocalizedValue<string>),
					[editingLocale]: formatedValue.raw,
				};

				onChange({target: {value: localazedValue}});
			}
			else {
				onChange({target: {value: formatedValue.raw}});
			}
		}
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
							Liferay.Language.direction[editingLocale] === 'rtl',
					})}
					disabled={readOnly}
					id={id ?? name}
					name={name}
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
