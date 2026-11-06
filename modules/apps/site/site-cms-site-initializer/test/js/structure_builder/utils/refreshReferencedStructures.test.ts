/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectRelationship,
} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import {
	NonRepeatableGroup,
	ReferencedStructure,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import refreshReferencedStructures from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/state/refreshReferencedStructures';

describe('refreshReferencedStructures', () => {
	it('add new field, update label and keep uuids', () => {
		const fieldUuid = getUuid();
		const referencedStructureUuid = getUuid();
		const structureUuid = getUuid();

		const field: Field = {
			erc: 'field-erc',
			indexableConfig: {
				indexed: false,
			},
			label: {
				en_US: 'Field',
			},
			localized: false,
			locked: false,
			name: 'field-name',
			parent: referencedStructureUuid,
			required: false,
			settings: {},
			type: 'text' as const,
			uuid: fieldUuid,
		};

		const referencedStructure: ReferencedStructure = {
			children: new Map([[field.uuid, field]]),
			editURL: '',
			erc: 'referenced-structure-erc',
			label: {
				en_US: 'Referenced Structure',
			},
			name: 'referenced-structure-name',
			parent: structureUuid,
			relationshipERC: 'relationship-erc',
			relationshipName: 'relationship',
			spaces: [],
			type: 'referenced-structure',
			uuid: referencedStructureUuid,
			workflows: {},
		};

		const root: Structure = {
			children: new Map([
				[referencedStructure.uuid, referencedStructure],
			]),
			erc: 'structure-erc',
			label: {
				en_US: 'Structure',
			},
			name: 'structure-name',
			path: '',
			slug: '',
			spaces: [],
			status: 'published',
			system: false,
			titleFieldName: 'title',
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: structureUuid,
			workflows: {},
		};

		const objectDefinition: ObjectDefinition = {
			enableComments: true,
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryHistory: true,
			enableObjectEntrySchedule: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'referenced-structure-erc',
			id: 1,
			label: {
				en_US: 'Referenced Structure 2',
			},
			name: 'referenced-structure-name',
			objectFields: [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'field-erc',
					indexed: true,
					indexedLanguageId: 'en_US',
					label: {
						en_US: 'Field 2',
					},
					listTypeDefinitionId: 0,
					localized: true,
					name: 'field-name',
					objectFieldSettings: [],
					required: false,
					system: false,
				},
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'new-field-erc',
					indexed: true,
					indexedLanguageId: 'en_US',
					label: {
						en_US: 'New Field',
					},
					listTypeDefinitionId: 0,
					localized: true,
					name: 'new-field-name',
					objectFieldSettings: [],
					required: false,
					system: false,
				},
			],
			objectRelationships: [],
			pluralLabel: {},
			scope: 'depot',
		};

		const objectDefinitions = {
			[objectDefinition.externalReferenceCode]: objectDefinition,
		};

		const result = refreshReferencedStructures({
			objectDefinitions,
			root,
			systemFieldNames: {},
		});

		const updatedReferencedStructure = result.get(
			referencedStructure.uuid
		) as ReferencedStructure;

		expect(updatedReferencedStructure.label.en_US).toBe(
			'Referenced Structure 2'
		);
		expect(updatedReferencedStructure.uuid).toBe(referencedStructure.uuid);

		const [updatedField, newField] = Array.from(
			updatedReferencedStructure.children.values()
		) as Field[];

		expect(updatedField.label.en_US).toBe('Field 2');
		expect(updatedField.uuid).toBe(updatedField.uuid);

		expect(newField.label.en_US).toBe('New Field');
	});
});

