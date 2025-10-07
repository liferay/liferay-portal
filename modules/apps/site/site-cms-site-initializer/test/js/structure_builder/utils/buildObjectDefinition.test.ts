/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import buildObjectDefinition from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectDefinition';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/config',
	() => ({
		config: {
			acceptedGroupExternalReferenceCodes:
				'acceptedGroupExternalReferenceCodesConfig',
		},
	})
);

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
	uuid: getUuid(),
};

const TEXT_FIELD: Field = {
	erc: 'text-field',
	indexableConfig: {indexed: true, indexedAsKeyword: true},
	label: {en_US: 'Text Field'},
	localized: false,
	locked: false,
	name: 'textField',
	parent: getUuid(),
	required: true,
	settings: {},
	type: 'text',
	uuid: getUuid(),
};

const TITLE_FIELD: Field = {
	erc: 'title-field',
	indexableConfig: {indexed: true, indexedAsKeyword: true},
	label: {en_US: 'Title Field'},
	localized: false,
	locked: true,
	name: 'titleField',
	parent: getUuid(),
	required: true,
	settings: {},
	type: 'text',
	uuid: getUuid(),
};

function getChildren(fields: Field[]) {
	const children = new Map();

	for (const field of fields) {
		children.set(field.uuid, field);
	}

	return children;
}

describe('buildObjectDefinition', () => {
	it('builds objectDefinition with a field without settings and a locked field', () => {
		const result = buildObjectDefinition({
			children: getChildren([TEXT_FIELD, TITLE_FIELD]),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'draft',
		});

		expect(result).toEqual({
			enableComments: true,
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryHistory: true,
			enableObjectEntrySchedule: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			objectFields: [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'text-field',
					indexed: true,
					indexedAsKeyword: true,
					indexedLanguageId: '',
					label: {en_US: 'Text Field'},
					localized: false,
					name: 'textField',
					objectFieldSettings: [],
					required: true,
					system: false,
				},
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'title-field',
					indexed: true,
					indexedAsKeyword: true,
					indexedLanguageId: '',
					label: {en_US: 'Title Field'},
					localized: false,
					name: 'titleField',
					objectFieldSettings: [],
					required: true,
					system: true,
				},
			],
			objectRelationships: [],
			pluralLabel: {en_US: 'Structure'},
			scope: 'depot',
			status: {
				code: 2,
			},
			titleObjectFieldName: 'title',
		});
	});

	it('builds objectDefinition with a field with settings', () => {
		const result = buildObjectDefinition({
			children: getChildren([DATE_TIME_FIELD]),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'published',
		});

		expect(result).toEqual({
			enableComments: true,
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryHistory: true,
			enableObjectEntrySchedule: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			objectFields: [
				{
					DBType: 'DateTime',
					businessType: 'DateTime',
					externalReferenceCode: 'datetime-field',
					indexed: false,
					label: {en_US: 'Date and Time Field'},
					localized: true,
					name: 'datetimeField',
					objectFieldSettings: [
						{name: 'timeStorage', value: 'convertToUTC'},
					],
					required: false,
					system: false,
				},
			],
			objectRelationships: [],
			pluralLabel: {en_US: 'Structure'},
			scope: 'depot',
			status: {
				code: 0,
			},
			titleObjectFieldName: 'title',
		});
	});

	it('builds objectDefinition with spaces and workflows selected', () => {
		const result = buildObjectDefinition({
			children: getChildren([TEXT_FIELD]),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: ['space-1-erc', 'space-2-erc'],
			status: 'published',
			workflows: {'': 'Workflow 2', 'space-1-erc': 'Workflow 1'},
		});

		expect(result).toEqual({
			enableComments: true,
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryHistory: true,
			enableObjectEntrySchedule: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			objectDefinitionSettings: [
				{
					name: 'acceptedGroupExternalReferenceCodes',
					value: 'space-1-erc,space-2-erc',
				},
			],
			objectFields: [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'text-field',
					indexed: true,
					indexedAsKeyword: true,
					indexedLanguageId: '',
					label: {en_US: 'Text Field'},
					localized: false,
					name: 'textField',
					objectFieldSettings: [],
					required: true,
					system: false,
				},
			],
			objectRelationships: [],
			pluralLabel: {en_US: 'Structure'},
			scope: 'depot',
			status: {
				code: 0,
			},
			titleObjectFieldName: 'title',
			workflowDefinitionLinks: [
				{
					groupExternalReferenceCode: '',
					workflowDefinitionName: 'Workflow 2',
				},
				{
					groupExternalReferenceCode: 'space-1-erc',
					workflowDefinitionName: 'Workflow 1',
				},
			],
		});
	});
});
