/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getAllEditables from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_content/getAllEditables';
import getAllPortals from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/getAllPortals';
import {ITEM_TYPES} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/itemTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import selectLayoutDataItemLabel from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/selectors/selectLayoutDataItemLabel';
import isMapped from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/editable_value/isMapped';
import isMappedToCollection from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/editable_value/isMappedToCollection';
import isHideable from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/isHideable';
import {isItemHidden} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/isItemHidden';
import isRemovable from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/isRemovable';
import getTreeNodes, {
	getCollectionAncestor,
} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/getTreeNodes';

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_content/getAllEditables'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/getAllPortals'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor',
	() => ({
		EDITABLE_FRAGMENT_ENTRY_PROCESSOR: 'mock-processor',
	})
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/isItemHidden'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/isRemovable'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/isHideable'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/selectors/selectLayoutDataItemLabel'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/editable_value/isMapped'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/editable_value/isMappedToCollection'
);

const originalConsoleError = console.error;
const rootId = 'root';

describe('getTreeNodes', () => {
	beforeEach(() => {
		console.error = jest.fn();
	});

	afterAll(() => {
		console.error = originalConsoleError;
	});

	test('should correctly build a single-node tree for a root item', () => {
		const itemId = rootId;
		const mockItem = {
			children: [],
			itemId,
			parentId: null,
			type: LAYOUT_DATA_ITEM_TYPES.root,
		};
		const mockItems = {
			[rootId]: mockItem,
		};

		isRemovable.mockReturnValue(false);
		isHideable.mockReturnValue(false);
		selectLayoutDataItemLabel.mockReturnValue(rootId);
		isItemHidden.mockReturnValue(false);

		const mockOptions = {
			layoutData: {
				items: mockItems,
			},
			layoutDataRef: {
				current: {
					items: mockItems,
				},
			},
		};

		const result = getTreeNodes(mockItem, mockItems, mockOptions);

		expect(result.id).toBe(itemId);
		expect(result.name).toBe(rootId);
		expect(result.itemType).toBe(ITEM_TYPES.layoutDataItem);
		expect(result.children).toHaveLength(0);
		expect(result.removable).toBe(false);
		expect(result.hideable).toBe(false);
	});

	test('should correctly build a tree node for a simple container item', () => {
		const itemId = 'container-1';
		const mockItem = {
			children: [],
			config: {},
			itemId,
			parentId: rootId,
			type: LAYOUT_DATA_ITEM_TYPES.container,
		};
		const mockItems = {
			[itemId]: mockItem,
		};
		const mockOptions = {
			canUpdateItemConfiguration: true,
			isMasterPage: false,
		};

		isItemHidden.mockReturnValue(false);
		isRemovable.mockReturnValue(true);
		isHideable.mockReturnValue(true);
		selectLayoutDataItemLabel.mockReturnValue('Container');

		const result = getTreeNodes(mockItem, mockItems, mockOptions);

		expect(result.id).toBe(itemId);
		expect(result.type).toBe(LAYOUT_DATA_ITEM_TYPES.container);
		expect(result.itemType).toBe(ITEM_TYPES.layoutDataItem);
		expect(result.children).toHaveLength(0);
		expect(result.removable).toBe(true);
		expect(result.hideable).toBe(true);
		expect(result.name).toBe('Container');
	});

	test('should correctly handle a fragment with both editables and drop-zones', () => {
		const childOfDropzone = 'child-of-dropzone';
		const mockFragmentId = 'fragment-1';
		const mockDropZoneId = 'dropzone-1';
		const mockEditableId = 'editable-text-1';
		const fragmentEntryLinkId = 'link-1';

		const mockFragmentItem = {
			config: {
				fragmentEntryLinkId,
			},
			itemId: mockFragmentId,
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		};

		const mockDropZoneItem = {
			children: [childOfDropzone],
			itemId: mockDropZoneId,
			type: LAYOUT_DATA_ITEM_TYPES.fragmentDropZone,
		};

		const mockChildOfDropZone = {
			children: [],
			config: {},
			itemId: childOfDropzone,
			type: LAYOUT_DATA_ITEM_TYPES.container,
		};

		const mockItems = {
			[childOfDropzone]: mockChildOfDropZone,
			[mockDropZoneId]: mockDropZoneItem,
			[mockFragmentId]: mockFragmentItem,
		};

		const mockFragmentEntryLink = {
			content: '<div></div>',
			editableTypes: {
				[mockEditableId]: 'text',
			},
			editableValues: {
				'mock-processor': {
					[mockEditableId]: {
						mappedField: 'title',
					},
				},
			},
		};

		const mockOptions = {
			fragmentEntryLinks: {
				[fragmentEntryLinkId]: mockFragmentEntryLink,
			},
			isMasterPage: false,
			layoutData: {
				items: mockItems,
			},
			layoutDataRef: {current: {items: mockItems}},
			mappingFields: {
				title: 'Mock Label',
			},
		};

		getAllEditables.mockReturnValue([{editableId: mockEditableId}]);
		getAllPortals.mockReturnValue([
			{dropZoneId: mockDropZoneId, mainItemId: mockDropZoneId},
		]);
		isMapped.mockReturnValue(true);
		isMappedToCollection.mockReturnValue(false);
		isItemHidden.mockReturnValue(false);
		isRemovable.mockReturnValue(true);
		isHideable.mockReturnValue(true);
		selectLayoutDataItemLabel.mockReturnValue('Fragment');

		const result = getTreeNodes(mockFragmentItem, mockItems, mockOptions);

		expect(result.id).toBe(mockFragmentId);
		expect(result.type).toBe(LAYOUT_DATA_ITEM_TYPES.fragment);
		expect(result.itemType).toBe(ITEM_TYPES.layoutDataItem);
		expect(result.children).toHaveLength(2);

		const editableNode = result.children.find(
			(c) => c.itemType === ITEM_TYPES.editable
		);
		expect(editableNode).toBeDefined();
		expect(editableNode.id).toBe(
			`${fragmentEntryLinkId}-${mockEditableId}`
		);
		expect(editableNode.name).toBe(mockEditableId);

		const dropZoneNode = result.children.find(
			(c) => c.type === LAYOUT_DATA_ITEM_TYPES.fragmentDropZone
		);
		expect(dropZoneNode).toBeDefined();
		expect(dropZoneNode.id).toBe(mockDropZoneId);
		expect(dropZoneNode.children).toHaveLength(1);
	});

	test('should throw a TypeError for a null item', () => {
		process.env.NODE_ENV = 'development';
		const mockItems = {};

		let errorThrown = false;
		try {
			getTreeNodes(null, mockItems, {});
		}
		catch (error) {
			errorThrown = true;
			expect(error).toBeInstanceOf(TypeError);
		}

		expect(errorThrown).toBe(true);
	});

	test('should throw an error and log in development mode for a fragment with a missing fragmentEntryLink', () => {
		const fragmentId = 'fragment-1';
		const missingFragmentEntryLinkId = 'non-existent-link';
		const mockFragmentItem = {
			children: [],
			config: {
				fragmentEntryLinkId: missingFragmentEntryLinkId,
			},
			itemId: fragmentId,
			parentId: rootId,
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		};

		const mockItems = {
			[fragmentId]: mockFragmentItem,
		};
		const mockOptions = {
			canUpdateEditables: true,
			fragmentEntryLinks: {},
			layoutData: {
				items: mockItems,
			},
			layoutDataRef: {current: {items: mockItems}},
		};

		let errorThrown = false;
		try {
			getTreeNodes(mockFragmentItem, mockItems, mockOptions);
		}
		catch (error) {
			errorThrown = true;
			expect(error).toBeInstanceOf(TypeError);
		}

		expect(errorThrown).toBe(true);
		expect(console.error).toHaveBeenCalledWith(
			`No fragment entry link with id ${missingFragmentEntryLinkId} found for fragment id ${fragmentId}.`
		);
	});

	test('should throw an error and log in development mode when dropzone is missing a uuid', () => {
		const mockFragmentId = 'fragment-with-bad-dropzone';
		const fragmentEntryLinkId = 'link-1';

		const mockFragmentItem = {
			config: {
				fragmentEntryLinkId,
			},
			itemId: mockFragmentId,
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		};
		const mockItems = {
			[mockFragmentId]: mockFragmentItem,
		};
		const mockFragmentEntryLink = {
			content: '<div></div>',
			editableTypes: {},
			editableValues: {},
		};
		const mockOptions = {
			fragmentEntryLinks: {
				[fragmentEntryLinkId]: mockFragmentEntryLink,
			},
			layoutData: {
				items: mockItems,
			},
			layoutDataRef: {current: {items: mockItems}},
		};

		getAllEditables.mockReturnValue([]);
		getAllPortals.mockReturnValue([
			{dropZoneId: 'bad-dropzone-1', priority: 1},
		]);

		let errorThrown = false;
		try {
			getTreeNodes(mockFragmentItem, mockItems, mockOptions);
		}
		catch (error) {
			errorThrown = true;
			expect(error).toBeInstanceOf(TypeError);
		}

		expect(errorThrown).toBe(true);
		expect(console.error).toHaveBeenCalledWith(
			`Dropzone belonging to ${mockFragmentId} does not have a uuid.`
		);
	});

	test('should throw an error and log in development mode when dropzone item id is not found in layout data', () => {
		const mockFragmentId = 'fragment-with-missing-item';
		const fragmentEntryLinkId = 'link-1';
		const missingItemId = 'missing-dropzone-item';

		const mockFragmentItem = {
			config: {
				fragmentEntryLinkId,
			},
			itemId: mockFragmentId,
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		};
		const mockItems = {
			[mockFragmentId]: mockFragmentItem,
		};
		const mockFragmentEntryLink = {
			content: '<div></div>',
			editableTypes: {},
			editableValues: {},
		};
		const mockOptions = {
			fragmentEntryLinks: {
				[fragmentEntryLinkId]: mockFragmentEntryLink,
			},
			layoutData: {
				items: mockItems,
			},
			layoutDataRef: {current: {items: mockItems}},
		};

		getAllEditables.mockReturnValue([]);
		getAllPortals.mockReturnValue([
			{
				dropZoneId: 'valid-dropzone',
				mainItemId: missingItemId,
				priority: 1,
			},
		]);

		let errorThrown = false;
		try {
			getTreeNodes(mockFragmentItem, mockItems, mockOptions);
		}
		catch (error) {
			errorThrown = true;
			expect(error).toBeInstanceOf(TypeError);
		}

		expect(errorThrown).toBe(true);
		expect(console.error).toHaveBeenCalledWith(
			`Dropzone with uuid ${missingItemId} was not found in the layout data.`
		);
	});

	test('should throw an error and log in development mode when a child item it is not found in the layout data', () => {
		const missingItemId = 'missing-item';
		const itemId = 'item-1';
		const parentId = 'parent-1';
		const mockParentItem = {
			children: [itemId, missingItemId],
			config: {},
			itemId: parentId,
			parentId: rootId,
			type: LAYOUT_DATA_ITEM_TYPES.container,
		};
		const mockChildItem = {
			children: [],
			config: {},
			itemId,
			type: LAYOUT_DATA_ITEM_TYPES.container,
		};

		const mockItems = {
			[itemId]: mockChildItem,
			[parentId]: mockParentItem,
		};
		const mockOptions = {
			layoutData: {
				items: mockItems,
			},
			layoutDataRef: {current: {items: mockItems}},
		};

		let errorThrown = false;
		try {
			getTreeNodes(mockParentItem, mockItems, mockOptions);
		}
		catch (error) {
			errorThrown = true;
			expect(error).toBeInstanceOf(TypeError);
		}

		expect(errorThrown).toBe(true);
		expect(console.error).toHaveBeenCalledWith(
			`Child item with id ${missingItemId} was not found in the layout data.`
		);
	});

	test('should throw an error and log in development mode when the main root item is missing from layout data in a dropZone', () => {
		const nonExistentMainId = 'non-existent-main';
		const layoutData = {
			items: {
				'drop-zone-1': {
					children: [],
					itemId: 'drop-zone-1',
					type: LAYOUT_DATA_ITEM_TYPES.dropZone,
				},
				[rootId]: {
					children: ['drop-zone-1'],
					itemId: rootId,
					type: LAYOUT_DATA_ITEM_TYPES.root,
				},
			},
			rootItems: {main: nonExistentMainId},
		};

		const mockOptions = {
			layoutData: {
				items: layoutData.items,
				rootItems: layoutData.rootItems,
			},
			layoutDataRef: {
				current: {
					items: layoutData.items,
					rootItems: layoutData.rootItems,
				},
			},
		};

		let errorThrown = false;
		try {
			getTreeNodes(
				layoutData.items[rootId],
				layoutData.items,
				mockOptions
			);
		}
		catch (error) {
			errorThrown = true;
			expect(error).toBeInstanceOf(TypeError);
		}

		expect(errorThrown).toBe(true);
		expect(console.error).toHaveBeenCalledWith(
			`Root with id ${nonExistentMainId} was not found in the layout data.`
		);
	});
});

