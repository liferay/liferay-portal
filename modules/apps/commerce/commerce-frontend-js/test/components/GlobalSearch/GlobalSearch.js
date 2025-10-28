/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import fetchMock from 'fetch-mock';
import React from 'react';

import ServiceProvider from '../../../src/main/resources/META-INF/resources/ServiceProvider/index';
import GlobalSearch from '../../../src/main/resources/META-INF/resources/components/global_search/GlobalSearch';
import {
	accountTemplate,
	getAccounts,
} from '../../tests_utilities/fake_data/accounts';
import {getOrders, orderTemplate} from '../../tests_utilities/fake_data/orders';
import {
	getProducts,
	productTemplate,
} from '../../tests_utilities/fake_data/products';

const accountsEndpointRegexp = new RegExp(
	ServiceProvider.AdminAccountAPI('v1').baseURL
);

const cartsEndpointRegexp = new RegExp(
	'/o/headless-commerce-delivery-cart/v1.0/channels/'
);

const productsEndpointRegexp = new RegExp(
	ServiceProvider.DeliveryCatalogAPI('v1').getBaseURL(11111)
);

const query = 'test';

describe('Global Search', () => {
	describe('When responses are ok', () => {
		let renderedComponent;

		beforeEach(() => {
			fetchMock.mock(accountsEndpointRegexp, (url) => getAccounts(url));
			fetchMock.mock(cartsEndpointRegexp, (url) => getOrders(url));
			fetchMock.mock(productsEndpointRegexp, (url) => getProducts(url));

			renderedComponent = render(
				<GlobalSearch
					accountId={11111}
					accountURLTemplate="/account-page/{id}"
					accountsSearchURLTemplate="/accounts?search={query}"
					cartURLTemplate="/cart-page/{id}"
					cartsSearchURLTemplate="/carts?search={query}"
					channelId={11111}
					globalSearchURLTemplate="/global?search={query}"
					productURLTemplate="/product-page/{id}"
					productsSearchURLTemplate="/products?search={query}"
				/>
			);
		});

		afterEach(() => {
			fetchMock.restore();
		});

		describe('When input is empty', () => {
			it('must display the global search input', () => {
				expect(
					renderedComponent.getByPlaceholderText(/search/)
				).toBeInTheDocument();
			});
		});

		describe('When input is filled', () => {
			beforeEach(() => {
				const input = renderedComponent.getByPlaceholderText(/search/);

				fireEvent.change(input, {target: {value: query}});
			});

			it('must show 3 loaders', () => {
				expect(
					renderedComponent.baseElement.querySelectorAll(
						'.loading-animation'
					).length
				).toBe(3);
			});

			describe('after the results are loaded', () => {
				beforeEach(async () => {
					await renderedComponent.findByText(
						`search-${query}-in-accounts`
					);
					await renderedComponent.findByText(
						`search-${query}-in-catalog`
					);
					await renderedComponent.findByText(
						`search-${query}-in-orders`
					);
				});

				it('must format the URL templates replacing the placeholders with entity properties', () => {
					expect(
						renderedComponent.getByText(`search-${query}-in-orders`)
							.href
					).toContain(`/carts?search=${query}`);

					expect(
						renderedComponent.getByText(
							`search-${query}-in-accounts`
						).href
					).toContain(`/accounts?search=${query}`);

					expect(
						renderedComponent.getByText(
							`search-${query}-in-catalog`
						).href
					).toContain(`/products?search=${query}`);

					expect(
						renderedComponent.getByText(`more-global-results`).href
					).toContain(`/global?search=${query}`);
				});

				it('must show a product list', () => {
					const products =
						renderedComponent.baseElement.querySelectorAll(
							'.product-item'
						);
					const firstProduct = products[0];
					const firstProductThumbnail =
						firstProduct.querySelector('img');

					expect(products.length).toBe(4);

					expect(firstProductThumbnail.src).toContain(
						productTemplate.urlImage
					);

					expect(firstProductThumbnail.alt).toBe(
						productTemplate.name
					);

					expect(firstProduct.href).toContain(
						`/product-page/${productTemplate.id}`
					);

					expect(firstProduct.text).toBe(productTemplate.name);
				});

				it('must show a orders list', () => {
					const carts =
						renderedComponent.baseElement.querySelectorAll(
							'.order-item'
						);
					const firstCart = carts[0];

					expect(carts.length).toBe(4);

					expect(firstCart.text).toContain(
						orderTemplate.id.toString()
					);

					expect(firstCart.href).toContain(
						`/cart-page/${orderTemplate.id}`
					);
				});

				it('must show an account list', () => {
					const accounts =
						renderedComponent.baseElement.querySelectorAll(
							'.account-item'
						);
					const firstAccount = accounts[0];
					const firstAccountThumbnail =
						firstAccount.querySelector('img');

					expect(accounts.length).toBe(4);

					expect(firstAccountThumbnail.src).toContain(
						accountTemplate.logoURL
					);

					expect(firstAccountThumbnail.alt).toBe(
						accountTemplate.name
					);

					expect(firstAccount.href).toContain(
						`/account-page/${accountTemplate.id}`
					);

					expect(firstAccount.text).toBe(accountTemplate.name);
				});
			});
		});
	});
});
