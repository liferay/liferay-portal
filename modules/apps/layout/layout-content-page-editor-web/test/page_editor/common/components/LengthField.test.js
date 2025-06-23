/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {StoreAPIContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import {LengthField} from '../../../../src/main/resources/META-INF/resources/page_editor/common/components/LengthField';

const FIELD = {label: 'length-field', name: 'lengthField'};

const renderLengthField = ({
	onValueSelect = () => {},
	value = '12px',
	field = FIELD,
	selectedViewportSize = 'desktop',
} = {}) =>
	render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				selectedViewportSize,
			})}
		>
			<LengthField
				field={field}
				onValueSelect={onValueSelect}
				value={value}
			/>
		</StoreAPIContextProvider>
	);

describe('LengthField', () => {
	async function openUnitDropdown() {

		// Hackily work around:
		//
		//      "TypeError: Cannot read property '_defaultView' of undefined"
		//
		// Caused by: https://github.com/jsdom/jsdom/issues/2499

		await userEvent.click(screen.getByLabelText('select-a-unit'));

		document.activeElement.blur = () => {};
	}

	it('renders LengthField', () => {
		renderLengthField();

		expect(screen.getByText('length-field')).toBeInTheDocument();
	});

	it('shows the value', () => {
		renderLengthField();

		expect(screen.getByLabelText('length-field')).toHaveValue(12);
		expect(screen.getByLabelText('select-a-unit').textContent).toBe('PX');
	});

	it('changes the number of the value', async () => {
		renderLengthField();
		const input = screen.getByLabelText('length-field');

		await userEvent.clear(input);
		await userEvent.type(input, '20');

		expect(input).toHaveValue(20);
	});

	it('saves the value', async () => {
		const onValueSelect = jest.fn();
		renderLengthField({onValueSelect});
		const input = screen.getByLabelText('length-field');

		await userEvent.clear(input);
		await userEvent.type(input, '24');
		fireEvent.blur(input);

		expect(onValueSelect).toBeCalledWith(FIELD.name, '24px');
	});

	it('saves the value when the Enter button is pressed', async () => {
		const onValueSelect = jest.fn();
		renderLengthField({onValueSelect});
		const input = screen.getByLabelText('length-field');

		await userEvent.clear(input);
		await userEvent.type(input, '30');
		fireEvent.keyUp(input, {key: 'Enter'});

		expect(onValueSelect).toBeCalledWith(FIELD.name, '30px');
	});

	it('changes the unit of the value', async () => {
		renderLengthField();

		await openUnitDropdown();

		await userEvent.click(screen.getByText('%'));

		expect(screen.getByLabelText('select-a-unit').textContent).toBe('%');
	});

	it('keeps the empty input and the units when the value is cleared', async () => {
		renderLengthField({value: '14vh'});
		const input = screen.getByLabelText('length-field');

		await userEvent.clear(input);

		expect(input).toHaveValue(null);
		expect(screen.getByLabelText('select-a-unit').textContent).toBe('VH');
	});

	it('renders an icon code in the button if custom option is selected', () => {
		renderLengthField({value: 'calc(12px - 3px)'});

		expect(
			screen
				.getByLabelText('select-a-unit')
				.querySelector('.lexicon-icon-code')
		).toBeInTheDocument();
	});

	it('focuses the input when custom option is selected', async () => {
		renderLengthField();

		await openUnitDropdown();

		await userEvent.click(screen.getByText('CUSTOM'));

		expect(screen.getByLabelText('length-field')).toHaveFocus();
	});

	it('does not allow typing letters when a unit is selected', async () => {
		renderLengthField();
		const input = screen.getByLabelText('length-field');

		await userEvent.clear(input);
		await userEvent.type(input, 'auto');

		expect(input).toHaveValue(null);
	});

	it('allows a default unit and disables the button', () => {
		const field = {
			...FIELD,
			typeOptions: {
				defaultUnit: '%',
			},
		};

		renderLengthField({field});

		const button = screen.getByLabelText('select-a-unit');

		expect(button.textContent).toBe('%');
		expect(button).toBeDisabled();
	});

	it('renders the restore button when a value is introduced', async () => {
		renderLengthField({
			field: {defaultValue: '', label: 'opacity', name: 'opacity'},
			value: '',
		});
		const input = screen.getByLabelText('opacity');

		expect(screen.queryByTitle('reset-to-x-value')).not.toBeInTheDocument();

		await userEvent.clear(input);
		await userEvent.type(input, '100');
		fireEvent.blur(input);

		expect(
			screen.queryByTitle('reset-to-initial-value')
		).toBeInTheDocument();
	});

	it('clears the value when the restore button is clicked', async () => {
		renderLengthField({field: {label: 'opacity', name: 'opacity'}});

		await userEvent.click(screen.getByTitle('reset-to-initial-value'));

		expect(screen.getByLabelText('opacity').textContent).toBe('');
	});

	describe('LengthField when it is part of a Select field', () => {
		const field = {
			...FIELD,
			typeOptions: {
				showLengthField: true,
			},
		};

		it('focuses the input when the currently option is custom and a other unit is selected', async () => {
			renderLengthField({field, value: 'calc(12px - 3px)'});

			await openUnitDropdown();

			await userEvent.click(screen.getByText('%'));

			expect(screen.getByLabelText('length-field')).toHaveFocus();
		});

		it('does not save the value and keeps the previous value when the input is cleared', async () => {
			const onValueSelect = jest.fn();
			renderLengthField({
				field,
				onValueSelect,
				value: 'initial',
			});
			const input = screen.getByLabelText('length-field');

			await userEvent.clear(input);
			fireEvent.blur(input);

			expect(input).toHaveValue('initial');
			expect(onValueSelect).not.toBeCalled();
		});
	});
});
