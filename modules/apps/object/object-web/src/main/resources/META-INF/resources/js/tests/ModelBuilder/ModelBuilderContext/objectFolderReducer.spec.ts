/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {ObjectFolderReducer} from '../../../components/ModelBuilder/ModelBuilderContext/objectFolderReducer';
import {TYPES} from '../../../components/ModelBuilder/ModelBuilderContext/typesEnum';
import {TAction, TState} from '../../../components/ModelBuilder/types';

const OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE = 'OBJECT_FOLDER_ERC';

const OBJECT_FOLDER_NAME = 'objectFolder';

function buildObjectDefinition(id: number): ObjectDefinitionNodeData {
	return {
		actions: {},
		dbTableName: `ObjectDefinition${id}Table`,
		defaultLanguageId: 'en_US',
		externalReferenceCode: `OBJECT_DEFINITION_ERC_${id}`,
		hasObjectDefinitionDeleteResourcePermission: true,
		hasObjectDefinitionManagePermissionsResourcePermission: true,
		hasObjectDefinitionUpdateResourcePermission: true,
		hasObjectDefinitionViewResourcePermission: true,
		id,
		label: {en_US: `Object Definition ${id}`},
		linkedObjectDefinition: false,
		name: `ObjectDefinition${id}`,
		objectFields: [],
		objectFolderExternalReferenceCode:
			OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE,
		objectRelationships: [],
		selected: false,
		showAllObjectFields: false,
		status: {code: 0, label: 'approved', label_i18n: 'Approved'},
	} as unknown as ObjectDefinitionNodeData;
}

function buildObjectFolder(
	objectDefinitions: ObjectDefinitionNodeData[],
	objectFolderItems: ObjectFolderItem[]
): ObjectFolder {
	return {
		externalReferenceCode: OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE,
		id: 1,
		label: {en_US: 'Object Folder'},
		name: OBJECT_FOLDER_NAME,
		objectDefinitions,
		objectFolderItems,
	} as ObjectFolder;
}

function buildObjectFolderItem(
	id: number,
	positionX: number,
	positionY: number
): ObjectFolderItem {
	return {
		linkedObjectDefinition: false,
		objectDefinitionExternalReferenceCode: `OBJECT_DEFINITION_ERC_${id}`,
		positionX,
		positionY,
	};
}

function buildState(state: Partial<TState> = {}): TState {
	return {
		baseResourceURL: 'http://localhost/base-resource-url',
		elements: [],
		hasUnsavedObjectFolderItemPositions: false,
		leftSidebarItems: [],
		modelBuilderModals: {} as ModelBuilderModals,
		objectDefinitionPermissionsURL: '',
		objectFolders: [],
		rightSidebarType: 'empty',
		selectedObjectFolder: {} as ObjectFolder,
		showChangesSaved: false,
		...state,
	} as TState;
}

function updateModelBuilderStructure(objectFolder: ObjectFolder): TState {
	return ObjectFolderReducer(buildState(), {
		payload: {
			dispatch: jest.fn(),
			objectFolders: [objectFolder],
			selectedObjectFolderName: OBJECT_FOLDER_NAME,
		},
		type: TYPES.UPDATE_MODEL_BUILDER_STRUCTURE,
	} as TAction);
}

describe('ObjectFolderReducer', () => {
	it('does not flag unsaved positions when every object folder item is already placed', () => {
		const objectFolder = buildObjectFolder(
			[buildObjectDefinition(1), buildObjectDefinition(2)],
			[
				buildObjectFolderItem(1, 100, 200),
				buildObjectFolderItem(2, 480, 200),
			]
		);

		const state = updateModelBuilderStructure(objectFolder);

		expect(state.hasUnsavedObjectFolderItemPositions).toBe(false);

		expect(state.selectedObjectFolder.objectFolderItems).toEqual([
			buildObjectFolderItem(1, 100, 200),
			buildObjectFolderItem(2, 480, 200),
		]);
	});

	it('flags unsaved positions and places an object folder item stored at the origin', () => {
		const objectFolder = buildObjectFolder(
			[buildObjectDefinition(1), buildObjectDefinition(2)],
			[buildObjectFolderItem(1, 100, 200), buildObjectFolderItem(2, 0, 0)]
		);

		const state = updateModelBuilderStructure(objectFolder);

		expect(state.hasUnsavedObjectFolderItemPositions).toBe(true);

		expect(state.selectedObjectFolder.objectFolderItems[1]).toEqual(
			buildObjectFolderItem(2, 480, 200)
		);
	});

	it('flags unsaved positions when an object definition is added to the object folder', () => {
		const selectedObjectFolder = buildObjectFolder(
			[buildObjectDefinition(1)],
			[buildObjectFolderItem(1, 100, 200)]
		);

		const state = ObjectFolderReducer(buildState({selectedObjectFolder}), {
			payload: {
				dbTableName: 'ObjectDefinition2Table',
				dispatch: jest.fn(),
				elements: [],
				leftSidebarItems: [],
				newObjectDefinition: buildObjectDefinition(
					2
				) as unknown as ObjectDefinition,
				objectFolders: [],
				selectedObjectFolder,
			},
			type: TYPES.ADD_OBJECT_DEFINITION_TO_OBJECT_FOLDER,
		} as TAction);

		expect(state.hasUnsavedObjectFolderItemPositions).toBe(true);
	});

	it('clears the unsaved positions flag', () => {
		const state = ObjectFolderReducer(
			buildState({hasUnsavedObjectFolderItemPositions: true}),
			{
				payload: {
					updatedHasUnsavedObjectFolderItemPositions: false,
				},
				type: TYPES.SET_HAS_UNSAVED_OBJECT_FOLDER_ITEM_POSITIONS,
			}
		);

		expect(state.hasUnsavedObjectFolderItemPositions).toBe(false);
	});
});
