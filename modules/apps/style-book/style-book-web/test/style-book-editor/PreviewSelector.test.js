/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useIsMobileDevice} from '@clayui/shared';
import {act, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {LayoutSelector} from '../../src/main/resources/META-INF/resources/js/style-book-editor/LayoutSelector';
import {LayoutTypeSelector} from '../../src/main/resources/META-INF/resources/js/style-book-editor/LayoutTypeSelector';
import PreviewSelector from '../../src/main/resources/META-INF/resources/js/style-book-editor/PreviewSelector';
import {LAYOUT_TYPES} from '../../src/main/resources/META-INF/resources/js/style-book-editor/constants/layoutTypes';
import {LayoutContextProvider} from '../../src/main/resources/META-INF/resources/js/style-book-editor/contexts/LayoutContext';
import openItemSelector from '../../src/main/resources/META-INF/resources/js/style-book-editor/openItemSelector';

jest.mock('@clayui/shared', () => ({
	...jest.requireActual('@clayui/shared'),
	useIsMobileDevice: jest.fn(),
}));

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/openItemSelector',
	() => jest.fn(() => {})
);

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/config',
	() => ({
		config: {
			fragmentCollectionPreviewURL: 'http://localhost/preview',
			namespace: '_ns_',
			previewOptions: [
				{
					data: {
						itemSelectorURL: 'master-item-selector-url',
						recentLayouts: [],
						totalLayouts: 0,
					},
					type: 'master',
				},
				{
					data: {
						itemSelectorURL: 'page-item-selector-url',
						recentLayouts: [
							{
								id: '1',
								name: 'Page 1',
								private: false,
								url: 'page-1-url',
							},
							{
								name: 'Page 2',
								private: false,
								url: 'page-2-url',
							},
							{
								name: 'Page 3',
								private: false,
								url: 'page-3-url',
							},
							{
								name: 'Page 4',
								private: true,
								url: 'page-4-url',
							},
						],
						totalLayouts: 6,
					},
					type: 'page',
				},
				{
					data: {
						itemSelectorURL: 'page-template-item-selector-url',
						recentLayouts: [
							{
								name: 'Page Template 1',
								private: false,
								url: 'page-template-1-url',
							},
						],
						totalLayouts: 1,
					},
					type: 'pageTemplate',
				},
				{
					data: {
						itemSelectorURL: 'display-page-item-selector-url',
						recentLayouts: [
							{
								name: 'Display Page 1',
								private: false,
								url: 'display-page-1-url',
							},
						],
						totalLayouts: 1,
					},
					type: 'displayPageTemplate',
				},
				{
					data: {
						itemSelectorURL: 'fragment-collection-selector-url',
						recentLayouts: [
							{
								fragmentCollectionKey: 'collection-1',
								groupId: 10,
								name: 'Fragment Collection 1',
								private: false,
								url: 'fragment-collection-1-url',
							},
							{
								fragmentCollectionKey: 'collection-2',
								groupId: 0,
								name: 'Fragment Collection 2',
								private: false,
								url: 'fragment-collection-2-url',
							},
						],
						totalLayouts: 5,
					},
					type: 'fragmentCollection',
				},
			],
		},
	})
);

const renderPreviewSelector = (layoutType = LAYOUT_TYPES.page) => {
	return render(
		<>
			<LayoutTypeSelector
				layoutType={layoutType}
				setLayoutType={() => {}}
			/>
			<LayoutSelector layoutType={layoutType} />
		</>
	);
};

const renderLayoutSelector = (layoutType, previewLayout) => {
	return render(
		<LayoutContextProvider
			initialState={{previewLayout, previewLayoutType: layoutType}}
		>
			<LayoutSelector layoutType={layoutType} />
		</LayoutContextProvider>
	);
};

const pickFromMore = (layoutType, previewLayout, item) => {
	const renderResult = renderLayoutSelector(layoutType, previewLayout);

	const {getByRole} = renderResult;

	const trigger = getByRole('button');

	fireEvent.click(trigger);
	fireEvent.click(getByRole('button', {name: 'more'}));

	act(() => {
		openItemSelector.mock.calls[0][0].callback(item);
	});

	fireEvent.click(trigger);

	return renderResult;
};

