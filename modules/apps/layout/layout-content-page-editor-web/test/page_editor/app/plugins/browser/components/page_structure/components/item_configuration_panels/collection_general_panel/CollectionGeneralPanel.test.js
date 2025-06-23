/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {COLLECTION_FILTER_FRAGMENT_ENTRY_KEY} from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/collectionFilterFragmentEntryKey';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreAPIContextProvider} from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CollectionService from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService';
import updateItemConfig from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';
import {
	CACHE_KEYS,
	disposeCache,
	initializeCache,
	setCacheItem,
} from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/cache';
import CollectionSelector from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/common/components/CollectionSelector';
import {CollectionGeneralPanel} from '../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/collection_general_panel/CollectionGeneralPanel';

jest.mock(
	'../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableLanguages: {
				en_US: {
					default: false,
					displayName: 'English (United States)',
					languageIcon: 'en-us',
					languageId: 'en_US',
					w3cLanguageId: 'en-US',
				},
			},
			commonStyles: [],
			searchContainerPageMaxDelta: '50',
		},
	})
);

jest.mock(
	'../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService',
	() => ({
		getCollectionItemCount: jest.fn(() =>
			Promise.resolve({
				totalNumberOfItems: 32,
			})
		),
		getCollectionVariations: jest.fn(() => Promise.resolve([])),
		getCollectionWarningMessage: jest.fn(() =>
			Promise.resolve({warningMessage: null})
		),
	})
);

jest.mock(
	'../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/common/components/CollectionSelector',
	() => jest.fn(() => null)
);

jest.mock(
	'../../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

const DEFAULT_ITEM_CONFIG = {
	collection: {
		classNameId: '22101',
		classPK: '40724',
		title: 'collection1',
	},
	displayAllItems: false,
	displayAllPages: false,
	gutters: false,
	listStyle: '',
	numberOfColumns: 1,
	numberOfItems: 5,
	numberOfItemsPerPage: 5,
	numberOfPages: 1,
	paginationType: 'numeric',
	verticalAlignment: 'start',
};

const renderComponent = ({
	itemConfig = {},
	fragmentEntryLinks = {},
	itemId = '0',
	layoutData = {},
	selectedViewportSize = 'desktop',
} = {}) => {
	return render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				fragmentEntryLinks,
				languageId: 'en_US',
				layoutData,
				permissions: {UPDATE: true},
				restrictedItemIds: new Set(),
				segmentsExperienceId: '0',
				selectedViewportSize,
			})}
		>
			<CollectionGeneralPanel
				item={{
					children: [],
					config: {...DEFAULT_ITEM_CONFIG, ...itemConfig},
					itemId,
					parentId: '',
					type: LAYOUT_DATA_ITEM_TYPES.collection,
				}}
			/>
		</StoreAPIContextProvider>
	);
};

