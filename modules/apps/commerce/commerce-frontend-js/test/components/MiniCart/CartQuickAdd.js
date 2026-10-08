/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import CartQuickAdd from '../../../src/main/resources/META-INF/resources/components/mini_cart/CartQuickAdd';
import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';
import {getRequestBody} from '../../tests_utilities';
import {mockCartItem} from '../fixtures/cartFixtures';
import {
	BASE_CHANNEL,
	mockChannelProduct,
	mockChannelProductSku,
	mockDiscontinuedChannelProductSku,
} from '../fixtures/productFixtures';

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openToast: jest.fn(),
}));

const ACCOUNT_ID = 43879;

const quickAdd = async (...skus) => {
	for (const sku of skus) {
		await search(sku);

		await userEvent.click(
			await screen.findByRole('menuitem', {name: new RegExp(sku)})
		);
	}

	await userEvent.click(screen.getByRole('button', {name: 'add-to-cart'}));
};

const renderCartQuickAdd = ({cartItems = [], products = []} = {}) => {
	fetch.mockResponse(async ({method, url}) => {
		if (url.includes('/products')) {
			return JSON.stringify({
				items: products,
				lastPage: 1,
				page: 1,
				pageSize: 100,
				totalCount: products.length,
			});
		}

		if (method === 'POST') {
			return JSON.stringify({});
		}

		if (method === 'PATCH') {
			return JSON.stringify({cartItems: []});
		}

		return JSON.stringify({cartItems});
	});

	return render(
		<MiniCartContext.Provider
			value={{
				cartState: {
					accountId: ACCOUNT_ID,
					cartItems,
					channel: {channel: BASE_CHANNEL},
					id: 43882,
				},
				guestOrderEnabled: false,
			}}
		>
			<CartQuickAdd />
		</MiniCartContext.Provider>
	);
};

const search = async (text) => {
	await userEvent.type(screen.getByRole('combobox'), text);

	await act(async () => {
		jest.advanceTimersByTime(500);
	});
};

