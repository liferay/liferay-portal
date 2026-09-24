/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';

import deleteItem, {
	getPreviousItemId,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/deleteItem';

jest.mock('@liferay/layout-js-components-web', () => ({
	openConfirmModal: jest.fn(),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/services/InfoItemService',
	() => ({
		getPageContents: jest.fn(() => Promise.resolve()),
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/services/LayoutService',
	() => ({
		markItemForDeletion: jest.fn(() =>
			Promise.resolve({
				layoutData: {
					items: {
						container: {children: [], parentId: 'root'},
						root: {
							children: ['container'],
						},
					},
					rootItems: {main: 'root'},
				},
			})
		),
	})
);

const STATE = {
	fragmentEntryLinks: {
		38685: {
			editableValues: {
				portletId: 'com_liferay_blogs_web_portlet_BlogsPortlet',
			},
			fragmentEntryLinkId: '38685',
			name: 'Blogs',
		},
		38687: {
			editableValues: {
				portletId:
					'com_liferay_microblogs_web_portlet_MicroblogsPortlet',
			},
			fragmentEntryLinkId: '38687',
			name: 'Microblogs',
		},
		38688: {
			editableValues: {
				instanceId: '2411',
				portletId: 'com_liferay_microblogs_web_portlet_AnotherPortlet',
			},
			fragmentEntryLinkId: '38688',
			name: 'AnotherPortlet',
		},
	},
	layoutData: {
		items: {
			child1: {
				children: [],
				config: {
					fragmentEntryLinkId: '38687',
				},
				itemId: 'child1',
				parentId: 'container',
				type: 'fragment',
			},
			child2: {
				children: [],
				config: {
					fragmentEntryLinkId: '38685',
				},
				itemId: 'child2',
				parentId: 'container',
				type: 'fragment',
			},
			child3: {
				children: [],
				config: {
					fragmentEntryLinkId: '38688',
				},
				itemId: 'child3',
				parentId: 'container',
				type: 'fragment',
			},
			child4: {
				children: [],
				itemId: 'child4',
				parentId: 'column',
				type: 'fragment',
			},
			child5: {
				children: [],
				itemId: 'child5',
				parentId: 'collectionItem',
				type: 'fragment',
			},
			collection: {
				children: ['collection-item'],
				itemId: 'collection',
				parentId: 'root',
				type: 'collection',
			},
			collectionItem: {
				children: ['child4'],
				itemId: 'collectionItem',
				parentId: 'collection',
				type: 'collection-item',
			},
			column: {
				children: ['child4'],
				itemId: 'column',
				parentId: 'row',
				type: 'column',
			},
			container: {
				children: ['child1', 'child2', 'child3'],
				config: {},
				itemId: 'container',
				parentId: 'root',
				type: 'container',
			},
			root: {
				children: ['container'],
				config: {},
				itemId: 'root',
				parentId: '',
				type: 'root',
			},
			row: {
				children: ['column'],
				itemId: 'row',
				parentId: 'root',
				type: 'row',
			},
		},
		pageRules: [],
		rootItems: {
			dropZone: '',
			main: 'root',
		},
		version: 1,
	},
};

function getElementVariationsState(segmentsExperienceERC) {
	return {
		...STATE,
		availableSegmentsExperiences: {
			1: {
				segmentsExperienceERC: 'experience',
				segmentsExperienceId: '1',
			},
		},
		elementVariations: [
			{
				segmentsExperienceERC,
				targetElement:
					'.lfr-layout-structure-item-child1 [data-lfr-editable-id="element-text"]',
			},
		],
		layoutData: {...STATE.layoutData, deletedItems: []},
		segmentsExperienceId: '1',
	};
}

describe('deleteItem', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('dispatches the delete item action with the portletIds of the removed portlets, if any', async () => {
		const dispatch = jest.fn();

		await deleteItem({itemIds: ['container'], store: STATE})(
			dispatch,
			() => STATE
		);

		expect(dispatch).toBeCalledWith(
			expect.objectContaining({
				portletIds: [
					'com_liferay_microblogs_web_portlet_MicroblogsPortlet',
					'com_liferay_blogs_web_portlet_BlogsPortlet',
					'com_liferay_microblogs_web_portlet_AnotherPortlet_INSTANCE_2411',
				],
			})
		);
	});

	it('warns before deleting an item whose descendants are referenced in element variations', async () => {
		const dispatch = jest.fn();

		await deleteItem({itemIds: ['container']})(dispatch, () =>
			getElementVariationsState('experience')
		);

		expect(openConfirmModal).toHaveBeenCalledTimes(1);
		expect(openConfirmModal).toHaveBeenCalledWith(
			expect.objectContaining({
				text: 'one-or-more-of-the-selected-fragments-are-referenced-in-one-or-more-element-variations.-are-you-sure-you-want-to-proceed-with-the-deletion',
			})
		);
		expect(dispatch).not.toHaveBeenCalled();
	});

	it('ignores the element variations of other experiences', async () => {
		const dispatch = jest.fn();

		await deleteItem({itemIds: ['container']})(dispatch, () =>
			getElementVariationsState('otherExperience')
		);

		expect(openConfirmModal).not.toHaveBeenCalled();
		expect(dispatch).toHaveBeenCalled();
	});

	it('warns about the rules after the element variations', async () => {
		openConfirmModal.mockImplementationOnce(({onConfirm}) => onConfirm());

		const state = getElementVariationsState('experience');

		await deleteItem({itemIds: ['container']})(jest.fn(), () => ({
			...state,
			layoutData: {
				...state.layoutData,
				pageRules: [{actions: [{itemId: 'container'}], conditions: []}],
			},
		}));

		expect(openConfirmModal).toHaveBeenCalledTimes(2);
		expect(openConfirmModal).toHaveBeenLastCalledWith(
			expect.objectContaining({
				text: 'one-or-more-of-the-selected-fragments-are-referenced-in-one-or-more-rules',
			})
		);
	});
});

describe('getPreviousItemId', () => {
	it('returns the previous sibling if it exists', async () => {
		const layoutData = STATE.layoutData;
		const nextLayoutData = {
			...STATE.layoutData,
			items: {
				...STATE.layoutData.items,
				container: {
					...STATE.layoutData.items.container,
					children: ['child1', 'child3'],
				},
			},
		};

		delete nextLayoutData.items.child2;

		expect(
			getPreviousItemId(
				['child2'],
				layoutData.items,
				nextLayoutData.items
			)
		).toBe('child1');
	});

	it('returns the parentId if it does not have siblings', async () => {
		const layoutData = STATE.layoutData;
		const nextLayoutData = {
			...STATE.layoutData,
			items: {
				...STATE.layoutData.items,
				container: {
					...STATE.layoutData.items.container,
					children: [],
				},
			},
		};

		delete nextLayoutData.items.child1;

		expect(
			getPreviousItemId(
				['child1'],
				layoutData.items,
				nextLayoutData.items
			)
		).toBe('container');
	});

	it('returns null when the item is the last one in the layout', async () => {
		const layoutData = STATE.layoutData;
		const nextLayoutData = {
			...STATE.layoutData,
			items: {
				...STATE.layoutData.items,
				root: {
					...STATE.layoutData.items.root,
					children: [],
				},
			},
		};

		delete nextLayoutData.items.container;

		expect(
			getPreviousItemId(
				['container'],
				layoutData.items,
				nextLayoutData.items
			)
		).toBe(null);
	});

	it('returns the row id if it is the only element within a column (grid)', async () => {
		const layoutData = STATE.layoutData;
		const nextLayoutData = {
			...STATE.layoutData,
			items: {
				...STATE.layoutData.items,
				column: {
					...STATE.layoutData.items.column,
					children: [],
				},
			},
		};

		delete nextLayoutData.items.child4;

		expect(
			getPreviousItemId(
				['child4'],
				layoutData.items,
				nextLayoutData.items
			)
		).toBe('row');
	});

	it('returns the collection id if it is the only element within a collection item (display collection)', async () => {
		const layoutData = STATE.layoutData;
		const nextLayoutData = {
			...STATE.layoutData,
			items: {
				...STATE.layoutData.items,
				collectionItem: {
					...STATE.layoutData.items.collectionItem,
					children: [],
				},
			},
		};

		delete nextLayoutData.items.child4;

		expect(
			getPreviousItemId(
				['child5'],
				layoutData.items,
				nextLayoutData.items
			)
		).toBe('collection');
	});
});