describe('CollectionGeneralPanel', () => {
	beforeEach(() => {
		CollectionService.getCollectionVariations.mockClear();

		disposeCache();
		initializeCache();
	});

	it('allows changing the Gutter select', async () => {
		await act(async () => {
			renderComponent({
				itemConfig: {
					numberOfColumns: 2,
				},
			});
		});

		const input = screen.getByLabelText('show-gutter');

		await userEvent.click(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {gutters: true},
			itemIds: ['0'],
		});
	});

	it('allows changing the Vertical Alignment select', async () => {
		await act(async () => {
			renderComponent();
		});

		const input = screen.getByLabelText('vertical-alignment');

		await userEvent.selectOptions(input, 'center');
		fireEvent.change(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				verticalAlignment: 'center',
			},
			itemIds: ['0'],
		});
	});

	it('hides vertical alignment and layout selects when flex is selected', async () => {
		await act(async () => {
			renderComponent({itemConfig: {listStyle: 'flex-column'}});
		});

		expect(
			screen.queryByLabelText('vertical-alignment')
		).not.toBeInTheDocument();
		expect(screen.queryByLabelText('layout')).not.toBeInTheDocument();
	});

	it('hides flex options when flex is not selected', async () => {
		await act(async () => {
			renderComponent();
		});

		expect(screen.queryByLabelText('flex-wrap')).not.toBeInTheDocument();
		expect(screen.queryByLabelText('align-items')).not.toBeInTheDocument();
		expect(
			screen.queryByLabelText('justify-content')
		).not.toBeInTheDocument();
	});

	it('allows changing the Show Empty Collection Alert checkbox', async () => {
		await act(async () => {
			renderComponent();
		});

		const input = screen.getByLabelText('show-empty-collection-alert');

		await userEvent.click(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: expect.objectContaining({
				emptyCollectionOptions: {
					displayMessage: false,
				},
			}),
			itemIds: ['0'],
		});
	});

	it('allows changing the Empty Collection Alert input', async () => {
		await act(async () => {
			renderComponent();
		});

		const input = screen.getByLabelText('empty-collection-alert');

		await userEvent.clear(input);
		await userEvent.type(input, 'Hello world!');

		act(() => {
			fireEvent.blur(input);
		});

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: expect.objectContaining({
				emptyCollectionOptions: {
					message: {
						['en_US']: 'Hello world!',
					},
				},
			}),
			itemIds: ['0'],
		});
	});

	it('allows changing the Pagination select', async () => {
		await act(async () => {
			renderComponent();
		});

		const input = screen.getByLabelText('pagination');

		await userEvent.selectOptions(input, 'none');
		fireEvent.change(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				paginationType: 'none',
			},
			itemIds: ['0'],
		});
	});

	it('allows changing the Display All Collection Items checkbox', async () => {
		await act(async () => {
			renderComponent({itemConfig: {paginationType: 'none'}});
		});

		const input = screen.getByLabelText('display-all-collection-items');

		await act(async () => {
			await userEvent.click(input);
		});

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: expect.objectContaining({
				displayAllItems: true,
			}),
			itemIds: ['0'],
		});
	});

	it('shows a warning message from backend when collection has some problematic configuration', async () => {
		setCacheItem({
			data: {
				description: 'page-performance-warning-and-stuff',
				title: '',
			},
			key: [
				CACHE_KEYS.collectionWarningMessage,
				'0',
				'0',
				JSON.stringify({...DEFAULT_ITEM_CONFIG}),
			].join('-'),
			status: 'saved',
		});

		await act(async () => {
			renderComponent();
		});

		expect(
			await screen.findByText('page-performance-warning-and-stuff')
		).toBeInTheDocument();
	});

	it('allows changing the Display All Pages checkbox', async () => {
		await act(async () => {
			renderComponent();
		});

		const input = screen.getByLabelText('display-all-pages');

		await userEvent.click(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: expect.objectContaining({
				displayAllPages: true,
			}),
			itemIds: ['0'],
		});
	});

	it('allows changing Layout for a given viewport', async () => {
		await act(async () => {
			renderComponent({
				selectedViewportSize: 'tablet',
			});
		});

		const input = screen.getByLabelText('layout');

		await userEvent.type(input, '1');
		fireEvent.change(input);

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				tablet: {numberOfColumns: '1'},
			},
			itemIds: ['0'],
		});
	});

	it('shows variations popover trigger with the correct number of variations when the collection has variations', async () => {
		CollectionService.getCollectionVariations.mockImplementation(() =>
			Promise.resolve(['Variation 1', 'Variation 2'])
		);

		await act(async () => {
			renderComponent();
		});

		expect(await screen.findByText('2-variations')).toBeInTheDocument();
	});

	it('shows correct variation names in variations popover', async () => {
		CollectionService.getCollectionVariations.mockImplementation(() =>
			Promise.resolve(['Variation 1', 'Variation 2'])
		);

		await act(async () => {
			renderComponent();
		});

		const popoverTrigger = await screen.findByText('2-variations');

		await userEvent.click(popoverTrigger);

		expect(screen.getByText('Variation 1')).toBeInTheDocument();
		expect(screen.getByText('Variation 2')).toBeInTheDocument();
	});

	it('does not show variations popover when the collection does not have variations', async () => {
		await act(async () => {
			renderComponent();
		});

		expect(screen.queryByText('0-variations')).not.toBeInTheDocument();
	});

	describe('Number of Items Input', () => {
		it('allows changing input value', async () => {
			await act(async () => {
				renderComponent({itemConfig: {paginationType: 'none'}});
			});

			const input = screen.getByLabelText(
				'maximum-number-of-items-to-display'
			);

			await userEvent.clear(input);
			await userEvent.type(input, '3');

			await act(async () => {
				fireEvent.blur(input);
			});

			expect(updateItemConfig).toHaveBeenCalledWith({
				itemConfig: {
					numberOfItems: 3,
				},
				itemIds: ['0'],
			});
		});

		it('shows a warning message when the number of items is bigger than the total items of the collection', async () => {
			await act(async () => {
				renderComponent({
					itemConfig: {
						numberOfItems: 33,
						paginationType: 'none',
					},
				});
			});

			expect(
				await screen.findByText('this-collection-has-32-items')
			).toBeInTheDocument();
		});
	});

	describe('Number of Pages Input', () => {
		it('allows changing input value', async () => {
			await act(async () => {
				renderComponent();
			});

			const input = screen.getByLabelText(
				'maximum-number-of-pages-to-display'
			);

			await userEvent.clear(input);
			await userEvent.type(input, '3');

			act(() => {
				fireEvent.blur(input);
			});

			expect(updateItemConfig).toHaveBeenCalledWith({
				itemConfig: {
					numberOfPages: 3,
				},
				itemIds: ['0'],
			});
		});
	});

	describe('Number of Items per Page Input', () => {
		it('allows changing the input value', async () => {
			await act(async () => {
				renderComponent();
			});

			const input = screen.getByLabelText(
				'maximum-number-of-items-per-page'
			);

			await userEvent.clear(input);
			await userEvent.type(input, '2');

			act(() => {
				fireEvent.blur(input);
			});

			expect(updateItemConfig).toHaveBeenCalledWith({
				itemConfig: {
					numberOfItemsPerPage: 2,
				},
				itemIds: ['0'],
			});
		});

		it('shows a warning message when the number of items per page is bigger than searchContainerPageMaxDelta', async () => {
			await act(async () => {
				renderComponent({
					itemConfig: {
						numberOfItemsPerPage: 53,
					},
				});
			});

			expect(
				await screen.findByText(
					'a-maximum-of-50-items-per-page-is-allowed'
				)
			).toBeInTheDocument();
		});
	});

	describe('Collection Display with filtering', () => {
		let globalConfirm;

		beforeAll(() => {
			window.Liferay = {
				...Liferay,
				CustomDialogs: {},
			};
		});

		beforeEach(() => {
			globalConfirm = window.confirm;
			window.confirm = jest.fn();
		});

		afterEach(() => {
			window.confirm = globalConfirm;
		});

		it('shows a confirmation when updating a collection linked to a filter', async () => {
			CollectionSelector.mockImplementation(
				({onBeforeCollectionSelect}) => {
					onBeforeCollectionSelect({preventDefault: () => {}});

					return <h1>Collection Selector</h1>;
				}
			);

			await act(async () => {
				renderComponent({
					fragmentEntryLinks: {
						'collection-filter-fragment-a': {
							editableValues: {
								[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
									targetCollections: ['collection-display-a'],
								},
							},
							fragmentEntryKey:
								COLLECTION_FILTER_FRAGMENT_ENTRY_KEY,
						},
					},

					itemId: 'collection-display-a',
					layoutData: {
						deletedItems: [],
						items: {
							'collection-display-a': {
								itemId: 'collection-display-a',
								type: LAYOUT_DATA_ITEM_TYPES.collection,
							},
							'collection-filter-a': {
								config: {
									fragmentEntryLinkId:
										'collection-filter-fragment-a',
								},
								itemId: 'collection-filter-a',
								type: LAYOUT_DATA_ITEM_TYPES.fragment,
							},
						},
					},
				});
			});

			await screen.findByText('Collection Selector');

			expect(confirm).toHaveBeenCalledWith(
				'if-you-change-the-collection-you-unlink-the-collection-filter\n\ndo-you-want-to-continue'
			);
		});
	});
});
