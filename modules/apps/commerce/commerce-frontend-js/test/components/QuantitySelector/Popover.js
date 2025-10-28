/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
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

describe('Quantity Selector - Popover', () => {
	let inputQuantitySelector;
	let input;
	let onUpdate;
	const defaultProps = {...props};

	beforeEach(() => {
		onUpdate = jest.fn();

		defaultProps.onUpdate = onUpdate;
	});

	afterEach(() => {
		cleanup();
	});

	it('must show a popover when the min quantity is 0 and the current value is < 0 but not for min error', () => {
		inputQuantitySelector = render(
			<InputQuantitySelector {...defaultProps} min={0} quantity={-1} />
		);

		act(() => {
			input = inputQuantitySelector.container.querySelector('input');

			fireEvent.focus(input);
		});

		act(() => {
			const popover = document.querySelector('.popover');

			expect(popover).toBeInTheDocument();

			expect(
				screen.queryByText(/min-quantity-per-order-is/)
			).toBeInTheDocument();
		});
	});

	it('must show a popover when the min quantity is > 0 and the current value is <= 1', () => {
		inputQuantitySelector = render(
			<InputQuantitySelector {...defaultProps} min={4} quantity={1} />
		);

		act(() => {
			input = inputQuantitySelector.container.querySelector('input');

			fireEvent.focus(input);
		});

		act(() => {
			const popover = document.querySelector('.popover');

			expect(popover).toBeInTheDocument();

			expect(
				screen.getByText(/min-quantity-per-order-is/)
			).toBeInTheDocument();
		});
	});

	it("must show a popover when the max quantity is defined and the current value doesn't apply", () => {
		inputQuantitySelector = render(
			<InputQuantitySelector {...defaultProps} max={5} quantity={7} />
		);

		act(() => {
			input = inputQuantitySelector.container.querySelector('input');

			fireEvent.focus(input);
		});

		act(() => {
			const popover = document.querySelector('.popover');

			expect(popover).toBeInTheDocument();

			expect(
				screen.getByText(/max-quantity-per-order-is-/)
			).toBeInTheDocument();
		});
	});

	it("must show a popover when the multiple quantity is > 1 and the current value doesn't apply", () => {
		inputQuantitySelector = render(
			<InputQuantitySelector {...defaultProps} quantity={3} step={4} />
		);

		act(() => {
			input = inputQuantitySelector.container.querySelector('input');

			fireEvent.focus(input);
		});

		act(() => {
			const popover = document.querySelector('.popover');

			expect(popover).toBeInTheDocument();

			expect(
				screen.getByText(/quantity-must-be-a-multiple-of-/)
			).toBeInTheDocument();
		});
	});

	it("must inform the user when multiple constrains don't apply to the current value - min, multiple", () => {
		inputQuantitySelector = render(
			<InputQuantitySelector
				{...defaultProps}
				max={60}
				min={5}
				quantity={3}
				step={4}
			/>
		);

		act(() => {
			input = inputQuantitySelector.container.querySelector('input');

			fireEvent.focus(input);
		});

		act(() => {
			const popover = document.querySelector('.popover');

			expect(popover).toBeInTheDocument();

			const maxMessage = screen.getByText(/max-quantity-per-order-is-/);
			const minMessage = screen.getByText(/min-quantity-per-order-is/);
			const multipleMessage = screen.getByText(
				/quantity-must-be-a-multiple-of-/
			);

			expect(maxMessage).toBeInTheDocument();
			expect(maxMessage.classList).not.toContain('text-danger');

			expect(multipleMessage).toBeInTheDocument();
			expect(multipleMessage.classList).toContain('text-danger');

			expect(minMessage).toBeInTheDocument();
			expect(minMessage.classList).toContain('text-danger');
		});
	});

	it("must inform the user when multiple constrains don't apply to the current value - max, multiple", () => {
		inputQuantitySelector = render(
			<InputQuantitySelector max={60} min={5} quantity={61} step={4} />
		);

		act(() => {
			input = inputQuantitySelector.container.querySelector('input');

			fireEvent.focus(input);
		});

		act(() => {
			const popover = document.querySelector('.popover');

			expect(popover).toBeInTheDocument();

			const maxMessage = screen.getByText(/max-quantity-per-order-is-/);
			const minMessage = screen.getByText(/min-quantity-per-order-is/);
			const multipleMessage = screen.getByText(
				/quantity-must-be-a-multiple-of-/
			);

			expect(maxMessage).toBeInTheDocument();
			expect(maxMessage.classList).toContain('text-danger');

			expect(multipleMessage).toBeInTheDocument();
			expect(multipleMessage.classList).toContain('text-danger');

			expect(minMessage).toBeInTheDocument();
			expect(minMessage.classList).not.toContain('text-danger');
		});
	});
});