describe('PreviewSelector', () => {
	beforeEach(() => {
		useIsMobileDevice.mockReturnValue(false);
	});

	afterEach(() => {
		openItemSelector.mockClear();
	});

	it('renders an icon dropdown on small screens', () => {
		useIsMobileDevice.mockReturnValue(true);

		const {getByRole} = render(
			<LayoutContextProvider
				initialState={{
					previewLayout: {
						name: 'Page 1',
						url: 'page-1-url',
					},
					previewLayoutType: LAYOUT_TYPES.page,
				}}
			>
				<PreviewSelector />
			</LayoutContextProvider>
		);

		expect(getByRole('button', {name: /preview/i})).toBeInTheDocument();
	});

	it('renders inline dropdowns on large screens', () => {
		useIsMobileDevice.mockReturnValue(false);

		const {queryByRole} = render(
			<LayoutContextProvider
				initialState={{
					previewLayout: {
						name: 'Page 1',
						url: 'page-1-url',
					},
					previewLayoutType: LAYOUT_TYPES.page,
				}}
			>
				<PreviewSelector />
			</LayoutContextProvider>
		);

		expect(
			queryByRole('button', {name: /preview/i})
		).not.toBeInTheDocument();
	});

	it('does not show layout type if it does not have at least one item', () => {
		const {queryByText} = renderPreviewSelector();

		expect(queryByText('masters')).not.toBeInTheDocument();
	});

	it('shows correct items in layout selector when selecting a type', () => {
		const {getByText} = renderPreviewSelector(
			LAYOUT_TYPES.displayPageTemplate
		);

		expect(getByText('Display Page 1')).toBeInTheDocument();
	});

	it('shows More button and number of items info when selected type has more than 4 items', () => {
		const {getByText} = renderPreviewSelector();

		expect(getByText('more')).toBeInTheDocument();
		expect(getByText('showing-x-of-x-items')).toBeInTheDocument();
	});

	it('does not show More button and number of items info when selected type has 4 items or less', () => {
		const {queryByText} = renderPreviewSelector(LAYOUT_TYPES.pageTemplate);

		expect(queryByText('more')).not.toBeInTheDocument();
		expect(queryByText('showing-x-of-x-items')).not.toBeInTheDocument();
	});

	it('calls openItemSelector with correct url when clicking More button', () => {
		const {getByText} = renderPreviewSelector();

		fireEvent.click(getByText('more'));

		expect(openItemSelector).toHaveBeenCalledWith(
			expect.objectContaining({
				itemSelectorURL: 'page-item-selector-url',
			})
		);
	});

	describe('restricted page icon', () => {
		it('is not shown for fragment collections without guest view permission', () => {
			const {getByRole, queryAllByLabelText} = renderLayoutSelector(
				LAYOUT_TYPES.fragmentCollection,
				{name: 'Preview', url: 'preview-url'}
			);

			fireEvent.click(getByRole('button'));

			expect(queryAllByLabelText('restricted-page')).toHaveLength(0);
		});

		it('is shown for a page picked from More without guest view permission', () => {
			const {getByLabelText} = pickFromMore(
				LAYOUT_TYPES.page,
				{name: 'Page 1', url: 'page-1-url'},
				{
					hasGuestViewPermission: false,
					value: JSON.stringify({
						name: 'Restricted Page',
						plid: 99,
						previewURL: 'http://localhost/restricted-page',
					}),
				}
			);

			expect(getByLabelText('restricted-page')).toBeInTheDocument();
		});
	});

	describe('items picked from More', () => {
		const fragmentCollectionPreview = {
			name: 'Fragment Collection 1',
			url: 'fragment-collection-1-url',
		};

		it('does not list a fragment collection twice when it matches by key and group', () => {
			const {getAllByRole} = pickFromMore(
				LAYOUT_TYPES.fragmentCollection,
				fragmentCollectionPreview,
				{
					value: JSON.stringify({
						fragmentCollectionKey: 'collection-1',
						groupId: 10,
						name: 'Fragment Collection 1',
					}),
				}
			);

			expect(
				getAllByRole('menuitem').map((menuItem) => menuItem.textContent)
			).toEqual(['Fragment Collection 1', 'Fragment Collection 2']);
		});

		it('lists a fragment collection with the same key in another group separately', () => {
			const {getAllByRole} = pickFromMore(
				LAYOUT_TYPES.fragmentCollection,
				fragmentCollectionPreview,
				{
					value: JSON.stringify({
						fragmentCollectionKey: 'collection-1',
						groupId: 20,
						name: 'Other Group Collection 1',
					}),
				}
			);

			expect(
				getAllByRole('menuitem').map((menuItem) => menuItem.textContent)
			).toEqual(['Other Group Collection 1', 'Fragment Collection 1']);
		});

		it('does not list a page twice when it matches by id', () => {
			const {getAllByRole} = pickFromMore(
				LAYOUT_TYPES.page,
				{name: 'Page 2', url: 'page-2-url'},
				{
					value: JSON.stringify({
						name: 'Page 1',
						plid: 1,
						previewURL: 'http://localhost/page-1-other-url',
					}),
				}
			);

			expect(
				getAllByRole('menuitem').map((menuItem) => menuItem.textContent)
			).toEqual(['Page 1', 'Page 2', 'Page 3', 'Page 4']);
		});
	});
});
