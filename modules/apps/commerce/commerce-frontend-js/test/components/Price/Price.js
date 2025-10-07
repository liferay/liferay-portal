/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, render, waitFor} from '@testing-library/react';
import React from 'react';

import Price from '../../../src/main/resources/META-INF/resources/components/price/Price';
import {CP_INSTANCE_CHANGED} from '../../../src/main/resources/META-INF/resources/utilities/eventsDefinitions';

describe('Price', () => {
	const BASE_PRICE_PROPS = {
		currency: 'USD',
		price: 10.0,
		priceFormatted: '$ 10.00',
	};

	const BASE_PROPS = {
		price: BASE_PRICE_PROPS,
	};

	describe('by data flow', () => {
		beforeEach(() => {
			jest.resetAllMocks();

			window.Liferay.Language.get = jest.fn();
		});

		it('displays the formatted list price of an item', () => {
			const price = {
				discount: 0.0,
				discountFormatted: '0.0',
				discountPercentage: '0.00',
				discountPercentageLevel1: 0.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: BASE_PROPS.price.price,
				finalPriceFormatted: BASE_PROPS.price.priceFormatted,
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const labels = container.querySelectorAll('.price-label');
			const values = container.querySelectorAll('.price-value');

			expect(Array.from(labels).length).toEqual(1);
			expect(Array.from(values).length).toEqual(1);

			expect(window.Liferay.Language.get).toHaveBeenCalledTimes(1);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'list-price'
			);

			const [listPrice] = Array.from(values);

			expect(listPrice.classList.length).toEqual(1);
			expect(listPrice.classList.contains('price-value')).toBe(true);
			expect(listPrice.classList.contains('price-value-inactive')).toBe(
				false
			);
			expect(listPrice.innerHTML).toEqual(
				BASE_PROPS.price.priceFormatted
			);
		});

		it('displays the formatted promo price of an item', () => {
			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '0.00',
				discountPercentageLevel1: 0.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 8,
				finalPriceFormatted: '$ 8.00',
				promoPrice: 8,
				promoPriceFormatted: '$ 8.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const labels = container.querySelectorAll('.price-label');
			const values = container.querySelectorAll('.price-value');

			expect(Array.from(labels).length).toEqual(2);
			expect(Array.from(values).length).toEqual(2);

			expect(window.Liferay.Language.get).toHaveBeenCalledTimes(2);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'list-price'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'promotion-price'
			);

			const [listPrice, promoPrice] = Array.from(values);

			expect(listPrice.classList.length).toEqual(2);
			expect(listPrice.classList.contains('price-value')).toBe(true);
			expect(listPrice.classList.contains('price-value-inactive')).toBe(
				true
			);

			expect(promoPrice.classList.length).toEqual(2);
			expect(promoPrice.classList.contains('price-value')).toBe(true);
			expect(promoPrice.classList.contains('price-value-promo')).toBe(
				true
			);
			expect(promoPrice.classList.contains('price-value-inactive')).toBe(
				false
			);

			expect(listPrice.innerHTML).toEqual(
				BASE_PROPS.price.priceFormatted
			);
			expect(promoPrice.innerHTML).toEqual(price.promoPriceFormatted);
		});

		it('displays the formatted discounted price of an item', () => {
			const price = {
				discount: 2.0,
				discountFormatted: '$ 2.00',
				discountPercentage: '20.00',
				discountPercentageLevel1: 8.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 8,
				finalPriceFormatted: '$ 8.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const labels = container.querySelectorAll('.price-label');
			const values = container.querySelectorAll('.price-value');

			expect(Array.from(labels).length).toEqual(3);
			expect(Array.from(values).length).toEqual(3);

			expect(window.Liferay.Language.get).toHaveBeenCalledTimes(3);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'list-price'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'discount'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'net-price'
			);

			const [listPrice, discount, finalPrice] = Array.from(values);

			expect(listPrice.classList.length).toEqual(2);
			expect(listPrice.classList.contains('price-value')).toBe(true);
			expect(listPrice.classList.contains('price-value-inactive')).toBe(
				true
			);

			expect(discount.classList.length).toEqual(2);
			expect(discount.classList.contains('price-value')).toBe(true);
			expect(discount.classList.contains('price-value-discount')).toBe(
				true
			);

			expect(finalPrice.classList.length).toEqual(2);
			expect(finalPrice.classList.contains('price-value')).toBe(true);
			expect(finalPrice.classList.contains('price-value-final')).toBe(
				true
			);

			expect(listPrice.innerHTML).toEqual(
				BASE_PROPS.price.priceFormatted
			);
			expect(
				discount.querySelector('.price-value-percentage').innerHTML
			).toEqual(`–${price.discountPercentage}%`);
			expect(finalPrice.innerHTML).toEqual(price.finalPriceFormatted);
		});

		it('displays the formatted discounted price of an item, also with a promo price applied', () => {
			const price = {
				discount: 2.0,
				discountFormatted: '$ 2.00',
				discountPercentage: '20.00',
				discountPercentageLevel1: 8.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 8,
				finalPriceFormatted: '$ 8.00',
				promoPrice: 10,
				promoPriceFormatted: '$ 10.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const labels = container.querySelectorAll('.price-label');
			const values = container.querySelectorAll('.price-value');

			expect(Array.from(labels).length).toEqual(4);
			expect(Array.from(values).length).toEqual(4);

			expect(window.Liferay.Language.get).toHaveBeenCalledTimes(4);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'list-price'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'promotion-price'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'discount'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'net-price'
			);

			const [listPrice, promoPrice, discount, finalPrice] =
				Array.from(values);

			expect(listPrice.classList.length).toEqual(2);
			expect(listPrice.classList.contains('price-value')).toBe(true);
			expect(listPrice.classList.contains('price-value-inactive')).toBe(
				true
			);

			expect(promoPrice.classList.length).toEqual(3);
			expect(promoPrice.classList.contains('price-value')).toBe(true);
			expect(promoPrice.classList.contains('price-value-promo')).toBe(
				true
			);
			expect(promoPrice.classList.contains('price-value-inactive')).toBe(
				true
			);

			expect(discount.classList.length).toEqual(2);
			expect(discount.classList.contains('price-value')).toBe(true);
			expect(discount.classList.contains('price-value-discount')).toBe(
				true
			);

			expect(finalPrice.classList.length).toEqual(2);
			expect(finalPrice.classList.contains('price-value')).toBe(true);
			expect(finalPrice.classList.contains('price-value-final')).toBe(
				true
			);

			expect(listPrice.innerHTML).toEqual(
				BASE_PROPS.price.priceFormatted
			);
			expect(promoPrice.innerHTML).toEqual(price.promoPriceFormatted);
			expect(
				discount.querySelector('.price-value-percentage').innerHTML
			).toEqual(`–${price.discountPercentage}%`);
			expect(finalPrice.innerHTML).toEqual(price.finalPriceFormatted);
		});
	});

	describe('by display settings', () => {
		beforeEach(() => {
			jest.resetAllMocks();

			window.Liferay.Language.get = jest.fn();
		});

		it('displays the formatted discounted gross price of an item', () => {
			const netPrice = false;

			const price = {
				discount: 2.0,
				discountFormatted: '$ 2.00',
				discountPercentage: '20.00',
				discountPercentageLevel1: 8.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 8,
				finalPriceFormatted: '$ 8.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					netPrice={netPrice}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const labels = container.querySelectorAll('.price-label');
			const values = container.querySelectorAll('.price-value');

			expect(Array.from(labels).length).toEqual(3);
			expect(Array.from(values).length).toEqual(3);

			expect(window.Liferay.Language.get).toHaveBeenCalledTimes(3);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'list-price'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'discount'
			);
			expect(window.Liferay.Language.get).toHaveBeenCalledWith(
				'gross-price'
			);
		});

		it('displays the price of an item with a wrapping container, by default', () => {
			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '0.00',
				discountPercentageLevel1: 0.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 10,
				finalPriceFormatted: '$ 10.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const wrapper = container.querySelector('.price');

			expect(wrapper).toBeInTheDocument();
		});

		it('displays the price of an item without a wrapping container, if standalone', () => {
			const standalone = true;

			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '0.00',
				discountPercentageLevel1: 0.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 10,
				finalPriceFormatted: '$ 10.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					price={{...BASE_PRICE_PROPS, ...price}}
					standalone={standalone}
				/>
			);

			const wrapper = container.querySelector('.price');

			expect(wrapper).not.toBeInTheDocument();
		});

		it('displays the price of an item in a non-compact visualization by default', () => {
			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '0.00',
				discountPercentageLevel1: 0.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 10,
				finalPriceFormatted: '$ 10.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const wrapper = container.querySelector('.price.compact');

			expect(wrapper).not.toBeInTheDocument();
		});

		it('displays the price of an item in a compact visualization if set', () => {
			const compact = true;

			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '0.00',
				discountPercentageLevel1: 0.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 10,
				finalPriceFormatted: '$ 10.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					compact={compact}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const wrapper = container.querySelector('.price.compact');

			expect(wrapper).toBeInTheDocument();
		});

		it('displays the discount levels in place of a discount percentage', () => {
			const displayDiscountLevels = true;

			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '20.00',
				discountPercentageLevel1: 8.5,
				discountPercentageLevel2: 2.25,
				discountPercentageLevel3: 3.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 10,
				finalPriceFormatted: '$ 10.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const {container} = render(
				<Price
					{...BASE_PROPS}
					displayDiscountLevels={displayDiscountLevels}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			const labels = Array.from(
				container.querySelectorAll('.price-label')
			);
			const values = Array.from(
				container.querySelectorAll('.price-value')
			);

			expect(labels.length).toEqual(3);
			expect(values.length).toEqual(3);

			const [, discount] = values;

			const discountPercentages = Array.from(
				discount.querySelectorAll('.price-value-percentages')
			);

			expect(discountPercentages.length).toEqual(4);

			['8.50', '2.25', '3', '0'].forEach((resultingPercentage, i) => {
				expect(discountPercentages[i].innerHTML).toEqual(
					resultingPercentage
				);
			});

			const singleDiscountPercentage = discount.querySelector(
				'.price-value-percentage'
			);

			expect(singleDiscountPercentage).not.toBeInTheDocument();
		});
	});

	describe('by event update', () => {
		beforeEach(() => {
			jest.resetAllMocks();

			window.Liferay.Language.get = jest.fn();
		});

		it('attaches a namespaced event listener for price update via event', () => {
			const namespace = 'someNamespace_';

			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '20.00',
				discountPercentageLevel1: 8.5,
				discountPercentageLevel2: 2.25,
				discountPercentageLevel3: 3.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 10,
				finalPriceFormatted: '$ 10.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			window.Liferay.on = jest.fn();

			render(
				<Price
					{...BASE_PROPS}
					namespace={namespace}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			expect(window.Liferay.on).toHaveBeenCalledWith(
				`${namespace}${CP_INSTANCE_CHANGED}`,
				expect.any(Function)
			);
		});

		it(`updates the price via event listener on namespaced '${CP_INSTANCE_CHANGED}'`, async () => {
			const namespace = 'someNamespace_';

			const price = {
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '20.00',
				discountPercentageLevel1: 8.5,
				discountPercentageLevel2: 2.25,
				discountPercentageLevel3: 3.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 10,
				finalPriceFormatted: '$ 10.00',
				promoPrice: 0,
				promoPriceFormatted: '$ 0.00',
			};

			const incomingCPInstancePrice = {
				cpInstance: {
					price: {
						discountPercentage: '0',
						discountPercentages: null,
						finalPrice: 0,
						price: '$ 20.00',
						promoPrice: null,
					},
				},
			};

			let updatePriceCB;

			window.Liferay.on = jest.fn((eventName, callback) => {
				if (eventName.includes(CP_INSTANCE_CHANGED)) {
					updatePriceCB = callback;
				}
			});

			const {container} = render(
				<Price
					{...BASE_PROPS}
					namespace={namespace}
					price={{...BASE_PRICE_PROPS, ...price}}
				/>
			);

			await act(async () => {
				updatePriceCB(incomingCPInstancePrice);
			});

			await waitFor(() => {
				const labels = container.querySelectorAll('.price-label');
				const values = container.querySelectorAll('.price-value');

				expect(Array.from(labels).length).toEqual(1);
				expect(Array.from(values).length).toEqual(1);

				expect(window.Liferay.Language.get).toHaveBeenCalledWith(
					'list-price'
				);

				const [listPrice] = Array.from(values);

				expect(listPrice.classList.length).toEqual(1);
				expect(listPrice.classList.contains('price-value')).toBe(true);
				expect(
					listPrice.classList.contains('price-value-inactive')
				).toBe(false);
				expect(listPrice.innerHTML).toEqual(
					incomingCPInstancePrice.cpInstance.price.price
				);
			});
		});
	});
});
