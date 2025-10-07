/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {config} from '../config';
import {
	ObjectDefinition,
	ObjectField,
	ObjectRelationship,
} from '../types/ObjectDefinition';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
} from '../types/Structure';
import {
	FIELD_TYPE_TO_BUSINESS_TYPE,
	FIELD_TYPE_TO_DB_TYPE,
	Field,
} from './field';
import {isFieldTextSearchable} from './isFieldTextSearchable';

export default function buildObjectDefinition({
	children = new Map(),
	erc,
	label,
	name,
	spaces,
	status = 'draft',
	workflows,
}: {
	children?: Structure['children'];
	erc: Structure['erc'];
	label: Structure['label'];
	name: Structure['name'];
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

	if (workflows && Object.keys(workflows).length) {
		objectDefinition.workflowDefinitionLinks = buildWorkflowDefinitionLinks(
			{spaces, workflows}
		);
	}

	return objectDefinition;
}

function getFields(children: Structure['children']): Field[] {
	return Array.from(children.values()).filter(
		(child) =>
			child.type !== 'referenced-structure' &&
			child.type !== 'repeatable-group'
	) as Field[];
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
			businessType: FIELD_TYPE_TO_BUSINESS_TYPE[field.type],
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
	repeatableGroups,
	structureERC,
}: {
	referencedStructures: ReferencedStructure[];
	repeatableGroups: RepeatableGroup[];
	structureERC: Structure['erc'];
}) {
	const relationships: ObjectRelationship[] = [];

	for (const referencedStructure of referencedStructures) {
		relationships.push({
			deletionType: 'cascade',
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
			label: repeatableGroup.label,
			name: repeatableGroup.relationshipName,
			objectDefinitionExternalReferenceCode1: structureERC,
			objectDefinitionExternalReferenceCode2: repeatableGroup.erc,
			type: 'oneToMany',
		});
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
