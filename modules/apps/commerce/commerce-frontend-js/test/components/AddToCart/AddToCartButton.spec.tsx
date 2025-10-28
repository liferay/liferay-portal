/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {RenderResult, cleanup, fireEvent, render} from '@testing-library/react';

// @ts-ignore

import fetchMock from 'fetch-mock';
import React from 'react';
import {act} from 'react-dom/test-utils';

// @ts-ignore

import AddToCartButton from '../../../src/main/resources/META-INF/resources/components/add_to_cart/AddToCartButton';

// @ts-ignore

import {selectOrderType} from '../../../src/main/resources/META-INF/resources/utilities/modals/selectOrderType';

jest.mock(
	'../../../src/main/resources/META-INF/resources/utilities/modals/selectOrderType'
);

interface ILocators {
	button: HTMLButtonElement;
}

const getLocators = (renderedComponent: RenderResult): ILocators => {
	return {
		button: renderedComponent.container.querySelector(
			'button'
		) as HTMLButtonElement,
	};
};

const props = {
	accountId: 43879,
	cartId: '43882',
	cartUUID: 'a711bf49-a2d3-2c8d-23c9-abaff7d288a5',
	channel: {
		currencyCode: 'USD',
		groupId: '42398',
		id: '42397',
	},
	settings: {
		iconOnly: false,
		productConfiguration: {
			allowedOrderQuantities: [],
			maxOrderQuantity: 50,
			minOrderQuantity: 1,
			multipleOrderQuantity: 1,
		},
	},
	size: 'sm',
};

