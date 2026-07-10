/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectField,
	ObjectRelationship,
} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import buildObjectDefinition from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectDefinition';
import buildObjectRelationships from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectRelationships';
import buildStructure from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildStructure';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

const parent = getUuid();

const SAMPLE_STRUCTURE_FIELDS: Field[] = [
	{
		erc: 'attachment-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'attachment-field'},
		localized: false,
		locked: false,
		name: 'attachment-field',
		parent,
		required: false,
		settings: {
			acceptedFileExtensions: 'jpg,png',
			fileSource: 'documentsAndMedia',
			maximumFileSize: 100,
		},
		type: 'upload',
		uuid: getUuid(),
	},
	{
		erc: 'boolean-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'boolean-field'},
		localized: false,
		locked: false,
		name: 'boolean-field',
		parent,
		required: false,
		settings: {},
		type: 'boolean',
		uuid: getUuid(),
	},
	{
		erc: 'date-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'date-field'},
		localized: false,
		locked: false,
		name: 'date-field',
		parent,
		required: false,
		settings: {},
		type: 'date',
		uuid: getUuid(),
	},
	{
		erc: 'datetime-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'datetime-field'},
		localized: false,
		locked: false,
		name: 'datetime-field',
		parent,
		required: false,
		settings: {timeStorage: 'convertToUTC'},
		type: 'datetime',
		uuid: getUuid(),
	},
	{
		erc: 'decimal-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'decimal-field'},
		localized: false,
		locked: false,
		name: 'decimal-field',
		parent,
		required: false,
		settings: {},
		type: 'decimal',
		uuid: getUuid(),
	},
	{
		erc: 'email-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'email-field'},
		localized: false,
		locked: false,
		name: 'email-field',
		parent,
		required: false,
		settings: {
			autocompleteDomains: '@liferay.com',
			autocompleteEnabled: true,
			blockedDomains: '@example.com',
			uniqueValues: true,
		},
		type: 'email',
		uuid: getUuid(),
	},
	{
		erc: 'integer-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'integer-field'},
		localized: false,
		locked: false,
		name: 'integer-field',
		parent,
		required: false,
		settings: {},
		type: 'integer',
		uuid: getUuid(),
	},
	{
		erc: 'long-text-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'long-text-field'},
		localized: false,
		locked: false,
		name: 'long-text-field',
		parent,
		required: false,
		settings: {},
		type: 'long-text',
		uuid: getUuid(),
	},
	{
		erc: 'multi-picklist-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'multi-picklist-field'},
		localized: false,
		locked: false,
		multiselection: true,
		name: 'multi-picklist-field',
		parent,
		picklistId: 1,
		required: false,
		settings: {},
		type: 'select-from-list',
		uuid: getUuid(),
	},
	{
		erc: 'picklist-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'picklist-field'},
		localized: false,
		locked: false,
		multiselection: false,
		name: 'picklist-field',
		parent,
		picklistId: 2,
		required: false,
		settings: {},
		type: 'select-from-list',
		uuid: getUuid(),
	},
	{
		erc: 'rich-text-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'rich-text-field'},
		localized: false,
		locked: false,
		name: 'rich-text-field',
		parent,
		required: false,
		settings: {},
		type: 'rich-text',
		uuid: getUuid(),
	},
	{
		erc: 'text-field',
		indexableConfig: {indexed: false},
		label: {en_US: 'text-field'},
		localized: false,
		locked: false,
		name: 'text-field',
		parent,
		required: false,
		settings: {},
		type: 'text',
		uuid: getUuid(),
	},
];

function getChildren(fields: Field[]) {
	const children = new Map();

	for (const field of fields) {
		children.set(field.uuid, field);
	}

	return children;
}

function createObjectField(overrides: Partial<ObjectField> = {}): ObjectField {
	return {
		DBType: 'String',
		businessType: 'Text',
		externalReferenceCode: 'TEST',
		indexed: false,
		label: {en_US: 'Test'},
		localized: false,
		name: 'test',
		required: false,
		system: false,
		...overrides,
	} as ObjectField;
}

