/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, waitFor} from '@testing-library/react';
import React from 'react';

import ProductEditorSticker from '../../src/main/resources/META-INF/resources/js/ProductEditorSticker';

const mockFetch = jest.fn();

jest.mock('frontend-js-web', () => ({
	fetch: (...args: any[]) => mockFetch(...args),
}));

function mockImages(images: any[]) {
	mockFetch.mockResolvedValue({json: () => Promise.resolve({images})});
}

describe('ProductEditorSticker', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	it('renders the first image with a file', async () => {
		mockImages([
			{image: null},
			{image: {link: {href: '/first.png'}}},
			{image: {link: {href: '/second.png'}}},
		]);

		const {container} = render(
			<ProductEditorSticker
				objectEntryId="123"
				restContextPath="/o/c/basicproducts"
			/>
		);

		await waitFor(() =>
			expect(container.querySelector('img')).toHaveAttribute(
				'src',
				'/first.png'
			)
		);

		expect(mockFetch).toHaveBeenCalledWith(
			'/o/c/basicproducts/123?fields=images&nestedFields=images'
		);
	});

	it('renders the product icon when the product has no image with a file', async () => {
		mockImages([{image: null}]);

		const {container} = render(
			<ProductEditorSticker
				objectEntryId="123"
				restContextPath="/o/c/basicproducts"
			/>
		);

		await waitFor(() => expect(mockFetch).toHaveBeenCalled());

		expect(container.querySelector('img')).toBeNull();
		expect(
			container.querySelector('.lexicon-icon-product')
		).toBeInTheDocument();
	});

	it('renders the product icon without fetching for a new product', () => {
		const {container} = render(<ProductEditorSticker />);

		expect(mockFetch).not.toHaveBeenCalled();
		expect(container.querySelector('img')).toBeNull();
		expect(
			container.querySelector('.lexicon-icon-product')
		).toBeInTheDocument();
	});
});
