/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import CartItemsList from '../../../src/main/resources/META-INF/resources/components/mini_cart/CartItemsList';
import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';
import {
	ADD_PRODUCT,
	ITEM,
	ITEMS_LIST,
	ITEMS_LIST_ACTIONS,
	SUMMARY,
} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';
import {DEFAULT_LABELS} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/labels';

describe.skip('MiniCart Items List', () => {
	const BASE_CONTEXT_MOCK = {
		CartViews: {
			[ITEMS_LIST_ACTIONS]: () => <div>{ITEMS_LIST_ACTIONS}</div>,
		},
		cartState: {},
		labels: DEFAULT_LABELS,
	};

	const COMPONENT_SELECTOR = '.mini-cart-items-list';

	afterEach(() => {
		jest.resetAllMocks();

		cleanup();
	});

	describe('by default', () => {
		it(
			`renders the ${ITEMS_LIST} component w/ ` +
				`the ${ITEMS_LIST_ACTIONS} and a div including ` +
				`a shopping cart icon and the label "${ADD_PRODUCT}"`,
			() => {
				const EMPTY_CART_SELECTOR = '.empty-cart';

				const {container, getByText} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItemsList />
					</MiniCartContext.Provider>
				);

				expect(getByText(ITEMS_LIST_ACTIONS)).toBeInTheDocument();

				const CartItemsListElement =
					container.querySelector(COMPONENT_SELECTOR);

				const EmptyCartElement =
					CartItemsListElement.querySelector(EMPTY_CART_SELECTOR);

				expect(CartItemsListElement).toBeInTheDocument();
				expect(EmptyCartElement).toBeInTheDocument();

				expect(
					getByText(BASE_CONTEXT_MOCK.labels[ADD_PRODUCT])
				).toBeInTheDocument();

				expect(CartItemsListElement.innerHTML).toMatchSnapshot();
			}
		);
	});

	describe('by data flow', () => {
		describe('if there are cart items', () => {
			const WITH_ITEMS_CONTEXT_MOCK = {
				...BASE_CONTEXT_MOCK,
				CartViews: {
					...BASE_CONTEXT_MOCK.CartViews,
					[ITEM]: (props) => <div>{props.item.name}</div>,
					[SUMMARY]: ({dataMapper, isLoading, summaryData}) => {
						dataMapper();

						return (
							<div>
								{isLoading
									? `${SUMMARY} is loading`
									: `${SUMMARY} ${summaryData.itemsQuantity}`}
							</div>
						);
					},
				},
				cartState: {
					cartItems: [
						{
							id: 1,
							name: 'An Item',
						},
					],
					summary: {
						itemsQuantity: 1,
					},
				},
				isUpdating: false,
				labels: DEFAULT_LABELS,
				summaryDataMapper: jest.fn(),
			};

			it(
				`renders the ${ITEMS_LIST} component displaying ` +
					`the ${ITEMS_LIST_ACTIONS}, a div including ` +
					`the list of cart items and the order ${SUMMARY}`,
				() => {
					const {container, getByText} = render(
						<MiniCartContext.Provider
							value={WITH_ITEMS_CONTEXT_MOCK}
						>
							<CartItemsList />
						</MiniCartContext.Provider>
					);

					expect(getByText(ITEMS_LIST_ACTIONS)).toBeInTheDocument();

					const CartItemsListElement =
						container.querySelector(COMPONENT_SELECTOR);
					const CartItemElements =
						CartItemsListElement.querySelectorAll(
							'.mini-cart-cart-items div'
						);

					expect(CartItemElements.length).toEqual(1);
					expect(CartItemElements[0].innerHTML).toEqual(
						WITH_ITEMS_CONTEXT_MOCK.cartState.cartItems[0].name
					);

					expect(
						getByText(
							`${SUMMARY} ${WITH_ITEMS_CONTEXT_MOCK.cartState.summary.itemsQuantity}`
						)
					).toBeInTheDocument();
				}
			);

			it(`if the cart is updating, the ${SUMMARY} will be informed`, () => {
				const {getByText} = render(
					<MiniCartContext.Provider
						value={{
							...WITH_ITEMS_CONTEXT_MOCK,
							isUpdating: true,
						}}
					>
						<CartItemsList />
					</MiniCartContext.Provider>
				);

				expect(getByText(`${SUMMARY} is loading`)).toBeInTheDocument();
			});
		});
	});
});
