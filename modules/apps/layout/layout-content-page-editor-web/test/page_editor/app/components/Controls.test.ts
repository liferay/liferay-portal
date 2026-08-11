/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ITEM_ACTIVATION_ORIGINS} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/itemActivationOrigins';
import {ITEM_TYPES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/itemTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {MULTI_SELECT_TYPES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/multiSelectTypes';
import {
	getItemsWithinRange,
	reducer,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {LayoutData} from '../../../../src/main/resources/META-INF/resources/page_editor/types/layout_data/LayoutData';

import '@testing-library/jest-dom';

const ACTION = {
	itemId: null,
	itemType: null,
	origin: ITEM_ACTIVATION_ORIGINS.layout,
};

const HOVER_ITEM = 'HOVER_ITEM' as const;
const MULTI_SELECT = 'MULTI_SELECT' as const;
const SELECT_ITEM = 'SELECT_ITEM' as const;

const STATE = {
	activationOrigin: ITEM_ACTIVATION_ORIGINS.layout,
	activeItemIds: [],
	activeItemType: null,
	hoveredItemId: null,
	hoveredItemType: null,
	rangeLimitIds: {},
};

const LAYOUT_DATA: LayoutData = {
	deletedItems: [],
	items: {
		collectionItem: {
			children: [],
			config: {},
			itemId: 'collectionItem',
			parentId: 'column',
			type: LAYOUT_DATA_ITEM_TYPES.collectionItem,
		},
		column: {
			children: ['fragment05', 'formStep', 'dropZone', 'collectionItem'],
			config: {
				landscapeMobile: {},
				portraitMobile: {},
				size: 12,
				tablet: {},
			},
			itemId: 'column',
			parentId: 'grid',
			type: LAYOUT_DATA_ITEM_TYPES.column,
		},
		container01: {
			children: ['fragment01', 'fragment02'],
			config: {
				htmlTag: 'header',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'container01',
			parentId: 'root',
			type: LAYOUT_DATA_ITEM_TYPES.container,
		},
		container02: {
			children: ['container03', 'fragment03', 'fragment04'],
			config: {
				htmlTag: 'header',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'container02',
			parentId: 'root',
			type: LAYOUT_DATA_ITEM_TYPES.container,
		},
		container03: {
			children: ['grid'],
			config: {
				htmlTag: 'header',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'container03',
			parentId: 'container02',
			type: LAYOUT_DATA_ITEM_TYPES.container,
		},
		dropZone: {
			children: [],
			config: {},
			itemId: 'dropZone',
			parentId: 'column',
			type: LAYOUT_DATA_ITEM_TYPES.fragmentDropZone,
		},
		formStep: {
			children: [],
			config: {},
			itemId: 'formStep',
			parentId: 'column',
			type: LAYOUT_DATA_ITEM_TYPES.formStep,
		},
		fragment01: {
			children: [],
			config: {
				fragmentEntryLinkId: '0',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'fragment01',
			parentId: 'container01',
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		},
		fragment02: {
			children: [],
			config: {
				fragmentEntryLinkId: '0',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'fragment02',
			parentId: 'container01',
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		},
		fragment03: {
			children: [],
			config: {
				fragmentEntryLinkId: '0',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'fragment03',
			parentId: 'container02',
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		},
		fragment04: {
			children: [],
			config: {
				fragmentEntryLinkId: '0',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'fragment04',
			parentId: 'container02',
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		},
		fragment05: {
			children: [],
			config: {
				fragmentEntryLinkId: '0',
				landscapeMobile: {},
				portraitMobile: {},
				tablet: {},
			},
			itemId: 'fragment05',
			parentId: 'column',
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		},
		grid: {
			children: ['column'],
			config: {landscapeMobile: {}, portraitMobile: {}, tablet: {}},
			itemId: 'grid',
			parentId: 'container03',
			type: LAYOUT_DATA_ITEM_TYPES.row,
		},
		root: {
			children: ['container01', 'container02'],
			config: {},
			itemId: 'root',
			parentId: '',
			type: LAYOUT_DATA_ITEM_TYPES.root,
		},
	},
	pageRules: [],
	rootItems: {
		dropZone: '',
		main: 'root',
	},
	version: '1.0',
};

describe('Reducer', () => {
	describe('Hover action', () => {
		it('hovers a fragment', () => {
			const action = {
				...ACTION,
				itemId: 'item-1',
				itemType: ITEM_TYPES.layoutDataItem,
				type: HOVER_ITEM,
			};
			const state = {
				...STATE,
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-1',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			});
		});

		it('hovers an editable when a fragment is selected', () => {
			const action = {
				...ACTION,
				itemId: 'editable-1',
				itemType: ITEM_TYPES.editable,
				type: HOVER_ITEM,
			};
			const state = {
				...STATE,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'editable-1',
				hoveredItemType: ITEM_TYPES.editable,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'editable-1',
				hoveredItemType: ITEM_TYPES.editable,
			});
		});

		it('hovers a mapped content', () => {
			const action = {
				...ACTION,
				itemId: 'mapped-content-1',
				itemType: ITEM_TYPES.mappedContent,
				type: HOVER_ITEM,
			};
			const state = {
				...STATE,
				activationOrigin: null,
				hoveredItemId: 'mapped-content-1',
				hoveredItemType: ITEM_TYPES.mappedContent,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activationOrigin: null,
				hoveredItemId: 'mapped-content-1',
				hoveredItemType: ITEM_TYPES.mappedContent,
			});
		});
	});

	describe('Hover out action', () => {
		it('hovers out a fragment', () => {
			const action = {
				...ACTION,
				itemType: ITEM_TYPES.layoutDataItem,
				type: HOVER_ITEM,
			};
			const state = {
				...STATE,
				hoveredItemId: 'item-1',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				hoveredItemId: null,
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			});
		});

		it('hovers out an editable', () => {
			const action = {
				...ACTION,
				itemType: ITEM_TYPES.layoutDataItem,
				type: HOVER_ITEM,
			};
			const state = {
				...STATE,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'editable-1',
				hoveredItemType: ITEM_TYPES.editable,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: null,
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			});
		});
	});

	describe('Select action', () => {
		it('selects a fragment which is hovered', () => {
			const action = {
				...ACTION,
				itemId: 'item-1',
				itemType: ITEM_TYPES.layoutDataItem,
				type: SELECT_ITEM,
			};
			const state = {
				...STATE,
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-1',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-1',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			});
		});

		it('selects a fragment which is already selected', () => {
			const action = {
				...ACTION,
				itemId: 'item-1',
				itemType: ITEM_TYPES.layoutDataItem,
				type: SELECT_ITEM,
			};
			const state = {
				...STATE,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-1',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-1',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			});
		});

		it('selects an editable when a fragment is selected', () => {
			const action = {
				...ACTION,
				itemId: 'editable-1',
				itemType: ITEM_TYPES.editable,
				type: SELECT_ITEM,
			};
			const state = {
				...STATE,
				activeItemIds: ['item-1'],
				hoveredItemId: 'editable-1',
				hoveredItemType: ITEM_TYPES.editable,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: ['editable-1'],
				activeItemType: ITEM_TYPES.editable,
				hoveredItemId: 'editable-1',
				hoveredItemType: ITEM_TYPES.editable,
			});
		});

		it('selects an item in page structure tree', () => {
			const action = {
				...ACTION,
				itemId: 'item-1',
				itemType: ITEM_TYPES.layoutDataItem,
				origin: ITEM_ACTIVATION_ORIGINS.sidebar,
				type: SELECT_ITEM,
			};
			const state = {
				...STATE,
				hoveredItemId: 'item-1',
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activationOrigin: ITEM_ACTIVATION_ORIGINS.sidebar,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-1',
			});
		});

		describe('Simple selection', () => {
			it('selects multiple items', () => {
				const action = {
					...ACTION,
					itemId: 'fragment01',
					multiSelect: MULTI_SELECT_TYPES.simple,
					type: SELECT_ITEM,
				};
				const state = {
					...STATE,
					activeItemIds: ['fragment04', 'fragment02'],
				};

				expect(reducer(state, action)).toEqual(
					expect.objectContaining({
						activeItemIds: [
							'fragment04',
							'fragment02',
							'fragment01',
						],
					})
				);
			});

			it('deselects an item if it is already selected', () => {
				const action = {
					...ACTION,
					itemId: 'fragment02',
					multiSelect: MULTI_SELECT_TYPES.simple,
					type: SELECT_ITEM,
				};
				const state = {
					...STATE,
					activeItemIds: ['fragment04', 'fragment02'],
				};

				expect(reducer(state, action)).toEqual(
					expect.objectContaining({
						activeItemIds: ['fragment04'],
					})
				);
			});
		});

		describe('Range selection', () => {
			it('selects in range when only one range limit is selected and there are more items selected', () => {
				const action = {
					...ACTION,
					itemId: 'fragment01',
					layoutData: LAYOUT_DATA,
					multiSelect: MULTI_SELECT_TYPES.range,
					type: SELECT_ITEM,
				};
				const state = {
					...STATE,
					activeItemIds: ['fragment03', 'fragment04', 'fragment02'],
					rangeLimitIds: {start: 'fragment02'},
				};

				expect(reducer(state, action)).toEqual(
					expect.objectContaining({
						activeItemIds: [
							'fragment03',
							'fragment04',
							'fragment02',
							'fragment01',
						],
					})
				);
			});

			it('selects in range when 2 range limits are selected and there are more items selected', () => {
				const action = {
					...ACTION,
					itemId: 'container02',
					layoutData: LAYOUT_DATA,
					multiSelect: MULTI_SELECT_TYPES.range,
					type: SELECT_ITEM,
				};
				const state = {
					...STATE,
					activeItemIds: [
						'fragment03',
						'fragment04',
						'fragment02',
						'fragment01',
					],
					rangeLimitIds: {end: 'fragment01', start: 'fragment02'},
				};

				expect(reducer(state, action)).toEqual(
					expect.objectContaining({
						activeItemIds: [
							'fragment03',
							'fragment04',
							'fragment02',
							'container02',
						],
					})
				);
			});

			it('selects the item when the range multiselection is activated before there are any items selected', () => {
				const action = {
					...ACTION,
					itemId: 'fragment01',
					layoutData: LAYOUT_DATA,
					multiSelect: MULTI_SELECT_TYPES.range,
					type: SELECT_ITEM,
				};
				const state = {
					...STATE,
					activeItemIds: [],
				};

				expect(reducer(state, action)).toEqual(
					expect.objectContaining({
						activeItemIds: ['fragment01'],
					})
				);
			});

			it('selects a single item when the start and the end of the range are the same id', () => {
				const action = {
					...ACTION,
					itemId: 'fragment01',
					layoutData: LAYOUT_DATA,
					multiSelect: MULTI_SELECT_TYPES.range,
					type: SELECT_ITEM,
				};
				const state = {
					...STATE,
					activeItemIds: ['fragment01', 'fragment02'],
					rangeLimitIds: {end: 'fragment01', start: 'fragment01'},
				};

				expect(reducer(state, action)).toEqual(
					expect.objectContaining({
						activeItemIds: ['fragment01'],
					})
				);
			});

			it('selects parents when range end is an editable', () => {
				const action = {
					...ACTION,
					itemId: 'editable02',
					layoutData: LAYOUT_DATA,
					multiSelect: MULTI_SELECT_TYPES.range,
					parentId: 'fragment02',
					type: SELECT_ITEM,
				};

				const state = {
					...STATE,
					activeItemIds: ['fragment01'],
				};

				expect(reducer(state, action)).toEqual(
					expect.objectContaining({
						activeItemIds: ['fragment01', 'fragment02'],
					})
				);
			});
		});
	});

	describe('Deselect action', () => {
		it('deselects a fragment', () => {
			const action = {
				...ACTION,
				itemType: ITEM_TYPES.layoutDataItem,
				type: SELECT_ITEM,
			};
			const state = {
				...STATE,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: [],
				activeItemType: ITEM_TYPES.layoutDataItem,
			});
		});

		it('deselects a fragment when other fragment is selected', () => {
			const action = {
				...ACTION,
				itemId: 'item-2',
				itemType: ITEM_TYPES.layoutDataItem,
				type: SELECT_ITEM,
			};
			const state = {
				...STATE,
				activeItemIds: ['item-1'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-2',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: ['item-2'],
				activeItemType: ITEM_TYPES.layoutDataItem,
				hoveredItemId: 'item-2',
				hoveredItemType: ITEM_TYPES.layoutDataItem,
			});
		});

		it('deselects an editable', () => {
			const action = {
				...ACTION,
				itemType: ITEM_TYPES.layoutDataItem,
				type: SELECT_ITEM,
			};
			const state = {
				...STATE,
				activeItemIds: ['editable-1'],
				activeItemType: ITEM_TYPES.editable,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: [],
				activeItemType: ITEM_TYPES.layoutDataItem,
			});
		});
	});

	describe('Multiselect action', () => {
		it('selects multiple fragments', () => {
			const state = {...STATE, activeItemIds: []};
			const action = {
				...ACTION,
				activeItemIds: ['item-1', 'item-2'],
				type: MULTI_SELECT,
			};

			expect(reducer(state, action)).toEqual({
				...state,
				activeItemIds: ['item-1', 'item-2'],
			});
		});
	});

	describe('getItemsWithinRange', () => {
		it('select range from top to bottom avoiding non-selectable elements', () => {
			expect(
				getItemsWithinRange({
					itemIds: LAYOUT_DATA.items.root.children,
					layoutDataItems: LAYOUT_DATA.items,
					rangeLimitIds: {end: 'fragment04', start: 'fragment02'},
				})
			).toEqual([
				'fragment02',
				'container02',
				'container03',
				'grid',
				'fragment05',
				'fragment03',
				'fragment04',
			]);
		});

		it('select range from bottom to top avoiding non-selectable elements', () => {
			expect(
				getItemsWithinRange({
					itemIds: LAYOUT_DATA.items.root.children,
					layoutDataItems: LAYOUT_DATA.items,
					rangeLimitIds: {end: 'fragment01', start: 'fragment03'},
				})
			).toEqual([
				'fragment01',
				'fragment02',
				'container02',
				'container03',
				'grid',
				'fragment05',
				'fragment03',
			]);
		});
	});
});
