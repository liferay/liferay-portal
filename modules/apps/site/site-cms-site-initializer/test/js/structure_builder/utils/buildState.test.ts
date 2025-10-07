/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import buildObjectDefinition from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectDefinition';
import buildState from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildState';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/config',
	() => {
		return {
			config: {
				objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
			},
		};
	}
);

const DATE_TIME_FIELD_UUID = getUuid();
const TEXT_FIELD_UUID = getUuid();
const TITLE_FIELD_UUID = getUuid();

const DATE_TIME_FIELD: Field = {
	erc: 'datetime-field',
	indexableConfig: {indexed: false},
	label: {en_US: 'Date and Time Field'},
	localized: true,
	locked: false,
	name: 'datetimeField',
	parent: getUuid(),
	required: false,
	settings: {
		timeStorage: 'convertToUTC',
	},
	type: 'datetime',
	uuid: DATE_TIME_FIELD_UUID,
};

const TEXT_FIELD: Field = {
	erc: 'text-field',
	indexableConfig: {
		indexed: true,
		indexedAsKeyword: true,
		indexedLanguageId: undefined,
	},
	label: {en_US: 'Text Field'},
	localized: false,
	locked: false,
	name: 'textField',
	parent: getUuid(),
	required: true,
	settings: {},
	type: 'text',
	uuid: TEXT_FIELD_UUID,
};

const TITLE_FIELD: Field = {
	erc: 'title-field',
	indexableConfig: {
		indexed: true,
		indexedAsKeyword: true,
		indexedLanguageId: undefined,
	},
	label: {en_US: 'Title Field'},
	localized: false,
	locked: true,
	name: 'titleField',
	parent: getUuid(),
	required: true,
	settings: {},
	type: 'text',
	uuid: TITLE_FIELD_UUID,
};

function getChildren(fields: Field[]) {
	const children = new Map();

	for (const field of fields) {
		children.set(field.uuid, field);
	}

	return children;
}

describe('buildState', () => {
	it('Builds state with two editable fields and one locked field', () => {
		const structure: State['structure'] = {
			children: new Map(),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'draft',
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: getUuid(),
			workflows: {},
		};

		const initialState: State = {
			error: null,
			history: {deletedChildren: false},
			invalids: new Map(),
			publishedChildren: new Set(),
			selection: [],
			structure,
			unsavedChanges: false,
		};

		const objectDefinition = buildObjectDefinition({
			children: getChildren([TEXT_FIELD, DATE_TIME_FIELD, TITLE_FIELD]),
			erc: structure.erc,
			label: structure.label,
			name: structure.name,
			spaces: structure.spaces,
		});

		const result = buildState({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {},
		});

		const {children, uuid} = result!.structure;

		const nextState = {
			...initialState,
			structure: {
				...structure,
				children,
				uuid,
			},
		};

		expect(result).toEqual(nextState);
	});

	it('Takes into account the status of the object definition', () => {
		const structure: State['structure'] = {
			children: new Map(),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'published',
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: getUuid(),
			workflows: {},
		};

		const initialState: State = {
			error: null,
			history: {deletedChildren: false},
			invalids: new Map(),
			publishedChildren: new Set(),
			selection: [],
			structure,
			unsavedChanges: false,
		};

		const objectDefinition = buildObjectDefinition({
			children: getChildren([TEXT_FIELD, DATE_TIME_FIELD]),
			erc: structure.erc,
			label: structure.label,
			name: structure.name,
			spaces: structure.spaces,
		});

		const result = buildState({
			mainObjectDefinition: {
				...objectDefinition,
				status: {
					code: 0,
				},
			},
			objectDefinitions: {},
		});

		const {children, uuid} = result!.structure;

		const publishedChildren = new Set(children.keys());

		const nextState = {
			...initialState,
			publishedChildren,
			structure: {
				...structure,
				children,
				uuid,
			},
		};

		expect(result).toEqual(nextState);
	});

	it('Takes into account spaces and workflows', () => {
		const structure: State['structure'] = {
			children: new Map(),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: ['space-1-erc', 'space-2-erc'],
			status: 'published',
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: getUuid(),
			workflows: {
				'': 'Workflow 2',
				'space-1-erc': 'Workflow 1',
			},
		};

		const initialState: State = {
			error: null,
			history: {deletedChildren: false},
			invalids: new Map(),
			publishedChildren: new Set(),
			selection: [],
			structure,
			unsavedChanges: false,
		};

		const objectDefinition = buildObjectDefinition({
			children: getChildren([TEXT_FIELD, DATE_TIME_FIELD]),
			erc: structure.erc,
			label: structure.label,
			name: structure.name,
			spaces: structure.spaces,
			workflows: structure.workflows,
		});

		const result = buildState({
			mainObjectDefinition: {
				...objectDefinition,
				status: {
					code: 0,
				},
			},
			objectDefinitions: {},
		});

		const {children, uuid} = result!.structure;

		const publishedChildren = new Set(children.keys());

		const nextState = {
			...initialState,
			publishedChildren,
			structure: {
				...structure,
				children,
				uuid,
			},
		};

		expect(result).toEqual(nextState);
	});

	it('It works with Double fields ', () => {
		const objectDefinition = {
			enableComments: true,
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryHistory: true,
			enableObjectEntrySchedule: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'ca7f96e2-3436-4aa4-9626-265d006bea87',
			label: {
				en_US: 'Untitled Structure',
			},
			objectFields: [
				{
					DBType: 'Double',
					businessType: 'Decimal',
					externalReferenceCode: 'decimal-field',
					indexed: true,
					label: {
						en_US: 'Decimal',
					},
					localized: true,
					name: 'decimal',
					required: false,
					system: false,
					type: 'Double',
				},
			],
			pluralLabel: {
				en_US: 'Untitled Structure',
			},
			scope: 'depot' as const,
		};

		const state = buildState({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {},
		});

		const [, field] = [...state!.structure.children][0];

		expect(field).toEqual(expect.objectContaining({type: 'decimal'}));
	});
});
