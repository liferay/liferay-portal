/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {fireEvent, render, screen} from '@testing-library/react';
import React, {Context, createContext} from 'react';

import GalleryView from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/views/GalleryView';

import {ICardSchema} from '@liferay/frontend-data-set-web';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/components/AssetPreview',
	() =>
		function AssetPreview({item}: any) {
			return <div data-testid="asset-preview">{item.id}</div>;
		}
);

jest.mock('@liferay/frontend-data-set-web', () => ({
	...(jest.requireActual('@liferay/frontend-data-set-web') as any),
	Card: function Card({item}: any) {
		return <div data-testid="card-item">{item.id}</div>;
	},
}));

const mockLiferayLanguageGet = jest.fn((key: string) => key);

(global as any).Liferay = {
	Language: {
		get: mockLiferayLanguageGet,
	},
	ThemeDisplay: {
		getPathThemeImages: () => '/path/to/images',
	},
};

class MockResizeObserver {
	disconnect() {}
	observe() {}
	unobserve() {}
}

(global as any).ResizeObserver = MockResizeObserver;

let mockThumbnailsWidth = 1100;

HTMLElement.prototype.getBoundingClientRect = jest.fn(
	() =>
		({
			bottom: 0,
			height: 0,
			left: 0,
			right: mockThumbnailsWidth,
			toJSON: () => ({}),
			top: 0,
			width: mockThumbnailsWidth,
			x: 0,
			y: 0,
		}) as DOMRect
);

const mockItems = Array.from({length: 10}, (_, i) => ({
	id: `item-${i}`,
	title: `Item ${i}`,
}));

const mockSchema = {
	title: 'title',
} as ICardSchema;

describe('GalleryView', () => {
	let frontendDataSetContext: Context<any>;

	beforeEach(() => {
		mockThumbnailsWidth = 1100;
		frontendDataSetContext = createContext({
			selectedItems: [],
		});
	});

	it('renders correctly with initial items', () => {
		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		expect(screen.getByTestId('asset-preview')).toHaveTextContent('item-0');
		expect(screen.getAllByTestId('card-item').length).toBe(5);
		expect(screen.getAllByTestId('card-item')[0]).toHaveTextContent(
			'item-0'
		);
	});

	describe('the number of thumbnails shown per container width', () => {
		it.each([
			{expectedCount: 1, width: 300},
			{expectedCount: 2, width: 450},
			{expectedCount: 3, width: 700},
			{expectedCount: 4, width: 900},
			{expectedCount: 5, width: 1100},
			{expectedCount: 6, width: 1300},
		])(
			'shows $expectedCount thumbnails when the strip is $width px wide',
			({expectedCount, width}) => {
				mockThumbnailsWidth = width;

				render(
					<GalleryView
						frontendDataSetContext={frontendDataSetContext}
						items={mockItems}
						schema={mockSchema}
					/>
				);

				expect(screen.getAllByTestId('card-item')).toHaveLength(
					expectedCount
				);
			}
		);
	});

	it('changes the preview when a thumbnail is clicked', () => {
		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		const secondThumbnail = screen.getAllByTestId('card-item')[1];
		fireEvent.click(secondThumbnail.parentElement!);

		expect(screen.getByTestId('asset-preview')).toHaveTextContent('item-1');
	});

	it('navigates to the next item when the next button is clicked', () => {
		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		const nextButton = screen.getByLabelText('next');
		fireEvent.click(nextButton);

		expect(screen.getByTestId('asset-preview')).toHaveTextContent('item-1');
	});

	it('navigates to the previous item when the previous button is clicked', () => {
		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		const nextButton = screen.getByLabelText('next');
		fireEvent.click(nextButton);

		const prevButton = screen.getByLabelText('previous');
		fireEvent.click(prevButton);

		expect(screen.getByTestId('asset-preview')).toHaveTextContent('item-0');
	});

	it('disables navigation buttons when there is only one item', () => {
		const fewItems = mockItems.slice(0, 1);
		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={fewItems}
				schema={mockSchema}
			/>
		);

		expect(screen.getByLabelText('previous')).toBeDisabled();
		expect(screen.getByLabelText('next')).toBeDisabled();
	});

	it('shows an empty state when multiple items are selected', () => {
		frontendDataSetContext = createContext({
			selectedItems: [mockItems[0], mockItems[1]],
		});

		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		expect(
			screen.getByText('select-a-single-file-to-preview-its-content')
		).toBeInTheDocument();
		expect(screen.getByText('no-preview-available')).toBeInTheDocument();

		expect(
			document.querySelector('img[src*="_reduced_motion"]')
		).not.toBeInTheDocument();
	});

	it('changes the preview when a thumbnail is focused and Enter key is pressed', () => {
		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		const secondThumbnail =
			screen.getAllByTestId('card-item')[1].parentElement!;
		fireEvent.keyDown(secondThumbnail, {code: 'Enter', key: 'Enter'});

		expect(screen.getByTestId('asset-preview')).toHaveTextContent('item-1');
	});

	it('changes the preview when a thumbnail is focused and Space key is pressed', () => {
		render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		const secondThumbnail =
			screen.getAllByTestId('card-item')[1].parentElement!;
		fireEvent.keyDown(secondThumbnail, {code: 'Space', key: ' '});

		expect(screen.getByTestId('asset-preview')).toHaveTextContent('item-1');
	});

	it('checks the accessibility', async () => {
		const {container} = render(
			<GalleryView
				frontendDataSetContext={frontendDataSetContext}
				items={mockItems}
				schema={mockSchema}
			/>
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
