/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, fireEvent, render, wait} from '@testing-library/react';
import React from 'react';

import Header from '../../../src/main/resources/META-INF/resources/components/mini_cart/Header';
import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';
import {
	ORDER_IS_EMPTY,
	YOUR_ORDER,
} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';
import {DEFAULT_LABELS} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/labels';

describe.skip('MiniCart Header', () => {
	const BASE_CONTEXT_MOCK = {
		cartState: {},
		closeCart: jest.fn(),
		labels: DEFAULT_LABELS,
		toggleable: true,
	};

	afterEach(() => {
		cleanup();
	});

	describe('by default', () => {
		it('renders the MiniCart header which includes a dynamic title and a button to close the cart', async () => {
			const {container} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<Header />
				</MiniCartContext.Provider>
			);

			const headerWrapper = container.querySelector('.mini-cart-header');
			const headerTitle = headerWrapper.querySelector(
				'.mini-cart-header-title'
			);
			const closeButton = headerWrapper.querySelector('.mini-cart-close');

			expect(headerWrapper).toBeInTheDocument();
			expect(headerTitle).toBeInTheDocument();
			expect(closeButton).toBeInTheDocument();

			await act(async () => {
				fireEvent.click(closeButton);
			});

			await wait(() => {
				expect(BASE_CONTEXT_MOCK.closeCart).toHaveBeenCalled();
				expect(headerWrapper).toMatchSnapshot();
			});
		});
	});

	describe('by data flow', () => {
		it(`if there are no cart items, the header title shows the label "${ORDER_IS_EMPTY}"`, async () => {
			const {getByText} = render(
				<MiniCartContext.Provider
					value={{
						...BASE_CONTEXT_MOCK,
						...{
							cartState: {
								cartItems: [],
							},
						},
					}}
				>
					<Header />
				</MiniCartContext.Provider>
			);

			expect(
				getByText(DEFAULT_LABELS[ORDER_IS_EMPTY])
			).toBeInTheDocument();

			try {
				expect(getByText(DEFAULT_LABELS[YOUR_ORDER])).toThrow();
			}
			catch (_ignore) {}
		});

		it(`if there are cart items, the header title shows the label "${YOUR_ORDER}"`, async () => {
			const {getByText} = render(
				<MiniCartContext.Provider
					value={{
						...BASE_CONTEXT_MOCK,
						...{
							cartState: {
								cartItems: [{id: 1}],
							},
						},
					}}
				>
					<Header />
				</MiniCartContext.Provider>
			);

			expect(getByText(DEFAULT_LABELS[YOUR_ORDER])).toBeInTheDocument();

			try {
				expect(getByText(DEFAULT_LABELS[ORDER_IS_EMPTY])).toThrow();
			}
			catch (_ignore) {}
		});

		it('if MiniCart is not toggleable, will not render the close button', () => {
			const {asFragment, container} = render(
				<MiniCartContext.Provider
					value={{
						...BASE_CONTEXT_MOCK,
						toggleable: false,
					}}
				>
					<Header />
				</MiniCartContext.Provider>
			);

			expect(container.querySelector('button')).not.toBeInTheDocument();
			expect(asFragment()).toMatchSnapshot();
		});
	});
});