describe('MiniCart CartQuickAdd', () => {
	beforeEach(() => {
		jest.useFakeTimers();

		Liferay.CommerceContext = {showSeparateOrderItems: false};
	});

	afterEach(() => {
		jest.useRealTimers();

		delete Liferay.CommerceContext;
	});

	describe('search', () => {
		it('searches the products of the channel for the current account', async () => {
			renderCartQuickAdd({
				products: [
					mockChannelProduct({
						skus: [mockChannelProductSku({sku: 'MIN93016A'})],
					}),
				],
			});

			await search('MIN93016A');

			expect(
				await screen.findByRole('menuitem', {name: /MIN93016A/})
			).toBeInTheDocument();

			const [searchURLString] = fetch.mock.calls.findLast(([url]) =>
				url.includes('/products')
			);

			expect(searchURLString).toContain(
				'/o/headless-commerce-delivery-catalog/v1.0/channels/42397/products?'
			);

			const searchURL = new URL(searchURLString);

			expect(searchURL.searchParams.get('accountId')).toBe(
				String(ACCOUNT_ID)
			);
			expect(searchURL.searchParams.get('search')).toBe('MIN93016A');
		});

		it('does not list the SKUs that cannot be purchased', async () => {
			renderCartQuickAdd({
				products: [
					mockChannelProduct({
						skus: [
							mockChannelProductSku({id: 101, sku: 'MIN93016A'}),
							mockChannelProductSku({
								id: 102,
								purchasable: false,
								sku: 'MIN93016B',
							}),
						],
					}),
				],
			});

			await search('MIN93016');

			expect(
				await screen.findByRole('menuitem', {name: /MIN93016A/})
			).toBeInTheDocument();
			expect(
				screen.queryByRole('menuitem', {name: /MIN93016B/})
			).not.toBeInTheDocument();
		});

		it('lists the SKUs of the other products when a product has no SKUs', async () => {
			renderCartQuickAdd({
				products: [
					mockChannelProduct({name: 'Calipers', skus: []}),
					mockChannelProduct({
						name: 'ABS Sensor',
						skus: [mockChannelProductSku({sku: 'MIN93015'})],
					}),
				],
			});

			await search('MIN930');

			expect(
				await screen.findByRole('menuitem', {name: /MIN93015/})
			).toBeInTheDocument();
		});

		it('searches the pasted text', async () => {
			renderCartQuickAdd({
				products: [
					mockChannelProduct({
						skus: [mockChannelProductSku({sku: 'MIN93016A'})],
					}),
				],
			});

			fireEvent.paste(screen.getByRole('combobox'), {
				clipboardData: {getData: () => 'MIN93016A'},
			});

			await act(async () => {
				jest.advanceTimersByTime(500);
			});

			expect(
				await screen.findByRole('menuitem', {name: /MIN93016A/})
			).toBeInTheDocument();
		});

		it('keeps the typed text without selecting it when Enter or a comma is pressed', async () => {
			renderCartQuickAdd();

			const input = screen.getByRole('combobox');

			await search('abc123');

			fireEvent.keyDown(input, {key: ','});
			fireEvent.keyDown(input, {key: 'Enter'});

			expect(input).toHaveValue('abc123');
			expect(screen.queryByRole('row')).not.toBeInTheDocument();
		});

		it('removes the selected SKU chips one by one', async () => {
			const skus = ['MIN55859', 'MIN55860', 'MIN55861'];

			renderCartQuickAdd({
				products: [
					mockChannelProduct({
						name: 'Brake Pads',
						skus: skus.map((sku, index) =>
							mockChannelProductSku({id: index + 1, sku})
						),
					}),
				],
			});

			const searchInput = screen.getByPlaceholderText('search-products');

			for (const sku of skus) {
				userEvent.type(searchInput, 'MIN');

				await act(async () => {
					jest.advanceTimersByTime(500);
				});

				userEvent.click(await screen.findByText(sku));
			}

			for (const sku of skus) {
				expect(
					screen.getByRole('button', {name: `Remove ${sku}`})
				).toBeInTheDocument();
			}

			expect(
				screen.getByRole('button', {name: 'add-to-cart'})
			).toBeEnabled();
			expect(
				screen.queryByPlaceholderText('search-products')
			).not.toBeInTheDocument();

			for (const sku of skus) {
				userEvent.click(
					screen.getByRole('button', {name: `Remove ${sku}`})
				);

				expect(
					screen.queryByRole('button', {name: `Remove ${sku}`})
				).not.toBeInTheDocument();
			}

			expect(
				screen.getByRole('button', {name: 'add-to-cart'})
			).toBeDisabled();
			expect(screen.getByPlaceholderText('search-products')).toHaveValue(
				''
			);
		});
	});

	describe('adding to the cart', () => {
		it('adds the selected SKU with its options', async () => {
			renderCartQuickAdd({
				products: [
					mockChannelProduct({
						skus: [
							mockChannelProductSku({
								id: 101,
								sku: 'MIN93016A',
								skuOptions: [
									{
										key: 233906,
										skuOptionKey: 'package-quantity',
										skuOptionName: 'Package Quantity',
										skuOptionValueKey: '12',
										value: 233907,
									},
								],
							}),
						],
					}),
				],
			});

			await quickAdd('MIN93016A');

			await waitFor(() => {
				expect(fetch).toHaveBeenCalledWith(
					expect.anything(),
					expect.objectContaining({method: 'POST'})
				);
			});

			const {options, quantity, skuId} = getRequestBody('POST');

			expect(JSON.parse(options)).toEqual([
				expect.objectContaining({key: 'package-quantity', value: '12'}),
			]);
			expect(quantity).toBe(1);
			expect(skuId).toBe(101);
		});

		it('increases the quantity of the cart item when a SKU that is already in the cart is added with another SKU', async () => {
			renderCartQuickAdd({
				cartItems: [
					mockCartItem({
						id: 1,
						quantity: 1,
						sku: 'MIN93016A',
						skuId: 101,
					}),
				],
				products: [
					mockChannelProduct({
						skus: [
							mockChannelProductSku({id: 101, sku: 'MIN93016A'}),
							mockChannelProductSku({id: 102, sku: 'MIN93016B'}),
						],
					}),
				],
			});

			await quickAdd('MIN93016A', 'MIN93016B');

			await waitFor(() => {
				expect(fetch).toHaveBeenCalledWith(
					expect.anything(),
					expect.objectContaining({method: 'PATCH'})
				);
			});

			expect(getRequestBody('PATCH')).toEqual({
				cartItems: [
					expect.objectContaining({id: 1, quantity: 2, skuId: 101}),
					expect.objectContaining({quantity: 1, skuId: 102}),
				],
			});
		});

		it('adds a separate cart item when a SKU that is already in the cart is added and the order items are shown separately', async () => {
			Liferay.CommerceContext.showSeparateOrderItems = true;

			renderCartQuickAdd({
				cartItems: [
					mockCartItem({
						id: 1,
						quantity: 1,
						sku: 'MIN93016A',
						skuId: 101,
					}),
				],
				products: [
					mockChannelProduct({
						skus: [
							mockChannelProductSku({id: 101, sku: 'MIN93016A'}),
							mockChannelProductSku({id: 102, sku: 'MIN93016B'}),
						],
					}),
				],
			});

			await quickAdd('MIN93016A', 'MIN93016B');

			await waitFor(() => {
				expect(fetch).toHaveBeenCalledWith(
					expect.anything(),
					expect.objectContaining({method: 'PATCH'})
				);
			});

			expect(getRequestBody('PATCH')).toEqual({
				cartItems: [
					expect.objectContaining({id: 1, quantity: 1, skuId: 101}),
					expect.objectContaining({quantity: 1, skuId: 101}),
					expect.objectContaining({quantity: 1, skuId: 102}),
				],
			});
		});

		it('adds the discontinued SKU when its product allows back order', async () => {
			renderCartQuickAdd({
				products: [
					mockChannelProduct({
						productConfiguration: {allowBackOrder: true},
						skus: [
							mockDiscontinuedChannelProductSku({
								id: 101,
								sku: 'MIN55861',
							}),
						],
					}),
				],
			});

			await quickAdd('MIN55861');

			await waitFor(() => {
				expect(fetch).toHaveBeenCalledWith(
					expect.anything(),
					expect.objectContaining({method: 'POST'})
				);
			});

			expect(getRequestBody('POST')).toEqual(
				expect.objectContaining({replacedSkuId: 0, skuId: 101})
			);
		});

		it('shows an error and adds nothing when no valid quantity can be added', async () => {
			renderCartQuickAdd({
				products: [
					mockChannelProduct({
						productConfiguration: {
							maxOrderQuantity: 4,
							multipleOrderQuantity: 5,
						},
						skus: [mockChannelProductSku({sku: 'MIN55860'})],
					}),
				],
			});

			await quickAdd('MIN55860');

			expect(
				await screen.findByText(/please-enter-a-valid-quantity/)
			).toBeInTheDocument();
			expect(fetch).not.toHaveBeenCalledWith(
				expect.anything(),
				expect.objectContaining({method: 'PATCH'})
			);
			expect(fetch).not.toHaveBeenCalledWith(
				expect.anything(),
				expect.objectContaining({method: 'POST'})
			);
		});
	});
});
