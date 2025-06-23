/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {
	MarketplaceView,
	useMarketplaceContext,
} from '@liferay/marketplace-js-components-web';

import MarketplaceTabItem from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/MarketplaceTabItem';

global.Liferay = {
	Language: {get: (key) => key},
};

jest.mock('@liferay/marketplace-js-components-web', () => ({
	...jest.requireActual('@liferay/marketplace-js-components-web'),
	useMarketplaceContext: jest.fn(),
}));

const mockItem = {
	catalogName: 'Test Catalog',
	name: 'Test Item',
	urlImage: 'test-image.jpg',
};

const mockContext = {
	modal: {onOpenChange: jest.fn()},
	setProduct: jest.fn(),
	setView: jest.fn(),
};

function renderMarketPlaceTabItem({onClickRef, item = mockItem} = {}) {
	return render(<MarketplaceTabItem item={item} onClickRef={onClickRef} />);
}

describe('MarketplaceTabItem', () => {
	beforeEach(() => {
		useMarketplaceContext.mockReturnValue(mockContext);
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders item details with correct name, catalog, and image', () => {
		renderMarketPlaceTabItem();

		expect(screen.getByTitle('Test Item-details')).toBeInTheDocument();

		expect(screen.getByText('Test Item')).toBeInTheDocument();
		expect(screen.getByText('Test Catalog')).toBeInTheDocument();
		expect(screen.getByRole('img')).toHaveAttribute(
			'src',
			'test-image.jpg'
		);
		expect(screen.getByRole('img')).toHaveAttribute('alt', '');
	});

	it('renders the card with correct classNames', () => {
		const {container} = renderMarketPlaceTabItem();
		expect(
			container.querySelector('.card-interactive')
		).toBeInTheDocument();
		expect(
			container.querySelector('.card-interactive-primary')
		).toBeInTheDocument();
		expect(
			container.querySelector('.card-type-template')
		).toBeInTheDocument();
		expect(
			container.querySelector('.template-card-horizontal')
		).toBeInTheDocument();
		expect(container.querySelector('.card-title')).toBeInTheDocument();
		expect(container.querySelector('.card-subtitle')).toBeInTheDocument();

		const useElement = container.querySelector('use');
		expect(useElement.getAttribute('href')).toBe('#angle-right');
	});

	it('calls context functions on card click', () => {
		const onClickRef = React.createRef();

		renderMarketPlaceTabItem({onClickRef});

		onClickRef?.current?.();

		expect(mockContext.setProduct).toHaveBeenCalledWith(mockItem);
		expect(mockContext.setView).toHaveBeenCalledWith(
			MarketplaceView.STOREFRONT
		);
		expect(mockContext.modal.onOpenChange).toHaveBeenCalledWith(true);
	});
});
