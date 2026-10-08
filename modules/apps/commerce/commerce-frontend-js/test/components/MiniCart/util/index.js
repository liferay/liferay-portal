/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {openToast} from 'frontend-js-components-web';

import {
	DEFAULT_ORDER_DETAILS_PORTLET_ID,
	ORDER_UUID_PARAMETER,
} from '../../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';
import {
	filterOptions,
	getCorrectedQuantity,
	hasErrors,
	parseOptions,
	summaryDataMapper,
} from '../../../../src/main/resources/META-INF/resources/components/mini_cart/util/index';
import {regenerateOrderDetailURL} from '../../../../src/main/resources/META-INF/resources/utilities/regenerateOrderDetailURL';
import {BASE_CHANNEL_PRODUCT_CONFIGURATION} from '../../fixtures/productFixtures';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/ServiceProvider/index'
);

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: (key, ...values) => [key, ...values].join(' '),
}));

describe('MiniCart tests_utilities', () => {
	describe('filterOptions', () => {
		it('parses the options JSON string and keeps only options with a truthy value', () => {
			const OPTIONS_JSON = JSON.stringify([
				{key: 'size', value: ['L']},
				{key: 'empty', value: []},
				{key: 'blank', value: ['']},
			]);

			expect(filterOptions(OPTIONS_JSON)).toEqual([
				{key: 'size', value: ['L']},
			]);
		});

		it('returns an empty array when the input is not valid options JSON', () => {
			expect(filterOptions('/fail]')).toEqual([]);
			expect(filterOptions(null)).toEqual([]);
		});
	});

	describe('getCorrectedQuantity', () => {
		const SKU = 'MIN55860';

		const getCartItems = (cartQuantity) =>
			cartQuantity ? [{quantity: cartQuantity, sku: SKU}] : [];

		beforeEach(() => {
			openToast.mockClear();
		});

		it.each([
			[
				'the minimum order quantity when the SKU is not in the cart',
				{minOrderQuantity: 3},
				0,
				3,
			],
			[
				'the first multiple above the minimum order quantity when the multiple order quantity is lower than the minimum',
				{minOrderQuantity: 6, multipleOrderQuantity: 5},
				0,
				10,
			],
			[
				'the multiple order quantity when it is higher than the minimum order quantity',
				{minOrderQuantity: 4, multipleOrderQuantity: 5},
				0,
				5,
			],
			[
				'one unit when the SKU is already in the cart and no quantity rule is set',
				{},
				1,
				1,
			],
			[
				'the multiple order quantity when the SKU is already in the cart',
				{multipleOrderQuantity: 3},
				3,
				3,
			],
			[
				'the first allowed quantity when the SKU is not in the cart',
				{allowedOrderQuantities: [3, 7]},
				0,
				3,
			],
			[
				'the quantity that reaches the next allowed quantity when the SKU is already in the cart',
				{allowedOrderQuantities: [3, 7]},
				3,
				4,
			],
			[
				'the first allowed quantity that reaches the minimum order quantity',
				{allowedOrderQuantities: [7, 8], minOrderQuantity: 8},
				0,
				8,
			],
			[
				'the quantity that reaches the next allowed quantity that is a multiple of the multiple order quantity',
				{allowedOrderQuantities: [5, 6, 10], multipleOrderQuantity: 5},
				5,
				5,
			],
		])(
			'returns %s',
			(
				_description,
				productConfiguration,
				cartQuantity,
				expectedQuantity
			) => {
				expect(
					getCorrectedQuantity(
						{
							...BASE_CHANNEL_PRODUCT_CONFIGURATION,
							...productConfiguration,
						},
						SKU,
						getCartItems(cartQuantity)
					)
				).toBe(expectedQuantity);
				expect(openToast).not.toHaveBeenCalled();
			}
		);

		it('ignores the quantity of the other SKUs in the cart', () => {
			expect(
				getCorrectedQuantity(
					{
						...BASE_CHANNEL_PRODUCT_CONFIGURATION,
						allowedOrderQuantities: [3, 7],
					},
					SKU,
					[{quantity: 7, sku: 'MIN55861'}]
				)
			).toBe(3);
			expect(openToast).not.toHaveBeenCalled();
		});

		it.each([
			[
				'the SKU in the cart already has the last allowed quantity',
				{allowedOrderQuantities: [3, 7]},
				7,
				'the-maximum-allowed-quantity-for-x-is-x MIN55860 7',
			],
			[
				'the next allowed quantity exceeds the maximum order quantity',
				{allowedOrderQuantities: [6, 8], maxOrderQuantity: 7},
				6,
				'max-quantity-per-order-is-x 7',
			],
			[
				'every allowed quantity exceeds the maximum order quantity',
				{allowedOrderQuantities: [8, 9], maxOrderQuantity: 7},
				0,
				'max-quantity-per-order-is-x 7',
			],
			[
				'every allowed quantity is lower than the minimum order quantity',
				{allowedOrderQuantities: [6, 7], minOrderQuantity: 8},
				0,
				'the-minimum-quantity-is-x 8',
			],
			[
				'no further allowed quantity is a multiple of the multiple order quantity',
				{allowedOrderQuantities: [5, 6, 10], multipleOrderQuantity: 5},
				10,
				'the-product-quantity-is-not-valid',
			],
			[
				'adding the multiple order quantity exceeds the maximum order quantity',
				{maxOrderQuantity: 6, multipleOrderQuantity: 5},
				5,
				'max-quantity-per-order-is-x 6',
			],
			[
				'the multiple order quantity exceeds the maximum order quantity',
				{maxOrderQuantity: 4, multipleOrderQuantity: 5},
				0,
				'max-quantity-per-order-is-x 4',
			],
		])(
			'shows an error and returns zero when %s',
			(_description, productConfiguration, cartQuantity, message) => {
				expect(
					getCorrectedQuantity(
						{
							...BASE_CHANNEL_PRODUCT_CONFIGURATION,
							...productConfiguration,
						},
						SKU,
						getCartItems(cartQuantity)
					)
				).toBe(0);
				expect(openToast).toHaveBeenCalledWith({
					message,
					type: 'danger',
				});
			}
		);
	});

	describe('hasErrors', () => {
		it('returns true if at least one cart item contains error messages', () => {
			const CART_ITEMS = [
				{id: 1},
				{errorMessages: 'Error', id: 2},
				{id: 3},
			];

			expect(hasErrors(CART_ITEMS)).toBe(true);
		});

		it('returns false if no cart item contains error messages', () => {
			const CART_ITEMS = [{id: 1}, {id: 2}, {id: 3}];

			expect(hasErrors(CART_ITEMS)).toBe(false);
		});
	});

	describe('parseOptions', () => {
		it('parses and formats an array of {value} entries to a comma-separated options list string', () => {
			const PARSED_OPTIONS = [
				{key: 'package-quantity', value: '24'},
				{key: 'size', value: 'L'},
			];

			expect(parseOptions(PARSED_OPTIONS)).toEqual('24, L');
		});

		it('passes a non-array input through unchanged (graceful fall-through)', () => {
			expect(parseOptions(null)).toBeNull();
			expect(parseOptions('/fail]')).toEqual('/fail]');
		});
	});

	describe('regenerateOrderDetailURL', () => {
		const VALID_BASE_ORDER_DETAIL_PORTLET_URL = `http://localhost:3333/group/name/?p_p_id=${DEFAULT_ORDER_DETAILS_PORTLET_ID}`;
		const VALID_BASE_ORDER_DETAIL_URL = 'http://localhost:3333/group/name/';
		const VALID_ORDER_UUID = '00000-00000-22222-213jd-qwerty';

		const errorMessage = (argName) =>
			`Cannot generate a new Order Detail URL. Invalid "${argName}"`;

		it('returns a new valid Order Detail Portlet URL string', () => {
			expect(
				regenerateOrderDetailURL(
					VALID_BASE_ORDER_DETAIL_PORTLET_URL,
					12345,
					VALID_ORDER_UUID
				)
			).toEqual(
				`${VALID_BASE_ORDER_DETAIL_PORTLET_URL}&_${DEFAULT_ORDER_DETAILS_PORTLET_ID}_${ORDER_UUID_PARAMETER}=${VALID_ORDER_UUID}`
			);
		});

		it('returns a new valid Order Detail URL string', () => {
			expect(
				regenerateOrderDetailURL(
					VALID_BASE_ORDER_DETAIL_URL,
					12345,
					VALID_ORDER_UUID
				)
			).toEqual(VALID_BASE_ORDER_DETAIL_URL + 12345);
		});

		it('throws if the "orderId" string argument is empty or null', () => {
			try {
				expect(
					regenerateOrderDetailURL(
						VALID_BASE_ORDER_DETAIL_URL,
						'',
						VALID_ORDER_UUID
					)
				).toThrow();
				expect(
					regenerateOrderDetailURL(
						VALID_BASE_ORDER_DETAIL_URL,
						null,
						VALID_ORDER_UUID
					)
				).toThrow();
			}
			catch (error) {
				expect(error.message).toEqual(errorMessage`orderId`);
			}
		});

		it('throws if the "orderUUID" string argument is empty or null', () => {
			try {
				expect(
					regenerateOrderDetailURL(
						VALID_BASE_ORDER_DETAIL_PORTLET_URL,
						12345,
						''
					)
				).toThrow();

				expect(
					regenerateOrderDetailURL(
						VALID_BASE_ORDER_DETAIL_PORTLET_URL,
						12345,
						null
					)
				).toThrow();
			}
			catch (error) {
				expect(error.message).toEqual(errorMessage`orderUUID`);
			}
		});

		it('throws if the "baseOrderDetailURL" string argument is empty or null', () => {
			try {
				expect(
					regenerateOrderDetailURL('', 12345, VALID_ORDER_UUID)
				).toThrow();

				expect(
					regenerateOrderDetailURL(null, 12345, VALID_ORDER_UUID)
				).toThrow();

				expect(
					regenerateOrderDetailURL('', 12345, VALID_ORDER_UUID)
				).toThrow();

				expect(
					regenerateOrderDetailURL(null, 12345, VALID_ORDER_UUID)
				).toThrow();
			}
			catch (error) {
				expect(error.message).toEqual(errorMessage`baseOrderDetailURL`);
			}
		});
	});

	describe('summaryDataMapper', () => {
		const SUMMARY_SAMPLE = {
			currency: 'US Dollar',
			itemsQuantity: 48,
			shippingDiscountPercentages: ['0.00', '0.00', '0.00', '0.00'],
			shippingDiscountValue: 0.0,
			shippingDiscountValueFormatted: '$ 0.00',
			shippingValue: 0.0,
			shippingValueFormatted: '$ 0.00',
			shippingValueWithTaxAmount: 0.0,
			shippingValueWithTaxAmountFormatted: '$ 0.00',
			subtotal: 1858.5,
			subtotalDiscountPercentages: ['0.00', '0.00', '0.00', '0.00'],
			subtotalDiscountValue: 0.0,
			subtotalDiscountValueFormatted: '$ 0.00',
			subtotalFormatted: '$ 1,858.50',
			taxValue: 0.0,
			taxValueFormatted: '$ 0.00',
			total: 1858.5,
			totalDiscountPercentages: ['0.00', '0.00', '0.00', '0.00'],
			totalDiscountValue: 0.0,
			totalDiscountValueFormatted: '$ 0.00',
			totalFormatted: '$ 1,858.50',
		};

		it('converts a DeliveryCart API summary payload to a label/value map list for the Summary used in the MiniCart', () => {
			expect(summaryDataMapper(SUMMARY_SAMPLE)).toEqual([
				{label: 'quantity', value: 48},
				{label: 'subtotal', value: '$ 1,858.50'},
				{label: 'subtotal-discount', value: '$ 0.00'},
				{label: 'order-discount', value: '$ 0.00'},
				{label: 'total', style: 'big', value: '$ 1,858.50'},
			]);
		});

		describe('discount-to-subtotal and discount-to-total rows', () => {
			it('surfaces the subtotal discount value in the subtotal-discount row', () => {
				const rows = summaryDataMapper({
					...SUMMARY_SAMPLE,
					subtotalDiscountValue: 25.0,
					subtotalDiscountValueFormatted: '$ 25.00',
					total: 1833.5,
					totalFormatted: '$ 1,833.50',
				});

				const subtotalDiscountRow = rows.find(
					(row) => row.label === 'subtotal-discount'
				);
				const totalRow = rows.find((row) => row.label === 'total');

				expect(subtotalDiscountRow.value).toBe('$ 25.00');
				expect(totalRow.value).toBe('$ 1,833.50');
			});

			it('surfaces the order-level discount value in the order-discount row', () => {
				const rows = summaryDataMapper({
					...SUMMARY_SAMPLE,
					total: 1758.5,
					totalDiscountValue: 100.0,
					totalDiscountValueFormatted: '$ 100.00',
					totalFormatted: '$ 1,758.50',
				});

				const orderDiscountRow = rows.find(
					(row) => row.label === 'order-discount'
				);
				const totalRow = rows.find((row) => row.label === 'total');

				expect(orderDiscountRow.value).toBe('$ 100.00');
				expect(totalRow.value).toBe('$ 1,758.50');
			});
		});
	});
});
