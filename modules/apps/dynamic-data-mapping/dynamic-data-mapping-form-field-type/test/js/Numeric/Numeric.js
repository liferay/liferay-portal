/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {FormProvider} from 'data-engine-js-components-web';
import React from 'react';

import Numeric from '../../../src/main/resources/META-INF/resources/js/Numeric/Numeric';
import {maxLengthExceeded} from '../../../src/main/resources/META-INF/resources/js/Numeric/numericUtil';

const globalLanguageDirection = Liferay.Language.direction;

describe('Field Numeric', () => {
	afterAll(() => {
		Liferay.Language.direction = globalLanguageDirection;
	});

	afterEach(cleanup);

	beforeAll(() => {
		Liferay.Language.direction = {en_US: 'rtl'};
	});

	describe('Confirmation Field', () => {
		it('remove decimal symbol of the confirmation value if the data type is Integer', () => {
			render(
				<Numeric
					confirmationValue="22.82"
					name="numericField"
					requireConfirmation
				/>
			);

			const confirmationField = document.getElementById(
				'numericFieldconfirmationField'
			);

			expect(confirmationField.value).toBe('2282');
		});

		it('renders the confirmation field with the same data type as the original field', () => {
			render(
				<Numeric
					confirmationValue="22.82"
					dataType="double"
					name="numericField"
					requireConfirmation
				/>
			);

			const confirmationField = document.getElementById(
				'numericFieldconfirmationField'
			);

			expect(confirmationField.value).toBe('22.82');
		});
	});

	describe('Decimal Input Mask toggle', () => {
		it('allows user to input a decimal separator', () => {
			const onChange = jest.fn();
			const {container} = render(
				<Numeric
					dataType="double"
					inputMask
					name="numericField"
					onChange={onChange}
					symbols={{decimalSymbol: ','}}
				/>
			);

			const input = container.querySelector('input');

			userEvent.type(input, '1,234');

			expect(onChange).toHaveBeenLastCalledWith({
				target: {value: '1,23'},
			});
		});

		it('allows user to input only the decimal quantity defined by decimal places field', () => {
			const onChange = jest.fn();
			const {container} = render(
				<Numeric
					dataType="double"
					decimalPlaces={3}
					inputMask
					name="numericField"
					onChange={onChange}
					symbols={{decimalSymbol: ','}}
				/>
			);

			const input = container.querySelector('input');

			userEvent.type(input, '1,2345678');

			expect(onChange).toHaveBeenLastCalledWith({
				target: {value: '1,234'},
			});
		});

		/*
		 * LPS-141862
		 */
		it('does not allow typing sequence of zeroes', () => {
			const onChange = jest.fn();
			const {container} = render(
				<Numeric
					dataType="double"
					decimalPlaces={3}
					inputMask
					name="numericField"
					onChange={onChange}
					symbols={{decimalSymbol: ','}}
				/>
			);

			const input = container.querySelector('input');

			userEvent.type(input, '00,083');

			expect(onChange).toHaveBeenLastCalledWith({
				target: {value: '0,083'},
			});
		});

		it('does not allow typing zeroes not followed by decimal symbol', () => {
			const onChange = jest.fn();
			const {container} = render(
				<Numeric
					dataType="double"
					decimalPlaces={3}
					inputMask
					name="numericField"
					onChange={onChange}
					symbols={{decimalSymbol: ','}}
				/>
			);

			const input = container.querySelector('input');

			userEvent.type(input, '0083,5');

			expect(onChange).toHaveBeenLastCalledWith({
				target: {value: '83,5'},
			});
		});

		it('generates a placeholder', () => {
			const {container} = render(
				<Numeric
					dataType="double"
					decimalPlaces="2"
					inputMask
					name="numericField"
					symbols={{decimalSymbol: ','}}
				/>
			);

			const input = container.querySelector('input');

			expect(input).toHaveAttribute('placeholder', '0,00');
		});

		it('hides the thousand separator if it is set to `none`', () => {
			const {container} = render(
				<Numeric
					dataType="double"
					inputMask
					name="numericField"
					symbols={{decimalSymbol: '.', thousandsSeparator: 'none'}}
					value="1234"
				/>
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('1234');
		});

		/**
		 * LPS-136519 / LPS-136523
		 */
		it('ignores non decimal input', () => {
			const onChange = jest.fn();
			const {container} = render(
				<Numeric
					append="999"
					appendType="suffix"
					dataType="double"
					inputMask
					name="numericField"
					onChange={onChange}
					symbols={{decimalSymbol: ','}}
				/>
			);

			const input = container.querySelector('input');

			userEvent.type(input, 'a# @e');

			expect(onChange).not.toHaveBeenCalled();
		});

		it('renders a prefix', () => {
			const {container, getByText} = render(
				<Numeric
					append="$"
					appendType="prefix"
					dataType="double"
					inputMask
					name="numericField"
					value="123"
				/>
			);

			const input = container.querySelector('input');

			expect(getByText('$')).toHaveClass('input-group-text');
			expect(input.value).toBe('123');
		});

		it('renders a suffix', () => {
			const {container, getByText} = render(
				<Numeric
					append="$"
					appendType="suffix"
					dataType="double"
					inputMask
					name="numericField"
					value="123"
				/>
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('123');
			expect(getByText('$')).toHaveClass('input-group-text');
		});

		it('renders the thousand separator', () => {
			const {container} = render(
				<Numeric
					dataType="double"
					inputMask
					name="numericField"
					symbols={{decimalSymbol: '.', thousandsSeparator: ','}}
					value="1234"
				/>
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('1,234');
		});
	});

	describe('Integer Input Mask toggle', () => {
		it('allows input mask format to have only numbers', () => {
			const {container} = render(
				<Numeric inputMask inputMaskFormat={99} value="1234" />
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('12');
		});

		it('applies mask to value', () => {
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="+99 (99) 9999-9999"
					value="123456789012"
				/>
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('+12 (34) 5678-9012');
		});

		it('applies mask to predefined value', () => {
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="+99 (99) 9999-9999"
					predefinedValue="123456789012"
				/>
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('+12 (34) 5678-9012');
		});

		it('has an inputMaskFormat', () => {
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="+99 (99) 9999-9999"
					name="numericField"
					value="123456789012"
				/>
			);

			expect(container).toMatchSnapshot();
		});

		it('ignores optional digits whenever input is less than mandatory', () => {
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="+09 (099) 9999-9999"
					value="12345"
				/>
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('+1 (23) 45');
		});

		it('limits predefined value size according to the mask', () => {
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="99-99"
					name="LPS-134259"
					predefinedValue="12345"
				/>
			);

			const input = container.querySelector('input[name="LPS-134259"]');

			expect(input.value).toBe('1234');
		});

		it('sends unmasked value though onChange event', () => {
			const onChange = jest.fn();
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="E.g +99 (99) 9999-9999"
					onChange={onChange}
				/>
			);

			const input = container.querySelector('input');

			userEvent.type(input, 'E.g +55 (81) 2121-6000');

			expect(onChange).toHaveBeenLastCalledWith({
				target: {value: '558121216000'},
			});
		});

		it('truncates values over mask digit limit', () => {
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="+99 (099) 9999-9999"
					value="12345678901234"
				/>
			);

			const input = container.querySelector('input');

			expect(input.value).toBe('+12 (345) 6789-0123');
		});

		/**
		 * This test was skipped due to an issue on userEvent.type() that not
		 * allows simulate backspace key pressing (with the current
		 * @testing-library/use-event)
		 */
		xit('it allows to delete non numeric characters from mask', () => {
			const {container} = render(
				<Numeric
					inputMask
					inputMaskFormat="99-99"
					onChange={() => {}}
					predefinedValue="12"
				/>
			);

			const input = container.querySelector('input');

			userEvent.click(input);
			userEvent.type(input, '{backspace}');

			expect(input.value).toBe('1');
		});
	});

	it('changes the mask type', () => {
		const {container} = render(<Numeric dataType="double" value="22.22" />);

		expect(container.querySelector('input').value).toBe('22.22');
	});

	it('check field value is the same without decimal symbol when fieldType is integer but it receives a double', () => {
		const {container} = render(<Numeric value="3.8" />);

		const input = container.querySelector('input');

		expect(input.value).toBe('38');
	});

	it('disables input whenever readOnly is set', () => {
		const {container} = render(<Numeric readOnly />);

		const input = container.querySelector('input');

		expect(input).toBeDisabled();
	});

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(<Numeric required={true} />);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const {container} = render(<Numeric required={true} value="123" />);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not render html autocomplete attribute', () => {
		render(<Numeric />);

		expect(
			document.querySelector('.form-control').hasAttribute('autocomplete')
		).toBe(false);
	});

	it('enables input whenever readOnly is omitted', () => {
		const {container} = render(<Numeric />);

		const input = container.querySelector('input');

		expect(input).toBeEnabled();
	});

	it('fills with an input number', () => {
		const onChange = jest.fn();
		const {container} = render(<Numeric onChange={onChange} />);

		const input = container.querySelector('input');
		userEvent.type(input, '2');

		expect(onChange).toHaveBeenCalledWith({target: {value: '2'}});
	});

	it('filters the non numeric characters when set to integer', () => {
		const onChange = jest.fn();
		const {container} = render(<Numeric onChange={onChange} />);

		const input = container.querySelector('input');
		userEvent.type(input, '3.0');

		expect(onChange).toHaveBeenLastCalledWith({
			target: {value: '30'},
		});
	});

	it('has a helptext', () => {
		const {getAllByText} = render(<Numeric tip="Type something" />);

		expect(getAllByText('Type something')[0]).toBeInTheDocument();
	});

	it('has a label', () => {
		const {getAllByText} = render(<Numeric label="label" />);

		const allByText = getAllByText(/label/);
		expect(allByText).toHaveLength(2);
		expect(allByText[0]).toBeInTheDocument();
		expect(allByText[1]).toBeInTheDocument();
	});

	it('has a name', () => {
		const {container} = render(<Numeric name="numericField" />);

		const input = container.querySelector('input');

		expect(input).toHaveAttribute('name', 'numericField');
	});

	it('has a placeholder', () => {
		const {container} = render(<Numeric placeholder="Placeholder" />);

		const input = container.querySelector('input');

		expect(input).toHaveAttribute('placeholder', 'Placeholder');
	});

	it('has a value', () => {
		const {container} = render(<Numeric value="123" />);

		const input = container.querySelector('input');

		expect(input).toHaveValue('123');
	});

	it('has an id', () => {
		const {container} = render(<Numeric id="ID" />);

		const input = container.querySelector('input');

		expect(input).toHaveAttribute('id', 'ID');
	});

	it('is required', () => {
		const {getByText} = render(<Numeric required />);

		expect(getByText(/required/)).toBeInTheDocument();
	});

	it('remove decimal symbol from value when changing from decimal to integer when symbol of language is comma', () => {
		const {container} = render(
			<Numeric
				dataType="integer"
				symbols={{decimalSymbol: ','}}
				value="22,82"
			/>
		);

		expect(container.querySelector('input').value).toBe('2282');
	});

	it('renders Label if showLabel is true', () => {
		const {getAllByText} = render(
			<Numeric label="Numeric Field" showLabel />
		);

		const allByText = getAllByText(/Numeric Field/);
		expect(allByText).toHaveLength(2);
		expect(allByText[0]).toHaveClass('ddm-label');
		expect(allByText[1]).toHaveClass('sr-only');
	});

	it('renders the default markup', () => {
		const {container} = render(<Numeric />);

		expect(container).toMatchSnapshot();
	});

	it('renders the html autocomplete attribute', () => {
		render(<Numeric htmlAutocompleteAttribute="name" />);

		expect(
			document.querySelector('.form-control').getAttribute('autocomplete')
		).toBe('name');
	});

	it('updates decimal symbol using the current value of symbols', () => {
		const {container} = render(
			<Numeric
				dataType="double"
				symbols={{decimalSymbol: ','}}
				value="-1.2"
			/>
		);

		expect(container.querySelector('input').value).toBe('-1,2');
	});

	it('updates decimal symbol using the localizedSymbols based on current editing language', () => {
		const {container} = render(
			<FormProvider initialState={{editingLanguageId: 'pt_BR'}}>
				<Numeric
					dataType="double"
					localizedSymbols={{
						en_US: {
							decimalSymbol: '.',
						},
						pt_BR: {
							decimalSymbol: ',',
						},
					}}
					value="1.2"
				/>
			</FormProvider>
		);

		expect(container.querySelector('input').value).toBe('1,2');
	});

	describe('maxLengthExceeded function', () => {

		/**
		 * LPD-39819
		 */

		it('returns true if the input has surpassed the max length of the mask', () => {

			// input smaller or with the same size as the mask should return false

			expect(maxLengthExceeded('10', '90')).toBe(false);
			expect(maxLengthExceeded('1', '90')).toBe(false);

			// input longer than the size of the mask should return true

			expect(maxLengthExceeded('100', '90')).toBe(true);

			// when inputMaskFormat is undefined it should return false

			expect(maxLengthExceeded('10', undefined)).toBe(false);
		});
	});
});
