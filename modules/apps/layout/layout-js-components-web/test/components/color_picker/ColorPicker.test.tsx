/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ColorPicker from '../../../src/main/resources/META-INF/resources/js/components/color_picker/ColorPicker';
import {StyleErrorsContextProvider} from '../../../src/main/resources/META-INF/resources/js/contexts/StyleErrorsContext';
import {Field} from '../../../src/main/resources/META-INF/resources/js/types/ColorPicker';

const COLOR_PICKER_CLASS = '.layout__color-picker';
const INPUT_NAME = 'Color Picker';
const TOKEN_VALUES = {
	blue: {
		editorType: 'ColorPicker',
		label: 'Blue',
		name: 'blue',
		tokenCategoryLabel: 'Category 1',
		tokenSetLabel: 'TokenSet 1',
		value: '#4b9fff',
	},
	darkBlue: {
		editorType: 'ColorPicker',
		label: 'Dark Blue',
		name: 'darkBlue',
		tokenCategoryLabel: 'Category 1',
		tokenSetLabel: 'TokenSet 1',
		value: '#00008b',
	},
	green: {
		editorType: 'ColorPicker',
		label: 'Green',
		name: 'green',
		tokenCategoryLabel: 'Category 1',
		tokenSetLabel: 'TokenSet 1',
		value: '#9be169',
	},
	orange: {
		editorType: 'ColorPicker',
		label: 'Orange',
		name: 'orange',
		tokenCategoryLabel: 'Category 2',
		tokenSetLabel: 'TokenSet 2',
		value: '#ffb46e',
	},
};

const FIELD: Field = {label: INPUT_NAME, name: INPUT_NAME};

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	sub: (str: string, arg: string) => str.replace('x', arg),
}));

const renderColorPicker = ({
	onValueSelect = () => {},
	value = 'green',
	field = FIELD,
	editedTokenValues = {},
} = {}) =>
	render(
		<StyleErrorsContextProvider>
			<ColorPicker
				editedTokenValues={editedTokenValues}
				field={field}
				onValueSelect={onValueSelect}
				tokenValues={TOKEN_VALUES}
				value={value}
			/>
		</StyleErrorsContextProvider>
	);

const onTypeValue = async (input: HTMLInputElement, value: string) => {
	await userEvent.clear(input);

	if (value) {
		await userEvent.type(input, value);
	}

	fireEvent.blur(input);
};

