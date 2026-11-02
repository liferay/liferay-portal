/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import fetchMock from 'fetch-mock';
import React from 'react';

import ServiceProvider from '../../../src/main/resources/META-INF/resources/ServiceProvider/index';
import EditItem from '../../../src/main/resources/META-INF/resources/components/mini_cart/EditItem';
import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';

jest.mock(
	'../../../src/main/resources/META-INF/resources/ServiceProvider/index',
	() => {
		const cartApi = {
			createItemByCartId: jest.fn(),
			deleteItemById: jest.fn(),
			updateItemById: jest.fn(),
		};

		const {default: ActualServiceProvider} = jest.requireActual(
			'../../../src/main/resources/META-INF/resources/ServiceProvider/index'
		);

		return {
			__esModule: true,
			default: {
				...ActualServiceProvider,
				DeliveryCartAPI: jest.fn(() => cartApi),
			},
		};
	}
);

describe('MiniCart EditItem', () => {
	const CartResource = ServiceProvider.DeliveryCartAPI('v1');

	const PRICE = {
		currency: 'USD',
		discountPercentageLevel1: 0,
		discountPercentageLevel2: 0,
		discountPercentageLevel3: 0,
		discountPercentageLevel4: 0,
		finalPrice: 8,
		finalPriceFormatted: '$ 8.00',
		price: 8,
		priceFormatted: '$ 8.00',
	};

	const EDITED_ITEM = {
		deliveryGroupName: 'Delivery Group',
		id: 202,
		name: 'An Item',
		options: '[]',
		price: PRICE,
		productId: 303,
		quantity: 1,
		requestedDeliveryDate: '2026-12-01T00:00:00Z',
		settings: {
			maxQuantity: 10,
			minQuantity: 1,
			multipleQuantity: 1,
		},
		shippingAddressId: 404,
		skuId: 10001,
	};

	beforeEach(() => {
		fetchMock.mockGlobal();

		fetchMock.route(/skus\/by-sku-option/, {
			id: '10002',
			price: PRICE,
		});
		fetchMock.route('*', {items: []});

		CartResource.createItemByCartId.mockReturnValue(Promise.resolve());
		CartResource.deleteItemById.mockReturnValue(Promise.resolve());
	});

	afterEach(() => {
		fetchMock.hardReset();

		jest.resetAllMocks();
	});

	it('deletes the edited item before adding the new SKU with its delivery data', async () => {
		render(
			<MiniCartContext.Provider
				value={{
					cartState: {
						accountId: 505,
						channel: {
							channel: {
								id: 606,
							},
						},
						id: 101,
					},
					closeCart: jest.fn(),
					editedItem: EDITED_ITEM,
					isUpdating: false,
					setEditedItem: jest.fn(),
					setIsUpdating: jest.fn(),
				}}
			>
				<EditItem />
			</MiniCartContext.Provider>
		);

		await waitFor(() =>
			expect(fetchMock.callHistory.called(/by-sku-option/)).toBe(true)
		);

		fireEvent.click(screen.getByText('save'));

		await waitFor(() =>
			expect(CartResource.createItemByCartId).toHaveBeenCalledWith(
				101,
				expect.objectContaining({
					deliveryGroupName: 'Delivery Group',
					requestedDeliveryDate: '2026-12-01T00:00:00Z',
					shippingAddressId: 404,
					skuId: 10002,
				})
			)
		);

		expect(CartResource.deleteItemById).toHaveBeenCalledWith(202);
		expect(
			CartResource.deleteItemById.mock.invocationCallOrder[0]
		).toBeLessThan(
			CartResource.createItemByCartId.mock.invocationCallOrder[0]
		);
		expect(CartResource.updateItemById).not.toHaveBeenCalled();
	});
});