function createObjectDefinition(
	overrides: Partial<ObjectDefinition> = {}
): ObjectDefinition {
	return {
		enableComments: true,
		enableFriendlyURLCustomization: true,
		enableIndexSearch: true,
		enableLocalization: true,
		enableObjectEntryDraft: true,
		enableObjectEntryHistory: true,
		enableObjectEntrySchedule: true,
		enableObjectEntryVersioning: true,
		externalReferenceCode: 'TEST_ERC',
		label: {en_US: 'Test'},
		pluralLabel: {en_US: 'Tests'},
		scope: 'depot',
		status: {code: 0},
		...overrides,
	};
}

function getChildFieldNames(structure: ReturnType<typeof buildStructure>) {
	return Array.from(structure.children.values()).map((child) => child.name);
}

describe('buildStructure', () => {
	it('Maps object field business types to structure field types', () => {
		const objectDefinition = buildObjectDefinition({
			children: getChildren(SAMPLE_STRUCTURE_FIELDS),
			erc: 'main-structure-erc',
			label: {en_US: 'Main Structure'},
			name: 'mainStructure',
			spaces: [],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {},
		});

		const childrenMap = new Map(
			Array.from(structure.children.values()).map((child) => [
				child.erc,
				child,
			])
		);

		for (const {erc, type} of SAMPLE_STRUCTURE_FIELDS) {
			expect(childrenMap.get(erc)).toEqual(
				expect.objectContaining({type})
			);
		}
	});

	it('Restores email field settings from the EmailAddress business type', () => {
		const objectDefinition = buildObjectDefinition({
			children: getChildren(SAMPLE_STRUCTURE_FIELDS),
			erc: 'main-structure-erc',
			label: {en_US: 'Main Structure'},
			name: 'mainStructure',
			spaces: [],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {},
		});

		const emailField = Array.from(structure.children.values()).find(
			(child) => child.erc === 'email-field'
		);

		expect(emailField).toEqual(
			expect.objectContaining({
				settings: {
					autocompleteDomains: '@liferay.com',
					autocompleteEnabled: true,
					blockedDomains: '@example.com',
					uniqueValues: true,
				},
				type: 'email',
			})
		);
	});

	it('Includes allowed system fields for L_CMS_BLOG and filters unknown ones', () => {
		const objectDefinition = createObjectDefinition({
			externalReferenceCode: 'L_CMS_BLOG',
			objectFields: [
				createObjectField({
					externalReferenceCode: 'TITLE',
					name: 'title',
					system: true,
				}),
				createObjectField({
					businessType: 'RichText',
					externalReferenceCode: 'CONTENT',
					name: 'content',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'SUBTITLE',
					name: 'subtitle',
					system: true,
				}),
				createObjectField({
					businessType: 'Attachment',
					externalReferenceCode: 'COVER_IMAGE',
					name: 'coverImage',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'UNKNOWN_SYSTEM',
					name: 'unknownSystemField',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'CUSTOM',
					name: 'customField',
					system: false,
				}),
			],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {},
		});

		const fieldNames = getChildFieldNames(structure);

		expect(fieldNames).toContain('title');
		expect(fieldNames).toContain('content');
		expect(fieldNames).toContain('subtitle');
		expect(fieldNames).toContain('coverImage');
		expect(fieldNames).toContain('customField');
		expect(fieldNames).not.toContain('unknownSystemField');
	});

	it('Includes allowed system fields for L_CMS_EXTERNAL_VIDEO and filters unknown ones', () => {
		const objectDefinition = createObjectDefinition({
			externalReferenceCode: 'L_CMS_EXTERNAL_VIDEO',
			objectFields: [
				createObjectField({
					externalReferenceCode: 'TITLE',
					name: 'title',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'VIDEO_URL',
					name: 'videoURL',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'CONTENT',
					name: 'content',
					system: true,
				}),
			],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {},
		});

		const fieldNames = getChildFieldNames(structure);

		expect(fieldNames).toContain('title');
		expect(fieldNames).toContain('videoURL');
		expect(fieldNames).not.toContain('content');
	});

	it('Restores a related content field referencing the same structure', () => {
		const objectDefinition = createObjectDefinition({
			externalReferenceCode: 'SELF_ERC',
			objectRelationships: [
				{
					deletionType: 'disassociate',
					externalReferenceCode: 'self-related-content',
					label: {en_US: 'Self Related Content'},
					name: 'selfRelatedContent',
					objectDefinitionExternalReferenceCode1: 'SELF_ERC',
					objectDefinitionExternalReferenceCode2: 'SELF_ERC',
					type: 'oneToMany',
				},
			],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {SELF_ERC: objectDefinition},
		});

		const relatedContents = Array.from(structure.children.values()).filter(
			(child) => child.type === 'related-content'
		);

		expect(relatedContents).toEqual([
			expect.objectContaining({
				erc: 'self-related-content',
				multiselection: false,
				name: 'selfRelatedContent',
				relatedStructureERC: 'SELF_ERC',
			}),
		]);
	});

	it('Round-trips a related content field referencing the same structure', () => {
		const objectRelationship: ObjectRelationship = {
			deletionType: 'disassociate',
			externalReferenceCode: 'self-related-content',
			label: {en_US: 'Self Related Content'},
			name: 'selfRelatedContent',
			objectDefinitionExternalReferenceCode1: 'SELF_ERC',
			objectDefinitionExternalReferenceCode2: 'SELF_ERC',
			type: 'oneToMany',
		};

		const objectDefinition = createObjectDefinition({
			externalReferenceCode: 'SELF_ERC',
			objectRelationships: [objectRelationship],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {SELF_ERC: objectDefinition},
		});

		expect(
			buildObjectRelationships({
				children: structure.children,
				structureERC: structure.erc,
			})
		).toEqual([objectRelationship]);
	});

	it('Builds a self-referencing repeatable group definition only as a repeatable group', () => {
		const objectDefinition = createObjectDefinition({
			externalReferenceCode: 'SELF_GROUP_ERC',
			objectFolderExternalReferenceCode:
				'L_CMS_STRUCTURE_REPEATABLE_GROUPS',
			objectRelationships: [
				{
					deletionType: 'disassociate',
					externalReferenceCode: 'self-group',
					label: {en_US: 'Self Group'},
					name: 'selfGroup',
					objectDefinitionExternalReferenceCode1: 'SELF_GROUP_ERC',
					objectDefinitionExternalReferenceCode2: 'SELF_GROUP_ERC',
					type: 'oneToMany',
				},
			],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {SELF_GROUP_ERC: objectDefinition},
		});

		const children = Array.from(structure.children.values());

		expect(
			children.filter((child) => child.type === 'related-content')
		).toEqual([]);

		expect(
			children.filter((child) => child.type === 'repeatable-group')
		).toEqual([
			expect.objectContaining({
				erc: 'SELF_GROUP_ERC',
				relationshipERC: 'self-group',
			}),
		]);
	});

	it('Includes only title and file system fields for custom object definitions', () => {
		const objectDefinition = createObjectDefinition({
			externalReferenceCode: 'CUSTOM_OBJECT_ERC',
			objectFields: [
				createObjectField({
					externalReferenceCode: 'TITLE',
					name: 'title',
					system: true,
				}),
				createObjectField({
					businessType: 'Attachment',
					externalReferenceCode: 'FILE',
					name: 'file',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'CONTENT',
					name: 'content',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'VIDEO_URL',
					name: 'videoURL',
					system: true,
				}),
				createObjectField({
					externalReferenceCode: 'CUSTOM',
					name: 'customField',
					system: false,
				}),
			],
		});

		const structure = buildStructure({
			mainObjectDefinition: objectDefinition,
			objectDefinitions: {},
		});

		const fieldNames = getChildFieldNames(structure);

		expect(fieldNames).toContain('title');
		expect(fieldNames).toContain('file');
		expect(fieldNames).toContain('customField');
		expect(fieldNames).not.toContain('content');
		expect(fieldNames).not.toContain('videoURL');
	});
});
