/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {
	ObjectDefinition,
	ObjectField,
	ObjectRelationship,
} from '../../common/types/ObjectDefinition';
import {config} from '../config';
import {
	ReferencedStructure,
	RelatedContent,
	RepeatableGroup,
	Structure,
} from '../types/Structure';
import {FIELD_TYPE_TO_DB_TYPE, Field, getFieldBusinessType} from './field';
import isField from './isField';
import {isFieldTextSearchable} from './isFieldTextSearchable';

export default function buildObjectDefinition({
	children = new Map(),
	erc,
	id,
	label,
	name,
	settings,
	slug,
	spaces,
	status = 'draft',
	workflows,
}: {
	children?: Structure['children'];
	erc: Structure['erc'];
	id?: Structure['id'];
	label: Structure['label'];
	name: Structure['name'];
	settings?: Structure['settings'];
	slug?: Structure['slug'];
	spaces: Structure['spaces'];
	status?: Structure['status'];
	workflows?: Structure['workflows'];
}): ObjectDefinition {
	const objectDefinition: ObjectDefinition = {
		enableComments: true,
		enableFriendlyURLCustomization: true,
		enableIndexSearch: true,
		enableLocalization: true,
		enableObjectEntryDraft: true,
		enableObjectEntryHistory: true,
		enableObjectEntrySchedule: true,
		enableObjectEntryVersioning: true,
		externalReferenceCode: erc,
		label,
		objectFields: buildFields(getFields(children)),
		objectRelationships: buildRelationships({
			referencedStructures: getReferencedStructures(children),
			relatedContents: getRelatedContents(children),
			repeatableGroups: getRepeatableGroups(children),
			structureERC: erc,
		}),
		pluralLabel: label,
		scope: 'depot',
		status: {
			code: status === 'published' ? 0 : 2,
		},
		titleObjectFieldName: 'title',
	};

	if (slug) {
		objectDefinition.friendlyURLSeparator = slug;
	}

	if (id) {
		objectDefinition.id = id;
	}

	if (name) {
		objectDefinition.name = name;
	}

	if (config.objectFolderExternalReferenceCode) {
		objectDefinition.objectFolderExternalReferenceCode =
			config.objectFolderExternalReferenceCode;
	}

	if (spaces === 'all') {
		objectDefinition.objectDefinitionSettings = [
			{name: 'acceptAllGroups', value: 'true'},
		];
	}
	else if (spaces.length) {
		objectDefinition.objectDefinitionSettings = [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: spaces.join(','),
			},
		];
	}

	if (settings?.allowStandaloneObjectEntry !== undefined) {
		objectDefinition.objectDefinitionSettings = [
			...(objectDefinition.objectDefinitionSettings ?? []),
			{
				name: 'allowStandaloneObjectEntry',
				value: settings.allowStandaloneObjectEntry,
			},
		];
	}

	if (workflows && Object.keys(workflows).length) {
		objectDefinition.workflowDefinitionLinks = buildWorkflowDefinitionLinks(
			{spaces, workflows}
		);
	}

	return objectDefinition;
}

function getFields(children: Structure['children']): Field[] {
	return Array.from(children.values()).filter((child) =>
		isField(child)
	) as Field[];
}

function getRelatedContents(children: Structure['children']): RelatedContent[] {
	return Array.from(children.values()).filter(
		(child) => child.type === 'related-content'
	) as RelatedContent[];
}

function getReferencedStructures(
	children: Structure['children']
): ReferencedStructure[] {
	return Array.from(children.values()).filter(
		(child) => child.type === 'referenced-structure'
	) as ReferencedStructure[];
}

function getRepeatableGroups(
	children: Structure['children']
): RepeatableGroup[] {
	return Array.from(children.values()).filter(
		(child) => child.type === 'repeatable-group'
	) as RepeatableGroup[];
}

function buildFields(fields: Field[]) {
	return fields.map((field) => {
		const objectField: ObjectField = {
			DBType: FIELD_TYPE_TO_DB_TYPE[field.type],
			businessType: getFieldBusinessType(field),
			externalReferenceCode: field.erc,
			indexed: field.indexableConfig.indexed,
			label: field.label,
			localized: field.localized,
			name: field.name,
			required: field.required,
			system: field.locked,
		};

		if (field.indexableConfig.indexed) {
			objectField.indexedAsKeyword =
				field.indexableConfig.indexedAsKeyword;

			if (isFieldTextSearchable(field)) {
				objectField.indexedLanguageId =
					field.indexableConfig.indexedLanguageId ?? '';
			}
		}

		if ('settings' in field) {
			objectField.objectFieldSettings = Object.entries(field.settings)
				.filter(([_, value]) => !isNullOrUndefined(value))
				.map(([name, value]) => ({name, value}));
		}

		if ('picklistId' in field) {
			objectField.listTypeDefinitionId = field.picklistId;
		}

		return objectField;
	});
}

function buildRelationships({
	referencedStructures,
	relatedContents,
	repeatableGroups,
	structureERC,
}: {
	referencedStructures: ReferencedStructure[];
	relatedContents: RelatedContent[];
	repeatableGroups: RepeatableGroup[];
	structureERC: Structure['erc'];
}) {
	const relationships: ObjectRelationship[] = [];

	for (const referencedStructure of referencedStructures) {
		relationships.push({
			deletionType: 'cascade',
			edge: true,
			externalReferenceCode: referencedStructure.relationshipERC,
			label: {
				en_US: referencedStructure.name,
			},
			name: referencedStructure.relationshipName,
			objectDefinitionExternalReferenceCode1: structureERC,
			objectDefinitionExternalReferenceCode2: referencedStructure.erc,
			type: 'oneToMany',
		});
	}

	for (const repeatableGroup of repeatableGroups) {
		relationships.push({
			deletionType: 'cascade',
			edge: true,
			externalReferenceCode: repeatableGroup.relationshipERC,
			label: repeatableGroup.label,
			name: repeatableGroup.relationshipName,
			objectDefinitionExternalReferenceCode1: structureERC,
			objectDefinitionExternalReferenceCode2: repeatableGroup.erc,
			type: 'oneToMany',
		});
	}

	for (const relatedContent of relatedContents) {
		if (relatedContent.multiselection) {
			relationships.push({
				deletionType: 'disassociate',
				externalReferenceCode: relatedContent.erc,
				label: relatedContent.label,
				name: relatedContent.name,
				objectDefinitionExternalReferenceCode1: structureERC,
				objectDefinitionExternalReferenceCode2:
					relatedContent.relatedStructureERC!,
				type: 'manyToMany',
			});
		}
	}

	return relationships;
}

function buildWorkflowDefinitionLinks({
	spaces,
	workflows,
}: {
	spaces: Structure['spaces'];
	workflows: Structure['workflows'];
}) {
	const definitionLinks: ObjectDefinition['workflowDefinitionLinks'] = [];

	for (const [
		groupExternalReferenceCode,
		workflowDefinitionName,
	] of Object.entries(workflows)) {

		// Don't insert workflow if structure does not include the space

		if (
			spaces !== 'all' &&
			groupExternalReferenceCode &&
			!spaces.includes(groupExternalReferenceCode)
		) {
			continue;
		}

		// Don't insert if there's no workflow name, what means the Default one was selected

		if (!workflowDefinitionName) {
			continue;
		}

		// Insert the workflow link

		definitionLinks.push({
			groupExternalReferenceCode,
			workflowDefinitionName,
		});
	}

	return definitionLinks;
}