describe('Add to Cart Button', () => {
	const addProductToCartFn = jest.fn();
	const addProductsToCartFn = jest.fn();
	const createCartFn = jest.fn();
	const getCartFn = jest.fn();

	const {Liferay: originalLiferayObject} = global.window;

	beforeEach(() => {
		fetchMock.get(
			/headless-commerce-delivery-cart\/v1.0\/carts\/[0-9]+\?nestedFields=cartItems/,
			() => {
				getCartFn();

				return {cartItems: []};
			}
		);

		fetchMock.get(
			/headless-commerce-delivery-cart\/v1.0\/channels\/[0-9]+\/account\/[0-9]+\/carts/,
			() => {
				return {items: []};
			}
		);

		fetchMock.post(
			/headless-commerce-delivery-cart\/v1.0\/channels\/[0-9]+\/carts\?nestedFields=cartItems/,
			(_: any, options: any) => {
				createCartFn(JSON.parse(options.body || '{}'));

				return {items: []};
			}
		);

		fetchMock.post(
			/headless-commerce-delivery-cart\/v1.0\/carts\/[0-9]+\/items/,
			(_: any, options: any) => {
				addProductToCartFn(JSON.parse(options.body || '{}'));

				return {};
			}
		);

		fetchMock.patch(
			/\/o\/headless-commerce-delivery-cart\/v1.0\/carts\/[0-9]+\?nestedFields=cartItems/,
			(_: any, options: any) => {
				addProductsToCartFn(JSON.parse(options.body || '{}'));

				return {cartItems: []};
			}
		);

		global.window.Liferay = {
			...originalLiferayObject,
			CommerceContext: {
				...global.window.Liferay.CommerceContext,
				orderTypes: [],
			},
		};

		selectOrderType.mockImplementation(() => Promise.resolve(123));
	});

	afterEach(() => {
		cleanup();

		fetchMock.restore();

		addProductToCartFn.mockReset();
		addProductsToCartFn.mockReset();
		createCartFn.mockReset();
		getCartFn.mockReset();
	});

	afterAll(() => {
		global.window.Liferay = originalLiferayObject;
	});

	it('Must render the component', () => {
		const addToCartButton = render(<AddToCartButton {...props} />);

		const {button} = getLocators(addToCartButton);

		expect(addToCartButton.container).toBeInTheDocument();
		expect(button).toBeInTheDocument();
	});

	it('Must be disabled consistently with its prop', () => {
		const addToCartButton = render(
			<AddToCartButton {...props} disabled={true} />
		);

		const {button} = getLocators(addToCartButton);

		expect(button).toBeDisabled();
	});

	it('Must add a single product to the cart', async () => {
		const addToCartButton = render(
			<AddToCartButton
				{...props}
				cpInstances={[
					{
						inCart: false,
						quantity: 3,
						skuId: 123,
						skuOptions: [],
						validQuantity: true,
					},
				]}
			/>
		);

		const {button} = getLocators(addToCartButton);

		await act(async () => {
			fireEvent.click(button);
		});

		expect(addProductToCartFn).toHaveBeenCalledWith({
			options: '[]',
			quantity: 3,
			replacedSkuId: 0,
			skuId: 123,
		});
		expect(getCartFn).toHaveBeenCalled();
	});

	it('Must add a single product to the cart also if already in', async () => {
		const addToCartButton = render(
			<AddToCartButton
				{...props}
				cpInstances={[
					{
						inCart: true,
						quantity: 3,
						skuId: 123,
						skuOptions: [],
						validQuantity: true,
					},
				]}
			/>
		);

		const {button} = getLocators(addToCartButton);

		await act(async () => {
			fireEvent.click(button);
		});

		expect(addProductToCartFn).toHaveBeenCalledWith({
			options: '[]',
			quantity: 3,
			replacedSkuId: 0,
			skuId: 123,
		});
		expect(getCartFn).toHaveBeenCalled();
		expect(getCartFn).toHaveBeenCalledTimes(1);
	});

	it('Must add multiple products to the cart with full cart patch', async () => {
		const addToCartButton = render(
			<AddToCartButton
				{...props}
				cpInstances={[
					{
						inCart: false,
						quantity: 3,
						skuId: 123,
						skuOptions: [],
						validQuantity: true,
					},
					{
						inCart: false,
						quantity: 5,
						skuId: 456,
						skuOptions: [],
						validQuantity: true,
					},
				]}
			/>
		);

		const {button} = getLocators(addToCartButton);

		await act(async () => {
			fireEvent.click(button);
		});

		expect(getCartFn).toHaveBeenCalled();
		expect(addProductsToCartFn).toHaveBeenCalledWith({
			cartItems: [
				{
					options: '[]',
					quantity: 3,
					replacedSkuId: 0,
					skuId: 123,
				},
				{
					options: '[]',
					quantity: 5,
					replacedSkuId: 0,
					skuId: 456,
				},
			],
		});
		expect(getCartFn).toHaveBeenCalledTimes(1);
	});

	it('Must not a product to the cart if it has invalid quantities', async () => {
		const addToCartButton = render(
			<AddToCartButton
				{...props}
				cpInstances={[
					{
						inCart: false,
						quantity: 3,
						skuId: 123,
						skuOptions: [],
						validQuantity: false,
					},
				]}
			/>
		);

		const {button} = getLocators(addToCartButton);

		await act(async () => {
			fireEvent.click(button);
		});

		expect(addProductToCartFn).not.toHaveBeenCalledWith();
		expect(getCartFn).not.toHaveBeenCalled();
	});

	it('Must not add multiple products to the cart if any has an invalid quantity', async () => {
		const addToCartButton = render(
			<AddToCartButton
				{...props}
				cpInstances={[
					{
						inCart: false,
						quantity: 3,
						skuId: 123,
						skuOptions: [],
						validQuantity: true,
					},
					{
						inCart: false,
						quantity: 5,
						skuId: 456,
						skuOptions: [],
						validQuantity: false,
					},
				]}
			/>
		);

		const {button} = getLocators(addToCartButton);

		await act(async () => {
			fireEvent.click(button);
		});

		expect(getCartFn).not.toHaveBeenCalled();
		expect(addProductsToCartFn).not.toHaveBeenCalledWith();
	});

	it('Must create a new cart if does not exist', async () => {
		const addToCartButton = render(
			<AddToCartButton
				{...props}
				cartId={0}
				cpInstances={[
					{
						inCart: false,
						quantity: 3,
						skuId: 123,
						skuOptions: [],
						validQuantity: true,
					},
				]}
			/>
		);

		const {button} = getLocators(addToCartButton);

		await act(async () => {
			fireEvent.click(button);
		});

		expect(createCartFn).toHaveBeenCalledWith({
			accountId: 43879,
			cartItems: [
				{
					options: '[]',
					quantity: 3,
					replacedSkuId: 0,
					skuId: 123,
				},
			],
			currencyCode: 'USD',
			orderTypeId: null,
		});
		expect(getCartFn).not.toHaveBeenCalled();
	});

	it('Must show the order type modal if provided', async () => {
		global.window.Liferay.CommerceContext.orderTypes = [
			{
				label_i18n: 'type-1',
				orderTypeId: 123,
			},
			{
				label_i18n: 'type-2',
				orderTypeId: 456,
			},
		];

		const addToCartButton = render(
			<AddToCartButton
				{...props}
				cartId={0}
				cpInstances={[
					{
						inCart: false,
						quantity: 3,
						skuId: 123,
						skuOptions: [],
						validQuantity: true,
					},
				]}
			/>
		);

		const {button} = getLocators(addToCartButton);

		await act(async () => {
			fireEvent.click(button);
		});

		expect(selectOrderType).toHaveBeenCalledWith(
			global.window.Liferay.CommerceContext.orderTypes
		);

		expect(createCartFn).toHaveBeenCalledWith({
			accountId: 43879,
			cartItems: [
				{
					options: '[]',
					quantity: 3,
					replacedSkuId: 0,
					skuId: 123,
				},
			],
			currencyCode: 'USD',
			orderTypeId: 123,
		});
		expect(getCartFn).not.toHaveBeenCalled();

		global.window.Liferay.CommerceContext.orderTypes = [];
	});
});