describe('refreshReferencedStructures with groups', () => {
	it('Does not duplicate the fields of a nested group', () => {
		const structureUuid = getUuid();
		const repeatableUuid = getUuid();
		const nestedUuid = getUuid();

		const buildTextField = (
			erc: string,
			name: string,
			parent: Uuid
		): Field => ({
			erc,
			indexableConfig: {indexed: false},
			label: {en_US: name},
			localized: false,
			locked: false,
			name,
			parent,
			required: false,
			settings: {},
			type: 'text' as const,
			uuid: getUuid(),
		});

		const text = buildTextField('text-erc', 'text', repeatableUuid);

		const longText = buildTextField(
			'long-text-erc',
			'longText',
			nestedUuid
		);

		const nestedGroup: NonRepeatableGroup = {
			children: new Map([[longText.uuid, longText]]),
			isRepeatable: false,
			label: {en_US: 'Group'},
			parent: repeatableUuid,
			type: 'group',
			uuid: nestedUuid,
		};

		const repeatableGroup: RepeatableGroup = {
			children: new Map<Uuid, StructureChild>([
				[text.uuid, text],
				[nestedUuid, nestedGroup],
			]),
			erc: 'repeatable-erc',
			isRepeatable: true,
			label: {en_US: 'Group'},
			name: 'C_Group',
			parent: structureUuid,
			relationshipERC: 'relationship-erc',
			relationshipName: 'relationship',
			type: 'group',
			uuid: repeatableUuid,
		};

		const root: Structure = {
			children: new Map([[repeatableUuid, repeatableGroup]]),
			erc: 'structure-erc',
			label: {en_US: 'Structure'},
			name: 'structure-name',
			path: '',
			slug: '',
			spaces: [],
			status: 'published',
			system: false,
			titleFieldName: 'title',
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: structureUuid,
			workflows: {},
		};

		const objectDefinition: ObjectDefinition = {
			enableComments: true,
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryHistory: true,
			enableObjectEntrySchedule: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode: 'repeatable-erc',
			id: 1,
			label: {en_US: 'Group'},
			name: 'C_Group',
			objectFields: [text, longText].map(({erc, label, name}) => ({
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: erc,
				indexed: true,
				indexedLanguageId: 'en_US',
				label,
				listTypeDefinitionId: 0,
				localized: true,
				name,
				objectFieldSettings: [],
				required: false,
				system: false,
			})),
			objectRelationships: [],
			pluralLabel: {},
			scope: 'depot',
		};

		const children = refreshReferencedStructures({
			objectDefinitions: {
				[objectDefinition.externalReferenceCode]: objectDefinition,
			},
			root,
			systemFieldNames: {},
		});

		const repeatable = children.get(repeatableUuid) as RepeatableGroup;

		expect(
			Array.from(repeatable.children.values()).map(({type}) => type)
		).toEqual(['text', 'group']);

		const nested = repeatable.children.get(
			nestedUuid
		) as NonRepeatableGroup;

		expect(
			Array.from(nested.children.values()).map(({name}) => name)
		).toEqual(['longText']);
	});

	it('Does not duplicate a repeatable group inside a group', () => {
		const structureUuid = getUuid();
		const outerUuid = getUuid();
		const tabUuid = getUuid();
		const innerUuid = getUuid();

		const buildObjectDefinition = (
			externalReferenceCode: string,
			name: string,
			objectRelationships: ObjectRelationship[] = []
		): ObjectDefinition => ({
			enableComments: true,
			enableFriendlyURLCustomization: true,
			enableIndexSearch: true,
			enableLocalization: true,
			enableObjectEntryDraft: true,
			enableObjectEntryHistory: true,
			enableObjectEntrySchedule: true,
			enableObjectEntryVersioning: true,
			externalReferenceCode,
			id: 1,
			label: {en_US: name},
			name,
			objectFields: [],
			objectFolderExternalReferenceCode:
				'L_CMS_STRUCTURE_REPEATABLE_GROUPS',
			objectRelationships,
			pluralLabel: {},
			scope: 'depot',
		});

		const innerGroup: RepeatableGroup = {
			children: new Map(),
			erc: 'inner-erc',
			isRepeatable: true,
			label: {en_US: 'Inner'},
			name: 'C_Inner',
			parent: tabUuid,
			relationshipERC: 'inner-relationship-erc',
			relationshipName: 'innerRelationship',
			type: 'group',
			uuid: innerUuid,
		};

		const tab: NonRepeatableGroup = {
			children: new Map([[innerUuid, innerGroup]]),
			isRepeatable: false,
			label: {en_US: 'Tab'},
			parent: outerUuid,
			type: 'group',
			uuid: tabUuid,
		};

		const outerGroup: RepeatableGroup = {
			children: new Map<Uuid, StructureChild>([[tabUuid, tab]]),
			erc: 'outer-erc',
			isRepeatable: true,
			label: {en_US: 'Outer'},
			name: 'C_Outer',
			parent: structureUuid,
			relationshipERC: 'outer-relationship-erc',
			relationshipName: 'outerRelationship',
			type: 'group',
			uuid: outerUuid,
		};

		const root: Structure = {
			children: new Map([[outerUuid, outerGroup]]),
			erc: 'structure-erc',
			label: {en_US: 'Structure'},
			name: 'structure-name',
			path: '',
			slug: '',
			spaces: [],
			status: 'published',
			system: false,
			titleFieldName: 'title',
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: structureUuid,
			workflows: {},
		};

		const children = refreshReferencedStructures({
			objectDefinitions: {
				'inner-erc': buildObjectDefinition('inner-erc', 'C_Inner'),
				'outer-erc': buildObjectDefinition('outer-erc', 'C_Outer', [
					{
						deletionType: 'cascade',
						edge: true,
						externalReferenceCode: 'inner-relationship-erc',
						label: {en_US: 'Inner'},
						name: 'innerRelationship',
						objectDefinitionExternalReferenceCode1: 'outer-erc',
						objectDefinitionExternalReferenceCode2: 'inner-erc',
						objectDefinitionName2: 'C_Inner',
						type: 'oneToMany',
					},
				]),
			},
			root,
			systemFieldNames: {},
		});

		const outer = children.get(outerUuid) as RepeatableGroup;

		expect(Array.from(outer.children.keys())).toEqual([tabUuid]);

		const refreshedTab = outer.children.get(tabUuid) as NonRepeatableGroup;

		expect(Array.from(refreshedTab.children.keys())).toEqual([innerUuid]);
	});
});
