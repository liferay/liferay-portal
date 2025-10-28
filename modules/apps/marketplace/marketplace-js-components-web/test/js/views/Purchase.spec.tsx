/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {MarketplaceContext} from '../../../src/main/resources/META-INF/resources/js/MarketplaceContext';
import {MarketplaceProduct} from '../../../src/main/resources/META-INF/resources/js/core/MarketplaceProduct';
import {MarketplacePurchase} from '../../../src/main/resources/META-INF/resources/js/views/Purchase';
import product from '../__mock__/product';

describe('MarketplacePurchase', () => {
	it('rendering components with its props', async () => {
		const marketplaceProduct = new MarketplaceProduct(product);

		const {queryByText} = render(
			<MarketplaceContext.Provider value={{product} as any}>
				<MarketplacePurchase productPurchaseChildren="Product Purchase Children">
					children
				</MarketplacePurchase>
			</MarketplaceContext.Provider>
		);

		const {LATEST_VERSION} = marketplaceProduct.specificationValues;

		expect(queryByText('0CPUs, 0GB RAM')).toBeInTheDocument();
		expect(queryByText('children')).toBeInTheDocument();
		expect(queryByText('Free')).toBeInTheDocument();
		expect(queryByText('Product Purchase Children')).toBeInTheDocument();
		expect(
			queryByText(`${LATEST_VERSION} by ${product.catalogName}`)
		).toBeInTheDocument();
		expect(queryByText(product.name)).toBeInTheDocument();
	});
});
