/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/ObjectDefinition';
import {
	ReferencedStructure,
	Structure,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import refreshReferencedStructures from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/refreshReferencedStructures';

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
			spaces: [],
			status: 'published',
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
		});

		const updatedReferencedStructure = result.get(referencedStructure.uuid);

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
