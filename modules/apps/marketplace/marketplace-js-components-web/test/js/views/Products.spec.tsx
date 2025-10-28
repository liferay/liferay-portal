/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {MarketplaceContext} from '../../../src/main/resources/META-INF/resources/js/MarketplaceContext';
import {MarketplaceProducts} from '../../../src/main/resources/META-INF/resources/js/views/Products';
import productResponse from '../__mock__/productResponse';

const onClickProduct = jest.fn();
const setProduct = jest.fn();
const setProductSearchParams = jest.fn();

const searchParams = {
	page: 1,
	pageSize: 8,
	search: '',
	sortDirection: 'asc' as 'asc' | 'desc',
	sortKey: 'createDate',
};

const productListView = {
	loading: false,
	productsResponse: productResponse,
	searchParams,
	setProductSearchParams,
};

describe('Products', () => {
	it('rendering component with all its props', async () => {
		const {queryByText} = render(
			<MarketplaceContext.Provider value={{productListView} as any}>
				<MarketplaceProducts onClickProduct={onClickProduct}>
					{(product) => <div onClick={setProduct(product)}></div>}
				</MarketplaceProducts>
			</MarketplaceContext.Provider>
		);

		const productCard = queryByText(
			productResponse.items[0].name
		) as HTMLButtonElement;

		expect(productCard).toBeInTheDocument();
		expect(
			queryByText(productResponse.items[0].catalogName)
		).toBeInTheDocument();

		fireEvent.click(productCard);

		expect(setProduct).toHaveBeenCalledTimes(1);

		fireEvent.click(queryByText('1') as HTMLButtonElement);

		expect(setProductSearchParams).toHaveBeenCalled();

		fireEvent.click(queryByText('8 items') as HTMLButtonElement);

		fireEvent.click(queryByText('16 items') as HTMLButtonElement);

		expect(setProductSearchParams).toHaveBeenCalledTimes(2);

		cleanup();
	});

	it('rendering components while loading true', async () => {
		productListView.loading = true;

		const {container} = render(
			<MarketplaceContext.Provider value={{productListView} as any}>
				<MarketplaceProducts onClickProduct={onClickProduct}>
					{(product) => <div onClick={setProduct(product)}></div>}
				</MarketplaceProducts>
			</MarketplaceContext.Provider>
		);

		expect(
			container.querySelector('.loading-animation-squares')
		).toBeTruthy();

		cleanup();
	});

	it('rendering component without product response', async () => {
		const productListViewWithoutProductResponse = {
			loading: false,
			productsResponse: [],
			searchParams,
			setProductSearchParams,
		};

		const {queryByText} = render(
			<MarketplaceContext.Provider
				value={
					{
						productListView: productListViewWithoutProductResponse,
					} as any
				}
			>
				<MarketplaceProducts onClickProduct={onClickProduct}>
					{(product) => <div onClick={setProduct(product)}></div>}
				</MarketplaceProducts>
			</MarketplaceContext.Provider>
		);

		expect(queryByText('no-products-were-found')).toBeInTheDocument();

		cleanup();
	});

	it('rendering component without search params', async () => {
		const hasSearchParams = {
			page: 1,
			pageSize: 8,
			search: 'search for this app',
			sortDirection: 'asc' as 'asc' | 'desc',
			sortKey: 'createDate',
		};

		const productListViewWithoutSearchParams = {
			loading: false,
			productsResponse: [],
			searchParams: hasSearchParams,
			setProductSearchParams,
		};

		const {queryByText} = render(
			<MarketplaceContext.Provider
				value={
					{
						productListView: productListViewWithoutSearchParams,
					} as any
				}
			>
				<MarketplaceProducts onClickProduct={onClickProduct}>
					{(product) => <div onClick={setProduct(product)}></div>}
				</MarketplaceProducts>
			</MarketplaceContext.Provider>
		);

		expect(
			queryByText('there-are-no-results-for-the-search-term-x')
		).toBeInTheDocument();
	});
});
