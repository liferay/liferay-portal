/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import NumericInputMask from '../../../src/main/resources/META-INF/resources/js/NumericInputMask/NumericInputMask';

const globalLanguageDirection = Liferay.Language.direction;

const DECIMAL_SYMBOLS = [
	{label: '0,00', reference: ',', value: ','},
	{label: '0.00', reference: '.', value: '.'},
];

const THOUSANDS_SEPARATORS = [
	{label: '1 000', reference: ' ', value: ' '},
	{label: "1'000", reference: "'", value: "'"},
	{label: 'None', reference: 'none', value: 'none'},
	{label: '1,000', reference: ',', value: ','},
	{label: '1.000', reference: '.', value: '.'},
];

describe('Field Numeric Input Mask', () => {
	afterAll(() => {
		Liferay.Language.direction = globalLanguageDirection;
	});

	afterEach(cleanup);

	beforeAll(() => {
		jest.useFakeTimers();
		Liferay.Language.direction = {en_US: 'rtl'};
	});

	it('disables the decimalSymbol option if its equal to the selected thousands separator option', () => {
		render(
			<NumericInputMask
				append=""
				decimalSymbol=""
				decimalSymbols={DECIMAL_SYMBOLS}
				thousandsSeparator=","
				thousandsSeparators={THOUSANDS_SEPARATORS}
				visible={true}
			/>
		);

		const divWithDecimalSymbol = document.querySelector(
			'div[data-field-name="decimalSymbol"]'
		);
		const buttonInsideDiv = divWithDecimalSymbol.querySelector('button');

		userEvent.click(buttonInsideDiv);
		const disabledOption = document.querySelector(
			'.dropdown-item[disabled]'
		);
		expect(disabledOption.innerHTML).toBe('0,00');
	});

	it('disables the thousandsSeparator option if its equal to the selected decimal symbol option', () => {
		render(
			<NumericInputMask
				append=""
				decimalSymbol="."
				decimalSymbols={DECIMAL_SYMBOLS}
				thousandsSeparator=""
				thousandsSeparators={THOUSANDS_SEPARATORS}
				visible={true}
			/>
		);

		const divWithThousandsSeparator = document.querySelector(
			'div[data-field-name="thousandsSeparator"]'
		);
		const buttonInsideDiv =
			divWithThousandsSeparator.querySelector('button');

		userEvent.click(buttonInsideDiv);
		const disabledOption = document.querySelector(
			'.dropdown-item[disabled]'
		);

		expect(disabledOption.innerHTML).toBe('1.000');
	});

	it('shows the radio selector to choose the append type when there is a suffix or preffix text', () => {
		const {container} = render(
			<NumericInputMask
				append="$"
				decimalSymbols={DECIMAL_SYMBOLS}
				thousandsSeparators={THOUSANDS_SEPARATORS}
				visible={true}
			/>
		);

		const appendTypeField = container.querySelector(
			'[data-field-name="appendType"]'
		);

		expect(appendTypeField).toBeInTheDocument();
	});

	it('shows the Thousands Separator, Decimal Separator, Decimal places and Prefix or Suffix field by default', () => {
		const {container} = render(
			<NumericInputMask
				append=""
				decimalSymbols={DECIMAL_SYMBOLS}
				thousandsSeparators={THOUSANDS_SEPARATORS}
				visible={true}
			/>
		);

		const appendField = container.querySelector(
			'[data-field-name="append"]'
		);
		const appendFieldInput = container.querySelector(
			'input[name="append"]'
		);
		const decimalPlacesFieldInput = container.querySelector(
			'input[name="decimal_places"]'
		);
		const appendTypeField = container.querySelector(
			'[data-field-name="appendType"]'
		);
		const decimalSymbolField = container.querySelector(
			'[data-field-name="decimalSymbol"]'
		);
		const thousandsSeparatorField = container.querySelector(
			'[data-field-name="thousandsSeparator"]'
		);

		expect(appendField).toBeInTheDocument();
		expect(appendFieldInput.maxLength).toBe(10);
		expect(appendTypeField).not.toBeInTheDocument();
		expect(decimalPlacesFieldInput).toBeInTheDocument();
		expect(decimalSymbolField).toBeInTheDocument();
		expect(thousandsSeparatorField).toBeInTheDocument();
	});
});
