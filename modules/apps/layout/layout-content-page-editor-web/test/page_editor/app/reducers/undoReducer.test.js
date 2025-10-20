/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import duplicateItem from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/duplicateItem';
import {
	addFragmentEntryLinks,
	updateEditableValues,
	updateLanguageId,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/index';
import {
	ADD_FRAGMENT_ENTRY_LINKS,
	ADD_ITEM,
	ADD_UNDO_ACTION,
	DUPLICATE_ITEM,
	MOVE_ITEM,
	UPDATE_COL_SIZE,
	UPDATE_EDITABLE_VALUES,
	UPDATE_FRAGMENT_ENTRY_LINK_CONFIGURATION,
	UPDATE_ITEM_CONFIG,
	UPDATE_LANGUAGE_ID,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/types';
import updateFragmentEntryLinkConfiguration from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/updateFragmentEntryLinkConfiguration';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';
import undoReducer from '../../../../src/main/resources/META-INF/resources/page_editor/app/reducers/undoReducer';
import {SELECT_SEGMENTS_EXPERIENCE} from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/experience/actions';
import selectExperience from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/experience/actions/selectExperience';

const STATE = {
	availableSegmentsExperiences: {
		0: {name: 'default'},
		2: {name: 'experience-2'},
	},
	layoutData: {items: []},
	undoHistory: [],
};

describe('undoReducer', () => {
	it('allows having 100 maximum undo items', () => {
		const initialState = {...STATE};

		const actions = new Array(105).fill({
			actionType: DUPLICATE_ITEM,
			type: ADD_UNDO_ACTION,
		});

		const finalState = actions.reduce((state, action) => {
			return undoReducer(state, action);
		}, initialState);

		expect(finalState.undoHistory.length).toBe(100);
	});

	it('saves needed state for undo when dispatching ADD_FRAGMENT_ENTRY_LINKS action', () => {
		const ITEM_ID = 'itemId';
		const initialState = {...STATE};

		const action = addFragmentEntryLinks({addedItemId: ITEM_ID});

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: ADD_FRAGMENT_ENTRY_LINKS,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.itemIds).toStrictEqual([ITEM_ID]);
	});

	it('saves needed state for undo when dispatching DUPLICATE_ITEM action', () => {
		const ITEM_ID = 'itemId';
		const initialState = {...STATE};

		const action = duplicateItem({itemIds: [ITEM_ID]});

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: DUPLICATE_ITEM,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.itemIds).toStrictEqual([ITEM_ID]);
	});

	it('saves needed state for undo when dispatching SELECT_SEGMENTS_EXPERIENCE action', () => {
		const SEGMENTS_EXPERIENCE_ID = '2';
		const initialState = {
			...STATE,
			segmentsExperienceId: SEGMENTS_EXPERIENCE_ID,
		};

		const action = selectExperience({
			segmentsExperienceId: '0',
		});

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: SELECT_SEGMENTS_EXPERIENCE,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.segmentsExperienceId).toBe(SEGMENTS_EXPERIENCE_ID);
	});

	it('saves needed state for undo when dispatching UPDATE_LANGUAGE_ID action', () => {
		const LANGUAGE_ID = 'es_ES';
		const initialState = {
			...STATE,
			languageId: LANGUAGE_ID,
		};

		const action = updateLanguageId({
			languageId: 'en_US',
		});

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: UPDATE_LANGUAGE_ID,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.languageId).toBe(LANGUAGE_ID);
	});

	it('saves needed state for undo when dispatching UPDATE_EDITABLE_VALUES action', () => {
		const FRAGMENT_ENTRY_LINK_ID = '1';
		const EDITABLE_VALUES = {
			[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
				en_US: 'Sample',
			},
		};

		const initialState = {
			fragmentEntryLinks: {
				[FRAGMENT_ENTRY_LINK_ID]: {
					editableValues: EDITABLE_VALUES,
				},
			},
			layoutData: {items: []},
			undoHistory: [],
		};

		const action = updateEditableValues({
			editableValues: {
				[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
					en_US: 'New Sample',
				},
			},
			fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
		});

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: UPDATE_EDITABLE_VALUES,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.fragmentEntryLinkId).toBe(FRAGMENT_ENTRY_LINK_ID);
		expect(undoAction.editableValues).toBe(EDITABLE_VALUES);
	});

	it('saves needed state for undo when dispatching UPDATE_FRAGMENT_ENTRY_LINK_CONFIGURATION action', () => {
		const FRAGMENT_ENTRY_LINK_ID = '1';
		const EDITABLE_VALUES = {
			[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
				checkbox: false,
			},
		};

		const initialState = {
			...STATE,
			fragmentEntryLinks: {
				[FRAGMENT_ENTRY_LINK_ID]: {
					editableValues: EDITABLE_VALUES,
					fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
				},
			},
		};

		const action = updateFragmentEntryLinkConfiguration({
			fragmentEntryLink: {
				editableValues: {
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
						checkbox: true,
					},
				},
				fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
			},
			fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
		});

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: UPDATE_FRAGMENT_ENTRY_LINK_CONFIGURATION,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.fragmentEntryLinkId).toBe(FRAGMENT_ENTRY_LINK_ID);
		expect(undoAction.editableValues).toBe(EDITABLE_VALUES);
	});

	it('saves needed state for undo when dispatching ADD_ITEM action', () => {
		const ITEM_ID = 'itemId';

		const initialState = {
			...STATE,
		};

		const action = {
			itemIds: [ITEM_ID],
		};

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: ADD_ITEM,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.itemIds).toStrictEqual([ITEM_ID]);
	});

	it('saves needed state for undo when dispatching MOVE_ITEM action', () => {
		const ITEM_ID = 'itemId';

		const LAYOUT_DATA = {
			items: {
				[ITEM_ID]: {
					children: [],
					config: {},
					itemId: 'containerId',
					parentId: 'rootId',
					type: 'container',
				},
				rootId: {
					children: [ITEM_ID],
					config: {},
					itemId: 'rootId',
					parentId: '',
					type: 'root',
				},
			},
			rootItems: {
				dropZone: '',
				main: 'a91cab32-3f2a-4278-91a0-399ebd1c8cc1',
			},
			version: 1,
		};

		const initialState = {
			...STATE,
			layoutData: LAYOUT_DATA,
		};

		const action = {
			fragmentEntryLinks: [],
			itemIds: [ITEM_ID],
			layoutData: {items: []},
		};

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: MOVE_ITEM,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.itemIds).toStrictEqual([ITEM_ID]);
		expect(undoAction.parentItemIds).toStrictEqual(['rootId']);
		expect(undoAction.positions).toStrictEqual([0]);
	});

	it('saves needed state for undo when dispatching UPDATE_ITEM_CONFIG action', () => {
		const ITEM_ID = 'itemId';

		const LAYOUT_DATA = {
			items: {
				[ITEM_ID]: {
					children: [],
					config: {paddingBottom: 3, paddingTop: 1},
					itemId: 'containerId',
					parentId: 'rootId',
					type: 'container',
				},
				rootId: {
					children: [ITEM_ID],
					config: {},
					itemId: 'rootId',
					parentId: '',
					type: 'root',
				},
			},
			rootItems: {
				dropZone: '',
				main: 'a91cab32-3f2a-4278-91a0-399ebd1c8cc1',
			},
			version: 1,
		};

		const initialState = {
			...STATE,
			layoutData: LAYOUT_DATA,
		};

		const action = {
			itemIds: [ITEM_ID],
			layoutData: {items: []},
		};

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: UPDATE_ITEM_CONFIG,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.itemIds).toStrictEqual([ITEM_ID]);
		expect(undoAction.config).toStrictEqual({
			[ITEM_ID]: {
				paddingBottom: 3,
				paddingTop: 1,
			},
		});
	});

	it('saves needed state for undo when dispatching UPDATE_COL_SIZE action', () => {
		const ROW_ITEM_ID = 'rowItemId';

		const LAYOUT_DATA = {
			items: {
				[ROW_ITEM_ID]: {
					children: ['column1', 'column2', 'column3', 'column4'],
					config: {},
					itemId: 'containerId',
					parentId: 'rootId',
					type: 'row',
				},
				column1: {
					children: [],
					config: {size: 2},
					itemId: 'containerId',
					parentId: 'rootId',
					type: 'row',
				},
				column2: {
					children: [],
					config: {size: 4},
					itemId: 'containerId',
					parentId: 'rootId',
					type: 'row',
				},
				column3: {
					children: [],
					config: {size: 2},
					itemId: 'containerId',
					parentId: 'rootId',
					type: 'row',
				},
				column4: {
					children: [],
					config: {size: 4},
					itemId: 'containerId',
					parentId: 'rootId',
					type: 'row',
				},
				rootId: {
					children: [ROW_ITEM_ID],
					config: {},
					itemId: 'rootId',
					parentId: '',
					type: 'root',
				},
			},
			rootItems: {
				dropZone: '',
				main: 'a91cab32-3f2a-4278-91a0-399ebd1c8cc1',
			},
			version: 1,
		};

		const initialState = {
			...STATE,
			layoutData: LAYOUT_DATA,
		};

		const action = {
			layoutData: {items: []},
			rowItemId: ROW_ITEM_ID,
		};

		const {undoHistory} = undoReducer(initialState, {
			...action,
			actionType: UPDATE_COL_SIZE,
			type: ADD_UNDO_ACTION,
		});

		const undoAction = undoHistory[0];

		expect(undoAction.rowItemId).toBe(ROW_ITEM_ID);
		expect(undoAction.columnConfigs).toStrictEqual([
			{config: {size: 2}, itemId: 'column1'},
			{config: {size: 4}, itemId: 'column2'},
			{config: {size: 2}, itemId: 'column3'},
			{config: {size: 4}, itemId: 'column4'},
		]);
	});

	it('preserves redo history when dispatching a redo action', () => {
		const LANGUAGE_ID = 'es_ES';
		const initialState = {
			...STATE,
			languageId: LANGUAGE_ID,
			redoHistory: [{languageId: 'en_US', type: UPDATE_LANGUAGE_ID}],
		};

		const action = updateLanguageId({
			languageId: 'en_US',
		});

		const {redoHistory} = undoReducer(initialState, {
			...action,
			actionType: UPDATE_LANGUAGE_ID,
			isRedo: true,
			type: ADD_UNDO_ACTION,
		});

		expect(redoHistory.length).toBe(1);
	});
});