describe('ColorPicker', () => {
	it('renders the ColorPicker', () => {
		const {baseElement} = renderColorPicker();

		expect(
			baseElement.querySelector(`${COLOR_PICKER_CLASS}`)
		).toBeInTheDocument();
	});

	it('clears the value and sets "default"', async () => {
		const onValueSelect = jest.fn();

		const {getByTitle} = renderColorPicker({onValueSelect});

		await userEvent.click(getByTitle('clear-selection'));

		expect(onValueSelect).toBeCalledWith('Color Picker', '');
	});

	it('clears the value and sets the default value of the field if it exists', async () => {
		const {baseElement, getByTitle} = renderColorPicker({
			field: {...FIELD, defaultValue: '#abcabc'},
		});

		await userEvent.click(getByTitle('clear-selection'));

		expect(baseElement.querySelector('input')).toHaveValue('ABCABC');
	});

	describe('When the value is an existing token', () => {
		it('renders the dropdown color picker', () => {
			const {getByLabelText, getByTitle} = renderColorPicker();

			expect(getByTitle('detach-style')).toBeInTheDocument();
			expect(
				getByLabelText('select-color.-color-Green-is-selected')
			).toBeInTheDocument();
		});

		it('shows action buttons when the color picker is clicked', async () => {
			const {baseElement, getByLabelText} = renderColorPicker();

			await userEvent.click(
				getByLabelText('select-color.-color-Green-is-selected')
			);

			expect(baseElement.querySelector(COLOR_PICKER_CLASS)).toHaveClass(
				'hovered'
			);
		});

		it('shows the Value From Stylebook button as selected', async () => {
			renderColorPicker();

			await userEvent.click(
				screen.getByLabelText('select-color.-color-Green-is-selected')
			);

			expect(screen.getByText('value-from-stylebook')).toHaveAttribute(
				'aria-selected',
				'true'
			);
			expect(screen.getByText('custom')).toHaveAttribute(
				'aria-selected',
				'false'
			);
		});

		it('change to input color picker when detach token button is clicked', async () => {
			const {baseElement, getByLabelText, getByText, getByTitle} =
				renderColorPicker();

			await userEvent.click(getByTitle('detach-style'));
			await userEvent.click(getByLabelText('select-color'));
			await userEvent.click(getByText('value-from-stylebook'));

			expect(baseElement.querySelector('input')).toHaveValue('9BE169');
			expect(
				baseElement.querySelector('.clay-color-picker')
			).toBeInTheDocument();
		});

		it('does not show the Value From Stylebook button when the value is inherited', async () => {
			renderColorPicker({
				field: {...FIELD, inherited: true},
				value: '',
			});

			expect(
				screen.queryByText('select-a-color')
			).not.toBeInTheDocument();
			expect(screen.getByTitle('inherited-value')).toBeInTheDocument();
		});

		it('disabled the color when the token references itself', async () => {
			const {getByLabelText, getByText, getByTitle} = renderColorPicker({
				field: {...FIELD, name: 'orange'},
				value: '#fff',
			});

			await userEvent.click(getByLabelText('select-color'));
			await userEvent.click(getByText('value-from-stylebook'));

			expect(getByTitle('Orange')).toBeDisabled();
		});

		it('disables the colors when the tokens are mutually referenced', async () => {
			const {getByLabelText, getByText, getByTitle} = renderColorPicker({
				editedTokenValues: {
					orange: {
						name: 'blue',
						value: '#ffb46e',
					},
				},
				field: {...FIELD, name: 'blue'},
				value: '#fff',
			});

			await userEvent.click(getByLabelText('select-color'));
			await userEvent.click(getByText('value-from-stylebook'));

			expect(getByTitle('Orange')).toBeDisabled();
			expect(getByTitle('Blue')).toBeDisabled();
		});
	});

	describe('When the value is an hexadecimal', () => {
		it('renders the autocomplete color picker', () => {
			const {baseElement} = renderColorPicker({
				value: '#ffb46e',
			});

			expect(baseElement.querySelector('input')).toHaveValue('FFB46E');
			expect(
				baseElement.querySelector('.clay-color-picker')
			).toBeInTheDocument();
		});

		it('shows the Custom button as selected', async () => {
			renderColorPicker({
				value: '#ffb46e',
			});

			await userEvent.click(screen.getByLabelText('select-color'));

			expect(screen.getByText('value-from-stylebook')).toHaveAttribute(
				'aria-selected',
				'false'
			);
			expect(screen.getByText('custom')).toHaveAttribute(
				'aria-selected',
				'true'
			);
		});

		it('changes to dropdown color picker and focus it when value from stylebook button is clicked', async () => {
			const {getByLabelText, getByText, getByTitle} = renderColorPicker({
				value: '#fff',
			});

			await userEvent.click(getByLabelText('select-color'));
			await userEvent.click(getByText('value-from-stylebook'));
			await userEvent.click(getByTitle('Blue'));

			expect(getByTitle('detach-style')).toBeInTheDocument();
			expect(
				getByLabelText('select-color.-color-Blue-is-selected')
			).toBeInTheDocument();
		});

		it('sets a token if the written value is an existing token', async () => {
			const {baseElement, getByLabelText, getByTitle} = renderColorPicker(
				{
					value: '#fff',
				}
			);

			await onTypeValue(baseElement.querySelector('input')!, 'green');

			expect(getByTitle('detach-style')).toBeInTheDocument();
			expect(
				getByLabelText('select-color.-color-Green-is-selected')
			).toBeInTheDocument();
		});

		it('sets the previous value when the input value is removed', async () => {
			const {baseElement} = renderColorPicker({
				value: '#444444',
			});
			const input = baseElement.querySelector('input')!;

			await onTypeValue(input, '');

			expect(input).toHaveValue('444444');
		});

		it('sets the previous value when the input value is an invalid hexcolor', async () => {
			const {baseElement} = renderColorPicker({
				value: '#444444',
			});
			const input = baseElement.querySelector('input')!;

			await onTypeValue(input, '#44');

			expect(input).toHaveValue('444444');
		});

		it('takes a 6-digit hexcolor if the input value has 7 digits', async () => {
			const {baseElement} = renderColorPicker({
				value: '#444444',
			});
			const input = baseElement.querySelector('input')!;

			await onTypeValue(input, '123456A');

			expect(input).toHaveValue('123456');
		});

		it('takes an 8-digit hexcolor', async () => {
			const {baseElement} = renderColorPicker({
				value: '#44444444',
			});
			const input = baseElement.querySelector('input')!;

			await onTypeValue(input, '#AABBCCDD');

			expect(input).toHaveValue('AABBCCDD');
		});

		it('takes an 8-digit hexcolor even if the input value has more digits', async () => {
			const {baseElement} = renderColorPicker({
				value: '#44444444',
			});
			const input = baseElement.querySelector('input')!;

			await onTypeValue(input, '#55555555555');

			expect(input).toHaveValue('55555555');
		});

		it('converts the 3-digit hexcolor to a 6-digit hexcolor', async () => {
			const {baseElement} = renderColorPicker({
				value: '#444444',
			});
			const input = baseElement.querySelector('input')!;

			await onTypeValue(input, '#abc');

			expect(input).toHaveValue('AABBCC');
		});

		it('converts the 4-digit hexcolor to an 8-digit hexcolor', async () => {
			const {baseElement} = renderColorPicker({
				value: '#444444',
			});
			const input = baseElement.querySelector('input')!;

			await onTypeValue(input, '#abcd');

			expect(input).toHaveValue('AABBCCDD');
		});

		describe('Input errors', () => {
			it('restores previous value when the written token does not exist', async () => {
				const {baseElement} = renderColorPicker({
					value: '#FFF',
				});

				const input = baseElement.querySelector('input')!;

				await onTypeValue(input, 'prim');

				expect(input).toHaveValue('FFF');
			});

			it('clears an error when the clear selection button is clicked', async () => {
				const {baseElement, getByTitle, queryByText} =
					renderColorPicker({
						field: {...FIELD, name: 'orange'},
						value: '#fff',
					});

				await onTypeValue(
					baseElement.querySelector('input')!,
					'orange'
				);

				await userEvent.click(getByTitle('clear-selection'));

				expect(
					queryByText('tokens-cannot-reference-itself')
				).not.toBeInTheDocument();
			});

			it('renders an error when the written token is the same that the name field', async () => {
				const {baseElement, getByText} = renderColorPicker({
					field: {...FIELD, name: 'orange'},
					value: '#fff',
				});

				await onTypeValue(
					baseElement.querySelector('input')!,
					'orange'
				);

				expect(
					getByText('tokens-cannot-reference-itself')
				).toBeInTheDocument();
			});

			it('renders an error when two tokens are mutually referenced', async () => {
				const {baseElement, getByText} = renderColorPicker({
					editedTokenValues: {
						blue: {
							name: 'orange',
							value: '#ffb46e',
						},
					},
					field: {...FIELD, name: 'orange'},
					value: '#fff',
				});

				await onTypeValue(baseElement.querySelector('input')!, 'blue');

				expect(
					getByText('tokens-cannot-be-mutually-referenced')
				).toBeInTheDocument();
			});
		});
	});

	describe('When typing in the Editor hex input popup', () => {
		it('calls onValueSelect with # prefix when a hex value is typed', async () => {
			const onValueSelect = jest.fn();
			const {getByLabelText} = renderColorPicker({
				onValueSelect,
				value: '#ffb46e',
			});

			await userEvent.click(getByLabelText('select-color'));

			const hexInput = screen.getByTestId('customHexInput');

			fireEvent.change(hexInput, {target: {value: 'FF0000'}});
			fireEvent.blur(hexInput);

			await waitFor(() => {
				expect(onValueSelect).toHaveBeenCalledWith(
					INPUT_NAME,
					'#FF0000'
				);
			});
		});

		it('calls onValueSelect with # prefix when a color name is typed', async () => {
			const onValueSelect = jest.fn();
			const {getByLabelText} = renderColorPicker({
				onValueSelect,
				value: '#ffb46e',
			});

			await userEvent.click(getByLabelText('select-color'));

			const hexInput = screen.getByTestId('customHexInput');

			fireEvent.change(hexInput, {target: {value: 'red'}});
			fireEvent.blur(hexInput);

			await waitFor(() => {
				expect(onValueSelect).toHaveBeenCalledWith(
					INPUT_NAME,
					'#FF0000'
				);
			});
		});

		it('calls onValueSelect without double # prefix when value already has #', async () => {
			const onValueSelect = jest.fn();
			const {getByLabelText} = renderColorPicker({
				onValueSelect,
				value: '#ffb46e',
			});

			await userEvent.click(getByLabelText('select-color'));

			const hexInput = screen.getByTestId('customHexInput');

			fireEvent.change(hexInput, {target: {value: '#FF0000'}});
			fireEvent.blur(hexInput);

			await waitFor(() => {
				expect(onValueSelect).toHaveBeenCalledWith(
					INPUT_NAME,
					'#FF0000'
				);
			});
		});

		it('calls onValueSelect with # prefix when an 8-digit hex value is typed', async () => {
			const onValueSelect = jest.fn();
			const {getByLabelText} = renderColorPicker({
				onValueSelect,
				value: '#ffb46e',
			});

			await userEvent.click(getByLabelText('select-color'));

			const hexInput = screen.getByTestId('customHexInput');

			fireEvent.change(hexInput, {target: {value: 'FF000080'}});
			fireEvent.blur(hexInput);

			await waitFor(() => {
				expect(onValueSelect).toHaveBeenCalledWith(
					INPUT_NAME,
					'#FF000080'
				);
			});
		});

		it('does not call onValueSelect with invalid hex value', async () => {
			const onValueSelect = jest.fn();
			const {getByLabelText} = renderColorPicker({
				onValueSelect,
				value: '#ffb46e',
			});

			await userEvent.click(getByLabelText('select-color'));

			const hexInput = screen.getByTestId('customHexInput');

			fireEvent.change(hexInput, {target: {value: 'ZZZZZZ'}});
			fireEvent.blur(hexInput);

			await waitFor(() => {
				expect(onValueSelect).not.toHaveBeenCalledWith();
			});
		});
	});

	describe('When the value is a CSS color', () => {
		it('ensures that when a CSS color color longer than 9 characters is typed, the color remains unchanged', async () => {
			const {baseElement} = renderColorPicker({
				value: '#ffb46e',
			});

			await onTypeValue(baseElement.querySelector('input')!, 'aliceblue');

			expect(baseElement.querySelector('input')).toHaveValue('aliceblue');
		});

		it('accepts a light-dark() value', async () => {
			const onValueSelect = jest.fn();

			const {baseElement} = renderColorPicker({
				onValueSelect,
				value: '#ffb46e',
			});

			const input = baseElement.querySelector('input')!;

			fireEvent.change(input, {
				target: {value: 'light-dark(#fff, #000)'},
			});
			fireEvent.blur(input);

			expect(input).toHaveValue('light-dark(#fff, #000)');
			expect(onValueSelect).toHaveBeenCalledWith(
				INPUT_NAME,
				'light-dark(#fff, #000)'
			);
		});
	});

	describe('Filter a Value from Stylebook', () => {
		const goToStylebookTab = async () => {
			await userEvent.click(
				screen.getByLabelText('select-color.-color-Green-is-selected')
			);
			await userEvent.click(screen.getByText('value-from-stylebook'));
		};

		it('filters by category', async () => {
			renderColorPicker();

			await goToStylebookTab();

			const searchForm = screen.getByLabelText('search-form');

			await onTypeValue(searchForm as HTMLInputElement, 'Category 2');

			await waitFor(() => {
				expect(
					screen.queryByText('Category 1')
				).not.toBeInTheDocument();
				expect(screen.queryByText('Category 2')).toBeInTheDocument();
			});
		});

		it('filters by tokenSet', async () => {
			renderColorPicker();

			await goToStylebookTab();

			const searchForm = screen.getByLabelText('search-form');

			await onTypeValue(searchForm as HTMLInputElement, 'tokenset 2');

			await waitFor(() => {
				expect(
					screen.queryByText('Category 1')
				).not.toBeInTheDocument();
				expect(
					screen.queryByText('TokenSet 1')
				).not.toBeInTheDocument();
				expect(screen.queryByText('Category 2')).toBeInTheDocument();
				expect(screen.queryByText('TokenSet 2')).toBeInTheDocument();
			});
		});

		it('filters by color', async () => {
			renderColorPicker();

			await goToStylebookTab();

			const searchForm = screen.getByLabelText('search-form');

			await onTypeValue(searchForm as HTMLInputElement, 'dark blue');

			await waitFor(() => {
				expect(screen.queryByText('Category 1')).toBeInTheDocument();
				expect(screen.queryByText('TokenSet 1')).toBeInTheDocument();
				expect(screen.queryByTitle('Dark Blue')).toBeInTheDocument();
				expect(
					screen.queryByText('Category 2')
				).not.toBeInTheDocument();
				expect(
					screen.queryByText('TokenSet 2')
				).not.toBeInTheDocument();
				expect(screen.queryByTitle('Green')).not.toBeInTheDocument();
			});
		});

		it('shows empty results', async () => {
			renderColorPicker();

			await goToStylebookTab();

			const searchForm = screen.getByLabelText('search-form');

			await onTypeValue(searchForm as HTMLInputElement, 'Color 123');

			const noResultsMessage =
				await screen.findByText('no-results-found');

			expect(noResultsMessage).toBeInTheDocument();
		});
	});
});