describe('getCollectionAncestor', () => {
	beforeAll(() => {
		console.error = jest.fn();
	});

	afterAll(() => {
		console.error = originalConsoleError;
	});

	test('should throw an error and log in development mode if the item does not exist in layout data', () => {
		process.env.NODE_ENV = 'development';
		const nonExistentItemId = 'non-existent-item';
		const mockLayoutData = {
			items: {
				'item-1': {
					itemId: 'item-1',
					parentId: 'parent-1',
					type: 'fragment',
				},
			},
		};

		let errorThrown = false;
		try {
			getCollectionAncestor(mockLayoutData, nonExistentItemId);
		}
		catch (error) {
			errorThrown = true;
			expect(error).toBeInstanceOf(TypeError);
		}

		expect(errorThrown).toBe(true);
		expect(console.error).toHaveBeenCalledWith(
			`Item with id ${nonExistentItemId} does not exist in layout data.`
		);
	});

	test('should return null if the item exists but has no collection ancestor', () => {
		const itemId = 'fragment-1';
		const mockLayoutData = {
			items: {
				[itemId]: {
					itemId,
					parentId: rootId,
					type: LAYOUT_DATA_ITEM_TYPES.fragment,
				},
				[rootId]: {
					itemId: rootId,
					parentId: null,
					type: LAYOUT_DATA_ITEM_TYPES.root,
				},
			},
		};

		const result = getCollectionAncestor(mockLayoutData, itemId);
		expect(result).toBeNull();
	});

	test('should return the correct collection ancestor when one exists', () => {
		const fragmentId = 'fragment-1';
		const collectionId = 'collection-1';
		const mockLayoutData = {
			items: {
				[collectionId]: {
					itemId: collectionId,
					parentId: rootId,
					type: LAYOUT_DATA_ITEM_TYPES.collection,
				},
				[fragmentId]: {
					itemId: fragmentId,
					parentId: collectionId,
					type: LAYOUT_DATA_ITEM_TYPES.fragment,
				},
				[rootId]: {
					itemId: rootId,
					parentId: null,
					type: LAYOUT_DATA_ITEM_TYPES.root,
				},
			},
		};

		const result = getCollectionAncestor(mockLayoutData, fragmentId);
		expect(result).toEqual(mockLayoutData.items[collectionId]);
	});
});
