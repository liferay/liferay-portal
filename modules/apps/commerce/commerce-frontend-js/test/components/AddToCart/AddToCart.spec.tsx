/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {
	RenderResult,
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';

// @ts-ignore

import fetchMock from 'fetch-mock';
import React from 'react';

// @ts-ignore

import AddToCart from '../../../src/main/resources/META-INF/resources/components/add_to_cart/AddToCart';
import {
	CART_PRODUCT_QUANTITY_CHANGED,
	CURRENT_ACCOUNT_UPDATED,

	// eslint-disable-next-line lines-around-comment
	// @ts-ignore
} from '../../../src/main/resources/META-INF/resources/utilities/eventsDefinitions';

interface ILocators {
	button: HTMLButtonElement;
	input: HTMLInputElement;
}

const getLocators = (renderedComponent: RenderResult): ILocators => {
	return {
		button: renderedComponent.container.querySelector(
			'button'
		) as HTMLButtonElement,
		input: renderedComponent.container.querySelector(
			'input'
		) as HTMLInputElement,
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
	cpInstance: {
		inCart: false,
		options: [],
		quantity: 3,
		skuId: 42633,
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

jest.mock('frontend-js-components-web', () => {
	return {
		...(jest.requireActual('frontend-js-components-web') as any),
		openToast: jest.fn(),
	};
});

describe('Add to Cart', () => {
	const addProductToCartFn = jest.fn();

	const {Liferay: originalLiferayObject} = global.window;

	beforeEach(() => {
		fetchMock.get(
			/headless-commerce-delivery-cart\/v1.0\/channels\/[0-9]+\/account\/[0-9]+\/carts/,
			() => {
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

		global.window.Liferay = {
			...originalLiferayObject,
			CommerceContext: {
				...global.window.Liferay.CommerceContext,
				orderTypes: [],
			},
		};
	});

	afterEach(() => {
		cleanup();

		fetchMock.restore();

		addProductToCartFn.mockReset();
	});

	afterAll(() => {
		global.window.Liferay = originalLiferayObject;
	});

	it('Must render the component', () => {
		const addToCart = render(<AddToCart {...props} />);

		const {button, input} = getLocators(addToCart);

		expect(addToCart.container).toBeInTheDocument();
		expect(button).toBeInTheDocument();
		expect(input).toBeInTheDocument();
	});

	it('Must be disabled consistently with its prop', () => {
		const addToCart = render(<AddToCart {...props} disabled={true} />);

		const {button} = getLocators(addToCart);

		expect(button).toBeDisabled();
	});

	it('Must be disabled if accountId is not provided', () => {
		const addToCart = render(<AddToCart {...props} accountId={0} />);

		const {button} = getLocators(addToCart);

		expect(addToCart.container).toBeInTheDocument();
		expect(button).toBeDisabled();
	});

	it('Must hide indicator if sku not in the cart', () => {
		const addToCart = render(<AddToCart {...props} />);

		const {button} = getLocators(addToCart);

		expect(addToCart.container).toBeInTheDocument();
		expect(Array.from(button.classList)).not.toContain('is-added');
	});

	it('Must show indicator if sku already in the cart', () => {
		const addToCart = render(
			<AddToCart
				{...props}
				cpInstance={{
					inCart: true,
					options: [],
					quantity: 10,
					skuId: 42633,
				}}
			/>
		);

		const {button} = getLocators(addToCart);

		expect(addToCart.container).toBeInTheDocument();
		expect(Array.from(button.classList)).toContain('is-added');
	});

	it('Must focus the quantity selector when a user tries to add to the cart an invalid quantity', async () => {
		const addToCart = render(
			<AddToCart
				{...props}
				settings={{
					...props.settings,
					productConfiguration: {
						allowedOrderQuantities: [],
						maxOrderQuantity: 50,
						minOrderQuantity: 5,
						multipleOrderQuantity: 7,
					},
				}}
			/>
		);

		const {button, input} = getLocators(addToCart);

		fireEvent.change(input, {target: {value: 6}});

		const focusHandler = jest.fn();

		input.addEventListener('focus', focusHandler);

		fireEvent.focus(input);
		fireEvent.click(button);

		expect(addProductToCartFn).not.toHaveBeenCalled();
		expect(focusHandler).toHaveBeenCalled();
	});

	describe('Must handle Liferay events', () => {
		it('Must be disabled when accountId is not provided', () => {
			const addToCart = render(<AddToCart {...props} />);

			const {button} = getLocators(addToCart);

			(Liferay as any).fire(CURRENT_ACCOUNT_UPDATED, {
				id: 0,
			});

			expect(button).toBeDisabled();

			(Liferay as any).fire(CURRENT_ACCOUNT_UPDATED, {
				id: 1,
			});

			expect(button).toBeEnabled();
		});

		it('Must give a UI feedback about the state of sku in the cart', () => {
			const addToCart = render(<AddToCart {...props} />);

			const {button} = getLocators(addToCart);

			expect(Array.from(button.classList)).not.toContain('is-added');

			(Liferay as any).fire(CART_PRODUCT_QUANTITY_CHANGED, {
				quantity: 5,
				skuId: props.cpInstance.skuId,
			});

			expect(Array.from(button.classList)).toContain('is-added');

			(Liferay as any).fire(CART_PRODUCT_QUANTITY_CHANGED, {
				quantity: 0,
				skuId: props.cpInstance.skuId,
			});

			expect(Array.from(button.classList)).not.toContain('is-added');
		});
	});

	it('Must use the updated quantity to add a new item', async () => {
		const addToCart = render(<AddToCart {...props} />);

		const {button, input} = getLocators(addToCart);

		fireEvent.change(input, {target: {value: 10}});

		fireEvent.click(button);

		await waitFor(() =>
			expect(addProductToCartFn).toHaveBeenCalledWith({
				options: '[]',
				quantity: 10,
				replacedSkuId: 0,
				skuId: 42633,
			})
		);
	});

	describe('Product configuration constraints', () => {
		const renderWithConfiguration = (configuration: {
			allowedOrderQuantities?: number[];
			maxOrderQuantity?: number;
			minOrderQuantity?: number;
			multipleOrderQuantity?: number;
		}) =>
			render(
				<AddToCart
					{...props}
					settings={{
						...props.settings,
						productConfiguration: {
							allowedOrderQuantities: [],
							maxOrderQuantity: 0,
							minOrderQuantity: 1,
							multipleOrderQuantity: 1,
							...configuration,
						},
					}}
				/>
			);

		it('Must reject add-to-cart when the typed quantity is below minOrderQuantity', () => {
			const addToCart = renderWithConfiguration({minOrderQuantity: 4});

			const {button, input} = getLocators(addToCart);

			fireEvent.change(input, {target: {value: 2}});

			fireEvent.click(button);

			expect(addProductToCartFn).not.toHaveBeenCalled();
		});

		it('Must reject add-to-cart when the typed quantity exceeds maxOrderQuantity', () => {
			const addToCart = renderWithConfiguration({maxOrderQuantity: 4});

			const {button, input} = getLocators(addToCart);

			fireEvent.change(input, {target: {value: 6}});

			fireEvent.click(button);

			expect(addProductToCartFn).not.toHaveBeenCalled();
		});

		it('Must reject add-to-cart when the typed quantity is not a multiple of multipleOrderQuantity', () => {
			const addToCart = renderWithConfiguration({
				multipleOrderQuantity: 3,
			});

			const {button, input} = getLocators(addToCart);

			fireEvent.change(input, {target: {value: 5}});

			fireEvent.click(button);

			expect(addProductToCartFn).not.toHaveBeenCalled();
		});

		it('Must add to cart when the typed quantity satisfies all constraints', () => {
			const addToCart = renderWithConfiguration({
				maxOrderQuantity: 10,
				minOrderQuantity: 2,
				multipleOrderQuantity: 2,
			});

			const {button, input} = getLocators(addToCart);

			fireEvent.change(input, {target: {value: 4}});

			fireEvent.click(button);

			expect(addProductToCartFn).toHaveBeenCalledWith({
				options: '[]',
				quantity: 4,
				replacedSkuId: 0,
				skuId: 42633,
			});
		});

		it('Must render an allowedOrderQuantities dropdown with only the listed quantities', () => {
			renderWithConfiguration({
				allowedOrderQuantities: [2, 3, 6],
			});

			const select = screen.getByRole('combobox') as HTMLSelectElement;

			expect(select).toBeInTheDocument();
			expect(
				Array.from(select.options).map((option) => option.value)
			).toEqual(['2', '3', '6']);
		});
	});

	describe('Poshi: CommerceProductCard Unit ports', () => {
		// eslint-disable-next-line @typescript-eslint/no-var-requires
		const {
			mockBundledProductMultiSku,
			mockBundledProductSingleSku,

			// @ts-ignore
		} = require('../fixtures/productFixtures');

		it('CanAddToCartSingleSkuDynamicPriceBundledProductFromProductCard: a single-SKU bundled product (purchasable=true) renders an enabled add-to-cart button alongside the quantity selector', () => {
			const addToCart = render(
				<AddToCart {...mockBundledProductSingleSku()} />
			);

			const {button, input} = getLocators(addToCart);

			expect(input).toBeInTheDocument();
			expect(button).toBeInTheDocument();
			expect(button).not.toBeDisabled();
		});

		it('CanAddToCartSingleSkuStaticPriceBundledProductFromProductCard: a static-price single-SKU bundled product also renders an enabled add-to-cart button (the price-type does not affect the AddToCart contract)', () => {
			const addToCart = render(
				<AddToCart
					{...mockBundledProductSingleSku({
						settings: {priceType: 'static'},
					})}
				/>
			);

			const {button, input} = getLocators(addToCart);

			expect(input).toBeInTheDocument();
			expect(button).toBeInTheDocument();
			expect(button).not.toBeDisabled();
		});

		it('CannotAddToCartMultipleSkuDynamicPriceBundledProductFromProductCard: a multi-SKU bundled product (purchasable=false) renders the add-to-cart button in the disabled state', () => {
			const addToCart = render(
				<AddToCart {...mockBundledProductMultiSku()} />
			);

			const {button} = getLocators(addToCart);

			expect(button).toBeInTheDocument();
			expect(button).toBeDisabled();
		});

		it('CannotAddToCartMultipleSkuStaticPriceBundledProductFromProductCard: a static-price multi-SKU bundled product also renders the add-to-cart button disabled', () => {
			const addToCart = render(
				<AddToCart
					{...mockBundledProductMultiSku({
						settings: {priceType: 'static'},
					})}
				/>
			);

			const {button} = getLocators(addToCart);

			expect(button).toBeInTheDocument();
			expect(button).toBeDisabled();
		});

		it('AssertViewAllVariantsButtonRedirectsToProductDetailsPage: when a product cannot be purchased directly (purchasable=false), AddToCart is disabled — the surrounding product card is expected to surface a "view all variants" link instead', () => {
			const addToCart = render(
				<AddToCart {...mockBundledProductMultiSku()} />
			);

			const {button} = getLocators(addToCart);

			expect(button).toBeDisabled();
		});
	});
});
