/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createNumberMask} from 'text-mask-addons';
import {conformToMask} from 'text-mask-core';

import {ISymbols} from '../NumericInputMask/NumericInputMask';
import {LocalizedValue} from '../types';
import {trimLeftZero} from '../util/numericalOperations';
import {NumericDataType} from './Numeric';
import {IMaskedNumber} from './NumericBase';

const NON_NUMERIC_REGEX = /[\D]/g;

const adaptiveMask = (rawValue: string, inputMaskFormat: string) => {
	const generateMask = (mask: string): string => {
		if (!mask.includes('0')) {
			return mask;
		}

		const inputNumbers = rawValue.match(/\d/g)?.length ?? 0;
		const mandatorySize = mask.match(/9/g)?.length ?? 0;
		if (inputNumbers <= mandatorySize) {
			return mask.replace(/0/g, '');
		}

		return generateMask(mask.replace('0', '9'));
	};

	return [...generateMask(inputMaskFormat)].map((char) =>
		char === '9' ? /\d/ : char
	);
};

export function maxLengthExceeded(value: string, inputMaskFormat?: string) {
	return !!(inputMaskFormat && value.length > inputMaskFormat.length);
}

interface INumberMaskConfig {
	allowDecimal?: boolean;
	allowLeadingZeroes: boolean;
	allowNegative: boolean;
	decimalLimit?: number | null;
	decimalSymbol?: string;
	includeThousandsSeparator: boolean;
	prefix?: string;
	suffix?: string;
	thousandsSeparatorSymbol?: string | null;
}

export function getMaskedValue({
	dataType,
	decimalPlaces,
	focused,
	includeThousandsSeparator = false,
	inputMaskFormat,
	symbols,
	value,
}: {
	dataType: NumericDataType;
	decimalPlaces: number;
	focused: boolean;
	includeThousandsSeparator?: boolean;
	inputMaskFormat: string;
	symbols: ISymbols;
	value: string;
}): IMaskedNumber {
	let mask;
	if (dataType === 'double') {
		const config: INumberMaskConfig = {
			allowDecimal: true,
			allowLeadingZeroes: true,
			allowNegative: true,
			decimalLimit: decimalPlaces,
			decimalSymbol: symbols.decimalSymbol,
			includeThousandsSeparator,
			prefix: '',
			thousandsSeparatorSymbol: symbols.thousandsSeparator ?? '',
		};

		mask = createNumberMask(config);
	}
	else {
		mask = adaptiveMask(value, inputMaskFormat);
	}

	const {conformedValue: masked} = conformToMask(value, mask, {
		guide: false,
		keepCharPositions: false,
		placeholderChar: '\u2000',
	});

	const regex = new RegExp(
		dataType === 'double' ? `[^-${symbols.decimalSymbol}\\d]` : '[^\\d]',
		'g'
	);

	let raw = masked.replace(regex, '');

	if (dataType === 'double') {
		const symbolsValue = raw.match(/[^-\d]/g);

		if (symbolsValue) {
			raw = raw.replace(symbolsValue[0], '.');
		}
	}

	const splitNumbers = masked.split(symbols.decimalSymbol);

	const decimalDigitsLength =
		splitNumbers.length > 1 ? splitNumbers.pop().length : 0;

	return {
		masked:
			!focused && dataType === 'double' && decimalDigitsLength
				? masked + '0'.repeat(decimalPlaces - decimalDigitsLength)
				: masked,
		placeholder:
			dataType === 'double'
				? `0${symbols.decimalSymbol}${'0'.repeat(decimalPlaces)}`
				: inputMaskFormat.replace(/\d/g, '_'),
		raw,
	};
}

export function getFormattedValue({
	dataType,
	decimalSymbol,
	value,
}: {
	dataType: NumericDataType;
	decimalSymbol: ',' | '.';
	value: string;
}) {
	if (!value) {
		return {masked: '', raw: ''};
	}

	const config: INumberMaskConfig = {
		allowLeadingZeroes: true,
		allowNegative: true,
		includeThousandsSeparator: false,
		prefix: '',
	};

	if (dataType === 'double') {
		config.allowDecimal = true;
		config.decimalLimit = null;
		config.decimalSymbol = decimalSymbol;
	}
	const mask = createNumberMask(config);

	const {conformedValue: masked}: {conformedValue: string} = conformToMask(
		value,
		mask,
		{
			guide: false,
			keepCharPositions: false,
			placeholderChar: '\u2000',
		}
	);

	let raw = masked;

	if (dataType === 'double') {
		const symbolsValue = masked.match(/[^-\d]/g);

		if (symbolsValue) {
			raw = masked.replace(symbolsValue[0], '.');
		}
	}

	return {
		masked: dataType === 'double' ? value : masked,
		raw,
	};
}

export function getValue({
	editingLanguageId,
	localizedObjectField,
	value,
}: {
	editingLanguageId: Liferay.Language.Locale;
	localizedObjectField: boolean;
	value: string | LocalizedValue<string> | undefined;
}) {
	if (localizedObjectField) {
		const localizedValue = (value as LocalizedValue<string>)[
			editingLanguageId
		];

		return localizedValue?.toString();
	}

	return value as string;
}

type formatValueProps = {
	dataType: 'integer' | 'double';
	decimalPlaces: number;
	focused: boolean;
	inputMask: boolean | undefined;
	inputMaskFormat: string | undefined;
	inputValue: IMaskedNumber;
	symbols: ISymbols;
	value: string;
};

export function formatValue({
	dataType,
	decimalPlaces,
	focused,
	inputMask,
	inputMaskFormat,
	inputValue,
	symbols,
	value,
}: formatValueProps) {
	if (maxLengthExceeded(value, inputMaskFormat)) {
		return;
	}

	value =
		inputMask && dataType === 'integer'
			? value
			: trimLeftZero({
					decimalSymbol: symbols.decimalSymbol,
					thousandsSeparator: symbols.thousandsSeparator,
					value,
				});

	// allows user to delete characters from the mask

	const inputValueRaw = inputValue.raw.replace(NON_NUMERIC_REGEX, '');
	const rawValue = value.replace(NON_NUMERIC_REGEX, '');

	if (
		inputValue.masked?.length > value.length &&
		(inputValueRaw?.length ?? 0) === rawValue.length
	) {
		value = inputValueRaw.slice(0, -1);
	}

	return inputMask
		? getMaskedValue({
				dataType,
				decimalPlaces,
				focused,
				inputMaskFormat: String(inputMaskFormat),
				symbols,
				value,
			})
		: getFormattedValue({
				dataType,
				decimalSymbol: symbols.decimalSymbol,
				value,
			});
}
