/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import InputQuantitySelector from '../../../src/main/resources/META-INF/resources/components/quantity_selector/InputQuantitySelector';

const props = {
	allowedQuantities: [],
	disabled: false,
	max: 9999,
	min: 1,
	name: 'test-name',
	quantity: 1,
	size: 'md',
	step: 1,
};

describe('Quantity Selector', () => {
	let formGroup;
	let input;
	let inputQuantitySelector;
	let onUpdate;
	const defaultProps = {...props};

	beforeEach(() => {
		onUpdate = jest.fn();

		defaultProps.onUpdate = onUpdate;

		inputQuantitySelector = render(
			<InputQuantitySelector {...defaultProps} />
		);

		formGroup =
			inputQuantitySelector.container.querySelector('.form-group');
		input = inputQuantitySelector.container.querySelector('input');
	});

	afterEach(() => {
		cleanup();
	});

	it('must set a consistent value to the select', () => {
		expect(input.value).toBe(defaultProps.quantity.toString());
	});

	it('must be consistently disabled', () => {
		expect(input.disabled).toBe(false);

		inputQuantitySelector.rerender(
			<InputQuantitySelector {...defaultProps} disabled={true} />
		);

		expect(input.disabled).toBe(true);
	});

	it('must have the passed name', () => {
		expect(input.name).toBe(defaultProps.name);
	});

	it('must pass the updated value via callback', () => {
		act(() => {
			fireEvent.change(input, {target: {value: '10'}});
		});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: [],
			value: 10,
		});

		act(() => {
			fireEvent.change(input, {target: {value: '300'}});
		});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: [],
			value: 300,
		});
	});

	it('must handle the product configuration: minQuantity', () => {
		inputQuantitySelector = render(
			<InputQuantitySelector min={4} onUpdate={onUpdate} />
		);

		input = inputQuantitySelector.container.querySelector('input');

		expect(input.min).toBe('4');

		act(() => {
			fireEvent.change(input, {target: {value: '0'}});
		});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: ['min'],
			value: 0,
		});

		act(() => {
			fireEvent.change(input, {target: {value: '6'}});
		});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: [],
			value: 6,
		});
	});

	it('must show a feedback when the typed quantity is not correct: minQuantity', () => {
		inputQuantitySelector = render(
			<InputQuantitySelector min={4} quantity={1} />
		);

		formGroup =
			inputQuantitySelector.container.querySelector('.form-group');

		expect(formGroup.classList).toContain('has-error');
	});

	it('must handle the product configuration: maxQuantity', () => {
		inputQuantitySelector = render(
			<InputQuantitySelector max={4} onUpdate={onUpdate} />
		);

		input = inputQuantitySelector.container.querySelector('input');

		act(() => {
			fireEvent.change(input, {target: {value: '6'}});
		});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: ['max'],
			value: 6,
		});

		act(() => {
			fireEvent.change(input, {target: {value: '4'}});
		});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: [],
			value: 4,
		});
	});

	it('must show a feedback when the typed quantity is not correct: maxQuantity', () => {
		inputQuantitySelector = render(
			<InputQuantitySelector max={4} quantity={7} />
		);

		formGroup =
			inputQuantitySelector.container.querySelector('.form-group');

		expect(formGroup.classList).toContain('has-error');
	});

	it('must handle the product configuration: multipleQuantity', async () => {
		inputQuantitySelector = render(
			<InputQuantitySelector
				alignment="top"
				max={54}
				min={6}
				onUpdate={onUpdate}
				step={5}
			/>
		);

		input = inputQuantitySelector.container.querySelector('input');

		expect(input.min).toBe('10');
		expect(input.max).toBe('50');

		act(() => {
			fireEvent.change(input, {target: {value: '7'}});
		});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: ['min', 'multiple'],
			value: 7,
		});
	});

	it('must show a feedback when the typed quantity is not correct: multipleQuantity', () => {
		inputQuantitySelector = render(
			<InputQuantitySelector quantity={7} step={4} />
		);

		formGroup =
			inputQuantitySelector.container.querySelector('.form-group');

		expect(formGroup.classList).toContain('has-error');
	});
});
