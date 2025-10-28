/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import {ProductCard} from '../../../src/main/resources/META-INF/resources/js/components/ProductCard';
import product from '../__mock__/product';

const onClick = jest.fn();

describe('ProductCard', () => {
	it('rendering component with props', () => {
		const {container, queryByText} = render(
			<ProductCard onClick={onClick} product={product}>
				children
			</ProductCard>
		);

		expect(container.querySelector('img')).toHaveAttribute(
			'src',
			product?.urlImage
		);
		expect(queryByText('children')).toBeInTheDocument();
		expect(queryByText(product?.name)).toBeInTheDocument();
		expect(queryByText(product?.catalogName)).toBeInTheDocument();
		expect(queryByText(product?.description)).toBeInTheDocument();

		const marketplaceSearchResultsButton = container.querySelector(
			'.marketplace-search-results-card-content'
		) as HTMLButtonElement;

		marketplaceSearchResultsButton.addEventListener('click', onClick);

		fireEvent.click(marketplaceSearchResultsButton);

		expect(onClick).toHaveBeenCalled();
		expect(queryByText(product.categories[0].name)).toBeInTheDocument();
		expect(queryByText('+ 1')).toBeInTheDocument();
	});

	it('rendering components witout props', () => {
		product.categories = [];

		const {queryByText} = render(
			<ProductCard onClick={onClick} product={product} />
		);

		expect(queryByText('+ 1')).toBeFalsy();
		expect(queryByText('children')).toBeFalsy();
		expect(queryByText('product category name')).toBeFalsy();
	});
});
