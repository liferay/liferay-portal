/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	DEFAULT_ORDER_DETAILS_PORTLET_ID,
	ORDER_UUID_PARAMETER,
} from '../../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';
import {
	hasErrors,
	parseOptions,
	regenerateOrderDetailURL,
	summaryDataMapper,
} from '../../../../src/main/resources/META-INF/resources/components/mini_cart/util/index';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/ServiceProvider/index'
);

describe('MiniCart tests_utilities', () => {
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

	describe.skip('parseOptions', () => {
		it('parses and formats a JSON string input to an options list string', () => {
			const VALID_JSON_INPUT = `[
				{
					"key": "package-quantity", 
					"value": "24"
				},
				{
					"key": "size", 
					"value": "L"
				}
			]`;

			expect(parseOptions(VALID_JSON_INPUT)).toEqual('24, L');
		});

		it('returns an empty string when input is neither valid JSON nor a JSON-parsed array', () => {
			expect(parseOptions(null)).toEqual('');
			expect(parseOptions('/fail]')).toEqual('');
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
	});
});
