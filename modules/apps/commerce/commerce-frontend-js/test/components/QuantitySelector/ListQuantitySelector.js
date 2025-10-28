/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import ListQuantitySelector from '../../../src/main/resources/META-INF/resources/components/quantity_selector/ListQuantitySelector';

const defaultProps = {
	allowedQuantities: [1, 5, 10, 300],
	disabled: false,
	max: 9999,
	min: 1,
	name: 'test-name',
	quantity: 1,
	size: 'md',
	step: 1,
};

describe('Quantity Selector', () => {
	let listQuantitySelector;
	let select;
	let onUpdate;
	let options;

	beforeEach(() => {
		onUpdate = jest.fn();

		listQuantitySelector = render(
			<ListQuantitySelector {...defaultProps} onUpdate={onUpdate} />
		);

		options = listQuantitySelector.container.querySelectorAll('option');
		select = listQuantitySelector.container.querySelector('select');
	});

	afterEach(() => {
		cleanup();
	});

	it('must set a consistent value to the select', () => {
		expect(select.value).toBe(defaultProps.quantity.toString());

		listQuantitySelector.rerender(
			<ListQuantitySelector {...defaultProps} quantity={5} />
		);

		expect(select.value).toBe('5');
	});

	it('must render a consistent amount of options', () => {
		expect(options).toHaveLength(defaultProps.allowedQuantities.length);
	});

	it('must be consistently disabled', () => {
		expect(select.disabled).toBe(false);

		listQuantitySelector.rerender(
			<ListQuantitySelector {...defaultProps} disabled={true} />
		);

		expect(select.disabled).toBe(true);
	});

	it('must have the passed name', () => {
		expect(select.name).toBe(defaultProps.name);
	});

	it('must pass the updated value via callback', () => {
		fireEvent.change(select, {target: {value: '10'}});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: [],
			value: 10,
		});

		fireEvent.change(select, {target: {value: '300'}});

		expect(onUpdate).toHaveBeenLastCalledWith({
			errors: [],
			value: 300,
		});
	});
});
