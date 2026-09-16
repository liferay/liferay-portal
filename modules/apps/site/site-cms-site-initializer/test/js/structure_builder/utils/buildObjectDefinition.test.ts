/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ReferencedStructure,
	RelatedContent,
	RepeatableGroup,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
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

const EMAIL_FIELD: Field = {
	erc: 'email-field',
	indexableConfig: {indexed: false},
	label: {en_US: 'Email Field'},
	localized: false,
	locked: false,
	name: 'emailField',
	parent: getUuid(),
	required: false,
	settings: {
		autocompleteDomains: '@liferay.com,@gmail.com',
		autocompleteEnabled: true,
		blockedDomains: '@example.com',
		uniqueValues: true,
	},
	type: 'email',
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

const RELATED_CONTENT: RelatedContent = {
	erc: 'related-content-erc',
	label: {en_US: 'Related Content'},
	multiselection: true,
	name: 'relatedContent',
	parent: getUuid(),
	relatedStructureERC: 'related-structure-erc',
	type: 'related-content',
	uuid: getUuid(),
};

const RELATED_CONTENT_SINGLE: RelatedContent = {
	erc: 'related-content-single-erc',
	label: {en_US: 'Related Content Single'},
	multiselection: false,
	name: 'relatedContentSingle',
	parent: getUuid(),
	relatedStructureERC: 'related-structure-single-erc',
	type: 'related-content',
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

	it('builds an email field as an EmailAddress business type with its connected settings', () => {
		const result = buildObjectDefinition({
			children: getChildren([EMAIL_FIELD]),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'draft',
		});

		expect(result.objectFields).toEqual([
			{
				DBType: 'String',
				businessType: 'EmailAddress',
				externalReferenceCode: 'email-field',
				indexed: false,
				label: {en_US: 'Email Field'},
				localized: false,
				name: 'emailField',
				objectFieldSettings: [
					{
						name: 'autocompleteDomains',
						value: '@liferay.com,@gmail.com',
					},
					{name: 'autocompleteEnabled', value: true},
					{name: 'blockedDomains', value: '@example.com'},
					{name: 'uniqueValues', value: true},
				],
				required: false,
				system: false,
			},
		]);
	});

	it('omits autocomplete settings when the email field has no autocomplete domains', () => {
		const result = buildObjectDefinition({
			children: getChildren([
				{
					...EMAIL_FIELD,
					settings: {blockedDomains: '@example.com'},
				},
			]),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'draft',
		});

		expect(result.objectFields?.[0].objectFieldSettings).toEqual([
			{name: 'blockedDomains', value: '@example.com'},
		]);
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

	it('re-emits the allowStandaloneObjectEntry setting carried by the structure', () => {
		const result = buildObjectDefinition({
			children: getChildren([TEXT_FIELD]),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			settings: {allowStandaloneObjectEntry: 'true'},
			spaces: 'all',
			status: 'published',
		});

		expect(result.objectDefinitionSettings).toEqual([
			{name: 'acceptAllGroups', value: 'true'},
			{name: 'allowStandaloneObjectEntry', value: 'true'},
		]);
	});

	it('does not emit the allowStandaloneObjectEntry setting when the structure does not carry it', () => {
		const result = buildObjectDefinition({
			children: getChildren([TEXT_FIELD]),
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			settings: {},
			spaces: 'all',
			status: 'published',
		});

		expect(result.objectDefinitionSettings).toEqual([
			{name: 'acceptAllGroups', value: 'true'},
		]);
	});

	it('builds objectDefinition with related content relationships', () => {
		const children: Map<Uuid, StructureChild> = new Map<
			Uuid,
			StructureChild
		>([
			[RELATED_CONTENT.uuid, RELATED_CONTENT],
			[RELATED_CONTENT_SINGLE.uuid, RELATED_CONTENT_SINGLE],
			[TEXT_FIELD.uuid, TEXT_FIELD],
		]);

		const result = buildObjectDefinition({
			children,
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'draft',
		});

		expect(result.objectRelationships).toEqual([
			{
				deletionType: 'disassociate',
				externalReferenceCode: 'related-content-erc',
				label: {en_US: 'Related Content'},
				name: 'relatedContent',
				objectDefinitionExternalReferenceCode1: 'structureERC',
				objectDefinitionExternalReferenceCode2: 'related-structure-erc',
				type: 'manyToMany',
			},
		]);
	});

	it('builds objectDefinition with edge relationships for referenced structures', () => {
		const referencedStructure: ReferencedStructure = {
			children: new Map(),
			editURL: '',
			erc: 'ref-structure-erc',
			label: {en_US: 'Referenced Structure'},
			name: 'refStructure',
			parent: getUuid(),
			relationshipERC: 'ref-rel-erc',
			relationshipName: 'refRelationship',
			spaces: [],
			type: 'referenced-structure',
			uuid: getUuid(),
			workflows: {},
		};

		const children: Map<Uuid, StructureChild> = new Map<
			Uuid,
			StructureChild
		>([
			[referencedStructure.uuid, referencedStructure],
			[TEXT_FIELD.uuid, TEXT_FIELD],
		]);

		const result = buildObjectDefinition({
			children,
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'draft',
		});

		expect(result.objectRelationships).toEqual([
			{
				deletionType: 'cascade',
				edge: true,
				externalReferenceCode: 'ref-rel-erc',
				label: {en_US: 'refStructure'},
				name: 'refRelationship',
				objectDefinitionExternalReferenceCode1: 'structureERC',
				objectDefinitionExternalReferenceCode2: 'ref-structure-erc',
				type: 'oneToMany',
			},
		]);
	});

	it('builds objectDefinition with edge relationships for repeatable groups', () => {
		const groupUuid = getUuid();

		const repeatableGroup: RepeatableGroup = {
			children: new Map(),
			erc: 'group-erc',
			isRepeatable: true,
			label: {en_US: 'Repeatable Group'},
			name: 'repeatableGroup',
			parent: getUuid(),
			relationshipERC: 'group-rel-erc',
			relationshipName: 'groupRelationship',
			type: 'group',
			uuid: groupUuid,
		};

		const children: Map<Uuid, StructureChild> = new Map<
			Uuid,
			StructureChild
		>([
			[repeatableGroup.uuid, repeatableGroup],
			[TEXT_FIELD.uuid, TEXT_FIELD],
		]);

		const result = buildObjectDefinition({
			children,
			erc: 'structureERC',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			spaces: [],
			status: 'draft',
		});

		expect(result.objectRelationships).toEqual([
			{
				deletionType: 'cascade',
				edge: true,
				externalReferenceCode: 'group-rel-erc',
				label: {en_US: 'Repeatable Group'},
				name: 'groupRelationship',
				objectDefinitionExternalReferenceCode1: 'structureERC',
				objectDefinitionExternalReferenceCode2: 'group-erc',
				type: 'oneToMany',
			},
		]);
	});
});
